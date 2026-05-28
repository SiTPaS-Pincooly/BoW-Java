package model.spells;

import model.GameState;
import model.Player;

public class FireballSpell implements Spell {

    @Override public String getName()        { return "Fireball"; }
    @Override public int getManaCost()       { return 3; }
    @Override public String getDescription() { return "Deal 1 damage to target (costs 3 mana)"; }

    @Override
    public void cast(Player caster, Player target, GameState state) {
        if (caster.useMana(getManaCost())) {
            target.loseHp(1);
        }
    }
}
