/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server;

/*
 * @Refactored by Assistant
 * @Features: 
 * 1. Dual Respawn Logic (REST_XXX & AppearType)
 * 2. Read "Related Bosses" from Source
 * 3. IDE Standard Formatting (Shift + Alt + F style)
 */


import Data.DataGame;
import QuanLiBoss.BossData;
import QuanLiBoss.BossesData;
import QuanLiBoss.TypeAppear;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jbcd.ConnectDB;
import nro.skill.Skill;

public class BossEditorPanel extends JPanel {

    private static final String SOURCE_FILE_PATH = "src/QuanLiBoss/BossesData.java";

    // --- ĐỊNH NGHĨA CÁC BỘ SKILL FULL (FULL SETS) ---
    private static final Map<String, int[][]> FULL_SETS = new LinkedHashMap<>();

    static {
        FULL_SETS.put("FULL_DRAGON", new int[][]{{Skill.DRAGON, 1}, {Skill.DRAGON, 2}, {Skill.DRAGON, 3}, {Skill.DRAGON, 4}, {Skill.DRAGON, 5}, {Skill.DRAGON, 6}, {Skill.DRAGON, 7}});
        FULL_SETS.put("FULL_DEMON", new int[][]{{Skill.DEMON, 1}, {Skill.DEMON, 2}, {Skill.DEMON, 3}, {Skill.DEMON, 4}, {Skill.DEMON, 5}, {Skill.DEMON, 6}, {Skill.DEMON, 7}});
        FULL_SETS.put("FULL_GALICK", new int[][]{{Skill.GALICK, 1}, {Skill.GALICK, 2}, {Skill.GALICK, 3}, {Skill.GALICK, 4}, {Skill.GALICK, 5}, {Skill.GALICK, 6}, {Skill.GALICK, 7}});
        FULL_SETS.put("FULL_KAMEJOKO", new int[][]{{Skill.KAMEJOKO, 1}, {Skill.KAMEJOKO, 2}, {Skill.KAMEJOKO, 3}, {Skill.KAMEJOKO, 4}, {Skill.KAMEJOKO, 5}, {Skill.KAMEJOKO, 6}, {Skill.KAMEJOKO, 7}});
        FULL_SETS.put("FULL_TAI_TAO_NANG_LUONG", new int[][]{{Skill.TAI_TAO_NANG_LUONG, 1}, {Skill.TAI_TAO_NANG_LUONG, 2}, {Skill.TAI_TAO_NANG_LUONG, 3}, {Skill.TAI_TAO_NANG_LUONG, 4}, {Skill.TAI_TAO_NANG_LUONG, 5}, {Skill.TAI_TAO_NANG_LUONG, 6}, {Skill.TAI_TAO_NANG_LUONG, 7}});
        FULL_SETS.put("FULL_MASENKO", new int[][]{{Skill.MASENKO, 1}, {Skill.MASENKO, 2}, {Skill.MASENKO, 3}, {Skill.MASENKO, 4}, {Skill.MASENKO, 5}, {Skill.MASENKO, 6}, {Skill.MASENKO, 7}});
        FULL_SETS.put("FULL_ANTOMIC", new int[][]{{Skill.ANTOMIC, 1}, {Skill.ANTOMIC, 2}, {Skill.ANTOMIC, 3}, {Skill.ANTOMIC, 4}, {Skill.ANTOMIC, 5}, {Skill.ANTOMIC, 6}, {Skill.ANTOMIC, 7}});
        FULL_SETS.put("FULL_LIENHOAN", new int[][]{{Skill.LIEN_HOAN, 1}, {Skill.LIEN_HOAN, 2}, {Skill.LIEN_HOAN, 3}, {Skill.LIEN_HOAN, 4}, {Skill.LIEN_HOAN, 5}, {Skill.LIEN_HOAN, 6}, {Skill.LIEN_HOAN, 7}});
        FULL_SETS.put("FULL_TDHS", new int[][]{{Skill.THAI_DUONG_HA_SAN, 1}, {Skill.THAI_DUONG_HA_SAN, 2}, {Skill.THAI_DUONG_HA_SAN, 3}, {Skill.THAI_DUONG_HA_SAN, 4}, {Skill.THAI_DUONG_HA_SAN, 5}, {Skill.THAI_DUONG_HA_SAN, 6}, {Skill.THAI_DUONG_HA_SAN, 7}});
    }

    // Components
    private JTextField txtBossName, txtBossHp, txtBossDame, txtBossMap;
    private JComboBox<Object> cboBossRespawn; 
    private JTextField txtRelatedBosses; // Ô nhập cho mảng int[] boss đi kèm
    private JTextField txtOutfitHead, txtOutfitBody, txtOutfitLeg, txtOutfitBag, txtOutfitAura, txtOutfitEff;
    
    private JTextArea txtBossChat;
    private JList<String> listBosses;
    private JTable tableSkills;
    private DefaultTableModel modelSkills;
    private JLabel lblStatus;

    private BossData currentSelectedBossData;
    private String currentBossKey;
    private String previousBossKey; 
    private Vector<String> originalBossKeys;
    private JTextField txtSearchBoss;

    private Map<String, String> pendingChanges = new HashMap<>();
    
    private Map<Integer, Integer> partIconMap = new HashMap<>();
    private Map<Integer, ImageIcon> iconImageCache = new HashMap<>();
    private List<SkillOption> cachedSkillOptions = null;

    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 12);

    private static class SkillOption {
        int id;
        String name;
        int iconId;
        ImageIcon icon;

        public SkillOption(int id, String name, int iconId, ImageIcon icon) {
            this.id = id;
            this.name = name;
            this.iconId = iconId;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return id + ". " + name; 
        }
    }

    private static class RespawnOption {
        String label;
        String codeConstant;
        int value;

        public RespawnOption(String label, String codeConstant, int value) {
            this.label = label;
            this.codeConstant = codeConstant;
            this.value = value;
        }

        @Override
        public String toString() {
            return label; 
        }
    }

    // Mapping ngược từ giá trị int sang tên biến REST_ để hiển thị
    private static final Map<Integer, String> REST_TIME_MAP = new HashMap<>();
    static {
        REST_TIME_MAP.put(1, "REST_1_S");
        REST_TIME_MAP.put(2, "REST_2_S");
        REST_TIME_MAP.put(5, "REST_5_S");
        REST_TIME_MAP.put(10, "REST_10_S");
        REST_TIME_MAP.put(20, "REST_20_S");
        REST_TIME_MAP.put(30, "REST_30_S");
        REST_TIME_MAP.put(60, "REST_1_M");
        REST_TIME_MAP.put(120, "REST_2_M");
        REST_TIME_MAP.put(300, "REST_5_M");
        REST_TIME_MAP.put(600, "REST_10_M");
        REST_TIME_MAP.put(900, "REST_15_M");
        REST_TIME_MAP.put(1800, "REST_30_M");
        REST_TIME_MAP.put(7200, "REST_2_H");
        REST_TIME_MAP.put(86400000, "REST_24_H");
    }
    
    private Vector<RespawnOption> respawnOptions;

    public BossEditorPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initRespawnOptions();
        loadPartDataFromDB(); 
        add(createBossEditorPanel());
    }

    private void initRespawnOptions() {
        respawnOptions = new Vector<>();
        respawnOptions.add(new RespawnOption("1 Giây", "REST_1_S", 1));
        respawnOptions.add(new RespawnOption("2 Giây", "REST_2_S", 2));
        respawnOptions.add(new RespawnOption("5 Giây", "REST_5_S", 5));
        respawnOptions.add(new RespawnOption("10 Giây", "REST_10_S", 10));
        respawnOptions.add(new RespawnOption("20 Giây", "REST_20_S", 20));
        respawnOptions.add(new RespawnOption("30 Giây", "REST_30_S", 30));
        respawnOptions.add(new RespawnOption("1 Phút", "REST_1_M", 60));
        respawnOptions.add(new RespawnOption("2 Phút", "REST_2_M", 120));
        respawnOptions.add(new RespawnOption("5 Phút", "REST_5_M", 300));
        respawnOptions.add(new RespawnOption("10 Phút", "REST_10_M", 600));
        respawnOptions.add(new RespawnOption("15 Phút", "REST_15_M", 900));
        respawnOptions.add(new RespawnOption("30 Phút", "REST_30_M", 1800));
        respawnOptions.add(new RespawnOption("2 Giờ", "REST_2_H", 7200));
        respawnOptions.add(new RespawnOption("24 Giờ", "REST_24_H", 86400000));
        respawnOptions.add(new RespawnOption("Khác (Custom)", "", 0));
    }

    private Connection getConnection() throws SQLException {
        return ConnectDB.getConnection();
    }

    private void loadPartDataFromDB() {
        new Thread(() -> {
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, DATA FROM part WHERE TYPE = 0")) { 
                
                partIconMap.clear();
                while (rs.next()) {
                    int partId = rs.getInt("id");
                    String json = rs.getString("DATA");
                    try {
                        JsonArray arr = new JsonParser().parse(json).getAsJsonArray();
                        if (arr.size() > 0) {
                            JsonArray firstLayer = arr.get(0).getAsJsonArray();
                            int iconId = firstLayer.get(0).getAsInt();
                            partIconMap.put(partId, iconId);
                        }
                    } catch (Exception e) {}
                }
                if (listBosses != null) SwingUtilities.invokeLater(() -> listBosses.repaint());
            } catch (Exception e) {
                System.err.println("Lỗi load Part DB: " + e.getMessage());
            }
        }).start();
    }

    private ImageIcon getIconByIconId(int iconId, int size) {
        if (iconId <= -1) return null;
        if (iconImageCache.containsKey(iconId)) {
             Image img = iconImageCache.get(iconId).getImage();
             if (img.getWidth(null) != size) {
                 return new ImageIcon(img.getScaledInstance(size, size, Image.SCALE_SMOOTH));
             }
             return iconImageCache.get(iconId);
        }
        try {
            String[] zoomLevels = {"x4", "x3", "x2", "x1"};
            for (String zoom : zoomLevels) {
                File f = DataGame.getIconFile(iconId);
                if (f.exists()) {
                    BufferedImage img = ImageIO.read(f);
                    Image dimg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(dimg);
                    iconImageCache.put(iconId, new ImageIcon(img));
                    return icon;
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public JPanel createBossEditorPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.setBackground(new Color(245, 245, 245));

        originalBossKeys = new Vector<>();
        reloadBossListFromClass();

        listBosses = new JList<>(originalBossKeys);
        listBosses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listBosses.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listBosses.setFixedCellHeight(40);
        listBosses.setSelectionBackground(new Color(200, 230, 255));
        listBosses.setSelectionForeground(Color.BLACK);
        
        listBosses.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String bossKey = (String) value;
                BossData data = null;
                
                try {
                    Field field = BossesData.class.getField(bossKey);
                    data = (BossData) field.get(null);
                    lbl.setForeground(Color.BLACK);
                } catch (Exception e) {}

                if (data != null && data.getOutfit() != null && data.getOutfit().length > 0) {
                    int headPartId = data.getOutfit()[0];
                    int iconId = partIconMap.getOrDefault(headPartId, headPartId);
                    ImageIcon icon = getIconByIconId(iconId, 32); 
                    if (icon != null) {
                        lbl.setIcon(icon);
                    }
                }
                
                lbl.setBorder(new EmptyBorder(0, 5, 0, 0));
                lbl.setIconTextGap(10);
                return lbl;
            }
        });

        listBosses.addListSelectionListener(this::onBossSelected);

        JScrollPane scrollList = new JScrollPane(listBosses);
        scrollList.setBorder(createSectionBorder("Danh Sách Boss"));

        // --- TOP LEFT PANEL: SEARCH & BUTTONS ---
        JPanel pTopLeft = new JPanel(new BorderLayout(5, 0));
        pTopLeft.setOpaque(false);
        
        txtSearchBoss = new JTextField();
        txtSearchBoss.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        txtSearchBoss.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JButton btnReloadDB = createStyledButton("Tải Lại", new Color(100, 100, 100), Color.WHITE);
        btnReloadDB.setToolTipText("Tải lại Danh sách & Cache");
        btnReloadDB.setPreferredSize(new Dimension(80, 30));
        btnReloadDB.addActionListener(e -> {
            iconImageCache.clear();
            loadPartDataFromDB();   
            cachedSkillOptions = null;
            reloadBossListFromClass(); 
            filterBossList();
            JOptionPane.showMessageDialog(this, "Đã tải lại danh sách boss và dữ liệu!");
        });

        pTopLeft.add(txtSearchBoss, BorderLayout.CENTER);
        pTopLeft.add(btnReloadDB, BorderLayout.EAST);

        txtSearchBoss.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterBossList(); }
            public void removeUpdate(DocumentEvent e) { filterBossList(); }
            public void changedUpdate(DocumentEvent e) { filterBossList(); }
        });

        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setOpaque(false);
        leftPanel.add(pTopLeft, BorderLayout.NORTH);
        leftPanel.add(scrollList, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(280, 0));

        // --- FORM PANEL ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 5, 0, 0));

        // 1. INFO PANEL
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(createSectionBorder("Thông Tin Cơ Bản"));
        infoPanel.setOpaque(false);
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5); g.fill = GridBagConstraints.HORIZONTAL; g.anchor = GridBagConstraints.WEST; g.weightx = 1.0;

        txtBossName = createStyledTextField(Color.BLACK);
        txtBossHp = createStyledTextField(new Color(220, 0, 0)); // Đỏ đậm
        txtBossHp.setFont(new Font("Consolas", Font.BOLD, 14));
        txtBossDame = createStyledTextField(new Color(0, 0, 200)); // Xanh đậm
        txtBossDame.setFont(new Font("Consolas", Font.BOLD, 14));

        addFormRow(infoPanel, g, 0, "Tên Boss:", txtBossName);
        addFormRow(infoPanel, g, 1, "HP (Máu):", txtBossHp);
        addFormRow(infoPanel, g, 2, "Sức Đánh:", txtBossDame);

        // 2. APPEARANCE PANEL
        JPanel appearPanel = new JPanel(new BorderLayout(5, 5));
        appearPanel.setBorder(createSectionBorder("Ngoại Hình (Outfit)"));
        appearPanel.setOpaque(false);
        appearPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel pOutfitFields = new JPanel(new GridLayout(1, 6, 5, 0));
        pOutfitFields.setOpaque(false);
        txtOutfitHead = createStyledOutfitField("Đầu");
        txtOutfitBody = createStyledOutfitField("Thân");
        txtOutfitLeg = createStyledOutfitField("Chân");
        txtOutfitBag = createStyledOutfitField("Cánh");
        txtOutfitAura = createStyledOutfitField("Aura");
        txtOutfitEff = createStyledOutfitField("Eff");
        
        pOutfitFields.add(txtOutfitHead); pOutfitFields.add(txtOutfitBody); pOutfitFields.add(txtOutfitLeg);
        pOutfitFields.add(txtOutfitBag); pOutfitFields.add(txtOutfitAura); pOutfitFields.add(txtOutfitEff);

        JButton btnSearchCaiTrang = createStyledButton("Tìm Skin (Type 5)", new Color(23, 162, 184), Color.WHITE);
        btnSearchCaiTrang.setPreferredSize(new Dimension(130, 25));
        btnSearchCaiTrang.addActionListener(e -> openCaiTrangSearchDialog());

        JPanel pAppearHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pAppearHeader.setOpaque(false);
        pAppearHeader.add(btnSearchCaiTrang);

        appearPanel.add(pOutfitFields, BorderLayout.CENTER);
        appearPanel.add(pAppearHeader, BorderLayout.NORTH);

        // 3. LOCATION & RESPAWN (Updated Logic)
        JPanel locPanel = new JPanel(new GridBagLayout());
        locPanel.setBorder(createSectionBorder("Vị Trí & Hồi Sinh / Dạng Xuất Hiện"));
        locPanel.setOpaque(false);
        locPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        txtBossMap = createStyledTextField(Color.DARK_GRAY);
        JButton btnSearchMap = createStyledButton("Tìm Map", new Color(108, 117, 125), Color.WHITE);
        btnSearchMap.addActionListener(e -> openMapSearchDialog());
        
        JPanel pMapCont = new JPanel(new BorderLayout(5, 0));
        pMapCont.setOpaque(false);
        pMapCont.add(txtBossMap, BorderLayout.CENTER);
        pMapCont.add(btnSearchMap, BorderLayout.EAST);

        // -- ComboBox hỗn hợp (String REST và Enum AppearType) --
        cboBossRespawn = new JComboBox<>();
        cboBossRespawn.setFont(FONT_PLAIN);
        cboBossRespawn.setBackground(Color.WHITE);
        // Add REST constants
        for (String key : REST_TIME_MAP.values()) {
            cboBossRespawn.addItem(key);
        }
        // Add AppearType
        for (TypeAppear type : TypeAppear.values()) {
            cboBossRespawn.addItem(type);
        }

        txtRelatedBosses = createStyledTextField(Color.BLUE);
        txtRelatedBosses.setToolTipText("Nhập ID các Boss đi kèm, cách nhau bởi dấu phẩy. VD: BossID.SO_1, BossID.SO_2");

        addFormRow(locPanel, g, 0, "Map ID:", pMapCont);
        addFormRow(locPanel, g, 1, "Kiểu Hồi/Dạng:", cboBossRespawn);
        addFormRow(locPanel, g, 2, "Boss đi kèm:", txtRelatedBosses);

        // 4. SKILLS & CHAT
        JPanel miscPanel = new JPanel(new GridBagLayout());
        miscPanel.setBorder(createSectionBorder("Kỹ Năng & Hội Thoại"));
        miscPanel.setOpaque(false);
        
        // Skill Table
        String[] skillCols = {"Tên Chiêu / Set", "Level", "Delay (ms)"};
        modelSkills = new DefaultTableModel(skillCols, 0);
        tableSkills = new JTable(modelSkills);
        tableSkills.setRowHeight(25);
        tableSkills.getTableHeader().setFont(FONT_BOLD);
        JScrollPane scrollTable = new JScrollPane(tableSkills);
        scrollTable.setPreferredSize(new Dimension(0, 100));
        scrollTable.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JPanel pSkillBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pSkillBtns.setOpaque(false);
        JButton btnAddSkill = createStyledButton("+ Thêm Skill / Set", new Color(40, 167, 69), Color.WHITE);
        JButton btnDelSkill = createStyledButton("- Xóa", new Color(220, 53, 69), Color.WHITE);
        pSkillBtns.add(btnAddSkill); pSkillBtns.add(btnDelSkill);

        btnAddSkill.addActionListener(e -> openSkillSelectionDialog());
        btnDelSkill.addActionListener(e -> {
            if (tableSkills.getSelectedRow() >= 0) modelSkills.removeRow(tableSkills.getSelectedRow());
        });

        // Chat Area
        txtBossChat = new JTextArea(8, 20);
        txtBossChat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBossChat.setLineWrap(true);
        txtBossChat.setWrapStyleWord(true);
        JScrollPane scrollChat = new JScrollPane(txtBossChat);
        scrollChat.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Nội Dung Hội Thoại (Xuống dòng để tách câu)", 
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, FONT_PLAIN, Color.GRAY));
        
        // Layout Misc Panel
        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        miscPanel.add(new JLabel("Skills:"), g);
        
        g.gridx = 1; g.weightx = 1;
        JPanel pSkillCont = new JPanel(new BorderLayout(0, 5));
        pSkillCont.setOpaque(false);
        pSkillCont.add(scrollTable, BorderLayout.CENTER);
        pSkillCont.add(pSkillBtns, BorderLayout.SOUTH);
        miscPanel.add(pSkillCont, g);

        g.gridx = 0; g.gridy = 1; g.gridwidth = 2; g.weightx = 1; g.fill = GridBagConstraints.BOTH; g.weighty = 1.0;
        g.insets = new Insets(10, 5, 5, 5);
        miscPanel.add(scrollChat, g);

        // Add all panels to form
        formPanel.add(infoPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(appearPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(locPanel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(miscPanel);

        // --- BOTTOM BUTTON ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 13));
        lblStatus.setForeground(new Color(0, 150, 0));

        JButton btnSaveAll = createStyledButton("LƯU TẤT CẢ (BATCH SAVE)", new Color(200, 50, 0), Color.WHITE);
        btnSaveAll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveAll.setPreferredSize(new Dimension(250, 45));
        btnSaveAll.addActionListener(e -> commitAllChangesToFile());

        btnPanel.add(lblStatus);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(btnSaveAll);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        p.add(leftPanel, BorderLayout.WEST);
        p.add(rightPanel, BorderLayout.CENTER);
        return p;
    }

    // --- LOGIC LOAD LIST TỪ CLASS ---
    private void reloadBossListFromClass() {
        originalBossKeys.clear();
        try {
            Field[] fields = BossesData.class.getFields();
            for (Field field : fields) {
                if (field.getType() == BossData.class) {
                    originalBossKeys.add(field.getName());
                }
            }
        } catch (Exception e) {
            originalBossKeys.add("Error Loading");
        }
    }

    // --- LOGIC HIỂN THỊ DỮ LIỆU ---
    private void loadBossDataToUI(String bossKey) {
        if (bossKey == null) return;
        this.currentBossKey = bossKey;
        try {
            Field field = BossesData.class.getField(bossKey);
            currentSelectedBossData = (BossData) field.get(null);

            txtBossName.setText(currentSelectedBossData.getName());
            
            StringBuilder sbHp = new StringBuilder();
            if (currentSelectedBossData.getHp() != null) {
                for (long hp : currentSelectedBossData.getHp()) {
                    sbHp.append(formatLong(hp)).append("L, ");
                }
                if (sbHp.length() > 2) sbHp.setLength(sbHp.length() - 2);
            }
            txtBossHp.setText(sbHp.toString());
            
            txtBossDame.setText(String.valueOf(currentSelectedBossData.getDame()));
            txtBossMap.setText(Arrays.toString(currentSelectedBossData.getMapJoin()).replace("[", "").replace("]", ""));

            // Outfit
            short[] outfit = currentSelectedBossData.getOutfit();
            if (outfit != null && outfit.length >= 6) {
                txtOutfitHead.setText(String.valueOf(outfit[0]));
                txtOutfitBody.setText(String.valueOf(outfit[1]));
                txtOutfitLeg.setText(String.valueOf(outfit[2]));
                txtOutfitBag.setText(String.valueOf(outfit[3]));
                txtOutfitAura.setText(String.valueOf(outfit[4]));
                txtOutfitEff.setText(String.valueOf(outfit[5]));
            }

            // Logic nhận diện Respawn / AppearType
            if (currentSelectedBossData.getTypeAppear() != TypeAppear.DEFAULT_APPEAR) {
                cboBossRespawn.setSelectedItem(currentSelectedBossData.getTypeAppear());
            } else {
                int seconds = currentSelectedBossData.getSecondsRest();
                String restConstant = REST_TIME_MAP.getOrDefault(seconds, "");
                if (!restConstant.isEmpty()) {
                    cboBossRespawn.setSelectedItem(restConstant);
                } else {
                    cboBossRespawn.setSelectedItem(null); 
                }
            }
            
            // --- ĐỌC FILE SOURCE ĐỂ LẤY BOSS ĐI KÈM ---
            String related = readRelatedBossesFromSource(bossKey);
            txtRelatedBosses.setText(related);

            // Skills
            modelSkills.setRowCount(0);
            int[][] skills = currentSelectedBossData.getSkillTemp();
            if (skills != null) {
                int i = 0;
                while (i < skills.length) {
                    String detectedSet = detectSet(skills, i);
                    if (detectedSet != null) {
                        modelSkills.addRow(new Object[]{"Set: " + detectedSet, "-", "-"});
                        i += 7;
                    } else {
                        int[] sk = skills[i];
                        if (sk.length >= 3) {
                             modelSkills.addRow(new Object[]{getSkillName(sk[0]), sk[1], sk[2]});
                        }
                        i++;
                    }
                }
            }

            // Chat
            StringBuilder chatBuilder = new StringBuilder();
            if (currentSelectedBossData.getTextS() != null) {
                for (String s : currentSelectedBossData.getTextS()) chatBuilder.append(s).append("\n");
            }
            if (currentSelectedBossData.getTextM() != null) {
                for (String s : currentSelectedBossData.getTextM()) chatBuilder.append(s).append("\n");
            }
            if (currentSelectedBossData.getTextE() != null) {
                for (String s : currentSelectedBossData.getTextE()) chatBuilder.append(s).append("\n");
            }
            txtBossChat.setText(chatBuilder.toString().trim());

            lblStatus.setText("Đang chỉnh sửa: " + bossKey);

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Lỗi đọc dữ liệu: " + e.getMessage());
        }
    }
    
    // --- HÀM MỚI: ĐỌC BOSS ĐI KÈM TỪ FILE SOURCE ---
    private String readRelatedBossesFromSource(String bossKey) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(SOURCE_FILE_PATH));
            boolean foundBoss = false;
            StringBuilder sb = new StringBuilder();
            
            for (String line : lines) {
                if (line.contains("public static final BossData " + bossKey + " ")) {
                    foundBoss = true;
                }
                if (foundBoss) {
                    sb.append(line.trim());
                    if (line.trim().endsWith(");")) {
                        break; 
                    }
                }
            }
            
            String fullStr = sb.toString();
            // Regex to find related bosses array content
            Pattern p = Pattern.compile(",\\s*new\\s*int\\[\\]\\{(.*?)\\}\\s*\\)\\s*;");
            Matcher m = p.matcher(fullStr);
            if (m.find()) {
                return m.group(1).trim(); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    // --- DIALOG CHỌN KỸ NĂNG ---
    private void openSkillSelectionDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Kỹ Năng", true);
        d.setSize(500, 600);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());
        
        JTabbedPane tab = new JTabbedPane();
        
        if (cachedSkillOptions == null) {
            cachedSkillOptions = new ArrayList<>();
            Set<Integer> addedIds = new HashSet<>(); 
             try (Connection conn = getConnection(); Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name, icon_id FROM skill_template ORDER BY id ASC")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    if (!addedIds.contains(id)) {
                        cachedSkillOptions.add(new SkillOption(id, rs.getString("name"), rs.getInt("icon_id"), getIconByIconId(rs.getInt("icon_id"), 25)));
                        addedIds.add(id);
                    }
                }
            } catch (Exception e) {}
        }

        JPanel pSingle = new JPanel(new BorderLayout());
        JList<SkillOption> listSkills = new JList<>(new Vector<>(cachedSkillOptions));
        listSkills.setFixedCellHeight(35);
        listSkills.setCellRenderer(new DefaultListCellRenderer() {
             public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SkillOption) {
                    SkillOption skill = (SkillOption) value;
                    lbl.setText(skill.toString());
                    if (skill.icon != null) lbl.setIcon(skill.icon);
                    lbl.setIconTextGap(10);
                }
                return lbl;
             }
        });

        JTextField txtSearchSkill = new JTextField();
        txtSearchSkill.setBorder(BorderFactory.createTitledBorder("Tìm kiếm Kỹ Năng..."));
        txtSearchSkill.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String q = txtSearchSkill.getText().toLowerCase();
                Vector<SkillOption> filtered = new Vector<>();
                for (SkillOption s : cachedSkillOptions) {
                    if (s.name.toLowerCase().contains(q) || String.valueOf(s.id).contains(q)) filtered.add(s);
                }
                listSkills.setListData(filtered);
            }
        });

        Runnable addSingleAction = () -> {
            SkillOption sel = listSkills.getSelectedValue();
            if (sel != null) {
                modelSkills.addRow(new Object[]{sel.toString(), "7", "1000"});
                d.dispose();
            }
        };

        listSkills.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSingleAction.run();
                }
            }
        });

        JButton btnSelectSingle = createStyledButton("Thêm Skill Lẻ", new Color(40, 167, 69), Color.WHITE);
        btnSelectSingle.setPreferredSize(new Dimension(0, 40));
        btnSelectSingle.addActionListener(e -> addSingleAction.run());
        
        pSingle.add(txtSearchSkill, BorderLayout.NORTH);
        pSingle.add(new JScrollPane(listSkills), BorderLayout.CENTER);
        pSingle.add(btnSelectSingle, BorderLayout.SOUTH);
        
        JPanel pSets = new JPanel(new BorderLayout());
        Vector<String> setNames = new Vector<>(FULL_SETS.keySet());
        JList<String> listSets = new JList<>(setNames);
        listSets.setFixedCellHeight(40);
        listSets.setFont(new Font("Segoe UI", Font.BOLD, 13));
        listSets.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String setName = (String) value;
                ImageIcon icon = null;
                if (FULL_SETS.containsKey(setName)) {
                    int[][] skills = FULL_SETS.get(setName);
                    if (skills.length > 0) {
                        int firstSkillId = skills[0][0];
                        for (SkillOption opt : cachedSkillOptions) {
                            if (opt.id == firstSkillId) {
                                icon = opt.icon;
                                break;
                            }
                        }
                    }
                }
                if (icon != null) {
                    l.setIcon(icon);
                } else {
                    l.setIcon(getIconByIconId(1234, 25));
                }
                l.setBorder(new EmptyBorder(0, 10, 0, 0));
                return l;
            }
        });

        Runnable addSetAction = () -> {
            String val = listSets.getSelectedValue();
            if (val != null) {
                modelSkills.addRow(new Object[]{"Set: " + val, "-", "-"});
                d.dispose();
            }
        };
        
        listSets.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSetAction.run();
                }
            }
        });
        
        JButton btnSelectSet = createStyledButton("Thêm Bộ Skill (Full Set)", new Color(0, 123, 255), Color.WHITE);
        btnSelectSet.setPreferredSize(new Dimension(0, 40));
        btnSelectSet.addActionListener(e -> addSetAction.run());

        pSets.add(new JScrollPane(listSets), BorderLayout.CENTER);
        pSets.add(btnSelectSet, BorderLayout.SOUTH);
        
        tab.addTab("Skill Đơn Lẻ", pSingle);
        tab.addTab("Bộ Skill (Full Set)", pSets);
        
        d.add(tab, BorderLayout.CENTER);
        d.setVisible(true);
    }

    // --- DIALOG MAP ---
    private void openMapSearchDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tìm Kiếm Map (map_template)", true);
        d.setSize(600, 650);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JTextField txtSearchMap = new JTextField();
        txtSearchMap.setBorder(BorderFactory.createTitledBorder("Nhập Tên Map hoặc ID..."));
        
        DefaultTableModel m = new DefaultTableModel(new Object[]{"Chọn", "ID", "Tên Map"}, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) { return columnIndex == 0 ? Boolean.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
        };
        
        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setFont(FONT_PLAIN);
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(0).setMaxWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(60);
        t.getColumnModel().getColumn(1).setMaxWidth(80);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        t.setRowSorter(sorter);

        Set<String> existingIds = new HashSet<>();
        String currentText = txtBossMap.getText().trim();
        if (!currentText.isEmpty()) {
            String[] parts = currentText.split(",");
            for (String part : parts) existingIds.add(part.trim());
        }

        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name FROM map_template")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    boolean isChecked = existingIds.contains(String.valueOf(id));
                    SwingUtilities.invokeLater(() -> m.addRow(new Object[]{isChecked, id, name}));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        txtSearchMap.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String text = txtSearchMap.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else {
                    var idFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1);
                    var nameFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2);
                    sorter.setRowFilter(RowFilter.orFilter(Arrays.asList(idFilter, nameFilter)));
                }
            }
        });

        Runnable addCheckedMaps = () -> {
            List<String> idsToAdd = new ArrayList<>();
            for (int i = 0; i < m.getRowCount(); i++) {
                Boolean isChecked = (Boolean) m.getValueAt(i, 0);
                if (isChecked != null && isChecked) {
                    idsToAdd.add(m.getValueAt(i, 1).toString());
                }
            }
            if (idsToAdd.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Bạn chưa tích chọn map nào!");
                return;
            }
            String newText = String.join(",", idsToAdd);
            txtBossMap.setText(newText);
            d.dispose();
        };

        JButton btnAddSelected = createStyledButton("Xác Nhận Map Đã Chọn", new Color(40, 167, 69), Color.WHITE);
        btnAddSelected.setPreferredSize(new Dimension(0, 50));
        btnAddSelected.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddSelected.addActionListener(e -> addCheckedMaps.run());

        d.add(txtSearchMap, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.add(btnAddSelected, BorderLayout.SOUTH);
        d.setVisible(true);
    }

    // --- DIALOG CAI TRANG ---
    private void openCaiTrangSearchDialog() {
        JDialog d = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tìm Kiếm Cải Trang (Type 5)", true);
        d.setSize(650, 550);
        d.setLocationRelativeTo(this);
        d.setLayout(new BorderLayout());

        JTextField txtSearchCT = new JTextField();
        txtSearchCT.setBorder(BorderFactory.createTitledBorder("Nhập Tên hoặc ID Cải Trang..."));
        
        DefaultTableModel m = new DefaultTableModel(new String[]{"ID", "Icon", "Tên Item", "Head", "Body", "Leg"}, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 1 ? ImageIcon.class : Object.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        JTable t = new JTable(m);
        t.setRowHeight(35);
        t.setFont(FONT_PLAIN);
        t.getColumnModel().getColumn(0).setPreferredWidth(50);
        t.getColumnModel().getColumn(1).setPreferredWidth(40);
        t.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(m);
        t.setRowSorter(sorter);

        new Thread(() -> {
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, name, icon_id, head, body, leg FROM item_template WHERE type = 5")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int iconId = rs.getInt("icon_id");
                    int head = rs.getInt("head");
                    int body = rs.getInt("body");
                    int leg = rs.getInt("leg");
                    ImageIcon icon = getIconByIconId(iconId, 28);
                    SwingUtilities.invokeLater(() -> m.addRow(new Object[]{id, icon, name, head, body, leg}));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();

        txtSearchCT.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { f(); }
            public void removeUpdate(DocumentEvent e) { f(); }
            public void changedUpdate(DocumentEvent e) { f(); }
            void f() {
                String text = txtSearchCT.getText().trim();
                if (text.isEmpty()) sorter.setRowFilter(null);
                else {
                    var idFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0);
                    var nameFilter = RowFilter.regexFilter("(?i)" + Pattern.quote(text), 2);
                    sorter.setRowFilter(RowFilter.orFilter(Arrays.asList(idFilter, nameFilter)));
                }
            }
        });

        t.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int r = t.getSelectedRow();
                    if (r != -1) {
                        int modelRow = t.convertRowIndexToModel(r);
                        txtOutfitHead.setText(m.getValueAt(modelRow, 3).toString());
                        txtOutfitBody.setText(m.getValueAt(modelRow, 4).toString());
                        txtOutfitLeg.setText(m.getValueAt(modelRow, 5).toString());
                        d.dispose();
                    }
                }
            }
        });

        d.add(txtSearchCT, BorderLayout.NORTH);
        d.add(new JScrollPane(t), BorderLayout.CENTER);
        d.setVisible(true);
    }

    private void filterBossList() {
        String query = txtSearchBoss.getText().trim().toLowerCase();
        Vector<String> filtered = new Vector<>();
        if (originalBossKeys != null) {
            for (String key : originalBossKeys) {
                if (key.toLowerCase().contains(query)) filtered.add(key);
            }
        }
        listBosses.setListData(filtered);
    }

    private void onBossSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;

        if (previousBossKey != null && !previousBossKey.isEmpty()) {
            saveCurrentConfigToMemory(previousBossKey);
        }

        String selectedKey = listBosses.getSelectedValue();
        if (selectedKey != null) {
            currentBossKey = selectedKey;
            previousBossKey = currentBossKey; 
            loadBossDataToUI(selectedKey);
        }
    }
    
    private String detectSet(int[][] source, int startIndex) {
        if (startIndex + 7 > source.length) return null;
        for (Map.Entry<String, int[][]> entry : FULL_SETS.entrySet()) {
            int[][] setSkills = entry.getValue();
            boolean match = true;
            for (int k = 0; k < 7; k++) {
                int[] srcSkill = source[startIndex + k];
                int[] setSkill = setSkills[k];
                if (srcSkill[0] != setSkill[0] || srcSkill[1] != setSkill[1]) {
                    match = false;
                    break;
                }
            }
            if (match) return entry.getKey();
        }
        return null;
    }

    private void saveCurrentConfigToMemory(String keyToSave) {
        try {
            // 1. DATA PREPARATION
            String nameRaw = txtBossName.getText().trim();
            long dameRaw = parseLong(txtBossDame.getText());

            String[] hpParts = txtBossHp.getText().split(",");
            long[] hpRaw = new long[hpParts.length];
            for (int i = 0; i < hpParts.length; i++) hpRaw[i] = parseLong(hpParts[i]);

            String[] outParts = {txtOutfitHead.getText(), txtOutfitBody.getText(), txtOutfitLeg.getText(),
                    txtOutfitBag.getText(), txtOutfitAura.getText(), txtOutfitEff.getText()};
            short[] outfitRaw = new short[6];
            for (int i = 0; i < 6; i++) {
                String val = outParts[i].trim().isEmpty() ? "-1" : outParts[i].trim();
                outfitRaw[i] = Short.parseShort(val);
            }

            String mapTxt = txtBossMap.getText().trim();
            int[] mapRaw;
            if (mapTxt.isEmpty()) mapRaw = new int[0];
            else {
                String[] mSplit = mapTxt.split(",");
                mapRaw = new int[mSplit.length];
                for (int i=0; i<mSplit.length; i++) mapRaw[i] = Integer.parseInt(mSplit[i].trim());
            }

            int[][] skillRaw = getRawSkillsFromTable(modelSkills);

            String[] chatLines = txtBossChat.getText().split("\n");
            List<String> validChats = new ArrayList<>();
            for (String s : chatLines) {
                if (!s.trim().isEmpty()) validChats.add(s.trim());
            }
            String[] chatRaw = validChats.toArray(new String[0]);

            Object selectedOpt = cboBossRespawn.getSelectedItem();
            int respawnValueRaw = 1000;
            if (selectedOpt instanceof RespawnOption) {
                respawnValueRaw = ((RespawnOption)selectedOpt).value;
            } else if (currentSelectedBossData != null) {
                respawnValueRaw = currentSelectedBossData.getSecondsRest();
            }
            
            byte genderRaw = 0; 
            if (currentSelectedBossData != null) genderRaw = currentSelectedBossData.getGender();

            // 2. GENERATE BEAUTIFUL CODE (Shift + Alt + F style)
            String newCode = generateBeautifulBossCode(keyToSave, nameRaw, genderRaw, outfitRaw, dameRaw, hpRaw, mapTxt, chatRaw, selectedOpt, respawnValueRaw);
            
            pendingChanges.put(keyToSave, newCode);
            
            // 3. UPDATE RAM
            if (currentBossKey.equals(keyToSave) && currentSelectedBossData != null) {
                updateObjectInMemory(currentSelectedBossData, nameRaw, genderRaw, outfitRaw, dameRaw, hpRaw, mapRaw, skillRaw, chatRaw, respawnValueRaw);
            } else {
                try {
                      Field field = BossesData.class.getField(keyToSave);
                      BossData oldBossData = (BossData) field.get(null);
                      if (oldBossData != null) {
                          updateObjectInMemory(oldBossData, nameRaw, genderRaw, outfitRaw, dameRaw, hpRaw, mapRaw, skillRaw, chatRaw, respawnValueRaw);
                      }
                } catch(Exception ex) {}
            }
            
        } catch (Exception e) {
            System.out.println("Lỗi parse data khi chuyển boss: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // --- IDE FORMATTER FUNCTION (SHIFT + ALT + F STYLE) ---
    private String generateBeautifulBossCode(String key, String name, byte gender, short[] outfit, long dame, long[] hp, String mapTxt, String[] chat, Object respawnOpt, int respawnVal) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("    public static final BossData ").append(key).append(" = new BossData(\n");
        
        // Name
        sb.append("            \"").append(name).append("\", //name\n");
        
        // Gender
        String genderCode = "ConstPlayer.TRAI_DAT";
        if(gender == 1) genderCode = "ConstPlayer.NAMEC";
        if(gender == 2) genderCode = "ConstPlayer.XAYDA";
        sb.append("            ").append(genderCode).append(", //gender\n");
        
        // Outfit
        StringBuilder outSb = new StringBuilder("new short[]{");
        for(int i=0; i<6; i++) outSb.append(outfit[i]).append(i<5?", ":"");
        outSb.append("}");
        sb.append("            ").append(outSb.toString()).append(", //outfit\n");
        
        // Dame
        sb.append("            ").append(dame).append(", //dame\n");
        
        // HP
        StringBuilder hpSb = new StringBuilder("new long[]{");
        for(int i=0; i<hp.length; i++) hpSb.append(hp[i]).append("L").append(i<hp.length-1?", ":"");
        hpSb.append("}");
        sb.append("            ").append(hpSb.toString()).append(", //hp\n");
        
        // Map
        sb.append("            new int[]{").append(mapTxt).append("}, //map\n");
        
        // Skill
        String skillCode = getSkillCodeFromTable(modelSkills);
        sb.append("            ").append(skillCode).append(", //skill\n");
        
        // Chat 1 (Default Empty)
        sb.append("            new String[]{}, //text chat 1\n");
        
        // Chat 2
        StringBuilder chatSb = new StringBuilder("new String[]{");
        if (chat.length > 0) {
            for(int i=0; i<chat.length; i++) {
                chatSb.append("\"").append(chat[i].replace("\"", "\\\"")).append("\"").append(i<chat.length-1?", ":"");
            }
        }
        chatSb.append("}");
        sb.append("            ").append(chatSb.toString()).append(", //text chat 2\n");
        
        // Chat 3 (Default Empty)
        sb.append("            new String[]{}, //text chat 3\n");
        
        // Respawn / Appear
        String respawnCode;
        if (respawnOpt instanceof TypeAppear) {
            respawnCode = "AppearType." + respawnOpt.toString();
        } else if (respawnOpt instanceof String) {
            respawnCode = (String) respawnOpt;
        } else {
            respawnCode = String.valueOf(respawnVal);
        }
        
        String related = txtRelatedBosses.getText().trim();
        if (related.isEmpty()) {
            sb.append("            ").append(respawnCode).append("\n");
            sb.append("    )");
        } else {
            sb.append("            ").append(respawnCode).append(",\n");
            sb.append("            new int[]{").append(related).append("}\n");
            sb.append("    )");
        }
        
        return sb.toString();
    }
    
    private String getSkillCodeFromTable(DefaultTableModel model) {
        List<String> arrayParts = new ArrayList<>();
        List<String> manualParts = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 0).toString();
            if (name.startsWith("Set: ")) {
                String setName = name.replace("Set: ", "").trim();
                arrayParts.add(setName);
            } else {
                int id = getSkillIdFromName(name);
                int level = Integer.parseInt(model.getValueAt(i, 1).toString());
                int delay = Integer.parseInt(model.getValueAt(i, 2).toString());
                String constName = getSkillConstantName(id);
                manualParts.add("{" + constName + ", " + level + ", " + delay + "}");
            }
        }

        String manualArrayCode = "";
        if (!manualParts.isEmpty()) {
            manualArrayCode = "new int[][]{" + String.join(", ", manualParts) + "}";
        }

        if (arrayParts.isEmpty()) {
            return manualParts.isEmpty() ? "new int[][]{}" : manualArrayCode;
        }

        StringBuilder sb = new StringBuilder("(int[][]) Util.addArray(");
        for (String set : arrayParts) {
            sb.append(set).append(", ");
        }
        
        if (!manualArrayCode.isEmpty()) {
            sb.append(manualArrayCode);
        } else {
            sb.setLength(sb.length() - 2); 
        }
        sb.append(")");
        return sb.toString();
    }
    
    private int[][] getRawSkillsFromTable(DefaultTableModel model) {
        List<int[]> allSkills = new ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String name = model.getValueAt(i, 0).toString();
            if (name.startsWith("Set: ")) {
                String setName = name.replace("Set: ", "").trim();
                if (FULL_SETS.containsKey(setName)) {
                    int[][] set = FULL_SETS.get(setName);
                    for (int[] sk : set) {
                        int[] fullSk = new int[3];
                        fullSk[0] = sk[0]; // ID
                        fullSk[1] = sk[1]; // Level
                        fullSk[2] = (sk.length > 2) ? sk[2] : 1000; // Default Delay
                        allSkills.add(fullSk);
                    }
                }
            } else {
                int id = getSkillIdFromName(name);
                int level = Integer.parseInt(model.getValueAt(i, 1).toString());
                int delay = Integer.parseInt(model.getValueAt(i, 2).toString());
                allSkills.add(new int[]{id, level, delay});
            }
        }
        
        int[][] result = new int[allSkills.size()][3];
        for(int k=0; k<allSkills.size(); k++) result[k] = allSkills.get(k);
        return result;
    }

    private void updateObjectInMemory(BossData data, String name, byte gender, short[] outfit, long dame, long[] hp, int[] map, int[][] skill, String[] chat, int respawn) {
        try {
            setField(data, "name", name);
            setField(data, "gender", gender);
            setField(data, "outfit", outfit);
            setField(data, "dame", dame);
            setField(data, "hp", hp);
            setField(data, "mapJoin", map);
            setField(data, "skillTemp", skill);
            setField(data, "textM", chat);
            setField(data, "secondsRest", respawn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setField(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {}
    }

    private void commitAllChangesToFile() {
        if (currentBossKey != null) {
            saveCurrentConfigToMemory(currentBossKey);
        }

        if (pendingChanges.isEmpty()) {
            lblStatus.setText("Không có thay đổi nào để lưu!");
            lblStatus.setForeground(Color.ORANGE);
            return;
        }

        File sourceFile = new File(SOURCE_FILE_PATH);
        if (!sourceFile.exists()) {
            lblStatus.setText("Lỗi: Không tìm thấy file Source!");
            lblStatus.setForeground(Color.RED);
            return;
        }

        try {
            String content = Files.readString(sourceFile.toPath());
            int countUpdated = 0;

            for (Map.Entry<String, String> entry : pendingChanges.entrySet()) {
                String key = entry.getKey();
                String newCode = entry.getValue();
                String searchStart = "public static final BossData " + key + " = new BossData(";
                int startIndex = content.indexOf(searchStart);

                if (startIndex != -1) {
                    int endIndex = -1;
                    int openParens = 0;
                    for (int i = startIndex; i < content.length(); i++) {
                        char c = content.charAt(i);
                        if (c == '(') openParens++;
                        else if (c == ')') openParens--;
                        else if (c == ';' && openParens == 0) {
                            endIndex = i;
                            break;
                        }
                    }
                    if (endIndex != -1) {
                        content = content.substring(0, startIndex) + newCode + ";" + content.substring(endIndex + 1);
                        countUpdated++;
                    }
                }
            }

            Files.writeString(sourceFile.toPath(), content, StandardOpenOption.TRUNCATE_EXISTING);

            pendingChanges.clear(); 
            listBosses.repaint();
            
            lblStatus.setText("Đã Cập Nhật & Lưu File: " + countUpdated + " Boss!");
            lblStatus.setForeground(new Color(0, 150, 0));

            javax.swing.Timer t = new javax.swing.Timer(3000, evt -> lblStatus.setText(""));
            t.setRepeats(false);
            t.start();

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Lỗi Exception: " + e.getMessage());
            lblStatus.setForeground(Color.RED);
        }
    }

    private JTextField createStyledTextField(Color textColor) {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt.setForeground(textColor);
        txt.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)), 
            new EmptyBorder(5, 8, 5, 8)
        ));
        return txt;
    }

    private JTextField createStyledOutfitField(String title) {
        JTextField txt = new JTextField("-1");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txt.setHorizontalAlignment(JTextField.CENTER);
        txt.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            title, 
            TitledBorder.CENTER, 
            TitledBorder.TOP, 
            new Font("Segoe UI", Font.PLAIN, 10), 
            Color.GRAY
        ));
        return txt;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(new CompoundBorder(
            new LineBorder(bg.darker(), 1),
            new EmptyBorder(5, 15, 5, 15)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private Border createSectionBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)), 
            title, 
            TitledBorder.DEFAULT_JUSTIFICATION, 
            TitledBorder.DEFAULT_POSITION, 
            new Font("Segoe UI", Font.BOLD, 12), 
            new Color(0, 102, 204)
        );
    }

    private void addFormRow(JPanel panel, GridBagConstraints g, int y, String labelText, JComponent comp) {
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_BOLD);
        panel.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        panel.add(comp, g);
    }

    private String formatLong(long val) {
        return String.format("%,d", val).replace(",", "_");
    }

//    private long parseLong(String val) throws NumberFormatException {
//        if (val == null || val.trim().isEmpty()) return 0;
//        String clean = val.replaceAll("[_,lL\\s]", "").trim();
//        return Long.parseLong(clean);
//    }
private long parseLong(String val) throws NumberFormatException {
    return parseNumberLong(val);
}

private long parseNumberLong(String val) throws NumberFormatException {
    if (val == null) return 0L;
    String s = val.trim();
    if (s.isEmpty()) return 0L;
    s = s.replace(".", "")
         .replace(",", "")
         .replace("_", "")
         .replace(" ", "")
         .replace("L", "")
         .replace("l", "");

    if (s.isEmpty() || s.equals("-")) return 0L;

    return Long.parseLong(s);
}
private int parseInt(String val) throws NumberFormatException {
    long v = parseNumberLong(val);
    if (v > Integer.MAX_VALUE) {
        throw new NumberFormatException("Value too large for int: " + v);
    }
    if (v < Integer.MIN_VALUE) {
        throw new NumberFormatException("Value too small for int: " + v);
    }
    return (int) v;
}

    private int getSkillIdFromName(String name) {
        try { return Integer.parseInt(name.split("\\.")[0]); } catch (Exception e) { return 0; }
    }

    private String getSkillName(int id) {
        return switch (id) {
            case 0 -> "0. Dragon"; case 1 -> "1. Kamejoko"; case 2 -> "2. Demon";
            case 3 -> "3. Masenko"; case 4 -> "4. Galick"; case 5 -> "5. Antomic";
            case 6 -> "6. Thái Dương HS"; case 7 -> "7. Trị Thương"; case 8 -> "8. Tái Tạo NL";
            case 9 -> "9. Kaioken"; case 10 -> "10. QC Kênh Khi"; case 11 -> "11. Makankosappo";
            case 12 -> "12. Đẻ Trứng"; case 13 -> "13. Biến Khỉ"; case 14 -> "14. Tự Sát";
            case 17 -> "17. Liên Hoàn"; case 18 -> "18. Biến Socola"; case 19 -> "19. Khiên NL";
            case 20 -> "20. Dịch Chuyển"; case 21 -> "21. Huýt Sáo"; case 22 -> "22. Thôi Miên";
            case 23 -> "23. Trói"; 
            case 24 -> "24. Super Kame"; case 25 -> "25. Liên Hoàn Chưởng"; case 26 -> "26. Ma Phong Ba";
            case 27 -> "27. Biến Hình"; case 28 -> "28. Phân Thân";
            default -> id + ". Skill Khác";
        };
    }

    private String getSkillConstantName(int id) {
        return switch (id) {
            case 0 -> "Skill.DRAGON"; case 1 -> "Skill.KAMEJOKO"; case 2 -> "Skill.DEMON";
            case 3 -> "Skill.MASENKO"; case 4 -> "Skill.GALICK"; case 5 -> "Skill.ANTOMIC";
            case 6 -> "Skill.THAI_DUONG_HA_SAN"; case 7 -> "Skill.TRI_THUONG"; case 8 -> "Skill.TAI_TAO_NANG_LUONG";
            case 9 -> "Skill.KAIOKEN"; case 10 -> "Skill.QUA_CAU_KENH_KHI"; case 11 -> "Skill.MAKANKOSAPPO";
            case 12 -> "Skill.DE_TRUNG"; case 13 -> "Skill.BIEN_KHI"; case 14 -> "Skill.TU_SAT";
            case 17 -> "Skill.LIEN_HOAN"; case 18 -> "Skill.SOCOLA"; case 19 -> "Skill.KHIEN_NANG_LUONG";
            case 20 -> "Skill.DICH_CHUYEN_TUC_THOI"; case 21 -> "Skill.HUYT_SAO"; case 22 -> "Skill.THOI_MIEN";
            case 23 -> "Skill.TROI"; 
            case 24 -> "Skill.SUPER_KAME"; case 25 -> "Skill.LIEN_HOAN_CHUONG"; case 26 -> "Skill.MA_PHONG_BA";
            case 27 -> "Skill.BIEN_HINH"; case 28 -> "Skill.PHAN_THAN";
            default -> String.valueOf(id);
        };
    }
}