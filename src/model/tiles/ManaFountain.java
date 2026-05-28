package model.tiles;

import model.GameState;
import model.Player;

public class ManaFountain extends Tile {
    public ManaFountain() { super("assets/icons/mana_fountain.png"); }

    @Override
    public void activate(Player player, GameState state) {
        player.gainMana(player.getMaxMana() - player.getMana()); // full mana
    }
}
