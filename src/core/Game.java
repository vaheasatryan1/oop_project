package core;

import ui.Renderer;
import java.util.List;

public class Game {
    private final MapManager mapManager;
    private final Inventory inventory;
    private final Renderer renderer;
    private final CraftingSystem craftingSystem;
    private Player player;
    private boolean gameComplete = false;

    public Game(MapManager mapManager, Inventory inventory, Renderer renderer) {
        this.mapManager = mapManager;
        this.inventory = inventory;
        this.renderer = renderer;
        this.craftingSystem = new CraftingSystem();
        this.player = new Player(mapManager.getCurrentMap().getPlayerStart());
    }

    public void render() {
        GameMap map = mapManager.getCurrentMap();
        System.out.println("--- " + map.getId() + " ---");
        renderer.render(map, player);
        printHUD();
    }

    private void printHUD() {
        String mapId = mapManager.getCurrentMap().getId();

        // key status
        if (inventory.hasKeyForMap(mapId)) {
            System.out.println("Key: COMPLETE");
        } else {
            System.out.println("Key: incomplete");
        }

        // resources
        StringBuilder res = new StringBuilder("Resources: ");
        if (inventory.getAllResources().isEmpty()) {
            res.append("none");
        } else {
            inventory.getAllResources().forEach((r, amt) ->
                    res.append(r.name()).append(" x").append(amt).append("  ")
            );
        }
        System.out.println(res);

        // items
        StringBuilder itm = new StringBuilder("Items: ");
        if (inventory.getAllItems().isEmpty()) {
            itm.append("none");
        } else {
            inventory.getAllItems().forEach((i, amt) ->
                    itm.append(i.getName()).append(" x").append(amt).append("  ")
            );
        }
        System.out.println(itm);

        // equipped
        Item equipped = inventory.getEquippedItem();
        System.out.println("Equipped: " + (equipped != null ? equipped.getName() : "nothing"));
        System.out.println("Controls: WASD=move  E=use  C=craft  I=inventory  Q=quit");
    }

    public boolean isGameComplete() { return gameComplete; }

    public void movePlayer(Direction direction) {
        GameMap map = mapManager.getCurrentMap();
        Position nextPosition = player.getPosition().move(direction.getDRow(), direction.getDCol());

        if (!map.isInside(nextPosition)) return;

        char tile = map.getTile(nextPosition);

        if (tile == '#') return;

        if (tile == 'W') {
            System.out.println("Breakable wall! Equip a PICKAXE or BOMB and press E.");
            return;
        }

        // pick up resource
        Resource resource = Resource.fromChar(tile);
        if (resource != null) {
            inventory.addResource(resource);
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("Picked up: " + resource.name());
            return;
        }

        if (tile == 'K') {
            inventory.collectFullKey(map.getId());
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("You picked up the key!");
            return;
        }

        if (tile == '1' || tile == '2' || tile == '3') {
            inventory.collectKeyPart(map.getId(), tile);
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("Key part " + tile + " collected!");
            if (inventory.hasKeyForMap(map.getId())) {
                System.out.println("You have all key parts! Find the door.");
            }
            return;
        }

        if (tile == 'D') {
            if (inventory.hasKeyForMap(map.getId())) {
                switchToNextMap(map);
            } else {
                System.out.println("The door is locked. Find the key first.");
            }
            return;
        }

        player.setPosition(nextPosition);
    }

    public void useItem() {
        Item equipped = inventory.getEquippedItem();
        if (equipped == null) {
            System.out.println("Nothing equipped. Craft something first (press C).");
            return;
        }

        GameMap map = mapManager.getCurrentMap();
        Position pos = player.getPosition();

        // check all 4 neighbors for a breakable wall
        Position[] neighbors = {
                pos.move(-1, 0),
                pos.move(1, 0),
                pos.move(0, -1),
                pos.move(0, 1)
        };

        boolean broke = false;

        if (equipped == Item.PICKAXE) {
            for (Position neighbor : neighbors) {
                if (map.isInside(neighbor) && map.getTile(neighbor) == 'W') {
                    map.setTile(neighbor, '.');
                    broke = true;
                    break; // pickaxe breaks one wall
                }
            }
            if (broke) {
                inventory.consumeItem(Item.PICKAXE);
                System.out.println("Wall broken with pickaxe!");
            } else {
                System.out.println("No breakable wall next to you.");
            }
        }

        if (equipped == Item.BOMB) {
            for (Position neighbor : neighbors) {
                if (map.isInside(neighbor) && map.getTile(neighbor) == 'W') {
                    map.setTile(neighbor, '.');
                    // bomb also breaks neighbors of that wall
                    Position[] blastNeighbors = {
                            neighbor.move(-1, 0),
                            neighbor.move(1, 0),
                            neighbor.move(0, -1),
                            neighbor.move(0, 1)
                    };
                    for (Position blast : blastNeighbors) {
                        if (map.isInside(blast) && map.getTile(blast) == 'W') {
                            map.setTile(blast, '.');
                        }
                    }
                    broke = true;
                }
            }
            if (broke) {
                inventory.consumeItem(Item.BOMB);
                System.out.println("BOOM! Walls destroyed!");
            } else {
                System.out.println("No breakable wall next to you.");
            }
        }
    }

    public void openCrafting(java.util.Scanner scanner) {
        List<Recipe> recipes = craftingSystem.getRecipes();

        System.out.println("\n--- CRAFTING ---");
        inventory.getAllResources().forEach((r, amt) ->
                System.out.println("  " + r.name() + " x" + amt)
        );
        System.out.println();

        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            String canCraft = craftingSystem.canCraft(recipe, inventory) ? " [can craft]" : " [need more resources]";
            System.out.println("[" + (i + 1) + "] " + recipe.describe() + canCraft);
        }

        System.out.print("Enter number to craft, Q to close: ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("q")) return;

        try {
            int choice = Integer.parseInt(input) - 1;
            if (choice < 0 || choice >= recipes.size()) {
                System.out.println("Invalid choice.");
                return;
            }
            Recipe chosen = recipes.get(choice);
            if (craftingSystem.craft(chosen, inventory)) {
                System.out.println("Crafted: " + chosen.getResult().getName());
            } else {
                System.out.println("Not enough resources.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    public void showInventory() {
        System.out.println("\n--- INVENTORY ---");
        System.out.println("Resources:");
        if (inventory.getAllResources().isEmpty()) {
            System.out.println("  none");
        } else {
            inventory.getAllResources().forEach((r, amt) ->
                    System.out.println("  " + r.name() + " x" + amt)
            );
        }
        System.out.println("Items:");
        if (inventory.getAllItems().isEmpty()) {
            System.out.println("  none");
        } else {
            inventory.getAllItems().forEach((i, amt) ->
                    System.out.println("  " + i.getName() + " x" + amt)
            );
        }
        String mapId = mapManager.getCurrentMap().getId();
        System.out.println("Key parts for this level: " +
                (inventory.hasKeyForMap(mapId) ? "COMPLETE" : "incomplete"));
    }

    private void switchToNextMap(GameMap currentMap) {
        String nextMapId = currentMap.getNextMapId();
        if (nextMapId == null) {
            gameComplete = true;
            return;
        }
        System.out.println("Level complete! Moving to next level...");
        mapManager.switchToMap(nextMapId);
        player = new Player(mapManager.getCurrentMap().getPlayerStart());
    }
}