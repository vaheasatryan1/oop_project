package core;

import exceptions.InvalidMapException;

public class GameMap {
    private static final int REQUIRED_ROWS = 15;
    private static final int REQUIRED_COLS = 60;

    private final char[][] grid;
    private final int rows;
    private final int cols;
    private Position playerStart;

    public GameMap(String mapText) {
        String[] lines = mapText.strip().split("\\R");

        rows = lines.length;
        cols = lines[0].length();

        if (rows != REQUIRED_ROWS || cols != REQUIRED_COLS) {
            throw new InvalidMapException("Map must be exactly 20 rows and 40 columns.");
        }

        grid = new char[rows][cols];

        for (int row = 0; row < rows; row++) {
            if (lines[row].length() != REQUIRED_COLS) {
                throw new InvalidMapException("Each row must be exactly 40 characters.");
            }

            for (int col = 0; col < cols; col++) {
                char symbol = lines[row].charAt(col);

                if (symbol == 'P') {
                    playerStart = new Position(row, col);
                    grid[row][col] = '.';
                } else {
                    grid[row][col] = symbol;
                }
            }
        }

        if (playerStart == null) {
            throw new InvalidMapException("Map must contain player start P.");
        }
    }

    public char getTile(Position position) {
        return grid[position.row()][position.col()];
    }

    public boolean isWall(Position position) {
        return getTile(position) == '#';
    }

    public boolean isInside(Position position) {
        return position.row() >= 0 && position.row() < rows &&
                position.col() >= 0 && position.col() < cols;
    }

    public Position getPlayerStart() {
        return playerStart;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}