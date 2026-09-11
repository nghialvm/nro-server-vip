package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.ItemCheckUtil;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.io.IOException;
import jbcd.dao.EventDAO;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;


public class HungVuong extends Npc {

    public HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }    

    @Override
    public void openBaseMenu(Player player) {
        if (EventManager.HUNG_VUONG) {
            switch (this.mapId) {
                case 5:
                case 13:
                case 20:
                    if (Util.canDoWithTime(EventDAO.getRECEIVE_MELON_SEED(), 300_000)) {
                        if (Util.isTrue(50, 100)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Hãy trồng dưa hấu và mang quả đến gặp ta để đổi quà",
                                    "Thưởng Vé\nQuay Ngọc\nMiễn Phí", "Nhận Quà", "Cửa Hàng", "Đổi Điểm\n[" + player.event.getHungVuongPoint() + "]", "Bảng Xếp Hạng", "Từ chối");
                        } else {
                            this.createOtherMenu(player, 0,
                                    "Hãy trồng dưa hấu và mang quả đến gặp ta để đổi quà",
                                    "Nhận Hạt\nDưa Hấu");
                        }
                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, 
                                "Hãy trồng dưa hấu và mang quả đến gặp ta để đổi quà",
                                "Thưởng Vé\nQuay Ngọc\nMiễn Phí", "Nhận Quà", "Cửa Hàng", "Đổi Điểm\n[" + player.event.getHungVuongPoint() + "]", "Bảng Xếp Hạng", "Từ chối");
                    }   
                    break;
                case 2:
                case 9:
                case 16:
                case 27:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì ?",
                            "Dâng\nSính Lễ", "Dâng\nSính Lễ\nXịn", "Dâng\nBánh Dầy", "Dâng\nBánh Chưng\nLang Liêu");
                    break;
                case 0:
                case 7:
                case 14:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi tìm ta có việc gì ?",
                            "Đổi\nDưa Hấu\nThường", "Đổi\nDưa Hấu\nXịn", "Đóng");
                    break;
                default:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ta sẽ đưa con quay về",
                            "Đồng ý", "Từ chối");
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.HUNG_VUONG) {
                switch (this.mapId) {
                    case 5:
                    case 13:
                    case 20: {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    if ((player.NhanQuaHungVuongFree & 1) != 0) {
                                        Service.gI().sendThongBao(player, "Hôm nay đã nhận rồi, hãy chờ đến ngày mai!");
                                        return;
                                    }
                                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                        return;
                                    }
                                    Item veQuay = ItemService.gI().createNewItem((short) 821, 1);
                                    veQuay.addOptionParam(93, 30);
                                    veQuay.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, veQuay);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + veQuay.Name());
                                    player.NhanQuaHungVuongFree |= 1; // đánh dấu đã nhận ở case 0
                                    break;
                                }
                                case 1: {
                                    if ((player.NhanQuaHungVuongFree & 2) != 0) {
                                        Service.gI().sendThongBao(player, "Hôm nay đã nhận rồi, hãy chờ đến ngày mai!");
                                        return;
                                    }
                                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                        return;
                                    }
                                    short[] Param = {3, 5, 7, 15, 30};
                                    short[] List_Item = {1550, 1551, 1557, 1882};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    switch (Item_Template) {
                                        case 1882:
                                            itemReceived.addOptionParam(30, 0);
                                            itemReceived.addOptionParam(93, 35);
                                            break;
                                        case 1550:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(14, Util.nextInt(5, 15));
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            break;
                                        case 1551:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(94, Util.nextInt(8, 15));
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            break;
                                        case 1557:
                                            itemReceived.addOptionParam(50, 25);
                                            itemReceived.addOptionParam(77, 24);
                                            itemReceived.addOptionParam(103, 24);
                                            itemReceived.addOptionParam(80, 15);
                                            itemReceived.addOptionParam(108, 15);
                                            itemReceived.addOptionParam(94, 15);
                                            itemReceived.addOptionParam(114, 50);
                                            itemReceived.addOptionParam(117, 10);
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            break;
                                        default:
                                            break;
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                                    player.NhanQuaHungVuongFree |= 2; // đánh dấu đã nhận ở case 1
                                    break;
                                }
                                case 2: {
                                    ShopService.gI().opendShop(player, "HUNG_VUONG_EVENT", true);
                                    break;
                                }
                                case 3: {
                                    ShopService.gI().opendShop(player, "HUNG_VUONG_EVENT2", true);
                                    break;
                                }
                                case 4: {
                                    this.createOtherMenu(player, 7,
                                        "Bảng Xếp Hạng Sự Kiện",
                                        "Top\nMở Trứng\nVàng", "Top\nMở Hộp\nQuà\nGiỗ Tổ", "Top\nDâng Bánh", "Top\nĐổi\nDưa Hấu", "Đóng");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 0) {
                            switch (select) {
                                case 0: {
                                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                        return;
                                    }
                                    Item HatDua = ItemService.gI().createNewItem((short) 1771, 1);
                                    HatDua.addOptionParam(93, 35);
                                    InventoryService.gI().addItemBag(player, HatDua);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + HatDua.Name());
                                    EventDAO.setRECEIVE_MELON_SEED(System.currentTimeMillis());
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 7) {
                            switch (select) {
                                case 0: {
                                    TopService.showListTopMoTrungRong(player);
                                    break;
                                }
                                case 1: {
                                    TopService.showListTopMoHopQuaGioTo(player);
                                    break;
                                }
                                case 2: {
                                    TopService.showListTopDangBanhHungVuong(player);
                                    break;
                                }
                                case 3: {
                                    TopService.showListTopDoiDuaHau(player);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 0:
                    case 7:
                    case 14: {
                        Item DuaHau = InventoryService.gI().findItemBag(player, 569);
                        Item TemMaiAnTien = InventoryService.gI().findItemBag(player, 1558);
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    this.createOtherMenu(player, 5,
                                        "Ngươi muốn đổi hồng ngọc thì mang dưa hấu đến cho ta nhé\n"
                                                + "Đang có " + (DuaHau != null ? DuaHau.quantity : 0) + " quả dưa hấu",
                                        "200 Ngọc\n30 Quả", "150 Ngọc\n25 Quả", "100 Ngọc\n20 Quả", "30 Ngọc\n10 Quả", "2 Ngọc\n1 Quả");
                                    break;
                                }
                                case 1: {
                                    this.createOtherMenu(player, 6,
                                        "Ngươi muốn đổi hồng ngọc thì mang dưa hấu đến cho ta nhé\n"
                                                + "Đang có " + (DuaHau != null ? DuaHau.quantity : 0) + " quả dưa hấu",
                                        "250 Ngọc\n30 Quả\n5 Tem", "185 Ngọc\n25 Quả\n4 Tem", "120 Ngọc\n20 Quả\n3 Tem", "40 Ngọc\n10 Quả\n2 Tem", "5 Ngọc\n1 Quả\n1 Tem");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 5) {
                            switch (select) {
                                case 0: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 30) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        player.DuaTopDoiDuaHau += 30;
                                        InventoryService.gI().subQuantityItemsBag(player, DuaHau, 30);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.addRuby(200);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 200 hồng ngọc");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 25) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        player.DuaTopDoiDuaHau += 25;
                                        InventoryService.gI().subQuantityItemsBag(player, DuaHau, 25);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.addRuby(150);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 150 hồng ngọc");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 2: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 20) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        player.DuaTopDoiDuaHau += 20;
                                        InventoryService.gI().subQuantityItemsBag(player, DuaHau, 20);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.addRuby(100);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 100 hồng ngọc");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 3: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 10) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        player.DuaTopDoiDuaHau += 10;
                                        InventoryService.gI().subQuantityItemsBag(player, DuaHau, 10);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.addRuby(30);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 30 hồng ngọc");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 4: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 1) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        player.DuaTopDoiDuaHau += 1;
                                        InventoryService.gI().subQuantityItemsBag(player, DuaHau, 1);
                                        InventoryService.gI().sendItemBag(player);
                                        player.inventory.addRuby(2);
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được 2 hồng ngọc");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 6) {
                            switch (select) {
                                case 0: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 30) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        if (TemMaiAnTien != null) {
                                            if (TemMaiAnTien.quantity < 5) {
                                                Service.gI().sendThongBao(player, "Bạn không đủ Tem chứng nhận Mai An Tiêm!");
                                                return;
                                            }
                                            player.DuaTopDoiDuaHau += 30;
                                            InventoryService.gI().subQuantityItemsBag(player, DuaHau, 30);
                                            InventoryService.gI().subQuantityItemsBag(player, TemMaiAnTien, 5);
                                            InventoryService.gI().sendItemBag(player);
                                            player.inventory.addRuby(250);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được 250 hồng ngọc");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không có Tem chứng nhận Mai An Tiêm!");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 25) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        if (TemMaiAnTien != null) {
                                            if (TemMaiAnTien.quantity < 4) {
                                                Service.gI().sendThongBao(player, "Bạn không đủ Tem chứng nhận Mai An Tiêm!");
                                                return;
                                            }
                                            player.DuaTopDoiDuaHau += 25;
                                            InventoryService.gI().subQuantityItemsBag(player, DuaHau, 25);
                                            InventoryService.gI().subQuantityItemsBag(player, TemMaiAnTien, 4);
                                            InventoryService.gI().sendItemBag(player);
                                            player.inventory.addRuby(185);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được 185 hồng ngọc");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không có Tem chứng nhận Mai An Tiêm!");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 2: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 20) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        if (TemMaiAnTien != null) {
                                            if (TemMaiAnTien.quantity < 3) {
                                                Service.gI().sendThongBao(player, "Bạn không đủ Tem chứng nhận Mai An Tiêm!");
                                                return;
                                            }
                                            player.DuaTopDoiDuaHau += 20;
                                            InventoryService.gI().subQuantityItemsBag(player, DuaHau, 20);
                                            InventoryService.gI().subQuantityItemsBag(player, TemMaiAnTien, 3);
                                            InventoryService.gI().sendItemBag(player);
                                            player.inventory.addRuby(120);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được 120 hồng ngọc");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không có Tem chứng nhận Mai An Tiêm!");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 3: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 10) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        if (TemMaiAnTien != null) {
                                            if (TemMaiAnTien.quantity < 2) {
                                                Service.gI().sendThongBao(player, "Bạn không đủ Tem chứng nhận Mai An Tiêm!");
                                                return;
                                            }
                                            player.DuaTopDoiDuaHau += 10;
                                            InventoryService.gI().subQuantityItemsBag(player, DuaHau, 10);
                                            InventoryService.gI().subQuantityItemsBag(player, TemMaiAnTien, 2);
                                            InventoryService.gI().sendItemBag(player);
                                            player.inventory.addRuby(40);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được 40 hồng ngọc");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không có Tem chứng nhận Mai An Tiêm!");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                                case 4: {
                                    if (DuaHau != null) {
                                        if (DuaHau.quantity < 1) {
                                            Service.gI().sendThongBao(player, "Bạn không đủ quả dưa hấu!");
                                            return;
                                        }
                                        if (TemMaiAnTien != null) {
                                            if (TemMaiAnTien.quantity < 1) {
                                                Service.gI().sendThongBao(player, "Bạn không đủ Tem chứng nhận Mai An Tiêm!");
                                                return;
                                            }
                                            player.DuaTopDoiDuaHau += 1;
                                            InventoryService.gI().subQuantityItemsBag(player, DuaHau, 1);
                                            InventoryService.gI().subQuantityItemsBag(player, TemMaiAnTien, 1);
                                            InventoryService.gI().sendItemBag(player);
                                            player.inventory.addRuby(5);
                                            Service.gI().sendMoney(player);
                                            Service.gI().sendThongBao(player, "Bạn nhận được 5 hồng ngọc");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không có Tem chứng nhận Mai An Tiêm!");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có quả dưa hấu!");
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    case 2:
                    case 9:
                    case 16:
                    case 27: {
                        ItemCheckUtil DangSinhLe = new ItemCheckUtil(player)
                            .check(1220, 9, "Ngà Voi")
                            .check(1221, 9, "Cựa Gà")
                            .check(1222, 9, "Hồng Mao");
                        ItemCheckUtil BanhDay = new ItemCheckUtil(player)
                            .check(1543, 1, "Bánh Dầy");
                        ItemCheckUtil BanhChung = new ItemCheckUtil(player)
                            .check(1556, 1, "Bánh Chưng Lang Liêu");
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    if (DangSinhLe.isAllEnough()) {
                                        this.createOtherMenu(player, 1,
                                            DangSinhLe.getStatusText("Con Muốn Dâng Sính Lễ ?", true, false, 50_000_000, 0),
                                            "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            DangSinhLe.getStatusText("Con Muốn Dâng Sính Lễ ?", true, false, 50_000_000, 0),
                                            "Đóng");
                                    }
                                    break;
                                }
                                case 1: {
                                    if (DangSinhLe.isAllEnough()) {
                                        this.createOtherMenu(player, 2,
                                            DangSinhLe.getStatusText("Con Muốn Dâng Sính Lễ Xịn ?", false, true, 0, 50),
                                            "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            DangSinhLe.getStatusText("Con Muốn Dâng Sính Lễ Xịn ?", false, true, 0, 50),
                                            "Đóng");
                                    }
                                    break;
                                }
                                case 2: {
                                    if (BanhDay.isAllEnough()) {
                                        this.createOtherMenu(player, 3,
                                            BanhDay.getStatusText("Con Muốn Dâng Sính Lễ ?", false, false, 0, 0),
                                            "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            BanhDay.getStatusText("Con Muốn Dâng Sính Lễ ?", false, false, 0, 0),
                                            "Đóng");
                                    }
                                    break;
                                }
                                case 3: {
                                    if (BanhChung.isAllEnough()) {
                                        this.createOtherMenu(player, 4,
                                            BanhChung.getStatusText("Con Muốn Dâng Sính Lễ ?", false, false, 0, 0),
                                            "Đồng ý", "Từ chối");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            BanhChung.getStatusText("Con Muốn Dâng Sính Lễ ?", false, false, 0, 0),
                                            "Đóng");
                                    }
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
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
                                    Item NgaVoi = InventoryService.gI().findItemBag(player, 1220);
                                    Item CuaGa = InventoryService.gI().findItemBag(player, 1221);
                                    Item HongMao = InventoryService.gI().findItemBag(player, 1222);
                                    Item HopQua = ItemService.gI().createNewItem((short) 1227);
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
                                        msgg.writer().writeByte(3);
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, NgaVoi));
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, CuaGa));
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HongMao));
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(7);
                                        msgg.writer().writeShort(HopQua.template.iconID);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        InventoryService.gI().subQuantityItemsBag(player, NgaVoi, 9);
                                        InventoryService.gI().subQuantityItemsBag(player, CuaGa, 9);
                                        InventoryService.gI().subQuantityItemsBag(player, HongMao, 9);
                                        player.inventory.subGold(50_000_000);
                                        HopQua.addOptionParam(30, 0);
                                        InventoryService.gI().addItemBag(player, HopQua);
                                        new Thread(() -> {
                                            Functions.sleep(2000);
                                            Service.gI().sendThongBao(player, "Bạn nhận được " + HopQua.template.name);
                                        }).start();
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendMoney(player);
                                        this.npcChat(player, "Khá lắm, đúng là con trai của ta");
                                        this.npcChat(player, "Ta có quà cho con, hãy nhận lấy.");
                                    } catch (IOException e) {
                                    }
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 2) {
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
                                    Item NgaVoi = InventoryService.gI().findItemBag(player, 1220);
                                    Item CuaGa = InventoryService.gI().findItemBag(player, 1221);
                                    Item HongMao = InventoryService.gI().findItemBag(player, 1222);
                                    Item HopQua = ItemService.gI().createNewItem((short) 1228);
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
                                        msgg.writer().writeByte(3);
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, NgaVoi));
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, CuaGa));
                                        msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HongMao));
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        msgg = new Message(-81);
                                        msgg.writer().writeByte(7);
                                        msgg.writer().writeShort(HopQua.template.iconID);
                                        player.sendMessage(msgg);
                                        msgg.cleanup();
                                        InventoryService.gI().subQuantityItemsBag(player, NgaVoi, 9);
                                        InventoryService.gI().subQuantityItemsBag(player, CuaGa, 9);
                                        InventoryService.gI().subQuantityItemsBag(player, HongMao, 9);
                                        player.inventory.subRuby(50);
                                        HopQua.addOptionParam(30, 0);
                                        InventoryService.gI().addItemBag(player, HopQua);
                                        new Thread(() -> {
                                            Functions.sleep(2000);
                                            Service.gI().sendThongBao(player, "Bạn nhận được " + HopQua.template.name);
                                        }).start();
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendMoney(player);
                                        this.npcChat(player, "Khá lắm, đúng là con trai của ta");
                                        this.npcChat(player, "Ta có quà cho con, hãy nhận lấy.");
                                    } catch (IOException e) {
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
                                    Item BanhDayInventory = InventoryService.gI().findItemBag(player, 1543);
                                    if (BanhDayInventory != null) {
                                        InventoryService.gI().subQuantityItemsBag(player, BanhDayInventory, 1);
                                        short[] Param = {3, 7, 15, 30};
                                        short[] List_Item = {1207, 884, 1078, 1083, 1084, 1085, 1086, 1173, 1207, 884};
                                        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                        int Item_Template = itemReceived.template.id;
                                        switch (Item_Template) {
                                            case 1084:
                                            case 1085:
                                            case 1086:
                                                itemReceived.addOptionParam(30, 0);
                                                break;
                                            case 1173:
                                                itemReceived.addOptionParam(30, 0);
                                                itemReceived.quantity = 5;
                                                break;
                                            case 1207:
                                                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(14, 11);
                                                if (Util.isTrue(95, 100)) {
                                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                                }
                                                break;
                                            case 884:
                                                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                                                itemReceived.addOptionParam(14, Util.nextInt(10, 20));
                                                itemReceived.addOptionParam(5, Util.nextInt(20, 120));
                                                if (Util.isTrue(99, 100)) {
                                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        InventoryService.gI().addItemBag(player, itemReceived);
                                        InventoryService.gI().sendItemBag(player);
                                        player.DuaTopDangBanhHungVuong++;
                                        new Thread(() -> {
                                            Functions.sleep(2000);
                                            Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                        }).start();
                                        this.npcChat(player, "Khá lắm, đúng là con trai của ta");
                                        this.npcChat(player, "Ta có quà cho con, hãy nhận lấy.");
                                    } else {
                                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                                    }
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 4) {
                            switch (select) {
                                case 0: {
                                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                        return;
                                    }
                                    Item BanhChungInventory = InventoryService.gI().findItemBag(player, 1556);
                                    if (BanhChungInventory != null) {
                                        InventoryService.gI().subQuantityItemsBag(player, BanhChungInventory, 1);
                                        short[] Param = {3, 7, 15, 30};
                                        short[] List_Item = {1469, 1470, 1471, 1078, 1083, 1084, 1085, 1086, 1173, 1207, 1469, 1470, 1471};
                                        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                        int Item_Template = itemReceived.template.id;
                                        switch (Item_Template) {
                                            case 1084:
                                            case 1085:
                                            case 1086:
                                                itemReceived.addOptionParam(30, 0);
                                                break;
                                            case 1173:
                                                itemReceived.addOptionParam(30, 0);
                                                itemReceived.quantity = 5;
                                                break;
                                            case 1207:
                                                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                                itemReceived.addOptionParam(14, 11);
                                                if (Util.isTrue(95, 100)) {
                                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                                }
                                                break;
                                            case 1469:
                                            case 1470:
                                            case 1471:
                                                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                                                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                                                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                                                itemReceived.addOptionParam(95, 10);
                                                itemReceived.addOptionParam(96, 10);
                                                itemReceived.addOptionParam(101, 25);
                                                itemReceived.addOptionParam(114, 20);
                                                if (Util.isTrue(99, 100)) {
                                                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                        InventoryService.gI().addItemBag(player, itemReceived);
                                        InventoryService.gI().sendItemBag(player);
                                        player.DuaTopDangBanhHungVuong++;
                                        new Thread(() -> {
                                            Functions.sleep(2000);
                                            Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                        }).start();
                                        this.npcChat(player, "Khá lắm, đúng là con trai của ta");
                                        this.npcChat(player, "Ta có quà cho con, hãy nhận lấy.");
                                    } else {
                                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                                    }
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == 0) {
                            switch (select) {
                                case 0: {
                                    if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                        Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                        return;
                                    }
                                    Item HatDua = ItemService.gI().createNewItem((short) 1771, 1);
                                    HatDua.addOptionParam(93, 35);
                                    InventoryService.gI().addItemBag(player, HatDua);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + HatDua.Name());
                                    EventDAO.setRECEIVE_MELON_SEED(System.currentTimeMillis());
                                    EventDAO.save();
                                    break;
                                }
                            }
                        }   
                        break;
                    }
                    default: {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    switch (player.gender) {
                                        case 0:
                                            ChangeMapService.gI().changeMap(player, 5, -1, Util.nextInt(900, 1000), player.zone.map.yPhysicInTop(player.location.x, player.location.y));
                                            break;
                                        case 1:
                                            ChangeMapService.gI().changeMap(player, 13, -1, Util.nextInt(900, 1000), player.zone.map.yPhysicInTop(player.location.x, player.location.y));
                                            break;
                                        case 2:
                                            ChangeMapService.gI().changeMap(player, 20, -1, Util.nextInt(1000, 1100), player.zone.map.yPhysicInTop(player.location.x, player.location.y));
                                            break;
                                        default:
                                            break;
                                    }
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
}