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

import l1j.server.server.ClientThread;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.FaceToFace;

import java.util.logging.Logger;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Propose extends ClientBasePacket {

    private static final String C_PROPOSE = "[C] C_Propose";
    private static Logger _log = Logger.getLogger(C_Propose.class.getName());

    public C_Propose(byte abyte0[], ClientThread clientthread) {
        super(abyte0);
        int c = readC();

        L1PcInstance pc = clientthread.getActiveChar();
        if (c == 0) { // /propose(/프로포즈)
            if (pc.isGhost()) {
                return;
            }
            L1PcInstance target = FaceToFace.faceToFace(pc);
            if (target != null) {
                if (pc.getPartnerId() != 0) {
                    pc.sendPackets(new S_ServerMessage(657)); // \f1당신은 벌써 결혼했습니다.
                    return;
                }
                if (target.getPartnerId() != 0) {
                    pc.sendPackets(new S_ServerMessage(658)); // \f1 그 상대는 벌써 결혼했습니다.
                    return;
                }
                if (pc.get_sex() == target.get_sex()) {
                    pc.sendPackets(new S_ServerMessage(661)); // \f1결혼상대는 이성이 아니면 안됩니다.
                    return;
                }
                if (pc.getX() >= 33974 && pc.getX() <= 33976 && pc.getY() >= 33362 && pc.getY() <= 33365 && pc.getMapId() == 4 && target.getX() >= 33974 && target.getX() <= 33976 && target.getY() >= 33362 && target.getY() <= 33365 && target.getMapId() == 4) {
                    target.setTempID(pc.getId()); // 상대의 오브젝트 ID를 보존해 둔다
                    target.sendPackets(new S_Message_YN(654, pc.getName())); // %0%s당신과 결혼 하고 싶어하고 있습니다.%0과 결혼합니까? (Y/N)
                }
            }
        } else if (c == 1) { // /divorce(/이혼)
            if (pc.getPartnerId() == 0) {
                pc.sendPackets(new S_ServerMessage(662)); // \f1당신은 결혼하지 않았습니다.
                return;
            }
            String y = "y";
            String Y = "Y";
            pc.sendPackets(new S_Message_YN(653, "")); // 이혼을 하면(자) 링은 사라져 버립니다. 이혼을 바랍니까? (Y/N)
            if (toString() == y || toString() == Y) {
                if (pc.getPartnerId() == 0) {
                    pc.getInventory().checkItem(40901, 1);
                    pc.getInventory().consumeItem(40901, 1);
                    pc.getInventory().checkItem(40902, 1);
                    pc.getInventory().consumeItem(40902, 1);
                    pc.getInventory().checkItem(40903, 1);
                    pc.getInventory().consumeItem(40903, 1);
                    pc.getInventory().checkItem(40904, 1);
                    pc.getInventory().consumeItem(40904, 1);
                    pc.getInventory().checkItem(40905, 1);
                    pc.getInventory().consumeItem(40905, 1);
                    pc.getInventory().checkItem(40906, 1);
                    pc.getInventory().consumeItem(40906, 1);
                    pc.getInventory().checkItem(40907, 1);
                    pc.getInventory().consumeItem(40907, 1);
                    pc.getInventory().checkItem(40908, 1);
                    pc.getInventory().consumeItem(40908, 1);
                }
            }
        }
    }

    @Override
    public String getType() {
        return C_PROPOSE;
    }
}
