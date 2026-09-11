package event.BlackFriday;

import event.Event;



public class BlackFriday extends Event {
    
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void npc() {
        createNpc(5, 96, 900, 408);
        createNpc(13, 96, 950, 384);
        createNpc(20, 96, 1100, 360);
    }
}






