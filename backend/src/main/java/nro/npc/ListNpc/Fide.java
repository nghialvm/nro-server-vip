package nro.npc.ListNpc;



import nro.services.Service;
import nro.services.TaskService;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstTranhNgocNamek;
import nro.map.DragonNamecWar.TranhNgocService;
import nro.npc.Npc;
import nro.player.Player;

public class Fide extends Npc {

    public Fide(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            TaskService.gI().checkDoneTaskTalkNpc(player, this);
            if (mapId == ConstTranhNgocNamek.MAP_ID) {
                if (player.iDMark.getTranhNgoc() == 1) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Phắn đê! Ta không nói chuyện với sinh vật hạ đẳng", "Đóng");
                    return;
                }
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Hãy mang ngọc rồng về cho ta", "Đưa ngọc", "Đóng");
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                if (this.mapId == ConstTranhNgocNamek.MAP_ID) {
                    switch (select) {
                        case 0: {
                            if (player.iDMark.getTranhNgoc() == 2 && player.isHoldNamecBallTranhDoat) {
                                if (!Util.canDoWithTime(player.lastTimePickItem, 20000)) {
                                    Service.gI().sendThongBao(player, "Vui lòng đợi " + ((player.lastTimePickItem + 20000 - System.currentTimeMillis()) / 1000) + " giây để có thể trả");
                                    return;
                                }
                                TranhNgocService.getInstance().dropBall(player, (byte) 2);
                                player.zone.pointFide++;
                                if (player.zone.pointFide > ConstTranhNgocNamek.MAX_POINT) {
                                    player.zone.pointFide = ConstTranhNgocNamek.MAX_POINT;
                                }
                                TranhNgocService.getInstance().sendUpdatePoint(player);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
