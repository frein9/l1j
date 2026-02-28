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
package l1j.server.server.templates;

import l1j.server.server.datatables.ItemTable;

public class L1ShopItem {
    private static final long serialVersionUID = 1L;

    private final int _itemId;

    private final L1Item _item;

    private final int _price;

    private final int _packCount;
    private final int _Enchant;

    private final String[] _Message;

    public L1ShopItem(int itemId, int price, int packCount, int Enchant) {
        _itemId = itemId;
        _item = ItemTable.getInstance().getTemplate(itemId);
        _price = price;
        _packCount = packCount;
        _Enchant = Enchant;
        _Message = null;
    }

    public L1ShopItem(int itemId, int price, int packCount, int Enchant, String... Message) {
        _itemId = itemId;
        _item = ItemTable.getInstance().getTemplate(itemId);
        _price = price;
        _packCount = packCount;
        _Enchant = Enchant;
        _Message = Message;
    }

    public int getItemId() {
        return _itemId;
    }

    public L1Item getItem() {
        return _item;
    }

    public int getPrice() {
        return _price;
    }

    public int getPackCount() {
        return _packCount;
    }

    public int getEnchant() {
        return _Enchant;
    }

    // 멘트
    public String[] getMessage() {
        return _Message;
    }
}