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
import models.Item.ItemService;
import nro.combine.CombineService;

public class NhapNgocRong {

    private static final int SO_NGOC_CAN = 7;

    /* =========================
     * HIỂN THỊ MENU
     * ========================= */
    public static void showInfoCombine(Player player) {

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendDialogMessage(player, "Hành trang đã đầy, cần ít nhất 1 ô trống");
            return;
        }

        if (player.combine.itemsCombine.size() != 1) {
            Service.gI().sendDialogMessage(player, "Cần 7 viên Ngọc Rồng");
            return;
        }

        Item item = player.combine.itemsCombine.get(0);
        if (!isValid(item)) {
            Service.gI().sendDialogMessage(player, "Cần 7 viên Ngọc Rồng hợp lệ");
            return;
        }

        // ===== NGỌC 2–7 SAO =====
        if (item.template.id > 14 && item.template.id <= 20) {

            if (item.template.id <= 16) {
                Service.gI().sendDialogMessage(player, "Không thể nhập Ngọc Rồng 3 sao hoặc thấp hơn!");
                return;
            }

            String text = ConstFont.BOLD_BLUE +
                    "Con có muốn biến 7 " + item.template.name + " thành\n" +
                    "1 viên " + ItemService.gI().getTemplate((short) (item.template.id - 1)).name + "\n" +
                    ConstFont.BOLD_GREEN + "Cần 7 " + item.template.name;

            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_START_COMBINE,
                    text,
                    "Nhập x1",
                    "Nhập x10",
                    "Nhập x100",
                    "Từ chối"
            );
            return;
        }

        // ===== NGỌC 1 SAO → SIÊU CẤP =====
        if (item.template.id == 14) {

            Item loNuocPhep = InventoryService.gI().findItemBag(player, 1029);

            StringBuilder text = new StringBuilder();
            text.append(ConstFont.BOLD_BLUE)
                    .append("Con có muốn biến 7 Ngọc Rồng 1 sao thành\n")
                    .append("1 viên Ngọc Rồng Siêu Cấp\n")
                    .append(ConstFont.BOLD_GREEN)
                    .append("Cần 7 Ngọc Rồng 1 sao\n")
                    .append(player.inventory.gold >= 150_000_000 ? ConstFont.BOLD_GREEN : ConstFont.BOLD_RED)
                    .append("Cần 150.000.000 vàng\n")
                    .append(loNuocPhep == null ? ConstFont.BOLD_RED : ConstFont.BOLD_GREEN)
                    .append("Cần 1 Lọ nước phép\n")
                    .append(ConstFont.BOLD_BLUE)
                    .append("Tỉ lệ thành công: 50%");

            if (loNuocPhep == null || player.inventory.gold < 150_000_000) {
                CombineService.gI().baHatMit.createOtherMenu(
                        player,
                        ConstNpc.IGNORE_MENU,
                        text.toString(),
                        "Thiếu\nNguyên liệu"
                );
                return;
            }

            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.MENU_START_COMBINE,
                    text.toString(),
                    "Nhập x1",
                    "Nhập x10",
                    "Nhập x100",
                    "Từ chối"
            );
        }
    }

    /* =========================
     * XỬ LÝ NHẬP
     * ========================= */
    public static void nhapNgocRong(Player player, boolean useDBV) {

        int times = player.nhapNgocRongTimes;
        player.nhapNgocRongTimes = 1; 

        if (times <= 0) return;
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) return;
        if (player.combine.itemsCombine.size() != 1) return;

        Item item = player.combine.itemsCombine.get(0);
        if (!isValid(item)) return;

        int soLan = Math.min(times, item.quantity / SO_NGOC_CAN);
        if (soLan <= 0) {
            Service.gI().sendServerMessage(player, "Không đủ số lượng Ngọc Rồng");
            return;
        }

        // ===== NGỌC THƯỜNG =====
        if (item.template.id > 14 && item.template.id <= 20) {

            if (item.template.id <= 16) {
                Service.gI().sendServerMessage(player, "Không thể nhập Ngọc Rồng 3 sao hoặc thấp hơn!");
                return;
            }

            if (InventoryService.gI().getCountEmptyBag(player) < 1) {
                Service.gI().sendServerMessage(player, "Hành trang không đủ chỗ trống");
                return;
            }

            Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
            nr.quantity = soLan;
            nr.itemOptions.add(new ItemOption(30, 1));

            CombineService.gI().sendEffectCombineDB(player, nr.template.iconID);
            InventoryService.gI().addItemBag(player, nr);
            InventoryService.gI().subQuantityItemsBag(player, item, soLan * SO_NGOC_CAN);
            InventoryService.gI().sendItemBag(player);
            CombineService.gI().reOpenItemCombine(player);
            return;
        }

        // ===== 1 SAO → SIÊU CẤP =====
        if (item.template.id == 14) {

            Item loNuocPhep = InventoryService.gI().findItemBag(player, 1029);
            if (loNuocPhep == null || loNuocPhep.quantity < soLan) return;

            long vangCan = 500_000_000L * soLan;
            if (player.inventory.gold < vangCan) return;

            int thanhCong = 0;
            for (int i = 0; i < soLan; i++) {
                if (Util.isTrue(50, 100)) thanhCong++;
            }

            if (thanhCong > 0) {
                Item nrSC = ItemService.gI().createNewItem((short) 1015);
                nrSC.quantity = thanhCong;
                nrSC.itemOptions.add(new ItemOption(30, 0));
                nrSC.itemOptions.add(new ItemOption(87, 0));
                InventoryService.gI().addItemBag(player, nrSC);
                CombineService.gI().sendEffectCombineDB(player, nrSC.template.iconID);
            } else {
                CombineService.gI().sendEffectFailCombine(player);
            }

            InventoryService.gI().subQuantityItemsBag(player, item, soLan * SO_NGOC_CAN);
            InventoryService.gI().subQuantityItemsBag(player, loNuocPhep, soLan);
            player.inventory.gold -= vangCan;

            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            CombineService.gI().reOpenItemCombine(player);
        }
    }

    private static boolean isValid(Item item) {
        return item != null
                && item.isNotNullItem()
                && item.template.id >= 14
                && item.template.id <= 20
                && item.quantity >= SO_NGOC_CAN;
    }
}
