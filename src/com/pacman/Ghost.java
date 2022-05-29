package com.pacman;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Function;

public class Ghost implements Entity {

    public static final int ghostColorAlpha = 190;
    public static final Color[] ghostColors = {
            new Color(255, 0, 0, ghostColorAlpha),
            new Color(252, 181, 255, ghostColorAlpha),
            new Color(248, 187, 85, ghostColorAlpha),
            new Color(0, 255, 255, ghostColorAlpha)
    };

    public static final Map<Point, Integer> velocityAnimationMap;
    static {
        velocityAnimationMap = new HashMap<>();
        velocityAnimationMap.put(new Point(1, 0), 0);
        velocityAnimationMap.put(new Point(-1, 0), 1);
        velocityAnimationMap.put(new Point(0, -1), 2);
        velocityAnimationMap.put(new Point(0, 1), 3);
    }

    public static final Map<Integer, String> phaseStringMap;
    static {
        phaseStringMap = new HashMap<>();
        phaseStringMap.put(Ghost.SCATTER_PHASE, "Scatter");
        phaseStringMap.put(Ghost.FRIGHTENED_PHASE, "Frightened");
        phaseStringMap.put(Ghost.CHASE_PHASE, "Chase");
    }

    public static boolean canMove = true;
    protected static boolean drawDebug = true;

    public final static int SCATTER_PHASE       = 1;
    public final static int CHASE_PHASE         = 2;
    public final static int FRIGHTENED_PHASE    = 3;

    private final static double CHASE_PHASE_TIME        =   20 * 60;
    private final static double SCATTER_PHASE_TIME      =    7 * 60;
    private final static double FRIGHTENED_PHASE_TIME   =    6 * 60;

    protected double phaseTime = SCATTER_PHASE_TIME;
    protected double phaseTimer = 0.0;

    protected int phase = SCATTER_PHASE;

    protected Point position;
    protected Point velocity;
    protected Image targetImage;

    protected AnimationController anim;

    protected Point target = new Point(0, 0);
    public final int GIndex;

    public Ghost(int x, int y, int vx, int vy, int ghostIndex) {
        GIndex = ghostIndex;
        this.position = new Point(x, y);
        this.velocity = new Point(vx, vy);
        targetImage = Assets.targets[ghostIndex];
        anim = new AnimationController(Assets.ghostAnim[ghostIndex]);
    }

    public void handleJunction(GameMap map, Function<ArrayList<Point>, Point> choseVelocity) {
        // Generate possible velocities
        ArrayList<Point> velocities = new ArrayList<>();
        velocities.add(velocity);
        velocities.add(new Point(+velocity.y, -velocity.x));
        velocities.add(new Point(-velocity.y, +velocity.x));
        // Remove invalid velocities
        velocities.removeIf(v -> map.isTileSolid(PointUtils.add(position, v)));
        velocity = choseVelocity.apply(velocities);
    }

    public Point pickClosestToTarget(ArrayList<Point> velocities) {
        HashMap<Integer, Point> map = new HashMap<>();
        for(Point v : velocities) {
            final Point diff = PointUtils.sub(PointUtils.add(position, v), target);
            map.put(PointUtils.sqmag(diff), v);
        }
        return Collections.min(map.entrySet(), Map.Entry.comparingByKey()).getValue();
    }

    public Point pickRandom(ArrayList<Point> velocities) {
        return velocities.get(Math.max(new Random().nextInt(), 0) % velocities.size());
    }

    protected void seek(GameMap map) {
        if(map.isJunction(position)) {
            handleJunction(map, this::pickClosestToTarget);
        }
    }

    protected void chase(double delta, Game game) {

    }

    protected void frightened(double delta, Game game) {
        if(game.map.isJunction(position)) {
            handleJunction(game.map, this::pickRandom);
        }
    }

    protected void scatter(double delta, Game game) {

    }

    @Override
    public void draw(Graphics g) {
        final Point spriteSize = PointUtils.scale(anim.spriteSize, Game.SCALE);
        final Point tileSize = GameMap.SCALED_TILE_SIZE;
        final Point offset = PointUtils.scale(PointUtils.sub(tileSize, spriteSize), 0.5);
        final Point drawPosition = PointUtils.add(PointUtils.mul(position, tileSize), offset);
        if(drawDebug) {
            final Point targetPos = PointUtils.mul(tileSize, target);
            final Point half = PointUtils.scale(tileSize, 0.5);
            final Point p =  PointUtils.add(PointUtils.mul(tileSize, target), half);
            final Point q =  PointUtils.add(PointUtils.mul(tileSize, position), half);
            DrawUtils.drawImage(g, targetImage, targetPos, tileSize);
            g.setColor(ghostColors[GIndex]);
            DrawUtils.drawLine(g, p, q);
        }
        DrawUtils.drawImage(g, anim.sprite, drawPosition, spriteSize);
    }

    @Override
    public void update(double delta, Game game) {
        phaseTimer += delta;

        if(phaseTimer >= phaseTime) {
            phaseTimer = 0;
            if(phase == SCATTER_PHASE ) {
                setPhase(CHASE_PHASE);
                phaseTime = CHASE_PHASE_TIME;
            }
            else if((phase == CHASE_PHASE) || (phase == FRIGHTENED_PHASE)) {
                setPhase(SCATTER_PHASE);
                phaseTime = SCATTER_PHASE_TIME;
            }
        }

        anim.update(delta);
        anim.setAnimationIndex(velocityAnimationMap.get(velocity));

        if(game.moveTimer >= Game.TIME_PER_MOVE) {

            switch(phase) {
                case FRIGHTENED_PHASE -> frightened(delta, game);
                case SCATTER_PHASE -> scatter(delta, game);
                case CHASE_PHASE -> chase(delta, game);
            }

            if(canMove) {
                final Point newPosition = new Point(position);
                newPosition.translate(velocity.x, velocity.y);
                final boolean solid = game.map.isTileSolid(newPosition);
                final Point right = new Point(1, 0);
                final Point left = new Point(-1, 0);
                final Point leftTunnel = new Point(0, 17);
                final Point rightTunnel = new Point(27, 17);
                final boolean goingRight = velocity.equals(right) && position.equals(rightTunnel);
                final boolean goingLeft = velocity.equals(left) && position.equals(leftTunnel);
                // apply velocity only if way is clear or
                if(!solid || goingRight || goingLeft) {
                    position.translate(velocity.x, velocity.y);
                }
            }

            // Tunneling logic
            if(position.x == GameMap.TILE_COUNT.x) position.x = 0;
            if(position.y == GameMap.TILE_COUNT.y) position.y = 0;
            if(position.x == -1) position.x = GameMap.TILE_COUNT.x - 1;
            if(position.y == -1) position.y = GameMap.TILE_COUNT.y - 1;
        }
    }

    public void setPhase(int phase) {
        if(this.phase == phase) return;
        this.phase = phase;
        phaseTimer = 0.0;
        switch(phase) {
            case SCATTER_PHASE -> phaseTime = SCATTER_PHASE_TIME;
            case CHASE_PHASE -> phaseTime = CHASE_PHASE_TIME;
            case FRIGHTENED_PHASE -> phaseTime = FRIGHTENED_PHASE_TIME;
        }
        if(phase == FRIGHTENED_PHASE) {
            anim = new AnimationController(Assets.frigAnim);
        } else {
            anim  = new AnimationController(Assets.ghostAnim[GIndex]);
        }
    }

    @Override
    public void handle(KeyEvent e, Game game) {

    }
}
