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
package l1j.server.server.command.executor;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.util.logging.Logger;

public class L1Describe implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1Describe.class.getName());

    private L1Describe() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1Describe();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        try {
            if (pc.getInventory().checkEquipped(300000)) {   // 운영자의 반지 착용했을때 운영자 명령어 사용가능
                StringBuilder msg = new StringBuilder();
                pc.sendPackets(new S_SystemMessage("-- describe: " + pc.getName()
                        + " --"));
                int hpr = pc.getHpr() + pc.getInventory().hpRegenPerTick();
                int mpr = pc.getMpr() + pc.getInventory().mpRegenPerTick();
                msg.append("Dmg: +" + pc.getDmgup() + " / ");
                msg.append("Hit: +" + pc.getHitup() + " / ");
                msg.append("MR: " + pc.getMr() + " / ");
                msg.append("HPR: " + hpr + " / ");
                msg.append("MPR: " + mpr + " / ");
                msg.append("Karma: " + pc.getKarma() + " / ");
                msg.append("Item: " + pc.getInventory().getSize() + " / ");
                pc.sendPackets(new S_SystemMessage(msg.toString()));
            } else {
                pc.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
                return;
            }
        } catch (Exception e) {
            pc.sendPackets(new S_SystemMessage(cmdName + " 커멘드 에러"));
        }
    }
}
