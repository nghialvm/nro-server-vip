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

public class ThoDaiKa extends Npc {

    public ThoDaiKa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.TRUNG_THU) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Hôm nay ta rảnh nên ta sẽ cho ngươi 1 điều ước",
                            "Ước\nMiễn Phí", "Ước bằng\n50 Ngọc", "Ước bằng\n99 Cà rốt", "Cửa hàng", "Gia Hạn\nCải Trang", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.TRUNG_THU) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                createOtherMenu(player, 0,
                                        "Ước miễn phí, ước nhanh rồi đi ra chỗ khác cho ta, chiếm chỗ quá.",
                                        "Đồng ý", "Từ chối");
                                break;
                            }
                            case 1: {
                                createOtherMenu(player, 1,
                                        "Ước bằng ngọc, ước nhanh rồi đi ra chỗ khác cho ta, chiếm chỗ quá.",
                                        "Đồng ý", "Từ chối");
                                break;
                            }
                            case 2: {
                                createOtherMenu(player, 2,
                                        "Ước bằng Cà rốt, ước nhanh rồi đi ra chỗ khác cho ta, chiếm chỗ quá.",
                                        "Đồng ý", "Từ chối");
                                break;
                            }
                            case 3: {
                                ShopService.gI().opendShop(player, "TRUNG_THU_EVENT_THODAIKA", true);
                                break;
                            }
                            case 4: {
                                createOtherMenu(player, 3,
                                        "Ngươi muốn gia hạn cải trang nào, nhanh lên ta còn bao việc!\n"
                                        + "Lưu ý: Muốn gia hạn cái nào thì phải mặc cái đó, phải để ít nhất 1 cải trang cùng loại đang mặc mới có thể gia hạn.",
                                        "Nhật Thần", "Nguyệt Thần", "Từ chối");
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
                                if (player.UocMienPhi == 0) {
                                    createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thích nhé.", "Đóng");
                                    short[] Param = {3, 5, 7, 15, 30};
                                    short[] List_Item = {828, 829, 830, 831, 832, 833, 834, 835, 836, 837, 838, 839, 840, 841, 842, 733, 734, 735, 862, 819, 806, 863, 864};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    if (Item_Template >= 828 && Item_Template <= 842) {
                                        itemReceived.addOptionParam(87, 0);
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template == 733 || Item_Template == 734 || Item_Template == 735) {
                                        itemReceived.addOptionParam(84, 0);
                                        itemReceived.addOptionParam(50, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(77, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(103, Util.nextInt(9, 13));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 862) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(5, Util.nextInt(10, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 819) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(8, Util.nextInt(2, 5));
                                        itemReceived.addOptionParam(162, 2);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 806) {
                                        itemReceived.addOptionParam(50, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(77, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(103, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 863) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(95, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(96, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(236, Util.nextInt(15, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 864) {
                                        itemReceived.addOptionParam(50, 25);
                                        itemReceived.addOptionParam(77, 25);
                                        itemReceived.addOptionParam(94, 15);
                                        itemReceived.addOptionParam(97, 15);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    player.UocMienPhi++;
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                } else {
                                    Service.gI().sendThongBao(player, "Hôm nay bạn đã Ước miễn phí rồi!");
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
                                if (player.inventory.getRuby() >= 50) {
                                    createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thích nhé.", "Đóng");
                                    short[] Param = {3, 5, 7, 15, 30};
                                    short[] List_Item = {837, 838, 839, 840, 841, 842, 733, 734, 735, 862, 819, 806, 863, 864, 1143, 1173, 1023, 735};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    if (Item_Template == 735) {
                                        itemReceived.addOptionParam(50, 11);
                                        itemReceived.addOptionParam(77, 11);
                                        itemReceived.addOptionParam(103, 11);
                                        itemReceived.addOptionParam(84, 0);
                                        itemReceived.addOptionParam(94, 5);
                                        if (Util.isTrue(97, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 1023) {
                                        itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                        itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                        itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                        itemReceived.addOptionParam(14, 11);
                                        itemReceived.addOptionParam(5, 11);
                                        if (Util.isTrue(97, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 1143 || Item_Template == 1173) {
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template >= 837 && Item_Template <= 842) {
                                        itemReceived.addOptionParam(87, 0);
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template == 733 || Item_Template == 734 || Item_Template == 735) {
                                        itemReceived.addOptionParam(84, 0);
                                        itemReceived.addOptionParam(50, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(77, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(103, Util.nextInt(9, 13));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 862) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(5, Util.nextInt(10, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 819) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(8, Util.nextInt(2, 5));
                                        itemReceived.addOptionParam(162, 2);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 806) {
                                        itemReceived.addOptionParam(50, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(77, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(103, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 863) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(95, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(96, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(236, Util.nextInt(15, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 864) {
                                        itemReceived.addOptionParam(50, 25);
                                        itemReceived.addOptionParam(77, 25);
                                        itemReceived.addOptionParam(94, 15);
                                        itemReceived.addOptionParam(97, 15);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    player.inventory.subRuby(50);
                                    Service.gI().sendMoney(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ Hồng ngọc");
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
                                Item CuCaRot = InventoryService.gI().findItemBag(player, 462);
                                if (CuCaRot != null) {
                                    if (CuCaRot.quantity < 99) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Củ cà rốt");
                                        return;
                                    }
                                    createOtherMenu(player, ConstNpc.IGNORE_MENU, "Thích nhé.", "Đóng");
                                    short[] Param = {3, 5, 7, 15, 30};
                                    short[] List_Item = {837, 838, 839, 840, 841, 842, 733, 734, 735, 862, 819, 806, 863, 864, 1143};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    if (Item_Template >= 837 && Item_Template <= 842) {
                                        itemReceived.addOptionParam(87, 0);
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    if (Item_Template == 733 || Item_Template == 734 || Item_Template == 735) {
                                        itemReceived.addOptionParam(84, 0);
                                        itemReceived.addOptionParam(50, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(77, Util.nextInt(9, 13));
                                        itemReceived.addOptionParam(103, Util.nextInt(9, 13));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 862) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(5, Util.nextInt(10, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 819) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(8, Util.nextInt(2, 5));
                                        itemReceived.addOptionParam(162, 2);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 806) {
                                        itemReceived.addOptionParam(50, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(77, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(103, Util.nextInt(21, 25));
                                        itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 863) {
                                        itemReceived.addOptionParam(50, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(77, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(103, Util.nextInt(19, 23));
                                        itemReceived.addOptionParam(95, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(96, Util.nextInt(10, 15));
                                        itemReceived.addOptionParam(236, Util.nextInt(15, 25));
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 864) {
                                        itemReceived.addOptionParam(50, 25);
                                        itemReceived.addOptionParam(77, 25);
                                        itemReceived.addOptionParam(94, 15);
                                        itemReceived.addOptionParam(97, 15);
                                        if (Util.isTrue(95, 100)) {
                                            itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                        }
                                    }
                                    if (Item_Template == 1143) {
                                        itemReceived.addOptionParam(30, 0);
                                    }
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.template.name);
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Củ cà rốt");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 3) {
                        switch (select) {
                            case 0: {
                                Item nhatThanInBag = InventoryService.gI().findItemBag(player, 1277);
                                Item itemInBody = player.inventory.itemsBody.get(5);

                                if (itemInBody != null && itemInBody.isNotNullItem() && itemInBody.template.id == 1277) {
                                    if (nhatThanInBag != null) {
                                        itemInBody.addOptionParam(93, 3);
                                        InventoryService.gI().subQuantityItemsBag(player, nhatThanInBag, 1);
                                        InventoryService.gI().sendItemBag(player);
                                        InventoryService.gI().sendItemBody(player);
                                        Service.gI().sendThongBao(player, "Gia hạn thành công, cải trang của bạn được cộng thêm 3 ngày sử dụng.");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có cải trang Nhật thần trong hành trang để gia hạn.");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Hãy mặc cải trang Nhật thần để gia hạn.");
                                }
                                break;
                            }
                            case 1: {
                                Item nguyetThanInBag = InventoryService.gI().findItemBag(player, 1276);
                                Item itemInBody = player.inventory.itemsBody.get(5);

                                if (itemInBody != null && itemInBody.isNotNullItem() && itemInBody.template.id == 1276) {
                                    if (nguyetThanInBag != null) {
                                        itemInBody.addOptionParam(93, 3);
                                        InventoryService.gI().subQuantityItemsBag(player, nguyetThanInBag, 1);
                                        InventoryService.gI().sendItemBag(player);
                                        InventoryService.gI().sendItemBody(player);
                                        Service.gI().sendThongBao(player, "Gia hạn thành công, cải trang của bạn được cộng thêm 3 ngày sử dụng.");
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có cải trang Nguyệt thần trong hành trang để gia hạn.");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Hãy mặc cải trang Nguyệt thần để gia hạn.");
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
