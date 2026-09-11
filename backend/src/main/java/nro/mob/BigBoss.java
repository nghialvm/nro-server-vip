package nro.mob;

import nro.server.Manager;
import nro.services.Service;
import Utils.TimeUtil;
import Utils.Util;
import consts.ConstMob;
import event.EventManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import network.io.Message;
import nro.player.Player;
import nro.services.MapService;


public class BigBoss extends Mob {
    
    public int action = 0;
        
    public long lastBigBossAttackTime;
    
    //
    
    public long lastTimeDieBigBoss;
    
    private long lastPianoAttackTime;
    
    private long lastVoiChinNgaAttackTime;
    private long lastGaChinCuaAttackTime;
    private long lastNguaChinLmaoAttackTime;

    public BigBoss(Mob mob) {
        super(mob);
        this.zone = mob.zone;
    }

    @Override
    public void update() {
        if (zone.isGoldenFriezaAlive && TimeUtil.is21H()) {
            if (!isDie()) {
                startDie();
                return;
            }
        }
        if (MapService.gI().isMapHungVuongEvent(this.zone.map.mapId)) {
            switch (this.tempId) {
                case ConstMob.KONG:
                case ConstMob.GOZILLA:
                case ConstMob.VOI_CHIN_NGA:
                case ConstMob.NGUA_CHIN_LMAO:
                case ConstMob.GA_CHIN_CUA:
                    this.point.dame = 100_000;
                    break;
            }
        } else {
            switch (this.tempId) {
                case ConstMob.VOI_CHIN_NGA:
                case ConstMob.NGUA_CHIN_LMAO:
                case ConstMob.GA_CHIN_CUA:
                    this.point.dame = 100;
                    break;
            }
        }
        if (this.isDie() && this.isBigBossHungVuongEvent() && Util.canDoWithTime(lastTimeDieBigBoss, 1_800_000)) {
            this.hoiSinh();
            this.sendMobHoiSinh();
            lastTimeDieBigBoss = System.currentTimeMillis();
        }
        effectSkill.update();
        attack();
        PianoMove();
        if (EventManager.HUNG_VUONG) {
            GaChinCuaMove();
            VoiChinNgaMove();
            NguaChinHongMaoMove();
        }
    }
    
    public void NguaChinHongMaoMove() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastNguaChinLmaoAttackTime, 3000)) {
            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }
            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 12);
            switch (action) {
                case 11: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 12: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10;
            }
            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);
                switch (action) {
                    case 10:
                    case 21: {
                        for (Player player : players) {
                            this.location.x = player.location.x + Util.nextInt(-10, 10);
                            this.location.y = player.location.y;
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);
                        break;
                    }
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20: {
                        msg.writer().writeByte(players.size());
                        int dir = 0;
                        for (Player pl : players) {
                            double dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id);
                            msg.writeCris(Util.CrisGH(dame), Manager.readInt);
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir);
                        break;
                    }
                    case 22: {
                        break;
                    }
                    case 23: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastNguaChinLmaoAttackTime = System.currentTimeMillis();
            } catch (IOException e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
    
    public void VoiChinNgaMove() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastVoiChinNgaAttackTime, 3000)) {
            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }
            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 12);
            switch (action) {
                case 11: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 12: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10;
            }
            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);
                switch (action) {
                    case 10:
                    case 21: {
                        for (Player player : players) {
                            this.location.x = player.location.x + Util.nextInt(-10, 10);
                            this.location.y = player.location.y;
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);
                        break;
                    }
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20: {
                        msg.writer().writeByte(players.size());
                        int dir = 0;
                        for (Player pl : players) {
                            double dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id);
                            msg.writeCris(Util.CrisGH(dame), Manager.readInt);
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir);
                        break;
                    }
                    case 22: {
                        break;
                    }
                    case 23: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastVoiChinNgaAttackTime = System.currentTimeMillis();
            } catch (IOException e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
    
    public void GaChinCuaMove() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastGaChinCuaAttackTime, 3000)) {
            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }
            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 12);
            switch (action) {
                case 11: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 12: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10;
            }
            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);
                switch (action) {
                    case 10:
                    case 21: {
                        for (Player player : players) {
                            this.location.x = player.location.x + Util.nextInt(-10, 10);
                            this.location.y = player.location.y;
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);
                        break;
                    }
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20: {
                        msg.writer().writeByte(players.size());
                        int dir = 0;
                        for (Player pl : players) {
                            double dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id);
                            msg.writeCris(Util.CrisGH(dame), Manager.readInt);
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir);
                        break;
                    }
                    case 22: {
                        break;
                    }
                    case 23: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastGaChinCuaAttackTime = System.currentTimeMillis();
            } catch (IOException e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
    
    public void PianoMove() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && Util.canDoWithTime(lastPianoAttackTime, 2000)) {
            if (this.zone.getNotBosses().isEmpty()) {
                return;
            }
            List<Player> players = new ArrayList<>();
            action = Util.nextInt(11, 14);
            switch (action) {
                case 11: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 50) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 12: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 100) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                            break;
                        }
                    }
                    break;
                }
                case 13: 
                case 14: {
                    for (Player pl : this.zone.getNotBosses()) {
                        if (Util.getDistance(pl, this) < 150) {
                            players.add(pl);
                            if (pl.isDie()) {
                                action = 10;
                            }
                        }
                    }
                    break;
                }
            }
            if (players.isEmpty()) {
                int index = Util.nextInt(0, this.zone.getNotBosses().size() - 1);
                players.add(this.zone.getNotBosses().get(index));
                action = 10;
            }
            Message msg = null;
            try {
                msg = new Message(102);
                msg.writer().writeByte(action);
                msg.writer().writeByte(this.id);
                switch (action) {
                    case 10:
                    case 21: {
                        for (Player player : players) {
                            this.location.x = player.location.x + Util.nextInt(-10, 10);
                            this.location.y = player.location.y;
                        }
                        msg.writer().writeShort(this.location.x);
                        msg.writer().writeShort(this.location.y);
                        break;
                    }
                    case 11:
                    case 12:
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20: {
                        msg.writer().writeByte(players.size()); // sl player;
                        int dir = 0;
                        for (Player pl : players) {
                            double dame = pl.injured(null, this.point.getDameAttack(), false, true);
                            msg.writer().writeInt((int) pl.id); // id player
                            msg.writeCris(Util.CrisGH(dame), Manager.readInt); // dame
                            dir = pl.location.x < this.location.x ? -1 : 1;
                        }
                        msg.writer().writeByte(dir); // dir
                        break;
                    }
                    case 22: {
                        break;
                    }
                    case 23: {
                        break;
                    }
                    default: {
                        break;
                    }
                }
                Service.gI().sendMessAllPlayerInMap(this.zone, msg);
                lastPianoAttackTime = System.currentTimeMillis();
            } catch (IOException e) {
            } finally {
                if (msg != null) {
                    msg.cleanup();
                }
            }
        }
    }
}






