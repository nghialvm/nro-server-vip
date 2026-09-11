package nro.server;

import Data.DataGame;
import jbcd.ConnectDB;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DropItemPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;

    private JTextField tfId;
    private JCheckBox cbActive;
    private JTextField tfMobId;
    private JTextField tfMapId;
    private JTextField tfItemId;
    private JTextField tfQuantity;
    private JTextField tfRateNum;
    private JTextField tfRateDen;
    private JComboBox<String> cbFamily;
    private JTextField tfNote;
    private JTextArea taOptions;
    private JTextArea taConditions;

    private JLabel lbMobName;
    private JLabel lbMapName;
    private JLabel lbItemName;
    private JLabel lbIcon;

    private final Map<Integer, String> mapNames = new HashMap<>();
    private final Map<Integer, String> mobNames = new HashMap<>();
    private final Map<Integer, ItemInfo> itemInfos = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();

    private final Color COL_PRIMARY = new Color(0, 120, 215);
    private final Color COL_SUCCESS = new Color(30, 160, 60);
    private final Color COL_DANGER = new Color(220, 53, 69);
    private final Color COL_WARNING = new Color(245, 160, 0);
    private final Color COL_HEADER = new Color(240, 242, 245);

    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private static class ItemInfo {
        String name;
        int iconId;

        ItemInfo(String name, int iconId) {
            this.name = name;
            this.iconId = iconId;
        }
    }

    public DropItemPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        createTableIfMissing();
        loadCacheData();
        initUI();
        loadDrops();
    }

    private Connection getConnection() throws Exception {
        return ConnectDB.getConnection();
    }

    private void createTableIfMissing() {
        String sql = "CREATE TABLE IF NOT EXISTS drop_item ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "active TINYINT(1) NOT NULL DEFAULT 1,"
                + "mob_id INT NOT NULL DEFAULT -1,"
                + "map_id INT NOT NULL DEFAULT -1,"
                + "item_id INT NOT NULL,"
                + "quantity INT NOT NULL DEFAULT 1,"
                + "rate_num INT NOT NULL DEFAULT 1,"
                + "rate_den INT NOT NULL DEFAULT 100,"
                + "family INT NOT NULL DEFAULT -1,"
                + "note VARCHAR(255) DEFAULT '',"
                + "options TEXT DEFAULT '',"
                + "conditions TEXT DEFAULT '',"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "INDEX idx_active(active),"
                + "INDEX idx_mob_map(mob_id, map_id),"
                + "INDEX idx_item(item_id)"
                + ")";

        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tạo bảng drop_item: " + e.getMessage());
        }
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ DROP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(COL_PRIMARY);

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm ID, item, map, mob, ghi chú...");
        txtSearch.setFont(FONT_UI);
        txtSearch.setPreferredSize(new Dimension(300, 36));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                loadDrops();
            }

            public void removeUpdate(DocumentEvent e) {
                loadDrops();
            }

            public void changedUpdate(DocumentEvent e) {
                loadDrops();
            }
        });

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRight.setOpaque(false);

        JButton btnReloadServer = createButton("Reload Server", COL_WARNING);
        btnReloadServer.addActionListener(e -> {
            DropManager.gI().reload();
            JOptionPane.showMessageDialog(this, "Đã reload DropManager trong server!");
        });

        JButton btnReload = createButton("Làm mới", COL_PRIMARY);
        btnReload.addActionListener(e -> {
            loadCacheData();
            loadDrops();
        });

        topRight.add(txtSearch);
        topRight.add(btnReloadServer);
        topRight.add(btnReload);

        top.add(title, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.58);
        split.setDividerLocation(760);
        split.setBorder(null);

        split.setLeftComponent(createTablePanel());
        split.setRightComponent(createEditorPanel());

        add(split, BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        String[] cols = {
                "ID",
                "Bật",
                "Mob",
                "Map",
                "Item",
                "SL",
                "Tỉ lệ",
                "Note"
        };

        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            public Class<?> getColumnClass(int column) {
                if (column == 1) {
                    return Boolean.class;
                }
                return Object.class;
            }
        };

        table = new JTable(model);
        styleTable(table);
        table.setRowHeight(34);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(45);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(55);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);
        table.getColumnModel().getColumn(7).setPreferredWidth(250);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int id = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString());
                    loadDropToForm(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEditorPanel() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(0, 10, 0, 0));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        tfId = new JTextField();
        tfId.setEditable(false);

        cbActive = new JCheckBox("Bật drop");
        cbActive.setSelected(true);
        cbActive.setBackground(Color.WHITE);

        tfMobId = new JTextField("-1");
        tfMapId = new JTextField("-1");
        tfItemId = new JTextField("");
        tfQuantity = new JTextField("1");
        tfRateNum = new JTextField("1");
        tfRateDen = new JTextField("100");
        tfNote = new JTextField("");

        cbFamily = new JComboBox<>(new String[]{
                "-1 - Tất cả",
                "0 - Trái Đất",
                "1 - Namếc",
                "2 - Xayda"
        });

        taOptions = new JTextArea(4, 20);
        taOptions.setLineWrap(true);
        taOptions.setWrapStyleWord(true);

        taConditions = new JTextArea(5, 20);
        taConditions.setLineWrap(true);
        taConditions.setWrapStyleWord(true);

        lbMobName = new JLabel("Tất cả mob");
        lbMapName = new JLabel("Tất cả map");
        lbItemName = new JLabel("");
        lbIcon = new JLabel();
        lbIcon.setPreferredSize(new Dimension(42, 42));

        addRow(form, g, 0, "ID:", tfId);
        addRow(form, g, 1, "Trạng thái:", cbActive);
        addSection(form, g, 2, "Điều Kiện & Item");

        addRow(form, g, 3, "Mob ID:", tfMobId);
        addRow(form, g, 4, "Tên Mob:", lbMobName);
        addRow(form, g, 5, "Map ID:", tfMapId);
        addRow(form, g, 6, "Tên Map:", lbMapName);
        addRow(form, g, 7, "Item ID:", tfItemId);

        JPanel itemPreview = new JPanel(new BorderLayout(8, 0));
        itemPreview.setBackground(Color.WHITE);
        itemPreview.add(lbIcon, BorderLayout.WEST);
        itemPreview.add(lbItemName, BorderLayout.CENTER);
        addRow(form, g, 8, "Tên Item:", itemPreview);

        addRow(form, g, 9, "Số lượng:", tfQuantity);
        addRow(form, g, 10, "Tỉ lệ:", createRatePanel());
        addRow(form, g, 11, "Family:", cbFamily);

        addSection(form, g, 12, "Nâng Cao");
        addRow(form, g, 13, "Note:", tfNote);
        addRow(form, g, 14, "Options:", new JScrollPane(taOptions));
        addRow(form, g, 15, "Conditions:", new JScrollPane(taConditions));

        JLabel hint = new JLabel("<html>"
                + "Options: 30:0;93:30;50:10<br>"
                + "Conditions: min_power=1000000;task_id=5;active=true;full_dtl=true;full_dhd=true<br>"
                + "Mob ID = -1 là tất cả mob, Map ID = -1 là tất cả map."
                + "</html>");
        hint.setForeground(Color.GRAY);
        addRow(form, g, 16, "Gợi ý:", hint);

        addDocListener(tfMobId, this::refreshPreviewNames);
        addDocListener(tfMapId, this::refreshPreviewNames);
        addDocListener(tfItemId, this::refreshPreviewNames);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.setBackground(Color.WHITE);

        JButton btnNew = createButton("Mới", Color.GRAY);
        btnNew.addActionListener(e -> clearForm());

        JButton btnDelete = createButton("Xóa", COL_DANGER);
        btnDelete.addActionListener(e -> deleteCurrent());

        JButton btnSave = createButton("Lưu", COL_PRIMARY);
        btnSave.addActionListener(e -> saveCurrent(false));

        JButton btnAdd = createButton("Thêm", COL_SUCCESS);
        btnAdd.addActionListener(e -> saveCurrent(true));

        buttons.add(btnNew);
        buttons.add(btnDelete);
        buttons.add(btnSave);
        buttons.add(btnAdd);

        root.add(new JScrollPane(form), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private JPanel createRatePanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 5, 0));
        p.setBackground(Color.WHITE);
        p.add(tfRateNum);
        p.add(new JLabel("/", JLabel.CENTER));
        p.add(tfRateDen);
        return p;
    }

    private void addSection(JPanel panel, GridBagConstraints g, int y, String text) {
        JLabel lb = new JLabel(text);
        lb.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lb.setForeground(COL_PRIMARY);

        g.gridx = 0;
        g.gridy = y;
        g.gridwidth = 2;
        panel.add(lb, g);
        g.gridwidth = 1;
    }

    private void addRow(JPanel panel, GridBagConstraints g, int y, String label, Component comp) {
        JLabel lb = new JLabel(label);
        lb.setFont(FONT_BOLD);

        g.gridx = 0;
        g.gridy = y;
        g.weightx = 0;
        panel.add(lb, g);

        g.gridx = 1;
        g.gridy = y;
        g.weightx = 1;

        if (comp instanceof JTextField) {
            ((JTextField) comp).setFont(FONT_UI);
            ((JTextField) comp).setPreferredSize(new Dimension(260, 32));
        }

        comp.setFont(FONT_UI);
        panel.add(comp, g);
    }

    private void loadCacheData() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            mapNames.clear();
            mobNames.clear();
            itemInfos.clear();
            iconCache.clear();

            try (ResultSet rs = st.executeQuery("SELECT * FROM map_template")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = getStringColumnSafe(rs, "NAME", "name");
                    mapNames.put(id, name != null ? name : "Map " + id);
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM mob_template")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = getStringColumnSafe(rs, "name", "NAME");
                    mobNames.put(id, name != null ? name : "Mob " + id);
                }
            }

            try (ResultSet rs = st.executeQuery("SELECT * FROM item_template")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = getStringColumnSafe(rs, "NAME", "name");
                    int iconId = getIntColumnSafe(rs, "icon_id", "iconID", "icon", "iconId");
                    itemInfos.put(id, new ItemInfo(name != null ? name : "Item " + id, iconId));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDrops() {
        String keyword = txtSearch == null ? "" : txtSearch.getText().trim();

        String sql = "SELECT * FROM drop_item ";
        boolean hasKey = !keyword.isEmpty();

        if (hasKey) {
            sql += "WHERE CAST(id AS CHAR) LIKE ? "
                    + "OR CAST(mob_id AS CHAR) LIKE ? "
                    + "OR CAST(map_id AS CHAR) LIKE ? "
                    + "OR CAST(item_id AS CHAR) LIKE ? "
                    + "OR note LIKE ? ";
        }

        sql += "ORDER BY id ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasKey) {
                String k = "%" + keyword + "%";
                for (int i = 1; i <= 5; i++) {
                    ps.setString(i, k);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                model.setRowCount(0);

                while (rs.next()) {
                    int id = rs.getInt("id");
                    boolean active = rs.getInt("active") == 1;
                    int mobId = rs.getInt("mob_id");
                    int mapId = rs.getInt("map_id");
                    int itemId = rs.getInt("item_id");
                    int quantity = rs.getInt("quantity");
                    int rateNum = rs.getInt("rate_num");
                    int rateDen = rs.getInt("rate_den");
                    String note = getStringColumnSafe(rs, "note");

                    Vector<Object> row = new Vector<>();
                    row.add(id);
                    row.add(active);
                    row.add(mobId == -1 ? "All" : mobId + " - " + getMobName(mobId));
                    row.add(mapId == -1 ? "All" : mapId + " - " + getMapName(mapId));
                    row.add(itemId + " - " + getItemName(itemId));
                    row.add(quantity);
                    row.add(rateNum + "/" + rateDen);
                    row.add(note == null ? "" : note);
                    model.addRow(row);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải drop_item: " + e.getMessage());
        }
    }

    private void loadDropToForm(int id) {
        String sql = "SELECT * FROM drop_item WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tfId.setText(String.valueOf(rs.getInt("id")));
                    cbActive.setSelected(rs.getInt("active") == 1);
                    tfMobId.setText(String.valueOf(rs.getInt("mob_id")));
                    tfMapId.setText(String.valueOf(rs.getInt("map_id")));
                    tfItemId.setText(String.valueOf(rs.getInt("item_id")));
                    tfQuantity.setText(String.valueOf(rs.getInt("quantity")));
                    tfRateNum.setText(String.valueOf(rs.getInt("rate_num")));
                    tfRateDen.setText(String.valueOf(rs.getInt("rate_den")));

                    int family = rs.getInt("family");
                    cbFamily.setSelectedIndex(family == 0 ? 1 : family == 1 ? 2 : family == 2 ? 3 : 0);

                    tfNote.setText(safe(rs.getString("note")));
                    taOptions.setText(safe(rs.getString("options")));
                    taConditions.setText(safe(rs.getString("conditions")));

                    refreshPreviewNames();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi đọc drop: " + e.getMessage());
        }
    }

    private void saveCurrent(boolean insert) {
        try {
            int id = tfId.getText().trim().isEmpty() ? -1 : Integer.parseInt(tfId.getText().trim());

            int active = cbActive.isSelected() ? 1 : 0;
            int mobId = Integer.parseInt(tfMobId.getText().trim());
            int mapId = Integer.parseInt(tfMapId.getText().trim());
            int itemId = Integer.parseInt(tfItemId.getText().trim());
            int quantity = Integer.parseInt(tfQuantity.getText().trim());
            int rateNum = Integer.parseInt(tfRateNum.getText().trim());
            int rateDen = Integer.parseInt(tfRateDen.getText().trim());
            int family = getFamilyValue();

            String note = tfNote.getText().trim();
            String options = taOptions.getText().trim();
            String conditions = taConditions.getText().trim();

            if (itemId <= 0) {
                JOptionPane.showMessageDialog(this, "Item ID phải lớn hơn 0!");
                return;
            }

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
                return;
            }

            if (rateNum <= 0 || rateDen <= 0 || rateNum > rateDen) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ sai. Ví dụ: 1 / 100");
                return;
            }

            if (!isValidOptions(options)) {
                JOptionPane.showMessageDialog(this, "Options sai. Ví dụ đúng: 30:0;93:30");
                return;
            }

            if (insert || id <= 0) {
                insertDrop(active, mobId, mapId, itemId, quantity, rateNum, rateDen, family, note, options, conditions);
            } else {
                updateDrop(id, active, mobId, mapId, itemId, quantity, rateNum, rateDen, family, note, options, conditions);
            }

            DropManager.gI().reload();
            loadDrops();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập sai: " + e.getMessage());
        }
    }

    private void insertDrop(int active, int mobId, int mapId, int itemId, int quantity,
                            int rateNum, int rateDen, int family, String note,
                            String options, String conditions) throws Exception {

        String sql = "INSERT INTO drop_item(active, mob_id, map_id, item_id, quantity, rate_num, rate_den, family, note, options, conditions) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillDropStatement(ps, active, mobId, mapId, itemId, quantity, rateNum, rateDen, family, note, options, conditions);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tfId.setText(String.valueOf(rs.getInt(1)));
                }
            }
        }

        JOptionPane.showMessageDialog(this, "Thêm drop thành công!");
    }

    private void updateDrop(int id, int active, int mobId, int mapId, int itemId, int quantity,
                            int rateNum, int rateDen, int family, String note,
                            String options, String conditions) throws Exception {

        String sql = "UPDATE drop_item SET active=?, mob_id=?, map_id=?, item_id=?, quantity=?, "
                + "rate_num=?, rate_den=?, family=?, note=?, options=?, conditions=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillDropStatement(ps, active, mobId, mapId, itemId, quantity, rateNum, rateDen, family, note, options, conditions);
            ps.setInt(12, id);
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Lưu drop thành công!");
    }

    private void fillDropStatement(PreparedStatement ps, int active, int mobId, int mapId, int itemId,
                                   int quantity, int rateNum, int rateDen, int family,
                                   String note, String options, String conditions) throws Exception {
        ps.setInt(1, active);
        ps.setInt(2, mobId);
        ps.setInt(3, mapId);
        ps.setInt(4, itemId);
        ps.setInt(5, quantity);
        ps.setInt(6, rateNum);
        ps.setInt(7, rateDen);
        ps.setInt(8, family);
        ps.setString(9, note);
        ps.setString(10, options);
        ps.setString(11, conditions);
    }

    private void deleteCurrent() {
        if (tfId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa chọn drop để xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xóa drop này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(tfId.getText().trim());

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM drop_item WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            clearForm();
            DropManager.gI().reload();
            loadDrops();

            JOptionPane.showMessageDialog(this, "Đã xóa drop!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage());
        }
    }

    private void clearForm() {
        tfId.setText("");
        cbActive.setSelected(true);
        tfMobId.setText("-1");
        tfMapId.setText("-1");
        tfItemId.setText("");
        tfQuantity.setText("1");
        tfRateNum.setText("1");
        tfRateDen.setText("100");
        cbFamily.setSelectedIndex(0);
        tfNote.setText("");
        taOptions.setText("");
        taConditions.setText("");
        lbMobName.setText("Tất cả mob");
        lbMapName.setText("Tất cả map");
        lbItemName.setText("");
        lbIcon.setIcon(null);
    }

    private int getFamilyValue() {
        int index = cbFamily.getSelectedIndex();
        return switch (index) {
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            default -> -1;
        };
    }

    private void refreshPreviewNames() {
        try {
            int mobId = Integer.parseInt(tfMobId.getText().trim());
            lbMobName.setText(mobId == -1 ? "Tất cả mob" : getMobName(mobId));
        } catch (Exception e) {
            lbMobName.setText("Mob ID sai");
        }

        try {
            int mapId = Integer.parseInt(tfMapId.getText().trim());
            lbMapName.setText(mapId == -1 ? "Tất cả map" : getMapName(mapId));
        } catch (Exception e) {
            lbMapName.setText("Map ID sai");
        }

        try {
            int itemId = Integer.parseInt(tfItemId.getText().trim());
            lbItemName.setText(getItemName(itemId));
            lbIcon.setIcon(getItemIcon(itemId));
        } catch (Exception e) {
            lbItemName.setText("Item ID sai");
            lbIcon.setIcon(null);
        }
    }

    private boolean isValidOptions(String options) {
        if (options == null || options.trim().isEmpty()) {
            return true;
        }

        try {
            String[] arr = options.split(";");

            for (String raw : arr) {
                String s = raw.trim();
                if (s.isEmpty()) {
                    continue;
                }

                String[] kv = s.split(":");
                if (kv.length != 2) {
                    return false;
                }

                Integer.parseInt(kv[0].trim());
                Integer.parseInt(kv[1].trim());
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private String getMapName(int id) {
        return mapNames.getOrDefault(id, "Map lạ " + id);
    }

    private String getMobName(int id) {
        return mobNames.getOrDefault(id, "Mob lạ " + id);
    }

    private String getItemName(int id) {
        ItemInfo info = itemInfos.get(id);
        if (info == null) {
            return "Item ID " + id + " chưa có trong item_template";
        }
        return info.name;
    }

    private ImageIcon getItemIcon(int itemId) {
        ItemInfo info = itemInfos.get(itemId);
        if (info == null || info.iconId <= 0) {
            return null;
        }

        int iconId = info.iconId;

        if (iconCache.containsKey(iconId)) {
            return iconCache.get(iconId);
        }

        try {
            File f = DataGame.getIconFile(iconId);

            if (f != null && f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(img);
                iconCache.put(iconId, icon);
                return icon;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getStringColumnSafe(ResultSet rs, String... cols) {
        for (String col : cols) {
            try {
                return rs.getString(col);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private int getIntColumnSafe(ResultSet rs, String... cols) {
        for (String col : cols) {
            try {
                return rs.getInt(col);
            } catch (Exception ignored) {
            }
        }
        return -1;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private void addDocListener(JTextField tf, Runnable run) {
        tf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                run.run();
            }

            public void removeUpdate(DocumentEvent e) {
                run.run();
            }

            public void changedUpdate(DocumentEvent e) {
                run.run();
            }
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 14, 8, 14));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void styleTable(JTable t) {
        t.setFont(FONT_UI);
        t.setGridColor(new Color(230, 230, 230));
        t.setShowGrid(true);
        t.setSelectionBackground(new Color(204, 229, 255));
        t.setSelectionForeground(Color.BLACK);

        JTableHeader header = t.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COL_HEADER);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 34));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < t.getColumnCount(); i++) {
            if (i != 7) {
                t.getColumnModel().getColumn(i).setCellRenderer(center);
            }
        }
    }
}