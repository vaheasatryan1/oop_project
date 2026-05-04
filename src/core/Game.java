package core;

public class Game {
    private final GameMap map;
    private final Player player;

    public Game(GameMap map, Player player) {
        this.map = map;
        this.player = player;
    }

    public void movePlayer(int dRow, int dCol) {
        Position nextPosition = player.getPosition().move(dRow, dCol);

        if (!map.isInside(nextPosition)) {
            return;
        }

        if (map.isWall(nextPosition)) {
            return;
        }

        player.setPosition(nextPosition);
    }
}