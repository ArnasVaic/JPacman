package com.pacman;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected int x;
    protected int y;
    protected AffineTransform transform;
    protected BufferedImage sprite;

    public abstract void draw(Graphics g);
    public abstract void update(double delta, GameMap map);
    public abstract void handle(KeyEvent e, GameMap map);
}
