package nro.map;

import consts.ConstMap;
import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import nro.map.MajinBuu12H.MajinBuu12H;
import nro.mob.Mob;
import nro.npc.Npc;
import nro.npc.NpcFactory;
import nro.player.Player;
import QuanLiBoss.Manager.BossManager;
import Utils.Functions;
import nro.server.Manager;
import nro.services.MapService;
import nro.services.Service;
import Utils.Logger;
import Utils.Util;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import consts.ConstMob;
import nro.map.BlackBallWar.BlackBallWar;
import nro.map.BlackBallWar.BlackBallWarService;
import nro.map.BossOfTheGangs.BossOfTheGangs;
import nro.map.BossOfTheGangs.BossOfTheGangsService;
import nro.map.DestronGas.DestronGas;
import nro.map.DestronGas.DestronGasService;
import nro.map.MajinBuu14H.MajinBuu14H;
import nro.map.MajinBuu14H.MajinBuu14HService;
import nro.map.RedRibbonHQ.RedRibbonHQ;
import nro.map.RedRibbonHQ.RedRibbonHQService;
import nro.map.SnakeWay.SnakeWay;
import nro.map.SnakeWay.SnakeWayService;
import nro.map.SuperDivineWater.SuperDivineWater;
import nro.map.TreasureUnderSea.TrapMap;
import nro.map.TreasureUnderSea.TreasureUnderSea;
import nro.map.TreasureUnderSea.TreasureUnderSeaService;
import nro.mob.ListBigMob.GaChinCua;
import nro.mob.ListBigMob.GauTuongCuop;
import nro.mob.ListBigMob.Godzila;
import nro.mob.ListBigMob.Hirudegarn;
import nro.mob.ListBigMob.Kong;
import nro.mob.ListBigMob.NguaChinLmao;
import nro.mob.ListBigMob.Piano;
import nro.mob.ListBigMob.RobotBaoVe;
import nro.mob.ListBigMob.VoiChinNga;
import nro.mob.ListBigMob.VuaBachTuoc;
import nro.template.MobTemplate;

public class Map implements Runnable {

    public static final byte T_EMPTY = 0;
    public static final byte T_TOP = 2;
    private static final int SIZE = 24;

    public int mapId;
    public String mapName;

    public byte planetId;
    public String planetName;

    public byte tileId;
    public byte bgId;
    public byte bgType;
    public byte type;

    public int[][] tileMap;
    public int[] tileTop;
    public int mapWidth;
    public int mapHeight;

    public List<Zone> zones;
    public List<WayPoint> wayPoints;
    public List<Npc> npcs;
    
    public int tmw;
    public int tmh;
    public int pxh;
    public int pxw;
    public int[] types;
    public int[] maps;
    public List<EffectMap> effMap;
    
    public byte genderType;
    
    public Map(int mapId, String mapName, byte planetId, byte tileId, byte bgId, byte bgType, byte type, int[][] tileMap, int[] tileTop, int zones, int maxPlayer, List<WayPoint> wayPoints, List<EffectMap> effMap, byte genderType) {
        this.mapId = mapId;
        this.mapName = mapName;
        this.planetId = planetId;
        this.planetName = Service.gI().get_HanhTinh(planetId);
        this.tileId = tileId;
        this.bgId = bgId;
        this.bgType = bgType;
        this.type = type;
        this.tileMap = tileMap;
        this.tileTop = tileTop;
        this.zones = new ArrayList<>();
        this.wayPoints = wayPoints;
        this.effMap = effMap;
        this.genderType = genderType;
        try {
            this.mapHeight = tileMap.length * SIZE;
            this.mapWidth = tileMap[0].length * SIZE;
        } catch (Exception ignored) {
        }
        this.readTileMap(mapId);
        this.initZone(zones, maxPlayer);
        this.initItem();
        this.initTrapMap();
    }

    private void initZone(int nZone, int maxPlayer) {
        switch (this.type) {
            case ConstMap.MAP_OFFLINE:
                nZone = 1;
                break;
            case ConstMap.MAP_BANG_HOI:
                nZone = 1;
                break;
            case ConstMap.MAP_BLACK_BALL_WAR:
                nZone = BlackBallWar.AVAILABLE;
                break;
            case ConstMap.MAP_MABU_14H:
                nZone = MajinBuu14H.AVAILABLE;
                break;
            case ConstMap.MAP_MA_BU_12H:
                nZone = MajinBuu12H.AVAILABLE;
                break;
            case ConstMap.MAP_DOANH_TRAI:
                nZone = RedRibbonHQ.AVAILABLE;
                break;
            case ConstMap.MAP_BAN_DO_KHO_BAU:
                nZone = TreasureUnderSea.AVAILABLE;
                break;
            case ConstMap.MAP_CON_DUONG_RAN_DOC:
                nZone = SnakeWay.AVAILABLE;
                break;
            case ConstMap.MAP_KHI_GAS_HUY_DIET:
                nZone = DestronGas.AVAILABLE;
                break;
            case ConstMap.MAP_BOSS_BANG_HOI:
                nZone = BossOfTheGangs.AVAILABLE;
                break;
        }

        for (int i = 0; i < nZone; i++) {
            Zone zone = new Zone(this, i, maxPlayer);
            this.zones.add(zone);
            switch (this.type) {
                case ConstMap.MAP_BLACK_BALL_WAR:
                    BlackBallWarService.gI().addMapBlackBallWar(i, zone);
                    break;
                case ConstMap.MAP_MABU_14H:
                    MajinBuu14HService.gI().addMapMaBu2H(i, zone);
                    break;
                case ConstMap.MAP_DOANH_TRAI:
                    RedRibbonHQService.gI().addMapDoanhTrai(i, zone);
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    TreasureUnderSeaService.gI().addMapBanDoKhoBau(i, zone);
                    break;
                case ConstMap.MAP_CON_DUONG_RAN_DOC:
                    SnakeWayService.gI().addMapConDuongRanDoc(i, zone);
                    break;
                case ConstMap.MAP_KHI_GAS_HUY_DIET :
                    DestronGasService.gI().addMapKhiGasHuyDiet(i, zone);
                    break;
                case ConstMap.MAP_TAY_KARIN:
                    SuperDivineWater.gI().addZone(zone);
                    break;
                case ConstMap.MAP_BOSS_BANG_HOI :
                    BossOfTheGangsService.gI().addMapBossOfTheGangs(i, zone);
                    break;
            }
        }
    }

    public void initNpc(byte[] npcId, short[] npcX, short[] npcY) {
        this.npcs = new ArrayList<>();
        for (int i = 0; i < npcId.length; i++) {
            this.npcs.add(NpcFactory.createNPC(this.mapId, 1, npcX[i], npcY[i], npcId[i]));
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                long st = System.currentTimeMillis();
                for (Zone zone : this.zones) {
                    try {
                        zone.update();
                    } catch (Exception e) {
                        Logger.logException(Map.class, e, "Lỗi update zone");
                    }
                }
                Functions.sleep(Math.max(1000 - (System.currentTimeMillis() - st), 10));
            } catch (Exception e) {
                Logger.logException(Map.class, e, "Lỗi update map " + this.mapName);
            }
        }
    }
    
    public void initMob(byte[] mobTemp, byte[] mobLevel, int[] mobHp, short[] mobX, short[] mobY) {
        for (int i = 0; i < mobTemp.length; i++) {
            int mobTempId = mobTemp[i];
            MobTemplate temp = Manager.getMobTemplateByTemp(mobTempId);
            if (temp != null) {
                Mob mob = new Mob();
                mob.id = i;
                mob.tempId = mobTemp[i];
                mob.level = mobLevel[i];
                mob.point.setHpFull(mobHp[i]);
                mob.location.x = mobX[i];
                mob.location.y = mobY[i];
                mob.point.sethp((mob.point.getHpFull()));
                mob.pDame = temp.percentDame;
                mob.pTiemNang = temp.percentTiemNang;
                mob.percent_gold = temp.percent_gold;
                mob.type = temp.type;
                mob.setTiemNang();
                for (Zone zone : this.zones) {
                    Mob mobZone;
                    switch (mob.tempId) {
                        case ConstMob.HIRUDEGARN:
                            mobZone = new Hirudegarn(mob);
                            break;
                        case ConstMob.VUA_BACH_TUOC:
                            mobZone = new VuaBachTuoc(mob);
                            break;
                        case ConstMob.ROBOT_BAO_VE:
                            mobZone = new RobotBaoVe(mob);
                            break;
                        case ConstMob.GAU_TUONG_CUOP:
                            mobZone = new GauTuongCuop(mob);
                            break;
                        case ConstMob.VOI_CHIN_NGA:
                            mobZone = new VoiChinNga(mob);
                            break;
                        case ConstMob.GA_CHIN_CUA:
                            mobZone = new GaChinCua(mob);
                            break;
                        case ConstMob.NGUA_CHIN_LMAO:
                            mobZone = new NguaChinLmao(mob);
                            break;
                        case ConstMob.PIANO:
                            mobZone = new Piano(mob);
                            break;
                        case ConstMob.KONG:
                            mobZone = new Kong(mob);
                            break;
                        case ConstMob.GOZILLA:
                            mobZone = new Godzila(mob);
                            break;
                        default:
                            mobZone = new Mob(mob);
                            break;
                    }
                    mobZone.zone = zone;
                    zone.mobs.add(mobZone);
                }
            }
        }
    }
    
    public void initMob(List<Mob> mobs) {
        for (Zone zone : zones) {
            for (Mob m : mobs) {
                Mob mob = new Mob(m);
                mob.zone = zone;
                zone.mobs.add(mob);
            }
        }
    }

    private void initTrapMap() {
        for (Zone zone : zones) {
            TrapMap trap = null;
            switch (this.mapId) {
                case 135: {
                    trap = new TrapMap();
                    trap.x = 260;
                    trap.y = 960;
                    trap.w = 740;
                    trap.h = 72;
                    trap.effectId = 49; //xiên
                    zone.trapMaps.add(trap);
                    break;
                }
            }
        }
    }
    
    private void initItem() {
        for (Zone zone : zones) {
            ItemMap itemMap;
            switch (this.mapId) {
                case 21:
                    itemMap = new ItemMap(zone, 74, 1, 633, 315, -1);
                    break;
                case 22:
                    itemMap = new ItemMap(zone, 74, 1, 56, 315, -1);
                    break;
                case 23:
                    itemMap = new ItemMap(zone, 74, 1, 633, 320, -1);
                    break;
                case 42:
                    itemMap = new ItemMap(zone, 78, 1, 70, 288, -1);
                    break;
                case 43:
                    itemMap = new ItemMap(zone, 78, 1, 70, 264, -1);
                    break;
                case 44:
                    itemMap = new ItemMap(zone, 78, 1, 70, 288, -1);
                    break;
                case 85: //1 sao đen
                    itemMap = new ItemMap(zone, 372, 1, 0, 0, -1);
                    break;
                case 86: //2 sao đen
                    itemMap = new ItemMap(zone, 373, 1, 0, 0, -1);
                    break;
                case 87: //3 sao đen
                    itemMap = new ItemMap(zone, 374, 1, 0, 0, -1);
                    break;
                case 88: //4 sao đen
                    itemMap = new ItemMap(zone, 375, 1, 0, 0, -1);
                    break;
                case 89: //5 sao đen
                    itemMap = new ItemMap(zone, 376, 1, 0, 0, -1);
                    break;
                case 90: //6 sao đen
                    itemMap = new ItemMap(zone, 377, 1, 0, 0, -1);
                    break;
                case 91: //7 sao đen
                    itemMap = new ItemMap(zone, 378, 1, 0, 0, -1);
                    break;
            }
        }
    }  
    
    public void initBoss() {
        for (Zone zone : zones) {
            short bossId = -1;
            switch (this.mapId) {
                case 111:
                    bossId = BossID.TAU_PAY_PAY_DONG_NAM_KARIN;
                    break;
                case 114:
                    bossId = BossID.DRABURA;
                    break;
                case 115:
                    bossId = BossID.BUI_BUI;
                    break;
                case 117:
                    bossId = BossID.BUI_BUI_2;
                    break;
                case 118:
                    bossId = BossID.YA_CON;
                    break;
                case 119:
                    bossId = BossID.DRABURA_2;
                    break;
                case 120:
                    bossId = BossID.MABU_12H;
                    break;
                case 127:
                    bossId = BossID.MABU;
                    break;
                case 128:
                    bossId = BossID.SUPERBU;
                    break;
                case 131:
                    bossId = BossID.TAN_BINH_5;
                    break;
                case 132:
                    bossId = BossID.CHIEN_BINH_5;
                    break;
                case 133:
                    bossId = BossID.DOI_TRUONG_5;
                    break;
                }
            if (bossId != -1) {
                Boss boss = BossManager.gI().createBoss(bossId);
                boss.zoneFinal = zone;
                boss.joinMapByZone(zone);
            }
        }
    }

    public short mapIdNextMabu(short mapId) {
        switch (mapId) {
            case 114:
                return 115;
            case 115:
                return 117;
            case 117:
                return 118;
            case 118:
                return 119;
            case 119:
                return 120;
            default:
                return -1;
        }
    }

    public Npc getNpc(Player player, int tempId) {
        for (Npc npc : npcs) {
            if (npc.tempId == tempId && (MapService.gI().isMapBlackBallWar(mapId) || Util.getDistance(player, npc) <= 60)) {
                return npc;
            }
        }
        return null;
    }

    //--------------------------------------------------------------------------
    public int yPhysicInTop(int x, int y) {
        try {
            int rX = (int) x / SIZE;
            int rY = 0;
            if (isTileTop(tileMap[y / SIZE][rX])) {
                return y;
            }
            for (int i = y / SIZE; i < tileMap.length; i++) {
                if (isTileTop(tileMap[i][rX])) {
                    rY = i * SIZE;
                    break;
                }
            }
            return rY;
        } catch (Exception e) {
            return y;
        }
    }
    
    public int yPhysicInTopBot(int x, int y) {
        try {
            int tileX = x / SIZE;
            int tileY = y / SIZE;

            // Nếu tile hiện tại là tile mặt đất, giữ nguyên y
            if (isTileTop(tileMap[tileY][tileX])) {
                return y;
            }

            // Ngược lại vẫn giữ nguyên y (KHÔNG rơi xuống nữa)
            return y;

        } catch (Exception e) {
            return y; // Nếu lỗi, vẫn giữ nguyên y
        }
    }

    public int LastY(int cx, int cy) {
        int num = 0;
        int ySd = 0;
        int xSd = cx;
        if (this.tileTypeAt(cx, cy, 2)) {
            return cy;
        }
        while (num < 30) {
            num++;
            ySd += 24;
            if (this.tileTypeAt(xSd, ySd, 2)) {
                if (ySd % 24 != 0) {
                    ySd -= ySd % 24;
                }
                break;
            }
        }
        return ySd;
    }

    public boolean tileTypeAt(int x, int y, int type) {
        try {
            return (types[y / 24 * tmw + x / 24] & type) == type;
        } catch (Exception e) {
            return false;
        }
    }

    public int[] moveXY(Player player) {
        int xsd = player.location.x / 24;
        int ysd = player.location.y / 24;
        int p = this.mapId == 103 ? 4 : 3;
        if (tileMap[ysd][xsd] != 0) {
            if (tileMap[ysd - p][xsd] != 0) {
                if (tileMap[LastY(player.location.x, player.location.y - p * 24) / 24][xsd] != 0) {
                    return new int[]{
                        player.xSend,
                        player.ySend
                    };
                } else {
                    return new int[]{
                        player.xSend,
                        LastY(player.location.x, 120)
                    };
                }
            }
            return new int[]{
                player.location.x,
                ysd
            };
        }
        if (LastY(player.location.x, player.location.y) >= pxh - 24) {
            return new int[]{
                player.xSend,
                player.ySend
            };
        }
        return new int[]{
            player.location.x,
            ysd
        };
    }

    private boolean isTileTop(int tileMap) {
        for (int i = 0; i < tileTop.length; i++) {
            if (tileTop[i] == tileMap) {
                return true;
            }
        }
        return false;
    }

    public final void readTileMap(int mapId) {
        try {
            try (DataInputStream dis = new DataInputStream(new FileInputStream("data/map/tile_map_data/" + mapId))) {
                dis.readByte();
                tmw = dis.readByte();
                tmh = dis.readByte();
                pxw = tmw * SIZE;
                pxh = tmh * SIZE;
                maps = new int[tmw * tmh];
                for (int j = 0; j < maps.length; j++) {
                    maps[j] = dis.readByte();
                }
                types = new int[maps.length];
            }
        } catch (IOException e) {
        }
    }
    
    public Zone getZoneByIndex(int zoneIndex) {
        if (zoneIndex >= 0 && zoneIndex < this.zones.size()) {
            return this.zones.get(zoneIndex);
        }
        return null;
    }
}
