/*
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

package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PrivateNpcShop;
import l1j.server.server.serverpackets.S_PrivateShop;
import l1j.server.server.templates.L1ShopItem;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_ShopList extends ClientBasePacket {

	private static final String C_SHOP_LIST = "[C] C_ShopList";
	private static Logger _log = Logger.getLogger(C_ShopList.class.getName());

	public C_ShopList(byte abyte0[], ClientThread clientthread) {
		super(abyte0);

		int type = readC();
		int objectId = readD();

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
		
		// npc 개인상점이 있으므로 오브젝트를 먼저 찾은후 개별 처리
		L1Object o = L1World.getInstance().findObject(objectId);
		// 그 시키가 인간이면
		if(o instanceof L1PcInstance){
			pc.sendPackets(new S_PrivateShop(pc, objectId, type));
		// npc 라면 
		}else if(o instanceof L1NpcInstance){
			L1NpcInstance n = (L1NpcInstance) o;
			// 개인상점 npc
			L1ShopItem shop = ShopTable.getInstance().getShop(n.getNpcTemplate().get_npcId());
			// 개인상점 npc 라면
			if(shop != null && shop.getMessage() != null){
				switch(type){
					// buy 
					case 0:
						pc.sendPackets(new S_PrivateNpcShop(n.getId(), pc));
						break;
					// sell
					case 1:
						pc.sendPackets(new S_PrivateShop(pc, objectId, type));
						break;
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_SHOP_LIST;
	}

}