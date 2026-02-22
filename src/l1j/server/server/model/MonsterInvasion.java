package l1j.server.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.*;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.skill.*;
import l1j.server.server.serverpackets.*;
import l1j.server.server.datatables.*;
import l1j.server.server.templates.*;
import l1j.server.server.*;
import l1j.server.server.utils.L1SpawnUtil;


public class MonsterInvasion{
 
 private static final Logger _log = Logger.getLogger(MonsterInvasion.class
   .getName());
 private static Random _random = new Random();
 
 int[] _Monster1={451751,451911,452201,452331,452381}; //1라운드 몬스터ID
 int[] _Monster2={454981,455301,455371,455621,456031}; //2라운드 몬스터ID
 int[] _Monster3={456231,456241,456281,456291,456301}; //3라운드 몬스터ID
 

private  L1NpcInstance 균열 = L1World.getInstance().findNpc(2031);


 private static MonsterInvasion instance;

 public static MonsterInvasion getInstance(){
  if(instance == null){
   instance = new MonsterInvasion();
  }
  return instance;
 }
 
 private int _MainX;
 
 public void setMainX (int X){
  _MainX = X;
 }
 public int getMainX(){
  return _MainX;
 }
 
 private int _MainY;
 
 public void setMainY (int Y){
  _MainY = Y;
 }
 public int getMainY(){
  return _MainY;
 }
 private int _MaInMapid;
 
 public int getMainMap(){
  return _MaInMapid;
 }
 public void setMainMap(int A){
  _MaInMapid = A;
 }
 private boolean _AttackStart = false;
 
 public boolean getAttackStart(){
  return _AttackStart;
 }
 public void setAttackStart(boolean flag){
  _AttackStart = flag;
 }
 private boolean _StopCommand;
 
 public boolean getStopCom(){
  return _StopCommand;
 }
 public void setStopCom(boolean flag){
  _StopCommand = flag;
 }
 private int _Round;
 
 public int getRound(){
  return _Round;
 }
 public void setRound(int Number){
  _Round = Number;
 }
 
 public void WorldMessage(String A){
  L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(A));
 }
 public void EndMonster(){
  for(L1Object obj : L1World.getInstance().getObject()){
    if(obj instanceof L1InvasionInstance){
     L1InvasionInstance gud = (L1InvasionInstance) obj;
     gud.deleteMe();
    }
  }
 }
 public class InvasionStart implements Runnable {
  public void run(){
   try{
    WorldMessage("\\fY 몬스터침공이 시작됩니다.");
    Thread.sleep(1000);
    WorldMessage("\\fY 마지막에 나오는 보스까지 제거하셔야됩니다.");
    L1SpawnUtil.spawn(2031,getMainX(),getMainY(),5,getMainMap());
    Thread.sleep(2000);
    setRound(1);
    setAttackStart(true);
    SpawnStartON();
    while(true){
     if(getStopCom()==true){
     균열.deleteMe();
     EndMonster();
     WorldMessage("\\fY 보스몬스터가 제거되었습니다.");
     Thread.sleep(3000);
     WorldMessage("\\fY 축하드립니다 몬스터침공 방어에 성공하셨습니다..");
     break;
     }
     Thread.sleep(1000);
    }
   }catch(Exception a){
    
   }
  }
 }
 public void InvasionStartON(){
  InvasionStart A2 = new InvasionStart();
  GeneralThreadPool.getInstance().execute(A2);
 }
 public void SpawnStartON(){
  SpawnStart A1 = new SpawnStart();
  GeneralThreadPool.getInstance().execute(A1);
 }
 public class SpawnStart implements Runnable {
  int X = getMainX();
  int Y = getMainY();
  int Heading=5;
  int Mapid=getMainMap();
  public void run(){
   try{
   
    while(getRound() <= 4){
     WorldMessage("\\fY"+getRound()+"차공격이 시작됩니다.");
    for(int B=0;B<=5;B++){
     WorldMessage("\\fY몬스터가 나타났습니다.");
    for(int A=0;A<=50;A++){
     switch(getRound()){
     case 1:
     int RandomMb = _random.nextInt(_Monster1.length);
     int npcId = _Monster1[RandomMb];
     L1SpawnUtil.spawn(npcId,X,Y,Heading,Mapid);
     Thread.sleep(100);
     break;
     case 2:
      int RandomMb2 = _random.nextInt(_Monster2.length);
      int npcId2 = _Monster2[RandomMb2];
      L1SpawnUtil.spawn(npcId2,X,Y,Heading,Mapid);
      Thread.sleep(100);
      break;
     case 3:
      int RandomMb3 = _random.nextInt(_Monster3.length);
      int npcId3 = _Monster3[RandomMb3];
      L1SpawnUtil.spawn(npcId3,X,Y,Heading,Mapid);
      Thread.sleep(100);
      break;
     case 4:
      WorldMessage("\\fY보스몬스터가 나타났습니다.");
      L1SpawnUtil.spawn(456851,X,Y,Heading,Mapid);
      break;
     }
     if(getRound()==4){
      break;
     }
     }
    Thread.sleep(40000);
    }
    WorldMessage("\\fY"+getRound()+"차공격을 저지하셨습니다.");
    setRound(getRound()+1);
   }
   }catch(Exception a){
    WorldMessage("\\fY 스폰에러.");
   }
  }
 }
 
}
