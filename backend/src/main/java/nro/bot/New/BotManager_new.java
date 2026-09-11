package nro.bot.New;

import Utils.Functions;
import Utils.Logger;
import nro.server.ServerManager;
import java.util.ArrayList;
import java.util.List;
import Utils.Util;

public class BotManager_new implements Runnable {

    public static BotManager_new i;
    
    public List<Bot_new> botnew =  new ArrayList<>();
    
    public List<Bot_new> getBot() {
        return this.botnew;
    }
    
    public static BotManager_new gI(){
        if (i == null) {
            i = new BotManager_new();
        }
        return i;
    }
    
    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                int delay = 100;
                long st = System.currentTimeMillis();
                for (int j = this.botnew.size() - 1; j >= 0; j--) {
                    try {
                        this.botnew.get(j).update();
                    } catch (Exception e) {
                        Logger.logException(BotManager_new.class, e);
                    }
                }
                Functions.sleep(Math.max(delay - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                Logger.logException(BotManager_new.class, e);
            }
        }
    }
}





