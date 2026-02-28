/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;

import java.util.ArrayList;
import java.util.logging.Logger;

public class L1DeleteGroundItem implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1DeleteGroundItem.class
            .getName());

    private L1DeleteGroundItem() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1DeleteGroundItem();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        for (L1Object l1object : L1World.getInstance().getObject()) {
            if (l1object instanceof L1ItemInstance) {
                L1ItemInstance l1iteminstance = (L1ItemInstance) l1object;
                if (l1iteminstance.getX() == 0 && l1iteminstance.getY() == 0) { // 지면상의 아이템은 아니고, 누군가의 소유물
                    continue;
                }

                ArrayList<L1PcInstance> players = L1World.getInstance()
                        .getVisiblePlayer(l1iteminstance, 0);
                if (0 == players.size()) {
                    L1Inventory groundInventory = L1World.getInstance()
                            .getInventory(l1iteminstance.getX(),
                                    l1iteminstance.getY(),
                                    l1iteminstance.getMapId());
                    int itemId = l1iteminstance.getItem().getItemId();
                    if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
                        PetTable.getInstance()
                                .deletePet(l1iteminstance.getId());
                    } else if (itemId >= 49016 && itemId <= 49025) { // 편지지
                        LetterTable lettertable = new LetterTable();
                        lettertable.deleteLetter(l1iteminstance.getId());
                    } else if (itemId >= 41383 && itemId <= 41400) { // 가구
                        if (l1object instanceof L1FurnitureInstance) {
                            L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
                            if (furniture.getItemObjId() == l1iteminstance
                                    .getId()) { // 이미 꺼내고 있는 가구
                                FurnitureSpawnTable.getInstance()
                                        .deleteFurniture(furniture);
                            }
                        }
                    }
                    groundInventory.deleteItem(l1iteminstance);
                    L1World.getInstance().removeVisibleObject(l1iteminstance);
                    L1World.getInstance().removeObject(l1iteminstance);
                }
            }
        }
        L1World.getInstance().broadcastServerMessage(
                "월드맵상의 아이템이 운영자에 의해 삭제되었습니다.");
    }
}
