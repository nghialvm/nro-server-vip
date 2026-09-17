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
import nro.clan.Clan;
import consts.ConstPlayer;
import nro.map.ItemMap;
import nro.map.Zone;

public class NinjaAoTim extends Boss {

    private Clan clan;
    private boolean calledNinja;

    public NinjaAoTim(Zone zone, Clan clan, long dame, long hp) throws Exception {
        super(PHOBANDT, BossID.NINJA_AO_TIM, new BossData(
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
        this.clan = clan;
    }

    @Override
    public void reward(Player plKill) {
        if (plKill != null && Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    Util.nextInt(14, 16),
                    10,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }

        if (plKill != null && Util.isTrue(70, 100)) {
            ItemMap bdkb = new ItemMap(
                    this.zone,
                    611,
                    10,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, bdkb);
        }
    }

    @Override
    public void joinMap() {
        ChangeMapService.gI().changeMap(this, this.zone, 190, 312);
        this.changeStatus(BossStatus.CHAT_S);
    }

    @Override
    public void doneChatS() {
        Service.gI().setPos(this, 190, 312);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(30, 100)) {
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

            if (this.nPoint.hp <= this.nPoint.hpMax / 2 && !this.calledNinja) {
                if (Util.isTrue(4, 5)) {
                    try {
                        this.chat("Phân thân chi thuật!");

                        if (clan != null && clan.doanhTrai != null) {
                            clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM1));
                            clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM2));
                            clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM3));
                            clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM4));

                            if (Util.isTrue(1, 2)) {
                                clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM5));
                                clan.doanhTrai.bosses.add(new NinjaClone(this.zone, this, this.nPoint.dame / 5L, this.nPoint.hpMax / 5L, BossID.NINJA_AO_TIM6));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                this.calledNinja = true;
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