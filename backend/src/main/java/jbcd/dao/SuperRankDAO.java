package jbcd.dao;

import nro.inventory.Inventory;
import nro.player.Player;
import Utils.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jbcd.ConnectDB;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.simple.JSONArray;
import jbcd.CrisResultSet;

public class SuperRankDAO {

    // Lấy rank cao nhất từ bảng player
    public static int getHighestRank() {
        CrisResultSet rs = null;
        try {
            rs = ConnectDB.executeQuery("SELECT rank FROM player ORDER BY rank DESC LIMIT 1");
            if (rs.next()) {
                return rs.getInt("rank");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Lấy danh sách người chơi trong khoảng rank
    public static List<Long> getPlayerListInRankRange(int rank, int limit) {
        List<Long> list = new ArrayList<>();
        CrisResultSet rs = null;

        try {
            rs = ConnectDB.executeQuery(
                    "SELECT id FROM player WHERE rank <= ? AND rank > 0 ORDER BY rank DESC LIMIT ?", rank, limit
            );
            while (rs.next()) {
                list.add((long) rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Thêm một ID ngẫu nhiên dựa trên rank
        int rand = random(rank);
        if (rand != -1) {
            try {
                rs = ConnectDB.executeQuery("SELECT id FROM player WHERE rank = ? LIMIT 1", rand);
                if (rs.next()) {
                    list.add((long) rs.getInt("id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.reverse(list);
        return list;
    }

    // Lấy danh sách người chơi theo rank
    public static List<Long> getPlayerListInRank(int rank, int limit) {
        List<Long> list = new ArrayList<>();
        CrisResultSet rs = null;

        try {
            rs = ConnectDB.executeQuery("SELECT id FROM player WHERE rank > 0 ORDER BY rank ASC LIMIT ?", limit);
            while (rs.next()) {
                list.add((long) rs.getInt("id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rank > 100) {
            try {
                rs = ConnectDB.executeQuery(
                        "SELECT id FROM player WHERE rank > ? AND rank < ? ORDER BY rank ASC LIMIT 4",
                        rank - 3, rank + 2
                );
                while (rs.next()) {
                    list.add((long) rs.getInt("id"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // Hàm tạo giá trị ngẫu nhiên dựa trên rank
    public static int random(int rank) {
        if (rank > 10000) {
            return Util.nextInt(6666, 10000);
        } else if (rank > 6666) {
            return Util.nextInt(3333, 6666);
        } else if (rank > 3333) {
            return Util.nextInt(1000, 3333);
        } else if (rank > 1000) {
            return Util.nextInt(666, 1000);
        } else if (rank > 666) {
            return Util.nextInt(333, 666);
        } else if (rank > 333) {
            return Util.nextInt(100, 333);
        }
        System.err.println("Rank too low to generate random value: " + rank);
        return -1;
    }

    // Cập nhật rank của người chơi
    public static void updateRank(Player player) {
        try {
            String query = "UPDATE player SET rank = ? WHERE id = ?";
            ConnectDB.executeUpdate(query, player.superRank.rank, player.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cập nhật thông tin người chơi
    public static void updatePlayer(Player player) {
        if (player != null && player.iDMark.isLoadedAllDataPlayer()) {
            try {
                // Tạo dữ liệu inventory
                JSONArray dataArray = new JSONArray();
                dataArray.add(Math.min(player.inventory.gold, Inventory.LIMIT_GOLD));
                dataArray.add(player.inventory.gem);
                dataArray.add(player.inventory.ruby);
                String inventory = dataArray.toJSONString();

                // Tạo dữ liệu super rank
                String dataSuperRank = createSuperRankData(player);

                // Cập nhật vào database
                String query = "UPDATE player SET data_inventory = ?, rank = ?, data_super_rank = ? WHERE id = ?";
                ConnectDB.executeUpdate(query, inventory, player.superRank.rank, dataSuperRank, player.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String createSuperRankData(Player player) {
        try {
            JSONArray dataArray = new JSONArray();

            dataArray.add(player.superRank.lastTimePK);
            dataArray.add(player.superRank.lastTimeReward);
            dataArray.add(player.superRank.ticket);
            dataArray.add(player.superRank.win);
            dataArray.add(player.superRank.lose);

            // Gộp history và lastTime thành 1 object
            JsonArray historyArray = new JsonArray();
            for (String str : player.superRank.getHistory()) {
                historyArray.add(str);
            }

            JsonArray lastTimeArray = new JsonArray();
            for (Long value : player.superRank.getLastTime()) {
                lastTimeArray.add(value);
            }

            JsonObject historyObject = new JsonObject();
            historyObject.add("history", historyArray);
            historyObject.add("lasttime", lastTimeArray);

            // Thêm vào dataArray
            dataArray.add(historyObject);

            return dataArray.toJSONString(); // lưu dưới dạng JSONArray chuỗi
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }
}