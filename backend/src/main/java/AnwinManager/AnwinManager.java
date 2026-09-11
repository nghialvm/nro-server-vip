package AnwinManager;

import nro.server.Client;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jbcd.dao.PlayerDAO;

import java.util.concurrent.ScheduledExecutorService;



public class AnwinManager {

    private static AnwinManager instance = null;

    public static synchronized AnwinManager getInstance() {
        if (instance == null) {
            instance = new AnwinManager();
        }
        return instance;
    }

    private ScheduledExecutorService scheduler;

     public void startAutoSave() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                handleAutoSave();
            } catch (Exception e) {
                System.out.println("[AutoSaveManager] start autosave error: " + e.getLocalizedMessage());
            }
        }, 60, 90, TimeUnit.MINUTES);
    }

    public void handleAutoSave() {
        System.out.println("[AutoSaveManager] start autosave sucessfully !!");
        Client.gI().getPlayers().forEach(player -> {
            long st = System.currentTimeMillis();
            PlayerDAO.updatePlayer(player);
            Logger.success(TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin() + "m: Tự động lưu dữ liệu người chơi thành công! " + (System.currentTimeMillis() - st) + "ms\n");

        });
    }
    
    public void stopAutoSave() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            System.out.println("[AutoSaveManager] autosave scheduler stopped.");
        }
    }

}





