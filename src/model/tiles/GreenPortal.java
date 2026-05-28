package model.tiles;

import model.GameState;
import model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreenPortal extends Tile {
    private final Random rand = new Random();

    public GreenPortal() { super("assets/icons/green_portal.png"); }

    @Override
    public void activate(Player player, GameState state) {
        if (state.getBoard() == null) return;

        Tile[] tiles = state.getBoard().getTiles();
        int currentPos = player.getPosition();

        // Collect all other green portal positions
        List<Integer> otherPortals = new ArrayList<>();
        for (int i = 0; i < tiles.length; i++) {
            int cellNum = i + 1;
            if (tiles[i] instanceof GreenPortal && cellNum != currentPos) {
                otherPortals.add(cellNum);
            }
        }

        if (!otherPortals.isEmpty()) {
            // Teleport to a random other green portal
            int destination = otherPortals.get(rand.nextInt(otherPortals.size()));
            player.setPosition(destination);
        }
        // If no other green portals exist, nothing happens
    }
}