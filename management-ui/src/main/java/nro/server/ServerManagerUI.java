package nro.server;

import AnwinManager.AnwinManager;
import nro.bot.BotManager;
import Utils.Logger;
import event.EventManager;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.Socket;
import java.time.Instant;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.border.EmptyBorder;
import jbcd.dao.EventDAO;
import network.Network;
import network.session.MySession;
import network.session.SessionManager;
import nro.bot.New.BotManager_new;
import nro.clan.ClanService;
import nro.consignmentstore.ConsignShopManager;
import nro.server.Proxy.ProxyManager;

//public final class ServerManagerUI extends JFrame {
//
//    private Preferences preferences;
//    private JLabel plCountLabel;
//    private JLabel SessionCountLabel;
//    private JLabel botCountLabel;
//    private JLabel botCountLabel_new;
//    private JLabel threadCountLabel;
//    private JLabel EventCountLabel;
//    private JLabel TypeDataCountLabel;
//    private JTextField minutesField;
//    private JLabel messageLabel;
//    private JLabel countdownLabel;
//    private Timer countdownTimer;
//    private int remainingSeconds;
//    private ButtonGroup maintenanceGroup;
//    private JCheckBox maintenanceOption1;
//    private JCheckBox maintenanceOption2;
//    private JLabel info;
//    public static boolean isRunning;
//
//    public ServerManagerUI() {
//        preferences = Preferences.userNodeForPackage(ServerManagerUI.class);
//        setTitle("Ngọc Rồng Tuổi Thơ");
//        setSize(500, 300);
//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//        setLocationRelativeTo(null);
//        addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                confirmExit();
//            }
//        });
//        JPanel panel = new JPanel();
//        getContentPane().add(panel);
//        panel.setLayout(new GridLayout(0, 2));
//        JButton maintenanceButton = new JButton("BẢO_TRÌ");
//        maintenanceButton.addActionListener(e -> showMaintenanceDialog());
//        panel.add(maintenanceButton);
//        JButton maintenanceButton1 = new JButton("KICK_ALL_PLAYER");
//        maintenanceButton1.addActionListener(e -> kick());
//        panel.add(maintenanceButton1);
//        JButton maintenanceButton2 = new JButton("THAY_EXP");
//        maintenanceButton2.addActionListener(e -> tnsm());
//        panel.add(maintenanceButton2);
//        
//        JButton maintenanceButton3 = new JButton("START_ANTIDDOS");
//        maintenanceButton3.addActionListener(e -> startAntiDDoS());
//        panel.add(maintenanceButton3);
//        
//        JButton saveButton = new JButton("SAVE_DATA");
//        saveButton.addActionListener((ActionEvent e) -> {
//            Logger.success("Đang tiến hành lưu data");
//            Network.gI().stopConnect();
//            
//            Maintenance.isRunning = false;
//            try {
//                Logger.error("Đang tiến hành lưu data bang hội");
//                ClanService.gI().close();
//                Thread.sleep(1000);
//                Logger.success("Lưu dữ liệu bang hội thành công");
//            } catch (InterruptedException ex) {
//                Logger.error("Lỗi lưu dữ liệu bang hội");
//            }
//            try {
//                Logger.error("Đang tiến hành lưu data ký gửi");
//                ConsignShopManager.gI().save();
//                Thread.sleep(1000);
//                Logger.success("Lưu dữ liệu ký gửi thành công");
//            } catch (InterruptedException ex) {
//                Logger.error("Lỗi lưu dữ liệu ký gửi");
//            }
//            
//            try {
//                Logger.error("Đang tiến hành đẩy người chơi");
//                Client.gI().close();
//                EventDAO.save();
//                Thread.sleep(1000);
//                Logger.success("Lưu dữ liệu người dùng thành công");
//            } catch (InterruptedException ex) {
//                Logger.error("Lỗi lưu dữ liệu người dùng");
//            }
//            System.exit(0);
//        });
//        panel.add(saveButton);
//        
//        JButton clearFw = new JButton("CLEAR_FIREWALL");
//        clearFw.addActionListener((ActionEvent e) -> {
//            JOptionPane.showMessageDialog(null, "Đã clear firewall");
//        });
//        panel.add(clearFw);
//        
//        JButton loadshop = new JButton("LOADING_SHOP");
//        loadshop.addActionListener((ActionEvent e) -> {
//            Manager.gI().updateShop();
//            JOptionPane.showMessageDialog(null, "Đã Update Shop.");
//        });
//        panel.add(loadshop);
//        
//        JButton loadtop = new JButton("LOADING_TOP");
//        loadtop.addActionListener((ActionEvent e) -> {
//            TopServer.LoadingTop();
//            JOptionPane.showMessageDialog(null, "Đã Update TOP.");
//        });
//        panel.add(loadtop);
//        
//        info = new JLabel("");
//        // Đọc giá trị từ tệp tin
//        try (BufferedReader reader = new BufferedReader(new FileReader("maintenanceConfig.txt"))) {
//            String hoursLine = reader.readLine();
//            String minutesLine = reader.readLine();
//
//            int hours = Integer.parseInt(hoursLine);
//            int minutes = Integer.parseInt(minutesLine);
//
//            // Thêm giá trị vào DefaultComboBoxModel
//            DefaultComboBoxModel<Integer> hoursModel = new DefaultComboBoxModel<>();
//            for (int i = -1; i < 24; i++) {
//                hoursModel.addElement(i);
//            }
//            JComboBox<Integer> hoursComboBox = new JComboBox<>(hoursModel);
//            panel.add(hoursComboBox);
//            hoursComboBox.setSelectedItem(hours);
//
//            // Thêm giá trị vào DefaultComboBoxModel
//            DefaultComboBoxModel<Integer> minutesModel = new DefaultComboBoxModel<>();
//            for (int i = -1; i < 60; i++) {
//                minutesModel.addElement(i);
//            }
//            JComboBox<Integer> minutesComboBox = new JComboBox<>(minutesModel);
//            panel.add(minutesComboBox);
//            minutesComboBox.setSelectedItem(minutes);
//            JLabel jLabel2 = new JLabel("SETTING_AUTO_MAINTENACE");
//            panel.add(jLabel2);
//            JButton scheduleButton2 = new JButton("Hẹn Giờ Bảo Trì");
//            scheduleButton2.addActionListener(e -> scheduleMaintenance(hoursComboBox, minutesComboBox));
//            panel.add(scheduleButton2);
//            if (hours != -1 && minutes != -1) {
//                scheduleMaintenance(hoursComboBox, minutesComboBox);
//            }
//        } catch (IOException e) {
//        }
//        
//        // Tạo DefaultComboBoxModel với các sự kiện
//        DefaultComboBoxModel<String> eventModel = new DefaultComboBoxModel<>();
//        eventModel.addElement("Chọn Sự Kiện");
//        eventModel.addElement("Sự Kiện Tết Nguyên Đán");
//        eventModel.addElement("Sự Kiện Noel Giáng Sinh");
//        eventModel.addElement("Sự Kiện Halloween");
//        JComboBox<String> eventComboBox = new JComboBox<>(eventModel);
//        eventComboBox.addActionListener((ActionEvent e) -> {
//        String selectedEvent = (String) eventComboBox.getSelectedItem();
//            handleEventSelection(selectedEvent);  // Gọi hàm xử lý khi chọn sự kiện
//        });
//        panel.add(eventComboBox);
//        
//        EventCountLabel = new JLabel("\nSự Kiện Hiện Tại : ");
//        panel.add(EventCountLabel);
//        
//        // Tạo DefaultComboBoxModel với các sự kiện
//        DefaultComboBoxModel<String> datatype = new DefaultComboBoxModel<>();
//        datatype.addElement("Chọn Kiểu Dữ Liệu");
//        datatype.addElement("int");
//        datatype.addElement("long");
//        datatype.addElement("double");
//        JComboBox<String> datatypeComboBox = new JComboBox<>(datatype);
//        datatypeComboBox.addActionListener((ActionEvent e) -> {
//        String selecteddatatype = (String) datatypeComboBox.getSelectedItem();
//            handleDataTypeSelection(selecteddatatype);  // Gọi hàm xử lý khi chọn sự kiện
//        });
//        panel.add(datatypeComboBox);
//        
//        TypeDataCountLabel = new JLabel("\nKiểu Dữ Liệu Hiện Tại : ");
//        panel.add(TypeDataCountLabel);
//        
//        JButton Login = new JButton("LOGIN_PLAYER");
//        Login.addActionListener((ActionEvent e) -> {
//            int size = 0;
//            MySession session;
//            try {
//                session = new MySession(new Socket("127.0.0.1", 14445));
//                session.version = 240;
//                session.login("1", "1");
//                if (session.isConnected() && session.player != null) {
//                    Manager.player = session.player;
//                    Manager.player.isBotLogin = true;
//                    size ++;
//                }
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(ServerManagerUI.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            try {
//                session = new MySession(new Socket("127.0.0.1", 14445));
//                session.version = 240;
//                session.login("3", "1");
//                if (session.isConnected() && session.player != null) {
//                    Manager.player = session.player;
//                    Manager.player.isBotLogin = true;
//                    size ++;
//                }
//            } catch (IOException ex) {
//                java.util.logging.Logger.getLogger(ServerManagerUI.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            JOptionPane.showMessageDialog(null, "Đã Login Thành Công " + size + " Người Chơi.");
//        });
//        panel.add(Login);
//        
//        messageLabel = new JLabel();
//        panel.add(messageLabel);
//
//        countdownLabel = new JLabel();
//        panel.add(countdownLabel);
//        
//        panel.add(info);
//        threadCountLabel = new JLabel("Số Thread : ");
//        panel.add(threadCountLabel);
//        
//        plCountLabel = new JLabel("Số Người Online : ");
//        panel.add(plCountLabel);
//        SessionCountLabel = new JLabel("Session : ");
//        panel.add(SessionCountLabel);
//        
//        botCountLabel = new JLabel("\nTổng Số Bot Hoạt Động : ");
//        panel.add(botCountLabel);
//        botCountLabel_new = new JLabel("\nTổng Số Bot_New Hoạt Động : ");
//        panel.add(botCountLabel_new);
//        
//        ScheduledExecutorService EventCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        EventCountExecutor.scheduleAtFixedRate(() -> {
//            EventCountLabel.setText("Kiểu Dữ Liệu Hiện Tại : " + (EventManager.LUNNAR_NEW_YEAR ? "Tết Nguyên Đán" : "Không Có Sự Kiện"));
//        }, 1, 1, TimeUnit.SECONDS);
//        
//        ScheduledExecutorService typedataCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        typedataCountExecutor.scheduleAtFixedRate(() -> {
//            boolean typedata = Manager.readInt;
//            TypeDataCountLabel.setText("Kiểu Dữ Liệu Hiện Tại : " + (typedata ? "int" : "long"));
//        }, 1, 1, TimeUnit.SECONDS);
//
//        ScheduledExecutorService threadCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        threadCountExecutor.scheduleAtFixedRate(() -> {
//            int threadCount = Thread.activeCount();
//            threadCountLabel.setText("Số Thread : " + threadCount);
//        }, 1, 1, TimeUnit.SECONDS);
//
//        ScheduledExecutorService plCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        plCountExecutor.scheduleAtFixedRate(() -> {
//            int plcount = Client.gI().getPlayers().size();
//            plCountLabel.setText("Số Người Online : " + plcount);
//        }, 5, 1, TimeUnit.SECONDS);
//        ScheduledExecutorService ssCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        ssCountExecutor.scheduleAtFixedRate(() -> {
//            int sscount = SessionManager.gI().getSessions().size();
//            SessionCountLabel.setText("Session : " + sscount);
//        }, 5, 1, TimeUnit.SECONDS);
//        
//        ScheduledExecutorService botCountExecutor = Executors.newSingleThreadScheduledExecutor();
//        botCountExecutor.scheduleAtFixedRate(() -> {
//            int botcount = BotManager.gI().getBot().size();
//            botCountLabel.setText("\nTổng Số Bot Hoạt Động : " + botcount);
//        }, 5, 1, TimeUnit.SECONDS);
//        ScheduledExecutorService botCountExecutor_new = Executors.newSingleThreadScheduledExecutor();
//        botCountExecutor_new.scheduleAtFixedRate(() -> {
//            int botcount = BotManager_new.gI().getBot().size();
//            botCountLabel_new.setText("\nTổng Số Bot_New Hoạt Động : " + botcount);
//        }, 5, 1, TimeUnit.SECONDS);
//        
//        messageLabel.setText("Server đang chạy tại port : " + ServerManager.PORT);
//        
//        setVisible(true);
//        ServerManager.gI().run();
//        
//        // Đọc giá trị từ tệp
//        MaiTienDungManager.getInstance().startAutoSave();
//    }
//    
//    private void handleDataTypeSelection(String selecteddata) {
//        // Thực hiện hành động tùy theo sự kiện đã chọn
//        switch (selecteddata) {
//            case "Chọn Kiểu Dữ Liệu":
//                break;
//            case "int":
//                Manager.readInt = true;
//                JOptionPane.showMessageDialog(this, "Thành công!");
//                break;
//            case "long":
//                Manager.readInt = false;
//                JOptionPane.showMessageDialog(this, "Thành công!");
//                break;
//            case "double":
//                JOptionPane.showMessageDialog(this, "Thành công!");                
//                break;
//            default:
//                JOptionPane.showMessageDialog(this, "Lựa chọn không hợp lệ");
//                break;
//        }
//    }
//    
//    private void handleEventSelection(String selectedEvent) {
//        // Thực hiện hành động tùy theo sự kiện đã chọn
//        switch (selectedEvent) {
//            case "Chọn Sự Kiện":
//                EventManager.CHRISTMAS = false;
//                EventManager.HALLOWEEN = false;
//                EventManager.HUNG_VUONG = false;
//                EventManager.INTERNATIONAL_WOMANS_DAY = false;
//                EventManager.LUNNAR_NEW_YEAR = false;
//                EventManager.TRUNG_THU = false;
//                EventManager.gI().init();
//                break;
//            case "Sự Kiện Tết Nguyên Đán":
//                EventManager.LUNNAR_NEW_YEAR = true;
//                EventManager.CHRISTMAS = false;
//                EventManager.HALLOWEEN = false;
//                EventManager.HUNG_VUONG = false;
//                EventManager.INTERNATIONAL_WOMANS_DAY = false;
//                EventManager.TRUNG_THU = false;
//                EventManager.gI().init();
//                JOptionPane.showMessageDialog(this, "Bạn đã chọn Sự kiện Tết Nguyên Đán");
//                Logger.success("Máy chủ đang diễn ra sự kiện Tết Nguyên Đán");
//                break;
//            case "Sự Kiện Noel Giáng Sinh":
//                EventManager.HALLOWEEN = false;
//                EventManager.HUNG_VUONG = false;
//                EventManager.INTERNATIONAL_WOMANS_DAY = false;
//                EventManager.LUNNAR_NEW_YEAR = false;
//                EventManager.TRUNG_THU = false;
//                EventManager.CHRISTMAS = true;
//                JOptionPane.showMessageDialog(this, "Bạn đã chọn Sự kiện Noel Giáng Sinh");
//                Logger.success("Máy chủ đang diễn ra sự kiện Noel Giáng Sinh");
//                
//                break;
//            case "Sự Kiện Halloween":
//                EventManager.CHRISTMAS = false;
//                EventManager.HUNG_VUONG = false;
//                EventManager.INTERNATIONAL_WOMANS_DAY = false;
//                EventManager.LUNNAR_NEW_YEAR = false;
//                EventManager.TRUNG_THU = false;
//                EventManager.HALLOWEEN = true;
//                JOptionPane.showMessageDialog(this, "Bạn đã chọn Sự kiện Halloween");
//                Logger.success("Máy chủ đang diễn ra sự kiện Halloween");
//                break;
//            default:
//                JOptionPane.showMessageDialog(this, "Lựa chọn không hợp lệ");
//                break;
//        }
//    }
//
//    private void showMaintenanceDialog() {
//        try {
//            int dialogButton = JOptionPane.YES_NO_OPTION;
//            int dialogResult = JOptionPane.showConfirmDialog(this, "Bắt đầu bảo trì?", "Bảo trì", dialogButton);
//            if (dialogResult == 0) {
//                Logger.error("Server tiến hành bảo trì");
//                Maintenance.gI().start(5);
//
//            } else {
//                System.out.println("No Option");
//            }
//        } catch (HeadlessException e) {
//        }
//    }
//
//    private void kick() {
//        new Thread(() -> {
//            Client.gI().close();
//        }).start();
//
//    }
//
//    private void tnsm() {
//        String exp = JOptionPane.showInputDialog(this, "Bảng Exp Server\n"
//                + "Exp Server hiện tại: " + Manager.RATE_EXP_SERVER + "\nExp Server Tối Thiểu Là 100");
//        if (exp != null) {
//            Manager.RATE_EXP_SERVER = Byte.parseByte(exp);
//            Logger.error("Exp hiện tại là: " + exp + "\n");
//        }
//
//    }
//    public static void startAntiDDoS() {
//        try {
//            Runtime rt = Runtime.getRuntime();
//            String command = "cmd /c start run_chongddosvv.bat";
//            rt.exec(command);
//            Logger.success("Đã bật chống DDOS");
//        } catch (IOException ex) {
//            Logger.error("Không thể bật chống DDOS");
//        }
//    }
//
//    private void confirmExit() {
//        int dialogButton = JOptionPane.YES_NO_OPTION;
//        int dialogResult = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn thoát chương trình?", "Thoát", dialogButton);
//        if (dialogResult == 0) {
//            System.exit(0);
//        }
//    }
//
//    @Override
//    public void setDefaultCloseOperation(int operation) {
//        if (operation == JFrame.EXIT_ON_CLOSE) {
//            addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    confirmExit();
//                }
//            });
//        } else {
//            super.setDefaultCloseOperation(operation);
//        }
//    }
//
//    private void scheduleMaintenance(JComboBox<Integer> hoursComboBox, JComboBox<Integer> minutesComboBox) {
//        int hours = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
//        int minutes = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
//        if (minutes == -1 || hours == -1) {
//            JOptionPane.showMessageDialog(this, "Thời gian sai");
//            return;
//        }
//        // Ghi giá trị vào tệp tin
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("maintenanceConfig.txt"))) {
//            writer.write(hours + "\n");
//            writer.write(minutes + "\n");
//            writer.flush();
//        } catch (IOException e) {
//        }
//
//        AtomicBoolean timeReached = new AtomicBoolean(false); // Sử dụng AtomicBoolean để đảm bảo tính nhất quán trong thread
//        info.setText("Thời Gian Bảo Trì Tự Động : " + hours + ":" + minutes);
//        new Thread(() -> {
//            while (!timeReached.get()) { // Kiểm tra điều kiện dừng
//                try {
//                    LocalTime currentTime = LocalTime.now();
//                    int hourss = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
//                    int minutess = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
//                    int hour_now = currentTime.getHour();
//                    int minute_now = currentTime.getMinute();
//
//                    if (hourss == hour_now && minutess == minute_now) {
//                        performMaintenance();
//                        timeReached.set(true); // Gán giá trị true để dừng vòng lặp
//                    }
//                    Functions.sleep(10000);
//                } catch (Exception e) {
//                }
//            }
//        }).start();
//    }
//
//    private void performMaintenance() {
//        Maintenance.gI().start(15);
//
//    }
//
//    public static void runBatchFile(String batchFilePath) throws IOException {
//        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", batchFilePath);
//        Process process = processBuilder.start();
//        try {
//            process.waitFor();
//        } catch (InterruptedException e) {
//        }
//    }
//}
public class ServerManagerUI extends JFrame {

    private static class NavItem {
        String name;
        Icon icon;
        String key;

        public NavItem(String name, String iconPath, String key) {
            this.name = name;
            this.key = key;
            this.icon = ServerGuiUtils.loadIcon(iconPath);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class MathSignaturePanel extends JPanel {

        public MathSignaturePanel() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(240, 70));
            setBorder(new EmptyBorder(10, 0, 10, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            int[] encryptedDev = {351, 516, 601, 516, 551, 566, 571, 516, 511, 171, 501, 616};
            String text1 = decodeSecret(encryptedDev);
            int wPrefix = g2.getFontMetrics().stringWidth(text1);
            g2.drawString(text1, (getWidth() - wPrefix) / 2, 25);
            drawVectorSignature(g2);
            g2.setColor(new Color(180, 180, 180));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
            int[] encryptedCopy = {856, 171, 261, 251, 261, 281, 171, 396, 496, 561, 496, 526, 516, 581};
            String text2 = decodeSecret(encryptedCopy);
            int wCopy = g2.getFontMetrics().stringWidth(text2);
            g2.drawString(text2, (getWidth() - wCopy) / 2, 60);
        }

        private String decodeSecret(int[] data) {
            StringBuilder sb = new StringBuilder();
            for (int value : data) {
                char c = (char) ((value - 11) / 5);
                sb.append(c);
            }
            return sb.toString();
        }
        private void drawVectorSignature(Graphics2D g2) {
    AffineTransform original = g2.getTransform();

    g2.translate((getWidth() / 2) - 38, 45);

    g2.setColor(new Color(0, 120, 215));
    g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

    Path2D p = new Path2D.Float();

    // ===== a =====
    p.moveTo(0, -4);
    p.curveTo(-2, -8, 6, -8, 6, -2);
    p.curveTo(6, 2, 0, 2, 0, -1);
    p.lineTo(6, -1);

    // ===== n =====
    float n = 10;
    p.moveTo(n, 0);
    p.lineTo(n, -5);
    p.curveTo(n, -8, n + 6, -8, n + 6, -3);
    p.lineTo(n + 6, 0);

    // ===== w =====
    float w = 22;
    p.moveTo(w, -5);
    p.lineTo(w + 2, 0);
    p.lineTo(w + 4, -3);
    p.lineTo(w + 6, 0);
    p.lineTo(w + 8, -5);

    // ===== i =====
    float i = 34;
    p.moveTo(i, -5);
    p.lineTo(i, 0);
    p.moveTo(i, -8);
    p.lineTo(i, -8.1);

    // ===== n =====
    float n2 = 42;
    p.moveTo(n2, 0);
    p.lineTo(n2, -5);
    p.curveTo(n2, -8, n2 + 6, -8, n2 + 6, -3);
    p.lineTo(n2 + 6, 0);

    g2.draw(p);
    g2.setTransform(original);
}

        
    }

    private final Instant serverStartTime;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JList<NavItem> sidebar;

    public ServerManagerUI() {
        super("Server Control Panel - NRO Manager");
        setIconImage(createAppIconImage());
        ServerGuiUtils.setupTheme();
        initUI();
        startServerProcesses();
        this.serverStartTime = Instant.now();
    }

    private Image createAppIconImage() {
        int size = 64;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(new Color(0, 120, 215));
        g2.fill(new RoundRectangle2D.Double(4, 4, 56, 56, 16, 16));
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(10, 10, 44, 44, 8, 8));
        g2.setColor(new Color(52, 152, 219, 50));
        Path2D area = new Path2D.Double();
        area.moveTo(15, 45);
        area.lineTo(22, 38);
        area.lineTo(29, 42);
        area.lineTo(38, 28);
        area.lineTo(38, 45);
        area.closePath();
        g2.fill(area);
        g2.setColor(new Color(41, 128, 185));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D line = new Path2D.Double();
        line.moveTo(15, 45);
        line.lineTo(22, 38);
        line.lineTo(29, 42);
        line.lineTo(38, 28);
        g2.draw(line);
        g2.setColor(new Color(46, 204, 113));
        g2.fill(new Ellipse2D.Double(43, 16, 6, 6));
        g2.setColor(new Color(243, 156, 18));
        g2.fill(new Ellipse2D.Double(43, 26, 6, 6));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(189, 195, 199));
        g2.drawLine(40, 38, 50, 38);
        g2.setColor(new Color(52, 73, 94));
        g2.fill(new Ellipse2D.Double(42, 36, 4, 4));
        g2.setColor(new Color(189, 195, 199));
        g2.drawLine(40, 45, 50, 45);
        g2.setColor(new Color(52, 73, 94));
        g2.fill(new Ellipse2D.Double(47, 43, 4, 4));
        g2.dispose();
        return image;
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        NavItem[] menuItems = {
            new NavItem("Bảng Điều Khiển", "/icon/dashboard.png", "Dashboard"),
            new NavItem("Quản Lý Tài Khoản", "/icon/account.png", "Account"),
            new NavItem("Danh Sách Người Chơi", "/icon/players.png", "Players"),
            new NavItem("Cửa Hàng (Shop)", "/icon/shop.png", "ShopEditor"),
            new NavItem("Quản Lý Giftcode", "/icon/giftcode.png", "Giftcode"),
//            new NavItem("Nạp Thẻ & Thưởng", "/icon/topup.png", "TopupReward"),
            new NavItem("Sự Kiện (Events)", "/icon/events.png", "Events"),
            new NavItem("Danh Hiệu (Badges)", "/icon/badges.png", "data_badges"),
            new NavItem("Dữ Liệu Bản Đồ", "/icon/map_data.png", "map_template"),
            new NavItem("Dữ Liệu Vật Phẩm", "/icon/item_data.png", "item_template"),
            new NavItem("Lịch sử giao dịch", "/icon/transaction.png", "LichSuGd"),
            new NavItem("Quản Lý Radar", "/icon/radar.png", "radar"),
            new NavItem("Quản Lý Part", "/icon/part.png", "part"),
            new NavItem("Quản Lý Drop Item", "/icon/item_data.png", "drop_item"),
            new NavItem("Cấu Hình Boss", "/icon/boss_config.png", "Boss Config"),
            new NavItem("Bảo Mật & Firewall", "/icon/security.png", "Security"),
            new NavItem("AntiDDos", "/icon/security.png", "AntiDDoS"),

        };

        sidebar = new JList<>(menuItems);
        sidebar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sidebar.setSelectedIndex(0);
        sidebar.setFixedCellHeight(50);
        sidebar.setBackground(new Color(255, 255, 255));
        sidebar.setBorder(new EmptyBorder(10, 0, 10, 0));

        sidebar.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NavItem) {
                    NavItem item = (NavItem) value;
                    lbl.setText(item.name);
                    if (item.icon != null) {
                        lbl.setIcon(item.icon);
                    }
                }
                lbl.setBorder(new EmptyBorder(0, 15, 0, 0));
                lbl.setIconTextGap(12);
                lbl.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 13));
                if (isSelected) {
                    lbl.setBackground(new Color(230, 242, 255));
                    lbl.setForeground(new Color(0, 102, 204));
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0, 120, 215)),
                            new EmptyBorder(0, 11, 0, 0)
                    ));
                } else {
                    lbl.setBackground(Color.WHITE);
                    lbl.setForeground(new Color(60, 60, 60));
                }
                return lbl;
            }
        });

        JPanel sidebarContainer = new JPanel(new BorderLayout());
        sidebarContainer.setPreferredSize(new Dimension(240, getHeight()));
        sidebarContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        JScrollPane scrollSidebar = new JScrollPane(sidebar);
        scrollSidebar.setBorder(null);
        sidebarContainer.add(scrollSidebar, BorderLayout.CENTER);

        sidebarContainer.add(new MathSignaturePanel(), BorderLayout.SOUTH);

        add(sidebarContainer, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new AccountPanel(), "Account");
        contentPanel.add(new PlayersPanel(), "Players");
        contentPanel.add(new ShopEditorPanel(), "ShopEditor");
        contentPanel.add(new GiftcodePanel(), "Giftcode");
        contentPanel.add(new TopupRewardPanel(), "TopupReward");
        contentPanel.add(new EventPanel(), "Events");
        contentPanel.add(new BadgesPanel(), "data_badges");
        contentPanel.add(new MapTemplatePanel(), "map_template");
        contentPanel.add(new ItemTemplatePanel(), "item_template");
        contentPanel.add(new LichSuGdPanel(), "LichSuGd");
        contentPanel.add(new RadarPanel(), "radar");
        contentPanel.add(new PartPanel(), "part");
        contentPanel.add(new DropItemPanel(), "drop_item");
        contentPanel.add(new BossEditorPanel(), "Boss Config");
        contentPanel.add(new SecurityPanel(), "Security");
        contentPanel.add(new AntiDDoSPanelV2(), "AntiDDoS");


        add(contentPanel, BorderLayout.CENTER);

        sidebar.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                NavItem selected = sidebar.getSelectedValue();
                if (selected != null) {
                    cardLayout.show(contentPanel, selected.key);
                }
            }
        });

        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        ServerManagerUI.this,
                        "Bạn có chắc muốn dừng Server và thoát chương trình?",
                        "Xác nhận tắt Server",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    shutdownServer();
                }
            }
        });
    }

   private void startServerProcesses() {
    Logger.connect("Starting Server Engine...");

    new Thread(() -> {
        try {
            Logger.system("SERVER_UI", "Đang gọi ServerManager.gI().run()");
            ServerManager.gI().run();

            EventQueue.invokeLater(() -> setVisible(true));
        } catch (Exception e) {
            Logger.logException(ServerManagerUI.class, e, "Lỗi startServerProcesses");
        }
    }, "Thread Start Server Engine").start();
}

    private void shutdownServer() {
        try {
            System.out.println(">> Đang lưu dữ liệu và đóng kết nối...");
            if (ProxyManager.getInstance() != null) {
                ProxyManager.getInstance().stopAll();
            }
            if (AnwinManager.getInstance() != null) {
                AnwinManager.getInstance().stopAutoSave();
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng tài nguyên: " + e.getMessage());
        }
        System.out.println(">> Server shutting down... Bye!");
        System.exit(0);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(ServerManagerUI::new);
    }
}
