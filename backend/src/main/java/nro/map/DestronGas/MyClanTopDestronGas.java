package nro.map.DestronGas;

import nro.player.Player;
import Utils.Logger;
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



public class MyClanTopDestronGas {

    @Getter
    private List<Player> list = new ArrayList<>();

    private static final MyClanTopDestronGas INSTANCE = new MyClanTopDestronGas();

    public static MyClanTopDestronGas getInstance() {
        return INSTANCE;
    }

    public void load2(int idLeader) {
        list.clear();
        try (Connection con = ConnectDB.getConnection();  
                PreparedStatement ps = con.prepareStatement("SELECT *, "
                + "SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang2, ',', 1), '[', -1) AS so1, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang2, ',', 2), ',', -1) AS SIGNED) AS so2, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang2, ',', 3), ',', -1) AS SIGNED) AS so3, "
                + "CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang2, ',', 4), ',', -1) AS SIGNED) AS so4 "
                + "FROM player WHERE CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichBang2, ',', 2), ',', -1), ']', 1) AS SIGNED) > 0 "
                + "AND id = ? ORDER BY so2 DESC, so1 ASC LIMIT 100")) {
            // Äáº·t giÃ¡ trá»‹ thá»±c táº¿ cá»§a idLeader vÃ o cÃ¢u truy váº¥n
            ps.setInt(1, idLeader);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Player player = extractPlayerFromResultSet(rs);
                    list.add(player);
                }
            }
        } catch (Exception e) {
            Logger.logException(MyClanTopDestronGas.class, e);
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
        player.levelKhiGasDone = rs.getInt("so2");
        player.timeKhiGasDone = rs.getLong("so3");
        player.lastTimeUpdateTopKhiGas = (System.currentTimeMillis() - rs.getLong("so4")) / 1000;
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






