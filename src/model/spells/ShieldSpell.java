package model.spells;

import model.GameState;
import model.Player;

public class ShieldSpell implements Spell {

    @Override public String getName()        { return "Shield"; }
    @Override public int getManaCost()       { return 2; }
    @Override public String getDescription() { return "Block next hit (costs 2 mana)"; }

    @Override
    public void cast(Player caster, Player target, GameState state) {
        if (caster.useMana(getManaCost())) {
            caster.activateShield();
        }
    }
}
