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
import nro.player.Detu;
import nro.player.Player;
import nro.skill.Skill;

public class TaskTauPayPay extends Boss {

    private final Player taskOwner;

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

        this.taskOwner = pl;
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
    }

    @Override
    public void reward(Player plKill) {
        Player rewardPlayer = getTaskOwner(plKill);
        if (rewardPlayer == this.taskOwner && getTaskId() == ConstTask.TASK_10_1) {
            TaskService.gI().checkDoneTaskKillBoss(rewardPlayer, this);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie() || isMobAttack || !isTaskActor(plAtt)) {
            return 0;
        }

        if (!piercing && Util.isTrue(400, 1000)) {
            this.chat("Xí hụt");
            return 0;
        }

        int taskId = getTaskId();
        if (taskId == ConstTask.TASK_9_0
                || taskId == ConstTask.TASK_9_1
                || taskId == ConstTask.TASK_9_2) {
            return 1;
        }

        if (taskId != ConstTask.TASK_10_1) {
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

    @Override
    public void update() {
        if (!isOwnerInZone()) {
            leaveMap();
            return;
        }

        super.update();

        if (this.zone != null && !isOwnerInZone()) {
            leaveMap();
        }
    }

    /**
     * This task boss is scoped to its task owner instead of the generic boss
     * no-hunter timeout. Its dialogue alone can last longer than five seconds.
     */
    @Override
    protected void checkAutoResetBySecondsRest() {
    }

    private boolean isOwnerInZone() {
        return this.taskOwner != null && this.zone != null && this.taskOwner.zone == this.zone;
    }

    private boolean isTaskActor(Player player) {
        if (!isOwnerInZone() || player == null) {
            return false;
        }
        return player == this.taskOwner
                || (player instanceof Detu && ((Detu) player).master == this.taskOwner);
    }

    private Player getTaskOwner(Player player) {
        if (player instanceof Detu) {
            return ((Detu) player).master;
        }
        return player;
    }

    private int getTaskId() {
        if (this.taskOwner == null || this.taskOwner.playerTask == null
                || this.taskOwner.playerTask.taskMain == null) {
            return -1;
        }
        return TaskService.gI().getIdTask(this.taskOwner);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        // The task boss keeps the player's zone reference.  The player may
        // leave/disconnect before the boss loop reaches JOIN_MAP, in which
        // case the reference is cleared asynchronously.  Do not call the
        // spaceship mapper with a detached boss; remove this orphan instead.
        if (this.zone == null || this.zone.map == null || this.location == null) {
            BossManager.gI().removeBoss(this);
            this.dispose();
            return;
        }
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
