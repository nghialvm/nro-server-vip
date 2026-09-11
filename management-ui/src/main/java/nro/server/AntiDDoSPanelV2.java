package nro.server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AntiDDoSPanelV2 extends JPanel {

    private final DefaultTableModel tableModel;
    private final JTable tableBlocked;
    private final JTextArea logArea;

    private final JButton btnStart = new JButton("START");
    private final JButton btnStop = new JButton("STOP");
    private final JButton btnAutoScan = new JButton("Auto Scan Netstat: OFF");
    private final JButton btnLockdown = new JButton("Lockdown Mode: OFF");
    private final JButton btnUnblockAll = new JButton("Unblock All IPs");
    private final JButton btnSyncFromDb = new JButton("Sync DB -> Firewall");

    private final JTextField txtApiPort = new JTextField("12345", 6);
    private final JTextField txtToken   = new JTextField("", 12);

    private final JTextField txtGamePort = new JTextField("14445", 6);
    private final JTextField txtConnLimit = new JTextField("10", 4);
    private final JTextField txtScanSeconds = new JTextField("60", 4);

    private final JTextField txtDbHost = new JTextField("localhost", 10);
    private final JTextField txtDbName = new JTextField("anwinvip", 8);
    private final JTextField txtDbUser = new JTextField("root", 8);
    private final JPasswordField txtDbPass = new JPasswordField("", 8);

    private final AntiddosServiceV2 service;

    private static final Color BG_WHITE = Color.WHITE;
    private static final Color BORDER   = new Color(220, 220, 220);
    private static final Color TEXT     = new Color(40, 40, 40);

    public AntiDDoSPanelV2() {
        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(BG_WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        top.setBackground(BG_WHITE);
        top.setBorder(new TitledBorder(BorderFactory.createLineBorder(BORDER), "Anti-DDoS Controls"));

        styleButton(btnStart);
        styleButton(btnStop);
        styleButton(btnAutoScan);
        styleButton(btnLockdown);
        styleButton(btnUnblockAll);
        styleButton(btnSyncFromDb);

        top.add(btnStart);
        top.add(btnStop);
        top.add(btnAutoScan);
        top.add(btnLockdown);
        top.add(btnUnblockAll);
        top.add(btnSyncFromDb);

        top.add(Box.createHorizontalStrut(10));
        top.add(label("API Port:")); top.add(txtApiPort);
        top.add(label("Token:"));    top.add(txtToken);

        top.add(Box.createHorizontalStrut(10));
        top.add(label("Game Port:"));     top.add(txtGamePort);
        top.add(label("Conn/IP Limit:")); top.add(txtConnLimit);
        top.add(label("Scan(s):"));       top.add(txtScanSeconds);

        top.add(Box.createHorizontalStrut(10));
        top.add(label("DB Host:")); top.add(txtDbHost);
        top.add(label("DB:"));      top.add(txtDbName);
        top.add(label("User:"));    top.add(txtDbUser);
        top.add(label("Pass:"));    top.add(txtDbPass);

        add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"IP Address", "Connections", "Reason", "Time Blocked"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableBlocked = new JTable(tableModel);
        tableBlocked.setRowHeight(26);
        tableBlocked.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableBlocked.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableBlocked.setBackground(BG_WHITE);
        tableBlocked.setForeground(TEXT);
        tableBlocked.setGridColor(new Color(210, 210, 210));
        tableBlocked.setShowGrid(true);
        tableBlocked.setFillsViewportHeight(true);

        JScrollPane scrollBlocked = new JScrollPane(tableBlocked);
        scrollBlocked.getViewport().setBackground(BG_WHITE);
        scrollBlocked.setBorder(new TitledBorder(BorderFactory.createLineBorder(BORDER), "Blocked IPs"));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(BG_WHITE);
        logArea.setForeground(TEXT);
        logArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.getViewport().setBackground(BG_WHITE);
        scrollLog.setBorder(new TitledBorder(BorderFactory.createLineBorder(BORDER), "System Logs"));
        scrollLog.setPreferredSize(new Dimension(100, 240));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(BG_WHITE);
        center.add(scrollBlocked, BorderLayout.CENTER);
        center.add(scrollLog, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        service = new AntiddosServiceV2(new AntiddosServiceV2.UiCallbacks() {
            @Override
            public void onLog(String msg) {
                SwingUtilities.invokeLater(() -> appendUi(msg));
            }

            @Override
            public void onBlockedAdded(String ip, int connections, String reason, String time) {
                SwingUtilities.invokeLater(() -> tableModel.addRow(new Object[]{ip, connections, reason, time}));
            }

            @Override
            public void onBlockedRemoved(String ip) {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (ip.equals(tableModel.getValueAt(i, 0))) {
                            tableModel.removeRow(i);
                            break;
                        }
                    }
                });
            }

            @Override
            public void onClearBlockedTable() {
                SwingUtilities.invokeLater(() -> tableModel.setRowCount(0));
            }
        });

        btnStart.addActionListener(e -> {
            int apiPort = parseInt(txtApiPort.getText().trim(), 12345);
            String token = txtToken.getText().trim();
            if (token.isEmpty()) token = "anwinV2";

            int gamePort = parseInt(txtGamePort.getText().trim(), 14445);
            int limit = parseInt(txtConnLimit.getText().trim(), 10);
            int scanSec = parseInt(txtScanSeconds.getText().trim(), 60);

            AntiddosServiceV2.DbConfig db = new AntiddosServiceV2.DbConfig(
                    txtDbHost.getText().trim(),
                    txtDbName.getText().trim(),
                    txtDbUser.getText().trim(),
                    new String(txtDbPass.getPassword())
            );

            service.configure(apiPort, token, gamePort, limit, scanSec, db);
            service.startAll();
            appendUi("START OK (gamePort=" + gamePort + ", limit=" + limit + ", scan=" + scanSec + "s)");
        });

        btnStop.addActionListener(e -> {
            service.stopAll();
            appendUi("STOP OK");
        });

        btnAutoScan.addActionListener(e -> {
            service.toggleAutoScan();
            btnAutoScan.setText("Auto Scan Netstat: " + (service.isAutoScanEnabled() ? "ON" : "OFF"));
        });

        btnLockdown.addActionListener(e -> {
            if (!service.isLockdownMode()) service.enableLockdown();
            else service.disableLockdown();
            btnLockdown.setText("Lockdown Mode: " + (service.isLockdownMode() ? "ON" : "OFF"));
        });

        btnUnblockAll.addActionListener(e -> {
            service.unblockAll();
            appendUi("Unblock all requested.");
        });

        btnSyncFromDb.addActionListener(e -> {
            service.syncDbToFirewall();
            appendUi("Sync DB -> Firewall requested.");
        });
    }

    private JLabel label(String s) {
        JLabel lb = new JLabel(s);
        lb.setForeground(TEXT);
        return lb;
    }

    private void appendUi(String msg) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        logArea.append("[" + time + "] " + msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private void styleButton(JButton b) {
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setBackground(Color.WHITE);
        b.setForeground(TEXT);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(6, 12, 6, 12)
        ));
    }
}
