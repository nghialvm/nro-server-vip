package nro.boss.event.Halloween;

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

public class BiNgo extends Boss {

    public BiNgo() throws Exception {
        super(HALLOWEEN_EVENT, BossID.BI_NGO, false, true, false, false, BossesData.BI_NGO);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(50, 100)) {
            ItemMap it = new ItemMap(this.zone, 739, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
            it.addOptionParam(50, 20);
            it.addOptionParam(77, 20);
            it.addOptionParam(103, 20);
            it.addOptionParam(163, 0);
            it.addOptionParam(32, 0);
            it.addOptionParam(93, 30);
            Service.gI().dropItemMap(this.zone, it);
        } else {
            ItemMap it = new ItemMap(this.zone, 1804, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id);
            it.addOptionParam(5, 100);
            it.addOptionParam(159, 5);
            it.addOptionParam(160, 100);
            it.addOptionParam(210, 1);
            it.addOptionParam(93, 3);
            Service.gI().dropItemMap(this.zone, it);
        }
    }

    private void halloween(Player player) {
        if (player.effectSkill != null && !player.effectSkill.isHalloween) {
            EffectSkillService.gI().setIsHalloween(player, 3, 1800000);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(100, 1000)) {
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
                damage = damage * 2;
            } else {
                damage = damage / 2;
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
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    String[] transformPhrases = {
                        "Phùuu... Hô biến thành bí!",
                        "Bí hóa thân! Triệu hồi hình tròn!",
                        "Bí bí bí... NGÔ!",
                        "Xoay tròn và... hóa bí ngô!",
                        "Ngươi giờ là... món chính trong bữa tiệc!",
                        "Lăn đi nào, bí ngô bé nhỏ!",
                        "Từ rễ đến ngọn... BIẾN!",
                        "Giờ ngươi thuộc về... đội Bí!",
                        "Cho ngươi nếm... bí thuật tối thượng!",
                        "Chào mừng đến hội Bí Ngô Bất Tử!"
                    };
                    if (Util.isTrue(10, 100)) {
                        int index = Util.nextInt(0, transformPhrases.length);
                        Service.gI().chat(this, transformPhrases[index]);
                        EffectSkillService.gI().setBiNgo(pl, 5000);
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
        super.joinMap();
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 3_600_000)) {
            this.leaveMapNew();
        }
        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }
}
