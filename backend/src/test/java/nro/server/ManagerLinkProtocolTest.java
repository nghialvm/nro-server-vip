package nro.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ManagerLinkProtocolTest {

    @Test
    void normalizesLegacyServerEntryWithLanguageAndPrioritySuffix() {
        assertEquals("NRO:127.0.0.1:14445:0",
                Manager.normalizeServerLinkEntry("NRO:127.0.0.1:14445:0,0,0"));
    }

    @Test
    void addsDefaultLanguageToBareServerEntry() {
        assertEquals("NRO:127.0.0.1:14445:0",
                Manager.normalizeServerLinkEntry("NRO:127.0.0.1:14445"));
    }

    @Test
    void usesConfiguredEntriesWithoutPrependingCurrentServer() {
        assertEquals("NRO:127.0.0.1:14445:0",
                Manager.buildServerLinkList("Vũ Trụ 1", "127.0.0.1", 14445,
                        Arrays.asList("NRO:127.0.0.1:14445:0")));
    }

    @Test
    void fallsBackToCurrentServerWhenNoEntryIsConfigured() {
        assertEquals("Vũ Trụ 1:127.0.0.1:14445:0",
                Manager.buildServerLinkList("Vũ Trụ 1", "127.0.0.1", 14445,
                        Arrays.asList()));
    }
}
