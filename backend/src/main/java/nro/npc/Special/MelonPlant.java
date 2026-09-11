package nro.npc.Special;

import Utils.Logger;
import java.io.IOException;
import network.io.Message;
import nro.player.Player;



public class MelonPlant {

    private static final long DEFAULT_TIME_DONE = 86400000L;
    private static final long PHASE_DURATION = 28800000L;

    private Player player;
    public long lastTimeCreate;
    public long timeDone;

    private final short idNpc = 51;

    public MelonPlant(Player player, long lastTimeCreate, long timeDone) {
        this.player = player;
        this.lastTimeCreate = lastTimeCreate;
        this.timeDone = timeDone;
    }

    public static void createDuaHau(Player player) {
        player.duahau = new MelonPlant(player, System.currentTimeMillis(), DEFAULT_TIME_DONE);
    }
    
    public static int getCurrentPhase(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;

        if (elapsed >= DEFAULT_TIME_DONE) {
            return -1;
        }

        return (int) (elapsed / PHASE_DURATION);
    }
    
    public void sendDuaHau() {
        int phase = getCurrentPhase(this.lastTimeCreate);
        short Icon = 4669;
        switch (phase) {
            case 0:
                Icon = 4669;
                break;
            case 1:
                Icon = 4670;
                break;
            case 2:
                Icon = 4671;
                break;
            case -1:
                Icon = 4672;
                break;
        }
        Message msg;
        try {
            msg = new Message(-122);
            msg.writer().writeShort(this.idNpc);
            msg.writer().writeByte(1);
            msg.writer().writeShort(Icon);
            msg.writer().writeByte(0);
            msg.writer().writeInt(this.getSecondDone());
            this.player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
            Logger.logException(MelonPlant.class, e);
        }
    }

    public int getSecondDone() {
        int seconds = (int) ((lastTimeCreate + timeDone - System.currentTimeMillis()) / 1000);
        return seconds > 0 ? seconds : 0;
    }

    public void destroyDuaHau() {
        try {
            Message msg = new Message(-117);
            msg.writer().writeByte(101);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
        this.player.duahau = null;
    }

    public void subTimeDone(int d, int h, int m, int s) {
        this.timeDone -= ((d * 24 * 60 * 60 * 1000) + (h * 60 * 60 * 1000) + (m * 60 * 1000) + (s * 1000));
        this.sendDuaHau();
    }

    public void dispose() {
        this.player = null;
    }
}

