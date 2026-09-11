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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import jbcd.ConnectDB;
import models.Item.Item;
import models.Item.ItemService;


public class TaiXiu  implements Runnable{
    
    public int goldTai;
    public int goldXiu;
    public boolean ketquaTai = false;
    public boolean ketquaXiu = false;
    public boolean ketquaTamhoa = false;
    
    public boolean baotri = false;
    public long lastTimeEnd;
    public List<Player> PlayersTai = new ArrayList<>();
    public List<Player> PlayersXiu = new ArrayList<>();
    private static TaiXiu instance;
    public int x,y,z;
    
    public static TaiXiu gI() {
        if (instance == null) {
            instance = new TaiXiu();
        }
        return instance;
    }
    
    public void addPlayerXiu(Player pl){
        if(!PlayersXiu.equals(pl)){
            PlayersXiu.add(pl);
        }
    }
    
    public void addPlayerTai(Player pl){
        if(!PlayersTai.equals(pl)){
            PlayersTai.add(pl);
        }
    }
    
    public void removePlayerXiu(Player pl){
        if(PlayersXiu.equals(pl)){
            PlayersXiu.remove(pl);
        }
    }
    
    public void removePlayerTai(Player pl){
        if(PlayersTai.equals(pl)){
            PlayersTai.remove(pl);
        }
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                if ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) <= 0) {
                    TaiXiu tx = TaiXiu.gI();
                    int x = 1, y = 1, z = 1;
                    boolean ketquaTai = false, ketquaXiu = false, ketquaTamhoa = false;

                    int countTai = tx.PlayersTai.size();
                    int countXiu = tx.PlayersXiu.size();
                    int totalPlayer = countTai + countXiu;

                    boolean noPlayer = totalPlayer == 0;
                    boolean onlyOnePlayer = totalPlayer == 1;

                    boolean raTamHoa = !noPlayer && Util.isTrue(5, 100); // 5% xuất hiện tam hoa

                    if (raTamHoa) {
                        x = y = z = Util.nextInt(1, 6);
                        ketquaTamhoa = true;
                    } else {
                        boolean favorTai;
                        if (noPlayer) {
                            favorTai = Util.isTrue(50, 100); // Không có người → ngẫu nhiên
                        } else if (onlyOnePlayer) {
                            // Chỉ có 1 người chơi → giảm khả năng họ thắng
                            boolean playerIsTai = countTai == 1;
                            favorTai = playerIsTai ? Util.isTrue(30, 100) : Util.isTrue(70, 100);
                        } else {
                            // Nhiều người chơi → chọn cửa người chơi đặt ít hơn
                            if (tx.goldTai > tx.goldXiu) {
                                favorTai = Util.isTrue(30, 100); // Người chơi đặt nhiều Tài → ưu tiên Xỉu
                            } else if (tx.goldTai < tx.goldXiu) {
                                favorTai = Util.isTrue(70, 100); // Người chơi đặt nhiều Xỉu → ưu tiên Tài
                            } else {
                                favorTai = Util.isTrue(50, 100); // Hai bên ngang nhau → ngẫu nhiên
                            }
                        }

                        boolean luaDau = Util.isTrue(50, 100);
                        boolean luaCuoi = Util.isTrue(50, 100);

                        if (luaDau) {
                            // LỪA 2 VIÊN ĐẦU
                            if (favorTai) {
                                // Làm người chơi tưởng Xỉu → đầu nhỏ, cuối lớn
                                do {
                                    x = Util.nextInt(1, 4);
                                    y = Util.nextInt(1, 4);
                                    int sum = x + y;
                                    int minZ = 11 - sum + 1;
                                    if (minZ > 6) continue;
                                    z = luaCuoi ? Util.nextInt(minZ, 6) : Util.nextInt(1, 6);
                                } while (x == y && y == z);
                                ketquaTai = true;
                            } else {
                                // Làm người chơi tưởng Tài → đầu lớn, cuối nhỏ
                                do {
                                    x = Util.nextInt(4, 6);
                                    y = Util.nextInt(4, 6);
                                    int sum = x + y;
                                    int maxZ = 10 - sum;
                                    if (maxZ < 1) continue;
                                    z = luaCuoi ? Util.nextInt(1, maxZ) : Util.nextInt(1, 6);
                                } while (x == y && y == z);
                                ketquaXiu = true;
                            }
                        } else {
                            // KHÔNG LỪA 2 ĐẦU
                            do {
                                x = Util.nextInt(1, 6);
                                y = Util.nextInt(1, 6);
                                z = Util.nextInt(1, 6);
                            } while (x == y && y == z);

                            int tong = x + y + z;
                            if (tong >= 4 && tong <= 10) ketquaXiu = true;
                            else ketquaTai = true;
                        }
                    }

                    // Gán kết quả
                    tx.x = x;
                    tx.y = y;
                    tx.z = z;
                    int tong = x + y + z;

                    // Thông báo lần lượt
                    sendToAll(tx, "|2|Xí Ngầu Đầu Tiên --> " + x);
                    Thread.sleep(2000);
                    sendToAll(tx, "|2|Xí Ngầu Thứ Hai --> " + y);
                    Thread.sleep(3000);
                    sendToAll(tx, "|2|Xí Ngầu Cuối --> " + z);
                    Thread.sleep(1500);

                    // Xử lý kết quả
                    if (ketquaTai) {
                        sendToAll(tx, "|7|Tổng Số : " + tong + " (TÀI)");
                        processWinners(tx.PlayersTai, true, x, y, z, tong);
                        processLosers(tx.PlayersXiu, false, x, y, z, tong);
                    } else if (ketquaXiu) {
                        sendToAll(tx, "|7|Tổng Số : " + tong + " (XỈU)");
                        processWinners(tx.PlayersXiu, false, x, y, z, tong);
                        processLosers(tx.PlayersTai, true, x, y, z, tong);
                    } else if (ketquaTamhoa) {
                        sendToAll(tx, "|7|Tam hoa xuất hiện! Nhà cái ăn sạch!");
                        processTamHoa(tx.PlayersTai, x, y, z, tong);
                        processTamHoa(tx.PlayersXiu, x, y, z, tong);
                    }

                    // Reset
                    for (Player pl : tx.PlayersTai) if (pl != null) pl.goldTai = 0;
                    for (Player pl : tx.PlayersXiu) if (pl != null) pl.goldXiu = 0;
                    tx.goldTai = 0;
                    tx.goldXiu = 0;
                    tx.PlayersTai.clear();
                    tx.PlayersXiu.clear();
                    tx.lastTimeEnd = System.currentTimeMillis() + 33000;
                    addSession(this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void sendToAll(TaiXiu tx, String msg) {
        for (Player pl : tx.PlayersTai)
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null)
                Service.getInstance().sendThongBao(pl, msg);
        for (Player pl : tx.PlayersXiu)
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null)
                Service.getInstance().sendThongBao(pl, msg);
    }
    
    private void processWinners(List<Player> players, boolean isTai, int x, int y, int z, int tong) {
        for (Player pl : players) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null) {
                int goldC = isTai ? pl.goldTai * 190 / 100 : pl.goldXiu * 190 / 100;
                Item ThoiVang = ItemService.gI().createNewItem((short) 457, goldC);
                ThoiVang.addOptionParam(30, 0);
                InventoryService.gI().addItemBag(pl, ThoiVang);
                Service.getInstance().sendThongBao(pl, "|1|Bạn đã chiến thắng!!");
                Service.getInstance().sendThongBao(pl, "|2|Bạn nhận được " + Util.format(goldC) + " Thỏi Vàng");
                ChatGlobalService.gI().chat(pl, pl.name + " đã thắng và nhận được " + Util.format(goldC) + " Thỏi Vàng");
                Service.getInstance().sendMoney(pl);
                InventoryService.gI().sendItemBag(pl);
            }
        }
    }
    
    private void processLosers(List<Player> players, boolean taiRa, int x, int y, int z, int tong) {
        for (Player pl : players) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null) {
                Service.getInstance().sendThongBao(pl,
                    "|7|Trắng tay gòi, chơi lại đi!!!");
            }
        }
    }
    
    private void processTamHoa(List<Player> players, int x, int y, int z, int tong) {
        for (Player pl : players) {
            if (pl != null && Client.gI().getPlayerByName(pl.name) != null) {
                Service.getInstance().sendThongBao(pl,
                    "Số hệ thống quay ra\n" + x + " : " + y + " : " + z +
                    "\n(TAM HOA)\n\n|7|Hahaha Nhà cái lụm hết nha!!!");
            }
        }
    }
    
    public String getHistoryGame() {
        StringBuilder sb = new StringBuilder("");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM tai_xiu ORDER BY time DESC LIMIT 20")) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int i = rs.getInt("id");
                    sb.append("#").append(i).append(". ");

                    // Chuyển đổi Timestamp sang LocalDateTime
                    LocalDateTime dateTime = rs.getTimestamp("time").toLocalDateTime();
                    sb.append(dateTime.format(formatter)).append(" | ");

                    // Lấy 3 số xúc xắc
                    int number_1 = rs.getInt("number_1");
                    int number_2 = rs.getInt("number_2");
                    int number_3 = rs.getInt("number_3");
                    sb.append(number_1).append(" - ").append(number_2).append(" - ").append(number_3).append(" | ");

                    // Tính tổng và xác định kết quả
                    int sum = number_1 + number_2 + number_3;
                    sb.append("Kết quả: ");
                    if (number_1 == number_2 && number_2 == number_3) {
                        sb.append("Tam Hoa");
                    } else if (sum > 10) {
                        sb.append("Tài");
                    } else {
                        sb.append("Xỉu");
                    }
                    sb.append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    
    private boolean isOdd;
    public boolean getResult() {
    // 0 = even, 1 = odd
        return isOdd;
    }
    
    private int id;
    public void setId(int id) {
        this.id = id;
    }
    
    private boolean addSession(TaiXiu taixiu) {
        boolean result = taixiu.getResult();
        try (Connection conn = ConnectDB.getConnection()) {
            // 1. Insert bản ghi mới
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO `tai_xiu`(`number_1`, `number_2`, `number_3`, `result`) VALUES (?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, x);
                ps.setInt(2, y);
                ps.setInt(3, z);
                ps.setBoolean(4, result);

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Lỗi khi add session");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        taixiu.setId(generatedId);
                    } else {
                        throw new SQLException("Creating session failed, no ID obtained.");
                    }
                }
            }

            // 2. Xóa các dòng cũ, chỉ giữ lại 20 bản ghi mới nhất
            try (PreparedStatement deleteOld = conn.prepareStatement(
                    "DELETE FROM tai_xiu WHERE id NOT IN (" +
                    "SELECT id FROM (SELECT id FROM tai_xiu ORDER BY id DESC LIMIT 20) AS recent_ids)")) {
                deleteOld.executeUpdate();
            }

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
