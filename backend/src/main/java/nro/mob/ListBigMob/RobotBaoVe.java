package nro.mob.ListBigMob;



import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import nro.mob.BigBoss;
import nro.mob.Mob;

public class RobotBaoVe extends BigBoss {

    public RobotBaoVe(Mob mob) {
        super(mob);
    }

    @Override
    public void update() {
        super.update();
        if (this.isDie()) {
            Service.gI().sendBigBoss(this.zone, 6, 0, -1, -1);
        }
    }

    @Override
    public void attack() {
        Player player = getPlayerCanAttack();
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastBigBossAttackTime, 1000)) {
            if (player != null) {
                double dameMob = this.point.getDameAttack();
                double dame = player.injured(null, dameMob, false, true);
                action = 0;
                int dis = Util.getDistance(player, this);
                if (dis <= 100) {
                    action = 0;
                } else if (dis > 100 && dis <= 200) {
                    action = 1;
                } else if (dis > 200) {
                    action = 2;
                }
                this.location.x = (short) player.location.x;
                Service.gI().sendBigBoss(this.zone, action, 1, (int) player.id, Util.CrisGH(dame));
            }
            lastBigBossAttackTime = System.currentTimeMillis();
        }
    }

}
