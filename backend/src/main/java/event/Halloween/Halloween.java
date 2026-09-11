package event.Halloween;



import QuanLiBoss.BossID;
import event.Event;

public class Halloween extends Event {
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void npc() {
        createNpc(5, 84, 900, 408);
        createNpc(13, 84, 950, 384);
        createNpc(20, 84, 1100, 360);
    }

    @Override
    public void boss() {
        createBoss(BossID.XUONG_KHO, 15);
        createBoss(BossID.MATROI, 15);
        createBoss(BossID.DOI, 15);
        createBoss(BossID.BI_NGO, 1);
    }
}






