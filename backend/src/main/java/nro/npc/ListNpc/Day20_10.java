package nro.npc.ListNpc;



import Utils.Functions;
import Utils.ItemCheckUtil;
import nro.inventory.InventoryService;
import nro.services.Fun.ChangeMapService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstTask;
import event.EventManager;
import java.io.IOException;
import models.Item.Item;
import models.Item.ItemService;
import network.io.Message;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.TaskService;
import nro.shop.ShopService;
import nro.top.TopService;


public class Day20_10 extends Npc {

    public Day20_10(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.DAY_20_10) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 19) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Cậu cần tôi giúp gì?\n",
                            "Cửa Hàng", "Trồng Hoa", "Trồng Hoa\nVào Chậu", "Đổi điểm\n[" + player.event.get20_10Point()+ "]", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            ItemCheckUtil BongHoaXanh = new ItemCheckUtil(player)
            .check(1093, 10, "Đất trồng cây")
            .check(1094, 5, "Phân bón")
            .check(1095, 1, "Hạt mầm");
            ItemCheckUtil ChauHoa = new ItemCheckUtil(player)
            .check(1093, 10, "Đất trồng cây")
            .check(1094, 5, "Phân bón")
            .check(1098, 1, "Bông hoa xanh")
            .check(1097, 1, "Chậu sứ")
            .check(1096, 1, "Thuốc tăng trưởng");
            if (EventManager.DAY_20_10) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 19) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "20_10_EVENT", true);
                                break;
                            }
                            case 1: {
                                if (BongHoaXanh.isAllEnough()) {
                                    this.createOtherMenu(player, 0,
                                        BongHoaXanh.getStatusText("Công thức trồng hoa", true, true, 1_000_000, 2),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        BongHoaXanh.getStatusText("Công thức trồng hoa", true, true, 1_000_000, 2),
                                        "Đóng");
                                }
                                break;
                            }
                            case 2: {
                                if (ChauHoa.isAllEnough()) {
                                    this.createOtherMenu(player, 1,
                                        ChauHoa.getStatusText("Công thức trồng hoa vào chậu", true, false, 10_000_000, 0),
                                        "Đồng ý", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        ChauHoa.getStatusText("Công thức trồng hoa vào chậu", true, false, 10_000_000, 0),
                                        "Đóng");
                                }
                                break;
                            }
                            case 3: {
                                ShopService.gI().opendShop(player, "20_10_EVENT_2", true);
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
                                if (player.inventory.getGold() < 1_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                if (player.inventory.getRuby() < 2) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                                    return;
                                }
                                Item DatTrongcay = InventoryService.gI().findItemBag(player, 1093);
                                Item Phanbon = InventoryService.gI().findItemBag(player, 1094);
                                Item Hatmam = InventoryService.gI().findItemBag(player, 1095);
                                Item HoaXanh = ItemService.gI().createNewItem((short) 1098);
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
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DatTrongcay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Phanbon));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Hatmam));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(HoaXanh.template.iconID);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, DatTrongcay, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, Phanbon, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, Hatmam, 1);
                                    player.inventory.subGold(1_000_000);
                                    player.inventory.subRuby(2);
                                    HoaXanh.addOptionParam(30, 0);
                                    HoaXanh.addOptionParam(93, 30);
                                    InventoryService.gI().addItemBag(player, HoaXanh);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + HoaXanh.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
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
                                if (player.inventory.getGold() < 10_000_000) {
                                    Service.gI().sendThongBao(player, "Bạn không đủ vàng để thực hiện");
                                    return;
                                }
                                Item DatTrongcay = InventoryService.gI().findItemBag(player, 1093);
                                Item Phanbon = InventoryService.gI().findItemBag(player, 1094);
                                Item HoaXanh = InventoryService.gI().findItemBag(player, 1098);
                                Item ChauSu = InventoryService.gI().findItemBag(player, 1097);
                                Item ThuocTangtrg = InventoryService.gI().findItemBag(player, 1096);
                                Item ChauHoaXanh = ItemService.gI().createNewItem((short) 1099);
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
                                    msgg.writer().writeByte(5);
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, DatTrongcay));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, Phanbon));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, HoaXanh));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, ChauSu));
                                    msgg.writer().writeByte(InventoryService.gI().getIndexBag(player, ThuocTangtrg));
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    msgg = new Message(-81);
                                    msgg.writer().writeByte(7);
                                    msgg.writer().writeShort(ChauHoaXanh.template.iconID);
                                    player.sendMessage(msgg);
                                    msgg.cleanup();
                                    InventoryService.gI().subQuantityItemsBag(player, DatTrongcay, 10);
                                    InventoryService.gI().subQuantityItemsBag(player, Phanbon, 5);
                                    InventoryService.gI().subQuantityItemsBag(player, HoaXanh, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, ChauSu, 1);
                                    InventoryService.gI().subQuantityItemsBag(player, ThuocTangtrg, 3);
                                    player.inventory.subGold(10_000_000);
                                    ChauHoaXanh.addOptionParam(30, 0);
                                    ChauHoaXanh.addOptionParam(93, 30);
                                    InventoryService.gI().addItemBag(player, ChauHoaXanh);
                                    new Thread(() -> {
                                        Functions.sleep(2000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + ChauHoaXanh.template.name);
                                    }).start();
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendMoney(player);
                                } catch (IOException e) {
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
