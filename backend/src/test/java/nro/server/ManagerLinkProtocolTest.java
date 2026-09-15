package nro.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
