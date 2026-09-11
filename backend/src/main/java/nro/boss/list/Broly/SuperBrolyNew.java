package nro.boss.list.Broly;

import QuanLiBoss.BossData;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.BROLY;
import QuanLiBoss.BossesData;
import nro.effect.EffectSkillService;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import nro.map.Zone;
import consts.ConstPlayer;
import consts.ConstRatio;
import nro.player.Player;
import QuanLiBoss.Manager.BossManager;
import nro.services.DetuService;
import nro.services.Service;
import nro.services.Fun.ChangeMapService;
import nro.skill.Skill;
import nro.skill.SkillService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.Util;
import nro.map.ItemMap;
import static nro.server.Manager.player;

public class SuperBrolyNew extends Boss {

    private long st;
    private int timeLeaveMap;
    private long spawnTime;
    private static final int RESET_AFTER = 30 * 60 * 1000;

    public SuperBrolyNew() throws Exception {
        super(BossID.SUPER_BROLY_NEW, false, true, false, false, BossesData.SUPPER_BROLY_NEW);
    }

    @Override
    public void reward(Player plKill) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        if (Util.isTrue(30, 100)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 568, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (this.currentLevel != 0) {
                damage /= 2;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

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
    public void autoLeaveMap() {
        if (this.zone != null && !this.isDie() && Util.canDoWithTime(spawnTime, RESET_AFTER)) {
            this.chat("Ta sẽ quay lại mạnh hơn!");
            this.leaveMapNew();
            return;
        }

        if (Util.canDoWithTime(st, timeLeaveMap)) {
            if (Util.isTrue(1, 2)) {
                this.leaveMap();
            } else {
                this.leaveMapNew();
            }
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
            timeLeaveMap = Util.nextInt(1800000, 3600000);
        }
    }

    @Override
    public void joinMap() {
        this.name = this.data[this.currentLevel].getName() + " " + Util.nextInt(1, 100);
        super.joinMap();
        st = System.currentTimeMillis();
        timeLeaveMap = Util.nextInt(1800000, 3600000);
        spawnTime = System.currentTimeMillis();
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                int dis = Util.getDistance(this, pl);

                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                } else {
                    if (Util.isTrue(30, 100)) {
                        int move = Util.nextInt(50);
                        move(pl.location.x + (Util.nextInt(0, 1) == 1 ? move : -move), this.location.y);
                    }

                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                }
            } catch (Exception ex) {
                Logger.logException(Boss.class, ex);
            }
        }
    }
}