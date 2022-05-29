package com.pacman;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Player implements Entity {
    Point position;
    Point velocity = new Point(1, 0);
    public AnimationController anim;
    public boolean canMove = true;

    Player(int x, int y) {
        position = new Point(x, y);
        this.anim = new AnimationController(
            Assets.pacman,
            new Point(3, 4),
            new Point(16, 16),
            5
        );
    }

    @Override
    public void draw(Graphics g) {
        final Point spriteSize = PointUtils.scale(anim.spriteSize, Game.SCALE);
        final Point tileSize = GameMap.SCALED_TILE_SIZE;
        final Point offset = PointUtils.scale(PointUtils.sub(tileSize, spriteSize), 0.5);
        final Point drawPosition = PointUtils.add(PointUtils.mul(position, tileSize), offset);
        DrawUtils.drawImage(g, anim.sprite, drawPosition, spriteSize);
    }

    @Override
    public void update(double delta, Game game) {
        if(game.moveTimer >= Game.TIME_PER_MOVE) {
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


            if(game.map.isFood(position, GameMap.SMALL_FOOD)) {
                game.map.removeFood(position);
                game.score += Game.smallFoodScore;
            }
            if(game.map.isFood(position, GameMap.BIG_FOOD)) {
                game.map.removeFood(position);
                game.score += Game.bigFoodScore;
                // ghost go nuts
                for(Ghost g: game.ghosts) {
                    g.setPhase(Ghost.FRIGHTENED_PHASE);
                }
            }
        }
        anim.update(delta);
    }

    @Override
    public void handle(KeyEvent e, Game game) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> {
                if(!game.map.isTileSolid(position.x + 1, position.y)) {
                    velocity = new Point(1, 0);
                    anim.setAnimationIndex(0);
                }
            }
            case KeyEvent.VK_LEFT -> {
                if(!game.map.isTileSolid(position.x - 1, position.y)) {
                    velocity = new Point(-1, 0);
                    anim.setAnimationIndex(1);
                }
            }
            case KeyEvent.VK_DOWN -> {
                if(!game.map.isTileSolid(position.x, position.y + 1)) {
                    velocity = new Point(0, 1);
                    anim.setAnimationIndex(2);
                }
            }
            case KeyEvent.VK_UP -> {
                if(!game.map.isTileSolid(position.x, position.y - 1)) {
                    velocity = new Point(0, -1);
                    anim.setAnimationIndex(3);
                }
            }
        }
    }
}
