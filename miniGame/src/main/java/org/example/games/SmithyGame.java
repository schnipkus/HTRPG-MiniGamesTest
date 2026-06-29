package org.example.games;

import org.example.MiniGame;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import static org.example.ShakeEffect.shakeScreen;

public class SmithyGame implements MiniGame {

    private int timer = 0;
    private boolean hammerTime = false;
    private boolean showResult = false;
    private String resultText = "";

    private int markerX = 100;
    private int markerSpeed = 5;

    private int totalScore = 0; // tracks how good the sword ends up
    private int strikes = 0;    // how many times player has pressed space

    private final Image dwarfDown;
    private final Image dwarfUp;

    private final Node screen; // the node to shake on a good hit (your GamePanel canvas)

    public SmithyGame(Node screen) {
        this.screen = screen;
        dwarfDown = new Image(getClass().getResource("/dwarfArmDown.png").toExternalForm());
        dwarfUp = new Image(getClass().getResource("/dwarfArmUp.png").toExternalForm());
    }

    public int getTotalScore() { return totalScore; }
    public int getStrikes() { return strikes; }

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
    public void draw(GraphicsContext gc) {
        // Coloured Zones
        gc.setFill(Color.DARKGRAY); gc.fillRect(100, 500, 600, 40);
        gc.setFill(Color.ORANGE);   gc.fillRect(275, 500, 100, 40);
        gc.setFill(Color.GREEN);    gc.fillRect(375, 500, 50, 40);
        gc.setFill(Color.ORANGE);   gc.fillRect(425, 500, 100, 40);

        // the moving marker
        gc.setFill(Color.BLACK);
        gc.fillRect(markerX, 500, 10, 40);

        // Dwarf img
        if (hammerTime) {
            gc.drawImage(dwarfDown, 200, 200, 400, 300);
        } else {
            gc.drawImage(dwarfUp, 200, 200, 400, 300);
        }

        // hit or miss txt
        if (showResult) {
            gc.setFill(Color.GREEN);
            gc.fillText(resultText, 400, 150);
        }

        // counter for hits n stuff (add maximum and stuff later)
        gc.setFill(Color.WHITE);
        gc.fillText("Sword quality: " + totalScore, 20, 30);
        gc.fillText("Strikes: " + strikes, 20, 50);
    }

    @Override
    public void onKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.SPACE) {
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
            shakeScreen(screen, 16, 0.25);
        } else if (markerX >= 275 && markerX <= 525) {
            resultText = "Okay!";
            totalScore += 1;
            shakeScreen(screen, 5, 0.25);
        } else {
            resultText = "Miss!";
            shakeScreen(screen, 2, 0.25);
        }
    }
}