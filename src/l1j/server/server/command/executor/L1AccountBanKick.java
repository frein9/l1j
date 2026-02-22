/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.command.executor;

import java.util.logging.Logger;

import l1j.server.server.Account;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1AccountBanKick implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1AccountBanKick.class
			. getName());

	private L1AccountBanKick() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1AccountBanKick();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			L1PcInstance target = L1World.getInstance(). getPlayer(arg);
			if (pc.getInventory().checkEquipped(300000)){   // 운영자의 반지 착용했을때 운영자 명령어 사용가능
			if (target != null) {
				// 어카운트를 BAN 한다
				Account.ban(target.getAccountName());
				pc.sendPackets(new S_SystemMessage(target.getName()
						+ "님을 추방 했습니다. "));
				target.sendPackets(new S_Disconnect());
			} else {
				pc.sendPackets(new S_SystemMessage(
						"그러한 이름의 캐릭터는 월드내에는 존재하지 않습니다. "));
			}
			} else {
				pc.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
				return;
			}
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(cmdName + " 캐릭터명으로 입력해 주세요. "));
		}
	}
}
