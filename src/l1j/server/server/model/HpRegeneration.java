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

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.model.Instance.L1ItemInstance;  //상단에 임포트
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.types.Point;

public class HpRegeneration extends TimerTask {

	private static Logger _log = Logger.getLogger(HpRegeneration.class
			.getName());

	private final L1PcInstance _pc;

	private int _regenMax = 0;

	private int _regenPoint = 0;

	private int _curPoint = 4;

	private static Random _random = new Random();

	public HpRegeneration(L1PcInstance pc) {
		_pc = pc;

		updateLevel();
	}

	public void setState(int state) {
		if (_curPoint < state) {
			return;
		}

		_curPoint = state;
	}

	@Override
	public void run() {
		try {
			if (_pc.isDead()) {
				return;
			}

			_regenPoint += _curPoint;
			_curPoint = 4;

			synchronized (this) {
				if (_regenMax <= _regenPoint) {
					_regenPoint = 0;
					regenHp();
				}
			}
		} catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}

	public void updateLevel() {
		final int lvlTable[] = new int[] { 30, 25, 20, 16, 14, 12, 11, 10, 9,
				3, 2 };

		int regenLvl = Math.min(10, _pc.getLevel());
		if (30 <= _pc.getLevel() && _pc.isKnight()) {
			regenLvl = 11;
		}

		synchronized (this) {
			_regenMax = lvlTable[regenLvl - 1] * 4;
		}
	}

	public void regenHp() {
		if (_pc.isDead()) {
			return;
		}

		int maxBonus = 1;
		// CON 보너스
		if (11 < _pc.getLevel() && 14 <= _pc.getCon()) {
			maxBonus = _pc.getCon() - 12;
			if (25 < _pc.getCon()) {
				maxBonus = 14;
			}
		}

		int equipHpr = _pc.getInventory().hpRegenPerTick();
		equipHpr += _pc.getHpr();
		int bonus = _random.nextInt(maxBonus) + 1;

		if (_pc.hasSkillEffect(L1SkillId.NATURES_TOUCH)) {
			bonus += 15;
		}
		if (L1HouseLocation.isInHouse(_pc.getX(), _pc.getY(), _pc.getMapId())) {
			bonus += 5;
		}
		if (_pc.getMapId() == 16384 || _pc.getMapId() == 16896
				|| _pc.getMapId() == 17408 || _pc.getMapId() == 17920
				|| _pc.getMapId() == 18432 || _pc.getMapId() == 18944
				|| _pc.getMapId() == 19968 || _pc.getMapId() == 19456
				|| _pc.getMapId() == 20480 || _pc.getMapId() == 20992
				|| _pc.getMapId() == 21504 || _pc.getMapId() == 22016
				|| _pc.getMapId() == 22528 || _pc.getMapId() == 23040
				|| _pc.getMapId() == 23552 || _pc.getMapId() == 24064
				|| _pc.getMapId() == 24576 || _pc.getMapId() == 25088) { // 여인숙
			bonus += 5;
		}
		if ((_pc.getLocation(). isInScreen(new Point(33055,32336))
				&& _pc.getMapId() == 4 && _pc.isElf())) {
			bonus += 5;
		}
 		if (_pc.hasSkillEffect(L1SkillId.COOKING_1_5_N)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_1_5_S)) {
			bonus += 3;
		} 		
 		if (_pc.hasSkillEffect(L1SkillId.COOKING_2_4_N)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_2_4_S)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_3_6_N)
				|| _pc.hasSkillEffect(L1SkillId.COOKING_3_6_S)) {
			bonus += 2;
		}
 		if (_pc.getOriginalHpr() > 0) { // 오리지날 CON HPR 보정
 			bonus += _pc.getOriginalHpr();
 		}
		boolean inLifeStream = false;
		if (isPlayerInLifeStream(_pc)) {
			inLifeStream = true;
			// 고대의 공간, 마족의 신전에서는 HPR+3은 없어져?
			bonus += 3;
		}

		// 공복과 중량의 체크
		if (_pc.get_food() < 24 || isOverWeight(_pc)
				|| _pc.hasSkillEffect(L1SkillId.BERSERKERS)) {
			bonus = 0;
			// 장비에 의한 HPR 증가는 만복도, 중량에 의해 없어지지만, 감소인 경우는 만복도, 중량에 관계없이 효과가 남는다
			if (equipHpr > 0) {
				equipHpr = 0;
			}
		}

		int newHp = _pc.getCurrentHp();
		newHp += bonus + equipHpr;

		if (newHp < 1) {
			newHp = 1; // HPR 감소 장비에 의해 사망은 하지 않는다
		}
		// 수중에서의 감소 처리
		// 라이프 시냇물로 감소를 없앨 수 있을까 불명
		if (isUnderwater(_pc)) {
			newHp -= 20;
			if (newHp < 1) {
				if (_pc.isGm() && _pc.getInventory().checkEquipped(300000)) {
					newHp = 1;
				} else {
					_pc.death(null); // 질식에 의해 HP가 0이 되었을 경우는 사망한다.
				}
			}
		}
		// Lv50 퀘스트의 고대의 공간 1 F2F에서의 감소 처리
		if (isLv50Quest(_pc) && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm() && _pc.getInventory().checkEquipped(300000)) {
					newHp = 1;
				} else {
					_pc.death(null); // HP가 0이 되었을 경우는 사망한다.
				}
			}
		}
		// 마족의 신전에서의 감소 처리
		if (_pc.getMapId() == 410 && !inLifeStream) {
			newHp -= 10;
			if (newHp < 1) {
				if (_pc.isGm() && _pc.getInventory().checkEquipped(300000)) {
					newHp = 1;
				} else {
					_pc.death(null); // HP가 0이 되었을 경우는 사망한다.
				}
			}
		}

		if (!_pc.isDead()) {
			_pc.setCurrentHp(Math.min(newHp, _pc.getMaxHp()));
		}
	}

	private boolean isUnderwater(L1PcInstance pc) {
		// 워터 부츠 장비시인가, 에바의 축복 상태, 수리된 장비 세트이면 수중은 아니면 간주한다.
		if (pc.getInventory().checkEquipped(20207)) {
			return false;
		}
		if (pc.hasSkillEffect(L1SkillId.STATUS_UNDERWATER_BREATH)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(21048)
				&& pc.getInventory().checkEquipped(21049)
				&& pc.getInventory().checkEquipped(21050)) {
			return false;
		}

		return pc.getMap().isUnderwater();
	}

	private boolean isOverWeight(L1PcInstance pc) {
		// 에키조틱크바이타라이즈 상태, 아디쇼나르파이아 상태인가
		// 골든 윙 장비시이면, 중량 오버이지 않으면 간주한다.
		if (pc.hasSkillEffect(L1SkillId.EXOTIC_VITALIZE)
				|| _pc.getMapId() == 16384 || _pc.getMapId() == 17408 || _pc.getMapId() == 18432  
				|| _pc.getMapId() == 20480 || _pc.getMapId() == 21504 || _pc.getMapId() == 22528
				|| _pc.getMapId() == 24576 
				|| pc.hasSkillEffect(L1SkillId.ADDITIONAL_FIRE)) {
			return false;
		}
		if (pc.getInventory().checkEquipped(20049)) {
			return false;
		}

		//return (119 < pc.getInventory().getWeight30()) ?  true : false;
		return (120 <= pc.getInventory().getWeight240()) ? true : false;
	}

	private boolean isLv50Quest(L1PcInstance pc) {
		int mapId = pc.getMapId();
		return (mapId == 2000 || mapId == 2001) ?  true : false;
	}

	/**
	 * 지정한 PC가 라이프 시냇물의 범위내에 있는지 체크한다
	 * 
	 * @param pc
	 *            PC
	 * @return true PC가 라이프 시냇물의 범위내에 있는 경우
	 */
	private static boolean isPlayerInLifeStream(L1PcInstance pc) {
		for (L1Object object : pc.getKnownObjects()) {
			if (object instanceof L1EffectInstance == false) {
				continue;
			}
			L1EffectInstance effect = (L1EffectInstance) object;
			if (effect.getNpcId() == 81169 && effect.getLocation()
					.getTileLineDistance(pc.getLocation()) < 4) {
				return true;
			}
		}
		return false;
	}
}
