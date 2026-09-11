package nro.services;

import nro.inventory.InventoryService;
import nro.effect.EffectSkillService;
import nro.player.Player;
import network.io.Message;
import nro.server.Client;
import nro.server.Manager;
import nro.services.Fun.ChangeMapService;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.Util;
import consts.ConstTranhNgocNamek;
import event.EventManager;
import jbcd.ConnectDB;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import jbcd.dao.PlayerDAO;
import models.Item.Item;
import models.Item.ItemService;
import nro.badges.BadgesTaskService;
import nro.map.DragonNamecWar.TranhNgocService;
import nro.player.DailyGift.DailyGiftService;

public class PlayerService {

    private static PlayerService i;

    public PlayerService() {
    }

    public static PlayerService gI() {
        if (i == null) {
            i = new PlayerService();
        }
        return i;
    }

    public void subMPPlayer(Player player, int mp) {
        if (!player.isDie()) {
            player.nPoint.subMP(mp);
            Service.getInstance().Send_Info_NV(player);
            if (!player.isDeTu && !player.isBo && !player.isMe && !player.isNguoiYeu
                && !player.isConOne && !player.isConTwo && !player.isConThree) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }
    
    public void subHPPlayer(Player player, int hp) {
        if (!player.isDie()) {
            player.nPoint.subHP(hp);
            Service.getInstance().Send_Info_NV(player);
            if (!player.isDeTu && !player.isBo && !player.isMe && !player.isNguoiYeu
                && !player.isConOne && !player.isConTwo && !player.isConThree) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }

    public void sendTNSM(Player player, byte type, long param) {
        if (param > 0) {
            Message msg;
            try {
                msg = new Message(-3);
                msg.writer().writeByte(type);// 0 là cộng sm, 1 cộng tn, 2 là cộng cả 2
                msg.writeCris(Util.CrisGH(param), Manager.readInt);// số tn cần cộng
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }
    
    public void sendSubTNSM(Player player, long param) {
        if (param < 0) {
            Message msg;
            try {
                msg = new Message(-3);
                msg.writer().writeByte(-1);
                msg.writeCris(Util.CrisGH(param), Manager.readInt);
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    public void sendMessageAllPlayer(Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();

    }

    public void sendMessageIgnore(Player plIgnore, Message msg) {
        for (Player pl : Client.gI().getPlayers()) {
            if (pl != null && !pl.equals(plIgnore)) {
                pl.sendMessage(msg);
            }
        }
        msg.cleanup();
    }

    public void sendInfoHp(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 5);
            msg.writeCris(Util.CrisGH(player.nPoint.hp), Manager.readInt);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoMp(Player player) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 6);
            msg.writeCris(Util.CrisGH(player.nPoint.mp), Manager.readInt);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void sendInfoHpMp(Player player) {
        sendInfoHp(player);
        sendInfoMp(player);
    }

    public void hoiPhuc(Player player, long hp, long mp) {
        if (!player.isDie()) {
            player.nPoint.addHp(hp);
            player.nPoint.addMp(mp);
            Service.getInstance().Send_Info_NV(player);
            if (!player.isDeTu && !player.isBo && !player.isMe && !player.isPetFollow && !player.isDuongTang && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree) {
                PlayerService.gI().sendInfoHpMp(player);
            }
        }
    }

    public void sendInfoHpMpMoney(Player player) {
        if (player == null || !player.isPl()) {
            return;
        }
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 4);
            try {
                if (player.getSession().version >= 214) {
                    msg.writer().writeLong(player.inventory.gold);
                } else {
                    msg.writer().writeInt((int) player.inventory.gold);
                }
            } catch (Exception e) {
                msg.writer().writeInt((int) player.inventory.gold);
            }
            msg.writer().writeInt(player.inventory.gem);//luong
            msg.writeCris(Util.CrisGH(player.nPoint.hp), Manager.readInt);//chp
            msg.writeCris(Util.CrisGH(player.nPoint.mp), Manager.readInt);//cmp
            msg.writer().writeInt(player.inventory.ruby);//ruby
            player.sendMessage(msg);
        } catch (Exception e) {
            Logger.logException(PlayerService.class, e);
        }
    }

    public void playerMove(Player player, int x, int y) {
        if (player.zone == null) {
            return;
        }
        if (!player.isDie()) {
            if (player.effectSkill.isCharging) {
                EffectSkillService.gI().stopCharge(player);
            }
            if (player.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(player);
            }

            player.location.x = x;
            player.location.y = y;
            player.location.lastTimeplayerMove = System.currentTimeMillis();
            switch (player.zone.map.mapId) {
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                    if (!player.isBoss && !player.isDeTu && !player.isBo && !player.isMe && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree) {
                        if (x < 24 || x > player.zone.map.mapWidth - 24 || y < 0 || y > player.zone.map.mapHeight - 24) {
                            if (MapService.gI().getWaypointPlayerIn(player) == null) {
                                ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                                return;
                            }
                        }
                        int yTop = player.zone.map.yPhysicInTop(player.location.x, player.location.y);
                        if (yTop >= player.zone.map.mapHeight - 24) {
                            ChangeMapService.gI().changeMap(player, 21 + player.gender, 0, 200, 336);
                            return;
                        }
                    }
                    break;
            }
            if (player.Detu != null) {
                player.Detu.followMaster();
            }
            
            if (player.PetFollow != null) {
                player.PetFollow.followMaster();
            }
            if (player.Duongtang != null) {
                player.Duongtang.followPlayer();
            }
            if (player.PhanThan != null) {
                player.PhanThan.followMaster();
            }
            
            if (player.isPl()) {
                try {
                    int type = player.zone.map.tileMap[player.location.y / 24][player.location.x / 24];
                    player.isFly = type == 0;
                } catch (Exception e) {
                }
                if (player.isFly && player.getMount() == -1) {
                    long mp = player.nPoint.mpg / (100 * (player.effectSkill.isMonkey ? 2 : 1));
                    hoiPhuc(player, 0, -mp);
                }
            }
            MapService.gI().sendPlayerMove(player);
            TaskService.gI().checkDoneTaskGoToMap(player, player.zone);
        }
    }

    public void sendCurrentStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-68);
            msg.writer().writeShort(player.nPoint.stamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendMaxStamina(Player player) {
        Message msg;
        try {
            msg = new Message(-69);
            msg.writer().writeShort(player.nPoint.maxStamina);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void changeAndSendTypePK(Player player, int type) {
        changeTypePK(player, type);
        sendTypePk(player);
    }

    public void changeTypePK(Player player, int type) {
        player.typePk = (byte) type;
    }

    public void sendTypePk(Player player) {
        Message msg;
        try {
            msg = Service.gI().messageSubCommand((byte) 35);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(player.typePk);
            Service.gI().sendMessAllPlayerInMap(player.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void KhoaTaiKhoan(Player playerBaned) {
        try {
            ConnectDB.executeUpdate("update account set ban = 1 where id = ? and username = ?",
                    playerBaned.getSession().userId, playerBaned.getSession().uu);
        } catch (Exception e) {
        }
        Service.gI().sendThongBaoOK(playerBaned, "Tài Khoản Của Bạn Đã Bị Khoá Vì Sử Dụng Những Hành Vi Tiêu Cực Ảnh Hưởng Đến Game\n"
                + "Tài Khoản Sẽ Bị Khoá Sau 5 Giây Nữa!");
        playerBaned.iDMark.setLastTimeBan(System.currentTimeMillis());
        playerBaned.iDMark.setBan(true);
    }
    
    public void MoThanhVienPlayer(Player mtv) {
        try {
            ConnectDB.executeUpdate("update account set active = 1 where id = ? and username = ?", mtv.getSession().userId, mtv.getSession().uu);
        } catch (Exception e) {
        }
        mtv.iDMark.setLastTimeBan(System.currentTimeMillis());
        mtv.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(mtv, "Tài Khoản Của Bạn Đã Được Mở Thành Viên\n"
                + "Hãy Đăng Nhập Lại Để Kích Hoạt!");
    }
    
    public void XoaThanhVienPlayer(Player unmtv) {
        try {
            ConnectDB.executeUpdate("update account set active = 0 where id = ? and username = ?", unmtv.getSession().userId, unmtv.getSession().uu);
        } catch (Exception e) {
        }
        unmtv.iDMark.setLastTimeBan(System.currentTimeMillis());
        unmtv.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(unmtv, "Tài Khoản Của Bạn Đã Bị Huỷ Bỏ Quyền Làm Thành Viên\n"
                + "Hệ Thống Sẽ Kick Bạn Ra Khỏi Server Sau 5 Giây!\n"
                + "Chi Tiết Xin Liên Hệ ZALO : 0982542412");
    }
    
    public void CapQuyenKeyController(Player mad) {
        try {
            ConnectDB.executeUpdate("update account set isQuanTriVien = 1 where id = ? and username = ?",
                    mad.getSession().userId, mad.getSession().uu);
        } catch (Exception e) {
        }
        mad.iDMark.setLastTimeBan(System.currentTimeMillis());
        mad.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(mad,"Tài khoản Đã Được Cấp Quyền Key Controller"
                + "\nVui Lòng Thoát Game Vào Lại Để Kích Hoạt!");
    }
    
    public void HuyQuyenKeyController(Player tad) {
        try {
            ConnectDB.executeUpdate("update account set isQuanTriVien = 0 where id = ? and username = ?",
                    tad.getSession().userId, tad.getSession().uu);
        } catch (Exception e) {
        } 
        tad.iDMark.setLastTimeBan(System.currentTimeMillis());
        tad.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(tad,"Bạn Đã Bị Huỷ Quyền KEY CONTROLLER Vì Nghi Ngờ Lạm Dụng!\n"
                + "Hệ Thống Sẽ Kick Bạn Ra Khỏi Sever Sau 5 Giây Nữa!");
        Client.gI().getPlayers().remove(tad);
    }
    
    public void CapQuyenAdmin(Player mad) {
        try {
            ConnectDB.executeUpdate("update account set is_admin = 1, isFounder = 1 where id = ? and username = ?",
                    mad.getSession().userId, mad.getSession().uu);
        } catch (Exception e) {
        }
        mad.iDMark.setLastTimeBan(System.currentTimeMillis());
        mad.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(mad,"Tài khoản Đã Được Cấp Quyền ADMINISTRATOR"
                + "\nVui Lòng Thoát Game Vào Lại Để Kích Hoạt!");
    }
    
    public void HuyQuyenAdmin(Player tad) {
        try {
            ConnectDB.executeUpdate("update account set is_admin = 0, isFounder = 0 where id = ? and username = ?",
                    tad.getSession().userId, tad.getSession().uu);
        } catch (Exception e) {
        } 
        tad.iDMark.setLastTimeBan(System.currentTimeMillis());
        tad.iDMark.setBan(true);
        Service.gI().sendThongBaoFromAdmin(tad,"Bạn Đã Bị Huỷ Quyền ADMINISTRATOR Vì Nghi Ngờ Lạm Dụng!\n"
                + "Hệ Thống Sẽ Kick Bạn Ra Khỏi Sever Sau 5 Giây Nữa!");
        Client.gI().getPlayers().remove(tad);
    }
    
    public void jail(Player jail) {
        try {
            if (jail.getSession().isJail == false) {
                jail.getSession().isJail = true;
                ConnectDB.executeUpdate("update account set is_jail = 1 where id = ? and username = ?",
                     jail.getSession().userId, jail.getSession().uu);
            } else {
                jail.getSession().isJail = false;
                ConnectDB.executeUpdate("update account set is_jail = 0 where id = ? and username = ?",
                     jail.getSession().userId, jail.getSession().uu);
            }
        } catch (Exception e) {
        }
    }
    
    public static void updateAccountLogout(Player session) {
        if (session.getSession().uu != null && session.getSession().pp != null) {
            Connection con = null;
            PreparedStatement ps = null;

            try {
                con = ConnectDB.getConnection();
                if (con != null && !con.isClosed()) {
                    ps = con.prepareStatement("update account set last_time_logout = ? where id = ?");
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(2, session.getSession().userId);
                    ps.executeUpdate();
                } else {
                    System.out.println("Error");
                }
            } catch (SQLException e) {
                Logger.logException(PlayerService.class, e);
                System.out.println("Error account: " + session.getSession().uu);
            } finally {
                try {
                    if (ps != null && !ps.isClosed()) {
                        ps.close();
                    }
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static void updatePlayerLastTimeLoginGame(Player player) {
        PreparedStatement ps;
        try (Connection con = ConnectDB.getConnection();) {
            ps = con.prepareStatement("update player set LastTimeLoginGame = ? where id = ?");
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, (int) player.id);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            Logger.logException(PlayerDAO.class, e, "Lỗi update LastTimeLoginGame " + player.name);
        } finally {
        }
    }
        
    public boolean savePlayer(Player player) {
        try {
            PlayerDAO.updateTimeLogout = true;
            PlayerDAO.updatePlayer(player);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    
    public void setPos(Player player, int x, int y, int effID) {
        Message msg = new Message(123);
        try {
            DataOutputStream ds = msg.writer();
            ds.writeInt((int) player.id);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.writeByte(effID);
            ds.flush();
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    private static final int COST_GEM_HOI_SINH = 1;
    private static final int COST_GOLD_HOI_SINH_NRSD = 50000000;
    private static final int COST_GOLD_HOI_SINH_PVP = 200000000;

    public void hoiSinh(Player player) {
        if (player.isDie() && player.zone != null && player.zone.map.mapId != 51) {
            if (Util.canDoWithTime(player.lastTimeRevived, 1500)) {
                boolean canHs;
                if (MapService.gI().isMapBlackBallWar(player.zone.map.mapId)) {
                    if (player.inventory.gold >= COST_GOLD_HOI_SINH_NRSD) {
                        player.inventory.gold -= COST_GOLD_HOI_SINH_NRSD;
                        canHs = true;
                    } else {
                        Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " + Util.formatNumber(COST_GOLD_HOI_SINH_NRSD
                                - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
                        return;
                    }
                } else if (MapService.gI().isMapPVP(player.zone.map.mapId)) {
                    if (player.inventory.gold >= COST_GOLD_HOI_SINH_PVP) {
                        player.inventory.gold -= COST_GOLD_HOI_SINH_PVP;
                        canHs = true;
                    } else {
                        Service.gI().sendThongBao(player, "Không đủ vàng để thực hiện, còn thiếu " 
                        + Util.formatNumber(COST_GOLD_HOI_SINH_PVP - player.inventory.gold, FormatStyle.VIETNAMESE) + " vàng");
                        return;
                    }
                } else {
                    if (player.inventory.gem >= COST_GEM_HOI_SINH) {
                        player.inventory.gem -= COST_GEM_HOI_SINH;
                        canHs = true;
                    } else {
                        Service.gI().sendThongBao(player, "Bạn không đủ hồng ngọc để thực hiện");
                        return;
                    }
                }
                if (canHs) {
                    Service.gI().sendMoney(player);
                    Service.gI().hsChar(player, Util.CrisGH(player.nPoint.hpMax), Util.CrisGH(player.nPoint.mpMax));
                    if (player.zone.map.mapId == ConstTranhNgocNamek.MAP_ID) {
                        TranhNgocService.getInstance().sendUpdateLift(player);
                    }
                }
            }
        }
    }
    
    private void ReceiveGiftsEveryday(Player player) {
        if (InventoryService.gI().getCountEmptyBag(player) == 0) {
            return;
        }
        Item HopQua = ItemService.gI().createNewItem((short) 1591, 2);
        HopQua.addOptionParam(30, 0);
        HopQua.addOptionParam(93, 30);
        InventoryService.gI().addItemBag(player, HopQua);
        InventoryService.gI().sendItemBag(player);
        Service.gI().sendThongBao(player, "Bạn nhận được x2 " + HopQua.Name());
    }
    
    public void dailyLogin(Player player) {
        if (Util.isAfterDay(Date.from(Instant.now()), player.firstTimeLogin)) {
            player.firstTimeLogin = Date.from(Instant.now());
            BadgesTaskService.createAndResetTask(player);
            DailyGiftService.addAndReset(player);
            if (EventManager.TRUNG_THU) {
                ReceiveGiftsEveryday(player);
            }
        }
    }
}
