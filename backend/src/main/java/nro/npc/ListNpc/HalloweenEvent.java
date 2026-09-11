package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.io.IOException;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;


public class HalloweenEvent extends Npc {

    public HalloweenEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Bạn cần tôi giúp gì?",
                            "Cửa hàng\nsự kiện", "Bảng\nXếp Hạng\nĐua Top", "Sự kiện", "Đổi Điểm\n" + "[" + player.event.getHalloweenPoint() + "]", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.HALLOWEEN) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    Item KeoBanTay = InventoryService.gI().findItemBag(player, 901);
                    Item GioDungKeoTraiBi = InventoryService.gI().findItemBag(player, 1355);
                    Item GiayTrangTriHalloween = InventoryService.gI().findItemBag(player, 1354);
                    boolean KBT = false;
                    boolean GDKEO = false;
                    boolean GIAYTT = false;
                    boolean KBTx10 = false;
                    boolean GDKEOx10 = false;
                    boolean GIAYTTx10 = false;
                    if (KeoBanTay != null && KeoBanTay.quantity >= 99) {
                        KBT = true;
                    }
                    if (GioDungKeoTraiBi != null && GioDungKeoTraiBi.quantity >= 1) {
                        GDKEO = true;
                    }
                    if (GiayTrangTriHalloween != null && GiayTrangTriHalloween.quantity >= 3) {
                        GIAYTT = true;
                    }
                    if (KeoBanTay != null && KeoBanTay.quantity >= 99 * 10) {
                        KBTx10 = true;
                    }
                    if (GioDungKeoTraiBi != null && GioDungKeoTraiBi.quantity >= 1 * 10) {
                        GDKEOx10 = true;
                    }
                    if (GiayTrangTriHalloween != null && GiayTrangTriHalloween.quantity >= 3 * 10) {
                        GIAYTTx10 = true;
                    }
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "HALLOWEEN_EVENT", true);
                                break;
                            }
                            case 1: {
                                this.createOtherMenu(player, 0,
                                    "Ta có thể giúp gì cho ngươi ?",
                                    "Top 100\nHộp Kẹo", "Top 100\nThiệp\nHalloween", "Đóng");
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 1,
                                            "Halloween vui vẻ\n"
                                            + "Ngươi cần ta giúp gì?\n",
                                            "Nhận quà", "Làm Hộp\nKẹo\nMa Quỷ", "Làm 10 Hộp\nKẹo\nMa Quỷ", "Ước bằng\n1000 ngọc", "Đóng");
                                break;
                            }
                            case 3: {
                                ShopService.gI().opendShop(player, "HALLOWEEN_EVENT_2", true);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopMoHopQuaMaQuy(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopThiepHalloween(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, 2,
                                            "Ngươi muốn đổi quà gì nào?\n"
                                            + "1) 50 Bí ngô: Cải trang bộ xương\n"
                                            + "2) 50 Bí ngô: Cải trang ma trơi\n"
                                            + "3) 50 Bí ngô: Cải trang dơi nhí\n"
                                            + "4) 99 Bí ngô: Phiếu giảm giá\n",
                                            "1) 50\nBí ngô", "2) 50\nBí ngô", "3) 50\nBí ngô", "4) 99\nBí ngô", "Ok");
                                break;
                            }
                            case 1: {
                                if (KBT && GDKEO && GIAYTT) {
                                    this.createOtherMenu(player, 3,
                                        "|1|Hộp Kẹo Ma Quỷ\n"
                                        + "|2|Kẹo bàn tay " + KeoBanTay.quantity + "/99\n"
                                        + "|2|Giỏ đựng kẹo trái bí " + GioDungKeoTraiBi.quantity + "/1\n"
                                        + "|2|Giấy trang trí Halloween " + GiayTrangTriHalloween.quantity + "/3\n",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Hộp Kẹo Ma Quỷ\n"
                                        + (KeoBanTay == null ? "|7|" : KeoBanTay.quantity >= 99 ? "|2|" : "|7|") + "Kẹo bàn tay " + (KeoBanTay == null ? "0" : KeoBanTay.quantity) + "/99\n"
                                        + (GioDungKeoTraiBi == null ? "|7|" : GioDungKeoTraiBi.quantity >= 1 ? "|2|" : "|7|") + "Giỏ đựng kẹo trái bí " + (GioDungKeoTraiBi == null ? "0" : GioDungKeoTraiBi.quantity) + "/1\n"
                                        + (GiayTrangTriHalloween == null ? "|7|" : GiayTrangTriHalloween.quantity >= 3 ? "|2|" : "|7|") + "Giấy trang trí Halloween " + (GiayTrangTriHalloween == null ? "0" : GiayTrangTriHalloween.quantity) + "/3\n",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 2: {
                                if (KBTx10 && GDKEOx10 && GIAYTTx10) {
                                    this.createOtherMenu(player, 4,
                                        "|1|Hộp Kẹo Ma Quỷ\n"
                                        + "|2|Kẹo bàn tay " + KeoBanTay.quantity + "/990\n"
                                        + "|2|Giỏ đựng kẹo trái bí " + GioDungKeoTraiBi.quantity + "/10\n"
                                        + "|2|Giấy trang trí Halloween " + GiayTrangTriHalloween.quantity + "/30\n",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Hộp Kẹo Ma Quỷ\n"
                                        + (KeoBanTay == null ? "|7|" : KeoBanTay.quantity >= 990 ? "|2|" : "|7|") + "Kẹo bàn tay " + (KeoBanTay == null ? "0" : KeoBanTay.quantity) + "/990\n"
                                        + (GioDungKeoTraiBi == null ? "|7|" : GioDungKeoTraiBi.quantity >= 10 ? "|2|" : "|7|") + "Giỏ đựng kẹo trái bí " + (GioDungKeoTraiBi == null ? "0" : GioDungKeoTraiBi.quantity) + "/10\n"
                                        + (GiayTrangTriHalloween == null ? "|7|" : GiayTrangTriHalloween.quantity >= 30 ? "|2|" : "|7|") + "Giấy trang trí Halloween " + (GiayTrangTriHalloween == null ? "0" : GiayTrangTriHalloween.quantity) + "/30\n",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 3: {
                                if (player.inventory.ruby < 1000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                player.inventory.subRuby(1000);
                                Service.gI().sendMoney(player);
                                short[] Param = {3, 5, 7, 15, 30};
                                short[] List_Item = {899, 900, 901, 902, 903, 740, 910, 908, 909, 904, 1143, 866, 867, 868, 1344};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                int Item_Template = itemReceived.template.id;
                                if (Item_Template == 1344) {
                                    itemReceived.addOptionParam(50, 18);
                                    itemReceived.addOptionParam(77, 18);
                                    itemReceived.addOptionParam(103, 18);
                                    itemReceived.addOptionParam(14, 15);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 1143) {
                                    itemReceived.addOptionParam(30, 0);
                                }
                                if (Item_Template >= 866 && Item_Template <= 868) {
                                    itemReceived.addOptionParam(50, 20);
                                    itemReceived.addOptionParam(77, 20);
                                    itemReceived.addOptionParam(103, 20);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 904) {
                                    itemReceived.addOptionParam(50, 24);
                                    itemReceived.addOptionParam(77, 21);
                                    itemReceived.addOptionParam(103, 21);
                                    itemReceived.addOptionParam(117, 5);
                                    itemReceived.addOptionParam(14, Util.nextInt(10, 20));
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 740) {
                                    itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                                    itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                                    itemReceived.addOptionParam(14, 11);
                                    itemReceived.addOptionParam(5, Util.nextInt(10, 20));
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 909) {
                                    itemReceived.addOptionParam(50, Util.nextInt(12, 15));
                                    itemReceived.addOptionParam(14, Util.nextInt(10, 13));
                                    itemReceived.addOptionParam(5, Util.nextInt(15, 20));
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 908) {
                                    itemReceived.addOptionParam(103, Util.nextInt(17, 20));
                                    itemReceived.addOptionParam(81, Util.nextInt(17, 20));
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                if (Item_Template == 910) {
                                    itemReceived.addOptionParam(77, Util.nextInt(17, 20));
                                    itemReceived.addOptionParam(97, 11);
                                    if (Util.isTrue(99, 100)) {
                                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                    }
                                }
                                InventoryService.gI().addItemBag(player, itemReceived);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        Item BiNgo = InventoryService.gI().findItemBag(player, 585);
                        switch (select) {
                            case 0: {
                                if (BiNgo != null && BiNgo.quantity < 50) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ bí ngô");
                                    return;
                                }
                                if (BiNgo == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bí ngô");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item CaiTrang = ItemService.gI().createNewItem((short) (644 + player.gender), 1);
                                CaiTrang.addOptionParam(50, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(77, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(103, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(101, 30);
                                CaiTrang.addOptionParam(95, 20);
                                CaiTrang.addOptionParam(96, 20);
                                CaiTrang.addOptionParam(32, 0);
                                if (Util.isTrue(99, 100)) {
                                    CaiTrang.addOptionParam(93, 7);
                                }
                                InventoryService.gI().subQuantityItemsBag(player, BiNgo, 50);
                                InventoryService.gI().addItemBag(player, CaiTrang);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + CaiTrang.template.name);
                                break;
                            }
                            case 1: {
                                if (BiNgo != null && BiNgo.quantity < 50) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ bí ngô");
                                    return;
                                }
                                if (BiNgo == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bí ngô");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item CaiTrang = ItemService.gI().createNewItem((short) 642, 1);
                                CaiTrang.addOptionParam(50, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(77, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(103, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(101, 30);
                                CaiTrang.addOptionParam(95, 20);
                                CaiTrang.addOptionParam(96, 20);
                                CaiTrang.addOptionParam(32, 0);
                                if (Util.isTrue(99, 100)) {
                                    CaiTrang.addOptionParam(93, 7);
                                }
                                InventoryService.gI().subQuantityItemsBag(player, BiNgo, 50);
                                InventoryService.gI().addItemBag(player, CaiTrang);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + CaiTrang.template.name);
                                break;
                            }
                            case 2: {
                                if (BiNgo != null && BiNgo.quantity < 50) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ bí ngô");
                                    return;
                                }
                                if (BiNgo == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bí ngô");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item CaiTrang = ItemService.gI().createNewItem((short) 643, 1);
                                CaiTrang.addOptionParam(50, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(77, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(103, Util.nextInt(20, 25));
                                CaiTrang.addOptionParam(101, 30);
                                CaiTrang.addOptionParam(95, 20);
                                CaiTrang.addOptionParam(96, 20);
                                CaiTrang.addOptionParam(32, 0);
                                if (Util.isTrue(99, 100)) {
                                    CaiTrang.addOptionParam(93, 7);
                                }
                                InventoryService.gI().subQuantityItemsBag(player, BiNgo, 50);
                                InventoryService.gI().addItemBag(player, CaiTrang);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + CaiTrang.template.name);
                                break;
                            }
                            case 3: {
                                if (BiNgo != null && BiNgo.quantity < 99) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ bí ngô");
                                    return;
                                }
                                if (BiNgo == null) {
                                    Service.gI().sendThongBao(player, "Bạn không có bí ngô");
                                    return;
                                }
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                Item Pgg = ItemService.gI().createNewItem((short) 459, 1);
                                Pgg.addOptionParam(112, 80);
                                Pgg.addOptionParam(93, 90);
                                Pgg.addOptionParam(30, 0);
                                InventoryService.gI().subQuantityItemsBag(player, BiNgo, 99);
                                InventoryService.gI().addItemBag(player, Pgg);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + Pgg.template.name);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
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
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, KeoBanTay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GioDungKeoTraiBi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GiayTrangTriHalloween));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(11743);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, KeoBanTay, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, GioDungKeoTraiBi, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, GiayTrangTriHalloween, 3);
                                    Item HopKeoMaQuy = ItemService.gI().createNewItem((short) 1356);
                                    HopKeoMaQuy.addOptionParam(30, 0);
                                    HopKeoMaQuy.addOptionParam(87, 0);
                                    HopKeoMaQuy.addOptionParam(93, 35);
                                    InventoryService.gI().addItemBag(player, HopKeoMaQuy);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        player.event.addHalloweenPoint(1);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HopKeoMaQuy.template.name);
                                    }).start();
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
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
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
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, KeoBanTay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GioDungKeoTraiBi));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GiayTrangTriHalloween));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(11743);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, KeoBanTay, 990);
                                    InventoryService.gI().subQuantityItemsBag(player, GioDungKeoTraiBi, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, GiayTrangTriHalloween, 30);
                                    Item HopKeoMaQuy = ItemService.gI().createNewItem((short) 1356);
                                    HopKeoMaQuy.addOptionParam(30, 0);
                                    HopKeoMaQuy.addOptionParam(87, 0);
                                    HopKeoMaQuy.addOptionParam(93, 35);
                                    HopKeoMaQuy.quantity = 10;
                                    InventoryService.gI().addItemBag(player, HopKeoMaQuy);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        player.event.addHalloweenPoint(10);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HopKeoMaQuy.template.name);
                                    }).start();
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
            }
        }
    }
}
