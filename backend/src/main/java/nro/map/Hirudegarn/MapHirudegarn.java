package nro.map.Hirudegarn;

import nro.player.Player;
import nro.services.MapService;
import nro.services.Service;
import nro.services.Fun.ChangeMapService;
import Utils.TimeUtil;
import java.util.List;

public class MapHirudegarn {

    public static final byte HOUR_OPEN_MAP_22H = 22;
    public static final byte MIN_OPEN_MAP_22H = 0;
    public static final byte SECOND_OPEN_MAP_22H = 0;

    public static final byte HOUR_CLOSE_MAP_22H = 22;
    public static final byte MIN_CLOSE_MAP_22H = 30;
    public static final byte SECOND_CLOSE_MAP_22H = 0;

    public static final int AVAILABLE = 120;

    private static MapHirudegarn i;

    public static long TIME_OPEN_22h;
    public static long TIME_CLOSE_22h;

    private int day = -1;

    public static MapHirudegarn gI() {
        if (i == null) {
            i = new MapHirudegarn();
        }

        i.setTimeJoinMap22h();
        return i;
    }

    public void setTimeJoinMap22h() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();

            try {
                TIME_OPEN_22h = TimeUtil.getTime(
                        TimeUtil.getTimeNow("dd/MM/yyyy") + " "
                        + HOUR_OPEN_MAP_22H + ":"
                        + MIN_OPEN_MAP_22H + ":"
                        + SECOND_OPEN_MAP_22H,
                        "dd/MM/yyyy HH:mm:ss"
                );

                TIME_CLOSE_22h = TimeUtil.getTime(
                        TimeUtil.getTimeNow("dd/MM/yyyy") + " "
                        + HOUR_CLOSE_MAP_22H + ":"
                        + MIN_CLOSE_MAP_22H + ":"
                        + SECOND_CLOSE_MAP_22H,
                        "dd/MM/yyyy HH:mm:ss"
                );
            } catch (Exception ignored) {
            }
        }
    }

    private void kickOutOfMap22h(Player player) {
        if (player != null
                && player.zone != null
                && MapService.gI().isMapHirudegarn(player.zone.map.mapId)) {

            Service.getInstance().sendThongBao(
                    player,
                    "Trận Chiến Đã Kết Thúc, Tàu Vận Chuyển Sẽ Đưa Bạn Về Nhà"
            );

            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    private void ketthuc22h(Player player) {
        if (player == null || player.zone == null) {
            return;
        }

        List<Player> playersMap = player.zone.getPlayers();

        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfMap22h(pl);
        }
    }

    public void joinMap22h(Player player) {
        if (player.clan != null) {
            List<Player> players = player.zone.getPlayers();

            for (Player pl : players) {
            }
        }
    }

    public void update(Player player) {
        try {
            if (player == null
                    || player.zone == null
                    || !MapService.gI().isMapHirudegarn(player.zone.map.mapId)) {
                return;
            }

            long now = System.currentTimeMillis();

            if (now < TIME_OPEN_22h || now > TIME_CLOSE_22h) {
                ketthuc22h(player);
            }
        } catch (Exception ignored) {
        }
    }
}