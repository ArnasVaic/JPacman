package com.pacman;

import org.lwjgl.openal.ALUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Player extends Entity {

    private double timePerMove = 20;
    private double moveTimer = 0.0;
    // velocity of the
    private int velX = 0;
    private int velY = 0;

    // Animation variables
    private double animTimer = 0;
    private int frameX = 0;
    private int frameY = 0;
    private double timePerFrame = 5;
    private final int frameCountX = 3;
    private final int frameCountY = 4;

    private final int spriteWidth = 16;
    private final int spriteHeight = 16;

    private BufferedImage sheet = null;

    /**
     *
     * @param x x coordinate in tile space
     * @param y y coordiante in tile space
     */
    Player(int x, int y) {

        this.x = x;
        this.y = y;

        try {
            sheet = ImageIO.read(new File("assets/playerSheet.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        transform = new AffineTransform();
        transform.setToScale(Game.SCALE, Game.SCALE);
        transform.translate(x, y);

        if(sheet != null) {
            sprite = sheet.getSubimage(0, 0, spriteWidth, spriteHeight);
        }
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        //g2D.drawImage(sprite, transform, null);
        final int sw = spriteWidth * Game.SCALE;
        final int sh = spriteHeight * Game.SCALE;

        final int w = GameMap.tileWidth * Game.SCALE;
        final int h = GameMap.tileHeight * Game.SCALE;

        final double offsetX = (w - sw) / 2;
        final double psx = offsetX + (x - velX) * w;
        final double csx = offsetX + (x +    0) * w;
        final double nsx = offsetX + (x + velX) * w;

        boolean Xpos = velX > 0;

        //final int sx = (int) GameUtils.lerp(Xpos?psx:csx, Xpos?csx:nsx, moveTimer / timePerMove);
        final int sx = (int) csx;
        final double csy = y * h + (h - sh) / 2;
        final double nsy = (y + velY) * h + (h - sh) / 2;

        //final int sy = (int) GameUtils.lerp(csy, nsy, moveTimer / timePerMove);
        final int sy = (int) csy;
        g.drawImage(sprite, sx, sy, sw, sh, null);
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(x * w, y * h, w, h);
    }

    @Override
    public void update(double delta, GameMap map) {
        moveTimer += delta;
        animTimer += delta;

        if(map.isTileSolid(x + velX, y + velY)) {
            velX = 0;
            velY = 0;
        }

        if(moveTimer > timePerMove) {
            x += velX;
            y += velY;
            moveTimer = 0;
        }

        if(animTimer > timePerFrame) {
            frameX++;
            frameX %= frameCountX;
            animTimer = 0;
        }

        sprite = sheet.getSubimage(
            frameX * spriteWidth,
            frameY * spriteHeight,
            spriteWidth,
            spriteHeight
        );
    }

    @Override
    public void handle(KeyEvent e, GameMap map) {

        // Player position in tile space
        int tx = (int) x / (GameMap.tileWidth * Game.SCALE);
        int ty = (int) y / (GameMap.tileHeight * Game.SCALE);

        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> {
                if(!map.isTileSolid(x + 1, y )) {
                    velX = 1;
                    velY = 0;
                    frameY = 0;
                }
            }
            case KeyEvent.VK_LEFT -> {
                if(!map.isTileSolid(x - 1, y )) {
                    velX = -1;
                    velY = 0;
                    frameY = 1;
                }
            }
            case KeyEvent.VK_DOWN -> {
                if(!map.isTileSolid(x, y + 1)) {
                    velX = 0;
                    velY = 1;
                    frameY = 2;
                }
            }
            case KeyEvent.VK_UP -> {
                if(!map.isTileSolid(x, y - 1)) {
                    velX = 0;
                    velY = -1;
                    frameY = 3;
                }
            }
        }
    }
}
