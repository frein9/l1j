/* This program is free software; you can redistribute it and/or modify
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

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonRandom;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_OwnCharPack; //뚫어핵
import l1j.server.server.CrockController;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.L1Object;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_MoveChar extends ClientBasePacket {

	private static Logger _log = Logger.getLogger(C_MoveChar.class.getName());
	
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };
	
	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
		
	private static final int CLIENT_LANGUAGE = Config.CLIENT_LANGUAGE;

	// 맵 타일 조사용
	private void sendMapTileLog(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage(pc.getMap().toString(
				pc.getLocation())));
	}

	// 이동
	public C_MoveChar(byte decrypt[], ClientThread client)
			throws Exception {
		super(decrypt);
		int locx = readH();
		int locy = readH();
		int heading = readC();

		L1PcInstance pc = client.getActiveChar();
	/*	//뚫어핵
		if (pc == null) { // 추가
	        return;
	    } */
		//뚫어핵
		if (pc.isTeleport()) { // 텔레포트 처리중
			return;
		}
	    ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
        if(pc.getOnlineStatus() == 0){
           client.kick();
           return;
        }
        ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
		// 이동 요구 간격을 체크한다
		if (Config.CHECK_MOVE_INTERVAL) {
			int result;
			result = pc.getAcceleratorChecker()
					.checkInterval(AcceleratorChecker.ACT_TYPE.MOVE);
			if (result == AcceleratorChecker.R_DISCONNECTED) {
				return;
			}
		}

		//pc.killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.killSkillEffectTimer(MEDITATION);
		pc.setCallClanId(0); // 콜 크란을 주창한 후로 이동하면(자) 소환 무효

		//if (!pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 아브소르트바리아중은 아니다
		//	pc.setRegenState(REGENSTATE_MOVE);
		if (!pc.hasSkillEffect(ABSOLUTE_BARRIER)) { //
		}
		pc.getMap().setPassable(pc.getLocation(), true);
		//뚫어핵
	/*	if (pc.checkMove() == 0){
        pc.sendPackets(new S_OwnCharPack(pc));
        pc.removeAllKnownObjects();
        pc.updateObject();
        L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
        } 
		//뚫어핵
		  if (pc.checkMove() == 0){
        //  pc.sendPackets(new S_SystemMessage("뚫어."));
        pc.bkteleport();//효과부여 ~짱돌 2009 - 08 -26
        }  */

		if (CLIENT_LANGUAGE == 3) { // Taiwan Only
			heading ^= 0x49;
			locx = pc.getX();
			locy = pc.getY();
		}
		locx += HEADING_TABLE_X[heading];
		locy += HEADING_TABLE_Y[heading];
		
		  /** 시간의 균열 */
		//---------------------------수정 시작 by neodis-------------------------------------//
		/** 시간의 균열 */
		if(CrockController.getInstance().isMove())
		{
		 int[] loc = CrockController.getInstance().loc();
		 /* pc 좌표와 시간의 균열의 좌표가 일치하다면 */
		 if(Math.abs(loc[0]-pc.getX())<=1 && Math.abs(loc[1] - pc.getY())<=1 && loc[2] == pc.getMap().getId()) 
			//시간의 균열 주위 1px에 들어가면 텔레포트
	//   if(loc[0] == pc.getX() && loc[1] == pc.getY() && loc[2] == pc.getMap().getId()) 

		 {
		  pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 2000);
		  pc.stopHpRegeneration();
		  pc.stopMpRegeneration();
		  
		  if(CrockController.getInstance().isTebeOsiris() == true)
		  {
		   L1Teleport.teleport(pc, 32638, 32876, (short)780,4,false);    //테베   
		  }
		  else
		  {
		   L1Teleport.teleport(pc, 32793, 32753, (short)783,4,false);    //티칼   
		  }
		 }
		}
		//---------------------------수정 끝 by neodis-------------------------------------//
		/*	// 드래곤포탈 시작 - ACE
		if(!(L1World.getInstance().findNpc(777781)==null)){
			L1NpcInstance npc = L1World.getInstance().findNpc(777781);
			if(npc.getX()-pc.getX() == 1 && npc.getY() - pc.getY() == 0 
				&& npc.getMap().getId() == pc.getMap().getId()) {
					L1Teleport.teleport(pc, 32601, 32741, (short) 1005, 5,true);
					return;
			}
		} 
		// 드래곤포탈 끝 - ACE*/

		if (Dungeon.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // 지하 감옥에 텔레포트 했을 경우
			return;
		}
		if (DungeonRandom.getInstance().dg(locx, locy, pc.getMap().getId(), pc)) { // 텔레포트처가 랜덤인 텔레포트 지점
			return;
		}
		pc.getLocation().set(locx, locy);
		pc.setHeading(heading);
		pc.broadcastPacket(new S_MoveCharPacket(pc));
		L1WorldTraps.getInstance().onPlayerMoved(pc);
		pc.getMap().setPassable(pc.getLocation(), false);
		// user.UpdateObject(); // 가시 범위내의 전오브젝트 갱신
	}
}