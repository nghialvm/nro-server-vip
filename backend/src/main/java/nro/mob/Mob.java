package nro.mob;

import nro.inventory.InventoryService;
import nro.services.Service;
import models.Item.ItemMapService;
import nro.services.TaskService;
import models.Item.ItemService;
import nro.services.MapService;
import consts.ConstMap;
import consts.ConstMob;
import consts.ConstTask;
import nro.map.ItemMap;
import java.util.List;
import nro.map.Zone;
import nro.player.Location;
import nro.player.Detu;
import nro.player.Player;
import nro.skill.Skill;
import network.io.Message;
import nro.server.Manager;
import Utils.Util;
import java.util.ArrayList;
import nro.server.Maintenance;
import nro.server.ServerManager;
import Utils.Logger;
import Utils.TimeUtil;
import consts.ConstAttribute;
import consts.ConstTaskBadges;
import event.EventManager;
import java.util.Collections;
import models.Item.Item;
import models.Item.ItemOption;
import nro.achievement.AchievementService;
import nro.attribute.Attribute;
import nro.badges.BadgesTaskService;
import nro.boss.map.TrainingBoss.TrainningService;

public class Mob {

    private static Mob i;

    public static Mob gI() {
        if (i == null) {
            i = new Mob();
        }
        return i;
    }

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public List<Player> temporaryEnemies = new ArrayList<>();

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;
    public int type = 1;
    public int percent_gold;

    private long lastTimeAttackPlayer;
    private long timeAttack = 2000;
    public long lastTimePhucHoi = System.currentTimeMillis();
    public long lastTimeSendEffect = System.currentTimeMillis();

    public int MobImage;

    public boolean isMobMe;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.type = mob.type;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public void setDie() {
        this.lastTimePhucHoi = System.currentTimeMillis();
        this.lastTimeDie = System.currentTimeMillis();
    }

    public void addTemporaryEnemies(Player pl) {
        if (pl != null && !temporaryEnemies.contains(pl)) {
            temporaryEnemies.add(pl);
        }
    }

    public boolean isSieuQuai() {
        return this.lvMob > 0;
    }

    public void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
        long startTotal = System.currentTimeMillis();

        if (this.isDie()) {
            return;
        }
        // --- CHẶN PLAYER CHÍNH GÂY DAMAGE Ở MAP 249 ---
        if (plAtt != null && plAtt.zone != null && plAtt.zone.map.mapId == 179) {
            // Nếu là người chơi chính thì không gây damage
            if (!plAtt.isDeTu) {
                damage = 0;
            }
        }

        long start;
        long elapsed;

        start = System.currentTimeMillis();
        if (damage >= this.point.hp) {
            damage = this.point.hp;
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] damage cap to current HP: " + elapsed + "ms");
        }

        // Xử lý logic khi không cho phép chết ngay khi HP đầy
        start = System.currentTimeMillis();
        if (!dieWhenHpFull) {
            if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                if (MapService.gI().isMapRiengTu(this.zone.map.mapId)) {
                    damage = this.point.hp - 0;
                } else {
                    damage = this.point.hp - 1;
                }
            }
            if (this.tempId == ConstMob.HIRUDEGARN) {
                double maxDamageAtFullHP = 20_000_000;
                double hpRatio = (double) this.point.hp / this.point.maxHp;
                double power = 0.8;
                int calcDamage = (int) (maxDamageAtFullHP * Math.pow(hpRatio, power));
                int minDamage = (int) (calcDamage * 0.5);
                int maxDamage = Math.max(calcDamage, 10);
                damage = Util.nextInt(minDamage, maxDamage);
                if (damage < 10) {
                    damage = 10;
                }
            }
            if ((this.tempId == ConstMob.MOC_NHAN || this.tempId == ConstMob.BU_NHIN_MA_QUAI) && damage > this.point.maxHp / 10) {
                damage = this.point.maxHp / 10;
            }
            if (plAtt != null && MapService.gI().isMapDiaNguc(plAtt.zone.map.mapId)) {
                damage = this.point.maxHp / 10;
            }
            if (plAtt != null && MapService.gI().isMapNguHanhSon(plAtt.zone.map.mapId)) {
                damage = 80000;
            }
            if (plAtt != null && !MapService.gI().isMapHungVuongEvent(plAtt.zone.map.mapId)
                    && (this.tempId == ConstMob.VOI_CHIN_NGA || this.tempId == ConstMob.GA_CHIN_CUA || this.tempId == ConstMob.NGUA_CHIN_LMAO)
                    && damage > this.point.maxHp / 10) {
                damage = this.point.maxHp / 10;
            }
            if (plAtt != null && MapService.gI().isMapHungVuongEvent(plAtt.zone.map.mapId)) {
                if (this.tempId == ConstMob.VOI_CHIN_NGA && damage > 100_000) {
                    damage = 100_000;
                }
                if (this.tempId == ConstMob.NGUA_CHIN_LMAO && damage > 75_000) {
                    damage = 75_000;
                }
                if (this.tempId == ConstMob.GA_CHIN_CUA && damage > 60_000) {
                    damage = 60_000;
                }
            }
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] dieWhenHpFull checks & damage adjustment: " + elapsed + "ms");
        }

        // Tăng damage theo các hiệu ứng từ plAtt
        start = System.currentTimeMillis();
        if (plAtt != null) {
            if (this.tempId == ConstMob.MAY_DO_SUC_MANH) {
                plAtt.TotalDameMayDam += damage;
                plAtt.lastTimeDameMayDam = System.currentTimeMillis();
            }
            int TlDameFly = plAtt.nPoint.tlDameMobFly;
            int TlDameMonkey = plAtt.nPoint.tlDameMobMonkey;
            int TlDameRun = plAtt.nPoint.tlDameMobRun;
            if (TlDameFly > 0 && isMobBay()) {
                damage += Util.CrisGH((damage / 100) * TlDameFly);
            }
            if (TlDameMonkey > 0 && isMobKhi()) {
                damage += Util.CrisGH((damage / 100) * TlDameMonkey);
            }
            if (TlDameRun > 0 && isMobMatDat()) {
                damage += Util.CrisGH((damage / 100) * TlDameRun);
            }
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] apply plAtt damage modifiers: " + elapsed + "ms");
        }

        // Kiểm tra map KhiGasHuyDiet và giới hạn damage theo skill
        start = System.currentTimeMillis();
        if (MapService.gI().isMapKhiGasHuyDiet(this.zone.map.mapId)) {
            boolean mob76Die = true;
            for (Mob mob : this.zone.mobs) {
                if (!mob.isDie() && mob.tempId == ConstMob.CO_MAY_HUY_DIET) {
                    mob76Die = false;
                    break;
                }
            }
            if (!mob76Die && plAtt != null && plAtt.playerSkill != null && plAtt.playerSkill.skillSelect != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.LIEN_HOAN:
                    case Skill.ANTOMIC:
                    case Skill.MASENKO:
                    case Skill.KAMEJOKO:
                        damage = 1;
                        break;
                }
            }
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] map KhiGasHuyDiet checks: " + elapsed + "ms");
        }

        // Kiểm tra điều kiện đặc biệt khi không cho chết và map
        start = System.currentTimeMillis();
        if (!dieWhenHpFull && !isBigBoss() && !MapService.gI().isMapPhoBan(this.zone.map.mapId) && this.lvMob > 0
                && plAtt != null && plAtt.charms.tdOaiHung < System.currentTimeMillis()) {
            damage = ((this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000) * (10.0 / 100));
            this.mobAttackPlayer(plAtt);
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] special death & map checks: " + elapsed + "ms");
        }

        // Mob tấn công lại player nếu là boss
        start = System.currentTimeMillis();
        if (plAtt != null && plAtt.isBoss && this.tempId > 0 && Util.isTrue(1, 2) && Util.canDoWithTime(lastTimeAttackPlayer, 2500)) {
            this.mobAttackPlayer(plAtt);
            lastTimeAttackPlayer = System.currentTimeMillis();
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] boss retaliate attack: " + elapsed + "ms");
        }

        // Trừ HP mob
        start = System.currentTimeMillis();
        this.point.hp -= damage;
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] subtract HP: " + elapsed + "ms");
        }

        // Thêm vào danh sách kẻ thù tạm thời
        start = System.currentTimeMillis();
        addTemporaryEnemies(plAtt);
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] addTemporaryEnemies: " + elapsed + "ms");
        }

        // Xử lý khi mob chết hoặc còn sống
        start = System.currentTimeMillis();
        if (this.isDie()) {
            this.status = 0;
            this.setDie();
            this.temporaryEnemies.clear();
            if (plAtt != null) {
                this.sendMobDieAffterAttacked(plAtt, Util.CrisGH(damage));
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneClanTaskKillMob(plAtt, this);
                AchievementService.gI().checkDoneTaskKillMob(plAtt, this);
                if (plAtt.isPl()) {
                    plAtt.playerTask.kolTask.addCount();
                }
                TaskService.gI().checkDoneEventTaskKillMob(plAtt, this);
            }
            if (this.id == 13) {
                this.zone.isbulon1Alive = false;
            }
            if (this.id == 14) {
                this.zone.isbulon2Alive = false;
            }
        } else {
            this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? (plAtt.nPoint != null && plAtt.nPoint.isCrit) : false);
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] mob death/alive handling: " + elapsed + "ms");
        }

        if (plAtt != null && plAtt.isNguoiYeu) {
            Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
        }
        long start2 = 0;
        long start3 = 0;
        if (plAtt != null) {
            // Các xử lý buff và trainning cho player
            start = System.currentTimeMillis();
            if (plAtt.isPl() && plAtt.satellite != null && plAtt.satellite.isDefend) {
                plAtt.satellite.isDefend = false;
            }
            start2 = System.currentTimeMillis();

            long tiemNang = getTiemNangForPlayer(plAtt, damage);
            long afterCal = System.currentTimeMillis();

            if (afterCal - start2 > 50) {
                System.out.println("[SLOW] GET tiemnanng : " + (afterCal - start2) + "ms");
            }
            Service.gI().addSMTN(plAtt, (byte) 2, tiemNang, true);
            long afterAddSMTN = System.currentTimeMillis();

            if (afterAddSMTN - afterCal > 50) {
                System.out.println("[SLOW] ADD TNSM " + (afterAddSMTN - afterCal));
            }

            TrainningService.gI().tangTnsmLuyenTap(plAtt, tiemNang);
            long afterTrain = System.currentTimeMillis();
            if (afterTrain - afterAddSMTN > 50) {
                System.out.println("[SLOW] TRAINGING : " + (afterTrain - afterAddSMTN) + "ms");
            }

            if (afterTrain - start2 > 50) {
                System.out.println("[TIME] calSucManh: " + (afterCal - start) + "ms");
                System.out.println("[TIME] addSMTN: " + (afterAddSMTN - afterCal) + "ms");
                System.out.println("[TIME] tangTnsmLuyenTap: " + (afterTrain - afterAddSMTN) + "ms");
            }

            start3 = System.currentTimeMillis();
            TrainningService.gI().tangTnsmLuyenTap(plAtt, tiemNang);
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 50) {
            System.out.println("[SLOW] buff & training update: " + elapsed + "ms" + " START 2 : " + (System.currentTimeMillis() - start2) + " START 3 : " + (System.currentTimeMillis() - start3));
        }

        long totalElapsed = System.currentTimeMillis() - startTotal;
        if (totalElapsed > 500) {
            System.out.println("[SLOW] Total injured() time: " + totalElapsed + "ms for mob id: " + this.id + ", attacker: " + (plAtt != null ? plAtt.name : "null"));
        }
    }

    public long getTiemNangForPlayer(Player pl, double dame) {
        long startTotal = System.currentTimeMillis();

        int levelPlayer = Service.getInstance().getCurrLevel(pl);

        long start = System.currentTimeMillis();
        int n = levelPlayer - this.level;
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] calculate level difference n: " + elapsed + "ms");
        }

        start = System.currentTimeMillis();
        long pDameHit;
        if (point.getHpFull() >= 100000000) {
            pDameHit = Util.CrisGH(dame) * 500 / point.getHpFull();
        } else {
            pDameHit = Util.CrisGH(dame) * 100 / point.getHpFull();
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] calculate pDameHit: " + elapsed + "ms");
        }

        start = System.currentTimeMillis();
        long tiemNang = pDameHit * maxTiemNang / 100;
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] initial tiemNang calculation: " + elapsed + "ms");
        }

        start = System.currentTimeMillis();
        if (n >= 0) {
            for (int j = 0; j < n; j++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int j = 0; j < -n; j++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] adjust tiemNang in loop by n: " + elapsed + "ms, n=" + n);
        }

        start = System.currentTimeMillis();
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (this.isSieuQuai()) {
            tiemNang *= 1;
        }
        if (this.tempId == ConstMob.MAY_DO_SUC_MANH) {
            tiemNang = 1;
        }
        if (this.tempId == ConstMob.HIRUDEGARN) {
            tiemNang = 1;
        }
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] apply special case adjustments: " + elapsed + "ms");
        }

        start = System.currentTimeMillis();
        tiemNang = Util.CrisGH(pl.nPoint.calSucManhTiemNang(tiemNang));
        elapsed = System.currentTimeMillis() - start;
        if (elapsed > 10) {
            System.out.println("[SLOW] apply calSucManhTiemNang: " + elapsed + "ms");
        }

        long totalElapsed = System.currentTimeMillis() - startTotal;
        if (totalElapsed > 50) {
            System.out.println("[SLOW] Total getTiemNangForPlayer time: " + totalElapsed + "ms for player: " + pl.name);
        }

        return tiemNang;
    }

    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        if (!this.isDie() && this.tempId == ConstMob.CO_MAY_HUY_DIET && Util.canDoWithTime(lastTimeSendEffect, 1000)) {
            sendEffect(55);
            lastTimeSendEffect = System.currentTimeMillis();
        }
        if (!this.isDie() && this.tempId == ConstMob.MAY_DO_SUC_MANH) {
            this.point.hp = 2000000000;
        }
        if (this.isDie() && !Maintenance.isRunning && !isBigBoss()) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI: {
                    if (this.tempId == ConstMob.BULON && this.zone.isTUTAlive && Util.canDoWithTime(lastTimeDie, 10000)) {
                        this.hoiSinh();
                        this.hoiSinhMobPhoBan();
                        if (this.id == 13) {
                            this.zone.isbulon1Alive = true;
                        }
                        if (this.id == 14) {
                            this.zone.isbulon2Alive = true;
                        }
                    }
                    break;
                }
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    break;
                case ConstMap.MAP_CON_DUONG_RAN_DOC:
                    break;
                case ConstMap.MAP_KHI_GAS_HUY_DIET:
                    break;
                case ConstMap.MAP_BOSS_BANG_HOI:
                    break;
                case ConstMap.MAP_TAY_KARIN:
                    break;
                default: {
                    if (this.zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
                        return;
                    }
                    if (Util.canDoWithTime(lastTimePhucHoi, 30000) && !isDie()) {
                        lastTimePhucHoi = System.currentTimeMillis();
                        long hpMax = this.point.maxHp;
                        if (this.point.hp < hpMax) {
                            hoi_hp(hpMax / 10);
                        } else {
                            this.sendMobHoiSinh();
                        }
                    }
                    if (Util.canDoWithTime(lastTimeDie, 5000) && isDie()) {
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }

                }
            }
        }
        effectSkill.update();
        attack();
    }
    
    public boolean isBigBoss() {
        return (this.tempId == ConstMob.HIRUDEGARN || this.tempId == ConstMob.VUA_BACH_TUOC || this.tempId == ConstMob.ROBOT_BAO_VE || this.tempId == ConstMob.GAU_TUONG_CUOP
                || this.tempId == ConstMob.VOI_CHIN_NGA || this.tempId == ConstMob.GA_CHIN_CUA || this.tempId == ConstMob.NGUA_CHIN_LMAO || this.tempId == ConstMob.PIANO
                || this.tempId == ConstMob.KONG || this.tempId == ConstMob.GOZILLA);
    }

    public boolean isBigBossHungVuongEvent() {
        return (this.tempId == ConstMob.VOI_CHIN_NGA || this.tempId == ConstMob.GA_CHIN_CUA || this.tempId == ConstMob.NGUA_CHIN_LMAO || this.tempId == ConstMob.KONG || this.tempId == ConstMob.GOZILLA);
    }

    public boolean isMobBay() {
        return this.type == 4;
    }

    public boolean isMobKhi() {
        return (this.tempId == ConstMob.KHI_GIAP_SAT || this.tempId == ConstMob.KHI_LONG_DEN || this.tempId == ConstMob.KHI_LONG_DO || this.tempId == ConstMob.KHI_LONG_VANG
                || this.tempId == ConstMob.KHI_LONG_XANH);
    }

    public boolean isMobHeo() {
        return (this.tempId == ConstMob.HEO_DA_XANH || this.tempId == ConstMob.HEO_RUNG || this.tempId == ConstMob.HEO_RUNG_ME || this.tempId == ConstMob.HEO_XANH_ME
                || this.tempId == ConstMob.HEO_XAYDA || this.tempId == ConstMob.HEO_XAYDA_ME);
    }

    public boolean isMobMatDat() {
        return this.type == 1;
    }

    public void attack() {
        Player player = getPlayerCanAttack();
        if (!isDie() && !effectSkill.isHaveEffectSkill() && tempId != ConstMob.MOC_NHAN && tempId != ConstMob.MAY_DO_SUC_MANH
                && tempId != ConstMob.BU_NHIN_MA_QUAI && tempId != ConstMob.CO_MAY_HUY_DIET && !this.isBigBoss()
                && (this.lvMob < 1 || MapService.gI().isMapPhoBan(this.zone.map.mapId))
                && Util.canDoWithTime(lastTimeAttackPlayer, timeAttack)) {
            if (player != null) {
                this.mobAttackPlayer(player);
            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public Player getPlayerCanAttack() {
        Player plAttack = getFirstPlayerCanAttack();
        if (plAttack != null) {
            return plAttack;
        }

        int distance = 100;
        try {
            List<Player> players = this.zone.getNotBosses();
            if (players == null || players.isEmpty()) {
                return null; // Không có player nào trong zone
            }

            for (Player pl : players) {
                if (pl == null) {
                    continue;
                }

                boolean canAttack = !pl.isDie()
                        && !pl.isBoss
                        && !pl.isPetFollow
                        && !pl.isDuongTang
                        && (pl.satellite == null || !pl.satellite.isDefend)
                        && (pl.effectSkin == null || !pl.effectSkin.isVoHinh)
                        && (this.tempId > 18 || (this.tempId > 9 && this.type == 4) || isBigBoss());

                if (canAttack) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance || isBigBoss()) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }

            this.timeAttack = 2000;
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }

        return plAttack;
    }

    private Player getFirstPlayerCanAttack() {
        Player plAtt = null;
        try {
            List<Player> playersMap = zone.getHumanoids();
            int dis = 300;
            if (playersMap != null) {
                for (Player plAttt : new ArrayList<>(playersMap)) {
                    if (plAttt == null) {
                        continue;
                    }
                    if (plAttt.isDie()
                            || plAttt.isBoss
                            || (plAttt.satellite != null && plAttt.satellite.isDefend)
                            || (plAttt.effectSkin != null && plAttt.effectSkin.isVoHinh)
                            || !this.temporaryEnemies.contains(plAttt)) {
                        continue;
                    }
                    int d = Util.getDistance(plAttt, this);
                    if (d <= dis) {
                        dis = d;
                        plAtt = plAttt;
                    }
                }
            }
            this.timeAttack = 1000;
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
        return plAtt;
    }

    private void mobAttackPlayer(Player player) {
        double dameMob = Util.CrisGH(this.point.getDameAttack());
        if (dameMob > 2_000_000_000) {
            dameMob = 2_000_000_000;
        }
        if (player.charms != null && player.charms.tdDaTrau > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (player.clan != null && player.clan.BuaDaTrau > System.currentTimeMillis()) {
            int clanLevel = player.clan.level;
            int bonusPercent = clanLevel * 20;
            if (bonusPercent > 200) {
                bonusPercent = 200;
            }
            dameMob -= (dameMob * bonusPercent / 100);
        }
        if (player.isDeTu && ((Detu) player).master.charms != null && ((Detu) player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameMob /= 2;
        }
        if (this.lvMob > 0 && !MapService.gI().isMapPhoBan(this.zone.map.mapId)) {
            dameMob = (player.nPoint.hpMax * (10.0 / 100));
        }
        if (player.satellite != null && player.satellite.isDefend) {
            dameMob -= dameMob / 5;
        }
        if (player.itemTime != null && player.itemTime.iscommenson) {
            dameMob = Math.round(dameMob * 0.1);
        }
        if (this.lvMob > 0 && player.charms.tdOaiHung > System.currentTimeMillis()) {
            dameMob = 0;
        }
        if (MapService.gI().isMapNguHanhSon(player.zone.map.mapId)) {
            dameMob = player.nPoint.hpMax / 10;
        }
        double dame = player.injured(null, Util.CrisGH(dameMob), false, true);
        this.sendMobAttackMe(player, dame);
        this.sendMobAttackPlayer(player);
        this.phanSatThuong(player, dame);
    }

    private void sendMobAttackMe(Player player, double dame) {
        if (!player.isDeTu && !player.isBo && !player.isMe && !player.isPetFollow && !player.isDuongTang && !player.isBot && !player.isBot_Event && !player.isBot_New && !player.isBot_Valentine && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writeCris(Util.CrisGH(dame), Manager.readInt);
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Mob.class, e);
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writeCris(Util.CrisGH(player.nPoint.hp), Manager.readInt);
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public int lvMob() {
        for (Mob mobMap : this.zone.mobs) {
            if (mobMap.lvMob > 0) {
                return 0;
            }
        }
        this.lvMob = this.tempId > 18 && !isBigBoss() ? Util.isTrue(10, 100) ? 1 : 0 : 0;
        this.point.hp = this.lvMob > 0 ? this.point.maxHp <= 20000000 ? this.point.maxHp * 10 : 2000000000 : this.point.maxHp;
        return this.lvMob;
    }

    public void sendMobHoiSinh() {
        Message msg = null;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob());
            msg.writeCris(Util.CrisGH(this.point.hp), Manager.readInt);
//            msg.writeLong(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            this.sendMobMaxHp(this.point.hp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void sendMobMaxHp(long maxHp) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(maxHp), Manager.readInt);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void hoi_hp(long hp) {
        Message msg = null;
        try {
            this.point.sethp(this.point.gethp() + hp);
            long HP = hp > 0 ? 1 : Math.abs(hp);
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(this.point.gethp()), Manager.readInt);
            msg.writeCris(Util.CrisGH(HP), Manager.readInt);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    public void sendEffect(int Effect) {
        Message msg = null;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(this.point.gethp()), Manager.readInt);
            msg.writeCris(Util.CrisGH(this.point.gethp()), Manager.readInt);
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(Effect);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
                msg = null;
            }
        }
    }

    private void sendMobDieAffterAttacked(Player plKill, double dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(dameHit), Manager.readInt);
            msg.writer().writeBoolean(plKill.nPoint.isCrit);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            if (plKill.isPl()) {
                List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
                hutItem(plKill, items);
            }
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    private ItemMap createItemMap(Player player, int itemId, int x, int y, ItemOption... options) {
        ItemMap itemMap = new ItemMap(this.zone, itemId, 1, x + Util.nextInt(-10, 10), y, player.id);
        Collections.addAll(itemMap.options, options);
        return itemMap;
    }

    private ItemOption opt(int id, int param) {
        return new ItemOption(id, param);
    }

    private List<ItemMap> getItemEventMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        if (player.isBoss || this.tempId == 0) {
            return list;
        }

        double tyLeMayMan = player.nPoint.tlMayman;
        int mapId = player.zone.map.mapId;

        // ------------------------- TẾT NGUYÊN ĐÁN -------------------------
        if (EventManager.LUNNAR_NEW_YEAR && !MapService.gI().isMapNotDropItems(mapId)) {
            if (isMobHeo() && Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 748, x, yEnd, opt(86, 0), opt(93, 30)));
            } else {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 12, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, Util.nextInt(749, 750), x, yEnd, opt(86, 0), opt(93, 30)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 8, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, Util.nextInt(1177, 1181), x, yEnd, opt(86, 0), opt(93, 30)));
                }
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 8, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, Util.nextInt(1473, 1474), x, yEnd, opt(86, 0), opt(93, 30)));
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1475, x, yEnd, opt(86, 0), opt(93, 30)));
            }
        }

        // ------------------------- GIÁNG SINH -------------------------
        if (EventManager.CHRISTMAS && !MapService.gI().isMapNotDropItems(mapId)) {
            if (InventoryService.gI().findNonNoel(player)) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 100, player.itemTime.isUseCoBonLa)
                        && player.itemEvent != null && player.itemEvent.canDropKeoGiangSinh(99)) {
                    list.add(createItemMap(player, 533, x, yEnd, opt(30, 0)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 80, player.itemTime.isUseCoBonLa)
                        && player.itemEvent != null && player.itemEvent.canDropTatVoGiangSinh(99)) {
                    list.add(createItemMap(player, 649, x, yEnd, opt(30, 0), opt(93, 30)));
                }
            }
            if (MapService.gI().isMapChristMasEvent(mapId)) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, Util.nextInt(1459, 1461), x, yEnd, opt(86, 0), opt(93, 30)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 50, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 1839, x, yEnd, opt(86, 0), opt(93, 30)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 1445, x, yEnd, opt(86, 0), opt(93, 30)));
                }
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 199, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, Util.nextInt(925, 927), x, yEnd, opt(87, 0), opt(30, 0), opt(93, 35)));
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 99, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, Util.nextInt(928, 931), x, yEnd, opt(87, 0), opt(30, 0), opt(93, 35)));
            }
        }

        // ------------------------- VU LAN -------------------------
        if (EventManager.VU_LAN_FESTIVAL && MapService.gI().isMapDiaNguc(mapId) && !MapService.gI().isMapNotDropItems(mapId)) {
            boolean useMayDo = player.itemTime != null && player.itemTime.isUseMayDoLinhHon;
            if (Util.getChanceFromLuck(tyLeMayMan, 1, useMayDo ? 5 : 10, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1258, x, yEnd, opt(30, 0)));
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 5, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1032, x, yEnd, opt(30, 0)));
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 7, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1035, x, yEnd, opt(30, 0)));
            }
        }

        // ------------------------- HALLOWEEN -------------------------
        if (EventManager.HALLOWEEN && !MapService.gI().isMapNotDropItems(mapId)) {
            double hp = this.point.hp;
            double hpFactor = Math.floor(hp / 1000.0); // Mỗi 200 HP tăng 1 đơn vị

            // Tính max, càng nhiều HP thì càng dễ rơi, nhưng giới hạn trong khoảng [10, 30]
            double max = 30.0 - (hpFactor * 0.02);
            max = Math.max(10.0, Math.min(30.0, max)); // ép giá trị max nằm trong [10, 30]

            boolean hasMayDoNgocBi = player.itemTime != null && player.itemTime.isUseMayDoNgocBi;
            boolean hasCoBonLa = player.itemTime != null && player.itemTime.isUseCoBonLa;

            if (hasMayDoNgocBi
                    && Util.getChanceFromLuck(tyLeMayMan, 1, max, hasCoBonLa)) {
                list.add(createItemMap(player, Util.nextInt(705, 708), x, yEnd,
                        opt(87, 0), opt(30, 0), opt(93, 35)));
            }

            if (Util.getChanceFromLuck(tyLeMayMan, 1, max, hasCoBonLa)) {
                list.add(createItemMap(player, 901, x, yEnd,
                        opt(86, 0), opt(93, 35)));
            }
        }

        // ------------------------- 8/3 -------------------------
        if (EventManager.INTERNATIONAL_WOMANS_DAY && !MapService.gI().isMapNotDropItems(mapId)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1526, x, yEnd, opt(30, 0), opt(93, 30)));
            }
            if (!MapService.gI().isMapUpSKH(mapId)
                    && Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1505, x, yEnd, opt(86, 0), opt(93, 35), opt(30, 0)));
            }
            if (!InventoryService.gI().findItemGender(player)
                    && Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1507, x, yEnd, opt(93, 35), opt(86, 0), opt(30, 0)));
            }
            if (MapService.gI().isMapSauLang(mapId)
                    && Util.getChanceFromLuck(tyLeMayMan, 1, 30, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1508, x, yEnd, opt(93, 35), opt(86, 0), opt(30, 0)));
            }
        }

        // ------------------------- TRUNG THU -------------------------
        if (EventManager.TRUNG_THU && !MapService.gI().isMapNotDropItems(mapId)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 50, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1311, x, yEnd, opt(30, 0), opt(93, 35)));
            }
            //
            if (!MapService.gI().isMapMobBangHoi_Part1(mapId)) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, Util.nextInt(888, 889), x, yEnd, opt(86, 0), opt(30, 0), opt(93, 35)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 886, x, yEnd, opt(30, 0), opt(93, 35)));
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 25, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 887, x, yEnd, opt(30, 0), opt(93, 35)));
                }
            }
            if (MapService.gI().isMapMobBangHoi_Part1(mapId)) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 30, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 1312, x, yEnd, opt(86, 0), opt(30, 0), opt(93, 35)));
                }
            }
        }

        // ------------------------- HÙNG VƯƠNG --------------------------------
        if (EventManager.HUNG_VUONG && !MapService.gI().isMapNotDropItems(mapId)) {
            if (MapService.gI().isMapKongvsGodzila(mapId)) {
                if (this.tempId == ConstMob.KONG) {
                    list.add(createItemMap(player, 1551, x, yEnd,
                            opt(50, Util.nextInt(15, 18)),
                            opt(77, Util.nextInt(15, 18)),
                            opt(103, Util.nextInt(15, 18)),
                            opt(94, Util.nextInt(10, 15)),
                            (Util.isTrue(90, 100) ? opt(93, Util.nextInt(3, 7)) : opt(73, 0))));
                    if (Util.isTrue(70, 100)) {
                        list.add(createItemMap(player, 1173, x, yEnd, opt(30, 0)));
                    }
                    for (int j = 0; j < 10; j++) {
                        short[] List_Item = {1548, 1547, 1545};
                        list.add(createItemMap(player, List_Item[Util.nextInt(0, List_Item.length - 1)], x + Util.nextInt(-50, 50), yEnd, opt(87, 0), opt(93, 30)));
                    }
                }
                if (this.tempId == ConstMob.GOZILLA) {
                    list.add(createItemMap(player, 1550, x, yEnd,
                            opt(50, Util.nextInt(15, 17)),
                            opt(77, Util.nextInt(15, 17)),
                            opt(103, Util.nextInt(15, 17)),
                            opt(14, Util.nextInt(11, 15)),
                            (Util.isTrue(90, 100) ? opt(93, Util.nextInt(3, 7)) : opt(73, 0))));
                    if (Util.isTrue(70, 100)) {
                        list.add(createItemMap(player, 1173, x, yEnd, opt(30, 0)));
                    }
                    for (int j = 0; j < 10; j++) {
                        short[] List_Item = {1548, 1547, 1545};
                        list.add(createItemMap(player, List_Item[Util.nextInt(0, List_Item.length - 1)], x + Util.nextInt(-50, 50), yEnd, opt(87, 0), opt(93, 30)));
                    }
                }
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 1546, x, yEnd, opt(86, 0), opt(93, 30)));
                }
            }
            if (this.tempId == ConstMob.VOI_CHIN_NGA) {
                for (int j = 0; j < Util.nextInt(1, 2); j++) {
                    list.add(createItemMap(player, 1220, x + Util.nextInt(-50, 50), yEnd, opt(87, 0), opt(93, 30)));
                }
                for (int j = 0; j < Util.nextInt(1, 10); j++) {
                    list.add(createItemMap(player, 861, x + Util.nextInt(-50, 50), yEnd));
                }
            }
            if (this.tempId == ConstMob.GA_CHIN_CUA) {
                for (int j = 0; j < Util.nextInt(1, 2); j++) {
                    list.add(createItemMap(player, 1221, x + Util.nextInt(-50, 50), yEnd, opt(87, 0), opt(93, 30)));
                }
                for (int j = 0; j < Util.nextInt(1, 10); j++) {
                    list.add(createItemMap(player, 861, x + Util.nextInt(-50, 50), yEnd));
                }
            }
            if (this.tempId == ConstMob.NGUA_CHIN_LMAO) {
                for (int j = 0; j < Util.nextInt(1, 2); j++) {
                    list.add(createItemMap(player, 1222, x + Util.nextInt(-50, 50), yEnd, opt(87, 0), opt(93, 30)));
                }
                for (int j = 0; j < Util.nextInt(1, 10); j++) {
                    list.add(createItemMap(player, 861, x + Util.nextInt(-50, 50), yEnd));
                }
            }
            if (isMobHeo()) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 20, player.itemTime.isUseCoBonLa)) {
                    list.add(createItemMap(player, 1549, x + Util.nextInt(-15, 15), yEnd, opt(87, 0), opt(30, 0), opt(93, 30)));
                }
            }
        }

        // ------------------------- VALENTINE ---------------------------------
        if (EventManager.VALENTINE_DAY && !MapService.gI().isMapNotDropItems(mapId)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 18, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 709, x + Util.nextInt(-15, 15), yEnd, opt(86, 0), opt(93, 30)));
            }
        }

        // ------------------------- 20/10 -------------------------------------
        if (EventManager.DAY_20_10 && !MapService.gI().isMapNotDropItems(mapId)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 15, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1093, x + Util.nextInt(-15, 15), yEnd, opt(93, 30)));
            }
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 15, player.itemTime.isUseCoBonLa)) {
                list.add(createItemMap(player, 1095, x + Util.nextInt(-15, 15), yEnd, opt(93, 30)));
            }
        }

        return list;
    }

    //--------------------------------------------------------------------------
    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isDeTu && !player.isBo && !player.isMe && !player.isPetFollow && !player.isPhanThan && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else if (player.isMaster()) {
            if (player.getMaster().charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player.getMaster(), item.itemMapId, true);
                }
            }
        } else {
            if (((Detu) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Detu) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan vat
            }
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
        return itemReward;
    }

    private int getGoldDrop(Player player, Mob mob) {
        long playerPower = player.nPoint.power;
        long mobHp = mob.point.maxHp;
        double baseGold = (mobHp / 200.0) * Util.nextInt(20, 30);
        double smFactor = 1 + Math.pow(playerPower / (mobHp * 1000.0 + 1), 0.5);
        double totalGold = baseGold / smFactor;
        totalGold *= Math.log10(mobHp + 1);
        if (totalGold > 300_000) {
            totalGold = 300_000;
        }
        totalGold *= 0.5 + Math.random() * 0.2;
        return Math.max(1, (int) totalGold);
    }

    private void dropGold(Player player, Mob mob, List<ItemMap> list, Zone zone, int x, int y) {
        Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.VANG);
        int goldAmount = getGoldDrop(player, mob);
        if (goldAmount <= 0) {
            return;
        }
        goldAmount = goldAmount + (this.percent_gold - 1);
        double playerBonus = 1.0 + player.nPoint.tlGold / 100.0;
        double totalGold = goldAmount * playerBonus;
        if (at != null && !at.isExpired()) {
            totalGold *= at.getValue() / 100.0;
        }
        int finalGold = (int) totalGold;
        if (finalGold > 300_000) {
            finalGold = 300_000;
        }
        if (finalGold <= 0) {
            finalGold = 1;
        }
        ItemMap itemMap = new ItemMap(zone, 190, finalGold, x + Util.nextInt(-10, 10), y, player.id);
        list.add(itemMap);
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();

        // Nhiệm vụ huy hiệu
        BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.ONG_THAN_VE_CHAI, 1);

        // Không cho boss nhận reward
        if (player.isBoss) {
            return list;
        }

        // ------------------ ADD EVENT ITEM ------------------
        list.addAll(getItemEventMobReward(player, x, yEnd));

        double tyLeMayMan = player.nPoint.tlMayman;
        int mapid = player.zone.map.mapId;

        // ------------------ DROP ĐIỂM NGŨ HÀNH SƠN ------------------
        if (MapService.gI().isMapNguHanhSon(mapid)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 5, player.itemTime.isUseCoBonLa)) {
                player.event.addEventPointNHS(1);
                Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm Ngũ Hành Sơn");
            }
        }
           // ------------------ DROP MẢNH VỠ BÔNG TAI ------------------
        if (MapService.gI().isMapBang(mapid)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
                ItemMap it = new ItemMap(this.zone, 934, 1, x + Util.nextInt(-10, 10), yEnd, player.id);
                Service.gI().dropItemMap(this.zone, it);
            }
        }
//           if (MapService.gI().isMapBang(mapid)) {
//            if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
//                ItemMap it = new ItemMap(this.zone, 933, 1, x + Util.nextInt(-10, 10), yEnd, player.id);
//                Service.gI().dropItemMap(this.zone, it);
//            }
//        }

        // ------------------ DROP BÌNH NƯỚC ------------------
        if (mapid == 5 || mapid == 13 || mapid == 29 || mapid == 30 || mapid == 33 || mapid == 34) {
            Player pl = player;
            if (pl.isDeTu) {
                pl = ((Detu) pl).master;
            }

            if ((mapid == 5 || mapid == 13)
                    ? Util.getChanceFromLuck(tyLeMayMan, 1, 5, player.itemTime.isUseCoBonLa)
                    : Util.getChanceFromLuck(tyLeMayMan, 1, 4, player.itemTime.isUseCoBonLa)) {
                if (pl.itemEvent != null && pl.itemEvent.canDropBinhNuoc(100)) {
                    list.add(new ItemMap(zone, 456, 1, x, yEnd, pl.id));
                }
            }
        }

        // ------------------ DROP VÀNG THƯỜNG ------------------
        if (!MapService.gI().isMapBanDoKhoBau(mapid) && !MapService.gI().isMapLang(mapid) && Util.isTrue(2, 10)) {
            dropGold(player, this, list, this.zone, x, yEnd);
        }

        // ------------------ DROP BẢN ĐỒ KHO BÁU ------------------
        if (MapService.gI().isMapBanDoKhoBau(mapid)) {
            DropVangForBanDoKhoBau(player, yEnd);
        }

        // ------------------ DROP THIÊN TỬ ------------------
        if (MapService.gI().isMapHanhTinhNgucTu(mapid)) {
            if (player.LastTimeDanhHieu_ThienTu > 0 && player.isUseDanhHieu_ThienTu) {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 10, player.itemTime.isUseCoBonLa)) {
                    list.add(new ItemMap(this.zone, 2012, 1, x + Util.nextInt(-10, 10), yEnd, player.id));
                }
            } else {
                if (Util.getChanceFromLuck(tyLeMayMan, 1, 15, player.itemTime.isUseCoBonLa)) {
                    list.add(new ItemMap(this.zone, 2012, 1, x + Util.nextInt(-10, 10), yEnd, player.id));
                }
            }
        }

        // ------------------ DROP KÍCH HOẠT THƯỜNG ------------------
//        dropItemKichHoat(player, this.zone, mapid, x, yEnd, list);

        // -------------------DROP CAPSULE----------------------------
        if (player.itemTime != null && player.itemTime.isUseMayDo && MapService.gI().isMapTuongLai(mapid) && (Util.isTrue(1, 30))) {
            list.add(new ItemMap(zone, 380, 1, x, yEnd, player.id));
        }
        // -------------------DROP DÁ SIEU HÓA----------------------------
        if (player.itemTime != null && player.itemTime.isUseMayDoSieuHoa && MapService.gI().isMapCold(mapid) && (Util.isTrue(1, 70))) {
            list.add(new ItemMap(zone, 1979, 1, x, yEnd, player.id));
        }

        // -------------------DROP ITEM RÁC---------------------------
        if (Util.isTrue(1, 7) && !MapService.gI().isMapNotDropItems(mapid)) {
            ItemMap it = new ItemMap(this.zone, 225, 1, x + Util.nextInt(-10, 10), yEnd, player.id);
            it.addOptionParam(74, 0);
            list.add(it);
        }
        if (Util.isTrue(1, 25)) {
            list.add(new ItemMap(this.zone, 20, 1, x + Util.nextInt(-10, 10), yEnd, player.id));
        }
       // -------------------DROP THỎI VÀNG---------------------------
        if (player.isActive()) {
            int tile = 200;
            if (player.getSession().vethang == 1
                    && player.getSession().vethangExpire > System.currentTimeMillis()) {
                tile = 50;
            } else if (player.getSession().vetuan == 1
                    && player.getSession().vetuanExpire > System.currentTimeMillis()) {
                tile = 150;
            }
            if (Util.isTrue(1, tile)) {
                list.add(new ItemMap(this.zone, 457, 1,
                        x + Util.nextInt(-10, 10), yEnd, player.id));
            }
        }
        
        if (Util.isTrue(1, 30)) {
            list.add(new ItemMap(this.zone, 19, 1, x + Util.nextInt(-10, 10), yEnd, player.id));
        }
        if (player.nPoint.isDoSPL && Util.isTrue(1, 10)) {
            int rand = Util.nextInt(0, 6);
            ItemMap it = new ItemMap(this.zone, (rand + 441), 1, x + Util.nextInt(-10, 10), yEnd, player.id);
            it.addOptionParam(95 + rand, (rand == 3 || rand == 4) ? 3 : 5);
            list.add(it);
        }

        // ------------------ DROP VÀNG THƯỜNG ------------------
        if (!MapService.gI().isMapBanDoKhoBau(mapid) && !MapService.gI().isMapLang(mapid) && Util.isTrue(2, 10)) {
            dropGold(player, this, list, this.zone, x, yEnd);
        }

         // ------------------ DROP THỨC ĂN KHI MANG ĐỒ THẦN LINH ------------------
        if (MapService.gI().isMapCold(mapid) && Util.isTrue(1, 30)) {
            int countSet = 0;
            for (Item item : player.inventory.itemsBody) {
                if (item != null && item.isNotNullItem() && item.isDTL()) {
                    countSet++;
                }
            }
            if (countSet == 5) {
                int[] thucAnList = MobDrop.list_thuc_an;
                int randomThucAn = thucAnList[Util.nextInt(0, thucAnList.length - 1)];

                ItemMap itemMap = new ItemMap(
                        this.zone,
                        randomThucAn,
                        1,
                        x + Util.nextInt(-15, 15),
                        yEnd,
                        player.id
                );
                Service.getInstance().dropItemMap(this.zone, itemMap);
            }
        }
        
        if (MapService.gI().isMapLang(mapid)
                || MapService.gI().isMapUpSKH(mapid) || MapService.gI().isMapCold(mapid) || MapService.gI().isMapTuongLai(mapid)
                && Util.isTrue(1, 10)) { // tỉ lệ 10%

            if (Util.isTrue(1, 10)) {
                // Rơi ngẫu nhiên 1 trong 5 loại thức ăn
                int[] thucAnList = MobDrop.list_Da;
                int randomThucAn = thucAnList[Util.nextInt(0, thucAnList.length - 1)];

                ItemMap itemMap = new ItemMap(this.zone, randomThucAn, 1,
                        x + Util.nextInt(-15, 15), yEnd, player.id);
                //itemMap.options.add(new ItemOption(30, 0)); // không thể giao dịch

                Service.getInstance().dropItemMap(this.zone, itemMap);
            }
        }
        // ------------------ DROP ITEM KHI MẶC FULL SET HỦY DIỆT ------------------
        if (mapid >= 161 && mapid <= 163 && Util.isTrue(1, 100)) {
            int countSetHuyDiet = 0;

            for (Item item : player.inventory.itemsBody) {
                if (item != null && item.isNotNullItem() && item.isDHD()) {
                    countSetHuyDiet++;
                }
            }

            // Nếu mặc đủ 5 món Hủy Diệt
            if (countSetHuyDiet == 5) {
                int randomItem = Util.nextInt(1066, 1070);

                ItemMap itemMap = new ItemMap(
                        this.zone,
                        randomItem,
                        1,
                        x + Util.nextInt(-15, 15),
                        yEnd,
                        player.id
                );

                Service.getInstance().dropItemMap(this.zone, itemMap);
            }
        }

        //-------------------MAP HIRUDEGARN----------------------------
        if (MapService.gI().isMapHirudegarn(mapid)) {
//            if (Util.isTrue(5, 50)) {
//                if (Util.isTrue(1, 2)) {
//                    ItemMap it = ItemService.gI().randDoTLBoss(this.zone, 1, x + Util.nextInt(-35, 35), yEnd, player.id);
//                    if (it != null) {
//                        Service.gI().dropItemMap(zone, it);
//                    }
//                }
//            }
//            if (Util.isTrue(1, 2)) {
//                ItemMap it = new ItemMap(this.zone, 568, 1, x + Util.nextInt(-10, 10), yEnd, player.id);
//                Service.gI().dropItemMap(this.zone, it);
//            }
//            if (Util.isTrue(1, 2) && player.mabuEgg != null) {
//                ItemMap it = new ItemMap(this.zone, 1911, 1, x + Util.nextInt(-15, 15), yEnd, player.id);
//                it.addOptionParam(262, 0);
//                it.addOptionParam(30, 0);
//                Service.gI().dropItemMap(this.zone, it);
//            }
        }
        if (MapService.gI().isMapBinhHutNangLuong(mapid) && InventoryService.gI().findItemBinhHutNangLuong(player)) {
            for (Item item : player.inventory.itemsBag) {
                if (!item.isNotNullItem() || item.template.id != 1911) {
                    continue;
                }
                int param = item.getOptionParam(262);
                if (param >= 3000) {
                    continue;
                }
                int chance;
                if (param < 1000) {
                    chance = 3;
                } else if (param < 2000) {
                    chance = 6;
                } else if (param < 2900) {
                    chance = 9;
                } else {
                    chance = 15;
                }
                if (Util.isTrue(1, chance)) {
                    item.addOptionParam(262, 1);
                    if (item.getOptionParam(262) > 3000) {
                        item.getOptionParam(262, 3000);
                    }
                    InventoryService.gI().sendItemBag(player);
                }
            }
        }

        if (MapService.gI().isMapPotara(mapid)) {
            if (Util.getChanceFromLuck(tyLeMayMan, 1, 5, player.itemTime.isUseCoBonLa)) {
                ItemMap it = new ItemMap(this.zone, 1927, 1, x + Util.nextInt(-10, 10), yEnd, player.id);
                it.addOptionParam(31, 1);
                it.addOptionParam(30, 0);
                Service.gI().dropItemMap(this.zone, it);
            }
        }

        return list;
    }

    private static final int GOLD_MAX = 100_000_000;

    private void DropVangForBanDoKhoBau(Player pl, int LocationY) {
        Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.VANG);
        if (pl.clan == null || pl.clan.BanDoKhoBau.level <= 0) {
            return;
        }
        long baseGold = this.point.maxHp / 40;
        List<Long> goldChunks = new ArrayList<>();
        for (int j = 0; j < 8; j++) {
            double randFactor = 0.9 + (Math.random() * 0.2);
            long goldAmount = (long) (baseGold * randFactor);
            double playerBonus = (100.0 + pl.nPoint.tlGold) / 100.0;
            long totalGold = (long) (goldAmount * playerBonus);
            if (at != null && !at.isExpired()) {
                totalGold = (long) (totalGold * at.getValue() / 100.0);
            }
            if (totalGold <= 0) {
                totalGold = 1;
            }
            while (totalGold > 0) {
                long chunk = Math.min(GOLD_MAX, totalGold);
                goldChunks.add(chunk);
                totalGold -= chunk;
            }
        }
        int[] baseOffsets = new int[8];
        for (int j = 0; j < 8 && j < goldChunks.size(); j++) {
            baseOffsets[j] = (j - 5) * 15;
            int goldDrop = (int) Math.min(GOLD_MAX, goldChunks.get(j));
            dropGoldItem(baseOffsets[j], LocationY, goldDrop);
        }
        int extraCount = goldChunks.size() - 8;
        if (extraCount > 0) {
            int leftCount = extraCount / 2;
            int rightCount = extraCount - leftCount;
            int leftStart = baseOffsets[0] - 15;
            for (int j = 0; j < leftCount; j++) {
                int idx = 8 + j;
                int goldDrop = (int) Math.min(GOLD_MAX, goldChunks.get(idx));
                dropGoldItem(leftStart - (j * 15), LocationY, goldDrop);
            }
            int rightStart = baseOffsets[7] + 15;
            for (int j = 0; j < rightCount; j++) {
                int idx = 8 + leftCount + j;
                int goldDrop = (int) Math.min(GOLD_MAX, goldChunks.get(idx));
                dropGoldItem(rightStart + (j * 15), LocationY, goldDrop);
            }
        }
    }

    private void dropGoldItem(int offsetX, int offsetY, int goldAmount) {
        ItemMap item = new ItemMap(this.zone, 190, goldAmount, this.location.x + offsetX, offsetY, -1);
        Service.gI().dropItemMap(this.zone, item);
    }

    private void dropItemKichHoat(Player player, Zone zone, int mapid, int x, int yEnd, List<ItemMap> list) {
        int mayMan = 10;
        if (player.isActive()) {
            mayMan += 20;
        }
        if (player.itemTime != null && player.itemTime.isUseCoBonLa) {
            mayMan += 70;
        }
        if (player.nPoint != null && player.nPoint.tlMayman > 0) {
            mayMan += player.nPoint.tlMayman;
        }
        if (mayMan > 500) {
            mayMan = 500;
        }
        int fullRate, lowRate;
        if (MapService.gI().isMapUpSKH(mapid)) {
            fullRate = 30_000 * 3;
            lowRate = 15_000 * 3;
        } else if (MapService.gI().isMapRiengTu(mapid)) {
            fullRate = 35_000 * 3;
            lowRate = 18_000 * 3;
        } else {
            return;
        }
        if (Util.isTrue(mayMan, fullRate - powerDropActiveSet(player))) {
            list.add(createItemKichHoat(zone, player, x, yEnd, true));
        } else if (Util.isTrue(mayMan, lowRate - powerDropActiveSet(player))) {
            list.add(createItemKichHoat(zone, player, x, yEnd, false));
        }
    }

    private ItemMap createItemKichHoat(Zone zone, Player player, int x, int yEnd, boolean fullOption) {
        short itTemp = (short) ItemService.gI().randTempItemKichHoat(player.gender);
        ItemMap it = new ItemMap(zone, itTemp, 1, x, yEnd, player.id);
        List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itTemp);
        if (!ops.isEmpty()) {
            it.options = ops;
        }
        if (fullOption) {
            if (Util.isTrue(75, 100)) {
                int[] opsrand = ItemService.gI().randOptionItemKichHoat(player.gender);
                it.options.add(new ItemOption(opsrand[0], 0));
                it.options.add(new ItemOption(opsrand[1], 0));
                it.options.add(new ItemOption(30, 0));
            } else {
                int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(player.gender);
                it.options.add(new ItemOption(opsrand[0], 0));
                it.options.add(new ItemOption(opsrand[1], 0));
                it.options.add(new ItemOption(opsrand[2], 0));
                it.options.add(new ItemOption(opsrand[3], 0));
                it.options.add(new ItemOption(30, 0));
            }
        }
        if (Util.isTrue(10, 100)) {
            it.options.add(new ItemOption(107, 1));
        } else if (Util.isTrue(8, 100)) {
            it.options.add(new ItemOption(107, 2));
        } else if (Util.isTrue(5, 100)) {
            it.options.add(new ItemOption(107, 3));
        } else if (Util.isTrue(1, 100)) {
            it.options.add(new ItemOption(107, 4));
        }
        //Trai Dat
        randomizeOption(it, 47, 7, 9, 3);
        randomizeOption(it, 6, 28, 32, 6);
        randomizeOption(it, 6, 147, 153, 35);
        randomizeOption(it, 0, 13, 15, 37);
        randomizeOption(it, 7, 23, 27, 30);
        randomizeOption(it, 7, 117, 123, 39);
        //Namec
        randomizeOption(it, 47, 7, 9, 4);
        randomizeOption(it, 6, 23, 27, 43);
        randomizeOption(it, 6, 117, 123, 10);
        randomizeOption(it, 0, 11, 13, 25);
        randomizeOption(it, 7, 28, 32, 47);
        randomizeOption(it, 7, 147, 153, 31);
        //Xayda
        randomizeOption(it, 47, 9, 11, 5);
        randomizeOption(it, 6, 19, 21, 51);
        randomizeOption(it, 6, 97, 103, 11);
        randomizeOption(it, 0, 15, 17, 26);
        randomizeOption(it, 7, 19, 21, 55);
        randomizeOption(it, 7, 97, 103, 32);
        return it;
    }

    private void randomizeOption(ItemMap it, int optionId, int min, int max, int iditem) {
        if (it.itemTemplate.id == iditem) {
            it.getOptionParam(optionId, Util.nextInt(min, max));
        }
    }

    private int powerDropActiveSet(Player player) {
        if (player != null && player.nPoint != null) {
            long Power = player.nPoint.power;
            if (Power < 2000) {
                return 5_000;
            } else if (Power >= 2000 && Power < 15_000) {
                return 4_700;
            } else if (Power >= 15_000 && Power < 150_000) {
                return 4_200;
            } else if (Power >= 150_000 && Power < 500_000) {
                return 3_800;
            } else if (Power >= 500_000 && Power < 1_500_000) {
                return 3_500;
            } else if (Power >= 1_500_000 && Power < 5_000_000) {
                return 3_200;
            } else if (Power >= 5_000_000 && Power < 15_000_000) {
                return 2_900;
            } else if (Power >= 15_000_000 && Power < 50_000_000) {
                return 2_500;
            } else if (Power >= 50_000_000 && Power < 100_000_000) {
                return 2_000;
            } else if (Power >= 100_000_000 && Power < 200_000_000) {
                return 1_500;
            } else if (Power >= 200_000_000 && Power < 500_000_000) {
                return 1_000;
            } else if (Power >= 500_000_000 && Power < 1_000_000_000) {
                return 500;
            } else if (Power >= 1_000_000_000 && Power < 2_000_000_000) {
                return 100;
            } else if (Power >= 2_000_000_000 && Power < 5_000_000_000L) {
                return 0;
            } else if (Power >= 5_000_000_000L && Power < 10_000_000_000L) {
                return -500;
            } else if (Power >= 10_000_000_000L && Power < 20_000_000_000L) {
                return -1_000;
            } else if (Power >= 20_000_000_000L && Power < 40_000_000_000L) {
                return -2_000;
            } else if (Power >= 40_000_000_000L && Power < 80_000_000_000L) {
                return -5_000;
            } else if (Power >= 80_000_000_000L) {
                return -10_000;
            }
        }
        return 0;
    }
//------------------------------------------------------------------------------

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(zone, 73, 1, location.x, location.y, player.id);
                }
                break;
            case ConstMob.THAN_LAN_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (Util.isTrue(1, 5)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con thằn lằn mẹ này không giữ ngọc, hãy tìm con thằn lằn mẹ khác");
                    }
                }
            case ConstMob.PHI_LONG_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (Util.isTrue(1, 5)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con phi long mẹ này không giữ ngọc, hãy tìm con phi long mẹ khác");
                    }
                }
            case ConstMob.QUY_BAY_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_8_1) {
                    if (Util.isTrue(1, 5)) {
                        itemMap = new ItemMap(zone, 20, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con quỷ bay mẹ này không giữ ngọc, hãy tìm con quỷ bay mẹ khác");
                    }
                }
            case ConstMob.OC_MUON_HON:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (Util.isTrue(1, 4)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con ốc mượn hồn này không giữ truyện tranh, hãy thử tìm con ốc mượn hồn khác");
                    }
                }
            case ConstMob.HEO_XAYDA_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (Util.isTrue(1, 4)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con heo xayda mẹ này không giữ truyện tranh, hãy thử tìm con heo xayda mẹ khác");
                    }
                }
            case ConstMob.OC_SEN:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_14_1) {
                    if (Util.isTrue(1, 4)) {
                        itemMap = new ItemMap(zone, 85, 1, location.x, location.y, player.id);
                    } else {
                        Service.gI().sendThongBao(player, "Con ốc xên này không giữ truyện tranh, hãy thử tìm con ốc xên khác");
                    }
                }
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(double dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(this.point.gethp()), Manager.readInt);
            msg.writeCris(Util.CrisGH(dameHit), Manager.readInt);
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void hoiSinhMobPhoBan() {
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); //level mob
            msg.writeCris(Util.CrisGH(this.point.hp), Manager.readInt);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void hoiSinhMobTayKarin() {
        this.point.hp = this.point.maxHp;
        this.maxTiemNang = 1;
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(this.lvMob); //level mob
            msg.writeCris(Util.CrisGH(this.point.hp), Manager.readInt);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendSieuQuai(int type) {
        Message msg;
        try {
            msg = new Message(-75);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(type);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendDisable(boolean bool) {
        Message msg;
        try {
            msg = new Message(81);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendDoneMove(boolean bool) {
        Message msg;
        try {
            msg = new Message(82);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendFire(boolean bool) {
        Message msg;
        try {
            msg = new Message(85);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendIce(boolean bool) {
        Message msg;
        try {
            msg = new Message(86);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendWind(boolean bool) {
        Message msg;
        try {
            msg = new Message(87);
            msg.writer().writeByte(this.id);
            msg.writer().writeBoolean(bool);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    private void phanSatThuong(Player plTarget, double dame) {
        if (plTarget.nPoint == null) {
            return;
        }
        int percentPST = plTarget.nPoint.tlPST;
        if (percentPST != 0) {
            double damePST = Util.CrisGH(dame * percentPST / 100L);
            Message msg;
            try {
                msg = new Message(-9);
                msg.writer().writeByte(this.id);
                if (damePST >= this.point.hp) {
                    damePST = this.point.hp - 1;
                }
                long hpMob = Util.CrisGH(this.point.hp);
                injured(null, damePST, true);
                damePST = hpMob - this.point.hp;
                msg.writeCris(Util.CrisGH(this.point.hp), Manager.readInt);
                msg.writeCris(Util.CrisGH(damePST), Manager.readInt);
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(Mob.class, e);
            }
        }
    }

    public void startDie() {
        Message msg;
        try {
            setDie();
            this.point.hp = -1;
            this.status = 0;
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
    }

    public void sendMobDieAfterMobMeAttackedPet(Player plKill, double dameHit) {
        this.status = 0;
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writeCris(Util.CrisGH(dameHit), Manager.readInt);
            msg.writer().writeBoolean(false); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
            Logger.logException(Mob.class, e);
        }
        this.lastTimeDie = System.currentTimeMillis();
    }
}
