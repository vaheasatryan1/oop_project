package core;

import java.util.HashSet;
import java.util.Set;

public class Inventory {
    private final Set<Character> keyParts = new HashSet<>();

    public void collectKeyPart(char part) {
        keyParts.add(part);
    }

    public boolean hasFullKey() {
        return keyParts.contains('1') &&
                keyParts.contains('2') &&
                keyParts.contains('3');
    }
}