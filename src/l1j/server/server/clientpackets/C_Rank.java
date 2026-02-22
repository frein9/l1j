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

import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Rank extends ClientBasePacket {

	private static final String C_RANK = "[C] C_Rank";
	private static Logger _log = Logger.getLogger(C_Rank.class.getName());

	public C_Rank(byte abyte0[], ClientThread clientthread)
			throws Exception {
		super(abyte0);

		int type = readC(); // 타입
		// 임시방편으로 동맹 보기가 아니면 그대로.
		if(type != 2){
			int rank = readC();
			String name = readS();

			L1PcInstance pc = clientthread.getActiveChar();
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);

			if (pc == null) {
				return;
			}

			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan == null) {
				return;
			}

			if (rank < 1 && 3 < rank) {
				// 랭크를 변경하는 사람의 이름과 랭크를 입력해 주세요.[랭크=가디안, 일반, 견습]
				pc.sendPackets(new S_ServerMessage(781));
				return;
			}

			if (pc.isCrown()) { // 군주
				if (pc.getId() != clan.getLeaderId()) { // 혈맹주
					pc.sendPackets(new S_ServerMessage(785)); // 당신은 이제 군주가 아닙니다.
					return;
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을 이용할 수 있습니다.
				return;
			}

			if (targetPc != null) { // 온라인중
				if (pc.getClanid() == targetPc.getClanid()) { // 같은 크란
					try {
						targetPc.setClanRank(rank);
						targetPc.save(); // DB에 캐릭터 정보를 기입한다
						String rankString = "$772";
						if (rank == L1Clan.CLAN_RANK_PROBATION) {
							rankString = "$774";
						} else if (rank == L1Clan.CLAN_RANK_PUBLIC) {
							rankString = "$773";
						} else if (rank == L1Clan.CLAN_RANK_GUARDIAN) {
							rankString = "$772";
						}
						targetPc.sendPackets(new S_ServerMessage(784, rankString)); // 당신의 랭크가%s로 변경되었습니다.
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(414)); // 같은 혈맹원이 아닙니다.
					return;
				}
			} else { // 오프 라인중
				L1PcInstance restorePc = CharacterTable.getInstance()
				.restoreCharacter(name);
				if (restorePc != null
						&& restorePc.getClanid() == pc.getClanid()) { // 같은 크란
					try {
						restorePc.setClanRank(rank);
						restorePc.save(); // DB에 캐릭터 정보를 기입한다
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(109, name)); // %0라는 이름의 사람은 없습니다.
					return;
				}
			}
		}else return;
	}

	@Override
	public String getType() {
		return C_RANK;
	}
}
