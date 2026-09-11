package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.server.Manager;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import models.Reward.RewardService;
import nro.combine.CombineService;

public class NangCapKichHoat {

    private static final int GOLD_REQUIRE = 2_000_000_000; // 💰 2 tỷ vàng

    public static boolean isDoHuyDiet(Item item) {
        return item != null && item.template.id >= 650 && item.template.id <= 662;
    }

    // ✅ Hiển thị thông tin nâng cấp
    public static void showInfoCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine == null || player.combine.itemsCombine.size() != 1) {
            Service.gI().sendThongBaoOK(player, "Cần 1 món trang bị Huỷ Diệt để nâng cấp!");
            return;
        }

        Item huyDiet = player.combine.itemsCombine.get(0);
        if (!isDoHuyDiet(huyDiet)) {
            Service.gI().sendThongBaoOK(player, "Vật phẩm không hợp lệ, cần Huỷ Diệt!");
            return;
        }

        if (player.inventory.gold < GOLD_REQUIRE) {
            Service.gI().sendThongBaoOK(player, "Cần " + Util.formatNumber(GOLD_REQUIRE, FormatStyle.VIETNAMESE) + " vàng để nâng cấp!");
            return;
        }

        String text = "Cường hoá Huỷ Diệt sẽ tạo thành Kích Hoạt cùng hành tinh\n"
                + "Cần " + Util.formatNumber(GOLD_REQUIRE, FormatStyle.VIETNAMESE) + " vàng\n"
                + "Tỉ lệ thành công: 100%\n"
                + "Nếu thất bại... (không có đâu 😎).";
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                text, "Nâng cấp", "Huỷ");
    }

    // ✅ Tiến hành nâng cấp
    public static void startCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine.size() != 1) {
            Service.gI().sendThongBao(player, "Cần 1 Huỷ Diệt để nâng cấp");
            return;
        }

        Item huyDiet = player.combine.itemsCombine.get(0);
        if (!isDoHuyDiet(huyDiet)) {
            Service.gI().sendThongBao(player, "Đây không phải là trang bị Huỷ Diệt!");
            return;
        }

        if (player.inventory.gold < GOLD_REQUIRE) {
            Service.gI().sendThongBao(player, "Không đủ vàng, thiếu "
                    + Util.formatNumber(GOLD_REQUIRE - player.inventory.gold, FormatStyle.VIETNAMESE));
            return;
        }

        player.inventory.gold -= GOLD_REQUIRE;
        Service.gI().sendMoney(player);

        int gender = player.gender; // ✅ lấy theo hệ của nhân vật, không lấy theo item
        int[] maleOptions = {129, 141, 127, 139, 128, 140};
        int[] femaleOptions = {132, 144, 131, 254, 130, 143};
        int[] otherOptions = {135, 138, 133, 136, 134, 137};
        int[] selectedOptions = (gender == 0) ? maleOptions
                : (gender == 1) ? femaleOptions : otherOptions;

        // ✅ Sinh đồ Kích Hoạt mới theo hệ của nhân vật
        Item newItem = (huyDiet.template.type == 4)
                ? ItemService.gI().createNewItem((short) 12)
                : ItemService.gI().createNewItem(Manager.TrangBiKichHoat[player.gender][huyDiet.template.type]);

        RewardService.gI().initBaseOptionClothes(newItem.template.id, newItem.template.type, newItem.itemOptions);

        // ✅ Random option kích hoạt
        if (Util.isTrue(15, 100)) {
            newItem.itemOptions.add(new ItemOption(selectedOptions[0], 0));
            newItem.itemOptions.add(new ItemOption(selectedOptions[1], 0));
        } else if (Util.isTrue(75, 100)) {
            newItem.itemOptions.add(new ItemOption(selectedOptions[2], 0));
            newItem.itemOptions.add(new ItemOption(selectedOptions[3], 0));
        } else {
            newItem.itemOptions.add(new ItemOption(selectedOptions[4], 0));
            newItem.itemOptions.add(new ItemOption(selectedOptions[5], 0));
        }

        newItem.itemOptions.add(new ItemOption(30, 0));

        // ✅ Cập nhật hành trang
        InventoryService.gI().subQuantityItemsBag(player, huyDiet, 1);
        InventoryService.gI().addItemBag(player, newItem);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().sendEffectSuccessCombine(player);

        Service.gI().sendThongBao(player, "Nâng cấp thành công! Huỷ Diệt đã trở thành Kích Hoạt theo hệ của bạn!");
    }
}