package nro.npc.ListNpc;



import Utils.FormatStyle;
import Utils.Util;
import consts.ConstNpc;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Service;

public class RuongSuuTap extends Npc {

    public RuongSuuTap(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Vàng bạc châu báu gì cứ yên tâm giao hết cho tôi",
                    "Mở rương", "Nâng cấp\nrương", "Từ chối");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            long cost = 0;
            long basePrice = 500_000_000L;
            int BoxCollectionSize = player.inventory.itemsBoxCollection.size();
            if (BoxCollectionSize >= 20) {
                cost = basePrice + (BoxCollectionSize - 20) * 500_000_000L;
            }
            if (cost <= 0) {
                cost = 0;
            }
            if (cost >= 10_000_000_000L) {
                cost = 10_000_000_000L;
            }
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0: {
                        player.typeBox = 2;
                        InventoryService.gI().sendItemBoxCollection(player);
                        break;
                    }
                    case 1: {
                        this.createOtherMenu(player, 0, "Bạn có chắc muốn mở thêm ô " + (BoxCollectionSize + 1) + " với giá " + Util.formatNumber(cost, FormatStyle.VIETNAMESE) + " vàng?",
                                "Đồng ý", "Từ chối");
                        break;
                    }
                }
            } else if (player.iDMark.getIndexMenu() == 0) {
                switch (select) {
                    case 0: {
                        if (player.inventory.itemsBoxCollection.size() >= 120) {
                            this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Rương sưu tập đã đạt mức tối đa",
                                    "Ok");
                            return;
                        }
                        if (player.inventory.getGold() > cost) {
                            player.inventory.subGold(cost);
                            Service.gI().sendMoney(player);
                            player.inventory.itemsBoxCollection.add(ItemService.gI().createItemNull());
                            Service.gI().sendThongBao(player, "Nâng cấp xong");
                        } else {
                            Service.gI().sendThongBao(player, "Bạn không đủ vàng, còn thiếu " + (Util.formatNumber(cost - player.inventory.getGold(), FormatStyle.VIETNAMESE)) + " vàng nữa");
                        }
                        break;
                    }
                }
            }
        }
    }
}
