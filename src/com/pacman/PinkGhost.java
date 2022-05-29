package com.pacman;

import java.awt.*;
import static com.pacman.PointUtils.*;

public class PinkGhost extends Ghost {

    public static final int ghostIndex = 1;
    public final static Point scatterTarget = new Point(2, 0);

    public PinkGhost(int x, int y, int vx, int vy) {
        super(x, y, vx, vy, ghostIndex);
    }

    @Override
    protected void scatter(double delta, Game game) {
        target = scatterTarget;
        seek(game.map);
    }

    protected void chase(double delta, Game game) {
        Point pp = game.player.position;
        Point pv = game.player.velocity;
        // Pink ghost chasing logic
        // https://gameinternals.com/understanding-pac-man-ghost-behavior
        final Point front = add(pp, scale(pv, 4));
        if((pv.x == 0) && (pv.y == -1)) front.x -= 4;
        target = clamp(front, GameMap.MIN_BOUNDS, GameMap.MAX_BOUNDS);
        seek(game.map);
    }
}
