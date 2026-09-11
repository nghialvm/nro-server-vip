package nro.map.SnakeWay;



import nro.boss.Anw.SnakeWay.Cadich;
import nro.boss.Anw.SnakeWay.Nadic;
import nro.boss.Anw.SnakeWay.Saibamen;
import nro.mob.Mob;
import nro.player.Player;
import QuanLiBoss.Boss;
import QuanLiBoss.BossStatus;
import Utils.Functions;
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
import nro.boss.Anw.SnakeWay.Saibamen2;
import nro.boss.Anw.SnakeWay.Saibamen3;
import nro.boss.Anw.SnakeWay.Saibamen4;
import nro.boss.Anw.SnakeWay.Saibamen5;
import nro.boss.Anw.SnakeWay.Saibamen6;
import nro.map.Zone;

@Data
public class SnakeWay implements Runnable {

    public static final long POWER_CAN_GO_TO_CDRD = 2000000000;
    public static final int AVAILABLE = 50;
    public static final int TIME_CON_DUONG_RAN_DOC = 1800000;

    public int id;
    public byte level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;
    private long lastTimeUpdateMessage;
    private boolean kickoutcdrd;
    private long timeKickOutCDRD;
    public List<Boss> bosses = new ArrayList<>();
    public boolean endCDRD;
    public boolean allMobsDead;

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public SnakeWay(int id) {
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
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (isOpened) {
            if (Util.canDoWithTime(lastTimeOpen, TIME_CON_DUONG_RAN_DOC) || (kickoutcdrd && Util.canDoWithTime(timeKickOutCDRD, 60000))) {
                finish();
                dispose();
            }

            boolean allCharactersDead = true;
            for (Zone zone : zones) {
                for (Mob mob : zone.mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }
            }
            if (allCharactersDead) {
                allMobsDead = true;
            }

            if (!kickoutcdrd && (endCDRD || Util.canDoWithTime(lastTimeOpen, TIME_CON_DUONG_RAN_DOC - 60000))) {
                kickoutcdrd = true;
                timeKickOutCDRD = System.currentTimeMillis();
            }
            if (kickoutcdrd && Util.canDoWithTime(lastTimeUpdateMessage, 10000)) {
                lastTimeUpdateMessage = System.currentTimeMillis();
                for (Zone zone : zones) {
                    List<Player> players = zone.getPlayers();
                    for (Player pl : players) {
                        Service.gI().sendThongBao(pl, "Trận chiến với người Xayda sẽ kết thúc sau " + TimeUtil.getTimeLeft(timeKickOutCDRD, 60) + " nữa");
                    }

                }
            }

        }
    }

    public void openConDuongRanDoc(Player plOpen, Clan clan, byte level) {
        try {
            this.level = level;
            this.lastTimeOpen = System.currentTimeMillis();
            this.clan = clan;
            this.clan.lastTimeOpenConDuongRanDoc = this.lastTimeOpen;
            this.clan.playerOpenConDuongRanDoc = plOpen;
            this.clan.ConDuongRanDoc = this;
            this.isOpened = true;
            this.init();
            sendTextConDuongRanDoc();
        } catch (Exception e) {
            plOpen.clan.lastTimeOpenConDuongRanDoc = 0;
            this.dispose();
        }
    }

    private void init() {
        //Hồi sinh quái
        for (Zone zone : this.zones) {
            List<Mob> mobs = zone.mobs;
            for (int i = 0; i < mobs.size(); i++) {
                Mob mob = mobs.get(i);
                if (i == 5) {
                    mob.lvMob = 1;
                    mob.point.dame = level * 300L * mob.tempId * 12L;
                    mob.point.maxHp = level * 5000L * mob.tempId * 12L;
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                } else {
                    mob.lvMob = 0;
                    mob.point.dame = level * 300L * mob.tempId;
                    mob.point.maxHp = level * 5000L * mob.tempId;
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }

            if (zone.map.mapId == 144) {
                try {
                    long bossDamageSaibamen = (8000L * level);
                    long bossMaxHealthSaibamen = (300000L * level);
                    long bossDamageNadic = (12000L * level);
                    long bossMaxHealthNadic = (3000000L * level);
                    long bossDamageCadich = (15000L * level);
                    long bossMaxHealthCadich = (6000000L * level);
                    //
                    bosses.add(new Saibamen(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Saibamen2(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Saibamen3(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Saibamen4(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Saibamen5(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Saibamen6(zone, clan, bossDamageSaibamen, bossMaxHealthSaibamen));
                    bosses.add(new Nadic(zone, clan, bossDamageNadic, bossMaxHealthNadic));
                    bosses.add(new Cadich(zone, clan, bossDamageCadich, bossMaxHealthCadich));
                } catch (Exception exception) {
                }
            }
        }
        new Thread(this, "Con Đường Rắn Độc: " + this.clan.name).start();
    }
    
    public void sendThanhTichConDuongRanDoc(Player pl) {
        long timeDoneCDRD;
        timeDoneCDRD = System.currentTimeMillis() - pl.clan.lastTimeOpenConDuongRanDoc;
        int levelDoneCDRD;
        levelDoneCDRD = pl.clan.ConDuongRanDoc.level;
        if (levelDoneCDRD > pl.clan.levelDoneConDuongRanDoc) {
            pl.clan.levelDoneConDuongRanDoc = levelDoneCDRD;
            pl.clan.thoiGianHoanThanhCDRD = timeDoneCDRD;
            System.out.println("levelDoneCDRD: " + levelDoneCDRD);
            System.out.println("timeDoneCDRD: " + timeDoneCDRD);
        } else if (levelDoneCDRD == pl.clan.levelDoneConDuongRanDoc) {
            if (timeDoneCDRD < pl.clan.thoiGianHoanThanhCDRD) {
                pl.clan.thoiGianHoanThanhCDRD = timeDoneCDRD;
            }
        }
        pl.clan.updatethanhTichCDRD(pl.clan.id);
        pl.clan.updatethanhTichCDRDForLeader();
        pl.clan.updateThongTinLeader(pl.clan.id);
    }

    //kết thúc con đường rắn độc
    public void finish() {
        for (Zone zone : zones) {
            for (int i = zone.getPlayers().size() - 1; i >= 0; i--) {
                if (i < zone.getPlayers().size()) {
                    Player pl = zone.getPlayers().get(i);
                    kickOutOfCDRD(pl);
                    sendThanhTichConDuongRanDoc(pl);
                }
            }
        }
    }

    private void kickOutOfCDRD(Player player) {
        if (MapService.gI().isMapConDuongRanDoc(player.zone.map.mapId)) {
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

    private void sendTextConDuongRanDoc() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextConDuongRanDoc(pl);
        }
    }

    private void removeTextConDuongRanDoc() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().removeTextConDuongRanDoc(pl);
        }
    }

    public long getNumBossAlive() {
        return bosses.stream().filter(boss -> boss.bossStatus != BossStatus.REST).count();
    }

    public void dispose() {
        // remove bosses
        for (Boss boss : bosses) {
            if (!boss.isDie()) {
                boss.leaveMap();
            }
        }
        for (Zone zone : zones) {
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }
        this.removeTextConDuongRanDoc();
        this.bosses.clear();
        this.allMobsDead = false;
        this.endCDRD = false;
        this.isOpened = false;
        this.clan.ConDuongRanDoc = null;
        this.clan = null;
        this.kickoutcdrd = false;
    }
}
