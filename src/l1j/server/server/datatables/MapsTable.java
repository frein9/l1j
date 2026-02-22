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
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public final class MapsTable {
	private class MapData {
		public int startX = 0;
		public int endX = 0;
		public int startY = 0;
		public int endY = 0;
		public double monster_amount = 1;
		public double dropRate = 1;
		public boolean isUnderwater = false;
		public boolean markable = false;
		public boolean teleportable = false;
		public boolean escapable = false;
		public boolean isUseResurrection = false;
		public boolean isUsePainwand = false;
		public boolean isEnabledDeathPenalty = false;
		public boolean isTakePets = false;
		public boolean isRecallPets = false;
		public boolean isUsableItem = false;
		public boolean isUsableSkill = false;
	}

	private static Logger _log = Logger.getLogger(MapsTable.class.getName());

	private static MapsTable _instance;

	/**
	 * Key에 맵 ID, Value에 텔레포트 가부 플래그가 격납되는 HashMap
	 */
	private final Map<Integer, MapData> _maps = new HashMap<Integer, MapData>();

	/**
	 * 새롭고 MapsTable 오브젝트를 생성해, 맵의 텔레포트 가부 플래그를 읽어들인다.
	 */
	private MapsTable() {
		loadMapsFromDatabase();
	}

	/**
	 * 맵의 텔레포트 가부 플래그를 데이타베이스로부터 읽어들여, HashMap _maps에 격납한다.
	 */
	private void loadMapsFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM mapids");

			for (rs = pstm.executeQuery(); rs.next();) {
				MapData data = new MapData();
				int mapId = rs.getInt("mapid");
				// rs.getString("locationname");
				data.startX = rs.getInt("startX");
				data.endX = rs.getInt("endX");
				data.startY = rs.getInt("startY");
				data.endY = rs.getInt("endY");
				data.monster_amount = rs.getDouble("monster_amount");
				data.dropRate = rs.getDouble("drop_rate");
				data.isUnderwater = rs.getBoolean("underwater");
				data.markable = rs.getBoolean("markable");
				data.teleportable = rs.getBoolean("teleportable");
				data.escapable = rs.getBoolean("escapable");
				data.isUseResurrection = rs.getBoolean("resurrection");
				data.isUsePainwand = rs.getBoolean("painwand");
				data.isEnabledDeathPenalty = rs.getBoolean("penalty");
				data.isTakePets = rs.getBoolean("take_pets");
				data.isRecallPets = rs.getBoolean("recall_pets");
				data.isUsableItem = rs.getBoolean("usable_item");
				data.isUsableSkill = rs.getBoolean("usable_skill");

				_maps.put(new Integer(mapId), data);
			}

			_log.config("Maps " + _maps.size());
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * MapsTable의 인스턴스를 돌려준다.
	 * 
	 * @return MapsTable의 인스턴스
	 */
	public static MapsTable getInstance() {
		if (_instance == null) {
			_instance = new MapsTable();
		}
		return _instance;
	}

	/**
	 * 맵이의 X개시 좌표를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return X개시 좌표
	 */
	public int getStartX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startX;
	}

	/**
	 * 맵이의 X종료 좌표를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return X종료 좌표
	 */
	public int getEndX(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endX;
	}

	/**
	 * 맵이의 Y개시 좌표를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return Y개시 좌표
	 */
	public int getStartY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).startY;
	}

	/**
	 * 맵이의 Y종료 좌표를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return Y종료 좌표
	 */
	public int getEndY(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).endY;
	}

	/**
	 * 맵의 monster량 배율을 돌려준다
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return monster량의 배율
	 */
	public double getMonsterAmount(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.monster_amount;
	}

	/**
	 * 맵의 드롭 배율을 돌려준다
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return 드롭 배율
	 */
	public double getDropRate(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return map.dropRate;
	}

	/**
	 * 맵이, 수중일까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 수중이면 true
	 */
	public boolean isUnderwater(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUnderwater;
	}

	/**
	 * 맵이, 북마크 가능한가를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return 북마크 가능하면 true
	 */
	public boolean isMarkable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).markable;
	}

	/**
	 * 맵이, 랜덤 텔레포트 가능한가를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return 가능하면 true
	 */
	public boolean isTeleportable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).teleportable;
	}

	/**
	 * 맵이, MAP를 넘은 텔레포트 가능한가를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * @return 가능하면 true
	 */
	public boolean isEscapable(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).escapable;
	}

	/**
	 * 맵이, 부활 가능한가를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 부활 가능하면 true
	 */
	public boolean isUseResurrection(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUseResurrection;
	}

	/**
	 * 맵이, 파인쥬스 wand 사용 가능한가를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 파인쥬스 wand 사용 가능하면 true
	 */
	public boolean isUsePainwand(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsePainwand;
	}

	/**
	 * 맵이, 데스페나르티가 있을까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 데스페나르티이면 true
	 */
	public boolean isEnabledDeathPenalty(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isEnabledDeathPenalty;
	}

	/**
	 * 맵이, 애완동물·사몬을 데리고 갈 수 있을까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 펫·사몬을 데리고 갈 수 있다면 true
	 */
	public boolean isTakePets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isTakePets;
	}

	/**
	 * 맵이, 애완동물·사몬을 호출할 수 있을까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 펫·사몬을 호출할 수 있다면 true
	 */
	public boolean isRecallPets(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isRecallPets;
	}

	/**
	 * 맵이, 아이템을 사용할 수 있을까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 아이템을 사용할 수 있다면 true
	 */
	public boolean isUsableItem(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableItem;
	}

	/**
	 * 맵이, 스킬을 사용할 수 있을까를 돌려준다.
	 * 
	 * @param mapId
	 *            조사하는 맵의 맵 ID
	 * 
	 * @return 스킬을 사용할 수 있다면 true
	 */
	public boolean isUsableSkill(int mapId) {
		MapData map = _maps.get(mapId);
		if (map == null) {
			return false;
		}
		return _maps.get(mapId).isUsableSkill;
	}

}
