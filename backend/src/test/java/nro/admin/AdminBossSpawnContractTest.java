package nro.admin;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.BossesData;
import QuanLiBoss.Manager.BossManager;
import Boss.nro.boss.task.BlackGoku.BlackGoku;
import com.google.gson.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import nro.skill.NClass;
import nro.skill.Skill;
import nro.server.Manager;
import nro.services.Fun.ChangeMapService;
import nro.template.SkillTemplate;
import org.junit.jupiter.api.Test;

class AdminBossSpawnContractTest {

    @Test
    void exposesBlackGokuFactoryAndConfiguredMapsInTheRuntimeCatalog() {
        Boss boss = BossManager.gI().createBoss(BossID.BLACK_GOKU);
        assertNotNull(boss);
        try {
            assertEquals(-183, BossID.BLACK_GOKU);
            assertArrayEquals(new int[]{102, 92, 93, 94, 96, 97, 98, 99, 100},
                    BossesData.BLACK_GOKU.getMapJoin());
            assertTrue(BossManager.gI().getSpawnOptions().stream()
                    .anyMatch(option -> option.bossId() == BossID.BLACK_GOKU));
        } finally {
            BossManager.gI().removeBoss(boss);
            boss.dispose();
        }
    }

    @Test
    void followsTheRuntimeZoneIndexRules() {
        assertTrue(BossManager.isSpawnZoneAllowed(1, 3, true, false));
        assertTrue(BossManager.isSpawnZoneAllowed(2, 3, false, true));
        assertTrue(BossManager.isSpawnZoneAllowed(0, 1, true, false));
        assertTrue(BossManager.isSpawnZoneAllowed(1, 3, false, false));
        assertFalse(BossManager.isSpawnZoneAllowed(0, 3, true, false));
        assertFalse(BossManager.isSpawnZoneAllowed(3, 3, true, false));
    }

    @Test
    void spawnsBlackGokuAtMap102Zone1AndRejectsTheOccupiedZone() throws Exception {
        BossManager manager = BossManager.gI();
        while (Manager.NCLASS.size() < 3) {
            Manager.NCLASS.add(new NClass());
        }
        addTestSkills();
        Boss representative = manager.createBoss(BossID.BLACK_GOKU);
        nro.map.Map map = new nro.map.Map(102, "Test map 102", (byte) 0, (byte) 1, (byte) 1,
                (byte) 0, (byte) 0, new int[20][20], new int[]{0}, 3, 100,
                new ArrayList<>(), new ArrayList<>(), (byte) -1);
        Manager.MAPS.add(0, map);
        Boss spawned = null;
        try {
            var option = manager.getSpawnOptions().stream()
                    .filter(item -> item.bossId() == BossID.BLACK_GOKU)
                    .findFirst()
                    .orElseThrow();
            var mapOption = option.maps().stream()
                    .filter(item -> item.mapId() == 102)
                    .findFirst()
                    .orElseThrow();
            assertTrue(mapOption.zones().stream().anyMatch(zone -> zone.zoneId() == 1 && zone.available()));

            spawned = manager.spawnBossAt(BossID.BLACK_GOKU, 102, 1);
            assertSame(map.zones.get(1), spawned.zone);
            assertTrue(map.zones.get(1).getBosses().contains(spawned));
            assertFalse(manager.getSpawnOptions().stream()
                    .flatMap(item -> item.maps().stream())
                    .filter(item -> item.mapId() == 102)
                    .flatMap(item -> item.zones().stream())
                    .filter(zone -> zone.zoneId() == 1)
                    .findFirst()
                    .orElseThrow()
                    .available());

            BossManager.BossSpawnException occupied = assertThrows(BossManager.BossSpawnException.class,
                    () -> manager.spawnBossAt(BossID.BLACK_GOKU, 102, 1));
            assertEquals("BOSS_ZONE_OCCUPIED", occupied.code);
        } finally {
            if (spawned != null) {
                if (spawned.zone != null) {
                    ChangeMapService.gI().exitMap(spawned);
                }
                manager.removeBoss(spawned);
                spawned.dispose();
            }
            manager.removeBoss(representative);
            representative.dispose();
            Manager.MAPS.remove(map);
        }
    }

    @Test
    void blackGokuNoHunterResetCanStartTheNextRestCycle() throws Exception {
        BossManager manager = BossManager.gI();
        Boss boss = manager.createBoss(BossID.BLACK_GOKU);
        try {
            Method reset = BlackGoku.class.getDeclaredMethod("autoResetBossBecauseNoHunter");
            reset.setAccessible(true);
            reset.invoke(boss);

            assertEquals(2, boss.currentLevel);
            assertEquals(BossStatus.REST, boss.bossStatus);

            var lastTimeRest = Boss.class.getDeclaredField("lastTimeRest");
            lastTimeRest.setAccessible(true);
            lastTimeRest.setLong(boss, System.currentTimeMillis() - 15 * 60 * 1000L - 1);
            boss.rest();
            assertEquals(BossStatus.RESPAWN, boss.bossStatus);
        } finally {
            manager.removeBoss(boss);
            boss.dispose();
        }
    }

    private static void addTestSkills() {
        int[] skillIds = {Skill.KAMEJOKO, Skill.TAI_TAO_NANG_LUONG, Skill.GALICK};
        for (int skillId : skillIds) {
            SkillTemplate template = new SkillTemplate();
            template.id = (byte) skillId;
            for (int level = 1; level <= 7; level++) {
                Skill skill = new Skill();
                skill.skillId = (short) skillId;
                skill.point = level;
                template.skillss.add(skill);
            }
            Manager.NCLASS.get(0).skillTemplatess.add(template);
        }
    }

    @Test
    void rejectsUnknownFieldsFromTargetedSummonPayloads() throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("action", "summon");
        body.addProperty("bossId", -183);
        body.addProperty("mapId", 102);
        body.addProperty("zoneId", 1);
        body.addProperty("resting", true);

        AdminHttpServer server = AdminHttpServer.gI();
        Method method = AdminHttpServer.class.getDeclaredMethod(
                "validateAllowedFields", JsonObject.class, Set.class, String.class);
        method.setAccessible(true);
        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> method.invoke(server, body, Set.of("action", "bossId", "mapId", "zoneId"), "boss summon"));

        assertEquals("FIELD_NOT_ALLOWED", errorCode(exception));
    }

    private static String errorCode(InvocationTargetException exception) throws Exception {
        var field = exception.getCause().getClass().getDeclaredField("code");
        field.setAccessible(true);
        return (String) field.get(exception.getCause());
    }
}
