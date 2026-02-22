/** 스탯초기화 */

package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.ClientThread;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId; // 버프효과 해제
import l1j.server.server.model.skill.L1SkillUse; // 버프효과 해제
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_ReturnedStat;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.serverpackets.S_ShowOrignalBonus;

public class C_ReturnStaus extends ClientBasePacket {
	
	private static Logger _log = Logger.getLogger(C_ReturnStaus.class.getName());
	
	
	
	public C_ReturnStaus(byte[] decrypt, ClientThread client) {
		
		super(decrypt);
        int type = readC();
		L1PcInstance pc = client.getActiveChar();
		pc.getInventory().takeoffEquip(945); // 아이템 벗기기
		L1SkillUse l1skilluse = new L1SkillUse(); // 버프효과 해제
		l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(),
				null, 0, L1SkillUse.TYPE_LOGIN); // 버프효과 해제
        if(type == 1)
        {
    		short init_hp = 0;
    		short init_mp = 0;
            int str = readC();
            int intel = readC();
            int wis = readC();
            int dex = readC();
            int con = readC();
            int cha = readC();
            pc.setStr(str);
            pc.setInt(intel);
            pc.setWis(wis);
            pc.setDex(dex);
            pc.setCon(con);
            pc.setCha(cha);
            pc.setOriginalStr(str);
            pc.setOriginalInt(intel);
            pc.setOriginalWis(wis);
            pc.setOriginalDex(dex);
            pc.setOriginalCon(con);
            pc.setOriginalCha(cha);
            pc.setLevel(1);
    		if (pc.isCrown()) { // CROWN
    			init_hp = 14;
    			switch (pc.getWis()) {
    			case 11:
    				init_mp = 2;
    				break;
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 3;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 4;
    				break;
    			default:
    				init_mp = 2;
    				break;
    			}
    		} else if (pc.isKnight()) { // KNIGHT
    			init_hp = 16;
    			switch (pc.getWis()) {
    			case 9:
    			case 10:
    			case 11:
    				init_mp = 1;
    				break;
    			case 12:
    			case 13:
    				init_mp = 2;
    				break;
    			default:
    				init_mp = 1;
    				break;
    			}
    		} else if (pc.isElf()) { // ELF
    			init_hp = 15;
    			switch (pc.getWis()) {
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 4;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 6;
    				break;
    			default:
    				init_mp = 4;
    				break;
    			}
    		} else if (pc.isWizard()) { // WIZ
    			init_hp = 12;
    			switch (pc.getWis()) {
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 6;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 8;
    				break;
    			default:
    				init_mp = 6;
    				break;
    			}
    		} else if (pc.isDarkelf()) { // DE
    			init_hp = 12;
    			switch (pc.getWis()) {
    			case 10:
    			case 11:
    				init_mp = 3;
    				break;
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 4;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 6;
    				break;
    			default:
    				init_mp = 3;
    				break;
    			}
    		} else if (pc.isDragonKnight()) { // 용기사
    			init_hp = 16;
    			switch (pc.getWis()) {
    			case 10:
    			case 11:
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 2;
    				break;
    			default:
    				init_mp = 2;
    				break;
    			}
    		} else if (pc.isBlackWizard()) { // 환술사
    			init_hp = 14;
    			switch (pc.getWis()) {
    			case 10:
    			case 11:
    			case 12:
    			case 13:
    			case 14:
    			case 15:
    				init_mp = 5;
    				break;
    			case 16:
    			case 17:
    			case 18:
    				init_mp = 6;
    				break;
    			default:
    				init_mp = 5;
    				break;
    			}
    		}
            pc.setMaxHp(init_hp);
            pc.setMaxMp(init_mp);
            pc.setAc(10);
            pc.sendPackets(new S_OwnCharStatus(pc));
    		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
        } else if(type == 2) {
            int levelup = readC();
            if(levelup == 0) {
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 1) {
                pc.setStr(pc.getStr() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 2) {
                pc.setInt(pc.getInt() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 3) {
                pc.setWis(pc.getWis() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 4) {
                pc.setDex(pc.getDex() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 5) {
                pc.setCon(pc.getCon() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 6) {
                pc.setCha(pc.getCha() + 1);
            	statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 7) {
                for(int m = 0; m < 10; m++)
                statup(pc);
        		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.레벨업));
            } else if(levelup == 8) {
                int statusup = readC();
                if(statusup == 1) {
                    pc.setStr(pc.getStr() + 1);
                }else if(statusup == 2) {
                    pc.setInt(pc.getInt() + 1);
                }else if(statusup == 3) {
                    pc.setWis(pc.getWis() + 1);
                }else if(statusup == 4) {
                    pc.setDex(pc.getDex() + 1);
                }else if(statusup == 5) {
                    pc.setCon(pc.getCon() + 1);
                }else if(statusup == 6) {
                    pc.setCha(pc.getCha() + 1);
                }
                if(pc.getElixirStats() > 0){
            		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.종료));
                } else {
            		pc.sendPackets(new S_ReturnedStat(pc, S_ReturnedStat.종료));

                    pc.setCurrentHp(pc.getMaxHp());
                    pc.setCurrentMp(pc.getMaxMp());
        			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
        			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
        			pc.setBonusStats(pc.getHighLevel()-50);
        			pc.setExp(pc.getExp());
        			pc.setBaseMaxHp((short) pc.getMaxHp());
        			pc.setBaseMaxMp((short) pc.getMaxMp());
        			pc.setBaseStr((byte) pc.getStr()); 
        			pc.setBaseDex((byte) pc.getDex()); // 소의 DEX치에+1
        			pc.setBaseInt((byte) pc.getInt());
        			pc.setBaseWis((byte) pc.getWis()); // 소의 WIS치에+1
        			pc.setBaseCha((byte) pc.getCha());
        			pc.setBaseCon((byte) pc.getCon());
        			pc.resetBaseHitup();
        			pc.resetBaseDmgup();
        			pc.resetBaseAc();
        			pc.resetBaseMr();
        			pc.resetLevel();
        			pc.sendPackets(new S_OwnCharStatus2(pc));
        			pc.sendPackets(new S_CharVisualUpdate(pc));
        			try {
        			pc.save(); 
        			}catch(Exception e){}
        			L1Teleport.teleport(pc, 32610, 32777, (short)4, 5, true);
                }
            }
        } else if(type == 3) {
            int str = readC();
            int intel = readC();
            int wis = readC();
            int dex = readC();
            int con = readC();
            int cha = readC();
            pc.setStr(str);
            pc.setInt(intel);
            pc.setWis(wis);
            pc.setDex(dex);
            pc.setCon(con);
            pc.setCha(cha);
            pc.sendPackets(new S_OwnCharStatus(pc));
            pc.sendPackets(new S_OwnCharAttrDef(pc));
            pc.setCurrentHp(pc.getMaxHp());
            pc.setCurrentMp(pc.getMaxMp());
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.setBonusStats(pc.getHighLevel()-50);
			pc.setExp(pc.getExp());
			pc.setBaseMaxHp((short) pc.getMaxHp());
			pc.setBaseMaxMp((short) pc.getMaxMp());
			pc.setBaseStr((byte) str); 
			pc.setBaseDex((byte) dex); // 소의 DEX치에+1
			pc.setBaseInt((byte) intel);
			pc.setBaseWis((byte) wis); // 소의 WIS치에+1
			pc.setBaseCha((byte) cha);
			pc.setBaseCon((byte) con);
			pc.resetBaseHitup();
			pc.resetBaseDmgup();
			pc.resetBaseAc();
			pc.resetBaseMr();
			pc.resetLevel();
			pc.sendPackets(new S_OwnCharStatus2(pc));
			pc.sendPackets(new S_CharVisualUpdate(pc));
			try {
			pc.save(); 
			}catch(Exception e){}
			L1Teleport.teleport(pc, 32610, 32777, (short)4, 5, true);
        }
    }
	public void statup(L1PcInstance pc){
        int Stathp = 0;
        int Statmp = 0;
        int Statac = 0;
        pc.setLevel(pc.getLevel() + 1);
        Stathp = CalcStat.calcStatHp(pc.getType(), pc.getMaxHp(), pc.getCon());
        Statmp = CalcStat.calcStatMp(pc.getType(), pc.getMaxMp(), pc.getWis());
        Statac = CalcStat.calcAc(pc.getLevel(), pc.getDex());
        pc.setAc(Statac);
        pc.setMaxHp(pc.getMaxHp() + Stathp);
        pc.setMaxMp(pc.getMaxMp() + Statmp);
	}
}
