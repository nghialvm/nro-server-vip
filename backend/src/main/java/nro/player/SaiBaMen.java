package nro.player;

import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import java.io.IOException;
import java.util.List;
import network.io.Message;

public class SaiBaMen extends Player {
    
    public Player player;
        
    public SaiBaMen(Player pl) {
        super();
        this.player = pl;
        this.IsSaibamen = true;
        this.id = Util.nextInt(1_700_000_000, 1_800_000_000);
        this.gender = 2;
        this.name = "Saibamen";
        this.nPoint.hpg = pl.nPoint.hpg;
        this.nPoint.mpg = pl.nPoint.mpg;
        this.nPoint.dameg = pl.nPoint.dameg;
        this.nPoint.hp = pl.nPoint.hp;
        this.nPoint.mp = pl.nPoint.mp;
        this.nPoint.dame = pl.nPoint.dame;
        this.nPoint.hpMax = pl.nPoint.hpMax;
        this.nPoint.mpMax = pl.nPoint.mpMax;
        this.nPoint.power = pl.nPoint.power;
        this.nPoint.tiemNang = pl.nPoint.tiemNang;
        this.nPoint.stamina = pl.nPoint.stamina;
        this.nPoint.maxStamina = pl.nPoint.maxStamina;
        this.nPoint.calPoint();
        this.nPoint.setFullHpMp();
    }
        
    @Override
    public void update() {
        super.update();
        if (this.isDie()) {
            dispose();
        }
        if (player != null && (this.zone == null || this.zone != player.zone)) {
            joinMap();
        }
    }

    public void joinMap() {
        this.location.x = player.location.x;
        this.location.y = player.location.y;
        MapService.gI().goToMap(this, player.zone);
        this.zone.load_Me_To_Another(this);
        setBom(player);
    }
    
    @Override
    public short getHead() {
        return 642;
    }

    @Override
    public short getBody() {
        return 643;
    }

    @Override
    public short getLeg() {
        return 644;
    }
    
    @Override
    public void setBom(Player plAtt) {
        if (!this.playerSkill.prepareTuSat) {
            //gồng tự sát
            this.playerSkill.prepareTuSat = true;
            this.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
            Message msg;
            try {
                msg = new Message(-45);
                msg.writer().writeByte(7);
                msg.writer().writeInt((int) this.id);
                msg.writer().writeShort(104);
                msg.writer().writeShort(2000);
                Service.gI().sendMessAllPlayerInMap(this, msg);
                msg.cleanup();
            } catch (IOException e) {
            }
            this.nPoint.hp = 0;
        }
        if (this.playerSkill != null) {
            while (this.playerSkill.prepareTuSat) {
                if (Util.canDoWithTime(this.playerSkill.lastTimePrepareTuSat, 2500)) {
                    this.playerSkill.prepareTuSat = false;
                    this.setDie(this);
                    double dame = this.nPoint.hpMax;
                    List<Player> playersMap;
                    playersMap = this.zone.getHumanoids();
                    if (!MapService.gI().isMapOffline(this.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!this.equals(pl)) {
                                pl.injured(this, dame, false, false);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.gI().Send_Info_NV(pl);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void dispose() {
        ChangeMapService.gI().exitMap(this);
        if (this.player != null) {
            this.player.Saibamen = null;
        }
        this.player = null;
        super.dispose();
    }
}
