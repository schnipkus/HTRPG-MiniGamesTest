package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private MiniGame currentGame;
    private Thread gameThread;
    private boolean running;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (currentGame != null) currentGame.onKeyPress(e);
            }
        });

        // start with the smithy game for now
        currentGame = new org.example.SmithyGame();
    }

    public void setGame(MiniGame game) { //chooses the game that plays. just for this project to try all easily
        currentGame = game;
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(() -> {
            while (running) {
                if (currentGame != null) currentGame.update();
                repaint();
                try { Thread.sleep(16); } catch (InterruptedException e) { break; }
            }
        });
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentGame != null) currentGame.draw(g);
    }
}