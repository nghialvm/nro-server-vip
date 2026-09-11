package nro.map.RankSuper;



import nro.player.Player;
import nro.server.Client;
import nro.services.MapService;
import nro.services.Service;
import Utils.FormatStyle;
import Utils.TimeUtil;
import Utils.Util;
import network.io.Message;
import consts.ConstSuperRank;
import java.util.ArrayList;
import java.util.List;
import jbcd.dao.SuperRankDAO;
import jbcd.data.GodGK;
import nro.map.Map;
import nro.map.Zone;

public class SuperRankService {

    private static SuperRankService instance;

    public static SuperRankService gI() {
        if (instance == null) {
            instance = new SuperRankService();
        }
        return instance;
    }

    public void competing(Player player, long id) {
        if (player.zone.map.mapId != 113 || id == -1) {
            return;
        }
        int menuType = player.iDMark.getMenuType();
        Player pl = loadPlayer(id);
        if (pl == null) {
            return;
        }
        if (SuperRankManager.gI().currentlyCompeting(player)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DANG_THI_DAU);
            return;
        } else if (SuperRankManager.gI().currentlyCompeting(pl)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DOI_THU_DANG_THI_DAU);
            return;
        } else if (SuperRankManager.gI().awaitingCompetition(player)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DANG_CHO);
            return;
        } else if (SuperRankManager.gI().awaitingCompetition(pl)) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DOI_THU_CHO_THI_DAU);
            return;
        } else if (player.superRank.rank < pl.superRank.rank) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_DUOI_HANG);
            return;
        } else if (player.superRank.rank == pl.superRank.rank) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_CHINH_MINH);
            return;
        } else if (pl.superRank.rank < 10 && player.superRank.rank - pl.superRank.rank > 2) {
            Service.gI().sendThongBao(player, ConstSuperRank.TEXT_KHONG_THE_THI_DAU_TREN_2_HANG);
            return;
        } else if (player.superRank.ticket <= 0 && player.inventory.getGemAndRuby() < 1) {
            Service.gI().sendThongBao(player, "Bạn không đủ ngọc, còn thiếu 1 ngọc nữa");
            return;
        }
        switch (menuType) {
            case 0: {
                Service.gI().sendThongBao(player, ConstSuperRank.TEXT_TOP_100);
                break;
            }
            case 1: {
                if (SuperRankManager.gI().SPRCheck(player.zone)) {
                    Service.gI().sendThongBao(player, ConstSuperRank.TEXT_CHO_IT_PHUT);
                    SuperRankManager.gI().addWSPR(player.id, pl.id);
                } else {
                    SuperRankManager.gI().addSPR(new SuperRankTournament(player, id, player.zone));
                }
                break;
            }
            case 2: {
                SuperRankManager.gI().addSPR(new SuperRankTournament(player, id, getZone(113)));
                break;
            }
        }
    }

    public void topList(Player player, int type) {
        player.iDMark.setMenuType(type);
        Message msg = null;
        try {
            List<Long> list = type == 0
                    ? SuperRankDAO.getPlayerListInRank(player.superRank.rank, 100)
                    : player.superRank.rank <= 10 ? SuperRankDAO.getPlayerListInRank(player.superRank.rank, 11) : SuperRankDAO.getPlayerListInRankRange(player.superRank.rank, 11);
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top 100 Cao Thủ");
            msg.writer().writeByte(list.size());
            for (int i = 0; i < list.size(); i++) {
                Player pl = loadPlayer(list.get(i));
                msg.writer().writeInt(pl.superRank.rank);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                if (player.getSession().version >= 214) {
                    msg.writer().writeShort(-1);
                }
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(textStatus(pl));
                msg.writer().writeUTF(textInfo(pl));
            }
            player.sendMessage(msg);
            msg.cleanup();
            list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }

    public Player loadPlayer(long id) {
        Player player = GodGK.loadById(id);
        SuperRankManager.gI().put(player);
        return player;
    }

    public Player getPlayer(long id) {
        return Client.gI().getPlayerByID(id);
    }

    public String textInfo(Player pl) {
        if (pl == null || pl.nPoint == null) {
            return "Không xác định!";
        }
        pl.setClothes.setup();
        if (pl.Detu != null) {
            pl.Detu.setClothes.setup();
        }
        
        pl.nPoint.calPoint();
        StringBuilder text = new StringBuilder("HP: " + Util.formatNumber(pl.nPoint.hpMax, FormatStyle.VIETNAMESE) + "\n");
        text.append("Sức đánh: ").append(Util.formatNumber(pl.nPoint.dame, FormatStyle.VIETNAMESE)).append("\n");
        text.append("Giáp: ").append(Util.formatNumber(pl.nPoint.def, FormatStyle.VIETNAMESE)).append("\n");
        List<String> historyList = pl.superRank != null ? pl.superRank.getHistory() : new ArrayList<>();
        List<Long> lastTimeList = pl.superRank != null ? pl.superRank.getLastTime() : new ArrayList<>();

        // Mặc định nếu superRank null thì hiển thị 0/0
        int win = pl.superRank != null ? pl.superRank.win : 0;
        int lose = pl.superRank != null ? pl.superRank.lose : 0;

        // Phần tiêu đề
        if (historyList.isEmpty()) {
            text.append("Thắng/Thua: ").append(win).append("/").append(lose);
        } else {
            text.append("Thắng: ").append(win).append(" , Thua: ").append(lose);
        }

        // In lịch sử và thời gian
        int size = Math.min(historyList.size(), lastTimeList.size());
        for (int i = 0; i < size; i++) {
            String history = historyList.get(i);
            long lastTime = lastTimeList.get(i);
            text.append("\n").append(history).append(" ").append(TimeUtil.getTimeLeft(lastTime));
        }
        return text.toString();
    }

    public String textStatus(Player pl) {
        if (SuperRankManager.gI().awaitingCompetition(pl)) {
            return ConstSuperRank.TEXT_DANG_CHO;
        } else if (SuperRankManager.gI().currentlyCompeting(pl)) {
            return SuperRankManager.gI().getCompeting(pl);
        }
        return textReward(pl.superRank.rank);
    }

    public String textReward(int rank) {
        String text = "";
        if (rank == 1) {
            text = "+1000 hồng ngọc/ ngày";
        } else if (rank >= 2 && rank <= 10) {
            text = "+200 hồng ngọc/ ngày";
        } else if (rank >= 11 && rank <= 100) {
            text = "+20 hồng ngọc/ ngày";
        } else if (rank >= 101 && rank <= 199) {
            text = "+1 hồng ngọc/ ngày";
        }
        return text;
    }

    public int reward(int rank) {
        int rw = -1;
        if (rank == 1) {
            rw = 1000;
        } else if (rank >= 2 && rank <= 10) {
            rw = 200;
        } else if (rank >= 11 && rank <= 100) {
            rw = 20;
        } else if (rank >= 101 && rank <= 199) {
            rw = 1;
        }
        return rw;
    }

    public Zone getZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        try {
            if (map != null) {
                int zoneId = 0;
                while (zoneId < map.zones.size()) {
                    Zone zonez = map.zones.get(zoneId);
                    if (!SuperRankManager.gI().SPRCheck(zonez)) {
                        return zonez;
                    }
                    zoneId++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
