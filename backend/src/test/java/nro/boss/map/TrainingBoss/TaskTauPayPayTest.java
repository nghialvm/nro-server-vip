package nro.boss.map.TrainingBoss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import QuanLiBoss.Boss;
import QuanLiBoss.BossID;
import QuanLiBoss.BossStatus;
import QuanLiBoss.Manager.BossManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import nro.map.Map;
import nro.map.Zone;
import nro.player.Detu;
import nro.player.Player;
import nro.task.SubTaskMain;
import nro.task.TaskMain;
import org.junit.jupiter.api.Test;

class TaskTauPayPayTest {

    @Test
    void keepsBossAliveForTheEntireDialoguePastTheGenericTimeout() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            boss.currentLevel = 0;
            boss.bossStatus = BossStatus.CHAT_S;
            setLong(boss, "lastTimeBossSpawn", System.currentTimeMillis() - 10_000);

            for (int i = 0; i < 5; i++) {
                setLong(boss, "lastTimeChatS", 0);
                setInt(boss, "timeChatS", 0);
                boss.update();

                assertSame(zone, boss.zone);
                assertTrue(BossManager.gI().getBosses().contains(boss));
            }

            assertEquals(BossStatus.ACTIVE, boss.bossStatus);
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void keepsBossWhenAnotherPlayerJoinsTheOwnersZone() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        Player otherPlayer = addPlayer(zone, "other-player");
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            boss.update();

            assertSame(zone, boss.zone);
            assertTrue(zone.getPlayers().contains(owner));
            assertTrue(zone.getPlayers().contains(otherPlayer));
            assertTrue(BossManager.gI().getBosses().contains(boss));
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void removesBossWhenTaskOwnerLeavesTheZone() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            zone.removePlayer(owner);
            owner.zone = null;

            boss.update();

            assertNull(boss.zone);
            assertFalse(BossManager.gI().getBosses().contains(boss));
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void onlyOwnerAndOwnersDiscipleCanDamageTheTaskBoss() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        Player otherPlayer = addPlayer(zone, "other-player");
        Detu disciple = new Detu(owner);
        disciple.location.x = 775;
        disciple.location.y = 100;
        disciple.nPoint.hpMax = 100;
        disciple.nPoint.hp = 100;
        disciple.zone = zone;
        zone.addPlayer(disciple);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            long initialHp = boss.nPoint.hp;

            assertEquals(0, boss.injured(otherPlayer, 25, true, false));
            assertEquals(initialHp, boss.nPoint.hp);

            assertEquals(25, boss.injured(owner, 25, true, false));
            assertEquals(initialHp - 25, boss.nPoint.hp);

            assertEquals(25, boss.injured(disciple, 25, true, false));
            assertEquals(initialHp - 50, boss.nPoint.hp);
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void creditsTaskProgressWhenOwnerKillsTheBoss() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            SubTaskMain activeSubTask = owner.playerTask.taskMain.subTasks.get(1);

            assertEquals(100, boss.injured(owner, 100, true, false));
            assertEquals(1, activeSubTask.count);
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void creditsTaskProgressToOwnerWhenDiscipleKillsTheBoss() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        Detu disciple = new Detu(owner);
        disciple.location.x = 775;
        disciple.location.y = 100;
        disciple.nPoint.hpMax = 100;
        disciple.nPoint.hp = 100;
        disciple.zone = zone;
        zone.addPlayer(disciple);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            SubTaskMain activeSubTask = owner.playerTask.taskMain.subTasks.get(1);

            assertEquals(100, boss.injured(disciple, 100, true, false));
            assertEquals(1, activeSubTask.count);
        } finally {
            cleanup(boss);
        }
    }

    @Test
    void creditsTaskProgressToOwnerWhenDiscipleIsReportedAsKiller() throws Exception {
        Zone zone = newTestZone();
        Player owner = addTaskOwner(zone);
        Player otherPlayer = addPlayer(zone, "other-player");
        Detu disciple = new Detu(owner);
        TaskTauPayPay boss = newTaskBoss(owner, zone);

        try {
            SubTaskMain activeSubTask = owner.playerTask.taskMain.subTasks.get(1);

            boss.reward(otherPlayer);
            assertEquals(0, activeSubTask.count);

            boss.reward(disciple);
            assertEquals(1, activeSubTask.count);
        } finally {
            cleanup(boss);
        }
    }

    private static TaskTauPayPay newTaskBoss(Player owner, Zone zone) throws Exception {
        TaskTauPayPay boss = new TestTaskTauPayPay(owner, zone);
        boss.nPoint.hpMax = 100;
        boss.nPoint.hp = 100;
        boss.nPoint.def = 0;
        return boss;
    }

    private static class TestTaskTauPayPay extends TaskTauPayPay {

        private TestTaskTauPayPay(Player owner, Zone zone) throws Exception {
            super(owner, BossID.TAUPAYPAY, zone, 1_000, 775, 100);
        }

        @Override
        protected void setDie(Player plAtt) {
        }
    }

    private static Player addTaskOwner(Zone zone) {
        Player owner = addPlayer(zone, "task-owner");
        TaskMain taskMain = new TaskMain();
        taskMain.id = 10;
        taskMain.index = 1;
        taskMain.subTasks.add(subTask(1));
        taskMain.subTasks.add(subTask(2));
        owner.playerTask.taskMain = taskMain;
        return owner;
    }

    private static SubTaskMain subTask(int maxCount) {
        SubTaskMain subTask = new SubTaskMain();
        subTask.maxCount = (short) maxCount;
        return subTask;
    }

    private static Player addPlayer(Zone zone, String name) {
        Player player = new Player();
        player.name = name;
        player.isPlayer = true;
        player.location.x = 775;
        player.location.y = 100;
        player.nPoint.hpMax = 100;
        player.nPoint.hp = 100;
        player.zone = zone;
        zone.addPlayer(player);
        return player;
    }

    private static Zone newTestZone() {
        Map map = new Map(47, "Test Rung Karin", (byte) 0, (byte) 1, (byte) 1,
                (byte) 0, (byte) 0, new int[20][20], new int[]{0}, 1, 10,
                new ArrayList<>(), new ArrayList<>(), (byte) -1);
        return map.zones.get(0);
    }

    private static void setLong(Boss boss, String fieldName, long value) throws Exception {
        Field field = Boss.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setLong(boss, value);
    }

    private static void setInt(Boss boss, String fieldName, int value) throws Exception {
        Field field = Boss.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.setInt(boss, value);
    }

    private static void cleanup(TaskTauPayPay boss) {
        if (boss == null) {
            return;
        }
        if (boss.zone != null) {
            nro.services.Fun.ChangeMapService.gI().exitMap(boss);
        }
        BossManager.gI().removeBoss(boss);
        if (boss.nPoint != null) {
            boss.dispose();
        }
    }
}
