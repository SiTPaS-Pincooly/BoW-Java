package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetLoader {

    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage load(String path) {
        if (path == null) return null;
        return cache.computeIfAbsent(path, p -> {
            try {
                return ImageIO.read(new File(p));
            } catch (IOException e) {
                System.err.println("Asset not found: " + p);
                return null;
            }
        });
    }

    public static BufferedImage scale(BufferedImage src, int w, int h) {
        if (src == null) return null;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        out.createGraphics().drawImage(src.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return out;
    }
}
