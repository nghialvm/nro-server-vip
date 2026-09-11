package nro.npc.ListNpc;



import Utils.Util;
import nro.services.Service;
import consts.ConstNpc;
import models.Item.Item;
import nro.inventory.InventoryService;
import nro.map.DragonNamecWar.TranhNgoc;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.TaskService;
import nro.shop.ShopService;
import nro.top.TopService;

public class TranhNgocNamek extends Npc {

    public TranhNgocNamek(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\n"
                + "Hãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                "Tham gia", "Đổi điểm\nThưởng\n[" + player.event.getNamekWarPoint() + "]", "Bảng\nxếp hạng","Hành tinh Cereal", "Từ chối");
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: {
                        if (TranhNgoc.gI().isTimeRegisterWar()) {
                            if (player.iDMark.getTranhNgoc() == -1) {
                                this.createOtherMenu(player, ConstNpc.REGISTER_TRANH_NGOC,
                                        "Ngọc rồng Namếc đang bị 2 thế lực tranh giành"
                                        + "\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\n|2|"
                                        + "Phe Ca đíc: " + TranhNgoc.gI().getPlayersCadic().size() + "\n|7|"
                                        + "Phe Fide: " + TranhNgoc.gI().getPlayersFide().size() + "\n"
                                        + "|6|Chú ý: Đăng kí xong phải online cho tới lúc tranh đấu, out game thì phải đăng kí lại!",
                                        "Tham gia phe Ca đíc", "Tham gia phe Fide", "Đóng");
                            } else {
                                this.createOtherMenu(player, ConstNpc.LOG_OUT_TRANH_NGOC,
                                        "Ngọc rồng Namếc đang bị 2 thế lực tranh giành"
                                        + "\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân\n|2|"
                                        + "Phe Ca đíc: " + TranhNgoc.gI().getPlayersCadic().size() + "\n|7|"
                                        + "Phe Fide: " + TranhNgoc.gI().getPlayersFide().size() + "\n"
                                        + "|6|Chú ý: Đăng kí xong phải online cho tới lúc tranh đấu, out game thì phải đăng kí lại!",
                                        "Hủy\nĐăng Ký", "Đóng");
                            }
                            return;
                        }
                        Service.gI().sendPopUpMultiLine(player, 0, 4335, "Sự kiện sẽ mở đăng ký vào lúc " + TranhNgoc.HOUR_REGISTER + ":" + TranhNgoc.MIN_REGISTER + "\nSự kiện sẽ bắt đầu vào " + TranhNgoc.HOUR_OPEN + ":" + TranhNgoc.MIN_OPEN + " và kết thúc vào " + TranhNgoc.HOUR_CLOSE + ":" + TranhNgoc.MIN_CLOSE);
                        break;
                    }
                    case 1:
                        ShopService.gI().opendShop(player, "SHOP_NAMEK_WAR", false);
                        break;
                    case 2:
                        TopService.showListTopNamecWar(player);
                        break;
                    case 3: {

                        if (TaskService.gI().getIdTask(player) < 28) {
                            Service.gI().sendThongBao(player, "Bạn cần hoàn thành nhiệm vụ 28 mới có thể đến Hành tinh Cereal!");
                            break;
                        }
                        Item thoiVang = InventoryService.gI().findItemBag(player, 457);
                        if (thoiVang == null || thoiVang.quantity < 5) {
                            Service.gI().sendThongBao(player, "Bạn cần 5 Thỏi vàng để vào Hành tinh Cereal!");
                            break;
                        }
                        InventoryService.gI().subQuantityItemsBag(player, thoiVang, 5);
                        InventoryService.gI().sendItemBag(player);

                        ChangeMapService.gI().changeMapNonSpaceship(player, 194, 200 + Util.nextInt(-100, 100), 192);
                        break;
                    }
                }
            }
            switch (player.iDMark.getIndexMenu()) {
                case ConstNpc.REGISTER_TRANH_NGOC: {
                    switch (select) {
                        case 0:
                            if (!player.getSession().actived) {
                                Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                return;
                            }
                            player.iDMark.setTranhNgoc((byte) 1);
                            TranhNgoc.gI().addPlayersCadic(player);
                            Service.gI().sendThongBao(player, "Đăng ký vào phe Xanh thành công");
                            break;
                        case 1:
                            if (!player.getSession().actived) {
                                Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                                return;
                            }
                            player.iDMark.setTranhNgoc((byte) 2);
                            TranhNgoc.gI().addPlayersFide(player);
                            Service.gI().sendThongBao(player, "Đăng ký vào phe Đỏ thành công");
                            break;
                    }
                    break;
                }
                case ConstNpc.LOG_OUT_TRANH_NGOC: {
                    switch (select) {
                        case 0:
                            player.iDMark.setTranhNgoc((byte) -1);
                            TranhNgoc.gI().removePlayersCadic(player);
                            TranhNgoc.gI().addPlayersFide(player);
                            Service.gI().sendThongBao(player, "Hủy đăng ký thành công");
                            break;
                    }
                    break;
                }
            }
        }
    }
}
