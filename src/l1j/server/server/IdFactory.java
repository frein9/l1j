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
package l1j.server.server;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IdFactory {
    private static final int FIRST_ID = 0x10000000;
    private static Logger _log = Logger.getLogger(IdFactory.class.getName());
    private static IdFactory _instance = new IdFactory();
    private int _curId;
    private Object _monitor = new Object();

    private IdFactory() {
        loadState();
    }

    public static IdFactory getInstance() {
        return _instance;
    }

    public int nextId() {
        synchronized (_monitor) {
            return _curId++;
        }
    }

    private void loadState() {
        // DB로부터 MAXID를 요구한다
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("SELECT MAX(ID)+1 AS NEXTID FROM (SELECT ID FROM CHARACTER_ITEMS UNION ALL SELECT ID FROM CHARACTER_TELEPORT UNION ALL SELECT ID FROM CHARACTER_WAREHOUSE UNION ALL SELECT ID FROM CHARACTER_ELF_WAREHOUSE UNION ALL SELECT OBJID AS ID FROM CHARACTERS UNION ALL SELECT CLAN_ID AS ID FROM CLAN_DATA UNION ALL SELECT ID FROM CLAN_WAREHOUSE UNION ALL SELECT OBJID AS ID FROM PETS) T");
            rs = pstm.executeQuery();

            int id = 0;
            if (rs.next()) {
                id = rs.getInt("nextid");
            }
            if (id < FIRST_ID) {
                id = FIRST_ID;
            }
            _curId = id;
            _log.info("Object ID: " + _curId);
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }
}
