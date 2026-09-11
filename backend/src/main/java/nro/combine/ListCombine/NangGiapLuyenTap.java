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

public class NangGiapLuyenTap {

    private static final int GOLD_COMBINE = 100000000;
    private static final int DA_HO_PHACH_ID = 1980;
    private static final int GIAP_LUYEN_TAP_MOI_ID = 1797;
    private static final int SO_DA_CAN = 200;

    private static boolean isGiapLuyenTap(Item item) {
        return item != null && item.isNotNullItem() && item.template.type == 32;
    }

    private static boolean isDaHoPhach(Item item) {
        return item != null && item.isNotNullItem() && item.template.id == DA_HO_PHACH_ID;
    }

    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hãy chọn 1 giáp luyện tập và " + SO_DA_CAN + " đá hổ phách", "Đóng");
            return;
        }

        Item giapLuyenTap = null;
        Item daHoPhach = null;

        for (Item item : player.combine.itemsCombine) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (isGiapLuyenTap(item)) {
                giapLuyenTap = item;
            } else if (isDaHoPhach(item)) {
                daHoPhach = item;
            }
        }

        if (giapLuyenTap == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Thiếu giáp luyện tập", "Đóng");
            return;
        }

        if (daHoPhach == null) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Thiếu đá hổ phách", "Đóng");
            return;
        }

        if (daHoPhach.quantity < SO_DA_CAN) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Còn thiếu\n" + (SO_DA_CAN - daHoPhach.quantity) + " đá hổ phách", "Đóng");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hành trang không đủ chỗ trống", "Đóng");
            return;
        }

        player.combine.goldCombine = GOLD_COMBINE;
        player.combine.ratioCombine = 100f;
        player.combine.countDaQuy = SO_DA_CAN;
        player.combine.countDaBaoVe = 0;

        String npcSay = "|2|Nâng giáp luyện tập\n"
                + "|7|Sau khi nâng cấp sẽ nhận:\n"
                + "|1|Giáp luyện tập cấp 4\n"            
                + (player.inventory.gold < GOLD_COMBINE ? "|7|" : "|1|")
                + "Cần " + Util.formatNumber(GOLD_COMBINE, FormatStyle.VIETNAMESE) + " vàng\n"
                + (daHoPhach.quantity < SO_DA_CAN ? "|7|" : "|1|")
                + "Cần " + SO_DA_CAN + " đá hổ phách";

        if (player.inventory.gold < GOLD_COMBINE) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    npcSay,
                    "Còn thiếu\n" + Util.formatNumber(GOLD_COMBINE - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
            return;
        }

        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                npcSay,
                "Nâng cấp\n" + Util.formatNumber(GOLD_COMBINE, FormatStyle.VIETNAMESE) + " vàng",
                "Từ chối");
    }

    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() != 2) {
            return;
        }

        Item giapLuyenTap = null;
        Item daHoPhach = null;

        for (Item item : player.combine.itemsCombine) {
            if (item == null || !item.isNotNullItem()) {
                continue;
            }
            if (isGiapLuyenTap(item)) {
                giapLuyenTap = item;
            } else if (isDaHoPhach(item)) {
                daHoPhach = item;
            }
        }

        if (giapLuyenTap == null || daHoPhach == null) {
            return;
        }

        if (player.inventory.gold < GOLD_COMBINE) {
            Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện");
            return;
        }

        if (daHoPhach.quantity < SO_DA_CAN) {
            Service.gI().sendThongBao(player, "Không đủ đá hổ phách để thực hiện");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Hành trang không đủ chỗ trống");
            return;
        }

        Item itemMoi = ItemService.gI().createNewItem((short) GIAP_LUYEN_TAP_MOI_ID);
        if (itemMoi == null) {
            Service.gI().sendThongBao(player, "Không thể tạo vật phẩm, vui lòng báo admin");
            return;
        }

        itemMoi.quantity = 1;
        itemMoi.itemOptions.clear();
        itemMoi.itemOptions.add(new ItemOption(77, 10));
        itemMoi.itemOptions.add(new ItemOption(9, 0));
        itemMoi.itemOptions.add(new ItemOption(30, 0));

        player.inventory.gold -= GOLD_COMBINE;

        InventoryService.gI().subQuantityItemsBag(player, giapLuyenTap, 1);
        InventoryService.gI().subQuantityItemsBag(player, daHoPhach, SO_DA_CAN);
        InventoryService.gI().addItemBag(player, itemMoi);

        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}