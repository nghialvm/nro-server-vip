package nro.boss.map.TrainingBoss;

import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossStatus;
import QuanLiBoss.Manager.BossManager;
import nro.services.TaskService;
import nro.services.Fun.ChangeMapService;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstTask;
import nro.map.Zone;
import nro.player.Player;
import nro.skill.Skill;

public class TaskTauPayPay extends Boss {

    public TaskTauPayPay(Player pl, int bossID, Zone zone, int dame, int x, int y) throws Exception {
        super(bossID, new BossData(
                "Tàu Pảy Pảy", // name
                ConstPlayer.TRAI_DAT, // gender
                new short[]{92, 93, 94, -1, -1, -1}, // outfit {head, body, leg, bag, aura, eff}
                (TaskService.gI().getIdTask(pl) != ConstTask.TASK_10_1 ? dame / 5 : dame / 10),
                new long[]{(TaskService.gI().getIdTask(pl) != ConstTask.TASK_10_1 ? 10000 : 1100)}, // hp
                new int[]{47}, // map join
                new int[][]{
                    {Skill.DRAGON, 1, 1000},
                    {Skill.KAMEJOKO, Util.nextInt(3, 5), 2000}
                },
                new String[]{
                    "|-1|Ta cho ngươi 10 giây suy nghĩ",
                    "|-1|Mau giao ngọc rồng ra đây",
                    "|-2|Đừng trách ta",
                    "|-1|Xem ta đây"
                }, // text chat 1
                new String[]{}, // text chat 2
                new String[]{
                    "|-2|Tuổi trẻ chưa trải sự đời"
                }, // text chat 3
                5 // second rest
        ));

        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }

    @Override
    public void reward(Player plKill) {
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(400, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (TaskService.gI().getIdTask(plAtt) == ConstTask.TASK_9_0
                    || TaskService.gI().getIdTask(plAtt) == ConstTask.TASK_9_1
                    || TaskService.gI().getIdTask(plAtt) == ConstTask.TASK_9_2) {
                return 1;
            }

            if (TaskService.gI().getIdTask(plAtt) != ConstTask.TASK_10_1) {
                return 100;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);
            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return damage;
        }

        return 0;
    }

    @Override
    public void update() {
        super.update();

        if (this.zone != null && this.zone.getNumOfPlayers() != 1) {
            leaveMap();
        }
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMapBySpaceShip(this, this.zone, 775);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}