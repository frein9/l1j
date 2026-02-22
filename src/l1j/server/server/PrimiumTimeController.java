/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;

import java.util.logging.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Logger;
import l1j.server.Config;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.Announcements;

public class PrimiumTimeController implements Runnable {
	private static Logger _log = Logger.getLogger(PrimiumTimeController.class.getName());

	private static PrimiumTimeController _instance;

	public static PrimiumTimeController getInstance() {
		if (_instance == null) {
			_instance = new PrimiumTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkPrimiumTime();     // 추가
				Thread.sleep(60000);
				   for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
					     Object[] dollList = pc.getDollList().values().toArray();
				   for (Object dollObject : dollList) {
					     L1DollInstance doll = (L1DollInstance) dollObject;
					     doll.getActionByDoll();
					     }
				        }
			           }
		} catch (Exception e1) {
		}
	}
	private Calendar getRealTime() {
		  TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		  Calendar cal = Calendar.getInstance(_tz);
		  return cal;
	      }
		 
	 private void checkPrimiumTime() {
		  SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		  int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));

		  int primiumTime = Config.RATE_PRIMIUM_TIME;
		  int primiumNumber = Config.RATE_PRIMIUM_NUMBER;
		  /** 버그 방지 **/
		   if (primiumNumber >= 1000) { 
			  return;
		  }
		  /** 버그 방지 **/
		   if (nowtime % primiumTime == 0) {	
		      for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
		      int AccessLevel = pc.getLevel();					
                    if (!pc.isDead() && !pc.isPrivateShop()) { 
			            pc.getInventory().storeItem(41159,primiumNumber); // 신비한 날개깃털 지급 
			            pc.sendPackets(new S_SystemMessage("신비한 날개 깃털 ["+primiumNumber+"]을 얻었습니다.")); 				   
             } else if (pc.isPrivateShop()) { // 죽지 않고 상점일경우                       
            	//		pc.getInventory().storeItem(41159, 3); // 신비한 날개깃털 지급 
            	//		pc.sendPackets(new S_SystemMessage("\\fU[이벤트] 신비한 날개깃털 [3]개를 얻었습니다!."));
            			} 
				    if (AccessLevel < 70 && !pc.isPrivateShop()) { // 억세스레벨 70미만일때
                        pc.getInventory().storeItem(41159, 1); // 신비한 날개깃털 지급
                        pc.sendPackets(new S_SystemMessage("\\fU[견습생] "+ pc.getName()+ " 님 깃털 [1]개 추가지급!."));
				        }
			   else if (AccessLevel < 80 && !pc.isPrivateShop()) { // 억세스레벨 80미만일때
                        pc.getInventory().storeItem(41159, 2); // 신비한 날개깃털 지급
                        pc.sendPackets(new S_SystemMessage("\\fU[숙련자] "+ pc.getName()+ " 님 깃털 [2]개 추가지급!."));
				        }                          
			   else if (AccessLevel < 83 && !pc.isPrivateShop()) { // 억세스레벨 83미만일때
                        pc.getInventory().storeItem(41159, 3); // 신비한 날개깃털 지급 
                        pc.sendPackets(new S_SystemMessage("\\fU[고수] "+ pc.getName()+ " 님 깃털 [3]개 추가지급!.")); 
				        }
               else if (AccessLevel < 85 && !pc.isPrivateShop()) { // 억세스레벨 85미만일때
                        pc.getInventory().storeItem(41159, 4); // 신비한 날개깃털 지급 
                        pc.sendPackets(new S_SystemMessage("\\fU[영웅] "+ pc.getName()+ " 님 깃털 [4]개 추가지급!.")); 
				        }
			   else if (AccessLevel < 90 && !pc.isPrivateShop()) { // 억세스레벨 90미만일때
                        pc.getInventory().storeItem(41159, 5); // 신비한 날개깃털 지급
                        pc.sendPackets(new S_SystemMessage("\\fU[지존] "+ pc.getName()+ " 님 깃털 [5]개 추가지급!.")); 
				        }                          
               else if (AccessLevel < 100 && !pc.isPrivateShop()) { // 억세스레벨 99미만일때
                        pc.getInventory().storeItem(41159, 6); // 신비한 날개깃털 지급
                        pc.sendPackets(new S_SystemMessage("\\fU[신] "+ pc.getName()+ " 님 깃털 [6]개 추가지급!.")); 
				        }
                        L1Clan clan = pc.getClan();
                    if (clan != null && !pc.isPrivateShop()){ // 클랜이 있다면
                   // 혈맹 가입자에게 깃털 보너스 지급
                    if (clan.getClanId() != 561644842){ // 임시혈맹이 아니라면
                        pc.getInventory().storeItem(41159, 2); // 신비한 날개깃털 지급 
                        pc.sendPackets(new S_SystemMessage("\\fU혈맹 가입 보너스 깃털 [2]개 추가지급!"));
                        }
                   // 성혈에게 보너스 지급
                    if (clan.getCastleId() != 0 && !pc.isPrivateShop()){ // 클랜이 소유한 혈맹이 있다면
                        pc.getInventory().storeItem(41159, 3);
                        pc.sendPackets(new S_SystemMessage("\\fU성혈 보너스 깃털 [3]개 추가지급!"));
                        }
                       }
                      }
			         } else {
		                    return;
		                    } // end if - else 
		 	               } // end checkPrimium method
                          }
