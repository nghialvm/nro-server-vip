package nro.boss.AnwRedRibbonHQ;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.PHOBANDT;
import QuanLiBoss.Manager.RedRibbonHQManager;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import nro.skill.Skill;
import Utils.Util;
import consts.ConstPlayer;
import nro.map.ItemMap;
import nro.map.Zone;

public class NinjaClone extends Boss {

    private Boss boss;

    public NinjaClone(Zone zone, Boss boss, long dame, long hp, int id) throws Exception {
        super(PHOBANDT, id, new BossData(
                "Ninja Áo Tím",
                ConstPlayer.TRAI_DAT,
                new short[]{123, 124, 125, -1, -1, -1},
                dame,
                new long[]{hp},
                new int[]{54},
                new int[][]{
                    {Skill.DRAGON, 1, 1000},
                    {Skill.KAMEJOKO, 7, 2000}
                },
                new String[]{},
                new String[]{
                    "|-1|Ta sẽ xé xác ngươi ra thành trăm mảnh",
                    "|-1|Ha ha ha"
                },
                new String[]{},
                60
        ));

        this.zone = zone;
        this.boss = boss;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill != null && Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    Util.nextInt(16, 20),
                    1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void joinMap() {
        if (this.boss != null && this.boss.location != null) {
            ChangeMapService.gI().changeMap(
                    this,
                    this.zone,
                    this.boss.location.x + Util.nextInt(-200, 200),
                    this.boss.location.y
            );
        } else {
            ChangeMapService.gI().changeMap(this, this.zone, 190, 312);
        }

        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(20, 100)) {
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
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
        }

        this.changeStatus(BossStatus.DIE);
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.boss = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}