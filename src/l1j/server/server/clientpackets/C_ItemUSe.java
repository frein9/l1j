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

import java.lang.reflect.Constructor;
import java.sql.Connection; // 케릭복구주문서때문에 
import java.sql.PreparedStatement; // 케릭복구주문서때문에 
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.Statement;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;  
import l1j.server.server.ActionCodes;
import l1j.server.server.ClientThread;
import l1j.server.server.FishingTimeController;
import l1j.server.server.Announcements;
import l1j.server.server.GMCommands;
import l1j.server.server.IdFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.FurnitureSpawnTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.LogEnchantTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Cooking;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1Map;//매스
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1FurnitureInstance;
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.item.L1TreasureBox;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_AttackPacket;
import l1j.server.server.serverpackets.S_ChatPacket; 
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_ChatPacket; 
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_IdentifyDesc;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemColor; // 봉인주문서
import l1j.server.server.serverpackets.S_Letter;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_UseMap;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;
import l1j.server.server.utils.L1SpawnUtil;
import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SkillIconExp; 
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.CrockController;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.model.L1NpcDeleteTimer;


public class C_ItemUSe extends ClientBasePacket {

	private static final String C_ITEM_USE = "[C] C_ItemUSe";
	private static Logger _log = Logger.getLogger(C_ItemUSe.class.getName());

	private static Random _random = new Random();

	public C_ItemUSe(byte abyte0[], ClientThread client) throws Exception {
		super(abyte0);
		int itemObjid = readD();

		L1PcInstance pc = client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
	    ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
     if(pc.getOnlineStatus() == 0){
        client.kick();
        return;
     }
     ////중복 접속 버그방지 by 마트무사 for only 포더서버만!
		L1ItemInstance l1iteminstance = pc.getInventory().getItem(itemObjid);

		if (l1iteminstance.getItem().getUseType() == -1) { // none:사용할 수 없는 아이템
			pc
					. sendPackets(new S_ServerMessage(74, l1iteminstance
							. getLogName())); // \f1%0은 사용할 수 없습니다.
			return;
		}
		int pcObjid = pc.getId();
		if (pc.isTeleport()) { // 텔레포트 처리중
			return;
		}	
				
      //** 존재 버그 사용자 잡아보자 **//  by 도우너
       L1PcInstance jonje = L1World.getInstance().getPlayer(pc.getName());
        if (jonje == null && pc.getAccessLevel() != 200) {
            pc.sendPackets(new S_SystemMessage("존재버그 강제종료! 재접속하세요."));
            client.kick();
        return;
     }  
      //** 존재 버그 사용자 잡아보자 **//  by 도우너 

  	if (l1iteminstance == null && pc.isDead() == true) {
			return;
		}
		if (!pc.getMap().isUsableItem()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}
		int itemId;
		try {
			itemId = l1iteminstance.getItem().getItemId();
		} catch (Exception e) {
			return;
		}
		
		  //** 노딜버그 막아 보자 **//
        long nowtime = System.currentTimeMillis();
        if(pc.getItemdelay2() >=  nowtime ) {
     	if(!(itemId == 40100 || itemId == 40079 || itemId == 140100
     			|| itemId == 40099 || itemId == 40081 || itemId == 40103
     			|| l1iteminstance.getItem().getType2() == 1)) {  // 텔렉 방지 위해 추가 by 아기쿠우
         return;
     	} 
     }  
 		int l = 0;

		String s = "";
		String ak = "";
		int bmapid = 0;
		int btele = 0;
		int blanksc_skillid = 0;
		int spellsc_objid = 0;
		int spellsc_x = 0;
		int spellsc_y = 0;
		int resid = 0;
		int letterCode = 0;
		String letterReceiver = "";
		byte[] letterText = null;
		int cookStatus = 0;
		int cookNo = 0;
		int fishX = 0;
		int fishY = 0;		
 
        if (itemId == 500213){// 아크변신주문서   
            pc.sendPackets(new S_ShowPolyList(pc.getId(), "ak"));
            pc.getInventory().removeItem(l1iteminstance, 1);	
            return;
   }else if (pc.hasSkillEffect(ANTARAS)
            || pc.hasSkillEffect(BALAKAS)
            || pc.hasSkillEffect(PAPORION)) {
            pc.sendPackets(new S_ServerMessage(1384)); // 현재 상태에서는 변신할 수 없습니다.
            return;
            }
    else if(itemId == 500214) {
            pc.sendPackets(new S_ShowPolyList(pc.getId(), "ak"));
            return;
            }
  // 아크변신주문서 
		int use_type = l1iteminstance.getItem().getUseType();
		if (itemId == 40088 || itemId == 40096 || itemId == 140088) {
			s = readS();
  } else if (itemId == L1ItemId.SCROLL_OF_ENCHANT_ARMOR
				|| itemId == 350000 || itemId == 350001 || itemId == 350002 || itemId == 350003 //속성업그레이드 By추억
			    || itemId == 350004  
				|| itemId == L1ItemId.SCROLL_OF_ENCHANT_WEAPON
				|| itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON
				|| itemId == 40077 || itemId == 40078 || itemId == 40126
				|| itemId == 40098 || itemId == 40129 || itemId == 40130
				|| itemId == 140129 || itemId == 140130
				|| itemId == L1ItemId.TEST_ENCHANT_ARMOR
                || itemId == L1ItemId.TEST_ENCHANT_WEAPON
				|| itemId == 540341 || itemId == 540342			
				|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
				|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
				|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR
				|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON
				|| itemId == 41029 // 소환공의 조각
				|| itemId == 40317 || itemId == 41036 || itemId == 41245
				|| itemId == 40127 || itemId == 40128 || itemId == 40964  // 흑마법 가루 추가
				|| itemId == 41048 || itemId == 41049 || itemId == 41050 // 풀먹임 된 항해 일지 페이지
				|| itemId == 41051 || itemId == 41052 || itemId == 41053 // 풀먹임 된 항해 일지 페이지
				|| itemId == 41054 || itemId == 41055 || itemId == 41056 // 풀먹임 된 항해 일지 페이지
				|| itemId == 41057 // 풀먹임 된 항해 일지 페이지
				|| itemId == 40925 || itemId == 40926 || itemId == 40927 // 정화·신비적인 일부
				|| itemId == 40928 || itemId == 40929
				|| itemId == 40931 || itemId == 40932 || itemId == 40933 // 가공된 사파이어
				|| itemId == 40934
				|| itemId == 40935 || itemId == 40936 || itemId == 40937 // 가공된 에메랄드
				|| itemId == 40938
				|| itemId == 40939 || itemId == 40940 || itemId == 40941 // 가공된 루비
				|| itemId == 40942
				|| itemId == 40943 || itemId == 40944 || itemId == 40945 // 가공된 땅다이아
				|| itemId == 40946
				|| itemId == 40947 || itemId == 40948 || itemId == 40949 // 가공된 물다이아
				|| itemId == 40950
				|| itemId == 40951 || itemId == 40952 || itemId == 40953 // 가공된 바람 다이아
				|| itemId == 40954
				|| itemId == 40955 || itemId == 40956 || itemId == 40957 // 가공된 불다이아
				|| itemId == 40958 || itemId == 500031 || itemId == 500035 || itemId == 500037 // 균열의핵 / 하 상자
				|| itemId == 350011 || itemId == 350012 || itemId == 500011 || itemId == 500012// 창천 갑옷/무기마법주문서 추가 . 봉인 해재 주문서
				|| itemId == 500061 || itemId == 500063) {  //쿠쿨 상,하 상자.
			l = readD();
		} else if (itemId == 140100 || itemId == 40100 || itemId == 40099
				|| itemId == 40086 || itemId == 40863) {
			bmapid = readH();
			btele = readD();
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,
					false));
		} else if (itemId == 40090 || itemId == 40091 || itemId == 40092
				|| itemId == 40093 || itemId == 40094) { // 공백 스크롤(Lv1)~(Lv5)
			blanksc_skillid = readC();
		} else if (use_type == 30 || itemId == 40870 || itemId == 40879) { // spell_buff
			spellsc_objid = readD();
		} else if (use_type == 5 || use_type == 17) { // spell_long, spell_short
			spellsc_objid = readD();
			spellsc_x = readH();
			spellsc_y = readH();
		} else if (itemId == 40089 || itemId == 140089) { // 부활 스크롤, 축복된 부활 스크롤
			resid = readD();
		} else if (itemId == 40310 || itemId == 40311
				|| itemId == 40730 || itemId == 40731 || itemId == 40732) { // 편지지
			letterCode = readH();
			letterReceiver = readS();
			letterText = readByte();
		} else if (itemId >= 41255 && itemId <=41259) { // 요리의 책
			cookStatus = readC();
			cookNo = readC();
		} else if (itemId == 41293 || itemId == 41294) { // 낚싯대
			fishX = readH();
			fishY = readH();
		} else {
			l = readC();
		}

		if (pc.getCurrentHp() > 0) {
			int delay_id = 0;
			if (l1iteminstance.getItem().getType2() == 0) { // 종별：그 외의 아이템
				delay_id = ((L1EtcItem) l1iteminstance.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}

			// 재사용 체크
			boolean isDelayEffect = false;
			if (l1iteminstance.getItem().getType2() == 0) {
				int delayEffect = ((L1EtcItem) l1iteminstance.getItem())
						.get_delayEffect();
				if (delayEffect > 0) {
					isDelayEffect = true;
					Timestamp lastUsed = l1iteminstance.getLastUsed();
				if (lastUsed != null) {
					Calendar cal = Calendar.getInstance();
				if ((cal.getTimeInMillis() - lastUsed.getTime()) / 1000 <= delayEffect) {
					pc.sendPackets(new S_ServerMessage(79));
					return;
					}
				  }
				}
			  }
			       L1ItemInstance l1iteminstance1 = pc.getInventory().getItem(l);
			       _log.finest("request item use (obj) = " + itemObjid + " action = "
					+ l + " value = " + s);

			 //** 아이템 딜레이타이머 추가 **// 
        if (l1iteminstance.getItem().getType2() == 0) {
         int delay_time = 0;
             delay_time = ((L1EtcItem) l1iteminstance.getItem()).get_delaytime();
        if(pc.getItemdelay2() <=  nowtime ){
            pc.setItemdelay2( nowtime + delay_time ); 
         //** 아이템 딜레이타이머 추가 **//  

		if (itemId == 40077 || itemId == L1ItemId.SCROLL_OF_ENCHANT_WEAPON
					|| itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON
					|| itemId == 40130 || itemId == 140130
					|| itemId == L1ItemId.TEST_ENCHANT_WEAPON
                    || itemId == 540342
					|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
					|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON
					|| itemId == 40128 || itemId == 350012
					|| itemId == 350000 || itemId == 350001
				    || itemId == 350002 || itemId == 350003) { // 무기 강화 스크롤, 창천 무기 주문서 추가
		if (l1iteminstance1 == null
					|| l1iteminstance1.getItem().getType2() != 1) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
		if (l1iteminstance1.getLockitem() > 100){
	        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
	            return;
	            }
			int safe_enchant = l1iteminstance1.getItem().get_safeenchant();
		if (safe_enchant < 0) { // 강화 불가
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}

			int quest_weapon = l1iteminstance1.getItem().getItemId();
		if (quest_weapon >= 246 && quest_weapon <= 249) { // 강화 불가
		if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) { // 시련의 스크롤
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		if (itemId == L1ItemId.SCROLL_OF_ENCHANT_QUEST_WEAPON) { // 시련의 스크롤
		if (quest_weapon >= 246 && quest_weapon <= 249) { // 강화 불가
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		int weaponId = l1iteminstance1.getItem().getItemId();
		if (weaponId == 36 || weaponId == 183
			|| weaponId >= 250 && weaponId <= 255) { // 일루젼 무기
		if (itemId == 40128) { // 일루젼 무기 강화 스크롤
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		if (itemId == 40128) { // 일루젼 무기 강화 스크롤
		if (weaponId == 36 || weaponId == 183
			|| weaponId >= 250 && weaponId <= 255) { // 일루젼 무기
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
				/* 창천 무기 마법 주문서 추가 */
		if (weaponId >= 231 && weaponId <= 240) { // 창천 무기
		if (itemId == 350012) { // 창천 무기 마법 주문서
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		if (itemId == 350012) { // 창천 무기 마법 주문서
		if (weaponId >= 231 && weaponId <= 240) { // 창천 무기
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
				/* 창천 무기 마법 주문서 추가 */
				/* 여행자 무기 마법 주문서 추가 */
	   int fantasy_weapon = l1iteminstance1.getItem().getItemId();

	   if (fantasy_weapon == 7 || fantasy_weapon == 35 || fantasy_weapon == 48 || fantasy_weapon == 73 || fantasy_weapon == 105 || fantasy_weapon == 120 || fantasy_weapon == 147 || fantasy_weapon == 156 || fantasy_weapon == 174 || fantasy_weapon == 175 || fantasy_weapon == 224) { // 상아탑 무기
       if (itemId != 540342) { // 여행자 무기 마법 주문서
             pc.sendPackets(new S_ServerMessage(79)); // f1 아무것도 일어나지 않았습니다.
                return;
                }
                }
       if (itemId == 540342) { // 여행자 무기 마법 주문서
       if (fantasy_weapon == 7 || fantasy_weapon == 35 || fantasy_weapon == 48 || fantasy_weapon == 73 || fantasy_weapon == 105 || fantasy_weapon == 120 || fantasy_weapon == 147 || fantasy_weapon == 156 || fantasy_weapon == 174 || fantasy_weapon == 175 || fantasy_weapon == 224) { // 상아탑 무기
	} else { // 강화 불가
             pc.sendPackets(new S_ServerMessage(79)); // f1 아무것도 일어나지 않았습니다.
                 return;
                }
                }
				/* 여행자 무기 마법 주문서 추가 */
		if(itemId == 350000 && //바람
			      l1iteminstance1.getAttrEnchantLevel() != 0 
			      && l1iteminstance1.getAttrEnchantLevel() != 8
			      && l1iteminstance1.getAttrEnchantLevel() != 7){
			     pc.sendPackets(new S_ServerMessage(1294));
			     return;
			    }

			    if(itemId == 350001 && //땅
			      l1iteminstance1.getAttrEnchantLevel() != 0 
			      && l1iteminstance1.getAttrEnchantLevel() != 11
			      && l1iteminstance1.getAttrEnchantLevel() != 10){
			     pc.sendPackets(new S_ServerMessage(1294));
			     return;
			    }

			    if(itemId == 350002 && //불
			      l1iteminstance1.getAttrEnchantLevel() != 0 
			      && l1iteminstance1.getAttrEnchantLevel() != 2
			      && l1iteminstance1.getAttrEnchantLevel() != 1){
			     pc.sendPackets(new S_ServerMessage(1294));
			     return;
			    }

			    if(itemId == 350003 && //물
			      l1iteminstance1.getAttrEnchantLevel() != 0 
			      && l1iteminstance1.getAttrEnchantLevel() != 5
			      && l1iteminstance1.getAttrEnchantLevel() != 4){
			     pc.sendPackets(new S_ServerMessage(1294));
			     return;
			    }
		int enchant_level = l1iteminstance1.getEnchantLevel();
		if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_WEAPON) { // c-dai
			pc.getInventory().removeItem(l1iteminstance, 1);
		if (enchant_level < -6) {
						// -7이상은 할 수 없다.
			FailureEnchant(pc, l1iteminstance1, client);
	} else {
			SuccessEnchant(pc, l1iteminstance1, client, -1);
			}
		   /** 속성인첸트 일팩화
		       attr_enchantlvl 의 1< 2 <3은 불 4< 5 <6 은 물 7< 8 <9는 바람 10 <11< 12는 땅
		     */
	} else if ( itemId == 350000 || itemId == 350001
		     || itemId == 350002 || itemId == 350003) {
		    AttrEnchant(pc, l1iteminstance1, itemId);
		    pc.getInventory().removeItem(l1iteminstance, 1);
		   /** 속성인첸트 일팩화 */
	} else if (enchant_level < safe_enchant) {
			pc.getInventory().removeItem(l1iteminstance, 1);
			SuccessEnchant(pc, l1iteminstance1, client,RandomELevel(l1iteminstance1, itemId));
	} else {
			pc.getInventory().removeItem(l1iteminstance, 1);
			int rnd = _random.nextInt(100) + 1;
			int enchant_chance_wepon;
		if (enchant_level >= Config.MAX_WEAPON_ENCHANT)  {  // 웨폰 인챈트 제한
			pc.sendPackets(new S_ServerMessage(79)); 
				return; 
				}
		switch (enchant_level){
			
			       case 7: 
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 6;
					   break;
				   case 8:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 7;
				       break;
				   case 9:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 9;
				       break;
				   case 10:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 10;
				       break;
				   case 11:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 11;
				       break;
				   case 12:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 12;
				       break;
				   case 13:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 13;
				       break;
				   case 14:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 14;
				       break;
				   case 15:
					   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 15;	  
					   break;
			       default :
			    	   enchant_chance_wepon = (100 + 3 * Config.ENCHANT_CHANCE_WEAPON) / 2;	  
					   
				   }

		if (rnd < enchant_chance_wepon) {
		int randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
			SuccessEnchant(pc, l1iteminstance1, client, randomEnchantLevel);
		if(enchant_level >= 11){ // 인챈 성공시 전체 메세지
            Announcements.getInstance().announceToAll((pc.getName()+"님께서 +"+l1iteminstance1.getEnchantLevel()+" "+l1iteminstance1.getName()+" 성공하였습니다!"));
           }
	} else if (enchant_level >= Config.MAX_WEAPON_ENCHANT && rnd < (enchant_chance_wepon * 2)) {
						// \f1%0이%2과 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
			pc.sendPackets(new S_ServerMessage(160, l1iteminstance1.getLogName(), "$245", "$248"));
	} else {
			FailureEnchant(pc, l1iteminstance1, client); 
        if(enchant_level >= 11){ // 실패 성공시 전체 메세지
            Announcements.getInstance().announceToAll((pc.getName()+"님께서 +"+l1iteminstance1.getEnchantLevel()+" "+l1iteminstance1.getName()+" 실패하였습니다!"));
            }
			}
			}
	} else if (itemId == 40078
					|| itemId == L1ItemId.SCROLL_OF_ENCHANT_ARMOR
					|| itemId == 40129 || itemId == 140129
					|| itemId == L1ItemId.TEST_ENCHANT_ARMOR
                    || itemId == 540341
					|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
					|| itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR
					|| itemId == 40127 || itemId == 350011) { // 방어용 기구 강화 스크롤 , 창천 갑옷 주문서 추가
		if (l1iteminstance1 == null
					|| l1iteminstance1.getItem().getType2() != 2) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
        if (l1iteminstance1.getLockitem() > 100){
            pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
                return;
                }
		int safe_enchant = ((L1Armor) l1iteminstance1.getItem())
			.get_safeenchant();
		if (safe_enchant < 0) { // 강화 불가
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
		int armorId = l1iteminstance1.getItem().getItemId();
		if (armorId == 20161 || armorId >= 21035 && armorId <= 21038) { // 일루젼 방어용 기구
		if (itemId == 40127) { // 일루젼 방어용 기구 강화 스크롤
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		if (itemId == 40127) { // 일루젼 방어용 기구 강화 스크롤
		if (armorId == 20161 || armorId >= 21035 && armorId <= 21038) { // 일루젼 방어용 기구
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
				/* 창천 갑옷 마법 주문서 추가 */
		if (armorId >= 20480 && armorId <= 20486) { // 창천 방어구
		if (itemId == 350011) { // 창천 갑옷 마법 주문서
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
		if (itemId == 350011) { // 창천 갑옷 마법 주문서
		if (armorId >= 20480 && armorId <= 20486) { // 창천 방어구
	} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				return;
				}
				}
				/* 창천 갑옷 마법 주문서 추가 */
				/* 여행자 갑옷 마법 주문서 추가 */
		int fantasy_armor = l1iteminstance1.getItem().getItemId();

		if (fantasy_armor == 20028 || fantasy_armor == 20082 || fantasy_armor == 20126 || fantasy_armor == 20173 || fantasy_armor == 20206 || fantasy_armor == 20232 || fantasy_armor == 20080) { // 상아탑 갑옷
        if (itemId != 540341) { // 여행자 갑옷 마법 주문서
            pc.sendPackets(new S_ServerMessage(79)); // f1 아무것도 일어나지 않았습니다.
                 return;
                    }
                }
        if (itemId == 540341) { // 여행자 갑옷 마법 주문서
        if (fantasy_armor == 20028 || fantasy_armor == 20082 || fantasy_armor == 20126 || fantasy_armor == 20173 || fantasy_armor == 20206 || fantasy_armor == 20232 || fantasy_armor == 20080) { // 상아탑 갑옷
     } else { // 강화 불가
             pc.sendPackets(new S_ServerMessage(79)); // f1 아무것도 일어나지 않았습니다.
                 return;
                    }
                }
				/* 여행자 갑옷 마법 주문서 추가 */
		int enchant_level = l1iteminstance1.getEnchantLevel();
		if (itemId == L1ItemId.C_SCROLL_OF_ENCHANT_ARMOR) { // c-zel
			pc.getInventory().removeItem(l1iteminstance, 1);
		if (enchant_level < -6) {
						// -7이상은 할 수 없다.
			FailureEnchant(pc, l1iteminstance1, client);
	} else {
			SuccessEnchant(pc, l1iteminstance1, client, -1);
				}
	} else if (enchant_level < safe_enchant) {
			pc.getInventory().removeItem(l1iteminstance, 1);
			SuccessEnchant(pc, l1iteminstance1, client, RandomELevel(l1iteminstance1, itemId));
	} else {
			pc.getInventory().removeItem(l1iteminstance, 1);
		int rnd = _random.nextInt(100) + 1;
		int enchant_chance_armor;
		int enchant_level_tmp;
		if (safe_enchant == 0) { // 뼈, 브락크미스릴용 보정
			enchant_level_tmp = enchant_level + 2;
	} else {
			enchant_level_tmp = enchant_level;
			}
		if (enchant_level >= Config.MAX_ARMOR_ENCHANT) {  // 아머 인챈트 제한 
			pc.sendPackets(new S_ServerMessage(79));
				return; 
				} 
		if (enchant_level >= Config.MAX_ARMOR_ENCHANT) {
			enchant_chance_armor = (100 + enchant_level_tmp * Config.ENCHANT_CHANCE_ARMOR)
			/ (enchant_level_tmp * 2);
	} else {
			enchant_chance_armor = (100 + enchant_level_tmp * Config.ENCHANT_CHANCE_ARMOR)
			/ enchant_level_tmp;
			}

		if (rnd < enchant_chance_armor) {
		int randomEnchantLevel = RandomELevel(l1iteminstance1, itemId);
			SuccessEnchant(pc, l1iteminstance1, client, randomEnchantLevel);
		if(enchant_level >= 9){ // 인챈 성공시 전체 메세지
            Announcements.getInstance().announceToAll((pc.getName()+"님께서 +"+l1iteminstance1.getEnchantLevel()+" "+l1iteminstance1.getName()+" 성공하였습니다!"));
             }
	} else if (enchant_level >= 9 && rnd < (enchant_chance_armor * 2)) {
			String item_name_id = l1iteminstance1.getName();
			String pm = "";
			String msg = "";
		if (enchant_level > 0) {
			pm = "+";
			}
			msg = (new StringBuilder()).append(pm + enchant_level).append(" ")
			.append(item_name_id).toString();
						// \f1%0이%2과 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
			pc.sendPackets(new S_ServerMessage(160, msg, "$252", "$248"));
	} else {
			FailureEnchant(pc, l1iteminstance1, client);
	     if(enchant_level >= 55){ // 인챈 실패시 전체 메세지
            Announcements.getInstance().announceToAll((pc.getName()+"님께서 +"+l1iteminstance1.getEnchantLevel()+" "+l1iteminstance1.getName()+" 실패하였습니다!"));
			}
			}
			}
	} else if (l1iteminstance.getItem().getType2() == 0) { // 종별：그 외의 아이템
		    int item_minlvl = ((L1EtcItem) l1iteminstance.getItem())
				.getMinLevel();
			int item_maxlvl = ((L1EtcItem) l1iteminstance.getItem())
				.getMaxLevel();
			if (item_minlvl != 0 && item_minlvl > pc.getLevel() && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(318, String.valueOf(item_minlvl))); // 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
					return;
	} else if (item_maxlvl != 0 && item_maxlvl < pc.getLevel() && !pc.isGm()) {
				pc.sendPackets(new S_ServerMessage(673, String.valueOf(item_maxlvl))); // 이 아이템은%d레벨 이상만 사용할 수 있습니다.
					return;
				}

			if ((itemId == 40576 && !pc.isElf()) // 영혼의 결정의 파편(흰색)
						|| (itemId == 40577 && !pc.isWizard()) // 영혼의 결정의 파편(흑)
						|| (itemId == 40578 && !pc.isKnight())) { // 영혼의 결정의 파편(빨강)
				pc.sendPackets(new S_ServerMessage(264)); // \f1당신의 클래스에서는 이 아이템은 사용할 수 없습니다.
					return;
				}

			if (l1iteminstance.getItem().getType() == 0) { // 아로
				pc.getInventory().setArrow(l1iteminstance.getItem().getItemId());
				pc.sendPackets(new S_ServerMessage(452, l1iteminstance.getLogName())); // %0가 선택되었습니다.
	} else if (l1iteminstance.getItem().getType() == 15) { // 스팅
				pc.getInventory().setSting(l1iteminstance.getItem().getItemId());
				pc.sendPackets(new S_ServerMessage(452, l1iteminstance.getLogName()));
	} else if (l1iteminstance.getItem().getType() == 16) { // treasure_box
				L1TreasureBox box = L1TreasureBox.get(itemId);
			if (box != null) {
			if (box.open(pc)) {
				L1EtcItem temp = (L1EtcItem) l1iteminstance.getItem();
			if (temp.get_delayEffect() > 0) {
				isDelayEffect = true;
	} else {
				pc.getInventory().removeItem(l1iteminstance.getId(), 1);
				}
				}
				}
	} else if (l1iteminstance.getItem().getType() == 2) { // light계 아이템
			if (l1iteminstance.getRemainingTime() <= 0 && itemId != 40004) {
					return;
					}
			if (l1iteminstance.isNowLighting()) {
				l1iteminstance.setNowLighting(false);
				pc.turnOnOffLight();
	} else {
				l1iteminstance.setNowLighting(true);
				pc.turnOnOffLight();
				}
				pc.sendPackets(new S_ItemName(l1iteminstance));
	} else if (itemId == 40003) { // 랜턴 오일
			for (L1ItemInstance lightItem : pc.getInventory().getItems()) {
			if (lightItem.getItem().getItemId() == 40002) {
				lightItem.setRemainingTime(l1iteminstance.getItem().getLightFuel());
				pc.sendPackets(new S_ItemName(lightItem));
				pc.sendPackets(new S_ServerMessage(230)); // 랜턴에 오일을 따랐습니다.
					break;
					}
					}
				pc.getInventory().removeItem(l1iteminstance, 1);
		
				//// 6종 스펠북  /////
				} else if (itemId == 400062) { // 디스스톰 마법서
			        L1Object target = L1World.getInstance()
			      .findObject(spellsc_objid);
			       if (pc.glanceCheck(target.getX(), target.getY()) == false) {
			        pc.sendPackets(new S_ServerMessage(328)); 
			        return; // 직선상에 장애물이 있다
			      }
			      if (target instanceof L1PcInstance) { // 타겟이 PC 안됨
			       L1PcInstance targetpc = (L1PcInstance) target;
			       pc.sendPackets(new S_ServerMessage(328));
			       return;
			       }
			      if (target instanceof L1MerchantInstance) { // 타겟이 NPC 안됨
			       L1MerchantInstance targetpc = (L1MerchantInstance) target;
			       pc.sendPackets(new S_ServerMessage(328));
			       return;
			      }

			      if (pc.isInParty()) { // 파티중 
			       pc.sendPackets(new S_SystemMessage("파티중에는 '디스 스톰'을 사용 할 수 없습니다.")); 
			       return;
			      }
			       if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)
			         || pc.hasSkillEffect(SILENCE)
			         || pc.hasSkillEffect(AREA_OF_SILENCE)
			         || pc.hasSkillEffect(STATUS_POISON_SILENCE)
			         || pc.isGhost() || pc.isInvisble() 
			       ) {//사용할 수 없다 엡솔 & 침묵상태<<<영창할수없기때문에 ㅎ
			       pc.sendPackets(new S_SystemMessage("'디스 스톰'을 사용 할 수 없는 상태입니다.")); 
			       return;
			       }
			      if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			       pc.sendPackets(new S_SystemMessage("'디스 스톰'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			       return;
			       }
			      if (pc.getCurrentMp() < 400) {//엠피 400이하 일때 
			       pc.sendPackets(new S_ServerMessage(278)); //엠피가 부족하여 마법을 사용할 수 없습니다.?
			       return;
			       } 
			       pc.sendPackets(new S_ChatPacket(pc, "나의 앞을 막아서는 모든 어리석고 하찮은 몬스터들에게" , 
                   Opcodes.S_OPCODE_NORMALCHAT, 2));
			       pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));
			       pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));
			       pc.sendPackets(new S_SkillSound(pc.getId() , 1127));
			       pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127)); 
			       Thread.sleep(1500);
			       pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			       pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			       pc.sendPackets(new S_ChatPacket(pc, "죽은영혼마저도 파괴시켜버리는..아인하사드의 천공의창!" , 
			       Opcodes.S_OPCODE_NORMALCHAT, 2));
			       Thread.sleep(1500);
			       pc.sendPackets(new S_ChatPacket(pc, "디스 스톰!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			       pc.broadcastPacket(new S_ChatPacket(pc, "디스 스톰!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			       pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			       pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			       pc.sendPackets(new S_SkillSound(target.getId() , 4825));// 보라?마법진????
			     for (L1Object obj : L1World.getInstance().getVisibleObjects(target, 5)) { // 5 범위 내에 오브젝트를 찾아서
			     if (obj instanceof L1MonsterInstance){ // 몬스터라면
			       L1NpcInstance npc = (L1NpcInstance) obj;
			       npc.receiveDamage(pc, 0);
			     if (npc.getCurrentHp() > 0){
			       pc.sendPackets(new S_SkillSound(obj.getId() , 1815)); // 옆 번호가 이펙트 번호입니다..현재 디스 설정
			       pc.broadcastPacket(new S_SkillSound(obj.getId() , 1815));
			       npc.receiveDamage(pc, 200); // 옆의 숫자가 데미지 입니다.. 
			       Thread.sleep(270);
			       pc.sendPackets(new S_SkillSound(obj.getId() , 1815)); 
			       pc.broadcastPacket(new S_SkillSound(obj.getId() , 1815));
			       npc.receiveDamage(pc, 300); //       
			       Thread.sleep(280);
			       pc.sendPackets(new S_SkillSound(obj.getId() , 1815)); 
			       pc.broadcastPacket(new S_SkillSound(obj.getId() , 1815));
			       npc.receiveDamage(pc, 400); // 
			    }else{
			       pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 
			       pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));
			      }
			     }
			    }
			 } else if (itemId == 400057) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'라이트닝 쇼크'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'라이트닝 쇼크'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'라이트닝 쇼크'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
                      //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "신들의 왕 제우스시여!" , 
                        Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "당신의 분노의힘을 빌려주시옵소서!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "라이트닝 쇼크!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "라이트닝 쇼크!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 4842)); // 옆 번호가 이펙트 번호입니다..현재 디스 설정
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 4842));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 1000 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
                        }
			           }
			          }
			 } else if (itemId == 400058) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'워터 슬램'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'워터 슬램'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'워터 슬램'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
                      //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "바다의신 포세이돈이시여...." , 
			            Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "그대의 위대한 힘앞의 적을 무릎 꿇게 하소서..." , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "워터 슬램!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "워터 슬램!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 5789)); // 옆 번호가 이펙트 번호입니다..현재 디스 설정
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 5789));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 1000 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
                        }
			           }
			          }
			 } else if (itemId == 400059) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'미티어 레인'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'미티어 레인'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'미티어 레인'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
			          //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "불의신 아폴론의 명을 받들어..." , 
			            Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "너희에게 심판을 내릴지어다." , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "미티어 레인!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "미티어 레인!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 762)); // 옆 번호가 이펙트 번호입니다..현재 디스 설정
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 762));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 1000 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
                        }
			           }
			          }
			 } else if (itemId == 400060) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'윈드 케인'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'윈드 케인'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'윈드 케인'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
			          //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "바람의 신 아이올로스.." , 
			            Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "그대와 나 힘을 합쳐 암흑의 구름을 날려버릴 지어다." , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "윈드 케인!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "윈드 케인!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 4784)); // 옆 번호가 이펙트 번호입니다..현재 디스 설정
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 4784));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 1000 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
			            }
			           }
			          }
			 } else if (itemId == 400061) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'가이아 앵글'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'가이아 앨글'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'가이아 앵글'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
			          //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "대지를 더럽힌자..." , 
			            Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "대지의신 가이아를 대신하여 처단하리.." , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "가이아 앵글!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "가이아 앵글!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 7617)); // 옆 번호가 이펙트 번호입니다..현재 이팩트 워터 슬램과 같음 
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 7617));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 500 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
			            }
			           }
			          }
			 } else if (itemId == 400056) { // ectitem에 아무거나 만드세요 
			        if (pc.isGhost() || pc.isInvisble()) { // 인비지 상태에서 불가 ㅎ
			            pc.sendPackets(new S_SystemMessage("'딥 임펙트 블리자드'을 사용 할 수 없습니다.")); 
			            return;
			            }
			        if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {//엡솔 상태에서도 불가 
			            pc.sendPackets(new S_SystemMessage("'딥 임펙트 블리자드'을 사용 할 수 없습니다.")); 
			            return;
			            } 
			        if (!pc.getInventory().checkItem(40318, 10)) { // 마돌 10개가 없다면
			            pc.sendPackets(new S_SystemMessage("'딥 임펙트 블리자드'을 시전하기위해서는 마력의돌이 10개가 필요합니다.")); 
			            return;
			            }
			        if (pc.getCurrentMp() < 200) {//엠피 200이하 일때 
			            pc.sendPackets(new S_ServerMessage(278)); 
			            return;
			            } 
			          //pc.getInventory().removeItem(l1iteminstance , 1);//소비하게하기.... 앞에 주석처리해제ㅎ
			            pc.sendPackets(new S_ChatPacket(pc, "눈의 여왕의 힘을 빌어..." , 
			            Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.sendPackets(new S_DoActionGFX(pc.getId(), 18));//모션
			            pc.broadcastPacket(new S_DoActionGFX(pc.getId() , 18));//모션
			            Thread.sleep(500);
			            pc.sendPackets(new S_SkillSound(pc.getId() , 1127));//마법진 다엘 마법진으로 
			            pc.broadcastPacket(new S_SkillSound(pc.getId() , 1127));//마법진
			            pc.setCurrentMp(pc.getCurrentMp() - 200);//엠소비
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "혹한의 바람으로 처단하리.." , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            Thread.sleep(1500);
			            pc.sendPackets(new S_ChatPacket(pc, "딥 임펙트 블리자드!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.broadcastPacket(new S_ChatPacket(pc, "딥 임펙트 블리자드!" , Opcodes.S_OPCODE_NORMALCHAT, 2));
			            pc.getInventory().consumeItem(40318, 10); // 마돌 10개 소모
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 7)) { // 7 범위 내에 오브젝트를 찾아서
			        if (obj instanceof L1MonsterInstance){ // 몬스터라면
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0); 
			        if (npc.getCurrentHp() > 0){//몬스터가 살아있다!!
			            pc.sendPackets(new S_SkillSound(obj.getId() , 7771)); // 옆 번호가 이펙트 번호입니다..현재 이팩트 워터 슬램과 같음 
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 7771));
			            npc.receiveDamage(pc, 500); // 옆의 숫자가 데미지 입니다.. 현재 500 설정
			    }else{
			            pc.sendPackets(new S_SkillSound(obj.getId() , 3740)); // 몬스터 시체위에 이펙트
			            pc.broadcastPacket(new S_SkillSound(obj.getId() , 3740));//
			            }
			           }
			          }
			 } else if (itemId == 400063) { //    미티어  스톰
			            pc.sendPackets(new S_SkillSound(pc.getId(), 761));
			            pc.broadcastPacket(new S_SkillSound(pc.getId(), 761));
			            Thread.sleep(100);
			            pc.broadcastPacket(new S_ChatPacket(pc,"Dark rain !!!", Opcodes.S_OPCODE_NORMALCHAT, 2));
			        for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 8)) {
			        if (obj instanceof L1MonsterInstance) {
			            L1NpcInstance npc = (L1NpcInstance) obj;
			            npc.receiveDamage(pc, 0);
			        if (npc.getCurrentHp() > 0) {
			            pc.sendPackets(new S_SkillSound(obj.getId(), 762));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 762));
			            npc.receiveDamage(pc, 500); 
			            Thread.sleep(50);
			            pc.sendPackets(new S_SkillSound(obj.getId(), 6574));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 6574));
			            npc.receiveDamage(pc, 800);    
			            Thread.sleep(10);
			            pc.sendPackets(new S_SkillSound(obj.getId(), 1811));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 1811));
			            npc.receiveDamage(pc, 1000);  
			            Thread.sleep(5);
			            pc.sendPackets(new S_SkillSound(obj.getId(), 762));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 762));
			            npc.receiveDamage(pc, 500);
			            Thread.sleep(50);
			            pc.sendPackets(new S_SkillSound(obj.getId(), 6574));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 6574));
			            npc.receiveDamage(pc, 800);        
			            Thread.sleep(10);
			            pc.sendPackets(new S_SkillSound(obj.getId(), 1811));
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 1811));
			            npc.receiveDamage(pc, 1000);       
			            Thread.sleep(5);
			    } else {
			            pc.sendPackets(new S_SkillSound(obj.getId(), 6574)); 
			            pc.broadcastPacket(new S_SkillSound(obj.getId(), 6574));
			            }
			           }
			          }
			 } else if (itemId >= 49410	&& itemId <= 49416) { 
		                cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
		            int skillid = itemId - 40858;
		            if (itemId == 49410) {		  // 수룡의 마안				
			            skillid = 7672;  	
		     } else if (itemId == 49411) { // 지룡의 마안					
			            skillid = 7671; 	
		     } else if (itemId == 49412) { // 풍룡의 마안				
			            skillid = 7673;
		     } else if (itemId == 49413) { // 화룡의 마안				
			            skillid = 7674;
		     } else if (itemId == 49414) { // 생명의 마안				
			            skillid = 7678;
		     } else if (itemId == 49415) { // 탄생의 마안					
			            skillid = 7675;
		     } else if (itemId == 49416) { // 형상의 마안				
			            skillid = 7676;
		               }
		               pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
		               L1SkillUse l1skilluse = new L1SkillUse();
		               l1skilluse.handleCommands(client.getActiveChar(), skillid,
				       spellsc_objid, spellsc_x, spellsc_y, null, 0,
				       L1SkillUse.TYPE_SPELLSC);  
                       pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동
		/*	 } else if (itemId == 555574) {	// 황색 해츨링 알(암컷)
						L1SpawnUtil.spawn(pc, 777790, 0, 300000);
						pc.getInventory().removeItem(l1iteminstance, 1);
			 } else if (itemId == 555575) {	// 녹색 해츨링 알(수컷)
						L1SpawnUtil.spawn(pc, 777787, 0, 300000);
						pc.getInventory().removeItem(l1iteminstance, 1);  */
			       //////////////////////////봉인 주문서 //////////////////////////////
			 } else if (itemId == 500011){  // 봉인줌서
				    if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 1 && l1iteminstance1.getLockitem() < 100
				     || l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2 && l1iteminstance1.getLockitem() < 100) {
					if (l1iteminstance1.getLockitem() < 100) {
					if (!pc.isGm()) {
	                if (pc.getSealingPW() == null || pc.getSealingPW() == "") {
	                    pc.sendPackets(new S_SystemMessage("봉인암호를 설정 해주십시오."));
	                    pc.sendPackets(new S_SystemMessage("사용 예) .봉인암호 원하는암호"));
	                    return;
	                    }
	                    }
				    int newLockitem = l1iteminstance1.getItem().getBless() + 128;
				        l1iteminstance1.setLockitem(newLockitem);
				        client.getActiveChar().getInventory().updateItem(l1iteminstance1,L1PcInventory.COL_LOCKITEM);
                        pc.sendPackets(new S_ItemColor(l1iteminstance1));
				        CharactersItemStorage storage = CharactersItemStorage.create();
				        storage.updateItemLockitem(l1iteminstance1);
				        pc.getInventory().removeItem(l1iteminstance, 1);	
			  } else {
	                    pc.sendPackets(new S_SystemMessage("이미 봉인되어 있는 아이템입니다."));
	                    }
			  } else pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				
			  } else if (itemId == 555582) { // 자기 계정 군주 혈맹에 가입하기
					 if (pc.isCrown()) { // 군주라면
					 if(pc.get_sex() == 0){ // 왕자라면
					    pc.sendPackets(new S_ServerMessage(87)); // 당신은 왕자입니다
			  } else { // 공주라면
					    pc.sendPackets(new S_ServerMessage(88)); // 당신은 공주입니다
					    }
					    return;
					    }
					 if(pc.getClanid() != 0){ // 혈맹이 있다면
					    pc.sendPackets(new S_ServerMessage(89)); // 이미 혈맹이 있습니다
					    return;
					    }
					    Connection con = null;
					    con = L1DatabaseFactory.getInstance().getConnection();
					    Statement pstm2 = con.createStatement(); 
					    ResultSet rs2 = pstm2.executeQuery("SELECT `account_name`, `char_name`, `ClanID`, `Clanname` FROM `characters` WHERE Type = 0"); // 케릭터 테이블에서 군주만 골라와서
					 while(rs2.next()){
					 if(pc.getNetConnection().getAccountName().equalsIgnoreCase(rs2.getString("account_name"))){ // 현재 접속한 계정과 계정을 비교해서 동일하면
					 if(rs2.getInt("ClanID") != 0){ // 군주의 혈맹이 있다면
					    L1Clan clan = L1World.getInstance().getClan(rs2.getString("Clanname")); // 군주의 혈맹으로 가입
					    L1PcInstance clanMember[] = clan.getOnlineClanMember();
					 for (int cnt = 0; cnt < clanMember.length; cnt++) { // 접속한 혈맹원에게 메세지 뿌리고
					    clanMember[cnt].sendPackets(new S_ServerMessage(94, pc.getName())); // f1%0이 혈맹의 일원으로서 받아들여졌습니다.
					    }
					    pc.setClanid(rs2.getInt("ClanID"));
					    pc.setClanname(rs2.getString("Clanname"));
					    pc.save(); // DB에 캐릭터 정보를 기입한다
					    clan.addMemberName(pc.getName()); // 맵에 넣고
					    pc.sendPackets(new S_ServerMessage(95, rs2.getString("Clanname"))); // f1%0 혈맹에 가입했습니다. // 메세지 보내고
					    pc.getInventory().removeItem(l1iteminstance, 1); // 아이템을 지워주고 끝
					    break;
					    }
					    }
					    }
					    rs2.first(); // 쿼리를 처음으로 되돌리고
					    rs2.close();//여기부터 아래까지 리소스삭제부분 
					    pstm2.close();
					    con.close();
					 if(pc.getClanid() == 0){ // 혈맹이 있다면
					    pc.sendPackets(new S_SystemMessage("계정내에 군주가 없거나 혈맹이 창설되지 않았습니다.")); // 메세지 보내고
					    }

				   ////////////////////// 봉인 해제 //////////////////
			 } else if (itemId == 500012){  // 봉인해제줌서
				    if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 1 && l1iteminstance1.getLockitem() > 100
				     || l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2 && l1iteminstance1.getLockitem() > 100) {
				    if (l1iteminstance1.getLockitem() > 100) {
					if (!pc.isGm()) {
		            if (pc.getSealingPW() != null) {
		                pc.sendPackets(new S_SystemMessage("봉인암호를 해제 해주십시오."));
		                pc.sendPackets(new S_SystemMessage("사용 예) .봉인해제 원하는암호"));
		                return;
		                }
		                }
				        l1iteminstance1.setLockitem(0);
				        client.getActiveChar().getInventory().updateItem(l1iteminstance1,L1PcInventory.COL_LOCKITEM);
				        pc.sendPackets(new S_ItemColor(l1iteminstance1));
				        CharactersItemStorage storage = CharactersItemStorage.create();
				        storage.updateItemLockitem(l1iteminstance1);
				        pc.getInventory().removeItem(l1iteminstance, 1);
			  } else {
		                pc.sendPackets(new S_SystemMessage("이미 봉인 해제 되어 있는 아이템입니다."));
		                }
			  } else    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
			  } else if (itemId == 500041) { // 천상의 물약
					 if (pc.hasSkillEffect(7013) == false) {
					     UseExpPotion(pc, itemId);				     
					     pc.getInventory().removeItem(l1iteminstance, 1);
			  } else {
					     pc.sendPackets(new S_SystemMessage("아직 상승 효과가 사라지지 않았습니다."));
					     return;	     
			             }
					     ///////////////경험치 물약 ///////
			  } else if (itemId == 54012) {  // 경험치물약
                         pc.setExp(pc.getExp() + 90000);
					     pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 경험치가 향상되었습니다. 축하드립니다. "));
					     pc.getInventory().removeItem(l1iteminstance, 1);
					     ///////////경험치물약 /////////////
			  } else if (itemId == 350004) {
			      if (l1iteminstance1 != null && l1iteminstance1.getItem().getType2() == 2 
			    &&(l1iteminstance1.getItem().getType() == 8 || l1iteminstance1.getItem().getType() == 9 
			    || l1iteminstance1.getItem().getType() == 10 || l1iteminstance1.getItem().getType() == 11 
			    || l1iteminstance1.getItem().getType() == 12)) {
			       int rnd = _random.nextInt(100) + 1;
			       int upacse_chance;
			       if (l1iteminstance1.getUpacse() <= 9) {
			        upacse_chance = Config.UPACSE_CHANCE;
			       } else {
			        upacse_chance = Config.UPACSE_CHANCE;
			       }
			       if (l1iteminstance1.getUpacse() >= 10) { 
			        pc.sendPackets(new S_ServerMessage(79));
			        return; 
			       }
			       if (l1iteminstance1.getUpacse() <= 6) {
			       if (rnd < upacse_chance) {
			        pc.getInventory().setEquipped(l1iteminstance1, false);
			        l1iteminstance1.setUpacse(l1iteminstance1.getUpacse() + 1);
			        CharactersItemStorage storage = CharactersItemStorage.create();
			           storage.updateItemUpacse(l1iteminstance1);
			        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_UPACSE);
			        pc.getInventory().removeItem(l1iteminstance, 1);
			        pc.sendPackets(new S_SystemMessage("장신구 업그레이드에 성공했습니다."));
			       } else {
			        pc.getInventory().removeItem(l1iteminstance, 1);
			        //pc.getInventory().removeItem(l1iteminstance1, 1);
			        l1iteminstance1.setUpacse(0);
			        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_UPACSE);
			        pc.sendPackets(new S_SystemMessage("장신구 업그레이드에 실패했습니다."));
			       }
			      } else if (l1iteminstance1.getUpacse() <= 7){
			       if (rnd < (upacse_chance / 4)) {
			        pc.getInventory().setEquipped(l1iteminstance1, false);
			        l1iteminstance1.setUpacse(l1iteminstance1.getUpacse() + 1);
			        CharactersItemStorage storage = CharactersItemStorage.create();
			           storage.updateItemUpacse(l1iteminstance1);
			        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_UPACSE);
			        pc.getInventory().removeItem(l1iteminstance, 1);
			        pc.sendPackets(new S_SystemMessage("장신구 업그레이드에 성공했습니다."));
			       } else {
			        pc.getInventory().removeItem(l1iteminstance, 1);
			        //pc.getInventory().removeItem(l1iteminstance1, 1);
			        l1iteminstance1.setUpacse(0);
			        pc.getInventory().updateItem(l1iteminstance1, L1PcInventory.COL_UPACSE);
			        pc.sendPackets(new S_SystemMessage("장신구 업그레이드에 실패했습니다."));
			       }
			      }
			      } else {
			       pc.sendPackets(new S_ServerMessage(79));
			      }
		
				} else if (itemId == 43000) { // 환생의 물약(Lv99 캐릭터만이 사용 가능/Lv1에 돌아오는 효과)
					pc.setExp(1);
					pc.resetLevel();
					pc.setBonusStats(0);
					pc.sendPackets(new S_SkillSound(pcObjid, 191));
					pc.broadcastPacket(new S_SkillSound(pcObjid, 191));
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_ServerMessage(822)); // 독자 아이템이므로, 메세지는 적당합니다.
					pc.save(); // DB에 캐릭터 정보를 기입한다
				} else if (itemId == 40033) { // 엘릭서:완력
					if (pc.getBaseStr() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseStr((byte) 1); // 소의 STR치에+1
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 40034) { // 엘릭서:체력
					if (pc.getBaseCon() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseCon((byte) 1); // 소의 CON치에+1
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 40035) { // 엘릭서:기민
					if (pc.getBaseDex() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseDex((byte) 1); // 소의 DEX치에+1
						pc.resetBaseAc();
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 40036) { // 엘릭서:지력
					if (pc.getBaseInt() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseInt((byte) 1); // 소의 INT치에+1
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 40037) { // 엘릭서:정신
					if (pc.getBaseWis() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseWis((byte) 1); // 소의 WIS치에+1
						pc.resetBaseMr();
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 40038) { // 엘릭서:매력
					if (pc.getBaseCha() < 35 && pc.getElixirStats() < 5) {
						pc.addBaseCha((byte) 1); // 소의 CHA치에+1
						pc.setElixirStats(pc.getElixirStats() + 1);
						pc.getInventory().removeItem(l1iteminstance, 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.save();
						; // DB에 캐릭터 정보를 기입한다
					} else {
						// pc.sendPackets(new S_ServerMessage(481)); // \f1 하나의 능력치의 최대치는 25입니다.다른 능력치를 선택해 주세요.
						pc.sendPackets(new S_SystemMessage("엘릭서는 최대 5개까지만 적용됩니다."));
					}
				} else if (itemId == 500101) { /// 혈맹가입 주문서  (임시혈맹 가입)
				     if (pc.getClanid() == 0 ) {
				         L1Clan clan = L1World.getInstance().getClan("상점");
				         pc.setClanid(561644842);
				         pc.setClanname("상점");
				         pc.save(); // DB에 캐릭터 정보를 기입한다
				         clan.addMemberName(pc.getName());
				         pc.sendPackets(new S_ServerMessage( 95,
				           "상점")); 
				         pc.getInventory().removeItem(l1iteminstance , 1); 
				         } else {
				          pc.sendPackets(new S_SystemMessage("당신은 이미 혈맹에 가입하였습니다."));
				         }
		    	} else if (itemId == 50078) { //드래곤의다이아몬드 번호
			     if (pc.getAinPoint() < 100){
			      pc.setAinPoint(pc.getAinPoint() + 100);
			    pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
			    pc.getInventory().removeItem(l1iteminstance, 1);
			     }else{
			      pc.sendPackets(new S_SystemMessage("축복지수 100미만에서만 사용하실수 있습니다."));
			     } 
			    } else if (itemId == 50077) { //드래곤의사파이어 번호
			     if (pc.getAinPoint() < 150){
			      pc.setAinPoint(pc.getAinPoint() + 50);
			    pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
			    pc.getInventory().removeItem(l1iteminstance, 1);
			     }else{
			      pc.sendPackets(new S_SystemMessage("축복지수 150미만에서만 사용하실수 있습니다."));
			     } 
			    } else if (itemId == 50076) { //드래곤의루비 번호
			     if (pc.getAinPoint() < 170){
			      pc.setAinPoint(pc.getAinPoint() + 30);
			    pc.sendPackets(new S_SkillIconExp(pc.getAinPoint()));
			    pc.getInventory().removeItem(l1iteminstance, 1);
			     }else{
			      pc.sendPackets(new S_SystemMessage("축복지수 170미만에서만 사용하실수 있습니다."));
			     }
			    }
        		// 레드 일부, 농축 체력 회복제, 상아의 탑의 체력 회복제
			     else if (itemId == L1ItemId.POTION_OF_HEALING
						|| itemId == L1ItemId.CONDENSED_POTION_OF_HEALING
						|| itemId == 40029
					    || itemId == 41141) {
					UseHeallingPotion(pc, 15, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40022) { // 고대의 체력 회복제
					UseHeallingPotion(pc, 20, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.POTION_OF_EXTRA_HEALING
						|| itemId == L1ItemId.CONDENSED_POTION_OF_EXTRA_HEALING) {
					UseHeallingPotion(pc, 45, 194);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40023) { // 고대의 고급 체력 회복제
					UseHeallingPotion(pc, 30, 194);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.POTION_OF_GREATER_HEALING
						|| itemId == L1ItemId.CONDENSED_POTION_OF_GREATER_HEALING) {
					UseHeallingPotion(pc, 75, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40024 || itemId == 500083 
						|| itemId == 500085 || itemId == 41158) { // 고대의 강력 체력 회복제,야히의 징표,스피리드의 징표
					UseHeallingPotion(pc, 55, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40506) { // 엔트의 열매
					UseHeallingPotion(pc, 70, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
                                } else if (itemId == 555104) { // 따죠물약
					UseHeallingPotion(pc, 70, 4661);
					pc.getInventory().removeItem(l1iteminstance, 0);
				} else if (itemId == 40026 || itemId == 40027
						|| itemId == 40028) { // 쥬스
					UseHeallingPotion(pc, 25, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40058) { // 여우색의 빵
					UseHeallingPotion(pc, 30, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40071) { // 흑 타고의 빵
					UseHeallingPotion(pc, 70, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40734) { // 신뢰의 코인
					UseHeallingPotion(pc, 50, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.B_POTION_OF_HEALING) {
					UseHeallingPotion(pc, 25, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.C_POTION_OF_HEALING) {
					UseHeallingPotion(pc, 10, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.B_POTION_OF_EXTRA_HEALING) { // 축복된 오렌지
					// 일부
					UseHeallingPotion(pc, 55, 194);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.B_POTION_OF_GREATER_HEALING) { // 축복된 클리어
					// 일부
					UseHeallingPotion(pc, 85, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 140506) { // 축복된 엔트의 열매
					UseHeallingPotion(pc, 80, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40043 || itemId == 350014) { // 토끼의 간
					UseHeallingPotion(pc, 600, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 54013) { // 괴물 과즙 약
					UseHeallingPotion(pc, 100, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41403) { // 쿠쟈크의 식량
					UseHeallingPotion(pc, 300, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId >= 41417 && itemId <= 41421) { // 「에덴의 여름」이벤트 한정 아이템
					UseHeallingPotion(pc, 90, 197);
					pc.getInventory().removeItem(l1iteminstance, 1); 
					} else if (itemId == 41337) { //  축복된 보리 빵
						UseHeallingPotion(pc, 85, 197); 
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40858) { // liquor(술)
					pc.setDrink(true);
					pc.sendPackets(new S_Liquor(pc.getId()));
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == L1ItemId.POTION_OF_CURE_POISON
						|| itemId == 40507) { // 시안 일부, 엔트의 가지
					if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
						pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가 없습니다.
					} else {
						cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
						pc.sendPackets(new S_SkillSound(pc.getId(), 192));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 192));
						if (itemId == L1ItemId.POTION_OF_CURE_POISON) {
							pc.getInventory().removeItem(l1iteminstance, 1);
						} else if (itemId == 40507) {
							pc.getInventory().removeItem(l1iteminstance, 1);
						}

						pc.curePoison();
					}
				} else if (itemId == L1ItemId.POTION_OF_HASTE_SELF
						|| itemId == L1ItemId.B_POTION_OF_HASTE_SELF
						|| itemId == 40018 // 강화 그린 일부
						|| itemId == 500084 //악마왕의 징표
						|| itemId == 140018 // 축복된 강화 그린 일부
						|| itemId == 40039 // 와인
						|| itemId == 40040 // 위스키
						|| itemId == 40030 // 상아의 탑의 헤이 파업 일부
						|| itemId == 350015 // 퀵포션의 헤이 파업 일부
						|| itemId == 41338 // 축복된 와인
						|| itemId == 41261 // 주먹밥
						|| itemId == 41262 // 닭꼬치
						|| itemId == 41268 // 피자의 부분
						|| itemId == 41269 // 구이 수수
						|| itemId == 41271 // 팝콘
						|| itemId == 41272 // 오뎅
						|| itemId == 41273 // 와풀
						|| itemId == 41342) { // 메듀사의 피
					useGreenPotion(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
                                } else if (itemId == 555105) { // 촐기부적
	                                    useGreenPotion(pc, itemId);
	                                    pc.getInventory().removeItem(l1iteminstance, 0);

				} else if (itemId == L1ItemId.POTION_OF_EMOTION_BRAVERY // 치우침 이브 일부
						|| itemId == L1ItemId.B_POTION_OF_EMOTION_BRAVERY // 축복된 치우침 이브 일부
						|| itemId == 41415) { // 강화 치우침 이브 일부
					if (pc.isKnight()) {
						useBravePotion(pc, itemId);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40068 // 에르브왓훌
						|| itemId == 140068) { // 축복된 에르브왓훌
					if (pc.isElf()) {
						useBravePotion(pc, itemId);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40031) { // 이비르브랏드
					if (pc.isCrown()) {
						useBravePotion(pc, itemId);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
			    } else if (itemId == 500041) { // 천상의 물약
			        if (pc.hasSkillEffect(7013) == false) {
			          UseExpPotion(pc, itemId);         
			          pc.getInventory().removeItem(l1iteminstance, 1);
			         } else 
			          pc.sendPackets(new S_SystemMessage("아직 보너스 효과가 사라지지 않았습니다."));
		       	} else if (itemId == 40733) { // 명예의 코인
					useBravePotion(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
                         } else if (itemId == 555106) { // 용기의 결정					       
                                         useBravePotion(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 0);

				} else if (itemId == 500024 ) { // 용기사,환술사 유그드라
				     if (pc.isDragonKnight()|| pc.isBlackWizard()) { 
				    //	    pc.sendPackets(new S_SkillSound(pc.getId(), 7110)); //추가
                    //      pc.broadcastPacket(new S_SkillSound(pc.getId(), 7110)); //추가   
				    	 	useBravePotion(pc, itemId);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						}
						pc.getInventory().removeItem(l1iteminstance, 1);
			/*	 } else if (itemId == 43001) { // 안티매직포션
					 pc.setSkillEffect(L1SkillId.AntiMagic, 60000);
					 pc.sendPackets(new S_SystemMessage("마법의 힘으로 60초동안 저주계열 마법을 막아줍니다!"));
					 pc.sendPackets(new S_SkillSound(pc.getId(), 6320)); 
					 pc.broadcastPacket(new S_SkillSound(pc.getId(), 6320));
					 pc.getInventory().removeItem(l1iteminstance, 1);*/
				 } else if(itemId == 240097){// 올버프 물약
						int[] allBuffSkill = { LIGHT, DECREASE_WEIGHT, PHYSICAL_ENCHANT_DEX,
							                   MEDITATION, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
							                   BERSERKERS, IMMUNE_TO_HARM, ADVANCE_SPIRIT,
							                   REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
							                   ENCHANT_VENOM, BURNING_SPIRIT, VENOM_RESIST,
							                   GLOWING_AURA, BRAVE_AURA, DOUBLE_BRAKE, UNCANNY_DODGE,
							                   RESIST_MAGIC, CLEAR_MIND, WATER_LIFE, ELEMENTAL_FIRE,
							                   BURNING_WEAPON, IRON_SKIN, SOUL_OF_FLAME, ADDITIONAL_FIRE,  
							                   DRAGON_SKIN, MOTALBODY, MIRRORIMG, PAYTIONS, INSIGHT, CONSENTRATION };
							           pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
							           L1SkillUse l1skilluse = new L1SkillUse();
							           for (int i = 0; i < allBuffSkill.length ; i++) {
							           l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
							           }
							           pc.getInventory().removeItem(l1iteminstance, 1); 
					                   pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동
							
				     } else if (itemId == 500211) { //티칼 달력 추가부문
					    if(!CrockController.getInstance().TicalC())
					     pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendarc"));
					     else  {    
					      pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tcalendaro"));
					    }		
					    
				     } else if (itemId == 40066 || itemId == 41413) { // 송편, 월병
				      UseManaPotion(pc, 12, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 40067 || itemId == 41414 || itemId == 350013) { // 쑥송편, 복월병
				      UseManaPotion(pc, 35, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 40735) { // 용기의 코인
				      UseManaPotion(pc, 60, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 40042 || itemId == 500086 || itemId == 41142) { // 정신력의 물약,그림리퍼의 징표
				      UseManaPotion(pc, 50, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 41404) { // 쿠작의 영약
				      UseManaPotion(pc, 100, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 41412) { // 금쫑즈
				      UseManaPotion(pc, 15, itemId);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 40032 || itemId == 40041
						|| itemId == 41344) { // 에바의 축복, mermaid의 비늘, 물의 정수
					       useBlessOfEva(pc, itemId);
					  pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == L1ItemId.POTION_OF_MANA // 블루 일부
						|| itemId == L1ItemId.B_POTION_OF_MANA // 축복된 블루
						|| itemId == 40736) { // 지혜의 코인
					  useBluePotion(pc, itemId);
					  pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == L1ItemId.POTION_OF_EMOTION_WISDOM // 위즈 댐
						|| itemId == L1ItemId.B_POTION_OF_EMOTION_WISDOM) { // 축복된 위즈 댐
					        if (pc.isWizard()) {
					  useWisdomPotion(pc, itemId);
					 } else {
					  pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					  }
					  pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == L1ItemId.POTION_OF_BLINDNESS) { // 오페이크포션
					  useBlindPotion(pc);
					  pc.getInventory().removeItem(l1iteminstance, 1);
				     } else if (itemId == 40088 // 변신 스크롤
						|| itemId == 40096 // 상아의 탑의 변신 스크롤
						|| itemId == 140088) { // 축복된 변신 스크롤
					        if (usePolyScroll(pc, itemId, s)) {
					  pc.getInventory().removeItem(l1iteminstance, 1);
					 } else {
					  pc.sendPackets(new S_ServerMessage(181)); // \f1 그러한 monster에게는 변신할 수 없습니다.
					  }
				} else if (itemId == 41154 // 어둠의 비늘
						|| itemId == 41155 // 열화의 비늘
						|| itemId == 41156 // 배덕자의 비늘
						|| itemId == 41157) { // 증오의 비늘
					usePolyScale(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41143 // 러버 얼간이 변신 일부
						|| itemId == 41144 // 라바본아챠 변신 일부
						|| itemId == 41145) { // 러버 뼈 나이프 변신 일부
					usePolyPotion(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 500044 
						|| itemId == 500045 || itemId == 500047
						|| itemId == 500048 || itemId == 500049
						|| itemId == 500050 || itemId == 500051) {  //샤르나의 변신 주문서 
					useLevelPolyScroll(pc, itemId);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40317) { // 숫돌
					// 무기나 방어용 기구의 경우만
					if (l1iteminstance1.getItem().getType2() != 0
							&& l1iteminstance1.get_durability() > 0) {
						String msg0;
						pc.getInventory().recoveryDamage(l1iteminstance1);
						msg0 = l1iteminstance1.getLogName();
						if (l1iteminstance1.get_durability() == 0) {
							pc.sendPackets(new S_ServerMessage(464, msg0)); // %0%s는 신품 같은 상태가 되었습니다.
						} else {
							pc.sendPackets(new S_ServerMessage(463, msg0)); // %0 상태가 좋아졌습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40097 || itemId == 40119
						|| itemId == 140119 || itemId == 140329) { // 해주스크롤, 원주민의 토템
					for (L1ItemInstance eachItem : pc.getInventory().getItems()) {
						if (eachItem.getItem().getBless() != 2) {
							continue;
						}
						if (!eachItem.isEquipped()
								&& (itemId == 40119 || itemId == 40097)) {
							// n해주는 장비 하고 있는 것 밖에 해주 하지 않는다
							continue;
						}
						int id_normal = eachItem.getItemId() - 200000;
						L1Item template = ItemTable.getInstance().getTemplate(
								id_normal);
						if (template == null) {
							continue;
						}
						if (pc.getInventory().checkItem(id_normal)
								&& template.isStackable()) {
							pc.getInventory().storeItem(id_normal,
									eachItem.getCount());
							pc.getInventory().removeItem(eachItem,
									eachItem.getCount());
						} else {
							eachItem.setItem(template);
							pc.getInventory().updateItem(eachItem,
									L1PcInventory.COL_ITEMID);
							pc.getInventory().saveItem(eachItem,
									L1PcInventory.COL_ITEMID);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					pc.sendPackets(new S_ServerMessage(155)); // \f1누군가가 도와 준 것 같습니다.
				} else if (itemId == 40126 || itemId == 40098) { // 확인 스크롤
					if (l1iteminstance1.getLockitem() > 100){
                     return;
                 } else {
					if (!l1iteminstance1.isIdentified()) {
						l1iteminstance1.setIdentified(true);
						pc.getInventory().updateItem(l1iteminstance1,
								L1PcInventory.COL_IS_ID);
					}
					pc.sendPackets(new S_IdentifyDesc(l1iteminstance1));
					pc.getInventory().removeItem(l1iteminstance, 1);
				   }
				} else if (itemId == 41036) { // 풀
					int diaryId = l1iteminstance1.getItem().getItemId();
					if (diaryId >= 41038 && 41047 >= diaryId) {
						if ((_random.nextInt(99)+1) <= Config.CREATE_CHANCE_DIARY) {
							createNewItem(pc, diaryId + 10, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이 증발하고 있지 않게 되었습니다.
						}
						pc.getInventory(). removeItem(l1iteminstance1, 1);
						pc.getInventory(). removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId >= 41048 && 41055 >= itemId ) {
					// 풀먹임 된 항해 일지 페이지：1~8 페이지
					int logbookId = l1iteminstance1.getItem(). getItemId();
					if (logbookId == (itemId + 8034)) {
							createNewItem(pc, logbookId + 2, 1);
							pc.getInventory(). removeItem(l1iteminstance1, 1);
							pc.getInventory(). removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 41056 || itemId == 41057) {
					// 풀먹임 된 항해 일지 페이지：9, 10 페이지
					int logbookId = l1iteminstance1.getItem(). getItemId();
					if (logbookId == (itemId + 8034)) {
						createNewItem(pc, 41058, 1);
						pc.getInventory(). removeItem(l1iteminstance1, 1);
						pc.getInventory(). removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40925) { // 정화의 일부
					int earingId = l1iteminstance1.getItem(). getItemId();
					if (earingId >= 40987 && 40989 >= earingId) { // 저주해진 블랙 귀 링
						if (_random.nextInt(100) < Config.CREATE_CHANCE_RECOLLECTION) {
							createNewItem(pc, earingId + 186, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이 증발하고 있지 않게 되었습니다.
						}
						pc.getInventory(). removeItem(l1iteminstance1, 1);
						pc.getInventory(). removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId >= 40926 && 40929 >= itemId) {
					//　신비적인 일부(1~4 단계)
					int earing2Id = l1iteminstance1.getItem(). getItemId();
					int potion1 = 0;
					int potion2 = 0;
					if (earing2Id >= 41173 && 41184 >=earing2Id) {
						// 귀 링류
						if (itemId == 40926){
							potion1 = 247;
							potion2 = 249;
						} else if (itemId == 40927) {
							potion1 = 249;
							potion2 = 251;
						} else if (itemId == 40928) {
							potion1 = 251;
							potion2 = 253;
						} else if (itemId == 40929) {
							potion1 = 253;
							potion2 = 255;							
						}
						if (earing2Id >= (itemId + potion1) 
								&& (itemId + potion2) >= earing2Id ) {
							if ((_random.nextInt(99)+1) < 
									Config.CREATE_CHANCE_MYSTERIOUS) {
								createNewItem(pc, (earing2Id - 12), 1);
								pc.getInventory(). removeItem(l1iteminstance1, 1);
								pc.getInventory(). removeItem(l1iteminstance, 1);	
							} else {
								pc.sendPackets(new S_ServerMessage(160,
										l1iteminstance1.getName()));
								// \f1%0이%2 강렬하게%1 빛났습니다만, 다행히 무사하게 살았습니다.
								pc.getInventory(). removeItem(l1iteminstance, 1);
							}
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId >= 40931 && 40942 >= itemId) {
					//　가공된 보석류(사파이어·루비·에메랄드)
					int earing3Id = l1iteminstance1.getItem(). getItemId();
                 int earinglevel = 0;
					if (earing3Id >= 41161 && 41172 >= earing3Id) {
						// 신비적인 귀 링류
						if (earing3Id == (itemId + 230)) {
							if ((_random.nextInt(99)+1) < 
									Config.CREATE_CHANCE_PROCESSING) {
								if (earing3Id == 41161) {
									earinglevel = 21014;
								} else if (earing3Id == 41162) {
									earinglevel = 21006;
								} else if (earing3Id == 41163) {
									earinglevel = 21007;
								} else if (earing3Id == 41164) {
									earinglevel = 21015;
								} else if (earing3Id == 41165) {
									earinglevel = 21009;
								} else if (earing3Id == 41166) {
									earinglevel = 21008;
								} else if (earing3Id == 41167) {
									earinglevel = 21016;
								} else if (earing3Id == 41168) {
									earinglevel = 21012;
								} else if (earing3Id == 41169) {
									earinglevel = 21010;
								} else if (earing3Id == 41170) {
									earinglevel = 21017;
								} else if (earing3Id == 41171) {
									earinglevel = 21013;
								} else if (earing3Id == 41172) {
									earinglevel = 21011;
								}
								createNewItem(pc, earinglevel, 1);
							} else {
								pc.sendPackets(new S_ServerMessage(158,
										l1iteminstance1.getName())); 
								// \f1%0이 증발하고 있지 않게 되었습니다.
							}
							pc.getInventory(). removeItem(l1iteminstance1, 1);
							pc.getInventory(). removeItem(l1iteminstance, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId >= 40943 && 40958 >= itemId) {
					//　가공된 다이아몬드(워타·지구·파이어·윈드)
					int ringId = l1iteminstance1.getItem(). getItemId();
                 int ringlevel = 0;
                 int gmas = 0;
                 int gmam = 0;
					if (ringId >= 41185 && 41200 >= ringId) {
						// 세공된 링류
						if (itemId == 40943 || itemId == 40947
								|| itemId == 40951 || itemId == 40955) {
							gmas = 443;
							gmam = 447;
						} else if (itemId == 40944 || itemId == 40948
								|| itemId == 40952 || itemId == 40956) {
							gmas = 442;
							gmam = 446;
						} else if (itemId == 40945 || itemId == 40949
								|| itemId == 40953 || itemId == 40957) {
							gmas = 441;
							gmam = 445;
						} else if (itemId == 40946 || itemId == 40950
								|| itemId == 40954 || itemId == 40958) {
							gmas = 444;
							gmam = 448;
						}
						if (ringId == (itemId + 242)) {
							if ((_random.nextInt(99)+1) < 
									Config.CREATE_CHANCE_PROCESSING_DIAMOND) {
								if (ringId == 41185) {
									ringlevel = 20435;
								} else if (ringId == 41186) {
									ringlevel = 20436;
								} else if (ringId == 41187) {
									ringlevel = 20437;
								} else if (ringId == 41188) {
									ringlevel = 20438;
								} else if (ringId == 41189) {
									ringlevel = 20439;
								} else if (ringId == 41190) {
									ringlevel = 20440;
								} else if (ringId == 41191) {
									ringlevel = 20441;
								} else if (ringId == 41192) {
									ringlevel = 20442;
								} else if (ringId == 41193) {
									ringlevel = 20443;
								} else if (ringId == 41194) {
									ringlevel = 20444;
								} else if (ringId == 41195) {
									ringlevel = 20445;
								} else if (ringId == 41196) {
									ringlevel = 20446;
								} else if (ringId == 41197) {
									ringlevel = 20447;
								} else if (ringId == 41198) {
									ringlevel = 20448;
								} else if (ringId == 41199) {
									ringlevel = 20449;
								} else if (ringId == 411200) {
									ringlevel = 20450;
								}
								pc.sendPackets(new S_ServerMessage(gmas,
										l1iteminstance1.getName()));
								createNewItem(pc, ringlevel, 1);
								pc.getInventory(). removeItem(l1iteminstance1, 1);
								pc.getInventory(). removeItem(l1iteminstance, 1);
							} else {
								pc.sendPackets(new S_ServerMessage(gmam,
										l1iteminstance.getName()));
								pc.getInventory(). removeItem(l1iteminstance, 1);
							    }
						    } else {
							    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						        }
					        } else {
						        pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					            }
				} else if (itemId == 41029) { // 소환공의 조각
					int dantesId = l1iteminstance1.getItem(). getItemId();
					if (dantesId >= 41030 && 41034 >= dantesId) { // 소환공의 코어· 각 단계
						if ((_random.nextInt(99)+1) <
								Config.CREATE_CHANCE_DANTES) {
							createNewItem(pc, dantesId + 1, 1);
						} else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이 증발하고 있지 않게 되었습니다.
						}
						pc.getInventory(). removeItem(l1iteminstance1, 1);
						pc.getInventory(). removeItem(l1iteminstance, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40964) { // 흑마법 가루
					int historybookId = l1iteminstance1.getItem(). getItemId();
					if (historybookId >= 41011 && 41018 >= historybookId) {
						if ((_random.nextInt(99)+1) <= Config.CREATE_CHANCE_HISTORY_BOOK) {
							createNewItem(pc, historybookId + 8, 1);
				    } else {
							pc.sendPackets(new S_ServerMessage(158,
									l1iteminstance1.getName())); // \f1%0이 증발하고 있지 않게 되었습니다.
						}
						pc.getInventory(). removeItem(l1iteminstance1, 1);
						pc.getInventory(). removeItem(l1iteminstance, 1);
		          } else {
		                    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		                   } 	
				} else if (itemId == 500035) { // 하급 오시리스의 보물상자 조각(하)
				    if (l1iteminstance1 != null
				         && l1iteminstance1.getItem().getItemId() == 500034) { // 하급 오시리스의 보물상자 조각(상)
				        L1ItemInstance item = pc.getInventory().storeItem(500038, 1); //잠긴 하급 오시리스의 보물상자
				        if (item != null) {
				         pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				         pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				         pc.sendPackets(new S_SystemMessage("잠긴 하급 오시리스의 보물상자를 얻었습니다."));
				        }
				    } else {
				         pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				        }
				} else if (itemId == 500037) { // 상급 오시리스의 보물상자 조각(하)
				    if (l1iteminstance1 != null
				          && l1iteminstance1.getItem().getItemId() == 500036) { // 상급 오시리스의 보물상자 조각(상)
				         L1ItemInstance item = pc.getInventory().storeItem(500039, 1); //잠긴 상급 오시리스의 보물상자
				         if (item != null) {
				          pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				          pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				          pc.sendPackets(new S_SystemMessage("잠긴 상급 오시리스의 보물상자를 얻었습니다."));
				         }  
				    } else {
				         pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				        } 
				} else if (itemId == 500061) { // 쿠쿨칸의 상급  보물상자 조각(상)
				    if (l1iteminstance1 != null
				         && l1iteminstance1.getItem().getItemId() == 500062) { // 쿠쿨칸 상급의 보물상자 조각(하)
				        L1ItemInstance item = pc.getInventory().storeItem(500065, 1); //잠긴 쿠쿨칸의 보물상자 상급
				        if (item != null) {
				         pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				         pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				         pc.sendPackets(new S_SystemMessage("잠긴 상급 쿠쿨칸의 보물상자를 얻었습니다."));
				        }
				    } else {
				         pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				        }  
				} else if (itemId == 500063) { // 하급 쿠쿨칸의 보물상자 조각(상)
				    if (l1iteminstance1 != null
				          && l1iteminstance1.getItem().getItemId() == 500064) { // 하급 쿠쿨칸의 보물상자 조각(하)
				         L1ItemInstance item = pc.getInventory().storeItem(500066, 1); //잠긴 하급쿠쿨칸의 보물상자
				         if (item != null) {
				          pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				          pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				          pc.sendPackets(new S_SystemMessage("잠긴 하급 쿠쿨칸의 보물상자를 얻었습니다."));
				         }  
				    } else {
				         pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				        } 
				} else if (itemId == 500031) { // 균열의 핵
		            if (l1iteminstance1 != null
		              && l1iteminstance1.getItem().getItemId() == 500038) { // 잠긴 하급 오시리스의 보물상자
		             L1ItemInstance item = pc.getInventory().storeItem(500032, 1); //열린 하급 오시리스의 보물상자
		             if (item != null) {
		              pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
		              pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
		              pc.sendPackets(new S_SystemMessage("열린 하급 오시리스의 보물상자를 얻었습니다."));
		               }
		            } 
		            else if (l1iteminstance1 != null
				              && l1iteminstance1.getItem().getItemId() == 500039) { // 잠긴 상급 오시리스의 보물상자
				             L1ItemInstance item = pc.getInventory().storeItem(500033, 1); //열린 상급 오시리스의 보물상자
				             if (item != null) {
				              pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				              pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				              pc.sendPackets(new S_SystemMessage("열린 상급 오시리스의 보물상자를 얻었습니다."));
				             }
                     }
		            else if (l1iteminstance1 != null
				              && l1iteminstance1.getItem().getItemId() == 500065) { // 잠긴 상급 쿠쿨칸의 보물상자
				             L1ItemInstance item = pc.getInventory().storeItem(500067, 1); //열린 상급 쿠쿨칸의 보물상자
				             if (item != null) {
				              pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
				              pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
				              pc.sendPackets(new S_SystemMessage("열린 상급 쿠쿨칸의 보물상자를 얻었습니다."));
				             }
		            }
		            else if (l1iteminstance1 != null
							   && l1iteminstance1.getItem().getItemId() == 500066) { //  잠긴 하급 쿠쿨칸의 보물상자
						   L1ItemInstance item = pc.getInventory().storeItem(500068, 1); //열린 하급 쿠쿨칸의 보물상자
					             if (item != null) {
					            	 pc.getInventory().consumeItem(l1iteminstance.getItem().getItemId(), 1);
					            	 pc.getInventory().consumeItem(l1iteminstance1.getItem().getItemId(), 1);
					            	 pc.sendPackets(new S_SystemMessage("열린 하급 쿠쿨칸의 보물상자를 얻었습니다."));
					             }
		            }
		            else {
		                    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		                   } 	
				} else if (itemId == 40090 || itemId == 40091
						|| itemId == 40092 || itemId == 40093
						|| itemId == 40094) { // 공백 스크롤(Lv1)~공백
					// 스크롤(Lv5)
					if (pc.isWizard()) { // 위저드
						if (itemId == 40090 && blanksc_skillid <= 7 || // 공백
								// 스크롤(Lv1)로 레벨 1 이하의 마법
								itemId == 40091 && blanksc_skillid <= 15 || // 공백
								// 스크롤(Lv2)로 레벨 2 이하의 마법
								itemId == 40092 && blanksc_skillid <= 22 || // 공백
								// 스크롤(Lv3)로 레벨 3 이하의 마법
								itemId == 40093 && blanksc_skillid <= 31 || // 공백
								// 스크롤(Lv4)로 레벨 4 이하의 마법
								itemId == 40094 && blanksc_skillid <= 39) { // 공백
							// 스크롤(Lv5)로 레벨 5 이하의 마법
							L1ItemInstance spellsc = ItemTable.getInstance()
									.createItem(40859 + blanksc_skillid);
							if (spellsc != null) {
								if (pc.getInventory().checkAddItem(spellsc, 1) == L1Inventory.OK) {
									L1Skills l1skills = SkillsTable
											.getInstance().getTemplate(
													blanksc_skillid + 1); // blanksc_skillid는 0 시작
									if (pc.getCurrentHp() + 1 < l1skills
											.getHpConsume() + 1) {
										pc
												.sendPackets(new S_ServerMessage(
														279)); // \f1HP가 부족해 마법을 사용할 수 있지 않습니다.
										return;
									}
									if (pc.getCurrentMp() < l1skills
											.getMpConsume()) {
										pc
												.sendPackets(new S_ServerMessage(
														278)); // \f1MP가 부족해 마법을 사용할 수 있지 않습니다.
										return;
									}
									if (l1skills.getItemConsumeId() != 0) { // 재료가 필요
										if (!pc.getInventory().checkItem(
												l1skills.getItemConsumeId(),
												l1skills.getItemConsumeCount())) { // 필요 재료를 체크
											pc.sendPackets(new S_ServerMessage(
													299)); // \f1마법을 영창하기 위한 재료가 충분하지 않습니다.
											return;
										}
									}
									pc.setCurrentHp(pc.getCurrentHp()
											- l1skills.getHpConsume());
									pc.setCurrentMp(pc.getCurrentMp()
											- l1skills.getMpConsume());
									int lawful = pc.getLawful()
											+ l1skills.getLawful();
									if (lawful > 32767) {
										lawful = 32767;
									}
									if (lawful < -32767) {
										lawful = -32767;
									}
									pc.setLawful(lawful);
									if (l1skills.getItemConsumeId() != 0) { // 재료가 필요
										pc.getInventory().consumeItem(
												l1skills.getItemConsumeId(),
												l1skills.getItemConsumeCount());
									}
									pc.getInventory().removeItem(l1iteminstance, 1);
									pc.getInventory().storeItem(spellsc);
								    }
							      }
						     } else {
							        pc.sendPackets(new S_ServerMessage(591)); // \f1스크롤이 그렇게 강한 마법을 기록하려면  너무나 약합니다.
						            }
					         } else {
						            pc.sendPackets(new S_ServerMessage(264)); // \f1당신의 클래스에서는 이 아이템은 사용할 수 없습니다.
					                }

				// 스펠 스크롤
				} else if ((itemId >= 40859 && itemId <= 40898)
						&& itemId != 40863) { // 40863은 텔레포트 스크롤로서 처리된다
					if (spellsc_objid == pc.getId()
							&& l1iteminstance.getItem().getUseType() != 30) { // spell_buff
						pc.sendPackets(new S_ServerMessage(281)); // \f1마법이 무효가 되었습니다.
						return;
					}
					pc.getInventory().removeItem(l1iteminstance, 1);
					if (spellsc_objid == 0
							&& l1iteminstance.getItem().getUseType() != 0
							&& l1iteminstance.getItem().getUseType() != 26
							&& l1iteminstance.getItem().getUseType() != 27) {
						return;
						// 타겟이 없는 경우에 handleCommands송가 되기 (위해)때문에 여기서 return
						// handleCommands 쪽으로 판단＆처리해야 할 부분일지도 모른다
					}
					cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
					int skillid = itemId - 40858;
					pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(client.getActiveChar(), skillid,
							spellsc_objid, spellsc_x, spellsc_y, null, 0,
							L1SkillUse.TYPE_SPELLSC);  

					 pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동

				} else if (itemId >= 40373 && itemId <= 40382 // 지도 각종
						|| itemId >= 40385 && itemId <= 40390) {
					pc.sendPackets(new S_UseMap(pc, l1iteminstance.getId(),
							l1iteminstance.getItem().getItemId()));
				} else if (itemId == 40310 || itemId == 40730
						|| itemId == 40731 || itemId == 40732) { // 편지지(미사용)
					if (writeLetter(itemId, pc, letterCode, letterReceiver,
							letterText)) {
						pc.getInventory().removeItem(l1iteminstance, 1);
					}
				} else if (itemId == 40311) { // 혈맹 편지지(미사용)
					if (writeClanLetter(itemId, pc, letterCode, letterReceiver,
							letterText)) {
						pc.getInventory().removeItem(l1iteminstance, 1);
					}
				} else if (itemId == 49016 || itemId == 49018
						|| itemId == 49020 || itemId == 49022
						|| itemId == 49024) { // 편지지(미개봉)
					pc.sendPackets(new S_Letter(l1iteminstance));
					l1iteminstance.setItemId(itemId + 1);
					pc.getInventory().updateItem(l1iteminstance,
							L1PcInventory.COL_ITEMID);
					pc.getInventory().saveItem(l1iteminstance,
							L1PcInventory.COL_ITEMID);
				} else if (itemId == 49017 || itemId == 49019
						|| itemId == 49021 || itemId == 49023
						|| itemId == 49025) { // 편지지(개봉이 끝난 상태)
					pc.sendPackets(new S_Letter(l1iteminstance));
				} else if (itemId == 40314 || itemId == 40316) { // 펫의 아뮤렛트
					if (pc.getInventory().checkItem(41160)) { // 소환의 피리
						if (withdrawPet(pc, itemObjid)) {
							pc.getInventory().consumeItem(41160, 1);
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40315) { // 펫의 피리
					pc.sendPackets(new S_Sound(437));
					pc.broadcastPacket(new S_Sound(437));
					Object[] petList = pc.getPetList().values().toArray();
					for (Object petObject : petList) {
						if (petObject instanceof L1PetInstance) { // 펫
							L1PetInstance pet = (L1PetInstance) petObject;
							pet.call();
						}
					}
				} else if (itemId == 40493) { // 매직 플룻
					pc.sendPackets(new S_Sound(165));
					pc.broadcastPacket(new S_Sound(165));
					for (L1Object visible : pc.getKnownObjects()) {
						if (visible instanceof L1GuardianInstance) {
							L1GuardianInstance guardian = (L1GuardianInstance) visible;
							if (guardian.getNpcTemplate().get_npcId() == 70850) { // 빵
								if (createNewItem(pc, 88, 1)) {
									pc.getInventory().removeItem(
											l1iteminstance, 1);
								}
							}
						}
					}
				} else if (itemId == 40325) { // 2면 아이 인
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3237 + _random.nextInt(2);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40326) { // 3 방향 룰렛
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3229 + _random.nextInt(3);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40327) { // 4 방향 룰렛
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3241 + _random.nextInt(4);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40328) { // 6단계 주사위
					if (pc.getInventory().checkItem(40318, 1)) {
						int gfxid = 3204 + _random.nextInt(6);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
						pc.getInventory().consumeItem(40318, 1);
					} else {
						pc.sendPackets(new S_ServerMessage(79));
					}	// 마력의돌 소비
					   	int gfxid = 3204 + _random.nextInt(6);
						pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
						// 마력의돌 소비 안함
				  } else if (itemId == 500013) { // 1단계 기억의 수정 
				     if (pc.getInventory().checkItem(500013, 1) ){ 
				       pc.getInventory().consumeItem(500013, 1);      
				       pc.getInventory().storeItem(210000, 1);
				       pc.getInventory().storeItem(210001, 1);      
				       pc.getInventory().storeItem(210002, 1);      
				       pc.getInventory().storeItem(210003, 1);      
				       pc.getInventory().storeItem(210004, 1);
				       pc.sendPackets(new S_SystemMessage("1단계 기억의 수정이 지급 되었습니다."));  // 메세지 출력
				     } 
				  } else if (itemId == 500014) { // 2단계 기억의 수정
				     if (pc.getInventory().checkItem(500014, 1) ){ 
				       pc.getInventory().consumeItem(500014, 1); 
				       pc.getInventory().storeItem(210005, 1);         
				       pc.getInventory().storeItem(210006, 1);                        
				       pc.getInventory().storeItem(210007, 1); 
				       pc.getInventory().storeItem(210008, 1);
				       pc.getInventory().storeItem(210009, 1);
				       pc.sendPackets(new S_SystemMessage("2단계 기억의 수정이 지급 되었습니다."));  // 메세지 출력
				     }
				  } else if (itemId == 500015) { // 3단계 기억의 수정
				     if (pc.getInventory().checkItem(500015, 1) ){ 
				       pc.getInventory().consumeItem(500015, 1); 
				       pc.getInventory().storeItem(210010, 1);
				       pc.getInventory().storeItem(210011, 1);      
				    // pc.getInventory().storeItem(210012, 1);      
				       pc.getInventory().storeItem(210013, 1);      
				       pc.getInventory().storeItem(210014, 1);
				       pc.sendPackets(new S_SystemMessage("3단계 기억의 수정이 지급 되었습니다."));  // 메세지 출력
				     }
				  } else if (itemId == 500016) { // 4단계 기억의 수정
				     if (pc.getInventory().checkItem(500016, 1) ){ 
				       pc.getInventory().consumeItem(500016, 1); 
				       pc.getInventory().storeItem(210015, 1);         
				       pc.getInventory().storeItem(210016, 1);                        
				       pc.getInventory().storeItem(210017, 1); 
				    // pc.getInventory().storeItem(210018, 1);
				       pc.getInventory().storeItem(210019, 1);
				       pc.sendPackets(new S_SystemMessage("4단계 기억의 수정이 지급 되었습니다."));  // 메세지 출력
				     }
				  } else if (itemId == 500017) { // 1단계 용기사의 서판 
				     if (pc.getInventory().checkItem(500017, 1) ){ 
				       pc.getInventory().consumeItem(500017, 1);      
				       pc.getInventory().storeItem(210020, 1);
				     //pc.getInventory().storeItem(210021, 1);      
				     //pc.getInventory().storeItem(210022, 1);      
				       pc.getInventory().storeItem(210023, 1);      
				       pc.getInventory().storeItem(210024, 1);
				       pc.sendPackets(new S_SystemMessage("1단계 용기사의 서판이 지급 되었습니다."));  // 메세지 출력
				     }
				  } else if (itemId == 500018) { // 2단계 용기사의 서판 
				     if (pc.getInventory().checkItem(500018, 1) ){ 
				       pc.getInventory().consumeItem(500018, 1);
				     //pc.getInventory().storeItem(210025, 1);         
				     //pc.getInventory().storeItem(210026, 1);                        
				     //pc.getInventory().storeItem(210027, 1); 
				       pc.getInventory().storeItem(210028, 1);
				       pc.getInventory().storeItem(210029, 1);
				      pc.sendPackets(new S_SystemMessage("2단계 용기사의 서판이 지급 되었습니다."));  // 메세지 출력
				     }
				  } else if (itemId == 500019) { // 3단계 용기사의 서판 
				     if (pc.getInventory().checkItem(500019, 1) ){ 
				       pc.getInventory().consumeItem(500019, 1);
				       pc.getInventory().storeItem(210030, 1);
				       pc.getInventory().storeItem(210031, 1);      
				       pc.getInventory().storeItem(210032, 1);      
				       pc.getInventory().storeItem(210033, 1);      
				       pc.getInventory().storeItem(210034, 1);          
				       pc.sendPackets(new S_SystemMessage("3단계 용기사의 서판이 지급 되었습니다."));  // 메세지 출력
				     }
          
   //########### 디스석  ############# 

    } else if (itemId == 555599){   // 업그레이드 주문서 번호
        if (pc.getInventory(). checkItem(555599, 50)){ //수량체크부분
      Random random = new Random();
     int k3 = random.nextInt(100);
     if (k3 <= 10) { //확률 부분

            pc.getInventory(). consumeItem(555599, 50); //아이템 삭제부분
            pc.getInventory().storeItem(555597, 1); //지급 부분
    Announcements.getInstance().announceToAll((pc.getName()+"님께서 디스석1개를 얻었습니다."));
  }
    if (k3 >= 10 && k3 <= 100) {  //확률 부분
            pc.getInventory(). consumeItem(555599, 50); //장비는 그대로 재료만 삭제 되게끔 하세요.
     pc.sendPackets(new S_SystemMessage(" 디스석을 0개 얻었습니다")); 
     pc.getInventory().removeItem(555599, 50);
     }
        } else {
         pc.sendPackets(new S_SystemMessage("디스석파편 50개가 필요합니다.")); //재료가 부족할시에 멘트
        }
//########### 디스석  ############# 

 //########### 미티어석  ############# 

    } else if (itemId == 555100){   // 업그레이드 주문서 번호
        if (pc.getInventory(). checkItem(555100, 50)){ //수량체크부분
      Random random = new Random();
     int k3 = random.nextInt(100);
     if (k3 <= 10) { //확률 부분

            pc.getInventory(). consumeItem(555100, 50); //아이템 삭제부분
            pc.getInventory().storeItem(555598, 1); //지급 부분
    Announcements.getInstance().announceToAll((pc.getName()+"님께서 미티어석1개를 얻었습니다."));
  }
    if (k3 >= 10 && k3 <= 100) {  //확률 부분
            pc.getInventory(). consumeItem(555100, 50); //장비는 그대로 재료만 삭제 되게끔 하세요.
     pc.sendPackets(new S_SystemMessage(" 미티어석을 0개 얻었습니다")); 
     pc.getInventory().removeItem(555100, 50);
     }
        } else {
         pc.sendPackets(new S_SystemMessage("미티어석파편 50개가 필요합니다.")); //재료가 부족할시에 멘트
        }
//########### 미티어석  ############# 

 //########### 데미지석  ############# 

    } else if (itemId == 555101){   // 업그레이드 주문서 번호
        if (pc.getInventory(). checkItem(555101, 50)){ //수량체크부분
      Random random = new Random();
     int k3 = random.nextInt(100);
     if (k3 <= 10) { //확률 부분

            pc.getInventory(). consumeItem(555101, 50); //아이템 삭제부분
            pc.getInventory().storeItem(555103, 1); //지급 부분
    Announcements.getInstance().announceToAll((pc.getName()+"님께서 데미지석1개를 얻었습니다."));
  }
    if (k3 >= 10 && k3 <= 100) {  //확률 부분
            pc.getInventory(). consumeItem(555101, 50); //장비는 그대로 재료만 삭제 되게끔 하세요.
     pc.sendPackets(new S_SystemMessage(" 데미지석을 0개 얻었습니다")); 
     pc.getInventory().removeItem(555101, 50);
     }
        } else {
         pc.sendPackets(new S_SystemMessage("데미지석파편 50개가 필요합니다.")); //재료가 부족할시에 멘트
        }
//########### 데미지석  ############# 
//########### 데미지석  ############# 

    } else if (itemId == 555110){   // 업그레이드 주문서 번호
        if (pc.getInventory(). checkItem(555110, 50)){ //수량체크부분
      Random random = new Random();
     int k3 = random.nextInt(100);
     if (k3 <= 10) { //확률 부분

            pc.getInventory(). consumeItem(555110, 50); //아이템 삭제부분
            pc.getInventory().storeItem(555105, 1); //지급 부분
    Announcements.getInstance().announceToAll((pc.getName()+"님께서 촐기부적1개를 얻었습니다."));
  }
    if (k3 >= 10 && k3 <= 100) {  //확률 부분
            pc.getInventory(). consumeItem(555110, 50); //장비는 그대로 재료만 삭제 되게끔 하세요.
     pc.sendPackets(new S_SystemMessage(" 촐기부적을 0개 얻었습니다")); 
     pc.getInventory().removeItem(555110, 50);
     }
        } else {
         pc.sendPackets(new S_SystemMessage("촐기부적파편 50개가 필요합니다.")); //재료가 부족할시에 멘트
        }
//########### 데미지석  ############# 
//########### 데미지석  ############# 

    } else if (itemId == 555111){   // 업그레이드 주문서 번호
        if (pc.getInventory(). checkItem(555111, 50)){ //수량체크부분
      Random random = new Random();
     int k3 = random.nextInt(100);
     if (k3 <= 10) { //확률 부분

            pc.getInventory(). consumeItem(555111, 50); //아이템 삭제부분
            pc.getInventory().storeItem(555106, 1); //지급 부분
    Announcements.getInstance().announceToAll((pc.getName()+"님께서 용기부적1개를 얻었습니다."));
  }
    if (k3 >= 10 && k3 <= 100) {  //확률 부분
            pc.getInventory(). consumeItem(555111, 50); //장비는 그대로 재료만 삭제 되게끔 하세요.
     pc.sendPackets(new S_SystemMessage(" 용기부적을 0개 얻었습니다")); 
     pc.getInventory().removeItem(555111, 50);
     }
        } else {
         pc.sendPackets(new S_SystemMessage("용기부적파편 50개가 필요합니다.")); //재료가 부족할시에 멘트
        }
//########### 데미지석  ############# 

               /////영구피물약////

                                 } else if (itemId == 555107) { // 영구피물약
                                          if (pc.getBaseMaxHp() < 6000) { // HP설정제한
                                            Random random = new Random();
                                            int chance = random.nextInt(100); 
                                            if (chance <= 10) { // 성공확률
                                             pc.addBaseMaxHp((byte) 50); // HP보너스
                                             pc.sendPackets(new S_SkillSound(pcObjid, 5935));
                                             pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 피가 50 영구히 상승했습니다."));
                                            } else {
                                             pc.addBaseMaxHp((byte) 0); // HP마이너스
                                             pc.sendPackets(new S_SkillSound(pcObjid, 746));
                                             pc.sendPackets(new S_SystemMessage(pc.getName() + "님의 피 상승에 실패하였습니다."));

                                                }
                                             pc.getInventory().removeItem(l1iteminstance, 1); 
                                                }
                                              pc.sendPackets(new S_OwnCharStatus2(pc));
                                              pc.save(); // 저장
                                              
                                   
                             //////영구피물약 //////

  // 초보 아이템 상자 
      } else if (itemId == 500000){  // 초보아이템 상자 번호
         if (pc.getInventory().checkItem(500000, 1) ){  // 체크 되는 아이템과 수량
             pc.getInventory().consumeItem(500000, 1);  // 삭제되는 아이템과 수량
                pc.getInventory().storeItem(40030, 10); // 상아탑촐기 
                createNewItem(pc,20028, 1, 0); // 상아탑 투구
                createNewItem(pc,20082, 1, 0); // 상아탑 티셔츠
                createNewItem(pc,20126, 1, 0); // 상아탑 갑옷
                createNewItem(pc,20173, 1, 0); // 상아탑 장갑
                createNewItem(pc,20206, 1, 0); // 상아탑 부츠
                createNewItem(pc,20232, 1, 0); // 상아탑 방패
                createNewItem(pc,20080, 1, 0); // 상아탑 망토
         if (pc.isKnight()){
                createNewItem(pc,35, 1, 0); // 상아탑 검
                createNewItem(pc,48, 1, 0); // 상아탑 양손검
                createNewItem(pc,40014, 10, 0); // 용기의 물약
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isCrown()){
                createNewItem(pc,35, 1, 0); // 상아탑 검
                createNewItem(pc,48, 1, 0); // 상아탑 양손검
                createNewItem(pc,40031, 10, 0); // 악마의 피
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isWizard()){
                createNewItem(pc,120, 1, 0); // 상아탑 지팡이
                createNewItem(pc,40016, 10, 0); // 지혜의 물약
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isElf()){
                createNewItem(pc,40068, 10, 0); // 엘븐 와퍼
                createNewItem(pc,35, 1, 0); // 상아탑 검
                createNewItem(pc,174, 1, 0); // 상아탑 활
                createNewItem(pc,175, 1, 0); // 상아탑 활
                createNewItem(pc,40744, 1000, 0); // 은화살
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isDarkelf()){
                createNewItem(pc,73, 1, 0); // 상아탑 이도류
                createNewItem(pc,156, 1, 0); // 상아탑 크로우
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isDragonKnight()){
                createNewItem(pc,500024, 10, 0); // 유그드라 열매
                createNewItem(pc,35, 1, 0); // 상아탑 검
                createNewItem(pc,147, 1, 0); // 상아탑 도끼
                createNewItem(pc,48, 1, 0); // 상아탑 양손검
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
         if (pc.isBlackWizard()){
                createNewItem(pc,500024, 10, 0); // 유그드라 열매
                createNewItem(pc,120, 1, 0); // 상아탑 지팡이
                pc.sendPackets(new S_SystemMessage("기본 아이템이 지급 되었습니다."));
                }
              } 
		 } else if (itemId == 500059){  // 홍보 감사 상자(특)
				    if (pc.getInventory().checkItem(500059, 1) ){  // 체크 되는 아이템과 수량
				        pc.getInventory().consumeItem(500059, 1);  // 삭제되는 아이템과 수량
			            pc.getInventory().storeItem(500082, 1); // 스파토이 인형
                                    pc.getInventory().storeItem(555599, 30); // 디스석파편
                                    pc.getInventory().storeItem(555100, 30); // 미티어석파편
                                    pc.getInventory().storeItem(555101, 30); // 데미지석파편
                                    pc.getInventory().storeItem(555111, 30); // 용기파편
                                    pc.getInventory().storeItem(555110, 30); // 촐기파편
                                    pc.getInventory().storeItem(555102, 20); // 무한물약파편
                                    pc.getInventory().storeItem(555107, 20); // 스파토이 인형
                                    pc.getInventory().storeItem(555108, 100); // 홍보석
			            pc.getInventory().storeItem(500083, 100); // 야히의 징표
			            pc.getInventory().storeItem(600002, 1);  // 지존목걸이
						pc.getInventory().storeItem(140074, 5);  //축 갑옷 주문서
			            pc.getInventory().storeItem(140087, 5);  //축 무기 주문서
				    }  
    	  } else if (itemId == 42053) { 
		  int castle_id = 0; 
		  if (pc.getClanid() != 0) {  
			  L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			  if (clan != null) { 
				  castle_id = clan.getCastleId();
				  }
				  }
			  if (castle_id == 1 || castle_id == 2 || castle_id == 3 || castle_id == 4 || castle_id == 5) {  
			  L1Teleport.teleport(pc, 32700, 32896, (short) 523, 5, true);
		   } else {
			  pc.sendPackets(new S_SystemMessage("성을 소유한 혈맹원만 사용이 가능합니다."));
			}
		   } else if (itemId == 42054) { 
		      int castle_id = 0; 
		       if (pc.getClanid() != 0) {  
			    L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			   if (clan != null) { 
				castle_id = clan.getCastleId();
				}
				}
			   if (castle_id == 1 || castle_id == 2 || castle_id == 3 || castle_id == 4 || castle_id == 5) {  
				L1Teleport.teleport(pc, 32691, 32894, (short) 524, 5, true);
			} else {
				pc.sendPackets(new S_SystemMessage("성을 소유한 혈맹원만 사용이 가능합니다."));
				}
		    } else if (itemId == 500002) { // 4단계 종합마법책
				boolean isLawful = true;
				if(!pc.isWizard() && !pc.isElf()){ // 법사나 요정이 아니면
				 pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				 return;
			     }
				if((pc.getLevel() < 16 && pc.isWizard()) || (pc.getLevel() < 32 && pc.isElf()))  { 
				 pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				 return;
				 }					
				  SpellBook7(pc, 4, client,isLawful); // pc 다음 1이 단계, 1단계면 1, 2단계면 2
				  pc.sendPackets(new S_SkillSound(pc.getId(), 224));
				  pc.sendPackets(new S_SystemMessage("\\fY마법의 기운이 몸속으로 스며듭니다."));  // 메세지 출력
				  pc.getInventory().removeItem(l1iteminstance, 1); // 아이템을 지워주고 끝
				
			} else if (itemId == 500003) { // 5단계 종합마법책
			      boolean isLawful = true;
				 if(!pc.isWizard() && !pc.isElf()){ // 법사나 요정이 아니면
				   pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				   return;
				   }
				 if((pc.getLevel() < 20 && pc.isWizard()) || (pc.getLevel() < 40 && pc.isElf()))  { 
				   pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				   return;
				   }
				   SpellBook7(pc, 5, client,isLawful); // pc 다음 1이 단계, 1단계면 1, 2단계면 2
				   pc.sendPackets(new S_SkillSound(pc.getId(), 224));
				   pc.sendPackets(new S_SystemMessage("\\fY마법의 기운이 몸속으로 스며듭니다."));  // 메세지 출력
				   pc.getInventory().removeItem(l1iteminstance, 1); // 아이템을 지워주고 끝
				   
			} else if (itemId == 500004) { // 6단계 종합마법책
					boolean isLawful = true;
				  if(!pc.isWizard() && !pc.isElf()){ // 법사나 요정이 아니면
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					return;
					}
				  if((pc.getLevel() < 24 && pc.isWizard()) || (pc.getLevel() < 48 && pc.isElf()))  { 
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					return;
					}
					SpellBook7(pc, 6, client,isLawful); // pc 다음 1이 단계, 1단계면 1, 2단계면 2
					pc.sendPackets(new S_SkillSound(pc.getId(), 224));
					pc.sendPackets(new S_SystemMessage("\\fY마법의 기운이 몸속으로 스며듭니다."));  // 메세지 출력
					pc.getInventory().removeItem(l1iteminstance, 1); // 아이템을 지워주고 끝
			
			} else if (itemId == 555589){  // 기사 기술서 묶음
				if (pc.getInventory().checkItem(555589, 1) ){  /// 체크 되는 아이템과 수량
				    pc.getInventory().consumeItem(555589, 1);  /// 삭제되는 아이템과 수량
			        pc.getInventory().storeItem(40164, 1); //기술서 (쇼크 스턴)
			        pc.getInventory().storeItem(40165, 1); //기술서 (리덕션 아머)
			        pc.getInventory().storeItem(40166, 1);  ///기술서 (바운스 어택)
					pc.getInventory().storeItem(41147, 1);  //기술서 (솔리드 캐리지)
			//      pc.getInventory().storeItem(41148, 5);  //기술서 (카운터 배리어)
				    } 
			} else if (itemId == 555590){  // 군주 기술서 묶음
			    if (pc.getInventory().checkItem(555590, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555590, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40227, 1); //마법서 (글로잉 오라)
		            pc.getInventory().storeItem(40226, 1); //마법서 (트루 타겟)
		            pc.getInventory().storeItem(40228, 1);  //마법서 (콜 클렌)
					pc.getInventory().storeItem(40229, 1);  //마법서 (샤이닝 오라)
		            pc.getInventory().storeItem(40230, 1);  //마법서 (브레이브 오라)
			    }
			} else if (itemId == 555596){  // 다엘  마법 묶음
			    if (pc.getInventory().checkItem(555596, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555596, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40265, 1); //흑정령의 수정 (블라인드 하이딩)
		            pc.getInventory().storeItem(40266, 1); //흑정령의 수정 (인챈트 베놈)
		            pc.getInventory().storeItem(40267, 1);  //흑정령의 수정 (쉐도우 아머)
					pc.getInventory().storeItem(40268, 1);  //흑정령의 수정 (브링 스톤)
		            pc.getInventory().storeItem(40269, 1);  //흑정령의 수정 (드레스 마이티)
		            pc.getInventory().storeItem(40270, 1); //흑정령의 수정 (무빙 악셀레이션)
		            pc.getInventory().storeItem(40271, 1); //흑정령의 수정 (버닝 스피릿츠)
		            pc.getInventory().storeItem(40272, 1);  //흑정령의 수정 (다크 블라인드)
					pc.getInventory().storeItem(40273, 1);  //흑정령의 수정 (베놈 레지스트)
		            pc.getInventory().storeItem(40274, 1);  //흑정령의 수정 (드레스 덱스터리티)
		    //      pc.getInventory().storeItem(40275, 1); //흑정령의 수정 (더블 브레이크)
		    //      pc.getInventory().storeItem(40276, 1); //흑정령의 수정 (언케니 닷지)
		            pc.getInventory().storeItem(40277, 1);  //흑정령의 수정 (쉐도우 팽)
			//		pc.getInventory().storeItem(40278, 1);  //흑정령의 수정 (파이널 번)
		    //      pc.getInventory().storeItem(40279, 1);  //흑정령의 수정 (드레스 이베이젼)
		        }
			} else if (itemId == 555591){  // 요정 공통 마법 묶음
			    if (pc.getInventory().checkItem(555591, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555591, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40232, 1); //정령의 수정 (레지스트 매직)
		            pc.getInventory().storeItem(40233, 1); //정령의 수정 (바디 투 마인드)
		            pc.getInventory().storeItem(40235, 1);  //정령의 수정 (클리어 마인드)
					pc.getInventory().storeItem(40236, 1);  //정령의 수정 (레지스트 엘리멘트)
		            pc.getInventory().storeItem(40237, 1);  //정령의 수정 (리턴 투 네이처)
		    //      pc.getInventory().storeItem(40238, 1); //정령의 수정 (블러드 투 소울)
		            pc.getInventory().storeItem(40239, 1); //정령의 수정 (프로텍션 프롬 엘리멘트)
		    //      pc.getInventory().storeItem(40240, 1);  //정령의 수정 (트리플 애로우)
					pc.getInventory().storeItem(40241, 1);  //정령의 수정 (엘리멘탈 폴다운)
		    //      pc.getInventory().storeItem(40242, 1);  //정령의 수정 (이레이즈 매직)
		            pc.getInventory().storeItem(40243, 1); //정령의 수정 (서먼 레서 엘리멘탈)
		    //      pc.getInventory().storeItem(40244, 1); //정령의 수정 (에어리어 오브 사일런스)
		    //      pc.getInventory().storeItem(40245, 1);  //정령의 수정 (서먼 그레이터 엘리멘탈)
			//		pc.getInventory().storeItem(40246, 1);  //정령의 수정 (카운터 미러)
		        }
			} else if (itemId == 555592){  // 요정 땅 계열 마법 묶음
			    if (pc.getInventory().checkItem(555592, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555592, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40247, 1); //정령의 수정 (어스 스킨)
		            pc.getInventory().storeItem(40248, 1); //정령의 수정 (인탱글)
		    //      pc.getInventory().storeItem(40249, 1);  //정령의 수정 (어스 바인드)
					pc.getInventory().storeItem(40250, 1);  //정령의 수정 (블레스 오브 어스)
		    //      pc.getInventory().storeItem(40251, 1);  //정령의 수정 (아이언 스킨)
					pc.getInventory().storeItem(40252, 1); //정령의 수정 (엑조틱 바이탈라이즈)
			      }
			} else if (itemId == 555593){  // 요정 물 계열 마법 묶음
			    if (pc.getInventory().checkItem(555593, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555593, 1);  /// 삭제되는 아이템과 수량
			        pc.getInventory().storeItem(41152, 1);  //정령의 수정 (폴루트 워터)
		            pc.getInventory().storeItem(40253, 1); //정정령의 수정 (워터 라이프)
		            pc.getInventory().storeItem(40254, 1);  //정령의 수정 (네이쳐스 터치)
			//		pc.getInventory().storeItem(40255, 1);  //정령의 수정 (네이쳐스 블레싱)
		    //		pc.getInventory().storeItem(41151, 1);  //정령의 수정 (아쿠아 프로텍트)
		          }
			} else if (itemId == 555594){  // 요정 불 계열 마법 묶음
			    if (pc.getInventory().checkItem(555594, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555594, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40256, 1); //정령의 수정 (파이어 웨폰)
		            pc.getInventory().storeItem(40257, 1); //정령의 수정 (블레스 오브 파이어)
		    //      pc.getInventory().storeItem(40258, 1);  //정령의 수정 (버닝 웨폰)
			//		pc.getInventory().storeItem(40259, 1);  //정령의 수정 (엘리멘탈 파이어)
		    		pc.getInventory().storeItem(41150, 1);  //정령의 수정 (어디셔널 파이어)
		    //		pc.getInventory().storeItem(41149, 1);  //정령의 수정 (소울 오브 프레임)
		         }
			} else if (itemId == 555595){  // 요정 바람 계열 마법 묶음
			    if (pc.getInventory().checkItem(555595, 1) ){  /// 체크 되는 아이템과 수량
			        pc.getInventory().consumeItem(555595, 1);  /// 삭제되는 아이템과 수량
		            pc.getInventory().storeItem(40260, 1); //정령의 수정 (윈드 샷)
		            pc.getInventory().storeItem(40261, 1); //정령의 수정 (윈드 워크)
		    //      pc.getInventory().storeItem(40262, 1);  //정령의 수정 (아이 오브 스톰)
			//      pc.getInventory().storeItem(40263, 1);  //정령의 수정 (스톰 샷)
					pc.getInventory().storeItem(40264, 1);  //정령의 수정 (윈드 셰클)
			//		pc.getInventory().storeItem(41153, 1);  //정령의 수정 (스트라이커 게일)
		         }
			}else if(itemId == 500001){ //케릭복구주문서 추가
				Connection connection = null;
				connection = L1DatabaseFactory.getInstance().getConnection();
				PreparedStatement preparedstatement = connection.prepareStatement("UPDATE characters SET LocX=33087,LocY=33399,MapID=4 WHERE account_name=?and MapID not in (99,39,5166)");
				preparedstatement.setString(1, client.getAccountName());
				preparedstatement.execute();
				preparedstatement.close();
				connection.close();
				pc.getInventory().removeItem(l1iteminstance, 1);
				pc.sendPackets(new S_SystemMessage("모든 케릭터의 좌표가 정상적으로 복구 되었습니다."));

			} else if (itemId == 40089 || itemId == 140089) { // 부활 스크롤, 축복된 부활 스크롤
					L1Character resobject = (L1Character) L1World.getInstance()
							.findObject(resid);
					if (resobject != null) {
					if (resobject instanceof L1PcInstance) {
						L1PcInstance target = (L1PcInstance) resobject;
					if (pc.getId() == target.getId()) {
						return;
						}
					if (L1World.getInstance().getVisiblePlayer(target, 0).size() > 0) {
					for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(target, 0)) {
					if (!visiblePc.isDead()) {
						pc.sendPackets(new S_ServerMessage(592));
						return;
						}
						}
						}
					if (target.getCurrentHp() == 0 && target.isDead() == true) {
					if (pc.getMap().isUseResurrection()) {
						target.setTempID(pc.getId());
					if (itemId == 40089) {
						// 또 부활하고 싶습니까? (Y/N)
						target.sendPackets(new S_Message_YN(321, ""));
			 } else if (itemId == 140089) {
						// 또 부활하고 싶습니까? (Y/N)
						target.sendPackets(new S_Message_YN(322, ""));
						}
			 } else {
						return;
						}
						}
			 } else if (resobject instanceof L1NpcInstance) {
					if (!(resobject instanceof L1TowerInstance)) {
						L1NpcInstance npc = (L1NpcInstance) resobject;
					if (npc.getNpcTemplate(). isCantResurrect()) {
						pc.getInventory(). removeItem(l1iteminstance, 1);
						return;
						}
					if (npc instanceof L1PetInstance && L1World.getInstance().getVisiblePlayer(npc, 0).size() > 0) {
					for (L1PcInstance visiblePc : L1World.getInstance().getVisiblePlayer(npc, 0)) {
					if (!visiblePc.isDead()) {
						// \f1그 자리소에 다른 사람이 서 있으므로 부활시킬 수가 없습니다.
						pc.sendPackets(new S_ServerMessage(592));
						return;
						}
						}
						}
					if (npc.getCurrentHp() == 0 && npc.isDead()) {
						npc.resurrect(npc.getMaxHp() / 4);
						npc.setResurrect(true);
						}
						}
						}
					    }
					    pc.getInventory().removeItem(l1iteminstance, 1);
			 } else if (itemId > 40169 && itemId < 40226 || itemId >= 45000 && itemId <= 45022) { // 마법서
					    useSpellBook(pc, l1iteminstance, itemId);
			 } else if (itemId > 40225 && itemId < 40232) {
					if (pc.isCrown() || pc.isGm()) {
					if (itemId == 40226 && pc.getLevel() >= 15) {
						SpellBook4(pc, l1iteminstance, client);
			 } else if (itemId == 40228 && pc.getLevel() >= 30) {
						SpellBook4(pc, l1iteminstance, client);
			 } else if (itemId == 40227 && pc.getLevel() >= 40) {
						SpellBook4(pc, l1iteminstance, client);
			 } else if ((itemId == 40231 || itemId == 40232) && pc.getLevel() >= 45) {
						SpellBook4(pc, l1iteminstance, client);
			 } else if (itemId == 40230 && pc.getLevel() >= 50) {
						SpellBook4(pc, l1iteminstance, client);
			 } else if (itemId == 40229 && pc.getLevel() >= 55) {
						SpellBook4(pc, l1iteminstance, client);
			 } else {
						pc.sendPackets(new S_ServerMessage(312)); // LV가 낮아서
						}
			 } else {
						pc.sendPackets(new S_ServerMessage(79));
					}
			 } else if (itemId >= 40232 && itemId <= 40264 // 정령의 수정
						|| itemId >= 41149 && itemId <= 41153) {
					   useElfSpellBook(pc, l1iteminstance, itemId);
			 } else if (itemId > 40264 && itemId < 40280) { // 어둠 정령의 수정
					if (pc.isDarkelf() || pc.isGm()) {
					if (itemId >= 40265 && itemId <= 40269 && pc.getLevel() >= 15) {
						SpellBook1(pc, l1iteminstance, client);
			 } else if (itemId >= 40270 && itemId <= 40274 && pc.getLevel() >= 30) {
						SpellBook1(pc, l1iteminstance, client);
			 } else if (itemId >= 40275 && itemId <= 40279 && pc.getLevel() >= 45) {
						SpellBook1(pc, l1iteminstance, client);
			 } else {
						pc.sendPackets(new S_ServerMessage(312));
						}
			 } else {
						pc.sendPackets(new S_ServerMessage(79)); // (원문:어둠 정령의 수정은 다크 에르프만을 습득할 수 있습니다.)
					    }
			 } else if (itemId >= 40164 && itemId <= 40166 // 기술서
						|| itemId >= 41147 && itemId <= 41148) {
					if (pc.isKnight() || pc.isGm()) {
					if (itemId >= 40164 && itemId <= 40165 && pc.getLevel() >= 50) { // 스탠, 축소 아모
						SpellBook3(pc, l1iteminstance, client);
			 } else if (itemId >= 41147 && itemId <= 41148 && pc.getLevel() >= 50) { // 솔리드 왕복대, 카운터 바리어
						SpellBook3(pc, l1iteminstance, client);
			 } else if (itemId == 40166 && pc.getLevel() >= 60) { // 바운스아탁크
						SpellBook3(pc, l1iteminstance, client);
			 } else {
						pc.sendPackets(new S_ServerMessage(312));
						}
			 } else {
						pc.sendPackets(new S_ServerMessage(79));
					    }
			 } else if (itemId >= 210020 && itemId <= 210034) {
					if (pc.isDragonKnight() || pc.isGm()) {
					if (itemId >= 210020 && itemId <= 210023 && pc.getLevel() >= 15) { // 용기사의 서판
						SpellBook5(pc, l1iteminstance, client);
			 } else if (itemId >= 210024 && itemId <= 210031 && pc.getLevel() >= 30) { // 용기사의 서판
						SpellBook5(pc, l1iteminstance, client);
			 } else if (itemId >= 210032 && itemId <= 210034 && pc.getLevel() >= 45) {
						SpellBook5(pc, l1iteminstance, client);
			 } else {
						pc.sendPackets(new S_ServerMessage(312));
						}
			 } else {
						pc.sendPackets(new S_ServerMessage(79));
					    }
			 } else if (itemId >= 210000 && itemId <= 210019) {
					if (pc.isBlackWizard() || pc.isGm()) {
					if (itemId >= 210000 && itemId <= 210004 && pc.getLevel() >= 10) {// 기억의 수정
						SpellBook6(pc, l1iteminstance, client);
			 } else if (itemId >= 210005 && itemId <= 210009 && pc.getLevel() >= 20) {// 기억의 수정
						SpellBook6(pc, l1iteminstance, client);
			 } else if (itemId >= 210010 && itemId <= 210014 && pc.getLevel() >= 30) {
						SpellBook6(pc, l1iteminstance, client);
			 } else if (itemId >= 210015 && itemId <= 210019 && pc.getLevel() >= 40) {
						SpellBook6(pc, l1iteminstance, client);
			 } else {
						pc.sendPackets(new S_ServerMessage(312));
						}
			 } else {
						pc.sendPackets(new S_ServerMessage(79));
					    }
		     } else if (itemId == 40079 || itemId == 40095) { // 귀환 스크롤
					if (pc.getMap().isEscapable() || pc.isGm()) {
						int[] loc = Getback.GetBack_Location(pc, true);
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2], 5, true);
						pc.getInventory().removeItem(l1iteminstance, 1);
			 } else {
						pc.sendPackets(new S_ServerMessage(647));
						// pc.sendPackets(new
						// S_CharVisualUpdate(pc));
					    }
					    cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
			 } else if (itemId == 40124) { // 혈맹 귀환 스크롤
					if (pc.getMap().isEscapable() || pc.isGm()) {
						int castle_id = 0;
						int house_id = 0;
					if (pc.getClanid() != 0) { // 크란 소속
						L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
					if (clan != null) {
						castle_id = clan.getCastleId();
						house_id = clan.getHouseId();
						}
						}
					if (castle_id != 0) { // 성주 크란원
					if (pc.getMap().isEscapable() || pc.isGm()) {
						int[] loc = new int[3];
						loc = L1CastleLocation.getCastleLoc(castle_id);
						int locx = loc[0];
						int locy = loc[1];
						short mapid = (short) (loc[2]);
						L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
						pc.getInventory().removeItem(l1iteminstance, 1);
			 } else {
						pc.sendPackets(new S_ServerMessage(647));
						}
					if (castle_id != 0){ // 인형 공성존에서 사용 불가
								   if(itemId == 500006 || itemId == 500007 || itemId == 500008 || itemId == 41248 
									|| itemId == 41249 || itemId == 41250 || itemId == 500020 || itemId == 500021 
									|| itemId == 500052 || itemId == 500053 || itemId == 500054 || itemId == 500082
									|| itemId == 500071 || itemId == 500055 || itemId == 500056 || itemId == 500057
									|| itemId == 500058 || itemId == 500022 || itemId == 500023){
								   pc.sendPackets(new S_SystemMessage("\fY공성 지역에서는 사용 할 수 없습니다."));
								   return;
								   }
								   }

								   if(pc.getMapId() == 37 || pc.getMapId() == 65 || pc.getMapId() == 67){ // **인형 용방에서는 사용불가**// 
									if(itemId == 500006 || itemId == 500007 || itemId == 500008 || itemId == 41248 
									|| itemId == 41249 || itemId == 41250 || itemId == 500020 || itemId == 500021 
									|| itemId == 500052 || itemId == 500053 || itemId == 500054 || itemId == 500082
									|| itemId == 500071 || itemId == 500055 || itemId == 500056 || itemId == 500057
									|| itemId == 500058 || itemId == 500022 || itemId == 500023){
								   pc.sendPackets(new S_SystemMessage("\fY이곳 에서는 사용 할 수 없습니다."));
								   return;
								   }
								   }
						} else if (house_id != 0) { // 아지트 소유 크란원
							if (pc.getMap().isEscapable() || pc.isGm()) {
								int[] loc = new int[3];
								loc = L1HouseLocation.getHouseLoc(house_id);
								int locx = loc[0];
								int locy = loc[1];
								short mapid = (short) (loc[2]);
								L1Teleport.teleport(pc, locx, locy, mapid, 5,
										true);
								pc.getInventory().removeItem(l1iteminstance, 1);
							} else {
								pc.sendPackets(new S_ServerMessage(647));
							}
						} else {
							if (pc.getHomeTownId() > 0) {
								int[] loc = L1TownLocation.getGetBackLoc(pc
										.getHomeTownId());
								int locx = loc[0];
								int locy = loc[1];
								short mapid = (short) (loc[2]);
								L1Teleport.teleport(pc, locx, locy, mapid, 5,
										true);
								pc.getInventory().removeItem(l1iteminstance, 1);
							} else {
								int[] loc = Getback.GetBack_Location(pc, true);
								L1Teleport.teleport(pc, loc[0], loc[1],
										(short) loc[2], 5, true);
								pc.getInventory().removeItem(l1iteminstance, 1);
							}
						}
					} else {
						pc.sendPackets(new S_ServerMessage(647));
					}
					cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
				} else if (itemId == 140100 || itemId == 40100
						|| itemId == 40099 // 축복된 텔레포트 스크롤, 텔레포트 스크롤
						|| itemId == 40086 || itemId == 40863) { // 스펠 스크롤(텔레포트)
					L1BookMark bookm = pc.getBookMark(btele);
					if (bookm != null) { // 북마크를 취득 할 수 있으면(자) 텔레포트
						if (pc.getMap().isEscapable() || pc.isGm()) {
							int newX = bookm.getLocX();
							int newY = bookm.getLocY();
							short mapId = bookm.getMapId();

							if (itemId == 40086) { // 매스 텔레포트 스크롤
								for (L1PcInstance member : L1World.getInstance()
										. getVisiblePlayer(pc)) {
									if (pc.getLocation()
											. getTileLineDistance(member
													. getLocation()) <= 3
											&& member.getClanid() == pc
													. getClanid()
											&& pc.getClanid() != 0
											&& member.getId() != pc.getId()) {
										L1Teleport.teleport(member, newX,
												newY, mapId, 5, true);
									}
								}
							}
							L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
							pc.getInventory().removeItem(l1iteminstance, 1);
						} else {
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false)); 
							pc.sendPackets(new S_ServerMessage(79));
						}
					} else {
						if (pc.getMap().isTeleportable() || pc.isGm()) {
							L1Location newLocation = pc.getLocation()
									. randomLocation(200, true);
							int newX = newLocation.getX();
							int newY = newLocation.getY();
							short mapId = (short) newLocation.getMapId();

							if (itemId == 40086) { // 매스 텔레포트 스크롤
								for (L1PcInstance member : L1World.getInstance()
										. getVisiblePlayer(pc)) {
									if (pc.getLocation()
											. getTileLineDistance(member
													. getLocation()) <= 3
											&& member.getClanid() == pc
													. getClanid()
											&& pc.getClanid() != 0
											&& member.getId() != pc.getId()) {
										L1Teleport.teleport(member, newX,
												newY, mapId, 5, true);
									}
								}
							}
							L1Teleport.teleport(pc, newX, newY, mapId, 5, true);
							pc.getInventory().removeItem(l1iteminstance, 1);
						} else {
							pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK, false));
							pc.sendPackets(new S_ServerMessage(276));
						}
					}
					cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
				} else if (itemId == 240100) { // 저주해진 텔레포트 스크롤(오리지날 아이템)
					L1Teleport.teleport(pc, pc.getX(), pc.getY(),
							pc.getMapId(), pc.getHeading(), true);
					pc.getInventory().removeItem(l1iteminstance, 1);
					cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
				} else if (itemId >= 40901 && itemId <= 40908) { // 각종 약혼 반지
					L1PcInstance partner = null;
					boolean partner_stat = false;
					if (pc.getPartnerId()  != 0) { // 결혼중
						partner = (L1PcInstance) L1World.getInstance()
								.findObject(pc.getPartnerId());
						if (partner != null && partner.getPartnerId() != 0
								&& pc.getPartnerId() == partner.getId()
								&& partner.getPartnerId() == pc.getId()) {
							partner_stat = true;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(662)); // \f1당신은 결혼하지 않았습니다.
						return;
					}

					if (partner_stat) {
						boolean castle_area = L1CastleLocation
								.checkInAllWarArea(
								// 몇개의 성에리어
										partner.getX(), partner.getY(), partner
												.getMapId());
						if ((partner.getMapId() == 0 || partner.getMapId() == 4 || partner
								.getMapId() == 304)
								&& castle_area == false) {
							L1Teleport.teleport(pc, partner.getX(), partner
									.getY(), partner.getMapId(), 5, true);
						} else {
							pc.sendPackets(new S_ServerMessage(547)); // \f1당신의 파트너는 지금 당신이 갈 수 없는 곳에서 플레이중입니다.
						}
					} else {
						pc.sendPackets(new S_ServerMessage(546)); // \f1당신의 파트너는 지금 플레이를 하고 있지 않습니다.
					}
				/*** 고렙 중렙  고유 사냥터  ***/
				} else if (itemId == 500102) { // 고렙  사냥터
				     if (pc.isDarkelf()|| pc.isElf()) {
				     if (pc.getLevel() >= 80){
				     int locx = 32705;
				     int locy = 32831;
				     short mapid = 205;
				     L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				     pc.getInventory().removeItem(l1iteminstance, 1);
				} else {
				     pc.sendPackets(new S_SystemMessage("레벨 80이상만 들어가실 수 있습니다."));
				     }
				} else {
				      pc.sendPackets(new S_SystemMessage("다크엘프,엘프 클래스만 들어가실수 있습니다."));
				     }

				} else if (itemId == 500103) { // 중렙  사냥터
					if (pc.isDarkelf()|| pc.isElf()) {
				     if (pc.getLevel() < 75){
				     int locx = 32878;
				     int locy = 33112;
				     short mapid = 508;
				     L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				     pc.getInventory().removeItem(l1iteminstance, 1);
				 } else {
				     pc.sendPackets(new S_SystemMessage("레벨 75이하만 들어가실 수 있습니다."));
				     }
				 } else {
				     pc.sendPackets(new S_SystemMessage("다크엘프,엘프 클래스만 들어가실수 있습니다."));
				     }
				          //////////////////////////////다엘 요정 끝
					          ///////////////////////////////////////////////////법사 시작
				} else if (itemId == 500104) { // 고렙  사냥터
				     if (pc.isWizard()|| pc.isBlackWizard()) {
				     if (pc.getLevel() >= 80){
				     int locx = 32869;
				     int locy = 32598;
				     short mapid = 503;
				     L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				     pc.getInventory().removeItem(l1iteminstance, 1);
				       
				} else {
				      pc.sendPackets(new S_SystemMessage("레벨 80이상만 들어가실 수 있습니다."));
				      }
				} else {
				      pc.sendPackets(new S_SystemMessage("환술사,마법사 클래스만 들어가실수 있습니다."));
				      }
			    } else if (itemId == 500105) { // 중렙  사냥터
				      if (pc.isWizard()|| pc.isBlackWizard()) {
				      if (pc.getLevel() < 75){
				      int locx = 32861;
				      int locy = 32601;
				      short mapid = 504;
				      L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				      pc.getInventory().removeItem(l1iteminstance, 1);
				} else {
				      pc.sendPackets(new S_SystemMessage("레벨 75이하만 들어가실 수 있습니다."));
				      }
				} else {
				      pc.sendPackets(new S_SystemMessage("환술사,마법사 클래스만 들어가실수 있습니다."));
				      }
				     ///////////////////법사 끝
				      ///////////////////////////////////////////////////기사 군주 시작
				} else if (itemId == 500106) { // 고렙  사냥터
				     if (pc.isDragonKnight()|| pc.isKnight()|| pc.isCrown()) {
				     if (pc.getLevel() >= 80){
				     int locx = 32869;
				     int locy = 32598;
				     short mapid = 505;
				     L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				     pc.getInventory().removeItem(l1iteminstance, 1);
				       
				 } else {
				     pc.sendPackets(new S_SystemMessage("레벨 80이상만 들어가실 수 있습니다."));
				     }
				 } else {
				     pc.sendPackets(new S_SystemMessage("용기사,기사,군주 클래스만 들어가실수 있습니다."));
				     }
				 } else if (itemId == 500107) { // 중렙  사냥터
					 if (pc.isDragonKnight()|| pc.isKnight()|| pc.isCrown()) {
				       if (pc.getLevel() < 75){
				       int locx = 32861;
				       int locy = 32601;
				       short mapid = 506;
				       L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
				       pc.getInventory().removeItem(l1iteminstance, 1);
				 } else {
				       pc.sendPackets(new S_SystemMessage("레벨 75이하만 들어가실 수 있습니다."));
				       }
				 } else {
				       pc.sendPackets(new S_SystemMessage("용기사,기사,군주 클래스만 들어가실수 있습니다."));
				      }
				     ///////////////////기사 군주 끝
					 /**보스방(나이트메어)**/
				} else if (itemId == 500109) { // 나이트 메어
					     if (pc.getLevel() >= 80){
					     int locx = 32868;
					     int locy = 32601;
					     short mapid = 502;
					     L1Teleport.teleport(pc, locx, locy, mapid, 5, false);
					     pc.getInventory().removeItem(l1iteminstance, 1);
					       
				} else {
					     pc.sendPackets(new S_SystemMessage("레벨 80이상만 들어가실 수 있습니다."));
					     }
					     
				//이동식창고
				} else if (itemId == 555583) {
				           L1Npc l1npc = NpcTable.getInstance().getTemplate(60009);
				       if (pc.getChango() == 0) {
				       int pc_castleId = L1CastleLocation.getCastleIdByArea(pc);
				       if (!(pc_castleId == 1 || pc_castleId == 2 || pc_castleId == 3 || pc_castleId == 4
				          || pc_castleId == 5 || pc_castleId == 6 || pc_castleId == 7 || pc_castleId == 8)) {
				       try {
				          String s34 = l1npc.getImpl();
				          Constructor constructor = Class.forName(
				          "l1j.server.server.model.Instance." + s34 + "Instance").getConstructors()[0];
				          L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(l1npc);
				          npc.setId(IdFactory.getInstance().nextId());
				          npc.setMap(pc.getMapId());
				          npc.setX(pc.getX()+ 1);
				          npc.setY(pc.getY()+ 1);
				          npc.setHeading(pc.getHeading());
				          L1World.getInstance().storeObject(npc);
				          L1World.getInstance().addVisibleObject(npc);
				          pc.sendPackets(new S_SystemMessage("특수 창고를 소환했습니다."));
				          pc.save();
				          L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 60000);
				          timer.begin();
				      } catch (Exception exception) {
				      } 
				  } else {
				          pc.sendPackets(new S_SystemMessage("공성존에서는 사용할수 없습니다."));
				         }
				       } 
				          pc.getInventory().removeItem(l1iteminstance, 1);
				          
			    //드래곤 포탈
				} else if (itemId == 555563) {
				           L1Npc l1npc = NpcTable.getInstance().getTemplate(777781);
				       if (pc.getdragonportal() == 0) {
				       int pc_castleId = L1CastleLocation.getCastleIdByArea(pc);
				       if (!(pc_castleId == 1 || pc_castleId == 2 || pc_castleId == 3 || pc_castleId == 4
				          || pc_castleId == 5 || pc_castleId == 6 || pc_castleId == 7 || pc_castleId == 8)) {
				    /* if (pc.getMap().isSafetyZone(pc.getLocation())) {
					       pc.sendPackets(new S_SystemMessage("마을안에서는 사용 할 수 없습니다.")); 
					       return;
					       } */
				       if (pc.getMapId() == 1005 && itemId == 555563) {  
						   pc.sendPackets(new S_SystemMessage("이 지역에서는 사용이 불가능합니다."));
						   return;
						   }
				    	   try {
				           String s34 = l1npc.getImpl();
				           Constructor constructor = Class.forName(
				           "l1j.server.server.model.Instance." + s34 + "Instance").getConstructors()[0];
				           L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(l1npc);
				           npc.setId(IdFactory.getInstance().nextId());
				           npc.setMap(pc.getMapId());
				           npc.setX(pc.getX()+ 1);
			               npc.setY(pc.getY()+ 1);
				           npc.setHeading(pc.getHeading());
				           L1World.getInstance().storeObject(npc);
				           L1World.getInstance().addVisibleObject(npc);
				           pc.sendPackets(new S_SystemMessage("드래곤 포탈을 소환했습니다."));
				           pc.save();
				           L1NpcDeleteTimer timer = new L1NpcDeleteTimer(npc, 600000);
				           timer.begin();
				      } catch (Exception exception) {
				       } 
				     } else {
				           pc.sendPackets(new S_SystemMessage("공성존에서는 사용할수 없습니다."));
				           }
				          } 
				           pc.getInventory().removeItem(l1iteminstance, 1);
			 	        
				} else if (itemId == 40555) { // 비밀의 방의 키
					if (pc.isKnight()
							&& (pc.getX() >= 32806 && // 오림 방
							pc.getX() <= 32814)
							&& (pc.getY() >= 32798 && pc.getY() <= 32807)
							&& pc.getMapId() == 13) {
						short mapid = 13;
						L1Teleport.teleport(pc, 32815, 32810, mapid, 5, false);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40417) { // 서울 크리스탈
					if ((pc.getX() >= 32665 && // 해적섬
					pc.getX() <= 32674)
							&& (pc.getY() >= 32976 && pc.getY() <= 32985)
							&& pc.getMapId() == 440) {
						short mapid = 430;
						L1Teleport.teleport(pc, 32922, 32812, mapid, 5, true);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40566) { // 신비적인 쉘
					if (pc.isElf()
							&& (pc.getX() >= 33971 && // 상아의 탑의 마을의 남쪽에 있는 매직 스퀘어의 좌표
							pc.getX() <= 33975)
							&& (pc.getY() >= 32324 && pc.getY() <= 32328)
							&& pc.getMapId() == 4
							&& !pc.getInventory().checkItem(40548)) { // 망령의 봉투
						boolean found = false;
						for (L1Object obj : L1World.getInstance().getObject()) {
							if (obj instanceof L1MonsterInstance) {
								L1MonsterInstance mob = (L1MonsterInstance) obj;
								if (mob != null) {
									if (mob.getNpcTemplate().get_npcId() == 45300) {
										found = true;
										break;
									}
								}
							}
						}
						if (found) {
							pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						} else {
							L1SpawnUtil.spawn(pc, 45300, 0, 0); // 고대인의 망령
						}
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40557) { // 암살 리스트(그르딘)
					if (pc.getX() == 32620 && pc.getY() == 32641 && pc
							.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45883) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45883, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40563) { // 암살 리스트(화전마을)
					if (pc.getX() == 32730 && pc.getY() == 32426
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45884) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45884, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40561) { // 암살 리스트(켄트)
					if (pc.getX() == 33046 && pc.getY() == 32806
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45885) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45885, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40560) { // 암살 리스트(우드 베크)
					if (pc.getX() == 32580 && pc.getY() == 33260
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45886) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45886, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40562) { // 암살 리스트(Heine)
					if (pc.getX() == 33447 && pc.getY() == 33476
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45887) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45887, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40559) { // 암살 리스트(에덴)
					if (pc.getX() == 34215 && pc.getY() == 33195
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45888) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45888, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40558) { // 암살 리스트(기란)
					if (pc.getX() == 33513 && pc.getY() == 32890
							&& pc.getMapId() == 4) {
						for (L1Object object : L1World.getInstance()
								.getObject()) {
							if (object instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) object;
								if (npc.getNpcTemplate().get_npcId() == 45889) {
									pc.sendPackets(new S_ServerMessage(79));
									return;
								}
							}
						}
						L1SpawnUtil.spawn(pc, 45889, 0, 300000);
					} else {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					}
				} else if (itemId == 40572) { // 아사신의 표
					     if (pc.getX() == 32778 && pc.getY() == 32738 && pc.getMapId() == 21) {
						    L1Teleport.teleport(pc, 32781, 32728, (short) 21, 5, true);
				} else if (pc.getX() == 32781 && pc.getY() == 32728 && pc.getMapId() == 21) {
						    L1Teleport.teleport(pc, 32778, 32738, (short) 21, 5, true);
				} else {
						    pc.sendPackets(new S_ServerMessage(79));
					        }
				} else if (itemId == 40006 || itemId == 40412
						    || itemId == 140006) { // 소막
					    if (pc.getMap().isUsePainwand()) {
						    S_AttackPacket s_attackPacket = new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand);
						    pc.sendPackets(s_attackPacket);
						    pc.broadcastPacket(s_attackPacket);
						int chargeCount = l1iteminstance.getChargeCount();
						if (chargeCount <= 0 && itemId != 40412) {
							// \f1 아무것도 일어나지 않았습니다.
							pc.sendPackets(new S_ServerMessage(79));
							return;
						    }
						if (pc.getMap().isSafetyZone(pc.getLocation())) {
							pc.sendPackets(new S_SystemMessage("마을안에서는 소나무 막대를 사용 할 수 없습니다.")); 
							return;
						    }
						int[] mobArray = { 45008, 45140, 45016, 45021, 45025,
								45033, 45099, 45147, 45123, 45130, 45046,
								45092, 45138, 45098, 45127, 45143, 45149,
								45171, 45040, 45155, 45192, 45173, 45213,
								45079, 45144 };
					    /* 고블린·호브코브린·코보르트·사슴·그렘린
						 * 인프·인프에르다·오우르베아·스케르톤아챠·스케르톤악스
						 * 비글·드워후워리아·오크스카우트·간지오크·로바오크
						 * 두다마라오크·아트바오크·네르가오크·베어·트롯그
						 * 래트 맨·라이칸스로프·가스트·노르·리자드만
						 *
						 * 45005, 45008, 45009, 45016, 45019, 45043, 45060,
						 * 45066, 45068, 45082, 45093, 45101, 45107, 45126,
						 * 45129, 45136, 45144, 45157, 45161, 45173, 45184,
						 * 45223 }; // 개구리, 고블린, 오크, 코보르드, // 오크
						 * 아챠, 울프, 슬라임, 좀비, // 후로팅아이, 오크 fighter, // 웨어
						 * 울프, 아리게이타, 스켈리턴, // 스토고렘, 스케르톤아챠, // 자이언트
						 * spider, 리자드만, 굴, // 스파르트이, 라이칸스로프, 드렛드스파이다, //
						 * 버그 베어
						 */
						int rnd = _random.nextInt(mobArray.length);
						L1SpawnUtil.spawn(pc, mobArray[rnd], 0, 300000);
						if (itemId == 40006 || itemId == 140006) {
							l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
							pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
						if (chargeCount <= 1) pc.getInventory().removeItem(l1iteminstance, 1);			
				} else {
							pc.getInventory().removeItem(l1iteminstance, 1);
						    }
				} else {
						    pc.sendPackets(new S_ServerMessage(79));
				     	    }
				} else if (itemId == 40007) { // 흑단막대
					        cancelAbsoluteBarrier(pc);
					    int chargeCount = l1iteminstance.getChargeCount();
					    if (chargeCount <= 0) {
					        pc.sendPackets(new S_ServerMessage(79));
					        return;
					        }
					    if(pc.isInvisble()){
						    pc.sendPackets(new S_ServerMessage(1003));
						    return;
					        }//흑단마을방지추가
				        if (pc.getMap().isSafetyZone(pc.getLocation())) {
				            pc.sendPackets(new S_SystemMessage("마을안에서는 흑단 막대를 사용 할 수 없습니다.")); 
				            return;
				            } 
					        L1Object target = L1World.getInstance().findObject(spellsc_objid);
					        pc.sendPackets(new S_UseAttackSkill(pc, spellsc_objid,
					        10, spellsc_x, spellsc_y, ActionCodes.ACTION_Wand));
					        pc.broadcastPacket(new S_UseAttackSkill(pc, spellsc_objid,
					        10, spellsc_x, spellsc_y, ActionCodes.ACTION_Wand));
					     
					    if (target != null) {
					        doWandAction(pc, target);
					        }
				            l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
				            pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
				        if (chargeCount <= 1) pc.getInventory().removeItem(l1iteminstance, 1);

				} else if (itemId == 40008 || itemId == 40410
						|| itemId == 140008) { // 메이프르원드
					    if (pc.getMapId() == 63 || pc.getMapId() == 552
							|| pc.getMapId() == 555 || pc.getMapId() == 557
							|| pc.getMapId() == 558
							|| pc.getMapId() == 779) { // 수중에서는 사용 불가
						    pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
				} else {					
					        pc.sendPackets(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand));
					        pc.broadcastPacket(new S_AttackPacket(pc, 0, ActionCodes.ACTION_Wand));
					    int chargeCount = l1iteminstance.getChargeCount();
						if (chargeCount <= 0 && itemId != 40410
							 || pc.getTempCharGfx() == 6034
							 || pc.getTempCharGfx() == 6035
                             || pc.isInvisble()) {
						// \f1 아무것도 일어나지 않았습니다.
						    pc.sendPackets(new S_ServerMessage(79));
						    return;
					        }
					L1Object target = L1World.getInstance()
							.findObject(spellsc_objid);
					    if (target != null) {
						    L1Character cha = (L1Character) target;
						    polyAction(pc, cha);
						    cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
						if (itemId == 40008 || itemId == 140008) {
							l1iteminstance.setChargeCount(l1iteminstance.getChargeCount() - 1);
							pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_CHARGE_COUNT);
						if (chargeCount <= 1) pc.getInventory().removeItem(l1iteminstance, 1);			
				} else {
							pc.getInventory().removeItem(l1iteminstance, 1);
						    }
				} else {
						    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
					        }
				          }

				} else if (itemId >= 40289 && itemId <= 40297) { // 오만의 탑11~91계테레포트아뮤렛트
					        useToiTeleportAmulet(pc, itemId, l1iteminstance);
				} else if (itemId >= 40280 && itemId <= 40288) {
					// 봉인된 오만의 탑 11~91층 테레포트아뮤렛트
					        pc.getInventory().removeItem(l1iteminstance, 1);
					        L1ItemInstance item = pc.getInventory().storeItem(itemId + 9, 1);
					    if (item != null) {
						    pc.sendPackets(new S_ServerMessage(403, item.getLogName()));
					        }
				// 육류
				} else if (itemId == 40056 || itemId == 40057
						|| itemId == 40059 || itemId == 40060
						|| itemId == 40061 || itemId == 40062
						|| itemId == 40063 || itemId == 40064
						|| itemId == 40065 || itemId == 40069
						|| itemId == 40072 || itemId == 40073
						|| itemId == 140061 || itemId == 140062
						|| itemId == 140065 || itemId == 140069
						|| itemId == 140072 || itemId == 41296
						|| itemId == 41297 || itemId == 41266
						|| itemId == 41267 || itemId == 41274
						|| itemId == 41275 || itemId == 41276
						|| itemId == 49040 || itemId == 555586
						|| itemId == 49041 || itemId == 49042
						|| itemId == 49043 || itemId == 49044
						|| itemId == 49045 || itemId == 49046
						|| itemId == 49047
						|| itemId >= 50003 && itemId <= 50010 //2차요리 재료
					    || itemId >= 50028 && itemId <= 50035 //3차요리 재료
					    || itemId == 50052) {
					    pc.getInventory().removeItem(l1iteminstance, 1);
					// XXX 음식 마다의 만복도가 차이가 나지 않는다
					if (pc.get_food() < 225) {
						pc.set_food(pc.get_food() + 10);
						if(pc.get_food() > 225){
							pc.set_food(225);
						}
						pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
					    } 					
					if (itemId == 40057) { // 괴물눈고기
						pc.setSkillEffect(STATUS_FLOATING_EYE, 0);
					    }
					    pc.sendPackets(new S_ServerMessage(76, l1iteminstance.getItem().getNameId()));
				} else if (itemId == 40070) { // 진화의 열매
					pc.sendPackets(new S_ServerMessage(76, l1iteminstance.getLogName()));
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41298) { // 어린 물고기
					UseHeallingPotion(pc, 4, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41299) { // 재빠른 물고기
					UseHeallingPotion(pc, 15, 194);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41300) { // 강한 물고기
					UseHeallingPotion(pc, 35, 197);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41301) { // 붉은 빛 나는 물고기
					int chance = _random.nextInt(10);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40019, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40045, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40049, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40053, 1);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);					
				} else if (itemId == 41302) { // 초록 빛 나는 물고기
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40018, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40047, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40051, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40055, 1);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);					
				} else if (itemId == 41303) { // 파란 빛 나는 물고기
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40015, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40046, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40050, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40054, 1);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);					
				} else if (itemId == 41304) { // 흰 빛 나는 물고기
					int chance = _random.nextInt(3);
					if (chance >= 0 && chance < 5) {
						UseHeallingPotion(pc, 15, 189);
					} else if (chance >= 5 && chance < 9) {
						createNewItem(pc, 40021, 1);
					} else if (chance >= 9) {
						int gemChance = _random.nextInt(3);
						if (gemChance == 0) {
							createNewItem(pc, 40044, 1);
						} else if (gemChance == 1) {
							createNewItem(pc, 40048, 1);
						} else if (gemChance == 2) {
							createNewItem(pc, 40052, 1);
						}
					}
					pc.getInventory().removeItem(l1iteminstance, 1);				
				} else if (itemId >= 40136 && itemId <= 40161) { // 불꽃
					int soundid = 3198;
					if (itemId == 40154) {
						soundid = 3198;
					} else if (itemId == 40152) {
						soundid = 2031;
					} else if (itemId == 40141) {
						soundid = 2028;
					} else if (itemId == 40160) {
						soundid = 2030;
					} else if (itemId == 40145) {
						soundid = 2029;
					} else if (itemId == 40159) {
						soundid = 2033;
					} else if (itemId == 40151) {
						soundid = 2032;
					} else if (itemId == 40161) {
						soundid = 2037;
					} else if (itemId == 40142) {
						soundid = 2036;
					} else if (itemId == 40146) {
						soundid = 2039;
					} else if (itemId == 40148) {
						soundid = 2043;
					} else if (itemId == 40143) {
						soundid = 2041;
					} else if (itemId == 40156) {
						soundid = 2042;
					} else if (itemId == 40139) {
						soundid = 2040;
					} else if (itemId == 40137) {
						soundid = 2047;
					} else if (itemId == 40136) {
						soundid = 2046;
					} else if (itemId == 40138) {
						soundid = 2048;
					} else if (itemId == 40140) {
						soundid = 2051;
					} else if (itemId == 40144) {
						soundid = 2053;
					} else if (itemId == 40147) {
						soundid = 2045;
					} else if (itemId == 40149) {
						soundid = 2034;
					} else if (itemId == 40150) {
						soundid = 2055;
					} else if (itemId == 40153) {
						soundid = 2038;
					} else if (itemId == 40155) {
						soundid = 2044;
					} else if (itemId == 40157) {
						soundid = 2035;
					} else if (itemId == 40158) {
						soundid = 2049;
					} else {
						soundid = 3198;
					}

					        S_SkillSound s_skillsound = new S_SkillSound(pc.getId(), soundid);
					        pc.sendPackets(s_skillsound);
					        pc.broadcastPacket(s_skillsound);
					        pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId >= 41357 && itemId <= 41382) { // 알파벳 불꽃
					   int soundid =itemId - 34946;
					        S_SkillSound s_skillsound = new S_SkillSound(pc.getId(), soundid);
					        pc.sendPackets(s_skillsound);
					        pc.broadcastPacket(s_skillsound);
					        pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 40615) { // 그림자의 신전 2층의 열쇠
					if ((pc.getX() >= 32701 && pc.getX() <= 32705)
							&& (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 522) { // 그림자의 신전 1F
						    L1Teleport.teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_locy(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_mapid(), 5, true);
				} else {
						// \f1 아무것도 일어나지 않았습니다.
						    pc.sendPackets(new S_ServerMessage(79));
					        }
				} else if (itemId == 40616 || itemId == 40782
						    || itemId == 40783) { // 그림자의 신전 3층의 열쇠
					   if ((pc.getX() >= 32698 && pc.getX() <= 32702)
							&& (pc.getY() >= 32894 && pc.getY() <= 32898)
							&& pc.getMapId() == 523) { // 그림자의 신전 2층
						    L1Teleport.teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_locy(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_mapid(), 5, true);
				} else {
						// \f1 아무것도 일어나지 않았습니다.
						   pc.sendPackets(new S_ServerMessage(79));
					       }
				} else if (itemId == 40692) { // 완성된 보물의 지도
					   if (pc.getInventory().checkItem(40621)) {
						// \f1 아무것도 일어나지 않았습니다.
						   pc.sendPackets(new S_ServerMessage(79));
				} else if ((pc.getX() >= 32856 && pc.getX() <= 32858)
							&& (pc.getY() >= 32857 && pc.getY() <= 32858)
							&& pc.getMapId() == 443) { // 해적섬의 지하 감옥 3층
						    L1Teleport.teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_locy(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_mapid(), 5, true);
				} else {
						// \f1 아무것도 일어나지 않았습니다.
						    pc.sendPackets(new S_ServerMessage(79));
					        }
				} else if (itemId == 41146) { // 드로몬드의 초대장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei001"));
				} else if (itemId == 40641) { // 토킹 스크롤
					if (Config.ALT_TALKINGSCROLLQUEST == true) {
						if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL) == 0) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolla"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 1) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollb"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 2) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollc"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 3) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolld"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 4) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolle"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 5) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollf"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 6) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollg"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 7) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollh"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 8) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolli"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 9) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollj"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 10) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollk"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 11) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolll"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 12) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollm"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 13) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrolln"));
						} else if (pc.getQuest(). get_step(L1Quest.QUEST_TOSCROLL)
								== 255) {
							pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"tscrollo"));
						}
					    } else {
						    pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
						    "tscrollp"));	
					    }
				} else if (itemId == 40383) { // 지도：노래하는 섬
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei035"));
				} else if (itemId == 40384) { // 지도：숨겨진 계곡
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei036"));
				/*} else if (itemId == 40101) { // 숨겨진 계곡 귀환 스크롤
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei037"));*/
				} else if (itemId == 41209) { // 포피레아의 의뢰서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei002"));
				} else if (itemId == 41210) { // 연마재
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei003"));
				} else if (itemId == 41211) { // 허브
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei004"));
				} else if (itemId == 41212) { // 특제 캔디
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei005"));
				} else if (itemId == 41213) { // 티미의 바스켓
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei006"));
				} else if (itemId == 41214) { // 운의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei012"));
				} else if (itemId == 41215) { // 지의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei010"));
				} else if (itemId == 41216) { // 력의 증거
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei011"));
				} else if (itemId == 41222) { // 마슈르
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei008"));
				} else if (itemId == 41223) { // 무기의 파편
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei007"));
				} else if (itemId == 41224) { // 배지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei009"));
				} else if (itemId == 41225) { // 케스킨의 발주서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei013"));
				} else if (itemId == 41226) { // 파고의 약
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei014"));
				} else if (itemId == 41227) { // 알렉스의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei033"));
				} else if (itemId == 41228) { // 율법박사의 부적
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei034"));
				} else if (itemId == 41229) { // 스켈리턴의 머리
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei025"));
				} else if (itemId == 41230) { // 지난에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei020"));
				} else if (itemId == 41231) { // 맛티에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei021"));
				} else if (itemId == 41233) { // 케이이에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei019"));
				} else if (itemId == 41234) { // 뼈가 들어온 봉투
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei023"));
				} else if (itemId == 41235) { // 재료표
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei024"));
				} else if (itemId == 41236) { // 본아챠의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei026"));
				} else if (itemId == 41237) { // 스켈리턴 스파이크의 뼈
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei027"));
				} else if (itemId == 41239) { // 브트에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei018"));
				} else if (itemId == 41240) { // 페다에의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "ei022"));
				} else if (itemId == 41060) { // 노나메의 추천서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "nonames"));
				} else if (itemId == 41061) { // 조사단의 증서：에르프 지역 두다마라카메
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kames"));
				} else if (itemId == 41062) { // 조사단의 증서：인간 지역 네르가바크모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bakumos"));
				} else if (itemId == 41063) { // 조사단의 증서：정령 지역 두다마라브카
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "bukas"));
				} else if (itemId == 41064) { // 조사단의 증서：오크 지역 네르가후우모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "huwoomos"));
				} else if (itemId == 41065) { // 조사단의 증서：조사단장 아트바노아
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "noas"));
				} else if (itemId == 41356) { // 파룸의 자원 리스트
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rparum3"));
				} else if (itemId == 40701) { // 작은 보물의 지도
					if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1) == 1) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"firsttmap"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 2) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapa"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 3) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapb"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 4) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"secondtmapc"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 5) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapd"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 6) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmape"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 7) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapf"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 8) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapg"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 9) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmaph"));
					} else if (pc.getQuest().get_step(L1Quest.QUEST_LUKEIN1)
							== 10) {
						pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
								"thirdtmapi"));
					}
				} else if (itemId == 40663) { // 아들의 편지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"sonsletter"));
				} else if (itemId == 40630) { // 디에고의 낡은 일기
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"diegodiary"));
				} else if (itemId == 41340) { // 용병 단장 티온의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tion"));
				} else if (itemId == 41317) { // 랄슨의 추천장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "rarson"));
				} else if (itemId == 41318) { // 쿠엔의 메모
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "kuen"));
				} else if (itemId == 41329) { // 박제의 제작 의뢰서
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"anirequest"));
				} else if (itemId == 41346) { // 로빈훗드의 메모 1
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"robinscroll"));
				} else if (itemId == 41347) { // 로빈훗드의 메모 2
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"robinscroll2"));
				} else if (itemId == 41348) { // 로빈훗드의 소개장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"robinhood"));
				} else if (itemId == 41007) { // 이리스의 명령서：영혼의 안식
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"erisscroll"));
				} else if (itemId == 41009) { // 이리스의 명령서：동맹의 의지
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"erisscroll2"));
				} else if (itemId == 41019) { //라스타바드 역사서 1장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory1"));
				} else if (itemId == 41020) { //라스타바드 역사서 2장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory2"));
				} else if (itemId == 41021) { //라스타바드 역사서 3장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory3"));
				} else if (itemId == 41022) { //라스타바드 역사서 4장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory4"));
				} else if (itemId == 41023) { //라스타바드 역사서 5장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory5"));
				} else if (itemId == 41024) { //라스타바드 역사서 6장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory6"));
				} else if (itemId == 41025) { //라스타바드 역사서 7장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory7"));
				} else if (itemId == 41026) { //라스타바드 역사서 8장
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(),
							"lashistory8"));
				} else if (itemId == 41208) { // 져 가는 영혼
					   if ((pc.getX() >= 32844 && pc.getX() <= 32845)
							&& (pc.getY() >= 32693 && pc.getY() <= 32694)
							&& pc.getMapId() == 550) { // 배의 묘지:지상층
						    L1Teleport.teleport(pc, ((L1EtcItem) l1iteminstance.getItem()).get_locx(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_locy(),
							((L1EtcItem) l1iteminstance.getItem())
							.get_mapid(), 5, true);
				} else {
						// \f1 아무것도 일어나지 않았습니다.
						     pc.sendPackets(new S_ServerMessage(79));
					         }
				} else if (itemId == 40700) { // 실버 플룻
					pc.sendPackets(new S_Sound(10));
					pc.broadcastPacket(new S_Sound(10));
					if ((pc.getX() >= 32619 && pc.getX() <= 32623)
							&& (pc.getY() >= 33120 && pc.getY() <= 33124)
							&& pc.getMapId() == 440){ // 해적 시마마에반매직 스퀘어 좌표
						boolean found = false;
					for (L1Object obj : L1World.getInstance().getObject()) {
					if (obj instanceof L1MonsterInstance) {
						L1MonsterInstance mob = (L1MonsterInstance)
						obj;
					if (mob != null) {
					if (mob.getNpcTemplate().get_npcId()
						== 45875) {
						found = true;
								break;
								}
								}
							}
						}
						if (found) {
				} else {
							L1SpawnUtil.spawn(pc, 45875, 0, 0); // 러버 얼간이
						   }
					       }
				} else if (itemId == 41121) { // 카헬의 계약서
					   if (pc.getQuest(). get_step(L1Quest.QUEST_SHADOWS)
							== L1Quest.QUEST_END
							|| pc.getInventory(). checkItem(41122, 1)) {
						   pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다. 				
				} else {
						   createNewItem(pc, 41122, 1);
					       }
				} else if (itemId == 41130) { // 핏자국의 계약서
					   if (pc.getQuest(). get_step(L1Quest.QUEST_DESIRE)
							== L1Quest.QUEST_END
							|| pc.getInventory(). checkItem(41131, 1)) {
						    pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				} else {
						    createNewItem(pc, 41131, 1);
					        }
				} else if (itemId == 42501) { // 스톰 워크
					    if (pc.getCurrentMp() < 10) {
						    pc.sendPackets(new S_ServerMessage(278)); // \f1MP가 부족해 마법을 사용할 수 있지 않습니다.
						    return;
					        }
					        pc.setCurrentMp(pc.getCurrentMp() - 10);
                         // pc.sendPackets(new S_CantMove()); // 텔레포트 후에 이동 불가능하게 되는 경우가 있다
					        L1Teleport.teleport(pc, spellsc_x, spellsc_y,
							pc.getMapId(), pc.getHeading(), true,
							L1Teleport.CHANGE_POSITION);
				} else if (itemId == 41293 || itemId == 41294) { // 낚싯대
					        startFishing(pc, itemId, fishX, fishY);
				} else if (itemId == 41245) { // 용해제
					   if (l1iteminstance1.getLockitem() > 100){
                            pc.sendPackets(new S_SystemMessage("봉인된 아이템은 용해할 수 없습니다."));
                            return;
                } else {
					        useResolvent(pc, l1iteminstance1, l1iteminstance);
				            }
					// 마법인형
				} else if (itemId == 41248 || itemId == 41249
						|| itemId == 41250 || itemId == 500006
						|| itemId == 500007 || itemId == 500008 // 2차 인형 추가 
						|| itemId == 500020 || itemId == 500021
						|| itemId == 500022 || itemId == 500023
						|| itemId == 500052 || itemId == 500053 
						|| itemId == 500054 || itemId == 500055 
						|| itemId == 500056 || itemId == 500057 
						|| itemId == 500058 || itemId == 500082 
						|| itemId == 500071) {   // 3차 인형 추가 에티
					useMagicDoll(pc, itemId, itemObjid);
					 // 요리의 책
				} else if (itemId >= 41255 && itemId <=41259) {
					if (cookStatus == 0) {
						pc.sendPackets(new S_PacketBox(S_PacketBox. COOK_WINDOW, (itemId - 41255)));
					} else {
						makeCooking(pc, cookNo);
					}
				} else if (itemId == 41260) { // 장작
					for (L1Object object : L1World.getInstance()
							.getVisibleObjects(pc, 3)) {
						if (object instanceof L1EffectInstance) {
						if (((L1NpcInstance) object).getNpcTemplate().get_npcId() == 81170) {
								// 벌써 주위에 모닥불이 있습니다.
						pc.sendPackets(new S_ServerMessage(1162));
								return;
							}
						}
					}
					int[] loc = new int[2];
					loc = pc.getFrontLoc();
					L1EffectSpawn.getInstance().spawnEffect(81170, 120000, loc[0], loc[1], pc.getMapId());
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId >= 41277 && itemId <= 41292
						|| itemId >= 50011 && itemId <= 50026
					    || itemId >= 50036 && itemId <= 50051
					    || itemId >= 49049 && itemId <= 49064
					    || itemId >= 49244 && itemId <= 49259) { // 요리
					L1Cooking.useCookingItem(pc, l1iteminstance);
				} else if (itemId == 51259 || itemId == 51260 || itemId == 51261) { //컬러풀 패키지 
	                   useCashScroll(pc, itemId); pc.getInventory().removeItem(l1iteminstance, 1);
     			} else if (itemId >= 41383 && itemId <= 41400) { // 가구
					useFurnitureItem(pc, itemId, itemObjid);
				} else if (itemId == 41401) { // 가구 제거 wand
					useFurnitureRemovalWand(pc, spellsc_objid, l1iteminstance);
				} else if (itemId == 41411) { // 은의 톨즈
					UseHeallingPotion(pc, 10, 189);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41345) { // 산성의 유액
					L1DamagePoison.doInfection(pc, pc, 3000, 5);
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41315) { // 성수
					if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						return;
					}
					if (pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
						pc.removeSkillEffect(STATUS_HOLY_MITHRIL_POWDER);
					}
					pc.setSkillEffect(STATUS_HOLY_WATER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1141));
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41316) { // 신성한 미스리르파우다
					if (pc.hasSkillEffect(STATUS_HOLY_WATER_OF_EVA)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						return;
					}
					if (pc.hasSkillEffect(STATUS_HOLY_WATER)) {
						pc.removeSkillEffect(STATUS_HOLY_WATER);
					}
					pc.setSkillEffect(STATUS_HOLY_MITHRIL_POWDER, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1142));
					pc.getInventory().removeItem(l1iteminstance, 1);
				} else if (itemId == 41354) { // 신성한 에바의 물
					if(pc.hasSkillEffect(STATUS_HOLY_WATER)
							|| pc.hasSkillEffect(STATUS_HOLY_MITHRIL_POWDER)) {
						pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
						return;
					}
					pc.setSkillEffect(STATUS_HOLY_WATER_OF_EVA, 900 * 1000);
					pc.sendPackets(new S_SkillSound(pc.getId(), 190));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
					pc.sendPackets(new S_ServerMessage(1140));
					pc.getInventory().removeItem(l1iteminstance, 1);
				    } else {
					int locX = ((L1EtcItem) l1iteminstance.getItem())
							.get_locx();
					int locY = ((L1EtcItem) l1iteminstance.getItem())
							.get_locy();
					short mapId = ((L1EtcItem) l1iteminstance.getItem())
							.get_mapid();
					    if (locX != 0 && locY != 0) { // 각종 텔레포트 스크롤
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.teleport(pc, locX, locY, mapId, pc.getHeading(), true);
							pc.getInventory().removeItem(l1iteminstance, 1);
					} else {
							pc.sendPackets(new S_ServerMessage(647));
						   }
						    cancelAbsoluteBarrier(pc); // 아브소르트바리아의 해제
					} else {
						if (l1iteminstance.getCount() < 1) { // 있을 수  없어?
							pc.sendPackets(new S_ServerMessage(329, l1iteminstance.getLogName())); // \f1%0을 가지고 있지 않습니다.
					} else {
							pc.sendPackets(new S_ServerMessage(74, l1iteminstance.getLogName())); // \f1%0은 사용할 수 없습니다.
						    }
					        }
				            }
				//** 아이템 딜레이타이머 추가 **//  by 도우너
			        } else {
			                pc.setItemdelay2( nowtime + delay_time ); 
			                }
			                }
			                }
			    if (l1iteminstance.getItem().getType2() == 1) { 
			    //** 아이템 딜레이타이머 추가 **//  by 도우너
		
			//  } else if (l1iteminstance.getItem().getType2() == 1) {
				// 종별：무기
				int min = l1iteminstance.getItem().getMinLevel();
				int max = l1iteminstance.getItem().getMaxLevel();
				if (min != 0 && min > pc.getLevel()) {
					// 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
					pc.sendPackets(new S_ServerMessage(318,
							String.valueOf(min)));
				} else if (max != 0 && max < pc.getLevel()) {
					// 이 아이템은%d레벨 이하만 사용할 수 있습니다.
					// S_ServerMessage에서는 인수가 표시되지 않는다
					if (max < 50) { 
						pc.sendPackets(new S_PacketBox(
								S_PacketBox.MSG_LEVEL_OVER, max));
					} else {
						pc.sendPackets(new S_SystemMessage("이 아이템은" + max + "레벨 이하만 사용할 수 있습니다."));
					}
				} else {
					if (pc.isGm() || pc.isCrown() && l1iteminstance.getItem().isUseRoyal()
							|| pc.isKnight()
							&& l1iteminstance.getItem().isUseKnight()
							|| pc.isElf()
							&& l1iteminstance.getItem().isUseElf()
							|| pc.isWizard()
							&& l1iteminstance.getItem().isUseMage()
							|| pc.isDarkelf()
							&& l1iteminstance.getItem().isUseDarkelf()
							|| pc.isDragonKnight()
							&& l1iteminstance.getItem().isUseDragonKnight()
							|| pc.isBlackWizard()
							&& l1iteminstance.getItem().isUseBlackwizard()) {
						UseWeapon(pc, l1iteminstance);
				} else {
						// \f1당신의 클래스에서는 이 아이템은 사용할 수 없습니다.
						pc.sendPackets(new S_ServerMessage(264));
					}
				}
			} else if (l1iteminstance.getItem().getType2() == 2) { // 종별：방어용 기구
				if (pc.isGm() || pc.isCrown() && l1iteminstance.getItem().isUseRoyal()
						      || pc.isKnight()
						      && l1iteminstance.getItem().isUseKnight() || pc.isElf()
						      && l1iteminstance.getItem().isUseElf() || pc.isWizard()
						      && l1iteminstance.getItem().isUseMage()
						      || pc.isDarkelf()
							  && l1iteminstance.getItem().isUseDarkelf()
							  || pc.isDragonKnight()
							  && l1iteminstance.getItem().isUseDragonKnight()
							  || pc.isBlackWizard()
							  && l1iteminstance.getItem().isUseBlackwizard()) {
					int min = ((L1Armor) l1iteminstance.getItem())
							.getMinLevel();
					int max = ((L1Armor) l1iteminstance.getItem())
							.getMaxLevel();
					if (min != 0 && min > pc.getLevel()) {
						// 이 아이템은%0레벨 이상이 되지 않으면 사용할 수 없습니다.
						pc.sendPackets(new S_ServerMessage(318, String
								.valueOf(min)));
					} else if (max != 0 && max < pc.getLevel()) {
						// 이 아이템은%d레벨 이하만 사용할 수 있습니다.
						// S_ServerMessage에서는 인수가 표시되지 않는다
						if (max < 50) { 
							pc.sendPackets(new S_PacketBox(
									S_PacketBox.MSG_LEVEL_OVER, max));
						} else {
							pc.sendPackets(new S_SystemMessage("이 아이템은" + max + "레벨 이하만 사용할 수 있습니다."));
						}
					    } else {
						UseArmor(pc, l1iteminstance);
					    }
				        } else {
					// \f1당신의 클래스에서는 이 아이템은 사용할 수 없습니다.
					    pc.sendPackets(new S_ServerMessage(264));
				        }
			            }

			// 효과 지연이 있는 경우는 현재 시간을 세트
			if (isDelayEffect) {
				Timestamp ts = new Timestamp(System.currentTimeMillis());
				l1iteminstance.setLastUsed(ts);
				pc.getInventory().updateItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
				pc.getInventory().saveItem(l1iteminstance, L1PcInventory.COL_DELAY_EFFECT);
			    }
   //			L1ItemDelay.onItemUse(client, l1iteminstance);//** 아이템 딜레이타이머 추가로 주석 **
		        }
	            }
	private void AttrEnchant(L1PcInstance pc, L1ItemInstance item, int item_id) {
		  int attr_level = item.getAttrEnchantLevel();
		  int chance = _random.nextInt(100) + 1;
		  if (item_id == 350002) { // 불의 무기 강화 주문서
		   if (attr_level == 0) {
		    if (chance < 20) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(1);
		    }else{
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 1) {
		    if (chance < 10) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(2);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 2) {
		    if (chance < 5) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(3);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 3) {
		    return;
		   }
		  } else if (item_id == 350003) { // 물의 무기 강화 주문서
		   if (attr_level == 0) {
		    if (chance < 20) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(4);
		    }else{
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 4) {
		    if (chance < 10) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(5);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 5) {
		    if (chance < 5) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(6);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 6) {
		    return;
		   }
		  } else if (item_id == 350000) { // 바람의 무기 강화 주문서
		   if (attr_level == 0) {
		    if (chance < 20) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(7);
		    }else{
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 7) {
		    if (chance < 10) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(8);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 8) {
		    if (chance < 5) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(9);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 9) {
		    return;
		   }
		  } else if (item_id == 350001) { // 땅의 무기 강화 주문서
		   if (attr_level == 0) {
		    if (chance < 20) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(10);
		    }else{
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 10) {
		    if (chance < 10) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(11);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 11) {
		    if (chance < 5) {
		     pc.sendPackets(new S_ServerMessage(1410, item.getLogName()));
		     item.setAttrEnchantLevel(12);
		    } else {
		     pc.sendPackets(new S_ServerMessage(1411, item.getLogName()));
		    }
		   } else if (attr_level == 12) {
		    return;
		   }
		  }
		  pc.getInventory().updateItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		  pc.getInventory().saveItem(item, L1PcInventory.COL_ATTRENCHANTLVL);
		 }  
	
	private void SuccessEnchant(L1PcInstance pc, L1ItemInstance item, ClientThread client, int i) {
		String s = "";
		String sa = "";
		String sb = "";
		String s1 = item.getName();
		String pm = "";
		
		if (item.getEnchantLevel() > 0) {
			pm = "+";
		}
		if (item.getItem().getType2() == 1) {
			if(i > 3 || i < -1) { // ########## 인챈트 버그 수정 ##########
				return; // #####
			} // #####
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				switch (i) {
				case -1:
					s = s1;
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = s1;
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = s1;
					sa = "$245";
					sb = "$248";
					break;
				}
			} else {
				switch (i) {
				case -1:
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$247";
					break;

				case 2: // '\002'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$248";
					break;

				case 3: // '\003'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$245";
					sb = "$248";
					break;
				}
			}
		} else if (item.getItem().getType2() == 2) {
			if(i > 3 || i < -1) { // ########## 인챈트 버그 수정 ##########
				return; // #####
			} // #####
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				switch (i) {
				case -1:
					s = s1;
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = s1;
					sa = "$252";
					sb = "$247 ";
					break;

				case 2: // '\002'
					s = s1;
					sa = "$252";
					sb = "$248 ";
					break;

				case 3: // '\003'
					s = s1;
					sa = "$252";
					sb = "$248 ";
					break;
				}
			} else {
				switch (i) {
				case -1:
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$246";
					sb = "$247";
					break;

				case 1: // '\001'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$252";
					sb = "$247 ";
					break;

				case 2: // '\002'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$252";
					sb = "$248 ";
					break;

				case 3: // '\003'
					s = (new StringBuilder()).append(
							pm + item.getEnchantLevel()).append(" ").append(s1)
							.toString(); // \f1%0이%2%1 빛납니다.
					sa = "$252";
					sb = "$248 ";
					break;
				   }
			       }
		           }
                pc.sendPackets(new S_ServerMessage(161, s, sa, sb));
		    int oldEnchantLvl = item.getEnchantLevel();
		    int newEnchantLvl = item.getEnchantLevel() + i;
		    int safe_enchant = item.getItem().get_safeenchant();
		        item.setEnchantLevel(newEnchantLvl);
		        client.getActiveChar().getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
		    if (newEnchantLvl > safe_enchant) {
			    client.getActiveChar().getInventory().saveItem(item, L1PcInventory.COL_ENCHANTLVL);
		        }
		    if (item.getItem().getType2() == 1
				&& Config.LOGGING_WEAPON_ENCHANT != 0) {
			if (safe_enchant == 0
					|| newEnchantLvl >= Config.LOGGING_WEAPON_ENCHANT) {
				LogEnchantTable logenchant = new LogEnchantTable();
				logenchant.storeLogEnchant(pc.getId(), item.getId(), oldEnchantLvl, newEnchantLvl);
			    }
		        }
		    if (item.getItem().getType2() == 2
				&& Config.LOGGING_ARMOR_ENCHANT != 0) {
			if (safe_enchant == 0
					|| newEnchantLvl >= Config.LOGGING_ARMOR_ENCHANT) {
				LogEnchantTable logenchant = new LogEnchantTable();
				logenchant.storeLogEnchant(pc.getId(), item.getId(), oldEnchantLvl, newEnchantLvl);
			    }
		        }

		    if (item.getItem().getType2() == 2) {
			    if (item.isEquipped()) {
				    pc.addAc(-i);
				int i2 = item.getItem().getItemId();
				if (i2 == 20011 || i2 == 20110 || i2 == 120011
						|| i2 == 20702 || i2 == 20706 
						|| i2 == 20710  || i2 == 20714) { // 마투,마사
					pc.addMr(i);
					pc.sendPackets(new S_SPMR(pc));
				    }
				if (i2 == 20056 || i2 == 120056 || i2 == 220056) { // 마법망토
					pc.addMr(i * 2);
					pc.sendPackets(new S_SPMR(pc));
				    }
				if (i2 == 20078 || i2 == 20744) { // 혼돈,서니망토
					pc.addMr(i * 3);
					pc.sendPackets(new S_SPMR(pc));
				    }
			        }
			        pc.sendPackets(new S_OwnCharStatus(pc));
		            }
	              }

	private void FailureEnchant(L1PcInstance pc, L1ItemInstance item, ClientThread client) {
		        String s = "";
		        String sa = "";
		    int itemType = item.getItem().getType2();
		        String nameId = item.getName();
		        String pm = "";
		    if (itemType == 1) { // 무기
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId; // \f1%0이 강렬하게%1 빛난 뒤, 증발하고 있지 않게 됩니다.
				sa = "$245";
			} else {
				if (item.getEnchantLevel() > 0) {
					pm = "+";
				}
				s = (new StringBuilder()).append(pm + item.getEnchantLevel())
						.append(" ").append(nameId).toString(); // \f1%0이 강렬하게%1 빛난 뒤, 증발하고 있지 않게 됩니다.
				sa = "$245";
			}
		} else if (itemType == 2) { // 방어용 기구
			if (!item.isIdentified() || item.getEnchantLevel() == 0) {
				s = nameId; // \f1%0이 강렬하게%1 빛난 뒤, 증발하고 있지 않게 됩니다.
				sa = " $252";
		} else {
				if (item.getEnchantLevel() > 0) {
					pm = "+";
				}
				s = (new StringBuilder()).append(pm + item.getEnchantLevel())
						.append(" ").append(nameId).toString(); // \f1%0이 강렬하게%1 빛난 뒤, 증발하고 있지 않게 됩니다.
				sa = " $252";
			}
		    }
		    pc.sendPackets(new S_ServerMessage(164, s, sa));
		    pc.getInventory().removeItem(item, item.getCount());
	        }

	private int EnchantChance(L1ItemInstance l1iteminstance) {
		byte byte0 = 0;
		int i = l1iteminstance.getEnchantLevel();
		if (l1iteminstance.getItem().getType2() == 1) {
			switch (i) {
			case 0: // '\0'
				byte0 = 50;
				break;

			case 1: // '\001'
				byte0 = 33;
				break;

			case 2: // '\002'
				byte0 = 25;
				break;

			case 3: // '\003'
				byte0 = 25;
				break;

			case 4: // '\004'
				byte0 = 25;
				break;

			case 5: // '\005'
				byte0 = 20;
				break;

			case 6: // '\006'
				byte0 = 33;
				break;

			case 7: // '\007'
				byte0 = 33;
				break;

			case 8: // '\b'
				byte0 = 33;
				break;

			case 9: // '\t'
				byte0 = 25;
				break;

			case 10: // '\n'
				byte0 = 20;
				break;
			}
		} else if (l1iteminstance.getItem().getType2() == 2) {
			switch (i) {
			case 0: // '\0'
				byte0 = 50;
				break;

			case 1: // '\001'
				byte0 = 33;
				break;

			case 2: // '\002'
				byte0 = 25;
				break;

			case 3: // '\003'
				byte0 = 25;
				break;

			case 4: // '\004'
				byte0 = 25;
				break;

			case 5: // '\005'
				byte0 = 20;
				break;

			case 6: // '\006'
				byte0 = 17;
				break;

			case 7: // '\007'
				byte0 = 14;
				break;

			case 8: // '\b'
				byte0 = 12;
				break;

			case 9: // '\t'
				byte0 = 11;
				break;
			 }
		    }
		    return byte0;
	        }

	private void UseHeallingPotion(L1PcInstance pc, int healHp, int gfxid) {
		if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		    cancelAbsoluteBarrier(pc);
		    pc.sendPackets(new S_SkillSound(pc.getId(), gfxid));
		int castle_id = L1CastleLocation.getCastleIdByArea(pc);//물약이펙 공성시본인만 보이게 
		if (castle_id == 0){
		    pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxid));
		    }
	  //	pc.sendPackets(new S_ServerMessage(77)); // \f1기분이 좋아졌습니다.
		    healHp *= (_random.nextGaussian() / 5.0D) + 1.0D;
		if (pc.hasSkillEffect(POLLUTE_WATER)) { // 포르트워타중은 회복량1/2배
			healHp /= 2;
		    }
		    pc.setCurrentHp(pc.getCurrentHp() + healHp);
	        }
		private void UseManaPotion(L1PcInstance pc, int healMp, int gfxid) {
		   if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
		    pc.sendPackets(new S_ServerMessage(698)); // 마력에 의해 아무것도 마실 수가 없습니다.
		    return;
		    }

		   // 아브소르트바리아의 해제
		   cancelAbsoluteBarrier(pc);
		   pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		   int castle_id = L1CastleLocation.getCastleIdByArea(pc); //공성시 본인만 물약이펙트 보이게
		   if (castle_id == 0){
		   pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
		   }
		   pc.sendPackets(new S_ServerMessage(338, "$1084")); // 당신의%0가 회복해 갈 것입니다.
		   pc.setCurrentMp(pc.getCurrentMp() + healMp + _random.nextInt(5));
		   }

	private void useGreenPotion(L1PcInstance pc, int itemId) {
		if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		cancelAbsoluteBarrier(pc);
		int time = 0;
		if (itemId == L1ItemId.POTION_OF_HASTE_SELF) { // 그린 일부
			time = 300;
		} else if (itemId == L1ItemId.B_POTION_OF_HASTE_SELF) { // 축복된 그린
			time = 350;
		} else if (itemId == 40018 || itemId == 41338 
				|| itemId == 41342 || itemId == 500084) { // 강화 그린 일부, 축복된 와인, 메듀사의 피,악마왕의 징표
			time = 1800;
		} else if (itemId == 140018) { // 축복된 강화 그린 일부
			time = 2100;
		} else if (itemId == 40039) { // 와인
			time = 600;	
                } else if (itemId == 555105) { // 촐기 부적
			time = 1800;	//<<<<시간은 마음대로 정하세여	
		} else if (itemId == 40040) { // 위스키
			time = 900;
		} else if (itemId == 40030) { // 상아의 탑의 헤이 파업 일부
			time = 300;
		} else if (itemId == 350015) { // 퀵포션의 헤이 파업 일부
			time = 200;
		} else if (itemId == 41261 || itemId == 41262 || itemId == 41268
				|| itemId == 41269 || itemId == 41271 || itemId == 41272
				|| itemId == 41273) {
			time = 30;
		}
		pc.sendPackets(new S_SkillSound(pc.getId(), 191));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 191));
		// XXX:헤이스트아이템 장비시, 취한 상태가 해제되는지 불명
		if (pc.getHasteItemEquipped() > 0) {
			return;
		    }
		// 취한 상태를 해제
		pc.setDrink(false);

		// 헤이 파업, 그레이터 헤이 파업과는 중복 하지 않는다
		if (pc.hasSkillEffect(HASTE)) {
			pc.killSkillEffectTimer(HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		} else if (pc.hasSkillEffect(GREATER_HASTE)) {
			pc.killSkillEffectTimer(GREATER_HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		} else if (pc.hasSkillEffect(STATUS_HASTE)) {
			pc.killSkillEffectTimer(STATUS_HASTE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			pc.setMoveSpeed(0);
		    }

		// 슬로우, 매스 슬로우, 엔탕르중은 슬로우 상태를 해제할 뿐
		if (pc.hasSkillEffect(SLOW)) { // 슬로우
			pc.killSkillEffectTimer(SLOW);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else if (pc.hasSkillEffect(MASS_SLOW)) { // 매스 슬로우
			pc.killSkillEffectTimer(MASS_SLOW);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else if (pc.hasSkillEffect(ENTANGLE)) { // 엔탕르
			pc.killSkillEffectTimer(ENTANGLE);
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		} else {
			pc.sendPackets(new S_SkillHaste(pc.getId(), 1, time));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
			pc.setMoveSpeed(1);
			pc.setSkillEffect(STATUS_HASTE, time * 1000);
		    }
	        }

	private void useBravePotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		cancelAbsoluteBarrier(pc);
		if (pc.getBraveItemEquipped() > 0) {//용기아이템  
			    return; 
			    } 
			    pc.setDrink(false);
		int time = 0;
		if (item_id == L1ItemId.POTION_OF_EMOTION_BRAVERY) { // 치우침 이브 일부
			time = 300;
		} else if (item_id == L1ItemId.B_POTION_OF_EMOTION_BRAVERY) { // 축복된 치우침 이브 일부
			time = 350;
		} else if (item_id == 41415) { // 강화 치우침 이브 일부
			time = 1800;
                } else if (item_id == 555106) { // 용기 부적
			time = 1800;  //<<<시간은 마음대로 정하세여
		} else if (item_id == 500024) { // 유그드라 열매
			time = 480;
			     pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			     pc.setBraveSpeed(0);
	/*	} else if (item_id == 500024) { // 유그드라 열매
			time = 480;
			if (pc.hasSkillEffect(STATUS_RIBRAVE)) {
				pc.killSkillEffectTimer(STATUS_RIBRAVE);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			} */
		} else if (item_id == 40068) { // 에르브왓훌
			time = 600;
			if (pc.hasSkillEffect(WIND_WALK)) { // 윈드워크와는 중복 하지 않는다
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (item_id == 140068) { // 축복된 에르브왓훌
			time = 700;
			if (pc.hasSkillEffect(WIND_WALK)) { // 윈드워크와는 중복 하지 않는다
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
		} else if (item_id == 40031) { // 이비르브랏드
			time = 600;
		} else if (item_id == 500041) { // 천상의 물약
			   time = 3600;
		} else if (item_id == 40733) { // 명예의 코인
			time = 600;
			if (pc.hasSkillEffect(HOLY_WALK)) { // 호-리 워크와는 중복 하지 않는다
				pc.killSkillEffectTimer(HOLY_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // 무빙 악 세레이션과는 중복 하지 않는다
				pc.killSkillEffectTimer(MOVING_ACCELERATION);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			}
			if (pc.hasSkillEffect(WIND_WALK)) { // 윈드워크와는 중복 하지 않는다
				pc.killSkillEffectTimer(WIND_WALK);
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
				pc.setBraveSpeed(0);
			    }
		      }
		
		if (item_id == 40068 || item_id == 140068 // 에르브왓훌
				|| item_id == 40733 && pc.isElf()) { // 에르프가 명예의 코인을 사용
			pc.sendPackets(new S_SkillBrave(pc.getId(), 3, time));   
			pc.broadcastPacket(new S_SkillBrave(pc.getId(), 3, 0));
	} else {
			pc.sendPackets(new S_SkillBrave(pc.getId(), 1, time));
		    pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
		    }
		    pc.sendPackets(new S_SkillSound(pc.getId(), 751));
		    pc.broadcastPacket(new S_SkillSound(pc.getId(), 751));
		    pc.setBraveSpeed(1);
		    pc.setSkillEffect(STATUS_BRAVE, time * 1000);
	        }
	
	private void UseExpPotion(L1PcInstance pc , int item_id) {
	    if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
	        pc.sendPackets(new S_ServerMessage( 698, "")); // \f1마력에 의해 아무것도 마실 수가 없습니다.
	        return;
	        }
	     // 아브소르트바리아의 해제
	        cancelAbsoluteBarrier(pc); 
	    int time = 0;
	    if (item_id == 500041) { // 천상의 물약
	        time = 3600; // 1시간
	        }
	    if (pc.hasSkillEffect(7013) == false) {     
	        pc.sendPackets(new S_SkillSound(pc.getId() , 7013));
	        pc.broadcastPacket(new S_SkillSound(pc.getId() , 7013));
	        pc.setSkillEffect(7013, time * 1000);
	        }
	        pc.sendPackets(new S_SystemMessage("경험치 획득량이 증가 합니다"));
	        }

	private void useBluePotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(DECAY_POTION)) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		cancelAbsoluteBarrier(pc);
		int time = 0;
		if (item_id == 40015 || item_id == 40736 || item_id == 500086 || item_id == 41142) { // 블루 일부, 지혜의 코인,그림리퍼의 징표
			time = 600;
	} else if (item_id == 140015) { // 축복된 블루 일부
			time = 700;
	} else {
			return;
		    }

		    pc.sendPackets(new S_SkillIconGFX(34, time));
		    pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		    pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
		    pc.setSkillEffect(STATUS_BLUE_POTION, time * 1000);
		    pc.sendPackets(new S_ServerMessage(1007)); // MP의 회복 속도가 빨라집니다.
	        }

	private void useWisdomPotion(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		    cancelAbsoluteBarrier(pc);
		int time = 0; // 시간은 4의 배수로 하는 것
		if (item_id == L1ItemId.POTION_OF_EMOTION_WISDOM) { // 위즈 댐 일부
			time = 300;
	} else if (item_id == L1ItemId.B_POTION_OF_EMOTION_WISDOM) { // 축복된 위즈 댐
			// 일부
			time = 360;
		    }

		if (!pc.hasSkillEffect(STATUS_WISDOM_POTION)) {
			pc.addSp(2);
		    }
		    pc.sendPackets(new S_SkillIconWisdomPotion((time / 4)));
		    pc.sendPackets(new S_SkillSound(pc.getId(), 750));
		    pc.broadcastPacket(new S_SkillSound(pc.getId(), 750));
		    pc.setSkillEffect(STATUS_WISDOM_POTION, time * 1000);
	        }

	private void useBlessOfEva(L1PcInstance pc, int item_id) {
		if (pc.hasSkillEffect(71) == true) { // 디케이포션 상태
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		   cancelAbsoluteBarrier(pc);
		   int time = 0;
		   if (item_id == 40032) { // 에바의 축복
			time = 1800;
	} else if (item_id == 40041) { // mermaid의 비늘
			time = 300;
	} else if (item_id == 41344) { // 수의 정수
			time = 2100;
	} else {
			return;
		    }

		if (pc.hasSkillEffect(STATUS_UNDERWATER_BREATH)) {
			int timeSec = pc.getSkillEffectTimeSec(STATUS_UNDERWATER_BREATH);
			time += timeSec;
			if (time > 3600) {
				time = 3600;
			}
		    }
		    pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), time));
		    pc.sendPackets(new S_SkillSound(pc.getId(), 190));
		    pc.broadcastPacket(new S_SkillSound(pc.getId(), 190));
		    pc.setSkillEffect(STATUS_UNDERWATER_BREATH, time * 1000);
	        }

	private void useBlindPotion(L1PcInstance pc) {
		if (pc.hasSkillEffect(DECAY_POTION)) {
			pc.sendPackets(new S_ServerMessage(698)); // \f1마력에 의해 아무것도 마실 수가 없습니다.
			return;
		    }

		// 아브소르트바리아의 해제
		cancelAbsoluteBarrier(pc);
		int time = 160;
		if (pc.hasSkillEffect(CURSE_BLIND)) {
			pc.killSkillEffectTimer(CURSE_BLIND);
	} else if (pc.hasSkillEffect(DARKNESS)) {
			pc.killSkillEffectTimer(DARKNESS);
		    }
		if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
			pc.sendPackets(new S_CurseBlind(2));
	} else {
			pc.sendPackets(new S_CurseBlind(1));
		    }
		    pc.setSkillEffect(CURSE_BLIND, time * 1000);
	        }
	private void useCashScroll(L1PcInstance pc, int item_id) {
		  int time = 3600;
		  int scroll = 0;

		  if (pc.hasSkillEffect(COLOR_A)) {
		   pc.killSkillEffectTimer(COLOR_A);
		   pc.addMaxHp(-50);
		   pc.addHpr(-4);
		   pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
		   if (pc.isInParty()) {
		    pc.getParty().updateMiniHP(pc);
		   }
		   pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		  }
		  if (pc.hasSkillEffect(COLOR_B)) {
		   pc.killSkillEffectTimer(COLOR_B);
		   pc.addMaxMp(-40);
		   pc.addMpr(-4);
		   pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
		  }
		  if (pc.hasSkillEffect(COLOR_C)) {
		   pc.killSkillEffectTimer(COLOR_C);
		   pc.addDmgup(-3);
		   pc.addHitup(-3);
		   pc.addBowHitRate(-3);
		   pc.addBowDmgup(-3);
		   pc.addSp(-3);
		   pc.sendPackets(new S_SPMR(pc));
		  }
		  if (item_id == 51259) {
		   scroll = 6993;
		   pc.addMaxHp(50);
		   pc.addHpr(4);
		   pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
		   if (pc.isInParty()) {
		    pc.getParty().updateMiniHP(pc);
		   }
		   pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
	 } else if (item_id == 51260) {
		   scroll = 6994;
		   pc.addMaxMp(40);
		   pc.addMpr(4);
		   pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
	 } else if (item_id == 51261) {
		   scroll = 6995;
		   pc.addDmgup(3);
		   pc.addHitup(3);
		   pc.addBowHitRate(3);
		   pc.addBowDmgup(3);
		   pc.addSp(3);
		   pc.sendPackets(new S_SPMR(pc));
		   }
		   pc.sendPackets(new S_SkillSound(pc.getId(), scroll));
		   pc.broadcastPacket(new S_SkillSound(pc.getId(), scroll));
		   pc.setSkillEffect(scroll, time * 1000); // 바꿔주세요 
		   }

	private boolean usePolyScroll(L1PcInstance pc, int item_id, String s) {
		int time = 0;
		if (item_id == 40088 || item_id == 40096) { // 변신 스크롤, 상아의 탑의 변신 스크롤
			time = 1800;
		} else if (item_id == 140088) { // 축복된 변신 스크롤
			time = 2100;
		       }
		       L1PolyMorph poly = PolyTable.getInstance().getTemplate(s);
		    if (poly != null || s.equals("")) {
			if (s.equals("")) {
				/*if (pc.getTempCharGfx() == 6034
						|| pc.getTempCharGfx() == 6035) {
					return true;
				} else {   */
				pc.removeSkillEffect(SHAPE_CHANGE);
				return true;
				//}
		} else if (poly.getMinLevel() <= pc.getLevel() || pc.isGm()) {
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.MORPH_BY_ITEMMAGIC); 
				return true;
		} else {
				return false;
			    }
		} else {
			    return false;
		        }
	          }
		private void useEventPolyScroll(L1PcInstance pc, int item_id, String ak) {
		int time = 0;
		if (item_id == 500213) { // 변신 스크롤, 상아의 탑의 변신 스크롤
			time = 1800;
		        }
				L1PolyMorph poly = PolyTable.getInstance().getTemplate(ak);
				L1PolyMorph.doPoly(pc, poly.getPolyId(), time, L1PolyMorph.ARCH_MORPH_BY_ITEMMAGIC); 	
	            }

	private void usePolyScale(L1PcInstance pc, int itemId) {
		int polyId = 0;
		if (itemId == 41154) {        // 어둠의 비늘
			polyId = 3101;
		} else if (itemId == 41155) { // 열화의 비늘
			polyId = 3126;
		} else if (itemId == 41156) { // 배덕자의 비늘
			polyId = 3888;
		} else if (itemId == 41157) { // 증오의 비늘
			polyId = 3784;
		}
		L1PolyMorph.doPoly(pc, polyId, 600, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	    }

	private void usePolyPotion(L1PcInstance pc, int itemId) {
		int polyId = 0;
		if (itemId == 41143) {
			polyId = 6086;
		} else if (itemId == 41144) {
			polyId = 6087;
		} else if (itemId == 41145) {
			polyId = 6088;
		}
		L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
	    }
	
	private void useLevelPolyScroll(L1PcInstance pc, int itemId) { // 샤르나의 변신주문서
		  int polyId = 0;
		  if (itemId == 500044) { // 30
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6822;
		    } else {
		     polyId = 6823;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6824;
		    } else {
		     polyId = 6825;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6826;
		    } else {
		     polyId = 6827;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6828;
		    } else {
		     polyId = 6829;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6830;
		    } else {
		     polyId = 6831;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7139;
		    } else {
		     polyId = 7140;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7141;
		    } else {
		     polyId = 7142;
		    }
		   }
		  } else if (itemId == 500045) { // 40
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6832;
		    } else {
		     polyId = 6833;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6834;
		    } else {
		     polyId = 6835;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6836;
		    } else {
		     polyId = 6837;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6838;
		    } else {
		     polyId = 6839;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6840;
		    } else {
		     polyId = 6841;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7143;
		    } else {
		     polyId = 7144;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7145;
		    } else {
		     polyId = 7146;
		    }
		   }
		  } else if (itemId == 500047) { // 52
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6842;
		    } else {
		     polyId = 6843;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6844;
		    } else {
		     polyId = 6845;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6846;
		    } else {
		     polyId = 6847;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6848;
		    } else {
		     polyId = 6849;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6850;
		    } else {
		     polyId = 6851;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7147;
		    } else {
		     polyId = 7148;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7149;
		    } else {
		     polyId = 7150;
		    }
		   }
		  } else if (itemId == 500048) { // 55
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6852;
		    } else {
		     polyId = 6853;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6854;
		    } else {
		     polyId = 6855;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6856;
		    } else {
		     polyId = 6857;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6858;
		    } else {
		     polyId = 6859;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6860;
		    } else {
		     polyId = 6861;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7151;
		    } else {
		     polyId = 7152;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7153;
		    } else {
		     polyId = 7154;
		    }
		   }
		  } else if (itemId == 500049) { // 60
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6862;
		    } else {
		     polyId = 6863;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6864;
		    } else {
		     polyId = 6865;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6866;
		    } else {
		     polyId = 6867;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6868;
		    } else {
		     polyId = 6869;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6870;
		    } else {
		     polyId = 6871;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7155;
		    } else {
		     polyId = 7156;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7157;
		    } else {
		     polyId = 7158;
		    }
		   }
		  } else if (itemId == 500050) { // 65
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6872;
		    } else {
		     polyId = 6873;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6874;
		    } else {
		     polyId = 6875;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6876;
		    } else {
		     polyId = 6877;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6878;
		    } else {
		     polyId = 6879;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6880;
		    } else {
		     polyId = 6881;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7159;
		    } else {
		     polyId = 7160;
		    }
		   } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7161;
		    } else {
		     polyId = 7162;
		    }
		   }
		  } else if (itemId == 500051) { // 70
		   if(pc.isCrown()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6882;
		    } else {
		     polyId = 6883;
		    }
		   } else if (pc.isKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6884;
		    } else {
		     polyId = 6885;
		    }
		   } else if (pc.isElf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6886;
		    } else {
		     polyId = 6887;
		    }
		   } else if (pc.isWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6888;
		    } else {
		     polyId = 6889;
		    }
		   } else if (pc.isDarkelf()) {
		    if (pc.get_sex() == 0) {
		     polyId = 6890;
		    } else {
		     polyId = 6891;
		    }
		   } else if (pc.isDragonKnight()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7163;
		    } else {
		     polyId = 7164;
		    }
	       } else if (pc.isBlackWizard()) {
		    if (pc.get_sex() == 0) {
		     polyId = 7165;
		    } else {
		     polyId = 7166;
		    }
		    }
		    }
		     L1PolyMorph.doPoly(pc, polyId, 1800, L1PolyMorph.MORPH_BY_ITEMMAGIC);
		     }

	private void UseArmor(L1PcInstance activeChar, L1ItemInstance armor) {
		   int itemid = armor.getItem().getItemId();
		   int type = armor.getItem().getType();
		       L1PcInventory pcInventory = activeChar.getInventory();
		    boolean equipeSpace; // 장비 하는 개소가 비어 있을까
		    if (type == 9) { // 링의 경우
			   equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 1;
	} else {
			   equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		    }

		    if (equipeSpace && !armor.isEquipped()) { // 사용한 방어용 기구를 장비 하고 있지 않아, 그 장비 개소가 비어 있는 경우(장착을 시도한다)
			int polyid = activeChar.getTempCharGfx();

			if (!L1PolyMorph.isEquipableArmor(polyid, type)) { // 그 변신에서는 장비 불가
				return;
			}

			// 가더가 아니라면 
			if (type == 7 && type != 13 && activeChar.getWeapon()  != null) { // 쉴드(shield)의 경우, 무기를 장비 하고 있으면(자) 양손 무기 체크
				if (activeChar.getWeapon().getItem().isTwohandedWeapon()) { // 양손 무기
					activeChar.sendPackets(new S_ServerMessage(129)); // \f1양손의 무기를 무장한 채로 쉴드(shield)를 착용할 수 없습니다.
					return;
				}
			}
			
			if (type == 7 && pcInventory.getTypeEquipped(2, 13) >= 1) { // 방패의 경우, 가더를 입지 않은가 확인
				activeChar.sendPackets(new S_SystemMessage("가더를 착용하고서는 방패를 쓸 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} else if (type == 13 && pcInventory.getTypeEquipped(2, 7) >= 1) { // 가더의 경우, 방패를 입지 않은가 확인
				activeChar.sendPackets(new S_SystemMessage("방패를 착용하고서는 가더를 쓸 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} 
			
			/*if (type == 1 && pcInventory.getTypeEquipped(2, 13) >= 1) { // 한손검의 경우, 가더를 입지 않은가 확인
				activeChar.sendPackets(new S_SystemMessage("가더를 착용하고서는 한손무기를 쓸 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} else */
			 if (type == 13 && pcInventory.getTypeEquipped(1, 1) >= 1) { // 가더의 경우, 방패를 입지 않은가 확인
				activeChar.sendPackets(new S_SystemMessage("한손무기를 착용하고서는 가더를 쓸 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} 
			 if (type == 13 && pcInventory.getTypeEquipped(1, 2) >= 1) { // 가더의 경우, 대거(단검)를 찼는지 확인
				activeChar.sendPackets(new S_SystemMessage("한손 무기를 착용 하고서는 가더를 착용할 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} 
		     if (type == 13 && pcInventory.getTypeEquipped(1, 7) >= 1) { // 가더의 경우, 한손 지팡이를 찼는지 확인
				activeChar.sendPackets(new S_SystemMessage("한손 무기를 착용 하고서는 가더를 착용할 수 없습니다.")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			}
			if (type == 3 && pcInventory.getTypeEquipped(2, 4) >= 1) { // 셔츠의 경우, 망토를 입지 않은가 확인
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$225")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} else if ((type == 3) && pcInventory.getTypeEquipped(2, 2) >= 1) { // 셔츠의 경우, 메일을 입지 않은가 확인
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$226")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			} else if ((type == 2) && pcInventory.getTypeEquipped(2, 4) >= 1) { // 메일의 경우, 망토를 입지 않은가 확인
				activeChar
						.sendPackets(new S_ServerMessage(126, "$226", "$225")); // \f1%1상에%0를 입을 수 없습니다.
				return;
			    }
			    cancelAbsoluteBarrier(activeChar); // 아브소르트바리아의 해제
			    pcInventory.setEquipped(armor, true);
			    
		} else if (armor.isEquipped()) { // 사용한 방어용 기구를 장비 하고 있었을 경우(탈착을 시도한다)
			if (armor.getItem().getBless() == 2) { // 저주해지고 있었을 경우
				activeChar.sendPackets(new S_ServerMessage(150)); // \f1 뗄 수가 없습니다.저주를 걸칠 수 있고 있는 것 같습니다.
				return;
			    }
			if (type == 3 && pcInventory.getTypeEquipped(2, 2) >= 1) { // 셔츠의 경우, 메일을 입지 않은가 확인
				activeChar.sendPackets(new S_ServerMessage(127)); // \f1그것은 벗을 수가 없습니다.
				return;
		} else if ((type == 2 || type == 3)
					&& pcInventory.getTypeEquipped(2, 4) >= 1) { // 셔츠와 메일의 경우, 망토를 입지 않은가 확인
				activeChar.sendPackets(new S_ServerMessage(127)); // \f1그것은 벗을 수가 없습니다.
				return;
			    }
			    pcInventory.setEquipped(armor, false);
		} else {
			    activeChar.sendPackets(new S_ServerMessage(124)); // \f1 벌써 무엇인가를 장비 하고 있습니다.
		        }
		        // 세트 장비용 HP, MP, MR갱신
		        activeChar.setCurrentHp(activeChar.getCurrentHp());
		        activeChar.setCurrentMp(activeChar.getCurrentMp());
		        activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
		        activeChar.sendPackets(new S_OwnCharStatus(activeChar));
		        activeChar.sendPackets(new S_SPMR(activeChar));
	            }

	private void UseWeapon(L1PcInstance activeChar, L1ItemInstance weapon) {
		        L1PcInventory pcInventory = activeChar.getInventory();
		    if (activeChar.getWeapon() == null
				|| !activeChar.getWeapon().equals(weapon)) { // 지정된 무기가 장비 하고 있는 무기와 다른 경우, 장비 할 수 있을까 확인
			int weapon_type = weapon.getItem().getType();
			int polyid = activeChar.getTempCharGfx();
			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type)) { // 그 변신에서는 장비 불가
				return;
			    }
			if (weapon.getItem().isTwohandedWeapon()
					&& pcInventory.getTypeEquipped(2, 7) >= 1) { // 양손 무기의 경우, 쉴드(shield) 장비의 확인
				activeChar.sendPackets(new S_ServerMessage(128)); // \f1쉴드(shield)를 장비 하고 있을 때는 양손으로 가지는 무기를 사용할 수 없습니다.
				return;
			    }
			if ((!weapon.getItem().isTwohandedWeapon())
					&& pcInventory.getTypeEquipped(2, 13) >= 1) { // 양손무기가 아닌 경우, 가더 장비의 확인
				activeChar.sendPackets(new S_SystemMessage("두손 무기만 사용이 가능합니다."));
				return;
			    }			
		        }
		        cancelAbsoluteBarrier(activeChar); // 아브소르트바리아의 해제

		    if (activeChar.getWeapon() != null) { // 이미 무엇인가를 장비 하고 있는 경우, 전의 장비를 뗀다
			if (activeChar.getWeapon().getItem().getBless() == 2) { // 저주해지고 있었을 경우
				activeChar.sendPackets(new S_ServerMessage(150)); // \f1 뗄 수가 없습니다.저주를 걸칠 수 있고 있는 것 같습니다.
				return;
			    }
			if (activeChar.getWeapon().equals(weapon)) {
				// 장비 교환은 아니고 제외할 뿐
				pcInventory.setEquipped(activeChar.getWeapon(), false, false, false);
				return;
			} else {
				pcInventory.setEquipped(activeChar.getWeapon(), false, false, true);
			    }
		        }

		    if (weapon.getItemId() == 200002) { // 저주해진 다이스다가
			    activeChar.sendPackets(new S_ServerMessage(149, weapon.getLogName())); // \f1%0이 손에 들러붙었습니다.
		        }
		        pcInventory.setEquipped(weapon, true, false, false);
	            }

	private int RandomELevel(L1ItemInstance item, int itemId) {
		if (itemId == L1ItemId.B_SCROLL_OF_ENCHANT_ARMOR
				|| itemId == L1ItemId.B_SCROLL_OF_ENCHANT_WEAPON
				|| itemId == 140129 || itemId == 140130) {
			if (item.getEnchantLevel() <= 2) {
				int j = _random.nextInt(100) + 1;
				if (j < 32) {
					return 1;
				} else if (j >= 33 && j <= 76) {
					return 2;
				} else if (j >= 77 && j <= 100) {
					return 3;
				}
			} else if (item.getEnchantLevel() >= 3
					&& item.getEnchantLevel() <= 5) {
				int j = _random.nextInt(100) + 1;
				if (j < 50) {
					return 2;
				} else {
					return 1;
				}
			    }
			    {
				return 1;
			    }
		        }
		        return 1;
	            }

	private void useSpellBook(L1PcInstance pc, L1ItemInstance item,
			int itemId) {
		int itemAttr = 0;
		int locAttr = 0 ; // 0:other 1:law 2:chaos
		boolean isLawful = true;
		int pcX = pc.getX();
		int pcY = pc.getY();
		int mapId = pc.getMapId();
		int level = pc.getLevel();
		if (itemId == 45000 || itemId == 45008 || itemId == 45018
				|| itemId == 45021 || itemId == 40171
				|| itemId == 40179 || itemId == 40180
				|| itemId == 40182 || itemId == 40194
				|| itemId == 40197 || itemId == 40202
				|| itemId == 40206 || itemId == 40213
				|| itemId == 40220 || itemId == 40222) {
			itemAttr = 1;
		}
		if (itemId == 45009 || itemId == 45010 || itemId == 45019
				|| itemId == 40172 || itemId == 40173
				|| itemId == 40178 || itemId == 40185
				|| itemId == 40186 || itemId == 40192
				|| itemId == 40196 || itemId == 40201
				|| itemId == 40204 || itemId == 40211
				|| itemId == 40221 || itemId == 40225) {
			itemAttr = 2;
		}
		// 로우후르텐풀
		if (pcX > 33116 && pcX < 33128 && pcY > 32930 && pcY < 32942
				&& mapId == 4
				|| pcX > 33135 && pcX < 33147 && pcY > 32235 && pcY < 32247
				&& mapId == 4
				|| pcX >= 32783 && pcX <= 32803 && pcY >= 32831 && pcY <= 32851
				&& mapId == 77) {
			locAttr = 1;
			isLawful = true;
		}
		// 카오틱텐풀
		if (pcX > 32880 && pcX < 32892 && pcY > 32646 && pcY < 32658
				&& mapId == 4
				|| pcX > 32662
				&& pcX < 32674 && pcY > 32297 && pcY < 32309
				&& mapId == 4) {
			locAttr = 2;
			isLawful = false;
		}
		if (pc.isGm()) {
			SpellBook(pc, item, isLawful);
		} else if ((itemAttr == locAttr || itemAttr == 0) && locAttr != 0) {
			if (pc.isKnight()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 50) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45000 && itemId <= 45007) {
					pc.sendPackets(new S_ServerMessage(312));
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (pc.isCrown() || pc.isDarkelf()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 10) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 20) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015
						|| itemId >= 45000 && itemId <= 45007) {
					pc.sendPackets(new S_ServerMessage(312)); // 레벨이 낮고 그 마법을 기억할 수가 없습니다.
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				}
			} else if (pc.isElf()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 8) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 16) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45016 && itemId <= 45022 && level >= 24) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40170 && itemId <= 40177 && level >= 32) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40178 && itemId <= 40185 && level >= 40) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40186 && itemId <= 40193 && level >= 48) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45000 && itemId <= 45022
						|| itemId >= 40170 && itemId <= 40193) {
					pc.sendPackets(new S_ServerMessage(312)); // 레벨이 낮고 그 마법을 기억할 수가 없습니다.
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
				}
			} else if (pc.isWizard()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 4) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 8) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45016 && itemId <= 45022 && level >= 12) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40170 && itemId <= 40177 && level >= 16) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40178 && itemId <= 40185 && level >= 20) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40186 && itemId <= 40193 && level >= 24) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40194 && itemId <= 40201 && level >= 28) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40202 && itemId <= 40209 && level >= 32) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40210 && itemId <= 40217 && level >= 36) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40218 && itemId <= 40225 && level >= 40) {
					SpellBook(pc, item, isLawful);
				} else {
					pc.sendPackets(new S_ServerMessage(312)); // 레벨이 낮고 그 마법을 기억할 수가 없습니다.
				}
			}
		} else if (itemAttr != locAttr && itemAttr != 0 && locAttr != 0) {
			// 잘못한 템플에서 읽었을 경우 불벼락이 떨어진다
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
			S_SkillSound effect = new S_SkillSound(pc.getId(), 10);
			pc.sendPackets(effect);
			pc.broadcastPacket(effect);
			// 데미지는 적당
			pc.setCurrentHp(Math.max(pc.getCurrentHp() - 45, 0));
			if (pc.getCurrentHp() <= 0) {
				pc.death(null);
			}
			pc.getInventory(). removeItem(item, 1);
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		}
	}

	private void useElfSpellBook(L1PcInstance pc, L1ItemInstance item,
			int itemId) {
		int level = pc.getLevel();
		if ((pc.isElf() || pc.isGm()) && isLearnElfMagic(pc)) {
			if (itemId >= 40232 && itemId <= 40234 && level >= 10) {
				SpellBook2(pc, item);
			} else if (itemId >= 40235 && itemId <= 40236 && level >= 20) {
				SpellBook2(pc, item);
			}
			if (itemId >= 40237 && itemId <= 40240 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40241 && itemId <= 40243 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40244 && itemId <= 40246 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40247 && itemId <= 40248 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40249 && itemId <= 40250 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40251 && itemId <= 40252 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40253 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40254 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId == 40255 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40256 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40257 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40258 && itemId <= 40259 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40260 && itemId <= 40261 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40262 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40263 && itemId <= 40264 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 41149 && itemId <= 41150 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 41151 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 41152 && itemId <= 41153 && level >= 50) {
				SpellBook2(pc, item);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // (원문:정령의 수정은 에르프만을 습득할 수 있습니다. )
		}
	}

	private boolean isLearnElfMagic(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int pcMapId = pc.getMapId();
		if (pcX >=32786 && pcX <= 32797 && pcY >= 32842 && pcY <= 32859
				&& pcMapId == 75 // 상아의 탑
				|| pc.getLocation(). isInScreen(new Point(33055,32336))
				&& pcMapId == 4) { // 마더 트리
			return true;
		}
		return false ;
	}

	private void SpellBook(L1PcInstance pc, L1ItemInstance item,
			boolean isLawful) {
		String s = "";
		int i = 0;
		int level1 = 0;
		int level2 = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int skillId = 1; skillId < 81; skillId++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
			String s1 = "마법서 (" + l1skills.getName() + ")";
			if (item.getItem().getName().equalsIgnoreCase(s1)) {
				int skillLevel = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (skillLevel) {
				case 1: // '\001'
					level1 = i7;
					break;

				case 2: // '\002'
					level2 = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int objid = pc.getId();
		pc
				.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, l1,
						i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
						k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(objid, isLawful ?  224
				: 231);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(objid, i, s, 0, 0);
		pc.getInventory().removeItem(item, 1);
	}

	private void SpellBook1(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 97; j6 < 112; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "흑정령의 수정 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));		
		S_SkillSound s_skillSound = new S_SkillSound(k6, 231);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook2(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 129; j6 <= 176; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "정령의 수정 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				if (!pc.isGm() && l1skills.getAttr() != 0
						&& pc.getElfAttr() != l1skills.getAttr()) {
					if (pc.getElfAttr() == 0 || pc.getElfAttr() == 1
							|| pc.getElfAttr() == 2 || pc.getElfAttr() == 4
							|| pc.getElfAttr() == 8) { // 속성치가 이상한 경우는 전속성을 기억할 수 있도록(듯이) 해 둔다
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook3(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 87; j6 <= 91; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = (new StringBuilder()).append("기술서 (").append(
					l1skills.getName()).append(")").toString();
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook4(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 113; j6 < 121; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "마법서 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}
	
	private void SpellBook5(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 181; j6 < 200; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "용기사의 서판(" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}
	
	private void SpellBook6(L1PcInstance pc, L1ItemInstance l1iteminstance,
			ClientThread clientthread) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 201; j6 < 224; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "기억의 수정(" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1: // '\001'
					j = i7;
					break;

				case 2: // '\002'
					k = i7;
					break;

				case 3: // '\003'
					l = i7;
					break;

				case 4: // '\004'
					i1 = i7;
					break;

				case 5: // '\005'
					j1 = i7;
					break;

				case 6: // '\006'
					k1 = i7;
					break;

				case 7: // '\007'
					l1 = i7;
					break;

				case 8: // '\b'
					i2 = i7;
					break;

				case 9: // '\t'
					j2 = i7;
					break;

				case 10: // '\n'
					k2 = i7;
					break;

				case 11: // '\013'
					l2 = i7;
					break;

				case 12: // '\f'
					i3 = i7;
					break;

				case 13: // '\r'
					j3 = i7;
					break;

				case 14: // '\016'
					k3 = i7;
					break;

				case 15: // '\017'
					l3 = i7;
					break;

				case 16: // '\020'
					i4 = i7;
					break;

				case 17: // '\021'
					j4 = i7;
					break;

				case 18: // '\022'
					k4 = i7;
					break;

				case 19: // '\023'
					l4 = i7;
					break;

				case 20: // '\024'
					i5 = i7;
					break;

				case 21: // '\025'
					j5 = i7;
					break;

				case 22: // '\026'
					k5 = i7;
					break;

				case 23: // '\027'
					l5 = i7;
					break;

				case 24: // '\030'
					i6 = i7;
					break;
					
				case 25: // 용기사 3단계 마법
				     dk3 = i7;
				     break;
				     
				case 26: // 환술사 1단계 마법
				     bw1 = i7;
				     break;
				     
				case 27: // 환술사 2단계 마법
				     bw2 = i7;
				     break;
				     
				case 28: // 환술사 3단계 마법
				     bw3 = i7;
				     break;
				     
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		pc.broadcastPacket(s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}
	private void SpellBook7(L1PcInstance pc, int grade, ClientThread client, boolean isLawful) { // 종합 마법책을 위한..
		  String s = "";
		  int i = 0;
		  int level1 = 0;
		  int level2 = 0;
		  int l = 0;
		  int i1 = 0;
		  int j1 = 0;
		  int k1 = 0;
		  int l1 = 0;
		  int i2 = 0;
		  int j2 = 0;
		  int k2 = 0;
		  int l2 = 0;
		  int i3 = 0;
		  int j3 = 0;
		  int k3 = 0;
		  int l3 = 0;
		  int i4 = 0;
		  int j4 = 0;
		  int k4 = 0;
		  int l4 = 0;
		  int i5 = 0;
		  int j5 = 0;
		  int k5 = 0;
		  int l5 = 0;
		  int i6 = 0;
		  int dk3 = 0;
		  int bw1 = 0;
		  int bw2 = 0;
		  int bw3 = 0;
		  int gs = 0; // 등급별로 마법을 넣을 시작번호
		  gs = (grade*8) - 7; // 단계가 1일경우 1, 2일경우 9 이런식..
		  for (int skillId = gs; skillId < gs+8; skillId++) {
		   L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		   String s1 = "마법서 (" + l1skills.getName() + ")";
		    int skillLevel = l1skills.getSkillLevel();
		    int i7 = l1skills.getId();
		    s = l1skills.getName();
		    i = l1skills.getSkillId();
		    switch (skillLevel) {
		    case 1: // '\001'
		     level1 = i7;
		     break;

		    case 2: // '\002'
		     level2 = i7;
		     break;

		    case 3: // '\003'
		     l = i7;
		     break;

		    case 4: // '\004'
		     i1 = i7;
		     break;

		    case 5: // '\005'
		     j1 = i7;
		     break;

		    case 6: // '\006'
		     k1 = i7;
		     break;

		    case 7: // '\007'
		     l1 = i7;
		     break;

		    case 8: // '\b'
		     i2 = i7;
		     break;

		    case 9: // '\t'
		     j2 = i7;
		     break;

		    case 10: // '\n'
		     k2 = i7;
		     break;

		    case 11: // '\013'
		     l2 = i7;
		     break;

		    case 12: // '\f'
		     i3 = i7;
		     break;

		    case 13: // '\r'
		     j3 = i7;
		     break;

		    case 14: // '\016'
		     k3 = i7;
		     break;

		    case 15: // '\017'
		     l3 = i7;
		     break;

		    case 16: // '\020'
		     i4 = i7;
		     break;

		    case 17: // '\021'
		     j4 = i7;
		     break;

		    case 18: // '\022'
		     k4 = i7;
		     break;

		    case 19: // '\023'
		     l4 = i7;
		     break;

		    case 20: // '\024'
		     i5 = i7;
		     break;

		    case 21: // '\025'
		     j5 = i7;
		     break;

		    case 22: // '\026'
		     k5 = i7;
		     break;

		    case 23: // '\027'
		     l5 = i7;
		     break;

		    case 24: // '\030'
		     i6 = i7;
		     break;
			case 25: // 용기사 3단계 마법
			     dk3 = i7;
			     break;
			     
			case 26: // 환술사 1단계 마법
			     bw1 = i7;
			     break;
			     
			case 27: // 환술사 2단계 마법
			     bw2 = i7;
			     break;
			     
			case 28: // 환술사 3단계 마법
			     bw3 = i7;
			     break;
		    }
		  

		  int objid = pc.getId();
		  //pc.sendPackets(new S_SystemMessage("\\fY마법의 기운이 몸속으로 스며듭니다."));  // 메세지 출력
		  pc.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, l1,
		    i2, j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5,
		    k5, l5, i6,dk3, bw1, bw2, bw3));
		  SkillsTable.getInstance().spellMastery(objid, i, s, 0, 0);
		 }
		 }
	private void doWandAction(L1PcInstance user, L1Object target) {
		if (user.getId() == target.getId()) {
			return; // 자기 자신에게 맞혔다
		}
		if (user.glanceCheck(target.getX(), target.getY()) == false) {
			return; // 직선상에 장애물이 있다
		}

		// XXX 적당한 데미지 계산, 요점 수정
		int dmg = (_random.nextInt(11) - 5) + user.getStr();
		dmg = Math.max(1, dmg);

		if (target instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) target;
			if (pc.getMap().isSafetyZone(pc.getLocation())
					|| user.checkNonPvP(user, pc)) { // 공격할 수 없는 존
				return;
			}
			if (pc.hasSkillEffect(50) == true 
					|| pc.hasSkillEffect(78) == true
					|| pc.hasSkillEffect(157) == true) { // 타겟이 아이스 랑스, 아브소르트, 바리아아스바인드 상태
				return;
			}
			if (pc.isInvisble()) { // 투망해제
			    pc.delInvis();
			   } 
			int newHp = pc.getCurrentHp() - dmg;
			if (newHp > 0) {
				pc.sendPackets(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Damage));
				pc.broadcastPacket(new S_AttackPacket(pc, 0,
						ActionCodes.ACTION_Damage));
				pc.setCurrentHp(newHp);
			} else if (newHp <= 0 && pc.isGm()) {
				pc.setCurrentHp(pc.getMaxHp());
			} else if (newHp <= 0 && !pc.isGm()) {
				pc.death(user);
			}
		} else if (target instanceof L1MonsterInstance) {
			L1MonsterInstance mob = (L1MonsterInstance) target;
			mob.broadcastPacket(new S_DoActionGFX(mob.getId(), 2));
			mob.receiveDamage(user, dmg);
		} else if (target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			npc.broadcastPacket(new S_DoActionGFX(npc.getId(),
					ActionCodes.ACTION_Damage));
		}
	}

	private void polyAction(L1PcInstance attacker, L1Character cha) {
		boolean isSameClan = false;
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getClanid() != 0 && attacker.getClanid() == pc.getClanid()) {
				isSameClan = true;
			}
		}
		if (attacker.getId() != cha.getId() && !isSameClan) { // 자신 의외로 다른 크란
			int probability = 3 * (attacker.getLevel() - cha.getLevel()) + 70
					- cha.getMr();
			int rnd = _random.nextInt(100) + 1;
			if (rnd > probability) {
				return;
			}
		}

		int[] polyArray = { 29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862,
				3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378,
				3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875,
				7285, 6776, 7342, 7260, 7351, 4199, 7593, 7566, 4168, 4171,
				7348, 6699, 7496, 7481, 7489, 4199, 7503, 7509, 7515, 7193,
				3876 };

		int pid = _random.nextInt(polyArray.length);
		int polyId = polyArray[pid];

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.getInventory().checkEquipped(20281)) {
				pc.sendPackets(new S_ShowPolyList(pc.getId()));
				pc.sendPackets(new S_ServerMessage(966)); // string-j.tbl:968행째
				// 마법의 힘에 의해 보호됩니다.
				// 변신때의 메세지는, 타인이 자신을 변신시켰을 때에 나오는 메세지와 레벨이 부족할 때에 나오는 메세지 이외는 없습니다.
			} else {
				L1Skills skillTemp = SkillsTable.getInstance().getTemplate(
						SHAPE_CHANGE);

				L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration(), L1PolyMorph.ARCH_MORPH_BY_ITEMMAGIC); 
				if (attacker.getId() != pc.getId()) {
					pc.sendPackets(new S_ServerMessage(241, attacker.getName())); // %0가 당신을 변신시켰습니다.
				}				L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC); 
				if (attacker.getId() != pc.getId()) {
					pc.sendPackets(new S_ServerMessage(241, attacker.getName())); // %0가 당신을 변신시켰습니다.
				}
			}
		}
	
		//////////// npc에게 단풍 사용 안되도록 주석////////////////
		/*else if (cha instanceof L1MonsterInstance) { 
			L1MonsterInstance mob = (L1MonsterInstance) cha;
			L1NpcInstance npc = (L1NpcInstance) cha; // 추가
			if (mob.getLevel() > 50 || npc.getLevel() > 0) { // npc 에게 단풍 사용 불가 
				int npcId = mob.getNpcTemplate().get_npcId();
				if (npcId != 45338 && npcId != 45370 && npcId != 45456 // 일부의 보스 몬스터에게 단풍막대 사용 못하도록
						&& npcId != 45464 && npcId != 45473 && npcId != 45488 
						&& npcId != 45497 && npcId != 45516 && npcId != 45529 
						&& npcId != 45458) {					
					L1Skills skillTemp = SkillsTable.getInstance().getTemplate(SHAPE_CHANGE);   
					L1PolyMorph.doPoly(mob, polyId, skillTemp.getBuffDuration(), L1PolyMorph.MORPH_BY_ITEMMAGIC); 
				} 
			}
		}*/
    ////////////npc에게 단풍 사용 안되도록 주석////////////////
	}

	private void cancelAbsoluteBarrier(L1PcInstance pc) { // 아브소르트바리아의 해제
		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startMpRegenerationByDoll();
		}
	}

	private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		item.setCount(count);
		if (item != null) {
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 가질 수  없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
			return true;
		} else {
			return false;
		}
	}
	 private boolean createNewItem(L1PcInstance pc, int item_id, int count, int enchant) {
  L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
  item.setCount(count);

  item.setEnchantLevel(enchant);
  if (item != null) {
   if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
    pc.getInventory().storeItem(item);
   } else { // 가질 수  없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
    L1World.getInstance().getInventory(pc.getX(), pc.getY(),
      pc.getMapId()).storeItem(item);
   }
   pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0를 손에 넣었습니다.
   return true;
  } else {
   return false;
  }
 }
	private void useToiTeleportAmulet(L1PcInstance pc, int itemId,
			L1ItemInstance item) {
		boolean isTeleport = false;
		if (itemId == 40289 || itemId == 40293) { // 11,51Famulet
			if (pc.getX() >= 32816 && pc.getX() <= 32821 && pc.getY() >= 32778
					&& pc.getY() <= 32783 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40290 || itemId == 40294) { // 21,61Famulet
			if (pc.getX() >= 32815 && pc.getX() <= 32820 && pc.getY() >= 32815
					&& pc.getY() <= 32820 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40291 || itemId == 40295) { // 31,71Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32778
					&& pc.getY() <= 32783 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40292 || itemId == 40296) { // 41,81Famulet
			if (pc.getX() >= 32779 && pc.getX() <= 32784 && pc.getY() >= 32815
					&& pc.getY() <= 32820 && pc.getMapId() == 101) {
				isTeleport = true;
			}
		} else if (itemId == 40297) { // 91Famulet
			if (pc.getX() >= 32706 && pc.getX() <= 32710 && pc.getY() >= 32909
					&& pc.getY() <= 32913 && pc.getMapId() == 190) {
				isTeleport = true;
			}
		}

		if (true) {
			L1Teleport.teleport(pc, item.getItem().get_locx(), item.getItem()
					.get_locy(), item.getItem().get_mapid(), 5, true);
		} else {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
		}
	}

	private boolean writeLetter(int itemId, L1PcInstance pc, int letterCode,
			String letterReceiver, byte[] letterText) {

		int newItemId = 0;
		if (itemId == 40310) {
			newItemId = 49016;
		} else if (itemId == 40730) {
			newItemId = 49020;
		} else if (itemId == 40731) {
			newItemId = 49022;
		} else if (itemId == 40732) {
			newItemId = 49024;
		}
		L1ItemInstance item = ItemTable.getInstance().createItem(newItemId);
		item.setCount(1);
		if (item == null) {
			return false;
		}

		if (sendLetter(pc, letterReceiver, item, true)) {
			saveLetter(item.getId(), letterCode, pc.getName(),
					letterReceiver, letterText);
		} else {
			return false;
		}
		return true;
	}

	private boolean writeClanLetter(int itemId, L1PcInstance pc, int letterCode,
			String letterReceiver, byte[] letterText) {
		L1Clan targetClan = null;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (clan.getClanName().toLowerCase().equals(letterReceiver
					.toLowerCase())) {
				targetClan = clan;
				break;
			}
		}
		if (targetClan == null) {
			pc.sendPackets(new S_ServerMessage(434)); // 수신자가 없습니다.
			return false;
		}

		String memberName[] = targetClan.getAllMembers();
		for (int i = 0; i < memberName.length; i++) {
			L1ItemInstance item = ItemTable.getInstance().createItem(49016);
			item.setCount(1);
			if (item == null) {
				return false;
			}
			if (sendLetter(pc, memberName[i], item, false)) {
				saveLetter(item.getId(), letterCode, pc.getName(),
						memberName[i], letterText);
			}
		}
		return true;
	}

	private boolean sendLetter(L1PcInstance pc, String name,
			L1ItemInstance item, boolean isFailureMessage) {
		L1PcInstance target = L1World.getInstance().getPlayer(name);
		if (target != null) {
			if (target.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
				target.getInventory().storeItem(item);
				target.sendPackets(new S_SkillSound(target.getId(), 1091));
				target.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
			} else {
				if (isFailureMessage) {
					// 상대의 아이템이 너무 무겁기 (위해)때문에, 더 이상 줄 수 없습니다.
					pc.sendPackets(new S_ServerMessage(942));
				}
				return false;
			}
		} else {
			if (CharacterTable.doesCharNameExist(name)) {
				try {
					int targetId = CharacterTable.getInstance()
							.restoreCharacter(name).getId();
					CharactersItemStorage storage = CharactersItemStorage
							.create();
					if (storage.getItemCount(targetId) < 180) {
						storage.storeItem(targetId, item);
					} else {
						if (isFailureMessage) {
							// 상대의 아이템이 너무 무겁기 (위해)때문에, 더 이상 줄 수 없습니다.
							pc.sendPackets(new S_ServerMessage(942));
						}
						return false;
					}
				} catch (Exception e) {
					_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
				}
			} else {
				if (isFailureMessage) {
					pc.sendPackets(new S_ServerMessage(109, name)); // %0라는 이름의 사람은 없습니다.
				}
				return false;
			}
		}
		return true;
	}

	private void saveLetter(int itemObjectId, int code, String sender,
			String receiver, byte[] text) {
		// 일자를 취득한다
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
		TimeZone tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		String date = sdf.format(Calendar.getInstance(tz).getTime());

		// subject와 content의 단락(0x00 0x00) 위치를 찾아낸다
		int spacePosition1 = 0;
		int spacePosition2 = 0;
		for (int i = 0; i < text.length; i += 2) {
			if (text[i] == 0 && text[i + 1] == 0) {
				if (spacePosition1 == 0) {
					spacePosition1 = i;
				} else if (spacePosition1 != 0 && spacePosition2 == 0) {
					spacePosition2 = i;
					break;
				}
			}
		}

		// letter 테이블에 기입한다
		int subjectLength = spacePosition1 + 2;
		int contentLength = spacePosition2 - spacePosition1;
		if (contentLength <= 0) {
			contentLength = 1;
		}
		byte[] subject = new byte[subjectLength];
		byte[] content = new byte[contentLength];
		System.arraycopy(text, 0, subject, 0, subjectLength);
		System.arraycopy(text, subjectLength, content, 0, contentLength);
		LetterTable.getInstance().writeLetter(itemObjectId, code, sender,
				receiver, date, 0, subject, content);
	}

	private boolean withdrawPet(L1PcInstance pc, int itemObjectId) {
		if (!pc.getMap().isTakePets()) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return false;
		}

		int petCost = 0;
		Object[] petList = pc.getPetList().values().toArray();
		for (Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				if (((L1PetInstance) pet).getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 애완동물
					return false;
				}
			}
			petCost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 군주
			charisma += 6;
		} else if (pc.isElf()) { // 에르프
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		}
		charisma -= petCost;
		int petCount = charisma / 6;
		if (petCount <= 0) {
			pc.sendPackets(new S_ServerMessage(489)); // 물러가려고 하는 애완동물이 너무 많습니다.
			return false;
		}

		L1Pet l1pet = PetTable.getInstance().getTemplate(itemObjectId);
		if (l1pet != null) {
			L1Npc npcTemp = NpcTable.getInstance().getTemplate(
					l1pet.get_npcid());
			L1PetInstance pet = new L1PetInstance(npcTemp, pc, l1pet);
			pet.setPetcost(6);
		}
		return true;
	}

	private void startFishing(L1PcInstance pc, int itemId, int fishX, int fishY) { //검색추가
		 if (pc.getMapId() != 5302 || fishX <= 32704 || fishX >= 32831
		  || fishY <= 32768 || fishY >= 32895) {
		  // 여기에 낚싯대를 던질 수 없습니다.
		  pc.sendPackets(new S_ServerMessage(1138));
		  return;
		  }

		int rodLength = 0;
		if (itemId == 41293) {
			rodLength = 5;
		} else if (itemId == 41294) {
			rodLength = 3;
		}
		if (pc.getMap().isFishingZone(fishX, fishY)) {
			if (pc.getMap().isFishingZone(fishX + 1, fishY)
					&& pc.getMap().isFishingZone(fishX - 1, fishY)
					&& pc.getMap().isFishingZone(fishX, fishY + 1)
					&& pc.getMap().isFishingZone(fishX, fishY - 1)) {
				if (fishX > pc.getX() + rodLength
						|| fishX < pc.getX() - rodLength) {
					// 여기에 낚싯대를 던질 수 없습니다.
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (fishY > pc.getY() + rodLength
						|| fishY < pc.getY() - rodLength) {
					// 여기에 낚싯대를 던질 수 없습니다.
					pc.sendPackets(new S_ServerMessage(1138));
				} else if (pc.getInventory().consumeItem(41295, 1)) { // 먹이
					pc.sendPackets(new S_Fishing(pc.getId(), ActionCodes
							.ACTION_Fishing, fishX, fishY));
					pc.broadcastPacket(new S_Fishing(pc.getId(), ActionCodes
							.ACTION_Fishing, fishX, fishY));
					pc.setFishing(true);
					long time = System.currentTimeMillis() + 10000 +
							_random.nextInt(5) * 1000;
					pc.setFishingTime(time);
					FishingTimeController.getInstance().addMember(pc);
				} else {
					// 낚시를 하기 위해서는 먹이가 필요합니다.
					pc.sendPackets(new S_ServerMessage(1137));
				}
			} else {
				// 여기에 낚싯대를 던질 수 없습니다.
				pc.sendPackets(new S_ServerMessage(1138));
			}
		} else {
			// 여기에 낚싯대를 던질 수 없습니다.
			pc.sendPackets(new S_ServerMessage(1138));
		}
	}

	private void useResolvent(L1PcInstance pc, L1ItemInstance item,
			L1ItemInstance resolvent) {
		if (item == null || resolvent == null) {
			pc.sendPackets(new S_ServerMessage(79)); // \f1 아무것도 일어나지 않았습니다.
			return;
		}
		if (item.getItem().getType2() == 1 || item.getItem().getType2() == 2) { // 무기·방어용 기구
			if (item.getEnchantLevel() != 0) { // 강화가 끝난 상태
				pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
				return;
			}
			if (item.isEquipped()) { // 장비중
				pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
				return;
			}
		}
		int crystalCount = ResolventTable.getInstance(). getCrystalCount(
				item.getItem().getItemId());
		if (crystalCount == 0) {
			pc.sendPackets(new S_ServerMessage(1161)); // 용해할 수 없습니다.
			return;
		}

		int rnd = _random.nextInt(100) + 1;  //용해 확율 부분 ?
		if (rnd >= 1 && rnd <= 20) {
			crystalCount = 0;
			pc.sendPackets(new S_ServerMessage(158, item.getName())); // \f1%0이 증발하고 있지 않게 되었습니다.
		} else if (rnd >= 21 && rnd <= 90) {
			crystalCount *= 1;
		} else if (rnd >= 91 && rnd <= 100) {
			crystalCount *= 1.5;
			pc.getInventory().storeItem(41246, (int) (crystalCount * 1.5));
		}
		if (crystalCount != 0) {
			L1ItemInstance crystal = ItemTable.getInstance().createItem(41246);
			crystal.setCount(crystalCount);
			if (pc.getInventory().checkAddItem(crystal, 1) == L1Inventory.OK) {
				pc.getInventory().storeItem(crystal);
				pc.sendPackets(new S_ServerMessage(403, crystal.getLogName())); // %0를 손에 넣었습니다.
			} else { // 가질 수  없는 경우는 지면에 떨어뜨리는 처리의 캔슬은 하지 않는다(부정 방지)
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						pc.getMapId()).storeItem(crystal);
			}
		}
		pc.getInventory().removeItem(item, 1);
		pc.getInventory().removeItem(resolvent, 1);
	}

	private void useMagicDoll(L1PcInstance pc, int itemId, int itemObjectId) {
		boolean isAppear = true;
		L1DollInstance doll = null;
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			doll = (L1DollInstance) dollObject;
			if (doll.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 마법인형
				isAppear = false;
				break;
			}
		}

		if (isAppear) {
			if (!pc.getInventory().checkItem(41246, 50)) {
			     pc.sendPackets(new S_ServerMessage(337, "$5240")); // \f1%0이 부족합니다.
			     return;
	      	}
			if (dollList.length >= Config.MAX_DOLL_COUNT) {
				// \f1 더 이상의 monster를 조종할 수 없습니다.
				pc.sendPackets(new S_ServerMessage(319));
				return;
			}
			int npcId = 0;
			int dollType = 0;
			if (itemId == 41248) {
				npcId = 80106;
				dollType = L1DollInstance.DOLLTYPE_BUGBEAR;
				pc.sendPackets(new S_ServerMessage(1143));
			} else if (itemId == 41249) {
				npcId = 80107;
				dollType = L1DollInstance.DOLLTYPE_SUCCUBUS;
				pc.sendPackets(new S_ServerMessage(1143));
			} else if (itemId == 41250) {
				npcId = 80108;
				dollType = L1DollInstance.DOLLTYPE_WAREWOLF;
				pc.sendPackets(new S_ServerMessage(1143));
			} else if (itemId == 500006) {   // 2차 인형 추가
			    npcId = 200013;
			    dollType = L1DollInstance.DOLLTYPE_ELDER;
			    pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500007) {
			    npcId = 200014;
			    dollType = L1DollInstance.DOLLTYPE_CRUST;
			    pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500008) {
			    npcId = 200015;
			    dollType = L1DollInstance.DOLLTYPE_STONE;
			    pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500020) { // 3차 인형 추가
		        npcId = 200023;
		        dollType = L1DollInstance.DOLLTYPE_RICH;
		        pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500022) {
		        npcId = 200024;
		        dollType = L1DollInstance.DOLLTYPE_COKA;
		        pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500021) {
		        npcId = 200025;
		        dollType = L1DollInstance.DOLLTYPE_HESUABI;
		        pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500023) {
		        npcId = 200026;
		        dollType = L1DollInstance.DOLLTYPE_SEADANCER; 
		        pc.sendPackets(new S_ServerMessage(1143));
		    } else if (itemId == 500052) {
		        npcId = 200048;
		        dollType = L1DollInstance.DOLLTYPE_ETTY; 
		        pc.sendPackets(new S_ServerMessage(1143));
			 } else if (itemId == 500053) {
		        npcId = 200077;
		        dollType = L1DollInstance.DOLLTYPE_HEAL; //공주
		        pc.sendPackets(new S_ServerMessage(1143));
			 } else if (itemId == 500082) {
			    npcId = 200078;
			    dollType = L1DollInstance.DOLLTYPE_SPATOY; //스파토이
			    pc.sendPackets(new S_ServerMessage(1143));
			 } else if (itemId == 500054) {
			    npcId = 200071;
			    dollType = L1DollInstance.DOLLTYPE_LAMIA;  // 라미아 인형 타입이름 추가
			    pc.sendPackets(new S_ServerMessage(1143));
			 } else if (itemId == 500071) {
				npcId = 200075;
				dollType = L1DollInstance.DOLLTYPE_LAMIA;  // 해골 인형 타입이름 추가
				pc.sendPackets(new S_ServerMessage(1143));
		     } else if (itemId == 500055) {
	            npcId = 200072;
	            dollType = L1DollInstance.DOLLTYPE_DRAGON1;  // 해츨링 인형 타입이름 추가
	            pc.sendPackets(new S_ServerMessage(1143));
	         } else if (itemId == 500056) {
                npcId = 200073;
                dollType = L1DollInstance.DOLLTYPE_DRAGON2;  // 해츨링 인형 타입이름 추가
                pc.sendPackets(new S_ServerMessage(1143));
	         } else if (itemId == 500057) {
		        npcId = 200074;
		        dollType = L1DollInstance.DOLLTYPE_DRAGON3;  // 하이 해츨링 인형 타입이름 추가
		        pc.sendPackets(new S_ServerMessage(1143));
		     } else if (itemId == 500058) {
	            npcId = 200076;
	            dollType = L1DollInstance.DOLLTYPE_DRAGON4;  // 하이 해츨링 인형 타입이름 추가
	            pc.sendPackets(new S_ServerMessage(1143));
	         }
			L1Npc template = NpcTable.getInstance().getTemplate(npcId);
			doll = new L1DollInstance(template, pc, dollType, itemObjectId);
			pc.sendPackets(new S_SkillSound(doll.getId(), 5935));
			pc.broadcastPacket(new S_SkillSound(doll.getId(), 5935));
			pc.sendPackets(new S_SkillIconGFX(56, 1800));
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.getInventory().consumeItem(41246, 50);
		} else {
			pc.sendPackets(new S_SkillSound(doll.getId(), 5936));
			pc.broadcastPacket(new S_SkillSound(doll.getId(), 5936));
			doll.deleteDoll();
			pc.sendPackets(new S_SkillIconGFX(56, 0));
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	private void makeCooking(L1PcInstance pc, int cookNo) {
		boolean isNearFire =  false;
		for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 3)) {
			if (obj instanceof L1EffectInstance) {
				L1EffectInstance effect = (L1EffectInstance) obj;
				if (effect.getGfxId() == 5943) {
					isNearFire = true;
					break;
				}
			}
		}
		if (!isNearFire) {
			pc.sendPackets(new S_ServerMessage(1160)); // 요리에는 모닥불이 필요합니다.
			return;
		}
		if (pc.getMaxWeight() <= pc.getInventory().getWeight()) {
			pc.sendPackets(new S_ServerMessage(1103)); // 아이템이 너무 무거워, 요리할 수 없습니다.
			return;
		}

		int chance = _random.nextInt(100) + 1;
		if (cookNo == 0) { // 괴물 눈 스테이크
			if (pc.getInventory(). checkItem(40057, 1)) {
				pc.getInventory(). consumeItem(40057, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41277, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41285, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 1) { // 곰고기 구이
			if (pc.getInventory(). checkItem(41275, 1)) {
				pc.getInventory(). consumeItem(41275, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41278, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41286, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 2) { // 씨호떡
			if (pc.getInventory(). checkItem(41263, 1)
					&& pc.getInventory(). checkItem(41265, 1)) {
				pc.getInventory(). consumeItem(41263, 1);
				pc.getInventory(). consumeItem(41265, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41279, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41287, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 3) { // 개미다리 치즈 구이
			if (pc.getInventory(). checkItem(41274, 1)
					&& pc.getInventory(). checkItem(41267, 1)) {
				pc.getInventory(). consumeItem(41274, 1);
				pc.getInventory(). consumeItem(41267, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41280, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41288, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 4) { // 과일 샐러드
			if (pc.getInventory(). checkItem(40062, 1)
					&& pc.getInventory(). checkItem(40069, 1)
					&& pc.getInventory(). checkItem(40064, 1)) {
				pc.getInventory(). consumeItem(40062, 1);
				pc.getInventory(). consumeItem(40069, 1);
				pc.getInventory(). consumeItem(40064, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41281, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41289, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 5) { // 과일 탕수육
			if (pc.getInventory(). checkItem(40056, 1)
					&& pc.getInventory(). checkItem(40060, 1)
					&& pc.getInventory(). checkItem(40061, 1)) {
				pc.getInventory(). consumeItem(40056, 1);
				pc.getInventory(). consumeItem(40060, 1);
				pc.getInventory(). consumeItem(40061, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41282, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41290, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 6) { // 맷돼지 꼬치구이
			if (pc.getInventory(). checkItem(41276, 1)) {
				pc.getInventory(). consumeItem(41276, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41283, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41291, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 7) { // 버섯 스프
			if (pc.getInventory(). checkItem(40499, 1)
					&& pc.getInventory(). checkItem(40060, 1)) {
				pc.getInventory(). consumeItem(40499, 1);
				pc.getInventory(). consumeItem(40060, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 41284, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 41292, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 8) { // 캐비어 카나페
			if (pc.getInventory().  checkItem(50003, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50003, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50011, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50019, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 9) { // 악어 스테이크
			if (pc.getInventory().  checkItem(50004, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50004, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50012, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50020, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 10) { // 터틀드래곤의 과자
			if (pc.getInventory().  checkItem(50005, 1)
					&& pc.getInventory().  checkItem(41265, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50005, 1);
				pc.getInventory().  consumeItem(41265, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50013, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 91 && chance <= 95) {
					createNewItem(pc, 50021, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 11) { // 키위패롯 구이
			if (pc.getInventory().  checkItem(50006, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50006, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50014, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50022, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 12) { // 스콜피온 구이
			if (pc.getInventory().  checkItem(50007, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50007, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50015, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50023, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 13) { // 일렉카듐 스튜
			if (pc.getInventory().  checkItem(50008, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50008, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50016, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50024, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 14) { // 거미다리 꼬치구이
			if (pc.getInventory().  checkItem(50009, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50009, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50017, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50025, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 15) { // 크랩살 스프
			if (pc.getInventory().  checkItem(50010, 1)
					&& pc.getInventory().  checkItem(40499, 1)
					&& pc.getInventory().  checkItem(50027, 1)) {
				pc.getInventory(). consumeItem(50010, 1);
				pc.getInventory(). consumeItem(40499, 1);
				pc.getInventory().  consumeItem(50027, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50018, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50026, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 16) { // 크러스트시안 집게발 구이
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50028, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50028, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50036, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50044, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 17) { // 그리폰 구이
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50029, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50029, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50037, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50045, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 18) { // 코카트리스 스테이크
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50030, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50030, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50038, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50046, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 19) { // 대왕거북 구이
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50031, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50031, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50039, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50047, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 20) { // 레서 드래곤 날개 꼬치
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50032, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50032, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50040, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50048, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 21) { // 드레이크 구이
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50033, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50033, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50041, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50049, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 22) { // 심해어 스튜
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(50034, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50034, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50042, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50050, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}
		} else if (cookNo == 23) { // 바실리스크 알 스프
			if (pc.getInventory(). checkItem(50052, 1)
					&& pc.getInventory(). checkItem(50035, 1)
					&& pc.getInventory(). checkItem(50027, 1)
					&& pc.getInventory(). checkItem(40499, 1)) {
				pc.getInventory(). consumeItem(50052, 1);
				pc.getInventory(). consumeItem(50027, 1);
				pc.getInventory(). consumeItem(50035, 1);
				pc.getInventory(). consumeItem(40499, 1);
				if (chance >= 1 && chance <= 85) {
					createNewItem(pc, 50043, 1);
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6392));
				} else if (chance >= 86 && chance <= 95) {
					createNewItem(pc, 50051, 1);
					pc.sendPackets(new S_SkillSound(pc.getId(), 6390));
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6390));
				} else if (chance >= 96 && chance <= 100) {
					pc.sendPackets(new S_ServerMessage(1101)); // 요리가 실패했습니다.
					pc.broadcastPacket(new S_SkillSound(pc.getId(), 6394));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(1102)); // 요리의 재료가 충분하지 않습니다.
			}	
		}
	}
	 
	private void useFurnitureItem(L1PcInstance pc, int itemId, int itemObjectId) {
		if (!L1HouseLocation.isInHouse(pc.getX(), pc.getY(), pc.getMapId())) {
			pc.sendPackets(new S_ServerMessage(563)); // \f1 여기에서는 사용할 수 없습니다.
			return;
		}

		boolean isAppear = true;
		L1FurnitureInstance furniture = null;
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1FurnitureInstance) {
				furniture = (L1FurnitureInstance) l1object;
				if (furniture.getItemObjId() == itemObjectId) { // 이미 꺼내고 있는 가구
					isAppear = false;
					break;
				}
			}
		}

		if (isAppear) {
			if (pc.getHeading() != 0 && pc.getHeading() != 2) {
				return;
			}
			int npcId = 0;
			if (itemId == 41383) { // 쟈이안트안트소르쟈의 박제
				npcId = 80109;
			} else if (itemId == 41384) { // 베어의 박제
				npcId = 80110;
			} else if (itemId == 41385) { // 라미아의 박제
				npcId = 80113;
			} else if (itemId == 41386) { // 브락크타이가의 박제
				npcId = 80114;
			} else if (itemId == 41387) { // 사슴의 박제
				npcId = 80115;
			} else if (itemId == 41388) { // 하피의 박제
				npcId = 80124;
			} else if (itemId == 41389) { // 브론즈 나이트
				npcId = 80118;
			} else if (itemId == 41390) { // 브론즈 호스
				npcId = 80119;
			} else if (itemId == 41391) { // 촛대
				npcId = 80120;
			} else if (itemId == 41392) { // 티테이불
				npcId = 80121;
			} else if (itemId == 41393) { // 화로
				npcId = 80126;
			} else if (itemId == 41394) { // 횃불
				npcId = 80125;
			} else if (itemId == 41395) { // 군주용의 서는 받침대
				npcId = 80111;
			} else if (itemId == 41396) { // 기
				npcId = 80112;
			} else if (itemId == 41397) { // 티테이불용의 의자( 오른쪽)
				npcId = 80116;
			} else if (itemId == 41398) { // 티테이불용의 의자(왼쪽)
				npcId = 80117;
			} else if (itemId == 41399) { // 파티션( 오른쪽)
				npcId = 80122;
			} else if (itemId == 41400) { // 파티션(왼쪽)
				npcId = 80123;
			}

			try {
				L1Npc l1npc = NpcTable.getInstance().getTemplate(npcId);
				if (l1npc  != null) {
					Object obj = null;
					try {
						String s = l1npc.getImpl();
						Constructor constructor = Class.forName(
								"l1j.server.server.model.Instance." + s
										+ "Instance").getConstructors()[0];
						Object aobj[] = { l1npc };
						furniture = (L1FurnitureInstance) constructor
								.newInstance(aobj);
						furniture.setId(IdFactory.getInstance().nextId());
						furniture.setMap(pc.getMapId());
						if (pc.getHeading() == 0) {
							furniture.setX(pc.getX());
							furniture.setY(pc.getY() - 1);
						} else if (pc.getHeading() == 2) {
							furniture.setX(pc.getX() + 1);
							furniture.setY(pc.getY());
						}
						furniture.setHomeX(furniture.getX());
						furniture.setHomeY(furniture.getY());
						furniture.setHeading(0);
						furniture.setItemObjId(itemObjectId);

						L1World.getInstance().storeObject(furniture);
						L1World.getInstance().addVisibleObject(furniture);
						FurnitureSpawnTable.getInstance()
								.insertFurniture(furniture);
					} catch (Exception e) {
						_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			} catch (Exception exception) {
			}
		} else {
			furniture.deleteMe();
			FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
		}
	}
	
	private void useFurnitureRemovalWand(L1PcInstance pc, int targetId,
			L1ItemInstance item) {
		S_AttackPacket s_attackPacket = new S_AttackPacket(pc, 0, ActionCodes
				.ACTION_Wand);
		pc.sendPackets(s_attackPacket);
		pc.broadcastPacket(s_attackPacket);
		int chargeCount = item.getChargeCount();
		if (chargeCount <= 0) {
			return;
		}

		L1Object target = L1World.getInstance().findObject(targetId);
		if (target != null && target instanceof L1FurnitureInstance) {
			L1FurnitureInstance furniture = (L1FurnitureInstance) target;
			furniture.deleteMe();
			FurnitureSpawnTable.getInstance().deleteFurniture(furniture);
			item.setChargeCount(item.getChargeCount() - 1);
			pc.getInventory().updateItem(item, L1PcInventory.COL_CHARGE_COUNT);
		}
	}

	@Override
	public String getType() {
		return C_ITEM_USE;
	}
}
