package nro.map.DragonBallNamec;



import Utils.Logger;
import Utils.Util;
import consts.ConstNpc;
import consts.ConstPlayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.Item.Item;
import models.Item.ItemMapService;
import nro.map.ItemMap;
import nro.map.Zone;
import nro.player.Player;
import nro.server.Client;
import nro.server.Manager;
import nro.server.ServerManager;
import nro.services.Fun.ChangeMapService;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;

public class NgocRongNamec implements Runnable {

    private static NgocRongNamec instance;

    public static NgocRongNamec gI() {
        if (instance == null) {
            instance = new NgocRongNamec();
            instance.initNgocRongNamec((byte) 0);
        }
        return instance;
    }

    public int mapNrNamec[] = {-1, -1, -1, -1, -1, -1, -1};
    public String nameNrNamec[] = {"", "", "", "", "", "", ""};
    public byte zoneNrNamec[] = {-1, -1, -1, -1, -1, -1, -1};
    public String pNrNamec[] = {"", "", "", "", "", "", ""};
    public int idpNrNamec[] = {-1, -1, -1, -1, -1, -1, -1};

    public long timeNrNamec = 0;
    public boolean firstNrNamec = true;
    public long tOpenNrNamec = 0;
    public long lastTimeReinit;
    public boolean isReinit;

    private long lastTimeReInitNRNM = System.currentTimeMillis();

    // 7 8 9 10 11 12 13 25 31 32 33 34 43
    public boolean isMapNRNM(int mapId) {
        return (mapId >= 7 && mapId <= 13)
                || mapId == 25
                || (mapId >= 31 && mapId <= 34)
                || mapId == 43;
    }

    private String formatNamekLog(String name, String mapName, int mapId, int zoneId, int x, int y) {
        return String.format(
                "%-8s | map=%-28s(%02d) | khu=%-3d | x=%-5d | y=%-5d",
                name,
                mapName,
                mapId,
                zoneId,
                x,
                y
        );
    }

    private String formatNamekPlayerLog(String action, Player player, int star, int x, int y) {
        if (player == null || player.zone == null || player.zone.map == null) {
            return action + " | player=null";
        }

        return String.format(
                "%-12s | player=%-18s | %-5s | map=%-28s(%02d) | khu=%-3d | x=%-5d | y=%-5d",
                action,
                player.name,
                star + " sao",
                player.zone.map.mapName,
                player.zone.map.mapId,
                player.zone.zoneId,
                x,
                y
        );
    }

    public void initNgocRongNamec(byte type) {
        // type 0: INIT NGỌC RỒNG
        // type 1: INIT HÓA THẠCH NGỌC RỒNG

        ArrayList<Integer> listMap = new ArrayList<>();
        listMap.add(7);
        listMap.add(8);
        listMap.add(9);
        listMap.add(10);
        listMap.add(11);
        listMap.add(12);
        listMap.add(13);
        listMap.add(25);
        listMap.add(31);
        listMap.add(32);
        listMap.add(33);
        listMap.add(34);
        listMap.add(43);

        Logger.title(type == (byte) 0 ? "INIT NGỌC RỒNG NAMEK" : "INIT HÓA THẠCH NAMEK");

        for (byte i = 0; i < (byte) 7; i++) {
            int index = Util.nextInt(0, listMap.size() - 1);
            int mapId = listMap.get(index);
            int idZone = Util.nextInt(0, Manager.MAPS.get(mapId).zones.size() - 1);

            mapNrNamec[i] = mapId;
            nameNrNamec[i] = Manager.MAPS.get(mapId).mapName;
            zoneNrNamec[i] = (byte) idZone;

            Zone zone = Manager.MAPS.get(mapId).zones.get(idZone);

            int x = Util.nextInt(100, zone.map.mapWidth - 100);
            int y = zone.map.yPhysicInTop(x, 100);

            if (type == (byte) 0) {
                ItemMap itemMap = new ItemMap(zone, i + 353, 1, x, y, -1);
                Service.gI().dropItemMap(zone, itemMap);

                Logger.namek(formatNamekLog(
                        (i + 1) + " sao",
                        zone.map.mapName,
                        zone.map.mapId,
                        zone.zoneId,
                        x,
                        y
                ));
            } else {
                ItemMap itemMap = new ItemMap(zone, 362, 1, x, y, -1);
                Service.gI().dropItemMap(zone, itemMap);

                Logger.namek(formatNamekLog(
                        "Hóa thạch",
                        zone.map.mapName,
                        zone.map.mapId,
                        zone.zoneId,
                        x,
                        y
                ));
            }

            listMap.remove(index);
        }
    }

    public void reInitNgocRongNamec() {
        ArrayList<Integer> listMap = new ArrayList<>();
        listMap.add(7);
        listMap.add(8);
        listMap.add(9);
        listMap.add(10);
        listMap.add(11);
        listMap.add(12);
        listMap.add(13);
        listMap.add(25);
        listMap.add(31);
        listMap.add(32);
        listMap.add(33);
        listMap.add(34);
        listMap.add(43);

        Logger.title("REINIT NGỌC RỒNG NAMEK");

        for (byte i = 0; i < (byte) 7; i++) {
            if (NgocRongNamec.gI().pNrNamec[i].equals("")) {
                int index = Util.nextInt(0, listMap.size() - 1);
                int mapId = listMap.get(index);
                int idZone = Util.nextInt(0, Manager.MAPS.get(mapId).zones.size() - 1);

                mapNrNamec[i] = mapId;
                nameNrNamec[i] = Manager.MAPS.get(mapId).mapName;
                zoneNrNamec[i] = (byte) idZone;

                Zone zone = Manager.MAPS.get(mapId).zones.get(idZone);

                int x = Util.nextInt(100, zone.map.mapWidth - 100);
                int y = zone.map.yPhysicInTop(x, 100);

                if (this.isReinit && this.lastTimeReinit - System.currentTimeMillis() > 0) {
                    ItemMap itemMap = new ItemMap(zone, 362, 1, x, y, -1);
                    Service.gI().dropItemMap(zone, itemMap);

                    Logger.namek(formatNamekLog(
                            "Hóa thạch",
                            zone.map.mapName,
                            zone.map.mapId,
                            zone.zoneId,
                            x,
                            y
                    ));
                } else {
                    ItemMap itemMap = new ItemMap(zone, i + 353, 1, x, y, -1);
                    Service.gI().dropItemMap(zone, itemMap);

                    Logger.namek(formatNamekLog(
                            (i + 1) + " sao",
                            zone.map.mapName,
                            zone.map.mapId,
                            zone.zoneId,
                            x,
                            y
                    ));
                }

                listMap.remove(index);
            }
        }
    }

    public void removeStoneNrNamec() {
        for (byte i = 0; i < (byte) 7; i++) {
            try {
                if (mapNrNamec[i] < 0 || zoneNrNamec[i] < 0) {
                    continue;
                }

                Zone zone = Manager.MAPS.get(mapNrNamec[i]).zones.get(zoneNrNamec[i]);
                int idItem = i + 353;
                int idItem2 = 362;

                for (int j = zone.items.size() - 1; j >= 0; j--) {
                    if (zone.items.get(j).itemTemplate.id == idItem
                            || zone.items.get(j).itemTemplate.id == idItem2) {
                        ItemMapService.gI().removeItemMapAndSendClient(zone.items.remove(j));
                    }
                }
            } catch (Exception e) {
                Logger.err("NAMEK", "Lỗi removeStoneNrNamec viên " + (i + 1) + " sao | " + e.getMessage());
            }
        }
    }

    public void doneDragonNamec() {
        for (int i = 0; i < 7; i++) {
            Player p = Client.gI().getPlayerByID(idpNrNamec[i]);
            if (p != null) {
                p.idNRNM = -1;
                pNrNamec[i] = "";
                idpNrNamec[i] = -1;
                Service.gI().sendFlagBag(p);
                PlayerService.gI().changeAndSendTypePK(p, ConstPlayer.NON_PK);
            }
        }

        Logger.namek("Hoàn tất gọi rồng Namek | reset người giữ ngọc");
    }

    public void reInitNrNamec(long time) {
        lastTimeReinit = System.currentTimeMillis() + time;
        isReinit = true;

        Logger.namek("Chuyển sang trạng thái hóa thạch | time=" + time + "ms");
    }

    public boolean isSameMapNrNamec() {
        return (mapNrNamec[0] == 7)
                && (mapNrNamec[1] == 7)
                && (mapNrNamec[2] == 7)
                && (mapNrNamec[3] == 7)
                && (mapNrNamec[4] == 7)
                && (mapNrNamec[5] == 7)
                && (mapNrNamec[6] == 7);
    }

    public boolean isSameZoneNrNamec() {
        return (zoneNrNamec[0] == zoneNrNamec[1])
                && (zoneNrNamec[2] == zoneNrNamec[0])
                && (zoneNrNamec[3] == zoneNrNamec[0])
                && (zoneNrNamec[4] == zoneNrNamec[0])
                && (zoneNrNamec[5] == zoneNrNamec[0])
                && (zoneNrNamec[6] == zoneNrNamec[0]);
    }

    public boolean canCallDragonNamec(Player p) {
        byte count = 0;

        if (isSameMapNrNamec() && isSameZoneNrNamec()) {
            if (p.clan != null) {
                for (int i = 0; i < idpNrNamec.length; i++) {
                    for (int j = 0; j < p.clan.members.size(); j++) {
                        if (idpNrNamec[i] == p.clan.members.get(j).id) {
                            count++;
                        }
                    }
                }

                return count == (byte) 7;
            }
        }

        return false;
    }

    public void teleportToNrNamec(Player p) {
        if (p.idNRNM != -1) {
            return;
        }

        int idMAP = mapNrNamec[p.idGo];
        int idZone = zoneNrNamec[p.idGo];

        Zone z = Manager.MAPS.get(idMAP).zones.get(idZone);

        if (z != null && !z.items.isEmpty()) {
            for (int i = 0; i < z.items.size(); i++) {
                ItemMap it = z.items.get(i);
                if (it != null && (it.isNamecBall || it.itemTemplate.id == 362)) {
                    ChangeMapService.gI().changeMap(p, z, Util.nextInt(100, z.map.mapWidth), 5);
                    return;
                }
            }
        }

        Player player = Client.gI().getPlayerByID(idpNrNamec[p.idGo]);

        if (player != null && player.isPl()) {
            ChangeMapService.gI().changeMap(p, player.zone, Util.nextInt(100, player.zone.map.mapWidth), 5);
        }
    }

    public String getDis(Player pl, int id, short temp) {
        try {
            int idMAP = mapNrNamec[id];
            int idZone = zoneNrNamec[id];

            Integer[] sttMap = {7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43};

            Zone z = Manager.MAPS.get(idMAP).zones.get(idZone);

            if (z != null && !z.items.isEmpty()) {
                ItemMap it = z.getItemMapByTempId(temp);

                if (it != null) {
                    if (pl.zone.map.mapId == it.zone.map.mapId) {
                        if (pl.zone.zoneId == it.zone.zoneId) {
                            if ((pl.location.x - it.x) / 10 > 0) {
                                return (id + 1) + " Sao:" + Math.abs((pl.location.x - it.x) / 10) + " m(<-)";
                            } else if ((pl.location.x - it.x) / 10 < 0) {
                                return (id + 1) + " Sao:" + Math.abs((pl.location.x - it.x) / 10) + " m(->)";
                            } else {
                                return (id + 1) + " Sao";
                            }
                        }

                        return (id + 1) + " Sao:đây kv " + it.zone.zoneId;
                    } else {
                        List<Integer> check = Arrays.asList(sttMap);

                        if (check.contains(pl.zone.map.mapId)) {
                            int index = findIndex(pl.zone.map.mapId);
                            int indexMap = findIndex(idMAP);
                            int w = 0;

                            for (int i = 0; i < findIndex(index, indexMap).size(); i++) {
                                int map = findIndex(index, indexMap).get(i);
                                w += Manager.MAPS.get(map).mapWidth;
                            }

                            return (id + 1) + " Sao:" + z.map.mapName + " (" + Math.abs((pl.location.x - it.x - w) / 10) + " m)";
                        } else {
                            return (id + 1) + " Sao:Namếc ( ? m)";
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return getDisPlayer(pl, id, temp);
    }

    public String getDisPlayer(Player pl, int id, short temp) {
        try {
            String nPlayer = pNrNamec[id];

            Integer[] sttMap = {7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43};

            Player player = Client.gI().getPlayerByID(idpNrNamec[id]);

            if (player != null && player.isPl()) {
                int idMAP = player.zone.map.mapId;

                if (pl.zone.map.mapId == player.zone.map.mapId) {
                    if (pl.zone.zoneId == player.zone.zoneId) {
                        if ((pl.location.x - player.location.x) / 10 > 0) {
                            return (id + 1) + " Sao:" + Math.abs((pl.location.x - player.location.x) / 10) + " m(<-)(" + nPlayer + ")";
                        } else if ((pl.location.x - player.location.x) / 10 < 0) {
                            return (id + 1) + " Sao:" + Math.abs((pl.location.x - player.location.x) / 10) + " m(->)(" + nPlayer + ")";
                        } else {
                            return (id + 1) + " Sao(" + nPlayer + ")";
                        }
                    }

                    return (id + 1) + " Sao:đây kv " + player.zone.zoneId + " (" + nPlayer + ")";
                } else {
                    List<Integer> check = Arrays.asList(sttMap);

                    if (check.contains(pl.zone.map.mapId)) {
                        int index = findIndex(pl.zone.map.mapId);
                        int indexMap = findIndex(idMAP);
                        int w = 0;

                        for (int i = 0; i < findIndex(index, indexMap).size(); i++) {
                            int map = findIndex(index, indexMap).get(i);
                            w += Manager.MAPS.get(map).mapWidth;
                        }

                        return (id + 1) + " Sao:" + player.zone.map.mapName + " (" + Math.abs((pl.location.x - player.location.x - w) / 10) + " m)(" + nPlayer + ")";
                    } else {
                        return (id + 1) + " Sao:Namếc ( ? m)";
                    }
                }
            }
        } catch (Exception e) {
        }

        if (isReinit) {
            return getDisStone(pl, id, (short) 362);
        }

        return (id + 1) + " Sao:Namếc ( ? m)";
    }

    public String getDisStone(Player pl, int id, short temp) {
        try {
            int idMAP = mapNrNamec[id];
            int idZone = zoneNrNamec[id];

            Integer[] sttMap = {7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43};

            Zone z = Manager.MAPS.get(idMAP).zones.get(idZone);

            if (z != null && !z.items.isEmpty()) {
                ItemMap it = z.getItemMapByTempId(temp);

                if (it != null) {
                    if (pl.zone.map.mapId == it.zone.map.mapId) {
                        if (pl.zone.zoneId == it.zone.zoneId) {
                            if ((pl.location.x - it.x) / 10 > 0) {
                                return "Hóa thạch Ngọc Rồng:" + Math.abs((pl.location.x - it.x) / 10) + " m(<-)";
                            } else if ((pl.location.x - it.x) / 10 < 0) {
                                return "Hóa thạch Ngọc Rồng:" + Math.abs((pl.location.x - it.x) / 10) + " m(->)";
                            } else {
                                return "Hóa thạch Ngọc Rồng";
                            }
                        }

                        return "Hóa thạch Ngọc Rồng:đây kv " + it.zone.zoneId;
                    } else {
                        List<Integer> check = Arrays.asList(sttMap);

                        if (check.contains(pl.zone.map.mapId)) {
                            int index = findIndex(pl.zone.map.mapId);
                            int indexMap = findIndex(idMAP);
                            int w = 0;

                            for (int i = 0; i < findIndex(index, indexMap).size(); i++) {
                                int map = findIndex(index, indexMap).get(i);
                                w += Manager.MAPS.get(map).mapWidth;
                            }

                            return "Hóa thạch Ngọc Rồng:" + z.map.mapName + " (" + Math.abs((pl.location.x - it.x - w) / 10) + " m)";
                        } else {
                            return "Hóa thạch Ngọc Rồng:Namếc ( ? m)";
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return "Hóa thạch Ngọc Rồng:Namếc ( ? m)";
    }

    public byte findIndex(int id) {
        Integer[] sttMap = {7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43};

        for (byte i = 0; i < sttMap.length; i++) {
            if (sttMap[i] == id) {
                return i;
            }
        }

        return -1;
    }

    public List<Integer> findIndex(int start, int stop) {
        List<Integer> a = new ArrayList<>();

        Integer[] sttMap = {7, 8, 9, 10, 11, 12, 13, 25, 31, 32, 33, 34, 43};

        if (start < stop) {
            for (int i = start; i < stop; i++) {
                a.add(sttMap[i]);
            }
        } else {
            for (int i = stop; i < start; i++) {
                a.add(sttMap[i]);
            }
        }

        return a;
    }

    public boolean pickNamekBall(Player player, Item item) {
        try {
            switch (item.template.id) {
                case 362:
                    Service.gI().sendThongBao(player, "Chỉ là cục đá, vác chi cho nặng");
                    return false;

                case 353:
                case 354:
                case 355:
                case 356:
                case 357:
                case 358:
                case 359:
                    if (System.currentTimeMillis() >= NgocRongNamec.gI().tOpenNrNamec) {
                        if (player.idNRNM == -1) {
                            PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_ALL);

                            if (player.Detu != null) {
                                PlayerService.gI().changeAndSendTypePK(player.Detu, ConstPlayer.PK_ALL);
                            }

                            player.idNRNM = item.template.id;

                            NgocRongNamec.gI().mapNrNamec[item.template.id - 353] = player.zone.map.mapId;
                            NgocRongNamec.gI().nameNrNamec[item.template.id - 353] = player.zone.map.mapName;
                            NgocRongNamec.gI().zoneNrNamec[item.template.id - 353] = (byte) player.zone.zoneId;
                            NgocRongNamec.gI().pNrNamec[item.template.id - 353] = player.name;
                            NgocRongNamec.gI().idpNrNamec[item.template.id - 353] = (int) player.id;

                            player.lastTimePickNRNM = System.currentTimeMillis();

                            Service.gI().sendFlagBag(player);

                            int star = item.template.id - 352;
                            Logger.namek(formatNamekPlayerLog(
                                    "NHẶT",
                                    player,
                                    star,
                                    player.location.x,
                                    player.location.y
                            ));

                            return true;
                        } else {
                            Service.gI().sendThongBao(player, "Ngọc quá bự, bạn chỉ có thể mang theo 1 viên");
                            return false;
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Chỉ là cục đá, vác chi cho nặng");
                        return false;
                    }
            }

            return false;
        } catch (Exception e) {
            Logger.err("NAMEK", "Lỗi pickNamekBall | player="
                    + (player != null ? player.name : "null")
                    + " | " + e.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                if (this.isReinit && this.lastTimeReinit - System.currentTimeMillis() <= 0) {
                    Logger.namek("Hết thời gian hóa thạch, khởi tạo lại Ngọc Rồng Namek");

                    removeStoneNrNamec();
                    initNgocRongNamec((byte) 0);

                    this.isReinit = false;
                }

                if (Util.canDoWithTime(this.lastTimeReInitNRNM, 600000)) {
                    removeStoneNrNamec();
                    reInitNgocRongNamec();

                    this.lastTimeReInitNRNM = System.currentTimeMillis();
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                Logger.err("NAMEK", "Lỗi thread Ngọc Rồng Namek | " + e.getMessage());
            }
        }
    }

    public void dropNamekBall(Player pl) {
        if (pl.isPlandBot() && pl.idNRNM != -1) {
            int x = pl.location.x;
            int y = pl.zone.map.yPhysicInTop(x, 100);

            ItemMap itemMap = new ItemMap(pl.zone, pl.idNRNM, 1, x, y, -1);
            Service.gI().dropItemMap(pl.zone, itemMap);

            int star = pl.idNRNM - 352;
            Logger.namek(formatNamekPlayerLog(
                    "RƠI",
                    pl,
                    star,
                    x,
                    y
            ));

            NgocRongNamec.gI().pNrNamec[pl.idNRNM - 353] = "";
            NgocRongNamec.gI().idpNrNamec[pl.idNRNM - 353] = -1;

            pl.idNRNM = -1;

            PlayerService.gI().changeAndSendTypePK(pl, ConstPlayer.NON_PK);

            if (pl.Detu != null) {
                PlayerService.gI().changeAndSendTypePK(pl.Detu, ConstPlayer.NON_PK);
            }

            Service.gI().sendFlagBag(pl);
        }
    }

    // Rồng Thần đã xuất hiện, không thể nhặt ngọc được nữa
    public void menuCheckTeleNamekBall(Player pl) {
        String nrnm = "";

        for (int i = 0; i < 7; i++) {
            nrnm += NgocRongNamec.gI().getDis(pl, i, (short) (353 + i)) + "\n";
        }

        if (pl.idNRNM != -1) {
            NpcService.gI().createMenuConMeo(
                    pl,
                    ConstNpc.CONFIRM_TELE_NAMEC,
                    2294,
                    nrnm.trim(),
                    "Kết thúc"
            );
            return;
        }

        NpcService.gI().createMenuConMeo(
                pl,
                ConstNpc.CONFIRM_TELE_NAMEC,
                2294,
                nrnm.trim(),
                "Đến ngay\nViên " + (pl.idGo + 1) + " Sao\n50 ngọc",
                "Kết thúc"
        );
    }
}