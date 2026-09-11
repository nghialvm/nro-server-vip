package nro.boss.map.Yardart;



import QuanLiBoss.BossID;
import static QuanLiBoss.BossType.YARDART;
import QuanLiBoss.BossesData;

public class TapSu4 extends Yardart {

    public TapSu4() throws Exception {
        super(YARDART, BossID.TAP_SU_4, BossesData.TAP_SU_4);
    }

    @Override
    protected void init() {
        x = 993;
        x2 = 1063;
        y = 456;
        y2 = 456;
        range = 1000;
        range2 = 150;
        timeHoiHP = 30000;
        rewardRatio = 5;
    }
}






