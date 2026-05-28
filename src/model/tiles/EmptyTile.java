package model.tiles;

import model.GameState;
import model.Player;

public class EmptyTile extends Tile {
    public EmptyTile() { super(null); }

    @Override
    public void activate(Player player, GameState state) {
        // no effect
    }
}
