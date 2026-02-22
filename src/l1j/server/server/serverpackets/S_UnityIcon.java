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

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_UnityIcon extends ServerBasePacket {

	public S_UnityIcon(int DECREASE_WEIGHT, int DECAY_POTION, int SILENCE, int VENOM_RESIST, int WEAKNESS, int DISEASE,
			           int DRESS_EVASION, int BERSERKERS, int NATURES_TOUCH, int WIND_SHACKLE, 
			           int ERASE_MAGIC, int ADDITIONAL_FIRE, int ELEMENTAL_FALL_DOWN, int ELEMENTAL_FIRE,
			           int STRIKER_GALE, int SOUL_OF_FLAME, int POLLUTE_WATER,
			           int EXP_POTION, int SCROLL, int SCROLLTPYE,
			           int CONSENTRATION, int INSIGHT, int PANIC,
		               int MORTALBODY, int HOUROFDEATH, int PEAR,
			           int PAYTIONS, int GUARDBREAK, int DRAGON_SKIN, int STATUS_RIBRAVE) {
		writeC(Opcodes.S_OPCODE_SKILLICONGFX);
		writeC(0x14);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(DECREASE_WEIGHT); // 디크리즈 웨이트 DECREASE
		writeC(DECAY_POTION); // 디케이 포션
		writeC(0x00);
		writeC(SILENCE); // 사일런스
		writeC(VENOM_RESIST); // 베놈 레지스트
		writeC(WEAKNESS); // 위크니스
		writeC(DISEASE); // 디지즈
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(DRESS_EVASION);                      //드레스이베이전 !
		writeC(BERSERKERS);                        //버서커스 !
		writeC(NATURES_TOUCH);                       //네이쳐스터치
		writeC(WIND_SHACKLE);                         //윈드셰클
		writeC(ERASE_MAGIC);                         //이레이즈매직
		writeC(0x00);                              //디지즈아이콘인데 설명은 카운터미러효과라고 되있음
		writeC(ADDITIONAL_FIRE);                               //어디셔널 파이어
		writeC(ELEMENTAL_FALL_DOWN);                //엘리맨탈폴다운   
		writeC(0x00);
		writeC(ELEMENTAL_FIRE);                     //엘리맨탈 파이어
		writeC(0x00);
		writeC(0x00);              //기척을지워 괴물들이 눈치채지못하게합니다???아이콘도이상함
		writeC(0x00);
		
		writeC(STRIKER_GALE);                        // 스트라이커게일
		writeC(SOUL_OF_FLAME);                     //소울오브 프레임
		writeC(POLLUTE_WATER);                          //플루투워터
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);                //속성저항력 10? 
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);             //sp
		writeC(EXP_POTION);            //exp
		
		writeC(SCROLL);      //전투강화주문서 123 다있음?
		writeC(SCROLLTPYE);             //0-hp50hpr4, 1-mp40mpr4, 2-추타3공성3sp3
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(CONSENTRATION);                         //컨센트레이션
		writeC(INSIGHT);                        //인사이트
		writeC(PANIC);                       //패닉
		writeC(MORTALBODY);                       //모탈바디                 
		writeC(HOUROFDEATH);                       //호어오브데스
		writeC(PEAR);                     //피어
		writeC(PAYTIONS);                      //페이션스
		writeC(GUARDBREAK);                      //가드브레이크
		writeC(DRAGON_SKIN);                   //드래곤스킨
		writeC(STATUS_RIBRAVE);             //유그드라
		
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
		writeC(0x00);
	}
	
/*
	public S_BuffIcon() {
	  writeC(Opcodes.S_OPCODE_SKILLICONGFX);
	  writeC(0x14);
	  writeC(0); // 메디테이션
	  writeH(0); // 없음
	  writeC(0); // 디크리즈 웨이트
	  writeC(0); // 디케이 포션
	  writeC(0); // 앱솔루트 배리어
	  writeC(0); // 사일런스
	  writeC(0); // 베놈 레지스트
	  writeC(0); // 위크니스
	  writeC(0); // 디지즈
	  writeD(0); // 없음                16
	  
	  writeH(0); // 없음
	  writeC(0); // 없음
	  writeC(0); // 드레스 이베이젼
	  writeC(0); // 버서커스 
	  writeC(0); // 네이쳐스 터치
	  writeC(0); // 윈드셰클
	  writeC(0); // 이레이즈 매직
	  writeC(0); // 디지즈 (카운터 미러 효과)
	  writeC(0); // 엑조틱 바이탈라이즈
	  writeC(0); // 엘리멘탈 폴다운
	  writeC(0);
	  writeC(0); // 어디셔널 파이어
	  writeC(0);
	  writeC(0); // (기척을 지워 인식하지 못하게 합니다)
	  writeC(0);          
	  
	  writeC(0); // 스트라이커 게일
	  writeC(0); // 소울 오브 프레임
	  writeC(0); // 폴루트 워터
	  writeH(0);
	  writeC(0);
	  writeC(0); // 일반 요리 (속성저항력 10의 증가 효과가 있습니다)
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0); // 지혜의 물약
	  writeC(0); // 경험치 물약            
	  
	  writeC(0);
	  writeC(0); // 컬러풀 주문서
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0); // 컨센트 레이션
	  writeC(0); // 인사이트
	  writeC(0); // 패닉
	  writeC(0); // 모탈바디
	  writeC(0); // 호러 오브 데스
	  writeC(0); // 피어
	  writeC(0); // 페이션스
	  writeC(0); // 가드 브레이크
	  writeC(0); // 드래곤 스킨 
	  writeC(0);                      
	  
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);
	  writeC(0);                          
	 }
	 */

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
