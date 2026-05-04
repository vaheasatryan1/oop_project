package ui;

import core.GameMap;
import core.Player;

public interface Renderer {
    void render(GameMap map, Player player);
}