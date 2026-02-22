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

import l1j.server.server.Opcodes;

/*2E 4E 95 DD 29 1D 01 C8 53 20 B6 AD 97 30 BC CF 	.N..)...S ...0..
67 18 00 A1 7F 1E 82  

0000 : 6c 2e 4e 95 dd 29 1d 01 c8 53 20 b6 ad 97 30 bc    l.N..)...S ...0.
0010 : cf 67 5e 01 fc 32 00 06                            .g^..2..

0000 : 6c 2e 40 63 aa 61 03 88 a7 5a 7d 97 9d 7a 39 d0    l.@c.a...Z}..z9.
0010 : 04 73 6b 05 05 80 db 7f                            .sk....

0000 : 6c 2e 4e 95 dd 29 1d 01 c8 53 20 b6 ad 97 30 bc    l.N..)...S ...0.
0010 : cf 67 1d aa 75 df 73 4f                            .g..u.sO

0000 : 6c 2e 2f 2f 2d f8 2a e6 79 8e 35 02 03 d0 95 e3    l.//-.*.y.5.....
0010 : ac 00 57 b2 61 86 8a 14                            ..W.a...
*/

public class S_Unknown2 extends ServerBasePacket{
	public S_Unknown2(int type){
		writeC(Opcodes.S_OPCODE_UNKNOWN2);
		switch(type){
			case 0: // 계정 남은 시간, 따로 처리
			/*	writeC(0x3d);
				writeD(0x009283cb); // 남은시간
				writeC(0x00); // 남은결제
				writeC(0x23); */
				writeC(0x3d); //참고 이번에 올라온 패킷(10,02,26)
			    writeD(0x00); // 남은시간
			    writeC(0x00); // 결제남은 갯수
			    writeC(0x00); 
	    		break;
			case 1: // 점팩용 / 일팩 미사용
				writeC(0x2E); // 46
				writeC(0x42);
				writeC(0x01);
				writeC(0x00);
				writeC(0x00);
				writeC(0xac);
				writeC(0x98);
				writeC(0x37);
				writeC(0x03);
				writeC(0xff);
				writeC(0xe0);
				writeC(0x27);
				writeC(0xe4);
				writeC(0xbc);
				writeC(0x16);
				writeC(0xfe);
				writeC(0x42);
				writeC(0xdd);
				writeC(0x3a);
				writeC(0x16);
				writeC(0x00);
				writeC(0x93);
				writeC(0x17);
				break;
			case 2: // 리스
				writeC(0x2a);
				writeD(0);
				writeH(0);
				break;
			case 3: // 케릭접속 // 로긴투서버
				writeC(0x2E); // 46
				writeC(0x7A);
				writeC(0x00);
				writeC(0x00);
				writeC(0x00);
				writeC(0x44);
				writeC(0x2F);
				writeC(0x18);
				writeC(0xC1);
				writeC(0x12);
				writeC(0xd5);
				writeC(0xf7);
				writeC(0xaf);
				writeC(0xd9);
				writeC(0xd6);
				writeC(0x97);
				writeC(0x11);
				writeC(0x00);
				writeC(0x00);
				writeC(0x00);
				writeC(0x7f);
				writeC(0xdb);
				writeC(0x7d);
				writeD(0); //
				writeH(0); //						
				break;
			case 4: // 3단 가속
				writeC(0x0A);
				writeC(0x87);
				writeC(0xC9);
				writeC(0x25);
				writeC(0x2E);
				writeC(0xDE);
		}
	}
	
	@Override
	public byte[] getContent(){
		return getBytes();
	}
}
