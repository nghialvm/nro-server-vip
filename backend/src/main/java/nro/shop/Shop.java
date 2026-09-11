package nro.shop;

import nro.player.Player;
import java.util.ArrayList;
import java.util.List;
import nro.shop.TabShops.TabShop;
import nro.shop.TabShops.TabShopDanhHieu;
import nro.shop.TabShops.TabShopHocKynang;
import nro.shop.TabShops.TabShopSoHuu;
import nro.shop.TabShops.TabShopUron;

public class Shop {

    public int id;

    public byte npcId;

    public List<TabShop> tabShops;

    public String tagName;

    public byte typeShop;

    public Shop() {
        this.tabShops = new ArrayList<>();
    }

    public Shop(Shop shop, Player player) {
        this.tabShops = new ArrayList<>();
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.tagName = shop.tagName;
        this.typeShop = shop.typeShop;
        for (TabShop tabShop : shop.tabShops) {
            switch (tabShop.id) {
                case 19:
                    this.tabShops.add(new TabShopUron(tabShop, player));
                    break;
                case 24:
                    this.tabShops.add(new TabShopDanhHieu(tabShop, player));
                    break;
                case 25:
                    this.tabShops.add(new TabShopSoHuu(tabShop, player));
                    break;
                case 21:
                case 22:
                case 23:
                    this.tabShops.add(new TabShopHocKynang(tabShop, player));
                    break;
                default:
                    this.tabShops.add(new TabShop(tabShop, player.gender));
                    break;
            }
        }
    }

    public Shop(Shop shop) {
        this.tabShops = new ArrayList<>();
        this.id = shop.id;
        this.npcId = shop.npcId;
        this.tagName = shop.tagName;
        this.typeShop = shop.typeShop;
        for (TabShop tabShop : shop.tabShops) {
            this.tabShops.add(new TabShop(tabShop));
        }
    }

    public ItemShop getItemShop(int temp) {
        for (TabShop tab : this.tabShops) {
            for (ItemShop is : tab.itemShops) {
                if (is.temp.id == temp) {
                    return is;
                }
            }
        }
        return null;
    }

    public void dispose() {
        if (this.tabShops != null) {
            for (TabShop ts : this.tabShops) {
                ts.dispose();
            }
            this.tabShops.clear();
        }
        this.tabShops = null;
    }

}





