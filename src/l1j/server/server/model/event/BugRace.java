package l1j.server.server.model.event;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.*;
import l1j.server.server.model.*;
import l1j.server.server.model.Instance.*;
import l1j.server.server.datatables.*;
import l1j.server.server.templates.*;
import l1j.server.server.serverpackets.*;
import l1j.server.server.model.shop.L1Shop;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import l1j.server.server.datatables.RaceTable;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
public class BugRace extends Thread {
	private static BugRace _instance;
	
	boolean win;
	
	int winner;
	
	int in;
	
	private Random rnd = new Random();
	
	private GameServerSetting _GameServerSetting;
	
	public static BugRace getInstance() {
		if(_instance == null){
			_instance = new BugRace();
		}
		return _instance;
	}
	
	public static void del() {
		_instance = null;
	}
	
	L1PcInstance cha;

	int ���� = cha.getRnd().nextInt(5);
	int ����2 = cha.getRnd().nextInt(5);
	int ����3 = cha.getRnd().nextInt(20);

   	int ��� =  cha.getRnd().nextInt(50);

		List<L1ShopItem> sellingList1 = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList1 = new ArrayList<L1ShopItem>();

	// ���̽���
	public L1NpcInstance[] ��Ʋ���׺��� = new L1NpcInstance[5];
	
	//-- ���̽��� ������ġ
	private int Start_X[] = { 33522, 33520, 33518, 33516, 33514 };
	
	private int Start_Y[] = { 32861, 32863, 32865, 32867, 32869 };
	
	//-- ���̽��� gfx���̵�
	private int[][] GFX = { { 3478, 3497, 3498, 3499, 3500 }, { 3479, 3501, 3502, 3503, 3504 }, { 3480, 3505, 3506, 3507, 3508 }, { 3481, 3509, 3510, 3511, 3512 } };
	
	//-- ���̽��� �̸���
	
	public int[][] ��ȣ = { { 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10 }, { 11, 12, 13, 14, 15 }, { 16, 17, 18, 19, 20 }, { 21, 22, 23, 24, 25 }, { 26, 27, 28, 29, 30 } };
	
	public String[][] �̸� = { { "�̸��", "������", "�豸��", "��ȣ��", "���缮" }, { "������", "�¿�", "��ȿ��", "���", "�մ��" }, { "����", "�빫��", "����ȯ", "���¿�", "������" }, { "��õ��", "�Ƚ�", "������", "ŷ��¯", "MC��" }, { "���ٸ�", "���ο�", "�ν�", "������", "�ڿ���" }, { "�迬��", "�뼺", "������", "��ä��", "�����" } };
	
	public static int[] TIME = new int[5];
	
	
	public int ��ŷ = 0;
	
	public static String �ϵ� = null;
	
	public String �ϵ�2 = null;
	
	public HashMap _etc;
	
	
	private L1Item[] _allTemplates;
	
	private int highestId = 0;
	
	
	
	public BugRace() {
		super("BugRace");
		start();
	}
	
	public void Start() {
		_GameServerSetting.getInstance().BugRaceRestart = true;
	}
	
	public int ticket_0 = 0;
	
	public int ticket_1 = 0;
	
	public int ticket_2 = 0;
	
	public int ticket_3 = 0;
	
	public int ticket_4 = 0;
	
	
	public double �·�_0 = 0;
	
	public double �·�_1 = 0;
	
	public double �·�_2 = 0;
	
	public double �·�_3 = 0;
	
	public double �·�_4 = 0;
	
	
	public double �·�1_0 = 0;
	
	public double �·�1_1 = 0;
	
	public double �·�1_2 = 0;
	
	public double �·�1_3 = 0;
	
	public double �·�1_4 = 0;
	
	//public String ���� = { { "����", "����", "����", "����", "����" }, { "����", "����", "����", "����", "����" } } ;

	public String ����_0 = "����";
	
	public String ����_1 = "����";
	
	public String ����_2 = "����";
	
	public String ����_3 = "����";
	
	public String ����_4 = "����";
	
	
	public double ����_0 = 0.0;
	
	public double ����_1 = 0.0;
	
	public double ����_2 = 0.0;
	
	public double ����_3 = 0.0;
	
	public double ����_4 = 0.0;
	
	
	L1NpcInstance npc = null;

	L1NpcInstance npc2 = null;

	L1NpcInstance npc3 = null;
	
	
	/*	������ ��ǥ
	 1. 33526, 32839
	 2. 33526, 32841
	 3. 33526, 33843
	 4. 33526, 33845
	 5. 33526, 33847

	 x - 33475 ~ 33538
	 y - 32833 ~ 32884
	 */
	public void run() {
		try{
			_GameServerSetting = GameServerSetting.getInstance();

			L1Object[] obj2 = L1World.getInstance().getObject2();
			for(L1Object obj : obj2){
				if(obj instanceof L1NpcInstance){
					L1NpcInstance n = (L1NpcInstance) obj;
					if(n.getNpcTemplate().get_npcId() == 70041){
						npc = n;
					}else if(n.getNpcTemplate().get_npcId() == 70035){
						npc2 = n;
					}else if(n.getNpcTemplate().get_npcId() == 70042){
						npc3 = n;
					}
				}
			}			

			��ŷ = 0;
			�ϵ� = null;
			�ϵ�2 = null;
			
			
			//System.out.println("[::::::] ���׺��� ���� ���� ����");
			try{
				�����ʱ�ȭ();
			}catch(Exception e){
				System.out.println("[::::::] ���� : ���� �ʱ�ȭ ����");
				e.printStackTrace();
			}
			try{
				SleepTime(); // ���׺��� �޸��� �ӵ� ����
			}catch(Exception e){
				System.out.println("[::::::] ���� : ���׺��� �ӵ� ���� ����");
			}
			loadDog(); // ���׺��� �ʱ�ȭ                      
			//System.out.println("[::::::] ���� : [" + ��Ʋ���׺���[0].get_name() + "], [" + ��Ʋ���׺���[1].get_name() + "], [" + ��Ʋ���׺���[2].get_name() + "], [" + ��Ʋ���׺���[3].get_name() + "], [" + ��Ʋ���׺���[4].get_name() + "]");
			���°���();
			�·�ó��();
			������ǰ�ε�();
			���۾˸�();
		}catch(Exception e){
		}
	}
	
	
	public void �·�ó��() {
		�·�(0);
		�·�(1);
		�·�(2);
		�·�(3);
		�·�(4);
	}
	
	
	public void �·�(int j) {
		L1Racer racer = RaceTable.getInstance().getTemplate(��Ʋ���׺���[j].get_num());
		L1Racer racer0 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[0].get_num());
		L1Racer racer1 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[1].get_num());
		L1Racer racer2 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[2].get_num());
		L1Racer racer3 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[3].get_num());
		L1Racer racer4 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[4].get_num());
		
		String pattern = "#.#";
		DecimalFormat df = new DecimalFormat(pattern);
		
		switch(j){
			case 0:
				�·�_0 = (double) racer.get_�¸�Ƚ��() / (double) (racer.get_�¸�Ƚ��() + racer.get_��Ƚ��()) * 100.0;
				�·�1_0 = Double.parseDouble(df.format(�·�_0));
				break;
			case 1:
				�·�_1 = (double) racer.get_�¸�Ƚ��() / (double) (racer.get_�¸�Ƚ��() + racer.get_��Ƚ��()) * 100.0;
				�·�1_1 = Double.parseDouble(df.format(�·�_1));
				break;
			case 2:
				�·�_2 = (double) racer.get_�¸�Ƚ��() / (double) (racer.get_�¸�Ƚ��() + racer.get_��Ƚ��()) * 100.0;
				�·�1_2 = Double.parseDouble(df.format(�·�_2));
				break;
			case 3:
				�·�_3 = (double) racer.get_�¸�Ƚ��() / (double) (racer.get_�¸�Ƚ��() + racer.get_��Ƚ��()) * 100.0;
				�·�1_3 = Double.parseDouble(df.format(�·�_3));
				break;
			case 4:
				�·�_4 = (double) racer.get_�¸�Ƚ��() / (double) (racer.get_�¸�Ƚ��() + racer.get_��Ƚ��()) * 100.0;
				�·�1_4 = Double.parseDouble(df.format(�·�_4));
				break;
		}
	}
	
	
	public void �¼��߰�(int j) {
		L1Racer racer = RaceTable.getInstance().getTemplate(��Ʋ���׺���[j].get_num());
		racer.set_�¸�Ƚ��(racer.get_�¸�Ƚ��() + 1);
		racer.set_��Ƚ��(racer.get_��Ƚ��());
		SaveAllRacer(racer, ��Ʋ���׺���[j].get_num());
	}
	
	
	public void �м��߰�(int j) {
		L1Racer racer = RaceTable.getInstance().getTemplate(��Ʋ���׺���[j].get_num());
		racer.set_�¸�Ƚ��(racer.get_�¸�Ƚ��());
		racer.set_��Ƚ��(racer.get_��Ƚ��() + 1);
		SaveAllRacer(racer, ��Ʋ���׺���[j].get_num());
	}
	
	
	public void SaveAllRacer(L1Racer racer, int num) {
		Connection con = null;
		PreparedStatement statement = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE util_racer SET �¸�Ƚ��=?, ��Ƚ��=? WHERE ���̼���ȣ=" + num);
			statement.setInt(1, racer.get_�¸�Ƚ��());
			statement.setInt(2, racer.get_��Ƚ��());
			statement.execute();
		}catch(SQLException e){
			System.out.println("[::::::] SaveAllRacer �޼ҵ� ���� �߻�");
		}finally{
			if(statement != null){try{statement.close();}catch(Exception e){}};
			if(con != null){try{con.close();}catch(Exception e){}};
		}
	}
	
	
	public void ���°���() {
		����(0);
		����(1);
		����(2);
		����(3);
		����(4);
	}
	
	
	public void ����(int j) {
		//260, 340 //340~600
	}

	public void ���̽�ǥ(int id, int j) {
		L1Item item = ItemTable.getInstance().getTemplate(id);
		int a = ��Ʋ���׺���[j].getNpcTemplate().get_passispeed();
		L1Racer racer = RaceTable.getInstance().getTemplate(��Ʋ���׺���[j].get_num());
		L1Racer racer0 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[0].get_num());
		L1Racer racer1 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[1].get_num());
		L1Racer racer2 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[2].get_num());
		L1Racer racer3 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[3].get_num());
		L1Racer racer4 = RaceTable.getInstance().getTemplate(��Ʋ���׺���[4].get_num());
		switch(j){		
			case 0:
				if(���==0)item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(60) + 25) / �·�1_0));
				else item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(55) + 15) / �·�1_0));
				break;
			case 1:
				if(���==1)item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(60) + 20) / �·�1_1));
				else item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(55) + 10) / �·�1_1 ));
				break;
			case 2:
				if(���==2)item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(60) + 22) / �·�1_2));
				else item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(55) + 12) / �·�1_2));
				break;
			case 3:
				if(���==3)item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(60) + 23) / �·�1_3));
				else item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(55) + 13) / �·�1_3));
				break;
			case 4:
				if(���==4)item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(60) + 21) / �·�1_4));
				else item.set_����(((��Ʋ���׺���[j].getRnd().nextInt(55) + 11) / �·�1_4));
				break;
		}
		
		item.setNameId("���̽�ǥ #" + ��Ʋ���׺���[j].getNameId());
		item.set_price(5000);
		item.set_���̸�(��Ʋ���׺���[j].getName());
		
		String pattern = "#.#";
		DecimalFormat ef = new DecimalFormat(pattern);

		switch(j){
			case 0:
				����_0 = Double.parseDouble(ef.format(item.get_����()));
				break;
			case 1:
				����_1 = Double.parseDouble(ef.format(item.get_����()));
				break;
			case 2:
				����_2 = Double.parseDouble(ef.format(item.get_����()));
				break;
			case 3:
				����_3 = Double.parseDouble(ef.format(item.get_����()));
				break;
			case 4:
				����_4 = Double.parseDouble(ef.format(item.get_����()));
				break;
		}
		
		���̽�ǥ����("���̽�ǥ", id, 0);
	}
	
	
	public void ���̽�ǥ���ݺ���(int id) {
		L1Item item = ItemTable.getInstance().getTemplate(id);
		if(�ϵ� == item.get_���̸�()){
		L1ShopItem item1 = new L1ShopItem(id, 1, 1, 0);
		sellingList1.add(item1);		
		L1ShopItem item2 = new L1ShopItem(id, (int)(2 * 5000 * item.get_����()), 1, 0);
		purchasingList1.add(item2);		
		���̽�ǥ����2(id, (int) (2 * 5000 * item.get_����()));
		} else {
		L1ShopItem item1 = new L1ShopItem(id, 5000, 1, 0);
		sellingList1.add(item1);		
		L1ShopItem item2 = new L1ShopItem(id, 100, 1, 0);
		purchasingList1.add(item2);		
		���̽�ǥ����2(id, 1);
		}
			//item.set_price((int) (2 * 500 * item.get_����()));
			//���̽�ǥ����2(id, (int) (2 * 500 * item.get_����()));		
	}
	
	
	public void ������ǰ�ε�() {
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();

		ticket_0 =  300000 + ItemTable.getInstance().get_size() + 1;
		SaveRace(ticket_0, "���̽�ǥ " + ticket_0 + "-1");
		
		ticket_1 = 300000 + ItemTable.getInstance().get_size() + 1;
		SaveRace(ticket_1, "���̽�ǥ " + ticket_0 + "-2");
		
		ticket_2 = 300000 + ItemTable.getInstance().get_size() + 1;
		SaveRace(ticket_2, "���̽�ǥ " + ticket_0 + "-3");
		
		ticket_3 = 300000 + ItemTable.getInstance().get_size() + 1;
		SaveRace(ticket_3, "���̽�ǥ " + ticket_0 + "-4");
		
		ticket_4 = 300000 + ItemTable.getInstance().get_size() + 1;
		SaveRace(ticket_4, "���̽�ǥ " + ticket_0 + "-5");
		
		SaveRace(300000 + ItemTable.getInstance().get_size() + 1, "null");

		   
		L1ShopItem item = new L1ShopItem(ticket_0, 5000, 1, 0);
		sellingList.add(item);			
		L1ShopItem item1 = new L1ShopItem(ticket_0, 100, 1, 0);
		purchasingList.add(item1);
		���̽�ǥ(ticket_0, 0);

		L1ShopItem item2 = new L1ShopItem(ticket_1, 5000, 1, 0);
		sellingList.add(item2);			
		L1ShopItem item3 = new L1ShopItem(ticket_1, 100, 1 , 0);
		purchasingList.add(item3);
		���̽�ǥ(ticket_1, 1);

		L1ShopItem item4 = new L1ShopItem(ticket_2, 5000, 1, 0);
		sellingList.add(item4);			
		L1ShopItem item5 = new L1ShopItem(ticket_2, 100, 1, 0);
		purchasingList.add(item5);
		���̽�ǥ(ticket_2, 2);

		L1ShopItem item6 = new L1ShopItem(ticket_3, 5000, 1, 0);
		sellingList.add(item6);			
		L1ShopItem item7 = new L1ShopItem(ticket_3, 100, 1, 0);
		purchasingList.add(item7);
		���̽�ǥ(ticket_3, 3);

		L1ShopItem item8 = new L1ShopItem(ticket_4, 5000, 1, 0);
		sellingList.add(item8);			
		L1ShopItem item9 = new L1ShopItem(ticket_4, 100, 1, 0);
		purchasingList.add(item9);
		���̽�ǥ(ticket_4, 4);

		L1Shop shop = new L1Shop(70035, sellingList, purchasingList);
		ShopTable.getInstance().addShop(70035, shop);
			
        L1Shop shop10 = new L1Shop(70041, sellingList, purchasingList);
		ShopTable.getInstance().addShop(70041, shop10);
		
		
		/*��������(ticket_0, 0, 0);
		��������(ticket_1, 1, 1);
		��������(ticket_2, 2, 2);
		��������(ticket_3, 3, 3);
		��������(ticket_4, 4, 4);*/
	}
	
	
	public void �κ�����() {
		�κ�ǥ����("���̽�ǥ");
	}
	
	
	public void �κ�ǥ����(String j) {
		//L1PcInstance players[] = L1World.getInstance().getAllPlayers();
		
		L1ItemInstance temp = null;
		//for(int i = 0; i < pc.length; i++){
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			temp = pc.getInventory().������ã��(j);
			if(temp != null){
				pc.sendPackets(new S_DeleteInventoryItem(temp));
				pc.getInventory().deleteItem(temp);
				L1World.getInstance().removeObject(temp);
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		}
	}
	
	
	
	//public void ��������(int a, int b, int c) {
		//L1BuyList buylist = new L1BuyList();		
		//���̽�ǥ(a, b);		
		//buylist.set_Id((8888 + c));
		//buylist.set_itemId(a);
		//buylist.set_price(1);
		//buylist.set_shopId(70035); //���� id
		//buylist.set_order(c);
		//ShopTable.getInstance().addShop(70035, a, 1, 0);
		
	//}
	
	
	private void SaveRace(int i, String j) {
				L1RaceTicket etcItem = new L1RaceTicket();
				etcItem.setType2(0);
				etcItem.setItemId(i);
				etcItem.setName(j);
				etcItem.setNameId(j);
				etcItem.setType(12);
				etcItem.setType1(12);
				etcItem.setMaterial(5);
				etcItem.setWeight(0);
				etcItem.set_price(0);
				etcItem.setGfxId(143);
				etcItem.setGroundGfxId(151);
				etcItem.setMinLevel(0);
				etcItem.setMaxLevel(0);
				etcItem.setBless(1);
				etcItem.setTradable(false);
				etcItem.setDmgSmall(0);
				etcItem.setDmgLarge(0);
				etcItem.set_stackable(true);
		
		���̽�ǥ�߰�(etcItem);
		ItemTable.getInstance().Ƽ���߰�(etcItem);
	}
	
	
	public void �����ʱ�ȭ() {
		ShopTable.getInstance().delShop(70035);//���� id
		ShopTable.getInstance().delShop(70041);//��Ų id
	}
	
	
	public void �����ű��(int i) {		
		switch(i){
			case 0:
				��ŷ = ��ŷ + 1;
				npc.broadcastPacket(new S_NpcChatPacket(npc, ��ŷ + "�� - " + ��Ʋ���׺���[0].getNameId(), 0));
				if(��ŷ == 1){
					�ϵ� = ��Ʋ���׺���[0].getName();
					�ϵ�2 = ��Ʋ���׺���[0].getNameId();
					���̽�ǥ���ݺ���(ticket_0);
					���̽�ǥ���ݺ���(ticket_1);
					���̽�ǥ���ݺ���(ticket_2);
					���̽�ǥ���ݺ���(ticket_3);
					���̽�ǥ���ݺ���(ticket_4);
					�¼��߰�(0);
				}else{
					�м��߰�(0);
				}
				break;
			case 1:
				��ŷ = ��ŷ + 1;
				npc.broadcastPacket(new S_NpcChatPacket(npc, ��ŷ + "�� - " + ��Ʋ���׺���[1].getNameId(), 0));
				if(��ŷ == 1){
					�ϵ� = ��Ʋ���׺���[1].getName();
					�ϵ�2 = ��Ʋ���׺���[1].getNameId();
					���̽�ǥ���ݺ���(ticket_0);
					���̽�ǥ���ݺ���(ticket_1);
					���̽�ǥ���ݺ���(ticket_2);
					���̽�ǥ���ݺ���(ticket_3);
					���̽�ǥ���ݺ���(ticket_4);
					�¼��߰�(1);
				}else{
					�м��߰�(1);
				}
				break;
			case 2:
				��ŷ = ��ŷ + 1;
				npc.broadcastPacket(new S_NpcChatPacket(npc, ��ŷ + "�� - " + ��Ʋ���׺���[2].getNameId(), 0));
				if(��ŷ == 1){
					�ϵ� = ��Ʋ���׺���[2].getName();
					�ϵ�2 = ��Ʋ���׺���[2].getNameId();
					���̽�ǥ���ݺ���(ticket_0);
					���̽�ǥ���ݺ���(ticket_1);
					���̽�ǥ���ݺ���(ticket_2);
					���̽�ǥ���ݺ���(ticket_3);
					���̽�ǥ���ݺ���(ticket_4);
					�¼��߰�(2);
				}else{
					�м��߰�(2);
				}
				break;
			case 3:
				��ŷ = ��ŷ + 1;
				npc.broadcastPacket(new S_NpcChatPacket(npc, ��ŷ + "�� - " + ��Ʋ���׺���[3].getNameId(), 0));
				if(��ŷ == 1){
					�ϵ� = ��Ʋ���׺���[3].getName();
					�ϵ�2 = ��Ʋ���׺���[3].getNameId();
					���̽�ǥ���ݺ���(ticket_0);
					���̽�ǥ���ݺ���(ticket_1);
					���̽�ǥ���ݺ���(ticket_2);
					���̽�ǥ���ݺ���(ticket_3);
					���̽�ǥ���ݺ���(ticket_4);
					�¼��߰�(3);
				}else{
					�м��߰�(3);
				}
				break;
			case 4:
				��ŷ = ��ŷ + 1;
				npc.broadcastPacket(new S_NpcChatPacket(npc, ��ŷ + "�� - " + ��Ʋ���׺���[4].getNameId(), 0));
				if(��ŷ == 1){
					�ϵ� = ��Ʋ���׺���[4].getName();
					�ϵ�2 = ��Ʋ���׺���[4].getNameId();
					���̽�ǥ���ݺ���(ticket_0);
					���̽�ǥ���ݺ���(ticket_1);
					���̽�ǥ���ݺ���(ticket_2);
					���̽�ǥ���ݺ���(ticket_3);
					���̽�ǥ���ݺ���(ticket_4);
					�¼��߰�(4);
				}else{
					�м��߰�(4);
				}
				break;
		}
		//������� fix// by ����Ƽ������
		L1Shop shop = new L1Shop(70035, sellingList1, purchasingList1);
		ShopTable.getInstance().addShop(70035, shop);
	    L1Shop shop10 = new L1Shop(70041, sellingList1, purchasingList1);
		ShopTable.getInstance().addShop(70041, shop10);
		
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
	   
		L1ShopItem item = new L1ShopItem(ticket_0, 500000000, 1, 0);
		sellingList.add(item);			
		L1ShopItem item1 = new L1ShopItem(ticket_0, 10, 1, 0);
		purchasingList.add(item1);
		���̽�ǥ(ticket_0, 0);

		L1ShopItem item2 = new L1ShopItem(ticket_1, 500000000, 1, 0);
		sellingList.add(item2);			
		L1ShopItem item3 = new L1ShopItem(ticket_1, 10, 1, 0);
		purchasingList.add(item3);
		���̽�ǥ(ticket_1, 1);

		L1ShopItem item4 = new L1ShopItem(ticket_2, 500000000, 1, 0);
		sellingList.add(item4);			
		L1ShopItem item5 = new L1ShopItem(ticket_2, 10, 1, 0);
		purchasingList.add(item5);
		���̽�ǥ(ticket_2, 2);

		L1ShopItem item6 = new L1ShopItem(ticket_3, 500000000, 1, 0);
		sellingList.add(item6);			
		L1ShopItem item7 = new L1ShopItem(ticket_3, 10, 1, 0);
		purchasingList.add(item7);
		���̽�ǥ(ticket_3, 3);

		L1ShopItem item8 = new L1ShopItem(ticket_4, 500000000, 1, 0);
		sellingList.add(item8);			
		L1ShopItem item9 = new L1ShopItem(ticket_4, 10, 1, 0);
		purchasingList.add(item9);
		���̽�ǥ(ticket_4, 4);
		
		L1Shop shop1 = new L1Shop(70035, sellingList, purchasingList1);
		ShopTable.getInstance().addShop(70035, shop1);

		
		L1Shop shop11 = new L1Shop(70041, sellingList, purchasingList1);
		ShopTable.getInstance().addShop(70041, shop11);
		//������� fix// by ����Ƽ������
	}
	
	
	public void ��ⳡ() throws Exception {
		if((��Ʋ���׺���[0].getX() == 33527) && (��Ʋ���׺���[1].getX() == 33527) && (��Ʋ���׺���[2].getX() == 33527) && (��Ʋ���׺���[3].getX() == 33527) && (��Ʋ���׺���[4].getX() == 33527)){
			sleep(2000);
			//L1PcInstance[] player = L1World.getInstance().getAllPlayers();
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				��Ʋ���׺���[0].deleteMe();
				��Ʋ���׺���[1].deleteMe();
				��Ʋ���׺���[2].deleteMe();
				��Ʋ���׺���[3].deleteMe();
				��Ʋ���׺���[4].deleteMe();
				//������� fix// by ����Ƽ������
				List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
				List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
			   
				L1ShopItem item = new L1ShopItem(ticket_0, 500000000, 1, 0);
				sellingList.add(item);			
				L1ShopItem item1 = new L1ShopItem(ticket_0, 10, 1, 0);
				purchasingList.add(item1);
				���̽�ǥ(ticket_0, 0);

				L1ShopItem item2 = new L1ShopItem(ticket_1, 500000000, 1, 0);
				sellingList.add(item2);			
				L1ShopItem item3 = new L1ShopItem(ticket_1, 10, 1, 0);
				purchasingList.add(item3);
				���̽�ǥ(ticket_1, 1);

				L1ShopItem item4 = new L1ShopItem(ticket_2, 500000000, 1, 0);
				sellingList.add(item4);			
				L1ShopItem item5 = new L1ShopItem(ticket_2, 10, 1, 0);
				purchasingList.add(item5);
				���̽�ǥ(ticket_2, 2);

				L1ShopItem item6 = new L1ShopItem(ticket_3, 500000000, 1, 0);
				sellingList.add(item6);			
				L1ShopItem item7 = new L1ShopItem(ticket_3, 10, 1, 0);
				purchasingList.add(item7);
				���̽�ǥ(ticket_3, 3);

				L1ShopItem item8 = new L1ShopItem(ticket_4, 500000000, 1, 0);
				sellingList.add(item8);			
				L1ShopItem item9 = new L1ShopItem(ticket_4, 10, 1, 0);
				purchasingList.add(item9);
				���̽�ǥ(ticket_4, 4);
				
				L1Shop shop1 = new L1Shop(70035, sellingList, purchasingList1);
				ShopTable.getInstance().addShop(70035, shop1);

				
				L1Shop shop11 = new L1Shop(70041, sellingList, purchasingList1);
				ShopTable.getInstance().addShop(70041, shop11);
				//������� fix// by ����Ƽ������
			}
			if(_GameServerSetting.getInstance().RaceCount != 5){
				_GameServerSetting.getInstance().RaceCount += 1;
				_GameServerSetting.getInstance().���� =2;
				del();
				sleep(3000);
				Start();
			}else{
				_GameServerSetting.getInstance().RaceCount = 0;
				del();
				sleep(1000);
				����˸�();
			}
		}
	}
	
	
	private void ���۾˸�() throws Exception {

		if(_GameServerSetting.getInstance().RaceCount == 0){
	//		���((new StringBuilder()).append("[******] ��� �� ��� ���̽� ����忡�� ���׺��� ���ְ� ����ǿ��� ���� ���� �ٶ��ϴ�.").toString());
			sleep(4 * 1000); // 3���� �޼��� ����
		}else{
//			���((new StringBuilder()).append("[******] �̾ ("+_GameServerSetting.getInstance().RaceCount+"/5) ��° ���׺��� ���ְ� ����ǰڽ��ϴ�.").toString());
			sleep(4 * 1000); // 3���� �޼��� ����
		}
		npc.broadcastPacket(new S_NpcChatPacket(npc, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		
		sleep(60 * 1000); // 3���� �޼��� ����

	//	������ǰ�ε�(); 
		npc.broadcastPacket(new S_NpcChatPacket(npc, "���̽�ǥ �ǸŸ� �����Ͽ����ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "���̽�ǥ �ǸŸ� �����Ͽ����ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "���̽�ǥ �ǸŸ� �����Ͽ����ϴ�.", 0));
		//System.out.println("[::::::] ���̽�ǥ �ǸŸ� �����Ͽ����ϴ�.");
		_GameServerSetting.getInstance().���� = 0;
		
		sleep(60 * 1000);
		
		npc.broadcastPacket(new S_NpcChatPacket(npc, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		//_GameServerSetting.getInstance().���� = 0;

		sleep(40 * 1000);
		
		npc.broadcastPacket(new S_NpcChatPacket(npc, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
 

		sleep(40 * 1000);

		npc.broadcastPacket(new S_NpcChatPacket(npc, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));

		sleep(30 * 1000);

		npc.broadcastPacket(new S_NpcChatPacket(npc, "��� �� ���̽�ǥ �ǸŰ� �����˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "��� �� ���̽�ǥ �ǸŰ� �����˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "��� �� ���̽�ǥ �ǸŰ� �����˴ϴ�.", 0));

		sleep(15 * 1000);

		�����ʱ�ȭ();
		npc.broadcastPacket(new S_NpcChatPacket(npc, "���̽�ǥ �ǸŰ� �����Ǿ����ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "���̽�ǥ �ǸŰ� �����Ǿ����ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "���̽�ǥ �ǸŰ� �����Ǿ����ϴ�.", 0));

		sleep(2000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "10�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "10�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "10�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(5000);
		
		npc.broadcastPacket(new S_NpcChatPacket(npc, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "5�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "4�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "4�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "4�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "3�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "2�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "1�� �� ���׺��� ���ְ� ���۵˴ϴ�.", 0));
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "���!", 0));
		npc2.broadcastPacket(new S_NpcChatPacket(npc2, "���!", 0));
		npc3.broadcastPacket(new S_NpcChatPacket(npc3, "���!", 0));
		
	//	���((new StringBuilder()).append("[******] ��� ���̽� ����忡�� ���׺��� ���ְ� ���۵Ǿ����ϴ�.").toString());
		
		_GameServerSetting.getInstance().���� = 1;
		
		
		//System.out.println("[::::::] ����: ���!");
		//DoorStatus(0);
		StartGame();
		
		
		//���� ��ǥ
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, "���� ������ ��ǥ �ϰڽ��ϴ�.", 0));
		
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, ��Ʋ���׺���[0].getNameId() + ": " + ����_0 + "��", 0));

		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, ��Ʋ���׺���[1].getNameId() + ": " + ����_1 + "��", 0));
	
		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, ��Ʋ���׺���[2].getNameId() + ": " + ����_2 + "��", 0));	

		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, ��Ʋ���׺���[3].getNameId() + ": " + ����_3 + "��", 0));

		sleep(1000);
		npc.broadcastPacket(new S_NpcChatPacket(npc, ��Ʋ���׺���[4].getNameId() + ": " + ����_4 + "��", 0));
		
		//������� fix// by ����Ƽ������
		List<L1ShopItem> sellingList = new ArrayList<L1ShopItem>();
		List<L1ShopItem> purchasingList = new ArrayList<L1ShopItem>();
		
		L1ShopItem item = new L1ShopItem(ticket_0, 500000000, 1, 0);
		sellingList.add(item);			
		L1ShopItem item1 = new L1ShopItem(ticket_0, 10, 1, 0);
		purchasingList.add(item1);
		���̽�ǥ(ticket_0, 0);

		L1ShopItem item2 = new L1ShopItem(ticket_1, 500000000, 1, 0);
		sellingList.add(item2);			
		L1ShopItem item3 = new L1ShopItem(ticket_1, 10, 1, 0);
		purchasingList.add(item3);
		���̽�ǥ(ticket_1, 1);

		L1ShopItem item4 = new L1ShopItem(ticket_2, 500000000, 1, 0);
		sellingList.add(item4);			
		L1ShopItem item5 = new L1ShopItem(ticket_2, 10, 1, 0);
		purchasingList.add(item5);
		���̽�ǥ(ticket_2, 2);

		L1ShopItem item6 = new L1ShopItem(ticket_3, 500000000, 1, 0);
		sellingList.add(item6);			
		L1ShopItem item7 = new L1ShopItem(ticket_3, 10, 1, 0);
		purchasingList.add(item7);
		���̽�ǥ(ticket_3, 3);

		L1ShopItem item8 = new L1ShopItem(ticket_4, 500000000, 1, 0);
		sellingList.add(item8);			
		L1ShopItem item9 = new L1ShopItem(ticket_4, 10, 1, 0);
		purchasingList.add(item9);
		���̽�ǥ(ticket_4, 4);
		
		L1Shop shop = new L1Shop(70035, sellingList, purchasingList1);
		ShopTable.getInstance().addShop(70035, shop);

		
		L1Shop shop10 = new L1Shop(70041, sellingList, purchasingList1);
		ShopTable.getInstance().addShop(70041, shop10);
		return;
		//������� fix// by ����Ƽ������
	}
	
	
	public void ���(String text) {
		//L1PcInstance[] players = L1World.getInstance().getAllPlayers();
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			try{
				pc.sendPackets(new S_SystemMessage(text));
			}catch(Exception exception){
			}
		}
	}
	
	
	private void ����˸�() throws Exception {
		_GameServerSetting.getInstance().���� =2;
		_GameServerSetting.getInstance().BugRaceRestart = false;
		sleep(1000);
		���((new StringBuilder()).append("[******] ��� ���׺��� ���ְ� ���� �Ǿ����ϴ�.").toString());

		sleep(1000);
		���((new StringBuilder()).append("[******] ��÷�� ǥ�� �������� �Ƶ����� ��ȯ�� �帳�ϴ�.").toString());

		sleep(60 * 1000);
	}
	
	
	private void StartGame() {
		bug1 d1 = new bug1();
		bug2 d2 = new bug2();
		bug3 d3 = new bug3();
		bug4 d4 = new bug4();
		bug5 d5 = new bug5();
		d1.start();
		d2.start();
		d3.start();
		d4.start();
		d5.start();
	}
	
	
	
	class bug1 extends Thread {
		public void run() {
			int nx = 0;
			int ny = 0;
			int id = 0;
			try{
				do{
					int count = 46;
					do{
						��Ʋ���׺���[0].setDirectionMove(6);
						--count;
						sleep(��Ʋ���׺���[0].getNpcTemplate().get_passispeed() - (int)(�·�1_0));
					}while(count != 0);
					count = 3;
					do{
						��Ʋ���׺���[0].setDirectionMove(7);
						--count;
						sleep(��Ʋ���׺���[0].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 6;
					do{
						��Ʋ���׺���[0].setDirectionMove(0);
						--count;
						sleep(��Ʋ���׺���[0].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 5;
					do{
						if(����3 == 0 && count == 1){
							��Ʋ���׺���[0].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[0], ��Ʋ���׺���[0].getId(), 30));
							sleep(3000);
						}
						��Ʋ���׺���[0].setDirectionMove(1);
						--count;
						sleep(��Ʋ���׺���[0].getNpcTemplate().get_passispeed()- (int)(�·�1_0));
					}while(count != 0);
					count = 44;
					do{
						if(���� == 0 && count == 1){
							��Ʋ���׺���[0].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[0], ��Ʋ���׺���[0].getId(), 30));
							sleep(3000);
						}
						if(����2 == 1 && count == 5){
							��Ʋ���׺���[0].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[0], ��Ʋ���׺���[0].getId(), 30));
							sleep(3000);
						}
						if(��Ʋ���׺���[0].getX() != 33527){
							��Ʋ���׺���[0].setDirectionMove(2);
							--count;
							��Ʋ���׺���[0].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[0].getNpcTemplate().get_passispeed() - (int)(�·�1_0*2));
						}else{
							�����ű��(0);
							��ⳡ();
							break;
						}
					}while(true);
					break;
				}while(true);
			}catch(Exception e){
			}
		}
	}
	
	
	class bug2 extends Thread {
		public void run() {
			int nx = 0;
			int ny = 0;
			int id = 0;
			try{
				do{
					int count = 43; //45
					do{
						��Ʋ���׺���[1].setDirectionMove(6);
						--count;
						sleep(��Ʋ���׺���[1].getNpcTemplate().get_passispeed()- (int)(�·�1_1));
					}while(count != 0);
					count = 5;
					do{
						��Ʋ���׺���[1].setDirectionMove(7);
						--count;
						sleep(��Ʋ���׺���[1].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 6; //6
					do{
						��Ʋ���׺���[1].setDirectionMove(0);
						--count;
						sleep(��Ʋ���׺���[1].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 7; //8
					do{
						if(����3 == 1  && count == 1){
							��Ʋ���׺���[1].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[1], ��Ʋ���׺���[1].getId(), 30));
							sleep(3000);
						}
						��Ʋ���׺���[1].setDirectionMove(1);
						--count;
						sleep(��Ʋ���׺���[1].getNpcTemplate().get_passispeed()- (int)(�·�1_1));
					}while(count != 0);
					count = 43; 
					do{
						if(���� == 1  && count == 1){
							��Ʋ���׺���[1].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[1], ��Ʋ���׺���[1].getId(), 30));
							sleep(3000);
						}
						if(����2 == 2 && count == 5){
							��Ʋ���׺���[1].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[1], ��Ʋ���׺���[1].getId(), 30));
							sleep(3000);
						}
						if(��Ʋ���׺���[1].getX() != 33527){
							��Ʋ���׺���[1].setDirectionMove(2);
							--count;
							��Ʋ���׺���[1].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[1].getNpcTemplate().get_passispeed()- (int)(�·�1_1*2));
					}else{
							�����ű��(1);
							��ⳡ();
							break;
						}
					}while(true);
					break;
				}while(true);
				}catch(Exception e){
			}
		}
	}
	
	
	class bug3 extends Thread {
		public void run() {
			int nx = 0;
			int ny = 0;
			int id = 0;
			try{
				do{
					int count = 40; //44
					do{
						��Ʋ���׺���[2].setDirectionMove(6);
						--count;
						sleep(��Ʋ���׺���[2].getNpcTemplate().get_passispeed()- (int)(�·�1_2));
					}while(count != 0);
					count = 7; //5
					do{
						��Ʋ���׺���[2].setDirectionMove(7);
						--count;
						sleep(��Ʋ���׺���[2].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 6; //7
					do{
						��Ʋ���׺���[2].setDirectionMove(0);
						--count;
						sleep(��Ʋ���׺���[2].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 9; //10
					do{
						if(����3 == 2  && count == 1){
							��Ʋ���׺���[2].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[2], ��Ʋ���׺���[2].getId(), 30));
							sleep(3000);
						}
						��Ʋ���׺���[2].setDirectionMove(1);
						--count;
						sleep(��Ʋ���׺���[2].getNpcTemplate().get_passispeed()- (int)(�·�1_2));
					}while(count != 0);
					count = 45;
					do{
						if(���� == 2  && count == 1){
							��Ʋ���׺���[2].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[2], ��Ʋ���׺���[2].getId(), 30));
							sleep(3000);
						}
						if(����2 == 3 && count == 6){
							��Ʋ���׺���[2].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[2], ��Ʋ���׺���[2].getId(), 30));
							sleep(3000);
						}
						if(��Ʋ���׺���[2].getX() != 33527){
							��Ʋ���׺���[2].setDirectionMove(2);
							--count;
							��Ʋ���׺���[2].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[2].getNpcTemplate().get_passispeed()- (int)(�·�1_2*2));
						}else{
							�����ű��(2);
							��ⳡ();
							break;
						}
					}while(true);
					break;
				}while(true);
			}catch(Exception e){
			}
		}
	}
	
	
	class bug4 extends Thread {
		public void run() {
			int nx = 0;
			int ny = 0;
			int id = 0;
			try{
				do{
					int count = 37; //46
					do{
						��Ʋ���׺���[3].setDirectionMove(6);
						--count;
						sleep(��Ʋ���׺���[3].getNpcTemplate().get_passispeed()- (int)(�·�1_3));
					}while(count != 0);
					count = 9; //3
					do{
						��Ʋ���׺���[3].setDirectionMove(7);
						--count;
						sleep(��Ʋ���׺���[3].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 6;
					do{
						��Ʋ���׺���[3].setDirectionMove(0);
						--count;
						sleep(��Ʋ���׺���[3].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 11; //18
					do{
						if(����3 == 3  && count == 1){
							��Ʋ���׺���[3].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[3], ��Ʋ���׺���[3].getId(), 30));
							sleep(3000);
						}
						��Ʋ���׺���[3].setDirectionMove(1);
						--count;
						sleep(��Ʋ���׺���[3].getNpcTemplate().get_passispeed()- (int)(�·�1_3));
					}while(count != 0);
					count = 43;
					do{
						if(���� == 3  && count == 1){
							��Ʋ���׺���[3].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[3], ��Ʋ���׺���[3].getId(), 30));
							sleep(3000);
						}
						if(����2 == 4 && count == 8){
							��Ʋ���׺���[3].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[3], ��Ʋ���׺���[3].getId(), 30));
							sleep(3000);
						}
						if(��Ʋ���׺���[3].getX() != 33527){
							��Ʋ���׺���[3].setDirectionMove(2);
							--count;
							��Ʋ���׺���[3].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[3].getNpcTemplate().get_passispeed()- (int)(�·�1_3*2));
						}else{
							�����ű��(3);
							��ⳡ();
							break;
						}
					}while(true);
					break;
				}while(true);
			}catch(Exception e){
			}
		}
	}
	
	
	class bug5 extends Thread {
		public void run() {
			int nx = 0;
			int ny = 0;
			int id = 0;
			try{
				do{
					int count = 34; //46
					do{
						��Ʋ���׺���[4].setDirectionMove(6);
						--count;
						sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed()- (int)(�·�1_4));
					}while(count != 0);
					count = 11; //3
					do{
						��Ʋ���׺���[4].setDirectionMove(7);
						--count;
						sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 6;
					do{
						��Ʋ���׺���[4].setDirectionMove(0);
						--count;
						sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed());
					}while(count != 0);
					count = 11; //22
					do{
						if(����3 == 4  && count == 1){
							��Ʋ���׺���[4].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[4], ��Ʋ���׺���[4].getId(), 30));
							sleep(3000);
						}
						��Ʋ���׺���[4].setDirectionMove(1);
						--count;
						sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed()- (int)(�·�1_4));
					}while(count != 0);
					count = 43;
					do{
						if(���� == 4  && count == 1){
							��Ʋ���׺���[4].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[4], ��Ʋ���׺���[4].getId(), 30));
							sleep(3000);
						}
						if(����2 == 0 && count == 4){
							��Ʋ���׺���[4].broadcastPacket(new S_AttackPacketForNpc(��Ʋ���׺���[4], ��Ʋ���׺���[4].getId(), 30));
							sleep(3000);
						}

						if(��Ʋ���׺���[4].getX() == 33496){
							��Ʋ���׺���[4].setDirectionMove(1);
							--count;
							��Ʋ���׺���[4].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed()- (int)(�·�1_4*2));
						}else if(��Ʋ���׺���[4].getX() == 33512){
							��Ʋ���׺���[4].setDirectionMove(1);
							--count;
							��Ʋ���׺���[4].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed()- (int)(�·�1_4*2));
						}else if(��Ʋ���׺���[4].getX() != 33527){
							��Ʋ���׺���[4].setDirectionMove(2);
							--count;
							��Ʋ���׺���[4].getNpcTemplate().get_passispeed();
							sleep(��Ʋ���׺���[4].getNpcTemplate().get_passispeed()- (int)(�·�1_4*2));
						}else{
							�����ű��(4);
							��ⳡ();
							break;
						}
					}while(true);
					break;
				}while(true);
			}catch(Exception e){
			}
		}
	}

	private void SleepTime() {
		for(int i = 0; i < 5; ++i){
			TIME[i] = ��Ʋ���׺���[i].getRnd().nextInt(50) + 350; // 260, 240
		}
	}
	private void loadDog() {
		L1Npc npc;
		for(int m = 0; m < 5; ++m){
			try{
				npc = new L1Npc();
				npc.set_passispeed(TIME[m]);
				npc.set_family(0);
				npc.set_agrofamily(0);
				npc.set_picupitem(false);
				//npc.set_candie(0);
				//npc.set_type("L1Npc");

				Object[] parameters = { npc };
				��Ʋ���׺���[m] = (L1NpcInstance) Class.forName("l1j.server.server.model.Instance.L1NpcInstance").getConstructors()[0].newInstance(parameters);
				��Ʋ���׺���[m].setGfxId(GFX[��Ʋ���׺���[m].getRnd().nextInt(4)][��Ʋ���׺���[m].getRnd().nextInt(5)]);
				int ���̼� = ��Ʋ���׺���[m].getRnd().nextInt(6);
				��Ʋ���׺���[m].setNameId(�̸�[���̼�][m]);
				��Ʋ���׺���[m].setName(��Ʋ���׺���[m].getNameId());
				��Ʋ���׺���[m].set_num(��ȣ[���̼�][m]);
				��Ʋ���׺���[m].setX(Start_X[m]);
				��Ʋ���׺���[m].setY(Start_Y[m]);
				��Ʋ���׺���[m].setMap((short) 4);
				��Ʋ���׺���[m].setHeading(6);
				��Ʋ���׺���[m].setId(IdFactory.getInstance().nextId());
				L1World.getInstance().storeObject(��Ʋ���׺���[m]);
				L1World.getInstance().addVisibleObject(��Ʋ���׺���[m]);
	        	for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(��Ʋ���׺���[m])) {
					if (pc != null){
						pc.UpdateObject();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	public void ���̽�ǥ�߰�(L1RaceTicket race) {
		Connection con = null;
		PreparedStatement statement = null;

		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO items_race SET item_id=?,name=?,item_type=?,material=?,inv_gfxid=?," + "ground_gfxid=?,name_id=?,weight=?,price=?,consume=?,piles=?");
			statement.setInt(1, race.getItemId());
			statement.setString(2, race.getName());
			statement.setString(3, "other");
			statement.setString(4, "paper");
			statement.setInt(5, 143);
			statement.setInt(6, 1019);
			statement.setString(7, race.getNameId());
			statement.setInt(8, 0);
			statement.setInt(9, 0);
			statement.setInt(10, 0);
			statement.setInt(11, 1);
			statement.execute();
		}catch(SQLException e){
			System.out.println("[::::::] ���̽�ǥ �߰� �޼ҵ� ���� �߻�");
			e.printStackTrace();
		}finally{
			if(statement != null){try{statement.close();}catch(Exception e){}};
			if(con != null){try{con.close();}catch(Exception e){}};
		}
	}
	
	
	public void ���̽�ǥ����(String name, int itemid, int price) {
		Connection con = null;
		PreparedStatement statement = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items_race SET item_type=?,material=?,inv_gfxid=?,ground_gfxid=?," + "name_id=?,weight=?,price=?,consume=?,piles=? WHERE item_id=" + itemid);
			statement.setString(1, "other");
			statement.setString(2, "paper");
			statement.setInt(3, 143);
			statement.setInt(4, 151);
			statement.setString(5, name);
			statement.setInt(6, 0);
			statement.setInt(7, price);
			statement.setInt(8, 0);
			statement.setInt(9, 1);
			statement.execute();
		}catch(SQLException e){
			System.out.println("[::::::] ���̽�ǥ���� �޼ҵ� ���� �߻�");
			e.printStackTrace();
		}finally{
			if(statement != null){try{statement.close();}catch(Exception e){}};
			if(con != null){try{con.close();}catch(Exception e){}};
		}
	}
	
	
	public void ���̽�ǥ����2(int itemid, int price) {
		Connection con = null;
		PreparedStatement statement = null;
		try{
			con = L1DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("UPDATE items_race SET item_type=?,material=?,inv_gfxid=?,ground_gfxid=?," + "weight=?,price=?,consume=?,piles=? WHERE item_id=" + itemid);
			statement.setString(1, "other");
			statement.setString(2, "paper");
			statement.setInt(3, 143);
			statement.setInt(4, 151);
			statement.setInt(5, 0);
			statement.setInt(6, price);
			statement.setInt(7, 0);
			statement.setInt(8, 1);
			statement.execute();
		}catch(SQLException e){
			System.out.println("[::::::] ���̽�ǥ����2 �޼ҵ� ���� �߻�");
			e.printStackTrace();
		}finally{
			if(statement != null){try{statement.close();}catch(Exception e){}};
			if(con != null){try{con.close();}catch(Exception e){}};
		}
	}
}
