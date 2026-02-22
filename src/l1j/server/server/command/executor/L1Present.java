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

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class L1Present implements L1CommandExecutor {
	private static Logger _log = Logger.getLogger(L1Present.class.getName());

	private L1Present() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Present();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			if (pc.getInventory().checkEquipped(300000)){   // 운영자의 반지 착용했을때 운영자 명령어 사용가능

			StringTokenizer st = new StringTokenizer(arg);
			String account = st.nextToken();
			int itemid = Integer.parseInt(st.nextToken(), 10);
			int enchant = Integer.parseInt(st.nextToken(), 10);
			int count = Integer.parseInt(st.nextToken(), 10);

			L1Item temp = ItemTable.getInstance(). getTemplate(itemid);
			if (temp == null) {
				pc.sendPackets(new S_SystemMessage("존재하지 않는 아이템 ID입니다. "));
				return;
			}

			L1DwarfInventory.present(account, itemid, enchant, count);
			pc.sendPackets(new S_SystemMessage(temp.getNameId() + "를" + count
					+ "개선물 했습니다. ", true));
			} else {
				pc.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
				return;
			}
		} catch (Exception e) {
			pc
					. sendPackets(new S_SystemMessage(
							".선물 계정명 아이템ID 인챈트수 아이템수로 입력해 주세요. (계정명=* 으로 모두)"));
		}
	}
}
