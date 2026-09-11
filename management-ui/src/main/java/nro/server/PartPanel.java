package nro.server;

import Data.DataGame;
import jbcd.ConnectDB;

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
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Quản lý Part:
 * - part(id, type, DATA)
 * - head_avatar(head_id, avatar_id)
 * - array_head_2_frames(id, data)
 *
 * DATA lưu dạng chuẩn:
 * [[iconId, dx, dy], [iconId, dx, dy]]
 */
public class PartPanel extends JPanel {

    private static final String TABLE_PART = "part";
    private static final String COL_PART_DATA = "DATA";

    private static final String TABLE_HEAD_AVATAR = "head_avatar";
    private static final String TABLE_HEAD_FRAMES = "array_head_2_frames";

    private final Color COL_PRIMARY = new Color(0, 120, 215);
    private final Color COL_SUCCESS = new Color(30, 160, 60);
    private final Color COL_DANGER = new Color(220, 53, 69);
    private final Color COL_WARNING = new Color(255, 145, 0);
    private final Color COL_TABLE_HEAD = new Color(240, 242, 245);

    private final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);

    private final Map<Integer, ImageIcon> iconCacheSmall = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCacheBig = new HashMap<>();

    private boolean loaded = false;
    private boolean updatingIconPreview = false;
    private boolean updatingAvatarPreview = false;

    // PART TAB
    private JTable tblPart;
    private DefaultTableModel modelPart;
    private JTextField txtSearchPart;
    private JComboBox<String> cbFilterPart;

    private JTextField txtPartId;
    private JComboBox<String> cbPartType;
    private JTable tblIcon;
    private DefaultTableModel modelIcon;
    private JLabel lblPartStatus;

    // HEAD AVATAR TAB
    private JTable tblAvatar;
    private DefaultTableModel modelAvatar;

    // HEAD FRAMES TAB
    private JTable tblFrames;
    private DefaultTableModel modelFrames;

    public PartPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();

        // Không load DB tại constructor để tránh lag ServerManagerUI.
        // Khi CardLayout show panel này, componentShown sẽ gọi loadWhenOpen().
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadWhenOpen();
            }
        });
    }

    public void loadWhenOpen() {
        if (loaded) {
            return;
        }
        loaded = true;
        reloadAll();
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    private void initUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("QUẢN LÝ PART & HEAD DATA");
        title.setFont(FONT_TITLE);
        title.setForeground(COL_PRIMARY);

        JButton btnReload = createButton("Làm Mới", Color.GRAY, 120, 42);
        btnReload.addActionListener(e -> reloadAll());

        top.add(title, BorderLayout.WEST);
        top.add(btnReload, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BOLD);
        tabs.addTab("Part", createPartTab());
        tabs.addTab("Head Avatar", createHeadAvatarTab());
        tabs.addTab("Head Frames", createHeadFramesTab());

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    // =========================================================
    // TAB PART
    // =========================================================

    private JPanel createPartTab() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setOpaque(false);

        JPanel filter = new JPanel(new BorderLayout(8, 0));
        filter.setOpaque(false);

        txtSearchPart = new JTextField();
        txtSearchPart.setFont(FONT_UI);
        txtSearchPart.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        txtSearchPart.putClientProperty("JTextField.placeholderText", "Tìm ID part...");

        cbFilterPart = new JComboBox<>(new String[]{
                "Tất cả",
                "HEAD",
                "BODY",
                "LEG",
                "DATA rỗng"
        });
        cbFilterPart.setFont(FONT_UI);
        cbFilterPart.setBackground(Color.WHITE);
        cbFilterPart.setPreferredSize(new Dimension(120, 34));

        JButton btnSearch = createButton("Tìm", COL_PRIMARY, 70, 34);
        btnSearch.addActionListener(e -> loadParts());

        txtSearchPart.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                lazySearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lazySearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lazySearch();
            }

            private void lazySearch() {
                Timer timer = new Timer(350, e -> loadParts());
                timer.setRepeats(false);
                timer.start();
            }
        });

        cbFilterPart.addActionListener(e -> loadParts());

        filter.add(txtSearchPart, BorderLayout.CENTER);

        JPanel filterRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filterRight.setOpaque(false);
        filterRight.add(cbFilterPart);
        filterRight.add(btnSearch);
        filter.add(filterRight, BorderLayout.EAST);

        left.add(filter, BorderLayout.NORTH);

        modelPart = new DefaultTableModel(new String[]{"ID", "Type", "Icon", "Preview"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0 || column == 2) {
                    return Integer.class;
                }
                if (column == 3) {
                    return PartPreview.class;
                }
                return String.class;
            }
        };

        tblPart = new JTable(modelPart);
        formatTable(tblPart);
        tblPart.setRowHeight(78);
        tblPart.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblPart.getColumnModel().getColumn(1).setPreferredWidth(95);
        tblPart.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblPart.getColumnModel().getColumn(3).setPreferredWidth(230);

        tblPart.getColumnModel().getColumn(1).setCellRenderer(new TypeRenderer());
        tblPart.getColumnModel().getColumn(3).setCellRenderer(new PartPreviewRenderer());

        tblPart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblPart.getSelectedRow();
                if (row >= 0) {
                    int modelRow = tblPart.convertRowIndexToModel(row);
                    int id = Integer.parseInt(modelPart.getValueAt(modelRow, 0).toString());
                    loadPartToEditor(id);
                }
            }
        });

        JScrollPane leftScroll = new JScrollPane(tblPart);
        leftScroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        leftScroll.getViewport().setBackground(Color.WHITE);
        left.add(leftScroll, BorderLayout.CENTER);

        JPanel right = createPartEditor();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.36);
        split.setDividerLocation(460);
        split.setBorder(null);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private JPanel createPartEditor() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtPartId = new JTextField();
        txtPartId.setFont(FONT_UI);

        JButton btnFind = createButton("Tải", COL_PRIMARY, 70, 34);
        btnFind.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtPartId.getText().trim());
                loadPartToEditor(id);
            } catch (Exception ex) {
                showMsg("Part ID phải là số!");
            }
        });

        cbPartType = new JComboBox<>(new String[]{
                "HEAD (0)",
                "BODY (1)",
                "LEG (2)"
        });
        cbPartType.setFont(FONT_UI);
        cbPartType.setBackground(Color.WHITE);

        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        form.add(new JLabel("Part ID:"), g);

        g.gridx = 1;
        g.weightx = 1;
        form.add(txtPartId, g);

        g.gridx = 2;
        g.weightx = 0;
        form.add(btnFind, g);

        g.gridx = 3;
        form.add(new JLabel("Type:"), g);

        g.gridx = 4;
        g.weightx = 1;
        form.add(cbPartType, g);

        root.add(form, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);

        JPanel tool = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tool.setOpaque(false);

        JButton btnPreview = createButton("Xem Icon", Color.GRAY, 100, 34);
        btnPreview.addActionListener(e -> previewSelectedIcon());

        JButton btnJson = createButton("Xem DATA", Color.GRAY, 105, 34);
        btnJson.addActionListener(e -> viewCurrentPartJson());

        JButton btnAddIcon = createButton("+ Thêm Icon", COL_SUCCESS, 120, 34);
        btnAddIcon.addActionListener(e -> modelIcon.addRow(new Object[]{0, 0, 0, null}));

        JButton btnDelIcon = createButton("- Xóa Icon", COL_DANGER, 110, 34);
        btnDelIcon.addActionListener(e -> {
            int row = tblIcon.getSelectedRow();
            if (row >= 0) {
                modelIcon.removeRow(tblIcon.convertRowIndexToModel(row));
                updatePartStatus();
            }
        });

        tool.add(btnPreview);
        tool.add(btnJson);
        tool.add(Box.createHorizontalStrut(15));
        tool.add(btnAddIcon);
        tool.add(btnDelIcon);

        center.add(tool, BorderLayout.NORTH);

        modelIcon = new DefaultTableModel(new String[]{"Icon ID", "dx", "dy", "Preview"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) {
                    return ImageIcon.class;
                }
                return Integer.class;
            }
        };

        tblIcon = new JTable(modelIcon);
        formatTable(tblIcon);
        tblIcon.setRowHeight(46);
        tblIcon.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblIcon.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblIcon.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblIcon.getColumnModel().getColumn(3).setPreferredWidth(120);

        modelIcon.addTableModelListener(e -> {
            if (!updatingIconPreview && e.getColumn() != 3) {
                refreshIconPreviewTable();
            }
        });

        JScrollPane iconScroll = new JScrollPane(tblIcon);
        iconScroll.setBorder(new LineBorder(new Color(230, 230, 230)));
        iconScroll.getViewport().setBackground(Color.WHITE);

        center.add(iconScroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        lblPartStatus = new JLabel("Chỉnh sửa | ID 0 | 0 icon");
        lblPartStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblPartStatus.setForeground(COL_SUCCESS);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton btnNew = createButton("Tạo Mới", COL_WARNING, 110, 42);
        btnNew.addActionListener(e -> newPart());

        JButton btnSave = createButton("Lưu", COL_SUCCESS, 100, 42);
        btnSave.addActionListener(e -> saveCurrentPart());

        JButton btnDelete = createButton("Xóa", COL_DANGER, 100, 42);
        btnDelete.addActionListener(e -> deleteCurrentPart());

        buttons.add(btnNew);
        buttons.add(btnSave);
        buttons.add(btnDelete);

        bottom.add(lblPartStatus, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        root.add(bottom, BorderLayout.SOUTH);

        return root;
    }

    private void loadParts() {
        if (!loaded) {
            return;
        }

        new Thread(() -> {
            List<Object[]> rows = new ArrayList<>();

            String keyword = txtSearchPart == null ? "" : txtSearchPart.getText().trim();
            int filterIndex = cbFilterPart == null ? 0 : cbFilterPart.getSelectedIndex();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT id, type, ").append(COL_PART_DATA)
                    .append(" FROM ").append(TABLE_PART)
                    .append(" WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            if (!keyword.isEmpty()) {
                sql.append(" AND id LIKE ? ");
                params.add(keyword + "%");
            }

            if (filterIndex == 1) {
                sql.append(" AND type = 0 ");
            } else if (filterIndex == 2) {
                sql.append(" AND type = 1 ");
            } else if (filterIndex == 3) {
                sql.append(" AND type = 2 ");
            } else if (filterIndex == 4) {
                sql.append(" AND (").append(COL_PART_DATA).append(" IS NULL OR ")
                        .append(COL_PART_DATA).append(" = '' OR ")
                        .append(COL_PART_DATA).append(" = '[]') ");
            }

            sql.append(" ORDER BY id ASC LIMIT 700");

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {

                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int type = rs.getInt("type");
                        String data = rs.getString(COL_PART_DATA);
                        List<PartIcon> icons = parsePartIcons(data);

                        rows.add(new Object[]{
                                id,
                                typeToText(type),
                                icons.size(),
                                new PartPreview(icons)
                        });
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    modelPart.setRowCount(0);
                    for (Object[] row : rows) {
                        modelPart.addRow(row);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi load Part: " + e.getMessage()));
            }
        }, "PartPanel-loadParts").start();
    }

    private void loadPartToEditor(int id) {
        new Thread(() -> {
            String sql = "SELECT id, type, " + COL_PART_DATA + " FROM " + TABLE_PART + " WHERE id=? LIMIT 1";

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        SwingUtilities.invokeLater(() -> showMsg("Không tìm thấy Part ID: " + id));
                        return;
                    }

                    int partId = rs.getInt("id");
                    int type = rs.getInt("type");
                    String data = rs.getString(COL_PART_DATA);
                    List<PartIcon> icons = parsePartIcons(data);

                    SwingUtilities.invokeLater(() -> {
                        txtPartId.setText(String.valueOf(partId));
                        cbPartType.setSelectedIndex(type >= 0 && type <= 2 ? type : 0);

                        updatingIconPreview = true;
                        try {
                            modelIcon.setRowCount(0);
                            for (PartIcon pi : icons) {
                                modelIcon.addRow(new Object[]{
                                        pi.icon,
                                        pi.dx,
                                        pi.dy,
                                        getIcon(pi.icon, 35)
                                });
                            }
                        } finally {
                            updatingIconPreview = false;
                        }

                        updatePartStatus();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi load Part: " + e.getMessage()));
            }
        }, "PartPanel-loadPartEditor").start();
    }

    private void newPart() {
        new Thread(() -> {
            int nextId = 0;

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT IFNULL(MAX(id), -1) + 1 FROM " + TABLE_PART);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    nextId = rs.getInt(1);
                }

                int finalNextId = nextId;
                SwingUtilities.invokeLater(() -> {
                    txtPartId.setText(String.valueOf(finalNextId));
                    cbPartType.setSelectedIndex(0);

                    updatingIconPreview = true;
                    try {
                        modelIcon.setRowCount(0);
                    } finally {
                        updatingIconPreview = false;
                    }

                    updatePartStatus();
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi tạo ID mới: " + e.getMessage()));
            }
        }, "PartPanel-newPart").start();
    }

    private void saveCurrentPart() {
        try {
            int id = Integer.parseInt(txtPartId.getText().trim());
            int type = cbPartType.getSelectedIndex();
            String data = buildPartDataFromTable();

            new Thread(() -> {
                try (Connection conn = getConnection()) {
                    boolean exists;

                    try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM " + TABLE_PART + " WHERE id=? LIMIT 1")) {
                        check.setInt(1, id);
                        try (ResultSet rs = check.executeQuery()) {
                            exists = rs.next();
                        }
                    }

                    String sql;
                    if (exists) {
                        sql = "UPDATE " + TABLE_PART + " SET type=?, " + COL_PART_DATA + "=? WHERE id=?";
                    } else {
                        sql = "INSERT INTO " + TABLE_PART + " (type, " + COL_PART_DATA + ", id) VALUES (?, ?, ?)";
                    }

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, type);
                        ps.setString(2, data);
                        ps.setInt(3, id);
                        ps.executeUpdate();
                    }

                    SwingUtilities.invokeLater(() -> {
                        showMsg("Lưu Part ID " + id + " thành công!");
                        loadParts();
                        loadPartToEditor(id);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> showMsg("Lỗi lưu Part: " + e.getMessage()));
                }
            }, "PartPanel-savePart").start();

        } catch (Exception e) {
            showMsg("Part ID / Icon ID / dx / dy phải là số!");
        }
    }

    private void deleteCurrentPart() {
        try {
            int id = Integer.parseInt(txtPartId.getText().trim());

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn chắc chắn muốn xóa Part ID " + id + "?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            new Thread(() -> {
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM " + TABLE_PART + " WHERE id=?")) {

                    ps.setInt(1, id);
                    ps.executeUpdate();

                    SwingUtilities.invokeLater(() -> {
                        showMsg("Đã xóa Part ID " + id);
                        txtPartId.setText("");

                        updatingIconPreview = true;
                        try {
                            modelIcon.setRowCount(0);
                        } finally {
                            updatingIconPreview = false;
                        }

                        updatePartStatus();
                        loadParts();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> showMsg("Lỗi xóa Part: " + e.getMessage()));
                }
            }, "PartPanel-deletePart").start();

        } catch (Exception e) {
            showMsg("Chưa chọn Part ID hợp lệ!");
        }
    }

    private void refreshIconPreviewTable() {
        if (updatingIconPreview) {
            return;
        }

        updatingIconPreview = true;

        SwingUtilities.invokeLater(() -> {
            try {
                for (int i = 0; i < modelIcon.getRowCount(); i++) {
                    try {
                        Object val = modelIcon.getValueAt(i, 0);
                        int iconId = Integer.parseInt(String.valueOf(val));
                        modelIcon.setValueAt(getIcon(iconId, 35), i, 3);
                    } catch (Exception ignored) {
                        modelIcon.setValueAt(null, i, 3);
                    }
                }
                updatePartStatus();
            } finally {
                updatingIconPreview = false;
            }
        });
    }

    private void previewSelectedIcon() {
        int row = tblIcon.getSelectedRow();

        if (row < 0) {
            showMsg("Chọn 1 dòng icon trước!");
            return;
        }

        int modelRow = tblIcon.convertRowIndexToModel(row);

        try {
            int iconId = Integer.parseInt(modelIcon.getValueAt(modelRow, 0).toString());
            ImageIcon icon = getIcon(iconId, 90);

            JLabel label = new JLabel(icon);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setPreferredSize(new Dimension(150, 130));

            JOptionPane.showMessageDialog(this, label, "Preview Icon ID: " + iconId, JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            showMsg("Icon ID không hợp lệ!");
        }
    }

    private void viewCurrentPartJson() {
        try {
            String data = buildPartDataFromTable();

            JTextArea area = new JTextArea(data, 12, 48);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(new Font("Consolas", Font.PLAIN, 13));

            JOptionPane.showMessageDialog(
                    this,
                    new JScrollPane(area),
                    "DATA Part",
                    JOptionPane.PLAIN_MESSAGE
            );
        } catch (Exception e) {
            showMsg("Dữ liệu icon chưa hợp lệ!");
        }
    }

    private void updatePartStatus() {
        if (lblPartStatus == null) {
            return;
        }

        String id = txtPartId == null ? "0" : txtPartId.getText().trim();
        int count = modelIcon == null ? 0 : modelIcon.getRowCount();

        lblPartStatus.setText("Chỉnh sửa | ID " + (id.isEmpty() ? "0" : id) + " | " + count + " icon");
    }

    // =========================================================
    // TAB HEAD AVATAR
    // =========================================================

    private JPanel createHeadAvatarTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        modelAvatar = new DefaultTableModel(new String[]{"Head ID", "Avatar ID", "Preview"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 2;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2) {
                    return ImageIcon.class;
                }
                return Integer.class;
            }
        };

        tblAvatar = new JTable(modelAvatar);
        formatTable(tblAvatar);
        tblAvatar.setRowHeight(46);
        tblAvatar.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblAvatar.getColumnModel().getColumn(1).setPreferredWidth(120);
        tblAvatar.getColumnModel().getColumn(2).setPreferredWidth(180);

        modelAvatar.addTableModelListener(e -> {
            if (!updatingAvatarPreview && e.getColumn() != 2) {
                refreshAvatarPreview();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton btnAdd = createButton("+ Thêm", COL_SUCCESS, 100, 38);
        btnAdd.addActionListener(e -> modelAvatar.addRow(new Object[]{0, 0, null}));

        JButton btnSave = createButton("Lưu", COL_SUCCESS, 100, 38);
        btnSave.addActionListener(e -> saveHeadAvatar());

        JButton btnDelete = createButton("Xóa", COL_DANGER, 100, 38);
        btnDelete.addActionListener(e -> deleteSelectedAvatar());

        buttons.add(btnAdd);
        buttons.add(btnSave);
        buttons.add(btnDelete);

        root.add(new JScrollPane(tblAvatar), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void loadHeadAvatar() {
        if (!loaded) {
            return;
        }

        new Thread(() -> {
            List<Object[]> rows = new ArrayList<>();

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT head_id, avatar_id FROM " + TABLE_HEAD_AVATAR + " ORDER BY head_id ASC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int headId = rs.getInt("head_id");
                    int avatarId = rs.getInt("avatar_id");

                    rows.add(new Object[]{
                            headId,
                            avatarId,
                            getIcon(avatarId, 35)
                    });
                }

                SwingUtilities.invokeLater(() -> {
                    updatingAvatarPreview = true;
                    try {
                        modelAvatar.setRowCount(0);
                        for (Object[] row : rows) {
                            modelAvatar.addRow(row);
                        }
                    } finally {
                        updatingAvatarPreview = false;
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi load Head Avatar: " + e.getMessage()));
            }
        }, "PartPanel-loadHeadAvatar").start();
    }

    private void saveHeadAvatar() {
        new Thread(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);

                for (int i = 0; i < modelAvatar.getRowCount(); i++) {
                    int headId = Integer.parseInt(modelAvatar.getValueAt(i, 0).toString());
                    int avatarId = Integer.parseInt(modelAvatar.getValueAt(i, 1).toString());

                    boolean exists;
                    try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM " + TABLE_HEAD_AVATAR + " WHERE head_id=? LIMIT 1")) {
                        check.setInt(1, headId);
                        try (ResultSet rs = check.executeQuery()) {
                            exists = rs.next();
                        }
                    }

                    String sql = exists
                            ? "UPDATE " + TABLE_HEAD_AVATAR + " SET avatar_id=? WHERE head_id=?"
                            : "INSERT INTO " + TABLE_HEAD_AVATAR + " (avatar_id, head_id) VALUES (?, ?)";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, avatarId);
                        ps.setInt(2, headId);
                        ps.executeUpdate();
                    }
                }

                conn.commit();

                SwingUtilities.invokeLater(() -> {
                    showMsg("Lưu Head Avatar thành công!");
                    loadHeadAvatar();
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi lưu Head Avatar: " + e.getMessage()));
            }
        }, "PartPanel-saveHeadAvatar").start();
    }

    private void deleteSelectedAvatar() {
        int row = tblAvatar.getSelectedRow();

        if (row < 0) {
            showMsg("Chọn dòng cần xóa!");
            return;
        }

        int modelRow = tblAvatar.convertRowIndexToModel(row);
        int headId = Integer.parseInt(modelAvatar.getValueAt(modelRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa Head Avatar head_id " + headId + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM " + TABLE_HEAD_AVATAR + " WHERE head_id=?")) {

                ps.setInt(1, headId);
                ps.executeUpdate();

                SwingUtilities.invokeLater(() -> {
                    showMsg("Đã xóa Head Avatar " + headId);
                    loadHeadAvatar();
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi xóa Head Avatar: " + e.getMessage()));
            }
        }, "PartPanel-deleteHeadAvatar").start();
    }

    private void refreshAvatarPreview() {
        if (updatingAvatarPreview) {
            return;
        }

        updatingAvatarPreview = true;

        SwingUtilities.invokeLater(() -> {
            try {
                for (int i = 0; i < modelAvatar.getRowCount(); i++) {
                    try {
                        int avatarId = Integer.parseInt(modelAvatar.getValueAt(i, 1).toString());
                        modelAvatar.setValueAt(getIcon(avatarId, 35), i, 2);
                    } catch (Exception ignored) {
                        modelAvatar.setValueAt(null, i, 2);
                    }
                }
            } finally {
                updatingAvatarPreview = false;
            }
        });
    }

    // =========================================================
    // TAB HEAD FRAMES
    // =========================================================

    private JPanel createHeadFramesTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setOpaque(false);

        modelFrames = new DefaultTableModel(new String[]{"ID", "Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        tblFrames = new JTable(modelFrames);
        formatTable(tblFrames);
        tblFrames.setRowHeight(38);
        tblFrames.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblFrames.getColumnModel().getColumn(1).setPreferredWidth(650);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);

        JButton btnAdd = createButton("+ Thêm", COL_SUCCESS, 100, 38);
        btnAdd.addActionListener(e -> addNewFrameRow());

        JButton btnSave = createButton("Lưu", COL_SUCCESS, 100, 38);
        btnSave.addActionListener(e -> saveHeadFrames());

        JButton btnDelete = createButton("Xóa", COL_DANGER, 100, 38);
        btnDelete.addActionListener(e -> deleteSelectedFrame());

        buttons.add(btnAdd);
        buttons.add(btnSave);
        buttons.add(btnDelete);

        root.add(new JScrollPane(tblFrames), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }

    private void loadHeadFrames() {
        if (!loaded) {
            return;
        }

        new Thread(() -> {
            List<Object[]> rows = new ArrayList<>();

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT id, data FROM " + TABLE_HEAD_FRAMES + " ORDER BY id ASC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("data")
                    });
                }

                SwingUtilities.invokeLater(() -> {
                    modelFrames.setRowCount(0);
                    for (Object[] row : rows) {
                        modelFrames.addRow(row);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi load Head Frames: " + e.getMessage()));
            }
        }, "PartPanel-loadHeadFrames").start();
    }

    private void addNewFrameRow() {
        new Thread(() -> {
            int nextId = 0;

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT IFNULL(MAX(id), -1) + 1 FROM " + TABLE_HEAD_FRAMES);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    nextId = rs.getInt(1);
                }

                int finalNextId = nextId;
                SwingUtilities.invokeLater(() -> modelFrames.addRow(new Object[]{finalNextId, "[]"}));

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi tạo ID frame: " + e.getMessage()));
            }
        }, "PartPanel-addFrame").start();
    }

    private void saveHeadFrames() {
        new Thread(() -> {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);

                for (int i = 0; i < modelFrames.getRowCount(); i++) {
                    int id = Integer.parseInt(modelFrames.getValueAt(i, 0).toString());
                    String data = String.valueOf(modelFrames.getValueAt(i, 1));

                    boolean exists;
                    try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM " + TABLE_HEAD_FRAMES + " WHERE id=? LIMIT 1")) {
                        check.setInt(1, id);
                        try (ResultSet rs = check.executeQuery()) {
                            exists = rs.next();
                        }
                    }

                    String sql = exists
                            ? "UPDATE " + TABLE_HEAD_FRAMES + " SET data=? WHERE id=?"
                            : "INSERT INTO " + TABLE_HEAD_FRAMES + " (data, id) VALUES (?, ?)";

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, data);
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                }

                conn.commit();

                SwingUtilities.invokeLater(() -> {
                    showMsg("Lưu Head Frames thành công!");
                    loadHeadFrames();
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi lưu Head Frames: " + e.getMessage()));
            }
        }, "PartPanel-saveFrames").start();
    }

    private void deleteSelectedFrame() {
        int row = tblFrames.getSelectedRow();

        if (row < 0) {
            showMsg("Chọn dòng cần xóa!");
            return;
        }

        int modelRow = tblFrames.convertRowIndexToModel(row);
        int id = Integer.parseInt(modelFrames.getValueAt(modelRow, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa Head Frames ID " + id + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(() -> {
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM " + TABLE_HEAD_FRAMES + " WHERE id=?")) {

                ps.setInt(1, id);
                ps.executeUpdate();

                SwingUtilities.invokeLater(() -> {
                    showMsg("Đã xóa Head Frames ID " + id);
                    loadHeadFrames();
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> showMsg("Lỗi xóa Head Frames: " + e.getMessage()));
            }
        }, "PartPanel-deleteFrame").start();
    }

    // =========================================================
    // DATA HELPER
    // =========================================================

    private List<PartIcon> parsePartIcons(String data) {
        List<PartIcon> list = new ArrayList<>();

        if (data == null || data.trim().isEmpty() || data.trim().equals("[]")) {
            return list;
        }

        String text = data.trim();

        // Format chuẩn: [[icon,dx,dy],[icon,dx,dy]]
        Pattern arrayPattern = Pattern.compile("\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\]");
        Matcher mArray = arrayPattern.matcher(text);

        while (mArray.find()) {
            int icon = Integer.parseInt(mArray.group(1));
            int dx = Integer.parseInt(mArray.group(2));
            int dy = Integer.parseInt(mArray.group(3));
            list.add(new PartIcon(icon, dx, dy));
        }

        if (!list.isEmpty()) {
            return list;
        }

        // Hỗ trợ format object nếu DB cũ lỡ lưu dạng này:
        // [{"icon":17,"dx":0,"dy":0}]
        Pattern objectPattern = Pattern.compile(
                "\\{\\s*\"icon\"\\s*:\\s*(-?\\d+)\\s*,\\s*\"dx\"\\s*:\\s*(-?\\d+)\\s*,\\s*\"dy\"\\s*:\\s*(-?\\d+)\\s*\\}"
        );
        Matcher mObj = objectPattern.matcher(text);

        while (mObj.find()) {
            int icon = Integer.parseInt(mObj.group(1));
            int dx = Integer.parseInt(mObj.group(2));
            int dy = Integer.parseInt(mObj.group(3));
            list.add(new PartIcon(icon, dx, dy));
        }

        return list;
    }

    private String buildPartDataFromTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < modelIcon.getRowCount(); i++) {
            int icon = Integer.parseInt(modelIcon.getValueAt(i, 0).toString());
            int dx = Integer.parseInt(modelIcon.getValueAt(i, 1).toString());
            int dy = Integer.parseInt(modelIcon.getValueAt(i, 2).toString());

            if (i > 0) {
                sb.append(",");
            }

            sb.append("[")
                    .append(icon)
                    .append(",")
                    .append(dx)
                    .append(",")
                    .append(dy)
                    .append("]");
        }

        sb.append("]");
        return sb.toString();
    }

    // =========================================================
    // UI HELPER
    // =========================================================

    private void reloadAll() {
        iconCacheSmall.clear();
        iconCacheBig.clear();

        loadParts();
        loadHeadAvatar();
        loadHeadFrames();
    }

    private ImageIcon getIcon(int iconId, int size) {
        if (iconId < 0) {
            return null;
        }

        Map<Integer, ImageIcon> cache = size <= 40 ? iconCacheSmall : iconCacheBig;

        if (cache.containsKey(iconId)) {
            return cache.get(iconId);
        }

        try {
            File f = DataGame.getIconFile(iconId);

            if (f != null && f.exists()) {
                BufferedImage source = ImageIO.read(f);
                if (source != null) {
                    Image img = source.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(img);
                    cache.put(iconId, icon);
                    return icon;
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private void formatTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(220, 235, 255));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(COL_TABLE_HEAD);
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        DefaultTableCellRenderer intCenter = new DefaultTableCellRenderer();
        intCenter.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Integer.class, intCenter);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                }

                return c;
            }
        });
    }

    private JButton createButton(String text, Color bg, int w, int h) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setPreferredSize(new Dimension(w, h));
        b.setBorder(new LineBorder(bg.darker(), 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private String typeToText(int type) {
        switch (type) {
            case 0:
                return "HEAD (0)";
            case 1:
                return "BODY (1)";
            case 2:
                return "LEG (2)";
            default:
                return "UNKNOWN (" + type + ")";
        }
    }

    private void showMsg(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    // =========================================================
    // INNER CLASS
    // =========================================================

    private static class PartIcon {
        int icon;
        int dx;
        int dy;

        PartIcon(int icon, int dx, int dy) {
            this.icon = icon;
            this.dx = dx;
            this.dy = dy;
        }
    }

    private static class PartPreview {
        List<PartIcon> icons;

        PartPreview(List<PartIcon> icons) {
            this.icons = icons;
        }
    }

    private class PartPreviewRenderer extends JPanel implements TableCellRenderer {

        private PartPreview preview;
        private boolean selected;

        PartPreviewRenderer() {
            setOpaque(true);
            setLayout(null);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            this.preview = value instanceof PartPreview ? (PartPreview) value : null;
            this.selected = isSelected;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            setBackground(selected ? new Color(220, 235, 255) : Color.WHITE);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (preview == null || preview.icons == null || preview.icons.isEmpty()) {
                drawEmptySlots(g2);
                g2.dispose();
                return;
            }

            int x = 8;
            int y = 8;
            int size = 28;
            int gap = 4;
            int maxW = getWidth() - 10;

            for (PartIcon pi : preview.icons) {
                if (x + size > maxW) {
                    x = 8;
                    y += size + gap;
                }

                if (y + size > getHeight() - 4) {
                    break;
                }

                drawSlot(g2, x, y, size);

                ImageIcon icon = getIcon(pi.icon, 26);
                if (icon != null) {
                    g2.drawImage(icon.getImage(), x + 1, y + 1, 26, 26, null);
                }

                x += size + gap;
            }

            g2.dispose();
        }

        private void drawEmptySlots(Graphics2D g2) {
            int size = 18;
            int gap = 4;
            int x = 8;
            int y = 8;

            for (int i = 0; i < 12; i++) {
                drawSlot(g2, x, y, size);

                x += size + gap;
                if (x + size > getWidth() - 8) {
                    x = 8;
                    y += size + gap;
                }
            }
        }

        private void drawSlot(Graphics2D g2, int x, int y, int size) {
            g2.setColor(new Color(245, 245, 245));
            g2.fillRoundRect(x, y, size, size, 6, 6);
            g2.setColor(new Color(210, 210, 210));
            g2.drawRoundRect(x, y, size, size, 6, 6);
        }
    }

    private static class TypeRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(SwingConstants.CENTER);

            if (!isSelected) {
                String text = value == null ? "" : value.toString();

                if (text.contains("HEAD")) {
                    c.setForeground(new Color(0, 128, 0));
                } else if (text.contains("BODY")) {
                    c.setForeground(new Color(220, 135, 0));
                } else if (text.contains("LEG")) {
                    c.setForeground(new Color(130, 0, 130));
                } else {
                    c.setForeground(Color.BLACK);
                }
            }

            return c;
        }
    }
}