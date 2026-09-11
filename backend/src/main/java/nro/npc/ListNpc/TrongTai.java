package nro.npc.ListNpc;



import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import consts.ConstNpc;
import nro.map.RankSuper.SuperRankManager;
import nro.map.RankSuper.SuperRankService;
import nro.npc.Npc;
import nro.player.Player;

public class TrongTai extends Npc {

    public TrongTai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (mapId) {
                case 113: {
                    if (SuperRankManager.gI().awaiting(player)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Vui lòng chờ, số thứ tự của bạn là " + SuperRankManager.gI().ordinal(player.id), "OK", "Về\nĐại Hội\nVõ Thuật");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đại hội võ thuật Siêu Hạng\ndiễn ra 24/7 kể cả ngày lễ và chủ nhật\nHãy thi đấu ngay để khẳng định đẳng cấp của mình nhé",
                        "Top 100\nCao Thủ", "Hướng\ndẫn\nthêm", player.superRank.ticket > 0 ? "Miễn phí\nCòn " + player.superRank.ticket + " vé" : "Thi đấu", "Ưu tiên\nđấu ngay", "Về\nĐại Hội\nVõ Thuật");
                    break;
                }
                default:
                    super.openBaseMenu(player);
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (mapId) {
                    case 113: {
                        if (SuperRankManager.gI().awaiting(player)) {
                            if (select == 1) {
                                ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                            }
                            return;
                        }
                        switch (select) {
                            case 0:
                                SuperRankService.gI().topList(player, 0);
                                break;
                            case 1:
                                NpcService.gI().createTutorial(player, tempId, avartar, ConstNpc.THONG_TIN_SIEU_HANG);
                                break;
                            case 2:
                                SuperRankService.gI().topList(player, 1);
                                break;
                            case 3:
                                SuperRankService.gI().topList(player, 2);
                                break;
                            case 4:
                                ChangeMapService.gI().changeMapNonSpaceship(player, 52, player.location.x, 336);
                                break;
                            default: {
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
