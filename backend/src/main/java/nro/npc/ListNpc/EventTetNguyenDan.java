package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;


public class EventTetNguyenDan extends Npc {

    public EventTetNguyenDan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Tết về rực rỡ sắc xuân,\n" +
                            "Mâm ngũ quả thắm muôn phần đẹp thay.\n" +
                            "Nào dưa hấu đỏ căng đầy,\n" +
                            "Cầu mong may mắn tràn đầy quanh năm.\n\n"
                          + "Tết rồi, cậu cần ta giúp gì?",
                            "Xin Chữ\nĐầu Năm", "Ghép Chữ\nĐầu Năm", "Bày Mâm\nNgũ Quả", "Cửa hàng", "Bảng\nXếp Hạng\nĐua Top", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.LUNNAR_NEW_YEAR) {
                Item Van = InventoryService.gI().findItemBag(player, 1488);
                Item Su = InventoryService.gI().findItemBag(player, 1489);
                Item Nhu = InventoryService.gI().findItemBag(player, 1490);
                Item Y = InventoryService.gI().findItemBag(player, 1491);
                Item N2024 = InventoryService.gI().findItemBag(player, 1492);
                boolean VANN = false;
                boolean SUU = false;
                boolean NHUU = false;
                boolean YY = false;
                boolean NN2024 = false;
                if (Van != null && Van.quantity >= 1) {
                    VANN = true;
                }
                if (Su != null && Su.quantity >= 1) {
                    SUU = true;
                }
                if (Nhu != null && Nhu.quantity >= 1) {
                    NHUU = true;
                }
                if (Y != null && Y.quantity >= 1) {
                    YY = true;
                }
                if (N2024 != null && N2024.quantity >= 1) {
                    NN2024 = true;
                }
                //
                Item ThiepDo = InventoryService.gI().findItemBag(player, 1494);
                Item ThiepDoVIP = InventoryService.gI().findItemBag(player, 1495);
                //
                Item MangCau = InventoryService.gI().findItemBag(player, 1177);
                Item QuaDua = InventoryService.gI().findItemBag(player, 1178);
                Item DuDu = InventoryService.gI().findItemBag(player, 1179);
                Item QuaXoai = InventoryService.gI().findItemBag(player, 1180);
                Item TraiSung = InventoryService.gI().findItemBag(player, 1181);
                Item BaoLiXiMeo = InventoryService.gI().findItemBag(player, 1183);
                boolean MC = false;
                boolean QD = false;
                boolean DD = false;
                boolean QX = false;
                boolean TS = false;
                boolean BLX = false;
                if (MangCau != null && MangCau.quantity >= 20) {
                    MC = true;
                }
                if (QuaDua != null && QuaDua.quantity >= 20) {
                    QD = true;
                }
                if (DuDu != null && DuDu.quantity >= 20) {
                    DD = true;
                }
                if (QuaXoai != null && QuaXoai.quantity >= 20) {
                    QX = true;
                }
                if (TraiSung != null && TraiSung.quantity >= 20) {
                    TS = true;
                }
                if (BaoLiXiMeo != null && BaoLiXiMeo.quantity >= 1) {
                    BLX = true;
                }
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, 1,
                                    "Ta sẽ cho con chữ, con hãy chọn thiệp để nhận chữ\n"
                                  + "Phí là 10 hồng ngọc nhé",
                                    "Xin bằng\nThiệp Đỏ", "Xin bằng\nThiệp Đỏ VIP", "Đóng");
                                break;
                            }
                            case 1: {
                                if (VANN && SUU && NHUU && YY && NN2024) {
                                    this.createOtherMenu(player, 2,
                                        "|1|Ghép chữ đầu năm\n"
                                        + "|2|Vạn " + Van.quantity + "/1\n"
                                        + "|2|Sự " + Su.quantity + "/1\n"
                                        + "|2|Như " + Nhu.quantity + "/1\n"
                                        + "|2|Ý " + Y.quantity + "/1\n"
                                        + "|2|2024 " + N2024.quantity + "/1\n",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Ghép chữ đầu năm\n"
                                        + (Van == null ? "|7|" : Van.quantity >= 1 ? "|2|" : "|7|") + "Vạn " + (Van == null ? "0" : Van.quantity) + "/1\n"
                                        + (Su == null ? "|7|" : Su.quantity >= 1 ? "|2|" : "|7|") + "Sự " + (Su == null ? "0" : Su.quantity) + "/1\n"
                                        + (Nhu == null ? "|7|" : Nhu.quantity >= 1 ? "|2|" : "|7|") + "Như " + (Nhu == null ? "0" : Nhu.quantity) + "/1\n"
                                        + (Y == null ? "|7|" : Y.quantity >= 1 ? "|2|" : "|7|") + "Ý " + (Y == null ? "0" : Y.quantity) + "/1\n"
                                        + (N2024 == null ? "|7|" : N2024.quantity >= 1 ? "|2|" : "|7|") + "2024 " + (N2024 == null ? "0" : N2024.quantity) + "/1\n",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 2: {
                                if (MC && QD && DD && QX && TS && BLX) {
                                    this.createOtherMenu(player, 0,
                                        "|1|Bạn muốn bày mâm ngũ quả?\n"
                                        + "|2|Mãng cầu " + MangCau.quantity + "/20\n"
                                        + "|2|Quả dừa " + QuaDua.quantity + "/20\n"
                                        + "|2|Đu đủ " + DuDu.quantity + "/20\n"
                                        + "|2|Quả xoài " + QuaXoai.quantity + "/20\n"
                                        + "|2|Trái sung " + TraiSung.quantity + "/20\n"
                                        + "|2|Bao lì xì rồng " + BaoLiXiMeo.quantity + "/1\n"
                                        + "|2|Giá vàng : 500 Triệu vàng",
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        "|1|Bạn muốn bày mâm ngũ quả?\n"
                                        + (MangCau == null ? "|7|" : MangCau.quantity >= 20 ? "|2|" : "|7|") + "Mãng cầu " + (MangCau == null ? "0" : MangCau.quantity) + "/20\n"
                                        + (DuDu == null ? "|7|" : DuDu.quantity >= 20 ? "|2|" : "|7|") + "Đu đủ " + (DuDu == null ? "0" : DuDu.quantity) + "/20\n"
                                        + (QuaXoai == null ? "|7|" : QuaXoai.quantity >= 20 ? "|2|" : "|7|") + "Quả xoài " + (QuaXoai == null ? "0" : QuaXoai.quantity) + "/20\n"
                                        + (TraiSung == null ? "|7|" : TraiSung.quantity >= 20 ? "|2|" : "|7|") + "Trái sung " + (TraiSung == null ? "0" : TraiSung.quantity) + "/20\n"
                                        + (QuaDua == null ? "|7|" : QuaDua.quantity >= 20 ? "|2|" : "|7|") + "Quả dừa " + (QuaDua == null ? "0" : QuaDua.quantity) + "/20\n"
                                        + (BaoLiXiMeo == null ? "|7|" : BaoLiXiMeo.quantity >= 1 ? "|2|" : "|7|") + "Bao lì xì rồng " + (BaoLiXiMeo == null ? "0" : BaoLiXiMeo.quantity) + "/1\n"
                                        + "|2|Giá vàng : 500 Triệu vàng",
                                        "Từ chối");
                                }
                                break;
                            }
                            case 3: {
                                this.createOtherMenu(player, 3,
                                    "Ta có thể giúp gì cho ngươi ?",
                                    "Cửa Hàng", "Đổi Điểm\nSự Kiện\n[" + player.event.getLunaNewYearPoint() + "]", "Đóng");
                                break;
                            }
                            case 4: {
                                this.createOtherMenu(player, 4,
                                    "Ta có thể giúp gì cho ngươi ?",
                                    "Top Tặng\nLì Xì", "Top Mở\nLì Xì", "Top\nPháo Hoa", "Top\nPháo Hoa\nVIP", "Đóng");
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getGold() < 500_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                try {
                                    String Message = "Chúc mừng con nhé";
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(6);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, MangCau));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, DuDu));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, QuaXoai));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, TraiSung));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, QuaDua));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, BaoLiXiMeo));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(10889);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    this.npcChat(player, Message);
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }
                                InventoryService.gI().subQuantityItemsBag(player, MangCau, 20);
                                InventoryService.gI().subQuantityItemsBag(player, DuDu, 20);
                                InventoryService.gI().subQuantityItemsBag(player, QuaXoai, 20);
                                InventoryService.gI().subQuantityItemsBag(player, TraiSung, 20);
                                InventoryService.gI().subQuantityItemsBag(player, QuaDua, 20);
                                InventoryService.gI().subQuantityItemsBag(player, BaoLiXiMeo, 1);
                                player.inventory.gold -= 500_000_000;
                                Item MamNguQua = ItemService.gI().createNewItem((short) 1182);
                                MamNguQua.addOptionParam(86, 0);
                                MamNguQua.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, MamNguQua);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                new Thread(() -> {
                                    Functions.sleep(3000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + MamNguQua.template.name);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getRuby() < 10) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                if (ThiepDo == null || ThiepDo.quantity <= 0) {
                                    Service.gI().sendThongBao(player, "Bạn không có thiệp đỏ để thực hiện");
                                    return;
                                }
                                short[] List_Item = {1488, 1489, 1490, 1491, 1492};
                                Item itemReceived = (Util.isTrue(60, 100) ? ItemService.gI().createNewItem(List_Item[Util.nextInt(0, 1)]) : 
                                        Util.isTrue(50, 100) ? ItemService.gI().createNewItem(List_Item[Util.nextInt(2, 3)]) 
                                        : ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]));
                                try {
                                    String Message = "Chúc mừng con nhé";
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(2);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThiepDo));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThiepDo));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(itemReceived.template.iconID);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    this.npcChat(player, Message);
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }                                
                                InventoryService.gI().subQuantityItemsBag(player, ThiepDo, 1);
                                player.inventory.subRuby(10);
                                itemReceived.addOptionParam(30, 0);
                                itemReceived.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, itemReceived);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                new Thread(() -> {
                                    Functions.sleep(2000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                break;
                            }
                        case 1: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (player.inventory.getRuby() < 10) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                if (ThiepDoVIP == null || ThiepDoVIP.quantity <= 0) {
                                    Service.gI().sendThongBao(player, "Bạn không có thiệp đỏ để thực hiện");
                                    return;
                                }
                                short[] List_Item = {1488, 1489, 1490, 1491, 1492};
                                Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                try {
                                    String Message = "Chúc mừng con nhé";
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(2);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThiepDoVIP));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThiepDoVIP));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(itemReceived.template.iconID);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    this.npcChat(player, Message);
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }                                
                                InventoryService.gI().subQuantityItemsBag(player, ThiepDoVIP, 1);
                                player.inventory.subRuby(10);
                                itemReceived.addOptionParam(30, 0);
                                itemReceived.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, itemReceived);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendMoney(player);
                                new Thread(() -> {
                                    Functions.sleep(2000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                try {
                                    String Message = "Chúc mừng con nhé";
                                    Message msg = new Message(-81);
                                    msg.writer().writeByte(0);
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeUTF("MaiTienDung");
                                    msg.writer().writeShort(tempId);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(1);
                                    msg.writer().writeByte(5);
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, Van));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, Su));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, Nhu));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, Y));
                                    msg.writer().writeByte(InventoryService.gI().getIndexBag(player, N2024));
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    msg = new Message(-81);
                                    msg.writer().writeByte(7);
                                    msg.writer().writeShort(10895);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    msg.writer().writeShort(-1);
                                    player.sendMessage(msg);
                                    msg.cleanup();
                                    this.npcChat(player, Message);
                                } catch (Exception e) {
                                    System.out.println("ERROR 404");
                                }                                
                                InventoryService.gI().subQuantityItemsBag(player, Van, 1);
                                InventoryService.gI().subQuantityItemsBag(player, Su, 1);
                                InventoryService.gI().subQuantityItemsBag(player, Nhu, 1);
                                InventoryService.gI().subQuantityItemsBag(player, Y, 1);
                                InventoryService.gI().subQuantityItemsBag(player, N2024, 1);
                                Item PhongBiTet = ItemService.gI().createNewItem((short) 1493);
                                PhongBiTet.addOptionParam(30, 0);
                                PhongBiTet.addOptionParam(93, 30);
                                InventoryService.gI().addItemBag(player, PhongBiTet);
                                InventoryService.gI().sendItemBag(player);
                                new Thread(() -> {
                                    Functions.sleep(2000);
                                    player.event.addLunaNewYearPoint(1);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + PhongBiTet.template.name);
                                    Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm sự kiện");
                                }).start();
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "LUNAR_NEW_YEAR", false);
                                break;
                            }
                            case 1: {
                                ShopService.gI().opendShop(player, "LUNAR_NEW_YEAR_EVENT", true);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 4) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopTangLixi(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopMoLixi(player);
                                break;
                            }
                            case 2: {
                                TopService.showListTopBanPhaoHoa(player);
                                break;
                            }
                            case 3: {
                                TopService.showListTopBanPhaoHoaVIP(player);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
