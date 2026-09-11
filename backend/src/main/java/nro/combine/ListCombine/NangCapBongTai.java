package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.Util;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import models.Item.ItemOption;
import nro.combine.CombineService;

public class NangCapBongTai {

    // ID Bông tai
    private static final int BT1 = 454;   // Bông tai cấp 1
    private static final int BT2 = 921;   // Bông tai cấp 2
    private static final int BT3 = 1943;  // Bông tai cấp 3

    // ID Mảnh
    private static final int MANH_BT1 = 933;   // Mảnh vỡ bông tai (dùng nâng BT1 -> BT2)
    private static final int MANH_BT3 = 1927;  // Mảnh bông tai cấp 3 (dùng nâng BT2 -> BT3)

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            Service.gI().sendDialogMessage(player, "Cần nguyên liệu hợp lệ để nâng bông tai.");
            return;
        }

        Item bongTai = null;
        Item manhVo = null;
        int type = 0; // 1 = nâng lên BT2, 2 = nâng lên BT3

        for (Item item : player.combine.itemsCombine) {
            if (item.template.id == BT1) {
                bongTai = item;
                type = 1;
            } else if (item.template.id == BT2) {
                bongTai = item;
                type = 2;
            } else if (item.template.id == MANH_BT1 || item.template.id == MANH_BT3) {
                manhVo = item;
            }
        }

        if (bongTai == null || manhVo == null) {
            Service.gI().sendDialogMessage(player, "Cần nguyên liệu hợp lệ để nâng bông tai.");
            return;
        }

        int opSoLuong = manhVo.getOptionParam(31);
        int quantityManhVo = (opSoLuong > 0) ? opSoLuong : manhVo.quantity;

        StringBuilder text = new StringBuilder();

        if (type == 1) {
            // Nâng BT1 -> BT2
            text.append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+2]\n\n");
            text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 20%\n");
            text.append(quantityManhVo >= 99 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 99 Mảnh vỡ bông tai\n");
            text.append(player.inventory.gold >= 500_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 500 Tr vàng\n");
            text.append(player.inventory.getGemAndRuby() >= 20 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 20 hồng ngọc\n");
            text.append(ConstFont.BOLD_RED).append("Thất bại -99 mảnh vỡ bông tai\n");
        } else {
            // Nâng BT2 -> BT3
            text.append(ConstFont.BOLD_BLUE).append("Bông tai Porata [+3]\n\n");
            text.append(ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 10%\n");
            text.append(quantityManhVo >= 999 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 999 Mảnh bông tai cấp 3\n");
            text.append(player.inventory.gold >= 2_000_000_000 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 1 Tỷ vàng\n");
            text.append(player.inventory.getGemAndRuby() >= 50 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED)
                    .append("Cần 50 hồng ngọc\n");
            text.append(ConstFont.BOLD_RED).append("Thất bại -999 mảnh bông tai cấp 3\n");
        }

        // Kiểm tra thiếu nguyên liệu
        if (type == 1) {
            if (player.inventory.getGemAndRuby() < 20) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + Util.formatNumber(20 - player.inventory.getGemAndRuby(), FormatStyle.VIETNAMESE) + " hồng ngọc");
                return;
            }
            if (player.inventory.gold < 500_000_000) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + Util.formatNumber(500_000_000 - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
                return;
            }
            if (quantityManhVo < 99) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + (99 - quantityManhVo) + " Mảnh vỡ bông tai");
                return;
            }
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                    "Nâng cấp\n500 Tr vàng\n20 hồng ngọc", "Từ chối");
        } else {
            if (player.inventory.getGemAndRuby() < 50) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + Util.formatNumber(50 - player.inventory.getGemAndRuby(), FormatStyle.VIETNAMESE) + " hồng ngọc");
                return;
            }
            if (player.inventory.gold < 2_000_000_000) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + Util.formatNumber(2_000_000_000 - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
                return;
            }
            if (quantityManhVo < 999) {
                CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(),
                        "Còn thiếu\n" + (999 - quantityManhVo) + " Mảnh bông tai cấp 3");
                return;
            }
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, text.toString(),
                    "Nâng cấp\n2 Tỷ vàng\n50  hồng ngọc", "Từ chối");
        }
    }

    public static void nangCapBongTai(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }

        Item bongTai = null;
        Item manhVo = null;
        int type = 0;

        for (Item item : player.combine.itemsCombine) {
            if (item.template.id == BT1) {
                bongTai = item;
                type = 1;
            } else if (item.template.id == BT2) {
                bongTai = item;
                type = 2;
            } else if (item.template.id == MANH_BT1 || item.template.id == MANH_BT3) {
                manhVo = item;
            }
        }

        if (bongTai == null || manhVo == null) {
            return;
        }

        int opSoLuong = manhVo.getOptionParam(31);
        int quantityManhVo = (opSoLuong > 0) ? opSoLuong : manhVo.quantity;

        if (type == 1) {
            // BT1 -> BT2
            if (quantityManhVo < 99 || player.inventory.gold < 500_000_000 || player.inventory.getRuby() < 20) {
                return;
            }
            player.inventory.gold -= 500_000_000;
            player.inventory.subRuby(20);

            if (Util.isTrue(20, 100)) {
                Item btc2 = ItemService.gI().createNewItem((short) BT2);
                btc2.itemOptions.add(new ItemOption(72, 2));
                InventoryService.gI().subQuantityItemsBag(player, bongTai, 1);
                if (opSoLuong > 0) {
                    manhVo.subOptionParam(31, 99);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, manhVo, 99);
                }
                InventoryService.gI().addItemBag(player, btc2);
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
                if (opSoLuong > 0) {
                    manhVo.subOptionParam(31, 99);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, manhVo, 99);
                }
            }

        } else if (type == 2) {
            // BT2 -> BT3
            if (quantityManhVo < 999 || player.inventory.gold < 2000_000_000 || player.inventory.getRuby() < 50) {
                return;
            }
            player.inventory.gold -= 2_000_000_000;
            player.inventory.subRuby(50);

            if (Util.isTrue(10, 100)) {
                // ✅ Nâng cấp tại chỗ: chỉ đổi id template từ BT2 sang BT3, giữ nguyên option
                bongTai.template = ItemService.gI().getTemplate((short) BT3);
                if (opSoLuong > 0) {
                    manhVo.subOptionParam(31, 999);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, manhVo, 999);
                }
                CombineService.gI().sendEffectSuccessCombine(player);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
                if (opSoLuong > 0) {
                    manhVo.subOptionParam(31, 999);
                } else {
                    InventoryService.gI().subQuantityItemsBag(player, manhVo, 999);
                }
            }
        }
    }
}
