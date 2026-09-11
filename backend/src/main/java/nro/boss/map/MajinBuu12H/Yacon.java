package nro.boss.map.MajinBuu12H;



import nro.effect.EffectSkillService;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import static QuanLiBoss.BossType.FINAL;
import QuanLiBoss.BossesData;
import nro.server.Manager;
import nro.server.ServerNotify;
import nro.services.Service;
import nro.services.TaskService;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import java.util.Random;
import nro.map.ItemMap;
import nro.skill.Skill;
import nro.skill.SkillService;

public class Yacon extends Boss {

    private long lastTimeTanHinh;

    private long lastTimeAfk;

    private long lastTimeChatAfk;

    private int timeChat;

    public Yacon() throws Exception {
        super(FINAL, BossID.YA_CON, BossesData.YACON);
    }

    @Override
    public void reward(Player plKill) {
        plKill.fightMabu.changePoint((byte) 10);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        byte random = (byte) new Random().nextInt(Manager.itemDC12.length - 1);

        if (Util.isTrue(30, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(Util.RaitiDoc12(zone, Manager.itemDC12[random], 1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                    plKill.id)));
        }

        if (Util.isTrue(100, 100)) {
            ItemMap mayluyentap = new ItemMap(this.zone, 521, 1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }

        if (Util.isTrue(100, 100)) {
            ItemMap mayluyentap = new ItemMap(this.zone, 521, 1,
                    this.location.x + Util.nextInt(30, 60),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }

        if (Util.isTrue(100, 100)) {
            ItemMap mayluyentap = new ItemMap(this.zone, 521, 1,
                    this.location.x - Util.nextInt(30, 60),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id);
            mayluyentap.addOptionParam(1, Util.nextInt(20, 60));
            Service.getInstance().dropItemMap(this.zone, mayluyentap);
        }

        int[] offsets = {-10, -20, -40, 10, 20, 40};
        for (int offset : offsets) {
            if (Util.isTrue(100, 100)) {
                ItemMap vang = new ItemMap(this.zone, 190, 30000,
                        this.location.x + offset,
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                        plKill.id);
                Service.getInstance().dropItemMap(this.zone, vang);
            }
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

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (Util.canDoWithTime(this.lastTimeTanHinh, 10000) && Util.isTrue(5, 20)) {
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

                if (!Util.canDoWithTime(this.lastTimeTanHinh, 10000)) {
                    this.nPoint.crit = 100;
                    Service.gI().setPos2(this,
                            pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                            10000);
                } else {
                    this.nPoint.crit = 10;
                }

                if (Util.canDoWithTime(this.lastTimeTanHinh, 30000)) {
                    if (Util.isTrue(1, 10)) {
                        String[] chat = {
                            "Mi đâu rồi",
                            "Đồ ăn gian!"
                        };
                        Service.gI().chat(pl, chat[Util.nextInt(chat.length)]);
                        this.lastTimeTanHinh = System.currentTimeMillis();
                    }
                }
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void afk() {
        if (Util.canDoWithTime(lastTimeChatAfk, timeChat)) {
            this.chat("Đừng vội mừng, ta sẽ hồi sinh và thịt hết bọn mi");
            this.lastTimeChatAfk = System.currentTimeMillis();
            this.timeChat = Util.nextInt(10000, 15000);
        }

        if (Util.canDoWithTime(lastTimeAfk, 60000)) {
            Service.gI().hsChar(this, this.nPoint.hpMax, this.nPoint.mpMax);
            this.changeStatus(BossStatus.CHAT_S);
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(200, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (plAtt != null && !(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT
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
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }

        this.lastTimeAfk = System.currentTimeMillis();
        this.changeStatus(BossStatus.AFK);
    }
}