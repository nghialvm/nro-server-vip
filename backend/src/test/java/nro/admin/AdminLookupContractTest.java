package nro.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class AdminLookupContractTest {

    @Test
    void normalizesAndDeduplicatesNumericBatchIds() {
        assertEquals(java.util.List.of("12", "13"), AdminHttpServer.lookupIds("12, 13, 12"));
    }

    @Test
    void rejectsInvalidOrOversizedBatches() {
        assertThrows(RuntimeException.class, () -> AdminHttpServer.lookupIds("12,items"));
        String oversized = IntStream.rangeClosed(1, 201)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
        assertThrows(RuntimeException.class, () -> AdminHttpServer.lookupIds(oversized));
    }

    @Test
    void exposesOnlyKnownLookupKinds() throws Exception {
        var method = AdminHttpServer.class.getDeclaredMethod("lookupDefinition", String.class);
        method.setAccessible(true);
        AdminHttpServer server = AdminHttpServer.gI();
        assertNotNull(method.invoke(server, "items"));
        assertNotNull(method.invoke(server, "clans"));
        assertNotNull(method.invoke(server, "shop-items"));
        assertNotNull(method.invoke(server, "tasks"));
        assertNotNull(method.invoke(server, "side-tasks"));
        assertNotNull(method.invoke(server, "clan-tasks"));
        assertNotNull(method.invoke(server, "kol-tasks"));
        assertNotNull(method.invoke(server, "event-tasks"));
        org.junit.jupiter.api.Assertions.assertNull(method.invoke(server, "arbitrary_table"));
    }

    @Test
    void itemLookupContainsReadOnlyProductStats() throws Exception {
        var method = AdminHttpServer.class.getDeclaredMethod("lookupDefinition", String.class);
        method.setAccessible(true);
        Object definition = method.invoke(AdminHttpServer.gI(), "items");
        var columnsMethod = definition.getClass().getDeclaredMethod("columns");
        columnsMethod.setAccessible(true);
        var columns = (java.util.List<?>) columnsMethod.invoke(definition);
        assertTrue(columns.contains("description"));
        assertTrue(columns.contains("power_require"));
        assertTrue(columns.contains("gold_sell"));
        assertTrue(columns.contains("ruby_sell"));
    }
}
