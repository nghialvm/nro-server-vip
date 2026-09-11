package nro.server;

import Data.DataGame;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;
import jbcd.ConnectDB;


public class PlayersPanel extends JPanel {

    private static final String SERVER_IP = "127.0.0.1"; 
    private static final int SERVER_PORT = 14445; 

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnQuickSave;
    
    private final Map<Integer, String> itemTemplateMap = new HashMap<>();
    private final Map<Integer, Integer> itemIconMap = new HashMap<>();
    private final Map<Integer, String> clanNameMap = new HashMap<>();
    private final Map<Integer, String> optionTemplateMap = new HashMap<>();
    
    private final Map<Integer, BadgeTemplate> badgeTemplateMap = new HashMap<>();

    private final Map<Integer, String> taskMainTemplateMap = new HashMap<>();
    private final Map<Integer, String> taskMainDetailMap = new HashMap<>();
    private final Map<Integer, List<SubTaskTemplate>> taskSubTemplateMap = new HashMap<>();
    private final Map<Integer, String> sideTaskTemplateMap = new HashMap<>();
    private final Map<Integer, String> clanTaskTemplateMap = new HashMap<>();
    private final Map<Integer, String> kolTaskTemplateMap = new HashMap<>();
    
    private final Map<Integer, String> inventoryCache = new HashMap<>();
    private final Set<Integer> modifiedPlayerIds = new HashSet<>();
    
    private final List<ItemData> listAllItems = new ArrayList<>();

    private static class ItemData {
        int id; String name; int type; int gender;
        public ItemData(int id, String name, int type, int gender) {
            this.id = id; this.name = name; this.type = type; this.gender = gender;
        }
    }
    
    private static class BadgeTemplate {
        int id;
        String name;
        String optionsJson;
        int iconId;
        
        public BadgeTemplate(int id, String name, String optionsJson, int iconId) {
            this.id = id;
            this.name = name;
            this.optionsJson = optionsJson;
            this.iconId = iconId;
        }
    }
    
    private static class SubTaskTemplate {
        String name;
        short maxCount;
        String notify;
        byte npcId;
        short mapId;
    }
    
    private final Map<Integer, Integer> partHeadIconMap = new HashMap<>(); 
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();
    private final Map<Integer, Boolean> noIconCache = new HashMap<>();
    private final Map<Integer, ImageIcon> headCache = new HashMap<>();
    private final Map<Integer, ImageIcon> rawIconCache = new HashMap<>();

    private final Color COLOR_PRIMARY = new Color(0, 120, 215);
    private final Color COLOR_SUCCESS = new Color(40, 167, 69);
    private final Color COLOR_INFO = new Color(23, 162, 184);
    private final Color COLOR_BG_HEADER = new Color(230, 240, 255);
    private final Color COLOR_ALT_ROW = new Color(245, 245, 245);
    private final Color COLOR_EDITABLE = new Color(0, 50, 150);
    private final Color COLOR_GRID = new Color(220, 220, 220);

    public PlayersPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initStaticData();
        loadCacheData();
        loadPartsHead(); 

        initTopControls();
        initTable();
        setupGlobalShortcuts();
    }

    private void setupGlobalShortcuts() {
        InputMap im = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = this.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "quickSave");
        am.put("quickSave", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnQuickSave.isEnabled()) {
                    saveModifiedRows();
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

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    private void sendCommandToServer(String command) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                InetAddress address = InetAddress.getByName(SERVER_IP);
                byte[] buffer = command.getBytes("UTF-8");
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVER_PORT);
                socket.send(packet);
                System.out.println("Đã gửi lệnh đến server: " + command);
            } catch (Exception e) {
                System.err.println("Lỗi gửi lệnh đến server: " + e.getMessage());
            }
        }).start();
    }

    private void loadCacheData() {
        new Thread(() -> {
            listAllItems.clear();
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, icon_id, type, gender FROM item_template")) {
                    while (rs.next()) {
                        listAllItems.add(new ItemData(rs.getInt("id"), rs.getString("name"), rs.getInt("type"), rs.getInt("gender")));
                        itemTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                        itemIconMap.put(rs.getInt("id"), rs.getInt("icon_id"));
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM clan")) {
                    while (rs.next()) clanNameMap.put(rs.getInt("id"), rs.getString("name"));
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM item_option_template")) {
                    while (rs.next()) optionTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT b.id, b.idEffect, b.name, b.Options, i.icon_id FROM data_badges b LEFT JOIN item_template i ON b.iditem = i.id")) {
                    while (rs.next()) {
                        int idKey = rs.getInt("idEffect"); 
                        
                        String name = rs.getString("name");
                        String options = rs.getString("Options");
                        int iconId = rs.getInt("icon_id");
                        
                        badgeTemplateMap.put(idKey, new BadgeTemplate(idKey, name, options, iconId));
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, detail FROM task_main_template")) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        taskMainTemplateMap.put(id, rs.getString("name"));
                        taskMainDetailMap.put(id, rs.getString("detail"));
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT task_main_id, name, max_count, notify, npc_id, map FROM task_sub_template ORDER BY task_main_id")) {
                    while (rs.next()) {
                        int taskMainId = rs.getInt("task_main_id");
                        SubTaskTemplate subTask = new SubTaskTemplate();
                        subTask.name = rs.getString("name");
                        subTask.maxCount = rs.getShort("max_count");
                        subTask.notify = rs.getString("notify");
                        subTask.npcId = rs.getByte("npc_id");
                        subTask.mapId = rs.getShort("map");
                        
                        taskSubTemplateMap.computeIfAbsent(taskMainId, k -> new ArrayList<>()).add(subTask);
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM side_task_template")) {
                    while (rs.next()) {
                        sideTaskTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM clan_task_template")) {
                    while (rs.next()) {
                        clanTaskTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                    }
                }
                
                try (ResultSet rs = stmt.executeQuery("SELECT id, info FROM task_kol_template")) {
                    while (rs.next()) {
                        kolTaskTemplateMap.put(rs.getInt("id"), rs.getString("info"));
                    }
                }
                
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
    
    private void loadPartsHead() {
        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, data FROM part WHERE type = 0")) {
                while (rs.next()) {
                    try {
                        JsonArray arr = new JsonParser().parse(rs.getString("data")).getAsJsonArray();
                        if (arr.size() > 0) partHeadIconMap.put(rs.getInt("id"), arr.get(0).getAsJsonArray().get(0).getAsInt());
                    } catch (Exception ignored) {}
                }
            } catch (SQLException ignored) {}
            SwingUtilities.invokeLater(() -> loadPlayersFromDB(""));
        }).start();
    }
    
    private ImageIcon drawHeadIcon(int headPartId) {
        if (headPartId <= 0) return null;
        if (headCache.containsKey(headPartId)) return headCache.get(headPartId);
        Integer iconId = partHeadIconMap.get(headPartId);
        if (iconId != null) {
            try {
                String[] zoomLevels = {"x4", "x3", "x2", "x1"};
                for (String zoom : zoomLevels) {
                    File f = DataGame.getIconFile(iconId);
                    if (f.exists()) {
                        Image dimg = ImageIO.read(f).getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                        ImageIcon icon = new ImageIcon(dimg);
                        headCache.put(headPartId, icon);
                        return icon;
                    }
                }
            } catch (Exception e) {}
        }
        return null;
    }

    private ImageIcon getItemIcon(int itemId) {
        if (iconCache.containsKey(itemId)) return iconCache.get(itemId);
        if (noIconCache.containsKey(itemId)) return null;
        try {
            int iconId = itemIconMap.getOrDefault(itemId, -1);
            if (iconId == -1) { noIconCache.put(itemId, true); return null; }
            return loadIconRaw(iconId);
        } catch (Exception e) { }
        noIconCache.put(itemId, true);
        return null;
    }
    
    private ImageIcon loadIconRaw(int iconId) {
        if (iconId <= 0) return null;
        if (rawIconCache.containsKey(iconId)) return rawIconCache.get(iconId);
        try {
            String[] zoomLevels = {"x4", "x3", "x2", "x1"};
            File f = null;
            for (String zoom : zoomLevels) {
                f = DataGame.getIconFile(iconId);
                if (f.exists()) break;
            }
            if (f != null && f.exists()) {
                Image dimg = ImageIO.read(f).getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(dimg);
                rawIconCache.put(iconId, icon);
                return icon;
            }
        } catch (Exception e) { }
        return null;
    }

    private void initStaticData() {
        String raw = "0,Tấn công +#;50,Sức đánh +#%;77,HP +#%;103,KI +#%;14,Chí mạng +#%;30,Khóa giao dịch;93,Hạn sử dụng # ngày;73,Không thể bán;9,Hiệu lực # phút";
        for (String s : raw.split(";")) {
            String[] p = s.split(",");
            if(p.length==2) optionTemplateMap.put(Integer.parseInt(p[0]), p[1]);
        }
    }

    private String getItemName(int id) { return itemTemplateMap.getOrDefault(id, "Unknown [" + id + "]"); }
    private String getClanName(int id) { return id == -1 ? "Không có" : clanNameMap.getOrDefault(id, "Clan [" + id + "]"); }
    private String getOptionName(int id) { return optionTemplateMap.getOrDefault(id, "Option " + id); }
    private String formatOption(int id, int param) { return getOptionName(id).replace("#", String.valueOf(param)); }

    private void initTopControls() {
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));
        JPanel searchP = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchP.setOpaque(false);
        
        txtSearch = new JTextField(25);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên nhân vật để tìm...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) loadPlayersFromDB(txtSearch.getText().trim()); }
        });
        addUndoRedo(txtSearch);
        
        JButton btnSearch = createStyledButton("Tìm kiếm", COLOR_PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> loadPlayersFromDB(txtSearch.getText().trim()));
        
        btnQuickSave = createStyledButton("Lưu thay đổi (Ctrl+S)", Color.GRAY, Color.WHITE);
        btnQuickSave.setEnabled(false);
        btnQuickSave.addActionListener(e -> saveModifiedRows());

        JButton btnReload = createStyledButton("Tải lại DB", new Color(100, 100, 100), Color.WHITE);
        btnReload.addActionListener(e -> { loadPartsHead(); });

        JButton btnGuide = createStyledButton("Hướng dẫn", COLOR_INFO, Color.WHITE);
        btnGuide.addActionListener(e -> showGuide());
        
        JButton btnCheckInventory = createStyledButton("Kiểm tra hành trang", new Color(139, 0, 139), Color.WHITE);
        btnCheckInventory.addActionListener(e -> openCheckInventoryDialog());
        
        JButton btnRevokeItem = createStyledButton("Thu hồi vật phẩm", new Color(178, 34, 34), Color.WHITE);
        btnRevokeItem.addActionListener(e -> openRevokeItemDialog());

        searchP.add(txtSearch); searchP.add(btnSearch); searchP.add(btnQuickSave); searchP.add(btnReload); searchP.add(btnGuide);
        searchP.add(btnCheckInventory); searchP.add(btnRevokeItem);
        top.add(searchP, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);
    }

    private void showGuide() {
        String html = "<html><body style='width: 300px'>"
                + "<h3>Hướng dẫn quản lý Người chơi</h3>"
                + "<ul>"
                + "<li><b>Sửa nhanh:</b> Click đúp vào các cột <font color='blue'>Vàng, Ngọc</font> để sửa trực tiếp trên bảng.</li>"
                + "<li><b>Chi tiết:</b> Click đúp vào các cột còn lại (Tên, ID...) để mở cửa sổ chỉnh sửa đầy đủ (Item, Đệ tử...).</li>"
                + "<li><b>Lưu:</b> Sau khi sửa nhanh trên bảng, nhấn nút <b>'Lưu thay đổi'</b> hoặc phím tắt <b>Ctrl + S</b>.</li>"
                + "<li><b>Tác động trực tiếp:</b> Thay đổi sẽ ảnh hưởng ngay lập tức đến server, kể cả khi người chơi đang online.</li>"
                + "<li><b>Tiện ích:</b>"
                + "<ul>"
                + "<li><b>Ctrl + Z:</b> Hoàn tác (Undo) khi nhập liệu text.</li>"
                + "<li><b>Ctrl + Y:</b> Làm lại (Redo) khi nhập liệu text.</li>"
                + "<li><b>Chuột phải:</b> Mở menu chức năng phụ (Buff item...).</li>"
                + "</ul></li>"
                + "</ul>"
                + "<p><i>Lưu ý: Cột 'Tình trạng' màu đỏ nghĩa là tài khoản đang bị khóa (Ban).</i></p>"
                + "</body></html>";
        JOptionPane.showMessageDialog(this, new JLabel(html), "Hướng dẫn sử dụng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void initTable() {
        String[] cols = { "ID", "Head", "Tên nhân vật", "Sức Mạnh", "Clan", "Vàng", "Ngọc", "Thỏi Vàng", "Trạng thái", "Tình trạng" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // Chỉ có thể sửa Vàng và Ngọc
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class; 
                if (columnIndex == 0 || columnIndex == 7) return Long.class;
                return super.getColumnClass(columnIndex);
            }
        };
        
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && col >= 0) {
                    try {
                        int playerId = Integer.parseInt(model.getValueAt(row, 0).toString());
                        modifiedPlayerIds.add(playerId);
                        btnQuickSave.setEnabled(true);
                        btnQuickSave.setBackground(new Color(255, 69, 0));
                        btnQuickSave.setText("Lưu thay đổi (" + modifiedPlayerIds.size() + ")");
                    } catch (Exception ex) {}
                }
            }
        });

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(COLOR_GRID);
        table.setSelectionBackground(new Color(232, 242, 252));
        table.setSelectionForeground(Color.BLACK);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(COLOR_BG_HEADER);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ALT_ROW);
                
                if (column == 5 || column == 6) {
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                    setForeground(COLOR_EDITABLE);
                    if (isSelected) setForeground(Color.BLUE);
                } else if (column == 8) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    setForeground("Đã kích hoạt".equals(value) ? new Color(0, 128, 0) : Color.RED);
                } else if (column == 9) {
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if ("Bị chặn (Block)".equals(value)) setForeground(Color.RED);
                    else setForeground(new Color(0, 128, 0));
                } else {
                    setForeground(Color.BLACK);
                }
                return this;
            }
        });

        createContextMenu();
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = table.getSelectedRow();
                    int viewCol = table.getSelectedColumn();
                    if (viewRow != -1) {
                        int modelCol = table.convertColumnIndexToModel(viewCol);
                        if (modelCol == 5 || modelCol == 6) {
                            return; 
                        }
                        int modelRow = table.convertRowIndexToModel(viewRow);
                        openPlayerEditorDB(Integer.parseInt(model.getValueAt(modelRow, 0).toString()));
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private void createContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem mBuffItem = new JMenuItem("Buff Item (Thêm vào hành trang)");
        mBuffItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mBuffItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) openBuffItemDialog(Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(r), 0).toString()), model.getValueAt(table.convertRowIndexToModel(r), 2).toString());
        });
        menu.add(mBuffItem);
        
        JMenuItem mCheckInventory = new JMenuItem("Check hành trang");
        mCheckInventory.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mCheckInventory.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                int playerId = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(r), 0).toString());
                String playerName = model.getValueAt(table.convertRowIndexToModel(r), 2).toString();
                openCheckInventoryDialog(playerId, playerName);
            } else {
                openCheckInventoryDialog();
            }
        });
        menu.add(mCheckInventory);
        
        JMenuItem mRevokeItem = new JMenuItem("Thu hồi vật phẩm");
        mRevokeItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mRevokeItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r != -1) {
                int playerId = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(r), 0).toString());
                String playerName = model.getValueAt(table.convertRowIndexToModel(r), 2).toString();
                openRevokeItemDialog(playerId, playerName);
            } else {
                openRevokeItemDialog();
            }
        });
        menu.add(mRevokeItem);
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) showMenu(e); }
            public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) showMenu(e); }
            private void showMenu(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void openBuffItemDialog(int playerId, String playerName) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Buff Item: " + playerName, true);
        d.setSize(400, 300); d.setLocationRelativeTo(null); d.setLayout(new GridLayout(5, 2, 10, 10));
        JTextField txtId = new JTextField(); JTextField txtQty = new JTextField("1"); JTextField txtOpt = new JTextField("[]");
        addUndoRedo(txtId); addUndoRedo(txtQty); addUndoRedo(txtOpt);
        d.add(new JLabel("ID Item:")); d.add(txtId); d.add(new JLabel("Số lượng:")); d.add(txtQty);
        d.add(new JLabel("Option:")); d.add(txtOpt);
        JButton btn = new JButton("OK");
        btn.addActionListener(e->{ 
            try {
                String command = "BUFF_ITEM:" + playerId + ":" + txtId.getText() + ":" + txtQty.getText() + ":" + txtOpt.getText();
                sendCommandToServer(command);
                d.dispose(); 
                JOptionPane.showMessageDialog(this, "Đã gửi lệnh buff item đến server!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });
        d.add(btn); d.setVisible(true);
    }

    private void openCheckInventoryDialog() {
        openCheckInventoryDialog(-1, "");
    }
    
    private void openRevokeItemDialog() {
        openRevokeItemDialog(-1, "");
    }
    
    private void openCheckInventoryDialog(int playerId, String playerName) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Kiểm tra hành trang", true);
        dialog.setSize(1100, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearchItem = new JTextField(20);
        txtSearchItem.putClientProperty("JTextField.placeholderText", "Nhập ID hoặc tên vật phẩm...");
        JComboBox<String> cbSearchType = new JComboBox<>(new String[]{"Tìm theo ID"});
        JCheckBox chkSearchAll = new JCheckBox("Tìm tất cả người chơi", playerId == -1);
        chkSearchAll.setEnabled(playerId != -1);
        
        // Thêm checkbox để chọn player
        JCheckBox chkSelectAllPlayers = new JCheckBox("Chọn tất cả Player");
        chkSelectAllPlayers.setVisible(false); // Ẩn cho đến khi có kết quả
        
        searchPanel.add(new JLabel("Vật phẩm:"));
        searchPanel.add(txtSearchItem);
        searchPanel.add(cbSearchType);
        JButton btnSearchItem = new JButton("Tìm kiếm");
        searchPanel.add(btnSearchItem);
        searchPanel.add(chkSearchAll);
        searchPanel.add(chkSelectAllPlayers);

        // Bảng kết quả - THÊM CỘT CHỌN PLAYER
        String[] columns = {"Chọn", "Player ID", "Tên nhân vật", "Vị trí", "ID Item", "Icon", "Icon ID", "Tên Item", "Số lượng", "Options"};
        DefaultTableModel resultModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                if (columnIndex == 5) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Chỉ cho phép chọn player
            }
        };
        
        JTable resultTable = new JTable(resultModel);
        resultTable.setRowHeight(30);
        resultTable.setShowGrid(true);
        resultTable.setGridColor(COLOR_GRID);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(40);
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        resultTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(9).setPreferredWidth(200);

        // Panel chức năng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRevokeSelected = createStyledButton("THU HỒI VẬT PHẨM ĐÃ CHỌN", new Color(178, 34, 34), Color.WHITE);
        JButton btnClose = createStyledButton("Đóng", Color.GRAY, Color.WHITE);
        
        actionPanel.add(btnRevokeSelected);
        actionPanel.add(btnClose);

        // Sự kiện tìm kiếm
        btnSearchItem.addActionListener(e -> {
            String searchText = txtSearchItem.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ID hoặc tên vật phẩm!");
                return;
            }
            
            boolean searchAll = chkSearchAll.isSelected();
            boolean searchById = cbSearchType.getSelectedIndex() == 0;
            
            searchItemInInventory(searchAll ? -1 : playerId, searchText, searchById, resultModel, chkSelectAllPlayers);
        });

        // Sự kiện chọn tất cả player
        chkSelectAllPlayers.addActionListener(e -> {
            boolean selectAll = chkSelectAllPlayers.isSelected();
            for (int i = 0; i < resultModel.getRowCount(); i++) {
                resultModel.setValueAt(selectAll, i, 0);
            }
        });

        // Sự kiện thu hồi vật phẩm đã chọn

btnRevokeSelected.addActionListener(e -> {
    List<Integer> selectedPlayerIds = new ArrayList<>();
    Map<Integer, String> playerNames = new HashMap<>();

    Integer itemId = null;
    String itemName = null;

    // Lấy danh sách player đã chọn + lấy luôn itemId từ dòng đầu tiên được tick
    for (int i = 0; i < resultModel.getRowCount(); i++) {
        Boolean isSelected = (Boolean) resultModel.getValueAt(i, 0);
        if (isSelected != null && isSelected) {
            int pid = Integer.parseInt(resultModel.getValueAt(i, 1).toString());
            String pname = resultModel.getValueAt(i, 2).toString();

            if (!selectedPlayerIds.contains(pid)) {
                selectedPlayerIds.add(pid);
                playerNames.put(pid, pname);
            }

            // Lấy itemId từ cột "ID Item" (index 4) ở dòng đầu tiên được chọn
            if (itemId == null) {
                try {
                    itemId = Integer.parseInt(resultModel.getValueAt(i, 4).toString());
                    itemName = getItemName(itemId);
                } catch (Exception ex) {
                    itemId = null;
                }
            }
        }
    }

    if (selectedPlayerIds.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ít nhất một Player!");
        return;
    }

    if (itemId == null) {
        JOptionPane.showMessageDialog(dialog, "Không xác định được ID vật phẩm từ danh sách (cột ID Item)!");
        return;
    }

    // Nếu bạn muốn CHẮC CHẮN tất cả dòng tick đều cùng 1 itemId (tránh tick nhiều item khác nhau)
    for (int i = 0; i < resultModel.getRowCount(); i++) {
        Boolean isSelected = (Boolean) resultModel.getValueAt(i, 0);
        if (isSelected != null && isSelected) {
            try {
                int rowItemId = Integer.parseInt(resultModel.getValueAt(i, 4).toString());
                if (rowItemId != itemId) {
                    JOptionPane.showMessageDialog(dialog,
                            "Bạn đang chọn nhiều dòng có ID Item khác nhau.\n"
                                    + "Hãy chỉ tick các dòng cùng 1 ID Item để thu hồi đúng!",
                            "Sai lựa chọn", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Có dòng bị lỗi ID Item!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    StringBuilder playerList = new StringBuilder();
    for (int pid : selectedPlayerIds) {
        playerList.append("- ").append(playerNames.get(pid)).append(" (ID: ").append(pid).append(")\n");
    }

    int confirm = JOptionPane.showConfirmDialog(dialog,
            "Bạn có chắc muốn thu hồi vật phẩm:\n" +
                    "• ID: " + itemId + "\n" +
                    "• Tên: " + itemName + "\n" +
                    "• Từ " + selectedPlayerIds.size() + " Player đã chọn:\n" + playerList +
                    "\nThao tác này KHÔNG THỂ hoàn tác!",
            "XÁC NHẬN THU HỒI",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        revokeItemFromSelectedPlayers(selectedPlayerIds, itemId, itemName, dialog);
    }
});
      

        // Sự kiện đóng dialog
        btnClose.addActionListener(e -> dialog.dispose());

        // Tự động tìm nếu có playerId cụ thể
        if (playerId != -1) {
            txtSearchItem.setText("");
            SwingUtilities.invokeLater(() -> {
                searchItemInInventory(playerId, "", false, resultModel, chkSelectAllPlayers);
            });
        }

        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(resultTable), BorderLayout.CENTER);
        dialog.add(actionPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void searchItemInInventory(int playerId, String searchText, boolean searchById, DefaultTableModel resultModel, JCheckBox chkSelectAllPlayers) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> resultModel.setRowCount(0));
            
            AtomicInteger totalFound = new AtomicInteger(0);
            Set<Integer> foundPlayerIds = new HashSet<>();
            
            try (Connection conn = getConnection()) {
                String sql = "SELECT id, name, items_body, items_bag, items_box FROM player";
                if (playerId != -1) {
                    sql += " WHERE id = ?";
                } else {
                    sql += " LIMIT 1000";
                }
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                if (playerId != -1) {
                    stmt.setInt(1, playerId);
                }
                
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    int pid = rs.getInt("id");
                    String playerName = rs.getString("name");
                    
                    String[][] locations = {
                        {"BODY", rs.getString("items_body")},
                        {"BAG", rs.getString("items_bag")},
                        {"BOX", rs.getString("items_box")}
                    };
                    
                    for (String[] loc : locations) {
                        String location = loc[0];
                        String json = loc[1];
                        
                        if (json == null || json.isEmpty() || json.equals("[]")) continue;
                        
                        try {
                            JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
                            for (int i = 0; i < arr.size(); i++) {
                                JsonElement el = arr.get(i);
                                if (el.isJsonPrimitive()) {
                                    String itemStr = el.getAsString();
                                    JsonArray itemArr = new JsonParser().parse(itemStr).getAsJsonArray();
                                    int itemId = itemArr.get(0).getAsInt();
                                    
                                    if (itemId == -1) continue;
                                    
                                    String itemName = getItemName(itemId);
                                    int quantity = itemArr.get(1).getAsInt();
                                    String options = itemArr.size() > 2 ? itemArr.get(2).getAsString() : "[]";
                                    int iconId = itemIconMap.getOrDefault(itemId, -1);
                                    ImageIcon icon = getItemIcon(itemId);
                                    
                                    boolean match = false;
                                    if (searchText.isEmpty()) {
                                        match = true;
                                    } else if (searchById) {
                                        try {
                                            int searchId = Integer.parseInt(searchText);
                                            match = itemId == searchId;
                                        } catch (NumberFormatException ex) {
                                            match = false;
                                        }
                                    } else {
                                        match = itemName.toLowerCase().contains(searchText.toLowerCase());
                                    }
                                    
                                    if (match) {
                                        totalFound.incrementAndGet();
                                        foundPlayerIds.add(pid);
                                        
                                        SwingUtilities.invokeLater(() -> {
                                            resultModel.addRow(new Object[]{
                                                false, // Chọn player
                                                pid,
                                                playerName,
                                                location,
                                                itemId,
                                                icon,
                                                iconId,
                                                itemName,
                                                quantity,
                                                parseOptionReadable(options)
                                            });
                                        });
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                
                SwingUtilities.invokeLater(() -> {
                    if (totalFound.get() == 0) {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy vật phẩm phù hợp!");
                        chkSelectAllPlayers.setVisible(false);
                    } else {
                        chkSelectAllPlayers.setVisible(true);
                        chkSelectAllPlayers.setText("Chọn tất cả Player (" + foundPlayerIds.size() + ")");
                    }
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm: " + ex.getMessage()));
            }
        }).start();
    }

    private void openRevokeItemDialog(int playerId, String playerName) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thu hồi vật phẩm", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thu hồi"));
        
        JTextArea taInfo = new JTextArea();
        taInfo.setEditable(false);
        taInfo.setLineWrap(true);
        taInfo.setWrapStyleWord(true);
        taInfo.setBackground(new Color(240, 240, 240));
        
        if (playerId == -1) {
            taInfo.setText("THU HỒI VẬT PHẨM TỪ TẤT CẢ NGƯỜI CHƠI\n\n"
                    + "• Chức năng này sẽ thu hồi vật phẩm từ TẤT CẢ người chơi\n"
                    + "• Vật phẩm sẽ bị xóa khỏi hành trang (đặt ID = -1)\n"
                    + "• Ô chứa vật phẩm vẫn được giữ nguyên\n"
                    + "• Thao tác này không thể hoàn tác!");
        } else {
            taInfo.setText("THU HỒI VẬT PHẨM TỪ NGƯỜI CHƠI\n\n"
                    + "• Người chơi: " + playerName + " (ID: " + playerId + ")\n"
                    + "• Vật phẩm sẽ bị xóa khỏi hành trang (đặt ID = -1)\n"
                    + "• Ô chứa vật phẩm vẫn được giữ nguyên\n"
                    + "• Thao tác này không thể hoàn tác!");
        }
        
        infoPanel.add(new JScrollPane(taInfo), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin vật phẩm"));
        
        JTextField txtItemId = new JTextField();
        JTextField txtItemName = new JTextField();
        txtItemName.setEditable(false);
        JCheckBox chkRevokeAll = new JCheckBox("Thu hồi từ TẤT CẢ người chơi", playerId == -1);
        chkRevokeAll.setEnabled(playerId != -1);
        
        JButton btnFindItem = new JButton("Tìm vật phẩm");
        
        inputPanel.add(new JLabel("ID Vật phẩm:"));
        inputPanel.add(txtItemId);
        inputPanel.add(new JLabel("Tên vật phẩm:"));
        inputPanel.add(txtItemName);
        inputPanel.add(new JLabel(""));
        inputPanel.add(chkRevokeAll);
        inputPanel.add(new JLabel(""));
        inputPanel.add(btnFindItem);

        btnFindItem.addActionListener(e -> {
            openItemSearchDialog(txtItemId, txtItemName);
        });

        txtItemId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateName(); }
            public void removeUpdate(DocumentEvent e) { updateName(); }
            public void changedUpdate(DocumentEvent e) { updateName(); }
            
            private void updateName() {
                try {
                    int id = Integer.parseInt(txtItemId.getText().trim());
                    String name = getItemName(id);
                    txtItemName.setText(name);
                } catch (Exception ex) {
                    txtItemName.setText("");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnRevoke = createStyledButton("THU HỒI VẬT PHẨM", new Color(178, 34, 34), Color.WHITE);
        JButton btnCancel = createStyledButton("Hủy", Color.GRAY, Color.WHITE);
        
        btnRevoke.addActionListener(e -> {
            String itemIdStr = txtItemId.getText().trim();
            if (itemIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ID vật phẩm!");
                return;
            }
            
            int itemId;
            try {
                itemId = Integer.parseInt(itemIdStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "ID vật phẩm phải là số!");
                return;
            }
            
            String itemName = getItemName(itemId);
            boolean revokeAll = chkRevokeAll.isSelected();
            int targetPlayerId = revokeAll ? -1 : playerId;
            
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc muốn thu hồi vật phẩm:\n" +
                    "• ID: " + itemId + "\n" +
                    "• Tên: " + itemName + "\n" +
                    "• Từ: " + (revokeAll ? "TẤT CẢ người chơi" : playerName + " (ID: " + playerId + ")") + "\n\n" +
                    "Thao tác này KHÔNG THỂ hoàn tác!",
                    "XÁC NHẬN THU HỒI",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                revokeItemFromPlayers(targetPlayerId, itemId, itemName, dialog);
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnRevoke);
        buttonPanel.add(btnCancel);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);
        
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openItemSearchDialog(JTextField targetIdField, JTextField targetNameField) {
        JDialog searchDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tìm vật phẩm", true);
        searchDialog.setSize(800, 500);
        searchDialog.setLocationRelativeTo(this);
        searchDialog.setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc ID vật phẩm...");
        JButton btnSearch = new JButton("Tìm");
        
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        String[] columns = {"ID", "Icon", "Tên Item", "Icon ID", "Loại", "Hệ"};
        DefaultTableModel searchModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (ItemData item : listAllItems) {
            searchModel.addRow(new Object[]{
                item.id,
                getItemIcon(item.id),
                item.name,
                itemIconMap.getOrDefault(item.id, -1),
                item.type,
                item.gender
            });
        }
        
        JTable itemTable = new JTable(searchModel);
        itemTable.setRowHeight(30);
        itemTable.setShowGrid(true);
        itemTable.setGridColor(COLOR_GRID);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(searchModel);
        itemTable.setRowSorter(sorter);
        
        btnSearch.addActionListener(e -> {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                try {
                    int id = Integer.parseInt(text);
                    List<RowFilter<Object, Object>> filters = new ArrayList<>();
                    filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, id, 0));
                    filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                    sorter.setRowFilter(RowFilter.orFilter(filters));
                } catch (NumberFormatException ex) {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                }
            }
        });

        itemTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = itemTable.getSelectedRow();
                    if (row != -1) {
                        int modelRow = itemTable.convertRowIndexToModel(row);
                        int itemId = (int) searchModel.getValueAt(modelRow, 0);
                        targetIdField.setText(String.valueOf(itemId));
                        searchDialog.dispose();
                    }
                }
            }
        });

        searchDialog.add(searchPanel, BorderLayout.NORTH);
        searchDialog.add(new JScrollPane(itemTable), BorderLayout.CENTER);
        searchDialog.setVisible(true);
    }

    private void revokeItemFromPlayers(int targetPlayerId, int itemId, String itemName, JDialog parentDialog) {
        new Thread(() -> {
            try {
                AtomicInteger affectedPlayers = new AtomicInteger(0);
                AtomicInteger totalRemoved = new AtomicInteger(0);
                
                try (Connection conn = getConnection()) {
                    conn.setAutoCommit(false);
                    
                    String playerSql;
                    if (targetPlayerId == -1) {
                        playerSql = "SELECT id, name, items_body, items_bag, items_box FROM player WHERE items_body LIKE ? OR items_bag LIKE ? OR items_box LIKE ? LIMIT 1000";
                    } else {
                        playerSql = "SELECT id, name, items_body, items_bag, items_box FROM player WHERE id = ?";
                    }
                    
                    PreparedStatement playerStmt = conn.prepareStatement(playerSql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                    
                    if (targetPlayerId == -1) {
                        String searchPattern = "%" + itemId + "%";
                        playerStmt.setString(1, searchPattern);
                        playerStmt.setString(2, searchPattern);
                        playerStmt.setString(3, searchPattern);
                    } else {
                        playerStmt.setInt(1, targetPlayerId);
                    }
                    
                    ResultSet rs = playerStmt.executeQuery();
                    
                    String updateSql = "UPDATE player SET items_body = ?, items_bag = ?, items_box = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    
                    while (rs.next()) {
                        int playerId = rs.getInt("id");
                        String playerName = rs.getString("name");
                        boolean modified = false;
                        
                        String[][] locations = {
                            {"items_body", rs.getString("items_body")},
                            {"items_bag", rs.getString("items_bag")},
                            {"items_box", rs.getString("items_box")}
                        };
                        
                        String[] updatedJsons = new String[3];
                        
                        for (int locIndex = 0; locIndex < locations.length; locIndex++) {
                            String locName = locations[locIndex][0];
                            String json = locations[locIndex][1];
                            String updatedJson = json;
                            
                            if (json != null && !json.isEmpty() && !json.equals("[]")) {
                                try {
                                    JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
                                    int removedInThisLocation = 0;
                                    
                                    for (int i = 0; i < arr.size(); i++) {
                                        JsonElement el = arr.get(i);
                                        if (el.isJsonPrimitive()) {
                                            String itemStr = el.getAsString();
                                            JsonArray itemArr = new JsonParser().parse(itemStr).getAsJsonArray();
                                            int currentItemId = itemArr.get(0).getAsInt();
                                            
                                            if (currentItemId == itemId) {
                                                JsonArray newItemArr = new JsonArray();
                                                newItemArr.add(-1);
                                                newItemArr.add(0);
                                                newItemArr.add("[]");
                                                newItemArr.add(System.currentTimeMillis());
                                                
                                                arr.set(i, new JsonPrimitive(newItemArr.toString()));
                                                removedInThisLocation++;
                                                modified = true;
                                            }
                                        }
                                    }
                                    
                                    if (removedInThisLocation > 0) {
                                        updatedJson = arr.toString();
                                        totalRemoved.addAndGet(removedInThisLocation);
                                        System.out.println("Đã xóa " + removedInThisLocation + " vật phẩm từ " + locName + " của player " + playerName + " (ID: " + playerId + ")");
                                    }
                                    
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            
                            updatedJsons[locIndex] = updatedJson;
                        }
                        
                        if (modified) {
                            updateStmt.setString(1, updatedJsons[0]);
                            updateStmt.setString(2, updatedJsons[1]);
                            updateStmt.setString(3, updatedJsons[2]);
                            updateStmt.setInt(4, playerId);
                            updateStmt.addBatch();
                            
                            sendCommandToServer("UPDATE_ITEMS:" + playerId + ":BODY:" + updatedJsons[0] + ":BAG:" + updatedJsons[1] + ":BOX:" + updatedJsons[2]);
                            
                            affectedPlayers.incrementAndGet();
                        }
                    }
                    
                    if (affectedPlayers.get() > 0) {
                        updateStmt.executeBatch();
                        conn.commit();
                    }
                    
                    rs.close();
                    playerStmt.close();
                    updateStmt.close();
                }
                
                SwingUtilities.invokeLater(() -> {
                    String message;
                    if (targetPlayerId == -1) {
                        message = "Đã thu hồi vật phẩm từ " + affectedPlayers.get() + " người chơi.\n"
                                + "• Tổng số vật phẩm bị xóa: " + totalRemoved.get() + "\n"
                                + "• Vật phẩm: " + itemName + " (ID: " + itemId + ")";
                    } else {
                        message = "Đã thu hồi vật phẩm từ người chơi.\n"
                                + "• Tổng số vật phẩm bị xóa: " + totalRemoved.get() + "\n"
                                + "• Vật phẩm: " + itemName + " (ID: " + itemId + ")";
                    }
                    
                    JOptionPane.showMessageDialog(parentDialog, message, "Thu hồi thành công", JOptionPane.INFORMATION_MESSAGE);
                    parentDialog.dispose();
                    
                    loadPlayersFromDB(txtSearch.getText().trim());
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(parentDialog, "Lỗi khi thu hồi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // PHƯƠNG THỨC MỚI: Thu hồi vật phẩm từ các player đã chọn
    private void revokeItemFromSelectedPlayers(List<Integer> selectedPlayerIds, int itemId, String itemName, JDialog parentDialog) {
        new Thread(() -> {
            try {
                AtomicInteger affectedPlayers = new AtomicInteger(0);
                AtomicInteger totalRemoved = new AtomicInteger(0);
                
                try (Connection conn = getConnection()) {
                    conn.setAutoCommit(false);
                    
                    // Tạo câu lệnh SQL với danh sách player IDs
                    StringBuilder sqlBuilder = new StringBuilder("SELECT id, name, items_body, items_bag, items_box FROM player WHERE id IN (");
                    for (int i = 0; i < selectedPlayerIds.size(); i++) {
                        sqlBuilder.append("?");
                        if (i < selectedPlayerIds.size() - 1) {
                            sqlBuilder.append(", ");
                        }
                    }
                    sqlBuilder.append(")");
                    
                    PreparedStatement playerStmt = conn.prepareStatement(sqlBuilder.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                    
                    for (int i = 0; i < selectedPlayerIds.size(); i++) {
                        playerStmt.setInt(i + 1, selectedPlayerIds.get(i));
                    }
                    
                    ResultSet rs = playerStmt.executeQuery();
                    
                    String updateSql = "UPDATE player SET items_body = ?, items_bag = ?, items_box = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    
                    while (rs.next()) {
                        int playerId = rs.getInt("id");
                        String playerName = rs.getString("name");
                        boolean modified = false;
                        
                        String[][] locations = {
                            {"items_body", rs.getString("items_body")},
                            {"items_bag", rs.getString("items_bag")},
                            {"items_box", rs.getString("items_box")}
                        };
                        
                        String[] updatedJsons = new String[3];
                        
                        for (int locIndex = 0; locIndex < locations.length; locIndex++) {
                            String locName = locations[locIndex][0];
                            String json = locations[locIndex][1];
                            String updatedJson = json;
                            
                            if (json != null && !json.isEmpty() && !json.equals("[]")) {
                                try {
                                    JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
                                    int removedInThisLocation = 0;
                                    
                                    for (int i = 0; i < arr.size(); i++) {
                                        JsonElement el = arr.get(i);
                                        if (el.isJsonPrimitive()) {
                                            String itemStr = el.getAsString();
                                            JsonArray itemArr = new JsonParser().parse(itemStr).getAsJsonArray();
                                            int currentItemId = itemArr.get(0).getAsInt();
                                            
                                            if (currentItemId == itemId) {
                                                JsonArray newItemArr = new JsonArray();
                                                newItemArr.add(-1);
                                                newItemArr.add(0);
                                                newItemArr.add("[]");
                                                newItemArr.add(System.currentTimeMillis());
                                                
                                                arr.set(i, new JsonPrimitive(newItemArr.toString()));
                                                removedInThisLocation++;
                                                modified = true;
                                            }
                                        }
                                    }
                                    
                                    if (removedInThisLocation > 0) {
                                        updatedJson = arr.toString();
                                        totalRemoved.addAndGet(removedInThisLocation);
                                        System.out.println("Đã xóa " + removedInThisLocation + " vật phẩm từ " + locName + " của player " + playerName + " (ID: " + playerId + ")");
                                    }
                                    
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            
                            updatedJsons[locIndex] = updatedJson;
                        }
                        
                        if (modified) {
                            updateStmt.setString(1, updatedJsons[0]);
                            updateStmt.setString(2, updatedJsons[1]);
                            updateStmt.setString(3, updatedJsons[2]);
                            updateStmt.setInt(4, playerId);
                            updateStmt.addBatch();
                            
                            sendCommandToServer("UPDATE_ITEMS:" + playerId + ":BODY:" + updatedJsons[0] + ":BAG:" + updatedJsons[1] + ":BOX:" + updatedJsons[2]);
                            
                            affectedPlayers.incrementAndGet();
                        }
                    }
                    
                    if (affectedPlayers.get() > 0) {
                        updateStmt.executeBatch();
                        conn.commit();
                    }
                    
                    rs.close();
                    playerStmt.close();
                    updateStmt.close();
                }
                
                SwingUtilities.invokeLater(() -> {
                    String message = "Đã thu hồi vật phẩm từ " + affectedPlayers.get() + "/" + selectedPlayerIds.size() + " người chơi đã chọn.\n"
                            + "• Tổng số vật phẩm bị xóa: " + totalRemoved.get() + "\n"
                            + "• Vật phẩm: " + itemName + " (ID: " + itemId + ")";
                    
                    JOptionPane.showMessageDialog(parentDialog, message, "Thu hồi thành công", JOptionPane.INFORMATION_MESSAGE);
                    parentDialog.dispose();
                    
                    loadPlayersFromDB(txtSearch.getText().trim());
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(parentDialog, "Lỗi khi thu hồi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private long countItemTotal(String... jsonLists) {
        long total = 0;
        for (String json : jsonLists) {
            try {
                if (json == null || json.isEmpty()) continue;
                JsonElement parsed = new JsonParser().parse(json);
                if (!parsed.isJsonArray()) continue;
                JsonArray arr = parsed.getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonArray item;
                    if (e.isJsonPrimitive()) item = new JsonParser().parse(e.getAsString()).getAsJsonArray();
                    else item = e.getAsJsonArray();
                    if (item.size() >= 2 && item.get(0).getAsInt() == 457) total += item.get(1).getAsLong(); 
                }
            } catch (Exception e) {}
        }
        return total;
    }

    private void loadPlayersFromDB(String keyword) {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0);
                inventoryCache.clear();
                modifiedPlayerIds.clear();
                btnQuickSave.setEnabled(false);
                btnQuickSave.setText("Lưu thay đổi (Ctrl+S)");
                btnQuickSave.setBackground(Color.GRAY);
            });
            
           String sql = "SELECT p.id, p.head, p.name, p.data_point, p.clan_id, p.data_inventory, p.items_bag, p.items_box, a.active, a.ban FROM player p LEFT JOIN account a ON p.account_id = a.id ";
            if (!keyword.isEmpty()) sql += "WHERE p.name LIKE '%" + keyword + "%' ";
            sql += "ORDER BY p.id ASC LIMIT 50";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int pid = rs.getInt("id");
                    Vector<Object> row = new Vector<>();
                    row.add((long)pid);
                    row.add(drawHeadIcon(rs.getInt("head"))); 
                    row.add(rs.getString("name"));
                    long power = 0;
                    try {
                        String dataPoint = rs.getString("data_point");
                        JsonArray point = new JsonParser().parse(dataPoint).getAsJsonArray();
                        power = point.get(1).getAsLong(); // phần tử số 2 = sức mạnh
                    } catch (Exception ex) {
                    }

                    row.add(String.format("%,d", power));
                    row.add(getClanName(rs.getInt("clan_id")));
                    
                    String rawInv = rs.getString("data_inventory");
                    inventoryCache.put(pid, rawInv);
                    
                    try {
                        JsonArray inv = new JsonParser().parse(rawInv).getAsJsonArray();
                        row.add(inv.get(0).getAsLong()); 
                        row.add(inv.get(1).getAsLong()); 
                    } catch (Exception e) { row.add(0L); row.add(0L); }
                    
                    row.add(countItemTotal(rs.getString("items_bag"), rs.getString("items_box"))); 
                    row.add(rs.getInt("active") == 1 ? "Đã kích hoạt" : "Chưa kích hoạt");
                    row.add(rs.getInt("ban") == 1 ? "Bị chặn (Block)" : "Bình thường");
                    
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void saveModifiedRows() {
        if (modifiedPlayerIds.isEmpty()) return;
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn lưu " + modifiedPlayerIds.size() + " tài khoản đã sửa? Thay đổi sẽ ảnh hưởng ngay đến server!", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false); 
                String updatePlayerSql = "UPDATE player SET data_inventory = ? WHERE id = ?";
                
                try (PreparedStatement psPlayer = conn.prepareStatement(updatePlayerSql)) {
                    
                    for (int i = 0; i < model.getRowCount(); i++) {
                        int pid = Integer.parseInt(model.getValueAt(i, 0).toString());
                        if (modifiedPlayerIds.contains(pid)) {
                            long newGold = Long.parseLong(model.getValueAt(i, 5).toString().replace(",", "").replace(".", ""));
                            long newGem = Long.parseLong(model.getValueAt(i, 6).toString().replace(",", "").replace(".", ""));
                            
                            String rawInv = inventoryCache.get(pid);
                            JsonArray invArr;
                            try { invArr = new JsonParser().parse(rawInv).getAsJsonArray(); } catch (Exception ex) { invArr = new JsonArray(); invArr.add(0); invArr.add(0); invArr.add(0); }
                            while (invArr.size() < 3) invArr.add(0);
                            
                            invArr.set(0, new JsonPrimitive(newGold));
                            invArr.set(1, new JsonPrimitive(newGem));
                            
                            psPlayer.setString(1, invArr.toString());
                            psPlayer.setInt(2, pid);
                            psPlayer.addBatch();
                            
                            sendCommandToServer("UPDATE_PLAYER:" + pid + ":GOLD:" + newGold + ":GEM:" + newGem);
                        }
                    }
                    psPlayer.executeBatch();
                    conn.commit();
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Đã lưu thành công và gửi lệnh cập nhật đến server!");
                        modifiedPlayerIds.clear();
                        btnQuickSave.setEnabled(false);
                        btnQuickSave.setBackground(Color.GRAY);
                        btnQuickSave.setText("Lưu thay đổi (Ctrl+S)");
                        loadPlayersFromDB(txtSearch.getText().trim());
                    });
                } catch (Exception ex) {
                    conn.rollback();
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + ex.getMessage()));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }).start();
    }

    private void openPlayerEditorDB(int playerId) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa Chi Tiết - ID: " + playerId, true);
        d.setSize(1200, 800);
        d.setLocationRelativeTo(null);
        d.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        Map<String, Component> inputs = new HashMap<>();
        Map<String, String> originalData = new HashMap<>();
        
        JRootPane rootPane = d.getRootPane();
        InputMap im = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rootPane.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "saveDetail");
        
        new Thread(() -> {
            String query = "SELECT p.*, a.active, a.ban FROM player p LEFT JOIN account a ON p.account_id = a.id WHERE p.id = " + playerId;
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                if (rs.next()) {
                    int accountId = rs.getInt("account_id");
                    originalData.put("data_inventory", rs.getString("data_inventory"));
                    originalData.put("data_point", rs.getString("data_point"));
                    originalData.put("items_body", rs.getString("items_body"));
                    originalData.put("items_bag", rs.getString("items_bag"));
                    originalData.put("items_box", rs.getString("items_box"));
                    originalData.put("pet", rs.getString("pet"));
                    
                    originalData.put("data_task", rs.getString("data_task"));
                    originalData.put("data_side_task", rs.getString("data_side_task"));
                    originalData.put("data_clan_task", rs.getString("data_clan_task"));
                    originalData.put("data_kol_task", rs.getString("data_kol_task"));
                    
                    originalData.put("dataBadges", rs.getString("dataBadges"));
                    
                    JPanel pMainInfo = new JPanel(new GridBagLayout());
                    pMainInfo.setBorder(new EmptyBorder(15, 15, 15, 15));
                    GridBagConstraints g = new GridBagConstraints();
                    g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5, 5, 5, 5);
                    
                    // Đã bỏ phần thông tin tài khoản
                    
                    JPanel pChar = createSectionPanel("Thông tin Nhân vật");
                    addLabelInput(pChar, "Tên:", rs.getString("name"), "name", inputs);
                    String powerStr = "0";
                    try {
                        JsonArray point = new JsonParser().parse(rs.getString("data_point")).getAsJsonArray();
                        powerStr = point.get(1).getAsString();
                    } catch (Exception ex) {
                    }

                    addLabelInput(pChar, "Sức mạnh:", powerStr, "power", inputs);

                    addLabelInput(pChar, "Head Part ID:", String.valueOf(rs.getInt("head")), "head", inputs); 

                    JPanel pPoint = createSectionPanel("Chỉ số & Tiềm năng");
                    pPoint.setLayout(new GridLayout(0, 2, 5, 5));
                    JsonArray point = new JsonParser().parse(originalData.get("data_point")).getAsJsonArray();
                    addLabelInputGrid(pPoint, "Tiềm năng:", getJsonVal(point, 2), "tiemnang", inputs);
                    addLabelInputGrid(pPoint, "HP Gốc:", getJsonVal(point, 5), "hpg", inputs);
                    addLabelInputGrid(pPoint, "KI Gốc:", getJsonVal(point, 6), "mpg", inputs);
                    addLabelInputGrid(pPoint, "Sức đánh:", getJsonVal(point, 7), "dameg", inputs);
                    addLabelInputGrid(pPoint, "Giáp:", getJsonVal(point, 8), "defg", inputs);
                    addLabelInputGrid(pPoint, "Chí mạng:", getJsonVal(point, 9), "critg", inputs);

                    JPanel pAsset = createSectionPanel("Tài sản");
                    JsonArray inv = new JsonParser().parse(originalData.get("data_inventory")).getAsJsonArray();
                    addLabelInput(pAsset, "Vàng:", inv.get(0).getAsString(), "gold", inputs);
                    addLabelInput(pAsset, "Ngọc xanh:", inv.get(1).getAsString(), "gem", inputs);
                    addLabelInput(pAsset, "Hồng ngọc:", inv.size()>2?inv.get(2).getAsString():"0", "ruby", inputs);

                    g.gridx=0; g.gridy=0; g.weightx=1.0; pMainInfo.add(pChar, g);
                    g.gridy=1; pMainInfo.add(pPoint, g);
                    g.gridy=2; g.weighty=1.0; g.anchor=GridBagConstraints.NORTH; pMainInfo.add(pAsset, g);

                    JTabbedPane tabItems = new JTabbedPane();
                    DefaultTableModel mBody = createItemModel();
                    DefaultTableModel mBag = createItemModel();
                    DefaultTableModel mBox = createItemModel();
                    
                    loadItemsToModel(originalData.get("items_body"), mBody);
                    loadItemsToModel(originalData.get("items_bag"), mBag);
                    loadItemsToModel(originalData.get("items_box"), mBox);
                    
                    tabItems.addTab("Đồ đang mặc", createItemPanel(mBody, d));
                    tabItems.addTab("Hành trang", createItemPanel(mBag, d));
                    tabItems.addTab("Rương đồ", createItemPanel(mBox, d));

                    JPanel pPet = new JPanel(new BorderLayout());
                    JPanel pPetContent = new JPanel(new GridBagLayout());
                    JScrollPane petScroll = new JScrollPane(pPetContent);
                    pPet.add(petScroll, BorderLayout.CENTER);
                    
                    String petStr = rs.getString("pet");
                    if(petStr != null && !petStr.equals("[]") && !petStr.isEmpty()) {
                        try {
                            JsonArray petArr = new JsonParser().parse(petStr).getAsJsonArray();
                            if(petArr.size() > 1) {
                                String infoStr = petArr.get(0).getAsString(); 
                                JsonArray infoArr = new JsonParser().parse(infoStr).getAsJsonArray();
                                String pointStr = petArr.get(1).getAsString();
                                JsonArray pointArr = new JsonParser().parse(pointStr).getAsJsonArray();

                                GridBagConstraints gp = new GridBagConstraints();
                                gp.fill = GridBagConstraints.HORIZONTAL; gp.weightx=1.0; gp.insets = new Insets(5,5,5,5); gp.gridx=0; gp.gridy=0;

                                JPanel pPetInfo = createSectionPanel("Thông tin cơ bản");
                                JComboBox<String> cbPetType = new JComboBox<>(new String[]{"0 - Mabu", "1 - Fide", "2 - Cadic", "3 - Pic", "4 - Quy lão"});
                                cbPetType.setEditable(true); cbPetType.setSelectedItem(infoArr.get(0).getAsString());
                                inputs.put("pet_type", cbPetType);
                                JPanel pT = new JPanel(new BorderLayout()); pT.add(new JLabel("Loại Đệ:"),BorderLayout.WEST); pT.add(cbPetType); pPetInfo.add(pT);

                                JComboBox<String> cbPetGender = new JComboBox<>(new String[]{"0 - Trái đất", "1 - Namếc", "2 - Xayda"});
                                cbPetGender.setSelectedIndex(infoArr.get(1).getAsInt());
                                inputs.put("pet_gender", cbPetGender);
                                JPanel pG = new JPanel(new BorderLayout()); pG.add(new JLabel("Hệ:"),BorderLayout.WEST); pG.add(cbPetGender); pPetInfo.add(pG);

                                addLabelInput(pPetInfo, "Tên Đệ tử:", infoArr.get(2).getAsString(), "pet_name", inputs);
                                
                                JComboBox<String> cbPetStatus = new JComboBox<>(new String[]{"0 - Đi theo", "1 - Bảo vệ", "2 - Tấn công", "3 - Về nhà", "4 - Hợp thể"});
                                try { cbPetStatus.setSelectedIndex(infoArr.get(5).getAsInt()); } catch(Exception ex) {}
                                inputs.put("pet_status", cbPetStatus);
                                JPanel pS = new JPanel(new BorderLayout()); pS.add(new JLabel("Trạng thái:"),BorderLayout.WEST); pS.add(cbPetStatus); pPetInfo.add(pS);
                                pPetContent.add(pPetInfo, gp);

                                gp.gridy=1;
                                JPanel pPetStats = createSectionPanel("Chỉ số Sức Mạnh (Point)");
                                pPetStats.setLayout(new GridLayout(0, 2, 10, 10));
                                
                                addLabelInputGrid(pPetStats, "Sức mạnh:", getJsonVal(pointArr, 1), "pet_power", inputs);
                                addLabelInputGrid(pPetStats, "Tiềm năng:", getJsonVal(pointArr, 2), "pet_tiemnang", inputs);
                                addLabelInputGrid(pPetStats, "HP Gốc:", getJsonVal(pointArr, 5), "pet_hpg", inputs);
                                addLabelInputGrid(pPetStats, "KI Gốc:", getJsonVal(pointArr, 6), "pet_mpg", inputs);
                                addLabelInputGrid(pPetStats, "Sức đánh:", getJsonVal(pointArr, 7), "pet_dameg", inputs);
                                addLabelInputGrid(pPetStats, "Giáp:", getJsonVal(pointArr, 8), "pet_defg", inputs);
                                addLabelInputGrid(pPetStats, "Chí mạng:", getJsonVal(pointArr, 9), "pet_critg", inputs);
                                
                                pPetContent.add(pPetStats, gp);
                            }
                        } catch(Exception ex) { pPet.add(new JLabel("Lỗi đọc đệ tử: " + ex.getMessage())); }
                    } else { pPet.add(new JLabel("Không có đệ tử.", SwingConstants.CENTER)); }

                    JPanel pTasks = createTaskPanel(originalData, inputs);
                    
                    DefaultTableModel mBadges = new DefaultTableModel(new String[]{"ID Badges", "Icon", "Tên Danh Hiệu", "Chỉ số (Options)", "Thời gian hết hạn (Long)", "Ngày còn lại", "Đang dùng"}, 0) {
                         @Override public boolean isCellEditable(int row, int column) { return column == 0 || column == 4 || column == 6; }
                         @Override public Class<?> getColumnClass(int columnIndex) {
                             if(columnIndex == 1) return ImageIcon.class;
                             if(columnIndex == 6) return Boolean.class;
                             return Object.class;
                         }
                    };
                    JPanel pBadges = createBadgesPanel(originalData.get("dataBadges"), mBadges, d);

                    tabs.addTab("Thông tin chung", new JScrollPane(pMainInfo));
                    tabs.addTab("Vật phẩm", tabItems);
                    tabs.addTab("Đệ tử", pPet);
                    tabs.addTab("Nhiệm vụ", new JScrollPane(pTasks));
                    tabs.addTab("Danh hiệu (Badges)", pBadges);

                    d.add(tabs, BorderLayout.CENTER);

                    JPanel pBtn = new JPanel(); pBtn.setBorder(new EmptyBorder(10, 0, 10, 0));
                    JButton btnSave = createStyledButton("LƯU DỮ LIỆU & CẬP NHẬT SERVER", COLOR_SUCCESS, Color.WHITE);
                    btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    btnSave.setPreferredSize(new Dimension(200, 45));
                    btnSave.addActionListener(ev -> savePlayerDB(playerId, accountId, inputs, mBody, mBag, mBox, mBadges, originalData, d));
                    pBtn.add(btnSave); d.add(pBtn, BorderLayout.SOUTH);
                    
                    am.put("saveDetail", new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            btnSave.doClick();
                        }
                    });
                    
                    SwingUtilities.invokeLater(() -> d.setVisible(true));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
    
    private JPanel createBadgesPanel(String jsonBadges, DefaultTableModel model, JDialog parent) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        model.setRowCount(0);

        if (jsonBadges != null && !jsonBadges.isEmpty() && !jsonBadges.equals("[]")) {
            try {
                JsonArray arr = new JsonParser().parse(jsonBadges).getAsJsonArray();
                for (JsonElement e : arr) {
                    JsonObject obj = e.getAsJsonObject();
                    
                    int id = obj.get("idBadGes").getAsInt();
                    long time = obj.get("timeofUseBadges").getAsLong();
                    boolean isUse = obj.get("isUse").getAsBoolean();
                    
                    long timeLeft = time - System.currentTimeMillis();
                    long daysLeft = timeLeft / (24 * 60 * 60 * 1000L);
                    
                    BadgeTemplate temp = badgeTemplateMap.get(id);
                    
                    String name;
                    String optionsReadable;
                    ImageIcon icon;

                    if (temp != null) {
                        name = temp.name;
                        optionsReadable = parseBadgeOptions(temp.optionsJson);
                        icon = loadIconRaw(temp.iconId); 
                    } else {
                        name = "Unknown [" + id + "]";
                        optionsReadable = "";
                        icon = null;
                    }
                    
                    model.addRow(new Object[]{id, icon, name, optionsReadable, time, (daysLeft > 0 ? daysLeft + " ngày" : "Hết hạn"), isUse});
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        JTable t = new JTable(model);
        t.setRowHeight(30);
        t.setShowGrid(true);
        t.setGridColor(COLOR_GRID);
        t.getColumnModel().getColumn(0).setPreferredWidth(60);
        t.getColumnModel().getColumn(1).setPreferredWidth(40);
        t.getColumnModel().getColumn(2).setPreferredWidth(150);
        t.getColumnModel().getColumn(3).setPreferredWidth(250);
        
        JPanel pTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Badge");
        JButton btnDel = new JButton("Xóa Badge");
        
        btnAdd.addActionListener(e -> openBadgeAddDialog(model, parent));
        btnDel.addActionListener(e -> {
            int r = t.getSelectedRow();
            if(r != -1) model.removeRow(r);
        });
        
        pTool.add(btnAdd); pTool.add(btnDel);
        p.add(pTool, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }
    
    private void openBadgeAddDialog(DefaultTableModel model, JDialog parent) {
        JDialog d = new JDialog(parent, "Thêm Danh Hiệu", true);
        d.setSize(600, 500); d.setLayout(new BorderLayout()); d.setLocationRelativeTo(parent);
        
        JPanel pTop = new JPanel(new BorderLayout(5, 5));
        pTop.setBorder(new EmptyBorder(5,5,5,5));
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc ID danh hiệu...");
        pTop.add(new JLabel("Tìm kiếm: "), BorderLayout.WEST);
        pTop.add(txtSearch, BorderLayout.CENTER);
        
        DefaultTableModel searchModel = new DefaultTableModel(new String[]{"ID", "Icon", "Tên Danh Hiệu", "Options"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex == 1) return ImageIcon.class;
                return Object.class;
            }
        };
        
        for(BadgeTemplate b : badgeTemplateMap.values()) {
            searchModel.addRow(new Object[]{b.id, loadIconRaw(b.iconId), b.name, parseBadgeOptions(b.optionsJson)});
        }
        
        JTable t = new JTable(searchModel);
        t.setRowHeight(25);
        t.setShowGrid(true);
        t.setGridColor(COLOR_GRID);
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(40);
        t.getColumnModel().getColumn(2).setPreferredWidth(150);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(searchModel);
        t.setRowSorter(sorter);
        
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            void filter() {
                String text = txtSearch.getText().trim();
                if(text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });
        
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int r = t.getSelectedRow();
                    if(r != -1) {
                        int modelRow = t.convertRowIndexToModel(r);
                        int id = (int) searchModel.getValueAt(modelRow, 0);
                        ImageIcon icon = (ImageIcon) searchModel.getValueAt(modelRow, 1);
                        String name = (String) searchModel.getValueAt(modelRow, 2);
                        String opts = (String) searchModel.getValueAt(modelRow, 3);
                        
                        String dayStr = JOptionPane.showInputDialog(d, "Nhập số ngày sử dụng:", "30");
                        if(dayStr != null) {
                            try {
                                int days = Integer.parseInt(dayStr);
                                long time = System.currentTimeMillis() + (long)days * 24 * 60 * 60 * 1000L;
                                model.addRow(new Object[]{id, icon, name, opts, time, days + " ngày", false});
                                d.dispose();
                            } catch(Exception ex) { JOptionPane.showMessageDialog(d, "Lỗi nhập ngày!"); }
                        }
                    }
                }
            }
        });
        
        d.add(pTop, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.setVisible(true);
    }
    
    private String parseBadgeOptions(String jsonOpt) {
        if(jsonOpt == null || jsonOpt.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        try {
            JsonArray arr = new JsonParser().parse(jsonOpt).getAsJsonArray();
            for(JsonElement e : arr) {
                JsonObject obj = e.getAsJsonObject();
                int id = obj.get("id").getAsInt();
                int param = obj.get("param").getAsInt();
                sb.append(formatOption(id, param)).append("; ");
            }
        } catch(Exception e) { return jsonOpt; }
        return sb.toString();
    }

    private JPanel createTaskPanel(Map<String, String> originalData, Map<String, Component> inputs) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel mainTaskPanel = createSectionPanel("Nhiệm vụ chính tuyến");
        mainTaskPanel.setLayout(new GridLayout(0, 2, 5, 5));
        
        JsonArray dataTask = new JsonArray();
        try {
            String taskStr = originalData.get("data_task");
            if (taskStr != null && !taskStr.isEmpty()) {
                dataTask = new JsonParser().parse(taskStr).getAsJsonArray();
            }
        } catch (Exception e) {
            dataTask = new JsonArray();
        }
        while (dataTask.size() < 4) dataTask.add(0);
        
        JLabel lblMainTaskId = new JLabel("ID Nhiệm vụ:");
        JComboBox<String> cbMainTaskId = new JComboBox<>();
        cbMainTaskId.addItem("-1 - Không có");
        for (Map.Entry<Integer, String> entry : taskMainTemplateMap.entrySet()) {
            cbMainTaskId.addItem(entry.getKey() + " - " + entry.getValue());
        }
        int currentMainTaskId = dataTask.get(0).getAsInt();
        for (int i = 0; i < cbMainTaskId.getItemCount(); i++) {
            String item = cbMainTaskId.getItemAt(i);
            if (item.startsWith(currentMainTaskId + " - ")) {
                cbMainTaskId.setSelectedIndex(i);
                break;
            }
        }
        inputs.put("mainTaskId", cbMainTaskId);
        
        JLabel lblMainTaskIndex = new JLabel("Index:");
        JTextField txtMainTaskIndex = new JTextField(dataTask.get(1).getAsString());
        addUndoRedo(txtMainTaskIndex);
        inputs.put("mainTaskIndex", txtMainTaskIndex);
        
        JLabel lblMainTaskCount = new JLabel("Count:");
        JTextField txtMainTaskCount = new JTextField(dataTask.get(2).getAsString());
        addUndoRedo(txtMainTaskCount);
        inputs.put("mainTaskCount", txtMainTaskCount);
        
        JLabel lblMainTaskLastTime = new JLabel("Last Time:");
        JTextField txtMainTaskLastTime = new JTextField(dataTask.get(3).getAsString());
        addUndoRedo(txtMainTaskLastTime);
        inputs.put("mainTaskLastTime", txtMainTaskLastTime);
        
        mainTaskPanel.add(lblMainTaskId); mainTaskPanel.add(cbMainTaskId);
        mainTaskPanel.add(lblMainTaskIndex); mainTaskPanel.add(txtMainTaskIndex);
        mainTaskPanel.add(lblMainTaskCount); mainTaskPanel.add(txtMainTaskCount);
        mainTaskPanel.add(lblMainTaskLastTime); mainTaskPanel.add(txtMainTaskLastTime);
        
        JPanel sideTaskPanel = createSectionPanel("Nhiệm vụ hàng ngày");
        sideTaskPanel.setLayout(new GridLayout(0, 4, 5, 5));
        
        JsonArray sideTask = new JsonArray();
        try {
            String sideStr = originalData.get("data_side_task");
            if (sideStr != null && !sideStr.isEmpty()) {
                sideTask = new JsonParser().parse(sideStr).getAsJsonArray();
            }
        } catch (Exception e) {
            sideTask = new JsonArray();
        }
        while (sideTask.size() < 6) sideTask.add(0);
        
        for (int i = 0; i < 3; i++) {
            int idx = i * 2;
            JLabel lblSideId = new JLabel("Nhiệm vụ " + (i + 1) + " ID:");
            JComboBox<String> cbSideId = new JComboBox<>();
            cbSideId.addItem("-1 - Không có");
            for (Map.Entry<Integer, String> entry : sideTaskTemplateMap.entrySet()) {
                cbSideId.addItem(entry.getKey() + " - " + entry.getValue());
            }
            int currentSideId = sideTask.get(idx).getAsInt();
            for (int j = 0; j < cbSideId.getItemCount(); j++) {
                String item = cbSideId.getItemAt(j);
                if (item.startsWith(currentSideId + " - ")) {
                    cbSideId.setSelectedIndex(j);
                    break;
                }
            }
            inputs.put("sideTaskId_" + i, cbSideId);
            
            JLabel lblSideCount = new JLabel("Count:");
            JTextField txtSideCount = new JTextField(sideTask.get(idx + 1).getAsString());
            addUndoRedo(txtSideCount);
            inputs.put("sideTaskCount_" + i, txtSideCount);
            
            sideTaskPanel.add(lblSideId); sideTaskPanel.add(cbSideId);
            sideTaskPanel.add(lblSideCount); sideTaskPanel.add(txtSideCount);
        }
        
        JPanel clanTaskPanel = createSectionPanel("Nhiệm vụ clan");
        clanTaskPanel.setLayout(new GridLayout(0, 4, 5, 5));
        
        JsonArray clanTask = new JsonArray();
        try {
            String clanStr = originalData.get("data_clan_task");
            if (clanStr != null && !clanStr.isEmpty()) {
                clanTask = new JsonParser().parse(clanStr).getAsJsonArray();
            }
        } catch (Exception e) {
            clanTask = new JsonArray();
        }
        while (clanTask.size() < 6) clanTask.add(0);
        
        for (int i = 0; i < 3; i++) {
            int idx = i * 2;
            JLabel lblClanId = new JLabel("Nhiệm vụ " + (i + 1) + " ID:");
            JComboBox<String> cbClanId = new JComboBox<>();
            cbClanId.addItem("-1 - Không có");
            for (Map.Entry<Integer, String> entry : clanTaskTemplateMap.entrySet()) {
                cbClanId.addItem(entry.getKey() + " - " + entry.getValue());
            }
            int currentClanId = clanTask.get(idx).getAsInt();
            for (int j = 0; j < cbClanId.getItemCount(); j++) {
                String item = cbClanId.getItemAt(j);
                if (item.startsWith(currentClanId + " - ")) {
                    cbClanId.setSelectedIndex(j);
                    break;
                }
            }
            inputs.put("clanTaskId_" + i, cbClanId);
            
            JLabel lblClanCount = new JLabel("Count:");
            JTextField txtClanCount = new JTextField(clanTask.get(idx + 1).getAsString());
            addUndoRedo(txtClanCount);
            inputs.put("clanTaskCount_" + i, txtClanCount);
            
            clanTaskPanel.add(lblClanId); clanTaskPanel.add(cbClanId);
            clanTaskPanel.add(lblClanCount); clanTaskPanel.add(txtClanCount);
        }
        
        JPanel kolTaskPanel = createSectionPanel("Nhiệm vụ KOL");
        kolTaskPanel.setLayout(new GridLayout(0, 2, 5, 5));
        
        JsonArray kolTask = new JsonArray();
        try {
            String kolStr = originalData.get("data_kol_task");
            if (kolStr != null && !kolStr.isEmpty()) {
                kolTask = new JsonParser().parse(kolStr).getAsJsonArray();
            }
        } catch (Exception e) {
            kolTask = new JsonArray();
        }
        while (kolTask.size() < 2) kolTask.add(0);
        
        JLabel lblKolTaskId = new JLabel("ID Nhiệm vụ KOL:");
        JComboBox<String> cbKolTaskId = new JComboBox<>();
        cbKolTaskId.addItem("-1 - Không có");
        for (Map.Entry<Integer, String> entry : kolTaskTemplateMap.entrySet()) {
            cbKolTaskId.addItem(entry.getKey() + " - " + entry.getValue());
        }
        int currentKolId = kolTask.get(0).getAsInt();
        for (int i = 0; i < cbKolTaskId.getItemCount(); i++) {
            String item = cbKolTaskId.getItemAt(i);
            if (item.startsWith(currentKolId + " - ")) {
                cbKolTaskId.setSelectedIndex(i);
                break;
            }
        }
        inputs.put("kolTaskId", cbKolTaskId);
        
        JLabel lblKolTaskCount = new JLabel("Count:");
        JTextField txtKolTaskCount = new JTextField(kolTask.get(1).getAsString());
        addUndoRedo(txtKolTaskCount);
        inputs.put("kolTaskCount", txtKolTaskCount);
        
        kolTaskPanel.add(lblKolTaskId); kolTaskPanel.add(cbKolTaskId);
        kolTaskPanel.add(lblKolTaskCount); kolTaskPanel.add(txtKolTaskCount);
        
        panel.add(mainTaskPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(sideTaskPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(clanTaskPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(kolTaskPanel);
        
        return panel;
    }

    private String getJsonVal(JsonArray arr, int index) {
        if (index < arr.size()) return arr.get(index).getAsString();
        return "0";
    }

    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(new Color(200, 200, 200)), title,
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), Color.DARK_GRAY
        ));
        return p;
    }

    private void addLabelInput(JPanel p, String label, String value, String key, Map<String, Component> map) {
        JPanel row = new JPanel(new BorderLayout(10, 5));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 25));
        JTextField txt = new JTextField(value);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addUndoRedo(txt);
        row.add(lbl, BorderLayout.WEST);
        row.add(txt, BorderLayout.CENTER);
        p.add(row);
        map.put(key, txt);
    }
    
    private void addLabelInputGrid(JPanel p, String label, String value, String key, Map<String, Component> map) {
        JPanel row = new JPanel(new BorderLayout(5, 0));
        JLabel lbl = new JLabel(label);
        JTextField txt = new JTextField(value);
        addUndoRedo(txt);
        row.add(lbl, BorderLayout.NORTH);
        row.add(txt, BorderLayout.CENTER);
        p.add(row);
        map.put(key, txt);
    }

    private JPanel createItemPanel(DefaultTableModel model, JDialog parent) {
        JPanel p = new JPanel(new BorderLayout());
        JTable t = new JTable(model);
        t.setRowHeight(30); 
        t.setShowGrid(true);
        t.setGridColor(COLOR_GRID);
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(40);
        t.getColumnModel().getColumn(2).setPreferredWidth(150);
        t.getColumnModel().getColumn(4).setPreferredWidth(300);
        
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int r = t.getSelectedRow();
                    if(r != -1) openItemDetailEditor(model, r, parent);
                }
            }
        });

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tool.setOpaque(false);
        JButton btnAdd = createStyledButton("Thêm Item", COLOR_PRIMARY, Color.WHITE);
        JButton btnDel = createStyledButton("Xóa Item", Color.RED, Color.WHITE);
        btnAdd.addActionListener(e -> openItemAddDialog(model, parent));
        btnDel.addActionListener(e -> { if(t.getSelectedRow()!=-1) model.removeRow(t.getSelectedRow()); });
        tool.add(btnAdd); tool.add(btnDel);
        p.add(tool, BorderLayout.NORTH);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private void openItemDetailEditor(DefaultTableModel model, int row, JDialog parent) {
        JDialog d = new JDialog(parent, "Chỉnh sửa Vật phẩm", true);
        d.setSize(600, 500); d.setLocationRelativeTo(parent); d.setLayout(new BorderLayout());

        JPanel pTop = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints(); g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;
        
        int currId = Integer.parseInt(model.getValueAt(row, 0).toString());
        String currName = model.getValueAt(row, 2).toString();
        int currQty = Integer.parseInt(model.getValueAt(row, 3).toString());
        String currOpt = model.getValueAt(row, 5).toString();

        JLabel lblIcon = new JLabel(getItemIcon(currId));
        JTextField txtId = new JTextField(String.valueOf(currId), 10); addUndoRedo(txtId);
        JLabel lblName = new JLabel(currName); lblName.setForeground(Color.BLUE);
        JTextField txtQty = new JTextField(String.valueOf(currQty), 10); addUndoRedo(txtQty);
        JButton btnFind = new JButton("🔍");

        g.gridx=0; g.gridy=0; pTop.add(new JLabel("ID Item:"), g);
        g.gridx=1; pTop.add(txtId, g);
        g.gridx=2; pTop.add(btnFind, g);
        
        g.gridx=0; g.gridy=1; pTop.add(new JLabel("Info:"), g);
        JPanel pInfo = new JPanel(new FlowLayout(FlowLayout.LEFT)); pInfo.add(lblIcon); pInfo.add(lblName);
        g.gridx=1; g.gridwidth=2; pTop.add(pInfo, g); g.gridwidth=1;

        g.gridx=0; g.gridy=2; pTop.add(new JLabel("Số lượng:"), g);
        g.gridx=1; pTop.add(txtQty, g);

        txtId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { u(); }
            public void removeUpdate(DocumentEvent e) { u(); }
            public void changedUpdate(DocumentEvent e) { u(); }
            void u() { try { 
                int id = Integer.parseInt(txtId.getText()); 
                lblName.setText(getItemName(id)); 
                lblIcon.setIcon(getItemIcon(id)); 
            } catch(Exception ex) {} }
        });

        btnFind.addActionListener(e -> {
             JDialog sd = new JDialog(d, "Tìm Item", true); sd.setSize(900, 600); 
             sd.setLocationRelativeTo(d); 
             sd.setLayout(new BorderLayout());
             JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
             JTextField st = new JTextField(15);
             JComboBox<String> cbType = new JComboBox<>(new String[]{"- Tất cả -", "0 - Áo", "1 - Quần", "2 - Găng", "3 - Giày", "4 - Rada", "5 - Cải trang", "12 - Ngọc rồng", "27 - Vật phẩm"});
             JComboBox<String> cbGender = new JComboBox<>(new String[]{"- Tất cả -", "0 - Trái đất", "1 - Namếc", "2 - Xayda"});
             pFilter.add(new JLabel("Tên/ID:")); pFilter.add(st); pFilter.add(new JLabel("Loại:")); pFilter.add(cbType); pFilter.add(new JLabel("Hệ:")); pFilter.add(cbGender);
             
             DefaultTableModel sm = new DefaultTableModel(new String[]{"ID", "Icon", "Name", "Type", "Gender"},0) {
                 public Class<?> getColumnClass(int c){return c==1?ImageIcon.class:Object.class;}
                 @Override public boolean isCellEditable(int r, int c) { return false; }
             };
             for(ItemData i : listAllItems) sm.addRow(new Object[]{i.id, getItemIcon(i.id), i.name, i.type, i.gender});
             JTable stab = new JTable(sm); stab.setRowHeight(30); 
             stab.setShowGrid(true);
             stab.setGridColor(COLOR_GRID);
             stab.getColumnModel().getColumn(0).setPreferredWidth(50); stab.getColumnModel().getColumn(1).setPreferredWidth(40);
             stab.getColumnModel().getColumn(3).setMinWidth(0); stab.getColumnModel().getColumn(3).setMaxWidth(0);
             stab.getColumnModel().getColumn(4).setMinWidth(0); stab.getColumnModel().getColumn(4).setMaxWidth(0);
             
             TableRowSorter<DefaultTableModel> ss = new TableRowSorter<>(sm); stab.setRowSorter(ss);
             
             Runnable doFilter = () -> {
                String text = st.getText().trim();
                List<RowFilter<Object, Object>> filters = new ArrayList<>();
                if (!text.isEmpty()) {
                    try {
                        int id = Integer.parseInt(text);
                        List<RowFilter<Object, Object>> orFilters = new ArrayList<>();
                        orFilters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, id, 0));
                        orFilters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                        filters.add(RowFilter.orFilter(orFilters));
                    } catch (NumberFormatException ex) {
                        filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                    }
                }
                if (cbType.getSelectedIndex() > 0) try { filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, Integer.parseInt(cbType.getSelectedItem().toString().split(" - ")[0]), 3)); } catch(Exception ex){}
                if (cbGender.getSelectedIndex() > 0) try { filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, Integer.parseInt(cbGender.getSelectedItem().toString().split(" - ")[0]), 4)); } catch(Exception ex){}
                if (filters.isEmpty()) ss.setRowFilter(null); else ss.setRowFilter(RowFilter.andFilter(filters));
             };
             st.getDocument().addDocumentListener(new DocumentListener() { public void insertUpdate(DocumentEvent e) {doFilter.run();} public void removeUpdate(DocumentEvent e) {doFilter.run();} public void changedUpdate(DocumentEvent e) {doFilter.run();} });
             cbType.addActionListener(e1->doFilter.run()); cbGender.addActionListener(e1->doFilter.run());

             stab.addMouseListener(new MouseAdapter(){
                 public void mouseClicked(MouseEvent e){
                     if(e.getClickCount()==2){
                         int viewRow = stab.getSelectedRow();
                         if(viewRow != -1) {
                             int modelRow = stab.convertRowIndexToModel(viewRow);
                             int mid = (int) sm.getValueAt(modelRow, 0);
                             txtId.setText(String.valueOf(mid)); 
                             sd.dispose(); 
                         }
                     }
                 }
             });
             sd.add(pFilter, BorderLayout.NORTH); sd.add(new JScrollPane(stab), BorderLayout.CENTER); sd.setVisible(true);
        });

        String[] optCols = {"ID Option", "Param", "Mô tả"};
        DefaultTableModel optModel = new DefaultTableModel(optCols, 0);
        try {
            JsonArray arr = new JsonParser().parse(currOpt).getAsJsonArray();
            for(JsonElement el : arr) {
                JsonArray o = el.getAsJsonArray();
                int oid = o.get(0).getAsInt(); int op = o.get(1).getAsInt();
                optModel.addRow(new Object[]{oid, op, formatOption(oid, op)});
            }
        } catch(Exception ex) {}
        
        JTable optTable = new JTable(optModel);
        optTable.setRowHeight(25);
        optTable.setShowGrid(true);
        optTable.setGridColor(COLOR_GRID);
        optModel.addTableModelListener(e -> {
            int r = e.getFirstRow(); int c = e.getColumn();
            if(r>=0 && r<optModel.getRowCount() && (c==0 || c==1)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int oid = Integer.parseInt(optModel.getValueAt(r, 0).toString());
                        int op = Integer.parseInt(optModel.getValueAt(r, 1).toString());
                        optModel.setValueAt(formatOption(oid, op), r, 2);
                    } catch(Exception ex){}
                });
            }
        });
        
        JPanel pOptTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddOpt = new JButton("Thêm Opt");
        JButton btnFindOpt = new JButton("Tìm Opt");
        JButton btnDelOpt = new JButton("Xóa Opt");
        
        btnAddOpt.addActionListener(e -> optModel.addRow(new Object[]{0, 0, getOptionName(0)}));
        
        btnFindOpt.addActionListener(e -> {
             JDialog fod = new JDialog(d, "Tìm Option", true); fod.setSize(400, 500); 
             fod.setLocationRelativeTo(d); 
             fod.setLayout(new BorderLayout());
             JTextField tf = new JTextField(); 
             
             DefaultTableModel om = new DefaultTableModel(new String[]{"ID", "Name"},0) {
                 @Override public boolean isCellEditable(int r, int c) { return false; }
             };
             optionTemplateMap.forEach((k,v)->om.addRow(new Object[]{k,v}));
             JTable ot = new JTable(om); 
             ot.setShowGrid(true);
             ot.setGridColor(COLOR_GRID);
             TableRowSorter<DefaultTableModel> os = new TableRowSorter<>(om); ot.setRowSorter(os);
             tf.getDocument().addDocumentListener(new DocumentListener() {
                 public void insertUpdate(DocumentEvent e) {f();} public void removeUpdate(DocumentEvent e) {f();} public void changedUpdate(DocumentEvent e) {f();}
                 void f() { String tx=tf.getText(); if(tx.isEmpty()) os.setRowFilter(null); else os.setRowFilter(RowFilter.regexFilter("(?i)"+Pattern.quote(tx))); }
             });
             ot.addMouseListener(new MouseAdapter(){
                 public void mouseClicked(MouseEvent e){
                     if(e.getClickCount()==2){
                         int viewRow = ot.getSelectedRow();
                         if(viewRow != -1) {
                             int modelRow = ot.convertRowIndexToModel(viewRow);
                             int oid = (int) om.getValueAt(modelRow, 0);
                             optModel.addRow(new Object[]{oid, 0, getOptionName(oid).replace("#", "0")});
                             fod.dispose(); 
                         }
                     }
                 }
             });
             fod.add(tf, BorderLayout.NORTH); fod.add(new JScrollPane(ot), BorderLayout.CENTER); fod.setVisible(true);
        });
        
        btnDelOpt.addActionListener(e -> { if(optTable.getSelectedRow()!=-1) optModel.removeRow(optTable.getSelectedRow()); });
        pOptTool.add(btnAddOpt); pOptTool.add(btnFindOpt); pOptTool.add(btnDelOpt);

        JPanel pCenter = new JPanel(new BorderLayout());
        pCenter.setBorder(new TitledBorder("Options"));
        pCenter.add(pOptTool, BorderLayout.NORTH);
        pCenter.add(new JScrollPane(optTable), BorderLayout.CENTER);

        JButton btnSave = createStyledButton("Lưu thay đổi", COLOR_SUCCESS, Color.WHITE);
        btnSave.addActionListener(e -> {
            JsonArray newArr = new JsonArray();
            for(int i=0; i<optModel.getRowCount(); i++){
                JsonArray o = new JsonArray();
                o.add(Integer.parseInt(optModel.getValueAt(i,0).toString()));
                o.add(Integer.parseInt(optModel.getValueAt(i,1).toString()));
                newArr.add(o);
            }
            int newId = Integer.parseInt(txtId.getText());
            model.setValueAt(newId, row, 0);
            model.setValueAt(getItemIcon(newId), row, 1);
            model.setValueAt(lblName.getText(), row, 2);
            model.setValueAt(Integer.parseInt(txtQty.getText()), row, 3);
            model.setValueAt(parseOptionReadable(newArr.toString()), row, 4);
            model.setValueAt(newArr.toString(), row, 5); 
            d.dispose();
        });

        d.add(pTop, BorderLayout.NORTH);
        d.add(pCenter, BorderLayout.CENTER);
        d.add(btnSave, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private DefaultTableModel createItemModel() {
        return new DefaultTableModel(new String[]{"ID", "Icon", "Tên Item", "SL", "Options (Readable)", "Raw Options"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } 
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class; 
                return Object.class;
            }
        };
    }

    private void loadItemsToModel(String jsonArrayStr, DefaultTableModel model) {
        try {
            JsonArray arr = new JsonParser().parse(jsonArrayStr).getAsJsonArray();
            for (JsonElement e : arr) {
                String innerStr = e.getAsString();
                JsonArray itemData = new JsonParser().parse(innerStr).getAsJsonArray();
                int id = itemData.get(0).getAsInt();
                if (id == -1) continue;
                int qty = itemData.get(1).getAsInt();
                String rawOpt = (itemData.size() > 2) ? itemData.get(2).getAsString() : "[]";
                model.addRow(new Object[]{id, getItemIcon(id), getItemName(id), qty, parseOptionReadable(rawOpt), rawOpt});
            }
        } catch (Exception e) {}
    }
    
    private String parseOptionReadable(String jsonOpt) {
        try {
            StringBuilder sb = new StringBuilder();
            JsonArray arr = new JsonParser().parse(jsonOpt).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonArray opt = e.getAsJsonArray();
                int id = opt.get(0).getAsInt();
                int param = opt.get(1).getAsInt();
                sb.append(formatOption(id, param)).append(", ");
            }
            if (sb.length() > 2) return sb.substring(0, sb.length() - 2);
        } catch (Exception e) { return jsonOpt; }
        return "";
    }

    private void openItemAddDialog(DefaultTableModel model, JDialog parent) {
        JDialog d = new JDialog(parent, "Thêm Vật Phẩm", true);
        d.setSize(900, 600); d.setLayout(new BorderLayout()); 
        d.setLocationRelativeTo(parent);

        JPanel pFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pFilter.setBorder(BorderFactory.createTitledBorder("Bộ Lọc"));
        JTextField txtSearch = new JTextField(15);
        String[] types = {"- Tất cả Loại -", "0 - Áo", "1 - Quần", "2 - Găng", "3 - Giày", "4 - Rada", "5 - Cải trang/Tóc", "6 - Đậu thần", "12 - Ngọc rồng", "27 - Vật phẩm", "29 - Capsule/Bánh", "32 - Giáp tập"};
        JComboBox<String> cbType = new JComboBox<>(types);
        String[] genders = {"- Tất cả Hệ -", "0 - Trái Đất", "1 - Namếc", "2 - Xayda", "3 - Chung/Tất cả"};
        JComboBox<String> cbGender = new JComboBox<>(genders);
        pFilter.add(new JLabel("Tên/ID:")); pFilter.add(txtSearch); pFilter.add(new JLabel(" | Loại:")); pFilter.add(cbType); pFilter.add(new JLabel(" | Hệ:")); pFilter.add(cbGender);

        DefaultTableModel searchModel = new DefaultTableModel(new String[]{"ID", "Icon", "Tên Item", "Type", "Gender"}, 0) {
             @Override public Class<?> getColumnClass(int c) { return c==1 ? ImageIcon.class : Object.class; }
             @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        for (ItemData item : listAllItems) searchModel.addRow(new Object[]{item.id, getItemIcon(item.id), item.name, item.type, item.gender});
        
        JTable t = new JTable(searchModel); t.setRowHeight(30);
        t.setShowGrid(true);
        t.setGridColor(COLOR_GRID);
        t.getColumnModel().getColumn(0).setPreferredWidth(50); t.getColumnModel().getColumn(1).setPreferredWidth(40); t.getColumnModel().getColumn(2).setPreferredWidth(350);
        t.getColumnModel().getColumn(3).setMinWidth(0); t.getColumnModel().getColumn(3).setMaxWidth(0);
        t.getColumnModel().getColumn(4).setMinWidth(0); t.getColumnModel().getColumn(4).setMaxWidth(0);
        
        TableRowSorter<DefaultTableModel> s = new TableRowSorter<>(searchModel); t.setRowSorter(s);

        Runnable doFilter = () -> {
            String text = txtSearch.getText().trim();
            int typeIdx = cbType.getSelectedIndex();
            int genderIdx = cbGender.getSelectedIndex();
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            
            if (!text.isEmpty()) {
                try {
                    int id = Integer.parseInt(text);
                    List<RowFilter<Object, Object>> orFilters = new ArrayList<>();
                    orFilters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, id, 0));
                    orFilters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                    filters.add(RowFilter.orFilter(orFilters));
                } catch (NumberFormatException e) {
                    filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
                }
            }
            
            if (typeIdx > 0) { try { int val = Integer.parseInt(cbType.getSelectedItem().toString().split(" - ")[0]); filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 3)); } catch (Exception e) {} }
            if (genderIdx > 0) { try { int val = Integer.parseInt(cbGender.getSelectedItem().toString().split(" - ")[0]); filters.add(RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, val, 4)); } catch (Exception e) {} }
            if (filters.isEmpty()) s.setRowFilter(null); else s.setRowFilter(RowFilter.andFilter(filters));
        };

        txtSearch.getDocument().addDocumentListener(new DocumentListener() { public void insertUpdate(DocumentEvent e) { doFilter.run(); } public void removeUpdate(DocumentEvent e) { doFilter.run(); } public void changedUpdate(DocumentEvent e) { doFilter.run(); } });
        cbType.addActionListener(e -> doFilter.run()); cbGender.addActionListener(e -> doFilter.run());

        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = t.getSelectedRow();
                    if(r!=-1) {
                         int modelRow = t.convertRowIndexToModel(r);
                         int id = (int) searchModel.getValueAt(modelRow, 0);
                         ImageIcon icon = (ImageIcon) searchModel.getValueAt(modelRow, 1);
                         String name = (String) searchModel.getValueAt(modelRow, 2);
                         model.addRow(new Object[]{id, icon, name, 1, "", "[]"});
                         d.dispose();
                    }
                }
            }
        });

        d.add(pFilter, BorderLayout.NORTH); d.add(new JScrollPane(t), BorderLayout.CENTER); d.setVisible(true);
    }

    private void savePlayerDB(int pid, int accountId, Map<String, Component> inputs, 
                              DefaultTableModel mBody, DefaultTableModel mBag, DefaultTableModel mBox, 
                              DefaultTableModel mBadges,
                              Map<String, String> originalData, JDialog d) {
        new Thread(() -> {
            try {
                JsonArray inv = new JsonArray();
                inv.add(getLongVal(inputs, "gold"));
                inv.add(getLongVal(inputs, "gem"));
                inv.add(getLongVal(inputs, "ruby"));
                JsonArray oldInv = new JsonParser().parse(originalData.get("data_inventory")).getAsJsonArray();
                for(int i = 3; i < oldInv.size(); i++) {
                    inv.add(oldInv.get(i));
                }

                JsonArray point = new JsonParser().parse(originalData.get("data_point")).getAsJsonArray();
                setVal(point, 1, getText(inputs, "power"));
                setVal(point, 2, getText(inputs, "tiemnang"));
                setVal(point, 5, getText(inputs, "hpg"));
                setVal(point, 6, getText(inputs, "mpg"));
                setVal(point, 7, getText(inputs, "dameg"));
                setVal(point, 8, getText(inputs, "defg"));
                setVal(point, 9, getText(inputs, "critg"));

                String jsonBody, jsonBag, jsonBox;
                try {
                    jsonBody = mergeItemToDB(mBody, originalData.get("items_body"), "Body/Đồ mặc");
                    jsonBag = mergeItemToDB(mBag, originalData.get("items_bag"), "Hành trang");
                    jsonBox = mergeItemToDB(mBox, originalData.get("items_box"), "Rương đồ");
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(d, ex.getMessage(), "Lỗi đây rồi", JOptionPane.ERROR_MESSAGE));
                    return; 
                }

                String petJson = originalData.get("pet");
                if (petJson != null && !petJson.equals("[]") && inputs.containsKey("pet_name")) {
                    JsonArray petArr = new JsonParser().parse(petJson).getAsJsonArray();
                    if(petArr.size() > 1) {
                        JsonArray infoArr = new JsonParser().parse(petArr.get(0).getAsString()).getAsJsonArray();
                        String typeStr = ((JComboBox)inputs.get("pet_type")).getSelectedItem().toString();
                        if(typeStr.contains(" - ")) typeStr = typeStr.split(" - ")[0];
                        setVal(infoArr, 0, typeStr);
                        setVal(infoArr, 1, String.valueOf(((JComboBox)inputs.get("pet_gender")).getSelectedIndex()));
                        setVal(infoArr, 2, getText(inputs, "pet_name"));
                        setVal(infoArr, 5, String.valueOf(((JComboBox)inputs.get("pet_status")).getSelectedIndex()));
                        petArr.set(0, new JsonPrimitive(infoArr.toString()));

                        JsonArray pointArr = new JsonParser().parse(petArr.get(1).getAsString()).getAsJsonArray();
                        setVal(pointArr, 1, getText(inputs, "pet_power"));
                        setVal(pointArr, 2, getText(inputs, "pet_tiemnang"));
                        setVal(pointArr, 5, getText(inputs, "pet_hpg"));
                        setVal(pointArr, 6, getText(inputs, "pet_mpg"));
                        setVal(pointArr, 7, getText(inputs, "pet_dameg"));
                        setVal(pointArr, 8, getText(inputs, "pet_defg"));
                        setVal(pointArr, 9, getText(inputs, "pet_critg"));
                        petArr.set(1, new JsonPrimitive(pointArr.toString()));
                        petJson = petArr.toString();
                    }
                }

                String dataTaskJson = createTaskJson(inputs, "mainTaskId", "mainTaskIndex", "mainTaskCount", "mainTaskLastTime", 4);
                String sideTaskJson = createMultiTaskJson(inputs, "sideTaskId_", "sideTaskCount_", 3);
                String clanTaskJson = createMultiTaskJson(inputs, "clanTaskId_", "clanTaskCount_", 3);
                String kolTaskJson = createTaskJson(inputs, "kolTaskId", null, "kolTaskCount", null, 2);
                
                JsonArray badgesArr = new JsonArray();
                for(int i = 0; i < mBadges.getRowCount(); i++) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("idBadGes", Integer.parseInt(mBadges.getValueAt(i, 0).toString()));
                    obj.addProperty("timeofUseBadges", Long.parseLong(mBadges.getValueAt(i, 4).toString()));
                    obj.addProperty("isUse", Boolean.parseBoolean(mBadges.getValueAt(i, 6).toString()));
                    badgesArr.add(obj);
                }
                String dataBadgesJson = badgesArr.toString();

                String sqlPlayer = "UPDATE player SET name=?, head=?, data_inventory=?, data_point=?, items_body=?, items_bag=?, items_box=?, pet=?, data_task=?, data_side_task=?, data_clan_task=?, data_kol_task=?, dataBadges=? WHERE id=?";
                try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sqlPlayer)) {
                    ps.setString(1, getText(inputs, "name"));
                    ps.setInt(2, Integer.parseInt(getText(inputs, "head")));
                    ps.setString(3, inv.toString());
                    ps.setString(4, point.toString());
                    ps.setString(5, jsonBody);
                    ps.setString(6, jsonBag);
                    ps.setString(7, jsonBox);
                    ps.setString(8, petJson);
                    ps.setString(9, dataTaskJson);
                    ps.setString(10, sideTaskJson);
                    ps.setString(11, clanTaskJson);
                    ps.setString(12, kolTaskJson);
                    ps.setString(13, dataBadgesJson);
                    ps.setInt(14, pid);

                    ps.executeUpdate();
                }

                String fullUpdateCommand = "FULL_UPDATE:" + pid + ":INV:" + inv.toString() + ":POINT:" + point.toString() + ":BODY:" + jsonBody + 
                                                          ":BAG:" + jsonBag + ":BOX:" + jsonBox + ":PET:" + petJson + ":TASK:" + dataTaskJson + 
                                                          ":SIDE_TASK:" + sideTaskJson + ":CLAN_TASK:" + clanTaskJson + ":KOL_TASK:" + kolTaskJson + 
                                                          ":BADGES:" + dataBadgesJson + ":NAME:" + getText(inputs, "name") + ":POWER:" + getText(inputs, "power") + ":HEAD:" + getText(inputs, "head");
                sendCommandToServer(fullUpdateCommand);

                SwingUtilities.invokeLater(() -> { 
                    JOptionPane.showMessageDialog(d, "Đã lưu thành công và gửi lệnh cập nhật đầy đủ đến server!"); 
                    d.dispose(); 
                    loadPlayersFromDB(""); 
                });
            } catch (Exception e) { 
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(d, "Lỗi lưu: " + e.getMessage())); 
            }
        }).start();
    }

    private String createTaskJson(Map<String, Component> inputs, String idKey, String indexKey, String countKey, String timeKey, int size) {
        JsonArray arr = new JsonArray();
        
        int id = -1;
        Component idComp = inputs.get(idKey);
        if (idComp instanceof JComboBox) {
            String selected = ((JComboBox)idComp).getSelectedItem().toString();
            if (selected.contains(" - ")) {
                try {
                    id = Integer.parseInt(selected.split(" - ")[0]);
                } catch (NumberFormatException e) {
                    id = -1;
                }
            }
        }
        arr.add(id);
        
        if (indexKey != null && inputs.containsKey(indexKey)) {
            try {
                int index = Integer.parseInt(((JTextField)inputs.get(indexKey)).getText());
                arr.add(index);
            } catch (Exception e) {
                arr.add(0);
            }
        } else if (size > 1) {
            arr.add(0);
        }
        
        if (countKey != null && inputs.containsKey(countKey)) {
            try {
                int count = Integer.parseInt(((JTextField)inputs.get(countKey)).getText());
                arr.add(count);
            } catch (Exception e) {
                arr.add(0);
            }
        } else if (size > 2) {
            arr.add(0);
        }
        
        if (timeKey != null && inputs.containsKey(timeKey)) {
            try {
                long lastTime = Long.parseLong(((JTextField)inputs.get(timeKey)).getText());
                arr.add(lastTime);
            } catch (Exception e) {
                arr.add(System.currentTimeMillis());
            }
        } else if (size > 3) {
            arr.add(System.currentTimeMillis());
        }
        
        while (arr.size() < size) {
            arr.add(0);
        }
        
        return arr.toString();
    }

    private String createMultiTaskJson(Map<String, Component> inputs, String idPrefix, String countPrefix, int count) {
        JsonArray arr = new JsonArray();
        
        for (int i = 0; i < count; i++) {
            String idKey = idPrefix + i;
            String countKey = countPrefix + i;
            
            int id = -1;
            Component idComp = inputs.get(idKey);
            if (idComp instanceof JComboBox) {
                String selected = ((JComboBox)idComp).getSelectedItem().toString();
                if (selected.contains(" - ")) {
                    try {
                        id = Integer.parseInt(selected.split(" - ")[0]);
                    } catch (NumberFormatException e) {
                        id = -1;
                    }
                }
            }
            arr.add(id);
            
            int taskCount = 0;
            Component countComp = inputs.get(countKey);
            if (countComp instanceof JTextField) {
                try {
                    taskCount = Integer.parseInt(((JTextField)countComp).getText());
                } catch (Exception e) {
                    taskCount = 0;
                }
            }
            arr.add(taskCount);
        }
        
        return arr.toString();
    }

    private String mergeItemToDB(DefaultTableModel model, String originalJson, String typeName) throws Exception {
        JsonArray dbArr;
        try {
            if (originalJson == null || originalJson.isEmpty()) dbArr = new JsonArray();
            else dbArr = new JsonParser().parse(originalJson).getAsJsonArray();
        } catch (Exception e) {
            dbArr = new JsonArray();
        }

        int maxSlots = dbArr.size();
        int itemsInTable = model.getRowCount();

        if (itemsInTable > maxSlots) {
            throw new Exception("Lỗi: " + typeName + " đã bị ĐẦY!\nSố lượng hiện tại: " + itemsInTable + "\nSức chứa tối đa: " + maxSlots + "\nVui lòng xóa bớt item trước khi lưu.");
        }

        for (int i = 0; i < maxSlots; i++) {
            if (i < itemsInTable) {
                try {
                    int id = Integer.parseInt(model.getValueAt(i, 0).toString());
                    int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
                    String rawOpt = model.getValueAt(i, 5).toString();

                    JsonArray itemNode = new JsonArray();
                    itemNode.add(id);
                    itemNode.add(qty);
                    itemNode.add(rawOpt); 
                    itemNode.add(System.currentTimeMillis()); 

                    dbArr.set(i, new JsonPrimitive(itemNode.toString()));
                } catch (Exception e) {
                    dbArr.set(i, new JsonPrimitive(createEmptyItem()));
                }
            } else {
                dbArr.set(i, new JsonPrimitive(createEmptyItem()));
            }
        }
        return dbArr.toString();
    }

    private String createEmptyItem() {
        JsonArray emptyNode = new JsonArray();
        emptyNode.add(-1);
        emptyNode.add(0);
        emptyNode.add("[]");
        emptyNode.add(System.currentTimeMillis());
        return emptyNode.toString();
    }

    private String getText(Map<String, Component> inputs, String key) {
        Component c = inputs.get(key);
        if (c instanceof JTextField) return ((JTextField)c).getText();
        return "0";
    }
    
    private long getLongVal(Map<String, Component> inputs, String key) {
        try {
            String txt = ((JTextField)inputs.get(key)).getText();
            return Long.parseLong(txt.replaceAll("[^0-9-]", ""));
        } catch(Exception e) { return 0; }
    }

    private void setVal(JsonArray arr, int index, String val) {
        while (arr.size() <= index) {
            arr.add(new JsonPrimitive(0));
        }
        try {
            String cleanVal = val.replaceAll("[^0-9-]", "");
            long v = Long.parseLong(cleanVal);
            arr.set(index, new JsonPrimitive(v));
        } catch (Exception e) {
            arr.set(index, new JsonPrimitive(val));
        }
    }
    
    private static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}