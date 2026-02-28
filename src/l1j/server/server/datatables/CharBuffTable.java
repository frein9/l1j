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
package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.utils.SQLUtil;

import static l1j.server.server.model.skill.L1SkillId.*;

public class CharBuffTable {
    private CharBuffTable() {
    }

    private static Logger _log = Logger
            .getLogger(CharBuffTable.class.getName());

    private static final int[] buffSkill = {
            /** 포션 마법 버프 */
            STATUS_BRAVE, STATUS_HASTE, STATUS_BLUE_POTION,
            STATUS_CHAT_PROHIBITED, STATUS_RIBRAVE,
            /** 코마.상아.혈흔.마안 버프 */
            FAFU_MAAN, ANTA_MAAN, LIND_MAAN, VALA_MAAN, LIFE_MAAN, BIRTH_MAAN, SHAPE_MAAN,
            SANGA, SANGABUFF, COMA, COMABUFF, CRAY, ANTA_BLOOD,
            /** 일반 마법 버프 */
            LIGHT, SHIELD, PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, HASTE, HOLY_WALK,
            GREATER_HASTE, SHAPE_CHANGE, DECREASE_WEIGHT, DECAY_POTION,
            SILENCE, WEAKNESS, DISEASE, BERSERKERS,
            /** 군주 관련 버프 */
            GLOWING_AURA, SHINING_AURA, BRAVE_AURA,
            /** 다크 엘프 관련 버프 */
            SHADOW_ARMOR, MOVING_ACCELERATION, DRESS_EVASION, BLOODLUST, VENOM_RESIST,
            DRESS_MIGHTY, DRESS_DEXTERITY,
            /** 용기사 환술사 관련 버프 */
            CONSENTRATION, INSIGHT, PANIC, PAYTIONS,
            GUARDBREAK, DRAGON_SKIN, MOTALBODY, HOUROFDEATH, PEAR,
            /** 요정 정령 마법 관련 버프 */
            FIRE_WEAPON, NATURES_TOUCH, WIND_SHACKLE, ERASE_MAGIC, ELEMENTAL_FALL_DOWN,
            ADDITIONAL_FIRE, STRIKER_GALE, ELEMENTAL_FIRE, SOUL_OF_FLAME, POLLUTE_WATER, WIND_SHOT,
            RESIST_MAGIC, CLEAR_MIND, RESIST_ELEMENTAL, ELEMENTAL_PROTECTION, WIND_WALK, EARTH_SKIN,
            FIRE_BLESS, STORM_EYE, EARTH_BLESS, BURNING_WEAPON, STORM_SHOT, IRON_SKIN,
            /** 컬러풀 패키지 버프 */
            EXP_POTION, COLOR_A, COLOR_B, COLOR_C,
            /** 운세 버프 */
            LUCK_A, LUCK_B, LUCK_C, LUCK_D,
            /** 요리 버프 */
            COOKING_1_0_N, COOKING_1_0_S, COOKING_1_1_N, COOKING_1_1_S,
            COOKING_1_2_N, COOKING_1_2_S, COOKING_1_3_N, COOKING_1_3_S,
            COOKING_1_4_N, COOKING_1_4_S, COOKING_1_5_N, COOKING_1_5_S,
            COOKING_1_6_N, COOKING_1_6_S, COOKING_1_8_N, COOKING_1_8_S,
            COOKING_1_9_N, COOKING_1_9_S, COOKING_1_10_N, COOKING_1_10_S,
            COOKING_1_11_N, COOKING_1_11_S, COOKING_1_12_N, COOKING_1_12_S,
            COOKING_1_13_N, COOKING_1_13_S, COOKING_1_14_N, COOKING_1_14_S,
            COOKING_1_16_N, COOKING_1_16_S, COOKING_1_17_N, COOKING_1_17_S,
            COOKING_1_18_N, COOKING_1_18_S, COOKING_1_19_N, COOKING_1_19_S,
            COOKING_1_20_N, COOKING_1_20_S, COOKING_1_21_N, COOKING_1_21_S,
            COOKING_1_22_N, COOKING_1_22_S};

    private static void StoreBuff(int objId, int skillId, int time, int polyId) {
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("INSERT INTO CHARACTER_BUFF SET char_obj_id=?, skill_id=?, remaining_time=?, poly_id=? ");
            pstm.setInt(1, objId);
            pstm.setInt(2, skillId);
            pstm.setInt(3, time);
            pstm.setInt(4, polyId);
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    }

    public static void DeleteBuff(L1PcInstance pc) {
        java.sql.Connection con = null;
        PreparedStatement pstm = null;
        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con
                    .prepareStatement("DELETE FROM CHARACTER_BUFF WHERE CHAR_OBJ_ID=? ");
            pstm.setInt(1, pc.getId());
            pstm.execute();
        } catch (SQLException e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
        } finally {
            SQLUtil.close(pstm);
            SQLUtil.close(con);

        }
    }

    public static void SaveBuff(L1PcInstance pc) {
        for (int skillId : buffSkill) {
            int timeSec = pc.getSkillEffectTimeSec(skillId);
            if (0 < timeSec) {
                int polyId = 0;
                if (skillId == SHAPE_CHANGE) {
                    polyId = pc.getTempCharGfx();
                }
                StoreBuff(pc.getId(), skillId, timeSec, polyId);
            }
        }
    }

}
