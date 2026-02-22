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
package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import l1j.server.Config;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage; 
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;

public class L1Shop {
	private final int _npcId;
	private final List<L1ShopItem> _sellingItems;
	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(int npcId, List<L1ShopItem> sellingItems,
			List<L1ShopItem> purchasingItems) {
		if (sellingItems == null || purchasingItems == null) {
			throw new NullPointerException();
		}

		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;
	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * 이 상점에서, 지정된 아이템이 매입 가능한 상태일까를 돌려준다.
	 * 
	 * @param item
	 * @return 아이템이 매입 가능하면 true
	 */
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // 장비중이면 불가
			return false;
		}
		if (item.getEnchantLevel() != 0) { // 강화(or약화)되고 있으면 불가
			return false;
		}

		return true;
	}

	private L1ShopItem getPurchasingItem(int itemId) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1ShopItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1ShopItem item) {
		return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item
				.getPackCount());
	}

	/**
	 * 목록내의 매입 가능 아이템을 사정한다.
	 * 
	 * @param inv
	 *            사정 대상의 목록
	 * @return 사정된 매입 가능 아이템의 리스트
	 */
	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}

				result.add(new L1AssessedItem(targetItem.getId(),
						getAssessedPrice(item)));
			}
		}
		return result;
	}

	/**
	 * 플레이어에 아이템을 판매할 수 있는 것을 보증한다.
	 * 
	 * @return 어떠한 이유로써 아이템을 판매할 수 없는 경우, false
	 */
	private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPriceTaxIncluded();
		// 오버플로우 체크
		if (!IntRange.includes(price, 0, 2000000000)) {
			// 총판 매가격은%d아데나를 초과할 수 없습니다.
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
		// 구입할 수 있을까 체크
		if (!pc.getInventory().checkItem(L1ItemId.ADENA, price)) {
			System.out.println(price);
			// \f1아데나가 부족합니다.
			pc.sendPackets(new S_ServerMessage(189));
			return false;
		}
		// 중량 체크
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
//		 ########## (버그 방지) 상점 버그 방지
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
            return false;
        }
		  //구매수량한계 by
		  int Count = 0;//구매수량
		  int new_price = 0;//구매 금액
		  int set_count = 0;//가능 구매수량
		  for (L1ShopBuyOrder order : orderList.getList()) {
		   L1Item temp = order.getItem().getItem();
		    Count += 1;
		    new_price += orderList.getTotalPrice();
		  }
		  set_count = 2000000000 / new_price;
		  if(Count > set_count) {
		   pc.sendPackets(new S_ServerMessage(936));
		   return false;
		  }
		  //구매수량한계 by end
		//(슈크림)버그 방지
// ########## (버그 방지) 상점 버그 방지
		return true;
	}

	/**
	 * 지역세 납세 처리 에덴성·디아드 요새를 제외한 성은 에덴성에 국세로 해서10% 납세한다
	 * 
	 * @param orderList
	 */
	private void payCastleTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		int price = orderList.getTotalPrice();

		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		int castleTax = calc.calcCastleTaxPrice(price);
		int nationalTax = calc.calcNationalTaxPrice(price);
		// 에덴성·디아드성의 경우는 국세 없음
		if (castleId == L1CastleLocation.ADEN_CASTLE_ID
				|| castleId == L1CastleLocation.DIAD_CASTLE_ID) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		if (castleId != 0 && castleTax > 0) {
			L1Castle castle = CastleTable.getInstance()
					.getCastleTable(castleId);

			synchronized (castle) {
				int money = castle.getPublicMoney();
				if (2000000000 > money) {
					money = money + castleTax;
					castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}

			if (nationalTax > 0) {
				L1Castle aden = CastleTable.getInstance().getCastleTable(
						L1CastleLocation.ADEN_CASTLE_ID);
				synchronized (aden) {
					int money = aden.getPublicMoney();
					if (2000000000 > money) {
						money = money + nationalTax;
						aden.setPublicMoney(money);
						CastleTable.getInstance().updateCastle(aden);
					}
				}
			}
		}
	}

	/**
	 * 디아드세 납세 처리 전쟁세의10%가 디아드 요새의 공금이 된다.
	 * 
	 * @param orderList
	 */
	private void payDiadTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		int price = orderList.getTotalPrice();

		// 디아드세
		int diadTax = calc.calcDiadTaxPrice(price);
		if (diadTax <= 0) {
			return;
		}

		L1Castle castle = CastleTable.getInstance().getCastleTable(
				L1CastleLocation.DIAD_CASTLE_ID);
		synchronized (castle) {
			int money = castle.getPublicMoney();
			if (2000000000 > money) {
				money = money + diadTax;
				castle.setPublicMoney(money);
				CastleTable.getInstance().updateCastle(castle);
			}
		}
	}

	/**
	 * 조세 납세 처리
	 * 
	 * @param orderList
	 */
	private void payTownTax(L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();

		// 마을의 매상
		if (!L1World.getInstance().isProcessingContributionTotal()) {
			int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if (town_id >= 1 && town_id <= 10) {
				TownTable.getInstance().addSalesMoney(town_id, price);
			}
		}
	}

	// XXX 납세 처리는 이 클래스의 책무는 아닌 생각이 들지만 우선
	private void payTax(L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		payTownTax(orderList);
		payDiadTax(orderList);
	}

	/**
	 * 판매 거래
	 */
	private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(L1ItemId.ADENA, orderList
				.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("구입에 필요한 아데나를 소비할 수 없었습니다.");
		}
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchantLevel =order.getItem().getEnchant();
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			if (_npcId == 70068 || _npcId == 70020) {
				item.setIdentified(false);
				Random random = new Random();
				int chance = random.nextInt(100) + 1;
				if (chance <= 15) {
					item.setEnchantLevel(-2);
				} else if (chance >= 16 && chance <= 30) {
					item.setEnchantLevel(-1);
				} else if (chance >= 31 && chance <= 70) {
					item.setEnchantLevel(0);
				} else if (chance >= 71 && chance <= 87) {
					item.setEnchantLevel(random.nextInt(2)+1);
				} else if (chance >= 88 && chance <= 97) {
					item.setEnchantLevel(random.nextInt(3)+3);
				} else if (chance >= 98 && chance <= 99) {
					item.setEnchantLevel(6);
				} else if (chance == 100) {
					item.setEnchantLevel(7);
				}
			} else {
				item.setEnchantLevel(enchantLevel);
			}
			item.setIdentified(true);
			inv.storeItem(item);
		}
	}

	/**
	 * 플레이어에, L1ShopBuyOrderList에 기재된 아이템을 판매한다.
	 * 
	 * @param pc
	 *            판매하는 플레이어
	 * @param orderList
	 *            판매해야 할 아이템이 기재된 L1ShopBuyOrderList
	 */
	public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		if(getNpcId()!= 200000 && getNpcId()!= 200001
				&& getNpcId()!= 200002 && getNpcId() != 200004){ //특정 npc가 아닌 상황(즉 프리미엄 상인이 아닌상황)프리미엄 상인 구현 
		if (!ensureSell(pc, orderList)) {
			return;
		}

		sellItems(pc.getInventory(), orderList);
		payTax(orderList);
	}
		else{	//프리미엄 상인인 경우
			if (!ensurePrimiumSell(pc, orderList)) {
			return; 
			} 
			sellPrimiumItems(pc.getInventory(), orderList); 
		} 
	}

	/**
	 * L1ShopSellOrderList에 기재된 아이템을 매입한다.
	 * 
	 * @param orderList
	 *            매입해야 할 아이템과 가격이 기재된 L1ShopSellOrderList
	 */
	public void buyItems(L1ShopSellOrderList orderList) {
		L1PcInventory inv = orderList.getPc().getInventory();
		int totalPrice = 0;
		for (L1ShopSellOrder order : orderList.getList()) {
			L1Object object = inv.getItem(order.getItem().getTargetId());
			L1ItemInstance item = (L1ItemInstance) object;
			// 봉인돼지 않았다면..
			if(item.getItem().getBless() < 128){
				int count = inv.removeItem(order.getItem().getTargetId(), order.getCount());
			totalPrice += order.getItem().getAssessedPrice() * count;
		}
		}

		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		
		//	** 상점 판매 비셔스 방어 **//   by 도우너
		if (totalPrice <= 0) {
			return;} 
		//** 상점 판매 비셔스 방어 **//   by 도우너	
		
		if (0 < totalPrice) {
			inv.storeItem(L1ItemId.ADENA, totalPrice);
		}
	}

	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}

// 프리미엄 상인 구현 
	//프리미엄 아이템을 사는 부분//
	private void sellPrimiumItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
		if (!inv.consumeItem(41159, orderList
				.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("구입에 필요한 신비한 깃털을 소비할 수 없었습니다.");
		}
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			item.setCount(amount);
			inv.storeItem(item);
		}
	}

	//프리미엄 상인으로 부터 아이템을 살수 있는지 체크//
	private boolean ensurePrimiumSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		// 오버플로우 체크
		if (!IntRange.includes(price, 0, 60000)) {
			// 총판 매가격은 프리미엄깃털 60000개를 초과할 수 없습니다.
			pc.sendPackets(new S_SystemMessage("신비한 날개깃털은 한번에 60000개 이상 사용할수 없습니다."));
			return false;
		}
		// 구입할 수 있을까 체크
		if (!pc.getInventory().checkItem(41159, price)) {
			//System.out.println(price);
			// \f1아데나가 부족합니다.
			pc.sendPackets(new S_SystemMessage("신비한 날개깃털이 부족합니다."));
			return false;
		}
		// 중량 체크
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 개수 체크
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (price <= 0 || price > 2000000000) {
			pc.sendPackets(new S_Disconnect());
            return false;
        }
		return true;
	}
}