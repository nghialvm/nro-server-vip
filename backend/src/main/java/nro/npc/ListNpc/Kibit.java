package nro.npc.ListNpc;



import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;

public class Kibit extends Npc {

    public Kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            switch (this.mapId) {
                case 50:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                    "Đến\nKaio", "Từ chối");
                    break;
                case 52:
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                    "Từ chối");
                    break;
                case 114:
                    if (player.cFlag != 9) {
                        NpcService.gI().createTutorial(player, tempId, this.avartar, "Ngươi hãy về phe của mình mà thể hiện");
                        return;
                    }
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                    "Về nhà", "Từ chối");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 50) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                            break;
                    }
                }
            } else if (this.mapId == 114) {
                if (player.iDMark.isBaseMenu()) {
                    switch (select) {
                        case 0:
                            if (player.cFlag != 9) {
                                return;
                            }
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            break;
                    }
                }
            }
        }
    }
}
