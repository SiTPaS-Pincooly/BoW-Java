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

    public BoardRenderer() {
        int size = UIConstants.BOARD_PX;
        setPreferredSize(new Dimension(size, size));
        setMinimumSize(new Dimension(size, size));
        setMaximumSize(new Dimension(size, size));
        setBackground(UIConstants.BG);
    }

    public void update(Board board, List<Player> players) {
        this.board   = board;
        this.players = players;
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

        for (int i = 0; i < players.size(); i++) {
            Player p    = players.get(i);
            int[] pos   = Board.getScreenPosition(p.getPosition(), cs);
            int x       = pos[0];
            int y       = pos[1];
            int r       = Math.max(8, cs / 4);
            Color color = UIConstants.PLAYER_COLORS[i % UIConstants.PLAYER_COLORS.length];

            BufferedImage avatar = AssetLoader.load(pieceFiles[i % pieceFiles.length]);

            if (avatar != null) {
                // Draw avatar scaled to cell size
                g2.drawImage(AssetLoader.scale(avatar, cs, cs), x, y, null);
            } else {
                // Fallback: colored circle if art not ready
                int cx = x + cs / 2;
                int cy = y + cs / 2;

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