package nro.services;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import network.io.Message;
import org.junit.jupiter.api.Test;

class ServiceRegistrationTest {

    @Test
    void parsesCurrentTwoFieldFormat() throws Exception {
        Message message = messageWithUtfFields("user01", "pass01");

        assertArrayEquals(
                new String[]{"user01", "pass01"},
                Service.parseRegistrationCredentials(message));
    }

    @Test
    void parsesLegacyNineFieldFormat() throws Exception {
        Message message = messageWithUtfFields(
                "0", "1", "2", "3", "4", "5", "6", "legacyUser", "legacyPass");

        assertArrayEquals(
                new String[]{"legacyUser", "legacyPass"},
                Service.parseRegistrationCredentials(message));
    }

    @Test
    void rejectsUnsupportedFieldCount() throws Exception {
        Message message = messageWithUtfFields("user01", "pass01", "extra");

        assertNull(Service.parseRegistrationCredentials(message));
    }

    @Test
    void rejectsMissingField() throws Exception {
        Message message = messageWithUtfFields("user01");

        assertNull(Service.parseRegistrationCredentials(message));
    }

    @Test
    void rejectsTruncatedUtfField() {
        Message message = new Message((byte) 42, new byte[]{0, 6, 'u', 's'});

        assertThrows(EOFException.class, () -> Service.parseRegistrationCredentials(message));
    }

    private static Message messageWithUtfFields(String... fields) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(output);
        for (String field : fields) {
            data.writeUTF(field);
        }
        data.flush();
        return new Message((byte) 42, output.toByteArray());
    }
}
