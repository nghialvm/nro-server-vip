package event;



import QuanLiBoss.Manager.BossManager;
import nro.server.Manager;
import nro.services.MapService;
import nro.services.Service;
import Utils.Logger;
import consts.ConstMob;
import event.IEvent.IEvent;
import java.util.Set;
import nro.map.Map;
import nro.map.Zone;
import nro.mob.BigBoss;
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
import nro.mob.Mob;
import nro.npc.NpcFactory;
import nro.player.Player;
import nro.server.Client;
import nro.template.MobTemplate;

public abstract class Event implements IEvent {

    @Override
    public void init() {
        npc();
        boss();
        itemMap();
        itemBoss();
        bigboss();
        mob();
        runEvent();
    }
    
    @Override
    public void bigboss() {

    }

    @Override
    public void createBigBoss(int mapId, int BigbossTempId, byte Level, int zoneId, long hpBigboss, long dameBigboss, int x, int y) {
        Map map = MapService.gI().getMapById(mapId);
        if (map == null) return;
        Zone zone = map.zones.get(zoneId);
        if (zone == null) return;
        MobTemplate temp = Manager.getMobTemplateByTemp(BigbossTempId);
        if (temp == null) return;
        Mob mob = new Mob();
        mob.zone = zone;
        mob.id = zone.getNewMobId();
        mob.tempId = BigbossTempId;
        mob.level = Level;
        mob.point.setHpFull(hpBigboss);
        mob.point.sethp(hpBigboss);
        mob.point.dame = dameBigboss;
        mob.location.x = x;
        mob.location.y = mob.zone.map.yPhysicInTop(x, y);
        mob.type = temp.type;
        mob.pDame = temp.percentDame;
        mob.pTiemNang = temp.percentTiemNang;
        mob.setTiemNang();
        Mob mobBoss;
        switch (BigbossTempId) {
            case ConstMob.HIRUDEGARN:
                mobBoss = new Hirudegarn(mob);
                break;
            case ConstMob.VUA_BACH_TUOC:
                mobBoss = new VuaBachTuoc(mob);
                break;
            case ConstMob.ROBOT_BAO_VE:
                mobBoss = new RobotBaoVe(mob);
                break;
            case ConstMob.GAU_TUONG_CUOP:
                mobBoss = new GauTuongCuop(mob);
                break;
            case ConstMob.VOI_CHIN_NGA:
                mobBoss = new VoiChinNga(mob);
                break;
            case ConstMob.GA_CHIN_CUA:
                mobBoss = new GaChinCua(mob);
                break;
            case ConstMob.NGUA_CHIN_LMAO:
                mobBoss = new NguaChinLmao(mob);
                break;
            case ConstMob.PIANO:
                mobBoss = new Piano(mob);
                break;
            case ConstMob.KONG:
                mobBoss = new Kong(mob);
                break;
            case ConstMob.GOZILLA:
                mobBoss = new Godzila(mob);
                break;
            default:
                mobBoss = new BigBoss(mob);
                break;
        }
        zone.mobs.add(mobBoss);
    }
    
    @Override
    public void mob() {

    }

    @Override
    public void createMob(int mapId, int BigbossTempId, byte Level, int zoneId, long hpBigboss, long dameBigboss, int x, int y) {
        
    }

    @Override
    public void npc() {

    }

    @Override
    public void createNpc(int mapId, int npcId, int x, int y) {
        MapService.gI().getMapById(mapId).npcs.add(NpcFactory.createNPC(mapId, 1, x, y, npcId));
    }

    @Override
    public void boss() {

    }

    @Override
    public void createBoss(int bossId, int... total) {
        int len = 1;
        if (total.length > 0) {
            len = total[0];
        }
        try {
            for (int i = 0; i < len; i++) {
                BossManager.gI().createBoss(bossId);
            }
        } catch (Exception e) {
            Logger.error(e + "\n");
        }
    }

    @Override
    public void itemMap() {

    }

    @Override
    public void itemBoss() {

    }
    
    @Override
    public void runEvent() {
        removeOldEventNpcs();
        for (Player p : Client.gI().getPlayers()) {
            Service.gI().UpdateAllMap(p);
        }
    }
    
    Set<Integer> mapIds = Set.of(0);
    
    private void removeOldEventNpcs() {
        if (!EventManager.LUNNAR_NEW_YEAR) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 66);
            Manager.NPCS.removeIf(npc -> npc.tempId == 79);
            Manager.NPCS.removeIf(npc -> npc.tempId == 80);
            Manager.NPCS.removeIf(npc -> mapIds.contains(npc.mapId) && (npc.tempId == 49));
        }
        if (!EventManager.CHRISTMAS) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 81);
            Manager.NPCS.removeIf(npc -> npc.tempId == 82);
        }
        if (!EventManager.VU_LAN_FESTIVAL) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 83);
        }
        if (!EventManager.HALLOWEEN) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 84);
        }
        if (!EventManager.INTERNATIONAL_WOMANS_DAY) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 90);
            Manager.NPCS.removeIf(npc -> npc.tempId == 91);
        }
        if (!EventManager.TRUNG_THU) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 41);
            Manager.NPCS.removeIf(npc -> npc.tempId == 66);
            Manager.NPCS.removeIf(npc -> npc.tempId == 92);
            Manager.NPCS.removeIf(npc -> npc.tempId == 93);
        }
        if (!EventManager.HUNG_VUONG) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 51);
            Manager.NPCS.removeIf(npc -> npc.tempId == 52);
            Manager.NPCS.removeIf(npc -> npc.tempId == 66);
        }
        if (!EventManager.BLACK_FRIDAY) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 96);
        }
        if (!EventManager.DAY_20_10) {
            Manager.NPCS.removeIf(npc -> npc.tempId == 100);
        }
        
        for (Map map : Manager.MAPS) {
            if (!EventManager.LUNNAR_NEW_YEAR) {
                map.npcs.removeIf(npc -> npc.tempId == 66);
                map.npcs.removeIf(npc -> npc.tempId == 79);
                map.npcs.removeIf(npc -> npc.tempId == 80);
                map.npcs.removeIf(npc -> mapIds.contains(npc.mapId) && (npc.tempId == 49));
            }
            if (!EventManager.CHRISTMAS) {
                map.npcs.removeIf(npc -> npc.tempId == 81);
                map.npcs.removeIf(npc -> npc.tempId == 82);
            }
            if (!EventManager.VU_LAN_FESTIVAL) {
                map.npcs.removeIf(npc -> npc.tempId == 83);
            }
            if (!EventManager.HALLOWEEN) {
                map.npcs.removeIf(npc -> npc.tempId == 84);
            }
            if (!EventManager.INTERNATIONAL_WOMANS_DAY) {
                map.npcs.removeIf(npc -> npc.tempId == 90);
                map.npcs.removeIf(npc -> npc.tempId == 91);
            }
            if (!EventManager.TRUNG_THU) {
                map.npcs.removeIf(npc -> npc.tempId == 41);
                map.npcs.removeIf(npc -> npc.tempId == 66);
                map.npcs.removeIf(npc -> npc.tempId == 92);
                map.npcs.removeIf(npc -> npc.tempId == 93);
            }
            if (!EventManager.HUNG_VUONG) {
                map.npcs.removeIf(npc -> npc.tempId == 51);
                map.npcs.removeIf(npc -> npc.tempId == 52);
                map.npcs.removeIf(npc -> npc.tempId == 66);
            }
            if (!EventManager.BLACK_FRIDAY) {
                map.npcs.removeIf(npc -> npc.tempId == 96);
            }
            if (!EventManager.DAY_20_10) {
                map.npcs.removeIf(npc -> npc.tempId == 100);
            }
        }
    }
}





