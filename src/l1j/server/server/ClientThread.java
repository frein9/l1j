/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.encryptions.ClientIdExistsException;
import l1j.server.server.encryptions.LineageEncryption;
import l1j.server.server.encryptions.LineageKeys;
import l1j.server.server.model.Getback;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowerInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.UByte8;
import l1j.server.server.types.UChar8;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.utils.StreamUtil;
import l1j.server.server.utils.SystemUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
// 동일 아이피 3회이상 접속시 벤

// Referenced classes of package l1j.server.server:
// PacketHandler, Logins, IpTable, LoginController,
// ClanTable, IdFactory
//
public class ClientThread implements Runnable, PacketOutput {

    private static final byte[] FIRST_PACKET = {

            /*   (byte) 0x12, (byte) 0x00, // size
               (byte) 0x59, // id
               (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, // key
               (byte) 0xB8, (byte) 0x0C, (byte) 0xD0, (byte) 0x57,
               (byte) 0x46, (byte) 0xF4, (byte) 0xB8, (byte) 0x75,
               (byte) 0x02, (byte) 0x1F, (byte) 0xE4 */
            (byte) 0x12, (byte) 0x00, // size
            (byte) 0x34, // id
            (byte) 0x4D, (byte) 0x57, (byte) 0xD2, (byte) 0x34, // key
            (byte) 0xA6, (byte) 0xD4, (byte) 0xF7, (byte) 0x67,
            (byte) 0x25, (byte) 0x81, (byte) 0x04, (byte) 0x1C,
            (byte) 0x01, (byte) 0x00, (byte) 0xCE
    };
    private static final int M_CAPACITY = 3; // 이동 요구를 한 변에 받아들이는 최대 용량
    private static final int H_CAPACITY = 2;// 행동 요구를 한 변에 받아들이는 최대 용량
    private static Logger _log = Logger.getLogger(ClientThread.class.getName());
    private static Timer _IpTimer = new Timer();
    private static Timer _observerTimer = new Timer();
    public int IpTimeOk = 0;   //** 아이피 타이머 추가 **//  by 도우너
    public int ReturnToLogin = 0; // ########## A121 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
    long seed = 0x34d2574dL;
    private InputStream _in;
    private OutputStream _out;
    private PacketHandler _handler;
    private Account _account;
    private L1PcInstance _activeChar;
    private String _ip;
    private String _hostname;
    private Socket _csocket;
    private int _bullshitip; /*섭폭방지1*/
    private int _loginStatus = 0;
    // ClientThread에 의한 일정 간격 자동 세이브를 제한하기 때문에(위해)의 플래그(true:제한 false:제한 없음)
    // 현재는 C_LoginToServer가 실행되었을 때에 false가 되어,
    // C_NewCharSelect가 실행되었을 때에 true가 된다
    private boolean _charRestart = true;
    private LineageKeys _clkey;
    private long _lastSavedTime = System.currentTimeMillis();
    private int i = 0;
    private int 저장시간 = -1;
    private int 현시간 = -1;
    private int 시간당프레임 = 0;
    private long _lastSavedTime_inventory = System.currentTimeMillis();
    private int _kick = 0;

    /**
     * for Test
     */
    protected ClientThread() {
    }

    public ClientThread(Socket socket) throws IOException {
        _csocket = socket;
        _ip = socket.getInetAddress().getHostAddress();
        _bullshitip = socket.getPort(); /*섭폭방지1*/
        if (Config.HOSTNAME_LOOKUPS) {
            _hostname = socket.getInetAddress().getHostName();
        } else {
            _hostname = _ip;
        }
        _in = socket.getInputStream();
        _out = new BufferedOutputStream(socket.getOutputStream());

        // PacketHandler 초기설정
        _handler = new PacketHandler(this);
    }

    public static void quitGame(L1PcInstance pc) {
        // 사망하고 있으면(자) 거리에 되돌려, 공복 상태로 한다
        if (pc.isDead()) {
            int[] loc = Getback.GetBack_Location(pc, true);
            pc.setX(loc[0]);
            pc.setY(loc[1]);
            pc.setMap((short) loc[2]);
            pc.setCurrentHp(pc.getLevel());
            pc.set_food(40);
        }

        // 트레이드를 중지한다
        if (pc.getTradeID() != 0) { // 트레이드중
            L1Trade trade = new L1Trade();
            trade.TradeCancel(pc);
        }

        // 결투를 중지한다
        if (pc.getFightId() != 0) {
            pc.setFightId(0);
            L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
                    .findObject(pc.getFightId());
            if (fightPc != null) {
                fightPc.setFightId(0);
                fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
                        0, 0));
            }
        }

        // 파티를 빠진다
        if (pc.isInParty()) { // 파티중
            pc.getParty().leaveMember(pc);
        }

        // 채팅 파티를 빠진다
        if (pc.isInChatParty()) { // 채팅 파티중
            pc.getChatParty().leaveMember(pc);
        }

        // 애완동물을 월드 맵상으로부터 지운다
        // 사몬의 표시명을 변경한다
        Object[] petList = pc.getPetList().values().toArray();
        for (Object petObject : petList) {
            if (petObject instanceof L1PetInstance) {
                L1PetInstance pet = (L1PetInstance) petObject;
                pet.dropItem();
                pc.getPetList().remove(pet.getId());
                pet.deleteMe();
            }
            if (petObject instanceof L1SummonInstance) {
                L1SummonInstance summon = (L1SummonInstance) petObject;
                for (L1PcInstance visiblePc : L1World.getInstance()
                        .getVisiblePlayer(summon)) {
                    visiblePc.sendPackets(new S_SummonPack(summon, visiblePc,
                            false));
                }
            }
        }

        // 마법인형을 월드 맵상으로부터 지운다
        Object[] dollList = pc.getDollList().values().toArray();
        for (Object dollObject : dollList) {
            L1DollInstance doll = (L1DollInstance) dollObject;
            doll.deleteDoll();
        }

        // 종자를 월드 맵상으로부터 지워, 도우지점에 같은 글씨, 글귀가 다른 곳에도  나타내게 한다
        Object[] followerList = pc.getFollowerList().values().toArray();
        for (Object followerObject : followerList) {
            L1FollowerInstance follower = (L1FollowerInstance) followerObject;
            follower.setParalyzed(true);
            follower.spawn(follower.getNpcTemplate().get_npcId(),
                    follower.getX(), follower.getY(), follower.getHeading(),
                    follower.getMapId());
            follower.deleteMe();
        }

        // 엔챤트를 DB의 character_buff에 보존한다
        CharBuffTable.DeleteBuff(pc);
        CharBuffTable.SaveBuff(pc);
        pc.clearSkillEffectTimer();
        SkillCheck.getInstance().QuitDelSkill(pc);
        List<L1ItemInstance> itemlist = pc.getInventory().getItems();
        for (L1ItemInstance item : itemlist) {
            if (item.getCount() <= 0) {
                pc.getInventory().deleteItem(item);
                continue;
            }
        }
        pc.stopEtcMonitor();
        pc.setOnlineStatus(0);
        try {
            pc.save();
            pc.saveInventory();
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public String getIp() {
        return _ip;
    }

    public String getHostname() {
        return _hostname;
    }

    public void CharReStart(boolean flag) {
        _charRestart = flag;
    }

    private byte[] readPacket() throws Exception {
        try {
            int hiByte = _in.read();
            int loByte = _in.read();
            if (loByte < 0) {
                throw new RuntimeException();
            }
            int dataLength = (loByte * 256 + hiByte) - 2;

            byte data[] = new byte[dataLength];

            int readSize = 0;

            for (int i = 0; i != -1 && readSize < dataLength; readSize += i) {
                i = _in.read(data, readSize, dataLength - readSize);
            }

            if (readSize != dataLength) {
                _log
                        .warning("Incomplete Packet is sent to the server, closing connection.");
                throw new RuntimeException();
            }

            return LineageEncryption.decrypt(data, dataLength, _clkey);
        } catch (IOException e) {
            throw e;
        }
    }

    private void doAutoSave() throws Exception {
        if (_activeChar == null || _charRestart) {
            return;
        }
        try {
            // 캐릭터 정보
            if (Config.AUTOSAVE_INTERVAL * 1000
                    < System.currentTimeMillis() - _lastSavedTime) {
                _activeChar.save();
                _lastSavedTime = System.currentTimeMillis();
            }

            // 소지 아이템 정보
            if (Config.AUTOSAVE_INTERVAL_INVENTORY * 1000
                    < System.currentTimeMillis() - _lastSavedTime_inventory) {
                _activeChar.saveInventory();
                _lastSavedTime_inventory = System.currentTimeMillis();
            }
        } catch (Exception e) {
            _log.warning("Client autosave failure.");
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw e;
        }
    }

    @Override
    public void run() {
        /*섭폭방지2*/
        ipcount(_hostname);
        ipcountban(_hostname);
        IpTimer iptimer = new IpTimer();
        GeneralThreadPool.getInstance().execute(iptimer);
        /*섭폭방지2*/
        _log.info("Server connection host:(" + _hostname + ")");
        System.out.println("──────────────");
        System.out.println("Used Memory: " + SystemUtil.getUsedMemoryMB() + "MB");
        System.out.println("Waiting for client ...");
        System.out.println("──────────────");

        Socket socket = _csocket;
        /*
         * 클라이언트로부터의 패킷을 어느 정도 제한한다.이유：부정의 오류 검출이 다발할 우려가 있기 때문에
         * ex1.서버에 과부하가 걸려있는 경우, 부하가 떨어졌을 때에 클라이언트 패킷을 단번에 처리해, 결과적으로 부정 취급이 된다.
         * ex2.서버측의 네트워크(나오고)에 래그가 있는 경우, 클라이언트 패킷이 단번에 흘러들어, 결과적으로 부정 취급이 된다.
         * ex3.클라이언트측의 네트워크(오름)에 래그가 있는 경우, 이하 같이.
         *
         * 무제한하게 하기 전에 부정 검출 방법을 다시 볼 필요가 있다.
         */
        HcPacket movePacket = new HcPacket(M_CAPACITY);
        HcPacket hcPacket = new HcPacket(H_CAPACITY);
        GeneralThreadPool.getInstance().execute(movePacket);
        GeneralThreadPool.getInstance().execute(hcPacket);


        ClientThreadIpTimer iptimer2 = new ClientThreadIpTimer(); //** 아이피타이머 추가 **//  by 도우너
        iptimer2.start();//** 아이피타이머 추가 **//  by 도우너

        ClientThreadObserver observer =
                new ClientThreadObserver(Config.AUTOMATIC_KICK * 60 * 1000); // 자동 절단까지의 시간(단위:ms)

        // 클라이언트 thread의 감시
        if (Config.AUTOMATIC_KICK > 0) {
            observer.start();
        }

        try {
            _out.write(FIRST_PACKET);
            _out.flush();
            try {
                //		long seed = 0x01010101L;
                long seed = 0x34d2574dL;
                _clkey = LineageEncryption.initKeys(socket, seed);
            } catch (ClientIdExistsException e) {
            }

            while (true) {
                doAutoSave();

                byte data[] = null;
                try {
                    data = readPacket();
                } catch (Exception e) {
                    break;
                }
                // _log.finest("[C]\n" + new
                // ByteArrayUtil(data).dumpToString());

                int opcode = data[0] & 0xFF;


                //아이피 타이머 추가 **//  by 도우너
                if (opcode == Opcodes.C_OPCODE_CLIENTVERSION) {
                    IpTimeOk = 1;
                }//아이피 타이머 추가 **//  by 도우너

                // 다중 로그인 대책
                if (opcode == Opcodes.C_OPCODE_COMMONCLICK
                        || opcode == Opcodes.C_OPCODE_CHANGECHAR) {
                    _loginStatus = 1;
                }
                if (opcode == Opcodes.C_OPCODE_LOGINTOSERVER) {
                    if (_loginStatus != 1) {
                        continue;
                    }
                }
                if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK
                        || opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
                    _loginStatus = 0;
                }

                // ########## A121 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
                if (opcode == Opcodes.C_OPCODE_COMMONCLICK) {
                    ReturnToLogin = 1;
                }
                if (opcode == Opcodes.C_OPCODE_LOGINTOSERVEROK) {
                    ReturnToLogin = 0;
                }
                if (opcode == Opcodes.C_OPCODE_RETURNTOLOGIN) {
                    ReturnToLogin++;
                    if (ReturnToLogin == 2) {
                        LoginController.getInstance().logout(this);
                        ReturnToLogin = 0;
                    }
                }
                // ########## A121 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########

                if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
                    // C_OPCODE_KEEPALIVE 이외의 뭔가의 패킷을 받으면(자) Observer에 통지
                    observer.packetReceived();
                }
                // null의 경우는 캐릭터 선택전이므로 Opcode의 취사 선택은 하지 않고 모두 실행
                if (_activeChar == null) {
                    _handler.handlePacket(data, _activeChar);
                    continue;
                }

                // 이후, PacketHandler의 처리 상황이 ClientThread에 영향을 주지 않게 하기 때문에(위해)의 처리
                // 목적은 Opcode의 취사 선택과 ClientThread와 PacketHandler의 분리

                // 파기해선 안 되는 Opecode군
                // restart, 아이템 드롭, 아이템 삭제
                if (opcode == Opcodes.C_OPCODE_CHANGECHAR
                        || opcode == Opcodes.C_OPCODE_DROPITEM
                        || opcode == Opcodes.C_OPCODE_DELETEINVENTORYITEM) {
                    _handler.handlePacket(data, _activeChar);
                } else if (opcode == Opcodes.C_OPCODE_MOVECHAR) {
                    // 이동은 가능한 한 확실히 실시하기 때문에(위해), 이동 전용 thread에 주고 받아
                    movePacket.requestWork(data);
                } else {
                    // 패킷 처리 thread에 주고 받아
                    hcPacket.requestWork(data);
                }
                //스핵,노딜 막아보자
                Date now = new Date();
			/*	 if(opcode == Opcodes.C_OPCODE_USESKILL){ //무딜버그
					 switch(_activeChar.getTempCharGfx()) { //폴리번호

					 case 2177: // 사일런스
					 case 870: // 캔슬레이션
					 case 227: // 리무브 커스
					 case 2175: // 다크니스
					 case 226: // 크리에이트 좀비
					 case 751: // 인챈트 마이티
					 case 755: // 헤이스트
					 case 2176: // 블레스 웨폰
					 case 3936: // 홀리 워크
					 case 3104: // 그레이터 헤이스트
					 case 3943: // 버서커스
					 case 2230: // 디지즈
					 case 3944: // 리절렉션
					 case 231: // 셰이프 체인지
					 case 2236: // 매스 텔레포트
					 case 3934: // 카운터 디텍션
					 case 763: // 크리에이트 매지컬 웨폰
					 case 3935: // 어드밴스 스피릿
					 case 1819: // 파이어 스톰
					 case 4155: // 린드비오르 바람 브레스
					 case 1783: // 화염의 혼 켈베로스 불 뿜기
					 시간당프레임 = 1;
					 break;
					 case 2244: // 블레싱
					 case 744: // 힐
					 case 2510: // 라이트
					 case 221: // 실드
					 case 167: // 에너지 볼트
					 case 169: // 텔레포트
					 case 1797: // 아이스 대거
					 case 1799: // 윈드 커터
					 case 236: // 뱀파이어릭 터치
					 case 830: // 그레이터 힐
					 case 1804: // 프로즌 클라우드
					 case 1805: // 어스 재일
					 case 1809: // 콘 오브 콜드
					 case 2171: // 마나 드레인
					 case 129: // 이럽션
					 case 749: // 디텍션
					 case 746: // 커스: 블라인드
					 case 1811: // 선 버스트
					 case 2228: // 위크니스
					 case 759: // 힐 올
					 case 832: // 풀 힐
					 case 1812: // 어스 퀘이크
					 case 3924: // 라이트닝 스톰
					 case 757: // 블리자드
					 case 2232: // 디케이 포션
					 시간당프레임 = 5;
					 break;
					 case 760: // 포그 오브 슬리핑
					 case 228: // 이뮨 투 함
					 case 762: // 미티어 스트라이크
					 case 4434: // 쇼크 스턴
					 시간당프레임 = 16;
					 break;
					 case 1815: // 디스인티그레이트
					 case 4648: // 바운스
					 case 5831: // 솔리드 캐리지
					 시간당프레임 = 21;
					 break;
					 default:
					 시간당프레임 = 4;
					 break;
					 }
					 if(now.getSeconds() == 저장시간)
					 {
					 현시간 = 저장시간;
					 i++;
					 }
					 if(now.getSeconds() != 현시간)
					 {
					 i = 0;
					 현시간 = now.getSeconds();
					 }
					 if(i >= 시간당프레임) //변신종류 별로 최대 프레임 조건 판단
					 {
					 System.out.print("[노딜]: ");
					 System.out.println("(" + getAccountName() + ")");
					 System.out.print("[초당 프레임 첵크]: ");
					 System.out.println(i+1);
					 i = 0;
					 break;
					 }
					 저장시간 = now.getSeconds();
					 } */

                if (opcode == Opcodes.C_OPCODE_ARROWATTACK
                        || opcode == Opcodes.C_OPCODE_ATTACK
                        || opcode == Opcodes.C_OPCODE_MOVECHAR) {//공속 이속프레임추가
                    switch (_activeChar.getTempCharGfx()) { //폴리번호

                        case 734: //법사
                        case 1186: //여법
                        case 61: //남기
                        case 48: //여기
                        case 0: //남군
                        case 1: //여군
                        case 37: //여요
                        case 138: //남요
                        case 2786: //남다
                        case 2796: //여다
                        case 6658: //용남
                        case 6661: //용녀
                        case 6671: //환남
                        case 6650: //환녀
                            시간당프레임 = 4;
                            break;
                        case 240: //데스나이트
                        case 2284: //다크엘프
                        case 3784: //55 데스나이트
                        case 3890: //다크나이트
                        case 3891: //블랙위자드
                        case 3892: //다크레인저
                        case 3893: //실버나이트
                        case 3894: //실버메지스터
                        case 3896: //소드나이트
                        case 3897: //위자드마스터
                        case 3898: //에로우마스터
                        case 3899: //아크나이트
                        case 3900: //아크위자드
                        case 3901: //아크레인저
                        case 3895: //실버요정
                        case 4932: //어세신마스터
                        case 6698: //블랙위자드
                        case 7038: //라미아스
                        case 7041: //엔디아스
                        case 7044: //이데아
                        case 6010: //붉은오크
                            시간당프레임 = 6;
                            break;
                        case 6137://걍데스
                        case 6140://다엘
                        case 6267://다크1
                        case 6268://다크2
                        case 6269://다크3
                        case 6279://다크4
                        case 6270://실버1
                        case 6271://실버1
                        case 6272://실버1
                        case 6280://실버1
                        case 6273://소드1
                        case 6274://소드1
                        case 6275://소드1
                        case 6281://소드1
                        case 6276://아크1
                        case 6277://아크1
                        case 6278://아크1
                        case 6282://아크1
                            시간당프레임 = 7;
                            break;
                        case 5645: //호박
                        case 3479: //리틀버그
                        case 3480: //리틀버그
                        case 3481: //리틀버그
                        case 3482: //리틀버그
                        case 7925: //산타
                        case 3888: //바포
                        case 3905: //베레스
                        case 4923: //흑기사
                        case 4133: //라쿤
                        case 146: //웅골
                        case 95: //셀로브
                            시간당프레임 = 8;
                            break;
                        case 2501: //잭
                        case 6080: //기마투구
                        case 6094: //기마투구
                        case 5641: //뽕데스
                        case 1080: //메티스
                        case 6227: //도펠보스
                        case 6697: //하피터번
                            시간당프레임 = 9;
                            break;
                        case 1353: //경주견
                        case 1355:
                        case 1357:
                        case 1359:
                        case 1461:
                        case 1462:
                        case 1463:
                        case 1464:
                        case 1465:
                        case 1466:
                        case 1467:
                        case 1468:
                        case 1469:
                        case 1470:
                        case 1471:
                        case 1472:
                        case 1473:
                        case 1474:
                        case 1475:
                        case 1476:
                            시간당프레임 = 10;
                            break;
                        default:
                            시간당프레임 = 5;
                            break;
                    }
                    if (now.getSeconds() == 저장시간) {
                        현시간 = 저장시간;
                        i++;
                    }
                    if (now.getSeconds() != 현시간) {
                        i = 0;
                        현시간 = now.getSeconds();
                    }
                    if (i >= 시간당프레임) //변신종류 별로 최대 프레임 조건 판단
                    {
                        System.out.print("[스핵]: ");
                        System.out.println("(" + getAccountName() + ")");
                        System.out.print("[초당 프레임 첵크]: ");
                        System.out.println(i + 1);
                        i = 0;
                        break;
                    }
                    저장시간 = now.getSeconds();
                }
            }
        } catch (Throwable e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            try {
                if (_activeChar != null) {
                    quitGame(_activeChar);
                    L1PcInstance pc = getActiveChar();
                    l1j.server.Leaf.list.remove(pc.getName());

                    synchronized (_activeChar) {
                        // 캐릭터를 월드내로부터 제거
                        _activeChar.logout();
                        setActiveChar(null);
                    }
                }

                // 만약을 위해 송신
                sendPacket(new S_Disconnect());

                StreamUtil.close(_out, _in);
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            } finally {
                LoginController.getInstance().logout(this);
            }
        }
        _csocket = null;
        _log.fine("Server thread[C] stopped");
        if (_kick < 1) {
            _log.info("(" + getAccountName() + ":" + _hostname + ")shut down connection.");
            System.out.println("Used Memory: " + SystemUtil.getUsedMemoryMB() + "MB");
            System.out.println("Waiting for client...");
        }
        /*섭폭방지1 될라나*/
        if (_bullshitip == 0) { /*포트가 0이면*/
            _log.info("[DDOS] [" + getAccountName() + ":" + _hostname + "]shut down connection. ");
            System.out.println("Used Memory: " + SystemUtil.getUsedMemoryMB() + "MB");
            System.out.println("Waiting for client...");
        }
        /*섭폭방지1 될라나*/
        return;
    }
    //** 아이피 타이머 추가 **//  by 도우너

    // 기존 소스와 약간 다르게 아이피카운트에서 삭제되게했습니다.
    public void ipcountzero(String ip) {
        Connection con = null;
        PreparedStatement pstm = null;
        String findip = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("DELETE FROM LOG_IPCOUNT WHERE IP=?");
            pstm.setString(1, ip);
            pstm.execute();
        } catch (SQLException e) {
            _log.info("아이피 카운터 제로 오류");
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public void kick() {
        sendPacket(new S_Disconnect());
        _kick = 1;
        StreamUtil.close(_out, _in);
    }

    private void ipcount(String _hostname) {
        Connection con = null;
        Connection con2 = null;
        Connection con3 = null;
        PreparedStatement pstm = null;
        PreparedStatement pstm2 = null;
        PreparedStatement pstm3 = null;
        ResultSet find = null;
        String findip = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM LOG_IPCOUNT WHERE IP=?");
            pstm.setString(1, _hostname);
            find = pstm.executeQuery();

            while (find.next()) {
                findip = find.getString(1);
            }

            if (findip == null) {
                con2 = L1DatabaseFactory.getInstance().getConnection();
                pstm2 = con2.prepareStatement("INSERT INTO LOG_IPCOUNT SET ip=?, COUNT=1");
                pstm2.setString(1, _hostname);
                pstm2.execute();
            } else {
                con3 = L1DatabaseFactory.getInstance().getConnection();
                pstm3 = con.prepareStatement("UPDATE LOG_IPCOUNT SET COUNT=COUNT+1 WHERE IP=?");
                pstm3.setString(1, _hostname);
                pstm3.execute();

            }
        } catch (SQLException e) {
            _log.info("아이피 카운터 추가 에러!");
        } finally {
            SQLUtil.close(find);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
            SQLUtil.close(pstm2);
            SQLUtil.close(con2);
            SQLUtil.close(pstm3);
            SQLUtil.close(con3);
        }
    }

    private void ipcountban(String _hostname) {
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet find = null;
        String findip = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT * FROM LOG_IPCOUNT WHERE IP=? AND COUNT > 5");
            // 이부분의 색을 구분한 이유는 이부분의 " 10 " 이란 숫자가 단일아이피로 접속한 아이피의 수를 제한한것입니다. 3, 4 는 너무적고 공유기를 사용하는 단체에서 실수를 할수있는부분이니'';
            pstm.setString(1, _hostname);
            find = pstm.executeQuery();

            while (find.next()) {
                findip = find.getString(1);
            }

            if (findip != null) {
                IpTable iptable = IpTable.getInstance();
                iptable.banIp(_hostname);
                MiniClient.getInstance().MessageToServer(_hostname); //심플파윌 1
            }
        } catch (SQLException e) {
            _log.info("3회 이상 접속 아이피 차단 에러");
        } finally {
            SQLUtil.close(find);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    @Override
    public void sendPacket(ServerBasePacket packet) {
        synchronized (this) {
            try {
                byte abyte0[] = packet.getContent();
                char ac[] = new char[abyte0.length];
                ac = UChar8.fromArray(abyte0);
                //System.out.println(packet); // cmd콘솔창 패킷추출
                //		l1j.server.Leaf.패킷출력창.append(packet + "\n");
                ac = LineageEncryption.encrypt(ac, _clkey);
                abyte0 = UByte8.fromArray(ac);
                int j = abyte0.length + 2;

                _out.write(j & 0xff);
                _out.write(j >> 8 & 0xff);
                _out.write(abyte0);
                _out.flush();
            } catch (Exception e) {
            }
        }
    }

    public void close() throws IOException {
        _csocket.close();
    }

    public L1PcInstance getActiveChar() {
        return _activeChar;
    }

    public void setActiveChar(L1PcInstance pc) {
        _activeChar = pc;
    }

    public Account getAccount() {
        return _account;
    }

    public void setAccount(Account account) {
        _account = account;
    }

    public String getAccountName() {
        if (_account == null) {
            return null;
        }
        return _account.getName();
    }

    /* 아이피 타이머 추가 */
    class IpTimer implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) {
                kick();
                _log.info("아이피타임카운터 에러!");
            }
            try {
                if (IpTimeOk == 0) {
                    kick();
                    _log.info(_hostname + " 10초동안 로그인정보가 없어 강제 종료합니다");
                }
            } catch (Exception e) {
                kick();
                _log.info("아이피타임카운터 에러!");
            }
        }
    }

    class ClientThreadIpTimer extends TimerTask {

        public void start() {
            _IpTimer.scheduleAtFixedRate(ClientThreadIpTimer.this, 0, 20 * 1000);
        }

        @Override
        public void run() {
            try {
                Thread.sleep(60 * 1000);

                if (IpTimeOk == 1) {
                    cancel();
                }

                if (IpTimeOk == 0) {
                    sendPacket(new S_CommonNews("15초안에 입력바람 3회이상시 벤."));
                    kick();
                    cancel();
                }

            } catch (Exception e) {
                _log.info("아이피타임카운터 에러!");
            }
        }
    }

    // 캐릭터의 행동 처리 thread
    class HcPacket implements Runnable {
        private final Queue<byte[]> _queue;

        private PacketHandler _handler;

        public HcPacket() {
            _queue = new ConcurrentLinkedQueue<byte[]>();
            _handler = new PacketHandler(ClientThread.this);
        }

        public HcPacket(int capacity) {
            _queue = new LinkedBlockingQueue<byte[]>(capacity);
            _handler = new PacketHandler(ClientThread.this);
        }

        public void requestWork(byte data[]) {
            _queue.offer(data);
        }

        @Override
        public void run() {
            byte[] data;
            while (_csocket != null) {
                data = _queue.poll();
                if (data != null) {
                    try {
                        _handler.handlePacket(data, _activeChar);
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                    }
                }
            }
            return;
        }
    }

    // 클라이언트 thread의 감시 타이머
    class ClientThreadObserver extends TimerTask {
        private final int _disconnectTimeMillis;
        private int _checkct = 1;

        public ClientThreadObserver(int disconnectTimeMillis) {
            _disconnectTimeMillis = disconnectTimeMillis;
        }

        public void start() {
            _observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 0,
                    _disconnectTimeMillis);
        }

        @Override
        public void run() {
            try {
                if (_csocket == null) {
                    cancel();
                    return;
                }

                if (_checkct > 0) {
                    _checkct = 0;
                    return;
                }

                if (_activeChar == null // 캐릭터 선택전
                        || _activeChar != null && !_activeChar.isPrivateShop()) { // 개인 상점중
                    kick();
                    _log.warning("일정시간 응답을 얻을 수  없었던 때문(" + _hostname
                            + ")(와)과의 접속을 강제 절단 했습니다.");
                    cancel();
                    return;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                cancel();
            }
        }

        public void packetReceived() {
            _checkct++;
        }
    }
}
