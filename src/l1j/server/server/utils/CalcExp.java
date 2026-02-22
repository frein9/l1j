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


package l1j.server.server.utils;

import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan; // 성혈일경우 경험치 증가 구현
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconExp;
import l1j.server.server.serverpackets.S_ChatPacket; //요것두 추가해주세요
import l1j.server.server.serverpackets.S_Disconnect; //임포트시킵니다.
import l1j.server.server.templates.L1Pet;
import l1j.server.server.model.Instance.L1ScarecrowInstance;

// import l1j.server.server.serverpackets.S_bonusstats; // 보류

// Referenced classes of package l1j.server.server.utils:
// CalcStat

public class CalcExp {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(CalcExp.class.getName());

	private static Random _random = new Random(System.nanoTime()); // 추가해주세요


	public static final int MAX_EXP = ExpTable.getExpByLevel(100) - 1;

	private CalcExp() {
	}
	private static L1NpcInstance _npc = null;

	public static void calcExp(L1PcInstance l1pcinstance, int targetid,
			ArrayList acquisitorList, ArrayList hateList, int exp) {

		int i = 0;
		double party_level = 0;
		double dist = 0;
		int member_exp = 0;
		int member_lawful = 0;
		L1Object l1object = L1World.getInstance().findObject(targetid);
		L1NpcInstance npc = (L1NpcInstance) l1object;

		// 헤이트의 합계를 취득
		L1Character acquisitor;
		int hate = 0;
		int acquire_exp = 0;
		int acquire_lawful = 0;
		int party_exp = 0;
		int party_lawful = 0;
		int totalHateExp = 0;
		int totalHateLawful = 0;
		int partyHateExp = 0;
		int partyHateLawful = 0;
		int ownHateExp = 0;

		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		for (i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			hate = (Integer) hateList.get(i);
			if (acquisitor != null && !acquisitor.isDead()) {
				totalHateExp += hate;
				if (acquisitor instanceof L1PcInstance) {
					totalHateLawful += hate;
				}
			} else { // null였거나 죽어 있으면(자) 배제
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		if (totalHateExp == 0) { // 취득자가 없는 경우
			return;
		}

		if (l1object != null && !(npc instanceof L1PetInstance)
				&& !(npc instanceof L1SummonInstance)) {
			// int exp = npc.get_exp();
			if (!L1World.getInstance().isProcessingContributionTotal()
					&& l1pcinstance.getHomeTownId() > 0) {
				int contribution = npc.getLevel() / 10;
				l1pcinstance.addContribution(contribution);
			}
			int lawful = npc.getLawful();

			if (l1pcinstance.isInParty()) { // 파티중
				// 파티의 헤이트의 합계를 산출
				// 파티 멤버 이외에는 그대로 배분
				partyHateExp = 0;
				partyHateLawful = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else if (l1pcinstance.getParty().isMember(pc)) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							if (totalHateLawful > 0) {
								acquire_lawful = (lawful * hate / totalHateLawful);
							}
							AddExp(pc, acquire_exp, acquire_lawful);
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							AddExpPet(pet, acquire_exp);
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
						}
					}
				}
				if (totalHateExp > 0) {
					party_exp = (exp * partyHateExp / totalHateExp);
				}
				if (totalHateLawful > 0) {
					party_lawful = (lawful * partyHateLawful / totalHateLawful);
				}

				// EXP, 로우훌 배분

				// 프리보나스
				double pri_bonus = 0;
				L1PcInstance leader = l1pcinstance.getParty().getLeader();
				if (leader.isCrown()
						&& (l1pcinstance.knownsObject(leader)
								|| l1pcinstance.equals(leader))) {
					pri_bonus = 0.059;
				}

				// PT경험치의 계산
				L1PcInstance[] ptMembers = l1pcinstance.getParty().getMembers();
				double pt_bonus = 0;
				for (L1PcInstance each : ptMembers) {
					if (l1pcinstance.knownsObject(each)
							|| l1pcinstance.equals(each)) {
						party_level += each.getLevel() * each.getLevel();
					}
					if (l1pcinstance.knownsObject(each)) {
						pt_bonus += 0.04;
					}
				}

				party_exp = (int) (party_exp * (1 + pt_bonus + pri_bonus));

				// 자캐릭터와 그 애완동물·사몬의 헤이트의 합계를 산출
				if (party_level > 0) {
					dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
				}
				member_exp = (int) (party_exp * dist);
				member_lawful = (int) (party_lawful * dist);

				ownHateExp = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					}
				}
				// 자캐릭터와 그 애완동물·사몬에 분배
				if (ownHateExp != 0) { // 공격에 참가하고 있었다
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = (Integer) hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExp(pc, acquire_exp, member_lawful);
							}
						} else if (acquisitor instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) acquisitor;
							L1PcInstance master = (L1PcInstance) pet
									.getMaster();
							if (master == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExpPet(pet, acquire_exp);
							}
						} else if (acquisitor instanceof L1SummonInstance) {
						}
					}
				} else { // 공격에 참가하고 있지 않았다
					// 자캐릭터에만 분배
					AddExp(l1pcinstance, member_exp, member_lawful);
				}

				// 파티 멤버와 그 애완동물·사몬의 헤이트의 합계를 산출
				for (int cnt = 0; cnt < ptMembers.length; cnt++) {
					if (l1pcinstance.knownsObject(ptMembers[cnt])) {
						if (party_level > 0) {
							dist = ((ptMembers[cnt].getLevel() * ptMembers[cnt]
									.getLevel()) / party_level);
						}
						member_exp = (int) (party_exp * dist);
						member_lawful = (int) (party_lawful * dist);

						ownHateExp = 0;
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = (L1Character) acquisitorList.get(i);
							hate = (Integer) hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) pet
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1SummonInstance) {
								L1SummonInstance summon = (L1SummonInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) summon
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							}
						}
						// 파티 멤버와 그 애완동물·사몬에 분배
						if (ownHateExp != 0) { // 공격에 참가하고 있었다
							for (i = hateList.size() - 1; i >= 0; i--) {
								acquisitor = (L1Character) acquisitorList
										.get(i);
								hate = (Integer) hateList.get(i);
								if (acquisitor instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) acquisitor;
									if (pc == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExp(pc, acquire_exp, member_lawful);
									}
								} else if (acquisitor instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) acquisitor;
									L1PcInstance master = (L1PcInstance) pet
											.getMaster();
									if (master == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExpPet(pet, acquire_exp);
									}
								} else if (acquisitor instanceof L1SummonInstance) {
								}
							}
						} else { // 공격에 참가하고 있지 않았다
							// 파티 멤버에만 분배
							AddExp(ptMembers[cnt], member_exp, member_lawful);
						}
					}
				}
			} else { // 파티를 짜지 않았다
				// EXP, 로우훌의 분배
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					acquire_exp = (exp * hate / totalHateExp);
					if (acquisitor instanceof L1PcInstance) {
						if (totalHateLawful > 0) {
							acquire_lawful = (lawful * hate / totalHateLawful);
						}
					}

					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						AddExp(pc, acquire_exp, acquire_lawful);
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						AddExpPet(pet, acquire_exp);
					} else if (acquisitor instanceof L1SummonInstance) {
					}
				}
			}
		}
	}

	private static void AddExp(L1PcInstance pc, int exp, int lawful) {
		int Ain_Exp = (int) (exp);
		int add_lawful = (int) (lawful * Config.RATE_LA) * -1;
		pc.addLawful(add_lawful);

		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		double foodBonus = 1.0;
		double expposion = 1.0;
		double buffBonus = 1.0;
        double ainBonus = 1.0;  // 아인하사드의 축복


		if (pc.hasSkillEffect(L1SkillId.COOKING_1_7_N) // 버섯스프 경험치 증가 1%
				|| pc.hasSkillEffect(L1SkillId.COOKING_1_7_S)) {
			foodBonus = 1.01;
		}
		if (pc.hasSkillEffect(L1SkillId.COOKING_1_15_N) // 크랩살스프 경험치 증가 2%
				|| pc.hasSkillEffect(L1SkillId.COOKING_1_15_S)) {
			foodBonus = 1.02;
		}
		if (pc.hasSkillEffect(L1SkillId.COOKING_1_23_N) // 바실리스크 알 경험치 증가 3%
				|| pc.hasSkillEffect(L1SkillId.COOKING_1_23_S)) {
			foodBonus = 1.03;
		}
		if (pc.hasSkillEffect(3549) == true) {
            foodBonus = 1.20;
        }  
		if (pc.hasSkillEffect(L1SkillId.EXP_POTION)) {
			expposion = 1.20;
		}
		if (pc.hasSkillEffect(7383)) {
            buffBonus = 1.20;
        } 
		if (pc.getAinPoint() > 0) {   // 아인하사드의 축복
			
   if (!(_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance
     || _npc instanceof L1ScarecrowInstance)) { // 펫/서먼/허수아비는 제외
            ainBonus = 1.7;
            pc.setStExp(pc.getStExp() + Ain_Exp);
            if (pc.getStExp() >= 5500 && pc.getAinPoint() > 0){
             pc.setAinPoint(pc.getAinPoint() - 1);
             pc.setStExp(pc.getStExp() - pc.getStExp());
             pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
             }
            }
          } 
 
////////////////////  코마 버프사 추가  //////////////////////

		int add_exp = (int) (exp * exppenalty * Config.RATE_XP * foodBonus * expposion * buffBonus * ainBonus);	


        /*스텟창*/
		/*if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) {
		if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
			+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < 150) {
			pc.sendPackets(new S_bonusstats(pc.getId(), 1));
			}
		}*/ // 보류
		/*스텟창*/
		
		/* 폭렙 방지*/
		
		/* L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
  if (pc.getLevel() == 1 || pc.getLevel() == 2  || pc.getLevel() == 3 || pc.getLevel() == 4 || pc.getLevel() == 5 || pc.getLevel() == 6
			 || pc.getLevel() == 7 || pc.getLevel() == 8 || pc.getLevel() == 9 || pc.getLevel() == 10 || pc.getLevel() == 11 || pc.getLevel() == 12
			 || pc.getLevel() == 13 || pc.getLevel() == 14 || pc.getLevel() == 15 || pc.getLevel() == 16 || pc.getLevel() == 17 || pc.getLevel() == 18
			 || pc.getLevel() == 19 || pc.getLevel() == 20 || pc.getLevel() == 21 || pc.getLevel() == 22 || pc.getLevel() == 23 || pc.getLevel() == 24
			 || pc.getLevel() == 25 || pc.getLevel() == 26 || pc.getLevel() == 27  || pc.getLevel() == 28 || pc.getLevel() == 29 || pc.getLevel() == 30
			 || pc.getLevel() == 31 || pc.getLevel() == 32 || pc.getLevel() == 33 || pc.getLevel() == 34 || pc.getLevel() == 35 || pc.getLevel() == 36
			 || pc.getLevel() == 37 || pc.getLevel() == 38 || pc.getLevel() == 39 || pc.getLevel() == 40 || pc.getLevel() == 41 || pc.getLevel() == 42
			 || pc.getLevel() == 43 || pc.getLevel() == 44 || pc.getLevel() == 45 || pc.getLevel() == 46 || pc.getLevel() == 47 || pc.getLevel() == 48 || pc.getLevel() == 49
			 || pc.getLevel() == 50 || pc.getLevel() == 51 || pc.getLevel() == 52 || pc.getLevel() == 53 || pc.getLevel() == 54 || pc.getLevel() == 55
			 || pc.getLevel() == 56 || pc.getLevel() == 57 || pc.getLevel() == 58 || pc.getLevel() == 59 || pc.getLevel() == 60 || pc.getLevel() == 61
			 || pc.getLevel() == 62 || pc.getLevel() == 63 || pc.getLevel() == 64 || pc.getLevel() == 65 || pc.getLevel() == 66 || pc.getLevel() == 67
			 || pc.getLevel() == 68 || pc.getLevel() == 69 || pc.getLevel() == 70 || pc.getLevel() == 71 || pc.getLevel() == 72 || pc.getLevel() == 73
			 || pc.getLevel() == 74 || pc.getLevel() == 75 || pc.getLevel() == 76 || pc.getLevel() == 77 || pc.getLevel() == 78 || pc.getLevel() == 79
			 || pc.getLevel() == 80 || pc.getLevel() == 81 || pc.getLevel() == 82 || pc.getLevel() == 83 || pc.getLevel() == 84 || pc.getLevel() == 85
			 || pc.getLevel() == 86 || pc.getLevel() == 87 || pc.getLevel() == 88 || pc.getLevel() == 89 || pc.getLevel() == 90 || pc.getLevel() == 91
			 || pc.getLevel() == 92 || pc.getLevel() == 93 || pc.getLevel() == 94 || pc.getLevel() == 95) {
		  if ((add_exp + pc.getExp()) > ExpTable.getExpByLevel((pc.getLevel()+1))) {
		   add_exp =  ExpTable.getExpByLevel((pc.getLevel()+1)) - pc.getExp();
		  }
		}
		if (add_exp < 0){
			return;
		}
  if (clan != null){ 
  if (clan.getCastleId() != 0) {
   add_exp *= Config.RATE_CCLAN_XP;
  pc.addExp(add_exp); 
  } else { 
   pc.addExp(add_exp); 
  }
  } else { 
   pc.addExp(add_exp); 
  }*/
		/* 폭렙 방지 */
		
	/////////////////////////////성던 경험치 증가////////////
		if(pc.getMapId() == 523 || pc.getMapId() == 524 || pc.getMapId() == 88
				 || pc.getMapId() == 783 || pc.getMapId() == 784 || pc.getMapId() == 23 || pc.getMapId() == 24
				 || pc.getMapId() == 240 || pc.getMapId() == 241 || pc.getMapId() == 242 || pc.getMapId() == 243
				 || pc.getMapId() == 248 || pc.getMapId() == 249 || pc.getMapId() == 250 || pc.getMapId() == 251
				 || pc.getMapId() == 257 || pc.getMapId() == 258 || pc.getMapId() == 259)////맵아뒤입니다.
		  {
		   pc.addExp(add_exp * 6/5);/// 숫자가 배율입니다.
		  // add_exp += add_exp * 0.5;// 
		  }else{

	if (pc.get_autogo()==1){ //오토인증을 입력받기위해 대기중
          pc.set_autook(1);  // 오토인증 성공시 0을 입력받기위한
          pc.set_autoct(pc.get_autoct()+1); 
         }
  
    if (pc.get_autook()==0){ // 오토인증 성공시
          pc.set_autoct(0); // 오토 카운트 초기화
         }
  
    if (pc.get_autoct() >= 7){ //오토 실패 7번 강제종료
         pc.sendPackets(new S_Disconnect()); // 여기 수정시 압류및 제제를 가함
         }
      String code ; // 랜덤 코드를 정하는 스트링 선언
      int autoch = 0; // 오토인증을 랜덤으로 뜨게하기위해
        autoch = _random.nextInt(250);  // 여기 수치를 높이면 높일수록 인증코드뜰 확률 적어짐
    if(autoch == 177 && pc.get_autoct()==0) { // 250/1확률로 오토인증코드 표시 && 오토카운트 0일때
        pc.set_autogo(1);
   
       code = String.format("%04d", new Object[] { //랜덤 코드를 정하는 스트링
              Integer.valueOf(_random.nextInt(10000))
          });
        pc.set_autocode(code); // 정해진 코드를 저장하는 스트링
      String chatText = "오토 방지 코드"+"["+pc.get_autocode()+"]"+"를  채팅창에 입력하십시오. 3회 미입력시 게임종료";
       S_ChatPacket s_chatpacket1 = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
       pc.sendPackets(s_chatpacket1);
   
   
          }

   if(pc.get_autoct()==3){ // 오토인증 뜨고 무시하고 몹3마리 잡을시 두번째 새로운 오토인증
      pc.set_autogo(1);
      code = String.format("%04d", new Object[] {
               Integer.valueOf(_random.nextInt(10000))
          });
       pc.set_autocode(code);
     String chatText = "오토 방지 코드"+"["+pc.get_autocode()+"]"+"를  채팅창에 입력하십시오. 2회 미입력시 게임종료";
      S_ChatPacket s_chatpacket1 = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
      pc.sendPackets(s_chatpacket1);
        }

   if(pc.get_autoct() == 6){ //두번째 오토인증 무시후 몹 3마리더 잡을시 마지막 오토인증
      pc.set_autogo(1);
      code = String.format("%04d", new Object[] {
              Integer.valueOf(_random.nextInt(10000))
         });
      pc.set_autocode(code);
     String chatText = "오토 방지 코드"+"["+code+"]"+"를 채팅창에 입력하십시오. 미입력시 게임종료";
      S_ChatPacket s_chatpacket1 = new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_NORMALCHAT, 2);
      pc.sendPackets(s_chatpacket1); 
   }
			   pc.addExp(add_exp);
		  }
 }

////////////////////////////성던 경험치 증가////////////


	private static void AddExpPet(L1PetInstance pet, int exp) {
		L1PcInstance pc = (L1PcInstance) pet.getMaster();

		int petNpcId = pet.getNpcTemplate().get_npcId();
		int petItemObjId = pet.getItemObjId();

		int levelBefore = pet.getLevel();
//		int totalExp = (int) (exp * Config.RATE_XP + pet.getExp()); // ########## A116 원본 소스 주석 처리 
		int totalExp = (int) (exp * Config.RATE_PET_XP + pet.getExp()); // ########## A137 펫 경험치 배율 설정 외부화 [넬] 
		if (totalExp >= ExpTable.getExpByLevel(51)) {
			totalExp = ExpTable.getExpByLevel(51) - 1;
		}
		pet.setExp(totalExp);

		pet.setLevel(ExpTable.getLevelByExp(totalExp));

		int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

		int gap = pet.getLevel() - levelBefore;
		for (int i = 1; i <= gap; i++) {
			IntRange hpUpRange = pet.getPetType().getHpUpRange();
			IntRange mpUpRange = pet.getPetType().getMpUpRange();
			pet.addMaxHp(hpUpRange.randomValue());
			pet.addMaxMp(mpUpRange.randomValue());
		}

		pet.setExpPercent(expPercentage);
		pc.sendPackets(new S_PetPack(pet, pc));

		if (gap != 0) { // 레벨업하면(자) DB에 기입한다
			L1Pet petTemplate = PetTable.getInstance()
					.getTemplate(petItemObjId);
			if (petTemplate == null) { // PetTable에 없다
				_log.warning("L1Pet == null");
				return;
			}
			petTemplate.set_exp(pet.getExp());
			petTemplate.set_level(pet.getLevel());
			petTemplate.set_hp(pet.getMaxHp());
			petTemplate.set_mp(pet.getMaxMp());
			PetTable.getInstance().storePet(petTemplate); // DB에 기입해
			pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0의 레벨이 올랐습니다.
		}
	}
}