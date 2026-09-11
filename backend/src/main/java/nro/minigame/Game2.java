package nro.minigame;

import nro.player.Player;
import nro.server.Client;
import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author MaiTienDung
 */

public class Game2 {

    // Mini game
    public static byte TIME_START_GAME = 8;
    public static byte TIME_END_GAME = 22;
    public static byte TIME_MINUTES_GAME = 5;
    // SETTING GAME
    public long second = TIME_MINUTES_GAME * 60;
    public long currlast = System.currentTimeMillis();
    public long money = 0;
    public long min = 0;
    public long max = 99;

    // KẾT QUẢ
    public long result = 0;
    public long result_next = Util.nextInt((int) min, (int) max);
    // GIA
    public long price = 1;
    boolean giftGiven = false;
    // PLAYER NAME WIN

    public String result_name;

    public List<GameData> players = new ArrayList<>();

    public Timer timer;
    public TimerTask task;
    protected boolean actived = false;

    public void close() {
        try {
            actived = false;
            task.cancel();
            timer.cancel();
            task = null;
            timer = null;
        } catch (Exception e) {
            e.printStackTrace();
            task = null;
            timer = null;
        }
    }

    public void active(int delay) {
        if (!actived) {
            actived = true;
            this.timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Game2.this.update();
                }
            };
            this.timer.schedule(task, delay, delay);
        }
    }

    public void update() {
        try {
            if (second > 0) {
                second--;
                if (second <= 0) {
                    currlast = System.currentTimeMillis();
                }
//                Thread.sleep(1000);
            }
            if (second <= 0 && Util.canDoWithTime(currlast, 10000)) {
                ResetGame((int) result_next, (int) TIME_MINUTES_GAME * 60);
                result_next = Util.nextInt((int) min, (int) max);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newData(Player player, int point) {
//        if (player.inventory.getGemAndRuby() < 5000) {
//            Service.gI().sendThongBao(player, "Bạn không đủ 5000 ngọc để thực hiện");
//            return;
//        }
//        Item thoivang = InventoryServiceNew.gI().findItemBag(player, 457);
//        int sl = 10;
//        if (thoivang == null) {
//            Service.gI().sendThongBao(player, "không có thỏi vàng ");
//            return;
//        }
//        if (thoivang.quantity < sl) {
//            Service.gI().sendThongBao(player, "Không đủ: " + thoivang.template.name);
//            return;
//        }
        if (players.stream().filter(d -> d.id == player.id).toList().size() >= 10) {
            Service.gI().sendThongBao(player, "Bạn đã chon 10 số rồi không thể chọn thêm");
            return;
        }
        players.stream().filter(d -> d.id == player.id).forEach((game)
                -> {
            if (game.id == player.id && game.point == point) {
                Service.gI().sendThongBao(player, "Số này bạn đã chọn rồi vui lòng chọn số khác.");
            }
        });

        if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {

            GameData data = new GameData();
            data.id = (int) player.id;
            data.point = point;
            players.add(data);
            money = 1;// add lên
            Service.gI().sendMoney(player);
            Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);

        }
    }

    //    }
    public String strNumber(int id) {
        String number = "";
        List<GameData> pl = players.stream().filter(d -> d.id == id).toList();
        for (int i = 0; i < pl.size(); i++) {
            GameData d = pl.get(i);
            if (d.id == id) {
                number += d.point + (i >= pl.size() - 1 ? "" : ",");
            }
        }
        return number;
    }

    public String strFinish(int id) {
        String finish = "Con số chúng thưởng là " + result + " chúc bạn may mắn lần sau";
        return finish;
    }

    public void ResetGame(int result, int second) {
        this.result = result;
        this.second = second;
        this.result_name = "";
        players.stream().filter(g -> g.point == result).forEach((g) -> {
            Player player = Client.gI().getPlayerByID(g.id);
            result_name += (player != null ? player.name : "") + ",";
        });
        if (result_name.length() > 0) {
            result_name = result_name != null ? result_name.substring(0, result_name.length() - 1) : null;
        }
        players.forEach((g)
                -> {
            Player player = Client.gI().getPlayerByID(g.id);
            if (player != null) {
                Service.gI().showYourNumber(player, "", result + "", strFinish(g.id), 1);
            }
        });
        giftGiven = false;
        // Trả thưởng...
//        money = 450;
        money = 0;
        players.clear();
    }
}
