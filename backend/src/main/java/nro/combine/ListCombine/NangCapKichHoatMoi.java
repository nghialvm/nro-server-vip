package nro.combine.ListCombine;

import Utils.Util;
import consts.ConstNpc;
import models.Item.ActivationSetCatalog;
import models.Item.Item;
import nro.combine.CombineService;
import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;

/** Combine flow for the complete new activation-set pool. */
public final class NangCapKichHoatMoi {

    private static final long COST = 500_000_000L;

    private NangCapKichHoatMoi() {
    }

    public static void showInfoCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine == null
                || player.combine.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.IGNORE_MENU,
                    "Can dung 3 mon do Huy Diet de doi Kich Hoat moi.",
                    "Dong"
            );
            return;
        }

        if (!hasThreeDestroyItems(player)) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.IGNORE_MENU,
                    "Can 3 mon trang bi Huy Diet.",
                    "Dong"
            );
            return;
        }

        if (player.inventory.gold < COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player,
                    ConstNpc.IGNORE_MENU,
                    "Can 500 trieu vang.",
                    "Dong"
            );
            return;
        }

        String text = "Doi 3 mon Huy Diet thanh trang bi Kich Hoat moi\n"
                + "Can 500 trieu vang\n"
                + "Ty le thanh cong: 70%\n"
                + "That bai se mat nguyen lieu va vang.";
        CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.MENU_START_COMBINE,
                text,
                "Doi\n500 trieu",
                "Tu choi"
        );
    }

    public static void startCombine(Player player) {
        if (player.combine == null || player.combine.itemsCombine == null
                || player.combine.itemsCombine.size() != 3 || !hasThreeDestroyItems(player)) {
            Service.gI().sendThongBao(player, "Can dung 3 mon trang bi Huy Diet.");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Can it nhat 1 o trong hanh trang.");
            return;
        }

        if (player.inventory.gold < COST) {
            Service.gI().sendThongBao(player, "Khong du vang.");
            return;
        }

        player.inventory.gold -= COST;
        Item first = player.combine.itemsCombine.get(0);
        Item second = player.combine.itemsCombine.get(1);
        Item third = player.combine.itemsCombine.get(2);

        if (!Util.isTrue(70, 100)) {
            consumeInputs(player, first, second, third);
            CombineService.gI().sendEffectFailCombine(player);
            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Doi Kich Hoat moi that bai.");
            reopen(player);
            return;
        }

        ActivationSetCatalog.SetDefinition definition = ActivationSetCatalog.randomNewSet();
        Item item = NangCapKichHoatVip.createVipActivationItem(player, definition);
        if (item == null) {
            player.inventory.gold += COST;
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Khong tao duoc trang bi, vui long thu lai.");
            reopen(player);
            return;
        }

        consumeInputs(player, first, second, third);
        InventoryService.gI().addItemBag(player, item);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);
        CombineService.gI().sendEffectSuccessCombine(player);
        Service.gI().sendThongBao(player,
                "Ban nhan duoc " + item.template.name + " Kich Hoat moi.");
        reopen(player);
    }

    private static boolean hasThreeDestroyItems(Player player) {
        return player.combine.itemsCombine.stream()
                .allMatch(item -> item != null && item.isDHD1());
    }

    private static void consumeInputs(Player player, Item first, Item second, Item third) {
        InventoryService.gI().subQuantityItemsBag(player, first, 1);
        InventoryService.gI().subQuantityItemsBag(player, second, 1);
        InventoryService.gI().subQuantityItemsBag(player, third, 1);
    }

    private static void reopen(Player player) {
        player.combine.itemsCombine.clear();
        CombineService.gI().reOpenItemCombine(player);
    }
}
