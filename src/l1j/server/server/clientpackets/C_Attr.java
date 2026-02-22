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

package l1j.server.server.clientpackets;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.model.L1Racing;		// 레이싱
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;  
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.model.L1PetMember; 
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.item.L1ItemId;	 
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_bonusstats;  
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Resurrection;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Trade;	 
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.Announcements;
import l1j.server.server.datatables.BuddyTable;
import l1j.server.server.serverpackets.S_Party; // 파티장 위임, 추방  - ACE

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attr extends ClientBasePacket {

	private static final Logger _log = Logger.getLogger(C_Attr.class.getName());
	private static final String C_ATTR = "[C] C_Attr";

	public C_Attr(byte abyte0[], ClientThread clientthread) throws Exception {
		super(abyte0);
		int i = readH();
		int c;
		String name;

		L1PcInstance pc = clientthread.getActiveChar();

		if (pc == null) {
			return;
		}
		switch (i) {
		
		case 623: // 설문조사
			c = readC();
			   if(pc.isPrivateShop() == true){
				    break;
			   }
			int d = Config.Quest_No + 1;
			int e = Config.Quest_Yes + 1;
				if (c == 0) { //No
				    Config.setParameterValue1("No", d );
				    pc.sendPackets(new S_SystemMessage("반대를 선택하셨습니다.")); 
				} else if(c == 1) { //Yes
				    Config.setParameterValue1("Yes", e );
				    pc.sendPackets(new S_SystemMessage("찬성을 선택하셨습니다.")); 
				}
		  break; 
	
		case 97: // %0가 혈맹에 가입했지만은 있습니다.승낙합니까? (Y/N)
			c = readC();
			L1PcInstance joinPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (joinPc != null) {
				if (c == 0) { // No
					joinPc.sendPackets(new S_ServerMessage(96, pc
							.getName())); // \f1%0은 당신의 요청을 거절했습니다.
				} else if (c == 1) { // Yes
					int clan_id = pc.getClanid();
					String clanName = pc.getClanname();
					L1Clan clan = L1World.getInstance().getClan(clanName);
					if (clan != null) {
						int maxMember = 0;
						int charisma = pc.getCha();
						boolean lv45quest = false;
						if (pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
							lv45quest = true;
						}
						if (pc.getLevel() >= 50) { // Lv50 이상
							if (lv45quest == true) { // Lv45 퀘스트 클리어가 끝난 상태
								maxMember = charisma * 9;
							} else {
								maxMember = charisma * 3;
							}
						} else { // Lv50 미만
							if (lv45quest == true) { // Lv45 퀘스트 클리어가 끝난 상태
								maxMember = charisma * 6;
							} else {
								maxMember = charisma * 2;
							}
						}
						if (Config.MAX_CLAN_MEMBER > 0) { // Clan 인원수의 상한의 설정 있어
							maxMember = Config.MAX_CLAN_MEMBER;
						}

						if (joinPc.getClanid() == 0) { // 크란미가입
							String clanMembersName[] = clan.getAllMembers();
							if (maxMember <= clanMembersName.length) { // 빈 곳이 없다
								joinPc.sendPackets( // %0는 당신을 혈맹원으로서 받아들일 수가 없습니다.
										new S_ServerMessage(188, pc.getName()));
								return;
							}
							for (L1PcInstance clanMembers : clan
									.getOnlineClanMember()) {
								clanMembers.sendPackets(new S_ServerMessage(94,
										joinPc.getName())); // \f1%0이 혈맹의 일원으로서 받아들여졌습니다.
							}
							joinPc.setClanid(clan_id);
							joinPc.setClanname(clanName);
							joinPc.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
							joinPc.save(); // DB에 캐릭터 정보를 기입한다
							clan.addMemberName(joinPc.getName());
							joinPc.sendPackets(new S_ServerMessage(95,
									clanName)); // \f1%0 혈맹에 가입했습니다.
						} else { // 크란 가입이 끝난 상태(크란 연합)
							if (Config.CLAN_ALLIANCE) {
								changeClan(clientthread, pc, joinPc, maxMember);
							} else {
								joinPc.sendPackets(new S_ServerMessage(89)); // \f1당신은 벌써 혈맹에 가입하고 있습니다.
							}
						}
					}
				}
			}
			break;

		case 217: // %0혈맹의%1가 당신의 혈맹과의 전쟁을 바라고 있습니다.전쟁에 응합니까? (Y/N)
		case 221: // %0혈맹이 항복을 바라고 있습니다.받아들입니까? (Y/N)
		case 222: // %0혈맹이 전쟁의 종결을 바라고 있습니다.종결합니까? (Y/N)
			c = readC();
			L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			if (enemyLeader == null) {
				return;
			}
			pc.setTempID(0);
			String clanName = pc.getClanname();
			String enemyClanName = enemyLeader.getClanname();
			if (c == 0) { // No
				if (i == 217) {
					enemyLeader.sendPackets(new S_ServerMessage(236, clanName)); // %0혈맹이 당신의 혈맹과의 전쟁을 거절했습니다.
				} else if (i == 221 || i == 222) {
					enemyLeader.sendPackets(new S_ServerMessage(237, clanName)); // %0혈맹이 당신의 제안을 거절했습니다.
				}
			} else if (c == 1) { // Yes
				if (i == 217) {
					L1War war = new L1War();
					war.handleCommands(2, enemyClanName, clanName); // 모의전 개시
				} else if (i == 221 || i == 222) {
					// 전전쟁 리스트를 취득
					for (L1War war : L1World.getInstance().getWarList()) {
						if (war.CheckClanInWar(clanName)) { // 자크란이 가고 있는 전쟁을 발견
							if (i == 221) {
								war.SurrenderWar(enemyClanName, clanName); // 항복
							} else if (i == 222) {
								war.CeaseWar(enemyClanName, clanName); // 종결
							}
							break;
						}
					}
				}
			}
			break;

		case 252: // 
            c = readC();
        //    L1Object obj = L1World.getInstance().findObject(pc.getTradeID());
			L1PcInstance trading_partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTradeID());
           ///////////주사위 
					L1Npc npc = NpcTable.getInstance().getTemplate(3000000);
					L1Npc npc2 = NpcTable.getInstance().getTemplate(300026);
					L1Npc npc3 = NpcTable.getInstance().getTemplate(3000001);
/////////////주사위 
	/*		if (obj != null) {
                    if(obj instanceof L1PcInstance){
                            L1PcInstance trading_partner = (L1PcInstance)obj;
                            if (c == 0) // No
                            {
                                    trading_partner.sendPackets(new S_ServerMessage(253, pc
                                                    .getName())); // 
                                    pc.setTradeID(0);
                                    trading_partner.setTradeID(0);
                            pc.setTrade(false);
                    trading_partner.setTrade(false);
                            } else if (c == 1) // Yes
                            {
                                    pc.sendPackets(new S_Trade(trading_partner.getName()));
                                    trading_partner.sendPackets(new S_Trade(pc.getName()));
                            }                                       
                    }else if(obj instanceof L1NpcInstance){
                            L1NpcInstance cha = (L1NpcInstance) obj;
                            if (c == 0){
                                    pc.setTradeID(0);
                            }else if (c == 1){
                                    pc.sendPackets(new S_Trade(cha.getName()));
                            }
                    }
            }                       
            break;*/
if (trading_partner != null) {
				if (c == 0) // No
				{
					trading_partner.sendPackets(new S_ServerMessage(253, pc
							.getName())); // %0%d는 당신과의 거래에 응하지 않았습니다.
					pc.setTradeID(0);
					trading_partner.setTradeID(0);
			        pc.setTrade(false);
	     	        trading_partner.setTrade(false);
				} else if (c == 1) // Yes
				{
					pc.sendPackets(new S_Trade(trading_partner.getName()));
					trading_partner.sendPackets(new S_Trade(pc.getName()));
				}
			 } else {
				    if (c == 0){ // No
				    } else if (c == 1){ // Yes  
				     if(pc.getX() == 33420 && pc.getY() == 32799 && pc.getMapId() == 4
				     || pc.getX() == 33971 && pc.getY() == 33232 && pc.getMapId() == 4
				     || pc.getX() == 33969 && pc.getY() == 33230 && pc.getMapId() == 4){
				     pc.sendPackets(new S_Trade(npc.get_name()));
				     pc.sendPackets(new S_Trade(npc2.get_name())); 
				     pc.sendPackets(new S_Trade(npc3.get_name()));
				      }
				   }
				   break;
			 }
			break;


		case 321: // 또 부활하고 싶습니까? (Y/N)
			c = readC();
			L1PcInstance resusepc1 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc1 != null) { // 부활 스크롤
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					// pc.resurrect(pc.getLevel());
					// pc.setCurrentHp(pc.getLevel());
					pc.resurrect(pc.getMaxHp() / 2);
					pc.setCurrentHp(pc.getMaxHp() / 2);
					pc.startHpRegeneration();
					pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc1, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc1, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
				}
			}
			break;
			
		case 322: // 또 부활하고 싶습니까? (Y/N)
			c = readC();
			L1PcInstance resusepc2 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc2 != null) { // 축복된 부활 스크롤, 리자레크션, 그레이타리자레크션
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
					pc.resurrect(pc.getMaxHp());
					pc.setCurrentHp(pc.getMaxHp());
					pc.startHpRegeneration();
					pc.startMpRegeneration();
					pc.startMpRegenerationByDoll();
					pc.sendPackets(new S_Resurrection(pc, resusepc2, 0));
					pc.broadcastPacket(new S_Resurrection(pc, resusepc2, 0));
					pc.sendPackets(new S_CharVisualUpdate(pc));
					pc.broadcastPacket(new S_CharVisualUpdate(pc));
					// EXP 로스트 하고 있는, G-RES를 걸 수 있던, EXP 로스트 한 사망
					// 모두를 채우는 경우만 EXP 복구
					if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
						pc.resExp();
						pc.setExpRes(0);
						pc.setGres(false);
					}
				}
			}
			break;

		case 325: // 동물의 이름을 결정해 주세요：
			c = readC(); // ?
			name = readS();
			L1PetInstance pet = (L1PetInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			renamePet(pet, name);
			break;

		case 512: // 가의 이름은?
			c = readC(); // ?
			name = readS();
			int houseId = pc.getTempID();
			pc.setTempID(0);
			if (name.length() <= 16) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				house.setHouseName(name);
				HouseTable.getInstance().updateHouse(house); // DB에 기입해
			} else {
				pc.sendPackets(new S_ServerMessage(513)); // 가의 이름이 너무 깁니다.
			}
			break;

		case 622: 
            c = readC();   
   BuddyTable buddyTable = BuddyTable.getInstance();
   L1Buddy buddyList = buddyTable.getBuddyTable(pc.getId());
   L1PcInstance target2 = (L1PcInstance) L1World.getInstance().findObject(pc.getTempID());
   pc.setTempID(0);
   String name2 = pc.getName();
   if (target2 != null) { // 있다면
    if (c == 0) { // No
     target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 요청을 거절하였습니다."));
    } else if (c == 1) { // Yes
     buddyList.add(pc.getId(), name2);
     buddyTable.addBuddy(target2.getId(), pc.getId(), name2);
     target2.sendPackets(new S_SystemMessage(pc.getName() + "님이 친구 등록 되었습니다."));
     pc.sendPackets(new S_SystemMessage(target2.getName() + "님에게 친구 등록이 되었습니다."));
    }
   } else {
    target2.sendPackets(new S_SystemMessage("그러한 케릭명을 가진 사람이 없습니다."));
   }
   break;

		case 630: // %0%s가 당신과 결투를 바라고 있습니다.응합니까? (Y/N)
			c = readC();
			L1PcInstance fightPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getFightId());
			if (c == 0) {
				pc.setFightId(0);
				fightPc.setFightId(0);
				fightPc.sendPackets(new S_ServerMessage(631, pc.getName())); // %0%d가 당신과의 결투를 거절했습니다.
			} else if (c == 1) {
				fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL,
						fightPc.getFightId(), fightPc.getId()));
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, pc
						.getFightId(), pc.getId()));
			}
			break;

		case 653: // 이혼을 하면(자) 링은 사라져 버립니다.이혼을 바랍니까? (Y/N)
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				pc.setPartnerId(0);
				pc.save(); // DB에 캐릭터 정보를 기입한다
			}
			break;

		case 654: // %0%s당신과 결혼 하고 싶어하고 있습니다.%0과 결혼합니까? (Y/N)
			c = readC();
			L1PcInstance partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (partner != null) {
				if (c == 0) { // No
					partner.sendPackets(new S_ServerMessage( // %0%s는 당신과의 결혼을 거절했습니다.
							656, pc.getName()));
				} else if (c == 1) { // Yes
					pc.setPartnerId(partner.getId());
					pc.save();
					pc.sendPackets(new S_ServerMessage( // 모두의 축복 중(안)에서, 두 명의 결혼을 했습니다.
							790));
					pc.sendPackets(new S_ServerMessage(655, partner.getName()));
							// 축하합니다!%0과 결혼했습니다.
	                Announcements.getInstance().announceToAll(("축하합니다! "+pc.getName()+"님과 "+partner.getName()+"님이 결혼하셨습니다.")); 
					partner.setPartnerId(pc.getId());
					partner.save();
					partner.sendPackets(new S_ServerMessage( // 모두의 축복 중(안)에서, 두 명의 결혼을 했습니다.
							790));
					partner.sendPackets(new S_ServerMessage( // 축하합니다!%0과 결혼했습니다.
							655, pc.getName()));
				}
			}
			break;

		// 콜 크란
		case 729: // 군주가 부르고 있습니다. 소환에 응합니까? (Y/N)
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				callClan(pc);
			}
			break;

		case 738: // 경험치를 회복하려면%0의 아데나가 필요합니다. 경험치를 회복합니까?
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1 && pc.getExpRes() == 1) { // Yes
				int cost = 0;
				int level = pc.getLevel();
				int lawful = pc.getLawful();
				if (level < 45) {
					cost = level * level * 100;
				} else {
					cost = level * level * 200;
				}
				if (lawful >= 0) {
					cost = (cost / 2);
				}
				if (pc.getInventory(). consumeItem(L1ItemId.ADENA, cost)) {
					pc.resExp();
					pc.setExpRes(0);
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
				}
			}
			break;

		case 951: // 채팅 파티 초대를 허가합니까? (Y/N)
			c = readC();
			L1PcInstance chatPc = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (chatPc != null) {
				if (c == 0) { // No
					chatPc.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가 초대를 거부했습니다.
					pc.setPartyID(0);
				} else if (c == 1) { // Yes
					if (chatPc.isInChatParty()) {
						if (chatPc.getChatParty().isVacancy() || chatPc
								.isGm()) {
							chatPc.getChatParty().addMember(pc);
						} else {
							chatPc.sendPackets(new S_ServerMessage(417)); // 더 이상 파티 멤버를 받아들일 수 없습니다.
						}
					} else {
						L1ChatParty chatParty = new L1ChatParty();
						chatParty.addMember(chatPc);
						chatParty.addMember(pc);
						chatPc.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0가 파티에 들어갔습니다.
					}
				}
			}
			break;

		case 953: // 파티 초대를 허가합니까? (Y/N) - ACE
			c = readC();
			L1PcInstance target = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (target != null) {
				if (c == 0) // No
				{
					target.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가 초대를 거부했습니다.
					pc.setPartyID(0);
				} else if (c == 1) // Yes
				{
					if (target.isInParty()) {
						// 초대주가 파티중
						if (target.getParty().isVacancy() || target.isGm()) {
							// 파티에 빈 곳이 있다
							target.getParty().addMember(pc);
							pc.setAutoDivision(false);
							target.setAutoDivision(false);
						} else {
							// 파티에 빈 곳이 없다
							target.sendPackets(new S_ServerMessage(417)); // 더 이상 파티 멤버를 받아들일 수 없습니다.
						}
					} else {
						// 초대주가 파티중이 아니다
						L1Party party = new L1Party();
						party.addMember(target);
						party.addMember(pc);
						target.sendPackets(new S_ServerMessage(424, pc
								.getName())); // %0가 파티에 들어갔습니다.
						pc.setAutoDivision(false);
						target.setAutoDivision(false);
					}
				}
			}
			break;			

		case 954: // 분배파티 초대를 허가합니까? (Y/N) - ACE
			c = readC();
			L1PcInstance target1 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartyID());
			if (target1 != null) {
				if (c == 0) // No
				{
					target1.sendPackets(new S_ServerMessage(423, pc.getName())); // %0가 초대를 거부했습니다.
					pc.setPartyID(0);
				} else if (c == 1) // Yes
				{
					if (target1.isInParty()) {
						// 초대주가 파티중
						if (target1.getParty().isVacancy() || target1.isGm()) {
							// 파티에 빈 곳이 있다
							target1.getParty().addMember(pc);
							pc.setAutoDivision(true);
							target1.setAutoDivision(true);
						} else {
							// 파티에 빈 곳이 없다
							target1.sendPackets(new S_ServerMessage(417)); // 더 이상 파티 멤버를 받아들일 수 없습니다.
						}
					} else {
						// 초대주가 파티중이 아니다
						L1Party party = new L1Party();
						party.addMember(target1);
						party.addMember(pc);
						target1.sendPackets(new S_ServerMessage(424, pc.getName())); // %0가 파티에 들어갔습니다.
						pc.setAutoDivision(true);
						target1.setAutoDivision(true);
					}
				}
			}
			break;		
			
		case 479: // 어느 능력치를 향상시킵니까? (str, dex, int, con, wis, cha)
			if (readC() == 1) {
				String s = readS();
				if (!(pc.getLevel() - 50 > pc.getBonusStats())) {
					return;
				}
				/**
				 * 지금 스텟버그는
				 * 2케릭 Vm 아이템 복사가안되기에
				 * 아덴세탁도 물론안되기에 -_-;;
				 * 할게없어 2케릭으로 스텟버그를 사용하는것
				 * 
				 * 물론 스텟 벨류값을 잡아 시전하는것이다.
				 * 여기서 동시 접속을 하여 스텟을 중복으로 찍을수있기에
				 * 127까지 한도가 올라가기마련이다.
				 * 
				 * 여기서 2케릭 동접자가 같은계정에있다면 
				 * 강종처리 -_-;; 이럼막힘
				 * 
				 */
				if (pc.getOnlineStatus() != 1) {		// 127 스텟 버그 수정	
					pc.sendPackets(new S_Disconnect());
				    return;
				}
				// 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
				if(isTwoLogin(pc)) return;  // 다중케릭 스텟 버그 수정
				
				if (s.toLowerCase().equals("str".toLowerCase())) {
					// if(l1pcinstance.get_str() < 255)
					if (pc.getBaseStr() < 35) {
						pc.addBaseStr((byte) 1); // 소의 STR치에+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481));
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				} else if (s.toLowerCase().equals("dex".toLowerCase())) {
					// if(l1pcinstance.get_dex() < 255)
					if (pc.getBaseDex() < 35) {
						pc.addBaseDex((byte) 1); // 소의 DEX치에+1
						pc.resetBaseAc();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				} else if (s.toLowerCase().equals("con".toLowerCase())) {
					// if(l1pcinstance.get_con() < 255)
					if (pc.getBaseCon() < 35) {
						pc.addBaseCon((byte) 1); // 소의 CON치에+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				} else if (s.toLowerCase().equals("int".toLowerCase())) {
					// if(l1pcinstance.get_int() < 255)
					if (pc.getBaseInt() < 35) {
						pc.addBaseInt((byte) 1); // 소의 INT치에+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				} else if (s.toLowerCase().equals("wis".toLowerCase())) {
					// if(l1pcinstance.get_wis() < 255)
					if (pc.getBaseWis() < 35) {
						pc.addBaseWis((byte) 1); // 소의 WIS치에+1
						pc.resetBaseMr();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				} else if (s.toLowerCase().equals("cha".toLowerCase())) {
					// if(l1pcinstance.get_cha() < 255)
					if (pc.getBaseCha() < 35) {
						pc.addBaseCha((byte) 1); // 소의 CHA치에+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB에 캐릭터 정보를 기입한다
					} else {
						pc.sendPackets(new S_ServerMessage(481)); // 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요
						//pc.sendPackets(new S_SystemMessage("한 능력치의 최대값은 25입니다. 다른 능력치를 선택해 주세요."));
					}
				}
				if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) {
				    if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
				    		+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < 150) {
				          pc.sendPackets(new S_bonusstats(pc.getId(), 1));
				    }
				}
			}
			break;
		default:
			break;
		}
	}

	private void changeClan(ClientThread clientthread,
			L1PcInstance pc, L1PcInstance joinPc, int maxMember) {
		int clanId = pc.getClanid();
		String clanName = pc.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clanName);
		String clanMemberName[] = clan.getAllMembers();
		int clanNum = clanMemberName.length;

		int oldClanId = joinPc.getClanid();
		String oldClanName = joinPc.getClanname();
		L1Clan oldClan = L1World.getInstance().getClan(oldClanName);
		String oldClanMemberName[] = oldClan.getAllMembers();
		int oldClanNum = oldClanMemberName.length;
		if (clan != null && oldClan != null && joinPc.isCrown() && // 자신이 군주
				joinPc.getId() == oldClan.getLeaderId()) {
			if (maxMember < clanNum + oldClanNum) { // 빈 곳이 없다
				joinPc.sendPackets( // %0는 당신을 혈맹원으로서 받아들일 수가 없습니다.
						new S_ServerMessage(188, pc.getName()));
				return;
			}
			L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				clanMember[cnt].sendPackets(new S_ServerMessage(94, joinPc
						.getName())); // \f1%0이 혈맹의 일원으로서 받아들여졌습니다.
			}

			for (int i = 0; i < oldClanMemberName.length; i++) {
				L1PcInstance oldClanMember = L1World.getInstance().getPlayer(
						oldClanMemberName[i]);
				if (oldClanMember != null) { // 온라인중의 구크란 멤버
					oldClanMember.setClanid(clanId);
					oldClanMember.setClanname(clanName);
					// 혈맹 연합에 가입한 군주는 가디안
					// 군주가 데려 온 혈맹원은 본받아
					if (oldClanMember.getId() == joinPc.getId()) {
						oldClanMember.setClanRank(L1Clan.CLAN_RANK_GUARDIAN);
					} else {
						oldClanMember.setClanRank(L1Clan.CLAN_RANK_PROBATION);
					}
					try {
						// DB에 캐릭터 정보를 기입한다
						oldClanMember.save();
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
					clan.addMemberName(oldClanMember.getName());
					oldClanMember.sendPackets(new S_ServerMessage(95,
							clanName)); // \f1%0 혈맹에 가입했습니다.
				} else { // 오프 라인중의 구크란 멤버
					try {
						L1PcInstance offClanMember = CharacterTable
								.getInstance().restoreCharacter(
										oldClanMemberName[i]);
						offClanMember.setClanid(clanId);
						offClanMember.setClanname(clanName);
						offClanMember.setClanRank(L1Clan.CLAN_RANK_PROBATION);
						offClanMember.save(); // DB에 캐릭터 정보를 기입한다
						clan.addMemberName(offClanMember.getName());
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
			// 구크란 삭제
			String emblem_file = String.valueOf(oldClanId);
			File file = new File("emblem/" + emblem_file);
			file.delete();
			ClanTable.getInstance().deleteClan(oldClanName);
		}
	}
	/**
	 * 월드상에 있는 모든 캐릭의 계정을 비교해 같은 계정이 있다면 true 없다면 false
	 * @param c L1PcInstance
	 * @return 있다면 true
	 */
	private boolean isTwoLogin(L1PcInstance c) {
		boolean bool = false;
		for(L1PcInstance target : L1World.getInstance().getAllPlayers3()){
			if(c.getId() != target.getId() && !target.isPrivateShop()){
				if(c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
					bool = true;
					break;
				}
			}
		}
		return bool;
	}
	
	private static void renamePet(L1PetInstance pet, String name) {
		if (pet == null || name == null) {
			throw new NullPointerException();
		}

		int petItemObjId = pet.getItemObjId();
		L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetTable.isNameExists(name)) {
			pc.sendPackets(new S_ServerMessage(327)); // 같은 이름이 벌써 존재하고 있습니다.
			return;
		}
		L1Npc l1npc = NpcTable.getInstance().getTemplate(pet.getNpcId());
		if (!(pet.getName().equalsIgnoreCase(l1npc.get_name())) ) {
			pc.sendPackets(new S_ServerMessage(326)); // 한 번 결정한 이름은 변경할 수 없습니다.
			return;
		}
 		pet.setName(name);
		petTemplate.set_name(name);
		PetTable.getInstance().storePet(petTemplate); // DB에 기입해
		L1ItemInstance item = pc.getInventory().getItem(pet.getItemObjId());
		pc.getInventory().updateItem(item); 
		pc.sendPackets(new S_ChangeName(pet.getId(), name));
		pc.broadcastPacket(new S_ChangeName(pet.getId(), name));
	}

	private void callClan(L1PcInstance pc) {
		L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance()
				. findObject(pc.getTempID());
		pc.setTempID(0);
		if (callClanPc == null) {
			return;
		}
		if (!pc.getMap().isEscapable() && !pc.isGm()) {
			// 주변의 에너지가 텔레포트를 방해하고 있습니다. 그 때문에, 여기서 텔레포트는 사용할 수 없습니다.
			pc.sendPackets(new S_ServerMessage(647));
			L1Teleport.teleport(pc, pc.getLocation(), pc.getHeading(), false);
			return;
		}
		if (pc.getId() != callClanPc.getCallClanId()) {
			return;
		}
		/*if (callClanPc.isPrivateShop()) {  // 상점중이라면 <깃털 버그 방지>
			return;
		}	 */

		boolean isInWarArea = false;
		int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
		if (castleId != 0) {
			isInWarArea = true;
			if (WarTimeController.getInstance(). isNowWar(castleId)) {
				isInWarArea = false; // 전쟁 시간중은 기내에서도 사용 가능
			}
		}
		short mapId = callClanPc.getMapId();	
		if (mapId != 0 && mapId != 4 && mapId != 304 || isInWarArea || mapId == 350) {  
			// \f1당신의 파트너는 지금 당신이 갈 수 없는 곳에서 플레이중입니다.
			pc.sendPackets(new S_ServerMessage(547));
			return;
		}

		L1Map map = callClanPc.getMap();
		int callCalnX = callClanPc.getX();
		int callCalnY = callClanPc.getY();
		int locX = 0;
		int locY = 0;
		int heading = 0;
		switch (callClanPc.getCallClanHeading()) {
		case 0:
			locY = callCalnY - 1;
			heading = 4;
			break;

		case 1:
			locX = callCalnX + 1;
			locY = callCalnY - 1;
			heading = 5;
			break;

		case 2:
			locX = callCalnX + 1;
			heading = 6;
			break;

		case 3:
			locX = callCalnX + 1;
			locY = callCalnY + 1;
			heading = 7;
			break;

		case 4:
			locY = callCalnY + 1;
			heading = 0;
			break;

		case 5:
			locX = callCalnX - 1;
			locY = callCalnY + 1;
			heading = 1;
			break;

		case 6:
			locX = callCalnX - 1;
			heading = 2;
			break;

		case 7:
			locX = callCalnX - 1;
			locY = callCalnY - 1;
			heading = 3;
			break;

		default:
			break;
		}

		boolean isExsistCharacter = false;
		for (L1Object object : L1World.getInstance()
				. getVisibleObjects(callClanPc, 1)) {
			if (object instanceof L1Character) {
				L1Character cha = (L1Character) object;
				if (cha.getX() == locX && cha.getY() == locY
						&& cha.getMapId() == mapId) {
					isExsistCharacter = true;
					break;
				}
			}
		}

		if (locX == 0 && locY == 0 || ! map.isPassable(locX, locY)
				|| isExsistCharacter) {
			// 장애물이 있어 거기까지 이동할 수가 없습니다.
			pc.sendPackets(new S_ServerMessage(627));
			return;
		}
		L1Teleport.teleport(pc, locX, locY, mapId, heading, true, L1Teleport
				. CALL_CLAN);
	}

	@Override
	public String getType() {
		return C_ATTR;
	}
}