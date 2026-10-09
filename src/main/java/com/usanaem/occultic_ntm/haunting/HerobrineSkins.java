package com.usanaem.occultic_ntm.haunting;

import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;

/** Common-side validation of the exact bundled legacy-format skins; no fallback. */
public final class HerobrineSkins {
    public static final int COUNT = 2;
    private static Boolean available;
    private HerobrineSkins() { }
    public static String path(int variant) { return "textures/entity/herobrine_" + variant + ".png"; }
    public static void validate(InputStream stream) throws IOException {
        if (stream == null) throw new IOException("Missing Herobrine skin");
        BufferedImage image = ImageIO.read(stream);
        if (image == null || image.getWidth() != 64 || image.getHeight() != 32)
            throw new IOException("Herobrine skin must be a readable 64x32 PNG");
    }
    public static synchronized boolean bundledAvailable() {
        if (available != null) return available;
        try {
            for (int variant = 0; variant < COUNT; variant++) {
                try (InputStream stream = HerobrineSkins.class.getResourceAsStream("/assets/occultic_ntm/" + path(variant))) {
                    validate(stream);
                }
            }
            available = true;
        } catch (IOException | RuntimeException failure) {
            available = false;
            LogManager.getLogger("Occultic NTM").error("Herobrine sightings cancelled: bundled skin failed to load.", failure);
        }
        return available;
    }
}
