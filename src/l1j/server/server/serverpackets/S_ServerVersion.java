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

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.model.gametime.L1GameTimeClock;

public class S_ServerVersion extends ServerBasePacket {
	private static final String S_SERVER_VERSION = "[S] ServerVersion";

	public S_ServerVersion(){
		int time = L1GameTimeClock.getInstance().currentTime().getSeconds();
		time = time - (time % 300);
        writeC(Opcodes.S_OPCODE_SERVERVERSION);
   /*   writeC(0x00);
        writeC(0x64);
        writeD(0x00016451);
        writeD(0x00016450);
        writeD(0x00016068);
        writeD(0x000163f7);
        writeD(time);
        writeC(0x00);
        writeC(0x00);
        writeC(0x00); */
        //writeC(0x00); // must be
        //writeC(0x13); // low version
        //writeD(0x000188af); // serverver    af 88 01 00
        //writeD(0x000188f9); // cache version    f9 88 01 00
        //writeD(0x77ced152); // auth ver    52 d1 ce 77
        //writeD(0x00018898); // npc ver     98 88 01 00
		writeC(0x00);
        writeC(0x21);
        writeD(0x0001890E);
        writeD(0x000188f9);
        writeD(0x77cef9f0);
        writeD(0x00018914);
        writeD(time); // 로그인시의 시간 설정 49DC57F1
        writeC(0x00); // unk 1
        writeC(0x00); // unk 2
        writeC(0x00); // 0:영어 8:일본어 //writeC(0x00);
        //writeH(0x3727); //3727
        //writeH(0x4669); //4669
        //writeH(0x8793); //8793
	}

	@Override
	public byte[] getContent(){
		return getBytes();
	}

	public String getType(){
		return S_SERVER_VERSION;
	}
}