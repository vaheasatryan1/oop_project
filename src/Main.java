import cli.CliRenderer;
import core.GameMap;
import core.Player;
import core.Game;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String mapText = """
        ############################################################
        #P.........................................................#
        #..........................................................#
        #..........########........................................#
        #..........................................................#
        #.....................######...............................#
        #..........................................................#
        #......####................................................#
        #..........................................................#
        #.........................####.............................#
        #..........................................................#
        #..............1...........................................#
        #.....2.............................................3......#
        #.......................................................D..#
        ############################################################
        """;

        GameMap map = new GameMap(mapText);
        Player player = new Player(map.getPlayerStart());
        Game game = new Game(map, player);
        CliRenderer renderer = new CliRenderer();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            renderer.render(map, player);

            System.out.print("Move with WASD, q to quit: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("q")) {
                break;
            }

            switch (input.toLowerCase()) {
                case "w" -> game.movePlayer(-1, 0);
                case "s" -> game.movePlayer(1, 0);
                case "a" -> game.movePlayer(0, -1);
                case "d" -> game.movePlayer(0, 1);
                default -> System.out.println("Invalid input.");
            }

            System.out.println();
        }
    }
}