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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.LoginController;
import l1j.server.server.clientpackets.C_LoginToServer;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.shop.L1ShopBuyOrderList;
import l1j.server.server.model.shop.L1ShopSellOrderList;
import l1j.server.server.serverpackets.S_Disconnect; // ########## (버그 방지) 개인 상점 버그, 창고 복사 버그 방지 
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage; // ########## 창고 아이템 격납 제한 메시지 출력 위해 임포트 
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;	   
import l1j.server.server.BugKick;	//** 버그쟁이 처단 **//	By 도우너

public class C_Result extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Result.class
			.getName());
	private static final String C_RESULT = "[C] C_Result";
	
	private int _loginStatus = 0;  // 추가
	
	public int ReturnToLogin = 0;

	public C_Result(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);
		int npcObjectId = readD();
		int resultType = readC();
		int size = readC();
		int unknown = readC();

		L1PcInstance pc = clientthread.getActiveChar();
		int level = pc.getLevel();

		if(size < 0){
			pc.sendPackets(new S_Disconnect());
			return;
		}
		
		int npcId = 0;
		String npcImpl = "";
		boolean isPrivateShop = false;
		boolean tradable = true;
		L1Object findObject = L1World.getInstance().findObject(npcObjectId);
		if (findObject != null) {
			int diffLocX = Math.abs(pc.getX() - findObject.getX());
			int diffLocY = Math.abs(pc.getY() - findObject.getY());
			// 3 매스 이상 떨어졌을 경우 액션 무효
			if (diffLocX > 3 || diffLocY > 3) {
				return;
			}
			if (findObject instanceof L1NpcInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) findObject;
				npcId = targetNpc.getNpcTemplate().get_npcId();
				npcImpl = targetNpc.getNpcTemplate().getImpl();
			} else if (findObject instanceof L1PcInstance) {
				isPrivateShop = true;
			}
		}

	    ////중복 접속 버그방지 
        if(pc.getOnlineStatus() == 0){
        	clientthread.kick();
           return;
        }
        ////중복 접속 버그방지 
        if (resultType == 0 && size != 0
		&& npcImpl.equalsIgnoreCase("L1Merchant")) { // 아이템 구입
	if(level >= 100 && pc.getClanid() <= 0){ 
        pc.sendPackets(new S_SystemMessage("\\fR레벨 70이상 혈맹에 가입하셔야 상점 이용이 가능합니다.")); 
        return ; 
        }else{ 
        }
	L1Shop shop = ShopTable.getInstance().get(npcId);
	L1ShopBuyOrderList orderList = shop.newBuyOrderList();
	int itemNumber; long itemcount;
		
////////////////////////////버그방지////////////////////////////	
			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if(itemcount <= 0) {
					return;
				}
				orderList.add(itemNumber, (int)itemcount , pc);	
			}
////////////////////////////버그방지////////////////////////////
			int bugok = orderList.BugOk();
			if (bugok == 0){
				shop.sellItems(pc, orderList);
			}

//////////////////////////////////////////////////중략///////////////////////////////////////////
					
		} else if (resultType == 1 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // 아이템 매각
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
			int itemNumber; long itemcount;
			///////////////////////////////////////
/*			for (int i = 0; i < size; i++) {
				orderList.add(readD(), readD());
			}
				shop.buyItems(orderList);	*/
			
			//** 상점 비셔스 버그 방지로 위에주석 아래 수정 **// by 도우너			
			for (int i = 0; i < size; i++) {
				itemNumber = readD();
				itemcount = readD();
				if(itemcount <= 0){
					return;
				}
				orderList.add(itemNumber, (int)itemcount , pc);	
			}
			int bugok = orderList.BugOk();
			if (bugok == 0){
			shop.buyItems(orderList);
			 	}
			//** 상점 비셔스 버그 방지로 위에주석 아래 수정 **// by 도우너			
			
		} else if (resultType == 2 && size != 0
				 && npcImpl.equalsIgnoreCase("L1Dwarf")) { // 자신의 창고에 격납 
                       if (level < 5 ) { 
                           pc.sendPackets(new S_SystemMessage("\\fU창고는 5레벨 이상 사용 가능 합니다.")); 
                           return; 
                           }			
			// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
			if(isTwoLogin(pc)) return;

			int objectId; 
			long count;
               //비수량버그//레츠비
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				if(count < 0){
					pc.sendPackets(new S_Disconnect());
					_log.info("창고 맡기기 버그 시도 : char=" + pc.getName());
					return;
				}
                //비수량버그//레츠비
				L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
				
			    int itemType = item.getItem().getType2();
			    
			    /*버그방지*/
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/*버그방지*/
				
			    if (item == null || item.getCount() < count) {
			    	 pc.sendPackets(new S_Disconnect());
			         return;
			    }
			    if ((itemType == 1 && item.getCount() != 1) ||	(itemType == 2 && item.getCount() != 1)){
					pc.sendPackets(new S_Disconnect());
					return;
				}
			    if (count <= 0 || count < 1 || item.getCount() <= 0) {
			    	BugKick.getInstance().KickPlayer(pc);
			        return;
			    }			
		      /*  if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
                    pc.sendPackets(new S_SystemMessage("아데나는 창고에 맡길 수 없습니다."));
                    return;
			    }
		        
		        if (item.getItem().getItemId() == 40074 || item.getItem().getItemId() == 40087
		        		|| item.getItem().getItemId() == 140074 || item.getItem().getItemId() == 140087
		        		|| item.getItem().getItemId() == 240074 || item.getItem().getItemId() == 240087)  {
                    pc.sendPackets(new S_SystemMessage("아이템 강화주문서는 창고 이용을 할 수 없습니다."));
                    return;
			    }*/
		        
		        if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
					  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
					  return;
				}
			   
				if (item.getLockitem() > 100){
                    pc.sendPackets(new S_SystemMessage("봉인된 아이템은 창고에 맡길 수 없습니다."));
                    return;
                }
		        
				if (!item.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				}
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName()));
							break;
						}
					}
				}
      L1DollInstance doll = null;
      Object[] dollList = pc.getDollList().values().toArray();
      for (Object dollObject : dollList) {
      doll = (L1DollInstance) dollObject;
      if (item.getId() == doll.getItemObjId()) {
      pc.sendPackets(new S_ServerMessage(1181)); // 
       return;
      }
     }
                                if (pc.getDwarfInventory().checkAddItemToWarehouse(item, (int)count,
						L1Inventory.WAREHOUSE_TYPE_PERSONAL) == L1Inventory
								.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1 더 이상 것을 두는 장소가 없습니다.
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, (int)count,
							pc.getDwarfInventory());
					pc.turnOnOffLight();
				}
			}
		} else if (resultType == 3 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 자신의 창고로부터 꺼내
			int objectId; long count;
			L1ItemInstance item;
            //비수량버그//레츠비
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				
				if(count < 0){
					pc.sendPackets(new S_Disconnect());
					_log.info("창고버그 시도자 : char=" + pc.getName());
					return;
				}
				// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
				if(isTwoLogin(pc)) return;
				
				item = pc.getDwarfInventory().getItem(objectId);
				
				if (pc.getInventory().findItemId(40308).getCount() < 31) {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
					return;
				}
				
			    int itemType = item.getItem().getType2();	
			    
			    /*버그방지*/
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/*버그방지*/
			    
			    if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
			           pc.sendPackets(new S_Disconnect());
				       return;
				}	
			    if (item == null || item.getCount() < count  || count <= 0 || item.getCount() <= 0) {
			    	BugKick.getInstance().KickPlayer(pc);
					return;
				}
			    if (item.getCount() > 2000000000) {
				    return;
				}
			    if (count > 2000000000) {
				    return;
				}
			    
			    if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
					  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
					  return;
				}

				if (pc.getInventory().checkAddItem(item, (int)count) == L1Inventory.OK) // 용량 중량 확인 및 메세지 송신
				{
					if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
						pc.getDwarfInventory().tradeItem(item, (int)count,
								pc.getInventory());
					} else {
						pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
					break;
				}
			}
		} else if (resultType == 4 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 크란 창고에 격납
			int objectId; long count;
			if (pc.getClanid() != 0) { // 크란 소속
				for (int i = 0; i < size; i++) {
					tradable = true;
					objectId = readD();
					count = readD();
					if(count < 0){
						pc.sendPackets(new S_Disconnect());
						_log.info("혈맹 창고 버그 시도자 : char=" + pc.getName());
						return;
					}
					// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
					if(isTwoLogin(pc)) return;
					
                    //비수량버그//레츠비
					L1Clan clan = L1World.getInstance().getClan(
							pc.getClanname());
					L1Object object = pc.getInventory().getItem(objectId);
					L1ItemInstance item = (L1ItemInstance) object;

				int itemType = item.getItem().getType2();
				
				/*버그방지*/
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/*버그방지*/
				
				if (item == null || item.getCount() < count) {
					 pc.sendPackets(new S_Disconnect());
			         return;
			    }
			    if ((itemType == 1 && item.getCount() != 1) ||	(itemType == 2 && item.getCount() != 1)){
					pc.sendPackets(new S_Disconnect());
					return;
				}
			    if (count <= 0 || count < 1 || item.getCount() <= 0) {
			    	BugKick.getInstance().KickPlayer(pc);
			        return;
			    }			
			    if (item.getLockitem() > 100){
                    pc.sendPackets(new S_SystemMessage("봉인된 아이템은 창고에 맡길 수 없습니다."));
                    return;
                }
              /*  if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
                     pc.sendPackets(new S_SystemMessage("아데나는 창고에 맡길 수 없습니다."));
                     return;
			    }                
					
                if (item.getItem().getItemId() == 40074 || item.getItem().getItemId() == 40087
		        		|| item.getItem().getItemId() == 140074 || item.getItem().getItemId() == 140087
		        		|| item.getItem().getItemId() == 240074 || item.getItem().getItemId() == 240087)  {
                    pc.sendPackets(new S_SystemMessage("아이템 강화주문서는 창고 이용을 할 수 없습니다."));
                    return;
			    }  */                            
                if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
					  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
					  return;
				}
			    
					if (clan != null) {
						if (!item.getItem().isTradable()) {
							tradable = false;
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
						}
						Object[] petlist = pc.getPetList().values().toArray();
						for (Object petObject : petlist) {
							if (petObject instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) petObject;
								if (item.getId() == pet.getItemObjId()) {
									tradable = false;
									// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
									pc.sendPackets(new S_ServerMessage(210,
											item.getItem().getName()));
									break;
								}
							}
						}
      L1DollInstance doll = null;
      Object[] dollList = pc.getDollList().values().toArray();
      for (Object dollObject : dollList) {
      doll = (L1DollInstance) dollObject;
      if (item.getId() == doll.getItemObjId()) {
      pc.sendPackets(new S_ServerMessage(1181)); // 
       return;
      }
     }
						if (clan.getDwarfForClanInventory()
								.checkAddItemToWarehouse(item, (int)count,
										L1Inventory.WAREHOUSE_TYPE_CLAN)
												== L1Inventory.SIZE_OVER) {
							pc.sendPackets(new S_ServerMessage(75)); // \f1 더 이상 것을 두는 장소가 없습니다.
							break;
						}
						if (tradable) {
							pc.getInventory().tradeItem(objectId, (int)count,
									clan.getDwarfForClanInventory());
							pc.turnOnOffLight();
						}
					}
				}
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1혈맹 창고를 사용하려면  혈맹에 가입하지 않으면 안됩니다.
			}
		} else if (resultType == 5 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 크란 창고로부터 꺼내
			
			if (pc.getInventory().findItemId(40308).getCount() < 31) {
				pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
				return;
			}
			
			// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
			if(isTwoLogin(pc)) return;
						
			int objectId; long count;
			L1ItemInstance item;

			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
                //비수량버그//레츠비
				for (int i = 0; i < size; i++) {
					objectId = readD();
					count = readD();
					if(count < 0){
						pc.sendPackets(new S_Disconnect());
						_log.info("혈맹꺼내기 버그시도자 : char=" + pc.getName());
						return;
					}
                //비수량버그//레츠비
					item = clan.getDwarfForClanInventory().getItem(objectId);
			
				    int itemType = item.getItem().getType2();
				    
				    /*버그방지*/
					if (objectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if (!item.isStackable() && count != 1) {
						pc.sendPackets(new S_Disconnect());
						return;
					}
					if (count > item.getCount()) {
						count = item.getCount();
					}
					/*버그방지*/
				    
				    if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
				           pc.sendPackets(new S_Disconnect());
					       return;
					}
				    if (item == null || item.getCount() < count  || count <= 0 || item.getCount() <= 0) {
				    	BugKick.getInstance().KickPlayer(pc);
						return;
					}
				    if (item.getCount() > 2000000000) {
					    return;
					}
				    if (count > 2000000000) {
					    return;
					}
				    
				   if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
						  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
						  return;
					}

					if (pc.getInventory().checkAddItem(item, (int)count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							clan.getDwarfForClanInventory().tradeItem(item,
									(int)count, pc.getInventory());
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
						break;
					}
				}
				clan.setWarehouseUsingChar(0); // 크란 창고의 락을 해제
			}
		} else if (resultType == 5 && size == 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf")) { // 크란 창고로부터 꺼내 안에 Cancel, 또는, ESC 키
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				clan.setWarehouseUsingChar(0); // 크란 창고의 락을 해제
			}
		} else if (resultType == 8 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5 && pc
						.isElf()) { // 자신의 에르프 창고에 격납
			int objectId; long count;
			
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				if(count < 0){
					pc.sendPackets(new S_Disconnect());
					_log.info("요정 창고 버그 시도자 : char=" + pc.getName());
					return;
				}
				// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
				if(isTwoLogin(pc)) return;
				
                L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
			 
			    int itemType = item.getItem().getType2();
			    
			    /*버그방지*/
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/*버그방지*/
			    
			    if (item == null || item.getCount() < count) {
			    	 pc.sendPackets(new S_Disconnect());
			         return;
			    }
			    if ((itemType == 1 && item.getCount() != 1) ||	(itemType == 2 && item.getCount() != 1)){
					pc.sendPackets(new S_Disconnect());
					return;
				}
			    if (count <= 0 || count < 1 || item.getCount() <= 0) {
			    	BugKick.getInstance().KickPlayer(pc);
			        return;
			    }	
			    if (item.getLockitem() > 100){
                    pc.sendPackets(new S_SystemMessage("봉인된 아이템은 창고에 맡길 수 없습니다."));
                    return;
                }
              /*  if (!pc.isGm() && item.getItem().getItemId() == 40308)  {
                    pc.sendPackets(new S_SystemMessage("아데나는 창고에 맡길 수 없습니다."));
                    return;
		            }						
                if (item.getItem().getItemId() == 40074 || item.getItem().getItemId() == 40087
		        		|| item.getItem().getItemId() == 140074 || item.getItem().getItemId() == 140087
		        		|| item.getItem().getItemId() == 240074 || item.getItem().getItemId() == 240087)  {
                    pc.sendPackets(new S_SystemMessage("아이템 강화주문서는 창고 이용을 할 수 없습니다."));
                    return;
			    }*/
                
                if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
					  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
					  return;
				}
			     
				if (!item.getItem().isTradable()) {
					tradable = false;
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
				}
			
				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							tradable = false;
							// \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName()));
							break;
						}
					}
				}
      L1DollInstance doll = null;
      Object[] dollList = pc.getDollList().values().toArray();
      for (Object dollObject : dollList) {
      doll = (L1DollInstance) dollObject;
      if (item.getId() == doll.getItemObjId()) {
      pc.sendPackets(new S_ServerMessage(1181)); // 
       return;
      }
     }

				if (pc.getDwarfForElfInventory().checkAddItemToWarehouse(item,
						(int)count, L1Inventory.WAREHOUSE_TYPE_PERSONAL) ==
								L1Inventory.SIZE_OVER) {
					pc.sendPackets(new S_ServerMessage(75)); // \f1 더 이상 것을 두는 장소가 없습니다.
					break;
				}
				if (tradable) {
					pc.getInventory().tradeItem(objectId, (int)count,
							pc.getDwarfForElfInventory());
					pc.turnOnOffLight();
				}
			}
		} else if (resultType == 9 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5 && pc
						.isElf()) { // 자신의 에르프 창고로부터 꺼내
			
			//int objectId, count;
			int objectId; long count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				if(count < 0){
					pc.sendPackets(new S_Disconnect());
					_log.info("요정창고 꺼내기 버그시도자 : char=" + pc.getName());
					return;
				}
				// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
				if(isTwoLogin(pc)) return;
				
                //비수량버그//레츠비
				item = pc.getDwarfForElfInventory().getItem(objectId);
			
				if (pc.getInventory().findItemId(40308).getCount() < 31) {
					pc.sendPackets(new S_SystemMessage("아데나가 부족합니다."));
					return;
				}
				
			    int itemType = item.getItem().getType2();	
			    
			    /*버그방지*/
				if (objectId != item.getId()) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (!item.isStackable() && count != 1) {
					pc.sendPackets(new S_Disconnect());
					return;
				}
				if (count > item.getCount()) {
					count = item.getCount();
				}
				/*버그방지*/
			    
			    if ((itemType == 1 && item.getCount() != 1) || (itemType == 2 && item.getCount() != 1)) {
			           pc.sendPackets(new S_Disconnect());
				       return;
				}
			    if (item == null || item.getCount() < count  || count <= 0 || item.getCount() <= 0) {
			    	BugKick.getInstance().KickPlayer(pc);
					return;
				}
			    if (item.getCount() > 2000000000) {
				    return;
				}
			    if (count > 2000000000) {
				    return;
				}
			    if (item.getItem().getItemId() == 41159 || item.getItem().getItemId() == 41246){ 
					  pc.sendPackets(new S_SystemMessage("해당 아이템은 창고 이용을 할 수 없습니다."));
					  return;
				}
				if (pc.getInventory().checkAddItem(item, (int)count) == L1Inventory
						.OK) { // 용량 중량 확인 및 메세지 송신
					if (pc.getInventory(). consumeItem(40494, 2)) { // 미스릴
						pc.getDwarfForElfInventory().tradeItem(item, (int)count,
								pc.getInventory());
					} else {
						pc.sendPackets(new S_ServerMessage(337,"$767")); // \f1%0이 부족합니다.
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
					break;
				}
			}
		} else if (resultType == 0 && size != 0 && isPrivateShop) { // 개인 상점으로부터 아이템 구입
			int order;
			long count;
			int price;
			ArrayList sellList;
			L1PrivateShopSellList pssl;
			int itemObjectId;
			int sellPrice;
			int sellTotalCount;
			int sellCount;
			L1ItemInstance item;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
				if (targetPc == null) {
					return;
				}
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}
			sellList = targetPc.getSellList();
			synchronized (sellList) {
				// 품절이 발생해, 열람중의 아이템수와 리스트수가 다르다
				if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
					return;
				}
				targetPc.setTradingInPrivateShop(true);

				for (int i = 0; i < size; i++) { // 구입 예정의 상품
					order = readD();
					count = readD();
					if(count < 0){
						pc.sendPackets(new S_Disconnect());
					_log.info("개인상점 버그 시도 : char=" + pc.getName());
						return;
					}					
					pssl = (L1PrivateShopSellList) sellList.get(order);
					itemObjectId = pssl.getItemObjectId();
					sellPrice = pssl.getSellPrice();
					sellTotalCount = pssl.getSellTotalCount(); // 팔 예정의 개수
					sellCount = pssl.getSellCount(); // 판 누계
					item = targetPc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}
				    if (item.isEquipped()) {
				        pc.sendPackets(new S_ServerMessage(905, "")); // 장비 하고 있는 아이템 구매못하게.
				        continue;
				    }
					if (count > sellTotalCount - sellCount) {
						count = sellTotalCount - sellCount;
					}
					if (count == 0) {
						continue;
					}	

				if (pc.getInventory().checkAddItem(item, (int)count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
						for (int j = 0; j < count; j++) { // 오버플로우를 체크
						if (sellPrice * j > 100000000) {
								pc.sendPackets(new S_ServerMessage(904, // 총판 매가격은%d아데나를 초과할 수 없습니다.
									"100000000"));
								targetPc.setTradingInPrivateShop(false);
								return;
							}
						}
					price = (int)count * sellPrice;	 
					
					if (item.isEquipped()){
						targetPc.sendPackets(new S_SystemMessage("상점에 등록된 아이템이 착용중인지 확인하십시요."));
						pc.sendPackets(new S_SystemMessage("상대방이 아이템을 착용하고 있습니다."));							
						 return;
					}
					/*버그방지*/
					if (itemObjectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}
					if (!item.isStackable() && count != 1) {
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}
					if (count >= item.getCount()) {
						count = item.getCount();
					}
					/*버그방지*/
									
					if (item.getCount() <= 0 || count <= 0 || item.getCount() < count) {
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}			 
					if (price <= 0 || item.getCount() > 1000) {                     
						    return;
					}
					
					if (price > 2000000000)	{
					        return;
					}	
					if (item.getItem().getItemId() == 41159) { // 신비깃털 방지
						    return;
					}
				

						if (pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
							L1ItemInstance adena = pc.getInventory()
									.findItemId(L1ItemId.ADENA);
							if (targetPc != null && adena != null) {
								if (targetPc.getInventory().tradeItem(item,
										(int)count, pc.getInventory()) == null) {
									targetPc.setTradingInPrivateShop(false);
									return;
								}
								pc.getInventory().tradeItem(adena, price,
										targetPc.getInventory());
								String message = item.getItem().getName()
										+ " (" + String.valueOf(count) + ")";
								targetPc.sendPackets(new S_ServerMessage(877, // %1%o
										// %0에 판매했습니다.
										pc.getName(), message));
										pc.sendPackets(new S_ServerMessage(143, // %1%o
												// %0에 판매했습니다.
												targetPc.getName(), message));  // 구입메세지 추가
										
							pssl.setSellCount((int)count + sellCount);
								sellList.set(order, pssl);
								if (pssl.getSellCount() == pssl
										.getSellTotalCount()) { // 팔 예정의 개수를 팔았다
									isRemoveFromList[order] = true;
								}	
								try {
							         pc.saveInventory();
							         targetPc.saveInventory();
							        } catch (Exception e) {
//							     //_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
							        }
							}
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
						break;
					}
				}
				// 품절된 아이템을 리스트의 말미로부터 삭제
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						sellList.remove(i);
					}
				}
				targetPc.setTradingInPrivateShop(false);
			}
		} else if (resultType == 1 && size != 0 && isPrivateShop) { // 개인 상점에 아이템 매각
			long count;
			int order;
			ArrayList buyList;
			L1PrivateShopBuyList psbl;
			int itemObjectId;
			L1ItemInstance item;
			int buyPrice;
			int buyTotalCount;
			int buyCount;
			L1ItemInstance targetItem;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
				if (targetPc == null) {
					return;
				}
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}
			targetPc.setTradingInPrivateShop(true);
			buyList = targetPc.getBuyList();

			for (int i = 0; i < size; i++) {
				itemObjectId = readD();
				count = readCH();
				order = readC();
				
				if(count < 0){
					pc.sendPackets(new S_Disconnect());
					_log.info("개인상점 버그 시도 : char=" + pc.getName());
					return;
				}
				
				item = pc.getInventory().getItem(itemObjectId);
				if (item == null) {
					continue;
				}
				psbl = (L1PrivateShopBuyList) buyList.get(order);
				buyPrice = psbl.getBuyPrice();
				buyTotalCount = psbl.getBuyTotalCount(); // 살 예정의 개수
				buyCount = psbl.getBuyCount(); // 산 누계
				if (count > buyTotalCount - buyCount) {
					count = buyTotalCount - buyCount;
				}
				if (item.isEquipped()) {
					pc.sendPackets(new S_ServerMessage(905)); // 장비 하고 있는 아이템은 판매할 수 없습니다.
					continue;
				}

				if (targetPc.getInventory().checkAddItem(item, (int)count) == L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
					for (int j = 0; j < count; j++) { // 오버플로우를 체크
						if (buyPrice * j > 100000000) {
							targetPc.sendPackets(new S_ServerMessage(904, // 총판 매가격은%d아데나를 초과할 수 없습니다.
									"100000000"));
							return;
						}
					}
					//** 개인상점 부분 비셔스 방어 **//	by 도우너					  
					int itemType = item.getItem().getType2(); 
					/*버그방지*/
					if (itemObjectId != item.getId()) {
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}
					if (!item.isStackable() && count != 1) {
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}
					if (count >= item.getCount()) {
						count = item.getCount();
					}
					/*버그방지*/
					if ((itemType == 1 && count != 1) || (itemType == 2 && count != 1)) {
						return;
					}  
					if (item.getCount() <= 0 || count <= 0 || item.getCount() < count) { 
						pc.sendPackets(new S_Disconnect());
						targetPc.sendPackets(new S_Disconnect());
						return;
					}
					if (buyPrice * count <= 0 || buyPrice * count > 2000000000) {
						return;
					}
					if (item.getItem().getItemId() == 41159) { // 신비깃털 방지
						return;
					}					
					//** 개인상점 부분 비셔스 방어 **//	by 도우너	

					if (targetPc.getInventory().checkItem(L1ItemId.ADENA,
							(int)count * buyPrice)) {
						L1ItemInstance adena = targetPc.getInventory()
								.findItemId(L1ItemId.ADENA);
						if (adena != null) {
							targetPc.getInventory().tradeItem(adena,
									(int)count * buyPrice, pc.getInventory());
							pc.getInventory().tradeItem(item, (int)count,
									targetPc.getInventory());
						
							
							psbl.setBuyCount((int)count + buyCount);
							buyList.set(order, psbl);
							if (psbl.getBuyCount() == psbl.getBuyTotalCount()) { // 살 예정의 개수를 샀다
								isRemoveFromList[order] = true;
							}	
							try {
						          pc.saveInventory();
						          targetPc.saveInventory();
						         } catch (Exception e) {
//						          _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
						         } 
						}
					} else {
						targetPc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
						break;
					}
				} else {
					pc.sendPackets(new S_ServerMessage(271)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
					break;
				}
			}
			// 매점한 아이템을 리스트의 말미로부터 삭제
			for (int i = 7; i >= 0; i--) {
				if (isRemoveFromList[i]) {
					buyList.remove(i);
				}
			}
			targetPc.setTradingInPrivateShop(false);
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
		return C_RESULT;
	}
}