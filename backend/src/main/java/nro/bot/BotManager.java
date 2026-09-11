package nro.bot;

import Utils.Functions;
import Utils.Logger;
import java.util.ArrayList;
import java.util.List;
import nro.services.Fun.ChangeMapService;
import nro.server.ServerManager;

public class BotManager implements Runnable {

    public static BotManager i;
    public static boolean ALLOW_CREATE_BOT = false;
    public static volatile boolean BOT_SYSTEM_ENABLED = false;

    public static volatile int TARGET_MOB_BOT  = 0;
    public static volatile int TARGET_SHOP_BOT = 0;
    public static volatile int TARGET_BOSS_BOT = 0;
    public static volatile int TARGET_SELL_BOT = 0;

    public static volatile int SHOP_ITEM_ID = 441;
    public static volatile int SHOP_TRADE_ID = 14;
    public static volatile int SHOP_TRADE_NEED = 1;

    public List<Bot> bot = new ArrayList<>();

    public List<Bot> getBot() {
        return this.bot;
    }

    public static BotManager gI() {
        if (i == null) {
            i = new BotManager();
        }
        return i;
    }

    /** Đếm bot theo type (an toàn) */
    public int countByType(int type) {
        int c = 0;
        List<Bot> snapshot;
        synchronized (bot) {
            snapshot = new ArrayList<>(bot);
        }
        for (Bot b : snapshot) {
            if (b != null && b.type == type) {
                c++;
            }
        }
        return c;
    }

    @Override
    public void run() {
        final int delay = 130;

        while (ServerManager.isRunning) {
            long startTime = System.currentTimeMillis();

            try {
                if (!BOT_SYSTEM_ENABLED) {
                    boolean has;
                    synchronized (bot) {
                        has = !bot.isEmpty();
                    }
                    if (has) stopAllBots();
                } else {
                    if (ALLOW_CREATE_BOT) {
                        int curMob = countByType(0);
                        if (curMob < TARGET_MOB_BOT) {
                            int need = TARGET_MOB_BOT - curMob;
                            NewBot.gI().runBot(0, null, null, need);
                        }

                        int curShop = countByType(1);
                        if (curShop < TARGET_SHOP_BOT) {
                            int need = TARGET_SHOP_BOT - curShop;
                            ShopBot shopTemplate = new ShopBot(SHOP_ITEM_ID, SHOP_TRADE_ID, SHOP_TRADE_NEED);
                            NewBot.gI().runBot(1, shopTemplate, null, need);
                        }

                        int curBoss = countByType(2);
                        if (curBoss < TARGET_BOSS_BOT) {
                            int need = TARGET_BOSS_BOT - curBoss;
                            NewBot.gI().runBot(2, null, null, need);
                        }

                        int curSell = countByType(3);
                        if (curSell < TARGET_SELL_BOT) {
                            int need = TARGET_SELL_BOT - curSell;
                            SellBot sellTemplate = new SellBot(441, 14, 99);
                            NewBot.gI().runBot(3, null, sellTemplate, need);
                        }
                    }
                }

                // ===== UPDATE BOT (snapshot) =====
                List<Bot> snapshot;
                synchronized (bot) {
                    snapshot = new ArrayList<>(bot);
                }
                for (int j = snapshot.size() - 1; j >= 0; j--) {
                    Bot currentBot = snapshot.get(j);
                    if (currentBot != null) {
                        try {
                            currentBot.update();
                        } catch (Exception e) {
                            Logger.logException(BotManager.class, e);
                        }
                    }
                }

            } catch (Exception e) {
                Logger.logException(BotManager.class, e);
            }

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = Math.max(delay - elapsed, 10);

            try {
                Functions.sleep(sleepTime);
            } catch (Exception e) {
                Logger.logException(BotManager.class, e);
            }
        }
    }

    public void stopAllBots() {
        List<Bot> snapshot;
        synchronized (bot) {
            snapshot = new ArrayList<>(bot);
            bot.clear();
        }

        for (int i = snapshot.size() - 1; i >= 0; i--) {
            Bot b = snapshot.get(i);
            try {
                if (b != null) {
                    ChangeMapService.gI().exitMap(b);
                }
            } catch (Exception ignored) {
            }
        }
    }
}
