//package nro.chanle;
//
//import Inventory.InventoryService;
//import Player.Player;
//import Services.Service;
//import models.Item.Item;
//import models.Item.ItemService;
//
//public class ChanLeServices {
//
//    private static ChanLeServices instance;
//
//    public static ChanLeServices gI() {
//        if (instance == null) {
//            instance = new ChanLeServices();
//        }
//        return instance;
//    }
//
//    public void addPlayerEven(Player player) {
//        if (ChanLeManager.currentPariry != null) {
//            ChanLeManager.gI().addPlayerEven(player);
//        }
//    }
//
//    public void addPlayerOdd(Player player) {
//        if (ChanLeManager.currentPariry != null) {
//            ChanLeManager.gI().addPlayerOdd(player);
//        }
//    }
//
//    public void rewardRuby(Player player) {
//        if (player != null) {
//            Item tvthang = ItemService.gI().createNewItem((short)457);
//            tvthang.quantity = (int) Math.abs(player.rubyWin * 1.5);
//            if (tvthang.template.id == 457) {
//                tvthang.itemOptions.add(new Item.ItemOption(30, 1));
//                tvthang.itemOptions.add(new Item.ItemOption(93, 10));
//            }
//             InventoryService.gI().addItemBag(player, tvthang);
//
//             InventoryService.gI().sendItemBags(player);
//
//            ChanLeManager.gI().setAfterPlayerReward(player);
//            Service.gI().sendThongBao(player, "Bạn đã nhận được " + tvthang.quantity + " thỏi vàng");
//            player.rubyWin = 0;
//        }
//    }
//
//    public boolean checkHavePariry() {
//        return ChanLeManager.currentPariry != null;
//    }
//
//    public String getHistoryPlayer(Player player) {
//        return ChanLeManager.gI().getHistoryPlayer(player);
//    }
//
//    public String getHistory() {
//        return ChanLeManager.gI().getHistoryGame();
//    }
//}
