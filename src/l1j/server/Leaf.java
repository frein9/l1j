package l1j.server;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.*;
import java.util.Calendar;
import java.sql.*;
import javax.swing.*;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

import l1j.server.server.GameServer;
import l1j.server.server.model.*;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.Instance.*;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.templates.L1Item;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.IpTable;
import static l1j.server.server.model.skill.L1SkillId.*;

public class Leaf extends Frame implements ActionListener, ItemListener, MouseListener{
  public static boolean ���� = false;
  
  public static Checkbox noticeCheckbox, ä����Ƽ, ��ħä��, ���ä��, ��Ƽä��, ����ä��, �۷ι�ä��, �ӼӸ�ä��, �Ϲ�ä��;
  
  Button �����ֱ� = new Button("�����ֱ�");
  Button �̵���Ű�� = new Button("�̵���Ű��");
  Button ���Ž�Ű�� = new Button("���Ž�Ű��");
  Button allBuffButton = new Button("��ü��������");
  Button allPresentButton1 = new Button("�����������");
  Button allPresentButton2 = new Button("������������ֱ�");
  Button �������� = new Button("��������");
  Button ��þ���� = new Button("��þ����");
  Button ���������� = new Button("����������");
  
  //account make
    public static Button ���� = new Button("��������");
    public static Label ���̵� = new Label("�����̵�");
    public static Label ��й�ȣ = new Label("��й�ȣ");
    public static Label �������� = new Label("��������");
    public static TextField ���̵�2 = new TextField("", 10);
    public static TextField ��й�ȣ2 = new TextField("", 10);
    public static TextField ��������2 = new TextField("", 10);
    
    public static Button ���� = new Button("��������");
    public static Label �������̵� = new Label("    ���̵�");
    public static TextField �������̵�2 = new TextField("", 10);
  
 static MemoryMonitor memorymonitor;
  
  public static List list = null;
  
  public static JFrame �����ֱ�JFrame, �̵���Ű��Frame, ���Ž�Ű��Frame, allPresentJFrame, ��������JFrame,
                       ��þ����JFrame, ����������JFrame;
  public static JFrame ��������2 = new JFrame("���� ����");
  public static JFrame ��������2 = new JFrame("���� ����");
  public static JFrame ��Ŷ���JFrame = new JFrame("��Ŷ���â");
  
  public static Menu serverMenu, serverMenuSub, ����, ����Sub;
  
  public static MenuItem serverStart, serverDown, userNick, ��������, ��þ����, ����������, ��ǻ�Ͳ���,
                         ����, �ٽý���, �޺����α׷�, �����Ͻ���, ���������ϱ�, ���������ϱ�;
  public static MenuItem ĳ���ͻ��� = new MenuItem("ĳ���ͻ���");
  public static MenuItem ��Ŷ��� = new MenuItem("��Ŷ���");
  
  public static TextArea chatlog, tarea;
  public static TextArea ��Ŷ���â = new TextArea("", 0, 0, 1);
  
  public static TextField chat, �г���, �����ֱ�г���, ���Ŵг���, �̵��г���,
                          �����۹�ȣ, ��þƮ����, �����۰���, X��ǥ, Y��ǥ, �ʹ�ȣ,
                          ���͹�ȣ, ����ġ, ������, �Ƶ���, ���Ǯ, �����ġ,
                          ������þ, �Ƹ���þ, �Ǽ��Ӽ���þ, äâ����, ���Լ���, ���޽ð�, ���ް���;
  
  // ������
  Image im = Toolkit.getDefaultToolkit().getImage("Leaf/images/LinFree_Leaf.gif");
  
  // ��¥�� �ð�
  Calendar now = Calendar.getInstance();
  int year = now.get(Calendar.YEAR);
  int month =  now.get(Calendar.MONTH)+1;
  int day = now.get(Calendar.DATE);
  int hour = now.get(Calendar.HOUR_OF_DAY);
  int min = now.get(Calendar.MINUTE);
  String date = "."+year+"."+month+"."+day+"-"+hour+min;
    
  public Leaf(String title){
    super(title);
    
    //�޸� ����͸�
    memorymonitor = new MemoryMonitor();
    memorymonitor.setVisible(true);
    memorymonitor.surf.setVisible(true);
    memorymonitor.surf.start();  
    
    // ����Ʈ ���
    list = new List();
    
    // ����Ʈ ����
    list.setBounds(635, 80, 150, 300);
    
    // �� ����
    Label chatLogLabel = new Label("ä�÷α�");
    Label listLabel = new Label("������");
    Label tareaLogLabel = new Label("�Ŵ����α�");
    Label userChatLabel = new Label("�޼�������");
    Label serviceLabel = new Label("����");
    
    // �� ����
    chatLogLabel.setBounds(15, 60, 80, 20);
    listLabel.setBounds(635, 60, 60, 20);
    tareaLogLabel.setBounds(15, 390, 100, 20);
    userChatLabel.setBounds(15, 518, 60, 20);
    serviceLabel.setBounds(635, 390, 90, 20);
    
    // Checkbox ����
    noticeCheckbox = new Checkbox("����");
    
    ä����Ƽ = new Checkbox("ä����Ƽ",true);
    ��ħä�� = new Checkbox("��ħ",true);
    ���ä�� = new Checkbox("���",true);
    ��Ƽä�� = new Checkbox("��Ƽ",true);
    ����ä�� = new Checkbox("����",true);
    �۷ι�ä�� = new Checkbox("�۷ι�",true);
    �ӼӸ�ä�� = new Checkbox("�ӼӸ�",true);
    �Ϲ�ä�� = new Checkbox("�Ϲ�",true);
    
    // Checkbox ����
    noticeCheckbox.setBounds(582, 518, 45, 20);
    
    // TextArea ����
    chatlog = new TextArea("", 0, 0, 1);
    tarea = new TextArea("Suny Server starting..!", 0, 0, 1);
    
    // TextArea ����
    chatlog.setBounds(15, 80, 612, 300);
    tarea.setBounds(15, 410, 612, 95);
    
    // TextField ����
    chat = new TextField("");
    
    // TextField ����
    chat.setBounds(85, 518, 488, 20);
    
    // �Ŵ��� �޴�
    serverMenu = new Menu("�����޴�");
    serverMenuSub = new Menu();
    ���� = new Menu("����");
    ����Sub = new Menu();
    
    // �Ŵ��� �޴�������
    serverStart = new MenuItem("��������");
    serverDown = new MenuItem("��������");
    �������� = new MenuItem("��������");
    ��þ���� = new MenuItem("��þ����");
    ���� = new MenuItem("����");
    �ٽý��� = new MenuItem("�ٽý���");
    serverMenuSub.setLabel("��ǻ�Ͳ���");
    ����Sub.setLabel("��������");
    ���������� = new MenuItem("����������");
    �޺����α׷� = new MenuItem("�޺����α׷�");
    �����Ͻ��� = new MenuItem("�����Ͻ���");
    ���������ϱ� = new MenuItem("���������ϱ�");
    ���������ϱ� = new MenuItem("���������ϱ�");
    
    // �Ŵ��� �޴��� �߰��ϱ�
    // �ڵ����۵ǹǷ� ���������� �ʿ���� �ּ�ó��
//  serverMenu.add(serverStart);
    serverMenu.add(serverDown);
    serverMenu.addSeparator();
    serverMenu.add(serverMenuSub);
    serverMenuSub.add(����);
    serverMenuSub.add(�ٽý���);
    
    ����.add(���������ϱ�);
    ����.add(���������ϱ�);
    ����.addSeparator();
    ����.add(ĳ���ͻ���);
    ����.addSeparator();
    ����.add(����Sub);
    ����Sub.add(��������);
    ����Sub.add(��þ����);
    ����Sub.add(����������);
    ����.addSeparator();
    ����.add(�޺����α׷�);
    ����.add(�����Ͻ���);
    ����.addSeparator();
    ����.add(��Ŷ���);
    
    // �޴���
    MenuBar mb = new MenuBar();
    
    // Panel ����
    Panel chatCheckboxPanel = new Panel();
    Panel servicePanel = new Panel();
    Panel memberMonitorPanel = new Panel();
    
    // Panel ����
    chatCheckboxPanel.setBounds(91, 542, 500, 30);
    chatCheckboxPanel.setLayout(new FlowLayout(0));
    servicePanel.setBounds(635, 410, 150, 55);
    servicePanel.setLayout(new FlowLayout(0));
    memberMonitorPanel.setBounds(635, 475, 150, 120);
    
    //Panel�� �߰��ϱ�
    chatCheckboxPanel.add(ä����Ƽ);
    chatCheckboxPanel.add(��ħä��);
    chatCheckboxPanel.add(���ä��);
    chatCheckboxPanel.add(��Ƽä��);
    chatCheckboxPanel.add(����ä��);
    chatCheckboxPanel.add(�۷ι�ä��);
    chatCheckboxPanel.add(�ӼӸ�ä��);
    chatCheckboxPanel.add(�Ϲ�ä��);
    memberMonitorPanel.add(memorymonitor);
    
    servicePanel.add(allBuffButton);
    servicePanel.add(allPresentButton1);
    
    // �޴��ٿ� �߰��ϱ�
    mb.add(serverMenu);
    mb.add(����);
    
    // �޴���â�� �߰��ϱ�
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
    
    // ���������� �߰�
    ��������2.add(���̵�);
  	��������2.add(���̵�2);
  	��������2.add(��й�ȣ);
  	��������2.add(��й�ȣ2);
  	��������2.add(��������);
  	��������2.add(��������2);
  	��������2.add(����);
  	
  	// ���������� �߰�
    ��������2.add(�������̵�);
  	��������2.add(�������̵�2);
  	��������2.add(����);
  	
  	// ��Ŷ���â�� �߰�
  	��Ŷ���JFrame.add(��Ŷ���â);
    
    // �޴���â ����
    this.setIconImage(im); 
    this.setLayout(null);
    this.setLocation(127, 80);
    this.setSize(800, 600);
    this.setVisible(true);
    
    // �̺�Ʈ ������ ���
    serverStart.addActionListener(this);
    serverDown.addActionListener(this);
    ����.addActionListener(this);
    �ٽý���.addActionListener(this);
    
    �����ֱ�.addActionListener(this);
    �̵���Ű��.addActionListener(this);
    ���Ž�Ű��.addActionListener(this);
    allBuffButton.addActionListener(this);
    allPresentButton1.addActionListener(this);
    allPresentButton2.addActionListener(this);
    
    ��������.addActionListener(this);
    ��������.addActionListener(this);
    ��þ����.addActionListener(this);
    ��þ����.addActionListener(this);
    ����������.addActionListener(this);
    ����������.addActionListener(this);
    �޺����α׷�.addActionListener(this);
    �����Ͻ���.addActionListener(this);
    ����.addActionListener(this);
    ����.addActionListener(this);
    ���������ϱ�.addActionListener(this);
    ���������ϱ�.addActionListener(this);
    ����.addActionListener(this);
    ��Ŷ���.addActionListener(this);
    
    // ������ ������ ���
    noticeCheckbox.addItemListener(this);
    
    ä����Ƽ.addItemListener(this);
    ��ħä��.addItemListener(this);
    ���ä��.addItemListener(this);
    ��Ƽä��.addItemListener(this);
    ����ä��.addItemListener(this);
    �۷ι�ä��.addItemListener(this);
    �ӼӸ�ä��.addItemListener(this);
    �Ϲ�ä��.addItemListener(this);
    
    // Ű�̺�Ʈ ������ ���
    EnterKey enterKey = new EnterKey();
    chat.addKeyListener(enterKey);
    
    // ���콺�̺�Ʈ ������ ���
    list.addMouseListener(this);

    // ���۽� ä��â ��κ���
    Config.�Ϲ� = true;//�Ϲ�
    Config.�ӼӸ� = true;//�ӼӸ�
    Config.�۷ι� = true;//�۷ι�
    Config.���� = true;//����
    Config.��Ƽ = true;//��Ƽ
    Config.��� = true;//���
    Config.��ħ = true;//��ħ   
    Config.ä����Ƽ = true;//ä����Ƽ

    // ���� �ڵ�����
    serverStart();
    // ä�� �ؽ�Ʈ�ʵ�� ��Ŀ�� �̵�
    chat.requestFocus();
  }
  
  // ����� �޼ҵ� ����
  public void serverDownCount(int sec){
    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
      pc.sendPackets(new S_SystemMessage( "\\fY�˸� : ���� ���� ī��Ʈ ����..������ ����"));
    }
    
    for (int is = sec; is > 0; is--){
      for (L1PcInstance pc : L1World.getInstance().getAllPlayers()){
      pc.sendPackets(new S_SystemMessage( "\\fY�˸� : ���� ���� " + is + "�� ��"));
      }
    
      tarea.append("\n�������������������������� ���� " + is + "�� ��");
      
      try{
      int i = 1000;
      Thread.sleep(i);
      } catch (InterruptedException interruptedexception) { }
    }
    
    serverDown();
  }
  
  public void serverStart(){
    if(���� == false){
  		���� = true;
  		tarea.setText("�ؼ����� ���� �Ǿ����ϴ�.��");
      
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
  		tarea.setText("������ �����Ǿ����ϴ�. Ŭ���̾�Ʈ ���� �����.");
  		} catch (Exception ex) {
  			ex.printStackTrace();
  		}
  	}
  }
  
  public void serverDown(){
    if(���� == true){
			tarea.append("\n����÷��̾� ������ �����ϴ� ��..");
			disconnectAllCharacters();
			tarea.append("- �Ϸ�");
			tarea.append("\n�����ϼ̽��ϴ� ^^");
			try{
				if(���� == true){
					BufferedWriter bw = new BufferedWriter(new FileWriter("Leaf_Log/Leaf.Log"+date+" (Server Log).txt"));
					bw.write(tarea.getText()+"\r\n");
					bw.close();
    
					BufferedWriter bw2 = new BufferedWriter(new FileWriter("Leaf_Log/Leaf.Log"+date+" (Chating Log).txt"));
					bw2.write("\r\n\r\n Server Manager - Chating Log \n\n"+chatlog.getText());
					bw2.close();
				}
			}catch(IOException ie){}
			
			try{
				int i = 3000;
				Thread.sleep(i);
			}catch(InterruptedException interruptedexception) { }
		}
		dispose();
    System.exit(0);
  }
  
  // �߹� ��Ű��
  public void disconnectCharacters(String name){
    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
      if(pc.getName().equalsIgnoreCase(name)){
      pc.sendPackets(new S_Disconnect());
      pc.sendPackets(new S_SystemMessage("\n"+pc.getName()+"���� �߹�Ǿ����ϴ�."));
      tarea.append("\n"+pc.getName()+"���� �߹�Ǿ����ϴ�");
      }
    }
	}
  
  // ĳ���� ���� ����
  public void disconnectAllCharacters(){
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			pc.sendPackets(new S_Disconnect());
		}
	}
	
	public void popupMenu(String title, int x, int y){
	  PopupMenu userPopMenu = new PopupMenu();
	  userNick = new MenuItem(title);
	  MenuItem ���� = new MenuItem("����");
	  MenuItem �̵� = new MenuItem("�̵�");
	  MenuItem ���� = new MenuItem("����");
	  MenuItem �����߹� = new MenuItem("�����߹�");
	  
	  userPopMenu.add(userNick);
	  userPopMenu.addSeparator();
	  userPopMenu.add(����);
	  userPopMenu.add(�̵�);
	  userPopMenu.add(����);
	  userPopMenu.add(�����߹�);
	  
	  list.add(userPopMenu);
	  userPopMenu.show(list, x, y);
	  
	  ����.addActionListener(this);
	  �̵�.addActionListener(this);
	  ����.addActionListener(this);
	  �����߹�.addActionListener(this);
	}
	
	public void �����ֱ�������(String nickname){
      �����ֱ�г��� = new TextField(nickname, 14);
      �����۹�ȣ = new TextField("", 14);
      ��þƮ���� = new TextField("", 14);
      �����۰��� = new TextField("", 14);
      
      �����ֱ�JFrame = new JFrame();
      �����ֱ�JFrame.add(new Label("    ĳ���͸�"));
      �����ֱ�JFrame.add(�����ֱ�г���);
      �����ֱ�JFrame.add(new Label("�����۹�ȣ"));
      �����ֱ�JFrame.add(�����۹�ȣ);
      �����ֱ�JFrame.add(new Label("��þƮ����"));
      �����ֱ�JFrame.add(��þƮ����);
      �����ֱ�JFrame.add(new Label("�����۰���"));
      �����ֱ�JFrame.add(�����۰���);
      �����ֱ�JFrame.add(�����ֱ�);
      �����ֱ�JFrame.setTitle(�����ֱ�г���.getText() + "���� �����ֱ�");
      �����ֱ�JFrame.setIconImage(im);
      �����ֱ�JFrame.setLayout(new FlowLayout(1));
      �����ֱ�JFrame.setSize(250, 180);
      �����ֱ�JFrame.setLocation(200, 175);
      �����ֱ�JFrame.setResizable(false);
      �����ֱ�JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      �����ֱ�JFrame.setVisible(true);
	}
	
	public void ��������(){
	  String name2 = �����ֱ�г���.getText();
	  
	  int itemid = Integer.parseInt(�����۹�ȣ.getText());
		int enchant = Integer.parseInt(��þƮ����.getText());
		int count = Integer.parseInt(�����۰���.getText());
		
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		  if(pc.getName().equalsIgnoreCase(name2)){
		    L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		    if (temp != null) {
		      L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
  				item.setEnchantLevel(enchant);
  				item.setCount(count);
  				
  				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
  				  pc.getInventory().storeItem(item);
    				pc.sendPackets(new S_SystemMessage("\n��ڰ� "+temp.getName()+ "��(��) ������ �־����ϴ�."));
    				tarea.append("\n��ڰ� "+temp.getName()+ "��(��) ������ �־����ϴ�.");
  				}
  				else {
  				  L1ItemInstance item1 = null;
						int createCount;
						for (createCount = 0; createCount < count; createCount++) {
						  item1 = ItemTable.getInstance().createItem(itemid);
							item1.setEnchantLevel(enchant);
							if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
								pc.getInventory().storeItem(item);
							} 
							else break;
						}
						
						if (createCount > 0){
						  pc.sendPackets(new S_SystemMessage(temp.getName()+ "(��)��" + createCount + "�� �����߽��ϴ�."));
						  tarea.append(temp.getName()+ "(��)��" + createCount + "�� �����߽��ϴ�.");
						}
  				}
		    }
		    else if (temp == null){
					tarea.append("\n[�޽���] �׷� �������� �������� �ʽ��ϴ�.");
				}
		  }
		}
	}
	
	public void �̵���Ű��������(String nickname){
	  �̵��г��� = new TextField(nickname, 16);
	  X��ǥ = new TextField("", 16);
	  Y��ǥ = new TextField("", 16);
	  �ʹ�ȣ = new TextField("", 16);
	  
	  �̵���Ű��Frame = new JFrame();
	  �̵���Ű��Frame.add(new Label("ĳ���͸�"));
	  �̵���Ű��Frame.add(�̵��г���);
	  �̵���Ű��Frame.add(new Label("     X��ǥ"));
	  �̵���Ű��Frame.add(X��ǥ);
	  �̵���Ű��Frame.add(new Label("     Y��ǥ"));
	  �̵���Ű��Frame.add(Y��ǥ);
	  �̵���Ű��Frame.add(new Label("   �ʹ�ȣ"));
	  �̵���Ű��Frame.add(�ʹ�ȣ);
      �̵���Ű��Frame.add(�̵���Ű��);
	  
      �̵���Ű��Frame.setLayout(new FlowLayout(1));
      �̵���Ű��Frame.setTitle(�̵��г���.getText() + "�� �̵�");
      �̵���Ű��Frame.setSize(250, 180);
      �̵���Ű��Frame.setIconImage(im);
      �̵���Ű��Frame.setLocation(200, 175);
      �̵���Ű��Frame.setResizable(false);
      �̵���Ű��Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      �̵���Ű��Frame.setVisible(true);
	}
	
	public void �̵�����(){
	  String kname = �̵��г���.getText();
	  
    int xtt = Integer.parseInt(X��ǥ.getText());
    int ytt = Integer.parseInt(Y��ǥ.getText());
    short map = Short.parseShort(�ʹ�ȣ.getText());
    
    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
      if(pc.getName().equalsIgnoreCase(kname)){	
        L1Teleport.teleport(pc, xtt, ytt, map, 5, false);
        tarea.append("\n "+kname+" �ɸ��� �̵����׽��ϴ�.");
      }
      else {
        tarea.append("\n���峻�� "+kname+" ����ɸ����� ���������ʽ��ϴ�.");
      }
    }
	}
	
	public void ���Ž�Ű��������(String nickname){
	  ���Ŵг��� = new TextField(nickname, 16);
	  ���͹�ȣ = new TextField("", 16);
	  
	  ���Ž�Ű��Frame = new JFrame();
	  ���Ž�Ű��Frame.add(new Label("     ĳ���͸�"));
	  ���Ž�Ű��Frame.add(���Ŵг���);
	  ���Ž�Ű��Frame.add(new Label(" ���͹�ȣ"));
	  ���Ž�Ű��Frame.add(���͹�ȣ);
	  ���Ž�Ű��Frame.add(���Ž�Ű��);
	  
	  ���Ž�Ű��Frame.setLayout(new FlowLayout(1));
      ���Ž�Ű��Frame.setTitle(���Ŵг���.getText() + "�� ����");
      ���Ž�Ű��Frame.setSize(250, 120);
      ���Ž�Ű��Frame.setIconImage(im);
      ���Ž�Ű��Frame.setLocation(200, 175);
      ���Ž�Ű��Frame.setResizable(false);
      ���Ž�Ű��Frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      ���Ž�Ű��Frame.setVisible(true);
	}
	
	public void ��������(){
	  String kname1 = ���Ŵг���.getText();
	  
    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
      if(pc.getName().equalsIgnoreCase(kname1)){
        int polyid = Integer.parseInt(���͹�ȣ.getText());
        pc.setTempCharGfx(polyid);
        pc.sendPackets(new S_ChangeShape(pc.getId(), polyid));
        pc.broadcastPacket(new S_ChangeShape(pc.getId(), polyid));
        pc.getInventory().takeoffEquip(polyid);				
        tarea.append("\n[�޽���] "+kname1+" ĳ���͸� ���� ���׽��ϴ�,");
      }else if(!pc.getName().equalsIgnoreCase(kname1)){
        tarea.append("\n[�޽���] ���峻�� "+kname1+" ��� ĳ���͸��� �����ϴ�.");
      }
    }
	}
	
	public void �����߹��Ű��(){
	  String kname2 = �г���.getText();
	  
	  try {
			L1PcInstance target = L1World.getInstance().getPlayer(kname2);

			IpTable iptable = IpTable.getInstance();
			if (target != null) {
				iptable.banIp(target.getNetConnection().getIp()); // BAN ����Ʈ�� IP�� ���Ѵ�
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage((new StringBuilder()).append(target.getName()).append(" ���� �߹� �߽��ϴ�.").toString()));
				target.sendPackets(new S_Disconnect());
				tarea.append("\n" +kname2+"�� �����߹���׽��ϴ�.");
			} else {
				tarea.append("�׷��� �̸��� ĳ���ʹ� ���峻���� �������� �ʽ��ϴ�.");
			}
		} catch (Exception e) {	}
	}
	
	public void allBuff(){
		int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
				IMMUNE_TO_HARM, ADVANCE_SPIRIT, GLOWING_AURA, BRAVE_AURA,
				BURNING_WEAPON, IRON_SKIN, ELEMENTAL_FIRE,
				SOUL_OF_FLAME };
	  
	  try {
	//  if(���� == true){
      for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
        L1SkillUse l1skilluse = new L1SkillUse();
        for (int i = 0; i < allBuffSkill.length ; i++) {
          l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
          
  //        Thread.sleep(500);
        }
      }
      tarea.append("\n[�޽���]���峻 ��� �����鿡�� ��ü������ ���� �Ǿ����ϴ�.");
	  }
	  catch (Exception exception19) {
      exception19.printStackTrace();
		}
	}
	
	public void allPresentJFrame(){
	  allPresentJFrame = new JFrame("�������� �������� �����ֱ�");
	  
	  �����۹�ȣ = new TextField("", 14);
    ��þƮ���� = new TextField("", 14);
    �����۰��� = new TextField("", 14);
	  
	allPresentJFrame.add(new Label("�����۹�ȣ"));
    allPresentJFrame.add(�����۹�ȣ);
    allPresentJFrame.add(new Label("��þƮ����"));
    allPresentJFrame.add(��þƮ����);
    allPresentJFrame.add(new Label("�����۰���"));
    allPresentJFrame.add(�����۰���);
    allPresentJFrame.add(allPresentButton2);
	  
	allPresentJFrame.setLayout(new FlowLayout(1));
    allPresentJFrame.setSize(250, 150);
    allPresentJFrame.setIconImage(im);
    allPresentJFrame.setLocation(200, 175);
    allPresentJFrame.setResizable(false);
    allPresentJFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    allPresentJFrame.setVisible(true);
	}
	
	public void allPresent(){
	  int itemid = Integer.parseInt(�����۹�ȣ.getText());
		int enchant = Integer.parseInt(��þƮ����.getText());
		int count = Integer.parseInt(�����۰���.getText());
		
	  for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	    L1Item temp = ItemTable.getInstance().getTemplate(itemid);
		    if (temp != null) {
		      L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
  				item.setEnchantLevel(enchant);
  				item.setCount(count);
  				
  				if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
  				  pc.getInventory().storeItem(item);
    				pc.sendPackets(new S_SystemMessage("\n��ڰ� "+temp.getName()+ "��(��) ������ �־����ϴ�."));
    				tarea.append("\n��ڰ� "+temp.getName()+ "��(��) ������ �־����ϴ�.");
  				}
  				else {
  				  L1ItemInstance item1 = null;
						int createCount;
						for (createCount = 0; createCount < count; createCount++) {
						  item1 = ItemTable.getInstance().createItem(itemid);
							item1.setEnchantLevel(enchant);
							if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
								pc.getInventory().storeItem(item);
							} 
							else break;
						}
						
						if (createCount > 0){
						  pc.sendPackets(new S_SystemMessage(temp.getName()+ "(��)��" + createCount + "�� �����߽��ϴ�."));
						  tarea.append(temp.getName()+ "(��)��" + createCount + "�� �����߽��ϴ�.");
						}
  				}
		    }
		    else if (temp == null){
					tarea.append("\n[�޽���] �׷� �������� �������� �ʽ��ϴ�.");
				}
	  }
	}
	
	public void ��������JFrame(){
	  ��������JFrame = new JFrame();
	  
	  ����ġ = new TextField(""+Config.RATE_XP, 16);
	  ������ = new TextField(""+Config.RATE_DROP_ITEMS, 16);
	  �Ƶ��� = new TextField(""+Config.RATE_DROP_ADENA, 16);
	  ���Ǯ = new TextField(""+Config.RATE_LA, 16);
	  �����ġ = new TextField(""+Config.RATE_PET_XP, 16);
	  
	  ��������JFrame.add(new Label("                �ִ밪�� 32767.0�Դϴ�."));
	  ��������JFrame.add(new Label("    ����ġ"));
	  ��������JFrame.add(����ġ);
	  ��������JFrame.add(new Label("    ������"));
	  ��������JFrame.add(������);
	  ��������JFrame.add(new Label("    �Ƶ���"));
	  ��������JFrame.add(�Ƶ���);
	  ��������JFrame.add(new Label("    ���Ǯ"));
	  ��������JFrame.add(���Ǯ);
	  ��������JFrame.add(new Label("�����ġ"));
	  ��������JFrame.add(�����ġ);
	  ��������JFrame.add(��������);
	  
	  ��������JFrame.setLayout(new FlowLayout(1));
      ��������JFrame.setTitle("��������");
      ��������JFrame.setSize(250, 240);
      ��������JFrame.setIconImage(im);
      ��������JFrame.setLocation(200, 175);
      ��������JFrame.setResizable(false);
      ��������JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      ��������JFrame.setVisible(true);
	}
	
	public void ��������(){
    Float Exprate = Float.parseFloat(����ġ.getText());
    double Exprate2 = (double)Exprate;
    Config.RATE_XP = Exprate2;
    
    Float Droprate = Float.parseFloat(������.getText());
    double Droprate2 = (double)Droprate;
    Config.RATE_DROP_ITEMS = Droprate2;
    
    Float Aden = Float.parseFloat(�Ƶ���.getText());
    double Aden2 = (double)Aden;
    Config.RATE_DROP_ADENA = Aden2;
    
    Float lauful = Float.parseFloat(���Ǯ.getText());
    double lauful2 = (double)lauful;
    Config.RATE_LA = lauful2;
    
    Float pet = Float.parseFloat(�����ġ.getText());
    double pet2 = (double)pet;
   Config. RATE_PET_XP = pet2;
   
    tarea.append("\n�� �Ʒ��� ���� �����մϴ�.");
    tarea.append("\n[�޽���] ��������: ����ġ "+Exprate2+"��");
    tarea.append("\n[�޽���] ��������: ������ "+Droprate2+"��");
    tarea.append("\n[�޽���] ��������: �Ƶ��� "+Aden2+"��");
    tarea.append("\n[�޽���] ��������: ���Ǯ "+lauful2+"��");
    tarea.append("\n[�޽���] ��������: �����ġ "+pet2+"��");
	}
	
	public void ��þ����JFrame(){
	  ��þ����JFrame = new JFrame();
	  
	  ������þ = new TextField(""+Config.ENCHANT_CHANCE_WEAPON, 16);
	  �Ƹ���þ = new TextField(""+Config.ENCHANT_CHANCE_ARMOR, 16);
	  �Ǽ��Ӽ���þ = new TextField(""+Config.UPACSE_CHANCE, 16);
	  
	  ��þ����JFrame.add(new Label("����Ƹ�: 32767.0 ������: 127.0�Դϴ�."));
	  ��þ����JFrame.add(new Label("        ������þ"));
	  ��þ����JFrame.add(������þ);
	  ��þ����JFrame.add(new Label("        �Ƹ���þ"));
	  ��þ����JFrame.add(�Ƹ���þ);
	  ��þ����JFrame.add(new Label("�Ǽ��Ӽ���þ"));
	  ��þ����JFrame.add(�Ǽ��Ӽ���þ);
	  ��þ����JFrame.add(��þ����);
	  
	  ��þ����JFrame.setLayout(new FlowLayout(1));
      ��þ����JFrame.setTitle("��þ����");
      ��þ����JFrame.setSize(250, 290);
      ��þ����JFrame.setIconImage(im);
      ��þ����JFrame.setLocation(200, 175);
      ��þ����JFrame.setResizable(false);
      ��þ����JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      ��þ����JFrame.setVisible(true);
	}
	
	public void ��þ����(){
	  int enchant = Integer.parseInt(������þ.getText());
    Config.ENCHANT_CHANCE_WEAPON = enchant;
    
    int armor = Integer.parseInt(�Ƹ���þ.getText());
		Config.ENCHANT_CHANCE_ARMOR = armor;
		
    int upacse = Integer.parseInt(�Ǽ��Ӽ���þ.getText());
    Config.UPACSE_CHANCE = upacse;
    
    tarea.append("\n�� �Ʒ��� ���� �����մϴ�.");
    tarea.append("\n[�޽���] ��������: ������þ "+enchant+"��");
    tarea.append("\n[�޽���] ��������: �Ƹ���þ "+armor+"��");
    tarea.append("\n[�޽���] ��������: �Ǽ��Ӽ���þ "+upacse+"��");
	}
	
	public void ����������JFrame(){
	  ����������JFrame = new JFrame();
	  
	  äâ���� = new TextField(""+Config.GLOBAL_CHAT_LEVEL, 16);
	  ���Լ��� = new TextField(""+Config.RATE_WEIGHT_LIMIT, 16);
	  ���޽ð� = new TextField(""+Config.RATE_PRIMIUM_TIME, 16);
	  ���ް��� = new TextField(""+Config.RATE_PRIMIUM_NUMBER, 16);
	  
	  ����������JFrame.add(new Label("        äâ����"));
	  ����������JFrame.add(äâ����);
	  ����������JFrame.add(new Label("        ���Լ���"));
	  ����������JFrame.add(���Լ���);
	  ����������JFrame.add(new Label("�������޽ð�"));
	  ����������JFrame.add(���޽ð�);
	  ����������JFrame.add(new Label("�������ް���"));
	  ����������JFrame.add(���ް���);
	  ����������JFrame.add(����������);
	  
	  ����������JFrame.setLayout(new FlowLayout(1));
      ����������JFrame.setTitle("����������");
      ����������JFrame.setSize(250, 190);
      ����������JFrame.setIconImage(im);
      ����������JFrame.setLocation(200, 175);
      ����������JFrame.setResizable(false);
      ����������JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      ����������JFrame.setVisible(true);
	}
	
	public void ����������(){
    int chatlvl = Integer.parseInt(äâ����.getText());
    short chatlvl2 = (short)chatlvl;
    Config.GLOBAL_CHAT_LEVEL = chatlvl2;
    	    
    Float weight = Float.parseFloat(���Լ���.getText());
    double weight2 = (double)weight;
    Config.RATE_WEIGHT_LIMIT = weight2;
		   
    int wingtime = Integer.parseInt(���޽ð�.getText());
    Config.RATE_PRIMIUM_TIME = wingtime;
    
    int wingnumber = Integer.parseInt(���ް���.getText());
    Config.RATE_PRIMIUM_NUMBER = wingnumber;
   
    tarea.append("\n�� �Ʒ��� ���� �����մϴ�.");
    tarea.append("\n[�޽���] ��������: äâ���� "+chatlvl2+"����");
    tarea.append("\n[�޽���] ��������: ���޽ð� "+weight);
    tarea.append("\n[�޽���] ��������: ���޽ð� "+wingtime+"��");
    tarea.append("\n[�޽���] ��������: ���ް��� "+wingnumber+"��");
	}
	
	public void shutdownS(){
    try {
      Runtime.getRuntime().exec("C:\\windows\\system32\\shutdown.exe -s -f -t 20 -c \"��ǻ�͸� ���� �����մϴ�.\n���� �Ϸ� �Ǽ���\""); 
    } catch (IOException ex) { ex.printStackTrace(); } 
    
    serverDownCount(10);
	}
	
	public void shutdownR(){
	  try {
      Runtime.getRuntime().exec("C:\\windows\\system32\\shutdown.exe -r -f -t 20 -c \"��ǻ�͸� �ٽ� �����մϴ�.\n���� �Ϸ� �Ǽ���\""); 
    } catch (IOException ex) { ex.printStackTrace(); } 
    
    serverDownCount(10);
	}
	
	private static String encodePassword2(String rawPassword)throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte buf[] = rawPassword.getBytes("UTF-8");
		buf = MessageDigest.getInstance("SHA"). digest(buf);

		return Base64.encodeBytes(buf);
	}
  // ����� �޼ҵ� ��
  
  // �� �޴��̺�Ʈ ����
  public void actionPerformed(ActionEvent e){
    String menu = e.getActionCommand();
    
    // ���� �޴�
    if(menu == "��������") serverStart();
    else if(menu == "��������"){
      int chk = JOptionPane.showConfirmDialog(null, "���� ������ �����Ͻðڽ��ϱ�?", "������ �����մϴ�.", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if(chk == 0) serverDownCount(10);
    }
    else if(menu == "����") shutdownS();
    else if(menu == "�ٽý���") shutdownR();
    
    //���� �޴�
    if(menu == "��������") ��������JFrame();
    else if(menu == "��������") ��������();
    else if(menu == "��þ����") ��þ����JFrame();
    else if(menu == "��þ����") ��þ����();
    else if(menu == "����������") ����������JFrame();
    else if(menu == "����������") ����������();
    else if(menu == "�޺����α׷�"){
      try {
        Runtime.getRuntime().exec("./data/�޺��������α׷�/�޺��������α׷�.exe"); 
      } catch (IOException ex) { ex.printStackTrace(); }
    }
    else if(menu == "�����Ͻ���"){
      try {
        Runtime.getRuntime().exec("C:/Program Files/PremiumSoft/Navicat 8.0 MySQL/navicat.exe"); 
      } catch (IOException ex) { ex.printStackTrace(); }
    }
    else if(menu == "���������ϱ�"){
            ��������2.setLayout(new FlowLayout());
			��������2.setSize(200, 155);
			��������2.setLocation(200, 175);
			��������2.setResizable(false);
			��������2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			��������2.setVisible(true);
    }
    else if(menu == "��������"){
      Connection con = null;
      PreparedStatement pstm = null;
      
      try {
        //Lead account =  new Account();
        String _name = ���̵�2.getText();
        String _password = encodePassword2(��й�ȣ2.getText());
        int _level = Integer.parseInt(��������2.getText());//�߰��ؾߵ�
        Timestamp lastactive = new Timestamp(System.currentTimeMillis());
        
        String _ip = "000.000.000.000";
        String _host = "000.000.000.000";
        int _banned = 0;
        
        
        con = L1DatabaseFactory.getInstance(). getConnection();
        pstm = con.prepareStatement("INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=?");
        pstm.setString(1, _name);
        pstm.setString(2, _password);
        pstm.setTimestamp(3, lastactive);
        pstm.setInt(4, _level);//����
        pstm.setString(5, _ip);
        pstm.setString(6, _host);
        pstm.setInt(7, _banned);			
        pstm.execute();
        tarea.append("\n���������� ������ ���� �Ǿ����ϴ�.");
      } catch (Exception e23) {
        tarea.append("\n���� �������� �˼����� ������ �߻� �߽��ϴ�.");
      }
    }
    else if(menu == "���������ϱ�"){
            ��������2.setLayout(new FlowLayout());
			��������2.setSize(200, 100);
			��������2.setLocation(200, 175);
			��������2.setResizable(false);
			��������2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			��������2.setVisible(true);
    }
    else if(menu == "��������"){
      JOptionPane.showMessageDialog(null, "���� �غ���!");
    }
    else if(menu == "ĳ���ͻ���"){
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
      
      try{
        c = L1DatabaseFactory.getInstance().getConnection();
        p = c.prepareStatement("select login as uID from accounts where date_add(lastactive, interval 7 day) <= curdate()");
        r = p.executeQuery();
        // �ִٸ�
        while(r.next()){
          uAccuntName = r.getString(1);
          // ������Ʈ ���̵� �˻�
          pp = c.prepareStatement("select objid as oID from characters where account_name=?");
          pp.setString(1, uAccuntName);
          rr = pp.executeQuery();
          // ������ ĳ�� ��ŭ ������, â�� �� ����
          while(rr.next()){
            objId = rr.getInt(1);
            // â��
            warehouse = c.prepareStatement("delete from character_warehouse where account_name=?");
            warehouse.setString(1, uAccuntName);
            warehouse.execute();
            // ��
            teleport  = c.prepareStatement("delete from character_teleport where char_id=?");
            teleport.setInt(1, objId);
            teleport.execute();
            // ��ų
            skills  = c.prepareStatement("delete from character_skills where char_obj_id=?");
            skills.setInt(1, objId);
            skills.execute();
            // ����Ʈ
            quests  = c.prepareStatement("delete from character_quests where char_id=?");
            quests.setInt(1, objId);
            quests.execute();
            //������
            items  = c.prepareStatement("delete from character_items where char_id=?");
            items.setInt(1, objId);
            items.execute();
            //���� â��
            elf_warehouse  = c.prepareStatement("delete from character_elf_warehouse where account_name=?");
            elf_warehouse.setString(1, uAccuntName);
            elf_warehouse.execute();
            //�� -��-
            config  = c.prepareStatement("delete from character_config where object_id=?");
            config.setInt(1, objId);
            config.execute();
            //����
            buff  = c.prepareStatement("delete from character_buff where char_obj_id=?");
            buff.setInt(1, objId);
            buff.execute();    
            // ģ��
            buddys= c.prepareStatement("delete from character_buddys where char_id=?");
            buddys.setInt(1, objId);
            buddys.execute();
            
            // ����� Statement ����
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
          // ������Ʈ ���̵� �˻�
          ppp = c.prepareStatement("delete from characters where objid=?");
          ppp.setInt(1, objId);
          ppp.execute();
          SQLUtil.close(ppp);
          pppp = c.prepareStatement("delete from accounts where login=?");
          pppp.setString(1, uAccuntName);
          pppp.execute();
          SQLUtil.close(pppp);
        }
      }catch(Exception e23){
        tarea.append("ĳ������ ���� : ");
      }finally{
        SQLUtil.close(rr);
        SQLUtil.close(pp);
        SQLUtil.close(r);
        SQLUtil.close(p);
        SQLUtil.close(c);
        tarea.append("\nDB �ȿ� 7�� ������ �ɸ��͸� �����Ͽ����ϴ�.");   
        JOptionPane.showMessageDialog(this, "DB �ɸ����� �Ϸ�", "Has Server", JOptionPane.INFORMATION_MESSAGE);
      }
    }
    else if(menu == "��Ŷ���"){
      ��Ŷ���JFrame.setSize(280, 450);
      ��Ŷ���JFrame.setIconImage(im);
      ��Ŷ���JFrame.setLocation(520, 0);
      ��Ŷ���JFrame.setResizable(false);
      ��Ŷ���JFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      ��Ŷ���JFrame.setVisible(true);
    }
    
    // ����
    if(menu == "��ü��������") allBuff();
    else if(menu == "�����������") allPresentJFrame();
    else if(menu == "������������ֱ�") allPresent();
    
    // ������ �г��� �˾��޴�
    if(menu == "����") �����ֱ�������(userNick.getLabel());
    else if(menu == "�����ֱ�") ��������();
    else if(menu == "�̵�") �̵���Ű��������(userNick.getLabel());
    else if(menu == "�̵���Ű��") �̵�����();
    else if(menu == "����") ���Ž�Ű��������(userNick.getLabel());
    else if(menu == "���Ž�Ű��") ��������();
    else if(menu == "�����߹�") �����߹��Ű��();
  }
  // �� �޴��̺�Ʈ ��
  
  // ������ ������ ����
  public void itemStateChanged(ItemEvent ie) {
    if(ie.getStateChange() == 1 && ie.getSource() == noticeCheckbox){
	    int time = 1;
	    
	    tarea.append("\n�������������������������� ���� ������ ��������������������������������");
	    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	      pc.sendPackets(new S_SystemMessage( "\\fY�˸� : �����߿� ä���� ���� �մϴ�."));
        pc.setSkillEffect(1005 , time * 60 * 1000);
        pc.sendPackets(new S_SkillIconGFX(36, time * 60));
      }
	  }
	  else if(ie.getStateChange() == 2 && ie.getSource() == noticeCheckbox){
	    tarea.append("\n�������������������������� ���� ������ ��������������������������������");
	    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
	      pc.sendPackets(new S_SystemMessage( "\\fY�˸� : ��ſ� �ð� ��������"));
        pc.removeSkillEffect(1005);
      }
	  }
	  
	    boolean a = �Ϲ�ä��.getState();
    	boolean b = �ӼӸ�ä��.getState();
    	boolean c = �۷ι�ä��.getState();
    	boolean d = ����ä��.getState();
    	boolean e = ��Ƽä��.getState();
    	boolean f = ���ä��.getState();
    	boolean g = ��ħä��.getState();  
	    boolean e2 = ä����Ƽ.getState();  // ä����Ƽ �߰� [������] 5�� 26��
           
    	if(ie.getSource() == �Ϲ�ä��){
    		Config.�Ϲ� = true;
    		if (a ==false){
    			Config.�Ϲ� = false;
    		}
    	}else if(ie.getSource() == �ӼӸ�ä��){
    		Config.�ӼӸ�= true;
    		if(b == false){
    			Config.�ӼӸ� = false;
    		}
    	}else if (ie.getSource() == �۷ι�ä��){
    		Config.�۷ι� = true;
    		if (c == false){
    			Config.�۷ι� = false;
    		}
    	}else if (ie.getSource() == ����ä��){
    		Config.���� = true;
    		if (d == false){
    			Config.���� = false;
    		}
    	}else if (ie.getSource() == ��Ƽä��){
    		Config.��Ƽ = true;
    		if (e == false){
    			Config.��Ƽ = false;
    		}
    	}else if (ie.getSource() == ���ä��){
    		Config.��� = true;
    		if (f == false){
    			Config.��� = false;
    		}
    		
    	}else if (ie.getSource() == ��ħä��){
    		Config.��ħ = true;
    		if (g == false){
    			Config.��ħ = false;
    		}	 
    
       }else if (ie.getSource() == ä����Ƽ){  // ä����Ƽ �߰� [������] 5�� 26��
    		Config.ä����Ƽ = true;
    		if (e2 == false){
    			Config.ä����Ƽ = false;
    		}
    	}
  }
  // ������ ������ ��
  
  // ���콺 �̺�Ʈ ����
  public void mouseClicked(MouseEvent e){
    int tmp_a = list.getSelectedIndex();
    if(e.getButton() == 3 && tmp_a != -1){
      popupMenu(list.getItem(list.getSelectedIndex()), e.getX(), e.getY());
      �г��� = new TextField(list.getItem(list.getSelectedIndex()));
    }
  }
  
  public void mousePressed(MouseEvent e){ }
  public void mouseReleased(MouseEvent e){ }
  public void mouseEntered(MouseEvent e){ }
  public void mouseExited(MouseEvent e){ }
  // ���콺 �̺�Ʈ ��
  
  // EnterŰ �̺�Ʈ�� ���Ͱ� ������ �����͸� �����Ҽ� �ְ� �Ѵ�.
  class EnterKey extends KeyAdapter {
  	public void keyPressed(KeyEvent e){
  		int code = e.getKeyCode();
  
  		if(code == KeyEvent.VK_ENTER){  //����Ű�� ������
        for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
          pc.sendPackets(new S_SystemMessage("[******] "+chat.getText()));
        }    			
        chatlog.append("\r\n[******] "+chat.getText());
        chat.setText("");
  		}
  	}
  }
  
  public static void main(String args[]){
    new Leaf("Server Manager");
  }
}