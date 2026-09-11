package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.combine.CombineService;

public class NangCapSachTuyetKy {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
            return;
        }
        Item sachTuyetKy = null;
        Item kimBamGiay = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isSachTuyetKy()) {
                sachTuyetKy = item;
            } else if (item.template.id == 1285) {
                kimBamGiay = item;
            }
        }
        if (sachTuyetKy == null || kimBamGiay == null) {
            Service.gI().sendDialogMessage(player, "Cần Sách Tuyệt Kỹ 1 và 10 Kìm bấm giấy.");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append("Nâng cấp sách tuyệt kỹ\n");
        text.append(kimBamGiay.quantity >= 10 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cần 10 Kìm bấm giấy\n");
        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 10%\n");
        text.append(ConstFont.BOLD_BLUE).append("Nâng cấp thất bại sẽ mất 10 Kìm bấm giấy");
        if (kimBamGiay.quantity < 10) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                    "Còn thiếu\n" + (10 - kimBamGiay.quantity) + " Kìm bấm giấy");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                "Nâng cấp", "Từ chối");
    }

    public static void nangCapSachTuyetKy(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }
        Item sachTuyetKy = null;
        Item kimBamGiay = null;
        for (Item item : player.combine.itemsCombine) {
            if (item.isSachTuyetKy()) {
                sachTuyetKy = item;
            } else if (item.template.id == 1285) {
                kimBamGiay = item;
            }
        }
        if (sachTuyetKy == null || kimBamGiay == null) {
            return;
        }
        if (Util.isTrue(10, 100)) {
            switch (sachTuyetKy.template.id) {
                case 1044:
                    sachTuyetKy.template = ItemService.gI().getTemplate(1278);
                    break;
                case 1211:
                    sachTuyetKy.template = ItemService.gI().getTemplate(1279);
                    break;
                case 1212:
                    sachTuyetKy.template = ItemService.gI().getTemplate(1280);
                    break;
            }
            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }
        InventoryService.gI().subQuantityItemsBag(player, kimBamGiay, 10);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }

}
