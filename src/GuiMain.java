import core.DefaultMaps;
import core.Game;
import core.Inventory;
import ui.GameFrame;

import javax.swing.SwingUtilities;

public class GuiMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame(() ->
                    new Game(DefaultMaps.createMapManager(), new Inventory(), (map, player) -> { })
            );
            frame.setVisible(true);
        });
    }
}
