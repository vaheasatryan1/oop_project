package core;

public class GameMap {
    private final String id;
    private final char[][] grid;
    private final int rows;
    private final int cols;
    private Position playerStart;
    private final String nextMapId;

    public GameMap(String id, String mapText, String nextMapId) {
        this.id = id;
        this.nextMapId = nextMapId;

        String[] lines = mapText.strip().split("\\R");
        this.rows = lines.length;

        int maxCols = 0;
        for (String line : lines) {
            if (line.length() > maxCols) maxCols = line.length();
        }
        this.cols = maxCols;
        this.grid = new char[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char symbol = c < lines[r].length() ? lines[r].charAt(c) : '#';
                if (symbol == 'P') {
                    playerStart = new Position(r, c);
                    grid[r][c] = '.';
                } else {
                    grid[r][c] = symbol;
                }
            }
        }

        if (playerStart == null) {
            throw new IllegalArgumentException("Map " + id + " must contain P.");
        }
    }

    public GameMap(String mapText) {
        this("default", mapText, null);
    }

    public String getId() {
        return id;
    }

    public String getNextMapId() {
        return nextMapId;
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

    public char getTile(Position pos) {
        return grid[pos.row()][pos.col()];
    }

    public void setTile(Position pos, char symbol) {
        grid[pos.row()][pos.col()] = symbol;
    }

    public boolean isInside(Position pos) {
        return pos.row() >= 0 && pos.row() < rows && pos.col() >= 0 && pos.col() < cols;
    }

    public boolean isWall(Position pos) {
        return getTile(pos) == '#';
    }
}