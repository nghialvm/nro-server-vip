package nro.effect;

import nro.map.Zone;
import nro.player.Player;
import network.io.Message;
import nro.services.Service;


public class EffectMapService {

    private static EffectMapService i;

    private EffectMapService() {

    }

    public static EffectMapService gI() {
        if (i == null) {
            i = new EffectMapService();
        }
        return i;
    }

    public void sendEffectMapToPlayer(Player player, int id, int layer, int loop, int x, int y, int delay) {
        Message msg;
        try {
            msg = new Message(113);
            msg.writer().writeByte(id);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(delay);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public void sendCharEffect_HoaBang(Player player) {
        Message msg;
        try {
            msg = new Message(-128);
            msg.writer().writeByte(0);// 0 : adÄ‘
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(202);
            msg.writer().writeByte(1);
            msg.writer().writeByte(-1);
            msg.writer().writeShort(10);
            msg.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void Remove_sendCharEffect_HoaBang(Player player) {
        Message msg;
        try {
            msg = new Message(-128);
            msg.writer().writeByte(1);// 0 : adÄ‘
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(202);
            msg.writer().writeByte(1);
            msg.writer().writeByte(-1);
            msg.writer().writeShort(10);
            msg.writer().writeByte(1);
            player.sendMessage(msg);
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendEffectMapToAllInMap(Zone zone, int id, int layer, int loop, int x, int y, int delay) {
        Message msg;
        try {
            msg = new Message(113);
            msg.writer().writeByte(loop);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(delay);
            Service.gI().sendMessAllPlayerInMap(zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public void sendEffectMapToAllInMap(Player player, int id, int layer, int loop, int x, int y, int delay) {
        Message msg = null;
        try {
            msg = new Message(113);
            msg.writer().writeByte(loop);
            msg.writer().writeByte(layer);
            msg.writer().writeByte(id);
            msg.writer().writeShort(x);
            msg.writer().writeShort(y);
            msg.writer().writeShort(delay);
            Service.gI().sendMessAllPlayerInMap(player, msg);
        } catch (Exception e) {
        } finally {
            if (msg != null) {
                msg.cleanup();
            }
        }
    }    
}





