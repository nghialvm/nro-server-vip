package Utils;

import models.Item.Item;
import nro.map.ItemMap;
import nro.map.Zone;
import java.text.NumberFormat;
import java.util.*;
import nro.npc.Npc;
import nro.player.Player;
import QuanLiBoss.Manager.BossManager;
import nro.server.Manager;
import models.Item.ItemService;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.ArrayUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import models.Item.ItemOption;
import nro.mob.Mob;

public class Util {

    private static final Random rand;
    private static final SimpleDateFormat dateFormat;
    private static final SimpleDateFormat dateFormatWeek;
    public static final SimpleDateFormat dateFormatDay;
    private static final Locale locale = new Locale("vi", "VN");
    private static final NumberFormat num = NumberFormat.getInstance(locale);

    static {
        rand = new Random();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatWeek = new SimpleDateFormat("yyyy-MM-ww");
        dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");
    }
    
    public static int createIdBossClone(int idPlayer) {
        return -idPlayer - 1_000_000_000;
    }

    public static boolean contains(String[] arr, String key) {
        return Arrays.toString(arr).contains(key);
    }
    
    public static String formatTimeHMS(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String formatNumber(double power, FormatStyle style) {
        Locale locale = new Locale("vi", "VN");
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(locale);

        df.setGroupingUsed(true); // phân cách hàng nghìn bằng dấu chấm
        df.setMaximumFractionDigits(1); // tối đa 3 số sau dấu phẩy
        df.setMinimumFractionDigits(0); // không ép có số lẻ nếu không cần
        df.setRoundingMode(RoundingMode.DOWN); // cắt bớt, không làm tròn

        String suffix = "";

        while (power >= 1_000_000_000) {
            power /= 1_000_000_000;
            suffix = (style == FormatStyle.KMB ? "B" : "Tỷ") + suffix;
        }
        while (power >= 1_000_000) {
            power /= 1_000_000;
            suffix = (style == FormatStyle.KMB ? "M" : "Triệu") + suffix;
        }
        while (power >= 1_000) {
            power /= 1_000;
            suffix = (style == FormatStyle.KMB ? "K" : "Nghìn") + suffix;
        }

        return df.format(power) + (suffix.isEmpty() ? "" : " " + suffix);
    }
     public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }
    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int getDistance(Player pl1, Player pl2) {
        return getDistance(pl1.location.x, pl1.location.y, pl2.location.x, pl2.location.y);
    }

    public static int getDistance(Player pl, Npc npc) {
        return getDistance(pl.location.x, pl.location.y, npc.cx, npc.cy);
    }

    public static int getDistance(Player pl, Mob mob) {
        return getDistance(pl.location.x, pl.location.y, mob.location.x, mob.location.y);
    }

    public static int getDistance(Mob mob1, Mob mob2) {
        return getDistance(mob1.location.x, mob1.location.y, mob2.location.x, mob2.location.y);
    }    
    
    public static int nextInt(int from, int to) {
        return from + rand.nextInt(to - from + 1);
    }
    
    public static float nextfloat(float from, float to) {
        return from + rand.nextFloat(to - from + 1);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static long nextLong(long from, long to) {
        return from + rand.nextLong(to - from + 1);
    }

    public static long nextLong(long max) {
        return rand.nextLong(max);
    }
    
    public static double nextDouble(double max) {
        return rand.nextDouble(max);
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }
    
    public static boolean getChanceFromLuck(double tyLeMayMan, double mum, double max, boolean isCoBonLa) {
        if (isCoBonLa) {
            max = (max * 100) / 133;
        }
        return Util.isTrue(mum + ((tyLeMayMan * max) / 300), max);
    }
    
    public static boolean isTrue(int ratio, int typeRatio) {
        if (typeRatio <= 0 || ratio <= 0) {
            return false;
        }
        if (ratio >= typeRatio) {
            return true;
        }
        return Util.nextInt(typeRatio) < ratio;
    }

    public static int getOne(int n1, int n2) {
        return rand.nextInt() % 2 == 0 ? n1 : n2;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static boolean isTrue(long ratioPercentage, long totalPercentage) {
        long num = Util.nextLong(totalPercentage);
        return num < ratioPercentage;
    }
    
    public static boolean isTrue(double ratioPercentage, double totalPercentage) {
        double num = Util.nextDouble(totalPercentage);
        return num < ratioPercentage;
    }

    public static boolean isTrue(float ratioPercentage, long totalPercentage) {
        if (ratioPercentage < 1) {
            ratioPercentage *= 100;
            totalPercentage *= 100;
        }
        return isTrue((long) ratioPercentage, totalPercentage);
    }

    public static boolean isTrue(long ratioPercentage, long totalPercentage, int accuracy) {
        return Util.nextLong(totalPercentage * accuracy) < ratioPercentage && Util.nextInt(accuracy) == 0;
    }

    public static boolean isTrue(float ratioPercentage, long totalPercentage, int accuracy) {
        if (ratioPercentage < 1) {
            ratioPercentage *= 100;
            totalPercentage *= 100;
        }
        return isTrue((long) ratioPercentage, totalPercentage, accuracy);
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }
    
    public static boolean haveSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        return b || text.contains(" ");
    }

    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
        'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
        'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
        'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
        'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
        'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
        'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
        'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
        'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
        'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
        'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
        'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
        'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
        'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
        'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
        'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
        'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
        'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
        'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
        'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
        'U', 'u', 'U', 'u',};

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }
    
    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static Object[] addArray(Object[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        Object[] arr0 = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            arr0 = ArrayUtils.addAll(arr0, arrays[i]);
        }
        return arr0;
    }

    public static int randomBossId() {
        int bossId = Util.nextInt(-1000000, -100000);
        while (BossManager.gI().getBossById(bossId) != null) {
            bossId = Util.nextInt(-1000000, 100000);
        }
        return bossId;
    }
    
    public static boolean isAfterMidnight(long currenttimemillis) {
        Instant instant = Instant.ofEpochMilli(currenttimemillis);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        LocalDate otherDate = zonedDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(otherDate);
    }

    public static boolean isAfterMidnightVietNam(long currenttimemillis) {
        Instant instant = Instant.ofEpochMilli(currenttimemillis);
        ZoneId zoneId = ZoneId.of("UTC+7");
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        LocalDate otherDate = zonedDateTime.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return currentDate.isAfter(otherDate);
    }

    public static boolean isTimeDifferenceGreaterThanNDays(long setTime, long nDays) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - setTime;
        long daysDifference = timeDifference / 86400000;
        return daysDifference >= nDays;
    }
    
    public static long getDaysSince(long setTime) {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - setTime;
        return timeDifference / 86_400_000L;
    }

    public static void threadPool(Runnable task) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                Logger.error(e + "\n");
            } finally {
                executor.shutdown();
            }
        });
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " bytes";
        } else if (bytes < 1024 * 1024) {
            return bytes / 1024 + " KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return bytes / (1024 * 1024) + " MB";
        } else {
            return bytes / (1024 * 1024 * 1024) + " GB";
        }
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
            }
        }).start();
    }

    public static String addSlashes(String input) {
        input = input.replace("\\", "\\\\");
        input = input.replace("'", "\\'");
        input = input.replace("\"", "\\\"");
        input = input.replace("\b", "\\b");
        input = input.replace("\n", "\\n");
        input = input.replace("\r", "\\r");
        input = input.replace("\t", "\\t");

        return input;
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Long.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String roundToTwoDecimals(double num) {
        double roundedNumber = Math.round(num * 100.0) / 100.0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(roundedNumber);
    }
   
    public static String strSQL(final String str) {
        return str.replaceAll("['\"\\\\%]", "\\\\$0");
    }
    
    public static long getPercent(long value, int percent) {
        return value / 100 * percent;
    }

    public static String formatCountdown(long endTimeMillis, boolean includeDays, boolean showExpired, boolean isLanguage) {
        long remaining = endTimeMillis - System.currentTimeMillis();
        if (remaining <= 0) {
            return showExpired ? "Hết hạn" : "0 giây";
        }

        long totalSeconds = remaining / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600) % 24;
        long days = totalSeconds / 86400;

        StringBuilder sb = new StringBuilder();
        if (includeDays && days > 0) {
            sb.append(days).append(isLanguage ? " ngày " : " day ");
        }
        if (hours > 0 || (includeDays && days > 0)) {
            sb.append(hours).append(isLanguage ? " giờ " : " h ");
        }
        if (minutes > 0 || sb.length() > 0) {
            sb.append(minutes).append(isLanguage ? " phút " : " m ");
        }
        sb.append(seconds).append(isLanguage ? " giây" : " s ");

        return sb.toString().trim();
    }
     
    public static String formatToSecond(long ms) {
        ms = ms - System.currentTimeMillis();
        if (ms < 0) {
            ms = 0;
        }
        long ss;
        ss = ms / 1000;
        String ssString = String.valueOf(ss);
        String time;
        if (ss != 0) {
            time = ssString + " giây";
        } else {
            time = "Hết hạn";
        }
        return time;
    }
     
     public static String formatToSecond_Minute(long power) {
        num.setMaximumFractionDigits(1);
        if (power >= 60) {
            return num.format((double) power / 60) + " phút";
        } else {
            return num.format(power);
        }
    }
     
    public static String toDateString(Date date) {
        try {
            String a = Util.dateFormat.format(date);
            return a;
        } catch (Exception e) {
            return "2025-01-01 01:01:00";
        }
    }
    
    public static synchronized Date getDate(final String dateString) {
        if (dateString == null || dateString.equals("")) {
            return new Date();
        }
        try {
            return Util.dateFormat.parse(dateString);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static long Crisnext(double from, double to) {
        return (long) (from + rand.nextInt((int) (to - from + 1)));
    }
    
    public static long CrisGH(double a) {
        if (Manager.readInt) {
            if (a > Integer.MAX_VALUE) {
                a = Integer.MAX_VALUE;
            }
            return (int) a;
        }
        return (long) a;
    }
    
    public static long Crisnext(double max) {
        return (long) rand.nextInt((int) max);
    }

    public static String format(double power) {
        return num.format(power);
    }
    
    public static String format(int power) {
        return num.format(power);
    }
    
    public static String format(long power) {
        return num.format(power);
    }
    
    public static long GioiHannext(double from, double to) {
        return (long) (from + rand.nextInt((int) (to - from + 1)));
    }

    public static double GioiHannextdame(double from, double to) {
        return from + rand.nextInt((int) (to - from + 1));
    }

    public static int currentTimeSec() {
        return (int) System.currentTimeMillis() / 1000;
    }   
    
    public static ItemMap ratiDTL(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1300)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.options.add(new ItemOption(14, new Random().nextInt(2) + 15));
        }
        it.options.add(new ItemOption(21, 18)); // ycsm 18 tỉ
        
        if (Util.isTrue(60, 100)) {
            it.options.add(new ItemOption(86, 1)); 
        } else {
            it.options.add(new ItemOption(87, 1)); 
        }
        
        if (Util.isTrue(50, 100)) {// tỉ lệ ra spl
            it.options.add(new ItemOption(107, 0));
        } else if (Util.isTrue(40, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(1, 3)));
        } else if (Util.isTrue(12, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(3, 5)));
        } else if (Util.isTrue(1, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(6, 7)));
        }
        return it;
    }
    
    public static ItemMap ratiDHD(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y), playerId);
        List<Integer> ao = Arrays.asList(650, 652, 654);
        List<Integer> quan = Arrays.asList(651, 653, 655);
        List<Integer> gang = Arrays.asList(657, 659, 661);
        List<Integer> giay = Arrays.asList(658, 660, 662);
        int nhd = 656;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, Util.nextInt(1600, 1800)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(22, Util.nextInt(90, 110)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, Util.nextInt(9000, 10500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(23, Util.nextInt(90, 110)));
        }
        if (nhd == tempId) {
            it.options.add(new ItemOption(14, Util.nextInt(16, 18)));
        }
        it.options.add(new ItemOption(21, 50)); 
        it.options.add(new ItemOption(30, 1)); 
        
        if (Util.isTrue(60, 100)) {
            it.options.add(new ItemOption(107, 0));
        } else if (Util.isTrue(35, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(1, 3)));
        } else if (Util.isTrue(8, 110)) {
            it.options.add(new ItemOption(107, Util.nextInt(4, 5)));
        } else if (Util.isTrue(1, 150)) {
            it.options.add(new ItemOption(107, 6));
        }
        return it;
    }

    public static ItemMap RaitiDoc12(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(233, 237, 241);
        List<Integer> quan = Arrays.asList(245, 249, 253);
        List<Integer> gang = Arrays.asList(257, 261, 265);
        List<Integer> giay = Arrays.asList(269, 273, 277);
        int rd12 = 281;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(121) + 350)));//giáp 350-470
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(5) + 20)));//hp 20-24k
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(51) + 2200)));//2200-2250
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(4) + 20)));//20-23k ki
        }
        if (rd12 == tempId) {
            it.options.add(new ItemOption(14, new Random().nextInt(3) + 10));//10-12cm
        }
        
        if (Util.isTrue(60, 100)) {
        it.options.add(new ItemOption(86, 1)); 
        } else {
        it.options.add(new ItemOption(87, 1)); 
        }
        
        if (Util.isTrue(40, 100)) {
            it.options.add(new ItemOption(107, 0));
        } else if (Util.isTrue(60, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(1, 3)));
        } else if (Util.isTrue(25, 100)) {
            it.options.add(new ItemOption(107, Util.nextInt(4, 5)));
        } else if (Util.isTrue(3, 100)) {
            it.options.add(new ItemOption(107, 6));
        }
        return it;
    }

    public static Item ratiItemTL(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.itemOptions.add(new ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.itemOptions.add(new ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.itemOptions.add(new ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.itemOptions.add(new ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.itemOptions.add(new ItemOption(14, new Random().nextInt(3) + 15));
        }
        it.itemOptions.add(new ItemOption(21, 15));
        return it;
    }

    public static ItemMap useItem(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> tanjiro = Arrays.asList(1087, 1088, 1091, 1090);
        if (tanjiro.contains(tempId)) {
            it.options.add(new ItemOption(77, highlightsItem(it.itemTemplate.gender == 3, new Random().nextInt(30) + 1)));
            it.options.add(new ItemOption(103, highlightsItem(it.itemTemplate.gender == 3, new Random().nextInt(30) + 1)));
            it.options.add(new ItemOption(50, highlightsItem(it.itemTemplate.gender == 3, new Random().nextInt(30) + 1)));
        }
        it.options.add(new ItemOption(209, 1)); // đồ rơi từ boss
        it.options.add(new ItemOption(30, 1)); // ko thể gd

        return it;
    }
    
    public static ItemMap dasucmanh (Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> dasucmanh = Arrays.asList(1595,1596,1597,1598);
        if (dasucmanh.contains(tempId)) {
        it.options.add(new ItemOption(209, 1)); // đồ rơi từ boss
        }
        return it;
    }
    
    public static ItemMap ngocrong1den3s (Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> dasucmanh = Arrays.asList(14,15,16);
        if (dasucmanh.contains(tempId)) {
        it.options.add(new ItemOption(86, 1)); 
        }
        return it;
    }
            
    public static ItemMap ratiItem(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.options.add(new ItemOption(14, new Random().nextInt(3) + 15));
        }
        if (Util.isTrue(60, 100)) {
        it.options.add(new ItemOption(86, 1)); 
        } else {
        it.options.add(new ItemOption(87, 1)); 
        }
        it.options.add(new ItemOption(21, 15));
        return it;
    }

    public static int highlightsItem(boolean highlights, int value) {
        double highlightsNumber = 1.1;
        return highlights ? (int) (value * highlightsNumber) : value;
    }

    public static Item sendDo(int itemId, int sql, List<ItemOption> ios) {
        Item item = ItemService.gI().createNewItem((short) itemId);
        item.itemOptions.addAll(ios);
        item.itemOptions.add(new ItemOption(107, sql));
        return item;
    }
    
    public static boolean checkDo(ItemOption itemOption) {
        switch (itemOption.optionTemplate.id) {
            case 0:// tấn công
                if (itemOption.param > 12000) {
                    return false;
                }
                break;
            case 14:// chí mạng
                if (itemOption.param > 30) {
                    return false;
                }
                break;
            case 107:// spl
            case 102:// spl
                if (itemOption.param > 8) {
                    return false;
                }
                break;
            case 77:
            case 103:
            case 95:
            case 96:
                if (itemOption.param > 41) {
                    return false;
                }
                break;
            case 50:// sd 3%
                if (itemOption.param > 24) {
                    return false;
                }
                break;
            case 6:// hp
            case 7:// ki
                if (itemOption.param > 120000) {
                    return false;
                }
                break;
            case 47:// giáp
                if (itemOption.param > 3500) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static void useCheckDo(Player player, Item item, String position) {
        try {
            if (item.template != null) {
                if (item.template.id >= 381 && item.template.id <= 385) {
                    return;
                }
                if (item.template.id >= 66 && item.template.id <= 135) {
                    return;
                }
                if (item.template.id >= 474 && item.template.id <= 515) {
                    return;
                }
                item.itemOptions.forEach(itemOption -> {
                    if (!Util.checkDo(itemOption)) {
                        Logger.error(player.name + "-" + item.template.name + "-" + position + "\n");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long tinhLuyThua(int coSo, int soMu) {
        long ketQua = 1;

        for (int i = 0; i < soMu; i++) {
            ketQua *= coSo;
        }
        return ketQua;
    }

    public static String convertMillisecondsDouble(long milliseconds) {
        double seconds = (double) milliseconds / 1000;
        return String.format("%.2f", seconds).replace('.', ',');
    }
    
    public static String convertSecondsToTime(long totalSeconds) {
        if (totalSeconds < 60) {
            return totalSeconds + "s trước";
        } else if (totalSeconds < 3600) {
            long minutes = totalSeconds / 60;
            return minutes + "p" + " trước";
        } else if (totalSeconds < 86400) {
            long hours = totalSeconds / 3600;
            return hours + "g" + " trước";
        } else {
            long days = totalSeconds / 86400;
            return days + "n" + " trước";
        }
    }
    
    public static String convertMilliseconds(long ms) {
        long seconds = ms / 1000 % 60;
        long minutes = ms / (1000 * 60) % 60;
        long hours = ms / (1000 * 60 * 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    public static String convertMilliseconds_ms(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        return minutes + " m " + seconds + " s";
    }
    
    public static String convertMilliseconds_Giay(long milliseconds) {
        if (milliseconds < 0) milliseconds = 0;
        long seconds = milliseconds / 1000;
        return seconds + " giây";
    }
    
    public static boolean isAfterDay(Date now, Date when) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = df.parse(df.format(now));
            Date d2 = df.parse(df.format(when));
            return d1.after(d2);
        } catch (ParseException e) {
            return false;
        }
    }
    
    public static void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
    
    public static byte getHead(byte gender) {
        switch (gender) {
            case 2:
                return 28;
            case 1:
                return 32;
            default:
                return 64;
        }
    }

    public static byte getLeg(byte gender) {
        switch (gender) {
            case 2:
                return 17;
            case 1:
                return 11;
            default:
                return 15;
        }
    }

    public static byte getBody(byte gender) {
        switch (gender) {
            case 2:
                return 16;
            case 1:
                return 10;
            default:
                return 14;
        }
    }
}
