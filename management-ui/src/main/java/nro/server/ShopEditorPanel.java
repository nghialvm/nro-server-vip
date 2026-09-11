package nro.server;

import Data.DataGame;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;
import jbcd.ConnectDB;

public class ShopEditorPanel extends JPanel {

    private static final Color GRID_COLOR = Color.LIGHT_GRAY;

    private String colId = "id";
    private String colShopId = "shop_id";
    private String colName = "";
    private String colType = "";
    private String colItems = "items";

    private JTable tableShop;
    private DefaultTableModel modelShop;

    private final Map<Integer, String> itemTemplateMap = new HashMap<>();
    private final Map<Integer, Integer> itemIconMap = new HashMap<>();
    private final Map<Integer, String> shopOwnerMap = new HashMap<>();
    private final Map<Integer, Integer> npcHeadPartIconMap = new HashMap<>();
    private static final Map<Integer, String> OPTION_TEMPLATE = new HashMap<>();
    private final List<ItemData> listAllItems = new ArrayList<>();

    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();
    private final Map<Integer, Boolean> noIconCache = new HashMap<>();
    private final Map<Integer, ImageIcon> npcHeadCache = new HashMap<>();

    public ShopEditorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        startLoadingData();
    }

    private void setupGlobalShortcuts(JComponent component, Runnable saveAction) {
        InputMap im = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = component.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "quickSave");
        am.put("quickSave", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveAction != null) {
                    saveAction.run();
                }
            }
        });
    }

    private void addUndoRedo(JTextComponent textComponent) {
        UndoManager undoManager = new UndoManager();
        textComponent.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        InputMap im = textComponent.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = textComponent.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "Undo");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "Redo");

        am.put("Undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) undoManager.undo();
            }
        });
        am.put("Redo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) undoManager.redo();
            }
        });
    }

    private void startLoadingData() {
        new Thread(() -> {
            loadPartsHead();
            initOptionTemplateData();
            loadItemTemplates();
            loadShopOwners();
            loadDataFromDB();
        }).start();
    }

    private static class ItemData {
        int id;
        String name;
        int type;
        int gender;

        public ItemData(int id, String name, int type, int gender) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.gender = gender;
        }
    }

    private static class RestoreData {
        int index;
        Vector<Object> data;

        public RestoreData(int index, Vector<Object> data) {
            this.index = index;
            this.data = data;
        }
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    private void loadPartsHead() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT id, data FROM part WHERE type = 0")) {
            while (rs.next()) {
                try {
                    String dataJson = rs.getString("data");
                    JsonArray arr = new JsonParser().parse(dataJson).getAsJsonArray();
                    if (arr.size() > 0) {
                        JsonArray firstElement = arr.get(0).getAsJsonArray();
                        int iconId = firstElement.get(0).getAsInt();
                        npcHeadPartIconMap.put(rs.getInt("id"), iconId);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (SQLException ignored) {
        }
    }

    private ImageIcon drawNpcHead(int headPartId) {
        if (headPartId <= 0) {
            return null;
        }
        if (npcHeadCache.containsKey(headPartId)) {
            return npcHeadCache.get(headPartId);
        }

        Integer iconId = npcHeadPartIconMap.get(headPartId);
        if (iconId != null) {
            ImageIcon icon = getIconByFileId(iconId, 32);
            if (icon != null) {
                npcHeadCache.put(headPartId, icon);
                return icon;
            }
        }
        return null;
    }

    private ImageIcon getIconByFileId(int fileId, int size) {
        try {
            String[] zoomLevels = {"x4", "x3", "x2", "x1"};
            for (String zoom : zoomLevels) {
                File f = DataGame.getIconFile(fileId);
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    Image dimg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    return new ImageIcon(dimg);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void initOptionTemplateData() {
        OPTION_TEMPLATE.clear();
        String query = "SELECT id, name FROM item_option_template";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                OPTION_TEMPLATE.put(id, name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            OPTION_TEMPLATE.put(50, "Sức đánh +#%");
            OPTION_TEMPLATE.put(77, "HP +#%");
            OPTION_TEMPLATE.put(103, "KI +#%");
        }
    }

    private String getOptionDescription(int id, int param) {
        String template = OPTION_TEMPLATE.getOrDefault(id, "Unknown Option (" + id + ")");
        return template.replace("#", String.valueOf(param));
    }

    private void detectColumns(ResultSetMetaData meta) throws SQLException {
        int count = meta.getColumnCount();
        for (int i = 1; i <= count; i++) {
            String cName = meta.getColumnName(i).toLowerCase();
            if (cName.equals("items") || cName.equals("item_json")) {
                colItems = cName;
            } else if (cName.equals("id")) {
                colId = cName;
            } else if (cName.equals("shop_id")) {
                colShopId = cName;
            } else if (colName.isEmpty() && (cName.contains("name") || cName.contains("title"))) {
                colName = cName;
            } else if (colType.isEmpty() && (cName.contains("type") || cName.equals("kieu") || cName.contains("index"))) {
                colType = cName;
            }
        }
    }

    private void loadItemTemplates() {
        listAllItems.clear();
        itemTemplateMap.clear();

        String query = "SELECT id, name, icon_id, type, gender FROM item_template";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int type = rs.getInt("type");
                int gender = rs.getInt("gender");
                int iconId = rs.getInt("icon_id");

                itemTemplateMap.put(id, name);
                itemIconMap.put(id, iconId);
                listAllItems.add(new ItemData(id, name, type, gender));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getItemName(int tempId) {
        return itemTemplateMap.getOrDefault(tempId, "Unknown Item (" + tempId + ")");
    }

    private ImageIcon getItemIcon(int itemId) {
        if (iconCache.containsKey(itemId)) {
            return iconCache.get(itemId);
        }
        if (noIconCache.containsKey(itemId)) {
            return null;
        }

        int iconId = itemIconMap.getOrDefault(itemId, -1);
        ImageIcon icon = getIconByFileId(iconId, 20);
        if (icon != null) {
            iconCache.put(itemId, icon);
            return icon;
        }

        noIconCache.put(itemId, true);
        return null;
    }

    private void loadShopOwners() {
        String query = "SELECT s.id, n.name FROM shop s LEFT JOIN npc_template n ON s.npc_id = n.id";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int shopId = rs.getInt("id");
                String npcName = rs.getString("name");
                if (npcName == null) {
                    npcName = "Unknown";
                }
                shopOwnerMap.put(shopId, npcName);
            }
        } catch (SQLException e) {
        }
    }

    private String getShopOwnerName(int shopId) {
        return shopOwnerMap.getOrDefault(shopId, "---");
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.setBorder(ServerGuiUtils.createSectionBorder("Quản lý Shop (Table: tab_shop)"));

        JButton btnReload = ServerGuiUtils.createStyledButton("Tải lại", new Color(0, 120, 215), Color.WHITE);
        btnReload.addActionListener(e -> {
            modelShop.setRowCount(0);
            startLoadingData();
        });

        JButton btnSave = ServerGuiUtils.createStyledButton("Lưu xuống DB (Ctrl+S)", new Color(40, 167, 69), Color.WHITE);
        btnSave.addActionListener(e -> saveSelectedRowToDB());

        JButton btnHelp = ServerGuiUtils.createStyledButton("Hướng dẫn", new Color(23, 162, 184), Color.WHITE);
        btnHelp.addActionListener(e -> showHelp());

        top.add(btnReload);
        top.add(btnSave);
        top.add(btnHelp);
        top.add(new JLabel("<html><i style='color:red'>*Click đúp 'Vật Phẩm' để sửa.</i></html>"));
        add(top, BorderLayout.NORTH);

        String[] columns = {"ID", "Avatar", "Shop ID", "Chủ Shop (NPC)", "Tên Tab", "Loại/Index", "Danh sách Vật Phẩm (Click đúp)"};
        modelShop = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return (c == 1) ? ImageIcon.class : Object.class;
            }
        };

        tableShop = new JTable(modelShop);
        tableShop.setRowHeight(40);
        tableShop.setShowGrid(true);
        tableShop.setGridColor(GRID_COLOR);

        tableShop.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        });

        tableShop.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableShop.getColumnModel().getColumn(1).setPreferredWidth(45);
        tableShop.getColumnModel().getColumn(2).setPreferredWidth(50);
        tableShop.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableShop.getColumnModel().getColumn(4).setPreferredWidth(120);
        tableShop.getColumnModel().getColumn(5).setPreferredWidth(50);
        tableShop.getColumnModel().getColumn(6).setPreferredWidth(450);

        tableShop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tableShop.getSelectedColumn() == 6) {
                    openItemEditorDialog(tableShop.getSelectedRow());
                }
            }
        });

        setupGlobalShortcuts(this, this::saveSelectedRowToDB);
        add(new JScrollPane(tableShop), BorderLayout.CENTER);
    }

    private void showHelp() {
        String html = "<html><body style='width: 300px'>"
                + "<h3>Hướng dẫn sử dụng Shop Editor</h3>"
                + "<ul>"
                + "<li><b>Sửa Tên/Loại Tab:</b> Click đúp trực tiếp vào ô trên bảng chính để sửa.</li>"
                + "<li><b>Sửa Vật Phẩm:</b> Click đúp vào cột <i>'Danh sách Vật Phẩm'</i> để mở cửa sổ chi tiết.</li>"
                + "<li><b>Trong cửa sổ Vật Phẩm:</b>"
                + "<ul>"
                + "<li><b>Thêm Mới:</b> Thêm từng item.</li>"
                + "<li><b>Thêm (Nhiều):</b> Mở danh sách chọn nhiều item cùng lúc.</li>"
                + "<li><b>Xóa:</b> Chọn 1 hoặc nhiều dòng rồi nhấn Xóa.</li>"
                + "<li><b>Hoàn tác (Undo):</b> Khôi phục lại các dòng vừa xóa nhầm.</li>"
                + "<li><b>Di chuyển:</b> Dùng nút Lên/Xuống để sắp xếp vị trí item.</li>"
                + "</ul></li>"
                + "<li><b>Phím tắt tiện ích:</b>"
                + "<ul>"
                + "<li><b>Ctrl + S:</b> Lưu dữ liệu (Hoạt động cả ở bảng chính và bảng item).</li>"
                + "<li><b>Ctrl + Z:</b> Hoàn tác (Undo) khi nhập liệu text.</li>"
                + "<li><b>Ctrl + Y:</b> Làm lại (Redo) khi nhập liệu text.</li>"
                + "</ul></li>"
                + "</ul>"
                + "</body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(html), "Hướng dẫn", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    private String buildItemsJsonByTabId(Connection conn, int tabId) {
    try {
        // 1) lấy list item_shop của tab
        String qItem = "SELECT id, temp_id, is_new, is_sell, type_sell, cost, icon_spec FROM item_shop WHERE tab_id=? ORDER BY id ASC";
        try (PreparedStatement psItem = conn.prepareStatement(qItem)) {
            psItem.setInt(1, tabId);

            try (ResultSet rsItem = psItem.executeQuery()) {
                JsonArray arr = new JsonArray();

                // chuẩn bị statement lấy options theo item_shop_id
                String qOpt = "SELECT option_id, param FROM item_shop_option WHERE item_shop_id=? ORDER BY id ASC";
                try (PreparedStatement psOpt = conn.prepareStatement(qOpt)) {

                    while (rsItem.next()) {
                        int itemShopId = rsItem.getInt("id");

                        JsonObject obj = new JsonObject();
                        obj.addProperty("temp_id", rsItem.getInt("temp_id"));
                        obj.addProperty("is_new", rsItem.getInt("is_new") == 1);
                        obj.addProperty("is_sell", rsItem.getInt("is_sell") == 1);
                        obj.addProperty("type_sell", rsItem.getInt("type_sell"));
                        obj.addProperty("cost", rsItem.getInt("cost"));
                        obj.addProperty("item_spec", rsItem.getInt("icon_spec"));

                        // 2) options của item
                        JsonArray opts = new JsonArray();
                        psOpt.setInt(1, itemShopId);
                        try (ResultSet rsOpt = psOpt.executeQuery()) {
                            while (rsOpt.next()) {
                                JsonObject o = new JsonObject();
                                o.addProperty("id", rsOpt.getInt("option_id"));
                                o.addProperty("param", rsOpt.getInt("param"));
                                opts.add(o);
                            }
                        }
                        obj.add("options", opts);

                        arr.add(obj);
                    }
                }

                // format đẹp giống bạn đang làm
                StringBuilder sb = new StringBuilder("[\n");
                for (int i = 0; i < arr.size(); i++) {
                    sb.append(arr.get(i).toString());
                    if (i < arr.size() - 1) sb.append(",\n");
                }
                sb.append("\n]");
                return sb.toString();
            }
        }
    } catch (Exception ignored) {
    }
    return "[]";
}


    private void loadDataFromDB() {
        SwingUtilities.invokeLater(() -> modelShop.setRowCount(0));

        String query = """
                SELECT ts.*, n.head 
                FROM tab_shop ts 
                LEFT JOIN shop s ON ts.shop_id = s.id 
                LEFT JOIN npc_template n ON s.npc_id = n.id 
                ORDER BY ts.shop_id ASC, ts.id ASC
                """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            detectColumns(rs.getMetaData());

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                int sId = rs.getInt(colShopId);
                int headPartId = rs.getInt("head");

                row.add(rs.getInt(colId));
                row.add(drawNpcHead(headPartId));
                row.add(sId);
                row.add(getShopOwnerName(sId));

                try {
                    row.add(rs.getString(colName));
                } catch (Exception e) {
                    row.add("NULL");
                }
                try {
                    row.add(rs.getInt(colType));
                } catch (Exception e) {
                    row.add(0);
                }

                int tabId = rs.getInt(colId);
                String itemsJson = buildItemsJsonByTabId(conn, tabId);
                row.add(itemsJson);


                SwingUtilities.invokeLater(() -> modelShop.addRow(row));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSelectedRowToDB() {
        int row = tableShop.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chưa chọn dòng!");
            return;
        }

        try {
            int id = Integer.parseInt(modelShop.getValueAt(row, 0).toString());
            int shopId = Integer.parseInt(modelShop.getValueAt(row, 2).toString());
            String name = modelShop.getValueAt(row, 4).toString();
            int type = Integer.parseInt(modelShop.getValueAt(row, 5).toString());
            String items = modelShop.getValueAt(row, 6).toString();

            new Thread(() -> {
                try (Connection conn = getConnection()) {
                    conn.setAutoCommit(false);

                    // =========================
                    // 1. update tab_shop
                    // =========================
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE tab_shop SET shop_id=?, name=? WHERE id=?")) {
                        ps.setInt(1, shopId);
                        ps.setString(2, name);
                        ps.setInt(3, id);
                        ps.executeUpdate();
                    }

                    // =========================
                    // 2. xóa option cũ
                    // =========================
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE o FROM item_shop_option o "
                            + "JOIN item_shop i ON o.item_shop_id = i.id "
                            + "WHERE i.tab_id = ?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }

                    // =========================
                    // 3. xóa item_shop cũ
                    // =========================
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM item_shop WHERE tab_id = ?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                    }

                    // =========================
                    // 4. insert lại item + option
                    // =========================
                    JsonArray arr = new JsonParser().parse(items).getAsJsonArray();

                    String sqlItem
                            = "INSERT INTO item_shop(tab_id,temp_id,is_new,is_sell,type_sell,cost,icon_spec) "
                            + "VALUES (?,?,?,?,?,?,?)";

                    String sqlOpt
                            = "INSERT INTO item_shop_option(item_shop_id,option_id,param) VALUES (?,?,?)";

                    try (
                            PreparedStatement psItem
                            = conn.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS); PreparedStatement psOpt
                            = conn.prepareStatement(sqlOpt)) {

                        for (JsonElement el : arr) {
                            JsonObject obj = el.getAsJsonObject();

                            psItem.setInt(1, id); // tab_id
                            psItem.setInt(2, obj.get("temp_id").getAsInt());
                            psItem.setInt(3, obj.get("is_new").getAsBoolean() ? 1 : 0);
                            psItem.setInt(4, obj.get("is_sell").getAsBoolean() ? 1 : 0);
                            psItem.setInt(5, obj.get("type_sell").getAsInt());
                            psItem.setInt(6, obj.get("cost").getAsInt());
                            psItem.setInt(7, obj.get("item_spec").getAsInt());
                            psItem.executeUpdate();

                            int itemShopId;
                            try (ResultSet gk = psItem.getGeneratedKeys()) {
                                gk.next();
                                itemShopId = gk.getInt(1);
                            }

                            JsonArray opts = obj.has("options")
                                    ? obj.getAsJsonArray("options")
                                    : new JsonArray();

                            for (JsonElement oe : opts) {
                                JsonObject o = oe.getAsJsonObject();
                                psOpt.setInt(1, itemShopId);
                                psOpt.setInt(2, o.get("id").getAsInt());
                                psOpt.setInt(3, o.get("param").getAsInt());
                                psOpt.executeUpdate();
                            }
                        }
                    }

                    conn.commit();

                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(this, "Đã lưu shop!")
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(this, "Lỗi lưu shop!")
                    );
                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu lỗi!");
        }
    }

    private void openItemEditorDialog(int mainRow) {
        String jsonString = (String) modelShop.getValueAt(mainRow, 6);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh sách vật phẩm", true);
        dialog.setSize(1100, 600);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        String[] itemCols = {"Temp ID", "Icon", "Tên Item", "Giá", "Loại Bán", "Spec", "Mới?", "Bán?", "Options (JSON)"};
        DefaultTableModel itemModel = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 1 && column != 2 && column != 8;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 1) {
                    return ImageIcon.class;
                }
                return (c == 6 || c == 7) ? Boolean.class : String.class;
            }
        };

        JTable itemTable = new JTable(itemModel);
        itemTable.setRowHeight(30);
        itemTable.setShowGrid(true);
        itemTable.setGridColor(GRID_COLOR);

        itemTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(40);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        itemTable.getColumnModel().getColumn(8).setPreferredWidth(300);

        itemModel.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                int r = e.getFirstRow();
                try {
                    int newId = Integer.parseInt(itemModel.getValueAt(r, 0).toString());
                    itemModel.setValueAt(getItemName(newId), r, 2);
                    itemModel.setValueAt(getItemIcon(newId), r, 1);
                } catch (Exception ignored) {
                }
            }
        });

        try {
            JsonArray arr = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject obj = e.getAsJsonObject();
                Vector<Object> v = new Vector<>();
                int tempId = Integer.parseInt(getData(obj, "temp_id"));
                v.add(tempId);
                v.add(getItemIcon(tempId));
                v.add(getItemName(tempId));
                v.add(getData(obj, "cost"));
                v.add(getData(obj, "type_sell"));
                v.add(getData(obj, "item_spec"));
                v.add(obj.has("is_new") && obj.get("is_new").getAsBoolean());
                v.add(obj.has("is_sell") && obj.get("is_sell").getAsBoolean());
                v.add(obj.has("options") ? obj.get("options").toString() : "[]");
                itemModel.addRow(v);
            }
        } catch (Exception ignored) {
        }

        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnAddBulk = new JButton("Thêm (Nhiều)");
        JButton btnUp = new JButton("⬆ Lên");
        JButton btnDown = new JButton("⬇ Xuống");
        JButton btnEdit = new JButton("Sửa Item");
        JButton btnDel = new JButton("Xóa (Chọn nhiều)");
        btnDel.setForeground(Color.RED);

        JButton btnUndo = new JButton("↩ Hoàn tác");
        btnUndo.setEnabled(false);

        JButton btnOk = new JButton("LƯU JSON & ĐÓNG (Ctrl+S)");
        btnOk.setBackground(new Color(0, 120, 215));
        btnOk.setForeground(Color.WHITE);

        Stack<List<RestoreData>> undoStack = new Stack<>();

        btnAdd.addActionListener(e -> openItemBuilderDialog(itemModel, -1));

        btnAddBulk.addActionListener(e -> showBulkSearchDialog(dialog, (id, name) -> {
            Vector<Object> row = new Vector<>();
            row.add(id);
            row.add(getItemIcon(id));
            row.add(name);
            row.add("0");
            row.add("0");
            row.add("0");
            row.add(false);
            row.add(true);
            row.add("[]");
            itemModel.addRow(row);
        }));

        btnUp.addActionListener(e -> {
            int row = itemTable.getSelectedRow();
            if (row > 0) {
                itemModel.moveRow(row, row, row - 1);
                itemTable.setRowSelectionInterval(row - 1, row - 1);
            }
        });

        btnDown.addActionListener(e -> {
            int row = itemTable.getSelectedRow();
            if (row != -1 && row < itemModel.getRowCount() - 1) {
                itemModel.moveRow(row, row, row + 1);
                itemTable.setRowSelectionInterval(row + 1, row + 1);
            }
        });

        btnEdit.addActionListener(e -> {
            if (itemTable.getSelectedRow() != -1) {
                openItemBuilderDialog(itemModel, itemTable.getSelectedRow());
            }
        });

        btnDel.addActionListener(e -> {
            int[] selectedRows = itemTable.getSelectedRows();
            if (selectedRows.length > 0) {
                if (JOptionPane.showConfirmDialog(dialog, "Xóa " + selectedRows.length + " item đã chọn?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    List<RestoreData> batch = new ArrayList<>();
                    for (int i = selectedRows.length - 1; i >= 0; i--) {
                        int rowIdx = selectedRows[i];
                        Vector data = (Vector) ((Vector) itemModel.getDataVector()).get(rowIdx);
                        batch.add(new RestoreData(rowIdx, new Vector<>(data)));
                        itemModel.removeRow(rowIdx);
                    }

                    undoStack.push(batch);
                    btnUndo.setEnabled(true);
                }
            }
        });

        btnUndo.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                List<RestoreData> batch = undoStack.pop();
                for (RestoreData item : batch) {
                    itemModel.insertRow(item.index, item.data);
                }
                if (undoStack.isEmpty()) {
                    btnUndo.setEnabled(false);
                }
            }
        });

        Runnable saveAction = () -> {
            try {
                JsonArray newArr = new JsonArray();
                for (int i = 0; i < itemModel.getRowCount(); i++) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("cost", Integer.parseInt(itemModel.getValueAt(i, 3).toString()));
                    obj.addProperty("type_sell", Integer.parseInt(itemModel.getValueAt(i, 4).toString()));
                    obj.addProperty("is_new", (Boolean) itemModel.getValueAt(i, 6));
                    obj.addProperty("temp_id", Integer.parseInt(itemModel.getValueAt(i, 0).toString()));
                    obj.addProperty("item_spec", Integer.parseInt(itemModel.getValueAt(i, 5).toString()));

                    try {
                        obj.add("options", new JsonParser().parse(itemModel.getValueAt(i, 8).toString()));
                    } catch (Exception ex) {
                        obj.add("options", new JsonArray());
                    }

                    obj.addProperty("is_sell", (Boolean) itemModel.getValueAt(i, 7));
                    newArr.add(obj);
                }

                StringBuilder sb = new StringBuilder("[\n");
                for (int j = 0; j < newArr.size(); j++) {
                    sb.append(newArr.get(j).toString());
                    if (j < newArr.size() - 1) {
                        sb.append(",\n");
                    }
                }
                sb.append("\n]");

                modelShop.setValueAt(sb.toString(), mainRow, 6);
                dialog.dispose();
                if (JOptionPane.showConfirmDialog(this, "Lưu xuống Database?", "Lưu?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    saveSelectedRowToDB();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        };
        btnOk.addActionListener(e -> saveAction.run());

        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openItemBuilderDialog(itemModel, itemTable.getSelectedRow());
                }
            }
        });

        setupGlobalShortcuts(dialog.getRootPane(), saveAction);

        btnPanel.add(btnAdd);
        btnPanel.add(btnAddBulk);
        btnPanel.add(btnUp);
        btnPanel.add(btnDown);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDel);
        btnPanel.add(btnUndo);
        btnPanel.add(btnOk);
        dialog.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openItemBuilderDialog(DefaultTableModel parentModel, int editRow) {
        JDialog dialog = new JDialog((Frame) null, "Cấu hình Vật Phẩm Chi Tiết", true);
        dialog.setSize(750, 700);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(null);

        JPanel pTop = new JPanel(new GridBagLayout());
        pTop.setBorder(new EmptyBorder(10, 10, 0, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField();
        addUndoRedo(txtId);
        JLabel lblName = new JLabel("...");
        lblName.setForeground(Color.BLUE);
        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(24, 24));

        JButton btnSearchItem = new JButton("🔍 Tìm");
        JTextField txtCost = new JTextField("0");
        addUndoRedo(txtCost);
        JComboBox<String> cbTypeSell = new JComboBox<>(new String[]{"0 - Vàng", "1 - Ngọc Xanh", "2 - Hồng Ngọc"});
        JTextField txtSpec = new JTextField("0");
        addUndoRedo(txtSpec);
        JLabel lblSpecName = new JLabel("None");
        lblSpecName.setForeground(new Color(0, 100, 0));
        JButton btnSearchSpec = new JButton("🔍 Tìm Spec");

        JCheckBox chkNew = new JCheckBox("Mới (New)");
        JCheckBox chkSell = new JCheckBox("Bán (Sell)");
        chkSell.setSelected(true);

        String oldJsonOpt = "[]";
        if (editRow != -1) {
            txtId.setText(parentModel.getValueAt(editRow, 0).toString());
            lblName.setText(parentModel.getValueAt(editRow, 2).toString());
            txtCost.setText(parentModel.getValueAt(editRow, 3).toString());
            try {
                cbTypeSell.setSelectedIndex(Integer.parseInt(parentModel.getValueAt(editRow, 4).toString()));
            } catch (Exception e) {
            }
            txtSpec.setText(parentModel.getValueAt(editRow, 5).toString());
            chkNew.setSelected((Boolean) parentModel.getValueAt(editRow, 6));
            chkSell.setSelected((Boolean) parentModel.getValueAt(editRow, 7));
            oldJsonOpt = parentModel.getValueAt(editRow, 8).toString();
        }

        txtId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                u();
            }

            public void removeUpdate(DocumentEvent e) {
                u();
            }

            public void changedUpdate(DocumentEvent e) {
                u();
            }

            void u() {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    lblName.setText(getItemName(id));
                    lblIcon.setIcon(getItemIcon(id));
                } catch (Exception ex) {
                    lblName.setText("...");
                    lblIcon.setIcon(null);
                }
            }
        });

        txtSpec.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                u();
            }

            public void removeUpdate(DocumentEvent e) {
                u();
            }

            public void changedUpdate(DocumentEvent e) {
                u();
            }

            void u() {
                try {
                    lblSpecName.setText(getItemName(Integer.parseInt(txtSpec.getText())));
                } catch (Exception ex) {
                    lblSpecName.setText("None");
                }
            }
        });

        try {
            int id = Integer.parseInt(txtId.getText());
            lblName.setText(getItemName(id));
            lblIcon.setIcon(getItemIcon(id));
        } catch (Exception ignored) {
        }

        btnSearchItem.addActionListener(e -> showItemSearchDialog(dialog, (id, name) -> {
            txtId.setText(String.valueOf(id));
            lblName.setText(name);
        }));

        btnSearchSpec.addActionListener(e -> showItemSearchDialog(dialog, (id, name) -> {
            txtSpec.setText(String.valueOf(id));
            lblSpecName.setText(name);
        }));

        g.gridx = 0;
        g.gridy = 0;
        pTop.add(new JLabel("ID Item:"), g);
        g.gridx = 1;
        pTop.add(txtId, g);
        g.gridx = 2;
        pTop.add(btnSearchItem, g);
        g.gridx = 0;
        g.gridy = 1;
        pTop.add(new JLabel("Info:"), g);
        JPanel pInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pInfo.add(lblIcon);
        pInfo.add(lblName);
        g.gridx = 1;
        g.gridwidth = 2;
        pTop.add(pInfo, g);
        g.gridwidth = 1;
        g.gridx = 0;
        g.gridy = 2;
        pTop.add(new JLabel("Giá bán:"), g);
        g.gridx = 1;
        pTop.add(txtCost, g);
        g.gridx = 2;
        pTop.add(cbTypeSell, g);
        g.gridx = 0;
        g.gridy = 3;
        pTop.add(new JLabel("Spec ID:"), g);
        g.gridx = 1;
        pTop.add(txtSpec, g);
        g.gridx = 2;
        pTop.add(btnSearchSpec, g);
        g.gridx = 0;
        g.gridy = 4;
        pTop.add(new JLabel("Tên Spec:"), g);
        g.gridx = 1;
        g.gridwidth = 2;
        pTop.add(lblSpecName, g);
        g.gridwidth = 1;
        g.gridx = 0;
        g.gridy = 5;
        pTop.add(new JLabel("Tùy chọn:"), g);
        g.gridx = 1;
        g.gridwidth = 2;
        JPanel pChk = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pChk.add(chkNew);
        pChk.add(chkSell);
        pTop.add(pChk, g);
        g.gridwidth = 1;

        String[] oCols = {"ID Option", "Param", "Mô tả (Tự động)"};
        DefaultTableModel oModel = new DefaultTableModel(oCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c != 2;
            }
        };
        JTable oTable = new JTable(oModel);
        oTable.setRowHeight(25);
        oTable.setShowGrid(true);
        oTable.setGridColor(GRID_COLOR);

        oModel.addTableModelListener(e -> {
            int r = e.getFirstRow();
            if (r >= 0 && r < oModel.getRowCount() && e.getColumn() != 2) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int id = Integer.parseInt(oModel.getValueAt(r, 0).toString());
                        int param = Integer.parseInt(oModel.getValueAt(r, 1).toString());
                        oModel.setValueAt(getOptionDescription(id, param), r, 2);
                    } catch (Exception ex) {
                    }
                });
            }
        });

        try {
            JsonArray arr = new JsonParser().parse(oldJsonOpt).getAsJsonArray();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                int id = Integer.parseInt(getData(obj, "id"));
                int param = Integer.parseInt(getData(obj, "param"));
                oModel.addRow(new Object[]{id, param, getOptionDescription(id, param)});
            }
        } catch (Exception e) {
        }

        JPanel pOpt = new JPanel(new BorderLayout());
        pOpt.setBorder(ServerGuiUtils.createSectionBorder("Options"));
        JPanel pOptTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddO = new JButton("Thêm");
        JButton btnFindO = new JButton("🔍 Tìm Option");
        JButton btnDelO = new JButton("Xóa");
        btnAddO.addActionListener(e -> oModel.addRow(new Object[]{"0", "0", getOptionDescription(0, 0)}));

        btnFindO.addActionListener(e -> showSearchDialog(dialog, "Tìm Option", OPTION_TEMPLATE, (id, name) -> {
            oModel.addRow(new Object[]{id, "0", name.replace("#", "0")});
            int newRow = oModel.getRowCount() - 1;
            oTable.setRowSelectionInterval(newRow, newRow);
            oTable.editCellAt(newRow, 1);
            oTable.getEditorComponent().requestFocus();
        }));

        btnDelO.addActionListener(e -> {
            if (oTable.getSelectedRow() != -1) {
                oModel.removeRow(oTable.getSelectedRow());
            }
        });
        pOptTool.add(btnAddO);
        pOptTool.add(btnFindO);
        pOptTool.add(btnDelO);
        pOpt.add(pOptTool, BorderLayout.NORTH);
        pOpt.add(new JScrollPane(oTable), BorderLayout.CENTER);

        JButton btnOk = new JButton("XONG - LƯU ITEM");
        btnOk.setBackground(new Color(40, 167, 69));
        btnOk.setForeground(Color.WHITE);
        Runnable saveItemAction = () -> {
            JsonArray arr = new JsonArray();
            for (int i = 0; i < oModel.getRowCount(); i++) {
                try {
                    JsonObject o = new JsonObject();
                    o.addProperty("id", Integer.parseInt(oModel.getValueAt(i, 0).toString()));
                    o.addProperty("param", Integer.parseInt(oModel.getValueAt(i, 1).toString()));
                    arr.add(o);
                } catch (Exception ex) {
                }
            }
            Object[] row = {txtId.getText(), getItemIcon(Integer.parseInt(txtId.getText())), lblName.getText(), txtCost.getText(), String.valueOf(cbTypeSell.getSelectedIndex()), txtSpec.getText(), chkNew.isSelected(), chkSell.isSelected(), arr.toString()};
            if (editRow == -1) {
                parentModel.addRow(row);
            } else {
                for (int i = 0; i < 9; i++) {
                    parentModel.setValueAt(row[i], editRow, i);
                }
            }
            dialog.dispose();
        };
        btnOk.addActionListener(e -> saveItemAction.run());

        setupGlobalShortcuts(dialog.getRootPane(), saveItemAction);

        dialog.add(pTop, BorderLayout.NORTH);
        dialog.add(pOpt, BorderLayout.CENTER);
        dialog.add(btnOk, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showItemSearchDialog(JDialog parent, SearchCallback callback) {
        JDialog d = new JDialog(parent, "Tìm kiếm Vật Phẩm (Tìm theo ID hoặc Tên)", true);
        d.setSize(900, 600);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(parent);

        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pFilter.setBorder(BorderFactory.createTitledBorder("Bộ Lọc"));

        JTextField txtSearch = new JTextField(20);
        addUndoRedo(txtSearch);
        String[] types = {"- Tất cả Loại -", "0 - Áo", "1 - Quần", "2 - Găng", "3 - Giày", "4 - Rada",
                "5 - Cải trang/Tóc", "6 - Đậu thần", "12 - Ngọc rồng", "27 - Vật phẩm", "29 - Capsule/Bánh", "32 - Giáp tập"};
        JComboBox<String> cbType = new JComboBox<>(types);

        String[] genders = {"- Tất cả Hệ -", "0 - Trái Đất", "1 - Namếc", "2 - Xayda", "3 - Chung/Tất cả"};
        JComboBox<String> cbGender = new JComboBox<>(genders);

        pFilter.add(new JLabel("Tên/ID:"));
        pFilter.add(txtSearch);
        pFilter.add(new JLabel(" | Loại:"));
        pFilter.add(cbType);
        pFilter.add(new JLabel(" | Hệ:"));
        pFilter.add(cbGender);

        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Icon", "Tên Item", "Type", "Gender"}, 0) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 1 ? ImageIcon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (ItemData item : listAllItems) {
            m.addRow(new Object[]{item.id, getItemIcon(item.id), item.name, item.type, item.gender});
        }

        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setShowGrid(true);
        t.setGridColor(GRID_COLOR);

        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(40);
        t.getColumnModel().getColumn(2).setPreferredWidth(350);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        t.setRowSorter(sorter);

        Runnable doFilter = () -> {
            String text = txtSearch.getText().trim();
            int typeIdx = cbType.getSelectedIndex();
            int genderIdx = cbGender.getSelectedIndex();

            java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

            if (!text.isEmpty()) {
                var idFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0);
                var nameFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2);
                filters.add(RowFilter.orFilter(Arrays.asList(idFilter, nameFilter)));
            }

            if (typeIdx > 0) {
                try {
                    int val = Integer.parseInt(cbType.getSelectedItem().toString().split(" - ")[0]);
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 3));
                } catch (Exception e) {
                }
            }

            if (genderIdx > 0) {
                try {
                    int val = Integer.parseInt(cbGender.getSelectedItem().toString().split(" - ")[0]);
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 4));
                } catch (Exception e) {
                }
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                doFilter.run();
            }
        });
        cbType.addActionListener(e -> doFilter.run());
        cbGender.addActionListener(e -> doFilter.run());

        Runnable doSelect = () -> {
            int r = t.getSelectedRow();
            if (r != -1) {
                int modelRow = t.convertRowIndexToModel(r);
                int id = (int) m.getValueAt(modelRow, 0);
                String name = (String) m.getValueAt(modelRow, 2);
                callback.onSelect(id, name);
                d.dispose();
            }
        };

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doSelect.run();
                }
            }
        });

        JButton btnOk = new JButton("CHỌN ITEM NÀY");
        btnOk.setBackground(new Color(40, 167, 69));
        btnOk.setForeground(Color.WHITE);
        btnOk.addActionListener(e -> doSelect.run());

        d.add(pFilter, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(btnOk, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    interface SearchCallback {
        void onSelect(int id, String name);
    }

    private void showBulkSearchDialog(JDialog parent, SearchCallback callback) {
        JDialog d = new JDialog(parent, "Thêm Nhiều Vật Phẩm (Tích chọn để thêm)", true);
        d.setSize(950, 600);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(parent);

        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pFilter.setBorder(BorderFactory.createTitledBorder("Bộ Lọc Tìm Kiếm"));

        JTextField txtSearch = new JTextField(20);
        addUndoRedo(txtSearch);
        String[] types = {"- Tất cả Loại -", "0 - Áo", "1 - Quần", "2 - Găng", "3 - Giày", "4 - Rada",
                "5 - Cải trang/Tóc", "6 - Đậu thần", "12 - Ngọc rồng", "27 - Vật phẩm", "29 - Capsule/Bánh", "32 - Giáp tập"};
        JComboBox<String> cbType = new JComboBox<>(types);

        String[] genders = {"- Tất cả Hệ -", "0 - Trái Đất", "1 - Namếc", "2 - Xayda", "3 - Chung/Tất cả"};
        JComboBox<String> cbGender = new JComboBox<>(genders);

        pFilter.add(new JLabel("Tên/ID:"));
        pFilter.add(txtSearch);
        pFilter.add(new JLabel(" | Loại:"));
        pFilter.add(cbType);
        pFilter.add(new JLabel(" | Hệ:"));
        pFilter.add(cbGender);

        DefaultTableModel m = new DefaultTableModel(new String[]{"Chọn", "ID", "Icon", "Tên Item", "Type", "Gender"}, 0) {
            @Override
            public Class<?> getColumnClass(int c) {
                if (c == 0) return Boolean.class;
                return c == 2 ? ImageIcon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0;
            }
        };

        for (ItemData item : listAllItems) {
            m.addRow(new Object[]{false, item.id, getItemIcon(item.id), item.name, item.type, item.gender});
        }

        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setShowGrid(true);
        t.setGridColor(GRID_COLOR);

        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(50);
        t.getColumnModel().getColumn(2).setPreferredWidth(40);
        t.getColumnModel().getColumn(3).setPreferredWidth(350);
        t.getColumnModel().getColumn(4).setPreferredWidth(50);
        t.getColumnModel().getColumn(5).setPreferredWidth(50);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        t.setRowSorter(sorter);

        Runnable doFilter = () -> {
            String text = txtSearch.getText().trim();
            int typeIdx = cbType.getSelectedIndex();
            int genderIdx = cbGender.getSelectedIndex();

            java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();

            if (!text.isEmpty()) {
                var idFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1);
                var nameFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 3);
                filters.add(RowFilter.orFilter(Arrays.asList(idFilter, nameFilter)));
            }

            if (typeIdx > 0) {
                String str = cbType.getSelectedItem().toString();
                try {
                    int val = Integer.parseInt(str.split(" - ")[0]);
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 4));
                } catch (Exception e) {
                }
            }

            if (genderIdx > 0) {
                String str = cbGender.getSelectedItem().toString();
                try {
                    int val = Integer.parseInt(str.split(" - ")[0]);
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 5));
                } catch (Exception e) {
                }
            }

            if (filters.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.andFilter(filters));
            }
        };

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void removeUpdate(DocumentEvent e) {
                doFilter.run();
            }

            public void changedUpdate(DocumentEvent e) {
                doFilter.run();
            }
        });
        cbType.addActionListener(e -> doFilter.run());
        cbGender.addActionListener(e -> doFilter.run());

        JButton btnSelect = new JButton("Thêm Các Mục Đã Tích (✔)");
        btnSelect.setBackground(new Color(0, 120, 215));
        btnSelect.setForeground(Color.WHITE);
        btnSelect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSelect.addActionListener(e -> {
            int count = 0;
            for (int i = 0; i < m.getRowCount(); i++) {
                Boolean isChecked = (Boolean) m.getValueAt(i, 0);
                if (isChecked != null && isChecked) {
                    int id = (int) m.getValueAt(i, 1);
                    String name = (String) m.getValueAt(i, 3);
                    callback.onSelect(id, name);
                    count++;
                }
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(d, "Chưa tích chọn item nào!");
                return;
            }
            d.dispose();
        });

        d.add(pFilter, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(btnSelect, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void showSearchDialog(JDialog parent, String title, Map<Integer, String> dataMap, SearchCallback callback) {
        JDialog d = new JDialog(parent, title, true);
        d.setSize(600, 500);
        d.setLayout(new BorderLayout());
        d.setLocationRelativeTo(parent);
        JTextField txtSearch = new JTextField();
        txtSearch.setBorder(BorderFactory.createTitledBorder("Nhập ID hoặc Tên để lọc..."));
        addUndoRedo(txtSearch);

        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Icon", "Tên / Mô tả"}, 0) {
            @Override
            public Class<?> getColumnClass(int c) {
                return c == 1 ? ImageIcon.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Map.Entry<Integer, String> entry : dataMap.entrySet()) {
            ImageIcon icon = (dataMap == itemTemplateMap) ? getItemIcon(entry.getKey()) : null;
            m.addRow(new Object[]{entry.getKey(), icon, entry.getValue()});
        }
        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setShowGrid(true);
        t.setGridColor(GRID_COLOR);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        t.setRowSorter(sorter);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                f();
            }

            public void removeUpdate(DocumentEvent e) {
                f();
            }

            public void changedUpdate(DocumentEvent e) {
                f();
            }

            void f() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    var idFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0);
                    var nameFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2);
                    sorter.setRowFilter(RowFilter.orFilter(Arrays.asList(idFilter, nameFilter)));
                }
            }
        });
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = t.getSelectedRow();
                    if (r != -1) {
                        int modelRow = t.convertRowIndexToModel(r);
                        callback.onSelect((int) m.getValueAt(modelRow, 0), (String) m.getValueAt(modelRow, 2));
                        d.dispose();
                    }
                }
            }
        });
        d.add(txtSearch, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.setVisible(true);
    }

    private String getData(JsonObject obj, String key) {
        return obj.has(key) ? obj.get(key).getAsString() : "0";
    }
}