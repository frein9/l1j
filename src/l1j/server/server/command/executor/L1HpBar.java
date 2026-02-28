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

import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_SystemMessage;

import java.util.logging.Logger;

public class L1HpBar implements L1CommandExecutor {
    private static Logger _log = Logger.getLogger(L1HpBar.class.getName());

    private L1HpBar() {
    }

    public static L1CommandExecutor getInstance() {
        return new L1HpBar();
    }

    @Override
    public void execute(L1PcInstance pc, String cmdName, String arg) {
        if (pc.getInventory().checkEquipped(300000)) {   // 운영자의 반지 착용했을때 운영자 명령어 사용가능
            if (arg.equalsIgnoreCase("on")) {
                pc.setSkillEffect(L1SkillId.GMSTATUS_HPBAR, 0);
            } else if (arg.equalsIgnoreCase("off")) {
                pc.removeSkillEffect(L1SkillId.GMSTATUS_HPBAR);

                for (L1Object obj : pc.getKnownObjects()) {
                    if (isHpBarTarget(obj)) {
                        pc.sendPackets(new S_HPMeter(obj.getId(), 0xFF));
                    }
                }
            } else {
                pc.sendPackets(new S_SystemMessage(cmdName + " on 또는 off 라고 입력해 주세요."));
            }
        } else {
            pc.sendPackets(new S_SystemMessage("당신은 운영자가 될 조건이 되지 않습니다."));
            return;
        }
    }

    public static boolean isHpBarTarget(L1Object obj) {
        if (obj instanceof L1MonsterInstance) {
            return true;
        }
        if (obj instanceof L1PcInstance) {
            return true;
        }
        if (obj instanceof L1SummonInstance) {
            return true;
        }
        if (obj instanceof L1PetInstance) {
            return true;
        }
        return false;
    }
}
