package nro.services;

import consts.ConstMap;
import nro.map.Map;
import nro.map.WayPoint;
import nro.map.Zone;
import nro.mob.Mob;
import nro.player.Player;
import nro.server.Manager;
import network.io.Message;
import nro.services.Fun.ChangeMapService;
import Utils.Logger;
import Utils.Util;
import java.util.ArrayList;
import java.util.List;

public class MapService {

    private static MapService i;

    public static MapService gI() {
        if (i == null) {
            i = new MapService();
        }
        return i;
    }
    
    public boolean isMapPotara(int mapId) {
        return mapId >= 189 && mapId <= 192;
    }
    
    public boolean isMapBinhHutNangLuong(int mapId) {
        return mapId == 180;
    }
    
    public boolean isMapNotDropItems(int mapId) {
        return isMapBanDoKhoBau(mapId) || isMapDoanhTrai(mapId) || isMapConDuongRanDoc(mapId) || isMapKhiGasHuyDiet(mapId) || isMapGiaiCuuMiNuong(mapId) || isMapTayKarin(mapId) || 
                isMapBossBangHoi(mapId) || MapService.gI().isMapHirudegarn(mapId) || MapService.gI().isMapTranhNgocNamec(mapId) || 
                MapService.gI().isMapRiengTu(mapId);
    }

    public WayPoint getWaypointPlayerIn(Player player) {
        for (WayPoint wp : player.zone.map.wayPoints) {
            if (player.location.x >= wp.minX && player.location.x <= wp.maxX && player.location.y >= wp.minY && player.location.y <= wp.maxY) {
                return wp;
            }
        }
        return null;
    }

    public Zone getMapCanJoin(Player player, int mapId, int zoneId) {
        if (isMapOffline(mapId) || isMapBangHoi(mapId)) {
            return getMapById(mapId).zones.get(0);
        }

        if (this.isMapDoanhTrai(mapId) && (player.zone == null || player.clan == null || player.clan.doanhTrai == null)) {
            Zone zone = getZone(21 + player.gender);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapBanDoKhoBau(mapId) && (player.zone == null || player.clan == null || player.clan.BanDoKhoBau == null)) {
            Zone zone = getZone(5);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapKhiGasHuyDiet(mapId) && (player.zone == null || player.clan == null || player.clan.KhiGasHuyDiet == null)) {
            Zone zone = getZone(5);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapConDuongRanDoc(mapId) && (player.zone == null || player.clan == null || player.clan.ConDuongRanDoc == null)) {
            Zone zone = getZone(48);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }
        
        if (this.isMapBossBangHoi(mapId) && (player.zone == null || player.clan == null || player.clan.BossOfTheGang == null)) {
            Zone zone = getZone(153);
            player.location.x = Util.nextInt(100, zone.map.mapWidth - 100);
            player.location.y = zone.map.yPhysicInTop(player.location.x, 100);
            return zone;
        }

        if (this.isMapDoanhTrai(mapId) && player.clan != null && player.clan.doanhTrai != null) {
            if (this.isMapDoanhTrai(player.zone.map.mapId)) {
                boolean allCharactersDead = true;

                for (Mob mob : player.clan.doanhTrai.getMapById(mapId).mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player boss : player.clan.doanhTrai.getMapById(mapId).getBosses()) {
                        if (!boss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }

                if (allCharactersDead) {
                    return player.clan.doanhTrai.getMapById(mapId);
                }

                for (Mob mob : player.zone.mobs) {
                    if (!mob.isDie()) {
                        return null;
                    }
                }

                for (Player boss : player.zone.getBosses()) {
                    if (!boss.isDie()) {
                        return null;
                    }
                }
            }
            return player.clan.doanhTrai.getMapById(mapId);
        }
        
        if (this.isMapBanDoKhoBau(mapId) && player.clan != null && player.clan.BanDoKhoBau != null) {
            if (this.isMapBanDoKhoBau(player.zone.map.mapId)) {
                boolean allCharactersDead = true;

                for (Mob mob : player.clan.BanDoKhoBau.getMapById(mapId).mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player boss : player.clan.BanDoKhoBau.getMapById(mapId).getBosses()) {
                        if (!boss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }

                if (allCharactersDead) {
                    return player.clan.BanDoKhoBau.getMapById(mapId);
                }

                for (Mob mob : player.zone.mobs) {
                    if (!mob.isDie()) {
                        return null;
                    }
                }

                for (Player boss : player.zone.getBosses()) {
                    if (!boss.isDie()) {
                        return null;
                    }
                }
            }
            return player.clan.BanDoKhoBau.getMapById(mapId);
        }

        if (this.isMapKhiGasHuyDiet(mapId) && player.clan != null && player.clan.KhiGasHuyDiet != null) {
            if (this.isMapKhiGasHuyDiet(player.zone.map.mapId)) {
                boolean allCharactersDead = true;

                for (Mob mob : player.clan.KhiGasHuyDiet.getMapById(mapId).mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player boss : player.clan.KhiGasHuyDiet.getMapById(mapId).getBosses()) {
                        if (!boss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }

                if (allCharactersDead) {
                    return player.clan.KhiGasHuyDiet.getMapById(mapId);
                }

                for (Mob mob : player.zone.mobs) {
                    if (!mob.isDie()) {
                        return null;
                    }
                }

                for (Player boss : player.zone.getBosses()) {
                    if (!boss.isDie()) {
                        return null;
                    }
                }
            }
            return player.clan.KhiGasHuyDiet.getMapById(mapId);
        }

        if (this.isMapConDuongRanDoc(mapId) && player.clan != null && player.clan.ConDuongRanDoc != null) {
            if (this.isMapConDuongRanDoc(player.zone.map.mapId)) {
                boolean allCharactersDead = true;

                for (Mob mob : player.clan.ConDuongRanDoc.getMapById(mapId).mobs) {
                    if (!mob.isDie()) {
                        allCharactersDead = false;
                        break;
                    }
                }

                if (allCharactersDead) {
                    for (Player boss : player.clan.ConDuongRanDoc.getMapById(mapId).getBosses()) {
                        if (!boss.isDie()) {
                            allCharactersDead = false;
                            break;
                        }
                    }
                }

                if (allCharactersDead) {
                    return player.clan.ConDuongRanDoc.getMapById(mapId);
                }

                for (Mob mob : player.zone.mobs) {
                    if (!mob.isDie()) {
                        return null;
                    }
                }

                for (Player boss : player.zone.getBosses()) {
                    if (!boss.isDie()) {
                        return null;
                    }
                }
            }
            return player.clan.ConDuongRanDoc.getMapById(mapId);
        }

        //**********************************************************************
        if (zoneId == -1) { //vào khu bất kỳ
            return getZone(mapId);
        } else {
            return getZoneByMapIDAndZoneID(mapId, zoneId);
        }
    }
    

    public int GetZone(int mapId) {
        Map map = getMapById(mapId);
        int x = 20;
        int y = Util.nextInt(0, map.zones.size() - 1);
        for (int j = 0; j < map.zones.size(); j++) {
            if (map.zones.get(j).getNumOfPlayers() < x) {
                y = j;
                x = map.zones.get(j).getNumOfPlayers();
            }
        }
        return y;
    }

    public Zone getZone(int mapId) {
        Map map = getMapById(mapId);
        if (map == null) {
            return null;
        }
        int z = 0;
        while (map.zones.get(z).getNumOfPlayers() >= map.zones.get(z).maxPlayer) {
            z = Util.nextInt(0, map.zones.size() - 1);
        }
        return map.zones.get(z);
        
    }

    public Zone getZoneJoinByMapIdAndZoneId(Player player, int mapId, int zoneId) {
       
        Map map = getMapById(mapId);
        Zone zoneJoin = null;
        try {
            if (map != null) {
                zoneJoin = map.zones.get(zoneId);
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
        return zoneJoin;
    }

    
    private Zone getZoneByMapIDAndZoneID(int mapId, int zoneId) {
        Zone zoneJoin = null;
        try {
            Map map = getMapById(mapId);
            if (map != null) {
                zoneJoin = map.zones.get(zoneId);
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
        return zoneJoin;
    }

    public Map getMapById(int mapId) {
        for (Map map : Manager.MAPS) {
            if (map.mapId == mapId) {
                return map;
            }
        }
        return null;
    }
    
    public Map getMapByGender(int genderType) {
        for (Map map : Manager.MAPS) {
            if (map.genderType == genderType) {
                return map;
            }
        }
        return null;
    }
    

    /**
     * Trả về 1 map random cho boss
     *
     * @param mapId
     * @return
     */
    public Zone getMapWithRandZone(int mapId) {
        Map map = MapService.gI().getMapById(mapId);
        Zone zone = null;
        try {
            if (map != null) {
                zone = map.zones.get(Util.nextInt(map.zones.size()));
            }
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
        return zone;
    }

    public String getPlanetName(byte planetId) {
        switch (planetId) {
            case 0:
                return "Trái đất";
            case 1:
                return "Namếc";
            case 2:
                return "Xayda";
            default:
                return "";
        }
    }

    /**
     * lấy danh sách map cho capsule
     *
     * @param pl
     * @return
     */
    public List<Zone> getMapCapsule(Player pl) {
        List<Zone> list = new ArrayList<>();
        if (pl.mapBeforeCapsule != null && pl.mapBeforeCapsule.map.mapId != 21 && pl.mapBeforeCapsule.map.mapId != 22 && pl.mapBeforeCapsule.map.mapId != 23 
                && !isMapTuongLai(pl.mapBeforeCapsule.map.mapId)) {
            addListMapCapsule(pl, list, pl.mapBeforeCapsule);
        }
        addListMapCapsule(pl, list, getMapCanJoin(pl, 21 + pl.gender, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 47, 0));
        if (pl.levelLuyenTap > 1) { 
            addListMapCapsule(pl, list, getMapCanJoin(pl, 45, 0));
        }
        addListMapCapsule(pl, list, getMapCanJoin(pl, 0, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 7, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 14, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 5, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 20, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 13, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 24 + pl.gender, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 27, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 19, 0));
        addListMapCapsule(pl, list, getMapCanJoin(pl, 79, 0));
        if (pl.nPoint.power > 20_000_000) { 
            addListMapCapsule(pl, list, getMapCanJoin(pl, 84, 0));
        }
        if (pl.levelLuyenTap > 5) { 
            addListMapCapsule(pl, list, getMapCanJoin(pl, 154, 0));
        }
        return list;
    }

    public List<Zone> getMapBlackBall() {
        List<Zone> list = new ArrayList<>();
        for (int j = 0; j < 7; j++) {
            list.add(getMapById(85 + j).zones.get(0));
        }
        return list;
    }

    public List<Zone> getMapMaBu() {
        List<Zone> list = new ArrayList<>();
        for (int j = 0; j < 7; j++) {
            list.add(getMapById(114 + j).zones.get(0));
        }
        return list;
    }

    private void addListMapCapsule(Player pl, List<Zone> list, Zone zone) {
        for (Zone z : list) {
            if (z != null && zone != null && z.map.mapId == zone.map.mapId) {
                return;
            }
        }
        if (zone != null && pl.zone.map.mapId != zone.map.mapId) {
            list.add(zone);
        }
    }

    public void sendPlayerMove(Player player) {
        Message msg;
        try {
            msg = new Message(-7);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.location.x);
            msg.writer().writeShort(player.location.y);
            Service.gI().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(MapService.class, e);
        }
    }
    
    public boolean isMapOffline(int mapId) {
        for (Map map : Manager.MAPS) {
            if (map.mapId == mapId) {
                return map.type == ConstMap.MAP_OFFLINE;
            }
        }
        return false;
    }
    
    public boolean isMapBangHoi(int mapId) {
        for (Map map : Manager.MAPS) {
            if (map.mapId == mapId) {
                return map.type == ConstMap.MAP_BANG_HOI;
            }
        }
        return false;
    }
    
    public boolean isMapNotCanJoinPet(int mapId) {
        return MapService.gI().isMapOffline(mapId) || MapService.gI().isMapBangHoi(mapId) || MapService.gI().isMapPotaufeu(mapId) || MapService.gI().isMapWar(mapId)
                || MapService.gI().isMapTestDame(mapId) || MapService.gI().isMapTranhNgocNamec(mapId) || MapService.gI().isMapHungVuongEvent(mapId) || isMapRiengTu(mapId);
    }
    
    public boolean shouldChangeMap(int currentMapId, int newMapId) {
        return MapService.gI().isMapKhiGasHuyDiet(currentMapId)
                && MapService.gI().isMapKhiGasHuyDiet(newMapId)
                && currentMapId != 148 && newMapId != 148;
    }
    
    public boolean isMapNoNottify(int mapId) {
        return isMapPhoBan(mapId) || isMapBlackBallWar(mapId) || isMapMaBu12H(mapId) || isMapMabu14H(mapId) || isMapLuyenTap(mapId) || isMapDongNamKarin(mapId) || isMapYardart(mapId) || isMapOffline(mapId) || isMapBangHoi(mapId) || isMapRiengTu(mapId);
    }
    
    public boolean isHome(int mapId) {
        return mapId >= 21 && mapId <= 23;
    }
    
    public boolean isMapLang(int mapId) {
        return mapId == 7 || mapId == 14 || mapId == 0;
    }
     public boolean isMapBang(int mapId) {
        return mapId == 156 || mapId == 157 || mapId == 158 || mapId == 159;
    }
    
    public boolean isMapTestDame(int mapId) {
        return mapId >= 169 && mapId <= 171;
    }
    
    public boolean isMapHanhTinhThucVat(int mapId) {
        return mapId >= 160 && mapId <= 163;
    }
    
    public boolean isMapHungVuongEvent(int mapId) {
        return mapId >= 181 && mapId <= 184;
    }
    
    public boolean isMapRiengTu(int mapId) {
        return mapId >= 185 && mapId <= 187;
    }
    
    public boolean isMapTranhNgocNamec(int mapId) {
        return mapId == 164;
    }
    
    public boolean isMapLuyenTap(int mapId) {
        return mapId >= 45 && mapId <= 50 && mapId != 47;
    }
    
    public boolean isMapHirudegarn(int mapId) {
        return mapId == 126;
    }
    
    public boolean isMapgranola(int mapId) {
        return mapId == 194;
    }
    
    public boolean isMapsontinh(int mapId) {
        return mapId == 207;
    }
    public boolean isMapthuytinh(int mapId) {
        return mapId == 208;
    }

    public boolean isMapMabu14H(int mapId) {
        return mapId == 127 || mapId == 128;
    }
    
    public boolean isMapDongNamKarin(int mapId) {
        return mapId == 111;
    }
    public boolean isMapTayKarin(int mapId) {
        return mapId == 146;
    }
            
    public boolean isMapDiaNguc(int mapId) {
        return mapId == 167 || mapId == 168 || mapId == 172 || mapId == 173;
    }
    
    public boolean isMapUpSKH(int mapId) {
        return mapId == 1 || mapId == 2 || mapId == 3 || mapId == 8 || mapId == 9 || mapId == 11 || mapId == 15 || mapId == 16 || mapId == 17;
    }
    
    public boolean isMapMobBangHoi_Part1(int mapId) {
        return mapId == 27 || mapId == 28 || mapId == 31 || mapId == 32 || mapId == 35 || mapId == 36;
    }
    
    public boolean isMapKongvsGodzila(int mapId) {
        return mapId == 181;
    }
    
    public boolean isMapSauLang(int mapId) {
        return mapId == 1 || mapId == 8 || mapId == 15;
    }
        
    public Map getMapForCalich() {
        int mapId = Util.nextInt(27, 29);
        return MapService.gI().getMapById(mapId);
    }
    
    public Map getMapForHoaHong() {
        int[] mapIds = {1, 2, 3, 6, 8, 9, 10, 11, 12, 15, 16, 17, 18, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 42};
        int mapId = mapIds[Util.nextInt(0, mapIds.length - 1)];
        return MapService.gI().getMapById(mapId);
    }
    
    public boolean isMapBlackBallWar(int mapId) {
        return mapId >= 85 && mapId <= 91;
    }

    public boolean isMapMaBu12H(int mapId) {
        return mapId >= 114 && mapId <= 120;
    }
    
    public boolean isMapDoanhTrai(int mapId) {
        return mapId >= 53 && mapId <= 62;
    }
    
    public boolean isMapBanDoKhoBau(int mapId) {
        return mapId >= 135 && mapId <= 138;
    }
    public boolean isMapKVTH(int mapId) {
        return mapId ==179;
    }

    public boolean isMapConDuongRanDoc(int mapId) {
        return mapId >= 141 && mapId <= 144;
    }

    public boolean isMapKhiGasHuyDiet(int mapId) {
        return mapId >= 147 && mapId <= 152 && mapId != 150;
    }
    
    public boolean isMapBossBangHoi(int mapId) {
        return mapId == 165;
    }
    
    public boolean isMapPhoBan(int mapId) {
        return isMapBanDoKhoBau(mapId) || isMapDoanhTrai(mapId) || isMapConDuongRanDoc(mapId) || isMapKhiGasHuyDiet(mapId) || isMapGiaiCuuMiNuong(mapId) || isMapTayKarin(mapId) || isMapBossBangHoi(mapId);
    }
    
    public boolean isMapYardart(int mapId) {
        return mapId >= 131 && mapId <= 133;
    }   
    
    public boolean isMapHanhTinhNgucTu(int mapId) {
        return mapId == 155 || mapId == 206;
    }    

    public boolean isMapPVP(int mapId) {
        return mapId == 112;
    }

    public boolean isMapCold(Map map) {
        int mapId = map.mapId;
        return ((mapId >= 105 && mapId <= 110) || mapId == 158 || mapId == 159 || mapId == 152);
    }
    public boolean isMapCold(int mapId) {
        
        return ((mapId >= 105 && mapId <= 110) || mapId == 158 || mapId == 159 || mapId == 152);
    }
    
    public boolean isMapChristMasEvent(int mapId) {
        return (mapId >= 174 && mapId <= 179);
    }
    
    public boolean isMapColdforBot(int mapId) {
        return (mapId >= 105 && mapId <= 110) || (mapId >= 174 && mapId <= 179);
    }
    
    public boolean isMapThanhDia(int mapId) {
        return (mapId >= 156 && mapId <= 159);
    }
    
    public boolean isMapCereal(Map map) {
        int mapId = map.mapId;
        return mapId >= 194 && mapId <= 201;
    }
    
    public boolean isMapVohinh(Map map) {
        int mapId = map.mapId;
        return mapId == 201;
    }   
            
    public boolean isMapPotaufeu(int mapId) {
        return mapId >= 139 && mapId <= 140;
    }
    
    public boolean isMapTanThu(int mapId) {
        return mapId >= 0 && mapId <= 38;
    }
    
    public boolean isMapNguHanhSon(int mapId) {
        return mapId >= 122 && mapId <= 124;
    }
    
    public boolean islanhdiabanghoi(int mapId) {
        return mapId >= 156 && mapId <= 159;
    }

    public boolean isMapGiaiCuuMiNuong(int mapId) {
        return (mapId >= 185 && mapId <= 188) || (mapId >= 207 && mapId <= 209);
    }

    public boolean isMapTuongLai(int mapId) {
        return (mapId >= 92 && mapId <= 94) || (mapId >= 96 && mapId <= 100) || mapId == 102 || mapId == 103;
    }
    
    public boolean isMap5000NamTruoc(int mapId) {
        return mapId >= 210 && mapId <= 248;
    }
    
    public boolean isMapWar(int mapId) {
        return mapId == 51 || mapId == 112 || mapId == 113 || mapId == 129;
    }

    public void goToMap(Player player, Zone zoneJoin) {
        Zone oldZone = player.zone;
        if (oldZone != null) {
            ChangeMapService.gI().exitMap(player);
            if (player.DeTrung != null) {
                player.DeTrung.goToMap(zoneJoin);
            }
        }
        player.zone = zoneJoin;
        player.zone.addPlayer(player);
    }   

    public boolean isMapKhongCoSieuQuai(int mapId) {
        return mapId != 4 && mapId != 27 && mapId != 28 && mapId != 12 && mapId != 31 && mapId != 32 && mapId != 18 && mapId != 35 && mapId != 36;
    }
    
    public boolean isMapCallDragon(int mapId) {
        return (mapId >= 1 && mapId <= 6) || (mapId >= 8 && mapId <= 13) || (mapId >= 15 && mapId <= 20) || (mapId >= 27 && mapId <= 38);
    }
    
    public List<Player> getAllPlayerInMap(int mapId) {
        Map map = getMapById(mapId);
        if (map == null) {
            return null;
        }
        List<Player> players = new ArrayList<Player>();
        for (Zone zone : map.zones) {
            players.addAll(zone.getPlayers());
        }
        return players;
    }
}
