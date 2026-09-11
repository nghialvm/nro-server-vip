package nro.npc.ListNpc;

import Utils.Util;
import consts.ConstNpc;
import nro.minigame.ChanLe;
import nro.npc.Npc;
import nro.player.Player;
import nro.services.Fun.Input;
import nro.services.Service;

public class LyTieuNuong extends Npc {

    public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        super(mapId, status, cx, cy, tempId, avartar);
    }

    @Override
    public void openBaseMenu(Player player) {
        if (!canOpenNpc(player)) {
            return;
        }

        long remain = Math.max(0, (ChanLe.gI().lastTimeEnd - System.currentTimeMillis()) / 1000);
        String message = "Chan/Le\n"
                + "Dat cuoc bang Thoi Vang, tra thuong 1.9x\n\n"
                + "Tong Chan: " + Util.format(ChanLe.gI().goldChan) + " Thoi Vang\n"
                + "Tong Le: " + Util.format(ChanLe.gI().goldLe) + " Thoi Vang\n\n"
                + "Lich su gan day:\n" + ChanLe.gI().getHistoryGame() + "\n"
                + "So vua quay: " + ChanLe.gI().number + "\n"
                + "Ket qua moi sau " + remain + " giay";

        if (ChanLe.gI().isAllowBetting()) {
            createOtherMenu(player, ConstNpc.CHAN_LE, message,
                    "Cap nhat", "Bang xep hang", "Mua Chan", "Mua Le", "Dong");
        } else {
            createOtherMenu(player, ConstNpc.CHAN_LE, message,
                    "Cap nhat", "Bang xep hang", "Dong");
        }
    }

    @Override
    public void confirmMenu(Player player, int select) {
        if (!canOpenNpc(player) || player.iDMark.getIndexMenu() != ConstNpc.CHAN_LE) {
            return;
        }

        boolean allowBetting = ChanLe.gI().isAllowBetting();
        if (!allowBetting) {
            switch (select) {
                case 0 -> openBaseMenu(player);
                case 1 -> Service.gI().sendThongBao(player, "Chuc nang bang xep hang chua duoc cap nhat");
                case 2 -> Service.gI().sendThongBao(player, "Hen gap lai!");
                default -> {
                }
            }
            return;
        }

        switch (select) {
            case 0 -> openBaseMenu(player);
            case 1 -> Service.gI().sendThongBao(player, "Chuc nang bang xep hang chua duoc cap nhat");
            case 2 -> Input.gI().CHAN(player);
            case 3 -> Input.gI().LE(player);
            case 4 -> Service.gI().sendThongBao(player, "Hen gap lai!");
            default -> {
            }
        }
    }
}

