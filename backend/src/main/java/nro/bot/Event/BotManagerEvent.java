package nro.bot.Event;

import Utils.Logger;
import nro.server.ServerManager;
import java.util.ArrayList;
import java.util.List;

public class BotManagerEvent implements Runnable {

    public static BotManagerEvent i;
    
    public List<BotEvent> bot =  new ArrayList<>();
    
    
    public static BotManagerEvent gI(){
        if(i == null){
            i = new BotManagerEvent();
        }
            return i;
    }
       
    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (BotEvent bot : this.bot) {
                    bot.update();
                }
                Thread.sleep(150 - (System.currentTimeMillis() - st));
            } catch (Exception e) {
            }
        }
    }
}




