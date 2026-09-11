package nro.minigame;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.server.Client;
import nro.services.Service;
import Utils.TimeUtil;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import jbcd.data.DatabaseUpdater;
import models.Item.Item;
import models.Item.ItemService;



public class ConSoMayMan {
    //TIME START
    public static long TIME_START_8H;
    public static long TIME_STOP_22H;
    
    public static final byte HOUR_START_8H = 8;
    public static final byte MIN_START_8H = 0;
    public static final byte SECOND_START_8H = 0;

    public static final byte HOUR_STOP_22H = 22;
    public static final byte MIN_STOP_22H = 0;
    public static final byte SECOND_STOP_22H = 0;
    
    public static byte TIME_MINUTES_GAME = 1;
    
    public long second = 60;
    public long currlast = System.currentTimeMillis();
    
    public int vnd = 100000;
    
    public int hongngoc = 30000;
    
    public int thoivang = 150;
    
    public long min = 0;
    public long max = 99;
    
    public long result = 0;
    public long result_next = Util.nextInt((int) min, (int) max);
    
    public int giaSo_Ruby = 1000;
    public int giaSo_Gold = 10;
    
    public String result_name;

    public List<ConSoMayManData> players = new ArrayList<>();

    public List<Player> conSoMayManNgoc = new ArrayList<>();
    public List<Player> conSoMayManVang = new ArrayList<>();
    
    private static ConSoMayMan i; 
    
    private int day = -1;
    
    public static ConSoMayMan gI() {
        if (i == null) {
            i = new ConSoMayMan();
        }
        i.SetTimeStartConSoMayMan();
        return i;
    }
    
    public void SetTimeStartConSoMayMan() {
        if (i.day == -1 || i.day != TimeUtil.getCurrDay()) {
            i.day = TimeUtil.getCurrDay();
            try {
                TIME_START_8H = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_START_8H + ":" + MIN_START_8H + ":" + SECOND_START_8H, "dd/MM/yyyy HH:mm:ss");
                TIME_STOP_22H = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_STOP_22H + ":" + MIN_STOP_22H + ":" + SECOND_STOP_22H, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception ignored) {
            }
        }
    }

    public void addPlayerVang(Player pl) { // thêm player chơi = vàng vô list
        if (!conSoMayManVang.contains(pl)) {
            conSoMayManVang.add(pl);
        }
    }

    public void addPlayerNgoc(Player pl) { // thêm player chơi = ngọc vô list
        if (!conSoMayManNgoc.contains(pl)) {
            conSoMayManNgoc.add(pl);
        }
    }

    public void removePlayeVang(Player pl) { // xóa player chơi = vàng vô list
        conSoMayManVang.removeIf(player -> player == pl);
    }

    public void removePlayerNgoc(Player pl) { // thêm player chơi = ngọc vô list
        conSoMayManNgoc.removeIf(player -> player == pl);
    }

    public List<Long> dataKQ_CSMM = new ArrayList<>();

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
            task = null;
            timer = null;
        }
    }

    public void activate(int delay) {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                ConSoMayMan.this.update();
            }
        };
        timer.schedule(task, delay, delay);
    }

    public void update() {
        try {
            if (second > 0) {
                second--;
                if (second <= 0) {
                    currlast = System.currentTimeMillis();
                }
            }
            if (second <= 0 && Util.canDoWithTime(currlast, 10000)) {
                ResetGame((int) result_next, (int) 60);
                result_next = Util.nextInt((int) min, (int) max);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newData(Player player, int point, int VangorNgoc) {// vang = 0; ngoc = 1; vnd = 2;
        Item thoiVang = InventoryService.gI().findItemBag(player, (short) 457);
        switch (VangorNgoc) {
            case 0:
                if (thoiVang == null) {
                    Service.getInstance().sendThongBao(player, "Bạn không có thỏi vàng!");
                    return;
                } else if (thoiVang.quantity < this.giaSo_Gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng!");
                    return;
                }   
                break;
            case 1:
                if (player.inventory.ruby < 1000) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ 1000 hồng ngọc để thực hiện");
                    return;
                }   
                break;
            case 2:
                if (player.getSession().vnd < 10000) {
                    Service.gI().sendThongBao(player, "Bạn không đủ 10000 VNĐ");
                    return;
                }
                break;
            default:
                break;
        }
        if (players.stream().filter(d -> d.id == player.id).toList().size() >= 10) {
            Service.gI().sendThongBao(player, "Bạn đã chọn 10 số rồi không thể chọn thêm");
            return;
        }
        players.stream().filter(d -> d.id == player.id).forEach((game)
                -> {
            if (game.id == player.id && game.point == point) {
                Service.gI().sendThongBao(player, "Số này bạn đã chọn rồi vui lòng chọn số khác.");
            }
        });
        if (VangorNgoc == 0) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManNgoc = 1;
                data.point = point;
                players.add(data);
                InventoryService.gI().subQuantityItemsBag(player, thoiVang, giaSo_Gold);
                InventoryService.gI().sendItemBag(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 1) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManVang = 0;
                data.point = point;
                players.add(data);
                player.inventory.ruby -= (giaSo_Ruby);
                Service.gI().sendMoney(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 2) {
            if (DatabaseUpdater.subVND_byPlayer(player, 10000)) {
                if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                    ConSoMayManData data = new ConSoMayManData();
                    data.id = (int) player.id;
                    data.conSoMayManVang = 2;
                    data.point = point;
                    players.add(data);
                    Service.gI().sendMoney(player);
                    Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
                }
            }
        }
    }

    public void ramdom1SoLe(Player player, int VangorNgoc) { // ngọc
        Random random = new Random();
        int point = random.nextInt(50) * 2 + 1;  // Lấy số lẻ từ 1 đến 99
        Item thoiVang = InventoryService.gI().findItemBag(player, (short) 457);
        switch (VangorNgoc) {
            case 0:
                if (thoiVang == null) {
                    Service.getInstance().sendThongBao(player, "Bạn không có thỏi vàng!");
                    return;
                } else if (thoiVang.quantity < this.giaSo_Gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng!");
                    return;
                }   
                break;
            case 1:
                if (player.inventory.ruby < 1000) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ 1000 hồng ngọc để thực hiện");
                    return;
                }   
                break;
            case 2:
                if (player.getSession().vnd < 10000) {
                    Service.gI().sendThongBao(player, "Bạn không đủ 10000 VNĐ");
                    return;
                }
                break;
            default:
                break;
        }
        if (players.stream().filter(d -> d.id == player.id).toList().size() >= 10) {
            Service.gI().sendThongBao(player, "Bạn đã chọn 10 số rồi không thể chọn thêm");
            return;
        }
        players.stream().filter(d -> d.id == player.id).forEach((game) -> {
            if (game.id == player.id && game.point == point) {
                // Nếu số đã chọn trùng, thực hiện đệ quy để chọn số mới
                this.ramdom1SoLe(player, VangorNgoc);
            }
        });
        if (VangorNgoc == 0) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManNgoc = 1;
                data.point = point;
                players.add(data);
                InventoryService.gI().subQuantityItemsBag(player, thoiVang, giaSo_Gold);
                InventoryService.gI().sendItemBag(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 1) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManVang = 0;
                data.point = point;
                players.add(data);
                player.inventory.ruby -= (giaSo_Ruby);
                Service.gI().sendMoney(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 2) {
            if (DatabaseUpdater.subVND_byPlayer(player, 10000)) {
                if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                    ConSoMayManData data = new ConSoMayManData();
                    data.id = (int) player.id;
                    data.conSoMayManVang = 2;
                    data.point = point;
                    players.add(data);
                    Service.gI().sendMoney(player);
                    Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
                }
            }
        }
    }

    public void ramdom1SoChan(Player player, int VangorNgoc) { // ngọc
        Random random = new Random();
        int point = random.nextInt(50) * 2;  // Lấy số lẻ từ 1 đến 99
        Item thoiVang = InventoryService.gI().findItemBag(player, (short) 457);
        switch (VangorNgoc) {
            case 0:
                if (thoiVang == null) {
                    Service.getInstance().sendThongBao(player, "Bạn không có thỏi vàng!");
                    return;
                } else if (thoiVang.quantity < this.giaSo_Gold) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng!");
                    return;
                }   
                break;
            case 1:
                if (player.inventory.ruby < 1000) {
                    Service.getInstance().sendThongBao(player, "Bạn không đủ 1000 hồng ngọc để thực hiện");
                    return;
                }   
                break;
            case 2:
                if (player.getSession().vnd < 10000) {
                    Service.gI().sendThongBao(player, "Bạn không đủ 10000 VNĐ");
                    return;
                }
                break;
            default:
                break;
        }
        if (players.stream().filter(d -> d.id == player.id).toList().size() >= 10) {
            Service.gI().sendThongBao(player, "Bạn đã chọn 10 số rồi không thể chọn thêm");
            return;
        }
        players.stream().filter(d -> d.id == player.id).forEach((game) -> {
            if (game.id == player.id && game.point == point) {
                // Nếu số đã chọn trùng, thực hiện đệ quy để chọn số mới
                this.ramdom1SoChan(player, VangorNgoc);
            }
        });
        if (VangorNgoc == 0) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManNgoc = 1;
                data.point = point;
                players.add(data);
                InventoryService.gI().subQuantityItemsBag(player, thoiVang, giaSo_Gold);
                InventoryService.gI().sendItemBag(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 1) {
            if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                ConSoMayManData data = new ConSoMayManData();
                data.id = (int) player.id;
                data.conSoMayManVang = 0;
                data.point = point;
                players.add(data);
                player.inventory.ruby -= (giaSo_Ruby);
                Service.gI().sendMoney(player);
                Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
            }
        }
        if (VangorNgoc == 2) {
            if (DatabaseUpdater.subVND_byPlayer(player, 10000)) {
                if (!players.stream().anyMatch(game -> game.id == player.id && game.point == point)) {
                    ConSoMayManData data = new ConSoMayManData();
                    data.id = (int) player.id;
                    data.conSoMayManVang = 2;
                    data.point = point;
                    players.add(data);
                    Service.gI().sendMoney(player);
                    Service.gI().showYourNumber(player, strNumber((int) player.id), null, null, 0);
                }
            }
        }
    }

    public String strNumber(int id) {
        String number = "";
        List<ConSoMayManData> pl = players.stream().filter(d -> d.id == id).toList();
        for (int i = 0; i < pl.size(); i++) {
            ConSoMayManData d = pl.get(i);
            if (d.id == id) {
                number += d.point + (i >= pl.size() - 1 ? "" : ",");
            }
        }
        return number;
    }

    public String strFinish(int id) {
        String finish = "Con số chúng thưởng là " + result + " chúc bạn may mắn lần sau";
        for (ConSoMayManData g : players) {
            if (id == g.id && g.point == result && g.conSoMayManNgoc == 1) {
                Player player = Client.gI().getPlayerByID(g.id);
                Item thoiVang = ItemService.gI().createNewItem((short) 457, (int) MiniGame.gI().MiniGame_S1.thoivang);
                finish = "Chúc mừng " + (player == null ? "NULL" : player.name) + " đã thắng " + Util.format(MiniGame.gI().MiniGame_S1.thoivang * players.stream().filter(d -> d.id == player.id).toList().size()) + " thỏi vàng với con số may mắn " + result;
                InventoryService.gI().addItemBag(player, thoiVang);
                InventoryService.gI().sendItemBag(player);
                break;
            }
            if (id == g.id && g.point == result && g.conSoMayManVang == 0) {
                Player player = Client.gI().getPlayerByID(g.id);
                finish = "Chúc mừng " + (player == null ? "NULL" : player.name) + " đã thắng " + Util.format(MiniGame.gI().MiniGame_S1.hongngoc * players.stream().filter(d -> d.id == player.id).toList().size()) + " hồng ngọc với con số may mắn " + result;
                player.inventory.ruby += hongngoc;
                Service.gI().sendMoney(player);
                break;
            }
            if (id == g.id && g.point == result && g.conSoMayManVang == 2) {
                Player player = Client.gI().getPlayerByID(g.id);
                finish = "Chúc mừng " + (player == null ? "NULL" : player.name) + " đã thắng " + Util.format(MiniGame.gI().MiniGame_S1.vnd * players.stream().filter(d -> d.id == player.id).toList().size()) + " VNĐ với con số may mắn " + result;
                DatabaseUpdater.addVND_byPlayer(player, vnd);
                Service.gI().sendMoney(player);
                break;
            }
        }
        return finish;
    }

    public void ResetGame(int result, int second) {
        this.result = result;
        dataKQ_CSMM.add(this.result);
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
        hongngoc = 30000;
        thoivang = 150;
        vnd = 100000;
        players.clear();
    }
}
