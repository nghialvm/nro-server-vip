package nro.npc.ListNpc;



import QuanLiBoss.Manager.BossManager;
import nro.services.NpcService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import nro.npc.Npc;
import nro.player.Player;

public class Potage extends Npc {

    public Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (canOpenNpc(player)) {
            if (this.mapId == 140) {
                Player BossClone = BossManager.gI().findBossClone(player);
                if (BossClone != null) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                    "Đang có 1 nhân bản của " + BossClone.name + " hãy chờ kết quả trận đấu",
                    "OK");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Hãy giúp ta đánh bại bản sao\nNgươi chỉ có 5 phút để hạ hắn\nPhần thưởng cho ngươi là 1 bình Commeson",
                    "Hướng\ndẫn\nthêm", "OK", "Từ chối");
                }
            }
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (canOpenNpc(player)) {
            if (this.mapId == 140) {
                if (player.iDMark.isBaseMenu()) {
                    Player BossClone = BossManager.gI().findBossClone(player);
                    if (BossClone == null) {
                        switch (select) {
                            case 0:
                                NpcService.gI().createTutorial(player, tempId, this.avartar, 
                                        "Thứ bị phong ấn tại đây là vũ khí có tên Commeson, được tạo ra nhằm bảo vệ cho hành tinh Potaufeu."
                                                + "\nTuy nhiên nó đã tàn phá mọi thứ trong quá khứ"
                                                + "\nKhiến cư dân Potaufeu niêm phong nó với cái giá phải trả là mạng sống của họ"
                                                + "\nPotage là người duy nhất sống sót và hắn ta đã bảo vệ phong ấn hơn một trăm năm."
                                                + "\nTuy nhiên bọn xâm lượt Gryll đã đến và giải thoát Commeson."
                                                + "\nHãy giúp ta tiêu diệt bản sao do Commeson tạo ra và niêm phong Commeson một lần và mãi mãi");
                                break;
                            case 1:
                                if (!Util.isAfterMidnight(player.lastPkCommesonTime) && !player.isFounder()) {
                                    Service.gI().sendThongBao(player, "Hãy chờ đến ngày mai");
                                } else {
                                    Service.gI().callNhanBan(player);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

}
