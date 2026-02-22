//by.ACE
package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.clientpackets.C_LoginToServer;

public class S_ShowOrignalBonus extends ServerBasePacket {

	private static final String S_ShowOrignalBonus = "[S] S_ShowOrignalBonus";

	private byte[] _byte = null;
	public S_ShowOrignalBonus(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		int increaseStr = 0;
		int increaseDex = 0;
		int increaseCon = 0;
		int increaseWis = 0;
		int increaseInt = 0;
		int increaseCha = 0; // 카리스마는 초기스탯보너스가 없다
		if (pc.isCrown()) { // 군주
			increaseStr = Math.max(pc.getOriginalStr() - 13, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 10, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 10, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 11, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 10, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 13, 0);
		} else if (pc.isKnight()) { // 나이트
			increaseStr = Math.max(pc.getOriginalStr() - 16, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 12, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 14, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 9, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 8, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 12, 0);
		} else if (pc.isWizard()) { // 위저드
			increaseStr = Math.max(pc.getOriginalStr() - 8, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 7, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 12, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 12, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 12, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 8, 0);
		} else if (pc.isElf()) { // 엘프
			increaseStr = Math.max(pc.getOriginalStr() - 11, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 12, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 12, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 12, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 12, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 9, 0);
		} else if (pc.isDarkelf()) { // 다크엘프
			increaseStr = Math.max(pc.getOriginalStr() - 12, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 15, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 8, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 10, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 11, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 9, 0);
		} else if (pc.isDragonKnight()) { // 용기사
			increaseStr = Math.max(pc.getOriginalStr() - 13, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 11, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 14, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 12, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 11, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 8, 0);
		} else if (pc.isBlackWizard()) { // 환술사 
			increaseStr = Math.max(pc.getOriginalStr() - 11, 0);
			increaseDex = Math.max(pc.getOriginalDex() - 10, 0);
			increaseCon = Math.max(pc.getOriginalCon() - 12, 0);
			increaseWis = Math.max(pc.getOriginalWis() - 12, 0);
			increaseInt = Math.max(pc.getOriginalInt() - 12, 0);
			increaseCha = Math.max(pc.getOriginalCha() - 8, 0);
		}

		writeC(Opcodes.S_OPCODE_RETURNEDSTAT);
		writeC(4);
		writeC(increaseInt * 16 + increaseStr); // 인트증가수치 * 16 + 힘증가수치
		writeC(increaseDex * 16 + increaseWis); // 덱스증가수치 * 16 + 위즈증가수치
		writeC(increaseCha * 16 + increaseCon); // 카리증가수치 * 16 + 콘증가수치
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	public String getType() {
		return S_ShowOrignalBonus;
	}
}