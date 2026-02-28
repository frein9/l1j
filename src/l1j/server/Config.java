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
package l1j.server;

import l1j.server.server.utils.IntRange;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Config {

    private static final Logger _log = Logger.getLogger(Config.class.getName());

    public static boolean 일반 = false;//매니저 때문에 추가부분
    public static boolean 귓속말 = false;
    public static boolean 글로벌 = false;
    public static boolean 혈맹 = false;
    public static boolean 파티 = false;
    public static boolean 장사 = false;//여기까지.
    public static boolean 외침 = false;
    public static boolean 채팅파티 = false; // 채팅파티 서버매니저 추가

    /**
     * 매크로
     **/

    //카시
    public static int systime;
    public static String sys1;
    public static String sys2;
    public static String sys3;
    public static String sys4;
    public static String sys5;
    public static String sys6;
    public static String sys7;

    /** 매크로 끝 **/


    /**
     * Debug/release mode
     */
    public static final boolean DEBUG = false;

    /**
     * Thread pools size
     */
    public static int THREAD_P_EFFECTS;

    public static int THREAD_P_GENERAL;

    public static int AI_MAX_THREAD;

    public static int THREAD_P_TYPE_GENERAL;

    public static int THREAD_P_SIZE_GENERAL;

    /**
     * Server control
     */
    public static String GAME_SERVER_HOST_NAME;

    public static int GAME_SERVER_PORT;

    public static String DB_DRIVER;

    public static String DB_URL;

    public static String DB_LOGIN;

    public static String DB_PASSWORD;

    public static String DBMS;

    public static String FILE_DB_PATH;

    public static boolean FILE_DB_AUTO_INIT;

    public static String TIME_ZONE;

    public static int CLIENT_LANGUAGE;

    public static boolean HOSTNAME_LOOKUPS;

    public static int AUTOMATIC_KICK;

    public static boolean AUTO_CREATE_ACCOUNTS;

    public static short MAX_ONLINE_USERS;

    public static boolean CACHE_MAP_FILES;

    public static boolean LOAD_V2_MAP_FILES;

    public static boolean CHECK_MOVE_INTERVAL;

    public static boolean CHECK_ATTACK_INTERVAL;

    public static boolean CHECK_SPELL_INTERVAL;

    public static short INJUSTICE_COUNT;

    public static int JUSTICE_COUNT;

    public static int CHECK_STRICTNESS;

    public static byte LOGGING_WEAPON_ENCHANT;

    public static byte LOGGING_ARMOR_ENCHANT;

    public static boolean LOGGING_CHAT_NORMAL;

    public static boolean LOGGING_CHAT_WHISPER;

    public static boolean LOGGING_CHAT_SHOUT;

    public static boolean LOGGING_CHAT_WORLD;

    public static boolean LOGGING_CHAT_CLAN;

    public static boolean LOGGING_CHAT_PARTY;

    public static boolean LOGGING_CHAT_COMBINED;

    public static boolean LOGGING_CHAT_CHAT_PARTY;

    public static int AUTOSAVE_INTERVAL;

    public static int AUTOSAVE_INTERVAL_INVENTORY;

    public static int SKILLTIMER_IMPLTYPE;

    public static int NPCAI_IMPLTYPE;

    public static boolean TELNET_SERVER;

    public static int TELNET_SERVER_PORT;

    public static int PC_RECOGNIZE_RANGE;

    public static boolean CHARACTER_CONFIG_IN_SERVER_SIDE;

    public static boolean ALLOW_2PC;

    public static int LEVEL_DOWN_RANGE;

    public static boolean SEND_PACKET_BEFORE_TELEPORT;

    public static boolean DETECT_DB_RESOURCE_LEAKS;

    public static int ACCOUNT_LIMIT; // IP당 계정 생성 개수 외부화

    /**
     * Rate control
     */
    public static double RATE_XP;

    public static double RATE_PET_XP; // ########## A137 펫 경험치 배율 설정 외부화 [넬]

    public static double RATE_CCLAN_XP;
    /// 성혈경험치 외부화

    public static double RATE_LA;

    public static double RATE_KARMA;

    public static double RATE_DROP_ADENA;

    public static double RATE_DROP_ITEMS;

    public static int ENCHANT_CHANCE_WEAPON;

    public static int ENCHANT_CHANCE_ARMOR;

    public static int MAX_WEAPON_ENCHANT;  // 무기 인챈 외부화

    public static int MAX_ARMOR_ENCHANT;   //방어구 인챈 외부화
    //장신구업그레이드 By추억
    public static int UPACSE_CHANCE;

    public static double RATE_WEIGHT_LIMIT;

    public static double RATE_WEIGHT_LIMIT_PET;

    public static double RATE_SHOP_SELLING_PRICE;

    public static double RATE_SHOP_PURCHASING_PRICE;

    public static int CREATE_CHANCE_DIARY;

    public static int CREATE_CHANCE_RECOLLECTION;

    public static int CREATE_CHANCE_MYSTERIOUS;

    public static int CREATE_CHANCE_PROCESSING;

    public static int CREATE_CHANCE_PROCESSING_DIAMOND;

    public static int CREATE_CHANCE_DANTES;

    public static int CREATE_CHANCE_ANCIENT_AMULET;

    public static int CREATE_CHANCE_HISTORY_BOOK;

    public static int RATE_PRIMIUM_TIME; // 프리미엄 상인

    public static int RATE_PRIMIUM_NUMBER; // 프리미엄 상인

    public static int RATE_AIN_TIME; // 아인하사드 축복 시간

    public static int RATE_AIN_OUTTIME;

    public static int RATE_BUGRACE_TIME; //버경

    public static int RATE_EITEM;  //모든 몹 아이템 드랍

    /**
     * AltSettings control
     */

    public static short GLOBAL_CHAT_LEVEL;

    public static short WHISPER_CHAT_LEVEL;

    public static byte AUTO_LOOT;

    public static int LOOTING_RANGE;

    public static boolean ALT_NONPVP;

    public static boolean ALT_ATKMSG;

    public static boolean CHANGE_TITLE_BY_ONESELF;

    public static int MAX_CLAN_MEMBER;

    public static boolean CLAN_ALLIANCE;

    public static int MAX_PT;

    public static int MAX_CHAT_PT;

    public static boolean SIM_WAR_PENALTY;

    public static boolean GET_BACK;

    public static String ALT_ITEM_DELETION_TYPE;

    public static int ALT_ITEM_DELETION_TIME;

    public static int ALT_ITEM_DELETION_RANGE;

    public static boolean ALT_GMSHOP;

    public static int ALT_GMSHOP_MIN_ID;

    public static int ALT_GMSHOP_MAX_ID;

    public static boolean ALT_HALLOWEENIVENT;

    public static boolean ALT_TALKINGSCROLLQUEST;

    public static int WHOIS_CONTER; // #### 뻥튀기 외부화(아우라) ####

    public static boolean ALT_WHO_COMMAND;

    public static boolean ALT_REVIVAL_POTION;

    public static int ALT_WAR_TIME;

    public static int ALT_WAR_TIME_UNIT;

    public static int ALT_WAR_INTERVAL;

    public static int ALT_WAR_INTERVAL_UNIT;

    public static int ALT_RATE_OF_DUTY;

    public static boolean SPAWN_HOME_POINT;

    public static int SPAWN_HOME_POINT_RANGE;

    public static int SPAWN_HOME_POINT_COUNT;

    public static int SPAWN_HOME_POINT_DELAY;

    public static boolean INIT_BOSS_SPAWN;

    public static int ELEMENTAL_STONE_AMOUNT;

    public static int HOUSE_TAX_INTERVAL;

    public static int MAX_DOLL_COUNT;

    public static boolean RETURN_TO_NATURE;

    public static int MAX_NPC_ITEM;

    public static int MAX_PERSONAL_WAREHOUSE_ITEM;

    public static int MAX_CLAN_WAREHOUSE_ITEM;

    public static boolean DELETE_CHARACTER_AFTER_7DAYS;

    public static int BUFF_PRICE;

    public static int Quest_Yes;

    public static int Quest_No;

    public static int NPC_DELETION_TIME;

    /**
     * CharSettings control
     */
    public static int PRINCE_MAX_HP;

    public static int PRINCE_MAX_MP;

    public static int KNIGHT_MAX_HP;

    public static int KNIGHT_MAX_MP;

    public static int ELF_MAX_HP;

    public static int ELF_MAX_MP;

    public static int WIZARD_MAX_HP;

    public static int WIZARD_MAX_MP;

    public static int DARKELF_MAX_HP;

    public static int DARKELF_MAX_MP;

    public static int DRAGONKNIGHT_MAX_HP;

    public static int DRAGONKNIGHT_MAX_MP;

    public static int BLACKWIZARD_MAX_HP;

    public static int BLACKWIZARD_MAX_MP;

    public static int LV1_EXP;

    public static int LV2_EXP;

    public static int LV3_EXP;

    public static int LV4_EXP;

    public static int LV5_EXP;

    public static int LV6_EXP;

    public static int LV7_EXP;

    public static int LV8_EXP;

    public static int LV9_EXP;

    public static int LV10_EXP;

    public static int LV11_EXP;

    public static int LV12_EXP;

    public static int LV13_EXP;

    public static int LV14_EXP;

    public static int LV15_EXP;

    public static int LV16_EXP;

    public static int LV17_EXP;

    public static int LV18_EXP;

    public static int LV19_EXP;

    public static int LV20_EXP;

    public static int LV21_EXP;

    public static int LV22_EXP;

    public static int LV23_EXP;

    public static int LV24_EXP;

    public static int LV25_EXP;

    public static int LV26_EXP;

    public static int LV27_EXP;

    public static int LV28_EXP;

    public static int LV29_EXP;

    public static int LV30_EXP;

    public static int LV31_EXP;

    public static int LV32_EXP;

    public static int LV33_EXP;

    public static int LV34_EXP;

    public static int LV35_EXP;

    public static int LV36_EXP;

    public static int LV37_EXP;

    public static int LV38_EXP;

    public static int LV39_EXP;

    public static int LV40_EXP;

    public static int LV41_EXP;

    public static int LV42_EXP;

    public static int LV43_EXP;

    public static int LV44_EXP;

    public static int LV45_EXP;

    public static int LV46_EXP;

    public static int LV47_EXP;

    public static int LV48_EXP;

    public static int LV49_EXP;

    public static int LV50_EXP;

    public static int LV51_EXP;

    public static int LV52_EXP;

    public static int LV53_EXP;

    public static int LV54_EXP;

    public static int LV55_EXP;

    public static int LV56_EXP;

    public static int LV57_EXP;

    public static int LV58_EXP;

    public static int LV59_EXP;

    public static int LV60_EXP;

    public static int LV61_EXP;

    public static int LV62_EXP;

    public static int LV63_EXP;

    public static int LV64_EXP;

    public static int LV65_EXP;

    public static int LV66_EXP;

    public static int LV67_EXP;

    public static int LV68_EXP;

    public static int LV69_EXP;

    public static int LV70_EXP;

    public static int LV71_EXP;

    public static int LV72_EXP;

    public static int LV73_EXP;

    public static int LV74_EXP;

    public static int LV75_EXP;

    public static int LV76_EXP;

    public static int LV77_EXP;

    public static int LV78_EXP;

    public static int LV79_EXP;

    public static int LV80_EXP;

    public static int LV81_EXP;

    public static int LV82_EXP;

    public static int LV83_EXP;

    public static int LV84_EXP;

    public static int LV85_EXP;

    public static int LV86_EXP;

    public static int LV87_EXP;

    public static int LV88_EXP;

    public static int LV89_EXP;

    public static int LV90_EXP;

    public static int LV91_EXP;

    public static int LV92_EXP;

    public static int LV93_EXP;

    public static int LV94_EXP;

    public static int LV95_EXP;

    public static int LV96_EXP;

    public static int LV97_EXP;

    public static int LV98_EXP;

    public static int LV99_EXP;

    /**
     * Configuration files
     */
    public static final String SERVER_CONFIG_FILE = "./config/server.properties";

    public static final String RATES_CONFIG_FILE = "./config/rates.properties";

    public static final String ALT_SETTINGS_FILE = "./config/altsettings.properties";

    public static final String CHAR_SETTINGS_CONFIG_FILE = "./config/charsettings.properties";

    /**
     * 그 외의 설정
     */

    // NPC로부터 들이마실 수 있는 MP한계
    public static final int MANA_DRAIN_LIMIT_PER_NPC = 40;

    // 1회의 공격으로 들이마실 수 있는 MP한계(SOM, 강철 SOM)
    public static final int MANA_DRAIN_LIMIT_PER_SOM_ATTACK = 9;

    // NPC로부터 들이마실 수 있는 HP한계 // (파멸의 대검)
    public static final int HP_DRAIN_LIMIT_PER_NPC = 30;

    // 1회의 공격으로 들이마실 수 있는 HP한계 (파멸의 대검)
    public static final int HP_DRAIN_LIMIT_PER_HP_ATTACK = 15;

    public static void load() {
        _log.info("loading gameserver config");
        // server.properties
        try {
            Properties serverSettings = new Properties();
            InputStream is = new FileInputStream(new File(SERVER_CONFIG_FILE));
            serverSettings.load(is);
            is.close();

            GAME_SERVER_HOST_NAME = serverSettings.getProperty("GameserverHostname", "*");
            GAME_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("GameserverPort", "2000"));
            DBMS = serverSettings.getProperty("DBMS", "mysql").trim().toLowerCase();
            FILE_DB_PATH = serverSettings.getProperty("FileDBPath", "./data/filedb/l1jdb").trim();
            FILE_DB_AUTO_INIT = Boolean.parseBoolean(serverSettings.getProperty("FileDBAutoInit", "true"));

            if ("filedb".equals(DBMS)) {
                DB_DRIVER = "org.h2.Driver";
                DB_URL = buildFileDbUrl(FILE_DB_PATH);
                DB_LOGIN = serverSettings.getProperty("FileDBLogin", "sa");
                DB_PASSWORD = serverSettings.getProperty("FileDBPassword", "");
            } else {
                DB_DRIVER = serverSettings.getProperty("Driver", "com.mysql.jdbc.Driver");
                DB_URL = serverSettings.getProperty("URL", "jdbc:mysql://localhost/l1jdb? useUnicode=true&characterEncoding=euckr");
                DB_LOGIN = serverSettings.getProperty("Login", "root");
                DB_PASSWORD = serverSettings.getProperty("Password", "");
            }
            THREAD_P_TYPE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolType", "0"), 10);
            THREAD_P_SIZE_GENERAL = Integer.parseInt(serverSettings.getProperty("GeneralThreadPoolSize", "0"), 10);
            CLIENT_LANGUAGE = Integer.parseInt(serverSettings.getProperty("ClientLanguage", "0"));
            TIME_ZONE = serverSettings.getProperty("TimeZone", "KST");
            HOSTNAME_LOOKUPS = Boolean.parseBoolean(serverSettings.getProperty("HostnameLookups", "false"));
            AUTOMATIC_KICK = Integer.parseInt(serverSettings.getProperty("AutomaticKick", "10"));
            AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(serverSettings.getProperty("AutoCreateAccounts", "true"));
            MAX_ONLINE_USERS = Short.parseShort(serverSettings.getProperty("MaximumOnlineUsers", "30"));
            CACHE_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("CacheMapFiles", "false"));
            LOAD_V2_MAP_FILES = Boolean.parseBoolean(serverSettings.getProperty("LoadV2MapFiles", "false"));
            CHECK_MOVE_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckMoveInterval", "false"));
            CHECK_ATTACK_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckAttackInterval", "false"));
            CHECK_SPELL_INTERVAL = Boolean.parseBoolean(serverSettings.getProperty("CheckSpellInterval", "false"));
            INJUSTICE_COUNT = Short.parseShort(serverSettings.getProperty("InjusticeCount", "10"));
            JUSTICE_COUNT = Integer.parseInt(serverSettings.getProperty("JusticeCount", "4"));
            CHECK_STRICTNESS = Integer.parseInt(serverSettings.getProperty("CheckStrictness", "102"));
            LOGGING_WEAPON_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingWeaponEnchant", "0"));
            LOGGING_ARMOR_ENCHANT = Byte.parseByte(serverSettings.getProperty("LoggingArmorEnchant", "0"));
            LOGGING_CHAT_NORMAL = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatNormal", "false"));
            LOGGING_CHAT_WHISPER = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWhisper", "false"));
            LOGGING_CHAT_SHOUT = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatShout", "false"));
            LOGGING_CHAT_WORLD = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatWorld", "false"));
            LOGGING_CHAT_CLAN = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatClan", "false"));
            LOGGING_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatParty", "false"));
            LOGGING_CHAT_COMBINED = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatCombined", "false"));
            LOGGING_CHAT_CHAT_PARTY = Boolean.parseBoolean(serverSettings.getProperty("LoggingChatChatParty", "false"));
            AUTOSAVE_INTERVAL = Integer.parseInt(serverSettings.getProperty("AutosaveInterval", "1200"), 10);
            AUTOSAVE_INTERVAL_INVENTORY = Integer.parseInt(serverSettings.getProperty("AutosaveIntervalOfInventory", "300"), 10);
            SKILLTIMER_IMPLTYPE = Integer.parseInt(serverSettings.getProperty("SkillTimerImplType", "1"));
            NPCAI_IMPLTYPE = Integer.parseInt(serverSettings.getProperty("NpcAIImplType", "1"));
            TELNET_SERVER = Boolean.parseBoolean(serverSettings.getProperty("TelnetServer", "false"));
            TELNET_SERVER_PORT = Integer.parseInt(serverSettings.getProperty("TelnetServerPort", "23"));
            PC_RECOGNIZE_RANGE = Integer.parseInt(serverSettings.getProperty("PcRecognizeRange", "20"));
            CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(serverSettings.getProperty("CharacterConfigInServerSide", "true"));
            ALLOW_2PC = Boolean.parseBoolean(serverSettings.getProperty("Allow2PC", "true"));
            LEVEL_DOWN_RANGE = Integer.parseInt(serverSettings.getProperty("LevelDownRange", "0"));
            SEND_PACKET_BEFORE_TELEPORT = Boolean.parseBoolean(serverSettings.getProperty("SendPacketBeforeTeleport", "false"));
            DETECT_DB_RESOURCE_LEAKS = Boolean.parseBoolean(serverSettings.getProperty("EnableDatabaseResourceLeaksDetection", "false"));
            ACCOUNT_LIMIT = Integer.parseInt(serverSettings.getProperty("AccountLimit", "1"));

        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + SERVER_CONFIG_FILE + " File.");
        }

        // rates.properties
        try {
            Properties rateSettings = new Properties();
            //InputStream is = new FileInputStream(new File(RATES_CONFIG_FILE));
            FileReader is = new FileReader(new File(RATES_CONFIG_FILE));
            rateSettings.load(is);
            is.close();

            systime = Integer.parseInt(rateSettings.getProperty("systime", "6000"));
            sys1 = rateSettings.getProperty("sys1", "게임마스터나 당사 운영진을 사칭하여 게임 내에서 비밀번호/계정 정보 등을 요구하는 플레이어는 E-Mail로 제보해 주시기 바랍니다.");
            sys2 = rateSettings.getProperty("sys2", ";;");
            sys3 = rateSettings.getProperty("sys3", ";;");
            sys4 = rateSettings.getProperty("sys4", ";;");
            sys5 = rateSettings.getProperty("sys5", ";;");
            sys6 = rateSettings.getProperty("sys6", ";;");
            sys7 = rateSettings.getProperty("sys7", ";;");

            RATE_XP = Double.parseDouble(rateSettings.getProperty("RateXp", "1.0"));
            RATE_PET_XP = Double.parseDouble(rateSettings.getProperty("RatePetXp", "1.0")); // #####
            RATE_CCLAN_XP = Double.parseDouble(rateSettings.getProperty("RateCclanXp", "1.0"));
            RATE_LA = Double.parseDouble(rateSettings.getProperty("RateLawful", "1.0"));
            RATE_KARMA = Double.parseDouble(rateSettings.getProperty("RateKarma", "1.0"));
            RATE_DROP_ADENA = Double.parseDouble(rateSettings.getProperty("RateDropAdena", "1.0"));
            RATE_DROP_ITEMS = Double.parseDouble(rateSettings.getProperty("RateDropItems", "1.0"));
            ENCHANT_CHANCE_WEAPON = Integer.parseInt(rateSettings.getProperty("EnchantChanceWeapon", "68"));
            ENCHANT_CHANCE_ARMOR = Integer.parseInt(rateSettings.getProperty("EnchantChanceArmor", "52"));
            MAX_WEAPON_ENCHANT = Integer.parseInt(rateSettings.getProperty("Maxweaponenchant", "13"));
            MAX_ARMOR_ENCHANT = Integer.parseInt(rateSettings.getProperty("Maxarmorenchant", "11"));
            //장신구업그레이드 By추억
            UPACSE_CHANCE = Integer.parseInt(rateSettings.getProperty("UpAcseChance", "1"));
            RATE_BUGRACE_TIME = Integer.parseInt(rateSettings.getProperty("RateBugRaceTime", "1"));
            RATE_WEIGHT_LIMIT = Double.parseDouble(rateSettings.getProperty("RateWeightLimit", "1"));
            RATE_WEIGHT_LIMIT_PET = Double.parseDouble(rateSettings.getProperty("RateWeightLimitforPet", "1"));
            RATE_SHOP_SELLING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopSellingPrice", "1.0"));
            RATE_SHOP_PURCHASING_PRICE = Double.parseDouble(rateSettings.getProperty("RateShopPurchasingPrice", "1.0"));
            RATE_EITEM = Integer.parseInt(rateSettings.getProperty("Rateeitem", "1"));
            CREATE_CHANCE_DIARY = Integer.parseInt(rateSettings.getProperty("CreateChanceDiary", "33"));
            CREATE_CHANCE_RECOLLECTION = Integer.parseInt(rateSettings.getProperty("CreateChanceRecollection", "90"));
            CREATE_CHANCE_MYSTERIOUS = Integer.parseInt(rateSettings.getProperty("CreateChanceMysterious", "90"));
            CREATE_CHANCE_PROCESSING = Integer.parseInt(rateSettings.getProperty("CreateChanceProcessing", "90"));
            CREATE_CHANCE_PROCESSING_DIAMOND = Integer.parseInt(rateSettings.getProperty("CreateChanceProcessingDiamond", "90"));
            CREATE_CHANCE_DANTES = Integer.parseInt(rateSettings.getProperty("CreateChanceDantes", "50"));
            CREATE_CHANCE_ANCIENT_AMULET = Integer.parseInt(rateSettings.getProperty("CreateChanceAncientAmulet", "90"));
            CREATE_CHANCE_HISTORY_BOOK = Integer.parseInt(rateSettings.getProperty("CreateChanceHistoryBook", "50"));
            RATE_PRIMIUM_TIME = Integer.parseInt(rateSettings.getProperty("RatePrimiumTime", "1"));
            RATE_PRIMIUM_NUMBER = Integer.parseInt(rateSettings.getProperty("RatePrimiumNumber", "2"));
            RATE_AIN_TIME = Integer.parseInt(rateSettings.getProperty("RateAinTime", "1"));
            RATE_AIN_OUTTIME = Integer.parseInt(rateSettings.getProperty("RateAinOutTime", "1"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + RATES_CONFIG_FILE + " File.");
        }

        // altsettings.properties
        try {
            Properties altSettings = new Properties();
            InputStream is = new FileInputStream(new File(ALT_SETTINGS_FILE));
            altSettings.load(is);
            is.close();

            GLOBAL_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("GlobalChatLevel", "30"));
            WHISPER_CHAT_LEVEL = Short.parseShort(altSettings.getProperty("WhisperChatLevel", "5"));
            AUTO_LOOT = Byte.parseByte(altSettings.getProperty("AutoLoot", "2"));
            LOOTING_RANGE = Integer.parseInt(altSettings.getProperty("LootingRange", "3"));
            ALT_NONPVP = Boolean.parseBoolean(altSettings.getProperty("NonPvP", "true"));
            ALT_ATKMSG = Boolean.parseBoolean(altSettings.getProperty("AttackMessageOn", "true"));
            CHANGE_TITLE_BY_ONESELF = Boolean.parseBoolean(altSettings.getProperty("ChangeTitleByOneself", "false"));
            MAX_CLAN_MEMBER = Integer.parseInt(altSettings.getProperty("MaxClanMember", "0"));
            CLAN_ALLIANCE = Boolean.parseBoolean(altSettings.getProperty("ClanAlliance", "true"));
            MAX_PT = Integer.parseInt(altSettings.getProperty("MaxPT", "8"));
            MAX_CHAT_PT = Integer.parseInt(altSettings.getProperty("MaxChatPT", "8"));
            SIM_WAR_PENALTY = Boolean.parseBoolean(altSettings.getProperty("SimWarPenalty", "true"));
            GET_BACK = Boolean.parseBoolean(altSettings.getProperty("GetBack", "false"));
            ALT_ITEM_DELETION_TYPE = altSettings.getProperty("ItemDeletionType", "auto");
            ALT_ITEM_DELETION_TIME = Integer.parseInt(altSettings.getProperty("ItemDeletionTime", "10"));
            ALT_ITEM_DELETION_RANGE = Integer.parseInt(altSettings.getProperty("ItemDeletionRange", "5"));
            ALT_GMSHOP = Boolean.parseBoolean(altSettings.getProperty("GMshop", "false"));
            ALT_GMSHOP_MIN_ID = Integer.parseInt(altSettings.getProperty("GMshopMinID", "0xffffffff")); // 취득 실패시는 무효
            ALT_GMSHOP_MAX_ID = Integer.parseInt(altSettings.getProperty("GMshopMaxID", "0xffffffff")); // 취득 실패시는 무효
            ALT_HALLOWEENIVENT = Boolean.parseBoolean(altSettings.getProperty("HalloweenIvent", "true"));
            ALT_TALKINGSCROLLQUEST = Boolean.parseBoolean(altSettings.getProperty("TalkingScrollQuest", "false"));
            WHOIS_CONTER = Integer.parseInt(altSettings.getProperty("WhoisConter", "0")); // #### 뻥튀기 외부화(아우라) ####
            ALT_WHO_COMMAND = Boolean.parseBoolean(altSettings.getProperty("WhoCommand", "false"));
            ALT_REVIVAL_POTION = Boolean.parseBoolean(altSettings.getProperty("RevivalPotion", "false"));
            String strWar;
            strWar = altSettings.getProperty("WarTime", "2h");
            if (strWar.indexOf("d") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.indexOf("h") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.indexOf("m") >= 0) {
                ALT_WAR_TIME_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }
            ALT_WAR_TIME = Integer.parseInt(strWar);
            strWar = altSettings.getProperty("WarInterval", "4d");
            if (strWar.indexOf("d") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.DATE;
                strWar = strWar.replace("d", "");
            } else if (strWar.indexOf("h") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.HOUR_OF_DAY;
                strWar = strWar.replace("h", "");
            } else if (strWar.indexOf("m") >= 0) {
                ALT_WAR_INTERVAL_UNIT = Calendar.MINUTE;
                strWar = strWar.replace("m", "");
            }
            ALT_WAR_INTERVAL = Integer.parseInt(strWar);
            SPAWN_HOME_POINT = Boolean.parseBoolean(altSettings.getProperty("SpawnHomePoint", "true"));
            SPAWN_HOME_POINT_COUNT = Integer.parseInt(altSettings.getProperty("SpawnHomePointCount", "2"));
            SPAWN_HOME_POINT_DELAY = Integer.parseInt(altSettings.getProperty("SpawnHomePointDelay", "100"));
            SPAWN_HOME_POINT_RANGE = Integer.parseInt(altSettings.getProperty("SpawnHomePointRange", "8"));
            INIT_BOSS_SPAWN = Boolean.parseBoolean(altSettings.getProperty("InitBossSpawn", "true"));
            ELEMENTAL_STONE_AMOUNT = Integer.parseInt(altSettings.getProperty("ElementalStoneAmount", "300"));
            HOUSE_TAX_INTERVAL = Integer.parseInt(altSettings.getProperty("HouseTaxInterval", "10"));
            MAX_DOLL_COUNT = Integer.parseInt(altSettings.getProperty("MaxDollCount", "1"));
            RETURN_TO_NATURE = Boolean.parseBoolean(altSettings.getProperty("ReturnToNature", "false"));
            MAX_NPC_ITEM = Integer.parseInt(altSettings.getProperty("MaxNpcItem", "8"));
            MAX_PERSONAL_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxPersonalWarehouseItem", "100"));
            MAX_CLAN_WAREHOUSE_ITEM = Integer.parseInt(altSettings.getProperty("MaxClanWarehouseItem", "200"));
            DELETE_CHARACTER_AFTER_7DAYS = Boolean.parseBoolean(altSettings.getProperty("DeleteCharacterAfter7Days", "True"));
            Quest_Yes = Integer.parseInt(altSettings.getProperty("Yes", "0"));
            Quest_No = Integer.parseInt(altSettings.getProperty("No", "0"));
            NPC_DELETION_TIME = Integer.parseInt(altSettings.getProperty("NpcDeletionTime", "10"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + ALT_SETTINGS_FILE + " File.");
        }

        // charsettings.properties
        try {
            Properties charSettings = new Properties();
            InputStream is = new FileInputStream(new File(
                    CHAR_SETTINGS_CONFIG_FILE));
            charSettings.load(is);
            is.close();

            PRINCE_MAX_HP = Integer.parseInt(charSettings.getProperty("PrinceMaxHP", "4500"));
            PRINCE_MAX_MP = Integer.parseInt(charSettings.getProperty("PrinceMaxMP", "1500"));
            KNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("KnightMaxHP", "5000"));
            KNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("KnightMaxMP", "1000"));
            ELF_MAX_HP = Integer.parseInt(charSettings.getProperty("ElfMaxHP", "3000"));
            ELF_MAX_MP = Integer.parseInt(charSettings.getProperty("ElfMaxMP", "1500"));
            WIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("WizardMaxHP", "3000"));
            WIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("WizardMaxMP", "4000"));
            DARKELF_MAX_HP = Integer.parseInt(charSettings.getProperty("DarkelfMaxHP", "4000"));
            DARKELF_MAX_MP = Integer.parseInt(charSettings.getProperty("DarkelfMaxMP", "1500"));
            DRAGONKNIGHT_MAX_HP = Integer.parseInt(charSettings.getProperty("DragonknightMaxHP", "4800"));
            DRAGONKNIGHT_MAX_MP = Integer.parseInt(charSettings.getProperty("DragonknightMaxMP", "1000"));
            BLACKWIZARD_MAX_HP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxHP", "3000"));
            BLACKWIZARD_MAX_MP = Integer.parseInt(charSettings.getProperty("BlackwizardMaxMP", "4000"));
            LV1_EXP = Integer.parseInt(charSettings.getProperty("Lv1Exp", "1"));
            LV2_EXP = Integer.parseInt(charSettings.getProperty("Lv2Exp", "1"));
            LV3_EXP = Integer.parseInt(charSettings.getProperty("Lv3Exp", "1"));
            LV4_EXP = Integer.parseInt(charSettings.getProperty("Lv4Exp", "1"));
            LV5_EXP = Integer.parseInt(charSettings.getProperty("Lv5Exp", "1"));
            LV6_EXP = Integer.parseInt(charSettings.getProperty("Lv6Exp", "1"));
            LV7_EXP = Integer.parseInt(charSettings.getProperty("Lv7Exp", "1"));
            LV8_EXP = Integer.parseInt(charSettings.getProperty("Lv8Exp", "1"));
            LV9_EXP = Integer.parseInt(charSettings.getProperty("Lv9Exp", "1"));
            LV10_EXP = Integer.parseInt(charSettings.getProperty("Lv10Exp", "1"));
            LV11_EXP = Integer.parseInt(charSettings.getProperty("Lv11Exp", "1"));
            LV12_EXP = Integer.parseInt(charSettings.getProperty("Lv12Exp", "1"));
            LV13_EXP = Integer.parseInt(charSettings.getProperty("Lv13Exp", "1"));
            LV14_EXP = Integer.parseInt(charSettings.getProperty("Lv14Exp", "1"));
            LV15_EXP = Integer.parseInt(charSettings.getProperty("Lv15Exp", "1"));
            LV16_EXP = Integer.parseInt(charSettings.getProperty("Lv16Exp", "1"));
            LV17_EXP = Integer.parseInt(charSettings.getProperty("Lv17Exp", "1"));
            LV18_EXP = Integer.parseInt(charSettings.getProperty("Lv18Exp", "1"));
            LV19_EXP = Integer.parseInt(charSettings.getProperty("Lv19Exp", "1"));
            LV20_EXP = Integer.parseInt(charSettings.getProperty("Lv20Exp", "1"));
            LV21_EXP = Integer.parseInt(charSettings.getProperty("Lv21Exp", "1"));
            LV22_EXP = Integer.parseInt(charSettings.getProperty("Lv22Exp", "1"));
            LV23_EXP = Integer.parseInt(charSettings.getProperty("Lv23Exp", "1"));
            LV24_EXP = Integer.parseInt(charSettings.getProperty("Lv24Exp", "1"));
            LV25_EXP = Integer.parseInt(charSettings.getProperty("Lv25Exp", "1"));
            LV26_EXP = Integer.parseInt(charSettings.getProperty("Lv26Exp", "1"));
            LV27_EXP = Integer.parseInt(charSettings.getProperty("Lv27Exp", "1"));
            LV28_EXP = Integer.parseInt(charSettings.getProperty("Lv28Exp", "1"));
            LV29_EXP = Integer.parseInt(charSettings.getProperty("Lv29Exp", "1"));
            LV30_EXP = Integer.parseInt(charSettings.getProperty("Lv30Exp", "1"));
            LV31_EXP = Integer.parseInt(charSettings.getProperty("Lv31Exp", "1"));
            LV32_EXP = Integer.parseInt(charSettings.getProperty("Lv32Exp", "1"));
            LV33_EXP = Integer.parseInt(charSettings.getProperty("Lv33Exp", "1"));
            LV34_EXP = Integer.parseInt(charSettings.getProperty("Lv34Exp", "1"));
            LV35_EXP = Integer.parseInt(charSettings.getProperty("Lv35Exp", "1"));
            LV36_EXP = Integer.parseInt(charSettings.getProperty("Lv36Exp", "1"));
            LV37_EXP = Integer.parseInt(charSettings.getProperty("Lv37Exp", "1"));
            LV38_EXP = Integer.parseInt(charSettings.getProperty("Lv38Exp", "1"));
            LV39_EXP = Integer.parseInt(charSettings.getProperty("Lv39Exp", "1"));
            LV40_EXP = Integer.parseInt(charSettings.getProperty("Lv40Exp", "1"));
            LV41_EXP = Integer.parseInt(charSettings.getProperty("Lv41Exp", "1"));
            LV42_EXP = Integer.parseInt(charSettings.getProperty("Lv42Exp", "1"));
            LV43_EXP = Integer.parseInt(charSettings.getProperty("Lv43Exp", "1"));
            LV44_EXP = Integer.parseInt(charSettings.getProperty("Lv44Exp", "1"));
            LV45_EXP = Integer.parseInt(charSettings.getProperty("Lv45Exp", "1"));
            LV46_EXP = Integer.parseInt(charSettings.getProperty("Lv46Exp", "1"));
            LV47_EXP = Integer.parseInt(charSettings.getProperty("Lv47Exp", "1"));
            LV48_EXP = Integer.parseInt(charSettings.getProperty("Lv48Exp", "1"));
            LV49_EXP = Integer.parseInt(charSettings.getProperty("Lv49Exp", "1"));
            LV50_EXP = Integer.parseInt(charSettings.getProperty("Lv50Exp", "1"));
            LV51_EXP = Integer.parseInt(charSettings.getProperty("Lv51Exp", "1"));
            LV52_EXP = Integer.parseInt(charSettings.getProperty("Lv52Exp", "1"));
            LV53_EXP = Integer.parseInt(charSettings.getProperty("Lv53Exp", "1"));
            LV54_EXP = Integer.parseInt(charSettings.getProperty("Lv54Exp", "1"));
            LV55_EXP = Integer.parseInt(charSettings.getProperty("Lv55Exp", "1"));
            LV56_EXP = Integer.parseInt(charSettings.getProperty("Lv56Exp", "1"));
            LV57_EXP = Integer.parseInt(charSettings.getProperty("Lv57Exp", "1"));
            LV58_EXP = Integer.parseInt(charSettings.getProperty("Lv58Exp", "1"));
            LV59_EXP = Integer.parseInt(charSettings.getProperty("Lv59Exp", "1"));
            LV60_EXP = Integer.parseInt(charSettings.getProperty("Lv60Exp", "1"));
            LV61_EXP = Integer.parseInt(charSettings.getProperty("Lv61Exp", "1"));
            LV62_EXP = Integer.parseInt(charSettings.getProperty("Lv62Exp", "1"));
            LV63_EXP = Integer.parseInt(charSettings.getProperty("Lv63Exp", "1"));
            LV64_EXP = Integer.parseInt(charSettings.getProperty("Lv64Exp", "1"));
            LV65_EXP = Integer.parseInt(charSettings.getProperty("Lv65Exp", "2"));
            LV66_EXP = Integer.parseInt(charSettings.getProperty("Lv66Exp", "2"));
            LV67_EXP = Integer.parseInt(charSettings.getProperty("Lv67Exp", "2"));
            LV68_EXP = Integer.parseInt(charSettings.getProperty("Lv68Exp", "2"));
            LV69_EXP = Integer.parseInt(charSettings.getProperty("Lv69Exp", "2"));
            LV70_EXP = Integer.parseInt(charSettings.getProperty("Lv70Exp", "4"));
            LV71_EXP = Integer.parseInt(charSettings.getProperty("Lv71Exp", "4"));
            LV72_EXP = Integer.parseInt(charSettings.getProperty("Lv72Exp", "4"));
            LV73_EXP = Integer.parseInt(charSettings.getProperty("Lv73Exp", "4"));
            LV74_EXP = Integer.parseInt(charSettings.getProperty("Lv74Exp", "4"));
            LV75_EXP = Integer.parseInt(charSettings.getProperty("Lv75Exp", "8"));
            LV76_EXP = Integer.parseInt(charSettings.getProperty("Lv76Exp", "8"));
            LV77_EXP = Integer.parseInt(charSettings.getProperty("Lv77Exp", "8"));
            LV78_EXP = Integer.parseInt(charSettings.getProperty("Lv78Exp", "8"));
            LV79_EXP = Integer.parseInt(charSettings.getProperty("Lv79Exp", "16"));
            LV80_EXP = Integer.parseInt(charSettings.getProperty("Lv80Exp", "32"));
            LV81_EXP = Integer.parseInt(charSettings.getProperty("Lv81Exp", "64"));
            LV82_EXP = Integer.parseInt(charSettings.getProperty("Lv82Exp", "128"));
            LV83_EXP = Integer.parseInt(charSettings.getProperty("Lv83Exp", "256"));
            LV84_EXP = Integer.parseInt(charSettings.getProperty("Lv84Exp", "512"));
            LV85_EXP = Integer.parseInt(charSettings.getProperty("Lv85Exp", "1024"));
            LV86_EXP = Integer.parseInt(charSettings.getProperty("Lv86Exp", "2048"));
            LV87_EXP = Integer.parseInt(charSettings.getProperty("Lv87Exp", "4096"));
            LV88_EXP = Integer.parseInt(charSettings.getProperty("Lv88Exp", "8192"));
            LV89_EXP = Integer.parseInt(charSettings.getProperty("Lv89Exp", "16384"));
            LV90_EXP = Integer.parseInt(charSettings.getProperty("Lv90Exp", "32768"));
            LV91_EXP = Integer.parseInt(charSettings.getProperty("Lv91Exp", "65536"));
            LV92_EXP = Integer.parseInt(charSettings.getProperty("Lv92Exp", "131072"));
            LV93_EXP = Integer.parseInt(charSettings.getProperty("Lv93Exp", "262144"));
            LV94_EXP = Integer.parseInt(charSettings.getProperty("Lv94Exp", "524288"));
            LV95_EXP = Integer.parseInt(charSettings.getProperty("Lv95Exp", "1048576"));
            LV96_EXP = Integer.parseInt(charSettings.getProperty("Lv96Exp", "2097152"));
            LV97_EXP = Integer.parseInt(charSettings.getProperty("Lv97Exp", "4194304"));
            LV98_EXP = Integer.parseInt(charSettings.getProperty("Lv98Exp", "8388608"));
            LV99_EXP = Integer.parseInt(charSettings.getProperty("Lv99Exp", "16777216"));
        } catch (Exception e) {
            _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            throw new Error("Failed to Load " + CHAR_SETTINGS_CONFIG_FILE + " File.");
        }
        validate();
    }

    private static String buildFileDbUrl(String filePath) {
        String path = filePath == null ? "" : filePath.trim();
        if (path.length() == 0) {
            path = "./data/filedb/l1jdb";
        }
        if (path.startsWith("jdbc:h2:")) {
            return path;
        }
        return "jdbc:h2:file:" + path
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;AUTO_SERVER=TRUE";
    }

    private static void validate() {
        if (!IntRange.includes(Config.ALT_ITEM_DELETION_RANGE, 0, 5)) {
            throw new IllegalStateException("ItemDeletionRange의 값이 설정 가능 범위외입니다. ");
        }

        if (!IntRange.includes(Config.ALT_ITEM_DELETION_TIME, 1, 35791)) {
            throw new IllegalStateException("ItemDeletionTime의 값이 설정 가능 범위외입니다. ");
        }
    }

    public static boolean setParameterValue1(String pName, int pValue) {
        if (pName.equalsIgnoreCase("Yes")) {
            Quest_Yes = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("No")) {
            Quest_No = Integer.valueOf(pValue);
        } else {
            return false;
        }
        return true;
    }

    public static boolean setParameterValue(String pName, String pValue) {
        // server.properties
        if (pName.equalsIgnoreCase("GameserverHostname")) {
            GAME_SERVER_HOST_NAME = pValue;
        } else if (pName.equalsIgnoreCase("GameserverPort")) {
            GAME_SERVER_PORT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Driver")) {
            DB_DRIVER = pValue;
        } else if (pName.equalsIgnoreCase("URL")) {
            DB_URL = pValue;
        } else if (pName.equalsIgnoreCase("Login")) {
            DB_LOGIN = pValue;
        } else if (pName.equalsIgnoreCase("Password")) {
            DB_PASSWORD = pValue;
        } else if (pName.equalsIgnoreCase("ClientLanguage")) {
            CLIENT_LANGUAGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("TimeZone")) {
            TIME_ZONE = pValue;
        } else if (pName.equalsIgnoreCase("AutomaticKick")) {
            AUTOMATIC_KICK = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AutoCreateAccounts")) {
            AUTO_CREATE_ACCOUNTS = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("MaximumOnlineUsers")) {
            MAX_ONLINE_USERS = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("LoggingWeaponEnchant")) {
            LOGGING_WEAPON_ENCHANT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("LoggingArmorEnchant")) {
            LOGGING_ARMOR_ENCHANT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("CharacterConfigInServerSide")) {
            CHARACTER_CONFIG_IN_SERVER_SIDE = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("Allow2PC")) {
            ALLOW_2PC = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("LevelDownRange")) {
            LEVEL_DOWN_RANGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SendPacketBeforeTeleport")) {
            SEND_PACKET_BEFORE_TELEPORT = Boolean.parseBoolean(pValue);
        } else if (pName.equalsIgnoreCase("AccountLimit")) { // IP당 계정 생성 개수 외부화
            ACCOUNT_LIMIT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("systime")) {
            systime = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("sys1")) {
            sys1 = pValue;
        } else if (pName.equalsIgnoreCase("sys2")) {
            sys2 = pValue;
        } else if (pName.equalsIgnoreCase("sys3")) {
            sys3 = pValue;
        } else if (pName.equalsIgnoreCase("sys4")) {
            sys4 = pValue;
        } else if (pName.equalsIgnoreCase("sys5")) {
            sys5 = pValue;
        } else if (pName.equalsIgnoreCase("sys6")) {
            sys6 = pValue;
        } else if (pName.equalsIgnoreCase("sys7")) {
            sys7 = pValue;
        }
        // rates.properties
        else if (pName.equalsIgnoreCase("RateXp")) {
            RATE_XP = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RatePetXp")) { // ########## A137 펫 경험치 배율 설정 외부화 [넬]
            RATE_PET_XP = Double.parseDouble(pValue); // #####
        } else if (pName.equalsIgnoreCase("RateCclanXp")) {         //성혈 경험치 외부화
            RATE_CCLAN_XP = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateLawful")) {
            RATE_LA = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateKarma")) {
            RATE_KARMA = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateDropAdena")) {
            RATE_DROP_ADENA = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("RateDropItems")) {
            RATE_DROP_ITEMS = Double.parseDouble(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceWeapon")) {
            ENCHANT_CHANCE_WEAPON = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("EnchantChanceArmor")) {
            ENCHANT_CHANCE_ARMOR = Integer.parseInt(pValue);
            // 무기 방어구 최대인챈 제한 외부화
        } else if (pName.equalsIgnoreCase("Maxweaponenchant")) {
            MAX_WEAPON_ENCHANT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Maxenchant")) {
            MAX_ARMOR_ENCHANT = Integer.parseInt(pValue);
            //장신구업그레이드 By추억
        } else if (pName.equalsIgnoreCase("UpAcseChance")) {
            UPACSE_CHANCE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Weightrate")) {
            RATE_WEIGHT_LIMIT = Byte.parseByte(pValue);
        }
        // altsettings.properties
        else if (pName.equalsIgnoreCase("GlobalChatLevel")) {
            GLOBAL_CHAT_LEVEL = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("WhisperChatLevel")) {
            WHISPER_CHAT_LEVEL = Short.parseShort(pValue);
        } else if (pName.equalsIgnoreCase("AutoLoot")) {
            AUTO_LOOT = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("LOOTING_RANGE")) {
            LOOTING_RANGE = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AltNonPvP")) {
            ALT_NONPVP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AttackMessageOn")) {
            ALT_ATKMSG = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ChangeTitleByOneself")) {
            CHANGE_TITLE_BY_ONESELF = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxClanMember")) {
            MAX_CLAN_MEMBER = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ClanAlliance")) {
            CLAN_ALLIANCE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxPT")) {
            MAX_PT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("MaxChatPT")) {
            MAX_CHAT_PT = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("SimWarPenalty")) {
            SIM_WAR_PENALTY = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GetBack")) {
            GET_BACK = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("AutomaticItemDeletionTime")) {
            ALT_ITEM_DELETION_TIME = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("AutomaticItemDeletionRange")) {
            ALT_ITEM_DELETION_RANGE = Byte.parseByte(pValue);
        } else if (pName.equalsIgnoreCase("GMshop")) {
            ALT_GMSHOP = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("GMshopMinID")) {
            ALT_GMSHOP_MIN_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("GMshopMaxID")) {
            ALT_GMSHOP_MAX_ID = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("HalloweenIvent")) {
            ALT_HALLOWEENIVENT = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("Whoiscount")) {
            WHOIS_CONTER = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("TalkingScrollQuest")) {
            ALT_TALKINGSCROLLQUEST = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("HouseTaxInterval")) {
            HOUSE_TAX_INTERVAL = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxDollCount")) {
            MAX_DOLL_COUNT = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("ReturnToNature")) {
            RETURN_TO_NATURE = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxNpcItem")) {
            MAX_NPC_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxPersonalWarehouseItem")) {
            MAX_PERSONAL_WAREHOUSE_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("MaxClanWarehouseItem")) {
            MAX_CLAN_WAREHOUSE_ITEM = Integer.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("DeleteCharacterAfter7Days")) {
            DELETE_CHARACTER_AFTER_7DAYS = Boolean.valueOf(pValue);
        } else if (pName.equalsIgnoreCase("NpcDeletionTime")) {
            NPC_DELETION_TIME = Integer.valueOf(pValue);
        }
        // charsettings.properties
        else if (pName.equalsIgnoreCase("PrinceMaxHP")) {
            PRINCE_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("PrinceMaxMP")) {
            PRINCE_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KnightMaxHP")) {
            KNIGHT_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("KnightMaxMP")) {
            KNIGHT_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ElfMaxHP")) {
            ELF_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("ElfMaxMP")) {
            ELF_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WizardMaxHP")) {
            WIZARD_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("WizardMaxMP")) {
            WIZARD_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DarkelfMaxHP")) {
            DARKELF_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DarkelfMaxMP")) {
            DARKELF_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DragonknightMaxHP")) {
            DRAGONKNIGHT_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("DragonknightMaxMP")) {
            DRAGONKNIGHT_MAX_MP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BlackwizardMaxHP")) {
            BLACKWIZARD_MAX_HP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("BlackwizardMaxMP")) {
            BLACKWIZARD_MAX_MP = Integer.parseInt(pValue);


        } else if (pName.equalsIgnoreCase("Lv1Exp")) {
            LV1_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv2Exp")) {
            LV2_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv3Exp")) {
            LV3_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv4Exp")) {
            LV4_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv5Exp")) {
            LV5_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv6Exp")) {
            LV6_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv7Exp")) {
            LV7_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv8Exp")) {
            LV8_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv9Exp")) {
            LV9_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv10Exp")) {
            LV10_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv11Exp")) {
            LV11_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv12Exp")) {
            LV12_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv13Exp")) {
            LV13_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv14Exp")) {
            LV14_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv15Exp")) {
            LV15_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv16Exp")) {
            LV16_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv17Exp")) {
            LV17_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv18Exp")) {
            LV18_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv19Exp")) {
            LV19_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv20Exp")) {
            LV20_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv21Exp")) {
            LV21_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv22Exp")) {
            LV22_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv23Exp")) {
            LV23_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv24Exp")) {
            LV24_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv25Exp")) {
            LV25_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv26Exp")) {
            LV26_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv27Exp")) {
            LV27_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv28Exp")) {
            LV28_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv29Exp")) {
            LV29_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv30Exp")) {
            LV30_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv31Exp")) {
            LV31_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv32Exp")) {
            LV32_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv33Exp")) {
            LV33_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv34Exp")) {
            LV34_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv35Exp")) {
            LV35_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv36Exp")) {
            LV36_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv37Exp")) {
            LV37_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv38Exp")) {
            LV38_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv39Exp")) {
            LV39_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv40Exp")) {
            LV40_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv41Exp")) {
            LV41_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv42Exp")) {
            LV42_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv43Exp")) {
            LV43_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv44Exp")) {
            LV44_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv45Exp")) {
            LV45_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv46Exp")) {
            LV46_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv47Exp")) {
            LV47_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv48Exp")) {
            LV48_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv49Exp")) {
            LV49_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv50Exp")) {
            LV50_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv51Exp")) {
            LV51_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv52Exp")) {
            LV52_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv53Exp")) {
            LV53_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv54Exp")) {
            LV54_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv55Exp")) {
            LV55_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv56Exp")) {
            LV56_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv57Exp")) {
            LV57_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv58Exp")) {
            LV58_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv59Exp")) {
            LV59_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv60Exp")) {
            LV60_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv61Exp")) {
            LV61_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv62Exp")) {
            LV62_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv63Exp")) {
            LV63_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv64Exp")) {
            LV64_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv65Exp")) {
            LV65_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv66Exp")) {
            LV66_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv67Exp")) {
            LV67_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv68Exp")) {
            LV68_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv69Exp")) {
            LV69_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv70Exp")) {
            LV70_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv71Exp")) {
            LV71_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv72Exp")) {
            LV72_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv73Exp")) {
            LV73_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv74Exp")) {
            LV74_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv75Exp")) {
            LV75_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv76Exp")) {
            LV76_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv77Exp")) {
            LV77_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv78Exp")) {
            LV78_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv79Exp")) {
            LV79_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv80Exp")) {
            LV80_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv81Exp")) {
            LV81_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv82Exp")) {
            LV82_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv83Exp")) {
            LV83_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv84Exp")) {
            LV84_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv85Exp")) {
            LV85_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv86Exp")) {
            LV86_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv87Exp")) {
            LV87_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv88Exp")) {
            LV88_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv89Exp")) {
            LV89_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv90Exp")) {
            LV90_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv91Exp")) {
            LV91_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv92Exp")) {
            LV92_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv93Exp")) {
            LV93_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv94Exp")) {
            LV94_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv95Exp")) {
            LV95_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv96Exp")) {
            LV96_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv97Exp")) {
            LV97_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv98Exp")) {
            LV98_EXP = Integer.parseInt(pValue);
        } else if (pName.equalsIgnoreCase("Lv99Exp")) {
            LV99_EXP = Integer.parseInt(pValue);
        } else {
            return false;
        }
        return true;
    }

    private Config() {
    }
}
