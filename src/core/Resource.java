package core;

public enum Resource {
    ROCK('R'),
    STICK('S');

    private final char symbol;

    Resource(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    public static Resource fromChar(char c) {
        for (Resource r : values()) {
            if (r.symbol == c) return r;
        }
        return null;
    }
}