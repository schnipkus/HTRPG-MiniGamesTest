package org.example.games;

import javafx.application.Platform;
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

    // bounds the marker travels between (track stays fixed, zones move within it)
    private static final int TRACK_MIN = 100;
    private static final int TRACK_MAX = 700;

    // width of the zones, kept constant; only their position (zoneCenter) changes
    private static final int PERFECT_WIDTH = 50;
    private static final int OKAY_WIDTH = 250;

    // current center of the target zones, randomized on each space press
    private int zoneCenter = 400;

    // how much the marker speed increases after every strike
    private static final int speedUp = 1;

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
        if(strikes > 10) Platform.exit(); //minigame auto closes
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Coloured Zones
        int okayStart = zoneCenter - OKAY_WIDTH / 2;
        int perfectStart = zoneCenter - PERFECT_WIDTH / 2;

        gc.setFill(Color.DARKGRAY); gc.fillRect(TRACK_MIN, 500, TRACK_MAX - TRACK_MIN, 40);
        gc.setFill(Color.ORANGE);   gc.fillRect(okayStart, 500, OKAY_WIDTH, 40);
        gc.setFill(Color.GREEN);    gc.fillRect(perfectStart, 500, PERFECT_WIDTH, 40);

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

        int perfectStart = zoneCenter - PERFECT_WIDTH / 2;
        int perfectEnd = zoneCenter + PERFECT_WIDTH / 2;
        int okayStart = zoneCenter - OKAY_WIDTH / 2;
        int okayEnd = zoneCenter + OKAY_WIDTH / 2;

        if (markerX >= perfectStart && markerX <= perfectEnd) {
            resultText = "Perfect!";
            totalScore += 3;
            shakeScreen(screen, 16, 0.25);
        } else if (markerX >= okayStart && markerX <= okayEnd) {
            resultText = "Okay!";
            totalScore += 1;
            shakeScreen(screen, 5, 0.25);
        } else {
            resultText = "Miss!";
            shakeScreen(screen, 2, 0.25);
        }
        // moves coloured bar
        int minCenter = TRACK_MIN + OKAY_WIDTH / 2;
        int maxCenter = TRACK_MAX - OKAY_WIDTH / 2;
        //decides new middle where everything moves relatively to
        zoneCenter = new java.util.Random().nextInt(minCenter, maxCenter + 1);

        // gas gas gas
        if (markerSpeed > 0) markerSpeed += speedUp;
        if (markerSpeed < 0) markerSpeed -= speedUp;
         }
}