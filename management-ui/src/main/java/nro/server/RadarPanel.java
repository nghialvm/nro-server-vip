package nro.server;


import Data.DataGame;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import jbcd.ConnectDB;
import nro.card.OptionCard;
import nro.card.RadarCard;


public class RadarPanel extends JPanel {


    // --- UI COLORS ---
    private final Color COLOR_PRIMARY = new Color(0, 120, 215);   // Xanh dương
    private final Color COLOR_SUCCESS = new Color(40, 167, 69);   // Xanh lá
    private final Color COLOR_DANGER = new Color(220, 53, 69);    // Đỏ
    private final Color COLOR_BG_HEADER = new Color(230, 242, 255); // Nền header bảng
    
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 13);

    private JList<RadarCard> listRadar;
    private DefaultListModel<RadarCard> listModel;
    private List<RadarCard> allRadars;

    // Form Components
    private JTextField txtId, txtName, txtIconId, txtMobId, txtRank, txtMax, txtType, txtAuraId;
    private JTextField txtHead, txtBody, txtLeg, txtBag;
    private JTextArea txtInfo;
    private JTable tblOptions;
    private DefaultTableModel modelOptions;
    private JLabel lblIconPreview;

    // Cache Data
    private final Map<Integer, ItemTemplateData> itemTemplateMap = new HashMap<>();
    private final Map<Integer, String> optionTemplateMap = new HashMap<>();
    private final Map<Integer, ImageIcon> iconCache = new HashMap<>();

    private static class ItemTemplateData {
        int id;
        String name;
        int iconId;
        int type;

        public ItemTemplateData(int id, String name, int iconId, int type) {
            this.id = id;
            this.name = name;
            this.iconId = iconId;
            this.type = type;
        }
    }

    public RadarPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        allRadars = new ArrayList<>();
        loadCacheData();
        initUI();
        loadDataFromDB();
    }

    // --- LOAD CACHE ---
    private void loadCacheData() {
        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                try (ResultSet rs = stmt.executeQuery("SELECT id, name, icon_id, type FROM item_template")) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        itemTemplateMap.put(id, new ItemTemplateData(id, rs.getString("name"), rs.getInt("icon_id"), rs.getInt("type")));
                    }
                }
                try (ResultSet rs = stmt.executeQuery("SELECT id, name FROM item_option_template")) {
                    while (rs.next()) {
                        optionTemplateMap.put(rs.getInt("id"), rs.getString("name"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private ImageIcon loadIconRaw(int iconId) {
        if (iconId <= 0) return null;
        if (iconCache.containsKey(iconId)) return iconCache.get(iconId);
        try {
            File f = DataGame.getIconFile(iconId);
            if (f.exists()) {
                Image dimg = ImageIO.read(f).getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(dimg);
                iconCache.put(iconId, icon);
                return icon;
            }
        } catch (Exception e) { }
        return null;
    }

    private String getOptionName(int id) {
        return optionTemplateMap.getOrDefault(id, "Unknown Opt [" + id + "]");
    }

    private String formatOption(int id, int param) {
        return getOptionName(id).replace("#", String.valueOf(param));
    }

    // --- INIT UI ---
    private void initUI() {
        // --- TRÁI: DANH SÁCH (LEFT PANEL) ---
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 10));
        pnlLeft.setPreferredSize(new Dimension(320, 0));
        pnlLeft.setOpaque(false);

        // Search Box & Tools
        JPanel pnlTopLeft = new JPanel(new BorderLayout(5, 5));
        pnlTopLeft.setOpaque(false);
        
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm radar...");
        txtSearch.setPreferredSize(new Dimension(100, 35));
        txtSearch.setFont(FONT_PLAIN);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true), 
            new EmptyBorder(5, 10, 5, 10))
        );
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { filterList(txtSearch.getText()); }
        });

        // Buttons Add/Delete
        JPanel pnlTools = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlTools.setOpaque(false);
        JButton btnAddRadar = createStyledButton("Thêm Mới", COLOR_SUCCESS, Color.WHITE);
        JButton btnDelRadar = createStyledButton("Xóa", COLOR_DANGER, Color.WHITE);
        
        btnAddRadar.addActionListener(e -> prepareAddRadar());
        btnDelRadar.addActionListener(e -> deleteRadar());

        pnlTools.add(btnAddRadar);
        pnlTools.add(btnDelRadar);

        pnlTopLeft.add(txtSearch, BorderLayout.NORTH);
        pnlTopLeft.add(pnlTools, BorderLayout.CENTER);

        // List View
        listModel = new DefaultListModel<>();
        listRadar = new JList<>(listModel);
        listRadar.setFixedCellHeight(50);
        listRadar.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RadarCard) {
                    RadarCard r = (RadarCard) value;
                    lbl.setText("<html><div style='padding:5px'><b><font color='#0056b3'>[" + r.Id + "]</font></b> " + r.Name + "</div></html>");
                    lbl.setIcon(loadIconRaw(r.IconId));
                }
                lbl.setBorder(new EmptyBorder(0, 5, 0, 0));
                lbl.setFont(FONT_PLAIN);
                if (isSelected) {
                    lbl.setBackground(COLOR_BG_HEADER);
                    lbl.setForeground(Color.BLACK);
                    lbl.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_PRIMARY),
                            new EmptyBorder(0, 5, 0, 0)
                    ));
                } else {
                    lbl.setBackground(Color.WHITE);
                }
                return lbl;
            }
        });
        listRadar.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetail(listRadar.getSelectedValue());
        });
        
        JScrollPane scrollList = new JScrollPane(listRadar);
        scrollList.setBorder(new LineBorder(new Color(220, 220, 220)));

        pnlLeft.add(pnlTopLeft, BorderLayout.NORTH);
        pnlLeft.add(scrollList, BorderLayout.CENTER);

        // --- PHẢI: FORM CHỈNH SỬA (RIGHT PANEL) ---
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setOpaque(false);

        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(new EmptyBorder(0, 5, 0, 0));

        // 1. Info Form (Grid Layout chỉnh lại cho đều)
        JPanel pnlBasic = new JPanel(new GridBagLayout());
        pnlBasic.setOpaque(false);
        pnlBasic.setBorder(createTitledBorder("THÔNG TIN CHUNG"));

        txtId = new JTextField(); txtId.setEditable(false); txtId.setBackground(new Color(240, 240, 240));
        txtId.setFont(FONT_BOLD); txtId.setForeground(COLOR_PRIMARY);
        txtId.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if(e.getClickCount() == 2) openItemSearchDialog(true); }
        });
        
        txtName = new JTextField();
        
        // Icon Group
        JPanel pnlIconGroup = new JPanel(new BorderLayout(2, 0));
        pnlIconGroup.setOpaque(false);
        txtIconId = new JTextField();
        JButton btnFindIcon = new JButton("...");
        btnFindIcon.setPreferredSize(new Dimension(30, 25));
        styleToolbarButton(btnFindIcon);
        
        lblIconPreview = new JLabel(); lblIconPreview.setPreferredSize(new Dimension(32, 32));
        lblIconPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblIconPreview.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblIconPreview.setOpaque(true); lblIconPreview.setBackground(Color.WHITE);
        
        pnlIconGroup.add(txtIconId, BorderLayout.CENTER);
        pnlIconGroup.add(btnFindIcon, BorderLayout.EAST);
        pnlIconGroup.add(lblIconPreview, BorderLayout.WEST);

        txtIconId.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { u(); }
            public void removeUpdate(DocumentEvent e) { u(); }
            public void changedUpdate(DocumentEvent e) { u(); }
            void u() { try { lblIconPreview.setIcon(loadIconRaw(Integer.parseInt(txtIconId.getText()))); } catch(Exception ex){ lblIconPreview.setIcon(null); } }
        });
        btnFindIcon.addActionListener(e -> openItemSearchDialog(false));

        txtMobId = new JTextField(); txtRank = new JTextField();
        txtMax = new JTextField(); txtType = new JTextField(); txtAuraId = new JTextField();

        // Add fields to GridBag
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 10); gbc.weightx = 1.0;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; addInputGrid(pnlBasic, "ID (2 click):", txtId, gbc);
        gbc.gridx = 1; gbc.gridy = 0; addInputGrid(pnlBasic, "Tên Radar:", txtName, gbc);
        gbc.gridx = 2; gbc.gridy = 0; pnlBasic.add(createLabel("Icon ID:"), gbc); 
        gbc.gridy = 1; pnlBasic.add(pnlIconGroup, gbc); // Icon panel ở dòng dưới label
        gbc.gridx = 3; gbc.gridy = 0; addInputGrid(pnlBasic, "Template Mob:", txtMobId, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 2; addInputGrid(pnlBasic, "Rank:", txtRank, gbc);
        gbc.gridx = 1; gbc.gridy = 2; addInputGrid(pnlBasic, "Max Amount:", txtMax, gbc);
        gbc.gridx = 2; gbc.gridy = 2; addInputGrid(pnlBasic, "Type:", txtType, gbc);
        gbc.gridx = 3; gbc.gridy = 2; addInputGrid(pnlBasic, "Aura ID:", txtAuraId, gbc);

        // 2. Body
        JPanel pnlBody = new JPanel(new GridLayout(1, 4, 15, 5));
        pnlBody.setOpaque(false);
        pnlBody.setBorder(createTitledBorder("NGOẠI HÌNH (Body JSON)"));
        txtHead = new JTextField("-1"); txtBody = new JTextField("-1");
        txtLeg = new JTextField("-1"); txtBag = new JTextField("-1");
        addInput(pnlBody, "Head:", txtHead); addInput(pnlBody, "Body:", txtBody);
        addInput(pnlBody, "Leg:", txtLeg); addInput(pnlBody, "Bag:", txtBag);

        // 3. Info Text
        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(createTitledBorder("MÔ TẢ (Info)"));
        txtInfo = new JTextArea(3, 20); txtInfo.setLineWrap(true); txtInfo.setFont(FONT_PLAIN);
        pnlInfo.add(new JScrollPane(txtInfo));

        // 4. Options
        JPanel pnlOption = new JPanel(new BorderLayout());
        pnlOption.setOpaque(false);
        pnlOption.setBorder(createTitledBorder("CHỈ SỐ (Options)"));
        // Không setPreferredSize cố định ở đây nữa để nó tự dãn theo bảng

        String[] cols = {"Option ID", "Param", "Active (Cấp)", "Mô tả tự động"};
        modelOptions = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c != 3; }
            @Override public Class<?> getColumnClass(int c) { return c==3?String.class:Integer.class; }
        };
        tblOptions = new JTable(modelOptions);
        tblOptions.setRowHeight(30); // Chiều cao mỗi dòng là 30px
        tblOptions.setFont(FONT_PLAIN);
        tblOptions.getTableHeader().setFont(FONT_BOLD);
        tblOptions.getTableHeader().setBackground(new Color(245, 245, 245));
        tblOptions.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblOptions.getColumnModel().getColumn(3).setPreferredWidth(250);
        
        // --- QUAN TRỌNG: THIẾT LẬP HIỂN THỊ 5 DÒNG ---
        // 5 dòng * 30px = 150px. JTable sẽ yêu cầu JScrollPane hiển thị đúng kích thước này.
        tblOptions.setPreferredScrollableViewportSize(new Dimension(tblOptions.getPreferredSize().width, tblOptions.getRowHeight() * 5));
        tblOptions.setFillsViewportHeight(true);

        modelOptions.addTableModelListener(e -> {
            int r = e.getFirstRow(); int c = e.getColumn();
            if(r>=0 && r<modelOptions.getRowCount() && (c==0 || c==1)) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        int id = Integer.parseInt(modelOptions.getValueAt(r, 0).toString());
                        int param = Integer.parseInt(modelOptions.getValueAt(r, 1).toString());
                        modelOptions.setValueAt(formatOption(id, param), r, 3);
                    } catch(Exception ex){}
                });
            }
        });

        JToolBar barOpt = new JToolBar(); barOpt.setFloatable(false); barOpt.setOpaque(false);
        JButton btnAddOpt = new JButton("Thêm Dòng");
        JButton btnFindOpt = new JButton("Tìm ID");
        JButton btnDelOpt = new JButton("Xóa Dòng");
        styleToolbarButton(btnAddOpt); styleToolbarButton(btnFindOpt); styleToolbarButton(btnDelOpt);
        
        btnAddOpt.addActionListener(e -> modelOptions.addRow(new Object[]{0, 0, 0, getOptionName(0)}));
        btnDelOpt.addActionListener(e -> { if(tblOptions.getSelectedRow()!=-1) modelOptions.removeRow(tblOptions.getSelectedRow()); });
        btnFindOpt.addActionListener(e -> openOptionSearchDialog());
        
        barOpt.add(btnAddOpt); barOpt.add(btnFindOpt); barOpt.add(btnDelOpt);
        pnlOption.add(barOpt, BorderLayout.NORTH);
        pnlOption.add(new JScrollPane(tblOptions), BorderLayout.CENTER);

        // Add to main panel
        pnlForm.add(pnlBasic); pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(pnlBody); pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(pnlInfo); pnlForm.add(Box.createVerticalStrut(10));
        pnlForm.add(pnlOption);

        // ScrollPane cho toàn bộ form bên phải (tránh bị khuất khi màn hình nhỏ)
        JScrollPane scrollForm = new JScrollPane(pnlForm);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);

        // Bottom Actions
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlBtn.setOpaque(false);
        JButton btnSave = createStyledButton("LƯU DỮ LIỆU", COLOR_PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(150, 40));
        
        JButton btnReload = createStyledButton("TẢI LẠI DB", Color.GRAY, Color.WHITE);
        btnReload.setPreferredSize(new Dimension(120, 40));
        
        btnSave.addActionListener(e -> saveData());
        btnReload.addActionListener(e -> loadDataFromDB());
        pnlBtn.add(btnReload); pnlBtn.add(btnSave);

        pnlRight.add(scrollForm, BorderLayout.CENTER);
        pnlRight.add(pnlBtn, BorderLayout.SOUTH);

        add(pnlLeft, BorderLayout.WEST);
        add(pnlRight, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text); 
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    // Helper cho GridBagLayout
    private void addInputGrid(JPanel p, String title, JComponent c, GridBagConstraints gbc) {
        JPanel tmp = new JPanel(new BorderLayout(0, 2));
        tmp.setOpaque(false);
        tmp.add(createLabel(title), BorderLayout.NORTH);
        if(c instanceof JTextField) {
            c.setFont(FONT_PLAIN);
            c.setPreferredSize(new Dimension(c.getPreferredSize().width, 30));
            ((JTextField)c).setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200)), new EmptyBorder(2,5,2,5)));
        }
        tmp.add(c, BorderLayout.CENTER);
        
        // Tinh chỉnh lại GBC cho panel con
        GridBagConstraints subGbc = (GridBagConstraints) gbc.clone();
        p.add(tmp, subGbc);
    }

    private void addInput(JPanel p, String title, JComponent c) {
        JPanel tmp = new JPanel(new BorderLayout(0, 5));
        tmp.setOpaque(false);
        tmp.add(createLabel(title), BorderLayout.NORTH);
        if(c instanceof JTextField) {
            c.setFont(FONT_PLAIN);
            ((JTextField)c).setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200)), new EmptyBorder(5,5,5,5)));
        }
        tmp.add(c, BorderLayout.CENTER);
        p.add(tmp);
    }

    // --- DIALOGS ---
    private void openItemSearchDialog(boolean onlyType33) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Tìm Vật Phẩm" + (onlyType33 ? " (Type 33 - Radar)" : ""), Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(750, 550); d.setLayout(new BorderLayout()); d.setLocationRelativeTo(this);

        JTextField txtS = new JTextField();
        txtS.putClientProperty("JTextField.placeholderText", "Nhập tên vật phẩm...");
        txtS.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtS.setBorder(new EmptyBorder(10,10,10,10));
        
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Icon", "Tên Item", "Icon ID"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m); t.setRowHeight(40);
        t.getColumnModel().getColumn(0).setPreferredWidth(60);
        t.getColumnModel().getColumn(1).setPreferredWidth(60);
        t.getColumnModel().getColumn(2).setPreferredWidth(400);
        t.setFont(FONT_PLAIN);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m); t.setRowSorter(sorter);

        for (ItemTemplateData item : itemTemplateMap.values()) {
            if (onlyType33 && item.type != 33) continue; 
            m.addRow(new Object[]{item.id, loadIconRaw(item.iconId), item.name, item.iconId});
        }

        txtS.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String text = txtS.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2));
            }
        });

        // Double click logic
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() != -1) {
                    int modelRow = t.convertRowIndexToModel(t.getSelectedRow());
                    int id = (int) m.getValueAt(modelRow, 0);
                    String name = (String) m.getValueAt(modelRow, 2);
                    int icon = (int) m.getValueAt(modelRow, 3);
                    
                    if (onlyType33) {
                        // Điền vào form
                        txtId.setText(String.valueOf(id));
                        txtName.setText(name);
                        txtIconId.setText(String.valueOf(icon));
                        txtMobId.setText("1"); txtRank.setText("0"); txtMax.setText("1");
                        txtType.setText("0"); txtAuraId.setText("-1");
                        
                        // --- HIỂN THỊ NGAY LẬP TỨC TRÊN DANH SÁCH (TẠM) ---
                        RadarCard temp = new RadarCard();
                        temp.Id = (short) id; temp.Name = name; temp.IconId = (short) icon;
                        
                        boolean exists = false;
                        for(int i=0; i<listModel.getSize(); i++) {
                            if(listModel.getElementAt(i).Id == temp.Id) {
                                listRadar.setSelectedIndex(i);
                                exists = true; break;
                            }
                        }
                        if(!exists) {
                            listModel.addElement(temp);
                            listRadar.setSelectedValue(temp, true);
                        }
                    } else {
                        txtIconId.setText(String.valueOf(icon));
                    }
                    d.dispose();
                }
            }
        });

        d.add(txtS, BorderLayout.NORTH); d.add(new JScrollPane(t), BorderLayout.CENTER); d.setVisible(true);
    }

    private void openOptionSearchDialog() {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this), "Tìm Kiếm Option", Dialog.ModalityType.APPLICATION_MODAL);
        d.setSize(500, 450); d.setLayout(new BorderLayout()); d.setLocationRelativeTo(this);
        JTextField txtS = new JTextField();
        txtS.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtS.setBorder(new EmptyBorder(10,10,10,10));
        
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Tên Option"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(m); t.setRowHeight(30); t.setFont(FONT_PLAIN);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m); t.setRowSorter(sorter);
        optionTemplateMap.forEach((id, name) -> m.addRow(new Object[]{id, name}));

        txtS.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String tx = txtS.getText().trim();
                if(tx.isEmpty()) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)"+Pattern.quote(tx), 1));
            }
        });

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && t.getSelectedRow() != -1) {
                    int modelRow = t.convertRowIndexToModel(t.getSelectedRow());
                    int optId = (int) m.getValueAt(modelRow, 0);
                    modelOptions.addRow(new Object[]{optId, 0, 0, formatOption(optId, 0)});
                    d.dispose();
                }
            }
        });
        d.add(txtS, BorderLayout.NORTH); d.add(new JScrollPane(t), BorderLayout.CENTER); d.setVisible(true);
    }

    // --- LOGIC ---
    
    private void prepareAddRadar() {
        listRadar.clearSelection();
        txtId.setText(""); txtId.setEditable(true); txtId.setBackground(Color.WHITE);
        txtName.setText(""); txtIconId.setText("0"); txtMobId.setText("0");
        txtRank.setText("0"); txtMax.setText("0"); txtType.setText("0"); txtAuraId.setText("-1");
        txtInfo.setText("");
        txtHead.setText("-1"); txtBody.setText("-1"); txtLeg.setText("-1"); txtBag.setText("-1");
        modelOptions.setRowCount(0);
        
        openItemSearchDialog(true);
    }

    private void deleteRadar() {
        RadarCard r = listRadar.getSelectedValue();
        if (r == null) { JOptionPane.showMessageDialog(this, "Chưa chọn Radar để xóa!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa Radar ID: " + r.Id + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM radar WHERE id=?")) {
                ps.setInt(1, r.Id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadDataFromDB();
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage()); }
        }
    }

    private void loadDataFromDB() {
        allRadars.clear(); listModel.clear();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("SELECT * FROM radar ORDER BY id")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RadarCard r = new RadarCard();
                r.Id = (short) rs.getInt("id");
                r.IconId = (short) rs.getInt("iconId");
                r.Rank = (byte) rs.getInt("rank");
                r.Max = (byte) rs.getInt("max");
                r.Type = (byte) rs.getInt("type");
                r.Template = (short) rs.getInt("mob_id");
                r.Name = rs.getString("name");
                r.Info = rs.getString("info");
                r.AuraId = (short) rs.getInt("aura_id");

                try {
                    JSONArray arr = (JSONArray) JSONValue.parse(rs.getString("body"));
                    if (arr != null && !arr.isEmpty()) {
                        JSONObject ob = (JSONObject) arr.get(0);
                        r.Head = Short.parseShort(ob.get("head").toString());
                        r.Body = Short.parseShort(ob.get("body").toString());
                        r.Leg = Short.parseShort(ob.get("leg").toString());
                        r.Bag = Short.parseShort(ob.get("bag").toString());
                    } else { r.Head = -1; r.Body = -1; r.Leg = -1; r.Bag = -1; }
                } catch (Exception ex) { r.Head = -1; r.Body = -1; r.Leg = -1; r.Bag = -1; }

                try {
                    JSONArray arrOpt = (JSONArray) JSONValue.parse(rs.getString("options"));
                    if (arrOpt != null) {
                        for (Object o : arrOpt) {
                            JSONObject ob = (JSONObject) o;
                            byte active = 0;
                            if (ob.containsKey("activeCard")) active = Byte.parseByte(ob.get("activeCard").toString());
                            else if (ob.containsKey("active")) active = Byte.parseByte(ob.get("active").toString());
                            r.Options.add(new OptionCard(
                                Integer.parseInt(ob.get("id").toString()),
                                Integer.parseInt(ob.get("param").toString()),
                                active
                            ));
                        }
                    }
                } catch (Exception ex) {}
                allRadars.add(r);
            }
            filterList("");
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "DB Load Error: " + e.getMessage()); }
    }

    private void filterList(String key) {
        listModel.clear();
        String k = key.toLowerCase();
        for (RadarCard r : allRadars) {
            if (String.valueOf(r.Id).contains(k) || r.Name.toLowerCase().contains(k)) {
                listModel.addElement(r);
            }
        }
    }

    private void showDetail(RadarCard r) {
        if (r == null) return;
        txtId.setText(String.valueOf(r.Id)); 
        txtId.setEditable(false); txtId.setBackground(new Color(240, 240, 240));
        
        txtName.setText(r.Name);
        txtIconId.setText(String.valueOf(r.IconId));
        txtMobId.setText(String.valueOf(r.Template));
        txtRank.setText(String.valueOf(r.Rank));
        txtMax.setText(String.valueOf(r.Max));
        txtType.setText(String.valueOf(r.Type));
        txtAuraId.setText(String.valueOf(r.AuraId));
        txtInfo.setText(r.Info);
        txtHead.setText(String.valueOf(r.Head));
        txtBody.setText(String.valueOf(r.Body));
        txtLeg.setText(String.valueOf(r.Leg));
        txtBag.setText(String.valueOf(r.Bag));

        modelOptions.setRowCount(0);
        for (OptionCard opt : r.Options) {
            modelOptions.addRow(new Object[]{opt.id, opt.param, opt.active, formatOption(opt.id, opt.param)});
        }
    }

    private void saveData() {
        int id = 0;
        try { id = Integer.parseInt(txtId.getText()); } catch(Exception e) { JOptionPane.showMessageDialog(this, "ID không hợp lệ!"); return; }

        try {
            JSONObject bodyObj = new JSONObject();
            bodyObj.put("head", Integer.parseInt(txtHead.getText()));
            bodyObj.put("body", Integer.parseInt(txtBody.getText()));
            bodyObj.put("leg", Integer.parseInt(txtLeg.getText()));
            bodyObj.put("bag", Integer.parseInt(txtBag.getText()));
            JSONArray bodyArr = new JSONArray(); bodyArr.add(bodyObj);

            JSONArray optArr = new JSONArray();
            for (int i = 0; i < modelOptions.getRowCount(); i++) {
                JSONObject opt = new JSONObject();
                opt.put("id", Integer.parseInt(modelOptions.getValueAt(i, 0).toString()));
                opt.put("param", Integer.parseInt(modelOptions.getValueAt(i, 1).toString()));
                opt.put("activeCard", Integer.parseInt(modelOptions.getValueAt(i, 2).toString()));
                optArr.add(opt);
            }

            try (Connection conn = getConnection()) {
                conn.setAutoCommit(true); // GHI NGAY LẬP TỨC

                // Kiểm tra ID tồn tại trong DB chưa
                boolean exists = false;
                try(PreparedStatement ps = conn.prepareStatement("SELECT id FROM radar WHERE id=?")) {
                    ps.setInt(1, id);
                    if(ps.executeQuery().next()) exists = true;
                }

                String sql;
                if (exists) {
                    // Nếu ID đã có -> Update
                    sql = "UPDATE radar SET name=?, iconId=?, mob_id=?, rank=?, max=?, type=?, aura_id=?, info=?, body=?, options=? WHERE id=?";
                } else {
                    // Nếu ID chưa có -> Insert
                    sql = "INSERT INTO radar (name, iconId, mob_id, rank, max, type, aura_id, info, body, options, id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                }
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, txtName.getText());
                ps.setInt(2, Integer.parseInt(txtIconId.getText()));
                ps.setInt(3, Integer.parseInt(txtMobId.getText()));
                ps.setInt(4, Integer.parseInt(txtRank.getText()));
                ps.setInt(5, Integer.parseInt(txtMax.getText()));
                ps.setInt(6, Integer.parseInt(txtType.getText()));
                ps.setInt(7, Integer.parseInt(txtAuraId.getText()));
                ps.setString(8, txtInfo.getText());
                ps.setString(9, bodyArr.toJSONString());
                ps.setString(10, optArr.toJSONString());
                ps.setInt(11, id);
                
                int rows = ps.executeUpdate();
                ps.close();
                
                if(rows > 0) {
                    JOptionPane.showMessageDialog(this, "Lưu dữ liệu thành công!");
                    loadDataFromDB(); // Reload list
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không lưu được vào Database!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi Exception: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException { return ConnectDB.getConnection(); }
    
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(240, 240, 240)), title, TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, COLOR_PRIMARY);
    }
    
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text); 
        b.setBackground(bg); 
        b.setForeground(fg);
        b.setFocusPainted(false); 
        b.setFont(FONT_BOLD);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        b.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bg.darker(), 1, true),
            new EmptyBorder(5, 15, 5, 15)
        ));
        return b;
    }
    
    private void styleToolbarButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setFont(FONT_PLAIN);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
    }
}