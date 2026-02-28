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

import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import java.util.TimeZone;
import java.lang.reflect.Constructor;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.BuffShopTable;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CastleDoorSpawnTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1CDA;
import l1j.server.server.model.L1Inventory;  // 조우의 돌골렘 때문에 추가
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.datatables.CastleDoorSpawnTable;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PetMatch;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.Instance.L1CastleDoorInstance;
import l1j.server.server.serverpackets.S_ApplyAuction;
import l1j.server.server.serverpackets.S_AuctionBoardRead;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Deposit;
import l1j.server.server.serverpackets.S_DoorPack;
import l1j.server.server.serverpackets.S_Drawal;
import l1j.server.server.serverpackets.S_HouseMap;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetList;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_RetrieveElfList;
import l1j.server.server.serverpackets.S_RetrievePledgeList;
import l1j.server.server.serverpackets.S_SelectTarget;
import l1j.server.server.serverpackets.S_SellHouse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_ShopBuyList;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_TaxRate;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.templates.L1Town;

import static l1j.server.server.model.skill.L1SkillId.*;

import l1j.server.server.CrockController;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.model.L1PetMember;
import l1j.server.server.model.L1Racing;
import l1j.server.server.GiranController;
import l1j.server.server.serverpackets.S_SkillIconKillBoss;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.IdFactory;

public class C_NPCAction extends ClientBasePacket {

    private static final String C_NPC_ACTION = "[C] C_NPCAction";
    private static Logger _log = Logger
            .getLogger(C_NPCAction.class
                    .getName());
    private static Random _random = new Random();

    public C_NPCAction(byte abyte0[], ClientThread client) throws Exception {
        super(abyte0);
        int objid = readD();
        String s = readS();

        String s2 = null;
        if (s.equalsIgnoreCase("select") // 경매 게시판의 리스트를 선택
                || s.equalsIgnoreCase("map") // 아지트의 위치를 확인한다
                || s.equalsIgnoreCase("apply")) { // 경매에 참가한다
            s2 = readS();
        } else if (s.equalsIgnoreCase("ent")) {
            L1Object obj = L1World.getInstance().findObject(objid);
            if (obj != null && obj instanceof L1NpcInstance) {
                if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80088) {
                    s2 = readS();
                }
            }
        }

        int[] materials = null;
        int[] counts = null;
        int[] createitem = null;
        int[] createcount = null;

        String htmlid = null;
        String success_htmlid = null;
        String failure_htmlid = null;
        String[] htmldata = null;

        L1PcInstance pc = client.getActiveChar();
        L1PcInstance target;
        L1Object obj = L1World.getInstance().findObject(objid);
        if (obj != null) {
            if (obj instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                int difflocx = Math.abs(pc.getX() - npc.getX());
                int difflocy = Math.abs(pc.getY() - npc.getY());
                // 3 매스 이상 떨어졌을 경우 액션 무효
                if (difflocx > 3 || difflocy > 3) {
                    return;
                }
                npc.onFinalAction(pc, s);
            } else if (obj instanceof L1PcInstance) {
                target = (L1PcInstance) obj;
                if (s.matches("[0-9]+")) {
                    summonMonster(target, s);
                } else {
                    L1PolyMorph.handleCommands(target, s);
                }
                return;
            }
        } else {
            // _log.warning("object not found, oid " + i);
        }

        // XML화 된 액션
        L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
        if (action != null) {
            L1NpcHtml result = action.execute(s, pc, obj, readByte());
            if (result != null) {
                pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
            }
            return;
        }

        /*
         * 액션 개별 처리
         */
        if (s.equalsIgnoreCase("buy")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            // "sell"마셔 표시되어야할 NPC를 체크한다.
            if (isNpcSellOnly(npc)) {
                return;
            }
// 판매 리스트 표시
            pc.sendPackets(new S_ShopSellList(objid));
        } else if (s.equalsIgnoreCase("sell")) {
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
            if (npcid == 70523 || npcid == 70805) { // 라다- or 배심
                htmlid = "ladar2";
            } else if (npcid == 70537 || npcid == 70807) { // 퍼 인 or 핀
                htmlid = "farlin2";
            } else if (npcid == 70525 || npcid == 70804) { // 라이언 or 죠엘
                htmlid = "lien2";
            } else if (npcid == 50527 || npcid == 50505 || npcid == 50519
                    || npcid == 50545 || npcid == 50531 || npcid == 50529
                    || npcid == 50516 || npcid == 50538 || npcid == 50518
                    || npcid == 50509 || npcid == 50536 || npcid == 50520
                    || npcid == 50543 || npcid == 50526 || npcid == 50512
                    || npcid == 50510 || npcid == 50504 || npcid == 50525
                    || npcid == 50534 || npcid == 50540 || npcid == 50515
                    || npcid == 50513 || npcid == 50528 || npcid == 50533
                    || npcid == 50542 || npcid == 50511 || npcid == 50501
                    || npcid == 50503 || npcid == 50508 || npcid == 50514
                    || npcid == 50532 || npcid == 50544 || npcid == 50524
                    || npcid == 50535 || npcid == 50521 || npcid == 50517
                    || npcid == 50537 || npcid == 50539 || npcid == 50507
                    || npcid == 50530 || npcid == 50502 || npcid == 50506
                    || npcid == 50522 || npcid == 50541 || npcid == 50523
                    || npcid == 50620 || npcid == 50623 || npcid == 50619
                    || npcid == 50621 || npcid == 50622 || npcid == 50624
                    || npcid == 50617 || npcid == 50614 || npcid == 50618
                    || npcid == 50616 || npcid == 50615 || npcid == 50626
                    || npcid == 50627 || npcid == 50628 || npcid == 50629
                    || npcid == 50630 || npcid == 50631) { // 아지트의 NPC
                String sellHouseMessage = sellHouse(pc, objid, npcid);
                if (sellHouseMessage != null) {
                    htmlid = sellHouseMessage;
                }
            } else { // 일반 상인

                // 매입 리스트 표시
                pc.sendPackets(new S_ShopBuyList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("retrieve")) { // 「개인 창고：아이템을 받는다」
            if (pc.getLevel() >= 5) {
                pc.sendPackets(new S_RetrieveList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("retrieve-elven")) { // 「에르프 창고：짐을 받는다」
            if (pc.getLevel() >= 5 && pc.isElf()) {
                pc.sendPackets(new S_RetrieveElfList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("retrieve-pledge")) { // 「혈맹 창고：짐을 받는다」
            if (pc.getLevel() >= 5) {
                if (pc.getClanid() == 0) {
                    // \f1혈맹 창고를 사용하려면  혈맹에 가입하지 않으면 안됩니다.
                    pc.sendPackets(new S_ServerMessage(208));
                    return;
                }
                int rank = pc.getClanRank();
                if (rank != L1Clan.CLAN_RANK_PUBLIC
                        && rank != L1Clan.CLAN_RANK_GUARDIAN
                        && rank != L1Clan.CLAN_RANK_PRINCE) {
                    // 타이틀이 없는 혈맹원 혹은, 견습 혈맹원의 경우는, 혈맹 창고를 이용할 수 없습니다.
                    pc.sendPackets(new S_ServerMessage(728));
                    return;
                }
                if (rank != L1Clan.CLAN_RANK_PRINCE
                        && pc.getTitle().equalsIgnoreCase("")) {
                    // 타이틀이 없는 혈맹원 혹은, 견습 혈맹원의 경우는, 혈맹 창고를 이용할 수 없습니다.
                    pc.sendPackets(new S_ServerMessage(728));
                    return;
                }
                pc.sendPackets(new S_RetrievePledgeList(objid, pc));
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70012
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70019
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70031
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70054
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70065
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70070
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70075
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70084
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70096) {
            if (s.equalsIgnoreCase("room")) {
                if (pc.getInventory().checkItem(40312))
                    htmlid = "inn5";
                else if (pc.getInventory().findItemId(40308).getCount() >= 300) {
                    materials = (new int[]{
                            40308
                    });
                    counts = (new int[]{
                            300
                    });
                    createitem = (new int[]{
                            40312
                    });
                    createcount = (new int[]{
                            1
                    });
                    htmlid = "inn4";
                } else {
                    htmlid = "inn3";
                }
            } else if (s.equalsIgnoreCase("room")) {
                if (pc.getInventory().checkItem(40312))
                    htmlid = "inn5";
                else if (pc.getInventory().findItemId(40308).getCount() >= 600) {
                    materials = (new int[]{
                            40308
                    });
                    counts = (new int[]{
                            600
                    });
                    createitem = (new int[]{
                            49016
                    });
                    createcount = (new int[]{
                            1
                    });
                    htmlid = "inn4";
                } else {
                    htmlid = "inn3";
                }
            } else if (s.equalsIgnoreCase("room")) {
                int k1 = 0;
                try {
                    k1 = pc.getLawful();
                } catch (Exception exception) {
                }
                if (k1 >= 0) {
                    htmlid = "inn2";
                    htmldata = (new String[]{
                            "여관주인", "300"
                    });
                } else {
                    htmlid = "inn1";
                }
            } else if (s.equalsIgnoreCase("hall")) {
                int k1 = 0;
                int c1 = 0;
                try {
                    k1 = pc.getLawful();
                    c1 = pc.getClassId();
                } catch (Exception exception1) {
                }
                if (k1 >= 0) {
                    if (c1 == 0 || c1 == 1) {
                        htmlid = "inn4";
                        htmldata = (new String[]{
                                "여관주인", "600"
                        });
                    } else {
                        htmlid = "inn10";
                    }
                } else {
                    htmlid = "inn11";
                }
            } else if (s.equalsIgnoreCase("return")) {
                if (pc.getInventory().checkItem(40312)) {
                    int ct = pc.getInventory().findItemId(40312).getCount();
                    int cash = ct * 60;
                    materials = (new int[]{
                            40312
                    });
                    counts = (new int[]{
                            ct
                    });
                    createitem = (new int[]{
                            40308
                    });
                    createcount = (new int[]{
                            cash
                    });
                    htmlid = "inn20";
                    String count = Integer.toString(cash);
                    htmldata = (new String[]{
                            "여관주인", count
                    });
                } else if (pc.getInventory().checkItem(40312)) {
                    int ct = pc.getInventory().findItemId(40312).getCount();
                    int cash = ct * 120;
                    materials = (new int[]{
                            49016
                    });
                    counts = (new int[]{
                            ct
                    });
                    createitem = (new int[]{
                            40308
                    });
                    createcount = (new int[]{
                            cash
                    });
                    htmlid = "inn20";
                    String count = Integer.toString(cash);
                    htmldata = (new String[]{
                            "여관주인", count
                    });
                } else {
                    htmlid = "inn7";
                }
            } else if (s.equalsIgnoreCase("enter")) {
                int nowX = pc.getX();
                int nowY = pc.getY();
                short map = pc.getMapId();
                if (pc.getInventory().checkItem(40312)) {
                    if (map == 0)
                        L1Teleport.teleport(pc, 32746, 32803, (short) 16384, 5, false);
                    else if (map > 0)
                        if (nowX < 32641 && nowX > 32621 && nowY < 32770 && nowY > 32750)
                            L1Teleport.teleport(pc, 32744, 32803, (short) 17408, 5, false); //글말
                        else if (nowX < 32638 && nowX > 32618 && nowY < 33177 && nowY > 33157)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 20480, 5, false); //윈말 여관
                        else if (nowX < 33995 && nowX > 33975 && nowY < 33322 && nowY > 33302)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 19456, 5, false);//아덴
                        else if (nowX < 33447 && nowX > 33427 && nowY < 32799 && nowY > 32779)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 18432, 5, false);//기란
                        else if (nowX < 33615 && nowX > 33595 && nowY < 33285 && nowY > 33265)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 22528, 5, false);//하이네
                        else if (nowX < 33126 && nowX > 33106 && nowY < 33389 && nowY > 33369)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 21504, 5, false);//은기사
                        else if (nowX < 34078 && nowX > 34058 && nowY < 32264 && nowY > 32244)
                            L1Teleport.teleport(pc, 32745, 32803, (short) 24576, 5, false);//오렌
                } else if (pc.getInventory().checkItem(40312)) {
                    if (map == 0)
                        L1Teleport.teleport(pc, 32744, 32808, (short) 16896, 5, false);
                    else if (map > 0)
                        if (nowX < 32641 && nowX > 32621 && nowY < 32760 && nowY > 32740)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 18944, 5, false);
                        else if (nowX < 32638 && nowX > 32618 && nowY < 33177 && nowY > 33157)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 19968, 5, false);
                        else if (nowX < 33995 && nowX > 33975 && nowY < 33322 && nowY > 33302)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 20992, 5, false);
                        else if (nowX < 33447 && nowX > 33427 && nowY < 32799 && nowY > 32779)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 22016, 5, false);
                        else if (nowX < 33615 && nowX > 33595 && nowY < 33285 && nowY > 33265)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 23040, 5, false);
                        else if (nowX < 33126 && nowX > 33106 && nowY < 33389 && nowY > 33369)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 24064, 5, false);
                        else if (nowX < 34078 && nowX > 34058 && nowY < 32264 && nowY > 32244)
                            L1Teleport.teleport(pc, 32745, 32807, (short) 25088, 5, false);
                } else {
                    htmlid = "inn9";
                }
            }
        } else if (s.equalsIgnoreCase("get")) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            int npcId = npc.getNpcTemplate().get_npcId();
            // 쿠퍼 or 댄 햄
            if (npcId == 70099 || npcId == 70796) {
                L1ItemInstance item = pc.getInventory().storeItem(20081, 1); // 오일스킨 망토
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                pc.getQuest().set_end(L1Quest.QUEST_OILSKINMANT);
                htmlid = ""; // 윈도우를 지운다
            }
            // 타운 마스터：보수를 받는다
            else if (npcId == 70528 || npcId == 70546 || npcId == 70567
                    || npcId == 70594 || npcId == 70654 || npcId == 70748
                    || npcId == 70774 || npcId == 70799 || npcId == 70815
                    || npcId == 70860) {

                if (pc.getHomeTownId() > 0) {

                } else {

                }
            }
        } else if (s.equalsIgnoreCase("fix")) { // 무기를 수리한다

        } else if (s.equalsIgnoreCase("room")) { // 방을 빌린다

        } else if (s.equalsIgnoreCase("hall")
                && obj instanceof L1MerchantInstance) { // 홀을 빌린다

        } else if (s.equalsIgnoreCase("return")) { // 방·홀을 돌려준다

        } else if (s.equalsIgnoreCase("enter")) { // 방·홀에 들어온다

        } else if (s.equalsIgnoreCase("openigate")) { // 게이트키퍼 / 성문을 연다
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("closeigate")) { // 게이트키퍼 / 성문을 닫는다
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("askwartime")) { // 근위병 / 다음의 공성 싸움의 시간을 묻는다
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (npc.getNpcTemplate().get_npcId() == 60514) { // 켄트성근위병
                htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
                htmlid = "ktguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60560) { // 오크 근위병
                htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
                htmlid = "orcguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60552) { // 윈다웃드성근위병
                htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
                htmlid = "wdguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60524 || // 기란거리 입구 근위병(활)
                    npc.getNpcTemplate().get_npcId() == 60525 || // 기란거리 입구 근위병
                    npc.getNpcTemplate().get_npcId() == 60529) { // 기란성근위병
                htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
                htmlid = "grguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 70857) { // Heine성Heine 가이드
                htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
                htmlid = "heguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60530 || // 드워후성드워후가드
                    npc.getNpcTemplate().get_npcId() == 60531) {
                htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
                htmlid = "dcguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 60533 || // 에덴성가이드
                    npc.getNpcTemplate().get_npcId() == 60534) {
                htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
                htmlid = "adguard7";
            } else if (npc.getNpcTemplate().get_npcId() == 81156) { // 에덴 정찰병(디아드 요새)
                htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
                htmlid = "dfguard3";
            }
        } else if (s.equalsIgnoreCase("inex")) { // 수입/지출의 보고를 받는다
            // 잠정적으로 공금을 채팅 윈도우에 표시시킨다.
            // 메세지는 적당.
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) { // 성주 크란
                    L1Castle l1castle = CastleTable.getInstance()
                            .getCastleTable(castle_id);
                    pc.sendPackets(new S_ServerMessage(309, // %0의 정산 총액은%1아데나입니다.
                            l1castle.getName(), String.valueOf(l1castle
                            .getPublicMoney())));
                    htmlid = ""; // 윈도우를 지운다
                }
            }
        } else if (s.equalsIgnoreCase("tax")) { // 세율을 조절한다
            pc.sendPackets(new S_TaxRate(pc.getId()));
        } else if (s.equalsIgnoreCase("withdrawal")) { // 자금을 인출한다
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int castle_id = clan.getCastleId();
                if (castle_id != 0) { // 성주 크란
                    L1Castle l1castle = CastleTable.getInstance()
                            .getCastleTable(castle_id);
                    pc.sendPackets(new S_Drawal(pc.getId(), l1castle
                            .getPublicMoney()));
                }
            }
        } else if (s.equalsIgnoreCase("cdeposit")) { // 자금을 입금한다
            pc.sendPackets(new S_Deposit(pc.getId()));
        } else if (s.equalsIgnoreCase("archer")) { // 성벽위 용병의 고용
            pc.sendPackets(new S_SystemMessage("성벽의 용병을 배치 하였습니다."));
            htmlid = "";
        } else if (s.equalsIgnoreCase("employ")) { //  용병 고용
            htmlid = "archmonlist";
        } else if (s.equalsIgnoreCase("arrange")) { // 고용한 용병의 배치
            pc.sendPackets(new S_SystemMessage("성벽의 용병을 배치 하였습니다."));
            htmlid = "";
        } else if (s.equalsIgnoreCase("castlegate")) { // 성문을 관리한다
            repairGate(pc);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("encw")) { // 무기 전문가 / 무기의 강화 마법을 받는다
            if (pc.getWeapon() == null) {
                pc.sendPackets(new S_ServerMessage(79));
            } else {
                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                int castle_id = clan.getCastleId();
                if (pc.getLevel() <= 13 || castle_id != 0) {  // 13렙 이하 또는 성혈만 가능
                    for (L1ItemInstance item : pc.getInventory().getItems()) {
                        if (pc.getWeapon().equals(item)) {
                            L1SkillUse l1skilluse = new L1SkillUse();
                            l1skilluse.handleCommands(pc, L1SkillId.ENCHANT_WEAPON,
                                    item.getId(), 0, 0, null, 0,
                                    L1SkillUse.TYPE_SPELLSC);
                            break;
                        }
                    }
                }
            }
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("enca")) { // 방어용 기구 전문가 / 방어용 기구의 강화 마법을 받는다
            L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            int castle_id = clan.getCastleId();
            if (pc.getLevel() <= 13 || castle_id != 0) {  // 13렙 이하만 가능
                if (item != null) {
                    L1SkillUse l1skilluse = new L1SkillUse();
                    l1skilluse.handleCommands(pc, L1SkillId.BLESSED_ARMOR, item
                            .getId(), 0, 0, null, 0, L1SkillUse.TYPE_SPELLSC);
                }
            } else {
                pc.sendPackets(new S_ServerMessage(79));
            }
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("depositnpc")) { // 「동물을 맡긴다」
            L1NpcInstance npc = (L1NpcInstance) obj;
            int npcId = npc.getNpcTemplate().get_npcId();
            if (npcId == 70773 || npcId == 70544 || npcId == 70664
                    || npcId == 70836 || npcId == 70617 || npcId == 70749
                    || npcId == 70632 || npcId == 70873 || npcId == 70671
                    || npcId == 70055 || npcId == 70723 || npcId == 70943
                    || npcId == 71053 || npcId == 70532) {
                Object[] petList = pc.getPetList().values().toArray();
                for (Object petObject : petList) {
                    if (petObject instanceof L1PetInstance) { // 펫
                        L1PetInstance pet = (L1PetInstance) petObject;
                        pet.collect();
                        pc.getPetList().remove(pet.getId());
                        pet.deleteMe();
                    }
                }
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777801) { // npcid
            if (s.equalsIgnoreCase("A")) {  // 지룡의 마안
                if (pc.getInventory().checkItem(49417, 1) && pc.getInventory().checkItem(40308, 100000)) {

                    pc.getInventory().storeItem(49411, 1);
                    pc.getInventory().consumeItem(49417, 1); //봉인된마안과 아데나소비
                    pc.getInventory().consumeItem(40308, 100000);
                    pc.sendPackets(new S_SystemMessage("지룡의 마안을 얻었습니다."));
                } else {
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("B")) {  // 수룡의 마안
                if (pc.getInventory().checkItem(49418, 1) && pc.getInventory().checkItem(40308, 100000)) {

                    pc.getInventory().storeItem(49410, 1);
                    pc.getInventory().consumeItem(49418, 1); //보인된마안과 아데나소비
                    pc.getInventory().consumeItem(40308, 100000);
                    pc.sendPackets(new S_SystemMessage("수룡의 마안을 얻었습니다."));
                } else {
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("C")) {  // 화룡의 마안
                if (pc.getInventory().checkItem(49420, 1) && pc.getInventory().checkItem(40308, 100000)) {
                    pc.getInventory().storeItem(49413, 1);
                    pc.getInventory().consumeItem(49420, 1); //보인된마안과 아데나소비
                    pc.getInventory().consumeItem(40308, 100000);
                    pc.sendPackets(new S_SystemMessage("화룡의 마안을 얻었습니다."));
                } else {
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("D")) {  // 풍룡의 마안
                if (pc.getInventory().checkItem(49419, 1) && pc.getInventory().checkItem(40308, 100000)) {
                    pc.getInventory().storeItem(49412, 1);
                    pc.getInventory().consumeItem(49419, 1); //보인된마안과 아데나소비
                    pc.getInventory().consumeItem(40308, 100000);
                    pc.sendPackets(new S_SystemMessage("풍룡의 마안을 얻었습니다."));
                } else {
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("E")) { // 마안제작
                Random random = new Random();
                if (pc.getInventory().checkItem(49411, 1) && pc.getInventory().checkItem(49410, 1)
                        && pc.getInventory().checkItem(40308, 200000)) {
                    if (random.nextInt(10) > 6) { // 30%의 확률로 성공
                        pc.getInventory().consumeItem(49411, 1);
                        pc.getInventory().consumeItem(49410, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        pc.getInventory().storeItem(49415, 1);
                        pc.sendPackets(new S_SystemMessage("탄생의 마안을 얻었습니다."));
                    } else { // 실패의 경우 아이템만 사라짐
                        pc.getInventory().consumeItem(49411, 1);
                        pc.getInventory().consumeItem(49410, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        htmlid = "sherme5";
                    }
                } else { // 재료가 부족한 경우
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("F")) { // 마안제작
                Random random = new Random();
                if (pc.getInventory().checkItem(49415, 1) && pc.getInventory().checkItem(49412, 1)
                        && pc.getInventory().checkItem(40308, 200000)) {
                    if (random.nextInt(10) > 6) { // 30%의 확률로 성공
                        pc.getInventory().consumeItem(49415, 1);
                        pc.getInventory().consumeItem(49412, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        pc.getInventory().storeItem(49416, 1);
                        pc.sendPackets(new S_SystemMessage("형상의 마안을 얻었습니다."));
                    } else { // 실패의 경우 아이템만 사라짐
                        pc.getInventory().consumeItem(49415, 1);
                        pc.getInventory().consumeItem(49412, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        htmlid = "sherme5";
                    }
                } else { // 재료가 부족한 경우
                    htmlid = "sherme1";
                }
            } else if (s.equalsIgnoreCase("G")) { // 마안제작
                Random random = new Random();
                if (pc.getInventory().checkItem(49416, 1) && pc.getInventory().checkItem(49413, 1)
                        && pc.getInventory().checkItem(40308, 200000)) {
                    if (random.nextInt(10) > 6) { // 30%의 확률로 성공
                        pc.getInventory().consumeItem(49416, 1);
                        pc.getInventory().consumeItem(49413, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        pc.getInventory().storeItem(49414, 1);
                        pc.sendPackets(new S_SystemMessage("생명의 마안을 얻었습니다."));
                    } else { // 실패의 경우 아이템만 사라짐
                        pc.getInventory().consumeItem(49416, 1);
                        pc.getInventory().consumeItem(49413, 1);
                        pc.getInventory().consumeItem(40308, 200000);
                        htmlid = "sherme5";
                    }
                } else { // 재료가 부족한 경우
                    htmlid = "sherme1";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 778798) { // 카뮤이벤트프리즘
            if (s.equalsIgnoreCase("2")) { //카뮤
                if (pc.getInventory().checkItem(40308, 10000)) {
                    pc.getInventory().consumeItem(40308, 10000);
                    pc.getInventory().storeItem(555581, 1);
                    htmlid = "camus4";
                } else {
                    htmlid = "camus3";
                }
            }
            if (s.equalsIgnoreCase("1")) { // 카뮤
                if (pc.getInventory().checkItem(555580, 10)) {
                    pc.getInventory().consumeItem(555580, 10);
                    pc.getInventory().storeItem(555579, 1);

                    htmlid = "camus4";
                } else {
                    htmlid = "camus5";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777780) { // 드루가 베일
            if (s.equalsIgnoreCase("1")) { //베일
                if (pc.getInventory().checkItem(40308, 1000000)) {
                    pc.getInventory().consumeItem(40308, 1000000);
                    pc.getInventory().storeItem(555565, 1);
                    htmlid = "veil1";
                } else {
                    htmlid = "veil2";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777800) { // 숨겨진 용의 입구
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "dsecret1";
            } else if (s.equalsIgnoreCase("0")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32876, 32883, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("1")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32948, 32602, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("2")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32794, 32593, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("3")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32819, 32811, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("4")) {
                htmlid = "";
                L1Teleport.teleport(pc, 33003, 32733, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("5")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32710, 32661, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("6")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32720, 32755, (short) 1002, 5, true);
            }
            if (s.equalsIgnoreCase("7")) {
                htmlid = "";
                L1Teleport.teleport(pc, 32986, 32631, (short) 1002, 5, true);
            }


            // 수상한 텔레포터
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 778807) {
            if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(41159, 1)) {
                    pc.getInventory().consumeItem(41159, 1);
                    htmlid = "";
                    L1Teleport.teleport(pc, 34061, 32276, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkItem(41159, 1)) {
                    pc.getInventory().consumeItem(41159, 1);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33705, 32504, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(41159, 2)) {
                    pc.getInventory().consumeItem(41159, 2);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33443, 32797, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("e")) {
                if (pc.getInventory().checkItem(41159, 3)) {
                    pc.getInventory().consumeItem(41159, 3);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33614, 33253, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("f")) {
                if (pc.getInventory().checkItem(41159, 3)) {
                    pc.getInventory().consumeItem(41159, 3);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33050, 32780, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("g")) {
                if (pc.getInventory().checkItem(41159, 4)) {
                    pc.getInventory().consumeItem(41159, 4);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32625, 32784, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("h")) {
                if (pc.getInventory().checkItem(41159, 4)) {
                    pc.getInventory().consumeItem(41159, 4);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33080, 33392, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("i")) {
                if (pc.getInventory().checkItem(41159, 5)) {
                    pc.getInventory().consumeItem(41159, 5);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32640, 33203, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("j")) {
                if (pc.getInventory().checkItem(41159, 5)) {
                    pc.getInventory().consumeItem(41159, 5);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32715, 32448, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("k")) {
                if (pc.getInventory().checkItem(41159, 7)) {
                    pc.getInventory().consumeItem(41159, 7);
                    htmlid = "";
                    L1Teleport.teleport(pc, 33118, 32933, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("l")) {
                if (pc.getInventory().checkItem(41159, 7)) {
                    pc.getInventory().consumeItem(41159, 7);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32885, 32652, (short) 4, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("m")) {
                if (pc.getInventory().checkItem(41159, 12)) {
                    pc.getInventory().consumeItem(41159, 12);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32583, 32924, (short) 0, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            if (s.equalsIgnoreCase("n")) {
                if (pc.getInventory().checkItem(41159, 12)) {
                    pc.getInventory().consumeItem(41159, 12);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32828, 32900, (short) 320, 5, true);
                } else {
                    htmlid = "pctel2";
                }
            }
            // 마야의 그림자
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 778814) {
            if (s.equalsIgnoreCase("1")) { //아덴 마을로 귀환
                htmlid = "";
                L1Teleport.teleport(pc, 33965, 33253, (short) 4, 5, true);
            } else {
                htmlid = "";
            }
            if (s.equalsIgnoreCase("a")) { //오만 10층
                if (pc.getInventory().checkItem(41158, 10)) {
                    pc.getInventory().consumeItem(41158, 10);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32800, 32800, (short) 110, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("b")) { //오만 20층
                if (pc.getInventory().checkItem(41158, 20)) {
                    pc.getInventory().consumeItem(41158, 20);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32800, 32800, (short) 120, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("c")) { //오만 30층
                if (pc.getInventory().checkItem(41158, 30)) {
                    pc.getInventory().consumeItem(41158, 30);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32800, 32800, (short) 130, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("d")) { //오만 40층
                if (pc.getInventory().checkItem(41158, 40)) {
                    pc.getInventory().consumeItem(41158, 40);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32800, 32800, (short) 140, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("e")) { //오만 50층
                if (pc.getInventory().checkItem(41158, 50)) {
                    pc.getInventory().consumeItem(41158, 50);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32796, 32796, (short) 150, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("f")) { //오만 60층
                if (pc.getInventory().checkItem(41158, 60)) {
                    pc.getInventory().consumeItem(41158, 60);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32720, 32821, (short) 160, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("g")) { //오만 70층
                if (pc.getInventory().checkItem(41158, 70)) {
                    pc.getInventory().consumeItem(41158, 70);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32720, 32821, (short) 170, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("h")) { //오만 80층
                if (pc.getInventory().checkItem(41158, 80)) {
                    pc.getInventory().consumeItem(41158, 80);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32724, 32822, (short) 180, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("i")) { //오만 90층
                if (pc.getInventory().checkItem(41158, 90)) {
                    pc.getInventory().consumeItem(41158, 90);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32722, 32827, (short) 190, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }
            if (s.equalsIgnoreCase("j")) { //오만 100층
                if (pc.getInventory().checkItem(41158, 100)) {
                    pc.getInventory().consumeItem(41158, 100);
                    htmlid = "";
                    L1Teleport.teleport(pc, 32731, 32856, (short) 200, 5, true);
                } else {
                    htmlid = "adenshadow2";
                }
            }

        } else if (s.equalsIgnoreCase("withdrawnpc")) { // 「동물을 받는다」
            L1NpcInstance npc = (L1NpcInstance) obj;
            int npcId = npc.getNpcTemplate().get_npcId();
            if (npcId == 70773 || npcId == 70544 || npcId == 70664
                    || npcId == 70836 || npcId == 70617 || npcId == 70749
                    || npcId == 70632 || npcId == 70873 || npcId == 70671
                    || npcId == 70055 || npcId == 70723 || npcId == 70943
                    || npcId == 71053 || npcId == 70532) {
                pc.sendPackets(new S_PetList(objid, pc));
            }
        } else if (s.equalsIgnoreCase("changename")) { // 「이름을 결정한다」
            pc.setTempID(objid); // 펫의 오브젝트 ID를 보존해 둔다
            pc.sendPackets(new S_Message_YN(325, "")); // 동물의 이름을 결정해 주세요：
        } else if (s.equalsIgnoreCase("attackchr")) {
            if (obj instanceof L1Character) {
                L1Character cha = (L1Character) obj;
                pc.sendPackets(new S_SelectTarget(cha.getId()));
            }
        } else if (s.equalsIgnoreCase("select")) { // 경매 게시판의 리스트를 클릭
            pc.sendPackets(new S_AuctionBoardRead(objid, s2));
        } else if (s.equalsIgnoreCase("map")) { // 아지트의 위치를 확인한다
            pc.sendPackets(new S_HouseMap(objid, s2));
        } else if (s.equalsIgnoreCase("apply")) { // 경매에 참가한다
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 군주, 한편, 혈맹주
                    if (pc.getLevel() >= 15) {
                        if (clan.getHouseId() == 0) {
                            pc.sendPackets(new S_ApplyAuction(objid, s2));
                        } else {
                            pc.sendPackets(new S_ServerMessage(521)); // 벌써 집을 소유하고 있습니다.
                            htmlid = ""; // 윈도우를 지운다
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(519)); // 레벨 15 미만의 군주는 경매에 참가할 수 없습니다.
                        htmlid = ""; // 윈도우를 지운다
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
                    htmlid = ""; // 윈도우를 지운다
                }
            } else {
                pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("open") // 문을 연다
                || s.equalsIgnoreCase("close")) { // 문을 닫는다
            L1NpcInstance npc = (L1NpcInstance) obj;
            openCloseDoor(pc, npc, s);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("expel")) { // 외부의 인간을 내쫓는다
            L1NpcInstance npc = (L1NpcInstance) obj;
            expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("pay")) { // 세금을 납부한다
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmldata = makeHouseTaxStrings(pc, npc);
            htmlid = "agpay";
        } else if (s.equalsIgnoreCase("payfee")) { // 세금을 납부한다
            L1NpcInstance npc = (L1NpcInstance) obj;
            payFee(pc, npc);
            htmlid = "";
        } else if (s.equalsIgnoreCase("name")) { // 가의 이름을 결정한다
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(
                            houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        pc.setTempID(houseId); // 아지트 ID를 보존해 둔다
                        pc.sendPackets(new S_Message_YN(512, "")); // 가의 이름은?
                    }
                }
            }
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("rem")) { // 집안의 가구를 모두 없앤다
        } else if (s.equalsIgnoreCase("tel0") // 텔레포트 한다(창고)
                || s.equalsIgnoreCase("tel1") // 텔레포트 한다(애완동물 보관소)
                || s.equalsIgnoreCase("tel2") // 텔레포트 한다(속죄의 사자)
                || s.equalsIgnoreCase("tel3")) { // 텔레포트 한다(기란 시장)
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(
                            houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        int[] loc = new int[3];
                        if (s.equalsIgnoreCase("tel0")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId,
                                    0);
                        } else if (s.equalsIgnoreCase("tel1")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId,
                                    1);
                        } else if (s.equalsIgnoreCase("tel2")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId,
                                    2);
                        } else if (s.equalsIgnoreCase("tel3")) {
                            loc = L1HouseLocation.getHouseTeleportLoc(houseId,
                                    3);
                        }
                        L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
                                5, true);
                    }
                }
            }
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("upgrade")) { // 지하 아지트를 만든다
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(
                            houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 군주, 한편, 혈맹주
                            if (house.isPurchaseBasement()) {
                                // 이미 지하 아지트를 소유하고 있습니다.
                                pc.sendPackets(new S_ServerMessage(1135));
                            } else {
                                if (pc.getInventory().consumeItem(
                                        L1ItemId.ADENA, 5000000)) {
                                    house.setPurchaseBasement(true);
                                    HouseTable.getInstance().updateHouse(house); // DB에 기입해
                                    // 지하 아지트가 생성되었습니다.
                                    pc.sendPackets(new S_ServerMessage(1099));
                                } else {
                                    // \f1아데나가 부족합니다.
                                    pc.sendPackets(new S_ServerMessage(189));
                                }
                            }
                        } else {
                            // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
                            pc.sendPackets(new S_ServerMessage(518));
                        }
                    }
                }
            }
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("hall")
                && obj instanceof L1HousekeeperInstance) { // 지하 아지트에 텔레포트 한다
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                int houseId = clan.getHouseId();
                if (houseId != 0) {
                    L1House house = HouseTable.getInstance().getHouseTable(
                            houseId);
                    int keeperId = house.getKeeperId();
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    if (npc.getNpcTemplate().get_npcId() == keeperId) {
                        if (house.isPurchaseBasement()) {
                            int[] loc = new int[3];
                            loc = L1HouseLocation.getBasementLoc(houseId);
                            L1Teleport.teleport(pc, loc[0], loc[1],
                                    (short) (loc[2]), 5, true);
                        } else {
                            // 지하 아지트가 없기 때문에, 텔레포트 할 수 없습니다.
                            pc.sendPackets(new S_ServerMessage(1098));
                        }
                    }
                }
            }
            htmlid = ""; // 윈도우를 지운다
        }

        // ElfAttr:0.무속성, 1.땅속성, 2.불속성, 4.물속성, 8.바람 속성
        else if (s.equalsIgnoreCase("fire")) // 에르프의 속성 변경 「불의 계열을 배운다」
        {
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }
                pc.setElfAttr(2);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.sendPackets(new S_SkillIconGFX(15, 1)); // 체의 구석구석에 화의 정령력이 스며들어 옵니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("water")) { // 에르프의 속성 변경 「물의 계열을 배운다」
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }
                pc.setElfAttr(4);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.sendPackets(new S_SkillIconGFX(15, 2)); // 체의 구석구석에 물의 정령력이 스며들어 옵니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("air")) { // 에르프의 속성 변경 「바람의 계열을 배운다」
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }
                pc.setElfAttr(8);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.sendPackets(new S_SkillIconGFX(15, 3)); // 체의 구석구석에 바람의 정령력이 스며들어 옵니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("earth")) { // 에르프의 속성 변경 「땅의 계열을 배운다」
            if (pc.isElf()) {
                if (pc.getElfAttr() != 0) {
                    return;
                }
                pc.setElfAttr(1);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.sendPackets(new S_SkillIconGFX(15, 4)); // 체의 구석구석에 땅의 정령력이 스며들어 옵니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("init")) { // 에르프의 속성 변경 「정령력을 제거한다」
            if (pc.isElf()) {
                if (pc.getElfAttr() == 0) {
                    return;
                }
                for (int cnt = 129; cnt <= 176; cnt++) // 전에르프 마법을 체크
                {
                    L1Skills l1skills1 = SkillsTable.getInstance().getTemplate(
                            cnt);
                    int skill_attr = l1skills1.getAttr();
                    if (skill_attr != 0) // 무속성 마법 이외의 에르프 마법을 DB로부터 삭제한다
                    {
                        SkillsTable.getInstance().spellLost(pc.getId(),
                                l1skills1.getSkillId());
                    }
                }
                // 일렉트로닉 멘탈 프로텍션에 의해 상승하고 있는 속성 방어를 리셋트
                if (pc.hasSkillEffect(L1SkillId.ELEMENTAL_PROTECTION)) {
                    pc.removeSkillEffect(L1SkillId.ELEMENTAL_PROTECTION);
                }
                pc.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0, 0, 0, 0, 0));
                pc.setElfAttr(0);
                pc.save(); // DB에 캐릭터 정보를 기입한다
                pc.sendPackets(new S_ServerMessage(678));
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("exp")) { // 「경험치를 회복한다」
            if (pc.getExpRes() == 1) {
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
                pc.sendPackets(new S_Message_YN(738, String.valueOf(cost))); // 경험치를 회복하려면%0의 아데나가 필요합니다.경험치를 회복합니까?
            } else {
                pc.sendPackets(new S_ServerMessage(739)); // 지금은 경험치를 회복할 수가 없습니다.
                htmlid = ""; // 윈도우를 지운다
            }
        } else if (s.equalsIgnoreCase("pk")) { // 「속죄 한다」
            if (pc.getLawful() < 30000) {
                pc.sendPackets(new S_ServerMessage(559)); // \f1 아직 죄 풀어에 충분한 젱교를 실시하고 있지 않습니다.
            } else if (pc.get_PKcount() < 5) {
                pc.sendPackets(new S_ServerMessage(560)); // \f1 아직 죄청등 해를 할 필요는 없습니다.
            } else {
                if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
                    pc.set_PKcount(pc.get_PKcount() - 5);
                    pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc
                            .get_PKcount()))); // PK회수가%0가 되었습니다.
                } else {
                    pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                }
            }
            // 윈도우를 지운다
            htmlid = "";
        } else if (s.equalsIgnoreCase("ent")) {
            // 「도깨비 저택에 들어간다」
            // 「얼티메이트 배틀에 참가한다」또는
            // 「관람 모드로 투기장에 들어간다」
            int npcId = ((L1NpcInstance) obj).getNpcId();
            if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
                htmlid = enterHauntedHouse(pc);
            } else if (npcId == 80088) {
                htmlid = enterPetMatch(pc, Integer.valueOf(s2));
            } else if (npcId == 200022) { // 펫 레이싱
                htmlid = enterPe(pc, npcId);
            } else if (npcId == 200041) {  // 스텟 초기화
                L1ItemInstance armor = pc.getInventory().getItemEquipped(2, 13);
                if (pc.getLevel() > 50) {
                    if (pc.getHighLevel() - 50 == pc.getBonusStats()) {
                        if (pc.getInventory().checkItem(500042)) {
                            if (armor != null) {
                                pc.getInventory().setEquipped(armor, false);
                                pc.sendPackets(new S_SystemMessage("가더가 벗겨집니다. NPC를 한번더 클릭해주세요."));
                            } else {
                                pc.getInventory().consumeItem(500042, 1);
                                StatInitialize(pc);
                                L1Teleport.teleport(pc, 32720, 32850, (short) 5166, 5, true);
                                htmlid = "";
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(1290));
                        }
                    } else {
                        pc.sendPackets(new S_SystemMessage("보너스 스텟을 다찍기 전엔 회상의 촛불을 사용하실수 없습니다.")); //스텟버그 fix
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("스텟초기화는 레벨 51이상부터 가능합니다."));
                }
            } else if (npcId == 50038 || npcId == 50042 || npcId == 50029
                    || npcId == 50019 || npcId == 50062) { // 부관리인의 경우는 관전
                htmlid = watchUb(pc, npcId);
            } else {
                htmlid = enterUb(pc, npcId);
            }
        } else if (s.equalsIgnoreCase("par")) { // UB관련 「얼티메이트 배틀에 참가한다」부관리인 경유
            htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
        } else if (s.equalsIgnoreCase("info")) { // 「정보를 확인한다」 「경기 정보를 확인한다」
            int npcId = ((L1NpcInstance) obj).getNpcId();
            if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
            } else {
                htmlid = "colos2";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 75026) {
            if (s.equalsIgnoreCase("a")) { // 장비를 받는다(방어구)
                pc.getInventory().storeItem(40029, 50); // 상아탑 빨강물약
                pc.getInventory().storeItem(40030, 5);  //  상아탑 초록물약
                pc.getInventory().storeItem(40099, 20);  //  상아탑
                pc.getInventory().storeItem(20028, 1); // 투구
                pc.getInventory().storeItem(20126, 1); // 갑옷
                pc.getInventory().storeItem(20082, 1); // 티셔츠
                pc.getInventory().storeItem(20173, 1); // 장갑
                pc.getInventory().storeItem(20206, 1); // 부츠
                pc.getInventory().storeItem(20232, 1); // 방패
                pc.getInventory().storeItem(20080, 1); // 망토
                pc.sendPackets(new S_SystemMessage("상아탑의 방어구를 지급 받았습니다."));
                htmlid = "";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 75025) {
            if (s.equalsIgnoreCase("0")) { // 뒤로?
                htmlid = "";
            } else if (s.equalsIgnoreCase("1")) { // 다른조언? 이게 맞나?!ㅋ
                htmlid = "lowlv15";
            } else if (s.equalsIgnoreCase("2")) { // 상아탑장비 수정
                if (!pc.getInventory().checkItem(20028) && !pc.getInventory().checkItem(20126) && !pc.getInventory().checkItem(20082) && !pc.getInventory().checkItem(20173) && !pc.getInventory().checkItem(20206) && !pc.getInventory().checkItem(20232) && !pc.getInventory().checkItem(20080) && !pc.getInventory().checkItem(7) && !pc.getInventory().checkItem(174) && !pc.getInventory().checkItem(73) && !pc.getInventory().checkItem(175) && !pc.getInventory().checkItem(224) && !pc.getInventory().checkItem(120) && !pc.getInventory().checkItem(105) && !pc.getInventory().checkItem(156) && !pc.getInventory().checkItem(35) && !pc.getInventory().checkItem(48)) { // 검 방어구 모두없을때
                    pc.getInventory().storeItem(500000, 1); // 아이템버너 변경 (초보아이템상자)
                    pc.sendPackets(new S_SystemMessage("초보 아이템 상자를 얻었습니다."));
                    htmlid = "lowlv16";
                } else if (!pc.getInventory().checkItem(7) && !pc.getInventory().checkItem(174) && !pc.getInventory().checkItem(73) && !pc.getInventory().checkItem(175) && !pc.getInventory().checkItem(224) && !pc.getInventory().checkItem(120) && !pc.getInventory().checkItem(105) && !pc.getInventory().checkItem(156) && !pc.getInventory().checkItem(35) && !pc.getInventory().checkItem(48)) {
                    if (pc.isKnight()) { // 기사이면?
                        pc.getInventory().storeItem(35, 1); // 상아탑 검
                        pc.getInventory().storeItem(48, 1); // 상아탑 양손검
                        pc.getInventory().storeItem(40014, 10); // 용기의 물약
                        htmlid = "lowlv19";
                    }
                    if (pc.isCrown()) { // 군주면
                        pc.getInventory().storeItem(35, 1); // 상아탑 검
                        pc.getInventory().storeItem(48, 1); // 상아탑 양손검
                        pc.getInventory().storeItem(40031, 10); // 악마의 피
                        htmlid = "lowlv19";
                    }
                    if (pc.isWizard()) { // 법사면
                        pc.getInventory().storeItem(120, 1); // 상아탑 지팡이
                        pc.getInventory().storeItem(40016, 10); // 지혜의 물약
                        htmlid = "lowlv19";
                    }
                    if (pc.isElf()) { // 요정이면
                        pc.getInventory().storeItem(40068, 10); // 엘븐 와퍼
                        pc.getInventory().storeItem(35, 1); // 상아탑 검
                        pc.getInventory().storeItem(174, 1); // 상아탑 활
                        pc.getInventory().storeItem(175, 1); // 상아탑 활
                        pc.getInventory().storeItem(40744, 2000); // 은화살
                        htmlid = "lowlv19";
                    }
                    if (pc.isDarkelf()) { // 다엘이면
                        pc.getInventory().storeItem(73, 1); // 상아탑 이도류
                        pc.getInventory().storeItem(156, 1); // 상아탑 크로우
                        htmlid = "lowlv19";
                    }
                    if (pc.isDragonKnight()) {  //용기사면
                        pc.getInventory().storeItem(400002, 10); // 유그드라 열매
                        pc.getInventory().storeItem(35, 1); // 상아탑 검
                        pc.getInventory().storeItem(147, 1); // 상아탑 도끼
                        pc.getInventory().storeItem(48, 1); // 상아탑 양손검
                        htmlid = "lowlv19";
                    }
                    if (pc.isBlackWizard()) { //환술사면
                        pc.getInventory().storeItem(430006, 10); // 유그드라 열매
                        pc.getInventory().storeItem(120, 1); // 상아탑 지팡이
                        htmlid = "lowlv19";
                    }// 상아탑에 무기가 없을때
                } else if (!pc.getInventory().checkItem(20028)
                        && !pc.getInventory().checkItem(20126)
                        && !pc.getInventory().checkItem(20082)
                        && !pc.getInventory().checkItem(20173)
                        && !pc.getInventory().checkItem(20232)
                        && !pc.getInventory().checkItem(20080)
                        && !pc.getInventory().checkItem(20206)) { // 방어구 모두 없을때
                    pc.getInventory().storeItem(20028, 1); // 투구
                    pc.getInventory().storeItem(20126, 1); // 갑옷
                    pc.getInventory().storeItem(20082, 1); // 티셔츠
                    pc.getInventory().storeItem(20173, 1); // 장갑
                    pc.getInventory().storeItem(20206, 1); // 부츠
                    pc.getInventory().storeItem(20232, 1); // 방패
                    pc.getInventory().storeItem(20080, 1); // 망토
                    htmlid = "lowlv18";
                } else {
                    htmlid = "lowlv17"; // 방어구 무기 모두 있을때
                }
            } else if (s.equalsIgnoreCase("3")) { // 방어구 주문서 1500원에 교환
                if (!pc.getInventory().checkItem(40308, 1500)) { // 1500 아데나가 안될때
                    htmlid = "lowlv20"; // 아데나 x 방어구 주문서 X
                } else { //아데나가 있을때
                    pc.getInventory().consumeItem(40308, 1500); // 1500원 감소
                    pc.getInventory().storeItem(540341, 1); // 방어구 주문서 획득
                    pc.sendPackets(new S_SystemMessage("여행자 방어구 주문서를 얻었습니다."));
                    htmlid = "lowlv21"; // 방어구 주문서 , 잘쓰게?!ㅋ
                }
            } else if (s.equalsIgnoreCase("4")) { // 무기 주문서 1000원에 교환
                if (!pc.getInventory().checkItem(40308, 1000)) { // 1000 아데나가 안될때
                    htmlid = "lowlv20"; // 아데나 x 무기 주문서 X
                } else { //아데나가 있을때
                    pc.getInventory().consumeItem(40308, 1000); // 1000원 감소
                    pc.getInventory().storeItem(540342, 1); // 무기 주문서 획득
                    pc.sendPackets(new S_SystemMessage("여행자 무기 주문서를 얻었습니다."));
                    htmlid = "lowlv21"; // 무기 주문서 , 잘쓰게?!ㅋ
                }
            } else if (s.equalsIgnoreCase("5")) { // 묘약 250원에 교환
                if (!pc.getInventory().checkItem(40308, 250)) { // 250원 아데나가 안될때
                    htmlid = "lowlv14"; // 아데나 x 묘약 안줌
                } else { // 아데나가 있을때
                    pc.getInventory().consumeItem(40308, 250); // 250원 감소
                    pc.getInventory().storeItem(540343, 1); // 묘약 획득(아이템버너변경)
                    pc.sendPackets(new S_SystemMessage("상아탑의 묘약를 얻었습니다."));
                    htmlid = "lowlv21"; // 묘약줄때 , 잘쓰게?!ㅋ
                } /////////////////////////////////////////  완료
            } else if (s.equalsIgnoreCase("6")) { // 상아탑 마법주머니 2000원에 교환
                if (pc.getInventory().checkItem(540344, 1) && pc.getInventory().checkItem(540345, 1)) { // 마법주머니,보급주문서 있을때(아이템버너변경)
                    htmlid = "lowlv23"; // 주머니O ,X
                } else if (!pc.getInventory().checkItem(40308, 2000)) { // 2000원 아데나가 안될때
                    htmlid = "lowlv20"; // 돈부족
                } else { // 주머니,보급주문서X , 돈있을때 (받을수 있는 조건 O)
                    pc.getInventory().consumeItem(40308, 2000); // 2000원 감소
                    pc.getInventory().storeItem(540344, 1); // 마법 주머니 획득(아이템버너변경)
                    L1ItemInstance l1iteminstance = pc.getInventory().findItemId(540345);
                              /*  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                L1ItemInstance.setPocketTime(timestamp); */
                    pc.sendPackets(new S_SystemMessage("상아탑의 마법주머니를 얻었습니다."));
                    htmlid = "lowlv22"; // 주머니줄때 , 사용법
                }
            } else if (s.equalsIgnoreCase("a")) { // 게렝
                htmlid = "";
                L1Teleport.teleport(pc, 32562, 33082, (short) 0, 4, true);
            } else if (s.equalsIgnoreCase("b")) { // 로우풀
                htmlid = "";
                L1Teleport.teleport(pc, 33117, 32937, (short) 4, 4, true);
            } else if (s.equalsIgnoreCase("c")) { // 카오틱
                htmlid = "";
                L1Teleport.teleport(pc, 32887, 32652, (short) 4, 4, true);
            } else if (s.equalsIgnoreCase("d")) { // 린다 (요정)
                htmlid = "";
                L1Teleport.teleport(pc, 32792, 32820, (short) 75, 4, true);
            } else if (s.equalsIgnoreCase("e")) { // 상아탑 정령마법수련실 (요정)
                htmlid = "";
                L1Teleport.teleport(pc, 32752, 32808, (short) 76, 4, true); // 확인,수정
            } else if (s.equalsIgnoreCase("f")) { //  상아탑 엘리온 (요정)
                htmlid = "";
                L1Teleport.teleport(pc, 32748, 32848, (short) 76, 4, true); // 수정
            } else if (s.equalsIgnoreCase("g")) { // 침묵 세디아 (다엘)
                if (pc.isDarkelf()) {
                    htmlid = "";
                    L1Teleport.teleport(pc, 32878, 32905, (short) 304, 4, true); // 확인
                } else {
                    htmlid = "lowlv40"; // 다엘 아닌경우 X 확인
                }
            } else if (s.equalsIgnoreCase("h")) { // 제파르 (용기사)
                if (pc.isDragonKnight()) {
                    htmlid = "";
                    L1Teleport.teleport(pc, 32824, 32873, (short) 1001, 4, true); // 확인
                } else {
                    htmlid = "lowlv41"; // < 용기사X 안보냄
                }
            } else if (s.equalsIgnoreCase("K")) { // 장신구쳇크후 준다 35이후부터 준다
                if (pc.getLevel() < 35) {
                    htmlid = "lowlv44"; //레벨35이하 X
                } else if (pc.getLevel() < 46) {
                    if (pc.getInventory().checkItem(420010, 1) && pc.getInventory().checkItem(20282, 1)) {  // 귀걸이,반지(상아)
                        htmlid = "lowlv45"; // < 이미 있다
                    } else { // 35이상 45이하 // 장신구X // 장신구를 준다
                        htmlid = "lowlv43"; //  장신구준다
                        pc.getInventory().storeItem(420010, 1); //귀걸이
                        pc.getInventory().storeItem(20282, 1); // 반지
                    }
                }
            } else if (s.equalsIgnoreCase("i")) { // 스비엘 (환술사)
                if (pc.isBlackWizard()) {
                    htmlid = "";
                    L1Teleport.teleport(pc, 32760, 32885, (short) 1000, 4, true);
                } else {
                    htmlid = "lowlv42"; // < 환술사X 안보냄
                }
            } else if (s.equalsIgnoreCase("j")) { // 군터 (군주)
                htmlid = "";
                L1Teleport.teleport(pc, 32670, 32790, (short) 3, 4, true);
            }
		/*} else if (s.equalsIgnoreCase("sco")) { // UB관련 「고득점자 일람을 확인한다」
			htmldata = new String[10];
			htmlid = "colos3";

		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50003) {  //일드라스
			if (s.equalsIgnoreCase("teleportURL")){
				htmlid = "illdrath1";}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50016) {  //제노
			if (s.equalsIgnoreCase("teleportURL")){
			    htmlid = "zeno2";}
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200027) {	// 용기사 피에나
			if (s.equalsIgnoreCase("teleportURL"))
				htmlid = "feaena3";
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200028) {	// 환술사 아샤
			if (s.equalsIgnoreCase("teleportURL")){
			    htmlid = "asha3";
			}*/
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70514) {
            if (s.equalsIgnoreCase("D")) { // 법사
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = ""
                    ;
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 4) {
                    htmlid = "tutorm1";
                } // 허수아비 안내
                else if (pc.getLevel() < 5) {
                    htmlid = "tutorm2";
                } // 1단계 마법
                else if (pc.getLevel() < 8) {
                    htmlid = "tutorm3";
                } // 창고 ㅋ
                else if (pc.getLevel() < 12) {
                    htmlid = "tutorm4";
                } // 2단계 마법
                else if (pc.getLevel() < 13) {
                    htmlid = "tutorm5";
                } // 3단계 마법
                else if (pc.getLevel() < 14) {
                    htmlid = "tutorm6";
                } // 배울게 없대 ㅋ
            } else if (s.equalsIgnoreCase("A")) { //군주
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = "tutor";
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 5) {
                    htmlid = "tutorp1";
                } // 허수아비 안내
                else if (pc.getLevel() < 7) {
                    htmlid = "tutorp2";
                } // 창고/ 혈맹창설 ㅋ
                else if (pc.getLevel() < 10) {
                    htmlid = "tutorp3";
                } // 칭찬질 --ㅋ
                else if (pc.getLevel() < 13) {
                    htmlid = "tutorp4";
                } // 1단계 일반마법
                else if (pc.getLevel() < 14) {
                    htmlid = "tutorp5";
                } // 배울게없대 ㅋ
            } else if (s.equalsIgnoreCase("C")) { //요정
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = "";
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 5) {
                    htmlid = "tutore1";
                } // 허수아비 안내
                else if (pc.getLevel() < 8) {
                    htmlid = "tutore2";
                } // 창고
                else if (pc.getLevel() < 9) {
                    htmlid = "tutore3";
                } // 1단계 일반마법
                else if (pc.getLevel() < 10) {
                    htmlid = "tutore4";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 11) {
                    htmlid = "tutore5";
                } // 1단계 요정마법
                else if (pc.getLevel() < 14) {
                    htmlid = "tutore6";
                } // 배울게 없대
            } else if (s.equalsIgnoreCase("E")) {//다엘
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = "";
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 5) {
                    htmlid = "tutord1";
                } // 허수아비 안내
                else if (pc.getLevel() < 6) {
                    htmlid = "tutord2";
                } // 창고
                else if (pc.getLevel() < 8) {
                    htmlid = "tutord3";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 10) {
                    htmlid = "tutord4";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 13) {
                    htmlid = "tutord5";
                } // 1단계 일반마법
                else if (pc.getLevel() < 14) {
                    htmlid = "tutord6";
                } // 배울게 없대 ㅋ
            } else if (s.equalsIgnoreCase("G")) { //환술사
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = "";
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 5) {
                    htmlid = "tutori1";
                } // 허수아비 안내
                else if (pc.getLevel() < 6) {
                    htmlid = "tutori2";
                } // 창고
                else if (pc.getLevel() < 10) {
                    htmlid = "tutori3";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 12) {
                    htmlid = "tutori4";
                } // 환술사 1단계마법
                else if (pc.getLevel() < 14) {
                    htmlid = "tutori5";
                } // 배울게없대 ㅋ
            } else if (s.equalsIgnoreCase("F")) { // 용기사인대 기사도 같아서 걍 같이씀 ㅋ
                pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1800));
                pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
                pc.sendPackets(new S_SkillSound(pc.getId(), 755));
                pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
                pc.setMoveSpeed(1);
                pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1800 * 1000);
                pc.setCurrentHp(pc.getMaxHp());
                pc.setCurrentMp(pc.getMaxMp());
                pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                if (pc.getLevel() < 2) {
                    htmlid = "";
                    pc.setExp(pc.getExp() + 125);
                } // 경험치 지급
                else if (pc.getLevel() < 5) {
                    htmlid = "tutordk1";
                } // 허수아비 안내
                else if (pc.getLevel() < 6) {
                    htmlid = "tutordk2";
                } // 창고
                else if (pc.getLevel() < 8) {
                    htmlid = "tutordk3";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 10) {
                    htmlid = "tutordk4";
                } // 그냥 칭찬질 --ㅋ
                else if (pc.getLevel() < 14) {
                    htmlid = "tutordk5";
                } // 마냥 칭찬질 --ㅋ
            } else if (s.equalsIgnoreCase("O")) { // 마을 서쪽 근교
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("P")) { // 마을 동쪽 근교
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("Q")) { // 마을 남서쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true);
            } else if (s.equalsIgnoreCase("R")) { // 마을 남동쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32733, 32902, (short) 2005, 4, true);
            } else if (s.equalsIgnoreCase("S")) { // 마을 북동쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32802, 32803, (short) 2005, 4, true);
            } else if (s.equalsIgnoreCase("T")) { // 마을 북서쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32642, 32763, (short) 2005, 4, true);
            } else if (s.equalsIgnoreCase("U")) { // 마을 서쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("V")) { // 마을 남쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("W")) { // 마을 동쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("X")) { // 마을 북쪽 사냥터
                htmlid = "";
                L1Teleport.teleport(pc, 32605, 32837, (short) 2005, 4, true); // 수정해야댐 ㅋ
            } else if (s.equalsIgnoreCase("L")) { // 상아탑
                if (pc.getLevel() < 3) { // 1~2레벨 허수아비로 보내기(감사합니다)를 클릭시
                    htmlid = "";
                    L1Teleport.teleport(pc, 32754, 32807, (short) 2005, 4, true); // 허수아비
                } else if (pc.getLevel() > 9) { // 10레벨 이상 (상아탑)클릭시 상아탑으로
                    L1Teleport.teleport(pc, 34041, 32155, (short) 4, 4, true);
                }
            } else if (s.equalsIgnoreCase("M")) { // 다엘 세디아
                htmlid = "";
                L1Teleport.teleport(pc, 32878, 32905, (short) 304, 4, true);
            } else if (s.equalsIgnoreCase("N")) { // 환술사 스비엘
                htmlid = "";
                L1Teleport.teleport(pc, 32760, 32885, (short) 1000, 4, true);
            } else if (s.equalsIgnoreCase("H")) { // 말하는섬 창고
                htmlid = "";
                L1Teleport.teleport(pc, 32572, 32945, (short) 0, 4, true);
            } else if (s.equalsIgnoreCase("K")) { // 게렝
                htmlid = "";
                L1Teleport.teleport(pc, 32562, 33082, (short) 0, 4, true);
            } else if (s.equalsIgnoreCase("J")) { // 숨계던젼
                htmlid = "";
                L1Teleport.teleport(pc, 32872, 32871, (short) 86, 4, true);
            }
        }
		/*} else if (s.equalsIgnoreCase("haste")) { // 헤이 파업사
			L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
			int npcid = l1npcinstance.getNpcTemplate().get_npcId();
			if (npcid == 70514) {
				pc.sendPackets(new S_ServerMessage(183));
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1600 * 1000);
				htmlid = ""; // 윈도우를 지운다
			}
		}   */
        // 변신 전문가
        else if (s.equalsIgnoreCase("skeleton nbmorph")) {
            poly(client, 2374);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
            poly(client, 3874);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("shelob nbmorph")) {
            poly(client, 95);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("ghoul nbmorph")) {
            poly(client, 3873);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("ghast nbmorph")) {
            poly(client, 3875);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
            poly(client, 3868);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
            poly(client, 2376);
            htmlid = ""; // 윈도우를 지운다
        } else if (s.equalsIgnoreCase("troll nbmorph")) {
            poly(client, 3878);
            htmlid = ""; // 윈도우를 지운다
        }
        // 장로 노나메
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
            // 「편지를 받는다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41060, 1); // 노나메의 추천서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfnoname9";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41060, 1)) {
                    htmlid = "orcfnoname11";
                }
            }
        }
        // 두다마라브우
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
            // 「알았습니다, 그 자리곳에 보내 주세요」
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "orcfbuwoo2";
            }
        }
        // 조사단장 아트바노아
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
            // 「해 봅니다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41065, 1); // 조사단의 증서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfnoa4";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41065, 1)) {
                    htmlid = "orcfnoa7";
                }
            }
        }
        // 네르가후우모
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
            // 「조사를 합니다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41064, 1); // 조사단의 증서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfhuwoomo4";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41064, 1)) {
                    htmlid = "orcfhuwoomo6";
                }
            }
        }
        // 네르가바크모
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
            // 「조사를 합니다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41062, 1); // 조사단의 증서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfbakumo4";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41062, 1)) {
                    htmlid = "orcfbakumo6";
                }
            }
        }
        // 두다마라브카
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
            // 「조사를 합니다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41063, 1); // 조사단의 증서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfbuka4";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41063, 1)) {
                    htmlid = "orcfbuka6";
                }
            }
        }
        // 두다마라카메
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
            // 「조사를 합니다」
            if (s.equalsIgnoreCase("A")) {
                L1NpcInstance npc = (L1NpcInstance) obj;
                L1ItemInstance item = pc.getInventory().storeItem(41061, 1); // 조사단의 증서
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0이%1를 주었습니다.
                htmlid = "orcfkame4";
            }
            // 「조사를 그만둡니다」
            else if (s.equalsIgnoreCase("Z")) {
                if (pc.getInventory().consumeItem(41061, 1)) {
                    htmlid = "orcfkame6";
                }
            }
        }
        // 포워르
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
            // 「들어가 본다」
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "usender2";
            }
        }
        // 치안 단장 아미스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
            // 「내가 도웁시다」
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "amisoo2";
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200011) {
            if (s.equals("A")) { /*robinhood1~7*/
                if (pc.getInventory().checkItem(40028)) { /*사과주스 체크*/
                    pc.getInventory().consumeItem(40028, 1); /*사과주스 소비*/
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 1); /*1단계 완료*/
                    htmlid = "robinhood4";
                } else {
                    htmlid = "robinhood19";
                }
            } else if (s.equals("B")) { /*robinhood8*/
                final int[] item_ids = {41346, 41348};
                final int[] item_amounts = {1, 1,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    // L1ItemInstance memo = pc.getInventory().storeItem(41346, 1);
                    // L1ItemInstance memo2 = pc.getInventory().storeItem(41348, 1);
                    pc.sendPackets(new S_SystemMessage("로빈후드의 메모지와 소개장을 얻었습니다."));
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 2);
                    htmlid = "robinhood13";
                }
            } else if (s.equals("C")) { /*robinhood9*/
                if (pc.getInventory().checkItem(41346) && pc.getInventory().checkItem(41351)
                        && pc.getInventory().checkItem(41352, 4) && pc.getInventory().checkItem(40618, 30)
                        && pc.getInventory().checkItem(40643, 30) && pc.getInventory().checkItem(40645, 30)
                        && pc.getInventory().checkItem(40651, 30) && pc.getInventory().checkItem(40676, 30)) {
                    pc.getInventory().consumeItem(41346, 1); /*메모장, 정기, 유뿔, 불, 물, 바람, 대지 어둠숨결*/
                    pc.getInventory().consumeItem(41351, 1);
                    pc.getInventory().consumeItem(41352, 4);
                    pc.getInventory().consumeItem(40651, 30);
                    pc.getInventory().consumeItem(40643, 30);
                    pc.getInventory().consumeItem(40645, 30);
                    pc.getInventory().consumeItem(40618, 30);
                    pc.getInventory().consumeItem(40676, 30);
                    // L1ItemInstance ring = pc.getInventory().storeItem(50006, 1); /*반지*/
                    // L1ItemInstance memo3 = pc.getInventory().storeItem(50009, 1); /*메모지*/
                    final int[] item_ids = {41350, 41347};
                    final int[] item_amounts = {1, 1,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("로빈후드의 반지와 메모지를 얻었습니다."));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 7); /*7단계 완료*/
                    htmlid = "robinhood10"; /*나머지 재료를 찾아오게..*/
                } else {
                    htmlid = "robinhood15"; /*달빛정기, 유뿔 가져왔는가*/
                }
            } else if (s.equals("E")) { /*robinhood11*/
                if (pc.getInventory().checkItem(41350) && pc.getInventory().checkItem(41347)
                        && pc.getInventory().checkItem(40491, 30) && pc.getInventory().checkItem(40495, 40)
                        && pc.getInventory().checkItem(100) && pc.getInventory().checkItem(40509, 12)
                        && pc.getInventory().checkItem(40052) && pc.getInventory().checkItem(40053)
                        && pc.getInventory().checkItem(40054) && pc.getInventory().checkItem(40055)) {
                    pc.getInventory().consumeItem(41350, 1); /*반지, 메모지, 그리폰깃털, 미스릴실, 오리뿔, 오판, 최고급보석1개씩*/
                    pc.getInventory().consumeItem(41347, 1);
                    pc.getInventory().consumeItem(40491, 30);
                    pc.getInventory().consumeItem(40495, 40);
                    pc.getInventory().consumeItem(100, 1);
                    pc.getInventory().consumeItem(40509, 12);
                    pc.getInventory().consumeItem(40052, 1);
                    pc.getInventory().consumeItem(40053, 1);
                    pc.getInventory().consumeItem(40054, 1);
                    pc.getInventory().consumeItem(40055, 1);
                    final int[] item_ids = {205};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("달의 장궁을 얻었습니다."));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 0); /*퀘스트 리셋*/
                    htmlid = "robinhood12"; /*완성이야*/
                } else {
                    htmlid = "robinhood17"; /*재료가 부족한걸*/
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200010) {//지브릴
            if (s.equals("A")) {
                if (pc.getInventory().checkItem(41348)) {
                    pc.getInventory().consumeItem(41348, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 3);
                    htmlid = "zybril12";
                } else {
                    htmlid = "zybril11";
                }
            } else if (s.equals("B")) {
                if (pc.getInventory().checkItem(40048, 10) && pc.getInventory().checkItem(40049, 10)
                        && pc.getInventory().checkItem(40050, 10) && pc.getInventory().checkItem(40051, 10)) {
                    pc.getInventory().consumeItem(40048, 10);
                    pc.getInventory().consumeItem(40049, 10);
                    pc.getInventory().consumeItem(40050, 10);
                    pc.getInventory().consumeItem(40051, 10);
                    final int[] item_ids = {41353};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("에바의 단검을 얻었습니다."));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 4);
                    htmlid = "zybril13";
                } else {
                    htmlid = "";
                }
            } else if (s.equals("C")) {
                if (pc.getInventory().checkItem(40514, 10) && pc.getInventory().checkItem(41353, 1)) {
                    pc.getInventory().consumeItem(40514, 10);
                    pc.getInventory().consumeItem(41353, 1);
                    final int[] item_ids = {41354};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("신성한 에바의 물을 얻었습니다."));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 5);
                    htmlid = "zybril9";
                } else {
                    htmlid = "zybril13";
                }
            } else if (s.equals("D")) {
                if (pc.getInventory().checkItem(41349)) {
                    pc.getInventory().consumeItem(41349, 1);
                    final int[] item_ids = {41351};
                    final int[] item_amounts = {1};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_SystemMessage("달빛의 정기를 얻었습니다."));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_MOONBOW, 6);
                    htmlid = "zybril10";
                } else {
                    htmlid = "zybril14";
                }
            }
        }

        // 공간의 일그러짐
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
            // 「그만둔다」
            if (s.equalsIgnoreCase("2")) {
                htmlid = ""; // 윈도우를 지운다
            }
        }
        // 요동하는 사람
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
            // 「바르로그의 의지를 맞아들인다」
            if (s.equalsIgnoreCase("1")) {
                if (pc.getKarma() <= -10000000) {
                    pc.setKarma(1000000);
                    // 바르로그의 웃음소리가 뇌리를 강타합니다.
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlid = "betray13";
                }
            }
        }
        // 야히의 집정관
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
            // 「 나의 영혼은 야히님에게…」
            if (s.equalsIgnoreCase("1")) {
                htmlid = "meet105";
            }
            // 「 나의 영혼을 걸쳐 야히님이 충성을 맹세합니다…」
            else if (s.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40718)) { // 블래드 크리스탈의 조각
                    htmlid = "meet106";
                } else {
                    htmlid = "meet110";
                }
            }
            // 「블래드 크리스탈의 조각을 1개 바칩니다」
            else if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40718, 1)) {
                    pc.addKarma((int) (-100 * Config.RATE_KARMA));
                    // 야히의 모습이 점점 근처에 느껴집니다.
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlid = "meet107";
                } else {
                    htmlid = "meet104";
                }
            }
            // 「블래드 크리스탈의 조각을 10개 바칩니다」
            else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40718, 10)) {
                    pc.addKarma((int) (-1000 * Config.RATE_KARMA));
                    // 야히의 모습이 점점 근처에 느껴집니다.
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlid = "meet108";
                } else {
                    htmlid = "meet104";
                }
            }
            // 「블래드 크리스탈의 조각을 100개 바칩니다」
            else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40718, 100)) {
                    pc.addKarma((int) (-10000 * Config.RATE_KARMA));
                    // 야히의 모습이 점점 근처에 느껴집니다.
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlid = "meet109";
                } else {
                    htmlid = "meet104";
                }
            }
            // 「야히님이 대면시쳐 주세요」
            else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(40615) // 그림자의 신전 2층의 열쇠
                        || pc.getInventory().checkItem(40616)) { // 그림자의 신전 3층의 열쇠
                    htmlid = "";
                } else {
                    L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
                }
            }
        }
        // 야히의 참모
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80052) {
            // 나에게 힘을 주시도록···
            if (s.equalsIgnoreCase("a")) {
                if (pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
                    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
                } else {
                    pc.setSkillEffect(STATUS_CURSE_BARLOG, 1020 * 1000);
                    pc.sendPackets(new S_SkillIconKillBoss(2, 1020));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
                    pc.sendPackets(new S_ServerMessage(1127));
                }
            }
        }
        // 야히의 대장간
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
            int karmaLevel = pc.getKarmaLevel();
            // 「재료 모든 것을 준비할 수 있었습니다」
            if (s.equalsIgnoreCase("a")) {
                // 바르로그의 투 핸드 소도 / 야히의 대장간
                int aliceMaterialId = 0;
                int[] aliceMaterialIdList = {40991, 196, 197, 198, 199, 200,
                        201, 202, 203};
                for (int id : aliceMaterialIdList) {
                    if (pc.getInventory().checkItem(id)) {
                        aliceMaterialId = id;
                        break;
                    }
                }
                if (aliceMaterialId == 0) {
                    htmlid = "alice_no";
                } else if (aliceMaterialId == 40991) {
                    if (karmaLevel <= -1) {
                        materials = new int[]{40995, 40718, 40991};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{196};
                        createcount = new int[]{1};
                        success_htmlid = "alice_1";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "aliceyet";
                    }
                } else if (aliceMaterialId == 196) {
                    if (karmaLevel <= -2) {
                        materials = new int[]{40997, 40718, 196};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{197};
                        createcount = new int[]{1};
                        success_htmlid = "alice_2";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_1";
                    }
                } else if (aliceMaterialId == 197) {
                    if (karmaLevel <= -3) {
                        materials = new int[]{40990, 40718, 197};
                        counts = new int[]{100, 100, 1};
                        createitem = new int[]{198};
                        createcount = new int[]{1};
                        success_htmlid = "alice_3";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_2";
                    }
                } else if (aliceMaterialId == 198) {
                    if (karmaLevel <= -4) {
                        materials = new int[]{40994, 40718, 198};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{199};
                        createcount = new int[]{1};
                        success_htmlid = "alice_4";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_3";
                    }
                } else if (aliceMaterialId == 199) {
                    if (karmaLevel <= -5) {
                        materials = new int[]{40993, 40718, 199};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{200};
                        createcount = new int[]{1};
                        success_htmlid = "alice_5";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_4";
                    }
                } else if (aliceMaterialId == 200) {
                    if (karmaLevel <= -6) {
                        materials = new int[]{40998, 40718, 200};
                        counts = new int[]{50, 100, 1};
                        createitem = new int[]{201};
                        createcount = new int[]{1};
                        success_htmlid = "alice_6";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_5";
                    }
                } else if (aliceMaterialId == 201) {
                    if (karmaLevel <= -7) {
                        materials = new int[]{40996, 40718, 201};
                        counts = new int[]{10, 100, 1};
                        createitem = new int[]{202};
                        createcount = new int[]{1};
                        success_htmlid = "alice_7";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_6";
                    }
                } else if (aliceMaterialId == 202) {
                    if (karmaLevel <= -8) {
                        materials = new int[]{40992, 40718, 202};
                        counts = new int[]{10, 100, 1};
                        createitem = new int[]{203};
                        createcount = new int[]{1};
                        success_htmlid = "alice_8";
                        failure_htmlid = "alice_no";
                    } else {
                        htmlid = "alice_7";
                    }
                } else if (aliceMaterialId == 203) {
                    htmlid = "alice_8";
                }
            }
        }
        // 야히의 보좌관
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmlid = getYaheeAmulet(pc, npc, s);
        }
        // 업의 관리자
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getKarma() <= -10000000) {
                getBloodCrystalByKarma(pc, npc, s);
            }
            htmlid = "";
        }
        // 차원의 문(바르로그의 방)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
            // 「안에 들어온다」
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(40921)) { // 원소의 지배자
                    L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
                } else {
                    htmlid = "gpass02";
                }
            }
        }
        // 바르로그의 집정관
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
            // 「 나의 영원의 주는 바르로그님 뿐입니다…」
            if (s.equalsIgnoreCase("1")) {
                htmlid = "meet005";
            }
            // 「 나의 영혼을 걸쳐 바르로그님이 충성을 맹세합니다…」
            else if (s.equalsIgnoreCase("2")) {
                if (pc.getInventory().checkItem(40678)) { // 서울 크리스탈의 조각
                    htmlid = "meet006";
                } else {
                    htmlid = "meet010";
                }
            }
            // 「서울 크리스탈의 조각을 1개 바칩니다」
            else if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().consumeItem(40678, 1)) {
                    pc.addKarma((int) (100 * Config.RATE_KARMA));
                    // 바르로그의 웃음소리가 뇌리를 강타합니다.
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlid = "meet007";
                } else {
                    htmlid = "meet004";
                }
            }
            // 「서울 크리스탈의 조각을 10개 바칩니다」
            else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().consumeItem(40678, 10)) {
                    pc.addKarma((int) (1000 * Config.RATE_KARMA));
                    // 바르로그의 웃음소리가 뇌리를 강타합니다.
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlid = "meet008";
                } else {
                    htmlid = "meet004";
                }
            }
            // 「서울 크리스탈의 조각을 100개 바칩니다」
            else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().consumeItem(40678, 100)) {
                    pc.addKarma((int) (10000 * Config.RATE_KARMA));
                    // 바르로그의 웃음소리가 뇌리를 강타합니다.
                    pc.sendPackets(new S_ServerMessage(1078));
                    htmlid = "meet009";
                } else {
                    htmlid = "meet004";
                }
            }
            // 「바르로그님이 대면시쳐 주세요」
            else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(40909) // 지의 통행증
                        || pc.getInventory().checkItem(40910) // 수의 통행증
                        || pc.getInventory().checkItem(40911) // 불의 통행증
                        || pc.getInventory().checkItem(40912) // 풍의 통행증
                        || pc.getInventory().checkItem(40913) // 지의 인장
                        || pc.getInventory().checkItem(40914) // 수의 인장
                        || pc.getInventory().checkItem(40915) // 불의 인장
                        || pc.getInventory().checkItem(40916) // 풍의 인장
                        || pc.getInventory().checkItem(40917) // 지의 지배자
                        || pc.getInventory().checkItem(40918) // 수의 지배자
                        || pc.getInventory().checkItem(40919) // 불의 지배자
                        || pc.getInventory().checkItem(40920) // 풍의 지배자
                        || pc.getInventory().checkItem(40921)) { // 원소의 지배자
                    htmlid = "";
                } else {
                    L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
                }
            }
        }
        // 흔들거리는 사람
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
            // 「카헬의 의지를 받아들인다」
            if (s.equalsIgnoreCase("1")) {
                if (pc.getKarma() >= 10000000) {
                    pc.setKarma(-1000000);
                    // 야히의 모습이 점점 근처에 느껴집니다.
                    pc.sendPackets(new S_ServerMessage(1079));
                    htmlid = "betray03";
                }
            }
        }
        // 바르로그의 보좌관
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            htmlid = getBarlogEarring(pc, npc, s);
        }
        // 바르로그의 참모
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80073) {
            // 나에게 힘을 주시도록···
            if (s.equalsIgnoreCase("a")) {
                if (pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
                    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
                } else {
                    pc.setSkillEffect(STATUS_CURSE_YAHEE, 1020 * 1000);
                    pc.sendPackets(new S_SkillIconKillBoss(1, 1020));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 750));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
                    pc.sendPackets(new S_ServerMessage(1127));
                }
            }
        }
        // 바르로그의 대장간
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
            int karmaLevel = pc.getKarmaLevel();
            if (s.equalsIgnoreCase("0")) {
                htmlid = "lsmitha";
            } else if (s.equalsIgnoreCase("1")) {
                htmlid = "lsmithb";
            } else if (s.equalsIgnoreCase("2")) {
                htmlid = "lsmithc";
            } else if (s.equalsIgnoreCase("3")) {
                htmlid = "lsmithd";
            } else if (s.equalsIgnoreCase("4")) {
                htmlid = "lsmithe";
            } else if (s.equalsIgnoreCase("5")) {
                htmlid = "lsmithf";
            } else if (s.equalsIgnoreCase("6")) {
                htmlid = "";
            } else if (s.equalsIgnoreCase("7")) {
                htmlid = "lsmithg";
            } else if (s.equalsIgnoreCase("8")) {
                htmlid = "lsmithh";
            }
            // 야히의 셔츠 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("a") && karmaLevel >= 1) {
                materials = new int[]{20158, 40669, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20083};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithaa";
            }
            // 야히의 아모 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("b") && karmaLevel >= 2) {
                materials = new int[]{20144, 40672, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20131};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithbb";
            }
            // 야히의 아모 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("c") && karmaLevel >= 3) {
                materials = new int[]{20075, 40671, 40678};
                counts = new int[]{1, 50, 100};
                createitem = new int[]{20069};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithcc";
            }
            // 야히의 글로브 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("d") && karmaLevel >= 4) {
                materials = new int[]{20183, 40674, 40678};
                counts = new int[]{1, 20, 100};
                createitem = new int[]{20179};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithdd";
            }
            // 야히의 부츠 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("e") && karmaLevel >= 5) {
                materials = new int[]{20190, 40674, 40678};
                counts = new int[]{1, 40, 100};
                createitem = new int[]{20209};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithee";
            }
            // 야히의 링 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("f") && karmaLevel >= 6) {
                materials = new int[]{20078, 40674, 40678};
                counts = new int[]{1, 5, 100};
                createitem = new int[]{20290};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithff";
            }
            // 야히의 아뮤렛트 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("g") && karmaLevel >= 7) {
                materials = new int[]{20078, 40670, 40678};
                counts = new int[]{1, 1, 100};
                createitem = new int[]{20261};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithgg";
            }
            // 야히의 헤룸 / 바르로그의 대장간
            else if (s.equalsIgnoreCase("h") && karmaLevel >= 8) {
                materials = new int[]{40719, 40673, 40678};
                counts = new int[]{1, 1, 100};
                createitem = new int[]{20031};
                createcount = new int[]{1};
                success_htmlid = "";
                failure_htmlid = "lsmithhh";
            }
        }
        // 업의 관리자
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getKarma() >= 10000000) {
                getSoulCrystalByKarma(pc, npc, s);
            }
            htmlid = "";
        }
        // 아르폰스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
            htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
            htmldata = new String[]{String.valueOf(pc.getKarmaPercent())};
        }
        // 차원의 문(흙풍수불)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062) {
            htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
        }
        // 잔크오란탄
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81124) {
            if (s.equalsIgnoreCase("1")) {
                poly(client, 4002);
                htmlid = ""; // 윈도우를 지운다
            } else if (s.equalsIgnoreCase("2")) {
                poly(client, 4004);
                htmlid = ""; // 윈도우를 지운다
            } else if (s.equalsIgnoreCase("3")) {
                poly(client, 4950);
                htmlid = ""; // 윈도우를 지운다
            }
        }

        // 퀘스트 관련
        // 일반 퀘스트 / 라이라
        else if (s.equalsIgnoreCase("contract1")) {
            pc.getQuest().set_step(L1Quest.QUEST_LYRA, 1);
            htmlid = "lyraev2";
        } else if (s.equalsIgnoreCase("contract1yes") || // 라이라 Yes
                s.equalsIgnoreCase("contract1no")) { // 라이라 No

            if (s.equalsIgnoreCase("contract1yes")) {
                htmlid = "lyraev5";
            } else if (s.equalsIgnoreCase("contract1no")) {
                pc.getQuest().set_step(L1Quest.QUEST_LYRA, 0);
                htmlid = "lyraev4";
            }
            int totem = 0;
            if (pc.getInventory().checkItem(40131)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40132)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40133)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40134)) {
                totem++;
            }
            if (pc.getInventory().checkItem(40135)) {
                totem++;
            }
            if (totem != 0) {
                materials = new int[totem];
                counts = new int[totem];
                createitem = new int[totem];
                createcount = new int[totem];

                totem = 0;
                if (pc.getInventory().checkItem(40131)) {
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40131);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40131;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40132)) {
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40132);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40132;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 100;
                    totem++;
                }
                if (pc.getInventory().checkItem(40133)) {
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40133);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40133;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 50;
                    totem++;
                }
                if (pc.getInventory().checkItem(40134)) {
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40134);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40134;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 30;
                    totem++;
                }
                if (pc.getInventory().checkItem(40135)) {
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40135);
                    int i1 = l1iteminstance.getCount();
                    materials[totem] = 40135;
                    counts[totem] = i1;
                    createitem[totem] = L1ItemId.ADENA;
                    createcount[totem] = i1 * 200;
                    totem++;
                }
            }
        }
        // 최근의 물가에 대해
        // 판도라, 코르드, 바르심, 메린, 그렌
        else if (s.equalsIgnoreCase("pandora6") || s.equalsIgnoreCase("cold6")
                || s.equalsIgnoreCase("balsim3")
                || s.equalsIgnoreCase("mellin3") || s.equalsIgnoreCase("glen3")) {
            htmlid = s;
            int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
            int taxRatesCastle = L1CastleLocation
                    .getCastleTaxRateByNpcId(npcid);
            htmldata = new String[]{String.valueOf(taxRatesCastle)};
        }
        // 타운 마스터(이 마을의 주민에게 등록한다)
        else if (s.equalsIgnoreCase("set")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    if (pc.getHomeTownId() == -1) {
                        // \f1 새롭게 주민 등록을 행하려면  시간이 걸립니다.시간을 두고 나서 또 등록해 주세요.
                        pc.sendPackets(new S_ServerMessage(759));
                        htmlid = "";
                    } else if (pc.getHomeTownId() > 0) {
                        // 이미 등록하고 있다
                        if (pc.getHomeTownId() != town_id) {
                            L1Town town = TownTable.getInstance().getTownTable(
                                    pc.getHomeTownId());
                            if (town != null) {
                                // 현재, 당신이 주민 등록 하고 있는 장소는%0입니다.
                                pc.sendPackets(new S_ServerMessage(758, town
                                        .get_name()));
                            }
                            htmlid = "";
                        } else {
                            // 있을 수 없어?
                            htmlid = "";
                        }
                    } else if (pc.getHomeTownId() == 0) {
                        // 등록
                        if (pc.getLevel() < 10) {
                            // \f1주민 등록이 생기는 것은 레벨 10이상의 캐릭터입니다.
                            pc.sendPackets(new S_ServerMessage(757));
                            htmlid = "";
                        } else {
                            int level = pc.getLevel();
                            int cost = level * level * 10;
                            if (pc.getInventory().consumeItem(L1ItemId.ADENA,
                                    cost)) {
                                pc.setHomeTownId(town_id);
                                pc.setContribution(0); // 만약을 위해
                                pc.save();
                            } else {
                                // 아데나가 부족합니다.
                                pc.sendPackets(new S_ServerMessage(337, "$4"));
                            }
                            htmlid = "";
                        }
                    }
                }
            }
        }
        // 타운 마스터(주민 등록을 취소한다)
        else if (s.equalsIgnoreCase("clear")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);
                if (town_id > 0) {
                    if (pc.getHomeTownId() > 0) {
                        if (pc.getHomeTownId() == town_id) {
                            pc.setHomeTownId(-1);
                            pc.setContribution(0); // 공헌도 클리어
                            pc.save();
                        } else {
                            // \f1당신은 다른 마을의 주민입니다.
                            pc.sendPackets(new S_ServerMessage(756));
                        }
                    }
                    htmlid = "";
                }
            }
        }
        // 타운 마스터(마을의 촌장이 누군가를 (듣)묻는다)
        else if (s.equalsIgnoreCase("ask")) {
            if (obj instanceof L1NpcInstance) {
                int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
                int town_id = L1TownLocation.getTownIdByNpcid(npcid);

                if (town_id >= 1 && town_id <= 10) {
                    L1Town town = TownTable.getInstance().getTownTable(town_id);
                    String leader = town.get_leader_name();
                    if (leader != null && leader.length() != 0) {
                        htmlid = "owner";
                        htmldata = new String[]{leader};
                    } else {
                        htmlid = "noowner";
                    }
                }
            }
        }
        // 타운 어드바이저
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876) {
            // 타운 어드바이저(수입에 관한 보고)
            if (s.equalsIgnoreCase("r")) {
                if (obj instanceof L1NpcInstance) {
                    int npcid = ((L1NpcInstance) obj).getNpcTemplate()
                            .get_npcId();
                    int town_id = L1TownLocation.getTownIdByNpcid(npcid);
                }
            }
            // 타운 어드바이저(세율 변경)
            else if (s.equalsIgnoreCase("t")) {

            }
            // 타운 어드바이저(보수를 받는다)
            else if (s.equalsIgnoreCase("c")) {

            }
        }
        // 드로몬드
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70997) {
            // 고마워요, 여행을 떠납니다
            if (s.equalsIgnoreCase("0")) {
                final int[] item_ids = {41146, 4, 20322, 173, 40743,};
                final int[] item_amounts = {1, 1, 1, 1, 500,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getLogName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 1);
                htmlid = "jpe0015";
            }
        }
        // 알렉스(노래하는 섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70999) {
            // 드로몬드의 소개장을 건네준다
            if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().consumeItem(41146, 1)) {
                    final int[] item_ids = {23, 20219, 20193,};
                    final int[] item_amounts = {1, 1, 1,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143,
                                ((L1NpcInstance) obj).getNpcTemplate()
                                        .get_name(), item.getLogName()));
                    }
                    pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 2);
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("2")) {
                final int[] item_ids = {41227}; // 알렉스의 소개장
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getLogName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_AREX, L1Quest.QUEST_END);
                htmlid = "";
            }
        }
        // 포피레아(노래하는 섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71005) {
            // 아이템을 받는다
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(41209)) {
                    final int[] item_ids = {41209,};
                    final int[] item_amounts = {1,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143,
                                ((L1NpcInstance) obj).getNpcTemplate()
                                        .get_name(), item.getItem().getName()));
                    }
                }
                htmlid = ""; // 윈도우를 지운다
            }
            // 아이템을 받는다
            else if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().consumeItem(41213, 1)) {
                    final int[] item_ids = {40029,};
                    final int[] item_amounts = {20,};
                    for (int i = 0; i < item_ids.length; i++) {
                        L1ItemInstance item = pc.getInventory().storeItem(
                                item_ids[i], item_amounts[i]);
                        pc.sendPackets(new S_ServerMessage(143,
                                ((L1NpcInstance) obj).getNpcTemplate()
                                        .get_name(), item.getItem().getName()
                                + " (" + item_amounts[i] + ")"));
                    }
                    htmlid = ""; // 윈도우를 지운다
                }
            }
        }
        // 티미(노래하는 섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71006) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() > 25) {
                    htmlid = "jpe0057";
                } else if (pc.getInventory().checkItem(41213)) { // 티미의 바스켓
                    htmlid = "jpe0056";
                } else if (pc.getInventory().checkItem(41210)
                        || pc.getInventory().checkItem(41211)) { // 연마재, 허브
                    htmlid = "jpe0055";
                } else if (pc.getInventory().checkItem(41209)) { // 포피리아의 의뢰서
                    htmlid = "jpe0054";
                } else if (pc.getInventory().checkItem(41212)) { // 특제 캔디
                    htmlid = "jpe0056";
                    materials = new int[]{41212}; // 특제 캔디
                    counts = new int[]{1};
                    createitem = new int[]{41213}; // 티미의 바스켓
                    createcount = new int[]{1};
                } else {
                    htmlid = "jpe0057";
                }
            }
        }
        // 치료사(노래하는 섬안：HP만 회복)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
            // 치료를 받는다("fullheal"로 리퀘스트가 오는 것은 있는지? )
            if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
                if (pc.getLevel() < 14) {
                    int hp = _random.nextInt(21) + 70;
                    pc.setCurrentHp(pc.getCurrentHp() + hp);
                    pc.sendPackets(new S_ServerMessage(77));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    htmlid = ""; // 윈도우를 지운다
                }
            }
        }
        // 마법사 멀린
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72001) {
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "merlin2";
            } else if (s.equalsIgnoreCase("teleport giranD")) {
                htmlid = "";
                if (!GiranController.getInstance().getGiranTime()) {
                    pc.sendPackets(new S_SystemMessage("지금은 기란감옥 입장시간이 아닙니다."));
                    return;
                }
                if (GiranController.getInstance().getGiranTime()) {
                    Random random = new Random();
                    int i13 = 32809 + random.nextInt(2);
                    int k19 = 32732 + random.nextInt(2);
                    L1Teleport.teleport(pc, i13, k19, (short) 53, 5, true);
                    pc.sendPackets(new S_SystemMessage("당신은 앞으로 3시간동안 기란감옥 체류가능합니다."));
                    return;
                }
            }
        }
        //// 상아탑 모험가 By Hamilton
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72006) {
            if (pc.hasSkillEffect(L1SkillId.SANGABUFF)) {
                pc.removeSkillEffect(L1SkillId.SANGABUFF);
            }
            int[] allBuffSkill = {SANGA};
            pc.setBuffnoch(1);
            L1SkillUse l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            htmlid = "aitexplorer1";
        }
        //// 상아탑 모험가 By Hamilton
        // 크레이
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777786) {
            if (pc.hasSkillEffect(L1SkillId.ANTA_BLOOD)) {
                pc.removeSkillEffect(L1SkillId.ANTA_BLOOD);
            }
            int[] allBuffSkill = {CRAY};
            pc.setBuffnoch(1);
            L1SkillUse l1skilluse = new L1SkillUse();
            for (int i = 0; i < allBuffSkill.length; i++) {
                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
            }
            htmlid = "grayknight2";
        }
        // 크레이
        // 수상한 조련사
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 73006) {    // 수상한 조련사
            int npcid = 0;
            int randomrange = 2;
            if (s.equalsIgnoreCase("buy 5")) {
                if (pc.getInventory().checkItem(41159, 1000)) {    // 신비한 날개 깃털
                    pc.getInventory().consumeItem(41159, 1000);  // 신비한 날개 깃털
                    String param = "46044"; //아기판다곰
                    //spawn(pc, param);
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("신비한 깃털이 부족합니다."));
                }
            }
            if (s.equalsIgnoreCase("buy 6")) {
                if (pc.getInventory().checkItem(41159, 1000)) {    // 신비한 날개 깃털
                    pc.getInventory().consumeItem(41159, 1000);  // 신비한 날개 깃털
                    String param = "46042"; //아기캥거루
                    //spawn(pc, param);
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("신비한 깃털이 부족합니다."));
                }
            }
        }
        // 반쿠
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777784) { //npc
            int npcid = 0;
            int randomrange = 2;
            if (s.equalsIgnoreCase("buy 7")) {
                if (pc.getInventory().checkItem(555574, 1)) {    // 녹색 알
                    pc.getInventory().consumeItem(555574, 1); //녹색 알
                    String param = "777790"; //해츨링 여
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else {
                    pc.sendPackets(new S_SystemMessage("\\fV녹색 해츨링 알이 부족합니다."));
                }
            }
            if (s.equalsIgnoreCase("buy 8")) {
                if (pc.getInventory().checkItem(555575, 1)) {      //황색 알
                    pc.getInventory().consumeItem(555575, 1);  //황색 알
                    String param = "777787"; //해츨링 남
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else {
                    pc.sendPackets(new S_SystemMessage("\\fV황색 해츨링 알이 부족합니다."));
                }
            }

        }
        // 치료사(훈련장：HPMP 회복)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71037) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() < 99) {
                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());
                    pc.sendPackets(new S_ServerMessage(77));
                    pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                    pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                    pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                }
            }
        }
        // 치료사(서부)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71030) {
            if (s.equalsIgnoreCase("fullheal")) {
                if (pc.getLevel() < 99) {
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
                        pc.setCurrentHp(pc.getMaxHp());
                        //pc.setCurrentMp(pc.getMaxMp());
                        pc.sendPackets(new S_ServerMessage(77));
                        pc.sendPackets(new S_SkillSound(pc.getId(), 830));
                        pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
                        pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
                        if (pc.isInParty()) { // 파티중
                            pc.getParty().updateMiniHP(pc);
                        }
                    } else {
                        pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
                    }
                }
            }
        }
        // 왈가닥 세레이션사
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71002) {
            // 왈가닥 세레이션 마법을 걸쳐 준다
            if (s.equalsIgnoreCase("0")) {
                if (pc.getLevel() <= 13) {
                    L1SkillUse skillUse = new L1SkillUse();
                    skillUse.handleCommands(pc, L1SkillId.CANCELLATION, pc
                                    .getId(), pc.getX(), pc.getY(), null, 0,
                            L1SkillUse.TYPE_NPCBUFF, (L1NpcInstance) obj);
                    htmlid = ""; // 윈도우를 지운다
                }
            }
        }
        // 케스킨(노래하는 섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
            if (s.equalsIgnoreCase("0")) {
                final int[] item_ids = {41225,};
                final int[] item_amounts = {1,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                }
                htmlid = "jpe0083";
            }
        }
        // 루케인(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
            // 아이템을 받는다
            if (s.equalsIgnoreCase("0")) {
                final int[] item_ids = {40701,};
                final int[] item_amounts = {1,};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 1);
                htmlid = "lukein8";
            }
            if (s.equalsIgnoreCase("1")) {
                pc.getQuest().set_end(L1Quest.QUEST_TBOX3);
                materials = new int[]{40716}; // 할아버지의 보물
                counts = new int[]{1};
                createitem = new int[]{20269}; // 해골목걸이
                createcount = new int[]{1};
                htmlid = "lukein0";
            } else if (s.equalsIgnoreCase("2")) {
                htmlid = "lukein12";
                pc.getQuest().set_step(L1Quest.QUEST_RESTA, 3);
            }
        }
        // 작은 상자-1번째
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
            if (s.equalsIgnoreCase("0")) {
                materials = new int[]{40701}; // 작은 보물의 지도
                counts = new int[]{1};
                createitem = new int[]{40702}; // 작은 봉투
                createcount = new int[]{1};
                htmlid = "maptbox1";
                pc.getQuest().set_end(L1Quest.QUEST_TBOX1);
                int[] nextbox = {1, 2, 3};
                int pid = _random.nextInt(nextbox.length);
                int nb = nextbox[pid];
                if (nb == 1) { // b지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 2);
                } else if (nb == 2) { // c지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 3);
                } else if (nb == 3) { // d지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 4);
                }
            }
        }
        // 작은 상자-2번째
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066) {
            if (s.equalsIgnoreCase("0")) {
                materials = new int[]{40701}; // 작은 보물의 지도
                counts = new int[]{1};
                createitem = new int[]{40702}; // 작은 봉투
                createcount = new int[]{1};
                htmlid = "maptbox1";
                pc.getQuest().set_end(L1Quest.QUEST_TBOX2);
                int[] nextbox2 = {1, 2, 3, 4, 5, 6};
                int pid = _random.nextInt(nextbox2.length);
                int nb2 = nextbox2[pid];
                if (nb2 == 1) { // e지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 5);
                } else if (nb2 == 2) { // f지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 6);
                } else if (nb2 == 3) { // g지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 7);
                } else if (nb2 == 4) { // h지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 8);
                } else if (nb2 == 5) { // i지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 9);
                } else if (nb2 == 6) { // j지점
                    pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 10);
                }
            }
        }
        // 작은 상자-3번째
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71067
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71068
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71069
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71070
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71071
                || ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71072) {
            if (s.equalsIgnoreCase("0")) {
                htmlid = "maptboxi";
                materials = new int[]{40701}; // 작은 보물의 지도
                counts = new int[]{1};
                createitem = new int[]{40716}; // 할아버지의 보물
                createcount = new int[]{1};
                pc.getQuest().set_end(L1Quest.QUEST_TBOX3);
                pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 11);
            }
        }
        // 시미즈(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
            // 아들을 찾는다
            if (s.equalsIgnoreCase("a")) {
                pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 1);
                htmlid = "SIMIZZ7";
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(40661)
                        && pc.getInventory().checkItem(40662)
                        && pc.getInventory().checkItem(40663)) {
                    htmlid = "SIMIZZ8";
                    pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 2);
                    materials = new int[]{40661, 40662, 40663};
                    counts = new int[]{1, 1, 1};
                    createitem = new int[]{20044};
                    createcount = new int[]{1};
                } else {
                    htmlid = "SIMIZZ9";
                }
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "SIMIZZ12";
                pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, L1Quest.QUEST_END);
            }
        }
        // 도일(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
            // 러쉬에 대해 듣는다
            if (s.equalsIgnoreCase("3")) {
                htmlid = "doil4";
            } else if (s.equalsIgnoreCase("6")) {
                htmlid = "doil6";
            } else if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(40714)) {
                    htmlid = "doil8";
                    materials = new int[]{40714};
                    counts = new int[]{1};
                    createitem = new int[]{40647};
                    createcount = new int[]{1};
                    pc.getQuest().set_step(L1Quest.QUEST_DOIL,
                            L1Quest.QUEST_END);
                } else {
                    htmlid = "doil7";
                }
            }
        }
        // 루디 안(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
            // 루디 안의 부탁을 받아들인다
            if (s.equalsIgnoreCase("A")) {
                htmlid = "rudian6";
                final int[] item_ids = {40700};
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                }
                pc.getQuest().set_step(L1Quest.QUEST_RUDIAN, 1);
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(40710)) {
                    htmlid = "rudian8";
                    materials = new int[]{40700, 40710};
                    counts = new int[]{1, 1};
                    createitem = new int[]{40647};
                    createcount = new int[]{1};
                    pc.getQuest().set_step(L1Quest.QUEST_RUDIAN,
                            L1Quest.QUEST_END);
                } else {
                    htmlid = "rudian9";
                }
            }
        }
        // 레스타(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
            // 동료들에 대해
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
                    htmlid = "resta6";
                } else {
                    htmlid = "resta4";
                }
            } else if (s.equalsIgnoreCase("B")) {
                htmlid = "resta10";
                pc.getQuest().set_step(L1Quest.QUEST_RESTA, 2);
            }
        }
        // 카좀스(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
            // 지도를 조합해 주세요
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(40647, 3)) {
                    htmlid = "cadmus6";
                    pc.getInventory().consumeItem(40647, 3);
                    pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 2);
                } else {
                    htmlid = "cadmus5";
                    pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 1);
                }
            }
        }
        // 카밋트(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71062) {
            // 할아버지가 기다리고 있으니 함께 오세요
            if (s.equalsIgnoreCase("start")) {
                htmlid = "kamit2";
                final int[] item_ids = {40711};
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 3);
                }
            }
        }
        // 카미라(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "kamyla7";
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 1);
            } else if (s.equalsIgnoreCase("c")) {
                htmlid = "kamyla10";
                pc.getInventory().consumeItem(40644, 1);
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 3);
            } else if (s.equalsIgnoreCase("e")) {
                htmlid = "kamyla13";
                pc.getInventory().consumeItem(40630, 1);
                pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 4);
            } else if (s.equalsIgnoreCase("i")) {
                htmlid = "kamyla25";
            } else if (s.equalsIgnoreCase("b")) { // 카 미라(흐랑코의 미궁)
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 1) {
                    L1Teleport.teleport(pc, 32679, 32742, (short) 482, 5, true);
                }
            } else if (s.equalsIgnoreCase("d")) { // 카 미라(디에고가 닫힌 뇌)
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 3) {
                    L1Teleport.teleport(pc, 32736, 32800, (short) 483, 5, true);
                }
            } else if (s.equalsIgnoreCase("f")) { // 카 미라(호세 지하소굴)
                if (pc.getQuest().get_step(L1Quest.QUEST_KAMYLA) == 4) {
                    L1Teleport.teleport(pc, 32746, 32807, (short) 484, 5, true);
                }
            }
        }
        // 흐랑코(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
            // 카미라에 당신의 결백을 증명합시다
            if (s.equalsIgnoreCase("a")) {
                htmlid = "francu10";
                final int[] item_ids = {40644};
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 2);
                }
            }
        }
        // 시련의 크리스탈 2(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
            // 네, 무기와 스크롤을 주세요
            if (s.equalsIgnoreCase("a")) {
                htmlid = "";
                final int[] item_ids = {246, 247, 248, 249, 40660};
                final int[] item_amounts = {1, 1, 1, 1, 5};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 1);
                }
            } else if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkEquipped(246)
                        || pc.getInventory().checkEquipped(247)
                        || pc.getInventory().checkEquipped(248)
                        || pc.getInventory().checkEquipped(249)) {
                    htmlid = "jcrystal5";
                } else if (pc.getInventory().checkItem(40660)) {
                    htmlid = "jcrystal4";
                } else {
                    pc.getInventory().consumeItem(246, 1);
                    pc.getInventory().consumeItem(247, 1);
                    pc.getInventory().consumeItem(248, 1);
                    pc.getInventory().consumeItem(249, 1);
                    pc.getInventory().consumeItem(40620, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 2);
                    L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
                }
            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkEquipped(246)
                        || pc.getInventory().checkEquipped(247)
                        || pc.getInventory().checkEquipped(248)
                        || pc.getInventory().checkEquipped(249)) {
                    htmlid = "jcrystal5";
                } else {
                    pc.getInventory().checkItem(40660);
                    L1ItemInstance l1iteminstance = pc.getInventory()
                            .findItemId(40660);
                    int sc = l1iteminstance.getCount();
                    if (sc > 0) {
                        pc.getInventory().consumeItem(40660, sc);
                    } else {
                    }
                    pc.getInventory().consumeItem(246, 1);
                    pc.getInventory().consumeItem(247, 1);
                    pc.getInventory().consumeItem(248, 1);
                    pc.getInventory().consumeItem(249, 1);
                    pc.getInventory().consumeItem(40620, 1);
                    pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 0);
                    L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
                }
            }
        }
        // 시련의 크리스탈 2(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
            // 안녕히!
            if (s.equalsIgnoreCase("a")) {
                htmlid = "";
                pc.getInventory().consumeItem(40654, 1);
                pc.getQuest()
                        .set_step(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
                L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
            }
        }
        // 리자드만의 장로(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
            // 그 전사는 지금 어디등에 에 있습니까?
            if (s.equalsIgnoreCase("A")) {
                htmlid = "lelder5";
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 1);
                // 보물을 되찾아 옵니다
            } else if (s.equalsIgnoreCase("B")) {
                htmlid = "lelder10";
                pc.getInventory().consumeItem(40633, 1);
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 3);
            } else if (s.equalsIgnoreCase("C")) { // 리자드맨 장갑 주도록 추가
                htmlid = "lelder13";
                if (pc.getQuest().get_step(L1Quest.QUEST_LIZARD) == L1Quest.QUEST_END) {
                }
                materials = new int[]{40634};
                counts = new int[]{1};
                createitem = new int[]{20167}; // 리자드망로브
                createcount = new int[]{1};
                pc.sendPackets(new S_SystemMessage("리자드맨 장로가 당신에게 리자드맨 영웅의 장갑을 주었습니다."));
                pc.getQuest().set_step(L1Quest.QUEST_LIZARD, L1Quest.QUEST_END);
            }
        }
        // 완전히 지쳐 버린 리자드만파이타(해적섬)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71075) {
            // 리자드만의 보고서
            if (s.equalsIgnoreCase("start")) {
                htmlid = "llizard2";
                final int[] item_ids = {40633};
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 2);
                }
            } else {
            }
        }
        // 용병 단장 티온
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71198) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(71198) != 0
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41339, 5)) { // 망자의 메모
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            41340); // 용병 단장 티온의 소개장
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getQuest().set_step(71198, 1);
                    htmlid = "tion4";
                } else {
                    htmlid = "tion9";
                }
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getQuest().get_step(71198) != 1
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41341, 1)) { // 제론의 교본
                    pc.getQuest().set_step(71198, 2);
                    htmlid = "tion5";
                } else {
                    htmlid = "tion10";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getQuest().get_step(71198) != 2
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41343, 1)) { // 파프리온의 핏자국
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            21057); // 훈련 기사의 망토 1
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getQuest().set_step(71198, 3);
                    htmlid = "tion6";
                } else {
                    htmlid = "tion12";
                }
            } else if (s.equalsIgnoreCase("D")) {
                if (pc.getQuest().get_step(71198) != 3
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41344, 1)) { // 수의 정수
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            21058); // 훈련 기사의 망토 2
                    if (item != null) {
                        pc.getInventory().consumeItem(21057, 1); // 훈련 기사의 망토 1
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getQuest().set_step(71198, 4);
                    htmlid = "tion7";
                } else {
                    htmlid = "tion13";
                }
            } else if (s.equalsIgnoreCase("E")) {
                if (pc.getQuest().get_step(71198) != 4
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41345, 1)) { // 산성의 유액
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            21059); // 포이즌서펜트크로크
                    if (item != null) {
                        pc.getInventory().consumeItem(21058, 1); // 훈련 기사의 망토 2
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getQuest().set_step(71198, 0);
                    pc.getQuest().set_step(71199, 0);
                    htmlid = "tion8";
                } else {
                    htmlid = "tion15";
                }
            }
        }
        // 제론
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71199) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getQuest().get_step(71199) != 0
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().checkItem(41340, 1)) { // 용병 단장 티온의 소개장
                    pc.getQuest().set_step(71199, 1);
                    htmlid = "jeron2";
                } else {
                    htmlid = "jeron10";
                }
            } else if (s.equalsIgnoreCase("B")) {
                if (pc.getQuest().get_step(71199) != 1
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(40308, 1000000)) {
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            41341); // 제론의 교본
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getInventory().consumeItem(41340, 1);
                    pc.getQuest().set_step(71199, 255);
                    htmlid = "jeron6";
                } else {
                    htmlid = "jeron8";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getQuest().get_step(71199) != 1
                        || pc.getInventory().checkItem(21059, 1)) {
                    return;
                }
                if (pc.getInventory().consumeItem(41342, 1)) { // 메듀사의 피
                    L1ItemInstance item = ItemTable.getInstance().createItem(
                            41341); // 제론의 교본
                    if (item != null) {
                        if (pc.getInventory().checkAddItem(item, 1) == 0) {
                            pc.getInventory().storeItem(item);
                            pc.sendPackets(new S_ServerMessage(143,
                                    ((L1NpcInstance) obj).getNpcTemplate()
                                            .get_name(), item.getItem()
                                    .getName())); // \f1%0이%1를 주었습니다.
                        }
                    }
                    pc.getInventory().consumeItem(41340, 1);
                    pc.getQuest().set_step(71199, 255);
                    htmlid = "jeron5";
                } else {
                    htmlid = "jeron9";
                }
            }
        }
        // 점성술사 케프리샤
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
            // 케프리샤와 영혼의 계약을 맺는다
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(41312)) { // 점성술사의 항아리
                    L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
                    if (item != null) {
                        pc.sendPackets(new S_ServerMessage(143,
                                ((L1NpcInstance) obj).getNpcTemplate()
                                        .get_name(), item.getItem().getName())); // \f1%0이%1를 주었습니다.
                        pc.getQuest().set_step(L1Quest.QUEST_KEPLISHA,
                                L1Quest.QUEST_END);
                    }
                    htmlid = "keplisha7";
                }
            }
            // 원조금을 내 운세를 본다
            else if (s.equalsIgnoreCase("1")) {
                if (!pc.getInventory().checkItem(41314)) { // 점성술사의 부적
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                        materials = new int[]{L1ItemId.ADENA, 41313}; // 아데나, 점성술사의 구슬
                        counts = new int[]{1000, 1};
                        createitem = new int[]{41314}; // 점성술사의 부적
                        createcount = new int[]{1};
                        int htmlA = _random.nextInt(3) + 1;
                        int htmlB = _random.nextInt(100) + 1;
                        switch (htmlA) {
                            case 1:
                                htmlid = "horosa" + htmlB; // horosa1 ~ horosa100
                                break;
                            case 2:
                                htmlid = "horosb" + htmlB; // horosb1 ~ horosb100
                                break;
                            case 3:
                                htmlid = "horosc" + htmlB; // horosc1 ~ horosc100
                                break;
                            default:
                                break;
                        }
                    } else {
                        htmlid = "keplisha8";
                    }
                }
            }
            // 케프리샤로부터 축복을 받는다
            else if (s.equalsIgnoreCase("2")) {
                if (pc.getTempCharGfx() != pc.getClassId()) {
                    htmlid = "keplisha9";
                } else {
                    if (pc.getInventory().checkItem(41314)) { // 점성술사의 부적
                        pc.getInventory().consumeItem(41314, 1); // 점성술사의 부적
                        int html = _random.nextInt(9) + 1;
                        int PolyId = 6180 + _random.nextInt(64);
                        polyByKeplisha(client, PolyId);
                        switch (html) {
                            case 1:
                                htmlid = "horomon11";
                                break;
                            case 2:
                                htmlid = "horomon12";
                                break;
                            case 3:
                                htmlid = "horomon13";
                                break;
                            case 4:
                                htmlid = "horomon21";
                                break;
                            case 5:
                                htmlid = "horomon22";
                                break;
                            case 6:
                                htmlid = "horomon23";
                                break;
                            case 7:
                                htmlid = "horomon31";
                                break;
                            case 8:
                                htmlid = "horomon32";
                                break;
                            case 9:
                                htmlid = "horomon33";
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            // 항아리를 나누어 계약을 파기한다
            else if (s.equalsIgnoreCase("3")) {
                if (pc.getInventory().checkItem(41312)) { // 점성술사의 항아리
                    pc.getInventory().consumeItem(41312, 1);
                    htmlid = "";
                }
                if (pc.getInventory().checkItem(41313)) { // 점성술사의 구슬
                    pc.getInventory().consumeItem(41313, 1);
                    htmlid = "";
                }
                if (pc.getInventory().checkItem(41314)) { // 점성술사의 부적
                    pc.getInventory().consumeItem(41314, 1);
                    htmlid = "";
                }
            }
        }
        // 개상인 로드니 by 아스라이
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70077) {    // 로드니
            int npcid = 0;
            int randomrange = 2;
            if (s.equalsIgnoreCase("buy 1")) {
                if (pc.getInventory().checkItem(40308, 50000)) {    // 아덴
                    pc.getInventory().consumeItem(40308, 50000);  // 아덴
                    String param = "45042"; //도베르만
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("아데나(50000) 가 부족합니다."));
                }
            }
            if (s.equalsIgnoreCase("buy 2")) {
                if (pc.getInventory().checkItem(40308, 50000)) {    // 아덴
                    pc.getInventory().consumeItem(40308, 50000);  // 아덴
                    String param = "45034"; //세퍼드
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("아데나(50000) 가 부족합니다."));
                }
            }
            if (s.equalsIgnoreCase("buy 3")) {
                if (pc.getInventory().checkItem(40308, 50000)) {    // 아덴
                    pc.getInventory().consumeItem(40308, 50000);  // 아덴
                    String param = "45046"; //비글
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("아데나(50000) 가 부족합니다."));
                }
            }
            if (s.equalsIgnoreCase("buy 4")) {
                if (pc.getInventory().checkItem(40308, 50000)) {    // 아덴
                    pc.getInventory().consumeItem(40308, 50000);  // 아덴
                    String param = "45047"; //세인트 버나드
                    npcid = Integer.parseInt(param);
                    mobspawn1(pc, npcid, randomrange);
                    htmlid = "";
                } else { // 재료가 부족한 경우
                    htmlid = "";
                    pc.sendPackets(new S_SystemMessage("아데나(50000) 가 부족합니다."));
                }
            }


            ///////////// 가이아 수정 ///////////////////////// 낚시터 1, 2, 3룸///////////////////
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80082) { // 낚시꼬마(IN)
            // 「마법 낚싯대」
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
                    materials = new int[]{L1ItemId.ADENA};
                    counts = new int[]{1000};
                    //    createitem = new int[] { 41293 };
                    //    createcount = new int[] { 1 };
                    L1PolyMorph.undoPoly(pc);
                    L1Teleport.teleport(pc, 32736, 32810, (short) 5302, 5, true);
                } else {
                    htmlid = "fk_in_0";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80083) { // 낚시꼬마(OUT)
            // 「낚시를 멈추어 밖에 나온다」
            if (s.equalsIgnoreCase("teleportURL")) {
                if (!pc.getInventory().checkItem(40308, 1)) {
                    htmlid = "";
                } else if (pc.getInventory().consumeItem(40308, 1)) {
                    L1Teleport.teleport(pc, 32613, 32781, (short) 4, 4, true);
                }
            }
            /////////////////////////////// 가이아 수정 /////////////////낚시터 1, 2, 3룸///////////////////
            // 시종장 맘몬
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200056) { // 시종장 맘몬
            if (s.equalsIgnoreCase("teleportURL")) {
                htmlid = "gr_mammon3";
            }
        }
        //조우의 돌골렘
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200012) { // npcid
            if (s.equalsIgnoreCase("A")) {  // 마력의 단검
                if (pc.getInventory().checkEnchant(5, 7) && pc.getInventory().checkEnchant(6, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(510, 1);
                    pc.getInventory().deleteEnchant(5, 7);
                    pc.getInventory().deleteEnchant(6, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem9";
                } else {
                    htmlid = "joegolem15";
                }
            }
            if (s.equalsIgnoreCase("B")) { // 광풍의 도끼
                if (pc.getInventory().checkEnchant(145, 7) && pc.getInventory().checkEnchant(148, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(511, 1);
                    pc.getInventory().deleteEnchant(145, 7);
                    pc.getInventory().deleteEnchant(148, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem10";
                } else {
                    htmlid = "joegolem15";
                }
            }
            if (s.equalsIgnoreCase("C")) { // 파멸의 대검
                if (pc.getInventory().checkEnchant(52, 7) && pc.getInventory().checkEnchant(64, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(512, 1);
                    pc.getInventory().deleteEnchant(52, 7);
                    pc.getInventory().deleteEnchant(64, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem11";
                } else {
                    htmlid = "joegolem15";
                }
            }
            if (s.equalsIgnoreCase("D")) { // 아크메이지의 지팡이
                if (pc.getInventory().checkEnchant(125, 7) && pc.getInventory().checkEnchant(129, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(513, 1);
                    pc.getInventory().deleteEnchant(125, 7);
                    pc.getInventory().deleteEnchant(129, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem12";
                } else {
                    htmlid = "joegolem15";
                }
            }
            if (s.equalsIgnoreCase("E")) { // 혹한의 창
                if (pc.getInventory().checkEnchant(99, 7) && pc.getInventory().checkEnchant(104, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(514, 1);
                    pc.getInventory().deleteEnchant(99, 7);
                    pc.getInventory().deleteEnchant(104, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem13";
                } else {
                    htmlid = "joegolem15";
                }
            }
            if (s.equalsIgnoreCase("F")) { // 뇌신검
                if (pc.getInventory().checkEnchant(32, 7) && pc.getInventory().checkEnchant(42, 7)
                        && pc.getInventory().checkItem(41246, 1000) && pc.getInventory().checkItem(500005, 10)) {
                    pc.getInventory().storeItem(515, 1);
                    pc.getInventory().deleteEnchant(32, 7);
                    pc.getInventory().deleteEnchant(42, 7);
                    pc.getInventory().consumeItem(41246, 1000);
                    pc.getInventory().consumeItem(500005, 10);
                    htmlid = "joegolem14";
                } else {
                    htmlid = "joegolem15";
                }
            }
        }
        // 조우의 돌골렘 (테베 사막)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200036) {
            if (s.equalsIgnoreCase("A")) {  // 균열의 핵
                if (pc.getInventory().checkItem(500030, 100)) {    // 시간 균열 파편
                    pc.getInventory().storeItem(500031, 1);       // 균열의 핵
                    pc.getInventory().consumeItem(500030, 100);   // 시간 균열 파편
                    htmlid = "joegolem18";
                } else {
                    htmlid = "joegolem19";
                }
            }
            if (s.equalsIgnoreCase("B")) {  // 균열의 핵   //조우의 돌골렘 ( 테베라스 ) 아덴 텔레포트
                if (pc.getInventory().consumeItem(500030, 1)) {
                    L1Teleport.teleport(pc, 32802, 32855, (short) 4, 5, true);
                } else {
                    htmlid = "joegolem20";
                }
            }
        }
        //// 조우의 돌골렘 (티칼)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 2000072) {
            if (s.equalsIgnoreCase("A")) {  // 균열의 핵
                if (pc.getInventory().checkItem(500030, 100)) {    // 시간 균열 파편
                    pc.getInventory().storeItem(500031, 1);       // 균열의 핵
                    pc.getInventory().consumeItem(500030, 100);   // 시간 균열 파편
                    htmlid = "joegolem18";
                } else {
                    htmlid = "joegolem19";
                }
            }
            if (s.equalsIgnoreCase("B")) {  // 균열의 핵   //조우의 돌골렘 ( 테베라스 ) 아덴 텔레포트
                if (pc.getInventory().consumeItem(500030, 1)) {
                    L1Teleport.teleport(pc, 32802, 32855, (short) 4, 5, true);
                } else {
                    htmlid = "joegolem20";
                }
            }
        }
        // 대장장이 퓨알 (베히모스)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200037) {
            if (s.equalsIgnoreCase("6")) {
                if (pc.getInventory().checkItem(17, 1) && pc.getInventory().checkItem(40393, 1)
                        && pc.getInventory().checkItem(40053, 20)
                        && pc.getInventory().checkItem(40406, 10)
                        && pc.getInventory().checkItem(40308, 1000000)) {
                    pc.getInventory().storeItem(501, 1);
                    pc.getInventory().consumeItem(17, 1);
                    pc.getInventory().consumeItem(40393, 1);
                    pc.getInventory().consumeItem(40053, 20);
                    pc.getInventory().consumeItem(40406, 10);
                    pc.getInventory().consumeItem(40308, 1000000);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200037) {
            if (s.equalsIgnoreCase("7")) {
                if (pc.getInventory().checkItem(20140, 1) && pc.getInventory().checkItem(40505, 50)
                        && pc.getInventory().checkItem(40495, 50)
                        && pc.getInventory().checkItem(40504, 20)
                        && pc.getInventory().checkItem(40521, 20)
                        && pc.getInventory().checkItem(40445, 3)
                        && pc.getInventory().checkItem(40308, 1000000)) {
                    pc.getInventory().storeItem(22000, 1);
                    pc.getInventory().consumeItem(20140, 1);
                    pc.getInventory().consumeItem(40505, 50);
                    pc.getInventory().consumeItem(40495, 50);
                    pc.getInventory().consumeItem(40521, 20);
                    pc.getInventory().consumeItem(40504, 20);
                    pc.getInventory().consumeItem(40445, 3);
                    pc.getInventory().consumeItem(40308, 1000000);
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200037) {
            if (s.equalsIgnoreCase("8")) {
                if (pc.getInventory().checkItem(20143, 1) && pc.getInventory().checkItem(40445, 5)
                        && pc.getInventory().checkItem(40308, 1000000)) {
                    pc.getInventory().storeItem(22003, 1);
                    pc.getInventory().consumeItem(20143, 1);
                    pc.getInventory().consumeItem(40445, 5);
                    pc.getInventory().consumeItem(40308, 1000000);
                }
            }
        }
        // 디에츠 (빛고목 제작)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71179) // 디에츠(빛고목 제작)
        {
            if (s.equalsIgnoreCase("A")) // 복원된 고대의 목걸이
            {
                Random random = new Random();
                if (pc.getInventory().checkItem(49028, 1) && pc.getInventory().checkItem(49029, 1)
                        && pc.getInventory().checkItem(49030, 1) && pc.getInventory().checkItem(41139, 1)) { // 보석과 볼품없는 목걸이 확인
                    if (random.nextInt(10) > 7) // 30%의 확률로 성공
                    {
                        materials = new int[]{49028, 49029, 49030, 41139};
                        counts = new int[]{1, 1, 1, 1};
                        createitem = new int[]{41140}; // 복원된 고대의 목걸이
                        createcount = new int[]{1};
                        htmlid = "dh8";
                    } else { // 실패의 경우 아이템만 사라짐
                        pc.getInventory().consumeItem(49028, 1);
                        pc.getInventory().consumeItem(49029, 1);
                        pc.getInventory().consumeItem(49030, 1);
                        pc.getInventory().consumeItem(41139, 1);
                        htmlid = "dh7";
                    }
                } else { // 재료가 부족한 경우
                    htmlid = "dh6";
                }
            } else if (s.equalsIgnoreCase("B")) // 빛나는 고대의 목걸이 제작을 부탁한다.
            {
                Random random = new Random();
                if (pc.getInventory().checkItem(49027, 1) && pc.getInventory().checkItem(41140, 1)) { // 다이아몬드와 복원된 목걸이
                    if (random.nextInt(10) > 7) // 30%의 확률로 성공시
                    {
                        materials = new int[]{49027, 41140};
                        counts = new int[]{1, 1};
                        createitem = new int[]{20422};
                        createcount = new int[]{1};
                        htmlid = "dh9";
                    } else { // 실패시 아이템만 증발
                        pc.getInventory().consumeItem(49027, 1);
                        pc.getInventory().consumeItem(41140, 1);
                        htmlid = "dh7";
                    }
                } else { // 재료가 부족한 경우
                    htmlid = "dh6";
                }
            }
        }
        /*상아탑 탐험가 by.쑨*/
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72004) {
            if (s.equalsIgnoreCase("1")) {
                if (!pc.getInventory().checkItem(500211, 1)) {
                    pc.getInventory().storeItem(500211, 1);    // 티칼 달력
                    pc.sendPackets(new S_SystemMessage("상아탑 탐험가가 당신에게 티칼 달력을 주었습니다."));
                    htmlid = "itexplorer1";
                } else {
                    htmlid = "itexplorer2";
                }
            }
        }
        // 로우기
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71178) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(41139, 1)) {    // 볼품없는 고대 목걸이
                    pc.getInventory().storeItem(40308, 500);       // 아데나
                    pc.getInventory().consumeItem(41139, 1);   // 볼품없는 고대 목걸이
                    pc.sendPackets(new S_SystemMessage("아데나(500)를 얻었습니다."));
                    htmlid = "ru6";
                } else {  // 재료가 부족한 경우
                    htmlid = "ru4";
                }
            }
            if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(49028, 1)) {    // 타로스의 루비
                    pc.getInventory().storeItem(40308, 100);       // 아데나
                    pc.getInventory().consumeItem(49028, 1);   // 타로스의 루비
                    pc.sendPackets(new S_SystemMessage("아데나(100)를 얻었습니다."));
                    htmlid = "ru6";
                } else {  // 재료가 부족한 경우
                    htmlid = "ru4";
                }
            }
            if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(49030, 1)) {    // 타로스의 에메랄드
                    pc.getInventory().storeItem(40308, 100);       // 아데나
                    pc.getInventory().consumeItem(49030, 1);   // 타로스의 에메랄드
                    pc.sendPackets(new S_SystemMessage("아데나(100)를 얻었습니다."));
                    htmlid = "ru6";
                } else {  // 재료가 부족한 경우
                    htmlid = "ru4";
                }
            }
            if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(49029, 1)) {    // 타로스의 사파이어
                    pc.getInventory().storeItem(40308, 100);       // 아데나
                    pc.getInventory().consumeItem(49029, 1);   // 타로스의 사파이어
                    pc.sendPackets(new S_SystemMessage("아데나(100)를 얻었습니다."));
                    htmlid = "ru6";
                } else {  // 재료가 부족한 경우
                    htmlid = "ru4";
                }
            }
            if (s.equalsIgnoreCase("E")) {
                if (pc.getInventory().checkItem(41140, 1)) {    // 복원된 고대 목걸이
                    pc.getInventory().storeItem(40308, 10000);       // 아데나
                    pc.getInventory().consumeItem(41140, 1);   // 복원된 고대 목걸이
                    pc.sendPackets(new S_SystemMessage("아데나(10000)를 얻었습니다."));
                    htmlid = "ru6";
                } else {  // 재료가 부족한 경우
                    htmlid = "ru4";
                }
            }
        }
        // 제이프
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71180) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(49026, 1000)) {    // 고대금화
                    pc.getInventory().storeItem(41093, 1);   // 꿈꾸는 곰인형
                    pc.getInventory().consumeItem(49026, 1000);   // 고대금화
                    htmlid = "jp6";
                    pc.sendPackets(new S_SystemMessage("꿈꾸는 곰인형을 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "jp5";
                }
            }
            if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(49026, 5000)) {    // 고대금화
                    pc.getInventory().storeItem(41094, 1);   // 유혹의 향수
                    pc.getInventory().consumeItem(49026, 5000);   // 고대금화
                    htmlid = "jp6";
                    pc.sendPackets(new S_SystemMessage("유혹의 향수를 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "jp5";
                }
            }
            if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(49026, 10000)) {    // 고대금화
                    pc.getInventory().storeItem(41095, 1);   // 사랑스러운 드레스
                    pc.getInventory().consumeItem(49026, 10000);   // 고대금화
                    htmlid = "jp6";
                    pc.sendPackets(new S_SystemMessage("사랑스러운 드레스를 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "jp5";
                }
            }
            if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(49026, 100000)) {    // 고대금화
                    pc.getInventory().storeItem(41096, 1);   // 화려한 반지
                    pc.getInventory().consumeItem(49026, 100000);   // 고대금화
                    htmlid = "jp6";
                    pc.sendPackets(new S_SystemMessage("화려한 반지를 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "jp5";
                }
            }
        }
        // 에마이
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71181) {
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(41093, 1)) {    // 꿈꾸는 곰인형
                    pc.getInventory().storeItem(41097, 1);   // 에마이의 마음
                    pc.getInventory().consumeItem(41093, 1);   // 꿈꾸는 곰인형
                    htmlid = "my5";
                    pc.sendPackets(new S_SystemMessage("에마이의 마음을 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "my4";
                }
            }
            if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(41094, 1)) {    // 유혹의 향수
                    pc.getInventory().storeItem(41097, 5);   // 에마이의 마음
                    pc.getInventory().consumeItem(41094, 1);   // 유혹의 향수
                    htmlid = "my6";
                    pc.sendPackets(new S_SystemMessage("에마이의 마음(5)을 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "my4";
                }
            }
            if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(41095, 1)) {    // 사랑스러운 드레스
                    pc.getInventory().storeItem(41097, 10);   // 에마이의 마음
                    pc.getInventory().consumeItem(41095, 1);   // 사랑스러운 드레스
                    htmlid = "my7";
                    pc.sendPackets(new S_SystemMessage("에마이의 마음(10)을 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "my4";
                }
            }
            if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(41096, 1)) {    // 화려한 반지
                    pc.getInventory().storeItem(41097, 100);   // 에마이의 마음
                    pc.getInventory().consumeItem(41096, 1);   // 화려한 반지
                    htmlid = "my8";
                    pc.sendPackets(new S_SystemMessage("에마이의 마음(100)을 얻었습니다."));
                } else { // 재료가 부족한 경우
                    htmlid = "my4";
                }
            }
        }
        // 깃털 수집가
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200039) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(41159, 100)) {    // 신비한 날개 깃털
                    pc.getInventory().consumeItem(41159, 100);  // 신비한 날개 깃털
                    pc.getInventory().storeItem(500041, 1);    // 천상의 물약
                    pc.sendPackets(new S_SystemMessage("천상의 물약을 얻었습니다."));
                    htmlid = "feathercol2";
                } else { // 재료가 부족한 경우
                    //pc.sendPackets(new S_SystemMessage("제작에 필요한 재료가 부족합니다."));
                    htmlid = "feathercol3";
                }
            }
        }
        // 7가지 서약 신녀 유리스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200054) {
            if (s.equalsIgnoreCase("part")) {
                pc.getInventory().storeItem(500041, 1);    // 천상의 물약
                pc.sendPackets(new S_SystemMessage("천상의 물약을 얻었습니다."));
                htmlid = "campaignY";
                pc.getQuest().set_step(L1Quest.QUEST_SEVEN, 1);
            } else {
            }
        }
        // 엘리프 - 원기회복의 귀걸이
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200055) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(40308, 33333) &&
                        pc.getInventory().checkItem(350006, 1)) {    // 아데나 , 원기의 결정체
                    pc.getInventory().consumeItem(40308, 33333);  // 아데나
                    pc.getInventory().consumeItem(350006, 1); // 원기의 결정체
                    pc.getInventory().storeItem(20465, 1);    // 원기 회복의 귀걸이
                    pc.sendPackets(new S_SystemMessage("원기 회복의 귀걸이를 얻었습니다."));
                    htmlid = "elriff4";
                } else { // 아데나가 부족한경우
                    pc.sendPackets(new S_SystemMessage("원기의 결정체가 부족합니다."));
                    htmlid = "elriff5";
                }
            }
        }
        // 보석 세공사 - 얼음여왕의 귀걸이
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200063) {
            if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(0단계), 얼음 결정체
                        pc.getInventory().checkItem(20466, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(0단계)
                    pc.getInventory().consumeItem(20466, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20467, 1);      // 얼음여왕의 귀걸이(1단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 1);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("c")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(1단계), 얼음 결정체
                        pc.getInventory().checkItem(20467, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(1단계)
                    pc.getInventory().consumeItem(20467, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20468, 1);      // 얼음여왕의 귀걸이(2단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 2);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("d")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(2단계), 얼음 결정체
                        pc.getInventory().checkItem(20468, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(2단계)
                    pc.getInventory().consumeItem(20468, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20469, 1);      // 얼음여왕의 귀걸이(3단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 3);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(3단계), 얼음 결정체
                        pc.getInventory().checkItem(20469, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(3단계)
                    pc.getInventory().consumeItem(20469, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20470, 1);      // 얼음여왕의 귀걸이(4단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 4);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("f")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(4단계), 얼음 결정체
                        pc.getInventory().checkItem(20470, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(4단계)
                    pc.getInventory().consumeItem(20470, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20471, 1);      // 얼음여왕의 귀걸이(5단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 5);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("g")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(5단계), 얼음 결정체
                        pc.getInventory().checkItem(20471, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(5단계)
                    pc.getInventory().consumeItem(20471, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20472, 1);      // 얼음여왕의 귀걸이(6단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 6);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("h")) {
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(6단계), 얼음 결정체
                        pc.getInventory().checkItem(20472, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(6단계)
                    pc.getInventory().consumeItem(20472, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20473, 1);      // 얼음여왕의 귀걸이(7단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 7);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("i")) {    // 힘 귀걸이
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(7단계), 얼음 결정체
                        pc.getInventory().checkItem(20473, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(7단계)
                    pc.getInventory().consumeItem(20473, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20474, 1);      // 얼음여왕의 귀걸이(8단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 8);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("j")) {   // 덱스 귀걸이
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(7단계), 얼음 결정체
                        pc.getInventory().checkItem(20473, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(7단계)
                    pc.getInventory().consumeItem(20473, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20475, 1);      // 얼음여왕의 귀걸이(8단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 8);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            } else if (s.equalsIgnoreCase("k")) {   // 위즈 귀걸이
                if (pc.getInventory().checkItem(350009, 1) &&       // 얼음여왕의 귀걸이(7단계), 얼음 결정체
                        pc.getInventory().checkItem(20473, 1)) {
                    pc.getInventory().consumeItem(350009, 1);   // 얼음여왕의 귀걸이(7단계)
                    pc.getInventory().consumeItem(20473, 1);    // 얼음 결정체
                    pc.getInventory().storeItem(20476, 1);      // 얼음여왕의 귀걸이(8단계)
                    pc.sendPackets(new S_SystemMessage("얼음여왕의 귀걸이를 얻었습니다."));
                    htmlid = "";
                    pc.getQuest().set_step(L1Quest.QUEST_ICE, 8);
                } else {
                    pc.sendPackets(new S_SystemMessage("귀걸이 세공의 필요한 재료가 부족합니다."));
                    htmlid = "";
                }
            }
        }
        // 샤르나 변신주문서
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200046) {
            L1NpcInstance npc = (L1NpcInstance) obj;
            if (pc.getLevel() < 30) {
                htmlid = "sharna4";
            } else if (pc.getInventory().checkItem(40308, 2500)) {
                int itemid = 0;
                if (pc.getLevel() >= 30 && pc.getLevel() < 40) {
                    itemid = 500044;
                } else if (pc.getLevel() >= 40 && pc.getLevel() < 52) {
                    itemid = 500045;
                } else if (pc.getLevel() >= 52 && pc.getLevel() < 55) {
                    itemid = 500047;
                } else if (pc.getLevel() >= 55 && pc.getLevel() < 60) {
                    itemid = 500048;
                } else if (pc.getLevel() >= 60 && pc.getLevel() < 65) {
                    itemid = 500049;
                } else if (pc.getLevel() >= 65 && pc.getLevel() < 70) {
                    itemid = 500050;
                } else if (pc.getLevel() >= 70) {
                    itemid = 500051;
                }
                pc.getInventory().consumeItem(40308, 2500);
                L1ItemInstance item = pc.getInventory().storeItem(itemid, 1);
                String npcName = npc.getNpcTemplate().get_name();
                String itemName = item.getItem().getName();
                pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                htmlid = "sharna3";
            } else {
                htmlid = "sharna5";
            }
        }
        // 이상한 오크 상인 파룸
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {
            // 「자원 리스트를 받는다」
            if (s.equalsIgnoreCase("q")) {
                if (pc.getInventory().checkItem(41356, 1)) {
                    htmlid = "rparum4";
                } else {
                    L1ItemInstance item = pc.getInventory().storeItem(41356, 1);
                    if (item != null) {
                        pc.sendPackets(new S_ServerMessage(143,
                                ((L1NpcInstance) obj).getNpcTemplate()
                                        .get_name(), item.getItem().getName())); // \f1%0이%1를 주었습니다.
                    }
                    htmlid = "rparum3";
                }
            }
        }

		/*  int castle_id = 0;
		   if (pc.getClanid() != 0) {   //이부분에서 else if가 아닌 if로 돌렸기에 뒤의 문지기와 길드원의 경우 마을 버프사가 버프를 안줌
		     L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		     if (clan != null) {
		      castle_id = clan.getCastleId();
		      }
		      }*/
        // 데포로쥬 4세 : 올버프사
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200049) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            int castle_id = clan.getCastleId();
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(41159, 5)) {
                    if (castle_id != 0) {
                        pc.getInventory().consumeItem(41159, 5);
                        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, IRON_SKIN, GLOWING_AURA, SHINING_AURA, HASTE, MIRRORIMG, CONSENTRATION, PAYTIONS, INSIGHT};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "ep6ev_p3";
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\fU성을 소유하고있는 혈원만 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("\\fU신비한날개깃털(5)이 부족합니다."));
                }
            }
        }

        // 데포로쥬 5세 : 올버프사
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 2000073) {
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(41159, 10)) {
                    if (pc.getLevel() >= 53) {
                        pc.getInventory().consumeItem(41159, 10);
                        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, HASTE, INSIGHT};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "ep6ev_p3";
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\fU53레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("\\fU신비한날개깃털(10)이 부족합니다."));
                }
            }
        }
        /// 코마관련 소스적용.
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72000) {
            if (pc.hasSkillEffect(L1SkillId.COMABUFF)) {
                pc.removeSkillEffect(L1SkillId.COMABUFF);
            }
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(51254, 1))
                    if (pc.getInventory().checkItem(51255, 1))
                        if (pc.getInventory().checkItem(51256, 1)) {
                            pc.getInventory().consumeItem(51254, 1);
                            pc.getInventory().consumeItem(51255, 1);
                            pc.getInventory().consumeItem(51256, 1);
                            int[] allBuffSkill = {COMA};
                            pc.setBuffnoch(1);
                            L1SkillUse l1skilluse = new L1SkillUse();
                            for (int i = 0; i < allBuffSkill.length; i++) {
                                l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                            }
                            htmlid = "";
                        } else {
                            htmlid = "coma3";
                        }
                    else {
                        htmlid = "coma3";
                    }
                else {
                    htmlid = "coma3";
                }
            } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72000) {
                if (pc.hasSkillEffect(L1SkillId.COMA)) {
                    pc.removeSkillEffect(L1SkillId.COMA);
                }
                if (s.equalsIgnoreCase("B")) {
                    if (pc.getInventory().checkItem(51254, 1))
                        if (pc.getInventory().checkItem(51255, 1))
                            if (pc.getInventory().checkItem(51256, 1))
                                if (pc.getInventory().checkItem(51257, 1))
                                    if (pc.getInventory().checkItem(51258, 1)) {
                                        pc.getInventory().consumeItem(51254, 1);
                                        pc.getInventory().consumeItem(51255, 1);
                                        pc.getInventory().consumeItem(51256, 1);
                                        pc.getInventory().consumeItem(51257, 1);
                                        pc.getInventory().consumeItem(51258, 1);
                                        int[] allBuffSkill = {COMABUFF};
                                        pc.setBuffnoch(1);
                                        L1SkillUse l1skilluse = new L1SkillUse();
                                        for (int i = 0; i < allBuffSkill.length; i++) {
                                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                                        }
                                        htmlid = "";
                                    } else {
                                        htmlid = "coma3";
                                    }
                                else {
                                    htmlid = "coma3";
                                }
                            else {
                                htmlid = "coma3";
                            }
                        else {
                            htmlid = "coma3";
                        }
                    else {
                        htmlid = "coma3";
                    }
                }
            }
        }
        //도망친 맘보토끼
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 778812) { //npc 번호
            if (s.equalsIgnoreCase("0")) {
                if (!pc.getInventory().checkItem(20758, 1)) {      // 신비한 날개 깃털
                    pc.getInventory().storeItem(20758, 1);
                    pc.sendPackets(new S_SystemMessage("맘보 토끼 모자를 얻었습니다."));
                    htmlid = "friendmambo2";
                } else { // 재료가 부족한 경우
                    htmlid = "friendmambo3";
                }
            }
            if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(41159, 12)) {      // 신비한 날개 깃털
                    if (pc.getLevel() >= 53) {
                        pc.getInventory().consumeItem(41159, 12);
                        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, GREATER_HASTE, BRAVE_AURA, NATURES_TOUCH, IRON_SKIN};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("\\fU53레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_SystemMessage("\\fU신비한날개깃털[12]이 부족합니다."));
                }
            }
            // 본섭 버프 NPC
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 778813) { //npc 번호
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(40308, 10000)) {      // 아데나
                    if (pc.getLevel() >= 5) {
                        pc.getInventory().consumeItem(40308, 10000);
                        int[] allBuffSkill = {HASTE, ADVANCE_SPIRIT, EARTH_SKIN, AQUA_PROTECTER, CONSENTRATION, PAYTIONS, INSIGHT, SHINING_AURA, FIRE_WEAPON};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
                }
            }
            if (s.equalsIgnoreCase("b")) {
                if (pc.getInventory().checkItem(40308, 10000)) {      // 아데나
                    if (pc.getLevel() >= 5) {
                        pc.getInventory().consumeItem(40308, 10000);
                        int[] allBuffSkill = {HASTE, ADVANCE_SPIRIT, EARTH_SKIN, AQUA_PROTECTER, CONSENTRATION, PAYTIONS, INSIGHT, SHINING_AURA, WIND_SHOT};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
                }
            }

            //수상한 요리사
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 199998) { //npc 번호
            if (s.equalsIgnoreCase("0")) {
                if (pc.getInventory().checkItem(41159, 45)) {      // 신비한 날개 깃털
                    pc.getInventory().consumeItem(41159, 45);    // 신비한 날개 깃털
                    pc.getInventory().storeItem(555584, 1);           // 포춘쿠키
                    pc.sendPackets(new S_SystemMessage("포춘쿠키를 얻었습니다."));
                    htmlid = "suschef1";
                } else { // 재료가 부족한 경우
                    pc.sendPackets(new S_SystemMessage("신비한 날개 깃털이 부족합니다."));
                    htmlid = "suschef5";
                }
            }
            if (s.equalsIgnoreCase("1")) {
                if (pc.hasSkillEffect(L1SkillId.LUCK_B)) {
                    pc.removeSkillEffect(L1SkillId.LUCK_B);
                }
                if (pc.hasSkillEffect(L1SkillId.LUCK_C)) {
                    pc.removeSkillEffect(L1SkillId.LUCK_C);
                }
                if (pc.hasSkillEffect(L1SkillId.LUCK_D)) {
                    pc.removeSkillEffect(L1SkillId.LUCK_D);
                }
                if (pc.getInventory().checkItem(555585, 1)) {      // 신비한 날개 깃털
                    pc.getInventory().consumeItem(555585, 1);
                    Random random = new Random();
                    int gn4 = random.nextInt(4) + 1;
                    if (gn4 == 1) {
                        int[] allBuffSkill = {LUCK_A};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else if (gn4 == 2) {
                        if (pc.hasSkillEffect(L1SkillId.LUCK_A)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_A);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_C)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_C);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_D)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_D);
                        }
                        int[] allBuffSkill = {LUCK_B};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else if (gn4 == 3) {
                        if (pc.hasSkillEffect(L1SkillId.LUCK_A)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_A);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_B)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_B);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_D)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_D);
                        }
                        int[] allBuffSkill = {LUCK_C};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else if (gn4 == 4) {
                        if (pc.hasSkillEffect(L1SkillId.LUCK_A)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_A);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_B)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_B);
                        }
                        if (pc.hasSkillEffect(L1SkillId.LUCK_C)) {
                            pc.removeSkillEffect(L1SkillId.LUCK_C);
                        }
                        int[] allBuffSkill = {LUCK_D};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0,
                                    L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    }

                    //  htmlid="suschef2"; //버프받는다 창 삭제
                } else { // 재료가 부족한 경우
                    pc.sendPackets(new S_SystemMessage("운세 쪽지가 없습니다.."));
                    htmlid = "suschef4";
                }
            }
        } // 풀버프 상인
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 100016) { // npcid
            if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(40308, 10)) {      // 신비한 날개 깃털
                    if (pc.getLevel() >= 5) {
                        pc.getInventory().consumeItem(40308, Config.BUFF_PRICE);
                        int[] allBuffSkill = {PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON, ADVANCE_SPIRIT, GREATER_HASTE, BRAVE_AURA, NATURES_TOUCH, IRON_SKIN};
                        pc.setBuffnoch(1);
                        L1SkillUse l1skilluse = new L1SkillUse();
                        for (int i = 0; i < allBuffSkill.length; i++) {
                            l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
                        }
                        htmlid = "";
                    } else {
                        pc.sendPackets(new S_SystemMessage("5레벨 이상부터 사용 가능합니다."));
                    }
                } else {
                    pc.sendPackets(new S_ServerMessage(337, "$4")); // \f1%0이 부족합니다.
                }
            }
        }
        // 변신 이벤트 대법관 소스적용.
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72101) { // 코마 밑에 넣어주심 되요^^
            if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(40308, 1000)) {
                    pc.getInventory().consumeItem(40308, 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 4899));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 4899));
                    L1PolyMorph.doPoly(pc, 7038, 1800, L1PolyMorph.MORPH_BY_NPC);
                    htmlid = "event_boss9";
                } else {
                    htmlid = "event_boss8";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72102) { // 본인 나비켓 엔피시 ID 넣어주심되죠
            if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(40308, 1000)) {
                    pc.getInventory().consumeItem(40308, 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 4899));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 4899));
                    L1PolyMorph.doPoly(pc, 7040, 1800, L1PolyMorph.MORPH_BY_NPC);
                    htmlid = "event_boss10";
                } else {
                    htmlid = "event_boss8";
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 72103) {
            if (s.equalsIgnoreCase("1")) {
                if (pc.getInventory().checkItem(40308, 1000)) {
                    pc.getInventory().consumeItem(40308, 1000);
                    pc.sendPackets(new S_SkillSound(pc.getId(), 4899));
                    pc.broadcastPacket(new S_SkillSound(pc.getId(), 4899));
                    L1PolyMorph.doPoly(pc, 7042, 1800, L1PolyMorph.MORPH_BY_NPC);
                    htmlid = "event_boss11";
                } else {
                    htmlid = "event_boss8";
                }
            }
        }
        // 변신 이벤트 대법관 소스적용.
        // 대장장이 바트르 (실베리아)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 200035) {
            if (s.equalsIgnoreCase("4")) {
                if (pc.getInventory().checkItem(40052, 10) && pc.getInventory().checkItem(40053, 10)
                        && pc.getInventory().checkItem(40054, 10) && pc.getInventory().checkItem(40055, 10)
                        && pc.getInventory().checkItem(40520, 30) && pc.getInventory().checkItem(40308, 1000000)
                        && pc.getInventory().checkItem(40470, 30)) {
                    pc.getInventory().storeItem(504, 1);
                    pc.getInventory().consumeItem(40052, 10);
                    pc.getInventory().consumeItem(40053, 10);
                    pc.getInventory().consumeItem(40054, 10);
                    pc.getInventory().consumeItem(40055, 10);
                    pc.getInventory().consumeItem(40520, 30);
                    pc.getInventory().consumeItem(40470, 30);
                    pc.getInventory().consumeItem(40308, 1000000);
                }
            }
        }
        // 에덴 기마 단원
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
            // 「새로운 힘을 관있다」
            if (s.equalsIgnoreCase("c")) {
                if (pc.isCrown()) {
                    if (pc.getInventory().checkItem(20383, 1)) {
                        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
                            L1ItemInstance item = pc.getInventory().findItemId(
                                    20383);
                            if (item != null && item.getChargeCount() != 50) {
                                item.setChargeCount(50);
                                pc.getInventory().updateItem(item,
                                        L1PcInventory.COL_CHARGE_COUNT);
                                pc.getInventory().consumeItem(L1ItemId.ADENA,
                                        100000);
                                htmlid = "";
                            }
                        } else {
                            pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
                        }
                    }
                }
            }
        }
        // 보좌관 이리스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71126) {
            // 「네. 내가 협력합시다」
            if (s.equalsIgnoreCase("B")) {
                if (pc.getInventory().checkItem(41007, 1)) { // 이리스의 명령서：영혼의 안식
                    htmlid = "eris10";
                } else {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41007, 1);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "eris6";
                }
            } else if (s.equalsIgnoreCase("C")) {
                if (pc.getInventory().checkItem(41009, 1)) { // 이리스의 명령서：동맹의 의사
                    htmlid = "eris10";
                } else {
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(41009, 1);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                    htmlid = "eris8";
                }
            } else if (s.equalsIgnoreCase("A")) {
                if (pc.getInventory().checkItem(41007, 1)) { // 이리스의 명령서：영혼의 안식
                    if (pc.getInventory().checkItem(40969, 20)) { // 다크 에르프영혼의 결정체
                        htmlid = "eris18";
                        materials = new int[]{40969, 41007};
                        counts = new int[]{20, 1};
                        createitem = new int[]{41008}; // 이리스의 가방
                        createcount = new int[]{1};
                    } else {
                        htmlid = "eris5";
                    }
                } else {
                    htmlid = "eris2";
                }
            } else if (s.equalsIgnoreCase("E")) {
                if (pc.getInventory().checkItem(41010, 1)) { // 이리스의 추천서
                    htmlid = "eris19";
                } else {
                    htmlid = "eris7";
                }
            } else if (s.equalsIgnoreCase("D")) {
                if (pc.getInventory().checkItem(41010, 1)) { // 이리스의 추천서
                    htmlid = "eris19";
                } else {
                    if (pc.getInventory().checkItem(41009, 1)) { // 이리스의 명령서：동맹의 의사
                        if (pc.getInventory().checkItem(40959, 1)) { // 명법군왕의 인장
                            htmlid = "eris17";
                            materials = new int[]{40959, 41009}; // 명법군왕의 인장
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40960, 1)) { // 마령군왕의 인장
                            htmlid = "eris16";
                            materials = new int[]{40960, 41009}; // 마령군왕의 인장
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40961, 1)) { // 마수령군왕의 인장
                            htmlid = "eris15";
                            materials = new int[]{40961, 41009}; // 마수군왕의 인장
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40962, 1)) { // 암살군왕의 인장
                            htmlid = "eris14";
                            materials = new int[]{40962, 41009}; // 암살군왕의 인장
                            counts = new int[]{1, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40635, 10)) { // 마령군의 배지
                            htmlid = "eris12";
                            materials = new int[]{40635, 41009}; // 마령군의 배지
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40638, 10)) { // 마수군의 배지
                            htmlid = "eris11";
                            materials = new int[]{40638, 41009}; // 마령군의 배지
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40642, 10)) { // 명법군의 배지
                            htmlid = "eris13";
                            materials = new int[]{40642, 41009}; // 명법군의 배지
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else if (pc.getInventory().checkItem(40667, 10)) { // 암살군의 배지
                            htmlid = "eris13";
                            materials = new int[]{40667, 41009}; // 암살군의 배지
                            counts = new int[]{10, 1};
                            createitem = new int[]{41010}; // 이리스의 추천서
                            createcount = new int[]{1};
                        } else {
                            htmlid = "eris8";
                        }
                    } else {
                        htmlid = "eris7";
                    }
                }
            }
        }
        // 넘어진 항해사
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076) {
            if (s.equalsIgnoreCase("A")) {
                int[] diaryno = {49082, 49083};
                int pid = _random.nextInt(diaryno.length);
                int di = diaryno[pid];
                if (di == 49082) { // 홀수 페이지 뽑아라
                    htmlid = "voyager6a";
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                } else if (di == 49083) { // 짝수 페이지 뽑아라
                    htmlid = "voyager6b";
                    L1NpcInstance npc = (L1NpcInstance) obj;
                    L1ItemInstance item = pc.getInventory().storeItem(di, 1);
                    String npcName = npc.getNpcTemplate().get_name();
                    String itemName = item.getItem().getName();
                    pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
                }
            }
        }
        // 연금 술사 페리타
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71128) {
            if (s.equals("A")) {
                if (pc.getInventory().checkItem(41010, 1)) { // 이리스의 추천서
                    htmlid = "perita2";
                } else {
                    htmlid = "perita3";
                }
            } else if (s.equals("p")) {
                // 저주해진 블랙 귀 링 판별
                if (pc.getInventory().checkItem(40987, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(40988, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(40989, 1)) { // 워리아크라스
                    htmlid = "perita43";
                } else if (pc.getInventory().checkItem(40987, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(40989, 1)) { // 워리아크라스
                    htmlid = "perita44";
                } else if (pc.getInventory().checkItem(40987, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(40988, 1)) { // 나이트 클래스
                    htmlid = "perita45";
                } else if (pc.getInventory().checkItem(40988, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(40989, 1)) { // 워리아크라스
                    htmlid = "perita47";
                } else if (pc.getInventory().checkItem(40987, 1)) { // 위저드 클래스
                    htmlid = "perita46";
                } else if (pc.getInventory().checkItem(40988, 1)) { // 나이트 클래스
                    htmlid = "perita49";
                } else if (pc.getInventory().checkItem(40987, 1)) { // 워리아크라스
                    htmlid = "perita48";
                } else {
                    htmlid = "perita50";
                }
            } else if (s.equals("q")) {
                // 블랙 귀 링 판별
                if (pc.getInventory().checkItem(41173, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41174, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(41175, 1)) { // 워리아크라스
                    htmlid = "perita54";
                } else if (pc.getInventory().checkItem(41173, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41175, 1)) { // 워리아크라스
                    htmlid = "perita55";
                } else if (pc.getInventory().checkItem(41173, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41174, 1)) { // 나이트 클래스
                    htmlid = "perita56";
                } else if (pc.getInventory().checkItem(41174, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(41175, 1)) { // 워리아크라스
                    htmlid = "perita58";
                } else if (pc.getInventory().checkItem(41174, 1)) { // 위저드 클래스
                    htmlid = "perita57";
                } else if (pc.getInventory().checkItem(41175, 1)) { // 나이트 클래스
                    htmlid = "perita60";
                } else if (pc.getInventory().checkItem(41176, 1)) { // 워리아크라스
                    htmlid = "perita59";
                } else {
                    htmlid = "perita61";
                }
            } else if (s.equals("s")) {
                // 신비적인 블랙 귀 링 판별
                if (pc.getInventory().checkItem(41161, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41162, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(41163, 1)) { // 워리아크라스
                    htmlid = "perita62";
                } else if (pc.getInventory().checkItem(41161, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41163, 1)) { // 워리아크라스
                    htmlid = "perita63";
                } else if (pc.getInventory().checkItem(41161, 1) // 위저드 클래스
                        && pc.getInventory().checkItem(41162, 1)) { // 나이트 클래스
                    htmlid = "perita64";
                } else if (pc.getInventory().checkItem(41162, 1) // 나이트 클래스
                        && pc.getInventory().checkItem(41163, 1)) { // 워리아크라스
                    htmlid = "perita66";
                } else if (pc.getInventory().checkItem(41161, 1)) { // 위저드 클래스
                    htmlid = "perita65";
                } else if (pc.getInventory().checkItem(41162, 1)) { // 나이트 클래스
                    htmlid = "perita68";
                } else if (pc.getInventory().checkItem(41163, 1)) { // 워리아크라스
                    htmlid = "perita67";
                } else {
                    htmlid = "perita69";
                }
            } else if (s.equals("B")) {
                // 정화의 일부
                if (pc.getInventory().checkItem(40651, 10) // 불의 숨결
                        && pc.getInventory().checkItem(40643, 10) // 수의 숨결
                        && pc.getInventory().checkItem(40618, 10) // 대지의 숨결
                        && pc.getInventory().checkItem(40645, 10) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40676, 10) // 어둠의 숨결
                        && pc.getInventory().checkItem(40442, 5) // 프롭브의 위액
                        && pc.getInventory().checkItem(40051, 1)) { // 고급 에메랄드
                    htmlid = "perita7";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676,
                            40442, 40051};
                    counts = new int[]{10, 10, 10, 10, 20, 5, 1};
                    createitem = new int[]{40925}; // 정화의 일부
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita8";
                }
            } else if (s.equals("G") || s.equals("h") || s.equals("i")) {
                // 신비적인 일부：1 단계
                if (pc.getInventory().checkItem(40651, 5) // 불의 숨결
                        && pc.getInventory().checkItem(40643, 5) // 수의 숨결
                        && pc.getInventory().checkItem(40618, 5) // 대지의 숨결
                        && pc.getInventory().checkItem(40645, 5) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40676, 5) // 어둠의 숨결
                        && pc.getInventory().checkItem(40675, 5) // 어둠의 광석
                        && pc.getInventory().checkItem(40049, 3) // 고급 루비
                        && pc.getInventory().checkItem(40051, 1)) { // 고급 에메랄드
                    htmlid = "perita27";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676,
                            40675, 40049, 40051};
                    counts = new int[]{5, 5, 5, 5, 10, 10, 3, 1};
                    createitem = new int[]{40926}; // 신비적인 일부：1 단계
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita28";
                }
            } else if (s.equals("H") || s.equals("j") || s.equals("k")) {
                // 신비적인 일부：2 단계
                if (pc.getInventory().checkItem(40651, 10) // 불의 숨결
                        && pc.getInventory().checkItem(40643, 10) // 수의 숨결
                        && pc.getInventory().checkItem(40618, 10) // 대지의 숨결
                        && pc.getInventory().checkItem(40645, 10) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40676, 20) // 어둠의 숨결
                        && pc.getInventory().checkItem(40675, 10) // 어둠의 광석
                        && pc.getInventory().checkItem(40048, 3) // 고급 다이아몬드
                        && pc.getInventory().checkItem(40051, 1)) { // 고급 에메랄드
                    htmlid = "perita29";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676,
                            40675, 40048, 40051};
                    counts = new int[]{10, 10, 10, 10, 20, 10, 3, 1};
                    createitem = new int[]{40927}; // 신비적인 일부：2 단계
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita30";
                }
            } else if (s.equals("I") || s.equals("l") || s.equals("m")) {
                // 신비적인 일부：3 단계
                if (pc.getInventory().checkItem(40651, 20) // 불의 숨결
                        && pc.getInventory().checkItem(40643, 20) // 수의 숨결
                        && pc.getInventory().checkItem(40618, 20) // 대지의 숨결
                        && pc.getInventory().checkItem(40645, 20) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40676, 30) // 어둠의 숨결
                        && pc.getInventory().checkItem(40675, 10) // 어둠의 광석
                        && pc.getInventory().checkItem(40050, 3) // 고급 사파이어
                        && pc.getInventory().checkItem(40051, 1)) { // 고급 에메랄드
                    htmlid = "perita31";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676,
                            40675, 40050, 40051};
                    counts = new int[]{20, 20, 20, 20, 30, 10, 3, 1};
                    createitem = new int[]{40928}; // 신비적인 일부：3 단계
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita32";
                }
            } else if (s.equals("J") || s.equals("n") || s.equals("o")) {
                // 신비적인 일부：4 단계
                if (pc.getInventory().checkItem(40651, 30) // 불의 숨결
                        && pc.getInventory().checkItem(40643, 30) // 수의 숨결
                        && pc.getInventory().checkItem(40618, 30) // 대지의 숨결
                        && pc.getInventory().checkItem(40645, 30) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40676, 30) // 어둠의 숨결
                        && pc.getInventory().checkItem(40675, 20) // 어둠의 광석
                        && pc.getInventory().checkItem(40052, 1) // 최고급 다이아몬드
                        && pc.getInventory().checkItem(40051, 1)) { // 고급 에메랄드
                    htmlid = "perita33";
                    materials = new int[]{40651, 40643, 40618, 40645, 40676,
                            40675, 40052, 40051};
                    counts = new int[]{30, 30, 30, 30, 30, 20, 1, 1};
                    createitem = new int[]{40928}; // 신비적인 일부：4 단계
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita34";
                }
            } else if (s.equals("K")) { // 1 단계 귀 링(영혼의 귀 링)
                int earinga = 0;
                int earingb = 0;
                if (pc.getInventory().checkEquipped(21014)
                        || pc.getInventory().checkEquipped(21006)
                        || pc.getInventory().checkEquipped(21007)) {
                    htmlid = "perita36";
                } else if (pc.getInventory().checkItem(21014, 1)) { // 위저드 클래스
                    earinga = 21014;
                    earingb = 41176;
                } else if (pc.getInventory().checkItem(21006, 1)) { // 나이트 클래스
                    earinga = 21006;
                    earingb = 41177;
                } else if (pc.getInventory().checkItem(21007, 1)) { // 워리아크라스
                    earinga = 21007;
                    earingb = 41178;
                } else {
                    htmlid = "perita36";
                }
                if (earinga > 0) {
                    materials = new int[]{earinga};
                    counts = new int[]{1};
                    createitem = new int[]{earingb};
                    createcount = new int[]{1};
                }
            } else if (s.equals("L")) { // 2 단계 귀 링(지혜의 귀 링)
                if (pc.getInventory().checkEquipped(21015)) {
                    htmlid = "perita22";
                } else if (pc.getInventory().checkItem(21015, 1)) {
                    materials = new int[]{21015};
                    counts = new int[]{1};
                    createitem = new int[]{41179};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita22";
                }
            } else if (s.equals("M")) { // 3 단계 귀 링(진실의 귀 링)
                if (pc.getInventory().checkEquipped(21016)) {
                    htmlid = "perita26";
                } else if (pc.getInventory().checkItem(21016, 1)) {
                    materials = new int[]{21016};
                    counts = new int[]{1};
                    createitem = new int[]{41182};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita26";
                }
            } else if (s.equals("b")) { // 2 단계 귀 링(정열의 귀 링)
                if (pc.getInventory().checkEquipped(21009)) {
                    htmlid = "perita39";
                } else if (pc.getInventory().checkItem(21009, 1)) {
                    materials = new int[]{21009};
                    counts = new int[]{1};
                    createitem = new int[]{41180};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita39";
                }
            } else if (s.equals("d")) { // 3 단계 귀 링(명예의 귀 링)
                if (pc.getInventory().checkEquipped(21012)) {
                    htmlid = "perita41";
                } else if (pc.getInventory().checkItem(21012, 1)) {
                    materials = new int[]{21012};
                    counts = new int[]{1};
                    createitem = new int[]{41183};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita41";
                }
            } else if (s.equals("a")) { // 2 단계 귀 링(분노의 귀 링)
                if (pc.getInventory().checkEquipped(21008)) {
                    htmlid = "perita38";
                } else if (pc.getInventory().checkItem(21008, 1)) {
                    materials = new int[]{21008};
                    counts = new int[]{1};
                    createitem = new int[]{41181};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita38";
                }
            } else if (s.equals("c")) { // 3 단계 귀 링(용맹의 귀 링)
                if (pc.getInventory().checkEquipped(21010)) {
                    htmlid = "perita40";
                } else if (pc.getInventory().checkItem(21010, 1)) {
                    materials = new int[]{21010};
                    counts = new int[]{1};
                    createitem = new int[]{41184};
                    createcount = new int[]{1};
                } else {
                    htmlid = "perita40";
                }
            }
        }
        // 보석 세공인 룸스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71129) {
            if (s.equals("Z")) {
                htmlid = "rumtis2";
            } else if (s.equals("Y")) {
                if (pc.getInventory().checkItem(41010, 1)) { // 이리스의 추천서
                    htmlid = "rumtis3";
                } else {
                    htmlid = "rumtis4";
                }
            } else if (s.equals("q")) {
                htmlid = "rumtis92";
            } else if (s.equals("A")) {
                if (pc.getInventory().checkItem(41161, 1)) {
                    // 신비적인 블랙 귀 링
                    htmlid = "rumtis6";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("B")) {
                if (pc.getInventory().checkItem(41164, 1)) {
                    // 신비적인 위저드 귀 링
                    htmlid = "rumtis7";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("C")) {
                if (pc.getInventory().checkItem(41167, 1)) {
                    // 신비적인 회색 위저드 귀 링
                    htmlid = "rumtis8";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("T")) {
                if (pc.getInventory().checkItem(41167, 1)) {
                    // 신비적인 화이트 위저드 귀 링
                    htmlid = "rumtis9";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("w")) {
                if (pc.getInventory().checkItem(41162, 1)) {
                    // 신비적인 블랙 귀 링
                    htmlid = "rumtis14";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("x")) {
                if (pc.getInventory().checkItem(41165, 1)) {
                    // 신비적인 나이트 귀 링
                    htmlid = "rumtis15";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("y")) {
                if (pc.getInventory().checkItem(41168, 1)) {
                    // 신비적인 회색 나이트 귀 링
                    htmlid = "rumtis16";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("z")) {
                if (pc.getInventory().checkItem(41171, 1)) {
                    // 신비적인 화이트 나이트 귀 링
                    htmlid = "rumtis17";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("U")) {
                if (pc.getInventory().checkItem(41163, 1)) {
                    // 신비적인 블랙 귀 링
                    htmlid = "rumtis10";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("V")) {
                if (pc.getInventory().checkItem(41166, 1)) {
                    // 미스테리아스워리아이아링
                    htmlid = "rumtis11";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("W")) {
                if (pc.getInventory().checkItem(41169, 1)) {
                    // 미스테리아스그레이워리아이아링
                    htmlid = "rumtis12";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("X")) {
                if (pc.getInventory().checkItem(41172, 1)) {
                    // 미스테리아스화이워리아이아링
                    htmlid = "rumtis13";
                } else {
                    htmlid = "rumtis101";
                }
            } else if (s.equals("D") || s.equals("E") || s.equals("F")
                    || s.equals("G")) {
                int insn = 0;
                int bacn = 0;
                int me = 0;
                int mr = 0;
                int mj = 0;
                int an = 0;
                int men = 0;
                int mrn = 0;
                int mjn = 0;
                int ann = 0;
                if (pc.getInventory().checkItem(40959, 1) // 명법군왕의 인장
                        && pc.getInventory().checkItem(40960, 1) // 마령군왕의 인장
                        && pc.getInventory().checkItem(40961, 1) // 마수군왕의 인장
                        && pc.getInventory().checkItem(40962, 1)) { // 암살군왕의 인장
                    insn = 1;
                    me = 40959;
                    mr = 40960;
                    mj = 40961;
                    an = 40962;
                    men = 1;
                    mrn = 1;
                    mjn = 1;
                    ann = 1;
                } else if (pc.getInventory().checkItem(40642, 10) // 명법군의 배지
                        && pc.getInventory().checkItem(40635, 10) // 마령군의 배지
                        && pc.getInventory().checkItem(40638, 10) // 마수군의 배지
                        && pc.getInventory().checkItem(40667, 10)) { // 암살군의 배지
                    bacn = 1;
                    me = 40642;
                    mr = 40635;
                    mj = 40638;
                    an = 40667;
                    men = 10;
                    mrn = 10;
                    mjn = 10;
                    ann = 10;
                }
                if (pc.getInventory().checkItem(40046, 1) // 사파이어
                        && pc.getInventory().checkItem(40618, 5) // 대지의 숨결
                        && pc.getInventory().checkItem(40643, 5) // 수의 숨결
                        && pc.getInventory().checkItem(40645, 5) // 돌풍이 심함 취
                        && pc.getInventory().checkItem(40651, 5) // 불의 숨결
                        && pc.getInventory().checkItem(40676, 5)) { // 어둠의 숨결
                    if (insn == 1 || bacn == 1) {
                        htmlid = "rumtis60";
                        materials = new int[]{me, mr, mj, an, 40046, 40618,
                                40643, 40651, 40676};
                        counts = new int[]{men, mrn, mjn, ann, 1, 5, 5, 5, 5,
                                5};
                        createitem = new int[]{40926}; // 가공된 사파이어：1 단계
                        createcount = new int[]{1};
                    } else {
                        htmlid = "rumtis18";
                    }
                }
            }
        }
        // 아타로제
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71119) {
            // 「라스타바드의 역사서 1장에서 8장까지 전부 건네준다」
            if (s.equalsIgnoreCase("request las history book")) {
                materials = new int[]{41019, 41020, 41021, 41022, 41023,
                        41024, 41025, 41026};
                counts = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
                createitem = new int[]{41027};
                createcount = new int[]{1};
                htmlid = "";
            }
        }
        // 장로 수행원 크로렌스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71170) {
            // 「라스타바드의 역사서를 건네준다」
            if (s.equalsIgnoreCase("request las weapon manual")) {
                materials = new int[]{41027};
                counts = new int[]{1};
                createitem = new int[]{40965};
                createcount = new int[]{1};
                htmlid = "";
            }
        }
        // 진명왕 단테스
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71168) {
            // 「이계의 마귀가 있는 장소에 보내 주세요」
            if (s.equalsIgnoreCase("a")) {
                if (pc.getInventory().checkItem(41028, 1)) {
                    L1Teleport.teleport(pc, 32648, 32921, (short) 535, 6, true);
                    pc.getInventory().consumeItem(41028, 1);
                }
            }
        }
        //문지기 보스 공략 제한(2시간 30분)
        //오시리스 제단 문지기
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 208) {
            if (!CrockController.getInstance().isBoss()) {
                htmlid = "tebegate2";
            } else if (s.equalsIgnoreCase("e")) {
                CrockController.getInstance().add(pc);
                if (CrockController.getInstance().size() >= 20) {
                    htmlid = "tebegate4";
                } else if (pc.getInventory().checkItem(500040, 1)) {
                    L1Teleport.teleport(pc, 32735, 32831, (short) 782, 5, true);
                    pc.getInventory().consumeItem(500040, 1);
                } else {
                    htmlid = "tebegate3";
                }
            }
        }
        //쿠쿨칸 문지기

        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 210) {
            if (!CrockController.getInstance().isBoss()) {
                htmlid = "tikalgate2";
            } else if (s.equalsIgnoreCase("e")) {
                CrockController.getInstance().add(pc);
                if (CrockController.getInstance().size() >= 20) {
                    htmlid = "tikalgate4";
                } else if (pc.getInventory().checkItem(500060, 1)) {
                    L1Teleport.teleport(pc, 32731, 32863, (short) 784, 5, true);
                    pc.getInventory().consumeItem(500060, 1);
                } else {
                    htmlid = "tikalgate3";
                }
            }
        }
        //메티스의 조수

		/*  else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777922){
		     if(!CrockController.getInstance().isBoss()){
		         htmlid = "kingdevsum5";
		     }
		     else if (s.equalsIgnoreCase("b")){
		      CrockController.getInstance().add(pc);
		      if(CrockController.getInstance().size() >= 20){
		       htmlid = "kingdevsum3";
		      }
		      else if (pc.getInventory().checkItem(555600, 1)) {
		       L1Teleport.teleport(pc, 32723, 32800, (short) 5167, 5, true);
		       pc.getInventory().consumeItem(555600, 1);
		      } else {
		       htmlid = "kingdevsum15";
		      }
		     }
		  } */

        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777922) {
            if (s.equalsIgnoreCase("a")) {
                htmlid = "kingdevsum13";
            } else if (s.equalsIgnoreCase("b")) {
                htmlid = "";
                if (!GiranController.getInstance().getGiranTime()) {
                    pc.sendPackets(new S_SystemMessage("지금은 입장시간이 아닙니다."));
                    return;
                }
                if (GiranController.getInstance().getGiranTime()) {
                    Random random = new Random();
                    int i13 = 32560 + random.nextInt(2);
                    int k19 = 32868 + random.nextInt(2);
                    L1Teleport.teleport(pc, i13, k19, (short) 5167, 5, true);
                    pc.sendPackets(new S_SystemMessage("당신은 앞으로 3시간동안 악마왕 영토 체류가능합니다."));
                    return;
                }
            }
        } else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 777901) {
            if (s.equalsIgnoreCase("0")) {
                htmlid = "mtsmemory4";
            } else if (s.equalsIgnoreCase("a")) {
                htmlid = "";
                if (!GiranController.getInstance().getGiranTime()) {
                    pc.sendPackets(new S_SystemMessage("지금은 입장시간이 아닙니다."));
                    return;
                }
                if (GiranController.getInstance().getGiranTime()) {
                    Random random = new Random();
                    int i13 = 32560 + random.nextInt(2);
                    int k19 = 32868 + random.nextInt(2);
                    L1Teleport.teleport(pc, i13, k19, (short) 5167, 5, true);
                    pc.sendPackets(new S_SystemMessage("당신은 앞으로 3시간동안 아덴 사냥터 체류가능합니다."));
                    return;
                }
            }
        }
        // 첩보원(욕망의 동굴측)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80067) {
            // 「동요하면서도 승낙한다」
            if (s.equalsIgnoreCase("n")) {
                htmlid = "";
                poly(client, 6034);
                final int[] item_ids = {41132, 41133, 41134};
                final int[] item_amounts = {1, 1, 1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 1);
                }
                // 「그런 임무는 그만둔다」
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "minicod09";
                pc.getInventory().consumeItem(41130, 1);
                pc.getInventory().consumeItem(41131, 1);
                // 「초기화한다」
            } else if (s.equalsIgnoreCase("k")) {
                htmlid = "";
                pc.getInventory().consumeItem(41132, 1); // 핏자국의 타락 한 가루
                pc.getInventory().consumeItem(41133, 1); // 핏자국의 무력 한 가루
                pc.getInventory().consumeItem(41134, 1); // 핏자국의 아집 한 가루
                pc.getInventory().consumeItem(41135, 1); // 카헬의 타락 한 정수
                pc.getInventory().consumeItem(41136, 1); // 카헬의 무력 한 정수
                pc.getInventory().consumeItem(41137, 1); // 카헬의 아집 한 정수
                pc.getInventory().consumeItem(41138, 1); // 카헬의 정수
                pc.getQuest().set_step(L1Quest.QUEST_DESIRE, 0);
                // 정수를 건네준다
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_DESIRE) == L1Quest.QUEST_END
                        || pc.getKarmaLevel() >= 1) {
                    htmlid = "";
                } else {
                    if (pc.getInventory().checkItem(41138)) {
                        htmlid = "";
                        pc.addKarma((int) (1600 * Config.RATE_KARMA));
                        pc.getInventory().consumeItem(41130, 1); // 핏자국의 계약서
                        pc.getInventory().consumeItem(41131, 1); // 핏자국의 지령서
                        pc.getInventory().consumeItem(41138, 1); // 카헬의 정수
                        pc.getQuest().set_step(L1Quest.QUEST_DESIRE,
                                L1Quest.QUEST_END);
                    } else {
                        htmlid = "minicod04";
                    }
                }


                // 선물을 받는다
            } else if (s.equalsIgnoreCase("g")) {
                htmlid = "";
                final int[] item_ids = {41130}; // 핏자국의 계약서
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                }
            }
        }
        // 첩보원(그림자의 신전측)
        else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81202) {
            // 「화가 나지만 승낙한다」
            if (s.equalsIgnoreCase("n")) {
                htmlid = "";
                poly(client, 6035);
                final int[] item_ids = {41123, 41124, 41125};
                final int[] item_amounts = {1, 1, 1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                    pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 1);
                }
                // 「그런 임무는 그만둔다」
            } else if (s.equalsIgnoreCase("d")) {
                htmlid = "minitos09";
                pc.getInventory().consumeItem(41121, 1);
                pc.getInventory().consumeItem(41122, 1);
                // 「초기화한다」
            } else if (s.equalsIgnoreCase("k")) {
                htmlid = "";
                pc.getInventory().consumeItem(41123, 1); // 카헬의 타락 한 가루
                pc.getInventory().consumeItem(41124, 1); // 카헬의 무력 한 가루
                pc.getInventory().consumeItem(41125, 1); // 카헬의 아집 한 가루
                pc.getInventory().consumeItem(41126, 1); // 핏자국의 타락 한 정수
                pc.getInventory().consumeItem(41127, 1); // 핏자국의 무력 한 정수
                pc.getInventory().consumeItem(41128, 1); // 핏자국의 아집 한 정수
                pc.getInventory().consumeItem(41129, 1); // 핏자국의 정수
                pc.getQuest().set_step(L1Quest.QUEST_SHADOWS, 0);
                // 정수를 건네준다
            } else if (s.equalsIgnoreCase("e")) {
                if (pc.getQuest().get_step(L1Quest.QUEST_SHADOWS) == L1Quest.QUEST_END
                        || pc.getKarmaLevel() >= 1) {
                    htmlid = "";
                } else {
                    if (pc.getInventory().checkItem(41129)) {
                        htmlid = "";
                        pc.addKarma((int) (-1600 * Config.RATE_KARMA));
                        pc.getInventory().consumeItem(41121, 1); // 카헬의 계약서
                        pc.getInventory().consumeItem(41122, 1); // 카헬의 지령서
                        pc.getInventory().consumeItem(41129, 1); // 핏자국의 정수
                        pc.getQuest().set_step(L1Quest.QUEST_SHADOWS,
                                L1Quest.QUEST_END);
                    } else {
                        htmlid = "minitos04";
                    }
                }
                // 재빠르게 받는다
            } else if (s.equalsIgnoreCase("g")) {
                htmlid = "";
                final int[] item_ids = {41121}; // 카헬의 계약서
                final int[] item_amounts = {1};
                for (int i = 0; i < item_ids.length; i++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            item_ids[i], item_amounts[i]);
                    pc.sendPackets(new S_ServerMessage(143,
                            ((L1NpcInstance) obj).getNpcTemplate().get_name(),
                            item.getItem().getName()));
                }
            }
        }
        // else System.out.println("C_NpcAction: " + s);
        if (htmlid != null && htmlid.equalsIgnoreCase("colos2")) {
            htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate()
                    .get_npcId());
        }
        if (createitem != null) { // 아이템 정제
            boolean isCreate = true;
            for (int j = 0; j < materials.length; j++) {
                if (!pc.getInventory().checkItemNotEquipped(materials[j],
                        counts[j])) {
                    L1Item temp = ItemTable.getInstance().getTemplate(
                            materials[j]);
                    pc.sendPackets(new S_ServerMessage(337, temp.getName())); // \f1%0이 부족합니다.
                    isCreate = false;
                }
            }

            if (isCreate) {
                // 용량과 중량의 계산
                int create_count = 0; // 아이템의 개수(전만물은 1개)
                int create_weight = 0;
                for (int k = 0; k < createitem.length; k++) {
                    L1Item temp = ItemTable.getInstance().getTemplate(
                            createitem[k]);
                    if (temp.isStackable()) {
                        if (!pc.getInventory().checkItem(createitem[k])) {
                            create_count += 1;
                        }
                    } else {
                        create_count += createcount[k];
                    }
                    create_weight += temp.getWeight() * createcount[k] / 1000;
                }
                // 용량 확인
                if (pc.getInventory().getSize() + create_count > 180) {
                    pc.sendPackets(new S_ServerMessage(263)); // \f1한사람의 캐릭터가 가지고 걸을 수 있는 아이템은 최대 180개까지입니다.
                    return;
                }
                // 중량 확인
                if (pc.getMaxWeight() < pc.getInventory().getWeight()
                        + create_weight) {
                    pc.sendPackets(new S_ServerMessage(82)); // 아이템이 너무 무거워, 더 이상 가질 수 없습니다.
                    return;
                }

                for (int j = 0; j < materials.length; j++) {
                    // 재료 소비
                    pc.getInventory().consumeItem(materials[j], counts[j]);
                }
                for (int k = 0; k < createitem.length; k++) {
                    L1ItemInstance item = pc.getInventory().storeItem(
                            createitem[k], createcount[k]);
                    if (item != null) {
                        String itemName = ItemTable.getInstance().getTemplate(
                                createitem[k]).getName();
                        String createrName = "";
                        if (obj instanceof L1NpcInstance) {
                            createrName = ((L1NpcInstance) obj)
                                    .getNpcTemplate().get_name();
                        }
                        if (createcount[k] > 1) {
                            pc.sendPackets(new S_ServerMessage(143,
                                    createrName, itemName + " ("
                                    + createcount[k] + ")")); // \f1%0이%1를 주었습니다.
                        } else {
                            pc.sendPackets(new S_ServerMessage(143,
                                    createrName, itemName)); // \f1%0이%1를 주었습니다.
                        }
                    }
                }
                if (success_htmlid != null) { // html 지정이 있는 경우는 표시
                    pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid,
                            htmldata));
                }
            } else { // 정제 실패
                if (failure_htmlid != null) { // html 지정이 있는 경우는 표시
                    pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid,
                            htmldata));
                }
            }
        }

        if (htmlid != null) { // html 지정이 있는 경우는 표시
            pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
        }
    }

    private String karmaLevelToHtmlId(int level) {
        if (level == 0 || level < -7 || 7 < level) {
            return "";
        }
        String htmlid = "";
        if (0 < level) {
            htmlid = "vbk" + level;
        } else if (level < 0) {
            htmlid = "vyk" + Math.abs(level);
        }
        return htmlid;
    }

    private String watchUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        L1Location loc = ub.getLocation();
        if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
            try {
                pc.save();
                pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(),
                        true);
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        } else {
            pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
        }
        return "";
    }

    private String enterUb(L1PcInstance pc, int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        if (!ub.isActive() || !ub.canPcEnter(pc)) { // 시간외
            return "colos2";
        }
        if (ub.isNowUb()) { // 경기중
            return "colos1";
        }
        if (ub.getMembersCount() >= ub.getMaxPlayer()) { // 정원 오버
            return "colos4";
        }

        ub.addMember(pc); // 멤버에게 추가
        L1Location loc = ub.getLocation().randomLocation(10, false);
        L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
        return "";
    }

    private String enterHauntedHouse(L1PcInstance pc) {
        if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // 경기중
            pc.sendPackets(new S_ServerMessage(1182)); // 이제(벌써) 게임은 시작되어 있어.
            return "";
        }
        if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // 정원 오버
            pc.sendPackets(new S_ServerMessage(1184)); // 도깨비 저택은 사람으로 가득해.
            return "";
        }

        L1HauntedHouse.getInstance().addMember(pc); // 멤버에게 추가
        L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
        return "";
    }

    private String enterPetMatch(L1PcInstance pc, int objid2) {
        Object[] petlist = pc.getPetList().values().toArray();
        if (petlist.length > 0) {
            pc.sendPackets(new S_ServerMessage(1187)); // 펫의 아뮤렛트가 사용중입니다.
            return "";
        }
        if (!L1PetMatch.getInstance().enterPetMatch(pc, objid2)) {
            pc.sendPackets(new S_ServerMessage(1182)); // 이제(벌써) 게임은 시작되어 있어.
        }
        return "";
    }

    private void summonMonster(L1PcInstance pc, String s) {
        String[] summonstr_list;
        int[] summonid_list;
        int[] summonlvl_list;
        int[] summoncha_list;
        int summonid = 0;
        int levelrange = 0;
        int summoncost = 0;
		/*  summonstr_list = new String[] { "7", "263", "8", "264", "9", "265",
		    "10", "266", "11", "267", "12", "268", "13", "269", "14",
		    "270", "526", "15", "271", "527", "17", "18" };
		  summonid_list = new int[] { 81083, 81090, 81084, 81091, 81085, 81092,
		    81086, 81093, 81087, 81094, 81088, 81095, 81089, 81096, 81097,
		    81098, 81099, 81100, 81101, 81102, 81103, 81104 };

		  summonlvl_list = new int[] { 28, 28, 32, 32, 36, 36, 40, 40, 44, 44,
		    48, 48, 52, 52, 56, 56, 56, 60, 60, 60, 68, 72 };
		*/
        // 돕페르겐가보스, 크가에는 애완동물 보너스가 붙지 않기 때문에+6해 둔다
        summonstr_list = new String[]{"7", "263", "519", "8", "264", "520",
                "9", "265", "521", "10", "266", "522", "11", "267", "523",
                "12", "268", "524", "13", "269", "525", "14", "270", "526",
                "15", "271", "527", "16", "17", "18", "274"};
        summonid_list = new int[]{81210, 81211, 81212, 81213, 81214, 81215,
                81216, 81217, 81218, 81219, 81220, 81221, 81222, 81223, 81224,
                81225, 81226, 81227, 81228, 81229, 81230, 81231, 81232, 81233,
                81234, 81235, 81236, 81237, 81238, 81239, 81240};
        summonlvl_list = new int[]{28, 28, 28, 32, 32, 32, 36, 36, 36, 40, 40,
                40, 44, 44, 44, 48, 48, 48, 52, 52, 52, 56, 56, 56, 60, 60, 60,
                64, 68, 72, 72};

        summoncha_list = new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
                8, 8, 8, 8, 8, 8, 8, 10, 10, 10, 12, 12, 12, 20, 42, 42, 50};

        for (int loop = 0; loop < summonstr_list.length; loop++) {
            if (s.equalsIgnoreCase(summonstr_list[loop])) {
                summonid = summonid_list[loop];
                levelrange = summonlvl_list[loop];
                summoncost = summoncha_list[loop];
                break;
            }
        }
        // Lv부족
        if (pc.getLevel() < levelrange) {
            // 레벨이 낮아서 해당의 monster를 소환할 수가 없습니다.
            pc.sendPackets(new S_ServerMessage(743));
            return;
        }
        //////////////////////////////////////////////////////////////////////////////// 서먼몹 본섭화 몹스킬패턴 - 시작
        if (summonid == 81210 || summonid == 81211 || summonid == 81212    // 28레벨
                || summonid == 81213 || summonid == 81214 || summonid == 81215 // 32레벨
                || summonid == 81216 || summonid == 81217 || summonid == 81218 // 36레벨
                || summonid == 81219 || summonid == 81220 || summonid == 81221 // 40레벨
                || summonid == 81222 || summonid == 81223 || summonid == 81224 // 44레벨
                || summonid == 81225 || summonid == 81226 || summonid == 81227 // 48레벨
                || summonid == 81228 || summonid == 81229 || summonid == 81230 // 52레벨 - 1~5마리까지
        ) {
            if (pc.getCha() <= 9) {
                summoncost = 8;
            } else if (pc.getCha() >= 10 && pc.getCha() <= 17) {
                summoncost = 8;
            } else if (pc.getCha() >= 18 && pc.getCha() <= 25) {
                summoncost = 8;
            } else if (pc.getCha() >= 26 && pc.getCha() <= 33) {
                summoncost = 8;
            } else if (pc.getCha() >= 34 && pc.getCha() <= 41) {
                summoncost = 8;
            } else if (pc.getCha() >= 42 && pc.getCha() <= 47) {
                summoncost = 9;
            } else if (pc.getCha() >= 48) {
                summoncost = 10;
            }
        } else if (summonid == 81231 || summonid == 81232 || summonid == 81233) { // 56레벨 - 1~4마리까지
            if (pc.getCha() <= 13) {
                summoncost = 10;
            } else if (pc.getCha() >= 14 && pc.getCha() <= 23) {
                summoncost = 10;
            } else if (pc.getCha() >= 24 && pc.getCha() <= 33) {
                summoncost = 10;
            } else if (pc.getCha() >= 34 && pc.getCha() <= 43) {
                summoncost = 10;
            } else if (pc.getCha() >= 44 && pc.getCha() <= 48) {
                summoncost = 11;
            } else if (pc.getCha() >= 49) {
                summoncost = 12;
            }
        } else if (summonid == 81234 || summonid == 81235 || summonid == 81236) { // 60레벨 - 1~3마리까지
            if (pc.getCha() <= 17) {
                summoncost = 12;
            } else if (pc.getCha() >= 18 && pc.getCha() <= 29) {
                summoncost = 12;
            } else if (pc.getCha() >= 30 && pc.getCha() <= 41) {
                summoncost = 12;
            } else if (pc.getCha() >= 42 && pc.getCha() <= 45) {
                summoncost = 13;
            } else if (pc.getCha() >= 46 && pc.getCha() <= 49) {
                summoncost = 14;
            } else if (pc.getCha() >= 50) {
                summoncost = 15;
            }
        } else if (summonid == 81237) { // 64레벨 헬바운드 - 0~2마리까지
            if (pc.getCha() <= 13) {
                summoncost = 20;
            } else if (pc.getCha() >= 14 && pc.getCha() <= 33) {
                summoncost = 20;
            } else if (pc.getCha() >= 34 && pc.getCha() <= 53) {
                summoncost = 20;
            } else if (pc.getCha() >= 54 && pc.getCha() <= 56) {
                summoncost = 21;
            } else if (pc.getCha() >= 57) {
                summoncost = 22;
            }
        } else if (summonid == 81238 || summonid == 81239) { // 36카리 보스 - 0~1마리까지
            if (pc.getCha() <= 35) {
                summoncost = 42;
            } else if (pc.getCha() >= 36) {
                summoncost = 42;
            }
        } else if (summonid == 81240) { // 44카리 쿠거 - 0~1마리까지
            if (pc.getCha() <= 43) {
                summoncost = 50;
            } else if (pc.getCha() >= 44) {
                summoncost = 50;
            }
        }
        //////////////////////////////////////////////////////////////////////////////// 서먼몹 본섭화 몹스킬패턴 - 끝

        int petcost = 0;
        Object[] petlist = pc.getPetList().values().toArray();
        for (Object pet : petlist) {
            // 현재의 애완동물 코스트
            petcost += ((L1NpcInstance) pet).getPetcost();
        }
        // 이미 애완동물이 있는 경우는, 돕페르겐가보스, 크가는 호출할 수 없다
        if ((summonid == 81238 || summonid == 81239 || summonid == 81240) && petcost != 0) { /// 몹스킬패턴 - 수정
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }
        int charisma = pc.getCha() + 6 - petcost;
        int summoncount = charisma / summoncost;
        L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
        for (int cnt = 0; cnt < summoncount; cnt++) {
            L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
            if (summonid == 81238 || summonid == 81239 || summonid == 81240) {            ///////// 몹스킬패턴 - 수정
                summon.setPetcost(pc.getCha() + 7);
            } else {
                summon.setPetcost(summoncost);
            }
        }
        pc.sendPackets(new S_CloseList(pc.getId()));
    }

    private void poly(ClientThread clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
            pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_NPC);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
        }
    }

    private void polyByKeplisha(ClientThread clientthread, int polyId) {
        L1PcInstance pc = clientthread.getActiveChar();

        if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
            pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

            L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_KEPLISHA);
        } else {
            pc.sendPackets(new S_ServerMessage(337, "$4")); // 아데나가 부족합니다.
        }
    }

    private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan == null) {
            return ""; // 윈도우를 지운다
        }
        int houseId = clan.getHouseId();
        if (houseId == 0) {
            return ""; // 윈도우를 지운다
        }
        L1House house = HouseTable.getInstance().getHouseTable(houseId);
        int keeperId = house.getKeeperId();
        if (npcId != keeperId) {
            return ""; // 윈도우를 지운다
        }
        if (!pc.isCrown()) {
            pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
            return ""; // 윈도우를 지운다
        }
        if (pc.getId() != clan.getLeaderId()) {
            pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
            return ""; // 윈도우를 지운다
        }
        if (house.isOnSale()) {
            return "agonsale";
        }

        pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
        return null;
    }

    private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
        int doorId = 0;
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    L1DoorInstance door1 = null;
                    L1DoorInstance door2 = null;
                    L1DoorInstance door3 = null;
                    L1DoorInstance door4 = null;
                    for (L1DoorInstance door : DoorSpawnTable.getInstance()
                            .getDoorList()) {
                        if (door.getKeeperId() == keeperId) {
                            if (door1 == null) {
                                door1 = door;
                                continue;
                            }
                            if (door2 == null) {
                                door2 = door;
                                continue;
                            }
                            if (door3 == null) {
                                door3 = door;
                                continue;
                            }
                            if (door4 == null) {
                                door4 = door;
                                break;
                            }
                        }
                    }
                    if (door1 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door1.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door1.close();
                        }
                    }
                    if (door2 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door2.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door2.close();
                        }
                    }
                    if (door3 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door3.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door3.close();
                        }
                    }
                    if (door4 != null) {
                        if (s.equalsIgnoreCase("open")) {
                            door4.open();
                        } else if (s.equalsIgnoreCase("close")) {
                            door4.close();
                        }
                    }
                }
            }
        }
    }

    private void openCloseGate(L1PcInstance pc, int keeperId, boolean isOpen) {
        boolean isNowWar = false;
        int pcCastleId = 0;
        if (pc.getClanid() != 0) {
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                pcCastleId = clan.getCastleId();
            }
        }
        if (keeperId == 70656 || keeperId == 70549 || keeperId == 70985) { // 켄트성
            if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.KENT_CASTLE_ID);
        } else if (keeperId == 70600) { // OT
            if (isExistDefenseClan(L1CastleLocation.OT_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.OT_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.OT_CASTLE_ID);
        } else if (keeperId == 70778 || keeperId == 70987 || keeperId == 70687) { // WW성
            if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.WW_CASTLE_ID);
        } else if (keeperId == 70817 || keeperId == 70800 || keeperId == 70988
                || keeperId == 70990 || keeperId == 70989 || keeperId == 70991) { // 기란성
            if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.GIRAN_CASTLE_ID);
        } else if (keeperId == 70863 || keeperId == 70992 || keeperId == 70862) { // Heine성
            if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.HEINE_CASTLE_ID);
        } else if (keeperId == 70995 || keeperId == 70994 || keeperId == 70993) { // 드워후성
            if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.DOWA_CASTLE_ID);
        } else if (keeperId == 70996) { // 에덴성
            if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
                if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
                    return;
                }
            }
            isNowWar = WarTimeController.getInstance().isNowWar(
                    L1CastleLocation.ADEN_CASTLE_ID);
        }

        for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
            if (door.getKeeperId() == keeperId) {
                if (isNowWar && door.getMaxHp() > 1) { // 전쟁중은 성문개폐 불가
                } else {
                    if (isOpen) { // 개
                        door.open();
                    } else { // 폐
                        door.close();
                    }
                }
            }
        }
    }

    private boolean isExistDefenseClan(int castleId) {
        boolean isExistDefenseClan = false;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        return isExistDefenseClan;
    }

    private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
        int houseId = 0;
        for (L1House house : HouseTable.getInstance().getHouseTableList()) {
            if (house.getKeeperId() == keeperId) {
                houseId = house.getHouseId();
            }
        }
        if (houseId == 0) {
            return;
        }

        int[] loc = new int[3];
        for (L1Object object : L1World.getInstance().getObject()) {
            if (object instanceof L1PcInstance) {
                L1PcInstance pc = (L1PcInstance) object;
                if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(),
                        pc.getMapId())
                        && clanPc.getClanid() != pc.getClanid()) {
                    loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
                    if (pc != null) {
                        L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
                                5, true);
                    }
                }
            }
        }
    }

    private void repairGate(L1PcInstance pc) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int castleId = clan.getCastleId();
            if (castleId != 0) { // 성주 크란
                if (!WarTimeController.getInstance().isNowWar(castleId)) {
                    // 성문을 바탕으로 되돌린다
                    for (L1DoorInstance door : DoorSpawnTable.getInstance()
                            .getDoorList()) {
                        if (L1CastleLocation.checkInWarArea(castleId, door)) {
                            door.repairGate();
                        }
                    }
                    pc.sendPackets(new S_ServerMessage(990)); // 성문자동 수리를 명령했습니다.
                } else {
                    pc.sendPackets(new S_ServerMessage(991)); // 성문자동 수리 명령을 취소했습니다.
                }
            }
        }
    }

    private void payFee(L1PcInstance pc, L1NpcInstance npc) {
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    if (pc.getInventory().checkItem(L1ItemId.ADENA, 2000)) {
                        pc.getInventory().consumeItem(L1ItemId.ADENA, 2000);
                        TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
                        Calendar cal = Calendar.getInstance(tz);
                        cal.add(Calendar.DATE, Config.HOUSE_TAX_INTERVAL);
                        cal.set(Calendar.MINUTE, 0); // 분 , 초는 잘라서 버림
                        cal.set(Calendar.SECOND, 0);
                        house.setTaxDeadline(cal);
                        HouseTable.getInstance().updateHouse(house); // DB에 기입해
                    } else {
                        pc.sendPackets(new S_ServerMessage(189)); // \f1아데나가 부족합니다.
                    }
                }
            }
        }
    }

    private String[] makeHouseTaxStrings(L1PcInstance pc, L1NpcInstance npc) {
        String name = npc.getNpcTemplate().get_name();
        String[] result;
        result = new String[]{name, "2000", "1", "1", "00"};
        L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
        if (clan != null) {
            int houseId = clan.getHouseId();
            if (houseId != 0) {
                L1House house = HouseTable.getInstance().getHouseTable(houseId);
                int keeperId = house.getKeeperId();
                if (npc.getNpcTemplate().get_npcId() == keeperId) {
                    Calendar cal = house.getTaxDeadline();
                    int month = cal.get(Calendar.MONTH) + 1;
                    int day = cal.get(Calendar.DATE);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    result = new String[]{name, "2000",
                            String.valueOf(month), String.valueOf(day),
                            String.valueOf(hour)};
                }
            }
        }
        return result;
    }

    private String[] makeWarTimeStrings(int castleId) {
        L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
        if (castle == null) {
            return null;
        }
        Calendar warTime = castle.getWarTime();
        int year = warTime.get(Calendar.YEAR);
        int month = warTime.get(Calendar.MONTH) + 1;
        int day = warTime.get(Calendar.DATE);
        int hour = warTime.get(Calendar.HOUR_OF_DAY);
        int minute = warTime.get(Calendar.MINUTE);
        String[] result;
        if (castleId == L1CastleLocation.OT_CASTLE_ID) {
            result = new String[]{String.valueOf(year),
                    String.valueOf(month), String.valueOf(day),
                    String.valueOf(hour), String.valueOf(minute)};
        } else {
            result = new String[]{"", String.valueOf(year),
                    String.valueOf(month), String.valueOf(day),
                    String.valueOf(hour), String.valueOf(minute)};
        }
        return result;
    }

    private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] amuletIdList = {20358, 20359, 20360, 20361, 20362, 20363, 20364,
                20365};
        int amuletId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (s.equalsIgnoreCase("1")) {
            if (pc.getKarmaLevel() == -1) { // -1 부분
                amuletId = amuletIdList[0];
            }
        } else if (s.equalsIgnoreCase("2")) {
            if (pc.getKarmaLevel() == -2) { // -2 부분
                amuletId = amuletIdList[1];
            }
        } else if (s.equalsIgnoreCase("3")) {
            if (pc.getKarmaLevel() == -3) { // -3 부분
                amuletId = amuletIdList[2];
            }
        } else if (s.equalsIgnoreCase("4")) {
            if (pc.getKarmaLevel() == -4) { // -1 부분
                amuletId = amuletIdList[3];
            }
        } else if (s.equalsIgnoreCase("5")) {
            if (pc.getKarmaLevel() == -5) { // -1 부분
                amuletId = amuletIdList[4];
            }
        } else if (s.equalsIgnoreCase("6")) {
            if (pc.getKarmaLevel() == -6) { // -1 부분
                amuletId = amuletIdList[5];
            }
        } else if (s.equalsIgnoreCase("7")) {
            if (pc.getKarmaLevel() == -7) { // -1 부분
                amuletId = amuletIdList[6];
            }
        } else if (s.equalsIgnoreCase("8")) {
            if (pc.getKarmaLevel() == -8) { // -1 부분
                amuletId = amuletIdList[7];
            }
        }
        if (amuletId != 0) {
            item = pc.getInventory().storeItem(amuletId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            for (int id : amuletIdList) {
                if (id == amuletId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
        int[] earringIdList = {21020, 21021, 21022, 21023, 21024, 21025,
                21026, 21027};
        int earringId = 0;
        L1ItemInstance item = null;
        String htmlid = null;
        if (s.equalsIgnoreCase("1")) {
            if (pc.getKarmaLevel() == 1) { // 1 부분
                earringId = earringIdList[0];
            }
        } else if (s.equalsIgnoreCase("2")) {
            if (pc.getKarmaLevel() == 2) { // 2 부분
                earringId = earringIdList[1];
            }
        } else if (s.equalsIgnoreCase("3")) {
            if (pc.getKarmaLevel() == 3) { // 3 부분
                earringId = earringIdList[2];
            }
        } else if (s.equalsIgnoreCase("4")) {
            if (pc.getKarmaLevel() == 4) { // 4 부분
                earringId = earringIdList[3];
            }
        } else if (s.equalsIgnoreCase("5")) {
            if (pc.getKarmaLevel() == 5) { // 5 부분
                earringId = earringIdList[4];
            }
        } else if (s.equalsIgnoreCase("6")) {
            if (pc.getKarmaLevel() == 6) { // 6 부분
                earringId = earringIdList[5];
            }
        } else if (s.equalsIgnoreCase("7")) {
            if (pc.getKarmaLevel() == 7) { // 7 부분
                earringId = earringIdList[6];
            }
        } else if (s.equalsIgnoreCase("8")) {
            if (pc.getKarmaLevel() == 8) { // 8 부분
                earringId = earringIdList[7];
            }
        }
        if (earringId != 0) {
            item = pc.getInventory().storeItem(earringId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            for (int id : earringIdList) {
                if (id == earringId) {
                    break;
                }
                if (pc.getInventory().checkItem(id)) {
                    pc.getInventory().consumeItem(id, 1);
                }
            }
            htmlid = "";
        }
        return htmlid;
    }

    private String[] makeUbInfoStrings(int npcId) {
        L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
        return ub.makeUbInfoStrings();
    }

    private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc,
                                       String s) {
        String htmlid = "";
        int protectionId = 0;
        int sealId = 0;
        int locX = 0;
        int locY = 0;
        short mapId = 0;
        if (npc.getNpcTemplate().get_npcId() == 80059) { // 차원의 문(토)
            protectionId = 40909;
            sealId = 40913;
            locX = 32773;
            locY = 32835;
            mapId = 607;
        } else if (npc.getNpcTemplate().get_npcId() == 80060) { // 차원의 문(바람)
            protectionId = 40912;
            sealId = 40916;
            locX = 32757;
            locY = 32842;
            mapId = 606;
        } else if (npc.getNpcTemplate().get_npcId() == 80061) { // 차원의 문(수)
            protectionId = 40910;
            sealId = 40914;
            locX = 32830;
            locY = 32822;
            mapId = 604;
        } else if (npc.getNpcTemplate().get_npcId() == 80062) { // 차원의 문(화)
            protectionId = 40911;
            sealId = 40915;
            locX = 32835;
            locY = 32822;
            mapId = 605;
        }

        // 「안에 들어와 본다」 「원소의 지배자를 접근해 본다」 「통행증을 사용한다」 「통과한다」
        if (s.equalsIgnoreCase("a")) {
            L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
            htmlid = "";
        }
        // 「그림으로부터 돌출부분을 없앤다」
        else if (s.equalsIgnoreCase("b")) {
            L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            htmlid = "";
        }
        // 「통행증을 버려, 이 땅을 포기한다」
        else if (s.equalsIgnoreCase("c")) {
            htmlid = "wpass07";
        }
        // 「계속한다」
        else if (s.equalsIgnoreCase("d")) {
            if (pc.getInventory().checkItem(sealId)) { // 지의 인장
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
        }
        // 「그대로 한다」 「당황해 줍는다」
        else if (s.equalsIgnoreCase("e")) {
            htmlid = "";
        }
        // 「사라지도록(듯이) 한다」
        else if (s.equalsIgnoreCase("f")) {
            if (pc.getInventory().checkItem(protectionId)) { // 지의 통행증
                pc.getInventory().consumeItem(protectionId, 1);
            }
            if (pc.getInventory().checkItem(sealId)) { // 지의 인장
                L1ItemInstance item = pc.getInventory().findItemId(sealId);
                pc.getInventory().consumeItem(sealId, item.getCount());
            }
            htmlid = "";
        }
        return htmlid;
    }

    private boolean isNpcSellOnly(L1NpcInstance npc) {
        int npcId = npc.getNpcTemplate().get_npcId();
        String npcName = npc.getNpcTemplate().get_name();
        if (npcId == 70027 // 디오
                || "에덴상단".equals(npcName)) {
            return true;
        }
        return false;
    }

    private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
                                        String s) {
        L1ItemInstance item = null;

        // 「블래드 크리스탈의 조각을 1개 주세요」
        if (s.equalsIgnoreCase("1")) {
            pc.addKarma((int) (500 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 야히의 모습을 기억하는 것이 어려워집니다.
            pc.sendPackets(new S_ServerMessage(1081));
        }
        // 「블래드 크리스탈의 조각을 10개 주세요」
        else if (s.equalsIgnoreCase("2")) {
            pc.addKarma((int) (5000 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 야히의 모습을 기억하는 것이 어려워집니다.
            pc.sendPackets(new S_ServerMessage(1081));
        }
        // 「블래드 크리스탈의 조각을 100개 주세요」
        else if (s.equalsIgnoreCase("3")) {
            pc.addKarma((int) (50000 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40718, 100);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 야히의 모습을 기억하는 것이 어려워집니다.
            pc.sendPackets(new S_ServerMessage(1081));
        }
    }

    private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
                                       String s) {
        L1ItemInstance item = null;

        // 「서울 크리스탈의 조각을 1개 주세요」
        if (s.equalsIgnoreCase("1")) {
            pc.addKarma((int) (-500 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 1);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 바르로그의 냉소를 느낌 오한이 달립니다.
            pc.sendPackets(new S_ServerMessage(1080));
        }
        // 「서울 크리스탈의 조각을 10개 주세요」
        else if (s.equalsIgnoreCase("2")) {
            pc.addKarma((int) (-5000 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 10);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 바르로그의 냉소를 느낌 오한이 달립니다.
            pc.sendPackets(new S_ServerMessage(1080));
        }
        // 「서울 크리스탈의 조각을 100개 주세요」
        else if (s.equalsIgnoreCase("3")) {
            pc.addKarma((int) (-50000 * Config.RATE_KARMA));
            item = pc.getInventory().storeItem(40678, 100);
            if (item != null) {
                pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
                        .get_name(), item.getLogName())); // \f1%0이%1를 주었습니다.
            }
            // 바르로그의 냉소를 느낌 오한이 달립니다.
            pc.sendPackets(new S_ServerMessage(1080));
        }
    }

    private void StatInitialize(L1PcInstance pc) {

        pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.시작));
    }

    /*private String enterPe(L1PcInstance pc, int npcId) {

		if(L1Racing.getInstance().isStart()){
			pc.sendPackets(new S_SystemMessage("이미 경기가 시작중입니다."));
		}else if(L1Racing.getInstance().contains(0, pc)){
			pc.sendPackets(new S_SystemMessage("이미 경기에 참여하셨습니다."));
		}else{
			L1Racing.getInstance().add(0, pc); // 멤버에게 추가
			pc.sendPackets(new S_ServerMessage(1253, L1Racing.getInstance().size(0) + ""));
		}
		return "";
	}*/
    private String enterPe(L1PcInstance pc, int npcId) {
        L1PetMember pm = L1World.getInstance().getPetMember();
        L1PetRace pe = L1World.getInstance().getPetRace();

        if (pe == null) { // 시간외
            pc.sendPackets(new S_SystemMessage("펫 레이싱 경기는 준비중입니다."));
            return "";
        }

        if (pe.isNowPet()) { // 경기중
            pc.sendPackets(new S_ServerMessage(1182, ""));
            return "";
        }

        if (pe.getMembersCount() > 10) { // 정원 오버
            pc.sendPackets(new S_ServerMessage(1229, ""));
            return "";
        }

        if (pe.isMember(pc)) { // 이미맴버라면
            pc.sendPackets(new S_ServerMessage(1254, ""));
            return "";
        }

        if (pe.isInTime()) { // 입장대기 1분이라면
            pe.addMember(pc);
            Random random = new Random(); // 펫레이싱
            int locx = 32767 + random.nextInt(2);
            int locy = 32848 + random.nextInt(2);
            new L1SkillUse().handleCommands(pc, 44, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
            L1Teleport.teleport(pc, locx, locy, (short) 5143, 5, true);
            return "";
        }

        pe.addMember(pc); // 멤버에게 추가
        pc.sendPackets(new S_ServerMessage(1253, "" + pe.getMembersCount() + ""));

        return "";
    }

    // 펫상인 및 수상한조련사 by 아스라이
    private void mobspawn1(L1PcInstance pc, int npcid, int randomrange) {
        try {
            L1Npc l1npc = NpcTable.getInstance().getTemplate(npcid);
            if (l1npc != null) {
                Object obj = null;
                try {
                    String s3 = l1npc.getImpl();
                    Constructor constructor = Class.forName(
                            "l1j.server.server.model.Instance." + s3
                                    + "Instance").getConstructors()[0];
                    Object aobj[] = {l1npc};
                    L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
                    npc.setId(IdFactory.getInstance().nextId());

                    L1World.getInstance().storeObject(npc);
                    L1World.getInstance().addVisibleObject(npc);
                    L1Object object = L1World.getInstance().findObject(npc.getId());
                    L1NpcInstance newnpc = (L1NpcInstance) object;
                    npc.setMap(pc.getMapId());
                    if (randomrange == 0) {
                        if (pc.getHeading() == 0) {
                            npc.setX(pc.getX());
                            npc.setY(pc.getY() - 1);
                        } else if (pc.getHeading() == 1) {
                            npc.setX(pc.getX() + 1);
                            npc.setY(pc.getY() - 1);
                        } else if (pc.getHeading() == 2) {
                            npc.setX(pc.getX() + 1);
                            npc.setY(pc.getY());
                        } else if (pc.getHeading() == 3) {
                            npc.setX(pc.getX() + 1);
                            npc.setY(pc.getY() + 1);
                        } else if (pc.getHeading() == 4) {
                            npc.setX(pc.getX());
                            npc.setY(pc.getY() + 1);
                        } else if (pc.getHeading() == 5) {
                            npc.setX(pc.getX() - 1);
                            npc.setY(pc.getY() + 1);
                        } else if (pc.getHeading() == 6) {
                            npc.setX(pc.getX() - 1);
                            npc.setY(pc.getY());
                        } else if (pc.getHeading() == 7) {
                            npc.setX(pc.getX() - 1);
                            npc.setY(pc.getY() - 1);
                        }
                    } else {
                        int tryCount = 0;
                        do {
                            tryCount++;
                            npc.setX(pc.getX()
                                    + (int) (Math.random() * randomrange)
                                    - (int) (Math.random() * randomrange));
                            npc.setY(pc.getY()
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
                            if (pc.getHeading() == 0) {
                                npc.setX(pc.getX());
                                npc.setY(pc.getY() - 1);
                            } else if (pc.getHeading() == 1) {
                                npc.setX(pc.getX() + 1);
                                npc.setY(pc.getY() - 1);
                            } else if (pc.getHeading() == 2) {
                                npc.setX(pc.getX() + 1);
                                npc.setY(pc.getY());
                            } else if (pc.getHeading() == 3) {
                                npc.setX(pc.getX() + 1);
                                npc.setY(pc.getY() + 1);
                            } else if (pc.getHeading() == 4) {
                                npc.setX(pc.getX());
                                npc.setY(pc.getY() + 1);
                            } else if (pc.getHeading() == 5) {
                                npc.setX(pc.getX() - 1);
                                npc.setY(pc.getY() + 1);
                            } else if (pc.getHeading() == 6) {
                                npc.setX(pc.getX() - 1);
                                npc.setY(pc.getY());
                            } else if (pc.getHeading() == 7) {
                                npc.setX(pc.getX() - 1);
                                npc.setY(pc.getY() - 1);
                            }
                        }
                    }

                    npc.setHomeX(npc.getX());
                    npc.setHomeY(npc.getY());
                    npc.setHeading(pc.getHeading());
                    tamePet(pc, newnpc);
                } catch (Exception e) {
                    _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception exception) {
        }
    }

    private void tamePet(L1PcInstance pc, L1NpcInstance newnpc) {
        if (newnpc instanceof L1PetInstance
                || newnpc instanceof L1SummonInstance) {
            return;
        }

        int petcost = 0;
        Object[] petlist = pc.getPetList().values().toArray();
        for (Object pet : petlist) {
            petcost += ((L1NpcInstance) pet).getPetcost();
        }
        int charisma = pc.getCha();
        if (pc.isCrown()) { // 군주
            charisma += 6;
        } else if (pc.isElf()) { // 에르프
            charisma += 12;
        } else if (pc.isWizard()) { // WIZ
            charisma += 6;
        } else if (pc.isDarkelf()) { // DE
            charisma += 6;
        } else if (pc.isDragonKnight()) { // 드래곤 나이트
            charisma += 6;
        } else if (pc.isBlackWizard()) { // 환술사
            charisma += 6;
        }
        charisma -= petcost;

        L1PcInventory inv = pc.getInventory();
        String npcname = newnpc.getNpcTemplate().get_name();
        if (charisma >= 6 && inv.getSize() < 180) {
            if (isTamePet(newnpc)) {
                L1ItemInstance petamu = inv.storeItem(40314, 1); // 펫의 아뮤렛트
                if (petamu != null) {
                    new L1PetInstance(newnpc, pc, petamu.getId());
                    pc.sendPackets(new S_ItemName(petamu));
                    pc.sendPackets(new S_SystemMessage(npcname + "의 목걸이를 얻었습니다."));
                }
            } else {
                pc.sendPackets(new S_ServerMessage(324)); // 길들이는데 실패했습니다.
            }
        }
    }

    private boolean isTamePet(L1NpcInstance newnpc) {
        boolean isSuccess = false;
        int npcId = newnpc.getNpcTemplate().get_npcId();
        switch (npcId) {
            case 45034: //세퍼드
                isSuccess = true;
                break;
            case 45042: //도베르만
                isSuccess = true;
                break;
            case 45046: //비글
                isSuccess = true;
                break;
            case 45047: //세인트 버나드
                isSuccess = true;
                break;
            case 46044: //아기판다곰
                isSuccess = true;
                break;
            case 46042: //아기캥거루
                isSuccess = true;
                break;
            case 777790: //해츨링 여
                isSuccess = true;
                break;
            case 777787: //해츨링 남
                isSuccess = true;
                break;
            default:
                isSuccess = false;
                break;
        }
        return isSuccess;
    }

    // 펫상인 및 수상한조련사
    @Override
    public String getType() {
        return C_NPC_ACTION;
    }

}