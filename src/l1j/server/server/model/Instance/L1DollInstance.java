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

package l1j.server.server.model.Instance;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.WarTimeController; 
import l1j.server.server.serverpackets.S_SkillIconGFX; 
import l1j.server.server.serverpackets.S_OwnCharStatus; 
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.map.L1WorldMap; 

public class L1DollInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;
	public static final int DOLLTYPE_BUGBEAR = 0;
	public static final int DOLLTYPE_SUCCUBUS = 1;
	public static final int DOLLTYPE_WAREWOLF = 2;
	public static final int DOLLTYPE_ELDER = 3;  // 2차인형 추가
	public static final int DOLLTYPE_CRUST = 4;
	public static final int DOLLTYPE_STONE = 5;
	public static final int DOLLTYPE_RICH = 6;   // 3차인형  추가
	public static final int DOLLTYPE_COKA = 7;
	public static final int DOLLTYPE_HESUABI = 8;
	public static final int DOLLTYPE_SEADANCER = 9; 
	public static final int DOLLTYPE_ETTY = 10; // 에티
	public static final int DOLLTYPE_HEAL = 11; //공주 
	public static final int DOLLTYPE_LAMIA = 12; // 라미아
	public static final int DOLLTYPE_DRAGON1 = 13; //해츨링 여
	public static final int DOLLTYPE_DRAGON2 = 14; //해츨링 남
	public static final int DOLLTYPE_DRAGON3 = 15; //하이 해츨링 여
	public static final int DOLLTYPE_DRAGON4 = 16; //하이 해츨링 남
	public static final int DOLLTYPE_SPATOY = 17; //스파토이
	public static final int DOLLTYPE_SKELETON = 18; //스켈렉톤
	public static final int DOLL_TIME = 1800000;

	private static Logger _log = Logger.getLogger(L1DollInstance.class.getName());
	private ScheduledFuture<? > _dollFuture;
	private static Random _random = new Random();
	private int _dollType;
	private int _itemObjId;

	// 타겟이 없는 경우의 처리
	@Override
	public boolean noTarget() {
		int castleid = L1CastleLocation.getCastleIdByArea(_master);
        	if (_master.isDead() || _master.isInvisble()) { // 투명상태일때 추가
        		broadcastPacket(new S_SkillSound(getId(), 5936)); //인형 주인이 사망시 삭제
        		_master.getDollList().remove(getId());
        		if (_master instanceof L1PcInstance) {
        		L1PcInstance pc = (L1PcInstance) _master;
        		pc.sendPackets(new S_SkillIconGFX(56, 0));
        		pc.sendPackets(new S_OwnCharStatus(pc));
        		}
        		deleteDoll();
        		return true;
        		/**인형 공성시 삭제**/
        		} else if (castleid != 0 && WarTimeController.getInstance().isNowWar(castleid)){ //인형 공성시 삭제
        		broadcastPacket(new S_SkillSound(getId(), 5936)); 
        		_master.getDollList().remove(getId());
        		if (_master instanceof L1PcInstance) {
        		L1PcInstance pc = (L1PcInstance) _master;
        		pc.sendPackets(new S_SkillIconGFX(56, 0));
        		pc.sendPackets(new S_OwnCharStatus(pc));
        		} 
        	deleteDoll();
			return true;
			    /**인형 용방에서 삭제**/
		} else if (_master != null && _master.getMapId() == getMapId()) {
			if( _master.getMapId() == 37 || _master.getMapId() == 65 || _master.getMapId() == 67 ) {  
				broadcastPacket(new S_SkillSound(getId(), 5936));
				_master.getDollList().remove(getId());
				if (_master instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) _master;
				pc.sendPackets(new S_SkillIconGFX(56, 0));
				pc.sendPackets(new S_OwnCharStatus(pc));
				}
				deleteDoll();
				return true;
				}
 
			if (getLocation().getTileLineDistance(_master.getLocation()) > 2) {
				int dir = moveDirection(_master.getX(), _master.getY());
				if (dir == -1) {
				/*	if (!isAiRunning()) {
						startAI();
					} */
				//	deleteDoll(); //주인이 너무 떨어지면 인형 삭제
					if (_master instanceof L1PcInstance) {
						L1PcInstance player = (L1PcInstance) _master;
						if (player.isTeleport()) {
						startAI();
						return true;
						}
						}
					} else {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
						}
						} 
		            if (getLocation().getTileLineDistance(_master.getLocation()) > 5) {  // 추가
			            teleport(_master.getX(), _master.getY(), getHeading());
			            }
					} else {
						deleteDoll();
						return true;
						}
						return false;
						}
	// 시간 계측용
	class DollTimer implements Runnable {
		@Override
		public void run() {
			if (_destroyed) { // 이미 파기되어 있지 않은가 체크
				return;
			}
			deleteDoll();
		}
	}

	public L1DollInstance(L1Npc template, L1PcInstance master, int dollType, int itemObjId) {
		super(template);
		setId(IdFactory.getInstance().nextId());
		setDollType(dollType);
		setItemObjId(itemObjId);
		_dollFuture = GeneralThreadPool.getInstance().schedule(new DollTimer(), DOLL_TIME);
		setMaster(master);
		setX(master.getX() + _random.nextInt(5) - 2);
		setY(master.getY() + _random.nextInt(5) - 2);
		setMap(master.getMapId());
		setHeading(5);
		setLightSize(template.getLightSize());
		L1World.getInstance().storeObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
		onPerceive(pc);
		}
		master.addDoll(this);
		if (!isAiRunning()) {
			startAI();
		}
		if (isMpRegeneration()) {
			master.startMpRegenerationByDoll();
		}
		if (isHpRegeneration()) {
			master.startHpRegenerationByDoll();
		}
	}

	public void deleteDoll() {
		if (isMpRegeneration()) {
			((L1PcInstance) _master).stopMpRegenerationByDoll();
		} else if (isHpRegeneration()) {  
			((L1PcInstance) _master).stopHpRegenerationByDoll();
		} 
		
		_master.getDollList().remove(getId());
	//	((L1PcInstance) _master).sendPackets(new S_SkillIconGFX(56, 0)); // 아이콘 제거
		deleteMe();
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_DollPack(this, perceivedFrom));
	}

	@Override
	public void onItemUse() {
		if (!isActived()) {
			// 100%의 확률로 헤이 파업 일부 사용
			useItem(USEITEM_HASTE, 100);
		}
	}

	@Override
	public void onGetItem(L1ItemInstance item) {
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
		if (Arrays.binarySearch(haestPotions, item.getItem().getItemId()) >= 0) {
			useItem(USEITEM_HASTE, 100);
		}
	}
	
	public void getActionByDoll() {  // 인형 액션 추가
		   int chance = _random.nextInt(2);
		   L1PcInstance pc = (L1PcInstance) _master;
		   if (getLocation().getTileLineDistance(pc.getLocation()) < 2) { // pc와의 거리
		   switch (chance) {
		   case 0:
		    pc.sendPackets(new S_DoActionGFX(getId(), 67));
		    pc.broadcastPacket(new S_DoActionGFX(getId(), 67));
		    break;
		   case 1:
		    pc.sendPackets(new S_DoActionGFX(getId(), 66));
		    pc.broadcastPacket(new S_DoActionGFX(getId(), 66));
		    break;
		          }
		   }
	}
	

	public int getDollType() {
		return _dollType;
	}

	public void setDollType(int i) {
		_dollType = i;
	}

	public int getItemObjId() {
		return _itemObjId;
	}

	public void setItemObjId(int i) {
		_itemObjId = i;
	}

	public int getDamageByDoll() {
		int damage = 0;
		if (getDollType() == DOLLTYPE_WAREWOLF) { //늑인
			int chance = _random.nextInt(100) + 1;
			if (chance <= 8) {
				damage = 10;
				if (_master instanceof L1PcInstance) {
					L1PcInstance pc = (L1PcInstance) _master;
					pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
				}
				_master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
			}
			} else if (getDollType() == DOLLTYPE_CRUST
					|| getDollType() == DOLLTYPE_SKELETON) { //크러스트시안
   int chance = _random.nextInt(100) + 1;
   if (chance <= 9) { // 확률
    damage = 13; // 추가데미지
    if (_master instanceof L1PcInstance) {
     L1PcInstance pc = (L1PcInstance) _master;
     pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
    }
	_master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
		}
	} else if (getDollType() == DOLLTYPE_SPATOY) { 
   int chance = _random.nextInt(100) + 1;
   if (chance <= 9) { // 확률
    damage = 15; // 추가데미지
    if (_master instanceof L1PcInstance) {
     L1PcInstance pc = (L1PcInstance) _master;
     pc.sendPackets(new S_SkillSound(_master.getId(), 6319));
    }
    _master.broadcastPacket(new S_SkillSound(_master.getId(), 6319));
   }
		 }
		return damage;
	}
	
	public int getBowDamageByDoll() {
		  int damage = 0;
		  int _hitRate = 0;
		 if (getDollType() == DOLLTYPE_COKA
				 || getDollType() == DOLLTYPE_SKELETON) { // 코카트리스
		   damage = 5;  //본섭은 추타 +1 이지만, 프리섭인만큼 +5로 해줌.  //이부분은 서버에 맞게 알아서들 수정하세요.
		   _hitRate = 5; // 본섭은 명중률 +1 이지만 프리섭인만큼 +5로 해줌. //이부분은 서버에 맞게 알아서들 수정하세요.
		   }
		 return damage;
		 }
	
	public int getDamageReductionByDoll() {  // 돌골렘 인형(데미지감소)  허수아비 인형 
		  int DamageReduction = 0;
		  if (getDollType() == DOLLTYPE_STONE
				    || getDollType() == DOLLTYPE_HEAL
				    || getDollType() == DOLLTYPE_DRAGON1 
					|| getDollType() == DOLLTYPE_DRAGON2
					|| getDollType() == DOLLTYPE_DRAGON3
					|| getDollType() == DOLLTYPE_DRAGON4) {
		   int chance = _random.nextInt(100) + 1;
		   if (chance <= 8) {
		    DamageReduction = 10;
		    if (_master instanceof L1PcInstance) {
		     L1PcInstance pc = (L1PcInstance) _master;
		     pc.sendPackets(new S_SkillSound(_master.getId(), 6320));
		    }
		    _master.broadcastPacket(new S_SkillSound(_master.getId(), 6320));
		   }
		   } else if (getDollType() == DOLLTYPE_HESUABI) { // 허수아비 인형 
     int chance = _random.nextInt(100) + 1;
     if (chance <= 8) { // 확률
      DamageReduction = 15; // 뎀감소
      if (_master instanceof L1PcInstance) {
       L1PcInstance pc = (L1PcInstance) _master;
       pc.sendPackets(new S_SkillSound(_master.getId(), 6320));
      }
      _master.broadcastPacket(new S_SkillSound(_master.getId(), 6320));
	  }
	  }
		return DamageReduction;
	}
	
	public boolean isMpRegeneration() {
		boolean isMpRegeneration = false;
		if (getDollType() == DOLLTYPE_SUCCUBUS 
				|| getDollType() == DOLLTYPE_ELDER 
				|| getDollType() == DOLLTYPE_RICH 
				|| getDollType() == DOLLTYPE_LAMIA 
				|| getDollType() == DOLLTYPE_DRAGON1 
				|| getDollType() == DOLLTYPE_DRAGON2
				|| getDollType() == DOLLTYPE_DRAGON3
				|| getDollType() == DOLLTYPE_DRAGON4) {  // 장로인형 추가, 리치인형
			isMpRegeneration = true;
		}
		return isMpRegeneration;
	}
	
	public boolean isHpRegeneration() {
		boolean isHpRegeneration = false;
		if (getDollType() == DOLLTYPE_SEADANCER  
				|| getDollType() == DOLLTYPE_HEAL 
				|| getDollType() == DOLLTYPE_ETTY) {  // 시댄서 인형
			isHpRegeneration = true;
		}
		return isHpRegeneration;
	}
	private static void teleport(L1NpcInstance npc, int x, int y, short map,int head) { // 추가
        L1World.getInstance(). moveVisibleObject(npc, map);
        L1WorldMap.getInstance(). getMap(npc.getMapId()). setPassable(npc.getX(),
        npc.getY(), true);
        npc.setX(x);
        npc.setY(y);
        npc.setMap(map);
        npc.setHeading(head);
        L1WorldMap.getInstance(). getMap(npc.getMapId()). setPassable(npc.getX(),
        npc.getY(), false);
    }

	public int getWeightReductionByDoll() {
		int weightReduction = 0;
		if (getDollType() == DOLLTYPE_BUGBEAR
				|| getDollType() == DOLLTYPE_DRAGON1
				|| getDollType() == DOLLTYPE_DRAGON2
				|| getDollType() == DOLLTYPE_DRAGON3
				|| getDollType() == DOLLTYPE_DRAGON4) {
			weightReduction = 20;
		}
		return weightReduction;
	}

}
