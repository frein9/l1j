/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.util.List;

import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Gambling;
import l1j.server.server.model.L1Gambling2;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_TradeAddItem;
import l1j.server.server.serverpackets.S_TradeStatus;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

// Referenced classes of package l1j.server.server.model:
// L1Trade

public class L1Trade {
	private static L1Trade _instance;

	public L1Trade() {
	}

	public static L1Trade getInstance() {
		if (_instance == null) {
			_instance = new L1Trade();
		}
		return _instance;
	}

	public void TradeAddItem(L1PcInstance player, int itemid, int itemcount) {
		L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance()
				.findObject(player.getTradeID());
		L1ItemInstance l1iteminstance = player.getInventory().getItem(itemid);
		if (l1iteminstance != null && trading_partner != null) {
			if (!l1iteminstance.isEquipped()) {
				if (l1iteminstance.getCount() < itemcount || 0 >= itemcount
						 || player.getParalysis() != null || player.isInvisble()
					     || trading_partner.getParalysis() != null || trading_partner.isInvisble()) { 
					/*player.sendPackets(new S_TradeStatus(1));
					trading_partner.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					trading_partner.setTradeOk(false);
					player.setTradeID(0);
					trading_partner.setTradeID(0);*/
					L1Trade trade = new L1Trade(); // 추가
   			        trade.TradeCancel(player); // 추가 
    			    trade.TradeCancel(trading_partner); // 거래창 버그 방지 수정 By_Black
					return;
				}
				player.getInventory().tradeItem(l1iteminstance, itemcount,
						player.getTradeWindowInventory());
				player.sendPackets(new S_TradeAddItem(l1iteminstance,
						itemcount, 0));
				trading_partner.sendPackets(new S_TradeAddItem(l1iteminstance,
						itemcount, 1));
				
				//** 2중 교환 버그 수정  **//		By도우너
				player.setTradeTarget(null);	
				//** 2중 교환 버그 수정  **//		By도우너
				
		}
		}
		else{
			if (!l1iteminstance.isEquipped()) {
				if (l1iteminstance.getCount() < itemcount || 0 > itemcount || l1iteminstance.getItem().getItemId() != L1ItemId.ADENA && itemcount != 100000) {
					player.sendPackets(new S_TradeStatus(1));
					player.setTradeOk(false);
					player.setTradeID(0);
					return;
				}
				player.getInventory().tradeItem(l1iteminstance, itemcount,
						player.getTradeWindowInventory());
				player.sendPackets(new S_TradeAddItem(l1iteminstance,
						itemcount, 0));	
				
			}
		}
	}


	public void TradeOK(L1PcInstance player) {
		int cnt;
		L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance()
				.findObject(player.getTradeID());
		if (trading_partner != null) {
			List player_tradelist = player.getTradeWindowInventory().getItems();
			int player_tradecount = player.getTradeWindowInventory().getSize();

			List trading_partner_tradelist = trading_partner
					.getTradeWindowInventory().getItems();
			int trading_partner_tradecount = trading_partner
					.getTradeWindowInventory().getSize();

			for (cnt = 0; cnt < player_tradecount; cnt++) {
				L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist
						.get(0);
				player.getTradeWindowInventory().tradeItem(l1iteminstance1,
						l1iteminstance1.getCount(),
						trading_partner.getInventory());
			}
			for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
				L1ItemInstance l1iteminstance2 = (L1ItemInstance) trading_partner_tradelist
						.get(0);
				trading_partner.getTradeWindowInventory().tradeItem(
						l1iteminstance2, l1iteminstance2.getCount(),
						player.getInventory());
			}

			player.sendPackets(new S_TradeStatus(0));
			trading_partner.sendPackets(new S_TradeStatus(0));
			player.setTradeOk(false);
			trading_partner.setTradeOk(false);
			player.setTradeID(0);
			trading_partner.setTradeID(0);
            player.setTrade(false);
	     	trading_partner.setTrade(false);
			//** 2중 교환 버그 수정  **//		By도우너
			player.setTradeTarget(null);	
			//** 2중 교환 버그 수정  **//		By도우너	   
			player.turnOnOffLight();
			trading_partner.turnOnOffLight();
		
	           
				}else if(player.getX() == 33507 && player.getY() == 32851 && player.getMapId() == 4){
				List player_tradelist = player.getTradeWindowInventory().getItems();
				 L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist.get(0);
				player.getTradeWindowInventory().consumeItem(l1iteminstance1.getItemId() , l1iteminstance1.getCount());
				player.sendPackets(new S_TradeStatus(0));
				player.setTradeOk(false);
				L1Gambling gambling = new L1Gambling();
				gambling.Gambling(player, l1iteminstance1.getCount());

				//** 2중 교환 버그 수정  **//		By도우너
				player.setTradeTarget(null);	
				//** 2중 교환 버그 수정  **//		By도우너
				
				}else if(player.getX() == 33420 && player.getY() == 32799 && player.getMapId() == 4){
				List player_tradelist = player.getTradeWindowInventory().getItems();
				 L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist.get(0);
				player.getTradeWindowInventory().consumeItem(l1iteminstance1.getItemId() , l1iteminstance1.getCount());
				player.sendPackets(new S_TradeStatus(0));
				player.setTradeOk(false);
				L1Gambling2 gambling = new L1Gambling2();
				gambling.Gambling(player, l1iteminstance1.getCount());
				//** 2중 교환 버그 수정  **//		By도우너
				//player.setTradeTarget(null};
				//** 2중 교환 버그 수정  **//		By도우너
				
				}else if(player.getX() == 33515 && player.getY() == 32851 && player.getMapId() == 4){
					List player_tradelist = player.getTradeWindowInventory().getItems();
					 L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist.get(0);
					player.getTradeWindowInventory().consumeItem(l1iteminstance1.getItemId() , l1iteminstance1.getCount());
					player.sendPackets(new S_TradeStatus(0));
					player.setTradeOk(false);
					L1Gambling3 gambling = new L1Gambling3();
					gambling.Gambling(player, l1iteminstance1.getCount());
					//** 2중 교환 버그 수정  **//		By도우너
					//player.setTradeTarget(null};
					//** 2중 교환 버그 수정  **//		By도우너
				
				}
	        }

	public void TradeCancel(L1PcInstance player) {
		int cnt;
		L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance()
				.findObject(player.getTradeID());
		if (trading_partner != null) {
			List player_tradelist = player.getTradeWindowInventory().getItems();
			int player_tradecount = player.getTradeWindowInventory().getSize();

			List trading_partner_tradelist = trading_partner
					.getTradeWindowInventory().getItems();
			int trading_partner_tradecount = trading_partner
					.getTradeWindowInventory().getSize();

			for (cnt = 0; cnt < player_tradecount; cnt++) {
				L1ItemInstance l1iteminstance1 = (L1ItemInstance) player_tradelist
						.get(0);
				player.getTradeWindowInventory().tradeItem(l1iteminstance1,
						l1iteminstance1.getCount(), player.getInventory());
			}
			for (cnt = 0; cnt < trading_partner_tradecount; cnt++) {
				L1ItemInstance l1iteminstance2 = (L1ItemInstance) trading_partner_tradelist
						.get(0);
				trading_partner.getTradeWindowInventory().tradeItem(
						l1iteminstance2, l1iteminstance2.getCount(),
						trading_partner.getInventory());
			}

			player.sendPackets(new S_TradeStatus(1));
			trading_partner.sendPackets(new S_TradeStatus(1));
			player.setTradeOk(false);
			trading_partner.setTradeOk(false);
			player.setTradeID(0);
			trading_partner.setTradeID(0);
			player.setTrade(false);
	     	trading_partner.setTrade(false);
			//** 2중 교환 버그 수정  **//		By도우너
			player.setTradeTarget(null);	
			//** 2중 교환 버그 수정  **//		By도우너
		}
	}
}
