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
package l1j.server.server.serverpackets;

import java.util.logging.Logger;

import l1j.server.Config;//<-----임포트 추가
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_ChatPacket extends ServerBasePacket {

	private static Logger _log = Logger.getLogger(S_ChatPacket.class.getName());
	private static final String _S__1F_NORMALCHATPACK = "[S] S_ChatPacket";
	private byte[] _byte = null;

	public S_ChatPacket(L1PcInstance pc, String chat, int opcode, int type) {

		if (type == 0) { // 통상 채팅
			writeC(opcode);
			writeC(type);
			writeD(pc.getId()); 
/*			if (pc.isInvisble()) { 
				writeD(0);
			} else {
				writeD(pc.getId());
			}	*/
			writeS(pc.getName() + ": " + chat);
			if(Config.일반 == true){
				l1j.server.Leaf.chatlog.append("\r\n[일반] " +pc.getName()+": " + chat);
			} 
		} else if (type == 2) // 절규
		{
			writeC(opcode);
			writeC(type);
			if (pc.isInvisble()) {
				writeD(0);
			} else {
				writeD(pc.getId());
			}
			writeS("<" + pc.getName() + "> " + chat);
			writeH(pc.getX());
			writeH(pc.getY());
		} else if (type == 3) { // 전체 채팅
			writeC(opcode);
			writeC(type);
			if (pc.isGm() == true) {
				writeS("\\fW[******] " + chat);
				if(Config.글로벌 == true){
					l1j.server.Leaf.chatlog.append("\r\n[운영자] " +pc.getName()+": " + chat);
				} 
			} else {
				writeS("[" + pc.getName() + "] " + chat);
			}
		} else if (type == 4) // 혈맹 채팅
		{
			writeC(opcode);
			writeC(type);
			writeS("{" + pc.getName() + "} " + chat);
			if(Config.혈맹 == true){
               l1j.server.Leaf.chatlog.append("\r\n[혈맹 ("+pc.getClanname()+") ] " +pc.getName()+": "+ chat);
            }
		} else if (type == 9) { // 위스파
			writeC(opcode);
			writeC(type);
			writeS("-> (" + pc.getName() + ") " + chat);
		} else if (type == 11) { // 파티 채팅
			writeC(opcode);
			writeC(type);
			writeS("(" + pc.getName() + ") " + chat);	
	        if(Config.파티 == true){
                  l1j.server.Leaf.chatlog.append("\r\n[파티] " +pc.getName()+": " + chat);
            }
		} else if (type == 12) { // 트레이드 채팅
			writeC(opcode);
			writeC(type);
			writeS("[" + pc.getName() + "] " + chat);
			/*if(Config.장사 == true){
               l1j.server.Leaf.chatlog.append("\r\n[장사] " +pc.getName()+": " + chat);
            }*/
		} else if (type == 13) { // 연합 채팅
			writeC(opcode);
			writeC(type);
			writeS("{{" + pc.getName() + "}} " + chat);
		} else if (type == 14) { // 채팅 파티
			writeC(opcode);
			writeC(type);
			//if (pc.isInvisble()) {
			//	writeD(0);
			//} else {
				writeD(pc.getId());
			//}
			writeS("(" + pc.getName() + ") " + chat);
			if(Config.채팅파티 == true){
               l1j.server.Leaf.chatlog.append("\r\n[채팅파티] " +pc.getName()+": " + chat);
           }
		} else if (type == 16) { // 위스파
			writeC(opcode);
			writeS(pc.getName());
			writeS(chat);
		}
	}

	@Override
	public byte[] getContent() {
		if (null == _byte) {
			_byte = _bao.toByteArray();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return _S__1F_NORMALCHATPACK;
	}

}