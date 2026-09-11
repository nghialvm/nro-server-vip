package nro.pariry;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;



public class PariryServices {

    private static PariryServices instance;

    public static PariryServices gI() {
        if (instance == null) {
            instance = new PariryServices();
        }
        return instance;
    }

    public void addPlayerEven(Player player) {
        if (pariryManager.currentPariry != null) {
            pariryManager.gI().addPlayerEven(player);
        }
    }

    public void addPlayerOdd(Player player) {
        if (pariryManager.currentPariry != null) {
            pariryManager.gI().addPlayerOdd(player);
        }
    }

    public void rewardRuby(Player player) {
        if (player != null) {
            Item tvthang = ItemService.gI().createNewItem((short) 457);
            tvthang.quantity = (int) Math.round(player.rubyWin * 1.5);
            if (tvthang.template.id == 457) {
                tvthang.itemOptions.add(new ItemOption(30, 1));
                tvthang.itemOptions.add(new ItemOption(93, 10));
            }
            InventoryService.gI().addItemBag(player, tvthang);
            InventoryService.gI().sendItemBag(player);
            pariryManager.gI().setAfterPlayerReward(player);
            Service.gI().sendThongBao(player, "Bạn đã nhận được " + tvthang.quantity + " thỏi vàng");
            player.rubyWin = 0;
        }
    }

    public boolean checkHavePariry() {
        return pariryManager.currentPariry != null;
    }

    public String getHistoryPlayer(Player player) {
        return pariryManager.gI().getHistoryPlayer(player);
    }

    public String getHistory() {
        return pariryManager.gI().getHistoryGame();
    }
}
