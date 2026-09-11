package nro.server;

import Data.DataGame;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jbcd.ConnectDB;


public class MapTemplatePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    
    // COLOR PALETTE
    private final Color COL_PRIMARY = new Color(0, 120, 215);     
    private final Color COL_HEADER = new Color(225, 230, 235);    
    private final Color COL_SUCCESS = new Color(40, 167, 69);     
    private final Color COL_DANGER = new Color(220, 53, 69);      
    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // Cache Dữ Liệu
    private final Map<Integer, String> mobTemplateMap = new HashMap<>();
    private final Map<Integer, NpcTemplateData> npcTemplateMap = new HashMap<>();
    private final Map<Integer, Integer> headPartIconMap = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();

    // Map tên sự kiện
    private static final Map<Integer, String> EVENT_NAMES = new HashMap<>();
    static {
        EVENT_NAMES.put(1, "HALLOWEEN");
        EVENT_NAMES.put(2, "QUỐC TẾ PHỤ NỮ (8/3)");
        EVENT_NAMES.put(3, "GIÁNG SINH (NOEL)");
        EVENT_NAMES.put(4, "TẾT NGUYÊN ĐÁN");
        EVENT_NAMES.put(5, "TRUNG THU");
        EVENT_NAMES.put(6, "GIỖ TỔ HÙNG VƯƠNG");
        EVENT_NAMES.put(7, "ĐUA TOP NẠP");
        EVENT_NAMES.put(8, "SỰ KIỆN POKEMON");
        EVENT_NAMES.put(9, "NHÀ GIÁO VIỆT NAM (20/11)");
        EVENT_NAMES.put(10, "PHÓ BẢN HẢI TẶC");
    }

    private static class NpcTemplateData {
        String name;
        int headId;
        public NpcTemplateData(String name, int headId) { this.name = name; this.headId = headId; }
    }

    private static class EventEffectData {
        int event_id; int eff_id; int layer; int x; int y; int loop; int delay;
        public EventEffectData(int event_id, int eff_id, int layer, int x, int y, int loop, int delay) {
            this.event_id = event_id; this.eff_id = eff_id; this.layer = layer;
            this.x = x; this.y = y; this.loop = loop; this.delay = delay;
        }
    }

    // --- UNDO/REDO SYSTEM ---
    interface Command {
        void undo();
        void redo();
    }

    // Quản lý lịch sử Undo/Redo cho Table
    private static class HistoryManager {
        private final Stack<Command> undoStack = new Stack<>();
        private final Stack<Command> redoStack = new Stack<>();
        private boolean isWorking = false; // Cờ để tránh loop khi undo kích hoạt listener

        public HistoryManager(JTable table) {
            // Đăng ký phím tắt Ctrl+Z và Ctrl+Y cho bảng
            InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
            ActionMap am = table.getActionMap();

            im.put(KeyStroke.getKeyStroke("control Z"), "Undo");
            im.put(KeyStroke.getKeyStroke("control Y"), "Redo");

            am.put("Undo", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { executeUndo(); } });
            am.put("Redo", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { executeRedo(); } });
        }

        public void addEdit(Command cmd) {
            if (isWorking) return;
            undoStack.push(cmd);
            redoStack.clear();
        }

        public void executeUndo() {
             if (undoStack.isEmpty()) return;
             isWorking = true;
             Command cmd = undoStack.pop();
             cmd.undo();
             redoStack.push(cmd);
             isWorking = false;
        }

        public void executeRedo() {
            if (redoStack.isEmpty()) return;
            isWorking = true;
            Command cmd = redoStack.pop();
            cmd.redo();
            undoStack.push(cmd);
            isWorking = false;
        }

        public boolean isWorking() { return isWorking; }
    }

    // Command: Thêm dòng
    private static class AddRowCmd implements Command {
        private final DefaultTableModel model;
        private final Object[] rowData;
        private final int rowIndex;

        public AddRowCmd(DefaultTableModel model, Object[] rowData, int rowIndex) {
            this.model = model; this.rowData = rowData; this.rowIndex = rowIndex;
        }
        @Override public void undo() { model.removeRow(rowIndex); }
        @Override public void redo() { model.insertRow(rowIndex, rowData); }
    }

    // Command: Xóa dòng
    private static class RemoveRowCmd implements Command {
        private final DefaultTableModel model;
        private final Object[] rowData;
        private final int rowIndex;

        public RemoveRowCmd(DefaultTableModel model, Object[] rowData, int rowIndex) {
            this.model = model; this.rowData = rowData; this.rowIndex = rowIndex;
        }
        @Override public void undo() { model.insertRow(rowIndex, rowData); }
        @Override public void redo() { model.removeRow(rowIndex); }
    }

    // Command: Sửa ô
    private static class CellEditCmd implements Command {
        private final DefaultTableModel model;
        private final Object oldVal, newVal;
        private final int row, col;

        public CellEditCmd(DefaultTableModel model, Object oldVal, Object newVal, int row, int col) {
            this.model = model; this.oldVal = oldVal; this.newVal = newVal; this.row = row; this.col = col;
        }
        @Override public void undo() { model.setValueAt(oldVal, row, col); }
        @Override public void redo() { model.setValueAt(newVal, row, col); }
    }

    // --- END UNDO SYSTEM ---

    public MapTemplatePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        loadCacheData();
        initTopControls();
        initTable();
        loadMaps("");
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    // --- PHẦN 1: LOAD CACHE ---
    private void loadCacheData() {
        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM mob_template")) {
                    while (rs.next()) mobTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                }
                try (ResultSet rs = stmt.executeQuery("SELECT id, data FROM part WHERE type = 0")) {
                    while (rs.next()) {
                        try {
                            JsonArray arr = new JsonParser().parse(rs.getString("data")).getAsJsonArray();
                            if (arr.size() > 0) headPartIconMap.put(rs.getInt("id"), arr.get(0).getAsJsonArray().get(0).getAsInt());
                        } catch (Exception e) { }
                    }
                }
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, head FROM npc_template")) {
                    while (rs.next()) npcTemplateMap.put(rs.getInt("id"), new NpcTemplateData(rs.getString("name"), rs.getInt("head")));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private ImageIcon loadIconRaw(int iconId) {
        if (iconId <= 0) return null;
        if (iconCache.containsKey(iconId)) return iconCache.get(iconId);
        try {
            String[] zoomLevels = {"x4", "x3", "x2", "x1"};
            File f = null;
            for (String zoom : zoomLevels) {
                f = DataGame.getIconFile(iconId);
                if (f.exists()) break;
            }
            if (f != null && f.exists()) {
                Image dimg = ImageIO.read(f).getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(dimg);
                iconCache.put(iconId, icon);
                return icon;
            }
        } catch (Exception e) { }
        return null;
    }

    private ImageIcon getNpcIcon(int npcId) {
        if (npcTemplateMap.containsKey(npcId)) {
            int headId = npcTemplateMap.get(npcId).headId;
            if (headPartIconMap.containsKey(headId)) return loadIconRaw(headPartIconMap.get(headId));
        }
        return null;
    }

    private String getNpcName(int npcId) {
        return npcTemplateMap.containsKey(npcId) ? npcTemplateMap.get(npcId).name : "NPC Lạ " + npcId;
    }

    private String getMobName(int mobId) {
        return mobTemplateMap.getOrDefault(mobId, "Quái Lạ " + mobId);
    }

    // --- PHẦN 2: UI CHÍNH ---
    private void initTopControls() {
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel pSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pSearch.setOpaque(false);
        
        JLabel lblTitle = new JLabel("QUẢN LÝ BẢN ĐỒ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COL_PRIMARY);
        
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên Map hoặc ID...");
        txtSearch.setPreferredSize(new Dimension(250, 40));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createStyledButton("Tìm Kiếm", COL_PRIMARY, Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(120, 40)); 
        btnSearch.addActionListener(e -> loadMaps(txtSearch.getText().trim()));

        JButton btnReload = createStyledButton("Làm Mới Cache", Color.GRAY, Color.WHITE);
        btnReload.setPreferredSize(new Dimension(140, 40));
        btnReload.addActionListener(e -> { loadCacheData(); loadMaps(""); });

        pSearch.add(txtSearch);
        pSearch.add(btnSearch);
        pSearch.add(btnReload);

        top.add(lblTitle, BorderLayout.WEST);
        top.add(pSearch, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"ID", "Tên Bản Đồ", "Hành Tinh", "Khu Vực", "Max Player", "Mobs", "NPCs"};
        
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        table = new JTable(model);
        styleTable(table);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                if (column == 3 || column == 4) {
                    try {
                        int mapId = Integer.parseInt(model.getValueAt(row, 0).toString());
                        Object newVal = model.getValueAt(row, column);
                        quickUpdateMap(mapId, column, newVal);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Giá trị nhập không hợp lệ (Phải là số)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        loadMaps(txtSearch.getText());
                    }
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int col = table.getSelectedColumn();
                    if (row != -1 && col != 3 && col != 4) {
                        openMapEditor(Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString()));
                    }
                }
            }
        });
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(scroll, BorderLayout.CENTER);
    }
    
    private void quickUpdateMap(int mapId, int colIndex, Object newValue) {
        new Thread(() -> {
            String colName = (colIndex == 3) ? "zones" : "max_player";
            String sql = "UPDATE map_template SET " + colName + " = ? WHERE id = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, Integer.parseInt(newValue.toString()));
                ps.setInt(2, mapId);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(FONT_UI);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(COL_HEADER);
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(0, 35));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i != 1) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer); 
        }
    }

    private void loadMaps(String keyword) {
        new Thread(() -> {
            String sql = "SELECT * FROM map_template";
            if (!keyword.isEmpty()) sql += " WHERE NAME LIKE '%" + keyword + "%' OR id LIKE '" + keyword + "%'";
            sql += " ORDER BY id ASC LIMIT 500";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                SwingUtilities.invokeLater(() -> model.setRowCount(0));
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("NAME"));
                    int pid = rs.getInt("planet_id");
                    row.add(pid == 0 ? "Trái Đất" : pid == 1 ? "Namếc" : pid == 2 ? "Xayda" : "Khác");
                    row.add(rs.getInt("zones"));
                    row.add(rs.getInt("max_player"));
                    row.add(countJsonItems(rs.getString("mobs")));
                    row.add(countJsonItems(rs.getString("npcs")));
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private int countJsonItems(String json) {
        try { return new JsonParser().parse(json).getAsJsonArray().size(); } catch (Exception e) { return 0; }
    }

    // --- PHẦN 3: EDITOR (CẬP NHẬT UNDO/REDO) ---
    private void openMapEditor(int mapId) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh Sửa Bản Đồ - ID: " + mapId, true);
        d.setSize(1100, 750);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        d.setBackground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        
        Map<String, JTextComponent> tfMap = new HashMap<>();
        
        // --- CẤU HÌNH MOB MODEL & HISTORY ---
        JTable mobTable = new JTable(); 
        styleTable(mobTable);
        
        final HistoryManager[] mobHistoryRef = new HistoryManager[1];
        DefaultTableModel mobModel = new DefaultTableModel(new String[]{"ID Mob", "Tên Mob", "Level", "HP", "X", "Y", "JSON Gốc"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 0 || column == 2 || column == 3 || column == 4 || column == 5; }
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                Object oldVal = getValueAt(row, column);
                super.setValueAt(aValue, row, column);
                if (mobHistoryRef[0] != null && !mobHistoryRef[0].isWorking()) {
                    mobHistoryRef[0].addEdit(new CellEditCmd(this, oldVal, aValue, row, column));
                }
            }
        };
        mobTable.setModel(mobModel);
        mobTable.getColumnModel().getColumn(6).setMinWidth(0); 
        mobTable.getColumnModel().getColumn(6).setMaxWidth(0);
        mobHistoryRef[0] = new HistoryManager(mobTable);

        // --- CẤU HÌNH NPC MODEL & HISTORY ---
        JTable npcTable = new JTable(); 
        styleTable(npcTable);
        npcTable.setRowHeight(40);
        
        final HistoryManager[] npcHistoryRef = new HistoryManager[1];
        DefaultTableModel npcModel = new DefaultTableModel(new String[]{"ID NPC", "Hình Ảnh", "Tên NPC", "X", "Y"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
            @Override public boolean isCellEditable(int row, int column) { return column == 0 || column == 3 || column == 4; }
            @Override public void setValueAt(Object aValue, int row, int column) {
                Object oldVal = getValueAt(row, column);
                super.setValueAt(aValue, row, column);
                if (npcHistoryRef[0] != null && !npcHistoryRef[0].isWorking()) {
                    npcHistoryRef[0].addEdit(new CellEditCmd(this, oldVal, aValue, row, column));
                }
            }
        };
        npcTable.setModel(npcModel);
        npcHistoryRef[0] = new HistoryManager(npcTable);

        // --- LOGIC AUTO UPDATE ---
        mobModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                if (mobHistoryRef[0].isWorking()) return;
                int row = e.getFirstRow();
                try {
                    int newId = Integer.parseInt(mobModel.getValueAt(row, 0).toString());
                    String newName = getMobName(newId);
                    SwingUtilities.invokeLater(() -> mobModel.setValueAt(newName, row, 1));
                } catch (Exception ex) {}
            }
        });

        npcModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                if (npcHistoryRef[0].isWorking()) return;
                int row = e.getFirstRow();
                try {
                    int newId = Integer.parseInt(npcModel.getValueAt(row, 0).toString());
                    String newName = getNpcName(newId);
                    ImageIcon newIcon = getNpcIcon(newId);
                    SwingUtilities.invokeLater(() -> {
                        npcModel.setValueAt(newIcon, row, 1);
                        npcModel.setValueAt(newName, row, 2);
                    });
                } catch (Exception ex) {}
            }
        });
        
        List<EventEffectData> allEventEffects = new ArrayList<>();

        // Load Data
     // Load Data
new Thread(() -> {
            try (Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM map_template WHERE id=" + mapId)) {

                if (rs.next()) {
                    final String fName = rs.getString("NAME");
                    final String fType = rs.getString("type");
                    final String fPlanet = rs.getString("planet_id");
                    final String fZones = rs.getString("zones");
                    final String fMax = rs.getString("max_player");
                    final String fBg = rs.getString("bg_id");
                    final String fTile = rs.getString("tile_id");
                    final String fWp = rs.getString("waypoints");
                    final String fMobs = rs.getString("mobs");
                    final String fNpcs = rs.getString("npcs");

                    SwingUtilities.invokeLater(() -> {
                        // 1. Tab Thông tin
                        JPanel pInfoWrapper = new JPanel(new BorderLayout());
                        pInfoWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
                        JPanel pInfo = new JPanel(new GridLayout(0, 4, 15, 15));

                        addTF(pInfo, "Tên Map", fName, tfMap);
                        addTF(pInfo, "Loại Map", fType, tfMap);
                        addTF(pInfo, "ID Hành Tinh", fPlanet, tfMap);
                        addTF(pInfo, "Số Khu Vực", fZones, tfMap);
                        addTF(pInfo, "Max Player", fMax, tfMap);
                        addTF(pInfo, "ID Nền (BG)", fBg, tfMap);
                        addTF(pInfo, "ID Địa Hình", fTile, tfMap);

                        JPanel pLongFields = new JPanel(new GridLayout(0, 1, 10, 10));
                        pLongFields.setBorder(new EmptyBorder(10, 0, 0, 0));
                        addArea(pLongFields, "Lối Đi (Waypoints JSON)", fWp, tfMap);

                        pInfoWrapper.add(pInfo, BorderLayout.NORTH);
                        pInfoWrapper.add(pLongFields, BorderLayout.CENTER);
                        tabs.addTab("Thông Tin Chung", new JScrollPane(pInfoWrapper));

                        // 2. Tab Mobs
                        try {
                            JsonArray arr = new JsonParser().parse(fMobs).getAsJsonArray();
                            for (JsonElement e : arr) {
                                String innerJson = e.getAsString();
                                JsonArray mData = new JsonParser().parse(innerJson).getAsJsonArray();
                                int mid = mData.get(0).getAsInt();
                                mobModel.addRow(new Object[]{
                                    mid, getMobName(mid),
                                    mData.get(1).getAsInt(),
                                    mData.get(2).getAsInt(),
                                    mData.get(3).getAsInt(),
                                    mData.get(4).getAsInt(),
                                    innerJson
                                });
                            }
                        } catch (Exception ex) {
                        }
                        tabs.addTab("Quái Vật (Mobs)", createMobPanel(mobTable, mobModel, d, mobHistoryRef[0]));

                        // 3. Tab NPCs
                        try {
                            JsonArray arr = new JsonParser().parse(fNpcs).getAsJsonArray();
                            for (JsonElement e : arr) {
                                JsonArray nData = e.getAsJsonArray();
                                int nid = nData.get(0).getAsInt();
                                npcModel.addRow(new Object[]{nid, getNpcIcon(nid), getNpcName(nid),
                                    nData.get(1).getAsInt(), nData.get(2).getAsInt()});
                            }
                        } catch (Exception ex) {
                        }
                        tabs.addTab("Danh Sách NPC", createNpcPanel(npcTable, npcModel, d, npcHistoryRef[0]));

                        // BỎ tab "Hiệu Ứng Sự Kiện"
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        d.add(tabs, BorderLayout.CENTER);

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton btnSave = createStyledButton("LƯU CẤU HÌNH MAP", COL_SUCCESS, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(200, 50));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSave.addActionListener(e -> saveMap(mapId, tfMap, mobModel, npcModel, allEventEffects, d));
        
        JButton btnCancel = createStyledButton("Đóng", Color.GRAY, Color.WHITE);
        btnCancel.setPreferredSize(new Dimension(120, 50));
        btnCancel.addActionListener(e -> d.dispose());
        
        pBtn.add(btnCancel);
        pBtn.add(btnSave);
        d.add(pBtn, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- PANEL HIỆU ỨNG SỰ KIỆN ---
    private JPanel createEventEffectPanel(List<EventEffectData> allData, JDialog parent) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Chọn Sự Kiện: "));
        JComboBox<String> cbEvent = new JComboBox<>();
        cbEvent.setFont(FONT_UI);
        EVENT_NAMES.forEach((k, v) -> cbEvent.addItem(k + " - " + v));
        topBar.add(cbEvent);
        panel.add(topBar, BorderLayout.NORTH);

        DefaultTableModel evModel = new DefaultTableModel(new String[]{"Eff ID", "Layer", "X", "Y", "Loop", "Delay"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tableEv = new JTable(evModel);
        styleTable(tableEv);
        panel.add(new JScrollPane(tableEv), BorderLayout.CENTER);

        Runnable updateTable = () -> {
            evModel.setRowCount(0);
            if (cbEvent.getSelectedItem() == null) return;
            int selectedEvId = Integer.parseInt(cbEvent.getSelectedItem().toString().split(" - ")[0]);
            List<EventEffectData> filtered = allData.stream().filter(ef -> ef.event_id == selectedEvId).collect(Collectors.toList());
            for (EventEffectData ef : filtered) evModel.addRow(new Object[]{ef.eff_id, ef.layer, ef.x, ef.y, ef.loop, ef.delay});
        };

        cbEvent.addActionListener(e -> updateTable.run());
        if (cbEvent.getItemCount() > 0) cbEvent.setSelectedIndex(0);

        JPanel botBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = createStyledButton("Thêm", COL_PRIMARY, Color.WHITE);
        JButton btnEdit = createStyledButton("Sửa", Color.ORANGE, Color.WHITE);
        JButton btnDel = createStyledButton("Xóa", COL_DANGER, Color.WHITE);

        btnAdd.addActionListener(e -> {
            if (cbEvent.getSelectedItem() == null) return;
            int evId = Integer.parseInt(cbEvent.getSelectedItem().toString().split(" - ")[0]);
            openEventEditDialog(parent, evId, null, (newData) -> { 
                allData.add(newData); 
                updateTable.run(); 
            });
        });

        Runnable doEdit = () -> {
            int r = tableEv.getSelectedRow();
            if (r == -1) return;
            int evId = Integer.parseInt(cbEvent.getSelectedItem().toString().split(" - ")[0]);
            int effId = Integer.parseInt(evModel.getValueAt(r, 0).toString());
            int x = Integer.parseInt(evModel.getValueAt(r, 2).toString());
            int y = Integer.parseInt(evModel.getValueAt(r, 3).toString());
            EventEffectData target = allData.stream().filter(ef -> ef.event_id == evId && ef.eff_id == effId && ef.x == x && ef.y == y).findFirst().orElse(null);
            if (target != null) openEventEditDialog(parent, evId, target, (newData) -> {
                target.eff_id = newData.eff_id; target.layer = newData.layer; target.x = newData.x;
                target.y = newData.y; target.loop = newData.loop; target.delay = newData.delay;
                updateTable.run();
            });
        };

        btnEdit.addActionListener(e -> doEdit.run());
        btnDel.addActionListener(e -> {
            int r = tableEv.getSelectedRow();
            if (r != -1) {
                int evId = Integer.parseInt(cbEvent.getSelectedItem().toString().split(" - ")[0]);
                int effId = Integer.parseInt(evModel.getValueAt(r, 0).toString());
                int x = Integer.parseInt(evModel.getValueAt(r, 2).toString());
                int y = Integer.parseInt(evModel.getValueAt(r, 3).toString());
                allData.removeIf(ef -> ef.event_id == evId && ef.eff_id == effId && ef.x == x && ef.y == y);
                updateTable.run();
            }
        });
        tableEv.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) doEdit.run(); } });

        botBar.add(btnAdd); botBar.add(btnEdit); botBar.add(btnDel);
        panel.add(botBar, BorderLayout.SOUTH);
        return panel;
    }

    private interface OnSaveEffect { void onSave(EventEffectData data); }

    private void openEventEditDialog(JDialog parent, int eventId, EventEffectData oldData, OnSaveEffect callback) {
        JDialog d = new JDialog(parent, oldData == null ? "Thêm Hiệu Ứng" : "Sửa Hiệu Ứng", true);
        d.setSize(300, 350); d.setLocationRelativeTo(parent);
        d.setLayout(new GridLayout(7, 2, 5, 5));
        JTextField tfEffId = new JTextField(oldData == null ? "0" : oldData.eff_id + "");
        JTextField tfLayer = new JTextField(oldData == null ? "2" : oldData.layer + "");
        JTextField tfX = new JTextField(oldData == null ? "0" : oldData.x + "");
        JTextField tfY = new JTextField(oldData == null ? "0" : oldData.y + "");
        JTextField tfLoop = new JTextField(oldData == null ? "0" : oldData.loop + "");
        JTextField tfDelay = new JTextField(oldData == null ? "1" : oldData.delay + "");

        d.add(new JLabel("Effect ID:")); d.add(tfEffId);
        d.add(new JLabel("Layer:")); d.add(tfLayer);
        d.add(new JLabel("X:")); d.add(tfX);
        d.add(new JLabel("Y:")); d.add(tfY);
        d.add(new JLabel("Loop:")); d.add(tfLoop);
        d.add(new JLabel("Delay:")); d.add(tfDelay);

        JButton btnOk = createStyledButton("OK", COL_PRIMARY, Color.WHITE);
        btnOk.addActionListener(e -> {
            try {
                callback.onSave(new EventEffectData(eventId, Integer.parseInt(tfEffId.getText()), Integer.parseInt(tfLayer.getText()),
                        Integer.parseInt(tfX.getText()), Integer.parseInt(tfY.getText()), Integer.parseInt(tfLoop.getText()), Integer.parseInt(tfDelay.getText())));
                d.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(d, "Nhập sai số!"); }
        });
        d.add(new JLabel("")); d.add(btnOk);
        d.setVisible(true);
    }

    // --- PANEL MOBS & NPCs ---
    private JPanel createMobPanel(JTable t, DefaultTableModel model, JDialog parent, HistoryManager history) {
        JPanel p = new JPanel(new BorderLayout(5, 5)); p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = createStyledButton("Thêm Quái", COL_PRIMARY, Color.WHITE);
        JButton btnDel = createStyledButton("Xóa Quái", COL_DANGER, Color.WHITE);
        
        btnAdd.addActionListener(e -> openMobSelector(parent, model, history));
        btnDel.addActionListener(e -> { 
            int r = t.getSelectedRow(); 
            if (r != -1) {
                // Add to history before removing
                Object[] rowData = new Object[model.getColumnCount()];
                for (int i=0; i<model.getColumnCount(); i++) rowData[i] = model.getValueAt(r, i);
                history.addEdit(new RemoveRowCmd(model, rowData, r));
                model.removeRow(r); 
            }
        });
        
        tools.add(btnAdd); tools.add(btnDel);
        JLabel lblHint = new JLabel(" (Ctrl+Z: Undo, Ctrl+Y: Redo)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        tools.add(lblHint);

        p.add(tools, BorderLayout.NORTH); p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void openMobSelector(JDialog parent, DefaultTableModel model, HistoryManager history) {
        JDialog d = new JDialog(parent, "Chọn Quái Vật", true);
        d.setSize(450, 600); d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());

        JPanel pTop = new JPanel(new BorderLayout(5, 5));
        pTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField tf = new JTextField(); 
        tf.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc ID để tìm...");
        tf.setPreferredSize(new Dimension(200, 35));
        pTop.add(new JLabel("Tìm kiếm: "), BorderLayout.WEST); pTop.add(tf, BorderLayout.CENTER);
        d.add(pTop, BorderLayout.NORTH);

        DefaultTableModel sm = new DefaultTableModel(new String[]{"ID", "Tên Quái Vật"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        mobTemplateMap.forEach((k, v) -> sm.addRow(new Object[]{k, v}));
        JTable st = new JTable(sm);
        styleTable(st);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(sm);
        st.setRowSorter(sorter);

        tf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); } public void removeUpdate(DocumentEvent e) { f(); } public void changedUpdate(DocumentEvent e) { f(); }
            void f() { 
                String t = tf.getText().trim();
                if (t.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + t)); 
            }
        });

        JButton btnSelect = createStyledButton("CHỌN (hoặc click đúp)", COL_SUCCESS, Color.WHITE);
        btnSelect.setPreferredSize(new Dimension(200, 40));

        Runnable doSelect = () -> {
            int r = st.getSelectedRow();
            if (r != -1) {
                int id = (int) st.getValueAt(r, 0);
                String n = (String) st.getValueAt(r, 1);
                Object[] data = new Object[]{id, n, 1, 1000, 500, 300, "[" + id + ",1,1000,500,300]"};
                model.addRow(data);
                history.addEdit(new AddRowCmd(model, data, model.getRowCount()-1));
                d.dispose();
            }
        };

        st.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) doSelect.run(); } });
        btnSelect.addActionListener(e -> doSelect.run());

        d.add(new JScrollPane(st), BorderLayout.CENTER);
        JPanel pBot = new JPanel(); pBot.setBorder(new EmptyBorder(5, 5, 5, 5)); pBot.add(btnSelect);
        d.add(pBot, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private JPanel createNpcPanel(JTable t, DefaultTableModel model, JDialog parent, HistoryManager history) {
        JPanel p = new JPanel(new BorderLayout(5, 5)); p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = createStyledButton("Thêm NPC", COL_PRIMARY, Color.WHITE);
        JButton btnDel = createStyledButton("Xóa NPC", COL_DANGER, Color.WHITE);
        
        btnAdd.addActionListener(e -> openNpcSelector(parent, model, history));
        btnDel.addActionListener(e -> { 
            int r = t.getSelectedRow(); 
            if (r != -1) {
                Object[] rowData = new Object[model.getColumnCount()];
                for (int i=0; i<model.getColumnCount(); i++) rowData[i] = model.getValueAt(r, i);
                history.addEdit(new RemoveRowCmd(model, rowData, r));
                model.removeRow(r); 
            }
        });
        
        tools.add(btnAdd); tools.add(btnDel);
        JLabel lblHint = new JLabel(" (Ctrl+Z: Undo, Ctrl+Y: Redo)");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(Color.GRAY);
        tools.add(lblHint);

        p.add(tools, BorderLayout.NORTH); p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void openNpcSelector(JDialog parent, DefaultTableModel model, HistoryManager history) {
        JDialog d = new JDialog(parent, "Chọn NPC", true);
        d.setSize(500, 600); d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());

        JPanel pTop = new JPanel(new BorderLayout(5, 5));
        pTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTextField tf = new JTextField(); 
        tf.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc ID...");
        tf.setPreferredSize(new Dimension(200, 35));
        pTop.add(new JLabel("Tìm kiếm: "), BorderLayout.WEST); pTop.add(tf, BorderLayout.CENTER);
        d.add(pTop, BorderLayout.NORTH);

        DefaultTableModel sm = new DefaultTableModel(new String[]{"ID", "Icon", "Tên NPC"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        npcTemplateMap.forEach((k, v) -> sm.addRow(new Object[]{k, getNpcIcon(k), v.name}));
        JTable st = new JTable(sm);
        styleTable(st); st.setRowHeight(45);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(sm);
        st.setRowSorter(sorter);

        tf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); } public void removeUpdate(DocumentEvent e) { f(); } public void changedUpdate(DocumentEvent e) { f(); }
            void f() { 
                String t = tf.getText().trim();
                if (t.length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + t)); 
            }
        });

        JButton btnSelect = createStyledButton("CHỌN (hoặc click đúp)", COL_SUCCESS, Color.WHITE);
        btnSelect.setPreferredSize(new Dimension(200, 40));

        Runnable doSelect = () -> {
            int r = st.getSelectedRow();
            if (r != -1) {
                int id = (int) st.getValueAt(r, 0);
                String n = (String) st.getValueAt(r, 2);
                ImageIcon i = (ImageIcon) st.getValueAt(r, 1);
                Object[] data = new Object[]{id, i, n, 500, 300};
                model.addRow(data);
                history.addEdit(new AddRowCmd(model, data, model.getRowCount()-1));
                d.dispose();
            }
        };

        st.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) doSelect.run(); } });
        btnSelect.addActionListener(e -> doSelect.run());

        d.add(new JScrollPane(st), BorderLayout.CENTER);
        JPanel pBot = new JPanel(); pBot.setBorder(new EmptyBorder(5, 5, 5, 5)); pBot.add(btnSelect);
        d.add(pBot, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- LƯU DỮ LIỆU ---
    private void saveMap(int mapId, Map<String, JTextComponent> tfMap, DefaultTableModel mobModel, DefaultTableModel npcModel, List<EventEffectData> effEvents, JDialog d) {
        try (Connection conn = getConnection()) {
            JsonArray mobsArr = new JsonArray();
            for (int i = 0; i < mobModel.getRowCount(); i++) {
                JsonArray mInfo = new JsonArray();
                mInfo.add(Integer.parseInt(mobModel.getValueAt(i, 0).toString()));
                mInfo.add(Integer.parseInt(mobModel.getValueAt(i, 2).toString()));
                mInfo.add(Integer.parseInt(mobModel.getValueAt(i, 3).toString()));
                mInfo.add(Integer.parseInt(mobModel.getValueAt(i, 4).toString()));
                mInfo.add(Integer.parseInt(mobModel.getValueAt(i, 5).toString()));
                mobsArr.add(mInfo.toString());
            }
            JsonArray npcsArr = new JsonArray();
            for (int i = 0; i < npcModel.getRowCount(); i++) {
                JsonArray nInfo = new JsonArray();
                nInfo.add(Integer.parseInt(npcModel.getValueAt(i, 0).toString()));
                nInfo.add(Integer.parseInt(npcModel.getValueAt(i, 3).toString()));
                nInfo.add(Integer.parseInt(npcModel.getValueAt(i, 4).toString()));
                npcsArr.add(nInfo);
            }
            String effEventJson = new Gson().toJson(effEvents);

            String sql = "UPDATE map_template SET NAME=?, type=?, planet_id=?, zones=?, max_player=?, bg_id=?, tile_id=?, waypoints=?, mobs=?, npcs=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, tfMap.get("Tên Map").getText());
                ps.setInt(2, Integer.parseInt(tfMap.get("Loại Map").getText()));
                ps.setInt(3, Integer.parseInt(tfMap.get("ID Hành Tinh").getText()));
                ps.setInt(4, Integer.parseInt(tfMap.get("Số Khu Vực").getText()));
                ps.setInt(5, Integer.parseInt(tfMap.get("Max Player").getText()));
                ps.setInt(6, Integer.parseInt(tfMap.get("ID Nền (BG)").getText()));
                ps.setInt(7, Integer.parseInt(tfMap.get("ID Địa Hình").getText()));
                ps.setString(8, tfMap.get("Lối Đi (Waypoints JSON)").getText());
                ps.setString(9, mobsArr.toString());
                ps.setString(10, npcsArr.toString());
                ps.setInt(11, mapId);

                ps.executeUpdate();
                JOptionPane.showMessageDialog(d, "Lưu bản đồ thành công!");
                d.dispose();
                loadMaps("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(d, "Lỗi khi lưu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTF(JPanel p, String label, String val, Map<String, JTextComponent> map) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_UI);
        JTextField tf = new JTextField(val);
        tf.setFont(FONT_UI);
        fieldPanel.add(lbl, BorderLayout.NORTH);
        fieldPanel.add(tf, BorderLayout.CENTER);
        p.add(fieldPanel);
        map.put(label, tf);
    }
    
    private void addArea(JPanel p, String label, String val, Map<String, JTextComponent> map) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_UI);
        JTextArea ta = new JTextArea(val);
        ta.setFont(FONT_UI);
        ta.setRows(5); 
        ta.setLineWrap(true); 
        ta.setWrapStyleWord(true); 
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fieldPanel.add(lbl, BorderLayout.NORTH);
        fieldPanel.add(scroll, BorderLayout.CENTER);
        p.add(fieldPanel);
        map.put(label, ta);
    }

    private static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        b.setBorder(new LineBorder(bg.darker(), 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}