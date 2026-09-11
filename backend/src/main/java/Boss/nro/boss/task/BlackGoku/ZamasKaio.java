package Boss.nro.boss.task.BlackGoku;

import nro.effect.EffectSkillService;
import QuanLiBoss.BossesData;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import nro.player.Player;
import nro.server.Manager;
import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.map.ItemMap;
import nro.services.TaskService;

public class ZamasKaio extends Boss {

    public ZamasKaio() throws Exception {
        super(BossID.ZAMASZIN, BossesData.ZAMAS);
    }

    @Override
    public void reward(Player plKill) {
        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        // ================== 1. Đồ thường (Long đỏ -> Lưỡng Long, index 2–5 + rada SKHVip) ==================
        if (Util.isTrue(1, 1)) {
            // random 50/50: rơi từ doSKHVip hoặc từ radaSKHVip
            if (Util.isTrue(1, 2)) {
                // ---- Rơi từ doSKHVip ----
                short[][][] doSKHVip = Manager.doSKHVip;
                short[][] planetItems = doSKHVip[Util.nextInt(0, doSKHVip.length - 1)];
                short[] part = planetItems[Util.nextInt(0, planetItems.length - 1)];

                if (part.length > 5) {
                    int idx = Util.nextInt(2, 5); // chỉ lấy đồ Long đỏ → Lưỡng Long
                    short itemId = part[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            } else {
                // ---- Rơi từ rada SKH VIP ----
                short[] rada = Manager.radaSKHVip;
                int idx = Util.nextInt(2, 5); // theo quy tắc: index 2–5 là lông đỏ → lưỡng long
                if (idx < rada.length) {
                    short itemId = rada[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            }
        }

        // ================== 2. Đồ Thần Linh (1/30) ==================
        if (Util.isTrue(1, 3)) {
            short[] thanLinh = {555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
            short itemId = thanLinh[Util.nextInt(0, thanLinh.length - 1)];
            dropCustomItem(itemId, x, y, plKill);
        }

        // ================== 3. Ngọc Rồng 3 sao (1/5) ==================
        if (Util.isTrue(1, 2)) {
            ItemMap nr3 = new ItemMap(this.zone, (short) 16, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr3);
        }

        // ================== 4. Vàng (luôn rơi) ==================
        ItemMap vang = new ItemMap(this.zone, (short) 190,
                Util.nextInt(28000, 30000), x + Util.nextInt(-15, 15), y, plKill.id);
        Service.gI().dropItemMap(zone, vang);

        // Check nhiệm vụ giết boss
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);
    }

    // ------------------ Hỗ trợ ------------------
    /**
     * Drop 1 item với option mặc định + % 100–115 + sao pha lê (opt 107)
     */
    /**
     * Drop 1 item với option mặc định + % 100–115 + sao pha lê (opt 107)
     */
    /**
     * Drop 1 item với option clone ra để không bị dồn param
     */
    private void dropCustomItem(short itemId, int x, int y, Player plKill) {
        ItemMap itemMap = new ItemMap(this.zone, itemId, 1,
                x + Util.nextInt(-15, 15), y, plKill.id);

        // 1) Lấy option gốc
        List<ItemOption> baseOps = (itemId >= 555 && itemId <= 567)
                ? ItemService.gI().getDefaultOptionTL(itemId) // đồ Thần Linh
                : ItemService.gI().getListOptionItemShop(itemId); // đồ shop

        // 2) Clone & scale
        List<ItemOption> ops = new ArrayList<>(baseOps.size());
        int mul = Util.nextInt(100, 116); // 100%..115%

        for (ItemOption src : baseOps) {
            int id = src.optionTemplate.id;
            int param = src.param;

            // scale nếu không phải TL và không nằm trong danh sách exempt
            if (!(itemId >= 555 && itemId <= 567) && !isNonScaleOption(id)) {
                param = (int) Math.round(param * (mul / 100.0));
                // clamp chí mạng
                if (id == 14) {
                    param = Math.min(param, 25);
                }
            }

            ops.add(new ItemOption(id, param)); // clone mới
        }

        // 3) Thêm option vào itemMap
        itemMap.options.addAll(ops);

        // 4) Sao pha lê (opt 107), chỉ thêm khi chưa có, không áp dụng cho nhẫn TL (561)
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
            return Util.nextInt(1, 4); // 1–3 sao
        }
        if (r < 80) {
            return 4;
        }
        return 5;
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
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
    public void leaveMap() {
        super.leaveMap();
    }
}