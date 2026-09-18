package nro.services;

import Utils.FormatStyle;
import Utils.Util;
import models.Item.ItemTimeService;
import nro.player.NPoint;
import nro.player.Detu;
import nro.player.Player;
import nro.power.PowerLimit;
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
        if (!isValid(player)) {
            return false;
        }
        synchronized (player) {
            if (player.nPoint.limitPower >= NPoint.MAX_LIMIT) {
                sendToOwner(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
                return false;
            }

            PowerLimit nextLimit = getNextLimit(player);
            if (nextLimit == null) {
                sendToOwner(player, "Dữ liệu giới hạn sức mạnh chưa được cập nhật đầy đủ");
                return false;
            }
            if (player.itemTime.isOpenPower) {
                sendToOwner(player, "Bạn đang mở giới hạn sức mạnh, hãy chờ hoàn tất");
                return false;
            }
            if (!player.nPoint.canOpenPower()) {
                sendNotEnoughPower(player);
                return false;
            }

            player.itemTime.isOpenPower = true;
            player.itemTime.lastTimeOpenPower = System.currentTimeMillis();
            ItemTimeService.gI().sendAllItemTime(player);

            // Giữ hành vi hiện tại: giới hạn được tăng ngay khi bắt đầu mở.
            // Bộ đếm ItemTime chỉ theo dõi trạng thái mở, không được mở thêm
            // một bậc ngoài thao tác của người chơi.
            advanceLimit(player, nextLimit);

            Service.gI().sendThongBao(player, "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
            return true;
        }
    }

    public boolean openPowerSpeed(Player player) {
        if (!isValid(player)) {
            return false;
        }
        synchronized (player) {
            if (player.nPoint.limitPower >= NPoint.MAX_LIMIT) {
                sendToOwner(player, "Sức mạnh của bạn đã đạt tới mức tối đa");
                return false;
            }

            PowerLimit nextLimit = getNextLimit(player);
            if (nextLimit == null) {
                sendToOwner(player, "Dữ liệu giới hạn sức mạnh chưa được cập nhật đầy đủ");
                return false;
            }
            if (!player.nPoint.canOpenPower()) {
                sendNotEnoughPower(player);
                return false;
            }

            advanceLimit(player, nextLimit);
            sendToOwner(player, player.isDeTu
                    ? "Giới hạn sức mạnh của đệ tử đã được tăng lên 1 bậc"
                    : "Giới hạn sức mạnh của bạn đã được tăng lên 1 bậc");
            return true;
        }
    }

    private boolean isValid(Player player) {
        return player != null && player.nPoint != null;
    }

    private PowerLimit getNextLimit(Player player) {
        return PowerLimitManager.getInstance().get(player.nPoint.limitPower + 1);
    }

    private void advanceLimit(Player player, PowerLimit nextLimit) {
        player.nPoint.limitPower++;
        player.nPoint.powerLimit = nextLimit;
    }

    private Player getOwner(Player player) {
        if (player != null && player.isDeTu && player instanceof Detu) {
            return ((Detu) player).master;
        }
        return player;
    }

    private void sendToOwner(Player player, String message) {
        Player owner = getOwner(player);
        if (owner != null) {
            Service.gI().sendThongBao(owner, message);
        }
    }

    private void sendNotEnoughPower(Player player) {
        Player owner = getOwner(player);
        if (owner == null) {
            return;
        }
        long required = player.nPoint.getPowerLimit();
        Service.gI().sendThongBao(owner,
                "Sức mạnh hiện tại chưa đạt giới hạn "
                + Util.formatNumber(required, FormatStyle.VIETNAMESE)
                + " để mở bậc kế tiếp");
    }
}
