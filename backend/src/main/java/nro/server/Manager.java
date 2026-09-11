package nro.server;

import nro.card.OptionCard;
import nro.card.RadarCard;
import nro.card.RadarService;
import consts.ConstPlayer;
import consts.ConstMap;
import Data.DataGame;
import static Data.DataGame.MAP_MOUNT_NUM;
import jbcd.dao.ShopDAO;
import nro.clan.Clan;
import nro.clan.ClanMember;
import nro.intrinsic.Intrinsic;
import nro.player.TestDame;
import models.Item.Item;
import nro.map.WayPoint;
import nro.npc.Npc;
import nro.npc.NpcFactory;
import nro.shop.Shop;
import nro.skill.NClass;
import nro.skill.Skill;
import nro.task.SideTaskTemplate;
import nro.task.SubTaskMain;
import nro.task.TaskMain;
import nro.services.MapService;
import Utils.Logger;
import jbcd.ConnectDB;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import models.Item.ItemOption;
import nro.attribute.Attribute;
import nro.attribute.AttributeManager;
import nro.attribute.AttributeTemplate;
import nro.attribute.AttributeTemplateManager;
import nro.badges.BadgesTaskTemplate;
import nro.badges.BagesTemplate;
import nro.consignmentstore.ConsignItem;
import nro.consignmentstore.ConsignShopManager;
import nro.clan.ClanTaskTemplate;
import nro.map.EffectMap;
import nro.map.Zone;
import nro.npc.NonInteractiveNPC;
import nro.player.Player;
import nro.power.CaptionManager;
import nro.power.PowerLimitManager;
import nro.tambao.TamBaoService;
import nro.task.EventTaskTemplate;
import nro.task.KolTaskTemplate;
import nro.template.AchievementTemplate;
import nro.template.ArrHead2Frames;
import nro.template.BgItem;
import nro.template.FlagBag;
import nro.template.HeadAvatar;
import nro.template.ItemOptionTemplate;
import nro.template.ItemTemplate;
import nro.template.MapTemplate;
import nro.template.MobTemplate;
import nro.template.NpcTemplate;
import nro.template.Part;
import nro.template.PartDetail;
import nro.template.SkillTemplate;
import nro.vongquaymayman.VongQuayMayManService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public final class Manager {

    private static Manager i;
    
    public static Manager gI() {
        if (i == null) {
            i = new Manager();
        }
        return i;
    }
   
    public static boolean readInt = true;
    public static long LAST_TIME_UPDATE_BXH;
    public static boolean HAVE_EFFECT_NIGHT_SKY = false; 
    public static byte SERVER = 1;
    public static byte SECOND_WAIT_LOGIN = 10;
    public static int MAX_PER_IP = 3;
    public static int MAX_PLAYER = 1000;
    public static int RATE_EXP_SERVER = 10;
    public static boolean LOCAL = false;
    public static boolean TEST = false;
    public static boolean DAO_AUTO_UPDATER = false;
    public static int TY_LE_NAP_THOI_VANG = 1;
    public static int TY_LE_NAP_HONG_NGOC = 1;
    // Update //
//    public static byte TNDETU = 1;// tn đệ
    public static byte TNDETU = 2;// tn đệ
    
    public static byte TNPET = 4; // tn đệ
    //ĐUA TOP//
    public static Timestamp timeSuKienDuaTop = Timestamp.valueOf("2026-03-18 20:00:00");

    public static String timeStartDuaTop = "20 giờ ngày 18/3/2026";

    public static String timeEndDuaTop = "17h00 ngày 21/3/2026";

    public static String timeEndNhanGiai = "22/3/2026";
    
    public static Player player;
    
    public static byte KHUYEN_MAI_NAP = 1;
    
    public static boolean Jake_DEBUG = false;
    
    // *** //
    public static MapTemplate[] MAP_TEMPLATES;
    public static final List<nro.map.Map> MAPS = new ArrayList<>();
    public static final List<ItemOptionTemplate> ITEM_OPTION_TEMPLATES = new ArrayList<>();
    public static final Map<String, Byte> IMAGES_BY_NAME = new HashMap<>();
    public static final List<AchievementTemplate> ACHIEVEMENT_TEMPLATE = new ArrayList<>();
    public static final List<ItemTemplate> ITEM_TEMPLATES = new ArrayList<>();
    public static final List<MobTemplate> MOB_TEMPLATES = new ArrayList<>();
    public static final List<NpcTemplate> NPC_TEMPLATES = new ArrayList<>();
    public static final List<TaskMain> TASKS = new ArrayList<>();
    public static final List<SideTaskTemplate> SIDE_TASKS_TEMPLATE = new ArrayList<>();
    public static final List<ClanTaskTemplate> CLAN_TASKS_TEMPLATE = new ArrayList<>();
    public static final List<Intrinsic> INTRINSICS = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_TD = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_NM = new ArrayList<>();
    public static final List<Intrinsic> INTRINSIC_XD = new ArrayList<>();
    public static final List<HeadAvatar> HEAD_AVATARS = new ArrayList<>();
    public static final List<ArrHead2Frames> ARR_HEAD_2_FRAMES = new ArrayList<>();
    public static final List<FlagBag> FLAGS_BAGS = new ArrayList<>();
    public static final List<NClass> NCLASS = new ArrayList<>();
    public static final List<Npc> NPCS = new ArrayList<>();
    public static List<Shop> SHOPS = new ArrayList<>();
    public static final List<Clan> CLANS = new ArrayList<>();
    public static final List<String> NOTIFY = new ArrayList<>();
    public static final List<Item> HONGNGOC_REWARDS = new ArrayList<>();
    public static final List<Item> RUBY_REWARDS = new ArrayList<>();
    public static final List<BgItem> BG_ITEMS = new ArrayList<>();
    public static final List<BadgesTaskTemplate> TASKS_BADGES_TEMPLATE = new ArrayList<>();
    public static final List<BagesTemplate> BAGES_TEMPLATES = new ArrayList<>();
    public static final RandomCollection<Integer> ITEM_VONG_QUAY = new RandomCollection<>();
    public static final List<KolTaskTemplate> KOL_TASKS_TEMPLATE = new ArrayList<>();
    public static final List<EventTaskTemplate> EVENT_TASKS_TEMPLATE = new ArrayList<>();
    
    public static final short[][] TrangBiKichHoat = {{0, 6, 21, 27}, {1, 7, 22, 28}, {2, 8, 23, 29}};
    public static final short[][] TrangBiKichHoatVip = {{555, 556, 562, 563}, {557, 558, 564, 565}, {559, 560, 566, 567}};
    
    public static final short[] itemIds_TL = {561};
    public static final short[] itemIds_HD = {650, 652, 654, 651, 653, 655, 657, 659, 661, 658, 660, 662, 656};
    public static final byte[] itemIds_NR_SB = {14, 15, 16};
    public static final int[] itemIds_MANH_SKH = {1394, 1395, 1396, 1397, 1398};
    public static final short[] itemDC12 = {233, 237, 241, 245, 249, 253, 257, 261, 265, 269, 273, 277};

    public static final short[] aotd = {138, 139, 230, 231, 232, 233, 555};
    public static final short[] quantd = {142, 143, 242, 243, 244, 245, 556};
    public static final short[] gangtd = {146, 147, 254, 255, 256, 257, 562};
    public static final short[] giaytd = {150, 151, 266, 267, 268, 269, 563};
    public static final short[] aoxd = {170, 171, 238, 239, 240, 241, 559};
    public static final short[] quanxd = {174, 175, 250, 251, 252, 253, 560};
    public static final short[] gangxd = {178, 179, 262, 263, 264, 265, 566};
    public static final short[] giayxd = {182, 183, 274, 275, 276, 277, 567};
    public static final short[] aonm = {154, 155, 234, 235, 236, 237, 557};
    public static final short[] quannm = {158, 159, 246, 247, 248, 249, 558};
    public static final short[] gangnm = {162, 163, 258, 259, 260, 261, 564};
    public static final short[] giaynm = {166, 167, 270, 271, 272, 273, 565};
    public static final short[] radaSKHVip = {186, 187, 278, 279, 280, 281, 561};
    
    public static final short[][][] doSKHVip = {{aotd, quantd, gangtd, giaytd}, {aonm, quannm, gangnm, giaynm}, {aoxd, quanxd, gangxd, giayxd}};
    public static final List<AchievementTemplate> ACHIEVEMENTS = new ArrayList<>();
    
    private final List<AttributeTemplate> listattr = new ArrayList<>();
    public void add(AttributeTemplate at) {
        listattr.add(at);
    }
    
  private Manager() {
    try {
        loadProperties();
    } catch (IOException ex) {
        Logger.logException(Manager.class, ex, "Lỗi load properites");
        System.exit(0);
    }

    Logger.title("KHỞI TẠO SERVER");

    AttributeTemplateManager.getInstance().load();
    PowerLimitManager.getInstance().load();
    CaptionManager.getInstance().load();

    loadAttributeServer();
    this.loadDatabase();

    NpcFactory.createNpcConMeo();
    NpcFactory.createNpcRongThieng();

    this.initMap();
    initRandomItem();

    TamBaoService.loadItem();
    VongQuayMayManService.gI().loadItem();

    Logger.connect("Finish connect Server | db=" + ConnectDB.DB_NAME);
}
    
    private void initRandomItem() {
        ITEM_VONG_QUAY.add(1, 0);
        ITEM_VONG_QUAY.add(1, 1);
        ITEM_VONG_QUAY.add(1, 2);
        ITEM_VONG_QUAY.add(1, 3);
        ITEM_VONG_QUAY.add(1, 4);
        ITEM_VONG_QUAY.add(1, 5);
        ITEM_VONG_QUAY.add(1, 6);        
    }

//    private void initMap() {
//        int[][] tileTyleTop = readTileIndexTileType(ConstMap.TILE_TOP);
//        for (MapTemplate mapTemp : MAP_TEMPLATES) {
//            int[][] tileMap = readTileMap(mapTemp.id);
//            int[] tileTop = tileTyleTop[mapTemp.tileId - 1];
//            nro.map.Map map = new nro.map.Map(mapTemp.id, mapTemp.name, mapTemp.planetId, mapTemp.tileId, mapTemp.bgId, mapTemp.bgType, mapTemp.type, tileMap, tileTop, mapTemp.zones,
//                    mapTemp.maxPlayerPerZone, mapTemp.wayPoints, mapTemp.effectMaps, mapTemp.genderType);
//            MAPS.add(map);
//            map.initMob(mapTemp.mobTemp, mapTemp.mobLevel, mapTemp.mobHp, mapTemp.mobX, mapTemp.mobY);
//            map.initNpc(mapTemp.npcId, mapTemp.npcX, mapTemp.npcY);
//            new Thread(map, "Update map" + map.mapName).start();
//        }
////        new Thread (()-> { //giảm thread scr
////        try {
////            while (!Maintenance.isRunning) {
////                long st = System.currentTimeMillis();
////                for (nro.map.Map map : MAPS) {
////                    for (Zone zone : map.zones) {
////                        try {
////                            zone.update();
////                        } catch (Exception e) {
////                        }
////                    }
////                }
////                long timeDo = System.currentTimeMillis() - st;
////                if (1000-timeDo > 0) {
////                    Thread.sleep(1000 - timeDo);
////                }
////            }
////        } catch (InterruptedException ex) {
////        }
////        },"Update maps").start();
//        new NonInteractiveNPC().initNonInteractiveNPC();
//        TestDame testdame = new TestDame();
//        testdame.initTestDame();
//
//    }
    private void initMap() {
    int[][] tileTyleTop = readTileIndexTileType(ConstMap.TILE_TOP);

    for (MapTemplate mapTemp : MAP_TEMPLATES) {
        int[][] tileMap = readTileMap(mapTemp.id);
        int[] tileTop = tileTyleTop[mapTemp.tileId - 1];

        nro.map.Map map = new nro.map.Map(
                mapTemp.id,
                mapTemp.name,
                mapTemp.planetId,
                mapTemp.tileId,
                mapTemp.bgId,
                mapTemp.bgType,
                mapTemp.type,
                tileMap,
                tileTop,
                mapTemp.zones,
                mapTemp.maxPlayerPerZone,
                mapTemp.wayPoints,
                mapTemp.effectMaps,
                mapTemp.genderType
        );

        MAPS.add(map);
        map.initMob(mapTemp.mobTemp, mapTemp.mobLevel, mapTemp.mobHp, mapTemp.mobX, mapTemp.mobY);
        map.initNpc(mapTemp.npcId, mapTemp.npcX, mapTemp.npcY);
    }

    new Thread(() -> {
        try {
            while (!Maintenance.isRunning) {
                long st = System.currentTimeMillis();

                for (nro.map.Map map : MAPS) {
                    if (map == null || map.zones == null) {
                        continue;
                    }

                    for (Zone zone : map.zones) {
                        if (zone == null) {
                            continue;
                        }
                        try {
                            zone.update();
                        } catch (Exception e) {
                        }
                    }
                }

                long elapsed = System.currentTimeMillis() - st;
                long sleep = 1000 - elapsed;
                if (sleep < 10) {
                    sleep = 10;
                }
                Thread.sleep(sleep);
            }
        } catch (InterruptedException e) {
        }
    }, "Update maps").start();

    new NonInteractiveNPC().initNonInteractiveNPC();

    TestDame testdame = new TestDame();
    testdame.initTestDame();
}

    public static MapTemplate getMapTemplate(int mapID) {
        for (MapTemplate map : MAP_TEMPLATES) {
            if (map.id == mapID) {
                return map;
            }
        }
        return null;
    }
    
    private void loadDatabase() {
        long st = System.currentTimeMillis();
        JSONArray dataArray;
        JSONObject dataObject;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection con = ConnectDB.getConnection();) {
            //load part
            ps = con.prepareStatement("select * from part");
            rs = ps.executeQuery();
            List<Part> parts = new ArrayList<>();
            while (rs.next()) {
                Part part = new Part();
                part.id = rs.getShort("id");
                part.type = rs.getByte("type");
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data").replaceAll("\\\"", ""));
                for (int j = 0; j < dataArray.size(); j++) {
                    JSONArray pd = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                    part.partDetails.add(new PartDetail(Short.parseShort(String.valueOf(pd.get(0))),
                            Byte.parseByte(String.valueOf(pd.get(1))),
                            Byte.parseByte(String.valueOf(pd.get(2)))));
                    pd.clear();
                }
                parts.add(part);
                dataArray.clear();
            }
            DataOutputStream dos = new DataOutputStream(new FileOutputStream("data/update_data/part"));
            dos.writeShort(parts.size());
            for (Part part : parts) {
                dos.writeByte(part.type);
                for (PartDetail partDetail : part.partDetails) {
                    dos.writeShort(partDetail.iconId);
                    dos.writeByte(partDetail.dx);
                    dos.writeByte(partDetail.dy);
                }
            }
            dos.flush();
            Logger.success("Successfully loaded part (" + parts.size() + ")\n");
            
            //load bg item template
            ps = con.prepareStatement("select * from bg_item_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                BgItem bgItem = new BgItem();
                bgItem.id = rs.getInt("id");
                bgItem.layer = rs.getByte("layer");
                bgItem.dx = rs.getShort("dx");
                bgItem.dy = rs.getShort("dy");
                bgItem.idImage = rs.getShort("image_id");
                BG_ITEMS.add(bgItem);
            }
            Logger.success("Loaded bg item template (" + BG_ITEMS.size() + ") successfully\n");
            
            //load item template
            ps = con.prepareStatement("select * from item_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemTemplate itemTemp = new ItemTemplate();
                itemTemp.id = rs.getShort("id");
                itemTemp.type = rs.getByte("type");
                itemTemp.gender = rs.getByte("gender");
                itemTemp.name = rs.getString("name");
                itemTemp.description = rs.getString("description");
                itemTemp.level = rs.getByte("level");
                itemTemp.iconID = rs.getShort("icon_id");
                itemTemp.part = rs.getShort("part");
                itemTemp.isUpToUp = rs.getBoolean("is_up_to_up");
                itemTemp.strRequire = rs.getInt("power_require");
                itemTemp.gold = rs.getInt("gold");
                itemTemp.goldSell = rs.getInt("gold_sell");
                itemTemp.gem = rs.getInt("gem");
                itemTemp.gemSell = rs.getInt("gem_sell");
                itemTemp.ruby = rs.getInt("ruby");
                itemTemp.ruby_sell = rs.getInt("ruby_sell");
                itemTemp.head = rs.getInt("head");
                itemTemp.body = rs.getInt("body");
                itemTemp.leg = rs.getInt("leg");
                itemTemp.TypeEvent = rs.getInt("TypeEvent");
                itemTemp.isGender = rs.getByte("isGender");
                ITEM_TEMPLATES.add(itemTemp);
            }
            Logger.success("Successfully loaded map item template (" + ITEM_TEMPLATES.size() + ")\n");
            
            //load item option template
            ps = con.prepareStatement("select id, name from item_option_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                ItemOptionTemplate optionTemp = new ItemOptionTemplate();
                optionTemp.id = rs.getInt("id");
                optionTemp.name = rs.getString("name");
                ITEM_OPTION_TEMPLATES.add(optionTemp);
            }
            Logger.success("Successfully loaded map item option template (" + ITEM_OPTION_TEMPLATES.size() + ")\n");
            
            //load clan
            ps = con.prepareStatement("select * from clan");
            rs = ps.executeQuery();
            while (rs.next()) {
                Clan clan = new Clan();
                clan.id = rs.getInt("id");
                clan.name = rs.getString("name");
                clan.name2 = rs.getString("name_2");
                clan.slogan = rs.getString("slogan");
                clan.imgId = rs.getByte("img_id");
                clan.powerPoint = rs.getLong("power_point");
                clan.maxMember = rs.getByte("max_member");
                clan.capsuleClan = rs.getInt("clan_point");
                clan.level = rs.getByte("level");
                clan.CongTiemNangSucManhToanBangHoi = rs.getInt("BuffExp");
                clan.LasttimeBuffExp = rs.getLong("LasttimeBuffExp");
                clan.TimeStarBuffExp = rs.getLong("TimeStarBuffExp");
                clan.loadItemsBoxClanFromSQL(rs.getString("items_box_clan"));
                clan.boss_clan_round = rs.getInt("Boss_clan");
                if (clan.level < 1) {
                    clan.level = 1;
                }
                clan.createTime = (int) (rs.getTimestamp("create_time").getTime() / 1000);
                dataArray = (JSONArray) JSONValue.parse(rs.getString("members"));
                for (int j = 0; j < dataArray.size(); j++) {
                    dataObject = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(j)));
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
                    } catch (NumberFormatException e) {
                    }
                    clan.addClanMember(cm);
                }
                dataArray = (JSONArray) JSONValue.parse(rs.getString("thanhTichBDKB"));
                if (!dataArray.isEmpty()) {
                    clan.levelDoneBanDoKhoBau = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    clan.thoiGianHoanThanhBDKB = Long.parseLong(String.valueOf(dataArray.get(1)));
                }
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_charms"));
                if (!dataArray.isEmpty()) {
                    clan.BuaTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
                    clan.BuaManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
                    clan.BuaDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
                }
                dataArray.clear();
                CLANS.add(clan);
            }

            ps = con.prepareStatement("select id from clan order by id desc limit 1");
            rs = ps.executeQuery();
            if (rs.first()) {
                Clan.NEXT_ID = rs.getInt("id") + 1;
            }
            Logger.success("Loaded clan (" + CLANS.size() + ") successfully, clan next id : " + Clan.NEXT_ID + " successfully\n");
            
            //load skill
            ps = con.prepareStatement("select * from skill_template order by nclass_id, slot");
            rs = ps.executeQuery();
            byte nClassId = -1;
            NClass nClass = null;
            while (rs.next()) {
                byte id = rs.getByte("nclass_id");
                if (id != nClassId) {
                    nClassId = id;
                    nClass = new NClass();
                    nClass.name = id == ConstPlayer.TRAI_DAT ? "Trái Đất" : id == ConstPlayer.NAMEC ? "Namếc" : "Xayda";
                    nClass.classId = nClassId;
                    NCLASS.add(nClass);
                }
                SkillTemplate skillTemplate = new SkillTemplate();
                skillTemplate.classId = nClassId;
                skillTemplate.id = rs.getByte("id");
                skillTemplate.name = rs.getString("name");
                skillTemplate.maxPoint = rs.getByte("max_point");
                skillTemplate.manaUseType = rs.getByte("mana_use_type");
                skillTemplate.type = rs.getByte("type");
                skillTemplate.iconId = rs.getShort("icon_id");
                skillTemplate.damInfo = rs.getString("dam_info");
                nClass.skillTemplatess.add(skillTemplate);

                dataArray = (JSONArray) JSONValue.parse(
                        rs.getString("skills")
                                .replaceAll("\\[\"", "[")
                                .replaceAll("\"\\[", "[")
                                .replaceAll("\"\\]", "]")
                                .replaceAll("\\]\"", "]")
                                .replaceAll("\\}\",\"\\{", "},{")
                );
                for (int j = 0; j < dataArray.size(); j++) {
                    JSONObject dts = (JSONObject) JSONValue.parse(String.valueOf(dataArray.get(j)));
                    Skill skill = new Skill();
                    skill.template = skillTemplate;
                    skill.skillId = Short.parseShort(String.valueOf(dts.get("id")));
                    skill.point = Byte.parseByte(String.valueOf(dts.get("point")));
                    skill.powRequire = Long.parseLong(String.valueOf(dts.get("power_require")));
                    skill.manaUse = Integer.parseInt(String.valueOf(dts.get("mana_use")));
                    skill.coolDown = Integer.parseInt(String.valueOf(dts.get("cool_down")));
                    skill.dx = Integer.parseInt(String.valueOf(dts.get("dx")));
                    skill.dy = Integer.parseInt(String.valueOf(dts.get("dy")));
                    skill.maxFight = Integer.parseInt(String.valueOf(dts.get("max_fight")));
                    skill.damage = Short.parseShort(String.valueOf(dts.get("damage")));
                    skill.price = Short.parseShort(String.valueOf(dts.get("price")));
                    skill.moreInfo = String.valueOf(dts.get("info"));
                    skillTemplate.skillss.add(skill);
                }
            }
            Logger.success("Loaded new skills (" + NCLASS.size() + ") successfully\n");

            //load head avatar
            ps = con.prepareStatement("select * from head_avatar");
            rs = ps.executeQuery();
            while (rs.next()) {
                HeadAvatar headAvatar = new HeadAvatar(rs.getInt("head_id"), rs.getInt("avatar_id"));
                HEAD_AVATARS.add(headAvatar);
            }
            Logger.success("Loaded head_avatar (" + HEAD_AVATARS.size() + ") successfully\n");

            //load flag bag
            ps = con.prepareStatement("select * from flag_bag");
            rs = ps.executeQuery();
            while (rs.next()) {
                FlagBag flagBag = new FlagBag();
                flagBag.id = rs.getInt("id");
                flagBag.name = rs.getString("name");
                flagBag.gold = rs.getInt("gold");
                flagBag.gem = rs.getInt("gem");
                flagBag.iconId = rs.getShort("icon_id");
                String[] iconData = rs.getString("icon_data").split(",");
                flagBag.iconEffect = new short[iconData.length];
                for (int j = 0; j < iconData.length; j++) {
                    flagBag.iconEffect[j] = Short.parseShort(iconData[j].trim());
                }
                FLAGS_BAGS.add(flagBag);
            }
            Logger.success("Loaded flag_bag (" + FLAGS_BAGS.size() + ") successfully\n");
            
            //load array head 2 frames
            ps = con.prepareStatement("select * from array_head_2_frames");
            rs = ps.executeQuery();
            while (rs.next()) {
                ArrHead2Frames arrHead2Frames = new ArrHead2Frames();
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data"));
                for (int j = 0; j < dataArray.size(); j++) {
                    arrHead2Frames.frames.add(Integer.valueOf(dataArray.get(j).toString()));
                }
                ARR_HEAD_2_FRAMES.add(arrHead2Frames);
            }
            Logger.success("Successfully loaded arr head 2 frames (" + ARR_HEAD_2_FRAMES.size() + ")\n");

            //load intrinsic
            ps = con.prepareStatement("select * from intrinsic");
            rs = ps.executeQuery();
            while (rs.next()) {
                Intrinsic intrinsic = new Intrinsic();
                intrinsic.id = rs.getByte("id");
                intrinsic.name = rs.getString("name");
                intrinsic.paramFrom1 = rs.getShort("param_from_1");
                intrinsic.paramTo1 = rs.getShort("param_to_1");
                intrinsic.paramFrom2 = rs.getShort("param_from_2");
                intrinsic.paramTo2 = rs.getShort("param_to_2");
                intrinsic.icon = rs.getShort("icon");
                intrinsic.gender = rs.getByte("gender");
                switch (intrinsic.gender) {
                    case ConstPlayer.TRAI_DAT:
                        INTRINSIC_TD.add(intrinsic);
                        break;
                    case ConstPlayer.NAMEC: 
                        INTRINSIC_NM.add(intrinsic);
                        break;
                    case ConstPlayer.XAYDA: 
                        INTRINSIC_XD.add(intrinsic);
                        break;
                    default: {
                        INTRINSIC_TD.add(intrinsic);
                        INTRINSIC_NM.add(intrinsic);
                        INTRINSIC_XD.add(intrinsic);
                        break;
                    }
                }
                INTRINSICS.add(intrinsic);
            }
            Logger.success("Loaded intrinsic (" + INTRINSICS.size() + ") successfully\n");

            //load task
            ps = con.prepareStatement("SELECT id, task_main_template.name, detail, "
                    + "task_sub_template.name AS 'sub_name', max_count, notify, npc_id, map "
                    + "FROM task_main_template JOIN task_sub_template ON task_main_template.id = "
                    + "task_sub_template.task_main_id");
            rs = ps.executeQuery();
            int taskId = -1;
            TaskMain task = null;
            while (rs.next()) {
                int id = rs.getInt("id");
                if (id != taskId) {
                    taskId = id;
                    task = new TaskMain();
                    task.id = taskId;
                    task.name = rs.getString("name");
                    task.detail = rs.getString("detail");
                    TASKS.add(task);
                }
                SubTaskMain subTask = new SubTaskMain();
                subTask.name = rs.getString("sub_name");
                subTask.maxCount = rs.getShort("max_count");
                subTask.notify = rs.getString("notify");
                subTask.npcId = rs.getByte("npc_id");
                subTask.mapId = rs.getShort("map");
                task.subTasks.add(subTask);
            }
            Logger.success("Loaded task_main_template (" + TASKS.size() + ") successfully\n");
            
            //load achievement template
            ps = con.prepareStatement("select * from achievement_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                ACHIEVEMENT_TEMPLATE.add(new AchievementTemplate(rs.getString("info1"), rs.getString("info2"), rs.getInt("money"), rs.getLong("max_count")));
            }
            Logger.success("Loaded archievement_template (" + ACHIEVEMENT_TEMPLATE.size() + ") successfully\n");
            
            //load side task
            ps = con.prepareStatement("select * from side_task_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                SideTaskTemplate sideTask = new SideTaskTemplate();
                sideTask.id = rs.getInt("id");
                sideTask.name = rs.getString("name");
                String[] mc1 = rs.getString("max_count_lv1").split("-");
                String[] mc2 = rs.getString("max_count_lv2").split("-");
                String[] mc3 = rs.getString("max_count_lv3").split("-");
                String[] mc4 = rs.getString("max_count_lv4").split("-");
                String[] mc5 = rs.getString("max_count_lv5").split("-");
                sideTask.count[0][0] = Integer.parseInt(mc1[0]);
                sideTask.count[0][1] = Integer.parseInt(mc1[1]);
                sideTask.count[1][0] = Integer.parseInt(mc2[0]);
                sideTask.count[1][1] = Integer.parseInt(mc2[1]);
                sideTask.count[2][0] = Integer.parseInt(mc3[0]);
                sideTask.count[2][1] = Integer.parseInt(mc3[1]);
                sideTask.count[3][0] = Integer.parseInt(mc4[0]);
                sideTask.count[3][1] = Integer.parseInt(mc4[1]);
                sideTask.count[4][0] = Integer.parseInt(mc5[0]);
                sideTask.count[4][1] = Integer.parseInt(mc5[1]);
                SIDE_TASKS_TEMPLATE.add(sideTask);
            }
            Logger.success("Loaded side_task_template (" + SIDE_TASKS_TEMPLATE.size() + ") successfully\n");
            
            //load clan task
            ps = con.prepareStatement("select * from clan_task_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                ClanTaskTemplate clanTask = new ClanTaskTemplate();
                clanTask.id = rs.getInt("id");
                clanTask.name = rs.getString("name");
                String[] mc1 = rs.getString("max_count_lv1").split("-");
                String[] mc2 = rs.getString("max_count_lv2").split("-");
                String[] mc3 = rs.getString("max_count_lv3").split("-");
                String[] mc4 = rs.getString("max_count_lv4").split("-");
                String[] mc5 = rs.getString("max_count_lv5").split("-");
                clanTask.count[0][0] = Integer.parseInt(mc1[0]);
                clanTask.count[0][1] = Integer.parseInt(mc1[1]);
                clanTask.count[1][0] = Integer.parseInt(mc2[0]);
                clanTask.count[1][1] = Integer.parseInt(mc2[1]);
                clanTask.count[2][0] = Integer.parseInt(mc3[0]);
                clanTask.count[2][1] = Integer.parseInt(mc3[1]);
                clanTask.count[3][0] = Integer.parseInt(mc4[0]);
                clanTask.count[3][1] = Integer.parseInt(mc4[1]);
                clanTask.count[4][0] = Integer.parseInt(mc5[0]);
                clanTask.count[4][1] = Integer.parseInt(mc5[1]);
                CLAN_TASKS_TEMPLATE.add(clanTask);
            }
            Logger.success("Loaded side_task_clan (" + CLAN_TASKS_TEMPLATE.size() + ") successfully\n");
            
            //load shop
            SHOPS = ShopDAO.getShops(con);
            Logger.success("Loaded shop (" + SHOPS.size() + ") successfully\n");

            //load image by name
            ps = con.prepareStatement("select name, n_frame from img_by_name");
            rs = ps.executeQuery();
            while (rs.next()) {
                IMAGES_BY_NAME.put(rs.getString("name"), rs.getByte("n_frame"));
            }
            Logger.success("Successfully loaded images by name (" + IMAGES_BY_NAME.size() + ")\n");

            //Load mount
            for (ItemTemplate item : ITEM_TEMPLATES) {
                if (item.type == 23 && getNFrameImageByName("mount_" + item.part + "_0") != 0) {
                    MAP_MOUNT_NUM.put(item.id, (short) (item.part + 30000));
                }
            }
            Logger.success("Successfully loaded mount (" + MAP_MOUNT_NUM.size() + ")\n");
            
            //load kol task
            ps = con.prepareStatement("select * from task_kol_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                KOL_TASKS_TEMPLATE.add(new KolTaskTemplate(rs.getInt("id"), rs.getString("info"), rs.getInt("max_count")));
            }
            Logger.success("Successfully loaded kol task (" + KOL_TASKS_TEMPLATE.size() + ")\n");
            
            // load side task
            ps = con.prepareStatement("select * from task_event_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                EventTaskTemplate eventTask = new EventTaskTemplate();
                eventTask.id = rs.getInt("id");
                eventTask.name = rs.getString("name");
                String[] mc1 = rs.getString("max_count_lv1").split("-");
                String[] mc2 = rs.getString("max_count_lv2").split("-");
                String[] mc3 = rs.getString("max_count_lv3").split("-");
                String[] mc4 = rs.getString("max_count_lv4").split("-");
                String[] mc5 = rs.getString("max_count_lv5").split("-");
                eventTask.count[0][0] = Integer.parseInt(mc1[0]);
                eventTask.count[0][1] = Integer.parseInt(mc1[1]);
                eventTask.count[1][0] = Integer.parseInt(mc2[0]);
                eventTask.count[1][1] = Integer.parseInt(mc2[1]);
                eventTask.count[2][0] = Integer.parseInt(mc3[0]);
                eventTask.count[2][1] = Integer.parseInt(mc3[1]);
                eventTask.count[3][0] = Integer.parseInt(mc4[0]);
                eventTask.count[3][1] = Integer.parseInt(mc4[1]);
                eventTask.count[4][0] = Integer.parseInt(mc5[0]);
                eventTask.count[4][1] = Integer.parseInt(mc5[1]);
                EVENT_TASKS_TEMPLATE.add(eventTask);
            }
            Logger.success("Successfully loaded event task (" + EVENT_TASKS_TEMPLATE.size() + ")\n");
            
            // load task badges
            ps = con.prepareStatement("select * from task_badges_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                BadgesTaskTemplate badgesTaskTemplate = new BadgesTaskTemplate();
                badgesTaskTemplate.id = rs.getInt("id");
                badgesTaskTemplate.name = rs.getString("NAME");
                badgesTaskTemplate.count = rs.getInt("maxCount");
                badgesTaskTemplate.idbadgesReward = rs.getInt("idbadgesReward");
                TASKS_BADGES_TEMPLATE.add(badgesTaskTemplate);
            }
            Logger.success("Loaded task badges (" + TASKS_BADGES_TEMPLATE.size() + ") successfully\n");
            
            ps = con.prepareStatement("select * from data_badges");
            rs = ps.executeQuery();
            while (rs.next()) {
                BagesTemplate template = new BagesTemplate();
                template.id = rs.getInt("id");
                template.idEffect = rs.getInt("idEffect");
                template.idItem = rs.getInt("idItem");
                template.NAME = rs.getString("NAME");

                JSONArray option = (JSONArray) JSONValue.parse(rs.getString("Options"));
                if (option != null) {
                    for (int u = 0; u < option.size(); u++) {
                        JSONObject jsonobject = (JSONObject) option.get(u);
                        int optionId = Integer.parseInt(jsonobject.get("id").toString());
                        int param = Integer.parseInt(jsonobject.get("param").toString());
                        template.options.add(new ItemOption(optionId, param));
                    }
                }
                BAGES_TEMPLATES.add(template);
            }
            Logger.success("Loaded badges template (" + BAGES_TEMPLATES.size() + ") successfully\n");

            //load mob template
            ps = con.prepareStatement("select * from mob_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                MobTemplate mobTemp = new MobTemplate();
                mobTemp.id = rs.getInt("id");
                mobTemp.type = rs.getByte("type");
                mobTemp.name = rs.getString("name");
                mobTemp.hp = rs.getInt("hp");
                mobTemp.rangeMove = rs.getByte("range_move");
                mobTemp.speed = rs.getByte("speed");
                mobTemp.dartType = rs.getByte("dart_type");
                mobTemp.percentDame = rs.getByte("percent_dame");
                mobTemp.percentTiemNang = rs.getByte("percent_tiem_nang");
                mobTemp.percent_gold = rs.getByte("percent_gold");
                MOB_TEMPLATES.add(mobTemp);
            }
            Logger.success("Loaded mob template (" + MOB_TEMPLATES.size() + ") successfully\n");

            //load npc template
            ps = con.prepareStatement("select * from npc_template");
            rs = ps.executeQuery();
            while (rs.next()) {
                NpcTemplate npcTemp = new NpcTemplate();
                npcTemp.id = rs.getByte("id");
                npcTemp.name = rs.getString("name");
                npcTemp.head = rs.getShort("head");
                npcTemp.body = rs.getShort("body");
                npcTemp.leg = rs.getShort("leg");
                npcTemp.avatar = rs.getInt("avatar");
                NPC_TEMPLATES.add(npcTemp);
            }
            Logger.success("Loaded npc_template (" + NPC_TEMPLATES.size() + ") successfully\n");

            //load map template
            ps = con.prepareStatement("select count(id) from map_template");
            rs = ps.executeQuery();
            if (rs.next()) {
                int countRow = rs.getShort(1);
                MAP_TEMPLATES = new MapTemplate[countRow];
                ps = con.prepareStatement("select * from map_template");
                rs = ps.executeQuery();
                short y = 0;
                while (rs.next()) {
                    MapTemplate mapTemplate = new MapTemplate();
                    int mapId = rs.getInt("id");
                    String mapName = rs.getString("name");
                    mapTemplate.id = mapId;
                    mapTemplate.name = mapName;
                    mapTemplate.type = rs.getByte("type");
                    mapTemplate.planetId = rs.getByte("planet_id");
                    mapTemplate.bgType = rs.getByte("bg_type");
                    mapTemplate.tileId = rs.getByte("tile_id");
                    mapTemplate.bgId = rs.getByte("bg_id");
                    mapTemplate.zones = rs.getByte("zones");
                    mapTemplate.maxPlayerPerZone = rs.getByte("max_player");
                    mapTemplate.genderType = rs.getByte("genderType");
                    //load waypoints
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("waypoints")
                            .replaceAll("\\[\"\\[", "[[")
                            .replaceAll("\\]\"\\]", "]]")
                            .replaceAll("\",\"", ",")
                    );
                    for (int j = 0; j < dataArray.size(); j++) {
                        WayPoint wp = new WayPoint();
                        JSONArray dtwp = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                        wp.name = String.valueOf(dtwp.get(0));
                        wp.minX = Short.parseShort(String.valueOf(dtwp.get(1)));
                        wp.minY = Short.parseShort(String.valueOf(dtwp.get(2)));
                        wp.maxX = Short.parseShort(String.valueOf(dtwp.get(3)));
                        wp.maxY = Short.parseShort(String.valueOf(dtwp.get(4)));
                        wp.isEnter = Byte.parseByte(String.valueOf(dtwp.get(5))) == 1;
                        wp.isOffline = Byte.parseByte(String.valueOf(dtwp.get(6))) == 1;
                        wp.goMap = Short.parseShort(String.valueOf(dtwp.get(7)));
                        wp.goX = Short.parseShort(String.valueOf(dtwp.get(8)));
                        wp.goY = Short.parseShort(String.valueOf(dtwp.get(9)));
                        mapTemplate.wayPoints.add(wp);
                        dtwp.clear();
                    }
                    dataArray.clear();
                    //load mobs
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("mobs").replaceAll("\\\"", ""));
                    mapTemplate.mobTemp = new byte[dataArray.size()];
                    mapTemplate.mobLevel = new byte[dataArray.size()];
                    mapTemplate.mobHp = new int[dataArray.size()];
                    mapTemplate.mobX = new short[dataArray.size()];
                    mapTemplate.mobY = new short[dataArray.size()];
                    for (int j = 0; j < dataArray.size(); j++) {
                        JSONArray dtm = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                        mapTemplate.mobTemp[j] = Byte.parseByte(String.valueOf(dtm.get(0)));
                        mapTemplate.mobLevel[j] = Byte.parseByte(String.valueOf(dtm.get(1)));
                        mapTemplate.mobHp[j] = Integer.parseInt(String.valueOf(dtm.get(2)));
                        mapTemplate.mobX[j] = Short.parseShort(String.valueOf(dtm.get(3)));
                        mapTemplate.mobY[j] = Short.parseShort(String.valueOf(dtm.get(4)));
                        dtm.clear();
                    }
                    dataArray.clear();
                    //load npcs
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("npcs").replaceAll("\\\"", ""));
                    mapTemplate.npcId = new byte[dataArray.size()];
                    mapTemplate.npcX = new short[dataArray.size()];
                    mapTemplate.npcY = new short[dataArray.size()];
                    for (int j = 0; j < dataArray.size(); j++) {
                        JSONArray dtn = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                        mapTemplate.npcId[j] = Byte.parseByte(String.valueOf(dtn.get(0)));
                        mapTemplate.npcX[j] = Short.parseShort(String.valueOf(dtn.get(1)));
                        mapTemplate.npcY[j] = Short.parseShort(String.valueOf(dtn.get(2)));
                        dtn.clear();
                    }
                    dataArray.clear();                    
                    
                    MAP_TEMPLATES[y++] = mapTemplate;
                }
                Logger.success("Successfully loaded map template (" + MAP_TEMPLATES.length + ")\n");
            }

            //Load item ki gui
            ps = con.prepareStatement("SELECT * FROM shop_ky_gui");
            rs = ps.executeQuery();
            while (rs.next()) {
                int y = rs.getInt("id");
                int idPl = rs.getInt("player_id");
                byte tab = rs.getByte("tab");
                short itemId = rs.getShort("item_id");
                int gold = rs.getInt("gold");
                int gem = rs.getInt("gem");
                int quantity = rs.getInt("quantity");
                long isTime = rs.getLong("lasttime");
                boolean isBuy = rs.getByte("isBuy") == 1;
                List<ItemOption> op = new ArrayList<>();
                JSONArray jsa2 = (JSONArray) JSONValue.parse(rs.getString("itemOption"));
                for (int j = 0; j < jsa2.size(); ++j) {
                    JSONObject jso2 = (JSONObject) jsa2.get(j);
                    int idOptions = Integer.parseInt(jso2.get("id").toString());
                    int param = Integer.parseInt(jso2.get("param").toString());
                    op.add(new ItemOption(idOptions, param));
                }
                ConsignShopManager.gI().listItem.add(new ConsignItem(y, itemId, idPl, tab, gold, gem, quantity, isTime, op, isBuy));
            }
            Logger.log(Logger.GREEN, "Loaded shop_ky_gui (" + ConsignShopManager.gI().listItem.size() + ") successfully\n");
            
            //load notify
            ps = con.prepareStatement("select * from notify order by id desc");
            rs = ps.executeQuery();
            while (rs.next()) {
                NOTIFY.add(rs.getString("name") + "<>" + rs.getString("text"));
            }
            Logger.success("Loaded notify (" + NOTIFY.size() + ") successfully\n");

            ps = con.prepareStatement("select * from radar");
            rs = ps.executeQuery();
            while (rs.next()) {
                RadarCard rd = new RadarCard();
                rd.Id = rs.getShort("id");
                rd.IconId = rs.getShort("iconId");
                rd.Rank = rs.getByte("rank");
                rd.Max = rs.getByte("max");
                rd.Type = rs.getByte("type");
                rd.Template = rs.getShort("mob_id");
                rd.Name = rs.getString("name");
                rd.Info = rs.getString("info");
                JSONArray arr = (JSONArray) JSONValue.parse(rs.getString("body"));
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject ob = (JSONObject) arr.get(j);
                    if (ob != null) {
                        rd.Head = Short.parseShort(ob.get("head").toString());
                        rd.Body = Short.parseShort(ob.get("body").toString());
                        rd.Leg = Short.parseShort(ob.get("leg").toString());
                        rd.Bag = Short.parseShort(ob.get("bag").toString());
                    }
                }
                rd.Options.clear();
                arr = (JSONArray) JSONValue.parse(rs.getString("options"));
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject ob = (JSONObject) arr.get(j);
                    if (ob != null) {
                        rd.Options.add(new OptionCard(Integer.parseInt(ob.get("id").toString()), Short.parseShort(ob.get("param").toString()), Byte.parseByte(ob.get("activeCard").toString())));
                    }
                }
                rd.AuraId = rs.getShort("aura_id");
                RadarService.gI().RADAR_TEMPLATE.add(rd);
            }
            Logger.success("Successfully loaded radar template (" + RadarService.gI().RADAR_TEMPLATE.size() + ")\n");
            
            //TOP
            TopServer.Topserver_data(con);
            
        } catch (Exception e) {
            Logger.logException(Manager.class, e, "Lỗi load database");
            System.exit(0);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
            }
        }
        Logger.log(Logger.PURPLE, "Tổng thời gian load Database " + (System.currentTimeMillis() - st) + "(ms)\n");
    }

    public void loadMap() {
    long st = System.currentTimeMillis();
    JSONArray dataArray;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try (Connection con = ConnectDB.getConnection();) {
        BG_ITEMS.clear();
        NPC_TEMPLATES.clear();

        ps = con.prepareStatement("select * from bg_item_template");
        rs = ps.executeQuery();
        while (rs.next()) {
            BgItem bgItem = new BgItem();
            bgItem.id = rs.getInt("id");
            bgItem.layer = rs.getByte("layer");
            bgItem.dx = rs.getShort("dx");
            bgItem.dy = rs.getShort("dy");
            bgItem.idImage = rs.getShort("image_id");
            BG_ITEMS.add(bgItem);
        }

        if (rs != null) rs.close();
        if (ps != null) ps.close();

        ps = con.prepareStatement("select * from npc_template");
        rs = ps.executeQuery();
        while (rs.next()) {
            NpcTemplate npcTemp = new NpcTemplate();
            npcTemp.id = rs.getByte("id");
            npcTemp.name = rs.getString("name");
            npcTemp.head = rs.getShort("head");
            npcTemp.body = rs.getShort("body");
            npcTemp.leg = rs.getShort("leg");
            npcTemp.avatar = rs.getInt("avatar");
            NPC_TEMPLATES.add(npcTemp);
        }

        if (rs != null) rs.close();
        if (ps != null) ps.close();

        ps = con.prepareStatement("select count(id) from map_template");
        rs = ps.executeQuery();
        if (rs.next()) {
            int countRow = rs.getShort(1);
            MAP_TEMPLATES = new MapTemplate[countRow];
        }

        if (rs != null) rs.close();
        if (ps != null) ps.close();

        ps = con.prepareStatement("select * from map_template");
        rs = ps.executeQuery();
        short y = 0;
        while (rs.next()) {
            MapTemplate mapTemplate = new MapTemplate();
            mapTemplate.id = rs.getInt("id");
            mapTemplate.name = rs.getString("name");
            mapTemplate.type = rs.getByte("type");
            mapTemplate.planetId = rs.getByte("planet_id");
            mapTemplate.bgType = rs.getByte("bg_type");
            mapTemplate.tileId = rs.getByte("tile_id");
            mapTemplate.bgId = rs.getByte("bg_id");
            mapTemplate.zones = rs.getByte("zones");
            mapTemplate.maxPlayerPerZone = rs.getByte("max_player");
            mapTemplate.genderType = rs.getByte("genderType");

            dataArray = (JSONArray) JSONValue.parse(rs.getString("waypoints")
                    .replaceAll("\\[\"\\[", "[[")
                    .replaceAll("\\]\"\\]", "]]")
                    .replaceAll("\",\"", ","));
            for (int j = 0; j < dataArray.size(); j++) {
                WayPoint wp = new WayPoint();
                JSONArray dtwp = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                wp.name = String.valueOf(dtwp.get(0));
                wp.minX = Short.parseShort(String.valueOf(dtwp.get(1)));
                wp.minY = Short.parseShort(String.valueOf(dtwp.get(2)));
                wp.maxX = Short.parseShort(String.valueOf(dtwp.get(3)));
                wp.maxY = Short.parseShort(String.valueOf(dtwp.get(4)));
                wp.isEnter = Byte.parseByte(String.valueOf(dtwp.get(5))) == 1;
                wp.isOffline = Byte.parseByte(String.valueOf(dtwp.get(6))) == 1;
                wp.goMap = Short.parseShort(String.valueOf(dtwp.get(7)));
                wp.goX = Short.parseShort(String.valueOf(dtwp.get(8)));
                wp.goY = Short.parseShort(String.valueOf(dtwp.get(9)));
                mapTemplate.wayPoints.add(wp);
            }

            dataArray = (JSONArray) JSONValue.parse(rs.getString("mobs").replaceAll("\\\"", ""));
            mapTemplate.mobTemp = new byte[dataArray.size()];
            mapTemplate.mobLevel = new byte[dataArray.size()];
            mapTemplate.mobHp = new int[dataArray.size()];
            mapTemplate.mobX = new short[dataArray.size()];
            mapTemplate.mobY = new short[dataArray.size()];
            for (int j = 0; j < dataArray.size(); j++) {
                JSONArray dtm = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                mapTemplate.mobTemp[j] = Byte.parseByte(String.valueOf(dtm.get(0)));
                mapTemplate.mobLevel[j] = Byte.parseByte(String.valueOf(dtm.get(1)));
                mapTemplate.mobHp[j] = Integer.parseInt(String.valueOf(dtm.get(2)));
                mapTemplate.mobX[j] = Short.parseShort(String.valueOf(dtm.get(3)));
                mapTemplate.mobY[j] = Short.parseShort(String.valueOf(dtm.get(4)));
            }

            dataArray = (JSONArray) JSONValue.parse(rs.getString("npcs").replaceAll("\\\"", ""));
            mapTemplate.npcId = new byte[dataArray.size()];
            mapTemplate.npcX = new short[dataArray.size()];
            mapTemplate.npcY = new short[dataArray.size()];
            for (int j = 0; j < dataArray.size(); j++) {
                JSONArray dtn = (JSONArray) JSONValue.parse(String.valueOf(dataArray.get(j)));
                mapTemplate.npcId[j] = Byte.parseByte(String.valueOf(dtn.get(0)));
                mapTemplate.npcX[j] = Short.parseShort(String.valueOf(dtn.get(1)));
                mapTemplate.npcY[j] = Short.parseShort(String.valueOf(dtn.get(2)));
            }

            MAP_TEMPLATES[y++] = mapTemplate;
        }

    } catch (Exception e) {
        Logger.logException(Manager.class, e, "Lỗi load map");
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException ex) {
        }
    }

    Logger.log(Logger.PURPLE, "Tổng thời gian load Map " + (System.currentTimeMillis() - st) + "(ms)\n");
}

    public void loadProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("data/config/data_base.properties"));
        Object value;
        if ((value = properties.get("server.sv")) != null) {
            SERVER = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.name")) != null) {
            String name = String.valueOf(value);
            ServerManager.NAME = name;
        }
        if ((value = properties.get("server.port")) != null) {
            ServerManager.PORT = Integer.parseInt(String.valueOf(value));
        }
        String linkServer = "";
        if ((value = properties.get("server.ip")) != null) {
            ServerManager.IP = String.valueOf(value);
            linkServer += ServerManager.NAME + ":" + ServerManager.IP + ":" + ServerManager.PORT + ":0,";
        }
        for (int j = 1; j <= 10; j++) {
            value = properties.get("server.sv" + j);
            if (value != null) {
                linkServer += String.valueOf(value) + ":0,";
            }
        }
        DataGame.LINK_IP_PORT = linkServer.substring(0, linkServer.length() - 1);
        if ((value = properties.get("server.waitlogin")) != null) {
            SECOND_WAIT_LOGIN = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.maxperip")) != null) {
            MAX_PER_IP = Integer.parseInt(String.valueOf(value));
        }
        if ((value = properties.get("server.maxplayer")) != null) {
            MAX_PLAYER = Integer.parseInt(String.valueOf(value));
        }
        if ((value = properties.get("server.expserver")) != null) {
            RATE_EXP_SERVER = Byte.parseByte(String.valueOf(value));
        }
        if ((value = properties.get("server.local")) != null) {
            LOCAL = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.test")) != null) {
            TEST = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.daoautoupdater")) != null) {
            DAO_AUTO_UPDATER = String.valueOf(value).toLowerCase().equals("true");
        }
        if ((value = properties.get("server.daoautoupdater")) != null) {
            DAO_AUTO_UPDATER = String.valueOf(value).equalsIgnoreCase("true");
        }
    }

    /**
     * @param tileTypeFocus tile type: top, bot, left, right...
     * @return [tileMapId][tileType]
     */
    private int[][] readTileIndexTileType(int tileTypeFocus) {
        int[][] tileIndexTileType = null;
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_set_info"));
            int numTileMap = dis.readByte();
            tileIndexTileType = new int[numTileMap][];
            for (int y = 0; y < numTileMap; y++) {
                int numTileOfMap = dis.readByte();
                for (int j = 0; j < numTileOfMap; j++) {
                    int tileType = dis.readInt();
                    int numIndex = dis.readByte();
                    if (tileType == tileTypeFocus) {
                        tileIndexTileType[y] = new int[numIndex];
                    }
                    for (int k = 0; k < numIndex; k++) {
                        int typeIndex = dis.readByte();
                        if (tileType == tileTypeFocus) {
                            tileIndexTileType[y][k] = typeIndex;

                        }
                    }
                }
            }
        } catch (IOException e) {
            Logger.logException(MapService.class, e);
        }
        return tileIndexTileType;
    }

    /**
     * @param mapId mapId
     * @return tile map for paint
     */
    private int[][] readTileMap(int mapId) {
        int[][] tileMap = null;
        try {
            try (DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_map_data/" + mapId))) {
                int w = dis.readByte();
                int h = dis.readByte();
                tileMap = new int[h][w];
                for (int[] tm : tileMap) {
                    for (int j = 0; j < tm.length; j++) {
                        tm[j] = dis.readByte();
                    }
                }
            }
        } catch (IOException e) {
        }
        return tileMap;
    }

    //service*******************************************************************
    public static Clan getClanById(int id) throws Exception {
        for (Clan clan : CLANS) {
            if (clan.id == id) {
                return clan;
            }
        }
        throw new Exception("Không tìm thấy clan id: " + id);
    }

    public static void addClan(Clan clan) {
        CLANS.add(clan);
    }

    public static int getNumClan() {
        return CLANS.size();

    }

    public static MobTemplate getMobTemplateByTemp(int mobTempId) {
        for (MobTemplate mobTemp : MOB_TEMPLATES) {
            if (mobTemp.id == mobTempId) {
                return mobTemp;
            }
        }
        return null;
    }

    public static byte getNFrameImageByName(String name) {
        Object n = IMAGES_BY_NAME.get(name);
        if (n != null) {
            return Byte.parseByte(String.valueOf(n));
        } else {
            return 0;
        }
    }
    
    public static String HienThiTimeEvent() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String formattedTime = dateFormat.format(timeSuKienDuaTop);
        return formattedTime;
    }

    public static String DemTimeEvent() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime event = timeSuKienDuaTop.toLocalDateTime();

    if (event.isAfter(now)) {
        long seconds = ChronoUnit.SECONDS.between(now, event);

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        if (days > 0) return "(" + days + " ngày " + hours + " giờ nữa)";
        if (hours > 0) return "(" + hours + " giờ " + minutes + " phút nữa)";
        return "(" + minutes + " phút nữa)";
    }
    return "(Đã kết thúc)";
}

   public static long HienThiTimeEventTwo() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime event = timeSuKienDuaTop.toLocalDateTime();
    long seconds = ChronoUnit.SECONDS.between(now, event);
    return Math.max(seconds, 0);
}
    
    public void loadAttributeServer() {
        PreparedStatement ps;
        ResultSet rs;
        try (Connection con = ConnectDB.getConnection();) {
            AttributeManager am = new AttributeManager();
            ps = con.prepareStatement("SELECT * FROM attribute_server");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int templateID = rs.getInt("attribute_template_id");
                int value = rs.getInt("value");
                int time = rs.getInt("time");
                Attribute at = Attribute.builder()
                        .id(id)
                        .templateID(templateID)
                        .value(value)
                        .time(time)
                        .build();
                am.add(at);
            }
            ServerManager.gI().setAttributeManager(am);
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Manager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateAttributeServer() {
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            AttributeManager am = ServerManager.gI().getAttributeManager();
            List<Attribute> attributes = am.getAttributes();
            ps = con.prepareStatement("UPDATE attribute_server SET attribute_template_id = ?, value = ?, time = ? WHERE id = ?");
            synchronized (attributes) {
                for (Attribute at : attributes) {
                    try {
                        if (at.isChanged()) {
                            ps.setInt(1, at.getTemplate().getId());
                            ps.setInt(2, at.getValue());
                            ps.setInt(3, at.getTime());
                            ps.setInt(4, at.getId());
                            ps.addBatch();
                        }
                    } catch (SQLException e) {
                    }
                }
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(Manager.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateShop() {
        try ( Connection con = ConnectDB.getConnection();) {
            SHOPS = ShopDAO.getShops(con);
        } catch (Exception ex) {

        }
    }
    
    public static void resetEventData(String eventName) {
        String defaultJson = "{\"eventPoint\":0,\"lastExpRewardStage\":0}";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE `event` SET `data` = ? WHERE `name` = ?")) {
            ps.setString(1, defaultJson);
            ps.setString(2, eventName);
            ps.executeUpdate();
        } catch (SQLException e) {
        }
    }
}
