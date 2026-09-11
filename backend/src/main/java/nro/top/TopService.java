package nro.top;

import nro.server.TopServer;
import nro.services.TaskService;
import Utils.FormatStyle;
import Utils.Util;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jbcd.ConnectDB;
import jbcd.dao.TopDAO;
import models.Item.ItemService;
import network.io.Message;
import nro.player.Player;

public class TopService {

    private static TopService instance;

    public static TopService gI() {
        if (instance == null) {
            instance = new TopService();
        }
        return instance;
    }
    
    public static void showListTopMayDamTraiDat(Player player) {
        List<TOP> tops = TopServer.TOP_MAY_DAM_TRAI_DAT;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Trái Đất");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(Util.format(top.getTopMayDamTraiDat()) + " điểm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMayDamNamec(Player player) {
        List<TOP> tops = TopServer.TOP_MAY_DAM_NAMEC;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Namếc");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(Util.format(top.getTopMayDamNamec()) + " điểm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMayDamXayda(Player player) {
        List<TOP> tops = TopServer.TOP_MAY_DAM_XAYDA;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Xayda");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(Util.format(top.getTopMayDamXayda()) + " điểm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopWhis(Player player) {
        List<TOP> tops = TopServer.TOP_WHIS;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 10 Thách Đấu Whis");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF("LV:" + top.getTop() + " với " + Util.roundToTwoDecimals(top.getTime() / 1000d) + " giây");
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopTask(Player player) {
        List<TOP> tops = TopServer.TOP_NHIEM_VU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Nhiệm Vụ");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(TaskService.gI().getTaskMainById(player, top.getNv()).name.substring(0, TaskService.gI().getTaskMainById(player, top.getNv()).name.length() > 20 ? 20 : TaskService.gI().getTaskMainById(player, top.getNv()).name.length()) + "...");
                msg.writer().writeUTF(TaskService.gI().getTaskMainById(player, top.getNv()).subTasks.get(top.getSubnv()).name + " - " + getTimeLeft(top.getLastTimeLogin()));
              
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopVND(Player player) {
        List<TOP> tops = TopServer.TOP_VND;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top VNĐ");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đang giữ " + Util.formatNumber(top.getVnd(), FormatStyle.VIETNAMESE) + " VNĐ trong người.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopCOIN(Player player) {
        List<TOP> tops = TopServer.TOP_COIN;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top COIN");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đang có " + Util.formatNumber(top.getCoin(), FormatStyle.VIETNAMESE) + " Coin trong tài khoản.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopDANAP(Player player) {
        List<TOP> tops = TopServer.TOP_DA_NAP;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Đại Gia");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã nạp " + Util.formatNumber(top.getDanap(), FormatStyle.VIETNAMESE) + " vào game.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopHONGNGOC(Player player) {
        List<TOP> tops = TopServer.TOP_HONG_NGOC;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Hồng Ngọc Xanh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Số hồng ngọc hiện có : " + Util.formatNumber(top.getHongngoc(), FormatStyle.VIETNAMESE));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static String getTopThoiVang() {
        StringBuilder sb = new StringBuilder("");
        PreparedStatement ps;
        ResultSet rs;
        try {
            try (Connection conn = ConnectDB.getConnection()) {
                ps = conn.prepareStatement(TopDAO.TOP_THOI_VANG);
                conn.setAutoCommit(false);
                rs = ps.executeQuery();
                byte i = 1;
                while (rs.next()) {
                    String username = rs.getString("name");
                    sb.append("Top ").append(i).append(" - Player [").append(username).append("] --> sở hữu ").append(rs.getString("thoi_vang")).append(" ").append(ItemService.gI().getTemplate(457).name).append("\b");
                    i++;
                }
            }
        } catch (SQLException e) {
        }
        return sb.toString();
    }
    
    public static void showListTopPower(Player player) {
        List<TOP> tops = TopServer.TOP_SUC_MANH;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Sức Mạnh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("" + Util.format(top.getPower()) + " (" + Util.formatNumber(top.getPower(), FormatStyle.VIETNAMESE) + ") Sức mạnh");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopHP(Player player) {
        List<TOP> tops = TopServer.TOP_HP;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top HP");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("" + Util.format(top.getHp()) + " (" + Util.formatNumber(top.getHp(), FormatStyle.VIETNAMESE) + ") HP");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopKI(Player player) {
        List<TOP> tops = TopServer.TOP_KI;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top KI");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("" + Util.format(top.getKi()) + " (" + Util.formatNumber(top.getKi(), FormatStyle.VIETNAMESE) + ") KI");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopSD(Player player) {
        List<TOP> tops = TopServer.TOP_SD;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Sức Đánh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("" + Util.format(top.getSd()) + " (" + Util.formatNumber(top.getSd(), FormatStyle.VIETNAMESE) + ") Sức Đánh");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopPower_Pet(Player player) {
        List<TOP> tops = TopServer.TOP_SUC_MANH_DE_TU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Sức Mạnh Đệ Tử");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(top.getPower_pet() > 0 ? "Đệ Tử : " + Util.format(top.getPower_pet()) + "\n(" + Util.formatNumber(top.getPower_pet(), FormatStyle.VIETNAMESE) + ") Sức mạnh" : "Người chơi này không có Đệ Tử.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopHP_Pet(Player player) {
        List<TOP> tops = TopServer.TOP_HP_DE_TU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top HP Đệ Tử");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(top.getHp_pet() > 0 ? "Đệ Tử : " + Util.format(top.getHp_pet()) + "\n(" + Util.formatNumber(top.getHp_pet(), FormatStyle.VIETNAMESE) + ") HP" : "Người chơi này không có Đệ Tử.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopKI_Pet(Player player) {
        List<TOP> tops = TopServer.TOP_KI_DE_TU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top KI Đệ Tử");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(top.getKi_pet() > 0 ? "Đệ Tử : " + Util.format(top.getKi_pet()) + "\n(" + Util.formatNumber(top.getKi_pet(), FormatStyle.VIETNAMESE) + ") KI" : "Người chơi này không có Đệ Tử.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopSD_Pet(Player player) {
        List<TOP> tops = TopServer.TOP_SD_DE_TU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top SĐ Đệ Tử");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(top.getSd_pet() > 0 ? "Đệ Tử : " + Util.format(top.getSd_pet()) + "\n(" + Util.formatNumber(top.getSd_pet(), FormatStyle.VIETNAMESE) + ") Sức Đánh" : "Người chơi này không có Đệ Tử.");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopNamecWar(Player player) {
        List<TOP> tops = TopServer.TOP_NAMEK_WAR;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Điểm Chiến Trường");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Sở hữu " + Util.format(top.getPointNamekWar()) + " điểm chiến trường");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopTangLixi(Player player) {
        List<TOP> tops = TopServer.TOP_TANG_LIXI;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Tặng Lì Xì");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã tặng " + Util.format(top.getTangLixi()) + " lì xì");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoLixi(Player player) {
        List<TOP> tops = TopServer.TOP_MO_LIXI;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Lì Xì");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getMoLixi()) + " lì xì");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopBanPhaoHoa(Player player) {
        List<TOP> tops = TopServer.TOP_BAN_PHAO_HOA;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(Util.format(top.getBanPhaoHoa()) + " điểm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopBanPhaoHoaVIP(Player player) {
        List<TOP> tops = TopServer.TOP_BAN_PHAO_HOA_VIP;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF(Util.format(top.getBanPhaoHoaVIP()) + " điểm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopTrangTriCayNoel(Player player) {
        List<TOP> tops = TopServer.TOP_TRANG_TRI_CAY_NOEL;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Trang Trí Noel");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã trang trí " + Util.format(top.getTrangtricayNoel()) + " cây Noel");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopCheTaoNguoiTuyet(Player player) {
        List<TOP> tops = TopServer.CHE_TAO_NGUOI_TUYET;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Người Tuyết");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã chế tạo " + Util.format(top.getChetaonguoituyet()) + " người Tuyết");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopCheTaoNguoiTuyetBangGia(Player player) {
        List<TOP> tops = TopServer.CHE_TAO_NGUOI_TUYET_BANG_GIA;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Người Tuyết Băng Giá");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã chế tạo " + Util.format(top.getChetaonguoituyetbanggia()) + " người Tuyết Băng Giá");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopDotDiem(Player player) {
        List<TOP> tops = TopServer.TOP_DOT_DIEM;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Đốt Diêm");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã quẹt " + Util.format(top.getDotdiem()) + " bao diêm thống nhất");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopBanPhaoHoaEventVuLan(Player player) {
        List<TOP> tops = TopServer.TOP_PHAO_HOA_VU_LAN;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Pháo Hoa");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã bắn " + Util.format(top.getBanPhaoHoaVuLan()) + " Pháo hoa");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopThaHoaDang(Player player) {
        List<TOP> tops = TopServer.TOP_HOA_DANG;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Hoa Đăng");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã thả " + Util.format(top.getHoaDang()) + " chiếc Hoa Đăng");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopThaHoaDangCoLoiChuc(Player player) {
        List<TOP> tops = TopServer.TOP_HOA_DANG_LOI_CHUC;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Hoa Đăng");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã thả " + Util.format(top.getHoaDangLoiChuc()) + " chiếc Hoa Đăng có lời chúc");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoHopQuaMaQuy(Player player) {
        List<TOP> tops = TopServer.TOP_MO_HOP_QUA_MA_QUY;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Hộp Quà Ma Quỷ");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getMoHopMaQuy()) + " Hộp kẹo ma quỷ");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopThiepHalloween(Player player) {
        List<TOP> tops = TopServer.TOP_THIEP_HALLOWEEN;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Thiệp Halloween");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getThiepHalloween()) + " Thiệp Halloween");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoThiep83(Player player) {
        List<TOP> tops = TopServer.TOP_MO_THIEP_83;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Thiệp 8-3");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getDuaTopMoThiep_83()) + " Thiệp 8-3");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopTangBongHoaHong(Player player) {
        List<TOP> tops = TopServer.TOP_TANG_BONG_HOA_HONG;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Tặng Hoa Hồng");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã tặng " + Util.format(top.getDuaTopTangBongHoaHong()) + " Bông hoa hồng");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoBanhTrungThuDacBiet(Player player) {
        List<TOP> tops = TopServer.TOP_MO_BANH_TRUNG_THU_DAC_BIET;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Làm Bánh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getDuaTopMoBanhTrungThuDacBiet()) + " hộp bánh trung thu đặc biệt");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopLamBanhTrungThu(Player player) {
        List<TOP> tops = TopServer.TOP_LAM_BANH_TRUNG_THU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Làm Bánh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã làm " + Util.format(top.getDuaTopLamBanhTrungThu()) + " chiếc bánh trung thu");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoTrungRong(Player player) {
        List<TOP> tops = TopServer.TOP_MO_TRUNG_VANG;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Trứng Vàng");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getDuaTopMoTrungRongVang()) + " Trứng rồng nhí");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoHopQuaGioTo(Player player) {
        List<TOP> tops = TopServer.TOP_MO_HOP_QUA_GIO_TO;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Hộp Quà Giỗ Tổ");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getDuaTopMoHopQuaGioTo()) + " Hộp Quà Giỗ Tổ");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopDangBanhHungVuong(Player player) {
        List<TOP> tops = TopServer.TOP_DANG_BANH;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Dâng Bánh");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã dâng " + Util.format(top.getDuaTopDangBanhHungVuong()) + " Bánh");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopDoiDuaHau(Player player) {
        List<TOP> tops = TopServer.TOP_DOI_DUA_HAU;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Đổi Dưa Hấu");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã đổi " + Util.format(top.getDuaTopDoiDuaHau()) + " Quả dưa hấu");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMoHopBlackFriday(Player player) {
        List<TOP> tops = TopServer.TOP_MO_HOP_BLACK_FRIDAY;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mở Hộp Black Friday");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("Đã mở " + Util.format(top.getDuaTopHopQuaBlackFriday()) + " Hộp Black Friday");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }
    
    public static void showListTopMuaSamBlackFriday(Player player) {
        List<TOP> tops = TopServer.TOP_MUA_SAM_BLACK_FRIDAY;
        Message msg = null;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Mua Sắm Black Friday");
            msg.writer().writeByte(tops.size());
            for (int i = 0; i < tops.size(); i++) {
                TOP top = tops.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt(i + 1);
                msg.writer().writeShort(top.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(top.getBody());
                msg.writer().writeShort(top.getLeg());
                msg.writer().writeUTF(top.getName());
                msg.writer().writeUTF(getTimeLeft(top.getLastTimeLogin()));
                msg.writer().writeUTF("sở hữu " + Util.format(top.getDuaTopMuaSam()) + " điểm mua sắm");
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public static String getTimeLeft(long firsttimelogin) {
        int secondsPassed = (int) ((System.currentTimeMillis() - firsttimelogin) / 1000);

        if (secondsPassed > 86400) {
            return (secondsPassed / 86400) + " ngày trước";
        } else if (secondsPassed > 3600) {
            return (secondsPassed / 3600) + " giờ trước";
        } else if (secondsPassed > 60) {
            return (secondsPassed / 60) + " phút trước";
        } else {
            return secondsPassed + " giây trước";
        }
    }

}
