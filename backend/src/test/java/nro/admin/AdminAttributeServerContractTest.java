package nro.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import nro.attribute.Attribute;
import nro.attribute.AttributeTemplate;
import nro.attribute.AttributeTemplateManager;
import org.junit.jupiter.api.Test;

class AdminAttributeServerContractTest {

    private final AdminHttpServer server = AdminHttpServer.gI();

    @Test
    void exposesOnlyValueAndTimeAsEditableAttributeFields() throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("value", 11000);
        body.addProperty("time", -1);

        invoke("validateAllowedFields", new Class<?>[]{JsonObject.class, Set.class, String.class},
                body, Set.of("value", "time"), "attribute_server");
        assertEquals(11000, invoke("requiredJsonInt", new Class<?>[]{JsonObject.class, String.class, int.class, int.class},
                body, "value", 0, 100_000));
        assertEquals(-1, invoke("requiredJsonInt", new Class<?>[]{JsonObject.class, String.class, int.class, int.class},
                body, "time", -1, Integer.MAX_VALUE));
    }

    @Test
    void rejectsInvalidRangesAndUnknownFields() throws Exception {
        JsonObject tooLarge = new JsonObject();
        tooLarge.addProperty("value", 100_001);
        tooLarge.addProperty("time", -1);
        InvocationTargetException valueException = assertThrows(InvocationTargetException.class,
                () -> invoke("requiredJsonInt", new Class<?>[]{JsonObject.class, String.class, int.class, int.class},
                        tooLarge, "value", 0, 100_000));
        assertEquals("INVALID_VALUE", exceptionCode(valueException));

        JsonObject badTime = new JsonObject();
        badTime.addProperty("value", 100);
        badTime.addProperty("time", -2);
        InvocationTargetException timeException = assertThrows(InvocationTargetException.class,
                () -> invoke("requiredJsonInt", new Class<?>[]{JsonObject.class, String.class, int.class, int.class},
                        badTime, "time", -1, Integer.MAX_VALUE));
        assertEquals("INVALID_TIME", exceptionCode(timeException));

        JsonObject unknown = new JsonObject();
        unknown.addProperty("value", 100);
        unknown.addProperty("time", 0);
        unknown.addProperty("templateId", 1);
        InvocationTargetException fieldException = assertThrows(InvocationTargetException.class,
                () -> invoke("validateAllowedFields", new Class<?>[]{JsonObject.class, Set.class, String.class},
                        unknown, Set.of("value", "time"), "attribute_server"));
        assertEquals("FIELD_NOT_ALLOWED", exceptionCode(fieldException));
    }

    @Test
    void serializesTheRuntimeRowWithoutMakingTemplateEditable() throws Exception {
        AttributeTemplateManager.getInstance().add(AttributeTemplate.builder()
                .id(991)
                .name("Tăng #value% thuộc tính test")
                .build());
        Attribute attribute = Attribute.builder()
                .id(91)
                .templateID(991)
                .value(11000)
                .time(-1)
                .build();

        JsonObject row = (JsonObject) invoke("attributeServerRow", new Class<?>[]{Attribute.class}, attribute);
        assertEquals(91, row.get("id").getAsInt());
        assertEquals(991, row.get("templateId").getAsInt());
        assertEquals("Tăng #value% thuộc tính test", row.get("templateName").getAsString());
        assertEquals(11000, row.get("value").getAsInt());
        assertEquals(-1, row.get("time").getAsInt());
        assertTrue(row.get("active").getAsBoolean());
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
