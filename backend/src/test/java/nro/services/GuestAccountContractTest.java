package nro.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class GuestAccountContractTest {

    private static final Pattern GUEST_USERNAME = Pattern.compile("guest[a-f0-9]{15}");

    @Test
    void generatesAUniqueGuestUsernameWithinTheAccountColumnLimit() {
        String first = Service.createGuestUsername();
        String second = Service.createGuestUsername();

        assertTrue(GUEST_USERNAME.matcher(first).matches());
        assertTrue(GUEST_USERNAME.matcher(second).matches());
        assertEquals(20, first.length());
        assertEquals(20, second.length());
        assertTrue(!first.equals(second));
    }

    @Test
    void usesAnActiveNonAdminAccountWithTheRequiredLegacyDefaults() {
        assertArrayEquals(
                new Object[]{"guest0123456789abc", "", 1, 0, 0, 0, 45, 0, 0, 0, 0},
                Service.guestAccountValues("guest0123456789abc"));
    }
}
