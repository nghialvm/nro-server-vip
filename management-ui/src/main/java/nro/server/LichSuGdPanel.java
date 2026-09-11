package nro.server;

import jbcd.ConnectDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LichSuGdPanel extends JPanel {

    private JTextField txtSearch;
    private JButton btnSearch, btnReload;
    private JTable table;
    private DefaultTableModel model;

    // màu zebra giống ảnh (nhẹ)
    private static final Color ROW_ODD  = new Color(245, 245, 245);
    private static final Color ROW_EVEN = Color.WHITE;

    // màu kẻ line giống ảnh
    private static final Color GRID_COLOR = new Color(210, 210, 210);

    // ✅ FIX: chiều cao mặc định + giới hạn max để tránh "phình"
    private static final int BASE_ROW_HEIGHT = 28;
    private static final int MAX_ROW_HEIGHT  = 160;

    public LichSuGdPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        initUI();
        loadData(""); // load mặc định 200 dòng mới nhất
    }

    private void initUI() {
        // ====== TOP BAR ======
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);
        top.setBorder(new TitledBorder("Lịch sử giao dịch"));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setOpaque(false);

        left.add(new JLabel("Tìm player:"));
        txtSearch = new JTextField(22);
        txtSearch.setToolTipText("Nhập tên player - tìm theo prefix");
        left.add(txtSearch);

        btnSearch = new JButton("Xem");
        btnReload = new JButton("Tải lại");

        left.add(btnSearch);
        left.add(btnReload);

        top.add(left, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        // ====== TABLE ======
        String[] cols = {"ID", "Player1", "Player2", "Item P1", "Item P2", "Thời gian"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(BASE_ROW_HEIGHT);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // ✅ KẺ CỘT / KẺ DÒNG giống hình bạn gửi
        table.setShowGrid(true);
        table.setGridColor(GRID_COLOR);
        table.setIntercellSpacing(new Dimension(1, 1));

        // header nhẹ
        table.getTableHeader().setBackground(new Color(235, 235, 235));
        table.getTableHeader().setForeground(new Color(40, 40, 40));

        // Renderer zebra cho tất cả cột thường
        ZebraCellRenderer zebra = new ZebraCellRenderer();
        table.setDefaultRenderer(Object.class, zebra);

        // Renderer multiline cho cột item (zebra luôn)
        MultiLineCellRenderer multi = new MultiLineCellRenderer();
        table.getColumnModel().getColumn(3).setCellRenderer(multi);
        table.getColumnModel().getColumn(4).setCellRenderer(multi);

        // Căn lề & width
        setColumnWidth(0, 70);
        setColumnWidth(1, 160);
        setColumnWidth(2, 160);
        setColumnWidth(5, 170);

        // Cột thời gian căn trái + zebra
        ZebraTimeRenderer timeRenderer = new ZebraTimeRenderer();
        table.getColumnModel().getColumn(5).setCellRenderer(timeRenderer);

        JScrollPane scroll = new JScrollPane(table);

        // ✅ (Khuyến nghị) giữ kích thước viewport ổn định để không "nở"
        table.setPreferredScrollableViewportSize(new Dimension(950, 520));

        add(scroll, BorderLayout.CENTER);

        // ====== EVENTS ======
        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        btnReload.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });

        // enter để search luôn
        txtSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
    }

    private void setColumnWidth(int col, int width) {
        TableColumn c = table.getColumnModel().getColumn(col);
        c.setPreferredWidth(width);
        c.setMaxWidth(width);
        c.setMinWidth(width);
    }

    /**
     * Load dữ liệu từ bảng history_transaction
     * searchName: nếu rỗng => load 200 dòng mới nhất
     * nếu có => WHERE player_1 LIKE name% OR player_2 LIKE name%
     */
    private void loadData(String searchName) {
        // disable UI khi load
        btnSearch.setEnabled(false);
        btnReload.setEnabled(false);

        new Thread(() -> {
            List<Object[]> rows = new ArrayList<>();

            String where = "";
            boolean hasSearch = searchName != null && !searchName.isEmpty();
            if (hasSearch) {
                where = " WHERE ht.player_1 LIKE ? OR ht.player_2 LIKE ? ";
            }

            String sql =
                    "SELECT ht.id, ht.player_1, ht.player_2, ht.item_player_1, ht.item_player_2, ht.time_tran " +
                    "FROM history_transaction ht " +
                    where +
                    "ORDER BY ht.id DESC " +
                    "LIMIT 200";

            try (Connection con = ConnectDB.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                if (hasSearch) {
                    String like = searchName + "%";
                    ps.setString(1, like);
                    ps.setString(2, like);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String p1 = rs.getString("player_1");
                        String p2 = rs.getString("player_2");

                        String itemP1 = formatItemsForCell(rs.getString("item_player_1"));
                        String itemP2 = formatItemsForCell(rs.getString("item_player_2"));

                        String time = rs.getString("time_tran");

                        rows.add(new Object[]{id, p1, p2, itemP1, itemP2, time});
                    }
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this,
                        "Lỗi tải lịch sử giao dịch: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                ));
            }

            SwingUtilities.invokeLater(() -> {
                model.setRowCount(0);
                for (Object[] r : rows) model.addRow(r);

                // ✅ FIX: reset + resize đúng (không phình to)
                resizeRowHeights();

                btnSearch.setEnabled(true);
                btnReload.setEnabled(true);
            });

        }).start();
    }

    /**
     * Format giống PHP: mỗi item 1 dòng, tách theo dấu ','.
     * Clean \r \n, nhiều space -> 1 space, trim.
     */
    private String formatItemsForCell(String text) {
        if (text == null || text.trim().isEmpty()) return "—";

        text = text.replace("\r", " ").replace("\n", " ").trim();
        text = text.replaceAll("\\s+", " ");

        String[] parts = text.split(",");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String it = p == null ? "" : p.trim();
            it = it.replaceAll("^[\\t\\s]+", "");
            it = it.replaceAll(",+$", "").trim();

            if (!it.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("• ").append(it);
            }
        }
        return sb.length() == 0 ? "—" : sb.toString();
    }

    /**
     * ✅ FIX: Tự tăng chiều cao row theo nội dung multiline ở cột 3-4
     * - Reset về BASE_ROW_HEIGHT trước (tránh mỗi lần search lại phình thêm)
     * - setSize theo width cột để wrap đúng
     * - cap MAX_ROW_HEIGHT để không bị quá cao
     */
    private void resizeRowHeights() {
        // reset toàn bộ về mặc định trước khi tính lại
        table.setRowHeight(BASE_ROW_HEIGHT);

        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = BASE_ROW_HEIGHT;

            for (int col : new int[]{3, 4}) {
                TableCellRenderer renderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(renderer, row, col);

                int colWidth = table.getColumnModel().getColumn(col).getWidth();

                // Quan trọng: setSize để JTextArea wrap đúng theo width
                comp.setSize(colWidth, Integer.MAX_VALUE);

                int prefH = comp.getPreferredSize().height;
                maxHeight = Math.max(maxHeight, prefH + 6);
            }

            // cap max height để không bị to bất thường
            maxHeight = Math.min(maxHeight, MAX_ROW_HEIGHT);
            table.setRowHeight(row, maxHeight);
        }
    }

    /**
     * Renderer zebra cho cell thường
     */
    private static class ZebraCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);
                c.setForeground(new Color(40, 40, 40));
            }
            return c;
        }
    }

    /**
     * Renderer thời gian: căn trái + zebra
     */
    private static class ZebraTimeRenderer extends DefaultTableCellRenderer {
        public ZebraTimeRenderer() {
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (!isSelected) {
                c.setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);
                c.setForeground(new Color(40, 40, 40));
            }
            return c;
        }
    }

    /**
     * Renderer multiline cho JTable cell (JTextArea) + zebra
     */
    private static class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(new EmptyBorder(6, 8, 6, 8));
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            setText(value == null ? "" : value.toString());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground((row % 2 == 0) ? ROW_EVEN : ROW_ODD);
                setForeground(new Color(40, 40, 40));
            }
            return this;
        }
    }
}
