package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.services.Service;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.npc.Npc;
import nro.npc.Special.MelonPlant;
import nro.player.Player;

public class DuaHau extends Npc {

    public DuaHau(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == (21 + player.gender)) {
                player.duahau.sendDuaHau();
                if (player.duahau.getSecondDone() != 0) {
                    this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_DUA, "Khi nào chín hãy thu hoạch và mang tôi đến gặp Vua Hùng để đổi quà nhé",
                            "Ok", "Từ chối");
                } else {
                    this.createOtherMenu(player, ConstNpc.CAN_OPEN_DUA, "Khi nào chín hãy thu hoạch và mang tôi đến gặp Vua Hùng để đổi quà nhé", 
                            "Thu hoạch", "Từ chối");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == (21 + player.gender)) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.CAN_OPEN_EGG: {
                        switch (select) {
                            case 0:
                                if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                                    Service.gI().sendThongBao(player, "Hàng trang đã đầy, cần một ô trống trong hành trang");
                                    return;
                                }
                                Item DuaHau = ItemService.gI().createNewItem((short) 569, 1);
                                DuaHau.addOptionParam(93, 35);
                                InventoryService.gI().addItemBag(player, DuaHau);
                                InventoryService.gI().sendItemBag(player);
                                Service.gI().sendThongBao(player, "Bạn nhận được " + DuaHau.Name());
                                if (player.duahau != null) {
                                    player.duahau.destroyDuaHau();
                                    MelonPlant.createDuaHau(player);
                                }
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }
}
