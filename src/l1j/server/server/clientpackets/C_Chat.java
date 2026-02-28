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

import l1j.server.Config;
import l1j.server.server.ClientThread;
import l1j.server.server.GMCommands;
import l1j.server.server.Opcodes;
import l1j.server.server.UserCommands;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Gambling;
import l1j.server.server.model.L1Gambling3;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.util.logging.Logger;

import static l1j.server.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static l1j.server.server.model.skill.L1SkillId.SILENCE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_POISON_SILENCE;
// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Chat extends ClientBasePacket {

    private static final String C_CHAT = "[C] C_Chat";
    private static Logger _log = Logger.getLogger(C_Chat.class.getName());

    public C_Chat(byte abyte0[], ClientThread clientthread) {
        super(abyte0);

        L1PcInstance pc = clientthread.getActiveChar();
        int chatType = readC();
        String chatText = readS();
        if (pc.get_autogo() == 1 && chatText.equals(pc.get_autocode())) //오토입력을 대기중&&입력한코드와 오토코드가동일할때 //by사부 오토인증 추가
        {
            pc.sendPackets(new S_SystemMessage("오토 방지 코드가 인증되었습니다. "));
            pc.set_autook(0);
            pc.set_autogo(0);

        } else if (pc.get_autogo() == 1) { // 오토입력을 대기중 입력한코드와 오토코드가 불일치할때
            pc.sendPackets(new S_SystemMessage("오토 방지 코드 입력 실패! 코드:" + pc.get_autocode() + "를 다시입력해주세요. "));
            pc.set_autook(1);
            pc.set_autoct(pc.get_autoct() + 1);
            pc.set_autogo(1);
        } //by사부 오토인증 추가

        //if (pc.hasSkillEffect(L1SkillId.SILENCE)
        //	|| pc.hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
        //	|| pc.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
        if (pc.hasSkillEffect(SILENCE)
                || pc.hasSkillEffect(AREA_OF_SILENCE)
                || pc.hasSkillEffect(STATUS_POISON_SILENCE)) {
            return;
        }
        if (pc.hasSkillEffect(1005)) { // 채팅 금지중
            pc.sendPackets(new S_ServerMessage(242)); // 현재 채팅 금지중입니다.
            return;
        }

        if (chatType == 0) { // 통상 채팅
            if (pc.isGm() && chatText.startsWith(".") || pc.isMonitor() && chatText.startsWith(".")) {
                String cmd = chatText.substring(1);
                GMCommands.getInstance().handleCommands(pc, cmd);
                return;
            } else if (chatText.startsWith(".")) {
                String cmd = chatText.substring(1);
                UserCommands.getInstance().handleCommands(pc, cmd);
                return;
            }

            // 트레이드 채팅
            // 본래는 chatType==12가 될 것이지만, 줄머리의 것$이 송신되지 않는다
            if (chatText.startsWith("$")) {
                String text = chatText.substring(1);
                chatWorld(pc, text, 12);
                if (!pc.isGm()) {
                    pc.checkChatInterval();
                }
                return;
            }

////////////////////////주사위...

            L1Gambling gam = new L1Gambling();
            if (pc.isGambling()) {
                if (chatText.startsWith("홀")) {
                    gam.Gambling2(pc, chatText, 1);
                    return;
                } else if (chatText.startsWith("짝")) {
                    gam.Gambling2(pc, chatText, 2);
                    return;
                } else if (chatText.startsWith("1")) {
                    gam.Gambling2(pc, chatText, 3);
                    return;
                } else if (chatText.startsWith("2")) {
                    gam.Gambling2(pc, chatText, 4);
                    return;
                } else if (chatText.startsWith("3")) {
                    gam.Gambling2(pc, chatText, 5);
                    return;
                } else if (chatText.startsWith("4")) {
                    gam.Gambling2(pc, chatText, 6);
                    return;
                } else if (chatText.startsWith("5")) {
                    gam.Gambling2(pc, chatText, 7);
                    return;
                } else if (chatText.startsWith("6")) {
                    gam.Gambling2(pc, chatText, 8);
                    return;
                }
            }
            if (pc.isGambling3()) {
                L1Gambling3 gam1 = new L1Gambling3();
                if (chatText.startsWith("오크전사")) {
                    gam1.Gambling3(pc, chatText, 1);
                    return;
                } else if (chatText.startsWith("스파토이")) {
                    gam1.Gambling3(pc, chatText, 2);
                    return;
                } else if (chatText.startsWith("멧돼지")) {
                    gam1.Gambling3(pc, chatText, 3);
                    return;
                } else if (chatText.startsWith("슬라임")) {
                    gam1.Gambling3(pc, chatText, 4);
                    return;
                } else if (chatText.startsWith("해골")) {
                    gam1.Gambling3(pc, chatText, 5);
                    return;
                } else if (chatText.startsWith("늑대인간")) {
                    gam1.Gambling3(pc, chatText, 6);
                    return;
                } else if (chatText.startsWith("버그베어")) {
                    gam1.Gambling3(pc, chatText, 7);
                    return;
                } else if (chatText.startsWith("장로")) {
                    gam1.Gambling3(pc, chatText, 8);
                    return;
                } else if (chatText.startsWith("괴물눈")) {
                    gam1.Gambling3(pc, chatText, 9);
                    return;
                }
            }
            ///////////////////////////////

            ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
            S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                    Opcodes.S_OPCODE_NORMALCHAT, 0);
            if (!pc.getExcludingList().contains(pc.getName())) {
                pc.sendPackets(s_chatpacket);
            }
            for (L1PcInstance listner : L1World.getInstance()
                    .getRecognizePlayer(pc)) {
                if (!listner.getExcludingList().contains(pc.getName())) {
                    listner.sendPackets(s_chatpacket);
                }
            }
            // 돕펠 처리
            for (L1Object obj : pc.getKnownObjects()) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mob = (L1MonsterInstance) obj;
                    if (mob.getNpcTemplate().is_doppel()
                            && mob.getName().equals(pc.getName())) {
                        mob.broadcastPacket(new S_NpcChatPacket(mob, chatText,
                                0));
                    }
                }
            }
        } else if (chatType == 2) { // 절규
            if (pc.isGhost()) {
                return;
            }
            ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
            S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                    Opcodes.S_OPCODE_NORMALCHAT, 2);
            if (!pc.getExcludingList().contains(pc.getName())) {
                pc.sendPackets(s_chatpacket);
            }
            for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(
                    pc, 50)) {
                if (!listner.getExcludingList().contains(pc.getName())) {
                    listner.sendPackets(s_chatpacket);
                }
            }

            // 돕펠 처리
            for (L1Object obj : pc.getKnownObjects()) {
                if (obj instanceof L1MonsterInstance) {
                    L1MonsterInstance mob = (L1MonsterInstance) obj;
                    if (mob.getNpcTemplate().is_doppel()
                            && mob.getName().equals(pc.getName())) {
                        for (L1PcInstance listner : L1World.getInstance()
                                .getVisiblePlayer(mob, 50)) {
                            listner.sendPackets(new S_NpcChatPacket(mob,
                                    chatText, 2));
                        }
                    }
                }
            }
        } else if (chatType == 3) { // 전체 채팅
            chatWorld(pc, chatText, chatType);
        } else if (chatType == 4) { // 혈맹 채팅
            if (pc.getClanid() != 0) { // 크란 소속중
                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                int rank = pc.getClanRank();
                if (clan != null
                        && (rank == L1Clan.CLAN_RANK_PUBLIC
                        || rank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_PRINCE)) {
                    ChatLogTable.getInstance().storeChat(pc, null, chatText,
                            chatType);
                    S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                            Opcodes.S_OPCODE_GLOBALCHAT, 4);
                    L1PcInstance[] clanMembers = clan.getOnlineClanMember();
                    for (L1PcInstance listner : clanMembers) {
                        if (!listner.getExcludingList().contains(pc.getName())) {
                            listner.sendPackets(s_chatpacket);
                        }
                    }
                }
            }
        } else if (chatType == 11) { // 파티 채팅
            if (pc.isInParty()) { // 파티중
                ChatLogTable.getInstance().storeChat(pc, null, chatText,
                        chatType);
                S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                        Opcodes.S_OPCODE_GLOBALCHAT, 11);
                L1PcInstance[] partyMembers = pc.getParty().getMembers();
                for (L1PcInstance listner : partyMembers) {
                    if (!listner.getExcludingList().contains(pc.getName())) {
                        listner.sendPackets(s_chatpacket);
                    }
                }
            }
        } else if (chatType == 12) { // 트레이드 채팅
            chatWorld(pc, chatText, chatType);
        } else if (chatType == 13) { // 연합 채팅
            if (pc.getClanid() != 0) { // 크란 소속중
                L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
                int rank = pc.getClanRank();
                if (clan != null
                        && (rank == L1Clan.CLAN_RANK_GUARDIAN || rank == L1Clan.CLAN_RANK_PRINCE)) {
                    ChatLogTable.getInstance().storeChat(pc, null, chatText,
                            chatType);
                    S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                            Opcodes.S_OPCODE_GLOBALCHAT, 13);
                    L1PcInstance[] clanMembers = clan.getOnlineClanMember();
                    for (L1PcInstance listner : clanMembers) {
                        int listnerRank = listner.getClanRank();
                        if (!listner.getExcludingList().contains(pc.getName())
                                && (listnerRank == L1Clan.CLAN_RANK_GUARDIAN || listnerRank == L1Clan.CLAN_RANK_PRINCE)) {
                            listner.sendPackets(s_chatpacket);
                        }
                    }
                }
            }
        } else if (chatType == 14) { // 채팅 파티
            if (pc.isInChatParty()) { // 채팅 파티중
                ChatLogTable.getInstance().storeChat(pc, null, chatText,
                        chatType);
                S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
                        Opcodes.S_OPCODE_NORMALCHAT, 14);
                L1PcInstance[] partyMembers = pc.getChatParty().getMembers();
                for (L1PcInstance listner : partyMembers) {
                    if (!listner.getExcludingList().contains(pc.getName())) {
                        listner.sendPackets(s_chatpacket);
                    }
                }
            }
        }
        if (!pc.isGm()) {
            pc.checkChatInterval();
        }
    }

    private void chatWorld(L1PcInstance pc, String chatText, int chatType) {
        if (pc.isGm()) {
            ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
            L1World.getInstance().broadcastPacketToAll(
                    new S_ChatPacket(pc, chatText, Opcodes.S_OPCODE_GLOBALCHAT,
                            chatType));
        } else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
            if (L1World.getInstance().isWorldChatElabled()) {
				/*if (pc.get_food() >= 6) {
					pc.set_food(pc.get_food() - 5);*/ // 채창사용시에 고기게이지 줄어들지 않게 주석처리
                ChatLogTable.getInstance().storeChat(pc, null, chatText,
                        chatType);
                pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc
                        .get_food()));
                for (L1PcInstance listner : L1World.getInstance()
                        .getAllPlayers()) {
                    if (!listner.getExcludingList().contains(pc.getName())) {
                        if (listner.isShowTradeChat() && chatType == 12) {
                            listner.sendPackets(new S_ChatPacket(pc,
                                    chatText, Opcodes.S_OPCODE_GLOBALCHAT,
                                    chatType));
                        } else if (listner.isShowWorldChat()
                                && chatType == 3) {
                            listner.sendPackets(new S_ChatPacket(pc,
                                    chatText, Opcodes.S_OPCODE_GLOBALCHAT,
                                    chatType));
                        }
                    }
                }
                if (Config.글로벌 == true) { // <--서버매니저 전체채팅 출력 추가
                    l1j.server.Leaf.chatlog.append("\r\n[전체] " + pc.getName() + ": " + chatText); //<--서버매니저 전체채팅 출력
                }
			/*	} else {
					pc.sendPackets(new S_ServerMessage(462)); // \f1공복이기 때문에 채팅할 수 없습니다.
				}*/ // 채창사용시에 고기게이지 줄어들지 않게 주석처리
            } else {
                pc.sendPackets(new S_ServerMessage(510)); // 현재 월드 채팅은 정지중이 되고 있습니다.당분간의 사이 승낙해 주십시오.
            }
        } else {
            pc.sendPackets(new S_ServerMessage(195, String
                    .valueOf(Config.GLOBAL_CHAT_LEVEL))); // 레벨%0미만의 캐릭터는 채팅을 할 수 없습니다.
        }
    }

    @Override
    public String getType() {
        return C_CHAT;
    }
}
