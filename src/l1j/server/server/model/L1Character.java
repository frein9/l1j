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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillTimer;
import l1j.server.server.model.skill.L1SkillTimerCreator;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;
import l1j.server.server.utils.IntRange;

// Referenced classes of package l1j.server.server.model:
// L1Object, Die, L1PcInstance, L1MonsterInstance,
// L1World, ActionFailed

public class L1Character extends L1Object {

	private static final long serialVersionUID = 1L;

	private static final Logger _log = Logger.getLogger(L1Character.class
			.getName());

	private L1Poison _poison = null;
	private boolean _paralyzed;
	private boolean _sleeped;

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();
	private final Map<Integer, L1SkillTimer> _skillEffect = new HashMap<Integer, L1SkillTimer>();
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();
	private final Map<Integer, L1FollowerInstance> _followerlist = new HashMap<Integer, L1FollowerInstance>();

	public L1Character() {
		_level = 1;
	}

	/**
	 * 캐릭터를 부활시킨다.
	 * 
	 * @param hp
	 *            부활 후의 HP
	 */
	public void resurrect(int hp) {
		if (!isDead()) {
			return;
		}
		if (hp <= 0) {
			hp = 1;
		}
		setCurrentHp(hp);
		setDead(false);
		setStatus(0);
		L1PolyMorph.undoPoly(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
			pc.updateObject();
		}
	}

	private int _currentHp;

	/**
	 * 캐릭터의 현재의 HP를 돌려준다.
	 * 
	 * @return 현재의 HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}
	/**
	 * 캐릭터의 HP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 HP
	 */
	// 특수한 처리가 있는 경우는 여기를 오바라이드(패킷 송신등 )
	public void setCurrentHp(int i) {
		_currentHp = i;
		if (_currentHp >= getMaxHp()) {
			_currentHp = getMaxHp();
		}
	}

	/**
	 * 캐릭터의 HP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 HP
	 */
	public void setCurrentHpDirect(int i) {
		_currentHp = i;
	}

	private int _currentMp;

	/**
	 * 캐릭터의 현재의 MP를 돌려준다.
	 * 
	 * @return 현재의 MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * 캐릭터의 MP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 MP
	 */
	// 특수한 처리가 있는 경우는 여기를 오바라이드(패킷 송신등 )
	public void setCurrentMp(int i) {
		_currentMp = i;
		if (_currentMp >= getMaxMp()) {
			_currentMp = getMaxMp();
		}
	}

	/**
	 * 캐릭터의 MP를 설정한다.
	 * 
	 * @param i
	 *            캐릭터의 새로운 MP
	 */
	public void setCurrentMpDirect(int i) {
		_currentMp = i;
	}

	/**
	 * 캐릭터의 잠상태를 돌려준다.
	 * 
	 * @return 잠상태를 나타내는 값.잠상태이면 true.
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * 캐릭터의 잠상태를 설정한다.
	 * 
	 * @param sleeped
	 *            잠상태를 나타내는 값.잠상태이면 true.
	 */
	public void setSleeped(boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * 캐릭터의 마비 상태를 돌려준다.
	 * 
	 * @return 마비 상태를 나타내는 값.마비 상태이면 true.
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * 캐릭터의 마비 상태를 설정한다.
	 * 
	 * @param i
	 *            마비 상태를 나타내는 값.마비 상태이면 true.
	 */
	public void setParalyzed(boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	L1Paralysis _paralysis;

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}

	/**
	 * 캐릭터의 가시 범위에 있는 플레이어에, 패킷을 송신한다.
	 * 
	 * @param packet
	 *            송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * 캐릭터의 가시 범위에 있는 플레이어에, 패킷을 송신한다. 다만 타겟의 화면내에는 송신하지 않는다.
	 * 
	 * @param packet
	 *            송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void broadcastPacketExceptTargetSight(ServerBasePacket packet,
			L1Character target) {
		for (L1PcInstance pc : L1World.getInstance()
				.getVisiblePlayerExceptTargetSight(this, target)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * 캐릭터의 50 매스 이내에 있는 플레이어에, 패킷을 송신한다.
	 * 
	 * @param packet
	 *            송신하는 패킷을 나타내는 ServerBasePacket 오브젝트.
	 */
	public void wideBroadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this,
				50)) {
			pc.sendPackets(packet);
		}
	}
 /**
  * 캐릭터 PK 킬뎃 관리.
  **/
 private int _Kills;

 public int getKills() {
  return _Kills;
 }

 public void setKills(int Kills) {
     _Kills = Kills;
 }
    
 private int _Deaths;
 
 public int getDeaths() {
     return _Deaths;
 }

 public void setDeaths(int Deaths) {
     _Deaths = Deaths;
 }

	/**
	 * 캐릭터의 정면의 좌표를 돌려준다.
	 * 
	 * @return 정면의 좌표
	 */
	public int[] getFrontLoc() {
		int[] loc = new int[2];
		int x = getX();
		int y = getY();
		int heading = getHeading();
		if (heading == 0) {
			y--;
		} else if (heading == 1) {
			x++;
			y--;
		} else if (heading == 2) {
			x++;
		} else if (heading == 3) {
			x++;
			y++;
		} else if (heading == 4) {
			y++;
		} else if (heading == 5) {
			x--;
			y++;
		} else if (heading == 6) {
			x--;
		} else if (heading == 7) {
			x--;
			y--;
		}
		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * 지정된 좌표에 대할 방향을 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 지정된 좌표에 대할 방향
	 */
	public int targetDirection(int tx, int ty) {
		float dis_x = Math.abs(getX() - tx); // X방향의 타겟까지의 거리
		float dis_y = Math.abs(getY() - ty); // Y방향의 타겟까지의 거리
		float dis = Math.max(dis_x, dis_y); // 타겟까지의 거리
		if (dis == 0) {
			return getHeading(); // 같은 위치라면 지금 향하고 있는 방향을 반환과 구
		}
		int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 상하 좌우가 조금 우선인 둥근
		int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 상하 좌우가 조금 우선인 둥근

		int dir_x = 0;
		int dir_y = 0;
		if (getX() < tx) {
			dir_x = 1;
		}
		if (getX() > tx) {
			dir_x = -1;
		}
		if (getY() < ty) {
			dir_y = 1;
		}
		if (getY() > ty) {
			dir_y = -1;
		}

		if (avg_x == 0) {
			dir_x = 0;
		}
		if (avg_y == 0) {
			dir_y = 0;
		}

		if (dir_x == 1 && dir_y == -1) {
			return 1; // 상
		}
		if (dir_x == 1 && dir_y == 0) {
			return 2; // 우상
		}
		if (dir_x == 1 && dir_y == 1) {
			return 3; // 오른쪽
		}
		if (dir_x == 0 && dir_y == 1) {
			return 4; // 우하
		}
		if (dir_x == -1 && dir_y == 1) {
			return 5; // 하
		}
		if (dir_x == -1 && dir_y == 0) {
			return 6; // 좌하
		}
		if (dir_x == -1 && dir_y == -1) {
			return 7; // 왼쪽
		}
		if (dir_x == 0 && dir_y == -1) {
			return 0; // 좌상
		}
		return getHeading(); // 여기에는 오지 않는다.(은)는 두
	}

	/**
	 * 지정된 좌표까지의 직선상에, 장애물이 존재*하지 않는가*를 돌려준다.
	 * 
	 * @param tx
	 *            좌표의 X치
	 * @param ty
	 *            좌표의 Y치
	 * @return 장애물이 없으면 true, 어느 false를 돌려준다.
	 */
	public boolean glanceCheck(int tx, int ty) {
		L1Map map = getMap();
		int chx = getX();
		int chy = getY();
		int arw = 0;
		for (int i = 0; i < 15; i++) {
			if ((chx == tx && chy == ty) || (chx + 1 == tx && chy - 1 == ty)
					|| (chx + 1 == tx && chy == ty)
					|| (chx + 1 == tx && chy + 1 == ty)
					|| (chx == tx && chy + 1 == ty)
					|| (chx - 1 == tx && chy + 1 == ty)
					|| (chx - 1 == tx && chy == ty)
					|| (chx - 1 == tx && chy - 1 == ty)
					|| (chx == tx && chy - 1 == ty)) {
				break;

			} else if (chx < tx && chy == ty) {
// if (!map.isArrowPassable(chx, chy, 2)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
			} else if (chx == tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 4)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chy++;
			} else if (chx > tx && chy == ty) {
// if (!map.isArrowPassable(chx, chy, 6)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
			} else if (chx == tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 0)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chy--;
			} else if (chx < tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 1)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
				chy--;
			} else if (chx < tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 3)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx++;
				chy++;
			} else if (chx > tx && chy < ty) {
// if (!map.isArrowPassable(chx, chy, 5)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
				chy++;
			} else if (chx > tx && chy > ty) {
// if (!map.isArrowPassable(chx, chy, 7)) {
				if (!map.isArrowPassable(chx, chy, targetDirection(tx, ty))) {
					return false;
				}
				chx--;
				chy--;
			}
		}
		if (arw == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 지정된 좌표에 공격 가능한가를 돌려준다.
	 * 
	 * @param x
	 *            좌표의 X치.
	 * @param y
	 *            좌표의 Y치.
	 * @param range
	 *            공격 가능한 범위(타일수)
	 * @return 공격 가능하면 true, 불가능하면 false
	 */
	public boolean isAttackPosition(int x, int y, int range) {
		if (range >= 7) // 원격 무기(7이상의 경우 기울기를 고려하면(자) 화면외에 나온다)
		{
			if (getLocation().getTileDistance(new Point(x, y)) > range) {
				return false;
			}
		} else // 근접 무기
		{
			if (getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		return glanceCheck(x, y);
	}

	/**
	 * 캐릭터의 목록을 돌려준다.
	 * 
	 * @return 캐릭터의 목록을 나타내는, L1Inventory 오브젝트.
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * 캐릭터에, 새롭게 스킬 효과를 추가한다.
	 * 
	 * @param skillId
	 *            추가하는 효과의 스킬 ID.
	 * @param timeMillis
	 *            추가하는 효과의 지속 시간.무한의 경우는 0.
	 */
	private void addSkillEffect(int skillId, int timeMillis) {
		L1SkillTimer timer = null;
		if (0 < timeMillis) {
			timer = L1SkillTimerCreator.create(this, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * 캐릭터에, 스킬 효과를 설정한다.<br>
	 * 중복 하는 스킬이 없는 경우는, 새롭게 스킬 효과를 추가한다.<br>
	 * 중복 하는 스킬이 있는 경우는, 나머지 효과 시간과 파라미터의 효과 시간의 긴 (분)편을 우선해 설정한다.
	 * 
	 * @param skillId
	 *            설정하는 효과의 스킬 ID.
	 * @param timeMillis
	 *            설정하는 효과의 지속 시간.무한의 경우는 0.
	 */
	public void setSkillEffect(int skillId, int timeMillis) {
		if (hasSkillEffect(skillId)) {
			int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			// 남은 시간이 유한해, 파라미터의 효과 시간이 긴가 무한의 경우는 덧쓰기한다.
			if (remainingTimeMills >= 0
					&& (remainingTimeMills < timeMillis || timeMillis == 0)) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * 캐릭터로부터, 스킬 효과를 삭제한다.
	 * 
	 * @param skillId
	 *            삭제하는 효과의 스킬 ID
	 */
	public void removeSkillEffect(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}

	/**
	 * 캐릭터로부터, 스킬 효과의 타이머를 삭제한다. 스킬 효과는 삭제되지 않는다.
	 * 
	 * @param skillId
	 *            삭제하는 타이머의 스킬 ID
	 */
	public void killSkillEffectTimer(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * 캐릭터로부터, 모든 스킬 효과 타이머를 삭제한다.스킬 효과는 삭제되지 않는다.
	 */
	public void clearSkillEffectTimer() {
		for (L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.kill();
			}
		}
		_skillEffect.clear();
	}

	/**
	 * 캐릭터에, 스킬 효과가 걸려있을까를 돌려준다.
	 * 
	 * @param skillId
	 *            조사하는 효과의 스킬 ID.
	 * @return 마법 효과가 있으면 true, 없으면 false.
	 */
	public boolean hasSkillEffect(int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * 캐릭터의 스킬 효과의 지속 시간을 돌려준다.
	 * 
	 * @param skillId
	 *            조사하는 효과의 스킬 ID
	 * @return 스킬 효과의 남은 시간(초).스킬이 걸리지 않은가 효과 시간이 무한의 경우,-1.
	 */
	public int getSkillEffectTimeSec(int skillId) {
		L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

	private boolean _isSkillDelay = false;

	/**
	 * 캐릭터에, 스킬 지연을 추가한다.
	 * 
	 * @param flag
	 */
	public void setSkillDelay(boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * 캐릭터의 독상태를 돌려준다.
	 * 
	 * @return 스킬 지연중인가.
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * 캐릭터에, 아이템 지연을 추가한다.
	 * 
	 * @param delayId
	 *            아이템 지연 ID. 통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디크로크이면 1.
	 * @param timer
	 *            지연 시간을 나타내는, L1ItemDelay.ItemDelayTimer 오브젝트.
	 */
	public void addItemDelay(int delayId, L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * 캐릭터로부터, 아이템 지연을 삭제한다.
	 * 
	 * @param delayId
	 *            아이템 지연 ID. 통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디크로크이면 1.
	 */
	public void removeItemDelay(int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * 캐릭터에, 아이템 지연이 있을까를 돌려준다.
	 * 
	 * @param delayId
	 *            조사하는 아이템 지연 ID. 통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디
	 *            클로크이면 1.
	 * @return 아이템 지연이 있으면 true, 없으면 false.
	 */
	public boolean hasItemDelay(int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * 캐릭터의 아이템 지연 시간을 나타내는, L1ItemDelay.ItemDelayTimer를 돌려준다.
	 * 
	 * @param delayId
	 *            조사하는 아이템 지연 ID. 통상의 아이템이면 0, 인비지비리티크로크, 바르로그브랏디
	 *            클로크이면 1.
	 * @return 아이템 지연 시간을 나타내는, L1ItemDelay.ItemDelayTimer.
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * 캐릭터에, 새롭게 애완동물, 사몬몬스타, 테이밍몬스타, 혹은 클리에 실 좀비를 추가한다.
	 * 
	 * @param npc
	 *            추가하는 Npc를 나타내는, L1NpcInstance 오브젝트.
	 */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
	}

	/**
	 * 캐릭터로부터, 애완동물, 사몬몬스타, 테이밍몬스타, 혹은 클리에 실 좀비를 삭제한다.
	 * 
	 * @param npc
	 *            삭제하는 Npc를 나타내는, L1NpcInstance 오브젝트.
	 */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
	}

	/**
	 * 캐릭터의 애완동물 리스트를 돌려준다.
	 * 
	 * @return 캐릭터의 애완동물 리스트를 나타내는, HashMap 오브젝트.이 오브젝트의 Key는 오브젝트 ID, Value는 L1NpcInstance.
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}

	/**
	 * 캐릭터에 마법인형을 추가한다.
	 * 
	 * @param doll
	 *            추가하는 doll를 나타내는, L1DollInstance 오브젝트.
	 */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/**
	 * 캐릭터로부터 마법인형을 삭제한다.
	 * 
	 * @param doll
	 *            삭제하는 doll를 나타내는, L1DollInstance 오브젝트.
	 */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/**
	 * 캐릭터의 마법인형 리스트를 돌려준다.
	 * 
	 * @return 캐릭터의 마법 인형 리스트를 나타내는, HashMap 오브젝트.이 오브젝트의 Key는 오브젝트 ID, Value는 L1DollInstance.
	 */
	public Map<Integer, L1DollInstance> getDollList() {
		return _dolllist;
	}

	/**
	 * 캐릭터에 종자를 추가한다.
	 * 
	 * @param follower
	 *            추가하는 follower를 나타내는, L1FollowerInstance 오브젝트.
	 */
	public void addFollower(L1FollowerInstance follower) {
		_followerlist.put(follower.getId(), follower);
	}

	/**
	 * 캐릭터로부터 종자를 삭제한다.
	 * 
	 * @param follower
	 *            삭제하는 follower를 나타내는, L1FollowerInstance 오브젝트.
	 */
	public void removeFollower(L1FollowerInstance follower) {
		_followerlist.remove(follower.getId());
	}

	/**
	 * 캐릭터의 종자 리스트를 돌려준다.
	 * 
	 * @return 캐릭터의 종자 리스트를 나타내는, HashMap 오브젝트.이 오브젝트의 Key는 오브젝트 ID, Value는 L1FollowerInstance.
	 */
	public Map<Integer, L1FollowerInstance> getFollowerList() {
		return _followerlist;
	}





	/**
	 * 캐릭터에, 독을 추가한다.
	 * 
	 * @param poison
	 *            독을 나타내는, L1Poison 오브젝트.
	 */
	public void setPoison(L1Poison poison) {
		_poison = poison;
	}

	/**
	 * 캐릭터의 독을 치료한다.
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * 캐릭터의 독상태를 돌려준다.
	 * 
	 * @return 캐릭터의 독을 나타내는, L1Poison 오브젝트.
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * 캐릭터에 독의 효과를 부가한다
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(int effectId) {
		broadcastPacket(new S_Poison(getId(), effectId));
	}

	/**
	 * 캐릭터가 존재하는 좌표가, 어느 존에 속하고 있을까를 돌려준다.
	 * 
	 * @return 좌표의 존을 나타내는 값.세이프티 존이면 1, 컴배트 존이면―1, 노멀 존이면 0.
	 */
	public int getZoneType() {
		if (getMap().isSafetyZone(getLocation())) {
			return 1;
		} else if (getMap().isCombatZone(getLocation())) {
			return -1;
		} else { // 노멀 존
			return 0;
		}
	}

	private int _exp; // ● 경험치

	/**
	 * 캐릭터가 보관 유지하고 있는 경험치를 돌려준다.
	 * 
	 * @return 경험치.
	 */
	public int getExp() {
		return _exp;
	}

	/**
	 * 캐릭터가 보관 유지하는 경험치를 설정한다.
	 * 
	 * @param exp
	 *            경험치.
	 */
	public void setExp(int exp) {
		_exp = exp;
	}
	 /**
  * 캐릭터가 존재하는 좌표가, 이동가능인가 불가능인가..
  * 
  * @return 가능불가능 값을 표시. 짱돌 2009 - 08 -26
  */
	//뚫어핵
 /*   public int checkMove() {
         if (getMap().isPassable(getLocation())) {
             return 1;
     } else {
             return 0;  
             }
          } */
    //뚫어핵
	// ■■■■■■■■■■ L1PcInstance에 이동하는 프롭퍼티 ■■■■■■■■■■
	private final List<L1Object> _knownObjects = new CopyOnWriteArrayList<L1Object>();
	private final List<L1PcInstance> _knownPlayer = new CopyOnWriteArrayList<L1PcInstance>();

	/**
	 * 지정된 오브젝트를, 캐릭터가 인식하고 있을까를 돌려준다.
	 * 
	 * @param obj
	 *            조사하는 오브젝트.
	 * @return 오브젝트를 캐릭터가 인식하고 있으면 true, 하고 있지 않으면 false. 자기 자신에 대해서는 false를 돌려준다.
	 */
	public boolean knownsObject(L1Object obj) {
		return _knownObjects.contains(obj);
	}

	/**
	 * 캐릭터가 인식하고 있는 모든 오브젝트를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 L1Object가 격납된 ArrayList.
	 */
	public List<L1Object> getKnownObjects() {
		return _knownObjects;
	}

	/**
	 * 캐릭터가 인식하고 있는 모든 플레이어를 돌려준다.
	 * 
	 * @return 캐릭터가 인식하고 있는 오브젝트를 나타내는 L1PcInstance가 격납된 ArrayList.
	 */
	public List<L1PcInstance> getKnownPlayers() {
		return _knownPlayer;
	}

	/**
	 * 캐릭터에, 새롭게 인식하는 오브젝트를 추가한다.
	 * 
	 * @param obj
	 *            새롭게 인식하는 오브젝트.
	 */
	public void addKnownObject(L1Object obj) {
		if (!_knownObjects.contains(obj)) {
			_knownObjects.add(obj);
			if (obj instanceof L1PcInstance) {
				_knownPlayer.add((L1PcInstance) obj);
			}
		}
	}

	/**
	 * 캐릭터로부터, 인식하고 있는 오브젝트를 삭제한다.
	 * 
	 * @param obj
	 *            삭제하는 오브젝트.
	 */
	public void removeKnownObject(L1Object obj) {
		_knownObjects.remove(obj);
		if (obj instanceof L1PcInstance) {
			_knownPlayer.remove(obj);
		}
	}

	/**
	 * 캐릭터로부터, 모든 인식하고 있는 오브젝트를 삭제한다.
	 */
	public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}

	// ■■■■■■■■■■ 프롭퍼티 ■■■■■■■■■■

	private static Random _rnd = new Random(); // ● 랜덤치 생성용
	
	public static Random getRnd() {
		return _rnd;
	}

	private String _name; // ● 이름

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}

	private int _level; // ● 레벨

	public synchronized int getLevel() {
		return _level;
	}

	public synchronized void setLevel(long level) {
		_level = (int) level;
	}

	private short _maxHp = 0; // ● MAXHP(1~32767)
	private int _trueMaxHp = 0; // ● 진정한 MAXHP

	public short getMaxHp() {
		return _maxHp;
	}

	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		_maxHp = (short) IntRange.ensure(_trueMaxHp, 1, 32767);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public void addMaxHp(int i) {
		setMaxHp(_trueMaxHp + i);
	}

	private short _maxMp = 0; // ● MAXMP(0~32767)
	private int _trueMaxMp = 0; // ● 진정한 MAXMP

	public short getMaxMp() {
		return _maxMp;
	}

	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) {
		setMaxMp(_trueMaxMp + i);
	}

	private int _ac = 0; // ● AC(-128~127)
	private int _trueAc = 0; // ● 진정한 AC

	public int getAc() {
		return _ac;
	}

	public void setAc(int i) {
		_trueAc = i;
		_ac = IntRange.ensure(i, -128, 127);
	}

	public void addAc(int i) {
		setAc(_trueAc + i);
	}

	private byte _str = 0; // ● STR(1~127)
	private short _trueStr = 0; // ● 진정한 STR

	public byte getStr() {
		return _str;
	}

	public void setStr(int i) {
		_trueStr = (short) i;
		_str = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addStr(int i) {
		setStr(_trueStr + i);
	}

	private byte _con = 0; // ● CON(1~127)
	private short _trueCon = 0; // ● 진정한 CON

	public byte getCon() {
		return _con;
	}

	public void setCon(int i) {
		_trueCon = (short) i;
		_con = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCon(int i) {
		setCon(_trueCon + i);
	}

	private byte _dex = 0; // ● DEX(1~127)
	private short _trueDex = 0; // ● 진정한 DEX

	public byte getDex() {
		return _dex;
	}

	public void setDex(int i) {
		_trueDex = (short) i;
		_dex = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addDex(int i) {
		setDex(_trueDex + i);
	}

	private byte _cha = 0; // ● CHA(1~127)
	private short _trueCha = 0; // ● 진정한 CHA

	public byte getCha() {
		return _cha;
	}

	public void setCha(int i) {
		_trueCha = (short) i;
		_cha = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCha(int i) {
		setCha(_trueCha + i);
	}

	private byte _int = 0; // ● INT(1~127)
	private short _trueInt = 0; // ● 진정한 INT

	public byte getInt() {
		return _int;
	}

	public void setInt(int i) {
		_trueInt = (short) i;
		_int = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addInt(int i) {
		setInt(_trueInt + i);
	}

	private byte _wis = 0; // ● WIS(1~127)
	private short _trueWis = 0; // ● 진정한 WIS

	public byte getWis() {
		return _wis;
	}

	public void setWis(int i) {
		_trueWis = (short) i;
		_wis = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addWis(int i) {
		setWis(_trueWis + i);
	}

	private int _wind = 0; // ● 방풍어(-128~127)
	private int _trueWind = 0; // ● 진정한 방풍어

	public int getWind() {
		return _wind;
	} // 사용할 때

	public void addWind(int i) {
		_trueWind += i;
		if (_trueWind >= 127) {
			_wind = 127;
		} else if (_trueWind <= -128) {
			_wind = -128;
		} else {
			_wind = _trueWind;
		}
	}

	private int _water = 0; // ● 수방어(-128~127)
	private int _trueWater = 0; // ● 진정한 수방어

	public int getWater() {
		return _water;
	} // 사용할 때

	public void addWater(int i) {
		_trueWater += i;
		if (_trueWater >= 127) {
			_water = 127;
		} else if (_trueWater <= -128) {
			_water = -128;
		} else {
			_water = _trueWater;
		}
	}

	private int _fire = 0; // ● 화재 예방어(-128~127)
	private int _trueFire = 0; // ● 진정한 화재 예방어

	public int getFire() {
		return _fire;
	} // 사용할 때

	public void addFire(int i) {
		_trueFire += i;
		if (_trueFire >= 127) {
			_fire = 127;
		} else if (_trueFire <= -128) {
			_fire = -128;
		} else {
			_fire = _trueFire;
		}
	}

	private int _earth = 0; // ● 지 방어(-128~127)
	private int _trueEarth = 0; // ● 진정한 땅방어

	public int getEarth() {
		return _earth;
	} // 사용할 때

	public void addEarth(int i) {
		_trueEarth += i;
		if (_trueEarth >= 127) {
			_earth = 127;
		} else if (_trueEarth <= -128) {
			_earth = -128;
		} else {
			_earth = _trueEarth;
		}
	}

	private int _addAttrKind; // 일렉트로닉 멘탈 폴 다운으로 감소한 속성의 종류

	public int getAddAttrKind() {
		return _addAttrKind;
	}

	public void setAddAttrKind(int i) {
		_addAttrKind = i;
	}

	// 스탠 내성
	private int _registStun = 0;
	private int _trueRegistStun = 0;

	public int getRegistStun() {
		return _registStun;
	} // 사용할 때

	public void addRegistStun(int i) {
		_trueRegistStun += i;
		if (_trueRegistStun > 127) {
			_registStun = 127;
		} else if (_trueRegistStun < -128) {
			_registStun = -128;
		} else {
			_registStun = _trueRegistStun;
		}
	}

	// 석유화학 내성
	private int _registStone = 0;
	private int _trueRegistStone = 0;

	public int getRegistStone() {
		return _registStone;
	} // 사용할 때

	public void addRegistStone(int i) {
		_trueRegistStone += i;
		if (_trueRegistStone > 127) {
			_registStone = 127;
		} else if (_trueRegistStone < -128) {
			_registStone = -128;
		} else {
			_registStone = _trueRegistStone;
		}
	}

	// 수면 내성
	private int _registSleep = 0;
	private int _trueRegistSleep = 0;

	public int getRegistSleep() {
		return _registSleep;
	} // 사용할 때

	public void addRegistSleep(int i) {
		_trueRegistSleep += i;
		if (_trueRegistSleep > 127) {
			_registSleep = 127;
		} else if (_trueRegistSleep < -128) {
			_registSleep = -128;
		} else {
			_registSleep = _trueRegistSleep;
		}
	}

	// 동결 내성
	private int _registFreeze = 0;
	private int _trueRegistFreeze = 0;

	public int getRegistFreeze() {
		return _registFreeze;
	} // 사용할 때

	public void add_regist_freeze(int i) {
		_trueRegistFreeze += i;
		if (_trueRegistFreeze > 127) {
			_registFreeze = 127;
		} else if (_trueRegistFreeze < -128) {
			_registFreeze = -128;
		} else {
			_registFreeze = _trueRegistFreeze;
		}
	}

	// hold 내성
	private int _registSustain = 0;
	private int _trueRegistSustain = 0;

	public int getRegistSustain() {
		return _registSustain;
	} // 사용할 때

	public void addRegistSustain(int i) {
		_trueRegistSustain += i;
		if (_trueRegistSustain > 127) {
			_registSustain = 127;
		} else if (_trueRegistSustain < -128) {
			_registSustain = -128;
		} else {
			_registSustain = _trueRegistSustain;
		}
	}

	// 어두운 곳 내성
	private int _registBlind = 0;
	private int _trueRegistBlind = 0;

	public int getRegistBlind() {
		return _registBlind;
	} // 사용할 때

	public void addRegistBlind(int i) {
		_trueRegistBlind += i;
		if (_trueRegistBlind > 127) {
			_registBlind = 127;
		} else if (_trueRegistBlind < -128) {
			_registBlind = -128;
		} else {
			_registBlind = _trueRegistBlind;
		}
	}

	private int _dmgup = 0; // ● 데미지 보정(-128~127)
	private int _trueDmgup = 0; // ● 진정한 데미지 보정

	public int getDmgup() {
		return _dmgup;
	} // 사용할 때

	public void addDmgup(int i) {
		_trueDmgup += i;
		if (_trueDmgup >= 127) {
			_dmgup = 127;
		} else if (_trueDmgup <= -128) {
			_dmgup = -128;
		} else {
			_dmgup = _trueDmgup;
		}
	}

	private int _bowDmgup = 0; // ● 활데미지 보정(-128~127)
	private int _trueBowDmgup = 0; // ● 진정한 활데미지 보정

	public int getBowDmgup() {
		return _bowDmgup;
	} // 사용할 때

	public void addBowDmgup(int i) {
		_trueBowDmgup += i;
		if (_trueBowDmgup >= 127) {
			_bowDmgup = 127;
		} else if (_trueBowDmgup <= -128) {
			_bowDmgup = -128;
		} else {
			_bowDmgup = _trueBowDmgup;
		}
	}

	private int _hitup = 0; // ● 명중 보정(-128~127)
	private int _trueHitup = 0; // ● 진정한 명중 보정

	public int getHitup() {
		return _hitup;
	} // 사용할 때

	public void addHitup(int i) {
		_trueHitup += i;
		if (_trueHitup >= 127) {
			_hitup = 127;
		} else if (_trueHitup <= -128) {
			_hitup = -128;
		} else {
			_hitup = _trueHitup;
		}
	}

	private int _bowHitup = 0; // ● 활명중 보정(-128~127)
	private int _trueBowHitup = 0; // ● 진정한 활명중 보정

	public int getBowHitup() {
		return _bowHitup;
	} // 사용할 때

	public void addBowHitup(int i) {
		_trueBowHitup += i;
		if (_trueBowHitup >= 127) {
			_bowHitup = 127;
		} else if (_trueBowHitup <= -128) {
			_bowHitup = -128;
		} else {
			_bowHitup = _trueBowHitup;
		}
	}

	private int _mr = 0; // ● 마법 방어(0~)
	private int _trueMr = 0; // ● 진정한 마법 방어

	public int getMr() {
		if (hasSkillEffect(153) == true) {
			return _mr / 4;
		} else {
			return _mr;
		}
	} // 사용할 때

	public int getTrueMr() {
		return _trueMr;
	} // 세트 할 때

	public void addMr(int i) {
		_trueMr += i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	private int _sp = 0; // ● 증가한 SP

	public int getSp() {
		return getTrueSp() + _sp;
	}

	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public void addSp(int i) {
		_sp += i;
	}

	private boolean _isDead; // ● 사망 상태

	public boolean isDead() {
		return _isDead;
	}

	public void setDead(boolean flag) {
		_isDead = flag;
	}

	private int _status; // ● 상태?

	public int getStatus() {
		return _status;
	}

	public void setStatus(int i) {
		_status = i;
	}

	private String _title; // ● 타이틀

	public String getTitle() {
		return _title;
	}

	public void setTitle(String s) {
		_title = s;
	}

	private int _lawful; // ● 아라이먼트

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}

	private int _heading; // ● 방향 0.좌상 1.상 2.우상 3.오른쪽 4.우하 5.하 6.좌하 7.좌

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}

	private int _moveSpeed; // ● 스피드 0.통상 1.헤이 파업 2.슬로우

	public int getMoveSpeed() {
		return _moveSpeed;
	}

	public void setMoveSpeed(int i) {
		_moveSpeed = i;
	}

	private int _braveSpeed; // ● 치우침 이브 상태 0.통상 1.치우침 이브

	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}
	
	// 용기사 : 블러드 러스트
	/*public void setBlood( int i ) {
		  _braveSpeed = (byte)i;
		  setBraveSpeed(i);
	}*/

	private int _tempCharGfx; // ● 베이스 그래픽 ID

	public int getTempCharGfx() {
		return _tempCharGfx;
	}

	public void setTempCharGfx(int i) {
		_tempCharGfx = i;
	}

	private int _gfxid; // ● 그래픽 ID

	public int getGfxId() {
		return _gfxid;
	}

	public void setGfxId(int i) {
		_gfxid = i;
	}

	public int getMagicLevel() {
		return getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getInt();
		if (i <= 5) {
			return -2;
		} else if (i <= 8) {
			return -1;
		} else if (i <= 11) {
			return 0;
		} else if (i <= 14) {
			return 1;
		} else if (i <= 17) {
			return 2;
		} else if (i <= 24) {
			return i - 15;
		} else if (i <= 35) {
			return 10;
		} else if (i <= 42) {
			return 11;
		} else if (i <= 49) {
			return 12;
		} else if (i <= 50) {
			return 13;
		} else {
			return i - 25;
		}
	}   
     //** 도우너 물약딜레이 타이머 수정 **// by 도우너 
     private long _itemdelay2;  
     public long getItemdelay2(){
         return _itemdelay2;
     } 
     public void setItemdelay2(long itemdelay2){
         _itemdelay2 = itemdelay2;
     }

    //** 도우너 물약딜레이 타이머 수정 **// by 도우너 

	public boolean isInvisble() {
		return (hasSkillEffect(L1SkillId.INVISIBILITY)
				|| hasSkillEffect(L1SkillId.BLIND_HIDING));
	}

	public void healHp(int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	private int _karma;

	/**
	 * 캐릭터가 보관 유지하고 있는 업을 돌려준다.
	 * 
	 * @return 업.
	 */
	public int getKarma() {
		return _karma;
	}

	/**
	 * 캐릭터가 보관 유지하는 업을 설정한다.
	 * 
	 * @param karma
	 *            업.
	 */
	public void setKarma(int karma) {
		_karma = karma;
	}

	public void setMr(int i) {
		_trueMr = i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

    private long _skilldelay2;  
    public long getSkilldelay2(){
         return _skilldelay2;
    } 

    public void setSkilldelay2(long skilldelay2){
         _skilldelay2= skilldelay2;
    } 

    private int _buffnoch;  
    public int getBuffnoch(){
         return _buffnoch;
    } 

    public void setBuffnoch(int buffnoch){
         _buffnoch = buffnoch;
    }

    private int _skillcheck;  
    public int getSkillCheck(){
         return _skillcheck;
    } 

    public void setSkillCheck(int skillcheck){
         _skillcheck = skillcheck;
    }

	public void turnOnOffLight() {
		int lightSize = 0;
		if (this instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) this;
			lightSize = npc.getLightSize(); // npc.sql의 라이트 사이즈
		}
		if (hasSkillEffect(L1SkillId.LIGHT)) {
			lightSize = 14;
		}

		for (L1ItemInstance item : getInventory().getItems()) {
			if (item.getItem().getType2() == 0 && item.getItem()
					.getType() == 2) { // light계 아이템
				int itemlightSize = item.getItem().getLightRange();
				if (itemlightSize != 0 && item.isNowLighting()) {
					if (itemlightSize > lightSize) {
						lightSize = itemlightSize;
					}
				}
			}
		}

		if (this instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) this;
			pc.sendPackets(new S_Light(pc.getId(), lightSize));
		}
		if (!isInvisble()) {
			broadcastPacket(new S_Light(getId(), lightSize));
		}

		setOwnLightSize(lightSize); // S_OwnCharPack의 라이트 범위
		setChaLightSize(lightSize); // S_OtherCharPack, S_NPCPack등의 라이트 범위
	}

	private int _chaLightSize; // ● 라이트의 범위

	public int getChaLightSize() {
		if (isInvisble()) {
			return 0;
		}
		return _chaLightSize;
	}

	public void setChaLightSize(int i) {
		_chaLightSize = i;
	}

	private int _ownLightSize; // ● 라이트의 범위(S_OwnCharPack용)

	public int getOwnLightSize() {
		return _ownLightSize;
	}

	public void setOwnLightSize(int i) {
		_ownLightSize = i;
	}
	Random rnd = new Random(); 
}
