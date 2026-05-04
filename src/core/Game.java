package core;

import ui.Renderer;

public class Game {
    private final GameMap map;
    private final Player player;
    private final Renderer renderer;

    public Game(GameMap map, Player player, Renderer renderer) {
        this.map = map;
        this.player = player;
        this.renderer = renderer;
    }

    public void render() {
        renderer.render(map, player);
    }

    public void movePlayer(Direction direction) {
        Position nextPosition = player.getPosition().move(direction.getDRow(), direction.getDCol());

        if (!map.isInside(nextPosition)) {
            return;
        }

        if (map.isWall(nextPosition)) {
            return;
        }

        player.setPosition(nextPosition);
    }
}