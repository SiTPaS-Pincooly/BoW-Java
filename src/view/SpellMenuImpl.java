package view;

import controller.SpellMenu;
import model.Player;
import model.spells.Spell;
import model.spells.SpellRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class SpellMenuImpl extends JPanel implements SpellMenu {

    private final JFrame parentFrame;
    private Spell lastChosen;

    // Map spell name to its icon path
    private static final Map<String, String> SPELL_ICONS = Map.of(
        "Fireball", "assets/icons/spell_fireball.png",
        "Heal",     "assets/icons/spell_heal.png",
        "Shield",   "assets/icons/spell_shield.png"
    );

    public SpellMenuImpl(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    // Colored background per spell so icons are always visible
    private static final Map<String, Color> SPELL_COLORS = Map.of(
        "Fireball", new Color(180, 60, 30),
        "Heal",     new Color(40, 140, 60),
        "Shield",   new Color(40, 80, 180)
    );

    @Override
    public Spell showMenu(Player current) {
        List<Spell> spells = SpellRegistry.getAll();

        JDialog dialog = new JDialog(parentFrame, "Spell Phase - " + current.getName(), true);
        dialog.setLayout(new GridLayout(0, 1, 4, 6));
        dialog.getContentPane().setBackground(UIConstants.BG);
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        lastChosen = null;

        for (Spell spell : spells) {
            boolean canAfford = current.getMana() >= spell.getManaCost();

            // Load spell icon onto a solid colored circle background
            BufferedImage icon = AssetLoader.load(SPELL_ICONS.getOrDefault(spell.getName(), null));
            ImageIcon imageIcon = null;
            if (icon != null) {
                int sz = 36;
                BufferedImage iconWithBg = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
                Graphics2D ig = iconWithBg.createGraphics();
                ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Solid colored circle behind icon
                Color bg = SPELL_COLORS.getOrDefault(spell.getName(), new Color(80, 60, 120));
                ig.setColor(canAfford ? bg : bg.darker());
                ig.fillOval(0, 0, sz, sz);
                // Draw icon centered inside the circle
                ig.drawImage(AssetLoader.scale(icon, sz - 8, sz - 8), 4, 4, null);
                ig.dispose();
                imageIcon = new ImageIcon(iconWithBg);
            }

            // Full description — no truncation
            String label = String.format("%s   [%d mana]   —   %s",
                    spell.getName(), spell.getManaCost(), spell.getDescription());

            JButton btn = new JButton(label, imageIcon);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(12);
            btn.setFont(UIConstants.FONT_MAIN);
            btn.setForeground(canAfford ? UIConstants.TEXT_MAIN : UIConstants.TEXT_DIM);
            btn.setBackground(canAfford ? new Color(55, 38, 88) : new Color(35, 25, 55));
            btn.setBorderPainted(false);
            btn.setEnabled(canAfford);
            btn.setPreferredSize(new Dimension(560, 58));
            btn.addActionListener(e -> {
                lastChosen = spell;
                dialog.dispose();
            });
            dialog.add(btn);
        }

        JButton skip = new JButton("Skip spell phase");
        skip.setFont(UIConstants.FONT_MAIN);
        skip.setForeground(UIConstants.TEXT_DIM);
        skip.setBackground(UIConstants.BG);
        skip.addActionListener(e -> dialog.dispose());
        dialog.add(skip);

        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            parentFrame.toFront();
            parentFrame.requestFocus();
        });

        return lastChosen;
    }

    @Override
    public Player chooseTarget(List<Player> targets) {
        if (targets.size() == 1) return targets.get(0);

        String[] names = targets.stream().map(Player::getName).toArray(String[]::new);
        String chosen = (String) JOptionPane.showInputDialog(
                parentFrame, "Choose target:", "Fireball",
                JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        return targets.stream()
                .filter(p -> p.getName().equals(chosen))
                .findFirst()
                .orElse(targets.get(0));
    }
}