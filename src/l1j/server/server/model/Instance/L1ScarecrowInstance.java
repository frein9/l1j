package l1j.server.server.model.Instance;

import l1j.server.server.model.L1Attack;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;

import java.util.ArrayList;
import java.util.logging.Logger;

public class L1ScarecrowInstance extends L1NpcInstance {

    private static final long serialVersionUID = 1L;

    private static Logger _log = Logger.getLogger(L1ScarecrowInstance.class.getName());

    public L1ScarecrowInstance(L1Npc template) {
        super(template);
    }

    @Override
    public void onAction(L1PcInstance player) {
        L1Attack attack = new L1Attack(player, this);
        if (player.getLevel() > 5 || player.isInParty()) {

        } else {
            if (attack.calcHit()) {
                if (player.getLevel() < 5) { // LV제한 버는 경우는 여기를 변경
                    ArrayList<L1PcInstance> targetList = new ArrayList<L1PcInstance>();

                    targetList.add(player);
                    ArrayList<Integer> hateList = new ArrayList<Integer>();
                    hateList.add(1);
                    CalcExp.calcExp(player, getId(), targetList, hateList, getExp());
                }
                if (getHeading() < 7) { // 지금의 방향을 취득
                    setHeading(getHeading() + 1); // 지금의 방향을 설정
                } else {
                    setHeading(0); // 지금의 방향이 7 이상이 되면(자) 지금의 방향을 0에 되돌린다
                }
                broadcastPacket(new S_ChangeHeading(this)); // 방향의 변경
            }
            attack.action();
        }
    }

    @Override
    public void onTalkAction(L1PcInstance l1pcinstance) {

    }

    public void onFinalAction() {

    }

    public void doFinalAction() {
    }
}
