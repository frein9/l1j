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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.SQLUtil;

public class ShopTable {

    private static final long serialVersionUID = 1L;

    private static Logger _log = Logger.getLogger(ShopTable.class.getName());

    private static ShopTable _instance;

    private final Map<Integer, L1Shop> _allShops = new HashMap<Integer, L1Shop>();

    private final Map<Integer, L1ShopItem> shops = new HashMap<Integer, L1ShopItem>();

    public static ShopTable getInstance() {
        if (_instance == null) {
            _instance = new ShopTable();
        }
        return _instance;
    }

    public static void reload() {
        ShopTable oldInstance = _instance;
        _instance = new ShopTable();
        oldInstance._allShops.clear();
    }

    private ShopTable() {
        loadShops();
    }

    private ArrayList<Integer> enumNpcIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT DISTINCT NPC_ID FROM SHOP");
            rs = pstm.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("npc_id"));
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
        return ids;
    }


    /**
     * 멘트 추출
     *
     * @param npcId 번호
     * @return 멘트
     */
    private String getMessage(int npcId) {
        String sData = "";
        ResultSet r = null;
        Connection c = null;
        PreparedStatement p = null;
        try {
            c = L1DatabaseFactory.getInstance().getConnection();
            p = c.prepareStatement("SELECT GROUP_CONCAT(MESSAGE) AS MSG FROM SHOP WHERE NPC_ID=? GROUP BY NPC_ID");
            p.setInt(1, npcId);
            r = p.executeQuery();
            if (r.next()) {
                sData = r.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLUtil.close(r);
            SQLUtil.close(p);
            SQLUtil.close(c);
        }
        return sData;
    }

    private L1Shop loadShop(int npcId, ResultSet rs) throws SQLException {
        List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
        List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
        while (rs.next()) {
            int itemId = rs.getInt("item_id");
            int sellingPrice = rs.getInt("selling_price");
            int purchasingPrice = rs.getInt("purchasing_price");
            int packCount = rs.getInt("pack_count");
            int Enchant = rs.getInt("Enchant");
            String Message = getMessage(npcId);
            String[] sData = null;
            if (Message != null) {
                StringTokenizer s = new StringTokenizer(Message, ",");
                sData = new String[s.countTokens()];
                int i = 0;
                while (s.hasMoreTokens()) {
                    sData[i++] = s.nextToken();
                }
            }
            packCount = packCount == 0 ? 1 : packCount;
            if (0 <= sellingPrice) {
                L1ShopItem item = new L1ShopItem(itemId, sellingPrice, packCount, Enchant, sData);
                sellingList.add(item);
                shops.put(npcId, item);
            }
            if (0 <= purchasingPrice) {
                L1ShopItem item = new L1ShopItem(itemId, purchasingPrice, packCount, Enchant, sData);
                purchasingList.add(item);
                shops.put(npcId, item);
            }
        }
        return new L1Shop(npcId, sellingList, purchasingList);
    }

    /*버경 관련*/
    public void addShop(int npcId, L1Shop shop) {
        _allShops.put(npcId, shop);
    }

    public void delShop(int npcId) {
        _allShops.remove(npcId);
    }

    private void loadShops() {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT * FROM SHOP WHERE NPC_ID=? ORDER BY ORDER_ID");
            for (int npcId : enumNpcIds()) {
                pstm.setInt(1, npcId);
                rs = pstm.executeQuery();
                L1Shop shop = loadShop(npcId, rs);
                _allShops.put(npcId, shop);
                rs.close();
            }
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs, pstm, con);
        }
    }

    public L1Shop get(int npcId) {
        return _allShops.get(npcId);
    }

    public L1ShopItem getShop(int npcId) {
        return shops.get(npcId);
    }
}