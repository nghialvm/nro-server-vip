package nro.map.RankSuper;



import nro.player.Player;
import nro.services.NpcService;
import nro.services.Service;
import consts.ConstTaskBadges;
import java.util.ArrayList;
import java.util.List;
import nro.badges.BadgesTaskService;

public class SuperRank {

    private Player player;
    public int rank;
    public int win;
    public int lose;
    private final List<String> history;
    private final List<Long> lastTime;
    public long lastTimePK;
    public long lastTimeReward;
    public int ticket = 3;

    public SuperRank(Player player) {
        this.player = player;
        this.history = new ArrayList<>();
        this.lastTime = new ArrayList<>();
    }

    /**
     * Ghi lại lịch sử (tối đa 5 bản ghi)
     */
    public void addHistory(String text, long time) {
        if (history.size() > 4) {
            history.remove(0);
            lastTime.remove(0);
        }
        history.add(text);
        lastTime.add(time);
    }
    
    // Optional: Getter để đọc dữ liệu nếu cần từ bên ngoài
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    public List<Long> getLastTime() {
        return new ArrayList<>(lastTime);
    }

    // Optional: Set player nếu bạn cần liên kết
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void reward() {
        int rw = SuperRankService.gI().reward(rank);
        if (rw != -1) {
            NpcService.gI().createTutorial(player, -1, "Bạn đang ở TOP " + rank + " võ đài Siêu Hạng, được thưởng " + rw + " hồng ngọc");
            if (rank == 1) {
                BadgesTaskService.updateCountBagesTask(player, ConstTaskBadges.CAO_THU_SIEU_HANG, 1);
            }
            player.inventory.ruby += rw;
            Service.gI().sendMoney(player);
        }
        lastTimeReward = System.currentTimeMillis();
    }

    /**
     * Dọn dẹp toàn bộ dữ liệu
     */
    public void dispose() {
        history.clear();
        lastTime.clear();
        win = -1;
        lose = -1;
        lastTimePK = -1;
        player = null;
    }
}
