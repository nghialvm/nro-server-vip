package jbcd.dao;


import Utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;

public class EventDAO {

    @Setter
    @Getter
    private static int remainingTimeToIncreaseHP = 0;

    @Setter
    @Getter
    private static int remainingTimeToIncreaseMP = 0;

    @Setter
    @Getter
    private static int remainingTimeToIncreaseDame = 0;

    // EVENT LUNAR NEW YEAR
    @Setter
    @Getter
    public static int RACE_EVENT_LUNA_NEW_YEAR = 0;

    @Setter
    @Getter
    private static int LAST_EXP_REWARD_STAGE_LUNA_NEW_YEAR = 0;

    // EVENT CHRISTMAS
    @Setter
    @Getter
    public static int RACE_CHRISTMAS_EVENT = 0;

    @Setter
    @Getter
    private static int LAST_EXP_REWARD_STAGE_CHRISTMAS = 0;

    // EVENT 8-3
    @Setter
    @Getter
    public static int RACE_INTERNATIONAL_WOMENS_DAY_EVENT = 0;

    @Setter
    @Getter
    private static int LAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY = 0;

    // EVENT VUA HÙNG
    @Setter
    @Getter
    private static long RECEIVE_MELON_SEED;

    public static void loadHungVuongEvent() {
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `data` FROM `event` WHERE `name` = 'hung_vuong'");
             ResultSet rs = ps.executeQuery()) {

            if (rs.first()) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(rs.getString("data"), JsonObject.class);

                if (json.has("ReceiveMelonSeed")) {
                    RECEIVE_MELON_SEED = json.get("ReceiveMelonSeed").getAsLong();
                }
            }

        } catch (Exception ex) {
            Logger.logException(EventDAO.class, ex, "Lỗi khi load dữ liệu sự kiện Hùng Vương");
        }
    }

    public static void loadInternationalWomensDayEvent() {
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `data` FROM `event` WHERE `name` = 'international_womens_day'");
             ResultSet rs = ps.executeQuery()) {

            if (rs.first()) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(rs.getString("data"), JsonObject.class);

                if (json.has("damePrecent")) {
                    remainingTimeToIncreaseDame = json.get("damePrecent").getAsInt();
                }
                if (json.has("hpPrecent")) {
                    remainingTimeToIncreaseHP = json.get("hpPrecent").getAsInt();
                }
                if (json.has("mpPrecent")) {
                    remainingTimeToIncreaseMP = json.get("mpPrecent").getAsInt();
                }
                if (json.has("eventPoint")) {
                    RACE_INTERNATIONAL_WOMENS_DAY_EVENT = json.get("eventPoint").getAsInt();
                }
                if (json.has("lastExpRewardStage")) {
                    LAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY = json.get("lastExpRewardStage").getAsInt();
                }
            }

        } catch (Exception ex) {
            Logger.logException(EventDAO.class, ex, "Lỗi khi load dữ liệu sự kiện 8/3");
        }
    }

    public static void loadEvent(String eventName, Consumer<Integer> setEventPoint, Consumer<Integer> setRewardStage) {
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT `data` FROM `event` WHERE `name` = ?")) {

            ps.setString(1, eventName);
            ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(rs.getString("data"), JsonObject.class);
                int eventPoint = jsonObject.getAsJsonPrimitive("eventPoint").getAsInt();
                int lastStage = jsonObject.getAsJsonPrimitive("lastExpRewardStage").getAsInt();

                setEventPoint.accept(eventPoint);
                setRewardStage.accept(lastStage);
            }
        } catch (Exception ex) {
            Logger.logException(EventDAO.class, ex);
        }
    }

    public static void loadLunaNewYearEvent() {
        loadEvent("luna_new_year",
                point -> RACE_EVENT_LUNA_NEW_YEAR = point,
                stage -> LAST_EXP_REWARD_STAGE_LUNA_NEW_YEAR = stage);
    }

    public static void loadChristMasEvent() {
        loadEvent("christ_mas",
                point -> RACE_CHRISTMAS_EVENT = point,
                stage -> LAST_EXP_REWARD_STAGE_CHRISTMAS = stage);
    }

    public static void save() {
        try {
            // Event 8/3
            JsonObject jsonWomenDay = new JsonObject();
            jsonWomenDay.addProperty("damePrecent", remainingTimeToIncreaseDame);
            jsonWomenDay.addProperty("hpPrecent", remainingTimeToIncreaseHP);
            jsonWomenDay.addProperty("mpPrecent", remainingTimeToIncreaseMP);
            jsonWomenDay.addProperty("eventPoint", RACE_INTERNATIONAL_WOMENS_DAY_EVENT);
            jsonWomenDay.addProperty("lastExpRewardStage", LAST_EXP_REWARD_STAGE_INTERNATIONAL_WOMENS_DAY);
            int rows1 = ConnectDB.executeUpdate(
                    "UPDATE `event` SET `data` = ? WHERE `name` = 'international_womens_day'",
                    jsonWomenDay.toString()
            );
            if (rows1 == 0) {
                Logger.warning("Không cập nhật được event 'international_womens_day'");
            }

            // Tết
            JsonObject jsonTet = new JsonObject();
            jsonTet.addProperty("eventPoint", RACE_EVENT_LUNA_NEW_YEAR);
            jsonTet.addProperty("lastExpRewardStage", LAST_EXP_REWARD_STAGE_LUNA_NEW_YEAR);
            int rows2 = ConnectDB.executeUpdate(
                    "UPDATE `event` SET `data` = ? WHERE `name` = 'luna_new_year'",
                    jsonTet.toString()
            );
            if (rows2 == 0) {
                Logger.warning("Không cập nhật được event 'luna_new_year'");
            }

            // Noel
            JsonObject jsonXmas = new JsonObject();
            jsonXmas.addProperty("eventPoint", RACE_CHRISTMAS_EVENT);
            jsonXmas.addProperty("lastExpRewardStage", LAST_EXP_REWARD_STAGE_CHRISTMAS);
            int rows3 = ConnectDB.executeUpdate(
                    "UPDATE `event` SET `data` = ? WHERE `name` = 'christ_mas'",
                    jsonXmas.toString()
            );
            if (rows3 == 0) {
                Logger.warning("Không cập nhật được event 'christ_mas'");
            }

            // Hùng Vương
            JsonObject jsonhungkings = new JsonObject();
            jsonhungkings.addProperty("ReceiveMelonSeed", RECEIVE_MELON_SEED);
            int rows4 = ConnectDB.executeUpdate(
                    "UPDATE `event` SET `data` = ? WHERE `name` = 'hung_vuong'",
                    jsonhungkings.toString()
            );
            if (rows4 == 0) {
                Logger.warning("Không cập nhật được event 'hung_vuong'");
            }

        } catch (Exception e) {
            Logger.error(e.toString());
        }
    }
}