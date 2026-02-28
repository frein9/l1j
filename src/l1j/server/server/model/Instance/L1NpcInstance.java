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
package l1j.server.server.model.Instance;

//

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NpcChatTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1MobGroupInfo;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcChatTimer;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1NpcChat;
import l1j.server.server.types.Point;
import l1j.server.server.utils.TimerPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_EXTRA_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HEALING;

public class L1NpcInstance extends L1Character {
    private static final long serialVersionUID = 1L;

    public static final int MOVE_SPEED = 0;
    public static final int ATTACK_SPEED = 1;
    public static final int MAGIC_SPEED = 2;

    public static final int HIDDEN_STATUS_NONE = 0;
    public static final int HIDDEN_STATUS_SINK = 1;
    public static final int HIDDEN_STATUS_FLY = 2;
    public static final int HIDDEN_STATUS_ICE = 3;        // 일팩 얼음성 던전 리뉴얼
    /// /////////////////////////////////////////////////////////////// 몹스킬패턴 추가 : 시작
    public static final int HIDDEN_STATUS_STOM = 4;   //기르타스
    public static final int HIDDEN_STATUS_SINK_ANTA = 5;  //안타라스 - 디텍으로 나오지않도록
    public static final int HIDDEN_STATUS_FLY_LIND = 6;        //린드비오르 - 먹이로 내려오지않도록
    public static final int HIDDEN_STATUS_APPEAR = 7;        //출현효과존재 몬스터
    public static final int HIDDEN_STATUS_DOLGOLLEM = 8;    //돌골렘류 따로
    public static final int HIDDEN_STATUS_SINK_ITEM = 9;    //쉘맨 추후 추가
    public static final int HIDDEN_STATUS_SINK_ANTA_NEW = 10;  //안타라스 리뉴얼
    public static final int HIDDEN_STATUS_SINK_ANTA_NEW2 = 10;  //안타라스 리뉴얼
    public static final int HIDDEN_STATUS_SINK_ANTA_NEW3 = 10;  //안타라스 리뉴얼
    public static final int HIDDEN_STATUS_MOVEDOWN_START = 11;    // 드레이크류
    /// //////////////////////////////////////////////////////////////// 몹스킬패턴 추가 : 끝

    public static final int CHAT_TIMING_APPEARANCE = 0;
    public static final int CHAT_TIMING_DEAD = 1;
    public static final int CHAT_TIMING_HIDE = 2;
    public static final int CHAT_TIMING_GAME_TIME = 3;

    private static Logger _log = Logger.getLogger(L1NpcInstance.class.getName());
    private L1Npc _npcTemplate;

    private L1Spawn _spawn;
    private int _spawnNumber; // L1Spawn로 관리되고 있는 넘버

    private int _petcost; // 펫이 되었을 때의 코스트
    public L1Inventory _inventory = new L1Inventory();
    private L1MobSkillUse mobSkill;
    private static Random _random = new Random();

    // 대상을 처음으로 발견했을 때.(텔레포트용)
    private boolean firstFound = true;

    // 경로 탐색 범위(반경) ※너무 올리고 주의!
    public static int courceRange = 20; // 기본 15 였음.

    // 들이마셔진 MP
    private int _drainedMana = 0;

    // 들이마셔진 HP  // 파멸의 대검 추가
    private int _drainedHp = 0;

    // 휴게
    private boolean _rest = false;

    //버경
    private int num;

    public void set_num(int num) {
        this.num = num;
    }

    public int get_num() {
        return num;
    }

    /**
     * 큐브다
     */

    private int CubeTime;

    public void setCubeTime(int CubeTime) {

        this.CubeTime = CubeTime;
    }

    public boolean isCube() {
        return CubeTime-- <= 0;
    }

    private L1PcInstance CubePc;

    public void setCubePc(L1PcInstance CubePc) {
        this.CubePc = CubePc;
    }

    public L1PcInstance CubePc() {
        return CubePc;
    }

    private int Cube = 20;

    public boolean Cube() {

        return Cube-- <= 0;
    }

    // 랜덤 이동시의 거리와 방향
    private int _randomMoveDistance = 0;

    private int _randomMoveDirection = 0;

    // ■■■■■■■■■■■■■ AI관련 ■■■■■■■■■■■

    interface NpcAI {
        public void start();
    }

    protected void startAI() {
        if (Config.NPCAI_IMPLTYPE == 1) {
            new NpcAITimerImpl().start();
        } else if (Config.NPCAI_IMPLTYPE == 2) {
            new NpcAIThreadImpl().start();
        } else {
            new NpcAITimerImpl().start();
        }
    }

    /**
     * 멀티(코어) 프로세서를 서포트하기 때문에(위해)의 타이머 풀. AI의 실장 타입이 타이머의 경우에 사용된다.
     */
    private static final TimerPool _timerPool = new TimerPool(4);

    class NpcAITimerImpl extends TimerTask implements NpcAI {
        /**
         * 사망 처리의 종료를 기다리는 타이머
         */
        private class DeathSyncTimer extends TimerTask {
            private void schedule(int delay) {
                _timerPool.getTimer().schedule(new DeathSyncTimer(), delay);
            }

            @Override
            public void run() {
                if (isDeathProcessing()) {
                    schedule(getSleepTime());
                    return;
                }
                allTargetClear();
                setAiRunning(false);
            }
        }

        @Override
        public void start() {
            setAiRunning(true);
            _timerPool.getTimer().schedule(NpcAITimerImpl.this, 0);
        }

        private void stop() {
            mobSkill.resetAllSkillUseCount();
            _timerPool.getTimer().schedule(new DeathSyncTimer(), 0); // 사망 동기를 개시
        }

        // 같은 인스턴스를 Timer에 등록할 수 없기 때문에, 고육지책.
        private void schedule(int delay) {
            _timerPool.getTimer().schedule(new NpcAITimerImpl(), delay);
        }

        @Override
        public void run() {
            try {
                if (notContinued()) {
                    stop();
                    return;
                }

                // XXX 동기가 매우 수상한 마비 판정
                if (0 < _paralysisTime) {
                    schedule(_paralysisTime);
                    _paralysisTime = 0;
                    setParalyzed(false);
                    return;
                } else if (isParalyzed() || isSleeped()) {
                    schedule(200);
                    return;
                }

                if (!AIProcess()) { // AI를 계속해야 하는 것이면, 다음의 실행을 스케줄 해, 종료
                    schedule(getSleepTime());
                    return;
                }
                stop();
            } catch (Exception e) {
                _log.log(Level.WARNING, "NpcAI로 예외가 발생했습니다.", e);
            }
        }

        private boolean notContinued() {
            return _destroyed || isDead() || getCurrentHp() <= 0 || getHiddenStatus() != HIDDEN_STATUS_NONE;
        }
    }

    class NpcAIThreadImpl implements Runnable, NpcAI {
        @Override
        public void start() {
            GeneralThreadPool.getInstance().execute(NpcAIThreadImpl.this);
        }

        @Override
        public void run() {
            try {
                setAiRunning(true);
                while (!_destroyed && !isDead() && getCurrentHp() > 0 && getHiddenStatus() == HIDDEN_STATUS_NONE) {
                    /*
                     * if (_paralysisTime > 0) { try {
                     * Thread.sleep(_paralysisTime); } catch (Exception
                     * exception) { break; } finally { setParalyzed(false);
                     * _paralysisTime = 0; } }
                     */
                    while (isParalyzed() || isSleeped()) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            setParalyzed(false);
                        }
                    }

                    if (AIProcess()) {
                        break;
                    }
                    try {
                        // 지정 시간분 thread 정지
                        Thread.sleep(getSleepTime());
                    } catch (Exception e) {
                        break;
                    }
                }
                mobSkill.resetAllSkillUseCount();
                do {
                    try {
                        Thread.sleep(getSleepTime());
                    } catch (Exception e) {
                        break;
                    }
                } while (isDeathProcessing());
                allTargetClear();
                setAiRunning(false);
            } catch (Exception e) {
                _log.log(Level.WARNING, "NpcAI로 예외가 발생했습니다.", e);
            }
        }
    }

    // AI의 처리 (돌아가 값은 AI처리를 종료할지 어떨지)
    private boolean AIProcess() {
        setSleepTime(300);

        checkTarget();
        if (_target == null && _master == null) {
            // 텅텅의 경우는 타겟을 찾아 본다
            // (주인이 있는 경우는 스스로 타겟을 찾지 않는다)
            searchTarget();
        }

        onItemUse();

        if (_target == null) {
            // 타겟이 없는 경우
            checkTargetItem();
            if (isPickupItem() && _targetItem == null) {
                // 아이템 줍는 아이의 경우는 아이템을 찾아 본다
                searchTargetItem();
            }

            if (_targetItem == null) {
                if (noTarget()) {
                    return true;
                }
            } else {
                // onTargetItem();
                L1Inventory groundInventory = L1World.getInstance().getInventory(_targetItem.getX(), _targetItem.getY(), _targetItem.getMapId());
                if (groundInventory.checkItem(_targetItem.getItemId())) {
                    onTargetItem();
                } else {
                    _targetItemList.remove(_targetItem);
                    _targetItem = null;
                    setSleepTime(1000);
                    return false;
                }
            }
        } else { // 타겟이 있는 경우
            if (getHiddenStatus() == HIDDEN_STATUS_NONE) {
                onTarget();
            } else {
                return true;
            }
        }

        return false; // AI처리 속행
    }

    // 아이템 사용 처리(Type에 의해 상당히 다르므로 오바라이드로 실장)
    public void onItemUse() {
    }

    // 타겟을 찾는다(Type에 의해 상당히 다르므로 오바라이드로 실장)
    public void searchTarget() {
    }

    // 유효한 타겟이나 확인 및 다음의 타겟을 설정
    public void checkTarget() {
        if (_target == null || _target.getMapId() != getMapId() || _target.getCurrentHp() <= 0 || _target.isDead() || (_target.isInvisble() && !getNpcTemplate().is_agrocoi() && !_hateList.containsKey(_target))) {
            if (_target != null) {
                tagertClear();
            }
            if (!_hateList.isEmpty()) {
                _target = _hateList.getMaxHateCharacter();
                checkTarget();
            }
        }
    }

    // 헤이트의 설정
    public void setHate(L1Character cha, int hate) {
        if (cha != null && cha.getId() != getId()) {
            if (!isFirstAttack() && hate != 0) {
                // hate += 20; // FA헤이트
                hate += getMaxHp() / 10; // FA헤이트
                setFirstAttack(true);
            }

            _hateList.add(cha, hate);
            _dropHateList.add(cha, hate);
            _target = _hateList.getMaxHateCharacter();
            checkTarget();
        }
    }

    // 링크의 설정
    public void setLink(L1Character cha) {
    }

    // 동료의식에 의해 액티브하게 되는 NPC의 검색(공격자가 플레이어만 유효)
    public void serchLink(L1PcInstance targetPlayer, int family) {
        List<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
        for (Object knownObject : targetKnownObjects) {
            if (knownObject instanceof L1NpcInstance) {
                L1NpcInstance npc = (L1NpcInstance) knownObject;
                if (npc.getNpcTemplate().get_agrofamily() > 0) {
                    // 동료에 대해서 액티브하게 되는 경우
                    if (npc.getNpcTemplate().get_agrofamily() == 1) {
                        // 동종족 에 대해서만 동료의식
                        if (npc.getNpcTemplate().get_family() == family) {
                            npc.setLink(targetPlayer);
                        }
                    } else {
                        // 모든 NPC에 대해서 동료의식
                        npc.setLink(targetPlayer);
                    }
                }
                L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
                if (mobGroupInfo != null) {
                    if (getMobGroupId() != 0 && getMobGroupId() == npc.getMobGroupId()) { // 같은 그룹
                        npc.setLink(targetPlayer);
                    }
                }
            }
        }
    }

    // 타겟이 있는 경우의 처리
    public void onTarget() {
        setActived(true);
        _targetItemList.clear();
        _targetItem = null;
        L1Character target = _target; // 여기에서 앞은_target가 바뀌면(자) 영향 나오므로 별영역에 참조 확보
        if (getAtkspeed() == 0) { // 도망치는 캐릭터
            if (getPassispeed() > 0) { // 이동할 수 있는 캐릭터
                int escapeDistance = 15;
                if (hasSkillEffect(40) == true) {
                    escapeDistance = 1;
                }
                if (getLocation().getTileLineDistance(target.getLocation()) > escapeDistance) { // 타겟으로부터 도망치는 것 종료
                    tagertClear();
                } else { // 타겟으로부터 도망친다
                    int dir = targetReverseDirection(target.getX(), target.getY());
                    dir = checkObject(getX(), getY(), getMapId(), dir);
                    setDirectionMove(dir);
                    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                }
            }
        } else { // 도망치지 않는 캐릭터
            boolean isSkillUse = false;
            isSkillUse = mobSkill.skillUse(target);
            if (isSkillUse == true) {
                setSleepTime(calcSleepTime(mobSkill.getSleepTime(), MAGIC_SPEED));
                return;
            }

            if (isAttackPosition(target.getX(), target.getY(), getNpcTemplate().get_ranged())) {
                setHeading(targetDirection(target.getX(), target.getY()));
                attackTarget(target);
            } else {
                // 공격 불가능 위치

                if (getPassispeed() > 0) {
                    // 이동할 수 있는 캐릭터
                    int distance = getLocation().getTileDistance(target.getLocation());
                    if (firstFound == true && getNpcTemplate().is_teleport() && distance > 3 && distance < 15) {
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            firstFound = false;
                            return;
                        }
                    }

                    if (getNpcTemplate().is_teleport() && 20 > _random.nextInt(100) && getCurrentMp() >= 10 && distance > 6 && distance < 15) { // 텔레포트 이동
                        if (nearTeleport(target.getX(), target.getY()) == true) {
                            return;
                        }
                    }
                    int dir = moveDirection(target.getX(), target.getY());
                    if (dir == -1) {
                        tagertClear();
                    } else {
                        setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                    }
                } else {
                    // 이동할 수 없는 캐릭터(타겟으로부터 배제, PT 때 드롭 찬스가 리셋트 되지만 아무튼 자업자득)
                    tagertClear();
                }
            }
        }
        //잠므 버땅
        if (getHas() == 1) {    ////// 그신버땅 몹
            if (getMaxHp() / 3 > getCurrentHp()) {
			/*	    int rnd = _random.nextInt(100);
				    if (3 > rnd)  */
                { // 타겟으로부터 도망친다
                    int dir = targetReverseDirection(target.getX(), target.getY());
                    dir = checkObject(getX(), getY(), getMapId(), dir);
                    setDirectionMove(dir);
                    setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                    setAtkspeed(0);
                    String chat = "$5011";
                    broadcastPacket(new S_NpcChatPacket(this, chat, 2));
                }
            }
        }    //잠므 버땅
    }

    // 목표를 지정의 스킬로 공격
    public void attackTarget(L1Character target) {
        if (target instanceof L1PcInstance) {
            L1PcInstance player = (L1PcInstance) target;
            if (player.isTeleport()) { // 텔레포트 처리중
                return;
            }
        } else if (target instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) target;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 텔레포트 처리중
                    return;
                }
            }
        } else if (target instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) target;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 텔레포트 처리중
                    return;
                }
            }
        }
        if (this instanceof L1PetInstance) {
            L1PetInstance pet = (L1PetInstance) this;
            L1Character cha = pet.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 텔레포트 처리중
                    return;
                }
            }
        } else if (this instanceof L1SummonInstance) {
            L1SummonInstance summon = (L1SummonInstance) this;
            L1Character cha = summon.getMaster();
            if (cha instanceof L1PcInstance) {
                L1PcInstance player = (L1PcInstance) cha;
                if (player.isTeleport()) { // 텔레포트 처리중
                    return;
                }
            }
        }

        if (target instanceof L1NpcInstance) {
            L1NpcInstance npc = (L1NpcInstance) target;
            if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // 지중에 기어들고 있는지, 날고 있다
                allTargetClear();
                return;
            }
        }

        boolean isCounterBarrier = false;
        L1Attack attack = new L1Attack(this, target);
        if (attack.calcHit()) {
            if (target.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
                L1Magic magic = new L1Magic(target, this);
                boolean isProbability = magic.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
                boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isCounterBarrier = true;
                }
            }
        }

        boolean isMotalbody = false;
        if (attack.calcHit()) {
            if (target.hasSkillEffect(L1SkillId.MOTALBODY)) {
                L1Magic magic = new L1Magic(target, this);
                boolean isProbability = magic.calcProbabilityMagic(L1SkillId.MOTALBODY);
                boolean isShortDistance = attack.isShortDistance();
                if (isProbability && isShortDistance) {
                    isMotalbody = true;
                }
            }
            if (!isMotalbody || !isCounterBarrier) {
                attack.calcDamage();
            }
        }
        if (isCounterBarrier || isMotalbody) {
            attack.actionMotalbody();
            attack.commitMotalbody();
        } else {
            attack.action();
            attack.commit();
        }
        setSleepTime(calcSleepTime(getAtkspeed(), ATTACK_SPEED));
    }

    // 타겟 아이템을 찾는다
    public void searchTargetItem() {
        ArrayList<L1Object> objects = L1World.getInstance().getVisibleObjects(this);
        ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

        for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if (obj != null && obj instanceof L1GroundInventory) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }

        // 줍는 아이템(의 목록)을 랜덤으로 선정
        int pickupIndex = (int) (Math.random() * gInventorys.size());
        L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (L1ItemInstance item : inventory.getItems()) {
            if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) { // 가질 수 있다면 타겟 아이템에 가세한다
                _targetItem = item;
                _targetItemList.add(_targetItem);
            }
        }
    }

    // 날고 있는 상태로부터 아이템을 찾아, 있으면 내려 줍는다
    public void searchItemFromAir() {
        ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

        for (L1Object obj : L1World.getInstance().getVisibleObjects(this)) {
            if (obj != null && obj instanceof L1GroundInventory) {
                gInventorys.add((L1GroundInventory) obj);
            }
        }
        if (gInventorys.size() == 0) {
            return;
        }

        // 줍는 아이템(의 목록)을 랜덤으로 선정
        int pickupIndex = (int) (Math.random() * gInventorys.size());
        L1GroundInventory inventory = gInventorys.get(pickupIndex);
        for (L1ItemInstance item : inventory.getItems()) {
            if (item.getItem().getType() == 6 // potion
                    || item.getItem().getType() == 7) { // food
                if (getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
                    if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
                        setHiddenStatus(HIDDEN_STATUS_NONE);
                        broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
                        setStatus(0);
                        broadcastPacket(new S_NPCPack(this));
                        onNpcAI();
                        startChat(CHAT_TIMING_HIDE);
                        _targetItem = item;
                        _targetItemList.add(_targetItem);
                    }
                }
            }
        }
    }

    public static void shuffle(L1Object[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int t = (int) (Math.random() * i);

            // 선택된 값과 교환한다
            L1Object tmp = arr[i];
            arr[i] = arr[t];
            arr[t] = tmp;
        }
    }

    // 유효한 타겟 아이템이나 확인 및 다음의 타겟 아이템을 설정
    public void checkTargetItem() {
        if (_targetItem == null || _targetItem.getMapId() != getMapId() || getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
            if (!_targetItemList.isEmpty()) {
                _targetItem = _targetItemList.get(0);
                _targetItemList.remove(0);
                checkTargetItem();
            } else {
                _targetItem = null;
            }
        }
    }

    // 타겟 아이템이 있는 경우의 처리
    public void onTargetItem() {
        if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) { // 픽업 가능 위치
            pickupTargetItem(_targetItem);
        } else { // 픽업 불가능 위치
            int dir = moveDirection(_targetItem.getX(), _targetItem.getY());
            if (dir == -1) { // 줍는 것 체념
                _targetItemList.remove(_targetItem);
                _targetItem = null;
            } else { // 타겟 아이템에 이동
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            }
        }
    }

    // 아이템을 줍는다
    public void pickupTargetItem(L1ItemInstance targetItem) {
        L1Inventory groundInventory = L1World.getInstance().getInventory(targetItem.getX(), targetItem.getY(), targetItem.getMapId());
        L1ItemInstance item = groundInventory.tradeItem(targetItem, targetItem.getCount(), getInventory());
        turnOnOffLight();
        onGetItem(item);
        _targetItemList.remove(_targetItem);
        _targetItem = null;
        setSleepTime(1000);
    }

    // 타겟이 없는 경우의 처리 (돌아가 값은 AI처리를 종료할지 어떨지)
    public boolean noTarget() {
        if (_master != null && _master.getMapId() == getMapId() && getLocation().getTileLineDistance(_master.getLocation()) > 2) { // 주인이 같은 맵에 있어 멀어지고 있는 경우는 추적
            int dir = moveDirection(_master.getX(), _master.getY());
            if (dir != -1) {
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            } else {
                return true;
            }
        } else {
            if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
                return true; // 주위에 플레이어가 없어지면(자) AI처리 종료
            }
            // 이동할 수 있는 캐릭터는 랜덤에 움직여 둔다
            if (_master == null && getPassispeed() > 0 && !isRest()) {
                // 그룹에 속하지 않은 or그룹에 속하고 있어 리더의 경우, 랜덤에 움직여 둔다
                L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
                if (mobGroupInfo == null || mobGroupInfo != null && mobGroupInfo.isLeader(this)) {
                    // 이동할 예정의 거리를 이동 끝마치면(자), 새롭게 거리와 방향을 결정한다
                    // 그렇지 않으면, 이동할 예정의 거리를 감소
                    if (_randomMoveDistance == 0) {
                        _randomMoveDistance = _random.nextInt(5) + 1;
                        _randomMoveDirection = _random.nextInt(20);
                        // 홈 포인트로부터 너무 멀어지지 않게, 일정한 확률로 홈 포인트의 방향으로 보정
                        if (getHomeX() != 0 && getHomeY() != 0 && _randomMoveDirection < 8 && _random.nextInt(3) == 0) {
                            _randomMoveDirection = moveDirection(getHomeX(), getHomeY());
                        }
                    } else {
                        _randomMoveDistance--;
                    }
                    int dir = checkObject(getX(), getY(), getMapId(), _randomMoveDirection);
                    if (dir != -1) {
                        setDirectionMove(dir);
                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                    }
                } else { // 리더를 추적
                    L1NpcInstance leader = mobGroupInfo.getLeader();
                    if (getLocation().getTileLineDistance(leader.getLocation()) > 2) {
                        int dir = moveDirection(leader.getX(), leader.getY());
                        if (dir == -1) {
                            return true;
                        } else {
                            setDirectionMove(dir);
                            setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
                        }
                    }
                }
            }
        }
        return false;
    }

    public void onFinalAction(L1PcInstance pc, String s) {
    }

    // 현재의 타겟을 삭제
    public void tagertClear() {
        if (_target == null) {
            return;
        }
        _hateList.remove(_target);
        _target = null;
    }

    // 지정된 타겟을 삭제
    public void targetRemove(L1Character target) {
        _hateList.remove(target);
        if (_target != null && _target.equals(target)) {
            _target = null;
        }
    }

    // 모든 타겟을 삭제
    public void allTargetClear() {
        _hateList.clear();
        _dropHateList.clear();
        _target = null;
        _targetItemList.clear();
        _targetItem = null;
    }

    // 마스터의 설정
    public void setMaster(L1Character cha) {
        _master = cha;
    }

    // 마스터의 취득
    public L1Character getMaster() {
        return _master;
    }

    // AI방아쇠
    public void onNpcAI() {

    }

    // 아이템 정제
    public void refineItem() {

        int[] materials = null;
        int[] counts = null;
        int[] createitem = null;
        int[] createcount = null;

        if (_npcTemplate.get_npcId() == 45032) { // 브롭브
            // 오리하르콘소드의 칼의 몸체
            if (getExp() != 0 && !_inventory.checkItem(20)) {
                materials = new int[]{40508, 40521, 40045};
                counts = new int[]{150, 3, 3};
                createitem = new int[]{20};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            // 롱 소도의 칼의 몸체
            if (getExp() != 0 && !_inventory.checkItem(19)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{150, 3};
                createitem = new int[]{19};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            // 쇼트 소도의 칼의 몸체
            if (getExp() != 0 && !_inventory.checkItem(3)) {
                materials = new int[]{40494, 40521};
                counts = new int[]{50, 1};
                createitem = new int[]{3};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            // 오리하르콘혼
            if (getExp() != 0 && !_inventory.checkItem(100)) {
                materials = new int[]{88, 40508, 40045};
                counts = new int[]{4, 80, 3};
                createitem = new int[]{100};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
            // 미스리르혼
            if (getExp() != 0 && !_inventory.checkItem(89)) {
                materials = new int[]{88, 40494};
                counts = new int[]{2, 80};
                createitem = new int[]{89};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        L1ItemInstance item = _inventory.storeItem(createitem[j], createcount[j]);
                        if (getNpcTemplate().get_digestitem() > 0) {
                            setDigestItem(item);
                        }
                    }
                }
            }
        } else if (_npcTemplate.get_npcId() == 81069) { // 돕페르겐가(퀘스트)
            // 돕페르겐가의 체액
            if (getExp() != 0 && !_inventory.checkItem(40542)) {
                materials = new int[]{40032};
                counts = new int[]{1};
                createitem = new int[]{40542};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
        } else if (_npcTemplate.get_npcId() == 45166 // 잔크오란탄
                || _npcTemplate.get_npcId() == 45167) {
            // 펌프킨의 종
            if (getExp() != 0 && !_inventory.checkItem(40726)) {
                materials = new int[]{40725};
                counts = new int[]{1};
                createitem = new int[]{40726};
                createcount = new int[]{1};
                if (_inventory.checkItem(materials, counts)) {
                    for (int i = 0; i < materials.length; i++) {
                        _inventory.consumeItem(materials[i], counts[i]);
                    }
                    for (int j = 0; j < createitem.length; j++) {
                        _inventory.storeItem(createitem[j], createcount[j]);
                    }
                }
            }
        }
    }

    private boolean _aiRunning = false; // AI가 실행중인가
    // ※AI를 스타트 시킬 때에 벌써 실행되지 않은가 확인할 때에 사용
    private boolean _actived = false; // NPC가 액티브한가
    // ※이 값이 false로_target가 있는 경우, 액티브하게 되어 초행동으로 간주해 헤이 파업 일부등을 사용하게 하는 판정으로 사용
    private boolean _firstAttack = false; // 파스트앗타크 되었는지
    private int _sleep_time; // AI를 정지하는 시간(ms) ※행동을 일으켰을 경우에 소요하는 시간을 세트
    protected L1HateList _hateList = new L1HateList();
    protected L1HateList _dropHateList = new L1HateList();
    // ※공격하는 타겟의 판정과 PT시의 드롭 판정으로 사용
    protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>(); // 다겟트아이템 일람
    protected L1Character _target = null; // 현재의 타겟
    protected L1ItemInstance _targetItem = null; // 현재의 타겟 아이템
    protected L1Character _master = null; // 주인 or그룹리더-
    private boolean _deathProcessing = false; // 사망 처리중인가
    // EXP, Drop 분배중은 타겟 리스트, 헤이 새 파업을 클리어 하지 않는다

    private int _paralysisTime = 0; // Paralysis RestTime

    public void setParalysisTime(int ptime) {
        _paralysisTime = ptime;
    }

    public L1HateList getHateList() {
        return _hateList;
    }

    public int getParalysisTime() {
        return _paralysisTime;
    }

    // HP자연 회복
    public final void startHpRegeneration() {
        int hprInterval = getNpcTemplate().get_hprinterval();
        int hpr = getNpcTemplate().get_hpr();
        if (!_hprRunning && hprInterval > 0 && hpr > 0) {
            _hprTimer = new HprTimer(hpr);
            L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_hprTimer, hprInterval, hprInterval);
            _hprRunning = true;
        }
    }

    public final void stopHpRegeneration() {
        if (_hprRunning) {
            _hprTimer.cancel();
            _hprRunning = false;
        }
    }

    // MP자연 회복
    public final void startMpRegeneration() {
        int mprInterval = getNpcTemplate().get_mprinterval();
        int mpr = getNpcTemplate().get_mpr();
        if (!_mprRunning && mprInterval > 0 && mpr > 0) {
            _mprTimer = new MprTimer(mpr);
            L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_mprTimer, mprInterval, mprInterval);
            _mprRunning = true;
        }
    }

    public final void stopMpRegeneration() {
        if (_mprRunning) {
            _mprTimer.cancel();
            _mprRunning = false;
        }
    }

    // ■■■■■■■■■■■■ 타이머 관련 ■■■■■■■■■■

    // HP자연 회복
    private boolean _hprRunning = false;

    private HprTimer _hprTimer;

    class HprTimer extends TimerTask {
        @Override
        public void run() {
            try {
                if ((!_destroyed && !isDead()) && (getCurrentHp() > 0 && getCurrentHp() < getMaxHp())) {
                    setCurrentHp(getCurrentHp() + _point);
                } else {
                    cancel();
                    _hprRunning = false;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }

        public HprTimer(int point) {
            if (point < 1) {
                point = 1;
            }
            _point = point;
        }

        private final int _point;
    }

    // MP자연 회복
    private boolean _mprRunning = false;

    private MprTimer _mprTimer;

    class MprTimer extends TimerTask {
        @Override
        public void run() {
            try {
                if ((!_destroyed && !isDead()) && (getCurrentHp() > 0 && getCurrentMp() < getMaxMp())) {
                    setCurrentMp(getCurrentMp() + _point);
                } else {
                    cancel();
                    _mprRunning = false;
                }
            } catch (Exception e) {
                _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
            }
        }

        public MprTimer(int point) {
            if (point < 1) {
                point = 1;
            }
            _point = point;
        }

        private final int _point;
    }

    // 아이템 소화
    private Map<Integer, Integer> _digestItems;
    public boolean _digestItemRunning = false;

    class DigestItemTimer implements Runnable {
        @Override
        public void run() {
            _digestItemRunning = true;
            while (!_destroyed && _digestItems.size() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (Exception exception) {
                    break;
                }

                Object[] keys = _digestItems.keySet().toArray();
                for (int i = 0; i < keys.length; i++) {
                    Integer key = (Integer) keys[i];
                    Integer digestCounter = _digestItems.get(key);
                    digestCounter -= 1;
                    if (digestCounter <= 0) {
                        _digestItems.remove(key);
                        L1ItemInstance digestItem = getInventory().getItem(key);
                        if (digestItem != null) {
                            getInventory().removeItem(digestItem, digestItem.getCount());
                        }
                    } else {
                        _digestItems.put(key, digestCounter);
                    }
                }
            }
            _digestItemRunning = false;
        }
    }

    // ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■

    public L1NpcInstance(L1Npc template) {
        setStatus(0);
        setMoveSpeed(0);
        setDead(false);
        setStatus(0);
        setreSpawn(false);

        if (template != null) {
            setting_template(template);
        }
    }

    // 지정의 템플릿으로 각종치를 초기화
    public void setting_template(L1Npc template) {
        _npcTemplate = template;
        int randomlevel = 0;
        double rate = 0;
        double diff = 0;
        setName(template.get_name());
        setNameId(template.get_nameid());
        if (template.get_randomlevel() == 0) { // 랜덤 Lv지정 없음
            setLevel(template.get_level());
        } else { // 랜덤 Lv지정 있어(최소치 :get_level(), 최대치 :get_randomlevel())
            randomlevel = _random.nextInt(template.get_randomlevel() - template.get_level() + 1);
            diff = template.get_randomlevel() - template.get_level();
            rate = randomlevel / diff;
            randomlevel += template.get_level();
            setLevel(randomlevel);
        }
        if (template.get_randomhp() == 0) {
            setMaxHp(template.get_hp());
            setCurrentHpDirect(template.get_hp());
        } else {
            double randomhp = rate * (template.get_randomhp() - template.get_hp());
            int hp = (int) (template.get_hp() + randomhp);
            setMaxHp(hp);
            setCurrentHpDirect(hp);
        }
        if (template.get_randommp() == 0) {
            setMaxMp(template.get_mp());
            setCurrentMpDirect(template.get_mp());
        } else {
            double randommp = rate * (template.get_randommp() - template.get_mp());
            int mp = (int) (template.get_mp() + randommp);
            setMaxMp(mp);
            setCurrentMpDirect(mp);
        }
        if (template.get_randomac() == 0) {
            setAc(template.get_ac());
        } else {
            double randomac = rate * (template.get_randomac() - template.get_ac());
            int ac = (int) (template.get_ac() + randomac);
            setAc(ac);
        }
        if (template.get_randomlevel() == 0) {
            setStr(template.get_str());
            setCon(template.get_con());
            setDex(template.get_dex());
            setInt(template.get_int());
            setWis(template.get_wis());
            setMr(template.get_mr());
        } else {
            setStr((byte) Math.min(template.get_str() + diff, 127));
            setCon((byte) Math.min(template.get_con() + diff, 127));
            setDex((byte) Math.min(template.get_dex() + diff, 127));
            setInt((byte) Math.min(template.get_int() + diff, 127));
            setWis((byte) Math.min(template.get_wis() + diff, 127));
            setMr((byte) Math.min(template.get_mr() + diff, 127));

            addHitup((int) diff * 2);
            addDmgup((int) diff * 2);
        }
        setPassispeed(template.get_passispeed());
        setAtkspeed(template.get_atkspeed());
        setHas(template.get_has());//잠므 버땅
        setAgro(template.is_agro());
        setAgrocoi(template.is_agrocoi());
        setAgrososc(template.is_agrososc());
        setTempCharGfx(template.get_gfxid());
        setGfxId(template.get_gfxid());
        if (template.get_randomexp() == 0) {
            setExp(template.get_exp());
        } else {
            setExp(template.get_randomexp() + randomlevel);
        }
        if (template.get_randomlawful() == 0) {
            setLawful(template.get_lawful());
            setTempLawful(template.get_lawful());
        } else {
            double randomlawful = rate * (template.get_randomlawful() - template.get_lawful());
            int lawful = (int) (template.get_lawful() + randomlawful);
            setLawful(lawful);
            setTempLawful(lawful);
        }
        setPickupItem(template.is_picupitem());
        if (template.is_bravespeed()) {
            setBraveSpeed(1);
        } else {
            setBraveSpeed(0);
        }
        if (template.get_digestitem() > 0) {
            _digestItems = new HashMap<Integer, Integer>();
        }
        setKarma(template.getKarma());
        setLightSize(template.getLightSize());

        mobSkill = new L1MobSkillUse(this);
    }

    private int _passispeed;

    public int getPassispeed() {
        return _passispeed;
    }

    public void setPassispeed(int i) {
        _passispeed = i;
    }

    private int _atkspeed;

    public int getAtkspeed() {
        return _atkspeed;
    }

    public void setAtkspeed(int i) {
        _atkspeed = i;
    }

    //잠므 버땅
    private int _has;

    public int getHas() {
        return _has;
    }

    public void setHas(int i) {
        _has = i;
    }//잠므 버땅

    private boolean _pickupItem;

    public boolean isPickupItem() {
        return _pickupItem;
    }

    public void setPickupItem(boolean flag) {
        _pickupItem = flag;
    }

    @Override
    public L1Inventory getInventory() {
        return _inventory;
    }

    public void setInventory(L1Inventory inventory) {
        _inventory = inventory;
    }

    public L1Npc getNpcTemplate() {
        return _npcTemplate;
    }

    public int getNpcId() {
        return _npcTemplate.get_npcId();
    }

    public void setPetcost(int i) {
        _petcost = i;
    }

    public int getPetcost() {
        return _petcost;
    }

    public void setSpawn(L1Spawn spawn) {
        _spawn = spawn;
    }

    public L1Spawn getSpawn() {
        return _spawn;
    }

    public void setSpawnNumber(int number) {
        _spawnNumber = number;
    }

    public int getSpawnNumber() {
        return _spawnNumber;
    }

    // 오브젝트 ID를 SpawnTask에 건네주어 재이용한다
    // 그룹 monster는 복잡하게 되므로 재이용하지 않는다
    public void onDecay(boolean isReuseId) {
        int id = 0;
        if (isReuseId) {
            id = getId();
        } else {
            id = 0;
        }
        _spawn.executeSpawnTask(_spawnNumber, id);
    }

    @Override
    public void onPerceive(L1PcInstance perceivedFrom) {
        perceivedFrom.addKnownObject(this);
        perceivedFrom.sendPackets(new S_NPCPack(this));
        onNpcAI();
    }

    public void deleteMe() {
        _destroyed = true;
        if (getInventory() != null) {
            getInventory().clearItems();
        }
        allTargetClear();
        _master = null;
        L1World.getInstance().removeVisibleObject(this);
        L1World.getInstance().removeObject(this);
        List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(this);
        if (players.size() > 0) {
            S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
            for (L1PcInstance pc : players) {
                if (pc != null) {
                    pc.removeKnownObject(this);
                    // if(!L1Character.distancepc(user, this))
                    pc.sendPackets(s_deleteNewObject);
                }
            }
        }
        removeAllKnownObjects();

        // 리스파운 설정
        L1MobGroupInfo mobGroupInfo = getMobGroupInfo();
        if (mobGroupInfo == null) {
            if (isReSpawn()) {
                onDecay(true);
            }
        } else {
            if (mobGroupInfo.removeMember(this) == 0) { // 그룹 멤버 전멸
                setMobGroupInfo(null);
                if (isReSpawn()) {
                    onDecay(false);
                }
            }
        }
    }

    public void ReceiveManaDamage(L1Character attacker, int damageMp) {
    }

    public void receiveDamage(L1Character attacker, int damage) {
    }

    public void setDigestItem(L1ItemInstance item) {
        _digestItems.put(new Integer(item.getId()), new Integer(getNpcTemplate().get_digestitem()));
        if (!_digestItemRunning) {
            DigestItemTimer digestItemTimer = new DigestItemTimer();
            GeneralThreadPool.getInstance().execute(digestItemTimer);
        }
    }

    public void onGetItem(L1ItemInstance item) {
        refineItem();
        getInventory().shuffle();
        if (getNpcTemplate().get_digestitem() > 0) {
            setDigestItem(item);
        }
    }

    public void approachPlayer(L1PcInstance pc) {
        if (pc.hasSkillEffect(60) || pc.hasSkillEffect(97)) { // 인비지비리티, 브라인드하이딘그중
            return;
        }
        if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
            if (getCurrentHp() == getMaxHp()) {
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
                    appearOnGround(pc);
                }
            }
            /////////////////////////////////////////////////////////////// 기르타스 잠수위해 수정 - 몹스킬패턴 수정 : 시작
        } else if (getHiddenStatus() == HIDDEN_STATUS_DOLGOLLEM) { // 돌골렘류
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
                appearOnGround(pc);
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_APPEAR) { // 출현 이펙 존재 몬스터
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 30) {
                appearOnGround(pc);
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_STOM) { // 기르타스
            Random random = new Random();
            int RndChance = random.nextInt(100) + 1;
            setCurrentHp(getCurrentHp() + 300);
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 3) {
                pc.setCurrentHp(pc.getCurrentHp() - (random.nextInt(30) + 1));
            }
            if (RndChance <= 80) {
                if (getCurrentHp() == getMaxHp()) {
                    if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 6) {
                        appearOnGround(pc);
                    }
                }
            } else {
                if (getCurrentHp() >= getMaxHp() / 2) {
                    if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 3) {
                        appearOnGround(pc);
                    }
                }
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ITEM) {  // 쉘맨 추후 추가
            searchItemFromAir();
            if (getCurrentHp() >= getMaxHp() / 2) {
                if (pc.getLocation().getTileLineDistance(this.getLocation()) >= 5) {
                    appearOnGround(pc);
                }
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA) {
            if (getCurrentHp() >= getMaxHp() / 3) {           // 안타라스
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 3) {
                    appearOnGround(pc);
                }
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA_NEW) {
            if (getCurrentMp() >= getMaxMp() / 2) { //엠피          // 안타라스(리뉴얼)
                if (pc.getLocation().getTileLineDistance(this.getLocation()) >= 3 && pc.getLocation().getTileLineDistance(this.getLocation()) < 4) {
                    appearOnGround(pc);
                }
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_MOVEDOWN_START) {  // 드레이크류
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 30) {
                //	|| getCurrentHp() < getMaxHp()
                appearOnGround(pc);
            }
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY_LIND) {
            if (getCurrentHp() >= getMaxHp() / 3) {           // 린드비오르
                if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 3) {
                    appearOnGround(pc);
                }
            }
            //////////////////////////////////////////////////////////////////// 기르타스 잠수위해 수정 - 몹스킬패턴 수정 : 끝
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
            //if (getCurrentHp() == getMaxHp()) {
            if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
                appearOnGround(pc);
            }
// }
        } else if (getHiddenStatus() == HIDDEN_STATUS_ICE) {
            if (getCurrentHp() < getMaxHp()) {
                appearOnGround(pc);
            }
        }
    }

    public void appearOnGround(L1PcInstance pc) {
        if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // 인비지비리티, 브라인드하이딘그중 이외, GM 이외
                    && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI(); // monster의 AI를 개시
            ////////////////////////////////////////////////////////////////// 기르타스 잠수위해 추가 - 몹스킬패턴 추가 : 시작
        } else if (getHiddenStatus() == HIDDEN_STATUS_APPEAR) {  // 출현효과 존재 몬스터
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Appear));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_STOM) {    // 기르타스
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), 11));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ITEM) { // 쉘맨 추후 추가
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), 13));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA || getHiddenStatus() == HIDDEN_STATUS_ICE || getHiddenStatus() == HIDDEN_STATUS_DOLGOLLEM || getHiddenStatus() == HIDDEN_STATUS_SINK_ANTA_NEW) {                                        // 안타라스, 얼음성 몹, 돌골렘
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), 11));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) //
                    && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_MOVEDOWN_START) {    // 드레이크류
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), 44));
            int MoveType = _random.nextInt(100) + 1;
            if (MoveType < 50) {
                setStatus(0);
            } else {
                setStatus(4);
            }
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) //
                    && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY_LIND) {    // 린드비오르
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI();
            /////////////////////////////////////////////////////////////////// 기르타스 잠수위해 추가 - 몹스킬패턴 추가 : 끝
        } else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
            setHiddenStatus(HIDDEN_STATUS_NONE);
            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Movedown));
            setStatus(0);
            broadcastPacket(new S_NPCPack(this));
            if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // 인비지비리티, 브라인드하이딘그중 이외, GM 이외
                    && !pc.isGm()) {
                _hateList.add(pc, 0);
                _target = pc;
            }
            onNpcAI(); // monster의 AI를 개시
            startChat(CHAT_TIMING_HIDE);
        }
    }

    // ■■■■■■■■■■■■■ 이동 관련 ■■■■■■■■■■■

    // 지정된 방향으로 이동시킨다
    public void setDirectionMove(int dir) {
        if (dir >= 0) {
            int nx = 0;
            int ny = 0;

            switch (dir) {
                case 1:
                    nx = 1;
                    ny = -1;
                    setHeading(1);
                    break;

                case 2:
                    nx = 1;
                    ny = 0;
                    setHeading(2);
                    break;

                case 3:
                    nx = 1;
                    ny = 1;
                    setHeading(3);
                    break;

                case 4:
                    nx = 0;
                    ny = 1;
                    setHeading(4);
                    break;

                case 5:
                    nx = -1;
                    ny = 1;
                    setHeading(5);
                    break;

                case 6:
                    nx = -1;
                    ny = 0;
                    setHeading(6);
                    break;

                case 7:
                    nx = -1;
                    ny = -1;
                    setHeading(7);
                    break;

                case 0:
                    nx = 0;
                    ny = -1;
                    setHeading(0);
                    break;

                default:
                    break;

            }

            getMap().setPassable(getLocation(), true);

            int nnx = getX() + nx;
            int nny = getY() + ny;
            setX(nnx);
            setY(nny);

            getMap().setPassable(getLocation(), false);

            broadcastPacket(new S_MoveCharPacket(this));

            // movement_distance 매스 이상 멀어지면(자) 홈 포인트에 텔레포트
            if (getMovementDistance() > 0) {
                if (this instanceof L1GuardInstance || this instanceof L1MerchantInstance || this instanceof L1MonsterInstance || this instanceof L1RguardInstance) {//카시 경비병
                    if (getLocation().getLineDistance(new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
                        teleport(getHomeX(), getHomeY(), getHeading());
                    }
                }
            }
            // 원한으로 가득 찬 솔저 고우스트, 원한으로 가득 찬 고우스트, 원한으로 가득 찬 하멜 장군
			/*if (getNpcTemplate().get_npcId() >= 45912
					&& getNpcTemplate().get_npcId() <= 45916) {
				if (getX() >= 32591 && getX() <= 32644
						&& getY() >= 32643 && getY() <= 32688
								&& getMapId() == 4) {
					teleport(getHomeX(), getHomeY(), getHeading());
				}
			}*/            // 얘네들 계속 텔레포트해서 주석처리 해놓음
        }
    }

    public int moveDirection(int x, int y) { // 목표점X 목표점Y
        return moveDirection(x, y, getLocation().getLineDistance(new Point(x, y)));
    }

    // 목표까지의 거리에 응해 최적이라고 생각되는 루틴으로 진행될 방향을 돌려준다
    public int moveDirection(int x, int y, double d) { // 목표점X 목표점Y 목표까지의 거리
        int dir = 0;
        if (hasSkillEffect(40) == true && d >= 2D) { // 다크네스가 걸려있어, 거리가 2이상의 경우 추적 종료
            return -1;
        } else if (d > 30D) { // 거리가 격렬하고 먼 경우는 추적 종료
            return -1;
        } else if (d > courceRange) { // 거리가 먼 경우는 단순 계산
            dir = targetDirection(x, y);
            dir = checkObject(getX(), getY(), getMapId(), dir);
        } else { // 목표까지의 최단 경로를 탐색
            dir = _serchCource(x, y);
            if (dir == -1) { // 목표까지의 경로가분경우는 우선 가까워져 둔다
                dir = targetDirection(x, y);
                if (!isExsistCharacterBetweenTarget(dir)) {
                    dir = checkObject(getX(), getY(), getMapId(), dir);
                }
            }
        }
        return dir;
    }

    private boolean isExsistCharacterBetweenTarget(int dir) {
        if (!(this instanceof L1MonsterInstance)) { // monster 이외는 대상외
            return false;
        }
        if (_target == null) { // 타겟이 없는 경우
            return false;
        }

        int locX = getX();
        int locY = getY();
        int targetX = locX;
        int targetY = locY;

        if (dir == 1) {
            targetX = locX + 1;
            targetY = locY - 1;
        } else if (dir == 2) {
            targetX = locX + 1;
        } else if (dir == 3) {
            targetX = locX + 1;
            targetY = locY + 1;
        } else if (dir == 4) {
            targetY = locY + 1;
        } else if (dir == 5) {
            targetX = locX - 1;
            targetY = locY + 1;
        } else if (dir == 6) {
            targetX = locX - 1;
        } else if (dir == 7) {
            targetX = locX - 1;
            targetY = locY - 1;
        } else if (dir == 0) {
            targetY = locY - 1;
        }

        for (L1Object object : L1World.getInstance().getVisibleObjects(this, 1)) {
            // PC, Summon, Pet가 있는 경우
            if (object instanceof L1PcInstance || object instanceof L1SummonInstance || object instanceof L1PetInstance) {
                L1Character cha = (L1Character) object;
                // 진행 방향으로 가로막고 서고 있는 경우, 타겟 리스트에 가세한다
                if (cha.getX() == targetX && cha.getY() == targetY && cha.getMapId() == getMapId()) {
                    if (object instanceof L1PcInstance) {
                        L1PcInstance pc = (L1PcInstance) object;
                        if (pc.isGhost()) { // UB관전중의 PC는 제외하다
                            continue;
                        }
                    }
                    _hateList.add(cha, 0);
                    _target = cha;
                    return true;
                }
            }
        }
        return false;
    }

    // 목표의 역방향을 돌려준다
    public int targetReverseDirection(int tx, int ty) { // 목표점X 목표점Y
        int dir = targetDirection(tx, ty);
        dir += 4;
        if (dir > 7) {
            dir -= 8;
        }
        return dir;
    }

    // 진행되고 싶은 방향으로 장애물이 없는가 확인, 어느 경우는 전방 기울기 좌우도 확인 후진방향을 돌려준다
    // ※종래 있던 처리에, 백할 수 없는 사양을 생략해, 목표의 반대(좌우 포함하기)에는 진행되지 않게 한 것
    public static int checkObject(int x, int y, short m, int d) { // 기점 X 기점 Y
        // 맵 ID
        // 진행 방향
        L1Map map = L1WorldMap.getInstance().getMap(m);
        if (d == 1) {
            if (map.isPassable(x, y, 1)) {
                return 1;
            } else if (map.isPassable(x, y, 0)) {
                return 0;
            } else if (map.isPassable(x, y, 2)) {
                return 2;
            }
        } else if (d == 2) {
            if (map.isPassable(x, y, 2)) {
                return 2;
            } else if (map.isPassable(x, y, 1)) {
                return 1;
            } else if (map.isPassable(x, y, 3)) {
                return 3;
            }
        } else if (d == 3) {
            if (map.isPassable(x, y, 3)) {
                return 3;
            } else if (map.isPassable(x, y, 2)) {
                return 2;
            } else if (map.isPassable(x, y, 4)) {
                return 4;
            }
        } else if (d == 4) {
            if (map.isPassable(x, y, 4)) {
                return 4;
            } else if (map.isPassable(x, y, 3)) {
                return 3;
            } else if (map.isPassable(x, y, 5)) {
                return 5;
            }
        } else if (d == 5) {
            if (map.isPassable(x, y, 5)) {
                return 5;
            } else if (map.isPassable(x, y, 4)) {
                return 4;
            } else if (map.isPassable(x, y, 6)) {
                return 6;
            }
        } else if (d == 6) {
            if (map.isPassable(x, y, 6)) {
                return 6;
            } else if (map.isPassable(x, y, 5)) {
                return 5;
            } else if (map.isPassable(x, y, 7)) {
                return 7;
            }
        } else if (d == 7) {
            if (map.isPassable(x, y, 7)) {
                return 7;
            } else if (map.isPassable(x, y, 6)) {
                return 6;
            } else if (map.isPassable(x, y, 0)) {
                return 0;
            }
        } else if (d == 0) {
            if (map.isPassable(x, y, 0)) {
                return 0;
            } else if (map.isPassable(x, y, 7)) {
                return 7;
            } else if (map.isPassable(x, y, 1)) {
                return 1;
            }
        }
        return -1;
    }

    // 목표까지의 최단 경로의 방향을 돌려준다
    // ※목표를 중심으로 한 탐색 범위의 맵으로 탐색
    private int _serchCource(int x, int y) // 목표점X 목표점Y
    {
        int i;
        int locCenter = courceRange + 1;
        int diff_x = x - locCenter; // X의 실제의 로케이션과의 차이
        int diff_y = y - locCenter; // Y의 실제의 로케이션과의 차이
        int[] locBace = {getX() - diff_x, getY() - diff_y, 0, 0}; // X Y
        // 방향
        // 초기 방향
        int[] locNext = new int[4];
        int[] locCopy;
        int[] dirFront = new int[5];
        boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
        LinkedList<int[]> queueSerch = new LinkedList<int[]>();

        // 탐색용 맵의 설정
        for (int j = courceRange * 2 + 1; j > 0; j--) {
            for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
                serchMap[j][locCenter + i] = true;
                serchMap[j][locCenter - i] = true;
            }
        }

        // 초기 방향의 설치
        int[] firstCource = {2, 4, 6, 0, 1, 3, 5, 7};
        for (i = 0; i < 8; i++) {
            System.arraycopy(locBace, 0, locNext, 0, 4);
            _moveLocation(locNext, firstCource[i]);
            if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                // 최단 경로가 발견되었을 경우:근처
                return firstCource[i];
            }
            if (serchMap[locNext[0]][locNext[1]]) {
                int tmpX = locNext[0] + diff_x;
                int tmpY = locNext[1] + diff_y;
                boolean found = false;
                if (i == 0) {
                    found = getMap().isPassable(tmpX, tmpY + 1, i);
                } else if (i == 1) {
                    found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
                } else if (i == 2) {
                    found = getMap().isPassable(tmpX - 1, tmpY, i);
                } else if (i == 3) {
                    found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
                } else if (i == 4) {
                    found = getMap().isPassable(tmpX, tmpY - 1, i);
                } else if (i == 5) {
                    found = getMap().isPassable(tmpX + 1, tmpY - 1, i);
                } else if (i == 6) {
                    found = getMap().isPassable(tmpX + 1, tmpY, i);
                } else if (i == 7) {
                    found = getMap().isPassable(tmpX + 1, tmpY + 1, i);
                }
                if (found)// 이동 경로가 있었을 경우
                {
                    locCopy = new int[4];
                    System.arraycopy(locNext, 0, locCopy, 0, 4);
                    locCopy[2] = firstCource[i];
                    locCopy[3] = firstCource[i];
                    queueSerch.add(locCopy);
                }
                serchMap[locNext[0]][locNext[1]] = false;
            }
        }
        locBace = null;

        // 최단 경로를 탐색
        while (queueSerch.size() > 0) {
            locBace = queueSerch.removeFirst();
            _getFront(dirFront, locBace[2]);
            for (i = 4; i >= 0; i--) {
                System.arraycopy(locBace, 0, locNext, 0, 4);
                _moveLocation(locNext, dirFront[i]);
                if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
                    return locNext[3];
                }
                if (serchMap[locNext[0]][locNext[1]]) {
                    int tmpX = locNext[0] + diff_x;
                    int tmpY = locNext[1] + diff_y;
                    boolean found = false;
                    if (i == 0) {
                        found = getMap().isPassable(tmpX, tmpY + 1, i);
                    } else if (i == 1) {
                        found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
                    } else if (i == 2) {
                        found = getMap().isPassable(tmpX - 1, tmpY, i);
                    } else if (i == 3) {
                        found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
                    } else if (i == 4) {
                        found = getMap().isPassable(tmpX, tmpY - 1, i);
                    }
                    if (found) // 이동 경로가 있었을 경우
                    {
                        locCopy = new int[4];
                        System.arraycopy(locNext, 0, locCopy, 0, 4);
                        locCopy[2] = dirFront[i];
                        queueSerch.add(locCopy);
                    }
                    serchMap[locNext[0]][locNext[1]] = false;
                }
            }
            locBace = null;
        }
        return -1; // 목표까지의 경로가 없는 경우
    }

    private void _moveLocation(int[] ary, int d) {
        if (d == 1) {
            ary[0] = ary[0] + 1;
            ary[1] = ary[1] - 1;
        } else if (d == 2) {
            ary[0] = ary[0] + 1;
        } else if (d == 3) {
            ary[0] = ary[0] + 1;
            ary[1] = ary[1] + 1;
        } else if (d == 4) {
            ary[1] = ary[1] + 1;
        } else if (d == 5) {
            ary[0] = ary[0] - 1;
            ary[1] = ary[1] + 1;
        } else if (d == 6) {
            ary[0] = ary[0] - 1;
        } else if (d == 7) {
            ary[0] = ary[0] - 1;
            ary[1] = ary[1] - 1;
        } else if (d == 0) {
            ary[1] = ary[1] - 1;
        }
        ary[2] = d;
    }

    private void _getFront(int[] ary, int d) {
        if (d == 1) {
            ary[4] = 2;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 7;
        } else if (d == 2) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 3;
        } else if (d == 3) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 1;
            ary[1] = 3;
            ary[0] = 5;
        } else if (d == 4) {
            ary[4] = 2;
            ary[3] = 4;
            ary[2] = 6;
            ary[1] = 3;
            ary[0] = 5;
        } else if (d == 5) {
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 3;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 6) {
            ary[4] = 4;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 7) {
            ary[4] = 6;
            ary[3] = 0;
            ary[2] = 1;
            ary[1] = 5;
            ary[0] = 7;
        } else if (d == 0) {
            ary[4] = 2;
            ary[3] = 6;
            ary[2] = 0;
            ary[1] = 1;
            ary[0] = 7;
        }
    }

    // ■■■■■■■■■■■■ 아이템 관련 ■■■■■■■■■■

    private void useHealPotion(int healHp, int effectId) {
        broadcastPacket(new S_SkillSound(getId(), effectId));
        if (this.hasSkillEffect(L1SkillId.POLLUTE_WATER)) { // 포르트워타중은 회복량1/2배
            healHp /= 2;
        }
        if (this instanceof L1PetInstance) {
            ((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);
        } else if (this instanceof L1SummonInstance) {
            ((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);
        } else {
            setCurrentHpDirect(getCurrentHp() + healHp);
        }
    }

    private void useHastePotion(int time) {
        broadcastPacket(new S_SkillHaste(getId(), 1, time));
        broadcastPacket(new S_SkillSound(getId(), 191));
        setMoveSpeed(1);
        setSkillEffect(L1SkillId.STATUS_HASTE, time * 1000);
    }

    // 아이템의 사용 판정 및 사용
    public static final int USEITEM_HEAL = 0;
    public static final int USEITEM_HASTE = 1;
    public static int[] healPotions = {POTION_OF_GREATER_HEALING, POTION_OF_EXTRA_HEALING, POTION_OF_HEALING};
    public static int[] haestPotions = {B_POTION_OF_GREATER_HASTE_SELF, POTION_OF_GREATER_HASTE_SELF, B_POTION_OF_HASTE_SELF, POTION_OF_HASTE_SELF};

    public void useItem(int type, int chance) { // 사용하는 종류 사용할 가능성(%)
        if (hasSkillEffect(71)) {
            return; // 디케이포션 상태나 체크
        }

        Random random = new Random();
        if (random.nextInt(100) > chance) {
            return; // 사용할 가능성
        }

        if (type == USEITEM_HEAL) { // 회복계 일부
            // 회복량의 큰 순서
            if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
                useHealPotion(75, 197);
            } else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
                useHealPotion(45, 194);
            } else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
                useHealPotion(15, 189);
            }
        } else if (type == USEITEM_HASTE) { // 헤이 파업계 일부
            if (hasSkillEffect(1001)) {
                return; // 헤이 파업 상태 체크
            }

            // 효과의 긴 순서
            if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
                useHastePotion(2100);
            } else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF, 1)) {
                useHastePotion(1800);
            } else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(350);
            } else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
                useHastePotion(300);
            }
        }
    }

    // ■■■■■■■■■■■■■ 스킬 관련(npcskills 테이블 실장되면(자) 지울지도) ■■■■■■■■■■■

    // 목표의 근처에 텔레포트
    public boolean nearTeleport(int nx, int ny) {
        int rdir = _random.nextInt(8);
        int dir;
        for (int i = 0; i < 8; i++) {
            dir = rdir + i;
            if (dir > 7) {
                dir -= 8;
            }
            if (dir == 1) {
                nx++;
                ny--;
            } else if (dir == 2) {
                nx++;
            } else if (dir == 3) {
                nx++;
                ny++;
            } else if (dir == 4) {
                ny++;
            } else if (dir == 5) {
                nx--;
                ny++;
            } else if (dir == 6) {
                nx--;
            } else if (dir == 7) {
                nx--;
                ny--;
            } else if (dir == 0) {
                ny--;
            }
            if (getMap().isPassable(nx, ny)) {
                dir += 4;
                if (dir > 7) {
                    dir -= 8;
                }
                teleport(nx, ny, dir);
                setCurrentMp(getCurrentMp() - 10);
                return true;
            }
        }
        return false;
    }

    // 목표에 텔레포트
    public void teleport(int nx, int ny, int dir) {
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            pc.sendPackets(new S_SkillSound(getId(), 169));
            pc.sendPackets(new S_RemoveObject(this));
            pc.removeKnownObject(this);
        }
        setX(nx);
        setY(ny);
        setHeading(dir);
    }

    // ----------From L1Character-------------
    private String _nameId; // ● 네임 ID

    public String getNameId() {
        return _nameId;
    }

    public void setNameId(String s) {
        _nameId = s;
    }

    private boolean _Agro; // ● 액티브한가

    public boolean isAgro() {
        return _Agro;
    }

    public void setAgro(boolean flag) {
        _Agro = flag;
    }

    private boolean _Agrocoi; // ● 인비지아크티브인가

    public boolean isAgrocoi() {
        return _Agrocoi;
    }

    public void setAgrocoi(boolean flag) {
        _Agrocoi = flag;
    }

    private boolean _Agrososc; // ● 변신 액티브한가

    public boolean isAgrososc() {
        return _Agrososc;
    }

    public void setAgrososc(boolean flag) {
        _Agrososc = flag;
    }

    private int _homeX; // ● 홈 포인트 X(monster가 돌아오는 위치라든지 애완동물의 경계 위치)

    public int getHomeX() {
        return _homeX;
    }

    public void setHomeX(int i) {
        _homeX = i;
    }

    private int _homeY; // ● 홈 포인트 Y(monster가 돌아오는 위치라든지 애완동물의 경계 위치)

    public int getHomeY() {
        return _homeY;
    }

    public void setHomeY(int i) {
        _homeY = i;
    }

    private boolean _reSpawn; // ● 재팝 할지 어떨지

    public boolean isReSpawn() {
        return _reSpawn;
    }

    public void setreSpawn(boolean flag) {
        _reSpawn = flag;
    }

    private int _lightSize; // ● 라이트 0.없음 1~14.크기

    public int getLightSize() {
        return _lightSize;
    }

    public void setLightSize(int i) {
        _lightSize = i;
    }

    private boolean _weaponBreaked; // ● 웨폰브레이크중인가 어떤가

    public boolean isWeaponBreaked() {
        return _weaponBreaked;
    }

    public void setWeaponBreaked(boolean flag) {
        _weaponBreaked = flag;
    }

    private int _hiddenStatus; // ● 지중에 기어들거나 하늘을 날고 있는 상태

    public int getHiddenStatus() {
        return _hiddenStatus;
    }

    public void setHiddenStatus(int i) {
        _hiddenStatus = i;
    }

    // 행동 거리
    private int _movementDistance = 0;

    public int getMovementDistance() {
        return _movementDistance;
    }

    public void setMovementDistance(int i) {
        _movementDistance = i;
    }

    // 표시용 로우훌
    private int _tempLawful = 0;

    public int getTempLawful() {
        return _tempLawful;
    }

    public void setTempLawful(int i) {
        _tempLawful = i;
    }

    protected int calcSleepTime(int sleepTime, int type) {
        switch (getMoveSpeed()) {
            case 0: // 통상
                break;
            case 1: // 헤이 파업
                sleepTime -= (sleepTime * 0.25);
                break;
            case 2: // 슬로우
                sleepTime *= 2;
                break;
        }
        if (getBraveSpeed() == 1) {
            sleepTime -= (sleepTime * 0.25);
        }
        if (hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
            if (type == ATTACK_SPEED || type == MAGIC_SPEED) {
                sleepTime += (sleepTime * 0.25);
            }
        }
        return sleepTime;
    }

    protected void setAiRunning(boolean aiRunning) {
        _aiRunning = aiRunning;
    }

    protected boolean isAiRunning() {
        return _aiRunning;
    }

    protected void setActived(boolean actived) {
        _actived = actived;
    }

    protected boolean isActived() {
        return _actived;
    }

    protected void setFirstAttack(boolean firstAttack) {
        _firstAttack = firstAttack;
    }

    protected boolean isFirstAttack() {
        return _firstAttack;
    }

    protected void setSleepTime(int sleep_time) {
        _sleep_time = sleep_time;
    }

    protected int getSleepTime() {
        return _sleep_time;
    }

    protected void setDeathProcessing(boolean deathProcessing) {
        _deathProcessing = deathProcessing;
    }

    protected boolean isDeathProcessing() {
        return _deathProcessing;
    }

    public int drainMana(int drain) {
        if (_drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
            return 0;
        }
        int result = Math.min(drain, getCurrentMp());
        if (_drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
            result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedMana;
        }
        _drainedMana += result;
        return result;
    }

    /// 파멸의 대검 추가 ///
    public int drainHp(int drain) {
        if (_drainedHp >= Config.HP_DRAIN_LIMIT_PER_NPC) {
            return 0;
        }
        int result = Math.min(drain, getCurrentHp());
        if (_drainedHp + result > Config.HP_DRAIN_LIMIT_PER_NPC) {
            result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedHp;
        }
        _drainedHp += result;
        return result;
    }

    /// 파멸의 대검 추가 ///

    public boolean _destroyed = false; // 이 인스턴스가 파기되고 있을까

    // ※파기 후에 움직이지 않게 강제적으로 AI등의 thread 처리 중지(만약을 위해)

    // NPC가 다른 NPC로 바뀌는 경우의 처리
    protected void transform(int transformId) {
        stopHpRegeneration();
        stopMpRegeneration();
        int transformGfxId = getNpcTemplate().getTransformGfxId();
        if (transformGfxId != 0) {
            broadcastPacket(new S_SkillSound(getId(), transformGfxId));
        }
        L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);
        setting_template(npcTemplate);

        broadcastPacket(new S_ChangeShape(getId(), getTempCharGfx()));
        for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
            onPerceive(pc);
        }

    }

    public void setRest(boolean _rest) {
        this._rest = _rest;
    }

    public boolean isRest() {
        return _rest;
    }

    private boolean _isResurrect;

    public boolean isResurrect() {
        return _isResurrect;
    }

    public void setResurrect(boolean flag) {
        _isResurrect = flag;
    }

    @Override
    public synchronized void resurrect(int hp) {
        if (_destroyed) {
            return;
        }
        if (_deleteTask != null) {
            if (!_future.cancel(false)) { // 캔슬할 수 없다
                return;
            }
            _deleteTask = null;
            _future = null;
        }
        super.resurrect(hp);

        // 왈가닥 세레이션을 효과없이 걸친다
        // 본래는 사망시에 실시해야 하지만, 부하가 커지기 (위해)때문에 부활시에 실시한다
        L1SkillUse skill = new L1SkillUse();
        skill.handleCommands(null, L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0, L1SkillUse.TYPE_LOGIN, this);
    }

    // 죽고 나서 사라질 때까지의 시간 계측용
    private DeleteTimer _deleteTask;
    private ScheduledFuture<?> _future = null;

    protected synchronized void startDeleteTimer() {
        if (_deleteTask != null) {
            return;
        }
        _deleteTask = new DeleteTimer(getId());
        _future = GeneralThreadPool.getInstance().schedule(_deleteTask, Config.NPC_DELETION_TIME * 1000);
    }

    protected static class DeleteTimer extends TimerTask {
        private int _id;

        protected DeleteTimer(int oId) {
            _id = oId;
            if (!(L1World.getInstance().findObject(_id) instanceof L1NpcInstance)) {
                throw new IllegalArgumentException("allowed only L1NpcInstance");
            }
        }

        @Override
        public void run() {
            L1NpcInstance npc = (L1NpcInstance) L1World.getInstance().findObject(_id);
            if (npc == null || !npc.isDead() || npc._destroyed) {
                return; // 부활하고 있어, 이미 파기가 끝난 상태라면 뽑아라
            }
            try {
                npc.deleteMe();
            } catch (Exception e) { // 절대 예외를 던지지 않게
                e.printStackTrace();
            }
        }
    }

    private L1MobGroupInfo _mobGroupInfo = null;

    public boolean isInMobGroup() {
        return getMobGroupInfo() != null;
    }

    public L1MobGroupInfo getMobGroupInfo() {
        return _mobGroupInfo;
    }

    public void setMobGroupInfo(L1MobGroupInfo m) {
        _mobGroupInfo = m;
    }

    private int _mobGroupId = 0;

    public int getMobGroupId() {
        return _mobGroupId;
    }

    public void setMobGroupId(int i) {
        _mobGroupId = i;
    }

    public void startChat(int chatTiming) {
        // 출현시의 채팅에도 불구하고 사망중, 사망시의 채팅에도 불구하고 생존중
        if (chatTiming == CHAT_TIMING_APPEARANCE && this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_DEAD && !this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_HIDE && this.isDead()) {
            return;
        }
        if (chatTiming == CHAT_TIMING_GAME_TIME && this.isDead()) {
            return;
        }

        int npcId = this.getNpcTemplate().get_npcId();
        L1NpcChat npcChat = null;
        if (chatTiming == CHAT_TIMING_APPEARANCE) {
            npcChat = NpcChatTable.getInstance().getTemplateAppearance(npcId);
        } else if (chatTiming == CHAT_TIMING_DEAD) {
            npcChat = NpcChatTable.getInstance().getTemplateDead(npcId);
        } else if (chatTiming == CHAT_TIMING_HIDE) {
            npcChat = NpcChatTable.getInstance().getTemplateHide(npcId);
        } else if (chatTiming == CHAT_TIMING_GAME_TIME) {
            npcChat = NpcChatTable.getInstance().getTemplateGameTime(npcId);
        }
        if (npcChat == null) {
            return;
        }

        Timer timer = new Timer(true);
        L1NpcChatTimer npcChatTimer = new L1NpcChatTimer(this, npcChat);
        if (!npcChat.isRepeat()) {
            timer.schedule(npcChatTimer, npcChat.getStartDelayTime());
        } else {
            timer.scheduleAtFixedRate(npcChatTimer, npcChat.getStartDelayTime(), npcChat.getRepeatInterval());
        }
    }

    Random rnd = new Random();
}
