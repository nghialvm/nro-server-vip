package nro.server;

import Data.DataGame;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import jbcd.ConnectDB;


public class AccountPanel extends JPanel {


    // Fonts & Colors gọn gàng hơn
    private static final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    
    private static final Color COL_PRIMARY = new Color(0, 120, 215);
    private static final Color COL_BG = new Color(250, 250, 250);
    private static final Color COL_BORDER = new Color(200, 200, 200);

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy");

    private final Map<Integer, Integer> partHeadIconMap = new HashMap<>();
    private final Map<Integer, ImageIcon> headCache = new HashMap<>();
    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    public AccountPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COL_BG);
        initUI();
        loadHeadPartCache();
    }

    private void initUI() {
        // --- TOP BAR ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, COL_BORDER), 
            new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblTitle = new JLabel("QUẢN LÝ TÀI KHOẢN");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COL_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm ID, User, Tên NV...");
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setFont(FONT_UI);

        JButton btnSearch = createButton("Tìm", COL_PRIMARY);
        JButton btnReload = createButton("Tải lại", new Color(40, 167, 69));

        btnSearch.addActionListener(e -> searchData(txtSearch.getText()));
        btnReload.addActionListener(e -> { txtSearch.setText(""); loadData(); });

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReload);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
     String[] columns = {"Head","ID","Tài khoản","Tên NV","Mật khẩu","Trạng thái","VIP","VND","Tổng nạp","Ngày tạo"};
        
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? ImageIcon.class : Object.class; }
        };

        table = new JTable(model);
        setupTableStyle();

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int id = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 1).toString());
                    openEditDialog(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private void setupTableStyle() {
        table.setFont(FONT_UI);
        table.setRowHeight(35); // Thu nhỏ chiều cao dòng cho gọn
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(232, 242, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 35));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply center render to specific columns
        for(int i : new int[]{1, 5, 6, 7, 8, 9}) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        // Custom renders for colors
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() { // Name
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(CENTER); setFont(FONT_BOLD); setForeground(new Color(0, 102, 204));
                if(s) setBackground(t.getSelectionBackground()); else setBackground(Color.WHITE);
                return this;
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() { // Status
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(CENTER); setFont(FONT_BOLD);
                String txt = v.toString();
                if (txt.contains("BAN")) setForeground(Color.RED);
                else if (txt.contains("Active")) setForeground(new Color(0, 150, 0));
                else setForeground(Color.GRAY);
                if(s) setBackground(t.getSelectionBackground()); else setBackground(Color.WHITE);
                return this;
            }
        });

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40); // Head
        cm.getColumn(1).setPreferredWidth(40); // ID
        cm.getColumn(2).setPreferredWidth(100);
        cm.getColumn(3).setPreferredWidth(100);
        cm.getColumn(4).setPreferredWidth(60); // Pass
    }

    // --- EDIT DIALOG ---
    private void openEditDialog(int accountId) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa ID: " + accountId, true);
        d.setSize(750, 550); // Resize nhỏ gọn
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());

        // Init Components
        JTextField txtUser = createField();
        JTextField txtPass = createField();
        JTextField txtEmail = createField();
        
        JCheckBox chkActive = new JCheckBox("Active");
        JCheckBox chkBan = new JCheckBox("BAN"); chkBan.setForeground(Color.RED);
        JCheckBox chkAdmin = new JCheckBox("Admin");
        
        JTextField txtBan = createField();
        JTextField txtVip = createField();
        JTextField txtServer = createField();

        JTextField txtVnd = createField(); txtVnd.setForeground(Color.RED);
        JTextField txtDanap = createField(); txtDanap.setForeground(Color.BLUE);
        JTextField txtGold = createField(); 
        JTextField txtPoint = createField();

        Map<String, JTextField> eventMap = new HashMap<>();
        String[] eventCols = {
            "DiemDanh", "diemboss", "bong_master", "hopquathang9", "hopquathang9vip",
            "hopquatrungthuvip", "longdentreo", "hoptrahoacuc", "hopkeomaquy",
            "capsuvip", "thiepchucvip", "halloween_master", "keo_halloween",
            "diemnoel", "vongquayvang", "phaobong", "lixi", "luotquay", "event_point"
        };
        for (String col : eventCols) eventMap.put(col, createField());

        final int[] headInfo = {-1};
        final int[] playerIdRef = {-1};
        final String[] invRef = {null};

        loadAccountData(accountId, txtUser, txtPass, txtEmail, chkActive, chkBan, chkAdmin,
        txtBan, txtVip, txtServer, txtVnd, txtDanap, txtGold, txtPoint,
        eventMap, headInfo, playerIdRef, invRef);


        // --- LAYOUT ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(COL_BG);

        // 1. Info & Status Area (Top Split)
        JPanel topSplit = new JPanel(new GridLayout(1, 2, 20, 0));
        topSplit.setOpaque(false);

        // Left: Login Info
        JPanel pLeft = createSection("Thông tin đăng nhập");
        pLeft.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(3, 5, 3, 5); gbc.weightx = 1;
        
        JLabel lblAvt = new JLabel(getAvatar(headInfo[0], txtUser.getText(), 60));
        gbc.gridx=0; gbc.gridy=0; gbc.gridheight=3; gbc.weightx=0; pLeft.add(lblAvt, gbc);
        
        gbc.gridx=1; gbc.gridheight=1; gbc.weightx=1;
        pLeft.add(createInputRow("Tài khoản:", txtUser), gbc);
        gbc.gridy=1; pLeft.add(createInputRow("Mật khẩu:", txtPass), gbc);
        gbc.gridy=2; pLeft.add(createInputRow("Email:", txtEmail), gbc);

        // Right: Status & Assets
        JPanel pRight = createSection("Trạng thái & Tài sản");
        pRight.setLayout(new GridLayout(4, 2, 10, 5));
        
        JPanel pChk = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); pChk.setOpaque(false);
        pChk.add(chkActive); pChk.add(chkBan); pChk.add(chkAdmin);
        
        pRight.add(wrapLabel("Trạng thái:", pChk));
        pRight.add(wrapLabel("Server:", txtServer));
        pRight.add(wrapLabel("Ban:", txtBan));
        pRight.add(wrapLabel("Điểm VIP:", txtVip));
        pRight.add(wrapLabel("VND:", txtVnd));
        pRight.add(wrapLabel("Tổng nạp:", txtDanap));
        pRight.add(wrapLabel("Thỏi Vàng:", txtGold));
        pRight.add(wrapLabel("VIP:", txtPoint));

        topSplit.add(pLeft);
        topSplit.add(pRight);
        mainPanel.add(topSplit, BorderLayout.NORTH);

        // 2. Events Area (Center)
        JPanel pEvents = createSection("Vật phẩm & Sự kiện");
        pEvents.setLayout(new BorderLayout());
        
        JPanel pGridEvent = new JPanel(new GridLayout(0, 4, 10, 8)); // 4 cột
        pGridEvent.setBackground(Color.WHITE);
        pGridEvent.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        for(String key : eventCols) {
            JTextField tf = eventMap.get(key);
            tf.setHorizontalAlignment(JTextField.CENTER);
            pGridEvent.add(wrapLabelMini(key, tf));
        }

        JScrollPane scrollEvents = new JScrollPane(pGridEvent);
        scrollEvents.setBorder(null);
        scrollEvents.getVerticalScrollBar().setUnitIncrement(16);
        pEvents.add(scrollEvents, BorderLayout.CENTER);
        
        mainPanel.add(pEvents, BorderLayout.CENTER);

        // 3. Button Area
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pBtn.setBackground(new Color(240, 240, 240));
        pBtn.setBorder(new MatteBorder(1,0,0,0, COL_BORDER));
        
        JButton btnSave = createButton("LƯU THAY ĐỔI", COL_PRIMARY);
        btnSave.setPreferredSize(new Dimension(140, 35));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> d.dispose());
        
        btnSave.addActionListener(e -> saveAccount(d, accountId, txtUser, txtPass, txtEmail, chkActive, chkBan, chkAdmin,
        txtBan, txtVip, txtServer, txtVnd, txtDanap, txtGold, txtPoint,
        eventMap, playerIdRef, invRef));

        pBtn.add(btnClose);
        pBtn.add(btnSave);

        d.add(mainPanel, BorderLayout.CENTER);
        d.add(pBtn, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- HELPER UI METHODS ---
    private JPanel createSection(String title) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(COL_BORDER), title, TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, FONT_BOLD, COL_PRIMARY));
        return p;
    }

    private JPanel createInputRow(String label, JTextField txt) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(label); l.setPreferredSize(new Dimension(60, 25));
        l.setFont(FONT_UI); l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapLabel(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false);
        JLabel l = new JLabel(title); 
        l.setFont(FONT_UI);
        l.setPreferredSize(new Dimension(65, 20));
        p.add(l, BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JPanel wrapLabelMini(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(title); 
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(Color.GRAY);
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField createField() {
        JTextField t = new JTextField();
        t.setFont(new Font("Consolas", Font.PLAIN, 13));
        t.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220)), new EmptyBorder(2, 5, 2, 5)));
        return t;
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(5, 15, 5, 15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // --- DATA LOGIC (Giữ nguyên logic cũ) ---
   private void loadAccountData(int id, JTextField user, JTextField pass, JTextField email,
                             JCheckBox act, JCheckBox ban, JCheckBox adm,
                             JTextField banField, JTextField vip, JTextField server,
                             JTextField vnd, JTextField tongnap, JTextField gold, JTextField point,
                             Map<String, JTextField> events, int[] headRef,
                             int[] playerIdRef, String[] invRef) {
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT a.*, p.id AS player_id, p.head AS p_head, p.data_inventory AS data_inventory " +
                 "FROM account a " +
                 "LEFT JOIN player p ON p.account_id = a.id " +
                 "WHERE a.id = ? LIMIT 1"
               )) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                headRef[0] = rs.getInt("p_head");
                user.setText(rs.getString("username"));
                pass.setText(rs.getString("password"));
                email.setText(rs.getString("email"));
                act.setSelected(rs.getInt("active") == 1);
                ban.setSelected(rs.getInt("ban") == 1);
                adm.setSelected(rs.getInt("is_admin") == 1);
                banField.setText(String.valueOf(rs.getInt("ban")));

                // Lấy player_id + data_inventory để còn lưu VIP
                playerIdRef[0] = rs.getInt("player_id");
                invRef[0] = rs.getString("data_inventory");

                vip.setText(String.valueOf(parseVipFromInventory(invRef[0])));

                server.setText(rs.getString("server_login"));

                
                vnd.setText(String.valueOf(rs.getInt("vnd")));
                tongnap.setText(String.valueOf(rs.getInt("tongnap")));
                gold.setText(String.valueOf(rs.getLong("thoi_vang")));
                point.setText(String.valueOf(rs.getInt("Vip_Point")));

                for (String key : events.keySet()) {
                    try { events.get(key).setText(String.valueOf(rs.getInt(key))); } catch (Exception ex) {}
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveAccount(JDialog d, int id, JTextField user, JTextField pass, JTextField email, JCheckBox act, JCheckBox ban, JCheckBox adm, JTextField banField, JTextField vip, JTextField server, JTextField vnd, JTextField tongnap, JTextField gold, JTextField point, Map<String, JTextField> events, int[] playerIdRef, String[] invRef) {
        if (JOptionPane.showConfirmDialog(d, "Xác nhận lưu dữ liệu?", "Lưu", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        
        new Thread(() -> {
    try (Connection conn = ConnectDB.getConnection()) {

        // 1) Build SQL sau khi đã có conn (để lọc cột tồn tại)
        StringBuilder sql = new StringBuilder(
            "UPDATE account SET username=?, password=?, email=?, active=?, ban=?, is_admin=?, server_login=?, "
        );
        sql.append("vnd=?, tongnap=?, thoi_vang=?, Vip_Point=?, ");

        // chỉ add event column nào tồn tại trong bảng account
        for (String k : events.keySet()) {
            if (hasColumn(conn, "account", k)) {
                sql.append(k).append("=?, ");
            }
        }
        sql.append("update_time=NOW() WHERE id=?");

        // 2) Prepare
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int i = 1;

            ps.setString(i++, user.getText());
            ps.setString(i++, pass.getText());
            ps.setString(i++, email.getText()); // nếu email là JSON thì xử lý như bạn muốn
            ps.setInt(i++, act.isSelected() ? 1 : 0);
            ps.setInt(i++, adm.isSelected() ? 1 : 0);
            ps.setInt(i++, safeInt(banField));
            ps.setInt(i++, safeInt(server));

            ps.setInt(i++, safeInt(vnd));
            ps.setInt(i++, safeInt(tongnap));
            ps.setInt(i++, safeInt(gold));     // thoi_vang
            ps.setInt(i++, safeInt(point));

            // set event param theo đúng các cột đã add vào SQL
            for (String k : events.keySet()) {
                if (hasColumn(conn, "account", k)) {
                    ps.setInt(i++, safeInt(events.get(k)));
                }
            }

            ps.setInt(i++, id);

            ps.executeUpdate();
        }

        // 3) Update VIP vào player.data_inventory (giữ nguyên phần bạn đã làm)
        if (playerIdRef[0] > 0) {
            String newInv = setVipInInventory(invRef[0], safeInt(vip));
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE player SET data_inventory=? WHERE id=?")) {
                ps2.setString(1, newInv);
                ps2.setInt(2, playerIdRef[0]);
                ps2.executeUpdate();
            }
            invRef[0] = newInv;
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(d, "Lưu thành công!");
            d.dispose();
            loadData();
        });

    } catch (Exception e) {
        e.printStackTrace();
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(d, "Lỗi: " + e.getMessage())
        );
    }
}).start();

    }
    
    private int safeInt(JTextField t) { try { return Integer.parseInt(t.getText().trim()); } catch(Exception e) { return 0; } }
    private long safeLong(JTextField t) { try { return Long.parseLong(t.getText().trim()); } catch(Exception e) { return 0; } }

    private String formatNum(long num) { return java.text.NumberFormat.getInstance().format(num); }

    // --- CACHE & IMAGE UTILS ---
    private void loadHeadPartCache() {
        new Thread(() -> {
            try (Connection conn = ConnectDB.getConnection(); Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, data FROM part WHERE type = 0")) {
                while (rs.next()) {
                    try {
                        JsonArray arr = new JsonParser().parse(rs.getString("data")).getAsJsonArray();
                        if (arr.size() > 0) partHeadIconMap.put(rs.getInt("id"), arr.get(0).getAsJsonArray().get(0).getAsInt());
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) {}
            SwingUtilities.invokeLater(this::loadData);
        }).start();
    }

    private ImageIcon getAvatar(int headId, String text, int size) {
        if (headId > 0) {
            if (headCache.containsKey(headId) && size == 28) return headCache.get(headId);
            Integer iconId = partHeadIconMap.get(headId);
            if (iconId != null) {
                try {
                    String[] zooms = {"x4", "x3", "x2", "x1"};
                    for (String z : zooms) {
                        File f = DataGame.getIconFile(iconId);
                        if (f.exists()) {
                            Image img = ImageIO.read(f).getScaledInstance(size, size, Image.SCALE_SMOOTH);
                            ImageIcon icon = new ImageIcon(img);
                            if(size == 28) headCache.put(headId, icon);
                            return icon;
                        }
                    }
                } catch (Exception e) {}
            }
        }
        return AvatarGenerator.generate(text, size);
    }

  private void loadData() {
    updateTable(
        "SELECT a.id, a.username, a.password, a.active, a.ban, a.vnd, a.tongnap, a.create_time, " +
        "p.head AS head, p.name AS p_name, p.data_inventory AS data_inventory " +
        "FROM account a " +
        "LEFT JOIN player p ON p.account_id = a.id " +
        "ORDER BY a.id ASC"
    );
}



    private void searchData(String txt) {
    if (txt.isEmpty()) { loadData(); return; }
    updateTable(
        "SELECT a.id, a.username, a.password, a.active, a.ban, a.vnd, a.tongnap, a.create_time, " +
        "p.head AS head, p.name AS p_name, p.data_inventory AS data_inventory " +
        "FROM account a " +
        "LEFT JOIN player p ON p.account_id = a.id " +
        "WHERE a.username LIKE '%" + txt + "%' OR a.id='" + txt + "' OR p.name LIKE '%" + txt + "%' " +
        "ORDER BY a.id ASC"
    );
}



    private void updateTable(String sql) {
        model.setRowCount(0);
        new Thread(() -> {
            try (Connection conn = ConnectDB.getConnection(); Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    String u = rs.getString("username");
                    row.add(getAvatar(rs.getInt("head"), u, 28));
                    row.add(rs.getInt("id"));
                    row.add(u);
                    String pn = rs.getString("p_name");
                    row.add(pn == null ? "-" : pn);
                    row.add("******");
                    
                    int active = rs.getInt("active");
                    int ban = rs.getInt("ban");
                    row.add(ban == 1 ? "ĐÃ BAN" : (active == 1 ? "Active" : "Chưa KH"));
                    
                    String inv = rs.getString("data_inventory");
                    row.add(parseVipFromInventory(inv));
                    row.add(formatNum(rs.getInt("vnd")));
                    row.add(formatNum(rs.getInt("tongnap")));
                    Timestamp ts = rs.getTimestamp("create_time");
                    row.add(ts != null ? DATE_FMT.format(ts) : "-");
                    
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    static class AvatarGenerator {
        private static final Color[] COLORS = { new Color(26,188,156), new Color(46,204,113), new Color(52,152,219), new Color(155,89,182), new Color(230,126,34), new Color(231,76,60) };
        public static ImageIcon generate(String text, int size) {
            if (text == null || text.isEmpty()) text = "?";
            String l = text.substring(0, 1).toUpperCase();
            Color bg = COLORS[Math.abs(text.hashCode()) % COLORS.length];
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fill(new Ellipse2D.Float(0, 0, size, size));
            g2.setColor(Color.WHITE); g2.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(l, (size - fm.stringWidth(l)) / 2, (size - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
            return new ImageIcon(img);
        }
    }
    private int parseVipFromInventory(String inv) {
    if (inv == null) return 0;
    inv = inv.trim();
    int rb = inv.lastIndexOf(']');
    int comma = inv.lastIndexOf(',');
    if (comma == -1) return 0;

    String last = (rb > comma) ? inv.substring(comma + 1, rb) : inv.substring(comma + 1);
    last = last.trim();
    try { return Integer.parseInt(last); } catch (Exception e) { return 0; }
}

private String setVipInInventory(String inv, int newVip) {
    if (inv == null) return "[" + newVip + "]";
    inv = inv.trim();

    int comma = inv.lastIndexOf(',');
    int rb = inv.lastIndexOf(']');

    if (comma == -1) return inv; // format lạ thì thôi, tránh phá dữ liệu
    String prefix = inv.substring(0, comma + 1);
    String suffix = (rb >= 0) ? inv.substring(rb) : "]";
    return prefix + newVip + suffix;
}
private boolean hasColumn(Connection conn, String table, String col) {
    try (ResultSet rs = conn.getMetaData().getColumns(null, null, table, col)) {
        return rs.next();
    } catch (Exception e) {
        return false;
    }
}



}