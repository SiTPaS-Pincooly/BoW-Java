package view;

import model.AIPlayer;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SetupScreen extends JPanel {

    private final JTextField           countField = new JTextField("2", 4);
    private final JPanel               nameFields = new JPanel(new GridLayout(0, 3, 8, 8));
    private final List<JTextField>     nameInputs = new ArrayList<>();
    private final List<JCheckBox>      aiToggles  = new ArrayList<>();
    private final Consumer<List<Player>> onStart;

    public SetupScreen(Consumer<List<Player>> onStart) {
        this.onStart = onStart;

        setPreferredSize(new Dimension(UIConstants.SCREEN_W, UIConstants.SCREEN_H));
        setBackground(UIConstants.BG);
        setLayout(new GridBagLayout());

        add(buildCard());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 28, 70));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 70, 180), 2),
            BorderFactory.createEmptyBorder(36, 52, 36, 52)
        ));

        int cardW = Math.max(380, UIConstants.SCREEN_W / 4);

        // Title
        JLabel title = new JLabel("Battle of Wizards");
        title.setFont(UIConstants.FONT_TITLE.deriveFont(Font.BOLD,
                UIConstants.FONT_TITLE.getSize() + 10f));
        title.setForeground(new Color(180, 140, 255));
        title.setAlignmentX(CENTER_ALIGNMENT);

        // Player count
        JLabel sub = new JLabel("Number of players (2-4):");
        sub.setFont(UIConstants.FONT_MAIN);
        sub.setForeground(UIConstants.TEXT_DIM);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        countField.setHorizontalAlignment(JTextField.CENTER);
        countField.setFont(UIConstants.FONT_BOLD);
        countField.setMaximumSize(new Dimension(80, 34));
        countField.setAlignmentX(CENTER_ALIGNMENT);

        JButton confirmCount = makeButton("Set players", new Color(60, 40, 110));
        confirmCount.setMaximumSize(new Dimension(cardW, 40));
        confirmCount.addActionListener(e -> buildNameFields());

        // Column headers — built dynamically inside buildNameFields()
        nameFields.setBackground(new Color(40, 28, 70));
        nameFields.setAlignmentX(CENTER_ALIGNMENT);
        nameFields.setLayout(new GridLayout(0, 4, 8, 8)); // 4 columns: icon, player, name, AI

        JButton startBtn = makeButton("Start Game", new Color(40, 110, 60));
        startBtn.setMaximumSize(new Dimension(cardW, 44));
        startBtn.setFont(UIConstants.FONT_BOLD);
        startBtn.addActionListener(e -> startGame());

        card.add(title);
        card.add(Box.createVerticalStrut(20));
        card.add(sub);
        card.add(Box.createVerticalStrut(8));
        card.add(countField);
        card.add(Box.createVerticalStrut(10));
        card.add(confirmCount);
        card.add(Box.createVerticalStrut(14));
        card.add(nameFields);
        card.add(Box.createVerticalStrut(20));
        card.add(startBtn);

        return card;
    }

    private void buildNameFields() {
        int n;
        try {
            n = Integer.parseInt(countField.getText().trim());
            if (n < 2 || n > 4) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a number between 2 and 4.");
            return;
        }

        nameFields.removeAll();
        nameInputs.clear();
        aiToggles.clear();

        // Column headers — shown only after Set Players is clicked
        nameFields.add(makeLabel("Icon"));
        JLabel h1 = makeLabel("Player");
        JLabel h2 = makeLabel("Name");
        JLabel h3 = makeLabel("AI Bot?");
        h3.setHorizontalAlignment(SwingConstants.CENTER);
        nameFields.add(h1);
        nameFields.add(h2);
        nameFields.add(h3);

        String[] pieceFiles = {
            "assets/icons/piece_blue.png",
            "assets/icons/piece_red.png",
            "assets/icons/piece_green.png",
            "assets/icons/piece_yellow.png"
        };

        Color[] pColors = UIConstants.PLAYER_COLORS;

        for (int i = 0; i < n; i++) {
            // Column 1: piece icon preview
            java.awt.image.BufferedImage img = view.AssetLoader.load(pieceFiles[i]);
            JLabel iconLabel;
            if (img != null) {
                iconLabel = new JLabel(new ImageIcon(view.AssetLoader.scale(img, 36, 36)));
            } else {
                // Fallback colored circle if asset missing
                iconLabel = new JLabel("●");
                iconLabel.setForeground(pColors[i % pColors.length]);
                iconLabel.setFont(UIConstants.FONT_TITLE);
            }
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nameFields.add(iconLabel);

            // Column 2: colored player label
            JLabel lbl = new JLabel("Player " + (i + 1));
            lbl.setForeground(pColors[i % pColors.length]);
            lbl.setFont(UIConstants.FONT_BOLD);
            nameFields.add(lbl);

            // Column 2: name input
            JTextField tf = new JTextField("Wizard " + (i + 1));
            tf.setFont(UIConstants.FONT_MAIN);
            nameInputs.add(tf);
            nameFields.add(tf);

            // Column 3: AI checkbox
            JCheckBox aiBox = new JCheckBox("AI");
            aiBox.setBackground(new Color(40, 28, 70));
            aiBox.setForeground(UIConstants.TEXT_DIM);
            aiBox.setFont(UIConstants.FONT_MAIN);
            aiBox.setHorizontalAlignment(SwingConstants.CENTER);
            aiBox.addActionListener(e -> {
                // Gray out name field if AI is checked — AI names themselves
                tf.setEnabled(!aiBox.isSelected());
                if (aiBox.isSelected()) tf.setText("AI Wizard " + (nameInputs.indexOf(tf) + 1));
            });
            aiToggles.add(aiBox);
            nameFields.add(aiBox);
        }

        revalidate();
        repaint();
    }

    private void startGame() {
        if (nameInputs.isEmpty()) { buildNameFields(); return; }

        // Must have at least one human player
        long humanCount = aiToggles.stream().filter(cb -> !cb.isSelected()).count();
        if (humanCount == 0) {
            JOptionPane.showMessageDialog(this,
                "At least one player must be human.");
            return;
        }

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < nameInputs.size(); i++) {
            String name = nameInputs.get(i).getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All names must be filled.");
                return;
            }
            boolean isAI = aiToggles.get(i).isSelected();
            players.add(isAI ? new AIPlayer(name) : new Player(name));
        }

        onStart.accept(players);
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_MAIN);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.FONT_BOLD);
        lbl.setForeground(UIConstants.TEXT_DIM);
        return lbl;
    }
}