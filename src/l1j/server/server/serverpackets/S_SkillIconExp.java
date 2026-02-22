package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_SkillIconExp extends ServerBasePacket {

	public S_SkillIconExp(int i){  // 아인하사드의 축복
	    writeC(Opcodes.S_OPCODE_SKILLICONGFX);
	    writeC(0x52);
	    writeC(i);
	}
	
	@Override
	public byte[] getContent() {
		return getBytes();
	}
}