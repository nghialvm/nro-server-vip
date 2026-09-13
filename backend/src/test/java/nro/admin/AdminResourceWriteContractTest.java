package nro.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;

class AdminResourceWriteContractTest {

    private final AdminHttpServer server = AdminHttpServer.gI();

    @Test
    void ignoresKnownReadOnlyPlayerColumnsButKeepsWritableItemData() throws Exception {
        Object definition = definition("players");
        JsonObject body = new JsonObject();
        body.addProperty("items_body", "[]");
        body.addProperty("LastTimeLoginGame", "2026-09-12 23:11:33.0");

        invokeValidateData(definition, body);

        @SuppressWarnings("unchecked")
        List<String> writable = (List<String>) invoke(
                "writableColumns", new Class<?>[]{definition.getClass(), JsonObject.class}, definition, body);
        assertEquals(List.of("items_body"), writable);
    }

    @Test
    void rejectsUnknownFields() throws Exception {
        Object definition = definition("players");
        JsonObject body = new JsonObject();
        body.addProperty("items_body", "[]");
        body.addProperty("not_a_player_column", "unexpected");

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class, () -> invokeValidateData(definition, body));
        assertEquals("FIELD_NOT_ALLOWED", exceptionCode(exception));
    }

    @Test
    void stillRejectsMalformedWritableJson() throws Exception {
        Object definition = definition("players");
        JsonObject body = new JsonObject();
        body.addProperty("items_body", "{not-json}");
        body.addProperty("LastTimeLoginGame", "not-used");

        InvocationTargetException exception = assertThrows(
                InvocationTargetException.class, () -> invokeValidateData(definition, body));
        assertEquals("INVALID_JSON", exceptionCode(exception));
    }

    private Object definition(String resource) throws Exception {
        return invoke("definitionFor", new Class<?>[]{String.class, String.class}, resource, null);
    }

    private void invokeValidateData(Object definition, JsonObject body) throws Exception {
        invoke("validateData", new Class<?>[]{definition.getClass(), JsonObject.class}, definition, body);
    }

    private String exceptionCode(InvocationTargetException exception) throws Exception {
        Field field = exception.getCause().getClass().getDeclaredField("code");
        field.setAccessible(true);
        return (String) field.get(exception.getCause());
    }

    private Object invoke(String name, Class<?>[] parameterTypes, Object... arguments) throws Exception {
        Method method = AdminHttpServer.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(server, arguments);
    }
}
