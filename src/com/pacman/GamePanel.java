package com.pacman;

import org.lwjgl.system.NonnullDefault;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JFrame {

    public GamePanel(String title, Game game) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.add(game);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);
    }

}
