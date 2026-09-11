package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstFont;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.combine.CombineService;

public class DoiSachTuyetKy {

    public static void showCombine(Player player) {
        Item cuonSachCu = InventoryService.gI().findItemBag(player, 1283);
        Item kimBamGiay = InventoryService.gI().findItemBag(player, 1285);
        int quantityCuonSachCu = cuonSachCu != null ? cuonSachCu.quantity : 0;
        int quantityKimBamGiay = kimBamGiay != null ? kimBamGiay.quantity : 0;
        StringBuilder text = new StringBuilder();
        text.append(ConstFont.BOLD_GREEN).append("Đổi sách Tuyệt Kỹ 1\n");
        text.append(quantityCuonSachCu >= 10 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Cuốn sách cũ ").append(quantityCuonSachCu).append("/10\n");
        text.append(quantityKimBamGiay >= 1 ? ConstFont.BOLD_BLUE : ConstFont.BOLD_RED).append("Kìm bấm giấy ").append(quantityKimBamGiay).append("/1\n");
        text.append(quantityCuonSachCu < 10 || quantityKimBamGiay < 1 ? ConstFont.BOLD_RED : ConstFont.BOLD_BLUE).append("Tỉ lệ thành công: 20%\n");
        if (quantityCuonSachCu < 10 || quantityKimBamGiay < 1) {
            CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, text.toString(), "Từ chối");
            return;
        }
        CombineService.gI().baHatMit.createOtherMenu(player, ConstNpc.DOI_SACH_TUYET_KY, text.toString(), "Đồng ý", "Từ chối");
    }

    public static void doiSachTuyetKy(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            Service.gI().sendThongBao(player, "Cần 1 ô trống trong hành trang.");
            return;
        }
        Item cuonSachCu = InventoryService.gI().findItemBag(player, 1283);
        Item kimBamGiay = InventoryService.gI().findItemBag(player, 1285);
        int quantityCuonSachCu = cuonSachCu != null ? cuonSachCu.quantity : 0;
        int quantityKimBamGiay = kimBamGiay != null ? kimBamGiay.quantity : 0;
        if (quantityCuonSachCu < 10 || quantityKimBamGiay < 1) {
            return;
        }
        CombineService.gI().sendAddItemCombine(player, ConstNpc.BA_HAT_MIT, cuonSachCu, kimBamGiay);
        int subCuonSach;
        if (Util.isTrue(50, 100)) {
            subCuonSach = 10;
            int[] sach = {1044, 1211, 1212};
            Item sachTuyetKy = ItemService.gI().createNewItem((short) sach[Util.nextInt(sach.length)]);
            for (int i = 0; i < (Util.isTrue(999, 1000) ? 1 : Util.nextInt(1, 3)); i++) {
                sachTuyetKy.itemOptions.add(new ItemOption(217, 0));
            }
            sachTuyetKy.itemOptions.add(new ItemOption(21, 40));
            sachTuyetKy.itemOptions.add(new ItemOption(30, 0));
            sachTuyetKy.itemOptions.add(new ItemOption(87, 0));
            sachTuyetKy.itemOptions.add(new ItemOption(219, 5));
            sachTuyetKy.itemOptions.add(new ItemOption(212, 1000));
            InventoryService.gI().addItemBag(player, sachTuyetKy);
            CombineService.gI().sendEffSuccessVip(player, sachTuyetKy.template.iconID);
            Util.setTimeout(() -> {
                Service.gI().sendServerMessage(player, "Bạn nhận được " + sachTuyetKy.template.name);
                CombineService.gI().baHatMit.npcChat(player, "Chúc mừng con nhé");
            }, 2000);
        } else {
            subCuonSach = 5;
            CombineService.gI().sendEffFailVip(player);
            Util.setTimeout(() -> {
                CombineService.gI().baHatMit.npcChat(player, "Chúc con may mắn lần sau, đừng buồn con nhé");
            }, 2000);
        }
        InventoryService.gI().subQuantityItemsBag(player, cuonSachCu, subCuonSach);
        InventoryService.gI().subQuantityItemsBag(player, kimBamGiay, 1);
        InventoryService.gI().sendItemBag(player);
    }
}
