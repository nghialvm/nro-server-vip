package nro.npc;

import Utils.Functions;
import consts.ConstNpc;
import consts.ConstTask;
import nro.player.Player;
import nro.server.Manager;
import nro.services.TaskService;
import java.util.ArrayList;
import java.util.List;



public class NpcManager {

    public static Npc getByIdAndMap(int id, int mapId) {
        for (Npc npc : Manager.NPCS) {
            if (npc.tempId == id && npc.mapId == mapId) {
                return npc;
            }
        }
        return null;
    }

    public static Npc getNpc(byte tempId) {
        for (Npc npc : Manager.NPCS) {
            if (npc.tempId == tempId) {
                return npc;
            }
        }
        return null;
    }
    
    public static Npc Autochatnpc() {
        for (Npc npc : Manager.NPCS) {
            switch (npc.tempId) {
                case ConstNpc.MR_POPO:
                    npc.npcChat("Chúng ta gặp rắc rối rồi...");
                    Functions.sleep(5000);
                    npc.npcChat("Chỉ còn 70 giờ nữa...");
                    Functions.sleep(5000);
                    npc.npcChat("Toàn bộ sinh vật sống sẽ bị hủy diệt...");
                    Functions.sleep(5000);
                    npc.npcChat("Thượng Đế nhờ tôi báo tin cho các cậu biết...");
                    Functions.sleep(5000);
                    npc.npcChat("Hãy nhanh chóng lên đường giải cứu trái đất.");
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    public static List<Npc> getNpcsByMapPlayer(Player player) {
        List<Npc> list = new ArrayList<>();
        if (player.zone != null) {
            for (Npc npc : player.zone.map.npcs) {
                if (npc.tempId == ConstNpc.QUA_TRUNG && player.mabuEgg == null && player.zone.map.mapId == (21 + player.gender)) {
                    continue;
                } else if (npc.tempId == ConstNpc.DUA_HAU && player.duahau == null && player.zone.map.mapId == (21 + player.gender)) {
                    continue;
                } else if (npc.tempId == ConstNpc.CALICK && TaskService.gI().getIdTask(player) < ConstTask.TASK_21_0) {
                    continue;
                } else if (npc.tempId == ConstNpc.JACO && TaskService.gI().getIdTask(player) < ConstTask.TASK_13_0) {
                    continue;
                } else if (npc.tempId == ConstNpc.QUOC_VUONG && player.nPoint.power < 17_900_000_000L) {
                    continue;
                }
                list.add(npc);
            }
        }
        return list;
    }
    
}
