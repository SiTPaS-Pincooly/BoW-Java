package model.tiles;

import model.GameState;
import model.Player;

public class Checkpoint extends Tile {
    private final int cellIndex;

    public Checkpoint(int cellIndex) {
        super("assets/icons/checkpoint.png");
        this.cellIndex = cellIndex;
    }

    @Override
    public void activate(Player player, GameState state) {
        player.gainHp(player.getMaxHp() - player.getHp()); // full heal
        player.setRevivePosition(cellIndex);
    }
}
