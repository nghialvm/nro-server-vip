package nro.map.SnakeWay;

import Utils.Logger;
import jbcd.ConnectDB;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import nro.clan.Clan;
import nro.clan.ClanMember;



public class TopSnakeWay {

    @Getter
    private List<Clan> list = new ArrayList<>();
    
    private static final TopSnakeWay INSTANCE = new TopSnakeWay();
    

    public static TopSnakeWay getInstance() {
        return INSTANCE;
    }

    public void load() {
        list.clear();
        try ( Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT *,CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichCDRD, ',', 1), '[', -1) AS UNSIGNED) AS part1, CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(thanhTichCDRD, ',', -1), ']', 1) AS UNSIGNED) AS part2 FROM clan ORDER BY part1 DESC, part2 ASC LIMIT 100;");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Clan clan = extractClanFromResultSet(rs);
                list.add(clan);
            }
        } catch (Exception e) {
            Logger.logException(TopSnakeWay.class, e);
        }
    }

    private Clan extractClanFromResultSet(ResultSet rs) throws SQLException {
        Clan clan = new Clan();
        JSONArray dataArray = null;
        JSONObject dataObject = null;
        JSONValue jv = new JSONValue();
        clan.id = rs.getInt("id");
        clan.name = rs.getString("name");
        dataArray = (JSONArray) JSONValue.parse(rs.getString("thanhTichCDRD"));
        if (!dataArray.isEmpty()) {
            clan.levelDoneConDuongRanDoc = Integer.parseInt(String.valueOf(dataArray.get(0)));
            clan.thoiGianHoanThanhCDRD = Long.parseLong(String.valueOf(dataArray.get(1)));
        }
        dataArray.clear();
                dataArray = (JSONArray) jv.parse(rs.getString("members"));
                for (int i = 0; i < dataArray.size(); i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    ClanMember cm = new ClanMember();
                    cm.clan = clan;
                    cm.id = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    cm.name = String.valueOf(dataObject.get("name"));
                    cm.head = Short.parseShort(String.valueOf(dataObject.get("head")));
                    cm.body = Short.parseShort(String.valueOf(dataObject.get("body")));
                    cm.leg = Short.parseShort(String.valueOf(dataObject.get("leg")));
                    cm.role = Byte.parseByte(String.valueOf(dataObject.get("role")));
                    cm.donate = Integer.parseInt(String.valueOf(dataObject.get("donate")));
                    cm.receiveDonate = Integer.parseInt(String.valueOf(dataObject.get("receive_donate")));
                    cm.memberPoint = Integer.parseInt(String.valueOf(dataObject.get("member_point")));
                    cm.memberDamage = Long.parseLong(String.valueOf(dataObject.get("member_damage")));
                    cm.clanPoint = Integer.parseInt(String.valueOf(dataObject.get("clan_point")));
                    cm.joinTime = Integer.parseInt(String.valueOf(dataObject.get("join_time")));
                    cm.timeAskPea = Long.parseLong(String.valueOf(dataObject.get("ask_pea_time")));
                    try {
                        cm.powerPoint = Long.parseLong(String.valueOf(dataObject.get("power")));
                    } catch (Exception e) {
                    }
                    clan.addClanMember(cm);
                }
        return clan;
    }
}






