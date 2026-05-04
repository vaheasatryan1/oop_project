package core;

public enum Tile {
    WALL('#'),
    FLOOR('.'),
    PLAYER_START('P'),
    DOOR('D'),
    KEY('K'),
    KEY_PART_1('1'),
    KEY_PART_2('2'),
    KEY_PART_3('3'),
    UNKNOWN('?');

    private final char symbol;

    Tile(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static Tile fromChar(char c) {
        for (Tile tile : values()) {
            if (tile.symbol == c) {
                return tile;
            }
        }
        return UNKNOWN;
    }

    public boolean isWalkable() {
        return this == FLOOR || this == KEY || this == KEY_PART_1
                || this == KEY_PART_2 || this == KEY_PART_3;
    }

    public boolean isKeyPart() {
        return this == KEY_PART_1 || this == KEY_PART_2 || this == KEY_PART_3;
    }
}