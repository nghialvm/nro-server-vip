package nro.server;

import Utils.Util;
import jbcd.ConnectDB;
import models.Item.ItemOption;
import nro.map.ItemMap;
import nro.mob.Mob;
import nro.player.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DropManager {

    private static DropManager instance;

    public static DropManager gI() {
        if (instance == null) {
            instance = new DropManager();
        }
        return instance;
    }

    private final List<DropData> drops = new ArrayList<>();

    private DropManager() {
        reload();
    }

    public synchronized void reload() {
        drops.clear();

        String sql = "SELECT * FROM drop_item WHERE active = 1 ORDER BY id ASC";

        try (Connection conn = ConnectDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                DropData d = new DropData();

                d.id = rs.getInt("id");
                d.active = rs.getInt("active") == 1;
                d.mobId = rs.getInt("mob_id");
                d.mapId = rs.getInt("map_id");
                d.itemId = rs.getInt("item_id");
                d.quantity = rs.getInt("quantity");
                d.rateNum = rs.getInt("rate_num");
                d.rateDen = rs.getInt("rate_den");
                d.family = rs.getInt("family");
                d.note = safe(rs.getString("note"));
                d.options = safe(rs.getString("options"));
                d.conditions = safe(rs.getString("conditions"));

                if (d.itemId <= 0) {
                    continue;
                }

                if (d.quantity <= 0) {
                    d.quantity = 1;
                }

                if (d.rateNum <= 0 || d.rateDen <= 0 || d.rateNum > d.rateDen) {
                    continue;
                }

                drops.add(d);
            }

            System.out.println("[DropManager] Loaded " + drops.size() + " drop item.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[DropManager] Load drop_item error: " + e.getMessage());
        }
    }

    public List<ItemMap> getDropItems(Player player, Mob mob, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();

        if (player == null || mob == null || mob.zone == null || mob.zone.map == null) {
            return list;
        }

        int mapId = mob.zone.map.mapId;
        int mobId = mob.tempId;
        int family = getPlayerFamily(player);
        double tyLeMayMan = player.nPoint != null ? player.nPoint.tlMayman : 0;
        boolean useCoBonLa = player.itemTime != null && player.itemTime.isUseCoBonLa;

        List<DropData> cache;
        synchronized (this) {
            cache = new ArrayList<>(drops);
        }

        for (DropData d : cache) {
            if (!d.active) {
                continue;
            }

            if (d.mapId != -1 && d.mapId != mapId) {
                continue;
            }

            if (d.mobId != -1 && d.mobId != mobId) {
                continue;
            }

            if (d.family != -1 && d.family != family) {
                continue;
            }

            if (!checkConditions(player, mob, d.conditions)) {
                continue;
            }

            if (!Util.getChanceFromLuck(tyLeMayMan, d.rateNum, d.rateDen, useCoBonLa)) {
                continue;
            }

            ItemMap itemMap = new ItemMap(
                    mob.zone,
                    d.itemId,
                    d.quantity,
                    x + Util.nextInt(-15, 15),
                    yEnd,
                    player.id
            );

            addOptions(itemMap, d.options);
            list.add(itemMap);
        }

        return list;
    }

    private int getPlayerFamily(Player player) {
        try {
            return player.gender;
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean checkConditions(Player player, Mob mob, String conditions) {
        if (conditions == null || conditions.trim().isEmpty()) {
            return true;
        }

        try {
            String[] arr = conditions.split(";");

            for (String raw : arr) {
                String s = raw.trim();
                if (s.isEmpty()) {
                    continue;
                }

                String[] kv = s.split("=");
                if (kv.length != 2) {
                    continue;
                }

                String key = kv[0].trim().toLowerCase();
                String val = kv[1].trim();

                switch (key) {
                    case "min_power" -> {
                        long min = Long.parseLong(val);
                        if (player.nPoint == null || player.nPoint.power < min) {
                            return false;
                        }
                    }

                    case "max_power" -> {
                        long max = Long.parseLong(val);
                        if (player.nPoint == null || player.nPoint.power > max) {
                            return false;
                        }
                    }

                    case "task_id" -> {
                        int taskId = Integer.parseInt(val);
                        if (player.playerTask == null
                                || player.playerTask.taskMain == null
                                || player.playerTask.taskMain.id != taskId) {
                            return false;
                        }
                    }

                    case "task_index" -> {
                        int taskIndex = Integer.parseInt(val);
                        if (player.playerTask == null
                                || player.playerTask.taskMain == null
                                || player.playerTask.taskMain.index != taskIndex) {
                            return false;
                        }
                    }

                    case "active" -> {
                        boolean needActive = Boolean.parseBoolean(val);
                        if (needActive && !player.isActive()) {
                            return false;
                        }
                    }

                    case "full_dtl" -> {
                        boolean need = Boolean.parseBoolean(val);
                        if (need && !isFullDTL(player)) {
                            return false;
                        }
                    }

                    case "full_dhd" -> {
                        boolean need = Boolean.parseBoolean(val);
                        if (need && !isFullDHD(player)) {
                            return false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean isFullDTL(Player player) {
        try {
            int count = 0;
            for (models.Item.Item item : player.inventory.itemsBody) {
                if (item != null && item.isNotNullItem() && item.isDTL()) {
                    count++;
                }
            }
            return count >= 5;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isFullDHD(Player player) {
        try {
            int count = 0;
            for (models.Item.Item item : player.inventory.itemsBody) {
                if (item != null && item.isNotNullItem() && item.isDHD()) {
                    count++;
                }
            }
            return count >= 5;
        } catch (Exception e) {
            return false;
        }
    }

    private void addOptions(ItemMap itemMap, String options) {
        if (itemMap == null || options == null || options.trim().isEmpty()) {
            return;
        }

        try {
            String[] arr = options.split(";");

            for (String raw : arr) {
                String s = raw.trim();
                if (s.isEmpty()) {
                    continue;
                }

                String[] kv = s.split(":");
                if (kv.length != 2) {
                    continue;
                }

                int optionId = Integer.parseInt(kv[0].trim());
                int param = Integer.parseInt(kv[1].trim());

                itemMap.options.add(new ItemOption(optionId, param));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private static class DropData {
        int id;
        boolean active;
        int mobId;
        int mapId;
        int itemId;
        int quantity;
        int rateNum;
        int rateDen;
        int family;
        String note;
        String options;
        String conditions;
    }
}