package nro.shop;



import nro.player.Player;
import models.Item.Item;
import models.Item.ItemService;
import nro.services.Service;

public class BuyBack {

    private static final byte MAX_ITEM_IN_BOX = 20;

    public static final byte MAX_COUNT_IN_BOX = 20;

    private static BuyBack i;

    public static BuyBack gI() {
        if (i == null) {
            i = new BuyBack();
        }
        return i;
    }

    public void addItem(Player player, Item item) {
        if (player.inventory.itemsDaBan.size() + 1 > MAX_ITEM_IN_BOX) {
            player.inventory.itemsDaBan.remove(0);
        }

        Item itemmua = ItemService.gI().copyItem(item);
        player.inventory.itemsDaBan.add(itemmua);

        Service.gI().sendThongBao(
                player,
                "CHÚ Ý: DANH SÁCH MUA LẠI [" + player.inventory.itemsDaBan.size() + "/20]"
        );

        if (player.iDMark != null && player.iDMark.getTagNameShop().equals("ITEMS_DABAN")) {
            ShopService.gI().opendShop(player, "ITEMS_DABAN", true);
        }
    }
}