package nro.dragon.ChristMasEvent;

import Utils.Functions;
import nro.server.Maintenance;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;

public class ShenronChristMasEventManager implements Runnable {

    private static ShenronChristMasEventManager instance;
    private long lastUpdate;
    private static final List<ShenronChristMasEvent> list = new ArrayList<>();

    ;

    public static ShenronChristMasEventManager gI() {
        if (instance == null) {
            instance = new ShenronChristMasEventManager();
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
            List<ShenronChristMasEvent> listCopy = new ArrayList<>(list);

            for (ShenronChristMasEvent se : listCopy) {
                try {
                    se.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            listCopy.clear();
        }
    }

    public void add(ShenronChristMasEvent se) {
        list.add(se);
    }

    public void remove(ShenronChristMasEvent se) {
        list.remove(se);
    }

}
