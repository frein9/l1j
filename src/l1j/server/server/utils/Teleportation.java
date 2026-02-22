/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
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

import java.util.HashSet;
import java.util.logging.Logger;
import java.util.Random;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet; 
import java.sql.PreparedStatement; 
import java.util.logging.Level;

import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_SummonPack; 
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.L1DatabaseFactory;

import static l1j.server.server.model.skill.L1SkillId.*;
// Referenced classes of package l1j.server.server.utils:
// FaceToFace

public class Teleportation {

	private static Logger _log = Logger
			. getLogger(Teleportation.class.getName());

	private static Random _random = new Random();

	private Teleportation() {
	}

	public static void Teleportation(L1PcInstance pc) {
		if (pc.isDead() || pc.isTeleport()) {
			return;
		}

		int x = pc.getTeleportX();
		int y = pc.getTeleportY();
		short mapId = pc.getTeleportMapId();
		int head = pc.getTeleportHeading();

		// 텔레포트처가 부정하면 원의 좌표에(GM는 제외하다)
		L1Map map = L1WorldMap.getInstance(). getMap(mapId);

		if (! map.isInMap(x, y) && ! pc.isGm()) {
			x = pc.getX();
			y = pc.getY();
			mapId = pc.getMapId();
		}

		pc.setTeleport(true);

		L1Clan clan = L1World.getInstance(). getClan(pc.getClanname());
		if (clan != null) {
			if (clan.getWarehouseUsingChar() == pc.getId()) { // 자캐릭터가 크란 창고 사용중
				clan.setWarehouseUsingChar(0); // 크란 창고의 락을 해제
			}
		}

		L1World.getInstance(). moveVisibleObject(pc, mapId);
		pc.setLocation(x, y, mapId);
		pc.setHeading(head);
		pc.sendPackets(new S_MapID(pc.getMapId(), pc.getMap(). isUnderwater()));

		if (pc.isReserveGhost()) { // 고우스트 상태 해제
			pc.endGhost();
		}
		if (! pc.isGhost() && ! pc.isGmInvis() && ! pc.isInvisble()) {
			pc.broadcastPacket(new S_OtherCharPacks(pc));
		}
		pc.sendPackets(new S_OwnCharPack(pc));

		pc.removeAllKnownObjects();
		pc.sendVisualEffectAtTeleport(); // 크라운, 독, 수중등의 시각 효과를 표시
		pc.updateObject();
		// spr 번호 6310, 5641의 변신중에 텔레포트 하면(자) 텔레포트 후로 이동할 수 없어진다
		// 무기를 착탈하면(자) 이동할 수 있게 되기 (위해)때문에, S_CharVisualUpdate를 송신한다
		pc.sendPackets(new S_CharVisualUpdate(pc));
		 if (pc.getBraveItemEquipped() > 0){ 
			  int type = 0; 
			  if (pc.isElf()){ 
			    type = 3; 
			  }else{ 
			    type = 1; 
			  } 
			  pc.setBraveSpeed(1); 
			  pc.sendPackets(new S_SkillBrave(pc.getId(), type, -1)); 
			  pc.broadcastPacket(new S_SkillBrave(pc.getId(), type, 0)); 
			  } 
		 if (pc.hasSkillEffect(BLOODLUST)){ 
			  buff(pc);
		  }
		pc.killSkillEffectTimer(L1SkillId.MEDITATION);
		pc.setCallClanId(0); // 콜 크란을 주창한 후로 이동하면(자) 소환 무효

		/*
		 * subjects 펫과 사몬의 텔레포트처 화면내에 있던 플레이어.
		 * 각 펫 마다 UpdateObject를 실시하는 (분)편이 코드상에서는 스마트하지만,
		 * 네트워크 부하가 커지기 때문에(위해), 일단 Set에 격납해 마지막에 정리해 UpdateObject 한다.
		 */
		HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
		subjects.add(pc);

		if (! pc.isGhost() && pc.getMap(). isTakePets()) {
			// 애완동물과 사몬도 함께 이동시킨다.
			for (L1NpcInstance petNpc : pc.getPetList(). values()) {

				// 텔레포트처의 설정
				L1Location loc = pc.getLocation(). randomLocation(3, false);
				int nx = loc.getX();
				int ny = loc.getY();
				if (pc.getMapId() == 5125 || pc.getMapId() == 5131
						|| pc.getMapId() == 5132 || pc.getMapId() == 5133
						|| pc.getMapId() == 5134) { // 펫 매치 회장
					nx = 32799 + _random.nextInt(5) - 3;
					ny = 32864 + _random.nextInt(5) - 3;
				}
				teleport(petNpc, nx, ny, mapId, head);
				if (petNpc instanceof L1SummonInstance) { // 사몬몬스타
					L1SummonInstance summon = (L1SummonInstance) petNpc;
					pc.sendPackets(new S_SummonPack(summon, pc));
				} else if (petNpc instanceof L1PetInstance) { // 펫
					L1PetInstance pet = (L1PetInstance) petNpc;
					pc.sendPackets(new S_PetPack(pet, pc));
				}

				for (L1PcInstance visiblePc : L1World.getInstance()
						. getVisiblePlayer(petNpc)) {
					// 텔레포트원과 먼저 같은 PC가 있었을 경우, 올바르게 갱신되지 않기 때문에, 한 번 remove 한다.
					visiblePc.removeKnownObject(petNpc);
					subjects.add(visiblePc);
				}

			}

			// 매직 실업 수당도 함께 이동시킨다.
			for (L1DollInstance doll : pc.getDollList(). values()) {

				// 텔레포트처의 설정
				L1Location loc = pc.getLocation(). randomLocation(3, false);
				int nx = loc.getX();
				int ny = loc.getY();

				teleport(doll, nx, ny, mapId, head);
				pc.sendPackets(new S_DollPack(doll, pc));

				for (L1PcInstance visiblePc : L1World.getInstance()
						. getVisiblePlayer(doll)) {
					// 텔레포트원과 먼저 같은 PC가 있었을 경우, 올바르게 갱신되지 않기 때문에, 한 번 remove 한다.
					visiblePc.removeKnownObject(doll);
					subjects.add(visiblePc);
				}

			}
		}

		for (L1PcInstance updatePc : subjects) {
			updatePc.updateObject();
		}

		pc.setTeleport(false);

		if (pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			pc.sendPackets(new S_SkillIconWindShackle(pc.getId(),
					pc.getSkillEffectTimeSec(L1SkillId.WIND_SHACKLE)));
		}
	}

	private static void teleport(L1NpcInstance npc, int x, int y, short map,
			int head) {
		L1World.getInstance(). moveVisibleObject(npc, map);

		L1WorldMap.getInstance(). getMap(npc.getMapId()). setPassable(npc.getX(), npc.getY(), true);
		npc.setX(x);
		npc.setY(y);
		npc.setMap(map);
		npc.setHeading(head);
		L1WorldMap.getInstance(). getMap(npc.getMapId()). setPassable(npc.getX(), npc.getY(), false);
	}
		private static void buff(L1PcInstance pc) {//블러드러스트때문에
		  Connection con = null; 
		  PreparedStatement pstm = null; 
		  ResultSet rs = null; 
		  try { 

		  con = L1DatabaseFactory.getInstance().getConnection(); 
		  pstm = con.prepareStatement("SELECT * FROM character_buff WHERE char_obj_id=?"); 
		  pstm.setInt(1, pc.getId()); 
		  rs = pstm.executeQuery(); 
		  while (rs.next()) { 
		    int skillid = rs.getInt("skill_id"); 
		    int remaining_time = rs.getInt("remaining_time"); 
		    if (skillid == BLOODLUST) { // 블러드러스트 
		    pc.sendPackets(new S_SkillBrave(pc.getId(), 6, remaining_time)); 
		    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0)); 
		    pc.setBraveSpeed(1); 
		    pc.setSkillEffect(skillid, remaining_time * 1000); 
		    } 
		  } 
		  } catch (SQLException e) { 
		  _log.log(Level.SEVERE, e.getLocalizedMessage(), e); 
		  } finally { 
		  SQLUtil.close(rs); 
		  SQLUtil.close(pstm); 
		  SQLUtil.close(con); 
		  } 
		 } 
		} 

	
