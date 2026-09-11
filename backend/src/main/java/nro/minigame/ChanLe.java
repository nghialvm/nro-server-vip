package nro.minigame;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.server.Client;
import nro.services.ChatGlobalService;
import nro.services.Service;
import Utils.Util;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jbcd.ConnectDB;
import models.Item.Item;
import models.Item.ItemService;

public class ChanLe implements Runnable {

    private static final int MAX_BET = 20000;
    private static final short THOI_VANG_TEMPLATE = 457;
    private static final int KEEP_HISTORY = 20;

    public int goldChan = 0;
    public int goldLe = 0;
    public long lastTimeEnd = 0;

    public boolean baotri = false;
    public boolean ketquaChan = false;
    public boolean ketquaLe = false;
    public int number = 0;

    // ép kết quả (chỉ hiệu lực cho 1 ván)
    private boolean forceResult = false;
    private boolean forceChan = false;
    private boolean forceUsed = false; // cờ để chỉ dùng ép 1 lần

    public List<Player> PlayersChan = new ArrayList<>();
    public List<Player> PlayersLe = new ArrayList<>();

    private static ChanLe instance;

    public static ChanLe gI() {
        if (instance == null) {
            instance = new ChanLe();
        }
        return instance;
    }

    public boolean isAllowBetting() {
        return System.currentTimeMillis() < this.lastTimeEnd;
    }

    // ================= ADMIN COMMAND =================
    public boolean handleAdminCommand(Player player, String text) {
        if (player == null || player.getSession() == null || !player.getSession().isFounder) {
            return false;
        }

        if (text.equalsIgnoreCase("chan")) {
            this.forceResult = true;
            this.forceChan = true;
            this.forceUsed = false;
            Service.gI().sendThongBao(player, "Đã ép kết quả: CHẴN cho ván hiện tại (chỉ áp dụng 1 lần)");
            return true;
        }

        if (text.equalsIgnoreCase("le")) {
            this.forceResult = true;
            this.forceChan = false;
            this.forceUsed = false;
            Service.gI().sendThongBao(player, "Đã ép kết quả: LẺ cho ván hiện tại (chỉ áp dụng 1 lần)");
            return true;
        }

        if (text.equalsIgnoreCase("atran")) {
            this.forceResult = false;
            this.forceUsed = false;
            Service.gI().sendThongBao(player, "Đã tắt ép kết quả (random bình thường)");
            return true;
        }

        if (text.equalsIgnoreCase("statuscl")) {
            String status = forceResult ? (forceChan ? "ÉP CHẴN" : "ÉP LẺ") : "RANDOM";
            Service.gI().sendThongBao(player, "Trạng thái hiện tại: " + status);
            return true;
        }

        return false;
    }

    // ================= ĐẶT CƯỢC =================
    public void datCuocChan(Player pl, int tienCuoc) {
        if (baotri) {
            Service.gI().sendThongBao(pl, "Hệ thống đang bảo trì");
            return;
        }
        if (!isAllowBetting()) {
            Service.gI().sendThongBao(pl, "Hết thời gian đặt cược, vui lòng chờ ván sau");
            return;
        }
        if (tienCuoc <= 0 || tienCuoc > MAX_BET) {
            Service.gI().sendThongBao(pl, "Tối đa " + Util.format(MAX_BET) + " Thỏi vàng");
            return;
        }

        Item tv = InventoryService.gI().findItemBag(pl, THOI_VANG_TEMPLATE);
        if (tv == null || tv.quantity < tienCuoc) {
            Service.gI().sendThongBao(pl, "Không đủ Thỏi vàng");
            return;
        }

        InventoryService.gI().subQuantityItemsBag(pl, tv, tienCuoc);
        InventoryService.gI().sendItemBag(pl);
        pl.goldChan += tienCuoc;
        this.goldChan += tienCuoc;
        if (!PlayersChan.contains(pl)) {
            PlayersChan.add(pl);
        }
        Service.gI().sendThongBao(pl, "Đặt CHẴN " + Util.format(tienCuoc) + " Thỏi vàng");
    }

    public void datCuocLe(Player pl, int tienCuoc) {
        if (baotri) {
            Service.gI().sendThongBao(pl, "Hệ thống đang bảo trì");
            return;
        }
        if (!isAllowBetting()) {
            Service.gI().sendThongBao(pl, "Hết thời gian đặt cược, vui lòng chờ ván sau");
            return;
        }
        if (tienCuoc <= 0 || tienCuoc > MAX_BET) {
            Service.gI().sendThongBao(pl, "Tối đa " + Util.format(MAX_BET) + " Thỏi vàng");
            return;
        }

        Item tv = InventoryService.gI().findItemBag(pl, THOI_VANG_TEMPLATE);
        if (tv == null || tv.quantity < tienCuoc) {
            Service.gI().sendThongBao(pl, "Không đủ Thỏi vàng");
            return;
        }

        InventoryService.gI().subQuantityItemsBag(pl, tv, tienCuoc);
        InventoryService.gI().sendItemBag(pl);
        pl.goldLe += tienCuoc;
        this.goldLe += tienCuoc;
        if (!PlayersLe.contains(pl)) {
            PlayersLe.add(pl);
        }
        Service.gI().sendThongBao(pl, "Đặt LẺ " + Util.format(tienCuoc) + " Thỏi vàng");
    }

    // ================= CHU TRÌNH GAME =================
    @Override
    public void run() {
        this.lastTimeEnd = System.currentTimeMillis() + 60_000;
        sendToAll("Bắt đầu ván mới, bạn có 60 giây để đặt cược.");

        while (true) {
            try {
                long remain = this.lastTimeEnd - System.currentTimeMillis();

                if (remain <= 0) {
                    quayKetQua();
                    resetRound();
                }

                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void quayKetQua() {
        int so;
        boolean raChan;

        // Nếu admin ép, chỉ dùng đúng 1 ván
        if (forceResult && !forceUsed) {
            raChan = this.forceChan;
            so = raChan ? Util.nextInt(0, 49) * 2 : Util.nextInt(0, 49) * 2 + 1;
            forceUsed = true; // đánh dấu đã dùng
        } else {
            so = Util.nextInt(0, 99);
            raChan = (so % 2 == 0);
        }

        boolean raLe = !raChan;
        this.number = so;
        this.ketquaChan = raChan;
        this.ketquaLe = raLe;

        if (raChan) {
            sendToAll("Kết quả: " + so + " (CHẴN)");
            processWinners(PlayersChan, true);
            processLosers(PlayersLe);
        } else {
            sendToAll("Kết quả: " + so + " (LẺ)");
            processWinners(PlayersLe, false);
            processLosers(PlayersChan);
        }

        addSession();
    }

    private void resetRound() {
        goldChan = 0;
        goldLe = 0;
        for (Player p : PlayersChan) p.goldChan = 0;
        for (Player p : PlayersLe) p.goldLe = 0;
        PlayersChan.clear();
        PlayersLe.clear();
        this.lastTimeEnd = System.currentTimeMillis() + 60_000;
        sendToAll("Bắt đầu ván mới, bạn có 60 giây để đặt cược.");

        // Nếu ép kết quả vừa dùng xong -> tắt luôn, tránh dính ván sau
        if (forceUsed) {
            forceResult = false;
            forceUsed = false;
        }
    }

    private void sendToAll(String msg) {
        for (Player pl : PlayersChan) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null)
                Service.gI().sendThongBao(pl, msg);
        }
        for (Player pl : PlayersLe) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null)
                Service.gI().sendThongBao(pl, msg);
        }
    }

    private void processWinners(List<Player> players, boolean isChan) {
        for (Player pl : players) {
            if (pl == null || Client.gI().getPlayerByName(pl.name) == null) continue;
            int stake = isChan ? pl.goldChan : pl.goldLe;
            if (stake <= 0) continue;
            int goldReward = stake * 190 / 100;
            Item tv = ItemService.gI().createNewItem((short) 457, goldReward);
            tv.addOptionParam(30, 0);
            InventoryService.gI().addItemBag(pl, tv);
            InventoryService.gI().sendItemBag(pl);
            Service.gI().sendThongBao(pl, "Thắng cược, nhận " + Util.format(goldReward) + " Thỏi vàng.");
            ChatGlobalService.gI().chat(pl, pl.name + " thắng Chẵn/Lẻ và nhận " + Util.format(goldReward) + " Thỏi vàng.");
        }
    }

    private void processLosers(List<Player> players) {
        for (Player pl : players) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null)
                Service.gI().sendThongBao(pl, "Thua rồi, thử lại ván sau.");
        }
    }

    private void addSession() {
        boolean isEven = (this.number % 2 == 0);
        try (Connection conn = ConnectDB.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO chan_le(number, result) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, this.number);
            ps.setBoolean(2, isEven);
            ps.executeUpdate();

            PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM chan_le WHERE id NOT IN (SELECT id FROM (SELECT id FROM chan_le ORDER BY id DESC LIMIT " + KEEP_HISTORY + ") AS tmp)");
            del.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHistoryGame() {
        final String sql = "SELECT number FROM chan_le ORDER BY id DESC LIMIT 10";
        List<Integer> nums = new ArrayList<>();
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                nums.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (nums.isEmpty()) return "Chưa có lịch sử.";
        StringBuilder sb = new StringBuilder();
        sb.append("Các số trước: ");
        for (int i = nums.size() - 1; i >= 0; i--) {
            sb.append(nums.get(i));
            if (i > 0) sb.append(", ");
        }
        return sb.toString();
    }
}
