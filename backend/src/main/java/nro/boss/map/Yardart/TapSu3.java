package nro.boss.map.Yardart;



import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.YARDART;
import QuanLiBoss.BossesData;

public class TapSu3 extends Yardart {

    public TapSu3() throws Exception {
        super(YARDART, BossID.TAP_SU_3, BossesData.TAP_SU_3);
    }

    @Override
    protected void init() {
        x = 787;
        x2 = 857;
        y = 456;
        y2 = 408;
        range = 1000;
        range2 = 150;
        timeHoiHP = 30000;
        rewardRatio = 5;
    }
}






