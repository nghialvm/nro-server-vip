package nro.boss.map.TrainingBoss;

import nro.player.Player;
import jbcd.ConnectDB;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class TopKillWhisManager {

    private static final TopKillWhisManager INSTANCE = new TopKillWhisManager();
    private final List<Player> list = new ArrayList<>();

    public static TopKillWhisManager getInstance() {
        return INSTANCE;
    }

    public List<Player> getList() {
        return list;
    }

    public void load() {
        list.clear();

        // Truy vấn SQL đã sửa, lấy từ `data_luyentap` thay vì `levelKillWhis`
        String sql = "SELECT player.id, player.name, player.head, player.gender, "
                + "       CAST(JSON_UNQUOTE(JSON_EXTRACT(data_luyentap, '$[5]')) AS UNSIGNED) AS levelKillWhis, "
                + "       CAST(JSON_UNQUOTE(JSON_EXTRACT(data_luyentap, '$[6]')) AS UNSIGNED) AS timeKillWhis, "
                + "       player.lastTimeLoginGame, player.data_point " // Thêm "player." để tránh lỗi
                + "FROM player "
                + "INNER JOIN account ON account.id = player.account_id "
                + "WHERE account.ban = 0 "
                + "AND JSON_EXTRACT(data_luyentap, '$[5]') IS NOT NULL "
                + "ORDER BY levelKillWhis DESC, timeKillWhis ASC "
                + "LIMIT 100;";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Player player = extractPlayerFromResultSet(rs);
                list.add(player);
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tải danh sách TopKillWhis!");
        }
    }

    private Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.id = rs.getInt("id");
        player.name = rs.getString("name");
        player.head = rs.getShort("head");
        player.gender = rs.getByte("gender");
        player.lastimelogin = rs.getTimestamp("lastTimeLoginGame");

        extractDataPoint(rs.getString("data_point"), player);

        return player;
    }

    private void extractDataPoint(String dataPoint, Player player) {
        if (dataPoint == null || dataPoint.isEmpty()) {
            player.nPoint.power = 0; // Nếu dữ liệu bị lỗi, đặt mặc định
            return;
        }

        try {
            JSONArray dataArray = (JSONArray) JSONValue.parse(dataPoint);
            if (dataArray != null && dataArray.size() > 11) {
                player.nPoint.power = Long.parseLong(dataArray.get(11).toString());
            } else {
                player.nPoint.power = 0;
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Lỗi khi xử lý `data_point` cho người chơi: " + player.name);
            player.nPoint.power = 0; // Tránh lỗi null
        }
    }
}