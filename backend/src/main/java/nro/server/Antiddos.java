package nro.server;

import Utils.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author MaiTienDung
 */
public class Antiddos {

    private static final int MAX_REQUESTS_PER_SECOND = 30;
    private static final long BLOCK_DURATION = 120 * 1000;

    private static final Map<String, Integer> ipRequestCount = new HashMap<>();
    private static final Map<String, Timer> blockedIps = new HashMap<>();

    private static boolean initialized = false;

    public static synchronized void handleRequest(String ipAddress) {
        if (!initialized) {
            initialized = true;
            antiDdosSuccess();
        }

        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }

        if (isBlocked(ipAddress)) {
            Logger.antiDdosBlock("IP đang bị chặn | ip=" + ipAddress);
            return;
        }

        int count = ipRequestCount.getOrDefault(ipAddress, 0) + 1;
        ipRequestCount.put(ipAddress, count);

        if (count > MAX_REQUESTS_PER_SECOND) {
            blockIP(ipAddress);
        }
    }

    private static synchronized void blockIP(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }

        if (blockedIps.containsKey(ipAddress)) {
            return;
        }

        Timer timer = new Timer("AntiDDOS-Unblock-" + ipAddress);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                unblockIP(ipAddress);
            }
        }, BLOCK_DURATION);

        blockedIps.put(ipAddress, timer);
        ipRequestCount.put(ipAddress, 0);

        Logger.antiDdosBlock(
                "BLOCK IP"
                + " | ip=" + ipAddress
                + " | time=" + (BLOCK_DURATION / 1000) + "s"
                + " | max=" + MAX_REQUESTS_PER_SECOND + "/s"
        );
    }

    private static synchronized void unblockIP(String ipAddress) {
        Timer timer = blockedIps.remove(ipAddress);

        if (timer != null) {
            timer.cancel();
        }

        ipRequestCount.put(ipAddress, 0);

        Logger.antiDdosUnblock(
                "UNBLOCK IP"
                + " | ip=" + ipAddress
                + " | sau=" + (BLOCK_DURATION / 1000) + "s"
        );
    }

    public static synchronized boolean isBlocked(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        return blockedIps.containsKey(ipAddress);
    }

    public static synchronized void clearIp(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }

        Timer timer = blockedIps.remove(ipAddress);

        if (timer != null) {
            timer.cancel();
        }

        ipRequestCount.remove(ipAddress);

        Logger.antiDdosInfo("CLEAR IP | ip=" + ipAddress);
    }

    private static void antiDdosSuccess() {
        Logger.antiDdosTitle("KHỞI TẠO BẢO VỆ");
        Logger.antiDdos(
                "Anti-DDOS đã được khởi tạo thành công"
                + " | max=" + MAX_REQUESTS_PER_SECOND + "/s"
                + " | block=" + (BLOCK_DURATION / 1000) + "s"
        );
    }
}