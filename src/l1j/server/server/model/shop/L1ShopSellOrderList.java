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
package l1j.server.server.model.shop;

import java.util.ArrayList;
import java.util.List;

import l1j.server.server.model.Instance.L1PcInstance;

//** 버그쟁이 처단 **//	By 도우너
import l1j.server.server.BugKick;	
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
//** 버그쟁이 처단 **//	By 도우너

class L1ShopSellOrder {
	private final L1AssessedItem _item;
	private final int _count;

	public L1ShopSellOrder(L1AssessedItem item, int count) {
		_item = item;
		_count = count;
	}

	public L1AssessedItem getItem() {
		return _item;
	}

	public int getCount() {
		return _count;
	}

}

public class L1ShopSellOrderList {
	private final L1Shop _shop;
	private final L1PcInstance _pc;
	private final List<L1ShopSellOrder> _list = new ArrayList<L1ShopSellOrder>();
	private int bugok  = 0;	//** 상점 판매 비셔스 방어 **//  by 도우너	

	L1ShopSellOrderList(L1Shop shop, L1PcInstance pc) {
		_shop = shop;
		_pc = pc;
	}

	public void add(int itemObjectId, int count, L1PcInstance pc) {
			
		//** 상점 판매 비셔스 방어 **///   by 도우너	
		L1ItemInstance item;
		item = pc.getInventory().getItem(itemObjectId);	
		
		if (itemObjectId != item.getId()) {
			 bugok =1;	  
			 return;}
		 
        int itemType = item.getItem().getType2(); 
        
        if ((itemType == 1 && count != 1) || (itemType ==2 && count != 1)){
		      bugok =1;
			  return;
		}  
		  
		  if ( item.getCount() < 0 || item.getCount() < count) {
		    	 bugok =1;
			     return;
		  }	
		  
		  if (count <= 0 ){		  
				BugKick.getInstance().KickPlayer(pc);
		    	bugok =1;
				return;
		  }	
		  
	 	  if (item.getLockitem() > 100){
              pc.sendPackets(new S_SystemMessage("봉인된 아이템은 판매할 수 없습니다."));
              return;
          }
		  		  
		  if (count > 50000 ) {
		       pc.sendPackets(new S_SystemMessage("5만개 이상은 판매하지 못합니다."));			  
			   return;
		  }	
		//** 상점 판매 비셔스 방어 **//   by 도우너		
		
		L1AssessedItem assessedItem = _shop.assessItem(_pc.getInventory()
				.getItem(itemObjectId));
		
		
		if (assessedItem == null) {
			/*
			 * 매입 리스트에 없는 아이템이 지정되었다. 부정 패키지의 가능성.
			 */
			throw new IllegalArgumentException();
		}
		_list.add(new L1ShopSellOrder(assessedItem, count));
		
	}
	
	//** 상점 판매 비셔스 방어 **//  by 도우너	
	public int BugOk() {
		return bugok;
	}	
	//** 상점 판매 비셔스 방어 **//  by 도우너	

	L1PcInstance getPc() {
		return _pc;
	}

	List<L1ShopSellOrder> getList() {
		return _list;
	}
}
