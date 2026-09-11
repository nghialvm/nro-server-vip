package nro.boss.event.Halloween;

import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.HALLOWEEN_EVENT;
import QuanLiBoss.BossesData;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import nro.effect.EffectSkillService;
import nro.map.ItemMap;
import nro.player.Player;
import nro.skill.SkillService;

public class MaTroi extends Boss {

    public MaTroi() throws Exception {
        super(HALLOWEEN_EVENT, BossID.MATROI, false, true, false, false, BossesData.MA_TROI);
    }

    @Override
    public void reward(Player plKill) {
        if (InventoryService.gI().findGioDungNgocBi(plKill)) {
            ItemMap it = new ItemMap(
                    this.zone,
                    703,
                    1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            it.addOptionParam(87, 0);
            it.addOptionParam(30, 0);
            it.addOptionParam(93, 35);
            Service.gI().dropItemMap(this.zone, it);
        }

        for (int i = 0; i < Util.nextInt(1, 3); i++) {
            ItemMap it = new ItemMap(
                    this.zone,
                    585,
                    1,
                    this.location.x + Util.nextInt(-50, 50),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id
            );
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    public void halloween(Player player) {
        if (player.effectSkill != null && !player.effectSkill.isHalloween) {
            EffectSkillService.gI().setIsHalloween(player, 1, 1800000);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
            }

            if (plAtt.nPoint.isCrit) {
                if (damage > 10_000) {
                    damage = 10_000;
                }
            } else {
                if (damage > 5_000) {
                    damage = 5_000;
                }
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
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 200) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    return;
                }

                this.nPoint.dame = pl.nPoint.hpMax / Util.nextInt(30, 50);
                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0
                                            ? pl.location.y
                                            : pl.location.y - Util.nextInt(0, 70)
                            );
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0
                                            ? pl.location.y
                                            : pl.location.y - Util.nextInt(0, 50)
                            );
                        }
                    }

                    halloween(pl);
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void joinMap() {
        this.name = "Ma trơi " + Util.nextInt(1, 150);
        super.joinMap();
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 1_800_000)) {
            this.leaveMapNew();
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }
}