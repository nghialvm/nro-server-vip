package event.MidAutumnFestival;



import QuanLiBoss.BossID;
import event.Event;

public class TrungThu extends Event {
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void boss() {
        createBoss(BossID.THO_DAI_KA, 10);
        createBoss(BossID.GOGETA, 5);
        createBoss(BossID.NGUYETTHAN, 5);
    }
    
    @Override
    public void npc() {
        createNpc(0, 41, 550, 432);
        createNpc(7, 41, 570, 432);
        createNpc(14, 41, 290, 408);
        createNpc(0, 92, 720, 432);
        createNpc(7, 92, 210, 432);
        createNpc(14, 92, 755, 408);
        createNpc(0, 66, 830, 432);
        createNpc(7, 66, 1080, 432);
        createNpc(14, 66, 480, 408);
        createNpc(5, 93, 900, 408);
        createNpc(13, 93, 950, 384);
        createNpc(20, 93, 1115, 360);
    }
}






