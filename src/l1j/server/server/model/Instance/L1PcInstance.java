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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Timestamp;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.GMCommands;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.PacketOutput;
import l1j.server.server.WarTimeController;
import l1j.server.server.command.executor.L1HpBar;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.HpRegeneration;
import l1j.server.server.model.AcceleratorChecker;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ChatParty;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1DwarfForElfInventory;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.MpRegeneration;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.HpRegenerationByDoll;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.L1GameTimeCarrier;
import l1j.server.server.model.monitor.L1PcAutoUpdate;
import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcHellMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_Exp;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Emblem;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.serverpackets.S_SkillIconExp;
import l1j.server.server.serverpackets.S_ShowKarma;
import l1j.server.server.model.L1PartyRefresh;
// Referenced classes of package l1j.server.server.model:
// L1Character, L1DropTable, L1Object, L1ItemInstance,
// L1World
//

public class L1PcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	public static final int CLASSID_DRAGONKNIGHT_MALE = 6658;
	public static final int CLASSID_DRAGONKNIGHT_FEMALE = 6661;
	public static final int CLASSID_BLACKWIZARD_MALE = 6671;
	public static final int CLASSID_BLACKWIZARD_FEMALE = 6650;	

	private short _hpr = 0;
	private short _trueHpr = 0;

	// 로또 수동 입력때문에..
	private boolean isLotto;
	public boolean isLotto() {
		return isLotto;
	}
	public void setLotto(boolean isLotto) {
		this.isLotto = isLotto;
	}
	// 분배파티 여부 - ACE
	private boolean AutoDivision;
	public boolean getAutoDivision() {
		return AutoDivision;
	}
	public void setAutoDivision(boolean AutoDivision) {
		this.AutoDivision = AutoDivision;
	}

/*	private boolean Petrace;
	public boolean isPetrace() {
		return Petrace;
	}
	public void setPetrace(boolean Petrace) {
		this.Petrace = Petrace;
	} */
	
	// 임시 펫 레이스 
	private boolean isPetRace;
	public void setPetrace(boolean a){
		this.isPetRace = a;
	}
	public boolean getPetrace(){
		return isPetRace;
	}

	public short getHpr() {
		return _hpr;
	}

	public void addHpr(int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMpr() {
		return _mpr;
	}

	public void addMpr(int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}
	public short _originalHpr = 0; // ● 오리지날 CON HPR

	public short getOriginalHpr() {

		return _originalHpr;
	}

	public short _originalMpr = 0; // ● 오리지날 WIS MPR

	public short getOriginalMpr() {

		return _originalMpr;
	}
	public void startHpRegeneration() {
		final int INTERVAL = 1000;

		if (!_hpRegenActive) {
			_hpRegen = new HpRegeneration(this);
			_regenTimer.scheduleAtFixedRate(_hpRegen, INTERVAL, INTERVAL);
			_hpRegenActive = true;
		}
	}
	
	public void startHpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 60000;
		boolean isExistHprDoll = false;
		Object[] dollList = getDollList().values().toArray();
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			if (doll.isHpRegeneration()) {
				isExistHprDoll = true;
			}
		}
		if (!_hpRegenActiveByDoll && isExistHprDoll) {
			_hpRegenByDoll = new HpRegenerationByDoll(this);
			_regenTimer.scheduleAtFixedRate(_hpRegenByDoll, INTERVAL_BY_DOLL,
					INTERVAL_BY_DOLL);
			_hpRegenActiveByDoll = true;
		}
	}

	public void stopHpRegeneration() {
		if (_hpRegenActive) {
			_hpRegen.cancel();
			_hpRegen = null;
			_hpRegenActive = false;
		}
	}
	
	public void stopHpRegenerationByDoll() {
		if (_hpRegenActiveByDoll) {
			_hpRegenByDoll.cancel();
			_hpRegenByDoll = null;
			_hpRegenActiveByDoll = false;
		}
	}

	public void startMpRegeneration() {
		final int INTERVAL = 1000;

		if (!_mpRegenActive) {
			_mpRegen = new MpRegeneration(this);
			_regenTimer.scheduleAtFixedRate(_mpRegen, INTERVAL, INTERVAL);
			_mpRegenActive = true;
		}
	}

	public void startMpRegenerationByDoll() {
		final int INTERVAL_BY_DOLL = 60000;
		boolean isExistMprDoll = false;
		Object[] dollList = getDollList().values().toArray();
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			if (doll.isMpRegeneration()) {
				isExistMprDoll = true;
			}
		}
		if (!_mpRegenActiveByDoll && isExistMprDoll) {
			_mpRegenByDoll = new MpRegenerationByDoll(this);
			_regenTimer.scheduleAtFixedRate(_mpRegenByDoll, INTERVAL_BY_DOLL,
					INTERVAL_BY_DOLL);
			_mpRegenActiveByDoll = true;
		}
	}

	public void stopMpRegeneration() {
		if (_mpRegenActive) {
			_mpRegen.cancel();
			_mpRegen = null;
			_mpRegenActive = false;
		}
	}

	public void stopMpRegenerationByDoll() {
		if (_mpRegenActiveByDoll) {
			_mpRegenByDoll.cancel();
			_mpRegenByDoll = null;
			_mpRegenActiveByDoll = false;
		}
	}

	public void startObjectAutoUpdate() {
		removeAllKnownObjects();
		_autoUpdateFuture = GeneralThreadPool.getInstance()
				.pcScheduleAtFixedRate(new L1PcAutoUpdate(getId()), 0L,
						INTERVAL_AUTO_UPDATE);
	}
	
	//파티창 관련 추가사항 시작
	private boolean _rpActive = false; // 파티
	L1PartyRefresh _rp;
	public void startRP() {
		int INTERVAL = 25000;
		if (!_rpActive) {
			_rp = new L1PartyRefresh(this);
			_regenTimer.scheduleAtFixedRate(_rp, INTERVAL, INTERVAL);
			_rpActive = true;
		}
	}

	public void stopRP() {
		if (_rpActive) {
			_rp.cancel();
			_rp = null;
			_rpActive = false;
		}
	}

// 파티창관련 추가사항 끝
	
	/*뒤로 밀릴 좌표값 지정

	 * 캐릭터의 해딩좌표값별로 뒤로 1칸 지정.. 짱돌 2009 - 08 -26


	 */

/*	 public void bkteleport() {
	  int nx = getX();
	  int ny = getY();
	  int aaa = getHeading();
	  switch (aaa) {
	  case 1:
	   nx += -1;
	   ny += 1;
	   break;
	  case 2:
	   nx += -1;
	   ny += 0;
	   break;
	  case 3:
	   nx += -1;
	   ny += -1;
	   break;
	  case 4:
	   nx += 0;
	   ny += -1;
	   break;
	  case 5:
	   nx += 1;
	   ny += -1;
	   break;
	  case 6:
	   nx += 1;
	   ny += 0;
	   break;
	  case 7:
	   nx += 1;
	   ny += 1;
	   break;
	  case 0:
	   nx += 0;
	   ny += 1;
	   break;
	  default:
	   break;
	  }
	  L1Teleport.teleport(this, nx, ny, getMapId(), aaa, false);
	 } */
	/* 추가부분
	CharacterTable 에서 받아온 레벨 값으로 새로 레벨갱신을 합니다. 
	레벨은 새로 세팅되며, 구 레벨과 현 레벨의 차이를 통해 피와 엠을 깍습니다. 
	레벨다운 버그에 대한 부분만 세팅한 것이므로 버그가 있다면
	wi연구소 - 가니 에게 문의 바랍니다.*/
	 public void CheckChangeExp() {
	  int level = ExpTable.getLevelByExp(getExp());
	  int char_level = CharacterTable.getInstance().PcLevelInDB(getId());
	  if(char_level == 0){ // 0이라면..에러겟지?
	   return; // 그럼 그냥 리턴
	  }
	  int gap = level - char_level;
	  if (gap == 0) {
	   // sendPackets(new S_OwnCharStatus(this));
	   sendPackets(new S_Exp(this));
	   return;
	  }

	  // 레벨이 변화했을 경우
	  if (gap > 0) {
	   levelUp(gap);
	  } else if (gap < 0) {
	   levelDown(gap);
	  }
	 }
	//추가부분


	/**
	 * 각종 모니터 태스크를 정지한다.
	 */
	public void stopEtcMonitor() {
		if (_autoUpdateFuture != null) {
			_autoUpdateFuture.cancel(true);
			_autoUpdateFuture = null;
		}
		if (_expMonitorFuture != null) {
			_expMonitorFuture.cancel(true);
			_expMonitorFuture = null;
		}
		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	private static final long INTERVAL_AUTO_UPDATE = 300;
	private ScheduledFuture<? > _autoUpdateFuture;

	private static final long INTERVAL_EXP_MONITOR = 500;
	private ScheduledFuture<? > _expMonitorFuture;

	public void onChangeExp() {
		int level = ExpTable.getLevelByExp(getExp());
		int char_level = getLevel();
		int gap = level - char_level;
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			sendPackets(new S_OwnCharStatus(this));
			return;
		}

		// 레벨이 변화했을 경우
		if (gap > 0) {
			levelUp(gap);
		} else if (gap < 0) {
			levelDown(gap);
		}
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		//if (isGmInvis() || isGhost() || isInvisble()) {
		//	return;
		//}

		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_OtherCharPacks(this)); // 자신의 정보를 보낸다
		if (isInParty() && getParty().isMember(perceivedFrom)) { // PT멤버라면 HP미터도 보낸다
			perceivedFrom.sendPackets(new S_HPMeter(this));
		}

		if (isPrivateShop()) {
			perceivedFrom.sendPackets(new S_DoActionShop(getId(),
					ActionCodes.ACTION_Shop, getShopChat()));
		}

		if (isCrown()) { // 군주
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (getId() == clan.getLeaderId() // 혈맹 주요해 성주 크란
						&& clan.getCastleId() != 0) {
					perceivedFrom.sendPackets(new S_CastleMaster(clan
							.getCastleId(), getId()));
				}
			}
		}
	}

	// 범위외가 된 인식이 끝난 오브젝트를 제거
	private void removeOutOfRangeObjects() {
		for (L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // 화면외
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	// 오브젝트 인식 처리
	public void updateObject() {
		removeOutOfRangeObjects();

		// 인식 범위내의 오브젝트 리스트를 작성
		for (L1Object visible : L1World.getInstance().getVisibleObjects(this,
				Config.PC_RECOGNIZE_RANGE)) {
			if (!knownsObject(visible)) {
				visible.onPerceive(this);
			} else {
				if (visible instanceof L1NpcInstance) {
					L1NpcInstance npc = (L1NpcInstance) visible;
					if (getLocation().isInScreen(npc.getLocation())
							&& npc.getHiddenStatus() != 0) {
						npc.approachPlayer(this);
					}
				}
			}
			if (hasSkillEffect(L1SkillId.GMSTATUS_HPBAR)
					&& L1HpBar.isHpBarTarget(visible)) {
				sendPackets(new S_HPMeter((L1Character) visible));
			}
		}
	}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) { // 독상태
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) { // 마비 상태
			// 마비 효과를 우선해 보내고 싶기 때문에, poisonId를 덧쓰기.
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) { // 이 if는 필요없을지도 모른다
			sendPackets(new S_Poison(getId(), poisonId));
			broadcastPacket(new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			sendPackets(new S_Emblem(clan.getClanId()));
		}

		if (getClanid() != 0) { // 크란 소속
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId() && // 프린스 또는 프린세스, 한편, 혈맹 주요해 자크란이 성주
						clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}

		sendVisualEffect();
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) { // liquor로 취하고 있다
			sendPackets(new S_Liquor(getId()));
		}

		sendVisualEffect();
	}

	public L1PcInstance() {
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		_dwarf = new L1DwarfInventory(this);
		_dwarfForElf = new L1DwarfForElfInventory(this);
		_tradewindow = new L1Inventory();
		_bookmarks = new ArrayList<L1BookMark>();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this); // constructor　 　 으로 this 포인터를 건네주는 것은 안전할 것일까···
	}

	@Override
	public void setCurrentHp(int i) {
		if (getCurrentHp() == i) {
			return;
		}
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
		sendPackets(new S_HPUpdate(currentHp, getMaxHp()));
		if (isInParty()) { // 파티중
			getParty().updateMiniHP(this);
		}
	}

	@Override
	public void setCurrentMp(int i) {
		if (getCurrentMp() == i) {
			return;
		}
		int currentMp = i;
		if (currentMp >= getMaxMp() || isGm()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);
		sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	public L1DwarfInventory getDwarfInventory() {
		return _dwarf;
	}

	public L1DwarfForElfInventory getDwarfForElfInventory() {
		return _dwarfForElf;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(boolean flag) {
		_gmInvis = flag;
	}

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	// 큐브 용 MR 계산용
	public int CubeMr;

	public void setCurrentWeapon(int i) {
		_currentWeapon = i;
	}
	
/*	public boolean isBlood() {
		  return (hasSkillEffect(L1SkillId.BLOODLUST));
	} */

	public int getType() {
		return _type;
	}

	public void setType(int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(short i) {
		_accessLevel = i;
	}
	//이동식창고 
	public int getChango() {
	return _chango;
	}

	public void setChango(int i) {
	_chango = i;
	}
	//이동식창고
	//드래곤 포탈
	public int getdragonportal() {
	return _chango;
	}

	public void setdragonportal(int i) {
	_chango = i;
	}
	//드래곤 포탈
	public int getClassId() {
	return _classId;
	}	

	public void setClassId(int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized int getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(int i) {
		_exp = i;
	}

	private int _PKcount; // ● PK카운트

    private int _autoct; //by사부 오토인증추가

    private int _autogo; //by사부 오토인증추가

    private String  _autocode; //by사부 오토인증추가

    private int _autook;  //by사부 오토인증추가

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(int i) {
		_PKcount = i;
	}

    public int get_autoct() {  return _autoct; } //by사부 오토인증추가
    public void set_autoct(int i) {  _autoct = i; }
 
    public int get_autogo() {  return _autogo; } //by사부 오토인증추가
    public void set_autogo(int i) {  _autogo = i; }
 
    public String get_autocode() {  return _autocode; } //by사부 오토인증추가
    public void set_autocode(String s) {  _autocode = s; }
 
    public int get_autook() {  return _autook; } //by사부 오토인증추가
    public void set_autook(int i) {  _autook = i; }

	private int _clanid; // ● 크란 ID

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(int i) {
		_clanid = i;
	}

	private String clanname; // ● 크란명

	public String getClanname() {
		return clanname;
	}

	public void setClanname(String s) {
		clanname = s;
	}
	
	private String _sealingPW; // ● 클랜명

	public String getSealingPW() {
		return _sealingPW;
	}

	public void setSealingPW(String s) {
		_sealingPW = s;
	}

	// 참조를 가지도록(듯이) 하는 편이 좋을지도 모른다
	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanname());
	}

	private int _clanRank; // ● 크란내의 랭크(혈맹 군주, 가디안, 일반, 견습)

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(int i) {
		_clanRank = i;
	}

	private byte _sex; // ● 성별

	public byte get_sex() {
		return _sex;
	}

	public void set_sex(int i) {
		_sex = (byte) i;
	}
	
	public boolean isGm() {
		return _gm;
	}

	public void setGm(boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(boolean flag) {
		_monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(double d, L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * 지정된 플레이어군에게 로그아웃 한 것을 통지한다
	 * 
	 * @param playersList
	 *            통지하는 플레이어의 배열
	 */
	private void notifyPlayersLogout(List<L1PcInstance> playersArray) {
		for (L1PcInstance player : playersArray) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		L1World world = L1World.getInstance();
		if (getClanid() != 0) // 크란 소속
		{
			L1Clan clan = world.getClan(getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == getId()) // 자캐릭터가 크란 창고 사용중
				{
					clan.setWarehouseUsingChar(0); // 크란 창고의 락을 해제
				}
			}
		}
		notifyPlayersLogout(getKnownPlayers());
		world.removeVisibleObject(this);
		world.removeObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		_inventory.clearItems();
		_dwarf.clearItems();
		removeAllKnownObjects();
		stopHpRegeneration();
		stopMpRegeneration();
		stopEtcMonitor();
		stopHpRegenerationByDoll();
		stopMpRegenerationByDoll();
		setDead(true); // 사용법 이상할지도 모르지만, NPC에 소멸한 것을 알게 하기 (위해)때문에
		setNetConnection(null);
		setPacketOutput(null);
	}

	public ClientThread getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(ClientThread clientthread) {
		_netConnection = clientthread;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(L1Party p) {
		_party = p;
	}

	public boolean isInChatParty() {
		return getChatParty() != null;
	}

	public L1ChatParty getChatParty() {
		return _chatParty;
	}

	public void setChatParty(L1ChatParty cp) {
		_chatParty = cp;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(int partyID) {
		_partyID = partyID;
	}
	//** 교환 타켓 아이디 기억시키기 **// 	By 도우너
	private String _tradetarget; // ● 타이틀

	public String getTradeTarget() {
		return _tradetarget;
	}

	public void setTradeTarget(String s) {
		_tradetarget = s;
	}	
	//** 교환 타켓 아이디 기억시키기 **// 	By 도우너

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}
	
	private boolean Petrace;
	
	public boolean isPetrace() {
		return Petrace;
	}

	public void setTempID(int tempID) {
		_tempID = tempID;
	}

	public boolean isTrade() {
		return _isTrade;
	}

	public void setTrade(boolean flag) {
		_isTrade = flag;
	}
	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(boolean flag) {
		_isGres = flag;
	}

	public boolean isPinkName() {
		return _isPinkName;
	}

	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}

	private ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	public ArrayList getSellList() {
		return _sellList;
	}

	private ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	public ArrayList getBuyList() {
		return _buyList;
	}

	private byte[] _shopChat;

	public void setShopChat(byte[] chat) {
		_shopChat = chat;
	}

	public byte[] getShopChat() {
		return _shopChat;
	}

	private boolean _isPrivateShop = false;

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(boolean flag) {
		_isPrivateShop = flag;
	}

	private boolean _isTradingInPrivateShop = false;

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // 열람중의 개인 상점의 아이템수

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(int i) {
		_partnersPrivateShopItemCount = i;
	}

	private PacketOutput _out;

	public void setPacketOutput(PacketOutput out) {
		_out = out;
	}

	public void sendPackets(ServerBasePacket serverbasepacket) {
		if (_out == null) {
			return;
		}

		try {
			_out.sendPacket(serverbasepacket);
		} catch (Exception e) {
		}
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		// XXX:NullPointerException 회피.onAction의 인수의 형태는 L1Character 쪽이 좋아?
		if (attacker == null) {
			return;
		}
		// 텔레포트 처리중
		if (isTeleport()) {
			return;
		}
		// 공격받는 측 또는 공격하는 측이 세이프티 존
		if (getZoneType() == 1 || attacker.getZoneType() == 1) {
			// 공격 모션 송신
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();

			boolean isMotalbody = false;
			L1Attack attack = new L1Attack(attacker, this);
			if (attack.calcHit()) {
				if (hasSkillEffect(L1SkillId.MOTALBODY)) {
					L1Magic magic = new L1Magic(this, attacker);
					boolean isProbability = magic.calcProbabilityMagic(L1SkillId.MOTALBODY);
					boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isMotalbody = true;
					}
				}
				
			}
				
			boolean isCounterBarrier = false;
			if (attack.calcHit()) {
				if (hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
					L1Magic magic = new L1Magic(this, attacker);
					boolean isProbability = magic
							.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
					boolean isShortDistance = attack.isShortDistance();
					if (isProbability && isShortDistance) {
						isCounterBarrier = true;
					}
				}
				if (!isCounterBarrier||!isMotalbody) {
					attacker.setPetTarget(this);

					attack.calcDamage();
					attack.calcStaffOfMana();
					attack.calcDrainOfHp(); 
					attack.addPcPoisonAttack(attacker, this);
				}
			}
			if (isCounterBarrier || isMotalbody) {
				attack.actionCounterBarrier();
				attack.commitCounterBarrier();
			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	public boolean checkNonPvP(L1PcInstance pc, L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;
		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // 상대가 PC, 사몬, 애완동물 이외
		}
		if (!Config.ALT_NONPVP) { // Non-PvP 설정
			if (getMap().isCombatZone(getLocation())) {
				return false;
			}

			// 전전쟁 리스트를 취득
			for (L1War war : L1World.getInstance().getWarList()) {
				if (pc.getClanid() != 0 && targetpc.getClanid() != 0) { // 모두 크란 소속중
					boolean same_war = war.CheckClanInSameWar(pc.getClanname(),
							targetpc.getClanname());
					if (same_war == true) { // 같은 전쟁에 참가중
						return false;
					}
				}
			}
			// Non-PvP 설정에서도 전쟁중은 포고없이 공격 가능
			if (target instanceof L1PcInstance) {
				L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc, targetPc)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isInWarAreaAndWarTime(L1PcInstance pc, L1PcInstance target) {
		// pc와 target가 전쟁중에 전쟁 에리어에 있을까
		int castleId = L1CastleLocation.getCastleIdByArea(pc);
		int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(L1Character target) {
		Object[] petList = getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				L1PetInstance pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		// 마법 접속 시간내는 이쪽을 이용
		if (hasSkillEffect(L1SkillId.INVISIBILITY)) { // 인비지비리티
			killSkillEffectTimer(L1SkillId.INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			L1World.getInstance().broadcastPacketToAll(new S_Invis(getId(), 0)); // 추가
			//broadcastPacket(new S_OtherCharPacks(this));
		}
		if (hasSkillEffect(L1SkillId.BLIND_HIDING)) { // 브라인드하이딘그
			killSkillEffectTimer(L1SkillId.BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			L1World.getInstance().broadcastPacketToAll(new S_Invis(getId(), 0)); // 추가
			//broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		// 마법 접속 시간 종료는 이쪽
		killSkillEffectTimer(L1SkillId.BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		L1World.getInstance().broadcastPacketToAll(new S_Invis(getId(), 0)); // 추가
		//broadcastPacket(new S_OtherCharPacks(this));
	}

	// 마법의 데미지의 경우는 여기를 사용 (여기서 마법 데미지 경감 처리) attr:0.무속성 마법, 1.땅마법, 2.불마법, 3.수해법, 4.바람 마법
	public void receiveDamage(L1Character attacker, int damage, int attr) {
		Random random = new Random();
		int player_mr = getMr();
		int rnd = random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage /= 2;
		}
		receiveDamage(attacker, damage);
		 if(attacker instanceof L1PcInstance){ //마법에의해 피해를 입을시 공격자가 PC라면 펫or서먼이 타겟으로등록
   L1PcInstance _Attacker = (L1PcInstance) attacker;
   _Attacker.setPetTarget(this);
  }
	}

	public void receiveManaDamage(L1Character attacker, int mpDamage) { // 공격으로 MP를 줄일 때는 여기를 사용
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			if (attacker instanceof L1PcInstance) {
				L1PinkName.onAction(this, attacker);
			}
		/*	if (attacker instanceof L1PcInstance
					&& ((L1PcInstance) attacker).isPinkName()) {
				// 가이드가 화면내에 있으면, 공격자를 가이드의 타겟으로 설정한다
				for (L1Object object : L1World.getInstance()
						.getVisibleObjects(attacker)) {
					if (object instanceof L1GuardInstance) {
						L1GuardInstance guard = (L1GuardInstance) object;
						guard.setTarget(((L1PcInstance) attacker));
					}
				}
			} */

			int newMp = getCurrentMp() - mpDamage;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}

			if (newMp <= 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	public void receiveDamage(L1Character attacker, int damage) { // 공격으로 HP를 줄일 때는 여기를 사용
		if (getCurrentHp() > 0 && !isDead()) {
			if (attacker != this && !knownsObject(attacker)
					&& attacker.getMapId() == this.getMapId()) {
				attacker.onPerceive(this);
			}

			if (damage > 0) {
				delInvis();
				if (attacker instanceof L1PcInstance) {
					L1PinkName.onAction(this, attacker);
					/*L1PcInstance fightPc = (L1PcInstance) attacker;
					if (fightPc.getFightId() != getId()) {
						L1PinkName.onAction(this, fightPc);
					}*/
				}
			/*	if (attacker instanceof L1PcInstance
						&& ((L1PcInstance) attacker).isPinkName()) {
					// 가이드가 화면내에 있으면, 공격자를 가이드의 타겟으로 설정한다
					for (L1Object object : L1World.getInstance()
							.getVisibleObjects(attacker)) {
						if (object instanceof L1GuardInstance) {
							L1GuardInstance guard = (L1GuardInstance) object;
							guard.setTarget(((L1PcInstance) attacker));
						}
					}
				} */
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
			}

			if (getInventory(). checkEquipped(145) // 바서카악스
					|| getInventory(). checkEquipped(149)) { // 미노타우르스악스
				damage *= 1.5; // 피안 됨 1.5배
			}
			if (hasSkillEffect(L1SkillId.AVATA)) {
				damage *= 0.8;
			}
			int newHp = getCurrentHp() - damage;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (isGm() && getInventory().checkEquipped(300000)) {
					setCurrentHp(getMaxHp());
				} else {
					death(attacker);
     if (getZoneType() == 0) { //만약 노멀존이 라면 (컴벳존에서 승작업 할것같에서.. 방지차원으로..)
      if (attacker instanceof L1PcInstance) { //pvp시에만..
       if (getLevel() >= 65) { //65랩 이상이라면..(저랩으로 승작업 할것같아서.. 방지차원으로..)
        attacker.setKills(attacker.getKills()+1); //이긴넘 킬수 +1
        setDeaths(getDeaths()+1); //진넘 데스수 +1
        attacker.getInventory().storeItem(43012, 1); //이건 저희섭 pk승리의 조각 지급..다른분들 상금넣으셔도됨..
       L1World.getInstance().broadcastPacketToAll(
       new S_SystemMessage(attacker.getName() + "\\fW님이 " + getName() + "\\fH님과의 전투에서 승리 하셧습니다."));
       } else { //65이하의 케릭을 죽였을시에는 멘트만 나가도록..
        L1World.getInstance().broadcastPacketToAll(
        new S_SystemMessage(attacker.getName() + "\\fW님이 " + getName() + "\\fH님과의 전투에서 승리 하셧습니다."));
             }
         }	
     } 
}  
			}
        if (newHp > 0) {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) { // 만약을 위해
			System.out
					.println("경고：플레이어의 HP감소 처리가 올바르게 행해지지 않은 개소가 있습니다.※혹은 최초부터 HP0");
			death(attacker);
		}
	}
						
	public void death(L1Character lastAttacker) {
		synchronized (this) {
			if (isDead()) {
				return;
			}
			setDead(true);
			setStatus(ActionCodes.ACTION_Die);
		}
		GeneralThreadPool.getInstance().execute(new Death(lastAttacker));

	}

	private class Death implements Runnable {
		L1Character _lastAttacker;

		Death(L1Character cha) {
			_lastAttacker = cha;
		}

	public void DiePack(int targetobjid){
      sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
      broadcastPacket(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
     }
		
		public void run() {
			L1Character lastAttacker = _lastAttacker;
			_lastAttacker = null;
			setCurrentHp(0);
			setGresValid(false); // EXP 로스트 할 때까지 G-RES 무효

			while (isTeleport()) { // 텔레포트중이라면 끝날 때까지 기다린다
				try {
					Thread.sleep(300);
				} catch (Exception e) {
				}
			}

			stopHpRegeneration();
			stopMpRegeneration();

			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			// 엔챤트를 해제한다
			// 변신 상태도 해제되기 (위해)때문에, 왈가닥 세레이션을 걸치고 나서 변신 상태에 되돌린다
			int tempchargfx = 0;
			if (hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				tempchargfx = getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
			}

			// 왈가닥 세레이션을 효과없이 걸친다
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this,
					L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
					L1SkillUse.TYPE_LOGIN);

			// 그림자계 변신중에 사망하면(자) 클라이언트가 떨어지기 (위해)때문에 잠정 대응
			if (tempchargfx == 5727 || tempchargfx == 5730
					|| tempchargfx == 5733 || tempchargfx == 5736) {
				tempchargfx = 0;
			}
			if (tempchargfx != 0) {
				sendPackets(new S_ChangeShape(getId(), tempchargfx));
				broadcastPacket(new S_ChangeShape(getId(), tempchargfx));
			} else {
				// 그림자계 변신중에 공격하면서 사망하면(자) 클라이언트가 떨어지기 (위해)때문에 지연을 넣는다
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
			}

		//	sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
		//	broadcastPacket(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));

			if (lastAttacker != L1PcInstance.this) {
				// 세이프티 존, 컴배트 존에서 마지막에 죽인 캐릭터가
				// 플레이어 or펫이라면, 패널티 없음
				if (getZoneType() != 0) {
					L1PcInstance player = null;
					if (lastAttacker instanceof L1PcInstance) {
						player = (L1PcInstance) lastAttacker;
					} else if (lastAttacker instanceof L1PetInstance) {
						player = (L1PcInstance) ((L1PetInstance) lastAttacker)
								.getMaster();
					} else if (lastAttacker instanceof L1SummonInstance) {
						player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
								.getMaster();
					}
					if (player != null) {
						// 전쟁중에 전쟁 에리어에 있는 경우는 예외
						if (!isInWarAreaAndWarTime(L1PcInstance.this, player)) {
							DiePack(targetobjid);
							return;
						}
					}
				}

				boolean sim_ret = simWarResult(lastAttacker); // 모의전
				if (sim_ret == true) { // 모의 전시중이라면 패널티 없음
					DiePack(targetobjid);
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				DiePack(targetobjid);
				return;
			}

			 // 결투중이라면 패널티 없음
			L1PcInstance fightPc = null;
			if (lastAttacker instanceof L1PcInstance) {
				fightPc = (L1PcInstance) lastAttacker;
			}
			if (fightPc != null) {
				if (getFightId() == fightPc.getId()
						&& fightPc.getFightId() == getId()) { // 결투중
					setFightId(0);
					sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					fightPc.setFightId(0);
					fightPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, 0, 0));
					DiePack(targetobjid);
					return;
				}
			}

			deathPenalty(); // EXP 로스트
			onChangeExp(); // 경험치변화에 따른 렙업/다운 변화
			setGresValid(true); // EXP 로스트 하면(자) G-RES 유효

			if (getExpRes() == 0) {
				setExpRes(1);
			}

			// 가이드에 살해당했을 경우만, PK카운트를 줄여 가이드에 공격받지 않게 된다
			if (lastAttacker instanceof L1GuardInstance || lastAttacker instanceof L1RguardInstance) {
				if (get_PKcount() > 0) {
					set_PKcount(get_PKcount() - 1);
				}
				setLastPk(null);
			}

			// 일정한 확률로 아이템을 DROP
			// 아라이먼트 32000이상으로0%, 이후-1000마다 0.4%
			// 아라이먼트가 0 미만의 경우는―1000마다 0.8%
			// 아라이먼트 32000 이하로 최고 51.2%의 DROP율
			int lostRate = (int) (((getLawful() + 32768D) / 1000D - 65D) * 5D);
			if (lostRate < 0) {
				lostRate *= -1;
				if (getLawful() < 0) {
					lostRate *= 2;
				}
				Random random = new Random();
				int rnd = random.nextInt(1000) + 1;
				if (rnd <= lostRate) {
					int count = 1;
					if (getLawful() <= -30000) {
						count = random.nextInt(4) + 1;
					} else if (getLawful() <= -20000) {
						count = random.nextInt(3) + 1;
					} else if (getLawful() <= -10000) {
						count = random.nextInt(2) + 1;
					} else if (getLawful() < 0) {
						count = random.nextInt(1) + 1;
					}
					caoPenaltyResult(count);
				}
			}

			boolean castle_ret = castleWarResult(); // 공성전
			if (castle_ret == true) { // 공성 전시중에 기내라면 빨강 네임 패널티 없음
				DiePack(targetobjid);
				return;
			}

			// 마지막에 죽인 캐릭터가 플레이어라면, 빨강 네임으로 한다
			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			    }
	    	if (player != null) {
				if (getLawful() >= 0 && isPinkName() == false) {
					boolean isChangePkCount = false;
					// 아라이먼트가 30000 미만의 경우는 PK카운트 증가
					if (player.getLawful() < 30000) {
						player.set_PKcount(player.get_PKcount() + 1);
						isChangePkCount = true;
					}
					player.setLastPk();

					// 아라이먼트 처리
					// 공식의 발표 및 각 LV에서의 PK로부터 사리가 맞도록(듯이) 변경
					// (PK측의 LV에 의존해, 고LV(정도)만큼 리스크도 높다)
					// 48당으로―8 k(정도)만큼 DK의 시점에서 10 k강
					// 60으로 약 20 k강 65로 30 k미만
					int lawful;

					if (player.getLevel() < 50) {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 2) * 4));
					} else {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 3) * 0.08));
					}
					// 만약(원래의 아라이먼트 1000)이 계산 후보다 낮은 경우
					// 원래의 아라이먼트 1000을 아라이먼트치로 한다
					// (연속으로 PK 했을 때에 거의 값이 변함없었던 기억보다)
					// 이것은 위의 식보다 자신도가 낮은 어설픈 기억이므로
					// 분명하게 이러하면 않다!그렇다고 하는 경우는 수정 부탁합니다
					if ((player.getLawful() - 1000) < lawful) {
						lawful = player.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					player.setLawful(lawful);

					S_Lawful s_lawful = new S_Lawful(player.getId(), player
							.getLawful());
					player.sendPackets(s_lawful);
					player.broadcastPacket(s_lawful);

				if (isChangePkCount && player.get_PKcount() >= 50
						&& player.get_PKcount() < 100) {
						// 당신의 PK회수가%0가 되었습니다.회수가%1가 되면(자) 지옥행입니다.
						player.sendPackets(new S_BlueMessage(551, String
							.valueOf(player.get_PKcount()), "100"));
				} else if (isChangePkCount && player.get_PKcount() >= 100) {
						player.beginHell(true);
					}
				} else {
					setPinkName(false);
				}
			}
	    	DiePack(targetobjid);
		}
	}

	private void caoPenaltyResult(int count) {
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = getInventory().CaoPenalty();

			if (item != null) {
				  if (item.getLockitem() > 100){
					     getInventory().removeItem(item, item.isStackable() ? item.getCount() : 1);
					     sendPackets(new S_ServerMessage(158,item.getLogName())); //\f1%0%s 증발되어 사라집니다.                  
				} else {
					     getInventory().tradeItem(item, item.isStackable() ? item.getCount() : 1,
					         L1World.getInstance().getInventory(getX(), getY(), getMapId()));
					     sendPackets(new S_ServerMessage(638,item.getLogName())); // %0를 잃었습니다.
					     }
					   }
					  }
					 }

	private void caoPenaltyResult2(int count) {
		int i=0;
		while(i < count){
			L1ItemInstance item = getInventory().CaoPenalty();

			if (item != null) {
				getInventory().tradeItem(
						item,
						item.isStackable() ?  item.getCount() : 1,
						L1World.getInstance().getInventory(getX(), getY(),
								getMapId()));
				sendPackets(new S_ServerMessage(638, item.getLogName())); // %0를 잃었습니다.
				++i;
			}  
		}
	}

	public boolean castleWarResult() {
		if (getClanid() != 0 && isCrown()) { // 크란 소속중 프리의 체크
			L1Clan clan = L1World.getInstance().getClan(getClanname());
			// 전전쟁 리스트를 취득
			for (L1War war : L1World.getInstance().getWarList()) {
				int warType = war.GetWarType();
				boolean isInWar = war.CheckClanInWar(getClanname());
				boolean isAttackClan = war.CheckAttackClan(getClanname());
				if (getId() == clan.getLeaderId() && // 혈맹 주요해 공격측에서 공성 전시중
						warType == 1 && isInWar && isAttackClan) {
					String enemyClanName = war.GetEnemyClanName(getClanname());
					if (enemyClanName != null) {
						war.CeaseWar(getClanname(), enemyClanName); // 종결
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(this);
		if (castleId != 0) { // 기내에 있다
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(L1Character lastAttacker) {
		if (getClanid() == 0) { // 크란 소속하지 않았다
			return false;
		}
		if (Config.SIM_WAR_PENALTY) { // 모의전 패널티 있는 경우는 false
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker)
					.getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker)
					.getMaster();
		} else {
			return false;
		}

		// 전전쟁 리스트를 취득
		for (L1War war : L1World.getInstance().getWarList()) {
			L1Clan clan = L1World.getInstance().getClan(getClanname());

			int warType = war.GetWarType();
			boolean isInWar = war.CheckClanInWar(getClanname());
			if (attacker != null && attacker.getClanid() != 0) { // lastAttacker가 PC, 사몬, 애완동물로 크란 소속중
				sameWar = war.CheckClanInSameWar(getClanname(), attacker
						.getClanname());
			}

			if (getId() == clan.getLeaderId() && // 혈맹 주요해 모의 전시중
					warType == 2 && isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanname());
				if (enemyClanName != null) {
					war.CeaseWar(getClanname(), enemyClanName); // 종결
				}
			}

			if (warType == 2 && sameWar) {// 모의전에서 같은 전쟁에 참가중의 경우, 패널티 없음
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 45) {
			exp = (int) (needExp * 0.05);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.045);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.04);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.035);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.03);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.025);
		}

		if (oldLevel == 1 || exp == 0) {
			return;
		}
		addExp(exp);
	}
	// 범위외가 된 인식이 끝난 오브젝트를 제거(버경)
	 private void removeOutOfRangeObjects(int distance) {
	  try{
	  List<L1Object> known = getKnownObjects();
	  for (int i = 0; i < known.size(); i++) {
	   if (known.get(i) == null) {
	    continue;
	   }

	   L1Object obj = known.get(i);
	       if (! getLocation().isInScreen(obj.getLocation())) { // 범위외가 되는 거리
	    removeKnownObject(obj);
	    sendPackets(new S_RemoveObject(obj));
	   }
	  }
	  }catch(Exception e){
	   System.out.println("removeOutOfRangeObjects 에러 : "+e);
	  }
	 }

	 // 오브젝트 인식 처리(버경)
	 public void UpdateObject() {
	  try{
	  if(this == null)
	   return;
	  try{
	   removeOutOfRangeObjects(17);
	  }catch(Exception e){
	   System.out.println("removeOutOfRangeObjects(17) 에러 : "+e);
	  }

	  // 화면내의 오브젝트 리스트를 작성 
	  ArrayList<L1Object> visible2 = L1World.getInstance().getVisibleObjects(this);
	  for (L1Object visible : visible2){
	   if(this == null){
	    break;
	   }
	   if(visible == null){
	    continue;
	   }
	   if (! knownsObject(visible)) {
	    visible.onPerceive(this);
	   } else {
	    if (visible instanceof L1NpcInstance) {
	     L1NpcInstance npc = (L1NpcInstance) visible;
	     if (npc.getHiddenStatus() != 0) {
	      npc.approachPlayer(this);
	     }
	    }

	   }
	  }
	  }catch(Exception e){
	   System.out.println("UpdateObject() 에러 : "+e);
	  }
	 }


	public void deathPenalty() {
		int oldLevel = getLevel();
		int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel >= 1 && oldLevel < 11) {
			exp = 0;
		} else if (oldLevel >= 11 && oldLevel < 45) {
			exp = (int) (needExp * 0.1);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.09);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.08);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.07);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.06);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.05);
		}

		if (exp == 0) {
			return;
		}
		addExp(-exp);
	}
	private int _originalEr = 0; // ● 오리지날 DEX ER보정

	public int getOriginalEr() {

		return _originalEr;	
	}
	public int getEr() {
		if (hasSkillEffect(L1SkillId.STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight()) { 
			er = getLevel() / 4; // 기사
		} else if (isCrown() || isElf()) {
			er = getLevel() / 8; // 군주·에르프
		} else if (isDarkelf() || isBlackWizard()) {
			er = getLevel() / 6; // 다크엘프, 환술사 er
		} else if (isWizard()) {
			er = getLevel() / 10; // 위저드
		} else if (isDragonKnight()) {
			er = getLevel() / 7; // 용기사 
		 } else if (isBlackWizard()) {
			er = getLevel() / 9; // 환술사
		}

		er += (getDex() - 8) / 2;

		er += getOriginalEr();
		
		if (hasSkillEffect(L1SkillId.DRESS_EVASION)) {
			er += 12;
		}
		if (hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
			er += 15;
		}
		return er;
	}

	public L1BookMark getBookMark(String name) {
		for (int i = 0; i < _bookmarks.size(); i++) {
			L1BookMark element = _bookmarks.get(i);
			if (element.getName().equalsIgnoreCase(name)) {
				return element;
			}

		}
		return null;
	}

	public L1BookMark getBookMark(int id) {
		for (int i = 0; i < _bookmarks.size(); i++) {
			L1BookMark element = _bookmarks.get(i);
			if (element.getId() == id) {
				return element;
			}

		}
		return null;
	}

	public int getBookMarkSize() {
		return _bookmarks.size();
	}

	public void addBookMark(L1BookMark book) {
		_bookmarks.add(book);
	}

	public void removeBookMark(L1BookMark book) {
		_bookmarks.remove(book);
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARK_ELF_MALE || getClassId() == CLASSID_DARK_ELF_FEMALE);
	}
	
	public boolean isDragonKnight() {
		return (getClassId() == CLASSID_DRAGONKNIGHT_MALE || getClassId() == CLASSID_DRAGONKNIGHT_FEMALE);
	}
	
	public boolean isBlackWizard() {
		return (getClassId() == CLASSID_BLACKWIZARD_MALE || getClassId() == CLASSID_BLACKWIZARD_FEMALE);
	}

	private static Logger _log = Logger.getLogger(L1PcInstance.class.getName());
	private ClientThread _netConnection;
	private int _classId;
	private int _type;
	private int _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm;
	private boolean _monitor;
	private boolean _gmInvis;
	private short _accessLevel;
    private int _chango;
	private int _dragonportal;
    private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1DwarfInventory _dwarf;
	private final L1DwarfForElfInventory _dwarfForElf;
	private final L1Inventory _tradewindow;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private L1ChatParty _chatParty;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private boolean _isTeleport = false;
	private boolean _isTrade = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	private boolean _isPinkName = false;
	private final ArrayList<L1BookMark> _bookmarks;
	private L1Quest _quest;
	private MpRegeneration _mpRegen;
	private MpRegenerationByDoll _mpRegenByDoll;
	private HpRegeneration _hpRegen;
	private HpRegenerationByDoll _hpRegenByDoll;
	private static Timer _regenTimer = new Timer(true);
	private boolean _mpRegenActive;
	private boolean _mpRegenActiveByDoll;
	private boolean _hpRegenActive;
	private boolean _hpRegenActiveByDoll;
	private L1EquipmentSlot _equipSlot;

	private String _accountName; // ● 어카운트 네임

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(String s) {
		_accountName = s;
	}

	private short _baseMaxHp = 0; // ● MAXHP 베이스(1~32767)

	public short getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(short i) {
		i += _baseMaxHp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 1) {
			i = 1;
		}
		addMaxHp(i - _baseMaxHp);
		_baseMaxHp = i;
	}

	public void setBaseMaxHp(short i) {
		_baseMaxHp = i;
	}

	private short _baseMaxMp = 0; // ● MAXMP 베이스(0~32767)

	public short getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(short i) {
		i += _baseMaxMp;
		if (i >= 32767) {
			i = 32767;
		} else if (i < 0) {
			i = 0;
		}
		addMaxMp(i - _baseMaxMp);
		_baseMaxMp = i;
	}

	public void setBaseMaxMp(short i) {
		_baseMaxMp = i;
	}

	private int _baseAc = 0; // ● AC베이스(-128~127)

	public int getBaseAc() {
		return _baseAc;
	}
	private int _originalAc = 0; // ● 오리지날 DEX AC보정

	public int getOriginalAc() {

		return _originalAc;
	}
	private byte _baseStr = 0; // ● STR 베이스(1~127)

	public byte getBaseStr() {
		return _baseStr;
	}

	public void addBaseStr(byte i) {
		i += _baseStr;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addStr((byte) (i - _baseStr));
		_baseStr = i;
	}

	public void setBaseStr(byte i) {
		_baseStr = i;
	}

	private byte _baseCon = 0; // ● CON 베이스(1~127)

	public byte getBaseCon() {
		return _baseCon;
	}

	public void addBaseCon(byte i) {
		i += _baseCon;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCon((byte) (i - _baseCon));
		_baseCon = i;
	}

	public void setBaseCon(byte i) {
		_baseCon = i;
	}

	private byte _baseDex = 0; // ● DEX 베이스(1~127)

	public byte getBaseDex() {
		return _baseDex;
	}

	public void addBaseDex(byte i) {
		i += _baseDex;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addDex((byte) (i - _baseDex));
		_baseDex = i;
	}

	public void setBaseDex(byte i) {
		_baseDex = i;
	}

	private byte _baseCha = 0; // ● CHA 베이스(1~127)

	public byte getBaseCha() {
		return _baseCha;
	}

	public void addBaseCha(byte i) {
		i += _baseCha;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCha((byte) (i - _baseCha));
		_baseCha = i;
	}

	public void setBaseCha(byte i) {
		_baseCha = i;
	}

	private byte _baseInt = 0; // ● INT 베이스(1~127)

	public byte getBaseInt() {
		return _baseInt;
	}

	public void addBaseInt(byte i) {
		i += _baseInt;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addInt((byte) (i - _baseInt));
		_baseInt = i;
	}

	public void setBaseInt(byte i) {
		_baseInt = i;
	}

	private byte _baseWis = 0; // ● WIS 베이스(1~127)

	public byte getBaseWis() {
		return _baseWis;
	}

	public void addBaseWis(byte i) {
		i += _baseWis;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addWis((byte) (i - _baseWis));
		_baseWis = i;
	}
	public void setBaseWis(byte i) {
		_baseWis = i;
	}

	private int _originalStr = 0; // ● 오리지날 STR

	public int getOriginalStr() {
		return _originalStr;
	}

	public void setOriginalStr(int i) {
		_originalStr = i;
	}

	private int _originalCon = 0; // ● 오리지날 CON

	public int getOriginalCon() {
		return _originalCon;
	}

	public void setOriginalCon(int i) {
		_originalCon = i;
	}

	private int _originalDex = 0; // ● 오리지날 DEX

	public int getOriginalDex() {
		return _originalDex;
	}

	public void setOriginalDex(int i) {
		_originalDex = i;
	}

	private int _originalCha = 0; // ● 오리지날 CHA

	public int getOriginalCha() {
		return _originalCha;
	}

	public void setOriginalCha(int i) {
		_originalCha = i;
	}

	private int _originalInt = 0; // ● 오리지날 INT

	public int getOriginalInt() {
		return _originalInt;
	}

	public void setOriginalInt(int i) {
		_originalInt = i;
	}

	private int _originalWis = 0; // ● 오리지날 WIS

	public int getOriginalWis() {
		return _originalWis;
	}

	public void setOriginalWis(int i) {
		_originalWis = i;		
	}

	private int _originalDmgup = 0; // ● 오리지날 STR 데미지 보정

	public int getOriginalDmgup() {

		return _originalDmgup;
	}

	private int _originalBowDmgup = 0; // ● 오리지날 DEX 활데미지 보정

	public int getOriginalBowDmgup() {

		return _originalBowDmgup;
	}

	private int _originalHitup = 0; // ● 오리지날 STR 명중 보정

	public int getOriginalHitup() {

		return _originalHitup;
	}

	private int _originalBowHitup = 0; // ● 오리지날 DEX 명중 보정

	public int getOriginalBowHitup() {

		return _originalHitup;
	}

	private int _originalMr = 0; // ● 오리지날 WIS 마법 방어

	public int getOriginalMr() {

		return _originalMr;
	}
	private int _originalMagicHit = 0; // ● 오리지날 INT 마법 명중

	public int getOriginalMagicHit() {

		return _originalMagicHit;
	}

	private int _originalMagicCritical = 0; // ● 오리지날 INT 마법 위기

	public int getOriginalMagicCritical() {

		return _originalMagicCritical;
	}
	private int _originalMagicConsumeReduction = 0; // ● 오리지날 INT 소비 MP경감

	public int getOriginalMagicConsumeReduction() {

		return _originalMagicConsumeReduction;
	}
	private int _originalMagicDamage = 0; // ● 오리지날 INT 마법 데미지

	public int getOriginalMagicDamage() {

		return _originalMagicDamage;
	}
	private int _originalHpup = 0; // ● 오리지날 CON HP상승치 보정

	public int getOriginalHpup() {

		return _originalHpup;
	}

	private int _originalMpup = 0; // ● 오리지날 WIS MP상승치 보정

	public int getOriginalMpup() {

		return _originalMpup;
	}

	private int _baseDmgup = 0; // ● 데미지 보정 베이스(-128~127)

	public int getBaseDmgup() {
		return _baseDmgup;
	}
	
	private int _baseBowDmgup = 0; // ● 활데미지 보정 베이스(-128~127)
	
	public int getBaseBowDmgup() {  
		return _baseBowDmgup;   
	} 

	private int _baseHitup = 0; // ● 명중 보정 베이스(-128~127)

	public int getBaseHitup() {
		return _baseHitup;
	}

	private int _baseBowHitup = 0; // ● 활명중 보정 베이스(-128~127)

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}
	
	private int _bowDmgModifier = 0; // ● 활추가타격 보정 베이스(-128~127)

	public int getBowDmgModifier() {
	   return _bowDmgModifier;
	}

	public void addBowDmgModifier(int i) {
	   _bowDmgModifier += i;
	}

	private int _baseMr = 0; // ● 마법 방어 베이스(0~)

	public int getBaseMr() {
		return _baseMr;
	}

	private int _advenHp; // ● // advanced 스피리츠로 증가하고 있는 HP

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(int i) {
		_advenHp = i;
	}

	private int _advenMp; // ● // advanced 스피리츠로 증가하고 있는 MP

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(int i) {
		_advenMp = i;
	}

	private int _highLevel; // ● 과거 최고 레벨

	public int getHighLevel() {
		return _highLevel;
	}

	public void setHighLevel(int i) {
		_highLevel = i;
	}

	private int _bonusStats; // ● 할당한 보너스 스테이터스

	public int getBonusStats() {
		return _bonusStats;
	}

	public void setBonusStats(int i) {
		_bonusStats = i;
	}

	private int _elixirStats; // ● 에리크서로 오른 스테이터스

	public int getElixirStats() {
		return _elixirStats;
	}

	public void setElixirStats(int i) {
		_elixirStats = i;
	}

	private int _elfAttr; // ● 에르프의 속성

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(int i) {
		_elfAttr = i;
	}

	private int _expRes; // ● EXP 복구

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(int i) {
		_expRes = i;
	}

	private int _partnerId; // ● 결혼상대

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // ● 온라인 상태

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ● 홈 타운

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(int i) {
		_homeTownId = i;
	}

	private int _contribution; // ● 공헌도

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(int i) {
		_contribution = i;
	}

	// 지옥에 체재하는 시간(초)
	private int _hellTime;

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(int i) {
		_hellTime = i;
	}

	private boolean _banned; // ● 동결

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(boolean flag) {
		_banned = flag;
	}

	private int _food; // ● 만복도

	public int get_food() {
		return _food;
	}

	public void set_food(int i) {
		_food = i;
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 이 플레이어 상태를 스토리지에 기입한다.
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			return;
		}

		CharacterTable.getInstance().storeCharacter(this);
	}
	private Timestamp _lastactive;

 public Timestamp getLastActive() {
  return _lastactive;
 }

 public void setLastActive(Timestamp i) {
  _lastactive = i;
 }
 private int _ainzone;
 public void setAinZone(int i) {
  _ainzone = i;
 }
 public int getAinZone() {
  return _ainzone;
 }
 private int _ainpoint = 0;
 public void setAinPoint(int i) {
  _ainpoint = i;
 }
 public int getAinPoint() {
  return _ainpoint;
 }
 private int _St_Exp = 0;
 
 public void setStExp(int i) {
  _St_Exp = i;
 }
 public int getStExp() {
  return _St_Exp;
 }
//DG 추가 - ACE
	private int _Dg = 0; 
	public void addDg(int i) {
		_Dg += i;
		sendPackets(new S_PacketBox(S_PacketBox.UPDATE_DG,_Dg));
	}
	public int getDg() {
		return _Dg;
	}

	/**
	 * 이 플레이어의 목록 아이템 상태를 스토리지에 기입한다.
	 */
	public void saveInventory() {
		for (L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public static final int REGENSTATE_NONE = 4;
	public static final int REGENSTATE_MOVE = 2;
	public static final int REGENSTATE_ATTACK = 1;

	public void setRegenState(int state) {
		_mpRegen.setState(state);
		_hpRegen.setState(state);
	}
	public double getMaxWeight() {
		int str = getStr();
		int con = getCon();
		//double maxWeight = 1500 + 150 * ((str + con - 18) / 2);

		//int weightReductionByArmor = getWeightReduction(); // 방어용 기구에 의한 중량 경감

		//int weightReductionByDoll = 0; // 마법인형에 의한 중량 경감
		double maxWeight = 150 * (Math.floor(0.6 * str + 0.4 * con + 1));
		double weightReductionByArmor = getWeightReduction(); // 방어용 기구에 의한 중량 경감
		weightReductionByArmor /= 100;
		double weightReductionByDoll = 0; // 마법인형에 의한 중량 경감
		
		
		Object[] dollList = getDollList().values().toArray();
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			weightReductionByDoll += doll.getWeightReductionByDoll();
		}
		weightReductionByDoll /= 100;

		int weightReductionByMagic = 0;
		if (hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) { // 디크리스웨이트
			//weightReductionByMagic = 10;
			weightReductionByMagic = 180;
		}

		//int weightReduction = weightReductionByArmor + weightReductionByDoll
		//		+ weightReductionByMagic;
		//maxWeight += ((maxWeight / 100) * weightReduction);
		
		double originalWeightReduction = 0;
		originalWeightReduction += 0.04 * (getOriginalStrWeightReduction()
				+ getOriginalConWeightReduction());
		
		double weightReduction = 1 + weightReductionByArmor
		+ weightReductionByDoll + originalWeightReduction;
		
		maxWeight *= weightReduction;
		
		maxWeight += weightReductionByMagic;
		
		

		maxWeight *= Config.RATE_WEIGHT_LIMIT; // 웨이트 레이트를 건다

		return maxWeight;
	}

	public boolean isFastMovable() {
		return (hasSkillEffect(L1SkillId.HOLY_WALK)
				|| hasSkillEffect(L1SkillId.MOVING_ACCELERATION)
				|| hasSkillEffect(L1SkillId.WIND_WALK)
				|| hasSkillEffect(L1SkillId.STATUS_RIBRAVE));  
	}

	public boolean isFastAttackable() {   
		return hasSkillEffect(L1SkillId.BLOODLUST);
	} 


	public boolean isBrave() {
		return hasSkillEffect(L1SkillId.STATUS_BRAVE);
	}

	public boolean isHaste() {
		return (hasSkillEffect(L1SkillId.STATUS_HASTE)
				|| hasSkillEffect(L1SkillId.HASTE)
				|| hasSkillEffect(L1SkillId.GREATER_HASTE)
                || getMoveSpeed() == 1);
	}
//private long _isSkillDelay = System.currentTimeMillis(); // 최초 수치를 현재 시간을 세팅
          
           /**
        * 캐릭터에, 스킬 지연을 추가한다.
        * 
        * @param flag
        */
    /*     public void setSkillDelay(long dy) {
       _isSkillDelay = dy;
      }
*/
          /**
          * 캐릭터가 스킬딜레이중인지 체크
          * 
          * @return 스킬 지연중인가.
          */
   /*    public boolean isSkillDelay() {
       if (_isSkillDelay > System.currentTimeMillis()) return true;// 딜레이중이면
       return false;
       } */

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	private Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(getId()),
				DELAY_INVIS);
	}

	public synchronized void addExp(int exp) {
		_exp += exp;
		if (_exp > ExpTable.MAX_EXP) {
			_exp = ExpTable.MAX_EXP;
		}
	}

	public synchronized void addContribution(int contribution) {
		_contribution += contribution;
	}

	public void beginExpMonitor() {
		_expMonitorFuture = GeneralThreadPool.getInstance()
				.pcScheduleAtFixedRate(new L1PcExpMonitor(getId()), 0L,
						INTERVAL_EXP_MONITOR);
	}

	private void levelUp(int gap) {
		resetLevel();

       if (getLevel() > 20){ 
       if (getMapId() == 2005  || getMapId() == 86) { 
       int locx = 32573;
       int locy = 32941;
       short mapid = 0; 
        L1Teleport.teleport(this, locx, locy, mapid, 5, false);
       }

    try {
    save();
   } catch (Exception ignore) {
   // ignore
  }
 }


	// 환생의 물약
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				L1Item l1item = ItemTable.getInstance().getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
				}
			} catch (Exception e) {
				_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				sendPackets(new S_SystemMessage("환생의 물약 입수에 실패했습니다."));
			}
		}
		// 아인하사드의 축복
		if (getLevel() == 49 && getHighLevel() == 49) {
		   setAinPoint(200);
		   sendPackets(new S_SkillIconExp(getAinPoint()));
		  }
	
		for (int i = 0; i < gap; i++) {
			short randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(),
					getBaseCon());
			short randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(),
					getBaseWis());
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
			setCurrentHp(getCurrentHp() + 32767); // 렙업 만피
	        setCurrentMp(getCurrentMp() + 32767); // 렙업 만엠
		}
		    resetBaseHitup();
		    resetBaseDmgup();
		    resetBaseAc();
		    resetBaseMr();
		if (getLevel() > getHighLevel()) {
			setHighLevel(getLevel());
		}

		try {
			// DB에 캐릭터 정보를 기입한다
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	////////////////// 레벨별 퀘스트템 자동지급 .By군주 ////////////////////////// <<추가 
					  L1Quest quest = getQuest();
					  /*int lv15_step = quest.get_step(L1Quest.QUEST_LEVEL15);
					  if (getLevel() >= 15 && lv15_step != L1Quest.QUEST_END) {
					      switch (getType()){  // <--케릭 클래스 구분
					      
					   case 0://군주라면
					   { L1ItemInstance l1item = getInventory().storeItem(40226, 1); // 트루타겟
					                             getInventory().storeItem(40227, 1); // 글로잉오라
                                                 getInventory().storeItem(40229, 1); // 샤이닝오라
                                                 getInventory().storeItem(40230, 1); // 브레이브오라
                                                 getInventory().storeItem(40231, 1); // 런클랜
					      if (l1item != null)
					       sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15);
					       break;
					   }

					   case 1:
					    { L1ItemInstance item = getInventory().storeItem(20027, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					   }
					   break;
					   
					   case 2:
					    {
					    L1ItemInstance item = getInventory().storeItem(20021, 1);
					                          getInventory().storeItem(500002, 1); // 4단계 마법책
					                          getInventory().storeItem(500003, 1); // 5단계 마법책
					                          getInventory().storeItem(500004, 1); // 6단계 마법책
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					    }
					    break;
					    
					   case 3:
					    {
					    L1ItemInstance item = getInventory().storeItem(20226, 1);
					                          getInventory().storeItem(500002, 1); // 4단계 마법책
                                              getInventory().storeItem(500003, 1); // 5단계 마법책
                                              getInventory().storeItem(500004, 1); // 6단계 마법책
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					    }
					    break;
					   
					   case 4:
					    {
					    L1ItemInstance item = getInventory().storeItem(40598, 1);
					    if(item != null)
					    sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					    }
					    break;  
					    
					   case 5: // 용기사
					    {
					    L1ItemInstance item = getInventory().storeItem(502, 1); // 용기사의 양손검
					                          getInventory().storeItem(22001, 1); // 용비늘 가더
					                          getInventory().storeItem(500017, 1); // 용기사의 서판 1단계
					                          getInventory().storeItem(500018, 1); // 용기사의 서판 2단계
					                          getInventory().storeItem(500019, 1); // 용기사의 서판 3단계
					    if(item != null)
					    sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					    }
					    break;
					    
					   case 6: // 환술사
					    {
					    L1ItemInstance item = getInventory().storeItem(505, 1); // 환술사의 전투봉
					                          //getInventory().storeItem(210004, 1); // 큐브 : 이그니션
					                          getInventory().storeItem(500013, 1); // 1단계 기억의 수정
					                          getInventory().storeItem(500014, 1); // 2단계 기억의 수정
					                          getInventory().storeItem(500015, 1); // 3단계 기억의 수정
					                          getInventory().storeItem(500016, 1); // 4단계 기억의 수정
					    if(item != null)
					    sendPackets(new S_SystemMessage("Level(15)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL15); 
					    }
					    break;  
					    }
					  }
                    
					   int lv30_step = quest.get_step(L1Quest.QUEST_LEVEL30);
					  if (getLevel() >= 30 && lv30_step != L1Quest.QUEST_END) {
					      switch (getType()){  // <--케릭 클래스 구분
					   case 0:
					    { L1ItemInstance item = getInventory().storeItem(40570, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					   }
					   break;
					   
					   case 1:
					    { L1ItemInstance item = getInventory().storeItem(20230, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					   }
					   break;
					   
					   case 2:
					    {
					    L1ItemInstance item = getInventory().storeItem(40588, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					    }
					    break;
					    
					    case 3:
					     {
					     L1ItemInstance item = getInventory().storeItem(115, 1);
					     if(item != null)
					      sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					     getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					     }
					     break;
					     
					    case 4:
					     {
					     L1ItemInstance item = getInventory().storeItem(40545, 1);
					     if(item != null)
					      sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					     getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					     }
					     break;  
					     
					    case 5:
					     {
					     L1ItemInstance item = getInventory().storeItem(210025, 0);
					     if(item != null)
					      sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					     getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					     }
					     break;
					     
					    case 6:
					     {
					     L1ItemInstance item = getInventory().storeItem(210014, 1);
					                           getInventory().storeItem(22006, 1);
					     if(item != null)
					      sendPackets(new S_SystemMessage("Level(30)퀘스트를 완료하였습니다.")); 
					     getQuest().set_end(L1Quest.QUEST_LEVEL30); 
					     }
					     break;    
					   }
					  }
					  int lv45_step = quest.get_step(L1Quest.QUEST_LEVEL45);
					  if (getLevel() >= 45 && lv45_step != L1Quest.QUEST_END) {
					      switch (getType()){  // <--케릭 클래스 구분
					   case 0:
					    { L1ItemInstance item = getInventory().storeItem(20287, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL45); 
					   }
					   break;
					   
					   case 1:
					    { L1ItemInstance item = getInventory().storeItem(20318, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL45);
					   }
					   break;
					   
					   case 2:
					    {
					    L1ItemInstance item = getInventory().storeItem(40546, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL45);
					    }
					    break;
					    
					   case 3:
					    {
					    L1ItemInstance item = getInventory().storeItem(40599, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL45);
					    }
					    break;
					    
					   case 4:
					    {
					    L1ItemInstance item = getInventory().storeItem(40553, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL45);
					    }
					    break; 
					    
					   case 5:
					    {
					    L1ItemInstance item = getInventory().storeItem(22004, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL45);
					    }
					    break;
					    
					   case 6:
					    {
					    L1ItemInstance item = getInventory().storeItem(22005, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(45)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL45);
					    }
					    break; 
					   }
					  }*/
					  int lv50_step = quest.get_step(L1Quest.QUEST_LEVEL50);
					  if (getLevel() >= 50 && lv50_step != L1Quest.QUEST_END) {
					      switch (getType()){  // <--케릭 클래스 구분
					   case 0:
					    { L1ItemInstance item = getInventory().storeItem(51, 1);
					   if(item != null)
					   sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL50);
					   }
					   break;
					   
					   case 1:
					    { L1ItemInstance item = getInventory().storeItem(56, 1);
					   if(item != null)
					    sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					   getQuest().set_end(L1Quest.QUEST_LEVEL50);
					   }
					   break;
					   
					   case 2:
					    {
					    L1ItemInstance item = getInventory().storeItem(184, 1);
					    L1ItemInstance item1 = getInventory().storeItem(50, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL50);
					    }
					    break;
					    
					   case 3:
					    {
					    L1ItemInstance item = getInventory().storeItem(20225, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL50);
					    }
					    break;
					    
					   case 4:
					    {
					    L1ItemInstance item = getInventory().storeItem(13, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL50);
					    }
					    break; 
					    
					   case 5:
					    {
					    L1ItemInstance item = getInventory().storeItem(500, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL50);
					    }
					    break;
					    
					   case 6:
					    {
					    L1ItemInstance item = getInventory().storeItem(503, 1);
					    if(item != null)
					     sendPackets(new S_SystemMessage("Level(50)퀘스트를 완료하였습니다.")); 
					    getQuest().set_end(L1Quest.QUEST_LEVEL50);
					    }
					    break; 
					   }
					  }////////////////// 레벨별 퀘스트템 자동지급 .By군주 ///////////////////////////
		// 보너스 스테이터스
		if (getLevel() >= 51 && getLevel() - 50 > getBonusStats()) {
			if ((getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt()
					+ getBaseWis() + getBaseCha()) < 210) {
				sendPackets(new S_bonusstats(getId(), 1));
			}
		}
		sendPackets(new S_OwnCharStatus(this));
		if (getLevel() >= 51) { // 지정 레벨 
			if (getMapId() == 777) { // 버림받은 사람들의 땅(그림자의 신전)
				L1Teleport.teleport(this, 34043, 32184, (short) 4, 5, true); // 상아의 탑전
			} else if (getMapId() == 778
					|| getMapId() == 779) { // 버림받은 사람들의 땅(욕망의 동굴)
				L1Teleport.teleport(this, 32608, 33178, (short) 4, 5, true); // WB
			}
		}
	}

	private void levelDown(int gap) {
		resetLevel();

		for (int i = 0; i > gap; i--) {
			// 레벨 다운시는 랜덤치를 그대로 마이너스 하기 위해, base치에 0을 설정
			short randomHp = CalcStat.calcStatHp(getType(), 0, getBaseCon());
			short randomMp = CalcStat.calcStatMp(getType(), 0, getBaseWis());
			addBaseMaxHp((short) -randomHp);
			addBaseMaxMp((short) -randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();
		if (Config.LEVEL_DOWN_RANGE != 0) {
			if (getHighLevel() - getLevel() >= Config.LEVEL_DOWN_RANGE) {
				sendPackets(new S_ServerMessage(64)); // 월드와의 접속이 절단 되었습니다.
				sendPackets(new S_Disconnect());
				_log.info(String.format("레벨 다운의 허용 범위를 넘었기 때문에%s를 강제 절단 했습니다. ",
								getName()));
			}
		}

		try {
			// DB에 캐릭터 정보를 기입한다
			save();
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		new L1GameTimeCarrier(this).start();
	}

	private boolean _ghost = false; // 고우스트

	public boolean isGhost() {
		return _ghost;
	}

	private void setGhost(boolean flag) {
		_ghost = flag;
	}

	private boolean _ghostCanTalk = true; // NPC에 말을 건넬 수 있을까

	public boolean isGhostCanTalk() {
		return _ghostCanTalk;
	}

	private void setGhostCanTalk(boolean flag) {
		_ghostCanTalk = flag;
	}

	private boolean _isReserveGhost = false; // 고우스트 해제 준비

	public boolean isReserveGhost() {
		return _isReserveGhost;
	}

	private void setReserveGhost(boolean flag) {
		_isReserveGhost = flag;
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(int locx, int locy, short mapid, boolean canTalk,
			int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getHeading();
		setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = GeneralThreadPool.getInstance().pcSchedule(
					new L1PcGhostMonitor(getId()), sec * 1000);
		}
	}

	public void makeReadyEndGhost() {
		setReserveGhost(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, true);
	}

	public void endGhost() {
		setGhost(false);
		setGhostCanTalk(true);
		setReserveGhost(false);
	}

	private ScheduledFuture<? > _ghostFuture;

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	private ScheduledFuture<? > _hellFuture;

	public void beginHell(boolean isFirst) {
		// 지옥 이외에 있을 때는 지옥에 강제 이동
		if (getMapId() != 666) {
			int locx = 32701;
			int locy = 32777;
			short mapid = 666;
			L1Teleport.teleport(this, locx, locy, mapid, 5, false);
		}

		if (isFirst) {
			if (get_PKcount() <= 10) {
				setHellTime(300);
			} else {
				setHellTime(300 * (get_PKcount() - 10) + 300);
			}
			// 당신의 PK회수가%0가 되어, 지옥에 떨어뜨려졌습니다.당신은 여기서%1분간 반성하지 않으면 안됩니다.
			sendPackets(new S_BlueMessage(552, String.valueOf(get_PKcount()),
					String.valueOf(getHellTime() / 60)));
		} else {
			// 당신은%0초간 여기에 머무르지 않으면 안됩니다.
			sendPackets(new S_BlueMessage(637, String.valueOf(getHellTime())));
		}
		if (_hellFuture == null) {
			_hellFuture = GeneralThreadPool.getInstance()
					.pcScheduleAtFixedRate(new L1PcHellMonitor(getId()), 0L,
							1000L);
		}
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		// 지옥으로부터 탈출하면(자) 화전마을에 귀환시킨다.
		int[] loc = L1TownLocation
				.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (Exception ignore) {
			// ignore
		}
	}

	@Override
	public void setPoisonEffect(int effectId) {
		sendPackets(new S_Poison(getId(), effectId));

		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			broadcastPacket(new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(int pt) {
		super.healHp(pt);

		sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(int i) {
		_karma.set(i);
	}

	public void addKarma(int i) {
		synchronized (_karma) {
			_karma.add(i);
			sendPackets(new S_ShowKarma(this));		
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * 플레이어의 최종 PK시간을 돌려준다.
	 * 
	 * @return _lastPk
	 * 
	 */
	public Timestamp getLastPk() {
		return _lastPk;
	}

	/**
	 * 플레이어의 최종 PK시간을 설정한다.
	 * 
	 * @param time
	 *            최종 PK시간(Timestamp형) 해제하는 경우는 null를 대입
	 */
	public void setLastPk(Timestamp time) {
		_lastPk = time;
	}

	/**
	 * 플레이어의 최종 PK시간을 현재의 시각으로 설정한다.
	 */
	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 플레이어가 준비중일까를 돌려준다.
	 * 
	 * @return 준비중이면, true
	 */
	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	private Timestamp _deleteTime; // 캐릭터 삭제까지의 시간

	public Timestamp getDeleteTime() {
		return _deleteTime;
	}

	public void setDeleteTime(Timestamp time) {
		_deleteTime = time;
	}

	@Override
	public int getMagicLevel() {
		return getClassFeature().getMagicLevel(getLevel());
	}

	private int _weightReduction = 0;

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(int i) {
		_weightReduction += i;
	}
	
	private int _originalStrWeightReduction = 0;
	
	public int getOriginalStrWeightReduction() {
		
		return _originalStrWeightReduction;
	}
	
	private int _originalConWeightReduction = 0;
	
	public int getOriginalConWeightReduction() {
		
		return _originalConWeightReduction;
	}

	private int _hasteItemEquipped = 0;

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (hasSkillEffect(L1SkillId.SLOW)) {
			removeSkillEffect(L1SkillId.SLOW);
		}
		if (hasSkillEffect(L1SkillId.MASS_SLOW)) {
			removeSkillEffect(L1SkillId.MASS_SLOW);
		}
		if (hasSkillEffect(L1SkillId.ENTANGLE)) {
			removeSkillEffect(L1SkillId.ENTANGLE);
		}
		if (hasSkillEffect(L1SkillId.HASTE)) {
			removeSkillEffect(L1SkillId.HASTE);
		}
		if (hasSkillEffect(L1SkillId.GREATER_HASTE)) {
			removeSkillEffect(L1SkillId.GREATER_HASTE);
		}
		if (hasSkillEffect(L1SkillId.STATUS_HASTE)) {
			removeSkillEffect(L1SkillId.STATUS_HASTE);
		}
	}
	private int _braveItemEquipped = 0;//용기아이템추가 

	 public int getBraveItemEquipped() { 
	  return _braveItemEquipped; 
	 } 

	 public void addBraveItemEquipped(int i) { 
	  _braveItemEquipped += i; 
	 } 
	  
	 public void removeBraveSkillEffect() {//용기아이템추가 
	  if (hasSkillEffect(L1SkillId.SLOW)) { 
	  removeSkillEffect(L1SkillId.SLOW); 
	  } 
	  if (hasSkillEffect(L1SkillId.MASS_SLOW)) { 
	  removeSkillEffect(L1SkillId.MASS_SLOW); 
	  } 
	  if (hasSkillEffect(L1SkillId.ENTANGLE)) { 
	  removeSkillEffect(L1SkillId.ENTANGLE); 
	  } 
	  if (hasSkillEffect(L1SkillId.STATUS_BRAVE)) { 
	  removeSkillEffect(L1SkillId.STATUS_BRAVE); 
	  } 
/*	  if (hasSkillEffect(L1SkillId.STATUS_ELFBRAVE)) { 
	  removeSkillEffect(L1SkillId.STATUS_ELFBRAVE); 
	  } */
	  if (hasSkillEffect(L1SkillId.STATUS_RIBRAVE)) { 
	  removeSkillEffect(L1SkillId.STATUS_RIBRAVE); 
	  } 
	  if (hasSkillEffect(L1SkillId.WIND_WALK)) { 
	  removeSkillEffect(L1SkillId.WIND_WALK); 
	  } 
	  if (hasSkillEffect(L1SkillId.HOLY_WALK)) { 
	  removeSkillEffect(L1SkillId.HOLY_WALK); 
	  } 
	  if (hasSkillEffect(L1SkillId.MOVING_ACCELERATION)) { 
	  removeSkillEffect(L1SkillId.MOVING_ACCELERATION); 
	  } 
	  if (hasSkillEffect(L1SkillId.BLOODLUST)) { 
	  removeSkillEffect(L1SkillId.BLOODLUST); 
	  } 
	 } 

	private int _damageReductionByArmor = 0; // 방어용 기구에 의한 데미지 경감

	public int getDamageReductionByArmor() {
		return _damageReductionByArmor;
	}

	public void addDamageReductionByArmor(int i) {
		_damageReductionByArmor += i;
	}

	private int _bowHitRate = 0; // 방어용 기구에 의한 활의 명중율

	public int getBowHitRate() {
		return _bowHitRate;
	}

	public void addBowHitRate(int i) {
		_bowHitRate += i;
	}

	private boolean _gresValid; // G-RES가 유효한가

	private void setGresValid(boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	private long _fishingTime = 0;

	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(long i) {
		_fishingTime = i;
	}

	private boolean _isFishing = false;

	public boolean isFishing() {
		return _isFishing;
	}

	public void setFishing(boolean flag) {
		_isFishing = flag;
	}

	private boolean _isFishingReady = false;

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishingReady(boolean flag) {
        _isFishingReady = flag;
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(int i) {
		_cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(int i) {
		_dessertId = i;
	}

	/**
	 * LV에 의한 명중 보너스를 설정하는 LV가 변동했을 경우 등에 호출하면 재계산된다
	 * 
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		int newBaseBowDmgup = 0;
		
		if (isKnight() || isDarkelf()) { // 나이트
			newBaseDmgup = getLevel() / 10;
			newBaseBowDmgup = 0;
		} else if (isElf()) { // 에르프
			newBaseDmgup = 0;
			newBaseBowDmgup = getLevel() / 10;
		}else  if (isDragonKnight()) { 
			newBaseDmgup = getLevel() / 10;
		}
		addDmgup(newBaseDmgup - _baseDmgup);
		addBowDmgup(newBaseBowDmgup - _baseBowDmgup);
		_baseDmgup = newBaseDmgup;
		_baseBowDmgup = newBaseBowDmgup;
	}

	/**
	 * LV에 의한 명중 보너스를 설정하는 LV가 변동했을 경우 등에 호출하면 재계산된다
	 * 
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (isCrown()) { // 프리
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isKnight()) { // 나이트
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) { // 에르프
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isDarkelf()) { // 다크 에르프
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isDragonKnight()) { // 용기사
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isBlackWizard()) { // 환술사 
			newBaseHitup = getLevel() / 5;   
			newBaseBowHitup = getLevel() / 5; 
		}
		addHitup(newBaseHitup - _baseHitup);
		addBowHitup(newBaseBowHitup - _baseBowHitup);
		_baseHitup = newBaseHitup;
		_baseBowHitup = newBaseBowHitup;
	}

	/**
	 * 캐릭터 스테이터스로부터 AC를 재계산해 설정하는 초기설정시, LVUP, LVDown시 등에 호출한다
	 */
	public void resetBaseAc() {
		int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
		addAc(newAc - _baseAc);
		_baseAc = newAc;
	}

	/**
	 * 캐릭터 스테이터스로부터 소의 MR를 재계산해 설정하는 초기설정시, 스킬 사용시나 LVUP, LVDown시에 호출한다
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (isCrown()) { // 프리
			newMr = 10;
		} else if (isElf()) { // 에르프
			newMr = 25;
		} else if (isWizard()) { // 위저드
			newMr = 15;
		} else if (isDarkelf()) { // 다크 에르프
			newMr = 10;
		//} else if (isDragonKnight()) { //용기사
			//newMr = 10;
		} else if (isBlackWizard()) {  // 환술사
			newMr = 20;
		}
		
		newMr += CalcStat.calcStatMr(getWis()); // WIS 분의 MR보너스
		newMr += getLevel() / 2; // LV의 반만큼 추가
		addMr(newMr - _baseMr);
		_baseMr = newMr;
	}

	/**
	 * EXP로부터 현재의 Lv를 재계산해 설정하는 초기설정시, 사망시나 LVUP시에 호출한다
	 */
	public void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));

		if (_hpRegen != null) {
			_hpRegen.updateLevel();
		}
	}
	/**
	 * 초기 스테이터스로부터 현재의 보너스를 재계산해 설정하는 초기설정시, 재배분시에 호출한다
	 */
	public void resetOriginalHpup() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if(originalCon == 12 || originalCon == 13) {
				_originalHpup = 1;
			} else if(originalCon == 14 || originalCon == 15) {
				_originalHpup = 2;
			} else if(originalCon >= 16) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if(isKnight()) {
			if(originalCon == 15 || originalCon == 16) {
				_originalHpup = 1;
			} else if(originalCon >= 17) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if(isElf()) {
			if(originalCon >= 13 && originalCon <= 17) {
				_originalHpup = 1;
			} else if(originalCon == 18) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if(isDarkelf()) {
			if(originalCon == 10 || originalCon == 11) {
				_originalHpup = 1;
			} else if(originalCon >= 12) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if(isWizard()) {
			if(originalCon == 14 || originalCon == 15) {
				_originalHpup = 1;
			} else if(originalCon >= 16) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		} else if(isDragonKnight()) {
			if(originalCon == 15 || originalCon == 16) {
				_originalHpup = 1;
			} else if(originalCon >= 17) {
				_originalHpup = 3;
			} else {
				_originalHpup = 0;
			}
		} else if(isBlackWizard()) {
			if(originalCon == 13 || originalCon == 14) {
				_originalHpup = 1;
			} else if(originalCon >= 15) {
				_originalHpup = 2;
			} else {
				_originalHpup = 0;
			}
		}
	}

	public void resetOriginalMpup() {
		int originalWis = getOriginalWis(); {
			if (isCrown()) {
				if(originalWis >= 16) {
					_originalMpup = 1;
				} else {
					_originalMpup = 0;
				}
			} else if(isKnight()) {
					_originalMpup = 0;
			} else if(isElf()) {
				if(originalWis >= 14 && originalWis <= 16) {
					_originalMpup = 1;
				} else if(originalWis >= 17) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if(isDarkelf()) {
				if(originalWis >= 12) {
					_originalMpup = 1;
				} else {
					_originalMpup = 0;
				}
			} else if(isWizard()) {
				if(originalWis >= 13 && originalWis <= 16) {
					_originalMpup = 1;
				} else if(originalWis >= 17) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if(isDragonKnight()) {
				if(originalWis >= 13 && originalWis <= 15) {
					_originalMpup = 1;
				} else if(originalWis >= 16) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			} else if(isBlackWizard()) {
				if(originalWis >= 13 && originalWis <= 15) {
					_originalMpup = 1;
				} else if(originalWis >= 16) {
					_originalMpup = 2;
				} else {
					_originalMpup = 0;
				}
			}
		}
	}

	public void resetOriginalStrWeightReduction() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if(originalStr >= 14 && originalStr <= 16) {
				_originalStrWeightReduction = 1;
			} else if(originalStr >= 17 && originalStr <= 19) {
				_originalStrWeightReduction = 2;
			} else if(originalStr == 20) {
				_originalStrWeightReduction = 3;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if(isKnight()) {
				_originalStrWeightReduction = 0;
		} else if(isElf()) {
			if(originalStr >= 16) {
				_originalStrWeightReduction = 2;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if(isDarkelf()) {
			if(originalStr >= 13 && originalStr <= 15) {
				_originalStrWeightReduction = 2;
			} else if(originalStr >= 16) {
				_originalStrWeightReduction = 3;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if(isWizard()) {
			if(originalStr >= 9) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if(isDragonKnight()) {
			if(originalStr >= 16) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		} else if(isBlackWizard()) {
			if(originalStr == 18) {
				_originalStrWeightReduction = 1;
			} else {
				_originalStrWeightReduction = 0;
			}
		}
	}

	public void resetOriginalDmgup() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if(originalStr >= 15 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if(originalStr >= 18) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if(isKnight()) {
			if(originalStr == 18 || originalStr == 19) {
				_originalDmgup = 2;
			} else if(originalStr == 20) {
				_originalDmgup = 4;
			} else {
				_originalDmgup = 0;
			}
		} else if(isElf()) {
			if(originalStr == 12 || originalStr == 13) {
				_originalDmgup = 1;
			} else if(originalStr >= 14) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if(isDarkelf()) {
			if(originalStr >= 14 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if(originalStr == 18) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if(isWizard()) {
			if(originalStr == 10 || originalStr == 11) {
				_originalDmgup = 1;
			} else if(originalStr >= 12) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		} else if(isDragonKnight()) {
			if(originalStr >= 15 && originalStr <= 17) {
				_originalDmgup = 1;
			} else if(originalStr >= 18) {
				_originalDmgup = 3;
			} else {
				_originalDmgup = 0;
			}
		} else if(isBlackWizard()) {
			if(originalStr == 13 || originalStr == 14) {
				_originalDmgup = 1;
			} else if(originalStr >= 15) {
				_originalDmgup = 2;
			} else {
				_originalDmgup = 0;
			}
		}
	}

	public void resetOriginalConWeightReduction() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if(originalCon >= 11) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if(isKnight()) {
			if(originalCon >= 15) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if(isElf()) {
			if(originalCon >= 15) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if(isDarkelf()) {
			if(originalCon >= 9) {
				_originalConWeightReduction = 1;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if(isWizard()) {
			if(originalCon == 13 || originalCon == 14) {
				_originalConWeightReduction = 1;
			} else if(originalCon >= 15) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		} else if(isDragonKnight()) {
				_originalConWeightReduction = 0;
		} else if(isBlackWizard()) {
			if(originalCon == 17) {
				_originalConWeightReduction = 1;
			} else if(originalCon == 18) {
				_originalConWeightReduction = 2;
			} else {
				_originalConWeightReduction = 0;
			}
		}
	}

	public void resetOriginalBowDmgup() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if(originalDex >= 13) {
				_originalBowDmgup = 1;
			} else {
				_originalBowDmgup = 0;
			}
		} else if(isKnight()) {
				_originalBowDmgup = 0;
		} else if(isElf()) {
			if(originalDex >= 14 && originalDex <= 16) {
				_originalBowDmgup = 2;
			} else if(originalDex >= 17) {
				_originalBowDmgup = 3;
			} else {
				_originalBowDmgup = 0;
			}
		} else if(isDarkelf()) {
			if(originalDex == 18) {
				_originalBowDmgup = 2;
			} else {
				_originalBowDmgup = 0;
			}
		} else if(isWizard()) {
				_originalBowDmgup = 0;
		} else if(isDragonKnight()) {
				_originalBowDmgup = 0;
		} else if(isBlackWizard()) {
				_originalBowDmgup = 0;
		}
	}

	public void resetOriginalHitup() {
		int originalStr = getOriginalStr();
		if (isCrown()) {
			if(originalStr >= 16 && originalStr <= 18) {
				_originalHitup = 1;
			} else if(originalStr >= 19) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if(isKnight()) {
			if(originalStr == 17 || originalStr == 18) {
				_originalHitup = 2;
			} else if(originalStr >= 19) {
				_originalHitup = 4;
			} else {
				_originalHitup = 0;
			}
		} else if(isElf()) {
			if(originalStr == 13 || originalStr == 14) {
				_originalHitup = 1;
			} else if(originalStr >= 15) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if(isDarkelf()) {
			if(originalStr >= 15 && originalStr <= 17) {
				_originalHitup = 1;
			} else if(originalStr == 18) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if(isWizard()) {
			if(originalStr == 11 || originalStr == 12) {
				_originalHitup = 1;
			} else if(originalStr >= 13) {
				_originalHitup = 2;
			} else {
				_originalHitup = 0;
			}
		} else if(isDragonKnight()) {
			if(originalStr >= 14 && originalStr <= 16) {
				_originalHitup = 1;
			} else if(originalStr >= 17) {
				_originalHitup = 3;
			} else {
				_originalHitup = 0;
			}
		} else if(isBlackWizard()) {
			if(originalStr == 12 || originalStr == 13) {
				_originalHitup = 1;
			} else if(originalStr == 14 || originalStr == 15) {
				_originalHitup = 2;
			} else if(originalStr == 16) {
				_originalHitup = 3;
			} else if(originalStr >= 17) {
				_originalHitup = 4;
			} else {
				_originalHitup = 0;
			}
		}
	}

	public void resetOriginalBowHitup() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
				_originalBowHitup = 0;
		} else if(isKnight()) {
				_originalBowHitup = 0;
		} else if(isElf()) {
			if(originalDex >= 13 && originalDex <= 15) {
				_originalBowHitup = 2;
			} else if(originalDex >= 16) {
				_originalBowHitup = 3;
			} else {
				_originalBowHitup = 0;
			}
		} else if(isDarkelf()) {
			if(originalDex == 17) {
				_originalBowHitup = 1;
			} else if(originalDex == 18) {
				_originalBowHitup = 2;
			} else {
				_originalBowHitup = 0;
			}
		} else if(isWizard()) {
				_originalBowHitup = 0;
		} else if(isDragonKnight()) {
				_originalBowHitup = 0;
		} else if(isBlackWizard()) {
				_originalBowHitup = 0;
		}
	}

	public void resetOriginalMr() {
		int originalWis = getOriginalWis();
		if (isCrown()) {
			if(originalWis == 12 || originalWis == 13) {
				_originalMr = 1;
			} else if(originalWis >= 14) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if(isKnight()) {
			if(originalWis == 10 || originalWis == 11) {
				_originalMr = 1;
			} else if(originalWis >= 12) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if(isElf()) {
			if(originalWis >= 13 && originalWis <= 15) {
				_originalMr = 1;
			} else if(originalWis >= 16) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if(isDarkelf()) {
			if(originalWis >= 11 && originalWis <= 13) {
				_originalMr = 1;
			} else if(originalWis == 14) {
				_originalMr = 2;
			} else if(originalWis == 15) {
				_originalMr = 3;
			} else if(originalWis >= 16) {
				_originalMr = 4;
			} else {
				_originalMr = 0;
			}
		} else if(isWizard()) {
			if(originalWis >= 15) {
				_originalMr = 1;
			} else {
				_originalMr = 0;
			}
		} else if(isDragonKnight()) {
			if(originalWis >= 14) {
				_originalMr = 2;
			} else {
				_originalMr = 0;
			}
		} else if(isBlackWizard()) {
			if(originalWis >= 15 && originalWis <= 17) {
				_originalMr = 2;
			} else if(originalWis == 18) {
				_originalMr = 4;
			} else {
				_originalMr = 0;
			}
		}
		
	addMr(_originalMr);
	}

	public void resetOriginalMagicHit() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			if(originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 1;
			} else if(originalInt >= 14) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isKnight()) {
			if(originalInt == 10 || originalInt == 11) {
				_originalMagicHit = 1;
			} else if(originalInt == 12) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isElf()) {
			if(originalInt == 13 || originalInt == 14) {
				_originalMagicHit = 1;
			} else if(originalInt >= 15) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isDarkelf()) {
			if(originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 1;
			} else if(originalInt >= 14) {
				_originalMagicHit = 2;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isWizard()) {
			if(originalInt >= 14) {
				_originalMagicHit = 1;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isDragonKnight()) {
			if(originalInt == 12 || originalInt == 13) {
				_originalMagicHit = 2;
			} else if(originalInt == 14 || originalInt == 15) {
				_originalMagicHit = 3;
			} else if(originalInt >= 16) {
				_originalMagicHit = 4;
			} else {
				_originalMagicHit = 0;
			}
		} else if(isBlackWizard()) {
			if(originalInt >= 13) {
				_originalMagicHit = 1;
			} else {
				_originalMagicHit = 0;
			}
		}
	}

	public void resetOriginalMagicCritical() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
				_originalMagicCritical = 0;
		} else if(isKnight()) {
				_originalMagicCritical = 0;
		} else if(isElf()) {
			if(originalInt == 14 || originalInt == 15) {
				_originalMagicCritical = 2;
			} else if(originalInt >= 16) {
				_originalMagicCritical = 4;
			} else {
				_originalMagicCritical = 0;
			}
		} else if(isDarkelf()) {
				_originalMagicCritical = 0;
		} else if(isWizard()) {
			if(originalInt == 15) {
				_originalMagicCritical = 2;
			} else if(originalInt == 16) {
				_originalMagicCritical = 4;
			} else if(originalInt == 17) {
				_originalMagicCritical = 6;
			} else if(originalInt == 18) {
				_originalMagicCritical = 8;
			} else {
				_originalMagicCritical = 0;
			}
		} else if(isDragonKnight()) {
				_originalMagicCritical = 0;
		} else if(isBlackWizard()) {
				_originalMagicCritical = 0;
		}
	}

	public void resetOriginalMagicConsumeReduction() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
			if(originalInt == 11 || originalInt == 12) {
				_originalMagicConsumeReduction = 1;
			} else if(originalInt >= 13) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if(isKnight()) {
			if(originalInt == 9 || originalInt == 10) {
				_originalMagicConsumeReduction = 1;
			} else if(originalInt >= 11) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if(isElf()) {
				_originalMagicConsumeReduction = 0;
		} else if(isDarkelf()) {
			if(originalInt == 13 || originalInt == 14) {
				_originalMagicConsumeReduction = 1;
			} else if(originalInt >= 15) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		} else if(isWizard()) {
				_originalMagicConsumeReduction = 0;
		} else if(isDragonKnight()) {
				_originalMagicConsumeReduction = 0;
		} else if(isBlackWizard()) {
			if(originalInt == 14) {
				_originalMagicConsumeReduction = 1;
			} else if(originalInt >= 15) {
				_originalMagicConsumeReduction = 2;
			} else {
				_originalMagicConsumeReduction = 0;
			}
		}
	}

	public void resetOriginalMagicDamage() {
		int originalInt = getOriginalInt();
		if (isCrown()) {
				_originalMagicDamage = 0;
		} else if(isKnight()) {
				_originalMagicDamage = 0;
		} else if(isElf()) {
				_originalMagicDamage = 0;
		} else if(isDarkelf()) {
				_originalMagicDamage = 0;
		} else if(isWizard()) {
			if(originalInt >= 13) {
				_originalMagicDamage = 1;
			} else {
				_originalMagicDamage = 0;
			}
		} else if(isDragonKnight()) {
			if(originalInt == 13 || originalInt == 14) {
				_originalMagicDamage = 1;
			} else if(originalInt == 15 || originalInt == 16) {
				_originalMagicDamage = 2;
			} else if(originalInt == 17) {
				_originalMagicDamage = 3;
			} else {
				_originalMagicDamage = 0;
			}
		} else if(isBlackWizard()) {
			if(originalInt == 16) {
				_originalMagicDamage = 1;
			} else if(originalInt == 17) {
				_originalMagicDamage = 2;
			} else {
				_originalMagicDamage = 0;
			}
		}
	}

	public void resetOriginalAc() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if(originalDex >= 12 && originalDex <= 14) {
				_originalAc = 1;
			} else if(originalDex == 15 || originalDex == 16) {
				_originalAc = 2;
			} else if(originalDex >= 17) {
				_originalAc = 3;
			} else {
				_originalAc = 0;
			}
		} else if(isKnight()) {
			if(originalDex == 13 || originalDex == 14) {
				_originalAc = 1;
			} else if(originalDex >= 15) {
				_originalAc = 3;
			} else {
				_originalAc = 0;
			}
		} else if(isElf()) {
			if(originalDex >= 15 && originalDex <= 17) {
				_originalAc = 1;
			} else if(originalDex == 18) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if(isDarkelf()) {
			if(originalDex >= 17) {
				_originalAc = 1;
			} else {
				_originalAc = 0;
			}
		} else if(isWizard()) {
			if(originalDex == 8 || originalDex == 9) {
				_originalAc = 1;
			} else if(originalDex >= 10) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if(isDragonKnight()) {
			if(originalDex == 12 || originalDex == 13) {
				_originalAc = 1;
			} else if(originalDex >= 14) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		} else if(isBlackWizard()) {
			if(originalDex == 11 || originalDex == 12) {
				_originalAc = 1;
			} else if(originalDex >= 13) {
				_originalAc = 2;
			} else {
				_originalAc = 0;
			}
		}
		
		addAc(0 - _originalAc);
	}

	public void resetOriginalEr() {
		int originalDex = getOriginalDex();
		if (isCrown()) {
			if(originalDex == 14 || originalDex == 15) {
				_originalEr = 1;
			} else if(originalDex == 16 || originalDex == 17) {
				_originalEr = 2;
			} else if(originalDex == 18) {
				_originalEr = 3;
			} else {
				_originalEr = 0;
			}
		} else if(isKnight()) {
			if(originalDex == 14 || originalDex == 15) {
				_originalEr = 1;
			} else if(originalDex == 16) {
				_originalEr = 3;
			} else {
				_originalEr = 0;
			}
		} else if(isElf()) {
				_originalEr = 0;
		} else if(isDarkelf()) {
			if(originalDex >= 16) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if(isWizard()) {
			if(originalDex == 9 || originalDex == 10) {
				_originalEr = 1;
			} else if(originalDex == 11) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if(isDragonKnight()) {
			if(originalDex == 13 || originalDex == 14) {
				_originalEr = 1;
			} else if(originalDex >= 15) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		} else if(isBlackWizard()) {
			if(originalDex == 12 || originalDex == 13) {
				_originalEr = 1;
			} else if(originalDex >= 14) {
				_originalEr = 2;
			} else {
				_originalEr = 0;
			}
		}
	}

	public void resetOriginalHpr() {
		int originalCon = getOriginalCon();
		if (isCrown()) {
			if(originalCon == 13 || originalCon == 14) {
				_originalHpr = 1;
			} else if(originalCon == 15 || originalCon == 16) {
				_originalHpr = 2;
			} else if(originalCon == 17) {
				_originalHpr = 3;
			} else if(originalCon == 18) {
				_originalHpr = 4;
			} else {
				_originalHpr = 0;
			}
		} else if(isKnight()) {
			if(originalCon == 16 || originalCon == 17) {
				_originalHpr = 2;
			} else if(originalCon == 18) {
				_originalHpr = 4;
			} else {
				_originalHpr = 0;
			}
		} else if(isElf()) {
			if(originalCon == 14 || originalCon == 15) {
				_originalHpr = 1;
			} else if(originalCon == 16) {
				_originalHpr = 2;
			} else if(originalCon >= 17) {
				_originalHpr = 3;
			} else {
				_originalHpr = 0;
			}
		} else if(isDarkelf()) {
			if(originalCon == 11 || originalCon == 12) {
				_originalHpr = 1;
			} else if(originalCon >= 13) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		} else if(isWizard()) {
			if(originalCon == 17) {
				_originalHpr = 1;
			} else if(originalCon == 18) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		} else if(isDragonKnight()) {
			if(originalCon == 16 || originalCon == 17) {
				_originalHpr = 1;
			} else if(originalCon == 18) {
				_originalHpr = 3;
			} else {
				_originalHpr = 0;
			}
		} else if(isBlackWizard()) {
			if(originalCon == 14 || originalCon == 15) {
				_originalHpr = 1;
			} else if(originalCon >= 16) {
				_originalHpr = 2;
			} else {
				_originalHpr = 0;
			}
		}
	}

	public void resetOriginalMpr() {
		int originalWis = getOriginalWis();
		if (isCrown()) {
			if(originalWis == 13 || originalWis == 14) {
				_originalMpr = 1;
			} else if(originalWis >= 15) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if(isKnight()) {
			if(originalWis == 11 || originalWis == 12) {
				_originalMpr = 1;
			} else if(originalWis == 13) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if(isElf()) {
			if(originalWis >= 15 && originalWis <= 17) {
				_originalMpr = 1;
			} else if(originalWis == 18) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if(isDarkelf()) {
			if(originalWis >= 13) {
				_originalMpr = 1;
			} else {
				_originalMpr = 0;
			}
		} else if(isWizard()) {
			if(originalWis == 14 || originalWis == 15) {
				_originalMpr = 1;
			} else if(originalWis == 16 || originalWis == 17) {
				_originalMpr = 2;
			} else if(originalWis == 18) {
				_originalMpr = 3;
			} else {
				_originalMpr = 0;
			}
		} else if(isDragonKnight()) {
			if(originalWis == 15 || originalWis == 16) {
				_originalMpr = 1;
			} else if(originalWis >= 17) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		} else if(isBlackWizard()) {
			if(originalWis >= 14 && originalWis <= 16) {
				_originalMpr = 1;
			} else if(originalWis >= 17) {
				_originalMpr = 2;
			} else {
				_originalMpr = 0;
			}
		}
	}

	public void refresh() {
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		resetOriginalHpup();
		resetOriginalMpup();
		resetOriginalDmgup();
		resetOriginalBowDmgup();
		resetOriginalHitup();
		resetOriginalBowHitup();
		resetOriginalMr();
		resetOriginalMagicHit();
		resetOriginalMagicCritical();
		resetOriginalMagicConsumeReduction();
		resetOriginalMagicDamage();
		resetOriginalAc();
		resetOriginalEr();
		resetOriginalHpr();
		resetOriginalMpr();
		resetOriginalStrWeightReduction();
		resetOriginalConWeightReduction();
	}
	private final L1ExcludingList _excludingList = new L1ExcludingList();

	public L1ExcludingList getExcludingList() {
		return _excludingList;
	}

	// -- 가속기 검지 기능 --
	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(this);

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	/**
	 * 텔레포트처의 좌표
	 */
	private int _teleportX = 0;

	public int getTeleportX() {
		return _teleportX;
	}

	public void setTeleportX(int i) {
		_teleportX = i;
	}

	private int _teleportY = 0;

	public int getTeleportY() {
		return _teleportY;
	}

	public void setTeleportY(int i) {
		_teleportY = i;
	}

	private short _teleportMapId = 0;

	public short getTeleportMapId() {
		return _teleportMapId;
	}

	public void setTeleportMapId(short i) {
		_teleportMapId = i;
	}

	private int _teleportHeading = 0;

	public int getTeleportHeading() {
		return _teleportHeading;
	}

	public void setTeleportHeading(int i) {
		_teleportHeading = i;
	}

	private int _tempCharGfxAtDead;

	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	public void setTempCharGfxAtDead(int i) {
		_tempCharGfxAtDead = i;
	}

	private boolean _isCanWhisper = true;

	public boolean isCanWhisper() {
		return _isCanWhisper;
	}

	public void setCanWhisper(boolean flag) {
		_isCanWhisper = flag;
	}

	private boolean _isShowTradeChat = true;

	public boolean isShowTradeChat() {
		return _isShowTradeChat;
	}

	public void setShowTradeChat(boolean flag) {
		_isShowTradeChat = flag;
	}

	private boolean _isShowWorldChat = true;

	public boolean isShowWorldChat() {
		return _isShowWorldChat;
	}

	public void setShowWorldChat(boolean flag) {
		_isShowWorldChat = flag;
	}

	private int _fightId;

	public int getFightId() {
		return _fightId;
	}

	public void setFightId(int i) {
		_fightId = i;
	}

	private byte _chatCount = 0;

	private long _oldChatTimeInMillis = 0L;

	public void checkChatInterval() {
		long nowChatTimeInMillis = System.currentTimeMillis();
		if (_chatCount == 0) {
			_chatCount++;
			_oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		long chatInterval = nowChatTimeInMillis - _oldChatTimeInMillis;
		if (chatInterval > 2000) {
			_chatCount = 0;
			_oldChatTimeInMillis = 0;
		} else {
			if (_chatCount >= 3) {
				setSkillEffect(L1SkillId.STATUS_CHAT_PROHIBITED, 120 * 1000);
				sendPackets(new S_SkillIconGFX(36, 120));
				sendPackets(new S_ServerMessage(153)); // \f3 폐가 되는 채팅 흘려 보내기를 했으므로, 향후 2분간 채팅을 실시할 수 없습니다.
				_chatCount = 0;
				_oldChatTimeInMillis = 0;
			}
			_chatCount++;
		}
	}
	
	

	private int _callClanId;

	public int getCallClanId() {
		return _callClanId;
	}

	public void setCallClanId(int i) {
		_callClanId = i;
	}

	private int _callClanHeading;

	public int getCallClanHeading() {
		return _callClanHeading;
	}

	public void setCallClanHeading(int i) {
		_callClanHeading = i;
	}   
 //주사위
	private boolean _isGambling = false;

	public boolean isGambling() {
		return _isGambling;
	}

	public void setGambling(boolean flag) {
		_isGambling = flag;
	}
	
	private int _gamblingmoney = 0;
	
	public int getGamblingMoney(){
		return _gamblingmoney;
	}
	
	public void setGamblingMoney(int i){
		_gamblingmoney = i;
	}   
////////////////  주사위 
	 ////##########소막게임추가############
	private boolean _isGambling3 = false;

	public boolean isGambling3() {
		return _isGambling3;
	}

	public void setGambling3(boolean flag) {
		_isGambling3 = flag;
	}
	
	private int _gamblingmoney3 = 0;
	
	public int getGamblingMoney3(){
		return _gamblingmoney3;
	}
	
	public void setGamblingMoney3(int i){
		_gamblingmoney3 = i;
	}
	////##########소막게임추가############
	private int _HpregenMax = 0;
	
Random rnd = new Random();

}
