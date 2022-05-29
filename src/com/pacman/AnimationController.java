package com.pacman;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimationController {

    public Point framePos;
    public final Point frameCount;
    public final Point spriteSize;

    public double timePerFrame;
    public double animTimer;

    public final BufferedImage spriteSheet;
    public BufferedImage sprite;

    public AnimationController(AnimationController ac) {
        this.sprite = ac.sprite;
        this.spriteSheet = ac.spriteSheet;

        this.frameCount = ac.frameCount;
        this.spriteSize = ac.spriteSize;
        this.framePos = new Point(0, 0);

        this.timePerFrame = ac.timePerFrame;
        this.animTimer = 0.0;
    }

    public AnimationController(BufferedImage spriteSheet, Point frameCount, Point spriteSize, double timePerFrame) {
        this.sprite = spriteSheet.getSubimage(0, 0, spriteSize.x, spriteSize.y);
        this.spriteSheet = spriteSheet;

        this.frameCount = frameCount;
        this.spriteSize = spriteSize;
        this.framePos = new Point(0, 0);

        this.timePerFrame = timePerFrame;
        this.animTimer = 0.0;
    }

    public void update(double delta) {
        animTimer += delta;
        if(animTimer > timePerFrame) {
            framePos.x++;
            framePos.x %= frameCount.x;
            animTimer = 0;
        }
        sprite = spriteSheet.getSubimage(
            framePos.x * spriteSize.x,
            framePos.y * spriteSize.y,
            spriteSize.x,
            spriteSize.y
        );
    }

    public void setAnimationIndex(int y) {
        framePos.y = y;
    }
}
