package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class SmithyGame implements MiniGame {

    private int timer = 0;
    private boolean hammerTime = false;
    private boolean showResult = false;
    private String resultText = "";

    private int markerX = 100;
    private int markerSpeed = 5;

    private int totalScore = 0; // tracks how good the sword ends up
    private int strikes = 0;    // how many times player has pressed space

    private Image dwarfDown;
    private Image dwarfUp;

    public SmithyGame() {
        dwarfDown = new ImageIcon(getClass().getResource("/dwarfArmDown.png")).getImage();
        dwarfUp = new ImageIcon(getClass().getResource("/dwarfArmUp.png")).getImage();
    }

    @Override
    public void update() {
        markerX += markerSpeed;
        if (markerX > 700 || markerX < 100) markerSpeed *= -1;

        if (timer > 0) {
            timer--;
        } else {
            hammerTime = false;
            showResult = false;
        }
    }

    @Override
    public void draw(Graphics g) {
        // Zones
        g.setColor(Color.DARK_GRAY); g.fillRect(100, 500, 600, 40);
        g.setColor(Color.ORANGE);    g.fillRect(275, 500, 100, 40);
        g.setColor(Color.GREEN);     g.fillRect(375, 500, 50, 40);
        g.setColor(Color.ORANGE);    g.fillRect(425, 500, 100, 40);

        // Marker
        g.setColor(Color.BLACK);
        g.fillRect(markerX, 500, 10, 40);

        // Dwarf
        if (hammerTime) {
            g.drawImage(dwarfDown, 200, 200, 400, 300, null);
        } else {
            g.drawImage(dwarfUp, 200, 200, 400, 300, null);
        }

        // Result text
        if (showResult) {
            g.setColor(Color.GREEN);
            g.drawString(resultText, 400, 150);
        }

        // Score
        g.setColor(Color.WHITE);
        g.drawString("Sword quality: " + totalScore, 20, 30);
        g.drawString("Strikes: " + strikes, 20, 50);
    }

    @Override
    public void onKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            checkResult();
        }
    }

    private void checkResult() {
        showResult = true;
        hammerTime = true;
        timer = 60;
        strikes++;

        if (markerX >= 375 && markerX <= 425) {
            resultText = "Perfect!";
            totalScore += 3;
        } else if (markerX >= 275 && markerX <= 525) {
            resultText = "Okay!";
            totalScore += 1;
        } else {
            resultText = "Miss!";
        }
    }

    public int getTotalScore() { return totalScore; }
    public int getStrikes() { return strikes; }
}