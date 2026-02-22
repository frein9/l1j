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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import l1j.server.server.datatables.ItemTable;


import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Fishing;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;

public class FishingTimeController implements Runnable {
 private static FishingTimeController _instance;
 private final List<L1PcInstance> _fishingList =
   new ArrayList<L1PcInstance>();

  private static Random _random = new Random(System.nanoTime());
 public static FishingTimeController getInstance() {
  if (_instance == null) {
   _instance = new FishingTimeController();
  }
  return _instance;
 }

 @Override
 public void run() {
  try {
   while (true) {
    Thread.sleep(300);
    fishing();
   }
  } catch (Exception e1) {
  }
 }

 public void addMember(L1PcInstance pc) {
  if (pc == null || _fishingList.contains(pc)) {
   return;
  }
  _fishingList.add(pc);
 }

 public void removeMember(L1PcInstance pc) {
  if (pc == null || !_fishingList.contains(pc)) {
   return;
  }
  _fishingList.remove(pc);
 }

 private void fishing() {
  if (_fishingList.size() > 0) {
   long currentTime = System.currentTimeMillis();
   L1PcInstance pc = null;
   for (int i = 0; i < _fishingList.size(); i++) {
    pc = _fishingList.get(i);
    if (pc.isFishing()) {
     long time = pc.getFishingTime();
     if (currentTime <= (time + 1000)
       && currentTime >= (time - 1000)
       && !pc.isFishingReady()) {
      pc.setFishingReady(true);
      pc.sendPackets(new S_Fishing());
      pc.sendPackets(new S_PacketBox(S_PacketBox.FISHING));
      } else  if ( currentTime > (time + 100)
                ){
      int chance = _random.nextInt(200) + 1;
      if (chance < 50) {
            successFishing(pc, 41298, "$5256"); // 25%어린 물고기   
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); //낚시시간 될려주기
         } else if (chance < 65) {
            successFishing(pc, 41300, "$5258"); // 7.5% 강한 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 80) {
            successFishing(pc, 41299, "$5257"); // 7.5%재빠른 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 90) {
            successFishing(pc, 41296, "$5249"); // 5%붕어
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 100) {
            successFishing(pc, 41297, "$5250"); // 5%잉어
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2);
         } else if (chance < 105) {
            successFishing(pc, 41301, "$5259"); // 2.5%붉은 빛 나는 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 110) {
            successFishing(pc, 41302, "$5260"); // 2.5%초록 빛 나는 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 115) {
            successFishing(pc, 41303, "$5261"); // 2.5%파란 빛 나는 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 120) {
            successFishing(pc, 41304, "$5262"); // 2.5%흰 빛 나는 물고기
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 123) {
            successFishing(pc, 41306, "$5263"); // 1.5%깨진 반지
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 126) {
            successFishing(pc, 41307, "$5265"); // 1.5%깨진 목걸이
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2);
         } else if (chance < 129) {
            successFishing(pc, 41305, "$5264"); // 1.5%깨진 귀걸이
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2);
         } else if (chance < 134) {
            successFishing(pc, 21051, "$5269"); // 2.5%물에 젖은 투구
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2);
         } else if (chance < 139) {
            successFishing(pc, 21052, "$5270"); // 2.5%물에 젖은 망토
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 144) {
            successFishing(pc, 21053, "$5271"); // 2.5%물에 젖은 갑옷
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 159) {
            successFishing(pc, 21054, "$5272"); // 2.5%물에 젖은 장갑
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2);
         } else if (chance < 161) {
            successFishing(pc, 41252, " 진귀한 거북이 (1) 개"); // 1%
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 163) {
            successFishing(pc, 555113, "상자 물고기 (1) 개"); // 1% //아이템번호
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); 
         } else if (chance < 164) {
            successFishing(pc, 555112, "반짝이는 비늘 (1) 개"); //0.5%//아이템번호
            long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
            pc.setFishingTime(time2); //낚시시간 될려주기
      /*} else if (chance < 172) { //4퍼 비늘나오게
            successFishing(pc, 555112, "반짝이는 비늘 (1) 개"); // 1.0% //아이템번호
            successFishing(pc, 41252, " 진귀한 거북이 (1) 개"); // 1%*/
            pc.setFishingTime(0);
            pc.setFishingReady(false);
            pc.setFishing(false);
            pc.sendPackets(new S_CharVisualUpdate(pc));
            pc.broadcastPacket(new S_CharVisualUpdate(pc));
            pc.sendPackets(new S_ServerMessage(1163, ""));  // 낚시가 종료했습니다.
            removeMember(pc);
     
     }else {
      pc.sendPackets(new S_ServerMessage(1136, "")); // 낚시해에 실패했습니다.
      pc.getInventory().consumeItem(41295, 1); //실패시 삭제 
      long time2 = System.currentTimeMillis() + 10000 + _random.nextInt(6) * 1000;
      pc.setFishingTime(time2);
     }
    
       }
     }
    }
   } 
 }

private void successFishing(L1PcInstance pc, int itemId, String message) {
   L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
   item.startItemOwnerTimer(pc);
   pc.getInventory().storeItem(item);
   pc.getInventory().consumeItem(41295, 1);   //먹이
   pc.sendPackets(new S_ServerMessage(1185, message));//낚시에 성공했습니다.
    if (!pc.getInventory().checkItem(41295)) {  //먹이
     
     pc.setFishingTime(0);
     pc.setFishingReady(false);
     pc.setFishing(false);
     pc.sendPackets(new S_CharVisualUpdate(pc));
     pc.broadcastPacket(new S_CharVisualUpdate(pc));
     pc.sendPackets(new S_ServerMessage(1137)); //낚시를하기 위해선 먹이가 필요합니다.
     removeMember(pc);
    
   }
}
}