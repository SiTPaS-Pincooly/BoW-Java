package view;

import model.Player;
import model.tiles.Board;
import model.tiles.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BoardRenderer extends JPanel {

    private Board        board;
    private List<Player> players;
    private int          currentPlayerIndex = 0;

    private int  diceOverlayFace = 0;   // 0 = hidden
    private javax.swing.Timer diceClearTimer;

    public BoardRenderer() {
        int size = UIConstants.BOARD_PX;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setBackground(UIConstants.BG);
    }

    /** Show the dice face centered on the board for ~600 ms, then hide it. */
    public void showDice(int face) {
        if (diceClearTimer != null) diceClearTimer.stop();
        diceOverlayFace = face;
        repaint();
        diceClearTimer = new javax.swing.Timer(600, e -> {
            diceOverlayFace = 0;
            repaint();
            ((javax.swing.Timer) e.getSource()).stop();
        });
        diceClearTimer.setRepeats(false);
        diceClearTimer.start();
    }

    public void update(Board board, List<Player> players, int currentPlayerIndex) {
        this.board               = board;
        this.players             = players;
        this.currentPlayerIndex  = currentPlayerIndex;
        paintImmediately(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background image if available, otherwise fill with BG color
        BufferedImage bg = AssetLoader.load("assets/bg/background.jpg");
        if (bg != null) {
            g2.drawImage(AssetLoader.scale(bg, UIConstants.BOARD_PX, UIConstants.BOARD_PX), 0, 0, null);
        } else {
            g2.setColor(UIConstants.BG);
            g2.fillRect(0, 0, UIConstants.BOARD_PX, UIConstants.BOARD_PX);
        }

        int cs     = UIConstants.CELL_SIZE;
        Tile[] tiles = board.getTiles();

        for (int i = 0; i < Board.TOTAL; i++) {
            int cellNum = i + 1;
            int[] pos   = Board.getScreenPosition(cellNum, cs);
            int x = pos[0], y = pos[1];

            // Tile icon (if asset exists)
            Tile tile = tiles[i];
            if (tile.getImagePath() != null) {
                BufferedImage img = AssetLoader.load(tile.getImagePath());
                if (img != null) {
                    g2.drawImage(AssetLoader.scale(img, cs, cs), x, y, null);
                } else {
                    // No image yet — draw a colored placeholder
                    g2.setColor(tileColor(tile));
                    g2.fillRect(x + 2, y + 2, cs - 4, cs - 4);
                }
            }

            // Grid border
            g2.setColor(UIConstants.GRID);
            g2.drawRect(x, y, cs, cs);

            // Cell number
            g2.setColor(UIConstants.TEXT_DIM);
            g2.setFont(UIConstants.FONT_SMALL);
            g2.drawString(String.valueOf(cellNum), x + 3, y + 13);
        }

        // Player pieces
        if (players == null) return;

        String[] pieceFiles = {
            "assets/icons/piece_blue.png",
            "assets/icons/piece_red.png",
            "assets/icons/piece_green.png",
            "assets/icons/piece_yellow.png"
        };

        // Two-pass drawing: non-current players first, current player on top
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < players.size(); i++) {
                boolean isCurrent = (i == currentPlayerIndex);

                // Pass 0: draw non-current players only
                // Pass 1: draw current player only
                if (pass == 0 && isCurrent) continue;
                if (pass == 1 && !isCurrent) continue;

                Player p    = players.get(i);
                int[] pos   = Board.getScreenPosition(p.getPosition(), cs);
                int x       = pos[0];
                int y       = pos[1];
                int r       = Math.max(8, cs / 4);
                Color color = UIConstants.PLAYER_COLORS[i % UIConstants.PLAYER_COLORS.length];

                BufferedImage avatar = AssetLoader.load(pieceFiles[i % pieceFiles.length]);

                if (avatar != null) {
                    // Draw glow ring around current player's icon
                    if (isCurrent) {
                        g2.setColor(new Color(
                            color.getRed(), color.getGreen(), color.getBlue(), 180));
                        g2.setStroke(new BasicStroke(4));
                        g2.drawRoundRect(x + 2, y + 2, cs - 4, cs - 4, 8, 8);

                        // Outer pulse ring
                        g2.setColor(new Color(255, 255, 255, 60));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(x, y, cs, cs, 8, 8);
                    }
                    g2.drawImage(AssetLoader.scale(avatar, cs, cs), x, y, null);
                } else {
                    // Fallback: colored circle
                    int cx = x + cs / 2;
                    int cy = y + cs / 2;

                    // Glow ring for current player
                    if (isCurrent) {
                        g2.setColor(new Color(
                            color.getRed(), color.getGreen(), color.getBlue(), 120));
                        g2.setStroke(new BasicStroke(4));
                        g2.drawOval(cx - r - 6, cy - r - 6, (r + 6) * 2, (r + 6) * 2);
                    }

                    g2.setColor(new Color(0, 0, 0, 80));
                    g2.fillOval(cx - r + 2, cy - r + 2, r * 2, r * 2);

                    g2.setColor(color);
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                    g2.setFont(UIConstants.FONT_BOLD);
                    FontMetrics fm = g2.getFontMetrics();
                    String label   = String.valueOf(i + 1);
                    g2.drawString(label,
                        cx - fm.stringWidth(label) / 2,
                        cy + fm.getAscent() / 2 - 1);
                }
            }
        }

        // Dice overlay — centered on the board
        if (diceOverlayFace >= 1 && diceOverlayFace <= 6) {
            int diceSize = UIConstants.CELL_SIZE * 2;
            int dx = (getWidth()  - diceSize) / 2;
            int dy = (getHeight() - diceSize) / 2;

            BufferedImage diceImg = AssetLoader.load(DiceAssetGenerator.pathFor(diceOverlayFace));
            if (diceImg != null) {
                g2.drawImage(AssetLoader.scale(diceImg, diceSize, diceSize), dx, dy, null);
            } else {
                // Fallback: purple square with number
                g2.setColor(new Color(50, 30, 90, 220));
                g2.fillRoundRect(dx, dy, diceSize, diceSize, 16, 16);
                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.FONT_TITLE.deriveFont((float) diceSize / 2));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(diceOverlayFace);
                g2.drawString(s, dx + (diceSize - fm.stringWidth(s)) / 2,
                              dy + (diceSize + fm.getAscent()) / 2 - 4);
            }
        }
    }

    // Placeholder color when no image is loaded yet
    private Color tileColor(Tile tile) {
        String name = tile.getClass().getSimpleName();
        switch (name) {
            case "Checkpoint":   return new Color(180, 140, 40, 160);
            case "ManaFountain": return new Color(40, 80, 200, 160);
            case "GreenPortal":  return new Color(40, 180, 80, 160);
            case "BluePortal":   return new Color(60, 120, 220, 160);
            default:             return UIConstants.BG;
        }
    }
}