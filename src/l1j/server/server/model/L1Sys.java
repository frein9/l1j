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

import java.util.List;
import java.util.logging.Logger;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

 

public class L1Sys implements Runnable {
 private boolean loop = true;

 private static final Logger _log = Logger
   .getLogger(L1DeleteItemOnGround.class.getName());

 private static L1Sys _instance;

 public static L1Sys getInstance() {
  if (_instance == null) {
   _instance = new L1Sys();
  }
  return _instance;
 }

 

  @Override 
  public void run() {
   while (loop) {
    try {
     String sys1 = String.format("%s", Config.sys1);
  String sys2 = String.format("%s", Config.sys2);
  String sys3 = String.format("%s", Config.sys3);
  String sys4 = String.format("%s", Config.sys4);
  String sys5 = String.format("%s", Config.sys5);
  String sys6 = String.format("%s", Config.sys6);
  String sys7 = String.format("%s", Config.sys7);
  
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys1)); 
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys2));
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys3));
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys4));
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys5));
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys6));
    Thread.sleep(100000*Config.systime);
    L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(sys7));
     
    
    
    } catch (Exception exception) {
    
   
    }
  }
}
}
