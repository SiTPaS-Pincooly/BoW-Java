package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Generates dice face PNG assets (dice_one.png … dice_six.png) into
 * assets/icons/ at startup if they are not already present.
 *
 * Each face is a 128×128 purple-themed die consistent with the game's palette.
 */
public class DiceAssetGenerator {

    private static final String[] NAMES = {
        "dice_one", "dice_two", "dice_three", "dice_four", "dice_five", "dice_six"
    };

    // Pip layouts: each int[2] is {col, row} on a 3×3 grid (0-indexed)
    // Grid cell centers are mapped to pixel positions inside the die face.
    private static final int[][][] PIPS = {
        { {1,1} },                                                        // 1
        { {0,0}, {2,2} },                                                 // 2
        { {0,0}, {1,1}, {2,2} },                                          // 3
        { {0,0}, {2,0}, {0,2}, {2,2} },                                   // 4
        { {0,0}, {2,0}, {1,1}, {0,2}, {2,2} },                           // 5
        { {0,0}, {2,0}, {0,1}, {2,1}, {0,2}, {2,2} }                     // 6
    };

    public static String pathFor(int face) {
        return "assets/icons/" + NAMES[face - 1] + ".png";
    }

    /** Generate all missing dice assets. Call once at app startup. */
    public static void generateIfMissing() {
        for (int face = 1; face <= 6; face++) {
            File f = new File(pathFor(face));
            if (!f.exists()) {
                try {
                    BufferedImage img = render(face);
                    f.getParentFile().mkdirs();
                    ImageIO.write(img, "PNG", f);
                } catch (IOException e) {
                    System.err.println("DiceAssetGenerator: could not write " + f.getPath());
                }
            }
        }
    }

    private static BufferedImage render(int face) {
        int size = 128;
        int pad  = 10;
        int arc  = 22;

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent background
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);

        // Die body — deep purple with subtle gradient
        RoundRectangle2D body = new RoundRectangle2D.Float(pad, pad, size - pad*2, size - pad*2, arc, arc);
        GradientPaint grad = new GradientPaint(
            pad, pad,           new Color(72, 48, 120),
            size - pad, size - pad, new Color(38, 22, 70)
        );
        g.setPaint(grad);
        g.fill(body);

        // Border — bright purple edge
        g.setColor(new Color(140, 100, 220));
        g.setStroke(new BasicStroke(2.5f));
        g.draw(body);

        // Inner subtle bevel highlight (top-left)
        g.setColor(new Color(200, 170, 255, 40));
        g.setStroke(new BasicStroke(1.5f));
        g.draw(new RoundRectangle2D.Float(pad + 3, pad + 3,
                size - pad*2 - 6, size - pad*2 - 6, arc - 4, arc - 4));

        // Draw pips
        int[][] layout = PIPS[face - 1];
        int innerSize  = size - pad * 2;
        int cellW      = innerSize / 3;
        int cellH      = innerSize / 3;
        int pipR       = Math.max(7, size / 14);

        for (int[] pip : layout) {
            int col = pip[0];
            int row = pip[1];
            // Center of this pip in screen coords
            int cx = pad + col * cellW + cellW / 2;
            int cy = pad + row * cellH + cellH / 2;

            // Shadow
            g.setColor(new Color(0, 0, 0, 60));
            g.fillOval(cx - pipR + 1, cy - pipR + 1, pipR * 2, pipR * 2);

            // Pip fill — bright white-lavender
            g.setColor(new Color(230, 215, 255));
            g.fillOval(cx - pipR, cy - pipR, pipR * 2, pipR * 2);

            // Pip highlight
            g.setColor(new Color(255, 255, 255, 160));
            g.fillOval(cx - pipR + 2, cy - pipR + 2, pipR - 2, pipR - 2);
        }

        g.dispose();
        return img;
    }
}