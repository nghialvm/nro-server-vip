package nro.npc.ListNpc;



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


public class ValentineEvent extends Npc {

    public ValentineEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.VALENTINE_DAY) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Anh em FA đã có gấu ôm chưa nhỉ\n" +
                            "Chưa có thì đến gặp em, em tặng các anh quà hicc\n",
                            "Tặng Hoa", "Nhiệm Vụ\nNgày", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.VALENTINE_DAY) {
                if (this.mapId == 0 || this.mapId == 7 || this.mapId == 14) {
                    Item HoaHong = InventoryService.gI().findItemBag(player, 709);
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, 0,
                                    "Tặng x99 Bông Hoa nhận Hộp Mù Tình Yêu x1\n"
                                    + "Tặng x999 Bông Hoa nhận Trái Tim Valentine",
                                    "Tặng 99\nBông Hoa", "Tặng 999\nBông Hoa", "Đóng");
                                break;
                            }
                            case 1: {
                                if (player.playerTask.eventTask.template != null) {
                                    String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.eventTask.getName() + " ("
                                            + player.playerTask.eventTask.getLevel() + ")"
                                            + "\nHiện tại đã hoàn thành: " + player.playerTask.eventTask.count + "/"
                                            + player.playerTask.eventTask.maxCount + " ("
                                            + player.playerTask.eventTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                            + player.playerTask.eventTask.leftTask + "/" + ConstTask.MAX_EVENT_TASK;
                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_EVENT_TASK,
                                            npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_EVENT_TASK,
                                            "Tôi có vài nhiệm vụ theo cấp bậc, hoàn thành để nhận quà sự kiện khủng, sức cậu có thể làm được cái nào?",
                                            "Dễ", "Bình thường", "Khó", "Siêu khó", "Bất khả thi", "Từ chối");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 0) {
                        switch (select) {
                            case 0: {
                                if (HoaHong != null) {
                                    if (HoaHong.quantity < 99) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Hoa hồng, còn thiếu " + (99 - HoaHong.quantity) + " Hoa hồng nữa!");
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, HoaHong, 99);
                                    Item HopQua = ItemService.gI().createNewItem((short) 1516);
                                    HopQua.addOptionParam(30, 0);
                                    HopQua.addOptionParam(93, 30);
                                    InventoryService.gI().addItemBag(player, HopQua);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + HopQua.Name());
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Hoa hồng");
                                }
                                break;
                            }
                            case 1: {
                                if (HoaHong != null) {
                                    if (HoaHong.quantity < 999) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ Hoa hồng, còn thiếu " + (999 - HoaHong.quantity) + " Hoa hồng nữa!");
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, HoaHong, 999);
                                    Item TraiTim = ItemService.gI().createNewItem((short) 1206);
                                    TraiTim.addOptionParam(50, Util.nextInt(16, 18));
                                    TraiTim.addOptionParam(77, Util.nextInt(16, 18));
                                    TraiTim.addOptionParam(103, Util.nextInt(16, 18));
                                    TraiTim.addOptionParam(14, 11);
                                    TraiTim.addOptionParam(210, 1);
                                    if (Util.isTrue(90, 100)) {
                                        TraiTim.addOptionParam(93, 30);
                                    }
                                    InventoryService.gI().addItemBag(player, TraiTim);
                                    InventoryService.gI().sendItemBag(player);
                                    Service.gI().sendThongBao(player, "Bạn nhận được " + TraiTim.Name());
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Hoa hồng");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_EVENT_TASK) {
                        switch (select) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4: {
                                TaskService.gI().changeEventTask(player, (byte) select);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_EVENT_TASK) {
                        switch (select) {
                            case 0: {
                                TaskService.gI().payEventTask(player);
                                break;
                            }
                            case 1: {
                                TaskService.gI().removeEventTask(player);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
