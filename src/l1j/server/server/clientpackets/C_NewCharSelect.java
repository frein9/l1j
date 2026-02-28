/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import l1j.server.server.serverpackets.S_Unknown3;

import java.util.logging.Logger;


public class C_NewCharSelect extends ClientBasePacket {
    private static final String C_NEW_CHAR_SELECT = "[C] C_NewCharSelect";
    private static Logger _log = Logger.getLogger(C_NewCharSelect.class.getName());

    public C_NewCharSelect(byte[] decrypt, ClientThread client) {
        super(decrypt);
        client.CharReStart(true);
        client.sendPacket(new S_Unknown3()); // 리스 시즌3
        // client.sendPacket(new S_Unknown2(2)); //리스버튼을 위한 구조변경 
        // CT 와 연결된 Pc 객체가 있다면
        if (client.getActiveChar() != null) {
            L1PcInstance pc = client.getActiveChar();

            // 개인 상점이 아니라면
            if (!pc.isPrivateShop()) {
                // 기존 소스 그대로 ..
                l1j.server.Leaf.list.remove(pc.getName()); // ########## A22 세이버 채팅매니저 추가 ######
                _log.fine("Disconnect from: " + pc.getName());
                ClientThread.quitGame(pc);
                synchronized (pc) {
                    PcSave(pc);
                    pc.logout();
                    client.setActiveChar(null);
                }
            } else {
                l1j.server.Leaf.list.remove(pc.getName());
                synchronized (pc) {
                    // 소켓 / Thread 부분만 해제..
                    PcSave(pc); // <추가
                    pc.saveInventory();
                    pc.setNetConnection(null);
                    pc.setPacketOutput(null);
                    pc.stopHpRegeneration();
                    pc.stopMpRegeneration();
                    client.setActiveChar(null);
                    try {
                        pc.save();
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            _log.fine("Disconnect Request from Account : " + client.getAccountName());
        }
    }

    /**
     * 저장구문
     **/
    private void PcSave(L1PcInstance pc) {
        try {

            /** 피씨저장하고**/
            pc.save();

            /** 피씨 인벤토리도저장해주고**/
            pc.saveInventory();
        } catch (Exception ex) {

            /** 예외처리? 인벤 저장**/
            pc.saveInventory();
        }
    }

    /**
     * 저장구문
     **/
    @Override
    public String getType() {
        return C_NEW_CHAR_SELECT;
    }
}
