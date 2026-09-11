package event.List.InternationalWomensDay;



import event.Event;
import jbcd.dao.EventDAO;

public class InternationalWomensDay extends Event {

    @Override
    public void init() {
        super.init();
        EventDAO.loadInternationalWomensDayEvent();
    }

    @Override
    public void npc() {
        createNpc(5, 90, 900, 408);
        createNpc(13, 90, 950, 384);
        createNpc(20, 90, 1100, 360);
        createNpc(42, 91, 1000, 408);
    }
}






