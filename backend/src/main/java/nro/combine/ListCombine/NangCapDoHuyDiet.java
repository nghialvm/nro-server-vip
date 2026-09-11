package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.combine.CombineService;

public class NangCapDoHuyDiet {

    private static final int GOLD_REQUIRE = 2_000_000_000; // 💰 2 tỷ vàng
    private static final int SUCCESS_RATE = 50; // 🔥 80% thành công

    public static boolean isDoThanLinh(Item item) {
        return item != null && item.template.id >= 555 && item.template.id <= 567;
    }

    // ✅ Hiển thị thông tin nâng cấp
    public static void showInfoCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine.size() != 1) {
            Service.gI().sendThongBaoOK(player, "Cần 1 món Thần Linh để nâng cấp!");
            return;
        }

        Item thanLinh = player.combine.itemsCombine.get(0);
        if (!isDoThanLinh(thanLinh)) {
            Service.gI().sendThongBaoOK(player, "Vật phẩm không hợp lệ, cần Thần Linh!");
            return;
        }

        if (player.inventory.gold < GOLD_REQUIRE) {
            Service.gI().sendThongBaoOK(player, "Cần " + Util.formatNumber(GOLD_REQUIRE, FormatStyle.VIETNAMESE) + " vàng để nâng cấp!");
            return;
        }

        String info = "Sử dụng 1 món Thần Linh và " + Util.formatNumber(GOLD_REQUIRE, FormatStyle.VIETNAMESE) + " vàng\n"
                + "để rèn thành Huỷ Diệt cùng loại và hành tinh.\n"
                + "Tỉ lệ thành công: " + SUCCESS_RATE + "%.\n"
                + "Thất bại chỉ trừ vàng, không mất đồ!";
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                info, "Nâng cấp", "Huỷ");
    }

    // ✅ Bắt đầu ghép
    public static void startCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine.size() != 1) {
            Service.gI().sendThongBao(player, "Cần 1 món Thần Linh để nâng cấp");
            return;
        }

        Item thanLinh = player.combine.itemsCombine.get(0);
        if (!isDoThanLinh(thanLinh)) {
            Service.gI().sendThongBao(player, "Không phải trang bị Thần Linh!");
            return;
        }

        if (player.inventory.gold < GOLD_REQUIRE) {
            Service.gI().sendThongBao(player, "Không đủ vàng, thiếu "
                    + Util.formatNumber(GOLD_REQUIRE - player.inventory.gold, FormatStyle.VIETNAMESE));
            return;
        }

        player.inventory.gold -= GOLD_REQUIRE;
        Service.gI().sendMoney(player);

        if (Util.isTrue(SUCCESS_RATE, 100)) {
            // ✅ Thành công → tạo đồ Huỷ Diệt cùng loại
            short idNew = (short) (650 + (thanLinh.template.id - 555)); // Map Thần Linh → Huỷ Diệt
            Item huyDiet = ItemService.gI().otphd(idNew, 1);

            // Option đặc biệt
            huyDiet.itemOptions.add(new ItemOption(30, 1));  // tăng sức mạnh

            InventoryService.gI().subQuantityItemsBag(player, thanLinh, 1);
            InventoryService.gI().addItemBag(player, huyDiet);
            InventoryService.gI().sendItemBag(player);
            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Nâng cấp thành công! Bạn nhận được " + huyDiet.template.name);
        } else {
            // ❌ Thất bại chỉ trừ vàng, không mất đồ
            CombineService.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Nâng cấp thất bại! Mất "
                    + Util.formatNumber(GOLD_REQUIRE, FormatStyle.VIETNAMESE) + " vàng.");
        }

        CombineService.gI().reOpenItemCombine(player);
    }
}
