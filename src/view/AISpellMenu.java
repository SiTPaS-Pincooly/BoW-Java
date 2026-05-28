package view;

import controller.SpellMenu;
import model.AIPlayer;
import model.Player;
import model.spells.Spell;

import java.util.List;

/**
 * SpellMenu implementation for AI players.
 * Instead of showing a dialog, it calls AIPlayer.decideSpell().
 * Human players still get the normal SpellMenuImpl dialog.
 */
public class AISpellMenu implements SpellMenu {

    private final SpellMenu humanMenu;

    public AISpellMenu(SpellMenu humanMenu) {
        this.humanMenu = humanMenu;
    }

    @Override
    public Spell showMenu(Player current) {
        if (current instanceof AIPlayer ai) {
            // AI decides silently — no dialog shown
            return ai.decideSpell(List.of()); // others filled in by controller
        }
        // Human player — show the normal dialog
        return humanMenu.showMenu(current);
    }

    @Override
    public Player chooseTarget(List<Player> targets) {
        // This is called by the controller — if current player is AI,
        // the controller uses AIPlayer.chooseBestFireballTarget() directly.
        // This fallback handles edge cases.
        return targets.isEmpty() ? null : targets.get(0);
    }
}