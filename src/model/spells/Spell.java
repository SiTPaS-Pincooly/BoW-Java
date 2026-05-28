package model.spells;

import model.GameState;
import model.Player;

public interface Spell {
    String getName();
    int getManaCost();
    String getDescription();
    void cast(Player caster, Player target, GameState state);
}
