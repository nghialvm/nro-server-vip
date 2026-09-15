package nro.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import network.io.Message;
import org.junit.jupiter.api.Test;

class GuestAccountProtocolTest {

    @Test
    void encodesTheGuestUsernameInTheMinus101Response() throws Exception {
        Message response = Controller.createGuestAccountResponse("guest0123456789abc");
        Message decoded = new Message(response.command, response.getData());
        try {
            assertEquals((byte) -101, response.command);
            assertEquals("guest0123456789abc", decoded.readUTF());
            assertEquals(0, decoded.reader().available());
        } finally {
            response.cleanup();
            decoded.cleanup();
        }
    }

    @Test
    void refusesAnEmptyGuestUsernameResponse() {
        assertThrows(IllegalArgumentException.class,
                () -> Controller.createGuestAccountResponse(""));
    }
}
