package models.Reward;

import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import Utils.TimeUtil;
import java.util.Date;


public class RewardBlackBall {

    private static final int TIME_REWARD = 79200000; // 22 giờ

    // Các chỉ số buff theo từng sao
    public static final int R1S_1 = 20;
    public static final int R1S_2 = 21;
    public static final int R2S_1 = 35;
    public static final int R2S_2 = 20;
    public static final int R3S_1 = 35;
    public static final int R3S_2 = 10;
    public static final int R4S_1 = 10;
    public static final int R4S_2 = 35;
    public static final int R5S_1 = 35;
    public static final int R5S_2 = 20;
    public static final int R5S_3 = 20;
    public static final int R6S_1 = 40;
    public static final int R6S_2 = 20;
    public static final int R7S_1 = 14;
    public static final int R7S_2 = 15;

    public static final int TIME_WAIT = 3600000; // 1 giờ
    public static long time8h;
    private Player player;

    public long[] timeOutOfDateReward;
    public int[] quantilyBlackBall;
    public long[] lastTimeGetReward;

    public RewardBlackBall(Player player) {
        this.player = player;
        this.timeOutOfDateReward = new long[7];
        this.lastTimeGetReward = new long[7];
        this.quantilyBlackBall = new int[7];
        time8h = TimeUtil.getStartTimeBlackBallWar();
    }

    public void reward(byte star) {
        if (this.timeOutOfDateReward[star - 1] > time8h) {
            quantilyBlackBall[star - 1]++;
        }
        this.timeOutOfDateReward[star - 1] = System.currentTimeMillis() + TIME_REWARD;
        Service.gI().point(player);
    }

    public void getRewardSelect(byte select) {
        int index = 0;
        for (int i = 0; i < timeOutOfDateReward.length; i++) {
            if (timeOutOfDateReward[i] > System.currentTimeMillis()) {
                index++;
                if (index == select + 1) {
                    getReward(i + 1);
                    break;
                }
            }
        }
    }

    private void getReward(int star) {
        if (timeOutOfDateReward[star - 1] > System.currentTimeMillis()
                && Util.canDoWithTime(lastTimeGetReward[star - 1], TIME_WAIT)) {

            // FIX: cập nhật lại thời gian nhận
            lastTimeGetReward[star - 1] = System.currentTimeMillis();
            quantilyBlackBall[star - 1]++;

            switch (star) {
                case 1:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 1 sao tự động nhận");
                    break;
                case 2:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 2 sao tự động nhận");
                    break;
                case 3:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 3 sao tự động nhận");
                    break;
                case 4:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 4 sao tự động nhận");
                    break;
                case 5:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 5 sao tự động nhận");
                    break;
                case 6:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 6 sao tự động nhận");
                    break;
                case 7:
                    Service.getInstance().sendThongBao(player, "Phần thưởng chỉ số 7 sao tự động nhận");
                    break;
            }

            Service.gI().point(player);

        } else {
            Service.getInstance().sendThongBao(player,
                    "Chưa thể nhận phần quà ngay lúc này, vui lòng đợi "
                    + TimeUtil.diffDate(new Date(lastTimeGetReward[star - 1]),
                            new Date(lastTimeGetReward[star - 1] + TIME_WAIT),
                            TimeUtil.MINUTE) + " phút nữa");
        }
    }

    public void dispose() {
        this.player = null;
    }
}
