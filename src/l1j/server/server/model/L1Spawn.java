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

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.L1GameTime;
import l1j.server.server.model.gametime.L1GameTimeAdapter;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1SpawnTime;
import l1j.server.server.types.Point;
import l1j.server.server.MessageController;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.datatables.ShopTable;

public class L1Spawn extends L1GameTimeAdapter {
	private static Logger _log = Logger.getLogger(L1Spawn.class.getName());
	private final L1Npc _template;

	private int _id; // just to find this in the spawn table
	private String _location;
	private int _maximumCount;
	private int _npcid;
	private int _groupId;
	private int _locx;
	private int _locy;
	private int _randomx;
	private int _randomy;
	private int _locx1;
	private int _locy1;
	private int _locx2;
	private int _locy2;
	private int _heading;
	private int _minRespawnDelay;
	private int _maxRespawnDelay;
	private short _mapid;
	private boolean _respaenScreen;
	private int _movementDistance;
	private boolean _rest;
	private int _spawnType;
	private int _delayInterval;
	private L1SpawnTime _time;
	private HashMap<Integer, Point> _homePoint = null; // init로 spawn 한 개개의 오브젝트의 홈 포인트
	private List<L1NpcInstance> _mobs = new ArrayList<L1NpcInstance>();

	private static Random _random = new Random();

	private String _name;

	private class SpawnTask implements Runnable {
		private int _spawnNumber;
		private int _objectId;

		private SpawnTask(int spawnNumber, int objectId) {
			_spawnNumber = spawnNumber;
			_objectId = objectId;
		}

		@Override
		public void run() {
			doSpawn(_spawnNumber, _objectId);
		}
	}

	public L1Spawn(L1Npc mobTemplate) {
		_template = mobTemplate;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public short getMapId() {
		return _mapid;
	}

	public void setMapId(short _mapid) {
		this._mapid = _mapid;
	}

	public boolean isRespawnScreen() {
		return _respaenScreen;
	}

	public void setRespawnScreen(boolean flag) {
		_respaenScreen = flag;
	}

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	public int getAmount() {
		return _maximumCount;
	}

	public int getGroupId() {
		return _groupId;
	}

	public int getId() {
		return _id;
	}

	public String getLocation() {
		return _location;
	}

	public int getLocX() {
		return _locx;
	}

	public int getLocY() {
		return _locy;
	}

	public int getNpcId() {
		return _npcid;
	}

	public int getHeading() {
		return _heading;
	}

	public int getRandomx() {
		return _randomx;
	}

	public int getRandomy() {
		return _randomy;
	}

	public int getLocX1() {
		return _locx1;
	}

	public int getLocY1() {
		return _locy1;
	}

	public int getLocX2() {
		return _locx2;
	}

	public int getLocY2() {
		return _locy2;
	}

	public int getMinRespawnDelay() {
		return _minRespawnDelay;
	}

	public int getMaxRespawnDelay() {
		return _maxRespawnDelay;
	}

	public void setAmount(int amount) {
		_maximumCount = amount;
	}

	public void setId(int id) {
		_id = id;
	}

	public void setGroupId(int i) {
		_groupId = i;
	}

	public void setLocation(String location) {
		_location = location;
	}

	public void setLocX(int locx) {
		_locx = locx;
	}

	public void setLocY(int locy) {
		_locy = locy;
	}

	public void setNpcid(int npcid) {
		_npcid = npcid;
	}

	public void setHeading(int heading) {
		_heading = heading;
	}

	public void setRandomx(int randomx) {
		_randomx = randomx;
	}

	public void setRandomy(int randomy) {
		_randomy = randomy;
	}

	public void setLocX1(int locx1) {
		_locx1 = locx1;
	}

	public void setLocY1(int locy1) {
		_locy1 = locy1;
	}

	public void setLocX2(int locx2) {
		_locx2 = locx2;
	}

	public void setLocY2(int locy2) {
		_locy2 = locy2;
	}

	public void setMinRespawnDelay(int i) {
		_minRespawnDelay = i;
	}

	public void setMaxRespawnDelay(int i) {
		_maxRespawnDelay = i;
	}

	private int calcRespawnDelay() {
		int respawnDelay = _minRespawnDelay * 1000;
		if (_delayInterval > 0) {
			respawnDelay += _random.nextInt(_delayInterval) * 1000;
		}
		L1GameTime currentTime = L1GameTimeClock.getInstance().currentTime();
		if (_time != null && ! _time.getTimePeriod().includes(currentTime)) { // 지정 시간외라면 지정 시간까지의 시간을 더한다
			long diff = (_time.getTimeStart(). getTime() - currentTime.toTime()
					.getTime());
			if (diff < 0) {
				diff += 24 * 1000L * 3600L;
			}
			diff /= 6; // real time to game time
			respawnDelay = (int) diff;
		}
		return respawnDelay;
	}

	/**
	 * SpawnTask를 기동한다.
	 * 
	 * @param spawnNumber
	 *            L1Spawn로 관리되고 있는 번호.홈 포인트가 없으면 무엇을 지정해도 좋다.
	 */
	public void executeSpawnTask(int spawnNumber, int objectId) {
		SpawnTask task = new SpawnTask(spawnNumber, objectId);
		GeneralThreadPool.getInstance().schedule(task, calcRespawnDelay());
	}

	private boolean _initSpawn = false;

	private boolean _spawnHomePoint;

	public void init() {
		if (_time != null && _time.isDeleteAtEndTime()) {
			// 시간외 삭제가 지정되고 있다면, 시간 경과의 통지를 받는다.
			L1GameTimeClock.getInstance().addListener(this);
		}
		_delayInterval = _maxRespawnDelay - _minRespawnDelay;
		_initSpawn = true;
		// 홈 포인트를 갖게할까
		if (Config.SPAWN_HOME_POINT
				&& Config.SPAWN_HOME_POINT_COUNT <= getAmount()
				&& Config.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay()
				&& isAreaSpawn()) {
			_spawnHomePoint = true;
			_homePoint = new HashMap<Integer, Point>();
		}

		int spawnNum = 0;
		while (spawnNum < _maximumCount) {
			// spawnNum는 1~maxmumCount까지
			doSpawn(++spawnNum);
		}
		_initSpawn = false;
	}

	/**
	 * 홈 포인트가 있는 경우는, spawnNumber를 기본으로 spawn 한다. 그 이외의 경우는, spawnNumber는 미사용.
	 */
	protected void doSpawn(int spawnNumber) { // 초기 배치
		// 지정 시간외이면, 다음 spawn를 예약해 끝난다.
		if (_time != null
				&& ! _time.getTimePeriod(). includes(
						L1GameTimeClock.getInstance().currentTime())) {
			executeSpawnTask(spawnNumber, 0);
			return;
		}
		doSpawn(spawnNumber, 0);   
		}  
	
	protected void doSpawn(int spawnNumber, int objectId) {  
		L1NpcInstance mob = null;
		try {
			int newlocx = getLocX();
			int newlocy = getLocY();
			int tryCount = 0;

			mob = NpcTable.getInstance(). newNpcInstance(_template);
			synchronized (_mobs) {
				_mobs.add(mob);
			}
			if (objectId == 0) {  
				mob.setId(IdFactory.getInstance().nextId());   
			   } else {   
				mob.setId(objectId); // 오브젝트 ID재이용
			} 
			
			if (0 <= getHeading() && getHeading() <= 7) {
				mob.setHeading(getHeading());
			} else {
				// heading치가 올바르지 않다
				mob.setHeading(5);
			}
			// 특정 shop !!
			L1ShopItem s = ShopTable.getInstance().getShop(mob.getNpcTemplate().get_npcId());
			if(s != null && s.getMessage() != null && s.getMessage().length > 0){
				MessageController.getInstance().add(mob);
			}

			int npcId = mob.getNpcTemplate().get_npcId();
			if (npcId == 45488 && getMapId() == 9) { // 앙금 파
				mob.setMap((short) (getMapId() + _random.nextInt(2)));
			} else if (npcId == 45601 && getMapId() == 11) { // 데스나이트
				mob.setMap((short) (getMapId() + _random.nextInt(3)));
			} else {
				mob.setMap(getMapId());
			}
			mob.setMovementDistance(getMovementDistance());
			mob.setRest(isRest());
			while (tryCount <= 50) {
				switch (getSpawnType()) {
				case SPAWN_TYPE_PC_AROUND: // PC주변에 솟아 오르는 타입
					if (!_initSpawn) { // 초기 배치에서는 무조건 통상 spawn
						ArrayList<L1PcInstance> players = new ArrayList<L1PcInstance>();
						for (L1PcInstance pc : L1World.getInstance()
								.getAllPlayers()) {
							if (getMapId() == pc.getMapId()) {
								players.add(pc);
							}
						}
						if (players.size() > 0) {
							L1PcInstance pc = players.get(_random
									.nextInt(players.size()));
							L1Location loc = pc.getLocation().randomLocation(
									PC_AROUND_DISTANCE, false);
							newlocx = loc.getX();
							newlocy = loc.getY();
							break;
						}
					}
					// 플로어에 PC가 없으면 통상의 출현 방법
				default:
					if (isAreaSpawn()) { // 좌표가 범위 지정되고 있는 경우
						Point pt = null;
						if (_spawnHomePoint
								&& null != (pt = _homePoint.get(spawnNumber))) { // 홈 포인트를 바탕으로 같은 글씨, 글귀가 다른 곳에도  나타내게 하는 경우
							L1Location loc = new L1Location(pt, getMapId())
									.randomLocation(
											Config.SPAWN_HOME_POINT_RANGE,
											false);
							newlocx = loc.getX();
							newlocy = loc.getY();
						} else {
							int rangeX = getLocX2() - getLocX1();
							int rangeY = getLocY2() - getLocY1();
							newlocx = _random.nextInt(rangeX) + getLocX1();
							newlocy = _random.nextInt(rangeY) + getLocY1();
						}
						if (tryCount > 49) { // 출현 위치가 정해지지 않을 때는 locx, locy의 값
							newlocx = getLocX();
							newlocy = getLocY();
						}
					} else if (isRandomSpawn()) { // 좌표의 랜덤치가 지정되고 있는 경우
						newlocx = (getLocX() + ((int) (Math.random() * getRandomx()) - (int) (Math
								.random() * getRandomx())));
						newlocy = (getLocY() + ((int) (Math.random() * getRandomy()) - (int) (Math
								.random() * getRandomy())));
					} else { // 어느쪽이나 지정되어 있지 않은 경우
						newlocx = getLocX();
						newlocy = getLocY();
					}
				}
				mob.setX(newlocx);
				mob.setHomeX(newlocx);
				mob.setY(newlocy);
				mob.setHomeY(newlocy);

				if (mob.getMap().isInMap(mob.getLocation())
						&& mob.getMap().isPassable(mob.getLocation())) {
					if (mob instanceof L1MonsterInstance) {
						if (isRespawnScreen()) {
							break;
						}
						L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
						if (L1World.getInstance().getVisiblePlayer(mobtemp)
								.size() == 0) {
							break;
						}
						// 화면내에 PC가 있어 출현할 수 없는 경우는, 3초 후에 스케줄링 해 주어 수선
						SpawnTask task = new SpawnTask(spawnNumber, mob.getId()); 
						GeneralThreadPool.getInstance().schedule(task, 3000L);
						return;
					}
				}
				tryCount++;
			}
			if (mob instanceof L1MonsterInstance) {
				((L1MonsterInstance) mob).initHide();
			}

			mob.setSpawn(this);
			mob.setreSpawn(true);
			mob.setSpawnNumber(spawnNumber); // L1Spawn에서의 관리 번호(홈 포인트에 사용)
			if (_initSpawn && _spawnHomePoint) { // 초기 배치로 홈 포인트를 설정
				Point pt = new Point(mob.getX(), mob.getY());
				_homePoint.put(spawnNumber, pt); // 여기서 보존한 point를 같은 글씨, 글귀가 다른 곳에도  지금에 사용한다
			}

			if (mob instanceof L1MonsterInstance) {
				if (mob.getMapId() == 666) {
					((L1MonsterInstance) mob).set_storeDroped(true);
				}
			}
			if (npcId == 45573 && mob.getMapId() == 2) { // 바포멧트
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					if (pc.getMapId() == 2) {
						L1Teleport.teleport(pc, 32664, 32797, (short) 2, 0, true);
					}
				}
			}
			
			if (npcId == 46142 && mob.getMapId() == 73
					|| npcId == 46141 && mob.getMapId() == 74) {
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					if (pc.getMapId() >= 72 && pc.getMapId() <= 74) {
						L1Teleport.teleport(pc, 32840, 32833, (short) 72,
								pc.getHeading(), true);
					}
				}
			}
			doCrystalCave(npcId);

			L1World.getInstance().storeObject(mob);
			L1World.getInstance().addVisibleObject(mob);

			if (mob instanceof L1MonsterInstance) {
				L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
				if (!_initSpawn && mobtemp.getHiddenStatus() == 0) {
					mobtemp.onNpcAI(); // monster의 AI를 개시
				}
			}
			if (getGroupId() != 0) {
				L1MobGroupSpawn.getInstance().doSpawn(mob, getGroupId(),
						isRespawnScreen(), _initSpawn);
			}
			 mob.turnOnOffLight();  
			mob.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public void setRest(boolean flag) {
		_rest = flag;
	}

	public boolean isRest() {
		return _rest;
	}

	private static final int SPAWN_TYPE_NORMAL = 0;
	private static final int SPAWN_TYPE_PC_AROUND = 1;

	private static final int PC_AROUND_DISTANCE = 30;

	private int getSpawnType() {
		return _spawnType;
	}

	public void setSpawnType(int type) {
		_spawnType = type;
	}

	private boolean isAreaSpawn() {
		return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0
				&& getLocY2() != 0;
	}

	private boolean isRandomSpawn() {
		return getRandomx() != 0 || getRandomy() != 0;
	}

	public L1SpawnTime getTime() {
		return _time;
	}

	public void setTime(L1SpawnTime time) {
		_time = time;
	}

	@Override
	public void onMinuteChanged(L1GameTime time) {
		if (_time.getTimePeriod(). includes(time)) {
			return;
		}
		synchronized (_mobs) {
			if (_mobs.isEmpty()) {
				return;
			}
			// 지정 시간외가 되어 있으면 삭제
			for (L1NpcInstance mob : _mobs) {
				mob.setCurrentHpDirect(0);
				mob.setDead(true);
				mob.setStatus(ActionCodes.ACTION_Die);
				mob.deleteMe();
			}
			_mobs.clear();
		}
	}
	public static void doCrystalCave(int npcId) {
		int[] npcId2 = { 46143, 46144, 46145, 46146, 46147,
				46148, 46149, 46150, 46151, 46152, 74001, 74000, 74002, 
				201024, 201025, 201026, 201027, 201028, 201029, 201030,
				201031, 201032, 201033, 201034, 201035, 201036, 201037, 
				201038, 201039, 201040, 201041, 201042, 201043, 201044,
				201045, 201046, 201047, 201048, 201049, 201050, 201051,
				201052, 201053, 201054, 201055, 201056, 201057, 201058, 
				201059, 201060, 201061, 201062, 201063, 201064, 201065,
				201066, 201067, 201068, 201069, 201070, 201071, 201072,
				201073, 201074, 46153, 46154, 46155, 46156, 46157, 
				46158, 46159, 46160};
				
		int[] doorId = { 5001, 5002, 5003, 5004, 5005, 5006,
				5007, 5008, 5009, 5010, 5100, 5101, 5102, 
				4005, 4006, 4007, 4008, 4009, 4010, 4011,
				4012, 4013, 4014, 4015, 4016, 4017, 4018,
				4019, 4020, 4021, 4022, 4023, 4024, 4025,
			    4026, 4027, 4028, 4029, 4030, 4031, 4032,
				4033, 4034, 4035, 4036, 4037, 4038, 4039,
		        4040, 4041, 4042, 4043, 4044, 4045, 4046,
				4047, 4048, 4049, 4050, 4051, 4052, 4053,
				4054, 4055, 5050, 5051, 5052, 5053, 5054, 
				5055, 5056, 5057};
		
		for (int i = 0; i < npcId2.length; i++) {
			if (npcId == npcId2[i]) {
				closeDoorInCrystalCave(doorId[i]);				
			}
		}
	}
	
	private static void closeDoorInCrystalCave(int doorId) {
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.close();
				}
			}
		}
	}
}