package nro.map.DragonNamecWar;

import nro.inventory.InventoryService;
import nro.player.Player;
import nro.services.Service;
import Utils.Util;
import network.io.Message;
import consts.ConstTranhNgocNamek;
import java.util.List;
import models.Item.ItemMapService;
import nro.map.ItemMap;
import nro.map.Zone;



public class TranhNgocService {

    private static TranhNgocService instance;

    public static TranhNgocService getInstance() {
        if (instance == null) {
            instance = new TranhNgocService();
        }
        return instance;
    }

    public void sendCreatePhoBan(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(0);
            msg.writer().writeShort(ConstTranhNgocNamek.MAP_ID);
            msg.writer().writeUTF(ConstTranhNgocNamek.CADIC); // team 1
            msg.writer().writeUTF(ConstTranhNgocNamek.FIDE); // team 2
            msg.writer().writeInt(ConstTranhNgocNamek.MAX_LIFE);
            msg.writer().writeShort(ConstTranhNgocNamek.TIME_SECOND);
            msg.writer().writeByte(ConstTranhNgocNamek.MAX_POINT);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateLift(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) pl.zone.getPlayersCadic().stream().filter(p -> p != null && !p.isDie()).count());
            msg.writer().writeInt((int) pl.zone.getPlayersFide().stream().filter(p -> p != null && !p.isDie()).count());
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEndPhoBan(Zone zone, byte type, boolean isFide) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(2);
            msg.writer().writeByte(type);
            if (zone != null) {
                List<Player> players = isFide ? zone.getPlayersFide(): zone.getPlayersCadic();
                synchronized (players) {
                    for (Player pl : players) {
                        if (pl != null) {
                            pl.sendMessage(msg);
                        }
                    }
                }
                msg.cleanup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdateTime(Player pl, short second) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(5);
            msg.writer().writeShort(second);
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendUpdatePoint(Player pl) {
        Message msg;
        try {
            msg = new Message(20);
            msg.writer().writeByte(0);
            msg.writer().writeByte(4);
            msg.writer().writeByte(pl.zone.pointCadic);
            msg.writer().writeByte(pl.zone.pointFide);
            Service.gI().sendMessAllPlayerInMap(pl.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void givePrice(List<Player> players, byte type, int point) {
        switch (type) {
            case ConstTranhNgocNamek.LOSE:
                int pointDiff = ConstTranhNgocNamek.MAX_POINT - point;
                for (Player pl : players) {
                    if (pl != null) {
                        if (pl.event.getNamekWarPoint() >= 7) {
                            pl.event.subNamekWarPoint(pointDiff);
                            InventoryService.gI().sendItemBag(pl);
                            Service.gI().sendThongBao(pl, "Báº¡n Ä‘Ã£ thua vÃ  bá»‹ thu " + pointDiff + " Äiá»ƒm chiáº¿n trÆ°á»ng Namek");
                        }
                        TranhNgoc.gI().removePlayersCadic(pl);
                        TranhNgoc.gI().removePlayersFide(pl);
                    }
                }
                break;
            case ConstTranhNgocNamek.WIN:
                for (Player pl : players) {
                    if (pl != null) {
                        pl.event.addNamekWarPoint(point);
                        InventoryService.gI().sendItemBag(pl);
                        Service.gI().sendThongBao(pl, "Báº¡n Ä‘Ã£ tháº¯ng vÃ  nháº­n " + point + " Äiá»ƒm chiáº¿n trÆ°á»ng Namek");
                        TranhNgoc.gI().removePlayersCadic(pl);
                        TranhNgoc.gI().removePlayersFide(pl);
                    }
                }
                break;
            case ConstTranhNgocNamek.DRAW:
                for (Player pl : players) {
                    if (pl != null) {
                        pl.event.addNamekWarPoint(point / 2);
                        InventoryService.gI().sendItemBag(pl);
                        Service.gI().sendThongBao(pl, "2 bÃªn hÃ²a nhau vÃ  nháº­n " + (point / 2) + " Äiá»ƒm chiáº¿n trÆ°á»ng Namek");
                        TranhNgoc.gI().removePlayersCadic(pl);
                        TranhNgoc.gI().removePlayersFide(pl);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void pickBall(Player player, ItemMap item) {
        if (player.isHoldNamecBallTranhDoat || item.typeHaveBallTranhDoat == player.iDMark.getTranhNgoc()) {
             Service.gI().sendThongBao(player, "Ngá»c rá»“ng nÃ y Ä‘Ã£ Ä‘Æ°á»£c sá»­ dá»¥ng rá»“i!");
            return;
        }
        if (item.typeHaveBallTranhDoat != -1 && item.typeHaveBallTranhDoat != player.iDMark.getTranhNgoc()) {
            if (player.iDMark.getTranhNgoc() == 1) {
                player.zone.pointCadic--;
            } else if (player.iDMark.getTranhNgoc() == 2) {
                player.zone.pointFide--;
            }
            sendUpdatePoint(player);
        }
        player.tempIdNamecBallHoldTranhDoat = item.itemTemplate.id;
        player.isHoldNamecBallTranhDoat = true;
        ItemMapService.gI().removeItemMapAndSendClient(item);
        Service.gI().sendFlagBag(player);
        Service.gI().sendThongBao(player, "Báº¡n Ä‘ang giá»¯ viÃªn ngá»c rá»“ng Namek");
    }

    public void dropBall(Player player, byte a) {
        if (player.tempIdNamecBallHoldTranhDoat != -1) {
            player.isHoldNamecBallTranhDoat = false;
        }
        int x = Util.nextInt(20, player.zone.map.mapWidth);
        int y = player.zone.map.yPhysicInTop(x, player.zone.map.mapHeight / 2);
        ItemMap itemMap = new ItemMap(player.zone, player.tempIdNamecBallHoldTranhDoat, 1, x, y, -1);
        itemMap.isNamecBallTranhDoat = true;
        itemMap.typeHaveBallTranhDoat = a;
        itemMap.x = player.location.x;
        itemMap.y = player.location.y;
        Service.gI().dropItemMap(player.zone, itemMap);
        Service.gI().sendFlagBag(player);
        player.tempIdNamecBallHoldTranhDoat = -1;
        Service.gI().sendThongBao(player, "Äi thu tháº­p tiáº¿p vá» Ä‘Ã¢y cho ta !!!");
    }
    
}






