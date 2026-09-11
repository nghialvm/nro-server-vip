package jbcd.dao;

import Utils.TimeUtil;
import java.sql.*;
import models.Item.Item;
import nro.player.Player;
import jbcd.ConnectDB;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryTransactionDAO {

    public static class TransactionLog {

        public String player1NameRecord;

        public String getPlayer1namerecord() {
            return this.player1NameRecord;
        }

        public void setPlayer1namerecord(String player1NameRecord) {
            this.player1NameRecord = player1NameRecord;
        }

        public String player2NameRecord;

        public String getPlayer2namerecord() {
            return this.player2NameRecord;
        }

        public void setPlayer2namerecord(String player2NameRecord) {
            this.player2NameRecord = player2NameRecord;
        }

        public String itemsExchangedByPlayer1;

        public String getItemsexchangedbyplayer1() {
            return this.itemsExchangedByPlayer1;
        }

        public void setItemsexchangedbyplayer1(String itemsExchangedByPlayer1) {
            this.itemsExchangedByPlayer1 = itemsExchangedByPlayer1;
        }

        public String itemsExchangedByPlayer2;

        public String getItemsexchangedbyplayer2() {
            return this.itemsExchangedByPlayer2;
        }

        public void setItemsexchangedbyplayer2(String itemsExchangedByPlayer2) {
            this.itemsExchangedByPlayer2 = itemsExchangedByPlayer2;
        }

        public Timestamp transactionTime;

        public Timestamp getTransactiontime() {
            return this.transactionTime;
        }

        public void setTransactiontime(Timestamp transactionTime) {
            this.transactionTime = transactionTime;
        }

        public TransactionLog(String p1Name, String p2Name, String p1Items, String p2Items, Timestamp time) {
            this.player1NameRecord = p1Name;
            this.player2NameRecord = p2Name;
            this.itemsExchangedByPlayer1 = p1Items;
            this.itemsExchangedByPlayer2 = p2Items;
            this.transactionTime = time;
        }
    }

    public static void insert(Player pl1, Player pl2,
            int goldP1, int goldP2, List<Item> itemP1, List<Item> itemP2,
            List<Item> bag1Before, List<Item> bag2Before,
            List<Item> bag1After, List<Item> bag2After,
            long gold1Before, long gold2Before, long gold1After, long gold2After) {

        String player1 = pl1.name + " (" + pl1.id + ")";
        String player2 = pl2.name + " (" + pl2.id + ")";
        String itemPlayer1 = formatItemsWithGold(goldP1, itemP1);
        String itemPlayer2 = formatItemsWithGold(goldP2, itemP2);

        String beforeTran1 = formatBagItems(bag1Before);
        String beforeTran2 = formatBagItems(bag2Before);
        String afterTran1 = formatBagItems(bag1After);
        String afterTran2 = formatBagItems(bag2After);

        try {
            ConnectDB.executeUpdate(
                    "INSERT INTO history_transaction "
                    + "(player_1, player_2, item_player_1, item_player_2, bag_1_before_tran, bag_2_before_tran, bag_1_after_tran, bag_2_after_tran, time_tran) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    player1, player2, itemPlayer1, itemPlayer2,
                    beforeTran1, beforeTran2, afterTran1, afterTran2,
                    new Timestamp(System.currentTimeMillis())
            );
        } catch (Exception ex) {
            System.err.println("Lỗi khi chèn lịch sử giao dịch: " + ex.getMessage());
        }
    }

    public static List<TransactionLog> getHistoryForPlayer(Player player, int limit) {
        List<TransactionLog> history = new ArrayList<>();
        String playerIdentifier = player.name + " (" + player.id + ")";
        String query = "SELECT player_1, player_2, item_player_1, item_player_2, time_tran "
                + "FROM history_transaction "
                + "WHERE player_1 = ? OR player_2 = ? "
                + "ORDER BY time_tran DESC LIMIT ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, playerIdentifier);
            ps.setString(2, playerIdentifier);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    history.add(new TransactionLog(
                            rs.getString("player_1"),
                            rs.getString("player_2"),
                            rs.getString("item_player_1"),
                            rs.getString("item_player_2"),
                            rs.getTimestamp("time_tran")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy lịch sử giao dịch cho người chơi " + player.name + ": " + e.getMessage());
        }

        return history;
    }

    private static String formatItemsWithGold(int gold, List<Item> items) {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Integer> itemCountMap = new HashMap<>();
        Map<Integer, String> itemNameMap = new HashMap<>();

        for (Item item : items) {
            if (item.isNotNullItem()) {
                int itemId = item.template.id;
                itemCountMap.put(itemId, itemCountMap.getOrDefault(itemId, 0) + item.quantityGD);
                itemNameMap.putIfAbsent(itemId, item.template.name);
            }
        }

        boolean hasItems = !itemCountMap.isEmpty();

        if (gold > 0 || hasItems) {
            sb.append("Gold: ").append(gold);
        }

        for (Map.Entry<Integer, Integer> entry : itemCountMap.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(itemNameMap.get(entry.getKey()))
                    .append(" (").append(entry.getValue()).append(")");
        }

        return sb.toString();
    }

    private static String formatBagItems(List<Item> items) {
        StringBuilder sb = new StringBuilder();

        for (Item item : items) {
            if (item.isNotNullItem()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(item.template.name)
                        .append(" (")
                        .append(item.quantity)
                        .append(")");
            }
        }

        return sb.toString();
    }

    public static void deleteHistory() {
        PreparedStatement ps = null;

        try (Connection con = ConnectDB.getConnection()) {
            ps = con.prepareStatement("DELETE FROM history_transaction WHERE time_tran < ?");
            ps.setString(1, TimeUtil.getTimeBeforeCurrent(3 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"));
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa lịch sử giao dịch cũ: " + e.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng PreparedStatement trong deleteHistory: " + ex.getMessage());
                }
            }
        }
    }
}