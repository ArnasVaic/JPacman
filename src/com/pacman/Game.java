package com.pacman;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Game extends Canvas {

    public final static int SCALE = 2;
    public final static int WIDTH = 224 * SCALE;
    public final static int HEIGHT = 288 * SCALE;



    public final int score = 0;

    boolean running;

    public BufferedImage image;

    Entity player;

    private GamePanel gpanel;
    private GameMap map;

    public Game() {
        running = true;
        this.addKeyListener(new GameKeyListener(this));

        this.setSize(new Dimension(WIDTH, HEIGHT));


        player = new Player(14, 20);
        map = new GameMap();

        gpanel = new GamePanel("Pacman, FPS: 0", this);
        this.run();
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while(running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                update(delta);
                delta--;
            }
            draw();
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                gpanel.setTitle("Pacman, FPS: " + frames);
                frames = 0;
            }
        }
    }

    public void update(double delta) {
        player.update(delta, map);
    }

    public void draw() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.black);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        map.draw(g);
        player.draw(g);

        g.dispose();
        bs.show();
    }

    public void handle(KeyEvent e) {
        player.handle(e, map);
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            map.renderGrid = !map.renderGrid;
        }
    }
}
