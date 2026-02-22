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

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MagicInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_Spot;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.templates.L1Skills;
import static l1j.server.server.model.skill.L1SkillId.*;


public class L1Magic {
	private static Logger _log = Logger.getLogger(L1MagicInstance.class
			.getName());

	private int _calcType;

	private final int PC_PC = 1;

	private final int PC_NPC = 2;

	private final int NPC_PC = 3;

	private final int NPC_NPC = 4;

	private L1PcInstance _pc = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private int _leverage = 10; // 1/10배로 표현한다.

	private static Random _random = new Random();

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	public L1Magic(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			if (target instanceof L1PcInstance) {
				_calcType = PC_PC;
				_pc = (L1PcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = PC_NPC;
				_pc = (L1PcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		} else {
			if (target instanceof L1PcInstance) {
				_calcType = NPC_PC;
				_npc = (L1NpcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = NPC_NPC;
				_npc = (L1NpcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		}
	}

	/* ■■■■■■■■■■■■■■■ 마법 공통 함수 ■■■■■■■■■■■■■■ */
	private int getSpellPower() {
		int spellPower = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			spellPower = _pc.getSp();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			spellPower = _npc.getSp();
		}
		return spellPower;
	}

	private int getMagicLevel() {
		int magicLevel = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicLevel = _pc.getMagicLevel();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicLevel = _npc.getMagicLevel();
		}
		return magicLevel;
	}

	private int getMagicBonus() {
		int magicBonus = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicBonus = _pc.getMagicBonus();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicBonus = _npc.getMagicBonus();
		}
		return magicBonus;
	}

	private int getLawful() {
		int lawful = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			lawful = _pc.getLawful();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			lawful = _npc.getLawful();
		}
		return lawful;
	}

	private int getTargetMr() {
		int mr = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mr = _targetPc.getMr();
		} else {
			mr = _targetNpc.getMr();
		}
		return mr;
	}

	/* ■■■■■■■■■■■■■■ 성공 판정 ■■■■■■■■■■■■■ */
	// ●●●● 확률계 마법의 성공 판정 ●●●●
	// 계산방법
	// 공격측 포인트：LV + ((MagicBonus * 3) * 마법 고유 계수)
	// 방어측 포인트：((LV / 2) + (MR * 3)) / 2
	// 공격 성공율：공격측 포인트 - 방어측 포인트
	public boolean calcProbabilityMagic(int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		// 공격자가 GM권한의 경우100% 성공
		if (_pc != null && _pc.isGm() && _pc.getInventory().checkEquipped(300000)) {
			return true;
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912 && npcId <= 45915 // 원한으로 가득 찬 솔저＆솔저 고우스트
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				return false;
			}
			if (npcId == 45916 // 원한으로 가득 찬 하멜 장군
					&& !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				return false;
			}
			if (npcId == 45941 // 저주해진 무녀 사엘
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				return false;
			}
			if (npcId == 45752 // 바르로그(변신전)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45753 // 바르로그(변신 후)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				return false;
			}
			if (npcId == 45675 // 야히(변신전)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 81082 // 야히(변신 후)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45625 // 혼돈
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45674 // 죽음
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
			if (npcId == 45685 // 타락
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				return false;
			}
		}

 		if (!checkZone(skillId)) {
			return false;
		}
		if (skillId == CANCELLATION) {
			if (_calcType == PC_PC && _pc != null && _targetPc != null) {
				// 자기 자신의 경우는100% 성공
				if (_pc.getId() == _targetPc.getId()) {
					return true;
				}
				// 같은 클랜의 경우는100% 성공
				if (_pc.getClanid() > 0
						&& (_pc.getClanid() == _targetPc.getClanid())) {
					return true;
				}
				// 같은 파티의 경우는100% 성공
				if (_pc.isInParty()) {
					if (_pc.getParty().isMember(_targetPc)) {
						return true;
					}
				}
				// 대상이 인비지 상태일땐 켄슬 무효
			    if (_targetPc.isInvisble()){
			        return false;
			    }
				// 그 이외의 경우, 세이프티 존내에서는 무효
				if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) {
					return false;
				}
			}
			// 대상이 NPC, 사용자가 NPC의 경우는100% 성공
			if (_calcType == PC_NPC
					|| _calcType == NPC_PC || _calcType == NPC_NPC) {
				return true;
			}
		}
	/*	if (_calcType == PC_PC && _targetPc.hasSkillEffect(AntiMagic)){ //안티매직포션
			   if (skillId == WEAPON_BREAK || skillId == SLOW
			    || skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
			    || skillId == DARKNESS || skillId == WEAKNESS
			    || skillId == DISEASE || skillId == DECAY_POTION
			    || skillId == MASS_SLOW || skillId == ENTANGLE
			    || skillId == ERASE_MAGIC || skillId == EARTH_BIND
			    || skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE
			    || skillId == STRIKER_GALE || skillId == SHOCK_STUN
			    || skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE
			    || skillId == POLLUTE_WATER || skillId == CURSE_POISON || skillId == CURSE_BLIND // 안티포션상태일시에 마법무효화
			    || skillId == SILENCE || skillId == DARK_BLIND || skillId == FINAL_BURN) { 
			    _targetPc.removeSkillEffect(AntiMagic);
			    return false;
			   }
			  } */
	    if (_calcType == PC_NPC && _targetNpc.getLevel() >= 54) { // 54렙 이상 npc 에게 아래 마법 안걸림:즉 보스몬스터에게 사용불가
	    	if (skillId == WEAPON_BREAK || skillId == SLOW
					|| skillId == CURSE_PARALYZE /*|| skillId == MANA_DRAIN*/
				  /*|| skillId == DARKNESS || skillId == WEAKNESS
					|| skillId == DISEASE || skillId == DECAY_POTION*/
					|| skillId == MASS_SLOW || skillId == ENTANGLE
					|| skillId == ERASE_MAGIC /*|| skillId == EARTH_BIND*/ //어바 보스에게도 가능
					|| skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE
				//	|| skillId == STRIKER_GALE || skillId == SHOCK_STUN
					|| skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE
					|| skillId == FREEZING_BLIZZARD
					|| skillId == POLLUTE_WATER
					|| skillId == ELEMENTAL_FALL_DOWN
					|| skillId == RETURN_TO_NATURE
					|| skillId == CONFUSION || skillId == JOYOFPAIN
					|| skillId == BONEBREAK) { // 본브레이크
			return false;
			}
	    }
	    if (_calcType == PC_PC) {                      // 이레이즈 매직시 확률마법 100%걸리게
	        if (_targetPc.hasSkillEffect(ERASE_MAGIC)) {  
	      if (skillId == WEAPON_BREAK || skillId == SLOW
	        || skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
	        || skillId == DARKNESS || skillId == WEAKNESS
	        || skillId == DISEASE || skillId == DECAY_POTION
	        || skillId == MASS_SLOW || skillId == FOG_OF_SLEEPING 
	        || skillId == ICE_LANCE || skillId == FREEZING_BLIZZARD
	        || skillId == CONFUSION || skillId == JOYOFPAIN
	        || skillId == CANCELLATION || skillId == SILENCE) {
	       return true;
	            }
	          }
	       }

	    if (_calcType == PC_PC && _targetPc.getMr() >= 160){ //마방이 160이상일때 확률마법 100%안걸리게
	     if (skillId == WEAPON_BREAK || skillId == SLOW
	        || skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
	        || skillId == DARKNESS || skillId == WEAKNESS
	        || skillId == DISEASE || skillId == DECAY_POTION
	        || skillId == MASS_SLOW || skillId == FOG_OF_SLEEPING 
	        || skillId == ICE_LANCE || skillId == FREEZING_BLIZZARD
	        || skillId == CONFUSION || skillId == JOYOFPAIN
	        || skillId == CANCELLATION || skillId == SILENCE) {
	     return false;
	     }
	    }

		// 아스바인드중은 WB, 왈가닥 세레이션 이외 무효
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK
						&& skillId != CANCELLATION) {
					return false;
				}
			}
		} else {
			if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
				if (skillId != WEAPON_BREAK
						&& skillId != CANCELLATION) {
					return false;  
					}   
				} 
			}
		if (_calcType == PC_PC) {    // 이레중에 이레실패
			if (_targetPc.hasSkillEffect(ERASE_MAGIC)) {
				if (skillId == ERASE_MAGIC) {   
					return false;
				}
			}
		}	

		if (_calcType == PC_PC) {    // 어바중에 마나드레인실패
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId == MANA_DRAIN) {   
					return false;
				}
			}
		}		

		if (_calcType == PC_PC) {    // 어바중에 커스실패
			if (_targetPc.hasSkillEffect(EARTH_BIND)) {
				if (skillId == CURSE_PARALYZE) {   
					return false;
				}
			}
		}	
		if (_calcType == PC_PC) {    // 앱솔중에 인탱글 실패
			if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
				if (skillId == ENTANGLE) {   
					return false;
				}
			}
		}
		
		if (_calcType == PC_PC) {    // 아이스랜스중에 스턴 실패
			if (_targetPc.hasSkillEffect(ICE_LANCE)) {
				if (skillId == SHOCK_STUN) {   
					return false;
				}
			}
		}

		if (_calcType == PC_PC) {    // 아이스랜스중에 커스 실패
			if (_targetPc.hasSkillEffect(ICE_LANCE)) {
				if (skillId == CURSE_PARALYZE) {   
					return false;
				}
			}
		}

		if (_calcType == PC_PC) {    // 아이스랜스중에 마나드레인
			if (_targetPc.hasSkillEffect(ICE_LANCE)) {
				if (skillId == MANA_DRAIN) {   
					return false;
				}
			}
		}
		
		if (_calcType == PC_PC) {    // 디스중에디스실패
			if (_targetPc.hasSkillEffect(DISINTEGRATE)) {
				if (skillId == DISINTEGRATE) {   
					return false;
				}
			}
		}
		if (_calcType == PC_PC) {    // 폴다운중에 폴다운 실패
			if (_targetPc.hasSkillEffect(ELEMENTAL_FALL_DOWN)){
				if (skillId == ELEMENTAL_FALL_DOWN){
					return false;
				}
			}
		}
	/*	if (_calcType == PC_PC) {    // 스턴중에 스턴실패
			if (_targetPc.hasSkillEffect(SHOCK_STUN)) {
			    if (skillId == SHOCK_STUN) {   
			        return false;  
			    }
			}   
		}  */ //연스턴 가능하게 
		if (_calcType == PC_PC) {    // 본브레이크 중에 본브레이크 실패
			if (_targetPc.hasSkillEffect(BONEBREAK)) {
			    if (skillId == BONEBREAK) {   
			        return false;  
			    }
			}   
		}  
		probability = calcProbability(skillId);

		Random random = new Random();
		int rnd = random.nextInt(100) + 1;
		if (probability > 90) {
			probability = 90; // 최고 성공율을90%로 한다.
		}

		if (probability >= rnd) {
			isSuccess = true;
		} else {
			isSuccess = false;
		}

		// 확률계 마법 메세지
		if (!Config.ALT_ATKMSG) {
			return isSuccess;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC)
					&& !_pc.isGm()) {
				return isSuccess;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return isSuccess;
			}
		}

		String msg0 = "";
		String msg1 = "에";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // 어텍커가 PC의 경우
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // 어텍커가 NPC의 경우
			msg0 = _npc.getName();
		}

		msg2 = "probability:" + probability + "%";
		if (_calcType == NPC_PC || _calcType == PC_PC) { // 타겟이 PC의 경우
			msg4 = _targetPc.getName();
		} else if (_calcType == PC_NPC) { // 타겟이 NPC의 경우
			msg4 = _targetNpc.getName();
		}
		if (isSuccess == true) {
			msg3 = "성공";
		} else {
			msg3 = "실패";
		}

		if (_calcType == PC_PC || _calcType == PC_NPC) { // 어텍커가 PC의 경우
			_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
					msg4)); // \f1%0이%4%1%3 %2
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // 타겟이 PC의 경우
			_targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
					msg3, msg4)); // \f1%0이%4%1%3 %2
		}

		return isSuccess;
	}

	private boolean checkZone(int skillId) {
		if (_pc != null && _targetPc != null) {
			if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) { // 세이프티 존
				if (skillId == WEAPON_BREAK || skillId == SLOW
						|| skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
						|| skillId == DARKNESS || skillId == WEAKNESS
						|| skillId == DISEASE || skillId == DECAY_POTION
						|| skillId == MASS_SLOW || skillId == ENTANGLE
						|| skillId == ERASE_MAGIC || skillId == EARTH_BIND
						|| skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE
						|| skillId == STRIKER_GALE || skillId == SHOCK_STUN
						|| skillId == FOG_OF_SLEEPING || skillId == ICE_LANCE
						|| skillId == FREEZING_BLIZZARD
						|| skillId == POLLUTE_WATER
						|| skillId == ELEMENTAL_FALL_DOWN
						|| skillId == RETURN_TO_NATURE
						|| skillId == CONFUSION || skillId == JOYOFPAIN
						|| skillId == BONEBREAK) {  // 본브레이크
					return false;
				}
			}
		}
		return true;
	}

 //	 확률 마법에 대해서 시전자의 INT 수치 및 피시전자의 마방 반영
	private int calcProbability(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int attackLevel = 0;
		int defenseLevel = 0;
        int attackInt = 0; // 추가
        int defenseMr = 0; //추가
		int mr = 0;
		int probability = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackLevel = _pc.getLevel();
            attackInt = _pc.getInt(); // 추가
		} else {
			attackLevel = _npc.getLevel();
	        attackInt = _npc.getInt(); // 추가
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			defenseLevel = _targetPc.getLevel();
			defenseMr = _targetPc.getMr();
		} else {
			defenseLevel = _targetNpc.getLevel();
			defenseMr = _targetNpc.getMr();
			if (skillId == RETURN_TO_NATURE) {
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					defenseLevel = summon.getMaster().getLevel();
				}
			}
		}

		if (skillId == ELEMENTAL_FALL_DOWN || skillId == RETURN_TO_NATURE
				|| skillId == ENTANGLE || skillId == AREA_OF_SILENCE 
			    || skillId == WIND_SHACKLE || skillId == STRIKER_GALE 
			    || skillId == POLLUTE_WATER|| skillId == CONFUSION) {
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//          probability = (int) (20 + (attackLevel - defenseLevel) * 1);   
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel)) + 5 ); 
            if (probability > 40){  // 만약 확률이40% 이상이라면
            probability = 40; // 40고정
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			} 
		} else if (skillId == ERASE_MAGIC) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//          probability = (int) (17 + (attackLevel - defenseLevel) * 1);   // 이레이즈 매직
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel)) - 10 ); 
            if (probability > 23){  // 만약 확률이 23% 이상이라면
            probability = 23;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}	 

	  } else if (skillId == EARTH_BIND) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (20 + (attackLevel - defenseLevel) * 1);   // 어스 바인드
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel)) - 10 ); 
            if (probability > 25){  // 만약 확률이 25% 이상이라면
            probability = 25;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}	 

	   } else if (skillId == CURSE_POISON || skillId == CURSE_BLIND ||
			   skillId == DARKNESS || skillId == WEAKNESS ||
		       skillId == DISEASE || skillId == FOG_OF_SLEEPING ||
			   skillId == MASS_SLOW || skillId == DARK_BLIND) {
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (20 + (attackLevel - defenseLevel) * 1);  
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 10 ); 
           if (probability > 30){  // 만약 확률이 30% 이상이라면
            probability = 30;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}

	  } else if (skillId == TURN_UNDEAD) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (30 + (attackLevel - defenseLevel) * 1);   // 턴언데드
    	    probability = (int) (attackInt + 15 ); 
            if (probability > 55){  // 만약 확률이 55% 이상이라면
            probability = 55;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}

	  } else if (skillId == SLOW) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (25 + (attackLevel - defenseLevel) * 1);   // 슬로우
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 8 ); 
            if (probability > 35){  // 만약 확률이35% 이상이라면
            probability = 35;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}
	  } else if (skillId == CURSE_PARALYZE) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (7 + (attackLevel - defenseLevel) * 1);   // 커스 패럴라이즈
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 0 ); 
            if (probability > 15){  // 만약 확률이 15% 이상이라면
            probability = 15;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}	
	  } else if (skillId == MANA_DRAIN) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
	        probability = (int) (25 + (attackLevel - defenseLevel) * 1);   // 마나드레인
            if (probability > 35){  // 만약 확률이 35% 이상이라면
            probability = 35;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}	
	  } else if (skillId == CANCELLATION) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (17 + (attackLevel - defenseLevel) * 1);   // 캔슬레이션
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 0 ); 
            if (probability > 25){  // 만약 확률이 25% 이상이라면
            probability = 25;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}	
	  } else if (skillId == SILENCE) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (17 + (attackLevel - defenseLevel) * 1);   // 사일런스
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 3 ); 
            if (probability > 35){  // 만약 확률이 35% 이상이라면
            probability = 35;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}
	  } else if (skillId == DECAY_POTION) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (17 + (attackLevel - defenseLevel) * 1);   // 디케이포션
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 3 ); 
            if (probability > 35){  // 만약 확률이 35% 이상이라면
            probability = 35;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}
	  } else if (skillId == ICE_LANCE) {   
			//성공확률은 마법고유계수 x lv차이 + 기본확률
//	        probability = (int) (20 + (attackLevel - defenseLevel) * 1);   // 아이스랜스
    	    probability = (int) ((attackInt + (attackLevel - defenseLevel) - (defenseMr / 4)) + 7 ); 
            if (probability > 35){  // 만약 확률이 35% 이상이라면
            probability = 35;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}			

		} else if (skillId == SHOCK_STUN) {
			//성공확률은 기본 확률 + LV차이 1마다 +-2%
	        probability = (int) (60 + (attackLevel - defenseLevel) * 1);   // 쇼크스턴
            if (probability > 75) { // 만약 확률이 75% 이상이라면
            probability = 75; // 확률은 75%로 고정
			}
		    //오리지날 인트에 의하 마법 명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}
		} else if (skillId == COUNTER_BARRIER) {
	        probability = (int) (30 + (attackLevel - defenseLevel) * 1);   // 카운터 배리어
            if (probability > 35) { // 만약 확률이 35% 이상이라면
            probability = 35; // 확률은 35%로 고정
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}

		} else if (skillId ==  BONEBREAK) {
	        probability = (int) (60 + (attackLevel - defenseLevel) * 1);   // 본 브레이크
            if (probability > 70) { // 만약 확률이 70% 이상이라면
            probability = 70; // 확률은 70%로 고정
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}

		} else if (skillId ==  MOTALBODY) {
	        probability = (int) (15 + (attackLevel - defenseLevel) * 1);   // 모탈 바디
            if (probability > 20) { // 만약 확률이 20% 이상이라면
            probability = 20; // 확률은 20%로 고정
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}


	   } else if (skillId == PEAR || skillId == HOUROFDEATH
		    || skillId == GUARDBREAK || skillId == FREEZINGOFBREATH
		    || skillId== JOYOFPAIN || skillId == SHOCKSKIN) {
			//성공확률은 마법고유계수 x lv차이 + 기본확률
	        probability = (int) (15 + (attackLevel - defenseLevel) * 1);  
           if (probability > 25){  // 만약 확률이 25% 이상이라면
            probability = 25;
			}
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
			 probability += 2 * _pc.getOriginalMagicHit();
			}


/*		} else if (skillId == PEAR || skillId == HOUROFDEATH
		    || skillId == GUARDBREAK || skillId == FREEZINGOFBREATH
		    || skillId== JOYOFPAIN) {

		  } else if(skillId == SHOCKSKIN || skillId == MOTALBODY) {
		   probability = 20;
			Random random = new Random();
			int dice = l1skills.getProbabilityDice();
			int value = l1skills.getProbabilityValue();
			int diceCount = 0;
			diceCount = getMagicBonus() + getMagicLevel();

			if (diceCount < 1) {
				diceCount = 1;
			}

			for (int i = 0; i < diceCount; i++) {
				probability += (random.nextInt(dice) + 1 + value);
			}

			probability = probability * getLeverage() / 10;

			//오리지날인트에의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}

			if (probability >= getTargetMr()) {
				probability = 100;				
			} else {
				probability = 0;
			}
*/
	  } else {
			Random random = new Random();
			int dice = l1skills.getProbabilityDice();
			int diceCount = 0;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isWizard()) {
					diceCount = getMagicBonus() + getMagicLevel() + 1;
				} else if (_pc.isElf()) {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				} else {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				}
			} else {
				diceCount = getMagicBonus() + getMagicLevel();
			}
			if (diceCount < 1) {
				diceCount = 1;
			}

			for (int i = 0; i < diceCount; i++) {
				probability += (random.nextInt(dice) + 1);
			}
			probability = probability * getLeverage() / 10;
			//오리지날 인트에 의한 마법명중
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				probability += 2 * _pc.getOriginalMagicHit();
			}
			probability -= getTargetMr();

	        if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;
				} else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.2;
				} else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				probability *= probabilityRevision;
			}
		}
		   if (skillId == EARTH_BIND) { 
			   if (_calcType == PC_PC || _calcType == NPC_PC) {  
				   probability -= _targetPc.getRegistSustain(); 
				   }  
			/*	if (_targetPc.hasSkillEffect(ANTA_MAAN)) {	// 지룡의 마안 - 석화내성+3
					probability -= 3;		
				}  */
			
			   } else if (skillId == SHOCK_STUN || skillId == Mob_RANGESTUN_20
					    || skillId == Mob_SHOCKSTUN_18 || skillId == Mob_RANGESTUN_19  
						|| skillId == Mob_RANGESTUN_18 || skillId == Mob_RANGESTUN_30
				      ) { 
				   if (_calcType == PC_PC || _calcType == NPC_PC) {  
					   probability -= 2 * _targetPc.getRegistStun(); 
					   } 
				/*	if (_targetPc.hasSkillEffect(VALA_MAAN)) {	// 화룡의 마안 - 스턴내성+3
						probability -= 3;		
					}			*/
				
			   } else if (skillId == CURSE_PARALYZE) { 
				   if (_calcType == PC_PC || _calcType == NPC_PC) {  
					   probability -= _targetPc.getRegistStone();   
					   }  
				/*	if (_targetPc.hasSkillEffect(ANTA_MAAN)) {	// 지룡의 마안 - 석화내성+3
						probability -= 3;		
					}  */
				
			   } else if (skillId == FOG_OF_SLEEPING) {  
				   if (_calcType == PC_PC || _calcType == NPC_PC) { 
					   probability -= _targetPc.getRegistSleep();  
					   }  
				 /*  if (_targetPc.hasSkillEffect(LIND_MAAN)) {	// 풍룡의 마안 - 수면내성+3
						probability -= 3;		
					}  */
			   } else if (skillId == ICE_LANCE 
					   || skillId == FREEZING_BLIZZARD) {  
				   if (_calcType == PC_PC || _calcType == NPC_PC) { 
					   probability -= _targetPc.getRegistFreeze();  
					   }   
				/*   if (_targetPc.hasSkillEffect(FAFU_MAAN)			// 수룡의 마안 - 동빙내성+3
							|| _targetPc.hasSkillEffect(SHAPE_MAAN)) {	// 수룡의 마안 - 홀드내성+3
							probability -= 3;		
						}  */
			   } else if (skillId == CURSE_BLIND  
					   || skillId == DARKNESS || skillId == DARK_BLIND) { 
				   if (_calcType == PC_PC || _calcType == NPC_PC) { 
					   probability -= _targetPc.getRegistBlind();  
					   }  
				   
		/*	if (_targetPc.hasSkillEffect(BIRTH_MAAN)) {	// 탄생의 마안 - 암흑내성+3
				probability -= 3;		
			} */
		}
		return probability;
	}

	/* ■■■■■■■■■■■■■■ 마법 데미지 산출 ■■■■■■■■■■■■■■ */

	public int calcMagicDamage(int skillId) {
		int damage = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			damage = calcPcMagicDamage(skillId);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			damage = calcNpcMagicDamage(skillId);
		}

		damage = calcMrDefense(damage);
	   //////////혈원에게는 범위마법 데미지 0 디플추가////////////
		  if (_calcType == PC_PC){
		  if (_pc.getClanid() > 0 && (_pc.getClanid() == _targetPc.getClanid())) {
		  if (skillId == 17 || skillId == 22 || skillId == 25 || skillId == 53
		   || skillId == 53|| skillId == 59|| skillId == 62|| skillId == 65
		   || skillId == 70|| skillId == 74|| skillId == 80) { //미티어 포함한 범위마법들.. 
		   damage = 0;
		  }
		  }
		  }
		  if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.hasSkillEffect(LIND_MAAN)		// 풍룡의 마안 - 일정확률로 마법치명타+1
					|| _pc.hasSkillEffect(SHAPE_MAAN)	// 형상의 마안 - 일정확률로 마법치명타+1
					|| _pc.hasSkillEffect(LIFE_MAAN)) {	// 생명의 마안 - 일정확률로 마법치명타+1
					int MaanMagicCri = _random.nextInt(100) + 1;
					if (MaanMagicCri <= 50) {	// 확률
						damage *= 1.1;	
					}
				}
			} 
			if (_calcType == PC_NPC) {
				int npcId = _targetNpc.getNpcTemplate().get_npcId();
				if (npcId == 45953 || npcId == 45954	// 개미알
					||npcId == 81259 || npcId == 81260	// 테베 오시리스의 보물상자(테베 신전내)
					|| npcId == 81263 || npcId == 81264){	// 두목의 보물상자(기란 감옥 던전)
					damage = 0;
				}
			}
		//////////혈원에게는 범위마법 데미지 0 디플추가끝////////////
		if (_calcType == NPC_PC){
		  if (skillId == 30074 || skillId == 10003) { // 서먼(쿠거,미노) 범위마법 케릭에게 0
			damage = 0;					
		  }
		}
		// 데미지 최대치는 대상의 현재의 HP와 같게 한다.
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (damage > _targetPc.getCurrentHp()) {
				damage = _targetPc.getCurrentHp();
			}
		} else {
			if (damage > _targetNpc.getCurrentHp()) {
				damage = _targetNpc.getCurrentHp();
			}
		}
		return damage;
	}

	// ●●●● 플레이어에의 파이어월의 마법 데미지 산출 ●●●●
	public int calcPcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());
		if (_calcType == PC_PC){
			if (_pc.hasSkillEffect(LIND_MAAN)		// 풍룡의 마안 - 일정확률로 마법치명타+1
				|| _pc.hasSkillEffect(SHAPE_MAAN)	// 형상의 마안 - 일정확률로 마법치명타+1
				|| _pc.hasSkillEffect(LIFE_MAAN)) {	// 생명의 마안 - 일정확률로 마법치명타+1
				int MaanMagicCri = _random.nextInt(100) + 1;
				if (MaanMagicCri <= 50) {	// 확률
					dmg *= 1.1;	
				}
			}
			} 
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(FAFU_MAAN)			// 수룡의 마안 - 마법데미지 50%감소
				|| _targetPc.hasSkillEffect(LIFE_MAAN)		// 생명의 마안 - 마법데미지 50%감소
				|| _targetPc.hasSkillEffect(SHAPE_MAAN)		// 형상의 마안 - 마법데미지 50%감소
				|| _targetPc.hasSkillEffect(BIRTH_MAAN)) {	// 탄생의 마안 - 마법데미지 50%감소
				dmg /= 2;		
			} 
			
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //제브 레퀴 콜라얼리기
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_AREA_ICE_LANCE)) {
			dmg = 0;
		}
	    if (_targetPc.hasSkillEffect(Mob_Basill)) {
		     dmg = 0;
	    }
		if (_targetPc.hasSkillEffect(Mob_Coca)) {  //코카얼리기데미지0
			dmg = 0;
		}
		if (dmg < 0) {
			dmg = 0;
		}	
		if (_pc.getId() == _targetPc.getId()) { // 자기자신은 안맞도록 Qoo
			    dmg = 0;
		}		

		return dmg;
	}

	// ●●●● NPC 에의 파이어월의 마법 데미지 산출 ●●●●
	public int calcNpcFireWallDamage() {
		int dmg = 0;
		double attrDeffence = calcAttrResistance(L1Skills.ATTR_FIRE);
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(FIRE_WALL);
		dmg = (int) ((1.0 - attrDeffence) * l1skills.getDamageValue());
		if (_calcType == PC_NPC){
			if (_pc.hasSkillEffect(LIND_MAAN)		// 풍룡의 마안 - 일정확률로 마법치명타+1
				|| _pc.hasSkillEffect(SHAPE_MAAN)	// 형상의 마안 - 일정확률로 마법치명타+1
				|| _pc.hasSkillEffect(LIFE_MAAN)) {	// 생명의 마안 - 일정확률로 마법치명타+1
				int MaanMagicCri = _random.nextInt(100) + 1;
				if (MaanMagicCri <= 50) {	// 확률
					dmg *= 1.1;	
				}
			}
		} 
		if (_calcType == PC_NPC) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId == 45953 || npcId == 45954	// 개미알
				||npcId == 81259 || npcId == 81260	// 테베 오시리스의 보물상자(테베 신전내)
				|| npcId == 81263 || npcId == 81264){	// 두목의 보물상자(기란 감옥 던전)
				dmg = 0;
			}
		}
		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //제브 레퀴 콜라얼리기
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_AREA_ICE_LANCE)) { //파루리온 범위얼리기
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_Basill)) {  //바실얼리기데미지0
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_Coca)) {  //코카얼리기데미지0
			dmg = 0;
		}
    	if (dmg < 0) {
			dmg = 0;
		}

		return dmg;
	}

	// ●●●● 플레이어·NPC 로부터 플레이어에의 마법 데미지 산출 ●●●●
	private int calcPcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			// dmg = (dmg * getLeverage()) / 10;
			// 플레이어·NPC로 부터 플레이어에의 마법 데미지 개별 처리
			if (_calcType == PC_PC) {   
				dmg = (dmg * getLeverage()) / 10; // PC로 부터 입는 마법 데미지 일괄 수정
			} else if (_calcType == NPC_PC) {
				dmg = (dmg * getLeverage()) / 60; // NPC로 부터 입는 마법 데미지 일괄 수정
			}
			// 플레이어·NPC로 부터 플레이어에의 마법 데미지 개별 처리
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 방어용 기구에 의한 데미지 경감

		if (_targetPc.hasSkillEffect(COOKING_1_0_S) // 요리에 의한 데미지 경감
				|| _targetPc.hasSkillEffect(COOKING_1_1_S)
				|| _targetPc.hasSkillEffect(COOKING_1_2_S)
				|| _targetPc.hasSkillEffect(COOKING_1_3_S)
				|| _targetPc.hasSkillEffect(COOKING_1_4_S)
				|| _targetPc.hasSkillEffect(COOKING_1_5_S)
				|| _targetPc.hasSkillEffect(COOKING_1_6_S)
				|| _targetPc.hasSkillEffect(COOKING_1_7_S)
				|| _targetPc.hasSkillEffect(COOKING_1_8_S)
				|| _targetPc.hasSkillEffect(COOKING_1_9_S)
				|| _targetPc.hasSkillEffect(COOKING_1_10_S)
				|| _targetPc.hasSkillEffect(COOKING_1_11_S)
				|| _targetPc.hasSkillEffect(COOKING_1_12_S)
				|| _targetPc.hasSkillEffect(COOKING_1_13_S)
				|| _targetPc.hasSkillEffect(COOKING_1_14_S)
				|| _targetPc.hasSkillEffect(COOKING_1_15_S)
				|| _targetPc.hasSkillEffect(COOKING_1_16_S)
				|| _targetPc.hasSkillEffect(COOKING_1_17_S)
				|| _targetPc.hasSkillEffect(COOKING_1_18_S)
				|| _targetPc.hasSkillEffect(COOKING_1_19_S)
				|| _targetPc.hasSkillEffect(COOKING_1_20_S)
				|| _targetPc.hasSkillEffect(COOKING_1_21_S)
				|| _targetPc.hasSkillEffect(COOKING_1_22_S)
				|| _targetPc.hasSkillEffect(COOKING_1_23_S)) {
			dmg -= 5;
		}
		 /**흑단막대 데미지수정**/
		 if (_targetPc.hasSkillEffect(CALL_LIGHTNING)) {
		     if (dmg > 30) { 
		        dmg = 15; 
		     }
		 }
		 /**흑단막대 데미지수정**/

		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}

		if (_calcType == NPC_PC) { // 펫, 사몬으로부터 플레이어에 공격
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_npc instanceof L1PetInstance) {
					dmg /= 8;
				}else if (_npc.getMaster() != null && _targetPc == _npc.getMaster()){
				     dmg = 0;
			    }
        	if (_npc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _npc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					     }
					    }
					   }
					  }
			if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg -= dmg * 0.3;
		}
			if (_targetPc.hasSkillEffect(FAFU_MAAN)			// 수룡의 마안 - 마법데미지 50%감소
					|| _targetPc.hasSkillEffect(LIFE_MAAN)		// 생명의 마안 - 마법데미지 50%감소
					|| _targetPc.hasSkillEffect(SHAPE_MAAN)		// 형상의 마안 - 마법데미지 50%감소
					|| _targetPc.hasSkillEffect(BIRTH_MAAN)) {	// 탄생의 마안 - 마법데미지 50%감소
					dmg /= 2;		
				}	
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //제브 레퀴 콜라얼리기
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_AREA_ICE_LANCE)) { //파루리온 범위얼리기
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_Basill)) {  //바실얼리기데미지0
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(Mob_Coca)) {  //코카얼리기데미지0
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if (_targetPc.hasSkillEffect(PAYTIONS)) {
			dmg -= 2;
		}
		/*if(skillId == FOU_SLAYER){ 
			if(_pc.hasSkillEffect(L1SkillId.STATUS_SPOT2)){ 
			dmg += dmg/5; 
			}
		}*/
	  /*용기사 : 썬더 그랩*/
		if (skillId == THUNDER_GRAB) {
			if (_targetPc.hasSkillEffect(4001)) dmg += 3;
			else if (_targetPc.hasSkillEffect(4002)) dmg += 6;
			else if (_targetPc.hasSkillEffect(4003)) dmg += 9;

			int rnd = (int)(Math.random() * 100) + 1;
			if(rnd >= 40){		// 60%
				_targetPc.sendPackets(new S_Poison(_targetPc.getId(), 2));
				_targetPc.broadcastPacket(new S_Poison(_targetPc.getId(), 2));
				_targetPc.sendPackets(new S_Paralysis(4, true));
				_targetPc.setSkillEffect(EARTH_BIND, 2 * 1000);
			}
			_targetPc.removeSkillEffect(4001);
			_targetPc.removeSkillEffect(4002);
			_targetPc.removeSkillEffect(4003);
			_pc.sendPackets(new S_Spot(0));
		}
		if (_targetPc.hasSkillEffect(COUNTER_MIRROR)) {
			if (_calcType == PC_PC) {
				if (_targetPc.getWis() >= _random.nextInt(100)) {
					_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
							ActionCodes.ACTION_Damage));
					_targetPc.sendPackets(new S_SkillSound(_targetPc.getId(),
							4395));
					_targetPc.broadcastPacket(new S_SkillSound(_targetPc
							.getId(), 4395));
					_pc.receiveDamage(_targetPc, dmg);
					dmg = 0;
					_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
				}
			} else if (_calcType == NPC_PC) {
				int npcId = _npc.getNpcTemplate().get_npcId();
				if (npcId == 45681 || npcId == 45682 || npcId == 45683
						|| npcId == 45684) {
				} else if (!_npc.getNpcTemplate().get_IsErase()) {
				} else {
					if (_targetPc.getWis() >= _random.nextInt(100)) {
						_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
								ActionCodes.ACTION_Damage));
						_targetPc.sendPackets(new S_SkillSound(_targetPc
								.getId(), 4395));
						_targetPc.broadcastPacket(new S_SkillSound(_targetPc
								.getId(), 4395));
						_npc.receiveDamage(_targetPc, dmg);
						dmg = 0;
						_targetPc.killSkillEffectTimer(COUNTER_MIRROR);
					}
				}
			}
		}

		if (dmg < 0) {
			dmg = 0;
		}
		return dmg;
	}

	// ●●●● 플레이어·NPC 로부터 NPC 에의 데미지 산출 ●●●●
	private int calcNpcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		if (_calcType == PC_NPC) { // 플레이어로부터 애완동물, 사몬에 공격
			boolean isNowWar = false;
			int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
			if (castleId > 0) {
				isNowWar = WarTimeController.getInstance().isNowWar(castleId);
			}
			if (!isNowWar) {
				if (_targetNpc instanceof L1PetInstance) {
					dmg /= 8;
				}
				if (_targetNpc instanceof L1SummonInstance) {
					L1SummonInstance summon = (L1SummonInstance) _targetNpc;
					if (summon.isExsistMaster()) {
						dmg /= 8;
					}
				}
			}
		}

		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //제브 레퀴 콜라얼리기
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_AREA_ICE_LANCE)) { //파루리온 범위얼리기
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_Basill)) {  //바실얼리기데미지0
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(Mob_Coca)) {  //코카얼리기데미지0
			dmg = 0;
		}
		   /*용기사 : 썬더 그랩*/
		if (skillId == THUNDER_GRAB) {
			if (_targetNpc.hasSkillEffect(4001)) dmg += 3;
			else if (_targetNpc.hasSkillEffect(4002)) dmg += 6;
			else if (_targetNpc.hasSkillEffect(4003)) dmg += 9;

			int rnd = (int)(Math.random() * 100) + 1;
			if(rnd >= 40){		// 60%
				_targetNpc.broadcastPacket(new S_Poison(_targetNpc.getId(), 2));
				_targetNpc.setSkillEffect(EARTH_BIND, 2 * 1000);
				_targetNpc.setParalyzed(true);
				_targetNpc.setParalysisTime(2000);
			}
			_targetNpc.hasSkillEffect(4001);
			_targetNpc.hasSkillEffect(4002);
			_targetNpc.hasSkillEffect(4003);
			_pc.sendPackets(new S_Spot(0));
		}

		if (_calcType == PC_NPC && _targetNpc != null) {
			int npcId = _targetNpc.getNpcTemplate().get_npcId();
			if (npcId >= 45912 && npcId <= 45915 // 원한으로 가득 찬 솔저＆솔저 고우스트
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
				dmg = 0;
			}
			if (npcId == 45916 // 원한으로 가득 찬 하멜 장군
					&& !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
				dmg = 0;
			}
			if (npcId == 45941 // 저주해진 무녀 사엘
					&& !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
				dmg = 0;
			}
			if (npcId == 45752 // 바르로그(변신전)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45753 // 바르로그(변신 후)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
				dmg = 0;
			}
			if (npcId == 45675 // 야히(변신전)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 81082 // 야히(변신 후)
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45625 // 혼돈
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45674 // 사
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
			if (npcId == 45685 // 타락
					&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
				dmg = 0;
			}
		}
		return dmg;
	}

	// ●●●● damage_dice, damage_dice_count, damage_value, SP로부터 마법 데미지를 산출 ●●●●
	private int calcMagicDiceDamage(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		int charaIntelligence = 0;
		Random random = new Random();

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (random.nextInt(dice) + 1);
		}
		magicDamage += value;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0; // 무기에 의한 추가 데미지
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg;
		}
		if( skillId == MINDBREAK ){
			   if (_calcType == PC_PC){
			    if(_targetPc.getCurrentMp() >= 5) {
			     magicDamage = 25 ;
			     _targetPc.setCurrentMp(_targetPc.getCurrentMp() - 5);
			    }else{
			     magicDamage = 0;
			    }
			   } else if(_calcType == PC_NPC){
			    if(_targetNpc.getCurrentMp() >= 5) {
			     magicDamage = 25 ;
			     _targetNpc.setCurrentMp(_targetNpc.getCurrentMp() - 5);
			    }else{
			     magicDamage = 0;
			    }
			   }
			  }
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int spByItem = _pc.getSp() - _pc.getTrueSp(); // 아이템에 의한 SP변동
			charaIntelligence = _pc.getInt() + spByItem - 12;
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			int spByItem = _npc.getSp() - _npc.getTrueSp(); // 아이템에 의한 SP변동
			charaIntelligence = _npc.getInt() + spByItem - 12;
		}
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}

		double attrDeffence = calcAttrResistance(l1skills.getAttr());

		double coefficient = (1.0 - attrDeffence) * (1.0 + charaIntelligence * 3.0 / 32.0);
		if (coefficient < 0) {
			coefficient = 0;
		}

		magicDamage *= coefficient;

		return magicDamage;
	}

	// ●●●● 힐 회복량(대안 데드에는 데미지)을 산출 ●●●●
	public int calcHealing(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;

		int magicBonus = getMagicBonus();
		if (magicBonus > 10) {
			magicBonus = 10;
		}

		Random random = new Random();
		int diceCount = value + magicBonus;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (magicDamage * getLeverage()) / 10;

		return magicDamage;
	}

	// ●●●● MR에 의한 데미지 경감 ●●●●
	private int calcMrDefense(int dmg) {
		  
		  if (getTargetMr() >= 0 && getTargetMr() <=100) {
		   dmg *= (1 + (100 - getTargetMr()) * 0.01);
		  } else {
		   dmg *= (1 - (getTargetMr() - 100) * 0.002);
		  }

		  return dmg;
		 }

	 // ●●●● 속성에 의한 데미지 경감 ●●●●
	// attr:0.무속성 마법, 1.땅마법, 2.불마법, 4.수해법, 8.바람 마법(, 16.광마법)
	private double calcAttrResistance(int attr) {
		int resist = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (attr == L1Skills.ATTR_EARTH) {
				resist = _targetPc.getEarth();
			} else if (attr == L1Skills.ATTR_FIRE) {
				resist = _targetPc.getFire();
			} else if (attr == L1Skills.ATTR_WATER) {
				resist = _targetPc.getWater();
			} else if (attr == L1Skills.ATTR_WIND) {
				resist = _targetPc.getWind();
			}
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
		}

		int resistFloor = (int) (0.4 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}

		double attrDeffence = resistFloor / 100.0;

		return attrDeffence;
	}

	/* ■■■■■■■■■■■■■■■ 계산 결과 반영 ■■■■■■■■■■■■■■■ */

	public void commit(int damage, int drainMana) {
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			commitPc(damage, drainMana);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			commitNpc(damage, drainMana);
		}

		// 데미지치 및 명중율 확인용 메세지
		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC)
					&& !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return;
			}
		}

		String msg0 = "";
		String msg1 = "에";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) {// 어텍커가 PC의 경우
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // 어텍커가 NPC의 경우
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // 타겟이 PC의 경우
			msg4 = _targetPc.getName();
			msg2 = "THP" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) { // 타겟이 NPC의 경우
			msg4 = _targetNpc.getName();
			msg2 = "THp" + _targetNpc.getCurrentHp();
		}

		msg3 = damage + "주었다";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // 어텍커가 PC의 경우
			_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
					msg4)); // \f1%0이%4%1%3 %2
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // 타겟이 PC의 경우
			_targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
					msg3, msg4)); // \f1%0이%4%1%3 %2
		}
	}

	// ●●●● 플레이어에 계산 결과를 반영 ●●●●
	private void commitPc(int damage, int drainMana) {
		if (_calcType == PC_PC) {
			if (drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (drainMana > _targetPc.getCurrentMp()) {
					drainMana = _targetPc.getCurrentMp();
				}
				int newMp = _pc.getCurrentMp() + drainMana;
				_pc.setCurrentMp(newMp);
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, damage);
		}
	}

	// ●●●● NPC에 계산 결과를 반영 ●●●●
	private void commitNpc(int damage, int drainMana) {
		if (_calcType == PC_NPC) {
			if (drainMana > 0) {
				int drainValue = _targetNpc.drainMana(drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, damage);
		}
	}
}