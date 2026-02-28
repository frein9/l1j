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

package l1j.server.server;

public class Opcodes {
    /**
     * ClientPackets
     */
    public static final int S_OPCODE_INITPACKET = 52; //0x34( 패킷값에서 키값만 적어주면 끝 SEED 값)
    public static final int C_OPCODE_CLIENTVERSION = 70;// 클라에서 서버 버전 요청 하는 부분
    public static final int S_OPCODE_SERVERVERSION = 105; // 서버 버전
    public static final int C_OPCODE_COMMONCLICK = 84; // 공지사항 확인 눌럿을때
    public static final int S_OPCODE_COMMONNEWS = 80;// 공지사항
    public static final int C_OPCODE_LOGINPACKET = 33;// 월드 진입 관련 옵코드.
    public static final int S_OPCODE_LOGINRESULT = 6;// 계정정보가 담긴 패킷.
    public static final int S_OPCODE_PACKETBOX = 123;// 통합 패킷 관리 담당
    public static final int S_OPCODE_ACTIVESPELLS = 123;// SKILLICONGFX  PACKETBOX 값과 동일
    public static final int S_OPCODE_SKILLICONGFX = 123;
    public static final int S_OPCODE_UNKNOWN2 = 123;
    public static final int S_OPCODE_CHARAMOUNT = 78;// 해당 계정의 케릭 갯수
    public static final int S_OPCODE_CHARRESET = 96;// 스텟 초기화
    public static final int C_OPCODE_NEWCHAR = 113; // 케릭 생성
    public static final int S_OPCODE_NEWCHARWRONG = 54;// 캐릭터 생성시 처리부분
    public static final int S_OPCODE_NEWCHARPACK = 17;// 케릭 새로 만든거 보내기
    public static final int C_OPCODE_LOGINTOSERVER = 90; // 리스창에서 케릭 선택
    public static final int S_OPCODE_UNKNOWN1 = 63;// 접속담당
    public static final int S_OPCODE_INVLIST = 25;// 인벤토리의 아이템리스트
    public static final int S_OPCODE_ADDSKILL = 49; // 스킬 추가[버프패킷박스 다음]
    public static final int S_OPCODE_OWNCHARSTATUS = 30;// 케릭 정보 갱신
    public static final int S_OPCODE_MAPID = 109;// 맵 아이디
    public static final int S_OPCODE_CHARPACK = 1;// 오브젝트 그리기
    public static final int S_OPCODE_WEATHER = 20;// 날씨 조작하기
    public static final int S_OPCODE_SPMR = 125;// sp와 mr변경
    public static final int S_OPCODE_OWNCHARSTATUS2 = 3;// 스테이터스 갱신(디크리즈,민투)115
    public static final int S_OPCODE_SYSMSG = 2;// 시스템 메세지 (전챗)
    public static final int S_OPCODE_GLOBALCHAT = 2; // 시스템 메세지 (전챗)
    public static final int S_OPCODE_NPCSHOUT = 70;// 샤우팅 글
    public static final int S_OPCODE_MOVEOBJECT = 36;// 이동 오브젝트
    public static final int S_OPCODE_REMOVE_OBJECT = 48;// 오브젝트 삭제 (토글etc)66
    public static final int S_OPCODE_ATTRIBUTE = 38;// 위치값을 이동가능&불가능 조작 부분
    public static final int C_OPCODE_MOVECHAR = 5;// 이동요청 부분
    public static final int S_OPCODE_BLUEMESSAGE = 82;// 서버 메세지[방어구중복으로체크]
    public static final int C_OPCODE_NPCTALK = 94;// Npc와 대화부분
    public static final int S_OPCODE_SKILLHASTE = 35; // 헤이스트
    public static final int S_OPCODE_SKILLSOUNDGFX = 118;// 이팩트 부분 (헤이스트등)
    public static final int S_OPCODE_SHOWHTML = 102;// Npc클릭 Html열람
    public static final int S_OPCODE_ADDITEM = 66;// 아이템 생성[아이템 떨궜다가먹기]48
    public static final int S_OPCODE_DROPITEM = 1; // 아이템 생성[아이템 떨궜다가먹기]89
    public static final int C_OPCODE_LOGINTOSERVEROK = 31;// [환경설정->전챗켬,끔]64
    public static final int C_OPCODE_KEEPALIVE = 114;// 1분마다 한번씩 옴14
    public static final int S_OPCODE_GAMETIME = 50;// 게임 시간
    public static final int C_OPCODE_CHANGECHAR = 80;// 겜중에 리스창으로 빠짐7
    public static final int C_OPCODE_DELETECHAR = 82;// 케릭터 삭제
    public static final int S_OPCODE_DETELECHAROK = 68; // 케릭 삭제
    public static final int S_OPCODE_CHARLIST = 56;// 케릭터리스트의 케릭정보
    public static final int C_OPCODE_RETURNTOLOGIN = 108; // 다시 로긴창으로 넘어갈때
    public static final int C_OPCODE_QUITGAME = 77;// 로그인창에서 겜 종료할때
    public static final int S_OPCODE_LIGHT = 89; // 밝기
    public static final int C_OPCODE_CHATGLOBAL = 63;// 전체채팅
    public static final int C_OPCODE_CALL = 48; // CALL버튼 .감시
    public static final int S_OPCODE_ITEMNAME = 41;// 아이템 착용 (E표시)
    public static final int S_OPCODE_OWNCHARATTRDEF = 115;// AC 및 속성방어 갱신
    public static final int S_OPCODE_BOOKMARKS = 127;// 기억 리스트
    public static final int S_OPCODE_PINKNAME = 4;// 보라돌이
    public static final int C_OPCODE_USESKILL = 67;// 스킬 사용 부분
    public static final int S_OPCODE_DOACTIONGFX = 19; // 액션 부분(맞는모습등)
    public static final int S_OPCODE_MPUPDATE = 40;// MP 업데이트
    public static final int S_OPCODE_STRUP = 44;// 마법 힘업
    public static final int S_OPCODE_HPUPDATE = 84;// HP 업데이트
    public static final int S_OPCODE_SKILLICONSHIELD = 62;// 실드
    public static final int S_OPCODE_DEXUP = 87;// 덱업
    public static final int S_OPCODE_RANGESKILLS = 46;// 파톰 어퀘등의 스킬
    public static final int S_OPCODE_ABILITY = 67;// 이반, 소반  인프라 사용
    public static final int C_OPCODE_NPCACTION = 34;// Npc 대화 액션 부분
    public static final int C_OPCODE_CHAT = 12;// 일반 채팅
    public static final int S_OPCODE_NORMALCHAT = 15;// 일반 채팅
    public static final int C_OPCODE_CHATWHISPER = 107;// 귓속 채팅
    public static final int S_OPCODE_WHISPERCHAT = 43; // 귓속말
    public static final int C_OPCODE_CHARACTERCONFIG = 71;// 캐릭인벤슬롯정보 마지막에오는거
    public static final int C_OPCODE_CHARRESET = 66;// 스텟 초기화127
    public static final int C_OPCODE_ATTACK = 85;// 일반공격 부분
    public static final int S_OPCODE_ATTACKPACKET = 0;// 공격 표현 부분
    public static final int C_OPCODE_SKILLBUY = 23;// 스킬 구입
    public static final int S_OPCODE_SKILLBUY = 106;// 스킬 구입 창
    public static final int C_OPCODE_SKILLBUYOK = 122;// 스킬 구입 OK
    public static final int C_OPCODE_RESTART = 7;// 겜중에 죽어서 리셋 눌럿을때
    public static final int S_OPCODE_EXP = 71;// 경험치 갱신
    public static final int S_OPCODE_LAWFUL = 7; // 라우풀
    public static final int C_OPCODE_BOARD = 22;// 게시판 클릭
    public static final int S_OPCODE_BOARD = 8;// 게시판
    public static final int C_OPCODE_BOARDREAD = 97;// 게시판 읽기
    public static final int S_OPCODE_BOARDREAD = 76; // 게시판 읽기
    public static final int C_OPCODE_BOARDBACK = 98;// 게시판 next
    public static final int C_OPCODE_BOARDWRITE = 46; // 게시판 쓰기
    public static final int C_OPCODE_BOARDDELETE = 75; // 게시글 삭제
    public static final int S_OPCODE_SHOWSHOPBUYLIST = 11;// 상점 구입 부분
    public static final int S_OPCODE_SHOWSHOPSELLLIST = 37;// 상점에 판매 리스트102
    public static final int C_OPCODE_RESULT = 16;// 상점 결과 처리
    public static final int S_OPCODE_ITEMSTATUS = 26;// 인벤 아이템 갱신
    public static final int C_OPCODE_SHOP = 3;// [/상점 -> OK]
    public static final int S_OPCODE_PRIVATESHOPLIST = 47;// 개인상점 물품 열람
    public static final int C_OPCODE_PRIVATESHOPLIST = 100;// 개인상점 buy, sell
    public static final int S_OPCODE_SOUND = 59;// 사운드 이팩트 부분
    public static final int C_OPCODE_USEITEM = 25;// 아이템 사용 부분
    public static final int S_OPCODE_CHARVISUALUPDATE = 32;// 무기 착,탈 부분
    public static final int C_OPCODE_TRADE = 4;// [/교환]
    public static final int S_OPCODE_YES_NO = 117;// [ Y , N ] 메세지66
    public static final int S_OPCODE_TRADE = 116;// 거래창 부분 41
    public static final int C_OPCODE_TRADEADDCANCEL = 42;// 교환 취소
    public static final int S_OPCODE_TRADESTATUS = 65;// 거래 취소, 완료
    public static final int C_OPCODE_TRADEADDITEM = 15; // 교환창에 아이템 추가
    public static final int S_OPCODE_TRADEADDITEM = 10; // 거래창 아이템 추가 부분66
    public static final int C_OPCODE_TRADEADDOK = 61; // 교환 OK
    public static final int C_OPCODE_DELETEINVENTORYITEM = 50; // 휴지통에 아이템 삭제49
    public static final int S_OPCODE_DELETEINVENTORYITEM = 55;// 인벤토리 아이템 삭제
    public static final int C_OPCODE_MAIL = 14; // 편지함 클릭후 혈맹편지 왔다갔다 오는건
    public static final int S_OPCODE_MAIL = 9; // 편지 읽기
    public static final int C_OPCODE_DROPITEM = 89;// 아이템 떨구기
    public static final int C_OPCODE_PICKUPITEM = 128;// 아이템 줍기
    public static final int S_OPCODE_IDENTIFYDESC = 53;// 확인주문서
    public static final int C_OPCODE_ARROWATTACK = 105;// 활공격 부분
    public static final int C_OPCODE_DOOR = 83;// 문짝 클릭 부분
    public static final int C_OPCODE_BOOKMARK = 112;// [/기억 OO]
    public static final int C_OPCODE_WHO = 65;// [/누구]
    public static final int C_OPCODE_CHANGEHEADING = 36;// 방향 전환 부분
    public static final int S_OPCODE_CHANGEHEADING = 13;// 방향 전환 부분
    public static final int C_OPCODE_TITLE = 73;// 호칭 명령어
    public static final int C_OPCODE_PARTY = 111; // 호칭 변경
    public static final int C_OPCODE_PROPOSE = 64;// [/청혼]
    public static final int C_OPCODE_FIGHT = 13; // [/결투]
    public static final int C_OPCODE_EXCLUDE = 19;// [/차단]
    public static final int S_OPCODE_CHARTITLE = 111; // [/파티]
    public static final int C_OPCODE_LEAVEPARTY = 109;// 파티 탈퇴
    public static final int C_OPCODE_CREATEPARTY = 130;// /채팅초대
    public static final int C_OPCODE_BANPARTY = 38;// 파티 추방
    public static final int C_OPCODE_CHATPARTY = 0xffffff82;// /초대131
    public static final int C_OPCODE_CHECKPK = 27;// [/checkpk]
    public static final int S_OPCODE_SERVERMSG = 82;// 피케이 횟수 메시지[REDMESSAGE]
    public static final int C_OPCODE_JOINCLAN = 102;// [/가입]
    public static final int C_OPCODE_ADDBUDDY = 69; // 친구추가
    public static final int C_OPCODE_DELBUDDY = 32;// 친구삭제
    public static final int C_OPCODE_BUDDYLIST = 26; // 친구리스트
    public static final int C_OPCODE_EXTCOMMAND = 41;// <알트+1 ~ 5 까지 액션 >17
    public static final int C_OPCODE_EXIT_GHOST = 81;// 무한대전 관람모드 탈출
    public static final int C_OPCODE_TELEPORT = 96;// 텔레포트 사용
    public static final int S_OPCODE_TELEPORT = 103;// 텔레포트23
    public static final int S_OPCODE_BLESSOFEVA = 60; // 에바 아이콘
    public static final int C_OPCODE_FIX_WEAPON_LIST = 47;// 무기수리
    public static final int S_OPCODE_SELECTLIST = 31;// 무기수리
    public static final int S_OPCODE_SKILLBRAVE = 81;// 용기
    public static final int S_OPCODE_POLY = 99;// 변신
    public static final int C_OPCODE_PLEDGE = 56;// [/혈맹]
    public static final int C_OPCODE_CREATECLAN = 11;// 혈맹 창설
    public static final int C_OPCODE_LEAVECLANE = 79;// 혈맹 탈퇴
    public static final int C_OPCODE_ATTR = 86;// [ Y , N ] 혈맹가입선택
    public static final int C_OPCODE_BANCLAN = 44; // 혈맹 추방 명령어
    public static final int S_OPCODE_EMBLEM = 72;// 클라 문장요청
    public static final int C_OPCODE_EMBLEM = 28;// 문장데이타를 서버에 요청함
    public static final int S_OPCODE_TRUETARGET = 85; // 트루타겟
    public static final int C_OPCODE_ENTERPORTAL = 88;// 오른쪽 버튼으로 포탈 진입120
    public static final int C_OPCODE_SHIP = 123;// 배타서 내릴때 나옴
    public static final int C_OPCODE_SELECTLIST = 17;// 펫리스트에서 펫찾기
    public static final int C_OPCODE_PETMENU = 57;// 펫 메뉴
    public static final int C_OPCODE_GIVEITEM = 126;// 강제로 아이템 주기
    public static final int C_OPCODE_SELECTTARGET = 121; // 펫 공격 목표 지정
    public static final int S_OPCODE_SELECTTARGET = 61;// 공격 목표지정
    public static final int S_OPCODE_HOUSELIST = 119;// 아지트 리스트
    public static final int S_OPCODE_HOUSEMAP = 57;// 아지트 맵
    public static final int S_OPCODE_SHOWRETRIEVELIST = 58;// 창고 리스트
    public static final int S_OPCODE_CURSEBLIND = 77;// 눈멀기 효과,커스블라인드
    public static final int S_OPCODE_ITEMCOLOR = 34;// 봉인 주문서
    public static final int C_OPCODE_CLIENTREPORT = 74; //불량 유저 신고
    public static final int S_OPCODE_RESURRECTION = 90; // 부활 처리 부분
    public static final int S_OPCODE_PARALYSIS = 23;// 행동 제한 (커스패럴 상태)
    public static final int S_OPCODE_POISON = 28;// 독과 굳은 상태 표현23
    public static final int S_OPCODE_DELSKILL = 73;// 스킬 삭제 (정령력 제거)
    public static final int C_OPCODE_BOOKMARKDELETE = 53;// [/기억 후 기억목록클릭 delete]112
    public static final int C_OPCODE_RANK = 118;// [/동맹]
    public static final int C_OPCODE_WAR = 92;// 전쟁
    public static final int S_OPCODE_HPMETER = 92;// 미니 HP표현 부분
    public static final int C_OPCODE_AMOUNT = 39;//여관열쇠ok
    public static final int S_OPCODE_INPUTAMOUNT = 120;// 여관열쇠수량표시
    public static final int S_OPCODE_INVIS = 16;// 투명 처리 부분
    public static final int S_OPCODE_EFFECTLOCATION = 113;// 트랩 (좌표위 이펙트)바포지팡이이펙?
    public static final int S_OPCODE_DISCONNECT = 75;// 해당 케릭 강제 종료
    public static final int S_OPCODE_WAR = 51;// 전쟁
    public static final int S_OPCODE_CASTLEMASTER = 86; // 성소유목록 세팅 왕관
    public static final int C_OPCODE_DEPOSIT = 21; // 성 공금 입금
    public static final int S_OPCODE_DEPOSIT = 93;// 공금 입금
    public static final int C_OPCODE_CHANGEWARTIME = 72; //72 공성전시간을정한다.공성시간 지정37
    public static final int S_OPCODE_WARTIME = 79;// 공성 시간 변경
    public static final int C_OPCODE_SELECTWARTIME = 37; // 공성시간 지정
    public static final int C_OPCODE_TAXRATE = 110;// 세금 조정
    public static final int S_OPCODE_TAXRATE = 107;// 세율 조정
    public static final int C_OPCODE_DRAWAL = 87;// 공금 출금[자금을 인출한다]
    public static final int S_OPCODE_DRAWAL = 126;// 공금 출금
    public static final int S_OPCODE_ITEMAMOUNT = 122;// 인벤내 아이템 수량 정보 바꾸기(흑단쓰면같이옴)
    public static final int C_OPCODE_CLAN = 9;// 가시범위의 혈맹 마크 요청[폴더내emblem삭제]29
    public static final int S_OPCODE_USEMAP = 91;// 지도사용
    public static final int C_OPCODE_USEPETITEM = 60;// 펫 인벤토리 아이템 사용
    public static final int S_OPCODE_CHANGENAME = 74; // 오브젝트 네임변경시
    /**
     * Client 찾아야 할 것들
     */
    public static final int C_OPCODE_HIRESOLDIER = 125; // 과연...용병 선택은...?
    public static final int S_OPCODE_HIRESOLDIER = 12; // S_OPCODE_SOLDIERGIVELIST
    public static final int C_OPCODE_FISHCLICK = 30; // 낚시 입질 클릭 9174 30 94
    public static final int S_OPCODE_UNDERWATER = 73;
    public static final int S_OPCODE_LIQUOR = 1007; //술
    public static final int S_OPCODE_RETURNEDSTAT = 96; // 스텟 초기화
    public static final int C_OPCODE_BASERESET = 66; // 스텟 초기화127

    public Opcodes() {
    }
// public static final int C_OPCODE_CLIENTVERSION=51
// public static final int C_OPCODE_LOGINPACKET=0x12
// public static final int C_OPCODE_QUITGAME =0x7f
// public static final int C_OPCODE_RETURNTOLOGIN=0x4b
// public static final int C_OPCODE_CREATE_CHARACTER=0x46
// public static final int C_OPCODE_SELECT_CHARACTER=0x62
// public static final int C_OPCODE_LOGINTOSERVEROK=0x53
// public static final int C_OPCODE_KEEPALIVE=0x21
// public static final int C_OPCODE_MOVECHAR=0x45
// public static final int S_OPCODE_SERVERVERSION=0x77
// public static final int S_OPCODE_CHARAMOUNT=0x74
// public static final int S_OPCODE_CHARLIST=0x6c
// public static final int S_OPCODE_NEWCHARWRONG=0x58
// public static final int S_OPCODE_INVLIST=0x4a
// public static final int S_OPCODE_OWNCHARSTATUS=0x6a
// public static final int S_OPCODE_SHOWOBJ=0x5b
// public static final int S_OPCODE_NORMALCHAT=0x26
// public static final int S_OPCODE_LIGHT=0x7f
// public static final int S_OPCODE_OWNCHARSTATUS2=0x29
// public static final int S_OPCODE_NPCSHOUT=0x4c
// public static final int S_OPCODE_NEWCHARPACK=0x02
// public static final int S_OPCODE_UNKNOWN1=0x7a
// public static final int S_OPCODE_MOVEOBJECT=0x66

}