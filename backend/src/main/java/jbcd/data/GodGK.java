package jbcd.data;

import nro.card.Card;
import nro.card.OptionCard;
import consts.ConstPlayer;
import Data.DataGame;
import nro.clan.Clan;
import nro.clan.ClanMember;
import nro.clan.ClanService;
import models.Item.Item;
import models.Item.ItemService;
import models.Item.ItemTime;
import nro.npc.Special.MabuEgg;
import nro.npc.Special.BillEgg;
import nro.npc.Special.MagicTree;
import nro.player.Enemy;
import nro.player.Friend;
import nro.player.Fusion;
import nro.player.Detu;
import nro.player.Player;
import nro.skill.Skill;
import nro.task.TaskMain;
import nro.server.Client;
import nro.server.Manager;
import nro.intrinsic.IntrinsicService;
import nro.services.MapService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.Logger;
import Utils.SkillUtil;
import Utils.TimeUtil;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Utils.Util;
import java.util.Calendar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import nro.server.AntiLogin;
import nro.services.PlayerService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import consts.ConstTask;
import event.EventManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jbcd.ConnectDB;
import network.session.MySession;
import nro.badges.BadgesData;
import nro.badges.BadgesTask;
import nro.badges.BadgesTaskService;
import jbcd.dao.PlayerDAO;
import nro.player.DailyGift.DailyGiftData;
import nro.player.DailyGift.DailyGiftService;
import models.Item.ItemOption;
import nro.npc.Special.MelonPlant;
import nro.template.AchievementQuest;
import jbcd.CrisResultSet;

public class GodGK {

    public static Boolean baotri = false;

    public static Player login(MySession session, AntiLogin al) {
        Player player = null;
        CrisResultSet rs = null;
        Player plInGame;
        try {
            rs = ConnectDB.executeQuery("select * from account where username = ? and password = ?", session.uu, session.pp);
            if (rs.first()) {
                session.userId = rs.getInt("account.id");
                session.isJail = rs.getBoolean("is_jail");
                // Keep compatibility with both the legacy Founder flag and the
                // is_admin flag managed by AccountPanel/database tools.
                session.isFounder = rs.getBoolean("isFounder") || rs.getBoolean("is_admin");
                session.isQuanTriVien = rs.getBoolean("isQuanTriVien");
                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("account.thoi_vang");
                session.bdPlayer = rs.getDouble("account.bd_player");
                session.vnd = rs.getInt("vnd");
                session.tongnap = rs.getInt("tongnap");
                session.coin = rs.getInt("account.coin");
                session.danap = rs.getInt("danap");
                session.Vip_Point = rs.getInt("Vip_Point");
                session.accountAgeDays = rs.getInt("accountAgeDays");
                session.vetuan = rs.getInt("vetuan");
                session.vethang = rs.getInt("vethang");
                session.vethangExpire = rs.getLong("vethang_expire");
                session.vetuanExpire = rs.getLong("vetuan_expire");
                long lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
                int secondsPass1 = (int) ((System.currentTimeMillis() - lastTimeLogin) / 1000);
                long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);
                long createTime = rs.getTimestamp("accountCreatedTimes").getTime();
                int deltaTime = (int) ((System.currentTimeMillis() - createTime) / 1000);
                session.timeCreateAcount = rs.getTimestamp("accountCreatedTimes").getTime();
                if (!session.isFounder) {
                    Service.gI().sendThongBaoOK(session, "Game đang bảo trì");
                    Service.gI().sendLoginFail(session, false); 
                    return null; 
                }

                plInGame = Client.gI().getPlayerByUser(session.userId);
                boolean kickedOldSession = false;

                if (plInGame != null && plInGame.getSession() != null && plInGame.getSession() != session) {
                    try {
                        Service.gI().sendThongBaoOK(plInGame.getSession(), "Tài khoản bị đăng nhập ở nơi khác!");
                    } catch (Exception e) {
                    }

                    try {
                        Client.gI().kickSession(plInGame.getSession());
                    } catch (Exception e) {
                    }

                    kickedOldSession = true;
                }
                if (rs.getBoolean("ban")) {
                    Service.gI().sendThongBaoOK(session, "Tài khoản này đang bị khóa. Liên hệ Admin để biết thêm thông tin");
                } else if (baotri && session.isFounder) {
                    Service.gI().sendThongBaoOK(session, "Máy chủ đang bảo trì!");
                } else if (false) {
                } else if (!kickedOldSession && secondsPass1 < Manager.SECOND_WAIT_LOGIN) {
                    if (secondsPass < secondsPass1) {
                        Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass);
                        return null;
                    }
                    Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass1);
                    return null;
                } else {
                    if (!kickedOldSession && secondsPass < Manager.SECOND_WAIT_LOGIN) {
                        Service.gI().sendWaitToLogin(session, Manager.SECOND_WAIT_LOGIN - secondsPass);
                    } else {
                        rs = ConnectDB.executeQuery("select * from player where account_id = ? limit 1", session.userId);
                        if (!rs.first()) {
                            //-28 -4 version data game
                            DataGame.sendVersionGame(session);
                            //-31 data item background
                            DataGame.sendDataItemBG(session);
                            Service.gI().switchToCreateChar(session);
                        } else {
                            // Check lại lần 2 sát thời điểm load để chống 2 tab bấm cùng lúc
                            plInGame = Client.gI().getPlayerByUser(session.userId);
                            if (plInGame != null && plInGame.getSession() != null && plInGame.getSession() != session) {
                                try {
                                    Service.gI().sendThongBaoOK(plInGame.getSession(), "Tài khoản bị đăng nhập ở nơi khác!");
                                } catch (Exception e) {
                                }

                                try {
                                    Client.gI().kickSession(plInGame.getSession());
                                } catch (Exception e) {
                                }
                            }

                            if ((player = loadPlayer(rs, false)) != null) {
                                player.isPlayer = true;
                                session.player = player;

                                // Load cột mocnap từ DB
                                String mocnapJson = "[0,0,0,0,0]";
                                CrisResultSet mocnapRS = null;
                                try {
                                    mocnapRS = ConnectDB.executeQuery("SELECT mocnap FROM account WHERE id = ?", session.userId);
                                    if (mocnapRS.first()) {
                                        mocnapJson = mocnapRS.getString("mocnap");
                                        if (mocnapJson == null || mocnapJson.isEmpty()) {
                                            mocnapJson = "[0,0,0,0,0]";
                                        }
                                    }
                                } catch (Exception e) {
                                    mocnapJson = "[0,0,0,0,0]";
                                } finally {
                                    if (mocnapRS != null) {
                                        mocnapRS.dispose();
                                    }
                                }
                                player.mocnap = new com.google.gson.Gson().fromJson(mocnapJson, int[].class);

                                player.deltaTime = deltaTime;
                                player.isNewMember = !Util.isTimeDifferenceGreaterThanNDays(createTime, 1);
                                player.accountCreatedDays = Util.getDaysSince(createTime);

                                ConnectDB.executeUpdate(
                                        "UPDATE account SET last_time_login = ? WHERE id = ?",
                                        new java.sql.Timestamp(System.currentTimeMillis()), session.userId
                                );
                            }

                        }
                    }
                }
                al.reset();
                if (player != null) {
                    session.player = player;

                    Client.gI().safePut(player);

                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_30_0) {

                    }

                    Service.gI().changeFlag(player, 0);
                    PlayerService.gI().dailyLogin(player);
                }
            } else {
                Service.gI().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                Service.gI().sendLoginFail(session, false);
                al.wrong();
            }
        } catch (Exception e) {
            Logger.error(session.uu);
            if (player != null) {
                player.dispose();
                player = null;
            }
            Logger.logException(GodGK.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    private static void setPlayerTask(Player player, int taskId, int subTaskIndex) {
        try {
            TaskMain newTask = TaskService.gI().getTaskMainById(player, taskId);
            if (newTask.id != taskId) {
                return;
            }
            if (subTaskIndex < 0 || subTaskIndex >= newTask.subTasks.size()) {
                return;
            }
            newTask.index = (byte) subTaskIndex;
            newTask.subTasks.get(newTask.index).count = 0;
            player.playerTask.taskMain = newTask;
            TaskService.gI().sendTaskMain(player);
        } catch (Exception e) {
        }
    }

    private static Player loadPlayer(CrisResultSet rs, boolean isOffline) throws Exception {
        Player player = null;
        try {
            long plHp;
            long plMp;
            long plDame;
            long plMaxHP;
            long plMaxKI;
            JSONArray dataArray;
            player = new Player();
            //base info
            player.id = rs.getInt("id");
            player.name = rs.getString("name");
            player.head = rs.getShort("head");
            player.gender = rs.getByte("gender");
            player.account_id = rs.getInt("account_id");
            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
            player.lastTimeDropTail = rs.getLong("lastTimeDropTail");
            player.Lasttimekhisukien = rs.getLong("lastTimeKhiSuKien");
            if (player.head == -1) {
                switch (player.gender) {
                    case 0:
                        player.head = 64;
                        break;
                    case 1:
                        player.head = 9;
                        break;
                    case 2:
                        player.head = 6;
                        break;
                }
            }
            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

            int clanId = rs.getInt("clan_id");
            if (clanId != -1) {
                try {
                    Clan clan = ClanService.gI().getClanById(clanId);
                    for (ClanMember cm : clan.getMembers()) {
                        if (cm.id == player.id) {
                            if (!isOffline) {
                                clan.addMemberOnline(player);
                            }
                            player.clan = clan;
                            player.clanMember = cm;
                            break;
                        }
                    }
                } catch (Exception e) {
                    player.clan = null;
                }
            }
            player.event.setEventPoint(rs.getInt("event_point"));
            player.event.setEventPointBHM(rs.getInt("event_point_boss"));
            player.event.setEventPointNHS(rs.getInt("event_point_nhs"));
            player.event.setEventPointQuai(rs.getInt("event_point_quai"));
            player.event.setHakaiPoint(rs.getInt("diem_huy_diet"));
            player.event.setNamekWarPoint(rs.getInt("diem_chien_truong_namek"));
            player.event.setLunaNewYearPoint(rs.getInt("diem_su_kien_tet"));
            player.event.setChristMasPoint(rs.getInt("diem_su_kien_giangsinh"));
            player.event.setHalloweenPoint(rs.getInt("diem_su_kien_halloween"));
            player.event.setInternationalWomensDayPoint(rs.getInt("diem_su_kien_8_3"));
            player.event.setTrungThuPoint(rs.getInt("diem_su_kien_trungthu"));
            player.event.setHungVuongPoint(rs.getInt("diem_su_kien_hung_vuong"));
            player.event.setBlackFridayPoint(rs.getInt("diem_su_kien_black_friday"));
            player.event.set20_10Point(rs.getInt("diem_su_kien_20_10"));

            player.pointfusion.setHpFusion(rs.getInt("hp_point_fusion"));
            player.pointfusion.setMpFusion(rs.getInt("mp_point_fusion"));
            player.pointfusion.setDameFusion(rs.getInt("dame_point_fusion"));

            // data kim lượng
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
            player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));

            if (dataArray.size() > 3) {
                player.inventory.Exp_Vip = Integer.parseInt(String.valueOf(dataArray.get(3)));
            } else {
                player.inventory.Exp_Vip = 0; // hoặc giá trị mặc định
            }
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("thoigianduhanh"));
            player.isthoigianduhanh = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
            player.thoigianduhanh = Long.parseLong(String.valueOf(dataArray.get(1)));
            dataArray.clear();

            // data rada card
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_card"));
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject obj = (JSONObject) dataArray.get(i);
                player.Cards.add(new Card(Short.parseShort(obj.get("id").toString()), Byte.parseByte(obj.get("amount").toString()), Byte.parseByte(obj.get("max").toString()), Byte.parseByte(obj.get("level").toString()), loadOptionCard((JSONArray) JSONValue.parse(obj.get("option").toString())), Byte.parseByte(obj.get("used").toString())));
            }
            dataArray.clear();

            // DATA THẺ
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_the_card"));
            player.THE_TUAN = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.LASTTIME_THE_TUAN = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.THE_THANG = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.LASTTIME_THE_THANG = Long.parseLong(String.valueOf(dataArray.get(3)));
            player.THE_NAM = Integer.parseInt(String.valueOf(dataArray.get(4)));
            player.LASTTIME_THE_NAM = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.THE_CHI_TON = Integer.parseInt(String.valueOf(dataArray.get(6)));
            player.LASTTIME_THE_CHI_TON = Long.parseLong(String.valueOf(dataArray.get(7)));
            dataArray.clear();

            // data điểm danh
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_diem_danh"));
            player.CHECK_DAY_ON_ONE = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DIEM_DANH = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.CHECK_DAY_ON_TUAN = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.DIEM_DANH_TUAN = Integer.parseInt(String.valueOf(dataArray.get(3)));
            player.CHECK_DAY_ON_THANG = Integer.parseInt(String.valueOf(dataArray.get(4)));
            player.DIEM_DANH_THANG = Integer.parseInt(String.valueOf(dataArray.get(5)));
            player.CHECK_DAY_ON_NAM = Integer.parseInt(String.valueOf(dataArray.get(6)));
            player.DIEM_DANH_NAM = Integer.parseInt(String.valueOf(dataArray.get(7)));
            player.CHECK_DAY_ON_CHI_TON = Integer.parseInt(String.valueOf(dataArray.get(8)));
            player.DIEM_DANH_CHI_TON = Integer.parseInt(String.valueOf(dataArray.get(9)));
            dataArray.clear();
            try {
                Calendar ngayvps = Calendar.getInstance();
                int ngayhomnay = ngayvps.get(Calendar.DAY_OF_YEAR);
                if (player.CHECK_DAY_ON_ONE == 0) {
                    player.CHECK_DAY_ON_ONE = (ngayhomnay - 1);
                }
                if (ngayhomnay > player.CHECK_DAY_ON_ONE) {
                    player.CHECK_DAY_ON_ONE = ngayhomnay;
                    player.DIEM_DANH = 0;
                }
            } catch (Exception e) {
            }
            try {
                Calendar ngayvps2 = Calendar.getInstance();
                int ngayhomnay2 = ngayvps2.get(Calendar.DAY_OF_YEAR);
                if (player.CHECK_DAY_ON_TUAN == 0) {
                    player.CHECK_DAY_ON_TUAN = (ngayhomnay2 - 1);
                }
                if (ngayhomnay2 > player.CHECK_DAY_ON_TUAN) {
                    player.CHECK_DAY_ON_TUAN = ngayhomnay2;
                    player.DIEM_DANH_TUAN = 0;
                }
            } catch (Exception e) {
            }
            try {
                Calendar ngayvps3 = Calendar.getInstance();
                int ngayhomnay3 = ngayvps3.get(Calendar.DAY_OF_YEAR);
                if (player.CHECK_DAY_ON_THANG == 0) {
                    player.CHECK_DAY_ON_THANG = (ngayhomnay3 - 1);
                }
                if (ngayhomnay3 > player.CHECK_DAY_ON_THANG) {
                    player.CHECK_DAY_ON_THANG = ngayhomnay3;
                    player.DIEM_DANH_THANG = 0;
                }
            } catch (Exception e) {
            }
            try {
                Calendar ngayvps4 = Calendar.getInstance();
                int ngayhomnay4 = ngayvps4.get(Calendar.DAY_OF_YEAR);
                if (player.CHECK_DAY_ON_NAM == 0) {
                    player.CHECK_DAY_ON_NAM = (ngayhomnay4 - 1);
                }
                if (ngayhomnay4 > player.CHECK_DAY_ON_NAM) {
                    player.CHECK_DAY_ON_NAM = ngayhomnay4;
                    player.DIEM_DANH_NAM = 0;
                }
            } catch (Exception e) {
            }
            try {
                Calendar ngayvps5 = Calendar.getInstance();
                int ngayhomnay5 = ngayvps5.get(Calendar.DAY_OF_YEAR);
                if (player.CHECK_DAY_ON_CHI_TON == 0) {
                    player.CHECK_DAY_ON_CHI_TON = (ngayhomnay5 - 1);
                }
                if (ngayhomnay5 > player.CHECK_DAY_ON_CHI_TON) {
                    player.CHECK_DAY_ON_CHI_TON = ngayhomnay5;
                    player.DIEM_DANH_CHI_TON = 0;
                }
            } catch (Exception e) {
            }

            /// DATA Máy Đấm
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_may_dam"));
            player.TopMayDamTraiDat = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.TopMayDamNamec = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.TopMayDamXayda = Long.parseLong(String.valueOf(dataArray.get(2)));
            dataArray.clear();

            // data tọa độ
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_location"));
                int mapId = Integer.parseInt(String.valueOf(dataArray.get(0)));
                player.location.x = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.location.y = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.location.lastTimeplayerMove = System.currentTimeMillis();
                if (mapId == 51 || MapService.gI().isMapPhoBan(mapId) || MapService.gI().isMapBlackBallWar(mapId) || MapService.gI().isMapTayKarin(mapId)
                        || MapService.gI().isMapMaBu12H(mapId) || MapService.gI().isMapMabu14H(mapId)) {
                    mapId = player.gender + 21;
                    player.location.x = 300;
                    player.location.y = 336;
                }
                if (MapService.gI().isMapMaBu12H(mapId)) {
                    if (!TimeUtil.isMabuOpen()) {
                        mapId = player.gender + 21;
                        player.location.x = 300;
                        player.location.y = 336;
                    }
                }
                if (mapId == 112) {
                    player.location.y = 408;
                } else if (mapId == 129 || mapId == 113) {
                    player.location.y = 360;
                }
                if (mapId == 49) {
                    mapId = 45;
                    player.location.x = 359;
                    player.location.y = 408;
                }

                player.zone = MapService.gI().getMapCanJoin(player, mapId, -1);
            } catch (Exception e) {
                Logger.error(e + "\n");
            }
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_models"));
            player.TimeTrongNgay = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.solanhotong = Short.parseShort(String.valueOf(dataArray.get(1)));
            player.NhanThuocTrongNgay = Byte.parseByte(String.valueOf(dataArray.get(2)));
            player.SoNgayTaoAcc = Integer.parseInt(String.valueOf(dataArray.get(3)));
            player.QuocTich = Byte.parseByte(String.valueOf(dataArray.get(4)));
            player.trbgiap = Byte.parseByte(String.valueOf(dataArray.get(5)));
            player.trbne = Byte.parseByte(String.valueOf(dataArray.get(6)));
            player.giaiphongan = Byte.parseByte(String.valueOf(dataArray.get(7)));
            player.PointRank = Byte.parseByte(String.valueOf(dataArray.get(8)));
            dataArray.clear();
            try {
                Calendar ngayvps = Calendar.getInstance();
                int ngayhomnay = ngayvps.get(Calendar.DAY_OF_YEAR);
                if (player.TimeTrongNgay == 0) {
                    player.TimeTrongNgay = (int) (ngayhomnay - 1);
                }
                if (ngayhomnay > player.TimeTrongNgay) {
                    player.TimeTrongNgay = (int) ngayhomnay;
                    player.solanhotong = 0;
                    player.NhanThuocTrongNgay = 0;
                    player.SoNgayTaoAcc += 1;
                    player.PointRank = 0;
                }
            } catch (Exception e) {
            }

            //
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_models"));
            player.CheckTrongNgay = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.NhanLiXiForNPC_1 = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.NhanLiXiForNPC_2 = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.NhanLiXiForNPC_3 = Integer.parseInt(String.valueOf(dataArray.get(3)));
            player.NhanLiXiForNPC_4 = Integer.parseInt(String.valueOf(dataArray.get(4)));
            player.NhanLiXiForNPC_5 = Integer.parseInt(String.valueOf(dataArray.get(5)));
            player.NhanLiXiForNPC_6 = Integer.parseInt(String.valueOf(dataArray.get(6)));
            player.NhanLiXiForNPC_7 = Integer.parseInt(String.valueOf(dataArray.get(7)));
            player.NhanLiXiForNPC_8 = Integer.parseInt(String.valueOf(dataArray.get(8)));
            player.NhanLiXiForNPC_9 = Integer.parseInt(String.valueOf(dataArray.get(9)));
            player.NhanLiXiForNPC_10 = Integer.parseInt(String.valueOf(dataArray.get(10)));
            player.NhanLiXiForNPC_11 = Integer.parseInt(String.valueOf(dataArray.get(11)));
            player.NhanLiXiForNPC_12 = Integer.parseInt(String.valueOf(dataArray.get(12)));
            player.NhanLiXiForNPC_13 = Integer.parseInt(String.valueOf(dataArray.get(13)));
            player.NhanLiXiForNPC_14 = Integer.parseInt(String.valueOf(dataArray.get(14)));
            player.NhanLiXiForNPC_15 = Integer.parseInt(String.valueOf(dataArray.get(15)));
            player.NhanLiXiForNPC_16 = Integer.parseInt(String.valueOf(dataArray.get(16)));
            player.NhanLiXiForNPC_17 = Integer.parseInt(String.valueOf(dataArray.get(17)));
            player.NhanLiXiForNPC_18 = Integer.parseInt(String.valueOf(dataArray.get(18)));
            player.NhanLiXiForNPC_19 = Integer.parseInt(String.valueOf(dataArray.get(19)));
            player.NhanLiXiForNPC_20 = Integer.parseInt(String.valueOf(dataArray.get(20)));
            player.NhanLiXiForNPC_21 = Integer.parseInt(String.valueOf(dataArray.get(21)));
            player.NhanLiXiForNPC_22 = Integer.parseInt(String.valueOf(dataArray.get(22)));
            player.NhanLiXiForNPC_23 = Integer.parseInt(String.valueOf(dataArray.get(23)));
            player.NhanLiXiForNPC_24 = Integer.parseInt(String.valueOf(dataArray.get(24)));
            player.NhanLiXiForNPC_25 = Integer.parseInt(String.valueOf(dataArray.get(25)));
            player.NhanKeoHayBiGheoNpc_1 = Integer.parseInt(String.valueOf(dataArray.get(26)));
            player.NhanKeoHayBiGheoNpc_2 = Integer.parseInt(String.valueOf(dataArray.get(27)));
            player.NhanKeoHayBiGheoNpc_3 = Integer.parseInt(String.valueOf(dataArray.get(28)));
            player.NhanKeoHayBiGheoNpc_4 = Integer.parseInt(String.valueOf(dataArray.get(29)));
            player.NhanKeoHayBiGheoNpc_5 = Integer.parseInt(String.valueOf(dataArray.get(30)));
            player.NhanKeoHayBiGheoNpc_6 = Integer.parseInt(String.valueOf(dataArray.get(31)));
            player.NhanKeoHayBiGheoNpc_7 = Integer.parseInt(String.valueOf(dataArray.get(32)));
            player.NhanKeoHayBiGheoNpc_8 = Integer.parseInt(String.valueOf(dataArray.get(33)));
            player.NhanKeoHayBiGheoNpc_9 = Integer.parseInt(String.valueOf(dataArray.get(34)));
            player.NhanKeoHayBiGheoNpc_10 = Integer.parseInt(String.valueOf(dataArray.get(35)));
            player.NhanKeoHayBiGheoNpc_11 = Integer.parseInt(String.valueOf(dataArray.get(36)));
            player.NhanKeoHayBiGheoNpc_12 = Integer.parseInt(String.valueOf(dataArray.get(37)));
            player.NhanKeoHayBiGheoNpc_13 = Integer.parseInt(String.valueOf(dataArray.get(38)));
            player.NhanKeoHayBiGheoNpc_14 = Integer.parseInt(String.valueOf(dataArray.get(39)));
            player.NhanKeoHayBiGheoNpc_15 = Integer.parseInt(String.valueOf(dataArray.get(40)));
            player.UocMienPhi = Integer.parseInt(String.valueOf(dataArray.get(41)));
            player.NhanQuaHungVuongFree = Integer.parseInt(String.valueOf(dataArray.get(42)));
            player.DiemDanhBangHoi = Integer.parseInt(String.valueOf(dataArray.get(43)));
            dataArray.clear();
            try {
                Calendar ngayvps = Calendar.getInstance();
                int ngayhomnay = ngayvps.get(Calendar.DAY_OF_YEAR);
                if (player.CheckTrongNgay == 0) {
                    player.CheckTrongNgay = (int) (ngayhomnay - 1);
                }
                if (ngayhomnay > player.CheckTrongNgay) {
                    player.CheckTrongNgay = (int) ngayhomnay;
                    player.NhanLiXiForNPC_1 = 0;
                    player.NhanLiXiForNPC_2 = 0;
                    player.NhanLiXiForNPC_3 = 0;
                    player.NhanLiXiForNPC_4 = 0;
                    player.NhanLiXiForNPC_5 = 0;
                    player.NhanLiXiForNPC_6 = 0;
                    player.NhanLiXiForNPC_7 = 0;
                    player.NhanLiXiForNPC_8 = 0;
                    player.NhanLiXiForNPC_9 = 0;
                    player.NhanLiXiForNPC_10 = 0;
                    player.NhanLiXiForNPC_11 = 0;
                    player.NhanLiXiForNPC_12 = 0;
                    player.NhanLiXiForNPC_13 = 0;
                    player.NhanLiXiForNPC_14 = 0;
                    player.NhanLiXiForNPC_15 = 0;
                    player.NhanLiXiForNPC_16 = 0;
                    player.NhanLiXiForNPC_17 = 0;
                    player.NhanLiXiForNPC_18 = 0;
                    player.NhanLiXiForNPC_19 = 0;
                    player.NhanLiXiForNPC_20 = 0;
                    player.NhanLiXiForNPC_21 = 0;
                    player.NhanLiXiForNPC_22 = 0;
                    player.NhanLiXiForNPC_23 = 0;
                    player.NhanLiXiForNPC_24 = 0;
                    player.NhanLiXiForNPC_25 = 0;
                    player.NhanKeoHayBiGheoNpc_1 = 0;
                    player.NhanKeoHayBiGheoNpc_2 = 0;
                    player.NhanKeoHayBiGheoNpc_3 = 0;
                    player.NhanKeoHayBiGheoNpc_4 = 0;
                    player.NhanKeoHayBiGheoNpc_5 = 0;
                    player.NhanKeoHayBiGheoNpc_6 = 0;
                    player.NhanKeoHayBiGheoNpc_7 = 0;
                    player.NhanKeoHayBiGheoNpc_8 = 0;
                    player.NhanKeoHayBiGheoNpc_9 = 0;
                    player.NhanKeoHayBiGheoNpc_10 = 0;
                    player.NhanKeoHayBiGheoNpc_11 = 0;
                    player.NhanKeoHayBiGheoNpc_12 = 0;
                    player.NhanKeoHayBiGheoNpc_13 = 0;
                    player.NhanKeoHayBiGheoNpc_14 = 0;
                    player.NhanKeoHayBiGheoNpc_15 = 0;
                    player.UocMienPhi = 0;
                    player.NhanQuaHungVuongFree = 0;
                    player.DiemDanhBangHoi = 0;
                }
            } catch (Exception e) {
                Logger.logException(GodGK.class, e);
            }

            //check band
            dataArray = (JSONArray) JSONValue.parse(rs.getString("check_band_account"));
            player.ErrorMap = Byte.parseByte(String.valueOf(dataArray.get(0)));
            player.ErrorLocation = Byte.parseByte(String.valueOf(dataArray.get(1)));
            player.ErrorPay = Byte.parseByte(String.valueOf(dataArray.get(2)));
            dataArray.clear();

            // data chỉ số
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_point"));
            player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
            player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
            player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
            player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
            player.nPoint.hpg = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.nPoint.mpg = Long.parseLong(String.valueOf(dataArray.get(6)));
            player.nPoint.dameg = Long.parseLong(String.valueOf(dataArray.get(7)));
            player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
            player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
            dataArray.get(10); //** Năng động
            plHp = Long.parseLong(String.valueOf(dataArray.get(11)));
            plMp = Long.parseLong(String.valueOf(dataArray.get(12)));
            plDame = Long.parseLong(String.valueOf(dataArray.get(13)));
            plMaxHP = Long.parseLong(String.valueOf(dataArray.get(14)));
            plMaxKI = Long.parseLong(String.valueOf(dataArray.get(15)));
            dataArray.clear();

            // data đậu thần
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_magic_tree"));
            byte level = Byte.parseByte(String.valueOf(dataArray.get(0)));
            byte currPea = Byte.parseByte(String.valueOf(dataArray.get(1)));
            boolean isUpgrade = Byte.parseByte(String.valueOf(dataArray.get(2))) == 1;
            long lastTimeHarvest = Long.parseLong(String.valueOf(dataArray.get(3)));
            long lastTimeUpgrade = Long.parseLong(String.valueOf(dataArray.get(4)));
            player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
            dataArray.clear();

            // data phần thưởng sao đen
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_black_ball"));
            JSONArray dataBlackBall;
            for (int i = 0; i < dataArray.size(); i++) {
                dataBlackBall = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(0)));
                player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(1)));
                try {
                    player.rewardBlackBall.quantilyBlackBall[i] = dataBlackBall.get(2) != null ? Integer.parseInt(String.valueOf(dataBlackBall.get(2))) : 0;
                } catch (NumberFormatException e) {
                    player.rewardBlackBall.quantilyBlackBall[i] = player.rewardBlackBall.timeOutOfDateReward[i] != 0 ? 1 : 0;
                }
                dataBlackBall.clear();
            }
            dataArray.clear();

            //data body
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    if ((item.template.id >= 386 && item.template.id <= 394) || item.template.id == 1464 || item.template.id == 533) {
                        if (!EventManager.CHRISTMAS) {
                            System.out.println("Thu há»“i váº­t pháº©m sá»± kiá»‡n noel.");
                            item = ItemService.gI().createItemNull();
                        }
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBody.add(item);
            }
            if (player.inventory.itemsBody.size() < ConstPlayer.QTY_MAX_ITEM_BODY_PLAYER) {
                player.inventory.itemsBody.add(ItemService.gI().createItemNull());
            }
            dataArray.clear();

            //data bag
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_bag"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    if ((item.template.id >= 386 && item.template.id <= 394) || item.template.id == 1464 || item.template.id == 533) {
                        if (!EventManager.CHRISTMAS) {
                            System.out.println("Thu há»“i váº­t pháº©m sá»± kiá»‡n noel.");
                            item = ItemService.gI().createItemNull();
                        }
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBag.add(item);
            }
            dataArray.clear();

            //data box
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    if ((item.template.id >= 386 && item.template.id <= 394) || item.template.id == 1464 || item.template.id == 533) {
                        if (!EventManager.CHRISTMAS) {
                            System.out.println("Thu há»“i váº­t pháº©m sá»± kiá»‡n noel.");
                            item = ItemService.gI().createItemNull();
                        }
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBox.add(item);
            }
            dataArray.clear();

            //data box
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box_Collection"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if (ItemService.gI().isOutOfDateTime(item)) {
                        item = ItemService.gI().createItemNull();
                    }
                } else {
                    item = ItemService.gI().createItemNull();
                }
                player.inventory.itemsBoxCollection.add(item);
            }
            dataArray.clear();

            //data box hòm thư
            dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))), Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    player.inventory.itemsMailBox.add(item);
                }
            }
            dataArray.clear();

            //data box lucky round
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box_lucky_round"));
            for (int i = 0; i < dataArray.size(); i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    player.inventory.itemsBoxCrackBall.add(item);
                }
            }
            dataArray.clear();

            //data friends
            dataArray = (JSONArray) JSONValue.parse(rs.getString("friends"));
            if (dataArray != null) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray dataFE = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    Friend friend = new Friend();
                    friend.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                    friend.name = String.valueOf(dataFE.get(1));
                    friend.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                    friend.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                    friend.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                    friend.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                    friend.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                    player.friends.add(friend);
                    dataFE.clear();
                }
                dataArray.clear();
            }

            //data enemies
            dataArray = (JSONArray) JSONValue.parse(rs.getString("enemies"));
            if (dataArray != null) {
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray dataFE = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    Enemy enemy = new Enemy();
                    enemy.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                    enemy.name = String.valueOf(dataFE.get(1));
                    enemy.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                    enemy.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                    enemy.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                    enemy.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                    enemy.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                    player.enemies.add(enemy);
                    dataFE.clear();
                }
                dataArray.clear();
            }

            //data nội tại
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_intrinsic"));
            byte intrinsicId = Byte.parseByte(String.valueOf(dataArray.get(0)));
            player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
            player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataArray.get(1)));
            player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataArray.get(2)));
            player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataArray.get(3)));
            if (dataArray.size() > 4) {
                try {
                    player.effectSkill.isIntrinsic = Boolean.parseBoolean(String.valueOf(dataArray.get(4)));
                    player.effectSkill.skillID = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.effectSkill.cooldown = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    player.effectSkill.lastTimeUseSkill = Long.parseLong(String.valueOf(dataArray.get(7)));
                } catch (NumberFormatException e) {
                }
            }
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_new_year"));
            player.DuaTopMoLiXi = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopTangLiXi = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.DuaTopBanPhaoHoa = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.DuaTopBanPhaoHoaVIP = Integer.parseInt(String.valueOf(dataArray.get(3)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_christ_mas"));
            player.DuaTopTrangTriCayNoel = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopCheTaoNguoiTuyet = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.DuaTopCheTaoNguoiTuyetBangGia = Integer.parseInt(String.valueOf(dataArray.get(2)));
            player.DuaTopDotDiem = Integer.parseInt(String.valueOf(dataArray.get(3)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_vulan"));
            player.DuaTopPhaoHoaVuLan = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopHoaDang = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.DuaTopHoaDangCoLoiChuc = Integer.parseInt(String.valueOf(dataArray.get(2)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_halloween"));
            player.DuaTopMoHopMaQuy = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopThiepHalloween = Integer.parseInt(String.valueOf(dataArray.get(1)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_8_3"));
            player.DuaTopMoThiep83 = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopTangBongHoaHong = Integer.parseInt(String.valueOf(dataArray.get(1)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_trung_thu"));
            player.DuaTopLamBanhTrungThu = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopMoHopTrungThuDacBiet = Integer.parseInt(String.valueOf(dataArray.get(1)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_hung_vuong"));
            player.DuaTopMoTrungRongVang = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.typeBanhDangNau = Integer.parseInt(String.valueOf(dataArray.get(1)));
            player.lastTimeNauBanhHungVuong = Long.parseLong(String.valueOf(dataArray.get(2)));
            player.DuaTopMoHopQuaGioTo = Integer.parseInt(String.valueOf(dataArray.get(3)));
            player.DuaTopDangBanhHungVuong = Integer.parseInt(String.valueOf(dataArray.get(4)));
            player.DuaTopDoiDuaHau = Integer.parseInt(String.valueOf(dataArray.get(5)));
            dataArray.clear();

            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_event_black_friday"));
            player.DuaTopMoHopBlackFriday = Integer.parseInt(String.valueOf(dataArray.get(0)));
            player.DuaTopMuaSamBlackFriday = Integer.parseInt(String.valueOf(dataArray.get(1)));
            dataArray.clear();

            //Data bảo vệ tài khoản
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("baovetaikhoan"));
                player.mbv = Integer.parseInt(dataArray.get(0).toString());
                player.baovetaikhoan = Boolean.parseBoolean(dataArray.get(1).toString());
                player.mbvtime = Long.parseLong(dataArray.get(2).toString());
            } catch (Exception e) {
                player.mbv = 0;
                player.baovetaikhoan = false;
                player.mbvtime = System.currentTimeMillis();
            }

            // Thời gian gọi rồng
            player.lastTimeShenronAppeared_Christmas = rs.getLong("dragon_christmas");

            // Thời gian gọi rồng
            player.lastTimeShenronAppeared_Halloween = rs.getLong("dragon_halloween");

            //data item time
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_item_time"));
            int timeHongDao1 = 0;
            int timeHongDao3 = 0;
            int timeHongDao5 = 0;
            int timeHongDao10 = 0;
            int timeHongDao25 = 0;
            int timeHongDao50 = 0;
            int timeHongDao99 = 0;
            int timeHongDao999 = 0;
            int timecommenson = 0;
            int timekhautrang = 0;
            int timekeohacam = 0;
            int timebihacam = 0;
            int timebanhgato = 0;
            int timehamburger = 0;
            int timetmthuong = 0;
            int timetmdacbiet = 0;
            int timecuarangme = 0;
            int timebachtuocnuong = 0;
            int timetomtambot = 0;
            int bocpha = 0;
            int gtpt = 0;
            int Duoikhitnsm = 0;
            int trbsd = 0;
            int trbhp = 0;
            int trbki = 0;
            int trbsdxd = 0;
            int trbhpxd = 0;
            int trbkixd = 0;
            int timeBoHuyet2 = 0;
            int timeBoKhi2 = 0;
            int timeGiapXen2 = 0;
            int timeCuongNo2 = 0;
            int timeAnDanh2 = 0;
            int timeUseNCD = 0;
            int timeUseBanhTet = 0;
            int timeUseBanhTrung = 0;
            int timeUseFoodMeoDen1 = 0;
            int timeUseFoodMeoDen2 = 0;
            int timeUseChiMang2 = 0;
            int timeUseChiMang3 = 0;
            int timeUseNeDon = 0;
            int timeUseNeDon2 = 0;
            int timeUsePhansatthuong = 0;
            int timeUsePhansatthuong2 = 0;
            int timeUsePhansatthuong3 = 0;
            int timeUseKamejoko = 0;
            int timeUseKamejoko2 = 0;
            int timeUserocket1h = 0;
            int timeUseSatThuongChuan = 0;
            int timeUseSatThuongChuan2 = 0;
            int timeUseBuatnsm = 0;
            int timeUseCoBonLa = 0;
            int timeUseSauRieng = 0;
            int timeUseMaydolinhhon = 0;
            int timeUseMaydongocbi = 0;
            int timeUseRongXuong = 0;
            int timeUseRongXuong2 = 0;
            int timeUseRongXuong3 = 0;
            int timeUseBanhDeo1 = 0;
            int timeUseBanhDeo2 = 0;
            int timeUseBanhDeo3 = 0;
            int timeUseTrung1 = 0;
            int timeUseTrung2 = 0;
            int timeUseTrungDB = 0;
            int timeUseTrungHB = 0;
            int timeUseMaydoSieuHoa = 0;
            //------------------------------------------------------------------
            int timeBoHuyet = Integer.parseInt(String.valueOf(dataArray.get(0)));
            int timeBoKhi = Integer.parseInt(String.valueOf(dataArray.get(1)));
            int timeGiapXen = Integer.parseInt(String.valueOf(dataArray.get(2)));
            int timeCuongNo = Integer.parseInt(String.valueOf(dataArray.get(3)));
            int timeAnDanh = Integer.parseInt(String.valueOf(dataArray.get(4)));
            int timeOpenPower = Integer.parseInt(String.valueOf(dataArray.get(5)));
            int timeMayDo = Integer.parseInt(String.valueOf(dataArray.get(6)));
            int timeMeal = Integer.parseInt(String.valueOf(dataArray.get(7)));
            int iconMeal = Integer.parseInt(String.valueOf(dataArray.get(8)));
            int timeUseTDLT = Integer.parseInt(String.valueOf(dataArray.get(9)));
            if (dataArray.size() > 10) {
                timeHongDao1 = Integer.parseInt(String.valueOf(dataArray.get(10)));
            }
            if (dataArray.size() > 11) {
                timeHongDao3 = Integer.parseInt(String.valueOf(dataArray.get(11)));
            }
            if (dataArray.size() > 12) {
                timeHongDao5 = Integer.parseInt(String.valueOf(dataArray.get(12)));
            }
            if (dataArray.size() > 13) {
                timeHongDao10 = Integer.parseInt(String.valueOf(dataArray.get(13)));
            }
            if (dataArray.size() > 14) {
                timeHongDao25 = Integer.parseInt(String.valueOf(dataArray.get(14)));
            }
            if (dataArray.size() > 15) {
                timeHongDao50 = Integer.parseInt(String.valueOf(dataArray.get(15)));
            }
            if (dataArray.size() > 16) {
                timeHongDao99 = Integer.parseInt(String.valueOf(dataArray.get(16)));
            }
            if (dataArray.size() > 17) {
                timeHongDao999 = Integer.parseInt(String.valueOf(dataArray.get(17)));
            }
            if (dataArray.size() > 18) {
                timecommenson = Integer.parseInt(String.valueOf(dataArray.get(18)));
            }
            if (dataArray.size() > 19) {
                timekhautrang = Integer.parseInt(String.valueOf(dataArray.get(19)));
            }
            if (dataArray.size() > 20) {
                timekeohacam = Integer.parseInt(String.valueOf(dataArray.get(20)));
            }
            if (dataArray.size() > 21) {
                timebihacam = Integer.parseInt(String.valueOf(dataArray.get(21)));
            }
            if (dataArray.size() > 22) {
                timebanhgato = Integer.parseInt(String.valueOf(dataArray.get(22)));
            }
            if (dataArray.size() > 23) {
                timehamburger = Integer.parseInt(String.valueOf(dataArray.get(23)));
            }
            if (dataArray.size() > 24) {
                timetmthuong = Integer.parseInt(String.valueOf(dataArray.get(24)));
            }
            if (dataArray.size() > 25) {
                timetmdacbiet = Integer.parseInt(String.valueOf(dataArray.get(25)));
            }
            if (dataArray.size() > 26) {
                timecuarangme = Integer.parseInt(String.valueOf(dataArray.get(26)));
            }
            if (dataArray.size() > 27) {
                timebachtuocnuong = Integer.parseInt(String.valueOf(dataArray.get(27)));
            }
            if (dataArray.size() > 28) {
                timetomtambot = Integer.parseInt(String.valueOf(dataArray.get(28)));
            }
            if (dataArray.size() > 29) {
                bocpha = Integer.parseInt(String.valueOf(dataArray.get(29)));
            }
            if (dataArray.size() > 30) {
                gtpt = Integer.parseInt(String.valueOf(dataArray.get(30)));
            }
            if (dataArray.size() > 31) {
                Duoikhitnsm = Integer.parseInt(String.valueOf(dataArray.get(31)));
            }
            if (dataArray.size() > 32) {
                trbsd = Integer.parseInt(String.valueOf(dataArray.get(32)));
            }
            if (dataArray.size() > 33) {
                trbhp = Integer.parseInt(String.valueOf(dataArray.get(33)));
            }
            if (dataArray.size() > 34) {
                trbki = Integer.parseInt(String.valueOf(dataArray.get(34)));
            }
            if (dataArray.size() > 35) {
                trbsdxd = Integer.parseInt(String.valueOf(dataArray.get(35)));
            }
            if (dataArray.size() > 36) {
                trbhpxd = Integer.parseInt(String.valueOf(dataArray.get(36)));
            }
            if (dataArray.size() > 37) {
                trbkixd = Integer.parseInt(String.valueOf(dataArray.get(37)));
            }
            if (dataArray.size() > 38) {
                timeBoHuyet2 = Integer.parseInt(String.valueOf(dataArray.get(38)));
            }
            if (dataArray.size() > 39) {
                timeBoKhi2 = Integer.parseInt(String.valueOf(dataArray.get(39)));
            }
            if (dataArray.size() > 40) {
                timeGiapXen2 = Integer.parseInt(String.valueOf(dataArray.get(40)));
            }
            if (dataArray.size() > 41) {
                timeCuongNo2 = Integer.parseInt(String.valueOf(dataArray.get(41)));
            }
            if (dataArray.size() > 42) {
                timeAnDanh2 = Integer.parseInt(String.valueOf(dataArray.get(42)));
            }
            if (dataArray.size() > 43) {
                timeUseNCD = Integer.parseInt(String.valueOf(dataArray.get(43)));
            }
            if (dataArray.size() > 44) {
                timeUseBanhTet = Integer.parseInt(String.valueOf(dataArray.get(44)));
            }
            if (dataArray.size() > 45) {
                timeUseBanhTrung = Integer.parseInt(String.valueOf(dataArray.get(45)));
            }
            if (dataArray.size() > 46) {
                timeUseFoodMeoDen1 = Integer.parseInt(String.valueOf(dataArray.get(46)));
            }
            if (dataArray.size() > 47) {
                timeUseFoodMeoDen2 = Integer.parseInt(String.valueOf(dataArray.get(47)));
            }
            if (dataArray.size() > 48) {
                timeUseChiMang2 = Integer.parseInt(String.valueOf(dataArray.get(48)));
            }
            if (dataArray.size() > 49) {
                timeUseChiMang3 = Integer.parseInt(String.valueOf(dataArray.get(49)));
            }
            if (dataArray.size() > 50) {
                timeUseNeDon = Integer.parseInt(String.valueOf(dataArray.get(50)));
            }
            if (dataArray.size() > 51) {
                timeUseNeDon2 = Integer.parseInt(String.valueOf(dataArray.get(51)));
            }
            if (dataArray.size() > 52) {
                timeUsePhansatthuong = Integer.parseInt(String.valueOf(dataArray.get(52)));
            }
            if (dataArray.size() > 53) {
                timeUsePhansatthuong2 = Integer.parseInt(String.valueOf(dataArray.get(53)));
            }
            if (dataArray.size() > 54) {
                timeUsePhansatthuong3 = Integer.parseInt(String.valueOf(dataArray.get(54)));
            }
            if (dataArray.size() > 55) {
                timeUseKamejoko = Integer.parseInt(String.valueOf(dataArray.get(55)));
            }
            if (dataArray.size() > 56) {
                timeUseKamejoko2 = Integer.parseInt(String.valueOf(dataArray.get(56)));
            }
            if (dataArray.size() > 57) {
                timeUserocket1h = Integer.parseInt(String.valueOf(dataArray.get(57)));
            }
            if (dataArray.size() > 58) {
                timeUseSatThuongChuan = Integer.parseInt(String.valueOf(dataArray.get(58)));
            }
            if (dataArray.size() > 59) {
                timeUseSatThuongChuan2 = Integer.parseInt(String.valueOf(dataArray.get(59)));
            }
            if (dataArray.size() > 60) {
                timeUseBuatnsm = Integer.parseInt(String.valueOf(dataArray.get(60)));
            }
            if (dataArray.size() > 61) {
                timeUseCoBonLa = Integer.parseInt(String.valueOf(dataArray.get(61)));
            }
            if (dataArray.size() > 62) {
                timeUseSauRieng = Integer.parseInt(String.valueOf(dataArray.get(62)));
            }
            if (dataArray.size() > 63) {
                timeUseMaydolinhhon = Integer.parseInt(String.valueOf(dataArray.get(63)));
            }
            if (dataArray.size() > 64) {
                timeUseMaydongocbi = Integer.parseInt(String.valueOf(dataArray.get(64)));
            }
            if (dataArray.size() > 65) {
                timeUseRongXuong = Integer.parseInt(String.valueOf(dataArray.get(65)));
            }
            if (dataArray.size() > 66) {
                timeUseRongXuong2 = Integer.parseInt(String.valueOf(dataArray.get(66)));
            }
            if (dataArray.size() > 67) {
                timeUseRongXuong3 = Integer.parseInt(String.valueOf(dataArray.get(67)));
            }
            if (dataArray.size() > 68) {
                timeUseBanhDeo1 = Integer.parseInt(String.valueOf(dataArray.get(68)));
            }
            if (dataArray.size() > 69) {
                timeUseBanhDeo2 = Integer.parseInt(String.valueOf(dataArray.get(69)));
            }
            if (dataArray.size() > 70) {
                timeUseBanhDeo3 = Integer.parseInt(String.valueOf(dataArray.get(70)));
            }
            if (dataArray.size() > 71) {
                timeUseTrung1 = Integer.parseInt(String.valueOf(dataArray.get(71)));
            }
            if (dataArray.size() > 72) {
                timeUseTrung2 = Integer.parseInt(String.valueOf(dataArray.get(72)));
            }
            if (dataArray.size() > 73) {
                timeUseTrungDB = Integer.parseInt(String.valueOf(dataArray.get(73)));
            }
            if (dataArray.size() > 74) {
                timeUseTrungHB = Integer.parseInt(String.valueOf(dataArray.get(74)));
            }
            if (dataArray.size() > 75) {
                timeUseMaydoSieuHoa = Integer.parseInt(String.valueOf(dataArray.get(75)));
            }
            //------------------------------------------------------------------
            player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeBoHuyet);
            player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeBoKhi);
            player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeGiapXen);
            player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeCuongNo);
            player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeAnDanh);
            player.itemTime.lastTimeOpenPower = System.currentTimeMillis() - (ItemTime.TIME_OPEN_POWER - timeOpenPower);
            player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
            player.itemTime.lastTimeEatMeal = System.currentTimeMillis() - (ItemTime.TIME_EAT_MEAL - timeMeal);
            player.itemTime.lastTimeHongDao1 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeHongDao1);
            player.itemTime.lastTimeHongDao3 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeHongDao3);
            player.itemTime.lastTimeHongDao5 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_1M - timeHongDao5);
            player.itemTime.lastTimeHongDao10 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_1M - timeHongDao10);
            player.itemTime.lastTimeHongDao25 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_1M - timeHongDao25);
            player.itemTime.lastTimeHongDao50 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeHongDao50);
            player.itemTime.lastTimeHongDao99 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeHongDao99);
            player.itemTime.lastTimeHongDao999 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeHongDao999);
            player.itemTime.lastcommenson = System.currentTimeMillis() - (ItemTime.TIME_ITEM_50M - timecommenson);
            player.itemTime.LastKhauTrang = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timekhautrang);
            player.itemTime.LastTimeKeoMotMat = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timekeohacam);
            player.itemTime.LastTimeSupbihacam = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timebihacam);
            player.itemTime.LastTimebanhgatonhen = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timebanhgato);
            player.itemTime.LastTimehamburgersau = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timehamburger);
            player.itemTime.timeTDLT = timeUseTDLT * 60 * 1000;
            player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();
            player.itemTime.iconMeal = iconMeal;
            player.itemTime.LastTimethuocmothuong = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timetmthuong);
            player.itemTime.LastTimethuocmodacbiet = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timetmdacbiet);
            player.itemTime.lasttimecuarangme = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timecuarangme);
            player.itemTime.lasttimebachtuocnuong = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timebachtuocnuong);
            player.itemTime.lasttimetomtambot = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timetomtambot);
            player.itemTime.TimeBocPha = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - bocpha);
            player.itemTime.lastTimeUseGTPT = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - gtpt);
            player.itemTime.lastTimeUseDuoiKhiTNSM = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - Duoikhitnsm);
            player.itemTime.lastTimetrbsd = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - trbsd);
            player.itemTime.lastTimetrbhp = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - trbhp);
            player.itemTime.lastTimetrbki = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - trbki);
            player.itemTime.lastTimetrbsdxd = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - trbsdxd);
            player.itemTime.lastTimetrbhpxd = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - trbhpxd);
            player.itemTime.lastTimetrbkixd = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - trbkixd);
            player.itemTime.lastTimeBoHuyet2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeBoHuyet2);
            player.itemTime.lastTimeBoKhi2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeBoKhi2);
            player.itemTime.lastTimeGiapXen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeGiapXen2);
            player.itemTime.lastTimeCuongNo2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeCuongNo2);
            player.itemTime.lastTimeAnDanh2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeAnDanh2);
            player.itemTime.lastTimeUseNCD = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseNCD);
            player.itemTime.lastTimeUseBanhTet = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseBanhTet);
            player.itemTime.lastTimeUseBanhTrung = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseBanhTrung);
            player.itemTime.lastTimeUseFoodMeoDen1 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_3M - timeUseFoodMeoDen1);
            player.itemTime.lastTimeUseFoodMeoDen2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_15M - timeUseFoodMeoDen2);
            player.itemTime.lastTimeUseChiMang2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseChiMang2);
            player.itemTime.lastTimeUseChiMang3 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseChiMang3);
            player.itemTime.lastTimeUseNedon = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUseNeDon);
            player.itemTime.lastTimeUseNedon2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUseNeDon2);
            player.itemTime.lastTimeUsePhanSatThuong = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUsePhansatthuong);
            player.itemTime.lastTimeUsePhanSatThuong2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUsePhansatthuong2);
            player.itemTime.lastTimeUsePhanSatThuong3 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUsePhansatthuong3);
            player.itemTime.lastTimeUseKamejoko = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUseKamejoko);
            player.itemTime.lastTimeUseKamejoko2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30S - timeUseKamejoko2);
            player.itemTime.lastTimeUseRocket1h = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUserocket1h);
            player.itemTime.lastTimeUseSatThuongChuan = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseSatThuongChuan);
            player.itemTime.lastTimeUseSatThuongChuan2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseSatThuongChuan2);
            player.itemTime.lastTimeUseBuaTNSMDetu = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseBuatnsm);
            player.itemTime.lastTimeUseCoBonLa = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseCoBonLa);
            player.itemTime.lastTimeUseSauRieng = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseSauRieng);
            player.itemTime.lastTimeUseMayDoLinhHon = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseMaydolinhhon);
            player.itemTime.lastTimeUseMayDoNgocBi = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseMaydongocbi);
            player.itemTime.LastTimeRongXuong = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseRongXuong);
            player.itemTime.LastTimeRongXuong_2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseRongXuong2);
            player.itemTime.LastTimeRongXuong_3 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_30M - timeUseRongXuong3);
            player.itemTime.lastTimeUseBanhDeoC1 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseBanhDeo1);
            player.itemTime.lastTimeUseBanhDeoC2 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_10M - timeUseBanhDeo2);
            player.itemTime.lastTimeUseBanhDeoC3 = System.currentTimeMillis() - (ItemTime.TIME_ITEM_20M - timeUseBanhDeo3);
            player.itemTime.lastTimeUseTrungThu1Trung = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseTrung1);
            player.itemTime.lastTimeUseTrungThu2Trung = System.currentTimeMillis() - (ItemTime.TIME_ITEM_90M - timeUseTrung2);
            player.itemTime.lastTimeUseTrungThuDB = System.currentTimeMillis() - (ItemTime.TIME_ITEM_120M - timeUseTrungDB);
            player.itemTime.lastTimeUseHBTrungThu = System.currentTimeMillis() - (ItemTime.TIME_ITEM_150M - timeUseTrungHB);
            player.itemTime.lastTimeUseMayDoSieuHoa = System.currentTimeMillis() - (ItemTime.TIME_ITEM_60M - timeUseMaydoSieuHoa);
            //------------------------------------------------------------------
            player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
            player.itemTime.isUseBoKhi = timeBoKhi != 0;
            player.itemTime.isUseGiapXen = timeGiapXen != 0;
            player.itemTime.isUseCuongNo = timeCuongNo != 0;
            player.itemTime.isUseAnDanh = timeAnDanh != 0;
            player.itemTime.isUseBoHuyet2 = timeBoHuyet != 0;
            player.itemTime.isUseBoKhi2 = timeBoKhi != 0;
            player.itemTime.isUseGiapXen2 = timeGiapXen != 0;
            player.itemTime.isUseCuongNo2 = timeCuongNo != 0;
            player.itemTime.isUseAnDanh2 = timeAnDanh != 0;
            player.itemTime.isOpenPower = timeOpenPower != 0;
            player.itemTime.isUseMayDo = timeMayDo != 0;
            player.itemTime.isEatMeal = timeMeal != 0;
            player.itemTime.isUseTDLT = timeUseTDLT != 0;
            player.itemTime.isUseHongDao1 = timeHongDao1 != 0;
            player.itemTime.isUseHongDao3 = timeHongDao3 != 0;
            player.itemTime.isUseHongDao5 = timeHongDao5 != 0;
            player.itemTime.isUseHongDao10 = timeHongDao10 != 0;
            player.itemTime.isUseHongDao25 = timeHongDao25 != 0;
            player.itemTime.isUseHongDao50 = timeHongDao50 != 0;
            player.itemTime.isUseHongDao99 = timeHongDao99 != 0;
            player.itemTime.isUseHongDao999 = timeHongDao999 != 0;
            player.itemTime.iscommenson = timecommenson != 0;
            player.itemTime.IsKhauTrang = timekhautrang != 0;
            player.itemTime.IsKeoMotMat = timekeohacam != 0;
            player.itemTime.IsSupbihacam = timebihacam != 0;
            player.itemTime.Isbanhgatonhen = timebanhgato != 0;
            player.itemTime.Ishamburgersau = timehamburger != 0;
            player.itemTime.Isthuocmothuong = timetmthuong != 0;
            player.itemTime.Isthuocmodacbiet = timetmdacbiet != 0;
            player.itemTime.iscuarangme = timecuarangme != 0;
            player.itemTime.istomtambot = timetomtambot != 0;
            player.itemTime.isbachtuocnuong = timebachtuocnuong != 0;
            player.itemTime.IsBocPha = bocpha != 0;
            player.itemTime.isUseGTPT = gtpt != 0;
            player.itemTime.isUseDuoiKhiTNSM = Duoikhitnsm != 0;
            player.itemTime.istrbsd = trbsd != 0;
            player.itemTime.istrbhp = trbhp != 0;
            player.itemTime.istrbki = trbki != 0;
            player.itemTime.istrbsdxd = trbsdxd != 0;
            player.itemTime.istrbhpxd = trbhpxd != 0;
            player.itemTime.istrbkixd = trbkixd != 0;
            player.itemTime.isUseBoHuyet2 = timeBoHuyet2 != 0;
            player.itemTime.isUseBoKhi2 = timeBoKhi2 != 0;
            player.itemTime.isUseGiapXen2 = timeGiapXen2 != 0;
            player.itemTime.isUseCuongNo2 = timeCuongNo2 != 0;
            player.itemTime.isUseAnDanh2 = timeAnDanh2 != 0;
            player.itemTime.isUseNCD = timeUseNCD != 0;
            player.itemTime.isUseBanhTet = timeUseBanhTet != 0;
            player.itemTime.isUseBanhTrung = timeUseBanhTrung != 0;
            player.itemTime.isUseFoodMeoDen1 = timeUseFoodMeoDen1 != 0;
            player.itemTime.isUseFoodMeoDen2 = timeUseFoodMeoDen2 != 0;
            player.itemTime.isUseChiMang2 = timeUseChiMang2 != 0;
            player.itemTime.isUseChiMang3 = timeUseChiMang3 != 0;
            player.itemTime.isUseNedon = timeUseNeDon != 0;
            player.itemTime.isUseNedon2 = timeUseNeDon2 != 0;
            player.itemTime.isUsePhanSatThuong = timeUsePhansatthuong != 0;
            player.itemTime.isUsePhanSatThuong2 = timeUsePhansatthuong2 != 0;
            player.itemTime.isUsePhanSatThuong3 = timeUsePhansatthuong3 != 0;
            player.itemTime.isUseKamejoko = timeUseKamejoko != 0;
            player.itemTime.isUseKamejoko2 = timeUseKamejoko2 != 0;
            player.itemTime.isUseRocket1h = timeUserocket1h != 0;
            player.itemTime.isUseSatThuongChuan = timeUseSatThuongChuan != 0;
            player.itemTime.isUseSatThuongChuan2 = timeUseSatThuongChuan2 != 0;
            player.itemTime.isUseBuaTNSMDetu = timeUseBuatnsm != 0;
            player.itemTime.isUseCoBonLa = timeUseCoBonLa != 0;
            player.itemTime.isUseSauRieng = timeUseSauRieng != 0;
            player.itemTime.isUseMayDoLinhHon = timeUseMaydolinhhon != 0;
            player.itemTime.isUseMayDoNgocBi = timeUseMaydongocbi != 0;
            player.itemTime.isRongXuong = timeUseRongXuong != 0;
            player.itemTime.isRongXuong_2 = timeUseRongXuong2 != 0;
            player.itemTime.isRongXuong_3 = timeUseRongXuong3 != 0;
            player.itemTime.isUseBanhDeoC1 = timeUseBanhDeo1 != 0;
            player.itemTime.isUseBanhDeoC2 = timeUseBanhDeo2 != 0;
            player.itemTime.isUseBanhDeoC3 = timeUseBanhDeo3 != 0;
            player.itemTime.isUseTrungThu1Trung = timeUseTrung1 != 0;
            player.itemTime.isUseTrungThu2Trung = timeUseTrung2 != 0;
            player.itemTime.isUseTrungThuDB = timeUseTrungDB != 0;
            player.itemTime.isUseHBTrungThu = timeUseTrungHB != 0;
            player.itemTime.isUseMayDoSieuHoa = timeUseMaydoSieuHoa != 0;
            dataArray.clear();

            //data siêu thánh thủy
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("sieuthanthuy"));
                player.winSTT = Boolean.parseBoolean(dataArray.get(0).toString());
                player.lastTimeWinSTT = Long.parseLong(dataArray.get(1).toString());
                player.callBossPocolo = Boolean.parseBoolean(dataArray.get(2).toString());
            } catch (Exception e) {
            }

            //data nhiệm vụ
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_task"));
            TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataArray.get(0))));
            taskMain.index = Byte.parseByte(String.valueOf(dataArray.get(1)));
            taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataArray.get(2)));
            if (dataArray.size() > 3) {
                taskMain.lastTime = Long.parseLong(String.valueOf(dataArray.get(3)));
            } else {
                taskMain.lastTime = System.currentTimeMillis();
            }
            player.playerTask.taskMain = taskMain;
            dataArray.clear();

            //data nhiệm vụ hằng ngày
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_side_task"));
            String format = "dd-MM-yyyy";
            long receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
            Date date = new Date(receivedTime);
            if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.playerTask.sideTask.receivedTime = receivedTime;
            }

            //data nhiệm vụ bang hàng ngày
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_clan_task"));
                format = "dd-MM-yyyy";
                receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
                date = new Date(receivedTime);
                if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                    player.playerTask.clanTask.template = TaskService.gI().getClanTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                    player.playerTask.clanTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.playerTask.clanTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    player.playerTask.clanTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    player.playerTask.clanTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.playerTask.clanTask.receivedTime = receivedTime;
                }
            } catch (Exception e) {
            }

            //data luyện tập
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_luyentap"));
                player.levelLuyenTap = Integer.parseInt(dataArray.get(0).toString());
                player.dangKyTapTuDong = Boolean.parseBoolean(dataArray.get(1).toString());
                player.mapIdDangTapTuDong = Integer.parseInt(dataArray.get(2).toString());
                player.tnsmLuyenTap = Integer.parseInt(dataArray.get(3).toString());
                player.lastTimeOffline = Long.parseLong(dataArray.get(4).toString());
                if (dataArray.size() > 5) {
                    player.traning.setTop(Integer.parseInt(dataArray.get(5).toString()));
                    player.traning.setTime(Integer.parseInt(dataArray.get(6).toString()));
                    player.traning.setLastTime(Long.parseLong(dataArray.get(7).toString()));
                    player.traning.setLastTop(Integer.parseInt(dataArray.get(8).toString()));
                    player.traning.setLastRewardTime(Long.parseLong(dataArray.get(9).toString()));
                }
            } catch (Exception e) {
                player.levelLuyenTap = 0;
                player.dangKyTapTuDong = false;
                player.mapIdDangTapTuDong = -1;
                player.tnsmLuyenTap = 0;
                player.lastTimeOffline = System.currentTimeMillis();
            }

            //data item event
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_item_event"));
                player.itemEvent.remainingTVGSCount = Integer.parseInt(dataArray.get(0).toString());
                player.itemEvent.lastTVGSTime = Long.parseLong(dataArray.get(1).toString());
                player.itemEvent.remainingHHCount = Integer.parseInt(dataArray.get(2).toString());
                player.itemEvent.lastHHTime = Long.parseLong(dataArray.get(3).toString());
                player.itemEvent.remainingBNCount = Integer.parseInt(dataArray.get(4).toString());
                player.itemEvent.lastBNTime = Long.parseLong(dataArray.get(5).toString());
                player.itemEvent.remainingKeoGiangSinhCount = Integer.parseInt(dataArray.get(6).toString());
                player.itemEvent.lastKeoGiangSinhTime = Long.parseLong(dataArray.get(7).toString());
            } catch (Exception e) {
                player.itemEvent.remainingTVGSCount = 0;
                player.itemEvent.lastTVGSTime = 0;
                player.itemEvent.remainingHHCount = 0;
                player.itemEvent.lastHHTime = 0;
                player.itemEvent.remainingBNCount = 0;
                player.itemEvent.lastBNTime = 0;
                player.itemEvent.remainingKeoGiangSinhCount = 0;
                player.itemEvent.lastKeoGiangSinhTime = 0;
            }

            //data trứng bư
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_mabu_egg"));
            if (dataArray.size() != 0) {
                player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                        Long.parseLong(String.valueOf(dataArray.get(1))));
            }
            dataArray.clear();

            //data trứng bill
            dataArray = (JSONArray) JSONValue.parse(rs.getString("bill_data"));
            if (dataArray.size() != 0) {
                player.billEgg = new BillEgg(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                        Long.parseLong(String.valueOf(dataArray.get(1))));
            }
            dataArray.clear();

            //data dưa hấu
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_trong_dua_hau"));
            if (dataArray.size() != 0) {
                player.duahau = new MelonPlant(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                        Long.parseLong(String.valueOf(dataArray.get(1))));
            }
            dataArray.clear();

            //data danh hiệu
            dataArray = (JSONArray) JSONValue.parse(rs.getString("danh_hieu"));
            player.isUseDanhHieu_ThienTu = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
            player.LastTimeDanhHieu_ThienTu = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.isUseDanhHieu_2 = Integer.parseInt(String.valueOf(dataArray.get(2))) == 1 ? true : false;
            player.LastTimeDanhHieu_2 = Long.parseLong(String.valueOf(dataArray.get(3)));
            player.isUseDanhHieu_3 = Integer.parseInt(String.valueOf(dataArray.get(4))) == 1 ? true : false;
            player.LastTimeDanhHieu_3 = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.isUseDanhHieu_4 = Integer.parseInt(String.valueOf(dataArray.get(6))) == 1 ? true : false;
            player.LastTimeDanhHieu_4 = Long.parseLong(String.valueOf(dataArray.get(7)));
            dataArray.clear();

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("LearnSkill"));
                player.LearnSkill.Time = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.LearnSkill.ItemTemplateSkillId = Short.parseShort(String.valueOf(dataArray.get(1)));
                player.LearnSkill.Potential = Integer.parseInt(String.valueOf(dataArray.get(2)));
            } catch (Exception e) {
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("BoughtSkill"));
                for (Object idSkill : dataArray) {
                    player.BoughtSkill.add(((Long) idSkill).intValue());
                }
                dataArray.clear();
            } catch (Exception e) {
                Logger.log(e.toString());
            }

            //notify
            player.notify = rs.getString("notify");

            //Data BDKB
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("bandokhobau"));
                player.timesPerDayBDKB = Integer.parseInt(dataArray.get(0).toString());
                player.lastTimeJoinBDKB = Long.parseLong(dataArray.get(1).toString());
            } catch (Exception e) {
                player.timesPerDayBDKB = 0;
                player.lastTimeJoinBDKB = System.currentTimeMillis();
            }

            //Data doanh trạii
            player.lastTimeJoinDT = rs.getLong("doanhtrai");

            //Data CDRD
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("conduongrandoc"));
                player.joinCDRD = Boolean.parseBoolean(dataArray.get(0).toString());
                player.lastTimeJoinCDRD = Long.parseLong(dataArray.get(1).toString());
                player.talkToThuongDe = Boolean.parseBoolean(dataArray.get(2).toString());
                player.talkToThanMeo = Boolean.parseBoolean(dataArray.get(2).toString());
                if (player.clan.ConDuongRanDoc == null || player.lastTimeJoinCDRD != player.clan.lastTimeOpenConDuongRanDoc) {
                    player.joinCDRD = false;
                    player.talkToThuongDe = false;
                    player.talkToThanMeo = false;
                }
            } catch (Exception e) {
                player.joinCDRD = false;
                player.lastTimeJoinCDRD = 0;
                player.talkToThuongDe = false;
                player.talkToThanMeo = false;
            }

            //data nhiệmn vụ kol
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_kol_task"));
            player.playerTask.kolTask.template = TaskService.gI().getKolTaskTemplateById(Integer.parseInt(dataArray.get(0).toString()));
            player.playerTask.kolTask.count = Integer.parseInt(dataArray.get(1).toString());

            //data nhiệm vụ event
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_side_task"));
            String formatt = "dd-MM-yyyy";
            long receivedTimee = Long.parseLong(String.valueOf(dataArray.get(1)));
            Date datee = new Date(receivedTimee);
            if (TimeUtil.formatTime(datee, formatt).equals(TimeUtil.formatTime(new Date(), formatt))) {
                player.playerTask.eventTask.template = TaskService.gI().getEventTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                player.playerTask.eventTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.playerTask.eventTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                player.playerTask.eventTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                player.playerTask.eventTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.playerTask.eventTask.receivedTime = receivedTimee;
            }
            dataArray.clear();

            //data bùa
            dataArray = (JSONArray) JSONValue.parse(rs.getString("data_charm"));
            player.charms.tdTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
            player.charms.tdManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
            player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
            player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataArray.get(3)));
            player.charms.tdBatTu = Long.parseLong(String.valueOf(dataArray.get(4)));
            player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataArray.get(5)));
            player.charms.tdThuHut = Long.parseLong(String.valueOf(dataArray.get(6)));
            player.charms.tdDeTu = Long.parseLong(String.valueOf(dataArray.get(7)));
            player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataArray.get(8)));
            player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataArray.get(9)));
            //
            player.charms.tdDeTu2 = Long.parseLong(String.valueOf(dataArray.get(10)));
            player.charms.tdDeTu3 = Long.parseLong(String.valueOf(dataArray.get(11)));
            player.charms.tdDeTu4 = Long.parseLong(String.valueOf(dataArray.get(12)));
            player.charms.tdDeTu5 = Long.parseLong(String.valueOf(dataArray.get(13)));
            player.charms.tdDeTu7 = Long.parseLong(String.valueOf(dataArray.get(14)));
            player.charms.tdDeTu10 = Long.parseLong(String.valueOf(dataArray.get(15)));
            player.charms.tdDeTu20 = Long.parseLong(String.valueOf(dataArray.get(16)));
            player.charms.tdTriTue5 = Long.parseLong(String.valueOf(dataArray.get(17)));
            player.charms.tdTriTue7 = Long.parseLong(String.valueOf(dataArray.get(18)));
            player.charms.tdTriTue10 = Long.parseLong(String.valueOf(dataArray.get(19)));
            player.charms.tdTriTue20 = Long.parseLong(String.valueOf(dataArray.get(20)));
            dataArray.clear();

            //data vip
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_vip"));
                player.timesPerDayCuuSat = Integer.parseInt(String.valueOf(dataArray.get(0)));
                player.lastTimeCuuSat = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.nhanDeTuNangVIP = Boolean.parseBoolean(String.valueOf(dataArray.get(2)));
                player.nhanVangNangVIP = Boolean.parseBoolean(String.valueOf(dataArray.get(3)));
                if (dataArray.size() > 4) {
                    player.nhanSKHVIP = Boolean.parseBoolean(String.valueOf(dataArray.get(4)));
                }
            } catch (Exception e) {
            }

            //data skill
            int[] skillsArr = player.gender == 0 ? ConstPlayer.SKILL_TD
                    : player.gender == 1 ? ConstPlayer.SKILL_NAMEC
                            : ConstPlayer.SKILL_XAYDA;
            dataArray = (JSONArray) JSONValue.parse(rs.getString("skills"));
            for (int i = 0; i < dataArray.size(); i++) {
                JSONArray dataSkill = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
                byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
                Skill skill;
                if (point != 0) {
                    skill = SkillUtil.createSkill(tempId, point);
                } else {
                    skill = SkillUtil.createSkillLevel0(tempId);
                }
                skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
                if (dataSkill.size() > 3) {
                    skill.currLevel = Short.parseShort(String.valueOf(dataSkill.get(3)));
                }
                player.playerSkill.skills.add(skill);
            }
            boolean hasSkill;
            for (int i = 0; i < skillsArr.length; i++) {
                hasSkill = false;
                for (Skill skillPl : player.playerSkill.skills) {
                    if (skillPl.template.id == skillsArr[i]) {
                        hasSkill = true;
                        break;
                    }
                }
                if (!hasSkill) {
                    Skill skill = SkillUtil.createSkillLevel0(skillsArr[i]);
                    player.playerSkill.skills.add(skill);
                }
            }
            dataArray.clear();

            //data skill shortcut
            dataArray = (JSONArray) JSONValue.parse(rs.getString("skills_shortcut"));
            for (int i = 0; i < dataArray.size(); i++) {
                player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
            }

            for (int i : player.playerSkill.skillShortCut) {
                if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                    break;
                }
            }
            if (player.playerSkill.skillSelect == null) {
                player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                        ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
            }
            dataArray.clear();

            //data pet
            JSONArray petData = (JSONArray) JSONValue.parse(rs.getString("pet"));
            if (!petData.isEmpty()) {
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(0)));
                Detu pet = new Detu(player);
                pet.id = -player.id;
                pet.typeDeTu = Byte.parseByte(String.valueOf(dataArray.get(0)));
                pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                pet.name = String.valueOf(dataArray.get(2));
                player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                player.fusion.lastTimeFusion = System.currentTimeMillis()
                        - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));
                try {

                } catch (Exception e) {
                }
                //data chỉ số
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(1)));
                pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                pet.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                pet.nPoint.hpg = Long.parseLong(String.valueOf(dataArray.get(5)));
                pet.nPoint.mpg = Long.parseLong(String.valueOf(dataArray.get(6)));
                pet.nPoint.dameg = Long.parseLong(String.valueOf(dataArray.get(7)));
                pet.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                long hp = Long.parseLong(String.valueOf(dataArray.get(10)));
                long mp = Long.parseLong(String.valueOf(dataArray.get(11)));
                long dame = Long.parseLong(String.valueOf(dataArray.get(12)));

                //data body
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(2)));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    pet.inventory.itemsBody.add(item);
                }
                //data skills
                dataArray = (JSONArray) JSONValue.parse(String.valueOf(petData.get(3)));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray skillTemp = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(i)));
                    int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                    byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                    Skill skill;
                    if (point != 0) {
                        skill = SkillUtil.createSkill(tempId, point);
                    } else {
                        skill = SkillUtil.createSkillLevel0(tempId);
                    }
                    if (skillTemp.size() > 3) {
                        skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(skillTemp.get(2)));
                    }
                    if (skillTemp.size() > 3) {
                        skill.currLevel = Short.parseShort(String.valueOf(skillTemp.get(3)));
                    }
                    switch (skill.template.id) {
                        case Skill.KAMEJOKO:
                        case Skill.MASENKO:
                        case Skill.ANTOMIC:
                            skill.coolDown = 1000;
                            break;
                    }
                    pet.playerSkill.skills.add(skill);
                }
                pet.nPoint.hp = hp;
                pet.nPoint.mp = mp;
                pet.nPoint.dame = dame;
                player.Detu = pet;
            }

            //data super rank
            player.superRank.rank = Integer.parseInt(rs.getString("rank"));
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_super_rank"));
                player.superRank.lastTimePK = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.superRank.lastTimeReward = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.superRank.ticket = Integer.parseInt(String.valueOf(dataArray.get(2)));
                player.superRank.win = Integer.parseInt(String.valueOf(dataArray.get(3)));
                player.superRank.lose = Integer.parseInt(String.valueOf(dataArray.get(4)));
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(String.valueOf(dataArray.get(5)), JsonObject.class);
                JsonArray history = jsonObject.getAsJsonArray("history");
                JsonArray lasttime = jsonObject.getAsJsonArray("lasttime");

                for (int i = 0; i < history.size(); i++) {
                    player.superRank.addHistory(history.get(i).getAsString(), lasttime.get(i).getAsLong());
                }
            } catch (Exception e) {
            }
            //data super rank         
            if (Util.isAfterMidnight(player.superRank.lastTimePK)) {
                if (player.superRank.ticket < 3) {
                    player.superRank.ticket++;
                }
                player.superRank.lastTimePK = System.currentTimeMillis();
            }

            try {
                player.firstTimeLogin = rs.getTimestamp("firstTimeLogin");
            } catch (Exception e) {
                player.firstTimeLogin = new Date(0); // hoáº·c Instant.EPOCH
            }

            try {
                player.LastTimeLoginGame = rs.getTimestamp("LastTimeLoginGame");
            } catch (Exception e) {
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dataBadges"));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    int idBadges = Integer.parseInt(obj.get("idBadGes").toString());
                    long timeOfUseBadges = Long.parseLong(obj.get("timeofUseBadges").toString());
                    boolean isUse = Boolean.parseBoolean(String.valueOf(obj.get("isUse")));
                    player.dataBadges.add(new BadgesData(idBadges, timeOfUseBadges, isUse));
                }
                dataArray.clear();
            } catch (Exception ex) {
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dataTaskBadges"));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONObject obj = (JSONObject) dataArray.get(i);
                    BadgesTask data = new BadgesTask();
                    data.id = Integer.parseInt(obj.get("id").toString());
                    data.count = Integer.parseInt(obj.get("count").toString());
                    data.countMax = Integer.parseInt(obj.get("countMax").toString());
                    data.idBadgesReward = Integer.parseInt(obj.get("idBadgesReward").toString());
                    player.dataTaskBadges.add(data);
                }
                dataArray.clear();
            } catch (Exception ex) {
                BadgesTaskService.createAndResetTask(player);
            }

            //data võ đài sinh tử­
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("vodaisinhtu"));
                player.haveRewardVDST = Boolean.parseBoolean(dataArray.get(0).toString());
                player.thoiVangVoDaiSinhTu = Integer.parseInt(dataArray.get(1).toString());
                player.lastTimePKVoDaiSinhTu = Long.parseLong(dataArray.get(2).toString());
                player.timePKVDST = Long.parseLong(dataArray.get(3).toString());
            } catch (Exception e) {
            }

            //data rương gỗ
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("ruonggo"));
                player.levelWoodChest = Integer.parseInt(dataArray.get(0).toString());
                player.goldChallenge = Long.parseLong(dataArray.get(1).toString());
                player.rubyChallenge = Long.parseLong(dataArray.get(2).toString());
                player.lastTimeRewardWoodChest = Long.parseLong(dataArray.get(3).toString());
                player.lastTimePKDHVT23 = Long.parseLong(dataArray.get(4).toString());
            } catch (Exception e) {
                player.levelWoodChest = 0;
                player.goldChallenge = 50000000;
                player.rubyChallenge = 100;
                player.lastTimeRewardWoodChest = System.currentTimeMillis();
                player.lastTimePKDHVT23 = 0;
            }

            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("dailyGift"));
                if (dataArray.size() < 2) {
                    DailyGiftService.addAndReset(player);
                } else {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONObject obj = (JSONObject) dataArray.get(i);
                        DailyGiftData data = new DailyGiftData();
                        data.id = Byte.parseByte(obj.get("id").toString());
                        data.daNhan = Boolean.parseBoolean(obj.get("daNhan").toString());
                        player.dailyGiftData.add(data);
                    }
                }
                dataArray.clear();
            } catch (Exception ex) {
                DailyGiftService.addAndReset(player);
            }
            PlayerService.gI().dailyLogin(player);// RESET DATA KHI QUA 12H ÄÃŠM

            //data PK Commeson
            player.lastPkCommesonTime = rs.getLong("lasttimepkcommeson");

            //data item da ban
            dataArray = (JSONArray) JSONValue.parse(rs.getString("items_daban"));
            for (int i = 0; i < dataArray.size() && i < 20; i++) {
                Item item;
                JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                if (tempId != -1) {
                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                    JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                    for (int j = 0; j < options.size(); j++) {
                        JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                        item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                Integer.parseInt(String.valueOf(opt.get(1)))));
                    }
                    // 26/06/2023 - Giáº£m NgÃ y Trong Shop
                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                    if ((item.template.id >= 386 && item.template.id <= 394) || item.template.id == 1464 || item.template.id == 533) {
                        if (!EventManager.CHRISTMAS) {
                            System.out.println("Thu há»“i váº­t pháº©m sá»± kiá»‡n noel.");
                            item = ItemService.gI().createItemNull();
                        }
                    }
                    if (!ItemService.gI().isOutOfDateTime(item)) {
                        player.inventory.itemsDaBan.add(item);
                    }
                }
            }
            dataArray.clear();

            // Sư phụ không tấn công
            try {
                player.doesNotAttack = rs.getBoolean("masterDoesAttack");
                player.lastTimePlayerNotAttack = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttack = false;
                player.lastTimePlayerNotAttack = System.currentTimeMillis();
            }
            //----------------------GIA ĐÌNH------------------------------------
            try {
                player.doesNotAttackBo = rs.getBoolean("masterDoesAttackBo");
                player.lastTimePlayerNotAttackBo = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackBo = false;
                player.lastTimePlayerNotAttackBo = System.currentTimeMillis();
            }
            try {
                player.doesNotAttackMe = rs.getBoolean("masterDoesAttackMe");
                player.lastTimePlayerNotAttackMe = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackMe = false;
                player.lastTimePlayerNotAttackMe = System.currentTimeMillis();
            }
            try {
                player.doesNotAttackNguoiYeu = rs.getBoolean("NguoiYeuDoesAttack");
                player.lastTimePlayerNotAttackNguoiYeu = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackNguoiYeu = false;
                player.lastTimePlayerNotAttackNguoiYeu = System.currentTimeMillis();
            }
            try {
                player.doesNotAttackConone = rs.getBoolean("CononeDoesAttack");
                player.lastTimePlayerNotAttackConone = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackConone = false;
                player.lastTimePlayerNotAttackConone = System.currentTimeMillis();
            }
            try {
                player.doesNotAttackContwo = rs.getBoolean("ContwoDoesAttack");
                player.lastTimePlayerNotAttackContwo = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackContwo = false;
                player.lastTimePlayerNotAttackContwo = System.currentTimeMillis();
            }
            try {
                player.doesNotAttackConthree = rs.getBoolean("ConthreeDoesAttack");
                player.lastTimePlayerNotAttackConthree = System.currentTimeMillis();
            } catch (Exception e) {
                player.doesNotAttackConthree = false;
                player.lastTimePlayerNotAttackConthree = System.currentTimeMillis();
            }
            //------------------------------------------------------------------

            //data achievement
            try {
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_achievement"));
                for (int i = 0; i < Manager.ACHIEVEMENT_TEMPLATE.size(); i++) {
                    AchievementQuest aq;
                    if (i < dataArray.size()) {
                        JSONArray data = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                        aq = new AchievementQuest(Long.parseLong(data.get(0).toString()), Boolean.parseBoolean(data.get(1).toString()));
                    } else {
                        aq = new AchievementQuest(0, false);
                    }
                    player.achievement.add(aq);
                }
                dataArray.clear();
            } catch (Exception e) {
            }

            player.nPoint.hp = plHp;
            player.nPoint.mp = plMp;
            player.nPoint.dame = plDame;
            player.nPoint.hpMax = plMaxHP;
            player.nPoint.mpMax = plMaxKI;
            player.iDMark.setLoadedAllDataPlayer(true);
        } catch (Exception e) {
            if (player != null) {
                player.dispose();
                player = null;
            }
            throw e;
        }
        return player;
    }

    public static List<Player> getAllPlayer() {
        try {
            List<Player> players = new ArrayList<>();
            CrisResultSet rs = null;
            try {
                Player player = new Player();
                rs = ConnectDB.executeQuery("select * from player");
                while (rs.next()) {
                    long plHp = 200000000;
                    long plMp = 200000000;
                    JSONArray dataArray;

                    player = new Player();

                    //base info
                    player.id = rs.getInt("id");
                    player.name = rs.getString("name");
                    player.head = rs.getShort("head");
                    player.gender = rs.getByte("gender");
                    player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                    //data box hòm thư
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item;
                        JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                            JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        player.inventory.itemsMailBox.add(item);
                    }
                    dataArray.clear();
                    player.nPoint.hp = plHp;
                    player.nPoint.mp = plMp;
                    player.iDMark.setLoadedAllDataPlayer(true);
                    players.add(player);

                }

            } catch (Exception e) {
                Logger.logException(GodGK.class, e);
            } finally {
                if (rs != null) {
                    rs.dispose();
                }
            }

            return players;
        } catch (Exception e) {
            Logger.logException(GodGK.class, e);
            return null;
        }
    }

    public static Player loadPlayerByName(String name) {
        Player player = null;
        CrisResultSet rs = null;
        try {
            player = Client.gI().getPlayerByName(name);
            if (player != null) {
                return player;
            }
            rs = ConnectDB.executeQuery("select * from player where name = ? limit 1", name);
            if (rs.first()) {
                long plHp = 200000000;
                long plMp = 200000000;
                JSONArray dataArray;

                player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsMailBox.add(item);
                }
                dataArray.clear();
                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                player.iDMark.setLoadedAllDataPlayer(true);
            }

        } catch (Exception e) {
            Logger.logException(GodGK.class, e);
            player.dispose();
            player = null;
            Logger.logException(GodGK.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static Player loadById(long id) {
        Player player = null;
        CrisResultSet rs = null;
        try {

            rs = ConnectDB.executeQuery("select * from player where id = ? limit 1", id);
            if (rs.next() && (player = loadPlayer(rs, true)) != null) {
                player.isOffline = true;
                player.iDMark.setLoadedAllDataPlayer(true);
            }
        } catch (Exception e) {
            if (player != null) {
                player.dispose();
                player = null;
            }
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static Player loadPlayerByID(long id) {
        Player player = null;
        CrisResultSet rs = null;
        try {
            player = Client.gI().getPlayerByID(id);
            if (player != null) {
                return player;
            }
            rs = ConnectDB.executeQuery("select * from player where id = ? limit 1", id);
            if (rs.first()) {
                long plHp = 200000000;
                long plMp = 200000000;
                JSONArray dataArray;

                player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("item_mails_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsMailBox.add(item);
                }
                dataArray.clear();
                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                player.isOffline = true;
                player.iDMark.setLoadedAllDataPlayer(true);
            }
        } catch (Exception e) {
            player.dispose();
            player = null;
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static boolean updateMailBox(Player player) {
        try {
            JSONArray dataArray = new JSONArray();
            JSONArray dataItem = new JSONArray();
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
            String itemsBox = dataArray.toJSONString();
            dataArray.clear();
            try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement("update `player` set item_mails_box = ? where id = ?")) {
                ps.setString(1, itemsBox);
                ps.setLong(2, player.id);
                ps.executeUpdate();
                ps.close();
                con.close();
                return true;
            } catch (SQLException e) {
                Logger.logException(PlayerDAO.class, e, "Error updating MailsBox for player: " + player.name);
            }
        } catch (Exception e) {
            Logger.logException(GodGK.class, e);
            return false;
        }
        return false;
    }

    public static List<OptionCard> loadOptionCard(JSONArray json) {
        List<OptionCard> ops = new ArrayList<>();
        try {
            for (Object o : json) {
                JSONObject ob = (JSONObject) o;
                if (ob != null) {
                    ops.add(new OptionCard(Integer.parseInt(ob.get("id").toString()), Integer.parseInt(ob.get("param").toString()), Byte.parseByte(ob.get("active").toString())));
                }
            }
        } catch (NumberFormatException e) {
        }
        return ops;
    }
}
