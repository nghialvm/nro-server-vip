package QuanLiBoss.Manager;

/*
 * @Author: MaiTienDung
 */

import Boss.list.Gomah.Gomah;
import Boss.list.Tramhuydiet.Berus;
import Boss.list.nro.boss.list.Chilled.Chilled;
import Boss.list.nro.boss.list.Cooler.Cooler;
import Boss.list.nro.boss.list.Frost.Frost;
import Boss.nro.boss.task.BlackGoku.BlackGoku;
import Boss.nro.boss.task.BlackGoku.ZamasKaio;
import Boss.nro.boss.task.BlackGoku.ZamasMax;
import Boss.list.nro.list.boss.Cumber.Cumber;
import Boss.list.nro.list.boss.Cumber.Cumber2;
import Boss.list.nro.list.boss.Cumber.Cumber3;

import QuanLiBoss.Boss;
import QuanLiBoss.BossFunction.TestBoss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.TypeEventBoss;

import Utils.Functions;
import Utils.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import network.io.Message;

import nro.boss.event.Halloween.BiNgo;
import nro.boss.event.ChristmasEvent.BrolyNoel;
import nro.boss.event.ChristmasEvent.ChiChiNoel;
import nro.boss.event.ChristmasEvent.ColdChristmas;
import nro.boss.event.ChristmasEvent.GokuGodNoel;
import nro.boss.event.ChristmasEvent.GokuNoel;
import nro.boss.event.ChristmasEvent.OngGiaNoel;
import nro.boss.event.ChristmasEvent.TuanLoc;
import nro.boss.event.Halloween.Doi;
import nro.boss.event.Halloween.MaTroi;
import nro.boss.event.Halloween.XuongKho;
import nro.boss.event.HungVuongEvent.RongNhi1Sao;
import nro.boss.event.HungVuongEvent.RongNhi2Sao;
import nro.boss.event.HungVuongEvent.RongNhi3Sao;
import nro.boss.event.HungVuongEvent.RongNhi4Sao;
import nro.boss.event.HungVuongEvent.RongNhi5Sao;
import nro.boss.event.HungVuongEvent.RongNhi6Sao;
import nro.boss.event.HungVuongEvent.RongNhi7Sao;
import nro.boss.event.HungVuongEvent.SonTinh;
import nro.boss.event.HungVuongEvent.SonTinhNew;
import nro.boss.event.HungVuongEvent.ThuyTinh;
import nro.boss.event.HungVuongEvent.ThuyTinhNew;
import nro.boss.event.LunarNewYearEvent.BeNa;
import nro.boss.event.LunarNewYearEvent.LanCon;
import nro.boss.event.LunarNewYearEvent.MeoDen;
import nro.boss.event.LunarNewYearEvent.NewYearDragon;
import nro.boss.event.LunarNewYearEvent.PiLong;
import nro.boss.event.LunarNewYearEvent.ThanTai;
import nro.boss.event.TrungThuEvent.Gogeta;
import nro.boss.event.TrungThuEvent.NguyetThan;
import nro.boss.event.TrungThuEvent.NhatThan;
import nro.boss.event.TrungThuEvent.Omega;
import nro.boss.event.TrungThuEvent.ThoDaiKa;
import nro.boss.event.ValentineEvent.ThoBunma;
import nro.boss.event.VuLanFestival.Pikkon;

import nro.boss.list.Broly.Broly;
import nro.boss.list.Broly.BrolyZone0;
import nro.boss.list.Broly.SuperBrolyNew;
import nro.boss.list.Earth.Bido;
import nro.boss.list.Earth.Bojack;
import nro.boss.list.Earth.Bujin;
import nro.boss.list.Earth.Kogu;
import nro.boss.list.Earth.SuperBojack;
import nro.boss.list.Earth.Zangya;
import nro.boss.list.GoldenFrieza.DeathBeam1;
import nro.boss.list.GoldenFrieza.DeathBeam2;
import nro.boss.list.GoldenFrieza.DeathBeam3;
import nro.boss.list.GoldenFrieza.DeathBeam4;
import nro.boss.list.GoldenFrieza.DeathBeam5;
import nro.boss.list.GoldenFrieza.GoldenFrieza;
import nro.boss.list.PilafGang.Mai;
import nro.boss.list.PilafGang.Pilap;
import nro.boss.list.PilafGang.Shu;

import nro.boss.map.BossNomal.AnTrom;
import nro.boss.map.BossNomal.ODo;
import nro.boss.map.BossNomal.RaiTi;
import nro.boss.map.BossNomal.SoiHecQuyn;
import nro.boss.map.BossNomal.Virus;
import nro.boss.map.BossNomal.XinBaTo;
import nro.boss.map.MajinBuu12H.BuiBui;
import nro.boss.map.MajinBuu12H.BuiBui2;
import nro.boss.map.MajinBuu12H.Cadic;
import nro.boss.map.MajinBuu12H.Drabura;
import nro.boss.map.MajinBuu12H.Drabura2;
import nro.boss.map.MajinBuu12H.Drabura3;
import nro.boss.map.MajinBuu12H.Goku;
import nro.boss.map.MajinBuu12H.Mabu;
import nro.boss.map.MajinBuu12H.Yacon;
import nro.boss.map.MajinBuu14H.MaBu2H;
import nro.boss.map.MajinBuu14H.SuperBu;
import nro.boss.map.TaoPaiPai.TaoPaiPai;
import nro.boss.map.Yardart.ChienBinh0;
import nro.boss.map.Yardart.ChienBinh1;
import nro.boss.map.Yardart.ChienBinh2;
import nro.boss.map.Yardart.ChienBinh3;
import nro.boss.map.Yardart.ChienBinh4;
import nro.boss.map.Yardart.ChienBinh5;
import nro.boss.map.Yardart.DoiTruong5;
import nro.boss.map.Yardart.TanBinh0;
import nro.boss.map.Yardart.TanBinh1;
import nro.boss.map.Yardart.TanBinh2;
import nro.boss.map.Yardart.TanBinh3;
import nro.boss.map.Yardart.TanBinh4;
import nro.boss.map.Yardart.TanBinh5;
import nro.boss.map.Yardart.TapSu0;
import nro.boss.map.Yardart.TapSu1;
import nro.boss.map.Yardart.TapSu2;
import nro.boss.map.Yardart.TapSu3;
import nro.boss.map.Yardart.TapSu4;

import nro.boss.task.Frieza.Fide;
import nro.boss.task.FutureCell.SieuBoHung;
import nro.boss.task.FutureCell.XenCon1;
import nro.boss.task.FutureCell.XenCon2;
import nro.boss.task.FutureCell.XenCon3;
import nro.boss.task.FutureCell.XenCon4;
import nro.boss.task.FutureCell.XenCon5;
import nro.boss.task.FutureCell.XenCon6;
import nro.boss.task.FutureCell.XenCon7;
import nro.boss.task.GinyuForce.So1;
import nro.boss.task.GinyuForce.So2;
import nro.boss.task.GinyuForce.So3;
import nro.boss.task.GinyuForce.So4;
import nro.boss.task.GinyuForce.TieuDoiTruong;
import nro.boss.task.GinyuForceNamek.So1Namek;
import nro.boss.task.GinyuForceNamek.So2Namek;
import nro.boss.task.GinyuForceNamek.So3Namek;
import nro.boss.task.GinyuForceNamek.So4Namek;
import nro.boss.task.GinyuForceNamek.TieuDoiTruongNamek;
import nro.boss.task.Napa.Kuku;
import nro.boss.task.Napa.MapDauDinh;
import nro.boss.task.Napa.Rambo;
import nro.boss.task.PresentCell.XenBoHung;
import nro.boss.task.RobotAssasinOne.Android19;
import nro.boss.task.RobotAssasinOne.DrKore;
import nro.boss.task.RobotAssasinThree.KingKong;
import nro.boss.task.RobotAssasinThree.Pic;
import nro.boss.task.RobotAssasinThree.Poc;
import nro.boss.task.RobotAssasinTwo.Android13;
import nro.boss.task.RobotAssasinTwo.Android14;
import nro.boss.task.RobotAssasinTwo.Android15;

import nro.map.Zone;
import nro.player.Player;
import nro.server.Maintenance;
import nro.services.MapService;
import nro.services.Fun.ChangeMapService;

public class BossManager implements Runnable {

    private static BossManager instance;
    public static byte ratioReward = 10;

    protected final List<Boss> bosses;
    private final Object runtimeLock = new Object();

    public static BossManager gI() {
        if (instance == null) {
            instance = new BossManager();
        }
        return instance;
    }

    public BossManager() {
        this.bosses = new ArrayList<>();
    }

    public void addBoss(Boss boss) {
        synchronized (runtimeLock) {
            this.bosses.add(boss);
        }
    }

    public void removeBoss(Boss boss) {
        synchronized (runtimeLock) {
            this.bosses.remove(boss);
        }
    }

    public List<Boss> getBosses() {
        return this.bosses;
    }

    /**
     * Returns a stable view for readers that run outside the boss loop.
     * The legacy getBosses() method is intentionally kept for game code that
     * expects the original mutable list.
     */
    public List<Boss> snapshotBosses() {
        synchronized (runtimeLock) {
            return new ArrayList<>(this.bosses);
        }
    }

    public record SpawnZone(int zoneId, int players, int bosses, boolean available, String status) {
    }

    public record SpawnMap(int mapId, String mapName, List<SpawnZone> zones) {
    }

    public record SpawnBoss(long bossId, String name, List<SpawnMap> maps,
            int totalInstances, int aliveInstances, int restingInstances, int deadInstances) {
    }

    public static final class BossSpawnException extends RuntimeException {

        public final int status;
        public final String code;

        public BossSpawnException(int status, String code, String message) {
            super(message);
            this.status = status;
            this.code = code;
        }

        public BossSpawnException(int status, String code, String message, Throwable cause) {
            super(message, cause);
            this.status = status;
            this.code = code;
        }
    }

    /**
     * Builds the catalog from the boss instances currently registered in the
     * runtime. Maps that are configured but not loaded are deliberately
     * omitted because they cannot be used by the admin action.
     */
    public List<SpawnBoss> getSpawnOptions() {
        synchronized (runtimeLock) {
            Map<Long, Boss> representatives = new LinkedHashMap<>();
            Map<Long, int[]> counts = new LinkedHashMap<>();

            for (Boss boss : this.bosses) {
                if (boss == null || boss.data == null || boss.data.length == 0) {
                    continue;
                }
                representatives.putIfAbsent(boss.id, boss);
                int[] status = counts.computeIfAbsent(boss.id, ignored -> new int[4]);
                status[0]++;
                if (isLivingBoss(boss)) {
                    status[1]++;
                } else if (boss.zone == null) {
                    status[2]++;
                } else if (isDeadBoss(boss)) {
                    status[3]++;
                } else {
                    status[2]++;
                }
            }

            List<SpawnBoss> result = new ArrayList<>();
            for (Map.Entry<Long, Boss> entry : representatives.entrySet()) {
                Boss representative = entry.getValue();
                Set<Integer> configuredMaps = configuredMapIds(representative);
                List<SpawnMap> maps = new ArrayList<>();

                for (Integer mapId : configuredMaps) {
                    nro.map.Map map = MapService.gI().getMapById(mapId);
                    if (map == null || map.zones == null || map.zones.isEmpty()) {
                        continue;
                    }

                    List<SpawnZone> zones = new ArrayList<>();
                    for (int zoneIndex = 0; zoneIndex < map.zones.size(); zoneIndex++) {
                        Zone zone = map.zones.get(zoneIndex);
                        if (zone == null) {
                            continue;
                        }
                        int livingBosses = countLivingBosses(zone);
                        boolean allowed = isSpawnZoneAllowed(representative, map.zones.size(), zoneIndex);
                        boolean available = allowed && livingBosses == 0;
                        String status = !allowed ? "RESTRICTED" : livingBosses > 0 ? "OCCUPIED" : "AVAILABLE";
                        zones.add(new SpawnZone(zoneIndex, zone.getNumOfPlayers(), livingBosses, available, status));
                    }
                    if (!zones.isEmpty()) {
                        maps.add(new SpawnMap(map.mapId,
                                map.mapName == null ? "Map " + map.mapId : map.mapName,
                                zones));
                    }
                }

                int[] status = counts.get(entry.getKey());
                String name = representative.data[0].getName();
                result.add(new SpawnBoss(entry.getKey(), name == null ? "Boss " + entry.getKey() : name,
                        maps, status[0], status[1], status[2], status[3]));
            }
            return result;
        }
    }

    /**
     * Creates a new boss and places it immediately in the requested runtime
     * zone. The same lock is held by the boss loop, so validation and list/map
     * insertion are one serialized operation.
     */
    public Boss spawnBossAt(int bossId, int mapId, int zoneId) {
        synchronized (runtimeLock) {
            Boss representative = findSpawnRepresentative(bossId);
            if (representative == null) {
                throw new BossSpawnException(404, "BOSS_NOT_FOUND", "Boss không tồn tại trong runtime");
            }
            if (!configuredMapIds(representative).contains(mapId)) {
                throw new BossSpawnException(400, "BOSS_MAP_NOT_ALLOWED", "Map không thuộc cấu hình của boss");
            }

            nro.map.Map map = MapService.gI().getMapById(mapId);
            if (map == null || map.zones == null || map.zones.isEmpty()) {
                throw new BossSpawnException(404, "MAP_NOT_LOADED", "Map chưa được load trong runtime");
            }
            Zone targetZone = map.getZoneByIndex(zoneId);
            if (targetZone == null) {
                throw new BossSpawnException(400, "BOSS_ZONE_NOT_FOUND", "Khu không tồn tại trong map");
            }
            if (!isSpawnZoneAllowed(representative, map.zones.size(), zoneId)) {
                throw new BossSpawnException(400, "BOSS_ZONE_NOT_ALLOWED", "Khu không nằm trong giới hạn spawn của boss");
            }
            if (countLivingBosses(targetZone) > 0) {
                throw new BossSpawnException(409, "BOSS_ZONE_OCCUPIED", "Khu đã có boss đang sống");
            }

            List<Boss> existingBosses = new ArrayList<>(this.bosses);
            Boss boss = createBoss(bossId);
            if (boss == null) {
                cleanupCreatedBosses(existingBosses);
                throw new BossSpawnException(400, "BOSS_FACTORY_NOT_FOUND", "Boss không có factory hỗ trợ");
            }

            boss.zoneFinal = targetZone;
            try {
                boss.currentLevel = -1;
                boss.respawn();
                boss.joinMap();
                if (boss.zone != targetZone || !targetZone.getBosses().contains(boss) || !isLivingBoss(boss)) {
                    throw new BossSpawnException(500, "BOSS_SPAWN_FAILED", "Không thể đưa boss vào đúng map/khu");
                }
                return boss;
            } catch (BossSpawnException exception) {
                cleanupCreatedBosses(existingBosses);
                throw exception;
            } catch (Exception exception) {
                cleanupCreatedBosses(existingBosses);
                throw new BossSpawnException(500, "BOSS_SPAWN_FAILED", "Không thể spawn boss trong runtime", exception);
            }
        }
    }

    public static boolean isSpawnZoneAllowed(int zoneId, int zoneCount,
            boolean isZoneRandomSpawn, boolean isZone02Spawn) {
        if (zoneId < 0 || zoneId >= zoneCount) {
            return false;
        }
        if (isZone02Spawn) {
            return zoneId >= 2;
        }
        if (zoneCount <= 1) {
            return true;
        }
        // The normal boss lifecycle starts at zone 1 when a map has multiple
        // zones. Random-spawn bosses use the same lower bound.
        return zoneId >= 1;
    }

    private static boolean isSpawnZoneAllowed(Boss boss, int zoneCount, int zoneId) {
        if (boss != null && boss.isSpawnPlayer) {
            return zoneId >= 0 && zoneId < zoneCount;
        }
        return isSpawnZoneAllowed(zoneId, zoneCount, boss != null && boss.isZoneRandomSpawn,
                boss != null && boss.isZone02Spawn);
    }

    private Boss findSpawnRepresentative(int bossId) {
        for (Boss boss : this.bosses) {
            if (boss != null && boss.id == bossId && boss.data != null && boss.data.length > 0) {
                return boss;
            }
        }
        return null;
    }

    private static Set<Integer> configuredMapIds(Boss boss) {
        Set<Integer> mapIds = new LinkedHashSet<>();
        if (boss != null && boss.data != null && boss.data.length > 0 && boss.data[0].getMapJoin() != null) {
            for (int mapId : boss.data[0].getMapJoin()) {
                mapIds.add(mapId);
            }
        }
        return mapIds;
    }

    private static int countLivingBosses(Zone zone) {
        int count = 0;
        if (zone == null || zone.getBosses() == null) {
            return count;
        }
        for (Player player : zone.getBosses()) {
            if (player instanceof Boss boss && isLivingBoss(boss)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isLivingBoss(Boss boss) {
        try {
            return boss != null && boss.zone != null && !boss.isDie();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private static boolean isDeadBoss(Boss boss) {
        try {
            return boss != null && boss.isDie();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private void cleanupCreatedBosses(List<Boss> existingBosses) {
        List<Boss> created = new ArrayList<>();
        for (Boss boss : this.bosses) {
            if (!existingBosses.contains(boss)) {
                created.add(boss);
            }
        }
        for (Boss boss : created) {
            try {
                if (boss.zone != null) {
                    ChangeMapService.gI().exitMap(boss);
                }
            } catch (Exception ignored) {
            }
            this.bosses.remove(boss);
            try {
                boss.dispose();
            } catch (Exception ignored) {
            }
        }
    }

    public void loadBoss() {
        this.createBoss(BossID.TIEU_DOI_TRUONG);
        this.createBoss(BossID.KING_KONG);
        this.createBoss(BossID.XEN_BO_HUNG);
        this.createBoss(BossID.SIEU_BO_HUNG);
        this.createBoss(BossID.KUKU);
        this.createBoss(BossID.MAP_DAU_DINH);
        this.createBoss(BossID.RAMBO);
        this.createBoss(BossID.FIDE);
        this.createBoss(BossID.ANDROID_14);
        this.createBoss(BossID.DR_KORE);
        this.createBoss(BossID.TAU_PAY_PAY_DONG_NAM_KARIN);
        this.createBoss(BossID.BOJACK);
        this.createBoss(BossID.SUPER_BOJACK);
        this.createBoss(BossID.GOLDEN_FRIEZA);
        this.createBoss(BossID.PI_LAP);
        this.createBoss(BossID.TIEU_DOI_TRUONG_NAMEK);
        this.createBoss(BossID.BLACK_GOKU, 3);
        this.createBoss(BossID.COOLER);
        this.createBoss(BossID.FROST);
        this.createBoss(BossID.CUMBER);
        this.createBoss(BossID.CHILER);
        this.createBoss(BossID.ZAMASZIN);
        this.createBoss(BossID.ZAMASMAX);
        this.createBoss(BossID.DRABULA2);
        this.createBoss(BossID.BERUS);
        this.createBoss(BossID.WHIS_TWO);
        this.createBoss(BossID.SUPER_BROLY_NEW, 5);
        this.createBoss(BossID.GOMAH);

        // this.createBoss(BossID.DRABULA3);
        for (int i = 0; i < 100; i++) {
            this.createBoss(BossID.SOI_HEC_QUYN_NOMAL);
            this.createBoss(BossID.O_DO_NOMAL);
            this.createBoss(BossID.VIRUS_NOMAL);
            this.createBoss(BossID.XIN_BA_TO_NOMAL);
        }
    }

    public void createBoss(int bossID, int total) {
        for (int i = 0; i < total; i++) {
            createBoss(bossID);
        }
    }

    public Boss createBoss(int bossID) {
        try {
            switch (bossID) {
                case BossID.TAP_SU_0:
                    return new TapSu0();
                case BossID.TAP_SU_1:
                    return new TapSu1();
                case BossID.TAP_SU_2:
                    return new TapSu2();
                case BossID.TAP_SU_3:
                    return new TapSu3();
                case BossID.TAP_SU_4:
                    return new TapSu4();
                case BossID.TAN_BINH_5:
                    return new TanBinh5();
                case BossID.TAN_BINH_0:
                    return new TanBinh0();
                case BossID.TAN_BINH_1:
                    return new TanBinh1();
                case BossID.TAN_BINH_2:
                    return new TanBinh2();
                case BossID.TAN_BINH_3:
                    return new TanBinh3();
                case BossID.TAN_BINH_4:
                    return new TanBinh4();
                case BossID.CHIEN_BINH_5:
                    return new ChienBinh5();
                case BossID.CHIEN_BINH_0:
                    return new ChienBinh0();
                case BossID.CHIEN_BINH_1:
                    return new ChienBinh1();
                case BossID.CHIEN_BINH_2:
                    return new ChienBinh2();
                case BossID.CHIEN_BINH_3:
                    return new ChienBinh3();
                case BossID.CHIEN_BINH_4:
                    return new ChienBinh4();
                case BossID.DOI_TRUONG_5:
                    return new DoiTruong5();
                case BossID.DRABURA:
                    return new Drabura();
                case BossID.BUI_BUI:
                    return new BuiBui();
                case BossID.BUI_BUI_2:
                    return new BuiBui2();
                case BossID.YA_CON:
                    return new Yacon();
                case BossID.DRABURA_2:
                    return new Drabura2();
                case BossID.GOKU:
                    return new Goku();
                case BossID.CADIC:
                    return new Cadic();
                case BossID.MABU_12H:
                    return new Mabu();
                case BossID.DRABURA_3:
                    return new Drabura3();
                case BossID.MABU:
                    return new MaBu2H();
                case BossID.SUPERBU:
                    return new SuperBu();
                case BossID.SO_4:
                    return new So4();
                case BossID.SO_3:
                    return new So3();
                case BossID.SO_2:
                    return new So2();
                case BossID.SO_1:
                    return new So1();
                case BossID.TIEU_DOI_TRUONG:
                    return new TieuDoiTruong();
                case BossID.KUKU:
                    return new Kuku();
                case BossID.MAP_DAU_DINH:
                    return new MapDauDinh();
                case BossID.RAMBO:
                    return new Rambo();
                case BossID.FIDE:
                    return new Fide();
                case BossID.DR_KORE:
                    return new DrKore();
                case BossID.ANDROID_19:
                    return new Android19();
                case BossID.ANDROID_13:
                    return new Android13();
                case BossID.ANDROID_14:
                    return new Android14();
                case BossID.ANDROID_15:
                    return new Android15();
                case BossID.PIC:
                    return new Pic();
                case BossID.POC:
                    return new Poc();
                case BossID.KING_KONG:
                    return new KingKong();
                case BossID.XEN_BO_HUNG:
                    return new XenBoHung();
                case BossID.SIEU_BO_HUNG:
                    return new SieuBoHung();
                case BossID.XEN_CON_1:
                    return new XenCon1();
                case BossID.XEN_CON_2:
                    return new XenCon2();
                case BossID.XEN_CON_3:
                    return new XenCon3();
                case BossID.XEN_CON_4:
                    return new XenCon4();
                case BossID.XEN_CON_5:
                    return new XenCon5();
                case BossID.XEN_CON_6:
                    return new XenCon6();
                case BossID.XEN_CON_7:
                    return new XenCon7();
                case BossID.GOLDEN_FRIEZA:
                    return new GoldenFrieza();
                case BossID.DEATH_BEAM_1:
                    return new DeathBeam1();
                case BossID.DEATH_BEAM_2:
                    return new DeathBeam2();
                case BossID.DEATH_BEAM_3:
                    return new DeathBeam3();
                case BossID.DEATH_BEAM_4:
                    return new DeathBeam4();
                case BossID.DEATH_BEAM_5:
                    return new DeathBeam5();
                case BossID.ONG_GIA_NOEL:
                    return new OngGiaNoel();
                case BossID.BUJIN:
                    return new Bujin();
                case BossID.KOGU:
                    return new Kogu();
                case BossID.ZANGYA:
                    return new Zangya();
                case BossID.BIDO:
                    return new Bido();
                case BossID.BOJACK:
                    return new Bojack();
                case BossID.SUPER_BOJACK:
                    return new SuperBojack();
                case BossID.TAU_PAY_PAY_DONG_NAM_KARIN:
                    return new TaoPaiPai();
                case BossID.SON_TINH:
                    return new SonTinh();
                case BossID.THUY_TINH:
                    return new ThuyTinh();
                case BossID.SON_TINH_NEW:
                    return new SonTinhNew();
                case BossID.THUY_TINH_NEW:
                    return new ThuyTinhNew();
                case BossID.SO_4_NAMEK:
                    return new So4Namek();
                case BossID.SO_3_NAMEK:
                    return new So3Namek();
                case BossID.SO_2_NAMEK:
                    return new So2Namek();
                case BossID.SO_1_NAMEK:
                    return new So1Namek();
                case BossID.TIEU_DOI_TRUONG_NAMEK:
                    return new TieuDoiTruongNamek();
                case BossID.PI_LAP:
                    return new Pilap();
                case BossID.MAI:
                    return new Mai();
                case BossID.SHU:
                    return new Shu();
                case BossID.MEO_DEN:
                    return new MeoDen();
                case BossID.PI_LONG:
                    return new PiLong();
                case BossID.BE_NA:
                    return new BeNa();
                case BossID.LAN_CON:
                    return new LanCon();
                case BossID.THAN_TAI:
                    return new ThanTai();
                case BossID.NEW_YEAR_DRAGON:
                    return new NewYearDragon();
                case BossID.GOKU_NOEL:
                    return new GokuNoel();
                case BossID.CHICHI_NOEL:
                    return new ChiChiNoel();
                case BossID.COLD_NOEL:
                    return new ColdChristmas();
                case BossID.SOI_HEC_QUYN_NOMAL:
                    return new SoiHecQuyn();
                case BossID.O_DO_NOMAL:
                    return new ODo();
                case BossID.AN_TROM_NOMAL:
                    return new AnTrom();
                case BossID.RAI_TI_NOMAL:
                    return new RaiTi();
                case BossID.XIN_BA_TO_NOMAL:
                    return new XinBaTo();
                case BossID.VIRUS_NOMAL:
                    return new Virus();
                case BossID.GOKU_GOD_NOEL:
                    return new GokuGodNoel();
                case BossID.BROLY_NOEL:
                    return new BrolyNoel();
                case BossID.TUAN_LOC:
                    return new TuanLoc();
                case BossID.PIKKON:
                    return new Pikkon();
                case BossID.DOI:
                    return new Doi();
                case BossID.MATROI:
                    return new MaTroi();
                case BossID.XUONG_KHO:
                    return new XuongKho();
                case BossID.BI_NGO:
                    return new BiNgo();
                case BossID.THO_DAI_KA:
                    return new ThoDaiKa();
                case BossID.NHATTHAN:
                    return new NhatThan();
                case BossID.NGUYETTHAN:
                    return new NguyetThan();
                case BossID.OMEGA:
                    return new Omega();
                case BossID.GOGETA:
                    return new Gogeta();
                case BossID.RONG_NHI_1S:
                    return new RongNhi1Sao();
                case BossID.RONG_NHI_2S:
                    return new RongNhi2Sao();
                case BossID.RONG_NHI_3S:
                    return new RongNhi3Sao();
                case BossID.RONG_NHI_4S:
                    return new RongNhi4Sao();
                case BossID.RONG_NHI_5S:
                    return new RongNhi5Sao();
                case BossID.RONG_NHI_6S:
                    return new RongNhi6Sao();
                case BossID.RONG_NHI_7S:
                    return new RongNhi7Sao();
                case BossID.THO_BUNMA:
                    return new ThoBunma();
                case BossID.BLACK_GOKU:
                    return new BlackGoku();
                case BossID.COOLER:
                    return new Cooler();
                case BossID.FROST:
                    return new Frost();
                case BossID.CUMBER:
                    return new Cumber();
                case BossID.CHILER:
                    return new Chilled();
                case BossID.ZAMASMAX:
                    return new ZamasMax();
                case BossID.ZAMASZIN:
                    return new ZamasKaio();
                case BossID.BROLY:
                    return new Broly();
                case BossID.BROLY_ZONE_0:
                    return new BrolyZone0();
                case BossID.DRABULA2:
                    return new Cumber2();
                case BossID.DRABULA3:
                    return new Cumber3();
                case BossID.BERUS:
                    return new Berus();
                case BossID.SUPER_BROLY_NEW:
                    return new SuperBrolyNew();
                case BossID.GOMAH:
                    return new Gomah();
                default:
                    return null;
            }
        } catch (Exception e) {
            Logger.error(e + "\n");
            return null;
        }
    }

    public Boss getBoss(int id) {
        try {
            Boss boss = this.bosses.get(id);
            if (boss != null) {
                return boss;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private boolean isBossNoNotify(Boss boss) {
        try {
            if (boss == null || boss.data == null || boss.data.length == 0 || boss.data[0] == null) {
                return true;
            }

            if (boss.data[0].getMapJoin() == null || boss.data[0].getMapJoin().length == 0) {
                return true;
            }

            return MapService.gI().isMapNoNottify(boss.data[0].getMapJoin()[0]);
        } catch (Exception e) {
            return true;
        }
    }

    private boolean needBossTimer(Boss boss) {
        if (boss == null) {
            return false;
        }

        return boss.zone == null
                || boss.isDie()
                || boss.bossStatus == BossStatus.DIE
                || boss.bossStatus == BossStatus.REST
                || boss.bossStatus == BossStatus.RESPAWN
                || boss.bossStatus == BossStatus.AFK;
    }

   private long getBossRemainingTime(Boss boss) {
    try {
        if (boss == null || boss.getLastTimeRest() <= 0 || boss.getSecondsRest() <= 0) {
            return 0L;
        }

        long remaining = (long) boss.getSecondsRest() * 1000L
                - (System.currentTimeMillis() - boss.getLastTimeRest());

        return Math.max(remaining, 0L);
    } catch (Exception e) {
        return 0L;
    }
}

   private String getBossKillerName(Boss boss) {
    try {
        if (boss != null
                && boss.getPlayerReward() != null
                && boss.getPlayerReward().name != null
                && !boss.getPlayerReward().name.isEmpty()) {
            return boss.getPlayerReward().name;
        }
    } catch (Exception e) {
    }

    return "Không rõ";
}

    private int getBossMapId(Boss boss) {
        try {
            if (boss == null) {
                return -1;
            }

            if (boss.zone != null && boss.zone.map != null) {
                return boss.zone.map.mapId;
            }

            if (boss.data != null
                    && boss.data.length > 0
                    && boss.data[0] != null
                    && boss.data[0].getMapJoin() != null
                    && boss.data[0].getMapJoin().length > 0) {
                return boss.data[0].getMapJoin()[0];
            }
        } catch (Exception e) {
        }

        return -1;
    }

    private int getBossZoneId(Boss boss) {
        try {
            if (boss != null && boss.zone != null) {
                return boss.zone.zoneId;
            }
        } catch (Exception e) {
        }

        return -1;
    }

    private String getBossLocationText(Boss boss) {
        try {
            if (boss != null && boss.zone != null && boss.zone.map != null) {
                return boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId;
            }

            int mapId = getBossMapId(boss);
            if (mapId != -1) {
                return "Chết rồi - map " + mapId;
            }
        } catch (Exception e) {
        }

        return "Chết rồi";
    }

    private long getLongField(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return 0L;
        }

        Class<?> clazz = obj.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.getLong(obj);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }

        return 0L;
    }

    private long getBossSpawnTime(Boss boss) {
        long value = getLongField(boss, "lastTimeBossSpawn");
        if (value > 0) {
            return value;
        }

        value = getLongField(boss, "lastTimeJoinMap");
        if (value > 0) {
            return value;
        }

        value = getLongField(boss, "lastTimeAppear");
        if (value > 0) {
            return value;
        }

        return 0L;
    }

    private void writeBossRow(Message msg, Player player, int index, Boss boss) throws Exception {
        msg.writer().writeInt(index);
        msg.writer().writeInt(index);

        msg.writer().writeShort(boss.data[0].getOutfit()[0]);

        if (player.getSession() != null && player.getSession().version >= 214) {
            msg.writer().writeShort(-1);
        }

        msg.writer().writeShort(boss.data[0].getOutfit()[1]);
        msg.writer().writeShort(boss.data[0].getOutfit()[2]);

        msg.writer().writeUTF(boss.data[0].getName());

        String status = boss.bossStatus != null ? boss.bossStatus.toString() : "UNKNOWN";
        msg.writer().writeUTF(status);
        msg.writer().writeUTF(getBossLocationText(boss));

        long lastTimeRestVal = 0L;
        int secondsRestVal = 0;
        long remainingTimeVal = 0L;

        if (needBossTimer(boss) && boss.getLastTimeRest() > 0) {
            lastTimeRestVal = boss.getLastTimeRest();
            secondsRestVal = boss.getSecondsRest();
            remainingTimeVal = getBossRemainingTime(boss);
        }

        msg.writer().writeLong(lastTimeRestVal);
        msg.writer().writeInt(secondsRestVal);
        msg.writer().writeLong(remainingTimeVal);

        msg.writer().writeLong(getBossSpawnTime(boss));
        msg.writer().writeUTF(getBossKillerName(boss));
        msg.writer().writeInt(getBossMapId(boss));
        msg.writer().writeInt(getBossZoneId(boss));
    }

   public void showListBoss(Player player) {
    if (player == null || !player.isFounder()) {
        return;
    }

    player.iDMark.setMenuType(3);

    Message msg = null;
    try {
        msg = new Message(-96);
        msg.writer().writeByte(0);
        msg.writer().writeUTF("Boss");

        int count = 0;
        for (Boss boss : bosses) {
            if (boss == null || boss.data == null || boss.data.length == 0 || boss.data[0] == null) {
                continue;
            }
            if (boss.data[0].getMapJoin() == null || boss.data[0].getMapJoin().length == 0) {
                continue;
            }
            if (!MapService.gI().isMapNoNottify(boss.data[0].getMapJoin()[0])) {
                count++;
            }
        }

        msg.writer().writeByte(count);

        for (int i = 0; i < bosses.size(); i++) {
            Boss boss = this.bosses.get(i);

            if (boss == null || boss.data == null || boss.data.length == 0 || boss.data[0] == null) {
                continue;
            }
            if (boss.data[0].getMapJoin() == null || boss.data[0].getMapJoin().length == 0) {
                continue;
            }
            if (MapService.gI().isMapNoNottify(boss.data[0].getMapJoin()[0])) {
                continue;
            }

            msg.writer().writeInt(i);
            msg.writer().writeInt(i);
            msg.writer().writeShort(boss.data[0].getOutfit()[0]);

            if (player.getSession() != null && player.getSession().version >= 214) {
                msg.writer().writeShort(-1);
            }

            msg.writer().writeShort(boss.data[0].getOutfit()[1]);
            msg.writer().writeShort(boss.data[0].getOutfit()[2]);
            msg.writer().writeUTF(boss.data[0].getName());

            if (boss.zone != null) {
                msg.writer().writeUTF(boss.bossStatus.toString());
                msg.writer().writeUTF(boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId);
            } else {
                msg.writer().writeUTF(boss.bossStatus.toString());
                msg.writer().writeUTF("Chết rồi");
            }
        }

        player.sendMessage(msg);
    } catch (Exception e) {
        Logger.logException(BossManager.class, e);
    } finally {
        if (msg != null) {
            msg.cleanup();
        }
    }
}

    public void showListBossMember(Player player) {
        if (player == null) {
            return;
        }

        player.iDMark.setMenuType(3);

        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Danh sách Boss");

            int count = 0;
            for (Boss boss : bosses) {
                if (boss != null
                        && boss.zone != null
                        && !boss.isDie()
                        && !isBossNoNotify(boss)) {
                    count++;
                }
            }

            msg.writer().writeByte(count);

            for (int i = 0; i < bosses.size(); i++) {
                Boss boss = bosses.get(i);

                if (boss == null
                        || boss.zone == null
                        || boss.isDie()
                        || isBossNoNotify(boss)) {
                    continue;
                }

                writeBossRow(msg, player, i, boss);
            }

            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(BossManager.class, e);
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public void showListBoss(Player player, List<TypeEventBoss> activeEvents) {
    if (player == null || activeEvents == null) {
        return;
    }

    player.iDMark.setMenuType(3);

    Message msg = null;
    try {
        msg = new Message(-96);
        msg.writer().writeByte(1);
        msg.writer().writeUTF("Boss Sự Kiện");

        int count = 0;
        for (Boss boss : bosses) {
            if (boss == null) {
                continue;
            }

            if (activeEvents.contains(TypeEventBoss.TRUNG_THU)) {
                if (boss instanceof ThoDaiKa) {
                    count++;
                }
            }
        }

        msg.writer().writeByte(count);

        for (int i = 0; i < bosses.size(); i++) {
            Boss boss = this.bosses.get(i);

            if (boss == null) {
                continue;
            }

            if (activeEvents.contains(TypeEventBoss.TRUNG_THU)) {
                if (!(boss instanceof ThoDaiKa)) {
                    continue;
                }
            }

            msg.writer().writeInt(i);
            msg.writer().writeInt(i);
            msg.writer().writeShort(boss.data[0].getOutfit()[0]);

            if (player.getSession() != null && player.getSession().version >= 214) {
                msg.writer().writeShort(-1);
            }

            msg.writer().writeShort(boss.data[0].getOutfit()[1]);
            msg.writer().writeShort(boss.data[0].getOutfit()[2]);
            msg.writer().writeUTF(boss.data[0].getName());

            if (boss.zone != null) {
                msg.writer().writeUTF(boss.zone.map.mapName);
                msg.writer().writeUTF(boss.zone.map.mapName + "(" + boss.zone.map.mapId + ") khu " + boss.zone.zoneId);
            } else {
                msg.writer().writeUTF("Boss bị thằng nào Thịt rồi!");
                msg.writer().writeUTF("Boss bị thằng nào Thịt rồi!");
            }
        }

        player.sendMessage(msg);
    } catch (Exception e) {
        Logger.logException(BossManager.class, e);
    } finally {
        if (msg != null) {
            msg.cleanup();
        }
    }
}

    public Boss getBossById(int bossId) {
        return this.bosses.stream()
                .filter(boss -> boss.id == bossId && !boss.isDie())
                .findFirst()
                .orElse(null);
    }

    public boolean checkBosses(Zone zone, int BossID) {
        return this.bosses.stream()
                .filter(boss -> boss.id == BossID && boss.zone != null && boss.zone.equals(zone) && !boss.isDie())
                .findFirst()
                .orElse(null) != null;
    }

    public Player findBossClone(Player player) {
        return player.zone.getBosses().stream()
                .filter(boss -> boss.id < -100_000_000 && !boss.isDie())
                .findFirst()
                .orElse(null);
    }

    public Boss getBossById(int bossId, int mapId, int zoneId) {
        return this.bosses.stream()
                .filter(boss -> boss.id == bossId
                        && boss.zone != null
                        && boss.zone.map.mapId == mapId
                        && boss.zone.zoneId == zoneId
                        && !boss.isDie())
                .findFirst()
                .orElse(null);
    }

    public Boss getBossTauPayPayByPlayer(Player player) {
        for (int i = bosses.size() - 1; i >= 0; i--) {
            if (bosses.get(i).id == (-251003 - player.id - 2000)) {
                return bosses.get(i);
            }
        }
        return null;
    }

    public void resetAllBosses() {
        synchronized (runtimeLock) {
            try {
                for (Boss boss : this.bosses) {
                    if (boss != null && boss.zone != null) {
                        boss.leaveMap();
                        boss.setDieLV(boss);
                    }
                }

                this.bosses.clear();
                this.loadBoss();
                System.out.println("[BossManager] Đã reset toàn bộ boss.");
            } catch (Exception e) {
                System.err.println("[BossManager] Lỗi khi reset boss: " + e.getMessage());
            }
        }
    }

    public int respawnAllRestingBosses() {
        synchronized (runtimeLock) {
            int count = 0;
            for (Boss boss : bosses) {
                if (boss != null && (boss.isDie() || boss.zone == null)) {
                    try {
                        boss.changeStatus(BossStatus.RESPAWN);
                        count++;
                    } catch (Exception e) {
                        System.err.println("Lỗi hồi sinh boss " + boss.name + ": " + e.getMessage());
                    }
                }
            }
            return count;
        }
    }

    public int[] getBossStatusCounts() {
        int alive = 0;
        int dead = 0;
        int resting = 0;

        for (Boss boss : snapshotBosses()) {
            if (boss == null) {
                continue;
            }

            if (boss.zone == null) {
                resting++;
            } else if (boss.isDie()) {
                dead++;
            } else {
                alive++;
            }
        }

        return new int[]{alive, dead, resting};
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                int delay = 150;
                long st = System.currentTimeMillis();

                synchronized (runtimeLock) {
                    for (int i = this.bosses.size() - 1; i >= 0; i--) {
                        try {
                            Boss boss = this.bosses.get(i);
                            if (boss != null) {
                                boss.update();
                            }
                        } catch (Exception e) {
                            Logger.logException(BossManager.class, e);
                        }
                    }
                }

                Functions.sleep(Math.max(delay - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                Logger.logException(BossManager.class, e);
            }
        }
    }
}
