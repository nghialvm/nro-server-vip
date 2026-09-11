package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.combine.CombineService;

public class NhapDa {

    private static final int FRAG_ID = 225; // 🪨 Mảnh Đá vụn
    private static final int MAGIC_WATER_ID = 226; // 💧 Bình nước phép
    private static final int FRAG_REQUIRE = 10; // 🔟 Cần 10 mảnh đá vụn
    private static final int SUCCESS_RATE = 100; // ✅ 100% thành công

    // Danh sách ID các đá có thể tạo ra
    private static final short[] STONE_IDS = {
        220, // Đá Lục bảo
        221, // Đá Saphia
        222, // Đá Ruby
        223, // Đá Titan
        224  // Đá Thạch anh tím
    };

    // ✅ Hiển thị thông tin ghép đá
    public static void showInfoCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine.isEmpty()) {
            Service.gI().sendThongBaoOK(player, "Hãy đặt vào 10 mảnh Đá vụn và 1 Bình nước phép để ghép!");
            return;
        }

        Item frag = null;
        Item water = null;
        for (Item i : player.combine.itemsCombine) {
            if (i.template.id == FRAG_ID) frag = i;
            else if (i.template.id == MAGIC_WATER_ID) water = i;
        }

        if (frag == null || water == null) {
            Service.gI().sendThongBaoOK(player, "Thiếu nguyên liệu: cần 10 mảnh Đá vụn và 1 Bình nước phép!");
            return;
        }

        if (frag.quantity < FRAG_REQUIRE) {
            Service.gI().sendThongBaoOK(player, "Cần ít nhất " + FRAG_REQUIRE + " mảnh Đá vụn để ghép!");
            return;
        }

        String info = "Ghép " + FRAG_REQUIRE + " mảnh Đá vụn + 1 Bình nước phép\n"
                + "→ nhận ngẫu nhiên 1 trong 5 loại Đá quý:\n"
                + "• Đá Lục bảo\n• Đá Saphia\n• Đá Ruby\n• Đá Titan\n• Đá Thạch anh tím\n"
                + "Tỉ lệ thành công: " + SUCCESS_RATE + "%.";
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                info, "Ghép", "Huỷ");
    }

    // ✅ Bắt đầu ghép
    public static void startCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine.isEmpty()) {
            Service.gI().sendThongBao(player, "Chưa đặt nguyên liệu!");
            return;
        }

        Item frag = null;
        Item water = null;
        for (Item i : player.combine.itemsCombine) {
            if (i.template.id == FRAG_ID) frag = i;
            else if (i.template.id == MAGIC_WATER_ID) water = i;
        }

        if (frag == null || water == null) {
            Service.gI().sendThongBao(player, "Thiếu nguyên liệu: cần 10 mảnh Đá vụn và 1 Bình nước phép!");
            return;
        }

        if (frag.quantity < FRAG_REQUIRE) {
            Service.gI().sendThongBao(player, "Cần ít nhất " + FRAG_REQUIRE + " mảnh Đá vụn!");
            return;
        }

        // ✅ Random 1 trong 5 loại đá quý
        if (Util.isTrue(SUCCESS_RATE, 100)) {
            short randStone = STONE_IDS[Util.nextInt(0, STONE_IDS.length - 1)];

            // Trừ nguyên liệu
            InventoryService.gI().subQuantityItemsBag(player, frag, FRAG_REQUIRE);
            InventoryService.gI().subQuantityItemsBag(player, water, 1);
            InventoryService.gI().sendItemBag(player);

            // Tạo đá mới
            Item newStone = ItemService.gI().createNewItem(randStone);
            newStone.quantity = 1;

            InventoryService.gI().addItemBag(player, newStone);
            InventoryService.gI().sendItemBag(player);

            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Ghép thành công! Bạn nhận được " + newStone.template.name);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Ghép thất bại!");
        }

        CombineService.gI().reOpenItemCombine(player);
    }
}
