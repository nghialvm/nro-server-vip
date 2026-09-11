package nro.map.TreasureUnderSea;

import Utils.Logger;
import nro.player.Player;
import jbcd.ConnectDB;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class MyClanTopTreasureUnderSea {

    @Getter
    private List<Player> list = new ArrayList<>();

    private static final MyClanTopTreasureUnderSea INSTANCE = new MyClanTopTreasureUnderSea();

    public static MyClanTopTreasureUnderSea getInstance() {
        return INSTANCE;
    }

    public void load2(int idLeader) {
        list.clear();
        try (Connection con = ConnectDB.getConnection();   
                PreparedStatement ps = con.prepareStatement("SELECT *, "
                + "SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang, ',', 1), '[', -1) AS so1, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang, ',', 2), ',', -1) AS SIGNED) AS so2, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang, ',', 3), ',', -1) AS SIGNED) AS so3, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang, ',', 4), ',', -1) AS SIGNED) AS so4 "
                + "FROM player WHERE CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang, ',', 2), ',', -1), ']', 1) AS SIGNED) > 0 "
                + "AND id = ? ORDER BY so2 DESC, so1 ASC LIMIT 100")) {
            // Đặt giá trị thực tế của idLeader vào câu truy vấn
            ps.setInt(1, idLeader);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Player player = extractPlayerFromResultSet(rs);
                    list.add(player);
                }
            }
        } catch (Exception e) {
            Logger.logException(MyClanTopTreasureUnderSea.class, e);
        }
    }

    private Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.id = rs.getInt("id");
        player.name = rs.getString("name");
        player.head = rs.getShort("head");
        player.gender = rs.getByte("gender");
        player.lastimelogin = rs.getTimestamp("lastimelogin");
        player.nameClan = rs.getString("so1");
        player.levelBDKBDone = rs.getInt("so2");
        player.timeBDKBDone = rs.getLong("so3");
        player.lastTimeUpdateTopBDKB = (System.currentTimeMillis() - rs.getLong("so4")) / 1000;
        extractDataPoint(rs.getString("data_point"), player);
        return player;
    }

    private void extractDataPoint(String dataPoint, Player player) {
        JSONArray dataArray;
        dataArray = (JSONArray) JSONValue.parse(dataPoint);
        player.nPoint.power = Long.parseLong(dataArray.get(11).toString());
        dataArray.clear();
    }     
}
