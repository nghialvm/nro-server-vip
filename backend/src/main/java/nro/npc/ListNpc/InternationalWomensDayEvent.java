package nro.npc.ListNpc;



import Utils.Functions;
import nro.inventory.InventoryService;
import nro.server.ServerManager;
import nro.services.Service;
import Utils.Util;
import consts.ConstAttribute;
import consts.ConstNpc;
import event.EventManager;
import java.util.List;
import jbcd.dao.EventDAO;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.attribute.Attribute;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;
import nro.top.TopService;
;

public class InternationalWomensDayEvent extends Npc {

    public InternationalWomensDayEvent(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }
    
    int TIME_12_HOUS = 43200;
    int TIME_24_HOUS = 86400;
    int TIME_36_HOUS = 129600;
    int TIME_48_HOUS = 172800;
    int TIME_60_HOUS = 216000;
    
    private void applyGlobalExpBuff(Attribute at, int point) {
        int rewardStage = 0;
        int value = 0;
        int time = 0;

        if (point >= 5000) {
            rewardStage = 5;
            value = 400;
            time = TIME_60_HOUS;
        } else if (point >= 1500) {
            rewardStage = 4;
            value = 300;
            time = TIME_48_HOUS;
        } else if (point >= 500) {
            rewardStage = 3;
            value = 200;
            time = TIME_36_HOUS;
        } else if (point >= 200) {
            rewardStage = 2;
            value = 100;
            time = TIME_24_HOUS;
        } else if (point >= 100) {
            rewardStage = 1;
            value = 100;
            time = TIME_12_HOUS;
        }

        // Chỉ kích hoạt nếu vượt mốc mới
        if (rewardStage > EventDAO.getLAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY()) {
            EventDAO.setLAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY(rewardStage);
            at.setValue(value);
            at.setTime(time);
        }
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (EventManager.INTERNATIONAL_WOMANS_DAY) {
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Toàn máy chủ đã tặng " + EventDAO.RACE_INTERNATIONAL_WOMENS_DAY_EVENT + " bông hoa hồng\n"
                        + "Tặng 100 Bông toàn máy chủ nhận: x2 exp toàn máy chủ " + (TIME_12_HOUS / 3600) + " giờ\n"
                        + "Tặng 200 Bông toàn máy chủ nhận: x2 exp toàn máy chủ " + (TIME_24_HOUS / 3600) + " giờ\n"
                        + "Tặng 500 Bông toàn máy chủ nhận: x3 exp toàn máy chủ " + (TIME_36_HOUS / 3600) + " giờ\n"
                        + "Tặng 1500 Bông toàn máy chủ nhận: x4 exp toàn máy chủ " + (TIME_48_HOUS / 3600) + " giờ\n"
                        + "Tặng 5000 Bông toàn máy chủ nhận: x5 exp toàn máy chủ " + (TIME_60_HOUS / 3600) + " giờ\n"
                        + ((at != null && at.getValue() != 0 && at.getTime() != 0) 
                                ? "|1|Toàn bộ máy chủ được tăng " + at.getValue() + "% TNSM, thời gian còn lại " + Util.formatTimeHMS(at.getTime()) + "s." : ""),
                            "Cửa hàng\nsự kiện", "Đổi Điểm\n" + "[" + player.event.getInternationalWomensDayPoint()+ "]", "Tặng Quà\n8-3", "Đua Top", "Sự Kiện", "Đóng");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (EventManager.INTERNATIONAL_WOMANS_DAY) {
                Attribute at = ServerManager.gI().getAttributeManager().find(ConstAttribute.TNSM);
                if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                    Item HopQuaChinChu = InventoryService.gI().findItemBag(player, 1511);
                    Item HopQuaNheNhang = InventoryService.gI().findItemBag(player, 1510);
                    Item BongHoaHong = InventoryService.gI().findItemBag(player, 1530);
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0: {
                                ShopService.gI().opendShop(player, "INTERNATIONAL_WOMANS_DAY", true);
                                break;
                            }
                            case 1: {
                                ShopService.gI().opendShop(player, "INTERNATIONAL_WOMANS_DAY2", true);
                                break;
                            }
                            case 2: {
                                this.createOtherMenu(player, 0,
                                            "Bạn muốn tặng quà cho mình ư?\n",
                                            "Tặng\n1 Hộp Quà\nChỉn Chu\nĐang có: " + (HopQuaChinChu != null ? HopQuaChinChu.quantity : 0), //0
                                            "Tặng\n1 Hộp Quà\nNhẹ Nhàng\nĐang có: " + (HopQuaNheNhang != null ? HopQuaNheNhang.quantity : 0), //1
                                            "Tặng\n20 Bông\nHoa Hồng\nĐang có: " + (BongHoaHong != null ? BongHoaHong.quantity : 0), "Đóng"); //2
                                break;
                            }
                            case 3: {
                                this.createOtherMenu(player, 1,
                                            "Bạn cần tôi giúp gì?\n",
                                            "Top\nMở Thiệp\n8-3", "Top\nTặng Bông\nHoa Hồng", "Đóng");
                                break;
                            }
                            case 4: {
                                this.createOtherMenu(player, 2,
                                        "Toàn bộ máy chủ đang được cộng " + EventDAO.getRemainingTimeToIncreaseDame() + "% Sức đánh.\n"
                                        + "Toàn bộ máy chủ đang được cộng " + EventDAO.getRemainingTimeToIncreaseHP() + "% HP.\n"
                                        + "Toàn bộ máy chủ đang được cộng " + EventDAO.getRemainingTimeToIncreaseMP() + "% KI.\n",
                                            "Cách Nhận", "Đóng");
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
                                if (HopQuaChinChu != null) {
                                    if (HopQuaChinChu.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ " + HopQuaChinChu.Name());
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, HopQuaChinChu, 1);
                                    short[] List_Item = {675, 676, 677, 678, 679, 680, 681, 227, 228, 229, 905, 907, 911, 1143};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    switch (Item_Template) {
                                        case 1143:
                                            itemReceived.addOptionParam(30, 0);
                                            break;
                                        case 675:
                                        case 676:
                                        case 677:
                                        case 678:
                                        case 679:
                                        case 680:
                                        case 681:
                                            List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) Item_Template);
                                            if (!ops.isEmpty()) {
                                                itemReceived.itemOptions = ops;
                                            }
                                            if (Util.isTrue(90, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 905:
                                        case 907:
                                        case 911:
                                            itemReceived.addOptionParam(47, 700);
                                            itemReceived.addOptionParam(2, 10);
                                            itemReceived.addOptionParam(8, 4);
                                            itemReceived.addOptionParam(101, 20);
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 227:
                                        case 228:
                                        case 229:
                                            itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                                            itemReceived.addOptionParam(97, Util.nextInt(10, 20));
                                            itemReceived.addOptionParam(93, 7);
                                            break;
                                        default:
                                            break;
                                    }
                                    Service.gI().chat(player, "Chúc bạn 8-3 vui vẻ");
                                    Service.gI().dropItem_OngTreNuoc(player, (Util.isTrue(80, 100) ? Util.nextInt(3, 5) : Util.nextInt(1, 3)));
                                    new Thread(() -> {
                                        Functions.sleep(1500);
                                        npcChat(player, "Ồ quý hoá quá, ta tặng lại món quà, hãy nhận lấy.");
                                    }).start();
                                    new Thread(() -> {
                                        Functions.sleep(3000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                                    }).start();
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    if (Util.isTrue(8, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseDame(EventDAO.getRemainingTimeToIncreaseDame() + 1);
                                        Service.gI().point(player);
                                    }
                                    if (Util.isTrue(9, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseHP(EventDAO.getRemainingTimeToIncreaseHP() + 1);
                                        Service.gI().point(player);
                                    }
                                    if (Util.isTrue(10, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseMP(EventDAO.getRemainingTimeToIncreaseMP() + 1);
                                        Service.gI().point(player);
                                    }
                                    EventDAO.save();
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Hộp quà chỉn chu.");
                                }
                                break;
                            }
                            case 1: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (HopQuaNheNhang != null) {
                                    if (HopQuaNheNhang.quantity < 1) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ " + HopQuaNheNhang.Name());
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, HopQuaNheNhang, 1);
                                    short[] List_Item = {675, 676, 677, 678, 679, 680, 681, 1519, 1520, 730, 731, 732, 1782};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    switch (Item_Template) {
                                        case 675:
                                        case 676:
                                        case 677:
                                        case 678:
                                        case 679:
                                        case 680:
                                        case 681:
                                            List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) Item_Template);
                                            if (!ops.isEmpty()) {
                                                itemReceived.itemOptions = ops;
                                            }
                                            if (Util.isTrue(90, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 730:
                                            itemReceived.addOptionParam(165, 10);
                                            itemReceived.addOptionParam(50, 20);
                                            itemReceived.addOptionParam(77, 17);
                                            itemReceived.addOptionParam(103, 17);
                                            if (Util.isTrue(90, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 731:
                                            itemReceived.addOptionParam(169, 0);
                                            itemReceived.addOptionParam(50, 20);
                                            itemReceived.addOptionParam(77, 19);
                                            itemReceived.addOptionParam(103, 19);
                                            if (Util.isTrue(90, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 732:
                                            itemReceived.addOptionParam(168, 0);
                                            itemReceived.addOptionParam(50, 21);
                                            itemReceived.addOptionParam(77, 19);
                                            itemReceived.addOptionParam(103, 19);
                                            if (Util.isTrue(90, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1519:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1520:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                                            itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1782:
                                            itemReceived.addOptionParam(50, 24);
                                            itemReceived.addOptionParam(77, 23);
                                            itemReceived.addOptionParam(108, 23);
                                            itemReceived.addOptionParam(5, Util.nextInt(10, 15));
                                            itemReceived.addOptionParam(95, Util.nextInt(10, 15));
                                            itemReceived.addOptionParam(160, 30);
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    Service.gI().chat(player, "Chúc bạn 8-3 vui vẻ");
                                    Service.gI().dropItem_OngTreNuoc(player, (Util.isTrue(80, 100) ? Util.nextInt(3, 5) : Util.nextInt(1, 3)));
                                    new Thread(() -> {
                                        Functions.sleep(1500);
                                        npcChat(player, "Ồ quý hoá quá, ta tặng lại món quà, hãy nhận lấy.");
                                    }).start();
                                    new Thread(() -> {
                                        Functions.sleep(3000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                                    }).start();
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    if (Util.isTrue(5, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseDame(EventDAO.getRemainingTimeToIncreaseDame() + 1);
                                        Service.gI().point(player);
                                    }
                                    if (Util.isTrue(7, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseHP(EventDAO.getRemainingTimeToIncreaseHP() + 1);
                                        Service.gI().point(player);
                                    }
                                    if (Util.isTrue(9, 100)) {
                                        EventDAO.setRemainingTimeToIncreaseMP(EventDAO.getRemainingTimeToIncreaseMP() + 1);
                                        Service.gI().point(player);
                                    }
                                    EventDAO.save();
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Hộp quà nhẹ nhàng.");
                                }
                                break;
                            }
                            case 2: {
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (BongHoaHong != null) {
                                    if (BongHoaHong.quantity < 20) {
                                        Service.gI().sendThongBao(player, "Bạn không đủ " + BongHoaHong.Name());
                                        return;
                                    }
                                    InventoryService.gI().subQuantityItemsBag(player, BongHoaHong, 20);
                                    short[] List_Item = {1519, 1520, 1143, 1363, 1100, 1344, 227, 228, 229};
                                    Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
                                    int Item_Template = itemReceived.template.id;
                                    switch (Item_Template) {
                                        case 227:
                                        case 228:
                                        case 229:
                                            itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                                            itemReceived.addOptionParam(97, Util.nextInt(10, 20));
                                            itemReceived.addOptionParam(93, 7);
                                            break;
                                        case 1143:
                                            itemReceived.addOptionParam(30, 0);
                                            break;
                                        case 1519:
                                        case 1520:
                                            itemReceived.addOptionParam(50, Util.nextInt(17, 19));
                                            itemReceived.addOptionParam(77, Util.nextInt(17, 19));
                                            itemReceived.addOptionParam(103, Util.nextInt(17, 19));
                                            itemReceived.addOptionParam(5, Util.nextInt(17, 19));
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1100:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(94, 11);
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1344:
                                            itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                                            itemReceived.addOptionParam(14, 11);
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        case 1363:
                                            itemReceived.addOptionParam(50, Util.nextInt(8, 15));
                                            itemReceived.addOptionParam(77, Util.nextInt(8, 15));
                                            itemReceived.addOptionParam(103, Util.nextInt(8, 15));
                                            itemReceived.addOptionParam(114, 25);
                                            if (Util.isTrue(99, 100)) {
                                                itemReceived.itemOptions.add(new ItemOption(93, 7));
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    applyGlobalExpBuff(at, EventDAO.RACE_INTERNATIONAL_WOMENS_DAY_EVENT);
                                    player.DuaTopTangBongHoaHong += 20;
                                    EventDAO.RACE_INTERNATIONAL_WOMENS_DAY_EVENT += 20;
                                    Service.gI().chat(player, "Chúc bạn 8-3 vui vẻ");
                                    Service.gI().dropItem_OngTreNuoc(player, (Util.isTrue(80, 100) ? Util.nextInt(3, 5) : Util.nextInt(1, 3)));
                                    new Thread(() -> {
                                        Functions.sleep(1500);
                                        npcChat(player, "Ồ quý hoá quá, ta tặng lại món quà, hãy nhận lấy.");
                                    }).start();
                                    new Thread(() -> {
                                        Functions.sleep(3000);
                                        Service.gI().sendThongBao(player, "Bạn nhận được " + itemReceived.Name());
                                    }).start();
                                    player.event.addInternationalWomensDayPoint(10);
                                    InventoryService.gI().addItemBag(player, itemReceived);
                                    InventoryService.gI().sendItemBag(player);
                                    EventDAO.save();
                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không có Bông hoa hồng.");
                                }
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        switch (select) {
                            case 0: {
                                TopService.showListTopMoThiep83(player);
                                break;
                            }
                            case 1: {
                                TopService.showListTopTangBongHoaHong(player);
                                break;
                            }
                        }
                    } else if (player.iDMark.getIndexMenu() == 2) {
                        switch (select) {
                            case 0: {
                                this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "|2|Để muốn cộng thêm nhiều chỉ số từ sự kiện\n"
                                        + "Bạn hãy cày quốc và tặng cho Bunma Xinh Gái hộp quà\n"
                                        + "Nếu nàng vui thì nàng có thể ban tặng máy chủ 1% chỉ số\n"
                                        + "(Có thể cộng dồn đến không giới hạn)\n"
                                        + "Lưu ý: đây là cộng cho toàn máy chủ chứ không phải bất kỳ cá nhân nào, phần thưởng sẽ tồn tại đến khi kết thúc sự kiện!", "Đóng");
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
