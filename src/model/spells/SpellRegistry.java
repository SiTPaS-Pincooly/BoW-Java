package model.spells;

import java.util.List;

public class SpellRegistry {
    public static List<Spell> getAll() {
        return List.of(new HealSpell(), new FireballSpell(), new ShieldSpell());
    }
}
