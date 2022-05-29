package com.pacman;

import java.awt.*;

public class PointUtils {

    public static Point scale(Point p, double scale) {
        return new Point((int)(p.x * scale), (int)(p.y * scale));
    }

    public static Point mul(Point p, Point q) {
        return new Point(p.x * q.x, p.y * q.y);
    }

    public static Point sub(Point p, Point q) {
        return new Point(p.x - q.x, p.y - q.y);
    }

    public static Point add(Point p, Point q) {
        return new Point(p.x + q.x, p.y + q.y);
    }

    public static Point clamp(Point p, Point max, Point min) {
        return new Point(
                Math.max(Math.min(p.x, min.x), max.x),
                Math.max(Math.min(p.y, min.y), max.y)
        );
    }

    public static int sqmag(Point p) {
        return p.x * p.x + p.y * p.y;
    }
}
