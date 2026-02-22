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
package l1j.server.server.model;

import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WarTimeController;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PinkName;

// Referenced classes of package l1j.server.server.model:
// L1PinkName

public class L1PinkName {
	private static final Logger _log = Logger.getLogger(L1PinkName.class
			.getName());

	private L1PinkName() {
	}

	static class PinkNameTimer implements Runnable {
		private L1PcInstance _attacker = null;

		public PinkNameTimer(L1PcInstance attacker) {
			_attacker = attacker;
		}

		@Override
		public void run() {
			for (int i = 0; i < 30; i++) {
				try {
					Thread.sleep(10000);
				} catch (Exception exception) {
					break;
				}
				// 사망, 또는, 상대를 넘어뜨려 빨강 네임이 되면 종료
				if (_attacker.isDead()) {
					// setPinkName(false);는 L1PcInstance#death()로 실시한다
					break;
				}
				/*
				if (_attacker.getLawful() < 0) {
					_attacker.setPinkName(false);
					break;
				}
				*/
			}
			stopPinkName(_attacker);
		}

		private void stopPinkName(L1PcInstance attacker) {
			attacker.setPinkName(false); 
			attacker.sendPackets(new S_PinkName(attacker.getId(), 0));
			attacker.broadcastPacket(new S_PinkName(attacker.getId(), 0));
			attacker.setPinkName(false);
		}
	}

	public static void onAction(L1PcInstance pc, L1Character cha) {
		if (pc == null || cha == null) {
			return;
		}

		if (!(cha instanceof L1PcInstance)) {
			return;
		}
		L1PcInstance attacker = (L1PcInstance) cha;
		if (pc.getId() == attacker.getId()) {
			return;
		}

		if (attacker.getFightId() == pc.getId()) {
			return;
		}
		
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(pc);  
		if (castleId != 0) { 
			isNowWar = WarTimeController.getInstance().isNowWar(castleId); 
		}

		if (pc.getLawful() >= 0
//				&& // pc, attacker 모두 파랑 네임
//				!pc.isPinkName() && attacker.getLawful() >= 0 // 쌍방 보라돌이 처리위해 주석
				&& !attacker.isPinkName()) {
			if (pc.getZoneType() == 0 && // 모두 노멀 존에서, 전쟁 시간내에 기내가 아니다
					attacker.getZoneType() == 0 && isNowWar == false) {
				attacker.setPinkName(true);
				attacker.sendPackets(new S_PinkName(attacker.getId(), 30));
				if (!attacker.isGmInvis()) {
					attacker.broadcastPacket(new S_PinkName(attacker.getId(),
							30));
				}
				PinkNameTimer pink = new PinkNameTimer(attacker);
				GeneralThreadPool.getInstance().execute(pink);
			}
		}
	}
}
