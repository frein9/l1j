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

package l1j.server.server.model.Instance;

import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1EquipmentTimer;
import l1j.server.server.model.L1ItemOwnerTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.utils.BinaryOutputStream;

// Referenced classes of package l1j.server.server.model:
// L1Object, L1PcInstance

public class L1ItemInstance extends L1Object {
	private static Logger _log = Logger.getLogger(L1ItemInstance.class
			.getName());

	private static final long serialVersionUID = 1L;

	private int _count;

	private int _itemId;

	private L1Item _item;

	private boolean _isEquipped = false;

	private int _enchantLevel;

	private boolean _isIdentified = false;

	private int _durability;

	private int _chargeCount;

	private int _remainingTime;

	private Timestamp _lastUsed = null;

	private int _lastWeight;

	private final LastStatus _lastStatus = new LastStatus();

	private L1PcInstance _pc;
		
	private int _upacse;
	
	private int _upacselv;
	 
	private int _attrenchantLevel;

	private boolean _isRunning = false;

	private EnchantTimer _timer;

	public L1ItemInstance() {
		_count = 1;
		_enchantLevel = 0;
		_upacse = 0;
	}

		// 봉인하자
	private int bless;

	public void setBless(int bless){
		this.bless = bless;
	}public int getBless(){
		return bless;
	}

	public L1ItemInstance(L1Item item, int count) {
		this();
		setItem(item);
		setCount(count);
	}

	private int Lockitem;                   
    public void setLockitem(int Lockitem){
       this.Lockitem = Lockitem;
    }
    public int getLockitem(){
       return Lockitem;
   }
 
	/**
	 * 아이템이 확인(감정)이 끝난 상태일까를 돌려준다.
	 * 
	 * @return 확인이 끝난 상태라면 true, 미확인이라면 false.
	 */
	public boolean isIdentified() {
		return _isIdentified;
	}

	/**
	 * 아이템이 확인(감정)이 끝난 상태인지를 설정한다.
	 * 
	 * @param identified
	 *            확인이 끝난 상태라면 true, 미확인이라면 false.
	 */
	public void setIdentified(boolean identified) {
		_isIdentified = identified;
	}

	public String getName() {
		return _item.getName();
	}

	/**
	 * 아이템의 개수를 돌려준다.
	 * 
	 * @return 아이템의 개수
	 */
	public int getCount() {
		return _count;
	}

	/**
	 * 아이템의 개수를 설정한다.
	 * 
	 * @param count
	 *            아이템의 개수
	 */
	public void setCount(int count) {
		_count = count;
	}

	/**
	 * 아이템이 장비 되고 있을까를 돌려준다.
	 * 
	 * @return 아이템이 장비 되고 있으면 true, 장비되어 있지 않으면 false.
	 */
	public boolean isEquipped() {
		return _isEquipped;
	}

	/**
	 * 아이템이 장비 되고 있는지를 설정한다.
	 * 
	 * @param equipped
	 *            아이템이 장비 되고 있으면 true, 장비되어 있지 않으면 false.
	 */
	public void setEquipped(boolean equipped) {
		_isEquipped = equipped;
	}

	public L1Item getItem() {
		return _item;
	}

	public void setItem(L1Item item) {
		_item = item;
		_itemId = item.getItemId();
	}

	public int getItemId() {
		return _itemId;
	}

	public void setItemId(int itemId) {
		_itemId = itemId;
	}

	public boolean isStackable() {
		return _item.isStackable();
	}

	@Override
	public void onAction(L1PcInstance player) {
	}

	public int getEnchantLevel() {
		return _enchantLevel;
	}

	public void setEnchantLevel(int enchantLevel) {
		_enchantLevel = enchantLevel;
	}

	public int getUpacse() {
	    return _upacse;
	}
	 
	public void setUpacse(int upacse) {
	    _upacse = upacse;
	}
	public int getUpacselv() {
	    return _upacselv;
	}
	public void setUpacselv(int upacselv) {
	    _upacselv = upacselv;
	}

	public int getAttrEnchantLevel() {
		return _attrenchantLevel;
	}

	public void setAttrEnchantLevel(int attrenchantLevel) {
		_attrenchantLevel = attrenchantLevel;
	}
		
	public int get_gfxid() {
		return _item.getGfxId();
	}

	public int get_durability() {
		return _durability;
	}

	public int getChargeCount() {
		return _chargeCount;
	}

	public void setChargeCount(int i) {
		_chargeCount = i;
	}

	public int getRemainingTime() {
		return _remainingTime;
	}

	public void setRemainingTime(int i) {
		_remainingTime = i;
	}

	public void setLastUsed(Timestamp t) {
		_lastUsed = t;
	}

	public Timestamp getLastUsed() {
		return _lastUsed;
	}

	public int getLastWeight() {
		return _lastWeight;
	}

	public void setLastWeight(int weight) {
		_lastWeight = weight;
	}

	public int getMr() {
		int mr = _item.get_mdef();
		if (getItemId() == 20011 || getItemId() == 20110
				|| getItemId() == 120011|| getItemId() == 20702 
				|| getItemId() == 20706 || getItemId() == 20710 
				|| getItemId() == 20714 || getItemId() == 20716
				|| getItemId() == 20734 || getItemId() == 20738 
				|| getItemId() == 20742 || getItemId() == 20745
				|| getItemId() == 20717) {
			mr += getEnchantLevel();
		}
		if (getItemId() == 20056 || getItemId() == 120056 || getItemId() == 220056) {
			mr += getEnchantLevel() * 2;
		}
		if (getItemId() == 20078) {
			   mr += getEnchantLevel() * 3;
			  }
		return mr;
	}
	
	/*
	 * 내구성,0~127까지 -의 값은 허가하지 않는다.
	 */
	public void set_durability(int i) {
		if (i < 0) {
			i = 0;
		}

		if (i > 127) {
			i = 127;
		}
		_durability = i;
	}

	public int getWeight() {
		if (getItem().getWeight() == 0) {
			return 0;
		} else {
			return Math.max(getCount() * getItem().getWeight() / 1000, 1);
		}
	}
	
	
	/**
	 * 전회 DB에 보존했을 때의 아이템의 스테이터스를 격납하는 클래스
	 */
	public class LastStatus {
		public int count;

		public int itemId;

		public boolean isEquipped = false;

		public int enchantLevel;

		public boolean isIdentified = true;

		public int durability;

		public int chargeCount;

		public int remainingTime;
		
		public int upacse;

		public int attrenchantLevel;
	
		public Timestamp lastUsed = null;

		public void updateAll() {
			count = getCount();
			itemId = getItemId();
			isEquipped = isEquipped();
			isIdentified = isIdentified();
			enchantLevel = getEnchantLevel();
			durability = get_durability();
			chargeCount = getChargeCount();
			remainingTime = getRemainingTime();
			upacse = getUpacse();
			attrenchantLevel = getAttrEnchantLevel();
			lastUsed = getLastUsed();
		}

		public void updateCount() {
			count = getCount();
		}

		public void updateItemId() {
			itemId = getItemId();
		}

		public void updateEquipped() {
			isEquipped = isEquipped();
		}

		public void updateIdentified() {
			isIdentified = isIdentified();
		}

		public void updateEnchantLevel() {
			enchantLevel = getEnchantLevel();
		}
		
		public void updateUpacse() {
		    upacse = getUpacse();
		}
		
		public void updateAttrEnchantLevel() {
			attrenchantLevel = getAttrEnchantLevel();
		}
		
    	public void updateDuraility() {
			durability = get_durability();
		}

		public void updateChargeCount() {
			chargeCount = getChargeCount();
		}

		public void updateRemainingTime() {
			remainingTime = getRemainingTime();
		}

		public void updateLastUsed() {
			lastUsed = getLastUsed();
		}
	}

	    public LastStatus getLastStatus() {
		return _lastStatus;
	}

	/**
	 * 전회 DB에 보존했을 때로부터 변화하고 있는 컬럼을 비트 집합으로서 돌려준다.
	 */
	public int getRecordingColumns() {
		int column = 0;

		if (getCount() != _lastStatus.count) {
			column += L1PcInventory.COL_COUNT;
		}
		if (getItemId() != _lastStatus.itemId) {
			column += L1PcInventory.COL_ITEMID;
		}
		if (isEquipped() != _lastStatus.isEquipped) {
			column += L1PcInventory.COL_EQUIPPED;
		}
		if (getEnchantLevel() != _lastStatus.enchantLevel) {
			column += L1PcInventory.COL_ENCHANTLVL;
		}
		if (getUpacse() != _lastStatus.upacse) {
		    column += L1PcInventory.COL_UPACSE;
		}
		if (getAttrEnchantLevel() != _lastStatus.attrenchantLevel) {
			column += L1PcInventory.COL_ATTRENCHANTLVL;
		 }
		if (get_durability() != _lastStatus.durability) {
			column += L1PcInventory.COL_DURABILITY;
		}
		if (getChargeCount() != _lastStatus.chargeCount) {
			column += L1PcInventory.COL_CHARGE_COUNT;
		}
		if (getLastUsed() != _lastStatus.lastUsed) {
			column += L1PcInventory.COL_DELAY_EFFECT;
		}
		if (isIdentified() != _lastStatus.isIdentified) {
			column += L1PcInventory.COL_IS_ID;
		}
		if (getRemainingTime() != _lastStatus.remainingTime) {
			column += L1PcInventory.COL_REMAINING_TIME;
		}

		return column;
	}
	
	/**
	 * 가방이나 창고에서 표시되는 형식의 이름을 개수를 지정해 취득한다.<br>
	 */
	public String getNumberedViewName(int count) {
		StringBuilder name = new StringBuilder(getNumberedName(count));
		int itemType2 = getItem().getType2();
		int itemId = getItem().getItemId();

		if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
			L1Pet pet = PetTable.getInstance().getTemplate(getId());
			if (pet != null) {
				L1Npc npc = NpcTable.getInstance().getTemplate(pet.get_npcid());
				name.append("[Lv." + pet.get_level() + " " + pet.get_name()
						+ "]HP" + pet.get_hp() + " " + npc.get_nameid());
			}
		}

		if (getItem().getType2() == 0 && getItem().getType() == 2) { // light계 아이템
			if (isNowLighting()) {
				name.append(" ($10)");
			}
			if (itemId == 40001 || itemId == 40002) { // 램프 or랜턴
				if (getRemainingTime() <= 0) {
					name.append(" ($11)");
				}
			}
		}

		if (isEquipped()) {
			if (itemType2 == 1) {
				name.append(" ($9)"); // 장비(Armed)
			} else if (itemType2 == 2) {
				name.append(" ($117)"); // 장비(Worn)
			} else if (itemType2 == 0 && getItem().getType() == 11) { // petitem
				name.append(" ($117)"); // 장비(Worn)
			}
		}
		return name.toString();
	}

	/**
	 * 가방이나 창고에서 표시되는 형식의 이름을 돌려준다.<br>
	 * 예:+10 카타나 (장비)
	 */
	public String getViewName() {
		return getNumberedViewName(_count);
	}

	/**
	 * 로그에 표시되는 형식의 이름을 돌려준다.<br>
	 * 예:아데나(250) / +6 다가
	 */
	public String getLogName() {
		return getNumberedName(_count);
	}

	/**
	 * 로그에 표시되는 형식의 이름을, 개수를 지정해 취득한다.
	 */
	public String getNumberedName(int count) {
		StringBuilder name = new StringBuilder();

		if (isIdentified()) {
			if (getItem().getType2() == 1 || getItem().getType2() == 2) { // 무기·방어용 기구
		    if (!(getItem().getType2() == 2 && (getItem().getType() == 8 || getItem().getType() == 9 
			     || getItem().getType() == 10 || getItem().getType() == 11 || getItem().getType() == 12))) {
		    	switch(getAttrEnchantLevel()){
		        case 1: name.append("$6115"); break; //불의
		        case 2: name.append("$6116"); break; //폭발의
		        case 3: name.append("$6117"); break; //이그니스의
		        case 4: name.append("$6118"); break; //물의
		        case 5: name.append("$6119"); break; //해일의
		        case 6: name.append("$6120"); break; //운디네의
		        case 7: name.append("$6121"); break; //바람의
		        case 8: name.append("$6122"); break; //태풍의
		        case 9: name.append("$6123"); break; //실프의
		        case 10: name.append("$6124"); break; //대지의
		        case 11: name.append("$6125"); break; //파괴의
		        case 12: name.append("$6126"); break; //클레이의
		        default: break;
		        }
		    	if (getEnchantLevel() >= 0) { 
			      name.append("+" + getEnchantLevel() + " ");
			     } else if (getEnchantLevel() < 0) {
			      name.append(String.valueOf(getEnchantLevel()) + " ");
			     }
			    } 
			    if (getItem().getType2() == 2 && (getItem().getType() == 8 || getItem().getType() == 9 
			     || getItem().getType() == 10 || getItem().getType() == 11 || getItem().getType() == 12)) {
			     if (getUpacse() >= 0) {
			      name.append("+" + getUpacse() + " ");
			     }
			    }
			}
		}
		name.append(_item.getNameId());
		if (isIdentified()) {
			if (getItem().getMaxChargeCount() > 0) {
				name.append(" (" + getChargeCount() + ")");
			}
			if (getItem().getItemId() == 20383) { // 기마용 헤룸
				name.append(" (" + getChargeCount() + ")");
			}
			if (getItem().getMaxUseTime() > 0 && getItem().getType2() != 0) { // 무기 방어용 기구로 사용시간 제한 있어
				name.append(" (" + getRemainingTime() + ")");
			}
		}

		if (count > 1) {
			name.append(" (" + count + ")");
		}

		return name.toString();
	}

	/**
	 * 아이템 상태로부터 서버 패킷으로 이용하는 형식의 바이트열을 생성해, 돌려준다.
	 */
	public byte[] getStatusBytes() {
		int itemType2 = getItem().getType2();
		int itemId = getItemId();
		BinaryOutputStream os = new BinaryOutputStream();
		
		if (itemType2 == 0) { // etcitem
			switch (getItem().getType()) {
			   case 2: // light
			    os.writeC(22); // 밝음
			    os.writeH(getItem().getLightRange());
			    os.writeC(getItem().getMaterial());
			    os.writeD(getWeight());
			    break;
			   case 7: // food
			    os.writeC(21); // 영양
			    os.writeH(getItem().getFoodVolume());
			    os.writeC(getItem().getMaterial());
			    os.writeD(getWeight());
			    break;
			   case 0: // arrow
			   case 15: // sting
			    os.writeC(1); // 타격치
			    os.writeC(getItem().getDmgSmall());
			    os.writeC(getItem().getDmgLarge());
			    os.writeC(getItem().getMaterial());
			    os.writeD(getWeight());
			    break;
			   default:
			    os.writeC(23); // 재질
			    os.writeC(getItem().getMaterial());
			    os.writeD(getWeight());
			    break;
			   }
		
		} else if (itemType2 == 1 || itemType2 == 2) { // weapon | armor
			if (itemType2 == 1) { // weapon
				// 타격치
				os.writeC(1);
				os.writeC(getItem().getDmgSmall()); 
				os.writeC(getItem().getDmgLarge());
				os.writeC(getItem().getMaterial());
				os.writeD(getWeight());
			} else if (itemType2 == 2) { // armor
				// AC
				os.writeC(19); 
				int ac = ((L1Armor) getItem()).get_ac();
				if (ac < 0) {
					ac = ac - ac - ac;
				}
				os.writeC(ac);
				os.writeC(getItem().getMaterial());
				os.writeC(getItem().getSolidity()); // 방어구는 255 악세사리는 상0 중1 하2로 구분
				os.writeD(getWeight());
			}
			// 강화수
			if (getEnchantLevel() != 0) {
				os.writeC(2);
				os.writeC(getEnchantLevel());
			}
			// 손상도
			if (get_durability() != 0) {
				os.writeC(3);
				os.writeC(get_durability());
			}
			// 양손 무기
			if (getItem().isTwohandedWeapon()) {
				os.writeC(4);
			}
			// 공격 성공
			if (getItem().getHitModifier() != 0) {
				os.writeC(5);
				os.writeC(getItem().getHitModifier());
			}
			// 추가 타격
			if (getItem().getDmgModifier() != 0) {
				os.writeC(6);
				os.writeC(getItem().getDmgModifier());
			}
			// 사용 가능
			int bit = 0;
			bit |= getItem().isUseRoyal()   ?  1 : 0;
			bit |= getItem().isUseKnight()  ?  2 : 0;
			bit |= getItem().isUseElf()     ?  4 : 0;
			bit |= getItem().isUseMage()    ?  8 : 0;
			bit |= getItem().isUseDarkelf() ?  16 : 0;
			bit |= getItem().isUseDragonKnight() ? 32 : 0;
			bit |= getItem().isUseBlackwizard() ? 64 : 0;
			// bit |= getItem().isUseHiPet() ?  64 : 0; // 하이 펫
			os.writeC(7);
			os.writeC(bit);
			// 활명중율
			if (getItem().getBowHitRate() != 0) {
				os.writeC(24);
				os.writeC(getItem().getBowHitRate());
			}
			//활추가타격치 
			   if (getItem().getBowDmgModifier() != 0) {   
			    os.writeC(35);
			    os.writeC(getItem().getBowDmgModifier()); 
			   } 
			// MP흡수
			if (itemId == 126 || itemId == 127 
				|| itemId == 419 || itemId == 564 || itemId == 510) { // 마나스탓후, 강철의 마나스탓후
				os.writeC(16);
			}
			// HP흡수

			if (itemId == 512 || itemId == 568 
				|| itemId == 567 || itemId == 562) { // 파멸의 대검
		        os.writeC(34);
			}
		
			// STR~CHA
			if (getItem().get_addstr() != 0) {
				os.writeC(8);
				os.writeC(getItem().get_addstr());
			}
			if (getItem().get_adddex() != 0) {
				os.writeC(9);
				os.writeC(getItem().get_adddex());
			}
			if (getItem().get_addcon() != 0) {
				os.writeC(10);
				os.writeC(getItem().get_addcon());
			}
			if (getItem().get_addwis() != 0) {
				os.writeC(11);
				os.writeC(getItem().get_addwis());
			}
			if (getItem().get_addint() != 0) {
				os.writeC(12);
				os.writeC(getItem().get_addint());
			}
			if (getItem().get_addcha() != 0) {
				os.writeC(13);
				os.writeC(getItem().get_addcha());
			}
			//장신구업그레이드 
			   // HP, MP
			if (getUpacse() != 0 && getItem().getUpacselv() == 2) { //장신구 인챈 적용
			    os.writeC(14);
			    os.writeH(getItem().get_addhp() + (getUpacse() * 2));

			   } else if (getItem().get_addhp() != 0) {
			    os.writeC(14);
			    os.writeH(getItem().get_addhp());
			   }
		    if (getUpacse() != 0 && getItem().getUpacselv() == 3) { //장신구 인챈 적용
			    os.writeC(32);
			    os.writeC(getItem().get_addmp() + getUpacse());
			   } else if (getItem().get_addmp() != 0) {
			    os.writeC(32);
			    os.writeC(getItem().get_addmp());
			}
			
			// MR
		    if (getUpacse() >= 6 && getItem().getUpacselv() == 2){
		        os.writeC(15);
		        os.writeH(getMr() + (getUpacse() - 5));
		       }else if (getMr() != 0) {
				os.writeC(15);
				os.writeH(getMr());
			}
			// SP(마력)
			if (getUpacse() >= 6 && getItem().getUpacselv() == 3) {
			    os.writeC(17);
			    os.writeC(getItem().get_addsp() + (getUpacse()));
			} else if (getItem().get_addsp() != 0) {
				os.writeC(17);
				os.writeC(getItem().get_addsp());
			}
			// 헤이 파업
			if (getItem().isHasteItem()) {
				os.writeC(18);
			}
			 // 용기아이템 
			if (getItem().isBraveItem()) { 
			    os.writeC(18); 
			} 
		
			//장신구업그레이드
			   // 불의 속성
			   if (getUpacse() != 0 && getItem().getUpacselv() == 1) { //장신구 인챈 적용
				    os.writeC(27);
				    os.writeC(getItem().get_defense_fire() + getUpacse());
				   } else if (getItem().get_defense_fire() != 0) {
				    os.writeC(27);
				    os.writeC(getItem().get_defense_fire());
				   }
				   // 물의 속성
				   if (getUpacse() != 0 && getItem().getUpacselv() == 1) { //장신구 인챈 적용
				    os.writeC(28);
				    os.writeC(getItem().get_defense_water() + getUpacse());
				   }else if (getItem().get_defense_water() != 0) {
				    os.writeC(28);
				    os.writeC(getItem().get_defense_water());
				   }
				   // 바람의 속성
				   if (getUpacse() != 0 && getItem().getUpacselv() == 1) { //장신구 인챈 적용
				    os.writeC(29);
				    os.writeC(getItem().get_defense_wind() + getUpacse());
				   }else if (getItem().get_defense_wind() != 0) {
				    os.writeC(29);
				    os.writeC(getItem().get_defense_wind());
				   }
				   // 땅의 속성
				   if (getUpacse() != 0 && getItem().getUpacselv() == 1) { //장신구 인챈 적용
				    os.writeC(30);
				    os.writeC(getItem().get_defense_earth() + getUpacse());
				   }else if (getItem().get_defense_earth() != 0) {
				    os.writeC(30);
				    os.writeC(getItem().get_defense_earth());
				   }
			//장신구업그레이드 

			// 동결 내성
			if (getItem().get_regist_freeze() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_freeze());
				os.writeC(33);
				os.writeC(1);
			}
			// 석유화학 내성
			if (getItem().get_regist_stone() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_stone());
				os.writeC(33);
				os.writeC(2);
			}
			// 수면 내성
			if (getItem().get_regist_sleep() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_sleep());
				os.writeC(33);
				os.writeC(3);
			}
			// 어두운 곳 내성
			if (getItem().get_regist_blind() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_blind());
				os.writeC(33);
				os.writeC(4);
			}
			// 스탠 내성
			if (getItem().get_regist_stun() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_stun());
				os.writeC(33);
				os.writeC(5);
			}
			// hold 내성
			if (getItem().get_regist_sustain() != 0) {
				os.writeC(15);
				os.writeH(getItem().get_regist_sustain());
				os.writeC(33);
				os.writeC(6);
			}
			// 행운
        /*  if (getItem.getLuck() != 0) {
                os.writeC(20);
                os.writeC(val);
             }
			// 종류
            if (getItem.getDesc() != 0) {
                os.writeC(25);
                os.writeH(val); // desc.tbl ID
             }
			// 레벨
            if (getItem.getLevel() != 0) {
                os.writeC(26);
                os.writeH(val);
             } */
		}
		return os.getBytes();
	}

class EnchantTimer extends TimerTask {

	public EnchantTimer() {
	}

	@Override
	public void run() {
		try {
			int type = getItem().getType();
			int type2 = getItem().getType2();
			int itemId = getItem().getItemId();
			if (_pc != null && _pc.getInventory().checkItem(itemId)) {
				if (type == 2 && type2 == 2 && isEquipped()) {
					_pc.addAc(3);
					_pc.sendPackets(new S_OwnCharStatus(_pc));
				}
			}
			setAcByMagic(0);
			setDmgByMagic(0);
			setHolyDmgByMagic(0);
			setHitByMagic(0);
			_pc.sendPackets(new S_ServerMessage(308, getLogName()));
			_isRunning = false;
			_timer = null;
		} catch (Exception e) {
		}
	}
}
public void stopEnchantTimer() {  // 블래스트아머 / 인챈트 웨폰 / 쉐도우팽 버그픽스 추가
	if (_timer != null) {
	setAcByMagic(0);
	setDmgByMagic(0);
	setHolyDmgByMagic(0);
	setHitByMagic(0);
	_pc.sendPackets(new S_ServerMessage(308, getLogName()));
	_isRunning = false;
	_timer = null;
	}
	} 

	private int _acByMagic = 0;

	public int getAcByMagic() {
		return _acByMagic;
	}

	public void setAcByMagic(int i) {
		_acByMagic = i;
	}

	private int _dmgByMagic = 0;

	public int getDmgByMagic() {
		return _dmgByMagic;
	}

	public void setDmgByMagic(int i) {
		_dmgByMagic = i;
	}

	private int _holyDmgByMagic = 0;

	public int getHolyDmgByMagic() {
		return _holyDmgByMagic;
	}

	public void setHolyDmgByMagic(int i) {
		_holyDmgByMagic = i;
	}

	private int _hitByMagic = 0;

	public int getHitByMagic() {
		return _hitByMagic;
	}

	public void setHitByMagic(int i) {
		_hitByMagic = i;
	}

	public void setSkillArmorEnchant(L1PcInstance pc, int skillId, int skillTime) {
		int type = getItem().getType();
		int type2 = getItem().getType2();
		if (_isRunning) {
			_timer.cancel();
			int itemId = getItem().getItemId();
			if (pc != null && pc.getInventory().checkItem(itemId)) {
				if (type == 2 && type2 == 2 && isEquipped()) {
					pc.addAc(3);
					pc.sendPackets(new S_OwnCharStatus(pc));
				}
			}
			setAcByMagic(0);
			_isRunning = false;
			_timer = null;
		}

		if (type == 2 && type2 == 2 && isEquipped()) {
			pc.addAc(-3);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
		setAcByMagic(3);
		_pc = pc;
		_timer = new EnchantTimer();
		(new Timer()).schedule(_timer, skillTime);
		_isRunning = true;
	}

	public void setSkillWeaponEnchant(L1PcInstance pc, int skillId,
			int skillTime) {
		if (getItem().getType2() != 1) {
			return;
		}
		if (_isRunning) {
			_timer.cancel();
			setDmgByMagic(0);
			setHolyDmgByMagic(0);
			setHitByMagic(0);
			_isRunning = false;
			_timer = null;
		}

		switch(skillId) {
			case L1SkillId.HOLY_WEAPON:
				setHolyDmgByMagic(1);
				setHitByMagic(1);
				break;

			case L1SkillId.ENCHANT_WEAPON:
				setDmgByMagic(2);
				break;

			case L1SkillId.BLESS_WEAPON:
				setDmgByMagic(2);
				setHitByMagic(2);
				break;

			case L1SkillId.SHADOW_FANG:
				setDmgByMagic(5);
				break;

			default:
				break;
		}

		_pc = pc;
		_timer = new EnchantTimer();
		(new Timer()).schedule(_timer, skillTime);
		_isRunning = true;
	}

	private int _itemOwnerId = 0;

	public int getItemOwnerId() {
	return _itemOwnerId;
	}

	public void setItemOwnerId(int i) {
		_itemOwnerId = i;
	}

	public void startItemOwnerTimer(L1PcInstance pc) {
		setItemOwnerId(pc.getId());
		L1ItemOwnerTimer timer = new L1ItemOwnerTimer(this, 10000);
		timer.begin();
	}

	private L1EquipmentTimer _equipmentTimer;

	public void startEquipmentTimer(L1PcInstance pc) {
		if (getRemainingTime() > 0) {
			_equipmentTimer = new L1EquipmentTimer(pc, this);
			Timer timer = new Timer(true);
			timer.scheduleAtFixedRate(_equipmentTimer, 1000, 1000);
		}
	}

	public void stopEquipmentTimer(L1PcInstance pc) {
		if (getRemainingTime() > 0) {
			_equipmentTimer.cancel();
			_equipmentTimer = null;
		}
	}

	private boolean _isNowLighting = false;

	public boolean isNowLighting() {
	return _isNowLighting;
	}

	public void setNowLighting(boolean flag) {
		_isNowLighting = flag;
	}
	public boolean get_pcDrop() {
		// TODO Auto-generated method stub
		return false;
	}
}
