package view;

import java.awt.*;

public class UIConstants {

    // Logical pixel size — respects Windows display scaling
    private static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int SCREEN_W = SCREEN.width;
    public static final int SCREEN_H = SCREEN.height;

    public static final int BOARD_CELLS  = 10;
    public static final int ACTION_BAR_H = Math.max(50, SCREEN_H / 18);
    public static final int LOG_HEIGHT   = Math.max(100, SCREEN_H / 8);
    public static final int STAT_WIDTH   = Math.max(160, SCREEN_W / 9);

    // Available space after subtracting ALL surrounding elements
    // Horizontal: screen minus left+right stat panels
    // Vertical:   screen minus top action bar minus bottom log
    public static final int CELL_SIZE = Math.min(
            (SCREEN_W - STAT_WIDTH * 2) / BOARD_CELLS,
            (SCREEN_H - ACTION_BAR_H - LOG_HEIGHT) / BOARD_CELLS
    );
    public static final int BOARD_PX = CELL_SIZE * BOARD_CELLS;

    private static final int FS      = Math.max(11, SCREEN_H / 72);
    public static final Font FONT_SMALL  = new Font("Arial", Font.PLAIN, FS);
    public static final Font FONT_MAIN   = new Font("Arial", Font.PLAIN, FS + 2);
    public static final Font FONT_BOLD   = new Font("Arial", Font.BOLD,  FS + 3);
    public static final Font FONT_TITLE  = new Font("Arial", Font.BOLD,  FS + 6);

    public static final Color BG         = new Color(30, 20, 50);
    public static final Color GRID       = new Color(80, 60, 120);
    public static final Color TEXT_MAIN  = Color.WHITE;
    public static final Color TEXT_DIM   = new Color(180, 160, 220);
    public static final Color HP_COLOR   = new Color(220, 60, 60);
    public static final Color MANA_COLOR = new Color(60, 120, 220);
    public static final Color SHIELD_CLR = new Color(200, 180, 60);

    public static final Color[] PLAYER_COLORS = {
        new Color(60, 140, 220),
        new Color(220, 60, 60),
        new Color(60, 200, 100),
        new Color(220, 180, 40)
    };
}