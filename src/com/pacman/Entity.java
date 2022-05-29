package com.pacman;

import java.awt.*;
import java.awt.event.KeyEvent;

public interface Entity {
    void draw(Graphics g);
    void update(double delta, Game game);
    void handle(KeyEvent e, Game game);
}
