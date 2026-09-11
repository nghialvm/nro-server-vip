package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import nro.combine.CombineService;

public class NangChiSoBongTai {

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            Service.gI().sendDialogMessage(player, 
                "Cần bông tai cấp 2 hoặc cấp 3, 99 mảnh hồn Porata và 1 đá xanh lam.");
            return;
        }

        Item bongTai = null;
        Item manhHonBongTai = null;
        Item daXanhLam = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 921: // BT2
                    case 1943: // BT3
                        bongTai = item;
                        break;
                    case 934: // Mảnh hồn
                        manhHonBongTai = item;
                        break;
                    case 935: // Đá xanh lam
                        daXanhLam = item;
                        break;
                }
            }
        }

        if (bongTai == null || manhHonBongTai == null || daXanhLam == null) {
            Service.gI().sendDialogMessage(player, 
                "Cần bông tai cấp 2 hoặc cấp 3, 99 mảnh hồn Porata và 1 đá xanh lam.");
            return;
        }

        StringBuilder text = new StringBuilder();
        if (bongTai.template.id == 921) {
            text.append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+2] - Nâng chỉ số\n\n");
            text.append(ConstFont.BOLD_GREEN).append("Reset toàn bộ chỉ số, random lại\n");
        } else {
            text.append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+3] - Nâng chỉ số\n\n");
            text.append(ConstFont.BOLD_GREEN).append("Chỉ thay thế dòng cuối bằng random mới\n");
        }

        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 50%\n");
        text.append(manhHonBongTai.quantity >= 99 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 99 Mảnh hồn bông tai\n");
        text.append(daXanhLam.quantity >= 1 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 1 Đá xanh lam\n");
        text.append(player.inventory.getGemAndRuby() >= 250 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
            .append("Cần 250 ngọc\n");

        if (player.inventory.getGemAndRuby() < 250) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + Util.formatNumber(250 - player.inventory.getGemAndRuby(), FormatStyle.VIETNAMESE) + " ngọc");
            return;
        }
        if (daXanhLam.quantity < 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\nĐá xanh lam");
            return;
        }
        if (manhHonBongTai.quantity < 99) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                "Còn thiếu\n" + (99 - manhHonBongTai.quantity) + " Mảnh hồn bông tai");
            return;
        }

        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
            text.toString(), "Nâng cấp\n250 ngọc", "Từ chối");
    }

    public static void nangChiSoBongTai(Player player) {
        if (player.combine.itemsCombine.size() != 3) {
            return;
        }

        Item bongTai = null;
        Item manhHonBongTai = null;
        Item daXanhLam = null;

        for (Item item : player.combine.itemsCombine) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 921: // BT2
                    case 1943: // BT3
                        bongTai = item;
                        break;
                    case 934: // Mảnh hồn
                        manhHonBongTai = item;
                        break;
                    case 935: // Đá xanh lam
                        daXanhLam = item;
                        break;
                }
            }
        }

        if (bongTai == null || manhHonBongTai == null || daXanhLam == null
                || player.inventory.getGemAndRuby() < 250
                || daXanhLam.quantity < 1
                || manhHonBongTai.quantity < 99) {
            return;
        }

        if (Util.isTrue(50, 100)) {
            // random option mới
            int[] options = {77, 103, 50, 108, 94, 14, 80, 81, 175, 5};
            int option = options[Util.nextInt(options.length)];
            int param = (option == 94 || option == 14) ? Util.nextInt(1, 10) : Util.nextInt(1, 15);

            if (bongTai.template.id == 921) {
                // BT2: reset toàn bộ option
                bongTai.itemOptions.clear();
                bongTai.itemOptions.add(new ItemOption(option, param));
                bongTai.itemOptions.add(new ItemOption(38, 0));
            } else {
                // BT3: thay thế dòng cuối cùng
                if (!bongTai.itemOptions.isEmpty()) {
                    bongTai.itemOptions.remove(bongTai.itemOptions.size() - 1);
                }
                bongTai.itemOptions.add(new ItemOption(option, param));
            }

            CombineService.gI().sendEffectSuccessCombine(player);
        } else {
            CombineService.gI().sendEffectFailCombine(player);
        }

        // Trừ nguyên liệu
        InventoryService.gI().subQuantityItemsBag(player, manhHonBongTai, 99);
        InventoryService.gI().subQuantityItemsBag(player, daXanhLam, 1);
        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}
