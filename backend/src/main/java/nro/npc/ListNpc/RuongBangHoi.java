package nro.npc.ListNpc;



import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Service;

public class RuongBangHoi extends Npc {

    public RuongBangHoi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (player.clan == null) {
                Service.gI().sendThongBao(player, "Không thể thực hiện!");
                return;
            }
            player.typeBox = 1;
            InventoryService.gI().sendItemBoxClan(player);
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {

        }
    }
}
