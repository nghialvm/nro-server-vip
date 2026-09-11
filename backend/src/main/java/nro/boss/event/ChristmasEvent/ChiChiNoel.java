package nro.boss.event.ChristmasEvent;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossType;
import QuanLiBoss.BossesData;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstTaskBadges;
import event.EventManager;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.skill.SkillService;

public class ChiChiNoel extends Boss {

    private long st;

    public ChiChiNoel() throws Exception {
        super(BossType.CHRISTMAS_EVENT, BossID.CHICHI_NOEL, false, true, false, false, BossesData.CHICHI_NOEL);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);

        if (EventManager.CHRISTMAS) {
            for (int i = 0; i < Util.nextInt(10, 50); i++) {
                ItemMap it = new ItemMap(
                        this.zone,
                        1462,
                        1,
                        this.location.x + Util.nextInt(-100, 100),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        -1
                );
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }

            for (int i = 0; i < Util.nextInt(3, 10); i++) {
                ItemMap it = new ItemMap(
                        this.zone,
                        925,
                        1,
                        this.location.x + Util.nextInt(-100, 100),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        -1
                );
                it.addOptionParam(87, 0);
                it.addOptionParam(30, 0);
                it.addOptionParam(93, 35);
                Service.gI().dropItemMap(this.zone, it);
            }

            for (int i = 0; i < Util.nextInt(5, 20); i++) {
                ItemMap it = new ItemMap(
                        this.zone,
                        Util.nextInt(1446, 1447),
                        1,
                        this.location.x + Util.nextInt(-100, 100),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        -1
                );
                it.addOptionParam(30, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }

            if (Util.isTrue(100, 100)) {
                ItemMap it = new ItemMap(
                        this.zone,
                        1436,
                        1,
                        this.location.x,
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id
                );
                it.addOptionParam(50, Util.nextInt(25, 30));
                it.addOptionParam(77, Util.nextInt(25, 30));
                it.addOptionParam(103, Util.nextInt(25, 30));
                it.addOptionParam(14, Util.nextInt(10, 15));
                it.addOptionParam(94, Util.nextInt(10, 15));
                it.addOptionParam(106, 0);
                it.addOptionParam(204, Util.nextInt(5, 10));

                if (Util.isTrue(80, 100)) {
                    it.addOptionParam(93, Util.nextInt(3, 7));
                }

                Service.gI().dropItemMap(this.zone, it);
            }
        }
    }

    @Override
    public void chatM() {
        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }

        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }

        String textChat = this.data[this.currentLevel].getTextM()[Util.nextInt(0, this.data[this.currentLevel].getTextM().length - 1)];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);

        this.chat(prefix, textChat);
        this.lastTimeChatM = System.currentTimeMillis();
        this.timeChatM = Util.nextInt(3000, 10000);
    }

    @Override
    public void joinMap() {
        this.name = "Chi Chi Tuần Lộc " + Util.nextInt(1, 100);
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        if (Util.canDoWithTime(st, 1800000)) {
            this.leaveMapNew();
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
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
        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = getPlayerAttack();

                if (pl == null || pl.location == null) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70)
                            );
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50)
                            );
                        }
                    }

                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}