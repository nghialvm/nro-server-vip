package nro.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IconAssetResolverTest {

    private static final JsonParser JSON = new JsonParser();

    @TempDir
    Path tempDirectory;

    @Test
    void resolvesPartDataToTheFirstAvailableIcon() throws Exception {
        IconAssetResolver resolver = new IconAssetResolver(tempDirectory);
        writePng(tempDirectory.resolve("x4").resolve("7679.png"));
        JsonElement data = JSON.parse("[[7679,3,-15],[7680,3,-13]]");

        assertEquals(List.of(7679, 7680), AdminHttpServer.partIconIds(data));
        assertEquals(7679, AdminHttpServer.firstAvailablePartIconId(data, resolver));
    }

    @Test
    void skipsMissingOrInvalidFramesAndFallsBackToAnotherZoomOrFrame() throws Exception {
        IconAssetResolver resolver = new IconAssetResolver(tempDirectory);
        Path x4 = tempDirectory.resolve("x4");
        Path x3 = tempDirectory.resolve("x3");
        Files.createDirectories(x4);
        Files.createDirectories(x3);
        Files.write(x4.resolve("100.png"), new byte[]{1, 2, 3});
        writePng(x3.resolve("100.png"));
        writePng(x4.resolve("202.png"));

        assertEquals(x3.resolve("100.png").toAbsolutePath().normalize(), resolver.find(100).orElseThrow());
        JsonElement data = JSON.parse("[[999,0,0],[202,0,0]]");
        assertEquals(202, AdminHttpServer.firstAvailablePartIconId(data, resolver));
        assertTrue(resolver.read(100).isPresent());
    }

    @Test
    void returnsEmptyForMalformedMissingOrOutOfRangeAssets() throws Exception {
        IconAssetResolver resolver = new IconAssetResolver(tempDirectory);

        assertTrue(AdminHttpServer.partIconIds(JSON.parse("\"{broken\"")).isEmpty());
        assertTrue(AdminHttpServer.partIconIds(JSON.parse("[]")).isEmpty());
        assertFalse(resolver.find(-1).isPresent());
        assertFalse(resolver.find(IconAssetResolver.MAX_ICON_ID + 1).isPresent());
        assertFalse(resolver.read(404).isPresent());
    }

    private static void writePng(Path path) throws Exception {
        Files.createDirectories(path.getParent());
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        assertTrue(ImageIO.write(image, "png", path.toFile()));
    }
}
