import cli.CliInputHandler;
import cli.CliRenderer;
import core.Direction;
import core.Game;
import core.GameMap;
import core.Player;
import ui.Renderer;

public class Main {
    public static void main(String[] args) {
        String mapText = """
##############################
#P...........................#
#....######..................#
#............................#
#..........####..............#
#............................#
#..####......................#
#............................#
#...............######.......#
#............................#
#......1.....................#
#............................#
#............####............#
#............................#
#..2.....................3...#
#............................#
#....................#####...#
#..........................D.#
#............................#
##############################
""";



        GameMap map = new GameMap(mapText);
        Player player = new Player(map.getPlayerStart());

        Renderer renderer = new CliRenderer();
        Game game = new Game(map, player, renderer);

        CliInputHandler input = new CliInputHandler();

        while (true) {
            game.render();
            System.out.print("WASD to move, Q to quit: ");

            String raw = input.readRaw();

            if (input.isQuit(raw)) break;

            Direction dir = switch (raw.toLowerCase()) {
                case "w" -> Direction.UP;
                case "s" -> Direction.DOWN;
                case "a" -> Direction.LEFT;
                case "d" -> Direction.RIGHT;
                default -> null;
            };

            if (dir != null) {
                game.movePlayer(dir);
            }

            System.out.println();
        }
    }
}