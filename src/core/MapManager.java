package core;

import exceptions.MapNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapManager {
    private final Map<String, GameMap> maps = new LinkedHashMap<>();
    private GameMap currentMap;

    public void addMap(GameMap map) {
        maps.put(map.getId(), map);
        if (currentMap == null) currentMap = map;
    }

    public GameMap getCurrentMap() { return currentMap; }

    public void switchToMap(String mapId) {
        GameMap next = maps.get(mapId);
        if (next == null) throw new MapNotFoundException(mapId);
        currentMap = next;
    }

    public boolean hasMap(String mapId) { return maps.containsKey(mapId); }
}