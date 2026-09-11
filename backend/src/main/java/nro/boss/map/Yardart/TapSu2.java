package nro.boss.map.Yardart;



import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.YARDART;
import QuanLiBoss.BossesData;

public class TapSu2 extends Yardart {

    public TapSu2() throws Exception {
        super(YARDART, BossID.TAP_SU_2, BossesData.TAP_SU_2);
    }

    @Override
    protected void init() {
        x = 582;
        x2 = 652;
        y = 432;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 30000;
        rewardRatio = 5;
    }
}






