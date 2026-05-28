package view;

import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StatPanel extends JPanel {

    private final int playerIndex;
    private Player  player;
    private boolean isActive;

    private static final int ICON_SIZE = 20;

    public StatPanel(int playerIndex) {
        this.playerIndex = playerIndex;
        setPreferredSize(new Dimension(UIConstants.STAT_WIDTH, UIConstants.SCREEN_H));
        setBackground(UIConstants.BG);
    }

    public void update(Player player, boolean isActive) {
        this.player   = player;
        this.isActive = isActive;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (player == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color pColor = UIConstants.PLAYER_COLORS[playerIndex % UIConstants.PLAYER_COLORS.length];
        int pad = 12;
        int w   = getWidth() - pad * 2;

        // Active highlight border
        if (isActive) {
            g2.setColor(pColor);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        }

        // Player name
        g2.setFont(UIConstants.FONT_TITLE);
        g2.setColor(pColor);
        g2.drawString(player.getName(), pad, 36);

        // HP bar with icon
        drawBar(g2, pad, 60, w,
                "assets/icons/icon_hp.png", "HP",
                player.getHp(), player.getMaxHp(), UIConstants.HP_COLOR);

        // Mana bar with icon
        drawBar(g2, pad, 104, w,
                "assets/icons/icon_mana.png", "Mana",
                player.getMana(), player.getMaxMana(), UIConstants.MANA_COLOR);

        // Shield with icon
        BufferedImage shieldIcon = AssetLoader.load("assets/icons/icon_shield.png");
        int shieldY = 150;
        if (shieldIcon != null) {
            g2.drawImage(AssetLoader.scale(shieldIcon, ICON_SIZE, ICON_SIZE), pad, shieldY - ICON_SIZE + 4, null);
        }
        g2.setFont(UIConstants.FONT_MAIN);
        g2.setColor(player.hasShield() ? UIConstants.SHIELD_CLR : UIConstants.TEXT_DIM);
        g2.drawString(player.hasShield() ? "Shield active" : "No shield",
                pad + (shieldIcon != null ? ICON_SIZE + 4 : 0), shieldY);

        // Position
        g2.setColor(UIConstants.TEXT_DIM);
        g2.drawString("Cell: " + player.getPosition(), pad, 178);

        if (isActive) {
            g2.setColor(pColor);
            g2.setFont(UIConstants.FONT_BOLD);
            g2.drawString(">> Your turn", pad, 210);
        }
    }

    private void drawBar(Graphics2D g2, int x, int y, int w,
                         String iconPath, String label,
                         int current, int max, Color color) {
        int textX = x;

        // Draw icon if available
        BufferedImage icon = AssetLoader.load(iconPath);
        if (icon != null) {
            g2.drawImage(AssetLoader.scale(icon, ICON_SIZE, ICON_SIZE), x, y - ICON_SIZE + 4, null);
            textX = x + ICON_SIZE + 4;
        }

        g2.setFont(UIConstants.FONT_MAIN);
        g2.setColor(UIConstants.TEXT_MAIN);
        g2.drawString(label + ": " + current + "/" + max, textX, y);

        int barH = 12;
        int barY = y + 6;
        int fill = max > 0 ? (int)((double) current / max * w) : 0;

        g2.setColor(new Color(60, 60, 80));
        g2.fillRoundRect(x, barY, w, barH, 6, 6);
        g2.setColor(color);
        if (fill > 0) g2.fillRoundRect(x, barY, fill, barH, 6, 6);
    }
}