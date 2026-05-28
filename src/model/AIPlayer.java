package model;

import model.spells.FireballSpell;
import model.spells.HealSpell;
import model.spells.ShieldSpell;
import model.spells.Spell;

import java.util.List;

public class AIPlayer extends Player {

    public AIPlayer(String name) {
        super(name);
    }

    /**
     * Decision tree for spell selection.
     * Returns the chosen Spell, or null to skip.
     * target list = all other players (used for Fireball targeting).
     */
    public Spell decideSpell(List<Player> others) {

        int hp      = getHp();
        int maxHp   = getMaxHp();
        int mana    = getMana();
        int pos     = getPosition();

        // --- Priority 1: Shield if low HP and not already shielded ---
        // Activate shield when below 40% HP and have mana for it
        if (!hasShield() && hp <= maxHp * 0.4 && mana >= 2) {
            return new ShieldSpell();
        }

        // --- Priority 2: Heal if HP is critically low ---
        // Heal when HP is 1 or below half health and mana allows
        if (hp == 1 && mana >= 4) {
            return new HealSpell();
        }
        if (hp <= maxHp / 2 && mana >= 4) {
            return new HealSpell();
        }

        // --- Priority 3: Fireball at the best target ---
        // Fire at someone low HP (finish them) or far ahead (slow them down)
        if (mana >= 3 && !others.isEmpty()) {
            Player target = chooseBestFireballTarget(others, pos);
            if (target != null) {
                return new FireballSpell();
            }
        }

        // --- Priority 4: Shield if mana is high and shield is not active ---
        // Save excess mana into a shield proactively
        if (!hasShield() && mana >= 8) {
            return new ShieldSpell();
        }

        return null; // skip spell phase
    }

    /**
     * Pick the best Fireball target:
     * 1. Any player at 1 HP (kill shot)
     * 2. The player furthest ahead on the board
     * Returns null if no worthwhile target found.
     */
    public Player chooseBestFireballTarget(List<Player> others, int myPos) {
        // Kill shot — target anyone at 1 HP first
        for (Player p : others) {
            if (p.getHp() == 1 && !p.hasShield()) {
                return p;
            }
        }

        // Otherwise target the player furthest ahead
        Player bestTarget = null;
        int furthest = -1;
        for (Player p : others) {
            if (!p.hasShield() && p.getPosition() > furthest) {
                furthest = p.getPosition();
                bestTarget = p;
            }
        }

        // Only shoot if they are ahead of us
        if (bestTarget != null && furthest > myPos) {
            return bestTarget;
        }

        return null;
    }

    public boolean isAI() { return true; }
}