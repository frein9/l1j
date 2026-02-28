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

package l1j.server.server.model.Instance;

import l1j.server.server.GameServerSetting;
import l1j.server.server.serverpackets.S_Board;
import l1j.server.server.serverpackets.S_BoardRead;
import l1j.server.server.serverpackets.S_ChoBoard;
import l1j.server.server.serverpackets.S_ChoBoardRead;
import l1j.server.server.serverpackets.S_EnchantRanking;
import l1j.server.server.serverpackets.S_Ranking;
import l1j.server.server.serverpackets.S_Ranking2;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

import java.util.logging.Logger;

public class L1BoardInstance extends L1NpcInstance {
    /**
     *
     */
    private GameServerSetting _GameServerSetting;
    private static final long serialVersionUID = 1L;
    private static Logger _log = Logger.getLogger(L1BoardInstance.class
            .getName());

    public L1BoardInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        if (this.getNpcTemplate().get_npcId() == 200021) {//버그베어 승률 게시판 //빨강것만 추가.
            _GameServerSetting = GameServerSetting.getInstance();
            if (_GameServerSetting.getInstance().버경 == 0) { //표판매중
                player.sendPackets(new S_Board(this, true));
            } else if (_GameServerSetting.getInstance().버경 == 1) { //경기중
                player.sendPackets(new S_SystemMessage("경기 중에는 보실 수 없습니다."));
            } else if (_GameServerSetting.getInstance().버경 == 2) { //다음경기준비중
                player.sendPackets(new S_SystemMessage("다음 경기를 준비 중 입니다."));
            }
        } else {
            player.sendPackets(new S_Board(this));
        }
    }

    public void onAction(L1PcInstance player, int number) {
        player.sendPackets(new S_Board(this, number));
    }

    public void onActionRead(L1PcInstance player, int number) {
        player.sendPackets(new S_BoardRead(number));
    }

    public void onRanking(L1PcInstance player) {
        player.sendPackets(new S_Ranking(this));
    }

    public void onRankingRead(L1PcInstance player, int number) {
        player.sendPackets(new S_Ranking(player, number));
    }

    public void onRanking2(L1PcInstance player) {
        player.sendPackets(new S_Ranking2(this));
    }

    public void onRankingRead2(L1PcInstance player, int number) {
        player.sendPackets(new S_Ranking2(player, number));
    }

    public void onEnchantRanking(L1PcInstance player) {
        player.sendPackets(new S_EnchantRanking(this));
    }

    public void onEnchantRankingRead(L1PcInstance player, int number) {
        player.sendPackets(new S_EnchantRanking(player, number));
    }

    public void onreadChobo(L1PcInstance player, int readD) {
        player.sendPackets(new S_ChoBoardRead(readD));
    }

    public void onChoboList(L1PcInstance pc) {
        pc.sendPackets(new S_ChoBoard(this));
    }
}
