package nro.combine.ListCombine;

import Utils.Util;
import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import nro.combine.CombineService;

public class TinhAnTrangBi {

    private static boolean isTrangBiAn(Item item) {
        if (item == null || !item.isNotNullItem()) {
            return false;
        }
        return item.isDTL() || item.isDHD();
    }

    public static void showInfoCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Hành trang cần ít nhất 1 chỗ trống", "Đóng");
            return;
        }

        if (player.combine.itemsCombine.size() != 2) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Cần bỏ đủ vật phẩm yêu cầu", "Đóng");
            return;
        }

        Item item = player.combine.itemsCombine.get(0);
        Item dangusac = player.combine.itemsCombine.get(1);

        if (!isTrangBiAn(item)) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Vật phẩm này không thể hóa ấn", "Đóng");
            return;
        }

        if (dangusac == null || !dangusac.isNotNullItem()
                || !(dangusac.template.id == 1724 || dangusac.template.id == 1725 || dangusac.template.id == 1726)
                || dangusac.quantity < 99) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                    "Bạn chưa bỏ đủ vật phẩm !!!", "Đóng");
            return;
        }

        String loaiTrangBi = item.isDTL() ? "thần linh" : (item.isDHD() ? "hủy diệt" : "này");

        String npcSay = item.template.name + "\n|2|";
        for (ItemOption io : item.itemOptions) {
            npcSay += io.getOptionString() + "\n";
        }

        npcSay += "|1|Con có muốn biến trang bị " + loaiTrangBi + " " + item.template.name + " thành\n"
                + "trang bị Ấn không?\n|4|Tỉ lệ thành công 100%\n"
                + "|7|Cần 99 " + dangusac.template.name;

        CombineService.gI().baHatMit.createOtherMenu(player,
                ConstNpc.MENU_START_COMBINE, npcSay, "Làm phép", "Từ chối");
    }

    public static void startCombine(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            return;
        }

        if (player.combine.itemsCombine.size() < 2) {
            return;
        }

        Item item = player.combine.itemsCombine.get(0);
        Item dangusac = player.combine.itemsCombine.get(1);

        if (!isTrangBiAn(item)) {
            Service.gI().sendThongBao(player, "Không thể tinh ấn vật phẩm này");
            return;
        }

        if (dangusac == null || !dangusac.isNotNullItem()
                || !(dangusac.template.id == 1724 || dangusac.template.id == 1725 || dangusac.template.id == 1726)
                || dangusac.quantity < 99) {
            Service.gI().sendThongBao(player, "Thiếu đá ngũ sắc");
            return;
        }

        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 34
                    || io.optionTemplate.id == 35
                    || io.optionTemplate.id == 36) {
                Service.gI().sendThongBao(player, "Trang bị đã có ấn rồi");
                return;
            }
        }

        boolean isSuccess = Util.nextInt(100) < 100;

        InventoryService.gI().subQuantityItemsBag(player, dangusac, 99);

        if (isSuccess) {
            switch (dangusac.template.id) {
                case 1724:
                    item.itemOptions.add(new ItemOption(34, 1));
                    break;
                case 1725:
                    item.itemOptions.add(new ItemOption(35, 1));
                    break;
                case 1726:
                    item.itemOptions.add(new ItemOption(36, 1));
                    break;
                default:
                    break;
            }

            CombineService.gI().sendEffectSuccessCombine(player);
            Service.gI().sendThongBao(player, "Tinh ấn thành công!");
        } else {
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            CombineService.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Tinh ấn thất bại! Trang bị đã bị phá hủy.");
        }

        InventoryService.gI().sendItemBag(player);
        CombineService.gI().reOpenItemCombine(player);
    }
}