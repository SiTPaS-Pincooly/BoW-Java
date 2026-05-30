import controller.GameController;
import model.Player;
import model.tiles.Board;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            view.DiceAssetGenerator.generateIfMissing();
            JFrame frame = new JFrame("Battle of Wizards");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(true);

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setSize(screen.width, screen.height);
            frame.setLocation(0, 0);

            // Callback now receives List<Player> — already constructed as
            // Player or AIPlayer depending on the checkbox in SetupScreen
            SetupScreen setup = new SetupScreen((List<Player> players) -> {
                Board            board     = new Board();
                GameLogPanelImpl log       = new GameLogPanelImpl();
                SpellMenuImpl    spellMenu = new SpellMenuImpl(frame);

                GameController controller = new GameController(board, log, spellMenu);
                controller.startGame(players);

                GamePanel gamePanel = new GamePanel(controller, log, frame);

                frame.getContentPane().removeAll();
                frame.setContentPane(gamePanel);
                frame.revalidate();
                frame.repaint();

                // Trigger AI immediately if first player is a bot
                SwingUtilities.invokeLater(gamePanel::checkAndTriggerAIIfNeeded);
            });

            frame.setContentPane(setup);
            frame.setVisible(true);
        });
    }
}