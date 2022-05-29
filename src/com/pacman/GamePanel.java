package com.pacman;

import javax.swing.*;

public class GamePanel extends JFrame {

    public GamePanel(String title, Game game) {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(game);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.toFront();
        this.requestFocus();
    }

}
