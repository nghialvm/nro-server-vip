package nro.clan;

import java.util.ArrayList;
import java.util.List;
import nro.player.Player;
import nro.server.Client;
import nro.services.Service;
import Utils.ErrorResolver;
import Utils.FormatStyle;
import network.io.Message;
import Utils.Logger;
import Utils.TimeUtil;
import Utils.Util;
import jbcd.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import jbcd.dao.PlayerDAO;
import jbcd.data.GodGK;
import lombok.Getter;
import lombok.Setter;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;
import nro.map.BossOfTheGangs.BossOfTheGangs;
import nro.map.RedRibbonHQ.RedRibbonHQ;
import nro.map.SnakeWay.SnakeWay;
import nro.map.TreasureUnderSea.TreasureUnderSea;
import nro.map.DestronGas.DestronGas;
import nro.map.Zone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Clan {

    public static int NEXT_ID = 0;

    public int clanMessageId = 0;
    private final List<ClanMessage> clanMessages;
    
    public List<Item> itemsBoxClan;

    public static final byte LEADER = 0;
    public static final byte DEPUTY = 1;
    public static final byte MEMBER = 2;

    public int id;
    public int imgId;
    public String name;
    public String name2;
    public String slogan;
    public int createTime;
    public long powerPoint;
    public byte maxMember;
    public int level;
    public boolean active;
    public int capsuleClan;
    
    public int CongTiemNangSucManhToanBangHoi;
    public long LasttimeBuffExp;
    public long TimeStarBuffExp;
    
    public int boss_clan_round;
    
    public long BuaTriTue;
    public long BuaManhMe;
    public long BuaDaTrau;

    public long lastTimeOpenDoanhTrai;
    public boolean haveGoneDoanhTrai;
    public RedRibbonHQ doanhTrai;
    public Player playerOpenDoanhTrai;

    public final List<ClanMember> members;
    public final List<Player> membersInGame;

    public TreasureUnderSea BanDoKhoBau;
    public long lastTimeOpenBanDoKhoBau;
    public Player playerOpenBanDoKhoBau;
    public long thoiGianHoanThanhBDKB;
    public int levelDoneBanDoKhoBau;

    public SnakeWay ConDuongRanDoc;
    public long lastTimeOpenConDuongRanDoc;
    public Player playerOpenConDuongRanDoc;
    public long thoiGianHoanThanhCDRD;
    public int levelDoneConDuongRanDoc;

    public DestronGas KhiGasHuyDiet;
    public long lastTimeOpenKhiGasHuyDiet;
    public Player playerOpenKhiGasHuyDiet;
    public int timesPerDayKGHD;
    public long thoiGianHoanThanhKhiGas;
    public int levelDoneKhiGas;
    
    public BossOfTheGangs BossOfTheGang;
    public long lastTimeOpenBossOfTheGangs;

    public boolean isLeader;
    
    public long timeUpdateClan;
    
    @Setter
    @Getter
    private Buff buff;

    public Clan() {
        this.id = NEXT_ID++;
        this.name = "";
        this.name2 = "";
        this.slogan = "";
        this.maxMember = 10;
        this.level = 1;
        this.createTime = (int) (System.currentTimeMillis() / 1000);
        this.members = new ArrayList<>();
        this.membersInGame = new ArrayList<>();
        this.clanMessages = new ArrayList<>();
        this.itemsBoxClan = new ArrayList<>();
        this.BuaDaTrau = System.currentTimeMillis();
        this.BuaManhMe = System.currentTimeMillis();
        this.BuaTriTue = System.currentTimeMillis();
    }
    
    public void rewardTopDamagers(Player player) {
        if (this == null || this.members == null || this.members.isEmpty()) {
            return;
        }
        List<ClanMember> damagers = new ArrayList<>();
        for (ClanMember m : this.members) {
            if (m.memberDamage > 0) {
                damagers.add(m);
            }
        }
        if (damagers.isEmpty()) {
            return;
        }
        if (damagers.size() == 1) {
            ClanMember only = damagers.get(0);
            Player pl = only.getPlayer();
            if (pl != null) {
                int reward = 25;
                int bonus = 10;
                addCapsuleReward(pl, reward + bonus);
            }
            return;
        }
        damagers.sort((a, b) -> Long.compare(b.memberDamage, a.memberDamage));
        for (int i = 0; i < damagers.size(); i++) {
            ClanMember member = damagers.get(i);
            Player pl = member.getPlayer();
            if (pl == null) continue;
            int reward;
            if (i == 0) {
                reward = 25;
            } else if (i == 1 || i == 2) {
                reward = 20;
            } else if (i == 3 || i == 4) {
                reward = 15;
            } else if (i >= 5 && i <= 9) {
                reward = 10;
            } else {
                reward = 5;
            }
            addCapsuleReward(pl, reward);
        }
        resetAllMemberDamage();
    }

    /**
     * Thêm capsule bang vào hành trang và gửi thông báo
     */
    private void addCapsuleReward(Player pl, int amount) {
        if (pl == null || pl.clan == null || pl.clanMember == null) {
            return;
        }
        try {
            pl.clanMember.memberPoint += amount;
            pl.clanMember.clanPoint += amount;
            pl.clan.capsuleClan += amount;
            for (ClanMember cm : pl.clan.getMembers()) {
                Player pls = Client.gI().getPlayerByID(cm.id);
                if (pls != null) {
                    ClanService.gI().sendMyClan(pls);
                    ClanService.gI().updateClanMembersToDB(pls.clan);
                }
            }
            this.saveClanPoint();
            Service.gI().sendThongBao(pl,
                "Bạn vừa nhận được " + amount + " capsule bang.");
        } catch (Exception e) {
            Logger.logException(Clan.class, e,
                "Có lỗi khi addCapsuleReward cho player=" + pl.name);
        }
    }
    
    /**
     * Reset damage của toàn bộ member trong clan về 0
     */
    private void resetAllMemberDamage() {
        if (this.members == null || this.members.isEmpty()) return;
        for (ClanMember member : this.members) {
            member.memberDamage = 0;
        }
        ClanService.gI().updateClanMembersToDB(this);
    }
        
    public void loadItemsBoxClanFromSQL(String json) {
        this.itemsBoxClan = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) org.json.simple.JSONValue.parse(json);
            if (array != null) {
                for (Object obj : array) {
                    try {
                        String raw = (String) obj;
                        JSONArray itemData = (JSONArray) org.json.simple.JSONValue.parse(raw);
                        int itemId = ((Long) itemData.get(0)).intValue();
                        int quantity = ((Long) itemData.get(1)).intValue();
                        String optionsJson = (String) itemData.get(2);
                        long createTime = (Long) itemData.get(3);
                        Item item;
                        if (itemId != -1) {
                            item = new Item();
                            item.template = ItemService.gI().getTemplate(itemId);
                            item.quantity = quantity;
                            item.createTime = createTime;

                            JSONArray opts = (JSONArray) org.json.simple.JSONValue.parse(optionsJson);
                            if (opts != null) {
                                for (Object optObj : opts) {
                                    JSONArray opt = (JSONArray) org.json.simple.JSONValue.parse((String) optObj);
                                    int optionId = ((Long) opt.get(0)).intValue();
                                    int param = ((Long) opt.get(1)).intValue();
                                    item.itemOptions.add(new ItemOption(optionId, param));
                                }
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                            item.createTime = createTime;
                        }
                        this.itemsBoxClan.add(item);
                    } catch (Exception e) {
                        this.itemsBoxClan.add(ItemService.gI().createItemNull());
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(Clan.class, e, "Lỗi khi load itemsBoxClan từ SQL");
        }
        while (this.itemsBoxClan.size() < 20) {
            this.itemsBoxClan.add(ItemService.gI().createItemNull());
        }
    }
    
    public void updateItemsBoxClanToSQL(Clan clan) {
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE clan SET items_box_clan = ? WHERE id = ?")) {
            JSONArray array = new JSONArray();
            for (Item item : clan.itemsBoxClan) {
                JSONArray itemData = new JSONArray();
                itemData.add(item.template != null ? item.template.id : -1);
                itemData.add(item.quantity);
                JSONArray opt = new JSONArray();
                for (ItemOption io : item.itemOptions) {
                    JSONArray optArr = new JSONArray();
                    optArr.add(io.optionTemplate.id);
                    optArr.add(io.param);
                    opt.add(optArr.toJSONString());
                }
                itemData.add(opt.toJSONString());
                itemData.add(item.createTime);
                array.add(itemData.toJSONString());
            }

            ps.setString(1, array.toJSONString());
            ps.setInt(2, clan.id);
            ps.executeUpdate();

        } catch (Exception e) {
            Logger.logException(Clan.class, e);
        }
    }

    public boolean canUpdateClan(Player player) {
        if (Util.canDoWithTime(timeUpdateClan, 60000)) {
            timeUpdateClan = System.currentTimeMillis();
            return true;
        }
        Service.gI().sendThongBao(player, "Vui lòng đợi " + TimeUtil.getTimeLeft(timeUpdateClan, 60) + " nữa");
        return false;
    }

    public ClanMember getLeader() {
        for (ClanMember cm : members) {
            if (cm.role == LEADER) {
                return cm;
            }
        }
        ClanMember cm = new ClanMember();
        cm.name = "Bang chủ";
        return cm;
    }

    public byte getRole(Player player) {
        for (ClanMember cm : members) {
            if (cm.id == player.id) {
                return cm.role;
            }
        }
        return -1;
    }

    public boolean isLeader(Player player) {
        for (ClanMember cm : members) {
            if (cm.id == player.id && cm.role == LEADER) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeputy(Player player) {
        for (ClanMember cm : members) {
            if (cm.id == player.id && cm.role == DEPUTY) {
                return true;
            }
        }
        return false;
    }

    public void addSMTNClan(Player plOri, long param) {
        for (int i = this.membersInGame.size() - 1; i >= 0; i--) {
            Player pl = this.membersInGame.get(i);
            if (!plOri.equals(pl) && pl != null && pl.zone != null && plOri.zone.equals(pl.zone)) {
                long tnsm = param / (Math.abs(Service.gI().getCurrLevel(pl) - Service.gI().getCurrLevel(plOri)) + 1);
                Service.gI().addSMTN(pl, (byte) 1, Util.CrisGH(tnsm), false);
            }
        }
    }

//    public void sendMessageClan(ClanMessage cmg) {
//        Message msg;
//        try {
//            msg = new Message(-51);
//            msg.writer().writeByte(cmg.type);
//            msg.writer().writeInt(cmg.id);
//            msg.writer().writeInt(cmg.playerId);
//            if (cmg.type == 2) {
//                msg.writer().writeUTF(cmg.playerName + " (" + Util.formatNumber(cmg.playerPower, FormatStyle.VIETNAMESE) + ")");
//            } else {
//                msg.writer().writeUTF(cmg.playerName);
//            }
//            msg.writer().writeByte(cmg.role);
//            msg.writer().writeInt(cmg.time);
//            if (cmg.type == 0) {
//                msg.writer().writeUTF(cmg.text);
//                msg.writer().writeByte(cmg.color);
//            } else if (cmg.type == 1) {
//                msg.writer().writeByte(cmg.receiveDonate);
//                msg.writer().writeByte(cmg.maxDonate);
//                msg.writer().writeByte(cmg.isNewMessage);
//            }
//            for (Player pl : this.membersInGame) {
//                pl.sendMessage(msg);
//            }
//            msg.cleanup();
//        } catch (Exception e) {
//            Logger.logException(Clan.class, e);
//        }
//    }
    public void sendMessageClan(ClanMessage cmg) {
    Message msg = null;
    try {
        msg = new Message(-51);
        msg.writer().writeByte(cmg.type);
        msg.writer().writeInt(cmg.id);
        msg.writer().writeInt(cmg.playerId);

        if (cmg.type == 2) {
            msg.writer().writeUTF(cmg.playerName + " (" 
                    + Util.formatNumber(cmg.playerPower, FormatStyle.VIETNAMESE) + ")");
        } else {
            msg.writer().writeUTF(cmg.playerName);
        }

        msg.writer().writeByte(cmg.role);
        msg.writer().writeInt(cmg.time);

        if (cmg.type == 0) {
            msg.writer().writeUTF(cmg.text);
            msg.writer().writeByte(cmg.color);
        } else if (cmg.type == 1) {
            msg.writer().writeByte(cmg.receiveDonate);
            msg.writer().writeByte(cmg.maxDonate);
            msg.writer().writeByte(cmg.isNewMessage);
        }

        for (ClanMember cm : this.members) {
            Player pl = Client.gI().getPlayerByID(cm.id);
            if (pl != null && pl.getSession() != null) {
                pl.sendMessage(msg);
            }
        }
    } catch (Exception e) {
        Logger.logException(Clan.class, e);
    } finally {
        if (msg != null) {
            msg.cleanup();
        }
    }
}

    public void addClanMessage(ClanMessage cmg) {
        this.clanMessages.add(0, cmg);
        if (clanMessages.size() > 20) {
            for (int i = clanMessages.size() - 1; i >= 20; i--) {
                clanMessages.remove(i).dispose();
            }
        }
    }

    public ClanMessage getClanMessage(int clanMessageId) {
        for (ClanMessage cmg : this.clanMessages) {
            if (cmg.id == clanMessageId) {
                return cmg;
            }
        }
        return null;
    }

    public List<ClanMessage> getCurrClanMessages() {
        List<ClanMessage> list = new ArrayList();
        if (this.clanMessages.size() <= 20) {
            list.addAll(this.clanMessages);
        } else {
            for (int i = 0; i < 20; i++) {
                list.add(this.clanMessages.get(i));
            }
        }
        return list;
    }

    public void sendMyClanForAllMember() {
        for (Player pl : this.membersInGame) {
            if (pl != null) {
                ClanService.gI().sendMyClan(pl);
            }
        }
    }

    public void sendFlagBagForAllMember() {
        for (Player pl : this.membersInGame) {
            if (pl != null) {
                Service.gI().sendFlagBag(pl);
            }
        }
    }

    public void addMemberOnline(Player player) {
        this.membersInGame.add(player);
    }

    public void removeMemberOnline(ClanMember cm, Player player) {
        if (player != null) {
            this.membersInGame.remove(player);
        }
        if (cm != null) {
            for (int i = this.membersInGame.size() - 1; i >= 0; i--) {
                if (this.membersInGame.get(i).id == cm.id) {
                    this.membersInGame.remove(i);
                    break;
                }
            }
        }
    }

    public Player getPlayerOnline(int playerId) {
        for (Player player : this.membersInGame) {
            if (player.id == playerId) {
                return player;
            }
        }

        Player playeroffline = GodGK.loadPlayerByID(playerId);
        if (playeroffline != null) {
            return playeroffline;
        }

        return null;
    }

    //load db danh sách member
    public void addClanMember(ClanMember cm) {
        this.members.add(cm);
    }

    //thêm vào khi player tạo mới clan or mới vào clan
    public void addClanMember(Player player, byte role) {
        ClanMember cm = new ClanMember(player, this, role);
        this.members.add(cm);
        player.clanMember = cm;
    }

    //xóa khi member rời clan or bị kích
    public void removeClanMember(ClanMember cm) {
        this.members.remove(cm);
        cm.dispose();
    }

    public byte getCurrMembers() {
        return (byte) this.members.size();
    }

    public List<ClanMember> getMembers() {
        return this.members;
    }

    public ClanMember getClanMember(int memberId) {
        for (ClanMember cm : members) {
            if (cm.id == memberId) {
                return cm;
            }
        }
        return null;
    }

    public void reloadClanMember() {
        for (ClanMember cm : this.members) {
            Player pl = Client.gI().getPlayerByID(cm.id);
            if (pl != null) {
                cm.powerPoint = pl.nPoint.power;
            }
        }
    }
    
    public boolean areAllMembersInSameZone(Player player) {
        if (player == null || player.zone == null || player.clan == null) {
            return false;
        }

        Zone currentZone = player.zone;
        Clan clan = player.clan;
        List<ClanMember> clanMembers = clan.getMembers();

        if (clanMembers == null || clanMembers.isEmpty()) {
            return false;
        }

        for (ClanMember cm : clanMembers) {
            Player member = Client.gI().getPlayerByID(cm.id);

            if (member == null || member.zone == null || !member.zone.equals(currentZone)) {
                return false;
            }
        }

        return true;
    }
    
    public boolean hasOfflineMember(Player player) {
        if (player == null || player.clan == null) {
            return false;
        }

        Clan clan = player.clan;
        List<ClanMember> members = clan.getMembers();

        if (members == null || members.isEmpty()) {
            return false;
        }

        for (ClanMember cm : members) {
            Player member = Client.gI().getPlayerByID(cm.id);

            if (member == null) {
                return true;
            }
        }
        return false;
    }
    
    public void insert() {
        JSONArray dataArray = new JSONArray();
        JSONObject dataObject = new JSONObject();
        for (ClanMember cm : this.members) {
            dataObject.put("id", cm.id);
            dataObject.put("name", cm.name);
            dataObject.put("head", cm.head);
            dataObject.put("body", cm.body);
            dataObject.put("leg", cm.leg);
            dataObject.put("role", cm.role);
            dataObject.put("donate", cm.donate);
            dataObject.put("receive_donate", cm.receiveDonate);
            dataObject.put("member_point", cm.memberPoint);
            dataObject.put("member_damage", cm.memberDamage);
            dataObject.put("clan_point", cm.clanPoint);
            dataObject.put("join_time", cm.joinTime);
            dataObject.put("ask_pea_time", cm.timeAskPea);
            dataObject.put("power", cm.powerPoint);
            dataArray.add(dataObject.toJSONString());
            dataObject.clear();
        }
        String member = dataArray.toJSONString();
        dataArray.clear();

        String top = dataArray.toJSONString();
        
        String thongTinLeader = "[" + getLeader().id + "," + getLeader().name + "," + getLeader().head + "," + getLeader().body + "," + getLeader().leg + "]";
        
        String topKhiGas = "[" + levelDoneKhiGas + "," + thoiGianHoanThanhKhiGas + "]";
        
        String topBDKB = "[" + levelDoneBanDoKhoBau + "," + thoiGianHoanThanhBDKB + "]";
        
        String topCDRD = "[" + levelDoneConDuongRanDoc + "," + thoiGianHoanThanhCDRD + "]";
        
        String data_charms = "[" + BuaTriTue + "," + BuaManhMe + "," + BuaDaTrau + "]";
        
        JSONArray item = new JSONArray();
        JSONArray options = new JSONArray();
        JSONArray opt = new JSONArray();
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
        String itemsClan = dataArray.toJSONString();
        dataArray.clear();

        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("insert into clan (id, name, name_2, slogan, img_id, power_point, max_member, clan_point, level, members, tops, thongTinLeader, thanhTichKhiGas, thanhTichBDKB, thanhTichCDRD, BuffExp, LasttimeBuffExp, TimeStarBuffExp, items_box_clan, Boss_clan, data_charms) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, this.id);
            ps.setString(2, this.name);
            ps.setString(3, this.name2);
            ps.setString(4, this.slogan);
            ps.setInt(5, this.imgId);
            ps.setLong(6, this.powerPoint);
            ps.setByte(7, this.maxMember);
            ps.setInt(8, this.capsuleClan);
            ps.setInt(9, this.level);
            ps.setString(10, member);
            ps.setString(11, top);
            ps.setString(12, thongTinLeader);
            ps.setString(13, topKhiGas);
            ps.setString(14, topBDKB);
            ps.setString(15, topCDRD);
            ps.setInt(16, this.CongTiemNangSucManhToanBangHoi);
            ps.setLong(17, this.LasttimeBuffExp);
            ps.setLong(18, this.TimeStarBuffExp);
            ps.setString(19, itemsClan);
            ps.setInt(20, this.boss_clan_round);
            ps.setString(21, data_charms);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(Clan.class, e, "Có lỗi khi insert clan vào db");
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                Logger.logException(Clan.class, e);
            }
        }

    }
    
    public boolean isClanBuffActive() {
        return System.currentTimeMillis() < (LasttimeBuffExp + TimeStarBuffExp);
    }
    
    public void updateExp() {
        String sql = "UPDATE clan SET LasttimeBuffExp = ?, TimeStarBuffExp = ?, CongTiemNangSucManhToanBangHoi = ? WHERE id = ?";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, this.LasttimeBuffExp);
            ps.setLong(2, this.TimeStarBuffExp);
            ps.setInt(3, this.CongTiemNangSucManhToanBangHoi);
            ps.setInt(4, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(Clan.class, e);
        }
    }

    public void update() {
        JSONArray dataArray = new JSONArray();
        JSONObject dataObject = new JSONObject();
        for (ClanMember cm : this.members) {
            dataObject.put("id", cm.id);
            dataObject.put("name", cm.name);
            dataObject.put("head", cm.head);
            dataObject.put("body", cm.body);
            dataObject.put("leg", cm.leg);
            dataObject.put("role", cm.role);
            dataObject.put("donate", cm.donate);
            dataObject.put("receive_donate", cm.receiveDonate);
            dataObject.put("member_point", cm.memberPoint);
            dataObject.put("member_damage", cm.memberDamage);
            dataObject.put("clan_point", cm.clanPoint);
            dataObject.put("join_time", cm.joinTime);
            dataObject.put("ask_pea_time", cm.timeAskPea);
            dataObject.put("power", cm.powerPoint);
            dataArray.add(dataObject.toJSONString());
            dataObject.clear();
        }

        String member = dataArray.toJSONString();
        
        String thongTinLeader = "[" + getLeader().id + "," + getLeader().name + "," + getLeader().head + "," + getLeader().body + "," + getLeader().leg + "]";

        String topKhiGas = "[" + levelDoneKhiGas + "," + thoiGianHoanThanhKhiGas + "]";
        
        String topBDKB = "[" + levelDoneBanDoKhoBau + "," + thoiGianHoanThanhBDKB + "]";
        
        String topCDRD = "[" + levelDoneConDuongRanDoc + "," + thoiGianHoanThanhCDRD + "]";
        
        String data_charms = "[" + BuaTriTue + "," + BuaManhMe + "," + BuaDaTrau + "]";
        
        JSONArray dataArrayboxclan = new JSONArray();
        JSONArray item = new JSONArray();
        JSONArray options = new JSONArray();
        JSONArray opt = new JSONArray();
        for (Item it : this.itemsBoxClan) {
            if (it.isNotNullItem()) {
                for (ItemOption io : it.itemOptions) {
                    opt.add(io.optionTemplate.id);
                    opt.add(io.param);
                    options.add(opt.toJSONString());
                    opt.clear();
                }
                item.add(it.template.id);
                item.add(it.quantity);
                item.add(options.toJSONString());
                item.add(it.createTime);
            } else {
                item.add(-1);
                item.add(0);
                item.add(opt.toJSONString());
                item.add(it.createTime);
            }
            dataArrayboxclan.add(item.toJSONString());
            item.clear();
            options.clear();
        }
        String itemsClan = dataArrayboxclan.toJSONString();
        dataArrayboxclan.clear();

        dataArray.clear();

        PreparedStatement ps = null;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update clan set slogan = ?, img_id = ?, power_point = ?, max_member = ?, clan_point = ?, level = ?, members = ?, name_2 = ?, tops = ?, "
                    + "thongTinLeader = ?, thanhTichKhiGas = ?, thanhTichBDKB = ?, thanhTichCDRD = ?, BuffExp = ?, LasttimeBuffExp = ?, TimeStarBuffExp = ?, items_box_clan = ?, Boss_clan = ?, data_charms = ? where id = ? limit 1");
            ps.setString(1, this.slogan);
            ps.setInt(2, this.imgId);
            ps.setLong(3, this.powerPoint);
            ps.setByte(4, this.maxMember);
            ps.setInt(5, this.capsuleClan);
            ps.setInt(6, this.level);
            ps.setString(7, member);
            ps.setString(8, this.name2);
            ps.setString(9, "null");
            ps.setString(10, thongTinLeader);
            ps.setString(11, topKhiGas);
            ps.setString(12, topBDKB);
            ps.setString(13, topCDRD);
            ps.setInt(14, this.CongTiemNangSucManhToanBangHoi);
            ps.setLong(15, this.LasttimeBuffExp);
            ps.setLong(16, this.TimeStarBuffExp);
            ps.setString(17, itemsClan);
            ps.setInt(18, this.boss_clan_round);
            ps.setString(19, data_charms);
            ps.setInt(20, this.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(Clan.class, e, "Có lỗi khi insert clan vào db");
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                Logger.logException(Clan.class, e);
            }
        }
    }

    public void deleteDB(int id) {
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("delete from clan where id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(Clan.class, e, "Có lỗi khi delete clan");
        }
    }
    
    //--------------------------------------------------------------------------
    public void updatethanhTichBDKB(int clanId) {
        String topBanDoKhoBau = "[" + levelDoneBanDoKhoBau + "," + thoiGianHoanThanhBDKB + "]";
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update clan set thanhTichBDKB = ? where id = ? limit 1");
            ps.setString(1, topBanDoKhoBau);
            ps.setInt(2, clanId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÀNH TÍCH BẢN ĐỒ KHO BÁU");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }
    }        
        
    public void updatethanhTichBDKBForLeader() {
        PreparedStatement ps;
        String topBanDoKhoBau = "[" + this.name + "," + this.levelDoneBanDoKhoBau + "," + thoiGianHoanThanhBDKB + "," + System.currentTimeMillis() + "]";
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update player set thanhTichBang = ? where id = ? limit 1");
            ps.setString(1, topBanDoKhoBau);
            ps.setInt(2, this.getLeader().id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÔNG TIN BẢN ĐỒ KHO BÁU LEADER");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }       
    }
    
    public void updatethanhTichKG(int clanId) {
        String topKhiGas = "[" + levelDoneKhiGas + "," + thoiGianHoanThanhKhiGas + "]";
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update clan set thanhTichKhiGas = ? where id = ? limit 1");
            ps.setString(1, topKhiGas);
            ps.setInt(2, clanId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÀNH TÍCH KHÍ GAS");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }
    }
    
    public void updatethanhTichKGForLeader() {
        PreparedStatement ps;
        String topKhiGas = "[" + this.name + "," + this.levelDoneKhiGas + "," + thoiGianHoanThanhKhiGas + "," + System.currentTimeMillis() + "]";
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update player set thanhTichBang2 = ? where id = ? limit 1");
            ps.setString(1, topKhiGas);
            ps.setInt(2, this.getLeader().id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÔNG TIN KHÍ GAS LEADER");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }
    }
    
    public void updatethanhTichCDRD(int clanId) {
        String topConDuongRanDoc = "[" + levelDoneConDuongRanDoc + "," + thoiGianHoanThanhCDRD + "]";
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update clan set thanhTichCDRD = ? where id = ? limit 1");
            ps.setString(1, topConDuongRanDoc);
            ps.setInt(2, clanId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÀNH TÍCH CON ĐƯỜNG RẮN ĐỘC");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }
    }        
        
    public void updatethanhTichCDRDForLeader() {
        PreparedStatement ps;
        String topConDuongRanDoc = "[" + this.name + "," + this.levelDoneConDuongRanDoc + "," + thoiGianHoanThanhCDRD + "," + System.currentTimeMillis() + "]";
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update player set thanhTichBang3 = ? where id = ? limit 1");
            ps.setString(1, topConDuongRanDoc);
            ps.setInt(2, this.getLeader().id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÔNG TIN CON ĐƯỜNG RẮN ĐỘC LEADER");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }       
    }
        
    public void updateThongTinLeader(int clanId) {
        String thongTinLeader = "[" + getLeader().id + "," + getLeader().name + "," + getLeader().head + "," + getLeader().body + "," + getLeader().leg + "]";
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update clan set thongTinLeader = ? where id = ? limit 1");
            ps.setString(1, thongTinLeader);
            ps.setInt(2, clanId);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "ERROR KHI UPDATE THÔNG TIN LEADER");
            ErrorResolver.howToFix(e.toString());
        } finally {
        }
    }
    
    /**
     * Lưu clan_point vào DB
     */
    public void saveClanPoint() {
        if (this.id < 0) return;

        String sql = "UPDATE clan SET clan_point = ? WHERE id = ? LIMIT 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, this.capsuleClan);
            ps.setInt(2, this.id);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(Clan.class, e,
                "Có lỗi khi save clan_point cho clanId=" + this.id);
        }
    }
    
    public void updateClanBasicInfo() {
        String sql = "UPDATE clan SET Boss_clan = ?, max_member = ?, clan_point = ?, LEVEL = ?, power_point = ?, img_id = ? WHERE id = ? LIMIT 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, this.boss_clan_round);
            ps.setByte(2, this.maxMember);
            ps.setInt(3, this.capsuleClan);
            ps.setInt(4, this.level);
            ps.setLong(5, this.powerPoint);
            ps.setInt(6, this.imgId);
            ps.setInt(7, this.id);

            ps.executeUpdate();
            Logger.success("Đã update cho clanId=" + this.id);

        } catch (Exception e) {
            Logger.logException(Clan.class, e,
                "Có lỗi khi update cho clanId=" + this.id);
        }
    }
    
    public void resetBossClanIfNewDay() {
        try {
            long now = System.currentTimeMillis();
            LocalDate today = Instant.ofEpochMilli(now).atZone(ZoneId.systemDefault()).toLocalDate();
            long startOfToday = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            if (this.LasttimeBuffExp < startOfToday) {
                this.boss_clan_round = 1;
                this.LasttimeBuffExp = now;
                this.updateClanBasicInfo();
                Logger.success("Reset boss_clan_round về 1 cho clanId=" + this.id);
            }
        } catch (Exception e) {
            Logger.logException(Clan.class, e,
                "Có lỗi khi reset boss_clan_round về 1 cho clanId=" + this.id);
        }
    }
    
    public void updateClanDataCharms(int clanId, long buaTriTue, long buaManhMe, long buaDaTrau) {
        String sql = "UPDATE clan SET data_charms = ? WHERE id = ?";
        String dataCharms = "[" + buaTriTue + "," + buaManhMe + "," + buaDaTrau + "]";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dataCharms);
            ps.setInt(2, clanId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Đã update data_charms cho clanId=" + clanId + " -> " + dataCharms);
            } else {
                System.out.println("Không tìm thấy clanId=" + clanId);
            }
        } catch (Exception e) {
            System.err.println("Có lỗi khi update data_charms cho clanId=" + clanId);
            Logger.logException(Clan.class, e);
        }
    }
    
    public void dispose() {
        if (this.itemsBoxClan != null){
            for(Item it : this.itemsBoxClan) {
                it.dispose();
            }
            this.itemsBoxClan.clear();
        }
        this.itemsBoxClan = null;
    }
}
