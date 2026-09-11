package nro.minigame;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.ChatGlobalService;
import nro.services.Service;
import Utils.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import models.Item.Item;
import models.Item.ItemOption;
import models.Item.ItemService;



public class ChonAiDay implements Runnable {

    public int goldNormar;
    public int goldVip;
    public long lastTimeEnd;
    public List<Player> PlayersNormar = new ArrayList<>();
    public List<Player> PlayersVIP = new ArrayList<>();
    private static ChonAiDay instance;

    public static ChonAiDay gI() {
        if (instance == null) {
            instance = new ChonAiDay();
        }
        return instance;
    }

    public void addPlayerVIP(Player pl) {
        if (!PlayersVIP.equals(pl)) {
            PlayersVIP.add(pl);
        }
    }

    public void addPlayerNormar(Player pl) {
        if (!PlayersNormar.equals(pl)) {
            PlayersNormar.add(pl);
        }
    }

    public void removePlayerVIP(Player pl) {
        if (PlayersVIP.equals(pl)) {
            PlayersVIP.remove(pl);
        }
    }

    public void removePlayerNormar(Player pl) {
        if (PlayersNormar.equals(pl)) {
            PlayersNormar.remove(pl);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                long timeToEnd = (ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000;
                if (timeToEnd <= 0) {
                    performChonAiDayWinnerLogic();
                    resetChonAiDayData();
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void performChonAiDayWinnerLogic() {
        List<Player> listN = new ArrayList<>();
        List<Player> listV = new ArrayList<>();
        // Xử lý chiến thắng cho Chọn Ai Đây giải thường
        ChonAiDay.gI().PlayersNormar.stream()
                .filter(p -> p != null && p.goldNormar != 0)
                .sorted(Comparator.comparing(p -> Math.ceil(((double) p.goldNormar / ChonAiDay.gI().goldNormar) * 100), Comparator.reverseOrder()))
                .forEach(cl -> listN.add(cl));
        if (listN.size() > 5) {
            Player pl = listN.get(Util.nextInt(0, 5));
            if (Util.isTrue(90, 100)) {
                if (pl != null) {
                    processChonAiDayWinner(pl, ChonAiDay.gI().goldNormar, 90);
                }
            } else {
                Service.gI().sendThongBaoAllPlayer("Xin lỗi bạn đã không giành chiến thắng giải thường");
            }
        } else if (!listN.isEmpty()) {
            Player pl = listN.get(Util.nextInt(0, listN.size() - 1));
            if (Util.isTrue(90, 100)) {
                if (pl != null) {
                    processChonAiDayWinner(pl, ChonAiDay.gI().goldNormar, 90);
                }
            } else {
                Service.gI().sendThongBaoAllPlayer("Xin lỗi bạn đã không giành chiến thắng giải thường");
            }
        }
        listN.clear();
        // Xử lý chiến thắng cho Chọn Ai Đây giải VIP
        ChonAiDay.gI().PlayersVIP.stream()
                .filter(p -> p != null && p.goldVIP != 0)
                .sorted(Comparator.comparing(p -> Math.ceil(((double) p.goldVIP / ChonAiDay.gI().goldVip) * 100), Comparator.reverseOrder()))
                .forEach(cl -> listV.add(cl));

        if (listV.size() > 5) {
            Player pl = listV.get(Util.nextInt(0, 5));
            if (Util.isTrue(90, 100)) {
                if (pl != null) {
                    processChonAiDayWinner(pl, ChonAiDay.gI().goldVip, 90);
                }
            } else {
                Service.gI().sendThongBaoAllPlayer("Xin lỗi bạn đã không giành chiến thắng giải VIP");
            }
        } else if (!listV.isEmpty()) {
            Player pl = listV.get(Util.nextInt(0, listV.size() - 1));
            if (Util.isTrue(90, 100)) {
                if (pl != null) {
                    processChonAiDayWinner(pl, ChonAiDay.gI().goldVip, 90);
                }
            } else {
                Service.gI().sendThongBaoAllPlayer("Xin lỗi bạn đã không giành chiến thắng giải VIP");
            }
        }

        listV.clear();
    }

    private void processChonAiDayWinner(Player player, int gold, int percentage) {
        String type = player.isFounder() ? "VIP" : "thường";
        String message = player.name + " đã chiến thắng Chọn Ai Đây giải " + type;
        int goldC = gold * percentage / 100;
        ChatGlobalService.gI().chat(player, message);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã dành chiến thắng Chọn Ai Đây giải " + type + " và nhận được " + goldC +" thỏi vàng");
        Item it = ItemService.gI().createNewItem((short) 457, goldC);
        it.itemOptions.add(new ItemOption(93, 15));
        InventoryService.gI().addItemBag(player, it);
        InventoryService.gI().sendItemBag(player);
    }    

    private void resetChonAiDayData() {
        for (Player pl : ChonAiDay.gI().PlayersNormar) {
            if (pl != null) {
                pl.goldVIP = 0;
                pl.goldNormar = 0;
            }
        }
        for (Player pl : ChonAiDay.gI().PlayersVIP) {
            if (pl != null) {
                pl.goldVIP = 0;
                pl.goldNormar = 0;
            }
        }
        ChonAiDay.gI().goldNormar = 0;
        ChonAiDay.gI().goldVip = 0;
        ChonAiDay.gI().PlayersNormar.clear();
        ChonAiDay.gI().PlayersVIP.clear();
        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
    }
}
