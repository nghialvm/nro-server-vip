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

    private static GiftCodeManager instance;

    public static GiftCodeManager gI() {
        if (instance == null) {
            instance = new GiftCodeManager();
        }
        return instance;
    }

    public void init() {
        try (Connection con = ConnectDB.getConnection();) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM giftcode");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                GiftCode giftcode = new GiftCode();
                ArrayList<Integer> tempListIdPlayer = new ArrayList<>();
                String tempDBListIdPlayers;
                giftcode.code = rs.getString("code");
                giftcode.countLeft = rs.getInt("count_left");
                giftcode.datecreate = rs.getTimestamp("datecreate");
                giftcode.dateexpired = rs.getTimestamp("expired");
                String dbListIdPlayer = rs.getString("listIdPlayers");
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("item"));
                if (jar != null) {
                    for (int i = 0; i < jar.size(); ++i) {
                        JSONObject jsonObj = (JSONObject) jar.get(i);
                        giftcode.detail.put(Integer.valueOf(jsonObj.get("id").toString()),
                                Integer.valueOf(jsonObj.get("quantity").toString()));
                        jsonObj.clear();
                    }
                }
                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("option"));
                if (option != null) {
                    for (int u = 0; u < option.size(); u++) {
                        JSONObject jsonobject = (JSONObject) option.get(u);
                        giftcode.option.add(new ItemOption(Integer.parseInt(jsonobject.get("id").toString()),
                                Integer.parseInt(jsonobject.get("param").toString())));
                        jsonobject.clear();
                    }
                }
                if (!dbListIdPlayer.isEmpty()) {
                    tempDBListIdPlayers = dbListIdPlayer = removeCharAt(dbListIdPlayer, 0);
                    tempDBListIdPlayers = dbListIdPlayer = removeCharAt(dbListIdPlayer, dbListIdPlayer.length() - 1);
                    String[] resultTempDBListPlayer = tempDBListIdPlayers.split(",");
                    for (String item : resultTempDBListPlayer) {
                        if (!item.isEmpty())
                            tempListIdPlayer.add(Integer.parseInt(item));
                    }
                    giftcode.listIdPlayer = tempListIdPlayer;
                }
                listGiftCode.add(giftcode);
            }
            con.close();
            ps.close();
            rs.close();
            Logger.log(Logger.GREEN,"LOAD GIFTCODE (" + listGiftCode.size() + ") SUCCESS\n");
        } catch (Exception erorlog) {
        }
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
    
    public GiftCode CheckCode(int idPlayer, String code) {
        for (GiftCode giftCode : listGiftCode) {
            if (giftCode.code.equals(code) && giftCode.countLeft > 0 && !giftCode.isUsedGiftCode(idPlayer)) {
                return giftCode;
            }
        }
        return null;
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
