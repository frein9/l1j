
package l1j.server.server.serverpackets;

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.Opcodes;

public class S_GameEnd extends ServerBasePacket {

	private static final String S_GameEnd = "[S] S_GameEnd";

	private static Logger _log = Logger.getLogger(S_GameEnd.class.getName());

	private byte[] _byte = null;

	public S_GameEnd(L1PcInstance pc){
		buildPacket1(pc);
	}

	//0000 : 7e 46 00 f6 af 53 02 5c                            ~F...S.\

    /*패킷삭제*/
	private void buildPacket(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x46);
		writeC(0x93);
		writeC(0x5c);
		writeC(0x97);
		writeC(0xdc);
		writeC(0x2a);

	 }

	private void buildPacket1(L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		writeC(0x46);
        writeC(147);
        writeC(92);
        writeC(151);
        writeC(220);
        writeC(42);
        writeC(74);
	 }


	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_GameEnd;
	}
}
