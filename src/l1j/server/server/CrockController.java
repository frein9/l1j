package l1j.server.server;

import java.util.Locale;
import java.util.Calendar;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import l1j.server.server.model.L1World;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.templates.L1Npc; 

import java.util.Random;   // 임포트 추가 by neodis

public class CrockController extends Thread{

 /** 시간의 균열 오픈 시각(ms) */
 private static long sTime = 0;

 /** 시간의 균열 임시 랜덤 값 */
 private static int rnd = 0;

 /** 시간의 균열 보스 횟수 */
 private static int dieCount = 0;

 /** 시간의 균열이 열리는 시간 간격 */
 private static final int LOOP = (int)(Math.random()*10+1);

 /** 시간의 균열 보스 공략 판단 */
 private boolean boss = false;

 /** 시간의 균열 이동 시간 판단 */
 private boolean move = false;

 /** 현재 시간을 임시로 담기 */
 private String NowTime = "";

 /** 카운트 시간 : 30분 */  //검색
 private static final long TIME = 1800000L; //보스잡는시간  30분

 /** 카운트 시간 : 1시간 */
 private static final long DAY = 3600000L; //보스잡을시 1시간 유지
 
 /** 카운트 시간 : 2시간30분 */
 private static final long DELAY = 9000000L; //문지기 열리기전까지의 시간 2시간 30분

 /** 싱글톤 단일 객체 */
 private static CrockController instance;

 /** 시간의 균열 객체 아이디 */
 private static final int[] ID = { 200, 201, 202, 203, 204, 205, 206, 207, 209 }; //209 하나 추가
 /*private static final int[] ID = { 2000035, 2000036, 2000037, 2000038, 2000039,
 /           2000077, 2000078, 2000079, 2000108};*/ 
// 균열 열림, 닫힘
 private boolean TicalC = false; // 티칼달력추가

 public static boolean openck = false;
 /** 시간의 균열 좌표 */
//1.카오틱신전 밑쪽  2.글루디오 위쪽  3.오아시스 왼쪽  4.오아시스 오른쪽  5.오만다리근처  6.자이언트밭  7.은말 왼쪽  8.오만의탑 근처  9.카오틱신전 왼쪽
private static final int[][] loc = { //변환시 DB spwanlist_npc에서 좌표수정
	  { 32852, 32709, 4 }, 
	  { 32729, 32702, 4 }, 
	  { 32906, 33174, 4 }, 
	  { 32959, 33254, 4 }, 
	  { 34254, 33206, 4 },
	  { 34223, 33316, 4 },
	  { 32912, 33429, 4 },
	  { 34266, 33367, 4 },
	  { 32832, 32650, 4 }};
	 //---------------------------추가 시작 by neodis------------------------//
	 private static String [] locName = {
	   "카오틱신전 밑쪽"
	  ,"글루디오 위쪽"
	  ,"오아시스 왼쪽"
	  ,"오아시스 오른쪽"
	  ,"오만다리"
	  ,"자이언트 밭"
	  ,"은말 왼쪽"
	  ,"오만의탑 근처"
	  ,"카오틱신전 왼쪽"};
 public static L1NpcInstance ClockInstance; 
 private boolean   isTebeosiris = false; // 테베 일경우 true 아닐경우 티칼
 private String divide;  //티베사막, 티칼사원 메시지 분할
 //---------------------------추가 끝 by neodis----------------------------//

 /** 보스방 선착순 20명을 담기 위한 리스트 */
 private static final ArrayList<L1PcInstance> sList = new ArrayList<L1PcInstance>();

 /** 시각 데이터 포맷 */
 private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);

 /** 시각 데이터 포맷 */
 private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

 /**
  * CrockController 객체 리턴
  * @return (CrockController) 단일객체
 */
 public static CrockController getInstance(){
  if(instance == null) instance = new CrockController();
  return instance;
 }
 /**
  * 기본 생성자 - 싱글톤구현으로 private
 */
 private CrockController(){
  super("CrockController");
 }

 /**
  * Super class abstract method
 */
 //---------------------------수정 by neodis------------------------//
 @Override
 public void run(){
  try{
   while(true){
    sleep(600000L);//시간의균열 다시 열릴 조건 검색 대기 시간(10분마다 검색)//sleep(1000L);  // 1초 
    if(!isOpen()) 
     continue;
    if(L1World.getInstance().getAllPlayers().size() <= 0)//음...size()    e
     continue;
    L1World.getInstance().broadcastServerMessage("잠시후 시간의 균열이 열립니다.");
    sleep(15000L);     // 15초    //검색
    npcId();
    ready(ID[rnd]);
    L1World.getInstance().broadcastServerMessage("시간의 균열이 "+locName[rnd]+" 근처에서 열렸습니다.\n"+divide+"으로부터 이계의 침공이 시작됩니다.");
    setBoss(false);
    setTicalC(true); // 티칼 달력 추가
    sleep(DELAY);
       L1World.getInstance().broadcastServerMessage("시간의 균열이 생성된 지 2시간 30분이 지났습니다.\n이계의 보스 몬스터를 잡을 수 있습니다.");
       setBoss(true);   // 보스 공략 시작
       sleep(TIME);     //30분시간
    /** 보스 공략이 실패 했다면 전원 바로 텔 */
    if(isTeleport())
    {
     L1World.getInstance().broadcastServerMessage(divide+"의 보스공략에 실패하였습니다.");
     TelePort();
    /** 보스 공략이 성공했다면 24시간 후 텔 */
    }else{
     L1World.getInstance().broadcastServerMessage(divide+"의 보스공략에 성공하였습니다. 앞으로 1시간 더 사냥이 가능합니다.");
     sleep(DAY);    //24시간
     TelePort();
    }
    L1World.getInstance().broadcastServerMessage(divide+"으로 통하는 시간의 균열이 닫혔습니다.");
    clear();
   }
  }catch(Exception e){
   e.printStackTrace();
  }
 }
 //---------------------------수정 끝 by neodis---------------------//

 /**
  * 시간의 균열 열리기 준비상태 및 균열 시작
 */
 //---------------------------수정 시작 by neodis-------------------------------------//

 // 함수 추가 by neodis
 public boolean isTebeOsiris()
 {
  return isTebeosiris;
 }
 /**
 * 시간의 균열 열리기 준비상태 및 균열 시작
 */
 private void ready(int npcId)
 {
  
  Random _rnd = new Random();
  if(_rnd.nextInt(10)<4){
   isTebeosiris = true;
   divide = "테베 사막"; //입장지역 분할 및 표시지정
  } else {
   isTebeosiris = false;
   divide = "티칼 사원";
  }
  L1Npc npc;
  npc = new L1Npc();
  
  npc.set_passispeed(0);
  npc.set_family(0);
  npc.set_agrofamily(0);
  npc.set_picupitem(false);
  npc.set_npcId(npcId);
  Object[] parameters = { npc };
  try
  {
   ClockInstance = (L1NpcInstance)Class.forName("l1j.server.server.model.Instance.L1NpcInstance").getConstructors()[0].newInstance(parameters);
   ClockInstance.setTempCharGfx(6919);
   ClockInstance.setGfxId(6919);
   ClockInstance.setName("$5656");
   ClockInstance.setX(loc[rnd][0]);
   ClockInstance.setY(loc[rnd][1]);
   
   ClockInstance.setMap((short)4);
   ClockInstance.setHeading(5);
  // ClockInstance.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_BUGRACE);
   ClockInstance.setId(IdFactory.getInstance().nextId());
   
   L1World.getInstance().storeObject(ClockInstance);
   L1World.getInstance().addVisibleObject(ClockInstance);
  }
  catch(Exception e)
  {
   
  }
  L1PcInstance[] list = L1World.getInstance().getVisiblePlayer(ClockInstance).toArray(new L1PcInstance[L1World.getInstance().getVisiblePlayer(ClockInstance).size()]);
  for(L1PcInstance pc : list){
   pc.sendPackets(new S_RemoveObject(ClockInstance));
   pc.removeKnownObject(ClockInstance);
   pc.updateObject();
  }
  try{ 
   Thread.sleep(15000L); 
  }catch(Exception e)
  {
   
  }
  for(L1PcInstance pc : list) 
   pc.sendPackets(new S_DoActionGFX(npc.getId(), 50));
  
  setMove(true);
  sTime = System.currentTimeMillis();
 }

 //---------------------------수정 끝 by neodis-------------------------------------//

 /**
  * 전원 아덴 마을로 텔
 */
 //---------------------------수정 시작 by neodis-------------------------------------//
 private void TelePort()
 {
  
  for(L1PcInstance c : L1World.getInstance().getAllPlayers())
  {
   //if(c.getInventory().checkItem(100036, 1)) 
    //c.getInventory().consumeItem(100036, 1);
   switch(c.getMap().getId())
   {
   case 780:
    c.setSkillEffect(78, 2000);
    c.stopHpRegeneration();
    c.stopMpRegeneration();
    L1Teleport.teleport(c, 34069, 33122, (short) 4, 4, true);
    break;
   case 781:
    c.setSkillEffect(78, 2000);
    c.stopHpRegeneration();
    c.stopMpRegeneration();
    L1Teleport.teleport(c, 34069, 33122, (short) 4, 4, true);
    break;
   case 782:
    c.setSkillEffect(78, 2000);
    c.stopHpRegeneration();
    c.stopMpRegeneration();
    L1Teleport.teleport(c, 34069, 33122, (short) 4, 4, true);
    break;
   case 783:
    c.setSkillEffect(78, 2000);
    c.stopHpRegeneration();
    c.stopMpRegeneration();
    L1Teleport.teleport(c, 34069, 33122, (short) 4, 4, true);
    break;
   case 784:
    c.setSkillEffect(78, 2000);
    c.stopHpRegeneration();
    c.stopMpRegeneration();
    L1Teleport.teleport(c, 34069, 33122, (short) 4, 4, true);
    break;
   }
  }
 }
 //---------------------------수정 끝 by neodis-------------------------------------//


 /**
  * 오픈 시각을 가져온다
  * @return (String) 오픈 시각(MM-dd HH:mm)
 */
 public String OpenTime(){
  Calendar c = Calendar.getInstance();
  c.setTimeInMillis(sTime);
  return ss.format(c.getTime());
 }

 /**
  * 현재시각을 가져온다
  * @return (String) 현재 시각(HH:mm)
 */
 private String getTime(){
  return s.format(Calendar.getInstance().getTime());
 }

 /**
  * 특정시각을 가져온다
  * @param (long)  특정한 시각(ms)
  * @return (String) 특정 시각(HH:mm)
 */
 private String getTime(long time){
  Calendar c = Calendar.getInstance();
  c.setTimeInMillis(time);
  return s.format(c.getTime());
 }

 /**
  * 시간의 균열이 현재 열려있는지 판단
  * @return (boolean) 열려있다면 true 닫혀있다면 false
 */
 private boolean isOpen(){
  NowTime = getTime();
  if((Integer.parseInt(NowTime) % LOOP) == 0) return true;//NowTime은 시간을 불러옴. 시간을 LOOP로 나눔 
  return false;
 }

 /**
  * 시간의 균열 이동 상태
  * @return (boolean) move 이동 여부
 */
 public boolean isMove(){
  return move;
 }

 /**
  * 시간의 균열 이동 상태 셋팅
  * @param (boolean) move 이동 여부
 */
 private void setMove(boolean move){
  this.move = move;
 }
 /*
 티칼 달력
 열림/닫힘 여부
 */

 public boolean TicalC(){
 return TicalC;

 }

  private void setTicalC(boolean TicalC){
   this.TicalC = TicalC;
  }

 /**
  * 시간의 균열 보스공략 시간상태
  * @return (boolean) boss 공략 여부
 */
 public boolean isBoss(){
  return boss;
 }

 /**
  * 시간의 균열 보스공략 시간 알림
  * @param (boolean) boss 공략 여부
 */
 private void setBoss(boolean boss){
  this.boss = boss;
 }

 /**
  * 선착순 20명 등록
 */
 public synchronized void add(L1PcInstance c){
  /** 등록되어 있지 않고 */
  if(!sList.contains(c)){
   /** 선착순 20명 이하라면 */
   if(sList.size() < 20) sList.add(c);
  }
 }

 /**
  * 선착순 리스트 사이즈 반납
  * @return (int) sList 의 사이즈
 */
 public int size(){
  return sList.size();
 }

 /**
  * 클리어(초기화) : 시스템이 한바퀴 끝날때 재 셋팅을 위해 쓰인다.
 */
 private void clear(){
  sList.clear();
  dieCount = 0;
  setBoss(false);
  setMove(false);
  setTicalC(false);
  for(int npcId : ID){
   L1NpcInstance npc = L1World.getInstance().findNpc(npcId);
   // for only open image is seeing...
   if(npc==null) //닫힌 이미지로 복귀
   continue;
   npc.setTempCharGfx(6920);   
   npc.setGfxId(6920);
   for(L1PcInstance pc : L1World.getInstance().getVisiblePlayer(npc).toArray(new L1PcInstance[L1World.getInstance().getVisiblePlayer(npc).size()])){
    pc.sendPackets(new S_RemoveObject(npc));
    pc.removeKnownObject(npc);
    pc.updateObject();
   }
  }
 }

 /**
  * 선착순 20명에게 아이템 지급
 */
 public void send(){
  //for(L1PcInstance c : sList.toArray(new L1PcInstance[sList.size()])) c.getInventory().storeItem(400073, 1);
 }

 /**
  * 시간의 균열중 하나의 랜덤의 아이디를 반납
  * @return (int) npcId 엔피씨 아이디
 */
 private int npcId(){
  rnd = (int)(Math.random() * ID.length);  
  return ID[rnd];
 }

 /**
  * 지정된 npcId 에 대한 loc 을 반납
  * @return (int[]) loc  좌표 배열
 */
 public int[] loc(){  
  return loc[rnd];
 }

 /**
  * 시간의 균열 보스공략 확인
  * @return (boolean) 2보스다 죽었다면 false 1보스 이하 죽였다면 true
 */
 private boolean isTeleport(){ //L1MonsterInstance에 티칼 보스 추가
  boolean sTemp = true;
  switch(dieCount()){
   case 2:
    sTemp = false;
    break;
   default:
    sTemp = true;
    break;
  }
  return sTemp;
 }

 /**
  * 시간의 균열 보스 다이 반납
  * @return (int) dieCount 보스 다이 횟수
 */
 public int dieCount(){
  return dieCount;
 }

 /**
  * 시간의 균열 보스 다이 설정
  * @param (int) dieCount 보스 다이 횟수
 */
 public void dieCount(int dieCount){
  this.dieCount = dieCount;
 }
}