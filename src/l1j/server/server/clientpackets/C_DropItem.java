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

import l1j.server.server.ClientThread;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_Disconnect; 
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage; 
import l1j.server.server.model.item.L1ItemId;	
import l1j.server.server.BugKick;
import l1j.server.server.model.L1PcInventory;

public class C_DropItem extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_DropItem.class.getName());
	private static final String C_DROP_ITEM = "[C] C_DropItem";

	public C_DropItem(byte[] decrypt, ClientThread client)
			throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int count = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc == null) {
			return;
		}

		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}

		if (pc.isGhost()) {
			return;
		}
        
		// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
		if(isTwoLogin(pc)) return;
		
		L1ItemInstance item = pc.getInventory().getItem(objectId);
		if (item != null) {
			if (!item.getItem().isTradable()) {
				// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName()));
				return;
			}
        if (item.getLockitem() > 100){
            pc.sendPackets(new S_SystemMessage("봉인된 아이템은 버릴 수 없습니다."));
            return;
        }
			Object[] petlist = pc.getPetList().values().toArray();
			for (Object petObject : petlist) {
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

			//** 아이템픽업방지 **//	by 도우너
			if (objectId != item.getId()) {  //바꺼치기 부분
				return; 
			}
			
			if (item.getCount() < count ) {
				return;
			}
			
			if (count <= 0 ) {
				BugKick.getInstance().KickPlayer(pc);
				return;
			}
	        int i9 = item.getItem(). getItemId();
		          if(!(i9 == 40308  //아데나가 아닐때
			          || i9 == 40019 || i9 == 40020 || i9 == 40738 //농축 물약이 아닐때
			          || i9 == 40022 || i9 == 40023 || i9 == 40023 //고대의 물약이 아닐때
			          || i9 == 40059 || i9 == 40060 || i9 == 40061 //축?고대의 물약이 아닐때
			          || i9 == 40010 || i9 == 40011 || i9 == 40012 //빨,주,맑 물약이 아닐때
			          || i9 == 40494 || i9 == 40743 || i9 == 40744 //미스릴, 미쓰릴화살, 은화살이 아닐때
			          || i9 == 40748 || i9 == 70022 || i9 == 40507 //결정체가 아닐때 
			          || i9 == 40318 || i9 == 40319 || i9 == 40320
                      || i9 == 40062 || i9 == 41159)) {  // 신비한 날개 깃털
		          if(count > 2000 ||item.getCount() > 2000 || item.getCount() <= 0) { 
			          pc.sendPackets(new S_Disconnect());
			          return;
			          }
	              }
             
             if (item.getCount() < count || count <= 0 || count > 2000000000)  {
                 pc.sendPackets(new S_Disconnect());
                 return;
             } 
						 
			 if (pc.getLevel() < 1) { // 아이템 드롭 가능 레벨 설정
                pc.sendPackets(new S_SystemMessage("\\fY80레벨부터 아이템을 버릴 수 있습니다."));
				return;
			 }
			 if (!pc.isGm() && item.getItem().getItemId() == 40308 )  {
	            pc.sendPackets(new S_SystemMessage("아데나를 버릴 수 없습니다."));
	            return;
	         }  
	             
	         if (!pc.isGm() && item.getItem().getItemId() == 41159 )  {
	            pc.sendPackets(new S_SystemMessage("신비한 날개 깃털을 버릴 수 없습니다."));
	            return;
	         }
			 if (item.isEquipped()) {
				// \f1삭제할 수 없는 아이템이나 장비 하고 있는 아이템은 버릴 수 없습니다.
				pc.sendPackets(new S_ServerMessage(125));
				return;
			 }
			 if (item.getEnchantLevel() >= 4 && !pc.isGm()){
		        pc.sendPackets(new S_SystemMessage("+4이상 인첸트된 아이템은 버릴수없습니다."));
			    return;
			 }

			/**인형 중복 방지**/
			  Object[] dollList = pc.getDollList().values().toArray();
			   for (Object dollObject : dollList) {
				   L1DollInstance doll = (L1DollInstance) dollObject;
			   if (doll.getItemObjId() == item.getId()) { // 이미 꺼내고 있는 마법 인형
			       return;
			      }
		       }
		    /**인형 중복 방지**/

			pc.getInventory().tradeItem(item, count, L1World.getInstance().getInventory(x, y, pc.getMapId()));
			pc.turnOnOffLight();
		}
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
		return C_DROP_ITEM;
	}	
}