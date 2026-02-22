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

import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1PetType;
import l1j.server.server.serverpackets.S_SystemMessage; 
public class C_GiveItem extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_GiveItem.class.getName());
	private static final String C_GIVE_ITEM = "[C] C_GiveItem";

	private static Random _random = new Random();

	public C_GiveItem(byte decrypt[], ClientThread client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		int itemId = readD();
		int count = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}

		if (pc.isGhost()) {
			return;
		}
		// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
		if(isTwoLogin(pc)) return;

		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
           return;
        }
       

		L1Object object = L1World.getInstance().findObject(targetId);
		if (object == null || !(object instanceof L1NpcInstance)) {
			return;
		}
		L1NpcInstance target = (L1NpcInstance) object;
		if (!isNpcItemReceivable(target.getNpcTemplate())) {
			return;
		}
		L1Inventory targetInv = target.getInventory();

		L1Inventory inv = pc.getInventory();
		L1ItemInstance item = inv.getItem(itemId);
		if (item == null) {
			return;
		}
		if (item.getLockitem() > 100){
         pc.sendPackets(new S_SystemMessage("봉인된 아이템은 건네줄 수 없습니다."));
         return;
       }

		/*버그방지*/
		if (itemId != item.getId()) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (!item.isStackable() && count != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		
		if (item.isEquipped()) {
			pc.sendPackets(new S_ServerMessage(141)); // \f1장비 하고 있는 것은, 사람에게 건네줄 수가 없습니다.
			return;
		}

		if (item.getCount() <= 0 || item.getCount() < count || count <= 0) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem()
					.getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.   
            pc.sendPackets(new S_Disconnect());
			return;
		}

		if (count >= item.getCount()) {
			count = item.getCount();
		}
		if(count != 1){ // 아이템 주기 갯수제한
			if (!(item.getItemId() == 40521 || item.getItemId() == 40494
					|| item.getItemId() == 40508 || item.getItemId() == 40045
					|| item.getItemId() == 88 || item.getItemId() == 40057)){  // 브롭 재료템, 괴물눈고기
			pc.sendPackets(new S_ServerMessage(942)); // 상대의 아이템이 너무 무겁기 (위해)때문에, 더 이상 줄 수 없습니다.
			return;
			}
		}
		if (!item.getItem().isTradable() || item.getItemId() == 500042) { // 회상의 촛불
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
			return;
		}
		for (Object petObject : pc.getPetList().values()) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					return;
				}
			}
		}
		if (targetInv.checkAddItem(item, count) != L1Inventory.OK) {
			pc.sendPackets(new S_ServerMessage(942)); // 상대의 아이템이 너무 무겁기 (위해)때문에, 더 이상 줄 수 없습니다.
			return;
		}
		item = inv.tradeItem(item, count, targetInv);
		target.onGetItem(item);
		target.turnOnOffLight();
		pc.turnOnOffLight();

		L1PetType petType = PetTypeTable.getInstance().get(
				target.getNpcTemplate().get_npcId());
		if (petType == null || target.isDead()) {
			return;
		}

		if (item.getItemId() == petType.getItemIdForTaming()) {
			tamePet(pc, target);
		}
		if (item.getItemId() == 40070 && petType.canEvolve()) {
			evolvePet(pc, target);
		}
		if (item.getItemId() == 41310 && petType.canGold()) { // 승자의 열매
			goldPet(pc, target);
		}
	}

	private final static String receivableImpls[] = new String[] { "L1Npc", // NPC
			"L1Monster", // monster
			"L1Guardian", // 에르프의 숲의 수호자
			"L1Teleporter", // 텔레 포터
			"L1Guard" }; // 가이드

	private boolean isNpcItemReceivable(L1Npc npc) {
		for (String impl : receivableImpls) {
			if (npc.getImpl().equals(impl)) {
				return true;
			}
		}
		return false;
	}

	private void tamePet(L1PcInstance pc, L1NpcInstance target) {
		if (target instanceof L1PetInstance
				|| target instanceof L1SummonInstance) {
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 군주
			charisma += 6;
		} else if (pc.isElf()) { // 에르프
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		} else if (pc.isBlackWizard()) { // 환술사
			charisma += 6;
		}
		charisma -= petcost;

		L1PcInventory inv = pc.getInventory();
		String npcname = target.getNpcTemplate().get_name();

		if (charisma >= 6 && inv.getSize() < 180) {
			if (isTamePet(target)) {
				L1ItemInstance petamu = inv.storeItem(40314, 1); // 펫의 아뮤렛트
				if (petamu != null) {
					new L1PetInstance(target, pc, petamu.getId());
					pc.sendPackets(new S_ItemName(petamu));
					 pc.sendPackets(new S_SystemMessage(npcname + "의 목걸이를 얻었습니다."));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(324)); // 길들이는데 실패했습니다.
			}
		}
	}

	private void evolvePet(L1PcInstance pc, L1NpcInstance target) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PcInventory inv = pc.getInventory();
		L1PetInstance pet = (L1PetInstance) target;
		L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
		String npcname = target.getNpcTemplate().get_name();
		if (pet.getLevel() >= 30 && // Lv30 이상
				pc == pet.getMaster() && // 자신의 애완동물
				petamu != null) {
			L1ItemInstance highpetamu = inv.storeItem(40316, 1);
			if (highpetamu != null) {
				pet.evolvePet( // 진화시킨다
						highpetamu.getId());
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
				pc.sendPackets(new S_SystemMessage(npcname + "의 진화에 성공 하였습니다."));

			}
		}else{
			   pc.sendPackets(new S_SystemMessage(npcname + "의 진화조건이 충족돼지 않았습니다."));
		  }

	}
	
	private void goldPet(L1PcInstance pc, L1NpcInstance target) { // 골드 드래곤
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PcInventory inv = pc.getInventory();
		L1PetInstance pet = (L1PetInstance) target;
		L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
		String npcname = target.getNpcTemplate().get_name();
		if (pet.getLevel() >= 30 && // Lv30 이상
				pc == pet.getMaster() && // 자신의 애완동물
				petamu != null) {
			L1ItemInstance highpetamu = inv.storeItem(40316, 1);
			if (highpetamu != null) {
				pet.goldPet( // 진화시킨다
						highpetamu.getId());
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
				pc.sendPackets(new S_SystemMessage(npcname + "의 진화에 성공 하였습니다."));
			}
		}else{
			   pc.sendPackets(new S_SystemMessage(npcname + "의 진화조건이 충족돼지 않았습니다."));
		  }
	}

	private boolean isTamePet(L1NpcInstance npc) {
		boolean isSuccess = false;
		int npcId = npc.getNpcTemplate().get_npcId();
		if (npcId == 45313) { // 타이거
			if (npc.getMaxHp() / 3 > npc.getCurrentHp() // HP가1/3미만으로1/16의 확률
					&& _random.nextInt(16) == 15) {
				isSuccess = true;
			}
		} else {
			if (npc.getMaxHp() / 3 > npc.getCurrentHp()) {
				isSuccess = true;
			}
		}

		if (npcId == 45313 || npcId == 45044 || npcId == 45711) { // 타이거, 라쿤, 기주견의 강아지
			if (npc.isResurrect()) { // RES 후는 테임 불가
				isSuccess = false;
			}
		}

		return isSuccess;
}
	
	/**
	 * 월드상에 있는 모든 캐릭의 계정을 비교해 같은 계정이 있다면 true 없다면 false
	 * @param c L1PcInstance
	 * @return 있다면 true
	 */
	private boolean isTwoLogin(L1PcInstance c) {
		boolean bool = false;
		for(L1PcInstance target : L1World.getInstance().getAllPlayers3()){
			if(c.getId() != target.getId() && !target.isPrivateShop()){
				if(c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}

	@Override
	public String getType() {
		return C_GIVE_ITEM;
	}
}
