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
package l1j.server.server.model.poison;

import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_ServerMessage;

public abstract class L1Poison {
	protected static boolean isValidTarget(L1Character cha) {
		if (cha == null) {
			return false;
		}
		// 독은 중복 하지 않는다
		if (cha.getPoison() != null) {
			return false;
		}

		if (!(cha instanceof L1PcInstance)) {
			return true;
		}

		L1PcInstance player = (L1PcInstance) cha;
		// 제니스링 장비중, 바포멧트아마 장비중 , 베놈레지스트중
		if (player.getInventory().checkEquipped(20298)
				|| player.getInventory().checkEquipped(20117)
			    || player.getInventory().checkEquipped(20700)  // 안타 인내력
                || player.getInventory().checkEquipped(20704) // 안타 완력
                || player.getInventory().checkEquipped(20708) // 안타 예지력
                || player.getInventory().checkEquipped(20712) // 안타 마력
                || player.hasSkillEffect(7678)		// 생명의 마안
				|| player.hasSkillEffect(104)) {
			return false;
		}
		return true;
	}

	// 미묘···솔직하게 sendPackets를 L1Character에 끌어올려야할 것인가도 모른다
	protected static void sendMessageIfPlayer(L1Character cha, int msgId) {
		if (!(cha instanceof L1PcInstance)) {
			return;
		}

		L1PcInstance player = (L1PcInstance) cha;
		player.sendPackets(new S_ServerMessage(msgId));
	}

	/**
	 * 이 독의 효과 ID를 돌려준다.
	 * 
	 * @see S_Poison#S_Poison(int, int)
	 * 
	 * @return S_Poison로 사용되는 효과 ID
	 */
	public abstract int getEffectId();

	/**
	 * 이 독의 효과를 없앤다.<br>
	 * 
	 * @see L1Character#curePoison()
	 */
	public abstract void cure();
}
