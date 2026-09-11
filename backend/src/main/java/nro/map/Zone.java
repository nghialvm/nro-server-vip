package nro.map;

import consts.ConstTask;
import QuanLiBoss.Boss;
import models.Item.Item;
import nro.mob.Mob;
import nro.npc.Npc;
import nro.npc.NpcManager;
import nro.player.Player;
import network.io.Message;
import nro.inventory.InventoryService;
import nro.player.TestDame;
import QuanLiBoss.BossID;
import nro.server.Manager;
import models.Item.ItemService;
import models.Item.ItemMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.TaskService;
import Utils.FileIO;
import Utils.FormatStyle;
import Utils.Logger;
import Utils.Util;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import consts.ConstMob;
import consts.ConstTranhNgocNamek;
import event.EventManager;
import java.io.IOException;
import nro.boss.map.TrainingBoss.TrainningBoss;
import nro.bot.Bot;
import nro.map.DragonNamecWar.TranhNgoc;
import nro.map.DragonNamecWar.TranhNgocService;
import nro.map.MajinBuu14H.MaBuHold;
import nro.map.TreasureUnderSea.TrapMap;
import nro.npc.NonInteractiveNPC;
import nro.power.CaptionManager;

public class Zone {

    public static final byte PLAYERS_TIEU_CHUAN_TRONG_MAP = 7;

    public int countItemAppeaerd = 0;

    public long lastTimeNotifyXinBaTo = 0; // thời gian lần cuối thông báo

    public Map map;
    public int zoneId;
    public int maxPlayer;
    public int shenronType = -1;

    @Getter
    private final List<Player> nonInteractiveNPCs; //npc
    @Getter
    private final List<Player> humanoids; //player, boss, pet
    @Getter
    private final List<Player> notBosses; //player, pet
    @Getter
    private final List<Player> players; //player
    @Getter
    private final List<Player> bosses; //boss
    private final List<Player> pets; //pet

    public final List<Mob> mobs;
    public final List<ItemMap> items;

    public long lastTimeDropBlackBall;
    public boolean finishBlackBallWar;
    public boolean finishMapMaBu;

    public boolean isbulon1Alive = true;
    public boolean isbulon2Alive = true;
    public boolean isTUTAlive = true;
    public boolean isGoldenFriezaAlive;

    public boolean isCompeting;
    public String rankName1;
    public String rankName2;
    public int rank1;
    public int rank2;

    public List<TrapMap> trapMaps;
    public List<MaBuHold> maBuHolds;

    //tranh ngọc namek
    public int pointFide;
    public int pointCadic;
    private final List<Player> playersFide;
    private final List<Player> playersCadic;
    public long lastTimeStartTranhNgoc;
    public boolean startZoneTranhNgoc;
    public long lastTimeDropBall;

    public List<Player> getPlayersCadic() {
        return this.playersCadic;
    }

    public List<Player> getPlayersFide() {
        return this.playersFide;
    }

    public void addPlayersCadic(Player player) {
        synchronized (playersCadic) {
            if (!this.playersCadic.contains(player)) {
                this.playersCadic.add(player);
            }
        }
    }

    public void addplayersFide(Player player) {
        synchronized (playersFide) {
            if (!this.playersFide.contains(player)) {
                this.playersFide.add(player);
            }
        }
    }

    public void removeplayersCadic(Player player) {
        synchronized (playersCadic) {
            if (this.playersCadic.contains(player)) {
                this.playersCadic.remove(player);
            }
        }
    }

    public void removeplayersFide(Player player) {
        synchronized (playersFide) {
            if (this.playersFide.contains(player)) {
                this.playersFide.remove(player);
            }
        }
    }

    @Setter
    @Getter
    public Player Npc;

    @Setter
    @Getter
    public Player TestDame;

    public boolean isTrungUyTrangAlive;
    public boolean isbulon13Alive;
    public boolean isbulon14Alive;

    public boolean isFullPlayer() {
        return this.players.size() >= this.maxPlayer;
    }

    private void updateMob() {
        for (int i = this.mobs.size() - 1; i >= 0; i--) {
            try {
                mobs.get(i).update();
            } catch (Exception e) {
                Logger.logException(Zone.class, e, "Lỗi update mobs");
            }
        }
    }

    private void updateNonInteractiveNPC() {
        if (this.nonInteractiveNPCs.isEmpty()) {
            return;
        }
        try {
            for (int i = this.getNonInteractiveNPCs().size() - 1; i >= 0; i--) {
                if (i < this.getNonInteractiveNPCs().size()) {
                    Player pl = this.getNonInteractiveNPCs().get(i);
                    if (pl != null && pl.zone != null) {
                        pl.update();
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(Zone.class, e, "Lỗi update npcs");
        }
    }

    private void updateItem() {
        if (this.items.isEmpty()) {
            return;
        }
        try {
            for (int i = this.items.size() - 1; i >= 0; i--) {
                try {
                    if (i < this.items.size()) {
                        ItemMap item = this.items.get(i);
                        if (item != null && item.itemTemplate != null) {
                            item.update();
                        } else {
                            items.remove(i);
                            System.err.println("Remove item " + i);
                        }
                    }
                } catch (Exception e) {
                    Logger.logException(Zone.class, e, "Lỗi item");
                }
            }
        } catch (Exception e) {
            Logger.logException(Zone.class, e, "Lỗi update items");
        }

    }

    private void updatePlayer() {
        for (int i = this.notBosses.size() - 1; i >= 0; i--) {
            Player pl = this.notBosses.get(i);
            if (pl.isPl()) {
                this.notBosses.get(i).update();
            }
        }
    }

    private void updateTestDame() {
        if (this.TestDame != null) {
            TestDame.update();
        }
    }

    private void updateZoneTranhNgoc() {
        if (!TranhNgoc.gI().isTimeStartWar() && startZoneTranhNgoc) {
            startZoneTranhNgoc = false;
            playersCadic.clear();
            playersFide.clear();
            pointCadic = 0;
            pointFide = 0;
            return;
        }
        if (startZoneTranhNgoc) {
            if (Util.canDoWithTime(this.lastTimeStartTranhNgoc, ConstTranhNgocNamek.TIME)) {
                startZoneTranhNgoc = false;
                if (pointCadic > pointFide) {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
                    TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.WIN, pointCadic);
                    TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.LOSE, pointFide);
                } else if (pointFide > pointCadic) {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
                    TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.WIN, pointFide);
                    TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.LOSE, pointCadic);
                } else {
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.DRAW, false);
                }
                items.clear();
                playersCadic.clear();
                playersFide.clear();
                pointCadic = 0;
                pointFide = 0;
            } else {
                if (pointCadic == 7) {
                    startZoneTranhNgoc = false;
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, false);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, true);
                    TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.WIN, pointCadic);
                    TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.LOSE, pointFide);
                    items.clear();
                    playersCadic.clear();
                    playersFide.clear();
                    pointCadic = 0;
                    pointFide = 0;
                } else if (pointFide == 7) {
                    startZoneTranhNgoc = false;
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.WIN, true);
                    TranhNgocService.getInstance().sendEndPhoBan(this, ConstTranhNgocNamek.LOSE, false);
                    TranhNgocService.getInstance().givePrice(getPlayersFide(), ConstTranhNgocNamek.WIN, pointFide);
                    TranhNgocService.getInstance().givePrice(getPlayersCadic(), ConstTranhNgocNamek.LOSE, pointCadic);
                    items.clear();
                    playersCadic.clear();
                    playersFide.clear();
                    pointCadic = 0;
                    pointFide = 0;
                }
            }
            if (Util.canDoWithTime(lastTimeDropBall, ConstTranhNgocNamek.LAST_TIME_DROP_BALL)) {
                int id = Util.nextInt(353, 359);//ngoc rong namek day
                ItemMap it = this.getItemMapByTempId(id);
                if (it == null && !findPlayerHaveBallTranhDoat(id)) {
                    lastTimeDropBall = System.currentTimeMillis();
                    int x = Util.nextInt(20, map.mapWidth);
                    int y = map.yPhysicInTop(x, Util.nextInt(20, map.mapHeight - 200));
                    ItemMap itemMap = new ItemMap(this, id, 1, x, y, -1);
                    itemMap.isNamecBallTranhDoat = true;
                    Service.gI().dropItemMap(this, itemMap);
                }
            }
        }
    }

    public boolean findPlayerHaveBallTranhDoat(int id) {
        for (Player pl : this.getPlayers()) {
            if (pl != null && pl.isHoldNamecBallTranhDoat && pl.tempIdNamecBallHoldTranhDoat == id) {
                return true;
            }
        }
        return false;
    }

    public void update() {
        updateMob();
        updateItem();
        updateNonInteractiveNPC();
        updatePlayer();
        updateZoneTranhNgoc();
        if (map.mapId == 169 || map.mapId == 170 || map.mapId == 171) {
            updateTestDame();
        }
    }

    public Zone(Map map, int zoneId, int maxPlayer) {
        this.map = map;
        this.zoneId = zoneId;
        this.maxPlayer = maxPlayer;
        this.humanoids = new ArrayList<>();
        this.notBosses = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bosses = new ArrayList<>();
        this.pets = new ArrayList<>();
        this.mobs = new ArrayList<>();
        this.items = new ArrayList<>();
        this.trapMaps = new ArrayList<>();
        this.nonInteractiveNPCs = new ArrayList<>();
        this.trapMaps = new ArrayList<>();
        this.maBuHolds = new ArrayList<>();
        this.playersFide = new ArrayList<>();
        this.playersCadic = new ArrayList<>();
    }

    public int getNumOfPlayers() {
        return this.players.size();
    }

    public int getNumOfBoss() {
        return this.bosses.size();
    }

    public boolean isBossCanJoin(Boss boss) {
        for (Player b : this.bosses) {
            if (b.id == boss.id) {
                return false;
            }
        }
        return true;
    }

    public boolean IsTrongTaiDeoCoTrongKhu() {
        for (Player b : this.players) {
            if (b.id == -1000000) {
                return false;
            }
        }
        return true;
    }

    public List<Player> getNotBosses() {
        return this.notBosses;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public List<Player> getHumanoids() {
        return this.humanoids;
    }

    public List<Player> getBosses() {
        return this.bosses;
    }

    public List<Player> getNonInteractiveNPCs() {
        return this.nonInteractiveNPCs;
    }

    public void addPlayer(Player player) {
        if (player != null) {
            if (!this.humanoids.contains(player)) {
                this.humanoids.add(player);
            }

            if (player instanceof NonInteractiveNPC) {
                this.nonInteractiveNPCs.add(player);
            }

            if (!player.isBoss && !this.notBosses.contains(player) && !player.isPetFollow && !player.isDuongTang && !(player instanceof NonInteractiveNPC)) {
                this.notBosses.add(player);
            }

            if (!player.isBoss && !player.isPetFollow && !player.isDuongTang && !player.isDeTu && !player.isBo && !player.isMe && !player.isNguoiYeu && !player.isConOne && !player.isConTwo && !player.isConThree && !this.players.contains(player) && !(player instanceof NonInteractiveNPC)) {
                this.players.add(player);
            }

            if (player.isBoss) {
                this.bosses.add(player);
            }
            if (player.isDeTu || player.isBo || player.isMe || player.isPetFollow || player.isDuongTang || player.isNguoiYeu || player.isConOne || player.isConTwo || player.isConThree) {
                this.pets.add(player);
            }
        }
    }

    public void removePlayer(Player player) {
        this.nonInteractiveNPCs.remove(player);
        this.humanoids.remove(player);
        this.notBosses.remove(player);
        this.players.remove(player);
        this.bosses.remove(player);
        this.pets.remove(player);
    }

    public ItemMap getItemMapByItemMapId(int itemId) {
        for (ItemMap item : this.items) {
            if (item != null && item.itemMapId == itemId) {
                return item;
            }
        }
        return null;
    }

    public ItemMap getItemMapByTempId(int tempId) {
        for (ItemMap item : this.items) {
            if (item.itemTemplate.id == tempId) {
                return item;
            }
        }
        return null;
    }

    public boolean findItemMapByPlayer(Player player, int tempId) {
        for (ItemMap it : player.zone.items) {
            if (it.playerId == player.id && it.itemTemplate.id == tempId) {
                return true;
            }
        }
        return false;
    }

    public List<ItemMap> getItemMapsForPlayer(Player player) {
        List<ItemMap> list = new ArrayList<>();
        for (ItemMap item : items) {
            if (item.itemTemplate.id == 78) {
                if (TaskService.gI().getIdTask(player) != ConstTask.TASK_3_1) {
                    continue;
                }
            }
            if (item.itemTemplate.id == 74) {
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_3_0) {
                    continue;
                }
            }
            list.add(item);
        }
        return list;
    }

    public Player getPlayerInMap(long idPlayer) {
        for (Player pl : humanoids) {
            if (pl.id == idPlayer) {
                return pl;
            }
        }
        return null;
    }

    public Player getPlayerInMapOffline(Player player, long idPlayer) {
        for (Player pl : bosses) {
            if (pl.id == idPlayer && pl instanceof TrainningBoss && ((TrainningBoss) pl).playerAtt.equals(player)) {
                return pl;
            }
        }
        return null;
    }

    public void pickItem(Player player, int itemMapId) {
        ItemMap itemMap = getItemMapByItemMapId(itemMapId);
        if (itemMap != null && itemMap.itemTemplate != null && !itemMap.isPickedUp) {
            if (itemMap.itemTemplate.type == 22) {
                return;
            }
            if (itemMap.isNamecBallTranhDoat) {
                TranhNgocService.getInstance().pickBall(player, itemMap);
                return;
            }
            int playerId = Math.abs(itemMap.playerId > 100_000_000 ? 1_000_000_000 - (int) itemMap.playerId : (int) itemMap.playerId);
            if (playerId == player.id || itemMap.playerId == player.id || itemMap.playerId == -1) {
                Item item = ItemService.gI().createItemFromItemMap(itemMap);
                boolean picked = false;
                if (item.template.id == 648) {
                    if (!InventoryService.gI().findItemTatVoGiangSinh(player)) {
                        Service.gI().sendThongBao(player, "Cần 1 Tất, vớ giáng sinh");
                        return;
                    }
                }
                if (item.template.id >= 702 && item.template.id <= 708) {
                    if (!InventoryService.gI().findGioDungNgocBi(player)) {
                        Service.gI().sendThongBao(player, "Cần 1 Giỏ đựng ngọc bí");
                        return;
                    }
                }
                if (InventoryService.gI().addItemBag(player, item)) {
                    int itemType = item.template.type;
                    Message msg;
                    try {
                        msg = new Message(-20);
                        msg.writer().writeShort(itemMapId);
                        switch (itemType) {
                            case 9:
                            case 10:
                            case 34: {
                                msg.writer().writeUTF(item.quantity > Short.MAX_VALUE ? "Bạn vừa nhận được " + Util.formatNumber(item.quantity, FormatStyle.VIETNAMESE) + " " + item.template.name : "");
                                PlayerService.gI().sendInfoHpMpMoney(player);
                                break;
                            }
                            default: {
                                switch (item.template.id) {
                                    case 362:
                                        msg.writer().writeUTF("Chỉ là cục đá thôi, nhặt làm gì?");
                                        break;
                                    case 73:
                                        msg.writer().writeUTF("");
                                        break;
                                    case 78:
                                        msg.writer().writeUTF("Wow, một cậu bé dễ thương!");
                                        break;
                                    case 516:
                                    case 191:
                                    case 192:
                                    case 74:
                                        msg.writer().writeUTF("Bạn mới vừa ăn " + item.template.name);
                                        break;
                                    case 670:
                                        msg.writer().writeUTF(item.template.name + " ngon quá !");
                                        break;
                                    default: {
                                        if (item.template.type >= 0 && item.template.type < 5) {
                                            msg.writer().writeUTF("Bạn nhặt được " + item.template.name);
                                        } else {
                                            msg.writer().writeUTF("Bạn nhận được " + item.template.name);
                                        }
                                        if (item.template.id == 648) {
                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 649), 1);
                                        }
                                        if (item.template.id >= 702 && item.template.id <= 708) {
                                            InventoryService.gI().subQuantityItemsBag(player, InventoryService.gI().findItemBag(player, 1348), 1);
                                        }
                                        InventoryService.gI().sendItemBag(player);
                                    }
                                }
                                break;
                            }
                        }
                        msg.writer().writeShort(item.quantity > Short.MAX_VALUE ? 9999 : item.quantity);
                        player.sendMessage(msg);
                        msg.cleanup();
                        Service.gI().sendToAntherMePickItem(player, itemMapId);
                        if (picked) {
                            if (itemMap.itemTemplate.id != 74) {
                                itemMap.isPickedUp = true;
                            }
                        }
                        if (!(this.map.mapId >= 21 && this.map.mapId <= 23 && itemMap.itemTemplate != null && itemMap.itemTemplate.id == 74
                                || this.map.mapId >= 42 && this.map.mapId <= 44 && itemMap.itemTemplate != null && itemMap.itemTemplate.id == 78)) {
                            removeItemMap(itemMap);
                        }
                    } catch (IOException e) {
                        Logger.logException(Zone.class, e);
                    }
                } else {
                    if (!ItemMapService.gI().isBlackBall(item.template.id) && !ItemMapService.gI().isNamecBall(item.template.id) && !ItemMapService.gI().isNamecBallStone(item.template.id)) {
                        String text = "Hành trang không còn chỗ trống, không thể nhặt thêm";
                        Service.gI().sendThongBao(player, text);
                        return;
                    }
                }
                picked = true;
            } else {
                Service.gI().sendThongBao(player, "Không thể nhặt vật phẩm của người khác");
                return;
            }
            TaskService.gI().checkDoneTaskPickItem(player, itemMap);
            TaskService.gI().checkDoneSideTaskPickItem(player, itemMap);
            TaskService.gI().checkDoneClanTaskPickItem(player, itemMap);
            player.playerTask.kolTask.checkDonePickItem(itemMap);
            TaskService.gI().checkDoneEventTaskPickItem(player, itemMap);
        } else {
            Service.gI().sendThongBao(player, "Không thể thực hiện");
        }
    }

    public void addItem(ItemMap itemMap) {
        if (itemMap != null && !items.contains(itemMap)) {
            items.add(0, itemMap);
        }
    }

    public void removeItemMap(ItemMap itemMap) {
        this.items.remove(itemMap);
    }

    public Player getRandomPlayerInMap() {
        List<Player> plNotVoHinh = new ArrayList();

        //Lỗi
        for (Player pl : this.notBosses) {
            if(pl == null) continue;
            if (pl != null && (pl.effectSkin == null || !pl.effectSkin.isVoHinh) && pl.maBuHold == null && !pl.isMabuHold) {
                plNotVoHinh.add(pl);
            }
        }

        if (!plNotVoHinh.isEmpty()) {
            return plNotVoHinh.get(Util.nextInt(0, plNotVoHinh.size() - 1));
        }

        return null;
    }

    public void load_Me_To_Another(Player player) { // load thông tin người chơi cho những người chơi khác
        try {
            // --- FIX riêng map 0: neo Y xuống nền để tránh đơ khi vừa bay đến ---
            if (this.map.mapId == 0 && player != null) {
                int groundY = this.map.yPhysicInTop(player.location.x, 100);
                if (groundY <= 0 || groundY > this.map.mapHeight) {
                    groundY = 432; // fallback an toàn
                }
                if (Math.abs(player.location.y - groundY) > 5) {
                    Service.gI().setPos(player, player.location.x, groundY);
                }
            }
            // --------------------------------------------------------------------

            if (player.zone != null) {
                if (MapService.gI().isMapOffline(this.map.mapId)) {
                    if (player instanceof TrainningBoss || player instanceof NonInteractiveNPC) {
                        for (int i = players.size() - 1; i >= 0; i--) {
                            Player pl = players.get(i);
                            if (!player.equals(pl) && (player instanceof NonInteractiveNPC || ((TrainningBoss) player).playerAtt.equals(pl))) {
                                infoPlayer(pl, player);
                            }
                        }
                    }
                } else if (MapService.gI().isMapBangHoi(this.map.mapId)) {
                    for (int i = players.size() - 1; i >= 0; i--) {
                        Player pl = players.get(i);
                        if (!player.equals(pl)
                                && player.clan != null
                                && pl.clan != null
                                && player.clan.equals(pl.clan)) {
                            infoPlayer(pl, player);
                        }
                    }
                } else {
                    for (int i = players.size() - 1; i >= 0; i--) {
                        Player pl = players.get(i);
                        if (!player.equals(pl)) {
                            infoPlayer(pl, player);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    public void load_Another_To_Me(Player player) { // load những player trong map và gửi cho player vào map
        try {
            // --- FIX riêng map 0: neo Y xuống nền để tránh đơ khi vừa bay đến ---
            if (this.map.mapId == 0 && player != null) {
                int groundY = this.map.yPhysicInTop(player.location.x, 100);
                if (groundY <= 0 || groundY > this.map.mapHeight) {
                    groundY = 432; // fallback an toàn
                }
                if (Math.abs(player.location.y - groundY) > 5) {
                    Service.gI().setPos(player, player.location.x, groundY);
                }
            }
            // --------------------------------------------------------------------

            if (MapService.gI().isMapOffline(this.map.mapId)) {
                for (int i = this.humanoids.size() - 1; i >= 0; i--) {
                    Player pl = this.humanoids.get(i);
                    if ((pl != null && (pl instanceof NonInteractiveNPC || pl instanceof TrainningBoss && ((TrainningBoss) pl).playerAtt.equals(player))) || pl instanceof TestDame) {
                        infoPlayer(player, pl);
                    }
                }
            } else if (MapService.gI().isMapBangHoi(this.map.mapId)) {
                for (int i = this.humanoids.size() - 1; i >= 0; i--) {
                    Player pl = this.humanoids.get(i);
                    if (pl != null && !player.equals(pl)
                            && player.clan != null
                            && pl.clan != null
                            && player.clan.equals(pl.clan)) {
                        infoPlayer(player, pl);
                    }
                }
            } else {
                for (int i = this.humanoids.size() - 1; i >= 0; i--) {
                    Player pl = this.humanoids.get(i);
                    if (pl != null && !player.equals(pl)) {
                        infoPlayer(player, pl);
                    }
                }
            }
        } catch (Exception e) {
            // giữ trống như bản gốc
        }
    }

    public void loadBoss(Boss boss) {
        try {
            if (MapService.gI().isMapOffline(this.map.mapId)) {
                //Load boss
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl) && !pl.isPl() && !pl.isDeTu && !pl.isBo && !pl.isMe && !pl.isPetFollow && !pl.isDuongTang && !pl.isPhanThan && !pl.IsSaibamen && !pl.isNguoiYeu && !pl.isConOne && !pl.isConTwo && !pl.isConThree) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            } else {
                for (Player pl : this.bosses) {
                    if (!boss.equals(pl)) {
                        infoPlayer(boss, pl);
                        infoPlayer(pl, boss);
                    }
                }
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }

    private void infoPlayer(Player plReceive, Player plInfo) {
        Message msg;
        try {
            msg = new Message(-5);
            msg.writer().writeInt((int) plInfo.id);
            if (plInfo.clan != null) {
                msg.writer().writeInt(plInfo.clan.id);
            } else if (plInfo.isBoss && (plInfo.id == BossID.MABU || plInfo.id == BossID.SUPERBU)) {
                msg.writer().writeInt(-100);
            } else if (plInfo.isCopy) {
                msg.writer().writeInt(-2);
            } else {
                msg.writer().writeInt(-1);
            }
            msg.writer().writeByte(CaptionManager.getInstance().getLevel(plInfo));
            msg.writer().writeBoolean(false);
            msg.writer().writeByte(plInfo.typePk);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeByte(plInfo.gender);
            msg.writer().writeShort(plInfo.getHead());
            msg.writer().writeUTF(Service.gI().name(plInfo));

            // Check null trước khi ghi hp và hpMax
            long hp = 0;
            long hpMax = 0;
            if (plInfo.nPoint != null) {
                hp = plInfo.nPoint.hp;
                hpMax = plInfo.nPoint.hpMax;
            }
            msg.writeCris(Util.CrisGH(hp), Manager.readInt);
            msg.writeCris(Util.CrisGH(hpMax), Manager.readInt);

            msg.writer().writeShort(plInfo.getBody());
            msg.writer().writeShort(plInfo.getLeg());
            msg.writer().writeByte(plInfo.getFlagBag()); //bag
            msg.writer().writeByte(-1);
            msg.writer().writeShort(plInfo.location.x);
            msg.writer().writeShort(plInfo.location.y);
            msg.writer().writeShort(0); // effbuffhp
            msg.writer().writeShort(0); // effbuffmp

            msg.writer().writeByte(0); // num eff

            //byte templateId, int timeStart, int timeLenght, short param
            msg.writer().writeByte(plInfo.iDMark.getIdSpaceShip());

            msg.writer().writeByte(plInfo.effectSkill != null && plInfo.effectSkill.isMonkey ? 1 : 0);
            msg.writer().writeShort(plInfo.getMount());
            msg.writer().writeByte(plInfo.cFlag);

            msg.writer().writeByte(0);
            msg.writer().writeShort(plInfo.getAura()); //idauraeff
            msg.writer().writeByte(plInfo.getEffFront()); //seteff
            msg.writer().writeShort(plInfo.getHat()); //id hat
            msg.writer().writeByte(0); // isTichXanh
            plReceive.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
        Service.gI().sendFlagPlayerToMe(plReceive, plInfo);
        if (plInfo.isPlandBot() && !(plInfo instanceof TestDame)) {
//            if (plInfo.effectSkill != null && plInfo.effectSkill.isChibi) {
//                Service.gI().sendChibiFollowToMe(plReceive, plInfo);
//            } else {
//                Service.gI().sendLinhThuToMe(plReceive, plInfo);
//            }
            Service.gI().sendDanhHieuInfo(plInfo, plReceive, (plInfo.isUseDanhHieu_ThienTu == true ? 0 : -1));
            if (!plInfo.inventory.itemsBody.isEmpty()) {
                if (plInfo.isPl() && plInfo.inventory.itemsBody.size() > 11 && plInfo.inventory.itemsBody.get(11).isNotNullItem()) {
                    Service.getInstance().sendChanMenhInfo(plInfo, plReceive, (short) plInfo.inventory.itemsBody.get(11).template.id);
                }
            }
        }
        try {
            if (plInfo.isDie()) {
                msg = new Message(-8);
                msg.writer().writeInt((int) plInfo.id);
                msg.writer().writeByte(0);
                if (plInfo.location != null) {
                    msg.writer().writeShort(plInfo.location.x);
                    msg.writer().writeShort(plInfo.location.y);
                }
                plReceive.sendMessage(msg);
                msg.cleanup();
            }
        } catch (IOException e) {
        }
    }

    public void mapInfo(Player pl) {
        Message msg;
        try {
            
            msg = new Message(-24);
            msg.writer().writeByte(this.map.mapId);
            msg.writer().writeByte(this.map.planetId);
            msg.writer().writeByte(this.map.tileId);
            msg.writer().writeByte(this.map.bgId);
            msg.writer().writeByte(this.map.type);
            msg.writer().writeUTF(this.map.mapName);
            msg.writer().writeByte(this.zoneId);

            msg.writer().writeShort(pl.location.x);
            msg.writer().writeShort(pl.location.y);

            // waypoint
            try {
                List<WayPoint> wayPoints = this.map.wayPoints;
                msg.writer().writeByte(wayPoints.size());
                for (WayPoint wp : wayPoints) {
                    msg.writer().writeShort(wp.minX);
                    msg.writer().writeShort(wp.minY);
                    msg.writer().writeShort(wp.maxX);
                    msg.writer().writeShort(wp.maxY);
                    msg.writer().writeBoolean(wp.isEnter);
                    msg.writer().writeBoolean(wp.isOffline);
                    msg.writer().writeUTF(wp.name);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // mob
            try {
                List<Mob> mobs = new ArrayList<>();
                for (Mob mob : this.mobs) {
                    if (mob.isBigBoss() && mob.tempId != 70 && mob.isDie()) {
                        continue;
                    }
                    mobs.add(mob);
                }
                msg.writer().writeByte(mobs.size());
                for (Mob mob : mobs) {
                    msg.writer().writeBoolean(false); //is disable
                    msg.writer().writeBoolean(false); //is dont move
                    msg.writer().writeBoolean(false); //is fire
                    msg.writer().writeBoolean(false); //is ice
                    msg.writer().writeBoolean(false); //is wind
                    msg.writer().writeByte(mob.tempId);
                    msg.writer().writeByte(0); // sys
                    msg.writeCris(Util.CrisGH(mob.point.gethp()), Manager.readInt);
                    msg.writer().writeByte(mob.level);
                    msg.writeCris(Util.CrisGH(mob.point.getHpFull()), Manager.readInt);
                    msg.writer().writeShort(mob.location.x);
                    msg.writer().writeShort(mob.location.y);
                    msg.writer().writeByte(mob.status);
                    msg.writer().writeByte(mob.lvMob);
                    msg.writer().writeBoolean(mob.tempId == ConstMob.GAU_TUONG_CUOP || mob.tempId >= ConstMob.VOI_CHIN_NGA && mob.tempId <= ConstMob.PIANO || mob.tempId == ConstMob.KONG || mob.tempId == ConstMob.GOZILLA); //is bigboss
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            msg.writer().writeByte(0);

            // npc
            try {
                List<Npc> npcs = NpcManager.getNpcsByMapPlayer(pl);
                msg.writer().writeByte(npcs.size());
                for (Npc npc : npcs) {
                    msg.writer().writeByte(npc.status);
                    msg.writer().writeShort(npc.cx);
                    msg.writer().writeShort(npc.cy);
                    msg.writer().writeByte(npc.tempId);
                    msg.writer().writeShort(npc.avartar);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // item
            try {
                List<ItemMap> itemsMap = this.getItemMapsForPlayer(pl);
                msg.writer().writeByte(itemsMap.size());
                for (ItemMap it : itemsMap) {
                    msg.writer().writeShort(it.itemMapId);
                    msg.writer().writeShort(it.itemTemplate.id);
                    msg.writer().writeShort(it.x);
                    msg.writer().writeShort(it.y);
                    msg.writer().writeInt((int) it.playerId);
                }
            } catch (Exception e) {
                msg.writer().writeByte(0);
            }

            // bg item
            try {
                final byte[] bgItem = FileIO.readFile("data/map/item_bg_map_data/" + this.map.mapId);
                msg.writer().write(bgItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            // eff map
            try {
                final byte[] effItem;
                if (EventManager.LUNNAR_NEW_YEAR) {
                    effItem = FileIO.readFile("data/map/eff_map_event_luna_new_year/" + this.map.mapId);
                } else if (EventManager.CHRISTMAS) {
                    effItem = FileIO.readFile("data/map/eff_map_christmas/" + this.map.mapId);
                } else if (EventManager.HALLOWEEN) {
                    effItem = FileIO.readFile("data/map/eff_map_halloween/" + this.map.mapId);
                } else {
                    effItem = FileIO.readFile("data/map/eff_map/" + this.map.mapId);
                }
                msg.writer().write(effItem);
            } catch (Exception e) {
                msg.writer().writeShort(0);
            }

            msg.writer().writeByte(this.map.bgType);
            msg.writer().writeByte(pl.iDMark.getIdSpaceShip());
            msg.writer().writeByte(this.map.mapId == 148 ? 1 : 0);
            pl.sendMessage(msg);

            msg.cleanup();

        } catch (Exception e) {
            Logger.logException(Service.class, e);
        }
    }

    public TrapMap isInTrap(Player player) {
        for (TrapMap trap : this.trapMaps) {
            if (player.location.x >= trap.x && player.location.x <= trap.x + trap.w
                    && player.location.y >= trap.y && player.location.y <= trap.y + trap.h) {
                return trap;
            }
        }
        return null;
    }

    public void sendBigBoss(Player player) {
        for (Mob mob : this.mobs) {
            if (!mob.isDie() && mob.tempId == ConstMob.HIRUDEGARN) {
                if (mob.lvMob >= 1) {
                    Service.gI().sendBigBoss2(player, 6, mob);
                }
                if (mob.lvMob >= 2) {
                    Service.gI().sendBigBoss2(player, 5, mob);
                }
                break;
            }
        }
    }

    public MaBuHold getMaBuHold() {
        for (MaBuHold hold : MapService.gI().getMapById(128).zones.get(this.zoneId).maBuHolds) {
            if (hold.player == null) {
                return hold;
            }
        }
        return null;
    }

    public void setMaBuHold(int slot, int zoneId, Player player) {
        MapService.gI().getMapById(128).zones.get(zoneId).maBuHolds.set(slot, new MaBuHold(slot, player));
    }

    public Player findPlayerByID(long id) {
        for (Player p : this.players) {
            if (p.id == id) {
                return p;
            }
        }
        return null;
    }

    public boolean isKhongCoTrongTaiTrongKhu() {
        boolean no = true;
        for (Player pl : players) {
            if (pl.name.compareTo("Trọng Tài") == 0) {
                no = false;
                break;
            }
            if (pl.zone.map.mapId >= 21 && pl.zone.map.mapId <= 23) {
                no = false;
            }
        }
        return no;
    }

    public int getNumOfBosses() {
        return this.bosses.size();
    }

    public Player PlayerMiNuongMap() {
        if (!this.humanoids.isEmpty()) {
            return this.humanoids.get(Util.nextInt(0, this.humanoids.size() - 1));
        } else {
            return null;
        }
    }

    public int getNewMobId() {
        int id = 0;
        while (true) {
            boolean exists = false;
            for (Mob m : mobs) {
                if (m.id == id) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                return id;
            }
            id++;
        }
    }

    
}
