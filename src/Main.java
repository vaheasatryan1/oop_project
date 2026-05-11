import cli.CliInputHandler;
import cli.CliRenderer;
import core.DefaultMaps;
import core.Direction;
import core.Game;
import core.Inventory;
import ui.GameFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("-cli")) {
            runCli();
        } else {
            runGui();
        }
    }

    private static void runGui() {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame(() ->
                    new Game(DefaultMaps.createMapManager(), new Inventory(), (map, player) -> { })
            );
            frame.setVisible(true);
        });
    }

    private static void runCli() {
        Game game = new Game(
                DefaultMaps.createMapManager(),
                new Inventory(),
                new CliRenderer()
        );

        CliInputHandler input = new CliInputHandler();

        while (true) {
            game.render();

            String raw = input.readRaw();
            if (input.isQuit(raw)) {
                break;
            }

            switch (raw.toLowerCase()) {
                case "w" -> game.movePlayer(Direction.UP);
                case "s" -> game.movePlayer(Direction.DOWN);
                case "a" -> game.movePlayer(Direction.LEFT);
                case "d" -> game.movePlayer(Direction.RIGHT);
                case "e" -> game.useItem();
                case "c" -> input.openCrafting(game);
                case "i" -> game.showInventory();
                case "1" -> {
                    if (game.equipBySlot(1)) {
                        System.out.println("Slot 1 equipped.");
                    }
                }
                case "2" -> {
                    if (game.equipBySlot(2)) {
                        System.out.println("Slot 2 equipped.");
                    }
                }
                default -> System.out.println("Unknown key.");
            }

            if (game.isGameComplete()) {
                System.out.println("You completed all 10 levels! Congratulations!");
                break;
            }

            System.out.println();
        }
    }
}