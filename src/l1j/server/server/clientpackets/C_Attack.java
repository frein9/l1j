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

package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.model.L1Gambling2;
import l1j.server.server.model.L1Gambling3;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import static l1j.server.server.model.skill.L1SkillId.*;
import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attack extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_Attack.class.getName());
	
	private int _targetX = 0;
	
	private int _targetY = 0;
	
	public C_Attack(byte[] decrypt, ClientThread client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		_targetX = x;
		_targetY = y;

		L1PcInstance pc = client.getActiveChar();

		if (pc.isGhost() || pc.isDead() || pc.isTeleport()) {
			return;
		}
		////중복 접속 버그방지 by 마트무사 for only 포더서버만!
        if(pc.getOnlineStatus() == 0){
           client.kick();
           return;
        }
        ////중복 접속 버그방지 by 마트무사 for only 포더서버만!

		L1Object target = L1World.getInstance().findObject(targetId);

		// 공격 액션을 취할 수 있는 상태나 확인
		//if (pc.getInventory().getWeight30() > 197) { // 중량 오버
		if (pc.getInventory().getWeight240() >= 197) { // 중량 오버			
			pc.sendPackets(new S_ServerMessage(110)); // \f1아이템이 너무 무거워 전투할 수가 없습니다.
			return;
		}

		if (pc.isInvisble()) { // 인비지비리티, 브라인드하이딘그중
			return;
		}
		if(pc.hasSkillEffect(L1SkillId.STATUS_XNAKD)){ //추가
			   return;
		}

		if (pc.isInvisDelay()) { // 인비지비리티디레이중
			return;
		}

		if (target instanceof L1Character) {
			if (target.getMapId() != pc.getMapId()
					|| pc.getLocation().getLineDistance(target.getLocation()) > 20D) { // 타겟이 이상한 장소에 있으면(자) 종료
				return;
			}
		}
	
		if (target instanceof L1NpcInstance) {
			int hiddenStatus = ((L1NpcInstance) target).getHiddenStatus();
              if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK
               || hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY
			   || hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK_ANTA			////////////////////////////////////  몹스킬패턴 추가   
			   || hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY_LIND			////////////////////////////////////  몹스킬패턴 추가 
			   || hiddenStatus == L1NpcInstance.HIDDEN_STATUS_APPEAR			////////////////////////////////////  몹스킬패턴 추가 
				) { // 지중에 끼어있지 않은, 날아있다
				return;
			}
		}

		// 공격 요구 간격을 체크한다
		if (Config.CHECK_ATTACK_INTERVAL) {
			int result;
			result = pc.getAcceleratorChecker()
					.checkInterval(AcceleratorChecker.ACT_TYPE.ATTACK);
			if (result == AcceleratorChecker.R_DISCONNECTED) {
				return;
			}
		}
		if(((pc.getX() == 33420 && pc.getY() == 32799)) && pc.getMapId() == 4){
			if (target instanceof L1NpcInstance) {
			        L1NpcInstance npc = (L1NpcInstance) target;
			         if(npc.getNpcTemplate().get_npcId() == 300026) {//버프사
			         L1Gambling2 gam2 = new L1Gambling2();
			         gam2.dealerTrade(pc);
			              }
			       }   
		}		
		if(((pc.getX() == 33515 && pc.getY() == 32851)) && pc.getMapId() == 4){
			   if (target instanceof L1NpcInstance) {
			        L1NpcInstance npc = (L1NpcInstance) target;
			        if(npc.getNpcTemplate().get_npcId() == 300027){//소막
			         L1Gambling3 gam3 = new L1Gambling3();
			         gam3.dealerTrade(pc);
			        }
			       }
			  }
		
		// 공격 액션을 취할 수 있는 경우의 처리
		//if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아의 해제
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			//pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startMpRegenerationByDoll();
		}
		//pc.killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.killSkillEffectTimer(MEDITATION);

		pc.delInvis(); // 투명상태의 해제

		pc.setRegenState(REGENSTATE_ATTACK);

		if (target != null && !((L1Character) target).isDead()) {
			target.onAction(pc);
		} else { // 하늘 공격
			L1ItemInstance weapon = pc.getWeapon();
			int weaponId = 0;
			int weaponType = 0;
			int tempchargfx = pc.getTempCharGfx() ;
    		L1ItemInstance arrow = null;
			L1ItemInstance sting = null;
			if (weapon != null) {
			weaponId = weapon.getItem().getItemId();
			weaponType = weapon.getItem().getType1();
			if (weaponType == 20) {
			arrow = pc.getInventory().getArrow();
			}
			if (weaponType == 62) {
			sting = pc.getInventory().getSting();
			}
			}
			pc.setHeading(pc.targetDirection(x, y));
			if (weaponType == 20 && (weaponId == 190 || arrow != null || tempchargfx == 7959)) {
			calcOrbit(pc.getX(), pc.getY(), pc.getHeading()); 
			if (arrow != null) { 
			pc.sendPackets(new S_UseArrowSkill(pc, 0, 66, _targetX, _targetY, true));
			pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 66, _targetX, _targetY, true));
			pc.getInventory().removeItem(arrow, 1);
			} else if (tempchargfx == 7959){ //천상의기사활마법
			pc.sendPackets(new S_UseArrowSkill(pc, 0, 7972, _targetX, _targetY, true));
			pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 7972, _targetX, _targetY, true));
	    	} else if (weaponId == 190) {
			pc.sendPackets(new S_UseArrowSkill(pc, 0, 2349, _targetX, _targetY, true));
			pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 2349, _targetX, _targetY, true));
			}
			} else if (weaponType == 62 && sting != null) {
				calcOrbit(pc.getX(), pc.getY(), pc.getHeading()); 
				pc.sendPackets(new S_UseArrowSkill(pc, 0, 2989, _targetX, _targetY, true));
				pc.broadcastPacket(new S_UseArrowSkill(pc, 0, 2989, _targetX, _targetY, true));
				pc.getInventory().removeItem(sting, 1);				
			} else {
				pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Attack));
				pc.broadcastPacket(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Attack));
				}
			}
		}
	private void calcOrbit(int cX, int cY, int head) {
		float disX = Math.abs(cX - _targetX);
		float disY = Math.abs(cY - _targetY);
		float dis = Math.max(disX, disY);
		float avgX = 0;
		float avgY = 0;
		if (dis == 0) {
			if (head == 1) {
				avgX = 1;
				avgY = -1;
				} else if (head == 2) {
					avgX = 1;
					avgY = 0;
					} else if (head == 3) {
						avgX = 1;
						avgY = 1;
						} else if (head == 4) {
							avgX = 0;
							avgY = 1;
							} else if (head == 5) {
								avgX = -1;
								avgY = 1;
								} else if (head == 6) {
									avgX = -1;
									avgY = 0;
									} else if (head == 7) {
										avgX = -1;
										avgY = -1;
										} else if (head == 0) {
											avgX = 0;
											avgY = -1;
											}
			} else {
				avgX = disX / dis;
				avgY = disY / dis;
				}
		
	int addX = (int) Math.floor((avgX * 15) + 0.59f);
	int addY = (int) Math.floor((avgY * 15) + 0.59f);

	if (cX > _targetX) {
	addX *= -1;
	}
	if (cY > _targetY) {
	addY *= -1;
	}

	_targetX = _targetX + addX;
	_targetY = _targetY + addY;
	}
	}

