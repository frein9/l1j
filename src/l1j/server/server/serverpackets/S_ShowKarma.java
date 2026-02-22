//UI 우호도 표시 by.ACE 
//S_PacketBox.java 에 추가하는 패킷임. subCode 가 0x57.
//이해하기 쉽게 임시로 만든 소스이며, 패킷을 S_PacketBox.java 에 추가하고 이 소스는 삭제하기 바람.

package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.clientpackets.C_LoginToServer;
public class S_ShowKarma extends ServerBasePacket {

	private static final String S_ShowKarma = "[S] S_ShowKarma";

	private byte[] _byte = null;
	public S_ShowKarma(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x57);
		writeD(pc.getKarma());
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_ShowKarma;
	}
}