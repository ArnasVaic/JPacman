package com.pacman;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/// Factory design pattern
public class GhostFactory {

    private static final Map<String, Function<Void, Ghost>> map;

    static {
        map = new HashMap<>();
        map.put("red", (v) -> new RedGhost(9, 14, 0, 1));
        map.put("cyan", (v) -> new CyanGhost(12, 14, 0, -1));
        map.put("pink", (v) -> new PinkGhost(15, 14, 0, -1));
        map.put("yellow", (v) -> new YellowGhost(18, 14, 0, 1));
    }

    public static Ghost create(String color) {
        return map.get(color.toLowerCase()).apply(null);
    }
}
