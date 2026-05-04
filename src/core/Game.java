package core;

import ui.Renderer;

public class Game {
    private final MapManager mapManager;
    private final Inventory inventory;
    private final Renderer renderer;
    private Player player;
    private boolean gameComplete = false;

    public Game(MapManager mapManager, Inventory inventory, Renderer renderer) {
        this.mapManager = mapManager;
        this.inventory = inventory;
        this.renderer = renderer;
        this.player = new Player(mapManager.getCurrentMap().getPlayerStart());
    }

    public void render() {
        System.out.println("--- " + mapManager.getCurrentMap().getId() + " ---");
        renderer.render(mapManager.getCurrentMap(), player);

        // show key status
        String mapId = mapManager.getCurrentMap().getId();
        if (inventory.hasKeyForMap(mapId)) {
            System.out.println("Key: COMPLETE");
        } else {
            System.out.println("Key: incomplete");
        }
    }

    public boolean isGameComplete() {
        return gameComplete;
    }

    public void movePlayer(Direction direction) {
        GameMap map = mapManager.getCurrentMap();
        Position nextPosition = player.getPosition().move(direction.getDRow(), direction.getDCol());

        if (!map.isInside(nextPosition)) {
            return;
        }

        char tile = map.getTile(nextPosition);

        if (tile == '#') {
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