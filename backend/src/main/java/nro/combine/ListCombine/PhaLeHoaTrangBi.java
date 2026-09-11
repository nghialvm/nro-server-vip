package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.server.ServerNotify;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import nro.combine.CombineService;

public class PhaLeHoaTrangBi {

    private static float getRatio(int star) {
        switch (star) {
            case 0:
                return 70;
            case 1:
                return 50;
            case 2:
                return 30;
            case 3:
                return 20;
            case 4:
                return 10;
            case 5:
                return 5;
            case 6:
                return 0.5f;
            case 7:
                return 0.25f;
//            case 8:
//                return 0.25f;
            default:
                return 0;
        }
    }

    private static String getRatioStr(int star) {
        int ratio = (int) getRatio(star);
        if (ratio < 1) {
            ratio = 1;
        }
        return String.valueOf(ratio);
    }

    private static int getGold(int star) {
        switch (star) {
            case 0:
                return 5_000_000;
            case 1:
                return 10_000_000;
            case 2:
                return 20_000_000;
            case 3:
                return 40_000_000;
            case 4:
                return 60_000_000;
            case 5:
                return 90_000_000;
            case 6:
                return 130_000_000;
            case 7:
                return 160_000_000;
//            case 8:
//                return 180_000_000;
            default:
                return 0;
        }
    }

    private static int getGem(int star) {
        switch (star) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 4;
            case 4:
                return 5;
            case 5:
                return 6;
            case 6:
                return 7;
            case 7:
                return 20;
//            case 8:
//                return 30;
            default:
                return 0;
        }
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Trang bị không phù hợp");
            return;
        }
        Item item = player.combine.itemsCombine.get(0);
        if (item == null || !item.isNotNullItem()) {
            return;
        }
        if (item.isHaveOption(93)) {
            Service.gI().sendDialogMessage(player, "Trang bị có hạn sử dụng, không thể thực hiện");
            return;
        }
        if (!item.canPhaLeHoa()) {
            Service.gI().sendDialogMessage(player, "Trang bị không phù hợp");
            return;
        }
        int star = item.getOptionParam(107);
        int gem = getGem(star);
        int gold = getGold(star);
        if (star >= CombineService.MAX_STAR_ITEM) {
            Service.gI().sendDialogMessage(player, "Đã đạt số pha lê tối đa");
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_BLUE).append(item.template.name).append("\n");
        text.append(ConstFont.BOLD_DARK).append(item.getOptionInfo()).append("\n");
        text.append(ConstFont.BOLD_GREEN).append(star + 1).append(" ô Sao Pha Lê\n");
        text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: ").append(getRatioStr(star)).append("%\n");
        text.append(player.inventory.gold < gold ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Cần ").append(Util.formatNumber(gold, FormatStyle.VIETNAMESE)).append(" vàng");
        if (player.inventory.gold < gold) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                    "Còn thiếu\n" + Util.formatNumber(gold - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                "Nâng cấp\n" + gem + " ngọc\nx100 lần", "Nâng cấp\n" + gem + " ngọc\nx10 lần", "Nâng cấp\n" + gem + " ngọc", "Từ chối");
    }

    public static void phaLeHoa(Player player, int... numm) {
        int n = 1;
        if (numm.length > 0) {
            n = numm[0];
        }
        if (!player.combine.itemsCombine.isEmpty()) {
            Item item = player.combine.itemsCombine.get(0);
            if (item == null || !item.isNotNullItem() || item.isHaveOption(93) || !item.canPhaLeHoa()) {
                return;
            }
            int star = item.getOptionParam(107);
            if (star >= CombineService.MAX_STAR_ITEM) {
                return;
            }
            int gold = getGold(star);
            int gem = getGem(star);
            if (n == 1) {
                if (player.inventory.gold < gold) {
                    return;
                } else if (player.inventory.gem < gem) {
                    Service.gI().sendServerMessage(player, "Bạn không đủ ngọc, còn thiếu " + (gem - player.inventory.gem) + " ngọc nữa");
                    return;
                }
            }
            int num = 0;
            boolean success = false;
            for (int i = 0; i < n; i++) {
                num = i + 1;
                if (player.inventory.gem < gem) {
                    Service.gI().sendServerMessage(player, "Sau " + i + " lần nâng cấp thất bại, bạn không đủ ngọc để tiếp tục.");
                    break;
                }
                if (player.inventory.gold < gold) {
                    Service.gI().sendServerMessage(player, "Sau " + i + " lần nâng cấp thất bại, bạn không đủ vàng để tiếp tục.");
                    break;
                }
                player.inventory.gold -= gold;
                player.inventory.gem-= gem;
                if (Util.isTrue(getRatio(star), 1000)) {
                    success = true;
                    break;
                }
            }
            if (success) {
                item.addOptionParam(107, 1);
                if (star > 7) {
                    ServerNotify.gI().notify("Chúc mừng " + player.name + " vừa pha lê hóa "
                            + "thành công " + item.template.name + " lên " + (star + 1) + " sao pha lê");
                }
                if (n > 1) {
                    Service.gI().sendServerMessage(player, "Thành công sau " + num + " lần nâng cấp.");
                }
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }
    }

}
