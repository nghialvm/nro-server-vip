package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import models.Item.ActivationSetCatalog;
import nro.combine.CombineService;

public class NangCapKichHoatVip {

    private static final int COST = 500_000_000;

    private static boolean isVipLastItem(int tempId) {
        int[] vipIds = {
            555,557,559,
            556,558,560,
            562,564,566,
            563,565,567,
            561
        };
        for (int id : vipIds) {
            if (id == tempId) return true;
        }
        return false;
    }

    static Item createVipActivationItem(Player player, ActivationSetCatalog.SetDefinition definition, int itemType) {
        if (player == null || definition == null) {
            return null;
        }

        int tempId = ItemService.gI().randTempItemKichHoat_VIP(player.gender, itemType);
        if (tempId < 0) {
            return null;
        }
        Item item = ItemService.gI().itemSKH(tempId, definition);
        if (item == null) {
            return null;
        }

        if (isVipLastItem(item.template.id)) {
            int value;
            switch (item.template.type) {
                case 0:
                    value = Util.nextInt(1200, 1400);
                    item.itemOptions.add(0, new ItemOption(47, value));
                    break;
                case 1:
                    value = Util.nextInt(48000, 55000);
                    item.itemOptions.add(0, new ItemOption(6, value));
                    break;
                case 2:
                    value = Util.nextInt(3500, 4000);
                    item.itemOptions.add(0, new ItemOption(0, value));
                    break;
                case 3:
                    value = Util.nextInt(48000, 55000);
                    item.itemOptions.add(0, new ItemOption(7, value));
                    break;
                case 4:
                    value = Util.nextInt(13, 15);
                    item.itemOptions.add(0, new ItemOption(14, value));
                    break;
            }
        }
        return item;
    }
    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.isEmpty()) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Hãy đưa ta 3 món huỷ diệt....", "Đóng");
            return;
        }

        if (player.combine.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Cần đúng 3 món huỷ diệt", "Đóng");
            return;
        }

        if (player.combine.itemsCombine.stream()
                .filter(item -> item.isNotNullItem() && item.isDHD1())
                .count() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Thiếu đồ huỷ diệt rồi", "Đóng");
            return;
        }

        if (player.inventory.gold < COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Hết tiền rồi\nẢo ít thôi con", "Đóng");
            return;
        }

        String npcSay =
                "|2|Con có muốn đổi các món nguyên liệu ?\n|7|" +
                "Nhận trang bị kích hoạt VIP\n" +
                "|1|Cần " + Util.numberToMoney(COST) + " vàng";

        CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.MENU_START_COMBINE,
                npcSay,
                "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng",
                "Từ chối"
        );
    }
    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) return;

        Item i1 = player.combine.itemsCombine.get(0);
        Item i2 = player.combine.itemsCombine.get(1);
        Item i3 = player.combine.itemsCombine.get(2);

        if (!(i1.isDHD1() && i2.isDHD1() && i3.isDHD1())) {
            Service.gI().sendThongBao(player, "Cần 3 món huỷ diệt cùng loại");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Cần ít nhất 1 ô trống hành trang");
            return;
        }

        if (player.inventory.gold < COST) {
            Service.gI().sendThongBao(player, "Không đủ vàng");
            return;
        }
        player.inventory.gold -= COST;
        boolean success = Util.isTrue(70, 100);
        if (!success) {
           
            CombineService.gI().sendEffectFailCombine(player);
        

            InventoryService.gI().subQuantityItemsBag(player, i1, 1);
            InventoryService.gI().subQuantityItemsBag(player, i2, 1);
            InventoryService.gI().subQuantityItemsBag(player, i3, 1);

            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Thất bại");
           
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
            return;
        }
        ActivationSetCatalog.SetDefinition definition = ActivationSetCatalog.randomUpgradeSet(player.gender);
        Item item = createVipActivationItem(player, definition, i1.template.type);
        if (item == null) {
            player.inventory.gold += COST;
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Không tạo được trang bị, vui lòng thử lại");
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
            return;
        }

        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().addItemBag(player, item);

        InventoryService.gI().subQuantityItemsBag(player, i1, 1);
        InventoryService.gI().subQuantityItemsBag(player, i2, 1);
        InventoryService.gI().subQuantityItemsBag(player, i3, 1);

        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);

        Service.gI().sendThongBao(player,
                "Bạn nhận được " + item.template.name + " kích hoạt VIP");

        player.combine.itemsCombine.clear();
        CombineService.gI().reOpenItemCombine(player);
    }
}
