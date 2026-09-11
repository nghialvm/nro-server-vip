package nro.boss.task.FutureCell;

import Boss.nro.boss.task.BlackGoku.BlackGoku;
import nro.effect.EffectSkillService;
import QuanLiBoss.BossID;
import QuanLiBoss.Boss;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossesData;
import Utils.Functions;
import Utils.Logger;
import consts.ConstPlayer;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import nro.services.PlayerService;
import nro.services.TaskService;
import consts.ConstTaskBadges;
import java.util.ArrayList;
import java.util.List;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;
import nro.map.ItemMap;
import nro.server.Manager;

public class SieuBoHung extends Boss {

    private long st;
    public boolean callCellCon;
    private long lastTimeHapThu;
    private int timeHapThu;

    private final String text[] = {
        "Thưa quý vị và các bạn, đây đúng là trận đấu trời long đất lở",
        "Vượt xa mọi dự đoán của chúng tôi",
        "Eo ơi toàn thân lão Xên bốc cháy kìa"
    };

    private long lastTimeChat;
    private long lastTimeMove;
    private int indexChat = 0;

    public SieuBoHung() throws Exception {
        super(BossID.SIEU_BO_HUNG, BossesData.SIEU_BO_HUNG_1, BossesData.SIEU_BO_HUNG_2);
    }

    @Override
    protected void resetBase() {
        super.resetBase();
        this.callCellCon = false;
    }

    public void callCellCon() {
        new Thread(() -> {
            try {
                this.changeStatus(BossStatus.AFK);
                this.changeToTypeNonPK();
                this.recoverHP();
                this.callCellCon = true;

                this.chat("Hãy đấu với 7 đứa con của ta, chúng đều là siêu cao thủ");
                Functions.sleep(2000);

                this.chat("Cứ chưởng tiếp đi haha");
                Functions.sleep(2000);

                this.chat("Liệu mà giữ mạng đấy");
                Functions.sleep(2000);

                for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                    switch ((int) boss.id) {
                        case BossID.XEN_CON_1:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_2:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_3:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_4:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_5:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_6:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                        case BossID.XEN_CON_7:
                            boss.changeStatus(BossStatus.RESPAWN);
                            break;
                    }
                }
            } catch (Exception e) {
            }
        }).start();
    }

    public void recoverHP() {
        PlayerService.gI().hoiPhuc(this, this.nPoint.hpMax, 0);
    }

    @Override
    public void reward(Player plKill) {
        BadgesTaskService.updateCountBagesTask(plKill, ConstTaskBadges.TRUM_SAN_BOSS, 1);
        TaskService.gI().checkDoneTaskKillBoss(plKill, this);

        int x = this.location.x;
        int y = this.zone.map.yPhysicInTop(x, this.location.y - 24);

        // ================== 1. Đồ thường (Long Đỏ -> Lưỡng Long, index 2 - 5 + rada SKH Vip) ==================
        if (Util.isTrue(1, 10)) {
            // Random 50/50: rơi từ doSKHVip hoặc từ radaSKHVip
            if (Util.isTrue(1, 2)) {
                // ---- Rơi từ doSKHVip ----
                short[][][] doSKHVip = Manager.doSKHVip;
                short[][] planetItems = doSKHVip[Util.nextInt(0, doSKHVip.length - 1)];
                short[] part = planetItems[Util.nextInt(0, planetItems.length - 1)];

                if (part.length > 5) {
                    int idx = Util.nextInt(2, 5); // Chỉ lấy đồ Long Đỏ -> Lưỡng Long
                    short itemId = part[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            } else {
                // ---- Rơi từ rada SKH VIP ----
                short[] rada = Manager.radaSKHVip;
                int idx = Util.nextInt(2, 5); // Theo quy tắc: index 2 - 5 là Long Đỏ -> Lưỡng Long
                if (idx < rada.length) {
                    short itemId = rada[idx];
                    dropCustomItem(itemId, x, y, plKill);
                }
            }
        }

        // ================== 2. Đồ Thần Linh ==================
        if (Util.isTrue(1, 100)) {
            short[] thanLinh = {
                555, 556, 557,
                558, 559, 560,
                561, 562, 563,
                564, 565, 566, 567
            };
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
            ItemMap nr2 = new ItemMap(this.zone, (short) 14, 1,
                    x + Util.nextInt(-15, 15), y, plKill.id);
            Service.gI().dropItemMap(zone, nr2);
        }

        // ================== 4. Vàng luôn rơi ==================
        ItemMap vang = new ItemMap(this.zone, (short) 190,
                Util.nextInt(28000, 30000),
                x + Util.nextInt(-15, 15),
                y,
                plKill.id);
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
        int mul = Util.nextInt(100, 116); // 100% -> 115%

        for (ItemOption src : baseOps) {
            int id = src.optionTemplate.id;
            int param = src.param;

            // Scale nếu không phải TL và không nằm trong danh sách miễn scale
            if (!(itemId >= 555 && itemId <= 567) && !isNonScaleOption(id)) {
                param = (int) Math.round(param * (mul / 100.0));

                // Clamp chí mạng
                if (id == 14) {
                    param = Math.min(param, 25);
                }
            }

            // Clone option mới
            ops.add(new ItemOption(id, param));
        }

        // 3) Thêm option vào itemMap
        itemMap.options.addAll(ops);

        // 4) Sao pha lê, option 107.
        // Chỉ thêm khi chưa có, không áp dụng cho nhẫn TL 561.
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
            return Util.nextInt(1, 4); // 1 - 3 sao
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
        this.attack();
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (prepareBom) {
            return 0;
        }

        if (!this.callCellCon && damage >= this.nPoint.hp) {
            this.callCellCon();
            return 0;
        }

        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }

            damage = this.nPoint.subDameInjureWithDeff(damage / 3);

            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 4;
            }

            this.nPoint.subHP(damage);

            if (isDie()) {
                setBom(plAtt);
                return 0;
            }

            return damage;
        }

        return 0;
    }

    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }

    @Override
    public void autoLeaveMap() {
        this.mc();

        if (this.currentLevel > 0) {
            if (this.bossStatus == BossStatus.AFK) {
                this.changeStatus(BossStatus.ACTIVE);
            }
        }

        if (Util.canDoWithTime(st, 900000)) {
            this.leaveMapNew();
        }

        if (this.zone != null && this.zone.getNumOfPlayers() > 0) {
            st = System.currentTimeMillis();
        }
    }

    public void mc() {
        if (zone == null) {
            return;
        }

        Player mc = zone.getNpc();

        if (mc != null) {
            if (Util.canDoWithTime(lastTimeChat, 3000)) {
                String textchat = text[indexChat];
                Service.gI().chat(mc, textchat);

                indexChat++;

                if (indexChat == text.length) {
                    indexChat = 0;
                    lastTimeChat = System.currentTimeMillis() + 7000;
                } else {
                    lastTimeChat = System.currentTimeMillis();
                }
            }

            if (Util.canDoWithTime(lastTimeMove, 15000)) {
                if (Util.isTrue(2, 3)) {
                    int x = this.location.x + Util.nextInt(-100, 100);
                    int y = x > 156 && x < 611 ? 288 : 312;
                    PlayerService.gI().playerMove(mc, x, y);
                }

                lastTimeMove = System.currentTimeMillis();
            }
        }
    }
}