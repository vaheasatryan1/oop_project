package cli;

import core.GameMap;
import core.Player;
import core.Position;
import ui.Renderer;

public class CliRenderer implements Renderer {

    @Override
    public void render(GameMap map, Player player) {
        Position playerPos = player.getPosition();

        for (int row = 0; row < map.getRows(); row++) {
            for (int col = 0; col < map.getCols(); col++) {
                if (playerPos.row() == row && playerPos.col() == col) {
                    System.out.print('@');
                } else {
                    System.out.print(map.getTile(new Position(row, col)));
                }
            }
            System.out.println();
        }
    }
}