package nro.admin;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.imageio.ImageIO;

/**
 * Resolves icon ids to safe, readable PNG assets on the server.
 *
 * The browser only receives bytes through the Admin API. It never receives
 * this filesystem path and it cannot choose an arbitrary path to read.
 */
final class IconAssetResolver {

    static final int MAX_ICON_ID = 2_000_000;
    private static final long MAX_ICON_BYTES = 10L * 1024L * 1024L;
    private static final List<String> ZOOM_LEVELS = Arrays.asList("x4", "x3", "x2", "x1");

    private final Path iconRoot;
    private final ConcurrentMap<Integer, Optional<Path>> pathCache = new ConcurrentHashMap<>();

    IconAssetResolver(Path iconRoot) {
        if (iconRoot == null) {
            throw new IllegalArgumentException("iconRoot must not be null");
        }
        this.iconRoot = iconRoot.toAbsolutePath().normalize();
    }

    Path iconRoot() {
        return iconRoot;
    }

    boolean isAvailable(int iconId) {
        return find(iconId).isPresent();
    }

    Optional<Path> find(int iconId) {
        if (iconId < 0 || iconId > MAX_ICON_ID) {
            return Optional.empty();
        }
        return pathCache.computeIfAbsent(iconId, this::findReadableAsset);
    }

    Optional<byte[]> read(int iconId) {
        for (int attempt = 0; attempt < 2; attempt++) {
            Optional<Path> selected = find(iconId);
            if (selected.isEmpty()) {
                return Optional.empty();
            }
            Path path = selected.get();
            try {
                byte[] bytes = Files.readAllBytes(path);
                if (isReadableImage(bytes)) {
                    return Optional.of(bytes);
                }
            } catch (IOException | RuntimeException ignored) {
                // The file may have been removed or replaced after lookup.
            }
            pathCache.remove(iconId, selected);
        }
        return Optional.empty();
    }

    void clearCache() {
        pathCache.clear();
    }

    private Optional<Path> findReadableAsset(int iconId) {
        if (Files.isSymbolicLink(iconRoot)) {
            return Optional.empty();
        }
        for (String zoom : ZOOM_LEVELS) {
            Path zoomRoot = iconRoot.resolve(zoom).normalize();
            Path candidate = zoomRoot.resolve(iconId + ".png").normalize();
            if (!candidate.startsWith(iconRoot)
                    || Files.isSymbolicLink(zoomRoot)
                    || Files.isSymbolicLink(candidate)) {
                continue;
            }
            if (isReadableImage(candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    private static boolean isReadableImage(Path path) {
        try {
            if (!Files.isRegularFile(path) || Files.size(path) <= 0 || Files.size(path) > MAX_ICON_BYTES) {
                return false;
            }
            try (InputStream input = Files.newInputStream(path)) {
                return isReadableImage(input);
            }
        } catch (IOException | RuntimeException ignored) {
            return false;
        }
    }

    private static boolean isReadableImage(byte[] bytes) {
        if (bytes == null || bytes.length == 0 || bytes.length > MAX_ICON_BYTES) {
            return false;
        }
        try (InputStream input = new ByteArrayInputStream(bytes)) {
            return isReadableImage(input);
        } catch (IOException | RuntimeException ignored) {
            return false;
        }
    }

    private static boolean isReadableImage(InputStream input) throws IOException {
        BufferedImage image = ImageIO.read(input);
        return image != null && image.getWidth() > 0 && image.getHeight() > 0;
    }
}
