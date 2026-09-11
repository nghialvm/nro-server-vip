package nro.player;

import consts.ConstPlayer;
import Utils.Util;
import lombok.Setter;


public class Fusion {

    public static final int TIME_FUSION = 600000;

    @Setter
    private Player player;
    public byte typeFusion;
    public long lastTimeFusion;

    public Fusion(Player player) {
        this.player = player;
    }

    public void update() {
        if (typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE && Util.canDoWithTime(lastTimeFusion, TIME_FUSION)) {
            this.player.Detu.unFusion();
        }
    }

    public void dispose() {
        this.player = null;
    }
}





