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
package l1j.server.server.model.trap;

import java.util.Random;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.util.ArrayList;

import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.storage.TrapStorage;
import l1j.server.server.utils.Dice;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.storage.TrapStorage;
import l1j.server.server.model.L1World;
import l1j.server.server.ActionCodes;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_DoorPack;
import l1j.server.server.clientpackets.C_Door;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1Racing;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.model.L1PetRace;
import l1j.server.server.model.L1PetRaceEnd;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_GameList;
import l1j.server.server.serverpackets.S_GameList2;
import l1j.server.server.serverpackets.S_GameRanking;
import l1j.server.server.serverpackets.S_GameOver;
import l1j.server.server.serverpackets.S_GameEnd;
import l1j.server.server.serverpackets.S_GameRap;

public class L1PetRaceTrap extends L1Trap {
	private final Dice _dice;
	private final int _base;
	private final int _diceCount;
	private final String _type;
	private final int _delay;
	private final int _time;
	private final int _damage;

	public void ListUpdate(){
		for(int i = 0; i < L1Racing.getInstance().size(0); i++){
			L1Racing.getInstance().arrayList(0).get(i).sendPackets(new S_GameList(L1Racing.getInstance().arrayList(0).get(i), i));
		}
	}

	public void ListChange(L1PcInstance pc, int i){
		ArrayList<L1PcInstance> member1 = new ArrayList<L1PcInstance>();
		ArrayList<L1PcInstance> member2 = new ArrayList<L1PcInstance>();
	}


	public L1PetRaceTrap(TrapStorage storage) {
		super(storage);

		_dice = new Dice(storage.getInt("dice"));
		_base = storage.getInt("base");
		_diceCount = storage.getInt("diceCount");
		_type = storage.getString("poisonType");
		_delay = storage.getInt("poisonDelay");
		_time = storage.getInt("poisonTime");
		_damage = storage.getInt("poisonDamage");
	}

	@Override
	public void onTrod(L1PcInstance c, L1Object trapObj) {
		sendEffect(trapObj);

		if(_type.equals("f")) { //결승점
			L1Racing racing = L1Racing.getInstance();
			// 초기 목록에 있다면 이미 1바퀴
			// 1바퀴째 있다면 2바퀴
			// 2바퀴째 있다면 3바퀴
			// 3바퀴째 있다면 4바퀴(골인)
			if(racing.contains(racing.체크1, c) && racing.contains(racing.체크2, c) && racing.contains(racing.체크3, c)){
				if(racing.contains(racing.한바퀴, c)){					// 1바퀴
					racing.remove(racing.한바퀴, c);
					racing.add(racing.두바퀴, c);
					c.sendPackets(new S_GameRap(c, 2));
					racing.remove(racing.체크1, c);									// 트랙 돈 체크 초기화
					racing.remove(racing.체크2, c);									// 
					racing.remove(racing.체크3, c);									//
				}else if(racing.contains(racing.두바퀴, c)){					// 2바퀴
					racing.remove(racing.두바퀴, c);
					racing.add(racing.세바퀴, c);
					c.sendPackets(new S_GameRap(c, 3));
					racing.remove(racing.체크1, c);									// 트랙 돈 체크 초기화
					racing.remove(racing.체크2, c);									// 
					racing.remove(racing.체크3, c);									//
				}else if(racing.contains(racing.세바퀴, c)){					// 3바퀴
					racing.remove(racing.세바퀴, c);
					racing.add(racing.네바퀴, c);
					c.sendPackets(new S_GameRap(c, 4));
					racing.remove(racing.체크1, c);									// 트랙 돈 체크 초기화
					racing.remove(racing.체크2, c);									// 
					racing.remove(racing.체크3, c);									//
				}else if(racing.contains(racing.네바퀴, c)){					// 3바퀴
					// 1등 추출 
					if(c.getId() == racing.toArray(racing.네바퀴, 0).getId()){
						c.sendPackets(new S_SystemMessage("1등 하셨습니다."));
						c.sendPackets(new S_GameRanking(c));
						c.getInventory().storeItem(500013, 1);
					}else{
						for(int i = 1; i < racing.arrayList(racing.네바퀴).size(); i++){
							racing.toArray(racing.네바퀴, i).sendPackets(new S_SystemMessage((i+1) + "등 하셨습니다."));
						}
					}
					racing.remove(racing.체크1, c);									// 트랙 돈 체크 초기화
					racing.remove(racing.체크2, c);									// 
					racing.remove(racing.체크3, c);									//
					racing.close();
				}
			}else{
				if(racing.contains(racing.일반, c)){				// 일반
					racing.remove(racing.일반, c);
					racing.add(racing.한바퀴, c);
					c.sendPackets(new S_GameRap(c, 1));
				}
			}
		}else if (_type.equals("g")) { //변신트랩
			Random random = new Random();
			int chance = random.nextInt(10);
			switch (chance) {
			case 0: 
				L1PolyMorph.doPoly(c, 29 ,1000, L1PolyMorph.MORPH_BY_NPC); //괴물눈
				break;
			case 1: 
				L1PolyMorph.doPoly(c, 3184 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이세퍼드
				break;
			case 2: 
				L1PolyMorph.doPoly(c, 3182 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이세인트
				break;
			case 3: 
				L1PolyMorph.doPoly(c, 938 ,1000, L1PolyMorph.MORPH_BY_NPC); //비글
				break;
			case 4: 
				L1PolyMorph.doPoly(c, 4168 ,1000, L1PolyMorph.MORPH_BY_NPC); //맘보토끼
				break;
			case 5: 
				L1PolyMorph.doPoly(c, 3156 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이폭스
				break;
			case 6: 
				L1PolyMorph.doPoly(c, 1649 ,1000, L1PolyMorph.MORPH_BY_NPC); //터틀
				break;
			case 7: 
				L1PolyMorph.doPoly(c, 3199 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이울프
				break;
			case 8: 
				L1PolyMorph.doPoly(c, 3107 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이허스키
				break;
			case 9: 
				L1PolyMorph.doPoly(c, 29 ,1000, L1PolyMorph.MORPH_BY_NPC); //괴물눈
				break;
			}
		}else if (_type.equals("h")) { //변신트랩
			Random random = new Random();
			int chance = random.nextInt(10);
			switch (chance) {
			case 0: 
				L1PolyMorph.doPoly(c, 29 ,1000, L1PolyMorph.MORPH_BY_NPC); //괴물눈
				break;
			case 1: 
				L1PolyMorph.doPoly(c, 3184 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이세퍼드
				break;
			case 2: 
				L1PolyMorph.doPoly(c, 3182 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이세인트
				break;
			case 3: 
				L1PolyMorph.doPoly(c, 938 ,1000, L1PolyMorph.MORPH_BY_NPC); //비글
				break;
			case 4: 
				L1PolyMorph.doPoly(c, 4168 ,1000, L1PolyMorph.MORPH_BY_NPC); //맘보토끼
				break;
			case 5: 
				L1PolyMorph.doPoly(c, 3156 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이폭스
				break;
			case 6: 
				L1PolyMorph.doPoly(c, 1649 ,1000, L1PolyMorph.MORPH_BY_NPC); //터틀
				break;
			case 7: 
				L1PolyMorph.doPoly(c, 3199 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이울프
				break;
			case 8: 
				L1PolyMorph.doPoly(c, 3107 ,1000, L1PolyMorph.MORPH_BY_NPC); //하이허스키
				break;
			case 9: 
				L1PolyMorph.doPoly(c, 29 ,1000, L1PolyMorph.MORPH_BY_NPC); //괴물눈
				break;
			}
		}else if (_type.equals("i")) { //속도트랩 1
			c.sendPackets(new S_SkillHaste(c.getId(), 1, 120));
			c.sendPackets(new S_SkillBrave(c.getId(), 1, 8));
			c.setSkillEffect(43, 1000 * 120);
			c.setSkillEffect(1000, 1000 * 8);
		}else if (_type.equals("j")) { //속도트랩 2
			c.sendPackets(new S_SkillHaste(c.getId(), 1, 120));
			c.sendPackets(new S_SkillBrave(c.getId(), 1, 8));
			c.setSkillEffect(43, 1000 * 120);
			c.setSkillEffect(1000, 1000 * 8);
		}
	}
}