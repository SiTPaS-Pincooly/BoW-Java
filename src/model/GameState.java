package model;

import java.util.ArrayList;
import java.util.List;
import model.tiles.Board;

public class GameState {

    private final List<Player> players;
    private int currentPlayerIndex;
    private int roundNumber;
    private boolean gameOver;
    private Board board;

    public GameState(List<Player> players) {
        this.players = players;
        this.currentPlayerIndex = 0;
        this.roundNumber = 1;
        this.gameOver = false;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            roundNumber++;
        }
    }

    public Player checkWinCondition(int totalCells) {
        for (Player player : players) {
            if (player.getPosition() == totalCells) {
                return player;
            }
        }
        return null;
    }

    public List<Player> getOtherPlayers(Player current) {
        List<Player> others = new ArrayList<>();
        for (Player player : players) {
            if (player != current) {
                others.add(player);
            }
        }
        return others;
    }

    public List<Player> getPlayers()   { return new ArrayList<>(players); }
    public int getRoundNumber()        { return roundNumber; }
    public boolean isGameOver()        { return gameOver; }
    public void setGameOver(boolean v) { gameOver = v; }
    public Board getBoard()            { return board; }
    public void setBoard(Board board)  { this.board = board; }
}