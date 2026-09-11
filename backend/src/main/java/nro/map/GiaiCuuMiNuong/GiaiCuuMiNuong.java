package nro.map.GiaiCuuMiNuong;

import QuanLiBoss.Boss;
import Utils.Functions;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import models.Item.ItemMapService;
import models.Item.ItemTimeService;
import nro.boss.Anw.BossOfTheGangs.MiNuongClan;
import nro.clan.Clan;
import nro.map.Zone;
import nro.mob.Mob;
import nro.player.Player;
import nro.server.Maintenance;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.Service;

/**
 * A private clan instance for the Mị Nương rescue activity.
 */
public class GiaiCuuMiNuong implements Runnable {

    public static final int N_PLAYER_CLAN = 3;
    public static final int N_PLAYER_MAP = 2;
    public static final int AVAILABLE = 120;
    public static final int TIME_GIAI_CUU_MI_NUONG = 1_800_000;
    private static final int TIME_KICK_OUT = 30_000;

    public final int id;
    public final List<Zone> zones;
    public final List<Boss> bosses;

    private Clan clan;
    private boolean isOpened;
    private long lastTimeOpen;
    private boolean bossDead;
    private boolean kickOut;
    private long timeKickOut;
    private long lastTimeNotify;

    public GiaiCuuMiNuong(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
        this.bosses = new ArrayList<>();
    }

    public synchronized Clan getClan() {
        return this.clan;
    }

    public synchronized boolean isOpened() {
        return this.isOpened;
    }

    public synchronized boolean isBossDead() {
        return this.bossDead;
    }

    public synchronized void addZone(Zone zone) {
        if (zone != null && !this.zones.contains(zone)) {
            this.zones.add(zone);
        }
    }

    public synchronized Zone getMapById(int mapId) {
        for (Zone zone : this.zones) {
            if (zone != null && zone.map != null && zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    /**
     * Opens this instance and moves the already validated party to map 185.
     */
    public synchronized boolean openGiaiCuuMiNuong(Player player, List<Player> participants) {
        if (this.isOpened || this.clan != null || player == null || player.clan == null) {
            return false;
        }

        Clan eventClan = player.clan;
        long now = System.currentTimeMillis();
        this.clan = eventClan;
        this.lastTimeOpen = now;
        this.bossDead = false;
        this.kickOut = false;
        this.timeKickOut = 0;
        this.lastTimeNotify = 0;
        this.isOpened = true;

        eventClan.giaiCuuMiNuong = this;
        eventClan.lastTimeOpenGiaiCuuMiNuong = now;
        eventClan.haveGoneGiaiCuuMiNuong = false;

        try {
            init();
            sendTextGiaiCuuMiNuong();
            new Thread(this, "Giải cứu Mị Nương: " + eventClan.name).start();
        } catch (Exception e) {
            Logger.logException(GiaiCuuMiNuong.class, e);
            close(false, false);
            return false;
        }

        Zone entryZone = getMapById(185);
        if (entryZone == null) {
            close(false, false);
            return false;
        }

        if (participants != null) {
            for (Player participant : new ArrayList<>(participants)) {
                moveToEntry(participant, entryZone);
            }
        }
        return true;
    }

    private void init() throws Exception {
        for (Zone zone : this.zones) {
            if (zone == null) {
                continue;
            }
            for (Mob mob : zone.mobs) {
                if (mob != null) {
                    mob.hoiSinh();
                    mob.hoiSinhMobPhoBan();
                }
            }
        }

        Zone bossZone = getMapById(185);
        if (bossZone == null) {
            throw new Exception("Không tìm thấy map 185 của instance " + this.id);
        }
        this.bosses.add(new MiNuongClan(bossZone, this.clan, this));
    }

    private void moveToEntry(Player player, Zone entryZone) {
        if (player == null || player.zone == null || player.clan != this.clan || player.isDie()) {
            return;
        }
        try {
            ChangeMapService.gI().changeMapInYard(player, entryZone, 60);
        } catch (Exception e) {
            Logger.logException(GiaiCuuMiNuong.class, e);
        }
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning && isOpened()) {
            try {
                long startTime = System.currentTimeMillis();
                update();
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = 150 - elapsedTime;
                if (sleepTime > 0) {
                    Functions.sleep(sleepTime);
                }
            } catch (Exception e) {
                Logger.logException(GiaiCuuMiNuong.class, e);
            }
        }
    }

    public synchronized void update() {
        if (!this.isOpened) {
            return;
        }

        if (Util.canDoWithTime(this.lastTimeOpen, TIME_GIAI_CUU_MI_NUONG)) {
            close(true, true);
            return;
        }

        if (this.bossDead && !this.kickOut) {
            this.kickOut = true;
            this.timeKickOut = System.currentTimeMillis();
        }

        if (this.kickOut) {
            if (Util.canDoWithTime(this.lastTimeNotify, 5_000)) {
                this.lastTimeNotify = System.currentTimeMillis();
                notifyKickOut();
            }
            if (Util.canDoWithTime(this.timeKickOut, TIME_KICK_OUT)) {
                close(true, true);
            }
        }
    }

    private void notifyKickOut() {
        Clan eventClan = this.clan;
        if (eventClan == null) {
            return;
        }
        for (Player player : new ArrayList<>(eventClan.membersInGame)) {
            if (player != null && player.zone != null && MapService.gI().isMapGiaiCuuMiNuong(player.zone.map.mapId)) {
                Service.gI().sendThongBao(player,
                        "Đã hoàn thành, về nhà sau " + TimeUtil.getTimeLeft(this.timeKickOut, TIME_KICK_OUT / 1000) + " nữa");
            }
        }
    }

    public synchronized void markBossDead() {
        if (this.isOpened) {
            this.bossDead = true;
        }
    }

    public synchronized void dispose() {
        close(true, true);
    }

    private synchronized void close(boolean completed, boolean kickPlayers) {
        if (!this.isOpened && this.clan == null) {
            return;
        }

        Clan eventClan = this.clan;
        this.isOpened = false;

        if (kickPlayers) {
            kickPlayers();
        }

        for (Zone zone : this.zones) {
            if (zone == null) {
                continue;
            }
            for (int i = zone.items.size() - 1; i >= 0; i--) {
                if (i < zone.items.size()) {
                    ItemMapService.gI().removeItemMap(zone.items.get(i));
                }
            }
        }

        for (Boss boss : new ArrayList<>(this.bosses)) {
            if (boss != null) {
                try {
                    boss.leaveMap();
                } catch (Exception e) {
                    Logger.logException(GiaiCuuMiNuong.class, e);
                }
            }
        }
        this.bosses.clear();
        removeTextGiaiCuuMiNuong();

        if (eventClan != null && eventClan.giaiCuuMiNuong == this) {
            eventClan.giaiCuuMiNuong = null;
            eventClan.haveGoneGiaiCuuMiNuong = completed;
            if (!completed) {
                eventClan.lastTimeOpenGiaiCuuMiNuong = 0;
            }
        }

        this.clan = null;
        this.lastTimeOpen = 0;
        this.bossDead = false;
        this.kickOut = false;
        this.timeKickOut = 0;
        this.lastTimeNotify = 0;
    }

    private void kickPlayers() {
        for (Zone zone : this.zones) {
            if (zone == null) {
                continue;
            }
            for (Player player : new ArrayList<>(zone.getPlayers())) {
                if (player != null && player.zone != null
                        && MapService.gI().isMapGiaiCuuMiNuong(player.zone.map.mapId)) {
                    try {
                        ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
                    } catch (Exception e) {
                        Logger.logException(GiaiCuuMiNuong.class, e);
                    }
                }
            }
        }
    }

    private void sendTextGiaiCuuMiNuong() {
        if (this.clan == null) {
            return;
        }
        for (Player player : new ArrayList<>(this.clan.membersInGame)) {
            if (player != null) {
                ItemTimeService.gI().sendTextGiaiCuuMiNuong(player);
            }
        }
    }

    private void removeTextGiaiCuuMiNuong() {
        if (this.clan == null) {
            return;
        }
        for (Player player : new ArrayList<>(this.clan.membersInGame)) {
            if (player != null) {
                ItemTimeService.gI().removeTextGiaiCuuMiNuong(player);
            }
        }
    }
}
