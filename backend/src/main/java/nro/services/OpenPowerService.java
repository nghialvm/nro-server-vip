package nro.services;

import models.Item.ItemTimeService;
import nro.player.NPoint;
import nro.player.Detu;
import nro.player.Player;
import nro.power.PowerLimitManager;

public class OpenPowerService {

    public static final int RUBY_SPEED_OPEN_LIMIT_POWER = 100;

    private static OpenPowerService i;

    private OpenPowerService() {

    }

    public static OpenPowerService gI() {
        if (i == null) {
            i = new OpenPowerService();
        }
        return i;
    }

    public boolean openPowerBasic(Player player) {
        byte curLimit = player.nPoint.limitPower;

        if (curLimit < NPoint.MAX_LIMIT) {
            if (!player.itemTime.isOpenPower && player.nPoint.canOpenPower()) {
                player.itemTime.isOpenPower = true;
                player.itemTime.lastTimeOpenPower = System.currentTimeMillis();
                ItemTimeService.gI().sendAllItemTime(player);

                // FIX: tăng limit và cập nhật powerLimit ngay
                player.nPoint.limitPower++;
                player.nPoint.powerLimit = PowerLimitManager.getInstance().get(player.nPoint.limitPower);

                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
                return true;
            } else {
                Service.gI().sendThongBao(player, "Sức mạnh của bạn không đủ để thực hiện");
                return false;
            }
        } else {
            Service.gI().sendThongBao(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
            return false;
        }
    }

    public boolean openPowerSpeed(Player player) {
        if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
            player.nPoint.limitPower++;

            if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                player.nPoint.limitPower = NPoint.MAX_LIMIT;
            }

            player.nPoint.powerLimit = PowerLimitManager.getInstance().get(player.nPoint.limitPower);

            if (!player.isDeTu) {
                Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
            } else {
                Service.gI().sendThongBao(((Detu) player).master, "Giới hạn sức mạnh của đệ tử đã được tăng lên 1 bậc");
            }

            return true;
        } else {
            if (!player.isDeTu) {
                Service.gI().sendThongBao(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
            } else {
                Service.gI().sendThongBao(((Detu) player).master, "Sức mạnh của đệ tử đã đạt tới mức tối đa");
            }

            return false;
        }
    }
}