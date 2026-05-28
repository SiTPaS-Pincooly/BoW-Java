package model.tiles;

import java.util.Random;

public class Board {

    public static final int WIDTH  = 10;
    public static final int HEIGHT = 10;
    public static final int TOTAL  = WIDTH * HEIGHT;

    private final Tile[] tiles = new Tile[TOTAL];
    private final Random rand  = new Random();

    public void createBoard() {
        for (int i = 0; i < TOTAL; i++) tiles[i] = new EmptyTile();

        placeTilesWithSpacing(new Checkpoint(0), 17, 23, 0);
        placeTilesWithSpacing(new ManaFountain(), 17, 23, rand.nextInt(4) + 2);
        placeTilesWithProbability(new BluePortal(), 10);
        placeTilesWithProbability(new GreenPortal(), 10);

        tiles[TOTAL - 1] = new EmptyTile(); // cell 100 always empty (win cell)
    }

    private void placeTilesWithSpacing(Tile type, int minGap, int maxGap, int startOffset) {
        int pos = startOffset;
        if (pos < TOTAL && tiles[pos] instanceof EmptyTile) {
            tiles[pos] = makeTile(type, pos);
        }
        while (pos < TOTAL) {
            int gap = rand.nextInt(maxGap - minGap + 1) + minGap;
            pos += gap;
            if (pos < TOTAL - 1 && tiles[pos] instanceof EmptyTile) {
                tiles[pos] = makeTile(type, pos);
            }
        }
    }

    private void placeTilesWithProbability(Tile type, int probability) {
        int min = TOTAL / probability; // minimum tiles to place

        // First pass — random placement across whole board
        for (int i = 1; i < TOTAL - 1; i++) {
            if (tiles[i] instanceof EmptyTile && rand.nextInt(probability) == 0) {
                tiles[i] = makeTile(type, i);
            }
        }

        // Second pass — if minimum not reached, force place on random empty cells
        long count = countTilesOfType(type);
        if (count < min) {
            java.util.List<Integer> empties = new java.util.ArrayList<>();
            for (int i = 1; i < TOTAL - 1; i++) {
                if (tiles[i] instanceof EmptyTile) empties.add(i);
            }
            java.util.Collections.shuffle(empties, rand);
            for (int i = 0; i < Math.min((int)(min - count), empties.size()); i++) {
                tiles[empties.get(i)] = makeTile(type, empties.get(i));
            }
        }
    }

    private long countTilesOfType(Tile type) {
        long count = 0;
        for (Tile t : tiles) {
            if (t.getClass() == type.getClass()) count++;
        }
        return count;
    }

    private Tile makeTile(Tile prototype, int index) {
        if (prototype instanceof Checkpoint) return new Checkpoint(index + 1);
        if (prototype instanceof ManaFountain) return new ManaFountain();
        if (prototype instanceof BluePortal)  return new BluePortal();
        if (prototype instanceof GreenPortal) return new GreenPortal();
        return new EmptyTile();
    }

    public Tile getTile(int position) {
        return tiles[position - 1];
    }

    public void setTile(int position, Tile tile) {
        tiles[position - 1] = tile;
    }

    public Tile[] getTiles() { return tiles; }

    /** Returns pixel (x, y) for cell number using snake path. */
    public static int[] getScreenPosition(int cellNumber, int cellSize) {
        int index = cellNumber - 1;
        int row   = index / WIDTH;
        int col   = index % WIDTH;
        int x     = (row % 2 == 0) ? col * cellSize : (WIDTH - 1 - col) * cellSize;
        int y     = row * cellSize;
        return new int[]{x, y};
    }
}