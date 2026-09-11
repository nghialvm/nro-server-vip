package nro.map.SuperDivineWater;



import nro.effect.EffectSkillService;
import nro.player.Player;
import nro.services.Fun.ChangeMapService;
import nro.services.MapService;
import nro.services.NpcService;
import nro.services.PlayerService;
import nro.services.Service;
import Utils.Util;
import consts.ConstNpc;
import java.util.ArrayList;
import java.util.List;
import nro.boss.Anw.SuperDivineWater.Pocolo;
import nro.map.Zone;
import nro.mob.Mob;

public class SuperDivineWater {

    private static SuperDivineWater instance;
    private final List<Zone> zones = new ArrayList<>();

    public static SuperDivineWater gI() {
        if (instance == null) {
            instance = new SuperDivineWater();
        }
        return instance;
    }

    public void addZone(Zone zone) {
        this.zones.add(zone);
    }

    public void joinMapThanhThuy(Player player) {
        if (player.winSTT && !Util.isAfterMidnight(player.lastTimeWinSTT)) {
            Service.gI().sendThongBao(player, "Hãy gặp thần mèo Karin để sử dụng");
            return;
        } else if (player.winSTT && Util.isAfterMidnight(player.lastTimeWinSTT)) {
            player.winSTT = false;
            player.callBossPocolo = false;
            player.zoneSieuThanhThuy = null;
        }
        Zone zoneJoin = null;
        for (Zone zone : this.zones) {
            if (zone.getNumOfPlayers() + zone.getNumOfBosses() == 0) {
                zoneJoin = zone;
                break;
            }
        }
        if (player.zoneSieuThanhThuy != null) {
            zoneJoin = player.zoneSieuThanhThuy;
        }
        if (zoneJoin != null) {
            init(zoneJoin, player);
            EffectSkillService.gI().setPKSTT(player, 900000);
            ChangeMapService.gI().changeMap(player, zoneJoin, 70, 336);
        } else {
            Service.gI().sendThongBao(player, "Vui lòng thử lại sau ít phút!");
        }
    }

    public void init(Zone zone, Player player) {
        Player mc = zone.getNpc();
        Service.gI().setPos(mc, 100, 336);
        player.zoneSieuThanhThuy = zone;
        long totalDamage = 0;
        long totalHp = 0;

        totalDamage += player.nPoint.dame;
        totalHp += player.nPoint.hpMax;

        for (Mob mob : zone.mobs) {
            switch (mob.tempId) {
                case 26:
                    mob.point.dame = totalHp / 5L;
                    mob.point.maxHp = totalDamage * 20L;
                    break;
                case 25:
                    mob.point.dame = totalHp / 6L;
                    mob.point.maxHp = totalDamage * 5L;
                    break;
                default:
                    mob.point.dame = totalHp / 8L;
                    mob.point.maxHp = totalDamage * 2L;
                    break;
            }
            mob.lvMob = 0;
            mob.hoiSinhMobTayKarin();
        }
    }

    public void update(Player player) {
        try {
            if (player.isPl() && MapService.gI().isMapTayKarin(player.zone.map.mapId) && player.zone == player.zoneSieuThanhThuy) {
                if (!player.callBossPocolo) {
                    boolean allCharactersDead = true;
                    for (Mob mob : player.zone.mobs) {
                        if (!mob.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                    if (allCharactersDead) {
                        try {
                            long bossDamage = player.nPoint.dame + 1L;
                            long bossMaxHealth = player.nPoint.hpMax + 1L;
                            new Pocolo(player.zone,player, bossMaxHealth / 2L, bossDamage * 10L);
                        } catch (Exception e) {
                        }
                        player.callBossPocolo = true;
                    }
                } else if (player.winSTT && Util.canDoWithTime(player.lastTimeWinSTT, 3500)) {
                    if (Util.canDoWithTime(player.lastTimeUpdateSTT, 10000)) {
                        Player mc = player.zone.getNpc();
                        int x = player.location.x + Util.nextInt(-50, 50);
                        int y = 336;
                        PlayerService.gI().playerMove(mc, x, y);
                        if (player.isDie()) {
                            Service.gI().hsChar(player, player.nPoint.hpMax, player.nPoint.hpMax);
                        }
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_SIEU_THAN_THUY, 2119, "Để tôi đưa cậu về", "Đồng ý", "Từ chối");
                        player.lastTimeUpdateSTT = System.currentTimeMillis();
                    }
                }
                if (!player.effectSkill.isPKSTT) {
                    ChangeMapService.gI().changeMap(player, player.gender + 21, -1, -1, 1);
                }
            }
        } catch (Exception e) {
        }
    }
}
