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

package l1j.server.server.utils;

import java.util.Random;

import l1j.server.Config;

public class CalcStat {

	private static Random rnd = new Random();

	private CalcStat() {

	}

	/**
	 * AC보너스를 돌려준다
	 * 
	 * @param level
	 * @param dex
	 * @return acBonus
	 * 
	 */
	public static int calcAc(int level, int dex) 
	{
	// 버그사용자이다. 보너스 읍다.
	if(dex < 0) return 0;
	int acBonus = 10;

	switch(dex)
	{
	case 0 : case 1 : case 2 : case 3 : case 4 :
	case 5 : case 6 : case 7 : case 8 : case 9 :
	acBonus -= level / 8;
	break;
	case 10 : case 11 : case 12 :
	acBonus -= level / 7;
	break;
	case 13 : case 14 : case 15 :
	acBonus -= level / 6;
	break;
	case 16 : case 17 : 
	acBonus -= level / 5;
	break;
	default :
	acBonus -= level / 4;
	break;
	}
	return acBonus;
	}

	/**
	* 인수의 WIS에 대응하는 MR보너스를 돌려준다
	* 
	* @param wis
	* @return mrBonus
	*/
	public static int calcStatMr(int wis) {
	// 버그 사용자
	if(wis < 0) return 0;
	int mrBonus = 0;
	switch(wis)
	{
	case 0 : case 1 : case 2 : case 3 : case 4 :
	case 5 : case 6 : case 7 : case 8 : case 9 :
	case 10 : case 11 : case 12 : case 13 : case 14 : 
	mrBonus = 0;
	break;

	default :
	case 24 : mrBonus += 3;
	case 23 : mrBonus += 10;
	case 22 : mrBonus += 9;
	case 21 : mrBonus += 7;
	case 20 : mrBonus += 6;
	case 19 : mrBonus += 5;
	case 18 : mrBonus += 4;
	case 17 : mrBonus += 3;
	case 16 :
	case 15 : mrBonus += 3;
	break;
	}

	return mrBonus;
	}

	public static int calcDiffMr(int wis, int diff) {
	return calcStatMr(wis + diff) - calcStatMr(wis);
	}

	/**
	* 각 클래스의 LVUP시의 HP상승치를 돌려준다
	* 
	* @param charType
	* @param baseMaxHp
	* @param baseCon
	* @return HP상승치
	*/
	public static short calcStatHp(int charType, int baseMaxHp, byte baseCon) {
	short randomhp = 0;
	if (baseCon > 15) {
	randomhp = (short) (baseCon - 15);
	}
	switch(charType)
	{
	case 0 : // 군주
	randomhp += (short) (9 + rnd.nextInt(6)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.PRINCE_MAX_HP) 
	{
	randomhp = (short) (Config.PRINCE_MAX_HP - baseMaxHp);
	}
	break;
	case 1 : // 기사
	randomhp += (short) (12 + rnd.nextInt(7)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.KNIGHT_MAX_HP) 
	{
	randomhp = (short) (Config.KNIGHT_MAX_HP - baseMaxHp);
	}
	break;
	case 2 : // 요정
	randomhp += (short) (8 + rnd.nextInt(6)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.ELF_MAX_HP)
	{
	randomhp = (short) (Config.ELF_MAX_HP - baseMaxHp);
	}
	break;
	case 3 : // 법사
	randomhp += (short) (5 + rnd.nextInt(4)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.WIZARD_MAX_HP)
	{
	randomhp = (short) (Config.WIZARD_MAX_HP - baseMaxHp);
	}
	break;
	case 4 : // 다크엘프
	randomhp += (short) (9 + rnd.nextInt(6)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.DARKELF_MAX_HP) 
	{
	randomhp = (short) (Config.DARKELF_MAX_HP - baseMaxHp);
	}
	break;
	case 5 : // 용기사
	randomhp += (short) (10 + rnd.nextInt(6)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.DRAGONKNIGHT_MAX_HP)
	{
	randomhp = (short) (Config.DRAGONKNIGHT_MAX_HP - baseMaxHp);
	}
	break;
	case 6 : // 환술사
	randomhp += (short) (6 + rnd.nextInt(5)); // 초기치분추가

	if (baseMaxHp + randomhp > Config.BLACKWIZARD_MAX_HP)
	{
	randomhp = (short) (Config.BLACKWIZARD_MAX_HP - baseMaxHp);
	}
	break;
	default : // 없는캐릭터
	randomhp = 0;
	break;
	}

	if (randomhp < 0) {
	randomhp = 0;
	}
	return randomhp;
	}

	/**
	* 각 클래스의 LVUP시의 MP상승치를 돌려준다
	* 
	* @param charType
	* @param baseMaxMp
	* @param baseWis
	* @return MP상승치
	*/
	public static short calcStatMp(int charType, int baseMaxMp, byte baseWis) {
	int randommp = 0;
	int seed = 0;
	switch(baseWis)
	{
	case 0 : case 1 : case 2 : case 3 : case 4 :
	case 5 : case 6 : case 7 : case 8 : case 9 :
	seed = -2;
	break;

	default :
	case 25 : seed = 7; break;
	case 24 : seed = 6; break;
	case 23 : case 22 : case 21 : seed = 5; break;
	case 20 : seed = 4; break;
	case 19 : case 18 : seed = 3; break;
	case 17 : case 16 : case 15 : seed = 2; break;
	case 14 : case 13 : case 12 : seed = 0; break;
	case 11 : case 10 : seed = -1; break;
	}

	randommp = 2 + rnd.nextInt(3 + seed % 2 + (seed / 6) * 2) + seed / 2
	- seed / 6;
	switch(charType)
	{
	case 0 :
	if (baseMaxMp + randommp > Config.PRINCE_MAX_MP) 
	{
	randommp = Config.PRINCE_MAX_MP - baseMaxMp;
	}
	break;
	case 1 :
	switch(baseWis)
	{
	case 9 : randommp -= 1; break;
	default : randommp = (int) (1.0 * randommp / 2 + 0.5); break;
	}
	case 2 :
	randommp = (int) (randommp * 1.5);

	if (baseMaxMp + randommp > Config.ELF_MAX_MP) 
	{
	randommp = Config.ELF_MAX_MP - baseMaxMp;
	}
	break;
	case 3 :
	randommp *= 2;

	if (baseMaxMp + randommp > Config.WIZARD_MAX_MP)
	{
	randommp = Config.WIZARD_MAX_MP - baseMaxMp;
	}
	break;
	case 4 :
	randommp = (int) (randommp * 1.5);

	if (baseMaxMp + randommp > Config.DARKELF_MAX_MP) 
	{
	randommp = Config.DARKELF_MAX_MP - baseMaxMp;
	}
	break;
	case 5 :
	randommp = (int) (randommp * 1.5);

	if (baseMaxMp + randommp > Config.DRAGONKNIGHT_MAX_MP)
	{
	randommp = Config.DRAGONKNIGHT_MAX_MP - baseMaxMp;
	}
	break;
	case 6 :
	randommp = (int) (randommp * 1.5);

	if (baseMaxMp + randommp > Config.BLACKWIZARD_MAX_MP) 
	{
	randommp = Config.BLACKWIZARD_MAX_MP - baseMaxMp;
	}
	break;
	default : 
	randommp = 0; 
	break; 
	}

	if (randommp < 0) {
	randommp = 0;
	}
	return (short) randommp;
	}
	}