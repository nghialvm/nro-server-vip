package nro.npc.ListNpc;



import nro.inventory.InventoryService;
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

public class TrungThuEvent extends Npc {

    public TrungThuEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.TRUNG_THU) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Chúc các bạn trung thu vui vẻ",
                            "Cửa Hàng", "Đổi\nThỏ Cưng", "Đổi 99\nCarot\nLấy Quà", "Đổi 99\nĐuôi Khỉ\nLấy Quà", "Đổi Điểm\n[" + player.event.getTrungThuPoint() +"]", "Đua Top", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.TRUNG_THU) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "TRUNG_THU_EVENT", true);
                                break;
                            }
                            case 1: {
                                createOtherMenu(player, 0, "Đổi 20 bánh trung thu Gà quay lấy Thỏ xám\n"
                                        + "Đổi 10 bánh trung thu thập cẩm lấy Thỏ trắng\n"
                                        + "Đổi 10 bánh trung thu hạt Sen lấy Pet mèo đuôi vàng\n"
                                        + "Hạn sử dụng hoặc vĩnh viễn.",
                                        "Đổi\nThỏ Xám", "Đổi\nThỏ Trắng", "Đổi Pet\nMèo\nĐuôi Vàng", "Từ chối");
                                break;
                            }
                            case 2: {
                                ShopService.gI().opendShop(player, "TRUNG_THU_EVENT_CAROT", true);
                                break;
                            }
                            case 3: {
                                ShopService.gI().opendShop(player, "TRUNG_THU_EVENT_DUOIKHI", true);
                                break;
                            }
                            case 4: {
                                ShopService.gI().opendShop(player, "TRUNG_THU_EVENT_2", true);
                                break;
                            }
                            case 5: {
                                createOtherMenu(player, 1, "Chúc các bạn trung thu vui vẻ",
                                        "Top\nNấu Bánh", "Top Mở\nHộp Bánh\nTrung Thung\nĐặc Biệt", "Đóng");
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
                                Item BanhGaQuay = InventoryService.gI().findItemBag(player, 890);
                                Item ThoXam = ItemService.gI().createNewItem((short) 892);
                                if (BanhGaQuay != null) {
                                    if (BanhGaQuay.quantity < 20) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Bánh trung thu gà quay.");
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, BanhGaQuay, 20);
                                    ThoXam.addOptionParam(50, Util.nextInt(15, 17));
                                    ThoXam.addOptionParam(77, Util.nextInt(15, 17));
                                    ThoXam.addOptionParam(103, Util.nextInt(15, 17));
                                    ThoXam.addOptionParam(80, 15);
                                    ThoXam.addOptionParam(81, 15);
                                    ThoXam.addOptionParam(94, 15);
                                    if (Util.isTrue(80, 100)) {
                                        ThoXam.addOptionParam(93, 30);
                                    }
                                    InventoryService.gI().addItemBag(player, ThoXam);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + ThoXam.Name());
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Bánh trung thu gà quay.");
                                }
                                break;
                            }
                            case 1: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                Item BanhThapCam = InventoryService.gI().findItemBag(player, 891);
                                Item ThoTrang = ItemService.gI().createNewItem((short) 893);
                                if (BanhThapCam != null) {
                                    if (BanhThapCam.quantity < 10) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Bánh trung thu thập cẩm.");
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, BanhThapCam, 10);
                                    ThoTrang.addOptionParam(50, Util.nextInt(15, 17));
                                    ThoTrang.addOptionParam(77, Util.nextInt(15, 17));
                                    ThoTrang.addOptionParam(103, Util.nextInt(15, 17));
                                    ThoTrang.addOptionParam(14, 11);
                                    ThoTrang.addOptionParam(5, 20);
                                    ThoTrang.addOptionParam(10, Util.nextInt(7, 12));
                                    if (Util.isTrue(80, 100)) {
                                        ThoTrang.addOptionParam(93, 30);
                                    }
                                    InventoryService.gI().addItemBag(player, ThoTrang);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + ThoTrang.Name());
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Bánh trung thu thập cẩm.");
                                }
                                break;
                            }
                            case 2: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                Item BanhHatSen = InventoryService.gI().findItemBag(player, 1313);
                                Item MeoDenDuoiVang = ItemService.gI().createNewItem((short) 1188);
                                Item MeoTrangDuoiVang = ItemService.gI().createNewItem((short) 1202);
                                if (BanhHatSen != null) {
                                    if (BanhHatSen.quantity < 10) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Bánh trung thu hạt sen.");
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, BanhHatSen, 10);
                                    if (Util.isTrue(50, 100)) {
                                        MeoDenDuoiVang.addOptionParam(50, Util.nextInt(15, 17));
                                        MeoDenDuoiVang.addOptionParam(77, Util.nextInt(15, 17));
                                        MeoDenDuoiVang.addOptionParam(103, Util.nextInt(15, 17));
                                        MeoDenDuoiVang.addOptionParam(236, Util.nextInt(15, 20));
                                        MeoDenDuoiVang.addOptionParam(95, 10);
                                        MeoDenDuoiVang.addOptionParam(96, 10);
                                        if (Util.isTrue(80, 100)) {
                                            MeoDenDuoiVang.addOptionParam(93, 30);
                                        }
                                        InventoryService.gI().addItemBag(player, MeoDenDuoiVang);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + MeoDenDuoiVang.Name());
                                    } else {
                                        MeoTrangDuoiVang.addOptionParam(50, Util.nextInt(15, 17));
                                        MeoTrangDuoiVang.addOptionParam(77, Util.nextInt(15, 17));
                                        MeoTrangDuoiVang.addOptionParam(103, Util.nextInt(15, 17));
                                        MeoTrangDuoiVang.addOptionParam(236, Util.nextInt(15, 20));
                                        MeoTrangDuoiVang.addOptionParam(95, 10);
                                        MeoTrangDuoiVang.addOptionParam(96, 10);
                                        if (Util.isTrue(80, 100)) {
                                            MeoTrangDuoiVang.addOptionParam(93, 30);
                                        }
                                        InventoryService.gI().addItemBag(player, MeoTrangDuoiVang);
                                        InventoryService.gI().sendItemBag(player);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + MeoTrangDuoiVang.Name());
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Bánh trung thu hạt sen.");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopLamBanhTrungThu(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopMoBanhTrungThuDacBiet(player);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
