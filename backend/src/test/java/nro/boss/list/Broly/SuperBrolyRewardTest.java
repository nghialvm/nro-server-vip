package nro.boss.list.Broly;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import QuanLiBoss.Boss;
import QuanLiBoss.Manager.BossManager;
import QuanLiBoss.Manager.BrolyManager;
import consts.ConstDetu;
import java.util.Arrays;
import java.util.List;
import nro.player.Detu;
import nro.player.Player;
import org.junit.jupiter.api.Test;

class SuperBrolyRewardTest {

    @Test
    void grantsMabuDiscipleToPlayersWithoutADisciple() throws Exception {
        for (BossFactory factory : superBrolyFactories()) {
            Boss boss = factory.create();
            Player player = new Player();

            try {
                boss.reward(player);

                Detu disciple = awaitMabuDisciple(player, null);
                assertNotNull(disciple);
                assertEquals(ConstDetu.MABU, disciple.typeDeTu);
            } finally {
                cleanup(boss);
            }
        }
    }

    @Test
    void replacesExistingDiscipleWithMabuForEverySuperBrolyVariant() throws Exception {
        for (BossFactory factory : superBrolyFactories()) {
            Boss boss = factory.create();
            Player player = new Player();
            Detu previousDisciple = new Detu(player);
            previousDisciple.typeDeTu = 0;
            player.Detu = previousDisciple;

            try {
                boss.reward(player);

                Detu disciple = awaitMabuDisciple(player, previousDisciple);
                assertNotNull(disciple);
                assertNotSame(previousDisciple, disciple);
                assertEquals(ConstDetu.MABU, disciple.typeDeTu);
            } finally {
                cleanup(boss);
            }
        }
    }

    private static List<BossFactory> superBrolyFactories() {
        return Arrays.asList(SuperBroly::new, SuperBrolyZone0::new, SuperBrolyNew::new);
    }

    private static Detu awaitMabuDisciple(Player player, Detu previousDisciple) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 3_000;

        while (System.currentTimeMillis() < deadline) {
            Detu disciple = player.Detu;
            if (disciple != null
                    && disciple != previousDisciple
                    && disciple.typeDeTu == ConstDetu.MABU) {
                return disciple;
            }
            Thread.sleep(10);
        }

        return player.Detu;
    }

    private static void cleanup(Boss boss) {
        BossManager.gI().removeBoss(boss);
        BrolyManager.gI().removeBoss(boss);
        boss.dispose();
    }

    @FunctionalInterface
    private interface BossFactory {

        Boss create() throws Exception;
    }
}
