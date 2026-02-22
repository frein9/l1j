/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;

import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import l1j.server.Config;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_SkillIconExp;
import l1j.server.server.Announcements;

public class AinTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(AinTimeController.class
			.getName());

	private static AinTimeController _instance;

	public static AinTimeController getInstance() {
		if (_instance == null) {
			_instance = new AinTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkAinTime();     // 추가
				Thread.sleep(60000);
			}
		} catch (Exception e1) {
		}
	}
	private Calendar getRealTime() {
		  TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		  Calendar cal = Calendar.getInstance(_tz);
		  return cal;
	}

	private void checkAinTime() {
		  SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		  int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));

		int ainTime = Config.RATE_AIN_TIME;
		int ainTime1 = Config.RATE_AIN_OUTTIME;
		
		if (nowtime % ainTime == 0) {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			 if (pc.getLevel() >= 49) {  // 추가
             if (pc.getAinPoint() < 200 && pc.getMap().isSafetyZone(pc.getLocation())) {  // 아인하사드의 축복 충전
				pc.setAinPoint(pc.getAinPoint() + 1);
				pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
			 }
			 } else {
				 return;
			 }
		}
		}
	 if (nowtime % ainTime1 == 0) {
		for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
			 if (pc.getLevel() >= 49) {  // 추가
             if (pc.getAinPoint() < 200 && pc.getMap().isSafetyZone(pc.getLocation())) {  // 아인하사드의 축복 충전
				pc.setAinPoint(pc.getAinPoint() + 1);
				pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
		    }
		} else {
			return;
		} 
	}
}
		}
		}
	


