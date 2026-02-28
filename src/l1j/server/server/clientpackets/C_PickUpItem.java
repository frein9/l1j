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
package l1j.server.server.clientpackets;

import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.util.Random;
import java.util.logging.Logger;

public class C_PickUpItem extends ClientBasePacket {

    private static final String C_PICK_UP_ITEM = "[C] C_PickUpItem";
    private static Logger _log = Logger.getLogger(C_PickUpItem.class.getName());
    private static final Random _random = new Random();

    public C_PickUpItem(byte decrypt[], ClientThread client) throws Exception {
        super(decrypt);
        int x = readH();
        int y = readH();
        int objectId = readD();
        int pickupCount = readD();

        //** 엔징 방어 **//	By 도우너
        long pickupCheck = 0;
        pickupCheck = pickupCheck + pickupCount;
        //** 엔징 방어 **//	By 도우너

        L1PcInstance pc = client.getActiveChar();
        if (pc.isDead() || pc.isGhost()) {
            return;
        }
        if (pc.getOnlineStatus() != 1) {
            pc.sendPackets(new S_Disconnect());
            return;
        }

        // 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
        //if(isTwoLogin(pc)) return;

        if (pc.isInvisble()) { // 인비지 상태
            return;
        }
        if (pc.isInvisDelay()) { // 인비지디레이 상태
            return;
        }

        L1Inventory groundInventory = L1World.getInstance().getInventory(x, y, pc.getMapId());
        L1Object object = groundInventory.getItem(objectId);

        if (object != null && !pc.isDead()) {
            L1ItemInstance item = (L1ItemInstance) object;
            if (item.getItemOwnerId() != 0 && pc.getId() != item.getItemOwnerId()) { // 아이템 소유 권한
                pc.sendPackets(new S_ServerMessage(623));
                return;
            }

            if (pc.getLocation().getTileLineDistance(item.getLocation()) > 3) {
                return;
            }

            //** 아이템 픽업방지 **// by 도우너
            if (objectId != item.getId()) {  //바꺼치기 부분
                return;
            }
            int itemType = item.getItem().getType2();
            if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
                return;
            }
            if (item.getLockitem() > 100) {
                item.setLockitem(0);
            }

            if (pickupCount <= 0 || item.getCount() < 0) {
                return;
            }

            if (item.getItem().getItemId() == L1ItemId.ADENA) {
                //L1ItemInstance inventoryItem = pc.getInventory().findItemId(
                L1ItemInstance l1iteminstance = pc.getInventory().findItemId(
                        L1ItemId.ADENA);
			/*	int inventoryItemCount = 0;
				if (inventoryItem != null) {
					inventoryItemCount = inventoryItem.getCount();
				}
				// 주운 후에 2 G를 초과하지 않게 체크
				if ((long) inventoryItemCount + (long) pickupCount > 2000000000L) {*/
                if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
                    pc.sendPackets(new S_ServerMessage(166, // \f1%0이%4%1%3%2
                            "소지하고 있는 아데나", "2,000,000,000을 초과하므로 주울 수 없습니다."));
                    return;
                }
                if (pc.hasSkillEffect(L1SkillId.STATUS_XNAKD)) {    //추가
                    return;
                }
                if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {    //추가
                    pc.sendPackets(new S_SystemMessage("앱솔루트 배리어 상태에서는 아이템을 주울 수 없습니다."));
                    return;
                }

            }

            if (pc.getInventory().checkAddItem( // 용량 중량 확인 및 메세지 송신
                    //item , pickupCount) == L1Inventory.OK) {
                    item, item.getCount()) == L1Inventory.OK) {

                if (item.getX() != 0 && item.getY() != 0) { // 월드 맵 위의 아이템
                    // 아이템줍기 시작 - ACE
                    if (pc.isInParty() && !item.get_pcDrop()) { // 파티상태이고 사람이 버린게 아닌 아이템
                        L1PcInstance partyMember[] = pc.getParty().getMembers();
                        L1PcInstance itemMember[] = new L1PcInstance[partyMember.length];
                        int cnt = 0;
                        int i = 0;
                        for (i = 0; i < partyMember.length; i++) {
                            if (partyMember[i].getLocation().getTileLineDistance(item.getLocation()) <= 16) {
                                itemMember[cnt] = partyMember[i];
                                cnt++; // 아이템을 가질수 있는 멤버 수
                            }
                        }
                        if (item.getItemId() == L1ItemId.ADENA) { // 아데나일 경우
                            int adenadiv = item.getCount() / cnt;
                            int adenaremainder = item.getCount() % cnt;
                            for (i = 0; i < cnt; i++) {
                                groundInventory.tradeItem(item, adenadiv, itemMember[i].getInventory());
                            }
                            if (adenaremainder > 0) { // 나누고 남은 아데나가 있으면 주운 사람이 갖자
                                groundInventory.tradeItem(item, adenaremainder, pc.getInventory());
                            }
                        } else { // 아데나가 아닌 아이템
                            if (pc.getAutoDivision()) { // 분배파티일 경우
                                int Who = _random.nextInt(itemMember.length);
                                groundInventory.tradeItem(item, item.getCount(), itemMember[Who].getInventory());
                                for (i = 0; i < partyMember.length; i++) {
                                    partyMember[i].sendPackets(new S_SystemMessage(itemMember[Who].getName() + " 님께서 " + item.getName() + "를 (" + item.getCount() + ")개 획득하였습니다."));
                                }
                            } else { // 일반파티일 경우
                                groundInventory.tradeItem(item, item.getCount(), pc.getInventory());
                            }
                        }
                    } else { // 파티가 아니거나 사람이 버린 아이템
                        groundInventory.tradeItem(item, item.getCount(), pc.getInventory());
                    }
                    pc.turnOnOffLight();
                    pc.sendPackets(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
                    if (!pc.isGmInvis()) {
                        pc.broadcastPacket(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
                    }
                    // 아이템줍기 끝 - ACE
                }
            }
        }
    }
/*	  if (pc.isInParty()) { // 파티의 경우
	       L1PcInstance partyMember[] = pc.getParty().getMembers(); //파티멤버를 배열에삽입
	       int Who = _random.nextInt(partyMember.length); //랜덤으로 파티원중하나선택
	       L1PcInstance pc1 = partyMember[Who]; //pc1으로 집어넣음
	  if (pc1.getLocation().getTileLineDistance(item.getLocation()) > 16 || item.get_pcDrop() || !pc.getAutoDivision()) {  //pc1이 아이템과 멀리떨어진 경우, 사람이 버린 경우, 분배파티가 아닌 경우
		   pc1 = pc; // 토글한 사람을 pc1으로, 즉 토글한 사람이 먹도록 함
	       }  
	      groundInventory.tradeItem(item, item.getCount(), pc1.getInventory()); //바닥의아이템을 pc1에게 삽입
	  if (!item.get_pcDrop()) {  
	       String 이름 = pc1.getName();
	       String 아이템이름 = item.getName();
	       int 아이템갯수 = item.getCount();
	       for(int i=0;i<partyMember.length;i++){
	       partyMember[i].sendPackets(new S_SystemMessage(""+이름+" 님께서 "+아이템이름+"를 ("+아이템갯수+")개 획득하였습니다."));
	       }
	       }
	       pc.turnOnOffLight();
	       pc.sendPackets(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
	  if (!pc.isGmInvis()) {
		   pc.broadcastPacket(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
		   }
	  }else{ //파티가아닐시
	       groundInventory.tradeItem(item, item.getCount(), pc.getInventory());
	       pc.turnOnOffLight();
	       pc.sendPackets(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
	  if (!pc.isGmInvis()) {
		   pc.broadcastPacket(new S_AttackPacket(pc, objectId, ActionCodes.ACTION_Pickup));
	      }
	      }
	      }
		  }
		  }
		  }*/

    /**
     * 월드상에 있는 모든 캐릭의 계정을 비교해 같은 계정이 있다면 true 없다면 false
     *
     * @param c L1PcInstance
     * @return 있다면 true
     */
    private boolean isTwoLogin(L1PcInstance c) {
        boolean bool = false;
        for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
            if (c.getId() != target.getId()) {
                if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public String getType() {
        return C_PICK_UP_ITEM;
    }
}