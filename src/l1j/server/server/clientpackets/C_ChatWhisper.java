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
import l1j.server.server.ClientThread;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ChatWhisper extends ClientBasePacket {

	private static final String C_CHAT_WHISPER = "[C] C_ChatWhisper";
	private static Logger _log = Logger
			. getLogger(C_ChatWhisper.class.getName());

	public C_ChatWhisper(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		String targetName = readS();
		String text = readS();
		L1PcInstance whisperFrom = client.getActiveChar();
		//System.out.println("텍스트 길이 : "+text.length()); 
		if (text.length() > 25) {
			whisperFrom.sendPackets(new S_SystemMessage("귓말로 보낼 수 있는 글자수를 초과하였습니다."));
			return;
		}
		// 채팅 금지중의 경우
		if (whisperFrom.hasSkillEffect(1005)) {
			whisperFrom.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
			return;
		}
		// 위스파 가능한 Lv미만의 경우
		if (whisperFrom.getLevel() < Config.WHISPER_CHAT_LEVEL) {
			whisperFrom.sendPackets(new S_ServerMessage(404, String
					. valueOf(Config.WHISPER_CHAT_LEVEL))); // %0레벨 이하에서는 위스파, 파티 채팅은 사용할 수 없습니다.
			return;
		}
		L1PcInstance whisperTo = L1World.getInstance().getPlayer(targetName);
		// 월드에 없는 경우
		if (whisperTo == null) {
			whisperFrom.sendPackets(new S_ServerMessage(73, targetName)); // \f1%0은 게임을 하고 있지 않습니다.
			return;
		}
		// 자기 자신에 대한 wis의 경우
		if (whisperTo.equals(whisperFrom)) {
			return;
		}
		// 차단되고 있는 경우
		if (whisperTo.getExcludingList(). contains(whisperFrom.getName())) {
			whisperFrom.sendPackets(new S_ServerMessage(117, whisperTo
					. getName())); // %0가 당신을 차단했습니다.
			return;
		}
		// 게임 옵션으로 OFF로 하고 있는 경우
		if (!whisperTo.isCanWhisper()) {
			whisperFrom.sendPackets(new S_ServerMessage(205, whisperTo
					. getName()));
			return;
		}

		ChatLogTable.getInstance().storeChat(whisperFrom, whisperTo, text, 1);
		whisperFrom.sendPackets(new S_ChatPacket(whisperTo, text,
				Opcodes.S_OPCODE_GLOBALCHAT, 9));
		whisperTo.sendPackets(new S_ChatPacket(whisperFrom, text,
				Opcodes.S_OPCODE_WHISPERCHAT, 16));
		if(Config.귓속말 == true){ //  // 편리성을 위해 c패킷으로 이동
           l1j.server.Leaf.chatlog.append("\r\n[귓속말] "+whisperFrom.getName()+"->"+whisperTo.getName()+" : "+text+""); // ##### 추가
        } // ##### 추가
	}

	@Override
	public String getType() {
		return C_CHAT_WHISPER;
	}
}
