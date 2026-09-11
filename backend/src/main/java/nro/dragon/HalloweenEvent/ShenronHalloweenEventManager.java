package nro.dragon.HalloweenEvent;



import Utils.Functions;
import nro.server.Maintenance;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import Utils.Util;

public class ShenronHalloweenEventManager implements Runnable {

    private static ShenronHalloweenEventManager instance;
    private long lastUpdate;
    private static final List<ShenronHalloweenEvent> list = new ArrayList<>();;

    public static ShenronHalloweenEventManager gI() {
        if (instance == null) {
            instance = new ShenronHalloweenEventManager();
        }
        return instance;
    }

    @Override
    public void run() {
        while (!Maintenance.isRunning) {
            try {
                long start = System.currentTimeMillis();
                update();
                long timeUpdate = System.currentTimeMillis() - start;
                Functions.sleep(Math.max(1000 - timeUpdate, 10));
            } catch (Exception ex) {
            }
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastUpdate, 1000)) {
            lastUpdate = System.currentTimeMillis();
            List<ShenronHalloweenEvent> listCopy = new ArrayList<>();
            for (ShenronHalloweenEvent se : list) {
                listCopy.add(se);
            }

            for (ShenronHalloweenEvent se : listCopy) {
                try {
                    se.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listCopy.clear();
        }
    }

    public void add(ShenronHalloweenEvent se) {
        list.add(se);
    }

    public void remove(ShenronHalloweenEvent se) {
        list.remove(se);
    }

}







