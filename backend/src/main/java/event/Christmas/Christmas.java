package event.Christmas;



import QuanLiBoss.BossID;
import event.Event;
import jbcd.dao.EventDAO;

public class Christmas extends Event {
    
    @Override
    public void init() {
        super.init();
        EventDAO.loadChristMasEvent();
    }

    @Override
    public void npc() {
        createNpc(0, 81, 950, 432);
        createNpc(7, 81, 950, 432);
        createNpc(14, 81, 950, 408);
        createNpc(174, 81, 155, 408);
        createNpc(174, 82, 1510, 408);
    }

    @Override
    public void boss() {
        createBoss(BossID.ONG_GIA_NOEL, 10);
        createBoss(BossID.GOKU_NOEL, 2);
        createBoss(BossID.CHICHI_NOEL, 2);
        createBoss(BossID.GOKU_NOEL, 2);
        createBoss(BossID.CHICHI_NOEL, 2);
        createBoss(BossID.COLD_NOEL, 2);
        createBoss(BossID.GOKU_GOD_NOEL, 2);
        createBoss(BossID.TUAN_LOC, 15);
    }
}






