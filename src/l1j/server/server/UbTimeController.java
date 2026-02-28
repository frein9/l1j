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

import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1UltimateBattle;

import java.util.logging.Logger;


public class UbTimeController implements Runnable {
    private static Logger _log = Logger.getLogger(UbTimeController.class
            .getName());

    private static UbTimeController _instance;

    public static UbTimeController getInstance() {
        if (_instance == null) {
            _instance = new UbTimeController();
        }
        return _instance;
    }

    @Override
    public void run() {
        try {
            while (true) {
                checkUbTime(); // UB개시 시간을 체크
                Thread.sleep(15000);
            }
        } catch (Exception e1) {
            _log.warning(e1.getMessage());
        }
    }

    private void checkUbTime() {
        for (L1UltimateBattle ub : UBTable.getInstance().getAllUb()) {
            if (ub.checkUbTime() && !ub.isActive()) {
                ub.start(); // UB개시
                switch (ub.getUbId()) {
                    case 1: // 기란
                        Announcements.getInstance().announceToAll("[******] 잠시후 기란 무한대전이 시작됩니다.");
                        Announcements.getInstance().announceToAll("참가를 원하시는 분들은 지금 입장하여 주십시오.");
                        break;
                    case 2: // 웰던
                        Announcements.getInstance().announceToAll("[******] 잠시후 웰던 무한대전이 시작됩니다.");
                        Announcements.getInstance().announceToAll("참가를 원하시는 분들은 지금 입장하여 주십시오.");
                        break;
                    case 3: // 글말
                        Announcements.getInstance().announceToAll("[******] 잠시후 글루딘 무한대전이 시작됩니다.");
                        Announcements.getInstance().announceToAll("참가를 원하시는 분들은 지금 입장하여 주십시오.");
                        break;
                    case 4: // 말섬
                        Announcements.getInstance().announceToAll("[******] 잠시후 말하는섬 무한대전이 시작됩니다.");
                        Announcements.getInstance().announceToAll("참가를 원하시는 분들은 지금 입장하여 주십시오.");
                        break;
                    case 5: // 은말
                        Announcements.getInstance().announceToAll("[******] 잠시후 은기사 마을 무한대전이 시작됩니다.");
                        Announcements.getInstance().announceToAll("참가를 원하시는 분들은 지금 입장하여 주십시오.");
                        break;
                }
            }
        }
    }
}
