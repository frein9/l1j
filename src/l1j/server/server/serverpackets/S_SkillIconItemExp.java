package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_SkillIconItemExp extends ServerBasePacket {

	public S_SkillIconItemExp(int paramInt){  // 천상의물약
		int i = 0;
		writeC(Opcodes.S_OPCODE_SKILLICONGFX);
		writeC(20);
		for (i = 0; i < 45; ++i)
			writeC(0);
		writeC((paramInt + 8) / 16);
		for (i = 0; i < 16; ++i)
			writeC(0);
		writeC(20);
		writeD(0);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}