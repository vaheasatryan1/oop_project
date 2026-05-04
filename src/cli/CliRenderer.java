package cli;

import core.GameMap;
import core.Player;
import core.Position;

public class CliRenderer {

    public void render(GameMap map, Player player) {
        Position playerPosition = player.getPosition();

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {

                if (playerPosition.row() == row && playerPosition.col() == col) {
                    System.out.print("@");
                } else {
                    System.out.print(map.getTile(new Position(row, col)));
                }

            }
            System.out.println();
        }
    }
}