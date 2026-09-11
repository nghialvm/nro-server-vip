package nro.npc.ListNpc;



import Utils.Functions;
import nro.services.NpcService;
import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.ItemCheckUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.io.IOException;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.npc.Npc;
import nro.player.Player;


public class NoiBanh extends Npc {

    public NoiBanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }    

    @Override
    public void openBaseMenu(Player player) {
        if (EventManager.LUNNAR_NEW_YEAR) {
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Xin chào " + player.name + "\n"
                        + "Tôi là nồi bánh\n"
                        + "Tôi có thể giúp gì cho bạn?",
                        "Tự nấu bánh", "Từ chối");

            }
        } else if (EventManager.TRUNG_THU) {
            if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Xin chào, mình là Nồi bánh, bạn muốn nấu bánh gì ?",
                        "Bánh\nTrung Thu\nGà Quay", "Bánh\nTrung Thu\nGà Quay\nHảo Hạng", "Bánh\nTrung Thu\nHạt Sen", "Từ chối");

            }
        } else if (EventManager.HUNG_VUONG) {
            if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                        "Xin chào " + player.name + "\n"
                        + "Tôi là nồi bánh\n"
                        + "Tôi có thể giúp gì cho bạn?",
                        "Tự nấu bánh", "Từ chối");

            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                Item ThitHeo = InventoryService.gI().findItemBag(player, 748);
                Item ThungNep = InventoryService.gI().findItemBag(player, 749);
                Item ThungDauXanh = InventoryService.gI().findItemBag(player, 750);
                Item LaDong = InventoryService.gI().findItemBag(player, 751);
                Item TrungVitMuoi = InventoryService.gI().findItemBag(player, 886);
                boolean Thit = false;
                boolean Nep = false;
                boolean DauXanh = false;
                boolean LDong = false;
                boolean TVMuoi = false;
                if (ThitHeo != null && ThitHeo.quantity >= 10) {
                    Thit = true;
                }
                if (ThungNep != null && ThungNep.quantity >= 10) {
                    Nep = true;
                }
                if (ThungDauXanh != null && ThungDauXanh.quantity >= 10) {
                    DauXanh = true;
                }
                if (LaDong != null && LaDong.quantity >= 10) {
                    LDong = true;
                }
                if (TrungVitMuoi != null && TrungVitMuoi.quantity >= 1) {
                    TVMuoi = true;
                }
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, 0,
                                        "Hãy tìm đủ nguyên liệu và chọn loại bánh muốn nấu!",
                                        "Nấu bánh tét", "Nấu bánh trưng", "Từ chối");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                if (Thit && Nep && DauXanh && LDong && TVMuoi) {
                                    this.createOtherMenu(player, 1,
                                        "|1|Bạn muốn nấu Bánh Tét?\n"
                                        + "|2|Thịt heo " + ThitHeo.quantity + "/10\n"
                                        + "|2|Thúng nếp " + ThungNep.quantity + "/10\n"
                                        + "|2|Thúng đậu xanh " + ThungDauXanh.quantity + "/10\n"
                                        + "|2|Lá dong " + LaDong.quantity + "/10\n"
                                        + "|2|Giá vàng : 100 Triệu vàng",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Bạn muốn nấu Bánh Tét?\n"
                                        + (ThitHeo == null ? "|7|" : ThitHeo.quantity >= 10 ? "|2|" : "|7|") + "Thịt heo " + (ThitHeo == null ? "0" : ThitHeo.quantity) + "/10\n"
                                        + (ThungNep == null ? "|7|" : ThungNep.quantity >= 10 ? "|2|" : "|7|") + "Thúng nếp " + (ThungNep == null ? "0" : ThungNep.quantity) + "/10\n"
                                        + (ThungDauXanh == null ? "|7|" : ThungDauXanh.quantity >= 10 ? "|2|" : "|7|") + "Thúng đậu xanh " + (ThungDauXanh == null ? "0" : ThungDauXanh.quantity) + "/10\n"
                                        + (LaDong == null ? "|7|" : LaDong.quantity >= 10 ? "|2|" : "|7|") + "Lá dong " + (LaDong == null ? "0" : LaDong.quantity) + "/10\n"
                                        + "|2|Giá vàng : 100 Triệu vàng",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 1: {
                                if (Thit && Nep && DauXanh && LDong && TVMuoi) {
                                    this.createOtherMenu(player, 2,
                                        "|1|Bạn muốn nấu Bánh Trưng?\n"
                                        + "|2|Thịt heo " + ThitHeo.quantity + "/10\n"
                                        + "|2|Thúng nếp " + ThungNep.quantity + "/10\n"
                                        + "|2|Thúng đậu xanh " + ThungDauXanh.quantity + "/10\n"
                                        + "|2|Lá dong " + LaDong.quantity + "/10\n"
                                        + "|2|Trứng vịt muối " + TrungVitMuoi.quantity + "/1\n"
                                        + "|2|Giá vàng : 200 Triệu vàng",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Bạn muốn nấu Bánh Trưng?\n"
                                        + (ThitHeo == null ? "|7|" : ThitHeo.quantity >= 10 ? "|2|" : "|7|") + "Thịt heo " + (ThitHeo == null ? "0" : ThitHeo.quantity) + "/10\n"
                                        + (ThungNep == null ? "|7|" : ThungNep.quantity >= 10 ? "|2|" : "|7|") + "Thúng nếp " + (ThungNep == null ? "0" : ThungNep.quantity) + "/10\n"
                                        + (ThungDauXanh == null ? "|7|" : ThungDauXanh.quantity >= 10 ? "|2|" : "|7|") + "Thúng đậu xanh " + (ThungDauXanh == null ? "0" : ThungDauXanh.quantity) + "/10\n"
                                        + (LaDong == null ? "|7|" : LaDong.quantity >= 10 ? "|2|" : "|7|") + "Lá dong " + (LaDong == null ? "0" : LaDong.quantity) + "/10\n"
                                        + (TrungVitMuoi == null ? "|7|" : TrungVitMuoi.quantity >= 10 ? "|2|" : "|7|") + "Trứng vịt muối " + (TrungVitMuoi == null ? "0" : TrungVitMuoi.quantity) + "/10\n"
                                        + "|2|Giá vàng : 200 Triệu vàng",
                                        "Từ chối");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                if (player.RestartNauBanh != 0) {
                                    Service.gI().sendThongBao(player, "Bạn đang nấu bánh!");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) < 2) {
                                    Service.gI().sendThongBao(player, "Cần ít nhất 2 ô trống hành trang!");
                                    return;
                                }
                                if (player.inventory.getGold() < 100_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                short[] Param = {3, 5, 7, 15};
                                short[] List_Item = {933, 934, 878, 1255, 904, 549, 898};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                InventoryService.gI().subQuantityItemsBag(player, ThitHeo, 10);
                                InventoryService.gI().subQuantityItemsBag(player, ThungNep, 10);
                                InventoryService.gI().subQuantityItemsBag(player, ThungDauXanh, 10);
                                InventoryService.gI().subQuantityItemsBag(player, LaDong, 10);
                                player.inventory.gold -= 100_000_000;
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                player.RestartNauBanh = 1;
                                new Thread(() -> {
                                    int timeWait = 40;
                                    while (timeWait > 0) {
                                        timeWait--;
                                        Service.gI().sendThongBao(player, "Đang nấu bánh Tét\n"
                                                + "còn lại: " + timeWait + " giây");
                                        Functions.sleep(2000);
                                    }
                                    Item BanhTet = ItemService.gI().createNewItem((short) 752);
                                    BanhTet.addOptionParam(50, 15);
                                    BanhTet.addOptionParam(14, 15);
                                    BanhTet.addOptionParam(86, 0);
                                    BanhTet.addOptionParam(93, 30);
                                    InventoryService.gI().addItemBag(player, BanhTet);
                                    if (Item_Template == 933) {
                                        itemReceived.addOptionParam(31, Util.nextInt(1, 10));
                                    }
                                    if (Item_Template == 934) {
                                        itemReceived.addOptionParam(87, 0);
                                        itemReceived.quantity = Util.nextInt(1, 10);
                                    }
                                    if (Item_Template == 878 || Item_Template == 1255 || Item_Template == 904 || Item_Template == 898) {
                                        itemReceived.addOptionParam(50, Util.nextInt(15, 25));
                                        itemReceived.addOptionParam(77, Util.nextInt(15, 25));
                                        itemReceived.addOptionParam(103, Util.nextInt(15, 25));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(210, Util.nextInt(1, 3));
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 549) {
                                        itemReceived.addOptionParam(8, 4);
                                        itemReceived.addOptionParam(50, 20);
                                        itemReceived.addOptionParam(77, 17);
                                        itemReceived.addOptionParam(103, 17);
                                        itemReceived.addOptionParam(210, 1);
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Util.isTrue(50, 100)) {
                                        Service.gI().congTiemNang(player,(byte) 2, Util.nextInt(100_000, 1_000_000));
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, 
                                            "Đã nấu bánh xong\n"
                                          + "Bạn đã nhận được " + BanhTet.template.name, "Ok");
                                    player.RestartNauBanh = 0;
                                    new Thread(() -> {
                                        Functions.sleep(3000);
                                        player.event.addLunaNewYearPoint(1);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                    }).start();
                                }).start();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0: {
                                if (player.RestartNauBanh != 0) {
                                    Service.gI().sendThongBao(player, "Bạn đang nấu bánh!");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) < 2) {
                                    Service.gI().sendThongBao(player, "Cần ít nhất 2 ô trống hành trang!");
                                    return;
                                }
                                if (player.inventory.getGold() < 200_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                short[] Param = {3, 5, 7, 15};
                                short[] List_Item = {1173, 1084, 1085, 1086, 1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083, 730, 731, 732, 1201};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                InventoryService.gI().subQuantityItemsBag(player, ThitHeo, 10);
                                InventoryService.gI().subQuantityItemsBag(player, ThungNep, 10);
                                InventoryService.gI().subQuantityItemsBag(player, ThungDauXanh, 10);
                                InventoryService.gI().subQuantityItemsBag(player, LaDong, 10);
                                InventoryService.gI().subQuantityItemsBag(player, TrungVitMuoi, 1);
                                player.inventory.gold -= 200_000_000;
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                player.RestartNauBanh = 1;
                                new Thread(() -> {
                                    int timeWait = 60;
                                    while (timeWait > 0) {
                                        timeWait--;
                                        Service.gI().sendThongBao(player, "Đang nấu bánh Trưng\n"
                                                + "còn lại: " + timeWait + " giây");
                                        Functions.sleep(2000);
                                    }
                                    Item BanhTrung = ItemService.gI().createNewItem((short) 753);
                                    BanhTrung.addOptionParam(50, 25);
                                    BanhTrung.addOptionParam(14, 25);
                                    BanhTrung.addOptionParam(86, 0);
                                    BanhTrung.addOptionParam(93, 30);
                                    InventoryService.gI().addItemBag(player, BanhTrung);
                                    //
                                    if (Item_Template >= 1074 && Item_Template <= 1086) {
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template == 1173) {
                                        itemReceived.quantity = Util.nextInt(1, 3);
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template == 730) {
                                        itemReceived.addOptionParam(165, 10);
                                        itemReceived.addOptionParam(50, 22);
                                        itemReceived.addOptionParam(77, 19);
                                        itemReceived.addOptionParam(103, 19);
                                        itemReceived.addOptionParam(210, 1);
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 731) {
                                        itemReceived.addOptionParam(169, 0);
                                        itemReceived.addOptionParam(50, 22);
                                        itemReceived.addOptionParam(77, 19);
                                        itemReceived.addOptionParam(103, 19);
                                        itemReceived.addOptionParam(210, 1);
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 732) {
                                        itemReceived.addOptionParam(168, 0);
                                        itemReceived.addOptionParam(50, 22);
                                        itemReceived.addOptionParam(77, 19);
                                        itemReceived.addOptionParam(103, 19);
                                        itemReceived.addOptionParam(210, 1);
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 1201) {
                                        itemReceived.addOptionParam(50, 24);
                                        itemReceived.addOptionParam(77, 22);
                                        itemReceived.addOptionParam(103, 22);
                                        itemReceived.addOptionParam(94, 20);
                                        itemReceived.addOptionParam(80, 20);
                                        itemReceived.addOptionParam(114, 20);
                                        if (Util.isTrue(90, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Util.isTrue(50, 100)) {
                                        Service.gI().congTiemNang(player,(byte) 2, Util.nextInt(100_000, 1_000_000));
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, 
                                            "Đã nấu bánh xong\n"
                                          + "Bạn đã nhận được " + BanhTrung.template.name, "Ok");
                                    player.RestartNauBanh = 0;
                                    new Thread(() -> {
                                        Functions.sleep(3000);
                                        player.event.addLunaNewYearPoint(1);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                    }).start();
                                }).start();
                                break;
                            }
                        }
                    }
                }
            } else if (EventManager.TRUNG_THU) {
                ItemCheckUtil checker = new ItemCheckUtil(player)
                    .check(888, 10, "Bột Mì")
                    .check(889, 10, "Đậu Xanh")
                    .check(886, 2, "Trứng Vịt Muối")
                    .check(887, 1, "Gà Quay Nguyên Con");
                ItemCheckUtil checker2 = new ItemCheckUtil(player)
                    .check(888, 10, "Bột Mì")
                    .check(889, 10, "Đậu Xanh")
                    .check(886, 2, "Trứng Vịt Muối")
                    .check(1312, 1, "Hạt Sen");
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                if (checker.isAllEnough()) {
                                    this.createOtherMenu(player, 3,
                                        checker.getStatusText("Bánh Trung Thu Gà Quay", true, false, 200_000_000, 0),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        checker.getStatusText("Bánh Trung Thu Gà Quay", true, false, 200_000_000, 0),
                                        "Đóng");
                                }
                                break;
                            }
                            case 1: {
                                if (checker.isAllEnough()) {
                                    this.createOtherMenu(player, 4,
                                        checker.getStatusText("Bánh Trung Thu Gà Quay\n30% Cơ Hội Nhận Thêm Bánh Trung Thu Thập Cẩm", false, true, 0, 20),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        checker.getStatusText("Bánh Trung Thu Gà Quay\n30% Cơ Hội Nhận Thêm Bánh Trung Thu Thập Cẩm", false, true, 0, 20),
                                        "Đóng");
                                }
                                break;
                            }
                            case 2: {
                                if (checker2.isAllEnough()) {
                                    this.createOtherMenu(player, 5,
                                        checker2.getStatusText("Bánh Trung Thu Hạt Sen", false, true, 0, 50),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        checker2.getStatusText("Bánh Trung Thu Hạt Sen", false, true, 0, 50),
                                        "Đóng");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                if (player.inventory.getGold() < 200_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                Item BotMi = InventoryService.gI().findItemBag(player, 888);
                                Item DauXanh = InventoryService.gI().findItemBag(player, 889);
                                Item TrungVitMuoi = InventoryService.gI().findItemBag(player, 886);
                                Item GaQuayNguyenCon = InventoryService.gI().findItemBag(player, 887);
                                Item BanhTrungThu = ItemService.gI().createNewItem((short) 890);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BotMi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DauXanh));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, TrungVitMuoi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GaQuayNguyenCon));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(BanhTrungThu.template.iconID);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, BotMi, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, DauXanh, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, TrungVitMuoi, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, GaQuayNguyenCon, 1);
                                    player.inventory.subGold(200_000_000);
                                    BanhTrungThu.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, BanhTrungThu);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + BanhTrungThu.template.name);
                                    }).start();
                                    player.DuaTopLamBanhTrungThu++;
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 4) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) < 2) {
                                    Service.gI().sendThongBao(player, "Cần 2 ô trống trong hành trang");
                                    return;
                                }
                                if (player.inventory.getRuby() < 20) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                Item BotMi = InventoryService.gI().findItemBag(player, 888);
                                Item DauXanh = InventoryService.gI().findItemBag(player, 889);
                                Item TrungVitMuoi = InventoryService.gI().findItemBag(player, 886);
                                Item GaQuayNguyenCon = InventoryService.gI().findItemBag(player, 887);
                                Item BanhTrungThu = ItemService.gI().createNewItem((short) 890);
                                Item BanhTrungThuThapCam = ItemService.gI().createNewItem((short) 891);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BotMi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DauXanh));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, TrungVitMuoi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GaQuayNguyenCon));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(BanhTrungThu.template.iconID);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, BotMi, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, DauXanh, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, TrungVitMuoi, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, GaQuayNguyenCon, 1);
                                    player.inventory.subRuby(20);
                                    BanhTrungThu.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, BanhTrungThu);
                                    if (Util.isTrue(30, 100)) {
                                        BanhTrungThuThapCam.addOptionParam(30, 0);
                                        InventoryService.gI().addItemBag(player, BanhTrungThuThapCam);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + BanhTrungThuThapCam.template.name);
                                    }
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + BanhTrungThu.template.name);
                                    }).start();
                                    player.DuaTopLamBanhTrungThu++;
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 5) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                if (player.inventory.getRuby() < 50) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                Item BotMi = InventoryService.gI().findItemBag(player, 888);
                                Item DauXanh = InventoryService.gI().findItemBag(player, 889);
                                Item TrungVitMuoi = InventoryService.gI().findItemBag(player, 886);
                                Item HatSen = InventoryService.gI().findItemBag(player, 1312);
                                Item BanhTrungThu = ItemService.gI().createNewItem((short) 1313);
                                try {
                                    Message msgg;
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(0);
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeUTF("MaiTienDung");
                                    msgg.writer().writeShort(tempId);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(1);
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BotMi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DauXanh));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, TrungVitMuoi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HatSen));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(BanhTrungThu.template.iconID);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, BotMi, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, DauXanh, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, TrungVitMuoi, 2);
                                    InventoryService.gI().subQuantityItemsBag(player, HatSen, 1);
                                    player.inventory.subRuby(50);
                                    BanhTrungThu.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, BanhTrungThu);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + BanhTrungThu.template.name);
                                    }).start();
                                    player.DuaTopLamBanhTrungThu++;
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                    }
                }
            } else if (EventManager.HUNG_VUONG) {
                ItemCheckUtil Banhday = new ItemCheckUtil(player)
                    .check(1546, 99, "Cơm Nếp")
                    .check(1547, 5, "Bột Gạo")
                    .check(1545, 2, "Muối Tiêu")
                    .check(1544, 1, "Chả Lụa");
                ItemCheckUtil BanhChung = new ItemCheckUtil(player)
                    .check(1546, 99, "Cơm Nếp")
                    .check(1548, 2, "Đậu Xanh")
                    .check(1549, 2, "Thịt Tươi");
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                if (player.typeBanhDangNau == 1) {
                                    long timePassed = System.currentTimeMillis() - player.lastTimeNauBanhHungVuong;
                                    long timeLeft = 120_000 - timePassed;
                                    if (timeLeft <= 0) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.HUNG_VUONG_BANH_DAY, this.avartar, "Bạn có bánh đã nấu xong, nhớ nhận nhé", 
                                                "Nhận\nBánh Dầy");
                                    } else {
                                        long seconds = timeLeft / 1000;
                                        long minutes = seconds / 60;
                                        seconds = seconds % 60;
                                        String timeStr = String.format("Còn lại: %d phút %02d giây", minutes, seconds);
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Bánh của bạn đang được nấu\n" + timeStr, "OK");
                                    }
                                    return;
                                }
                                if (player.typeBanhDangNau == 2) {
                                    long timePassed = System.currentTimeMillis() - player.lastTimeNauBanhHungVuong;
                                    long timeLeft = 120_000 - timePassed;
                                    if (timeLeft <= 0) {
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.HUNG_VUONG_BANH_CHUNG, this.avartar, "Bạn có bánh đã nấu xong, nhớ nhận nhé", 
                                                "Nhận\nBánh Chưng");
                                    } else {
                                        long seconds = timeLeft / 1000;
                                        long minutes = seconds / 60;
                                        seconds = seconds % 60;
                                        String timeStr = String.format("Còn lại: %d phút %02d giây", minutes, seconds);
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Bánh của bạn đang được nấu\n" + timeStr, "OK");
                                    }
                                    return;
                                }
                                this.createOtherMenu(player, 6, "Hãy tìm đủ nguyên liệu và chọn loại bánh muốn nấu",
                                    "Nấu\nBánh Dầy", "Nấu\nBánh Chưng", "Từ chối");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 6) {
                        switch (select) {
                            case 0: {
                                if (Banhday.isAllEnough()) {
                                    this.createOtherMenu(player, 7,
                                        Banhday.getStatusText("Bạn Muốn Nấu Bánh Dầy ?", true, false, 10_000_000, 0),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        Banhday.getStatusText("Bạn Muốn Nấu Bánh Dầy ?", true, false, 10_000_000, 0),
                                        "Đóng");
                                }
                                break;
                            }
                            case 1: {
                                if (BanhChung.isAllEnough()) {
                                    this.createOtherMenu(player, 8,
                                        BanhChung.getStatusText("Bạn Muốn Nấu Bánh Chưng ?", true, false, 50_000_000, 0),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        BanhChung.getStatusText("Bạn Muốn Nấu Bánh Chưng ?", true, false, 50_000_000, 0),
                                        "Đóng");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 7) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                if (player.inventory.getGold() < 10_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                if (player.typeBanhDangNau != 0) {
                                    Service.gI().sendThongBao(player, "Bạn đang nấu bánh rồi!");
                                    return;
                                }
                                Item ComNep = InventoryService.gI().findItemBag(player, 1546);
                                Item BotGao = InventoryService.gI().findItemBag(player, 1547);
                                Item MuoiTieu = InventoryService.gI().findItemBag(player, 1545);
                                Item ChaLua = InventoryService.gI().findItemBag(player, 1544);
                                if (ComNep != null && BotGao != null && MuoiTieu != null && ChaLua != null) {
                                    if (ComNep.quantity >= 99 && BotGao.quantity >= 5 && MuoiTieu.quantity >= 2 && ChaLua.quantity >= 1) {
                                        InventoryService.gI().subQuantityItemsBag(player, ComNep, 99);
                                        InventoryService.gI().subQuantityItemsBag(player, BotGao, 5);
                                        InventoryService.gI().subQuantityItemsBag(player, MuoiTieu, 2);
                                        InventoryService.gI().subQuantityItemsBag(player, ChaLua, 1);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.subGold(10_000_000);
                                        Service.gI().sendMoney(player);
                                        player.lastTimeNauBanhHungVuong = System.currentTimeMillis();
                                        player.typeBanhDangNau = 1;
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bánh của bạn đang được nấu\n2 phút sau đến nhận bánh nhé.", "OK");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu để nấu bánh!");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu để nấu bánh!");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 8) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                if (player.inventory.getGold() < 50_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                if (player.typeBanhDangNau != 0) {
                                    Service.gI().sendThongBao(player, "Bạn đang nấu bánh rồi!");
                                    return;
                                }
                                Item ComNep = InventoryService.gI().findItemBag(player, 1546);
                                Item DauXanh = InventoryService.gI().findItemBag(player, 1548);
                                Item ThitTuoi = InventoryService.gI().findItemBag(player, 1549);
                                if (ComNep != null && DauXanh != null && ThitTuoi != null) {
                                    if (ComNep.quantity >= 99 && DauXanh.quantity >= 2 && ThitTuoi.quantity >= 2) {
                                        InventoryService.gI().subQuantityItemsBag(player, ComNep, 99);
                                        InventoryService.gI().subQuantityItemsBag(player, DauXanh, 2);
                                        InventoryService.gI().subQuantityItemsBag(player, ThitTuoi, 2);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.subGold(50_000_000);
                                        Service.gI().sendMoney(player);
                                        player.lastTimeNauBanhHungVuong = System.currentTimeMillis();
                                        player.typeBanhDangNau = 2;
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Bánh của bạn đang được nấu\n2 phút sau đến nhận bánh nhé.", "OK");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu để nấu bánh!");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ nguyên liệu để nấu bánh!");
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
