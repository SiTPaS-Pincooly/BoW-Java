package model.tiles;

import model.GameState;
import model.Player;

import java.util.Random;

public class BluePortal extends Tile {
    private final Random rand = new Random();
    private static final int RANGE = 10;

    public BluePortal() { super("assets/icons/blue_portal.png"); }

    @Override
    public void activate(Player player, GameState state) {
        int current = player.getPosition();
        int total   = state.getBoard() != null ? state.getBoard().TOTAL : 100;

        // Pick a random offset in [-10, +10], excluding 0
        int offset;
        do {
            offset = rand.nextInt(RANGE * 2 + 1) - RANGE; // -10 to +10
        } while (offset == 0);

        int destination = current + offset;

        // Clamp to valid range — never below 1, never at or past cell 100
        destination = Math.max(1, Math.min(destination, total - 1));

        player.setPosition(destination);
    }
}