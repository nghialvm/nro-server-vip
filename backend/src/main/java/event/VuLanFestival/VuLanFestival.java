package event.VuLanFestival;



import QuanLiBoss.BossID;
import event.Event;

import QuanLiBoss.BossID;

import QuanLiBoss.BossID;

import QuanLiBoss.BossID;

public class VuLanFestival extends Event {
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void npc() {
        createNpc(5, 83, 900, 408);
        createNpc(13, 83, 950, 384);
        createNpc(20, 83, 1100, 360);
    }

    @Override
    public void boss() {
        createBoss(BossID.PIKKON, 3);
    }
}






