// package nro.chanle;

// import Player.Player;
// import Server.Client;
// import Server.Maintenance;
// import Server.ServerManager;
// import Server.ServerNotify;
// import Services.MapService;
// import Services.Service;
// import Utils.Logger;
// import com.girlkun.database.GirlkunDB;
// import consts.ConstNpc;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import nro.npc.Npc;
// import nro.npc.NpcManager;


// public class ChanLeManager implements Runnable {

//    private static ChanLeManager instance;
//    public static List<ChanLeModels> pariryList;
//    public static ChanLeModels currentPariry;
//    public static long TIME_RENEW = 60; // seconds
//    public static int TIME_TO_START = 15; // seconds

//    private static int[] numbersBefore = new int[3];
//    private static boolean resultBefore = false;
//    private static int idBefore = -1;

//    public static long time; // seconds

//    public static int timeToStart; // seconds

//    public ChanLeManager() {
//        pariryList = new ArrayList<>();
//    }

//    public static ChanLeManager gI() {
//        if (instance == null) {
//            instance = new ChanLeManager();
//        }
//        return instance;
//    }

//    public void addPlayerEven(Player player) {
//        if (currentPariry != null) {
//            currentPariry.addPlayerEven(player);
//        }
//    }

//    public void addPlayerOdd(Player player) {
//        if (currentPariry != null) {
//            currentPariry.addPlayerOdd(player);
//        }
//    }

//    public String getHistoryGame() {
//        StringBuilder sb = new StringBuilder("");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

//        try (
//                Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM pariry_session ORDER BY time DESC LIMIT 20");) {
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    int i = rs.getInt("id");
//                    sb.append("#").append(i).append(". ");

//                    // Chuyển đổi Timestamp sang LocalDateTime
//                    LocalDateTime dateTime = rs.getTimestamp("time").toLocalDateTime();
//                    sb.append(dateTime.format(formatter)).append(" | ");

//                    int number_1 = rs.getInt("number_1");
//                    int number_2 = rs.getInt("number_2");
//                    int number_3 = rs.getInt("number_3");
//                    sb.append(number_1).append(" - ").append(number_2).append(" - ").append(number_3).append(" | ");
//                    sb.append(" Kết quả: ");
//                    if (rs.getBoolean("result")) {
//                        sb.append("Lẻ");
//                    } else {
//                        sb.append("Chẵn");
//                    }
//                    sb.append("\n");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        return sb.toString();
//    }

//    public String getHistoryPlayer(Player player) {
//        StringBuilder sb = new StringBuilder("");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

//        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT * FROM pariry_players WHERE id_player = ? ORDER BY time DESC LIMIT 10")) {
//            ps.setInt(1, (int) player.id);

//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    int i = rs.getInt("id_session");
//                    sb.append("#").append(i).append(". ");

//                    // Chuyển đổi Timestamp sang LocalDateTime
//                    LocalDateTime dateTime = rs.getTimestamp("time").toLocalDateTime();
//                    sb.append(dateTime.format(formatter)).append(" | ");

//                    if (rs.getBoolean("is_odd")) {
//                        sb.append("Lẻ").append(" | ");
//                    } else {
//                        sb.append("Chẵn").append(" | ");
//                    }

//                    if (rs.getBoolean("is_win")) {
//                        sb.append("Thắng").append(" | ");
//                    } else {
//                        sb.append("Thua").append(" | ");
//                    }

//                    sb.append(rs.getInt("ruby_quantity")).append("\n");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        return sb.toString();
//    }

//    public void addPariry(ChanLeModels pariry) {
//        pariryList.add(pariry);
//    }

//    public void removePariry(ChanLeModels pariry) {
//        pariryList.remove(pariry);
//    }

//    public List<ChanLeModels> getAllPariries() {
//        return pariryList;
//    }

//    @Override
//    public void run() {
//        Npc npc = NpcManager.getNpc((byte)ConstNpc.CHANLE);
//        while (ServerManager.isRunning) {
//            try {
//                if (currentPariry == null && !Maintenance.isRuning) {
//                    PlayNewSession();
//                }
//                while (time > 0) {
//                    time--;
//                    String text = "Còn " + time + " giây nữa sẽ kết thúc phiên chẵn lẻ";
//                    List<Player> players = MapService.gI().getAllPlayerInMap(5);
//                    if (npc != null) {
//                        for (Player pl : players) {
//                            int check = currentPariry.checkPlayer(pl);
//                            if (check != -1) {
//                                text = "Còn " + time + " giây nữa sẽ kết thúc phiên chẵn lẻ. Bạn đã đặt cược vào "
//                                + (check == 0 ? "chẵn" : "lẻ") + " với số tiền là " + pl.cuoc + " thỏi vàng";
//                            }
//                            npc.npcChat(pl, text);
//                        }

//                    }
//                    Thread.sleep(1000);
//                }
//                finishSession();
//                setRubyWin();
//                while (timeToStart > 0) {
//                    timeToStart--;
//                    String text = "Còn " + timeToStart + " giây nữa sẽ bắt đầu phiên chẵn lẻ mới";
//                    List<Player> players = MapService.gI().getAllPlayerInMap(5);
//                    if (npc != null) {
//                        for (Player pl : players) {
//                            npc.npcChat(pl, text);
//                        }
//                    }
//                    if (timeToStart == 10 || timeToStart == 5) {
//                        ServerNotify.gI().notify("Kết quả phiên #" + idBefore + " là: " + numbersBefore[0] + " - " + numbersBefore[1]
//                            + " - "
//                            + numbersBefore[2] + " - "
//                            + (resultBefore == true ? "Lẻ" : " Chẵn"));
//                    }
//                    Thread.sleep(1000);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    private void setRubyWin() {
//        try {
//            getRubyWinAllPlayerNotYetReward();
//        } catch (Exception e) {   e.printStackTrace();
//            Logger.error(e.getMessage());
//        }
//    }

//    public boolean addSession(ChanLeModels pariry) {
//        int[] numbers = pariry.getNumbers();
//        boolean result = pariry.getResult();
//        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement(
//                "INSERT INTO `pariry_session`(`number_1`, `number_2`, `number_3`, `result`) VALUES (?,?,?,?)",
//                Statement.RETURN_GENERATED_KEYS)) { // Set the option to return generated keys

//            ps.setInt(1, numbers[0]);
//            ps.setInt(2, numbers[1]);
//            ps.setInt(3, numbers[2]);
//            ps.setBoolean(4, result);

//            int affectedRows = ps.executeUpdate();
//            if (affectedRows == 0) {
//                throw new SQLException("Lỗi khi add session");
//            }

//            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
//                if (generatedKeys.next()) {
//                    int generatedId = generatedKeys.getInt(1);
//                    pariry.setId(generatedId);
//                } else {
//                    throw new SQLException("Creating pariry session failed, no ID obtained.");
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
 

// public boolean test;
//    public void PlayNewSession() {
//        ChanLeModels newPariry = new ChanLeModels(-1, -1, -1, -1, false);
//        newPariry.generatedResult2();
//        currentPariry = newPariry;
//        addPariry(currentPariry);
//        time = TIME_RENEW;
//        System.out.println("Phiên mới:"
//                + (newPariry.getResult()?"Lẻ":"Chẵn"));//tỉ lệ như lòn :))) 50-50
//    }

//    public void finishSession() {
//        currentPariry.generatedResult();
//        if (addSession(currentPariry)) {
//            numbersBefore = currentPariry.getNumbers();
//            resultBefore = currentPariry.getResult();
//            idBefore = currentPariry.getId();
//            if (currentPariry.getResult() == true) {
//                if (currentPariry.getPlayersOdd() != null && !currentPariry.getPlayersOdd().isEmpty()) {
//                    for (Player pl : currentPariry.getPlayersOdd()) {
//                        addPlayerToNewSession(pl, true, true);
//                        Service.gI().sendThongBaoOK(pl, "Bạn đã thắng " + pl.cuoc * 1.5 + " thỏi vàng");
//                        Service.gI().sendThongBao(pl, "Bạn đã thắng " + pl.cuoc * 1.5 + " thỏi vàng");
//                        pl.cuoc = 0;
//                    }
//                }
//                if (currentPariry.getPlayersEven() != null && !currentPariry.getPlayersEven().isEmpty()) {
//                    for (Player pl : currentPariry.getPlayersEven()) {
//                        addPlayerToNewSession(pl, false, false);
//                        Service.gI().sendThongBaoOK(pl, "Kết quả là lẻ, bạn đã thua " + pl.cuoc + " thỏi vàng");
//                        Service.gI().sendThongBao(pl, "Kết quả là lẻ, bạn đã thua " + pl.cuoc + " thỏi vàng");
                       
//                        pl.cuoc = 0;
//                    }
//                }
//            } else {
//                if (currentPariry.getPlayersEven() != null && !currentPariry.getPlayersEven().isEmpty()) {
//                    for (Player pl : currentPariry.getPlayersEven()) {
//                        addPlayerToNewSession(pl, true, false);
//                        Service.gI().sendThongBaoOK(pl, "Bạn đã thắng " + pl.cuoc * 1.5 + " thỏi vàng");
//                        Service.gI().sendThongBao(pl, "Bạn đã thắng " + pl.cuoc * 1.5 + " thỏi vàng");
//                        pl.cuoc = 0;
//                    }
//                }
//                if (currentPariry.getPlayersOdd() != null && !currentPariry.getPlayersOdd().isEmpty()) {
//                    for (Player pl : currentPariry.getPlayersOdd()) {
//                        addPlayerToNewSession(pl, false, true);
//                        Service.gI().sendThongBaoOK(pl, "Kết quả là chẵn, bạn đã thua " + pl.cuoc + " thỏi vàng");
//                        Service.gI().sendThongBao(pl, "Kết quả là chẵn, bạn đã thua " + pl.cuoc + " thỏi vàng");
//                        pl.cuoc = 0;
//                    }
//                }
//            }
//            currentPariry = null;
//            timeToStart = TIME_TO_START;
//             PlayNewSession();
//        } else {
//            System.out.println("Lỗi khi kết thúc session");
//        }
//    }

//    public void addPlayerToNewSession(Player player, boolean isWin, boolean isOdd) {
//        try (Connection con = GirlkunDB.getConnection()) {
//            try (PreparedStatement ps = con.prepareStatement(
//                    "INSERT INTO `pariry_players`(`id_session`, `id_player`, `ruby_quantity`,`is_odd`, `is_win`, `reward`) VALUES (?,?,?,?,?,?)")) {
//                ps.setInt(1, currentPariry.getId());
//                ps.setInt(2, (int) player.id);
//                ps.setInt(3, player.cuoc);
//                ps.setBoolean(4, isOdd);
//                ps.setBoolean(5, isWin);
//                ps.setBoolean(6, false);
//                ps.executeUpdate();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

//    private void getRubyWinAllPlayerNotYetReward() {
//        Map<Integer, Integer> playerRubies = new HashMap<>();
//        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con
//                .prepareStatement("SELECT * FROM `pariry_players` WHERE `is_win` = 1 AND `reward` = 0")) {
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) { // Dùng vòng lặp để xử lý tất cả các dòng
//                    int id_player = rs.getInt("id_player");
//                    int ruby = rs.getInt("ruby_quantity");
//                    boolean isWin = rs.getBoolean("is_win");
//                    boolean isReward = rs.getBoolean("reward");
//                    if (isWin && !isReward) {
//                        playerRubies.put(id_player, playerRubies.getOrDefault(id_player, 0) + ruby);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

//        Iterator<Map.Entry<Integer, Integer>> itr = playerRubies.entrySet().iterator();
//        while (itr.hasNext()) {
//            Map.Entry<Integer, Integer> entry = itr.next();
//            Integer id_player = entry.getKey();
//            Integer ruby = entry.getValue();

//            Player player = Client.gI().getPlayer(id_player);
//            if (player != null) {
//                player.rubyWin = 0;
//                player.rubyWin += ruby;
//            } else {
//                itr.remove();
//            }
//        }
//    }

//    public void setAfterPlayerReward(Player player) {
//        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con
//                .prepareStatement("UPDATE `pariry_players` SET `reward` = ? WHERE `is_win` = 1 AND `id_player` = ?")) {
//            ps.setBoolean(1, true);
//            ps.setInt(2, (int) player.id);
//            int affectedRows = ps.executeUpdate();

//            if (affectedRows == 0) {
//                System.out.println("Không thể cập nhât cho player: " + player.id + "\n");

//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }

// }
