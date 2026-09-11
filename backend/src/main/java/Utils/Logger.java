package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    public static final String RESET = "\033[0m";

    // Màu chữ
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    // Màu nền
    public static final String BLACK_BG = "\033[40m";
    public static final String RED_BG = "\033[41m";
    public static final String GREEN_BG = "\033[42m";
    public static final String YELLOW_BG = "\033[43m";
    public static final String BLUE_BG = "\033[44m";
    public static final String PURPLE_BG = "\033[45m";
    public static final String CYAN_BG = "\033[46m";
    public static final String WHITE_BG = "\033[47m";

    // Kiểu chữ
    public static final String BOLD = "\033[1m";
    public static final String UNDERLINE = "\033[4m";
    public static final String REVERSED = "\033[7m";

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final Object LOCK = new Object();

    private static String now() {
        return TIME_FORMAT.format(new Date());
    }

    private static String tag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            tag = "LOG";
        }
        return String.format("%-14s", "[" + tag + "]");
    }

    private static void print(String color, String tag, String text, boolean newLine) {
        synchronized (LOCK) {
            String msg = color + "[" + now() + "] " + tag(tag) + " " + text + RESET;
            if (newLine) {
                System.out.println(msg);
            } else {
                System.out.print(msg);
            }
        }
    }

    // =====================================================
    // LOG CŨ - GIỮ TƯƠNG THÍCH SOURCE CŨ
    // =====================================================

    public static void log(String text) {
        print(GREEN, "LOG", text, false);
    }

    public static void logln(String text) {
        print(GREEN, "LOG", text, true);
    }

    public static void log(String color, String text) {
        print(GREEN, "LOG", text, false);
    }

    public static void logln(String color, String text) {
        print(GREEN, "LOG", text, true);
    }

    public static void log(String color, String background, String text) {
        print(GREEN, "LOG", text, false);
    }

    public static void success(String text) {
        print(GREEN, "OK", text, false);
    }

    public static void successln(String text) {
        print(GREEN, "OK", text, true);
    }

    public static void warning(String text) {
        print(YELLOW, "WARN", text, false);
    }

    public static void warningln(String text) {
        print(YELLOW, "WARN", text, true);
    }

    public static void error(String text) {
        print(RED + BOLD, "ERROR", text, false);
    }

    public static void errorln(String text) {
        print(RED + BOLD, "ERROR", text, true);
    }

    public static void primary(String text) {
        print(BLUE, "INFO", text, false);
    }

    public static void primaryln(String text) {
        print(BLUE, "INFO", text, true);
    }

    // =====================================================
    // LOG CƠ BẢN THEO TAG
    // =====================================================

    public static void info(String tag, String text) {
        print(GREEN, tag, text, true);
    }

    public static void success(String tag, String text) {
        print(GREEN + BOLD, tag, text, true);
    }

    public static void warn(String tag, String text) {
        print(YELLOW + BOLD, tag, text, true);
    }

    public static void err(String tag, String text) {
        print(RED + BOLD, tag, text, true);
    }

    public static void system(String tag, String text) {
        print(GREEN, tag, text, true);
    }

    // =====================================================
    // LOG SERVER / CONNECT
    // =====================================================

    public static void connect(String text) {
        print(PURPLE + BOLD, "CONNECT", text, true);
    }

    public static void server(String text) {
        print(GREEN + BOLD, "SERVER", text, true);
    }

    public static void serverInfo(String text) {
        print(GREEN, "SERVER_INFO", text, true);
    }

    public static void serverError(String text) {
        print(RED + BOLD, "SERVER_ERR", text, true);
    }

    // =====================================================
    // LOG PLAYER / LOGIN / SAVE
    // =====================================================

    public static void login(String text) {
        print(GREEN + BOLD, "LOGIN", text, true);
    }

    public static void save(String text) {
        print(GREEN, "SAVE", text, true);
    }

    public static void dupLogin(String text) {
        print(YELLOW + BOLD, "DUP_LOGIN", text, true);
    }

    // =====================================================
    // LOG BACKUP / DATABASE
    // =====================================================

    public static void backup(String text) {
        print(PURPLE + BOLD, "BACKUP", text, true);
    }

    public static void backupInfo(String text) {
        print(PURPLE, "BACKUP_INFO", text, true);
    }

    public static void backupError(String text) {
        print(RED + BOLD, "BACKUP_ERR", text, true);
    }

    public static void db(String text) {
        print(GREEN + BOLD, "DB", text, true);
    }

    public static void dbInfo(String text) {
        print(GREEN, "DB_INFO", text, true);
    }

    public static void dbError(String text) {
        print(RED + BOLD, "DB_ERR", text, true);
    }

    // =====================================================
    // LOG EVENT - TÁCH RIÊNG
    // =====================================================

    public static void eventTitle(String title) {
        synchronized (LOCK) {
            System.out.println(PURPLE + BOLD + "════════════════════════════════════════════════════════════" + RESET);
            System.out.println(PURPLE + BOLD + "▶ EVENT | " + title + RESET);
            System.out.println(PURPLE + BOLD + "════════════════════════════════════════════════════════════" + RESET);
        }
    }

    public static void event(String text) {
        print(PURPLE + BOLD, "EVENT", text, true);
    }

    public static void eventInfo(String text) {
        print(PURPLE, "EVENT_INFO", text, true);
    }

    public static void eventReward(String text) {
        print(PURPLE + BOLD, "EVENT_REWARD", text, true);
    }

    public static void eventError(String text) {
        print(RED + BOLD, "EVENT_ERR", text, true);
    }

    // =====================================================
    // LOG ANTI DDOS - TÁCH RIÊNG
    // =====================================================

    public static void antiDdosTitle(String title) {
        synchronized (LOCK) {
            System.out.println(BLUE + BOLD + "════════════════════════════════════════════════════════════" + RESET);
            System.out.println(BLUE + BOLD + "▶ ANTI_DDOS | " + title + RESET);
            System.out.println(BLUE + BOLD + "════════════════════════════════════════════════════════════" + RESET);
        }
    }

    public static void antiDdos(String text) {
        print(BLUE + BOLD, "ANTI_DDOS", text, true);
    }

    public static void antiDdosInfo(String text) {
        print(BLUE, "DDOS_INFO", text, true);
    }

    public static void antiDdosBlock(String text) {
        print(RED + BOLD, "DDOS_BLOCK", text, true);
    }

    public static void antiDdosUnblock(String text) {
        print(BLUE + BOLD, "DDOS_UNLOCK", text, true);
    }

    // =====================================================
    // LOG NAMEK / BOSS / GIFTCODE
    // =====================================================

    public static void namek(String text) {
        print(GREEN + BOLD, "NAMEK", text, true);
    }

    public static void boss(String text) {
        print(YELLOW + BOLD, "BOSS", text, true);
    }

    public static void giftcode(String text) {
        print(GREEN + BOLD, "GIFTCODE", text, true);
    }

    // =====================================================
    // TITLE / LINE
    // =====================================================

    public static void line() {
        synchronized (LOCK) {
            System.out.println(GREEN + "────────────────────────────────────────────────────────────" + RESET);
        }
    }

    public static void title(String title) {
        synchronized (LOCK) {
            System.out.println(GREEN + "────────────────────────────────────────────────────────────" + RESET);
            System.out.println(GREEN + BOLD + "▶ " + title + RESET);
            System.out.println(GREEN + "────────────────────────────────────────────────────────────" + RESET);
        }
    }

    public static void section(String title) {
        synchronized (LOCK) {
            System.out.println(GREEN + BOLD + "════════════════════════════════════════════════════════════" + RESET);
            System.out.println(GREEN + BOLD + "▶ " + title + RESET);
            System.out.println(GREEN + BOLD + "════════════════════════════════════════════════════════════" + RESET);
        }
    }

    // =====================================================
    // EXCEPTION LOG
    // =====================================================

    public static void logException(Class<?> clazz, Exception ex, String... log) {
        try {
            if (log != null && log.length > 0) {
                warn("EXCEPTION", log[0]);
            }

            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String exceptionDetails = sw.toString();

            err("EXCEPTION", "Class: " + clazz.getName());
            err("EXCEPTION", "Method: " + methodName);
            err("EXCEPTION", "Message: " + ex.getMessage());

            for (String line : exceptionDetails.split("\n")) {
                err("STACK", line);
            }

            line();
        } catch (Exception e) {
            errorln("Failed to log exception: " + e.getMessage());
        }
    }

    // =====================================================
    // FILE LOG
    // =====================================================

    public static void fileLog(String playerName, String string) {
        new Thread(() -> {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String timeNow = formatter.format(new Date());
                String logEntry = "[" + timeNow + "] " + string;
                writeFile("log/" + playerName + "_log.txt", logEntry);
            } catch (IOException e) {
                errorln("Không thể ghi file log: " + e.getMessage());
            }
        }, "Thread File Logger").start();
    }

    private static void writeFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(content);
        }
    }

    // =====================================================
    // TIỆN ÍCH
    // =====================================================

    public static String shortSession(Object session) {
        if (session == null) {
            return "null";
        }
        return Integer.toHexString(System.identityHashCode(session));
    }
}