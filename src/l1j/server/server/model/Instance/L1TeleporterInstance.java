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

package l1j.server.server.model.Instance;

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;

// Referenced classes of package l1j.server.server.model:
// L1NpcInstance, L1Teleport, L1NpcTalkData, L1PcInstance,
// L1TeleporterPrices, L1TeleportLocations

public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			if (npcid == 50014) { // �� ��
				if (player.isWizard()) { // ������
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // �� ������ ��
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
			} else if (npcid == 70779) { // ����Ʈ��Ʈ
				if (player.getTempCharGfx() == 1037) { // ���̾�Ʈ��Ʈ ����
					htmlid = "ants3";
				} else if (player.getTempCharGfx() == 1039) {// ���̾�Ʈ��Ʈ�Ҹ��� ����
					if (player.isCrown()) { // ����
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // �ֹε��� ��ǰ
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { //Step1 �̿�
							htmlid = "antsn";
						}
					} else { // ���� �̿�
						htmlid = "antsn";
					}
				}
			} else if (npcid == 70853) { // �� ��������
				if (player.isElf()) { // ������
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // �������� ���ɼ�
							Random random = new Random();
							if (random.nextInt(100) < 50) { // 50%�� ��ũ��������
								htmlid = "fairyp2";
							} else { // ��ũ ������ ���� ����
								htmlid = "fairyp1";
							}
						}
					}
				}
			} else if (npcid == 50031) { // ���Ǿ�
				if (player.isElf()) { // ������
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { // ��� �÷�
							htmlid = "sepia1";
						}
					}
				}
			} else if (npcid == 50043) { // Lambda
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �𰡸��� ���ǰ� ���� ����
					if (player.isCrown()) { // ����
						if (_isNowDely) { // �ڷ���Ʈ ������
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // ���� �̿�
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}
			}
			// �뷡�ϴ� ���� �ڷ� ����
			else if (npcid == 50082) {
				if (player.getLevel() < 13) {
					htmlid = "en0221";
				} else {
					if (player.isElf()) {
						htmlid = "en0222e";
					} else if (player.isDarkelf()) {
						htmlid = "en0222d";
					} else {
						htmlid = "en0222";
					}
				}
			}
			// �ٸ��Ͼ�
			else if (npcid == 50001) {
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
			}
            // �극��
			else if (npcid == 70751) {
			         htmlid = "brad1";
			}			
			// html ǥ��
			if (htmlid != null) { // htmlid�� �����ǰ� �ִ� ���
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // �÷��̾ ī��ƽ
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.finest((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			
			   String[] price = null;
			   
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50015: { // �̾߱��� �� �ִ� �����׸���
			     price = new String[]{"1500"};
			    }
			    break;
			    case 50020: { // ��Ʈ ���ĸ�
			     price = new String[]{ "50","50","120","120","50","180","120","120","180","200","200","600","7100" };
			    }
			    break;
			    case 50024: { // �׸���ƽ�Ÿ
			     price = new String[]{ "55","55","55","132","132","198","198","264","180","240","264","220","220","550","7480" };
			    }
			    break;
			    case 50036: { // ���������
			     price = new String[]{ "126","126","52","189","52","52","189","126","126","315","315","735","7770" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     price = new String[]{ "185","185","123","247","51","123","247","51","185","412","412","824","7931" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     price = new String[]{ "259","129","194","194","129","54","324","194","259","540","540","972","7992" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     price = new String[]{ "259","129","194","194","129","54","324","194","259","540","540","972","7992" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     price = new String[]{ "240","240","180","300","120","180","300","50","240","500","500","900","8000" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     price = new String[]{ "50","50","120","120","180","180","180","240","240","300","200","500","6500" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     price = new String[]{"55","55","55","132","132","132","198","198","246","330","330","770","7480"};
			    }
			    break;
			    case 50066: { // ���̳׸���
			     price = new String[]{ "180","50","120","120","50","50","240","120","180","400","400","800","7100" };
			    }
			    break;
			    case 200029: { // ������ - ��ī���
			    	
				 price = new String[]{ "50","50","50","50","120","120","180","180","180","240","240","400","400","800","7700" };
				}
				break;
			    case 200030: { // �Ǻ�����  - ����
			    	
				 price = new String[]{ "50","50","50","50","120","120","180","180","180","240","240","400","400","800","7700" };
				}
				break;
			    case 50068: { // ħ���� �������׸���, ��Ʈ, ���, ����, Heine, ����
			     price = new String[]{ "1500","800","600","1800","1800","1000" };
			    }
			    break;
			    case 50026: { // �׸��� ���墡��� ����, ���� ����, �ǹ� ����Ʈ Ÿ�� ����
			     price = new String[]{ "0","0","0"};
			    }
			    break;
			    case 50033: { // ��� ���墡�׸��� ����, ���� ����, �ǹ� ����Ʈ Ÿ�� ����
			     price = new String[]{ "0","0","0"};
			    }
			    break;
			    case 50049: { // ���� ���墡�׸��� ����, ��� ����, �ǹ� ����Ʈ Ÿ�� ����
			     price = new String[]{ "0","0","0"};
			    }
			    break;
			    case 50059: { // �ǹ� ����Ʈ Ÿ�� ���墡�׸��� ����, ��� ����, ���� ����
			     price = new String[]{ "0","0","0"};
			    }
			    break;
			    case 200051: { // �ų� �÷ζ�
				     price = new String[]{ "14000" };
				}
				break;
			    case 200056: { // ������ ����
				     price = new String[]{ "14000" };
				}
				break;
			    case 50079: { // �ٴϿ� ����� ��
			    	price = new String[]{ "550","550","550","600","600","600","650","700","750","750","500","500","7700" };
				}
				break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			}
		 if (action.equalsIgnoreCase("teleportURLA")) {   //     
		      String html = "";
		      String[] price = null;
		      int npcid = getNpcTemplate().get_npcId();
		      switch(npcid)
		      {
		       case 200030: { // �Ǻ����� ����� ��
		    html = "sharial3";
		        price = new String[]{"220","330","330","330","440","440","550","550","550","550"};
		       }
		       break;
		       case 200029: { // ������ ����� ��
		       html = "dekabia3";
		     price = new String[]{"100","220","220","220","330","330","330","330","440","440"};
		    }
		       break;
		       case 50079: { // �ٴϿ� ����� ��
		       html = "telediad3";
		     price = new String[]{"700","800","800","1000"};
		    }
		    break;
		       default: {
		        price = new String[]{""};
		       }
		      }
		      player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		  }
		 if (action.equalsIgnoreCase("teleportURLC")) {	  // 11~25���� ����		   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_1_2";
			     price = new String[]{ "465","465","465","465","1050","1050"};
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		 if (action.equalsIgnoreCase("teleportURLB")) {	// 11~25���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_1_1";
			     price = new String[]{ "450","450","450","450"};
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		 
		if (action.equalsIgnoreCase("teleportURLD")) {	// 11~25���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_1_3";
			     price = new String[]{ "480","480","480","480","630","1080","630" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLE")) {	// 26~35���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_2_1";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLF")) {	// 26~35���� ����   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_2_2";
			     price = new String[]{ "600","600","750","750" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLG")) {	// 26~35���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_2_3";
			     price = new String[]{ "630","630","780","1080","930" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLH")) {	// 36~40���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_3_1";
			     price = new String[]{ "750","750","750","1200","1050" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLI")) {	// 36~40���� ����   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_3_2";
			     price = new String[]{ "765","765","765","765","1515","1515","915" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLJ")) {	// 36~40���� ���Ѱ�   
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_3_3";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		
		if (action.equalsIgnoreCase("teleportURLK")) {	// 41~44���� 
			   String html = "";
			   String[] price = null;
			   int npcid = getNpcTemplate().get_npcId();
			   switch(npcid)
			   {
			    case 50020: { // ��Ʈ ���ĸ�
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50024: { // �۷�� �ƽ�Ÿ
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50036: { // ���������
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50039: { // ���� �����׸� ��
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50044: { // ���� �ø��콺
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50046: { // ���� ��������
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50051: { // ����Ű���콺
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50054: { // ���ٿ���Ʈ����
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50056: { // �ø��ٳ���ƮŸ���Ʈ
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    case 50066: { // ���̳׸���
			     html = "guide_4";
			     price = new String[]{ "780","780","780","780","780","1230","1080" };
			    }
			    break;
			    default: {
			     price = new String[]{""};
			    }
			   }
			   player.sendPackets(new S_NPCTalkReturn(objid, html, price));
		}
		 
		if (action.startsWith("teleport ")) {
			_log.finest((new StringBuilder()).append("Setting action to : ")
					.append(action).toString());
			doFinalAction(player, action);
		}
	}
	

	private void doFinalAction(L1PcInstance player, String action) {
		int objid = getId();

		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // �� ��
			if (!player.getInventory().checkItem(40581)) { // �� ������ Ű
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // Lambda
			if (_isNowDely) { // �ڷ���Ʈ ������
				isTeleport = false;
			}
		} else if (npcid == 50625) { // �����(Lv50 ����Ʈ ����� ���� 2 F)
			if (_isNowDely) { // �ڷ���Ʈ ������
				isTeleport = false;
			}
		}	
	
		/*if (isTeleport) { // �ڷ���Ʈ ����
			try {
				// ��źƮ��Ʈ����(���� Lv30 ����Ʈ)
				if (action.equalsIgnoreCase("teleport mutant-dungen")) {
					// 3 �Ž� �̳��� Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc.getClanid() == player.getClanid()
								&& otherPc.getId() != player.getId()) {
							L1Teleport.teleport(otherPc, 32740, 32800, (short) 217, 5,
									true);
						}
					}
					L1Teleport.teleport(player, 32740, 32800, (short) 217, 5,
							true);
				}
				// �÷��� ���� ����(������ Lv30 ����Ʈ)
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen")) {
					L1Teleport.teleport(player, 32791, 32788, (short) 201, 5,
							true);
				} else if (action.equalsIgnoreCase("teleport 29")) { // Lambda
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3 �Ž� �̳��� Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						L1Quest quest = otherPc.getQuest();
						if (otherPc.isKnight() // ����Ʈ
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �𰡸��� ���ǰ� ���� ����
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // ������
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �𰡸��� ���ǰ� ���� ����
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // ������
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // �𰡸��� ���ǰ� ���� ����
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // ��Ŭ���� ���߾��� �ִ�
						L1Teleport.teleport(player, 32723, 32850, (short) 2000,
								2, true);
						L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6,
								true);
						L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6,
								true);
						L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0,
								true);
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().execute(timer);
					}
				} else if (action.equalsIgnoreCase("teleport barlog")) { // �����(Lv50 ����Ʈ ����� ���� 2 F)
					L1Teleport.teleport(player, 32755, 32844, (short) 2002, 5,
							true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);
				}
			} catch (Exception e) {
			}
		}*/
		if (htmlid != null) { // ǥ���ϴ� html�� �ִ� ���
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
		}
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15��
			} catch (Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;
	private static Logger _log = Logger
			.getLogger(L1TeleporterInstance.class
					.getName());

}