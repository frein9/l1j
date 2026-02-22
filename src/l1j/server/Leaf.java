package l1j.server;

import l1j.server.server.GameServer;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.SQLUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.logging.LogManager;

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.BRAVE_AURA;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.GLOWING_AURA;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.IRON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;

public class Leaf extends Frame implements ActionListener, ItemListener, MouseListener {
    public static boolean 가동 = false;

    public static Checkbox noticeCheckbox, 채팅파티, 외침채팅, 장사채팅, 파티채팅, 혈맹채팅, 글로벌채팅, 귓속말채팅, 일반채팅;

    Button 선물주기 = new Button("선물주기");
    Button 이동시키기 = new Button("이동시키기");
    Button 변신시키기 = new Button("변신시키기");
    Button allBuffButton = new Button("전체버프시전");
    Button allPresentButton1 = new Button("모든유저선물");
    Button allPresentButton2 = new Button("모든유저선물주기");
    Button 배율적용 = new Button("배율적용");
    Button 인첸적용 = new Button("인첸적용");
    Button 나머지적용 = new Button("나머지적용");

    //account make
    public static Button 생성 = new Button("계정생성");
    public static Label 아이디 = new Label("　아이디");
    public static Label 비밀번호 = new Label("비밀번호");
    public static Label 계정레벨 = new Label("계정레벨");
    public static TextField 아이디2 = new TextField("", 10);
    public static TextField 비밀번호2 = new TextField("", 10);
    public static TextField 계정레벨2 = new TextField("", 10);

    public static Button 삭제 = new Button("계정삭제");
    public static Label 삭제아이디 = new Label("    아이디");
    public static TextField 삭제아이디2 = new TextField("", 10);

    static MemoryMonitor memorymonitor;

    public static List list = null;

    public static JFrame 선물주기JFrame, 이동시키기Frame, 변신시키기Frame, allPresentJFrame, 배율설정JFrame,
            인첸설정JFrame, 나머지설정JFrame;
    public static JFrame 계정생성2 = new JFrame("계정 생성");
    public static JFrame 계정삭제2 = new JFrame("계정 삭제");
    public static JFrame 패킷출력JFrame = new JFrame("패킷출력창");

    public static Menu serverMenu, serverMenuSub, 도구, 도구Sub;

    public static MenuItem serverStart, serverDown, userNick, 배율설정, 인첸설정, 나머지설정, 컴퓨터끄기,
            끄기, 다시시작, 달빛프로그램, 나비켓실행, 계정생성하기, 계정삭제하기;
    public static MenuItem 캐릭터삭제 = new MenuItem("캐릭터삭제");
    public static MenuItem 패킷출력 = new MenuItem("패킷출력");

    public static TextArea chatlog, tarea;
    public static TextArea 패킷출력창 = new TextArea("", 0, 0, 1);

    public static TextField chat, 닉네임, 선물주기닉네임, 변신닉네임, 이동닉네임,
            아이템번호, 인첸트레벨, 아이템갯수, X좌표, Y좌표, 맵번호,
            몬스터번호, 경험치, 아이템, 아데나, 라우풀, 펫경험치,
            무기인첸, 아머인첸, 악세속성인첸, 채창레벨, 무게설정, 지급시간, 지급갯수;

    // 아이콘
    Image im = Toolkit.getDefaultToolkit().getImage("Leaf/images/LinFree_Leaf.gif");

    // 날짜와 시간
    Calendar now = Calendar.getInstance();
    int year = now.get(Calendar.YEAR);
    int month = now.get(Calendar.MONTH) + 1;
    int day = now.get(Calendar.DATE);
    int hour = now.get(Calendar.HOUR_OF_DAY);
    int min = now.get(Calendar.MINUTE);
    String date = "." + year + "." + month + "." + day + "-" + hour + min;

    public Leaf(String title) {
        super(title);

        //메모리 모니터링
        memorymonitor = new MemoryMonitor();
        memorymonitor.setVisible(true);
        memorymonitor.surf.setVisible(true);
        memorymonitor.surf.start();

        // 리스트 등록
        list = new List();

        // 리스트 설정
        list.setBounds(635, 80, 150, 300);

        // 라벨 생성
        Label chatLogLabel = new Label("채팅로그");
        Label listLabel = new Label("접속자");
        Label tareaLogLabel = new Label("매니저로그");
        Label userChatLabel = new Label("메세지전송");
        Label serviceLabel = new Label("서비스");

        // 라벨 설정
        chatLogLabel.setBounds(15, 60, 80, 20);
        listLabel.setBounds(635, 60, 60, 20);
        tareaLogLabel.setBounds(15, 390, 100, 20);
        userChatLabel.setBounds(15, 518, 60, 20);
        serviceLabel.setBounds(635, 390, 90, 20);

        // Checkbox 생성
        noticeCheckbox = new Checkbox("공지");

        채팅파티 = new Checkbox("채팅파티", true);
        외침채팅 = new Checkbox("외침", true);
        장사채팅 = new Checkbox("장사", true);
        파티채팅 = new Checkbox("파티", true);
        혈맹채팅 = new Checkbox("혈맹", true);
        글로벌채팅 = new Checkbox("글로벌", true);
        귓속말채팅 = new Checkbox("귓속말", true);
        일반채팅 = new Checkbox("일반", true);

        // Checkbox 설정
        noticeCheckbox.setBounds(582, 518, 45, 20);

        // TextArea 생성
        chatlog = new TextArea("", 0, 0, 1);
        tarea = new TextArea("Suny Server starting..!", 0, 0, 1);

        // TextArea 설정
        chatlog.setBounds(15, 80, 612, 300);
        tarea.setBounds(15, 410, 612, 95);

        // TextField 생성
        chat = new TextField("");

        // TextField 설정
        chat.setBounds(85, 518, 488, 20);

        // 매니저 메뉴
        serverMenu = new Menu("서버메뉴");
        serverMenuSub = new Menu();
        도구 = new Menu("도구");
        도구Sub = new Menu();

        // 매니저 메뉴아이템
        serverStart = new MenuItem("서버시작");
        serverDown = new MenuItem("서버종료");
        배율설정 = new MenuItem("배율설정");
        인첸설정 = new MenuItem("인첸설정");
        끄기 = new MenuItem("끄기");
        다시시작 = new MenuItem("다시시작");
        serverMenuSub.setLabel("컴퓨터끄기");
        도구Sub.setLabel("서버설정");
        나머지설정 = new MenuItem("나머지설정");
        달빛프로그램 = new MenuItem("달빛프로그램");
        나비켓실행 = new MenuItem("나비켓실행");
        계정생성하기 = new MenuItem("계정생성하기");
        계정삭제하기 = new MenuItem("계정삭제하기");

        // 매니저 메뉴에 추가하기
        // 자동시작되므로 서버시작은 필요없다 주석처리
//  serverMenu.add(serverStart);
        serverMenu.add(serverDown);
        serverMenu.addSeparator();
        serverMenu.add(serverMenuSub);
        serverMenuSub.add(끄기);
        serverMenuSub.add(다시시작);

        도구.add(계정생성하기);
        도구.add(계정삭제하기);
        도구.addSeparator();
        도구.add(캐릭터삭제);
        도구.addSeparator();
        도구.add(도구Sub);
        도구Sub.add(배율설정);
        도구Sub.add(인첸설정);
        도구Sub.add(나머지설정);
        도구.addSeparator();
        도구.add(달빛프로그램);
        도구.add(나비켓실행);
        도구.addSeparator();
        도구.add(패킷출력);

        // 메뉴바
        MenuBar mb = new MenuBar();

        // Panel 생성
        Panel chatCheckboxPanel = new Panel();
        Panel servicePanel = new Panel();
        Panel memberMonitorPanel = new Panel();

        // Panel 설정
        chatCheckboxPanel.setBounds(91, 542, 500, 30);
        chatCheckboxPanel.setLayout(new FlowLayout(0));
        servicePanel.setBounds(635, 410, 150, 55);
        servicePanel.setLayout(new FlowLayout(0));
        memberMonitorPanel.setBounds(635, 475, 150, 120);

        //Panel에 추가하기
        chatCheckboxPanel.add(채팅파티);
        chatCheckboxPanel.add(외침채팅);
        chatCheckboxPanel.add(장사채팅);
        chatCheckboxPanel.add(파티채팅);
        chatCheckboxPanel.add(혈맹채팅);
        chatCheckboxPanel.add(글로벌채팅);
        chatCheckboxPanel.add(귓속말채팅);
        chatCheckboxPanel.add(일반채팅);
        memberMonitorPanel.add(memorymonitor);

        servicePanel.add(allBuffButton);
        servicePanel.add(allPresentButton1);

        // 메뉴바에 추가하기
        mb.add(serverMenu);
        mb.add(도구);

        // 메니저창에 추가하기
        this.setMenuBar(mb);
        this.add(chatLogLabel);
        this.add(listLabel);
        this.add(tareaLogLabel);
        this.add(serviceLabel);
        this.add(userChatLabel);
        this.add(list);
        this.add(chatlog);
        this.add(tarea);
        this.add(chat);
        this.add(noticeCheckbox);
        this.add(chatCheckboxPanel);
        this.add(servicePanel);
        this.add(memberMonitorPanel);

        // 계정생성에 추가
        계정생성2.add(아이디);
        계정생성2.add(아이디2);
        계정생성2.add(비밀번호);
        계정생성2.add(비밀번호2);
        계정생성2.add(계정레벨);
        계정생성2.add(계정레벨2);
        계정생성2.add(생성);

        // 계정생성에 추가
        계정삭제2.add(삭제아이디);
        계정삭제2.add(삭제아이디2);
        계정삭제2.add(삭제);

        // 패킷출력창에 추가
        패킷출력JFrame.add(패킷출력창);

        // 메니저창 설정
        this.setIconImage(im);
        this.setLayout(null);
        this.setLocation(127, 80);
        this.setSize(800, 600);
        this.setVisible(true);

        // 이벤트 리스너 등록
        serverStart.addActionListener(this);
        serverDown.addActionListener(this);
        끄기.addActionListener(this);
        다시시작.addActionListener(this);

        선물주기.addActionListener(this);
        이동시키기.addActionListener(this);
        변신시키기.addActionListener(this);
        allBuffButton.addActionListener(this);
        allPresentButton1.addActionListener(this);
        allPresentButton2.addActionListener(this);

        배율설정.addActionListener(this);
        배율적용.addActionListener(this);
        인첸설정.addActionListener(this);
        인첸적용.addActionListener(this);
        나머지설정.addActionListener(this);
        나머지적용.addActionListener(this);
        달빛프로그램.addActionListener(this);
        나비켓실행.addActionListener(this);
        생성.addActionListener(this);
        삭제.addActionListener(this);
        계정생성하기.addActionListener(this);
        계정삭제하기.addActionListener(this);
        도구.addActionListener(this);
        패킷출력.addActionListener(this);

        // 아이템 리스너 등록
        noticeCheckbox.addItemListener(this);

        채팅파티.addItemListener(this);
        외침채팅.addItemListener(this);
        장사채팅.addItemListener(this);
        파티채팅.addItemListener(this);
        혈맹채팅.addItemListener(this);
        글로벌채팅.addItemListener(this);
        귓속말채팅.addItemListener(this);
        일반채팅.addItemListener(this);

        // 키이벤트 리스너 등록
        EnterKey enterKey = new EnterKey();
        chat.addKeyListener(enterKey);

        // 마우스이벤트 리스너 등록
        list.addMouseListener(this);

        // 시작시 채팅창 모두보기
        Config.일반 = true;//일반
        Config.귓속말 = true;//귓속말
        Config.글로벌 = true;//글로벌
        Config.혈맹 = true;//혈맹
        Config.파티 = true;//파티
        Config.장사 = true;//장사
        Config.외침 = true;//외침
        Config.채팅파티 = true;//채팅파티

        // 서버 자동시작
        serverStart();
        // 채팅 텍스트필드로 포커스 이동
        chat.requestFocus();
    }

    // 사용자 메소드 시작
    public void serverDownCount(int sec) {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            pc.sendPackets(new S_SystemMessage("\\fY알림 : 서버 종료 카운트 시작..안전한 종료"));
        }

        for (int is = sec; is > 0; is--) {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                pc.sendPackets(new S_SystemMessage("\\fY알림 : 서버 종료 " + is + "초 전"));
            }

            tarea.append("\n───────────── 종료 " + is + "초 전");

            try {
                int i = 1000;
                Thread.sleep(i);
            } catch (InterruptedException interruptedexception) {
            }
        }

        serverDown();
    }

    public void serverStart() {
        if (가동 == false) {
            가동 = true;
            tarea.setText("※서버가 가동 되었습니다.※");

            File logFolder = new File("Leaf_Log");
            logFolder.mkdir();

            InputStream is = new Server().getClass().getResourceAsStream("/config/log.properties");
            try {
                LogManager.getLogManager().readConfiguration(is);

            } catch (SecurityException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Config.load();
                //int maxConnections = Math.min(Math.max(100, Config.DB_MAX_CONNECTIONS),	1000);
                L1DatabaseFactory.setDatabaseSettings(Config.DB_DRIVER, Config.DB_URL, Config.DB_LOGIN, Config.DB_PASSWORD);
                L1DatabaseFactory.getInstance();

            } catch (SQLException ex) {

            }

            try {
                GameServer.getInstance().initialize();
                tarea.setText("서버가 가동되었습니다. 클라이언트 접속 대기중.");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void serverDown() {
        if (가동 == true) {
            tarea.append("\n모든플레이어 연결을 종료하는 중..");
            disconnectAllCharacters();
            tarea.append("- 완료");
            tarea.append("\n수고하셨습니다 ^^");
            try {
                if (가동 == true) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter("Leaf_Log/Leaf.Log" + date + " (Server Log).txt"));
                    bw.write(tarea.getText() + "\r\n");
                    bw.close();

                    BufferedWriter bw2 = new BufferedWriter(new FileWriter("Leaf_Log/Leaf.Log" + date + " (Chating Log).txt"));
                    bw2.write("\r\n\r\n Server Manager - Chating Log \n\n" + chatlog.getText());
                    bw2.close();
                }
            } catch (IOException ie) {
            }

            try {
                int i = 3000;
                Thread.sleep(i);
            } catch (InterruptedException interruptedexception) {
            }
        }
        dispose();
        System.exit(0);
    }

    // 추방 시키기
    public void disconnectCharacters(String name) {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getName().equalsIgnoreCase(name)) {
                pc.sendPackets(new S_Disconnect());
                pc.sendPackets(new S_SystemMessage("\n" + pc.getName() + "님이 추방되었습니다."));
                tarea.append("\n" + pc.getName() + "님이 추방되었습니다");
            }
        }
    }

    // 캐릭터 접속 끊기
    public void disconnectAllCharacters() {
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            pc.sendPackets(new S_Disconnect());
        }
    }

    public void popupMenu(String title, int x, int y) {
        PopupMenu userPopMenu = new PopupMenu();
        userNick = new MenuItem(title);
        MenuItem 선물 = new MenuItem("선물");
        MenuItem 이동 = new MenuItem("이동");
        MenuItem 변신 = new MenuItem("변신");
        MenuItem 영구추방 = new MenuItem("영구추방");

        userPopMenu.add(userNick);
        userPopMenu.addSeparator();
        userPopMenu.add(선물);
        userPopMenu.add(이동);
        userPopMenu.add(변신);
        userPopMenu.add(영구추방);

        list.add(userPopMenu);
        userPopMenu.show(list, x, y);

        선물.addActionListener(this);
        이동.addActionListener(this);
        변신.addActionListener(this);
        영구추방.addActionListener(this);
    }

    public void 선물주기프레임(String nickname) {
        선물주기닉네임 = new TextField(nickname, 14);
        아이템번호 = new TextField("", 14);
        인첸트레벨 = new TextField("", 14);
        아이템갯수 = new TextField("", 14);

        선물주기JFrame = new JFrame();
        선물주기JFrame.add(new Label("    캐릭터명"));
        선물주기JFrame.add(선물주기닉네임);
        선물주기JFrame.add(new Label("아이템번호"));
        선물주기JFrame.add(아이템번호);
        선물주기JFrame.add(new Label("인첸트레벨"));
        선물주기JFrame.add(인첸트레벨);
        선물주기JFrame.add(new Label("아이템갯수"));
        선물주기JFrame.add(아이템갯수);
        선물주기JFrame.add(선물주기);
        선물주기JFrame.setTitle(선물주기닉네임.getText() + "에게 선물주기");
        선물주기JFrame.setIconImage(im);
        선물주기JFrame.setLayout(new FlowLayout(1));
        선물주기JFrame.setSize(250, 180);
        선물주기JFrame.setLocation(200, 175);
        선물주기JFrame.setResizable(false);
        선물주기JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        선물주기JFrame.setVisible(true);
    }

    public void 선물전달() {
        String name2 = 선물주기닉네임.getText();

        int itemid = Integer.parseInt(아이템번호.getText());
        int enchant = Integer.parseInt(인첸트레벨.getText());
        int count = Integer.parseInt(아이템갯수.getText());

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getName().equalsIgnoreCase(name2)) {
                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp != null) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                    item.setEnchantLevel(enchant);
                    item.setCount(count);

                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        pc.getInventory().storeItem(item);
                        pc.sendPackets(new S_SystemMessage("\n운영자가 " + temp.getName() + "을(를) 생성해 주었습니다."));
                        tarea.append("\n운영자가 " + temp.getName() + "을(를) 생성해 주었습니다.");
                    } else {
                        L1ItemInstance item1 = null;
                        int createCount;
                        for (createCount = 0; createCount < count; createCount++) {
                            item1 = ItemTable.getInstance().createItem(itemid);
                            item1.setEnchantLevel(enchant);
                            if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                                pc.getInventory().storeItem(item);
                            } else break;
                        }

                        if (createCount > 0) {
                            pc.sendPackets(new S_SystemMessage(temp.getName() + "(을)를" + createCount + "개 생성했습니다."));
                            tarea.append(temp.getName() + "(을)를" + createCount + "개 생성했습니다.");
                        }
                    }
                } else if (temp == null) {
                    tarea.append("\n[메시지] 그런 아이템은 존재하지 않습니다.");
                }
            }
        }
    }

    public void 이동시키기프레임(String nickname) {
        이동닉네임 = new TextField(nickname, 16);
        X좌표 = new TextField("", 16);
        Y좌표 = new TextField("", 16);
        맵번호 = new TextField("", 16);

        이동시키기Frame = new JFrame();
        이동시키기Frame.add(new Label("캐릭터명"));
        이동시키기Frame.add(이동닉네임);
        이동시키기Frame.add(new Label("     X좌표"));
        이동시키기Frame.add(X좌표);
        이동시키기Frame.add(new Label("     Y좌표"));
        이동시키기Frame.add(Y좌표);
        이동시키기Frame.add(new Label("   맵번호"));
        이동시키기Frame.add(맵번호);
        이동시키기Frame.add(이동시키기);

        이동시키기Frame.setLayout(new FlowLayout(1));
        이동시키기Frame.setTitle(이동닉네임.getText() + "를 이동");
        이동시키기Frame.setSize(250, 180);
        이동시키기Frame.setIconImage(im);
        이동시키기Frame.setLocation(200, 175);
        이동시키기Frame.setResizable(false);
        이동시키기Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        이동시키기Frame.setVisible(true);
    }

    public void 이동전달() {
        String kname = 이동닉네임.getText();

        int xtt = Integer.parseInt(X좌표.getText());
        int ytt = Integer.parseInt(Y좌표.getText());
        short map = Short.parseShort(맵번호.getText());

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getName().equalsIgnoreCase(kname)) {
                L1Teleport.teleport(pc, xtt, ytt, map, 5, false);
                tarea.append("\n " + kname + " 케릭을 이동시켰습니다.");
            } else {
                tarea.append("\n월드내에 " + kname + " 라는케릭명은 존재하지않습니다.");
            }
        }
    }

    public void 변신시키기프레임(String nickname) {
        변신닉네임 = new TextField(nickname, 16);
        몬스터번호 = new TextField("", 16);

        변신시키기Frame = new JFrame();
        변신시키기Frame.add(new Label("     캐릭터명"));
        변신시키기Frame.add(변신닉네임);
        변신시키기Frame.add(new Label(" 몬스터번호"));
        변신시키기Frame.add(몬스터번호);
        변신시키기Frame.add(변신시키기);

        변신시키기Frame.setLayout(new FlowLayout(1));
        변신시키기Frame.setTitle(변신닉네임.getText() + "을 변신");
        변신시키기Frame.setSize(250, 120);
        변신시키기Frame.setIconImage(im);
        변신시키기Frame.setLocation(200, 175);
        변신시키기Frame.setResizable(false);
        변신시키기Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        변신시키기Frame.setVisible(true);
    }

    public void 변신전달() {
        String kname1 = 변신닉네임.getText();

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            if (pc.getName().equalsIgnoreCase(kname1)) {
                int polyid = Integer.parseInt(몬스터번호.getText());
                pc.setTempCharGfx(polyid);
                pc.sendPackets(new S_ChangeShape(pc.getId(), polyid));
                pc.broadcastPacket(new S_ChangeShape(pc.getId(), polyid));
                pc.getInventory().takeoffEquip(polyid);
                tarea.append("\n[메시지] " + kname1 + " 캐릭터를 변신 시켰습니다,");
            } else if (!pc.getName().equalsIgnoreCase(kname1)) {
                tarea.append("\n[메시지] 월드내에 " + kname1 + " 라는 캐릭터명은 없습니다.");
            }
        }
    }

    public void 영구추방시키기() {
        String kname2 = 닉네임.getText();

        try {
            L1PcInstance target = L1World.getInstance().getPlayer(kname2);

            IpTable iptable = IpTable.getInstance();
            if (target != null) {
                iptable.banIp(target.getNetConnection().getIp()); // BAN 리스트에 IP를 더한다
                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage((new StringBuilder()).append(target.getName()).append(" 님을 추방 했습니다.").toString()));
                target.sendPackets(new S_Disconnect());
                tarea.append("\n" + kname2 + "를 영구추방시켰습니다.");
            } else {
                tarea.append("그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다.");
            }
        } catch (Exception e) {
        }
    }

    public void allBuff() {
        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
                IMMUNE_TO_HARM, ADVANCE_SPIRIT, GLOWING_AURA, BRAVE_AURA,
                BURNING_WEAPON, IRON_SKIN, ELEMENTAL_FIRE,
                SOUL_OF_FLAME};

        try {
            //  if(가동 == true){
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                L1SkillUse l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);

                    //        Thread.sleep(500);
                }
            }
            tarea.append("\n[메시지]월드내 모든 유저들에게 전체버프가 시전 되었습니다.");
        } catch (Exception exception19) {
            exception19.printStackTrace();
        }
    }

    public void allPresentJFrame() {
        allPresentJFrame = new JFrame("접속중인 유저에게 선물주기");

        아이템번호 = new TextField("", 14);
        인첸트레벨 = new TextField("", 14);
        아이템갯수 = new TextField("", 14);

        allPresentJFrame.add(new Label("아이템번호"));
        allPresentJFrame.add(아이템번호);
        allPresentJFrame.add(new Label("인첸트레벨"));
        allPresentJFrame.add(인첸트레벨);
        allPresentJFrame.add(new Label("아이템갯수"));
        allPresentJFrame.add(아이템갯수);
        allPresentJFrame.add(allPresentButton2);

        allPresentJFrame.setLayout(new FlowLayout(1));
        allPresentJFrame.setSize(250, 150);
        allPresentJFrame.setIconImage(im);
        allPresentJFrame.setLocation(200, 175);
        allPresentJFrame.setResizable(false);
        allPresentJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        allPresentJFrame.setVisible(true);
    }

    public void allPresent() {
        int itemid = Integer.parseInt(아이템번호.getText());
        int enchant = Integer.parseInt(인첸트레벨.getText());
        int count = Integer.parseInt(아이템갯수.getText());

        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
            L1Item temp = ItemTable.getInstance().getTemplate(itemid);
            if (temp != null) {
                L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
                item.setEnchantLevel(enchant);
                item.setCount(count);

                if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                    pc.getInventory().storeItem(item);
                    pc.sendPackets(new S_SystemMessage("\n운영자가 " + temp.getName() + "을(를) 생성해 주었습니다."));
                    tarea.append("\n운영자가 " + temp.getName() + "을(를) 생성해 주었습니다.");
                } else {
                    L1ItemInstance item1 = null;
                    int createCount;
                    for (createCount = 0; createCount < count; createCount++) {
                        item1 = ItemTable.getInstance().createItem(itemid);
                        item1.setEnchantLevel(enchant);
                        if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                            pc.getInventory().storeItem(item);
                        } else break;
                    }

                    if (createCount > 0) {
                        pc.sendPackets(new S_SystemMessage(temp.getName() + "(을)를" + createCount + "개 생성했습니다."));
                        tarea.append(temp.getName() + "(을)를" + createCount + "개 생성했습니다.");
                    }
                }
            } else if (temp == null) {
                tarea.append("\n[메시지] 그런 아이템은 존재하지 않습니다.");
            }
        }
    }

    public void 배율설정JFrame() {
        배율설정JFrame = new JFrame();

        경험치 = new TextField("" + Config.RATE_XP, 16);
        아이템 = new TextField("" + Config.RATE_DROP_ITEMS, 16);
        아데나 = new TextField("" + Config.RATE_DROP_ADENA, 16);
        라우풀 = new TextField("" + Config.RATE_LA, 16);
        펫경험치 = new TextField("" + Config.RATE_PET_XP, 16);

        배율설정JFrame.add(new Label("                최대값은 32767.0입니다."));
        배율설정JFrame.add(new Label("    경험치"));
        배율설정JFrame.add(경험치);
        배율설정JFrame.add(new Label("    아이템"));
        배율설정JFrame.add(아이템);
        배율설정JFrame.add(new Label("    아데나"));
        배율설정JFrame.add(아데나);
        배율설정JFrame.add(new Label("    라우풀"));
        배율설정JFrame.add(라우풀);
        배율설정JFrame.add(new Label("펫경험치"));
        배율설정JFrame.add(펫경험치);
        배율설정JFrame.add(배율적용);

        배율설정JFrame.setLayout(new FlowLayout(1));
        배율설정JFrame.setTitle("배율설정");
        배율설정JFrame.setSize(250, 240);
        배율설정JFrame.setIconImage(im);
        배율설정JFrame.setLocation(200, 175);
        배율설정JFrame.setResizable(false);
        배율설정JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        배율설정JFrame.setVisible(true);
    }

    public void 배율적용() {
        Float Exprate = Float.parseFloat(경험치.getText());
        double Exprate2 = (double) Exprate;
        Config.RATE_XP = Exprate2;

        Float Droprate = Float.parseFloat(아이템.getText());
        double Droprate2 = (double) Droprate;
        Config.RATE_DROP_ITEMS = Droprate2;

        Float Aden = Float.parseFloat(아데나.getText());
        double Aden2 = (double) Aden;
        Config.RATE_DROP_ADENA = Aden2;

        Float lauful = Float.parseFloat(라우풀.getText());
        double lauful2 = (double) lauful;
        Config.RATE_LA = lauful2;

        Float pet = Float.parseFloat(펫경험치.getText());
        double pet2 = (double) pet;
        Config.RATE_PET_XP = pet2;

        tarea.append("\n※ 아래와 같이 변경합니다.");
        tarea.append("\n[메시지] 서버세팅: 경험치 " + Exprate2 + "배");
        tarea.append("\n[메시지] 서버세팅: 아이템 " + Droprate2 + "배");
        tarea.append("\n[메시지] 서버세팅: 아데나 " + Aden2 + "배");
        tarea.append("\n[메시지] 서버세팅: 라우풀 " + lauful2 + "배");
        tarea.append("\n[메시지] 서버세팅: 펫경험치 " + pet2 + "배");
    }

    public void 인첸설정JFrame() {
        인첸설정JFrame = new JFrame();

        무기인첸 = new TextField("" + Config.ENCHANT_CHANCE_WEAPON, 16);
        아머인첸 = new TextField("" + Config.ENCHANT_CHANCE_ARMOR, 16);
        악세속성인첸 = new TextField("" + Config.UPACSE_CHANCE, 16);

        인첸설정JFrame.add(new Label("무기아머: 32767.0 나머지: 127.0입니다."));
        인첸설정JFrame.add(new Label("        무기인첸"));
        인첸설정JFrame.add(무기인첸);
        인첸설정JFrame.add(new Label("        아머인첸"));
        인첸설정JFrame.add(아머인첸);
        인첸설정JFrame.add(new Label("악세속성인첸"));
        인첸설정JFrame.add(악세속성인첸);
        인첸설정JFrame.add(인첸적용);

        인첸설정JFrame.setLayout(new FlowLayout(1));
        인첸설정JFrame.setTitle("인첸설정");
        인첸설정JFrame.setSize(250, 290);
        인첸설정JFrame.setIconImage(im);
        인첸설정JFrame.setLocation(200, 175);
        인첸설정JFrame.setResizable(false);
        인첸설정JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        인첸설정JFrame.setVisible(true);
    }

    public void 인첸적용() {
        int enchant = Integer.parseInt(무기인첸.getText());
        Config.ENCHANT_CHANCE_WEAPON = enchant;

        int armor = Integer.parseInt(아머인첸.getText());
        Config.ENCHANT_CHANCE_ARMOR = armor;

        int upacse = Integer.parseInt(악세속성인첸.getText());
        Config.UPACSE_CHANCE = upacse;

        tarea.append("\n※ 아래와 같이 변경합니다.");
        tarea.append("\n[메시지] 서버세팅: 무기인첸 " + enchant + "배");
        tarea.append("\n[메시지] 서버세팅: 아머인첸 " + armor + "배");
        tarea.append("\n[메시지] 서버세팅: 악세속성인첸 " + upacse + "배");
    }

    public void 나머지설정JFrame() {
        나머지설정JFrame = new JFrame();

        채창레벨 = new TextField("" + Config.GLOBAL_CHAT_LEVEL, 16);
        무게설정 = new TextField("" + Config.RATE_WEIGHT_LIMIT, 16);
        지급시간 = new TextField("" + Config.RATE_PRIMIUM_TIME, 16);
        지급갯수 = new TextField("" + Config.RATE_PRIMIUM_NUMBER, 16);

        나머지설정JFrame.add(new Label("        채창레벨"));
        나머지설정JFrame.add(채창레벨);
        나머지설정JFrame.add(new Label("        무게설정"));
        나머지설정JFrame.add(무게설정);
        나머지설정JFrame.add(new Label("깃털지급시간"));
        나머지설정JFrame.add(지급시간);
        나머지설정JFrame.add(new Label("깃털지급갯수"));
        나머지설정JFrame.add(지급갯수);
        나머지설정JFrame.add(나머지적용);

        나머지설정JFrame.setLayout(new FlowLayout(1));
        나머지설정JFrame.setTitle("나머지설정");
        나머지설정JFrame.setSize(250, 190);
        나머지설정JFrame.setIconImage(im);
        나머지설정JFrame.setLocation(200, 175);
        나머지설정JFrame.setResizable(false);
        나머지설정JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        나머지설정JFrame.setVisible(true);
    }

    public void 나머지적용() {
        int chatlvl = Integer.parseInt(채창레벨.getText());
        short chatlvl2 = (short) chatlvl;
        Config.GLOBAL_CHAT_LEVEL = chatlvl2;

        Float weight = Float.parseFloat(무게설정.getText());
        double weight2 = (double) weight;
        Config.RATE_WEIGHT_LIMIT = weight2;

        int wingtime = Integer.parseInt(지급시간.getText());
        Config.RATE_PRIMIUM_TIME = wingtime;

        int wingnumber = Integer.parseInt(지급갯수.getText());
        Config.RATE_PRIMIUM_NUMBER = wingnumber;

        tarea.append("\n※ 아래와 같이 변경합니다.");
        tarea.append("\n[메시지] 서버세팅: 채창레벨 " + chatlvl2 + "레벨");
        tarea.append("\n[메시지] 서버세팅: 지급시간 " + weight);
        tarea.append("\n[메시지] 서버세팅: 지급시간 " + wingtime + "분");
        tarea.append("\n[메시지] 서버세팅: 지급갯수 " + wingnumber + "개");
    }

    public void shutdownS() {
        try {
            Runtime.getRuntime().exec("C:\\windows\\system32\\shutdown.exe -s -f -t 20 -c \"컴퓨터를 끄기 시작합니다.\n좋은 하루 되세요\"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        serverDownCount(10);
    }

    public void shutdownR() {
        try {
            Runtime.getRuntime().exec("C:\\windows\\system32\\shutdown.exe -r -f -t 20 -c \"컴퓨터를 다시 시작합니다.\n좋은 하루 되세요\"");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        serverDownCount(10);
    }

    private static String encodePassword2(String rawPassword) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte buf[] = rawPassword.getBytes("UTF-8");
        buf = MessageDigest.getInstance("SHA").digest(buf);

        return Base64.encodeBytes(buf);
    }
    // 사용자 메소드 끝

    // 각 메뉴이벤트 시작
    public void actionPerformed(ActionEvent e) {
        String menu = e.getActionCommand();

        // 서버 메뉴
        if (menu == "서버시작") serverStart();
        else if (menu == "서버종료") {
            int chk = JOptionPane.showConfirmDialog(null, "정말 서버를 종료하시겠습니까?", "서버를 종료합니다.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (chk == 0) serverDownCount(10);
        } else if (menu == "끄기") shutdownS();
        else if (menu == "다시시작") shutdownR();

        //도구 메뉴
        if (menu == "배율설정") 배율설정JFrame();
        else if (menu == "배율적용") 배율적용();
        else if (menu == "인첸설정") 인첸설정JFrame();
        else if (menu == "인첸적용") 인첸적용();
        else if (menu == "나머지설정") 나머지설정JFrame();
        else if (menu == "나머지적용") 나머지적용();
        else if (menu == "달빛프로그램") {
            try {
                Runtime.getRuntime().exec("./data/달빛관리프로그램/달빛관리프로그램.exe");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (menu == "나비켓실행") {
            try {
                Runtime.getRuntime().exec("C:/Program Files/PremiumSoft/Navicat 8.0 MySQL/navicat.exe");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (menu == "계정생성하기") {
            계정생성2.setLayout(new FlowLayout());
            계정생성2.setSize(200, 155);
            계정생성2.setLocation(200, 175);
            계정생성2.setResizable(false);
            계정생성2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            계정생성2.setVisible(true);
        } else if (menu == "계정생성") {
            Connection con = null;
            PreparedStatement pstm = null;

            try {
                //Lead account =  new Account();
                String _name = 아이디2.getText();
                String _password = encodePassword2(비밀번호2.getText());
                int _level = Integer.parseInt(계정레벨2.getText());//추가해야됨
                Timestamp lastactive = new Timestamp(System.currentTimeMillis());

                String _ip = "000.000.000.000";
                String _host = "000.000.000.000";
                int _banned = 0;


                con = L1DatabaseFactory.getInstance().getConnection();
                pstm = con.prepareStatement("INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?");
                pstm.setString(1, _name);
                pstm.setString(2, _password);
                pstm.setTimestamp(3, lastactive);
                pstm.setInt(4, _level);//레벨
                pstm.setString(5, _ip);
                pstm.setString(6, _host);
                pstm.setInt(7, _banned);
                pstm.execute();
                tarea.append("\n정상적으로 계정이 생성 되었습니다.");
            } catch (Exception e23) {
                tarea.append("\n계정 생성도중 알수없는 오류가 발생 했습니다.");
            }
        } else if (menu == "계정삭제하기") {
            계정삭제2.setLayout(new FlowLayout());
            계정삭제2.setSize(200, 100);
            계정삭제2.setLocation(200, 175);
            계정삭제2.setResizable(false);
            계정삭제2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            계정삭제2.setVisible(true);
        } else if (menu == "계정삭제") {
            JOptionPane.showMessageDialog(null, "서비스 준비중!");
        } else if (menu == "캐릭터삭제") {
            int objId = 0;
            String uAccuntName = "";
            ResultSet r = null;
            ResultSet rr = null;
            Connection c = null;
            PreparedStatement p = null;
            PreparedStatement pp = null;
            PreparedStatement ppp = null;
            PreparedStatement pppp = null;
            PreparedStatement warehouse = null;
            PreparedStatement teleport = null;
            PreparedStatement skills = null;
            PreparedStatement quests = null;
            PreparedStatement items = null;
            PreparedStatement elf_warehouse = null;
            PreparedStatement config = null;
            PreparedStatement buff = null;
            PreparedStatement buddys = null;

            try {
                c = L1DatabaseFactory.getInstance().getConnection();
                p = c.prepareStatement("select login as uID from accounts where date_add(lastactive, interval 7 day) <= curdate()");
                r = p.executeQuery();
                // 있다면
                while (r.next()) {
                    uAccuntName = r.getString(1);
                    // 오브젝트 아이디를 검색
                    pp = c.prepareStatement("select objid as oID from characters where account_name=?");
                    pp.setString(1, uAccuntName);
                    rr = pp.executeQuery();
                    // 보유한 캐릭 만큼 아이템, 창고 등 삭제
                    while (rr.next()) {
                        objId = rr.getInt(1);
                        // 창고
                        warehouse = c.prepareStatement("delete from character_warehouse where account_name=?");
                        warehouse.setString(1, uAccuntName);
                        warehouse.execute();
                        // 텔
                        teleport = c.prepareStatement("delete from character_teleport where char_id=?");
                        teleport.setInt(1, objId);
                        teleport.execute();
                        // 스킬
                        skills = c.prepareStatement("delete from character_skills where char_obj_id=?");
                        skills.setInt(1, objId);
                        skills.execute();
                        // 퀘스트
                        quests = c.prepareStatement("delete from character_quests where char_id=?");
                        quests.setInt(1, objId);
                        quests.execute();
                        //아이탬
                        items = c.prepareStatement("delete from character_items where char_id=?");
                        items.setInt(1, objId);
                        items.execute();
                        //요정 창고
                        elf_warehouse = c.prepareStatement("delete from character_elf_warehouse where account_name=?");
                        elf_warehouse.setString(1, uAccuntName);
                        elf_warehouse.execute();
                        //모름 -ㅅ-
                        config = c.prepareStatement("delete from character_config where object_id=?");
                        config.setInt(1, objId);
                        config.execute();
                        //버프
                        buff = c.prepareStatement("delete from character_buff where char_obj_id=?");
                        buff.setInt(1, objId);
                        buff.execute();
                        // 친구
                        buddys = c.prepareStatement("delete from character_buddys where char_id=?");
                        buddys.setInt(1, objId);
                        buddys.execute();

                        // 연결된 Statement 종료
                        SQLUtil.close(warehouse);
                        SQLUtil.close(teleport);
                        SQLUtil.close(skills);
                        SQLUtil.close(quests);
                        SQLUtil.close(items);
                        SQLUtil.close(elf_warehouse);
                        SQLUtil.close(config);
                        SQLUtil.close(buff);
                        SQLUtil.close(buddys);
                    }
                    // 오브젝트 아이디를 검색
                    ppp = c.prepareStatement("delete from characters where objid=?");
                    ppp.setInt(1, objId);
                    ppp.execute();
                    SQLUtil.close(ppp);
                    pppp = c.prepareStatement("delete from accounts where login=?");
                    pppp.setString(1, uAccuntName);
                    pppp.execute();
                    SQLUtil.close(pppp);
                }
            } catch (Exception e23) {
                tarea.append("캐릭삭제 에러 : ");
            } finally {
                SQLUtil.close(rr);
                SQLUtil.close(pp);
                SQLUtil.close(r);
                SQLUtil.close(p);
                SQLUtil.close(c);
                tarea.append("\nDB 안에 7일 미접속 케릭터를 삭제하였습니다.");
                javax.swing.JOptionPane.showMessageDialog(this, "DB 케릭삭제 완료", "Has Server", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (menu == "패킷출력") {
            패킷출력JFrame.setSize(280, 450);
            패킷출력JFrame.setIconImage(im);
            패킷출력JFrame.setLocation(520, 0);
            패킷출력JFrame.setResizable(false);
            패킷출력JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            패킷출력JFrame.setVisible(true);
        }

        // 서비스
        if (menu == "전체버프시전") allBuff();
        else if (menu == "모든유저선물") allPresentJFrame();
        else if (menu == "모든유저선물주기") allPresent();

        // 접속자 닉네임 팝업메뉴
        if (menu == "선물") 선물주기프레임(userNick.getLabel());
        else if (menu == "선물주기") 선물전달();
        else if (menu == "이동") 이동시키기프레임(userNick.getLabel());
        else if (menu == "이동시키기") 이동전달();
        else if (menu == "변신") 변신시키기프레임(userNick.getLabel());
        else if (menu == "변신시키기") 변신전달();
        else if (menu == "영구추방") 영구추방시키기();
    }
    // 각 메뉴이벤트 끝

    // 아이템 리스너 시작
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getStateChange() == 1 && ie.getSource() == noticeCheckbox) {
            int time = 1;

            tarea.append("\n───────────── 서버 공지중 ────────────────");
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                pc.sendPackets(new S_SystemMessage("\\fY알림 : 공지중에 채팅을 금지 합니다."));
                pc.setSkillEffect(1005, time * 60 * 1000);
                pc.sendPackets(new S_SkillIconGFX(36, time * 60));
            }
        } else if (ie.getStateChange() == 2 && ie.getSource() == noticeCheckbox) {
            tarea.append("\n───────────── 서버 공지끝 ────────────────");
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                pc.sendPackets(new S_SystemMessage("\\fY알림 : 즐거운 시간 보내세요"));
                pc.removeSkillEffect(1005);
            }
        }

        boolean a = 일반채팅.getState();
        boolean b = 귓속말채팅.getState();
        boolean c = 글로벌채팅.getState();
        boolean d = 혈맹채팅.getState();
        boolean e = 파티채팅.getState();
        boolean f = 장사채팅.getState();
        boolean g = 외침채팅.getState();
        boolean e2 = 채팅파티.getState();  // 채팅파티 추가 [힘요정] 5월 26일

        if (ie.getSource() == 일반채팅) {
            Config.일반 = true;
            if (a == false) {
                Config.일반 = false;
            }
        } else if (ie.getSource() == 귓속말채팅) {
            Config.귓속말 = true;
            if (b == false) {
                Config.귓속말 = false;
            }
        } else if (ie.getSource() == 글로벌채팅) {
            Config.글로벌 = true;
            if (c == false) {
                Config.글로벌 = false;
            }
        } else if (ie.getSource() == 혈맹채팅) {
            Config.혈맹 = true;
            if (d == false) {
                Config.혈맹 = false;
            }
        } else if (ie.getSource() == 파티채팅) {
            Config.파티 = true;
            if (e == false) {
                Config.파티 = false;
            }
        } else if (ie.getSource() == 장사채팅) {
            Config.장사 = true;
            if (f == false) {
                Config.장사 = false;
            }

        } else if (ie.getSource() == 외침채팅) {
            Config.외침 = true;
            if (g == false) {
                Config.외침 = false;
            }

        } else if (ie.getSource() == 채팅파티) {  // 채팅파티 추가 [힘요정] 5월 26일
            Config.채팅파티 = true;
            if (e2 == false) {
                Config.채팅파티 = false;
            }
        }
    }
    // 아이템 리스너 끝

    // 마우스 이벤트 시작
    public void mouseClicked(MouseEvent e) {
        int tmp_a = list.getSelectedIndex();
        if (e.getButton() == 3 && tmp_a != -1) {
            popupMenu(list.getItem(list.getSelectedIndex()), e.getX(), e.getY());
            닉네임 = new TextField(list.getItem(list.getSelectedIndex()));
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    // 마우스 이벤트 끝

    // Enter키 이벤트로 엔터가 눌리면 데이터를 전송할수 있게 한다.
    class EnterKey extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_ENTER) {  //엔터키가 눌리면
                for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                    pc.sendPackets(new S_SystemMessage("[******] " + chat.getText()));
                }
                chatlog.append("\r\n[******] " + chat.getText());
                chat.setText("");
            }
        }
    }

    public static void main(String args[]) {
        new Leaf("Server Manager");
    }
}