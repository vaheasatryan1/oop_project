package core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Inventory {
    // key tracking
    private final Set<String> fullKeys = new HashSet<>();
    private final Map<String, Set<Character>> keyPartsByMap = new HashMap<>();

    // resources
    private final Map<Resource, Integer> resources = new HashMap<>();

    // crafted items
    private final Map<Item, Integer> items = new HashMap<>();

    // equipped item
    private Item equippedItem = null;

    // --- KEY METHODS ---

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

    // --- RESOURCE METHODS ---

    public void addResource(Resource resource) {
        resources.merge(resource, 1, Integer::sum);
    }

    public void removeResource(Resource resource, int amount) {
        int current = resources.getOrDefault(resource, 0);
        if (current <= amount) {
            resources.remove(resource);
        } else {
            resources.put(resource, current - amount);
        }
    }

    public int getResourceCount(Resource resource) {
        return resources.getOrDefault(resource, 0);
    }

    public Map<Resource, Integer> getAllResources() {
        return resources;
    }

    // --- ITEM METHODS ---

    public void addItem(Item item) {
        items.merge(item, 1, Integer::sum);
        if (equippedItem == null) equippedItem = item;
    }

    public boolean hasItem(Item item) {
        return items.getOrDefault(item, 0) > 0;
    }

    public void consumeItem(Item item) {
        int current = items.getOrDefault(item, 0);
        if (current <= 1) {
            items.remove(item);
            if (equippedItem == item) equippedItem = null;
        } else {
            items.put(item, current - 1);
        }
    }

    public Map<Item, Integer> getAllItems() {
        return items;
    }

    public Item getEquippedItem() { return equippedItem; }

    public void equipItem(Item item) {
        if (hasItem(item)) equippedItem = item;
    }
}