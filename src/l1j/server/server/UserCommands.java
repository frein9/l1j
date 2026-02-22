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

package l1j.server.server;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.StringTokenizer;

import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1NpcDeleteTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.serverpackets.S_WhoAmount;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.utils.IntRange;
import static l1j.server.server.model.skill.L1SkillId.*;
import l1j.server.server.serverpackets.S_Serchdrop; 
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_Chainfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import l1j.server.L1DatabaseFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// ClientThread, Shutdown, IpTable, MobTable,
// PolyTable, IdFactory
//

   public class UserCommands {
       private static Logger _log = Logger.getLogger(UserCommands.class.getName());

       boolean spawnTF = false;

       private static UserCommands _instance;

       private UserCommands() {
       }

       public static UserCommands getInstance() {
           if (_instance == null) {
             _instance = new UserCommands();
               }
              return _instance;
           }

       public void handleCommands(L1PcInstance pc, String cmdLine) {
          StringTokenizer token = new StringTokenizer(cmdLine);
          String cmd = token.nextToken();
          String param = "";
          while (token.hasMoreTokens()) {
             param = new StringBuilder(param).append(token.nextToken()).append(' ').toString(); }
             param = param.trim();
          try {
    
              if (cmd.equalsIgnoreCase("도움말")) {
                  showHelp(pc);    
              } else if (cmd.equalsIgnoreCase("정보")) {
                  help(pc);
              } else if (cmd.equalsIgnoreCase("조사")) {
                  serchPc(pc, param);
          /*  } else if (cmd.equalsIgnoreCase("상세한조사")) {   //이건 영자마음대로 공개하던지
                  serchPc2(pc, param); *///하면 될듯~ 비리없는 깨끗한 서버를 원한다면 이거 유저들한테공개 
              } else if (cmd.equalsIgnoreCase("텔렉풀기")) {
            	  tell(pc); 
              } else if (cmd.equalsIgnoreCase("버프")) {
            	  buff(pc); 
              } else if (cmd.equalsIgnoreCase("우호도")) {
            	  describe(pc);  
              } else if (cmd.equalsIgnoreCase("드랍리스트")) {
                  serchdroplist(pc, param);  
              } else if(cmd.equalsIgnoreCase("봉인암호")) {
				  sealing(pc, param);
			  } else if(cmd.equalsIgnoreCase("봉인암호변경")) {
			      chSealing(pc, param);
			  } else if(cmd.equalsIgnoreCase("봉인해제")) {
				  unSealing2(pc, param);
		   /* } else if(cmd.equalsIgnoreCase("해제주문서")) {
				  unSealing(pc, param);
		      if (!cmd.equalsIgnoreCase("조사")) { 
	               _log.info(pc.getName() + "가." + cmdLine + "커멘드를 사용했습니다.");
	                } 
              } else if (cmd.equalsIgnoreCase("무인상점")) {
            	  help2(pc); 
              } else {
                  String msg = new StringBuilder().append("커멘드：")
                  .append(cmd).append("가 존재하지 않는, 또는 실행권한이 없습니다.").toString();
                  pc.sendPackets(new S_SystemMessage(msg));
               } 
              if (!cmd.equalsIgnoreCase("조사")) {
                   _log.info(pc.getName() + "가." + cmdLine + "커멘드를 사용했습니다."); */
              } 
              } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
                   }
              }
       
      private void showHelp(L1PcInstance pc) {
           pc.sendPackets(new S_SystemMessage("-----------------< 유저 명령어 >-------------------"));
           pc.sendPackets(new S_SystemMessage(".정보  .버프  .조사  .우호도  .드랍리스트  .텔렉풀기 "));
           pc.sendPackets(new S_SystemMessage(".봉인암호  .봉인암호변경  .봉인해제  "));
           pc.sendPackets(new S_SystemMessage("---------------------------------------------------")); 
       }
          
      private void help(L1PcInstance pc) {
           pc.sendPackets(new S_SystemMessage("=============================================="));
           pc.sendPackets(new S_SystemMessage("낚시로 마법인형 구할 수 있음 "));
           pc.sendPackets(new S_SystemMessage("유령의 집과 신비의 큐브 아이템을 통해 "));
           pc.sendPackets(new S_SystemMessage("용기있는 자의 호박주머니 획득 가능"));
           pc.sendPackets(new S_SystemMessage("=============================================="));
      }
      
    /*  private void help2(L1PcInstance pc) {
          pc.sendPackets(new S_SystemMessage("=============================================="));
          pc.sendPackets(new S_SystemMessage("다른 계정에 있는 케릭터로 시장에 개인상점을 열고"));
          pc.sendPackets(new S_SystemMessage("종료하면 그 케릭터는 접속하기전까지 상점이 유지"));
          pc.sendPackets(new S_SystemMessage("무인상점인 상태에서는 신비한깃털을 얻을수 없음"));
          pc.sendPackets(new S_SystemMessage("상점중인 케릭터로 접속할때는 한번 팅기고나서 재접속"));
          pc.sendPackets(new S_SystemMessage("=============================================="));
     }*/
      
      private void tell(L1PcInstance pc) {
    	  try {
    	      L1Teleport.teleport(pc, pc.getX(), pc.getY(), pc.getMapId(), pc.getHeading(), false);
    	      } catch (Exception exception35) {
    	  }
     } 

      private void serchdroplist(L1PcInstance gm, String param) {
		  try {
		   StringTokenizer tok = new StringTokenizer(param);
		   String nameid = tok.nextToken();

		   int itemid = 0;
		   try {
		    itemid = Integer.parseInt(nameid);
		   } catch (NumberFormatException e) {
		    itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
		    if (itemid == 0) {
		     gm.sendPackets(new S_SystemMessage("해당 아이템이 발견되지 않았습니다."));
		     return;
		    }
		   }
		   gm.sendPackets(new S_Serchdrop(itemid));
		  } catch (Exception e) {
	  //   _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		   gm.sendPackets(new S_SystemMessage(".드랍리스트 [itemid 또는 name]를 입력해 주세요."));
		   gm.sendPackets(new S_SystemMessage("아이템 name을 공백없이 정확히 입력해야 합니다."));
		   gm.sendPackets(new S_SystemMessage("ex) .드랍리스트 마법서(디스인티그레이트) -- > 검색 O"));
		   gm.sendPackets(new S_SystemMessage("ex) .드랍리스트 디스 -- > 검색 X"));
		  }
		 }
      private static boolean isDisitAlaha(String str) {
          boolean check = true;
          for (int i = 0; i < str.length(); i++) {
          if (!Character.isDigit(str.charAt(i)) // 숫자가 아니라면
              && Character.isLetterOrDigit(str.charAt(i)) // 특수문자라면
              && !Character.isUpperCase(str.charAt(i)) // 대문자가 아니라면
              && Character.isWhitespace(str.charAt(i)) // 공백이라면
              && !Character.isLowerCase(str.charAt(i))) { // 소문자가 아니라면
                   check = false;
                   break;
                  }
               }
           return check;
          }	  	
      
      private void buff(L1PcInstance pc) {
  		int[] allBuffSkill = { PHYSICAL_ENCHANT_DEX, PHYSICAL_ENCHANT_STR, BLESS_WEAPON,
  				ADVANCE_SPIRIT, IRON_SKIN };
  		if (pc.getLevel() <= 52) { // 52레벨 이하
  		try {
  			//for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
  				pc.setBuffnoch(1); // 스킬버그땜시 추가 올버프는 미작동
  				L1SkillUse l1skilluse = new L1SkillUse();
  				for (int i = 0; i < allBuffSkill.length ; i++) {
  					l1skilluse.handleCommands(pc, allBuffSkill[i], pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_GMBUFF);
  				} 
  				pc.sendPackets(new S_SystemMessage("기분이 한결 좋아집니다."));
  				pc.setBuffnoch(0); // 스킬버그땜시 추가 올버프는 미작동
  			//}
  			pc.sendPackets(new S_SystemMessage("52레벨까지 초보 버프 사용이 가능합니다."));
  		} catch (Exception exception19) {
  			pc.sendPackets(new S_SystemMessage(".버프 명령어 에러"));
  		    }
  		} else {
  			pc.sendPackets(new S_SystemMessage("52레벨이상 케릭터는 버프를 받을 수 없습니다."));
  		}
  	}
    private void describe(L1PcInstance pc) {
  		  try {
  		  	  StringBuilder msg = new StringBuilder();
  			  pc.sendPackets(new S_SystemMessage("-- 내 우호도 정보 --"));  			
  			  pc.sendPackets(new S_SystemMessage("우호도: " + pc.getKarma() + ""));
  			  pc.sendPackets(new S_SystemMessage(msg.toString()));
  		  } catch (Exception e) {
  		      pc.sendPackets(new S_SystemMessage(".우호도 명령어 에러"));
  		  }
  	  }

      private void sealing(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String sealingPW = tok.nextToken();
		
			if (sealingPW.length() > 10) {
				pc.sendPackets(new S_SystemMessage("입력하신 암호의 길이가 너무 깁니다."));
				return;
			}
			if (pc.getSealingPW() != null) {
				pc.sendPackets(new S_SystemMessage("이미 봉인 암호가 설정되어 있습니다."));
				return;
			}
			pc.setSealingPW(sealingPW);
			pc.save();
			pc.sendPackets(new S_SystemMessage("봉인 암호(" + sealingPW + ")가 설정되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).봉인암호 암호"));
		}
	}

	private void chSealing(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String oldSealingPW = tok.nextToken();
			String newSealingPW = tok.nextToken();

			if (oldSealingPW.length() > 10 || newSealingPW.length() > 10) {
				pc.sendPackets(new S_SystemMessage("입력하신 암호의 길이가 너무 깁니다."));
				return;
			}

			if (pc.getSealingPW() == null || pc.getSealingPW() == "") {
				pc.sendPackets(new S_SystemMessage("봉인 암호가 설정되어 있지 않습니다."));
				return;
			}
			if (!oldSealingPW.equals(pc.getSealingPW())) {
				pc.sendPackets(new S_SystemMessage("설정된 암호와 일치하지 않습니다."));
				return;
			}
			pc.setSealingPW(newSealingPW);
			pc.save();
			pc.sendPackets(new S_SystemMessage("봉인 암호(" + newSealingPW + ")가 설정되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).봉인암호변경 기존암호 새암호"));
		}
	}

	/*private void unSealing(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String sealingPW = tok.nextToken();

			if (pc.getSealingPW() == null || pc.getSealingPW() == "") {
				pc.sendPackets(new S_SystemMessage("봉인 암호가 설정되어 있지 않습니다."));
				return;
			}
			if (!sealingPW.equals(pc.getSealingPW())) {
				pc.sendPackets(new S_SystemMessage("설정된 암호와 일치하지 않습니다."));
				return;
			}
			String s = null;
			pc.setSealingPW(sealingPW);
			pc.save();
			pc.getInventory().storeItem(500012, 5); 
			pc.sendPackets(new S_SystemMessage("봉인 해제 주문서(5)를 받으셨습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).해제주문서 암호"));
		}
	}*/
	private void unSealing2(L1PcInstance pc, String cmd) {
		try {
			StringTokenizer tok = new StringTokenizer(cmd);
			String sealingPW = tok.nextToken();

			if (pc.getSealingPW() == null || pc.getSealingPW() == "") {
				pc.sendPackets(new S_SystemMessage("봉인 암호가 설정되어 있지 않습니다."));
				return;
			}
			if (!sealingPW.equals(pc.getSealingPW())) {
				pc.sendPackets(new S_SystemMessage("설정된 암호와 일치하지 않습니다."));
				return;
			}
			String s = null;
			pc.setSealingPW(s);
			pc.save();
			pc.sendPackets(new S_SystemMessage("봉인 암호가 해제되었습니다."));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("사용 예).봉인해제 암호"));
		}
	}
	/*private void serchPc2(L1PcInstance pc, String param) {     //상세한 조사
		try {//이건 영자맘대로 공개하던지 하면될듯~ 비리섭 방지용 @_@
			StringTokenizer stringtokenizer = new StringTokenizer(param);
			String s = stringtokenizer.nextToken();
			pc.sendPackets(new S_Chainfo(1, s));
		} catch (Exception exception21) {
			pc.sendPackets(new S_SystemMessage(".조사 [캐릭터명]을 입력 해주세요."));
		}
	}*/
      private void serchPc(L1PcInstance pc, String param) {
           try {    
              StringTokenizer stringtokenizer = new StringTokenizer(param);
              String para1 = stringtokenizer.nextToken();  
              L1PcInstance TargetPc = L1World.getInstance().getPlayer(para1);    

              int Weapon = 0;
              int Armor = 0;
              int Aden = 0;
  
              if (TargetPc != null ) {
                  List <L1ItemInstance> enchant = TargetPc.getInventory().getItems();     
                   for (int j = 0; j < enchant.size(); ++j) {
                   int itemType = enchant.get(j).getItem().getType2();
                        if (enchant.get(j).getEnchantLevel() >= 10 && itemType == 1){
                         Weapon++;
                        }
                        if (enchant.get(j).getEnchantLevel() >= 8 && itemType == 2){
                         Armor++;                    
                        }
                        if (enchant.get(j).getItemId() == 40308 && enchant.get(j).getCount() >= 50000000){
                         Aden++;
                        }
                    }
                   pc.sendPackets(new S_SystemMessage("\\fU"+para1+" <케릭터 조사 결과>"));   
                   pc.sendPackets(new S_SystemMessage("\\fU<+10이상 무기: "+Weapon+"개>  <+8이상 방어구: "+Armor+"개 소지중>"));   
                   if ( Aden != 0) {
                   pc.sendPackets(new S_SystemMessage("\\fU<5천만이상 아덴 보유 유무: Yes>"));
                   } else {
                       pc.sendPackets(new S_SystemMessage("\\fU<5천만이상 아덴 보유 유무: No>"));
                   }
                   
           } else {
               pc.sendPackets(new S_SystemMessage(param+" 케릭터가 월드내에 존재 하지 않습니다"));    
           }
           } catch (Exception e) {
              pc.sendPackets(new S_SystemMessage(".조사 케릭명 으로 입력해 주세요."));
           }
       } 
   }
