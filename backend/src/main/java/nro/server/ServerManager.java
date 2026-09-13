package nro.server;

import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.Manager.BossNomalManager;
import QuanLiBoss.Manager.BossOfTheGangsManager;
import QuanLiBoss.Manager.BrolyManager;
import QuanLiBoss.Manager.ChristmasEventManager;
import QuanLiBoss.Manager.FinalBossManager;
import QuanLiBoss.Manager.GasDestroyManager;
import QuanLiBoss.Manager.HalloweenEventManager;
import QuanLiBoss.Manager.HungVuongEventManager;
import QuanLiBoss.Manager.LunarNewYearEventManager;
import QuanLiBoss.Manager.OtherBossManager;
import QuanLiBoss.Manager.RedRibbonHQManager;
import QuanLiBoss.Manager.SkillSummonedManager;
import QuanLiBoss.Manager.SnakeWayManager;
import QuanLiBoss.Manager.TreasureUnderSeaManager;
import QuanLiBoss.Manager.TrungThuEventManager;
import QuanLiBoss.Manager.ValentineEventManager;
import QuanLiBoss.Manager.VuLanEventManager;
import QuanLiBoss.Manager.YardartManager;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import event.BakeACake.NauBanh_NewYear;
import event.EventManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jbcd.ConnectDB;
import jbcd.CrisResultSet;
import jbcd.dao.EventDAO;
import jbcd.dao.HistoryTransactionDAO;
import lombok.Getter;
import lombok.Setter;
import network.Network;
import network.interfaces.ISession;
import network.interfaces.ISessionAcceptHandler;
import network.io.MessageSendCollect;
import network.io.MyKeyHandler;
import network.session.MySession;
import nro.attribute.AttributeManager;
import nro.boss.map.TrainingBoss.TopKillWhisManager;
import nro.bot.BotManager;
import nro.bot.Event.BotManagerEvent;
import nro.bot.Event.ChristMas.ShopBotChristMas;
import nro.bot.Event.Halloween.ShopBotHalloween;
import nro.bot.Event.HungVuong.ShopBotHungVuong;
import nro.bot.Event.LunaNewYear.ShopBotLunaNewYear;
import nro.bot.Event.NewBotEvent;
import nro.bot.Event.VuLan.ShopBotVulan;
import nro.bot.New.BotAttackPlayer_1;
import nro.bot.New.BotAttackPlayer_2;
import nro.bot.New.BotAttackPlayer_3;
import nro.bot.New.BotManager_new;
import nro.bot.New.NewBot_new;
import nro.bot.NewBot;
import nro.bot.SellBot;
import nro.bot.ShopBot;
import nro.clan.Clan;
import nro.clan.ClanService;
import nro.consignmentstore.ConsignShopManager;
import nro.dragon.ChristMasEvent.ShenronChristMasEventManager;
import nro.dragon.HalloweenEvent.ShenronHalloweenEventManager;
import nro.giftcode.GiftCodeManager;
import nro.map.DeathOrAliveArena.DeathOrAliveArenaManager;
import nro.map.DestronGas.TopDestronGas;
import nro.map.DragonBallNamec.NgocRongNamec;
import nro.map.RankSuper.SuperRankManager;
import nro.map.SnakeWay.TopSnakeWay;
import nro.map.The23rdMartialArtCongress.The23rdMartialArtCongressManager;
import nro.map.TreasureUnderSea.TopTreasureUnderSea;
import nro.map.WorldMartialArtsTournament.WorldMartialArtsTournamentManager;
import nro.minigame.ChonAiDay;
import nro.minigame.MiniGame;
import nro.minigame.TaiXiu;
import nro.pariry.pariryManager;
import nro.services.Service;
import nro.admin.AdminHttpServer;
import java.nio.charset.StandardCharsets;

public class ServerManager {

    private static final long DELAY = 8000;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static String timeStart;
    public int threadMap;

    public static final Map CLIENTS = new HashMap();

    public static String NAME = "NRO";
    public static String IP = "127.0.0.1";
    public static int PORT = 14445;

    public static String DOMAIN = "NRO.VN";
    public static String NAME_SERVER = "NRO";

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;

    @Getter
    @Setter
    private AttributeManager attributeManager;

    private long lastUpdateAttribute;
    private ExecutorService gameExecutorService;

    public void init() {
        Manager.gI();
        GiftCodeManager.gI().init();

        try {
            if (Manager.LOCAL) {
                return;
            }

            ConnectDB.executeUpdate("update account set last_time_login = '2000-01-01', last_time_logout = '2001-01-01'");
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e);
        }

        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");

        Logger.title("SERVER START");
        Logger.system("SERVER", "Khởi động game backend headless");

        Logger.title("BACKUP");
        backupDatabasePortable();

        scheduleDailyReset();

        try {
            Antiddos.handleRequest(null);
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi khởi tạo Anti-DDOS");
        }

        ServerManager.gI().run();
    }

    public void run() {
        isRunning = true;

        // The web management API lives in the backend process so both the
        // headless launcher and the Swing launcher share the same runtime.
        AdminHttpServer.gI().start();

        Logger.title("SERVER ONLINE");

        gameExecutorService = Executors.newCachedThreadPool();

        activeServerSocket();

        Logger.system("GAME", "Đang khởi động game loop");
        activeGame();

        Logger.system("BXH", "Đang bật tự động cập nhật bảng xếp hạng");
        autoUpdateBxh();

        Logger.system("AUTO_SAVE", "Đang bật tự động lưu database");
        AutoSavedGame();

        Logger.system("EVENT", "Đang cập nhật dữ liệu event người chơi");
        updateEventPlayer();

        logEventStatus();

        Logger.system("BOT", "Đang khởi động bot hệ thống");
        startCoreBots();
        startShopBots();
        startEventBots();
        startNewBots();

        startMiniGames();
        startBakeCakeEvent();

        startCoreSystemThreads();
        startBossManagers();
        AdminHttpServer.gI().applyStoredEvents();
        startEventBossManagers();
    }

    private void logEventStatus() {
        Logger.eventTitle("TRẠNG THÁI SỰ KIỆN");

        logOneEvent("TẾT / LUNAR_NEW_YEAR", EventManager.LUNNAR_NEW_YEAR);
        logOneEvent("GIÁNG SINH / CHRISTMAS", EventManager.CHRISTMAS);
        logOneEvent("VU LAN", EventManager.VU_LAN_FESTIVAL);
        logOneEvent("HALLOWEEN", EventManager.HALLOWEEN);
        logOneEvent("HÙNG VƯƠNG", EventManager.HUNG_VUONG);
        logOneEvent("TRUNG THU", EventManager.TRUNG_THU);
        logOneEvent("8/3 WOMAN DAY", EventManager.INTERNATIONAL_WOMANS_DAY);
        logOneEvent("BLACK FRIDAY", EventManager.BLACK_FRIDAY);
        logOneEvent("VALENTINE", EventManager.VALENTINE_DAY);
    }

    private void logOneEvent(String eventName, boolean enabled) {
        if (enabled) {
            Logger.eventInfo("[ON ] " + eventName + " | trạng thái=bật");
        } else {
            Logger.eventInfo("[OFF] " + eventName + " | trạng thái=tắt");
        }
    }

    private void startCoreBots() {
        new Thread(BotManager.gI(), "New Thread Bot Player").start();
        new Thread(BotManager_new.gI(), "Thread Bot Player_New").start();
        new Thread(BotManagerEvent.gI(), "Thread BotManagerEvent").start();

        Logger.system("BOT", "Đã bật BotManager, BotManager_new, BotManagerEvent");
    }

    private void startShopBots() {
        new Thread(() -> {
            NewBot.gI().runBot(0, null, null, 4000);

            NewBot.gI().runBot(1, new ShopBot(14, 457, 20), null, 1);
            NewBot.gI().runBot(1, new ShopBot(222, 457, 10), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1154, 457, 3), null, 1);
            NewBot.gI().runBot(1, new ShopBot(2012, 457, Util.nextInt(20, 40)), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1150, 457, 15), null, 1);
            NewBot.gI().runBot(1, new ShopBot(380, 457, Util.nextInt(19, 22)), null, 1);
            NewBot.gI().runBot(1, new ShopBot(17, 457, 5), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1151, 457, 15), null, 1);
            NewBot.gI().runBot(1, new ShopBot(18, 457, 4), null, 1);
            NewBot.gI().runBot(1, new ShopBot(221, 457, 8), null, 1);
            NewBot.gI().runBot(1, new ShopBot(16, 457, 8), null, 1);
            NewBot.gI().runBot(1, new ShopBot(443, 457, 1), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1153, 457, 15), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1045, 457, 40), null, 1);
            NewBot.gI().runBot(1, new ShopBot(1152, 457, 15), null, 1);
            NewBot.gI().runBot(1, new ShopBot(15, 457, 5), null, 1);
            NewBot.gI().runBot(1, new ShopBot(220, 457, 5), null, 1);
            NewBot.gI().runBot(1, new ShopBot(447, 457, 3), null, 1);
            NewBot.gI().runBot(1, new ShopBot(987, 457, 10), null, 1);
            NewBot.gI().runBot(1, new ShopBot(380, 457, Util.nextInt(21, 25)), null, 1);
            NewBot.gI().runBot(1, new ShopBot(441, 457, 1), null, 1);
            NewBot.gI().runBot(1, new ShopBot(987, 457, Util.nextInt(8, 12)), null, 1);
            NewBot.gI().runBot(1, new ShopBot(442, 457, 1), null, 1);
            NewBot.gI().runBot(1, new ShopBot(443, 457, 1), null, 1);
            NewBot.gI().runBot(1, new ShopBot(20, 457, 2), null, 1);
            NewBot.gI().runBot(1, new ShopBot(223, 457, 8), null, 1);
            NewBot.gI().runBot(1, new ShopBot(19, 457, 3), null, 1);
            NewBot.gI().runBot(1, new ShopBot(987, 457, Util.nextInt(8, 10)), null, 1);
            NewBot.gI().runBot(1, new ShopBot(223, 457, 8), null, 1);
            NewBot.gI().runBot(1, new ShopBot(224, 457, 10), null, 1);
            NewBot.gI().runBot(1, new ShopBot(2012, 457, Util.nextInt(20, 40)), null, 1);

            NewBot.gI().runBot(3, null, new SellBot(457, 16, 1), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 19, 99), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 14, 1), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 18, 99), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 15, 1), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 20, 99), 1);
            NewBot.gI().runBot(3, null, new SellBot(457, 17, 99), 1);
        }, "Thread Shop Bot").start();

        Logger.system("BOT", "Đã bật shop bot thường");
    }

    private void startEventBots() {
        Logger.eventTitle("KHỞI ĐỘNG BOT SỰ KIỆN");

        if (EventManager.LUNNAR_NEW_YEAR) {
            Logger.eventInfo("Bật bot sự kiện Tết");
            new Thread(() -> {
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(748, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(749, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(750, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1177, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1178, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1179, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1180, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1181, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1473, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1474, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
                NewBotEvent.gI().runBot(0, new ShopBotLunaNewYear(1475, 457, Util.nextInt(20, 30)), null, null, null, null, 1);
            }, "Bot Event Tet").start();
        } else {
            Logger.eventInfo("Sự kiện Tết tắt, không bật bot Tết");
        }

        if (EventManager.CHRISTMAS) {
            Logger.eventInfo("Bật bot sự kiện Giáng Sinh");
            new Thread(() -> {
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1459, 457, Util.nextInt(20, 30)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1460, 457, Util.nextInt(20, 30)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1461, 457, Util.nextInt(20, 30)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1462, 457, Util.nextInt(50, 60)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1463, 457, 99), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1445, 457, Util.nextInt(15, 20)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1447, 457, Util.nextInt(20, 30)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1446, 457, Util.nextInt(30, 35)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1444, 457, Util.nextInt(35, 50)), null, null, null, 1);
                NewBotEvent.gI().runBot(1, null, new ShopBotChristMas(1839, 457, Util.nextInt(90, 150)), null, null, null, 1);
            }, "Bot Event Christmas").start();
        } else {
            Logger.eventInfo("Sự kiện Giáng Sinh tắt, không bật bot Giáng Sinh");
        }

        if (EventManager.VU_LAN_FESTIVAL) {
            Logger.eventInfo("Bật bot sự kiện Vu Lan");
            new Thread(() -> {
                NewBotEvent.gI().runBot(2, null, null, new ShopBotVulan(1035, 457, Util.nextInt(20, 30)), null, null, 1);
                NewBotEvent.gI().runBot(2, null, null, new ShopBotVulan(1032, 457, Util.nextInt(30, 40)), null, null, 1);
                NewBotEvent.gI().runBot(2, null, null, new ShopBotVulan(1035, 457, Util.nextInt(20, 30)), null, null, 1);
                NewBotEvent.gI().runBot(2, null, null, new ShopBotVulan(1032, 457, Util.nextInt(30, 40)), null, null, 1);
            }, "Bot Event Vu Lan").start();
        } else {
            Logger.eventInfo("Sự kiện Vu Lan tắt, không bật bot Vu Lan");
        }

        if (EventManager.HALLOWEEN) {
            Logger.eventInfo("Bật bot sự kiện Halloween");
            new Thread(() -> {
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(901, 457, Util.nextInt(30, 50)), null, 1);
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(585, 457, Util.nextInt(50, 80)), null, 1);
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(901, 457, Util.nextInt(30, 50)), null, 1);
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(585, 457, Util.nextInt(50, 80)), null, 1);
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(901, 457, Util.nextInt(30, 50)), null, 1);
                NewBotEvent.gI().runBot(3, null, null, null, new ShopBotHalloween(585, 457, Util.nextInt(50, 80)), null, 1);
            }, "Bot Event Halloween").start();
        } else {
            Logger.eventInfo("Sự kiện Halloween tắt, không bật bot Halloween");
        }

        if (EventManager.HUNG_VUONG) {
            Logger.eventInfo("Bật bot sự kiện Hùng Vương");
            new Thread(() -> {
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1545, 457, Util.nextInt(20, 30)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1547, 457, Util.nextInt(20, 30)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1548, 457, Util.nextInt(20, 30)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1546, 457, Util.nextInt(20, 30)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1220, 457, Util.nextInt(80, 120)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1221, 457, Util.nextInt(80, 120)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(1222, 457, Util.nextInt(80, 120)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(569, 457, Util.nextInt(180, 250)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(569, 457, Util.nextInt(180, 250)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(569, 457, Util.nextInt(180, 250)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(569, 457, Util.nextInt(180, 250)), 1);
                NewBotEvent.gI().runBot(4, null, null, null, null, new ShopBotHungVuong(569, 457, Util.nextInt(180, 250)), 1);
            }, "Bot Event Hung Vuong").start();
        } else {
            Logger.eventInfo("Sự kiện Hùng Vương tắt, không bật bot Hùng Vương");
        }
    }

    private void startNewBots() {
        new Thread(() -> {
            NewBot_new.gI().runBot_new(0, null, null, null, 0);
            NewBot_new.gI().runBot_new(1, null, null, null, 0);
            NewBot_new.gI().runBot_new(2, null, null, null, 0);
            NewBot_new.gI().runBot_new(3, null, null, null, 0);
            NewBot_new.gI().runBot_new(4, null, null, null, 0);
            NewBot_new.gI().runBot_new(5, null, null, null, 0);
            NewBot_new.gI().runBot_new(6, null, null, null, 0);
            NewBot_new.gI().runBot_new(7, null, null, null, 0);
            NewBot_new.gI().runBot_new(8, null, null, null, 0);
            NewBot_new.gI().runBot_new(9, null, null, null, 0);
            NewBot_new.gI().runBot_new(10, null, null, null, 0);
            NewBot_new.gI().runBot_new(11, new BotAttackPlayer_1(true), null, null, 0);
            NewBot_new.gI().runBot_new(12, null, new BotAttackPlayer_2(true), null, 0);
            NewBot_new.gI().runBot_new(13, null, null, new BotAttackPlayer_3(true), 0);
        }, "Thread Bot Player_New_New").start();

        Logger.system("BOT", "Đã bật NewBot_new");
    }

    private void startMiniGames() {
        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
        TaiXiu.gI().lastTimeEnd = System.currentTimeMillis() + 50000;
        MiniGame.gI().MiniGame_S1.activate(1000);

        Logger.system("MINIGAME", "Đã bật Chọn Ai Đây, Tài Xỉu, MiniGame_S1");
    }

    private void startBakeCakeEvent() {
        if (EventManager.LUNNAR_NEW_YEAR) {
            Logger.eventTitle("SỰ KIỆN NẤU BÁNH TẾT");

            NauBanh_NewYear nauBanh = new NauBanh_NewYear();
            Thread thread = new Thread(nauBanh);
            thread.setName("Sự kiện nấu bánh Event Tết");
            thread.start();

            Logger.eventInfo("Đã bật thread nấu bánh Tết");
        } else {
            Logger.eventInfo("Sự kiện Tết tắt, không khởi động thread nấu bánh");
        }
    }

    private void startCoreSystemThreads() {
        new Thread(SuperRankManager.gI(), "Update Super Rank").start();
        new Thread(WorldMartialArtsTournamentManager.gI(), "Update WMAT").start();
        new Thread(The23rdMartialArtCongressManager.gI(), "Update DHVT23").start();
        new Thread(DeathOrAliveArenaManager.gI(), "Update Võ Đài Sinh Tử").start();
        new Thread(ChonAiDay.gI(), "Thread CAD").start();
        new Thread(NgocRongNamec.gI(), "Update NRNM").start();
        new Thread(TaiXiu.gI(), "Thread TaiXiu").start();
        new Thread(AutoMaintenance.gI(), "Thread Auto bảo trì tự động").start();
        new Thread(pariryManager.gI(), "Thread Pariry").start();

        Logger.system("THREAD", "Đã bật thread hệ thống chính");
    }

    private void startBossManagers() {
        BossManager.gI().loadBoss();
        Manager.MAPS.forEach(nro.map.Map::initBoss);
        EventManager.gI().init();

        new Thread(BossManager.gI(), "Update boss").start();
        new Thread(YardartManager.gI(), "Update yardart boss").start();
        new Thread(FinalBossManager.gI(), "Update final boss").start();
        new Thread(SkillSummonedManager.gI(), "Update Skill-summoned boss").start();
        new Thread(BrolyManager.gI(), "Update broly boss").start();
        new Thread(OtherBossManager.gI(), "Update other boss").start();
        new Thread(RedRibbonHQManager.gI(), "Update reb ribbon hq boss").start();
        new Thread(TreasureUnderSeaManager.gI(), "Update treasure under sea boss").start();
        new Thread(SnakeWayManager.gI(), "Update snake way boss").start();
        new Thread(GasDestroyManager.gI(), "Update gas destroy boss").start();
        new Thread(BossOfTheGangsManager.gI(), "Update the gangs boss").start();
        new Thread(BossNomalManager.gI(), "Update nomal boss").start();

        Logger.system("BOSS", "Đã bật boss manager thường");
    }

    private void startEventBossManagers() {
        Logger.eventTitle("KHỞI ĐỘNG BOSS SỰ KIỆN");

        if (EventManager.TRUNG_THU) {
            new Thread(TrungThuEventManager.gI(), "Update trung thu event boss").start();
            Logger.eventInfo("Đã bật boss sự kiện Trung Thu");
        } else {
            Logger.eventInfo("Trung Thu tắt, không bật boss Trung Thu");
        }

        if (EventManager.HALLOWEEN) {
            new Thread(HalloweenEventManager.gI(), "Update halloween event boss").start();
            new Thread(ShenronHalloweenEventManager.gI(), "Update Shenron Halloween").start();
            Logger.eventInfo("Đã bật boss + rồng Halloween");
        } else {
            Logger.eventInfo("Halloween tắt, không bật boss/rồng Halloween");
        }

        if (EventManager.CHRISTMAS) {
            new Thread(ChristmasEventManager.gI(), "Update christmas event boss").start();
            new Thread(ShenronChristMasEventManager.gI(), "Update Shenron ChristMas").start();
            Logger.eventInfo("Đã bật boss + rồng Giáng Sinh");
        } else {
            Logger.eventInfo("Giáng Sinh tắt, không bật boss/rồng Giáng Sinh");
        }

        if (EventManager.HUNG_VUONG) {
            new Thread(HungVuongEventManager.gI(), "Update Hung Vuong event boss").start();
            Logger.eventInfo("Đã bật boss Hùng Vương");
        } else {
            Logger.eventInfo("Hùng Vương tắt, không bật boss Hùng Vương");
        }

        if (EventManager.LUNNAR_NEW_YEAR) {
            new Thread(LunarNewYearEventManager.gI(), "Update lunar new year event boss").start();
            Logger.eventInfo("Đã bật boss Tết");
        } else {
            Logger.eventInfo("Tết tắt, không bật boss Tết");
        }

        if (EventManager.VU_LAN_FESTIVAL) {
            new Thread(VuLanEventManager.gI(), "Update vulan event boss").start();
            Logger.eventInfo("Đã bật boss Vu Lan");
        } else {
            Logger.eventInfo("Vu Lan tắt, không bật boss Vu Lan");
        }

        if (EventManager.VALENTINE_DAY) {
            new Thread(ValentineEventManager.gI(), "Update valentine event boss").start();
            Logger.eventInfo("Đã bật boss Valentine");
        } else {
            Logger.eventInfo("Valentine tắt, không bật boss Valentine");
        }
    }

    private void activeServerSocket() {
        try {
            Network.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                @Override
                public void sessionInit(ISession is) {
                    if (!canConnectWithIp(is.getIP())) {
                        Logger.warn("CONNECT", "Từ chối kết nối IP vượt giới hạn | ip=" + is.getIP());
                        is.disconnect();
                        return;
                    }

                    is.setMessageHandler(Controller.getInstance())
                            .setSendCollect(new MessageSendCollect())
                            .setKeyHandler(new MyKeyHandler())
                            .startCollect()
                            .startQueueHandler();
                }

                @Override
                public void sessionDisconnect(ISession session) {
                    Client.gI().kickSession((MySession) session);
                }
            }).setTypeSessionClone(MySession.class)
                    .setDoSomeThingWhenClose(() -> {
                        Logger.err("SERVER", "SERVER CLOSE");
                        System.exit(0);
                    })
                    .start(PORT);

            Logger.success("SERVER", "Server đang chạy tại port " + PORT);
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi activeServerSocket");
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.getIP());
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.getIP(), n);
        }
    }

    private void activeGame() {
        long delay = 500;
        gameExecutorService.submit(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    if (attributeManager != null) {
                        attributeManager.update();
                        if (Util.canDoWithTime(lastUpdateAttribute, 600000)) {
                            lastUpdateAttribute = System.currentTimeMillis();
                            Manager.gI().updateAttributeServer();
                        }
                    }
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    Logger.logException(ServerManager.class, e);
                }
            }
        });
    }

    private static void AutoSavedGame() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!isRunning) {
                    scheduler.shutdown();
                    Logger.warn("AUTO_SAVE", "Server đã dừng, tắt AutoSavedGame");
                    return;
                }

                Service.gI().AutoSavedDataBase();
            } catch (Exception e) {
                Logger.err("AUTO_SAVE", "Lỗi AutoSavedGame | " + e.getMessage());
            }
        }, 0, DELAY, TimeUnit.MILLISECONDS);
    }

    public void close() {
        isRunning = false;

        try {
            AdminHttpServer.gI().stop();
        } catch (Exception e) {
            Logger.warn("ADMIN_API", "Không thể dừng Admin API: " + e.getMessage());
        }

        Logger.title("SERVER CLOSE");

        try {
            ClanService.gI().close();
            Logger.success("SAVE", "Lưu clan thành công");
        } catch (Exception e) {
            Logger.err("SAVE", "Lỗi save clan");
            Logger.logException(ServerManager.class, e);
        }

        try {
            ConsignShopManager.gI().save();
            Logger.success("SAVE", "Lưu shop ký gửi thành công");
        } catch (Exception e) {
            Logger.err("SAVE", "Lỗi save shop ký gửi");
            Logger.logException(ServerManager.class, e);
        }

        try {
            Manager.gI().updateAttributeServer();
            Logger.success("SAVE", "Lưu attribute server thành công");
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e);
        }

        try {
            EventDAO.save();
            Logger.success("SAVE", "Lưu event thành công");
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi save event");
        }

        try {
            Client.gI().close();
            Logger.success("CLIENT", "Đã đóng toàn bộ client");
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi close client");
        }

        try {
            if (gameExecutorService != null) {
                gameExecutorService.shutdown();
            }
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Lỗi shutdown gameExecutorService");
        }

        Logger.success("SERVER", "BẢO TRÌ THÀNH CÔNG");

        System.exit(0);
    }

    public long getNumPlayer() {
        long num = 0;
        try {
            CrisResultSet rs = ConnectDB.executeQuery("SELECT COUNT(*) FROM `player`");
            rs.first();
            num = rs.getLong(1);
        } catch (Exception e) {
        }
        return num;
    }

    public void autoUpdateBxh() {
        ScheduledExecutorService autoTop = Executors.newScheduledThreadPool(1);
        autoTop.scheduleWithFixedDelay(() -> {
            try {
                TopServer.LoadingTop();

                TopDestronGas.getInstance().load();
                TopTreasureUnderSea.getInstance().load();
                TopSnakeWay.getInstance().load();
                TopKillWhisManager.getInstance().load();
            } catch (Exception e) {
                Logger.logException(ServerManager.class, e, "Auto load BXH error");
            }
        }, 0, 30000, TimeUnit.MILLISECONDS);
    }

    private static void backupDatabasePortable() {
        try {
            Properties properties = new Properties();
            try (FileInputStream input = new FileInputStream("data/config/data_base.properties")) {
                properties.load(input);
            }

            String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path backupFolder = Paths.get(properties.getProperty("database.backup.dir", "data/backups"));
            Files.createDirectories(backupFolder);
            String backupFilePath = backupFolder.resolve("nro_backup_" + dateTime + ".sql").toString();

            String mysqldumpPath = properties.getProperty("database.mysqldump.path", "mysqldump");
            String dbHost = properties.getProperty("database.host", "localhost");
            String dbPort = properties.getProperty("database.port", "3306");
            String dbUser = properties.getProperty("database.user", "root");
            String dbPassword = properties.getProperty("database.pass", "");
            String dbName = properties.getProperty("database.name");

            if (dbName == null || dbName.isBlank()) {
                Logger.warn("BACKUP_DB", "Skip backup because database.name is not configured");
                return;
            }

            List<String> command = new ArrayList<>();
            command.add(mysqldumpPath);
            command.add("-h");
            command.add(dbHost);
            command.add("-P");
            command.add(dbPort);
            command.add("-u");
            command.add(dbUser);
            command.add("--databases");
            command.add(dbName);
            command.add("--password=" + dbPassword);
            command.add("--result-file=" + backupFilePath);

            Logger.backup("Backing up database | db=" + dbName + " | file=" + backupFilePath);
            Process process = new ProcessBuilder(command).redirectErrorStream(false).start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Logger.info("BACKUP_DB", line);
                    }
                }
                while ((line = errorReader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Logger.err("BACKUP_DB", line);
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                Logger.success("BACKUP_DB", "Database backup completed | file=" + backupFilePath);
            } else {
                Logger.err("BACKUP_DB", "mysqldump failed | exitCode=" + exitCode);
            }
        } catch (IOException e) {
            Logger.warn("BACKUP_DB", "mysqldump is unavailable or backup failed: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.warn("BACKUP_DB", "Database backup interrupted");
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "Database backup failed");
        }
    }

    public void scheduleDatabaseBackup() {
        Logger.system("BACKUP_DB", "Đã bật tự động sao lưu database mỗi 30 phút");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                backupDatabasePortable();
            } catch (Exception e) {
                Logger.logException(ServerManager.class, e, "Lỗi scheduleDatabaseBackup");
            }
        }, 0, 30, TimeUnit.MINUTES);
    }

    public static void scheduleDailyReset() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable resetTask = () -> {
            try {
                resetClanAllClans();
            } catch (Exception e) {
                Logger.logException(ServerManager.class, e, "Lỗi reset clan hằng ngày");
            }
        };

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        long initialDelay = Duration.between(now, nextMidnight).toMillis();
        long oneDay = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(resetTask, initialDelay, oneDay, TimeUnit.MILLISECONDS);

        Logger.system("SCHEDULE", "Đã đặt lịch reset clan hằng ngày lúc 00:00");
    }

    private static void resetClanAllClans() {
        PreparedStatement ps = null;
        Connection con = null;

        try {
            con = ConnectDB.getConnection();
            ps = con.prepareStatement("UPDATE clan SET Boss_clan = 1");

            int rows = ps.executeUpdate();

            for (Clan clan : Manager.CLANS) {
                clan.boss_clan_round = 1;
            }

            Logger.success("CLAN", "Đã reset boss_clan_round về 1 | totalClan=" + rows);
        } catch (SQLException e) {
            Logger.logException(Clan.class, e, "Lỗi khi resetBossClanAllClans");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                Logger.logException(Clan.class, e, "Lỗi khi đóng kết nối resetBossClanAllClans");
            }
        }
    }

    public void updateEventPlayer() {
        if (!EventManager.LUNNAR_NEW_YEAR) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_tet = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_new_year = '[0,0,0,0]'");
                Manager.resetEventData("luna_new_year");
                Logger.eventInfo("Reset dữ liệu event Tết vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Tết | " + e.getMessage());
            }
        }

        if (!EventManager.CHRISTMAS) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_giangsinh = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_christ_mas = '[0,0,0,0]'");
                Manager.resetEventData("christ_mas");
                Logger.eventInfo("Reset dữ liệu event Giáng Sinh vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Giáng Sinh | " + e.getMessage());
            }
        }

        if (!EventManager.VU_LAN_FESTIVAL) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET data_event_vulan = '[0,0,0]'");
                Logger.eventInfo("Reset dữ liệu event Vu Lan vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Vu Lan | " + e.getMessage());
            }
        }

        if (!EventManager.HALLOWEEN) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_halloween = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_halloween = '[0,0]'");
                Logger.eventInfo("Reset dữ liệu event Halloween vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Halloween | " + e.getMessage());
            }
        }

        if (!EventManager.INTERNATIONAL_WOMANS_DAY) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_8_3 = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_8_3 = '[0,0]'");
                ConnectDB.executeUpdate("UPDATE `event` SET `data` = ? WHERE `name` = 'international_womens_day'",
                        "{\"damePrecent\":0,\"hpPrecent\":0,\"mpPrecent\":0,\"eventPoint\":0,\"lastExpRewardStage\":0}");
                Logger.eventInfo("Reset dữ liệu event 8/3 vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event 8/3 | " + e.getMessage());
            }
        }

        if (!EventManager.TRUNG_THU) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_trungthu = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_trung_thu = '[0,0]'");
                Logger.eventInfo("Reset dữ liệu event Trung Thu vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Trung Thu | " + e.getMessage());
            }
        }

        if (!EventManager.HUNG_VUONG) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_hung_vuong = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_hung_vuong = '[0,0,0,0,0,0]'");
                ConnectDB.executeUpdate("UPDATE `event` SET `data` = ? WHERE `name` = 'hung_vuong'", "{\"ReceiveMelonSeed\":0}");
                Logger.eventInfo("Reset dữ liệu event Hùng Vương vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Hùng Vương | " + e.getMessage());
            }
        }

        if (!EventManager.BLACK_FRIDAY) {
            try {
                ConnectDB.executeUpdate("UPDATE player SET diem_su_kien_black_friday = '0'");
                ConnectDB.executeUpdate("UPDATE player SET data_event_black_friday = '[0,0]'");
                Logger.eventInfo("Reset dữ liệu event Black Friday vì event đang tắt");
            } catch (Exception e) {
                Logger.eventError("Lỗi reset event Black Friday | " + e.getMessage());
            }
        }
    }
}
