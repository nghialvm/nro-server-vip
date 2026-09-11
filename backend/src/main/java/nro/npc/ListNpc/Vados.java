package nro.npc.ListNpc;



import nro.services.NpcService;
import consts.ConstNpc;
import models.Item.Item;
import models.Item.ItemService;
import nro.inventory.InventoryService;
import nro.npc.Npc;
import nro.player.Player;

public class Vados extends Npc {

    public Vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            createOtherMenu(player, ConstNpc.BASE_MENU,
                "|2|Ta là người dẫn đường cho những bậc Cha Mẹ đạt đến đỉnh cao sức mạnh.\n"
                + "|7|Bố mẹ ngươi không chỉ là người hỗ trợ… mà có thể trở thành huyền thoại.\n"
                + "|1|Muốn giúp họ đột phá? Hoặc nâng cấp kỹ năng chiến đấu?",
                "Đột phá\nsức mạnh", "Nâng cấp\nkỹ năng", "Đóng");
        }
    }
    
    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.isBaseMenu()) {
                switch (select) {
                    case 0:
                        createOtherMenu(player, 0,
                            "|2|Ta là người dẫn đường cho những bậc Cha Mẹ đạt đến đỉnh cao sức mạnh.\n"
                            + "|7|Bố mẹ ngươi không chỉ là người hỗ trợ… mà có thể trở thành huyền thoại.\n"
                            + "|1|Muốn giúp họ đột phá? Hoặc nâng cấp kỹ năng chiến đấu?",
                            "Đột phá\nsức mạnh", "Nâng cấp\nkỹ năng", "Đóng");
                        break;
                }
            }
        }
    }
}
