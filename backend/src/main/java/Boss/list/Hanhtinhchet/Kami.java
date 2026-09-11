package Boss.list.Hanhtinhchet;

import nro.effect.EffectSkillService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import nro.player.Player;
import consts.ConstPlayer;
import QuanLiBoss.BossData;
import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.SmallBoss;
import nro.map.Zone;
import nro.services.Fun.ChangeMapService;
import Utils.Util;


public class Kami extends SmallBoss {

    protected boolean isReady;
    private short x;
    private short y;

    public Kami() throws Exception {
        super(BossID.kami, BossesData.kami);
    }

    public Kami(Boss bigBoss, Zone zone, short x, short y, BossData data) throws Exception {
        super(BossID.kami, bigBoss, data);
        this.isReady = false;
        this.zone = zone;
        this.x = x;
        this.y = y;
    }

    @Override
    public void joinMap() {
        if (this.bigBoss == null) {
            super.joinMap();
        } else {
            ChangeMapService.gI().changeMap(this, this.zone, x + Util.getOne(-1, 1) * 50, y);
        }
    }

    @Override
    public void reward(Player plKill) {

    }

    @Override
    public void doneChatE() {
        if (this.parentBoss == null || this.parentBoss.bossAppearTogether == null
                || this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel] == null) {
            this.changeToTypePK();
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }

            if (damage >= this.nPoint.hp && this.bigBoss != null && !this.isReady) {
                this.changeToTypeNonPK();
                this.isReady = true;
                this.nPoint.hp = 1;
                this.effectSkill.removeSkillEffectWhenDie();

                if (((Oren) this.bigBoss).isReady) {
                    ((Oren) this.bigBoss).lastTimeFusion = System.currentTimeMillis();
                    ((Oren) this.bigBoss).lastTimecanAttack = System.currentTimeMillis();
                }
                return 0;
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }

            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void active() {
        if (this.bigBoss == null) {
            if (this.typePk == ConstPlayer.NON_PK) {
                return;
            }
            this.attack();
        } else {
            if (this.bigBoss != null && this.bigBoss.typePk == ConstPlayer.PK_ALL && !this.isReady) {
                this.changeToTypePK();
            }
            this.attack();
        }
    }

    @Override
    public void wakeupAnotherBossWhenDisappear() {
        Boss boss = this.parentBoss;
        if (boss != null && !boss.isDie()) {
            boss.changeToTypePK();
        }
    }

    @Override
    public void leaveMap() {
        if (this.bigBoss == null) {
            super.leaveMap();
        } else {
            synchronized (this) {
                BossManager.gI().removeBoss(this);
            }
            this.dispose();
        }
    }
}
