package l1j.server.server;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Logger;

public class GiranController extends Thread {
    /**
     * 감옥이 열리는 시간 간격
     **/
    private static final int LOOP = 3;
    /**
     * 시각 데이터 포맷
     **/
    private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);
    /**
     * 시각 데이터 포맷
     **/
    private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);
    //public class GiranController implements Runnable{
    private static Logger _log = Logger.getLogger(GiranController.class.getName());
    private static GiranController _instance;
    /**
     * 기란감옥 오픈 시간(ms)
     **/
    private static long sTime = 0;
    /**
     * 기란 감옥 시작알림
     **/
    private boolean _GiranStart;
    /**
     * 감옥 입장여부
     **/
    private boolean _GiranOpen;
    /**
     * 듀얼 입장시관관리
     **/
    private boolean _GiranTime;
    /**
     * 감옥 종료 메소드
     **/
    private boolean Close;
    /**
     * 현재 시간을 임시로 담기
     **/
    private String NowTime = "";

    /**
     * 타임 컨트롤 객체
     **/
    public static GiranController getInstance() {
        if (_instance == null) {
            _instance = new GiranController();
        }
        return _instance;
    }

    public boolean getGiranStart() {
        return _GiranStart;
    }

    public void setGiranStart(boolean giran) {
        _GiranStart = giran;
    }

    public boolean getGiranOpen() {
        return _GiranStart;
    }

    public void setGiranOpen(boolean giran) {
        _GiranOpen = giran;
    }

    public boolean getGiranTime() {
        return _GiranTime;
    }

    public void setGiranTime(boolean giran) {
        _GiranTime = giran;
    }

    @Override
    public void run() {
        try {
            while (true) {

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                /** 기란 감옥 오픈 **/
                if (!isOpen())
                    continue;
                if (L1World.getInstance().getAllPlayers().size() <= 0)
                    continue;

                /** 기란감옥 오픈 메세지 송출**/
                L1World.getInstance().broadcastServerMessage("기란 감옥이 열렸습니다. 앞으로 3시간 체류 가능합니다.");

                /** 기란감옥 오픈 메세지 송출**/
                L1World.getInstance().broadcastServerMessage("악마왕 영토가 열렸습니다. 앞으로 3시간 체류 가능합니다.");

                /** 처음엔 입장을 안받는다 **/
                setGiranTime(false);

                /**기란오픈 시작**/
                setGiranOpen(true);

                /** 기란 오픈입장 시작 **/
                setGiranTime(true);

                /** 기란감옥 시작**/
                setGiranStart(true);

                /**감옥 게임실행 3시간 시작**/
                try {
                    Thread.sleep(86400000L);
                } catch (Exception e) {
                }
                /** 3시간 후 자동 텔레포트**/

                TelePort();

                /** 기란감옥종료 모두체류**/
                End();
            }

        } catch (Exception e1) {
        }
    }

    /**
     * 오픈 시각을 가져온다
     *
     * @return (Strind) 오픈 시각(MM-dd HH:mm)
     */
    public String OpenTime() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(sTime);
        return ss.format(c.getTime());
    }

    /**
     * 특정시각을 가져온다
     *
     * @param (long)
     * @return (string) 특정 시각(HH:mm)
     */
    private String getTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return s.format(c.getTime());
    }

    /**
     * 기란감옥이 열려있는지 확인
     *
     * @return (boolean) 열려있다면 true 닫혀있다면 false
     */
    private boolean isOpen() {
        NowTime = getTime();
        if ((Integer.parseInt(NowTime) % LOOP) == 0) return true;
        return false;
    }

    /**
     * 실제 현재시각을 가져온다
     *
     * @return (String) 현재 시각(HH:mm)
     */
    private String getTime() {
        return s.format(Calendar.getInstance().getTime());
    }

    /**
     * 아덴마을로 팅기게
     **/
    private void TelePort() {
        for (L1PcInstance c : L1World.getInstance().getAllPlayers()) {
            switch (c.getMap().getId()) {
                case 53:
                case 54:
                case 55:
                case 56:
                    c.setSkillEffect(78, 2000);
                    c.stopHpRegeneration();
                    c.stopMpRegeneration();
                    L1Teleport.teleport(c, 33970, 33246, (short) 4, 4, true);
                    c.sendPackets(new S_SystemMessage("기란 감옥 체류시간이 만료되어 마을로 돌아갑니다."));
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 기란감옥 종료
     **/
    private void End() {
        Announcements.getInstance().announceToAll("기란 감옥 시간이 모두 만료되었습니다.");
        setGiranStart(false);
        Close = false;
    }
}