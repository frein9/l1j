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
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;

import java.util.logging.Logger;


// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Trade extends ClientBasePacket {

    private static final String C_TRADE = "[C] C_Trade";
    private static Logger _log = Logger.getLogger(C_Trade.class.getName());

    public C_Trade(byte abyte0[], ClientThread clientthread)
            throws Exception {
        super(abyte0);

        L1PcInstance player = clientthread.getActiveChar();
        if (player.isGhost()) {
            return;
        }
        // 월드맵상 내 계정과 같은 동일 한 계정을 가진 캐릭이 접속중이라면
        if (isTwoLogin(player)) return;

        ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
        if (player.getOnlineStatus() == 0) {
            clientthread.kick();
            return;
        }
        ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
        //** 2중 교환 버그 수정  **//		By도우너
        if (player.getTradeTarget() != null) {
            L1Trade trade = new L1Trade();
            trade.TradeCancel(player);
        }
        //** 2중 교환 버그 수정  **//		By도우너

        L1PcInstance target = FaceToFace.faceToFace(player);
        if (target.isTrade()) {
            player.sendPackets(new S_SystemMessage("상대가 다른상대방과 교환중입니다."));
            return;
        }
        /** 2케릭 교환 버그 수정 By 쿠우**/
        if (player.getAccountName().equalsIgnoreCase(target.getAccountName())) {
            L1World.getInstance().broadcastServerMessage("\\fY버그사용자 [" + player.getName() + "] 신고바람!!");
            return;
        }
        /** 2케릭 교환 버그 수정 By 쿠우**/

        if (target != null) {
            if (player.getLevel() > 4 && target.getLevel() > 4) {
                if (!target.isParalyzed()) {
                    player.setTradeID(target.getId()); // 상대의 오브젝트 ID를 보존해 둔다
                    target.setTradeID(player.getId());
                    target.sendPackets(new S_Message_YN(252, player.getName())); // %0%s가 당신과 아이템의 거래를 바라고 있습니다.거래합니까? (Y/N)
                    player.setTrade(true);
                    target.setTrade(true);
                    //** 2중 교환 버그 수정  **//		By도우너
                    player.setTradeTarget(target.getName());
                    //** 2중 교환 버그 수정  **//		By도우너
                }
            } else {
                player.sendPackets(new S_SystemMessage("레벨 5미만의 케릭터는 교환을 할 수 없습니다."));
                target.sendPackets(new S_SystemMessage("레벨 5미만의 케릭터는 교환을 할 수 없습니다."));
            }
        }
    }

    /**
     * 월드상에 있는 모든 캐릭의 계정을 비교해 같은 계정이 있다면 true 없다면 false
     *
     * @param c L1PcInstance
     * @return 있다면 true
     */
    private boolean isTwoLogin(L1PcInstance c) {
        boolean bool = false;
        for (L1PcInstance target : L1World.getInstance().getAllPlayers3()) {
            if (c.getId() != target.getId() && !target.isPrivateShop()) {
                if (c.getNetConnection().getAccountName().equalsIgnoreCase(target.getNetConnection().getAccountName())) {
                    bool = true;
                    break;
                }
            }
        }
        return bool;
    }

    @Override
    public String getType() {
        return C_TRADE;
    }
}
