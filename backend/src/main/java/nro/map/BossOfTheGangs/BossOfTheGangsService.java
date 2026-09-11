package nro.map.BossOfTheGangs;



import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import nro.clan.Clan;
import nro.clan.ClanMember;
import java.util.ArrayList;
import java.util.List;
import nro.map.Zone;
import nro.services.Fun.ChangeMapService;

public class BossOfTheGangsService {

    private static BossOfTheGangsService instance;

    public static BossOfTheGangsService gI() {
        if (instance == null) {
            instance = new BossOfTheGangsService();
        }
        return instance;
    }

    public List<BossOfTheGangs> BossOfTheGangss;

    private BossOfTheGangsService() {
        this.BossOfTheGangss = new ArrayList<>();
        for (int i = 0; i < BossOfTheGangs.AVAILABLE; i++) {
            this.BossOfTheGangss.add(new BossOfTheGangs(i));
        }
    }

    public void addMapBossOfTheGangs(int id, Zone zone) {
        this.BossOfTheGangss.get(id).getZones().add(zone);
    }
    
    private int getRubyPlayer(Player player) {
        switch (player.clan.boss_clan_round) {
            case 1:
                return 0;
            case 2:
                return 100;
            case 3:
                return 300;
            case 4:
                return 500;
            case 5:
                return 2000;
            default:
                return 0;
        }
    }
    
    public void openBossOfTheGangs(Player player) {
        Clan clan = player.clan;
        if (clan != null) {
            if (player.inventory.getRuby() < getRubyPlayer(player)) {
                Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc, còn thiếu " + (getRubyPlayer(player) - player.inventory.ruby) + " hồng ngọc nữa");
                return;
            }
            player.inventory.subRuby(getRubyPlayer(player));
            Service.gI().sendMoney(player);
            ClanMember cm = clan.getClanMember((int) player.id);
            if (cm != null) {
                if (player.clan.BossOfTheGang == null) {
                    if (clan.isLeader(player)) {
                        BossOfTheGangs BossOfTheGang = null;
                        for (BossOfTheGangs bbbb : this.BossOfTheGangss) {
                            if (!bbbb.isOpened) {
                                BossOfTheGang = bbbb;
                                break;
                            }
                        }
                        if (BossOfTheGang != null) {
                            if (player.clan.boss_clan_round > 5) {
                                Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                return;
                            }
                            player.clan.boss_clan_round++;
                            BossOfTheGang.openBossOfTheGangs(player.clan);
                        } else {
                            Service.gI().sendThongBao(player, "Map quá đông, hãy quay lại sau 30 phút");
                            return;
                        }
                    }
                }
                if (player.clan.BossOfTheGang != null) {
                    ChangeMapService.gI().changeMap(player, 165, -1, Util.nextInt(240, 260), 300);
                }
            }
        }
    }
}
