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
package l1j.server.server.command.executor;

import l1j.server.server.GMCommandsConfig;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.util.logging.Logger;

public class L1GMRoom implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1GMRoom.class.getName());

    private L1GMRoom() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1GMRoom();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (pc.getInventory().checkEquipped(300000)) {   // 운영자의 반지 착용했을때 운영자 명령어 사용가능

                int i = 0;
                try {
                    i = Integer.parseInt(arg);
                } catch (NumberFormatException e) {
                }

                if (i == 1) {
                    L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, false);
                } else if (i == 2) {
                    L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false);  //판도라
                } else if (i == 3) {
                    L1Teleport.teleport(pc, 33429, 32814, (short) 4, 5, false);  //기란
                } else if (i == 4) {
                    L1Teleport.teleport(pc, 32535, 32955, (short) 777, 5, false);  // 버땅 그신
                } else if (i == 5) {
                    L1Teleport.teleport(pc, 32736, 32787, (short) 15, 5, false);  //캔트성
                } else if (i == 6) {
                    L1Teleport.teleport(pc, 32735, 32788, (short) 29, 5, false);  //원다우드성
                } else if (i == 7) {
                    L1Teleport.teleport(pc, 32572, 32826, (short) 64, 5, false);  //하이네성
                } else if (i == 8) {
                    L1Teleport.teleport(pc, 32730, 32802, (short) 52, 5, false);  //기란성
                } else if (i == 9) {
                    L1Teleport.teleport(pc, 32895, 32533, (short) 300, 5, false);  //아덴
                } else if (i == 10) {
                    L1Teleport.teleport(pc, 32736, 32799, (short) 39, 5, false);  //감옥
                } else if (i == 11) {
                    L1Teleport.teleport(pc, 32737, 32737, (short) 8014, 5, false);  //창고
                } else if (i == 12) {
                    L1Teleport.teleport(pc, 32737, 32799, (short) 8013, 5, false);  //연구실
                } else if (i == 13) {
                    L1Teleport.teleport(pc, 32738, 32797, (short) 509, 5, false);  //카오스대전
                } else if (i == 14) {
                    L1Teleport.teleport(pc, 32866, 32640, (short) 501, 5, false);  //사탄의 늪
                } else if (i == 15) {
                    L1Teleport.teleport(pc, 32603, 32766, (short) 506, 5, false);  //시야의놀이터
                } else if (i == 16) {
                    L1Teleport.teleport(pc, 32769, 32827, (short) 610, 5, false);  //벗꽃;
                } else {
                    L1Location loc = GMCommandsConfig.ROOMS.get(arg.toLowerCase());
                    if (loc == null) {
                        pc.sendPackets(new S_SystemMessage(".1운영자방   2판도라   3기란   4버땅(그신)  5캔트성"));
                        pc.sendPackets(new S_SystemMessage(".6윈다우드성 7하이네성 8기란성 9아덴 10 감옥 11창고"));
                        pc.sendPackets(new S_SystemMessage(".12연구실 13카오스대전 14사탄의늪 15시야의놀이터   "));
                        pc.sendPackets(new S_SystemMessage(".16벗꽃   "));
                        return;
                    }
                    L1Teleport.teleport(pc, loc.getX(), loc.getY(), (short) loc
                            .getMapId(), 5, false);
                }
            } else {
                pc.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
                return;
            }
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(".귀환 [1~16] 또는 .귀환 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
        }
    }
}
