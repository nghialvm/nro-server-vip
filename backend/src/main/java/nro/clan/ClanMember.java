package nro.clan;

import nro.player.Player;
import Utils.TimeUtil;
import java.util.Date;
import nro.server.Client;

public class ClanMember {

    public Clan clan;

    public int id;

    public short head;

    public short leg;

    public short body;

    public String name;

    public byte role;

    public long powerPoint;

    public int donate;

    public int receiveDonate;

    /**
     * Capsule cÃƒÂ¡ nhÃƒÂ¢n
     */
    public int memberPoint;
    
    /**
     * TÃƒÂ­nh dame boss
     */
    public long memberDamage;

    /**
     * Capsule cho bang
     */
    public int clanPoint;

    public int lastRequest;

    public int joinTime;

    public long timeAskPea;

    public ClanMember() {
    }

    public ClanMember(Player player, Clan clan, byte role) {
        this.clan = clan;
        this.id = (int) player.id;
        this.head = player.getHead();
        this.body = player.getBody();
        this.leg = player.getLeg();
        this.name = player.name;
        this.role = role;
        this.powerPoint = player.nPoint.power;
        this.donate = 0;
        this.receiveDonate = 0;
        this.memberPoint = 0;
        this.memberDamage = 0;
        this.clanPoint = 0;
        this.lastRequest = 0;
        this.joinTime = (int) (System.currentTimeMillis() / 1000);
    }

    public int getNumDateFromJoinTimeToToday() {
        return (int) TimeUtil.diffDate(new Date(), new Date(this.joinTime * 1000L), TimeUtil.DAY);
    }
    
    public Player getPlayer() {
        return Client.gI().getPlayerByID(this.id);
    }

    public void dispose() {
        this.clan = null;
        this.name = null;
    }

}




