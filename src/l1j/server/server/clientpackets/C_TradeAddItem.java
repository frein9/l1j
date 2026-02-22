/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be trading_partnerful,
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
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Disconnect;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_TradeAddItem extends ClientBasePacket {
	private static final String C_TRADE_ADD_ITEM = "[C] C_TradeAddItem";
	private static Logger _log = Logger.getLogger(C_TradeAddItem.class
			.getName());

	public C_TradeAddItem(byte abyte0[], ClientThread client)
			throws Exception {
		super(abyte0);

		int itemid = readD();
		int itemcount = readD();
		L1PcInstance pc = client.getActiveChar();
		if (pc.getOnlineStatus() != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		L1Trade trade = new L1Trade();
		L1ItemInstance item = pc.getInventory().getItem(itemid);
		
		/*버그방지*/
		if (itemid != item.getId()) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (!item.isStackable() && itemcount != 1) {
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (itemcount > item.getCount()) {
			itemcount = item.getCount();
		}
		/*버그방지*/

		//** 복사버그 방지 **//	by 도우너
		int itemType = item.getItem().getType2();
		if (!item.getItem().isTradable() || itemcount <= 0 || item.getCount() < itemcount || item.getCount() <= 0) {
		//** 복사버그 방지 **//	by 도우너	
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0은 버리거나 또는 타인에게 양일을 할 수 없습니다.
			return;
		}
		if (item.getLockitem() > 100){
           pc.sendPackets(new S_SystemMessage("봉인된 아이템은 거래할 수 없습니다."));
          return;
       }			
					
        if (itemType == 1 && itemcount != 1){
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (itemType == 2 && itemcount != 1){
			pc.sendPackets(new S_Disconnect());
			return;
		}
		if (itemcount > 2000000000)  {
			return;
		}
		if (item.getItem().getItemId() == 41159) { // 신비깃털 방지
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
    /*	L1PcInstance tradingPartner = (L1PcInstance) L1World.getInstance()
				.findObject(pc.getTradeID());
		if (tradingPartner == null) {
			return;
		}
		if (pc.getTradeOk()) { // ok를 누른상태라면 아이템 더이상 못 올림
			return;
		}
		if (tradingPartner.getInventory().checkAddItem(item, itemcount)
				!= L1Inventory.OK) { // 용량 중량 확인 및 메세지 송신
			tradingPartner.sendPackets(new S_ServerMessage(270)); // \f1 가지고 있는 것이 무거워서 거래할 수 없습니다.
			pc.sendPackets(new S_ServerMessage(271)); // \f1상대가 물건을 너무 가지고 있어 거래할 수 없습니다.
			return;
                        }	 */
 	

	
	
	
	






		trade.TradeAddItem(pc, itemid, itemcount);
	}

	@Override
	public String getType() {
		return C_TRADE_ADD_ITEM;
	}
}
