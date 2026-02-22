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
package l1j.server.server.model.skill;

public class L1SkillId {
	public static final int SKILLS_BEGIN = 1;

	/*
	 * Regular Magic Lv1-10
	 */
	public static final int HEAL = 1; // E: LESSER_HEAL

	public static final int LIGHT = 2;

	public static final int SHIELD = 3;

	public static final int ENERGY_BOLT = 4;

	public static final int TELEPORT = 5;

	public static final int ICE_DAGGER = 6;

	public static final int WIND_CUTTER = 7; // E: WIND_SHURIKEN

	public static final int HOLY_WEAPON = 8;

	public static final int CURE_POISON = 9;

	public static final int CHILL_TOUCH = 10;

	public static final int CURSE_POISON = 11;

	public static final int ENCHANT_WEAPON = 12;

	public static final int DETECTION = 13;

	public static final int DECREASE_WEIGHT = 14;

	public static final int FIRE_ARROW = 15;

	public static final int STALAC = 16;

	public static final int LIGHTNING = 17;

	public static final int TURN_UNDEAD = 18;

	public static final int EXTRA_HEAL = 19; // E: HEAL

	public static final int CURSE_BLIND = 20;

	public static final int BLESSED_ARMOR = 21;

	public static final int FROZEN_CLOUD = 22;

	public static final int WEAK_ELEMENTAL = 23; // E: REVEAL_WEAKNESS

	// none = 24
	public static final int FIREBALL = 25;

	public static final int PHYSICAL_ENCHANT_DEX = 26; // E: ENCHANT_DEXTERITY

	public static final int WEAPON_BREAK = 27;

	public static final int VAMPIRIC_TOUCH = 28;

	public static final int SLOW = 29;

	public static final int EARTH_JAIL = 30;

	public static final int COUNTER_MAGIC = 31;

	public static final int MEDITATION = 32;

	public static final int CURSE_PARALYZE = 33;

	public static final int CALL_LIGHTNING = 34;

	public static final int GREATER_HEAL = 35;

	public static final int TAMING_MONSTER = 36; // E: TAME_MONSTER

	public static final int REMOVE_CURSE = 37;

	public static final int CONE_OF_COLD = 38;

	public static final int MANA_DRAIN = 39;

	public static final int DARKNESS = 40;

	public static final int CREATE_ZOMBIE = 41;

	public static final int PHYSICAL_ENCHANT_STR = 42; // E: ENCHANT_MIGHTY

	public static final int HASTE = 43;

	public static final int CANCELLATION = 44; // E: CANCEL MAGIC

	public static final int ERUPTION = 45;

	public static final int SUNBURST = 46;

	public static final int WEAKNESS = 47;

	public static final int BLESS_WEAPON = 48;

	public static final int HEAL_ALL = 49; // E: HEAL_PLEDGE

	public static final int ICE_LANCE = 50;

	public static final int SUMMON_MONSTER = 51;

	public static final int HOLY_WALK = 52;

	public static final int TORNADO = 53;

	public static final int GREATER_HASTE = 54;

	public static final int BERSERKERS = 55;

	public static final int DISEASE = 56;

	public static final int FULL_HEAL = 57;

	public static final int FIRE_WALL = 58;

	public static final int BLIZZARD = 59;

	public static final int INVISIBILITY = 60;

	public static final int RESURRECTION = 61;

	public static final int EARTHQUAKE = 62;

	public static final int LIFE_STREAM = 63;

	public static final int SILENCE = 64;

	public static final int LIGHTNING_STORM = 65;

	public static final int FOG_OF_SLEEPING = 66;

	public static final int SHAPE_CHANGE = 67; // E: POLYMORPH

	public static final int IMMUNE_TO_HARM = 68;

	public static final int MASS_TELEPORT = 69;

	public static final int FIRE_STORM = 70;

	public static final int DECAY_POTION = 71;

	public static final int COUNTER_DETECTION = 72;

	public static final int CREATE_MAGICAL_WEAPON = 73;

	public static final int METEOR_STRIKE = 74;

	public static final int GREATER_RESURRECTION = 75;

	public static final int MASS_SLOW = 76;

	public static final int DISINTEGRATE = 77; // E: DESTROY

	public static final int ABSOLUTE_BARRIER = 78;

	public static final int ADVANCE_SPIRIT = 79;

	public static final int FREEZING_BLIZZARD = 80;

	// none = 81 - 86
	/*
	 * Knight skills
	 */
	public static final int SHOCK_STUN = 87; // E: STUN_SHOCK

	public static final int REDUCTION_ARMOR = 88;

	public static final int BOUNCE_ATTACK = 89;

	public static final int SOLID_CARRIAGE = 90;

	public static final int COUNTER_BARRIER = 91;

	// none = 92-96
	/*
	 * Dark Spirit Magic
	 */
	public static final int BLIND_HIDING = 97;

	public static final int ENCHANT_VENOM = 98;

	public static final int SHADOW_ARMOR = 99;

	public static final int BRING_STONE = 100;

	public static final int MOVING_ACCELERATION = 101; // E: PURIFY_STONE

	public static final int BURNING_SPIRIT = 102;

	public static final int DARK_BLIND = 103;

	public static final int VENOM_RESIST = 104;

	public static final int DOUBLE_BRAKE = 105;

	public static final int UNCANNY_DODGE = 106;

	public static final int SHADOW_FANG = 107;

	public static final int FINAL_BURN = 108;

	public static final int DRESS_MIGHTY = 109;

	public static final int DRESS_DEXTERITY = 110;

	public static final int DRESS_EVASION = 111;

	// none = 112
	/*
	 * Royal Magic
	 */
	public static final int TRUE_TARGET = 113;

	public static final int GLOWING_AURA = 114;

	public static final int SHINING_AURA = 115;

	public static final int CALL_CLAN = 116; // E: CALL_PLEDGE_MEMBER

	public static final int BRAVE_AURA = 117;

	public static final int RUN_CLAN = 118;

	// unknown = 119 - 120
	// none = 121 - 128
	/*
	 * Spirit Magic
	 */
	public static final int RESIST_MAGIC = 129;

	public static final int BODY_TO_MIND = 130;

	public static final int TELEPORT_TO_MATHER = 131;

	public static final int TRIPLE_ARROW = 132;

	public static final int ELEMENTAL_FALL_DOWN = 133;

	public static final int COUNTER_MIRROR = 134;

	public static final int CLEAR_MIND = 137;

	public static final int RESIST_ELEMENTAL = 138;

	public static final int RETURN_TO_NATURE = 145;

	public static final int BLOODY_SOUL = 146; 

	public static final int ELEMENTAL_PROTECTION = 147; 

	public static final int FIRE_WEAPON = 148;

	public static final int WIND_SHOT = 149;

	public static final int WIND_WALK = 150;

	public static final int EARTH_SKIN = 151;

	public static final int ENTANGLE = 152;

	public static final int ERASE_MAGIC = 153;

	public static final int LESSER_ELEMENTAL = 154; // E:SUMMON_LESSER_ELEMENTAL

	public static final int FIRE_BLESS = 155; // E: BLESS_OF_FIRE

	public static final int STORM_EYE = 156; // E: EYE_OF_STORM

	public static final int EARTH_BIND = 157;

	public static final int NATURES_TOUCH = 158;

	public static final int EARTH_BLESS = 159; // E: BLESS_OF_EARTH

	public static final int AQUA_PROTECTER = 160;

	public static final int AREA_OF_SILENCE = 161;

	public static final int GREATER_ELEMENTAL = 162; // E:SUMMON_GREATER_ELEMENTAL

	public static final int BURNING_WEAPON = 163;

	public static final int NATURES_BLESSING = 164;

	public static final int CALL_OF_NATURE = 165; // E: NATURES_MIRACLE

	public static final int STORM_SHOT = 166;

	public static final int WIND_SHACKLE = 167;

	public static final int IRON_SKIN = 168;

	public static final int EXOTIC_VITALIZE = 169;

	public static final int WATER_LIFE = 170;

	public static final int ELEMENTAL_FIRE = 171;

	public static final int STORM_WALK = 172;

	public static final int POLLUTE_WATER = 173;

	public static final int STRIKER_GALE = 174;

	public static final int SOUL_OF_FLAME = 175;

	public static final int ADDITIONAL_FIRE = 176;

	public static final int SKILLS_END = 176;


	/*
	 *  용기사 스킬
	 */
	public static final int DRAGON_SKIN = 181;

	public static final int BURNING_SLASH = 182;

	public static final int GUARDBREAK = 183;

	public static final int BLOODBREATH = 184;

	public static final int ANTARAS = 185;//변신 6894

	public static final int BLOODLUST = 186;

	public static final int FOURSLAYER = 187;

	public static final int PEAR = 188;

	public static final int SHOCKSKIN = 189;

	public static final int PAPORION = 190;

	public static final int MOTALBODY = 191;

	public static final int THUNDER_GRAB = 192;

	public static final int HOUROFDEATH = 193;

	public static final int FREEZINGOFBREATH = 194;

	public static final int BALAKAS = 195;

	public static final int MIRRORIMG = 201;

	public static final int CONFUSION = 202;

	public static final int SMASH = 203;

	public static final int OUGU = 204;
	
	public static final int CUBE_IGNITION = 205;

	public static final int CONSENTRATION = 206;

	public static final int MINDBREAK = 207;

	public static final int BONEBREAK = 208;

	public static final int RICH = 209;

	public static final int CUBE_QUAKE = 210;

	public static final int PAYTIONS = 211;

	public static final int PANTASM = 212;

	public static final int ARMBREAKER = 213;

	public static final int DIAGOLEM = 214;

	public static final int CUBE_SHOCK = 215;

	public static final int INSIGHT = 216;

	public static final int PANIC = 217;

	public static final int JOYOFPAIN = 218;

	public static final int AVATA = 219;

	public static final int CUBE_BALANCE = 220;
	

	/*
	 * Status
	 */
	public static final int STATUS_BEGIN = 1000;

	public static final int STATUS_BRAVE = 1000;

	public static final int STATUS_HASTE = 1001;

	public static final int STATUS_BLUE_POTION = 1002;

	public static final int STATUS_UNDERWATER_BREATH = 1003;

	public static final int STATUS_WISDOM_POTION = 1004;

	public static final int STATUS_CHAT_PROHIBITED = 1005;

	public static final int STATUS_POISON = 1006;

	public static final int STATUS_POISON_SILENCE = 1007;

	public static final int STATUS_POISON_PARALYZING = 1008;

	public static final int STATUS_POISON_PARALYZED = 1009;

	public static final int STATUS_CURSE_PARALYZING = 1010;

	public static final int STATUS_CURSE_PARALYZED = 1011;

	public static final int STATUS_FLOATING_EYE = 1012;

	public static final int STATUS_HOLY_WATER = 1013;

	public static final int STATUS_HOLY_MITHRIL_POWDER = 1014;

	public static final int STATUS_HOLY_WATER_OF_EVA = 1015;
	
	public static final int ANTI_MAGIC = 1014; // 아인하사드 성수
							 
	public static final int STATUS_XNAKD = 5000; 
	
	public static final int STATUS_END = 5000;
	
	public static final int STATUS_RIBRAVE = 1017;

	public static final int GMSTATUS_BEGIN = 2000;

	public static final int GMSTATUS_INVISIBLE = 2000;

	public static final int GMSTATUS_HPBAR = 2001;

	public static final int GMSTATUS_SHOWTRAPS = 2002;

	public static final int GMSTATUS_END = 2002;

	public static final int COOKING_NOW = 2999;

	public static final int COOKING_BEGIN = 3000;

	public static final int COOKING_1_0_N = 3000;

	public static final int COOKING_1_1_N = 3001;

	public static final int COOKING_1_2_N = 3002;

	public static final int COOKING_1_3_N = 3003;

	public static final int COOKING_1_4_N = 3004;

	public static final int COOKING_1_5_N = 3005;

	public static final int COOKING_1_6_N = 3006;

	public static final int COOKING_1_7_N = 3007;
	
	public static final int COOKING_1_0_S = 3008;

	public static final int COOKING_1_1_S = 3009;

	public static final int COOKING_1_2_S = 3010;

	public static final int COOKING_1_3_S = 3011;

	public static final int COOKING_1_4_S = 3012;

	public static final int COOKING_1_5_S = 3013;

	public static final int COOKING_1_6_S = 3014;

	public static final int COOKING_1_7_S = 3015;

	public static final int COOKING_2_0_N = 3016;

	public static final int COOKING_2_1_N = 3017;

	public static final int COOKING_2_2_N = 3018;

	public static final int COOKING_2_3_N = 3019;

	public static final int COOKING_2_4_N = 3020;

	public static final int COOKING_2_5_N = 3021;

	public static final int COOKING_2_6_N = 3022;

	public static final int COOKING_2_7_N = 3023;

	public static final int COOKING_2_0_S = 3024;

	public static final int COOKING_2_1_S = 3025;

	public static final int COOKING_2_2_S = 3026;

	public static final int COOKING_2_3_S = 3027;

	public static final int COOKING_2_4_S = 3028;

	public static final int COOKING_2_5_S = 3029;

	public static final int COOKING_2_6_S = 3030;

	public static final int COOKING_2_7_S = 3031;

	public static final int COOKING_3_0_N = 3032;

	public static final int COOKING_3_1_N = 3033;

	public static final int COOKING_3_2_N = 3034;

	public static final int COOKING_3_3_N = 3035;

	public static final int COOKING_3_4_N = 3036;

	public static final int COOKING_3_5_N = 3037;

	public static final int COOKING_3_6_N = 3038;

	public static final int COOKING_3_7_N = 3039;

	public static final int COOKING_3_0_S = 3040;

	public static final int COOKING_3_1_S = 3041;

	public static final int COOKING_3_2_S = 3042;

	public static final int COOKING_3_3_S = 3043;

	public static final int COOKING_3_4_S = 3044;

	public static final int COOKING_3_5_S = 3045;

	public static final int COOKING_3_6_S = 3046;

	public static final int COOKING_3_7_S = 3047;
	
	public static final int COOKING_1_8_N = 3048;
	
	public static final int COOKING_1_8_S = 3049;
	
	public static final int COOKING_1_9_N = 3050;
	
	public static final int COOKING_1_9_S = 3051;
	
	public static final int COOKING_1_10_N = 3052;
	
	public static final int COOKING_1_10_S = 3053;
	
	public static final int COOKING_1_11_N = 3054;
	
	public static final int COOKING_1_11_S = 3055;
	
	public static final int COOKING_1_12_N = 3056;
	
	public static final int COOKING_1_12_S = 3057;
	
	public static final int COOKING_1_13_N = 3058;
	
	public static final int COOKING_1_13_S = 3059;
	
	public static final int COOKING_1_14_N = 3060;
	
	public static final int COOKING_1_14_S = 3061;
	
	public static final int COOKING_1_15_N = 3062;
	
	public static final int COOKING_1_15_S = 3063;
	
	public static final int COOKING_1_16_N = 3064;
	
	public static final int COOKING_1_16_S = 3065;
	
	public static final int COOKING_1_17_N = 3066;
	
	public static final int COOKING_1_17_S = 3067;
	
	public static final int COOKING_1_18_N = 3068;  

	public static final int COOKING_1_18_S = 3069;
	
	public static final int COOKING_1_19_N = 3070;
	
	public static final int COOKING_1_19_S = 3071;
	
	public static final int COOKING_1_20_N = 3072; 

	public static final int COOKING_1_20_S = 3073;
	
	public static final int COOKING_1_21_N = 3074;
	
	public static final int COOKING_1_21_S = 3075;
	
	public static final int COOKING_1_22_N = 3076;
	
	public static final int COOKING_1_22_S = 3077;
	
	public static final int COOKING_1_23_N = 3078;
	
	public static final int COOKING_1_23_S = 3079;
	
	public static final int COOKING_1_24_S = 3080;

	public static final int COOKING_END = 3047;

	public static final int STATUS_FREEZE = 10071;	

    public static final int CURSE_PARALYZE2 = 10101;

	public static final int ChinSword1 = 4899;
	
	public static final int ChinSword2 = 4903;

    public static final int ChinSword3 = 741;
    
    public static final int MAGE_DISEASE = 1016;	 // 아크메이지의 지팡이

    public static final int STATUS_CURSE_BARLOG = 1015;

	public static final int STATUS_CURSE_YAHEE = 1014;
	
	 /** 용기사 약점 노출 */
	public static final int SPOT1 = 4001;		// 1단계
	 
	public static final int SPOT2 = 4002;		// 2단계
	 
	public static final int SPOT3 = 4003;		// 3단계  

	public static final int COLOR_A = 6993; // 추가

    public static final int COLOR_B = 6994; // 추가

    public static final int COLOR_C = 6995; // 추가
    
    public static final int EXP_POTION = 7013;   // 천상의 물약

    public static final int COMA = 7382; //코마버프

    public static final int COMABUFF = 7383; //코마버프 2

	public static final int SANGA = 7322; //상아탑 버프
 
    public static final int SANGABUFF = 7323; //상아탑 버프
    
	public static final int CRAY = 7681; //크레이 버프 
	 
    public static final int ANTA_BLOOD = 7783; //혈흔 버프 
    
    public static final int FAFU_MAAN = 7676;	// 수룡의 마안
    
 	public static final int ANTA_MAAN = 7672;	// 지룡의 마안
	
 	public static final int LIND_MAAN = 7671;	// 풍룡의 마안
	
 	public static final int VALA_MAAN = 7673;	// 화룡의 마안
	
 	public static final int LIFE_MAAN = 7674;	// 생명의 마안
	
 	public static final int BIRTH_MAAN = 7678;	// 탄생의 마안
	
 	public static final int SHAPE_MAAN = 7675;	// 형상의 마안
 	
 	public static final int LUCK_A = 7947; //운세버프시작
 	 
 	public static final int LUCK_B = 7948;
 	  
 	public static final int LUCK_C = 7949;
 	  
 	public static final int LUCK_D = 7950;
    
    public static final int AntiMagic = 10072;  // 안티매직포션

//////////////////////////////////////////////////////몬스터에게 모션이 들어가기 위해 추가 - 몹스킬패턴 추가 : 시작
	public static final int Mob_SLOW_1 = 30060;	
	//슬로우 1번모션
	public static final int Mob_SLOW_18 = 30077;	
	//슬로우 18번모션
	public static final int Mob_WEAKNESS_1 = 30078;	
	//위크니스 1번모션
	public static final int Mob_DISEASE_1 = 30079;	
	//디지즈 1번모션
	public static final int Mob_Basill = 30011;	
	//바실리스크 얼리기에볼
	public static final int Mob_SHOCKSTUN_18 = 30081;
	//쇼크스턴 18번모션
	public static final int Mob_RANGESTUN_19 = 30082;
	//범위스턴 19번모션
	public static final int Mob_RANGESTUN_18 = 30083;
	//범위스턴 18번모션
	public static final int Mob_RANGESTUN_30 = 20167;
	//범위스턴 30번모션
	public static final int Mob_RANGESTUN_20 = 30104; 
	//범위스턴 20번모션
	public static final int Mob_DISEASE_30 = 30080;	
	//디지즈 30번모션
	public static final int Mob_WINDSHACKLE_1 = 30084;	
	//윈드셰클 1번모션
	public static final int Mob_Coca = 30010;	
	//코카트리스 얼리기공격
	public static final int Mob_CURSEPARALYZ_19 = 30085;
	//커스 19번모션
	public static final int Mob_CURSEPARALYZ_18 = 30086;
	//커스 18번모션
	public static final int Mob_CURSEPARALYZ_30 = 30096;
	//커스 30번모션
	public static final int Mob_CURSEPARALYZ_SHORT_18 = 30101;
	//커스 켄성메두사용 18번모션
	public static final int Mob_VAMPIRIC_TOUCH_1 = 30102;
	//뱀파이어릭터치 1번모션
	public static final int Mob_AREA_ICE_LANCE = 20315;
	//파푸리온 범위얼리기 18번모션
	public static final int Mob_AREA_FIRE_WALL = 20294;	
	//발라카스 범위파이어월 18번모션
	public static final int Mob_AREA_POISON_18 = 30007;	
	//범위 독구름 18번모션
	public static final int Mob_AREA_POISON_30 = 20293;	
	//범위 독구름 30번모션
	public static final int Mob_AREA_POISON = 20292;
	//안타라스 범위독구름 18번모션
	public static final int Mob_AREA_POISON_20 = 10152;	
	//범위 독구름 20번모션 5뭉치
	public static final int Mob_CURSPOISON_30 = 30088;
	//커스 포이즌 30번모션
	public static final int Mob_CURSPOISON_18 = 10012;	
	//커스 포이즌 18번모션
	public static final int Mob_CALL_LIGHTNING_ICE = 20158;	
	//제브 레퀴(남) 콜라이트닝(얼리기) 1번 모션
	public static final int Mob_AREA_CANCELLATION_19 = 10153;
	//캔슬레이션 19번모션
	public static final int Mob_AREA_EARTHQUAKE = 30106;
	// 안타라스(리뉴얼) 용언 11종
	public static final int ANTA_SKILL_1 = 10188;
	
	public static final int ANTA_SKILL_2 = 10189;
	
	public static final int ANTA_SKILL_3 = 10190;
	
	public static final int ANTA_SKILL_4 = 10191;
	
	public static final int ANTA_SKILL_5 = 10192;
	
	public static final int ANTA_SKILL_6 = 10193;
	
	public static final int ANTA_SKILL_7 = 10194;
	
	public static final int ANTA_SKILL_8 = 10195;
	
	public static final int ANTA_SKILL_9 = 10196;
	
	public static final int ANTA_SKILL_10 = 10197;
	/////////////////////////////////////////////////////// 몬스터에게 모션이 들어가기 위해 추가 - 몹스킬패턴 추가 : 끝
}
