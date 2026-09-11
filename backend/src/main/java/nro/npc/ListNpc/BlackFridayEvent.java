package nro.npc.ListNpc;



import Utils.TimeUtil;
import consts.ConstNpc;
import event.EventManager;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;

public class BlackFridayEvent extends Npc {

    public BlackFridayEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.BLACK_FRIDAY) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Black Friday vui vẻ!",
                        "FLASH\nSALE",
                        "Black\nFriday",
                        "Đổi điểm\n[" + player.event.getBlackFridayPoint() + "]",
                        "Đua Top",
                        "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.BLACK_FRIDAY) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0: {
                            if (TimeUtil.isFlashSale()) {
                                this.createOtherMenu(player, 0,
                                        "Thời gian Flash Sale sự kiện BLACK FRIDAY\n"
                                        + "Sáng từ 09:00 đến 09:30\n"
                                        + "Tối từ 21:00 đến 21:30",
                                        "Đồng ý",
                                        "Đóng");
                            } else {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "Thời gian Flash Sale sự kiện BLACK FRIDAY\n"
                                        + "Sáng từ 09:00 đến 09:30\n"
                                        + "Tối từ 21:00 đến 21:30",
                                        "Đóng");
                            }
                            break;
                        }

                        case 1: {
                            ShopService.gI().opendShop(player, "BLACK_FRIDAY_EVENT", true);
                            break;
                        }

                        case 2: {
                            ShopService.gI().opendShop(player, "BLACK_FRIDAY_EVENT2", true);
                            break;
                        }

                        case 3: {
                            this.createOtherMenu(player, 1,
                                    "Đua Top Sự Kiện BLACK FRIDAY\n"
                                    + "Mua vật phẩm trong thời gian Flash Sale sẽ nhận được điểm sự kiện\n"
                                    + "Khi mua vật phẩm với giá bao nhiêu thì bạn nhận bấy nhiêu điểm sự kiện\n",
                                    "Top\nMở Hộp\nBlack Friday",
                                    "Top\nMua Sắm",
                                    "Đóng");
                            break;
                        }
                    }
                } else if (player.iDMark.getIndexMenu() == 0) {
                    switch (select) {
                        case 0: {
                            ShopService.gI().opendShop(player, "BLACK_FRIDAY_SALE", true);
                            break;
                        }
                    }
                } else if (player.iDMark.getIndexMenu() == 1) {
                    switch (select) {
                        case 0: {
                            TopService.showListTopMoHopBlackFriday(player);
                            break;
                        }

                        case 1: {
                            TopService.showListTopMuaSamBlackFriday(player);
                            break;
                        }
                    }
                }
            }
        }
    }
}