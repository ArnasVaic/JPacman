package com.pacman;

import java.awt.*;

public class DrawUtils {

    public static void drawImage(Graphics g, Image image, Point position, Point size) {
        g.drawImage(image, position.x, position.y, size.x, size.y, null);
    }

    public static void drawLine(Graphics g, Point p, Point q) {
        g.drawLine(p.x, p.y, q.x, q.y);
    }
}
