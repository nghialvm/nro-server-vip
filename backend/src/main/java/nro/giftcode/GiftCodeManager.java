package nro.giftcode;

import nro.player.Player;
import nro.services.Service;
import Utils.Logger;
import Utils.TimeUtil;
import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import models.Item.ItemOption;
import jbcd.CrisResultSet;


public class GiftCodeManager {
    public String name;
    public final ArrayList<GiftCode> listGiftCode = new ArrayList<>();
    private final Object giftCodeLock = new Object();

    private static GiftCodeManager instance;

    public static GiftCodeManager gI() {
        if (instance == null) {
            instance = new GiftCodeManager();
        }
        return instance;
    }

    public void init() {
        reload();
    }

    /**
     * Reloads gift codes without exposing a partially loaded cache to players.
     * The existing list object is preserved for compatibility with callers,
     * but it is only replaced while the manager lock is held.
     */
    public boolean reload() {
        ArrayList<GiftCode> loadedGiftCodes = new ArrayList<>();
        try (Connection con = ConnectDB.getConnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM giftcode");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                loadedGiftCodes.add(readGiftCode(rs));
            }

            synchronized (giftCodeLock) {
                listGiftCode.clear();
                listGiftCode.addAll(loadedGiftCodes);
            }
            Logger.log(Logger.GREEN, "LOAD GIFTCODE (" + loadedGiftCodes.size() + ") SUCCESS\n");
            return true;
        } catch (Exception error) {
            Logger.logException(GiftCodeManager.class, error, "Loi reload giftcode");
            return false;
        }
    }

    private GiftCode readGiftCode(ResultSet rs) throws Exception {
        GiftCode giftcode = new GiftCode();
        giftcode.code = rs.getString("code");
        giftcode.countLeft = rs.getInt("count_left");
        giftcode.datecreate = rs.getTimestamp("datecreate");
        giftcode.dateexpired = rs.getTimestamp("expired");

        JSONArray items = (JSONArray) JSONValue.parse(rs.getString("item"));
        if (items != null) {
            for (Object value : items) {
                JSONObject jsonObj = (JSONObject) value;
                giftcode.detail.put(Integer.valueOf(jsonObj.get("id").toString()),
                        Integer.valueOf(jsonObj.get("quantity").toString()));
            }
        }

        JSONArray options = (JSONArray) JSONValue.parse(rs.getString("option"));
        if (options != null) {
            for (Object value : options) {
                JSONObject jsonObject = (JSONObject) value;
                giftcode.option.add(new ItemOption(Integer.parseInt(jsonObject.get("id").toString()),
                        Integer.parseInt(jsonObject.get("param").toString())));
            }
        }

        String dbListIdPlayer = rs.getString("listIdPlayers");
        if (dbListIdPlayer != null && !dbListIdPlayer.isBlank()) {
            String rawIds = dbListIdPlayer.trim();
            if (rawIds.startsWith("[") && rawIds.endsWith("]")) {
                rawIds = rawIds.substring(1, rawIds.length() - 1);
            }
            if (!rawIds.isBlank()) {
                for (String item : rawIds.split(",")) {
                    String trimmed = item.trim();
                    if (!trimmed.isEmpty()) {
                        giftcode.listIdPlayer.add(Integer.parseInt(trimmed));
                    }
                }
            }
        }
        return giftcode;
    }

    public void updateGiftCodeListIdPlayer(ArrayList<Integer> listIdPlayers, String code) {
        try {
            String sql = "UPDATE giftcode set listIdPlayers=? where code=?";
            ArrayList<Integer> deDupStriList = new ArrayList<>(new HashSet<>(listIdPlayers));
            ConnectDB.executeUpdate(sql, JSONValue.toJSONString(deDupStriList), code);
        } catch (Exception e) {
        }
    }
    
    public void sizeList(Player pl) {
        Service.gI().sendThongBao(pl, "" + GiftCode.class);
    }

    public GiftCode checkUseGiftCode(int idPlayer, String code) {
        synchronized (giftCodeLock) {
            for (GiftCode giftCode : listGiftCode) {
                if (giftCode.code.equals(code) && giftCode.countLeft > 0 && !giftCode.isUsedGiftCode(idPlayer)) {
                    giftCode.countLeft -= 1;
                    giftCode.addPlayerUsed(idPlayer);
                    updateGiftCodeListIdPlayer(giftCode.listIdPlayer, code);
                    return giftCode;
                }
            }
            return null;
        }
    }
    
    public GiftCode CheckCode(int idPlayer, String code) {
        synchronized (giftCodeLock) {
            for (GiftCode giftCode : listGiftCode) {
                if (giftCode.code.equals(code) && giftCode.countLeft > 0 && !giftCode.isUsedGiftCode(idPlayer)) {
                    return giftCode;
                }
            }
            return null;
        }
    }

    public void checkInfomationGiftCode(Player p) throws Exception {
        CrisResultSet rs = ConnectDB.executeQuery("SELECT * FROM Giftcode WHERE id > 0");
        String textGift = "|7|[ - THÔNG TIN GIFTCODE - ]\n\n";
         while (rs.next()) {
            String code = rs.getString("code");
            int Luot = rs.getInt("count_left");
           String hsd = TimeUtil.getTimeNow(rs.getString("datecreate"));
           String hhsd = TimeUtil.getTimeNow(rs.getString("expired"));
            textGift += "|0|Giftcode : " + code + "\n"
                    + "|0|Số Lượng Còn : " + (Luot == 0 ? "[HẾT]" : + Luot + " Lượt") + "\n"
                    +"|3|[Hạn Sử Dụng : " + hsd + " ---> " + hhsd + "]\n\n";
        }
        rs.dispose();
        Service.gI().sendThongBaoFromAdmin(p, textGift);
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
