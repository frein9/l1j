/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.datatables.AutoLoot;

// Referenced classes of package l1j.server.server.templates:
// L1Npc, L1Item, ItemTable

public class DropTable {

    private static Logger _log = Logger.getLogger(DropTable.class.getName());

    private static DropTable _instance;

    private final HashMap<Integer, ArrayList<L1Drop>> _droplists; // monster 마다의 드롭 리스트

    public static DropTable getInstance() {
        if (_instance == null) {
            _instance = new DropTable();
        }
        return _instance;
    }

    public static void reload() {
        DropTable oldInstance = _instance;
        _instance = new DropTable();
        oldInstance._droplists.clear();
    }

    private DropTable() {
        _droplists = allDropList();
    }

    private HashMap<Integer, ArrayList<L1Drop>> allDropList() {
        HashMap<Integer, ArrayList<L1Drop>> droplistMap = new HashMap<Integer, ArrayList<L1Drop>>();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM DROPLIST");
            rs = pstm.executeQuery();
            while (rs.next()) {
                int mobId = rs.getInt("mobId");
                int itemId = rs.getInt("itemId");
                int min = rs.getInt("min");
                int max = rs.getInt("max");
                int chance = rs.getInt("chance");

                L1Drop drop = new L1Drop(mobId, itemId, min, max, chance);

                ArrayList<L1Drop> dropList = droplistMap.get(drop.getMobid());
                if (dropList == null) {
                    dropList = new ArrayList<L1Drop>();
                    droplistMap.put(new Integer(drop.getMobid()), dropList);
                }
                dropList.add(drop);
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        return droplistMap;
    }

    // 목록에 드롭을 설정
    public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
        // 드롭 리스트의 취득
        int mobId = npc.getNpcTemplate().get_npcId();
        ArrayList<L1Drop> dropList = _droplists.get(mobId);
        if (dropList == null) {
            return;
        }

        // 레이트 취득
        double droprate = Config.RATE_DROP_ITEMS;
        if (droprate <= 0) {
            droprate = 0;
        }
        double adenarate = Config.RATE_DROP_ADENA;
        if (adenarate <= 0) {
            adenarate = 0;
        }
        if (droprate <= 0 && adenarate <= 0) {
            return;
        }

        int itemId;
        int itemCount;
        int addCount;
        int randomChance;
        L1ItemInstance item;
        Random random = new Random();

        for (L1Drop drop : dropList) {
            // 드롭 아이템의 취득
            itemId = drop.getItemid();
            if (adenarate == 0 && itemId == L1ItemId.ADENA) {
                continue; // 아데나레이트 0으로 드롭이 아데나의 경우는 스르
            }

            // 드롭 찬스 판정
            randomChance = random.nextInt(0xf4240) + 1;
            double rateOfMapId = MapsTable.getInstance().getDropRate(
                    npc.getMapId());
            double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
            if (droprate == 0
                    || drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance) {
                continue;
            }

            // 드롭 개수를 설정
            double amount = DropItemTable.getInstance().getDropAmount(itemId);
            int min = (int) (drop.getMin() * amount);
            int max = (int) (drop.getMax() * amount);

            itemCount = min;
            addCount = max - min + 1;
            if (addCount > 1) {
                itemCount += random.nextInt(addCount);
            }
            if (itemId == L1ItemId.ADENA) { // 드롭이 아데나의 경우는 아데나레이트를 건다
                itemCount *= adenarate;
            }
            if (itemCount < 0) {
                itemCount = 0;
            }
            if (itemCount > 2000000000) {
                itemCount = 2000000000;
            }

            // 아이템의 생성
            item = ItemTable.getInstance().createItem(itemId);
            item.setCount(itemCount);

            // 아이템 격납
            inventory.storeItem(item);
        }
    }

    // 드롭을 분배
    public void dropShare(L1NpcInstance npc, ArrayList acquisitorList,
                          ArrayList hateList) {
        L1Inventory inventory = npc.getInventory();
        if (inventory.getSize() == 0) {
            return;
        }
        if (acquisitorList.size() != hateList.size()) {
            return;
        }
        // 헤이트의 합계를 취득
        int totalHate = 0;
        L1Character acquisitor;
        for (int i = hateList.size() - 1; i >= 0; i--) {
            acquisitor = (L1Character) acquisitorList.get(i);
            if ((Config.AUTO_LOOT == 2)  // 오토 루팅 2의 경우는 사몬 및 애완동물은 생략한다
                    && (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
                acquisitorList.remove(i);
                hateList.remove(i);
            } else if (acquisitor != null
                    && acquisitor.getMapId() == npc.getMapId()
                    && acquisitor.getLocation().getTileLineDistance(
                    npc.getLocation()) <= Config.LOOTING_RANGE) {
                totalHate += (Integer) hateList.get(i);
            } else { // null였거나 죽기도 하고 멀었으면 배제
                acquisitorList.remove(i);
                hateList.remove(i);
            }
        }

        // 드롭의 분배
        L1ItemInstance item;
        L1Inventory targetInventory = null;
        L1PcInstance player;
        L1PcInstance[] partyMember;
        Random random = new Random();
        int randomInt;
        int chanceHate;
        int itemId;
        for (int i = inventory.getSize(); i > 0; i--) {
            item = inventory.getItems().get(0);
            itemId = item.getItemId();
            boolean isGround = false;
            if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light계 아이템
                item.setNowLighting(false);
            }

            if (((Config.AUTO_LOOT != 0) || AutoLoot.getInstance().isAutoLoot(itemId))
				 /* || itemId == L1ItemId.ADENA
				    || itemId == 55554 || itemId == 55555 || itemId == 22007      //티칼 아이템,테베 아이템
					|| itemId == 22008 || itemId == 22009 
					|| itemId == 40074 || itemId == 40087 || itemId == 240087    //무기,갑옷 주문서
					|| itemId == 140074 || itemId == 140087 || itemId == 240074 
				    || itemId == 40044 || itemId == 40045 || itemId == 40046     //보석류
				    || itemId == 40047 || itemId == 40048 || itemId == 40049 
				    || itemId == 40050 || itemId == 40051 || itemId == 40052 
				    || itemId == 40053 || itemId == 40054 || itemId == 40055 
				    || itemId == 40093 || itemId == 40094                        //4,5단계 빈 주문서
				    || itemId == 40076 || itemId == 40466                        // 고대의 주문서, 용의심장
				    || itemId == 40033 || itemId == 40035 || itemId == 40036     // 엘릭서
				    || itemId == 40037 || itemId == 40038 
				    || itemId == 40397 || itemId == 41159                        //키메라 가죽[용], 깃털
                    || itemId == 51254 || itemId == 51255 || itemId == 51256     //코마조각 1~5, 축순
                    || itemId == 51258 || itemId == 51257 || itemId == 140100 
                    || itemId == 555566 || itemId == 555567 || itemId == 555568  //난쟁이 부락 드랍 아이템
                    || itemId == 555569 )*/
                    && totalHate > 0) { // 오토 루팅이나 아데나로 취득자가 있는 경우
                randomInt = random.nextInt(totalHate);
                chanceHate = 0;
                for (int j = hateList.size() - 1; j >= 0; j--) {
                    chanceHate += (Integer) hateList.get(j);
                    if (chanceHate > randomInt) {
                        acquisitor = (L1Character) acquisitorList.get(j);
                        if (itemId >= 40131 && itemId <= 40135) {
                            if (!(acquisitor instanceof L1PcInstance)
                                    || hateList.size() > 1) {
                                targetInventory = null;
                                break;
                            }
                            player = (L1PcInstance) acquisitor;
                            if (player.getQuest().get_step(L1Quest
                                    .QUEST_LYRA) != 1) {
                                targetInventory = null;
                                break;
                            }
                        }
                        if (acquisitor.getInventory().checkAddItem(item,
                                item.getCount()) == L1Inventory.OK) {
                            targetInventory = acquisitor.getInventory();
                            if (acquisitor instanceof L1PcInstance) {
                                player = (L1PcInstance) acquisitor;
                                L1ItemInstance l1iteminstance = player
                                        .getInventory().findItemId(
                                                L1ItemId.ADENA); // 소지 아데나를 체크
                                if (l1iteminstance != null
                                        && l1iteminstance.getCount() > 2000000000) {
                                    targetInventory = L1World.getInstance()
                                            .getInventory(acquisitor.getX(),
                                                    acquisitor.getY(),
                                                    acquisitor.getMapId()); // 가질 수  없기 때문에 발밑에 떨어뜨린다
                                    isGround = true;
                                    player.sendPackets(new S_ServerMessage(166,
                                            "소지하고 있는 아데나",
                                            "2,000,000,000을 초과하고 있습니다.")); // \f1%0이%4%1%3%2
                                } else {
                                    if (player.isInParty()) { // 파티의 경우
                                        partyMember = player.getParty().getMembers();
                                        for (int p = 0; p < partyMember.length; p++) {
                                            partyMember[p]
                                                    .sendPackets(new S_ServerMessage(
                                                            813, npc.getName(),
                                                            item.getLogName(),
                                                            player.getName()));
                                        }
                                    } else {
                                        // 솔로의 경우
                                        player.sendPackets(new S_ServerMessage(
                                                143, npc.getName(), item
                                                .getLogName())); // \f1%0이%1를 주었습니다.
                                    }
                                }
                            }
                        } else {
                            targetInventory = L1World.getInstance()
                                    .getInventory(acquisitor.getX(),
                                            acquisitor.getY(),
                                            acquisitor.getMapId()); // 가질 수  없기 때문에 발밑에 떨어뜨린다
                            isGround = true;
                        }
                        break;
                    }
                }
            } else { // 논오트르팅
                List<Integer> dirList = new ArrayList<Integer>();
                for (int j = 0; j < 8; j++) {
                    dirList.add(j);
                }
                int x = 0;
                int y = 0;
                int dir = 0;
                do {
                    if (dirList.size() == 0) {
                        x = 0;
                        y = 0;
                        break;
                    }
                    randomInt = random.nextInt(dirList.size());
                    dir = dirList.get(randomInt);
                    dirList.remove(randomInt);
                    switch (dir) {
                        case 0:
                            x = 0;
                            y = -1;
                            break;
                        case 1:
                            x = 1;
                            y = -1;
                            break;
                        case 2:
                            x = 1;
                            y = 0;
                            break;
                        case 3:
                            x = 1;
                            y = 1;
                            break;
                        case 4:
                            x = 0;
                            y = 1;
                            break;
                        case 5:
                            x = -1;
                            y = 1;
                            break;
                        case 6:
                            x = -1;
                            y = 0;
                            break;
                        case 7:
                            x = -1;
                            y = -1;
                            break;
                    }
                } while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
                targetInventory = L1World.getInstance().getInventory(
                        npc.getX() + x, npc.getY() + y, npc.getMapId());
                isGround = true;
            }
            if (itemId >= 40131 && itemId <= 40135) {
                if (isGround || targetInventory == null) {
                    inventory.removeItem(item, item.getCount());
                    continue;
                }
            }
            inventory.tradeItem(item, item.getCount(), targetInventory);
        }
        npc.turnOnOffLight();
    }

}