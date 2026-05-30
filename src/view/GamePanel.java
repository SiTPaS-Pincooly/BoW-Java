package view;

import controller.GameController;
import model.GameState;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GamePanel extends JPanel {

    private final GameController controller;
    private final BoardRenderer  boardRenderer;
    private final StatPanel[]    statPanels;
    private final GameLogPanelImpl logPanel;
    private final JFrame         parentFrame;

    private int     phase  = 0;
    private boolean paused = false;

    private JButton actionButton;
    private JLabel  phaseLabel;
    private JLabel  currentPlayerLabel;
    private JPanel  pauseOverlay;

    public GamePanel(GameController controller, GameLogPanelImpl logPanel, JFrame parentFrame) {
        this.controller  = controller;
        this.logPanel    = logPanel;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        boardRenderer = new BoardRenderer();

        GameState    gs      = controller.getGameState();
        List<Player> players = gs.getPlayers();
        statPanels = new StatPanel[players.size()];

        // Left stat panel — always player 0 (and player 2 if 4 players)
        JPanel leftStats = new JPanel(new GridLayout(0, 1));
        leftStats.setBackground(UIConstants.BG);
        leftStats.setPreferredSize(new Dimension(UIConstants.STAT_WIDTH, UIConstants.BOARD_PX));
        statPanels[0] = new StatPanel(0);
        leftStats.add(statPanels[0]);
        if (players.size() >= 3) {
            statPanels[2] = new StatPanel(2);
            leftStats.add(statPanels[2]);
        }

        // Right stat panel — always player 1 (and player 3 if 4 players)
        JPanel rightStats = new JPanel(new GridLayout(0, 1));
        rightStats.setBackground(UIConstants.BG);
        rightStats.setPreferredSize(new Dimension(UIConstants.STAT_WIDTH, UIConstants.BOARD_PX));
        statPanels[1] = new StatPanel(1);
        rightStats.add(statPanels[1]);
        if (players.size() >= 4) {
            statPanels[3] = new StatPanel(3);
            rightStats.add(statPanels[3]);
        }

        // Center: board + log stacked vertically
        JPanel centerColumn = new JPanel(new BorderLayout());
        centerColumn.setBackground(UIConstants.BG);

        // Wrap board in a panel that centers it without stretching it
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardWrapper.setBackground(UIConstants.BG);
        boardWrapper.add(boardRenderer);

        logPanel.setPreferredSize(new Dimension(UIConstants.BOARD_PX, UIConstants.LOG_HEIGHT));

        centerColumn.add(boardWrapper, BorderLayout.CENTER);
        centerColumn.add(logPanel,     BorderLayout.SOUTH);

        add(buildActionBar(), BorderLayout.NORTH);
        add(leftStats,        BorderLayout.WEST);
        add(centerColumn,     BorderLayout.CENTER);
        add(rightStats,       BorderLayout.EAST);

        buildPauseOverlay();
        bindEscKey();
        refreshUI();
    }

    // ---------------------------------------------------------------
    // ESC key binding
    // ---------------------------------------------------------------
    private void bindEscKey() {
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(esc, "openPause");
        getActionMap().put("openPause", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { togglePause(); }
        });
    }

    // ---------------------------------------------------------------
    // Pause overlay
    // ---------------------------------------------------------------
    private void buildPauseOverlay() {
        pauseOverlay = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pauseOverlay.setOpaque(false);
        pauseOverlay.setVisible(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(28, 18, 52));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 70, 180), 2),
            BorderFactory.createEmptyBorder(32, 48, 32, 48)
        ));

        JLabel title = new JLabel("Paused");
        title.setFont(UIConstants.FONT_TITLE.deriveFont(Font.BOLD, UIConstants.FONT_TITLE.getSize() + 8f));
        title.setForeground(new Color(180, 140, 255));
        title.setAlignmentX(CENTER_ALIGNMENT);

        int btnW = Math.max(220, UIConstants.SCREEN_W / 7);
        int btnH = Math.max(44,  UIConstants.SCREEN_H / 20);

        JButton resumeBtn = makePauseMenuButton(">> Resume Game", new Color(40, 110, 60), btnW, btnH);
        resumeBtn.addActionListener(e -> togglePause());

        JButton quitBtn = makePauseMenuButton("X  Quit Game", new Color(140, 40, 40), btnW, btnH);
        quitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to quit?", "Quit",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) System.exit(0);
        });

        JLabel hint = new JLabel("Press ESC to resume");
        hint.setFont(UIConstants.FONT_SMALL);
        hint.setForeground(UIConstants.TEXT_DIM);
        hint.setAlignmentX(CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(24));
        card.add(resumeBtn);
        card.add(Box.createVerticalStrut(12));
        card.add(quitBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(hint);

        pauseOverlay.add(card);
    }

    private JButton makePauseMenuButton(String text, Color bg, int w, int h) {
        JButton btn = new JButton(text);
        btn.setFont(UIConstants.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(w, h));
        btn.setPreferredSize(new Dimension(w, h));
        return btn;
    }

    private void togglePause() {
        paused = !paused;
        JPanel glass = (JPanel) parentFrame.getGlassPane();
        if (paused) {
            glass.setLayout(new BorderLayout());
            glass.add(pauseOverlay, BorderLayout.CENTER);
            pauseOverlay.setVisible(true);
            glass.setVisible(true);
        } else {
            pauseOverlay.setVisible(false);
            glass.setVisible(false);
        }
    }

    // ---------------------------------------------------------------
    // Top action bar
    // ---------------------------------------------------------------
    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(new Color(20, 14, 38));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setFont(UIConstants.FONT_TITLE);
        currentPlayerLabel.setForeground(Color.WHITE);

        phaseLabel = new JLabel();
        phaseLabel.setFont(UIConstants.FONT_MAIN);
        phaseLabel.setForeground(UIConstants.TEXT_DIM);
        phaseLabel.setHorizontalAlignment(SwingConstants.CENTER);

        int btnW = Math.max(200, UIConstants.SCREEN_W / 7);
        int btnH = Math.max(36,  UIConstants.SCREEN_H / 24);

        actionButton = new JButton();
        actionButton.setFont(UIConstants.FONT_BOLD);
        actionButton.setFocusPainted(false);
        actionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionButton.setPreferredSize(new Dimension(btnW, btnH));
        actionButton.addActionListener(e -> handleAction());

        JButton helpBtn = new JButton("? How to play");
        helpBtn.setFont(UIConstants.FONT_SMALL);
        helpBtn.setForeground(UIConstants.TEXT_DIM);
        helpBtn.setBackground(new Color(20, 14, 38));
        helpBtn.setBorderPainted(false);
        helpBtn.setFocusPainted(false);
        helpBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        helpBtn.addActionListener(e -> showInstructions());

        JButton pauseBtn = new JButton("|| Menu");
        pauseBtn.setFont(UIConstants.FONT_SMALL);
        pauseBtn.setForeground(UIConstants.TEXT_DIM);
        pauseBtn.setBackground(new Color(20, 14, 38));
        pauseBtn.setBorderPainted(false);
        pauseBtn.setFocusPainted(false);
        pauseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pauseBtn.addActionListener(e -> togglePause());

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightGroup.setBackground(new Color(20, 14, 38));
        rightGroup.add(helpBtn);
        rightGroup.add(pauseBtn);
        rightGroup.add(actionButton);

        bar.add(currentPlayerLabel, BorderLayout.WEST);
        bar.add(phaseLabel,         BorderLayout.CENTER);
        bar.add(rightGroup,         BorderLayout.EAST);

        return bar;
    }

    // ---------------------------------------------------------------
    // Handle action button
    // ---------------------------------------------------------------
    // ---------------------------------------------------------------
    // Human button click — only runs human turn, never touches AI
    // ---------------------------------------------------------------
    private void handleAction() {
        if (controller.isGameOver() || paused) return;

        // Safety guard — ignore clicks during AI turn
        model.Player current = controller.getGameState().getCurrentPlayer();
        if (current instanceof model.AIPlayer) return;

        if (phase == 0) {
            controller.handleSpellPhase();
            phase = 1;
            SwingUtilities.invokeLater(() -> {
                refreshUI();
                parentFrame.toFront();
                parentFrame.requestFocus();
            });
        } else {
            controller.handleDicePhase();
            boardRenderer.showDice(controller.getLastRoll());
            if (!controller.isGameOver()) {
                controller.nextTurn();
                phase = 0;
                SwingUtilities.invokeLater(() -> {
                    refreshUI();
                    parentFrame.toFront();
                    parentFrame.requestFocus();
                    // Only now check if the NEXT player is AI
                    triggerAITurnIfNeeded();
                });
            } else {
                actionButton.setEnabled(false);
                actionButton.setText("Game Over!");
                refreshUI();
            }
        }
    }

    // ---------------------------------------------------------------
    // AI turn — completely separate, never calls handleAction()
    // ---------------------------------------------------------------
    public void checkAndTriggerAIIfNeeded() {
        triggerAITurnIfNeeded();
    }

    private void triggerAITurnIfNeeded() {
        model.Player current = controller.getGameState().getCurrentPlayer();
        if (!(current instanceof model.AIPlayer)) return;

        actionButton.setEnabled(false);
        actionButton.setText(current.getName() + " (AI) is thinking...");
        actionButton.setBackground(new Color(60, 60, 60));

        // Step 1 — AI spell phase after 1.5s
        javax.swing.Timer spellTimer = new javax.swing.Timer(1500, e -> {
            controller.handleSpellPhase();
            refreshUI();

            // Step 2 — AI dice phase after another 1.5s
            javax.swing.Timer diceTimer = new javax.swing.Timer(1500, e2 -> {
                controller.handleDicePhase();
                boardRenderer.showDice(controller.getLastRoll());

                if (!controller.isGameOver()) {
                    controller.nextTurn();
                    phase = 0;
                    SwingUtilities.invokeLater(() -> {
                        refreshUI();
                        parentFrame.toFront();
                        parentFrame.requestFocus();
                        // Check if the next player is also AI
                        triggerAITurnIfNeeded();
                    });
                } else {
                    actionButton.setEnabled(false);
                    actionButton.setText("Game Over!");
                    refreshUI();
                }
            });
            diceTimer.setRepeats(false);
            diceTimer.start();
        });
        spellTimer.setRepeats(false);
        spellTimer.start();
    }

    // ---------------------------------------------------------------
    // Refresh UI
    // ---------------------------------------------------------------
    private void refreshUI() {
        GameState    gs      = controller.getGameState();
        List<Player> players = gs.getPlayers();
        Player       current = gs.getCurrentPlayer();
        int          pidx    = players.indexOf(current);
        Color        pColor  = UIConstants.PLAYER_COLORS[pidx % UIConstants.PLAYER_COLORS.length];

        currentPlayerLabel.setText("  " + current.getName() + "'s turn");
        currentPlayerLabel.setForeground(pColor);

        if (phase == 0) {
            phaseLabel.setText("Step 1 of 2 — Cast a spell or skip");
            actionButton.setText("Cast Spell / Skip  >>");
            actionButton.setBackground(new Color(70, 45, 130));
            actionButton.setForeground(Color.WHITE);
            actionButton.setEnabled(true);
        } else {
            phaseLabel.setText("Step 2 of 2 — Roll the dice to move");
            actionButton.setText("Roll Dice  [click here]");
            actionButton.setBackground(new Color(40, 110, 60));
            actionButton.setForeground(Color.WHITE);
            actionButton.setEnabled(true);
        }

        boardRenderer.update(controller.getBoard(), players, pidx);

        for (int i = 0; i < statPanels.length; i++) {
            if (statPanels[i] != null && i < players.size()) {
                statPanels[i].update(players.get(i), players.get(i) == current);
            }
        }
    }

    // ---------------------------------------------------------------
    // Instructions popup
    // ---------------------------------------------------------------
    private void showInstructions() {
        String text =
            "HOW TO PLAY - Battle of Wizards\n" +
            "----------------------------------------\n\n" +
            "GOAL\n" +
            "  Be the first player to reach cell 100.\n\n" +
            "EACH TURN HAS 2 STEPS:\n\n" +
            "  Step 1 - Cast Spell / Skip\n" +
            "    Click 'Cast Spell / Skip' to open the spell menu.\n" +
            "    You can cast one spell or skip this step.\n\n" +
            "  Step 2 - Roll Dice\n" +
            "    Click 'Roll Dice' to move forward.\n" +
            "    You roll 1-6 and move that many cells.\n\n" +
            "SPELLS  (require mana)\n" +
            "  Fireball  - Deal 1 damage to another player  (3 mana)\n" +
            "  Heal      - Restore 1 HP to yourself          (4 mana)\n" +
            "  Shield    - Block the next hit you receive    (2 mana)\n\n" +
            "SPECIAL TILES\n" +
            "  Checkpoint    - Fully restores your HP\n" +
            "                  Sets your respawn point\n" +
            "  Mana Fountain - Fully restores your mana\n" +
            "  Green Portal  - Teleports you to another random green portal\n" +
            "  Blue Portal   - Shifts you forward or back up to 10 cells\n\n" +
            "BOARD\n" +
            "  The board follows a snake path:\n" +
            "  row 1 goes left to right, row 2 right to left, and so on.\n" +
            "  If your roll would take you past cell 100,\n" +
            "  you bounce back by the excess amount.\n\n" +
            "CONTROLS\n" +
            "  ESC - Open / close the pause menu\n\n" +
            "----------------------------------------";

        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, UIConstants.FONT_MAIN.getSize()));
        area.setBackground(new Color(30, 20, 50));
        area.setForeground(Color.WHITE);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane scroll = new JScrollPane(area);
        int popW = Math.min(560, UIConstants.SCREEN_W - 100);
        int popH = Math.min(500, UIConstants.SCREEN_H - 100);
        scroll.setPreferredSize(new Dimension(popW, popH));
        scroll.setBorder(null);

        JOptionPane.showMessageDialog(parentFrame, scroll,
            "How to Play", JOptionPane.PLAIN_MESSAGE);
    }
}