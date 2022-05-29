package com.pacman;

import java.awt.*;

public class RedGhost extends Ghost {

    public static final int ghostIndex = 0;
    public final static Point scatterTarget = new Point(25, 0);

    public RedGhost(int x, int y, int vx, int vy) {
        super(x, y, vx, vy, ghostIndex);
    }

    @Override
    protected void scatter(double delta, Game game) {
        target = new Point(game.player.position);
        seek(game.map);
    }

    @Override
    protected void chase(double delta, Game game) {
        target = new Point(game.player.position);
        seek(game.map);
    }
}
