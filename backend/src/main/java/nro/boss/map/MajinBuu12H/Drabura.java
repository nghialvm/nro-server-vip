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
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.SkillUtil;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nro.map.ItemMap;
import nro.skill.Skill;
import nro.skill.SkillService;

public class Drabura extends Boss {

    private long lastTimePetrify;

    private long lastTimeMove;

    private int timeMove;

    private long lastTimeAfk;

    private long lastTimeChatAfk;

    private int timeChat;

    public Drabura() throws Exception {
        super(FINAL, BossID.DRABURA, BossesData.DRABURA);
    }

    @Override
    public void joinMap() {
        try {
            if (zoneFinal != null) {
                this.zone = zoneFinal;
            }

            if (this.zone == null) {
                return;
            }

            ChangeMapService.gI().changeMap(this, this.zone, -1, -1);
            this.changeStatus(BossStatus.ACTIVE);
            Service.gI().changeFlag(this, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void active() {
        if (this.zone == null) {
            return;
        }

        this.attack();
    }

    @Override
    public Player getPlayerAttack() {
        try {
            if (this.zone == null) {
                return null;
            }

            List<Player> players = this.zone.getNotBosses();
            if (players == null || players.isEmpty()) {
                return null;
            }

            List<Player> plNotVoHinh = new ArrayList<>();

            for (Player pl : players) {
                if (pl == null) {
                    continue;
                }

                if (pl.zone == null || pl.isDie()) {
                    continue;
                }

                if ((pl.effectSkin == null || !pl.effectSkin.isVoHinh) && pl.cFlag != this.cFlag) {
                    plNotVoHinh.add(pl);
                }
            }

            if (!plNotVoHinh.isEmpty()) {
                return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void petrifyPlayersInTheMap() {
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

                if (Util.isTrue(1, 10)) {
                    this.chat("phẹt");
                    EffectSkillService.gI().setIsStone(pl, 22000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void attack() {
        if (this.zone == null) {
            return;
        }

        if (Util.canDoWithTime(this.lastTimeAttack, 100)) {
            if (Util.canDoWithTime(lastTimePetrify, 10000)) {
                petrifyPlayersInTheMap();
                this.lastTimePetrify = System.currentTimeMillis();
            }

            this.lastTimeAttack = System.currentTimeMillis();

            try {
                if (this.zone == null) {
                    return;
                }

                Player pl = getPlayerAttack();

                if (pl == null || pl.zone == null || pl.isDie()) {
                    if (Util.canDoWithTime(lastTimeMove, timeMove)) {
                        Player plRand = super.getPlayerAttack();

                        if (plRand != null && plRand.zone != null && !plRand.isDie()) {
                            this.moveToPlayer(plRand);
                            this.lastTimeMove = System.currentTimeMillis();
                            this.timeMove = Util.nextInt(1000, 5000);
                        }
                    }
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
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    pl.location.y
                            );
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    pl.location.y
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

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (plAtt == null) {
            return 0;
        }

        if (!this.isDie()) {
            if (!piercing && Util.isTrue(10, 100)) {
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
                    case Skill.TU_SAT:
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
                this.lastTimeAfk = System.currentTimeMillis();
                die(plAtt);
            }

            return damage;
        }

        return 0;
    }
}