package com.pacman;

import java.awt.*;

public class YellowGhost extends Ghost {

    public static final int MAX_DIST = 8;
    public static final int ghostIndex = 2;
    public static final Point scatterTarget = new Point(0, 35);

    public YellowGhost(int x, int y, int vx, int vy) {
        super(x, y, vx, vy, ghostIndex);
    }

    @Override
    protected void scatter(double delta, Game game) {
        target = scatterTarget;
        seek(game.map);
    }

    protected void chase(double delta, Game game) {
        final Point diff = PointUtils.sub(game.player.position, position);
        final boolean inRange = PointUtils.sqmag(diff) < MAX_DIST * MAX_DIST;
        target = inRange ? scatterTarget : new Point(game.player.position);
        seek(game.map);
    }
}
