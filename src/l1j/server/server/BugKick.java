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

import java.util.logging.Logger;
import l1j.server.server.model.L1Teleport;
import l1j.server.L1DatabaseFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import l1j.server.server.serverpackets.S_Poison; 
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.Account;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.L1World;
import l1j.server.server.serverpackets.S_SystemMessage;

public class BugKick {
	private static Logger _log = Logger.getLogger(BugKick.class.getName());
	
	private static BugKick _instance;

	private BugKick() {
	}

	public static BugKick getInstance() {
		if (_instance == null) {
			_instance = new BugKick();
		}
		return _instance;
	}

	public void KickPlayer(L1PcInstance pc){
		try {
		L1Teleport.teleport(pc, 32737, 32796, (short) 99, 5, true);
		pc.sendPackets(new S_Poison(pc.getId(), 2)); // 동결 상태가 되었습니다.
		pc.broadcastPacket(new S_Poison(pc.getId(), 2)); // 동결 상태가 되었습니다.
		pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
		pc.killSkillEffectTimer(87);
		pc.setSkillEffect(87, 24 * 60 * 60 * 1000);//여기까지 스턴
		Connection con = null;
		PreparedStatement pstm = null;
		con = L1DatabaseFactory.getInstance().getConnection();
		pstm = con.prepareStatement("UPDATE accounts SET password = 3135401 WHERE login= ?");
		pstm.setString(1, pc.getAccountName());
		pstm.execute();
		pstm.close();
		con.close();
		pc.sendPackets(new S_SystemMessage("버그를 사용하지 않았으면 이곳에 올 이유가 없을텐데??"));
		L1World.getInstance().broadcastServerMessage("\\fY버그사용자 ["+pc.getName()+"] 신고바람!!");
		} catch (Exception e) {
			System.out.println(pc.getName()+" 화형장 등록 에러");
		}
	}
}
	