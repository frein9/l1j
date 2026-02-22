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

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.Opcodes; //��ų 6�� ����Ʈ
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WarTimeController;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.L1PinkName.PinkNameTimer;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.serverpackets.S_AttackMissPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_ChinSword;
import l1j.server.server.serverpackets.S_ChatPacket; //��ų6�� ����Ʈ
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.types.Point;
import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacketForNpc;
import l1j.server.server.serverpackets.S_SystemMessage;					//////////////////////// ����ų���� - �߰�

public class L1Attack {
	private static Logger _log = Logger.getLogger(L1Attack.class.getName());

	private L1PcInstance _pc = null;

	private L1Character _target = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private final int _targetId;

	private int _targetX;

	private int _targetY;

	private int _statusDamage = 0;

	private static final Random _random = new Random();

	private int _hitRate = 0;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	private boolean _isHit = false;

	private int _damage = 0;

	private int _drainMana = 0;
	
	public int _drainHp = 0; // �ĸ��� ��� �߰�
	
	private int _attckGrfxId = 0;

	private int _attckActId = 0;

	// �����ڰ� �÷��̾��� ����� ���� ����
	private L1ItemInstance weapon = null;

	private int _weaponId = 0;

	private int _weaponType = 0;

	private int _weaponType2 = 0;

	private int _weaponAddHit = 0;

	private int _weaponAddDmg = 0;

	private int _weaponSmall = 0;

	private int _weaponLarge = 0;

	private int _weaponBless = 1;

	private int _weaponEnchant = 0;

	private int _weaponMaterial = 0;

	private int _weaponDoubleDmgChance = 0;

	private L1ItemInstance _arrow = null;

	private L1ItemInstance _sting = null;

	private int _leverage = 10; // 1/10��� ǥ���Ѵ�.

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	// �����ڰ� �÷��̾��� ����� �������ͽ��� ���� ����
// private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, -2, -2,
// -2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9,
// 9, 10, 10, 11, 11, 12, 12, 13, 13, 14 };

// private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -2, -2, -2,
// -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8,
// 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14 };

	private static final int[] strHit = { -2, -2, -2, -2, -2, -2, -2, // 0~7����
		-2, -1, -1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 6, // 8~26����
			7, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 11, 11, 11, 12, 12, 12, // 27~44����
			13, 13, 13, 14, 14, 14, 15, 15, 15, 16, 16, 16, 17, 17, 17}; // 45~59����

	private static final int[] dexHit = { -2, -2, -2, -2, -2, -2, -1, -1, 0, 0, // 1~10����
			1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, // 11~30����
		17, 18, 19, 19, 19, 20, 20, 20, 21, 21, 21, 22, 22, 22, 23, // 31~45����
		23, 23, 24, 24, 24, 25, 25, 25, 26, 26, 26, 27, 27, 27, 28 }; // 46~60����

	private static final int[] strDmg = new int[128];

	static {
				// STR ������ ����
  for (int str = 0; str <= 8; str++) {
   // 1~8�� -2
   strDmg[str] = -2;
  }
  for (int str = 9; str <= 10; str++) {
   // 9~10�� -1
   strDmg[str] = -1;
  }
  strDmg[11] = 0;
  strDmg[12] = 1;
  strDmg[13] = 1;
  strDmg[14] = 2;
  strDmg[15] = 2;
  strDmg[16] = 2;
  strDmg[17] = 3;
  strDmg[18] = 3;
  strDmg[19] = 4;
  strDmg[20] = 4;
  strDmg[21] = 5;
  strDmg[22] = 5;
  strDmg[23] = 6;
  strDmg[24] = 6;
  strDmg[25] = 6;
  strDmg[26] = 7;
  strDmg[27] = 7;
  strDmg[28] = 7;
  strDmg[29] = 8;
  strDmg[30] = 8;
  strDmg[31] = 9;
  strDmg[32] = 9;
  strDmg[33] = 10;
  strDmg[34] = 11;
  int dmg = 12;
  for (int str = 35; str <= 127; str++) { // 35~127�� 4���٣�1
   if (str % 4 == 1) {
		    dmg++;
   }
   strDmg[str] = dmg;
  }
 }

	private static final int[] dexDmg = new int[128];

	static {
		// DEX ������ ����
  for (int dex = 0; dex <= 14; dex++) {
   // 0~14�� 0
   dexDmg[dex] = -1;
  }
  dexDmg[15] = 1;
  dexDmg[16] = 2;
  dexDmg[17] = 3;
  dexDmg[18] = 4;
  dexDmg[19] = 4;
  dexDmg[20] = 5;
  dexDmg[21] = 5;
  dexDmg[22] = 6;
  dexDmg[23] = 6;
  dexDmg[24] = 7;
  dexDmg[25] = 7;
  dexDmg[26] = 7;
  dexDmg[27] = 8;
  dexDmg[28] = 9;
  dexDmg[29] = 10;
  dexDmg[30] = 10;
  dexDmg[31] = 10;
  dexDmg[32] = 11;
  dexDmg[33] = 12;
  dexDmg[34] = 12;
  dexDmg[35] = 13;
  int dmg = 10;
  for (int dex = 36; dex <= 127; dex++) { // 36~127�� 4���٣�1 //#
   if (dex % 4 == 1) {
    dmg+= 2;
   }
   dexDmg[dex] = dmg;
  }
 }
	private static final int[] IntDmg = new int[128]; // Ű��ũ ��Ʈ

	  static {
	   // Int ������ ����
	    for (int Int = 0; Int <= 14; Int++) { // 0~14�� 0 
	     IntDmg[Int] = -1;
	     }
	     IntDmg[15] = 1;    // ��Ʈ�� ���ϴµ����� 
	     IntDmg[16] = 2;
	     IntDmg[17] = 3;
	     IntDmg[18] = 4;
	     IntDmg[19] = 4;
	     IntDmg[20] = 5;
	     IntDmg[21] = 5;
	     IntDmg[22] = 6;
	     IntDmg[23] = 6;
	     IntDmg[24] = 7;
	     IntDmg[25] = 7;
	     IntDmg[26] = 7;
	     IntDmg[27] = 8;
	     IntDmg[28] = 9;
	     IntDmg[29] = 10;
	     IntDmg[30] = 11;
	     IntDmg[31] = 12;
	     IntDmg[32] = 13;
	     IntDmg[33] = 14;
	     IntDmg[34] = 15;
	     IntDmg[35] = 16;
	     IntDmg[36] = 17;
	     IntDmg[37] = 18;
	     IntDmg[38] = 19;
	     IntDmg[39] = 20;
	     int dmg = 21;
	     for (int Int = 40; Int <= 127; Int++) { // 40~127�� 2���٣�2 //#
	      if (Int % 2 == 1) {
	       dmg+= 2;
	      }
	      IntDmg[Int] = dmg;
	     }
	    }
	public void setActId(int actId) {
		_attckActId = actId;
	}

	public void setGfxId(int gfxId) {
		_attckGrfxId = gfxId;
	}

	public int getActId() {
		return _attckActId;
	}

	public int getGfxId() {
		return _attckGrfxId;
	}

	public L1Attack(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			_pc = (L1PcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = PC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = PC_NPC;
			}
			// ���� ������ ���
			weapon = _pc.getWeapon();
			if (weapon != null) {
				_weaponId = weapon.getItem().getItemId();
				_weaponType = weapon.getItem().getType1();
				_weaponType2 = weapon.getItem(). getType();
				_weaponAddHit = weapon.getItem().getHitModifier()
						 + weapon.getHitByMagic();
				_weaponAddDmg = weapon.getItem().getDmgModifier()
						+ weapon.getDmgByMagic();
				_weaponSmall = weapon.getItem().getDmgSmall();
				_weaponLarge = weapon.getItem().getDmgLarge();
				_weaponBless = weapon.getItem().getBless();
				if (_weaponType != 20 && _weaponType != 62) {
					_weaponEnchant = weapon.getEnchantLevel()
							- weapon.get_durability(); // �ջ�� ���̳ʽ�
				} else {
					_weaponEnchant = weapon.getEnchantLevel();
				}
				_weaponMaterial = weapon.getItem().getMaterial();
				if (_weaponType == 20) { // �Ʒ��� ���
					_arrow = _pc.getInventory().getArrow();
					if (_arrow != null) {
						_weaponBless = _arrow.getItem().getBless();
						_weaponMaterial = _arrow.getItem().getMaterial();
					}
				}
				if (_weaponType == 62) { // ������ ���
					_sting = _pc.getInventory().getSting();
					if (_sting != null) {
						_weaponBless = _sting.getItem().getBless();
						_weaponMaterial = _sting.getItem().getMaterial();
					}
				}
				_weaponDoubleDmgChance = weapon.getItem().getDoubleDmgChance();
			}
			// �������ͽ��� ���� �߰� ������ ����
			if (_weaponType == 20) { // Ȱ�� ���� DEXġ ����
				_statusDamage = dexDmg[_pc.getDex()];
			} else if (_weaponId == 503 || _weaponId == 504 || _weaponId == 292 || _weaponId == 506 
					|| _weaponId == 507 || _weaponId == 508 || _weaponId == 509) {  // Ű��ũ�� ��� INTġ ����   
				_statusDamage = IntDmg[_pc.getInt()];
			} else { // �� �ܴ̿� STRġ ����
				_statusDamage = strDmg[_pc.getStr()];
			}
		} else if (attacker instanceof L1NpcInstance) {
			_npc = (L1NpcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = NPC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = NPC_NPC;
			}
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/* ����������������� ���� ���� ����������������� */

	public boolean calcHit() {
	
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (_weaponType == 20 && _weaponId != 190 && _arrow == null && _pc.getTempCharGfx() != 7959) {
				_isHit = false; // ȭ���� ���� ���� �̽�
				
			} else if (_weaponType == 62 && _sting == null) {
				_isHit = false; // ������ ���� ���� �̽�		
			
			} else if (!_pc.glanceCheck(_targetX, _targetY)) {
				_isHit = false; // �����ڰ� �÷��̾��� ���� ��ֹ� ����
			
			} else if (_weaponId == 247 || _weaponId == 248
					|| _weaponId == 249) {
				_isHit = false; // �÷��� ��B~C ���� ��ȿ
			
			} else if (_calcType == PC_PC) {
				_isHit = calcPcPcHit();
				
			} else if (_calcType == PC_NPC) {
				_isHit = calcPcNpcHit();
				
			}
		} else if (_calcType == NPC_PC) {
			_isHit = calcNpcPcHit();
			
		} else if (_calcType == NPC_NPC) {
			_isHit = calcNpcNpcHit();
		
		}
		return _isHit;
	    }

	// �ܡܡܡ� �÷��̾�κ��� �÷��̾�� ���� ���� �ܡܡܡ�
	/*
	 * PC���� ������ =(PC�� Lv��Ŭ���� ������STR ������DEX ���������� ������DAI�� �ż�/2������ ����)��0.68��10
	 * �̰����� ����� ��ġ�� �ڽ��� �ִ� ����(95%)�� �ִ� ���� �� �� �ִ� ����� PC�� AC �ű�κ��� ����� PC�� AC��  1������ ������ �ڸ������κ���  1��� ����
	 * �ּ� ������5% �ִ� ������95%
	 */
	private boolean calcPcPcHit() {
		_pc.setPinkName(true);
		_pc.broadcastPacket(new S_PinkName(_pc.getId(),30));
		_pc.sendPackets(new S_PinkName(_pc.getId(), 30));
		PinkNameTimer pink = new PinkNameTimer(_pc);
		GeneralThreadPool.getInstance().execute(pink);
		
		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 59) {
			_hitRate += strHit[58];
		} else {
			_hitRate += strHit[_pc.getStr() - 1];
		}

		if (_pc.getDex() > 60) {
			_hitRate += dexHit[59];
		} else {
			_hitRate += dexHit[_pc.getDex() - 1];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup()
			+ (_weaponEnchant / 2);
        } else {
	        _hitRate += _weaponAddHit + _pc.getBowHitup() + _pc
			.getOriginalBowHitup() + (_weaponEnchant / 2);
        }
		if (_weaponType == 20 || _weaponType == 62) {
			_hitRate += _pc.getBowHitRate();
		}
		
		if (80 < _pc.getInventory().getWeight240()
				&& 120 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 1;
		} else if (121 <= _pc.getInventory().getWeight240()
				&& 160 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 3;
		} else if (161 <= _pc.getInventory().getWeight240()
				&& 200 >= _pc.getInventory().getWeight240()) {
			_hitRate -= 5;
		}
		
		int attackerDice = _random.nextInt(20) + 1 + _hitRate - 10;
		if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) {
			attackerDice -= 5;
		}
		if (_targetPc.hasSkillEffect(MIRRORIMG)) {
			attackerDice -= 5;
		}
		if (_targetPc.hasSkillEffect(PEAR)) {
			_hitRate += 3;
		}
	
		int defenderDice = 0;
		
		int defenderValue = (int) (_targetPc.getAc() * 1.5) * -1;
		
		if (_targetPc.getAc() >= 0) {
			defenderDice = 10 - _targetPc.getAc();
		} else if (_targetPc.getAc() < 0) {
			defenderDice = 10 + _random.nextInt(defenderValue) + 1;
		}
		
		int fumble = _hitRate - 9;
		int critical = _hitRate + 10;
		
		if (attackerDice <= fumble) {
			_hitRate = 0;
		} else if (attackerDice >= critical) {
			_hitRate = 100;
		} else {
			if (attackerDice > defenderDice) {
				_hitRate = 100;				
		} else if (attackerDice <= defenderDice) {
			_hitRate = 0;
		}
		}
		
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}
		if (_targetPc.hasSkillEffect(ANTA_MAAN)			    // ������ ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(BIRTH_MAAN)		// ź���� ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(SHAPE_MAAN)		// ������ ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(LIFE_MAAN)) {	// ������ ���� - ���� ����Ȯ�� ȸ��
				int MaanHitRnd = _random.nextInt(100) + 1;
				if (MaanHitRnd <= 10){		// Ȯ��
					_hitRate = 0;
				}
			}
		
		if (_pc.getLocation().getLineDistance(_targetPc.getLocation()) >= 3 
			 && _weaponType != 20 && _weaponType != 62) {   // Ÿ�ٰ��� �Ÿ��� 3�̻��̸鼭 Ȱ�̳� ������ �ƴϸ� ���ݹ̽� ��;
			_hitRate = 0;
		}
		int rnd = _random.nextInt(100) + 1;
		if (_weaponType == 20 && _hitRate > rnd) { // Ȱ�� ���, ��Ʈ ���� ��쿡���� ER������ ȸ�Ǹ� ���� �ǽ��Ѵ�.
			return calcErEvasion();
		}

		return _hitRate >= rnd;
	    }
	// �ܡܡܡ� �÷��̾�κ��� NPC ���� ���� ���� �ܡܡܡ�
	private boolean calcPcNpcHit() {
	
		// NPC���� ������
		// =(PC�� Lv��Ŭ���� ������STR ������DEX ���������� ������DAI�� �ż�/2������ ����)��5��{NPC�� AC��(-5)}
		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 39) {
			_hitRate += strHit[38];
		} else {
			_hitRate += strHit[_pc.getStr() - 1];
		}

		if (_pc.getDex() > 39) {
			_hitRate += dexHit[38];
		} else {
			_hitRate += dexHit[_pc.getDex() - 1];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + _pc.getOriginalHitup()
					+ (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup() + _pc
					.getOriginalBowHitup() + (_weaponEnchant / 2);
		}

		if (_weaponType == 20 || _weaponType == 62) {
			_hitRate += _pc.getBowHitRate();
		}

		_hitRate *= 5;
		_hitRate += _targetNpc.getAc() * 5;

		if (_hitRate > 95) {
			_hitRate = 95;
		} else if (_hitRate < 5) {
			_hitRate = 5;
		}

		int npcId = _targetNpc.getNpcTemplate().get_npcId();
		if (npcId >= 45912 && npcId <= 45915 // �������� ���� �� ���������� ��콺Ʈ
				&& !_pc.hasSkillEffect(STATUS_HOLY_WATER)) {
			_hitRate = 0;
		}
		if (npcId == 45916 // �������� ���� �� �ϸ� �屺
				&& !_pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
			_hitRate = 0;
		}
		if (npcId == 45941 // �������� ���� �翤
				&& !_pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
			_hitRate = 0;
		}
		if (npcId == 45752 // �ٸ��α�(������)
				&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
			_hitRate = 0;
		}
		if (npcId == 45753 // �ٸ��α�(���� ��)
				&& ! _pc.hasSkillEffect(STATUS_CURSE_BARLOG)) {
			_hitRate = 0;
		}
		if (npcId == 45675 // ����(������)
				&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
			_hitRate = 0;
		}
		if (npcId == 81082 // ����(���� ��)
				&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
			_hitRate = 0;
		}
		if (npcId == 45625 // ȥ��
				&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
			_hitRate = 0;
		}
		if (npcId == 45674 // ��
				&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
			_hitRate = 0;
		}
		if (npcId == 45685 // Ÿ��
				&& ! _pc.hasSkillEffect(STATUS_CURSE_YAHEE)) {
			_hitRate = 0;
		}
		if (npcId >= 46068 && npcId <= 46091 // ����� ������ mob
				&& _pc.getTempCharGfx() == 6035) {
			_hitRate = 0;
		}
		if (npcId >= 46092 && npcId <= 46106 // �׸����� ������ mob
				&& _pc.getTempCharGfx() == 6034) {
			_hitRate = 0;
		}
    	////////// ���� ��ȭ�� ���� �߰� - ����ų���� : ����
		String htmlid = null;
		if (npcId == 45953 || npcId == 45954) {  
			if (_pc.getInventory().checkItem(555561, 1)) {		// �˸��� üũ
				_pc.getInventory().consumeItem(555561, 1);		// �˸��� �Ҹ�
				_hitRate = 95;
				htmlid = ""; // �����츦 �����
			} else{
				_hitRate = 0;
				_pc.sendPackets(new S_SystemMessage("���� ��ȭ �˸����� ���� ��ȭ���� �ʽ��ϴ�."));
			}
		}
		////////// ���� ��ȭ�� ���� �߰� - ����ų���� : �� */

 		int rnd = _random.nextInt(100) + 1;

		return _hitRate >= rnd;
	}

	// �ܡܡܡ� NPC �κ��� �÷��̾�� ���� ���� �ܡܡܡ�
	private boolean calcNpcPcHit() {
		
		// PC���� ������
		// =(NPC�� Lv��2)��5��{NPC�� AC��(-5)}
		_hitRate = _npc.getLevel() * 2;
		_hitRate *= 5;
		_hitRate += _targetPc.getAc() * 5;

		if (_npc instanceof L1PetInstance) { // ���� LV1���� �߰� ����+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		// ���� �������� NPC�� ������ ����
		
		if (_hitRate < _npc.getLevel()) {
			_hitRate = _npc.getLevel();
		}

		if (_hitRate > 95) {
			_hitRate = 95;
		}

		if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= 20;
		}
		if (_targetPc.hasSkillEffect(ANTA_MAAN)			    // ������ ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(BIRTH_MAAN)		// ź���� ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(SHAPE_MAAN)		// ������ ���� - ���� ����Ȯ�� ȸ��
				|| _targetPc.hasSkillEffect(LIFE_MAAN)) {	// ������ ���� - ���� ����Ȯ�� ȸ��
				int MaanHitRnd = _random.nextInt(100) + 1;
				if (MaanHitRnd <= 10){		// Ȯ��
					_hitRate = 0;
				}
			}
		if (_targetPc.hasSkillEffect(MIRRORIMG)) {
			_hitRate -= 20;
		}		
		if (_targetPc.hasSkillEffect(PEAR)) {
			_hitRate += 8;
		}
		if (_hitRate < 5) {
			_hitRate = 5;
		}

		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}

		int rnd = _random.nextInt(100) + 1;

		// NPC�� ���� �������� 10�̻��� ����, 2�̻� ������ �ִ� ���Ȱ�������� �����Ѵ�
		if (_npc.getNpcTemplate().get_ranged() >= 10
				&& _hitRate > rnd
				&& _npc.getLocation().getTileLineDistance(
						new Point(_targetX, _targetY)) >= 2) {
			return calcErEvasion();
		}
		return _hitRate >= rnd;
	}

	// �ܡܡܡ� NPC �κ��� NPC ���� ���� ���� �ܡܡܡ�
	private boolean calcNpcNpcHit() {
		int target_ac = 10 - _targetNpc.getAc();
		int attacker_lvl = _npc.getNpcTemplate().get_level();

		if (target_ac != 0) {
			_hitRate = (100 / target_ac * attacker_lvl); // �ǰ����� AC = ������ Lv
			// �� �� ������ 100%
		} else {
			_hitRate = 100 / 1 * attacker_lvl;
		}

		if (_npc instanceof L1PetInstance) { // ���� LV1���� �߰� ����+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		if (_hitRate < attacker_lvl) {
			_hitRate = attacker_lvl; // ���� ������=L����
		}
		if (_hitRate > 95) {
			_hitRate = 95; // �ְ� �������� 95%
		}
		if (_hitRate < 5) {
			_hitRate = 5; // ������ Lv�� 5 �̸����� ������ 5%
		}

		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	// �ܡܡܡ� ER�� ���� ȸ�� ���� �ܡܡܡ�
	private boolean calcErEvasion() {
		int er = _targetPc.getEr();

		int rnd = _random.nextInt(100) + 1;
		return er < rnd;
	}

	/* ���������������� ������ ���� ���������������� */

	public int calcDamage() {
		if (_calcType == PC_PC) {
			_damage = calcPcPcDamage();
		} else if (_calcType == PC_NPC) {
			_damage = calcPcNpcDamage();
		} else if (_calcType == NPC_PC) {
			_damage = calcNpcPcDamage();
		} else if (_calcType == NPC_NPC) {
			_damage = calcNpcNpcDamage();
		}
		return _damage;
	}

	// �ܡܡܡ� �÷��̾�κ��� �÷��̾�� ������ ���� �ܡܡܡ�/////////////////////////////////////////////////////////////
	public int calcPcPcDamage() {
	
		int weaponMaxDamage = _weaponSmall;

		int weaponDamage = 0;  
		
        if ((_pc.getZoneType() == 1 && _targetPc.getZoneType() == 0)
        		|| (_pc.getZoneType() == 1 && _targetPc.getZoneType() == -1)) {  // ������Ƽ������ ���/�Ĺ��� ���� �Ұ�
				_isHit = false;
		}
					
		if (_weaponType == 58 && (_random.nextInt(100) + 1) <=
				_weaponDoubleDmgChance) { // ���� ��Ʈ
			weaponDamage = weaponMaxDamage;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0 || _weaponType == 20 || _weaponType == 62) { // �Ǽ�, Ȱ, �� ���� ��
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}
		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;
		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			int WM = 1;
			if (_pc.hasSkillEffect(WEAPON_BREAK)){
				WM += 1;
			}
			if ((_random.nextInt(100) + 1) <= (33 / WM)) {
				weaponTotalDamage *= 2;
			}
		}
		   		   
		if (_weaponType == 54 && (_random.nextInt(100) + 1) <=
				_weaponDoubleDmgChance) { // ���� ��Ʈ
			weaponTotalDamage *= 2;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}
		////////////// 7�� ���� ��Ÿ ////////////////
		if(_weaponEnchant == 7){      
			weaponTotalDamage += 1;
		}
		if(_weaponEnchant == 8){      
			weaponTotalDamage += 2;
		}
	     if(_weaponEnchant == 9){      
			weaponTotalDamage += 4;
		}
		 if(_weaponEnchant == 10){ 
			weaponTotalDamage += 7;
		}
		 if(_weaponEnchant == 11){ 
			weaponTotalDamage += 9;
		}
		 if(_weaponEnchant == 12){ 
			weaponTotalDamage += 11;
		} 
		 if(_weaponEnchant == 13){ 
			weaponTotalDamage += 12;
		}
		 if(_weaponEnchant == 14){ 
			weaponTotalDamage += 13;
		}
		 if(_weaponEnchant >= 15){ 
			weaponTotalDamage += 15;
		}
		//////////// �� ���� ����Ʈ �߰�
		/*if(_weaponEnchant >= 8){ 
			if (_weaponEnchant >= _random.nextInt(100)){
				_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 1248));  // �� ���� ����Ʈ
			}
		}*/
		//////////// 80�� ���� ��Ÿ �޸�Ʈ
		if (_pc.getLevel() >= 80){
			int RndPlus = _pc.getLevel() - 78;
			int RndPlus2 = _random.nextInt(RndPlus);
			if (RndPlus2 > 1){
				RndPlus2 = 1;
			}
			weaponTotalDamage += RndPlus2;
		}
		////////////// 8�� ���� ��Ÿ////////////////
    	double dmg;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup()
					+ _pc.getOriginalDmgup();
		} else {
			dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup()
					+ _pc.getOriginalBowDmgup();
		}

		if (_weaponType == 20) { // Ȱ
			if (_arrow != null) {
				int add_dmg = _arrow.getItem().getDmgSmall();
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190) { // �ڻԼ� ���� Ȱ
				dmg = dmg + _random.nextInt(15) + 1;
			}else if(_pc.getTempCharGfx() == 7959){//õ����Ȱ �̹����߰�
				dmg = dmg + _random.nextInt(13) + 1;

			}	
		} else if (_weaponType == 62) { // �� ���� ��
			int add_dmg = _sting.getItem().getDmgSmall();
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}
		if (_weaponType == 20 || _weaponType == 62) {
			   dmg += _pc.getBowDmgModifier();
			  }   ///////////////�߰��ߴµ� �ɶ�.....

		   dmg = calcBuffDamage(dmg);
		   /** AC�� ���� ������ ���� ������ **/
		/*  if (_targetPc.isKnight() || _targetPc.isDragonKnight()) {
		   dmg -= dmg * (calcPcDefense() * 0.0037);
		  } else if (_targetPc.isElf() || _targetPc.isDarkelf() || _targetPc.isCrown()) {
		   dmg -= dmg * (calcPcDefense() * 0.0035);
		  } else if (_targetPc.isWizard() || _targetPc.isBlackWizard()) {
		   dmg -= dmg * (calcPcDefense() * 0.0033);
		  } */
		  /** AC�� ���� ������ ���� ������ **/

		/*if (_weaponId == 124) { // ������Ʈ ������
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
		} else*/ if (_weaponId == 2 || _weaponId == 200002) { // ���̽��ٰ�
			dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
		} else if (_weaponId == 204 || _weaponId == 100204) { // ��ȫ�� ũ�ν�����
			L1WeaponSkill.giveFettersEffect(_targetPc);
		} else if (_weaponId == 500 || _weaponId == 501) { 
			dmg += L1WeaponSkill.getChainSwordDamage(_pc);
		/*} else if (_weaponId == 513) {  // ��ũ�������� ������
			L1WeaponSkill.giveDiseaseEffect(_targetPc);*/
		} else {
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
		}
			 /** �Ӽ���æƮ �߰�Ÿ��ġ */
	    if (_weaponType != 0 && weapon.getAttrEnchantLevel() !=0) {
	     switch(weapon.getAttrEnchantLevel()){
	    case 1: case 4: case 7: case 10: dmg += 1 * clacAttrMrDmg() * clacAttrResistDmg(); break;
	    case 2: case 5: case 8: case 11: dmg += 3 * clacAttrMrDmg() * clacAttrResistDmg(); break;
	    case 3: case 6: case 9: case 12: dmg += 5 * clacAttrMrDmg() * clacAttrResistDmg(); break;
	    default: break;
	    }
	    }
	    /** �Ӽ���æƮ �߰�Ÿ��ġ */
		if (_weaponType == 0) { // �Ǽ�
			dmg = (_random.nextInt(5 + (_pc.getLevel() / 10)) + 4) / 4;
		}
		
		if (_weaponType != 20 && _weaponType != 62) {
			Object[] dollList = _pc.getDollList().values().toArray(); // ���������� ���� �߰� ������
			for (Object dollObject : dollList) {
				L1DollInstance doll = (L1DollInstance) dollObject;
				dmg += doll.getDamageByDoll();
			}
		} 
		if (_weaponType == 20 || _weaponType == 62) {
			   Object[] dollList = _pc.getDollList().values().toArray(); // ���������� ���� �߰� ������ (���Ÿ�)
			   for (Object dollObject : dollList) {
			    L1DollInstance doll = (L1DollInstance) dollObject;
			    dmg += doll.getBowDamageByDoll();
			    _hitRate += doll.getBowDamageByDoll();
			   }
			  }
		if (_weaponType != 20 && _weaponType != 62) {
			   Object[] dollList = _pc.getDollList().values().toArray(); // ���������� ���� �߰� �� ������
			   for (Object dollObject : dollList) {
			    L1DollInstance doll = (L1DollInstance) dollObject;
			    int rnd = _random.nextInt(100) + 1;
			    if ( doll.getDollType() == 12 && rnd < 10){ // ��̾������� �̰� rnd ���ڰ� 10���� �������� ��� �Ʒ� ���Ŵ� 
			     int rnd2 = _random.nextInt(10) + 1;
			     L1DamagePoison.doInfection(_pc, _target, 3000, rnd2); //3�� rnd2 ������ ������
			    }
			   }
			  } 
	
		Object[] dollList1 = _targetPc.getDollList().values().toArray();  // ���� ������ ���� ������ ����. ��������
		for (Object dollObject : dollList1) {           
		    L1DollInstance doll = (L1DollInstance) dollObject;
		    dmg -= doll.getDamageReductionByDoll();
		}
		
		dmg -= _targetPc.getDamageReductionByArmor(); // ���� �ⱸ�� ���� ������ �氨

		if (_targetPc.hasSkillEffect(COOKING_1_0_S) // �丮�� ���� ������ �氨
				|| _targetPc.hasSkillEffect(COOKING_1_1_S)
				|| _targetPc.hasSkillEffect(COOKING_1_2_S)
				|| _targetPc.hasSkillEffect(COOKING_1_3_S)
				|| _targetPc.hasSkillEffect(COOKING_1_4_S)
				|| _targetPc.hasSkillEffect(COOKING_1_5_S)
				|| _targetPc.hasSkillEffect(COOKING_1_6_S)
			    || _targetPc.hasSkillEffect(COOKING_1_7_S)
			    || _targetPc.hasSkillEffect(COOKING_1_8_S)
			    || _targetPc.hasSkillEffect(COOKING_1_9_S)
			    || _targetPc.hasSkillEffect(COOKING_1_10_S)
			    || _targetPc.hasSkillEffect(COOKING_1_11_S)
			    || _targetPc.hasSkillEffect(COOKING_1_12_S)
			    || _targetPc.hasSkillEffect(COOKING_1_13_S)
			    || _targetPc.hasSkillEffect(COOKING_1_14_S)
			    || _targetPc.hasSkillEffect(COOKING_1_15_S)
			    || _targetPc.hasSkillEffect(COOKING_1_16_S)
			    || _targetPc.hasSkillEffect(COOKING_1_17_S)
			    || _targetPc.hasSkillEffect(COOKING_1_18_S)
			    || _targetPc.hasSkillEffect(COOKING_1_19_S)
			    || _targetPc.hasSkillEffect(COOKING_1_20_S)
			    || _targetPc.hasSkillEffect(COOKING_1_21_S)
			    || _targetPc.hasSkillEffect(COOKING_1_22_S)
			    || _targetPc.hasSkillEffect(COOKING_1_23_S)) {
			dmg -= 5;
		}
		//� ����
		  if (_targetPc.hasSkillEffect(LUCK_A)){
		   dmg -= 3;
		  }
		  if (_targetPc.hasSkillEffect(LUCK_B)){
		    dmg -= 2; 
		  }
		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg -= dmg * 0.3;
		}
		if (_targetPc.hasSkillEffect(AVATA)) {  // �ƹ�Ÿ
			dmg *= 1.5;
		}
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ����
		if (_targetPc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //���� ���� �ݶ�󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_AREA_ICE_LANCE)) { //��Ǫ���� �����󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_Basill)) {  //�ٽǾ󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_Coca)) {  //��ī�󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_pc.hasSkillEffect(VALA_MAAN)		// ȭ���� ���� - ����Ȯ���� �����߰�Ÿ��+2
				|| _pc.hasSkillEffect(LIFE_MAAN)) {	// ������ ���� - ����Ȯ���� �����߰�Ÿ��+2
				int MaanAttDmg = _random.nextInt(100) + 1;
				if (MaanAttDmg <= 30){	// Ȯ��
					dmg += 2;
				}		
			}
	// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ��
		/*
		 * ���罺ų
		 */
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if(_pc.hasSkillEffect(BURNING_SLASH)){
		   dmg += 10;
		   _pc.sendPackets(new S_SkillSound(_targetPc.getId(), 6591));
		   _pc.broadcastPacket(new S_SkillSound(_targetPc.getId(), 6591));
		   _pc.removeSkillEffect(BURNING_SLASH);
		  }
		if (_targetPc.hasSkillEffect(PAYTIONS)) {
			dmg -= 2;
		}
		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� �÷��̾�κ��� NPC ���� ������ ���� �ܡܡܡ�
	private int calcPcNpcDamage() {
		int weaponMaxDamage = 0;
		if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small")
				&& _weaponSmall > 0) {
			weaponMaxDamage = _weaponSmall;
		} else if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase(
				"large")
				&& _weaponLarge > 0) {
			weaponMaxDamage = _weaponLarge;
		}

		int weaponDamage = 0;
		if (_weaponType == 58 && (_random.nextInt(100) + 1) <=
				_weaponDoubleDmgChance) { // ���� ��Ʈ
			weaponDamage = weaponMaxDamage;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0 || _weaponType == 20 || _weaponType == 62) { // �Ǽ�, Ȱ, �� ���� ��
			weaponDamage = 0;
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}
		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;
		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			int WM = 1;
			if (_pc.hasSkillEffect(WEAPON_BREAK)){
				WM += 1;
			}
			if ((_random.nextInt(100) + 1) <= (33 / WM)) {
				weaponTotalDamage *= 2;
			}
		}

		weaponTotalDamage += calcMaterialBlessDmg(); // ���ູ ������ ���ʽ�
		if (_weaponType == 54 && (_random.nextInt(100) + 1) <=
				_weaponDoubleDmgChance) { // ���� ��Ʈ
			weaponTotalDamage *= 2;
			_pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}
		////////////// 8�� ���� ��Ÿ ////////////////
			////////////// 7�� ���� ��Ÿ ////////////////
			if(_weaponEnchant == 7){      
				weaponTotalDamage += 1;
			}
			if(_weaponEnchant == 8){      
				weaponTotalDamage += 2;
			}
		     if(_weaponEnchant == 9){      
				weaponTotalDamage += 3;
			}
			 if(_weaponEnchant == 10){ 
				weaponTotalDamage += 6;
			}
			 if(_weaponEnchant == 11){ 
				weaponTotalDamage += 8;
			}
			 if(_weaponEnchant == 12){ 
				weaponTotalDamage += 10;
			} 
			 if(_weaponEnchant == 13){ 
				weaponTotalDamage += 12;
			}
			 if(_weaponEnchant == 14){ 
				weaponTotalDamage += 13;
			}
			 if(_weaponEnchant >= 15){ 
				weaponTotalDamage += 15;
			}
		//////////// �� ���� ����Ʈ �߰�
		if(_weaponEnchant >= 8){ 
			if (_weaponEnchant >= _random.nextInt(100)){
				_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 1248));  // �� ���� ����Ʈ
			}
		}
		//////////// 80�� ���� ��Ÿ �޸�Ʈ
		if (_pc.getLevel() >= 80){
			int RndPlus = _pc.getLevel() - 78;
			int RndPlus2 = _random.nextInt(RndPlus);
			if (RndPlus2 > 1){
				RndPlus2 = 1;
			}
			weaponTotalDamage += RndPlus2;
		}
		////////////// 8�� ���� ��Ÿ////////////////

		double dmg;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage + _pc.getDmgup()
					+ _pc.getOriginalDmgup();
		} else {
			dmg = weaponTotalDamage + _statusDamage + _pc.getBowDmgup()
					+ _pc.getOriginalBowDmgup();
		}

		if (_weaponType == 20) { // Ȱ
			if (_arrow != null) {
				int add_dmg = 0;
				if (_targetNpc.getNpcTemplate().get_size().
						equalsIgnoreCase("large")) {
					add_dmg = _arrow.getItem().getDmgLarge();
				} else {
					add_dmg = _arrow.getItem().getDmgSmall();
				}
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				if (_targetNpc.getNpcTemplate().is_hard()) {
					add_dmg /= 2;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190) { // �ڻԼ� ���� Ȱ
				dmg = dmg + _random.nextInt(15) + 1;
			}else if(_pc.getTempCharGfx() == 7959){ //õ����Ȱ�̹����߰�
			    dmg = dmg + _random.nextInt(13) + 1;
			   }
		} else if (_weaponType == 62) { // �� ���� ��
			int add_dmg = 0;
			if (_targetNpc.getNpcTemplate().get_size().
					equalsIgnoreCase("large")) {
				add_dmg = _sting.getItem().getDmgLarge();
			} else {
				add_dmg = _sting.getItem().getDmgSmall();
			}
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}
		if (_weaponType == 20 || _weaponType == 62) {
			   dmg += _pc.getBowDmgModifier();
			  }/////////���⵵ �ϳ��߰�.......

		dmg = calcBuffDamage(dmg);
		/** AC�� ���� ������ ���� ������ **/
		dmg -= dmg * (calcNpcDefense() * 0.0035);
	    /** AC�� ���� ������ ���� ������ **/  

		/*if (_weaponId == 124) { // ������Ʈ ������
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
		} else */if (_weaponId == 204 || _weaponId == 100204) { // ��ȫ�� ũ�ν�����
			L1WeaponSkill.giveFettersEffect(_targetNpc);
		} else if (_weaponId == 2 || _weaponId == 200002) { // �ǰ�
			dmg = _targetNpc.getMaxHp() / 300;
			_pc.getInventory().removeItem(weapon, 1);
		/*} else if (_weaponId == 513) {  // ��ũ�������� ������
			L1WeaponSkill.giveDiseaseEffect(_targetNpc);*/
		} else if (_weaponId == 500 || _weaponId == 501) { 
			dmg += L1WeaponSkill.getChainSwordDamage(_pc);
		} else {
			dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target, _weaponId);
		}
		//Suny's tohandsword ���� �ߵ�(NPC)
		  if( ( _weaponId == 562 ) && !_target.hasSkillEffect(SHOCK_STUN) ){       //�������̶�� �ߺ��ȵ���~   
		   int[] stunTimeArray = { 1300, 2000, 2300, 2500, 3000, 3500, 4000, 4500 };  //���� �ߵ��Ǵ� �ð��� �������� ���õ˴ϴ�.
		   Random random = new Random();  
		   int rnd = random.nextInt(stunTimeArray.length); 
		   int probability = random.nextInt(100)+1; //�̰� �ߵ�Ȯ���Դϴ�~
		   if( probability < 10 ){
		    int _shockStunDuration = stunTimeArray[rnd];    
		    L1EffectSpawn.getInstance().spawnEffect(81162, _shockStunDuration,_target.getX(), _target.getY(), _target.getMapId());
		    L1NpcInstance npc1 = (L1NpcInstance) _target;
		    npc1.setParalyzed(true);
		    npc1.setParalysisTime(_shockStunDuration);
		    _target.setSkillEffect(SHOCK_STUN, _shockStunDuration);
		   }
		  }
		/*���罺ų
		 * 
		 */
		  if(_pc.hasSkillEffect(BURNING_SLASH)){
			   dmg += 10;
			   _pc.sendPackets(new S_SkillSound(_targetNpc.getId(), 6591));
			   _pc.broadcastPacket(new S_SkillSound(_targetNpc.getId(), 6591));
			   _pc.removeSkillEffect(BURNING_SLASH);
			  }
		  /** �Ӽ���æƮ �߰�Ÿ��ġ */
		    if (_weaponType != 0 && weapon.getAttrEnchantLevel() !=0) {
		     switch(weapon.getAttrEnchantLevel()){
		    case 1: case 4: case 7: case 10: dmg += 1 * clacAttrMrDmg() * clacAttrResistDmg(); break;
		    case 2: case 5: case 8: case 11: dmg += 3 * clacAttrMrDmg() * clacAttrResistDmg(); break;
		    case 3: case 6: case 9: case 12: dmg += 5 * clacAttrMrDmg() * clacAttrResistDmg(); break;
		    default: break;
		    }
		    }
		  /** �Ӽ���æƮ �߰�Ÿ��ġ */   
		if (_weaponType == 0) { // �Ǽ�
			dmg = (_random.nextInt(5 + (_pc.getLevel() / 10)) + 4) / 4;
		}

		if (_weaponType != 20 && _weaponType != 62) {
			Object[] dollList = _pc.getDollList().values().toArray();// ���������� ���� �߰� ������
			for (Object dollObject : dollList) {
				L1DollInstance doll = (L1DollInstance) dollObject;
				dmg += doll.getDamageByDoll();
			}
		}
		 if (_weaponType != 20 && _weaponType != 62) {
			   Object[] dollList = _pc.getDollList().values().toArray(); // ���������� ���� �߰� �� ������
			   for (Object dollObject : dollList) {
			    L1DollInstance doll = (L1DollInstance) dollObject;
			    int rnd = _random.nextInt(100) + 1;
			    if ( doll.getDollType() == 12 && rnd < 10){ // ��̾������� �̰� rnd ���ڰ� 10���� �������� ��� �Ʒ� ���Ŵ� 
			     int rnd2 = _random.nextInt(10) + 1;
			     L1DamagePoison.doInfection(_pc, _target, 3000, rnd2); //3�� rnd2 ������ ������
			    }
			   }
			  } 
	
		dmg -= calcNpcDamageReduction();

		// �÷��̾�κ��� �ֿϵ���, ��� ����
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetNpc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_targetNpc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_targetNpc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _targetNpc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}
		if (_pc.hasSkillEffect(VALA_MAAN)		// ȭ���� ���� - ����Ȯ���� �����߰�Ÿ��+2
				|| _pc.hasSkillEffect(LIFE_MAAN)) {	// ������ ���� - ����Ȯ���� �����߰�Ÿ��+2
				int MaanAttDmg = _random.nextInt(100) + 1;
				if (MaanAttDmg <= 30){	// Ȯ��
					dmg += 2;
				}		
			}
             
		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ����
		if (_targetNpc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //���� ���� �ݶ�󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_AREA_ICE_LANCE)) {	//��Ǫ���� �����󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_Basill)) {  //�ٽǾ󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_Coca)) {  //��ī�󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		//calcDamageInCrystalCave(_targetNpc,dmg);
	// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ��

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� NPC �κ��� �÷��̾�� ������ ���� �ܡܡܡ�
	private int calcNpcPcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0D;
		/*if (lvl < 10) {
			dmg = _random.nextInt(lvl) + 10D + _npc.getStr() + 1;
		} else {
			dmg = _random.nextInt(lvl) + _npc.getStr() + 1;
		}*/
		if (lvl > 1) {   //�������� ����
			dmg = _random.nextInt(lvl) + _npc.getStr() + 1;
		} else if (lvl > 55) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*2 + 1;
		} else if (lvl > 60) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*4 + 1;
		} else if (lvl > 65) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*6 + 1;
		} else if (lvl > 70) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*8 + 1;
		} else if (lvl > 75) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*10 + 1;
		} else if (lvl > 80) {
			dmg = _random.nextInt(lvl) + _npc.getStr()*12 + 1;
		}

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 16); // ���� LV16���� �߰� Ÿ��
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

		dmg += _npc.getDmgup();

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;
		/** AC�� ���� ������ ���� ������ **/
		  if (_targetPc.isKnight() || _targetPc.isDragonKnight()) {
		   dmg -= dmg * (calcPcDefense() * 0.0037);
		  } else if (_targetPc.isElf() || _targetPc.isDarkelf() || _targetPc.isCrown()) {
		   dmg -= dmg * (calcPcDefense() * 0.0035);
		  } else if (_targetPc.isWizard() || _targetPc.isBlackWizard()) {
		   dmg -= dmg * (calcPcDefense() * 0.0033);
		  }
		//  dmg -= calcPcDefense();  //���������� ����
		  /** AC�� ���� ������ ���� ������ **/

		if (_npc.isWeaponBreaked()) { // NPC�� �����극��ũ��.
			dmg /= 2;
		}

		dmg -= 1.5*_targetPc.getDamageReductionByArmor(); // ���� �ⱸ�� ���� ������ �氨 [1.5 x �÷��̾� ����ġ]
		
		Object[] dollList1 = _targetPc.getDollList().values().toArray();  // ���� ������ ���� ������ ����. ����
		for (Object dollObject : dollList1) {           
		    L1DollInstance doll = (L1DollInstance) dollObject;
		    dmg -= doll.getDamageReductionByDoll();
		}

		if (_targetPc.hasSkillEffect(COOKING_1_0_S) // �丮�� ���� ������ �氨
				|| _targetPc.hasSkillEffect(COOKING_1_1_S)
				|| _targetPc.hasSkillEffect(COOKING_1_2_S)
				|| _targetPc.hasSkillEffect(COOKING_1_3_S)
				|| _targetPc.hasSkillEffect(COOKING_1_4_S)
				|| _targetPc.hasSkillEffect(COOKING_1_5_S)
				|| _targetPc.hasSkillEffect(COOKING_1_6_S)
				|| _targetPc.hasSkillEffect(COOKING_1_7_S)
				|| _targetPc.hasSkillEffect(COOKING_1_8_S)
				|| _targetPc.hasSkillEffect(COOKING_1_9_S)
				|| _targetPc.hasSkillEffect(COOKING_1_10_S)
				|| _targetPc.hasSkillEffect(COOKING_1_11_S)
				|| _targetPc.hasSkillEffect(COOKING_1_12_S)
				|| _targetPc.hasSkillEffect(COOKING_1_13_S)
				|| _targetPc.hasSkillEffect(COOKING_1_14_S)
				|| _targetPc.hasSkillEffect(COOKING_1_15_S)
				|| _targetPc.hasSkillEffect(COOKING_1_16_S)
				|| _targetPc.hasSkillEffect(COOKING_1_17_S)
				|| _targetPc.hasSkillEffect(COOKING_1_18_S)
				|| _targetPc.hasSkillEffect(COOKING_1_19_S)
				|| _targetPc.hasSkillEffect(COOKING_1_20_S)
				|| _targetPc.hasSkillEffect(COOKING_1_21_S)
				|| _targetPc.hasSkillEffect(COOKING_1_22_S)
				|| _targetPc.hasSkillEffect(COOKING_1_23_S)) {
			dmg -= 5;
		}
		if (_targetPc.hasSkillEffect(COOKING_1_7_S)
				|| _targetPc.hasSkillEffect(COOKING_1_15_S)
				|| _targetPc.hasSkillEffect(COOKING_1_23_S)) { // ����Ʈ�� ���� ������ �氨
			dmg -= 5;
		}

		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg -= dmg * 0.3;
		}
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ����
		if (_targetPc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //���� ���� �ݶ�󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_AREA_ICE_LANCE)) {	//��Ǫ���� �����󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_Basill)) {  //�ٽǾ󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetPc.hasSkillEffect(Mob_Coca)) {  //��ī�󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
	// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ��
		if (_targetPc.hasSkillEffect(DRAGON_SKIN)) {
			dmg -= 2;
		}
		if (_targetPc.hasSkillEffect(PAYTIONS)) {
			dmg -= 2;
		}
		// �ֿϵ���, ������κ��� �÷��̾ ����
		boolean isNowWar = false;
		int castleId = L1CastleLocation.getCastleIdByArea(_targetPc);
		if (castleId > 0) {
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		if (!isNowWar) {
			if (_npc instanceof L1PetInstance) {
				dmg /= 8;
			}
			if (_npc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _npc;
				if (summon.isExsistMaster()) {
					dmg /= 8;
				}
			}
		}

		if (dmg <= 0) {
			_isHit = false;
		}

		addNpcPoisonAttack(_npc, _targetPc);

		return (int) dmg;
	}

	// �ܡܡܡ� NPC �κ��� NPC ���� ������ ���� �ܡܡܡ�
	private int calcNpcNpcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			dmg = _random.nextInt(_npc.getNpcTemplate().get_level())
					+ _npc.getStr() / 2 + 1;
			dmg += (lvl / 16); // ���� LV16���� �߰� Ÿ��
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		} else {
			dmg = _random.nextInt(lvl) + _npc.getStr() + 1;
		}

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		dmg = dmg * getLeverage() / 10;
		/** AC�� ���� ������ ���� ������ **/
		dmg -= dmg * (calcNpcDefense() * 0.0035);
		  /** AC�� ���� ������ ���� ������ **/
		dmg -= calcNpcDamageReduction();

		if (_npc.isWeaponBreaked()) { // NPC�� �����극��ũ��.
			dmg /= 2;
		}

		addNpcPoisonAttack(_npc, _targetNpc);

		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(FREEZING_BLIZZARD)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ����
		if (_targetNpc.hasSkillEffect(Mob_CALL_LIGHTNING_ICE)) { //���� ���� �ݶ�󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_AREA_ICE_LANCE)) { //�ķ縮�� �����󸮱�
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_Basill)) {  //�ٽǾ󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		if (_targetNpc.hasSkillEffect(Mob_Coca)) {  //��ī�󸮱ⵥ����0
			dmg = 0.00000000000000000000000000000000000000000000000000001;
		}
		//calcDamageInCrystalCave(_targetNpc,dmg);
	// ���Ͱ� ����߰��ϱ� ���� ���� - ����ų���� �߰� : ��

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// �ܡܡܡ� �÷��̾��� ������ ��ȭ ���� �ܡܡܡ�
	private double calcBuffDamage(double dmg) {
	
		// �ҹ���, �ټ�ī�� ��������  1.5�谡 ���� �ʴ´�
		if (_pc.hasSkillEffect(BURNING_SPIRIT)
				|| (_pc.hasSkillEffect(ELEMENTAL_FIRE) && _weaponType != 20 && _weaponType != 62)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				if (_pc.hasSkillEffect(FIRE_WEAPON)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(FIRE_BLESS)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(BURNING_WEAPON)) {
					tempDmg -= 6;
				}
				if (_pc.hasSkillEffect(BERSERKERS)) {
					tempDmg -= 5;
				}
				double diffDmg = dmg - tempDmg;
				dmg = tempDmg * 1.5 + diffDmg;
			}
		}

		return dmg;
	}

	// �ܡܡܡ� �÷��̾��� AC�� ���� ������ �氨 �ܡܡܡ�
	private int calcPcDefense() {
		int ac = Math.max(0, 10 - _targetPc.getAc());
	//	int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
	//	return _random.nextInt(acDefMax + 1);
		return ac; //�߰�
	}
	
	// �ܡܡܡ� NPC�� AC�� ���� ������ �氨 �ܡܡܡ�
	 private int calcNpcDefense() {
	  int ac = Math.max(0, 10 - _targetNpc.getAc());
	  return ac;
	 } 
	 /** �Ӽ���æƮ Mr Resist ���� */
	 private double clacAttrMrDmg() {
	  int mr = 0;
	  double mr2 = 0;
	  if (_calcType == PC_PC) {
	   mr = _targetPc.getMr();
	  } else if (_calcType == PC_NPC) {
	   mr = _targetNpc.getMr();
	  }  
	  if (mr >= 0 && mr <=100) {
	   mr2 = (1 + (100 - mr) * 0.01);
	  } else {
	   mr2 = (int)(1 - (mr - 100) * 0.002);
	  }
	  return mr2;
	 }
	 
	 private double clacAttrResistDmg() {
	  int resist = 0;
	  int resistFloor = 0;
	  
	  if (_calcType == PC_PC) {
	   switch(weapon.getAttrEnchantLevel()){
	   case 1 : resist = _targetPc.getEarth(); break;
	   case 2 : resist = _targetPc.getFire(); break;
	   case 3 : resist = _targetPc.getWater(); break;
	   case 4 : resist = _targetPc.getWind(); break;
	   }
	  } else if (_calcType == PC_NPC) {
	   switch(weapon.getAttrEnchantLevel()){
	   case 1 : case 2 : case 3 : 
	   if (_targetNpc.getNpcTemplate().get_weakfire() == 1) {//���Ӽ� ��
	    resist = -50; break;
	   }
	   case 4 : case 5 : case 6 : 
	   if (_targetNpc.getNpcTemplate().get_weakwater() == 1) {//���Ӽ� ��
	    resist = -50; break;
	   }
	   case 7 : case 8 : case 9 : 
	   if (_targetNpc.getNpcTemplate().get_weakwind() == 1) { //���Ӽ� �ٶ�
	    resist = -50; break;
	   }
	   case 10 : case 11 : case 12 : 
	   if (_targetNpc.getNpcTemplate().get_weakearth() == 1) {//���Ӽ� ��
	    resist = -50; break;
	   }
	   }
	  }
	  if (resist >= 100) {
	   resistFloor = 45;
	  } else if  (resist >= 0 && resist < 100){
	   resistFloor = (int)(0.4 * resist);
	  } else if  (resist < 0){
	   resistFloor = (int)(0.8 * resist);
	  }
	  
	  double attrDeffence = 1 - resistFloor / 100;

	  return attrDeffence;
	 }
	 /** �Ӽ���æƮ Mr Resist ���� */

	// �ܡܡܡ� NPC�� ������ ��ҿ� ���� �氨 �ܡܡܡ�
	private int calcNpcDamageReduction() {
		return _targetNpc.getNpcTemplate().get_damagereduction();
	}
	
	// �ܡܡܡ� ��ũ�������� ������ �ܡܡܡ�
	private double calcWeaponSkillDamage() {
			double dmg = 0;
			int chance = _random.nextInt(100) + 1;
			
	    if (_weaponId == 513 && chance < 10) { // ��ũ�������� ������
	    	 _target.killSkillEffectTimer(DISEASE); 
		     _target.setSkillEffect(DISEASE, 64000);
		     _pc.sendPackets(new S_SkillSound(_targetId, 2230));
		     _pc.broadcastPacket(new S_SkillSound(_targetId, 2230));
		}
	         return dmg;
	}
			
	// �ܡܡܡ� ������ ������ �ູ�� ���� �߰� ������ ���� �ܡܡܡ�
	private int calcMaterialBlessDmg() {
		int damage = 0;
		int undead = _targetNpc.getNpcTemplate().get_undead();
		if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22)
				&& (undead == 1 || undead == 3)) { // �����̽����������ϸ���, ����, �� ����衤�� ����� ����
			damage += _random.nextInt(20) + 1;
		}
		if (_weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3)) { // �ູ ����, ����, �� ����衤�Ǹ��衤�� ����� ����
			damage += _random.nextInt(4) + 1;
		}
		if (_pc.getWeapon() != null && _weaponType != 20 && _weaponType != 62
				&& weapon.getHolyDmgByMagic() != 0 && (undead == 1 || undead == 3)) {
			damage += weapon.getHolyDmgByMagic();
		}
		return damage;
	}

	// �ܡܡܡ� NPC�� �� ������ �߰� ���ݷ��� ��ȭ �ܡܡܡ�
	private boolean isUndeadDamage() {
		boolean flag = false;
		int undead = _npc.getNpcTemplate().get_undead();
		boolean isNight = L1GameTimeClock.getInstance(). currentTime(). isNight();
		if (isNight && (undead == 1 || undead == 3)) { // 18~6��, ����, �� ����衤�� ����� ����
			flag = true;
		}
		return flag;
	}

	// �ܡܡܡ� NPC�� �������� �ΰ� �ܡܡܡ�
	private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
		if (_npc.getNpcTemplate().get_poisonatk() != 0) { // ������ �־�
			if (15 >= _random.nextInt(100) + 1) { // 15%�� Ȯ���� ������
				if (_npc.getNpcTemplate().get_poisonatk() == 1) { // ���
					// 3�� �ֱ⿡ ������ 5
					L1DamagePoison.doInfection(attacker, target, 3000, 5);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 2) { // ħ����
					L1SilencePoison.doInfection(target);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 4) { // ����
					// 20�� �Ŀ� 45�ʰ� ����
					L1ParalysisPoison.doInfection(target, 20000, 45000);
				}
			}
		} else if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // ���� ���� �־�
		}
	}

	// ����� ������������ ��ö�� ������������ MP����� ���� �����
	public void calcStaffOfMana() {
		// SOM �Ǵ� ��ö�� SOM
		if (_weaponId == 126 || _weaponId == 127 
			|| _weaponId == 419 || _weaponId == 564) {
			int som_lvl = _weaponEnchant + 1; // �ִ� MP������� ����
			if (som_lvl < 0) {
				som_lvl = 0;
			}
			// MP������� ���� ���
			_drainMana = _random.nextInt(som_lvl) + 1;
			// �ִ� MP������� 9�� ����
			if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
				_drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
			}
		}
		if (_weaponId == 510   // ������ �ܰ�
			){
			_drainMana = 3;
		}
	}
	// ����� �ĸ��� ��� HP����� ���� �����
	public void calcDrainOfHp() {  
		  if (_weaponId == 512 || _weaponId == 568 
			  || _weaponId == 562 || _weaponId == 567) { // �ĸ��� ���
			  int HpWeapon_lvl = _weaponEnchant + 5; // �ִ� HP������� ����
			  if (HpWeapon_lvl < 0) {
				  HpWeapon_lvl = 0;
			  }
			// HP������� ���� ���
		   _drainHp = _random.nextInt(HpWeapon_lvl) + 2;
		    // �ִ� HP������� Config.HP_DRAIN_LIMIT_PER_HP_ATTACK �� ����
			if (_drainHp > Config.HP_DRAIN_LIMIT_PER_HP_ATTACK) {
				_drainHp = Config.HP_DRAIN_LIMIT_PER_HP_ATTACK;
			}
		 }
	}

	
	// ����� PC�� �������� �ΰ� �����
	public void addPcPoisonAttack(L1Character attacker, L1Character target) {
		int chance = _random.nextInt(100) + 1;
		if ((_weaponId == 13 || _weaponId == 44 // FOD, ����� ��ũ ������ �ҵ�
				|| (_weaponId != 0 && _pc.hasSkillEffect(ENCHANT_VENOM))) // ��îƮ
																			// ������
				&& chance <= 10) {
			// ���, 3�� �ֱ�, ������ HP-5
			int rnd = _random.nextInt(10) + 1;
			L1DamagePoison.doInfection(attacker, target, 3000, rnd);
		}
	}

	/* ��������������� ���� ��� �۽� ��������������� */

	public void action() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			actionPc();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			actionNpc();
		}
	}

	// �ܡܡܡ� �÷��̾��� ���� ��� �۽� �ܡܡܡ�
	private void actionPc() {
		_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ
		
		if (_weaponType == 20) {
			if (_arrow != null) {
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 66,
						_targetX, _targetY, _isHit));
				_pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId, 66,
						_targetX, _targetY, _isHit));
				if (_isHit) {
					_target.broadcastPacketExceptTargetSight(
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _pc);					
				}
				_pc.getInventory().removeItem(_arrow, 1);
			} else if (_weaponId == 190) { 
				_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2349,
						_targetX, _targetY, _isHit));
				_pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId,
						2349, _targetX, _targetY, _isHit));
				if (_isHit) {
					_target.broadcastPacketExceptTargetSight(
							new S_DoActionGFX(_targetId,
									ActionCodes.ACTION_Damage), _pc);
				}
			  } else if (_pc.getTempCharGfx() == 7959){ //�̺кк��� �߰���
				    _pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 7972,
				      _targetX, _targetY, _isHit));
				    _pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId,
				      7972, _targetX, _targetY, _isHit));
				    if (_isHit) {
				     _target.broadcastPacketExceptTargetSight(
				       new S_DoActionGFX(_targetId,
				         ActionCodes.ACTION_Damage), _pc);
				    }
				}
		} else if (_weaponType == 62 && _sting != null) {
			_pc.sendPackets(new S_UseArrowSkill(_pc, _targetId, 2989,
					_targetX, _targetY, _isHit));
			_pc.broadcastPacket(new S_UseArrowSkill(_pc, _targetId, 2989,
					_targetX, _targetY, _isHit));
			if (_isHit) {
				_target.broadcastPacketExceptTargetSight(
						new S_DoActionGFX(_targetId,
								ActionCodes.ACTION_Damage), _pc);
			}
			_pc.getInventory().removeItem(_sting, 1);
		} else {
			if (_isHit) {
				_pc.sendPackets(new S_AttackPacket(_pc, _targetId,
						ActionCodes.ACTION_Attack));
				_pc.broadcastPacket(new S_AttackPacket(_pc, _targetId,
						ActionCodes.ACTION_Attack));
				_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
						_targetId, ActionCodes.ACTION_Damage), _pc);
		
		} else {
			if (_targetId > 0) {
				_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
				_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
		} else {
				_pc.sendPackets(new S_AttackPacket(_pc, 0,
						ActionCodes.ACTION_Attack));
				_pc.broadcastPacket(new S_AttackPacket(_pc, 0,
						ActionCodes.ACTION_Attack));
				}
			}
		}
	}

	// �ܡܡܡ� NPC�� ���� ��� �۽� �ܡܡܡ�
	private void actionNpc() {
		int _npcObjectId = _npc.getId();
		int bowActId = 0;
		int actId = 0;

		_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ

		// Ÿ�ٰ��� �Ÿ��� 2�̻� ������ ���Ÿ� ����
		boolean isLongRange = (_npc.getLocation().getTileLineDistance(
				new Point(_targetX, _targetY)) > 1);
		bowActId = _npc.getNpcTemplate().getBowActId();

		if (getActId() > 0) {
			actId = getActId();
		} else {
			actId = ActionCodes.ACTION_Attack;
		}

		//////////////////////////////////////////////////////////////////////////////////////// ����ų���� - �߰� : ����
		//////////////////// �⺻������ ���� �͵��� �⺻���ݸ�Ǹ� ��������
		int npcId = _npc.getNpcTemplate().get_npcId();
		if (npcId == 45341 || npcId == 45520 || npcId == 81101 // ȥ�̺��ν� ��(������� ����)
			|| npcId == 45994 || npcId == 45998 || npcId == 46002 // ��μ����a,c,e ��
			|| npcId == 45749 || npcId == 46071 // ����(������) ��
			|| npcId == 45751 || npcId == 46073 // �Ļ���(������) ��
				) {  
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_AltAttack; // 30������� �⺻�������� ���
			}
		} else if (npcId == 45228 || npcId == 45500 || npcId == 45727 // �ô� ��
				|| npcId == 45812 || npcId == 45936	|| npcId == 45300 // �ô� ��
				|| npcId == 45499 || npcId == 45604	|| npcId == 45606 // �����̾� ��
				|| npcId == 45591 || npcId == 45942 || npcId == 45788 // ���Ǵ����� ��
				|| npcId == 45620 || npcId == 45643 // ���Ǵ����� ��
				|| npcId == 45594 || npcId == 45786 || npcId == 45622 || npcId == 45645	// ���Ǵ����� ��
				|| npcId == 45612 // �Ű���ٿ�Ƽ �����
				|| npcId == 45963 // ��������ī���� �����
				|| npcId == 45588 // ������Ŭ���� �����
				|| npcId == 45263 // ��ź��
				|| npcId == 45995 || npcId == 45999 || npcId == 46003 || npcId == 46004 || npcId == 46007 // ��μ����b,d,f,g,h ��
				) {  
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_SkillAttack; // 18������� �⺻�������� ���
			}
		} else if (npcId == 81082 // ����(������)
				) {  
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_SkillBuff; // 19������� �⺻�������� ���
			}
		}
		//////////////////////////////////////////////////////////////////////////////////////// ����ų���� - �߰� : ��

		/*if (_isHit) {
			// �Ÿ��� 2�̻�, �������� Ȱ�� �׼� ID�� �ִ� ���� ������
			if (isLongRange && bowActId > 0) {
				_npc.broadcastPacket(new S_UseArrowSkill(_npc, _targetId,
						bowActId, _targetX, _targetY));
			} else {*/
		if (isLongRange && bowActId > 0) {
			_npc.broadcastPacket(new S_UseArrowSkill(_npc, _targetId,
					bowActId, _targetX, _targetY, _isHit));
		} else {
			if (_isHit) {				
				if (getGfxId() > 0) {
					/*_npc
							.broadcastPacket(new S_UseAttackSkill(_target,
									_npcObjectId, getGfxId(), _targetX,*/
					_npc.broadcastPacket(new S_UseAttackSkill(_target,
							_npcObjectId, getGfxId(), _targetX,
									_targetY, actId));
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							_targetId, ActionCodes.ACTION_Damage), _npc);
				} else {
					_npc.broadcastPacket(new S_AttackPacketForNpc(_target,
							_npcObjectId, actId));
					_target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
							_targetId, ActionCodes.ACTION_Damage), _npc);
				}
			/*}
		} else {

			// �Ÿ��� 2�̻�, �������� Ȱ�� �׼� ID�� �ִ� ���� ������
			if (isLongRange && bowActId > 0) {
				_npc.broadcastPacket(new S_UseArrowSkill(_npc, 0, bowActId,
						_targetX, _targetY));*/
			} else {
				if (getGfxId() > 0) {
					_npc.broadcastPacket(new S_UseAttackSkill(_target,
							_npcObjectId, getGfxId(), _targetX, _targetY,
							actId, 0));
				} else {
					_npc.broadcastPacket(new S_AttackMissPacket(_npc,
							_targetId, actId));
				}
			}
		}
	}

	// ���� �� ����(ȭ��, ����)�� �̽����ٰ� ������ �˵��� ���
	public void calcOrbit(int cx, int cy, int head) // ���� X ���� Y ���� ���ϰ� �ִ� ����
	{
		float dis_x = Math.abs(cx - _targetX); // X������ Ÿ�ٱ����� �Ÿ�
		float dis_y = Math.abs(cy - _targetY); // Y������ Ÿ�ٱ����� �Ÿ�
		float dis = Math.max(dis_x, dis_y); // Ÿ�ٱ����� �Ÿ�
		float avg_x = 0;
		float avg_y = 0;
		if (dis == 0) { // ��ǥ�� ���� ��ġ��� ���ϰ� �ִ� ���⿡ ����
			if (head == 1) {
				avg_x = 1;
				avg_y = -1;
			} else if (head == 2) {
				avg_x = 1;
				avg_y = 0;
			} else if (head == 3) {
				avg_x = 1;
				avg_y = 1;
			} else if (head == 4) {
				avg_x = 0;
				avg_y = 1;
			} else if (head == 5) {
				avg_x = -1;
				avg_y = 1;
			} else if (head == 6) {
				avg_x = -1;
				avg_y = 0;
			} else if (head == 7) {
				avg_x = -1;
				avg_y = -1;
			} else if (head == 0) {
				avg_x = 0;
				avg_y = -1;
			}
		} else {
			avg_x = dis_x / dis;
			avg_y = dis_y / dis;
		}

		int add_x = (int) Math.floor((avg_x * 15) + 0.59f); // ���� �¿찡 ���� �켱�� �ձ�
		int add_y = (int) Math.floor((avg_y * 15) + 0.59f); // ���� �¿찡 ���� �켱�� �ձ�

		if (cx > _targetX) {
			add_x *= -1;
		}
		if (cy > _targetY) {
			add_y *= -1;
		}

		_targetX = _targetX + add_x;
		_targetY = _targetY + add_y;
	}

	/* ���������������� ��� ��� �ݿ� ���������������� */

	public void commit() {
		if (_isHit) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				
				commitPc();
			} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
				
				commitNpc();
			}
		}

		// ������ġ �� ������ Ȯ�ο� �޼���
		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return;
			}
		}
		String msg0 = "";
		String msg1 = "��";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";
		if (_calcType == PC_PC || _calcType == PC_NPC) { // ����Ŀ�� PC�� ���
			msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // ����Ŀ�� NPC�� ���
			msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // Ÿ���� PC�� ���
			msg4 = _targetPc.getName();
			msg2 = "HitR" + _hitRate + "% THP" + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) { // Ÿ���� NPC�� ���
			msg4 = _targetNpc.getName();
			msg2 = "Hit" + _hitRate + "% Hp" + _targetNpc.getCurrentHp();
		}
		msg3 = _isHit ?  _damage + "��" : "�̽� �߽��ϴ�";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // ����Ŀ�� PC�� ���
			_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
					msg4)); // \f1%0��%4%1%3 %2
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // Ÿ���� PC�� ���
			_targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
					msg3, msg4)); // \f1%0��%4%1%3 %2
		}
	}

	// �ܡܡܡ� �÷��̾ ��� ����� �ݿ� �ܡܡܡ�
	private void commitPc() {
		if (_calcType == PC_PC) {
			if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (_drainMana > _targetPc.getCurrentMp()) {
					_drainMana = _targetPc.getCurrentMp();
				}
				short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
				_targetPc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}
			// �ĸ��� ��� �߰� //
			if (_drainHp > 0 && _targetPc.getCurrentHp() > 0) { 
				if (_drainHp > _targetPc.getCurrentHp()) {
					_drainHp = _targetPc.getCurrentHp();
				}
				short newHp = (short) (_targetPc.getCurrentHp() - _drainHp);
				_targetPc.setCurrentHp(newHp);
				newHp = (short) (_pc.getCurrentHp() + _drainHp);
				_pc.setCurrentHp(newHp);
			}
			// �ĸ��� ��� �߰� //
			damagePcWeaponDurability(); // ���⸦ �ջ��Ų��.
			_targetPc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_PC) {
          if (_targetPc.getInventory().checkEquipped(20713)) { // ��Ǫ���¸���
            _targetPc.setCurrentMp(_targetPc.getCurrentMp() + _damage / 10); // ������������ 10���θ� hp�� ���
            }
			_targetPc.receiveDamage(_npc, _damage);
		}
	}

	// �ܡܡܡ� NPC�� ��� ����� �ݿ� �ܡܡܡ�
	private void commitNpc() {
		if (_calcType == PC_NPC) {
			if (_drainMana > 0) {
				int drainValue = _targetNpc.drainMana(_drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);

				if (drainValue > 0) {
					int newMp2 = _targetNpc.getCurrentMp() - drainValue;
					_targetNpc.setCurrentMpDirect(newMp2);
				}
			}
			// �ĸ��� ��� �߰� //
			if (_drainHp > 0) {
				int drainValue = _targetNpc.drainHp(_drainHp);
				int newHp = _pc.getCurrentHp() + drainValue;
				_pc.setCurrentHp(newHp);

				if (drainValue > 0) {
					int newHp2 = _targetNpc.getCurrentHp() - drainValue;
					_targetNpc.setCurrentHpDirect(newHp2);
				}
			}
			// �ĸ��� ��� �߰� //
			damageNpcWeaponDurability(); // ���⸦ �ջ��Ų��.
			_targetNpc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, _damage);
		}
	}

	/* ���������������� ī���� �ٸ��� ���������������� */

	// ����� ī���� �ٸ������ ���� ��� �۽� �����
	public void actionCounterBarrier() {

		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ
			_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.sendPackets(new S_SkillSound(_targetId, 4395));  // ī���� �踮�� ����Ʈ �߰� 
	        _pc.broadcastPacket(new S_SkillSound(_targetId, 4395)); //
		} else if (_calcType == NPC_PC) {
			int actId = 0;
			_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_Attack;
			}
			if (getGfxId() > 0) {
				_npc
						.broadcastPacket(new S_UseAttackSkill(_target, _npc
								.getId(), getGfxId(), _targetX, _targetY,
								actId, 0));
			} else {
				_npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
						actId));
			}
			_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
			_npc.broadcastPacket(new S_SkillSound(_targetId, 4395)); // ī���� �踮�� ����Ʈ �߰�
		}
	}

	// ����� ����� ���ݿ� ���ؼ� ī���� �ٸ�� ��ȿ�Ѱ��� �Ǻ� �����
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62) { // Ȱ�̳� ��Ʈ��Ʈ
				isShortDistance = false;
			}
		} else if (_calcType == NPC_PC) {
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// �Ÿ��� 2�̻�, �������� Ȱ�� �׼� ID�� �ִ� ���� ������
			if (isLongRange && bowActId > 0) {
				isShortDistance = false;
			}
		}
		return isShortDistance;
	}

	// ����� ī���� �ٸ����� �������� �ݿ� �����
	public void commitCounterBarrier() {
		int damage = calcCounterBarrierDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// �ܡܡܡ� ī���� �ٸ����� �������� ���� �ܡܡܡ�
	private int calcCounterBarrierDamage() {
		double damage = 0;  // �Ҽ��� ǥ���� ���� int �� double �� ..
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
			if (weapon.getItem().getType() == 3) { // ��հ�
			    // (BIG �ִ� ������+��ȭ��+�߰� ������)*1.2 
				damage = Math.round((weapon.getItem().getDmgLarge() + weapon
						.getEnchantLevel() + weapon.getItem()
								.getDmgModifier()) * 1.6);  // �Ҽ��� ǥ�� , Math.round �� ������ ����  �ݿø�!
			}
		}
		return (int)damage; // �Ҽ��� ǥ�������� int ������..
	}
	/* ���������������� ī���� �ٸ��� ���������������� */

	// ����� ��Ż�ٵ��� ���� ��� �۽� �����
	public void actionMotalbody() {
		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ
			_pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			_pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.sendPackets(new S_SkillSound(_targetId, 4395));  // ī���� �踮�� ����Ʈ �߰� 
	        _pc.broadcastPacket(new S_SkillSound(_targetId, 4395)); //
		} else if (_calcType == NPC_PC) {
			int actId = 0;
			_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // ���⼼Ʈ
			if (getActId() > 0) {
				actId = getActId();
			} else {
				actId = ActionCodes.ACTION_Attack;
			}
			if (getGfxId() > 0) {
				_npc
						.broadcastPacket(new S_UseAttackSkill(_target, _npc
								.getId(), getGfxId(), _targetX, _targetY,
								actId, 0));
			} else {
				_npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
						actId));
			}
			_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
			_npc.broadcastPacket(new S_SkillSound(_targetId, 4395)); // ī���� �踮�� ����Ʈ �߰�
		}
	}

	// ����� ����� ���ݿ� ���ؼ� ī���� �ٸ�� ��ȿ�Ѱ��� �Ǻ� �����
	public boolean isShortDistance2() {
		boolean isShortDistance2 = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62) { // Ȱ�̳� ��Ʈ��Ʈ
				isShortDistance2 = false;
			}
		} else if (_calcType == NPC_PC) {
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// �Ÿ��� 2�̻�, �������� Ȱ�� �׼� ID�� �ִ� ���� ������
			if (isLongRange && bowActId > 0) {
				isShortDistance2 = false;
			}
		}
		return isShortDistance2;
	}


	// ����� ��Ż�ٵ��� �������� �ݿ� �����
	public void commitMotalbody() {
		//int damage = calcMotalbodyDamage()/5;
		int damage = calcMotalbodyDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// �ܡܡܡ� ��Ż�ٵ� �������� ���� �ܡܡܡ�
	private int calcMotalbodyDamage() {
		int damage = 0;  
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		if (weapon != null) {
				damage = 30;
		}
		return damage; // �Ҽ��� ǥ�������� int ������..
	}
	/*
	 * ���⸦ �ջ��Ų��. ��NPC�� ���, �ջ� Ȯ���� 2%�� �Ѵ�.�ູ ����� 1%�� �Ѵ�.
	 */
	private void damageNpcWeaponDurability() {
		int chance = 2;
		int bchance = 1;

		/*
		 * �ջ����� �ʴ� NPC, �Ǽ�, �ջ����� �ʴ� ���� ���, SOF���� ��� �ƹ��͵� ���� �ʴ´�.
		 */
		if (_calcType != PC_NPC
				|| _targetNpc.getNpcTemplate().is_hard() == false
				|| _weaponType == 0 || weapon.getItem().get_canbedmg() == 0
				|| _pc.hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}
		// ����� ���⡤�������� ����
		if ((_weaponBless == 1 || _weaponBless == 2)
				&& ((_random.nextInt(100) + 1) < chance)) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
		// �ູ�� ����
		if (_weaponBless == 0 && ((_random.nextInt(100) + 1) < bchance)) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}
	 /**
	  * ���� ���� ���� �Ǵ�
	  * ����� ��ܰ����� �Ǵ�
	 */
	 /*private int Spot(L1PcInstance c){
	  int sTemp = 0;
	  /** 1�ܰ� */
	  /*if(c.hasSkillEffect(SPOT1)) sTemp = 1;
	  /** 2�ܰ� */
	  /*if(c.hasSkillEffect(SPOT2)) sTemp = 2;
	  return sTemp;
	 }*/ 
	/**
	  * ���� ���� ���� �Ǵ�
	  * ����� ��ܰ����� �Ǵ�
	 */
	 private int ChinSword(L1PcInstance c){
	  int sTemp = 0;
	  /** 1�ܰ� */
	  if(c.hasSkillEffect(ChinSword1)) sTemp = 1;
	  /** 2�ܰ� */
	  if(c.hasSkillEffect(ChinSword2)) sTemp = 2;
	  return sTemp;
	 }
	 
	

	/*
	 * �ٿ���ÿ� ���� ���⸦ �ջ��Ų��. �ٿ������ �ջ� Ȯ���� 4%
	 */
	private void damagePcWeaponDurability() {
		// PvP �̿�, �Ǽ�, Ȱ, �� ���� ��, ��밡 �ٿ��Źũ�̻��, SOF���� ��� �ƹ��͵� ���� �ʴ´�
		if (_calcType != PC_PC || _weaponType == 0 || _weaponType == 20
				|| _weaponType == 62
				|| _targetPc.hasSkillEffect(BOUNCE_ATTACK) == false
				|| _pc.hasSkillEffect(SOUL_OF_FLAME)) {
			return;
		}

		if (_random.nextInt(100) + 1 <= 4) {
			// \f1�����%0�� �ջ��߽��ϴ�.
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}
}
