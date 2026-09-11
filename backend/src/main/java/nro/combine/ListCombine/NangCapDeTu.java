package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import nro.services.DetuService;
import nro.combine.CombineService;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import java.util.ArrayList;
import java.util.List;

public class NangCapDeTu {

    // ====== CONFIG ======
    private static final long POWER_REQUIRE = 70_000_000_000L; 
    private static final int GOLD_BAR_ID = 457;               
    private static final int GOLD_BAR_REQUIRE = 200;         
    private static final int THAN_LINH_REQUIRE = 5;           

    public static void showInfoCombine(Player player) {
        if (player.Detu == null) {
            Service.gI().sendDialogMessage(player, "Bạn chưa có đệ tử nào để tiến hóa");
            return;
        }
        if (player.Detu.typeDeTu != 1) {
            Service.gI().sendDialogMessage(player, "Chỉ đệ tử Mabư mới có thể tiến hóa thành Đệ Black");
            return;
        }

        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE)
                .append("Con có muốn tiến hóa Mabư thành Đệ Black không?\n\n")
                .append(ConstFont.BOLD_GREEN).append("Điều kiện:\n")
                .append("- Sức mạnh ≥ 70 tỷ\n")
                .append("- ").append(THAN_LINH_REQUIRE).append(" món Thần Linh bất kỳ \n")
                .append("- ").append(GOLD_BAR_REQUIRE).append(" Thỏi vàng\n\n")
                .append(ConstFont.BOLD_BLUE)
                .append("Sau khi tiến hóa, Mabư sẽ trở thành Đệ Black với sức mạnh bóng tối!");

        CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.MENU_START_COMBINE,
                text.toString(),
                "Nâng cấp",
                "Từ chối"
        );
    }

    public static void startCombine(Player player) {
        try {
            if (player == null || player.Detu == null) {
                Service.gI().sendDialogMessage(player, "Bạn chưa có đệ tử nào");
                return;
            }
            if (player.Detu.typeDeTu != 1) {
                Service.gI().sendDialogMessage(player, "Chỉ đệ Mabư mới có thể tiến hóa");
                return;
            }
            if (player.Detu.nPoint.power < POWER_REQUIRE) {
                Service.gI().sendDialogMessage(player, "Đệ Mabư cần ít nhất 70 tỷ sức mạnh để tiến hóa");
                return;
            }
            Item thoiVang = InventoryService.gI().findItemBag(player, GOLD_BAR_ID);
            if (thoiVang == null || thoiVang.quantity < GOLD_BAR_REQUIRE) {
                Service.gI().sendDialogMessage(player, "Cần " + GOLD_BAR_REQUIRE + " Thỏi vàng để tiến hóa");
                return;
            }
            List<Item> thanLinhList = new ArrayList<>();
            int totalTL = 0;
            for (int i = 0; i < player.inventory.itemsBag.size(); i++) {
                Item it = player.inventory.itemsBag.get(i);
                if (it != null && it.template != null && it.isDTLNew()) {
                    totalTL += it.quantity;
                    thanLinhList.add(it);
                    if (totalTL >= THAN_LINH_REQUIRE) break;
                }
            }

            if (totalTL < THAN_LINH_REQUIRE) {
                Service.gI().sendDialogMessage(player,
                        "Cần " + THAN_LINH_REQUIRE + " món Thần Linh bất kỳ để tiến hóa");
                return;
            }
            InventoryService.gI().subQuantityItemsBag(player, thoiVang, GOLD_BAR_REQUIRE);
            int needRemove = THAN_LINH_REQUIRE;
            for (Item it : thanLinhList) {
                if (it == null || it.template == null) continue;
                int removeNow = Math.min(it.quantity, needRemove);
                InventoryService.gI().subQuantityItemsBag(player, it, removeNow);
                needRemove -= removeNow;
                if (needRemove <= 0) break;
            }

            InventoryService.gI().sendItemBag(player);

            // ===== HIỆU ỨNG TIẾN HÓA =====
            Service.gI().sendThongBao(player, "Đang tiến hóa đệ tử Mabư...");
            Service.gI().chatJustForMe(player, player.Detu, "Ta cảm nhận được... sức mạnh bóng tối đang trỗi dậy...");

            DetuService.gI().changeBlackPet(player);

            Service.gI().sendThongBao(player, "Tiến hóa thành công! Đệ tử của bạn đã trở thành Đệ Black.");
            Service.gI().sendThongBao(player,
                    "Đã sử dụng " + THAN_LINH_REQUIRE + " món Thần Linh và " + GOLD_BAR_REQUIRE + " Thỏi vàng.");

        } catch (Exception e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Đã xảy ra lỗi khi tiến hóa đệ tử.");
        }
    }
}