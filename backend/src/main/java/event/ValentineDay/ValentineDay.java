package event.ValentineDay;

import QuanLiBoss.BossID;
import event.Event;



public class ValentineDay extends Event {
    
    @Override
    public void init() {
        super.init();
    }
    
    @Override
    public void npc() {
        createNpc(0, 99, 550, 432);
        createNpc(7, 99, 570, 432);
        createNpc(14, 99, 290, 408);
    }
    
    @Override
    public void boss() {
        createBoss(BossID.THO_BUNMA, 10);
    }
    
}






