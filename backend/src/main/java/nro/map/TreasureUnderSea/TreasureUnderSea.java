package nro.map.TreasureUnderSea;

/*
 * @Author: MaiTienDung
 */

import nro.mob.Mob;
import nro.player.Player;
import QuanLiBoss.Boss;
import Utils.Functions;
import Utils.Logger;
import nro.server.Maintenance;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.Service;
import Utils.TimeUtil;
import Utils.Util;
import nro.clan.Clan;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import models.Item.ItemMapService;
import models.Item.ItemTimeService;
import nro.boss.Anw.TreasureUnderSea.TrungUyXanhLo;
import nro.map.Zone;

@Data
public class TreasureUnderSea implements Runnable {

    public static final long POWER_CAN_GO_TO_DBKB = 200000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_BAN_DO_KHO_BAU = 1800000;

    public int id;
    public byte level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;
    private boolean kickoutbdkb;
    private long timeKickOutBDKB;
    private Boss boss;
    private long lastTimeSendNotify;
    private boolean allCharactersDead;

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public TreasureUnderSea(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && isOpened) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 150 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                Logger.logException(TreasureUnderSea.class, e);
            }
        }
    }

    public void update() {
        if (isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU) || (kickoutbdkb && Util.canDoWithTime(timeKickOutBDKB, 60000))) {
                finish();
                dispose();
            }

            allCharactersDead = true;
            for (Zone zone : zones) {

                if (zone.map.mapId == 135) {
                    for (Player pl : zone.getNotBosses()) {
                        if (pl != null) {
                            TrapMap trap = zone.isInTrap(pl);
                            if (trap != null) {
                                trap.doPlayer(pl);
                            }
                        }
                    }
                }

                for (Mob mob : zone.mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player cBoss : zone.getBosses()) {
                        if (!cBoss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }
            }
            if (!kickoutbdkb && (allCharactersDead || Util.canDoWithTime(lastTimeOpen, TIME_BAN_DO_KHO_BAU - 60000))) {
                kickoutbdkb = true;
                timeKickOutBDKB = System.currentTimeMillis();
            }

            if (kickoutbdkb && Util.canDoWithTime(lastTimeSendNotify, 10000)) {
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Cái hang này sắp sập rồi, chúng ta phải rời khỏi đây ngay " + TimeUtil.getTimeLeft(timeKickOutBDKB, 60) + " nữa");
                    }
                    lastTimeSendNotify = System.currentTimeMillis();
                }
            }

        }
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.lastTimeOpenBanDoKhoBau = this.lastTimeOpen;
            this.clan.playerOpenBanDoKhoBau = plOpen;
            this.clan.BanDoKhoBau = this;
            this.kickoutbdkb = false;
            this.isOpened = true;
            this.allCharactersDead = false;
            this.init();
            ChangeMapService.gI().goToDBKB(plOpen);
            sendTextBanDoKhoBau();
        } catch (Exception e) {
            plOpen.clan.lastTimeOpenBanDoKhoBau = 0;
            this.dispose();
        }
    }
    
    public void sendThanhTichBanDoKhoBau(Player pl) {
        long timeDoneBDKB;
        timeDoneBDKB = System.currentTimeMillis() - pl.clan.lastTimeOpenBanDoKhoBau;
        int levelDoneBDKB;
        levelDoneBDKB = pl.clan.BanDoKhoBau.level;
        if (levelDoneBDKB > pl.clan.levelDoneBanDoKhoBau) {
            pl.clan.levelDoneBanDoKhoBau = levelDoneBDKB;
            pl.clan.thoiGianHoanThanhBDKB = timeDoneBDKB;
            System.out.println("levelDoneBDKB: " + levelDoneBDKB);
            System.out.println("timeDoneBDKB: " + timeDoneBDKB);
        } else if (levelDoneBDKB == pl.clan.levelDoneBanDoKhoBau) {
            if (timeDoneBDKB < pl.clan.thoiGianHoanThanhBDKB) {
                pl.clan.thoiGianHoanThanhBDKB = timeDoneBDKB;
            }
        }
        pl.clan.updatethanhTichBDKB(pl.clan.id);
        pl.clan.updatethanhTichBDKBForLeader();
        pl.clan.updateThongTinLeader(pl.clan.id);
    }

    private void init() {
        //Hồi sinh quái
        for (Zone zone : this.zones) {
            for (TrapMap trap : zone.trapMaps) {
                trap.dame = this.level * 10000;
            }

            if (zone.map.mapId == 135 || zone.map.mapId == 136 || zone.map.mapId == 137) {
                List<Mob> mobs = zone.mobs;
                for (int i = 0; i < mobs.size(); i++) {
                    Mob mob = mobs.get(i);
                    if (((i == 5 || i == 10) && zone.map.mapId == 135) || (i == 5 && zone.map.mapId == 136) || (i == 5 && zone.map.mapId == 137)) {
                        mob.lvMob = 1;
                        mob.point.dame = level <= 109 ? Math.min((level * 61 * mob.tempId * 2) * 8, 2_000_000_000) : Math.min((level * 610 * mob.tempId * 2) * 8, 2_000_000_000);
                        mob.point.maxHp = level <= 109 ? Math.min((level * 3107 * mob.tempId * 2) * 8, 2_000_000_000) : Math.min((level * 5107 * mob.tempId * 2) * 8, 2_000_000_000);
                        mob.hoiSinh();
                        mob.hoiSinhMobPhoBan();
                    } else {
                        mob.lvMob = 0;
                        mob.point.dame = level <= 109 ? Math.min(level * 61 * mob.tempId * 2, 2_000_000_000) : Math.min(level * 610 * mob.tempId * 2, 2_000_000_000);
                        mob.point.maxHp = level <= 109 ? Math.min(level * 3107 * mob.tempId * (mob.tempId == 72 ? 30 : mob.tempId == 71 ? 40 : 2), 2_000_000_000) : Math.min(level * 5107 * mob.tempId * (mob.tempId == 72 ? 30 : mob.tempId == 71 ? 40 : 2), 2_000_000_000);
                        mob.hoiSinh();
                        mob.hoiSinhMobPhoBan();
                    }
                }
            } else {
                for (Mob mob : zone.mobs) {
                    mob.point.dame = level <= 109 ? Math.min(level * 61 * mob.tempId * 2, 2_000_000_000) : Math.min(level * 610 * mob.tempId * 2, 2_000_000_000);
                    mob.point.maxHp = level <= 109 ? Math.min(level * 3107 * mob.tempId * (mob.tempId == 72 ? 30 : mob.tempId == 71 ? 40 : 2), 2_000_000_000) : Math.min(level * 6107 * mob.tempId * (mob.tempId == 72 ? 30 : mob.tempId == 71 ? 40 : 2), 2_000_000_000);
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }

            if (zone.map.mapId == 137) {
                try {
                    long bossDamage = level <= 109 ? (3000L * level) : (7200L * level);
                    long bossMaxHealth = level <= 109 ? (1870000L * level) : (3870000L * level);
                    boss = new TrungUyXanhLo(zone, level, bossDamage, bossMaxHealth);
                } catch (Exception exception) {
                }
            }
        }
        new Thread(this, "Bản Đồ Kho Báu: " + this.clan.name).start();
    }

    //kết thúc bản đồ kho báu
    public void finish() {
        for (Zone zone : zones) {
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOutOfBDKB(pl);
                    sendThanhTichBanDoKhoBau(pl);
                }
            }
        }
    }

    private void kickOutOfBDKB(Player player) {
        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1038);
        }
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    private void sendTextBanDoKhoBau() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }

    private void removeTextBanDoKhoBau() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().removeTextBanDoKhoBau(pl);
        }
    }

    public void dispose() {
        if (boss != null) {
            this.boss.leaveMap();
        }
        for (Zone zone : zones) {
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }
        this.removeTextBanDoKhoBau();
        this.allCharactersDead = false;
        this.boss = null;
        this.isOpened = false;
        this.clan.BanDoKhoBau = null;
        this.clan = null;
        this.kickoutbdkb = false;
    }
}
