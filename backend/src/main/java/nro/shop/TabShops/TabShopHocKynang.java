package nro.shop.TabShops;

import java.util.ArrayList;
import nro.player.Player;
import nro.shop.ItemShop;

public class TabShopHocKynang extends TabShop {

    public TabShopHocKynang(TabShop tabShop, Player player) {
        this.itemShops = new ArrayList<>();
        this.shop = tabShop.shop;
        this.id = tabShop.id;
        this.name = tabShop.name;

        for (ItemShop itemShop : tabShop.itemShops) {
            if (itemShop.temp.gender == player.gender || itemShop.temp.gender > 2) {
                boolean shouldAdd = true;
                for (Integer i : player.BoughtSkill) {//check xem co ki nang chua
                    if (itemShop.temp.id == i) {
                        shouldAdd = false;
                        break;
                    }
                }
                if (shouldAdd) {
                    
                    this.itemShops.add(new ItemShop(itemShop));
                }
            }
        }
    }
}





