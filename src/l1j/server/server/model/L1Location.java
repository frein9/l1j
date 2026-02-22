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

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.types.Point;

public class L1Location extends Point {
	private static Logger _log = Logger.getLogger(L1Location.class.getName());
	private static Random _random = new Random();
	protected L1Map _map = L1Map.newNull();

	public L1Location() {
		super();
	}

	public L1Location(L1Location loc) {
		this(loc._x, loc._y, loc._map);
	}

	public L1Location(int x, int y, int mapId) {
		super(x, y);
		setMap(mapId);
	}

	public L1Location(int x, int y, L1Map map) {
		super(x, y);
		_map = map;
	}

	public L1Location(Point pt, int mapId) {
		super(pt);
		setMap(mapId);
	}

	public L1Location(Point pt, L1Map map) {
		super(pt);
		_map = map;
	}

	public void set(L1Location loc) {
		_map = loc._map;
		_x = loc._x;
		_y = loc._y;
	}

	public void set(int x, int y, int mapId) {
		set(x, y);
		setMap(mapId);
	}

	public void set(int x, int y, L1Map map) {
		set(x, y);
		_map = map;
	}

	public void set(Point pt, int mapId) {
		set(pt);
		setMap(mapId);
	}

	public void set(Point pt, L1Map map) {
		set(pt);
		_map = map;
	}

	public L1Map getMap() {
		return _map;
	}

	public int getMapId() {
		return _map.getId();
	}

	public void setMap(L1Map map) {
		_map = map;
	}

	public void setMap(int mapId) {
		_map = L1WorldMap.getInstance().getMap((short) mapId);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof L1Location)) {
			return false;
		}
		L1Location loc = (L1Location) obj;
		return (this.getMap() == loc.getMap()) && (this.getX() == loc.getX())
				&& (this.getY() == loc.getY());
	}

	@Override
	public int hashCode() {
		return 7 * _map.getId() + super.hashCode();
	}

	@Override
	public String toString() {
		return String.format("(%d, %d) on %d", _x, _y, _map.getId());
	}

	/**
	 * 이 Location에 대한, 이동 가능한 랜덤 범위의 Location를 돌려준다.
	 * 랜덤 텔레포트의 경우는, 성에리어, 아지트내의 Location는 반환되지 않는다.
	 * 
	 * @param max
	 *            랜덤 범위의 최대치
	 * @param isRandomTeleport
	 *            랜덤 텔레포트인가
	 * @return 새로운 Location
	 */
	public L1Location randomLocation(int max, boolean isRandomTeleport) {
		return randomLocation(0, max, isRandomTeleport);
	}

	/**
	 * 이 Location에 대한, 이동 가능한 랜덤 범위의 Location를 돌려준다.
	 * 랜덤 텔레포트의 경우는, 성에리어, 아지트내의 Location는 반환되지 않는다.
	 * 
	 * @param min
	 *            랜덤 범위의 최소치(0으로 자신의 좌표를 포함한다)
	 * @param max
	 *            랜덤 범위의 최대치
	 * @param isRandomTeleport
	 *            랜덤 텔레포트인가
	 * @return 새로운 Location
	 */
	public L1Location randomLocation(int min, int max, boolean isRandomTeleport) {
		return L1Location.randomLocation(this, min, max, isRandomTeleport);
	}

	/**
	 * 인수의 Location에 대해서, 이동 가능한 랜덤 범위의 Location를 돌려준다.
	 * 랜덤 텔레포트의 경우는, 성에리어, 아지트내의 Location는 반환되지 않는다.
	 * 
	 * @param baseLocation
	 *            랜덤 범위의 바탕으로 되는 Location
	 * @param min
	 *            랜덤 범위의 최소치(0으로 자신의 좌표를 포함한다)
	 * @param max
	 *            랜덤 범위의 최대치
	 * @param isRandomTeleport
	 *            랜덤 텔레포트인가
	 * @return 새로운 Location
	 */
	public static L1Location randomLocation(L1Location baseLocation, int min,
			int max, boolean isRandomTeleport) {
		if (min > max) {
			throw new IllegalArgumentException("min > max가 되는 인수는 무효");
		}
		if (max <= 0) {
			return new L1Location(baseLocation);
		}
		if (min < 0) {
			min = 0;
		}

		L1Location newLocation = new L1Location();
		int newX = 0;
		int newY = 0;
		int locX = baseLocation.getX();
		int locY = baseLocation.getY();
		short mapId = (short) baseLocation.getMapId();
		L1Map map = baseLocation.getMap();

		newLocation.setMap(map);

		int locX1 = locX - max;
		int locX2 = locX + max;
		int locY1 = locY - max;
		int locY2 = locY + max;

		// map 범위
		int mapX1 = map.getX();
		int mapX2 = mapX1 + map.getWidth();
		int mapY1 = map.getY();
		int mapY2 = mapY1 + map.getHeight();

		// 최대에서도 맵의 범위내까지 보정
		if (locX1 < mapX1) {
			locX1 = mapX1;
		}
		if (locX2 > mapX2) {
			locX2 = mapX2;
		}
		if (locY1 < mapY1) {
			locY1 = mapY1;
		}
		if (locY2 > mapY2) {
			locY2 = mapY2;
		}

		int diffX = locX2 - locX1; // x방향
		int diffY = locY2 - locY1; // y방향

		int trial = 0;
		// 시행 회수를 범위 최소치에 의해 주기 때문에(위해)의 계산
		int amax = (int) Math.pow(1 + (max * 2), 2);
		int amin = (min == 0) ?  0 : (int) Math.pow(1 + ((min - 1) * 2), 2);
		int trialLimit = 40 * amax / (amax - amin);

		while (true) {
			if (trial >= trialLimit) {
				newLocation.set(locX, locY);
				break;
			}
			trial++;

			newX = locX1 + L1Location._random.nextInt(diffX + 1);
			newY = locY1 + L1Location._random.nextInt(diffY + 1);

			newLocation.set(newX, newY);

			if (baseLocation.getTileLineDistance(newLocation) < min) {
				continue;

			}
			if (isRandomTeleport) { // 랜덤 텔레포트의 경우
				if (L1CastleLocation.checkInAllWarArea(newX, newY, mapId)) { // 몇개의 성에리어
					continue;
				}

				// 몇개의 아지트내
				if (L1HouseLocation.isInHouse(newX, newY, mapId)) {
					continue;
				}
			}

			if (map.isInMap(newX, newY) && map.isPassable(newX, newY)) {
				break;
			}
		}
		return newLocation;
	}
}
