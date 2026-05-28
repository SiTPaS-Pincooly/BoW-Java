package controller;

import model.Player;
import model.spells.Spell;

import java.util.List;

public interface SpellMenu {
    Spell showMenu(Player current);
    Player chooseTarget(List<Player> targets);
}
