package l1j.server.server.model.Instance;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1World;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;

import java.util.Random;
import java.util.logging.Logger;

public class L1RguardInstance extends L1NpcInstance {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger _log = Logger.getLogger(L1RguardInstance.class.getName());
    private int _randomMoveDistance = 0;

    private int _randomMoveDirection = 0;
    private static Random _random = new Random();

    // 타겟을 찾는다
    @Override
    public void searchTarget() {
        // 타겟 수색
        L1PcInstance targetPlayer = null;
        boolean isNowWar = false; // by 쿠우

        for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
            int castleId = L1CastleLocation.getCastleIdByArea(pc);
            if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm() || pc.isGhost() || isNowWar) { // by 쿠우
                continue;
            }
            if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) // 인비지체크
            {
                if (pc.isWanted()) { // PK로 준비중인가
                    targetPlayer = pc;
                    break;
                }
            }
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
    public boolean noTarget() {
        if (getLocation().getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
            int dir = moveDirection(getHomeX(), getHomeY());
            if (dir != -1) {
                setDirectionMove(dir);
                setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
            } else // 너무 먼 or경로가 발견되지 않는 경우는 텔레포트 해 돌아간다
            {
                teleport(getHomeX(), getHomeY(), 1);
            }
        } else {
            if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
                return true; // 주위에 플레이어가 없어지면(자) AI처리 종료
            }
            // 그룹에 속하지 않은 or그룹에 속하고 있어 리더의 경우, 랜덤에 움직여 둔다

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


        }
        return false;
    }

    public L1RguardInstance(L1Npc template) {
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
                setCurrentHpDirect(0);
                setDead(true);
                setStatus(ActionCodes.ACTION_Die);
                Death death = new Death(attacker);
                GeneralThreadPool.getInstance().execute(death);
            }
            if (newHp > 0) {
                setCurrentHp(newHp);
            }
        } else if (!isDead()) { // 만약을 위해
            setDead(true);
            setStatus(ActionCodes.ACTION_Die);
            Death death = new Death(attacker);
            GeneralThreadPool.getInstance().execute(death);
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

            getMap().setPassable(getLocation(), true);

            broadcastPacket(new S_DoActionGFX(getId(), ActionCodes.ACTION_Die));

            startChat(CHAT_TIMING_DEAD);

            setDeathProcessing(false);

            allTargetClear();

            startDeleteTimer();
        }
    }

    private boolean checkHasCastle(L1PcInstance pc, int castleId) {
        boolean isExistDefenseClan = false;
        for (L1Clan clan : L1World.getInstance().getAllClans()) {
            if (castleId == clan.getCastleId()) {
                isExistDefenseClan = true;
                break;
            }
        }
        if (!isExistDefenseClan) { // 성주 크란이 없다
            return true;
        }

        if (pc.getClanid() != 0) { // 크란 소속중
            L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
            if (clan != null) {
                if (clan.getCastleId() == castleId) {
                    return true;
                }
            }
        }
        return false;
    }

}