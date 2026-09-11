package nro.npc.ListNpc;

import Utils.Util;
import consts.ConstNpc;
import jbcd.data.DatabaseUpdater;
import models.Item.Item;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Service;

public class ToriBot extends Npc {

    public ToriBot(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) return;
        if (player.getSession() == null) return;
        createOtherMenu(player, ConstNpc.BASE_MENU,
                "|7|MÙA VIP đang diễn ra!\n"
                + "|0|Tạo nhân vật mới được X2 EXP vĩnh viễn.\n"
                + "Nâng cấp VIP nhận quà khủng + ưu đãi vĩnh viễn.\n"
                + "Giới hạn 20 lần nâng VIP. (Đã mua: " + player.getSession().Vip_Point + "/20 lần)",
                "VIP 1", "VIP 2", "VIP 3", "VIP 4", "Đóng");
    }

    private void giveItem(Player player, short id, int quantity, int[][] options) {
        Item item = ItemService.gI().createNewItem(id, quantity);
        if (options != null) {
            for (int[] opt : options) {
                item.addOptionParam(opt[0], opt[1]);
            }
        }
        InventoryService.gI().addItemBag(player, item);
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) return;

        // Mở menu mô tả quà tặng VIP
        if (player.iDMark.isBaseMenu()) {
            switch (select) {
                case 0:
                    showVipMenu(player, 1);
                    break;
                case 1:
                    showVipMenu(player, 2);
                    break;
                case 2:
                    showVipMenu(player, 3);
                    break;
                case 3:
                    showVipMenu(player, 4);
                    break;
            }
        }

        // Nâng cấp thực tế
        int menu = player.iDMark.getIndexMenu();
        if (menu == 100 && select == 0) buyVip1(player);
        if (menu == 101 && select == 0) buyVip2(player);
        if (menu == 102 && select == 0) buyVip3(player);
        if (menu == 103 && select == 0) buyVip4(player);
    }

    // ================= MENU MÔ TẢ QUÀ =================
    private void showVipMenu(Player player, int level) {
        String msg = switch (level) {
            case 1 -> "|7|Nâng cấp VIP 1 bạn sẽ nhận được:\n"
                    + "|0| X3 Exp vĩnh viễn\n"
                    + "- 99 Thỏi vàng, 1000 Hồng ngọc\n"
                    + "- 3 phiếu giảm giá 80%\n"
                    + "- 5 Mảnh Rồng thần Namếc\n"
                    + "- Ngẫu nhiên đá may mắn và đá nâng cấp x5\n"
                    + "- Đá bảo vệ khoá x10, Sách cũ x10\n"
                    + "- Thú cưỡi Mèo hoá xương 30 ngày\n"
                    + "- Cải trang Jacky Chun 30 ngày\n"
                    + "- Hoa hồng 30 ngày, Pet Heo Bướm 30 ngày\n"
                    + "- Cải trang Gohan kính mát vĩnh viễn\n"
                    + "- Pet Shimo vĩnh viễn\n"
                    + "- Hộp quà Goku Day VIP x10\n"
                    + "- Hộp quà Cađíc VIP x10\n"
                    + "\n|0|Yêu cầu: 500 điểm VIP (Hiện có: " + player.inventory.Exp_Vip + ")";
            case 2 -> "|7|Nâng cấp VIP 2 bạn sẽ nhận được:\n"
                    + "|0| X3 Exp vĩnh viễn\n"
                    + "- 200 Thỏi vàng, 2000 Hồng ngọc\n"
                    + "- 5 phiếu giảm giá 80%, 10 Mảnh Rồng thần Namếc\n"
                    + "- Đá may mắn + nâng cấp x10, Sách cũ x15, Đá bảo vệ x15\n"
                    + "- Cải trang Kirin múa lân, Cờ Olympic, Tàu Ngầm 19 Cam, Song mã hoàng gia (vĩnh viễn)\n"
                    + "- Cải trang Gogeta 365 ngày\n"
                    + "- 50 Phiếu đổi Capsule\n"
                    + "- Hộp quà Goku Day, Bé Ba, Cađíc VIP x10 mỗi loại\n"
                    + "\n|0|Yêu cầu: 1250 điểm VIP (Hiện có: " + player.inventory.Exp_Vip + ")";
            case 3 -> "|7|Nâng cấp VIP 3 bạn sẽ nhận được:\n"
                    + "|0| X3 Exp vĩnh viễn\n"
                    + "- 500 Thỏi vàng, 5000 Hồng ngọc\n"
                    + "- 10 phiếu giảm giá 80%, 15 Mảnh Rồng thần Namếc\n"
                    + "- Đá may mắn + nâng cấp x30, Đá bảo vệ x50, Sách cũ x50\n"
                    + "- Cải trang Android 21, Lồng đèn kéo quân, Pet Albart Cup (vĩnh viễn)\n"
                    + "- Cải trang Gohan đi biển, Lý Tiểu Nương Rực Rỡ 365 ngày\n"
                    + "- Hộp quà Goku Day, Bé Ba, Cađíc VIP x30 mỗi loại\n"
                    + "- 100 Phiếu đổi Capsule\n"
                    + "\n|0|Yêu cầu: 3500 điểm VIP (Hiện có: " + player.inventory.Exp_Vip + ")";
            case 4 -> "|7|Nâng cấp VIP 4 bạn sẽ nhận được:\n"
                    + "|0| X3 Exp vĩnh viễn\n"
                    + "- 999 Thỏi vàng, 9999 Hồng ngọc\n"
                    + "- 10 phiếu giảm giá 80%, 20 Mảnh Rồng thần Namếc\n"
                    + "- Đá may mắn + nâng cấp x50, Đá bảo vệ x99, Sách cũ x99\n"
                    + "- Cải trang Android 21, Hằng Nga, Chichi Võ Đài, Song mã hoàng gia (vĩnh viễn)\n"
                    + "- Pet Capybara hồng, Thuyền Âu Lạc, Cánh Thiên Thần - Ác Quỷ (vĩnh viễn)\n"
                    + "- Hộp quà Goku Day, Bé Ba, Cađíc VIP x50 mỗi loại\n"
                    + "- 200 Phiếu đổi Capsule\n"
                    + "\n|0|Yêu cầu: 8000 điểm VIP (Hiện có: " + player.inventory.Exp_Vip + ")";
            default -> "Thông tin VIP không tồn tại!";
        };
        createOtherMenu(player, 99 + level, msg, "Nâng cấp", "Đóng");
    }

    // ================== VIP NÂNG CẤP ==================
    private void buyVip1(Player player) {
        if (!canBuy(player, 500, 18)) return;
        DatabaseUpdater.addVip_byPlayer(player, 1);
        player.inventory.subExpVip(500);
        Service.gI().sendVipExp(player);
        giveItem(player, (short) 457, 99, new int[][]{{30, 0}});
        player.inventory.addRuby(1000);
        giveItem(player, (short) 459, 3, new int[][]{{112, 80}, {93, 90}, {30, 0}});
        giveItem(player, (short) 1204, 5, new int[][]{{87, 0}, {30, 0}});
        for (int i = 0; i < 5; i++) giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
        giveItem(player, (short) 1143, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1283, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1592, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1838, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1345, 1, new int[][]{{50, 10}, {77, 10}, {103, 10}, {84, 0}, {93, 30}});
        giveItem(player, (short) 711, 1, new int[][]{{50, 23}, {77, 21}, {103, 21}, {159, 4}, {160, 50}, {93, 30}});
        giveItem(player, (short) 954, 1, new int[][]{{50, 15}, {77, 15}, {103, 15}, {94, 12}, {93, 30}});
        giveItem(player, (short) 1256, 1, new int[][]{{50, 16}, {77, 18}, {103, 16}, {80, 5}, {93, 30}});
        giveItem(player, (short) 1667, 1, new int[][]{{50, 25}, {77, 26}, {103, 26}, {114, 25}, {95, 10}, {108, 8}, {101, 20}});
        giveItem(player, (short) 1568, 1, new int[][]{{50, 16}, {77, 18}, {103, 18}, {94, 7}, {14, 5}});
        done(player, 1);
    }

    private void buyVip2(Player player) {
        if (!canBuy(player, 1250, 25)) return;
        DatabaseUpdater.addVip_byPlayer(player, 1);
        player.inventory.subExpVip(1250);
        Service.gI().sendVipExp(player);
        giveItem(player, (short) 457, 200, new int[][]{{30, 0}});
        player.inventory.addRuby(2000);
        giveItem(player, (short) 459, 5, new int[][]{{112, 80}, {93, 90}, {30, 0}});
        giveItem(player, (short) 1204, 10, new int[][]{{87, 0}, {30, 0}});
        for (int i = 0; i < 10; i++) giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
        giveItem(player, (short) 1143, 15, new int[][]{{30, 0}});
        giveItem(player, (short) 1283, 15, new int[][]{{30, 0}});
        giveItem(player, (short) 1592, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1770, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1838, 10, new int[][]{{30, 0}});
        giveItem(player, (short) 1690, 50, new int[][]{{30, 0}});
        giveItem(player, (short) 1684, 1, new int[][]{{50, 25}, {101, 70}, {95, 15}});
        giveItem(player, (short) 1679, 1, new int[][]{{50, 18}, {77, 16}, {103, 16}, {94, 12}});
        giveItem(player, (short) 1554, 1, new int[][]{{50, 10}, {77, 10}, {103, 10}, {85, 0}, {114, 100}});
        giveItem(player, (short) 1513, 1, new int[][]{{50, 8}, {77, 9}, {103, 8}, {85, 0}});
        giveItem(player, (short) 1697, 1, new int[][]{{50, 29}, {77, 29}, {103, 29}, {94, 12}, {108, 15}, {97, 15}, {93, 365}});
        done(player, 2);
    }

    private void buyVip3(Player player) {
        if (!canBuy(player, 3500, 27)) return;
        DatabaseUpdater.addVip_byPlayer(player, 1);
        player.inventory.subExpVip(3500);
        Service.gI().sendVipExp(player);
        giveItem(player, (short) 457, 500, new int[][]{{30, 0}});
        player.inventory.addRuby(5000);
        giveItem(player, (short) 459, 10, new int[][]{{112, 80}, {93, 90}, {30, 0}});
        giveItem(player, (short) 1204, 15, new int[][]{{87, 0}, {30, 0}});
        for (int i = 0; i < 30; i++) giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
        giveItem(player, (short) 1143, 50, new int[][]{{30, 0}});
        giveItem(player, (short) 1283, 50, new int[][]{{30, 0}});
        //giveItem(player, (short) 1537, 1, new int[][]{{30, 0}});
        giveItem(player, (short) 1592, 30, new int[][]{{30, 0}});
        giveItem(player, (short) 1770, 30, new int[][]{{30, 0}});
        giveItem(player, (short) 1838, 30, new int[][]{{30, 0}});
        giveItem(player, (short) 1690, 100, new int[][]{{30, 0}});
        giveItem(player, (short) 1789, 1, new int[][]{{50, Util.nextInt(21, 24)}, {77, Util.nextInt(25, 28)}, {103, Util.nextInt(20, 25)}, {8, 4}, {94, 15}});
        giveItem(player, (short) 1675, 1, new int[][]{{50, 18}, {77, 15}, {103, 15}, {14, 10}, {101, 20}});
        giveItem(player, (short) 1625, 1, new int[][]{{50, 8}, {77, 7}, {103, 10}, {14, 5}});
        giveItem(player, (short) 1597, 1, new int[][]{{50, 15}, {77, 15}, {94, 5}, {5, 8}});
        giveItem(player, (short) 1657, 1, new int[][]{{50, 24}, {77, 24}, {103, 24}, {204, 10}, {95, 15}, {236, 5}});
        giveItem(player, (short) 1755, 1, new int[][]{{50, 26}, {77, 25}, {103, 25}, {117, 10}, {14, 15}, {93, 365}});
        done(player, 3);
    }

    private void buyVip4(Player player) {
        if (!canBuy(player, 8000, 28)) return;
        DatabaseUpdater.addVip_byPlayer(player, 1);
        player.inventory.subExpVip(8000);
        Service.gI().sendVipExp(player);
        giveItem(player, (short) 457, 999, new int[][]{{30, 0}});
        player.inventory.addRuby(9999);
        giveItem(player, (short) 459, 10, new int[][]{{112, 80}, {93, 90}, {30, 0}});
        giveItem(player, (short) 1204, 20, new int[][]{{87, 0}, {30, 0}});
        for (int i = 0; i < 50; i++) giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
        giveItem(player, (short) 1143, 99, new int[][]{{30, 0}});
        giveItem(player, (short) 1283, 99, new int[][]{{30, 0}});
        //giveItem(player, (short) 1538, 1, new int[][]{{30, 0}});
        giveItem(player, (short) 1592, 50, new int[][]{{30, 0}});
        giveItem(player, (short) 1770, 50, new int[][]{{30, 0}});
        giveItem(player, (short) 1838, 50, new int[][]{{30, 0}});
        giveItem(player, (short) 1690, 200, new int[][]{{30, 0}});
        giveItem(player, (short) 1789, 1, new int[][]{{50, Util.nextInt(21, 24)}, {77, Util.nextInt(25, 28)}, {103, Util.nextInt(20, 25)}, {8, 4}, {94, 15}});
        giveItem(player, (short) 1668, 1, new int[][]{{50, 15}, {77, 15}, {103, 17}, {101, 20}});
        giveItem(player, (short) 1534, 1, new int[][]{{50, 9}, {77, 8}, {108, 8}, {80, 5}, {85, 0}});
        giveItem(player, (short) 1786, 1, new int[][]{{50, 24}, {77, 23}, {103, 23}, {5, 12}, {95, 10}, {226, 10}});
        giveItem(player, (short) 1814, 1, new int[][]{{50, 12}, {77, 11}, {103, 10}, {80, 5}, {89, 0}});
        giveItem(player, (short) 1700, 1, new int[][]{{50, Util.nextInt(23, 26)}, {77, 21}, {103, 21}, {117, 13}, {14, 15}});
        giveItem(player, (short) 1803, 1, new int[][]{{50, 17}, {77, 15}, {103, 15}, {5, 10}, {94, 7}});
        done(player, 4);
    }

    private boolean canBuy(Player player, int required, int emptySlot) {
        if (player.getSession().Vip_Point > 19) {
            Service.gI().sendThongBao(player, "Đã đạt giới hạn 20 lần mua VIP!");
            return false;
        }
        if (player.inventory == null || player.inventory.Exp_Vip < required) {
            Service.gI().sendThongBao(player, "Không đủ điểm VIP (cần " + required + ", hiện có " + player.inventory.Exp_Vip + ")");
            return false;
        }
        if (InventoryService.gI().getCountEmptyBag(player) < emptySlot) {
            Service.gI().sendThongBao(player, "Cần ít nhất " + emptySlot + " ô trống trong hành trang.");
            return false;
        }
        return true;
    }

    private void done(Player player, int level) {
        Service.gI().sendMoney(player);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Mua thành công VIP " + level + "! Tổng lượt: " + player.getSession().Vip_Point + "/20.");
    }
}
