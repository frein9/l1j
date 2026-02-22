
package l1j.server.server.serverpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.model.L1World;

public class S_GameRap extends ServerBasePacket {

	private static final String S_GameRanking = "[S] S_GameRap";

	private static Logger _log = Logger.getLogger(S_GameRap.class.getName());

	private byte[] _byte = null;


//0000 : 7e 43 04 00 01 00 c5 39                            ~C.....9

	public S_GameRap(L1PcInstance pc, int i){
		buildPacket1(pc, i);
	}
    
	private void buildPacket1(L1PcInstance pc, int i) {
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x43);
        writeH(0x04);
        writeH(i);

	 }

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_GameRanking;
	}
}
