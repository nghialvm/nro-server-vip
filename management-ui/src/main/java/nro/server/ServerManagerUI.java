package nro.server;

import Utils.Logger;
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
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.time.Instant;
import javax.swing.border.EmptyBorder;
import manager.NroManager;
import nro.server.Proxy.ProxyManager;


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
            if (NroManager.getInstance() != null) {
                NroManager.getInstance().stopAutoSave();
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
