package nro.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiddosServiceV2 {

    public interface UiCallbacks {
        void onLog(String msg);
        void onBlockedAdded(String ip, int connections, String reason, String time);
        void onBlockedRemoved(String ip);
        void onClearBlockedTable();
    }

    public static class DbConfig {
        public final String host;
        public final String db;
        public final String user;
        public final String pass;

        public DbConfig(String host, String db, String user, String pass) {
            this.host = host;
            this.db = db;
            this.user = user;
            this.pass = pass;
        }
    }

    private final UiCallbacks ui;
    private int apiPort = 12345;
    private String token = "anwinV2";
    private int gamePort = 14445;
    private int connLimitPerIp = 10;
    private int scanSeconds = 60;
    private DbConfig dbConfig = new DbConfig("localhost", "anwinvip", "root", "");

    private volatile boolean running = false;
    private volatile boolean autoScanEnabled = false;
    private volatile boolean lockdownMode = false;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "AntiDDoS-AutoScan");
        t.setDaemon(true);
        return t;
    });

    private ScheduledFuture<?> scanTask;

    private static final String RULE_BLOCK_MULTI = "Block Multiple IPs";
    private static final String RULE_LOCKDOWN_PREFIX = "Lockdown GamePort ";

    public AntiddosServiceV2(UiCallbacks ui) {
        this.ui = ui;
    }

    public void configure(int apiPort, String token, int gamePort, int limit, int scanSeconds, DbConfig db) {
        this.apiPort = apiPort;
        this.token = token;
        this.gamePort = gamePort;
        this.connLimitPerIp = Math.max(1, limit);
        this.scanSeconds = Math.max(5, scanSeconds);
        this.dbConfig = db;
    }

    public void startAll() {
        running = true;
        if (!autoScanEnabled) {
            autoScanEnabled = true;
            scheduleScan();
        } else {
            scheduleScan();
        }
        ui.onLog("Service started. AutoScan=" + autoScanEnabled);
        reloadBlockedFromDbToUi();
    }

    public void stopAll() {
        running = false;
        autoScanEnabled = false;
        if (scanTask != null) scanTask.cancel(true);
        ui.onLog("Service stopped.");
    }

    public void toggleAutoScan() {
        autoScanEnabled = !autoScanEnabled;
        ui.onLog("AutoScan set to " + autoScanEnabled);
        scheduleScan();
    }

    public boolean isAutoScanEnabled() {
        return autoScanEnabled;
    }

    public boolean isLockdownMode() {
        return lockdownMode;
    }

    public void enableLockdown() {
        String rule = RULE_LOCKDOWN_PREFIX + gamePort;
        try {
            addLockdownRule(rule, gamePort);
            lockdownMode = true;
            ui.onLog("Lockdown enabled for port " + gamePort);
        } catch (Exception e) {
            ui.onLog("Lockdown enable FAILED: " + e.getMessage());
        }
    }

    public void disableLockdown() {
        String rule = RULE_LOCKDOWN_PREFIX + gamePort;
        try {
            deleteFirewallRule(rule);
            lockdownMode = false;
            ui.onLog("Lockdown disabled for port " + gamePort);
        } catch (Exception e) {
            ui.onLog("Lockdown disable FAILED: " + e.getMessage());
        }
    }

    public void unblockAll() {
        try {
            deleteFirewallRule(RULE_BLOCK_MULTI);
        } catch (Exception ignored) {}
        clearDbAll();
        ui.onClearBlockedTable();
        ui.onLog("Unblocked all (firewall + DB cleared).");
    }
    public void syncDbToFirewall() {
        Set<String> ips = new LinkedHashSet<>(getAllBlockedIpsFromDb());
        if (ips.isEmpty()) {
            try { deleteFirewallRule(RULE_BLOCK_MULTI); } catch (Exception ignored) {}
            ui.onLog("DB empty -> removed firewall rule if existed.");
            return;
        }
        try {
            updateBlockMultiRule(ips);
            ui.onLog("Synced " + ips.size() + " IPs from DB to firewall rule.");
        } catch (Exception e) {
            ui.onLog("Sync FAILED: " + e.getMessage());
        }
    }
    private void scheduleScan() {
        if (scanTask != null) scanTask.cancel(false);
        if (!running || !autoScanEnabled) return;

        scanTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                scanOnce();
            } catch (Exception e) {
                ui.onLog("Scan error: " + e.getMessage());
            }
        }, 0, scanSeconds, TimeUnit.SECONDS);
    }

    private void scanOnce() {
        if (!running) return;

        Map<String, Integer> connByIp = getConnectionsByIp(gamePort);
        if (connByIp.isEmpty()) {
            ui.onLog("Scan: no active connections on port " + gamePort);
            cleanupFirewallIpsNotInDb();
            return;
        }
        for (Map.Entry<String, Integer> e : connByIp.entrySet()) {
            String ip = e.getKey();
            int cnt = e.getValue();
            if (cnt > connLimitPerIp) {
                blockIp(ip, "Conn/IP=" + cnt + " > " + connLimitPerIp, cnt);
            }
        }

        cleanupFirewallIpsNotInDb();
        ui.onLog("Scan done. Active IPs=" + connByIp.size());
    }
    private Map<String, Integer> getConnectionsByIp(int port) {
        String cmd = "cmd.exe /c netstat -nao | findstr :" + port;
        List<String> lines = execLines(cmd);

        Map<String, Integer> map = new HashMap<>();
        for (String line : lines) {
            String s = line.trim();
            if (s.isEmpty()) continue;

            String[] parts = s.split("\\s+");
            if (parts.length < 3) continue;
            String local = parts[1];
            if (!local.endsWith(":" + port) && !local.endsWith("]:" + port)) continue;

            String foreign = parts[2];
            String ip = extractIpFromNetstatAddress(foreign);
            if (ip == null || ip.equals("0.0.0.0") || ip.equals("127.0.0.1") || ip.equals("::") || ip.equals("::1")) continue;

            map.put(ip, map.getOrDefault(ip, 0) + 1);
        }
        return map;
    }

    private String extractIpFromNetstatAddress(String addr) {
        if (addr.startsWith("[")) {
            int r = addr.indexOf(']');
            if (r > 1) return addr.substring(1, r);
            return null;
        }
        int idx = addr.lastIndexOf(':');
        if (idx > 0) return addr.substring(0, idx);
        return null;
    }
    private void blockIp(String ip, String reason, int connections) {

        if (isIpInDb(ip)) return;

        try {
            addIpToDb(ip, reason);
            Set<String> ips = new LinkedHashSet<>(getAllBlockedIpsFromDb());
            ips.add(ip);
            updateBlockMultiRule(ips);

            String time = now();
            ui.onBlockedAdded(ip, connections, reason, time);
            ui.onLog("BLOCKED " + ip + " (" + reason + ")");
        } catch (Exception e) {
            ui.onLog("Block FAILED for " + ip + ": " + e.getMessage());
        }
    }

    private void updateBlockMultiRule(Set<String> ips) {
        String ipList = String.join(",", ips);
        try { deleteFirewallRule(RULE_BLOCK_MULTI); } catch (Exception ignored) {}
        addFirewallRule(RULE_BLOCK_MULTI, ipList);
    }

    private void addFirewallRule(String ruleName, String remoteIps) {
        String cmd = "cmd.exe /c netsh advfirewall firewall add rule name=\"" + ruleName +
                "\" dir=in action=block remoteip=" + remoteIps;
        execOk(cmd);
    }

    private void deleteFirewallRule(String ruleName) {
        String cmd = "cmd.exe /c netsh advfirewall firewall delete rule name=\"" + ruleName + "\"";
        execOk(cmd);
    }

    private void addLockdownRule(String ruleName, int localPort) {
        String cmd = "cmd.exe /c netsh advfirewall firewall add rule name=\"" + ruleName +
                "\" dir=in action=block protocol=TCP localport=" + localPort;
        execOk(cmd);
    }

    private void cleanupFirewallIpsNotInDb() {
        Set<String> dbIps = new LinkedHashSet<>(getAllBlockedIpsFromDb());
        if (dbIps.isEmpty()) {
            try { deleteFirewallRule(RULE_BLOCK_MULTI); } catch (Exception ignored) {}
            return;
        }
        try {
            updateBlockMultiRule(dbIps);
        } catch (Exception e) {
            ui.onLog("Cleanup firewall failed: " + e.getMessage());
        }
    }
    private Connection openDb() throws SQLException {
        String url = "jdbc:mysql://" + dbConfig.host + ":3306/" + dbConfig.db +
                "?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
        return DriverManager.getConnection(url, dbConfig.user, dbConfig.pass);
    }

    private void addIpToDb(String ip, String reason) {
        String sql = "INSERT INTO blockip_list (blocker_ip, block_reason, blocked_at) VALUES (?, ?, NOW())";
        try (Connection c = openDb();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ip);
            ps.setString(2, reason);
            ps.executeUpdate();
        } catch (SQLException e) {
            ui.onLog("DB insert failed (maybe duplicate): " + e.getMessage());
        }
    }

    private boolean isIpInDb(String ip) {
        String sql = "SELECT 1 FROM blockip_list WHERE blocker_ip=? LIMIT 1";
        try (Connection c = openDb();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ip);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            ui.onLog("DB check failed: " + e.getMessage());
            return false;
        }
    }

    private List<String> getAllBlockedIpsFromDb() {
        String sql = "SELECT blocker_ip FROM blockip_list";
        List<String> ips = new ArrayList<>();
        try (Connection c = openDb();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) ips.add(rs.getString(1));
        } catch (SQLException e) {
            ui.onLog("DB read failed: " + e.getMessage());
        }
        return ips;
    }

    private void clearDbAll() {
        String sql = "DELETE FROM blockip_list";
        try (Connection c = openDb();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            ui.onLog("DB clear failed: " + e.getMessage());
        }
    }

    private void reloadBlockedFromDbToUi() {
        ui.onClearBlockedTable();
        String sql = "SELECT blocker_ip, block_reason, blocked_at FROM blockip_list ORDER BY blocked_at DESC";
        try (Connection c = openDb();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String ip = rs.getString(1);
                String reason = rs.getString(2);
                Timestamp t = rs.getTimestamp(3);
                String time = t != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(t) : now();
                ui.onBlockedAdded(ip, -1, reason, time);
            }
        } catch (SQLException e) {
            ui.onLog("DB load->UI failed: " + e.getMessage());
        }
    }
    private void execOk(String cmd) {
        List<String> out = execLines(cmd);
    }

    private List<String> execLines(String cmd) {
        List<String> lines = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("cmd.exe", "/c", cmd.replace("cmd.exe /c ", ""))
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
            }
            p.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            ui.onLog("Exec failed: " + e.getMessage());
        }
        return lines;
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}





