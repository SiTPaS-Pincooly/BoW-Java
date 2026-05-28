package model.spells;

import model.GameState;
import model.Player;

public class HealSpell implements Spell {

    @Override public String getName()        { return "Heal"; }
    @Override public int getManaCost()       { return 4; }
    @Override public String getDescription() { return "Restore 1 HP (costs 4 mana)"; }

    @Override
    public void cast(Player caster, Player target, GameState state) {
        if (caster.useMana(getManaCost())) {
            caster.gainHp(1);
        }
    }
}
