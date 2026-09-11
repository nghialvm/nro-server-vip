package event.List.WomensDay;

import event.Event;


public class WomensDay extends Event {
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void npc() {
        createNpc(5, 100, 900, 408);
        createNpc(13, 100, 950, 384);
        createNpc(20, 100, 1100, 360);
    }
}






