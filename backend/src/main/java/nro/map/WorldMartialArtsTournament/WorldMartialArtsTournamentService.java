package nro.map.WorldMartialArtsTournament;



import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.services.Service;
import Utils.TimeUtil;
import consts.ConstNpc;
import consts.ConstTournament;
import static consts.ConstTournament.NGOAI_HANG;
import static consts.ConstTournament.NHI_DONG;
import static consts.ConstTournament.SIEU_CAP_1;
import static consts.ConstTournament.SIEU_CAP_2;
import static consts.ConstTournament.SIEU_CAP_3;
import static consts.ConstTournament.TEXT_DA_VO_DICH;
import static consts.ConstTournament.tournamentGems;
import static consts.ConstTournament.tournamentGolds;
import static consts.ConstTournament.tournamentNames;
import java.util.ArrayList;
import nro.npc.Npc;

public class WorldMartialArtsTournamentService extends ConstTournament {

    public static int getTournament() {
        int hours = TimeUtil.getCurrHour();
        switch (hours) {
            case 8:
            case 14:
            case 18: {
                return NHI_DONG;
            }
            case 9: 
            case 13:
            case 19: {
                return SIEU_CAP_1;
            }
            case 10:
            case 15:
            case 20: {
                return SIEU_CAP_2;
            }
            case 11:
            case 16:
            case 21: {
                return SIEU_CAP_3;
            }
            case 12:
            case 17:
            case 22:
            case 23: {
                return NGOAI_HANG;
            }
            default: {
                return -1;
            }
        }
    }

    public static int getNextTournamentTime() {
        int hours = TimeUtil.getCurrHour() + 1;
        if (hours > 23 || hours < 8) {
            hours = 8;
        }
        return hours;
    }

    public static String sayText() {
        return WorldMartialArtsTournamentManager.gI().canReg
                ? "Chào mừng bạn đến với đại hội võ thuật\nGiải " + tournamentNames[getTournament()] + " đang có " + WorldMartialArtsTournamentManager.gI().listReg.size() + " người đăng ký thi đấu"
                : "Đã hết hạn đăng ký thi đấu, xin vui lòng chờ đến giải sau vào lúc " + getNextTournamentTime() + "h";
    }

    public static void menu(Npc npc, Player player) {
        if (WorldMartialArtsTournamentManager.gI().round != 0 && WorldMartialArtsTournamentManager.gI().checkPlayer(player.id)) {
            NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, "Bạn được vào vòng " + (WorldMartialArtsTournamentManager.gI().round + 1) + "\nTrận tiếp theo sắp diễn ra, hãy đợi tại đây");
            return;
        }
        boolean canReg = WorldMartialArtsTournamentManager.gI().canReg;
        int tour = getTournament();
        ArrayList<String> menu = new ArrayList<>();
        menu.add("Thông tin\nChi tiết");
        if (canReg && tour != -1) {
            if (!regCheck(player)) {
                menu.add("Đăng kí");
            } else {
                menu.add("Hủy\nđăng kí");
            }
            menu.add("Giải\nSiêu Hạng");
            menu.add("Đại Hội\nVõ Thuật\nLần thứ\n23");
        } else {
            menu.add("Giải\nSiêu Hạng");
            menu.add("Đại Hội\nVõ Thuật\nLần thứ\n23");
            menu.add("Đóng");
        }
        npc.createOtherMenu(player, ConstNpc.BASE_MENU, sayText(), menu.toArray(String[]::new));
    }

    public static void confirm(Npc npc, Player player, int select) {
        boolean canReg = WorldMartialArtsTournamentManager.gI().canReg;
        int tour = getTournament();

        switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.DANGKYDHVT_CONFIRM: {
                if (select == 0 && canReg && tour != -1) {
                    dangky_huy(npc, player);
                }
                break;
            }
            case ConstNpc.BASE_MENU: {
                switch (select) {
                    case 0:
                        NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, ConstNpc.THONG_TIN_DAI_HOI_VO_THUAT);
                        break;
                    case 1: {
                        if (canReg && tour != -1) {
                            if (!regCheck(player)) {
                                ArrayList<String> menu = new ArrayList<>();
                                if (tour == NGOAI_HANG) {
                                    menu.add("Giải\n" + tournamentNames[tour] + "\n(" + tournamentGolds[tour] / 1000 + "k vàng)");
                                } else {
                                    menu.add("Giải\n" + tournamentNames[tour] + "\n(" + tournamentGems[tour] + " ngọc)");
                                }
                                menu.add("Từ chối");
                                npc.createOtherMenu(player, ConstNpc.DANGKYDHVT_CONFIRM, "Hiện đang có giải đấu " + tournamentNames[tour] + " bạn có muốn đăng ký không?", menu.toArray(String[]::new));
                            } else {
                                dangky_huy(npc, player);
                            }
                            break;
                        }
                        ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
                        break;
                    }
                    case 2: {
                        if (canReg) {
                            ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
                            break;
                        }
                        ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
                        break;
                    }
                    case 3: {
                        if (canReg) {
                            ChangeMapService.gI().changeMapNonSpaceship(player, 129, player.location.x, 360);
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public static boolean regCheck(Player player) {
        return WorldMartialArtsTournamentManager.gI().listReg.contains(player.id);
    }

    public static void dangky_huy(Npc npc, Player player) {
        if (WorldMartialArtsTournamentManager.gI().listChamp.contains(player.name)) {
            NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, TEXT_DA_VO_DICH);
            return;
        }
        if (!WorldMartialArtsTournamentManager.gI().listReg.contains(player.id)) {
            int tour = getTournament();
            int gold = tournamentGolds[tour];
            int gem = tournamentGems[tour];
            if (player.inventory.gold < gold) {
                NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, "Bạn không đủ vàng, còn thiếu " + (gold - player.inventory.gold) + " vàng nữa");
                return;
            }
            if (player.inventory.getGemAndRuby() < gem) {
                NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, "Bạn không đủ ngọc, còn thiếu " + (gem - player.inventory.getGemAndRuby()) + " ngọc nữa");
                return;
            }
            player.inventory.gold -= gold;
            player.inventory.subGemAndRuby(gem);
            Service.gI().sendMoney(player);
            WorldMartialArtsTournamentManager.gI().listReg.add(player.id);
            NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, ConstTournament.TEXT_DANG_KY_THANH_CONG.replaceAll("%1", TimeUtil.getCurrHour() + "").replaceAll("%2", TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin()));
        } else {
            WorldMartialArtsTournamentManager.gI().listReg.remove(player.id);
            NpcService.gI().createTutorial(player, npc.tempId, npc.avartar, ConstTournament.TEXT_HUY_DANG_KY);
        }
    }
}
