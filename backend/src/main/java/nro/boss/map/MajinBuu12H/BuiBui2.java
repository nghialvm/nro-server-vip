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
import java.util.List;
import java.util.Random;
import nro.effect.EffectSkinService;
import nro.map.ItemMap;
import nro.skill.Skill;
import nro.skill.SkillService;

public class BuiBui2 extends Boss {

    private int indexChat;
    private long lastTimeSlow;

    private long lastTimeAfk;

    private long lastTimeChatAfk;

    private int timeChat;

    public BuiBui2() throws Exception {
        super(FINAL, BossID.BUI_BUI_2, BossesData.BUI_BUI_2);
    }

    @Override
    public void reward(Player plKill) {
        if (this.zone == null || plKill == null) {
            return;
        }

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

    private void slowPlayerInMap() {
        try {
            if (this.zone == null) {
                return;
            }

            List<Player> players = this.zone.getNotBosses();
            if (players == null || players.isEmpty()) {
                return;
            }

            for (Player pl : players) {
                if (pl == null || pl.zone == null || pl.isDie()) {
                    continue;
                }

                if (Util.isTrue(5, 10)) {
                    EffectSkinService.gI().setSlow(pl, System.currentTimeMillis(), 5000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attack() {
        if (this.zone == null) {
            return;
        }

        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            if (Util.canDoWithTime(lastTimeSlow, 10000)) {
                slowPlayerInMap();
                this.lastTimeSlow = System.currentTimeMillis();
            }

            this.lastTimeAttack = System.currentTimeMillis();

            try {
                if (this.zone == null) {
                    return;
                }

                Player pl = getPlayerAttack();

                if (pl == null || pl.zone == null || pl.isDie()) {
                    return;
                }

                if (this.playerSkill == null || this.playerSkill.skills == null || this.playerSkill.skills.isEmpty()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(
                        Util.nextInt(0, this.playerSkill.skills.size() - 1)
                );

                if (this.playerSkill.skillSelect == null) {
                    return;
                }

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

    @Override
    public void chatM() {
        if (this.typePk == ConstPlayer.NON_PK) {
            return;
        }

        if (this.data[this.currentLevel].getTextM().length == 0) {
            return;
        }

        if (!Util.canDoWithTime(this.lastTimeChatM, this.timeChatM)) {
            return;
        }

        String textChat = this.data[this.currentLevel].getTextM()[indexChat];
        int prefix = Integer.parseInt(textChat.substring(1, textChat.lastIndexOf("|")));
        textChat = textChat.substring(textChat.lastIndexOf("|") + 1);

        this.chat(prefix, textChat);
        this.indexChat++;

        if (indexChat == this.data[this.currentLevel].getTextM().length) {
            this.indexChat = 0;
            this.lastTimeChatM = System.currentTimeMillis();
            this.timeChatM = 10000;
        } else {
            this.lastTimeChatM = System.currentTimeMillis();
            this.timeChatM = 3000;
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
    public void die(Player plKill) {
        if (plKill != null) {
            reward(plKill);
            ServerNotify.gI().notify(plKill.name + ": Đã tiêu diệt được " + this.name + " mọi người đều ngưỡng mộ.");
        }

        this.lastTimeAfk = System.currentTimeMillis();
        this.changeStatus(BossStatus.AFK);
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (plAtt == null) {
            return 0;
        }

        if (!this.isDie()) {
            if (!piercing && Util.isTrue(200, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (plAtt.playerSkill != null
                    && plAtt.playerSkill.skillSelect != null
                    && plAtt.playerSkill.skillSelect.template != null
                    && !(plAtt.playerSkill.skillSelect.template.id == Skill.TU_SAT
                    || plAtt.playerSkill.skillSelect.template.id == Skill.MAKANKOSAPPO
                    || plAtt.playerSkill.skillSelect.template.id == Skill.QUA_CAU_KENH_KHI)) {
                if (damage >= this.nPoint.hpMax / 10) {
                    damage = this.nPoint.hpMax / 10;
                }
            }

            if (plAtt.playerSkill != null
                    && plAtt.playerSkill.skillSelect != null
                    && plAtt.playerSkill.skillSelect.template != null) {
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
}