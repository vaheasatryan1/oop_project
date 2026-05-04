package core;

public enum Item {
    PICKAXE("Pickaxe", "Breaks one wall tile"),
    BOMB("Bomb", "Breaks wall and all neighbors");

    private final String name;
    private final String description;

    Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}