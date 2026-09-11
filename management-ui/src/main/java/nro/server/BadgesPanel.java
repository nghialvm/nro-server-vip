package nro.server;

import Data.DataGame;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import jbcd.ConnectDB;


public class BadgesPanel extends JPanel {

    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private final Color COLOR_PRIMARY = new Color(0, 120, 215);
    private final Color COLOR_SUCCESS = new Color(40, 167, 69);
    
    private final Map<Integer, String> optionTemplateMap = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();

    public BadgesPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        loadCacheData();

        initTopControls();
        initTable();
        loadBadges("");
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }
    
    private void loadCacheData() {
        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM item_option_template")) {
                    while (rs.next()) {
                        optionTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                    }
                }
                if(optionTemplateMap.isEmpty()){
                      String raw = "0,Tấn công +#;50,Sức đánh +#%;77,HP +#%;103,KI +#%;14,Chí mạng +#%;30,Khóa giao dịch;93,Hạn sử dụng # ngày;73,Không thể bán;9,Hiệu lực # phút";
                      for (String s : raw.split(";")) {
                        String[] p = s.split(",");
                        if(p.length==2) optionTemplateMap.put(Integer.parseInt(p[0]), p[1]);
                      }
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

    private String getOptionName(int id) { return optionTemplateMap.getOrDefault(id, "Option " + id); }
    private String formatOption(int id, int param) { return getOptionName(id).replace("#", String.valueOf(param)); }

    private String parseBadgeOptions(String jsonOpt) {
        if(jsonOpt == null || jsonOpt.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        try {
            JsonElement element = new JsonParser().parse(jsonOpt);
            if (element.isJsonArray()) {
                JsonArray arr = element.getAsJsonArray();
                for(JsonElement e : arr) {
                    if (e.isJsonObject()) {
                        int id = e.getAsJsonObject().get("id").getAsInt();
                        int param = e.getAsJsonObject().get("param").getAsInt();
                        sb.append(formatOption(id, param)).append(", ");
                    }
                }
            }
        } catch(Exception e) { return jsonOpt; }
        if (sb.length() > 2) return sb.substring(0, sb.length() - 2);
        return sb.toString();
    }

    private void initTopControls() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(0, 0, 10, 0));

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo tên danh hiệu...");
        txtSearch.setPreferredSize(new Dimension(200, 35));
        
        JButton btnSearch = createStyledButton("Tìm kiếm", COLOR_PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> loadBadges(txtSearch.getText().trim()));

        JButton btnAdd = createStyledButton("Thêm Danh Hiệu", COLOR_SUCCESS, Color.WHITE);
        btnAdd.addActionListener(e -> openEditor(-1)); 

        JButton btnReload = createStyledButton("Tải lại", Color.GRAY, Color.WHITE);
        btnReload.addActionListener(e -> {
            iconCache.clear(); 
            loadBadges("");
        });

        top.add(txtSearch);
        top.add(btnSearch);
        top.add(btnAdd);
        top.add(btnReload);

        add(top, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = {"ID", "Icon", "Tên Danh Hiệu", "ID Effect", "ID Item", "Chỉ số (Hiển thị)", "Raw Options"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                if (columnIndex == 0 || columnIndex == 3 || columnIndex == 4) return Integer.class;
                return Object.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(230, 240, 255));
        
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50); 
        table.getColumnModel().getColumn(1).setPreferredWidth(50); 
        table.getColumnModel().getColumn(2).setPreferredWidth(200); 
        table.getColumnModel().getColumn(5).setPreferredWidth(300); 
        
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setPreferredWidth(0);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int id = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString());
                        openEditor(id);
                    }
                }
                if (SwingUtilities.isRightMouseButton(e)) { 
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < table.getRowCount()) {
                        table.setRowSelectionInterval(row, row);
                        JPopupMenu menu = new JPopupMenu();
                        JMenuItem mEdit = new JMenuItem("Chỉnh sửa");
                        mEdit.addActionListener(ev -> openEditor(Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString())));
                        
                        JMenuItem mDel = new JMenuItem("Xóa Danh Hiệu này");
                        mDel.setForeground(Color.RED);
                        mDel.addActionListener(ev -> deleteBadge(Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString())));
                        
                        menu.add(mEdit);
                        menu.addSeparator();
                        menu.add(mDel);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(new Color(220, 220, 220)));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadBadges(String keyword) {
        new Thread(() -> {
            String sql = "SELECT b.*, i.icon_id FROM data_badges b LEFT JOIN item_template i ON b.idItem = i.id";
            if (!keyword.isEmpty()) {
                sql += " WHERE b.NAME LIKE '%" + keyword + "%'";
            }
            
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                SwingUtilities.invokeLater(() -> model.setRowCount(0));
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    
                    int iconId = rs.getInt("icon_id");
                    row.add(loadIconRaw(iconId)); 
                    
                    row.add(rs.getString("NAME"));
                    row.add(rs.getInt("idEffect"));
                    row.add(rs.getInt("idItem"));
                    
                    String rawOpt = rs.getString("Options");
                    row.add(parseBadgeOptions(rawOpt)); 
                    row.add(rawOpt);
                    
                    SwingUtilities.invokeLater(() -> model.addRow(row));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private void deleteBadge(int id) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa Badge ID: " + id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM data_badges WHERE id = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadBadges("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage());
            }
        }
    }

    private void openEditor(int id) {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), id == -1 ? "Thêm Danh Hiệu Mới" : "Chỉnh Sửa Danh Hiệu ID: " + id, true);
        d.setSize(700, 550);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JPanel pInfo = new JPanel(new GridLayout(0, 2, 10, 10));
        pInfo.setBorder(new TitledBorder("Thông tin cơ bản"));

        JTextField txtName = new JTextField();
        JTextField txtEffect = new JTextField("0");
        JTextField txtItem = new JTextField("-1");

        pInfo.add(new JLabel("Tên Danh Hiệu:")); pInfo.add(txtName);
        pInfo.add(new JLabel("ID Effect (Hiệu ứng):")); pInfo.add(txtEffect);
        pInfo.add(new JLabel("ID Item (Vật phẩm):")); pInfo.add(txtItem);
        
        JPanel pIconPreview = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblIconPreview = new JLabel("Chưa có icon");
        pIconPreview.add(lblIconPreview);
        
        txtItem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                try { lblIconPreview.setText("ID Item: " + txtItem.getText()); } catch(Exception e){}
            }
        });
        pInfo.add(new JLabel("Preview Icon:")); pInfo.add(pIconPreview);

        JPanel pOptions = new JPanel(new BorderLayout());
        pOptions.setBorder(new TitledBorder("Chỉnh sửa chỉ số (Options)"));

        DefaultTableModel optModel = new DefaultTableModel(new String[]{"ID Option", "Param (Chỉ số)", "Mô tả tự động"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return column == 0 || column == 1; }
        };
        JTable optTable = new JTable(optModel);
        optTable.setRowHeight(25);
        optTable.setShowGrid(true);
        optTable.setGridColor(Color.LIGHT_GRAY);
        
        optModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && row < optModel.getRowCount() && (col == 0 || col == 1)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int oid = Integer.parseInt(optModel.getValueAt(row, 0).toString());
                        int param = Integer.parseInt(optModel.getValueAt(row, 1).toString());
                        optModel.setValueAt(formatOption(oid, param), row, 2);
                    } catch (Exception ex) {}
                });
            }
        });

        JPanel pOptTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAddOpt = new JButton("Thêm dòng");
        JButton btnFindOpt = new JButton("Tìm Opt");
        JButton btnDelOpt = new JButton("Xóa dòng");
        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");

        btnAddOpt.addActionListener(e -> optModel.addRow(new Object[]{0, 0, getOptionName(0)}));
        btnDelOpt.addActionListener(e -> {
            int r = optTable.getSelectedRow();
            if(r != -1) optModel.removeRow(r);
        });

        btnFindOpt.addActionListener(e -> openOptionFinder(d, optModel));

        btnUp.addActionListener(e -> {
            int r = optTable.getSelectedRow();
            if (r > 0) {
                optModel.moveRow(r, r, r - 1);
                optTable.setRowSelectionInterval(r - 1, r - 1);
            }
        });

        btnDown.addActionListener(e -> {
            int r = optTable.getSelectedRow();
            if (r != -1 && r < optModel.getRowCount() - 1) {
                optModel.moveRow(r, r, r + 1);
                optTable.setRowSelectionInterval(r + 1, r + 1);
            }
        });

        pOptTool.add(btnAddOpt);
        pOptTool.add(btnFindOpt);
        pOptTool.add(btnDelOpt);
        pOptTool.add(new JSeparator(JSeparator.VERTICAL));
        pOptTool.add(btnUp);
        pOptTool.add(btnDown);

        pOptions.add(pOptTool, BorderLayout.NORTH);
        pOptions.add(new JScrollPane(optTable), BorderLayout.CENTER);

        if (id != -1) {
            try (Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM data_badges WHERE id = " + id)) {
                if (rs.next()) {
                    txtName.setText(rs.getString("NAME"));
                    txtEffect.setText(String.valueOf(rs.getInt("idEffect")));
                    txtItem.setText(String.valueOf(rs.getInt("idItem")));
                    
                    String jsonOpt = rs.getString("Options");
                    try {
                        JsonArray arr = new JsonParser().parse(jsonOpt).getAsJsonArray();
                        for (JsonElement el : arr) {
                            if(el.isJsonObject()) {
                                int oid = el.getAsJsonObject().get("id").getAsInt();
                                int param = el.getAsJsonObject().get("param").getAsInt();
                                optModel.addRow(new Object[]{oid, param, formatOption(oid, param)});
                            }
                        }
                    } catch(Exception ex) {}
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createStyledButton("LƯU DỮ LIỆU", COLOR_SUCCESS, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(150, 40));
        
        btnSave.addActionListener(e -> {
            try (Connection conn = getConnection()) {
                String sql;
                if (id == -1) sql = "INSERT INTO data_badges (NAME, idEffect, idItem, Options) VALUES (?, ?, ?, ?)";
                else sql = "UPDATE data_badges SET NAME=?, idEffect=?, idItem=?, Options=? WHERE id=?";
                
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, txtName.getText());
                    ps.setInt(2, Integer.parseInt(txtEffect.getText().trim()));
                    ps.setInt(3, Integer.parseInt(txtItem.getText().trim()));
                    
                    JsonArray jsonArr = new JsonArray();
                    for(int i = 0; i < optModel.getRowCount(); i++) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("id", Integer.parseInt(optModel.getValueAt(i, 0).toString()));
                        obj.addProperty("param", Integer.parseInt(optModel.getValueAt(i, 1).toString()));
                        jsonArr.add(obj);
                    }
                    ps.setString(4, jsonArr.toString());
                    
                    if (id != -1) ps.setInt(5, id);
                    
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(d, "Lưu thành công!");
                    d.dispose();
                    loadBadges("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Lỗi: " + ex.getMessage(), "Lỗi Save", JOptionPane.ERROR_MESSAGE);
            }
        });
        pBtn.add(btnSave);

        JPanel pTopContainer = new JPanel(new BorderLayout());
        pTopContainer.add(pInfo, BorderLayout.CENTER);
        
        d.add(pTopContainer, BorderLayout.NORTH);
        d.add(pOptions, BorderLayout.CENTER);
        d.add(pBtn, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    private void openOptionFinder(JDialog parent, DefaultTableModel targetModel) {
        JDialog d = new JDialog(parent, "Tìm kiếm Option", true);
        d.setSize(450, 500);
        d.setLocationRelativeTo(parent);
        d.setLayout(new BorderLayout());

        JTextField tfSearch = new JTextField();
        d.add(tfSearch, BorderLayout.NORTH);

        DefaultTableModel searchModel = new DefaultTableModel(new String[]{"ID", "Tên Option"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        optionTemplateMap.forEach((k, v) -> searchModel.addRow(new Object[]{k, v}));

        JTable tSearch = new JTable(searchModel);
        tSearch.setShowGrid(true);
        tSearch.setGridColor(Color.LIGHT_GRAY);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(searchModel);
        tSearch.setRowSorter(sorter);
        
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String text = tfSearch.getText();
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
        });

        tSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int viewRow = tSearch.getSelectedRow();
                    if (viewRow != -1) {
                        int modelRow = tSearch.convertRowIndexToModel(viewRow);
                        int id = (int) searchModel.getValueAt(modelRow, 0);
                        targetModel.addRow(new Object[]{id, 0, getOptionName(id).replace("#", "0")});
                        d.dispose();
                    }
                }
            }
        });

        d.add(new JScrollPane(tSearch), BorderLayout.CENTER);
        d.setVisible(true);
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