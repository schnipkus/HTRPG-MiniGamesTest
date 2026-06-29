package org.example;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private Thread gameThread;
    private boolean running;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(() -> {
            while (running) {
                update();
                repaint();
                try { Thread.sleep(16); } catch (InterruptedException e) { break; } // ~60fps
            }
        });
        gameThread.start();
    }

    private void update() {
        // game logic goes here
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // drawing goes here
        g.setColor(Color.WHITE);
        g.drawString("Game running!", 350, 300);
    }
}