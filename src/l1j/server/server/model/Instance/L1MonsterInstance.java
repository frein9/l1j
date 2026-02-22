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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object; 
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.CrockController;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.types.Point;
import l1j.server.server.templates.TimeMap;
import l1j.server.server.TimeMapController;		
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.model.skill.L1SkillUse; 
import l1j.server.server.model.Instance.L1PcInstance;  
import static l1j.server.server.model.skill.L1SkillId. *;  
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.utils.L1SpawnUtil;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.serverpackets.S_SkillSound;
public class L1MonsterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(L1MonsterInstance.class
			.getName());

	private static Random _random = new Random();

	private boolean _storeDroped; // 드롭 아이템의 독입이 완료했는지

	// 아이템 사용 처리
	@Override
	public void onItemUse() {
		if (!isActived() && _target != null) {
			useItem(USEITEM_HASTE, 40); // 40%의 확률로 헤이 파업 일부 사용
			// 몬스터 멘트 날리기

			   if(getNpcTemplate().get_npcId() == 201024) {//라바 문지기     
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                 }
			   if(getNpcTemplate().get_npcId() == 201025) {//라바 문지기 
			    String chat = "$3889";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                  }
			   if(getNpcTemplate().get_npcId() == 201026) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                 }
			   if(getNpcTemplate().get_npcId() == 201027) {//라바 문지기
			    String chat = "$3921";
			       broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                  }
			   if(getNpcTemplate().get_npcId() == 201028) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                 }
			   if(getNpcTemplate().get_npcId() == 201029) {//라바 문지기
			    String chat = "$3909";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201030) {//라바 문지기
			    String chat = "$3911";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                 }
			   if(getNpcTemplate().get_npcId() == 201031) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                 }
			   if(getNpcTemplate().get_npcId() == 201032) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201033) {//라바 문지기
			    String chat = "$3921";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201034) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
	           }
			   if(getNpcTemplate().get_npcId() == 201035) {//라바 문지기
			    String chat = "$3911";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201036) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201037) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201038) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201039) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
	            }
			   if(getNpcTemplate().get_npcId() == 201040) {//라바 문지기
			    String chat = "$3921";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
	            }
			   if(getNpcTemplate().get_npcId() == 201041) {//라바 문지기
			    String chat = "$3889";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201042) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201043) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
		  	   if(getNpcTemplate().get_npcId() == 201044) {//라바 문지기
			    String chat = "$3911";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201045) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201046) {//라바 문지기
			    String chat = "$3909";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201047) {//라바 문지기
			    String chat = "$3889";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201048) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201049) {//라바 문지기
			    String chat = "$3921";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201050) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201051) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201052) {//라바 문지기
			    String chat = "$3911";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201053) {//라바 문지기
			    String chat = "$3891";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201054) {//라바 문지기
			    String chat = "$3909";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201055) {//라바 문지기
			    String chat = "$3893";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201056) {//라바 문지기
			    String chat = "$3888";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201057) {//라바 문지기
			    String chat = "$3909";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
		  	    }
			   if(getNpcTemplate().get_npcId() == 201058) {//라바 문지기
				String chat = "$3911";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
			   if(getNpcTemplate().get_npcId() == 201059) {//라바 문지기
				String chat = "$3921";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201060) {//라바 문지기
				String chat = "$3888";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201061) {//라바 문지기
				String chat = "$3893";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201062) {//라바 문지기
				String chat = "$3909";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201063) {//라바 문지기
				String chat = "$3889";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201064) {//라바 문지기
				String chat = "$3888";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201065) {//라바 문지기
				String chat = "$3891";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201066) {//라바 문지기
				String chat = "$3921";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201067) {//라바 문지기
				String chat = "$3911";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201068) {//라바 문지기
				String chat = "$3889";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
				}
				if(getNpcTemplate().get_npcId() == 201069) {//라바 문지기
				String chat = "$3936";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201070) {//라바 문지기
				String chat = "$3937";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201071) {//라바 문지기
				String chat = "$3938";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201072) {//라바 문지기
				String chat = "$4626";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201073) {//라바 문지기
				String chat = "$4885";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 201074) {//라바 문지기
				String chat = "$4884";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
				if(getNpcTemplate().get_npcId() == 45583
				|| getNpcTemplate().get_npcId() == 45681) {//베레스.바포
			    String chat = "$825";
			    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
			    }
				if(getNpcTemplate().get_npcId() == 45672) {//리치
				String chat = "이 나의 허상 앞에서도 무기력한 존재들아...더 이상 나를 진노하게 하지마라...";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
				}
			    if(getNpcTemplate().get_npcId() == 777775		// 지룡 안타라스(리뉴얼)
			    || getNpcTemplate().get_npcId() == 45681		//4대용	    
			    || getNpcTemplate().get_npcId() == 45682			    
			    || getNpcTemplate().get_npcId() == 45683
			    || getNpcTemplate().get_npcId() == 45684) { 
			    	String chat = "감히 여기가 어디라고! 어리석은 인간들이란...";
				broadcastPacket(new S_NpcChatPacket(this, chat, 2));
			    }
			
		// 아이템이 아니지만 돕펠 처리
			if (getNpcTemplate().is_doppel() && _target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) _target;
				setName(_target.getName());
				setNameId(_target.getName());
				setTitle(_target.getTitle());
				setTempLawful(_target.getLawful());
				setTempCharGfx(targetPc.getClassId());
				setGfxId(targetPc.getClassId());
				setPassispeed(640);
				setAtkspeed(900); // 정확한 값이 몰라요
			for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
				pc.sendPackets(new S_RemoveObject(this));
				pc.removeKnownObject(this);
				pc.updateObject();
				}
			}
		}
		if (getCurrentHp() * 100 / getMaxHp() < 40) { // HP가 40%
			useItem(USEITEM_HEAL, 50); // 50%의 확률로 회복 일부 사용
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		if (0 < getCurrentHp()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK || getHiddenStatus() == HIDDEN_STATUS_ICE) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Hide));
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), ActionCodes.ACTION_Moveup));
			} else if (getHiddenStatus() == HIDDEN_STATUS_APPEAR) {    // 출현 이펙있는 몹 
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 4));
			} else if (getHiddenStatus() == HIDDEN_STATUS_STOM) {    // 기르타스 
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 4)); 														
			} else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA) {    // 안타라스
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 20)); 
			} else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA_NEW
					|| getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA_NEW2
					|| getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA_NEW3) {   // 안타라스(리뉴얼) - 1,2,3단계
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 8)); 
			} else if (getHiddenStatus() == HIDDEN_STATUS_FLY_LIND) {	// 린드비오르
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(),
						ActionCodes.ACTION_Moveup));
			} else if (getHiddenStatus() == HIDDEN_STATUS_MOVEDOWN_START) {    // 드레이크류
				perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 7));	
			}
			perceivedFrom.sendPackets(new S_NPCPack(this));
			onNpcAI(); // monster의 AI를 개시
			if (getBraveSpeed() == 1) { // 제대로 된 방법을 모른다
				perceivedFrom.sendPackets(new S_SkillBrave(getId(), 1, 600000));
			}
		} else {   
			perceivedFrom.sendPackets(new S_NPCPack(this)); 
		}
	}
	// 변신이팩트, 몹소환
	public void transformDelay(int npcid, int delay){
	    try{
	      Thread.sleep(delay);
          broadcastPacket(new S_SkillSound(getId(), 5477));
	    }catch(Exception e){ }
	    for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
	      pc.sendPackets(new S_RemoveObject(this));
	      L1SpawnUtil.spawnLocation(pc, npcid, getX(), getY());
	    }
	  }
	// 숨겨진 문을 열기 위해 추가 - ACE
	public void dragonportalspawn(int npcId, int x, int y, short mapid, int heading, int 
	timeMinToDelete) {
	  try {
	   L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
	   npc.setId(IdFactory.getInstance().nextId());
	   npc.setMap(mapid);
	   npc.setX(x);
	   npc.setY(y);
	   npc.setHomeX(npc.getX());
	   npc.setHomeY(npc.getY());
	   npc.setHeading(heading);

	   L1World.getInstance(). storeObject(npc);
	   L1World.getInstance(). addVisibleObject(npc);

	   npc.turnOnOffLight();
	   if (0 < timeMinToDelete) {
	    L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
	      timeMinToDelete * 60 * 1000);
	    timer.begin();
	   }
	  } catch (Exception e) {
	   _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
	  }
	 }
	// 타겟을 찾는다
	public static int[][] _classGfxId = { { 0, 1 }, { 48, 61 }, { 37, 138 },
			{ 734, 1186 }, { 2786, 2796 }, { 6658, 6661 },{ 6671, 6650 } };  // 용기사,환술사 추가

	@Override
	public void searchTarget() {
		// 타겟 수색
		L1PcInstance targetPlayer = null;
		L1MonsterInstance targetMonster = null; 

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm() && pc.getInventory().checkEquipped(300000)
					|| pc.isMonitor() || pc.isGhost()) {
				continue;
			}

			// 투기 장내는 변신/미변신에 한정하지 않고 모두 액티브
			int mapId = getMapId();
			if (mapId == 88 || mapId == 98 || mapId == 92 || mapId == 91
					|| mapId == 95) {
				if (! pc.isInvisble() || getNpcTemplate(). is_agrocoi()) { // 인비지체크
					targetPlayer = pc;
					break;
				}
			}
			if (getNpcId() == 45600) {	// 커츠 
				if (pc.isCrown() || pc.isDarkelf() 
					|| pc.getTempCharGfx() != pc.getClassId()) { 
					targetPlayer = pc; 
					break;
				}
			}
			// 어느 쪽인가의 조건을 채우는 경우, 우호라고 보여지고 선제 공격받지 않는다.
			// ·monster의 업이 마이너스치(바르로그측 monster)로 PC의 업 레벨이 1이상(바르로그 우호)
			// ·monster의 업이 플러스치(야히측 monster)로 PC의 업 레벨이―1 이하(야히 우호)
			if ((getNpcTemplate().getKarma() < 0 && pc.getKarmaLevel() >= 1)
					|| (getNpcTemplate().getKarma() > 0 && pc.getKarmaLevel() <= -1)) {
				continue;
			}
			// 버릴 수 있었던 사람들의 땅업 퀘스트의 변신중은, 각 진영의 monster로부터 선제 공격받지 않는다
			if (pc.getTempCharGfx() == 6034 && getNpcTemplate(). getKarma() < 0 
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate(). getKarma() > 0
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate(). get_npcId() == 46070
					|| pc.getTempCharGfx() == 6035 && getNpcTemplate(). get_npcId() == 46072) {
				continue;
			}

			if (!getNpcTemplate().is_agro() && !getNpcTemplate().is_agrososc()
					&& getNpcTemplate().is_agrogfxid1() < 0
					&& getNpcTemplate().is_agrogfxid2() < 0) { // 완전한 논아크티브몬스타
				if (pc.getLawful() < -1000) { // 플레이어가 카오틱
					targetPlayer = pc;
					break;
				}
				continue;
			}

			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // 인비지체크
				if (pc.hasSkillEffect(67)) { // 변신하고 있다
					if (getNpcTemplate().is_agrososc()) { // 변신에 대해서 액티브
						targetPlayer = pc;
						break;
					}
				} else if (getNpcTemplate().is_agro()) { // 액티브 monster
					targetPlayer = pc;
					break;
				}
								
				// 특정의 클래스 or그래픽 ID에 액티브
				if (getNpcTemplate().is_agrogfxid1() >= 0
						&& getNpcTemplate().is_agrogfxid1() <= 4) { // 클래스 지정
					if (_classGfxId[getNpcTemplate().is_agrogfxid1()][0] == pc
							.getTempCharGfx()
							|| _classGfxId[getNpcTemplate().is_agrogfxid1()][1] == pc
									.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid1()) { // 그래픽 ID지정
					targetPlayer = pc;
					break;
				}

				if (getNpcTemplate().is_agrogfxid2() >= 0
						&& getNpcTemplate().is_agrogfxid2() <= 4) { // 클래스 지정
					if (_classGfxId[getNpcTemplate().is_agrogfxid2()][0] == pc.getTempCharGfx()
						|| _classGfxId[getNpcTemplate().is_agrogfxid2()][1] == pc.getTempCharGfx()) {
						targetPlayer = pc;
						break;
					}
				} else if (pc.getTempCharGfx() == getNpcTemplate()
						.is_agrogfxid2()) { // 그래픽 ID지정
					targetPlayer = pc;
					break;
				}
			}
		}
		/** @설명글// 추가 
		   *   이후에있을지도모를 1.Monster vs Monster 
		   *                               2.Monster vs Guard
		   *                               3.Monster vs Guardian
		   *                               4.Monster vs Npc
		   *  위와같은 상황을 위해 오브젝트를 불러오도록 추가 현재는 1번만을위한 소스임
		   *  간단하게 오브젝트를 인스턴스of로 선언만해주면되게끔 설정 
		   * 
		   */
		  for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
		      if (obj instanceof L1MonsterInstance) {
		       L1MonsterInstance mon = (L1MonsterInstance) obj;
		       if(mon.getHiddenStatus() != 0 || mon.isDead()){
		       continue;
		       }
		    if(this.getNpcTemplate().get_npcId()==45570){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의)
		        targetMonster = mon;
		        break;
		       }
		      }

		    if(this.getNpcTemplate().get_npcId()==45571){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
		        targetMonster = mon;
		        break;
		       }
		      }

		   if(this.getNpcTemplate().get_npcId()==45582){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
		        targetMonster = mon;
		        break;
		       }
		      }

		   if(this.getNpcTemplate().get_npcId()==45587){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
		        targetMonster = mon;
		        break;
		       }
		      }

		   if(this.getNpcTemplate().get_npcId()==45605){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
		        targetMonster = mon;
		        break;
		       }
		      }

		   if(this.getNpcTemplate().get_npcId()==45685){ //적을 인식할 몬스터(사제)
		       if(mon.getNpcTemplate().get_npcId() == 45391 
		    		   || mon.getNpcTemplate().get_npcId() == 45450 
		    		   || mon.getNpcTemplate().get_npcId() == 45482 
		    		   || mon.getNpcTemplate().get_npcId() == 45569 
		    		   || mon.getNpcTemplate().get_npcId() == 45579 
		    		   || mon.getNpcTemplate().get_npcId() == 45315 
		    		   || mon.getNpcTemplate().get_npcId() == 45647){ //적으로 인식될몬스터 (발록의) 
		        targetMonster = mon;
		        break;
		       }
		      }

		   if(this.getNpcTemplate().get_npcId()==45391){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      } 
		   
		   if(this.getNpcTemplate().get_npcId()==45450){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }   

		   if(this.getNpcTemplate().get_npcId()==45482){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }   

		   if(this.getNpcTemplate().get_npcId()==45569){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }   

		   if(this.getNpcTemplate().get_npcId()==45579){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }   

		   if(this.getNpcTemplate().get_npcId()==45315){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }   

		   if(this.getNpcTemplate().get_npcId()==45647){ //적을 인식할 몬스터(발록)
		       if(mon.getNpcTemplate().get_npcId() == 45570 
		    		   || mon.getNpcTemplate().get_npcId() == 45571 
		    		   || mon.getNpcTemplate().get_npcId() == 45582 
		    		   || mon.getNpcTemplate().get_npcId() == 45587 
		    		   || mon.getNpcTemplate().get_npcId() == 45605){ //적으로 인식될몬스터 (사제) 
		        targetMonster = mon;
		        break;
		       }
		      }  
           //autopc(자동케릭) 추가 : 시작
			if(this.getNpcTemplate().get_npcId() == 778782){ // 적을 인식할 몬스터 (이카루스) 
				if(mon.getNpcTemplate().get_npcId() == 45130 || mon.getNpcTemplate().get_npcId() == 45131 
				   || mon.getNpcTemplate().get_npcId() == 45269 || mon.getNpcTemplate().get_npcId() == 45270 
				   || mon.getNpcTemplate().get_npcId() == 45286 || mon.getNpcTemplate().get_npcId() == 45278 
				   || mon.getNpcTemplate().get_npcId() == 45361 || mon.getNpcTemplate().get_npcId() == 45259
				   || mon.getNpcTemplate().get_npcId() == 777783){ //적으로 인식될 몬스터 (용뼈 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778783){ // 적을 인식할 몬스터 (디오니소스) 
				if(mon.getNpcTemplate().get_npcId() == 45130 || mon.getNpcTemplate().get_npcId() == 45131 
				   || mon.getNpcTemplate().get_npcId() == 45269 || mon.getNpcTemplate().get_npcId() == 45270 
				   || mon.getNpcTemplate().get_npcId() == 45286 || mon.getNpcTemplate().get_npcId() == 45278 
				   || mon.getNpcTemplate().get_npcId() == 45361 || mon.getNpcTemplate().get_npcId() == 45259){ //적으로 인식될 몬스터 (용뼈 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778784){ // 적을 인식할 몬스터 (헤스티아) 
				if(mon.getNpcTemplate().get_npcId() == 45130 || mon.getNpcTemplate().get_npcId() == 45131 
				   || mon.getNpcTemplate().get_npcId() == 45269 || mon.getNpcTemplate().get_npcId() == 45270 
				   || mon.getNpcTemplate().get_npcId() == 45286 || mon.getNpcTemplate().get_npcId() == 45278
				   || mon.getNpcTemplate().get_npcId() == 45259
				   || mon.getNpcTemplate().get_npcId() == 45361){ //적으로 인식될 몬스터 (용계삼거리 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778785){ // 적을 인식할 몬스터 (아레스) 
				if(mon.getNpcTemplate().get_npcId() == 45362 || mon.getNpcTemplate().get_npcId() == 45364 
				   || mon.getNpcTemplate().get_npcId() == 45390 || mon.getNpcTemplate().get_npcId() == 45449 
				   || mon.getNpcTemplate().get_npcId() == 45578){ //적으로 인식될 몬스터 (잊섬 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778786){ // 적을 인식할 몬스터 (아탈란테) 
				if(mon.getNpcTemplate().get_npcId() == 45402 || mon.getNpcTemplate().get_npcId() == 45403 
				   || mon.getNpcTemplate().get_npcId() == 45493 || mon.getNpcTemplate().get_npcId() == 45494 
					){ //적으로 인식될 몬스터 (오만40층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778787){ // 적을 인식할 몬스터 (이아손) 
				if(mon.getNpcTemplate().get_npcId() == 45496 || mon.getNpcTemplate().get_npcId() == 45522 
				   || mon.getNpcTemplate().get_npcId() == 45480){ //적으로 인식될 몬스터 (오만50층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778788){ // 적을 인식할 몬스터 (헬리오스) 
				if(mon.getNpcTemplate().get_npcId() == 45372 || mon.getNpcTemplate().get_npcId() == 45322 
				   || mon.getNpcTemplate().get_npcId() == 45221 || mon.getNpcTemplate().get_npcId() == 45162 
					){ //적으로 인식될 몬스터 (상아탑8층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778789){ // 적을 인식할 몬스터 (셀레네) 
				if(mon.getNpcTemplate().get_npcId() == 45724 || mon.getNpcTemplate().get_npcId() == 45725 
				   || mon.getNpcTemplate().get_npcId() == 45726 || mon.getNpcTemplate().get_npcId() == 45727 
				   || mon.getNpcTemplate().get_npcId() == 45728 || mon.getNpcTemplate().get_npcId() == 45732 
				   || mon.getNpcTemplate().get_npcId() == 45733){ //적으로 인식될 몬스터 (심해 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778790){ // 적을 인식할 몬스터 (아스클레피오스) 
				if(mon.getNpcTemplate().get_npcId() == 45946 || mon.getNpcTemplate().get_npcId() == 45947 
				   || mon.getNpcTemplate().get_npcId() == 45948 || mon.getNpcTemplate().get_npcId() == 45949 
				   || mon.getNpcTemplate().get_npcId() == 45950 || mon.getNpcTemplate().get_npcId() == 45951 
				   || mon.getNpcTemplate().get_npcId() == 46222 || mon.getNpcTemplate().get_npcId() == 46223){ //적으로 인식될 몬스터 (개던3층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778791){ // 적을 인식할 몬스터 (카이론) 
				if(mon.getNpcTemplate().get_npcId() == 45373 || mon.getNpcTemplate().get_npcId() == 45393 
				   || mon.getNpcTemplate().get_npcId() == 45451 || mon.getNpcTemplate().get_npcId() == 45289 
				   ){ //적으로 인식될 몬스터 (용던5층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778792){ // 적을 인식할 몬스터 (니케) 
				if(mon.getNpcTemplate().get_npcId() == 45644 || mon.getNpcTemplate().get_npcId() == 45549 
				   || mon.getNpcTemplate().get_npcId() == 45554){ //적으로 인식될 몬스터 (몽섬 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778793){ // 적을 인식할 몬스터 (마르스) 
				if(mon.getNpcTemplate().get_npcId() == 45223 || mon.getNpcTemplate().get_npcId() == 45298 
				   || mon.getNpcTemplate().get_npcId() == 45241 || mon.getNpcTemplate().get_npcId() == 45184 
				   ){ //적으로 인식될 몬스터 (본던5층 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778794){ // 적을 인식할 몬스터 (레인져 활) 
				if(mon.getNpcTemplate().get_npcId() == 45036 || mon.getNpcTemplate().get_npcId() == 45037 
				   || mon.getNpcTemplate().get_npcId() == 45038 || mon.getNpcTemplate().get_npcId() == 45056 
				   || mon.getNpcTemplate().get_npcId() == 45313){ //적으로 인식될 몬스터 (숨계 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778795){ // 적을 인식할 몬스터 (레인져 검) 
				if(mon.getNpcTemplate().get_npcId() == 45036 || mon.getNpcTemplate().get_npcId() == 45037 
				   || mon.getNpcTemplate().get_npcId() == 45038 || mon.getNpcTemplate().get_npcId() == 45056 
				   || mon.getNpcTemplate().get_npcId() == 45313){ //적으로 인식될 몬스터 (숨계 몹들)
				targetMonster = mon;
				break;
		       }
			}
			if(this.getNpcTemplate().get_npcId() == 778796){ // 적을 인식할 몬스터 (레토) 
				if(mon.getNpcTemplate().get_npcId() == 45669 || mon.getNpcTemplate().get_npcId() == 45838 
				   || mon.getNpcTemplate().get_npcId() == 45839 || mon.getNpcTemplate().get_npcId() == 45842  
				   || mon.getNpcTemplate().get_npcId() == 45533){ //적으로 인식될 몬스터 (라던 몹들)
				targetMonster = mon;
				break;
		       }
			}
			// autopc(자동케릭) 추가 : 끝
		  }
		  }
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
		if(targetMonster != null){ //추가
			   _hateList.add(targetMonster, 0);
			   _target = targetMonster;
			   }
	}

	// 링크의 설정
	@Override

	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // 타겟이 없는 경우만 추가
			_hateList.add(cha, 0);
			checkTarget();
		}
	}
	
	public L1MonsterInstance(L1Npc template) {
		super(template);
		_storeDroped = false;
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		if (!_storeDroped) // 쓸데없는 오브젝트 ID를 발행하지 않게 여기서 세트
		{
			DropTable.getInstance().setDrop(this, getInventory());
			getInventory().shuffle();
			_storeDroped = true;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onTalkAction(L1PcInstance pc) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance(). getTemplate(
				getNpcTemplate(). get_npcId());
		String htmlid = null;
		String[] htmldata = null;

			// html 표시 패킷 송신
			if (htmlid != null) { // htmlid가 지정되고 있는 경우
				if (htmldata != null) { // html 지정이 있는 경우는 표시
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			    } else {
				if (pc.getLawful() < -1000) { // 플레이어가 카오틱
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					pc.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		}

	@Override
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.calcDrainOfHp();   // 파멸의 대검 추가
				attack.addPcPoisonAttack(pc, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void ReceiveManaDamage(L1Character attacker, int mpDamage) { // 공격으로 MP를 줄일 때는 여기를 사용
		if (mpDamage > 0 && !isDead()) {
			// int Hate = mpDamage / 10 + 10; // 주의!계산 적당 데미지의 10분의 1＋힛트헤이트 10
			// setHate(attacker, Hate);
			setHate(attacker, mpDamage);

			onNpcAI();

			if (attacker instanceof L1PcInstance) { // 동료의식을 가지는 monster의 타겟으로 설정
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 공격으로 HP를 줄일 때는 여기를 사용
		if (getCurrentHp() > 0 && !isDead()) {
			if (getHiddenStatus() == HIDDEN_STATUS_SINK
					|| getHiddenStatus() == HIDDEN_STATUS_FLY) {
				return;
			}
			if (damage >= 0) {
				if (!(attacker instanceof L1EffectInstance)) { // FW는 헤이트 없음
					setHate(attacker, damage);
				}
			}
			if (damage > 0) {
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
			}
	/*		if (getMaxHp() >= 2000 && getExp() >= 1000){ // 피통 큰 몬스터에 한해 HP바 표시
				broadcastPacket(new S_HPMeter(getId(),100 * getCurrentHp() / getMaxHp()));
			}
			onNpcAI(); */

			if (attacker instanceof L1PcInstance) { // 동료의식을 가지는 monster의 타겟으로 설정
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			if (attacker instanceof L1PcInstance && damage > 0) {
				L1PcInstance player = (L1PcInstance) attacker;
				player.setPetTarget(this);
           // 몬스터 리콜
			if     (getNpcTemplate().get_npcId() == 45681     // 린드비올  리콜
		//		 || getNpcTemplate().get_npcId() == 45682     // 안타 라스
		//		 || getNpcTemplate().get_npcId() == 45683     // 파프리온
		//		 || getNpcTemplate().get_npcId() == 81163     // 기르타스
		//		 || getNpcTemplate().get_npcId() == 45684     // 발라카스
		//		 || getNpcTemplate().get_npcId() == 2000053   // 제브레퀴 암
		//		 || getNpcTemplate().get_npcId() == 2000054   // 제브레퀴 수
		//		 || getNpcTemplate().get_npcId() == 777775    // 안타라스1 리뉴얼
        //       || getNpcTemplate().get_npcId() == 777776    // 안타라스2 리뉴얼
                 || getNpcTemplate().get_npcId() == 777779    // 안타라스3 리뉴얼
                 ) { 
					recall(player);  
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp <= 0 && !isDead()) {
				
				Random random = new Random();    // 추가 모든 몹 이벤트 템 드랍

				int chance1 = random.nextInt(100) + 1;
				int chance2 = random.nextInt(100) + 1;

				if(Config.RATE_EITEM > chance1){ 
				   getInventory().storeItem(41158, 1); // (00000, 1) 이런식으로해주시면 00000아이템을 한개드랍
				}
				if(Config.RATE_EITEM > chance2){ 

				  getInventory().storeItem(41159, 2); // 위와 마찬가지
				} 


				    /* 제브레퀴 공략 후 전체 유저 버프 */
				 int npcid = getNpcTemplate().get_npcId();
				    if (npcid == 2000053 || npcid ==2000054){  //엔피씨 번호 
				     if (attacker instanceof L1PcInstance) {
				      for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				      pc.setBuffnoch(1);
				      L1SkillUse l1skilluse = new L1SkillUse();
				      l1skilluse.handleCommands(pc, SANGABUFF, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				      pc.setBuffnoch(0);
				      }
				     }
				    }			    
				    // 안타라스 혈흔
				    if (npcid == 777779){  //엔피씨 번호 
				     if (attacker instanceof L1PcInstance) {
				      for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				      pc.setBuffnoch(1);
				      L1SkillUse l1skilluse = new L1SkillUse();
				      l1skilluse.handleCommands(pc, ANTA_BLOOD, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
				      pc.setBuffnoch(0);
				      dragonportalspawn(777800,33724,32504,(short)4,6,720);
				      }
				     }
				    }
				 // pk멘트 뜨기
				/*     if (npcid == 777778 || npcid == 777779 || npcid == 2000054 || npcid == 81163 || npcid == 200070  //##보스몹 아이디 
				    		 || npcid == 45513 || npcid == 45546 || npcid == 45547 || npcid == 45609
				    		 || npcid == 45583 || npcid == 45584 || npcid == 45600 || npcid == 45573
				    		 || npcid == 45601 || npcid == 45606 || npcid == 45610 || npcid == 45614
				    		 || npcid == 45617 || npcid == 45618 || npcid == 45625 || npcid == 45649
				    		 || npcid == 45650 || npcid == 45651 || npcid == 45652 || npcid == 45653
				    		 || npcid == 45654 || npcid == 45671 || npcid == 45672 || npcid == 81047
				    		 || npcid == 45674 || npcid == 81082 || npcid == 45680 || npcid == 45681
				    		 || npcid == 45682 || npcid == 45683 || npcid == 45684 || npcid == 45685
				    		 || npcid == 45734 || npcid == 45735 || npcid == 45753 || npcid == 45772
				    		 || npcid == 45795 || npcid == 45801 || npcid == 45802 || npcid == 45829
				    		 || npcid == 45548 || npcid == 45585 || npcid == 45574 || npcid == 45648
				    		 || npcid == 45577 || npcid == 45844 || npcid == 45588 || npcid == 45607
				    		 || npcid == 45612 || npcid == 45602 || npcid == 45863 || npcid == 45608
				    		 || npcid == 45615 || npcid == 45676 || npcid == 46024 || npcid == 46025
				    		 || npcid == 46026 || npcid == 46037 || npcid == 45963 || npcid == 45944
				    		 || npcid == 45955 || npcid == 45956 || npcid == 45957 || npcid == 45958
				    		 || npcid == 45959 || npcid == 45960 || npcid == 45961 || npcid == 45962
				             || npcid == 2000053 || npcid == 400016 || npcid == 400017 || npcid == 45609){
				     if (attacker instanceof L1PcInstance) { // pk멘트 뜨기
				        L1World.getInstance().broadcastPacketToAll(
				       new S_SystemMessage("\\fT"+attacker.getName() + "\\fT님이 " + getName() + "\\fT 정복에 성공하였습니다.")); //##멘트를 띄워보자 - 드류 2008_05
				    }
				   } */
	    	int transformId = getNpcTemplate().getTransformId();
				// 변신하지 않는 monster
				if (transformId == -1) {
					setCurrentHpDirect(0);
					setDead(true);
					setStatus(ActionCodes.ACTION_Die);
					openDoorWhenNpcDied(this);
					Death death = new Death(attacker);
					GeneralThreadPool.getInstance().execute(death);
				 // Death(attacker);
				} else { // 변신하는 monster
                 // distributeExpDropKarma(attacker);
					transform(transformId);
				}			
			/*	switch(getNpcTemplate().get_npcId()){
			    case 3013: // 기란산적
			        transformDelay(3017, 1000); // 파우스트악령 변신 딜레이 1초;
			        break;
			    case 3014: // 기란산적
				    transformDelay(3018, 1000); // 파우스트악령 변신 딜레이 1초;
				    break;
			    case 3015: // 기란산적
				    transformDelay(3019, 1000); // 파우스트악령 변신 딜레이 1초;
				    break;
			    case 3016: // 기란산적
				    transformDelay(3020, 1000); // 파우스트악령 변신 딜레이 1초;
				    break;
			    case 3026: // 기란산적두목
				    transformDelay(3027, 1000); // 파우스트악령 보스 변신 딜레이 1초;
				    break;
			       }*/
			     }
			if (newHp > 0) {
				setCurrentHp(newHp);
				hide();
			}
		} else if (!isDead()) { // 만약을 위해
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			Death death = new Death(attacker);
			GeneralThreadPool.getInstance().execute(death);
		 // Death(attacker);
		}
	}
	private static void openDoorWhenNpcDied(L1NpcInstance npc) {
		int[] npcId = {/* 46143, 46144, 46145, 46146, 46147, 46148, //얼음성 안타동굴 열쇠로 열게끔
				46149, 46150, 46151, 46152, 74001, 74000, 74002, */ 
				201024, 201025, 201026, 201027, 201028, 201029, 201030,
				201031, 201032, 201033, 201034, 201035, 201036, 201037, 
				201038, 201039, 201040, 201041, 201042, 201043, 201044,
				201045, 201046, 201047, 201048, 201049, 201050, 201051,
				201052, 201053, 201054, 201055, 201056, 201057, 201058, 
				201059, 201060, 201061, 201062, 201063, 201064, 201065,
				201066, 201067, 201068, 201069, 201070, 201071, 201072,
				201073, 201074, 46153, 46154, 46155, 46156, 46157, 
				46158, 46159, 46160};
		
		int[] doorId = {/* 5001, 5002, 5003, 5004, 5005, 5006,      //얼음성 안타동굴 열쇠로 열게끔
				5007, 5008, 5009, 5010, 5100, 5101, 5102, */
				4005, 4006, 4007, 4008, 4009, 4010, 4011,
				4012, 4013, 4014, 4015, 4016, 4017, 4018,
				4019, 4020, 4021, 4022, 4023, 4024, 4025,
			    4026, 4027, 4028, 4029, 4030, 4031, 4032,
				4033, 4034, 4035, 4036, 4037, 4038, 4039,
		        4040, 4041, 4042, 4043, 4044, 4045, 4046,
				4047, 4048, 4049, 4050, 4051, 4052, 4053,
				4054, 4055, 5050, 5051, 5052, 5053, 5054, 
				5055, 5056, 5057};
		
			
		
		for (int i = 0; i < npcId.length; i++) {
			if (npc.getNpcTemplate().get_npcId() == npcId[i]) {
				openDoorInCrystalCave(doorId[i]);
		    }
		}
	}
	
	private static void openDoorInCrystalCave(int doorId) {
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) object;
				if (door.getDoorId() == doorId) {
					door.open();
				}
			}
		}
	}

	/**
	 * 거리가 5이상 떨어져 있는 pc를 거리 3~4의 위치에 끌어 들인다.
	 * 
	 * @param pc
	 */
	private void recall(L1PcInstance pc) {
		if (getMapId() != pc.getMapId()) {
			return;
		}
		if (getLocation().getTileLineDistance(pc.getLocation()) > 4) {
			for (int count = 0; count < 10; count++) {
				L1Location newLoc = getLocation().randomLocation(3, 4, false);
				if (glanceCheck(newLoc.getX(), newLoc.getY())) {
					L1Teleport.teleport(pc, newLoc.getX(), newLoc.getY(),
							getMapId(), 5, true);
					break;
				}
			}
		}
	}
	
	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	class Death implements Runnable {
		L1Character _lastAttacker;

		public Death(L1Character lastAttacker) {
			_lastAttacker = lastAttacker;
		}

		@Override
		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
			L1PcInstance player = null;
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));
			                   //개미알
			                  if (_lastAttacker instanceof L1PcInstance) {
			                      player = (L1PcInstance) _lastAttacker;
			                  if (getNpcTemplate().get_npcId() == 45953 || getNpcTemplate().get_npcId() == 45954) { //개미알npcid
			                  int rnd = _random.nextInt(100) + 1;
			                  if(rnd > 1 && rnd <= 15){
			                       L1ItemInstance item = player.getInventory().storeItem(52, 1);//양손검
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                   }else if(rnd > 15 && rnd <= 25){
			                       L1ItemInstance item = player.getInventory().storeItem(148, 1); //대형도끼
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 25 && rnd <= 35){
			                       L1ItemInstance item = player.getInventory().storeItem(20149, 1); //청동판금갑옷
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 35 && rnd <= 45){
			                       L1ItemInstance item = player.getInventory().storeItem(20231, 1); //사각방패
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 45 && rnd <= 55){
			                       L1ItemInstance item = player.getInventory().storeItem(140087, 1); //축데이
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 55 && rnd <= 65){
			                       L1ItemInstance item = player.getInventory().storeItem(140074, 1); //축젤
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 65 && rnd <= 75){
			                       L1ItemInstance item = player.getInventory().storeItem(40087, 1); //데이
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 75 && rnd <= 85){
			                       L1ItemInstance item = player.getInventory().storeItem(40074, 1); //젤
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 85 && rnd <= 95){
			                       L1ItemInstance item = player.getInventory().storeItem(40053, 1); //최고급루비
			                       String itemName = item.getItem().getName();
			                       String npcName = getNpcTemplate().get_name();
			                       player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                       }
			                      }
			                    }
				           //해츨링알
			               if (_lastAttacker instanceof L1PcInstance) {
			                   player = (L1PcInstance) _lastAttacker;
			               if (getNpcTemplate().get_npcId() == 81265 || getNpcTemplate().get_npcId() == 81266) { // 해츨링 알npcid
			               int rnd = _random.nextInt(100) + 1;
			               if(rnd > 1 && rnd <= 15){
			                      L1ItemInstance item = player.getInventory().storeItem(52, 1);//양손검
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 15 && rnd <= 25){
			                      L1ItemInstance item = player.getInventory().storeItem(148, 1); //대형도끼
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 25 && rnd <= 35){
			                      L1ItemInstance item = player.getInventory().storeItem(20149, 1); //청동판금갑옷
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 35 && rnd <= 45){
			                      L1ItemInstance item = player.getInventory().storeItem(20231, 1); //사각방패
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 45 && rnd <= 55){
			                      L1ItemInstance item = player.getInventory().storeItem(140087, 1); //축데이
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 55 && rnd <= 65){
			                      L1ItemInstance item = player.getInventory().storeItem(140074, 1); //축젤
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 65 && rnd <= 75){
			                      L1ItemInstance item = player.getInventory().storeItem(40087, 1); //데이
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 75 && rnd <= 85){
			                      L1ItemInstance item = player.getInventory().storeItem(40074, 1); //젤
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                  }else if(rnd > 85 && rnd <= 95){
			                      L1ItemInstance item = player.getInventory().storeItem(40053, 1); //최고급루비
			                      String itemName = item.getItem().getName();
			                      String npcName = getNpcTemplate().get_name();
			                      player.sendPackets(new S_ServerMessage(143, npcName, itemName));
			                      }
			                     }
			                    }
			//보스공략조건에 티칼 보스 추가
			    if(getNpcTemplate().get_npcId() == 400016
					 || getNpcTemplate().get_npcId() == 400017
					 || getNpcTemplate().get_npcId() == 2000053
					 || getNpcTemplate().get_npcId() == 2000054){
				int dieCount = CrockController.getInstance().dieCount();
				switch(dieCount){
					// 2명의 보스중 한명도 죽이지 않았을때 둘중 하나를 죽였다면 +1
					case 0:
						CrockController.getInstance().dieCount(1);
						break;
					// 2명의 보스중 이미 한명이 죽였고. 이제 또한명이 죽으니 2
					case 1:
						CrockController.getInstance().dieCount(2);
						CrockController.getInstance().send();
						break;
				}
			}
			startChat(CHAT_TIMING_DEAD);

			distributeExpDropKarma(_lastAttacker);
			giveUbSeal();

			setDeathProcessing(false);

			setExp(0);
			setKarma(0);
			allTargetClear();

			startDeleteTimer();
			
			if (getNpcTemplate().get_npcId() == 777775) { // 안타라스(리뉴얼) - 1단계
				L1PcInstance pc = (L1PcInstance) _lastAttacker;
		//		if (pc.getMapId() == 1005) {
					if (pc.getLocation().getTileLineDistance(pc.getLocation()) <= 20) {
						pc.sendPackets(new S_SystemMessage
							("안타라스 : 어리석은 자여! 나의 분노를 자극하는 구나."));
					try {
						Thread.sleep(30000);
						} catch (Exception e) {
					}
						pc.sendPackets(new S_SystemMessage
							("안타라스 : 이제 맛있는 식사를 해볼까? 너희 피냄새가 나를 미치게 하는구나."));
					try {
						Thread.sleep(10000);
						} catch (Exception e) {
					}
					L1SpawnUtil.spawn(pc, 777776, 0, 0);	
					}
		//		}
			}
			if (getNpcTemplate().get_npcId() == 777776) { // 안타라스(리뉴얼) - 2단계
				L1PcInstance pc = (L1PcInstance) _lastAttacker;
			//	if (pc.getMapId() == 1005) {
				if (pc.getLocation().getTileLineDistance(pc.getLocation()) <= 20) {
					pc.sendPackets(new S_SystemMessage
						("안타라스 : 감히 나를 상대하려 하다니..그러고도 너희가 살길 바라느냐?"));
					try {
						Thread.sleep(30000);
						} catch (Exception e) {
					}
					pc.sendPackets(new S_SystemMessage
						("안타라스 : 나의 분노가 하늘에 닿았다. 이제 곧 나의 아버지가 나설 것이다."));
					try {
						Thread.sleep(10000);
						} catch (Exception e) {
					} 
					L1SpawnUtil.spawn(pc, 777779, 0, 0);
				}
			//	}
			}
			if (getNpcTemplate().get_npcId() == 777779) { // 안타라스(리뉴얼) - 3단계 = 공략후 버프, 귀환텔
				for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				//	if (pc.getMapId() == 1005) {
						if (pc.getLocation().getTileLineDistance(pc.getLocation()) <= 20) {
						pc.sendPackets(new S_SystemMessage
							("안타라스 : 황혼의 저주가 그대들에게 있을 지어다! 실렌이여. 나의 어머니여. 나의 숨을.. 거두소서..."));
						pc.sendPackets(new S_SystemMessage
							("크레이 : 오오.. 최강의 용사임을 증명한 최고의 기사여! 엄청난 시련을 이겨내고 당신의 손에 안타라스의 피를 묻혔는가! 드디어 이 원한을 풀겠구나. 으하하하하!! 고맙다. 땅 위에 가장 강한 용사들이여!"));
						try {
							Thread.sleep(30000);
							} catch (Exception e) {
						}
						pc.sendPackets(new S_SystemMessage
							("난쟁이의 외침 : 웰던 마을에 숨겨진 용들의 땅으로 가는 문이 열렸습니다."));
						pc.sendPackets(new S_SystemMessage
							("시스템 메시지 : 10초 후에 마을로 텔레포트 됩니다."));
						}
						try {
							Thread.sleep(10000);
							} catch (Exception e) {
						}
						if (pc.getMapId() == 1005) {
							L1Teleport.teleport(pc, 33700, 32505, (short) 4, 5, true);	// 마을
						}
					}
			//	}
			}
		}
	}

	private void distributeExpDropKarma(L1Character lastAttacker) {
		L1PcInstance pc = null;
		if (lastAttacker instanceof L1PcInstance) {
			pc = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			pc = (L1PcInstance) ((L1PetInstance) lastAttacker).getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			pc = (L1PcInstance) ((L1SummonInstance) lastAttacker).getMaster();
		}

		if (pc != null) {
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			int exp = getExp();
			CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
			// 사망했을 경우는 드롭과 업도 분배, 사망하지 않고 변신했을 경우는 EXP만
			if (isDead()) {
				distributeDrop();
				giveKarma(pc);
			}
		} else if (lastAttacker instanceof L1EffectInstance) { // FW가 넘어뜨렸을 경우
			ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
			ArrayList<Integer> hateList = _hateList.toHateArrayList();
			// 헤이 새 파업에 캐릭터가 존재한다
			if (hateList.size() != 0) {
				// 최대 헤이트를 가지는 캐릭터가 넘어뜨린 것으로 한다
				int maxHate = 0;
				for (int i = hateList.size() - 1; i >= 0; i--) {
					if (maxHate < ((Integer) hateList.get(i))) {
						maxHate = (hateList.get(i));
						lastAttacker = targetList.get(i);
					}
				}
				if (lastAttacker instanceof L1PcInstance) {
					pc = (L1PcInstance) lastAttacker;
				} else if (lastAttacker instanceof L1PetInstance) {
					pc = (L1PcInstance) ((L1PetInstance) lastAttacker)
							.getMaster();
				} else if (lastAttacker instanceof L1SummonInstance) {
					pc = (L1PcInstance) ((L1SummonInstance)
							lastAttacker).getMaster();
				}
				int exp = getExp();
				CalcExp.calcExp(pc, getId(), targetList, hateList, exp);
				// 사망했을 경우는 드롭과 업도 분배, 사망하지 않고 변신했을 경우는 EXP만
				if (isDead()) {
					distributeDrop();
					giveKarma(pc);
				}
			}
		}
	}

	private void distributeDrop() {
		ArrayList<L1Character> dropTargetList = _dropHateList
				.toTargetArrayList();
		ArrayList<Integer> dropHateList = _dropHateList.toHateArrayList();
		try {
			int npcId = getNpcTemplate().get_npcId();
			if (npcId != 45640
					|| (npcId == 45640 && getTempCharGfx() == 2332)) { 
				DropTable.getInstance().dropShare(L1MonsterInstance.this,
						dropTargetList, dropHateList);
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	private void giveKarma(L1PcInstance pc) {
		int karma = getKarma();
		if (karma != 0) {
			int karmaSign = Integer.signum(karma);
			int pcKarmaLevel = pc.getKarmaLevel();
			int pcKarmaLevelSign = Integer.signum(pcKarmaLevel);
			// 업 배신 행위는 5배
			if (pcKarmaLevelSign != 0 && karmaSign != pcKarmaLevelSign) {
				karma *= 5;
			}
			// 업은 급소를 찌른 플레이어로 설정.애완동물 or사몬으로 넘어뜨렸을 경우도 들어간다.
			pc.addKarma((int) (karma * Config.RATE_KARMA));
		}
	}

	private void giveUbSeal() {
		if (getUbSealCount() != 0) { // UB의 용사의 증거
			L1UltimateBattle ub = UBTable.getInstance().getUb(getUbId());
		if (ub != null) {
		for (L1PcInstance pc : ub.getMembersArray()) {
		if (pc != null && !pc.isDead() && !pc.isGhost()) {
			L1ItemInstance item = pc.getInventory()
			.storeItem(41402, getUbSealCount());
			pc.sendPackets(new S_ServerMessage(403, item
			.getLogName())); // %0를 손에 넣었습니다.
			}
			}
		}
		}
	}

	public boolean is_storeDroped() {
		return _storeDroped;
	}

	public void set_storeDroped(boolean flag) {
		_storeDroped = flag;
	}

	private int _ubSealCount = 0; // UB로 쓰러졌을 때, 참가자에게 줄 수 있는 용사의 증거의 개수

	public int getUbSealCount() {
		return _ubSealCount;
	}

	public void setUbSealCount(int i) {
		_ubSealCount = i;
	}

	private int _ubId = 0; // UBID

	public int getUbId() {
		return _ubId;
	}

	public void setUbId(int i) {
		_ubId = i;
	}

	private void hide() {
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 // 카즈드스파르트이
				|| npcid == 45161 // 스파르트이
				|| npcid == 45181 // 스파르트이
				|| npcid == 45455) { // 데드 리스 펄 토이
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (2 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45682) { // 안타라스
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (2 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK_ANTA);			
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_AntharasHide));
					setStatus(20);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		// 기르타스 쉘맨 방어막 
		} else if (npcid == 81163) { // 기르타스
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (3 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_STOM);
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_AntharasHide));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));				
				}
			}	
		} else if (npcid == 777778) { // 쉘맨 
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (2 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK_ITEM);
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_AntharasHide));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));
				}
			} 
		} else if (npcid == 400000 || npcid == 400001 ) {	// 테베만드라고라		 	
			if (getMaxHp() / 4 > getCurrentHp()) {			
				int rnd = _random.nextInt(10);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_SINK);
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Hide));
					setStatus(13);
					broadcastPacket(new S_NPCPack(this));
				}			
			}	
		} else if (npcid == 45067 // 바레이하피
				|| npcid == 45264 // 하피
				|| npcid == 45452 // 하피
				|| npcid == 45090 // 바레이그리폰
				|| npcid == 45321 // 그리폰
				|| npcid == 45445) { // 그리폰
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(10);
				if (2 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY);
					broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Moveup));
					setStatus(4);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		} else if (npcid == 45681) { // 린드비올
			if (getMaxHp() / 3 > getCurrentHp()) {
				int rnd = _random.nextInt(50);
				if (1 > rnd) {
					allTargetClear();
					setHiddenStatus(HIDDEN_STATUS_FLY_LIND);		
					broadcastPacket(new S_DoActionGFX(getId(), 
					ActionCodes.ACTION_Moveup));
					setStatus(11);
					broadcastPacket(new S_NPCPack(this));
				}
			}
		}
	}

	public void initHide() {
		// 출현 직후가 숨는 동작
		// 기어드는 MOB는 일정한 확률로 지중에 기어든 상태에,
		// 나는 MOB는 난 상태로 해 둔다
		int npcid = getNpcTemplate().get_npcId();
		if (npcid == 45061 // 카즈드스파르트이
				|| npcid == 45161 // 스파르트이
				|| npcid == 45181 // 스파르트이
				|| npcid == 400000// 테베만드라고라									
				|| npcid == 400001// 테베만드라고라									
				|| npcid == 45455) { // 난폭한 스파토이
			int rnd = _random.nextInt(3);
			if (2 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			}
		} else if (npcid == 45045 // 쿠레이고렘
				|| npcid == 45126 // 스토고렘
				|| npcid == 45134 // 스토고렘
				|| npcid == 45281) { // 기란스토고렘
			int rnd = _random.nextInt(3);
			if (2 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_DOLGOLLEM);	
				setStatus(4);
			}
		// 안타라스 출현이펙			
		} else if (npcid == 45682) { // 안타라스
			int rnd = _random.nextInt(3);
			if (2 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK_ANTA);
				setStatus(20);
			}
		} else if (npcid == 777775) { // 안타라스(리뉴얼)
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK_ANTA_NEW);
				setStatus(8);	
				broadcastPacket(new S_NPCPack(this));

		// 출현 효과 들어감 
		} else if (npcid == 45309      // 블랙티거
			|| npcid == 45357 || npcid == 45483 || npcid == 45836  // 블랙티거
			|| npcid == 45651 || npcid == 45844  // 바란카(빅)
			|| npcid == 45570 || npcid == 45582  // 타락의 사제
			|| npcid == 45587 || npcid == 45605  // 타락의 사제
			) { 
		int rnd = _random.nextInt(5);
		if (3 > rnd) {
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_APPEAR);
			setStatus(4);
		}
		} else if (npcid == 45067 // 바레이하피
				|| npcid == 45264 // 하피
				|| npcid == 45452 // 하피
				|| npcid == 45090 // 바레이그리폰
				|| npcid == 45321 // 그리폰
				|| npcid == 45445) { // 그리폰
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
			setStatus(4);
		} else if (npcid == 45529 || npcid == 45578) { // 드레이크류
			int rnd = _random.nextInt(5);
			if (3 > rnd) {
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_MOVEDOWN_START);  
				setStatus(7);
			}	
		} else if (npcid == 45681) { // 린드비올
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY_LIND);  
			setStatus(11);
		} else if (npcid >= 200159 && npcid <= 200162) { // 얼음성 던전 몹
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4);
		}
	}
	
	public void initHideForMinion(L1NpcInstance leader) {
			int npcid = getNpcTemplate().get_npcId();
		if (leader.getHiddenStatus() == L1NpcInstance.HIDDEN_STATUS_SINK) {
			if (npcid == 45061 // 카즈드스파르트이
					|| npcid == 45161 // 스파르트이
					|| npcid == 45181 // 스파르트이
					|| npcid == 45455) { // 데드 리스 펄 토이
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
				setStatus(13);
			} else if (npcid == 45045 // 쿠레이고렘
					|| npcid == 45126 // 스토고렘
					|| npcid == 45134 // 스토고렘
					|| npcid == 45281) { // 기란스토고렘
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_DOLGOLLEM);			 
				setStatus(4);
			}
		} else if (leader.getHiddenStatus() == L1NpcInstance
				.HIDDEN_STATUS_FLY) {
			if (npcid == 45067 // 바레이하피
					|| npcid == 45264 // 하피
					|| npcid == 45452 // 하피
					|| npcid == 45090 // 바레이그리폰
					|| npcid == 45321 // 그리폰
					|| npcid == 45445) { // 그리폰
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
				setStatus(4);
			} else if (npcid == 45529 || npcid == 45578) { // 드레이크류
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_MOVEDOWN_START);  
				setStatus(7);	
			} else if (npcid == 45681) { // 린드비올
				setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY_LIND);   
				setStatus(11);
			}			
		} else if (npcid >= 200159 && npcid <= 200162) {		// 얼음성 던전 몹
			setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_ICE);
			setStatus(4); 
		}
	}


	@Override
	protected void transform(int transformId) {
		super.transform(transformId);
		getInventory().clearItems();
		DropTable.getInstance().setDrop(this, getInventory());
		getInventory().shuffle();
	}
}
