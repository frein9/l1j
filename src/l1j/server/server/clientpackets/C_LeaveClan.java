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

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_CharTitle;
import l1j.server.server.serverpackets.S_ServerMessage;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_LeaveClan extends ClientBasePacket {

    private static final String C_LEAVE_CLAN = "[C] C_LeaveClan";
    private static Logger _log = Logger.getLogger(C_LeaveClan.class.getName());

    public C_LeaveClan(byte abyte0[], ClientThread clientthread) throws Exception {
        super(abyte0);

        L1PcInstance player = clientthread.getActiveChar();
        String player_name = player.getName();
        String clan_name = player.getClanname();
        int clan_id = player.getClanid();
        if (clan_id == 0) {// 크란미소속
            return;
        }
        L1Clan clan = L1World.getInstance().getClan(clan_name);
        if (clan != null) {
            String clan_member_name[] = clan.getAllMembers();
            int i;
            if (player.isCrown() && player.getId() == clan.getLeaderId()) { // 프린스 또는 프린세스, 한편, 혈맹주
                int castleId = clan.getCastleId();
                int houseId = clan.getHouseId();
                if (castleId != 0 || houseId != 0) {
                    player.sendPackets(new S_ServerMessage(665)); // \f1성이나 아지트를 소유한 상태로 혈맹을 해산할 수 없습니다.
                    return;
                }
                for (L1War war : L1World.getInstance().getWarList()) {
                    if (war.CheckClanInWar(clan_name)) {
                        player.sendPackets(new S_ServerMessage(302)); // \f1해산시킬 수가 없습니다.
                        return;
                    }
                }
                for (i = 0; i < clan_member_name.length; i++) { // 크란원 크란 정보를 클리어
                    L1PcInstance online_pc = L1World.getInstance().getPlayer(clan_member_name[i]);
                    if (online_pc != null) { // 온라인중의 클랜원
                        online_pc.setClanid(0);
                        online_pc.setClanname("");
                        online_pc.setClanRank(0);
                        online_pc.setTitle("");
                        online_pc.sendPackets(new S_CharTitle(online_pc.getId(), ""));
                        online_pc.broadcastPacket(new S_CharTitle(online_pc.getId(), ""));
                        online_pc.save(); // DB에 캐릭터 정보를 기입한다
                        online_pc.sendPackets(new S_ServerMessage(269, player_name, clan_name)); // %1혈맹의 혈맹주%0가 혈맹을 해산시켰습니다.
                    } else { // 오프 라인중의 크란원
                        try {
                            L1PcInstance offline_pc = CharacterTable.getInstance().restoreCharacter(clan_member_name[i]);
                            offline_pc.setClanid(0);
                            offline_pc.setClanname("");
                            offline_pc.setClanRank(0);
                            offline_pc.setTitle("");
                            offline_pc.save(); // DB에 캐릭터 정보를 기입한다
                        } catch (Exception e) {
                            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                        }
                    }
                }
                String emblem_file = String.valueOf(clan_id);
                File file = new File("emblem/" + emblem_file);
                file.delete();
                ClanTable.getInstance().deleteClan(clan_name);
            } else { // 혈맹주 이외
                L1PcInstance clanMember[] = clan.getOnlineClanMember();
                for (i = 0; i < clanMember.length; i++) {
                    clanMember[i].sendPackets(new S_ServerMessage(178, player_name, clan_name)); // \f1%0이%1혈맹을 탈퇴했습니다.
                }
                if (clan.getWarehouseUsingChar() // 자캐릭터가 크란 창고 사용중
                        == player.getId()) {
                    clan.setWarehouseUsingChar(0); // 크란 창고의 락을 해제
                }
                player.setClanid(0);
                player.setClanname("");
                player.setClanRank(0);
                player.setTitle("");
                player.sendPackets(new S_CharTitle(player.getId(), ""));
                player.broadcastPacket(new S_CharTitle(player.getId(), ""));
                player.save(); // DB에 캐릭터 정보를 기입한다
                clan.delMemberName(player_name);
            }
        } else {
            player.setClanid(0);
            player.setClanname("");
            player.setClanRank(0);
            player.setTitle("");
            player.sendPackets(new S_CharTitle(player.getId(), ""));
            player.broadcastPacket(new S_CharTitle(player.getId(), ""));
            player.save(); // DB에 캐릭터 정보를 기입한다
            player.sendPackets(new S_ServerMessage(178, player_name, clan_name)); // \f1%0이%1혈맹을 탈퇴했습니다.
        }
    }

    @Override
    public String getType() {
        return C_LEAVE_CLAN;
    }

}
