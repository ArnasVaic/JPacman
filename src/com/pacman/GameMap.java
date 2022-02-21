package com.pacman;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameMap {

    public final static int tileCountX = 28;
    public final static int tileCountY = 36;
    public final static int tileWidth = 8;
    public final static int tileHeight = 8;

    private BufferedImage image;
    private AffineTransform transform;

    public boolean renderGrid = false;

    // Each bit in this string is a boolean value which determines
    // if a tile is solid or not. Hex character index hold the information
    // about tile position
    private boolean[] collisionData;

    public GameMap() {
        transform = new AffineTransform();
        transform.setToScale(Game.SCALE, Game.SCALE);
        try {
            image = ImageIO.read(new File("assets/map.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        // read map collision data and convert it to binary
        collisionData = new boolean[tileCountX * tileCountY];
        byte[] bytes = {0};
        try {
            bytes = Files.readAllBytes(Paths.get("assets/mapdata"));
        } catch(IOException e) {
            e.printStackTrace();
        }
        final int bitsPerByte = 8;
        for(int i = 0; i < bytes.length; i++) {
            for(int j = 0; j < bitsPerByte; j++) {
                final int index = bitsPerByte * i + bitsPerByte - 1 - j;
                final int flag = (bytes[i] >> j) & 1;
                collisionData[index] = (flag == 1);
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(image, transform, null);

        if(renderGrid) {
            g.setColor(new Color(255, 255, 255, 127));
            for(int i = 0; i < tileCountX; i++) {
                final int x = tileWidth * Game.SCALE;
                final int w = Game.WIDTH * Game.SCALE;
                g.drawLine(i * x, 0, i * x, w);
            }
            for(int i = 0; i < tileCountY; i++) {
                final int y = tileHeight * Game.SCALE;
                final int h = Game.HEIGHT * Game.SCALE;
                g.drawLine(0, i * y, h, i * y);
            }
        }
    }

    public boolean isTileSolid(int x, int y) {
        return collisionData[x + y * tileCountX];
    }
}
