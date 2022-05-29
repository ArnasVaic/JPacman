package com.pacman;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameMap {

    public static final Point TILE_COUNT = new Point(28, 36);
    public static final Point SCALED_TILE_COUNT = new Point(TILE_COUNT.x * Game.SCALE, TILE_COUNT.y * Game.SCALE);
    public static final Point TILE_SIZE = new Point(8, 8);
    public static final Point SCALED_TILE_SIZE = new Point(TILE_SIZE.x * Game.SCALE, TILE_SIZE.y * Game.SCALE);

    public static final Point MIN_BOUNDS = new Point(0, 0);
    public static final Point MAX_BOUNDS = PointUtils.sub(TILE_COUNT, new Point(1, 1));


    public static final int TURN            = 1;
    public static final int INTERSECTION    = 0;
    public static final int BIG_FOOD        = 1;
    public static final int SMALL_FOOD      = 0;

    Set<Point> junctions; // junction positions
    Map<Point, Integer> food; // food (true: large, false: small)

    public boolean renderGrid = true;

    private boolean[] solid;

    public GameMap() {
        parseMetaData(Assets.metadata);
    }

    public void parseMetaData(BufferedImage metaImage) {
        assert(metaImage.getWidth() == GameMap.TILE_COUNT.x);
        assert(metaImage.getHeight() == GameMap.TILE_COUNT.y);

        food = new HashMap<>();
        junctions = new HashSet<>();
        solid = new boolean[TILE_COUNT.x * TILE_COUNT.y];

        for(int i = 0; i < metaImage.getHeight(); i++) {
            for(int j = 0; j < metaImage.getWidth(); j++) {
                // current pixel color
                final Color color = new Color(metaImage.getRGB(j, i));
                // convert coordinates to 1d index
                final int index = j + i * metaImage.getWidth();
                // current pixel coordinates
                final Point p = new Point(j, i);
                // solid tile value: 255, empty tile value: 0
                final int red = color.getRed();     // red channel      - collision data
                // small food value: 255, big food value: 127
                final int green = color.getGreen(); // green channel    - food data
                // junction value: 255
                final int blue = color.getBlue();   // blue channel     - junction data
                // set tile solidity
                solid[index] = (red == 255);
                if(blue == 255) junctions.add(p);
                switch(green) {
                    case 255 -> food.put(p, SMALL_FOOD);
                    case 127 -> food.put(p, BIG_FOOD);
                }
            }
        }
    }

    public void draw(Graphics g) {
        final int x = TILE_SIZE.x * Game.SCALE;
        final int y = TILE_SIZE.y * Game.SCALE;
        final int w = Game.SIZE.x * Game.SCALE;
        final int h = Game.SIZE.y * Game.SCALE;

        g.drawImage(Assets.map, 0, 0, Game.SIZE.x, Game.SIZE.y, null);

        for (Map.Entry<Point, Integer> entry : food.entrySet()) {
            Point p = entry.getKey();
            Integer index = entry.getValue();
            g.drawImage(Assets.food[index], p.x * x, p.y * y, x, y, null);
        }

        if(renderGrid) {
            // Paint grid
            g.setColor(new Color(255, 255, 255, 40));
            for(int i = 0; i < TILE_COUNT.x; ++i) {
                g.fillRect(i * x - 1, 0, 2, h);
            }
            for(int i = 0; i < TILE_COUNT.y; ++i) {
                g.fillRect(0, i * y - 1, w, 2);
            }
            // Paint turns & intersections
            g.setColor(new Color(0, 255, 0, 40));
            for (Point p : junctions) {
                g.fillRect(p.x * x, p.y * y, x, y);
            }
        }
    }

    public boolean isTileSolid(int x, int y) {
        final int index = x + y * TILE_COUNT.x;
        return solid[index];
    }

    public boolean isTileSolid(Point p) {
        final int index = p.x + p.y * TILE_COUNT.x;
        return solid[index];
    }

    public boolean isJunction(Point point) {
        return junctions.contains(point);
    }

    public boolean isFood(Point point, int type) {
        if(!food.containsKey(point)) return false;
        return (food.get(point) == type);
    }

    public void removeFood(Point point) {
        food.remove(point);
    }
}