package nro.npc.ListNpc;

import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.DetuService;
import nro.services.Service;

public class Unknown extends Npc {

    public Unknown(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (player.getSession() == null) {
                return;
            }
            createOtherMenu(player, ConstNpc.BASE_MENU, "|7|Đang trong thời gian mùa VIP diễn ra\n"
                    + "|0|Tạo nhân vật mới sẽ được X2 Kinh nghiệm vĩnh viễn\n"
                    + "Nếu nâng cấp VIP sẽ được nhận\n"
                    + "nhiều ưu đãi hơn nữa\n"
                    + "Lưu ý: nâng cấp VIP chỉ được 20 lần " + (player.getSession().Vip_Point > 0 ? "(Bạn đã mua: " + player.getSession().Vip_Point + "/20 lần)" : ""), 
                    "VIP 1", "VIP 2", "VIP 3", "VIP 4", "Đóng");
        }
    }
    
    private void giveItem(Player player, short id, int quantity, int[][] options) {
        Item item = ItemService.gI().createNewItem(id, quantity);
        if (options != null) {
            for (int[] opt : options) {
                item.addOptionParam(opt[0], opt[1]);
            }
        }
        InventoryService.gI().addItemBag(player, item);
        Service.gI().sendThongBao(player, "Bạn nhận được x" + quantity + " " + item.Name());
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: 
                        createOtherMenu(player, 0, "|7|Nâng cấp VIP 1 bạn sẽ nhận được\n"
                                + "|0|X3 Exp vĩnh viễn. Tặng 1 đệ tử\n"
                                + "- Được tặng 99 Thỏi vàng, 1000 Hồng ngọc\n"
                                + "- 3 phiếu giảm giá 80%\n"
                                + "- 5 Mảnh Rồng thần Namếc\n"
                                + "- Ngẫu nhiên đá may mắn và đá nâng cấp x5\n"
                                + "- Đá bảo vệ khoá x10 và Sách cũ x10\n"
                                + "- Thú cưỡi Mèo hoá xương 30 ngày\n"
                                + "- Cải trang Jacky chun 30 ngày\n"
                                + "- Hoa hồng 30 ngày. Pet Heo Bướm 30 ngày\n"
                                + "- Cải trang Gohan kính mát vĩnh viễn\n"
                                + "- Pet Shimo vĩnh viễn\n"
                                + "- Hộp quà Goku Day VIP x10\n"
                                + "- Hộp quà Cađíc VIP x10",
                                "500\nĐiểm\n[" + player.inventory.Exp_Vip + ']', "Đóng");
                        break;
                    case 1: 
                        createOtherMenu(player, 1, "|7|Nâng cấp VIP 2 bạn sẽ nhận được\n"
                                + "|0|X3 Exp vĩnh viễn. Tặng 1 đệ tử\n"
                                + "- Được tặng 200 Thỏi vàng, 2000 Hồng ngọc\n"
                                + "- 5 phiếu giảm giá 80% và 10 Mảnh Rồng thần Namếc\n"
                                + "- Ngẫu nhiên đá may mắn và đá nâng cấp x10\n"
                                + "- Đá bảo vệ khoá x15 và Sách cũ x15\n"
                                + "- 1 Capsule 1 món set kích hoạt\n"
                                + "- Cải trang Kirin múa lân vĩnh viễn\n"
                                + "- Cờ Olympic vĩnh viễn\n"
                                + "- Tàu Ngầm 19 Cam vĩnh viễn\n"
                                + "- Song mã hoàng gia vĩnh viễn\n"
                                + "- Hộp quà Goku Day VIP x10\n"
                                + "- Hộp mù Bé Ba VIP x10\n"
                                + "- Hộp quà Cađíc VIP x10\n"
                                + "- Cải trang Gogeta 365 ngày\n"
                                + "- 50 Phiếu đổi Capsule",
                                "1250\nĐiểm\n[" + player.inventory.Exp_Vip + ']', "Đóng");
                        break;
                    case 2: 
                        createOtherMenu(player, 2, "|7|Nâng cấp VIP 3 bạn sẽ nhận được\n"
                                + "|0|X3 Exp vĩnh viễn. Tặng 1 đệ tử\n"
                                + "- Được tặng 500 Thỏi vàng, 5000 Hồng ngọc\n"
                                + "- 10 phiếu giảm giá 80% và 15 Mảnh Rồng thần Namếc\n"
                                + "- Ngẫu nhiên đá may mắn và đá nâng cấp x30\n"
                                + "- Đá bảo vệ khoá x50 và Sách cũ x50\n"
                                + "- 5 Capsule 1 món set kích hoạt\n"
                                + "- Hộp quà Set kích hoạt 3 sao x1\n"
                                + "- Cải trang Android 21 thân thiện vĩnh viễn\n"
                                + "- Lồng đèn kéo quân NRO vĩnh viễn\n"
                                + "- Tivi bay vĩnh viễn\n"
                                + "- Pet albart Cup vĩnh viễn\n"
                                + "- Cải trang Gohan đi biển vĩnh viễn\n"
                                + "- Hộp quà Goku Day VIP x30\n"
                                + "- Hộp mù Bé Ba VIP x30\n"
                                + "- Hộp quà Cađíc VIP x30\n"
                                + "- Cải trang Lý Tiểu Nương Rực Rỡ 365 ngày\n"
                                + "- 100 Phiếu đổi Capsule",
                                "3500\nĐiểm\n[" + player.inventory.Exp_Vip + ']', "Đóng");
                        break;
                    case 3: 
                        createOtherMenu(player, 3, "|7|Nâng cấp VIP 4 bạn sẽ nhận được\n"
                                + "|0|X3 Exp vĩnh viễn. Tặng 1 đệ tử\n"
                                + "- Được tặng 999 Thỏi vàng, 9999 Hồng ngọc\n"
                                + "- 10 phiếu giảm giá 80% và 20 Mảnh Rồng thần Namếc\n"
                                + "- Ngẫu nhiên đá may mắn và đá nâng cấp x50\n"
                                + "- Đá bảo vệ khoá x99 và Sách cũ x99\n"
                                + "- 10 Capsule 1 món set kích hoạt\n"
                                + "- Hộp quà Set kích hoạt 5 sao x1\n"
                                + "- Cải trang Android 21 thân thiện vĩnh viễn\n"
                                + "- Pet Capybara hồng vĩnh viễn\n"
                                + "- Thuyền âu lạc vĩnh viễn\n"
                                + "- Cải trang Chichi Võ đài vĩnh viễn\n"
                                + "- Thú cưỡi Thích Kim Quy vĩnh viễn\n"
                                + "- Cải trang Hằng nga vĩnh viễn\n"
                                + "- Cánh thiên thần - Ác quỷ vĩnh viễn\n"
                                + "- Hộp quà Goku Day VIP x50\n"
                                + "- Hộp mù Bé Ba VIP x50\n"
                                + "- Hộp quà Cađíc VIP x50\n"
                                + "- 200 Phiếu đổi Capsule",
                                "8000\nĐiểm\n[" + player.inventory.Exp_Vip + ']', "Đóng");
                        break;
                    default:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 0) {
                switch (select) {
                    case 0:
                        if (player.inventory == null) {
                            Service.gI().sendThongBao(player, "Không thể thực hiện");
                            return;
                        }
                        if (player.inventory.Exp_Vip < 500) {
                            Service.gI().sendThongBao(player, "Bạn không đủ điểm, còn thiếu " + (500 - player.inventory.Exp_Vip) + " điểm nữa.");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 18) {
                            Service.gI().sendThongBao(player, "Cần ít nhất 18 ô trống trong hành trang");
                            return;
                        }
                        if (player.Detu != null) {
                            createOtherMenu(player, 4, "Bạn đang có đệ tử,\n"
                                    + "bạn có muốn lấy đệ tử mới (sơ sinh)\n"
                                    + "theo tộc mình không ?\n"
                                    + "Nhớ thu lại trang bị đệ tử trước khi đổi.",
                                    "Đồng ý", "Không\nnhận\nĐệ tử mới", "Từ chối");
                            return;
                        }
                        DetuService.gI().createNormalPet(player, player.gender);
                        player.inventory.subExpVip(500);
                        Service.gI().sendVipExp(player);
                        giveItem(player, (short) 457, 99, new int[][]{{30, 0}});
                        player.inventory.addRuby(1000);
                        Service.gI().sendThongBao(player, "Bạn nhận được 1000 Hồng ngọc");
                        giveItem(player, (short) 459, 3, new int[][]{{112, 80}, {93, 90}, {30, 0}});
                        giveItem(player, (short) 1204, 5, new int[][]{{87, 0}, {30, 0}});
                        for (int i = 0; i < 5; i++) {
                            giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
                        }
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
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                        player.getSession().Vip_Point++;
                        break;
                    default:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 1) {
                switch (select) {
                    case 0:
                        if (player.inventory == null) {
                            Service.gI().sendThongBao(player, "Không thể thực hiện");
                            return;
                        }
                        if (player.inventory.Exp_Vip < 1250) {
                            Service.gI().sendThongBao(player, "Bạn không đủ điểm, còn thiếu " + (1250 - player.inventory.Exp_Vip) + " điểm nữa.");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 25) {
                            Service.gI().sendThongBao(player, "Cần ít nhất 25 ô trống trong hành trang");
                            return;
                        }
                        if (player.Detu != null) {
                            createOtherMenu(player, 4, "Bạn đang có đệ tử,\n"
                                    + "bạn có muốn lấy đệ tử mới (sơ sinh)\n"
                                    + "theo tộc mình không ?\n"
                                    + "Nhớ thu lại trang bị đệ tử trước khi đổi.",
                                    "Đồng ý", "Không\nnhận\nĐệ tử mới", "Từ chối");
                            return;
                        }
                        DetuService.gI().createNormalPet(player, player.gender);
                        player.inventory.subExpVip(1250);
                        Service.gI().sendVipExp(player);
                        giveItem(player, (short) 457, 200, new int[][]{{30, 0}});
                        player.inventory.addRuby(2000);
                        Service.gI().sendThongBao(player, "Bạn nhận được 2000 Hồng ngọc");
                        giveItem(player, (short) 459, 5, new int[][]{{112, 80}, {93, 90}, {30, 0}});
                        giveItem(player, (short) 1204, 10, new int[][]{{87, 0}, {30, 0}});
                        for (int i = 0; i < 10; i++) {
                            giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
                        }
                        giveItem(player, (short) 1143, 15, new int[][]{{30, 0}});
                        giveItem(player, (short) 1283, 15, new int[][]{{30, 0}});
                        giveItem(player, (short) 1559, 1, new int[][]{{30, 0}});
                        giveItem(player, (short) 1592, 10, new int[][]{{30, 0}});
                        giveItem(player, (short) 1770, 10, new int[][]{{30, 0}});
                        giveItem(player, (short) 1838, 10, new int[][]{{30, 0}});
                        giveItem(player, (short) 1690, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1684, 1, new int[][]{{50, 25}, {101, 70}, {95, 15}});
                        giveItem(player, (short) 1679, 1, new int[][]{{50, 18}, {77, 16}, {103, 16}, {94, 12}});
                        giveItem(player, (short) 1554, 1, new int[][]{{50, 10}, {77, 10}, {103, 10}, {85, 0}, {114, 100}});
                        giveItem(player, (short) 1513, 1, new int[][]{{50, 8}, {77, 9}, {103, 8}, {85, 0}});
                        giveItem(player, (short) 1697, 1, new int[][]{{50, 29}, {77, 29}, {103, 29}, {94, 12}, {108, 15}, {97, 15}, {93, 365}});
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                        player.getSession().Vip_Point++;
                        break;
                    default:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 2) {
                switch (select) {
                    case 0:
                        if (player.inventory == null) {
                            Service.gI().sendThongBao(player, "Không thể thực hiện");
                            return;
                        }
                        if (player.inventory.Exp_Vip < 3500) {
                            Service.gI().sendThongBao(player, "Bạn không đủ điểm, còn thiếu " + (3500 - player.inventory.Exp_Vip) + " điểm nữa.");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 27) {
                            Service.gI().sendThongBao(player, "Cần ít nhất 27 ô trống trong hành trang");
                            return;
                        }
                        if (player.Detu != null) {
                            createOtherMenu(player, 4, "Bạn đang có đệ tử,\n"
                                    + "bạn có muốn lấy đệ tử mới (sơ sinh)\n"
                                    + "theo tộc mình không ?\n"
                                    + "Nhớ thu lại trang bị đệ tử trước khi đổi.",
                                    "Đồng ý", "Không\nnhận\nĐệ tử mới", "Từ chối");
                            return;
                        }
                        DetuService.gI().createNormalPet(player, player.gender);
                        player.inventory.subExpVip(3500);
                        Service.gI().sendVipExp(player);
                        giveItem(player, (short) 457, 500, new int[][]{{30, 0}});
                        player.inventory.addRuby(5000);
                        Service.gI().sendThongBao(player, "Bạn nhận được 5000 Hồng ngọc");
                        giveItem(player, (short) 459, 10, new int[][]{{112, 80}, {93, 90}, {30, 0}});
                        giveItem(player, (short) 1204, 15, new int[][]{{87, 0}, {30, 0}});
                        for (int i = 0; i < 30; i++) {
                            giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
                        }
                        giveItem(player, (short) 1143, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1283, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1559, 5, new int[][]{{30, 0}});
                        giveItem(player, (short) 1537, 1, new int[][]{{30, 0}});
                        giveItem(player, (short) 1592, 30, new int[][]{{30, 0}});
                        giveItem(player, (short) 1770, 30, new int[][]{{30, 0}});
                        giveItem(player, (short) 1838, 30, new int[][]{{30, 0}});
                        giveItem(player, (short) 1690, 100, new int[][]{{30, 0}});
                        giveItem(player, (short) 1789, 1, new int[][]{{50, Util.nextInt(21, 24)},{77, Util.nextInt(25, 28)},{103, Util.nextInt(20, 25)},{8, 4}, {94, 15}});
                        giveItem(player, (short) 1675, 1, new int[][]{{50, 18}, {77, 15}, {103, 15}, {14, 10}, {101, 20}});
                        giveItem(player, (short) 1625, 1, new int[][]{{50, 8}, {77, 7}, {103, 10}, {14, 5}});
                        giveItem(player, (short) 1597, 1, new int[][]{{50, 15}, {77, 15}, {94, 5}, {5, 8}});
                        giveItem(player, (short) 1657, 1, new int[][]{{50, 24}, {77, 24}, {103, 24}, {204, 10}, {95, 15}, {236, 5}});
                        giveItem(player, (short) 1755, 1, new int[][]{{50, 26}, {77, 25}, {103, 25}, {117, 10}, {14, 15}, {93, 365}});
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                        player.getSession().Vip_Point++;
                        break;
                    default:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 3) {
                switch (select) {
                    case 0:
                        if (player.inventory == null) {
                            Service.gI().sendThongBao(player, "Không thể thực hiện");
                            return;
                        }
                        if (player.inventory.Exp_Vip < 8000) {
                            Service.gI().sendThongBao(player, "Bạn không đủ điểm, còn thiếu " + (8000 - player.inventory.Exp_Vip) + " điểm nữa.");
                            return;
                        }
                        if (InventoryService.gI().getCountEmptyBag(player) < 28) {
                            Service.gI().sendThongBao(player, "Cần ít nhất 28 ô trống trong hành trang");
                            return;
                        }
                        if (player.Detu != null) {
                            createOtherMenu(player, 4, "Bạn đang có đệ tử,\n"
                                    + "bạn có muốn lấy đệ tử mới (sơ sinh)\n"
                                    + "theo tộc mình không ?\n"
                                    + "Nhớ thu lại trang bị đệ tử trước khi đổi.",
                                    "Đồng ý", "Không\nnhận\nĐệ tử mới", "Từ chối");
                            return;
                        }
                        DetuService.gI().createNormalPet(player, player.gender);
                        player.inventory.subExpVip(8000);
                        Service.gI().sendVipExp(player);
                        giveItem(player, (short) 457, 999, new int[][]{{30, 0}});
                        player.inventory.addRuby(9999);
                        Service.gI().sendThongBao(player, "Bạn nhận được 9999 Hồng ngọc");
                        giveItem(player, (short) 459, 10, new int[][]{{112, 80}, {93, 90}, {30, 0}});
                        giveItem(player, (short) 1204, 20, new int[][]{{87, 0}, {30, 0}});
                        for (int i = 0; i < 50; i++) {
                            giveItem(player, (short) Util.nextInt(1074, 1083), 1, null);
                        }
                        giveItem(player, (short) 1143, 99, new int[][]{{30, 0}});
                        giveItem(player, (short) 1283, 99, new int[][]{{30, 0}});
                        giveItem(player, (short) 1559, 10, new int[][]{{30, 0}});
                        giveItem(player, (short) 1538, 1, new int[][]{{30, 0}});
                        giveItem(player, (short) 1592, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1770, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1838, 50, new int[][]{{30, 0}});
                        giveItem(player, (short) 1690, 200, new int[][]{{30, 0}});
                        giveItem(player, (short) 1789, 1, new int[][]{
                                {50, Util.nextInt(21, 24)},
                                {77, Util.nextInt(25, 28)},
                                {103, Util.nextInt(20, 25)},
                                {8, 4}, {94, 15}
                        });
                        giveItem(player, (short) 1668, 1, new int[][]{{50, 15}, {77, 15}, {103, 17}, {101, 20}});
                        giveItem(player, (short) 1534, 1, new int[][]{{50, 9}, {77, 8}, {108, 8}, {80, 5}, {85, 0}});
                        giveItem(player, (short) 1786, 1, new int[][]{{50, 24}, {77, 23}, {103, 23}, {5, 12}, {95, 10}, {226, 10}});
                        giveItem(player, (short) 1814, 1, new int[][]{{50, 12}, {77, 11}, {103, 10}, {80, 5}, {89, 0}});
                        giveItem(player, (short) 1700, 1, new int[][]{{50, Util.nextInt(23, 26)}, {77, 21}, {103, 21}, {117, 13}, {14, 15}});
                        giveItem(player, (short) 1803, 1, new int[][]{{50, 17}, {77, 15}, {103, 15}, {5, 10}, {93, 7}});
                        Service.gI().sendMoney(player);
                        InventoryService.gI().sendItemBag(player);
                        player.getSession().Vip_Point++;
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
