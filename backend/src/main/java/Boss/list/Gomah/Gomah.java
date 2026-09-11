package Boss.list.Gomah;

import nro.effect.EffectSkillService;
import QuanLiBoss.BossesData;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import Utils.Logger;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstPlayer;
import consts.ConstTaskBadges;
import java.util.ArrayList;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.server.Manager;
import nro.server.ServerNotify;
import nro.services.TaskService;
import nro.skill.SkillService;

public class Gomah extends Boss {

    private long st;
    private int timeLeaveMap;
    private long spawnTime;
    private static final int RESET_AFTER = 30 * 60 * 1000;

    // Thông báo boss còn sống mỗi 10 phút
    private long lastTimeNotifyAlive;
    private static final long TIME_NOTIFY_ALIVE = 10 * 60 * 1000;

    public Gomah() throws Exception {
        super(BossID.GOMAH, true, true, false, false, BossesData.GOMAH, BossesData.GOMAH1);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        // ================== 2. Đồ Thần Linh ==================
        if (Util.isTrue(3, 10)) {
            short[] thanLinh = {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
            short itemId = thanLinh[Util.nextInt(0, thanLinh.length - 1)];
            dropCustomItem(itemId, x, y, plKill);
        }

        // ================== 3. Ngọc Rồng ==================
        if (Util.isTrue(1, 5)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 15, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        if (Util.isTrue(1, 10)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 14, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        // Thỏi vàng
        if (Util.isTrue(2, 5)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 457, 10,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        // ================== 4. Vàng ==================
        ItemMap vang = new ItemMap(this.zone, (short) 190,
                Util.nextInt(28000, 30000), x + Util.nextInt(-15, 15), y, plKill.id);
        Service.gI().dropItemMap(zone, vang);

        // ================== 5. Nhẫn nhiệm vụ 31 ==================
        try {
            if (plKill.playerTask != null
                    && plKill.playerTask.taskMain != null
                    && plKill.playerTask.taskMain.id == 31) {

                if (Util.isTrue(1, 3)) {
                    ItemMap missionRing = new ItemMap(this.zone, (short) 992, 1,
                            x + Util.nextInt(-10, 10), y, plKill.id);
                    Service.gI().dropItemMap(zone, missionRing);
                }
            }
        } catch (Exception e) {
            Logger.logException(Gomah.class, e, "Lỗi drop nhẫn nhiệm vụ 31");
        }

        // ================== 6. Túi siêu ấn ==================
        if (Util.isTrue(2, 5)) {
            ItemMap tuiSieuAn = new ItemMap(this.zone, (short) 1985, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, tuiSieuAn);
        }

        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    private void dropCustomItem(short itemId, int x, int y, Player plKill) {
        ItemMap itemMap = new ItemMap(this.zone, itemId, 1,
                x + Util.nextInt(-15, 15), y, plKill.id);

        List<ItemOption> baseOps = (itemId >= 555 && itemId <= 567)
                ? ItemService.gI().getDefaultOptionTL(itemId)
                : ItemService.gI().getListOptionItemShop(itemId);

        List<ItemOption> ops = new ArrayList<>(baseOps.size());
        int mul = Util.nextInt(100, 116); // 100%..115%

        for (ItemOption src : baseOps) {
            int id = src.optionTemplate.id;
            int param = src.param;

            if (!(itemId >= 555 && itemId <= 567) && !isNonScaleOption(id)) {
                param = (int) Math.round(param * (mul / 100.0));
                if (id == 14) {
                    param = Math.min(param, 25);
                }
            }

            ops.add(new ItemOption(id, param));
        }

        itemMap.options.addAll(ops);

        boolean hasCrystal = ops.stream().anyMatch(o -> o.optionTemplate.id == 107);
        if (itemId != 561 && !hasCrystal) {
            itemMap.options.add(new ItemOption(107, getRandomStar()));
        }

        Service.gI().dropItemMap(zone, itemMap);
    }

    private boolean isNonScaleOption(int id) {
        return id == 30 || id == 73 || id == 93 || id == 199 || id == 246;
    }

    private int getRandomStar() {
        int r = Util.nextInt(100);
        if (r < 50) {
            return Util.nextInt(1, 4); // 1–3 sao
        }
        if (r < 80) {
            return 4;
        }
        return 5;
    }

    private void notifyAliveBoss() {
        if (this.zone != null && !this.isDie() && Util.canDoWithTime(lastTimeNotifyAlive, TIME_NOTIFY_ALIVE)) {
            ServerNotify.gI().notify("Boss " + this.name + " vẫn đang xuất hiện tại " + this.zone.map.mapName);
            lastTimeNotifyAlive = System.currentTimeMillis();
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            if (this.currentLevel != 0) {
                damage /= 2;
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
    public void autoLeaveMap() {
        notifyAliveBoss();

        if (this.zone != null && !this.isDie() && Util.canDoWithTime(spawnTime, RESET_AFTER)) {
            this.chat("Ta sẽ quay lại mạnh hơn!");
            this.leaveMapNew();
            return;
        }

        if (Util.canDoWithTime(st, timeLeaveMap)) {
            if (Util.isTrue(1, 2)) {
                this.leaveMap();
            } else {
                this.leaveMapNew();
            }
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
            timeLeaveMap = Util.nextInt(1800000, 3600000);
        }
    }

    @Override
    public void joinMap() {
        this.name = this.data[this.currentLevel].getName() + " " + Util.nextInt(1, 100);
        super.joinMap();

        st = System.currentTimeMillis();
        timeLeaveMap = Util.nextInt(1800000, 3600000);
        spawnTime = System.currentTimeMillis();
        lastTimeNotifyAlive = System.currentTimeMillis();

        if (this.zone != null) {
            ServerNotify.gI().notify("Boss " + this.name + " đã xuất hiện tại " + this.zone.map.mapName);
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

                int dis = Util.getDistance(this, pl);
                if (dis > 450) {
                    move(pl.location.x - 24, pl.location.y);
                } else if (dis > 100) {
                    int dir = (this.location.x - pl.location.x < 0 ? 1 : -1);
                    int move = Util.nextInt(50, 100);
                    move(this.location.x + (dir == 1 ? move : -move), pl.location.y);
                } else {
                    if (Util.isTrue(30, 100)) {
                        int move = Util.nextInt(50);
                        move(pl.location.x + (Util.nextInt(0, 1) == 1 ? move : -move), this.location.y);
                    }
                    SkillService.gI().useSkill(this, pl, null, -1, null);
                    checkPlayerDie(pl);
                }
            } catch (Exception ex) {
                Logger.logException(Boss.class, ex);
            }
        }
    }
}