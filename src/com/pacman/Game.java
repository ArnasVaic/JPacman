package com.pacman;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class Game extends Canvas {
    public final static int SCALE = 3;
    public final static Point SIZE = new Point(224 * SCALE, 288 * SCALE);
    public final static int smallFoodScore = 10;
    public final static int bigFoodScore = 50;

    private static final Point SCORE_TEXT_POS = new Point(
        GameMap.SCALED_TILE_SIZE.x * 2,
        GameMap.SCALED_TILE_SIZE.y * 2
    );
    private static final Point HSCORE_TEXT_POS = new Point(
        GameMap.SCALED_TILE_SIZE.x * 9,
        GameMap.SCALED_TILE_SIZE.x
    );

    private final boolean running;
    private boolean drawDebug = true;

    public Player player;
    public ArrayList<Ghost> ghosts;
    private final GamePanel gamePanel;
    public GameMap map;

    public int score = 0;
    public int highScore = 0;

    private boolean idle = true;
    private boolean gameOver = false;
    private boolean youWin = false;

    // idle state time, after that, game begins
    private final static double IDLE_TIME = 200;
    private double idleTimer;

    public double moveTimer = 0;
    public final static double TIME_PER_MOVE = 10;

    public Game() {
        try {
            Assets.getInstance().load();
        } catch(AssetNotFoundException e) {
            e.printStackTrace();
        }

        this.addKeyListener(new GameKeyListener(this));
        this.setSize(new Dimension(SIZE.x, SIZE.y));
        this.setFont(Assets.font);

        idleTimer = 0.0;
        running = true;

        player = new Player(14, 26);
        ghosts = new ArrayList<>();
        ghosts.add(GhostFactory.create("Red"));
        ghosts.add(GhostFactory.create("Cyan"));
        ghosts.add(GhostFactory.create("Pink"));
        ghosts.add(GhostFactory.create("Yellow"));
        map = new GameMap();

        gamePanel = new GamePanel("Pacman, FPS: 0", this);
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

            BufferStrategy bs = this.getBufferStrategy();
            if(bs == null) {
                createBufferStrategy(3);
                continue;
            }
            Graphics g = bs.getDrawGraphics();

            draw(g);

            g.dispose();
            bs.show();

            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                gamePanel.setTitle("Pacman, FPS: " + frames);
                frames = 0;
            }
        }
    }

    public void update(double delta) {
        idleTimer += delta;
        idle = !(idleTimer >= IDLE_TIME);
        moveTimer += delta;

        player.update(delta, this);
        for (Ghost g : ghosts) {
            g.update(delta, this);
        }

        if(moveTimer >= TIME_PER_MOVE) {
            moveTimer = 0;
        }

        // check if player collides with ghost?
        for(Ghost g: ghosts) {
            switch(g.phase) {
                case Ghost.FRIGHTENED_PHASE -> {
                    // TODO: create state where ghost tries to run to center
                }
                default -> {
                    if (player.position.equals(g.position)) {
                        gameOver = true;
                        player.canMove = false;
                        Ghost.canMove = false;
                        break;
                    }
                }
            }
        }

        if(map.food.size() == 0) {
            youWin = true;
            player.canMove = false;
            Ghost.canMove = false;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;

        g.setColor(Color.black);
        g.fillRect(0, 0, SIZE.x, SIZE.y);

        final int w = GameMap.SCALED_TILE_SIZE.x;
        final int h = GameMap.SCALED_TILE_SIZE.x;

        if(idle) {
            g.setColor(Color.yellow);
            g2D.drawString("ready!", 11 * w, 21 * h);
        }
        if(gameOver) {
            g.setColor(Color.red);
            g2D.drawString("game  over", 9 * w, 21 * h);
        }
        if(youWin) {
            g.setColor(Color.green);
            g2D.drawString("you  win", 10 * w, 21 * h);
        }

        // draw score board
        g.setColor(Color.white);
        g2D.drawString(String.valueOf(score), SCORE_TEXT_POS.x, SCORE_TEXT_POS.y);
        g2D.drawString("high score", HSCORE_TEXT_POS.x, HSCORE_TEXT_POS.y);

        map.draw(g);
        player.draw(g);
        for(Entity e: ghosts) e.draw(g);

        if(drawDebug) {
            for(int i = 0; i < ghosts.size(); ++i) {
                Ghost gh = ghosts.get(i);
                String string = "t=%.1f,<%d,%d>,%s".formatted(gh.phaseTimer / 60.0, gh.position.x, gh.position.y, Ghost.phaseStringMap.get(gh.phase));
                g.setColor(Ghost.ghostColors[gh.GIndex]);
                g2D.drawString(string, 0, (i + 4) * h);
            }
            String string = "Pacman:<%d,%d>".formatted(player.position.x, player.position.y);
            g.setColor(Color.yellow);
            g2D.drawString(string, 0, (ghosts.size() + 4) * h);
        }
    }

    public void handle(KeyEvent e) {
        player.handle(e, this);
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            map.renderGrid = !map.renderGrid;
            Ghost.drawDebug = !Ghost.drawDebug;
            this.drawDebug = !this.drawDebug;
        }
    }
}
