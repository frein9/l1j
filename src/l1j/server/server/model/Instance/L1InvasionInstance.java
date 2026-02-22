package l1j.server.server.model.Instance;

import java.util.logging.Logger;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1World;
import l1j.server.server.model.L1War;
import l1j.server.server.model.MonsterInvasion;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;

public class L1InvasionInstance extends L1NpcInstance {
 /**
  * 
  */
 private static final long serialVersionUID = 1L;
 private static Logger _log = Logger.getLogger(L1InvasionInstance.class
   . getName());

 // 타겟을 찾는다
 @Override
 public void searchTarget() {
  // 타겟 수색
  L1PcInstance targetPlayer = null;  
  L1GuardInstance targetGuard = null;  
  if(MonsterInvasion.getInstance().getAttackStart()==true){
  for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) { 
   if (pc.getCurrentHp() <= 0 || pc.isDead()
     || pc.isGm() || pc.isGhost()) {
    continue;
   }
   if(MonsterInvasion.getInstance().getAttackStart()==true){
    targetPlayer = pc;
    break;
   }
  }
   for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) { //경비도공격하도록
    if(obj instanceof L1GuardInstance){
     L1GuardInstance gud = (L1GuardInstance) obj;
     if (gud.getCurrentHp() <= 0 || gud.isDead()) {
      continue;
     }else{
      targetGuard = gud;
     }
    }
   }
  }
   if(targetGuard != null){
    _hateList.add(targetGuard, 0);
   _target = targetGuard;
   }
  if (targetPlayer != null) {
   _hateList.add(targetPlayer, 0);
   _target = targetPlayer;
  }
 }

 public void setTarget(L1PcInstance targetPlayer) {
  if (targetPlayer != null) {
   _hateList.add(targetPlayer, 0);
   _target = targetPlayer;
  }
 }

 // 타겟이 없는 경우의 처리
 @Override
 public boolean noTarget() { //타겟이없을시엔 균열중앙으로모이도록
  if(MonsterInvasion.getInstance().getAttackStart()==true){
   int dir = moveDirection(MonsterInvasion.getInstance().getMainX(), MonsterInvasion.getInstance().getMainY());
 //  setDirectionMove2(dir,MonsterInvasion.getInstance().getMainX(),MonsterInvasion.getInstance().getMainY());
    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
  }else{
   return true;
  }
   return false;
  }

 public L1InvasionInstance(L1Npc template) {
  super(template);
 }

 @Override
 public void onNpcAI() {
  if (isAiRunning()) {
   return;
  }
  setActived(false);
  startAI();  
 }

 @Override
 public void onAction(L1PcInstance pc) {
  if (!isDead()) {
   if (getCurrentHp() > 0) {
    L1Attack attack = new L1Attack(pc, this);
    if (attack.calcHit()) {
     attack.calcDamage();
     attack.calcStaffOfMana();
     attack.addPcPoisonAttack(pc, this);
    }
    attack.action();
    attack.commit();
   } else {
    L1Attack attack = new L1Attack(pc, this);
    attack.calcHit();
    attack.action();
   }
  }
 }

 

 public void onFinalAction() {

 }

 public void doFinalAction() {

 }

 @Override
 public void receiveDamage(L1Character attacker, int damage) { // 공격으로 HP를 줄일 때는 여기를 사용
  if (getCurrentHp() > 0 && !isDead()) {
   if (damage >= 0) {
    if (!(attacker instanceof L1EffectInstance)) { // FW는 헤이트 없음
     setHate(attacker, damage);
    }
   }
   if (damage > 0) {
    removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
   }

   onNpcAI();

   if (attacker instanceof L1PcInstance && damage > 0) {
    L1PcInstance pc = (L1PcInstance) attacker;
    pc.setPetTarget(this);
   }

   int newHp = getCurrentHp() - damage;
   if (newHp <= 0 && !isDead()) {
    if(getNpcTemplate().get_npcId()==456851){ //보스몬스터가 죽으면 균열이끝나도록
     MonsterInvasion.getInstance().setStopCom(true);
    }
    setCurrentHpDirect(0);
    setDead(true);
    setStatus(ActionCodes.ACTION_Die);
    Death death = new Death(attacker);
    GeneralThreadPool.getInstance(). execute(death);
   }
   if (newHp > 0) {
    setCurrentHp(newHp);
   }
  } else if (getCurrentHp() == 0 && !isDead()) {
  } else if (!isDead()) { // 만약을 위해
   setDead(true);
   setStatus(ActionCodes.ACTION_Die);
   Death death = new Death(attacker);
   GeneralThreadPool.getInstance(). execute(death);
  }
 }

 @Override
 public void setCurrentHp(int i) {
  int currentHp = i;
  if (currentHp >= getMaxHp()) {
   currentHp = getMaxHp();
  }
  setCurrentHpDirect(currentHp);

  if (getMaxHp() > getCurrentHp()) {
   startHpRegeneration();
  }
 }

 class Death implements Runnable {
  L1Character _lastAttacker;

  public Death(L1Character lastAttacker) {
   _lastAttacker = lastAttacker;
  }

  @Override
  public void run() {
   setDeathProcessing(true);
   setCurrentHpDirect(0);
   setDead(true);
   setStatus(ActionCodes.ACTION_Die);

   getMap(). setPassable(getLocation(), true);

   broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

   startChat(CHAT_TIMING_DEAD);

   setDeathProcessing(false);

   allTargetClear();

   startDeleteTimer();
  }
 }

}

