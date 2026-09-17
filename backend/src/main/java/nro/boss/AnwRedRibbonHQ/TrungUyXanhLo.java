package nro.boss.AnwRedRibbonHQ;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
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

public class TrungUyXanhLo extends Boss {

    public TrungUyXanhLo(Zone zone, long dame, long hp) throws Exception {
        super(PHOBANDT, BossID.TRUNG_UY_XANH_LO, new BossData(
                "Trung úy Xanh Lơ",
                ConstPlayer.TRAI_DAT,
                new short[]{135, 136, 137, -1, -1, -1},
                dame,
                new long[]{hp},
                new int[]{62},
                new int[][]{
                    {Skill.DRAGON, 1, 1000},
                    {Skill.KAMEJOKO, Util.nextInt(4, 6), 2000},
                    {Skill.THAI_DUONG_HA_SAN, Util.nextInt(4, 7), Util.nextInt(25000, 35000)}
                },
                new String[]{},
                new String[]{
                    "|-1|Xem các ngươi mạnh đến đâu",
                    "|-1|He he he"
                },
                new String[]{},
                60
        ));

        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill == null) {
            return;
        }

        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    Util.nextInt(14, 16),
                    10,
                    this.location.x + Util.nextInt(-15, 15),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }

        if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    611,
                    10,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        } else if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    1612,
                    1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        } else if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    1621,
                    1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }

        if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    1641,
                    1,
                    this.location.x + 30,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        } else if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    1642,
                    1,
                    this.location.x + 30,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        } else if (Util.isTrue(70, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    1643,
                    1,
                    this.location.x + 30,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void active() {
        super.active();
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
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 1210, 384);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        this.changeStatus(BossStatus.AFK);
        Service.gI().setPos(this, 1210, 384);
    }

    @Override
    public void afk() {
        Player pl = getPlayerAttack();

        if (pl == null || pl.isDie()) {
            return;
        }

        if (Util.getDistance(this, pl) <= 500) {
            this.changeStatus(BossStatus.ACTIVE);
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
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
        RedRibbonHQManager.gI().removeBoss(this);
        this.dispose();
    }
}