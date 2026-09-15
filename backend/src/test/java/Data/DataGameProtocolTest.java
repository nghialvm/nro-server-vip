package Data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import network.io.Message;
import org.junit.jupiter.api.Test;

class DataGameProtocolTest {

    @Test
    void encodesBothTrailingFlagsExpectedByTheClient() throws Exception {
        Message response = DataGame.createLinkIPMessage();
        Message decoded = new Message(response.command, response.getData());
        try {
            assertEquals((byte) -29, response.command);
            assertEquals((byte) 2, decoded.readByte());
            assertTrue(decoded.readUTF().contains(":0"));
            assertEquals((byte) 1, decoded.readByte());
            assertEquals((byte) 0, decoded.readByte());
            assertEquals(0, decoded.reader().available());
        } finally {
            response.cleanup();
            decoded.cleanup();
        }
    }
}
