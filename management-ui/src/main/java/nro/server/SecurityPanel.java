package nro.server;

import Utils.Logger;
import com.google.gson.Gson;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class SecurityPanel extends JPanel {

    // --- UI Components ---
    private JTable tableBlocked;
    private DefaultTableModel modelBlocked;
    private JTextArea logArea;
    private JToggleButton btnLockdown;
    private JToggleButton btnAutoScan;

    // --- Logic Variables ---
    private final Set<String> blockedIPs = ConcurrentHashMap.newKeySet();
    private final Set<String> whiteList = ConcurrentHashMap.newKeySet();
    private final Map<String, Integer> ipHitCount = new ConcurrentHashMap<>();
    private final Map<String, Long> ipLastSeen = new ConcurrentHashMap<>();
    
    private ScheduledExecutorService ddosScheduler;
    private ScheduledFuture<?> autoScanFuture;
    private volatile boolean ddosRunning = true;
    
    // Config ngưỡng tấn công (Packet Per Second)
    private static final int RATE_LIMIT_PPS = 300; 

    public SecurityPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Khởi tạo WhiteList (IP Local)
        whiteList.add("127.0.0.1");
        whiteList.add("localhost");

        initControls();
        initViews();
        initAntiDdosSystem();
    }

    private void initControls() {
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        ctrl.setOpaque(false);
        ctrl.setBorder(ServerGuiUtils.createSectionBorder("Anti-DDoS Controls"));

        // Button Auto Scan
        btnAutoScan = new JToggleButton("Auto Scan Netstat: OFF");
        btnAutoScan.addActionListener(e -> toggleAutoScan());

        // Button Lockdown
        btnLockdown = new JToggleButton("Lockdown Mode: OFF");
        btnLockdown.setBackground(new Color(255, 240, 240));
        btnLockdown.addActionListener(e -> {
            boolean on = btnLockdown.isSelected();
            btnLockdown.setText("Lockdown Mode: " + (on ? "ON" : "OFF"));
            btnLockdown.setBackground(on ? Color.RED : new Color(255, 240, 240));
            if (on) logDdos("!!! LOCKDOWN ENABLED !!!");
            else logDdos("Lockdown Disabled.");
        });

        // Button Unblock All
        JButton btnUnblockAll = ServerGuiUtils.createStyledButton("Unblock All IPs", new Color(0, 120, 215), Color.WHITE);
        btnUnblockAll.addActionListener(e -> unblockAllIps());

        ctrl.add(btnAutoScan);
        ctrl.add(btnLockdown);
        ctrl.add(btnUnblockAll);
        
        add(ctrl, BorderLayout.NORTH);
    }

    private void initViews() {
        // Table Panel
        modelBlocked = new DefaultTableModel(new String[]{"IP Address", "Reason", "Time Blocked"}, 0);
        tableBlocked = new JTable(modelBlocked);
        tableBlocked.setRowHeight(22);
        JScrollPane scrollTable = new JScrollPane(tableBlocked);
        scrollTable.setBorder(ServerGuiUtils.createSectionBorder("Blocked IPs"));

        // Log Panel
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setBorder(ServerGuiUtils.createSectionBorder("System Logs"));

        // Split Pane
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, scrollLog);
        split.setDividerLocation(300); // Chiều cao mặc định của bảng
        split.setResizeWeight(0.5);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);
    }

    // ================= LOGIC HỆ THỐNG =================

    private void initAntiDdosSystem() {
        ddosScheduler = Executors.newScheduledThreadPool(2);
        
        // 1. Load danh sách IP đã chặn từ file (nếu có)
        loadDdosState();
        
        // 2. Bắt đầu lắng nghe gói tin
        startPacketCapture();
        
        // 3. Cơ chế dọn dẹp bộ nhớ đệm (Reset đếm packet mỗi giây)
        ddosScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            // Xóa các IP không hoạt động quá 60s khỏi bộ nhớ đệm
            ipHitCount.keySet().removeIf(ip -> now - ipLastSeen.getOrDefault(ip, 0L) > 60000);
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void startPacketCapture() {
        ddosScheduler.execute(() -> {
            try {
                // Tìm card mạng khả dụng (Không phải Loopback)
                PcapNetworkInterface nif = Pcaps.findAllDevs().stream()
                        .filter(i -> !i.isLoopBack() && !i.getAddresses().isEmpty())
                        .findFirst().orElse(null);

                if (nif == null) {
                    logDdos("Error: No Network Interface found!");
                    return;
                }

                logDdos("Listening on: " + nif.getDescription());
                
                // Mở handle để bắt gói tin
                int snapshotLength = 65536; // Độ dài gói tin tối đa
                int readTimeout = 10; // ms
                try (PcapHandle handle = nif.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout)) {
                    
                    while (ddosRunning) {
                        try {
                            Packet packet = handle.getNextPacketEx();
                            IpV4Packet ipPkt = packet.get(IpV4Packet.class);
                            
                            if (ipPkt != null) {
                                String ip = ipPkt.getHeader().getSrcAddr().getHostAddress();
                                
                                // Nếu IP không nằm trong whitelist và chưa bị chặn
                                if (!whiteList.contains(ip) && !blockedIPs.contains(ip)) {
                                    ipHitCount.merge(ip, 1, Integer::sum);
                                    ipLastSeen.put(ip, System.currentTimeMillis());
                                    
                                    // Kiểm tra ngưỡng tấn công
                                    if (ipHitCount.get(ip) > RATE_LIMIT_PPS) {
                                        blockIP(ip, "High PPS (" + ipHitCount.get(ip) + ")");
                                    }
                                }
                            }
                        } catch (TimeoutException ignored) {
                        } catch (NotOpenException e) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logDdos("PCAP Error (Run Admin/Root): " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void blockIP(String ip, String reason) {
        if (blockedIPs.add(ip)) {
            // Update UI
            SwingUtilities.invokeLater(() -> modelBlocked.addRow(new Object[]{ip, reason, LocalTime.now().toString()}));
            logDdos("BLOCKED: " + ip + " - Reason: " + reason);
            
            // Execute OS Command
            try {
                String cmd;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // Windows Firewall Command
                    cmd = "netsh advfirewall firewall add rule name=\"Block_" + ip + "\" dir=in action=block remoteip=" + ip;
                } else {
                    // Linux Iptables Command
                    cmd = "iptables -A INPUT -s " + ip + " -j DROP";
                }
                Runtime.getRuntime().exec(cmd);
                saveDdosState();
            } catch (IOException e) {
                logDdos("Cmd Error: " + e.getMessage());
            }
        }
    }

    private void unblockAllIps() {
        if (blockedIPs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Danh sách trống.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this, "Gỡ chặn tất cả IP?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            for (String ip : new HashSet<>(blockedIPs)) {
                try {
                    String cmd;
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        cmd = "netsh advfirewall firewall delete rule name=\"Block_" + ip + "\"";
                    } else {
                        cmd = "iptables -D INPUT -s " + ip + " -j DROP";
                    }
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException ignored) {}
            }
            
            blockedIPs.clear();
            ipHitCount.clear();
            SwingUtilities.invokeLater(() -> modelBlocked.setRowCount(0));
            logDdos("All IPs Unblocked.");
            saveDdosState();
        }
    }

    // --- Helpers ---

    private void toggleAutoScan() {
        boolean enabled = btnAutoScan.isSelected();
        btnAutoScan.setText("Auto Scan Netstat: " + (enabled ? "ON" : "OFF"));
        
        if (enabled) {
            autoScanFuture = ddosScheduler.scheduleAtFixedRate(() -> {
                // Logic quét Netstat (giả lập hoặc implement thực tế nếu cần)
                // logDdos("Scanning connections...");
            }, 0, 30, TimeUnit.SECONDS);
        } else {
            if (autoScanFuture != null) autoScanFuture.cancel(true);
        }
    }

    private void logDdos(String msg) {
        SwingUtilities.invokeLater(() -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append("[" + time + "] " + msg + "\n");
            // Auto scroll
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    // --- Save/Load State ---

    private void saveDdosState() {
        try {
            Files.writeString(Paths.get("ddos_blocked.json"), new Gson().toJson(blockedIPs));
        } catch (Exception e) {
            Logger.error("Lỗi lưu file ddos: " + e.getMessage());
        }
    }

    private void loadDdosState() {
        try {
            File f = new File("ddos_blocked.json");
            if (f.exists()) {
                Set<String> s = new Gson().fromJson(Files.readString(f.toPath()), Set.class);
                if (s != null) {
                    blockedIPs.addAll(s);
                    // Đổ lại dữ liệu vào bảng
                    SwingUtilities.invokeLater(() -> {
                        for (String ip : blockedIPs) {
                            modelBlocked.addRow(new Object[]{ip, "Restored", "Previous Session"});
                        }
                    });
                    logDdos("Restored " + blockedIPs.size() + " blocked IPs.");
                }
            }
        } catch (Exception e) {
            logDdos("Lỗi load file ddos.");
        }
    }
    
    // Hàm gọi khi tắt server để giải phóng tài nguyên
    public void stop() {
        ddosRunning = false;
        if (ddosScheduler != null) ddosScheduler.shutdownNow();
    }
}