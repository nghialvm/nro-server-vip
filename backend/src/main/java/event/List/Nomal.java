package event.List;



import QuanLiBoss.BossID;
import event.Event;
import event.EventManager;

public class Nomal extends Event {

    @Override
    public void boss() {
        if (EventManager.LUNNAR_NEW_YEAR || EventManager.CHRISTMAS || EventManager.VU_LAN_FESTIVAL || EventManager.HALLOWEEN || EventManager.INTERNATIONAL_WOMANS_DAY) {
            createBoss(BossID.BROLY, 50);
            createBoss(BossID.BROLY_ZONE_0, 5);
            createBoss(BossID.SOI_HEC_QUYN_NOMAL, 4);
            createBoss(BossID.O_DO_NOMAL, 4);
            createBoss(BossID.AN_TROM_NOMAL, 4);
            createBoss(BossID.XIN_BA_TO_NOMAL, 4);
            createBoss(BossID.VIRUS_NOMAL, 5);
            createBoss(BossID.RAI_TI_NOMAL, 2);
        } else {
            createBoss(BossID.BROLY, 80);
            createBoss(BossID.BROLY_ZONE_0, 15);
            createBoss(BossID.SOI_HEC_QUYN_NOMAL, 7);
            createBoss(BossID.O_DO_NOMAL, 7);
            createBoss(BossID.AN_TROM_NOMAL, 7);
            createBoss(BossID.XIN_BA_TO_NOMAL, 7);
            createBoss(BossID.VIRUS_NOMAL, 10);
            createBoss(BossID.RAI_TI_NOMAL, 4);
        }
    }
}






