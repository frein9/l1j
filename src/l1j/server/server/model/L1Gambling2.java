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

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import l1j.server.server.Opcodes;
import l1j.server.server.ActionCodes;
import l1j.server.server.SkillCheck;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.L1Character;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.model.skill.L1SkillUse;

public class L1Gambling2 {
	private static final Logger _log = Logger.getLogger(L1Gambling2.class
			.getName());
	
	
	public void Gambling(L1PcInstance player, int bettingmoney){
		try {
		for (L1Object l1object : L1World.getInstance().getObject()) {
			   if (l1object instanceof L1NpcInstance) {
			    L1NpcInstance Npc = (L1NpcInstance) l1object;
			    if (Npc.getNpcTemplate().get_npcId() == 300026){
			    	L1NpcInstance dealer = Npc;
			    	if(bettingmoney == 20000){
			    	String chat = "정상 입금 되었습니다! 감사합니다. 또오세요~";
					player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
					player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
					Thread.sleep(800);
					player.sendPackets(new S_DoActionGFX(dealer.getId(), ActionCodes.ACTION_SkillBuff));
					player.broadcastPacket(new S_DoActionGFX(dealer.getId(), ActionCodes.ACTION_SkillBuff));
					player.setBuffnoch(1);
					new L1SkillUse().handleCommands(player, 48, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 79, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 151, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					//블레스,어벤,어스스킨
					Thread.sleep(300);
					for (L1Object l1object2 : L1World.getInstance().getObject()) {
						   if (l1object2 instanceof L1NpcInstance) {
						    L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
				     if (Npc2.getNpcTemplate().get_npcId() == 300028 || Npc2.getNpcTemplate().get_npcId() == 300030){
					L1NpcInstance dealer2 = Npc2;
					player.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_SkillBuff));
					player.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_SkillBuff));
					new L1SkillUse().handleCommands(player, 148, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					 }
						   }
					}
				     player.setBuffnoch(0);
			    	}else if(bettingmoney > 99999){
			    	String chat = "감사합니다. 행복한 하루되세요.";
					player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
					player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
					Thread.sleep(800);
					player.sendPackets(new S_DoActionGFX(dealer.getId(), ActionCodes.ACTION_SkillBuff));
					player.broadcastPacket(new S_DoActionGFX(dealer.getId(), ActionCodes.ACTION_SkillBuff));
					player.setBuffnoch(1);
					new L1SkillUse().handleCommands(player, 48, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 79, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 57, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 151, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 26, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 42, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 43, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 149, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 148, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					new L1SkillUse().handleCommands(player, 158, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					Thread.sleep(300);
					for (L1Object l1object2 : L1World.getInstance().getObject()) {
						   if (l1object2 instanceof L1NpcInstance) {
						    L1NpcInstance Npc2 = (L1NpcInstance) l1object2;
				     if (Npc2.getNpcTemplate().get_npcId() == 300028 || Npc2.getNpcTemplate().get_npcId() == 300030
				    		 || Npc2.getNpcTemplate().get_npcId() == 300031  || Npc2.getNpcTemplate().get_npcId() == 300032
				    		 || Npc2.getNpcTemplate().get_npcId() == 300033 || Npc2.getNpcTemplate().get_npcId() == 300034
				    		 || Npc2.getNpcTemplate().get_npcId() == 300035 || Npc2.getNpcTemplate().get_npcId() == 300036
				    		 || Npc2.getNpcTemplate().get_npcId() == 300037){
					L1NpcInstance dealer2 = Npc2;
					player.sendPackets(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_SkillBuff));
					player.broadcastPacket(new S_DoActionGFX(dealer2.getId(), ActionCodes.ACTION_SkillBuff));
					new L1SkillUse().handleCommands(player, 148, player.getId(), player.getX(), player.getY(), null,0, L1SkillUse.TYPE_GMBUFF);
					 }
						   }
					}
				     player.setBuffnoch(0);
			    	}else if(bettingmoney < 20000){
			    	String chat = "아덴이 모자르네요..." + bettingmoney + "아덴은 제가먹습니다!";
					player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
					player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
			    	}
			    }
			   }
		}
		}catch (Throwable e) {
			_log.log(Level.WARNING, e.getLocalizedMessage(), e);
		}
	}
	
	
	
	public void dealerTrade(L1PcInstance player) {
		L1Object obj = L1World.getInstance().findObject(300026);
		L1Npc npc = NpcTable.getInstance().getTemplate(300026);
		if(player.getX() == 33420 && player.getY() == 32799 && player.getHeading() == 0){
			for (L1Object l1object : L1World.getInstance().getObject()) {
				   if (l1object instanceof L1NpcInstance) {
				    L1NpcInstance Npc = (L1NpcInstance) l1object;
				    if (Npc.getNpcTemplate().get_npcId() == 300026){
				    	L1NpcInstance dealer = Npc;
				    	String chat = "이용료는 거지는 2만, 부자님은 10만입니다.~!";
						player.sendPackets(new S_NpcChatPacket(dealer, chat, 0));
						player.broadcastPacket(new S_NpcChatPacket(dealer, chat, 0));
				    }
				   }
			}
			player.sendPackets(new S_Message_YN(252, npc.get_name())); // %0%s가 당신과 아이템의 거래를 바라고 있습니다. 거래합니까? (Y/N)	
		}
	}	
}
