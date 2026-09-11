package nro.map.GiaiCuuMiNuong;

import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import nro.clan.Clan;
import nro.clan.ClanMember;
import nro.map.Zone;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;

public class GiaiCuuMiNuongService {

    private static GiaiCuuMiNuongService instance;

    private final List<GiaiCuuMiNuong> giaiCuuMiNuongs;

    public static GiaiCuuMiNuongService gI() {
        if (instance == null) {
            instance = new GiaiCuuMiNuongService();
        }
        return instance;
    }

    private GiaiCuuMiNuongService() {
        this.giaiCuuMiNuongs = new ArrayList<>();
        for (int i = 0; i < GiaiCuuMiNuong.AVAILABLE; i++) {
            this.giaiCuuMiNuongs.add(new GiaiCuuMiNuong(i));
        }
    }

    public List<GiaiCuuMiNuong> getGiaiCuuMiNuongs() {
        return this.giaiCuuMiNuongs;
    }

    public void addMapGiaiCuuMiNuong(int id, Zone zone) {
        if (id >= 0 && id < this.giaiCuuMiNuongs.size()) {
            this.giaiCuuMiNuongs.get(id).addZone(zone);
        }
    }

    public synchronized void openGiaiCuuMiNuong(Player player) {
        if (player == null || player.clan == null) {
            if (player != null) {
                Service.gI().sendThongBao(player, "Yêu cầu có bang hội mới tham gia được");
            }
            return;
        }

        Clan clan = player.clan;
        if (!hasMinimumEntryConditions(player, clan)) {
            Service.gI().sendThongBao(player, "Không thể tham gia lúc này");
            return;
        }

        GiaiCuuMiNuong active = clan.giaiCuuMiNuong;
        if (active != null && active.isOpened()) {
            if (active.getClan() == clan) {
                Zone entryZone = active.getMapById(185);
                if (entryZone != null) {
                    ChangeMapService.gI().changeMapInYard(player, entryZone, 60);
                }
                return;
            }
            Service.gI().sendThongBao(player, "Không thể tham gia khu vực của bang hội khác");
            return;
        }
        if (active != null) {
            clan.giaiCuuMiNuong = null;
        }

        if (clan.haveGoneGiaiCuuMiNuong && clan.lastTimeOpenGiaiCuuMiNuong != 0
                && !Util.isAfterMidnight(clan.lastTimeOpenGiaiCuuMiNuong)) {
            Service.gI().sendThongBaoOK(player,
                    "Bang hội của anh đã tham gia hôm nay rồi\nHẹn gặp anh vào ngày mai ♡");
            return;
        }

        if (clan.getMembers() == null || clan.getMembers().size() < GiaiCuuMiNuong.N_PLAYER_CLAN) {
            Service.gI().sendThongBao(player, "Bang hội phải có đủ 3 người mới được tham gia");
            return;
        }
        if (player.zone == null || player.isDie()) {
            Service.gI().sendThongBao(player, "Không thể thực hiện lúc này");
            return;
        }

        ClanMember playerMember = player.clanMember;
        if (playerMember == null || playerMember.getNumDateFromJoinTimeToToday() < 1) {
            Service.gI().sendThongBao(player, "Yêu cầu tham gia bang hội trên 1 ngày");
            return;
        }

        List<Player> participants = collectParticipants(player, clan);
        if (participants.size() < GiaiCuuMiNuong.N_PLAYER_CLAN) {
            Service.gI().sendThongBao(player, "Hãy đứng cùng 2 người trong bang để tham gia");
            return;
        }

        GiaiCuuMiNuong instanceToOpen = null;
        for (GiaiCuuMiNuong event : this.giaiCuuMiNuongs) {
            if (!event.isOpened() && event.getClan() == null) {
                instanceToOpen = event;
                break;
            }
        }
        if (instanceToOpen == null) {
            Service.gI().sendThongBao(player, "Khu vực đã đầy, hãy quay lại sau");
            return;
        }

        if (!instanceToOpen.openGiaiCuuMiNuong(player, participants)) {
            Service.gI().sendThongBao(player, "Không thể khởi tạo khu vực, vui lòng thử lại");
        }
    }

    private List<Player> collectParticipants(Player opener, Clan clan) {
        List<Player> participants = new ArrayList<>();
        if (opener == null || opener.zone == null || opener.location == null
                || opener.isDie() || opener.clanMember == null) {
            return participants;
        }
        participants.add(opener);
        for (Player player : new ArrayList<>(opener.zone.getPlayers())) {
            if (participants.size() >= GiaiCuuMiNuong.N_PLAYER_CLAN) {
                break;
            }
            if (player == null || player.equals(opener) || player.clan != clan || player.isDie()
                    || player.clanMember == null || player.location == null
                    || player.location.x < 1120 || player.location.x > 1500) {
                continue;
            }
            if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                continue;
            }
            participants.add(player);
        }
        return participants;
    }

    private boolean hasMinimumEntryConditions(Player player, Clan clan) {
        return player != null && clan != null
                && clan.getMembers() != null
                && clan.getMembers().size() >= GiaiCuuMiNuong.N_PLAYER_CLAN
                && player.zone != null
                && player.location != null
                && !player.isDie()
                && player.clanMember != null
                && player.clanMember.getNumDateFromJoinTimeToToday() >= 1;
    }
}
