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

import l1j.server.Config;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LightSpawnTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.MobGroupTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.RaceTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.datatables.WeaponSkillTable;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.ElementalStoneGenerator;
import l1j.server.server.model.Getback;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1BossCycle;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Cube;
import l1j.server.server.model.L1DeleteItemOnGround;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1Sys;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.utils.SystemUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;
// Referenced classes of package l1j.server.server:
// ClientThread, Logins, RateTable, IdFactory,
// LoginController, GameTimeController, Announcements,
// MobTable, SpawnTable, SkillsTable, PolyTable,
// TeleportLocations, ShopTable, NPCTalkDataTable, NpcSpawnTable,
// IpTable, Shutdown, NpcTable, MobGroupTable, NpcShoutTable

public class GameServer extends Thread {
    private static Logger _log = Logger.getLogger(GameServer.class.getName());
    private static GameServer _instance;
    private ServerSocket _serverSocket;
    private int _port;
    // private Logins _logins;
    private LoginController _loginController;
    private int chatlvl;
    private ServerShutdownThread _shutdownThread = null;

    private GameServer() {
        super("GameServer");
    }

    public static GameServer getInstance() {
        if (_instance == null) {
            _instance = new GameServer();
        }
        return _instance;
    }

    @Override
    public void run() {
        System.out.println("이용 메모리: " + SystemUtil.getUsedMemoryMB() + "MB");
        System.out.println("클라이언트 접속 대기중");
        System.out.println("──────────────");
        while (true) {
            try {
                Socket socket = _serverSocket.accept();
                System.out.println("접속 시행중 IP: " + socket.getInetAddress());
                String host = socket.getInetAddress().getHostAddress();
                if (IpTable.getInstance().isBannedIp(host)) {
                    MiniClient.getInstance().MessageToServer(host);
                    _log.info("banned IP(" + host + ")");
                }
                /*섭폭방지2*/
                else if (socket.getPort() == 0) {
                    System.out.println("섭폭의심 클라이언트 차단");
                    System.out.println("[접속정보]: " + socket.getInetAddress() + " :" + socket.getPort() + " [" + host + "]");
                }
                /*섭폭방지2*/
                else {
                    ClientThread client = new ClientThread(socket);
                    GeneralThreadPool.getInstance().execute(client);
                }
            } catch (IOException ioexception) {
            }
        }
    }

    public void initialize() throws Exception {
        String s = Config.GAME_SERVER_HOST_NAME;
        double rateXp = Config.RATE_XP;
        double LA = Config.RATE_LA;
        double rateKarma = Config.RATE_KARMA;
        double rateDropItems = Config.RATE_DROP_ITEMS;
        double rateDropAdena = Config.RATE_DROP_ADENA;

        chatlvl = Config.GLOBAL_CHAT_LEVEL;
        _port = Config.GAME_SERVER_PORT;
        if (!"*".equals(s)) {
            InetAddress inetaddress = InetAddress.getByName(s);

            inetaddress.getHostAddress();
            _serverSocket = new ServerSocket(_port, 50, inetaddress);
            System.out.println("서버 세팅: 서버 소켓 생성");
        } else {
            _serverSocket = new ServerSocket(_port);
            System.out.println("서버 세팅: 서버 소켓 생성");
        }

        System.out.println(
                "EXP:" + (rateXp) + "배  Lawful:" + (LA) + "배업:" + (rateKarma)
                        + "배드롭율:" + (rateDropItems) + "배취득 아데나:"
                        + (rateDropAdena) + "배");
        System.out.println("전체 채팅 가능 Lv " + (chatlvl));
        if (Config.ALT_NONPVP) { // Non-PvP 설정
            System.out.println("Non-PvP 설정: 무효(PvP 가능)");
        } else {
            System.out.println("Non-PvP 설정: 유효(PvP 불가)");
        }

        System.out.println("=================================================");
        System.out.println("                                    Server System");
        System.out.println("=================================================");

        int maxOnlineUsers = Config.MAX_ONLINE_USERS;

        System.out.println("접속 인원수 제한： 최대" + (maxOnlineUsers) + "인");
        IdFactory.getInstance();
        L1WorldMap.getInstance();
        _loginController = LoginController.getInstance();
        _loginController.setMaxAllowedOnlinePlayers(maxOnlineUsers);


        // 전캐릭터 네임 로드
        CharacterTable.getInstance().loadAllCharName();

        // 온라인 상태 리셋트
        CharacterTable.clearOnlineStatus();
        //펫레이싱
        L1Racing.getInstance().start();
        // 게임 시간 시계
        L1GameTimeClock.init();

        // 버경관련.
        GameServerSetting gameServerSetting = GameServerSetting.getInstance();
        GeneralThreadPool.getInstance().execute(gameServerSetting);

        // 버경타임 콘트롤러
        BugRaceTimeControl bugRaceTimeControl = BugRaceTimeControl.getInstance();
        GeneralThreadPool.getInstance().execute(bugRaceTimeControl);

        // UB타임 콘트롤러
        UbTimeController ubTimeContoroller = UbTimeController.getInstance();
        L1Sys.getInstance();//카시 sys
        GeneralThreadPool.getInstance().execute(ubTimeContoroller);

        //카시 sys
        L1Sys l1Sys = L1Sys.getInstance();
        GeneralThreadPool.getInstance().execute(l1Sys);

        // 전쟁 타임 콘트롤러
        WarTimeController warTimeController = WarTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(warTimeController);

        // 프리미엄타임 콘트롤러
        PrimiumTimeController primiumTimeController = PrimiumTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(primiumTimeController);
        // 아인하사드  콘트롤러
        AinTimeController ainTimeController = AinTimeController.getInstance();
        GeneralThreadPool.getInstance().execute(ainTimeController);


        // 정령의 이슈우성
        if (Config.ELEMENTAL_STONE_AMOUNT > 0) {
            ElementalStoneGenerator elementalStoneGenerator
                    = ElementalStoneGenerator.getInstance();
            GeneralThreadPool.getInstance().execute(elementalStoneGenerator);
        }

        // 홈 타운
        HomeTownTimeController.getInstance();
        // 기란 타임 콘트롤러
        GiranController.getInstance().start();
        //시간의균열
        CrockController.getInstance().start();
        //펫레이싱
        //	L1PetRace.getInstance().start();
        // 아지트 경매 타임 콘트롤러
        AuctionTimeController auctionTimeController = AuctionTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(auctionTimeController);

        // 아지트 세금 타임 콘트롤러
        HouseTaxTimeController houseTaxTimeController = HouseTaxTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(houseTaxTimeController);

        // 낚시 타임 콘트롤러
        FishingTimeController fishingTimeController = FishingTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(fishingTimeController);

        // NPC 채팅 타임 콘트롤러
        NpcChatTimeController npcChatTimeController = NpcChatTimeController
                .getInstance();
        GeneralThreadPool.getInstance().execute(npcChatTimeController);

        Announcements.getInstance();
        NpcTable.getInstance();
        L1DeleteItemOnGround deleteitem = new L1DeleteItemOnGround();
        deleteitem.initialize();

        if (!NpcTable.getInstance().isInitialized()) {
            throw new Exception("Could not initialize the npc table");
        }
        SpawnTable.getInstance();
        MobGroupTable.getInstance();
        SkillsTable.getInstance();
        PolyTable.getInstance();
        ItemTable.getInstance();
        DropTable.getInstance();
        DropItemTable.getInstance();
        ShopTable.getInstance();
        NPCTalkDataTable.getInstance();
        L1World.getInstance();
        L1WorldTraps.getInstance();
        Dungeon.getInstance();
        NpcSpawnTable.getInstance();
        IpTable.getInstance();
        MapsTable.getInstance();
        UBSpawnTable.getInstance();
        PetTable.getInstance();
        ClanTable.getInstance();
        CastleTable.getInstance();
        L1CastleLocation.setCastleTaxRate(); // 이것은 CastleTable 초기화 다음이 아니면 안 된다
        GetBackRestartTable.getInstance();
        DoorSpawnTable.getInstance();
        GeneralThreadPool.getInstance();
        L1NpcRegenerationTimer.getInstance();
        ChatLogTable.getInstance();
        WeaponSkillTable.getInstance();
        NpcActionTable.load();
        GMCommandsConfig.load();
        Getback.loadGetBack();
        PetTypeTable.load();
        L1BossCycle.load();
        RaceTable.getInstance();
        L1TreasureBox.load();
        SprTable.getInstance();
        ResolventTable.getInstance();
        FurnitureSpawnTable.getInstance();
        NpcChatTable.getInstance();
        LightSpawnTable.getInstance();
        L1Cube.getInstance();    // 큐브
        TimeMapController.getInstance().start();
        MessageController.getInstance().start();

        System.out.println("로딩 완료!");
        Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
        System.gc();
        System.runFinalization();

        this.start();
    }

    /**
     * 온라인중의 플레이어 모두에 대해서 kick, 캐릭터 정보의 보존을 한다.
     */
    public void disconnectAllCharacters() {
        Collection<L1PcInstance> players = L1World.getInstance()
                .getAllPlayers();
        for (L1PcInstance pc : players) {
            if (pc.getNetConnection() != null) {
                pc.getNetConnection().setActiveChar(null);
                pc.getNetConnection().kick();
            }
        }
        // 전원 Kick 한 후에 보존 처리를 한다
        for (L1PcInstance pc : players) {
            ClientThread.quitGame(pc);
            L1World.getInstance().removeObject(pc);
        }
    }

    public synchronized void shutdownWithCountdown(int secondsCount) {
        if (_shutdownThread != null) {
            // 이미 슛다운 요구를 하고 있다
            // TODO 에러 통지가 필요할지도 모른다
            return;
        }
        _shutdownThread = new ServerShutdownThread(secondsCount);
        GeneralThreadPool.getInstance().execute(_shutdownThread);
    }

    public void shutdown() {
        disconnectAllCharacters();
        System.exit(0);
    }

    public synchronized void abortShutdown() {
        if (_shutdownThread == null) {
            // 슛다운 요구를 하지 않았다
            // TODO 에러 통지가 필요할지도 모른다
            return;
        }

        _shutdownThread.interrupt();
        _shutdownThread = null;
    }

    private class ServerShutdownThread extends Thread {
        private final int _secondsCount;

        public ServerShutdownThread(int secondsCount) {
            _secondsCount = secondsCount;
        }

        @Override
        public void run() {
            L1World world = L1World.getInstance();
            try {
                int secondsCount = _secondsCount;
                world.broadcastServerMessage("서버 안정화/업데이트를 위한 리부팅을 실시합니다.");
                world.broadcastServerMessage("리부팅 중 게임정보의 소실을 방지하기 위하여 가급적");
                world.broadcastServerMessage("안전한 장소에서 게임을 종료 해 주십시오.");
                while (0 < secondsCount) {
                    if (secondsCount <= 30) {
                        world.broadcastServerMessage("게임이 " + secondsCount
                                + "초 후에 종료 됩니다. 게임을 중단해 주세요.");
                    } else {
                        if (secondsCount % 60 == 0) {
                            world.broadcastServerMessage("게임이 " + secondsCount
                                    / 60 + "분 후에 종료 됩니다.");
                        }
                    }
                    Thread.sleep(1000);
                    secondsCount--;
                }
                shutdown();
            } catch (InterruptedException e) {
                world.broadcastServerMessage("서버 종료가 중단되었습니다. 서버는 정상 가동중입니다.");
                return;
            }
        }
    }
}
