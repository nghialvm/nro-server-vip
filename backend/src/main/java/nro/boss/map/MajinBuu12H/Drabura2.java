package nro.boss.map.MajinBuu12H;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.FINAL;
import QuanLiBoss.BossesData;
import QuanLiBoss.TypeAppear;
import nro.server.Manager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Util;
import java.util.Random;
import nro.map.ItemMap;
import nro.skill.Skill;

public class Drabura2 extends Boss {

    private boolean callBoss = true;

    private long lastTimeJoin;

    public Drabura2() throws Exception {
        super(FINAL, BossID.DRABURA_2, BossesData.DRABURA_2);
    }

    @Override
    public void joinMap() {
        if (zoneFinal != null) {
            this.zone = zoneFinal;
        }
        this.lastTimeJoin = System.currentTimeMillis();
        this.callBoss = false;
        ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(300, 400), 336);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void reward(Player plKill) {
        plKill.fightMabu.changePoint((byte) 10);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
        byte random = (byte) new Random().nextInt(Manager.itemDC12.length -1);
        if (Util.isTrue(30, 100)) {
            Service.gI().dropItemMap(this.zone,new ItemMap (Util.RaitiDoc12(zone, Manager.itemDC12[random], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id)));
        }
        if (Util.isTrue(100, 100)) {
        ItemMap mayluyentap = new ItemMap(this.zone, 521, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap mayluyentap = new ItemMap(this.zone, 521, 1, this.location.x + Util.nextInt(30, 60), this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap mayluyentap = new ItemMap(this.zone, 521, 1, this.location.x - Util.nextInt(30, 60), this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x - 20, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x - 40, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x + 10, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x + 20, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
        if (Util.isTrue(100, 100)) {
        ItemMap vang = new ItemMap(this.zone, 190, 30000, this.location.x + 40, this.zone.map.yPhysicInTop(this.location.x, this.location.y), plKill.id);
            Service.getInstance().dropItemMap(this.zone, vang);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(200, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (plAtt!= null && !(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT 
                    || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO 
                    || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                if (damage >= this.nPoint.hpMax / 10) {
                    damage = this.nPoint.hpMax / 10;
                }
            }
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.LIEN_HOAN:
                        return 0;
                }
            }

            if (plAtt.isPl() && Util.isTrue(1, 5)) {
                plAtt.fightMabu.changePercentPoint((byte) 1);
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            
            if (damage >= this.nPoint.hp) {
                this.changeStatus(BossStatus.AFK);
                damage = 0;
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
    public void rest() {
        int nextLevel = this.currentLevel + 1;
        if (nextLevel >= this.data.length) {
            nextLevel = 0;
        }
        if (this.data[nextLevel].getTypeAppear() == TypeAppear.DEFAULT_APPEAR
                && Util.canDoWithTime(lastTimeRest, secondsRest * 1000)) {
            this.changeStatus(BossStatus.RESPAWN);
        }

        if (Util.canDoWithTime(lastTimeRest, 5000)) {
            if (!this.callBoss) {
                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    boss.changeStatus(BossStatus.RESPAWN);
                }
                this.callBoss = true;
            }
        }

    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(this.lastTimeJoin, 250000)) {
            this.leaveMap();
        }
    }

    @Override
    public void afk() {
        this.changeToTypeNonPK();
        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }

}






