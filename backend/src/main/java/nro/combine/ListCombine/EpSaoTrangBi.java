//package nro.combine.ListCombine;
//
//import nro.inventory.InventoryService;
//import nro.player.Player;
//import nro.services.Service;
//import consts.ConstFont;
//import consts.ConstNpc;
//import models.Item.Item;
//import models.Item.ItemOption;
//import nro.combine.CombineService;
//
//public class EpSaoTrangBi {
//
//    public static int getGem(int star) {
//        switch (star) {
//            case 7:
//                return 200;
//            case 8:
//                return 300;
//            default:
//                return 10;
//        }
//    }
//
//    public static void showInfoCombine(Player player) {
//        if (player.combine.itemsCombine.size() != 2) {
//            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
//            return;
//        }
//        Item trangBi = null;
//        Item daPhaLe = null;
//        for (Item item : player.combine.itemsCombine) {
//            if (item.canPhaLeHoa()) {
//                trangBi = item;
//            } else if (item.isDaPhaLeEpSao()) {
//                daPhaLe = item;
//            }
//        }
//        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem()) {
//            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
//            return;
//        }
//        int star = trangBi.getOptionParam(102);
//        int starEmpty = trangBi.getOptionParam(107);
//        int cuongHoa = trangBi.getOptionParam(228);
//        if (star < 7 && daPhaLe.isDaPhaLeMoi()) {
//            Service.gI().sendDialogMessage(player, "Sao pha lê cấp 2 hoặc lấp lánh chỉ dùng cho ô thứ 8 đã cường hóa trở lên.");
//            return;
//        }
//        if (star >= 8 && daPhaLe.isDaPhaLeCu()) {
//            Service.gI().sendDialogMessage(player, "Chỉ có thể nạm Sao pha lê mới.");
//            return;
//        }
//        if (star >= starEmpty) {
//            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
//            return;
//        }
//
//        // ====== BỎ KIỂM TRA CƯỜNG HÓA (ÉP SAO 8,9 KHÔNG CẦN DÙI ĐỤC) ======
//        /*
//        if (star == 7 && cuongHoa == 0 || star >= 8 && cuongHoa < star + 1) {
//            Service.gI().sendDialogMessage(player, "Cần cường hóa ô sao pha lê này trước");
//            return;
//        }
//        */
//        // ================================================================
//
//        StringBuilder text = new StringBuilder();
//        text.append(ConstFont.BOLD_BLUE).append(trangBi.template.name).append("\n");
//        text.append(ConstFont.BOLD_DARK).append(star >= 7 ? trangBi.getOptionInfoCuongHoa(daPhaLe) : trangBi.getOptionInfo(daPhaLe)).append("\n");
//        text.append(player.inventory.getGemAndRuby() < getGem(star) ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Cần 10 ngọc");
//        if (player.inventory.getGemAndRuby() < getGem(star)) {
//            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Còn thiếu\n" + (getGem(star) - player.inventory.getGemAndRuby()) + " ngọc");
//            return;
//        }
//        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(), "Nâng cấp\n" + getGem(star) + " ngọc", "Từ chối");
//    }
//
//    public static void epSaoTrangBi(Player player) {
//        if (player.combine.itemsCombine.size() != 2) {
//            return;
//        }
//        Item trangBi = null;
//        Item daPhaLe = null;
//        for (Item item : player.combine.itemsCombine) {
//            if (item.canPhaLeHoa()) {
//                trangBi = item;
//            } else if (item.isDaPhaLeEpSao()) {
//                daPhaLe = item;
//            }
//        }
//        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem()) {
//            return;
//        }
//        int star = trangBi.getOptionParam(102);
//        int starEmpty = trangBi.getOptionParam(107);
//        int cuongHoa = trangBi.getOptionParam(228);
//
//        // ====== BỎ KIỂM TRA CƯỜNG HÓA TRONG HÀM ÉP ======
//        /*
//        if (star >= starEmpty || star < 7 && daPhaLe.isDaPhaLeMoi() || star >= 7 && daPhaLe.isDaPhaLeCu()
//                || star == 7 && cuongHoa == 0 || star >= 8 && cuongHoa < star + 1
//                || player.inventory.getGemAndRuby() < getGem(star)) {
//            return;
//        }
//        */
//        // DÙNG ĐIỀU KIỆN RÚT GỌN KHÔNG KIỂM TRA CƯỜNG HÓA
//        if (star >= starEmpty 
//                || (star < 7 && daPhaLe.isDaPhaLeMoi()) 
//                || (star >= 8 && daPhaLe.isDaPhaLeCu()) 
//                || player.inventory.getGemAndRuby() < getGem(star)) {
//            return;
//        }
//        // =================================================
//
//        trangBi.addOptionParam(102, 1);
//        if (star >= 8) {
//            if (star == 8) {
//                trangBi.itemOptions.add(new ItemOption(218, 0));
//            }
//            trangBi.itemOptions.add(new ItemOption(daPhaLe.getOptionDaPhaLe().optionTemplate.id, daPhaLe.getOptionDaPhaLe().param));
//        } else {
//            trangBi.addOptionParam(daPhaLe.getOptionDaPhaLe().optionTemplate.id, daPhaLe.getOptionDaPhaLe().param);
//        }
//        player.inventory.subGemAndRuby(getGem(star));
//        InventoryService.gI().subQuantityItemsBag(player, daPhaLe, 1);
//        CombineService.gI().sendEffectSuccessCombine(player);
//        InventoryService.gI().sendItemBag(player);
//        Service.gI().sendMoney(player);
//        CombineService.gI().reOpenItemCombine(player);
//    }
//}
package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import nro.combine.CombineService;

public class EpSaoTrangBi {

    public static int getGem(int star) {
        switch (star) {
            case 7:
                return 200;
            case 8:
                return 300;
            default:
                return 10;
        }
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }

        Item trangBi = null;
        Item daPhaLe = null;

        for (Item item : player.combine.itemsCombine) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (item.canPhaLeHoa()) {
                trangBi = item;
            } else if (item.isDaPhaLeEpSao()) {
                daPhaLe = item;
            }
        }

        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem()) {
            Service.gI().sendDialogMessage(player, "Cần 1 trang bị có lỗ sao pha lê và 1 loại ngọc để ép vào.");
            return;
        }

        int star = trangBi.getOptionParam(102);
        int starEmpty = trangBi.getOptionParam(107);

        if (star >= starEmpty) {
            Service.gI().sendDialogMessage(player, "Trang bị đã max sao pha lê.");
            return;
        }

        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append(trangBi.template.name).append("\n");
        text.append(ConstFont.BOLD_DARK)
            .append(star >= 7 ? trangBi.getOptionInfoCuongHoa(daPhaLe) : trangBi.getOptionInfo(daPhaLe))
            .append("\n");
        text.append(player.inventory.getGemAndRuby() < getGem(star) ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE)
            .append("Cần ").append(getGem(star)).append(" ngọc");

        if (player.inventory.getGemAndRuby() < getGem(star)) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.IGNORE_MENU,
                    text.toString(),
                    "Còn thiếu\n" + (getGem(star) - player.inventory.getGemAndRuby()) + " ngọc"
            );
            return;
        }

        CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.MENU_START_COMBINE,
                text.toString(),
                "Nâng cấp\n" + getGem(star) + " ngọc",
                "Từ chối"
        );
    }

    public static void epSaoTrangBi(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }

        Item trangBi = null;
        Item daPhaLe = null;

        for (Item item : player.combine.itemsCombine) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (item.canPhaLeHoa()) {
                trangBi = item;
            } else if (item.isDaPhaLeEpSao()) {
                daPhaLe = item;
            }
        }

        if (trangBi == null || !trangBi.isNotNullItem() || daPhaLe == null || !daPhaLe.isNotNullItem()) {
            return;
        }

        int star = trangBi.getOptionParam(102);
        int starEmpty = trangBi.getOptionParam(107);

        if (star >= starEmpty || player.inventory.getGemAndRuby() < getGem(star)) {
            return;
        }

        trangBi.addOptionParam(102, 1);

        if (star >= 8) {
            if (star == 8 && trangBi.getOptionParam(218) == -1) {
                trangBi.itemOptions.add(new ItemOption(218, 0));
            }
            ItemOption optDa = daPhaLe.getOptionDaPhaLe();
            if (optDa != null) {
                trangBi.itemOptions.add(new ItemOption(optDa.optionTemplate.id, optDa.param));
            }
        } else {
            ItemOption optDa = daPhaLe.getOptionDaPhaLe();
            if (optDa != null) {
                trangBi.addOptionParam(optDa.optionTemplate.id, optDa.param);
            }
        }

        player.inventory.subGemAndRuby(getGem(star));
        InventoryService.gI().subQuantityItemsBag(player, daPhaLe, 1);
        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}