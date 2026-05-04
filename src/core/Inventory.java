package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Inventory {
    private final Set<String> fullKeys = new HashSet<>();
    private final Map<String, Set<Character>> keyPartsByMap = new HashMap<>();

    public void collectFullKey(String mapId) {
        fullKeys.add(mapId);
    }

    public void collectKeyPart(String mapId, char part) {
        keyPartsByMap.computeIfAbsent(mapId, k -> new HashSet<>()).add(part);
    }

    public boolean hasKeyForMap(String mapId) {
        if (fullKeys.contains(mapId)) return true;
        Set<Character> parts = keyPartsByMap.get(mapId);
        return parts != null && parts.contains('1') && parts.contains('2') && parts.contains('3');
    }
}