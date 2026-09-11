package nro.combine.ListCombine;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.combine.CombineService;

public class NangCapKichHoatVip {

    private static final int COST = 500_000_000;

    private static boolean isVipLastItem(int tempId) {
        int[] vipIds = {
            555,557,559,
            556,558,560,
            562,564,566,
            563,565,567,
            561
        };
        for (int id : vipIds) {
            if (id == tempId) return true;
        }
        return false;
    }
    public static void showInfoCombine(Player player) {
        if (player.combine.itemsCombine.isEmpty()) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Hãy đưa ta 3 món huỷ diệt....", "Đóng");
            return;
        }

        if (player.combine.itemsCombine.size() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Cần đúng 3 món huỷ diệt", "Đóng");
            return;
        }

        if (player.combine.itemsCombine.stream()
                .filter(item -> item.isNotNullItem() && item.isDHD1())
                .count() != 3) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Thiếu đồ huỷ diệt rồi", "Đóng");
            return;
        }

        if (player.inventory.gold < COST) {
            CombineService.gI().baHatMit.createOtherMenu(
                    player, ConstNpc.IGNORE_MENU,
                    "Hết tiền rồi\nẢo ít thôi con", "Đóng");
            return;
        }

        String npcSay =
                "|2|Con có muốn đổi các món nguyên liệu ?\n|7|" +
                "Nhận trang bị kích hoạt VIP\n" +
                "|1|Cần " + Util.numberToMoney(COST) + " vàng";

        CombineService.gI().baHatMit.createOtherMenu(
                player,
                ConstNpc.MENU_START_COMBINE,
                npcSay,
                "Nâng cấp\n" + Util.numberToMoney(COST) + " vàng",
                "Từ chối"
        );
    }
    public static void startCombine(Player player) {
        if (player.combine.itemsCombine.size() != 3) return;

        Item i1 = player.combine.itemsCombine.get(0);
        Item i2 = player.combine.itemsCombine.get(1);
        Item i3 = player.combine.itemsCombine.get(2);

        if (!(i1.isDHD1() && i2.isDHD1() && i3.isDHD1())) {
            Service.gI().sendThongBao(player, "Cần 3 món huỷ diệt cùng loại");
            return;
        }

        if (InventoryService.gI().getCountEmptyBag(player) <= 0) {
            Service.gI().sendThongBao(player, "Cần ít nhất 1 ô trống hành trang");
            return;
        }

        if (player.inventory.gold < COST) {
            Service.gI().sendThongBao(player, "Không đủ vàng");
            return;
        }
        player.inventory.gold -= COST;
        boolean success = Util.isTrue(70, 100);
        if (!success) {
           
            CombineService.gI().sendEffectFailCombine(player);
        

            InventoryService.gI().subQuantityItemsBag(player, i1, 1);
            InventoryService.gI().subQuantityItemsBag(player, i2, 1);
            InventoryService.gI().subQuantityItemsBag(player, i3, 1);

            InventoryService.gI().sendItemBag(player);
            Service.gI().sendMoney(player);
            Service.gI().sendThongBao(player, "Thất bại");
           
            player.combine.itemsCombine.clear();
            CombineService.gI().reOpenItemCombine(player);
            return;
        }
        int[][][] items = {
            { 
                {0,3,33,34,136,137,138,139,230,231,232,233,555},
                {6,9,35,36,140,141,142,143,242,243,244,245,556},
                {21,24,37,38,144,145,146,147,254,255,256,257,562},
                {27,30,39,40,148,149,150,151,266,267,268,269,563},
                {12,57,58,59,184,185,186,187,278,279,280,281,561}
            },
            { 
                {1,4,41,42,152,153,154,155,234,235,236,237,557},
                {7,10,43,44,156,157,158,159,246,247,248,249,558},
                {22,25,45,46,160,161,162,163,258,259,260,261,564},
                {28,31,47,48,164,165,166,167,270,271,272,273,565},
                {12,57,58,59,184,185,186,187,278,279,280,281,561}
            },
            { 
                {2,5,49,50,168,169,170,171,238,239,240,241,559},
                {8,11,51,52,172,173,174,175,250,251,252,253,560},
                {23,26,53,54,176,177,178,179,262,263,264,265,566},
                {29,32,55,56,180,181,182,183,274,275,276,277,567},
                {12,57,58,59,184,185,186,187,278,279,280,281,561}
            }
        };

        int[][] options = {
            {128,129,127},
            {130,131,132},
            {133,135,134}
        };

        int rd = Util.nextInt(1, 100);
        int skhId = (rd <= 25) ? 0 : (rd <= 60 ? 1 : 2);

        int[] arr = items[player.gender][i1.template.type];
        int tempId = arr[Util.nextInt(arr.length)];

        Item item = ItemService.gI().itemSKH(
                tempId,
                options[player.gender][skhId]
        );

        if (item != null && isVipLastItem(item.template.id)) {
            int value;
            switch (item.template.type) {
                case 0:
                    value = Util.nextInt(1200, 1400);
                    item.itemOptions.add(0, new ItemOption(47, value));
                    break;
                case 1:
                    value = Util.nextInt(48000, 55000);
                    item.itemOptions.add(0, new ItemOption(6, value));
                    break;
                case 2:
                    value = Util.nextInt(3500, 4000);
                    item.itemOptions.add(0, new ItemOption(0, value));
                    break;
                case 3:
                    value = Util.nextInt(48000, 55000);
                    item.itemOptions.add(0, new ItemOption(7, value));
                    break;
                case 4:
                    value = Util.nextInt(13, 15);
                    item.itemOptions.add(0, new ItemOption(14, value));
                    break;
            }
        }

        CombineService.gI().sendEffectSuccessCombine(player);
        InventoryService.gI().addItemBag(player, item);

        InventoryService.gI().subQuantityItemsBag(player, i1, 1);
        InventoryService.gI().subQuantityItemsBag(player, i2, 1);
        InventoryService.gI().subQuantityItemsBag(player, i3, 1);

        InventoryService.gI().sendItemBag(player);
        Service.gI().sendMoney(player);

        Service.gI().sendThongBao(player,
                "Bạn nhận được " + item.template.name + " kích hoạt VIP");

        player.combine.itemsCombine.clear();
        CombineService.gI().reOpenItemCombine(player);
    }
}
