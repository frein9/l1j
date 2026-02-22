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

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.UBSpawnTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.ActionCodes;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.serverpackets.S_GameStart;
import l1j.server.server.serverpackets.S_GameTime1;
import l1j.server.server.serverpackets.S_GameOver;
import l1j.server.server.serverpackets.S_GameEnd;


// Referenced classes of package l1j.server.server.model:
// L1UltimateBattle

public class L1PetMember {
	private int _locX;
	private int _locY;
	private short _mapId;

	private int _petId;
	private boolean _isNowPet;

	private static final Logger _log = Logger.getLogger(L1PetMember.class
			.getName());

	private final ArrayList<L1PcInstance> _members = new ArrayList<L1PcInstance>();

	/**
	 * constructor　 　.
	 */
	public L1PetMember() {
	}

	class PetThread implements Runnable {

		/**
		 * thread 프로시저.
		 */
		@Override
		public void run() {
			try {
				Thread.sleep(10000);
				
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 플레이어를 참가 멤버 리스트에 추가한다.
	 * 
	 * @param pc
	 *            새롭게 참가하는 플레이어
	 */
	public void addMember(L1PcInstance pc) {
		if (!_members.contains(pc)) {
			_members.add(pc);
		}
	}
	

	/**
	 * 플레이어를 참가 멤버 리스트로부터 삭제한다.
	 * 
	 * @param pc
	 *            삭제하는 플레이어
	 */
	public void removeMember(L1PcInstance pc) {
		_members.remove(pc);
	}


	/**
	 * 참가 멤버 리스트를 클리어 한다.
	 */
	public void clearMembers() {
		_members.clear();
	}

	/**
	 * 플레이어가, 참가 멤버인지를 돌려준다.
	 * 
	 * @param pc
	 *            조사하는 플레이어
	 * @return 참가 멤버이면 true, 그렇지 않으면 false.
	 */
	public boolean isMember(L1PcInstance pc) {
		return _members.contains(pc);
	}


	/**
	 * 참가 멤버의 배열을 작성해, 돌려준다.
	 * 
	 * @return 참가 멤버의 배열
	 */
	public L1PcInstance[] getMembersArray() {
		return _members.toArray(new L1PcInstance[_members.size()]);
	}

	/**
	 * 참가 멤버수를 돌려준다.
	 * 
	 * @return 참가 멤버수
	 */
	public int getMembersCount() {
		return _members.size();
	}



	/**
	 * UB중인지를 설정한다.
	 * 
	 * @param i
	 *            true/false
	 */
	private void setNowPet(boolean i) {
		_isNowPet = i;
	}

	/**
	 * 경기 중인지를 돌려준다.
	 * 
	 * @return 경기 중이면 true, 그렇지 않으면 false.
	 */
	public boolean isNowPet() {
		return _isNowPet;
	}

}
