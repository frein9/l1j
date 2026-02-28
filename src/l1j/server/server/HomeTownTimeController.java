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
package l1j.server.server;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1World;
import l1j.server.server.model.gametime.L1GameTime;
import l1j.server.server.model.gametime.L1GameTimeAdapter;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.utils.SQLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeTownTimeController {
    private static Logger _log = Logger.getLogger(HomeTownTimeController.class
            .getName());

    private static HomeTownTimeController _instance;
    private static L1TownFixedProcListener _listener;

    private HomeTownTimeController() {
        startListener();
    }

    public static HomeTownTimeController getInstance() {
        if (_instance == null) {
            _instance = new HomeTownTimeController();
        }
        return _instance;
    }

    private static String totalContribution(int townId) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        ResultSet rs1 = null;
        PreparedStatement pstm2 = null;
        ResultSet rs2 = null;
        PreparedStatement pstm3 = null;
        ResultSet rs3 = null;
        PreparedStatement pstm4 = null;
        PreparedStatement pstm5 = null;

        int leaderId = 0;
        String leaderName = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con
                    .prepareStatement("SELECT OBJID, CHAR_NAME FROM CHARACTERS WHERE HOMETOWNID = ?  ORDER BY CONTRIBUTION DESC");

            pstm1.setInt(1, townId);
            rs1 = pstm1.executeQuery();

            if (rs1.next()) {
                leaderId = rs1.getInt("objid");
                leaderName = rs1.getString("char_name");
            }

            double totalContribution = 0;
            pstm2 = con
                    .prepareStatement("SELECT SUM(CONTRIBUTION) AS TOTALCONTRIBUTION FROM CHARACTERS WHERE HOMETOWNID = ? ");
            pstm2.setInt(1, townId);
            rs2 = pstm2.executeQuery();
            if (rs2.next()) {
                totalContribution = rs2.getInt("TotalContribution");
            }

            double townFixTax = 0;
            pstm3 = con
                    .prepareStatement("SELECT TOWN_FIX_TAX FROM TOWN WHERE TOWN_ID = ? ");
            pstm3.setInt(1, townId);
            rs3 = pstm3.executeQuery();
            if (rs3.next()) {
                townFixTax = rs3.getInt("town_fix_tax");
            }

            double contributionUnit = 0;
            if (totalContribution != 0) {
                contributionUnit = Math.floor(townFixTax / totalContribution
                        * 100) / 100;
            }
            pstm4 = con
                    .prepareStatement("UPDATE CHARACTERS SET CONTRIBUTION = 0, PAY = CONTRIBUTION * ?  WHERE HOMETOWNID = ? ");
            pstm4.setDouble(1, contributionUnit);
            pstm4.setInt(2, townId);
            pstm4.execute();

            pstm5 = con
                    .prepareStatement("UPDATE TOWN SET LEADER_ID = ?, LEADER_NAME = ?, TAX_RATE = 0, TAX_RATE_RESERVED = 0, SALES_MONEY = 0, SALES_MONEY_YESTERDAY = SALES_MONEY, TOWN_TAX = 0, TOWN_FIX_TAX = 0 WHERE TOWN_ID = ? ");
            pstm5.setInt(1, leaderId);
            pstm5.setString(2, leaderName);
            pstm5.setInt(3, townId);
            pstm5.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs1);
            SQLUtil.close(rs2);
            SQLUtil.close(rs3);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(pstm3);
            SQLUtil.close(pstm4);
            SQLUtil.close(pstm5);
            SQLUtil.close(con);
        }

        return leaderName;
    }

    private static void clearHomeTownID() {
        Connection con = null;
        PreparedStatement pstm = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("UPDATE CHARACTERS SET HOMETOWNID = 0 WHERE HOMETOWNID = -1");
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    /**
     * 보수를 취득해 클리어 한다
     *
     * @return 보수
     */
    public static int getPay(int objid) {
        Connection con = null;
        PreparedStatement pstm1 = null;
        PreparedStatement pstm2 = null;
        ResultSet rs1 = null;
        int pay = 0;

		/*if (pay <= 0 || pay >= 3000000) {
			return pay;
		}*/

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm1 = con
                    .prepareStatement("SELECT PAY FROM CHARACTERS WHERE OBJID = ?  FOR UPDATE");

            pstm1.setInt(1, objid);
            rs1 = pstm1.executeQuery();

            if (rs1.next()) {
                pay = rs1.getInt("Pay");
            }

            pstm2 = con
                    .prepareStatement("UPDATE CHARACTERS SET PAY = 0 WHERE OBJID = ? ");
            pstm2.setInt(1, objid);
            pstm2.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(rs1);
            SQLUtil.close(pstm1);
            SQLUtil.close(pstm2);
            SQLUtil.close(con);
        }

        return pay;
    }

    private void startListener() {
        if (_listener == null) {
            _listener = new L1TownFixedProcListener();
            L1GameTimeClock.getInstance().addListener(_listener);
        }
    }

    private void fixedProc(L1GameTime time) {
        Calendar cal = time.getCalendar();
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (day == 25) {
            monthlyProc();
        } else {
            dailyProc();
        }
    }

    public void dailyProc() {
        _log.info("홈 타운 시스템：일시 처리 개시");
        TownTable.getInstance().updateTaxRate();
        TownTable.getInstance().updateSalesMoneyYesterday();
        TownTable.getInstance().load();
    }

    public void monthlyProc() {
        _log.info("홈 타운 시스템：월시 처리 개시");
        L1World.getInstance().setProcessingContributionTotal(true);
        Collection<L1PcInstance> players = L1World.getInstance()
                .getAllPlayers();
        for (L1PcInstance pc : players) {
            try {
                // DB에 캐릭터 정보를 기입한다
                pc.save();
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }

        for (int townId = 1; townId <= 10; townId++) {
            String leaderName = totalContribution(townId);
            if (leaderName != null) {
                S_PacketBox packet = new S_PacketBox(
                        S_PacketBox.MSG_TOWN_LEADER, leaderName);
                for (L1PcInstance pc : players) {
                    if (pc.getHomeTownId() == townId) {
                        pc.setContribution(0);
                        pc.sendPackets(packet);
                    }
                }
            }
        }
        TownTable.getInstance().load();

        for (L1PcInstance pc : players) {
            if (pc.getHomeTownId() == -1) {
                pc.setHomeTownId(0);
            }
            pc.setContribution(0);
            try {
                // DB에 캐릭터 정보를 기입한다
                pc.save();
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }
        clearHomeTownID();
        L1World.getInstance().setProcessingContributionTotal(false);
    }

    private class L1TownFixedProcListener extends L1GameTimeAdapter {
        @Override
        public void onDayChanged(L1GameTime time) {
            fixedProc(time);
        }
    }
}
