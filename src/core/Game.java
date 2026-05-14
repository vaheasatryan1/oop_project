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

        if (inventory.hasKeyForMap(mapId)) {
            System.out.println("Key: COMPLETE");
        } else {
            System.out.println("Key: incomplete");
        }

        StringBuilder res = new StringBuilder("Resources: ");
        if (inventory.getAllResources().isEmpty()) {
            res.append("none");
        } else {
            inventory.getAllResources().forEach((r, amt) ->
                    res.append(r.name()).append(" x").append(amt).append("  ")
            );
        }
        System.out.println(res);

        StringBuilder itm = new StringBuilder("Items: ");
        if (inventory.getAllItems().isEmpty()) {
            itm.append("none");
        } else {
            inventory.getAllItems().forEach((i, amt) ->
                    itm.append(i.getName()).append(" x").append(amt).append("  ")
            );
        }
        System.out.println(itm);

        Item equipped = inventory.getEquippedItem();
        System.out.println("Equipped: " + (equipped != null ? equipped.getName() : "nothing"));
        System.out.println("Controls: WASD=move  E=use  C=craft  I=inventory  1/2=equip  Q=quit");
    }

    public boolean isGameComplete() { return gameComplete; }

    public GameMap getCurrentMap() {
        return mapManager.getCurrentMap();
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public String getCurrentMapId() {
        return mapManager.getCurrentMap().getId();
    }

    public void movePlayer(Direction direction) {
        GameMap map = mapManager.getCurrentMap();
        Position nextPosition = player.getPosition().move(direction.getDRow(), direction.getDCol());

        if (!map.isInside(nextPosition)) return;

        Tile tile = Tile.fromChar(map.getTile(nextPosition));

        if (tile == Tile.WALL) return;

        if (tile == Tile.BREAKABLE_WALL) {
            System.out.println("Breakable wall! Equip a PICKAXE or BOMB and press E.");
            return;
        }

        Resource resource = Resource.fromChar(map.getTile(nextPosition));
        if (resource != null) {
            inventory.addResource(resource);
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("Picked up: " + resource.name());
            return;
        }

        if (tile == Tile.KEY) {
            inventory.collectFullKey(map.getId());
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("You picked up the key!");
            return;
        }

        if (tile.isKeyPart()) {
            char part = map.getTile(nextPosition);
            inventory.collectKeyPart(map.getId(), part);
            map.setTile(nextPosition, '.');
            player.setPosition(nextPosition);
            System.out.println("Key part " + part + " collected!");
            if (inventory.hasKeyForMap(map.getId())) {
                System.out.println("You have all key parts! Find the door.");
            }
            return;
        }

        if (tile == Tile.DOOR) {
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

        Position[] neighbors = {
                pos.move(-1, 0),
                pos.move(1, 0),
                pos.move(0, -1),
                pos.move(0, 1)
        };

        if (equipped == Item.PICKAXE) {
            boolean broke = false;
            for (Position neighbor : neighbors) {
                if (map.isInside(neighbor) && map.getTile(neighbor) == 'W') {
                    map.setTile(neighbor, '.');
                    broke = true;
                    break;
                }
            }
            if (broke) {
                inventory.consumeItem(Item.PICKAXE);
                System.out.println("Wall broken with pickaxe!");
            } else {
                System.out.println("No breakable wall next to you.");
            }
            return;
        }

        if (equipped == Item.BOMB) {
            boolean broke = false;
            for (Position neighbor : neighbors) {
                if (map.isInside(neighbor) && map.getTile(neighbor) == 'W') {
                    map.setTile(neighbor, '.');
                    broke = true;
                    // blast also clears the wall's own neighbors
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
                }
            }
            // consume after processing all walls — one bomb, one use
            if (broke) {
                inventory.consumeItem(Item.BOMB);
                System.out.println("BOOM! Walls destroyed!");
            } else {
                System.out.println("No breakable wall next to you.");
            }
        }
    }


    public List<Recipe> getCraftingRecipes() {
        return craftingSystem.getRecipes();
    }

    public boolean canCraft(Recipe recipe) {
        return craftingSystem.canCraft(recipe, inventory);
    }

    public boolean craft(int recipeIndex) {
        List<Recipe> recipes = craftingSystem.getRecipes();
        if (recipeIndex < 0 || recipeIndex >= recipes.size()) return false;
        return craftingSystem.craft(recipes.get(recipeIndex), inventory);
    }


    public boolean equipBySlot(int slot) {
        List<Item> items = new java.util.ArrayList<>(inventory.getAllItems().keySet());
        if (slot < 1 || slot > items.size()) return false;
        inventory.equipItem(items.get(slot - 1));
        return true;
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
        System.out.println("Items (press 1/2 to equip):");
        if (inventory.getAllItems().isEmpty()) {
            System.out.println("  none");
        } else {
            int slot = 1;
            for (java.util.Map.Entry<Item, Integer> e : inventory.getAllItems().entrySet()) {
                String tag = e.getKey() == inventory.getEquippedItem() ? " [equipped]" : "";
                System.out.println("  [" + slot + "] " + e.getKey().getName() + " x" + e.getValue() + tag);
                slot++;
            }
        }
        String mapId = mapManager.getCurrentMap().getId();
        System.out.println("Key: " + (inventory.hasKeyForMap(mapId) ? "COMPLETE" : "incomplete"));
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