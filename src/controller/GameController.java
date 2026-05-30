package controller;

import model.GameState;
import model.Player;
import model.spells.FireballSpell;
import model.spells.Spell;
import model.tiles.Board;
import model.tiles.Tile;

import java.util.List;

public class GameController {

    private GameState gameState;
    private final Board board;
    private final GameLogPanel gameLogPanel;
    private final SpellMenu spellMenu;

    private boolean gameOver = false;
    private int lastRoll = 0;

    public GameController(Board board, GameLogPanel gameLogPanel, SpellMenu spellMenu) {
        this.board        = board;
        this.gameLogPanel = gameLogPanel;
        this.spellMenu    = spellMenu;
    }

    public void startGame(List<Player> players) {
        board.createBoard();
        gameState = new GameState(players);
        gameState.setBoard(board);
        gameLogPanel.log("Game started with " + players.size() + " players.");
        gameLogPanel.log("Round 1 — " + gameState.getCurrentPlayer().getName() + "'s turn.");
    }

    public void handleSpellPhase() {
        Player current = gameState.getCurrentPlayer();

        if (current instanceof model.AIPlayer ai) {
            // AI decides spell automatically
            List<Player> others = gameState.getOtherPlayers(current);
            Spell chosen = ai.decideSpell(others);

            if (chosen != null) {
                Player target = current;
                if (chosen instanceof model.spells.FireballSpell) {
                    target = ai.chooseBestFireballTarget(others, ai.getPosition());
                    if (target == null) { // no valid target, skip
                        gameLogPanel.log(ai.getName() + " (AI) skipped the spell phase.");
                        return;
                    }
                }
                chosen.cast(current, target, gameState);
                gameLogPanel.log(ai.getName() + " (AI) cast " + chosen.getName()
                        + (chosen instanceof model.spells.FireballSpell
                            ? " at " + target.getName() : "") + ".");
                checkRevive(target);
            } else {
                gameLogPanel.log(current.getName() + " (AI) skipped the spell phase.");
            }
            return;
        }

        // Human player — show spell menu dialog
        Spell chosen = spellMenu.showMenu(current);
        if (chosen != null) {
            Player target = current;
            if (chosen instanceof model.spells.FireballSpell) {
                target = spellMenu.chooseTarget(gameState.getOtherPlayers(current));
            }
            chosen.cast(current, target, gameState);
            gameLogPanel.log(current.getName() + " cast " + chosen.getName() + ".");
            checkRevive(target);
        } else {
            gameLogPanel.log(current.getName() + " skipped the spell phase.");
        }
    }

    public void handleDicePhase() {
        Player current = gameState.getCurrentPlayer();

        int roll = current.rollDice();
        lastRoll = roll;
        gameLogPanel.log(current.getName() + " rolled a " + roll + ".");

        current.move(roll, Board.TOTAL);
        int newPos = current.getPosition();
        gameLogPanel.log(current.getName() + " moved to cell " + newPos + ".");

        // Gain 1-3 mana for moving each turn
        int manaGain = new java.util.Random().nextInt(3) + 1;
        current.gainMana(manaGain);
        gameLogPanel.log(current.getName() + " gained " + manaGain + " mana from moving.");

        Tile tile = board.getTile(newPos);
        tile.activate(current, gameState);
        logTileEffect(tile, current, newPos);

        checkRevive(current);

        if (current.isAlive()) {
            Player winner = gameState.checkWinCondition(Board.TOTAL);
            if (winner != null) {
                gameOver = true;
                gameState.setGameOver(true);
                gameLogPanel.log(winner.getName() + " reached cell 100 and wins!");
            }
        }
    }

    public void nextTurn() {
        if (gameOver) return;
        gameState.advanceTurn();
        Player current = gameState.getCurrentPlayer();
        gameLogPanel.log("--- Round " + gameState.getRoundNumber()
                + " — " + current.getName() + "'s turn ---");
    }

    // ---------------------------------------------------------------
    // Revive logic — called after any event that could deal damage
    // ---------------------------------------------------------------
    private void checkRevive(Player player) {
        if (!player.isAlive()) {
            int revivePos = player.getRevivePosition();
            player.setPosition(revivePos);
            player.gainHp(player.getMaxHp()); // full heal on revive
            gameLogPanel.log(player.getName() + " was defeated and revived at cell "
                    + revivePos + " with full HP!");
        }
    }

    private void logTileEffect(Tile tile, Player player, int pos) {
        String name = tile.getClass().getSimpleName();
        switch (name) {
            case "Checkpoint":
                gameLogPanel.log("Checkpoint! " + player.getName() + " fully healed.");
                break;
            case "ManaFountain":
                gameLogPanel.log("Mana Fountain! " + player.getName() + " mana restored.");
                break;
            case "GreenPortal":
                gameLogPanel.log("Green Portal! " + player.getName()
                        + " teleported to another green portal at cell " + player.getPosition() + ".");
                break;
            case "BluePortal":
                gameLogPanel.log("Blue Portal! " + player.getName()
                        + " shifted to cell " + player.getPosition() + " (+-10 range).");
                break;
            default:
                break;
        }
    }

    public GameState getGameState() { return gameState; }
    public Board     getBoard()     { return board; }
    public boolean   isGameOver()   { return gameOver; }
    public int       getLastRoll()  { return lastRoll; }
}