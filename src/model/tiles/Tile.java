package model.tiles;

import model.GameState;
import model.Player;

public abstract class Tile {
    protected String imagePath;

    public Tile(String imagePath) {
        this.imagePath = imagePath;
    }

    public abstract void activate(Player player, GameState state);

    public String getImagePath() { return imagePath; }
}
