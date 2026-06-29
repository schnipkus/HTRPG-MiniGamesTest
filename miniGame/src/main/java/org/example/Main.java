package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("HTRPG Minigame");
            window.setSize(800, 600);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            GamePanel panel = new GamePanel();
            window.add(panel);
            window.pack();
            window.setLocationRelativeTo(null); // center on screen
            window.setVisible(true);

            panel.startGameLoop();
        });
    }
}