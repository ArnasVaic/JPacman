package com.pacman;

import java.awt.*;
import static com.pacman.PointUtils.*;

public class CyanGhost extends Ghost {

    public final static int ghostIndex = 3;
    public final static Point scatterTarget = new Point(27, 35);

    public CyanGhost(int x, int y, int vx, int vy) {
        super(x, y, vx, vy, ghostIndex);
    }

    @Override
    protected void scatter(double delta, Game game) {
        target = scatterTarget;
        seek(game.map);
    }

    @Override
    protected void chase(double delta, Game game) {
        // Cyan ghost chasing logic
        // https://gameinternals.com/understanding-pac-man-ghost-behavior
        final Point redGhostPosition = game.ghosts.get(0).position;
        final Point playerFront = add(game.player.position, scale(game.player.velocity, 2));
        final Point diff = scale(sub(playerFront, redGhostPosition), 2);
        target = clamp(add(diff, redGhostPosition), GameMap.MIN_BOUNDS, GameMap.MAX_BOUNDS);
        seek(game.map);
    }
}
