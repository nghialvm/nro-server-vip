package nro.boss.task.PresentCell;

import Boss.nro.boss.task.BlackGoku.BlackGoku;
import nro.effect.EffectSkillService;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossesData;
import Utils.Logger;
import nro.services.Fun.ChangeMapService;
import nro.services.PlayerService;
import nro.player.Player;
import nro.services.Service;
import nro.services.TaskService;
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

public class XenBoHung extends Boss {

    private long lastTimeHapThu;
    private int timeHapThu;

    public XenBoHung() throws Exception {
        super(BossID.XEN_BO_HUNG, BossesData.XEN_BO_HUNG_1, BossesData.XEN_BO_HUNG_2, BossesData.XEN_BO_HUNG_3);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        // ================== 1. Đồ thường Long đỏ -> Lưỡng Long, index 2-5 + rada SKH VIP ==================
        if (Util.isTrue(1, 10)) {
            // Random 50/50: rơi từ doSKHVip hoặc từ radaSKHVip
            if (Util.isTrue(1, 2)) {
                // Rơi từ doSKHVip
                short[][][] doSKHVip = Manager.doSKHVip;
                short[][] planetItems = doSKHVip[Util.nextInt(0, doSKHVip.length - 1)];
                short[] part = planetItems[Util.nextInt(0, planetItems.length - 1)];

                if (part.length > 5) {
                    int idx = Util.nextInt(2, 5); // Chỉ lấy đồ Long đỏ -> Lưỡng Long
                    short itemId = part[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            } else {
                // Rơi từ rada SKH VIP
                short[] rada = Manager.radaSKHVip;
                int idx = Util.nextInt(2, 5); // Theo quy tắc: index 2-5 là Long đỏ -> Lưỡng Long

                if (idx < rada.length) {
                    short itemId = rada[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            }
        }

        // ================== 2. Đồ Thần Linh ==================
        if (Util.isTrue(1, 100)) {
            short[] thanLinh = {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
            short itemId = thanLinh[Util.nextInt(0, thanLinh.length - 1)];
            dropCustomItem(itemId, x, y, plKill);
        }

        // ================== 3. Ngọc Rồng ==================
        if (Util.isTrue(1, 10)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 15, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        if (Util.isTrue(1, 100)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 14, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        // ================== 4. Vàng luôn rơi ==================
        ItemMap vang = new ItemMap(this.zone, (short) 190,
                Util.nextInt(28000, 30000), x + Util.nextInt(-15, 15), y, plKill.id);
        Service.gI().dropItemMap(zone, vang);

        try {
            if (plKill.playerTask != null
                    && plKill.playerTask.taskMain != null
                    && plKill.playerTask.taskMain.id == 31) {

                // Ví dụ: tỉ lệ 1/3
                if (Util.isTrue(1, 3)) {
                    ItemMap missionRing = new ItemMap(this.zone, (short) 992, 1,
                            x + Util.nextInt(-10, 10), y, plKill.id);
                    Service.gI().dropItemMap(zone, missionRing);
                }
            }
        } catch (Exception e) {
            Logger.logException(BlackGoku.class, e, "Lỗi drop nhẫn nhiệm vụ 31");
        }

        // Check nhiệm vụ giết boss
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    // ------------------ Hỗ trợ ------------------
    /**
     * Drop 1 item với option clone ra để không bị dồn param.
     * Đồ thường được scale 100%-115%, có sao pha lê option 107.
     */
    private void dropCustomItem(short itemId, int x, int y, Player plKill) {
        ItemMap itemMap = new ItemMap(this.zone, itemId, 1,
                x + Util.nextInt(-15, 15), y, plKill.id);

        // 1) Lấy option gốc
        List<ItemOption> baseOps = (itemId >= 555 && itemId <= 567)
                ? ItemService.gI().getDefaultOptionTL(itemId) // Đồ Thần Linh
                : ItemService.gI().getListOptionItemShop(itemId); // Đồ shop

        // 2) Clone và scale
        List<ItemOption> ops = new ArrayList<>(baseOps.size());
        int mul = Util.nextInt(100, 116); // 100%..115%

        for (ItemOption src : baseOps) {
            int id = src.optionTemplate.id;
            int param = src.param;

            // Scale nếu không phải TL và không nằm trong danh sách bỏ qua
            if (!(itemId >= 555 && itemId <= 567) && !isNonScaleOption(id)) {
                param = (int) Math.round(param * (mul / 100.0));

                // Clamp chí mạng
                if (id == 14) {
                    param = Math.min(param, 25);
                }
            }

            ops.add(new ItemOption(id, param)); // Clone mới
        }

        // 3) Thêm option vào itemMap
        itemMap.options.addAll(ops);

        // 4) Sao pha lê option 107, chỉ thêm khi chưa có, không áp dụng cho nhẫn TL 561
        boolean hasCrystal = ops.stream().anyMatch(o -> o.optionTemplate.id == 107);

        if (itemId != 561 && !hasCrystal) {
            itemMap.options.add(new ItemOption(107, getRandomStar()));
        }

        // 5) Drop
        Service.gI().dropItemMap(zone, itemMap);
    }

    private boolean isNonScaleOption(int id) {
        return id == 30 || id == 73 || id == 93 || id == 199 || id == 246;
    }

    private int getRandomStar() {
        int r = Util.nextInt(100);

        if (r < 50) {
            return Util.nextInt(1, 4); // 1-3 sao
        }

        if (r < 80) {
            return 4;
        }

        return 5;
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }

        this.hapThu();
        this.attack();
    }

    private void hapThu() {
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();

        if (pl == null || pl.isDie()) {
            return;
        }

        ChangeMapService.gI().changeMapYardrat(this, this.zone, pl.location.x, pl.location.y);

        this.nPoint.dameg += (pl.nPoint.dame * 5 / 100);
        this.nPoint.hpg += (pl.nPoint.hp * 2 / 100);
        this.nPoint.critg++;
        this.nPoint.calPoint();

        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, 0);
        pl.injured(null, pl.nPoint.hpMax, true, false);

        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " hấp thụ!");
        this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
        this.chat("Haha, ngọt lắm đấy " + pl.name + "..");

        this.lastTimeHapThu = System.currentTimeMillis();
        this.timeHapThu = Util.nextInt(10000, 20000);
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage / 2);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }

                damage = damage / 4;
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
}