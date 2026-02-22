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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Item;

public class L1Inventory extends L1Object {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1Inventory.class.getName());

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

	public static final int MAX_AMOUNT = 2000000000; // 2G

	public static final int MAX_WEIGHT = 1500;

	public L1Inventory() {
		//
	}

	// 목록내의 아이템의 총수
	public int getSize() {
		return _items.size();
	}

	// 목록내의 모든 아이템
	public List<L1ItemInstance> getItems() {
		return _items;
	}

	// 목록내의 총중량
	public int getWeight() {
		int weight = 0;

		for (L1ItemInstance item : _items) {
			weight += item.getWeight();
		}

		return weight;
	}

	// 인수의 아이템을 추가해도 용량과 중량이 괜찮은가 확인
	public static final int OK = 0;

	public static final int SIZE_OVER = 1;

	public static final int WEIGHT_OVER = 2;

	public static final int AMOUNT_OVER = 3;

	public int checkAddItem(L1ItemInstance item, int count) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0) {   
			return -1;  
		} 
		if (getSize() > Config.MAX_NPC_ITEM
				|| (getSize() == Config.MAX_NPC_ITEM && (! item.isStackable() || ! checkItem(item
						.getItem().getItemId())))) { // 용량 확인
			return SIZE_OVER;
		}

		int weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
		if (weight < 0 || (item.getItem().getWeight() * count / 1000) < 0) {   
			return WEIGHT_OVER;  
		} 
		if (weight > (MAX_WEIGHT * Config.RATE_WEIGHT_LIMIT_PET)) { // 그 외의 중량 확인(주로 사몬과 애완동물)
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > MAX_AMOUNT) {
			return AMOUNT_OVER;
		}

		return OK;
	}

	// 인수의 아이템을 추가해도 창고의 용량이 괜찮은가 확인
	public static final int WAREHOUSE_TYPE_PERSONAL = 0;

	public static final int WAREHOUSE_TYPE_CLAN = 1;

	public int checkAddItemToWarehouse(L1ItemInstance item, int count,
			int type) {
		if (item == null) {
			return -1;
		}
		if (item.getCount() <= 0 || count <= 0) {
			return -1;
		}
		int maxSize = 100;
		if (type == WAREHOUSE_TYPE_PERSONAL) {
			maxSize = Config.MAX_PERSONAL_WAREHOUSE_ITEM;
		} else if (type == WAREHOUSE_TYPE_CLAN) {
			maxSize = Config.MAX_CLAN_WAREHOUSE_ITEM;
		}
		if (getSize() > maxSize
				|| (getSize() == maxSize && (!item.isStackable()
						 || !checkItem(item.getItem().getItemId())))) { // 용량 확인
			return SIZE_OVER;
		}

		return OK;
	}

	// 새로운 아이템의 격납
	public synchronized L1ItemInstance storeItem(int id, int count) {
		if (count <= 0) {  
			return null;  
		} 
		L1Item temp = ItemTable.getInstance().getTemplate(id);
		if (temp == null) {
			return null;
		}

		if (temp.isStackable()) {
			L1ItemInstance item = new L1ItemInstance(temp, count);

			if (findItemId(id) == null) { // 새롭게 생성할 필요가 있는 경우만 ID의 발행과 L1World에의 등록을 실시한다
				item.setId(IdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(item);
			}

			return storeItem(item);
		}

		// 스택 할 수 없는 아이템의 경우
		L1ItemInstance result = null;
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = new L1ItemInstance(temp, 1);
			item.setId(IdFactory.getInstance().nextId());
			L1World.getInstance().storeObject(item);
			storeItem(item);
			result = item;
		}
		// 마지막에 만든 아이템을 돌려준다.배열을 되돌리도록(듯이) 메소드 정의를 변경하는 편이 좋을지도 모른다.
		return result;
	}

	// DROP, 구입, GM커멘드로 입수한 새로운 아이템의 격납
	public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
		if (item.getCount() <= 0) {   
			return null;  
		} 
		int itemId = item.getItem().getItemId();
		if (item.isStackable()) {
			L1ItemInstance findItem = findItemId(itemId);
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		int chargeCount = item.getItem().getMaxChargeCount();
		if (itemId == 40006 || itemId == 40007
				|| itemId == 40008 || itemId == 140006
				|| itemId == 140008 || itemId == 41401) {
			Random random = new Random();
			chargeCount -= random.nextInt(5);
		}
		if (itemId == 20383) {
			chargeCount = 50;
		}
		item.setChargeCount(chargeCount);
		if (item.getItem().getType2() == 0 && item.getItem().getType() == 2) { // light계 아이템
			item.setRemainingTime(item.getItem().getLightFuel());
		} else {
			item.setRemainingTime(item.getItem().getMaxUseTime());
		}
		_items.add(item);
		insertItem(item);
		return item;
	}

	// /trade, 창고로부터 입수한 아이템의 격납
	public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
		if (item.isStackable()) {
			L1ItemInstance findItem = findItemId(item.getItem().getItemId());
			if (findItem != null) {
				findItem.setCount(findItem.getCount() + item.getCount());
				updateItem(findItem);
				return findItem;
			}
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		_items.add(item);
		insertItem(item);
		return item;
	}

	/**
	 * 목록으로부터 지정된 아이템 ID의 아이템을 삭제한다.L1ItemInstance에의 참조
	 * (이)가 있는 경우는 removeItem의 (분)편을 사용하는 것이 좋다. (이쪽은 화살이라든지 마석이라든지 특정의 아이템을 소비시킬 때 사용한다)
	 * 
	 * @param itemid -
	 *            삭제하는 아이템의 itemid(objid는 아니다)
	 * @param count -
	 *            삭제하는 개수
	 * @return 실제로 삭제되었을 경우는 true를 돌려준다.
	 */
	public boolean consumeItem(int itemid, int count) {
		if (count <= 0) {   
			return false;   
		} 
		if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
			L1ItemInstance item = findItemId(itemid);
			if (item != null && item.getCount() >= count) {
				removeItem(item, count);
				return true;
			}
		} else {
			L1ItemInstance[] itemList = findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			} else if (itemList.length > count) { // 지정 개수보다 많이 소지하고 있는 경우
				DataComparator dc = new DataComparator();
				Arrays.sort(itemList, dc); // 엔챤트순서에 소트 해, 엔챤트수의 적은 것으로부터 소비시킨다
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	public class DataComparator implements java.util.Comparator {
		public int compare(Object item1, Object item2) {
			return ((L1ItemInstance) item1).getEnchantLevel()
					- ((L1ItemInstance) item2).getEnchantLevel();
		}
	}

	// 지정한 아이템으로부터 지정 개수를 삭제(사용하거나 쓰레기통에 버려졌을 때) 반환값：실제로 삭제한 수
	public int removeItem(int objectId, int count) {
		L1ItemInstance item = getItem(objectId);
		return removeItem(item, count);
	}

	public int removeItem(L1ItemInstance item) {
		return removeItem(item, item.getCount());
	}

	public int removeItem(L1ItemInstance item, int count) {
		if (item == null) {
			return 0;
		}
		if (item.getCount() <= 0 || count <= 0) {  
			return 0;   
		} 
		if (item.getCount() < count) {
			count = item.getCount();
		}
		if (item.getCount() == count) {
			int itemId = item.getItem().getItemId();
			if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
				PetTable.getInstance().deletePet(item.getId());
			} else if (itemId >= 49016 && itemId <= 49025) { // 편지지
				LetterTable lettertable = new LetterTable();
				lettertable.deleteLetter(item.getId());
			} else if (itemId >= 41383 && itemId <= 41400) { // 가구
				for (L1Object l1object : L1World.getInstance().getObject()) {
					if (l1object instanceof L1FurnitureInstance) {
						L1FurnitureInstance furniture = (L1FurnitureInstance) l1object;
						if (furniture.getItemObjId() == item.getId()) { // 이미 꺼내고 있는 가구
							FurnitureSpawnTable.getInstance(). deleteFurniture(
									furniture);
						}
					}
				}
			}
			deleteItem(item);
			L1World.getInstance().removeObject(item);
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
		}
		return count;
	}

	// _items로부터 지정 오브젝트를 삭제(L1PcInstance, L1DwarfInstance, L1GroundInstance로 이 부분을 오바라이드 한다)
	public void deleteItem(L1ItemInstance item) {
		_items.remove(item);
	}

	// 인수의 목록에 아이템을 이양
	public synchronized L1ItemInstance tradeItem(int objectId, int count,
			L1Inventory inventory) {
		L1ItemInstance item = getItem(objectId);
		return tradeItem(item, count, inventory);
	}

	public synchronized L1ItemInstance tradeItem(L1ItemInstance item,
			int count, L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if (item.getCount() <= 0 || count <= 0) {   
			return null;   
		} 
		if (item.isEquipped()) {
			return null;
		}
		if (!checkItem(item.getItem().getItemId(), count)) {
			return null;
		}
		L1ItemInstance carryItem;
//		if (item.getCount() <= count) {
        if (item.getCount() <= count || count <= 0) { //** 엔진 복사 방어 **// 
			deleteItem(item);
			carryItem = item;
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
			carryItem = ItemTable.getInstance().createItem(
					item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			carryItem.setBless(item.getBless());
			carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
			carryItem.setUpacse(item.getUpacse());
		}
		return inventory.storeTradeItem(carryItem);
	}

	/*
	 * 아이템을 손상·손모시키는(무기·방어용 기구도 포함한다) 아이템의 경우, 손모이므로 마이너스 하지만 무기·방어용 기구는 손상도를 나타내므로 플러스로 한다.
	 */
	public L1ItemInstance receiveDamage(int objectId) {
		L1ItemInstance item = getItem(objectId);
		return receiveDamage(item);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item) {
		return receiveDamage(item, 1);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
		int itemType = item.getItem().getType2();
		int currentDurability = item.get_durability();

		if (item == null) {
			return null;
		}

		if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
			item.set_durability(0);
			return null;
		}

		// 무기·방어용 기구만 손상도를 플러스
		if (itemType == 0) {
			int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.set_durability(durability);
			}
		} else {
			int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				item.set_durability(durability);
			}
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(L1ItemInstance item) {
		int itemType = item.getItem().getType2();
		int durability = item.get_durability();

		if (item == null) {
			return null;
		}

		if ((durability == 0 && itemType != 0) || durability < 0) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			// 내구도를 플러스 하고 있다.
			item.set_durability(durability + 1);
		} else {
			// 손상도를 마이너스 하고 있다.
			item.set_durability(durability - 1);
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	// 아이템 ID로부터 검색
	public L1ItemInstance findItemId(int id) {
		for (L1ItemInstance item : _items) {
			if (item.getItem().getItemId() == id) {
				return item;
			}
		}
		return null;
	}

	public L1ItemInstance[] findItemsId(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				itemList.add(item);
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	public L1ItemInstance[] findItemsIdNotEquipped(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	// 오브젝트 ID로부터 검색
	public L1ItemInstance getItem(int objectId) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}
	public L1ItemInstance 아이템찾기(String NameID){
		  for (Object itemObject : _items) {
		   L1ItemInstance item = (L1ItemInstance) itemObject;
		   if(item.getItem().getNameId().equalsIgnoreCase(NameID)){
		    return item;
		   }
		  }

		  return null;
		 } 
	
	// 인첸트아이템 삭제 // 시즌 // 조우의 돌골렘
	  public boolean deleteEnchant(int itemid, int enchantLevel) {
	    L1ItemInstance item = findItemId(itemid);
	    if (item != null && item.getEnchantLevel() == enchantLevel) {
	     removeItem(item, 1);
	     return true;
	   }
	   return false;
	  }

	// 인첸트 검사// 시즌 // 조우의 돌골렘
	  public boolean checkEnchant(int id, int enchantLevel) {
	   L1ItemInstance item = findItemId(id);
	   if (item != null && item.getEnchantLevel() == enchantLevel
	     && item.getCount() >= 1) {  // 아이템이 1개만 있으면 되길래 >= 으로 바꿔줘서 여러개 있어도 가능하게
	    return true;
	   }
	   return false;
	  }

	// 특정의 아이템이 지정된 개수 이상 소지하고 있을까 확인(화살이라든지 마석의 확인)
	public boolean checkItem(int id) {
		return checkItem(id, 1);
	}

	public boolean checkItem(int id, int count) {
		if (count == 0) {
			return true;
		}
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item !=null && item.getCount() >= count) {
				return true;
			}
		} else {
			Object[] itemList = findItemsId(id);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}

	// 특정의 아이템이 지정된 개수 이상 소지하고 있을까 확인
	// 장비중의 아이템은 소지하고 있지 않으면 판별한다
	public boolean checkItemNotEquipped(int id, int count) {
		if (count == 0) {
			return true;
		}
		return count <= countItems(id);
	}

	// 특정의 아이템을 모두 필요한 개수 소지하고 있을까 확인(이벤트등으로 복수의 아이템을 소지하고 있을까 확인하기 위해(때문에))
	public boolean checkItem(int[] ids) {
		int len = ids.length;
		int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return checkItem(ids, counts);
	}

	public boolean checkItem(int[] ids, int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 이 목록내에 있는, 지정된 ID의 아이템의 수를 센다.
	 * 
	 * @return
	 */
	public int countItems(int id) {
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null) {
				return item.getCount();
			}
		} else {
			Object[] itemList = findItemsIdNotEquipped(id);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(_items);
	}

	// 목록내의 모든 아이템을 지운다(소유자를 지울 때 등)
	public void clearItems() {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			L1World.getInstance().removeObject(item);
		}
		_items.clear();
	}

	// 오버라이드(override)용
	public void loadItems() {
	}

	public void insertItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item, int colmn) {
	}

}
