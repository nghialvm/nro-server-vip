package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import event.EventManager;
import java.util.List;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.player.Player;

public class TreoDenTrungThu extends Npc {

    public TreoDenTrungThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.TRUNG_THU) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Mỗi lần treo đèn bạn sẽ được tặng một món quà xịn sò nhất.",
                            "Treo Đèn", "Đóng");
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
                                Item Longden = InventoryService.gI().findItemBag(player, 1311);
                                if (Longden != null) {
                                    InventoryService.gI().subQuantityItemsBag(player, Longden, 1);
                                    short[] Param = {3, 5, 7, 15, 30};
                                    short[] List_Item = {1150, 1151, 1152, 1153, 1143, 859, 956, 1204, 765, 904, 1145, 1213, 1223, 1684, 1205};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    switch (Item_Template) {
                                        case 1143:
                                            itemReceived.addOptionParam(30, 0);
                                            break;
                                        case 1150:
                                        case 1151:
                                        case 1152:
                                        case 1153:
                                            itemReceived.addOptionParam(87, 0);
                                            break;
                                        case 859:
                                        case 956:
                                        case 1204:
                                            itemReceived.addOptionParam(87, 0);
                                            itemReceived.addOptionParam(30, 0);
                                            break;
                                        case 765:
                                            List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) Item_Template);
                                            if (!ops.isEmpty()) {
                                                itemReceived.itemOptions = ops;
                                            }
                                            break;
                                        case 904:
                                            itemReceived.addOptionParam(50, 21);
                                            itemReceived.addOptionParam(77, 18);
                                            itemReceived.addOptionParam(103, 18);
                                            itemReceived.addOptionParam(5, 11);
                                            itemReceived.addOptionParam(14, 8);
                                            itemReceived.addOptionParam(80, 10);
                                            itemReceived.addOptionParam(114, 20);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        case 1145:
                                            itemReceived.addOptionParam(50, 24);
                                            itemReceived.addOptionParam(77, 21);
                                            itemReceived.addOptionParam(103, 21);
                                            itemReceived.addOptionParam(14, 15);
                                            itemReceived.addOptionParam(94, 15);
                                            itemReceived.addOptionParam(114, 20);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        case 1213:
                                            itemReceived.addOptionParam(50, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(77, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(103, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(108, 15);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        case 1223:
                                            itemReceived.addOptionParam(50, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(77, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(103, Util.nextInt(14, 16));
                                            itemReceived.addOptionParam(94, 12);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        case 1684:
                                            itemReceived.addOptionParam(50, 25);
                                            itemReceived.addOptionParam(101, 70);
                                            itemReceived.addOptionParam(95, 15);
                                            itemReceived.addOptionParam(96, 5);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        case 1205:
                                            itemReceived.addOptionParam(50, 24);
                                            itemReceived.addOptionParam(77, 24);
                                            itemReceived.addOptionParam(103, 24);
                                            itemReceived.addOptionParam(106, 0);
                                            if (Util.isTrue(95, 100)) {
                                                itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                                    player.event.addTrungThuPoint(1);
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Lồng đèn treo");
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
