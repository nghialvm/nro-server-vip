package nro.player;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NPointAttributeTest {

    @Test
    void serverTnsmAppliesToPlayersAndDisciplesOnly() {
        Player player = new Player();
        player.isPlayer = true;

        Player disciple = new Player();
        disciple.isPlayer = true;
        disciple.isDeTu = true;

        Player bot = new Player();
        bot.isPlayer = true;
        bot.isBot = true;

        assertTrue(NPoint.receivesServerTnsm(player));
        assertTrue(NPoint.receivesServerTnsm(disciple));
        assertFalse(NPoint.receivesServerTnsm(bot));
        assertFalse(NPoint.receivesServerTnsm(null));
    }
}
