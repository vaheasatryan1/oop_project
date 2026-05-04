package exceptions;

public class MapNotFoundException extends RuntimeException {
    public MapNotFoundException(String mapId) {
        super("Map not found: " + mapId);
    }
}