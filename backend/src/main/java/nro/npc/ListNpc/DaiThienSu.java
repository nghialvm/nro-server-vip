package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.server.Manager;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;

public class DaiThienSu extends Npc {

    public DaiThienSu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (player.NhanKeoHayBiGheoNpc_4 == 0) {
                    createOtherMenu(player, ConstNpc.NHAN_KEO_HALLOWEEN, "Ồ được rồi, kẹo đây, tha cho ta hahaha.",
                        "Cho kẹo\nhay\nbị ghẹo?", "Từ chối\nnhận kẹo", "Đóng");
                    return;
                }
            } else if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.NhanLiXiForNPC_3 == 0) {
                    String[] chucTetMessages = {
                        "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                        "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                        "Phát tài phát lộc",
                        "Vạn sự như ý",
                        "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                        "Năm mới phát tài phát lộc, vạn sự như ý nha",
                        "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                        "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                        "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                        "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                        "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                        "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                        "Tết đến cười thật nhiều, sống thật chill, vui hết mình",
                        "Chúc mừng năm mới"
                    };
                    
                    String message = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                    createOtherMenu(player, ConstNpc.NHAN_LI_XI, message, "Ok", "Chúc Mừng\nNăm Mới", "Đóng");
                    return;
                }
            }
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                if (Manager.gI().HienThiTimeEventTwo() != 0) {
                    this.createOtherMenu(player, ConstNpc.MENU_DUA_TOP,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nSức mạnh", "Top\nChỉ Số", "Top\nĐại Gia", "Top\nNhiệm Vụ", "Từ chối");
                } else {
                    this.createOtherMenu(player, ConstNpc.MENU_DUA_TOP,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(),
                            "Top\nSức mạnh", "Top\nChỉ Số", "Top\nĐại Gia", "Top\nNhiệm Vụ", "Từ chối");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_LI_XI) {
                    switch (select) {
                        case 1:
                            Item Lixi = ItemService.gI().createNewItem((short) 1760, 1);
                            String[] chucTetMessages = {
                                "Năm mới sẽ đem an lành thịnh vượng đến với bạn",
                                "Chúc bạn và gia đình có một năm mới hạnh phúc và thịnh vượng",
                                "Chúc bạn năm mới vui vẻ, tiền vô như nước, tình duyên rực rỡ",
                                "Năm mới phát tài phát lộc, vạn sự như ý nha",
                                "Xuân sang may mắn tràn đầy, hạnh phúc ngập lối",
                                "Năm mới bình an, vạn sự hanh thông, luôn vui cười",
                                "Tết đến rồi, quẩy hết mình và tận hưởng từng khoảnh khắc nhé",
                                "Năm mới vui như Tết, giàu như mơ, đẹp hơn xưa",
                                "Chúc bạn hạnh phúc tràn đầy, may mắn ngập tràn",
                                "Năm mới rực rỡ như pháo hoa, tươi vui như hoa mai nở",
                                "Tết đến cười thật nhiều, sống thật chill, vui hết mình"
                            };

                            String NpcChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            String PlayerChat = chucTetMessages[Util.nextInt(0, chucTetMessages.length - 1)];
                            this.npcChat(player, NpcChat);
                            Service.gI().chat(player, PlayerChat);
                            player.NhanLiXiForNPC_3++;
                            if (Util.isTrue(60, 100)) {
                                Lixi.addOptionParam(30, 0);
                                Lixi.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, Lixi);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Lixi.template.name);
                            } else {
                                Service.gI().sendThongBao(player, "(>_<)");
                            }
                            break;
                    }
                    return;
                }
            }
            if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DUA_TOP) {
                if (Manager.gI().HienThiTimeEventTwo() > 0) {
                    switch (select) {
                        case 0:
                            this.createOtherMenu(player, 0,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nSức mạnh\nBản Thân", "Top\nSức Mạnh\nĐệ Tử", "Đóng");
                            break;
                        case 1:
                            this.createOtherMenu(player, 1,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nHP", "Top\nKI", "Top\nSD", "Top\nHP\nĐệ Tử", "Top\nKI\nĐệ Tử", "Top\nSD\nĐệ Tử", "Đóng");
                            break;
                        case 2:
                            this.createOtherMenu(player, 2,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nVNĐ", "Top\nCoin", "Top\nThỏi Vàng", "Top\nHồng Ngọc", "Top\nSố Tiền\nĐã Nạp", "Đóng");
                            break;
                        case 3:
                            TopService.showListTopTask(player);
                            break;
                        case 4:
                            this.createOtherMenu(player, ConstNpc.MAIL_BOX,
                                "|0|Tình yêu như một dây đàn\n"
                                + "Tình vừa được thì đàn đứt dây\n"
                                + "Đứt dây này anh thay dây khác\n"
                                + "Mất em rồi anh biết thay ai?",
                                "Hòm Thư\n(" + (player.inventory.itemsMailBox.size()
                                - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsMailBox))
                                + " món)",
                                "Xóa Hết\nHòm Thư", "Đóng");
                            break;
                    }
                } else {
                    switch (select) {
                        case 0:
                            this.createOtherMenu(player, 0,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nSức mạnh\nBản Thân", "Top\nSức Mạnh\nĐệ Tử", "Đóng");
                            break;
                        case 1:
                            this.createOtherMenu(player, 1,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nHP", "Top\nKI", "Top\nSD", "Top\nHP\nĐệ Tử", "Top\nKI\nĐệ Tử", "Top\nSD\nĐệ Tử", "Đóng");
                            break;
                        case 2:
                            this.createOtherMenu(player, 2,
                            "|2|Sự kiện đua TOP chào mừng Vũ Trụ 1\n"
                            + "diễn ra từ " + Manager.timeStartDuaTop + " đến " + Manager.timeEndDuaTop + "\n"
                            + "Nhận thưởng vào " + Manager.timeEndNhanGiai + "\n"
                            + "Giải thưởng khủng chưa từng có, xem chi tiết tại box zalo\n"
                            + "|7|Thời gian diễn ra: " + Manager.DemTimeEvent(), 
                            "Top\nVNĐ", "Top\nCoin", "Top\nThỏi Vàng", "Top\nHồng Ngọc", "Top\nSố Tiền\nĐã Nạp", "Đóng");
                            break;
                        case 3:
                            TopService.showListTopTask(player);
                            break;
                        case 4:
                            this.createOtherMenu(player, ConstNpc.MAIL_BOX,
                                "|0|Tình yêu như một dây đàn\n"
                                + "Tình vừa được thì đàn đứt dây\n"
                                + "Đứt dây này anh thay dây khác\n"
                                + "Mất em rồi anh biết thay ai?",
                                "Hòm Thư\n(" + (player.inventory.itemsMailBox.size()
                                - InventoryService.gI().getCountEmptyListItem(player.inventory.itemsMailBox))
                                + " món)",
                                "Xóa Hết\nHòm Thư", "Đóng");
                            break;

                    }
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MAIL_BOX) {
                switch (select) {
                    case 0:
                        ShopService.gI().opendShop(player, "ITEMS_MAIL_BOX", true);
                        break;
                    case 1:
                        NpcService.gI().createMenuConMeo(player,
                                ConstNpc.CONFIRM_REMOVE_ALL_ITEM_MAIL_BOX, this.avartar,
                                "Bạn chắc muốn xóa hết vật phẩm trong hòm thư?\n"
                                + "Sau khi xóa sẽ không thể khôi phục!",
                                "Đồng ý", "Hủy bỏ");
                        break;
                    case 2:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 0) {
                switch (select) {
                    case 0:
                        TopService.showListTopPower(player);
                        break;
                    case 1:
                        TopService.showListTopPower_Pet(player);
                        break;
                    case 2:
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 1) {
                switch (select) {
                    case 0:
                        TopService.showListTopHP(player);
                        break;
                    case 1:
                        TopService.showListTopKI(player);
                        break;
                    case 2:
                        TopService.showListTopSD(player);
                        break;
                    case 3:
                        TopService.showListTopHP_Pet(player);
                        break;
                    case 4:
                        TopService.showListTopKI_Pet(player);
                        break;
                    case 5:
                        TopService.showListTopSD_Pet(player);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == 2) {
                switch (select) {
                    case 0:
                        TopService.showListTopVND(player);
                        break;
                    case 1:
                        TopService.showListTopCOIN(player);
                        break;
                    case 2:
                        Service.gI().sendThongBaoFromAdmin(player, "|2|TOP 20 NGƯỜI CHƠI NHIỀU THỎI VÀNG NHẤT\b\b|0|" + TopService.getTopThoiVang());
                        break;
                    case 3:
                        TopService.showListTopHONGNGOC(player);
                        break;
                    case 4:
                        TopService.showListTopDANAP(player);
                        break;
                }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.NHAN_KEO_HALLOWEEN) {
                switch (select) {
                    case 0:
                        Item KeoBanTay = ItemService.gI().createNewItem((short) 901, 1);
                        KeoBanTay.addOptionParam(86, 0);
                        KeoBanTay.addOptionParam(93, 35);
                        int quality = Util.nextInt(1, 3);
                        KeoBanTay.quantity = quality;
                        InventoryService.gI().addItemBag(player, KeoBanTay);
                        InventoryService.gI().sendItemBag(player);
                        Service.gI().chat(player, "Haha xin được " + quality + " kẹo bàn tay rồi");
                        player.NhanKeoHayBiGheoNpc_4++;
                        break;
                    case 1:
                        player.NhanKeoHayBiGheoNpc_4++;
                        break;
                }   
            }
        }
    }
}
