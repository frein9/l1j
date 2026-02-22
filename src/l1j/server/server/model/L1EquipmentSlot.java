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
package l1j.server.server.model;

import java.util.ArrayList;

import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1EquipmentTimer;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.templates.L1Item;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1EquipmentSlot {
	private L1PcInstance _owner;

	/**
	 * 효과중세트 아이템
	 */
	private ArrayList<L1ArmorSet> _currentArmorSet;

	private L1ItemInstance _weapon;
	private ArrayList<L1ItemInstance> _armors;

	public L1EquipmentSlot(L1PcInstance owner) {
		_owner = owner;

		_armors = new ArrayList<L1ItemInstance>();
		_currentArmorSet = new ArrayList<L1ArmorSet>();
	}

	private void setWeapon(L1ItemInstance weapon) {
		_owner.setWeapon(weapon);
		_owner.setCurrentWeapon(weapon.getItem().getType1());
		weapon.startEquipmentTimer(_owner);
		_weapon = weapon;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	private void setArmor(L1ItemInstance armor) {
		L1Item item = armor.getItem();
		int itemId = armor.getItem().getItemId();

		_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor
				.getAcByMagic());
		_owner.addDamageReductionByArmor(item.getDamageReduction());
		_owner.addWeightReduction(item.getWeightReduction());
		_owner.addBowHitRate(item.getBowHitRate());
		_owner.addBowDmgModifier(item.getBowDmgModifier());
		//장신구업그레이드 
		if (armor.getUpacse() != 0 && armor.getItem().getUpacselv() == 1){
			  _owner.addEarth(item.get_defense_earth() + armor.getUpacse());
			  _owner.addWind(item.get_defense_wind() + armor.getUpacse());
			  _owner.addWater(item.get_defense_water() + armor.getUpacse());
			  _owner.addFire(item.get_defense_fire() + armor.getUpacse());
		} else {
			  _owner.addEarth(item.get_defense_earth());
			  _owner.addWind(item.get_defense_wind());
			  _owner.addWater(item.get_defense_water());
			  _owner.addFire(item.get_defense_fire());
			  _owner.addRegistStun(item.get_regist_stun()); 
			  _owner.addRegistStone(item.get_regist_stone());
			  _owner.addRegistSleep(item.get_regist_sleep()); 
			  _owner.add_regist_freeze(item.get_regist_freeze()); 
			  _owner.addRegistSustain(item.get_regist_sustain()); 
			  _owner.addRegistBlind(item.get_regist_blind()); 
		}
		//장신구업그레이드 

		_armors.add(armor);

		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId) && armorSet.isValid(_owner)) {
				if (armor.getItem().getType2() == 2
						&& armor.getItem().getType() == 9) { // ring
					if (!armorSet.isEquippedRingOfArmorSet(_owner)) {
						armorSet.giveEffect(_owner);
						_currentArmorSet.add(armorSet);
					}
				} else {
					armorSet.giveEffect(_owner);
					_currentArmorSet.add(armorSet);
				}
			}
		}

		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			if (!_owner.hasSkillEffect(L1SkillId.INVISIBILITY)) {
				_owner.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
				_owner.setSkillEffect(L1SkillId.INVISIBILITY, 0);
				_owner.sendPackets(new S_Invis(_owner.getId(), 1));
				L1World.getInstance().broadcastPacketToAll(new S_Invis(_owner.getId(), 1)); // 추가
				//_owner.broadcastPacket(new S_RemoveObject(_owner));
			}
		}
		if (itemId == 20288) { // ROTC
			_owner.sendPackets(new S_Ability(1, true));
		}
		if (itemId == 20383) { // 기마용 헤룸
			if (armor.getChargeCount() != 0) {
				armor.setChargeCount(armor.getChargeCount() - 1);
				_owner.getInventory().updateItem(armor, L1PcInventory
						.COL_CHARGE_COUNT);
			}
		}
		armor.startEquipmentTimer(_owner);
	}

	public ArrayList<L1ItemInstance> getArmors() {
		return _armors;
	}

	private void removeWeapon(L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		_owner.setWeapon(null);
		_owner.setCurrentWeapon(0);
		weapon.stopEquipmentTimer(_owner);
		_weapon = null;
		if (_owner.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
			_owner.removeSkillEffect(L1SkillId.COUNTER_BARRIER);
		}
	}

	private void removeArmor(L1ItemInstance armor) {
		L1Item item = armor.getItem();
		int itemId = armor.getItem().getItemId();

		_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor
				.getAcByMagic()));
		_owner.addDamageReductionByArmor(-item.getDamageReduction());
		_owner.addWeightReduction(-item.getWeightReduction());
		_owner.addBowHitRate(-item.getBowHitRate());
		_owner.addBowDmgModifier(-item.getBowDmgModifier());
		//장신구업그레이드 
		if (armor.getUpacse() != 0 && armor.getItem().getUpacselv() == 1){
			  _owner.addEarth(-item.get_defense_earth() - armor.getUpacse());
			  _owner.addWind(-item.get_defense_wind() - armor.getUpacse());
			  _owner.addWater(-item.get_defense_water() - armor.getUpacse());
			  _owner.addFire(-item.get_defense_fire() - armor.getUpacse());
		 } else {
			  _owner.addEarth(-item.get_defense_earth());
			  _owner.addWind(-item.get_defense_wind());
			  _owner.addWater(-item.get_defense_water());
			  _owner.addFire(-item.get_defense_fire());
			  _owner.addRegistStun(-item.get_regist_stun());  
			  _owner.addRegistStone(-item.get_regist_stone()); 
			  _owner.addRegistSleep(-item.get_regist_sleep());  
			  _owner.add_regist_freeze(-item.get_regist_freeze());
			  _owner.addRegistSustain(-item.get_regist_sustain());
			  _owner.addRegistBlind(-item.get_regist_blind()); 
		}
		//장신구업그레이드 

		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId)
					&& _currentArmorSet.contains(armorSet)
					&& !armorSet.isValid(_owner)) {
				armorSet.cancelEffect(_owner);
				_currentArmorSet.remove(armorSet);
			}
		}

		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			_owner.delInvis(); // 인비지비리티 상태 해제
		}
		if (itemId == 20288) { // ROTC
			_owner.sendPackets(new S_Ability(1, false));
		}
		armor.stopEquipmentTimer(_owner);

		_armors.remove(armor);
	}

	public void set(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();
		if (item.getType2() == 0) {
			return;
		}

		//장신구업그레이드 
		if (equipment.getUpacse() != 0 && equipment.getItem().getUpacselv() == 2){
			  _owner.addMaxHp(item.get_addhp() + (equipment.getUpacse() * 2));
			  } else {
			  _owner.addMaxHp(item.get_addhp());
		}
		if (equipment.getUpacse() != 0 && equipment.getItem().getUpacselv() == 3){
			  _owner.addMaxMp(item.get_addmp() + equipment.getUpacse());
			  } else {
			  _owner.addMaxMp(item.get_addmp());
		}
		//장신구업그레이드 
		_owner.addStr(item.get_addstr());
		_owner.addCon(item.get_addcon());
		_owner.addDex(item.get_adddex());
		_owner.addInt(item.get_addint());
		_owner.addWis(item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.addCha(item.get_addcha());

		int addMr = 0;
		addMr += equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr += 5;
		}
		if (addMr != 0) {
			_owner.addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		else if (equipment.getUpacse() >= 6 && equipment.getItem().getUpacselv() == 2) {
		     _owner.addMr(addMr + (equipment.getUpacse() - 5));
		     _owner.sendPackets(new S_SPMR(_owner));
		   }
		//장신구업그레이드 
		  if (item.get_addsp() != 0) {
		   _owner.addSp(item.get_addsp());
		   _owner.sendPackets(new S_SPMR(_owner));
		  } else if (equipment.getUpacse() >= 6 && equipment.getItem().getUpacselv() == 3) {
		   _owner.addSp(item.get_addsp() + (equipment.getUpacse() - 5));
		   _owner.sendPackets(new S_SPMR(_owner));
		  }
		//장신구업그레이드 

		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(1);
			_owner.removeHasteSkillEffect();
			if (_owner.getMoveSpeed() != 1) {
				_owner.setMoveSpeed(1);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
				_owner.broadcastPacket(new S_SkillHaste(_owner.getId(), 1, 0));
			}
		}
		  if (item.isBraveItem()){//용기아이템 
			  int type = 0; 
			  if (_owner.isElf()){ 
			    type = 3; 
			  }else{ 
			    type = 1; 
			  } 
			  _owner.addBraveItemEquipped(1); 
			  _owner.removeBraveSkillEffect(); 
			  if (_owner.getBraveSpeed() < 1) { 
			    _owner.setBraveSpeed(1); 
			    _owner.sendPackets(new S_SkillBrave(_owner.getId(), type, -1)); 
			    _owner.broadcastPacket(new S_SkillBrave(_owner.getId(), type, 0)); 
			  } 
			  } 

		if (item.getItemId() == 20383) { // 기마용 헤룸
			if (_owner.hasSkillEffect(STATUS_BRAVE)) {
				_owner.killSkillEffectTimer(STATUS_BRAVE);
				_owner.sendPackets(new S_SkillBrave(_owner.getId(), 0, 0));
				_owner.broadcastPacket(new S_SkillBrave(_owner.getId(), 0, 0));
				_owner.setBraveSpeed(0);
			}
		}
 		_owner.getEquipSlot().setMagicHelm(equipment);

		if (item.getType2() == 1) {
			setWeapon(equipment);
		} else if (item.getType2() == 2) {
			setArmor(equipment);
			_owner.sendPackets(new S_SPMR(_owner));
		}
	}

	public void remove(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();
		if (item.getType2() == 0) {
			return;
		}

		//장신구업그레이드 
		if (equipment.getUpacse() != 0 && equipment.getItem().getUpacselv() == 2){
			  _owner.addMaxHp(-item.get_addhp() - (equipment.getUpacse() * 2));
			  } else {
			  _owner.addMaxHp(-item.get_addhp());
		}
		if (equipment.getUpacse() != 0 && equipment.getItem().getUpacselv() == 3){
			  _owner.addMaxMp(-item.get_addmp() - equipment.getUpacse());
			  } else {
			  _owner.addMaxMp(-item.get_addmp());
		}
		//장신구업그레이드 
		_owner.addStr((byte) -item.get_addstr());
		_owner.addCon((byte) -item.get_addcon());
		_owner.addDex((byte) -item.get_adddex());
		_owner.addInt((byte) -item.get_addint());
		_owner.addWis((byte) -item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.addCha((byte) -item.get_addcha());

		int addMr = 0;
		addMr -= equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr -= 5;
		}
		if (addMr != 0) {
			_owner.addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		else if (equipment.getUpacse() >= 6 && equipment.getItem().getUpacselv() == 2) {
		     _owner.addMr(addMr + (equipment.getUpacse() - 5));
		     _owner.sendPackets(new S_SPMR(_owner));
		   }
		//장신구업그레이드 
		if (item.get_addsp() != 0) {
		   _owner.addSp(-item.get_addsp());
		   _owner.sendPackets(new S_SPMR(_owner));
		} else if (equipment.getUpacse() >= 6 && equipment.getItem().getUpacselv() == 3) {
		   _owner.addSp(-(item.get_addsp() + (equipment.getUpacse() - 5)));
		   _owner.sendPackets(new S_SPMR(_owner));
		}
		//장신구업그레이드 

		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(-1);
			if (_owner.getHasteItemEquipped() == 0) {
				_owner.setMoveSpeed(0);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 0, 0));
				_owner.broadcastPacket(new S_SkillHaste(_owner.getId(), 0, 0));
			}
		}
		  if (item.isBraveItem()){//용기아이템  
			  _owner.addBraveItemEquipped(-1); 
			  if (_owner.getBraveItemEquipped() == 0) { 
			    _owner.setBraveSpeed(0); 
			    _owner.sendPackets(new S_SkillBrave(_owner.getId(), 0, 0)); 
			    _owner.broadcastPacket(new S_SkillBrave(_owner.getId(), 0, 0)); 
			  } 
			  } 

		_owner.getEquipSlot().removeMagicHelm(_owner.getId(), equipment);

		if (item.getType2() == 1) {
			removeWeapon(equipment);
		} else if (item.getType2() == 2) {
			removeArmor(equipment);
		}
	}

	public void setMagicHelm(L1ItemInstance item) {
		if (item.getItemId() == 20013) {
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		if (item.getItemId() == 20014) {
			_owner.sendPackets(new S_AddSkill(1, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		if (item.getItemId() == 20015) {
			_owner.sendPackets(new S_AddSkill(0, 24, 0, 0, 0, 2, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		if (item.getItemId() == 20008) {
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		if (item.getItemId() == 20023) {
			_owner.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
		}
		if (item.getItemId() == 20754) {
			_owner.sendPackets(new S_AddSkill(1, 24, 4, 2, 0, 6, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
	}

	public void removeMagicHelm(int objectId, L1ItemInstance item) {
		if (item.getItemId() == 20013) { // 마법의 헤룸：신속
			if (!SkillsTable.getInstance().spellCheck(objectId, 26)) { // 피지컬 엔챤트：DEX
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 43)) { // 헤이 파업
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20014) { 
			if (!SkillsTable.getInstance().spellCheck(objectId, 1)) { 
				_owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 19)) { // 엑스트라 힐
				_owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20015) {
			if (!SkillsTable.getInstance().spellCheck(objectId, 12)) { 
				_owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 13)) { // 디 텍 숀
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 42)) { // 피지컬 엔챤트：STR
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20008) {
			if (!SkillsTable.getInstance().spellCheck(objectId, 43)) { 
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20023) {
			if (!SkillsTable.getInstance().spellCheck(objectId, 54)) {
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20754) {
			if (!SkillsTable.getInstance().spellCheck(objectId, 26)) { 
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 43)) { 
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 1)) { 
				_owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 19)) { // 엑스트라 힐
				_owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 12)) { 
				_owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 13)) { // 디 텍 숀
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!SkillsTable.getInstance().spellCheck(objectId, 42)) { // 피지컬 엔챤트：STR
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
	}
}

