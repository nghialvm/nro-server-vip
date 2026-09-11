package jbcd.dao;

import nro.inventory.Inventory;
import models.Item.Item;
import models.Item.ItemTime;
import nro.player.Friend;
import nro.player.Fusion;
import nro.player.Player;
import nro.skill.Skill;
import nro.server.TopServer;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import consts.ConstPlayer;
import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import jbcd.data.DatabaseUpdater;
import models.Item.ItemOption;
import nro.template.AchievementQuest;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

public class PlayerDAO {

    public static boolean updateTimeLogout;

    private static String createEmptyItemSlots(int size) {
        JSONArray dataArray = new JSONArray();
        JSONArray item = new JSONArray();
        JSONArray options = new JSONArray();

        for (int i = 0; i < size; i++) {
            item.add(-1);
            item.add(0);
            item.add(options.toJSONString());
            item.add(System.currentTimeMillis());
            dataArray.add(item.toJSONString());
            item.clear();
        }

        return dataArray.toJSONString();
    }

    public static boolean createNewPlayer(int userId, String name, byte gender, int hair) {
        try {
            JSONArray dataArray = new JSONArray();

            dataArray.add(2000); //vàng
            dataArray.add(99999999); //ngọc xanh
            dataArray.add(99999999); //hồng ngọc
            dataArray.add(0); //point_EXP

            String inventory = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(39 + gender); //map
            dataArray.add(100); //x
            dataArray.add(384); //y
            String location = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); //giới hạn sức mạnh
            dataArray.add(1200); //sức mạnh
            dataArray.add(1200); //tiềm năng
            dataArray.add(1000); //thể lực
            dataArray.add(1000); //thể lực đầy
            dataArray.add(gender == 0 ? 200 : 100); //hp gốc
            dataArray.add(gender == 1 ? 200 : 100); //ki gốc
            dataArray.add(gender == 2 ? 15 : 12); //sức đánh gốc
            dataArray.add(0); //giáp gốc
            dataArray.add(0); //chí mạng gốc
            dataArray.add(0); //năng động
            dataArray.add(gender == 0 ? 200 : 100); //hp hiện tại
            dataArray.add(gender == 1 ? 200 : 100); //ki hiện tại
            dataArray.add(gender == 2 ? 15 : 12); //sức đánh
            dataArray.add(gender == 0 ? 200 : 100); //HP MAX
            dataArray.add(gender == 0 ? 200 : 100); //KI MAX
            String point = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(1); //level
            dataArray.add(5); //curent pea
            dataArray.add(0); //is upgrade
            dataArray.add(new Date().getTime()); //last time harvest
            dataArray.add(new Date().getTime()); //last time upgrade
            String magicTree = dataArray.toJSONString();
            dataArray.clear();
            /**
             *
             * [
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"1","option":[[5,7],[7,3]],"create_time":"49238749283748957""},
             * {"temp_id":"-1","option":[],"create_time":"0""}, ... ]
             */

            int idAo = gender == 0 ? 0 : gender == 1 ? 1 : 2;
            int idQuan = gender == 0 ? 6 : gender == 1 ? 7 : 8;
            int def = gender == 2 ? 3 : 2;
            long hp = gender == 0 ? 30 : 20;

            JSONArray item = new JSONArray();
            JSONArray options = new JSONArray();
            JSONArray opt = new JSONArray();
            for (int i = 0; i < ConstPlayer.QTY_MAX_ITEM_BODY_PLAYER; i++) {
                switch (i) {
                    case 0:
                        //áo
                        opt.add(47); //id option
                        opt.add(def); //param option
                        item.add(idAo); //id item
                        item.add(1); //số lượng
                        options.add(opt.toJSONString());
                        opt.clear();
                        break;
                    case 1:
                        //quần
                        opt.add(6); //id option
                        opt.add(hp); //param option
                        item.add(idQuan); //id item
                        item.add(1); //số lượng
                        options.add(opt.toJSONString());
                        opt.clear();
                        break;
                    default:
                        item.add(-1); //id item
                        item.add(0); //số lượng
                        break;
                }
                item.add(options.toJSONString()); //full option item
                item.add(System.currentTimeMillis()); //thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear();
                item.clear();
            }
            String itemsBody = dataArray.toJSONString();
            dataArray.clear();

            for (int i = 0; i < 20; i++) {
                item.add(-1); // id item
                item.add(0); // số lượng
                item.add(options.toJSONString()); // full option item
                item.add(System.currentTimeMillis()); // thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear();
                item.clear();
            }

            String itemsBag = dataArray.toJSONString();
            dataArray.clear();
             try (Connection con =ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE account SET vnd = ?, tongnap = ? WHERE id = ?")) {
                ps.setInt(1, 0);
                ps.setInt(2, 0);
                ps.setInt(3, userId);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
             // 100M VND
            try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE account SET vnd = ?, tongnap = ?, coin = ? WHERE id = ?")) {
                ps.setInt(1, 99999999);
                ps.setInt(2, 99999999);
                ps.setInt(3, 99999999);
                ps.setInt(4, userId);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 20; i++) {
                if (i == 0) { //rada
                    opt.add(14); //id option
                    opt.add(1); //param option
                    item.add(12); //id item
                    item.add(1); //số lượng
                    options.add(opt.toJSONString());
                    opt.clear();
                } else {
                    item.add(-1); //id item
                    item.add(0); //số lượng
                }
                item.add(options.toJSONString()); //full option item
                item.add(System.currentTimeMillis()); //thời gian item được tạo
                dataArray.add(item.toJSONString());
                options.clear();
                item.clear();
            }
            String itemsBox = dataArray.toJSONString();
            dataArray.clear();

            String itemsBoxCollection = createEmptyItemSlots(20);
            String itemsBoxLuckyRound = createEmptyItemSlots(110);
            String itemsDaBan = createEmptyItemSlots(20);
            String itemsMailBox = createEmptyItemSlots(200);

            String friends = dataArray.toJSONString();
            String enemies = dataArray.toJSONString();

            dataArray.add(0); //id nội tại
            dataArray.add(0); //chỉ số 1
            dataArray.add(0); //chỉ số 2
            dataArray.add(0); //số lần mở
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            String intrinsic = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); //bổ huyết
            dataArray.add(0); //bổ khí
            dataArray.add(0); //giáp xên
            dataArray.add(0); //cuồng nộ
            dataArray.add(0); //ẩn danh
            dataArray.add(0); //mở giới hạn sức mạnh
            dataArray.add(0); //máy dò
            dataArray.add(0); //thức ăn cold
            dataArray.add(0); //icon thức ăn cold
            dataArray.add(0); // tdlt
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0);
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); // 
            dataArray.add(0); // 
            dataArray.add(0); //
            dataArray.add(0); // 
            dataArray.add(0); // 
            dataArray.add(0); // 
            dataArray.add(0); // comemson
            dataArray.add(0); // khautrang
            dataArray.add(0); // keo
            dataArray.add(0); // bihacam
            dataArray.add(0); // hamburger
            dataArray.add(0); // gatonhen
            dataArray.add(0); // thuocmo
            dataArray.add(0); // tm
            dataArray.add(0); // crm
            dataArray.add(0); // bt
            dataArray.add(0); // tomtambot
            dataArray.add(0); // bocpha
            dataArray.add(0); // gtpt
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); // 
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); // 
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //bổ huyết2
            dataArray.add(0); //bổ khí2
            dataArray.add(0); //giáp xên2
            dataArray.add(0); //cuồng nộ2
            dataArray.add(0); //ẩn danh2
            dataArray.add(0); //ẩn danh2
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            dataArray.add(0); //
            String itemTime = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0); //id nhiệm vụ
            dataArray.add(0); //index nhiệm vụ con
            dataArray.add(0); //số lượng đã làm

            String task = dataArray.toJSONString();
            dataArray.clear();

            String mabuEgg = dataArray.toJSONString();

            dataArray.add(System.currentTimeMillis()); //bùa trí tuệ
            dataArray.add(System.currentTimeMillis()); //bùa mạnh mẽ
            dataArray.add(System.currentTimeMillis()); //bùa da trâu
            dataArray.add(System.currentTimeMillis()); //bùa oai hùng
            dataArray.add(System.currentTimeMillis()); //bùa bất tử
            dataArray.add(System.currentTimeMillis()); //bùa dẻo dai
            dataArray.add(System.currentTimeMillis()); //bùa thu hút
            dataArray.add(System.currentTimeMillis()); //bùa đệ tử
            dataArray.add(System.currentTimeMillis()); //bùa trí tuệ x3
            dataArray.add(System.currentTimeMillis()); //bùa trí tuệ x4
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            dataArray.add(System.currentTimeMillis());
            String charms = dataArray.toJSONString();
            dataArray.clear();

            int[] skillsArr = gender == 0 ? new int[]{0, 1, 6, 9, 10, 20, 22, 19, 24}
                    : gender == 1 ? new int[]{2, 3, 7, 11, 12, 17, 18, 19, 26}
                    : new int[]{4, 5, 8, 13, 14, 21, 23, 19, 25};

            JSONArray skill = new JSONArray();
            for (int i = 0; i < skillsArr.length; i++) {
                skill.add(skillsArr[i]); //id skill
                if (i == 0) {
                    skill.add(1); //level skill
                } else {
                    skill.add(0); //level skill
                }
                skill.add(0); //thời gian sử dụng trước đó
                dataArray.add(skill.toString());
                skill.clear();
            }
            String skills = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(gender == 0 ? 0 : gender == 1 ? 2 : 4);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            dataArray.add(-1);
            String skillsShortcut = dataArray.toJSONString();
            dataArray.clear();

            String petData = dataArray.toJSONString();

            String myFather = dataArray.toJSONString();

            String myMother = dataArray.toJSONString();

            JSONArray blackBall = new JSONArray();
            for (int i = 1; i <= 7; i++) {
                blackBall.add(0);
                blackBall.add(0);
                blackBall.add(0);
                dataArray.add(blackBall.toJSONString());
                blackBall.clear();
            }
            String dataBlackBall = dataArray.toString();
            dataArray.clear();

            dataArray.add(-1); //id side task
            dataArray.add(0); //thời gian nhận
            dataArray.add(0); //số lượng đã làm
            dataArray.add(0); //số lượng cần làm
            dataArray.add(20); //số nhiệm vụ còn lại có thể nhận
            dataArray.add(0); //mức độ nhiệm vụ
            String dataSideTask = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            String data_event_models = dataArray.toJSONString();
            dataArray.clear();

            LocalDateTime currentTime = LocalDateTime.now();
            Timestamp currentTimestamp = Timestamp.valueOf(currentTime);

            String thanhTichBang = "";
            String thanhTichBang2 = "";
            String thanhTichBang3 = "";

            String bill_data = dataArray.toJSONString();

            String trong_dua_hau = dataArray.toJSONString();

            String data_nguoi_yeu = "[]";

            String dataBadges = "[]";
            String dataTaskBadges = "[]";

            String dailyGift = "[]";

            String data_luyentap = "[]";

            dataArray.add(0);
            dataArray.add(0);
            String koltask = dataArray.toJSONString();
            dataArray.clear();

            dataArray.add(-1);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(0);
            dataArray.add(20);
            dataArray.add(0);
            String eventTask = dataArray.toJSONString();
            dataArray.clear();

            dataArray.clear();

            dataArray.add(gender == 0 ? 0 : gender == 1 ? 2 : 4);
            String dataBoughtSkill = dataArray.toJSONString();

            dataArray.clear();

            String sql
                    = "INSERT INTO player("
                    + "account_id, name, head, gender, have_tennis_space_ship, clan_id, "
                    + "data_inventory, data_location, data_point, data_magic_tree, "
                    + "items_body, items_bag, items_box, items_box_Collection, items_box_lucky_round, "
                    + "items_daban, item_mails_box, friends, enemies, data_intrinsic, data_item_time, "
                    + "data_task, data_mabu_egg, data_charm, skills, skills_shortcut, data_luyentap, "
                    + "pet, myFather, myMother, data_black_ball, data_side_task, data_kol_task, data_event_task, "
                    + "bill_data, data_trong_dua_hau, data_nguoi_yeu, lastimelogin, LastTimeLoginGame, "
                    + "thanhTichBang, thanhTichBang2, thanhTichBang3, BoughtSkill, dataBadges, dataTaskBadges, "
                    + "dailyGift, data_event_models"
                    + ") VALUES ("
                    + "?, ?, ?, ?, ?, ?, "
                    + // 1..6
                    "?, ?, ?, ?, "
                    + // 7..10
                    "?, ?, ?, ?, ?, "
                    + // 11..15
                    "?, ?, ?, ?, ?, ?, "
                    + // 16..21
                    "?, ?, ?, ?, ?, ?, "
                    + // 22..27
                    "?, ?, ?, ?, ?, ?, ?, "
                    + // 28..34
                    "?, ?, ?, ?, ?, ?, "
                    + // 35..40
                    "?, ?, ?, ?, ?, ?, "
                    + // 41..46
                    "?"
                    + // 47
                    ")";

            ConnectDB.executeUpdate(sql,
                    userId, name, hair, gender, 0, -1,
                    inventory, location, point, magicTree,
                    itemsBody, itemsBag, itemsBox, itemsBoxCollection, itemsBoxLuckyRound,
                    itemsDaBan, itemsMailBox, friends, enemies, intrinsic, itemTime,
                    task, mabuEgg, charms, skills, skillsShortcut, data_luyentap,
                    petData, myFather, myMother, dataBlackBall, dataSideTask, koltask, eventTask,
                    bill_data, trong_dua_hau, data_nguoi_yeu, currentTimestamp, currentTimestamp,
                    thanhTichBang, thanhTichBang2, thanhTichBang3, dataBoughtSkill, dataBadges, dataTaskBadges,
                    dailyGift, data_event_models
            );
            Logger.success("Tạo player mới thành công!");

            return true;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi tạo player mới");
            return false;
        }
    }

    public static void updatePlayer(Player player) {
        if (player.isSaving()) {
            return;
        }
        player.setSaving(true);
        if (!player.isDeTu && !player.isBo && !player.isMe && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree && !player.isPhanThan && !player.isBoss) {
            Service.gI().sendFlagBag(player);
        }
        try {
            if (player.iDMark.isLoadedAllDataPlayer()) {
                long st = System.currentTimeMillis();
                try {
                    JSONArray dataArray = new JSONArray();

                    //data kim lượng
                    dataArray.add(player.inventory.gold > Inventory.LIMIT_GOLD ? Inventory.LIMIT_GOLD : player.inventory.gold);
                    dataArray.add(player.inventory.gem);
                    dataArray.add(player.inventory.ruby);
                    dataArray.add(player.inventory.Exp_Vip);
                    String inventory = dataArray.toJSONString();
                    dataArray.clear();

                    int mapId = player.mapIdBeforeLogout;
                    int x = player.location.x;
                    int y = player.location.y;
                    long hp = Util.CrisGH(player.nPoint.hp);
                    long mp = Util.CrisGH(player.nPoint.mp);
                    long dame = Util.CrisGH(player.nPoint.dame);
                    long hpmax = Util.CrisGH(player.nPoint.hpMax);
                    long kimax = Util.CrisGH(player.nPoint.mpMax);
                    if (player.isDie()) {
                        mapId = player.gender + 21;
                        x = 300;
                        y = 336;
                        hp = 1;
                        mp = 1;
                    } else {
                        if (MapService.gI().isMapPhoBan(mapId) || MapService.gI().isMapBlackBallWar(mapId) || MapService.gI().isMapTayKarin(mapId)
                                || MapService.gI().isMapMaBu12H(mapId) || MapService.gI().isMapMabu14H(mapId)
                                || ChangeMapService.gI().checkMapCanJoin(player, MapService.gI().getMapCanJoin(player, mapId, 0)) == null) {
                            mapId = player.gender + 21;
                            x = 300;
                            y = 336;
                        }
                    }

                    //data vị trí
                    dataArray.add(mapId);
                    dataArray.add(x);
                    dataArray.add(y);
                    String location = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.clear();
                    //data chỉ số
                    dataArray.add(player.nPoint.limitPower);
                    dataArray.add(player.nPoint.power);
                    dataArray.add(player.nPoint.tiemNang);
                    dataArray.add(player.nPoint.stamina);
                    dataArray.add(player.nPoint.maxStamina);
                    dataArray.add(Util.CrisGH(player.nPoint.hpg));
                    dataArray.add(Util.CrisGH(player.nPoint.mpg));
                    dataArray.add(Util.CrisGH(player.nPoint.dameg));
                    dataArray.add(player.nPoint.defg);
                    dataArray.add(player.nPoint.critg);
                    dataArray.add(0);
                    dataArray.add(hp);
                    dataArray.add(mp);
                    dataArray.add(dame);
                    dataArray.add(hpmax);
                    dataArray.add(kimax);
                    String point = dataArray.toJSONString();
                    dataArray.clear();

                    //data đậu thần
                    dataArray.add(player.magicTree.level);
                    dataArray.add(player.magicTree.currPeas);
                    dataArray.add(player.magicTree.isUpgrade ? 1 : 0);
                    dataArray.add(player.magicTree.lastTimeHarvest);
                    dataArray.add(player.magicTree.lastTimeUpgrade);
                    String magicTree = dataArray.toJSONString();
                    dataArray.clear();

                    //data body
                    JSONArray dataItem = new JSONArray();
                    for (Item item : player.inventory.itemsBody) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsBody = dataArray.toJSONString();
                    dataArray.clear();

                    //data bag
                    for (Item item : player.inventory.itemsBag) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsBag = dataArray.toJSONString();
                    dataArray.clear();

                    //data box
                    for (Item item : player.inventory.itemsBox) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsBox = dataArray.toJSONString();
                    dataArray.clear();

                    //data boxCollection
                    for (Item item : player.inventory.itemsBoxCollection) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsBoxCollection = dataArray.toJSONString();
                    dataArray.clear();

                    //Card
                    String dataCard = JSONValue.toJSONString(player.Cards);

                    //data box crack ball
                    for (Item item : player.inventory.itemsBoxCrackBall) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsBoxLuckyRound = dataArray.toJSONString();
                    dataArray.clear();

                    //Ma Bao Ve
                    dataArray.add(player.mbv);
                    dataArray.add(player.baovetaikhoan);
                    dataArray.add(player.mbvtime);
                    String dataBVTK = dataArray.toJSONString();
                    dataArray.clear();

                    //data bạn bè
                    JSONArray dataFE = new JSONArray();
                    for (Friend f : player.friends) {
                        dataFE.add(f.id);
                        dataFE.add(f.name);
                        dataFE.add(f.head);
                        dataFE.add(f.body);
                        dataFE.add(f.leg);
                        dataFE.add(f.bag);
                        dataFE.add(f.power);
                        dataArray.add(dataFE.toJSONString());
                        dataFE.clear();
                    }
                    String friend = dataArray.toJSONString();
                    dataArray.clear();

                    //data kẻ thù
                    for (Friend e : player.enemies) {
                        dataFE.add(e.id);
                        dataFE.add(e.name);
                        dataFE.add(e.head);
                        dataFE.add(e.body);
                        dataFE.add(e.leg);
                        dataFE.add(e.bag);
                        dataFE.add(e.power);
                        dataArray.add(dataFE.toJSONString());
                        dataFE.clear();
                    }
                    String enemy = dataArray.toJSONString();
                    dataArray.clear();

                    //data nhiệm vụ
                    dataArray.add(player.playerTask.taskMain.id);
                    dataArray.add(player.playerTask.taskMain.index);
                    dataArray.add(player.playerTask.taskMain.subTasks.get(player.playerTask.taskMain.index).count);
                    dataArray.add(player.playerTask.taskMain.lastTime);
                    String task = dataArray.toJSONString();
                    dataArray.clear();

                    //data nhiệm vụ hàng ngày
                    dataArray.add(player.playerTask.sideTask.template != null ? player.playerTask.sideTask.template.id : -1);
                    dataArray.add(player.playerTask.sideTask.receivedTime);
                    dataArray.add(player.playerTask.sideTask.count);
                    dataArray.add(player.playerTask.sideTask.maxCount);
                    dataArray.add(player.playerTask.sideTask.leftTask);
                    dataArray.add(player.playerTask.sideTask.level);
                    String sideTask = dataArray.toJSONString();
                    dataArray.clear();

                    //data nhiệm vụ bang hàng ngày
                    dataArray.add(player.playerTask.clanTask.template != null ? player.playerTask.clanTask.template.id : -1);
                    dataArray.add(player.playerTask.clanTask.receivedTime);
                    dataArray.add(player.playerTask.clanTask.count);
                    dataArray.add(player.playerTask.clanTask.maxCount);
                    dataArray.add(player.playerTask.clanTask.leftTask);
                    dataArray.add(player.playerTask.clanTask.level);
                    String clanTask = dataArray.toJSONString();
                    dataArray.clear();

                    //Siêu thần thủy
                    dataArray.add(player.winSTT);
                    dataArray.add(player.lastTimeWinSTT);
                    dataArray.add(player.callBossPocolo);
                    String dataSieuThanThuy = dataArray.toJSONString();
                    dataArray.clear();

                    //data achievement
                    if (player.achievement != null) {
                        for (AchievementQuest aq : player.achievement.getAchievementList()) {
                            JSONArray a = new JSONArray();
                            a.add(aq.completed);
                            a.add(aq.isRecieve);
                            dataArray.add(a.toJSONString());
                            a.clear();
                        }
                    }
                    String achievement = dataArray.toJSONString();
                    dataArray.clear();

                    //data box mail
                    for (Item item : player.inventory.itemsMailBox) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemMailBox = dataArray.toJSONString();
                    dataArray.clear();

                    //data item da ban
                    for (Item item : player.inventory.itemsDaBan) {
                        JSONArray opt = new JSONArray();
                        if (item.isNotNullItem()) {
                            dataItem.add(item.template.id);
                            dataItem.add(item.quantity);
                            JSONArray options = new JSONArray();
                            for (ItemOption io : item.itemOptions) {
                                opt.add(io.optionTemplate.id);
                                opt.add(io.param);
                                options.add(opt.toJSONString());
                                opt.clear();
                            }
                            dataItem.add(options.toJSONString());
                        } else {
                            dataItem.add(-1);
                            dataItem.add(0);
                            dataItem.add(opt.toJSONString());
                        }
                        dataItem.add(item.createTime);
                        dataArray.add(dataItem.toJSONString());
                        dataItem.clear();
                    }
                    String itemsDaBan = dataArray.toJSONString();
                    dataArray.clear();

                    //data item time
                    dataArray.add((player.itemTime.isUseBoHuyet ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet)) : 0));
                    dataArray.add((player.itemTime.isUseBoKhi ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi)) : 0));
                    dataArray.add((player.itemTime.isUseGiapXen ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen)) : 0));
                    dataArray.add((player.itemTime.isUseCuongNo ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo)) : 0));
                    dataArray.add((player.itemTime.isUseAnDanh ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh)) : 0));
                    dataArray.add((player.itemTime.isOpenPower ? (ItemTime.TIME_OPEN_POWER - (System.currentTimeMillis() - player.itemTime.lastTimeOpenPower)) : 0));
                    dataArray.add((player.itemTime.isUseMayDo ? (ItemTime.TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo)) : 0));
                    dataArray.add((player.itemTime.isEatMeal ? (ItemTime.TIME_EAT_MEAL - (System.currentTimeMillis() - player.itemTime.lastTimeEatMeal)) : 0));
                    dataArray.add(player.itemTime.iconMeal);
                    dataArray.add((player.itemTime.isUseTDLT ? ((player.itemTime.timeTDLT - (System.currentTimeMillis() - player.itemTime.lastTimeUseTDLT)) / 60 / 1000) : 0));
                    dataArray.add((player.itemTime.isUseHongDao1 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao1)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao3 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao3)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao5 ? (ItemTime.TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao5)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao10 ? (ItemTime.TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao10)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao25 ? (ItemTime.TIME_ITEM_1M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao25)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao50 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao50)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao99 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao99)) : 0));
                    dataArray.add((player.itemTime.isUseHongDao999 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeHongDao999)) : 0));
                    dataArray.add((player.itemTime.iscommenson ? (ItemTime.TIME_ITEM_50M - (System.currentTimeMillis() - player.itemTime.lastcommenson)) : 0));
                    dataArray.add((player.itemTime.IsKhauTrang ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastKhauTrang)) : 0));
                    dataArray.add((player.itemTime.IsKeoMotMat ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeKeoMotMat)) : 0));
                    dataArray.add((player.itemTime.IsSupbihacam ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeSupbihacam)) : 0));
                    dataArray.add((player.itemTime.Isbanhgatonhen ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimebanhgatonhen)) : 0));
                    dataArray.add((player.itemTime.Ishamburgersau ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimehamburgersau)) : 0));
                    dataArray.add((player.itemTime.Isthuocmothuong ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.LastTimethuocmothuong)) : 0));
                    dataArray.add((player.itemTime.Isthuocmodacbiet ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimethuocmodacbiet)) : 0));
                    dataArray.add((player.itemTime.iscuarangme ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimecuarangme)) : 0));
                    dataArray.add((player.itemTime.isbachtuocnuong ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimebachtuocnuong)) : 0));
                    dataArray.add((player.itemTime.istomtambot ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lasttimetomtambot)) : 0));
                    dataArray.add((player.itemTime.IsBocPha ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.TimeBocPha)) : 0));
                    dataArray.add((player.itemTime.isUseGTPT ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseGTPT)) : 0));
                    dataArray.add((player.itemTime.isUseDuoiKhiTNSM ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseDuoiKhiTNSM)) : 0));
                    dataArray.add((player.itemTime.istrbsd ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbsd)) : 0));
                    dataArray.add((player.itemTime.istrbhp ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbhp)) : 0));
                    dataArray.add((player.itemTime.istrbki ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimetrbki)) : 0));
                    dataArray.add((player.itemTime.istrbsdxd ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbsdxd)) : 0));
                    dataArray.add((player.itemTime.istrbhpxd ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbhpxd)) : 0));
                    dataArray.add((player.itemTime.istrbkixd ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimetrbkixd)) : 0));
                    dataArray.add((player.itemTime.isUseBoHuyet2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet2)) : 0));
                    dataArray.add((player.itemTime.isUseBoKhi2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi2)) : 0));
                    dataArray.add((player.itemTime.isUseGiapXen2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen2)) : 0));
                    dataArray.add((player.itemTime.isUseCuongNo2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo2)) : 0));
                    dataArray.add((player.itemTime.isUseAnDanh2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh2)) : 0));
                    dataArray.add((player.itemTime.isUseNCD ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseNCD)) : 0));
                    dataArray.add((player.itemTime.isUseBanhTet ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhTet)) : 0));
                    dataArray.add((player.itemTime.isUseBanhTrung ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhTrung)) : 0));
                    dataArray.add((player.itemTime.isUseFoodMeoDen1 ? (ItemTime.TIME_ITEM_3M - (System.currentTimeMillis() - player.itemTime.lastTimeUseFoodMeoDen1)) : 0));
                    dataArray.add((player.itemTime.isUseFoodMeoDen2 ? (ItemTime.TIME_ITEM_15M - (System.currentTimeMillis() - player.itemTime.lastTimeUseFoodMeoDen2)) : 0));
                    dataArray.add((player.itemTime.isUseChiMang2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseChiMang2)) : 0));
                    dataArray.add((player.itemTime.isUseChiMang3 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseChiMang3)) : 0));
                    dataArray.add((player.itemTime.isUseNedon ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseNedon)) : 0));
                    dataArray.add((player.itemTime.isUseNedon2 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseNedon2)) : 0));
                    dataArray.add((player.itemTime.isUsePhanSatThuong ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong)) : 0));
                    dataArray.add((player.itemTime.isUsePhanSatThuong2 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong2)) : 0));
                    dataArray.add((player.itemTime.isUsePhanSatThuong3 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUsePhanSatThuong3)) : 0));
                    dataArray.add((player.itemTime.isUseKamejoko ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseKamejoko)) : 0));
                    dataArray.add((player.itemTime.isUseKamejoko2 ? (ItemTime.TIME_ITEM_30S - (System.currentTimeMillis() - player.itemTime.lastTimeUseKamejoko2)) : 0));
                    dataArray.add((player.itemTime.isUseRocket1h ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseRocket1h)) : 0));
                    dataArray.add((player.itemTime.isUseSatThuongChuan ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSatThuongChuan)) : 0));
                    dataArray.add((player.itemTime.isUseSatThuongChuan2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSatThuongChuan2)) : 0));
                    dataArray.add((player.itemTime.isUseBuaTNSMDetu ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBuaTNSMDetu)) : 0));
                    dataArray.add((player.itemTime.isUseCoBonLa ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.lastTimeUseCoBonLa)) : 0));
                    dataArray.add((player.itemTime.isUseSauRieng ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseSauRieng)) : 0));
                    dataArray.add((player.itemTime.isUseMayDoLinhHon ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoLinhHon)) : 0));
                    dataArray.add((player.itemTime.isUseMayDoNgocBi ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoNgocBi)) : 0));
                    dataArray.add((player.itemTime.isRongXuong ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong)) : 0));
                    dataArray.add((player.itemTime.isRongXuong_2 ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong_2)) : 0));
                    dataArray.add((player.itemTime.isRongXuong_3 ? (ItemTime.TIME_ITEM_30M - (System.currentTimeMillis() - player.itemTime.LastTimeRongXuong_3)) : 0));
                    dataArray.add((player.itemTime.isUseBanhDeoC1 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC1)) : 0));
                    dataArray.add((player.itemTime.isUseBanhDeoC2 ? (ItemTime.TIME_ITEM_10M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC2)) : 0));
                    dataArray.add((player.itemTime.isUseBanhDeoC3 ? (ItemTime.TIME_ITEM_20M - (System.currentTimeMillis() - player.itemTime.lastTimeUseBanhDeoC3)) : 0));
                    dataArray.add((player.itemTime.isUseTrungThu1Trung ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThu1Trung)) : 0));
                    dataArray.add((player.itemTime.isUseTrungThu2Trung ? (ItemTime.TIME_ITEM_90M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThu2Trung)) : 0));
                    dataArray.add((player.itemTime.isUseTrungThuDB ? (ItemTime.TIME_ITEM_120M - (System.currentTimeMillis() - player.itemTime.lastTimeUseTrungThuDB)) : 0));
                    dataArray.add((player.itemTime.isUseHBTrungThu ? (ItemTime.TIME_ITEM_150M - (System.currentTimeMillis() - player.itemTime.lastTimeUseHBTrungThu)) : 0));
                    dataArray.add((player.itemTime.isUseMayDoSieuHoa ? (ItemTime.TIME_ITEM_60M - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDoSieuHoa)) : 0));
                    String itemTime = dataArray.toJSONString();
                    dataArray.clear();

                    //BDKB
                    dataArray.add(player.timesPerDayBDKB);
                    dataArray.add(player.lastTimeJoinBDKB);
                    String dataBDKB = dataArray.toJSONString();
                    dataArray.clear();

                    //CDRD
                    dataArray.add(player.joinCDRD);
                    dataArray.add(player.lastTimeJoinCDRD);
                    dataArray.add(player.talkToThuongDe);
                    dataArray.add(player.talkToThanMeo);
                    String dataCDRD = dataArray.toJSONString();
                    dataArray.clear();

                    //data trứng bư
                    if (player.mabuEgg != null) {
                        dataArray.add(player.mabuEgg.lastTimeCreate);
                        dataArray.add(player.mabuEgg.timeDone);
                    }
                    String mabuEgg = dataArray.toJSONString();
                    dataArray.clear();

                    //data trứng bill
                    if (player.billEgg != null) {
                        dataArray.add(player.billEgg.lastTimeCreate);
                        dataArray.add(player.billEgg.timeDone);
                    }
                    String billEgg = dataArray.toJSONString();
                    dataArray.clear();

                    //data trứng bư
                    if (player.duahau != null) {
                        dataArray.add(player.duahau.lastTimeCreate);
                        dataArray.add(player.duahau.timeDone);
                    }
                    String TrongDuaHau = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.isthoigianduhanh == true ? 1 : 0);
                    dataArray.add(player.thoigianduhanh);
                    String thoigianduhanh = dataArray.toJSONString();
                    dataArray.clear();

                    //data bùa
                    dataArray.add(player.charms.tdTriTue);
                    dataArray.add(player.charms.tdManhMe);
                    dataArray.add(player.charms.tdDaTrau);
                    dataArray.add(player.charms.tdOaiHung);
                    dataArray.add(player.charms.tdBatTu);
                    dataArray.add(player.charms.tdDeoDai);
                    dataArray.add(player.charms.tdThuHut);
                    dataArray.add(player.charms.tdDeTu);
                    dataArray.add(player.charms.tdTriTue3);
                    dataArray.add(player.charms.tdTriTue4);
                    //
                    dataArray.add(player.charms.tdDeTu2);
                    dataArray.add(player.charms.tdDeTu3);
                    dataArray.add(player.charms.tdDeTu4);
                    dataArray.add(player.charms.tdDeTu5);
                    dataArray.add(player.charms.tdDeTu7);
                    dataArray.add(player.charms.tdDeTu10);
                    dataArray.add(player.charms.tdDeTu20);
                    dataArray.add(player.charms.tdTriTue5);
                    dataArray.add(player.charms.tdTriTue7);
                    dataArray.add(player.charms.tdTriTue10);
                    dataArray.add(player.charms.tdTriTue20);
                    String charm = dataArray.toJSONString();
                    dataArray.clear();

                    //data skill
                    JSONArray dataSkill = new JSONArray();
                    for (Skill skill : player.playerSkill.skills) {
                        dataSkill.add(skill.template.id);
                        dataSkill.add(skill.point);
                        dataSkill.add(skill.lastTimeUseThisSkill);
                        dataSkill.add(skill.currLevel);
                        dataArray.add(dataSkill.toJSONString());
                        dataSkill.clear();
                    }
                    String skills = dataArray.toJSONString();
                    dataArray.clear();
                    dataArray.clear();

                    //data skill shortcut
                    for (int skillId : player.playerSkill.skillShortCut) {
                        dataArray.add(skillId);
                    }
                    String skillShortcut = dataArray.toJSONString();
                    dataArray.clear();

                    //data super rank
                    dataArray.add(player.superRank.lastTimePK);
                    dataArray.add(player.superRank.lastTimeReward);
                    dataArray.add(player.superRank.ticket);
                    dataArray.add(player.superRank.win);
                    dataArray.add(player.superRank.lose);
                    JsonObject jsonObject = new JsonObject();
                    JsonArray stringArray = new JsonArray();
                    for (String str : player.superRank.getHistory()) {
                        stringArray.add(str);
                    }
                    // Duyệt danh sách thời gian (long)
                    JsonArray longArray = new JsonArray();
                    for (Long value : player.superRank.getLastTime()) {
                        longArray.add(value);
                    }
                    jsonObject.add("history", stringArray);
                    jsonObject.add("lasttime", longArray);
                    String jsonString = new Gson().toJson(jsonObject);
                    dataArray.add(jsonString);
                    String dataSuperRank = dataArray.toJSONString();
                    dataArray.clear();

                    //data models
                    dataArray.add(player.TimeTrongNgay);
                    dataArray.add(player.solanhotong);
                    dataArray.add(player.NhanThuocTrongNgay);
                    dataArray.add(player.SoNgayTaoAcc);
                    dataArray.add(player.QuocTich);
                    dataArray.add(player.trbgiap);
                    dataArray.add(player.trbne);
                    dataArray.add(player.giaiphongan);
                    dataArray.add(player.PointRank);
                    String data_models = dataArray.toJSONString();
                    dataArray.clear();

                    //data models
                    dataArray.add(player.CheckTrongNgay);
                    dataArray.add(player.NhanLiXiForNPC_1);
                    dataArray.add(player.NhanLiXiForNPC_2);
                    dataArray.add(player.NhanLiXiForNPC_3);
                    dataArray.add(player.NhanLiXiForNPC_4);
                    dataArray.add(player.NhanLiXiForNPC_5);
                    dataArray.add(player.NhanLiXiForNPC_6);
                    dataArray.add(player.NhanLiXiForNPC_7);
                    dataArray.add(player.NhanLiXiForNPC_8);
                    dataArray.add(player.NhanLiXiForNPC_9);
                    dataArray.add(player.NhanLiXiForNPC_10);
                    dataArray.add(player.NhanLiXiForNPC_11);
                    dataArray.add(player.NhanLiXiForNPC_12);
                    dataArray.add(player.NhanLiXiForNPC_13);
                    dataArray.add(player.NhanLiXiForNPC_14);
                    dataArray.add(player.NhanLiXiForNPC_15);
                    dataArray.add(player.NhanLiXiForNPC_16);
                    dataArray.add(player.NhanLiXiForNPC_17);
                    dataArray.add(player.NhanLiXiForNPC_18);
                    dataArray.add(player.NhanLiXiForNPC_19);
                    dataArray.add(player.NhanLiXiForNPC_20);
                    dataArray.add(player.NhanLiXiForNPC_21);
                    dataArray.add(player.NhanLiXiForNPC_22);
                    dataArray.add(player.NhanLiXiForNPC_23);
                    dataArray.add(player.NhanLiXiForNPC_24);
                    dataArray.add(player.NhanLiXiForNPC_25);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_1);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_2);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_3);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_4);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_5);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_6);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_7);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_8);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_9);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_10);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_11);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_12);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_13);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_14);
                    dataArray.add(player.NhanKeoHayBiGheoNpc_15);
                    dataArray.add(player.UocMienPhi);
                    dataArray.add(player.NhanQuaHungVuongFree);
                    dataArray.add(player.DiemDanhBangHoi);
                    String data_models_event = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.LearnSkill.Time);
                    dataArray.add(player.LearnSkill.ItemTemplateSkillId);
                    dataArray.add(player.LearnSkill.Potential);
                    String LearnSkill = dataArray.toJSONString();
                    dataArray.clear();

                    for (int idSkill : player.BoughtSkill) {
                        dataArray.add(idSkill);
                    }
                    String BoughtSkill = dataArray.toJSONString();
                    dataArray.clear();

                    //Rương Gỗ
                    dataArray.add(player.levelWoodChest);
                    dataArray.add(player.goldChallenge);
                    dataArray.add(player.rubyChallenge);
                    dataArray.add(player.lastTimeRewardWoodChest);
                    dataArray.add(player.lastTimePKDHVT23);
                    String dataRuongGo = dataArray.toJSONString();
                    dataArray.clear();

                    //Võ đài sinh tử
                    dataArray.add(player.haveRewardVDST);
                    dataArray.add(player.thoiVangVoDaiSinhTu);
                    dataArray.add(player.lastTimePKVoDaiSinhTu);
                    dataArray.add(player.timePKVDST);
                    String dataVoDaiSinhTu = dataArray.toJSONString();
                    dataArray.clear();

                    //////////////////////////////////////
                    dataArray.add(player.ErrorMap);
                    dataArray.add(player.ErrorLocation);
                    dataArray.add(player.ErrorPay);
                    String check_band_account = dataArray.toJSONString();
                    dataArray.clear();

                    //data CARD
                    dataArray.add(player.THE_TUAN);
                    dataArray.add(player.LASTTIME_THE_TUAN);
                    dataArray.add(player.THE_THANG);
                    dataArray.add(player.LASTTIME_THE_THANG);
                    dataArray.add(player.THE_NAM);
                    dataArray.add(player.LASTTIME_THE_NAM);
                    dataArray.add(player.THE_CHI_TON);
                    dataArray.add(player.LASTTIME_THE_CHI_TON);
                    String data_the_card = dataArray.toJSONString();
                    dataArray.clear();

                    //data điểm danh
                    dataArray.add(player.CHECK_DAY_ON_ONE);
                    dataArray.add(player.DIEM_DANH);
                    dataArray.add(player.CHECK_DAY_ON_TUAN);
                    dataArray.add(player.DIEM_DANH_TUAN);
                    dataArray.add(player.CHECK_DAY_ON_THANG);
                    dataArray.add(player.DIEM_DANH_THANG);
                    dataArray.add(player.CHECK_DAY_ON_NAM);
                    dataArray.add(player.DIEM_DANH_NAM);
                    dataArray.add(player.CHECK_DAY_ON_CHI_TON);
                    dataArray.add(player.DIEM_DANH_CHI_TON);
                    String data_diem_danh = dataArray.toJSONString();
                    dataArray.clear();

                    //data điểm danh
                    dataArray.add(player.TopMayDamTraiDat);
                    dataArray.add(player.TopMayDamNamec);
                    dataArray.add(player.TopMayDamXayda);
                    String data_may_dam = dataArray.toJSONString();
                    dataArray.clear();

                    //data nội tại
                    dataArray.add(player.playerIntrinsic.intrinsic.id);
                    dataArray.add(player.playerIntrinsic.intrinsic.param1);
                    dataArray.add(player.playerIntrinsic.intrinsic.param2);
                    dataArray.add(player.playerIntrinsic.countOpen);
                    dataArray.add(player.effectSkill.isIntrinsic);
                    dataArray.add(player.effectSkill.skillID);
                    dataArray.add(player.effectSkill.cooldown);
                    dataArray.add(player.effectSkill.lastTimeUseSkill);
                    String intrinsic = dataArray.toJSONString();
                    dataArray.clear();

                    String pet = dataArray.toJSONString();
                    String petInfo = dataArray.toJSONString();
                    String petPoint = dataArray.toJSONString();
                    String petBody = dataArray.toJSONString();
                    String petSkill = dataArray.toJSONString();

                    //data pet
                    if (player.Detu != null) {
                        dataArray.add(player.Detu.typeDeTu);
                        dataArray.add(player.Detu.gender);
                        dataArray.add(player.Detu.name);
                        dataArray.add(player.fusion.typeFusion);
                        int timeLeftFusion = (int) (Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion));
                        dataArray.add(timeLeftFusion < 0 ? 0 : timeLeftFusion);
                        dataArray.add(player.Detu.status);
                        petInfo = dataArray.toJSONString();
                        dataArray.clear();

                        dataArray.add(player.Detu.nPoint.limitPower);
                        dataArray.add(player.Detu.nPoint.power);
                        dataArray.add(player.Detu.nPoint.tiemNang);
                        dataArray.add(player.Detu.nPoint.stamina);
                        dataArray.add(player.Detu.nPoint.maxStamina);
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.hpg));
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.mpg));
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.dameg));
                        dataArray.add(player.Detu.nPoint.defg);
                        dataArray.add(player.Detu.nPoint.critg);
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.hp));
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.mp));
                        dataArray.add(Util.CrisGH(player.Detu.nPoint.dame));
                        petPoint = dataArray.toJSONString();
                        dataArray.clear();

                        JSONArray items = new JSONArray();
                        JSONArray options = new JSONArray();
                        JSONArray opt = new JSONArray();
                        for (Item item : player.Detu.inventory.itemsBody) {
                            if (item.isNotNullItem()) {
                                dataItem.add(item.template.id);
                                dataItem.add(item.quantity);
                                for (ItemOption io : item.itemOptions) {
                                    opt.add(io.optionTemplate.id);
                                    opt.add(io.param);
                                    options.add(opt.toJSONString());
                                    opt.clear();
                                }
                                dataItem.add(options.toJSONString());
                            } else {
                                dataItem.add(-1);
                                dataItem.add(0);
                                dataItem.add(options.toJSONString());
                            }

                            dataItem.add(item.createTime);

                            items.add(dataItem.toJSONString());
                            dataItem.clear();
                            options.clear();
                        }
                        petBody = items.toJSONString();

                        JSONArray petSkills = new JSONArray();
                        for (Skill s : player.Detu.playerSkill.skills) {
                            JSONArray pskill = new JSONArray();
                            if (s.skillId != -1) {
                                pskill.add(s.template.id);
                                pskill.add(s.point);
                            } else {
                                pskill.add(-1);
                                pskill.add(0);
                            }
                            petSkills.add(pskill.toJSONString());
                        }
                        petSkill = petSkills.toJSONString();

                        dataArray.add(petInfo);
                        dataArray.add(petPoint);
                        dataArray.add(petBody);
                        dataArray.add(petSkill);
                        pet = dataArray.toJSONString();
                    }
                    dataArray.clear();

                    String myFather = dataArray.toJSONString();
                    String fatherInfo = dataArray.toJSONString();
                    String fatherPoint = dataArray.toJSONString();
                    String fatherBody = dataArray.toJSONString();
                    String fatherSkill = dataArray.toJSONString();

                    //data vip
                    dataArray.add(player.timesPerDayCuuSat);
                    dataArray.add(player.lastTimeCuuSat);
                    dataArray.add(player.nhanDeTuNangVIP);
                    dataArray.add(player.nhanVangNangVIP);
                    dataArray.add(player.nhanSKHVIP);
                    String dataVip = dataArray.toJSONString();
                    dataArray.clear();

                    //data thưởng ngọc rồng đen
                    for (int i = 0; i < player.rewardBlackBall.timeOutOfDateReward.length; i++) {
                        JSONArray dataBlackBall = new JSONArray();
                        dataBlackBall.add(player.rewardBlackBall.timeOutOfDateReward[i]);
                        dataBlackBall.add(player.rewardBlackBall.lastTimeGetReward[i]);
                        dataBlackBall.add(player.rewardBlackBall.quantilyBlackBall[i]);
                        dataArray.add(dataBlackBall.toJSONString());
                        dataBlackBall.clear();
                    }
                    String dataBlackBall = dataArray.toJSONString();
                    dataArray.clear();

                    //data pet Dao Lu
                    String petNguoiyeu = dataArray.toJSONString();
                    String petNguoiyeuInfo = dataArray.toJSONString();
                    String petNguoiyeuPoint = dataArray.toJSONString();
                    String petNguoiyeuBody = dataArray.toJSONString();
                    String petNguoiyeuSkill = dataArray.toJSONString();

                    //data danh hiệu
                    dataArray.add(player.isUseDanhHieu_ThienTu == true ? 1 : 0);
                    dataArray.add(player.LastTimeDanhHieu_ThienTu);
                    dataArray.add(player.isUseDanhHieu_2 == true ? 1 : 0);
                    dataArray.add(player.LastTimeDanhHieu_2);
                    dataArray.add(player.isUseDanhHieu_3 == true ? 1 : 0);
                    dataArray.add(player.LastTimeDanhHieu_3);
                    dataArray.add(player.isUseDanhHieu_4 == true ? 1 : 0);
                    dataArray.add(player.LastTimeDanhHieu_4);
                    String danh_hieu = dataArray.toJSONString();
                    dataArray.clear();

                    //data nhiệm vụ kol
                    dataArray.add(player.playerTask.kolTask.template != null ? player.playerTask.kolTask.template.id : -1);
                    dataArray.add(player.playerTask.kolTask.count);
                    String kolTask = dataArray.toJSONString();
                    dataArray.clear();

                    //data nhiệm vụ event
                    dataArray.add(player.playerTask.eventTask.template != null ? player.playerTask.eventTask.template.id : -1);
                    dataArray.add(player.playerTask.eventTask.receivedTime);
                    dataArray.add(player.playerTask.eventTask.count);
                    dataArray.add(player.playerTask.eventTask.maxCount);
                    dataArray.add(player.playerTask.eventTask.leftTask);
                    dataArray.add(player.playerTask.eventTask.level);
                    String eventTask = dataArray.toJSONString();
                    dataArray.clear();

                    //Data item event
                    dataArray.add(player.itemEvent.remainingTVGSCount);
                    dataArray.add(player.itemEvent.lastTVGSTime);
                    dataArray.add(player.itemEvent.remainingHHCount);
                    dataArray.add(player.itemEvent.lastHHTime);
                    dataArray.add(player.itemEvent.remainingBNCount);
                    dataArray.add(player.itemEvent.lastBNTime);
                    dataArray.add(player.itemEvent.remainingKeoGiangSinhCount);
                    dataArray.add(player.itemEvent.lastKeoGiangSinhTime);
                    String dataItemEvent = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopMoLiXi);
                    dataArray.add(player.DuaTopTangLiXi);
                    dataArray.add(player.DuaTopBanPhaoHoa);
                    dataArray.add(player.DuaTopBanPhaoHoaVIP);
                    String data_event_new_year = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopTrangTriCayNoel);
                    dataArray.add(player.DuaTopCheTaoNguoiTuyet);
                    dataArray.add(player.DuaTopCheTaoNguoiTuyetBangGia);
                    dataArray.add(player.DuaTopDotDiem);
                    String data_event_christ_mas = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopPhaoHoaVuLan);
                    dataArray.add(player.DuaTopHoaDang);
                    dataArray.add(player.DuaTopHoaDangCoLoiChuc);
                    String data_event_vulan = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopMoHopMaQuy);
                    dataArray.add(player.DuaTopThiepHalloween);
                    String data_event_halloween = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopMoThiep83);
                    dataArray.add(player.DuaTopTangBongHoaHong);
                    String data_event_8_3 = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopLamBanhTrungThu);
                    dataArray.add(player.DuaTopMoHopTrungThuDacBiet);
                    String data_event_trung_thu = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopMoTrungRongVang);
                    dataArray.add(player.typeBanhDangNau);
                    dataArray.add(player.lastTimeNauBanhHungVuong);
                    dataArray.add(player.DuaTopMoHopQuaGioTo);
                    dataArray.add(player.DuaTopDangBanhHungVuong);
                    dataArray.add(player.DuaTopDoiDuaHau);
                    String data_event_hung_vuong = dataArray.toJSONString();
                    dataArray.clear();

                    dataArray.add(player.DuaTopMoHopBlackFriday);
                    dataArray.add(player.DuaTopMuaSamBlackFriday);
                    String data_event_black_friday = dataArray.toJSONString();
                    dataArray.clear();

                    //Data Luyện Tập
                    dataArray.add(player.levelLuyenTap);
                    dataArray.add(player.dangKyTapTuDong);
                    dataArray.add(player.mapIdDangTapTuDong);
                    dataArray.add(player.tnsmLuyenTap);
                    if (player.isOffline) {
                        dataArray.add(player.lastTimeOffline);
                    } else {
                        dataArray.add(System.currentTimeMillis());
                    }
                    dataArray.add(player.traning.getTop());
                    dataArray.add(player.traning.getTime());
                    dataArray.add(player.traning.getLastTime());
                    dataArray.add(player.traning.getLastTop());
                    dataArray.add(player.traning.getLastRewardTime());
                    String dataLuyenTap = dataArray.toJSONString();
                    dataArray.clear();

                    String dataBadges = JSONValue.toJSONString(player.dataBadges);

                    String dataTaskBadges = JSONValue.toJSONString(player.dataTaskBadges);

                    String dataDailyGift = JSONValue.toJSONString(player.dailyGiftData);

                    dataArray.clear();

                    String query = " update player set head = ?, have_tennis_space_ship = ?, "
                            + "clan_id = ?, data_inventory = ?, data_location = ?, data_point = ?, data_magic_tree = ?, items_body = ?, items_bag = ?, "
                            + "items_box = ?, items_box_Collection = ?, items_box_lucky_round = ?, items_daban = ?, item_mails_box = ?, friends = ?, enemies = ?, data_intrinsic = ?, "
                            + "data_item_time = ?, data_task = ?, data_mabu_egg = ?, pet = ?, data_black_ball = ?, data_side_task = ?, data_kol_task = ?, data_event_task = ?, data_charm = ?, skills = ?, "
                            + "skills_shortcut = ?, check_band_account = ?, data_luyentap = ?, data_card = ?, bill_data = ?, data_trong_dua_hau = ?, thoigianduhanh = ?, masterDoesNotAttack = ?, "
                            + "masterDoesNotAttackBo = ?, masterDoesNotAttackMe = ?, notify = ?, bandokhobau = ?, doanhtrai = ?, conduongrandoc = ?, data_clan_task = ?, data_achievement = ?, "
                            + "data_vip = ?, lasttimepkcommeson = ?, sieuthanthuy = ?, baovetaikhoan = ?, firstTimeLogin = ?, dataBadges = ?, dataTaskBadges = ?, ruonggo = ?, vodaisinhtu = ?, "
                            + "dragon_christmas = ?, dragon_halloween = ?, rank = ?, data_super_rank = ?, data_item_event = ?, data_the_card = ?, data_diem_danh = ?, data_may_dam = ?, data_nguoi_yeu = ?, "
                            + "NguoiYeuDoesNotAttack = ?, CononeDoesNotAttack = ?, ContwoDoesNotAttack = ?, ConthreeDoesNotAttack = ?, dailyGift = ?, BoughtSkill = ?, LearnSkill = ?, event_point = ?, "
                            + "event_point_boss = ?, event_point_nhs = ?, event_point_quai = ?, diem_huy_diet = ?, diem_chien_truong_namek = ?, diem_su_kien_tet = ?, diem_su_kien_giangsinh = ?, "
                            + "diem_su_kien_halloween = ?, diem_su_kien_8_3 = ?, diem_su_kien_trungthu = ?, diem_su_kien_hung_vuong = ?, diem_su_kien_black_friday = ?, diem_su_kien_20_10 = ?, "
                            + "data_event_new_year = ?, "
                            + "data_event_christ_mas = ?, data_event_vulan = ?, data_event_halloween = ?, data_event_8_3 = ?, data_event_trung_thu = ?, data_event_hung_vuong = ?, data_event_black_friday = ?, "
                            + "data_event_models = ?, data_models = ?, hp_point_fusion = ?, mp_point_fusion = ?, dame_point_fusion = ?, danh_hieu = ? where id = ?";
                    ConnectDB.executeUpdate(query,
                            player.head,
                            player.haveTennisSpaceShip,
                            (player.clan != null ? player.clan.id : -1),
                            inventory,
                            location,
                            point,
                            magicTree,
                            itemsBody,
                            itemsBag,
                            itemsBox,
                            itemsBoxCollection,
                            itemsBoxLuckyRound,
                            itemsDaBan,
                            itemMailBox,
                            friend,
                            enemy,
                            intrinsic,
                            itemTime,
                            task,
                            mabuEgg,
                            pet,
                            dataBlackBall,
                            sideTask,
                            kolTask,
                            eventTask,
                            charm,
                            skills,
                            skillShortcut,
                            check_band_account,
                            dataLuyenTap,
                            dataCard,
                            billEgg,
                            TrongDuaHau,
                            thoigianduhanh,
                            player.doesNotAttack,
                            player.doesNotAttackBo,
                            player.doesNotAttackMe,
                            player.notify,
                            dataBDKB,
                            player.lastTimeJoinDT,
                            dataCDRD,
                            clanTask,
                            achievement,
                            dataVip,
                            player.lastPkCommesonTime,
                            dataSieuThanThuy,
                            dataBVTK,
                            Util.toDateString(player.firstTimeLogin),
                            dataBadges,
                            dataTaskBadges,
                            dataRuongGo,
                            dataVoDaiSinhTu,
                            player.lastTimeShenronAppeared_Christmas,
                            player.lastTimeShenronAppeared_Halloween,
                            player.superRank.rank,
                            dataSuperRank,
                            dataItemEvent,
                            data_the_card,
                            data_diem_danh,
                            data_may_dam,
                            petNguoiyeu,
                            player.doesNotAttackNguoiYeu,
                            player.doesNotAttackConone,
                            player.doesNotAttackContwo,
                            player.doesNotAttackConthree,
                            dataDailyGift,
                            BoughtSkill,
                            LearnSkill,
                            player.event.getEventPoint(),
                            player.event.getEventPointBHM(),
                            player.event.getEventPointNHS(),
                            player.event.getEventPointQuai(),
                            player.event.getHakaiPoint(),
                            player.event.getNamekWarPoint(),
                            player.event.getLunaNewYearPoint(),
                            player.event.getChristMasPoint(),
                            player.event.getHalloweenPoint(),
                            player.event.getInternationalWomensDayPoint(),
                            player.event.getTrungThuPoint(),
                            player.event.getHungVuongPoint(),
                            player.event.getBlackFridayPoint(),
                            player.event.get20_10Point(),
                            data_event_new_year,
                            data_event_christ_mas,
                            data_event_vulan,
                            data_event_halloween,
                            data_event_8_3,
                            data_event_trung_thu,
                            data_event_hung_vuong,
                            data_event_black_friday,
                            data_models_event,
                            data_models,
                            player.pointfusion.getHpFusion(),
                            player.pointfusion.getMpFusion(),
                            player.pointfusion.getDameFusion(),
                            danh_hieu,
                            player.id);
                    // ✅ Lưu cột mocnap vào DB
                    try {
                        String mocnapJson = new com.google.gson.Gson().toJson(player.mocnap);
                        ConnectDB.executeUpdate("UPDATE account SET mocnap = ? WHERE id = ?", mocnapJson, player.account_id);
                    } catch (Exception ex) {
                        Utils.Logger.logException(PlayerDAO.class, ex, "Lỗi lưu mốc nạp cho player " + player.name);
                    }

                    if (updateTimeLogout) {
                        PlayerService.updateAccountLogout(player);
                    }
//------------------------------------------------------------------------------
                    if (player.isOffline) {
                        Logger.warning(TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin() + "m: Updates Succesfully Player : " + player.name + " {" + (System.currentTimeMillis() - st) + " ms}\n");
                        player.dispose();
                    } else {
                        Logger.success(TimeUtil.getCurrHour() + "h" + TimeUtil.getCurrMin() + "m: Saved Succesfully Player : " + player.name + " {" + (System.currentTimeMillis() - st) + " ms}\n");
                    }
                } catch (Exception e) {
                    Logger.logException(PlayerDAO.class, e, "Lỗi save player " + player.name);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            player.setSaving(false);
        }

    }

    public static void updateBangXepHang(Player player) {
        if (!player.isDeTu && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree && !player.isPhanThan && !player.isBoss) {
            Service.gI().sendFlagBag(player);
        }
        if (player.iDMark != null && player.iDMark.isLoadedAllDataPlayer()) {
            try {
                // Data new year
                JSONArray dataNewYear = new JSONArray();
                dataNewYear.add(player.DuaTopMoLiXi);
                dataNewYear.add(player.DuaTopTangLiXi);
                dataNewYear.add(player.DuaTopBanPhaoHoa);
                dataNewYear.add(player.DuaTopBanPhaoHoaVIP);

                // Data noel
                JSONArray dataNoel = new JSONArray();
                dataNoel.add(player.DuaTopTrangTriCayNoel);
                dataNoel.add(player.DuaTopCheTaoNguoiTuyet);
                dataNoel.add(player.DuaTopCheTaoNguoiTuyetBangGia);
                dataNoel.add(player.DuaTopDotDiem);

                // Data vulan
                JSONArray dataVuLan = new JSONArray();
                dataVuLan.add(player.DuaTopPhaoHoaVuLan);
                dataVuLan.add(player.DuaTopHoaDang);
                dataVuLan.add(player.DuaTopHoaDangCoLoiChuc);

                // Data halloween
                JSONArray dataHalloween = new JSONArray();
                dataHalloween.add(player.DuaTopMoHopMaQuy);
                dataHalloween.add(player.DuaTopThiepHalloween);

                // Data 8-3
                JSONArray data83 = new JSONArray();
                data83.add(player.DuaTopMoThiep83);
                data83.add(player.DuaTopTangBongHoaHong);

                // Data TRUNG THU
                JSONArray dataTrungThu = new JSONArray();
                dataTrungThu.add(player.DuaTopLamBanhTrungThu);
                dataTrungThu.add(player.DuaTopMoHopTrungThuDacBiet);

                // Data HUNG VUONG
                JSONArray dataHungVuong = new JSONArray();
                dataHungVuong.add(player.DuaTopMoTrungRongVang);
                dataHungVuong.add(player.typeBanhDangNau);
                dataHungVuong.add(player.lastTimeNauBanhHungVuong);
                dataHungVuong.add(player.DuaTopMoHopQuaGioTo);
                dataHungVuong.add(player.DuaTopDangBanhHungVuong);
                dataHungVuong.add(player.DuaTopDoiDuaHau);

                // Data BLACK FRIDAY
                JSONArray dataBlackFriday = new JSONArray();
                dataBlackFriday.add(player.DuaTopMoHopBlackFriday);
                dataBlackFriday.add(player.DuaTopMuaSamBlackFriday);

                // Data DATA MÁY ĐẤM
                JSONArray dataMayDam = new JSONArray();
                dataMayDam.add(player.TopMayDamTraiDat);
                dataMayDam.add(player.TopMayDamNamec);
                dataMayDam.add(player.TopMayDamXayda);

                String query = "UPDATE player SET data_event_new_year=?, data_event_christ_mas=?, data_event_vulan=?, data_event_halloween=?, data_event_8_3=?, data_event_trung_thu=?, "
                        + "data_event_hung_vuong=?, data_event_black_friday=?, data_may_dam=? WHERE id=?";
                ConnectDB.executeUpdate(query,
                        dataNewYear.toJSONString(),
                        dataNoel.toJSONString(),
                        dataVuLan.toJSONString(),
                        dataHalloween.toJSONString(),
                        data83.toJSONString(),
                        dataTrungThu.toJSONString(),
                        dataHungVuong.toJSONString(),
                        dataBlackFriday.toJSONString(),
                        dataMayDam.toJSONString(),
                        player.id);

                TopServer.LoadingTop();
            } catch (Exception e) {
                Logger.logException(PlayerDAO.class, e, "Lỗi save player " + player.name);
            }
        }
    }

    public static boolean subThoiVang(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update account set thoi_vang = (thoi_vang - ?), active = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, num);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
            player.getSession().goldBar -= num;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
            return false;
        } finally {
        }
        return false;
    }

    public static void saveisBienHinh(Player player) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update player set isbienhinh = ? where id = ?");
            ps.setInt(1, player.isbienhinh);
            ps.setInt(2, (int) player.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update Biến Hình" + player.name);
        } finally {
        }
    }

    public static boolean setIs_gift_box(Player player) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update account set is_gift_box = 0 where id = ?");
            ps.setInt(1, player.getSession().userId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update new_reg " + player.name);
            return false;
        }
        return true;
    }

    public static void addHistoryReceiveGoldBar(Player player, int goldBefore, int goldAfter,
            int goldBagBefore, int goldBagAfter, int goldBoxBefore, int goldBoxAfter) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("insert into history_receive_goldbar(player_id,player_name,gold_before_receive,"
                    + "gold_after_receive,gold_bag_before,gold_bag_after,gold_box_before,gold_box_after) values (?,?,?,?,?,?,?,?)");
            ps.setInt(1, (int) player.id);
            ps.setString(2, player.name);
            ps.setInt(3, goldBefore);
            ps.setInt(4, goldAfter);
            ps.setInt(5, goldBagBefore);
            ps.setInt(6, goldBagAfter);
            ps.setInt(7, goldBoxBefore);
            ps.setInt(8, goldBoxAfter);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update thỏi vàng " + player.name);
        } finally {
            try {
                ps.close();
            } catch (Exception e) {
            }
        }
    }
      public static boolean MuaThanhVien(Player player, int num) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            if (player.getSession().vnd >= num) {
            } else {
                return false;
            }
            ps = con.prepareStatement("update account set vnd = (vnd - ?), active = ? where id = ?");
            ps.setInt(1, num);
            ps.setInt(2, player.getSession().actived ? 1 : 0);
            ps.setInt(3, player.getSession().userId);
            ps.executeUpdate();
            player.getSession().vnd -= num;
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update mua thành viên " + player.name);
            return false;
        }
        return true;
    }

    public static boolean insertHistoryGold(Player player, int quantily) {
        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("insert into history_gold(name,gold) values (?,?)");
            ps.setString(1, player.name);
            ps.setInt(2, quantily);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi insert history_gold " + player.name);
            return false;
        }
        return true;
    }

    public static boolean checkLogout(Connection con, Player player) {
        long lastTimeLogout = 0;
        long lastTimeLogin = 0;
        try {
            PreparedStatement ps = con.prepareStatement("select * from account where id = ? limit 1");
            ps.setInt(1, player.getSession().userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
            }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        } catch (Exception e) {
            return false;
        }
        return lastTimeLogout > lastTimeLogin;
    }

    public static void LogNapTIen(String uid, String menhgia, String seri, String code, String tranid) {
        String UPDATE_PASS = "INSERT INTO naptien(uid,sotien,seri,code,loaithe,time,noidung,tinhtrang,tranid,magioithieu) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = null;
            //UPDATE NRSD,
            ps = conn.prepareStatement(UPDATE_PASS);
            conn.setAutoCommit(false);
            //NGOC RONG SAO DEN
            ps.setString(1, uid);
            ps.setString(2, menhgia);
            ps.setString(3, seri);
            ps.setString(4, code);

            ps.setString(5, "VIETTEL");
            ps.setString(6, "123123123123");
            ps.setString(7, "dang nap the");
            ps.setString(8, "0");
            ps.setString(9, tranid);
            ps.setString(10, "0");
            if (ps.executeUpdate() == 1) {
            }

            conn.commit();
            //UPDATE NRSD
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void resetCharms(Player player) {
        String UPDATE = "UPDATE player SET data_charm = ? WHERE id = ?";
        try {
            Connection conn = ConnectDB.getConnection();
            PreparedStatement ps = null;

            conn.setAutoCommit(false);

            // Reset charms trong RAM
            player.charms.resetAllCharms();

            // Chuyển charms thành JSON array
            JSONArray arr = player.charms.toJSONArray();

            ps = conn.prepareStatement(UPDATE);
            ps.setString(1, arr.toJSONString());
            ps.setInt(2, (int) player.id);

            if (ps.executeUpdate() == 1) {
                // Update thành công
            }

            conn.commit();
            conn.close();

            Service.gI().sendThongBao(player, "Tất cả bùa đã được reset!");
        } catch (SQLException e) {
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Có lỗi khi reset bùa!");
        }
    }

    public static void subTongNap2(Player player, long value) {
        if (value <= 0) {
            Service.gI().sendThongBao(player, "Giá trị không hợp lệ!");
            return;
        }

        String UPDATE = "UPDATE account SET coin = GREATEST(coin - ?, 0) WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            ps = conn.prepareStatement(UPDATE);
            ps.setLong(1, value);
            ps.setInt(2, player.getSession().userId);

            int check = ps.executeUpdate();

            if (check > 0) {
                // Cập nhật trong RAM
                player.getSession().coin -= value;
                if (player.getSession().coin < 0) {
                    player.getSession().coin = 0;
                }

                conn.commit();
                Service.gI().sendThongBao(player, "Đã trừ " + Util.format(value) + " VNĐ ");
            } else {
                conn.rollback();
                Service.gI().sendThongBao(player, "Không thể cập nhật ");
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            Service.gI().sendThongBao(player, "Có lỗi khi trừ tổng nạp ");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
