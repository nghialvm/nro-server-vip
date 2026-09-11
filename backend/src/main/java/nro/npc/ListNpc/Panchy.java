package nro.npc.ListNpc;



import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;
import nro.shop.ShopService;

public class Panchy extends Npc {

    public Panchy(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào cưng, tôi là Panchy\n"
                    + "Tôi có thể giúp gì cho cưng?",
                    "Cửa hàng\nbiểu tượng\nbang", "Cửa hàng\nbùa\nbang", "OK");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        ShopService.gI().opendShop(player, "SHOP_BIEU_TUONG_BANG", true);
                        break;
                    case 1:
                        ShopService.gI().opendShop(player, "SHOP_BUA_BANG", true);
                        break;
                }
            }
        }
    }
}
