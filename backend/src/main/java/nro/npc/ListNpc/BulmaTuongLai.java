package nro.npc.ListNpc;

import consts.ConstNpc;
import consts.ConstTask;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.NpcService;
import nro.services.TaskService;
import nro.shop.ShopService;

public class BulmaTuongLai extends Npc {

    public BulmaTuongLai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_29_1) {
                    this.createOtherMenu(
                            player,
                            ConstNpc.BASE_MENU,
                            "Cảm ơn bạn đã đến đây giúp chúng tôi",
                            "Kể chuyện",
                            "Cửa hàng"
                    );
                } else {
                    this.createOtherMenu(
                            player,
                            ConstNpc.BASE_MENU,
                            "Cảm ơn bạn đã đến đây giúp chúng tôi",
                            "Kể chuyện"
                    );
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0 ->
                            NpcService.gI().createTutorial(
                                    player,
                                    this.tempId,
                                    this.avartar,
                                    ConstNpc.CALICK_KE_CHUYEN
                            );
                    case 1 -> ShopService.gI().opendShop(player, "bummatuonglai", true);
                    default -> {
                    }
                }
            }
        }
    }
}
