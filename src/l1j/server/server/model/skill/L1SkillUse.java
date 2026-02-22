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

package l1j.server.server.model.skill;


import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.SkillCheck; 
import l1j.server.server.IdFactory;	 
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1Clan; //매스
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.Instance.*;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Disconnect; 
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RangeSkill;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.serverpackets.S_ChinSword;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.model.poison.L1ParalysisPoison;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1SkillUse {
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOGIN = 1;
	public static final int TYPE_SPELLSC = 2;
	public static final int TYPE_NPCBUFF = 3;
	public static final int TYPE_GMBUFF = 4;

	private L1Skills _skill;
	private int _skillId;
	private int _getBuffDuration;
	private int _shockStunDuration;
	private int _bonebreakDuration;	 // 본브레이크
	private int _armDuration;	// 암브레이커
	private L1ItemInstance weapon = null;  // 체인소드
	private int _weaponType = 0;  // 체인소드
	private int _getBuffIconDuration;
	private int _targetID;
	private int _mpConsume = 0;
	private int _hpConsume = 0;
	private int _targetX = 0;
	private int _targetY = 0;
    private int _hitRate = 0; // 추가
    private L1PcInstance _targetPc = null; // 추가
	private String _message = null;
	private int _skillTime = 0;
	private int _type = 0;
	private boolean _isPK = false;
	private int _bookmarkId = 0;
	private int _itemobjid = 0;
	private boolean _checkedUseSkill = false; // 사전 체크가 끝난 상태인가
	private int _leverage = 10; // 1/10배이므로 10으로 1배
	private boolean _isFreeze = false;
	private boolean _isCounterMagic = true;
	private boolean _isGlanceCheckFail = false;
	private static final Random _random = new Random();

	private L1Character _user = null;
	private L1Character _target = null;
	private L1PcInstance _player = null;
	private L1NpcInstance _npc = null;
	private L1NpcInstance _targetNpc = null;

	private int _calcType;
	private static final int PC_PC = 1;
	private static final int PC_NPC = 2;
	private static final int NPC_PC = 3;
	private static final int NPC_NPC = 4;

	private ArrayList<TargetStatus> _targetList;

	private static Logger _log = Logger.getLogger(L1SkillUse.class.getName());

	private static final int[] CAST_WITH_INVIS = { 1, 2, 3, 5, 8, 9, 12, 13,
			14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57,
			60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
			BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
			101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
			117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
			150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
			171, SOUL_OF_FLAME, ADDITIONAL_FIRE, AVATA, INSIGHT, RICH, OUGU, PAYTIONS
			, DIAGOLEM, COMA, COMABUFF, SANGA, SANGABUFF, CRAY, ANTA_BLOOD };

	private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12,
			13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55,
			57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN,
			REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
			97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
			114, 115, 116, 117, 118, 129, 130, 131, 132, 134, 137, 138, 146,
			147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163, 164, 165,
			166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE, 10026,
			10027, 10028, 10029, 10030, THUNDER_GRAB, BONEBREAK, AVATA, INSIGHT, RICH, OUGU, PAYTIONS
			, DIAGOLEM, FOURSLAYER
            // 카운터 매직으로 방어할수 없는 스킬
			,10034 ,10047 ,10090 ,20120 ,20167 ,20170 ,20171 ,20360 ,20361     
	        ,30002 ,30003 ,30006 ,30008 ,30043 ,30052 
	        ,30053 ,30054 ,30064 ,30081 ,30082 ,30083
            ,10190 ,10191 ,10192};
	public L1SkillUse() {
	}

	private static class TargetStatus {
		private L1Character _target = null;
		private boolean _isAction = false; // 데미지 모션이 발생할까?
		private boolean _isSendStatus = false; // 캐릭터 스테이터스를 송신할까? (힐, 슬로우 등 상태가 바뀔 때 보낸다)
		private boolean _isCalc = true; // 데미지나 확률 마법의 계산을 할 필요가 있을까?

		public TargetStatus(L1Character _cha) {
			_target = _cha;
		}

		public TargetStatus(L1Character _cha, boolean _flg) {
			_isCalc = _flg;
		}

		public L1Character getTarget() {
			return _target;
		}

		public boolean isCalc() {
			return _isCalc;
		}

		public void isAction(boolean _flg) {
			_isAction = _flg;
		}

		public boolean isAction() {
			return _isAction;
		}	

		public void isSendStatus(boolean _flg) {
			_isSendStatus = _flg;
		}

		public boolean isSendStatus() {
			return _isSendStatus;
		}
	}

	/*
	 * 1/10배로 표현한다.
	 */
	public void setLeverage(int i) {
		_leverage = i;
	}

	public int getLeverage() {
		return _leverage;
	}

	private boolean isCheckedUseSkill() {
		return _checkedUseSkill;
	}

	private void setCheckedUseSkill(boolean flg) {
		_checkedUseSkill = flg;
	}

	public boolean checkUseSkill(L1PcInstance player, int skillid,
			int target_id, int x, int y, String message, int time, int type,
			L1Character attacker) {
   //** 아래 버그 체크문 실행하면서 에러 안나게 **//  By 도우너
         if (player instanceof L1PcInstance) {
             L1Object l1object = L1World.getInstance().findObject(target_id);
         if (l1object instanceof L1ItemInstance) {
             L1ItemInstance item = (L1ItemInstance) l1object;
         if (item.getX() != 0 && item.getY() != 0) { // 지면상의 아이템은 아니고, 누군가의 소유물
             return false;
             }
         }
   //** 아래 버그 체크문 실행하면서 에러 안나게 **//  By 도우너   

   //** 노딜 방지 추가 **//
    long nowtime = System.currentTimeMillis();    
       if ( skillid == 17 && player.getSkilldelay2() >=  nowtime || skillid == 25  && player.getSkilldelay2() >=  nowtime){
     return false;
       } else  
       if(player.getSkilldelay2() >=  nowtime){
     return false;
        }
       //** 노딜 방지 추가 **// 

 
  //** 2차 스킬 버그 방지 소스 추가 **//  
			  int[] CheckSkillID = { 2, 4, 5, 6, 7, 9, 10, 11, 14, 15, 16,
			   17, 18, 20, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
			   41, 45, 46, 47, 48, 49, 50, 51, 52, 53, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
			   64, 65, 66, 67, 68, 69, 70, 71, 72, 73 ,74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 
			   86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 
			   106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 
			   123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 
			   140, 141, 142, 143, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 
			   158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 169, 170, 171, 172, 173, 174, 175, 176 };
			  //3, 12, 13, 21, 26, 42, 168, 43, 44, 54, 1, 8 치투 신투 힘투 헤이스트는 빠짐 켄슬 44빠짐

			  //스킬검사에서 빠지게 할 스킬은 위 번호에서 빼삼!
			  
			  int check = 0;  
			  for (int chskill : CheckSkillID) {
			   if (chskill == skillid) {
			    check = chskill;
			    break;
			   }
			  }
			  if (player.getBuffnoch() == 0) {
			  if (check != 0){
			   player.setSkillCheck(0);
			   SkillCheck.getInstance().CheckSkill(player, check); 
			   if (player.getSkillCheck() == 1) { 
			    return false;
			    }
			   }
			  }	
			  //** 2차 스킬 버그 방지 소스 추가 **//
           }    //** 위 버그 체크문 실행하면서 에러 안나게 **// 

  //** 존재 버그 사용자 잡아보자 **//  

      if (player instanceof L1PcInstance) {
         L1PcInstance jonje = L1World.getInstance().getPlayer(player.getName());
          if (jonje == null && player.getAccessLevel() != 200) {
		   player.sendPackets(new S_SystemMessage("존재버그가 발견되어 접속을 강제종료 합니다."));
            player.sendPackets(new S_Disconnect()); 
         return false;
        }  
     }
  //** 존재 버그 사용자 잡아보자 **//  

		// 초기설정 여기로부터
		setCheckedUseSkill(true);
		_targetList = new ArrayList<TargetStatus>(); // 타겟 리스트의 초기화
		_skill = SkillsTable.getInstance().getTemplate(skillid);
		_skillId = skillid;
		_targetX = x;
		_targetY = y;
		_message = message;
		_skillTime = time;
		_type = type;
		boolean checkedResult = true;

		if (attacker == null) {
			// pc
			_player = player;
			_user = _player;
		} else {
			// npc
			_npc = (L1NpcInstance) attacker;
			_user = _npc;
		}

		if (_skill.getTarget().equals("none")) {
			_targetID = _user.getId();
			_targetX = _user.getX();
			_targetY = _user.getY();
		} else {
			_targetID = target_id;
		}

		if (type == TYPE_NORMAL) { // 통상의 마법 사용시
			checkedResult = isNormalSkillUsable();
		} else if (type == TYPE_SPELLSC) { // 스펠 스크롤 사용시
			checkedResult = isSpellScrollUsable();
		} else if (type == TYPE_NPCBUFF) {
			checkedResult = true;
		}
		if (!checkedResult) {
			return false;
		}

		// 파이어월, 라이프 시냇물은 영창 대상이 좌표
		if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM
				|| _skillId == Mob_AREA_FIRE_WALL || _skillId == Mob_AREA_POISON_18
				|| _skillId == Mob_AREA_POISON_30 || _skillId == Mob_AREA_POISON 
				|| _skillId == Mob_AREA_POISON_20
				|| _skillId == ANTA_SKILL_6 || _skillId == ANTA_SKILL_7 || _skillId == ANTA_SKILL_10) {	// 안타라스 용언
			return true;
		}

		L1Object l1object = L1World.getInstance().findObject(_targetID);
		if (l1object instanceof L1ItemInstance) {
			_log.fine("skill target item name: "
					+ ((L1ItemInstance) l1object).getViewName());
			// 스킬 타겟이 정령의 돌이 되는 일이 있다.
			// Linux 환경에서 확인(Windows에서는 미확인)
			// 2008.5.4 덧붙여 씀：지면의 아이템에 마법을 사용한다고 된다.계속해도 에러가 될 뿐(만큼)이므로 return
			return false;
		}
		if (_user instanceof L1PcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = PC_PC;
			} else {
				_calcType = PC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		} else if (_user instanceof L1NpcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = NPC_PC;
			} else if (_skill.getTarget().equals("none")) {
				_calcType = NPC_PC;
			} else {
				_calcType = NPC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		}

		// 텔레포트, 매스 텔레포트는 대상이 북마크 ID
		if (_skillId == TELEPORT || _skillId == MASS_TELEPORT) {
			_bookmarkId = target_id;
		}
		// 대상이 아이템의 스킬
		if (_skillId == CREATE_MAGICAL_WEAPON || _skillId == BRING_STONE
				|| _skillId == BLESSED_ARMOR || _skillId == ENCHANT_WEAPON
				|| _skillId == SHADOW_FANG) {
			_itemobjid = target_id;
		}
		_target = (L1Character) l1object;

		if (!(_target instanceof L1MonsterInstance)
				&& _skill.getTarget().equals("attack")
				&& _user.getId() != target_id) {
			_isPK = true; // 타겟이 monster 이외로 공격계 스킬로, 자신 이외의 경우 PK모드로 한다.
		}

		// 초기설정 여기까지

		// 사전 체크
		if (!(l1object instanceof L1Character)) { // 타겟이 캐릭터 이외의 경우 아무것도 하지 않는다.
			checkedResult = false;
		}
		makeTargetList(); // 타겟의 일람을 작성
		if (_targetList.size() == 0 && (_user instanceof L1NpcInstance)) {
			checkedResult = false;
		}
		// 사전 체크 여기까지
		return checkedResult;
	}

	/**
	 * 통상의 스킬 사용시에 사용자 상태로부터 스킬이 사용 가능한가 판단한다
	 * 
	 * @return false 스킬이 사용 불가능한 상태인 경우
	 */
	private boolean isNormalSkillUsable() {

		 if(_user.hasSkillEffect(L1SkillId.STATUS_XNAKD)){ //추가

		      return false;
		   }

		
		// 스킬 사용자가 PC의 경우의 체크
		if (_user instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) _user;

			if (pc.isParalyzed()) { // 마비·동결 상태인가
				return false;
			}			
			if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // 인비지중에 사용 불가의 스킬
				return false;
			}
			//if (pc.getInventory().getWeight30() > 197) { // 중량 오버이면 스킬을 사용할 수 없다
			if (pc.getInventory().getWeight240() >= 197) { // 중량 오버이면 스킬을 사용할 수 없다
				pc.sendPackets(new S_ServerMessage(316));
				return false;
			}
			int polyId = pc.getTempCharGfx();
			L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
			// 마법을 사용할 수 없는 변신
			if (poly != null && !poly.canUseSkill()) {
				pc.sendPackets(new S_ServerMessage(285)); // \f1 그 상태에서는 마법을 사용할 수 없습니다.
				return false;
			}
			int castle_id = L1CastleLocation.getCastleIdByArea(pc);
		     if (castle_id != 0){
		        if(_skillId == 69 || _skillId == 157 || _skillId == 80   // 공성시 마법 제한
		        		       /* || _skillId == 50 || _skillId == 59 */
		        		       /* || _skillId == 58 || _skillId == 65 */
		        		       /* || _skillId == 78 || _skillId == 74 */
		        		       /* || _skillId == 70 */|| _skillId == 161){     
		       pc.sendPackets(new S_SystemMessage("공성존에서는 사용 할 수 없습니다."));
		       return false;
		      }
		     }	

			if (!isAttrAgrees()) { // 정령 마법으로, 속성이 일치하지 않으면 아무것도 하지 않는다.
				return false;
			}

			if (_skillId == ELEMENTAL_PROTECTION && pc.getElfAttr() == 0) {
				pc.sendPackets(new S_ServerMessage(280)); // \f1마법이 실패했습니다.
				return false;
			}

			// 스킬 지연중 사용 불가
			if (pc.isSkillDelay()) {
				return false;
			}

			// 침묵 상태에서는 사용 불가
			if (pc.hasSkillEffect(SILENCE)
					|| pc.hasSkillEffect(AREA_OF_SILENCE)
					|| pc.hasSkillEffect(STATUS_POISON_SILENCE)||
					pc.hasSkillEffect(CONFUSION)) {
			if  (_skillId == REDUCTION_ARMOR || _skillId == BOUNCE_ATTACK
					|| _skillId == SOLID_CARRIAGE || _skillId == COUNTER_BARRIER
					|| _skillId == SHOCK_STUN   // 기사 기술 사용가능하게
					|| _skillId == BONEBREAK || _skillId == ARMBREAKER
					|| _skillId == SMASH || _skillId == MINDBREAK
					|| _skillId == FOURSLAYER){ // 환술사 용기사 물리스킬 가능하게
					      return true;
			}else{
					pc.sendPackets(new S_ServerMessage(285)); // \f1 그 상태에서는 마법을 사용할 수 없습니다.
					      return false;			        
					     }
					  }
			if (_skillId == SOLID_CARRIAGE) { // 가더착용시 솔캐사용불가 
			    if(pc.getInventory().checkEquipped(22000) || 
			      pc.getInventory().checkEquipped(22001) || 
			      pc.getInventory().checkEquipped(22002) || 
			      pc.getInventory().checkEquipped(22003) ||
			      pc.getWeapon().getItem().getType() == 3) { //양검착용시 솔캐시전불가 
			     pc.sendPackets(new S_SystemMessage("가더 또는 양손검착용시  시전할 수 없습니다.")); 
			     return false;
			    }
			   }
			// 세이프티존에서 노멀/컴뱃존 공격불가
			if (_calcType == PC_PC) { 
            if (pc.getZoneType() == 1 && _targetPc.getZoneType() == 0
               || pc.getZoneType() == 1 && _targetPc.getZoneType() == -1
               || pc.getZoneType() == 0 && _targetPc.getZoneType() == 1
               || pc.getZoneType() == -1 && _targetPc.getZoneType() == 1) {  
                  return false;
                 }
            }
			
			// 포우슬레이어 - 체인소드를 제외
			/*weapon = pc.getWeapon();
			_weaponType = weapon.getItem().getType1();
			if (_skillId == FOURSLAYER && _weaponType == 24) { // 창계열 무기(체인소드)
				pc.sendPackets(new S_SystemMessage("그 상태로는 시전할 수 없습니다.")); 
				return false;
			}*/
			
			// 시장에서 매스텔레포트 사용 불가
			if (pc.getMapId() == 340 && pc.getMapId() == 350 && pc.getMapId() == 360 
				&& pc.getMapId() == 370 &&_skillId == MASS_TELEPORT) {  // 시장이면서 매스텔레포트
				pc.sendPackets(new S_SystemMessage("시장 안에서는 사용이 불가능합니다."));
				return false;
			}
			
			// DIG는 로우훌에서만 사용가능
			if (_skillId == DISINTEGRATE && pc.getLawful() < 500) {
				// 이 메세지이고 있어 미확인
				pc.sendPackets(new S_ServerMessage(352, "$967")); // 이 마법을 이용하려면  성향치가%0가 아니면 안됩니다.
								if (_target instanceof L1PcInstance) {
                    _targetPc = (L1PcInstance) _target;
                    _calcType = PC_PC;
                if (_skillId == DISINTEGRATE && _targetPc.hasSkillEffect(DISINTEGRATE)){
                    _hitRate = 0; 
                   } 
                }
				return false;
			}

			if (isItemConsume() == false && !_player.isGm()) { // 소비 아이템은 있을까
				_player.sendPackets(new S_ServerMessage(299)); // 영창 하는 재료가 없습니다.
				return false;
			}
		}
		// 스킬 사용자가 NPC의 경우의 체크
		else if (_user instanceof L1NpcInstance) {

			// 침묵 상태에서는 사용 불가
			if (_user.hasSkillEffect(SILENCE)) {
				// NPC에 침묵이 걸려있는 경우는 1회만 사용을 캔슬시키는 효과.
				_user.removeSkillEffect(SILENCE);
				return false;
			}
		}

		// PC, NPC 공통의 체크
		if (!isHPMPConsume()) { // 소비 HP, MP는 있을까
			return false;
		}
		return true;
	}

	/**
	 * 스펠 스크롤 사용시에 사용자 상태로부터 스킬이 사용 가능한가 판단한다
	 * 
	 * @return false 스킬이 사용 불가능한 상태인 경우
	 */
	private boolean isSpellScrollUsable() {
		// 스펠 스크롤을 사용하는 것은 PC만
		L1PcInstance pc = (L1PcInstance) _user;

		if (pc.isParalyzed()) { // 마비·동결 상태인가
			return false;
		}

		// 인비지중에 사용 불가의 스킬
		if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
			return false;
		}

		return true;
	}

	// 인비지중에 사용 가능한 스킬인지를 돌려준다
	private boolean isInvisUsableSkill() {
		for (int skillId : CAST_WITH_INVIS) {
			if (skillId == _skillId) {
				return true;
			}
		}
		return false;
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type) {
		L1Character attacker = null;
		handleCommands(player, skillId, targetId, x, y, message, timeSecs,
				type, attacker);
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type,
			L1Character attacker) {

		try {
			// 사전 체크를 하고 있을까?
			if (!isCheckedUseSkill()) {
				boolean isUseSkill = checkUseSkill(player, skillId, targetId,
						x, y, message, timeSecs, type, attacker);

				if (!isUseSkill) {
					failSkill();
					return;
				}
			}

			if (type == TYPE_NORMAL) { // 마법 영창시
				if (!_isGlanceCheckFail || _skill.getArea() > 0 
                 	|| _skill.getTarget().equals("none")) {
				runSkill();
				useConsume();
				sendGrfx(true);
				sendFailMessageHandle();
				setDelay();
				}
			} else if (type == TYPE_LOGIN) { // 로그인시(HPMP 재료 소비 이루어, 그래픽 없음)
				runSkill();
			} else if (type == TYPE_SPELLSC) { // 스펠 스크롤 사용시(HPMP 재료 소비 없음)
				runSkill();
				sendGrfx(true);
			} else if (type == TYPE_GMBUFF) { // GMBUFF 사용시(HPMP 재료 소비 이루어, 마법 모션 없음)
				runSkill();
				sendGrfx(false);
			} else if (type == TYPE_NPCBUFF) { // NPCBUFF 사용시(HPMP 재료 소비 없음)
				runSkill();
				sendGrfx(true);
			}
			setCheckedUseSkill(false);
		} catch (Exception e) {
			_log.log(Level.SEVERE, "", e);
		}
	}

	/**
	 * 스킬의 실패 처리(PC만)
	 */
	private void failSkill() {
		// HP가 부족해서 스킬을 사용할 수 없는 경우만, MP만 소비하고 싶지만 미실장(필요없어? )
		// 그 외의 경우는 아무것도 소비되지 않는다.
		// useConsume(); // HP, MP는 줄인다
		setCheckedUseSkill(false);
		// 텔레포트 스킬
		if (_skillId == TELEPORT || _skillId == MASS_TELEPORT
				|| _skillId == TELEPORT_TO_MATHER) {
			// 텔레포트 할 수 없는 경우에서도, 클라이언트측은 응답을 기다리고 있다
			// 텔레포트 대기 상태의 해제( 제2 인수에 의미는 없다)
			_player.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
		}
	}

	// 타겟인가?
	private boolean isTarget(L1Character cha) throws Exception {
		boolean _flg = false;

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.isGhost() || pc.isGmInvis()) {
				return false;
			}
		}
		if (_calcType == NPC_PC
				&& (cha instanceof L1PcInstance || cha instanceof L1PetInstance || cha instanceof L1SummonInstance)) {
			_flg = true;
		}

		// 파괴 불가능한 문은 대상외
		if (cha instanceof L1DoorInstance) {
			if (cha.getMaxHp() == 0 || cha.getMaxHp() == 1) {
				return false;
			}
		}

		// 마법인형은 대상외
		if (cha instanceof L1DollInstance && _skillId != HASTE) {
			return false;
		}

		// 원의 타겟이 Pet, Summon 이외의 NPC의 경우, PC, Pet, Summon는 대상외
		if (_calcType == PC_NPC
				&& _target instanceof L1NpcInstance
				&& !(_target instanceof L1PetInstance)
				&& !(_target instanceof L1SummonInstance)
				&& (cha instanceof L1PetInstance
				    || cha instanceof L1SummonInstance 
				    || cha instanceof L1PcInstance)) {
			return false;
		}

		// 원의 타겟이 가이드 이외의 NPC의 경우, 가이드는 대상외
		if (_calcType == PC_NPC
				&& _target instanceof L1NpcInstance
				&& ! (_target instanceof L1GuardInstance)
				&& cha instanceof L1GuardInstance) {
			return false;
		}

		// NPC대  PC로 타겟이 monster의 경우 타겟은 아니다.
		if ((_skill.getTarget().equals("attack") 
				|| _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_PC
				&& !(cha instanceof L1PetInstance)
				&& !(cha instanceof L1SummonInstance)
				&& !(cha instanceof L1PcInstance)) {
			return false;
		}

		// NPC대  NPC로 사용자가 MOB로, 타겟이 MOB의 경우 타겟은 아니다.
		if ((_skill.getTarget().equals("attack")
				|| _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_NPC
				&& _user instanceof L1MonsterInstance
				&& cha instanceof L1MonsterInstance) {
			return false;
		}

		// 무방향 범위 공격 마법으로 공격할 수 없는 NPC는 대상외
		if (_skill.getTarget().equals("none")
				&& _skill.getType() == L1Skills.TYPE_ATTACK
				&& (cha instanceof L1AuctionBoardInstance
						|| cha instanceof L1BoardInstance
						|| cha instanceof L1CrownInstance
						|| cha instanceof L1DwarfInstance
						|| cha instanceof L1EffectInstance
						|| cha instanceof L1FieldObjectInstance
						|| cha instanceof L1FurnitureInstance
						|| cha instanceof L1HousekeeperInstance
						|| cha instanceof L1MerchantInstance
						|| cha instanceof L1TeleporterInstance)) {
			return false;
		}

		// 공격계 스킬로 대상이 자신은 대상외
		if (_skill.getType() == L1Skills.TYPE_ATTACK
				&& cha.getId() == _user.getId()) {
			return false;
		}

		// 타겟이 스스로 H-A의 경우 효과 없음
		if (cha.getId() == _user.getId() && _skillId == HEAL_ALL) {
			return false;
		}

		if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
				|| (_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN || (_skill
				.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
				&& cha.getId() == _user.getId() && _skillId != HEAL_ALL) {
			return true; // 타겟이 파티나 크란원의 것은 자신에게 효과가 있다.(다만, 힐 올은 제외)
		}

		// 스킬 사용자가 PC로, PK모드가 아닌 경우, 자신의 사몬·애완동물은 대상외
		if (_user instanceof L1PcInstance
				&& (_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _isPK == false) {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (_player.getId() == summon.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (_player.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		if ((_skill.getTarget().equals("attack")
				|| _skill.getType() == L1Skills.TYPE_ATTACK)
				&& !(cha instanceof L1MonsterInstance)
				&& _isPK == false
				&& _target instanceof L1PcInstance) {
			L1PcInstance enemy = (L1PcInstance) cha;
			// 카운터 디 텍 숀
			if (_skillId == COUNTER_DETECTION && enemy.getZoneType() != 1
				&& (cha.hasSkillEffect(INVISIBILITY)
				|| cha.hasSkillEffect(BLIND_HIDING))) {
				return true; // 인비지나 브라인드하이딘그중
			}
			if (_player.getClanid() != 0 && enemy.getClanid() != 0) { // 크란 소속중
				// 전전쟁 리스트를 취득
				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInWar(_player.getClanname())) { // 자크란이 전쟁에 참가중
						if (war.CheckClanInSameWar( // 같은 전쟁에 참가중
								_player.getClanname(), enemy.getClanname())) {
							if (L1CastleLocation.checkInAllWarArea(enemy.getX(),
									enemy.getY(), enemy.getMapId())) {
								return true;
							}
						}
					}
				}
			}
			return false; // 공격 스킬로 PK모드가 아닌 경우
		}

		if (_user.glanceCheck(cha.getX(), cha.getY()) == false
			//	&& _skill.getIsThrough() == false) {
				&& _skill.isThrough() == false) {
			// 엔챤트, 부활 스킬은 장애물의 판정을 하지 않는다
			//if (!(_skill.getType() == L1Skills.TYPE_CHANGE || _skill.getType() == L1Skills.TYPE_RESTORE)) {
			if (!(_skill.getType() == L1Skills.TYPE_CHANGE   
					|| _skill.getType() == L1Skills.TYPE_RESTORE)) { 
				   _isGlanceCheckFail = true;
			return false; // 직선상에 장애물이 있다
			}
		}

		if (cha.hasSkillEffect(ICE_LANCE)
				&& (_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD
		|| _skillId == Mob_AREA_ICE_LANCE || _skillId == Mob_CALL_LIGHTNING_ICE  ////////////////////////////// 몬스터 마법 중복 불가 - 몹스킬패턴 추가	
		)) {
			return false; // 아이스 랑스중에 아이스 랑스, freezing 블리자드
		}

		if (cha.hasSkillEffect(FREEZING_BLIZZARD)
				&& (_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD
		|| _skillId == Mob_AREA_ICE_LANCE || _skillId == Mob_CALL_LIGHTNING_ICE	 ///////////////////////////// 몬스터 마법 중복 불가 - 몹스킬패턴 추가	
		)) {
			return false; // freezing 블리자드중에 아이스 랑스, freezing 블리자드
		}

		if (cha.hasSkillEffect(EARTH_BIND) && _skillId == EARTH_BIND) {
			return false; // 아스바인드중에 아스바인드
		}
	
		if (cha.hasSkillEffect(Mob_CALL_LIGHTNING_ICE) && (_skillId == Mob_CALL_LIGHTNING_ICE
			|| _skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD)) {
			return false; // 제브 레퀴(남) 얼리기중 중복불가
		}
		if (cha.hasSkillEffect(Mob_AREA_ICE_LANCE) && (_skillId == Mob_AREA_ICE_LANCE
			|| _skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD)) {
			return false; // 파푸리온 얼리기중 중복불가
		}
		if (cha.hasSkillEffect(Mob_Basill) && (_skillId == Mob_Basill
			|| _skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD)) {
			return false; // 바실굳기중에 바실굳기
		}
		if (cha.hasSkillEffect(Mob_Coca) && (_skillId == Mob_Coca
			|| _skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD)) {
			return false; // 코카굳기중에 코카굳기 
		}
		
		if (cha.hasSkillEffect(DISINTEGRATE) && _skillId == DISINTEGRATE) {
			return false; // 디스중.디스금지
		}

		if (!(cha instanceof L1MonsterInstance)
				&& (_skillId == TAMING_MONSTER || _skillId == CREATE_ZOMBIE)) {
			return false; // 타겟이 monster가 아니다(테이밍몬스타)
		}
		if (cha.isDead()
				&& (_skillId != CREATE_ZOMBIE
				&& _skillId != RESURRECTION
				&& _skillId != GREATER_RESURRECTION
				&& _skillId != CALL_OF_NATURE)) {
			return false; // 타겟이 사망하고 있다
		}

		if (cha.isDead() == false
				&& (_skillId == CREATE_ZOMBIE
				|| _skillId == RESURRECTION
				|| _skillId == GREATER_RESURRECTION
				|| _skillId == CALL_OF_NATURE)) {
			return false; // 타겟이 사망하고 있지 않다
		}

		if ((cha instanceof L1TowerInstance || cha instanceof L1DoorInstance)
				&& (_skillId == CREATE_ZOMBIE
				|| _skillId == RESURRECTION
				|| _skillId == GREATER_RESURRECTION
				|| _skillId == CALL_OF_NATURE)) {
			return false; // 타겟이 가디안 타워, 문
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 아브소르트바리아중
				if (_skillId == CURSE_BLIND || _skillId == WEAPON_BREAK
						|| _skillId == DARKNESS || _skillId == WEAKNESS
						|| _skillId == DISEASE || _skillId == FOG_OF_SLEEPING
						|| _skillId == MASS_SLOW || _skillId == SLOW
						|| _skillId == CANCELLATION || _skillId == SILENCE
						|| _skillId == DECAY_POTION || _skillId == MASS_TELEPORT
						|| _skillId == DETECTION || _skillId == EARTH_BIND
						|| _skillId == COUNTER_DETECTION
						|| _skillId == ERASE_MAGIC || _skillId == ENTANGLE
						|| _skillId == PHYSICAL_ENCHANT_DEX
						|| _skillId == PHYSICAL_ENCHANT_STR
						|| _skillId == BLESS_WEAPON || _skillId == EARTH_SKIN
						|| _skillId == IMMUNE_TO_HARM
						|| _skillId == REMOVE_CURSE ||_skillId == CONFUSION 
						|| _skillId == Mob_SLOW_1 || _skillId == Mob_SLOW_18 
						|| _skillId == Mob_WEAKNESS_1 || _skillId == Mob_DISEASE_1
						|| _skillId == Mob_Basill || _skillId == Mob_SHOCKSTUN_18
						|| _skillId == Mob_RANGESTUN_19 || _skillId == Mob_RANGESTUN_18
						|| _skillId == Mob_DISEASE_30 || _skillId == Mob_WINDSHACKLE_1
						|| _skillId == Mob_Coca || _skillId == Mob_CURSEPARALYZ_19
						|| _skillId == Mob_CURSEPARALYZ_18 || _skillId == Mob_CURSEPARALYZ_30
						|| _skillId == Mob_CURSEPARALYZ_SHORT_18 || _skillId == Mob_VAMPIRIC_TOUCH_1
						|| _skillId == Mob_AREA_ICE_LANCE || _skillId == Mob_AREA_FIRE_WALL
						|| _skillId == Mob_AREA_POISON_18 || _skillId == Mob_AREA_POISON_30 
						|| _skillId == Mob_AREA_POISON || _skillId ==  Mob_CURSPOISON_30 
						|| _skillId == Mob_CURSPOISON_18 || _skillId == Mob_CALL_LIGHTNING_ICE
						|| _skillId == Mob_RANGESTUN_30 || _skillId == Mob_AREA_CANCELLATION_19
						|| _skillId == Mob_AREA_POISON_20
						|| _skillId == ANTA_SKILL_1 || _skillId == ANTA_SKILL_2	|| _skillId == ANTA_SKILL_3	// 안타라스 용언
						|| _skillId == ANTA_SKILL_4 || _skillId == ANTA_SKILL_5	|| _skillId == ANTA_SKILL_6
						|| _skillId == ANTA_SKILL_7 || _skillId == ANTA_SKILL_8	|| _skillId == ANTA_SKILL_9
						|| _skillId == ANTA_SKILL_10) {
					return true;
				} else {
					return false;
				}
			}
		}

		if (cha instanceof L1NpcInstance) {
			int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
			if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK
				|| hiddenStatus == L1NpcInstance.HIDDEN_STATUS_DOLGOLLEM) {
				if (_skillId == DETECTION || _skillId == COUNTER_DETECTION) { // 디 텍, C디 텍
					return true;
				} else {
					return false;
				}
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
				return false;
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_STOM) {   // 기르타스
				return false;																								
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK_ANTA) { // 안타라스
				return false;
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY_LIND) { // 린드비오르
				return false;
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK_ITEM) {   // 쉘맨
				return false;
			}
		}

		if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // 타겟이 PC
				&& cha instanceof L1PcInstance) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // 타겟이 NPC
				&& (cha instanceof L1MonsterInstance
						|| cha instanceof L1NpcInstance
						|| cha instanceof L1SummonInstance || cha instanceof L1PetInstance)) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills
				. TARGET_TO_PET && _user instanceof L1PcInstance) { // 타겟이 Summon, Pet
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (summon.getMaster() != null) {
					if (_player.getId() == summon.getMaster().getId()) {
			_flg = true;
					}
				}
			}
			if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (pet.getMaster() != null) {
					if (_player.getId() == pet.getMaster().getId()) {
						_flg = true;
					}
				}
			}
		}

		if (_calcType == PC_PC && cha instanceof L1PcInstance) {
			if ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN
					&& ((_player.getClanid() != 0 // 타겟이 크란원
					&& _player.getClanid() == ((L1PcInstance) cha).getClanid()) || _player
							.isGm())) {
				return true;
			}
			if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY
					&& (_player.getParty() // 타겟이 파티
							.isMember((L1PcInstance) cha) || _player.isGm())) {
				return true;
			}
		}

		return _flg;
	}

	// 타겟의 일람을 작성
	private void makeTargetList() {
		try {
			if (_type == TYPE_LOGIN) { // 로그인시(사망시, 도깨비 저택의 왈가닥 세레이션 포함한다)는 사용자만
				_targetList.add(new TargetStatus(_user));
				return;
			}
			if (_skill.getTargetTo() == L1Skills.TARGET_TO_ME
					&& (_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK) {
				_targetList.add(new TargetStatus(_user)); // 타겟은 사용자만
				return;
			}

			// 사정거리-1의 경우는 화면내의 오브젝트가 대상
			if (_skill.getRanged() != -1) {
				if (_user.getLocation().getTileLineDistance(
						_target.getLocation()) > _skill.getRanged()) {
					return; // 사정 범위외
				}
			} else {
				if (!_user.getLocation().isInScreen(_target.getLocation())) {
					return; // 사정 범위외
				}
			}

			if (isTarget(_target) == false
					&& !(_skill.getTarget().equals("none"))) {
				// 대상이 다르므로 스킬이 발동하지 않는다.
				return;
			}

			if (_skillId == LIGHTNING
				|| _skillId == 10151	// 안타라스(리뉴얼) 큰이럽션입김
				|| _skillId == ANTA_SKILL_8 || _skillId == ANTA_SKILL_9) {	
				// 라이트닝 직선적으로 범위를 결정한다.
				ArrayList<L1Object> al1object = L1World.getInstance()
						.getVisibleLineObjects(_user, _target);
				for (L1Object tgobj : al1object) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) { // 타겟이 캐릭터 이외의 경우 아무것도 하지 않는다.
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (isTarget(cha) == false) {
						continue;
					}
					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

			if (_skill.getArea() == 0) { // 단체의 경우
			  //if (_user.glanceCheck(_target.getX(), _target.getY()) {// 직선상에 장애물이 있을까
				if (!_user.glanceCheck(_target.getX(), _target.getY())) { // 직선상에 장애물이 있을까
					if ((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK && _skillId != 10026
						&& _skillId != 10027 && _skillId != 10028 && _skillId != 10029) {
						_targetList.add(new TargetStatus(_target, false)); // 데미지도 발생하지 않고, 데미지 모션도 발생하지 않지만, 스킬은 발동
						return;
					}
				}
				_targetList.add(new TargetStatus(_target));
			} else { // 범위의 경우
				if (!_skill.getTarget().equals("none")) {
					_targetList.add(new TargetStatus(_target));
				}

				if (_skillId != 49
						&& !(_skill.getTarget().equals("attack") || _skill
								.getType() == L1Skills.TYPE_ATTACK)) {
					// 공격계 이외의 스킬과 H-A 이외는 타겟 자신을 포함한다
					_targetList.add(new TargetStatus(_user));
				}

				List<L1Object> objects;
				if (_skill.getArea() == -1) {
					objects = L1World.getInstance()
							.getVisibleObjects(_user);
				} else {
					objects = L1World.getInstance()
							.getVisibleObjects(_target, _skill.getArea());
				}
				for (L1Object tgobj : objects) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) { // 타겟이 캐릭터 이외의 경우 아무것도 하지 않는다.
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (!isTarget(cha)) {
						continue;
					}

					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

		} catch (Exception e) {
			_log.finest("exception in L1Skilluse makeTargetList" + e);
		}
	}

	// 메세지의 표시(무엇인가 일어났을 때)
	private void sendHappenMessage(L1PcInstance pc) {
		int msgID = _skill.getSysmsgIdHappen();
		if (msgID > 0) {
			pc.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 실패 메세지 표시의 핸들
	private void sendFailMessageHandle() {
		// 공격 스킬 이외로 대상을 지정하는 스킬이 실패했을 경우는 실패한 메세지를 클라이언트에 송신
		// ※공격 스킬은 장애물이 있어도 성공시와 같은 액션이어야 함.
		if (_skill.getType() != L1Skills.TYPE_ATTACK
				&& !_skill.getTarget().equals("none")
				&& _targetList.size() == 0) {
			sendFailMessage();
		}
	}

	// 메세지의 표시(실패했을 때)
	private void sendFailMessage() {
		int msgID = _skill.getSysmsgIdFail();
		if (msgID > 0 && (_user instanceof L1PcInstance)) {
			_player.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 정령 마법의 속성과 사용자의 속성은 일치할까? (우선의 대처이므로, 대응할 수 있으면(자) 소거해 주세요)
	/*private boolean isAttrAgrees() {
		int magicattr = _skill.getAttr();
		if (_user instanceof L1NpcInstance) { // NPC가 사용했을 경우 뭐든지 OK
			return true;
		}

		if ((_skill.getSkillLevel() >= 17 && magicattr != 0) // 정령 마법으로, 무속성 마법은 아니고,
				&& (magicattr != _player.getElfAttr() // 사용자와 마법의 속성이 일치하지 않는다.
				&& !_player.isGm())) { // 다만 GM는 예외
			return false;
		}
		return true;
	}*/	
	 private boolean isAttrAgrees() {
		  int magicattr = _skill.getAttr();
		  if (_user instanceof L1NpcInstance) { 
		   return true;
		  }

		  if ((_skill.getSkillLevel() >= 17 && _skill.getSkillLevel() <= 22 && magicattr != 0) 
		    && (magicattr != _player.getElfAttr() 
		    && !_player.isGm())) {
		   return false;
		  }
		  return true;
	}

	/**
	 * 스킬을 사용하기 위해서 필요한 HP가 있을까 돌려준다.
	 * 
	 * @return HP가 충분하면 true
	 */
	private boolean isEnoughHp() {
		return false;
	}

	/**
	 * 스킬을 사용하기 위해서 필요한 MP가 있을까 돌려준다.
	 * 
	 * @return MP가 충분하면 true
	 */
	private boolean isEnoughMp() {
		return false;
	}

	// 필요 HP, MP가 있을까?
	private boolean isHPMPConsume() {
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		if (_user instanceof L1NpcInstance) {
			currentMp = _npc.getCurrentMp();
			currentHp = _npc.getCurrentHp();
		} else {
			currentMp = _player.getCurrentMp();
			currentHp = _player.getCurrentHp();

			// MP의 INT 경감
			if (_player.getInt() > 12
					&& _skillId > HOLY_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // LV2 이상
				_mpConsume--;
			}
			if (_player.getInt() > 13
					&& _skillId > STALAC
					&& _skillId <= FREEZING_BLIZZARD) { // LV3 이상
				_mpConsume--;
			}
			if (_player.getInt() > 14
					&& _skillId > WEAK_ELEMENTAL
					&& _skillId <= FREEZING_BLIZZARD) { // LV4 이상
				_mpConsume--;
			}
			if (_player.getInt() > 15
					&& _skillId > MEDITATION
					&& _skillId <= FREEZING_BLIZZARD) { // LV5 이상
				_mpConsume--;
			}
			if (_player.getInt() > 16
					&& _skillId > DARKNESS
					&& _skillId <= FREEZING_BLIZZARD) { // LV6 이상
				_mpConsume--;
			}
			if (_player.getInt() > 17
					&& _skillId > BLESS_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // LV7 이상
				_mpConsume--;
			}
			if (_player.getInt() > 18
					&& _skillId > DISEASE
					&& _skillId <= FREEZING_BLIZZARD) { // LV8 이상
				_mpConsume--;
			}
			if (_player.getInt() > 19
					&& _skillId > HOLY_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // 인트 20이상
				_mpConsume--;
			}
			if (_player.getInt() > 21
					&& _skillId > HOLY_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // 인트 22이상
				_mpConsume--;
			}
			if (_player.getInt() > 23
					&& _skillId > HOLY_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // 인트 24이상
				_mpConsume--;
			}
			if (_player.getInt() > 24
					&& _skillId > HOLY_WEAPON
					&& _skillId <= FREEZING_BLIZZARD) { // 인트 25이상
				_mpConsume--;
			}
			if (_player.getInt() > 12
					&& _skillId >= SHOCK_STUN && _skillId <= COUNTER_BARRIER) {
				_mpConsume -= (_player.getInt() - 12);
			}			
			if (_player.getInt() > 17
					&& _skillId >= MIRRORIMG && _skillId <= CUBE_BALANCE) { // 환술사
				_mpConsume--;
			}

			// MP의 장비 경감
			if (_skillId == PHYSICAL_ENCHANT_DEX
					&& _player.getInventory().checkEquipped(20013)) { // 신속 헤룸 장비중에 PE:DEX
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20013)) { // 신속 헤룸 장비중에 헤이 파업
				_mpConsume /= 2;
			}
			if (_skillId == HEAL
					&& _player.getInventory().checkEquipped(20014)) { // 치유 헤룸 장비중에 힐
				_mpConsume /= 2;
			}
			if (_skillId == EXTRA_HEAL
					&& _player.getInventory().checkEquipped(20014)) { // 치유 헤룸 장비중에 엑스트라 힐
				_mpConsume /= 2;
			}
			if (_skillId == ENCHANT_WEAPON
					&& _player.getInventory().checkEquipped(20015)) { // 력 헤룸 장비중에 엔챤트 웨폰
				_mpConsume /= 2;
			}
			if (_skillId == DETECTION
					&& _player.getInventory().checkEquipped(20015)) { // 력 헤룸 장비중에 디 텍 숀
				_mpConsume /= 2;
			}
			if (_skillId == PHYSICAL_ENCHANT_STR
					&& _player.getInventory().checkEquipped(20015)) { // 력 헤룸 장비중에 PE:STR
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20008)) { // 마이나윈드헤룸 장비중에 헤이 파업
				_mpConsume /= 2;
			}
			if (_skillId == GREATER_HASTE
					&& _player.getInventory().checkEquipped(20023)) { // 윈드헤룸 장비중에 그레이터 헤이 파업
				_mpConsume /= 2;
			}
			/**매직 핼맷**/
			if (_skillId == PHYSICAL_ENCHANT_DEX
					&& _player.getInventory().checkEquipped(20754)) { // 신속 헤룸 장비중에 PE:DEX
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20754)) { // 신속 헤룸 장비중에 헤이 파업
				_mpConsume /= 2;
			}
			if (_skillId == HEAL
					&& _player.getInventory().checkEquipped(20754)) { // 치유 헤룸 장비중에 힐
				_mpConsume /= 2;
			}
			if (_skillId == EXTRA_HEAL
					&& _player.getInventory().checkEquipped(20754)) { // 치유 헤룸 장비중에 엑스트라 힐
				_mpConsume /= 2;
			}
			if (_skillId == ENCHANT_WEAPON
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸 장비중에 엔챤트 웨폰
				_mpConsume /= 2;
			}
			if (_skillId == DETECTION
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸 장비중에 디 텍 숀
				_mpConsume /= 2;
			}
			if (_skillId == PHYSICAL_ENCHANT_STR
					&& _player.getInventory().checkEquipped(20754)) { // 력 헤룸 장비중에 PE:STR
				_mpConsume /= 2;
			}
			if (0 < _skill.getMpConsume()) { // MP를 소비하는 스킬이면
				_mpConsume = Math.max(_mpConsume, 1); // 최악이어도 1 소비한다.
			}
			// 최초 인트에 의한 MP 소모 감소
			if(_player.getOriginalMagicConsumeReduction() > 0) {
				_mpConsume -= _player.getOriginalMagicConsumeReduction();
			}
		    }

		if (currentHp < _hpConsume + 1) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(279));
			}
			return false;
		} else if (currentMp < _mpConsume) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(278));
			}
			return false;
		}

		return true;
	}

	// 필요 재료가 있을까?
	private boolean isItemConsume() {

		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return true; // 재료를 필요로 하지 않는 마법
		}

		if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false; // 필요 재료가 부족했다.
		}

		return true;
	}

	// 사용 재료, HP·MP, Lawful를 마이너스 한다.
	private void useConsume() {
		if (_user instanceof L1NpcInstance) {
			// NPC의 경우, HP, MP만 마이너스
			int current_hp = _npc.getCurrentHp() - _hpConsume;
			_npc.setCurrentHp(current_hp);

			int current_mp = _npc.getCurrentMp() - _mpConsume;
			_npc.setCurrentMp(current_mp);
			return;
		}

		// HP·MP를 마이너스
		if (isHPMPConsume()) {
			if (_skillId == FINAL_BURN) { // 파이널 반
				_player.setCurrentHp(1);
				_player.setCurrentMp(0);
			} else {
				int current_hp = _player.getCurrentHp() - _hpConsume;
				_player.setCurrentHp(current_hp);

				int current_mp = _player.getCurrentMp() - _mpConsume;
				_player.setCurrentMp(current_mp);
			}
		}

		// Lawful를 마이너스
		int lawful = _player.getLawful() + _skill.getLawful();
		if (lawful > 32767) {
			lawful = 32767;
		}
		if (lawful < -32767) {
			lawful = -32767;
		}
		_player.setLawful(lawful);

		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return; // 재료를 필요로 하지 않는 마법
		}

		// 사용 재료를 마이너스
		_player.getInventory().consumeItem(itemConsume, itemConsumeCount);
	}

	// 매직 리스트에 추가한다.
	private void addMagicList(L1Character cha, boolean repetition) {
		if (_skillTime == 0) {
			_getBuffDuration = _skill.getBuffDuration() * 1000; // 효과 시간
			if (_skill.getBuffDuration() == 0) {
				if (_skillId == INVISIBILITY) { // 인비지비리티
					cha.setSkillEffect(INVISIBILITY, 0);
				}
				return;
			}
		} else {
			_getBuffDuration = _skillTime * 1000; // 파라미터의 time가  0이외라면, 효과 시간으로서 설정한다
		}

		if (_skillId == SHOCK_STUN ) {	
			_getBuffDuration = _shockStunDuration;
		}
		if (_skillId == BONEBREAK) { // 본브레이크
			_getBuffDuration = _bonebreakDuration;
		}
		if (_skillId == ARMBREAKER) { // 암브레이커
			_getBuffDuration = _armDuration;
		}

		if (_skillId == CURSE_POISON 
			|| _skillId == Mob_CURSPOISON_30 || _skillId == Mob_CURSPOISON_18  
			) { // 카즈포이즌의 효과 처리는 L1Poison에 이양.
			return;
		}
		if (_skillId == CURSE_PARALYZE
			|| _skillId == CURSE_PARALYZE2
			|| _skillId == Mob_CURSEPARALYZ_19 || _skillId == Mob_CURSEPARALYZ_18 
			|| _skillId == Mob_CURSEPARALYZ_30 || _skillId == Mob_CURSEPARALYZ_SHORT_18	
			) { // 카즈파라라이즈의 효과 처리는 L1CurseParalysis에 이양.
			return;
		}
		if (_skillId == SHAPE_CHANGE) {
			return; 
		} 
		if (_skillId == BLESSED_ARMOR || _skillId == HOLY_WEAPON // 무기·방어용 기구에 효과가 있는 처리는 L1ItemInstance에 이양.
				|| _skillId == ENCHANT_WEAPON || _skillId == BLESS_WEAPON
				|| _skillId == SHADOW_FANG) {
			return;
		}
		if ((_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD
		|| _skillId == Mob_AREA_ICE_LANCE || _skillId == Mob_CALL_LIGHTNING_ICE	 		
		)
				&& !_isFreeze) { // 동결 실패
			return;
		}
		cha.setSkillEffect(_skillId, _getBuffDuration);

		if (cha instanceof L1PcInstance && repetition) { // 대상이 PC로 이미 스킬이 중복 하고 있는 경우
			L1PcInstance pc = (L1PcInstance) cha;
			sendIcon(pc);
		}
	}

	// 아이콘의 송신
	private void sendIcon(L1PcInstance pc) {
		if (_skillTime == 0) {
			_getBuffIconDuration = _skill.getBuffDuration(); // 효과 시간
		} else {
			_getBuffIconDuration = _skillTime; // 파라미터의 time가  0이외라면, 효과 시간으로서 설정한다
		}

		if (_skillId == SHIELD) { // 쉴드(shield)
			pc.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
		} else if (_skillId == SHADOW_ARMOR) { // 그림자 아모
			pc.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
		} else if (_skillId == DRESS_DEXTERITY) { // 드레스데크스타리티
			pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
		} else if (_skillId == DRESS_MIGHTY) { // 드레스마이티
			pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
		} else if (_skillId == GLOWING_AURA) { // 그로윙오라
			pc.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
		} else if (_skillId == SHINING_AURA) { // 샤이닝오라
			pc.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
		} else if (_skillId == BRAVE_AURA) { // 치우침 이브 아우라
			pc.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
		} else if (_skillId == FIRE_WEAPON) { // 파이아웨폰
			pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
		} else if (_skillId == WIND_SHOT) { // 윈도우 쇼트
			pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
		} else if (_skillId == FIRE_BLESS) { // 파이어 호흡
			pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
		} else if (_skillId == STORM_EYE) { // 스토무아이
			pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
		} else if (_skillId == EARTH_BLESS) { // 지구 호흡
			pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
		} else if (_skillId == BURNING_WEAPON) { // 바닝웨폰
			pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
		} else if (_skillId == STORM_SHOT) { // 스톰 쇼트
			pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
		} else if (_skillId == IRON_SKIN) { // 아이언 스킨
			pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
		} else if (_skillId == EARTH_SKIN) { // 지구 스킨
			pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
		} else if (_skillId == PHYSICAL_ENCHANT_STR) { // 피지컬 엔챤트：STR
			pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
		} else if (_skillId == PHYSICAL_ENCHANT_DEX) { // 피지컬 엔챤트：DEX
			pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
		} else if (_skillId == HASTE || _skillId == GREATER_HASTE) { // 그레이터 헤이 파업
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
		} else if (_skillId == HOLY_WALK || _skillId == MOVING_ACCELERATION 
			|| _skillId == WIND_WALK) { // 호-리 워크, 무빙 악 세레이션, 윈드워크
			pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
		} else if (_skillId == BLOODLUST) {  
			pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
		} else if (_skillId == SLOW || _skillId == MASS_SLOW || _skillId == ENTANGLE
			|| _skillId == Mob_SLOW_1 || _skillId == Mob_SLOW_18) { // 슬로우, 엔탕르, 매스 슬로우
			pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 2, 0));
		} else if (_skillId == IMMUNE_TO_HARM) {
			pc.sendPackets(new S_SkillIconGFX(40, _getBuffIconDuration));
		}
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

	// 그래픽의 송신
	private void sendGrfx(boolean isSkillAction) {
		int actionId = _skill.getActionId();
		int castgfx = _skill.getCastGfx();
		int castgfx2 = _skill.getCastGfx2();
		if (castgfx == 0 && castgfx2 == 0) {
			return; // 표시하는 그래픽이 없다
		}
// 발라카스 범위 파이어월, 독구름 추가 
		if (_user instanceof L1NpcInstance) {
			if (_skillId == Mob_AREA_FIRE_WALL) {
				L1NpcInstance Npc = (L1NpcInstance) _user;
				S_DoActionGFX gfx = new S_DoActionGFX(Npc.getId(), 18);
				Npc.broadcastPacket(gfx);
				return;
			}
		}
		if (_user instanceof L1NpcInstance) {
			if (_skillId == Mob_AREA_POISON_18 || _skillId == Mob_AREA_POISON_30
				|| _skillId == Mob_AREA_POISON || _skillId == Mob_AREA_POISON_20
				|| _skillId == ANTA_SKILL_6 || _skillId == ANTA_SKILL_7 || _skillId == ANTA_SKILL_10) {
				L1NpcInstance Npc = (L1NpcInstance) _user;
				S_DoActionGFX gfx = new S_DoActionGFX(Npc.getId(), actionId);
				Npc.broadcastPacket(gfx);
				return;
			}
		}
	
// 발라카스 범위 파이어월, 독구름 추가 - 몹스킬패턴 추가 : 끝
		if (_user instanceof L1PcInstance) {
			if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM) {
				L1PcInstance pc = (L1PcInstance) _user;
				if (_skillId == FIRE_WALL) {
					pc.setHeading(pc.targetDirection(_targetX, _targetY));
					pc.sendPackets(new S_ChangeHeading(pc));
					pc.broadcastPacket(new S_ChangeHeading(pc));
				}
				S_DoActionGFX gfx = new S_DoActionGFX(pc.getId(), actionId);
				pc.sendPackets(gfx);
				pc.broadcastPacket(gfx);
				return;
			}

			int targetid = _target.getId();

			if (_skillId == SHOCK_STUN //쇼크 스턴 실패시	
					|| _skillId == Mob_SHOCKSTUN_18	|| _skillId == Mob_RANGESTUN_30	
					|| _skillId == Mob_RANGESTUN_19 || _skillId == Mob_RANGESTUN_18			
                    || _skillId == ANTA_SKILL_3 || _skillId == ANTA_SKILL_4 || _skillId == ANTA_SKILL_5) {
				 if (_target instanceof L1PcInstance) { 
	                  L1PcInstance pc = (L1PcInstance) _target; 
	           pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
	           pc.broadcastPacket(new S_SkillSound(pc.getId(), 4434));
	          } else if (_target instanceof L1NpcInstance) {
	           _target.broadcastPacket(new S_SkillSound(_target
	             .getId(), 4434));
	          }
	          return;
	        }
		
			/**
			 * 용기사 버프마법
			 */
			if (_skillId == LIGHT) {
				L1PcInstance pc = (L1PcInstance) _target;
				pc.sendPackets(new S_Sound(145));
			}

			if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
				// 타겟수가 0으로 대상을 지정하는 스킬의 경우, 마법 사용 효과만 표시해 종료
				int tempchargfx = _player.getTempCharGfx();
				if (tempchargfx == 5727 || tempchargfx == 5730) { // 그림자계 변신의 모션 대응
					actionId = ActionCodes.ACTION_SkillBuff;
				} else if (tempchargfx == 5733 || tempchargfx == 5736) {
					actionId = ActionCodes.ACTION_Attack;
				}
				if (isSkillAction) {
					S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),actionId);
					_player.sendPackets(gfx);
					_player.broadcastPacket(gfx);
				}
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (isPcSummonPet(_target)) { // 대상이 PC, 사몬, 애완동물
					if (_player.getZoneType() == 1
							|| _target.getZoneType() == 1 // 공격하는 측 또는 공격받는 측이 세이프티 존
							|| _player.checkNonPvP(_player, _target)) { // Non-PvP 설정
						_player.sendPackets(new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId)); // 타겟에의 모션은 없음
						_player.broadcastPacket(new S_UseAttackSkill(_player, 0, castgfx, _targetX, _targetY, actionId));
					if (castgfx2 > 0){
						_player.sendPackets(new S_UseAttackSkill(_player, 0, castgfx2, _targetX, _targetY, actionId)); 
						_player.broadcastPacket(new S_UseAttackSkill(_player, 0, castgfx2, _targetX, _targetY, actionId));
						}
						return;
					}
				}

				if (_skill.getArea() == 0) { // 단체 공격 마법
					_player.sendPackets(new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId));
					_player.broadcastPacket(new S_UseAttackSkill(_player, targetid, castgfx, _targetX, _targetY, actionId));
				if (castgfx2 > 0){
					_player.sendPackets(new S_UseAttackSkill(_player, targetid,	castgfx2, _targetX, _targetY, actionId));
					_player.broadcastPacket(new S_UseAttackSkill(_player, targetid, castgfx2, _targetX, _targetY, actionId));
					}
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(targetid, ActionCodes.ACTION_Damage), _player);
				} else { // 유방향 범위 공격 마법} else { // 유방향 범위 공격 마법
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						i++;
					}
					_player.sendPackets(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR));
					_player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR));
				if (castgfx2 > 0){
					_player.sendPackets(new S_RangeSkill(_player, cha, castgfx2, actionId, S_RangeSkill.TYPE_DIR));
					_player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx2, actionId, S_RangeSkill.TYPE_DIR));
					}
				}
			} else if (_skill.getTarget().equals("none") && _skill.getType() == L1Skills.TYPE_ATTACK) { // 무방향 범위 공격 마법
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					cha[i].broadcastPacketExceptTargetSight(new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage),
					_player);
					i++;
				}
				    _player.sendPackets(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
				    _player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
				if (castgfx2 > 0){
					_player.sendPackets(new S_RangeSkill(_player, cha, castgfx2, actionId, S_RangeSkill.TYPE_NODIR));
					_player.broadcastPacket(new S_RangeSkill(_player, cha, castgfx2, actionId, S_RangeSkill.TYPE_NODIR));
				}
			} else { // 보조 마법
				// 텔레포트, 매스 텔레, 테레포트트마자 이외
				if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
					// 마법을 사용하는 동작의 효과는 사용자만
					if (isSkillAction) {
						S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),
						_skill.getActionId());
						_player.sendPackets(gfx);
						_player.broadcastPacket(gfx);
					}
					if (_skillId == COUNTER_MAGIC
							|| _skillId == COUNTER_BARRIER
							|| _skillId == COUNTER_MIRROR) {
						_player.sendPackets(new S_SkillSound(targetid, castgfx));
						_player.broadcastPacket(new S_SkillSound(targetid, castgfx));
					if (castgfx2 > 0){
						_player.sendPackets(new S_SkillSound(targetid, castgfx2));		// 이펙트2
						_player.broadcastPacket(new S_SkillSound(targetid, castgfx2));
						}
					} else if (_skillId == TRUE_TARGET) { // 트루 타겟은 개별 처리로 송신제
						return;
					} else {
						_player.sendPackets(new S_SkillSound(targetid, castgfx));
						_player.broadcastPacket(new S_SkillSound(targetid, castgfx));
					}
				}

				// 스킬의 효과 표시는 타겟 전원이지만, 그다지 필요성이 없기 때문에, 스테이터스만 송신
				for (TargetStatus ts : _targetList) {
					L1Character cha = ts.getTarget();
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				}
			}
		} else if (_user instanceof L1NpcInstance) { // NPC가 스킬을 사용했을 경우
			int targetid = _target.getId();

			if (_user instanceof L1MerchantInstance) {
				_user.broadcastPacket(new S_SkillSound(targetid, castgfx));
			if (castgfx2 > 0){
				_user.broadcastPacket(new S_SkillSound(targetid, castgfx2));	// 이펙트2
				}
				return;
			}

			if (_targetList.size() == 0 && !(_skill.getTarget()
				.equals("none"))) {
				// 타겟수가 0으로 대상을 지정하는 스킬의 경우, 마법 사용 효과만 표시해 종료
				S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill.getActionId());
				_user.broadcastPacket(gfx);
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (_skill.getArea() == 0) { // 단체 공격 마법
					_user.broadcastPacket(new S_UseAttackSkill(_user, targetid, castgfx, _targetX, _targetY, actionId));
				if (castgfx2 > 0){
					_user.broadcastPacket(new S_UseAttackSkill(_user, targetid, castgfx2, _targetX, _targetY, actionId));
					}
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							targetid, ActionCodes.ACTION_Damage), _user);
				} else { // 유방향 범위 공격 마법
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						cha[i].broadcastPacketExceptTargetSight(
						new S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage), _user);
						i++;
					}
					_user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx, actionId, S_RangeSkill.TYPE_DIR));
					if (castgfx2 > 0){
						_user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx2, actionId, S_RangeSkill.TYPE_DIR));
					}
					if (_skillId == 10195){	// 모션 따로 주기 위해
						_user.broadcastPacket(new S_UseAttackSkill(_user, targetid, castgfx, _targetX, _targetY, 19));	// 안타라스(리뉴얼) 큰이럽모션
					}
					if (_skillId == 10196){	// 모션 따로 주기 위해
						_user.broadcastPacket(new S_UseAttackSkill(_user, targetid, castgfx, _targetX, _targetY, 12));	// 안타라스(리뉴얼) 왼손공격
					}
				}
			} else if (_skill.getTarget().equals("none") && _skill.getType() ==
					L1Skills.TYPE_ATTACK) { // 무방향 범위 공격 마법
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					i++;
				}
				    _user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx, actionId, S_RangeSkill.TYPE_NODIR));
				if (castgfx2 > 0){
					_user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx2, actionId, S_RangeSkill.TYPE_NODIR));
				}
			} else { // 보조 마법
				// 텔레포트, 매스 텔레, 테레포트트마자 이외
				if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
					// 마법을 사용하는 동작의 효과는 사용자만
					S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), _skill.getActionId());
					_user.broadcastPacket(gfx);
					_user.broadcastPacket(new S_SkillSound(targetid, castgfx));
					if (castgfx2 > 0){
						_user.broadcastPacket(new S_SkillSound(targetid, castgfx2));	// 이펙트2
					}
				}
			}
		}
	}

	// 중복 할 수 없는 스킬의 삭제
	// 예：파이아웨폰과 바닝웨폰 등
	private void deleteRepeatedSkills(L1Character cha) {
		final int[][] repeatedSkills = {
				// 호리웨폰, 엔체트웨폰, 브레스웨폰, 샤드우팡
				// 이것들은 L1ItemInstance로 관리
          //    { HOLY_WEAPON, ENCHANT_WEAPON, BLESS_WEAPON, SHADOW_FANG },
				// 파이아웨폰, 윈도우 쇼트, 파이어 호흡, 스토무아이, 바닝웨폰, 스톰 쇼트
				{ FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE, BURNING_WEAPON, STORM_SHOT },
				// 쉴드(shield), 그림자 아모, 지구 스킨, 지구 호흡, 아이언 스킨
				{ SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
				// 호-리 워크, 무빙 악 세레이션, 윈드워크, BP
				{ HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE, /*BLOOD_LUST,*/ STATUS_RIBRAVE },
				// 헤이 파업, 그레이터 헤이 파업, GP
				{ HASTE, GREATER_HASTE, STATUS_HASTE },
				// 피지컬 엔챤트：DEX, 드레스데크스타리티
				{ PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY },
				// 피지컬 엔챤트：STR, 드레스마이티
				{ PHYSICAL_ENCHANT_STR, DRESS_MIGHTY },
				// 그로윙오라, 샤이닝오라
				{ GLOWING_AURA, SHINING_AURA }, 
				// 안타, 파프, 발라
				{ ANTARAS , PAPORION , BALAKAS},
				// 마안
				{ FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN, BIRTH_MAAN, SHAPE_MAAN }};


		for (int[] skills : repeatedSkills) {
			for (int id : skills) {
				if (id == _skillId) {
					stopSkillList(cha, skills);
				}
			}
		}
	}

	// 중복 하고 있는 스킬을 일단 모두 삭제
	private void stopSkillList(L1Character cha, int[] repeat_skill) {
		for (int skillId : repeat_skill) {
			if (skillId != _skillId) {
				cha.removeSkillEffect(skillId);
			}
		}
	}

	// 지연의 설정
	private void setDelay() {
   //**스킬 딜레이 타이머 설정 **// 
       long nowtime = System.currentTimeMillis();
       if(_user.getSkilldelay2() <=  nowtime ){
        _user.setSkilldelay2( nowtime + _skill.getReuseDelay() );  
         } else {
        _user.setSkilldelay2( nowtime + _skill.getReuseDelay() );
         }
	 }
   //**스킬 딜레이 타이머 설정 **// 

 private void runSkill() {

		if (_skillId == LIFE_STREAM) {
			L1EffectSpawn.getInstance().spawnEffect(81169,
					_skill.getBuffDuration() * 1000,
					_targetX, _targetY, _user.getMapId());
			return;
		}

		if (_skillId == FIRE_WALL) { // 파이어월
			L1EffectSpawn.getInstance()
					.doSpawnFireWall(_user, _targetX, _targetY);
			return;
		}

// 발라카스 범위 파이어월, 독구름 마법 추가 - 몹스킬패턴 추가 : 시작
		if (_skillId == Mob_AREA_FIRE_WALL) {		// 범위 파이어월
			// 발라카스 파이어월 본섭 스타일 - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			/// 랜덤으로 0-2픽셀 거리변경사용
			Random random = new Random(); 
			int randomxy = random.nextInt(5);  
			int r = random.nextInt(2) + 1;
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getHeading(); //몹 방향
			switch (heading){
			case 1: 
				xx = a1 - r; yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				break;
			case 2:
				xx = a1 + 1;
				xx1 = b1; yy1 = a2;
				xx2 = b1; yy2 = a1;
				break;
			case 3:
				xx = a1 - r; yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				break;
			case 4:
				yy = a1 + 1;
				xx1 = a1; yy1 = b1;
				xx2 = a2; yy2 = b1;
				break;
			case 5:
				xx = a2 + r; yy = a1 - r;
				yy1 = a1; 
				xx2 = a2;
				break;
			case 6:
				xx = a2 - 1;
				xx1 = b2; yy1 = a1;
				xx2 = b2; yy2 = a2; 
				break;
			case 7:
				xx = a2 + r; yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				break;
			case 0:
				yy = a2 - 1;
				xx1 = a2; yy1 = b2;
				xx2 = a1; yy2 = b2;
				break;
			default:
			break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			//마름모 4*4픽셀 모양 (발라카스 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x - 1, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x - 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x - 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x - 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 2, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 2, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 2, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x + 2, y + 1, _user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1 - 1;
			//마름모 4*4픽셀 모양 (발라카스 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 - 1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 - 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 2, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x1 + 2, y1 + 1, _user.getMapId());
			int x2 = _npc.getX() + xx2 + 1;
			int y2 = _npc.getY() + yy2;
			//마름모 4*4픽셀 모양 (발라카스 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 - 1, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 - 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 1, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(81157, _skill.getBuffDuration() * 1000, x2 + 2, y2 + 1, _user.getMapId());
			// 발라카스 파이어월 본섭 스타일 - 끝
	
			return;
		}
		if (_skillId == Mob_AREA_POISON_18 || _skillId == Mob_AREA_POISON_30
			|| _skillId == Mob_AREA_POISON){ 
			//범위 독구름(안타라스, 제니스퀸류, 머미로드류, 오염엔트, 오염펑거스)
			// 독구름 본섭 스타일(몹 정면으로 3방향) - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			/// 랜덤으로 0-2픽셀 거리변경사용
			Random random = new Random(); 
			int randomxy = random.nextInt(3);
			int r = random.nextInt(2) + 1;
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getHeading(); //몹 방향
			switch (heading){
			case 1: 
				xx = a1 - r; yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				break;
			case 2:
				xx = a1 + 1;
				xx1 = b1; yy1 = a2;
				xx2 = b1; yy2 = a1;
				break;
			case 3:
				xx = a1 - r; yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				break;
			case 4:
				yy = a1 + 1;
				xx1 = a1; yy1 = b1;
				xx2 = a2; yy2 = b1;
				break;
			case 5:
				xx = a2 + r; yy = a1 - r;
				yy1 = a1; 
				xx2 = a2;
				break;
			case 6:
				xx = a2 - 1;
				xx1 = b2; yy1 = a1;
				xx2 = b2; yy2 = a2; 
				break;
			case 7:
				xx = a2 + r; yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				break;
			case 0:
				yy = a2 - 1;
				xx1 = a2; yy1 = b2;
				xx2 = a1; yy2 = b2;
				break;
			default:
			break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			//마름모 4*4픽셀 모양 (몹 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y + 1, _user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1;
			//마름모 4*4픽셀 모양 (몹 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 + 1, _user.getMapId());
			int x2 = _npc.getX() + xx2;
			int y2 = _npc.getY() + yy2;
			//마름모 4*4픽셀 모양 (몹 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 + 1, _user.getMapId());
			// 독구름 본섭 스타일(몹 정면으로 3방향 사용) - 끝

			return;
		}
		/** 용언 **/
		if (_skillId == Mob_AREA_POISON_20	//범위 독구름 5뭉치(안타라스 리뉴얼)
			|| _skillId == ANTA_SKILL_6 || _skillId == ANTA_SKILL_7 || _skillId == ANTA_SKILL_10 // 안타라스 용언
			){ 
			int npcId = _npc.getNpcTemplate().get_npcId();
			if (npcId == 777775 || npcId == 777776 || npcId == 777779){	// 안타라스(리뉴얼)1,2,3단계
				if (_skillId == ANTA_SKILL_6) {	// 안타라스(리뉴얼) - 용언 스킬6 
					_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7862", 0));
						// 오브 모크! 리라프[웨폰브레이크 + 브레스(독구름)]
					if (_user instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _user;
						L1ItemInstance weapon = pc.getWeapon();
						 Random random = new Random();
			    	      int rnd = random.nextInt(100) + 1;
			    	      if (weapon != null && rnd <= (100 - pc.getStr())/10) {//확률 :힘영향           
							int weaponDamage = _random.nextInt(3) + 1;
							pc.sendPackets(new S_ServerMessage(268, weapon.getLogName())); 
							pc.getInventory().receiveDamage(weapon, weaponDamage);
						}
					}
				}				
				if (_skillId == ANTA_SKILL_7) {	// 안타라스(리뉴얼) - 용언 스킬7
					_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7911", 0));
						// 오브 모크! 켄 로우[독구름 + 브레스]
				}
				if (_skillId == ANTA_SKILL_10) {	// 안타라스(리뉴얼) - 용언 스킬10
					_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7863", 0));
						// 오브 모크! 루오 타[독구름 + 이럽브레스]
				}
			}

			// 독구름 본섭 스타일(몹 정면으로 5방향) - 시작
			int xx = 0;
			int yy = 0;
			int xx1 = 0;
			int yy1 = 0;
			int xx2 = 0;
			int yy2 = 0;
			int xx3 = 0;
			int yy3 = 0;
			int xx4 = 0;
			int yy4 = 0;
			int randomxy = _random.nextInt(4);		/// 랜덤으로 0-4픽셀 거리변경사용
			int r = _random.nextInt(2) + 1;	//(1~2)
			int a1 = 3 + randomxy;
			int a2 = -3 - randomxy;
			int b1 = 2 + randomxy;
			int b2 = -2 - randomxy;
			int heading = _npc.getHeading(); //몹 방향
			switch (heading){
			case 1:			// 12시 방향
				xx = a1 - r; yy = a2 + r;
				yy1 = a2;
				xx2 = a1;
				xx3 = a2; yy3 = b2;
				xx4 = b1; yy4 = a1;
				break;
			case 2:			// 1-2시 방향
				xx = a1 + 1;
				xx1 = b1; yy1 = a2;
				xx2 = b1; yy2 = a1;
				xx3 = b1 - 3; yy3 = a2 - 2;
				xx4 = b1 - 2; yy4 = a1 + 3;
				break;
			case 3:			// 3시 방향
				xx = a1 - r; yy = a1 - r;
				xx1 = a1;
				yy2 = a1;
				xx3 = a1; yy3 = a2;
				xx4 = a2; yy4 = b1;
				break;
			case 4:			// 4-5시 방향
				yy = a1 + 1;
				xx1 = a1; yy1 = b1;
				xx2 = a2; yy2 = b1;
				xx3 = a1 + 3; yy3 = b1 - 3;
				xx4 = a2 - 3; yy4 = b1 - 3;
				break;
			case 5:			// 6시 방향
				xx = a2 + r; yy = a1 - r;
				yy1 = a1; 
				xx2 = a2;
				xx3 = a1; yy3 = b1;
				xx4 = b2; yy4 = a2;
				break;
			case 6:			// 7-8시 방향
				xx = a2 - 1;
				xx1 = b2; yy1 = a1;
				xx2 = b2; yy2 = a2; 
				xx3 = b2 + 3; yy3 = a1 + 2;
				xx4 = b2 + 2; yy4 = a2 - 3;
				break;
			case 7:			// 9시 방향
				xx = a2 + r; yy = a2 + r;
				xx1 = a2;
				yy2 = a2;
				xx3 = a2; yy3 = a1;
				xx4 = a1; yy4 = b2;
				break;
			case 0:			// 10-11시 방향
				yy = a2 - 1;
				xx1 = a2; yy1 = b2;
				xx2 = a1; yy2 = b2;
				xx3 = a2 - 3; yy3 = b2 + 3;
				xx4 = a1 + 3; yy4 = b2 + 3;
				break;
			default:
			break;
			}
			int x = _npc.getX() + xx;
			int y = _npc.getY() + yy;
			//마름모 4*4픽셀 모양 (몹 기준에서 정면에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x - 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 1, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x + 2, y + 1, _user.getMapId());
			int x1 = _npc.getX() + xx1;
			int y1 = _npc.getY() + yy1;
			//마름모 4*4픽셀 모양 (몹 기준에서 좌측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 - 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 1, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x1 + 2, y1 + 1, _user.getMapId());
			int x2 = _npc.getX() + xx2;
			int y2 = _npc.getY() + yy2;
			//마름모 4*4픽셀 모양 (몹 기준에서 우측에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 - 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 1, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x2 + 2, y2 + 1, _user.getMapId());
			int x3 = _npc.getX() + xx3;
			int y3 = _npc.getY() + yy3;
			//마름모 4*4픽셀 모양 (몹 기준에서 좌측2에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3, y3, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3, y3 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3, y3 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3, y3 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 - 1, y3, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 - 1, y3 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 - 1, y3 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 - 1, y3 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 1, y3 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 1, y3 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 1, y3, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 1, y3 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 2, y3 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 2, y3 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 2, y3, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x3 + 2, y3 + 1, _user.getMapId());
			int x4 = _npc.getX() + xx4;
			int y4 = _npc.getY() + yy4;
			//마름모 4*4픽셀 모양 (몹 기준에서 우측2에 출현)
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4, y4, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4, y4 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4, y4 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4, y4 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 - 1, y4, _user.getMapId());			
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 - 1, y4 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 - 1, y4 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 - 1, y4 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 1, y4 + 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 1, y4 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 1, y4, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 1, y4 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 2, y4 - 2, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 2, y4 - 1, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 2, y4, _user.getMapId());
			L1EffectSpawn.getInstance().spawnEffect(777777, _skill.getBuffDuration() * 1000, x4 + 2, y4 + 1, _user.getMapId());
			// 독구름 본섭 스타일(몹 정면으로 5방향 사용) - 끝
			return;
		} 
		/////////////////////// 발라카스 범위 파이어월, 독구름 마법 추가 - 몹스킬패턴 추가 : 끝

		// 카운터 매직유/무효의 설정
		for (int skillId : EXCEPT_COUNTER_MAGIC) {
			if (_skillId == skillId) {
				_isCounterMagic = false; // 카운터 매직 무효
				break;
			}
		}

		// NPC에 쇼크 스탠을 사용시키면(자) onAction로 NullPointerException가 발생하기 위해(때문에)
		// 우선 PC가 사용했을 때 마셔
		if (_skillId == SHOCK_STUN && _user instanceof L1PcInstance) {
			_target.onAction(_player);
		}
		if (_skillId == BONEBREAK && _user instanceof L1PcInstance) { // 본브레이크
			_target.onAction(_player);
		}


		if (!isTargetCalc(_target)) {
			return;
		}

		try {
			TargetStatus ts = null;
			L1Character cha = null;
			int dmg = 0;
			int drainMana = 0;
			int heal = 0;
			boolean isSuccess = false;
			int undeadType = 0;

			for (Iterator<TargetStatus> iter = _targetList.iterator(); iter
					.hasNext();) {
				ts = null;
				cha = null;
				dmg = 0;
				heal = 0;
				isSuccess = false;
				undeadType = 0;

				ts = iter.next();
				cha = ts.getTarget();

				if (!ts.isCalc() || !isTargetCalc(cha)) {
					continue; // 계산할 필요가 없다.
				}

				L1Magic _magic = new L1Magic(_user, cha);
				_magic.setLeverage(getLeverage());

				if (cha instanceof L1MonsterInstance) { // 안 뎁트의 판정
					undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
							.get_undead();
				}

				// 확률계 스킬로 실패가 확정되어 있는 경우
				if ((_skill.getType() == L1Skills.TYPE_CURSE || _skill
						.getType() == L1Skills.TYPE_PROBABILITY)
						&& isTargetFailure(cha)) {
					iter.remove();
					continue;
				}

				if (cha instanceof L1PcInstance) { // 타겟이 PC의 경우만 아이콘은 송신한다.
					if (_skillTime == 0) {
						_getBuffIconDuration = _skill.getBuffDuration(); // 효과 시간
					} else {
						_getBuffIconDuration = _skillTime; // 파라미터의 time가  0이외라면, 효과 시간으로서 설정한다
					}
				}

				deleteRepeatedSkills(cha); // 중복 한 스킬의 삭제

				if (_skill.getType() == L1Skills.TYPE_ATTACK
						&& _user.getId() != cha.getId()) { // 공격계 스킬＆타겟이 사용자 이외인 것.
					if (isUseCounterMagic(cha)) { // 카운터 매직이 발동했을 경우, 리스트로부터 삭제
						iter.remove();
						continue;
					}
					dmg = _magic.calcMagicDamage(_skillId);
					//cha.removeSkillEffect(ERASE_MAGIC); // erase 매직중이라면, 공격 마법으로 해제
					if (cha instanceof L1PcInstance) {
						 if(cha.hasSkillEffect(ERASE_MAGIC)){
						  cha.killSkillEffectTimer(ERASE_MAGIC);
						  L1PcInstance pc = (L1PcInstance) cha;
						  pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_AURA));
						  }
						 } else if (cha instanceof L1MonsterInstance) {
						  if(cha.hasSkillEffect(ERASE_MAGIC)){
						   cha.killSkillEffectTimer(ERASE_MAGIC);
						  }
						 }
				  //    }
				} else if (_skill.getType() == L1Skills.TYPE_CURSE
						|| _skill.getType() == L1Skills.TYPE_PROBABILITY) { // 확률계 스킬
					isSuccess = _magic.calcProbabilityMagic(_skillId);
		
					if (_skillId != FOG_OF_SLEEPING) {
						cha.removeSkillEffect(FOG_OF_SLEEPING); // fog 오브 슬리핑중이라면, 확률 마법으로 해제
					}
					if (isSuccess) { // 성공했지만 카운터 매직이 발동했을 경우, 리스트로부터 삭제
						if (isUseCounterMagic(cha)) { // 카운터 매직이 발동했는지
							iter.remove();
							continue;
						}
					} else { // 실패했을 경우, 리스트로부터 삭제
						if (_skillId == FOG_OF_SLEEPING
								&& cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_ServerMessage(297)); // 가벼운 현기증을 기억했습니다.
						}
						iter.remove();
						continue;
					}
				} else if (_skill.getType() == L1Skills.TYPE_HEAL) { // 회복계 스킬
					// 회복량은 마이너스 데미지로 표현
					dmg = -1 * _magic.calcHealing(_skillId);
					if (cha.hasSkillEffect(WATER_LIFE)) { // 워터 라이프중은 회복량 2배
						dmg *= 2;
					}
					if (cha.hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은 회복량1/2배
						dmg /= 2;
					}
				}

				// ■■■■ 개별 처리가 있는 스킬만 써 주세요. ■■■■

				// 벌써 스킬을 사용이 끝난 경우 아무것도 하지 않는다
				// 다만 쇼크 스탠은 겹침 벼랑 할 수 있기 (위해)때문에 예외
				if (cha.hasSkillEffect(_skillId) && _skillId != SHOCK_STUN
						&& _skillId != BONEBREAK) {   // 본브레이크 추가
					addMagicList(cha, true); // 타겟으로 마법의 효과 시간을 덧쓰기
					if (_skillId != SHAPE_CHANGE) { // 셰이프 체인지는 변신을 덧쓰기 할 수 있기 (위해)때문에 예외
						continue;
					}
				}

				// ●●●● PC, NPC 양쪽 모두 효과가 있는 스킬 ●●●●
				/** 
 				*  용기사 버프AND NONE
 				*/
		
				            if(_skillId == ANTARAS){ //각성 : 안타라스
								L1PcInstance pc = (L1PcInstance) cha;
								L1PolyMorph.doPoly(pc, 6894, 20, L1PolyMorph.MORPH_BY_NPC);
								pc.addAc(-15);
								pc.addMaxHp(127);
							} else if(_skillId == PAPORION){ //각성 : 파푸리온
								L1PcInstance pc = (L1PcInstance) cha;
								L1PolyMorph.doPoly(pc, 6894, 20, L1PolyMorph.MORPH_BY_NPC);
								pc.addMr(30);
								pc.addFire(30);
								pc.addWind(30);
								pc.addEarth(30);
								pc.addWater(30);
							} else if(_skillId == BALAKAS){ //각성 : 발라카스
								L1PcInstance pc = (L1PcInstance) cha;
								L1PolyMorph.doPoly(pc, 6894, 20, L1PolyMorph.MORPH_BY_NPC);
								pc.addWis((byte) 5);
								pc.addDex((byte) 5);
								pc.addStr((byte) 5);
								pc.addInt((byte) 5);
								pc.addCon((byte) 5);
					    	/*환술사:일루젼 오거 */
							} else if (_skillId == OUGU) {  
								   if (!(cha instanceof L1PcInstance)){
							         return; 
							        } 
							        L1PcInstance pc = (L1PcInstance) cha;
									pc.addDmgup(4);
									pc.addHitup(4);
							/*환술사:일루션 리치*/
							} else if (_skillId == RICH) {  
								   if (!(cha instanceof L1PcInstance)){
							         return; 
							        } 
							        L1PcInstance pc = (L1PcInstance) cha;
									pc.addSp(2);
									pc.sendPackets(new S_SPMR(pc));
							/*환술사:일루션 다이아골렘 */
							} else if (_skillId == DIAGOLEM) {  
								   if (!(cha instanceof L1PcInstance)){
							         return; 
							        } 
							        L1PcInstance pc = (L1PcInstance) cha;
									pc.addAc(-20);
							/*환술사:일루젼 아바타*/
							} else if (_skillId == AVATA) {
								   if (!(cha instanceof L1PcInstance)){
							         return; 
							        } 
							        L1PcInstance pc = (L1PcInstance) cha;
									pc.addDmgup(10);
									pc.addSp(6);
									pc.sendPackets(new S_SPMR(pc));
									 /*코마 버프사  */
						     } else if (_skillId == COMA) { 
                                    if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addHitup(3);
                                    pc.addBowHitup(3);
                                    pc.addStr(5);
                                    pc.addDex(5);
                                    pc.addCon(1);
                                    pc.addAc(-3);
                                    }
                                    /*코마 버프사  */
                              } else if (_skillId == COMABUFF) { 
                                    if (cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addHitup(5);
                                    pc.addBowHitup(5);
                                    pc.addStr(5);
                                    pc.addDex(5);
                                    pc.addCon(1);
								    pc.addAc(-8);
                                    pc.addSp(1);
      								pc.sendPackets(new S_SPMR(pc));
                                    } 
									// 상아탑 버프
                               } else if (_skillId == SANGA) {
                                    if(cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addHitup(5);
                                    pc.addDmgup(10);
                                    pc.addBowHitup(5);
                                    pc.addBowDmgup(10);
                                    pc.addStr(3);
                                    pc.addDex(3);
                                    pc.addInt(3);
                                    pc.addCon(3);
                                    pc.addWis(3);
                                    pc.addSp(3);
                                    pc.sendPackets(new S_SPMR(pc));
                                    }
                                } else if (_skillId == SANGABUFF) {
                                    if(cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addHitup(5);
                                    pc.addDmgup(5);
                                    pc.addBowHitup(5);
                                    pc.addBowDmgup(5);
                                    pc.addStr(2);
                                    pc.addDex(2);
                                    pc.addInt(2);
                                    pc.addCon(2);
                                    pc.addWis(2);
                                    pc.addSp(1);
                                    pc.sendPackets(new S_SPMR(pc));
                                    }	
                                 // 상아탑 버프 
                                 // 크레이  혈흔
                                } else if (_skillId == CRAY) {
                                    if(cha instanceof L1PcInstance) {
                                  L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addHitup(5);
                                    pc.addDmgup(1);
                                    pc.addBowHitup(5);
                                    pc.addBowDmgup(1);
                                    pc.addEarth(30);
                                    pc.addMaxHp(100);
                                    pc.addMaxMp(50);
                                    pc.addHpr(3);
                                    pc.addMpr(3);
                                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                                    pc.sendPackets(new S_SPMR(pc));
                                    }
                                  	 
                                } else if (_skillId == ANTA_BLOOD) {
                                    if(cha instanceof L1PcInstance) {
                                    L1PcInstance pc = (L1PcInstance) cha;
                                    pc.addAc(-2);
                        			pc.sendPackets(new S_SPMR(pc));
                                         }
                                 // 크레이 혈흔
                                 //요리사 운세버프
                 		       } else if (_skillId == LUCK_A) { 
                 		             if (cha instanceof L1PcInstance) {
                 		               L1PcInstance pc = (L1PcInstance) cha;
                 		                  pc.addHitup(2);
                 		                  pc.addBowHitup(2);
                 		                  pc.addDmgup(2);
                 		                  pc.addBowDmgup(2); //추가
                 		                  pc.addSp(2);
                 		                  pc.addMaxHp(50);
                 		                  pc.addMaxMp(30);
                 		                  pc.addMpr(3);
                 		                  pc.sendPackets(new S_SPMR(pc));
                 		                  }
                 		       } else if (_skillId == LUCK_B) { 
                 		             if (cha instanceof L1PcInstance) {
                 		               L1PcInstance pc = (L1PcInstance) cha;
                 		                  pc.addHitup(2);
                 		                  pc.addBowHitup(2);
                 		                  pc.addSp(1);
                 		                  pc.addMaxHp(50);
                 		                  pc.addMaxMp(30);
                 		                  pc.sendPackets(new S_SPMR(pc));
                 		                  }
                 		       } else if (_skillId == LUCK_C) { 
                 		             if (cha instanceof L1PcInstance) {
                 		                L1PcInstance pc = (L1PcInstance) cha;
                 		                  pc.addMaxHp(50);
                 		                  pc.addMaxMp(30);
                 		                  pc.addAc(-2);
                 		                  }
                 		       } else if (_skillId == LUCK_D) { 
                 		             if (cha instanceof L1PcInstance) {
                 		                L1PcInstance pc = (L1PcInstance) cha;
                 		                  pc.addAc(-1);
                 		                  }
							    } else if(_skillId == INSIGHT){ 
							    	    L1PcInstance pc = (L1PcInstance) cha;
								          pc.addStr((byte)1);
								          pc.addDex((byte)1);
								          pc.addCon((byte)1);
								          pc.addInt((byte)1);
								          pc.addCha((byte)1);
								          pc.addWis((byte)1);
								 //요리사 운세버프
			 } else if (_skillId == HASTE) { // 헤이 파업
					if (cha.getMoveSpeed() != 2) { // 슬로우중 이외
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							if (pc.getHasteItemEquipped() > 0) {
								continue;
							}
							pc.setDrink(false);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1,_getBuffIconDuration));
						}
						cha.broadcastPacket(new S_SkillHaste(cha.getId(),
										1, 0));
						cha.setMoveSpeed(1);
					} else { // 슬로우중
						int skillNum = 0;
						if (cha.hasSkillEffect(SLOW)) {
							skillNum = SLOW;
						} else if (cha.hasSkillEffect(MASS_SLOW)) {
							skillNum = MASS_SLOW;
						} else if (cha.hasSkillEffect(ENTANGLE)) {
							skillNum = ENTANGLE;
                		} else if (cha.hasSkillEffect(Mob_SLOW_1)) {  
							skillNum = Mob_SLOW_1;
						} else if (cha.hasSkillEffect(Mob_SLOW_18)) {  
							skillNum = Mob_SLOW_18;
						}
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(HASTE);
							cha.setMoveSpeed(0);
							continue;
						}
					}
				} else if (_skillId == CURE_POISON) {
					cha.curePoison();
				} else if (_skillId == REMOVE_CURSE) {
					cha.curePoison();
					if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING)
					 || cha.hasSkillEffect(STATUS_CURSE_PARALYZED)
                     || cha.hasSkillEffect(Mob_RANGESTUN_19)
					 || cha.hasSkillEffect(Mob_RANGESTUN_18)
					 || cha.hasSkillEffect(Mob_CURSEPARALYZ_19)
					 || cha.hasSkillEffect(Mob_CURSEPARALYZ_18)
					 || cha.hasSkillEffect(Mob_CURSEPARALYZ_30)
					 || cha.hasSkillEffect(Mob_CURSEPARALYZ_SHORT_18)
					 || cha.hasSkillEffect(Mob_RANGESTUN_30)
					 || cha.hasSkillEffect(CURSE_BLIND)
					 || cha.hasSkillEffect(CURSE_PARALYZE)
				//	 || cha.hasSkillEffect(SHOCK_STUN)	// 쇼크 스턴도 해제
					 || cha.hasSkillEffect(ANTA_SKILL_3)	// 안타라스 용언
					 || cha.hasSkillEffect(ANTA_SKILL_4)
					 || cha.hasSkillEffect(ANTA_SKILL_5)) {
						cha.cureParalaysis();
					}
				} else if (_skillId == RESURRECTION
						|| _skillId == GREATER_RESURRECTION) { // 리자레크션, 그레이타리자레크션
					   if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
					   if (_player.getId() != pc.getId()) {
					   if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
						for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(pc, 0)) {
					   if (!visiblePc.isDead()) {
						   _player.sendPackets(new S_ServerMessage(592));
							return;
							}
							}
							}
						if (pc.getCurrentHp() == 0 && pc.isDead()) {
						if (pc.getMap().isUseResurrection()) {
						if (_skillId == RESURRECTION) {
										pc.setGres(false);
				 } else if (_skillId == GREATER_RESURRECTION) {
										pc.setGres(true);
							}
							pc.setTempID(_player.getId());
							pc.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
							}
							}
						    }
					        }
					    if (cha instanceof L1NpcInstance) {
						if (!(cha instanceof L1TowerInstance)) {
							L1NpcInstance npc = (L1NpcInstance) cha;
						if (npc.getNpcTemplate().isCantResurrect()) {
							return;
							}
						if (npc instanceof L1PetInstance
							&& L1World.getInstance()
							   .getVisiblePlayer(npc, 0)
							   .size() > 0) {
						for (L1PcInstance visiblePc : L1World
							   .getInstance().getVisiblePlayer(npc, 0)) {
						if (!visiblePc.isDead()) {
							_player.sendPackets(new S_ServerMessage(592));
							return;
							}
							}
							}
						if (npc.getCurrentHp() == 0 && npc.isDead()) {
								npc.resurrect(npc.getMaxHp() / 4);
								npc.setResurrect(true);
							}
						}
					}
				} else if (_skillId == CALL_OF_NATURE) { // 콜 오브 네이쳐
					   if (cha instanceof L1PcInstance) {
						  L1PcInstance pc = (L1PcInstance) cha;
					   if (_player.getId() != pc.getId()) {
					   if (L1World.getInstance().getVisiblePlayer(pc, 0).size() > 0) {
					for (L1PcInstance visiblePc : L1World
						.getInstance().getVisiblePlayer(pc, 0)) {
					   if (!visiblePc.isDead()) {
						   _player.sendPackets(new S_ServerMessage(592));
							return;
							}
							}
							}
					   if (pc.getCurrentHp() == 0 && pc.isDead()) {
						   pc.setTempID(_player.getId());
						   pc.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
							}
						    }
				            }
					   if (cha instanceof L1NpcInstance) {
					   if (!(cha instanceof L1TowerInstance)) {
							L1NpcInstance npc = (L1NpcInstance) cha;
					   if (npc.getNpcTemplate().isCantResurrect()) {
							return;
							}
					   if (npc instanceof L1PetInstance
							&& L1World.getInstance()
							.getVisiblePlayer(npc, 0)
							.size() > 0) {
					for (L1PcInstance visiblePc : L1World
							.getInstance().getVisiblePlayer(npc, 0)) {
					   if (!visiblePc.isDead()) {
							// \f1그 자리소에 다른 사람이 서 있으므로 부활시킬 수가 없습니다.
							_player.sendPackets(new S_ServerMessage(592));
							return;
							}
							}
							}
						if (npc.getCurrentHp() == 0 && npc.isDead()) {
								npc.resurrect(cha.getMaxHp());// HP를 전회복한다
								npc.resurrect(cha.getMaxMp() / 100);// MP를 0으로 한다
								npc.setResurrect(true);
							}
						}
					}
				} else if (_skillId == DETECTION) { // 디 텍 숀
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(_player);
						}
					}
				} else if (_skillId == COUNTER_DETECTION) { // 카운터 디 텍 숀
					if (cha instanceof L1PcInstance) {
						dmg = _magic.calcMagicDamage(_skillId);
					} else if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(_player);
					} else {
						dmg = 0;
						}
					} else {
						dmg = 0;
					}
				} else if (_skillId == TRUE_TARGET) { // 트루 타겟
					if (_user instanceof L1PcInstance) {
						L1PcInstance pri = (L1PcInstance) _user;
						pri.sendPackets(new S_TrueTarget(_targetID, pri.getId(), _message));
						L1PcInstance players[] = L1World.getInstance().getClan(pri.getClanname()).getOnlineClanMember();
						for (L1PcInstance pc : players) {
							pc.sendPackets(new S_TrueTarget(_targetID, pc.getId(), _message));
						}
					}
				} else if (_skillId == ELEMENTAL_FALL_DOWN) { // 일렉트로닉 멘탈 폴 다운
					if (_user instanceof L1PcInstance) {
						int playerAttr = _player.getElfAttr();
						int i = -50;
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							switch (playerAttr) {
							case 0:
								_player.sendPackets(new S_ServerMessage(79));
								break;
							case 1:
								pc.addEarth(i);
								pc.setAddAttrKind(1);
								_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4399));	// 땅속성 이펙트
								break;
							case 2:
								pc.addFire(i);
								pc.setAddAttrKind(2);
								_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4397));	// 불속성 이펙트
								break;
							case 4:
								pc.addWater(i);
								pc.setAddAttrKind(4);
								_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4396));	// 물속성 이펙트
								break;
							case 8:
								pc.addWind(i);
								pc.setAddAttrKind(8);
								_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 4398));	// 바람속성 이펙트
								break;
							default:
								break;
							}
						} else if (cha instanceof L1MonsterInstance) {
							L1MonsterInstance mob = (L1MonsterInstance) cha;
							switch (playerAttr) {
							case 0:
								_player.sendPackets(new S_ServerMessage(79));
								break;
							case 1:
								mob.addEarth(i);
								mob.setAddAttrKind(1);
								_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 4399));	// 땅속성 이펙트
								break;
							case 2:
								mob.addFire(i);
								mob.setAddAttrKind(2);
								_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 4397));	// 불속성 이펙트
								break;
							case 4:
								mob.addWater(i);
								mob.setAddAttrKind(4);
								_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 4396));	// 물속성 이펙트
								break;
							case 8:
								mob.addWind(i);
								mob.setAddAttrKind(8);
								_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 4398));	// 바람속성 이펙트
								break;
							default:
								break;
							}
						}
					}
				}
				// ★★★ 회복계 스킬 ★★★
				else if ((_skillId == HEAL || _skillId == EXTRA_HEAL
						|| _skillId == GREATER_HEAL || _skillId == FULL_HEAL
						|| _skillId == HEAL_ALL || _skillId == NATURES_TOUCH
						|| _skillId == NATURES_BLESSING)
					  //&& (_user instanceof L1PcInstance)) {
						&& (cha instanceof L1PcInstance)) {
				//	cha.removeSkillEffect(WATER_LIFE);
					cha.killSkillEffectTimer(WATER_LIFE);
					L1PcInstance pc = (L1PcInstance) _user;
					pc.sendPackets(new S_PacketBox(S_PacketBox.DEL_ICON));
					
				}
				// ★★★ 공격계 스킬 ★★★
				// 치르탓치, 반파이아릭크탓치
				else if (_skillId == CHILL_TOUCH || _skillId == VAMPIRIC_TOUCH
				|| _skillId == Mob_VAMPIRIC_TOUCH_1) {  // 몬스터에게 모션이 들어가기위해 - 몹스킬패턴 추가	
				heal = dmg;
				} else 
					/**
					 * 용기사 공격마법
					 */
					if (_skillId == FOURSLAYER) { // 포우슬레이어
						boolean gfxcheck = false; 
						int[] AttackGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105, 
								3145, 3148, 3151, 3871, 4125, 2323, 3892, 3895,  
								3898, 3901, 4917, 4918, 4919, 4950, 6087, 6140,  
								6145, 6150, 6155, 6160, 6269, 6272, 6275, 6278,
								7139, 7140, 7143, 7144, 7147, 7148, 7151, 7152,
								7155, 7156, 7159, 7160, 7163, 7164, 7959};  
							 int playerGFX = _player.getTempCharGfx();  
							    for (int gfx : AttackGFX) {  
							  if (playerGFX != gfx) {  
							  gfxcheck = true;  
								    break;  
							  }
						}
				    if (!gfxcheck) { 
						return;
				    }
				    for (int i = 3; i > 0; i--) { 
				    	_target.onAction(_player); 
				    }
				    _player.sendPackets(new S_SkillSound(_player.getId(), 7020)); 
				    _player.sendPackets(new S_SkillSound(_targetID, 6509)); 
				    _player.broadcastPacket(new S_SkillSound(_player.getId(), 7020)); 
				    _player.broadcastPacket(new S_SkillSound(_targetID, 6509));  
				}else if (_skillId == BLOODBREATH) { // 마그마브레스
		    			dmg+=20;
		    			_player.sendPackets(new S_SkillSound(_targetID,6513));
		    			_player.broadcastPacket(new S_SkillSound(_targetID,6513));
				} else if (_skillId == TRIPLE_ARROW) { //  트리플애로우
					/** 1회 사출할 때마다 아로, 데미지, 명중을 계산한다
					    아로가 남아 1으로 코뿔소 하의 활을 가지고 있다고(면) 와,
					    처음은 보통 공격 그 후는 마법 공격
					    아로가 남아 1으로 보통 활을 가지고 있다고(면) 와, 처음은 보통 공격,
					    그 후는 아로의 사출을 실시하지 않고 움직임만을 실시한다.
                    */
					// GFX Check
					boolean gfxcheck = false;
					int[] BowGFX = { 138, 37, 3860, 3126, 3420, 2284, 3105,
							3145, 3148, 3151, 3871, 4125, 2323, 3892, 3895,
							3898, 3901, 4917, 4918, 4919, 4950, 6087, 6140,
							6145, 6150, 6155, 6160, 6269, 6272, 6275, 6278,
							6826, 6827, 6836, 6837, 6846, 6847, 6856, 6857,
							6866, 6867, 6876, 6877, 6886, 6887, 7959, 6406, 
                            6400, 5645, 7307, 7306, 7039, 7040, 7041
							};
					int playerGFX = _player.getTempCharGfx();
					for (int gfx : BowGFX) {
						if (playerGFX == gfx) {
							gfxcheck = true;
							break;
						}
					}
					if (!gfxcheck) {
						return;
					}

					for (int i = 3; i > 0; i--) {
						_target.onAction(_player);
					}
 					_player.sendPackets(new S_SkillSound(_player.getId(),
							4394));
					_player.broadcastPacket(new S_SkillSound(_player.getId(),
							4394));
					
				/** 용언 **/
			
				} else if (_skillId == 10026 || _skillId == 10027
						|| _skillId == 10028 || _skillId == 10029) { // 안식 공격
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$3717", 0)); // 자, 너에게 안식을 주자.
					} else {
						_player.broadcastPacket(new S_ChatPacket(_player, "$3717", 0, 0)); // 자, 너에게 안식을 주자.
					}
				} else if (_skillId == 10057) { // 끌어 들이고(리콜)
					L1Teleport.teleportToTargetFront(cha, _user, 1);
				
				} else if (_skillId == ANTA_SKILL_1) {	// 안타라스(리뉴얼) - 용언 스킬1 
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7914", 0));
						// 오브 모크! 세이 리라프[무기손상-웨폰브레이크 + 굳기]
					}     
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance weapon = pc.getWeapon();
						Random random = new Random();
						int rnd = _random.nextInt(100) + 1;
						if (weapon != null && rnd <= (100 - pc.getStr()) / 2) { // 확률 :힘영향        
							int weaponDamage = _random.nextInt(3) + 1;
							pc.sendPackets(new S_ServerMessage(268, weapon.getLogName())); // \f1당신의%0가 손상했습니다.
							pc.getInventory().receiveDamage(weapon, weaponDamage);
						}     
						if (rnd <= (200 - pc.getMr() / 3)){ // 확률:마방영향
							int time = 300 / pc.getLevel() + 1;
							L1ParalysisPoison.doInfection(pc, 20000, time * 1000); // 20초 후에 time초간 마비    
						}
					}    
			
						if (_skillId == ANTA_SKILL_2) {		// 안타라스(리뉴얼) - 용언 스킬2
							_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7948", 0));	
							// 오브 모크! 티 세토르[캔슬 + 독굳히기]
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;	
								int time = 300 / pc.getLevel() + 1;
								L1DamagePoison.doInfection(pc, cha, 10000, 5);
								L1ParalysisPoison.doInfection(pc, 10000, time * 1000); // 10초 후에 time초간 마비    
							}
						}
					
				} else if (_skillId == ANTA_SKILL_8) {	// 안타라스(리뉴얼) - 용언 스킬8 
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7905", 0));
						// 오브 모크! 티기르[오른손펀치 + 이럽브레스]
					}  
					dmg = _magic.calcMagicDamage(_skillId);
				} else if (_skillId == ANTA_SKILL_9) {	// 안타라스(리뉴얼) - 용언 스킬9 
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7907", 0));
						// 오브 모크! 켄 티기르[오른쪽펀치 + 왼쪽펀치 + 브레스]
					}     	
					dmg = _magic.calcMagicDamage(_skillId);
					dmg = _magic.calcMagicDamage(_skillId);
				//////////// 나머지 용언은 각각 스킬안에 추가 하였음..
				} 
				// ★★★ 확률계 스킬 ★★★
				else if (_skillId == SLOW
						|| _skillId == MASS_SLOW || _skillId == ENTANGLE
					    || _skillId == Mob_SLOW_1 || _skillId == Mob_SLOW_18	// 몹스킬패턴 추가 
					) { // 슬로우, 매스
					// 슬로우, 엔탕르
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
					}
					if (cha.getMoveSpeed() == 0) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_SkillHaste(pc.getId(), 2, _getBuffIconDuration));
						}
						cha.broadcastPacket(new S_SkillHaste(cha.getId(), 2, _getBuffIconDuration));
						cha.setMoveSpeed(2);
					} else if (cha.getMoveSpeed() == 1) {
						int skillNum = 0;
						if (cha.hasSkillEffect(HASTE)) {
							skillNum = HASTE;
						} else if (cha.hasSkillEffect(GREATER_HASTE)) {
							skillNum = GREATER_HASTE;
						} else if (cha.hasSkillEffect(STATUS_HASTE)) {
							skillNum = STATUS_HASTE;
						}
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(_skillId);
							cha.setMoveSpeed(0);
							continue;
						}
					}
				} else if (_skillId == CURSE_BLIND || _skillId == DARKNESS) {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
							pc.sendPackets(new S_CurseBlind(2));
						} else {
							pc.sendPackets(new S_CurseBlind(1));
						}
					}
				} else if (_skillId == CURSE_POISON
					|| _skillId == Mob_CURSPOISON_30 || _skillId == Mob_CURSPOISON_18
					) {
					L1DamagePoison.doInfection(_user, cha, 3000, 5);
				} else if (_skillId == CURSE_PARALYZE
						|| _skillId == CURSE_PARALYZE2
						|| _skillId == Mob_CURSEPARALYZ_19 || _skillId == Mob_CURSEPARALYZ_18 
						|| _skillId == Mob_CURSEPARALYZ_30 || _skillId == Mob_CURSEPARALYZ_SHORT_18		
					) {
					if (!cha.hasSkillEffect(EARTH_BIND)
							&& !cha.hasSkillEffect(ICE_LANCE)
							&& !cha.hasSkillEffect(FREEZING_BLIZZARD)
							&& !cha.hasSkillEffect(Mob_AREA_ICE_LANCE)		
							&& !cha.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) {
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 8000, 16000);
						} else if (cha instanceof L1MonsterInstance) {
							L1CurseParalysis.curse(cha, 8000, _skill.getBuffDuration() * 1000); 
						}
					}
				} else if (_skillId == WEAKNESS || _skillId == Mob_WEAKNESS_1) { // 위크네스
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-5);
						pc.addHitup(-1);
					}
				} else if (_skillId == DISEASE
				|| _skillId == Mob_DISEASE_1 || _skillId == Mob_DISEASE_30) { // 디지즈
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-6);
						pc.addAc(12);
					}
			    } else if (_skillId == GUARDBREAK) {  //가드브레이크
				       if (cha instanceof L1PcInstance) {
				         L1PcInstance pc = (L1PcInstance) cha;
				         pc.addAc(15);
				        }
				} else if (_skillId == HOUROFDEATH) {  //호러오브데스
				      if (cha instanceof L1PcInstance) {
				         L1PcInstance pc = (L1PcInstance) cha;
				         pc.addStr((byte) -5);
				         pc.addInt((byte) -5);
				        }
				        }
				      if (_skillId == PANIC) {  //패닉
					  if (cha instanceof L1PcInstance) {
						  L1PcInstance pc = (L1PcInstance) cha;
						  pc.addStr((byte)-1);
						  pc.addDex((byte)-1);
						  pc.addCon((byte)-1);
						  pc.addInt((byte)-1);
						  pc.addCha((byte)-1);
						  pc.addWis((byte)-1);
						 }
				} else if(_skillId == JOYOFPAIN){ //조이오브페인
						 dmg = (_player.getMaxHp() - _player.getCurrentHp() ) / 3;
						 }
			        	if (_skillId == ICE_LANCE || _skillId == FREEZING_BLIZZARD
						 || _skillId == Mob_AREA_ICE_LANCE || _skillId == Mob_CALL_LIGHTNING_ICE) { // 아이스 랑스, freezing 블리자드
					        _isFreeze = _magic.calcProbabilityMagic(_skillId);
					    if (_isFreeze) {
						int time = _skill.getBuffDuration() * 1000;
						    L1EffectSpawn.getInstance()
							.spawnEffect(81168, time,
							cha.getX(), cha.getY(), cha.getMapId());
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_Poison(pc.getId(), 2));
							pc.broadcastPacket(new S_Poison(pc.getId(), 2));
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
						} else if (cha instanceof L1MonsterInstance
								|| cha instanceof L1SummonInstance
								|| cha instanceof L1PetInstance) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							npc.broadcastPacket(new S_Poison(npc.getId(), 2));
							npc.setParalyzed(true);
							npc.setParalysisTime(time);
						}
					}
				} else if (_skillId == EARTH_BIND
				        || _skillId == Mob_Basill || _skillId == Mob_Coca) { // 아스바인드
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Poison(pc.getId(), 2));
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, true));
			    } else if (cha instanceof L1MonsterInstance 
			    		|| cha instanceof L1SummonInstance 
			    		|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
						npc.setParalyzed(true);
						npc.setParalysisTime(_skill.getBuffDuration() * 1000);
					}
				} else if (_skillId == SHOCK_STUN) {	
					int[] stunTimeArray = { 1300, 2000, 2300, 2500, 3000, 3500, 4000, 5000 };
					int rnd = _random.nextInt(stunTimeArray.length);
					_shockStunDuration = stunTimeArray[rnd];
					if (cha instanceof L1PcInstance && cha.hasSkillEffect(SHOCK_STUN)) {
						_shockStunDuration += cha
						.getSkillEffectTimeSec(SHOCK_STUN) * 1000;
					}
					L1EffectSpawn.getInstance()
						.spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(), cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(_shockStunDuration);
					}	
					/** 용언 **/
				} else if (_skillId == Mob_SHOCKSTUN_18 || _skillId == Mob_RANGESTUN_19  
						|| _skillId == Mob_RANGESTUN_18	|| _skillId == Mob_RANGESTUN_30	 	
						|| _skillId == ANTA_SKILL_3 || _skillId == ANTA_SKILL_4 || _skillId == ANTA_SKILL_5	// 안타라스 용언
					) {	
					int npcId = _npc.getNpcTemplate().get_npcId();
					if (npcId == 777775 || npcId == 777776 || npcId == 777779){	// 안타라스(리뉴얼)1,2,3단계
					if (_skillId == ANTA_SKILL_3) {		// 안타라스(리뉴얼) - 용언 스킬3
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7903", 0));
						// 오브 모크! 뮤즈 삼[범위스턴 + 점프공격]
						dmg = _magic.calcMagicDamage(_skillId);
					}
					if (_skillId == ANTA_SKILL_4) {		// 안타라스(리뉴얼) - 용언 스킬4
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7909", 0));
						// 오브 모크! 너츠 삼[범위스턴 + 전체운석]
						dmg = _magic.calcMagicDamage(_skillId);
					}
					if (_skillId == ANTA_SKILL_5) {		// 안타라스(리뉴얼) - 용언 스킬5
						_user.broadcastPacket(new S_NpcChatPacket(_npc, "$7915", 0));
						// 오브 모크! 티프 삼[범위스턴 + 점프공격 + 전체운석]
						dmg = _magic.calcMagicDamage(_skillId);
						dmg = _magic.calcMagicDamage(_skillId);
					}
					} 
					int[] stunTimeArray = { 1300, 2000, 2300, 2500, 3000, 3500, 4000, 5000 };
					int rnd = _random.nextInt(stunTimeArray.length);
					_shockStunDuration = stunTimeArray[rnd];
					if (cha instanceof L1PcInstance && cha.hasSkillEffect(SHOCK_STUN)) {
						_shockStunDuration += cha
						.getSkillEffectTimeSec(SHOCK_STUN) * 1000;
					}

					L1EffectSpawn.getInstance()
						.spawnEffect(81162, _shockStunDuration, cha.getX(), cha.getY(), cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(_shockStunDuration);
					}	
				} else if (_skillId == BONEBREAK) { // 본브레이크
					int[] breakTimeArray = { 2000 };
					Random random = new Random();
					int rnd = random.nextInt(breakTimeArray.length);
					_bonebreakDuration = breakTimeArray[rnd];
					if (cha instanceof L1PcInstance && cha.hasSkillEffect(BONEBREAK)) {
						_bonebreakDuration += cha
						.getSkillEffectTimeSec(BONEBREAK) * 1000;
					}
					L1EffectSpawn.getInstance()
					.spawnEffect(200044, _bonebreakDuration, cha.getX(), cha.getY(), cha.getMapId());
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
						dmg = 10;
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setParalyzed(true);
						npc.setParalysisTime(_bonebreakDuration);
						dmg = 10;
					}
					
				} else if (_skillId == WIND_SHACKLE
				|| _skillId == Mob_WINDSHACKLE_1 
				) { // 윈드산크루
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
						_getBuffIconDuration));
					}
				} else if (_skillId == CANCELLATION || _skillId == Mob_AREA_CANCELLATION_19
						   || _skillId == ANTA_SKILL_2) {
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int npcId = npc.getNpcTemplate().get_npcId();
						if (npcId == 71092) { // 조사원
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								npc.setTempCharGfx(1314);
								npc.broadcastPacket(new S_ChangeShape(npc.getId(), 1314));
								return;
				} else {
								return;
							}
						}
						if (npcId == 45640) { // 유니콘
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								npc.setCurrentHp(npc.getMaxHp());
								npc.setTempCharGfx(2332);
								npc.broadcastPacket(new S_ChangeShape(npc.getId(), 2332));
								npc.setName("$2103");
								npc.setNameId("$2103");
								npc.broadcastPacket(new S_ChangeName(npc.getId(), "$2103"));
							} else if (npc.getTempCharGfx() == 2332) {
								npc.setCurrentHp(npc.getMaxHp());
								npc.setTempCharGfx(2755);
								npc.broadcastPacket(new S_ChangeShape(npc.getId(), 2755));
								npc.setName("$2488");
								npc.setNameId("$2488");
								npc.broadcastPacket(new S_ChangeName(npc.getId(), "$2488"));
							}
						}
						if (npcId == 81209) { // 로이
							if (npc.getGfxId() == npc.getTempCharGfx()) {
								npc.setTempCharGfx(4310);
								npc.broadcastPacket(new S_ChangeShape(npc. getId(), 4310));
								return;
				} else {
								return;
							}
						}
					}
					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}
					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setMoveSpeed(0);
						npc.setBraveSpeed(0);
						npc.broadcastPacket(new S_SkillHaste(cha.getId(), 0, 0));
						npc.broadcastPacket(new S_SkillBrave(cha.getId(), 0, 0));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
					    }

					// 스킬의 해제
					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum) && !cha.isDead()) {
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 스테이터스 강화, 이상의 해제
					cha.curePoison();
					cha.cureParalaysis();
					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_END; skillNum++) {
						if (skillNum == STATUS_CHAT_PROHIBITED // 채팅 금지는 해제하지 않는다
								|| skillNum == STATUS_CURSE_BARLOG // 바르로그의 저주는 해제하지 않는다
								|| skillNum == STATUS_CURSE_YAHEE) { // 야히의 저주는 해제하지 않는다
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 요리의 해제
					for (int skillNum = COOKING_BEGIN; skillNum <= COOKING_END; skillNum++) {
						if (isNotCancelable(skillNum)) {
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 헤이스트아이템 장비시는 헤이 파업 관련의 스킬이 아무것도 걸려있지 않을 것이므로 여기서 해제
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							pc.setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
						}
					    if (pc.getBraveItemEquipped() > 0) {//용기아이템  
						    pc.setBraveSpeed(0); 
							pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0)); 
							pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0)); 
						} 
						} 

						cha.removeSkillEffect(STATUS_FREEZE); // Freeze 해제
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.broadcastPacket(new S_CharVisualUpdate(pc));
						if (pc.isPrivateShop()) {
							pc.sendPackets(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, pc.getShopChat()));
							pc.broadcastPacket(new S_DoActionShop(pc.getId(), ActionCodes.ACTION_Shop, pc.getShopChat()));
						}
						if (_user instanceof L1PcInstance) {  
							L1PinkName.onAction(pc, _user);  
						} 
					}
				} else if (_skillId == TURN_UNDEAD // 턴 안 데드
						&& (undeadType == 1 || undeadType == 3)) {
					// 데미지를 대상의 HP로 한다.
					dmg = cha.getCurrentHp();
				} else if (_skillId == MANA_DRAIN) { // 마나드레인
					    Random random = new Random();
					int chance = random.nextInt(10) + 5;
					    drainMana = chance + (_user.getInt() / 2);
					if (cha.getCurrentMp() < drainMana) {
						drainMana = cha.getCurrentMp();
					}
				} else if (_skillId == WEAPON_BREAK) { // 웨폰브레이크
					/*
					 * 대NPC의 경우, L1Magic의 데미지 산출로 데미지1/2로 하고 있으므로
					 * 이쪽에는, 대PC의 경우 밖에 기입하지 않는다. 손상량은1~(int/3)까지
					 */
					if (_calcType == PC_PC || _calcType == NPC_PC) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							L1ItemInstance weapon = pc.getWeapon();
						if (weapon != null) {
							Random random = new Random();
						int weaponDamage = random.nextInt(_user.getInt() / 3) + 1;
							pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
							pc.getInventory().receiveDamage(weapon, weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}
				} else if (_skillId == FOG_OF_SLEEPING) {
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, true));
					}
					cha.setSleeped(true);
				} else if (_skillId == STATUS_FREEZE) { // Freeze
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, true));
					}
                //보라
			    } else if (_skillId == CURE_POISON || _skillId == CURSE_POISON || _skillId == CURSE_BLIND
			      || _skillId == WEAPON_BREAK || _skillId == SLOW || _skillId == CURSE_PARALYZE 
			      || _skillId == MANA_DRAIN || _skillId == DARKNESS || _skillId == WEAKNESS
			      || _skillId == DISEASE || _skillId == SILENCE || _skillId == FOG_OF_SLEEPING
			      || _skillId == DECAY_POTION || _skillId == MASS_SLOW
			      || _skillId == EARTH_BIND) { 
			     if (cha instanceof L1PcInstance) {
			        L1PcInstance pc = (L1PcInstance) cha;
			     if (_user instanceof L1PcInstance) {  
			         L1PinkName.onAction(pc, _user);  
			         }
			         }
                   	}

				// ●●●● PC밖에 효과가 없는 스킬 ●●●●
				 if (_calcType == PC_PC || _calcType == NPC_PC) {
					// ★★★ 특수계 스킬★★★
				 if (_skillId == TELEPORT || _skillId == MASS_TELEPORT) { // 매스 텔레, 텔레포트
						L1PcInstance pc = (L1PcInstance) cha;
						L1BookMark bookm = pc.getBookMark(_bookmarkId);
				 if (bookm != null) { // 북마크를 취득 할 수 있으면(자) 텔레포트
				 if (pc.getMap().isEscapable() || pc.isGm()) {
					int newX = bookm.getLocX();
					int newY = bookm.getLocY();
					short mapId = bookm.getMapId();
                 if (_skillId == MASS_TELEPORT) { // 매스 텔레포트
					 Random random = new Random(); 
					 List<L1PcInstance>  clanMember = L1World.getInstance().getVisiblePlayer(pc);
					for (L1PcInstance member : clanMember) {
					if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3){
						continue;
						}
					if(pc.getMapId() !=  member.getMapId()){
						continue;
						}
					if ( member.getClanid() != pc.getClanid()) {
						continue;
						} 
					if ( member.getClanid() == 0) {
						continue;
						} 
						int newX2 = bookm.getLocX();
						int newY2 = bookm.getLocY();
						short mapId2 = bookm.getMapId();
                        int rndX,rndY;
						int ckbb = random.nextInt(2);
					if(ckbb ==1){rndX= random.nextInt(4)*-1;
				}else{rndX= random.nextInt(4); 
						}
					if(ckbb ==1){rndY= random.nextInt(4)*-1;
				}else{ rndY= random.nextInt(4);
						}
						newX2 = newX+rndX;
						newY2 = newY+rndY;
						L1Map map = L1WorldMap.getInstance().getMap(mapId);
					if ( map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
						L1Teleport.teleport( member, newX2, newY2, mapId2, 5, true);
				} else {
						L1Teleport.teleport( member, newX, newY, mapId, 5, true);
						}
						}
						}
						L1Teleport.teleport(pc, newX, newY, mapId, 5,true);
				} else { // 텔레포트 불가 MAP에의 이동 제한
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
						pc.sendPackets(new S_ServerMessage(79));
						}
				} else { // 북마크를 취득 할 수 없었다, 혹은 「임의의 장소」를 선택했을 경우의 처리
					if (pc.getMap().isTeleportable() || pc.isGm()) {
						L1Location newLocation = pc.getLocation()
						.randomLocation(200, true);
						int newX = newLocation.getX();
						int newY = newLocation.getY();
						short mapId = (short) newLocation.getMapId();
					if (_skillId == MASS_TELEPORT) { // 매스 텔레포트
						Random random = new Random(); 
						List<L1PcInstance>  clanMember = L1World.getInstance().getVisiblePlayer(pc);
					for (L1PcInstance member : clanMember) {
					if (pc.getLocation().getTileLineDistance(member.getLocation()) >= 3){
						continue;
						}
					if(pc.getMapId() !=  member.getMapId()){
						continue;
						}
					if ( member.getClanid() != pc.getClanid()) {
						continue;
						} 
					if ( member.getClanid() == 0) {
						continue;
						} 
						int newX2 = newLocation.getX();
						int newY2 = newLocation.getY();
						short mapId2 = (short) newLocation.getMapId();
						int rndX,rndY;
						int ckbb = random.nextInt(2);
					if(ckbb ==1){rndX= random.nextInt(4)*-1;
				}else{rndX= random.nextInt(4);
						}
					if(ckbb ==1){rndY= random.nextInt(4)*-1;
				}else{rndY= random.nextInt(4);
						}
						newX2 = newX+rndX;
						newY2 = newY+rndY;
						L1Map map = L1WorldMap.getInstance().getMap(mapId);
					if ( map.isInMap(newX2, newY2) && map.isPassable(newX2, newY2)) {
						L1Teleport.teleport( member, newX2, newY2, mapId2, 5, true);
				} else {
						L1Teleport.teleport( member, newX, newY, mapId, 5, true);
						}
						}
						}
						L1Teleport.teleport(pc, newX, newY, mapId, 5,true);
				} else {
						pc.sendPackets(new S_ServerMessage(276));
					    pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); 
						}
						}
				} else if (_skillId == TELEPORT_TO_MATHER) { // 테레포트트
						L1PcInstance pc = (L1PcInstance) cha;
					if (pc.getMap().isEscapable() || pc.isGm()) {
						L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5, true);
				} else {
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));  
						pc.sendPackets(new S_ServerMessage(647));
						}
				} else if (_skillId == CALL_CLAN) { // 콜클랜
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
					if (clanPc != null) {
						clanPc.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
					    clanPc.sendPackets(new S_Message_YN(729, "")); 
						}
				} else if (_skillId == RUN_CLAN) { // 런클랜
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World.getInstance().findObject(_targetID);
					if (clanPc != null) {
					if (pc.getMap().isEscapable() || pc.isGm()) {
						boolean castle_area = L1CastleLocation.checkInAllWarArea(
						clanPc.getX(), clanPc.getY(),
						clanPc.getMapId());
					if ((clanPc.getMapId() == 0|| clanPc.getMapId() == 4 || clanPc.getMapId() == 304)
						&& castle_area == false) {
						L1Teleport.teleport(pc, clanPc.getX(),
						clanPc.getY(), clanPc.getMapId(),
						5, true);
				} else {
						// \f1당신의 파트너는 지금 당신이 갈 수 없는 곳에서 플레이중입니다.
						pc.sendPackets(new S_ServerMessage(547));
						}
				} else {
						// 주변의 에너지가 텔레포트를 방해하고 있습니다.그 때문에, 여기서 텔레포트는 사용할 수 없습니다.
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));  
						pc.sendPackets(new S_ServerMessage(647));
						}
						}
				} else if (_skillId == CREATE_MAGICAL_WEAPON) { // 클리에 실
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							int item_type = item.getItem().getType2();
							int safe_enchant = item.getItem().get_safeenchant();
							int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
						if (safe_enchant < 0) { // 강화 불가
							pc.sendPackets(new S_ServerMessage(79));
				} else if (safe_enchant == 0) { // 안전권+0
							pc.sendPackets( // \f1 아무것도 일어나지 않았습니다.
							new S_ServerMessage(79));
				} else if (item_type == 1 && enchant_level == 0) {
						if (!item.isIdentified()) {// 미감정
							pc.sendPackets( // \f1%0이%2%1 빛납니다.
							new S_ServerMessage(161, item_name, "$245", "$247"));
				} else {
							item_name = "+0 " + item_name;
							pc.sendPackets(new S_ServerMessage(161, "+0 " + item_name, "$245", "$247"));
							}
							item.setEnchantLevel(1);
							pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
				} else {
							pc.sendPackets(new S_ServerMessage(79));
							}
				} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
				} else if (_skillId == BRING_STONE) { // 브링스톤
						    L1PcInstance pc = (L1PcInstance) cha;
						    Random random = new Random();
						    L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null) {
							int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc.getWis() - 6) * 1.2);
							int brave = (int) (dark / 2.0);
							int wise = (int) (brave / 1.9);
							int kayser = (int) (wise / 1.5);
							int chance = random.nextInt(100) + 1;
						if (item.getItem().getItemId() == 40320) {
							pc.getInventory().removeItem(item, 1);
						if (dark >= chance) {
							pc.getInventory().storeItem(40321, 1);
							pc.sendPackets(new S_ServerMessage(403, "$2475")); // %0를 손에 넣었습니다.
				} else {
							pc.sendPackets(new S_ServerMessage(280)); // \f1마법이 실패했습니다.
							}
				} else if (item.getItem().getItemId() == 40321) {
							pc.getInventory().removeItem(item, 1);
						if (brave >= chance) {
							pc.getInventory().storeItem(40322, 1);
							pc.sendPackets(new S_ServerMessage(403, "$2476")); // %0를 손에 넣었습니다.
				} else {
							pc.sendPackets(new S_ServerMessage(280)); // \f1마법이 실패했습니다.
							}
				} else if (item.getItem().getItemId() == 40322) {
							pc.getInventory().removeItem(item, 1);
						if (wise >= chance) {
							pc.getInventory().storeItem(40323, 1);
							pc.sendPackets(new S_ServerMessage(403, "$2477")); // %0를 손에 넣었습니다.
				} else {
							pc.sendPackets(new S_ServerMessage(280)); // \f1마법이 실패했습니다.
							}
				} else if (item.getItem().getItemId() == 40323) {
							pc.getInventory().removeItem(item, 1);
						if (kayser >= chance) {
							pc.getInventory().storeItem(40324, 1);
							pc.sendPackets(new S_ServerMessage(403, "$2478")); // %0를 손에 넣었습니다.
				} else {
							pc.sendPackets(new S_ServerMessage(280)); // \f1마법이 실패했습니다.
							}
							}
						   }
				} else if (_skillId == UNCANNY_DODGE) { // 언케니닷지
                        if (cha instanceof L1PcInstance) {
                            L1PcInstance pc = (L1PcInstance) cha;
                            pc.addDg(-5);
                            }
		         } else if (_skillId == MIRRORIMG) { // 미러이미지
                        if (cha instanceof L1PcInstance) {
                             L1PcInstance pc = (L1PcInstance) cha;
                             pc.addDg(-5);
                             }
		         } else if (_skillId == PEAR) { // 피어
                     if (cha instanceof L1PcInstance) {
                          L1PcInstance pc = (L1PcInstance) cha;
                          pc.addDg(-3);
                          }
				} else if (_skillId == SUMMON_MONSTER) { // 서먼몬스터
						    L1PcInstance pc = (L1PcInstance) cha;
						    int level = pc.getLevel();
						    int[] summons;
						if (pc.getMap().isRecallPets() || pc.isGm()) {
						if (pc.getInventory().checkEquipped(20284)) {
							pc.sendPackets(new S_ShowSummonList(pc.getId()));
				} else {
							summons = new int[] { 81210, 81213, 81216, 81219, 81222, 81225, 81228 }; ///// 일팩리버전 소스 사용 몹스킬패턴
							int summonid = 0;
							int summoncost = 6;
							int levelRange = 32;
						if (pc.getLevel() < 28){
							pc.sendPackets(new S_ServerMessage(743));
							return;
							}
						if (pc.getCha() <= 9){
							summoncost = 8;
				} else if (pc.getCha() >= 10 && pc.getCha() <= 17){ 
							summoncost = 8;
				} else if (pc.getCha() >= 18 && pc.getCha() <= 25){ 
							summoncost = 8;
				} else if (pc.getCha() >= 26 && pc.getCha() <= 33){ 
							summoncost = 8;
				} else if (pc.getCha() >= 34 && pc.getCha() <= 41){ 
							summoncost = 8;
				} else if (pc.getCha() >= 42){ 
							summoncost = 9;
							}
						for (int i = 0; i < summons.length; i++) { // 해당 LV범위 검색
						if (level < levelRange
							|| i == summons.length - 1) {
							summonid = summons[i];
							break;
							}
							levelRange += 4;
							}
                        int petcost = 0;
							Object[] petlist = pc.getPetList().values().toArray();
						for (Object pet : petlist) {
									// 현재의 애완동물 코스트
							petcost += ((L1NpcInstance) pet)
							.getPetcost();
							}
							int charisma = pc.getCha() + 6 - petcost;
							int summoncount = charisma / summoncost;
								L1Npc npcTemp = NpcTable.getInstance()
								.getTemplate(summonid);
							for (int i = 0; i < summoncount; i++) {
								L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
								summon.setPetcost(summoncost);
								}
							}
				} else {
						    pc.sendPackets(new S_ServerMessage(79));
						    }
				} else if (_skillId == LESSER_ELEMENTAL
							|| _skillId == GREATER_ELEMENTAL) { // 렛서에레멘탈, 그레이터 일렉트로닉 멘탈
						    L1PcInstance pc = (L1PcInstance) cha;
						    int attr = pc.getElfAttr();
						if (attr != 0) { // 무속성이 아니면 실행
						if (pc.getMap().isRecallPets() || pc.isGm()) {
						int petcost = 0;
							Object[] petlist = pc.getPetList().values()
							.toArray();
						for (Object pet : petlist) {
									// 현재의 애완동물 코스트
							petcost += ((L1NpcInstance) pet)
							.getPetcost();
							}

						if (petcost == 0) { // 1마리나 소속 NPC가 없으면 실행
							int summonid = 0;
							int summons[];
						if (_skillId == LESSER_ELEMENTAL) { // 렛서에레멘탈[지, 화, 수, 바람]
							summons = new int[] { 45306, 45303, 45304, 45305 };
				} else {
										// 그레이터 일렉트로닉 멘탈[지, 화, 수, 바람]
							summons = new int[] { 81053, 81050, 81051, 81052 };
							}
							int npcattr = 1;
							for (int i = 0; i < summons.length; i++) {
						if (npcattr == attr) {
							summonid = summons[i];
							i = summons.length;
							}
							npcattr *= 2;
							}
									// 특수 설정의 경우 랜덤으로 출현
						if (summonid == 0) {
							Random random = new Random();
							int k3 = random.nextInt(4);
							summonid = summons[k3];
							}
                            L1Npc npcTemp = NpcTable.getInstance()
							.getTemplate(summonid);
							L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
							summon.setPetcost(pc.getCha() + 7); // 정령 외에는 NPC를 소속 시킬 수 없다
							}
				} else {
							pc.sendPackets(new S_ServerMessage(79));
							}
						}		
				} else if (_skillId == ABSOLUTE_BARRIER) { // 아브소르트바리아
						L1PcInstance pc = (L1PcInstance) cha;
						pc.stopHpRegeneration();
						pc.stopMpRegeneration();
						pc.stopMpRegenerationByDoll();
					}

					// ★★★ 변화계 스킬(엔챤트) ★★★
					if (_skillId == LIGHT) { // 라이트
						// addMagicList() 후에, turnOnOffLight()으로 패킷 송신
				} else if (_skillId == GLOWING_AURA) { // 그로윙오라
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addBowHitup(5);
						pc.addMr(20);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_SkillIconAura(113,_getBuffIconDuration));
				} else if (_skillId == SHINING_AURA) { // 샤이닝오라
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-8);
						pc.sendPackets(new S_SkillIconAura(114,_getBuffIconDuration));
				} else if (_skillId == BRAVE_AURA) { // 치우침 이브 아우라
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.sendPackets(new S_SkillIconAura(116,_getBuffIconDuration));
				} else if (_skillId == SHIELD) { // 쉴드(shield)
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-2);
						pc.sendPackets(new S_SkillIconShield(5,_getBuffIconDuration));				
				} else if(_skillId == CUBE_IGNITION){ // 큐브 : 1단계
					    L1Npc npcTemp = NpcTable.getInstance().getTemplate(200031);
					    npcSpwan(npcTemp, _player, 0);
				} else if(_skillId == CUBE_QUAKE){    // 큐브 : 2단계
					    L1Npc npcTemp = NpcTable.getInstance().getTemplate(200032);
					    npcSpwan(npcTemp, _player, 1);
				} else if(_skillId == CUBE_SHOCK){    // 큐브 : 3단계
					    L1Npc npcTemp = NpcTable.getInstance().getTemplate(200033);
					    npcSpwan(npcTemp, _player, 2);
				} else if(_skillId == CUBE_BALANCE){  // 큐브 : 4단계
					    L1Npc npcTemp = NpcTable.getInstance().getTemplate(200034);
					    npcSpwan(npcTemp, _player, 3);
				} else if (_skillId == SHADOW_ARMOR) { // 그림자 아모
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-3);
						pc.sendPackets(new S_SkillIconShield(3,_getBuffIconDuration));
				} else if (_skillId == DRESS_DEXTERITY) { // 드레스데크스타리티
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 2);
						pc.sendPackets(new S_Dexup(pc, 2,_getBuffIconDuration));
				} else if (_skillId == DRESS_MIGHTY) { // 드레스마이티
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 2);
						pc.sendPackets(new S_Strup(pc, 2,_getBuffIconDuration));
				} else if (_skillId == SHADOW_FANG) { // 샤드우팡
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
				} else {
						pc.sendPackets(new S_ServerMessage(79));
						}
				} else if (_skillId == ENCHANT_WEAPON) { // 엔체트웨폰
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
							item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
				} else {
						pc.sendPackets(new S_ServerMessage(79));
						}
				} else if (_skillId == HOLY_WEAPON // 호리웨폰
							|| _skillId == BLESS_WEAPON) { // 브레스웨폰
						if (!(cha instanceof L1PcInstance)) {
							return;
						}
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getWeapon() == null) {
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}
						for (L1ItemInstance item : pc.getInventory().getItems()) {
						if (pc.getWeapon().equals(item)) {
							pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
							item.setSkillWeaponEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
							return;
							}
						}
				} else if (_skillId == BLESSED_ARMOR) { // 브레스드아마
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(_itemobjid);
						if (item != null && item.getItem().getType2() == 2
							&& item.getItem().getType() == 2) {
							pc.sendPackets(new S_ServerMessage(161, item.getLogName(), "$245", "$247"));
							item.setSkillArmorEnchant(pc, _skillId, _skill.getBuffDuration() * 1000);
				} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
				} else if (_skillId == EARTH_BLESS) { // 지구 호흡
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-7);
						pc.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
				} else if (_skillId == RESIST_MAGIC) { // 레지스터 매직
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(10);
						pc.sendPackets(new S_SPMR(pc));
				} else if (_skillId == CLEAR_MIND) { // 클리어 마인드
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addWis((byte) 3);
						pc.resetBaseMr();
				} else if (_skillId == RESIST_ELEMENTAL) { // 레지스터 엘리먼트
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addWind(10);
						pc.addWater(10);
						pc.addFire(10);
						pc.addEarth(10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
				} else if (_skillId == BODY_TO_MIND) { // 보디트마인드
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 2);
				} else if (_skillId == BLOODY_SOUL) { // 브랏디소울
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 12);
				} else if (_skillId == ELEMENTAL_PROTECTION) { // 일렉트로닉 멘탈 프로텍션
						L1PcInstance pc = (L1PcInstance) cha;
						int attr = pc.getElfAttr();
						if (attr == 1) {
						    pc.addEarth(50);
				} else if (attr == 2) {
							pc.addFire(50);
				} else if (attr == 4) {
							pc.addWater(50);
				} else if (attr == 8) {
							pc.addWind(50);
				           }
				} else if (_skillId == INVISIBILITY || _skillId == BLIND_HIDING) { // 인비지비리티, 브라인드하이딘그
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Invis(pc.getId(), 1));
						L1World.getInstance().broadcastPacketToAll(new S_Invis(pc.getId(), 1)); // 추가
				} else if (_skillId == IRON_SKIN) { // 아이언 스킨
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-10);
						pc.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
				} else if (_skillId == EARTH_SKIN) { // 지구 스킨
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-6);
						pc.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
				} else if (_skillId == PHYSICAL_ENCHANT_STR) { // 피지컬 엔챤트：STR
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 5);
						pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
				} else if (_skillId == PHYSICAL_ENCHANT_DEX) { // 피지컬 엔챤트：DEX
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 5);
						pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
				} else if (_skillId == FIRE_WEAPON) { // 파이아웨폰
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
				} else if (_skillId == FIRE_BLESS) { // 파이어 호흡
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
				} else if (_skillId == BURNING_WEAPON) { // 바닝웨폰
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addHitup(3);
						pc.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
				} else if (_skillId == WIND_SHOT) { // 윈도우 쇼트
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(6);
						pc.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
				} else if (_skillId == STORM_EYE) { // 스토무아이
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(2);
						pc.addBowDmgup(3);
						pc.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
				} else if (_skillId == STORM_SHOT) { // 스톰 쇼트
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowDmgup(5);
						pc.addBowHitup(-1);
						pc.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
				} else if (_skillId == BERSERKERS) { // 바서카
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(10);
						pc.addDmgup(6);
						pc.addHitup(2);
				} else if (_skillId == FAFU_MAAN) { // 수룡의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == ANTA_MAAN) { // 지룡의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == LIND_MAAN) { // 풍룡의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == VALA_MAAN) { // 화룡의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == LIFE_MAAN) { // 생명의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == BIRTH_MAAN) { // 탄생의 마안
						L1PcInstance pc = (L1PcInstance) cha;
				} else if (_skillId == SHAPE_MAAN) { // 형상의 마안
						L1PcInstance pc = (L1PcInstance) cha;

				} else if (_skillId == MAGE_DISEASE) { // 아크메이지의 지팡이 디지즈
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(12);
						pc.addDmgup(6);
				} else if(_skillId == ARMBREAKER){ //암브레이크
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(-2);
						int[] armTimeArray = { 2000 };
						Random random = new Random();
						int rnd = random.nextInt(armTimeArray.length);
						_armDuration = armTimeArray[rnd];
						if (cha instanceof L1PcInstance && cha.hasSkillEffect(ARMBREAKER)) {
						_armDuration += cha
						.getSkillEffectTimeSec(ARMBREAKER) * 1000;
						}
						L1EffectSpawn.getInstance()
						.spawnEffect(200044, _armDuration, cha.getX(), cha.getY(), cha.getMapId());					
				} else if (_skillId == SHAPE_CHANGE) { // 셰이프 체인지
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_ShowPolyList(pc.getId()));
				} else if (_skillId == ADVANCE_SPIRIT) { // advanced 스피리츠
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setAdvenHp(pc.getBaseMaxHp() / 5);
						pc.setAdvenMp(pc.getBaseMaxMp() / 5);
						pc.addMaxHp(pc.getAdvenHp());
						pc.addMaxMp(pc.getAdvenMp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					    if (pc.isInParty()) { // 파티중
						pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				} else if (_skillId == GREATER_HASTE) { // 그레이터 헤이 파업
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
						if (pc.getMoveSpeed() != 2) { // 슬로우중 이외
							pc.setDrink(false);
							pc.setMoveSpeed(1);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1, _getBuffIconDuration));
							pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				} else { // 슬로우중
						int skillNum = 0;
						if (pc.hasSkillEffect(SLOW)) {
							skillNum = SLOW;
				} else if (pc.hasSkillEffect(MASS_SLOW)) {
							skillNum = MASS_SLOW;
				} else if (pc.hasSkillEffect(ENTANGLE)) {
							skillNum = ENTANGLE;
                } else if (pc.hasSkillEffect(Mob_SLOW_1)) {
							skillNum = Mob_SLOW_1;
				} else if (pc.hasSkillEffect(Mob_SLOW_18)) {
							skillNum = Mob_SLOW_18;
							}
						if (skillNum != 0) {
							pc.removeSkillEffect(skillNum);
							pc.removeSkillEffect(GREATER_HASTE);
							pc.setMoveSpeed(0);
							continue;
							}
						}
				} else if (_skillId == HOLY_WALK
							|| _skillId == MOVING_ACCELERATION
							|| _skillId == WIND_WALK) { // 호-리 워크, 무빙 악 세레이션, 윈드워크
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setBraveSpeed(4);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _getBuffIconDuration));
						pc.broadcastPacket(new S_SkillBrave(pc.getId(), 4, 0));
				} else if (_skillId == BLOODLUST) {
							 L1PcInstance pc = (L1PcInstance) cha;   
						if (pc.getBraveItemEquipped() > 0) { 
						      continue; 
						      } 
							 pc.setBraveSpeed(6);   
							 pc.sendPackets(new S_SkillBrave(pc.getId(), 6, _getBuffIconDuration));   
							 pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0)); 
					         }
				            }

				// ●●●● NPC 밖에 효과가 없는 스킬 ●●●●
				        if (_calcType == PC_NPC || _calcType == NPC_NPC) {
					// ★★★ 애완동물계 스킬 ★★★
				        if (_skillId == TAMING_MONSTER
							&& ((L1MonsterInstance) cha).getNpcTemplate().isTamable()) { // 테이밍몬스타
						int petcost = 0;
						Object[] petlist = _user.getPetList().values().toArray();
						for (Object pet : petlist) {
							// 현재의 애완동물 코스트
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // 에르프
							charisma += 12;
			} else if (_player.isWizard()) { // 위저드
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // 펫 코스트의 확인
							L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, false);
							_target = summon; // 타겟 교체
			} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1 더 이상의 monster를 조종할 수 없습니다.
						}
			} else if (_skillId == CREATE_ZOMBIE) { // 클리에 실 좀비
						int petcost = 0;
						Object[] petlist = _user.getPetList().values().toArray();
						for (Object pet : petlist) {
							// 현재의 애완동물 코스트
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // 에르프
							charisma += 12;
			} else if (_player.isWizard()) { // 위저드
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // 펫 코스트의 확인
							L1SummonInstance summon = new L1SummonInstance(_targetNpc, _user, true);
							_target = summon; // 타겟 교체
			} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1 더 이상의 monster를 조종할 수 없습니다.
						}
			} else if (_skillId == WEAK_ELEMENTAL) { // 위크에레멘탈
						if (cha instanceof L1MonsterInstance) {
							L1Npc npcTemp = ((L1MonsterInstance) cha)
							.getNpcTemplate();
						if (npcTemp.get_weakearth() == 1) { // 지
							cha.broadcastPacket(new S_SkillSound(cha.getId(), 2169));
			} else if (npcTemp.get_weakwater() == 1) { // 수
							cha.broadcastPacket(new S_SkillSound(cha.getId(), 2167));
			} else if (npcTemp.get_weakfire() == 1) { // 불
							cha.broadcastPacket(new S_SkillSound(cha.getId(), 2166));
			} else if (npcTemp.get_weakwind() == 1) { // 풍
							cha.broadcastPacket(new S_SkillSound(cha.getId(), 2168));
							}
						}
			} else if (_skillId == RETURN_TO_NATURE) { // 리탄트네이챠
						if (Config.RETURN_TO_NATURE && cha instanceof L1SummonInstance) {
							L1SummonInstance summon = (L1SummonInstance) cha;
							summon.broadcastPacket(new S_SkillSound(summon.getId(), 2245));
							summon.returnToNature();
			} else {
						if (_user instanceof L1PcInstance) {
							_player.sendPackets(new S_ServerMessage(79));
							}
						}
					}
				}

				// ■■■■ 개별 처리 여기까지 ■■■■

				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 1) {
					dmg *= -1; // 만약, 안 뎁트로 회복계 스킬이라면 데미지가 된다.
				}
				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 3) {
					dmg = 0; // 만약, 안 뎁트계 보스로 회복계 스킬이라면 무효
				}
				if ((cha instanceof L1TowerInstance
						|| cha instanceof L1DoorInstance) && dmg < 0) { // 가디안 타워, 문에 힐을 사용
					dmg = 0;
				}
				if (dmg != 0 || drainMana != 0) {
					_magic.commit(dmg, drainMana); // 데미지계, 회복계의 값을 타겟으로 위탁한다.
				}
				// 힐계 외에, 별도 회복했을 경우(V-T 등)
				if (heal > 0) {
				if ((heal + _user.getCurrentHp()) > _user.getMaxHp()) {
						_user.setCurrentHp(_user.getMaxHp());
		   } else {
						_user.setCurrentHp(heal + _user.getCurrentHp());
					}
				   }

				if (cha instanceof L1PcInstance) { // 타겟이 PC라면, AC와 스테이터스를 송신
					L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
					sendHappenMessage(pc); // 타겟으로 메세지를 송신
				    }
                	addMagicList(cha, false); // 타겟으로 마법의 효과 시간을 설정

				if (cha instanceof L1PcInstance) { // 타겟이 PC라면, 라이트 상태를 갱신
					L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				    }
			       }

			    if (_skillId == DETECTION || _skillId == COUNTER_DETECTION) { // 디 텍 숀, 카운터 디 텍 숀
				    detection(_player);
			        }

		} catch (Exception e) {
			       _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		           }
	               }

	/**
	 * CANCELLATION 왈가닥 세레이션으로 해제할 수 없는 스킬인지를 돌려준다.
	 */
	private boolean isNotCancelable(int skillNum) {
		return skillNum == ENCHANT_WEAPON || skillNum == BLESSED_ARMOR
				|| skillNum == ABSOLUTE_BARRIER || skillNum == ADVANCE_SPIRIT
				|| skillNum == SHOCK_STUN || skillNum == SHADOW_FANG
				|| skillNum == REDUCTION_ARMOR || skillNum == SOLID_CARRIAGE
				|| skillNum == COUNTER_BARRIER || skillNum == THUNDER_GRAB
				|| skillNum == DRAGON_SKIN || skillNum == PAYTIONS
				|| skillNum == BONEBREAK || skillNum == CONSENTRATION  
				|| skillNum == ANTA_SKILL_3 || skillNum == ANTA_SKILL_4 || skillNum == ANTA_SKILL_5;
	            }

	private void detection(L1PcInstance pc) {
		if (!pc.isGmInvis() && pc.isInvisble()) { // 자신
			pc.delInvis();
			pc.beginInvisTimer();
		    }
     	for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) {
		if (!tgt.isGmInvis() && tgt.isInvisble()) {
			tgt.delInvis();
			}
		    }
		    L1WorldTraps.getInstance().onDetection(pc);
	       }
	
	private void npcSpwan(L1Npc npcTemp, L1PcInstance c, int type){ // 큐브

		    if (npcTemp != null) {
		      Object obj = null;
		    try {
		       String s = npcTemp.getImpl();
		       Constructor constructor = Class.forName("l1j.server.server.model.Instance." + s+ "Instance").getConstructors()[0];
		       Object object[] = { npcTemp };
		       L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(object);
		       npc.setId(IdFactory.getInstance().nextId());
		       npc.setMap(c.getMapId());
		       if(c.getHeading() == 0){
		       npc.setX(c.getX());
		       npc.setY(c.getY() - 1);
		} else if (c.getHeading() == 1) {
		       npc.setX(c.getX() + 1);
		       npc.setY(c.getY() - 1);
		} else if (c.getHeading() == 2) {
		       npc.setX(c.getX() + 1);
		       npc.setY(c.getY());
		} else if (c.getHeading() == 3) {
		       npc.setX(c.getX() + 1);
		       npc.setY(c.getY() + 1);
		} else if (c.getHeading() == 4) {
		       npc.setX(c.getX());
		       npc.setY(c.getY() + 1);
		} else if (c.getHeading() == 5) {
		       npc.setX(c.getX() - 1);
		       npc.setY(c.getY() + 1);
		} else if (c.getHeading() == 6) {
		       npc.setX(c.getX() - 1);
		       npc.setY(c.getY());
		} else if (c.getHeading() == 7) {
		       npc.setX(c.getX() - 1);
		       npc.setY(c.getY() - 1);
		}
		       npc.setHomeX(npc.getX());
		       npc.setHomeY(npc.getY());
		       npc.setHeading(c.getHeading());	
		int sTime = 0;
		switch(type){
		case 0:
		case 1:
		case 2:
		      sTime = 4;
		break;		
		default:
		sTime = 5;
		break;
		}
		       npc.setCubeTime(sTime);
		       npc.setCubePc(c);
		       L1Cube.getInstance().add(type, npc);
		       L1World.getInstance().storeObject(npc);
		       L1World.getInstance().addVisibleObject(npc);	
		} catch (Exception e) {

		       e.printStackTrace();
		       }
		     }
	       }


	// 타겟으로 붙어 계산할 필요가 있을까 돌려준다
	private boolean isTargetCalc(L1Character cha) {
		// 공격 마법의 Non－PvP 판정
		if (_skill.getTarget().equals("attack") && _skillId != 18) { // 공격 마법
		if (isPcSummonPet(cha)) { // 대상이 PC, 사몬, 애완동물
		if (_player.getZoneType() == 1 || cha.getZoneType() == 1 // 공격하는 측 또는 공격받는 측이 세이프티 존
			|| _player.checkNonPvP(_player, cha)) { // Non-PvP 설정
				return false;
				}
			    }
		        }

		// fog 오브 슬리핑은 자기 자신은 대상외
		if (_skillId == FOG_OF_SLEEPING && _user.getId() == cha.getId()) {
			return false;
		}

		// 매스 슬로우는 자기 자신과 자신의 애완동물은 대상외
		if (_skillId == MASS_SLOW) {
		if (_user.getId() == cha.getId()) {
				return false;
			}
		if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
		if (_user.getId() == summon.getMaster().getId()) {
				return false;
				}
	} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
		if (_user.getId() == pet.getMaster().getId()) {
				return false;
				}
			    }
		        }

		// 매스 텔레포트는 자기 자신만 대상(동시에 크란원도 텔레포트 시킨다)
		if (_skillId == MASS_TELEPORT) {
		if (_user.getId() != cha.getId()) {
			return false;
			}
		    }
            return true;
	        }

	// 대상이 PC, 사몬, 애완동물인지를 돌려준다
	private boolean isPcSummonPet(L1Character cha) {
		if (_calcType == PC_PC) { // 대상이 PC
			  return true;
		      }

		if (_calcType == PC_NPC) {
			if (cha instanceof L1SummonInstance) { // 대상이 사몬
				L1SummonInstance summon = (L1SummonInstance) cha;
			if (summon.isExsistMaster()) { // 마스터가 있다
				return true;
				}
			    }
			if (cha instanceof L1PetInstance) { // 대상이 애완동물
				return true;
			    }
		        }
	   	        return false;
	            }

	// 타겟으로 대해 반드시 실패가 될까 돌려준다
	private boolean isTargetFailure(L1Character cha) {
		boolean isTU = false;
		boolean isErase = false;
		boolean isManaDrain = false;
		int undeadType = 0;
    	if (cha instanceof L1TowerInstance || cha instanceof L1DoorInstance) { // 가디안 타워, 문에는 확률계 스킬 무효
			return true;
		}
		if (cha instanceof L1PcInstance) { // 대  PC의 경우
			if (_calcType == PC_PC && _player.checkNonPvP(_player, cha)) { // Non-PvP 설정
				L1PcInstance pc = (L1PcInstance) cha;
			if (_player.getId() == pc.getId()
				|| (pc.getClanid() != 0 && _player.getClanid() == pc.getClanid())) {
				return false;
				}
				return true;
			    }
			    return false;
		        }

		if (cha instanceof L1MonsterInstance) { // 턴 안 뎁트 가능한가 판정
			isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
		}

		if (cha instanceof L1MonsterInstance) { // erase 매직 가능한가 판정
			isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
		}

		if (cha instanceof L1MonsterInstance) { // 안 뎁트의 판정
			undeadType = ((L1MonsterInstance) cha).getNpcTemplate().get_undead();
		}

		// 마나드레인이 가능한가?
		if (cha instanceof L1MonsterInstance) {
			isManaDrain = true;
		}
		/*
		 * 성공 제외 조건 1：T-U가 성공했지만, 대상이 안 뎁트는 아니다. 성공 제외 조건 2：T-U가 성공했지만, 대상에는 턴 안 뎁트 무효.
		 * 성공 제외 조건 3：슬로우, 매스 슬로우, 마나드레인, 엔탕르, erase 매직, 윈드산크루 무효
		 * 성공 제외 조건 4：마나드레인이 성공했지만, monster 이외의 경우
		 */
		if ((_skillId == TURN_UNDEAD && (undeadType == 0 || undeadType == 2))
				|| (_skillId == TURN_UNDEAD && isTU == false)
				|| ((_skillId == ERASE_MAGIC || _skillId == SLOW
				|| _skillId == MANA_DRAIN || _skillId == MASS_SLOW
				|| _skillId == ENTANGLE || _skillId == WIND_SHACKLE)
					&& isErase == false)
				|| (_skillId == MANA_DRAIN && isManaDrain == false)) {
			return true;
		    }
		    return false;
	        }

	// 카운터 매직이 발동했는지 돌려준다
	private boolean isUseCounterMagic(L1Character cha) {
		// 카운터 매직 유효한 스킬로 카운터 매직중
		if (_isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
			cha.removeSkillEffect(COUNTER_MAGIC);
			int castgfx = SkillsTable.getInstance(). getTemplate(COUNTER_MAGIC). getCastGfx();
			cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
			}
			return true;
		    }
		    return false;
	        }
            }
