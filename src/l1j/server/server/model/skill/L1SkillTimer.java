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

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;
import static l1j.server.server.model.skill.L1SkillId.*;

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

/*
 * XXX 2008/02/13 vala 본래, 이 클래스는 있어서는 안되지만 잠정 처치.
 */
class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		if (skillId == LIGHT) { // 라이트
			if (cha instanceof L1PcInstance) {
				if (!cha.isInvisble()) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				}
			}
		} else if (skillId == GLOWING_AURA) { // 그로윙오라
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
		} else if (skillId == SHINING_AURA) { // 샤이닝오라
			cha.addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
		} else if (skillId == BRAVE_AURA) { // 치우침 이브 아우라
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
		} else if (skillId == SHIELD) { // 쉴드(shield)
			cha.addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
		} else if (skillId == BLIND_HIDING) { // 브라인드하이딘그
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
		} else if (skillId == UNCANNY_DODGE) { // 언케니닷지
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.addDg(5);
            }
        } else if (skillId == MIRRORIMG) { // 미러이미지
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.addDg(5);
           }
        } else if (skillId == PEAR) { // 피어
            if (cha instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) cha;
                pc.addDg(3);
           }
		} else if (skillId == SHADOW_ARMOR) { // 그림자 아모
			cha.addAc(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
		} else if (skillId == DRESS_DEXTERITY) { // 드레스데크스타리티
			cha.addDex((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 2, 0));
			}
		} else if (skillId == DRESS_MIGHTY) { // 드레스마이티
			cha.addStr((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 2, 0));
			}
		} else if (skillId == SHADOW_FANG) { // 샤드우팡
			cha.addDmgup(-5);
		} else if (skillId == ENCHANT_WEAPON) { // 엔체트웨폰
			cha.addDmgup(-2);
		} else if (skillId == BLESSED_ARMOR) { // 브레스드아마
			cha.addAc(3);
		} else if (skillId == EARTH_BLESS) { // 지구 호흡
			cha.addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
		} else if (skillId == RESIST_MAGIC) { // 레지스터 매직
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		} else if (skillId == CLEAR_MIND) { // 클리어 마인드
			cha.addWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
		} else if (skillId == RESIST_ELEMENTAL) { // 레지스터 엘리먼트
			cha.addWind(-10);
			cha.addWater(-10);
			cha.addFire(-10);
			cha.addEarth(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == ELEMENTAL_PROTECTION) { // 일렉트로닉 멘탈 프로텍션
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.addEarth(-50);
				} else if (attr == 2) {
					cha.addFire(-50);
				} else if (attr == 4) {
					cha.addWater(-50);
				} else if (attr == 8) {
					cha.addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		} else if (skillId == ELEMENTAL_FALL_DOWN) { // 일렉트로닉 멘탈 폴 다운
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					pc.addEarth(i);
					break;
				case 2:
					pc.addFire(i);
					break;
				case 4:
					pc.addWater(i);
					break;
				case 8:
					pc.addWind(i);
					break;
				default:
					break;
				}
				pc.setAddAttrKind(0);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			} else if (cha instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				int attr = npc.getAddAttrKind();
				int i = 50;
				switch (attr) {
				case 1:
					npc.addEarth(i);
					break;
				case 2:
					npc.addFire(i);
					break;
				case 4:
					npc.addWater(i);
					break;
				case 8:
					npc.addWind(i);
					break;
				default:
					break;
				}
				npc.setAddAttrKind(0);
			}
		} else if (skillId == IRON_SKIN) { // 아이언 스킨
			cha.addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
		} else if (skillId == EARTH_SKIN) { // 지구 스킨
			cha.addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
		} else if (skillId == GUARDBREAK) {  //가드브레이크
	        if (cha instanceof L1PcInstance) {
		         L1PcInstance pc = (L1PcInstance) cha;
		         pc.addAc(-15);
	        }
	      }else if (skillId == HOUROFDEATH) {  //호러오브데스
		   if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addStr((byte) 5);
			pc.addInt((byte) 5);
		      }
	        }else 
			
			if (skillId == ANTARAS) { // 안타
			L1PcInstance pc = (L1PcInstance) cha;
			L1PolyMorph.undoPoly(cha);
			pc.addAc(15);
			pc.addMaxHp(-127);
		} else if (skillId == PAPORION) { // 파푸
			L1PcInstance pc = (L1PcInstance) cha;
			L1PolyMorph.undoPoly(cha);
			pc.addMr(-30);
			pc.addFire(-30);
			pc.addWind(-30);
			pc.addEarth(-30);
			pc.addWater(-30);
		} else if (skillId == BALAKAS) { // 발라
			L1PcInstance pc = (L1PcInstance) cha;
			L1PolyMorph.undoPoly(cha);
			pc.addWis((byte) -5);
			pc.addDex((byte) -5);
			pc.addStr((byte) -5);
			pc.addInt((byte) -5);
			pc.addCon((byte) -5);
	/*	} else if(skillId == BLOOD_LUST){  //블러드러스트
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
		         } */
		/*환술사:일루젼 오거 */
		} else if (skillId == OUGU) {  
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDmgup(-4);
			pc.addHitup(-4);
		/*환술사:일루션 리치*/
		} else if (skillId == RICH) {  
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addSp(-2);
			pc.sendPackets(new S_SPMR(pc));
		/*환술사:일루션 다이아골렘 */
		} else if (skillId == DIAGOLEM) {  
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addAc(20);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
		/*코마 버프사 #켄파치# */
                 } else if (skillId == COMA) { 
                        if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(-3);
                        pc.addBowHitup(-3);
                        pc.addStr(-5);
                        pc.addDex(-5);
                        pc.addCon(-1);
                        pc.addAc(3);
                        }
                 /*코마 버프사 2 #켄파치# */
                   } else if (skillId == COMABUFF) { 
                        if (cha instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) cha;
                        pc.addHitup(-5);
                        pc.addBowHitup(-5);
                        pc.addStr(-5);
                        pc.addDex(-5);
                        pc.addCon(-1);
   						pc.addAc(8);
						pc.addSp(-1);
						pc.sendPackets(new S_SPMR(pc));
                   } //추가!  켄파치 //
     /* 상아탑 버프 */
  } else if (skillId == SANGA) {
   if(cha instanceof L1PcInstance) {
               L1PcInstance pc = (L1PcInstance) cha;
           pc.addHitup(-5);
           pc.addDmgup(-10);
           pc.addBowHitup(-5);
           pc.addBowDmgup(-10);
           pc.addStr(-3);
           pc.addDex(-3);
           pc.addInt(-3);
           pc.addCon(-3);
           pc.addWis(-3);
           pc.addSp(-3);
           pc.sendPackets(new S_SPMR(pc));
        }
  } else if (skillId == SANGABUFF) {
   if(cha instanceof L1PcInstance) {
               L1PcInstance pc = (L1PcInstance) cha;
               pc.addHitup(-5);
               pc.addDmgup(-5);
               pc.addBowHitup(-5);
               pc.addBowDmgup(-5);
               pc.addStr(-2);
               pc.addDex(-2);
               pc.addInt(-2);
               pc.addCon(-2);
               pc.addWis(-2);
               pc.addSp(-1);
               pc.sendPackets(new S_SPMR(pc));
        }	   /* 상아탑 버프 */
// 크레이
  } else if (skillId == CRAY) {
	   if(cha instanceof L1PcInstance) {
	   L1PcInstance pc = (L1PcInstance) cha;
	      pc.addHitup(-5);
          pc.addDmgup(-1);
          pc.addBowHitup(-5);
          pc.addBowDmgup(-1);
          pc.addEarth(-30);
          pc.addMaxHp(-100);
          pc.addMaxMp(-50);
		  pc.addHpr(-3);
		  pc.addMpr(-3);
		  pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
          pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		  pc.sendPackets(new S_SPMR(pc));
    }
	  
	  } else if (skillId == ANTA_BLOOD) {
	   if(cha instanceof L1PcInstance) {
	       L1PcInstance pc = (L1PcInstance) cha;
	              pc.addAc(2);
				   pc.sendPackets(new S_SPMR(pc));
	        }
	// 크레이
	 //운세버프
	  } else if (skillId == LUCK_A) { 
	        if (cha instanceof L1PcInstance) {
	          L1PcInstance pc = (L1PcInstance) cha;
	             pc.addHitup(-2);
	             pc.addBowHitup(-2);
	             pc.addDmgup(-2);
	             pc.addBowDmgup(-2); //추가
	             pc.addSp(-2);
	             pc.addMaxHp(-50);
	             pc.addMaxMp(-30);
	             pc.addMpr(-3);
	             pc.sendPackets(new S_SPMR(pc));
	            }
	  } else if (skillId == LUCK_B) { 
	        if (cha instanceof L1PcInstance) {
	          L1PcInstance pc = (L1PcInstance) cha;
	             pc.addHitup(-2);
	             pc.addBowHitup(-2);
	             pc.addSp(-1);
	             pc.addMaxHp(-50);
	             pc.addMaxMp(-30);
	             pc.sendPackets(new S_SPMR(pc));
	             
	         }
	  } else if (skillId == LUCK_C) { 
	        if (cha instanceof L1PcInstance) {
	          L1PcInstance pc = (L1PcInstance) cha;
	          pc.addMaxHp(-50);
	          pc.addMaxMp(-30);
	          pc.addAc(2);
	            }
	  } else if (skillId == LUCK_D) { 
	        if (cha instanceof L1PcInstance) {
	          L1PcInstance pc = (L1PcInstance) cha;
	             pc.addAc(1);
	                           
	         }
     		/*환술사:일루젼 아바타*/
		} else if (skillId == AVATA) {  
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDmgup(-10);
			pc.addSp(-6);
			pc.sendPackets(new S_SPMR(pc));
		} else if(skillId == ARMBREAKER){ //암브레이커
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addDmgup(2);		
		} else if(skillId == INSIGHT){  //인사이트
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addStr((byte)-1);
			pc.addDex((byte)-1);
			pc.addCon((byte)-1);
			pc.addInt((byte)-1);
			pc.addCha((byte)-1);
			pc.addWis((byte)-1);
		} else if(skillId == PANIC){ //패닉
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addStr((byte)1);
			pc.addDex((byte)1);
			pc.addCon((byte)1);
			pc.addInt((byte)1);
			pc.addCha((byte)1);
			pc.addWis((byte)1);
		} else if (skillId == 5003) { // 큐브:1단계
			if(cha instanceof L1PcInstance){
			L1PcInstance c = (L1PcInstance) cha;
			c.addFire((byte) -30);
			c.sendPackets(new S_OwnCharAttrDef(c));
			//System.out.println("큐브 끝났음  " + c.getEarth());			
			}
		} else if (skillId == 5001) { // 큐브:2단계
			if(cha instanceof L1PcInstance){
			L1PcInstance c = (L1PcInstance) cha;
			c.addEarth((byte) -30);
			c.sendPackets(new S_OwnCharAttrDef(c));
			}
		} else if (skillId == 5002) { // 큐브:3단계
			if(cha instanceof L1PcInstance){
			L1PcInstance c = (L1PcInstance) cha;
			c.addWind((byte) -30);
			c.sendPackets(new S_OwnCharAttrDef(c));
			}
		} else if (skillId == 7979) { // 큐브:mr 부분
			if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			pc.addMr(pc.CubeMr);
			pc.CubeMr = 0;
			pc.sendPackets(new S_SPMR(pc));
			}
		} else if (skillId == PHYSICAL_ENCHANT_STR) { // 피지컬 엔챤트：STR
			cha.addStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 5, 0));
			}
		} else if (skillId == PHYSICAL_ENCHANT_DEX) { // 피지컬 엔챤트：DEX
			cha.addDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 5, 0));
			}
		} else if (skillId == FIRE_WEAPON) { // 파이아웨폰
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
		} else if (skillId == FIRE_BLESS) { // 파이어 호흡
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
		} else if (skillId == BURNING_WEAPON) { // 바닝웨폰
			cha.addDmgup(-6);
			cha.addHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
		} else if (skillId == BLESS_WEAPON) { // 브레스웨폰
			cha.addDmgup(-2);
			cha.addHitup(-2);
			cha.addBowHitup(-2);
		} else if (skillId == WIND_SHOT) { // 윈도우 쇼트
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
		} else if (skillId == STORM_EYE) { // 스토무아이
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
		} else if (skillId == STORM_SHOT) { // 스톰 쇼트
			cha.addBowDmgup(-5);
			cha.addBowHitup(1);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
		} else if (skillId == BERSERKERS) { // 바서카
			cha.addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-2);
		} else if (skillId == FAFU_MAAN) { // 수룡의 마안
		} else if (skillId == ANTA_MAAN) { // 지룡의 마안
		} else if (skillId == LIND_MAAN) { // 풍룡의 마안
		} else if (skillId == VALA_MAAN) { // 화룡의 마안
		} else if (skillId == LIFE_MAAN) { // 생명의 마안
		} else if (skillId == BIRTH_MAAN) { // 탄생의 마안
		} else if (skillId == SHAPE_MAAN) { // 형상의 마안
		} else if (skillId == MAGE_DISEASE) { // 아크메이지의 지팡이 디지즈
			cha.addDmgup(-6);
			cha.addAc(-12);
		} else if (skillId == SHAPE_CHANGE) { // 셰이프 체인지
			L1PolyMorph.undoPoly(cha);
		} else if (skillId == ADVANCE_SPIRIT) { // advanced 스피리츠
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc
						. sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
						.getMaxHp()));
				if (pc.isInParty()) { // 파티중
					pc.getParty().updateMiniHP(pc);
				}
				pc
						. sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
						.getMaxMp()));
			}
		} else if (skillId == HASTE || skillId == GREATER_HASTE) { // 헤이 파업, 그레이터 헤이 파업
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		} else if (skillId == HOLY_WALK || skillId == MOVING_ACCELERATION
				|| skillId == WIND_WALK || skillId == BLOODLUST) {
			// 호-리 워크, 무빙 악 세레이션, 윈드워크
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}

		// ****** 상태 변화가 풀렸을 경우
		else if (skillId == CURSE_BLIND || skillId == DARKNESS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		} else if (skillId == CURSE_PARALYZE
			|| skillId == Mob_CURSEPARALYZ_19 || skillId == Mob_CURSEPARALYZ_18		//////// 몹마법 해제 - 몹스킬패턴 추가
			|| skillId == Mob_CURSEPARALYZ_30 || skillId == Mob_CURSEPARALYZ_SHORT_18 ////// 몹마법 해제 - 몹스킬패턴 추가
			) { // 카즈파라라이즈
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS,
						false));
			}
		} else if (skillId == WEAKNESS
			|| skillId == Mob_WEAKNESS_1		/////////////////////////////////////////// 몹마법 해제 - 몹스킬패턴 추가
			) { // 위크네스
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(5);
				pc.addHitup(1);
			}
		} else if (skillId == DISEASE
			|| skillId == Mob_DISEASE_1 || skillId == Mob_DISEASE_30		//////////////////// 몹마법 해제 - 몹스킬패턴 추가
			) { // 디지즈
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(6);
				pc.addAc(-12);
			}		
		} else if (skillId == ICE_LANCE || skillId == FREEZING_BLIZZARD
				|| skillId == Mob_AREA_ICE_LANCE || skillId == Mob_CALL_LIGHTNING_ICE) { // 아이스 랑스, freezing 블리자드
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		} else if (skillId == EARTH_BIND
			|| skillId == Mob_Basill || skillId == Mob_Coca  ////////////////////// 몬스터 모션을 추가하기 위해 - 몹스킬패턴 추가
		) { // 아스바인드
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		} else if (skillId == SHOCK_STUN || skillId == Mob_RANGESTUN_20
			|| skillId == Mob_SHOCKSTUN_18 || skillId == Mob_RANGESTUN_19 //////////////// 몬스터 모션을 추가하기 위해 - 몹스킬패턴 추가
			|| skillId == Mob_RANGESTUN_18 || skillId == Mob_RANGESTUN_30 //////////////// 몬스터 모션을 추가하기 위해 - 몹스킬패턴 추가
			|| skillId == ANTA_SKILL_3 || skillId == ANTA_SKILL_4 || skillId == ANTA_SKILL_5 // 안타라스 용언
			) { // 쇼크 스탠
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}	
		} else if (skillId == BONEBREAK) { // 본브레이크
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		} else if (skillId == FOG_OF_SLEEPING) { // fog 오브 슬리핑
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		} else if (skillId == ABSOLUTE_BARRIER) { // 아브소르트바리아
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.startHpRegeneration();
				pc.startMpRegeneration();
				pc.startMpRegenerationByDoll();
			}
		} else if (skillId == WIND_SHACKLE
			|| skillId == Mob_WINDSHACKLE_1					////////////////////////// 몹마법 해제 - 몹스킬패턴 추가
			) { // 윈드산크루
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
			}
		} else if (skillId == SLOW || skillId == ENTANGLE
				|| skillId == Mob_SLOW_1 || skillId == Mob_SLOW_18
				|| skillId == MASS_SLOW) { // 슬로우, 엔탕르, 매스 슬로우
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		} else if (skillId == STATUS_FREEZE) { // Freeze
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
 			}
		}

		// ****** 아이템 관계
		else if (skillId == STATUS_BRAVE) { // 치우침 이브 일부등
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
			cha.setBraveSpeed(0);
		} else if (skillId == STATUS_RIBRAVE) {  // 유그드라 계열
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
			}
			cha.setBraveSpeed(0);
		} else if (skillId == STATUS_HASTE) { // 그린 일부
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		} else if (skillId == STATUS_BLUE_POTION) { // 블루 일부
		} else if (skillId == STATUS_UNDERWATER_BREATH) { // 에바의 축복＆mermaid의 비늘
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
		} else if (skillId == STATUS_WISDOM_POTION) { // 위즈 댐 일부
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				cha.addSp(-2);
				pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
		} else if (skillId == STATUS_CHAT_PROHIBITED) { // 채팅 금지
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288)); // 채팅을 할 수 있게 되었습니다.
			} 
		} 

		// ****** 독관계
		else if (skillId == STATUS_POISON) { // 데미지독
			cha.curePoison();
		}

		// ****** 요리 관계
		else if (skillId == COOKING_1_0_N || skillId == COOKING_1_0_S) { // 후로팅아이스테이키
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_1_N || skillId == COOKING_1_1_S) { // 베어 스테이크
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(),
						pc.getMaxHp()));
				if (pc.isInParty()) { // 파티중
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_2_N || skillId == COOKING_1_2_S) { // 너트떡
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_3_N || skillId == COOKING_1_3_S) { // 의각의 치즈 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_4_N || skillId == COOKING_1_4_S) { // 과일샐러드
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(),
						pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_5_N || skillId == COOKING_1_5_S) { // 프루츠 단 식초 고명
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-3);
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_6_N || skillId == COOKING_1_6_S) { // 저육의 꼬치구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_1_7_N || skillId == COOKING_1_7_S) { // 버섯 스프
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
		 } else if (skillId == COOKING_1_8_N || skillId == COOKING_1_8_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addDmgup(-1);
			    pc.addHitup(-1);
			    pc.sendPackets(new S_PacketBox(53, 8, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_9_N || skillId == COOKING_1_9_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addMaxHp(-30);
			    pc.addMaxMp(-30);   
			    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			    if (pc.isInParty()) { // 파티중
			     pc.getParty().updateMiniHP(pc);
			    }
			    pc.sendPackets(new S_PacketBox(53, 9, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_10_N || skillId == COOKING_1_10_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addAc(2);
			    pc.sendPackets(new S_OwnCharStatus(pc));
			    pc.sendPackets(new S_PacketBox(53, 10, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_11_N || skillId == COOKING_1_11_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addBowHitRate(-1);
			    pc.addBowDmgup(-1);
			    pc.sendPackets(new S_PacketBox(53, 11, 0));
			    pc.setCookingId(0);
			   }   
			  } else if (skillId == COOKING_1_12_N || skillId == COOKING_1_12_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addHpr(-2);
			    pc.addMpr(-2);
			    pc.sendPackets(new S_PacketBox(53, 12, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_13_N || skillId == COOKING_1_13_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addMr(-10);
			    pc.sendPackets(new S_SPMR(pc));
			    pc.sendPackets(new S_PacketBox(53, 13, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_14_N || skillId == COOKING_1_14_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addSp(-1);
			    pc.sendPackets(new S_SPMR(pc));
			    pc.sendPackets(new S_OwnCharStatus(pc));
			    pc.sendPackets(new S_PacketBox(53, 14, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_15_N || skillId == COOKING_1_15_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.sendPackets(new S_PacketBox(53, 15, 0));
			    pc.setDessertId(0);
			   }
			  } else if (skillId == COOKING_1_16_N || skillId == COOKING_1_16_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addBowHitRate(-2);
			    pc.addBowDmgup(-1); 
			    pc.sendPackets(new S_PacketBox(53, 16, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_17_N || skillId == COOKING_1_17_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addMaxHp(-50);
			    pc.addMaxMp(-50);   
			    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			    if (pc.isInParty()) { // 파티중
			     pc.getParty().updateMiniHP(pc);
			    } 
			    pc.sendPackets(new S_PacketBox(53, 17, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_18_N || skillId == COOKING_1_18_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addDmgup(-1);
			    pc.addHitup(-2); 
			    pc.sendPackets(new S_PacketBox(53, 18, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_19_N || skillId == COOKING_1_19_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addAc(3);
			    pc.sendPackets(new S_OwnCharStatus(pc));
			    pc.sendPackets(new S_PacketBox(53, 19, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_20_N || skillId == COOKING_1_20_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addWind(-10);
			    pc.addWater(-10);
			    pc.addFire(-10);
			    pc.addEarth(-10);
			    pc.addMr(-15);
			    pc.sendPackets(new S_SPMR(pc));   
			    pc.sendPackets(new S_OwnCharAttrDef(pc)); 
			    pc.sendPackets(new S_PacketBox(53, 20, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_21_N || skillId == COOKING_1_21_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addSp(-2);
			    pc.addMpr(-2);
			    pc.sendPackets(new S_SPMR(pc));
			    pc.sendPackets(new S_OwnCharStatus(pc)); 
			    pc.sendPackets(new S_PacketBox(53, 21, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_22_N || skillId == COOKING_1_22_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.addHpr(-2);
			    pc.addMaxHp(-30);
			    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));  
			    pc.sendPackets(new S_PacketBox(53, 22, 0));
			    pc.setCookingId(0);
			   }
			  } else if (skillId == COOKING_1_23_N || skillId == COOKING_1_23_S) { 
			   if (cha instanceof L1PcInstance) {
			    L1PcInstance pc = (L1PcInstance) cha;
			    pc.sendPackets(new S_PacketBox(53, 23, 0));
			    pc.setDessertId(0);
			   }
			  
			 

			  if (cha instanceof L1PcInstance) {
			   L1PcInstance pc = (L1PcInstance) cha;
			   sendStopMessage(pc, skillId);
			   pc.sendPackets(new S_OwnCharStatus(pc));
			  }
			 
			 

		} else if (skillId == COOKING_2_0_N || skillId == COOKING_2_0_S) { // 캐비어 카나페
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 8, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_1_N || skillId == COOKING_2_1_S) { // 아리게이타스테이키
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(),
						pc.getMaxHp()));
				if (pc.isInParty()) { // 파티중
					pc.getParty(). updateMiniHP(pc);
				}
				pc.addMaxMp(-30);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(),
						pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 9, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_2_N || skillId == COOKING_2_2_S) { // 타트르드라곤의 과자
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.sendPackets(new S_PacketBox(53, 10, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_3_N || skillId == COOKING_2_3_S) { // 키위파롯트 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 11, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_4_N || skillId == COOKING_2_4_S) { // 스코피온 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 12, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_5_N || skillId == COOKING_2_5_S) { // 이렉카좀시츄
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 13, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_6_N || skillId == COOKING_2_6_S) { // 거미다리의 꼬치구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 14, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_2_7_N || skillId == COOKING_2_7_S) { // 클럽 스프
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 15, 0));
				pc.setDessertId(0);
			}
		} else if (skillId == COOKING_3_0_N || skillId == COOKING_3_0_S) { // 클러스터 시안의 가위 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 16, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_1_N || skillId == COOKING_3_1_S) { // 그리폰 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(),
						pc.getMaxHp()));
				if (pc.isInParty()) { // 파티중
					pc.getParty(). updateMiniHP(pc);
				}
				pc.addMaxMp(-50);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(),
						pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 17, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_2_N || skillId == COOKING_3_2_S) { // 코카트리스스테이키
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 18, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_3_N || skillId == COOKING_3_3_S) { // 타트르드라곤 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(3);
				pc.sendPackets(new S_PacketBox(53, 19, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_4_N || skillId == COOKING_3_4_S) { // 렛서드라곤의 닭의 가슴에서 날개까지의 고기
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 20, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_5_N || skillId == COOKING_3_5_S) { // 드레이크 구이
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 21, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_6_N || skillId == COOKING_3_6_S) { // 심해어의 스튜
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(),
						pc.getMaxHp()));
				if (pc.isInParty()) { // 파티중
					pc.getParty(). updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 22, 0));
				pc.setCookingId(0);
			}
		} else if (skillId == COOKING_3_7_N || skillId == COOKING_3_7_S) { // 바시리스크의 알스프
			  if (cha instanceof L1PcInstance) {
				    L1PcInstance pc = (L1PcInstance) cha;
				    pc.sendPackets(new S_PacketBox(53, 23, 0));
				    pc.setDessertId(0);
				   }
				  } else if (skillId == COLOR_A) {
				   if (cha instanceof L1PcInstance) {
				    L1PcInstance pc = (L1PcInstance) cha;
				    pc.addMaxHp(-50);
				    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
				      .getMaxHp()));
				    if (pc.isInParty()) { 
				     pc.getParty().updateMiniHP(pc);
				    }
				    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
				      .getMaxMp()));
				   }   
				  } else if (skillId == COLOR_B) {
				    if (cha instanceof L1PcInstance) {
				    L1PcInstance pc = (L1PcInstance) cha;
				    pc.addMaxMp(-40);
				    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
				      .getMaxMp()));
				   }
				   } else if  (skillId == COLOR_C) {
				    if (cha instanceof L1PcInstance) {
				    L1PcInstance pc = (L1PcInstance) cha;
				                pc.addDmgup(-3);
				    pc.addHitup(-3);
				    pc.addSp(-3);
				    pc.sendPackets(new S_SPMR(pc));
				    }
				   } 

				  if (cha instanceof L1PcInstance) {
				   L1PcInstance pc = (L1PcInstance) cha;
				   sendStopMessage(pc, skillId);
				   pc.sendPackets(new S_OwnCharStatus(pc));
				  }
				 }
	// 메세지의 표시(종료할 때)
	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if (l1skills == null || charaPc == null) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		for (int timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			} catch (InterruptedException e) {
				return;
			}
		}
		_cha.removeSkillEffect(_skillId);
	}

	public int getRemainingTime() {
		return _remainingTime;
	}

	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return; // 호출원thread가 스스로 있으면 멈추지 않는다
		}
		super.stop();
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
	private static Logger _log = Logger.getLogger(L1SkillTimerTimerImpl.class
			. getName());
	private ScheduledFuture<? > _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
		}
	}

	@Override
	public void begin() {
		_future = GeneralThreadPool.getInstance(). scheduleAtFixedRate(this,
				1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
		L1SkillStop.stopSkill(_cha, _skillId);
		} catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;
	private final int _timeMillis;
	private final int _skillId;
	private int _remainingTime;
}