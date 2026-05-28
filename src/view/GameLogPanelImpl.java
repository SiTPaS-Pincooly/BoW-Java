package view;

import controller.GameLogPanel;

import javax.swing.*;
import java.awt.*;

public class GameLogPanelImpl extends JPanel implements GameLogPanel {

    private final JTextArea area;

    public GameLogPanelImpl() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(UIConstants.BOARD_PX, UIConstants.LOG_HEIGHT));
        setBackground(new Color(20, 14, 38));

        area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(20, 14, 38));
        area.setForeground(UIConstants.TEXT_DIM);
        area.setFont(UIConstants.FONT_SMALL);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.GRID));
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            area.append(message + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        });
    }
}
