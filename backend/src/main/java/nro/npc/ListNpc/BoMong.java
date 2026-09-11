package nro.npc.ListNpc;

import nro.inventory.InventoryService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstTask;
import event.EventManager;
import jbcd.dao.PlayerDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.achievement.AchievementService;
import nro.npc.Npc;
import nro.player.Player;

public class BoMong extends Npc {

    public BoMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            // Sự kiện Tết
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_6 == 0) {
                    String[] chucTetMessages = {
                        "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                        "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                        "Phát tài phát lộc",
                        "Vạn sự như ý",
                        "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                        "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                        "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                        "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                        "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                        "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                    };

                    String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                    return;
                }
            }

            // Menu chính
            if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                if (this.mapId == 47 || this.mapId == 84) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi muốn có thêm thỏi vàng, có nhiều cách, nạp thẻ cào là nhanh nhất, còn không thì chịu khó làm vài nhiệm vụ sẽ được thưởng thỏi vàng",
                            "Nhiệm vụ\nhàng ngày",
                            "Nhiệm vụ\nthành tích",
                            // "Mốc nạp",
                            "Từ chối");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player)) {
            return;
        }

        if (this.mapId == 47 || this.mapId == 84) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: // Nhiệm vụ hàng ngày
                        if (player.playerTask.sideTask.template != null) {
                            String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " (" + player.playerTask.sideTask.getLevel() + ")"
                                    + "\nHoàn thành: " + player.playerTask.sideTask.count + "/" + player.playerTask.sideTask.maxCount
                                    + " (" + player.playerTask.sideTask.getPercentProcess() + "%)"
                                    + "\nSố nhiệm vụ còn lại trong ngày: " + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;

                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                    npcSay,
                                    "Trả nhiệm\nvụ",
                                    "Hủy nhiệm\nvụ");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                    "Tôi có vài nhiệm vụ theo cấp bậc, sức cậu có thể làm được cái nào?",
                                    "Dễ",
                                    "Bình thường",
                                    "Khó",
                                    "Siêu khó",
                                    "Địa ngục",
                                    "Từ chối");
                        }
                        break;

                    case 1: // Thành tích
                        AchievementService.gI().openAchievementUI(player);
                        break;

                    // case 2: // Mốc nạp
                    //     this.createOtherMenu(player, ConstNpc.MENU_QUA,
                    //             "Bạn đã nạp: " + player.getSession().tongnap + " VND\n"
                    //             + "Mốc 50k: 1 món Thần Linh bất kỳ cùng hành tinh \n"
                    //             + "Mốc 100k: 1 set Thần Linh cùng hành tinh \n"
                    //             + "Quà MTV: 50 thỏi vàng +500 hồng ngọc+ CT Black Rose 7 ngày\n"
                    //             + "Bạn có muốn nhận quà không?",
                    //             "50K", "100K", "MTV", "Từ chối");
                    //     break;
                }
            } // ====== Nhiệm vụ phụ ======
            else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                switch (select) {
                    case 0, 1, 2, 3, 4 ->
                        TaskService.gI().changeSideTask(player, (byte) select);
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                switch (select) {
                    case 0 ->
                        TaskService.gI().paySideTask(player);
                    case 1 ->
                        TaskService.gI().removeSideTask(player);
                }
            } // ====== Nhận Lì xì ======
            else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI && select == 1) {
                Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                Lixi.addOptionParam(30, 0);
                Lixi.addOptionParam(93, 30);
                InventoryService.gI().addItemBag(player, Lixi);
                InventoryService.gI().sendItemBag(player);
                player.NhanLiXiForNPC_6++;
                Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
            } // ====== Menu Quà ======
            else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_QUA) {
                switch (select) {
                    // Mốc 50K
                    // Mốc 50K - random full pool có thể ra nhẫn
                    case 0: {
                        if (player.getSession().tongnap >= 50000) {
                            if (player.mocnap[0] == 1) {
                                Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 50K rồi!");
                                break;
                            }

                            // Pool riêng từng hành tinh
                            short[] poolTL;
                            switch (player.gender) {
                                case 0: // Trái đất
                                    poolTL = new short[]{555, 556, 562, 563, 561};
                                    break;
                                case 1: // Namếc
                                    poolTL = new short[]{557, 558, 564, 565, 561};
                                    break;
                                case 2: // Xayda
                                    poolTL = new short[]{559, 560, 566, 567, 561};
                                    break;
                                default:
                                    poolTL = new short[]{561};
                                    break;
                            }

                            // Random 1 item trong pool tương ứng
                            short randomItem = poolTL[Util.nextInt(0, poolTL.length - 1)];
                            Item itemGift = ItemService.gI().createNewItem(randomItem, 1);
                            itemGift.itemOptions.addAll(ItemService.gI().getDefaultOptionTL(randomItem));
                            itemGift.addOptionParam(30, 0); // Khóa đồ

                            // Thêm item vào túi
                            InventoryService.gI().addItemBag(player, itemGift);
                            InventoryService.gI().sendItemBag(player);

                            // Đánh dấu đã nhận
                            player.mocnap[0] = 1;
                            PlayerDAO.updatePlayer(player);

                            Service.gI().sendThongBao(player,
                                    "Bạn đã nhận quà mốc 50K: " + itemGift.template.name + " (đồ khóa)");
                        } else {
                            Service.gI().sendThongBao(player, "Bạn chưa đủ 50.000 VND để nhận quà!");
                        }
                        break;
                    }

                    // Mốc 100K
                    case 1: {
                        if (player.getSession().tongnap >= 100000) {
                            if (player.mocnap[1] == 1) {
                                Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc 100K rồi!");
                                break;
                            }

                            short[] fullSet;
                            switch (player.gender) {
                                case 0 ->
                                    fullSet = new short[]{555, 556, 562, 563};
                                case 1 ->
                                    fullSet = new short[]{557, 558, 564, 565};
                                default ->
                                    fullSet = new short[]{559, 560, 566, 567};
                            }

                            // Nhẫn chung
                            for (short id : fullSet) {
                                Item itemGift = ItemService.gI().createNewItem(id, 1);
                                itemGift.itemOptions.addAll(ItemService.gI().getDefaultOptionTL(id));
                                itemGift.addOptionParam(30, 0);
                                InventoryService.gI().addItemBag(player, itemGift);
                            }

                            Item nhan = ItemService.gI().createNewItem((short) 561, 1);
                            nhan.itemOptions.addAll(ItemService.gI().getDefaultOptionTL(561));
                            nhan.addOptionParam(30, 0);
                            InventoryService.gI().addItemBag(player, nhan);

                            InventoryService.gI().sendItemBag(player);
                            player.mocnap[1] = 1; // Đánh dấu đã nhận
                            PlayerDAO.updatePlayer(player);

                            Service.gI().sendThongBao(player, "Bạn đã nhận full set Thần Linh mốc 100K!");
                        } else {
                            Service.gI().sendThongBao(player, "Bạn chưa đủ 100.000 VND để nhận quà!");
                        }
                        break;
                    }

                    case 2: {
                        if (player.getSession().actived) {
                            if (player.mocnap[2] == 1) {
                                Service.gI().sendThongBao(player, "Bạn đã nhận quà mốc Thành Viên rồi!");
                                break;
                            }

                            // Thỏi vàng (ID 457) - số lượng 50, khóa
                            Item thoiVang = ItemService.gI().createNewItem((short) 457, 50);
                            thoiVang.addOptionParam(30, 0); // Khóa
                            InventoryService.gI().addItemBag(player, thoiVang);

                            // Cải trang 1812 - 1 cái, option mặc định + khóa
                            Item caitrang = ItemService.gI().createNewItem((short) 1812, 1);
                            caitrang.addOptionParam(50, 35);
                            caitrang.addOptionParam(77, 35);
                            caitrang.addOptionParam(103, 35);
                            caitrang.addOptionParam(106, 1);
                            caitrang.addOptionParam(107, 7);
                            caitrang.addOptionParam(93, 7);
                            InventoryService.gI().addItemBag(player, caitrang);

                            // Cộng Ruby
                            player.inventory.ruby += 500;

                            // Gửi cập nhật túi và ruby
                            InventoryService.gI().sendItemBag(player);
                            Service.gI().sendMoney(player);

                            // Ghi nhận đã nhận quà và lưu DB
                            player.mocnap[2] = 1;
                            PlayerDAO.updatePlayer(player);

                            Service.gI().sendThongBao(player,
                                    "Bạn đã nhận quà Thành Viên: +50 Thỏi vàng, +500 Ruby, +1 Cải trang đặc biệt!");
                        } else {
                            Service.gI().sendThongBao(player, "Tài khoản chưa được mở Thành Viên, không thể nhận quà!");
                        }
                        break;
                    }
                }
            }
        }
    }
}