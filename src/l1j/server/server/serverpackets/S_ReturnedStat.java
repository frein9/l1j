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

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Character;


public class S_ReturnedStat extends ServerBasePacket {
	
	private static Logger _log = Logger.getLogger(S_ReturnedStat.class.
			getName());
	private static final String S_ReturnedStat = "[S] S_ReturnedStat";
	private byte[] _byte = null;

	
	public static final int 시작 = 1;
	
	public static final int 종료 = 2;
	
	public static final int 레벨업 = 3;
	
	public S_ReturnedStat(L1PcInstance pc, int type) {
		buildPacket(pc, type);
	}
	
	
	
	private void buildPacket(L1PcInstance pc, int type) {
		short init_hp = 0;
		short init_mp = 0;
		
		if (pc.isCrown()) { // CROWN
			init_hp = 14;
			switch (pc.getWis()) {
			case 11:
				init_mp = 2;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 3;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 4;
				break;
			default:
				init_mp = 2;
				break;
			}
		} else if (pc.isKnight()) { // KNIGHT
			init_hp = 16;
			switch (pc.getWis()) {
			case 9:
			case 10:
			case 11:
				init_mp = 1;
				break;
			case 12:
			case 13:
				init_mp = 2;
				break;
			default:
				init_mp = 1;
				break;
			}
		} else if (pc.isElf()) { // ELF
			init_hp = 15;
			switch (pc.getWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 4;
				break;
			}
		} else if (pc.isWizard()) { // WIZ
			init_hp = 12;
			switch (pc.getWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 6;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 8;
				break;
			default:
				init_mp = 6;
				break;
			}
		} else if (pc.isDarkelf()) { // DE
			init_hp = 12;
			switch (pc.getWis()) {
			case 10:
			case 11:
				init_mp = 3;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 3;
				break;
			}
		} else if (pc.isDragonKnight()) { // 용기사
			init_hp = 16;
			switch (pc.getWis()) {
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
				init_mp = 2;
				break;
			default:
				init_mp = 2;
				break;
			}
		} else if (pc.isBlackWizard()) { // 환술사
			init_hp = 14;
			switch (pc.getWis()) {
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 5;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 5;
				break;
			}
		}
		writeC(Opcodes.S_OPCODE_RETURNEDSTAT);
    		switch (type) {
    		case 시작:
    			writeC(1);
            	writeH(init_hp);
            	writeH(init_mp);
            	writeC(10);
            	writeC(pc.getLevel());
            	writeC(5);
            break;
    		case 종료:
            	writeC(3);
            	writeC(pc.getElixirStats());
            break;
    		case 레벨업:
                writeC(2);
                writeC(pc.getLevel());
                writeC(pc.getHighLevel());
                writeH(pc.getMaxHp());
                writeH(pc.getMaxMp());
                writeH(pc.getAc());
                writeC(pc.getStr());
                writeC(pc.getInt());
                writeC(pc.getWis());
                writeC(pc.getDex());
                writeC(pc.getCon());
                writeC(pc.getCha());
                break;
    		}
    }

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}
	
	public String getType() {
		return S_ReturnedStat;
	}
}