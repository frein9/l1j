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

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;

import java.util.logging.Level;
import java.util.logging.Logger;

public class L1SpawnUtil {
    private static Logger _log = Logger.getLogger(L1SpawnUtil.class.getName());

    public static void spawn(int npcId, int X, int Y, int Heading, int Mapid) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setMap((short) Mapid);
            npc.setX(X);
            npc.setY(Y);
            npc.setHomeX(X);
            npc.setHomeY(Y);
            npc.setHeading(Heading);
            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);
            npc.turnOnOffLight();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    // 변신  딜레이
    public static void spawnLocation(L1PcInstance pc, int npcId, int getX, int getY) {
        L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
        npc.setId(IdFactory.getInstance().nextId());
        npc.setMap(pc.getMapId());
        npc.setX(getX);
        npc.setY(getY);
        npc.setExp(2300); // 소환된 몹 경험치 셋팅
        L1World.getInstance().storeObject(npc);
        L1World.getInstance().addVisibleObject(npc);
    }

    public static void spawn(L1PcInstance pc, int npcId, int randomRange,
                             int timeMillisToDelete) {
        try {
            L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
            npc.setId(IdFactory.getInstance().nextId());
            npc.setMap(pc.getMapId());
            if (randomRange == 0) {
                npc.getLocation().set(pc.getLocation());
                npc.getLocation().forward(pc.getHeading());
            } else {
                int tryCount = 0;
                do {
                    tryCount++;
                    npc.setX(pc.getX() + (int) (Math.random() * randomRange)
                            - (int) (Math.random() * randomRange));
                    npc.setY(pc.getY() + (int) (Math.random() * randomRange)
                            - (int) (Math.random() * randomRange));
                    if (npc.getMap().isInMap(npc.getLocation())
                            && npc.getMap().isPassable(npc.getLocation())) {
                        break;
                    }
                    Thread.sleep(1);
                } while (tryCount < 50);

                if (tryCount >= 50) {
                    npc.getLocation().set(pc.getLocation());
                    npc.getLocation().forward(pc.getHeading());
                }
            }

            npc.setHomeX(npc.getX());
            npc.setHomeY(npc.getY());
            npc.setHeading(pc.getHeading());

            L1World.getInstance().storeObject(npc);
            L1World.getInstance().addVisibleObject(npc);
            // 안타라스(리뉴얼) 1,2,3 단계, (구)
            if (npcId == 777775 || npcId == 777776 || npcId == 777779
                    || npcId == 45682) {
                npc.broadcastPacket(new S_DoActionGFX(
                        npc.getId(), 11));
                npc.setStatus(11);
                npc.broadcastPacket(new S_NPCPack(npc));
                npc.broadcastPacket(new S_DoActionGFX(
                        npc.getId(), 11));
                npc.setStatus(0);
                npc.broadcastPacket(new S_NPCPack(npc));
                npc.onNpcAI();
            }

            npc.turnOnOffLight();
            npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE); // 채팅 개시
            if (0 < timeMillisToDelete) {
                L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
                        timeMillisToDelete);
                timer.begin();
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }
}
