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

package l1j.server.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.IdFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.templates.L1BookMark; 
import l1j.server.server.serverpackets.S_Bookmarks;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class Beginner {

	private static Logger _log = Logger.getLogger(Beginner.class.getName());

	private static Beginner _instance;

	public static Beginner getInstance() {
		if (_instance == null) {
			_instance = new Beginner();
		}
		return _instance;
	}

	private Beginner() {
	}
	public void addBaseBookMarks(L1PcInstance pc) {
		  Connection con = null;
		  PreparedStatement pstm = null;
		  PreparedStatement pstm2 = null;
		  ResultSet rs = null;
		  L1BookMark bookmark = new L1BookMark();
		  
		  String name;
		  int x,y;
		  short m;
		  try {
		    con = L1DatabaseFactory.getInstance().getConnection();
		    //begin_teleport테이블에서 데이타 읽기
		    pstm = con.prepareStatement("SELECT * FROM begin_teleport");
		    //character_teleport테이블에 삽입하기
		    pstm2 = con.prepareStatement("INSERT INTO character_teleport SET id = ?, char_id = ?, name = ?, locx = ?, locy = ?, mapid = ?");
		    rs = pstm.executeQuery();

		    while(rs.next()){
		     name = rs.getString(1);
		     x = rs.getInt(2);
		     y = rs.getInt(3);
		     m = rs.getShort(4);
		     
		     bookmark.setId(IdFactory.getInstance().nextId());
		     bookmark.setCharId(pc.getId());
		     bookmark.setName(name);
		     bookmark.setLocX(x);
		     bookmark.setLocY(y);
		     bookmark.setMapId(m);
		     
		     pstm2.setInt(1, bookmark.getId());
		     pstm2.setInt(2, bookmark.getCharId());
		     pstm2.setString(3, name);
		     pstm2.setInt(4, x);
		     pstm2.setInt(5, y);
		     pstm2.setInt(6, m);
		     pstm2.execute();

		     pc.addBookMark(bookmark);
		     pc.sendPackets(new S_Bookmarks(name, m, bookmark.getId()));
		    }
		  } catch (SQLException e2) {
		   _log.log(Level.SEVERE, e2.getLocalizedMessage(), e2);
		  } finally {
		   SQLUtil.close(rs);
		   SQLUtil.close(pstm);
		   SQLUtil.close(pstm2);
		   SQLUtil.close(con);
		  }
		 }

	public int GiveItem(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con
					.prepareStatement("SELECT * FROM beginner WHERE activate IN(?,?)");

			pstm1.setString(1, "A");
			if (pc.isCrown()) {
				pstm1.setString(2, "P");
			} else if (pc.isKnight()) {
				pstm1.setString(2, "K");
			} else if (pc.isElf()) {
				pstm1.setString(2, "E");
			} else if (pc.isWizard()) {
				pstm1.setString(2, "W");
			} else if (pc.isDarkelf()) {
				pstm1.setString(2, "D");
			} else if (pc.isDragonKnight()) {
				pstm1.setString(2, "N"); 
			} else if (pc.isBlackWizard()) {
				pstm1.setString(2, "B");
			} else {
				pstm1.setString(2, "A");// 만일 어떤 것도 아니었던 경우의 에러 회피용
			}
			rs = pstm1.executeQuery();

			while (rs.next()) {
				PreparedStatement pstm2 = null;
				try {
					pstm2 = con
							.prepareStatement("INSERT INTO character_items SET id=?, item_id=?, char_id=?, item_name=?, count=?, is_equipped=?, enchantlvl=?, is_id=?, durability=?, charge_count=? ");
					pstm2.setInt(1, IdFactory.getInstance().nextId());
					pstm2.setInt(2, rs.getInt("item_id"));
					pstm2.setInt(3, pc.getId());
					pstm2.setString(4, rs.getString("item_name"));
					pstm2.setInt(5, rs.getInt("count"));
					pstm2.setInt(6, 0);
					pstm2.setInt(7, rs.getInt("enchantlvl"));
					pstm2.setInt(8, 0);
					pstm2.setInt(9, 0);
					pstm2.setInt(10, rs.getInt("charge_count"));
					pstm2.execute();
				} catch (SQLException e2) {
					_log.log(Level.SEVERE, e2.getLocalizedMessage(), e2);
				} finally {
					SQLUtil.close(pstm2);
				}
			}
		} catch (SQLException e1) {
			_log.log(Level.SEVERE, e1.getLocalizedMessage(), e1);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(con);
		}
		return 0;
	}
}