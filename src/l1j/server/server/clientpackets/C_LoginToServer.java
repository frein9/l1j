/* This program is free software; you can redistribute it and/or modify
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

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.Collection;
import java.sql.Statement;

import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_CastleMaster; 
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.SkillCheck; 
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ActiveSpells;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_Bookmarks;
import l1j.server.server.serverpackets.S_CharacterConfig;
import l1j.server.server.serverpackets.S_CharTitle;	 
import l1j.server.server.serverpackets.S_Disconnect; // ########## A129 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
import l1j.server.server.serverpackets.S_InvList;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_ShowKarma;
import l1j.server.server.serverpackets.S_ShowOrignalBonus;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconExp;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_Unknown1;
import l1j.server.server.serverpackets.S_Unknown2;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1GetBackRestart;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.datatables.BuddyTable;
import l1j.server.server.clientpackets.C_AddBuddy;
import l1j.server.server.templates.L1CharName;
import l1j.server.server.model.L1Buddy;
import l1j.server.server.serverpackets.S_UnityIcon;
import l1j.server.server.serverpackets.S_ElfIcon;
import l1j.server.server.SkillCheck;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_SkillIconItemExp;

import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket
//
    public class C_LoginToServer extends ClientBasePacket {

	private static final String C_LOGIN_TO_SERVER = "[C] C_LoginToServer";
	private static Logger _log = Logger.getLogger(C_LoginToServer.class.getName());
	
	private int _old_status; // 무버그상태 + 엘릭서
	private int _new_status; // 50 이상의 무버그 캐릭터의 상태
	private int _lvl_status; // 보너스 스테이터스 무버그상태
	private int _All_base;  // 현재 캐릭터의 베이스상태
	
	public C_LoginToServer(byte abyte0[], ClientThread client)
			throws FileNotFoundException, Exception {
		super(abyte0);

		String login = client.getAccountName();

		String charName = readS();

		if (client.getActiveChar() != null) {
			_log.info("동일 ID에서의 중복 접속 때문에(" + client.getHostname()
					+ ")(와)과의 접속을 강제 종료 했습니다. ");
			client.close();
			return;
		}

		L1PcInstance pc = L1PcInstance.load(charName);
		L1PcInstance OtherPc = L1World.getInstance().getPlayer(pc.getName());
		if (OtherPc != null && !OtherPc.isPrivateShop()) {
	         client.kick();
	         OtherPc.sendPackets(new S_Disconnect());
	          return;
	    }  
	    serchPc(pc, client);
	
		if (pc == null || !login.equals(pc.getAccountName())) {
			_log.info("무효인 로그인 리퀘스트: char=" + charName + " account=" + login
					+ " host=" + client.getHostname());
			client.close();
			return;
		}

		if (Config.LEVEL_DOWN_RANGE != 0) {
			if (pc.getHighLevel() - pc.getLevel() >= Config.LEVEL_DOWN_RANGE) {
				_log.info("레벨 다운의 허용 범위를 넘은 캐릭터의 로그인 리퀘스트: char="
						+ charName + " account=" + login + " host=" + client.getHostname());
				client.kick();
				return;
			}
		}
		_log.info("Character login: char=" + charName + " account=" + login
				+ " host=" + client.getHostname());
		System.out.println("Thread count: " + Thread.activeCount());
		System.out.println("──────────────");
		pc.setOnlineStatus(1);
		CharacterTable.updateOnlineStatus(pc);
		L1World.getInstance().storeObject(pc);

		pc.setNetConnection(client);
		pc.setPacketOutput(client);
		client.setActiveChar(pc);

		S_Unknown1 s_unknown1 = new S_Unknown1();
		pc.sendPackets(s_unknown1);
		S_Unknown2 s_unknown2 = (new S_Unknown2(3)); // ########## A96 EPU 전환 관련 원본 소스 주석 처리 ##########
		pc.sendPackets(s_unknown2); // #####

		bookmarks(pc);

		// restart처가 getback_restart 테이블로 지정되고 있으면(자) 이동시킨다
		GetBackRestartTable gbrTable = GetBackRestartTable.getInstance();
		L1GetBackRestart[] gbrList = gbrTable.getGetBackRestartTableList();
		for (L1GetBackRestart gbr : gbrList) {
			if (pc.getMapId() == gbr.getArea()) {
				pc.setX(gbr.getLocX());
				pc.setY(gbr.getLocY());
				pc.setMap(gbr.getMapId());
				break;
			}
		}

		// altsettings.properties로 GetBack가 true라면 거리에 이동시킨다
		if (Config.GET_BACK) {
			int[] loc = Getback.GetBack_Location(pc, true);
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
		}

		// 전쟁중의 기내에 있었을 경우, 성주 혈맹이 아닌 경우는 귀환시킨다.
		int castle_id = L1CastleLocation.getCastleIdByArea(pc);
		if (0 < castle_id) {
			if (WarTimeController.getInstance().isNowWar(castle_id)) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					if (clan.getCastleId() != castle_id) {
						// 성주 크란은 아니다
						int[] loc = new int[3];
						loc = L1CastleLocation.getGetBackLoc(castle_id);
						pc.setX(loc[0]);
						pc.setY(loc[1]);
						pc.setMap((short) loc[2]);
					}
				} else {
					// 크란에 소속해 없는 경우는 귀환
					int[] loc = new int[3];
					loc = L1CastleLocation.getGetBackLoc(castle_id);
					pc.setX(loc[0]);
					pc.setY(loc[1]);
					pc.setMap((short) loc[2]);
				}
			}
		}

		L1World.getInstance().addVisibleObject(pc);
		S_ActiveSpells s_activespells = new S_ActiveSpells(pc);
		pc.sendPackets(s_activespells);

		pc.beginGameTimeCarrier();

		S_OwnCharStatus s_owncharstatus = new S_OwnCharStatus(pc);
		pc.sendPackets(s_owncharstatus);

		S_MapID s_mapid = new S_MapID(pc.getMapId(), pc.getMap().isUnderwater());
		pc.sendPackets(s_mapid);

		S_OwnCharPack s_owncharpack = new S_OwnCharPack(pc);
		pc.sendPackets(s_owncharpack);
		
		S_ShowKarma s_showkarma = new S_ShowKarma(pc);
		pc.sendPackets(s_showkarma);
		
		S_ShowOrignalBonus s_showorignalbonus = new S_ShowOrignalBonus(pc);
		pc.sendPackets(s_showorignalbonus);
		
		//DG - ACE 
		pc.sendPackets(new S_PacketBox(S_PacketBox.INIT_DG, 0x0000));

		pc.sendPackets(new S_PacketBox(S_PacketBox.UPDATE_DG, pc.getDg()));

		// XXX 타이틀 정보는 S_OwnCharPack에 포함되므로 아마 불요
		S_CharTitle s_charTitle = new S_CharTitle(pc.getId(), pc.getTitle());
		pc.sendPackets(s_charTitle);
		pc.broadcastPacket(s_charTitle);

		pc.sendVisualEffectAtLogin(); // 크라운, 독, 수중등의 시각 효과를 표시

		pc.sendPackets(new S_Weather(L1World.getInstance().getWeather()));

		items(pc);
		skills(pc);
		buff(client, pc);
        pc.turnOnOffLight();
		//** 존재 버그 사용자 잡아보자 **
		  L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
		  if (jonje == null) {
		      pc.sendPackets(new S_SystemMessage("존재버그가 발견되어 게임을 강제종료 합니다."));
		      client.kick();
		      return;
		  }  
		//** 존재 버그 사용자 잡아보자 **

		if (!pc.isGm()) {
			checkStatusBug(pc); 
		} // 아인하사드의 축복  

int ainOutTime = Config.RATE_AIN_OUTTIME;
   if (pc.getLevel() >= 49) { 
      if (pc.getAinZone() == 1) {
          Calendar cal = Calendar.getInstance();
           long time1 = (cal.getTimeInMillis() - pc.getLastActive().getTime()) / 60000;
	
                  /**로그아웃시 저장되는 아인하사드 패킷타임**///
               if (time1 >= ainOutTime) {
                 long time2 = time1 / ainOutTime;

                     /**로그아웃시 저장되는 아인하사드 패킷타임**///
                  long time3 = time2 + pc.getAinPoint();
                    if (time3 >=1 && time3 <=200) {
                          pc.setAinPoint((int)time3);
                } else if (time3 > 200) {
                   pc.setAinPoint(200);
                }  
              }
            }
         }
if (pc.getLevel() >= 49) {  // 추가

  if (pc.getAinPoint() > 0 ){
   pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
  }

}  // 추가
  // 아인하사드의 축복 

if (pc.getCurrentHp() > 0) {
			pc.setDead(false);
			pc.setStatus(0);
		} else {
			pc.setDead(true);
			pc.setStatus(ActionCodes.ACTION_Die);
		}
//미혼 or 이혼상태이면서 결혼반지가 인벤이있으면 삭제
if (pc.getPartnerId() == 0){
	   pc.getInventory().checkItem(40901, 1); 
	   pc.getInventory().consumeItem(40901, 1);
	   pc.getInventory().checkItem(40902, 1); 
	   pc.getInventory().consumeItem(40902, 1);
	   pc.getInventory().checkItem(40903, 1); 
	   pc.getInventory().consumeItem(40903, 1);
	   pc.getInventory().checkItem(40904, 1); 
	   pc.getInventory().consumeItem(40904, 1);
	   pc.getInventory().checkItem(40905, 1); 
	   pc.getInventory().consumeItem(40905, 1);
	   pc.getInventory().checkItem(40906, 1); 
	   pc.getInventory().consumeItem(40906, 1);
	   pc.getInventory().checkItem(40907, 1); 
	   pc.getInventory().consumeItem(40907, 1);
	   pc.getInventory().checkItem(40908, 1); 
	   pc.getInventory().consumeItem(40908, 1);
	  }
		if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) {
			if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
					+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < 210) {
				pc.sendPackets(new S_bonusstats(pc.getId(), 1));
			}
		}

		if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
			pc.sendPackets(new S_CharacterConfig(pc.getId()));
		}

		serchSummon(pc);

		WarTimeController.getInstance().checkCastleWar(pc);

		if (pc.getClanid() != 0) { // 크란 소속중
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				if (pc.getClanid() == clan.getClanId() && // 크란을 해산해, 재차, 동명의 크란이 창설되었을 때의 대책
						pc.getClanname().toLowerCase().equals(
								clan.getClanName().toLowerCase())) {
					L1PcInstance[] clanMembers = clan.getOnlineClanMember();
					for (L1PcInstance clanMember : clanMembers) {
						if (clanMember.getId() != pc.getId()) {
							clanMember.sendPackets(new S_ServerMessage(843, pc
									.getName())); // 지금, 혈맹원의%0%s가 게임에 접속했습니다.
						}
					}

					// 전전쟁 리스트를 취득
					for (L1War war : L1World.getInstance().getWarList()) {
						boolean ret = war.CheckClanInWar(pc.getClanname());
						if (ret) { // 전쟁에 참가중
							String enemy_clan_name = war.GetEnemyClanName(pc
									.getClanname());
							if (enemy_clan_name != null) {
								// 당신의 혈맹이 현재_혈맹과 교전중입니다.
								pc.sendPackets(new S_War(8, pc.getClanname(),
										enemy_clan_name));
							}
							break;
						}
					}
				} else {
					pc.setClanid(0);
					pc.setClanname("");
					pc.setClanRank(0);
					pc.save(); // DB에 캐릭터 정보를 기입한다
				}
			}
		}

		if (pc.getPartnerId() != 0) { // 결혼중
			L1PcInstance partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getPartnerId());
			if (partner != null && partner.getPartnerId() != 0) {
				if (pc.getPartnerId() == partner.getId()
						&& partner.getPartnerId() == pc.getId()) {
					pc.sendPackets(new S_ServerMessage(548)); // 당신의 파트너는 지금 게임중입니다.
					partner.sendPackets(new S_ServerMessage(549)); // 당신의 파트너는 방금 로그인했습니다.
				}
			}
		}

		pc.startHpRegeneration();
		pc.startMpRegeneration();
		pc.startObjectAutoUpdate();
		client.CharReStart(false);
		pc.beginExpMonitor();
		pc.save(); // DB에 캐릭터 정보를 기입한다
		l1j.server.Leaf.list.add(pc.getName()); 
        String amount = String.valueOf(L1World.getInstance().getAllPlayers().size() + Config.WHOIS_CONTER);
        S_WhoAmount s_whoamount = new S_WhoAmount(amount);
        l1j.server.Leaf.tarea.append("\r\n[현재인원]:"+L1World.getInstance().getAllPlayers().size()+"명  입니다   "+pc.getName()+"님께서 접속 하셨습니다. IP:"+ client.getIp()+" 계정명:"+ client.getAccountName());	  
        //서버 접속 알림 운영자만 보임
     for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
     if(player.isGm()){
        player.sendPackets(new S_SystemMessage("\\fU" +pc.getName()+" 님이 접속. \\fR IP:"+ client.getIp()+" 계정:"+ client.getAccountName()));
       }
       } 
	    pc.sendPackets(new S_OwnCharStatus(pc));
 
		if (pc.getHellTime() > 0) {
			pc.beginHell(false);
		}
		  //지존소스추가
/*
   zizon(pc); 
   }
    private void zizon(L1PcInstance pc){
    Connection con33 = null;
    int q = 0;
    int i = 0;
    int x = pc.getExp();
    
    try {
     con33 = L1DatabaseFactory.getInstance().getConnection();
     Statement pstm22 = con33.createStatement();
     ResultSet rs22 = pstm22.executeQuery("SELECT `Exp`,`char_name` FROM `characters` WHERE AccessLevel = 0 ORDER BY `Exp` DESC");
     while (rs22.next()) {
      q++;
      if (!pc.isGm() && rs22.getInt("Exp") <= x) { // 영자일경우 제외
       break;
      }
     }
     if (q == 1) {
      L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU서버랭킹 1위 "+ pc.getName()+ " 님이 오셨습니다.")); //멘트는 서버에맞춰 적당하게 변경하세요
      pc.sendPackets(new S_CastleMaster(6, pc.getId()));
    if (! pc.getInventory().checkItem(555109)) { // 지존아이템체크부분
      L1ItemInstance item = pc.getInventory().storeItem( 555109, 1);//지급할지존아이템
      L1World.getInstance().broadcastPacketToAll(
        new S_CastleMaster(6, pc.getId()));
     }
      if (q == 2) {
      L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU서버랭킹 2위 "+ pc.getName()+ " 님이 등장."));
      pc.sendPackets(new S_CastleMaster(7, pc.getId()));
      L1World.getInstance().broadcastPacketToAll(
        new S_CastleMaster(7, pc.getId()));
     }
     if (q == 3) {
      L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY서버랭킹 3위 "+ pc.getName()+ " 님이 등장."));
      pc.sendPackets(new S_CastleMaster(8, pc.getId()));
      L1World.getInstance().broadcastPacketToAll(
        new S_CastleMaster(8, pc.getId()));
     }
  }
/////////////////////////////////랭킹1위에게만 아템사용 (삭제부분)///////////////

int test = q;
while(test>1){
if (test>= 2 ) {
 pc.getInventory().consumeItem(555109, 1);//랭킹1위아이템삭제
test--;
}
      if (q == 2) {
      L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fU서버랭킹 2위 "+ pc.getName()+ " 님이 등장."));
      pc.sendPackets(new S_CastleMaster(7, pc.getId()));
      L1World.getInstance().broadcastPacketToAll(
        new S_CastleMaster(7, pc.getId()));
     }
     if (q == 3) {
      L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fY서버랭킹 3위 "+ pc.getName()+ " 님이 등장."));
      pc.sendPackets(new S_CastleMaster(8, pc.getId()));
      L1World.getInstance().broadcastPacketToAll(
        new S_CastleMaster(8, pc.getId()));
}
}

/////////////////////////////////랭킹1위에게만 아템사용 (삭제부분)///////////////
     rs22.close();//여기부터 아래까지 리소스삭제부분 
     pstm22.close();
     con33.close();
    } catch (Exception e) { 
     // TODO: handle exception
    } */


		if (CheckMail(pc) > 0){
			pc.sendPackets(new S_SkillSound(pc.getId(), 1091));
			pc.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
		}
		if (pc.getMapId() == 5166) { // 스텟초기화 다시 시작
			StatInitialize(pc);
		}
	}
	
	private void StatInitialize(L1PcInstance pc) {		 // 스텟초기화 다시 시작	
		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.시작));
	}   

	private void items(L1PcInstance pc) {
		// DB로부터 캐릭터와 창고의 아이템을 읽어들인다
		CharacterTable.getInstance().restoreInventory(pc);

		pc.sendPackets(new S_InvList(pc.getInventory().getItems()));
	}

	private int CheckMail(L1PcInstance pc){
		int count = 0;
		Connection con = null;
		PreparedStatement pstm1 = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm1 = con
			.prepareStatement(" SELECT count(*) as cnt FROM letter where receiver = ? AND isCheck = 0");
			pstm1.setString(1, pc.getName());
			
			rs = pstm1.executeQuery();
			if (rs.next()) {
				count = rs.getInt("cnt");
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm1);
			SQLUtil.close(con);
		}
		
		return count;
	}
  // ########## A129 캐릭터 중복 로그인 버그 수정 [By 도우너] ##########
	private void  serchPc(L1PcInstance pc, ClientThread client) {
		Connection con = null;
		PreparedStatement pstm = null;  
		PreparedStatement pstm2 = null;
		ResultSet find = null;
		ResultSet find2 = null; 
		String[] FindPc = null; 
		int PcCount = 0;
		try {   
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT COUNT(account_name) FROM characters WHERE account_name=?");
			pstm.setString(1, pc.getAccountName());
			find = pstm.executeQuery();   
			if (find.next()) {
				PcCount = find.getInt(1);
			}   
			pstm2 = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?");
			pstm2.setString(1, pc.getAccountName());
			find2 = pstm2.executeQuery();
			if (find2.next()){
				FindPc = new String[PcCount];   
				for (int i = 0; i < PcCount; i++) {
					FindPc[i] = find2.getString(1);
					L1PcInstance OtherPc = L1World.getInstance().getPlayer(FindPc[i]);    
					if (OtherPc != null && !OtherPc.isPrivateShop()) {
						client.kick(); 
						OtherPc.sendPackets(new S_Disconnect());
						break;
					}      
					find2.next();
				}
			}
		} catch (SQLException e) {
			client.kick(); 
			_log.info("중복 로그인 버그 체크 오류!");
		} finally {
			SQLUtil.close(find2);
			SQLUtil.close(find);
			SQLUtil.close(pstm2);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
// ########## A129 캐릭터 중복 로그인 버그 수정  ##########

	private void bookmarks(L1PcInstance pc) {

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_teleport WHERE char_id=?  ORDER BY name ASC");
			pstm.setInt(1, pc.getId());

			rs = pstm.executeQuery();
			while (rs.next()) {
				L1BookMark bookmark = new L1BookMark();
				bookmark.setId(rs.getInt("id"));
				bookmark.setCharId(rs.getInt("char_id"));
				bookmark.setName(rs.getString("name"));
				bookmark.setLocX(rs.getInt("locx"));
				bookmark.setLocY(rs.getInt("locy"));
				bookmark.setMapId(rs.getShort("mapid"));
				S_Bookmarks s_bookmarks = new S_Bookmarks(bookmark.getName(),
				bookmark.getMapId(), bookmark.getId());
				pc.addBookMark(bookmark);
				pc.sendPackets(s_bookmarks);
			}

		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void skills(L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_skills WHERE char_obj_id=? ");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			int i = 0;
			int lv1 = 0;
			int lv2 = 0;
			int lv3 = 0;
			int lv4 = 0;
			int lv5 = 0;
			int lv6 = 0;
			int lv7 = 0;
			int lv8 = 0;
			int lv9 = 0;
			int lv10 = 0;
			int lv11 = 0;
			int lv12 = 0;
			int lv13 = 0;
			int lv14 = 0;
			int lv15 = 0;
			int lv16 = 0;
			int lv17 = 0;
			int lv18 = 0;
			int lv19 = 0;
			int lv20 = 0;
			int lv21 = 0;
			int lv22 = 0;
			int lv23 = 0;
			int lv24 = 0;
			int lv25 = 0;
			int lv26= 0;
			int lv27 = 0;
			int lv28 = 0;
			while (rs.next()) {
				int skillId = rs.getInt("skill_id");
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
				if (l1skills.getSkillLevel() == 1) {
					lv1 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 2) {
					lv2 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 3) {
					lv3 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 4) {
					lv4 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 5) {
					lv5 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 6) {
					lv6 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 7) {
					lv7 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 8) {
					lv8 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 9) {
					lv9 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 10) {
					lv10 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 11) {
					lv11 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 12) {
					lv12 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 13) {
					lv13 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 14) {
					lv14 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 15) {
					lv15 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 16) {
					lv16 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 17) {
					lv17 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 18) {
					lv18 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 19) {
					lv19 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 20) {
					lv20 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 21) {
					lv21 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 22) {
					lv22 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 23) {
					lv23 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 24) {
					lv24 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 25) {
					lv25 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 26) {
					lv26 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 27) {
					lv27 |= l1skills.getId();
				}
				if (l1skills.getSkillLevel() == 28) {
					lv28 |= l1skills.getId();
				}
				i = lv1 + lv2 + lv3 + lv4 + lv5 + lv6 + lv7 + lv8 + lv9 + lv10
						+ lv11 + lv12 + lv13 + lv14 + lv15 + lv16 + lv17 + lv18
				+ lv19 + lv20 + lv21 + lv22 + lv23 + lv24 + lv25 + lv26 + lv27 + lv28;
			
		        int objid = pc.getId();
                SkillCheck.getInstance().AddSkill(objid, skillId);//
         	}
			if (i > 0) {
				pc.sendPackets(new S_AddSkill(lv1, lv2, lv3, lv4, lv5, lv6,
						lv7, lv8, lv9, lv10, lv11, lv12, lv13, lv14, lv15,
						lv16, lv17, lv18, lv19, lv20, lv21, lv22, lv23, lv24, lv25, lv26, lv27, lv28));
				// _log.warning("여기 끊어 오군요＠직역");
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void serchSummon(L1PcInstance pc) {
		for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
			if (summon.getMaster().getId() == pc.getId()) {
				summon.setMaster(pc);
				pc.addPet(summon);
				for (L1PcInstance visiblePc : L1World.getInstance()
						.getVisiblePlayer(summon)) {
					visiblePc.sendPackets(new S_SummonPack(summon, visiblePc));
				}
			}
		}
	}
	private void checkStatusBug(L1PcInstance pc) {
	// 스테이터스 조작 방지코드 
    _All_base = pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon() + pc.getBaseWis() + pc.getBaseCha() + pc.getBaseInt(); // 캐릭터의 기본 스테이터스
    _lvl_status = pc.getHighLevel() - 50; // 무버그 보너스 스테이터스
  if (_lvl_status < 0)
       {_lvl_status = 0;
      }
     _old_status = 80 + pc.getElixirStats() + _lvl_status; // 케릭의 정확한 총 스테이터스 결과값.

  if (pc.getLevel() >= 1) {
    if (_old_status < _All_base) {
      pc.sendPackets(new S_SystemMessage("스테이터스 수치가 정상적이지 않습니다.")); 
      pc.sendPackets(new S_Disconnect()); //캐릭터를 월드에서 추방
   System.out.println("무버그 캐릭의 수치 : " + _old_status);
      System.out.println("현재 캐릭터의 수치 : " + _All_base);
      System.out.println("Status 버그 사용자 : " + pc.getName());
     }
     } 
    }
 // 스테이터스 조작 방지코드 

	private void buff(ClientThread clientthread, L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM character_buff WHERE char_obj_id=? ");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				int skillid = rs.getInt("skill_id");
				int remaining_time = rs.getInt("remaining_time");
				if (skillid == SHAPE_CHANGE) { // 변신
					int poly_id = rs.getInt("poly_id");
					L1PolyMorph.doPoly(pc, poly_id, remaining_time, L1PolyMorph.MORPH_BY_LOGIN);
					/** 포션 마법 버프 */
				} else if (skillid == STATUS_BRAVE) { // 용기
				if (pc.isElf()) {
					pc.sendPackets(new S_SkillBrave(pc.getId(), 3, remaining_time));   
                    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 3, 0));
				} else {
					pc.sendPackets(new S_SkillBrave(pc.getId(), 1, remaining_time));   
                    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
					}
					pc.setBraveSpeed(1);
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == STATUS_HASTE) { // 속도
					pc.sendPackets(new S_SkillHaste(pc.getId(), 1, remaining_time));
					pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
					pc.setMoveSpeed(1);
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == STATUS_BLUE_POTION) { // 마나
					pc.sendPackets(new S_SkillIconGFX(34, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == STATUS_CHAT_PROHIBITED) { // 채팅 금지
					pc.sendPackets(new S_SkillIconGFX(36, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == STATUS_RIBRAVE) { // 유그드라 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4));
					pc.setSkillEffect(skillid, remaining_time * 1000);
						
					/** 요리 마법 버프 */
				} else if (skillid >= COOKING_1_0_N && skillid <= COOKING_1_6_N
						|| skillid >= COOKING_1_0_S && skillid <= COOKING_1_6_S
						|| skillid >= COOKING_2_0_N && skillid <= COOKING_2_6_N
						|| skillid >= COOKING_2_0_S && skillid <= COOKING_2_6_S
						|| skillid >= COOKING_3_0_N && skillid <= COOKING_3_6_N
						|| skillid >= COOKING_3_0_S && skillid <= COOKING_3_6_S
						|| skillid >= COOKING_1_8_N && skillid <= COOKING_1_14_N
					    || skillid >= COOKING_1_14_S && skillid <= COOKING_1_14_S
					    || skillid >= COOKING_1_16_N && skillid <= COOKING_1_22_N
					    || skillid >= COOKING_1_16_S && skillid <= COOKING_1_22_S) { // 요리(디저트는 제외하다)
					L1Cooking.eatCooking(pc, skillid, remaining_time);		

					/** 일반 마법 전용 버프 (패킷구조대로 나열) */
				} else if (skillid == DECREASE_WEIGHT) { // 디크리즈 웨이트 (디크패킷은 먼가 이상하다?)
					pc.sendPackets(new S_UnityIcon(remaining_time/16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == DECAY_POTION) { // 디케이 포션 //
					pc.sendPackets(new S_UnityIcon(0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == SILENCE) { // 사일런스 //
					pc.sendPackets(new S_UnityIcon(0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == WEAKNESS) { // 위크니스 //
					pc.addDmgup(-5);
					pc.addHitup(-1);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == DISEASE) { // 디지즈 //
					pc.addDmgup(-6);
					pc.addAc(12);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == BERSERKERS) { // 버서커스 //
					pc.addAc(10);
					pc.addDmgup(5);
					pc.addHitup(2);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
			
					/** 다크 엘프 관련 버프 */
				} else if (skillid == VENOM_RESIST) { // 베놈 레지스트 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == DRESS_EVASION) { // 드레스 이베이젼 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == BLOODLUST) { // 블러드러스트 
				    pc.sendPackets(new S_SkillBrave(pc.getId(), 6, remaining_time)); 
				    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0)); 
				    pc.setBraveSpeed(1); 
				    pc.setSkillEffect(skillid, remaining_time * 1000);	
				
					/** 용기사 환술사 관련 버프 */
				} else if (skillid == CONSENTRATION) { // 컨센트레이션 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == INSIGHT) { // 인사이트 //
					pc.addStr((byte)1);
					pc.addDex((byte)1);
					pc.addCon((byte)1);
					pc.addInt((byte)1);
					pc.addCha((byte)1);
					pc.addWis((byte)1);
					pc.resetBaseMr();
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == PANIC) { // 패닉 //
					pc.addStr((byte)-1);
					pc.addDex((byte)-1);
					pc.addCon((byte)-1);
					pc.addInt((byte)-1);
					pc.addCha((byte)-1);
					pc.addWis((byte)-1);
					pc.resetBaseMr();
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == MOTALBODY) { // 모탈바디 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == HOUROFDEATH) { // 호러 오브 데스 //
					pc.addStr((byte) -5);
					pc.addInt((byte) -5);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == PEAR) { // 피어 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == PAYTIONS) { // 페이션스 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == GUARDBREAK) { // 가드 브레이크 //
					pc.addAc(15);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == DRAGON_SKIN) { // 드래곤 스킨 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);					
			
				    /** 컬러풀 패키지용 버프 */
				} else if (skillid == EXP_POTION) { // 천상의 물약 //
					pc.sendPackets(new S_SkillIconItemExp(remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == COLOR_A) { // 체력증강주문서 //
					pc.addMaxHp(50);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
					if (pc.isInParty()) { 
						pc.getParty().updateMiniHP(pc);
					}
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == COLOR_B) { // 마력증강주문서 //
					pc.addMaxMp(40);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == COLOR_C) { // 전투주문서 //
					pc.addDmgup(3);
					pc.addHitup(3);
					pc.addSp(3);
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
			
					/** 요정 정령 마법 관련 버프 */
				} else if (skillid == NATURES_TOUCH) { // 네이쳐스 터치 // 
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == WIND_SHACKLE) { // 윈드 셰클 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == ERASE_MAGIC) { // 이레이즈 매직 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == ADDITIONAL_FIRE) { // 어디셔널 파이어 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == ELEMENTAL_FALL_DOWN) { // 엘리멘탈 폴다운 //
					int playerAttr = pc.getElfAttr();
					int i = -50;
					switch (playerAttr) {
					case 0: pc.sendPackets(new S_ServerMessage(79)); break;
					case 1: pc.addEarth(i); pc.setAddAttrKind(1); break;
					case 2: pc.addFire(i); pc.setAddAttrKind(2); break;
					case 4: pc.addWater(i); pc.setAddAttrKind(4); break;
					case 8: pc.addWind(i); pc.setAddAttrKind(8); break;
					default: break;
					}
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == ELEMENTAL_FIRE) { // 엘리멘탈 파이어 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == STRIKER_GALE) { // 스트라이커 게일 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == SOUL_OF_FLAME) { // 소울 오브 프레임 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == POLLUTE_WATER) { // 폴루트 워터 //
					pc.sendPackets(new S_UnityIcon(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, remaining_time/4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					
					/** 요정 공통계열 전용 버프 */
				} else if (skillid == RESIST_MAGIC) { // 레지스트 매직
					pc.addMr(10);
					pc.sendPackets(new S_ElfIcon(remaining_time/16, 0, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == CLEAR_MIND) { // 클리어 마인드
					pc.addWis((byte) 3);
					pc.resetBaseMr();
					pc.sendPackets(new S_ElfIcon(0, remaining_time/16, 0, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == RESIST_ELEMENTAL) { // 레지스트 엘리멘탈
					pc.addWind(10);
					pc.addWater(10);
					pc.addFire(10);
					pc.addEarth(10);
					pc.sendPackets(new S_ElfIcon(0, 0, remaining_time/16, 0));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				} else if (skillid == ELEMENTAL_PROTECTION) { // 프로텍션 프롬 엘리멘탈
					int attr = pc.getElfAttr();
					if (attr == 1) {
						pc.addEarth(50);
					} else if (attr == 2) {
						pc.addFire(50);
					} else if (attr == 4) {
						pc.addWater(50);
					} else if (attr == 8) {
						pc.addWind(50);
					}
					pc.sendPackets(new S_ElfIcon(0, 0, 0, remaining_time/16));
					pc.setSkillEffect(skillid, remaining_time * 1000);
			
				} else {
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(clientthread.getActiveChar(),
							skillid, pc.getId(), pc.getX(), pc.getY(), null,
							remaining_time, L1SkillUse.TYPE_LOGIN);
				}
			}
		} catch (SQLException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	@Override
	public String getType() {
		return C_LOGIN_TO_SERVER;
	}
}
