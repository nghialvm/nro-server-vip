package nro.services.Fun;

import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.Manager.TrungThuEventManager;
import QuanLiBoss.TypeEventBoss;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Functions;
import Utils.ItemCheckUtil;
import Utils.SkillUtil;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstDetu;
import consts.ConstNpc;
import consts.ConstPlayer;
import event.EventManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import models.Item.ItemTimeService;
import models.Reward.RewardService;
import nro.combine.CombineService;
import nro.effect.EffectMapService;
import nro.effect.EffectSkillService;
import nro.inventory.InventoryService;
import nro.npc.Special.MelonPlant;
import nro.player.Detu;
import nro.player.Player;
import nro.services.DetuService;


public class ItemUseHandler {

    private static ItemUseHandler instance;

    public static ItemUseHandler gI() {
        if (instance == null) {
            instance = new ItemUseHandler();
        }
        return instance;
    }

    public void useVePotara(Player pl) {
        ChangeMapService.gI().changeMapPlayerRandomZone(pl, Util.nextInt(189, 192), -1, Util.nextInt(100, 500), 50);
    }

    public void useBinhHutNangLuong(Player player, Item item) {
        if (item.getOptionParam(262) < 3000) {
            Service.gI().sendThongBao(player, "Vật phẩm chưa đủ Kilis, còn thiếu " + (3000 - item.getOptionParam(262) + " Kilis nữa"));
            return;
        }
        if ((player.Detu != null && player.Detu.typeDeTu == 0 && player.Detu.nPoint.power >= 40_000_000_000L && player.mabuEgg != null && player.mabuEgg.getSecondDone() <= 0)
                || player.Detu != null && player.Detu.typeDeTu == 1 && player.Detu.nPoint.power >= 40_000_000_000L) {
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            icon[1] = 980;
            CombineService.gI().sendEffectOpenItem(player, icon[0], icon[1]);
            int[] List = {0, 1, 2};
            int Random = List[Util.nextInt(0, List.length - 1)];
            if (player.Detu != null) {
                player.Detu.changeStatus(Detu.FOLLOW);
            }
            item.setOptionParam(262, item.getOptionParam(262) - 3000);

            //InventoryService.gI().subQuantityItemsBag(player, item, 1);
            switch (Random) {
                case 0:
                    new Thread(() -> {
                        Functions.sleep(4000);
                        DetuService.gI().changeUbuPet(player);
                    }).start();
                    break;
                case 1:
                    new Thread(() -> {
                        Functions.sleep(4000);
                        DetuService.gI().changeKidjirenPet(player);
                    }).start();
                    break;
                default:
                    new Thread(() -> {
                        Functions.sleep(4000);
                        DetuService.gI().changeKidbeerPet(player);
                    }).start();
                    break;
            }
            InventoryService.gI().sendItemBag(player);
        } else {
            Service.gI().sendThongBao(player, "Bạn chưa đủ điều kiện để sử dụng vật phẩm này!");
        }
    }

    public void doiDeTuMoi(Player pl, Item item) {
        // 1. Kiểm tra có đệ tử
        if (pl.Detu == null) {
            Service.gI().sendThongBao(pl, "Bạn chưa có đệ tử để đổi!");
            return;
        }

        // 2. Kiểm tra loại đệ tử (phải là đệ tử mới)
        if (pl.Detu.typeDeTu != ConstDetu.U_BU
                && pl.Detu.typeDeTu != ConstDetu.KID_JIREN
                && pl.Detu.typeDeTu != ConstDetu.KID_BEER) {
            Service.gI().sendThongBao(pl, "Chỉ đệ tử mới (U_Bu, Kid Jiren, Kid Beer) mới có thể đổi!");
            return;
        }

        // 4. Trừ item 1949
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);

        // 5. Random đổi sang loại đệ tử mới khác
        int[] list = {ConstDetu.U_BU, ConstDetu.KID_JIREN, ConstDetu.KID_BEER};
        int newType;
        do {
            newType = list[Util.nextInt(0, list.length - 1)];
        } while (newType == pl.Detu.typeDeTu); // tránh đổi trùng loại hiện tại

        // 6. Đổi đệ tử
        switch (newType) {
            case ConstDetu.U_BU:
                DetuService.gI().changeUbuPet(pl);
                Service.gI().sendThongBao(pl, "Bạn đã đổi sang đệ tử U_Bu!");
                break;
            case ConstDetu.KID_JIREN:
                DetuService.gI().changeKidjirenPet(pl);
                Service.gI().sendThongBao(pl, "Bạn đã đổi sang đệ tử Kid Jiren!");
                break;
            case ConstDetu.KID_BEER:
                DetuService.gI().changeKidbeerPet(pl);
                Service.gI().sendThongBao(pl, "Bạn đã đổi sang đệ tử Kid Beer!");
                break;
        }
    }

    public void useThiepChuc(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1512};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1512: {
                    itemReceived.addOptionParam(50, 29);
                    itemReceived.addOptionParam(77, 28);
                    itemReceived.addOptionParam(103, 28);
                    itemReceived.addOptionParam(14, 15);
                    itemReceived.addOptionParam(5, 20);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopTangThiepChuc++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }
     public void UseTuiSieuAn(Player pl, Item item) {
    short[] List_Item = {1724, 1725, 1726};
    short idNhan = List_Item[Util.nextInt(0, List_Item.length - 1)];
    Item itemReceived = ItemService.gI().createNewItem(idNhan);
    itemReceived.quantity = Util.nextInt(1, 5); 

    short[] icon = new short[2];
    icon[0] = item.template.iconID;
    icon[1] = itemReceived.template.iconID;

    if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
        InventoryService.gI().addItemBag(pl, itemReceived);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);

        CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);


        new Thread(() -> {
            Functions.sleep(2000);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name + " x" + itemReceived.quantity);
        }).start();
    } else {
        Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
    }
}

    public void useHopQua20_10(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {675, 676, 677, 678, 679, 680, 681, 580, 581, 582, 583, 584, 1041, 1042, 1043, 1208, 1209, 1210, 1789, 1790, 1791, 1503, 1504, 977, 975, 978, 1476, 1235, 1103, 914, 819, 1557, 1860, 1782, 1700, 1512};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 580:
                case 675:
                case 676:
                case 677:
                case 678:
                case 679:
                case 680:
                case 681:
                case 581:
                case 582:
                case 583:
                case 584:
                case 1208:
                case 1209:
                case 1210:
                case 1789:
                case 1790:
                case 1791:
                case 1503:
                case 1504: {
                    List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itemReceived.template.id);
                    if (!ops.isEmpty()) {
                        itemReceived.itemOptions.addAll(ops);
                    }
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1041:
                case 1042:
                case 1043: {
                    List<ItemOption> ops = ItemService.gI().getListOptionItemShop(itemReceived.template.id);
                    if (!ops.isEmpty()) {
                        itemReceived.itemOptions.addAll(ops);
                    }
                    break;
                }
                case 975:
                case 977:
                case 978: {
                    itemReceived.addOptionParam(50, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(94, 17);
                    itemReceived.addOptionParam(114, 20);
                    itemReceived.addOptionParam(108, 15);
                    itemReceived.addOptionParam(193, 2);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1476: {
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(103, 24);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(94, 10);
                    itemReceived.addOptionParam(14, 5);
                    itemReceived.addOptionParam(114, 50);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1235: {
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(103, 24);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(14, 10);
                    itemReceived.addOptionParam(108, 15);
                    itemReceived.addOptionParam(114, 20);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1103: {
                    itemReceived.addOptionParam(50, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(94, 18);
                    itemReceived.addOptionParam(114, 20);
                    itemReceived.addOptionParam(8, 4);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 914: {
                    itemReceived.addOptionParam(50, 23);
                    itemReceived.addOptionParam(103, 20);
                    itemReceived.addOptionParam(77, 20);
                    itemReceived.addOptionParam(94, 19);
                    itemReceived.addOptionParam(184, 2);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 819: {
                    itemReceived.addOptionParam(117, 10);
                    itemReceived.addOptionParam(80, 6);
                    itemReceived.addOptionParam(176, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1557: {
                    itemReceived.addOptionParam(50, 25);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(103, 24);
                    itemReceived.addOptionParam(80, 15);
                    itemReceived.addOptionParam(108, 5);
                    itemReceived.addOptionParam(94, 15);
                    itemReceived.addOptionParam(114, 50);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1860: {
                    itemReceived.addOptionParam(50, 23);
                    itemReceived.addOptionParam(77, 20);
                    itemReceived.addOptionParam(103, 20);
                    itemReceived.addOptionParam(5, 20);
                    itemReceived.addOptionParam(80, 5);
                    itemReceived.addOptionParam(117, 8);
                    itemReceived.addOptionParam(114, 20);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1782: {
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(77, 26);
                    itemReceived.addOptionParam(103, 26);
                    itemReceived.addOptionParam(94, 15);
                    itemReceived.addOptionParam(80, 10);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1700: {
                    itemReceived.addOptionParam(50, 26);
                    itemReceived.addOptionParam(77, 21);
                    itemReceived.addOptionParam(103, 21);
                    itemReceived.addOptionParam(117, 13);
                    itemReceived.addOptionParam(14, 15);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                case 1512: {
                    itemReceived.addOptionParam(50, 29);
                    itemReceived.addOptionParam(77, 28);
                    itemReceived.addOptionParam(103, 28);
                    itemReceived.addOptionParam(14, 15);
                    itemReceived.addOptionParam(5, 20);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                }
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHop20_10++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useCapsuleTuChon(Player pl) {
        NpcService.gI().createMenuConMeo(pl, ConstNpc.CAPSULE_KICH_HOAT_TU_CHON, -1, "Hãy chọn 1 trong các trang bị",
                "Áo", "Quần", "Găng", "Giày", "Rada", "Từ chối");
    }

    public void usePhieuDoiCapsule(Player pl, Item item) {
        if (item.quantity < 99) {
            Service.gI().sendThongBao(pl, "Bạn không đủ Phiếu đổi Capsule, còn thiếu " + (99 - item.quantity) + " Phiếu nữa!");
            return;
        }
        Item Capsulekichhoat = ItemService.gI().createNewItemLock((short) 1655);
        short[] icon = {item.template.iconID, Capsulekichhoat.template.iconID};
        CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        InventoryService.gI().subQuantityItemsBag(pl, item, 99);
        InventoryService.gI().addItemBag(pl, Capsulekichhoat);
        InventoryService.gI().sendItemBag(pl);
        new Thread(() -> {
            Functions.sleep(1000);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + Capsulekichhoat.Name());
        }).start();
    }

    public void useHopQuaKichHoat5Sao(Player pl, Item item) {
        short[][] setByGender = {
            {0, 6, 21, 27, 12},
            {1, 7, 22, 28, 12},
            {2, 8, 23, 29, 12}
        };
        short[] setItems = setByGender[pl.gender];
        List<ItemOption> setOptions = new ArrayList<>();
        if (Util.isTrue(70, 100)) {
            int[] opsrand = ItemService.gI().randOptionItemKichHoat(pl.gender);
            setOptions.add(new ItemOption(opsrand[0], 0));
            setOptions.add(new ItemOption(opsrand[1], 0));
            setOptions.add(new ItemOption(30, 0));
            setOptions.add(new ItemOption(107, 5));
        } else {
            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(pl.gender);
            for (int op : opsrand) {
                setOptions.add(new ItemOption(op, 0));
            }
            setOptions.add(new ItemOption(30, 0));
            setOptions.add(new ItemOption(107, 5));
        }
        if (InventoryService.gI().getCountEmptyBag(pl) < setItems.length) {
            Service.gI().sendThongBao(pl, "Hành trang phải còn ít nhất " + setItems.length + " ô trống.");
            return;
        }
        List<Item> itemsReceived = new ArrayList<>();
        for (short tempId : setItems) {
            Item it = ItemService.gI().createNewItem(tempId);
            List<ItemOption> ops = ItemService.gI().getListOptionItemShop(tempId);
            if (!ops.isEmpty()) {
                it.itemOptions.addAll(ops);
            }
            for (ItemOption op : setOptions) {
                it.itemOptions.add(new ItemOption(op.optionTemplate.id, op.param));
            }
            InventoryService.gI().addItemBag(pl, it);
            itemsReceived.add(it);
        }
        short[] icon = {item.template.iconID, itemsReceived.get(0).template.iconID};
        CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        new Thread(() -> {
            Functions.sleep(2000);
            Service.gI().sendThongBao(pl, "Bạn nhận được Set Kích Hoạt 5 Sao!");
        }).start();
    }

    public void useHopQuaKichHoat3Sao(Player pl, Item item) {
        short[][] setByGender = {
            {0, 6, 21, 27, 12},
            {1, 7, 22, 28, 12},
            {2, 8, 23, 29, 12}
        };
        short[] setItems = setByGender[pl.gender];
        List<ItemOption> setOptions = new ArrayList<>();
        if (Util.isTrue(70, 100)) {
            int[] opsrand = ItemService.gI().randOptionItemKichHoat(pl.gender);
            setOptions.add(new ItemOption(opsrand[0], 0));
            setOptions.add(new ItemOption(opsrand[1], 0));
            setOptions.add(new ItemOption(30, 0));
            setOptions.add(new ItemOption(107, 3));
        } else {
            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(pl.gender);
            for (int op : opsrand) {
                setOptions.add(new ItemOption(op, 0));
            }
            setOptions.add(new ItemOption(30, 0));
            setOptions.add(new ItemOption(107, 3));
        }
        if (InventoryService.gI().getCountEmptyBag(pl) < setItems.length) {
            Service.gI().sendThongBao(pl, "Hành trang phải còn ít nhất " + setItems.length + " ô trống.");
            return;
        }
        List<Item> itemsReceived = new ArrayList<>();
        for (short tempId : setItems) {
            Item it = ItemService.gI().createNewItem(tempId);
            List<ItemOption> ops = ItemService.gI().getListOptionItemShop(tempId);
            if (!ops.isEmpty()) {
                it.itemOptions.addAll(ops);
            }
            for (ItemOption op : setOptions) {
                it.itemOptions.add(new ItemOption(op.optionTemplate.id, op.param));
            }
            InventoryService.gI().addItemBag(pl, it);
            itemsReceived.add(it);
        }
        short[] icon = {item.template.iconID, itemsReceived.get(0).template.iconID};
        CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        new Thread(() -> {
            Functions.sleep(2000);
            Service.gI().sendThongBao(pl, "Bạn nhận được Set Kích Hoạt 3 Sao!");
        }).start();
    }

    public void useHopQuaKichHoat(Player pl, Item item) {
        short[][] setByGender = {
            {0, 6, 21, 27, 12},
            {1, 7, 22, 28, 12},
            {2, 8, 23, 29, 12}
        };
        short[] setItems = setByGender[pl.gender];
        List<ItemOption> setOptions = new ArrayList<>();
        if (Util.isTrue(70, 100)) {
            int[] opsrand = ItemService.gI().randOptionItemKichHoat(pl.gender);
            setOptions.add(new ItemOption(opsrand[0], 0));
            setOptions.add(new ItemOption(opsrand[1], 0));
            setOptions.add(new ItemOption(30, 0));
        } else {
            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(pl.gender);
            for (int op : opsrand) {
                setOptions.add(new ItemOption(op, 0));
            }
            setOptions.add(new ItemOption(30, 0));
        }
        if (InventoryService.gI().getCountEmptyBag(pl) < setItems.length) {
            Service.gI().sendThongBao(pl, "Hành trang phải còn ít nhất " + setItems.length + " ô trống.");
            return;
        }
        List<Item> itemsReceived = new ArrayList<>();
        for (short tempId : setItems) {
            Item it = ItemService.gI().createNewItem(tempId);
            List<ItemOption> ops = ItemService.gI().getListOptionItemShop(tempId);
            if (!ops.isEmpty()) {
                it.itemOptions.addAll(ops);
            }
            for (ItemOption op : setOptions) {
                it.itemOptions.add(new ItemOption(op.optionTemplate.id, op.param));
            }
            InventoryService.gI().addItemBag(pl, it);
            itemsReceived.add(it);
        }
        short[] icon = {item.template.iconID, itemsReceived.get(0).template.iconID};
        CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        new Thread(() -> {
            Functions.sleep(2000);
            Service.gI().sendThongBao(pl, "Bạn nhận được Set Kích Hoạt!");
        }).start();
    }

    public void useCapsuleKichHoat(Player pl, Item item) {
        short tempId = (short) ItemService.gI().randTempItemKichHoat_VIP(pl.gender);
        Item itemReceived = ItemService.gI().createNewItem(tempId);
        List<ItemOption> ops = ItemService.gI().getListOptionItemShop(tempId);
        if (!ops.isEmpty()) {
            itemReceived.itemOptions.addAll(ops);
        }
        if (Util.isTrue(70, 100)) {
            int[] opsrand = ItemService.gI().randOptionItemKichHoat(pl.gender);
            itemReceived.itemOptions.add(new ItemOption(opsrand[0], 0));
            itemReceived.itemOptions.add(new ItemOption(opsrand[1], 0));
            itemReceived.itemOptions.add(new ItemOption(30, 0));
            itemReceived.itemOptions.add(new ItemOption(213, 1));
        } else {
            int[] opsrand = ItemService.gI().randOptionItemKichHoatNew(pl.gender);
            for (int op : opsrand) {
                itemReceived.itemOptions.add(new ItemOption(op, 0));
            }
            itemReceived.itemOptions.add(new ItemOption(30, 0));
            itemReceived.itemOptions.add(new ItemOption(213, 1));
        }
        short[] icon = {item.template.iconID, itemReceived.template.iconID};
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hành trang đã đầy, cần ít nhất 1 ô trống.");
        }
    }

    public void useVeRiengTu(Player pl) {
//        if (!pl.isNewMember) {
//            Service.gI().sendThongBao(pl, "Không thể thực hiện!");
//            return;
//        }
        ChangeMapService.gI().changeMapPlayerRandomZone(pl, 185 + pl.gender, -1, Util.nextInt(100, 150), 50);
    }

    public void useLoaTheGioi(Player pl) {
        Input.gI().createFormLoaTheGioi(pl);
    }

    public void useLoaVuTru(Player pl) {
        Input.gI().createFormLoaVuTru(pl);
    }

    public void useHopQuaBlackFriday(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1076, 1077, 1078, 1081, 1082, 1083, 1084, 1085, 1086, 956, 1204, 1813, 1702, 1789, 1786, 1283, 1173, 1143, 459};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 459:
                    itemReceived.addOptionParam(112, 80);
                    itemReceived.addOptionParam(93, 90);
                    itemReceived.addOptionParam(30, 0);
                    break;
                case 956:
                case 1204:
                    itemReceived.addOptionParam(87, 0);
                    itemReceived.addOptionParam(30, 0);
                    break;
                case 1813:
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(14, 13);
                    itemReceived.addOptionParam(5, 18);
                    itemReceived.addOptionParam(10, 10);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1702:
                    itemReceived.addOptionParam(50, 17);
                    itemReceived.addOptionParam(77, 17);
                    itemReceived.addOptionParam(103, 17);
                    itemReceived.addOptionParam(14, 11);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1786:
                    itemReceived.addOptionParam(50, Util.nextInt(21, 25));
                    itemReceived.addOptionParam(77, Util.nextInt(21, 25));
                    itemReceived.addOptionParam(103, Util.nextInt(21, 25));
                    itemReceived.addOptionParam(5, Util.nextInt(10, 20));
                    itemReceived.addOptionParam(10, Util.nextInt(10, 20));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1789:
                    itemReceived.addOptionParam(50, Util.nextInt(21, 24));
                    itemReceived.addOptionParam(77, Util.nextInt(25, 28));
                    itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                    itemReceived.addOptionParam(8, 4);
                    itemReceived.addOptionParam(94, 15);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1084:
                case 1085:
                case 1086:
                case 1283:
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                case 1173:
                    itemReceived.quantity = 5;
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopBlackFriday++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopQuaHungVuong(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1224, 1225, 1226, 1173, 1143, 1865, 1866};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1865:
                    itemReceived.addOptionParam(77, 18);
                    itemReceived.addOptionParam(103, 18);
                    itemReceived.addOptionParam(94, 11);
                    itemReceived.addOptionParam(108, 8);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1866:
                    itemReceived.addOptionParam(50, 17);
                    itemReceived.addOptionParam(14, 11);
                    itemReceived.addOptionParam(5, 15);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1224:
                case 1225:
                case 1226:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1173:
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopQuaGioTo++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopQuaHungVuongVIP(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1143, 1865, 1866, 1870, 1557};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1557:
                    itemReceived.addOptionParam(50, 25);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(103, 24);
                    itemReceived.addOptionParam(80, 15);
                    itemReceived.addOptionParam(108, 15);
                    itemReceived.addOptionParam(94, 15);
                    itemReceived.addOptionParam(114, 50);
                    itemReceived.addOptionParam(117, 10);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1870:
                    itemReceived.addOptionParam(50, 26);
                    itemReceived.addOptionParam(77, 23);
                    itemReceived.addOptionParam(103, 23);
                    itemReceived.addOptionParam(95, 10);
                    itemReceived.addOptionParam(97, 10);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1865:
                    itemReceived.addOptionParam(77, 18);
                    itemReceived.addOptionParam(103, 18);
                    itemReceived.addOptionParam(94, 11);
                    itemReceived.addOptionParam(108, 8);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1866:
                    itemReceived.addOptionParam(50, 17);
                    itemReceived.addOptionParam(14, 11);
                    itemReceived.addOptionParam(5, 15);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopQuaGioTo++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }
    public void useHopQuaThanLinh(Player player, Item item) {

    if (InventoryService.gI().getCountEmptyBag(player) < 5) {
        Service.gI().sendThongBao(player, "Yêu cầu có 5 ô trống hành trang");
        return;
    }
    int[][] items = {
        {555, 556, 562, 563, 561}, // TD
        {557, 558, 564, 565, 561}, // NM
        {559, 560, 566, 567, 561}  // XD
    };
    Item aotl  = ItemService.gI().createNewItem((short) items[player.gender][0]);
    Item wTl   = ItemService.gI().createNewItem((short) items[player.gender][1]);
    Item gTl   = ItemService.gI().createNewItem((short) items[player.gender][2]);
    Item jayTl = ItemService.gI().createNewItem((short) items[player.gender][3]);
    Item rdTl  = ItemService.gI().createNewItem((short) items[player.gender][4]);

    Item[] list = {aotl, wTl, gTl, jayTl, rdTl};

    for (Item it : list) {
        RewardService.gI().initChiSoItem(it);
        it.itemOptions.add(new ItemOption(30, 1)); // SKH
        InventoryService.gI().addItemBag(player, it);
    }

    InventoryService.gI().subQuantityItemsBag(player, item, 1);
    InventoryService.gI().sendItemBag(player);
    Service.gI().sendThongBao(player, "Bạn vừa nhận được Set Thần Linh");
}


    public void useHopQuaGioToXin(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {860, 421, 422, 1224, 1225, 1226, 1112, 1173, 1143, 1078, 1077, 1076, 1081, 1082, 1083, 1557};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1557:
                    itemReceived.addOptionParam(50, 25);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(103, 24);
                    itemReceived.addOptionParam(80, 15);
                    itemReceived.addOptionParam(108, 15);
                    itemReceived.addOptionParam(94, 15);
                    itemReceived.addOptionParam(114, 50);
                    itemReceived.addOptionParam(117, 10);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    }
                    break;
                case 860:
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(117, 20);
                    itemReceived.addOptionParam(114, 25);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 421:
                case 422:
                    itemReceived.addOptionParam(50, 12);
                    itemReceived.addOptionParam(94, 12);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(83, 0);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1224:
                case 1225:
                case 1226:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1112:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(14, 11);
                    itemReceived.addOptionParam(5, 15);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1173:
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopQuaGioTo++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopQuaGioToThuong(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {860, 421, 422, 1224, 1225, 1226, 1112};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 860:
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(117, 20);
                    itemReceived.addOptionParam(114, 25);
                    itemReceived.addOptionParam(77, 24);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 421:
                case 422:
                    itemReceived.addOptionParam(50, 12);
                    itemReceived.addOptionParam(94, 12);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(83, 0);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1224:
                case 1225:
                case 1226:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                case 1112:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 16));
                    itemReceived.addOptionParam(14, 11);
                    itemReceived.addOptionParam(5, 15);
                    itemReceived.addOptionParam(93, Param[Util.nextInt(0, Param.length - 1)]);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopQuaGioTo++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void UseHoaDang(Player player, Item item) {
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được 1 điểm hoa đăng");
        EffectMapService.gI().sendEffectMapToPlayer(player, 66, 1, -1, player.location.x, player.location.y, 1500);
        player.DuaTopHoaDang++;
    }

    public void UseHoaDangCoLoiChuc(Player player, Item item) {
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
        NpcService.gI().createMenuConMeo(player, ConstNpc.THA_HOA_DANG_CO_LOI_CHUC, -1,
                "Hãy chọn 1 lời chúc\n"
                + "1) Chúc các bạn vui vẻ\n"
                + "2) Anh thắp sáng đường vào tym em\n"
                + "3) Anh em FA mau thoát ế\n"
                + "4) Anh yêu em nhiều lắm, ahihi\n"
                + "5) Quẩy lên đi anh em\n"
                + "6) Mình iu các bạn nhìu lắm\n",
                "Lời chúc 1", "Lời chúc 2", "Lời chúc 3", "Lời chúc 4", "Lời chúc 5", "Lời chúc 6");
    }

    public void useHatDuaHau(Player player, Item item) {
        if (player.duahau == null) {
            MelonPlant.createDuaHau(player);
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendThongBao(player, "Cây Dưa Hấu vừa được trồng tại nhà bạn");
        } else {
            Service.gI().sendThongBao(player, "Bạn đã trồng cây Dưa Hấu rồi mà!");
        }
    }

    public void useRadaPhongXa(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.RADA_PHONG_XA, -1, "Đã tìm thấy Godzila và Kong, bạn có muốn đến đó ngay ?",
                "Đồng ý", "Từ chối");
    }

    public void useBanDoTruyenThuyet(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_DO_TRUYEN_THUYET, -1, "Đã tìm thấy Linh Thú Truyền Thuyết, bạn có muốn đến đó ngay ?",
                "Đồng ý", "Từ chối");
    }

    public void useManhTrungRongNhi(Player player) {
        ItemCheckUtil manhrong = new ItemCheckUtil(player).check(1881, 99, "Mảnh Trứng Rồng Nhí");
        if (manhrong.isAllEnough()) {
            NpcService.gI().createMenuConMeo(player, ConstNpc.MANH_TRUNG_RONG_NHI, -1, manhrong.getStatusText("Bạn Muốn Ghép Trứng Rồng ?", false, false, 0, 0),
                    "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, manhrong.getStatusText("Bạn Muốn Ghép Trứng Rồng ?", false, false, 0, 0),
                    "Đồng ý", "Từ chối");
        }
    }

    public void useThoNgoc(Player player, Item item) {
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
        TrungThuEventManager.gI().showListBoss(player, List.of(TypeEventBoss.TRUNG_THU));
    }

    public void useAnhTrangTron(Player player, Item item) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.CALL_KHI_DOT, -1, "Bạn có chắc muốn dùng " + item.Name() + " để dụ Khỉ đột khổng lồ không ?",
                "Đồng ý", "Từ chối");
    }

    public void useHopBanhTrungThuDacBiet(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1518, 1517, 1150, 1151, 1152, 1153, 1143, 1204, 1700, 1598};
        Item itemReceived = ItemService.gI().createNewItem(Util.isTrue(60, 100) ? List_Item[Util.nextInt(0, List_Item.length - 1)] : List_Item[Util.nextInt(8, 9)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1598:
                    itemReceived.addOptionParam(50, 11);
                    itemReceived.addOptionParam(77, 10);
                    itemReceived.addOptionParam(103, 10);
                    itemReceived.addOptionParam(94, 7);
                    itemReceived.addOptionParam(10, 15);
                    itemReceived.addOptionParam(85, 0);
                    itemReceived.addOptionParam(93, 365);
                    break;
                case 1204:
                    itemReceived.addOptionParam(87, 0);
                    itemReceived.addOptionParam(30, 0);
                    break;
                case 1518:
                case 1517:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1150:
                case 1151:
                case 1152:
                case 1153:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1700:
                    itemReceived.addOptionParam(50, Util.nextInt(23, 26));
                    itemReceived.addOptionParam(77, 21);
                    itemReceived.addOptionParam(103, 21);
                    itemReceived.addOptionParam(117, 13);
                    itemReceived.addOptionParam(14, 15);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoHopTrungThuDacBiet++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopQuaThang9VIP(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1518, 1150, 1151, 1152, 1153, 1693, 1143, 730, 731, 732};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1518:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1150:
                case 1151:
                case 1152:
                case 1153:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1693:
                    itemReceived.addOptionParam(50, Util.nextInt(26, 28));
                    itemReceived.addOptionParam(77, Util.nextInt(24, 26));
                    itemReceived.addOptionParam(103, Util.nextInt(24, 26));
                    itemReceived.addOptionParam(101, 30);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(92, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1143:
                    itemReceived.addOptionParam(30, 0);
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
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopQuaThang9(Player pl, Item item) {
        short[] Param = {3, 7, 15, 30};
        short[] List_Item = {1518, 1150, 1151, 1152, 1153, 1154, 1693, 1143};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1518:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1150:
                case 1151:
                case 1152:
                case 1153:
                case 1154:
                    itemReceived.addOptionParam(87, 0);
                    break;
                case 1693:
                    itemReceived.addOptionParam(50, Util.nextInt(26, 28));
                    itemReceived.addOptionParam(77, Util.nextInt(24, 26));
                    itemReceived.addOptionParam(103, Util.nextInt(24, 26));
                    itemReceived.addOptionParam(101, 30);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1143:
                    itemReceived.addOptionParam(30, 0);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void UsePhaoHoa(Player player, Item item) {
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
        Service.gI().addSMTN(player, (byte) 2, 20000, false);
        if (EventManager.VU_LAN_FESTIVAL) {
            player.DuaTopPhaoHoaVuLan++;
        }
        new Thread(() -> {
            int sl = 3;
            while (sl > 0) {
                sl--;
                EffectMapService.gI().sendEffectMapToAllInMap(player, 64, 2, 1, player.location.x + Util.nextInt(-30, 30), player.location.y - 30, 1);
                EffectMapService.gI().sendEffectMapToAllInMap(player, 62, 2, 1, player.location.x + Util.nextInt(-30, 30), player.location.y - 30, 1);
                EffectMapService.gI().sendEffectMapToAllInMap(player, 63, 2, 1, player.location.x + Util.nextInt(-30, 30), player.location.y - 30, 1);
                EffectMapService.gI().sendEffectMapToAllInMap(player, 65, 2, 1, player.location.x + Util.nextInt(-30, 30), player.location.y - 30, 1);
                Functions.sleep(Util.nextInt(100, 200));
            }
        }).start();
    }

    public void UseDuoiKhi(Player player, Item item) {
        if (player.effectSkill != null && player.effectSkill.isMonkey) {
            Service.gI().sendThongBao(player, "Không thể dùng Đuôi Khỉ khi đang Hoá Khỉ!");
            return;
        }
        try {
            EffectSkillService.gI().SendEffectBienHinhorUseItem(player);
            Service.gI().sendSpeedPlayer(player, 0);
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
        }
        EffectSkillService.gI().SendEffectBienHinhorUseItem(player);
        EffectSkillService.gI().setIsDuoiKhi(player);
        EffectSkillService.gI().SendEffectBienHinhorUseItem(player);
        Service.getInstance().sendSpeedPlayer(player, 0);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().sendSpeedPlayer(player, -1);
        Service.getInstance().point(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
        ItemTimeService.gI().sendAllItemTime(player);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
    }

    public void useCapsuleTrungThu(Player pl, Item item) {
        short[] Param = {7, 15, 30, 45};
        short[] List_Item = {733, 734, 735, 14, 15, 16, 1302, 1598, 579};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 733:
                case 734:
                case 735:
                    itemReceived.addOptionParam(84, 0);
                    itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(114, 25);
                    if (Util.isTrue(90, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1302:
                    itemReceived.addOptionParam(50, 27);
                    itemReceived.addOptionParam(77, 25);
                    itemReceived.addOptionParam(103, 25);
                    itemReceived.addOptionParam(101, 30);
                    itemReceived.addOptionParam(8, 4);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1598:
                    itemReceived.addOptionParam(84, 0);
                    itemReceived.addOptionParam(50, Util.nextInt(12, 15));
                    itemReceived.addOptionParam(77, Util.nextInt(12, 15));
                    itemReceived.addOptionParam(103, Util.nextInt(12, 15));
                    itemReceived.addOptionParam(14, 11);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 579:
                    itemReceived.addOptionParam(93, 30);
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void UseMilk(Player player, Item item) {
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        ItemTimeService.gI().clearAllItemTime(player);
    }

    public void TrongBongHoaHong_Max(Player pl) {
        ItemCheckUtil checker = new ItemCheckUtil(pl)
                .check(1526, 99, "Đất Trồng Cây")
                .check(1527, 5, "Ống Tre Nước")
                .check(1525, 1, "Túi Hạt Giống Hoa Hồng")
                .check(1528, 1, "Chậu Đất")
                .check(1529, 1, "Thuốc Tăng Trưởng");

        if (checker.isAllEnough()) {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.TRONG_BONG_HOA_HONG_MAX, -1,
                    checker.getStatusText("Trồng Bông Hoa Hồng", false, false, 0, 0), "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.IGNORE_MENU, -1,
                    checker.getStatusText("Trồng Bông Hoa Hồng", false, false, 0, 0), "Đóng");
        }
    }

    public void TrongBongHoaHong_Min(Player pl) {
        ItemCheckUtil checker = new ItemCheckUtil(pl)
                .check(1526, 99, "Đất Trồng Cây")
                .check(1527, 5, "Ống Tre Nước")
                .check(1525, 1, "Túi Hạt Giống Hoa Hồng")
                .check(1528, 1, "Chậu Đất");

        if (checker.isAllEnough()) {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.TRONG_BONG_HOA_HONG_MIN, -1,
                    checker.getStatusText("Trồng Bông Hoa Hồng", false, false, 0, 0), "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.IGNORE_MENU, -1,
                    checker.getStatusText("Trồng Bông Hoa Hồng", false, false, 0, 0), "Đóng");
        }
    }

    public void OngTreNuoc(Player pl) {
        NpcService.gI().createMenuConMeo(pl, ConstNpc.ONG_TRE_NUOC, -1, "Bạn muốn trồng loại nào?\n",
                "Trồng 1-3\nBông Hoa\nHồng", "Trồng 3-5\nBông Hoa\nHồng", "Đóng");
    }

    public void SocolaTraiTim(Player pl) {
        NpcService.gI().createMenuConMeo(pl, ConstNpc.SOCOLA_TRAI_TIM, -1, "Bạn muốn gói loại nào?\n",
                "Gói\nHộp Quà\nNhẹ Nhàng", "Gói\nHộp Quà\nChỉn Chu", "Đóng");
    }

    public void useGiaymau(Player pl) {
        ItemCheckUtil checker = new ItemCheckUtil(pl)
                .check(1505, 99, "Giấy Màu");

        if (checker.isAllEnough()) {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.GIAY_MAU, -1,
                    checker.getStatusText("Gói Hộp Đựng Quà", false, false, 0, 0), "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.IGNORE_MENU, -1,
                    checker.getStatusText("Gói Hộp Đựng Quà", false, false, 0, 0), "Đóng");
        }
    }

    public void GoiHopQuaNheNhang(Player pl) {
        ItemCheckUtil checker = new ItemCheckUtil(pl)
                .check(1508, 30, "Hoa Hồng Giấy")
                .check(1507, 5, "Sôcôla Trái Tim")
                .check(1506, 1, "Hộp Đựng Quà");

        if (checker.isAllEnough()) {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.GOI_HOP_QUA_NHE_NHANG, -1,
                    checker.getStatusText("Gói Hộp Quà Nhẹ Nhàng", false, false, 0, 0),
                    "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.IGNORE_MENU, -1,
                    checker.getStatusText("Gói Hộp Quà Nhẹ Nhàng", false, false, 0, 0),
                    "Đóng");
        }
    }

    public void GoiHopQuaChinChu(Player pl) {
        ItemCheckUtil checker = new ItemCheckUtil(pl)
                .check(1508, 30, "Hoa Hồng Giấy")
                .check(1507, 5, "Sôcôla Trái Tim")
                .check(1506, 1, "Hộp Đựng Quà")
                .check(1509, 1, "Nơ Trang Trí");

        if (checker.isAllEnough()) {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.GOI_HOP_QUA_CHIN_CHU, -1,
                    checker.getStatusText("Gói Hộp Quà Chỉn Chu", false, false, 0, 0),
                    "Đồng ý", "Từ chối");
        } else {
            NpcService.gI().createMenuConMeo(pl, ConstNpc.IGNORE_MENU, -1,
                    checker.getStatusText("Gói Hộp Quà Chỉn Chu", false, false, 0, 0),
                    "Đóng");
        }
    }

    public void useItemTime(Player pl, Item item) {
        switch (item.template.id) {
            case 382: //bổ huyết
                if (pl.itemTime.isUseBoHuyet2) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Bổ Huyết 2");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet = true;
                break;
            case 383: //bổ khí
                if (pl.itemTime.isUseBoKhi2) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Bổ Khí 2");
                    return;
                }
                pl.itemTime.lastTimeBoKhi = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi = true;
                break;
            case 384: //giáp xên
                if (pl.itemTime.isUseGiapXen2) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Giáp xên 2");
                    return;
                }
                pl.itemTime.lastTimeGiapXen = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen = true;
                break;
            case 381: //cuồng nộ
                if (pl.itemTime.isUseCuongNo2) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Cuồng nộ 2");
                    return;
                }
                pl.itemTime.lastTimeCuongNo = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo = true;
                Service.gI().point(pl);
                break;
            case 385: //ẩn danh
                if (pl.itemTime.isUseAnDanh2) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Ẩn danh 2");
                    return;
                }
                pl.itemTime.lastTimeAnDanh = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh = true;
                break;
            case 1150:// cn
                if (pl.itemTime.isUseCuongNo) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Cuồng nộ 1");
                    return;
                }
                pl.itemTime.lastTimeCuongNo2 = System.currentTimeMillis();
                pl.itemTime.isUseCuongNo2 = true;
                break;
            case 1152:// bo huyet
                if (pl.itemTime.isUseBoHuyet) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Bổ Huyết 1");
                    return;
                }
                pl.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis();
                pl.itemTime.isUseBoHuyet2 = true;
                break;
            case 1153://gx
                if (pl.itemTime.isUseGiapXen) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Giáp xên 1");
                    return;
                }
                pl.itemTime.lastTimeGiapXen2 = System.currentTimeMillis();
                pl.itemTime.isUseGiapXen2 = true;
                break;
            case 1151://bk
                if (pl.itemTime.isUseBoKhi) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Bổ Khí 1");
                    return;
                }
                pl.itemTime.lastTimeBoKhi2 = System.currentTimeMillis();
                pl.itemTime.isUseBoKhi2 = true;
                break;
            case 1154://an danh
                if (pl.itemTime.isUseAnDanh) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng Ẩn danh 1");
                    return;
                }
                pl.itemTime.lastTimeAnDanh2 = System.currentTimeMillis();
                pl.itemTime.isUseAnDanh2 = true;
                break;
            case 663: //bánh pudding
            case 664: //xúc xíc
            case 665: //kem dâu
            case 666: //mì ly
            case 667: //sushi
                pl.itemTime.lastTimeEatMeal = System.currentTimeMillis();
                pl.itemTime.isEatMeal = true;
                ItemTimeService.gI().removeItemTime(pl, pl.itemTime.iconMeal);
                pl.itemTime.iconMeal = item.template.iconID;
                break;
            case 379: //máy dò capsule
                pl.itemTime.lastTimeUseMayDo = System.currentTimeMillis();
                pl.itemTime.isUseMayDo = true;
                break;
            case 638:// commeson
                pl.itemTime.lastcommenson = System.currentTimeMillis();
                pl.itemTime.iscommenson = true;
                break;
            case 541:// hđ
            case 542:// hđ
                pl.itemTime.lastTimeHongDao = System.currentTimeMillis();
                pl.itemTime.isUseHongDao = true;
                Service.gI().point(pl);
                break;
            case 1327: //thuoctanghinh
                pl.itemTime.lastTimeTHUOCTANGHINH = System.currentTimeMillis();
                pl.itemTime.isUseTHUOCTANGHINH = true;
                Service.gI().point(pl);
                break;
            case 1498: //thuoctanghinh
                pl.itemTime.lastTimeTHUOCTANGHINH10 = System.currentTimeMillis();
                pl.itemTime.isUseTHUOCTANGHINH10 = true;
                Service.gI().point(pl);
                break;
            case 764: //khẩu trang
                pl.itemTime.LastKhauTrang = System.currentTimeMillis();
                pl.itemTime.IsKhauTrang = true;
                Service.gI().point(pl);
                break;
            case 899:
                pl.itemTime.LastTimeKeoMotMat = System.currentTimeMillis();
                pl.itemTime.IsKeoMotMat = true;
                Service.gI().point(pl);
                break;
            case 900:
                pl.itemTime.LastTimeSupbihacam = System.currentTimeMillis();
                pl.itemTime.IsSupbihacam = true;
                Service.gI().point(pl);
                break;
            case 902:
                pl.itemTime.LastTimebanhgatonhen = System.currentTimeMillis();
                pl.itemTime.Isbanhgatonhen = true;
                Service.gI().point(pl);
                break;
            case 903:
                pl.itemTime.LastTimehamburgersau = System.currentTimeMillis();
                pl.itemTime.Ishamburgersau = true;
                Service.gI().point(pl);
                break;
            case 880:
                pl.itemTime.lasttimecuarangme = System.currentTimeMillis();
                pl.itemTime.iscuarangme = true;
                Service.gI().point(pl);
                break;
            case 881:
                pl.itemTime.lasttimebachtuocnuong = System.currentTimeMillis();
                pl.itemTime.isbachtuocnuong = true;
                Service.gI().point(pl);
                break;
            case 882:
                pl.itemTime.lasttimetomtambot = System.currentTimeMillis();
                pl.itemTime.istomtambot = true;
                Service.gI().point(pl);
                break;
            case 1164:
                pl.itemTime.TimeBocPha = System.currentTimeMillis();
                pl.itemTime.IsBocPha = true;
                Service.gI().point(pl);
                break;
            case 1233: //Nồi cơm điện
                pl.itemTime.lastTimeUseNCD = System.currentTimeMillis();
                pl.itemTime.isUseNCD = true;
                break;
            case 752:
                if (pl.itemTime.isUseBanhTrung) {
                    Service.gI().sendThongBao(pl, "Bạn đang ăn bánh trưng, không thể ăn bánh tét!");
                    return;
                }
                pl.itemTime.lastTimeUseBanhTet = System.currentTimeMillis();
                pl.itemTime.isUseBanhTet = true;
                break;
            case 753:
                if (pl.itemTime.isUseBanhTet) {
                    Service.gI().sendThongBao(pl, "Bạn đang ăn bánh tét, không thể ăn bánh trưng!");
                    return;
                }
                pl.itemTime.lastTimeUseBanhTrung = System.currentTimeMillis();
                pl.itemTime.isUseBanhTrung = true;
                break;
            case 1189:
                pl.itemTime.lastTimeUseFoodMeoDen1 = System.currentTimeMillis();
                pl.itemTime.isUseFoodMeoDen1 = true;
                break;
            case 1190:
                pl.itemTime.lastTimeUseFoodMeoDen2 = System.currentTimeMillis();
                pl.itemTime.isUseFoodMeoDen2 = true;
                break;
            case 1480:
                pl.itemTime.lastTimeUseMiThangLong = System.currentTimeMillis();
                pl.itemTime.isUseMiThangLong = true;
                break;
            case 1481:
                pl.itemTime.lastTimeUseComGaQuay = System.currentTimeMillis();
                pl.itemTime.isUseComGaQuay = true;
                break;
            case 1852:
                pl.itemTime.lastTimeUseChuotMap = System.currentTimeMillis();
                pl.itemTime.isUseChuotMap = true;
                break;
            case 1404:
                if (pl.itemTime.isUseChiMang3) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng chí mạng 3, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseChiMang2 = System.currentTimeMillis();
                pl.itemTime.isUseChiMang2 = true;
                break;
            case 1405:
                if (pl.itemTime.isUseChiMang2) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng chí mạng 2, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseChiMang3 = System.currentTimeMillis();
                pl.itemTime.isUseChiMang3 = true;
                break;
            case 1406:
                if (pl.itemTime.isUseNedon2) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng né đòn 2, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseNedon = System.currentTimeMillis();
                pl.itemTime.isUseNedon = true;
                break;
            case 1407:
                if (pl.itemTime.isUseNedon) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng né đòn, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseNedon2 = System.currentTimeMillis();
                pl.itemTime.isUseNedon2 = true;
                break;
            case 1409:
                if (pl.itemTime.isUsePhanSatThuong2 || pl.itemTime.isUsePhanSatThuong3) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử phản sát thương cấp độ khác, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUsePhanSatThuong = System.currentTimeMillis();
                pl.itemTime.isUsePhanSatThuong = true;
                break;
            case 1410:
                if (pl.itemTime.isUsePhanSatThuong || pl.itemTime.isUsePhanSatThuong3) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử phản sát thương cấp độ khác, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUsePhanSatThuong2 = System.currentTimeMillis();
                pl.itemTime.isUsePhanSatThuong2 = true;
                break;
            case 1411:
                if (pl.itemTime.isUsePhanSatThuong || pl.itemTime.isUsePhanSatThuong2) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử phản sát thương cấp độ khác, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUsePhanSatThuong3 = System.currentTimeMillis();
                pl.itemTime.isUsePhanSatThuong3 = true;
                break;
            case 1412:
                if (pl.itemTime.isUseKamejoko2) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng Kamejoko 2, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseKamejoko = System.currentTimeMillis();
                pl.itemTime.isUseKamejoko = true;
                break;
            case 1413:
                if (pl.itemTime.isUseKamejoko) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng Kamejoko, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseKamejoko2 = System.currentTimeMillis();
                pl.itemTime.isUseKamejoko2 = true;
                break;
            case 2062:
                pl.itemTime.lastTimeUseRocket1h = System.currentTimeMillis();
                pl.itemTime.isUseRocket1h = true;
                break;
            case 2069:
                pl.itemTime.lastTimeUseHoiSieuCap = System.currentTimeMillis();
                pl.itemTime.isUseHoiSieuCap = true;
                break;
            case 1517:
                if (pl.itemTime.isUseSatThuongChuan2) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng Sát thương chuẩn 2, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseSatThuongChuan = System.currentTimeMillis();
                pl.itemTime.isUseSatThuongChuan = true;
                break;
            case 1518:
                if (pl.itemTime.isUseSatThuongChuan) {
                    Service.gI().sendThongBao(pl, "Bạn đang sử dụng Sát thương chuẩn, không thể sử dụng thêm vật phẩm này.");
                    return;
                }
                pl.itemTime.lastTimeUseSatThuongChuan2 = System.currentTimeMillis();
                pl.itemTime.isUseSatThuongChuan2 = true;
                break;
            case 1628:
                pl.itemTime.lastTimeUseBuaTNSMDetu = System.currentTimeMillis();
                pl.itemTime.isUseBuaTNSMDetu = true;
                break;
            case 1672:
                pl.itemTime.lastTimeUseSauRieng = System.currentTimeMillis();
                pl.itemTime.isUseSauRieng = true;
                break;
            case 1635:
                pl.itemTime.lastTimeUseCoBonLa = System.currentTimeMillis();
                pl.itemTime.isUseCoBonLa = true;
                break;
            case 1264:
                pl.itemTime.lastTimeUseMayDoLinhHon = System.currentTimeMillis();
                pl.itemTime.isUseMayDoLinhHon = true;
                break;
            case 1986:
                pl.itemTime.lastTimeUseMayDoSieuHoa = System.currentTimeMillis();
                pl.itemTime.isUseMayDoSieuHoa = true;
                break;
            case 1115:
                pl.itemTime.lastTimeUseMayDoNgocBi = System.currentTimeMillis();
                pl.itemTime.isUseMayDoNgocBi = true;
                break;
            case 1306:
                if (pl.itemTime.isUseBanhDeoC2 || pl.itemTime.isUseBanhDeoC3) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng cùng lúc được một chiếc Bánh dẻo duy nhất.");
                    return;
                }
                pl.itemTime.lastTimeUseBanhDeoC1 = System.currentTimeMillis();
                pl.itemTime.isUseBanhDeoC1 = true;
                break;
            case 1307:
                if (pl.itemTime.isUseBanhDeoC1 || pl.itemTime.isUseBanhDeoC3) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng cùng lúc được một chiếc Bánh dẻo duy nhất.");
                    return;
                }
                pl.itemTime.lastTimeUseBanhDeoC2 = System.currentTimeMillis();
                pl.itemTime.isUseBanhDeoC2 = true;
                break;
            case 1308:
                if (pl.itemTime.isUseBanhDeoC1 || pl.itemTime.isUseBanhDeoC2) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng cùng lúc được một chiếc Bánh dẻo duy nhất.");
                    return;
                }
                pl.itemTime.lastTimeUseBanhDeoC3 = System.currentTimeMillis();
                pl.itemTime.isUseBanhDeoC3 = true;
                break;
            case 465:
                if (pl.itemTime.isUseTrungThu2Trung || pl.itemTime.isUseTrungThuDB || pl.itemTime.isUseHBTrungThu) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng được một loại Bánh trung thu cùng loại.");
                    return;
                }
                pl.itemTime.lastTimeUseTrungThu1Trung = System.currentTimeMillis();
                pl.itemTime.isUseTrungThu1Trung = true;
                break;
            case 466:
                if (pl.itemTime.isUseTrungThu1Trung || pl.itemTime.isUseTrungThuDB || pl.itemTime.isUseHBTrungThu) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng được một loại Bánh trung thu cùng loại.");
                    return;
                }
                pl.itemTime.lastTimeUseTrungThu2Trung = System.currentTimeMillis();
                pl.itemTime.isUseTrungThu2Trung = true;
                break;
            case 472:
                if (pl.itemTime.isUseTrungThu1Trung || pl.itemTime.isUseTrungThu2Trung || pl.itemTime.isUseHBTrungThu) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng được một loại Bánh trung thu cùng loại.");
                    return;
                }
                pl.itemTime.lastTimeUseTrungThuDB = System.currentTimeMillis();
                pl.itemTime.isUseTrungThuDB = true;
                break;
            case 473:
                if (pl.itemTime.isUseTrungThu1Trung || pl.itemTime.isUseTrungThu2Trung || pl.itemTime.isUseTrungThuDB) {
                    Service.gI().sendThongBao(pl, "Chỉ sử dụng được một loại Bánh trung thu cùng loại.");
                    return;
                }
                pl.itemTime.lastTimeUseHBTrungThu = System.currentTimeMillis();
                pl.itemTime.isUseHBTrungThu = true;
                break;
            case 1016:
                if (pl.itemTime.Isthuocmodacbiet) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng thuốc mỡ ipana đặc biệt, không thể sử dụng thuốc mỡ ipana thường");
                    return;
                }
                pl.itemTime.LastTimethuocmothuong = System.currentTimeMillis();
                pl.itemTime.Isthuocmothuong = true;
                break;
            case 1017:
                if (pl.itemTime.Isthuocmothuong) {
                    Service.gI().sendThongBao(pl, "Bạn đang dùng thuốc mỡ ipana thường, không thể sử dụng thuốc mỡ ipana đặc biệt");
                    return;
                }
                pl.itemTime.LastTimethuocmodacbiet = System.currentTimeMillis();
                pl.itemTime.Isthuocmodacbiet = true;
                break;
            case 1045:
                pl.itemTime.lastTimeUseDuoiKhiTNSM = System.currentTimeMillis();
                pl.itemTime.isUseDuoiKhiTNSM = true;
                break;
        }
        Service.gI().point(pl);
        ItemTimeService.gI().sendAllItemTime(pl);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
    }

    public void useThiepMung8_3(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1861, 837, 838, 839, 840, 841, 842, 859, 956, 1150, 1151, 1152, 1153, 1154, 1512, 1515};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1861:
                    itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(77, Util.nextInt(8, 15));
                    itemReceived.addOptionParam(103, Util.nextInt(8, 15));
                    itemReceived.addOptionParam(160, 25);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 837:
                case 838:
                case 839:
                case 840:
                case 841:
                case 842:
                case 859:
                case 956:
                    itemReceived.addOptionParam(87, 0);
                    itemReceived.addOptionParam(30, 0);
                    break;
                case 1150:
                case 1151:
                case 1152:
                case 1153:
                case 1154:
                    itemReceived.addOptionParam(86, 0);
                    break;
                case 1512:
                    itemReceived.addOptionParam(50, Util.nextInt(21, 26));
                    itemReceived.addOptionParam(77, Util.nextInt(21, 26));
                    itemReceived.addOptionParam(103, Util.nextInt(21, 26));
                    itemReceived.addOptionParam(101, 30);
                    itemReceived.addOptionParam(162, 2);
                    itemReceived.addOptionParam(8, 4);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1515:
                    itemReceived.addOptionParam(50, Util.nextInt(14, 17));
                    itemReceived.addOptionParam(77, Util.nextInt(14, 17));
                    itemReceived.addOptionParam(103, Util.nextInt(14, 17));
                    itemReceived.addOptionParam(101, 20);
                    itemReceived.addOptionParam(8, 2);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoThiep83++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopBabyThreeThuong(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1765, 1766, 1767, 1768, 1862, 1863};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1765:
                    if (Util.isTrue(50, 100)) {
                        itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    } else {
                        itemReceived.addOptionParam(103, Util.nextInt(18, 20));
                    }
                    itemReceived.addOptionParam(50, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(94, Util.nextInt(5, 10));
                    itemReceived.addOptionParam(5, Util.nextInt(5, 15));
                    itemReceived.addOptionParam(14, Util.nextInt(5, 10));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1766:
                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(94, Util.nextInt(5, 10));
                    itemReceived.addOptionParam(108, Util.nextInt(5, 10));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1767:
                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(5, Util.nextInt(5, 15));
                    itemReceived.addOptionParam(14, Util.nextInt(5, 10));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1768:
                    itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(236, 20);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1862:
                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(5, Util.nextInt(5, 15));
                    itemReceived.addOptionParam(14, Util.nextInt(5, 10));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1863:
                    itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(94, Util.nextInt(5, 10));
                    itemReceived.addOptionParam(108, Util.nextInt(5, 10));
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHopBabyThreeVIP(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1765, 1766, 1767, 1768, 1862, 1863};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 1765:
                    if (Util.isTrue(50, 100)) {
                        itemReceived.addOptionParam(77, Util.nextInt(20, 22));
                    } else {
                        itemReceived.addOptionParam(103, Util.nextInt(20, 22));
                    }
                    itemReceived.addOptionParam(50, Util.nextInt(20, 22));
                    itemReceived.addOptionParam(94, Util.nextInt(8, 12));
                    itemReceived.addOptionParam(5, Util.nextInt(8, 20));
                    itemReceived.addOptionParam(14, Util.nextInt(8, 12));
                    if (Util.isTrue(98, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1766:
                    itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(94, Util.nextInt(8, 12));
                    itemReceived.addOptionParam(108, Util.nextInt(8, 12));
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1767:
                    itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(5, Util.nextInt(8, 20));
                    itemReceived.addOptionParam(14, Util.nextInt(8, 12));
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1768:
                    itemReceived.addOptionParam(50, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(103, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(236, 20);
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1862:
                    itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(5, Util.nextInt(8, 20));
                    itemReceived.addOptionParam(14, Util.nextInt(8, 12));
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                case 1863:
                    itemReceived.addOptionParam(77, Util.nextInt(18, 20));
                    itemReceived.addOptionParam(94, Util.nextInt(8, 12));
                    itemReceived.addOptionParam(108, Util.nextInt(8, 12));
                    if (Util.isTrue(95, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useCapsuleHong(Player pl, Item item) {
        short[] List_Item = {580, 581, 582, 583, 464, 675, 724, 676, 677, 678, 679, 680, 681, 760};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            switch (Item_Template) {
                case 580:
                case 581:
                case 582:
                case 675:
                case 676:
                case 677:
                case 678:
                case 679:
                case 680:
                case 681:
                case 760:
                    List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) Item_Template);
                    if (!ops.isEmpty()) {
                        itemReceived.itemOptions = ops;
                    }
                    if (Util.isTrue(90, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, 180));
                    }
                    break;
                case 583:
                    itemReceived.addOptionParam(50, 8);
                    itemReceived.addOptionParam(77, 8);
                    itemReceived.addOptionParam(103, 8);
                    itemReceived.addOptionParam(150, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, 180));
                    }
                    break;
                case 464:
                    itemReceived.addOptionParam(50, 15);
                    itemReceived.addOptionParam(117, 15);
                    itemReceived.addOptionParam(114, 24);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, 180));
                    }
                    break;
                case 724:
                    itemReceived.addOptionParam(50, 20);
                    itemReceived.addOptionParam(77, 17);
                    itemReceived.addOptionParam(103, 17);
                    itemReceived.addOptionParam(162, 2);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, 180));
                    }
                    break;
                default:
                    break;
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useTuiMuHalloween(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1358, 1359, 1785, 1702, 1790, 1793, 1810};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1810) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 20));
                itemReceived.addOptionParam(103, Util.nextInt(15, 20));
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(108, 11);
                itemReceived.addOptionParam(94, 11);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1793) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                itemReceived.addOptionParam(14, 11);
                itemReceived.addOptionParam(32, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1790) {
                itemReceived.addOptionParam(50, 28);
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(14, 15);
                itemReceived.addOptionParam(94, 15);
                itemReceived.addOptionParam(117, 5);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1702) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(14, 11);
                itemReceived.addOptionParam(5, 18);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1785) {
                itemReceived.addOptionParam(50, Util.nextInt(11, 15));
                itemReceived.addOptionParam(77, Util.nextInt(11, 15));
                itemReceived.addOptionParam(103, Util.nextInt(11, 15));
                itemReceived.addOptionParam(14, 11);
                itemReceived.addOptionParam(84, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1358) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                itemReceived.addOptionParam(95, 20);
                itemReceived.addOptionParam(96, 20);
                itemReceived.addOptionParam(5, 20);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1359) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 18));
                itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                itemReceived.addOptionParam(191, Util.nextInt(3, 5));
                itemReceived.addOptionParam(8, Util.nextInt(2, 4));
                itemReceived.addOptionParam(14, 11);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useCapsuneSquidGame(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {914, 916, 917, 918, 919};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 914) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(14, 11);
                itemReceived.addOptionParam(5, 20);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 916 && Item_Template <= 918) {
                itemReceived.addOptionParam(50, Util.nextInt(13, 17));
                itemReceived.addOptionParam(77, Util.nextInt(13, 17));
                itemReceived.addOptionParam(103, Util.nextInt(13, 17));
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(94, 11);
                } else {
                    itemReceived.addOptionParam(14, 11);
                }
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 919) {
                itemReceived.addOptionParam(101, 20);
                itemReceived.addOptionParam(50, Util.nextInt(14, 17));
                itemReceived.addOptionParam(77, Util.nextInt(14, 17));
                itemReceived.addOptionParam(103, Util.nextInt(14, 17));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useCaplsuneHalloween(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {595, 604, 605, 606, 1358, 1359, 1360, 1361, 743, 1109, 740, 1108};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1109 || Item_Template == 740) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(14, 11);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1108) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, 12);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 743) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(50, Util.nextInt(10, 14));
                itemReceived.addOptionParam(77, Util.nextInt(10, 14));
                itemReceived.addOptionParam(103, Util.nextInt(10, 14));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 595) {
                itemReceived.addOptionParam(2, 256);
                itemReceived.quantity = 20;
            }
            if (Item_Template == 604 || Item_Template == 605 || Item_Template == 606) {
                itemReceived.addOptionParam(50, 23);
                itemReceived.addOptionParam(77, 20);
                itemReceived.addOptionParam(103, 20);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 1358 && Item_Template <= 1361) {
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                } else {
                    itemReceived.addOptionParam(97, Util.nextInt(10, 15));
                }
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(80, Util.nextInt(10, 15));
                } else {
                    itemReceived.addOptionParam(81, Util.nextInt(10, 15));
                }
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(101, 20);
                }
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useHomHalloween(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1105, 1104, 906, 1106};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1106) {
                itemReceived.addOptionParam(50, Util.nextInt(21, 24));
                itemReceived.addOptionParam(77, Util.nextInt(21, 24));
                itemReceived.addOptionParam(94, Util.nextInt(12, 15));
                itemReceived.addOptionParam(114, 25);
                itemReceived.addOptionParam(97, Util.nextInt(12, 15));
                itemReceived.addOptionParam(95, Util.nextInt(15, 17));
                itemReceived.addOptionParam(96, Util.nextInt(15, 17));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 906) {
                itemReceived.addOptionParam(50, 24);
                itemReceived.addOptionParam(14, 3);
                itemReceived.addOptionParam(77, 19);
                itemReceived.addOptionParam(103, 19);
                itemReceived.addOptionParam(94, 19);
                itemReceived.addOptionParam(5, Util.nextInt(13, 17));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1104) {
                itemReceived.addOptionParam(50, Util.nextInt(21, 24));
                itemReceived.addOptionParam(77, Util.nextInt(21, 24));
                itemReceived.addOptionParam(94, Util.nextInt(13, 17));
                itemReceived.addOptionParam(114, 25);
                itemReceived.addOptionParam(97, Util.nextInt(13, 17));
                itemReceived.addOptionParam(8, 4);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1105) {
                itemReceived.addOptionParam(50, Util.nextInt(21, 24));
                itemReceived.addOptionParam(77, Util.nextInt(21, 24));
                itemReceived.addOptionParam(94, Util.nextInt(15, 17));
                itemReceived.addOptionParam(114, 25);
                itemReceived.addOptionParam(97, Util.nextInt(15, 17));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useBoKeoKinhDi(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {702, 703, 704, 705, 706, 707, 708, 1345, 1346, 1347, 1802};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1802) {
                itemReceived.addOptionParam(50, 17);
                itemReceived.addOptionParam(77, 17);
                itemReceived.addOptionParam(103, 17);
                itemReceived.addOptionParam(101, 30);
                if (Util.isTrue(98, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1347) {
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, 20);
                if (Util.isTrue(98, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1345) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(50, Util.nextInt(12, 16));
                itemReceived.addOptionParam(77, Util.nextInt(10, 15));
                itemReceived.addOptionParam(103, Util.nextInt(10, 15));
                if (Util.isTrue(98, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1346) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(50, Util.nextInt(10, 15));
                itemReceived.addOptionParam(77, Util.nextInt(12, 16));
                itemReceived.addOptionParam(103, Util.nextInt(12, 16));
                if (Util.isTrue(98, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 702 && Item_Template <= 708) {
                itemReceived.itemOptions.add(new ItemOption(87, 0));
                itemReceived.itemOptions.add(new ItemOption(30, 0));
                itemReceived.itemOptions.add(new ItemOption(93, 35));
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                pl.event.addHalloweenPoint(1);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void useThiepHalloween(Player player, Item item) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        Item GoiQua = ItemService.gI().createNewItem((short) 1352, 1);
        GoiQua.addOptionParam(93, 30);
        GoiQua.addOptionParam(30, 0);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().addItemBag(player, GoiQua);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được " + GoiQua.template.name);
        player.DuaTopThiepHalloween++;
    }

    public void useKeoHalloween(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (Util.isTrue(25, 100)) {
            int Gold = Util.nextInt(80, 200) * 100000;
            pl.inventory.addGold(Gold);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + Util.formatNumber(Gold, FormatStyle.VIETNAMESE) + " vàng");
            Service.gI().sendMoney(pl);
            InventoryService.gI().sendItemBag(pl);
        } else if (Util.isTrue(25, 100)) {
            int Ruby = Util.nextInt(5, 15);
            pl.inventory.addRuby(Ruby);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + Ruby + " hồng ngọc");
            Service.gI().sendMoney(pl);
            InventoryService.gI().sendItemBag(pl);
        } else {
            short[] Param = {3, 5, 7, 15, 30};
            short[] List_Item = {897, 1173, 1143, 1107, 1789, 1791, 1041, 1042, 1043, 644, 645, 646, 743};
            Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
            int Item_Template = itemReceived.template.id;
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            icon[1] = itemReceived.template.iconID;
            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                if (Item_Template == 644 || Item_Template == 645 || Item_Template == 646) {
                    itemReceived.addOptionParam(8, 4);
                    itemReceived.addOptionParam(50, Util.nextInt(18, 22));
                    itemReceived.addOptionParam(77, Util.nextInt(18, 22));
                    itemReceived.addOptionParam(103, Util.nextInt(18, 22));
                    if (Util.isTrue(98, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1041 || Item_Template == 1042 || Item_Template == 1043) {
                    itemReceived.addOptionParam(50, Util.nextInt(22, 25));
                    itemReceived.addOptionParam(77, Util.nextInt(22, 25));
                    itemReceived.addOptionParam(103, Util.nextInt(22, 25));
                    itemReceived.addOptionParam(94, Util.nextInt(15, 18));
                    itemReceived.addOptionParam(114, 20);
                    itemReceived.addOptionParam(108, Util.nextInt(10, 15));
                    itemReceived.addOptionParam(196, 2);
                    if (Util.isTrue(98, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1791) {
                    itemReceived.addOptionParam(50, 24);
                    itemReceived.addOptionParam(77, 22);
                    itemReceived.addOptionParam(103, 22);
                    itemReceived.addOptionParam(159, 5);
                    itemReceived.addOptionParam(160, 60);
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
                if (Item_Template == 1789) {
                    itemReceived.addOptionParam(50, Util.nextInt(21, 24));
                    itemReceived.addOptionParam(77, Util.nextInt(25, 28));
                    itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                    itemReceived.addOptionParam(8, 4);
                    itemReceived.addOptionParam(94, 15);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1107) {
                    itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(14, 11);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 897 || Item_Template == 743) {
                    itemReceived.addOptionParam(84, 0);
                    itemReceived.addOptionParam(114, 25);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1143) {
                    itemReceived.addOptionParam(30, 0);
                }
                if (Item_Template == 1173) {
                    itemReceived.addOptionParam(30, 0);
                }
                InventoryService.gI().addItemBag(pl, itemReceived);
                CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                new Thread(() -> {
                    Functions.sleep(2000);
                    pl.event.addHalloweenPoint(1);
                    Service.gI().sendThongBao(pl, "Bạn nhận được 1 điểm sự kiện");
                }).start();
            } else {
                Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            }
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
    }

    public void useHopKeoMaQuy(Player player, Item item) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        Item Keo = ItemService.gI().createNewItem((short) 1357, 1);
        Keo.addOptionParam(93, 30);
        Keo.addOptionParam(30, 0);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().addItemBag(player, Keo);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được " + Keo.template.name);
        player.DuaTopMoHopMaQuy++;
    }

    public void HoiSinhLichTen(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1201, 815, 816, 817, 1266, 1267, 1268};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1201) {
                itemReceived.addOptionParam(77, 24);
                itemReceived.addOptionParam(103, 24);
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(101, 25);
                itemReceived.addOptionParam(95, 15);
                itemReceived.addOptionParam(96, 15);
                itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
            }
            if (Item_Template == 815) {
                itemReceived.addOptionParam(50, Util.nextInt(13, 16));
                itemReceived.addOptionParam(14, Util.nextInt(10, 12));
                itemReceived.addOptionParam(5, Util.nextInt(15, 20));
                itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
            }
            if (Item_Template == 816) {
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, Util.nextInt(10, 12));
                itemReceived.addOptionParam(80, Util.nextInt(15, 20));
                itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
            }
            if (Item_Template == 817) {
                itemReceived.addOptionParam(103, Util.nextInt(16, 18));
                itemReceived.addOptionParam(97, Util.nextInt(10, 12));
                itemReceived.addOptionParam(81, Util.nextInt(18, 20));
                itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
            }
            if (Item_Template == 1266 || Item_Template == 1267 || Item_Template == 1268) {
                itemReceived.addOptionParam(77, Util.nextInt(15, 18));
                itemReceived.addOptionParam(103, Util.nextInt(15, 18));
                itemReceived.addOptionParam(97, 10);
                itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void HoiSinhSieuLichTen(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {906, 913, 815, 816, 817, 1266, 1267, 1268, 1269, 1270, 1271};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 906) {
                itemReceived.addOptionParam(50, 20);
                itemReceived.addOptionParam(14, 12);
                itemReceived.addOptionParam(77, 19);
                itemReceived.addOptionParam(103, 19);
                itemReceived.addOptionParam(94, 19);
                itemReceived.addOptionParam(5, 14);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 913) {
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(103, 20);
                itemReceived.addOptionParam(77, 20);
                itemReceived.addOptionParam(94, 18);
                itemReceived.addOptionParam(184, 2);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 815) {
                itemReceived.addOptionParam(50, 20);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 816) {
                itemReceived.addOptionParam(77, 24);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 817) {
                itemReceived.addOptionParam(103, 24);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1266 || Item_Template == 1267 || Item_Template == 1268) {
                itemReceived.addOptionParam(77, 20);
                itemReceived.addOptionParam(103, 20);
                itemReceived.addOptionParam(97, 10);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1269 || Item_Template == 1270 || Item_Template == 1271) {
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(97, 15);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void UseQueDiemBungChay(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1456, 1454, 1437};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1437) {
                itemReceived.addOptionParam(77, 24);
                itemReceived.addOptionParam(103, 24);
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(97, 15);
                itemReceived.addOptionParam(94, 15);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1456) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(14, 12);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1454) {
                itemReceived.addOptionParam(77, 24);
                itemReceived.addOptionParam(103, 24);
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(14, 5);
                itemReceived.addOptionParam(97, 15);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                pl.DuaTopDotDiem++;
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void UseQueDiem(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        Item BaoDiem = InventoryService.gI().findItemBag(pl, 1839);
        if (BaoDiem != null && BaoDiem.quantity > 0) {
            Item QueDiemBungChay = ItemService.gI().createNewItem((short) 1847, 1);
            QueDiemBungChay.itemOptions.add(new ItemOption(30, 0));
            QueDiemBungChay.itemOptions.add(new ItemOption(93, 35));
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().subQuantityItemsBag(pl, BaoDiem, 1);
            InventoryService.gI().addItemBag(pl, QueDiemBungChay);
            InventoryService.gI().sendItemBag(pl);
            new Thread(() -> {
                Functions.sleep(1000);
                pl.event.addChristMasPoint(1);
                Service.gI().sendThongBao(pl, "Bạn nhận được 1 điểm sự kiện");
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Bạn không có hộp diêm thống nhất");
        }
    }

    public void openHopQuaGiangSinh(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            return;
        }
        if (Util.isTrue(50, 100)) {
            Item KeoGiangSinh = ItemService.gI().createNewItem((short) 533);
            KeoGiangSinh.itemOptions.add(new ItemOption(30, 0));
            InventoryService.gI().addItemBag(pl, KeoGiangSinh);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + KeoGiangSinh.Name());
            Service.gI().sendMoney(pl);
            InventoryService.gI().sendItemBag(pl);
        } else if (Util.isTrue(40, 100)) {
            int Gold = Util.nextInt(80, 200) * 1000000;
            pl.inventory.addGold(Gold);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + Util.formatNumber(Gold, FormatStyle.VIETNAMESE) + " vàng");
            Service.gI().sendMoney(pl);
            InventoryService.gI().sendItemBag(pl);
        } else if (Util.isTrue(40, 100)) {
            int Ruby = Util.nextInt(10, 20);
            pl.inventory.addRuby(Ruby);
            Service.gI().sendThongBao(pl, "Bạn nhận được " + Ruby + " hồng ngọc");
            Service.gI().sendMoney(pl);
            InventoryService.gI().sendItemBag(pl);
        } else {
            short[] Param = {3, 5, 7, 15, 30};
            short[] List_Item = {1443, 1455, 1465, 1466, 1143, 1173, 1836, 1830, 1832, 1829, 1748};
            Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
            int Item_Template = itemReceived.template.id;
            short[] icon = new short[2];
            icon[0] = item.template.iconID;
            icon[1] = itemReceived.template.iconID;
            if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                if (Item_Template == 1829 || Item_Template == 1748) {
                    itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                    itemReceived.addOptionParam(14, 12);
                    itemReceived.addOptionParam(94, 12);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1832) {
                    itemReceived.addOptionParam(50, Util.nextInt(12, 17));
                    itemReceived.addOptionParam(77, Util.nextInt(12, 17));
                    itemReceived.addOptionParam(103, Util.nextInt(12, 17));
                    itemReceived.addOptionParam(14, 10);
                    itemReceived.addOptionParam(94, 10);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1443 || Item_Template == 1455 || Item_Template == 1465 || Item_Template == 1466) {
                    itemReceived.addOptionParam(84, 0);
                    itemReceived.addOptionParam(50, 10);
                    itemReceived.addOptionParam(77, 12);
                    itemReceived.addOptionParam(94, 10);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1830) {
                    itemReceived.addOptionParam(84, 0);
                    itemReceived.addOptionParam(50, 12);
                    itemReceived.addOptionParam(77, 12);
                    itemReceived.addOptionParam(103, 12);
                    itemReceived.addOptionParam(14, 10);
                    itemReceived.addOptionParam(106, 0);
                    if (Util.isTrue(99, 100)) {
                        itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                    }
                }
                if (Item_Template == 1143) {
                    itemReceived.addOptionParam(30, 0);
                }
                if (Item_Template == 1173) {
                    itemReceived.addOptionParam(30, 0);
                }
                if (Item_Template == 1836) {
                    itemReceived.addOptionParam(30, 0);
                    itemReceived.addOptionParam(93, 30);
                }
                InventoryService.gI().addItemBag(pl, itemReceived);
                CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
                new Thread(() -> {
                    Functions.sleep(2000);
                    Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
                }).start();
            } else {
                Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
            }
        }
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
    }

    public void openNguoiTuyetBangGia(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1371, 1380, 1381, 954, 955, 879, 1150, 1151, 1152, 1153, 1154, 1455, 1456, 922, 923, 924, 937, 1454, 1437, 1424};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1424 || Item_Template == 1437) {
                itemReceived.addOptionParam(50, 24);
                itemReceived.addOptionParam(77, 24);
                itemReceived.addOptionParam(103, 24);
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(81, 20);
                itemReceived.addOptionParam(94, 15);
                itemReceived.addOptionParam(108, 15);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1454) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(117, 15);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(210, 1);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 937) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 922 || Item_Template == 923 || Item_Template == 924) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(101, 20);
                itemReceived.addOptionParam(210, 4);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1456) {
                itemReceived.addOptionParam(50, 15);
                itemReceived.addOptionParam(77, 15);
                itemReceived.addOptionParam(103, 15);
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(81, 20);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1455) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(77, 10);
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 879) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(81, 20);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1371 || Item_Template == 1380 || Item_Template == 1381) {
                itemReceived.addOptionParam(50, 24);
                itemReceived.addOptionParam(77, 24);
                itemReceived.addOptionParam(103, 24);
                itemReceived.addOptionParam(104, 20);
                itemReceived.addOptionParam(101, 20);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 954) {
                itemReceived.addOptionParam(50, 15);
                itemReceived.addOptionParam(77, 15);
                itemReceived.addOptionParam(103, 15);
                itemReceived.addOptionParam(94, 12);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 955) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(103, 18);
                itemReceived.addOptionParam(94, 15);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 1150 && Item_Template <= 1154) {
                itemReceived.addOptionParam(86, 0);
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openNguoiTuyet(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {381, 382, 383, 384, 385, 837, 838, 839, 840, 841, 842, 630, 631, 632, 1254, 1103, 937, 1207, 1443};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1443) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(77, 10);
                itemReceived.addOptionParam(80, 20);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1207) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 381 && Item_Template <= 385) {
                itemReceived.addOptionParam(86, 0);
            }
            if (Item_Template >= 837 && Item_Template <= 842) {
                itemReceived.addOptionParam(87, 0);
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template >= 630 && Item_Template <= 632) {
                List<ItemOption> ops = ItemService.gI().getListOptionItemShop((short) Item_Template);
                if (!ops.isEmpty()) {
                    itemReceived.itemOptions = ops;
                }
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1254) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1103) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 937) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 24));
                itemReceived.addOptionParam(77, Util.nextInt(20, 24));
                itemReceived.addOptionParam(103, Util.nextInt(20, 24));
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openHopQuaCadic(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1822, 1823, 1824, 1825, 1826, 1827, 17, 1143};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1823) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(210, 1);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1824) {
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(103, 22);
                itemReceived.addOptionParam(210, 2);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1822) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(103, 18);
                itemReceived.addOptionParam(210, 1);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1827) {
                itemReceived.addOptionParam(50, 27);
                itemReceived.addOptionParam(77, 27);
                itemReceived.addOptionParam(103, 27);
                itemReceived.addOptionParam(210, 4);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1826) {
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(210, 4);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1825) {
                itemReceived.addOptionParam(50, 23);
                itemReceived.addOptionParam(77, 23);
                itemReceived.addOptionParam(103, 23);
                itemReceived.addOptionParam(210, 3);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1143) {
                itemReceived.addOptionParam(30, 0);
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openHopQuaCadicVip(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1822, 1823, 1824, 1825, 1826, 1827, 17, 1143};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1823) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(210, 2);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1824) {
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(103, 22);
                itemReceived.addOptionParam(210, Util.nextInt(2, 3));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1822) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(103, 18);
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1827) {
                itemReceived.addOptionParam(50, 27);
                itemReceived.addOptionParam(77, 27);
                itemReceived.addOptionParam(103, 27);
                itemReceived.addOptionParam(210, Util.nextInt(4, 5));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1826) {
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(210, Util.nextInt(4, 5));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1825) {
                itemReceived.addOptionParam(50, 23);
                itemReceived.addOptionParam(77, 23);
                itemReceived.addOptionParam(103, 23);
                itemReceived.addOptionParam(210, Util.nextInt(3, 4));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1143) {
                itemReceived.addOptionParam(30, 0);
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(1000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }
   public void openTuiVang(Player pl, Item item) {
    if (pl.nPoint.power < 70000000000L) {
        Service.gI().sendThongBao(pl, "Yêu cầu sức mạnh  70 tỷ mới có thể mở");
        return;
    }

    if (InventoryService.gI().getCountEmptyBag(pl) <= 0) {
        Service.gI().sendThongBao(pl, "Hành trang đã đầy, cần một ô trống trong hành trang");
        return;
    }

    Item itemReceived = ItemService.gI().createNewItem((short) 457, 500);
    itemReceived.itemOptions.add(new ItemOption(30, 0));

    short[] icon = new short[2];
    icon[0] = item.template.iconID;
    icon[1] = itemReceived.template.iconID;

    InventoryService.gI().addItemBag(pl, itemReceived);
    InventoryService.gI().subQuantityItemsBag(pl, item, 1);
    InventoryService.gI().sendItemBag(pl);
    CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);

    new Thread(() -> {
        Functions.sleep(1000);
        Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name + " x500");
    }).start();
}
    public void UseItemThuoc(Player player, Item item) {
        switch (item.template.id) {
            case 2063:
                PlayerService.gI().hoiPhuc(player, 1, (player.nPoint.mpMax * 20) / 100);
                break;
            case 2064:
                PlayerService.gI().hoiPhuc(player, 1, (player.nPoint.mpMax * 50) / 100);
                break;
            case 2065:
                PlayerService.gI().hoiPhuc(player, 1, (player.nPoint.mpMax * 80) / 100);
                break;
            case 2066:
                PlayerService.gI().hoiPhuc(player, (player.nPoint.hpMax * 20) / 100, 1);
                break;
            case 2067:
                PlayerService.gI().hoiPhuc(player, (player.nPoint.hpMax * 20) / 100, 1);
                break;
            case 2068:
                PlayerService.gI().hoiPhuc(player, (player.nPoint.hpMax * 20) / 100, 1);
                break;
        }
        Service.gI().point(player);
        Service.getInstance().Send_Info_NV(player);
        PlayerService.gI().sendInfoHpMp(player);
        InventoryService.gI().subQuantityItemsBag(player, item, 1);
        InventoryService.gI().sendItemBag(player);
    }

    public void UseHoiSkills(Player player, Item item) {
        if (Util.canDoWithTime(player.lastTimeHoiskill, 15_000)) {
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            InventoryService.gI().sendItemBag(player);
            Service.gI().releaseCooldownSkill(player, player.playerSkill.skillSelect);
            Service.gI().sendThongBao(player, "Đã hồi skills đang chọn.");
            player.lastTimeHoiskill = System.currentTimeMillis();
        } else {
            Service.gI().sendThongBao(player, "Vui lòng đợi " + TimeUtil.getTimeLeft(player.lastTimeHoiskill, 15) + " nữa");
        }
    }

    public void openHopQuaGokuDayVIP(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1588, 1589, 1595, 1587, 1593, 1590};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1589) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(210, 2);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1595) {
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(103, 22);
                itemReceived.addOptionParam(210, Util.nextInt(2, 3));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1588) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(103, 18);
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1590) {
                itemReceived.addOptionParam(50, 27);
                itemReceived.addOptionParam(77, 27);
                itemReceived.addOptionParam(103, 27);
                itemReceived.addOptionParam(210, Util.nextInt(4, 5));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1593) {
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(210, Util.nextInt(4, 5));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1587) {
                itemReceived.addOptionParam(50, 23);
                itemReceived.addOptionParam(77, 23);
                itemReceived.addOptionParam(103, 23);
                itemReceived.addOptionParam(210, Util.nextInt(3, 4));
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(1000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openHopQuaGokuDay(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1588, 1589, 1595, 1587, 1593, 1590, 1143, 17};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1589) {
                itemReceived.addOptionParam(50, 21);
                itemReceived.addOptionParam(77, 21);
                itemReceived.addOptionParam(103, 21);
                itemReceived.addOptionParam(210, 1);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1595) {
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(103, 22);
                itemReceived.addOptionParam(210, 2);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1588) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(103, 18);
                itemReceived.addOptionParam(210, 1);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1590) {
                itemReceived.addOptionParam(50, 27);
                itemReceived.addOptionParam(77, 27);
                itemReceived.addOptionParam(103, 27);
                itemReceived.addOptionParam(210, 4);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1593) {
                itemReceived.addOptionParam(50, 25);
                itemReceived.addOptionParam(77, 25);
                itemReceived.addOptionParam(103, 25);
                itemReceived.addOptionParam(210, 4);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1587) {
                itemReceived.addOptionParam(50, 23);
                itemReceived.addOptionParam(77, 23);
                itemReceived.addOptionParam(103, 23);
                itemReceived.addOptionParam(210, 3);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1143) {
                itemReceived.addOptionParam(30, 0);
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openPhaoHoaThuong(Player pl, Item item) {
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        Service.gI().addSMTN(pl, (byte) 2, Util.nextInt(30_000, 100_000), true);
        new Thread(() -> {
            Functions.sleep(1000);
            pl.DuaTopBanPhaoHoa++;
        }).start();
    }
    public void showBossMember(Player player, Item item) {
    if (player == null) {
        return;
    }
    BossManager.gI().showListBossMember(player);
}


    public void openPhaoHoaThuongVIP(Player pl, Item item) {
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBag(pl);
        Service.gI().addSMTN(pl, (byte) 2, Util.nextInt(100_000, 700_000), true);
        new Thread(() -> {
            Functions.sleep(1000);
            pl.DuaTopBanPhaoHoaVIP++;
        }).start();
    }

    public void openLiXiVang(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1477, 1483, 1484, 1485, 1486, 1478, 1479};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template >= 1484 && Item_Template <= 1486) {
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(80, Util.nextInt(10, 20));
                } else {
                    itemReceived.addOptionParam(81, Util.nextInt(10, 20));
                }
                itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                itemReceived.addOptionParam(101, 25);
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(96, 20);
                } else {
                    itemReceived.addOptionParam(95, 20);
                }
                itemReceived.addOptionParam(210, 1);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1483) {
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                if (Util.isTrue(50, 100)) {
                    itemReceived.addOptionParam(80, Util.nextInt(10, 20));
                } else {
                    itemReceived.addOptionParam(81, Util.nextInt(10, 20));
                }
                itemReceived.addOptionParam(114, 50);
                itemReceived.addOptionParam(117, Util.nextInt(5, 10));
                itemReceived.addOptionParam(210, 1);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1478 || Item_Template == 1479) {
                itemReceived.addOptionParam(50, Util.nextInt(14, 16));
                itemReceived.addOptionParam(77, Util.nextInt(14, 16));
                itemReceived.addOptionParam(103, Util.nextInt(14, 16));
                itemReceived.addOptionParam(94, Util.nextInt(7, 12));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1477) {
                itemReceived.addOptionParam(50, Util.nextInt(5, 12));
                itemReceived.addOptionParam(77, Util.nextInt(5, 12));
                itemReceived.addOptionParam(103, Util.nextInt(5, 12));
                itemReceived.addOptionParam(84, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openLiXiXanh(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1477, 1476, 1469, 1470, 1471, 1478, 1479};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template >= 1469 && Item_Template <= 1471) {
                itemReceived.addOptionParam(77, Util.nextInt(24, 27));
                itemReceived.addOptionParam(103, Util.nextInt(24, 27));
                itemReceived.addOptionParam(50, Util.nextInt(24, 27));
                itemReceived.addOptionParam(101, 25);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1476) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                itemReceived.addOptionParam(114, 50);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1478 || Item_Template == 1479) {
                itemReceived.addOptionParam(50, Util.nextInt(14, 16));
                itemReceived.addOptionParam(77, Util.nextInt(14, 16));
                itemReceived.addOptionParam(103, Util.nextInt(14, 16));
                itemReceived.addOptionParam(94, Util.nextInt(7, 12));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1477) {
                itemReceived.addOptionParam(50, Util.nextInt(5, 12));
                itemReceived.addOptionParam(77, Util.nextInt(5, 12));
                itemReceived.addOptionParam(103, Util.nextInt(5, 12));
                itemReceived.addOptionParam(84, 0);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openLiXiVIP(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {837, 838, 839, 840, 841, 842, 859, 956, 1074, 1075, 1076, 1079, 1080, 1081, 1204, 1848};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template >= 1074 && Item_Template <= 1083) {
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 1204) {
                itemReceived.addOptionParam(87, 0);
                itemReceived.addOptionParam(30, 0);
            }
            if ((Item_Template >= 837 && Item_Template <= 842) || Item_Template == 859 || Item_Template == 956) {
                itemReceived.addOptionParam(87, 0);
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 1848) {
                itemReceived.addOptionParam(50, Util.nextInt(5, 12));
                itemReceived.addOptionParam(103, Util.nextInt(5, 12));
                itemReceived.addOptionParam(77, Util.nextInt(5, 12));
                itemReceived.addOptionParam(94, Util.nextInt(5, 10));
                itemReceived.addOptionParam(84, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(1000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
                pl.DuaTopMoLiXi++;
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openThiepChucMung(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1478, 1185, 1186, 1087, 1088, 1089, 1090, 1091, 457};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template >= 1087 && Item_Template <= 1091) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(94, Util.nextInt(10, 20));
                itemReceived.addOptionParam(108, Util.nextInt(10, 15));
                itemReceived.addOptionParam(114, 20);
                itemReceived.addOptionParam(205, 2);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 457) {
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 1185 || Item_Template == 1186) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 16));
                itemReceived.addOptionParam(77, Util.nextInt(10, 16));
                itemReceived.addOptionParam(103, Util.nextInt(10, 16));
                itemReceived.addOptionParam(94, Util.nextInt(7, 12));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1478) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 17));
                itemReceived.addOptionParam(77, Util.nextInt(15, 17));
                itemReceived.addOptionParam(103, Util.nextInt(15, 17));
                itemReceived.addOptionParam(94, Util.nextInt(5, 10));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openPhongBiTet(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30};
        short[] List_Item = {1023, 1477, 1207, 945, 920, 459, 213, 214, 215, 216, 217, 218, 219};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template >= 213 && Item_Template <= 219) {
                pl.charms.addTimeCharms(Item_Template, 60);
            }
            if (Item_Template == 459) {
                itemReceived.addOptionParam(112, 80);
                itemReceived.addOptionParam(93, 90);
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 920) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(114, 25);
                itemReceived.addOptionParam(50, Util.nextInt(5, 10));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 945) {
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(94, Util.nextInt(10, 20));
                itemReceived.addOptionParam(14, Util.nextInt(10, 15));
                itemReceived.addOptionParam(80, Util.nextInt(10, 20));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1207) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 17));
                itemReceived.addOptionParam(77, Util.nextInt(10, 17));
                itemReceived.addOptionParam(103, Util.nextInt(10, 17));
                itemReceived.addOptionParam(94, Util.nextInt(10, 17));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1477) {
                itemReceived.addOptionParam(50, Util.nextInt(5, 12));
                itemReceived.addOptionParam(77, Util.nextInt(5, 12));
                itemReceived.addOptionParam(103, Util.nextInt(5, 12));
                itemReceived.addOptionParam(84, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1023) {
                itemReceived.addOptionParam(50, Util.nextInt(11, 18));
                itemReceived.addOptionParam(77, Util.nextInt(10, 16));
                itemReceived.addOptionParam(103, Util.nextInt(10, 16));
                itemReceived.addOptionParam(14, Util.nextInt(10, 12));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openRuongSaoPhaLe(Player pl, Item item) {
        short[] List_Item = {1416, 1417, 1418, 1419, 1420, 1421, 1422};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1416) {
                itemReceived.addOptionParam(95, 5);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1417) {
                itemReceived.addOptionParam(96, 5);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1418) {
                itemReceived.addOptionParam(97, 5);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1419) {
                itemReceived.addOptionParam(98, 3);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1420) {
                itemReceived.addOptionParam(99, 3);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1421) {
                itemReceived.addOptionParam(100, 5);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            if (Item_Template == 1422) {
                itemReceived.addOptionParam(101, 5);
                itemReceived.addOptionParam(30, 0);
                itemReceived.addOptionParam(87, 0);
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openRuongSaoPhaLethuong(Player pl, Item item) {
        // Danh sách ID sao pha lê (đủ 7 loại, bạn muốn 6 thì bỏ 1)
        short[] LIST_SAO_PHA_LE = {441, 442, 443, 444, 445, 446, 447};

        if (InventoryService.gI().getCountEmptyBag(pl) >= LIST_SAO_PHA_LE.length) {
            // Lặp qua từng loại sao trong danh sách -> đảm bảo khác loại
            for (short itemId : LIST_SAO_PHA_LE) {
                Item itemReceived = ItemService.gI().createNewItem(itemId, 1);

                // Gán option theo loại sao
                switch (itemId) {
                    case 441: // đỏ
                        itemReceived.addOptionParam(95, 5);
                        break;
                    case 442: // lam
                        itemReceived.addOptionParam(96, 5);
                        break;
                    case 443: // hồng
                        itemReceived.addOptionParam(97, 5);
                        break;
                    case 444: // tím
                        itemReceived.addOptionParam(98, 3);
                        break;
                    case 445: // cam
                        itemReceived.addOptionParam(99, 3);
                        break;
                    case 446: // vàng
                        itemReceived.addOptionParam(100, 5);
                        break;
                    case 447: // lục
                        itemReceived.addOptionParam(101, 5);
                        break;
                }

                // Thêm vào túi
                InventoryService.gI().addItemBag(pl, itemReceived);
            }

            // Trừ rương sau khi mở
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            // Hiệu ứng mở rương
            short[] icon = new short[2];
            icon[0] = item.template.iconID; // icon rương
            icon[1] = 441; // ví dụ icon sao đỏ
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);

            Service.gI().sendThongBao(pl, "Bạn nhận được đủ 7 viên sao pha lê khác loại!");
        } else {
            Service.gI().sendThongBao(pl, "Cần ít nhất " + LIST_SAO_PHA_LE.length + " ô trống trong hành trang!");
        }
    }

    public void openManhThienSu(Player pl, Item item) {
        Item itemReceived = ItemService.gI().createNewItem((short) Util.nextInt(1066, 1070));
        int Quantity = Util.nextInt(1, 5);
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            itemReceived.addOptionParam(86, 0);
            itemReceived.quantity = Quantity;
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openHopQuaTet(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15};
        short[] List_Item = {227, 228, 229, 1142, 1144, 381, 382, 383, 384, 1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083, 933, 1173, 459, 1482, 1483};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1482) {
                itemReceived.addOptionParam(50, Util.nextInt(20, 25));
                itemReceived.addOptionParam(77, Util.nextInt(20, 25));
                itemReceived.addOptionParam(103, Util.nextInt(20, 25));
                itemReceived.addOptionParam(94, Util.nextInt(10, 15));
                itemReceived.addOptionParam(97, Util.nextInt(10, 15));
                itemReceived.addOptionParam(114, Util.nextInt(20, 50));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1482) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                itemReceived.addOptionParam(103, Util.nextInt(10, 20));
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 227 && Item_Template <= 229) {
                itemReceived.addOptionParam(77, Util.nextInt(10, 30));
                itemReceived.addOptionParam(97, Util.nextInt(10, 20));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1142) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                itemReceived.addOptionParam(103, Util.nextInt(10, 20));
                itemReceived.addOptionParam(210, Util.nextInt(1, 2));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 381 && Item_Template <= 384) {
                itemReceived.addOptionParam(86, 0);
            }
            if (Item_Template >= 1074 && Item_Template <= 1083) {
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 1144) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(210, Util.nextInt(1, 5));
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 933) {
                itemReceived.addOptionParam(31, Util.nextInt(1, 10));
            }
            if (Item_Template == 1173) {
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 459) {
                itemReceived.addOptionParam(112, 80);
                itemReceived.addOptionParam(93, 90);
                itemReceived.addOptionParam(30, 0);
            }
            if (Util.isTrue(100, 100)) {
                new Thread(() -> {
                    Functions.sleep(3000);
                    pl.event.addLunaNewYearPoint(1);
                    Service.gI().sendThongBao(pl, "Bạn nhận được 1 điểm sự kiện");
                }).start();
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openHopQuaDacBiet(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15};
        short[] List_Item = {1185, 1186, 1143, 1074, 1075, 1076, 1077, 1078, 1079, 1080, 1081, 1082, 1083, 1021, 1022, 1318};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1143 || (Item_Template >= 1074 && Item_Template <= 1083)) {
                itemReceived.addOptionParam(30, 0);
            }
            if (Item_Template == 1318) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 17));
                itemReceived.addOptionParam(77, Util.nextInt(10, 17));
                itemReceived.addOptionParam(103, Util.nextInt(10, 17));
                itemReceived.addOptionParam(94, 12);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1021 || Item_Template == 1022) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 17));
                itemReceived.addOptionParam(77, Util.nextInt(10, 17));
                itemReceived.addOptionParam(103, Util.nextInt(10, 17));
                itemReceived.addOptionParam(210, 1);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1185 || Item_Template == 1186) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                itemReceived.addOptionParam(103, Util.nextInt(10, 20));
                itemReceived.addOptionParam(210, 2);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Util.isTrue(100, 100)) {
                new Thread(() -> {
                    Functions.sleep(3000);
                    pl.event.addLunaNewYearPoint(1);
                    Service.gI().sendThongBao(pl, "Bạn nhận được 1 điểm sự kiện");
                }).start();
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openTui7ChuLun(Player pl, Item item) {
        short[] Option = {94, 97, 108};
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            Item ChuLun = ItemService.gI().createNewItem((short) Util.nextInt(1158, 1164));
            ChuLun.itemOptions.add(new ItemOption(50, 15));
            ChuLun.itemOptions.add(new ItemOption(77, 15));
            ChuLun.itemOptions.add(new ItemOption(103, 15));
            ChuLun.itemOptions.add(new ItemOption(Option[Util.nextInt(0, Option.length - 1)], Util.nextInt(5, 15)));
            if (Util.isTrue(80, 100)) {
                ChuLun.itemOptions.add(new ItemOption(93, Util.nextInt(1, 7)));
            }
            icon[1] = ChuLun.template.iconID;
            InventoryService.gI().addItemBag(pl, ChuLun);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openCapsuneTet(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15};
        short[] List_Item = {849, 852, 846, 847, 848, 941, 946, 947, 1198, 1199, 1200, 948, 952, 953, 942, 943, 944, 1757, 1757, 1757, 1757};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1757) {
                itemReceived.addOptionParam(50, Util.nextInt(8, 17));
                itemReceived.addOptionParam(77, Util.nextInt(8, 17));
                itemReceived.addOptionParam(103, Util.nextInt(8, 17));
                itemReceived.addOptionParam(101, 25);
                if (Util.isTrue(75, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 849) {
                itemReceived.addOptionParam(84, 0);
                itemReceived.addOptionParam(210, 3);
                if (Util.isTrue(75, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 852) {
                itemReceived.addOptionParam(50, Util.nextInt(12, 17));
                itemReceived.addOptionParam(77, Util.nextInt(12, 17));
                itemReceived.addOptionParam(103, Util.nextInt(12, 17));
                itemReceived.addOptionParam(210, 1);
                if (Util.isTrue(75, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 846 && Item_Template <= 848) {
                itemReceived.addOptionParam(77, 30);
                itemReceived.addOptionParam(80, 50);
                itemReceived.addOptionParam(50, 20);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 941 || Item_Template == 946 || Item_Template == 947) {
                itemReceived.addOptionParam(50, Util.nextInt(15, 25));
                itemReceived.addOptionParam(77, Util.nextInt(20, 30));
                itemReceived.addOptionParam(80, 50);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 1198 && Item_Template <= 1200) {
                itemReceived.addOptionParam(77, 30);
                itemReceived.addOptionParam(80, 50);
                itemReceived.addOptionParam(50, 20);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 948 || Item_Template == 952 || Item_Template == 953) {
                itemReceived.addOptionParam(77, 30);
                itemReceived.addOptionParam(80, 50);
                itemReceived.addOptionParam(50, 20);
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template >= 942 && Item_Template <= 944) {
                itemReceived.addOptionParam(50, Util.nextInt(10, 20));
                itemReceived.addOptionParam(77, Util.nextInt(10, 20));
                itemReceived.addOptionParam(103, Util.nextInt(10, 20));
                itemReceived.addOptionParam(14, Util.nextInt(5, 15));
                if (Util.isTrue(90, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Util.isTrue(100, 100)) {
                new Thread(() -> {
                    Functions.sleep(3000);
                    pl.event.addLunaNewYearPoint(1);
                    Service.gI().sendThongBao(pl, "Bạn nhận được 1 điểm sự kiện");
                }).start();
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openTrungRongNhi(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30, 45};
        short[] List_Item = {1872, 1873, 1874, 1875, 1876, 1877, 1878};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1872) {
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(94, 8);
                itemReceived.addOptionParam(5, 11);
                itemReceived.addOptionParam(14, 8);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1873) {
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(94, 8);
                itemReceived.addOptionParam(5, 11);
                itemReceived.addOptionParam(14, 8);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1874) {
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(94, 5);
                itemReceived.addOptionParam(108, 7);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1875) {
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(5, 7);
                itemReceived.addOptionParam(108, 5);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1876) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(94, 5);
                itemReceived.addOptionParam(108, 7);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1877) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(14, 5);
                itemReceived.addOptionParam(5, 7);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1878) {
                itemReceived.addOptionParam(77, 16);
                itemReceived.addOptionParam(50, 16);
                itemReceived.addOptionParam(103, 16);
                itemReceived.addOptionParam(94, 8);
                if (Util.isTrue(99, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void openTrungRongNhiVIP(Player pl, Item item) {
        short[] Param = {3, 5, 7, 15, 30, 45};
        short[] List_Item = {1872, 1873, 1874, 1875, 1876, 1877, 1878};
        Item itemReceived = ItemService.gI().createNewItem(List_Item[Util.nextInt(0, List_Item.length - 1)]);
        int Item_Template = itemReceived.template.id;
        short[] icon = new short[2];
        icon[0] = item.template.iconID;
        icon[1] = itemReceived.template.iconID;
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            if (Item_Template == 1872) {
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(94, 8);
                itemReceived.addOptionParam(5, 11);
                itemReceived.addOptionParam(14, 8);
                itemReceived.addOptionParam(106, 0);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1873) {
                itemReceived.addOptionParam(77, 22);
                itemReceived.addOptionParam(50, 22);
                itemReceived.addOptionParam(94, 8);
                itemReceived.addOptionParam(5, 11);
                itemReceived.addOptionParam(14, 8);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1874) {
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(94, 5);
                itemReceived.addOptionParam(108, 7);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1875) {
                itemReceived.addOptionParam(77, 18);
                itemReceived.addOptionParam(5, 7);
                itemReceived.addOptionParam(108, 5);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1876) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(94, 5);
                itemReceived.addOptionParam(108, 7);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1877) {
                itemReceived.addOptionParam(50, 18);
                itemReceived.addOptionParam(14, 5);
                itemReceived.addOptionParam(5, 7);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            if (Item_Template == 1878) {
                itemReceived.addOptionParam(77, 16);
                itemReceived.addOptionParam(50, 16);
                itemReceived.addOptionParam(103, 16);
                itemReceived.addOptionParam(94, 8);
                if (Util.isTrue(95, 100)) {
                    itemReceived.itemOptions.add(new ItemOption(93, Param[Util.nextInt(0, Param.length - 1)]));
                }
            }
            InventoryService.gI().addItemBag(pl, itemReceived);
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
            CombineService.gI().sendEffectOpenItem(pl, icon[0], icon[1]);
            pl.DuaTopMoTrungRongVang++;
            new Thread(() -> {
                Functions.sleep(2000);
                Service.gI().sendThongBao(pl, "Bạn nhận được " + itemReceived.template.name);
            }).start();
        } else {
            Service.gI().sendThongBao(pl, "Hàng trang đã đầy, cần một ô trống trong hành trang");
        }
    }

    public void usePorata(Player pl, Item item) {
        class FusionRule {

            int[] bannedTypes;
            Consumer<Detu> fusionMethod;

            FusionRule(int[] bannedTypes, Consumer<Detu> fusionMethod) {
                this.bannedTypes = bannedTypes;
                this.fusionMethod = fusionMethod;
            }
        }
        Map<Short, FusionRule> rules = new HashMap<>();
        rules.put((short) 454, new FusionRule(new int[]{4, 8, 10, 12, 14}, detu -> detu.fusion(true)));
        rules.put((short) 921, new FusionRule(new int[]{4, 6, 10, 12, 14}, detu -> detu.fusion2(true)));
        rules.put((short) 1943, new FusionRule(new int[]{4, 6, 8, 12, 14}, detu -> detu.fusion3(true)));
        rules.put((short) 2105, new FusionRule(new int[]{4, 6, 8, 10, 14}, detu -> detu.fusion4(true)));
        rules.put((short) 2106, new FusionRule(new int[]{4, 6, 8, 10, 12}, detu -> detu.fusion5(true)));
        FusionRule rule = rules.get(item.template.id);
        if (rule == null) {
            return;
        }
        if (pl.Detu == null || Arrays.stream(rule.bannedTypes).anyMatch(t -> t == pl.fusion.typeFusion)) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
            rule.fusionMethod.accept(pl.Detu);
        } else {
            pl.Detu.unFusion();
        }
    }

    public void upSkillDetu(Player pl, Item item) {
        if (pl.Detu == null) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        try {
            switch (item.template.id) {
                case 402:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 0)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 403:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 1)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 404:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 2)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 759:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 3)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 1732:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 4)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
                case 1733:
                    if (SkillUtil.upSkillPet(pl.Detu.playerSkill.skills, 5)) {
                        Service.gI().chatJustForMe(pl, pl.Detu, "Cảm ơn sư phụ");
                        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
                    } else {
                        Service.gI().sendThongBao(pl, "Không thể thực hiện");
                    }
                    break;
            }
        } catch (Exception e) {
            Service.gI().sendThongBao(pl, "Không thể thực hiện");
        }
    }

    public void hopCaiTrang(Player pl, Item item) {
        // Kiểm tra null, vật phẩm và hành trang trống
        if (pl == null || item == null) {
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(pl) == 0) {
            Service.gI().sendThongBao(pl, "Hành trang đã đầy, cần ít nhất 1 ô trống!");
            return;
        }

        try {
            // ✅ Tạo item mới (cài trang)
            Item caiTrang = ItemService.gI().createNewItem((short) 1813, 1); // ID 1813 là cài trang

            // ✅ Thêm các option mặc định
            caiTrang.itemOptions.add(new ItemOption(77, 30));   // Sức đánh
            caiTrang.itemOptions.add(new ItemOption(103, 30));  // Tấn công chí mạng
            caiTrang.itemOptions.add(new ItemOption(50, 30));   // HP tối đa
            caiTrang.itemOptions.add(new ItemOption(94, 10));   // Giáp
            caiTrang.itemOptions.add(new ItemOption(5, 10));    // Chí mạng
            caiTrang.itemOptions.add(new ItemOption(14, 10));   // Né đòn
            caiTrang.itemOptions.add(new ItemOption(107, 7));   // Không khóa
            caiTrang.itemOptions.add(new ItemOption(93, 7));    // May mắn hoặc chỉ số phụ

            // ✅ Thêm vào hành trang
            InventoryService.gI().addItemBag(pl, caiTrang);
            InventoryService.gI().sendItemBag(pl);

            // ✅ Trừ vật phẩm đã sử dụng (1 cái)
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);

            // ✅ Hiệu ứng mở hộp
            CombineService.gI().sendEffectOpenItem(pl, caiTrang.template.iconID, caiTrang.template.iconID);

            // ✅ Thông báo sau 2 giây (cho cảm giác "mở quà")
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Service.gI().sendThongBao(pl, "🎁 Bạn nhận được " + caiTrang.template.name + "!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            Service.gI().sendThongBao(pl, "⚠️ Lỗi khi mở hộp quà, vui lòng thử lại sau!");
        }
    }

    public void hopQuaTanThu(Player pl, Item it) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 14) {
            int gender = pl.gender;
            int[] id = {gender, 6 + gender, 21 + gender, 27 + gender, 12, 194, 441, 442, 443, 444, 445, 446, 447};
            int[] soluong = {1, 1, 1, 1, 1, 1, 10, 10, 10, 10, 10, 10, 10};
            int[] option = {0, 0, 0, 0, 0, 73, 95, 96, 97, 98, 99, 100, 101};
            int[] param = {0, 0, 0, 0, 0, 0, 5, 5, 5, 3, 3, 5, 5};
            int arrLength = id.length - 1;

            for (int i = 0; i < arrLength; i++) {
                if (i < 5) {
                    Item item = ItemService.gI().createNewItem((short) id[i]);
                    RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                    item.itemOptions.add(new ItemOption(107, 5));
                    InventoryService.gI().addItemBag(pl, item);
                } else {
                    Item item = ItemService.gI().createNewItem((short) id[i]);
                    item.quantity = soluong[i];
                    item.itemOptions.add(new ItemOption(option[i], param[i]));
                    InventoryService.gI().addItemBag(pl, item);
                }
            }

            int[] idpet = {916, 917, 918, 942, 943, 944, 1039, 1040};

            Item item = ItemService.gI().createNewItem((short) idpet[Util.nextInt(0, idpet.length - 1)]);
            item.itemOptions.add(new ItemOption(50, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(77, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(103, Util.nextInt(5, 10)));
            item.itemOptions.add(new ItemOption(93, 3));
            InventoryService.gI().addItemBag(pl, item);

            InventoryService.gI().subQuantityItemsBag(pl, it, 1);
            InventoryService.gI().sendItemBag(pl);
            Service.getInstance().sendThongBao(pl, "Chúc bạn chơi game vui vẻ");
        } else {
            Service.getInstance().sendThongBao(pl, "Cần tối thiểu 14 ô trống để nhận thưởng");
        }
    }

    public void OpenPoke(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            short[] possibleIds = {1992};
            short chosenId = possibleIds[Util.nextInt(0, possibleIds.length - 1)];
            Item it = ItemService.gI().createNewItem(chosenId);
            int rate = Util.nextInt(1, 100);
            int op5Value;

           if (rate <= 65) {              // 65%
                op5Value = Util.nextInt(1, 30);
            } else if (rate <= 85) {       // +20% (tổng 85%)
                op5Value = Util.nextInt(1, 60);
            } else if (rate <= 95) {       // +10% (tổng 95%)
                op5Value = Util.nextInt(1, 90);
            } else if (rate <= 99) {       // +4% (tổng 99%)
                op5Value = Util.nextInt(1, 120);
            } else {                       // +1% (tổng 100%)
                op5Value = Util.nextInt(1, 150);
            }

            it.itemOptions.add(new ItemOption(5, op5Value));

            // --- Các option phụ khác
            it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 30)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 30)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 30)));
            it.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
            it.itemOptions.add(new ItemOption(106, 1));
            it.itemOptions.add(new ItemOption(73, 1));

            // --- Thêm vật phẩm vào hành trang
            InventoryService.gI().addItemBag(pl, it);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + "");

            // --- Giảm vật phẩm dùng
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }
    public void OpenVanbay(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {

            // Random ra 1 trong 2 ID: 1938 hoặc 1939
            short[] possibleIds = {1955, 1956,1957,1958};
            short chosenId = possibleIds[Util.nextInt(0, possibleIds.length - 1)];
            Item it = ItemService.gI().createNewItem(chosenId);


            // --- Các option phụ khác
            it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 10)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 10)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 10)));
            it.itemOptions.add(new ItemOption(204, Util.nextInt(1, 15)));
            
            it.itemOptions.add(new ItemOption(73, 1));

            // --- Thêm vật phẩm vào hành trang
            InventoryService.gI().addItemBag(pl, it);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + "");

            // --- Giảm vật phẩm dùng
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }
    public void OpenHaitac(Player pl, Item item) {
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {

            // Random ra 1 trong 2 ID: 1938 hoặc 1939
            short[] possibleIds = {1992};
            short chosenId = possibleIds[Util.nextInt(0, possibleIds.length - 1)];
            Item it = ItemService.gI().createNewItem(chosenId);

            // --- Luôn có op vĩnh viễn (73)
            // --- Tính random cho op 5 theo tỉ lệ mong muốn
            int rate = Util.nextInt(1, 100);
            int op5Value;

            if (rate <= 65) {              // 65%
                op5Value = Util.nextInt(1, 30);
            } else if (rate <= 85) {       // +20% (tổng 85%)
                op5Value = Util.nextInt(1, 60);
            } else if (rate <= 95) {       // +10% (tổng 95%)
                op5Value = Util.nextInt(1, 90);
            } else if (rate <= 99) {       // +4% (tổng 99%)
                op5Value = Util.nextInt(1, 120);
            } else {                       // +1% (tổng 100%)
                op5Value = Util.nextInt(1, 150);
            }

            it.itemOptions.add(new ItemOption(204, op5Value));

            // --- Các option phụ khác
            it.itemOptions.add(new ItemOption(50, Util.nextInt(1, 20)));
            it.itemOptions.add(new ItemOption(77, Util.nextInt(1, 50)));
            it.itemOptions.add(new ItemOption(103, Util.nextInt(1, 50)));
            it.itemOptions.add(new ItemOption(14, Util.nextInt(1, 10)));
            it.itemOptions.add(new ItemOption(106, 1));
            it.itemOptions.add(new ItemOption(73, 1));

            // --- Thêm vật phẩm vào hành trang
            InventoryService.gI().addItemBag(pl, it);
            Service.getInstance().sendThongBao(pl, "Bạn đã nhận được " + it.template.name + "");

            // --- Giảm vật phẩm dùng
            InventoryService.gI().subQuantityItemsBag(pl, item, 1);
            InventoryService.gI().sendItemBag(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hàng trang đã đầy");
        }
    }

}
