package nro.server;


import Data.DataGame;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jbcd.ConnectDB;


public class ItemTemplatePanel extends JPanel {

    // Tên bảng và cột
    private static final String TABLE_PARTS = "part"; 
    private static final String COLUMN_PART_DATA = "DATA"; 
    
    private static final String TABLE_HEAD_AVATAR = "head_avatar";
    private static final String TABLE_HEAD_FRAMES = "array_head_2_frames";

    // --- UI COMPONENTS ---
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();
    
    private final ScheduledExecutorService searchExecutor = Executors.newSingleThreadScheduledExecutor();
    private java.util.concurrent.ScheduledFuture<?> searchTask;

    // --- COLORS & FONTS ---
    private final Color COL_PRIMARY = new Color(0, 120, 215);
    private final Color COL_SUCCESS = new Color(30, 160, 60);
    private final Color COL_DANGER = new Color(220, 53, 69);
    private final Color COL_TABLE_HEAD = new Color(240, 242, 245);
    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    public ItemTemplatePanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initTopControls();
        initTable();
        loadData("");
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    // ========================================================================
    //                             PHẦN 1: MAIN UI
    // ========================================================================

    private void initTopControls() {
        JPanel top = new JPanel(new BorderLayout(10, 0));
        top.setOpaque(false);

        JLabel lblTitle = new JLabel("QUẢN LÝ ITEM & DATA");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(COL_PRIMARY);

        JPanel pRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pRight.setOpaque(false);

        // -- Search Box --
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên hoặc ID Item...");
        txtSearch.setPreferredSize(new Dimension(250, 45));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true), 
            new EmptyBorder(5, 10, 5, 10))
        );
        
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { scheduleSearch(); }
            public void removeUpdate(DocumentEvent e) { scheduleSearch(); }
            public void changedUpdate(DocumentEvent e) { scheduleSearch(); }
            private void scheduleSearch() {
                if (searchTask != null && !searchTask.isDone()) searchTask.cancel(false);
                searchTask = searchExecutor.schedule(() -> loadData(txtSearch.getText().trim()), 300, TimeUnit.MILLISECONDS);
            }
        });

        // -- Buttons --
        JButton btnAdd = createButton("Thêm Item", COL_SUCCESS);
        btnAdd.addActionListener(e -> openEditor(-1));

        JButton btnReload = createButton("Làm Mới", Color.GRAY);
        btnReload.addActionListener(e -> {
            iconCache.clear();
            loadData("");
        });
        
        // -- Tools Menu --
        JButton btnTools = createButton("Công Cụ Mở Rộng", COL_PRIMARY);
        JPopupMenu popupTools = new JPopupMenu();
        
        JMenuItem menuPart = new JMenuItem("Quản lý PART (Head/Body/Leg)");
        menuPart.setFont(FONT_UI);
        menuPart.addActionListener(e -> openPartManager());
        
        JMenuItem menuHeadAvatar = new JMenuItem("Quản lý HEAD AVATAR");
        menuHeadAvatar.setFont(FONT_UI);
        menuHeadAvatar.addActionListener(e -> openHeadAvatarManager());
        
        JMenuItem menuFrames = new JMenuItem("Quản lý HEAD FRAMES (Effect)");
        menuFrames.setFont(FONT_UI);
        menuFrames.addActionListener(e -> openHeadFrameManager());

        popupTools.add(menuPart);
        popupTools.add(menuHeadAvatar);
        popupTools.add(menuFrames);
        
        btnTools.addActionListener(e -> popupTools.show(btnTools, 0, btnTools.getHeight()));

        pRight.add(txtSearch);
        pRight.add(btnTools);
        pRight.add(btnAdd);
        pRight.add(btnReload);

        top.add(lblTitle, BorderLayout.WEST);
        top.add(pRight, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
    }

    private void initTable() {
        String[] cols = { "ID", "Icon", "Tên Item", "Loại", "Giới Tính", "Level", "Part", "Mô Tả" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
            public Class<?> getColumnClass(int column) {
                if (column == 1) return ImageIcon.class;
                if (column == 0 || column == 3 || column == 5 || column == 6) return Integer.class;
                return String.class;
            }
        };

        table = new JTable(model);
        formatTable(table); // Áp dụng giao diện đẹp có grid
        
        table.setRowHeight(60); 

        // Căn chỉnh
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Kích thước cột
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(250);
        table.getColumnModel().getColumn(7).setPreferredWidth(350);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int id = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row), 0).toString());
                        openEditor(id);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(new Color(230,230,230)));
        add(scroll, BorderLayout.CENTER);
    }

    // ========================================================================
    //                             PHẦN 2: LOAD DATA
    // ========================================================================

    private ImageIcon getIcon(int iconId) {
        if (iconCache.containsKey(iconId)) return iconCache.get(iconId);
        try {
            File f = DataGame.getIconFile(iconId);
            if (f.exists()) {
                Image img = ImageIO.read(f).getScaledInstance(35, 35, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(img);
                iconCache.put(iconId, icon);
                return icon;
            }
        } catch (Exception e) { }
        return null;
    }

    private void loadData(String keyword) {
        new Thread(() -> {
            String sql = "SELECT * FROM item_template";
            if (!keyword.isEmpty()) {
                sql += " WHERE NAME LIKE '%" + keyword + "%' OR id LIKE '" + keyword + "%'";
            }
            sql += " ORDER BY id ASC"; 

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    int id = rs.getInt("id");
                    int iconId = rs.getInt("icon_id");
                    int gender = rs.getInt("gender");
                    
                    row.add(id);
                    row.add(getIcon(iconId));
                    row.add(rs.getString("NAME"));
                    row.add(rs.getInt("TYPE"));
                    row.add(gender == 0 ? "Trái Đất" : gender == 1 ? "Namếc" : gender == 2 ? "Xayda" : "Tất cả");
                    row.add(rs.getInt("level"));
                    row.add(rs.getInt("part"));
                    row.add(rs.getString("description"));
                    data.add(row);
                }
                SwingUtilities.invokeLater(() -> {
                    model.setRowCount(0);
                    for (Vector<Object> row : data) model.addRow(row);
                });
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    // ========================================================================
    //                        PHẦN 3: CÁC EDITOR PHỤ
    // ========================================================================

    // ----- 3.1 MANAGER: PARTS -----
    private void openPartManager() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Quản lý Part (Head/Body/Leg)", true);
        d.setSize(1000, 650);
        d.setLocationRelativeTo(this);
        
        // --- Thanh công cụ lọc ---
        JPanel pTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        pTop.setBackground(Color.WHITE);
        
        JLabel lblFilter = new JLabel("Chế độ hiển thị: ");
        lblFilter.setFont(FONT_BOLD);
        String[] filters = {"Hiển thị tất cả Part", "Chỉ hiện Part chưa có Data []"};
        JComboBox<String> cbbFilter = new JComboBox<>(filters);
        cbbFilter.setFont(FONT_UI);
        cbbFilter.setPreferredSize(new Dimension(250, 35));
        
        JButton btnAdd = createButton("Thêm ID Mới", COL_SUCCESS);
        btnAdd.setPreferredSize(new Dimension(150, 35));
        
        pTop.add(lblFilter);
        pTop.add(cbbFilter);
        pTop.add(Box.createHorizontalStrut(20));
        pTop.add(btnAdd);

        // --- Bảng hiển thị ---
        DefaultTableModel partModel = new DefaultTableModel(new String[]{"ID", "Type", "Data JSON"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable partTable = new JTable(partModel);
        formatTable(partTable); 
        
        partTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String val = value.toString();
                if (val.contains("HEAD")) setForeground(new Color(0, 128, 0));
                else if (val.contains("BODY")) setForeground(new Color(255, 140, 0));
                else if (val.contains("LEG")) setForeground(new Color(128, 0, 128));
                else setForeground(Color.BLACK);
                return c;
            }
        });
        
        partTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        partTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        partTable.getColumnModel().getColumn(2).setPreferredWidth(600);

        // --- Logic Load Data ---
        Runnable loadParts = () -> {
            new Thread(() -> {
                boolean showOnlyEmpty = cbbFilter.getSelectedIndex() == 1;
                String sql = "SELECT * FROM " + TABLE_PARTS;
                
                if (showOnlyEmpty) {
                    sql += " WHERE " + COLUMN_PART_DATA + " LIKE '%[]%' OR " + COLUMN_PART_DATA + " IS NULL";
                }
                sql += " ORDER BY id ASC"; 

                try(Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery(sql)) {
                    Vector<Vector<Object>> data = new Vector<>();
                    while(rs.next()) {
                        Vector<Object> r = new Vector<>();
                        int type = rs.getInt("type");
                        r.add(rs.getInt("id"));
                        r.add(type == 0 ? "HEAD (0)" : type == 1 ? "BODY (1)" : type == 2 ? "LEG (2)" : "CHƯA CÓ (-1)");
                        r.add(rs.getString(COLUMN_PART_DATA));
                        data.add(r);
                    }
                    SwingUtilities.invokeLater(() -> {
                        partModel.setRowCount(0);
                        for(Vector<Object> v : data) partModel.addRow(v);
                    });
                } catch(Exception ex) { 
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(d, "Lỗi tải Part: " + ex.getMessage()));
                }
            }).start();
        };

        cbbFilter.addActionListener(e -> loadParts.run());
        btnAdd.addActionListener(e -> openPartEditorFunc(d, -1, -1, "[]", loadParts));

        partTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && partTable.getSelectedRow() != -1) {
                    int row = partTable.getSelectedRow();
                    int id = Integer.parseInt(partModel.getValueAt(row, 0).toString());
                    String typeStr = partModel.getValueAt(row, 1).toString();
                    int type = typeStr.startsWith("HEAD") ? 0 : typeStr.startsWith("BODY") ? 1 : typeStr.startsWith("LEG") ? 2 : -1;
                    String data = partModel.getValueAt(row, 2).toString();
                    openPartEditorFunc(d, id, type, data, loadParts);
                }
            }
        });

        d.add(pTop, BorderLayout.NORTH);
        d.add(new JScrollPane(partTable), BorderLayout.CENTER);
        loadParts.run();
        d.setVisible(true);
    }
    
    private void openPartEditorFunc(JDialog parent, int id, int initialType, String initialData, Runnable onSaveSuccess) {
        boolean isAdd = (id == -1);
        JDialog ed = new JDialog(parent, isAdd ? "Thêm Part Mới" : "Sửa Part ID: " + id, true);
        ed.setSize(600, 450);
        ed.setLocationRelativeTo(parent);
        ed.setLayout(new GridBagLayout());
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10); g.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField tfId = new JTextField();
        if(!isAdd) {
            tfId.setText(String.valueOf(id));
            tfId.setEditable(false);
        } else {
            tfId.setEditable(true);
        }
        
        JComboBox<String> cbType = new JComboBox<>(new String[]{"0 - Head (Đầu)", "1 - Body (Thân)", "2 - Leg (Chân)"});
        if(initialType != -1) cbType.setSelectedIndex(initialType);
        
        JTextArea taData = new JTextArea(10, 30); 
        taData.setLineWrap(true);
        taData.setText(initialData);
        JScrollPane scrollData = new JScrollPane(taData);
        
        g.gridx=0; g.gridy=0; ed.add(new JLabel("Part ID:"), g);
        g.gridx=1; ed.add(tfId, g);
        
        g.gridx=0; g.gridy=1; ed.add(new JLabel("Loại Part:"), g);
        g.gridx=1; ed.add(cbType, g);
        
        g.gridx=0; g.gridy=2; ed.add(new JLabel("Dữ liệu JSON:"), g);
        g.gridx=1; ed.add(scrollData, g);

        JButton btnSave = new JButton("Lưu Dữ Liệu");
        btnSave.setBackground(COL_PRIMARY);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(FONT_BOLD);
        
        btnSave.addActionListener(ev -> {
            try {
                int saveId = Integer.parseInt(tfId.getText());
                int saveType = cbType.getSelectedIndex();
                String saveData = taData.getText();
                
                new Thread(() -> {
                    try (Connection conn = getConnection()) {
                        boolean exists = false;
                        try (ResultSet rs = conn.createStatement().executeQuery("SELECT 1 FROM " + TABLE_PARTS + " WHERE id=" + saveId)) {
                            if (rs.next()) exists = true;
                        }

                        String sql;
                        if (exists) sql = "UPDATE " + TABLE_PARTS + " SET type=?, " + COLUMN_PART_DATA + "=? WHERE id=?";
                        else sql = "INSERT INTO " + TABLE_PARTS + " (type, " + COLUMN_PART_DATA + ", id) VALUES (?,?,?)";

                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            ps.setInt(1, saveType);
                            ps.setString(2, saveData);
                            ps.setInt(3, saveId);
                            ps.executeUpdate();
                        }

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(ed, "Lưu thành công!");
                            ed.dispose();
                            onSaveSuccess.run();
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ed, "Lỗi: " + ex.getMessage()));
                    }
                }).start();
            } catch(NumberFormatException e) {
                JOptionPane.showMessageDialog(ed, "ID phải là số!");
            }
        });
        
        g.gridx=1; g.gridy=3; ed.add(btnSave, g);
        ed.setVisible(true);
    }


    // ----- 3.2 MANAGER: HEAD AVATAR -----
    private void openHeadAvatarManager() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Quản lý Head Avatar", true);
        d.setSize(600, 500);
        d.setLocationRelativeTo(this);
        
        DefaultTableModel modelAvt = new DefaultTableModel(new String[]{"Head ID (Part)", "Avatar Icon ID", "Preview"}, 0) {
            public Class<?> getColumnClass(int c) { return c == 2 ? ImageIcon.class : Object.class; }
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableAvt = new JTable(modelAvt);
        formatTable(tableAvt);
        tableAvt.setRowHeight(40);
        
        Runnable loadAvt = () -> {
            new Thread(() -> {
                try(Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + TABLE_HEAD_AVATAR)) {
                    Vector<Vector<Object>> data = new Vector<>();
                    while(rs.next()) {
                        Vector<Object> r = new Vector<>();
                        r.add(rs.getInt("head_id"));
                        int avtId = rs.getInt("avatar_id");
                        r.add(avtId);
                        r.add(getIcon(avtId));
                        data.add(r);
                    }
                    SwingUtilities.invokeLater(() -> {
                        modelAvt.setRowCount(0);
                        for(Vector<Object> v : data) modelAvt.addRow(v);
                    });
                } catch(Exception ex) { ex.printStackTrace(); }
            }).start();
        };

        Runnable openEditorAvt = () -> {
            int row = tableAvt.getSelectedRow();
            boolean isAdd = (row == -1);
            JDialog ed = new JDialog(d, "Chỉnh sửa Avatar", true);
            ed.setSize(400, 300);
            ed.setLocationRelativeTo(d);
            ed.setLayout(new GridLayout(4, 2, 10, 10));
            
            JTextField tfHead = new JTextField();
            JTextField tfAvt = new JTextField();
            JLabel lblPreview = new JLabel();
            
            tfAvt.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { upd(); }
                public void removeUpdate(DocumentEvent e) { upd(); }
                public void changedUpdate(DocumentEvent e) { upd(); }
                void upd() { try { lblPreview.setIcon(getIcon(Integer.parseInt(tfAvt.getText()))); } catch(Exception ex) {} }
            });
            
            if(!isAdd) {
                tfHead.setText(modelAvt.getValueAt(row, 0).toString());
                tfAvt.setText(modelAvt.getValueAt(row, 1).toString());
                tfHead.setEditable(false);
            }

            ed.add(new JLabel("Head ID (trong bảng Part):")); ed.add(tfHead);
            ed.add(new JLabel("Avatar Icon ID:")); ed.add(tfAvt);
            ed.add(new JLabel("Preview:")); ed.add(lblPreview);
            
            JButton btnSave = new JButton("Lưu");
            btnSave.setBackground(COL_PRIMARY); btnSave.setForeground(Color.WHITE);
            btnSave.addActionListener(ev -> {
                String sql = isAdd ? "INSERT INTO " + TABLE_HEAD_AVATAR + " VALUES (?,?)" : "UPDATE " + TABLE_HEAD_AVATAR + " SET avatar_id=? WHERE head_id=?";
                try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    if(isAdd) { ps.setInt(1, Integer.parseInt(tfHead.getText())); ps.setInt(2, Integer.parseInt(tfAvt.getText())); }
                    else { ps.setInt(1, Integer.parseInt(tfAvt.getText())); ps.setInt(2, Integer.parseInt(tfHead.getText())); }
                    ps.executeUpdate();
                    ed.dispose();
                    loadAvt.run();
                } catch(Exception ex) { JOptionPane.showMessageDialog(ed, "Lỗi: " + ex.getMessage()); }
            });
            ed.add(btnSave);
            ed.setVisible(true);
        };
        
        JPanel p = new JPanel();
        JButton b1 = new JButton("Thêm"); b1.addActionListener(e -> { tableAvt.clearSelection(); openEditorAvt.run(); });
        JButton b2 = new JButton("Sửa"); b2.addActionListener(e -> { if(tableAvt.getSelectedRow() != -1) openEditorAvt.run(); });
        JButton b3 = new JButton("Xóa"); b3.addActionListener(e -> {
             int r = tableAvt.getSelectedRow();
             if(r != -1 && JOptionPane.showConfirmDialog(d,"Xóa dòng này?") == 0) {
                 try(Connection c = getConnection()) { c.createStatement().executeUpdate("DELETE FROM " + TABLE_HEAD_AVATAR + " WHERE head_id=" + modelAvt.getValueAt(r, 0)); loadAvt.run(); } catch(Exception ex){}
             }
        });
        p.add(b1); p.add(b2); p.add(b3);
        d.add(new JScrollPane(tableAvt), BorderLayout.CENTER);
        d.add(p, BorderLayout.SOUTH);
        loadAvt.run();
        d.setVisible(true);
    }

    // ----- 3.3 MANAGER: HEAD FRAMES (EFFECT) -----
    private void openHeadFrameManager() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Quản lý Head Frames (Hiệu ứng)", true);
        d.setSize(700, 500);
        d.setLocationRelativeTo(this);
        
        DefaultTableModel modelFrame = new DefaultTableModel(new String[]{"ID", "Data Array (Frames)"}, 0);
        JTable tableFrame = new JTable(modelFrame);
        formatTable(tableFrame);
        tableFrame.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableFrame.getColumnModel().getColumn(1).setPreferredWidth(550);
        
        Runnable loadFrames = () -> {
            new Thread(() -> {
                try(Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM " + TABLE_HEAD_FRAMES)) {
                    Vector<Vector<Object>> data = new Vector<>();
                    while(rs.next()) {
                        Vector<Object> r = new Vector<>();
                        r.add(rs.getInt("id"));
                        r.add(rs.getString("data"));
                        data.add(r);
                    }
                    SwingUtilities.invokeLater(() -> {
                        modelFrame.setRowCount(0);
                        for(Vector<Object> v : data) modelFrame.addRow(v);
                    });
                } catch(Exception ex) { ex.printStackTrace(); }
            }).start();
        };

        Runnable openEditorFrame = () -> {
            int row = tableFrame.getSelectedRow();
            boolean isAdd = (row == -1);
            JDialog ed = new JDialog(d, "Chỉnh sửa Frames", true);
            ed.setSize(400, 300);
            ed.setLocationRelativeTo(d);
            ed.setLayout(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(5,5,5,5);
            
            JTextField tfId = new JTextField();
            // --- TỰ ĐỘNG TẠO ID MỚI ---
            if (isAdd) {
                new Thread(() -> {
                    try (Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(id) FROM " + TABLE_HEAD_FRAMES)) {
                        if (rs.next()) {
                            int nextId = rs.getInt(1) + 1;
                            SwingUtilities.invokeLater(() -> tfId.setText(String.valueOf(nextId)));
                        }
                    } catch(Exception ex) {}
                }).start();
            } else {
                tfId.setText(modelFrame.getValueAt(row, 0).toString());
                tfId.setEditable(false);
            }
            
            JTextArea taData = new JTextArea(5, 20); taData.setLineWrap(true);
            if(!isAdd) {
                taData.setText(modelFrame.getValueAt(row, 1).toString());
            }
            
            g.gridx=0; g.gridy=0; ed.add(new JLabel("ID (Auto):"), g);
            g.gridx=1; ed.add(tfId, g);
            g.gridx=0; g.gridy=1; ed.add(new JLabel("Data Array [id1, id2...]:"), g);
            g.gridx=1; ed.add(new JScrollPane(taData), g);
            
            JButton btnSave = new JButton("Lưu");
            btnSave.setBackground(COL_PRIMARY); btnSave.setForeground(Color.WHITE);
            btnSave.addActionListener(ev -> {
                String sql = isAdd ? "INSERT INTO " + TABLE_HEAD_FRAMES + " VALUES (?,?)" : "UPDATE " + TABLE_HEAD_FRAMES + " SET data=? WHERE id=?";
                try(Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    if(isAdd) { ps.setInt(1, Integer.parseInt(tfId.getText())); ps.setString(2, taData.getText()); }
                    else { ps.setString(1, taData.getText()); ps.setInt(2, Integer.parseInt(tfId.getText())); }
                    ps.executeUpdate();
                    ed.dispose();
                    loadFrames.run();
                } catch(Exception ex) { JOptionPane.showMessageDialog(ed, "Lỗi: " + ex.getMessage()); }
            });
            g.gridx=1; g.gridy=2; ed.add(btnSave, g);
            ed.setVisible(true);
        };

        JPanel p = new JPanel();
        JButton b1 = new JButton("Thêm"); b1.addActionListener(e -> { tableFrame.clearSelection(); openEditorFrame.run(); });
        JButton b2 = new JButton("Sửa"); b2.addActionListener(e -> { if(tableFrame.getSelectedRow() != -1) openEditorFrame.run(); });
        JButton b3 = new JButton("Xóa"); b3.addActionListener(e -> {
             int r = tableFrame.getSelectedRow();
             if(r != -1 && JOptionPane.showConfirmDialog(d,"Xóa dòng này?") == 0) {
                 try(Connection c = getConnection()) { c.createStatement().executeUpdate("DELETE FROM " + TABLE_HEAD_FRAMES + " WHERE id=" + modelFrame.getValueAt(r, 0)); loadFrames.run(); } catch(Exception ex){}
             }
        });
        p.add(b1); p.add(b2); p.add(b3);
        d.add(new JScrollPane(tableFrame), BorderLayout.CENTER);
        d.add(p, BorderLayout.SOUTH);
        loadFrames.run();
        d.setVisible(true);
    }

    // --- UTILS: LÀM ĐẸP BẢNG & THÊM DÒNG KẺ ---
    private void formatTable(JTable t) {
        t.setRowHeight(35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // --- HIỂN THỊ DÒNG KẺ (GRID) ---
        t.setShowGrid(true); 
        t.setShowVerticalLines(true);
        t.setShowHorizontalLines(true);
        t.setGridColor(new Color(220, 220, 220)); // Màu kẻ xám nhẹ
        
        t.setSelectionBackground(new Color(220, 235, 255));
        t.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = t.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEAD);
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        // Hiệu ứng dòng kẻ xen kẽ (Zebra)
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                }
                return c;
            }
        });
    }

    // ========================================================================
    //                        PHẦN 4: ITEM EDITOR DIALOG
    // ========================================================================

    private void openEditor(int itemId) {
        boolean isAdd = (itemId == -1);
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isAdd ? "Thêm Item Mới" : "Sửa Item ID: " + itemId, true);
        d.setSize(1000, 750);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(new EmptyBorder(25, 25, 25, 25));
        pForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        Map<String, JTextComponent> tfMap = new HashMap<>(); 
        Map<String, JComboBox> cbMap = new HashMap<>();
        Map<String, JCheckBox> chkMap = new HashMap<>();

        // Component Icon Preview
        JLabel lblIconPreview = new JLabel();
        lblIconPreview.setPreferredSize(new Dimension(64, 64));
        lblIconPreview.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblIconPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconPreview.setBackground(new Color(245, 245, 245));
        lblIconPreview.setOpaque(true);

        // -- ID Field & Auto ID Logic --
        JTextField tfId = new JTextField();
        tfId.setEditable(isAdd);
        tfId.setFont(FONT_BOLD);
        
        JTextField tfCount = new JTextField("1");
        
        if (isAdd) {
            new Thread(() -> {
                try(Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT MAX(id) FROM item_template")) {
                    if(rs.next()) {
                        int nextId = rs.getInt(1) + 1;
                        SwingUtilities.invokeLater(() -> tfId.setText(String.valueOf(nextId)));
                    }
                } catch(Exception ex) {}
            }).start();
        } else {
            tfId.setText(String.valueOf(itemId));
        }
        
        // -- Layout Components --
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; pForm.add(new JLabel("ID Bắt đầu:"), gbc);
        gbc.gridx = 1; pForm.add(tfId, gbc); 
        
        if (isAdd) {
            gbc.gridx = 2; pForm.add(new JLabel("Số lượng tạo:"), gbc);
            gbc.gridx = 3; pForm.add(tfCount, gbc);
        }
        row++;

        addFormItem(pForm, gbc, "Tên Item (NAME):", new JTextField(), row++, tfMap);
        addAreaItem(pForm, gbc, "Mô tả (Description):", new JTextArea(3, 20), row++, tfMap);
        
        // --- SỬA LỖI LAYOUT ICON BỊ ĐÈ ---
        JPanel pIcon = new JPanel(new BorderLayout(10, 0));
        pIcon.setBackground(Color.WHITE);
        
        JTextField tfIcon = new JTextField("0");
        tfIcon.setFont(FONT_UI);
        tfIcon.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updatePreview(); }
            public void removeUpdate(DocumentEvent e) { updatePreview(); }
            public void changedUpdate(DocumentEvent e) { updatePreview(); }
            void updatePreview() {
                try {
                    int i = Integer.parseInt(tfIcon.getText());
                    lblIconPreview.setIcon(getIcon(i));
                } catch(Exception ex) { lblIconPreview.setIcon(null); }
            }
        });
        
        pIcon.add(tfIcon, BorderLayout.CENTER);
        pIcon.add(lblIconPreview, BorderLayout.EAST);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; pForm.add(new JLabel("Icon ID & View:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; pForm.add(pIcon, gbc);
        tfMap.put("Icon ID", tfIcon);
        row++;

        // Part, Type
        addFormItem(pForm, gbc, "Part (Vẽ hình):", new JTextField("-1"), row++, tfMap);
        addFormItem(pForm, gbc, "Loại (TYPE):", new JTextField("0"), row++, tfMap);
        
        // Gender
        String[] genders = {"0 - Trái Đất", "1 - Namếc", "2 - Xayda", "3 - Tất cả"};
        JComboBox<String> cbGender = new JComboBox<>(genders);
        cbGender.setFont(FONT_UI);
        cbGender.setBackground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; pForm.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; pForm.add(cbGender, gbc); cbMap.put("Gender", cbGender);
        row++;

        // Stats Grid
        JPanel pStats = new JPanel(new GridLayout(0, 4, 15, 15));
        pStats.setOpaque(false);
        addStat(pStats, "Level:", "0", tfMap);
        addStat(pStats, "Power Require:", "0", tfMap);
        addStat(pStats, "Vàng (Gold):", "0", tfMap);
        addStat(pStats, "Ngọc (Gem):", "0", tfMap);
        addStat(pStats, "Head (Đầu):", "-1", tfMap);
        addStat(pStats, "Body (Thân):", "-1", tfMap);
        addStat(pStats, "Leg (Chân):", "-1", tfMap);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4; 
        pForm.add(pStats, gbc);
        row++;

        // Checkbox
        JPanel pChk = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pChk.setOpaque(false);
        JCheckBox chkUpToUp = new JCheckBox("Cộng dồn (UpToUp)");
        JCheckBox chkTrade = new JCheckBox("Có thể giao dịch", true);
        JCheckBox chkOver99 = new JCheckBox("Up to up > 99");
        
        chkUpToUp.setBackground(Color.WHITE); chkTrade.setBackground(Color.WHITE); chkOver99.setBackground(Color.WHITE);
        chkUpToUp.setFont(FONT_UI); chkTrade.setFont(FONT_UI); chkOver99.setFont(FONT_UI);
        
        pChk.add(chkUpToUp); pChk.add(chkTrade); pChk.add(chkOver99);
        chkMap.put("is_up_to_up", chkUpToUp);
        chkMap.put("can_trade", chkTrade);
        chkMap.put("is_up_to_up_over_99", chkOver99);
        
        gbc.gridy = row; pForm.add(pChk, gbc);

        // Load Data
        if (!isAdd) {
            new Thread(() -> {
                try (Connection conn = getConnection(); ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM item_template WHERE id=" + itemId)) {
                    if (rs.next()) {
                        String name = rs.getString("NAME");
                        String desc = rs.getString("description");
                        String icon = String.valueOf(rs.getInt("icon_id"));
                        String type = String.valueOf(rs.getInt("TYPE"));
                        String part = String.valueOf(rs.getInt("part"));
                        int gender = rs.getInt("gender");
                        
                        String level = rs.getString("level");
                        String power = rs.getString("power_require");
                        String gold = rs.getString("gold");
                        String gem = rs.getString("gem");
                        String head = rs.getString("head");
                        String body = rs.getString("body");
                        String leg = rs.getString("leg");
                        
                        boolean isUpToUp = rs.getBoolean("is_up_to_up");
                        boolean canTrade = true; 
                        boolean isOver99 = false; // DB không có cột này -> mặc định tắt

                        
                        SwingUtilities.invokeLater(() -> {
                            safeSetText(tfMap, "Tên Item (NAME):", name);
                            safeSetText(tfMap, "Mô tả (Description):", desc);
                            safeSetText(tfMap, "Icon ID", icon);
                            safeSetText(tfMap, "Loại (TYPE):", type);
                            safeSetText(tfMap, "Part (Vẽ hình):", part);
                            cbGender.setSelectedIndex(gender >=0 && gender <=3 ? gender : 3);
                            
                            safeSetText(tfMap, "Level:", level);
                            safeSetText(tfMap, "Power Require:", power);
                            safeSetText(tfMap, "Vàng (Gold):", gold);
                            safeSetText(tfMap, "Ngọc (Gem):", gem);
                            safeSetText(tfMap, "Head (Đầu):", head);
                            safeSetText(tfMap, "Body (Thân):", body);
                            safeSetText(tfMap, "Leg (Chân):", leg);
                            
                            safeSetCheck(chkMap, "is_up_to_up", isUpToUp);
                            safeSetCheck(chkMap, "can_trade", canTrade);
                            safeSetCheck(chkMap, "is_up_to_up_over_99", isOver99);
                        });
                    }
                } catch(Exception e) { e.printStackTrace(); }
            }).start();
        }

        JScrollPane scrollForm = new JScrollPane(pForm);
        scrollForm.setBorder(null);
        d.add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel pBtn = new JPanel();
        pBtn.setBackground(new Color(245, 245, 245));
        pBtn.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton btnSave = createButton(isAdd ? "THÊM MỚI" : "CẬP NHẬT", COL_SUCCESS);
        btnSave.setPreferredSize(new Dimension(200, 45));
        btnSave.addActionListener(e -> {
            int count = 1;
            if(isAdd) { try { count = Integer.parseInt(tfCount.getText()); } catch(Exception ex) {} }
            saveItem(itemId, tfId.getText(), count, tfMap, cbMap, chkMap, d);
        });
        
        JButton btnDelete = createButton("XÓA ITEM", COL_DANGER);
        btnDelete.setPreferredSize(new Dimension(120, 45));
        btnDelete.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(d, "Bạn chắc chắn muốn xóa item ID: " + itemId + "?") == 0) {
                deleteItem(itemId, d);
            }
        });
        if(!isAdd) pBtn.add(btnDelete);
        
        pBtn.add(btnSave);
        d.add(pBtn, BorderLayout.SOUTH);

        d.setVisible(true);
    }

    // --- HELPER METHODS ---
    private void safeSetText(Map<String, JTextComponent> map, String key, String value) {
        if (map.containsKey(key) && map.get(key) != null) map.get(key).setText(value != null ? value : "");
    }

    private void safeSetCheck(Map<String, JCheckBox> map, String key, boolean value) {
        if (map.containsKey(key) && map.get(key) != null) map.get(key).setSelected(value);
    }

    private void addFormItem(JPanel p, GridBagConstraints gbc, String label, JTextField tf, int row, Map<String, JTextComponent> map) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label); lbl.setFont(FONT_UI);
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; 
        tf.setFont(FONT_UI);
        tf.setPreferredSize(new Dimension(0, 30));
        p.add(tf, gbc);
        map.put(label, tf);
    }
    
    private void addAreaItem(JPanel p, GridBagConstraints gbc, String label, JTextArea ta, int row, Map<String, JTextComponent> map) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label); lbl.setFont(FONT_UI);
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        ta.setFont(FONT_UI);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setPreferredSize(new Dimension(0, 80));
        p.add(scroll, gbc);
        map.put(label, ta);
    }

    private void addStat(JPanel p, String label, String def, Map<String, JTextComponent> map) {
        JPanel sub = new JPanel(new BorderLayout());
        sub.setOpaque(false);
        JLabel lbl = new JLabel(label); lbl.setFont(FONT_UI);
        JTextField tf = new JTextField(def); tf.setFont(FONT_UI); tf.setPreferredSize(new Dimension(0, 30));
        sub.add(lbl, BorderLayout.NORTH);
        sub.add(tf, BorderLayout.CENTER);
        p.add(sub);
        map.put(label, tf);
    }

    private void saveItem(int oldId, String startIdStr, int count, Map<String, JTextComponent> tf, Map<String, JComboBox> cb, Map<String, JCheckBox> chk, JDialog d) {
        new Thread(() -> {
            try {
                int startId = Integer.parseInt(startIdStr);
                boolean isInsert = (oldId == -1);
                
                String sql;
                if (isInsert) {
                    sql = "INSERT INTO item_template (NAME, description, icon_id, part, TYPE, gender, level, power_require, gold, gem, head, body, leg, is_up_to_up, id) "
                            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                } else {
                    sql = "UPDATE item_template SET NAME=?, description=?, icon_id=?, part=?, TYPE=?, gender=?, level=?, power_require=?, gold=?, gem=?, head=?, body=?, leg=?, is_up_to_up=? WHERE id=?";
                }


                try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    conn.setAutoCommit(false);
                    int loop = isInsert ? count : 1;
                    
                    for (int i = 0; i < loop; i++) {
                        int currentId = isInsert ? (startId + i) : oldId;
                        
                        ps.setString(1, tf.get("Tên Item (NAME):").getText());
                        ps.setString(2, tf.get("Mô tả (Description):").getText());
                        ps.setInt(3, Integer.parseInt(tf.get("Icon ID").getText()));
                        ps.setInt(4, Integer.parseInt(tf.get("Part (Vẽ hình):").getText()));
                        ps.setInt(5, Integer.parseInt(tf.get("Loại (TYPE):").getText()));
                        ps.setInt(6, cb.get("Gender").getSelectedIndex());

                        ps.setInt(7, Integer.parseInt(tf.get("Level:").getText()));
                        ps.setLong(8, Long.parseLong(tf.get("Power Require:").getText()));
                        ps.setInt(9, Integer.parseInt(tf.get("Vàng (Gold):").getText()));
                        ps.setInt(10, Integer.parseInt(tf.get("Ngọc (Gem):").getText()));
                        ps.setInt(11, Integer.parseInt(tf.get("Head (Đầu):").getText()));
                        ps.setInt(12, Integer.parseInt(tf.get("Body (Thân):").getText()));
                        ps.setInt(13, Integer.parseInt(tf.get("Leg (Chân):").getText()));

                        ps.setBoolean(14, chk.get("is_up_to_up").isSelected());
                        ps.setInt(15, currentId);


                        ps.setInt(16, currentId);
                        ps.addBatch();

                    }
                    
                    ps.executeBatch();
                    conn.commit();
                    conn.setAutoCommit(true);

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(d, isInsert ? "Đã thêm " + count + " item thành công!" : "Cập nhật thành công!");
                        d.dispose();
                        loadData("");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(d, "Lỗi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    
    private void deleteItem(int id, JDialog d) {
        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM item_template WHERE id=" + id);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(d, "Đã xóa item ID: " + id);
                    d.dispose();
                    loadData("");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setPreferredSize(new Dimension(120, 45));
        b.setBorder(new LineBorder(bg.darker(), 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}