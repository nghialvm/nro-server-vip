package nro.player;

import nro.services.PlayerService;
import nro.services.Service;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import Utils.Util;


public class PetFollow extends Player{
    
    public Player master;
    public short body;
    public short leg;
    public static int idb = -310720020;

    public PetFollow(Player master, short h, short b, short l) {
        this.master = master;
        this.isPetFollow = true;
        this.id = idb;
        idb--;
        this.head = h;
        this.body = b;
        this.leg = l;
    }

    @Override
    public short getHead() {
        return head;
    }

    @Override
    public short getBody() {
        return body;
    }

    @Override
    public short getLeg() {
        return leg;
    }
    
    public void joinMapMaster() {
        if (master == null || master.zone == null) {
            return;
        }
        this.location.x = master.location.x + Util.nextInt(-10, 10);
        this.location.y = master.location.y;
        if (isPl()) {
            this.dispose();
            return;
        }
        ChangeMapService.gI().goToMap(this, master.zone);
        this.zone.load_Me_To_Another(this);
    }

    private long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - master.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, master.location.x
                    + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), master.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }
    
    @Override
    public void update() {
        super.update();
        if (this.isDie()) {
            Service.gI().hsChar(this, nPoint.hpMax, nPoint.mpMax);
        }
        if (master != null && master.zone != null && (this.zone == null || this.zone != master.zone) && 
                (!MapService.gI().isMapOffline(master.zone.map.mapId) ||
                !MapService.gI().isMapBangHoi(master.zone.map.mapId) ||
                !MapService.gI().isMapPotaufeu(master.zone.map.mapId) || 
                !MapService.gI().isMapWar(this.master.zone.map.mapId) || 
                !MapService.gI().isMapTestDame(this.master.zone.map.mapId) ||
                !MapService.gI().isMapTranhNgocNamec(this.master.zone.map.mapId))) {
            joinMapMaster();
        }
        if (master != null && master.isDie()) {
            return;
        }
        moveIdle();
    }
    
    public void followMaster() {
        followMaster(50);
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        int dx = (Math.random() < 0.5) ? -1 : 1;
        int dist = 12 + Util.nextInt(0, dis);
        int petX = mX + (dx > 0 ? -dist : dist);
        int petY = mY;
        this.location.x = petX;
        this.location.y = petY;
        PlayerService.gI().playerMove(this, this.location.x, this.location.y);
    }


    @Override
    public void dispose() {
        this.master = null;
        ChangeMapService.gI().exitMap(this);
        super.dispose();
    }
}





