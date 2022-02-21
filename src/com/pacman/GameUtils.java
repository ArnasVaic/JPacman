package com.pacman;

public class GameUtils {
    public static double lerp(double v1, double v2, double t) {
        return v1 + (v2 - v1) * t;
    }
}
