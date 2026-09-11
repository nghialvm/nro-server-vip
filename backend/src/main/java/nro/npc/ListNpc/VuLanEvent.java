package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
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


public class VuLanEvent extends Npc {

    public VuLanEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.VU_LAN_FESTIVAL) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Một đời tần tảo vì con\n" +
                            "Gian lao cha mẹ mỏi mòn tháng năm.\n" +
                            "Nay con khôn lớn âm thầm,\n" +
                            "Lệ rơi mỗi độ trăng rằm Vu Lan.\n\n"
                          + "Bạn cần tôi giúp gì?",
                            "Cửa hàng\nsự kiện", "Bảng\nXếp Hạng\nĐua Top", "Sự kiện", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.VU_LAN_FESTIVAL) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    Item KhungTre = InventoryService.gI().findItemBag(player, 1035);
                    Item GiayMau = InventoryService.gI().findItemBag(player, 1032);
                    Item Nen = InventoryService.gI().findItemBag(player, 1034);
                    Item LoiChuc = InventoryService.gI().findItemBag(player, 1033);
                    boolean KT = false;
                    boolean GM = false;
                    boolean NEN = false;
                    boolean LC = false;
                    if (KhungTre != null && KhungTre.quantity >= 10) {
                        KT = true;
                    }
                    if (GiayMau != null && GiayMau.quantity >= 5) {
                        GM = true;
                    }
                    if (Nen != null && Nen.quantity >= 1) {
                        NEN = true;
                    }
                    if (LoiChuc != null && LoiChuc.quantity >= 1) {
                        LC = true;
                    }
                    //
                    Item HonMa = InventoryService.gI().findItemBag(player, 1258);
                    Item BinhPhep = InventoryService.gI().findItemBag(player, 1259);
                    Item LoNuocHoiSinh = InventoryService.gI().findItemBag(player, 1260);
                    Item BuaHoiSinh = InventoryService.gI().findItemBag(player, 1261);
                    boolean HM = false;
                    boolean BP = false;
                    boolean LNHS = false;
                    boolean BHS = false;
                    if (HonMa != null && HonMa.quantity >= 99) {
                        HM = true;
                    }
                    if (BinhPhep != null && BinhPhep.quantity >= 1) {
                        BP = true;
                    }
                    if (LoNuocHoiSinh != null && LoNuocHoiSinh.quantity >= 1) {
                        LNHS = true;
                    }
                    if (BuaHoiSinh != null && BuaHoiSinh.quantity >= 1) {
                        BHS = true;
                    }
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "VU_LAN_FESTIVAL", true);
                                break;
                            }
                            case 1: {
                                this.createOtherMenu(player, 0,
                                    "Ta có thể giúp gì cho ngươi ?",
                                    "Top 100\nPháo hoa", "Top 100\nHoa Đăng", "Top 100\nHoa đăng\ncó lời chúc", "Đóng");
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 1,
                                            "Ngươi muốn xuống địa ngục tìm Lích Tên à?\n"
                                            + "Nhớ mang theo bình nước phép chứa linh hồn\n"
                                            + "Tìm đủ 99 linh hồn thì tới gặp ta",
                                            "Xuống Địa\nNgục", "Hồi Sinh\nLích Tên", "Ráp\nHoa đăng", "Đóng");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopBanPhaoHoaEventVuLan(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopThaHoaDang(player);
                                break;
                            }
                            case 2: {
                                TopService.showListTopThaHoaDangCoLoiChuc(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                if (player.nPoint.power < 20_000_000) {
                                    Service.gI().sendThongBao(player, "Sức mạnh của bạn không đủ yêu cầu");
                                    return;
                                }
                                ChangeMapService.gI().changeMap(player, 167, -1, Util.nextInt(700, 1100), 408);
                                break;
                            }
                            case 1: {
                                this.createOtherMenu(player, 5,
                                            "Ngươi muốn hồi sinh Lích Tên nào?\n",
                                            "Lích Tên\nthường", "Siêu\nLích Tên", "Đóng");
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 2,
                                            "Hãy chọn kiểu hoa đăng để ráp\n"
                                            + "Kiểu 1 cần 10 khung tre, 5 giấy màu, 1 nến\n"
                                            + "Kiểu 1 cần 10 khung tre, 5 giấy màu, 1 nến và 1 lời chúc",
                                            "Kiểu 1", "Kiểu 2", "Đóng");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0: {
                                if (KT && GM && NEN) {
                                    this.createOtherMenu(player, 3,
                                        "|1|Chế tạo Hoa đăng\n"
                                        + "|2|Khung tre " + KhungTre.quantity + "/10\n"
                                        + "|2|Giấy màu " + GiayMau.quantity + "/5\n"
                                        + "|2|Nến " + Nen.quantity + "/1\n",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Chế tạo Hoa đăng\n"
                                        + (KhungTre == null ? "|7|" : KhungTre.quantity >= 10 ? "|2|" : "|7|") + "Khung tre " + (KhungTre == null ? "0" : KhungTre.quantity) + "/10\n"
                                        + (GiayMau == null ? "|7|" : GiayMau.quantity >= 5 ? "|2|" : "|7|") + "Giấy màu " + (GiayMau == null ? "0" : GiayMau.quantity) + "/5\n"
                                        + (Nen == null ? "|7|" : Nen.quantity >= 1 ? "|2|" : "|7|") + "Nến " + (Nen == null ? "0" : Nen.quantity) + "/1\n",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 1: {
                                if (KT && GM && NEN && LC) {
                                    this.createOtherMenu(player, 4,
                                        "|1|Chế tạo Hoa đăng có lời chúc\n"
                                        + "|2|Khung tre " + KhungTre.quantity + "/10\n"
                                        + "|2|Giấy màu " + GiayMau.quantity + "/5\n"
                                        + "|2|Nến " + Nen.quantity + "/1\n"
                                        + "|2|Lời chúc " + LoiChuc.quantity + "/1\n",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Chế tạo Hoa đăng có lời chúc\n"
                                        + (KhungTre == null ? "|7|" : KhungTre.quantity >= 10 ? "|2|" : "|7|") + "Khung tre " + (KhungTre == null ? "0" : KhungTre.quantity) + "/10\n"
                                        + (GiayMau == null ? "|7|" : GiayMau.quantity >= 5 ? "|2|" : "|7|") + "Giấy màu " + (GiayMau == null ? "0" : GiayMau.quantity) + "/5\n"
                                        + (Nen == null ? "|7|" : Nen.quantity >= 1 ? "|2|" : "|7|") + "Nến " + (Nen == null ? "0" : Nen.quantity) + "/1\n"
                                        + (LoiChuc == null ? "|7|" : LoiChuc.quantity >= 1 ? "|2|" : "|7|") + "Lời chúc " + (LoiChuc == null ? "0" : LoiChuc.quantity) + "/1\n",
                                        "Từ chối");
                                }
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
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, KhungTre));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GiayMau));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Nen));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(9850);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, KhungTre, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, GiayMau, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, Nen, 1);
                                    Item HoaDang = ItemService.gI().createNewItem((short) 1037);
                                    HoaDang.addOptionParam(87, 0);
                                    HoaDang.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, HoaDang);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HoaDang.template.name);
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
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, KhungTre));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, GiayMau));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Nen));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, LoiChuc));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(9851);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, KhungTre, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, GiayMau, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, Nen, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, LoiChuc, 1);
                                    Item HoaDang = ItemService.gI().createNewItem((short) 1038);
                                    HoaDang.addOptionParam(87, 0);
                                    HoaDang.addOptionParam(30, 0);
                                    InventoryService.gI().addItemBag(player, HoaDang);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HoaDang.template.name);
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
                    } else if (player.iDMark.getIndexMenu() == 5) {
                        switch (select) {
                            case 0: {
                                if (HM && BP && LNHS) {
                                    this.createOtherMenu(player, 6,
                                        "|1|Hồi sinh siêu Lích Tên\n"
                                        + "|2|Hồn ma " + HonMa.quantity + "/99\n"
                                        + "|2|Bình phép " + BinhPhep.quantity + "/1\n"
                                        + "|2|Lọ nước hồi sinh " + LoNuocHoiSinh.quantity + "/1\n"
                                        + "|2|Giá vàng: 100.000.000",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Hồi sinh siêu Lích Tên\n"
                                        + (HonMa == null ? "|7|" : HonMa.quantity >= 99 ? "|2|" : "|7|") + "Hồn ma " + (HonMa == null ? "0" : HonMa.quantity) + "/99\n"
                                        + (BinhPhep == null ? "|7|" : BinhPhep.quantity >= 1 ? "|2|" : "|7|") + "Bình phép " + (BinhPhep == null ? "0" : BinhPhep.quantity) + "/1\n"
                                        + (LoNuocHoiSinh == null ? "|7|" : LoNuocHoiSinh.quantity >= 1 ? "|2|" : "|7|") + "Lọ nước hồi sinh " + (LoNuocHoiSinh == null ? "0" : LoNuocHoiSinh.quantity) + "/1\n"
                                        + "|2|Giá vàng: 100.000.000",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 1: {
                                if (HM && BP && LNHS && BHS) {
                                    this.createOtherMenu(player, 7,
                                        "|1|Hồi sinh siêu Lích Tên\n"
                                        + "|2|Hồn ma " + HonMa.quantity + "/99\n"
                                        + "|2|Bình phép " + BinhPhep.quantity + "/1\n"
                                        + "|2|Lọ nước hồi sinh " + LoNuocHoiSinh.quantity + "/1\n"
                                        + "|2|Bùa hồi sinh " + BuaHoiSinh.quantity + "/1\n"
                                        + "|2|Giá vàng: 100.000.000",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Hồi sinh siêu Lích Tên\n"
                                        + (HonMa == null ? "|7|" : HonMa.quantity >= 99 ? "|2|" : "|7|") + "Hồn ma " + (HonMa == null ? "0" : HonMa.quantity) + "/99\n"
                                        + (BinhPhep == null ? "|7|" : BinhPhep.quantity >= 1 ? "|2|" : "|7|") + "Bình phép " + (BinhPhep == null ? "0" : BinhPhep.quantity) + "/1\n"
                                        + (LoNuocHoiSinh == null ? "|7|" : LoNuocHoiSinh.quantity >= 1 ? "|2|" : "|7|") + "Lọ nước hồi sinh " + (LoNuocHoiSinh == null ? "0" : LoNuocHoiSinh.quantity) + "/1\n"
                                        + (BuaHoiSinh == null ? "|7|" : BuaHoiSinh.quantity >= 1 ? "|2|" : "|7|") + "Bùa hồi sinh " + (BuaHoiSinh == null ? "0" : BuaHoiSinh.quantity) + "/1\n"
                                        + "|2|Giá vàng: 100.000.000",
                                        "Từ chối");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 6) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getGold() < 100_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
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
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HonMa));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BinhPhep));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, LoNuocHoiSinh));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(11475);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, HonMa, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, BinhPhep, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, LoNuocHoiSinh, 1);
                                    Item LichTen = ItemService.gI().createNewItem((short) 1262);
                                    LichTen.addOptionParam(20, 2007);
                                    InventoryService.gI().addItemBag(player, LichTen);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + LichTen.template.name);
                                    }).start();
                                    player.inventory.subGold(100_000_000);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 7) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getGold() < 100_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
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
                                    msgg.writer().writeByte(4);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HonMa));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BinhPhep));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, LoNuocHoiSinh));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, BuaHoiSinh));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(11476);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, HonMa, 99);
                                    InventoryService.gI().subQuantityItemsBag(player, BinhPhep, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, LoNuocHoiSinh, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, BuaHoiSinh, 1);
                                    Item SieuLichTen = ItemService.gI().createNewItem((short) 1263);
                                    SieuLichTen.addOptionParam(20, 2007);
                                    InventoryService.gI().addItemBag(player, SieuLichTen);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + SieuLichTen.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
                                }
                                player.inventory.subGold(100_000_000);
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
