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

import l1j.server.Base64;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.command.L1Commands;
import l1j.server.server.command.executor.L1CommandExecutor;
import l1j.server.server.datatables.AutoLoot;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TrapInstance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.MonsterInvasion;
import l1j.server.server.model.event.BugRace;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_Chainfo;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_OpCode_Test;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Serchdrop;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.templates.L1Command;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BLESS_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.BRAVE_AURA;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.CONSENTRATION;
import static l1j.server.server.model.skill.L1SkillId.DRAGON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.INSIGHT;
import static l1j.server.server.model.skill.L1SkillId.IRON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.MOTALBODY;
import static l1j.server.server.model.skill.L1SkillId.PAYTIONS;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;

// Referenced classes of package l1j.server.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory
//

public class GMCommands {
    private static Logger _log = Logger.getLogger(GMCommands.class.getName());

    boolean spawnTF = false;

    private static GMCommands _instance;

    private GMCommands() {
    }

    public static GMCommands getInstance() {
        if (_instance == null) {
            _instance = new GMCommands();
        }
        return _instance;
    }

    private String complementClassName(String className) {
        // . 하지만 포함되어 있으면 풀 패스라고 봐 그대로 돌려준다
        if (className.contains(".")) {
            return className;
        }

        // 디폴트 패키지명을 보완
        return "l1j.server.server.command.executor." + className;
    }

    private boolean executeDatabaseCommand(L1PcInstance pc, String name,
                                           String arg) {
        try {
            L1Command command = L1Commands.get(name);
            if (command == null) {
                return false;
            }
            if (pc.getAccessLevel() < command.getLevel()) {
                pc.sendPackets(new S_ServerMessage(74, "커멘드" + name)); // \f1%0은 사용할 수 없습니다.
                return true;
            }

            Class<?> cls = Class.forName(complementClassName(command
                    .getExecutorClassName()));
            L1CommandExecutor exe = (L1CommandExecutor) cls.getMethod(
                    "getInstance").invoke(null);
            exe.execute(pc, name, arg);
            _log.info(pc.getName() + "가." + name + " " + arg + "커멘드를 사용했습니다. ");
            return true;
        } catch (Exception e) {
            _log.log(Level.SEVERE, "error gm command", e);
        }
        return false;
    }

    private void reloadDB(L1PcInstance gm, String cmd) {
        try {
            DropTable.reload();

            ShopTable.reload();

            ItemTable.reload();
            gm.sendPackets(new S_SystemMessage("Table Update Complete..."));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".나비켓로드 라고 입력해 주세요."));
        }
    }

    public void handleCommands(L1PcInstance gm, String cmdLine) {
        StringTokenizer token = new StringTokenizer(cmdLine);
        // 최초의 공백까지가 커멘드, 그 이후는 공백을 단락으로 한 파라미터로서 취급한다
        String cmd = token.nextToken();
        String param = "";
        while (token.hasMoreTokens()) {
            param = new StringBuilder(param).append(token.nextToken()).append(
                    ' ').toString();
        }
        param = param.trim();

        // 데이타베이스화 된 커멘드
        if (executeDatabaseCommand(gm, cmd, param)) {
            return;
        }
        try {
            // 일반 플레이어에 개방하는 커멘드는 여기에 쓴다

            if (gm.getAccessLevel() < 100) {
                gm.sendPackets(new S_ServerMessage(74, "커멘드" + cmd)); // \f1%0은 사용할 수 없습니다.
                return;
            }

            // 모니터에 개방하는 커멘드는 여기에 쓴다

            if (gm.getAccessLevel() < 200) {
                gm.sendPackets(new S_ServerMessage(74, "커멘드" + cmd)); // \f1%0은 사용할 수 없습니다.
                return;
            }
            if (gm.getInventory().checkEquipped(300000)) {   // 운영자의 반지 착용했을때 운영자 명령어 사용가능

                if (cmd.equalsIgnoreCase("도움말")) {
                    showHelp(gm);
                } else if (cmd.equalsIgnoreCase("오토루팅")) {
                    autoloot(gm, param);
                } else if (cmd.equalsIgnoreCase("전체소환")) {
                    allrecall(gm);
                } else if (cmd.equalsIgnoreCase("혈맹소환")) {
                    allrecall(gm);
                } else if (cmd.equalsIgnoreCase("침공시작")) {
                    침공시작(gm);
                } else if (cmd.equalsIgnoreCase("속도")) {
                    speed(gm);
                } else if (cmd.equalsIgnoreCase("드랍리스트")) {
                    serchdroplist(gm, param);
                } else if (cmd.equalsIgnoreCase("소환")) {
                    recall(gm, param);
                } else if (cmd.equalsIgnoreCase("파티소환")) {
                    partyrecall(gm, param);
                } else if (cmd.equalsIgnoreCase("이동")) {
                    teleportTo(gm, param);
                } else if (cmd.equalsIgnoreCase("죽어라")) {
                    kill(gm, param);
                } else if (cmd.equalsIgnoreCase("부활")) {
                    ress(gm);
                } else if (cmd.equalsIgnoreCase("검색")) {
                    searchDatabase(gm, param);
                } else if (cmd.equalsIgnoreCase("아데나")) {
                    adena(gm, param);
                } else if (cmd.equalsIgnoreCase("출두")) {
                    moveToChar(gm, param);
                } else if (cmd.equalsIgnoreCase("투스폰")) {
                    tospawn(gm, param);
                } else if (cmd.equalsIgnoreCase("투명")) {
                    invisible(gm);
                } else if (cmd.equalsIgnoreCase("불투명")) {
                    visible(gm);
                } else if (cmd.equalsIgnoreCase("날씨")) {
                    changeWeather(gm, param);
                } else if (cmd.equalsIgnoreCase("귀환")) {
                    gmRoom(gm, param);
                } else if (cmd.equalsIgnoreCase("영구추방")) {
                    powerkick(gm, param);
                } else if (cmd.equalsIgnoreCase("추방")) {
                    kick(gm, param);
                } else if (cmd.equalsIgnoreCase("계정추방")) {
                    accbankick(gm, param);
                } else if (cmd.equalsIgnoreCase("버프")) {
                    burf(gm, param);
                } else if (cmd.equalsIgnoreCase("버프미")) {
                    buff(gm, param, true);
                } else if (cmd.equalsIgnoreCase("버프")) {
                    buff(gm, param, false);
                } else if (cmd.equalsIgnoreCase("가라")) {
                    nocall(gm, param);
                } else if (cmd.equalsIgnoreCase("올버프")) {
                    allBuff(gm);
                } else if (cmd.equalsIgnoreCase("몬스터")) {
                    spawn(gm, param);
                } else if (cmd.equalsIgnoreCase("엔피씨")) {
                    npcSpawn(gm, param, "npc");
                } else if (cmd.equalsIgnoreCase("몹스폰")) {
                    npcSpawn(gm, param, "mob");
                } else if (cmd.equalsIgnoreCase("변신")) {
                    polymorph(gm, param);
                } else if (cmd.equalsIgnoreCase("혈전시작")) {
                    StartWar(gm, param);
                } else if (cmd.equalsIgnoreCase("혈전종료")) {
                    StopWar(gm, param);
    	  /*} else if (cmd.equalsIgnoreCase("아이템셋트")) {
				makeItemSet(gm, param);*/
                } else if (cmd.equalsIgnoreCase("아이템")) {
                    givesItem(gm, param);
                } else if (cmd.equalsIgnoreCase("채금")) {
                    chatng(gm, param);
                } else if (cmd.equalsIgnoreCase("채팅")) {
                    chat(gm, param);
                } else if (cmd.equalsIgnoreCase("선물")) {
                    present(gm, param);
                } else if (cmd.equalsIgnoreCase("레벨선물")) {
                    lvPresent(gm, param);
                } else if (cmd.equalsIgnoreCase("바로종료")) {
                    shutdownNow();
                } else if (cmd.equalsIgnoreCase("종료취소")) {
                    shutdownAbort();
                } else if (cmd.equalsIgnoreCase("종료")) {
                    shutdown(gm, param);
                } else if (cmd.equalsIgnoreCase("리셋트랩")) {
                    resetTrap();
                } else if (cmd.equalsIgnoreCase("홈타운")) {
                    hometown(gm, param);
                } else if (cmd.equalsIgnoreCase("이펙")) {
                    gfxId(gm, param);
                } else if (cmd.equalsIgnoreCase("인벤")) {
                    invGfxId(gm, param);
                } else if (cmd.equalsIgnoreCase("액션")) {
                    action(gm, param);
                } else if (cmd.equalsIgnoreCase("밴아이피")) {
                    banIp(gm, param);
                } else if (cmd.equalsIgnoreCase("누구")) {
                    who(gm, param);
                } else if (cmd.equalsIgnoreCase("감시")) {
                    patrol(gm);
                } else if (cmd.equalsIgnoreCase("skick")) {
                    skick(gm, param);
                } else if (cmd.equalsIgnoreCase("피바")) {
                    hpBar(gm, param);
                } else if (cmd.equalsIgnoreCase("showtrap")) {
                    showTraps(gm, param);
                } else if (cmd.equalsIgnoreCase("reloadtrap")) {
                    reloadTraps();
                } else if (cmd.equalsIgnoreCase("r")) {
                    redo(gm, param);
                } else if (cmd.equalsIgnoreCase("f")) {
                    favorite(gm, param);
                } else if (cmd.equalsIgnoreCase("gm")) {
                    gm(gm);
                } else if (cmd.equalsIgnoreCase("인챈검사")) {
                    checkEnchant(gm, param);
                } else if (cmd.equalsIgnoreCase("아덴검사")) {
                    checkAden(gm, param);
                } else if (cmd.equalsIgnoreCase("검사")) {
                    chainfo(gm, param);
                } else if (cmd.equalsIgnoreCase("계정추가")) {
                    accountadd(gm, param);
                } else if (cmd.equalsIgnoreCase("암호변경")) { //## A112 암호변경 추가
                    changePassword(gm, param);
                } else if (cmd.equalsIgnoreCase("펫레이싱")) {
                    PetRace(gm);
                } else if (cmd.equalsIgnoreCase("버경시작")) { //명령어 부분 적당한 위치에 추가.
                    BugRace();
                } else if (cmd.equalsIgnoreCase("디스비")) { //운영자명령어디스비
                    disbi(gm);
                } else if (cmd.equalsIgnoreCase("설문")) {
                    question(gm, param);
                } else if (cmd.equalsIgnoreCase("결과")) {
                    result(gm, param);
                } else if (cmd.equalsIgnoreCase("뻥")) {
                    popcon(gm, param);
                } else if (cmd.equalsIgnoreCase("나비켓로드")) {
                    reloadDB(gm, param);

                }

                // ■■■■ 오퍼레이션 코드 해석용 ■■■■ 여기로부터
                else if (cmd.equalsIgnoreCase("opcid2")) {
                    opcId2(gm, param);
                } else if (cmd.equalsIgnoreCase("opcid1")) {
                    opcId1(gm, param);
                } else if (cmd.equalsIgnoreCase("opcid")) {
                    opcId(gm, param);
                } else if (cmd.equalsIgnoreCase("opc2")) {
                    opc2(gm, param);
                } else if (cmd.equalsIgnoreCase("opc1")) {
                    opc1(gm, param);
                } else if (cmd.equalsIgnoreCase("opc")) {
                    opc(gm, param);
                }
                // ■■■■ 오페레이션코드 해석용 ■■■■ 여기까지
            } else {
				/*gm.sendPackets(new S_SystemMessage(
						"코만드 " + cmd + " 는 존재하지 않습니다."));*/
                gm.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
                return;
            }
            if (!cmd.equalsIgnoreCase("r")) {
                _lastCmd = cmdLine;
            }
            //_log.info(gm.getName() + "가." + cmdLine + "코만드를 사용했습니다.");

        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void showHelp(L1PcInstance pc) {
        pc.sendPackets(new S_SystemMessage("-------------------<GM 명령어>---------------------"));
        pc.sendPackets(new S_SystemMessage(".셋팅 .서먼 .청소 .전체소환 .스킬추가 .속도 .레벨"));
        pc.sendPackets(new S_SystemMessage(".소환 .출두 .파티소환 .이동 .위치 .죽어라 .부활"));
        pc.sendPackets(new S_SystemMessage(".아데나 .투명 .불투명 .날씨 .귀환 .버프 .올버프"));
        pc.sendPackets(new S_SystemMessage(".추방 .영구추방 .계정추방 .몬스터 .엔피씨 .몹스폰"));
        pc.sendPackets(new S_SystemMessage(".변신 .아이템 .채금 .채팅 .누구 .선물 .레벨선물"));
        pc.sendPackets(new S_SystemMessage(".홈타운 .종료 .바로종료 .종료취소 .밴아이피 .정보"));
        pc.sendPackets(new S_SystemMessage(".검사 .감시 .아덴검사 .인챈검사 .계정추가.디스비"));
        pc.sendPackets(new S_SystemMessage(".피바 .펫레이싱 .암호변경 .드랍리스트 .뻥  .skick"));
        pc.sendPackets(new S_SystemMessage(".설문 .결과 .혈맹소환 .검색 .나비켓로드 .침공시작"));
        pc.sendPackets(new S_SystemMessage(".혈전시작 .혈전종료 .버경시작 .오토루팅 "));
        pc.sendPackets(new S_SystemMessage("---------------------------------------------------"));
    }

    private void BugRace() {
        BugRace.getInstance();
    }


    private void popcon(L1PcInstance gm, String cmd) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(cmd);
            String s = stringtokenizer.nextToken();
            Config.setParameterValue("Whoiscount", s);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".뻥 [숫자]을 입력 해주세요."));
        }
    }

    private void StartWar(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String clan_name1 = tok.nextToken();
            String clan_name2 = tok.nextToken();


            L1Clan clan1 = L1World.getInstance().getClan(clan_name1);
            L1Clan clan2 = L1World.getInstance().getClan(clan_name2);

            if (clan1 == null) {
                gm.sendPackets(new S_SystemMessage(clan_name1 + "혈맹이 존재하지 않습니다."));
                return;
            }

            if (clan2 == null) {
                gm.sendPackets(new S_SystemMessage(clan_name2 + "혈맹이 존재하지 않습니다."));
                return;
            }

            for (L1War war : L1World.getInstance().getWarList()) {
                if (war.CheckClanInSameWar(clan_name1, clan_name2) == true) {
                    gm.sendPackets(new S_SystemMessage("[" + clan_name1 + "]혈맹과 [" + clan_name2 + "]혈맹은 현재 전쟁 중 입니다."));
                    return;
                }
            }

            L1War war = new L1War();
            war.handleCommands(2, clan_name1, clan_name2); // 모의전 개시
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                pc.sendPackets(new S_SystemMessage("[" + clan_name1 + "]혈맹과 [" + clan_name2 + "]혈맹의 전쟁이 시작 되었습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".혈전시작 혈맹이름 혈맹이름"));
        }
    }

    private void StopWar(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String clan_name1 = tok.nextToken();
            String clan_name2 = tok.nextToken();


            L1Clan clan1 = L1World.getInstance().getClan(clan_name1);
            L1Clan clan2 = L1World.getInstance().getClan(clan_name2);

            if (clan1 == null) {
                gm.sendPackets(new S_SystemMessage(clan_name1 + "혈맹이 존재하지 않습니다."));
                return;
            }

            if (clan2 == null) {
                gm.sendPackets(new S_SystemMessage(clan_name2 + "혈맹이 존재하지 않습니다."));
                return;
            }

            for (L1War war : L1World.getInstance().getWarList()) {
                if (war.CheckClanInSameWar(clan_name1, clan_name2) == true) {
                    war.CeaseWar(clan_name1, clan_name2);
                    for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                        pc.sendPackets(new S_SystemMessage("[" + clan_name1 + "]혈맹과 [" + clan_name2 + "]혈맹의 전쟁이 종료 되었습니다."));
                    }
                    return;
                }
            }
            gm.sendPackets(new S_SystemMessage("[" + clan_name1 + "]혈맹과 [" + clan_name2 + "]혈맹은 현재 전쟁중이지 않습니다."));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".혈전종료 혈맹이름 혈맹이름"));
        }
    }

    private void serchdroplist(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String nameid = tok.nextToken();

            int itemid = 0;
            try {
                itemid = Integer.parseInt(nameid);
            } catch (NumberFormatException e) {
                itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                if (itemid == 0) {
                    gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않았습니다."));
                    return;
                }
            }
            gm.sendPackets(new S_Serchdrop(itemid));
        } catch (Exception e) {
            //   _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            gm.sendPackets(new S_SystemMessage(".드랍리스트 [itemid 또는 name]를 입력해 주세요."));
            gm.sendPackets(new S_SystemMessage("아이템 name을 공백없이 정확히 입력해야 합니다."));
            gm.sendPackets(new S_SystemMessage("ex) .드랍리스트 마법서(디스인티그레이트) -- > 검색 O"));
            gm.sendPackets(new S_SystemMessage("ex) .드랍리스트 디스 -- > 검색 X"));
        }
    }

    private void autoloot(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String type = tok.nextToken();
            if (type.equalsIgnoreCase("리로드")) {
                AutoLoot.getInstance().reload();
                gm.sendPackets(new S_SystemMessage("오토루팅 설정이 리로드 되었습니다."));
            } else if (type.equalsIgnoreCase("검색")) {
                java.sql.Connection con = null;
                PreparedStatement pstm = null;
                ResultSet rs = null;

                String nameid = tok.nextToken();
                try {
                    con = L1DatabaseFactory.getInstance().getConnection();
                    String strQry;
                    strQry = " Select e.item_id, e.name from etcitem e, autoloot l where l.item_id = e.item_id and name Like '%" + nameid + "%' ";
                    strQry += " union all " + " Select w.item_id, w.name from weapon w, autoloot l where l.item_id = w.item_id and name Like '%" + nameid + "%' ";
                    strQry += " union all " + " Select a.item_id, a.name from armor a, autoloot l where l.item_id = a.item_id and name Like '%" + nameid + "%' ";
                    pstm = con.prepareStatement(strQry);
                    rs = pstm.executeQuery();
                    while (rs.next()) {
                        gm.sendPackets(new S_SystemMessage("[" + rs.getString("item_id") + "] " + rs.getString("name")));
                    }
                } catch (Exception e) {
                } finally {
                    rs.close();
                    pstm.close();
                    con.close();
                }
            } else {
                String nameid = tok.nextToken();
                int itemid = 0;
                try {
                    itemid = Integer.parseInt(nameid);
                } catch (NumberFormatException e) {
                    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
                    if (itemid == 0) {
                        gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않습니다. "));
                        return;
                    }
                }

                L1Item temp = ItemTable.getInstance().getTemplate(itemid);
                if (temp == null) {
                    gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않습니다. "));
                    return;
                }

                if (type.equalsIgnoreCase("추가")) {
                    if (AutoLoot.getInstance().isAutoLoot(itemid)) {
                        gm.sendPackets(new S_SystemMessage("이미 오토루팅 목록에 있습니다."));
                        return;
                    }
                    AutoLoot.getInstance().storeId(itemid);
                    gm.sendPackets(new S_SystemMessage("오토루팅 항목에 추가 했습니다."));
                } else if (type.equalsIgnoreCase("삭제")) {
                    if (!AutoLoot.getInstance().isAutoLoot(itemid)) {
                        gm.sendPackets(new S_SystemMessage("오토루팅 항목에 해당 아이템이 없습니다."));
                        return;
                    }
                    gm.sendPackets(new S_SystemMessage("오토루팅 항목에서 삭제 했습니다."));
                    AutoLoot.getInstance().deleteId(itemid);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".오토루팅 리로드"));
            gm.sendPackets(new S_SystemMessage(".오토루팅 추가|삭제 itemid|name"));
            gm.sendPackets(new S_SystemMessage(".오토루팅 검색 name"));
        }
    }

    private void speed(L1PcInstance pc) {
        int objectId = pc.getId();
        try {
            int time = 9999 * 9999;

            // 치우침 이브 부여
            pc.setSkillEffect(L1SkillId.STATUS_BRAVE, time);
            pc.sendPackets(new S_SkillBrave(objectId, 1, 3600));
            pc.sendPackets(new S_SkillSound(objectId, 751));
            pc.broadcastPacket(new S_SkillSound(objectId, 751));
            pc.setBraveSpeed(1);

            // 헤이 파업 부여
            pc.setSkillEffect(L1SkillId.STATUS_HASTE, time);

            pc.sendPackets(new S_SkillHaste(objectId, 1, 3600));
            pc.sendPackets(new S_SkillSound(objectId, 191));
            pc.broadcastPacket(new S_SkillSound(objectId, 191));
            pc.setMoveSpeed(1);

        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(".속도 커멘드 에러"));
        }
    }

    private void 침공시작(L1PcInstance gm) {
        try {

            MonsterInvasion.getInstance().setMainY(gm.getY());
            MonsterInvasion.getInstance().setMainX(gm.getX());
            MonsterInvasion.getInstance().setMainMap(gm.getMapId());
            gm.sendPackets(new S_SystemMessage("공격좌표설정완료"));
            MonsterInvasion.getInstance().InvasionStartON();
            gm.sendPackets(new S_SystemMessage("침공시작Start"));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage("침공시작에러"));
        }
    }

    private void disbi(L1PcInstance pc) {                                               // 함수 설정 부분 입니다.

        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 20)) //안의 20은 범위입니다..화면전체에 다 뿌려라..
        {                                                                                         // 20 범위 내에 오브젝트를 찾아서
            if (obj instanceof L1MonsterInstance) {                                                                                       // 막약 오브젝트로 잡히는 것이 몬스터라면..
                L1NpcInstance npc = (L1NpcInstance) obj;

                npc.receiveDamage(pc, 200000);                                       // 화면안의 모든 몹에게 20만 데미지의 마법을 뿌린다.

                if (npc.getCurrentHp() <= 0) {
                    pc.sendPackets(new S_SkillSound(obj.getId(), 1815));      // 디스 마법 그래픽이 모든 몹에게 작렬...
                    pc.broadcastPacket(new S_SkillSound(obj.getId(), 1815));
                } else {
                    pc.sendPackets(new S_SkillSound(obj.getId(), 1815));
                    pc.broadcastPacket(new S_SkillSound(obj.getId(), 1815));
                }
            }
        }

    }

    private void adena(L1PcInstance pc, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int count = Integer.parseInt(para1);

            L1ItemInstance adena = pc.getInventory().storeItem(L1ItemId.ADENA,
                    count);
            if (adena != null) {
                pc.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(count).append("아데나를 생성했습니다.").toString()));
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage((new StringBuilder()).append(
                    ".아데나 [액수]를 입력 해주세요.").toString()));
        }
    }

    private void searchDatabase(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            int type = Integer.parseInt(tok.nextToken());
            String name = tok.nextToken();
            searchObject(gm, type, name);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".검색 [0~4] [name]을 입력 해주세요."));
            gm.sendPackets(new S_SystemMessage("0=etcitem, 1=weapon, 2=armor, 3=npc, 4=polymorphs"));
            gm.sendPackets(new S_SystemMessage("name을 정확히 모르거나 띄워쓰기 되어있는 경우는"));
            gm.sendPackets(new S_SystemMessage("'%'를 앞이나 뒤에 붙여 쓰십시오."));
        }
    }

    private void searchObject(L1PcInstance gm, int type, String name) {
        try {
            String str1 = null;
            String str2 = null;
            int count = 0;
            java.sql.Connection con = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            switch (type) {
                case 0: // etcitem
                    statement = con.prepareStatement("select item_id, name from etcitem where name Like '" + name + "'");
                    break;
                case 1: // weapon
                    statement = con.prepareStatement("select item_id, name from weapon where name Like '" + name + "'");
                    break;
                case 2: // armor
                    statement = con.prepareStatement("select item_id, name from armor where name Like '" + name + "'");
                    break;
                case 3: // npc
                    statement = con.prepareStatement("select npcid, name from npc where name Like '" + name + "'");
                    break;
                case 4: // polymorphs
                    statement = con.prepareStatement("select polyid, name from polymorphs where name Like '" + name + "'");
                    break;
                default:
                    break;
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                str1 = rs.getString(1);
                str2 = rs.getString(2);
                gm.sendPackets(new S_SystemMessage("id : [" + str1 + "], name : [" + str2 + "]"));
                count++;
            }
            rs.close();
            statement.close();
            con.close();
            gm.sendPackets(new S_SystemMessage("총 [" + count + "]개의 데이터가 검색되었습니다."));
        } catch (Exception e) {
        }
    }

    private void moveToChar(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                L1Teleport.teleport(gm, target.getX(), target.getY(), target
                        .getMapId(), 5, false);
                gm.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(pcName).append("님에게 이동했습니다.").toString()));
            } else {
                gm.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(pcName).append("님은 없습니다.").toString()));
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".출두 [캐릭터명]을 입력 해주세요."));
        }
    }

    private void resetTrap() {
        L1WorldTraps.getInstance().resetAllTraps();
    }

    private void hometown(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String para1 = st.nextToken();
            if (para1.equalsIgnoreCase("매일")) {
                HomeTownTimeController.getInstance().dailyProc();
            } else if (para1.equalsIgnoreCase("매달")) {
                HomeTownTimeController.getInstance().monthlyProc();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(
                    ".홈타운 [매일 또는 매달] 이라고 입력 해주세요."));
        }

    }

    private void opc(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_OpCode_Test(Integer.parseInt(param), 0, gm));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 0, gm)).getInfo()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void opc1(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_OpCode_Test(Integer.parseInt(param), 1, gm));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 1, gm)).getInfo()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void opc2(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_OpCode_Test(Integer.parseInt(param), 2, gm));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 2, gm)).getInfo()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void opcId(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_SystemMessage((new S_OpCode_Test(Integer
                    .parseInt(param), 0, gm)).getCode()));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 0, gm)).getCodeList()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void opcId1(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_SystemMessage((new S_OpCode_Test(Integer
                    .parseInt(param), 1, gm)).getCode()));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 1, gm)).getCodeList()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void opcId2(L1PcInstance gm, String param) {
        try {
            gm.sendPackets(new S_SystemMessage((new S_OpCode_Test(Integer
                    .parseInt(param), 2, gm)).getCode()));
        } catch (Exception ex) {
            try {
                gm.sendPackets(new S_SystemMessage(
                        (new S_OpCode_Test(0, 2, gm)).getCodeList()));
            } catch (Exception ex2) {
                gm
                        .sendPackets(new S_SystemMessage(
                                "S_OpCode_Test로 에러가 발생했습니다."));
            }
        }
    }

    private void shutdownAbort() {
        GameServer.getInstance().abortShutdown();
    }

    private void shutdownNow() {
        GameServer.getInstance().shutdown();
    }

    private void shutdown(L1PcInstance gm, String params) {
        try {
            int sec = 0;
            StringTokenizer st = new StringTokenizer(params);
            if (st.hasMoreTokens()) {
                String param1 = st.nextToken();
                sec = Integer.parseInt(param1, 10);
            }
            if (sec < 5) {
                sec = 5;
            }
            GameServer.getInstance().shutdownWithCountdown(sec);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".종료 시간(초)을 입력해 주세요."));
        }
    }

    private void npcSpawn(L1PcInstance gm, String param, String type) {
        String msg = null;

        try {
            int npcid = Integer.parseInt(param.trim());
            L1Npc template = NpcTable.getInstance().getTemplate(npcid);

            if (template == null) {
                msg = "해당하는 NPC가 발견되지 않습니다.";
                return;
            }
            if (type.equals("mob")) {
                if (!template.getImpl().equals("L1Monster")) {
                    msg = "지정한 NPC는 L1Monster가 아닙니다.";
                    return;
                }
                SpawnTable.storeSpawn(gm, template);
            } else if (type.equals("npc")) {
                NpcSpawnTable.getInstance().storeSpawn(gm, template);
            }
            mobspawn(gm, npcid, 0, false);
            msg = new StringBuilder().append(template.get_name()).append(
                    " (" + npcid + ") ").append("를 추가했습니다.").toString();
        } catch (Exception e) {
            msg = ".엔피씨 NPCID 라고 입력해 주세요.";
        } finally {
            if (msg != null) {
                gm.sendPackets(new S_SystemMessage(msg));
            }
        }
    }

    private void spawn(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String nameid = tok.nextToken();
            int count = 1;
            if (tok.hasMoreTokens()) {
                count = Integer.parseInt(tok.nextToken());
            }
            int randomrange = 0;
            if (tok.hasMoreTokens()) {
                randomrange = Integer.parseInt(tok.nextToken(), 10);
            }
            int npcid = 0;
            try {
                npcid = Integer.parseInt(nameid);
            } catch (NumberFormatException e) {
                npcid = NpcTable.getInstance().findNpcIdByNameWithoutSpace(
                        nameid);
                if (npcid == 0) {
                    gm.sendPackets(new S_SystemMessage("해당 NPC가 발견되지 않습니다."));
                    return;
                }
            }
            spawnTF = true; // .spawn 사용
            for (int k3 = 0; k3 < count; k3++) {
                mobspawn(gm, npcid, randomrange, false);
            }
            nameid = NpcTable.getInstance().getTemplate(npcid).get_name();
            gm.sendPackets(new S_SystemMessage(nameid + "(" + npcid + ") ("
                    + count + ")를 소환했습니다.(범위:" + randomrange + ")"));
            spawnTF = false;
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            gm.sendPackets(new S_SystemMessage(
                    ".몬스터 npcid|name [수] [범위] 라고 입력해 주세요."));
        }
    }

    private void clanrecall(L1PcInstance gm, String cmd) {

        String s = null;
        try {
            s = cmd.substring(5);
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".혈맹소환 혈맹이름으로 입력해 주세요."));
            return;
        }

        L1Clan clan = L1World.getInstance().getClan(s);
        if (clan != null) {
            String clan_member_name[] = clan.getAllMembers();
            try {
                int i;
                for (i = 0; i < clan_member_name.length; i++) { // 혈맹원 리콜
                    L1PcInstance target = L1World.getInstance().getPlayer(clan_member_name[i]);
                    if (target != null) { // 온라인중의 크란원
                        if (gm != null && target.getAccessLevel() != 200) {
                            recallnow(gm, target);
                        }
                    }
                }
            } catch (Exception e) {
                gm.sendPackets(new S_SystemMessage(".혈맹소환 커멘드 에러"));
                return;
            }
        } else {
            gm.sendPackets(new S_SystemMessage(s + "의 혈맹은 존재하지 않습니다."));
        }
    }

    private void changeWeather(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String s27 = stringtokenizer.nextToken();
            int weather = Integer.parseInt(s27);
            L1World world = L1World.getInstance();
            world.setWeather(weather);
            L1World.getInstance().broadcastPacketToAll(new S_Weather(weather));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".날씨 0~3, 16~19 라고 입력해 주세요."));
        }
    }

    private void visible(L1PcInstance gm) {
        try {
            gm.setGmInvis(false);
            gm.sendPackets(new S_Invis(gm.getId(), 0));
            // pc.broadcastPacket(new S_Invis(pc.get_objectId(),
            // 0));
            //gm.broadcastPacket(new S_OtherCharPacks(gm));
            L1World.getInstance().broadcastPacketToAll(new S_Invis(gm.getId(), 0)); // 추가
            gm.sendPackets(new S_SystemMessage("투명상태를 해제했습니다."));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".불투명 커멘드 에러"));
        }
    }

    private void invisible(L1PcInstance gm) {
        try {
            gm.setGmInvis(true);
            gm.sendPackets(new S_Invis(gm.getId(), 1));
            // pc.broadcastPacket(new S_Invis(pc.get_objectId(),
            // 1));
            //gm.broadcastPacket(new S_RemoveObject(gm));
            L1World.getInstance().broadcastPacketToAll(new S_Invis(gm.getId(), 1)); // 추가
            gm.sendPackets(new S_SystemMessage("투명상태가 되었습니다."));

        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".투명 커멘드 에러"));
        }
    }


    private void recall(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                recallnow(gm, target);
            } else {
                gm.sendPackets(new S_SystemMessage("그러한 캐릭터는 없습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".소환 캐릭터명으로 입력해 주세요."));
        }
    }

    private void allrecall(L1PcInstance gm) {
        try {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (!pc.isGm()) {
                    recallnow(gm, pc);
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".전체소환 커멘드 에러"));
        }

    }

    private void partyrecall(L1PcInstance pc, String pcName) {
        L1PcInstance target = L1World.getInstance().getPlayer(pcName);

        if (target != null) {
            L1Party party = target.getParty();
            if (party != null) {
                int x = pc.getX();
                int y = pc.getY() + 2;
                short map = pc.getMapId();
                L1PcInstance[] players = party.getMembers();
                for (L1PcInstance pc2 : players) {
                    try {
                        L1Teleport.teleport(pc2, x, y, map, 5, true);
                        pc2.sendPackets(new S_SystemMessage(
                                "게임 마스터에게 소환되었습니다."));
                    } catch (Exception e) {
                        _log.log(Level.SEVERE, "", e);
                    }
                }
            } else {
                pc.sendPackets(new S_SystemMessage("파티 멤버가 아닙니다."));
            }
        } else {
            pc.sendPackets(new S_SystemMessage("그러한 캐릭터는 없습니다."));
        }
    }

    private void recallnow(L1PcInstance gm, L1PcInstance target) {
        try {
            L1Teleport.teleportToTargetFront(target, gm, 2);
            gm.sendPackets(new S_SystemMessage((new StringBuilder()).append(
                    target.getName()).append("님을 소환했습니다.").toString()));
            target.sendPackets(new S_SystemMessage("게임 마스터에게 소환되었습니다."));
        } catch (Exception e) {
            _log.log(Level.SEVERE, "", e);
        }
    }

    private void polymorph(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();
            int polyid = Integer.parseInt(st.nextToken());

            L1PcInstance pc = L1World.getInstance().getPlayer(name);

            if (pc == null) {
                gm.sendPackets(new S_ServerMessage(73, name)); // \f1%0은 게임을 하고 있지 않습니다.
            } else {
                try {
                    L1PolyMorph.doPoly(pc, polyid, 7200,
                            L1PolyMorph.MORPH_BY_GM);
                } catch (Exception exception) {
                    gm.sendPackets(new S_SystemMessage(
                            ".변신 캐릭터명 그래픽ID 라고 입력해 주세요."));
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(
                    ".변신 캐릭터명 그래픽ID 라고 입력해 주세요."));
        }
    }

    private void chatng(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String name = st.nextToken();
            int time = Integer.parseInt(st.nextToken());

            L1PcInstance pc = L1World.getInstance().getPlayer(name);

            if (pc != null) {
                pc.setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED,
                        time * 60 * 1000);
                pc.sendPackets(new S_SkillIconGFX(36, time * 60));
                pc.sendPackets(new S_ServerMessage(286, String.valueOf(time))); // \f3게임에 적합하지 않는 행동이기 (위해)때문에, 향후%0분간 채팅을 금지합니다.
                gm.sendPackets(new S_ServerMessage(287, name)); // %0의 채팅을 금지했습니다.
                L1World.getInstance().broadcastPacketToAll(
                        new S_SystemMessage("" + name + "님은 현재 채팅금지 중입니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(
                    ".채금 캐릭터명 시간(분)이라고 입력해 주세요."));
        }
    }

    private void chat(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            if (st.hasMoreTokens()) {
                String flag = st.nextToken();
                String msg;
                if (flag.compareToIgnoreCase("켬") == 0) {
                    L1World.getInstance().set_worldChatElabled(true);
                    msg = "전체 채팅을 가능하게 했습니다.";
                } else if (flag.compareToIgnoreCase("끔") == 0) {
                    L1World.getInstance().set_worldChatElabled(false);
                    msg = "전체 채팅을 정지했습니다.";
                } else {
                    throw new Exception();
                }
                gm.sendPackets(new S_SystemMessage(msg));
            } else {
                String msg;
                if (L1World.getInstance().isWorldChatElabled()) {
                    msg = "현재 전체 채팅이 가능합니다. 채팅 끔 으로 정지할 수 있습니다.";
                } else {
                    msg = "현재 전체 채팅은 정지되어 있습니다. 채팅 켬 으로 가능하게 할 수 있습니다.";
                }
                gm.sendPackets(new S_SystemMessage(msg));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".채팅 켬 또는 끔으로 입력해주세요."));
        }
    }

    private void teleportTo(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int locx = Integer.parseInt(st.nextToken());
            int locy = Integer.parseInt(st.nextToken());
            short mapid;
            if (st.hasMoreTokens()) {
                mapid = Short.parseShort(st.nextToken());
            } else {
                mapid = pc.getMapId();
            }
            L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
            pc.sendPackets(new S_SystemMessage("좌표 " + locx + ", " + locy
                    + ", " + mapid + "로 이동했습니다."));
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(
                    ".이동 X좌표 Y좌표 [맵 ID] 라고 입력해 주세요."));
        }
    }

    private int _spawnId = 0;

    /**
     * GM커멘드.tospawn 로부터 불린다.지정한 spawnid의 좌표에 난다.
     */
    private void tospawn(L1PcInstance gm, String param) {
        try {
            if (param.isEmpty() || param.equals("+")) {
                _spawnId++;
            } else if (param.equals("-")) {
                _spawnId--;
            } else {
                StringTokenizer st = new StringTokenizer(param);
                _spawnId = Integer.parseInt(st.nextToken());
            }
            L1Spawn spawn = NpcSpawnTable.getInstance().getTemplate(_spawnId);
            if (spawn == null) {
                spawn = SpawnTable.getInstance().getTemplate(_spawnId);
            }
            if (spawn != null) {
                L1Teleport.teleport(gm, spawn.getLocX(), spawn.getLocY(), spawn
                        .getMapId(), 5, false);
                gm.sendPackets(new S_SystemMessage("spawnid(" + _spawnId
                        + ")의 원래로 납니다"));
            } else {
                gm.sendPackets(new S_SystemMessage("spawnid(" + _spawnId
                        + ")(은)는 발견되지 않습니다"));
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(
                    "Error    usage:.tospawn spawnid|+|-"));
        }
    }

	/*private void makeItemSet(L1PcInstance gm, String param) {
		try {
			String name = new StringTokenizer(param).nextToken();
			List<ItemSetItem> list = GMCommandsConfig.ITEM_SETS.get(name);
			if (list == null) {
				gm.sendPackets(new S_SystemMessage(name + " 미정의된 세트입니다"));
				return;
			}
			for (ItemSetItem item : list) {
				L1Item temp = ItemTable.getInstance().getTemplate(item.getId());
				if (!temp.isStackable() && 0 != item.getEnchant()) {
					for (int i= 0; i < item.getAmount(); i++) {
						L1ItemInstance inst = ItemTable.getInstance()
								.createItem(item.getId());
						inst.setEnchantLevel(item.getEnchant());
						gm.getInventory().storeItem(inst);
					}
				} else {
					gm.getInventory().storeItem(item.getId(), item.getAmount());
				}
			}
		} catch (Exception e) {
			gm.sendPackets(new S_SystemMessage(".아이템셋트 세트명으로 입력해 주세요."));
		}
	}*/

    private void givesItem(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String nameid = st.nextToken();
            int count = 1;
            if (st.hasMoreTokens()) {
                count = Integer.parseInt(st.nextToken());
            }
            int enchant = 0;
            if (st.hasMoreTokens()) {
                enchant = Integer.parseInt(st.nextToken());
            }
            int isId = 0;
            if (st.hasMoreTokens()) {
                isId = Integer.parseInt(st.nextToken());
            }
            int itemid = 0;
            try {
                itemid = Integer.parseInt(nameid);
            } catch (NumberFormatException e) {
                itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(
                        nameid);
                if (itemid == 0) {
                    gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않았습니다."));
                    return;
                }
            }
            L1Item temp = ItemTable.getInstance().getTemplate(itemid);
            if (temp != null) {
                if (temp.isStackable()) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            itemid);
                    item.setEnchantLevel(0);
                    item.setCount(count);
                    if (isId == 1) {
                        item.setIdentified(true);
                    }
                    if (gm.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        gm.getInventory().storeItem(item);
                        gm.sendPackets(new S_ServerMessage(403, // %0를 손에 넣었습니다.
                                item.getLogName() + "(ID:" + itemid + ")"));
                    }
                } else {
                    L1ItemInstance item = null;
                    int createCount;
                    for (createCount = 0; createCount < count; createCount++) {
                        item = ItemTable.getInstance().createItem(itemid);
                        item.setEnchantLevel(enchant);
                        if (isId == 1) {
                            item.setIdentified(true);
                        }
                        if (gm.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                            gm.getInventory().storeItem(item);
                        } else {
                            break;
                        }
                    }
                    if (createCount > 0) {
                        gm.sendPackets(new S_ServerMessage(403, // %0를 손에 넣었습니다.
                                item.getLogName() + "(ID:" + itemid + ")"));
                    }
                }
            } else {
                gm.sendPackets(new S_SystemMessage("지정 ID의 아이템은 존재하지 않습니다"));
            }
        } catch (Exception e) {
            //_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            gm.sendPackets(new S_SystemMessage(
                    ".아이템 [itemid 또는 name] [개수] [인챈트수] [감정 상태] 라고 입력해 주세요."));
        }
    }

    private void present(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            String account = st.nextToken();
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            L1Item temp = ItemTable.getInstance().getTemplate(itemid);
            if (temp == null) {
                gm.sendPackets(new S_SystemMessage("존재하지 않는 아이템 ID입니다."));
                return;
            }

            L1DwarfInventory.present(account, itemid, enchant, count);
            gm.sendPackets(new S_SystemMessage(temp.getNameId() + "를" + count
                    + "개 선물 했습니다.", true));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(
                    ".선물 [어카운트명] [아이템 ID] [인챈트수] [아이템수]를 입력 해주세요.(어카운트명을 *으로 하면 전체 지급)"));
        }
    }

    private void lvPresent(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int minlvl = Integer.parseInt(st.nextToken(), 10);
            int maxlvl = Integer.parseInt(st.nextToken(), 10);
            int itemid = Integer.parseInt(st.nextToken(), 10);
            int enchant = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);

            L1Item temp = ItemTable.getInstance().getTemplate(itemid);
            if (temp == null) {
                gm.sendPackets(new S_SystemMessage("존재하지 않는 아이템 ID입니다."));
                return;
            }

            L1DwarfInventory.present(minlvl, maxlvl, itemid, enchant, count);
            gm.sendPackets(new S_SystemMessage(temp.getName() + "를" + count
                    + "개 선물 했습니다.(Lv" + minlvl + "~" + maxlvl + ")"));
        } catch (Exception e) {
            gm
                    .sendPackets(new S_SystemMessage(
                            ".레벨선물 minlvl maxlvl 아이템ID 인챈트수 아이템수로 입력해 주세요."));
        }
    }

    private void kill(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            if (target != null) {
                target.setCurrentHp(0);
                target.death(null);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".죽어라 캐릭터명으로 입력해 주세요."));
        }
    }

    private void ress(L1PcInstance gm) {
        try {
            int objid = gm.getId();
            gm.sendPackets(new S_SkillSound(objid, 759));
            gm.broadcastPacket(new S_SkillSound(objid, 759));
            for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(gm)) {
                if (pc.getCurrentHp() == 0 && pc.isDead()) {
                    pc.sendPackets(new S_SystemMessage(
                            "운영자에 의해 소생을 받았습니다."));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 3944));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 3944));
                    // 축복된 부활 스크롤과 같은 효과
                    pc.setTempID(objid);
                    pc.sendPackets(new S_Message_YN(322, "")); // 또 부활하고 싶습니까? (Y/N)
                } else {
                    pc.sendPackets(new S_SystemMessage(
                            "운영자가 달래 주었습니다."));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 832));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 832));
                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".부활 커멘드 에러"));
        }
    }

    private void gmRoom(L1PcInstance gm, String room) {
        try {
            int i = 0;
            try {
                i = Integer.parseInt(room);
            } catch (NumberFormatException e) {
            }
            if (i == 1) {
                L1Teleport.teleport(gm, 32737, 32796, (short) 99, 5, false);
            } else if (i == 2) {
                L1Teleport.teleport(gm, 32644, 32955, (short) 0, 5, false);  //판도라
            } else if (i == 3) {
                L1Teleport.teleport(gm, 33429, 32814, (short) 4, 5, false);  //기란
            } else if (i == 4) {
                L1Teleport.teleport(gm, 32535, 32955, (short) 777, 5, false);  // 버땅 그신
            } else if (i == 5) {
                L1Teleport.teleport(gm, 32736, 32787, (short) 15, 5, false);  //캔트성
            } else if (i == 6) {
                L1Teleport.teleport(gm, 32735, 32788, (short) 29, 5, false);  //원다우드성
            } else if (i == 7) {
                L1Teleport.teleport(gm, 32572, 32826, (short) 64, 5, false);  //하이네성
            } else if (i == 8) {
                L1Teleport.teleport(gm, 32730, 32802, (short) 52, 5, false);  //기란성
            } else if (i == 9) {
                L1Teleport.teleport(gm, 32895, 32533, (short) 300, 5, false);  //아덴
            } else if (i == 10) {
                L1Teleport.teleport(gm, 32736, 32799, (short) 39, 5, false);  //감옥
            } else if (i == 11) {
                L1Teleport.teleport(gm, 32861, 32806, (short) 66, 5, false);  //지저성
            } else if (i == 12) {
                L1Teleport.teleport(gm, 33384, 32347, (short) 4, 5, false);  //용뼈
            } else if (i == 13) {
                L1Teleport.teleport(gm, 32738, 32797, (short) 509, 5, false);  //카오스대전
            } else if (i == 14) {
                L1Teleport.teleport(gm, 32866, 32640, (short) 501, 5, false);  //사탄의 늪
            } else if (i == 15) {
                L1Teleport.teleport(gm, 32603, 32766, (short) 506, 5, false);  //시야의놀이터
            } else if (i == 16) {
                L1Teleport.teleport(gm, 32769, 32827, (short) 610, 5, false);  //벗꽃;
            } else if (i == 17) {
                L1Teleport.teleport(gm, 34061, 32276, (short) 4, 5, false);  //벗꽃;
            } else {
                L1Location loc = GMCommandsConfig.ROOMS.get(room.toLowerCase());
                if (loc == null) {
                    gm.sendPackets(new S_SystemMessage(".1운영자방   2판도라   3기란   4버땅(그신)  5켄트성"));
                    gm.sendPackets(new S_SystemMessage(".6윈다우드성 7하이네성 8기란성 9아덴성 10 감옥 11지저성"));
                    gm.sendPackets(new S_SystemMessage(".12용뼈 13카오스대전 14사탄의늪 15시야의놀이터   "));
                    gm.sendPackets(new S_SystemMessage(".16벗꽃   "));
                    return;
                }
                L1Teleport.teleport(gm, loc.getX(), loc.getY(), (short) loc
                        .getMapId(), 5, false);
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".귀환 [1~16] 또는 .귀환 [장소명]을 입력 해주세요.(장소명은 GMCommands.xml을 참조)"));
        }
    }

    private void kick(L1PcInstance gm, String param) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(param);

            if (target != null) {
                gm.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(target.getName()).append("님을 추방 했습니다.")
                        .toString()));
                target.sendPackets(new S_Disconnect());
            } else {
                gm.sendPackets(new S_SystemMessage(
                        "그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".추방 캐릭터명으로 입력해 주세요."));
        }
    }

    private void skick(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);
            if (target != null) {
                gm.sendPackets(new S_SystemMessage((new StringBuilder())
                        .append(target.getName()).append("님을 추방 했습니다.")
                        .toString()));
                // SKT에 이동시킨다
                target.setX(33080);
                target.setY(33392);
                target.setMap((short) 4);
                target.sendPackets(new S_Disconnect());
                ClientThread targetClient = target.getNetConnection();
                targetClient.kick();
                _log.warning("GM의 추방명령에 의해(" + targetClient.getAccountName()
                        + ":" + targetClient.getHostname() + ")와의 접속을 강제 절단 했습니다.");
            } else {
                gm.sendPackets(new S_SystemMessage(
                        "그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".추방 캐릭터명으로 입력해 주세요."));
        }
    }

    private void powerkick(L1PcInstance gm, String pcName) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(pcName);

            IpTable iptable = IpTable.getInstance();
            if (target != null) {
                Account.ban(target.getAccountName());
                iptable.banIp(target.getNetConnection().getIp()); // BAN 리스트에 IP를 더한다
                L1World.getInstance().broadcastPacketToAll(new S_SystemMessage((new StringBuilder())
                        .append(target.getName()).append(" 님을 추방 했습니다.")
                        .toString()));
                target.sendPackets(new S_Disconnect());
            } else {
                gm.sendPackets(new S_SystemMessage(
                        "그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".영구추방 캐릭터명으로 입력해 주세요."));
        }
    }

    private void accbankick(L1PcInstance gm, String param) {
        try {
            L1PcInstance target = L1World.getInstance().getPlayer(param);

            if (target != null) {
                // 어카운트를 BAN 한다
                Account.ban(target.getAccountName());
                gm.sendPackets(new S_SystemMessage(target.getName()
                        + "님을 추방 했습니다."));
                target.sendPackets(new S_Disconnect());
            } else {
                gm.sendPackets(new S_SystemMessage(
                        "그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다."));
            }
        } catch (Exception e) {
            gm
                    .sendPackets(new S_SystemMessage(
                            ".계정추방 캐릭터명으로 입력해 주세요."));
        }
    }

    private void burf(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            int sprid = Integer.parseInt(stringtokenizer.nextToken());

            gm.sendPackets(new S_SkillSound(gm.getId(), sprid));
            gm.broadcastPacket(new S_SkillSound(gm.getId(), sprid));
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".버프 castgfx 라고 입력해 주세요."));
        }

    }

    private void buff(L1PcInstance gm, String args, boolean buffMe) {
        try {
            StringTokenizer tok = new StringTokenizer(args);
            int skillId = Integer.parseInt(tok.nextToken());
            int time = 0;
            if (tok.hasMoreTokens()) {
                time = Integer.parseInt(tok.nextToken());
            }

            L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

            ArrayList<L1PcInstance> players = new ArrayList<L1PcInstance>();
            if (buffMe) {
                players.add(gm);
            } else {
                players = L1World.getInstance().getVisiblePlayer(gm);
            }
            if (skill.getTarget().equals("buff")) {
                for (L1PcInstance pc : players) {
                    new L1SkillUse().handleCommands(gm, skillId, pc.getId(), pc
                                    .getX(), pc.getY(), null, time,
                            L1SkillUse.TYPE_SPELLSC);
                }
            } else if (skill.getTarget().equals("none")) {
                for (L1PcInstance pc : players) {
                    new L1SkillUse().handleCommands(pc, skillId, pc.getId(), pc
                                    .getX(), pc.getY(), null, time,
                            L1SkillUse.TYPE_GMBUFF);
                }
            } else {
                gm.sendPackets(new S_SystemMessage("buff계의 스킬이 아닙니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".버프 skillId time 라고 입력해 주세요."));
        }
    }

    /*private void allBuff(L1PcInstance gm, String args) {
        int[] allBuffSkill = { LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
                MEDITATION, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
                BERSERKERS, IMMUNE_TO_HARM, ADVANCE_SPIRIT,
                REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE,
                ENCHANT_VENOM, BURNING_SPIRIT, VENOM_RESIST,
                DOUBLE_BRAKE, UNCANNY_DODGE, DRESS_EVASION,
                GLOWING_AURA, BRAVE_AURA,
                RESIST_MAGIC, CLEAR_MIND, ELEMENTAL_PROTECTION,
                AQUA_PROTECTER, BURNING_WEAPON, IRON_SKIN,
                EXOTIC_VITALIZE, WATER_LIFE, ELEMENTAL_FIRE,
                SOUL_OF_FLAME, ADDITIONAL_FIRE };
        try {
            StringTokenizer st = new StringTokenizer(args);
            String name = st.nextToken();
            L1PcInstance pc = L1World.getInstance().getPlayer(name);
            if (pc == null) {
                gm.sendPackets(new S_ServerMessage(73, name)); // \f1%0은 게임을 하고 있지 않습니다.
                return;
            }

            speed(pc);
            L1PolyMorph.doPoly(pc, 5641, 7200);
            for (int i = 0; i < allBuffSkill.length; i++) {
                L1Skills skill = SkillsTable.getInstance()
                        .getTemplate(allBuffSkill[i]);
                new L1SkillUse().handleCommands(pc, allBuffSkill[i], pc
                        .getId(), pc.getX(), pc.getY(), null, skill
                        .getBuffDuration() * 1000, L1SkillUse.TYPE_GMBUFF);
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".올버프 캐릭터명으로 입력해 주세요."));
        }
    }
*/
    private void allBuff(L1PcInstance gm) {
        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
                IMMUNE_TO_HARM, ADVANCE_SPIRIT, BRAVE_AURA,
                BURNING_WEAPON, IRON_SKIN, ELEMENTAL_FIRE,
                SOUL_OF_FLAME, CONSENTRATION, PAYTIONS, INSIGHT, DRAGON_SKIN, MOTALBODY};
        try {
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
                L1SkillUse l1skilluse = new L1SkillUse();
                for (int i = 0; i < allBuffSkill.length; i++) {
                    l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                }
                pc.sendPackets(new S_SystemMessage("기분이 한결 좋아집니다."));
                pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동
            }
            gm.sendPackets(new S_SystemMessage("월드내 모든 유저들에게 올버프를 시전 하였습니다."));
        } catch (Exception exception19) {
            gm.sendPackets(new S_SystemMessage(".올버프 에러"));
        }
    }

    /// /가라 명령어////
    private void nocall(L1PcInstance gm, String param) {
        try {
            StringTokenizer tokenizer = new StringTokenizer(param);
            String pcName = tokenizer.nextToken();

            L1PcInstance target = null; // q
            target = L1World.getInstance().getPlayer(pcName);
            if (target != null) { //타겟
                L1Teleport.teleport(target, 33440, 32795, (short) 4, 5, true); /// 가게될 지점 (유저가떨어지는지점)
            } else {
                gm.sendPackets(new S_SystemMessage("접속중이지 않는 유저 ID 입니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".가라 (보낼케릭터명) 으로 입력해 주세요."));
        }
    }

    /// /가라 명령어////

    private void mobspawn(L1PcInstance gm, int i, int randomrange,
                          boolean isPineWand) {
        try {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(i);
            if (l1npc != null) {
                Object obj = null;
                try {
                    String s = l1npc.getImpl();
                    Constructor constructor = Class.forName(
                            "l1j.server.server.model.Instance." + s
                                    + "Instance").getConstructors()[0];
                    Object aobj[] = {l1npc};
                    L1NpcInstance npc = (L1NpcInstance) constructor
                            .newInstance(aobj);
                    npc.setId(IdFactory.getInstance().nextId());
                    npc.setMap(gm.getMapId());
                    if (randomrange == 0) {
                        if (gm.getHeading() == 0) {
                            npc.setX(gm.getX());
                            npc.setY(gm.getY() - 1);
                        } else if (gm.getHeading() == 1) {
                            npc.setX(gm.getX() + 1);
                            npc.setY(gm.getY() - 1);
                        } else if (gm.getHeading() == 2) {
                            npc.setX(gm.getX() + 1);
                            npc.setY(gm.getY());
                        } else if (gm.getHeading() == 3) {
                            npc.setX(gm.getX() + 1);
                            npc.setY(gm.getY() + 1);
                        } else if (gm.getHeading() == 4) {
                            npc.setX(gm.getX());
                            npc.setY(gm.getY() + 1);
                        } else if (gm.getHeading() == 5) {
                            npc.setX(gm.getX() - 1);
                            npc.setY(gm.getY() + 1);
                        } else if (gm.getHeading() == 6) {
                            npc.setX(gm.getX() - 1);
                            npc.setY(gm.getY());
                        } else if (gm.getHeading() == 7) {
                            npc.setX(gm.getX() - 1);
                            npc.setY(gm.getY() - 1);
                        }
                    } else {
                        int tryCount = 0;
                        do {
                            tryCount++;
                            npc.setX(gm.getX()
                                    + (int) (Math.random() * randomrange)
                                    - (int) (Math.random() * randomrange));
                            npc.setY(gm.getY()
                                    + (int) (Math.random() * randomrange)
                                    - (int) (Math.random() * randomrange));
                            if (npc.getMap().isInMap(npc.getLocation())
                                    && npc.getMap().isPassable(
                                    npc.getLocation())) {
                                break;
                            }
                            Thread.sleep(1);
                        } while (tryCount < 50);

                        if (tryCount >= 50) {
                            if (gm.getHeading() == 0) {
                                npc.setX(gm.getX());
                                npc.setY(gm.getY() - 1);
                            } else if (gm.getHeading() == 1) {
                                npc.setX(gm.getX() + 1);
                                npc.setY(gm.getY() - 1);
                            } else if (gm.getHeading() == 2) {
                                npc.setX(gm.getX() + 1);
                                npc.setY(gm.getY());
                            } else if (gm.getHeading() == 3) {
                                npc.setX(gm.getX() + 1);
                                npc.setY(gm.getY() + 1);
                            } else if (gm.getHeading() == 4) {
                                npc.setX(gm.getX());
                                npc.setY(gm.getY() + 1);
                            } else if (gm.getHeading() == 5) {
                                npc.setX(gm.getX() - 1);
                                npc.setY(gm.getY() + 1);
                            } else if (gm.getHeading() == 6) {
                                npc.setX(gm.getX() - 1);
                                npc.setY(gm.getY());
                            } else if (gm.getHeading() == 7) {
                                npc.setX(gm.getX() - 1);
                                npc.setY(gm.getY() - 1);
                            }
                        }
                    }

                    npc.setHomeX(npc.getX());
                    npc.setHomeY(npc.getY());
                    npc.setHeading(gm.getHeading());

                    L1World.getInstance().storeObject(npc);
                    L1World.getInstance().addVisibleObject(npc);
                    if (spawnTF == true) {
                        L1Object object = L1World.getInstance().findObject(
                                npc.getId());
                        L1NpcInstance newnpc = (L1NpcInstance) object;
                        newnpc.onNpcAI();
                        newnpc.turnOnOffLight();
                        newnpc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
                    }
                    if (isPineWand) {
                        L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc,
                                300000);
                        timer.begin();
                    }
                } catch (Exception e) {
                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception exception) {
        }
    }

    public void mobspawn(ClientThread client, int i, int randomrange,
                         boolean isPineWand) {
        mobspawn(client.getActiveChar(), i, randomrange, isPineWand);
    }

    private void gfxId(L1PcInstance gm, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int gfxid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            for (int i = 0; i < count; i++) {
                L1Npc l1npc = NpcTable.getInstance().getTemplate(45001);
                if (l1npc != null) {
                    String s = l1npc.getImpl();
                    Constructor constructor = Class.forName(
                            "l1j.server.server.model.Instance." + s
                                    + "Instance").getConstructors()[0];
                    Object aobj[] = {l1npc};
                    L1NpcInstance npc = (L1NpcInstance) constructor
                            .newInstance(aobj);
                    npc.setId(IdFactory.getInstance().nextId());
                    npc.setGfxId(gfxid + i);
                    npc.setTempCharGfx(0);
                    npc.setNameId("");
                    npc.setMap(gm.getMapId());
                    npc.setX(gm.getX() + i * 2);
                    npc.setY(gm.getY() + i * 2);
                    npc.setHomeX(npc.getX());
                    npc.setHomeY(npc.getY());
                    npc.setHeading(4);

                    L1World.getInstance().storeObject(npc);
                    L1World.getInstance().addVisibleObject(npc);
                }
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".이펙 id 출현시키는 수로 입력해 주세요."));
        }
    }

    private void invGfxId(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int gfxid = Integer.parseInt(st.nextToken(), 10);
            int count = Integer.parseInt(st.nextToken(), 10);
            for (int i = 0; i < count; i++) {
                L1ItemInstance item = ItemTable.getInstance().createItem(40005);
                item.getItem().setGfxId(gfxid + i);
                item.getItem().setName(String.valueOf(gfxid + i));
                pc.getInventory().storeItem(item);
            }
        } catch (Exception exception) {
            pc
                    .sendPackets(new S_SystemMessage(
                            ".인벤 id 출현시키는 수로 입력해 주세요."));
        }
    }

    private void action(L1PcInstance pc, String param) {
        try {
            StringTokenizer st = new StringTokenizer(param);
            int actId = Integer.parseInt(st.nextToken(), 10);
            pc.sendPackets(new S_DoActionGFX(pc.getId(), actId));
        } catch (Exception exception) {
            pc.sendPackets(new S_SystemMessage(".액션 actid 라고 입력해 주세요."));
        }
    }

    private void banIp(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            // IP를 지정
            String s1 = stringtokenizer.nextToken();

            // add/del를 지정(하지 않아도 OK)
            String s2 = null;
            try {
                s2 = stringtokenizer.nextToken();
            } catch (Exception e) {
            }

            IpTable iptable = IpTable.getInstance();
            boolean isBanned = iptable.isBannedIp(s1);

            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                if (s1.equals(pc.getNetConnection().getIp())) {
                    String msg = new StringBuilder().append("IP:").append(s1)
                            .append(" 로 접속중의 플레이어:").append(pc.getName())
                            .toString();
                    gm.sendPackets(new S_SystemMessage(msg));
                }
            }

            if ("add".equals(s2) && !isBanned) {
                iptable.banIp(s1); // BAN 리스트에 IP를 더한다
                String msg = new StringBuilder().append("IP:").append(s1)
                        .append(" 를 BAN IP에 등록했습니다.").toString();
                gm.sendPackets(new S_SystemMessage(msg));
            } else if ("del".equals(s2) && isBanned) {
                if (iptable.liftBanIp(s1)) { // BAN 리스트로부터 IP를 삭제한다
                    String msg = new StringBuilder().append("IP:").append(s1)
                            .append(" 를 BAN IP로부터 삭제했습니다.").toString();
                    gm.sendPackets(new S_SystemMessage(msg));
                }
            } else {
                // BAN의 확인
                if (isBanned) {
                    String msg = new StringBuilder().append("IP:").append(s1)
                            .append(" 는 BAN IP에 등록되어 있습니다.").toString();
                    gm.sendPackets(new S_SystemMessage(msg));
                } else {
                    String msg = new StringBuilder().append("IP:").append(s1)
                            .append(" 는 BAN IP에 등록되어 있지 않습니다.").toString();
                    gm.sendPackets(new S_SystemMessage(msg));
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(
                    ".밴아이피 IP주소 [ add | del ]라고 입력해 주세요."));
        }
    }

    private void who(L1PcInstance gm, String param) {
        try {
            Collection<L1PcInstance> players = L1World.getInstance()
                    .getAllPlayers();
            String amount = String.valueOf(players.size());
            S_WhoAmount s_whoamount = new S_WhoAmount(amount);
            gm.sendPackets(s_whoamount);

            // 온라인의 플레이어 리스트를 표시
            if (param.equalsIgnoreCase("전체")) {
                gm.sendPackets(new S_SystemMessage("-- 온라인의 플레이어 --"));
                StringBuffer buf = new StringBuffer();
                for (L1PcInstance each : players) {
                    buf.append(each.getName());
                    buf.append(" / ");
                    if (buf.length() > 50) {
                        gm.sendPackets(new S_SystemMessage(buf.toString()));
                        buf.delete(0, buf.length() - 1);
                    }
                }
                if (buf.length() > 0) {
                    gm.sendPackets(new S_SystemMessage(buf.toString()));
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".누구 또는 .누구 전체 라고 입력해 주세요."));
        }
    }

    private void checkEnchant(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int enlvl = Integer.parseInt(para1);
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                List<L1ItemInstance> enchant = pc.getInventory().getItems();
                for (int j = 0; j < enchant.size(); ++j) {
                    if (enchant.get(j).getEnchantLevel() >= enlvl)
                        gm.sendPackets(new S_SystemMessage(pc.getName() + " : "
                                + enchant.get(j).getEnchantLevel() + enchant.get(j).getName() + " 보유."));
                }
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".인챈검사 [인챈트 레벨]을 입력 해주세요.(전체 온라인 사용자 인벤토리의 지정 인챈트 레벨 이상 아이템을 검사)"));
        }
    }

    private void checkAden(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String para1 = stringtokenizer.nextToken();
            int money = Integer.parseInt(para1);
            for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
                L1ItemInstance adena = pc.getInventory().findItemId(40308);
                if (adena.getCount() >= money)
                    gm.sendPackets(new S_SystemMessage(pc.getName() + " : " + adena.getCount() + " 아데나 보유."));
            }
        } catch (Exception exception27) {
            gm.sendPackets(new S_SystemMessage(".아덴검사 [액수]를 입력 해주세요."));
        }
    }


    private void chainfo(L1PcInstance gm, String param) {
        try {
            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String s = stringtokenizer.nextToken();
            gm.sendPackets(new S_Chainfo(1, s));
        } catch (Exception exception21) {
            gm.sendPackets(new S_SystemMessage(".검사 [캐릭터명]을 입력 해주세요."));
        }
    }

    private void patrol(L1PcInstance gm) {
        gm.sendPackets(new S_PacketBox(S_PacketBox.CALL_SOMETHING));
    }

    // ########## 계정 생성 추가           #########
    private void accountadd(L1PcInstance gm, String param) {

        try {

            StringTokenizer stringtokenizer = new StringTokenizer(param);
            String LoginName = stringtokenizer.nextToken();
            String password = stringtokenizer.nextToken();

            Connection con = null;
            PreparedStatement pstm = null;
            PreparedStatement pstm2 = null;
            ResultSet find = null;
            String login = null;
            String _ip = "000.000.000.000";
            String _host = "000.000.000.000";
            String _lastactive = "2008-01-01 00:00:00";
            int _normal = 0;

            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("SELECT login FROM accounts WHERE login=?");
            pstm.setString(1, LoginName);
            find = pstm.executeQuery();

            if (find.next()) {
                login = find.getString(1);
            }

            if (login == null) {
                pstm2 = con.prepareStatement("INSERT INTO accounts SET login=?,password=?,lastactive=?,access_level=?,ip=?,host=?,banned=? ");
                pstm2.setString(1, LoginName);
                pstm2.setString(2, password);
                pstm2.setString(3, _lastactive);
                pstm2.setInt(4, _normal);//레벨
                pstm2.setString(5, _ip);
                pstm2.setString(6, _host);
                pstm2.setInt(7, _normal);
                pstm2.execute();

                con.close();
                pstm.close();
                pstm2.close();
                find.close();
                gm.sendPackets(new S_SystemMessage("ID: " + LoginName + "  PW: " + password + "  계정생성 완료!"));

            } else {
                con.close();
                pstm.close();
                find.close();
                gm.sendPackets(new S_SystemMessage(LoginName + " 동일한 계정이 존재 합니다"));
            }
        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".계정추가 계정명 비밀번호 를입력해주세요"));
        }
    }
// ########## 계정 생성 추가           #########


    public static boolean isHpBarTarget(L1Object obj) {
        if (obj instanceof L1MonsterInstance) {
            return true;
        }
        if (obj instanceof L1PcInstance) {
            return true;
        }
        if (obj instanceof L1SummonInstance) {
            return true;
        }
        if (obj instanceof L1PetInstance) {
            return true;
        }
        return false;
    }

    private void hpBar(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("on")) {
            gm.setSkillEffect(L1SkillId.GMSTATUS_HPBAR, 0);
        } else if (param.equalsIgnoreCase("off")) {
            gm.removeSkillEffect(L1SkillId.GMSTATUS_HPBAR);

            for (L1Object obj : gm.getKnownObjects()) {
                if (isHpBarTarget(obj)) {
                    gm.sendPackets(new S_HPMeter(obj.getId(), 0xFF));
                }
            }
        } else {
            gm.sendPackets(new S_SystemMessage(".피바 on 또는 off 이라고 입력해 주세요."));
        }
    }

    private void reloadTraps() {
        L1WorldTraps.reloadTraps();
    }

    private void showTraps(L1PcInstance gm, String param) {
        if (param.equalsIgnoreCase("on")) {
            gm.setSkillEffect(L1SkillId.GMSTATUS_SHOWTRAPS, 0);
        } else if (param.equalsIgnoreCase("off")) {
            gm.removeSkillEffect(L1SkillId.GMSTATUS_SHOWTRAPS);

            for (L1Object obj : gm.getKnownObjects()) {
                if (obj instanceof L1TrapInstance) {
                    gm.removeKnownObject(obj);
                    gm.sendPackets(new S_RemoveObject(obj));
                }
            }
        } else {
            gm.sendPackets(new S_SystemMessage(".showtrap on|off 라고 입력해 주세요."));
        }
    }

    private String _lastCmd = "";

    private void redo(L1PcInstance gm, String param) {
        try {
            if (_lastCmd.isEmpty()) {
                gm.sendPackets(new S_SystemMessage("기억하고 있는 커멘드가 없습니다"));
                return;
            }
            if (param.isEmpty()) {
                gm.sendPackets(new S_SystemMessage("커멘드 " + _lastCmd
                        + " (을)를 재실행합니다"));
                handleCommands(gm, _lastCmd);
            } else {
                // 인수를 바꾸어 실행
                StringTokenizer token = new StringTokenizer(_lastCmd);
                String cmd = token.nextToken() + " " + param;
                gm.sendPackets(new S_SystemMessage("커멘드 " + cmd + " 를 실행합니다."));
                handleCommands(gm, cmd);
            }
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            gm.sendPackets(new S_SystemMessage(".r 커멘드 에러"));
        }
    }

    private String _faviCom = "";

    private void favorite(L1PcInstance gm, String param) {
        try {
            if (param.startsWith("set")) {
                // 커멘드의 등록
                StringTokenizer st = new StringTokenizer(param);
                st.nextToken();
                if (!st.hasMoreTokens()) {
                    gm.sendPackets(new S_SystemMessage("커멘드가 하늘입니다."));
                    return;
                }
                StringBuilder cmd = new StringBuilder();
                String temp = st.nextToken(); // 커멘드 타입
                if (temp.equalsIgnoreCase("f")) {
                    gm.sendPackets(new S_SystemMessage("f 자신은 등록할 수 없습니다."));
                    return;
                }
                cmd.append(temp + " ");
                while (st.hasMoreTokens()) {
                    cmd.append(st.nextToken() + " ");
                }
                _faviCom = cmd.toString().trim();
                gm.sendPackets(new S_SystemMessage(_faviCom + " 를 등록했습니다."));
            } else if (param.startsWith("show")) {
                gm.sendPackets(new S_SystemMessage("현재의 등록 커멘드: " + _faviCom));
            } else if (_faviCom.isEmpty()) {
                gm.sendPackets(new S_SystemMessage("등록하고 있는 커멘드가 없습니다."));
            } else {
                StringBuilder cmd = new StringBuilder();
                StringTokenizer st = new StringTokenizer(param);
                StringTokenizer st2 = new StringTokenizer(_faviCom);
                while (st2.hasMoreTokens()) {
                    String temp = st2.nextToken();
                    if (temp.startsWith("%")) {
                        cmd.append(st.nextToken() + " ");
                    } else {
                        cmd.append(temp + " ");
                    }
                }
                while (st.hasMoreTokens()) {
                    cmd.append(st.nextToken() + " ");
                }
                gm.sendPackets(new S_SystemMessage(cmd + " 를 실행합니다."));
                handleCommands(gm, cmd.toString());
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".f set 커멘드명 "
                    + "| .f show | .f [인수] 라고 입력해 주세요."));
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    private void gm(L1PcInstance gm) {
        if (gm.isGm()) {
            gm.setGm(false);
            gm.sendPackets(new S_SystemMessage("setGm = false."));
        } else {
            gm.setGm(true);
            gm.sendPackets(new S_SystemMessage("setGm = true"));
        }
    }

    //## A112 암호변경 추가
    /* 암호 변경 소스 - 당신 */
    // 입력받은 암호의 인코딩 메소드 - Account.java 참조.
    private static String encodePassword(String rawPassword)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte buf[] = rawPassword.getBytes("UTF-8");
        buf = MessageDigest.getInstance("SHA").digest(buf);

        return Base64.encodeBytes(buf);
    }

    /* 실제 암호 변경 메소드 */
    private void to_Change_Passwd(L1PcInstance gm, L1PcInstance pc, String passwd) {
        try {
            String login = null;
            String password = null;
            java.sql.Connection con = null;
            con = L1DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            PreparedStatement pstm = null;

            password = encodePassword(passwd);

            statement = con.prepareStatement("select account_name from characters where char_name Like '" + pc.getName() + "'");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                login = rs.getString(1);
                pstm = con.prepareStatement("UPDATE accounts SET password=? WHERE login Like '" + login + "'");
                pstm.setString(1, password);
                pstm.execute();
                gm.sendPackets(new S_SystemMessage("-암호 변경정보- Account:[" + login + "] Password:[" + passwd + "]"));
                gm.sendPackets(new S_SystemMessage(pc.getName() + "의 암호 변경이 성공적으로 완료되었습니다."));
                pc.sendPackets(new S_SystemMessage("귀하의 계정 정보가 갱신 되었습니다."));
            }
            rs.close();
            pstm.close();
            statement.close();
            con.close();
        } catch (Exception e) {
        }
    }

    /* 입력받은 암호에 한글이 포함되지 않았는지 확인해 주는 메소드 */
    /* 실제로 암호가 한글로 바뀌어버리면 클라이언트에서는 입력할 방법이 없다. */
    private static boolean isDisitAlpha(String str) {
        boolean check = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))    // 숫자가 아니라면
                    && Character.isLetterOrDigit(str.charAt(i))    // 특수문자라면
                    && !Character.isUpperCase(str.charAt(i))    // 대문자가 아니라면
                    && !Character.isLowerCase(str.charAt(i))) {    // 소문자가 아니라면
                check = false;
                break;
            }
        }
        return check;
    }

    /* 암호 변경에 필요한 변수를 입력받는다. */
    private void changePassword(L1PcInstance gm, String param) {
        try {
            StringTokenizer tok = new StringTokenizer(param);
            String user = tok.nextToken();
            String passwd = tok.nextToken();

            if (passwd.length() < 4) {
                gm.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 짧습니다."));
                gm.sendPackets(new S_SystemMessage("최소 4자 이상 입력해 주십시오."));
                return;
            }

            if (passwd.length() > 12) {
                gm.sendPackets(new S_SystemMessage("입력하신 암호의 자릿수가 너무 깁니다."));
                gm.sendPackets(new S_SystemMessage("최대 12자 이하로 입력해 주십시오."));
                return;
            }

            if (isDisitAlpha(passwd) == false) {
                gm.sendPackets(new S_SystemMessage("암호에 허용되지 않는 문자가 포함 되어 있습니다."));
                return;
            }

            L1PcInstance target = L1World.getInstance().getPlayer(user);
            if (target != null) {
                to_Change_Passwd(gm, target, passwd);
            } else {
                gm.sendPackets(new S_SystemMessage("그런 이름을 가진 캐릭터는 없습니다."));
            }
        } catch (Exception e) {
            gm.sendPackets(new S_SystemMessage(".암호변경 [캐릭터명] [변경할 암호]를 입력 해주세요."));
        }
    }

    //## A112 암호변경 추가
    private void question(L1PcInstance gm, String pcName) {
        try {
            Config.setParameterValue1("Yes", 0);
            Config.setParameterValue1("No", 0);

            L1World.getInstance().broadcastPacketToAll(new S_Message_YN(622, pcName));

        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".설문 [내용]을 입력 해주세요."));
        }
    }

    private void result(L1PcInstance gm, String pcName) {
        try {
            int a = Config.Quest_Yes - Config.Quest_No;
            String b;
            if (a > 0) {
                b = "투표결과 *찬성*";
            } else if (a < 0) {
                b = "투표결과 *반대*";
            } else if (a == 0) {
                b = "투표결과 *동일*";
            } else {
                b = "투표결과 *비정상*";
            }
            L1World.getInstance().broadcastPacketToAll
                    (new S_SystemMessage("찬성: " + Config.Quest_Yes + "명 반대:" + Config.Quest_No + "명  " + b));

        } catch (Exception exception) {
            gm.sendPackets(new S_SystemMessage(".결과 를 입력 해주세요."));
        }
    }

    private void PetRace(L1PcInstance gm) {
        try {
            L1PetRace pe11 = new L1PetRace();
            if (!pe11.isStartGame()) {
                pe11.start(1); // 스타트
                L1World.getInstance().setPetRace(pe11);
            } else {
                gm.sendPackets(new S_SystemMessage("이미 펫 레이싱이 시작했습니다."));
            }
        } catch (Exception e) {
        }
    }
}
