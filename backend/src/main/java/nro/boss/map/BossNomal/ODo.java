package nro.boss.map.BossNomal;

import nro.inventory.InventoryService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossData;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossType;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.SkillUtil;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstTaskBadges;
import event.EventManager;
import java.util.List;
import nro.badges.BadgesTaskService;
import nro.effect.EffectSkin;
import nro.map.ItemMap;
import nro.player.Player;
import nro.skill.Skill;
import nro.skill.SkillService;

public class ODo extends Boss {

    private long lastTimeOdo;
    private long lastTimeHpRegen;

    public ODo() throws Exception {
        super(BossType.NOMAL, BossID.O_DO_NOMAL, true, false, false, false, new BossData(
                "Ở Dơ " + Util.nextInt(1, 49),
                ConstPlayer.TRAI_DAT,
                new short[]{400, 401, 402, -1, -1, -1},
                1000,
                new long[]{500000},
                new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 79, 80, 81, 82, 83, 84},
                new int[][]{
                    {Skill.DRAGON, 7, 10000}
                },
                new String[]{}, // text chat 1
                new String[]{}, // text chat 2
                new String[]{},
                600000));
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (damage > 10_000) {
                damage = 10_000;
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

    private void updateOdo() {
        try {
            int param = 10;
            int randomTime = Util.nextInt(3000, 5000);

            if (Util.canDoWithTime(lastTimeOdo, randomTime)) {
                List<Player> playersMap = this.zone.getNotBosses();

                for (int i = playersMap.size() - 1; i >= 0; i--) {
                    Player pl = playersMap.get(i);

                    if (pl != null && pl.nPoint != null && !this.equals(pl) && !pl.isBoss && !pl.isDie() && Util.getDistance(this, pl) <= 200) {
                        long subHp = (pl.nPoint.hpMax * param / 100);

                        if (subHp >= pl.nPoint.hp) {
                            subHp = pl.nPoint.hp - 1;
                        }

                        if (Util.isTrue(60, 100)) {
                            this.chat("Khà khà");
                        } else {
                            this.chat("Bùm bùm");
                        }

                        Service.gI().chat(pl, EffectSkin.textOdo[Util.nextInt(0, EffectSkin.textOdo.length - 1)]);
                        PlayerService.gI().sendInfoHpMpMoney(pl);
                        pl.injured(null, subHp, true, false);
                    }
                }

                this.lastTimeOdo = System.currentTimeMillis(); // Cập nhật thời gian của Odo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regenHp() {
        try {
            if (Util.canDoWithTime(lastTimeHpRegen, 30000)) {
                int regenPercentage = Util.nextInt(10, 20);
                long regenAmount = (this.nPoint.hpMax * regenPercentage / 100);
                PlayerService.gI().hoiPhuc(this, regenAmount, 0);
                this.chat("Mùi của các ngươi thơm quá!! HAHA");
                this.lastTimeHpRegen = System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();

            try {
                Player pl = this.getPlayerAttack();

                if (pl == null || pl.isDie()) {
                    return;
                }

                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));

                if (Util.getDistance(this, pl) <= 40) {
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
                    this.updateOdo();
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        this.regenHp();
    }

    @Override
    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(30, 40);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.O_DO, 1);

        if (Util.isTrue(80, 100)) {
            int rand = Util.nextInt(0, 6);
            short idItem = (short) (rand + 441);
            ItemMap it = new ItemMap(zone, idItem, 1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id);
            it.addOptionParam(95 + rand, (rand == 3 || rand == 4) ? 3 : 5);
            Service.gI().dropItemMap(zone, it);
        } else {
            short idItem = 459;
            ItemMap it = new ItemMap(zone, idItem, 1,
                    this.location.x,
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y),
                    plKill.id);
            it.addOptionParam(112, 80);
            it.addOptionParam(93, 90);
            it.addOptionParam(20, Util.nextInt(10000));
            Service.gI().dropItemMap(zone, it);
        }

        InventoryService.gI().sendItemBag(plKill);

        if (EventManager.LUNNAR_NEW_YEAR) {
            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 751, 1,
                        this.location.x + Util.nextInt(-15, 15),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }

            for (int i = 0; i < Util.nextInt(1, 5); i++) {
                ItemMap it = new ItemMap(this.zone, 1472, 1,
                        this.location.x + Util.nextInt(-15, 15),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                        plKill.id);
                it.addOptionParam(86, 0);
                it.addOptionParam(93, 30);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
    }

    private long st;

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }

        this.attack();

        if (Util.canDoWithTime(st, 1_800_000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        this.joinMap2();
        st = System.currentTimeMillis();
    }

    public void joinMap2() {
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }

        if (this.zone != null) {
            try {
                int zoneid = 0;

                while (zoneid < this.zone.map.zones.size() && this.zone.map.zones.get(zoneid).getNumOfPlayers() < 3) {
                    zoneid++;
                }

                // Check trong khu có boss sẽ chuyển sang khu n + 1
                while (zoneid < this.zone.map.zones.size() && !this.zone.map.zones.get(zoneid).getBosses().isEmpty()) {
                    zoneid++;
                }

                if (zoneid < this.zone.map.zones.size()) {
                    this.zone = this.zone.map.zones.get(zoneid);
                } else {
                    this.leaveMapNew();
                    return;
                }

                ChangeMapService.gI().changeMap(this, this.zone, Util.nextInt(100, 500),
                        this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24));

                this.changeStatus(BossStatus.CHAT_S);
            } catch (Exception e) {
                this.changeStatus(BossStatus.REST);
            }
        } else {
            this.changeStatus(BossStatus.RESPAWN);
        }
    }

    @Override
    public void leaveMap() {
        ChangeMapService.gI().exitMap(this);
        this.lastZone = null;
        this.lastTimeRest = System.currentTimeMillis();
        this.changeStatus(BossStatus.REST);
    }
}