package org.example.games;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.example.GamePanel;
import org.example.MiniGame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CashCatch implements MiniGame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    // --- shooter (top, moves left/right on its own, fires downward) ---
    private static final double SHOOTER_Y = 30;
    private static final int SHOOTER_WIDTH = 70;
    private static final int SHOOTER_HEIGHT = 70;
    private static final double SHOOTER_BASE_SPEED = 180; // px/sec
    private double shooterX = WIDTH / 2.0 - SHOOTER_WIDTH / 2.0;
    private int shooterDir = 1; // 1 = right, -1 = left

    // --- player (bottom, catches projectiles) ---
    private static final double PLAYER_Y = HEIGHT - 90;
    private static final int PLAYER_WIDTH = 80;
    private static final int PLAYER_HEIGHT = 80;
    private static final double PLAYER_SPEED = 420; // px/sec
    private double playerX = WIDTH / 2.0 - PLAYER_WIDTH / 2.0;
    private boolean movingLeft = false;
    private boolean movingRight = false;

    // --- projectiles (coins falling from the shooter) ---
    private static final double PROJECTILE_BASE_SPEED = 270; // px/sec
    private static final int PROJECTILE_WIDTH = 24;
    private static final int PROJECTILE_HEIGHT = 24;
    private final List<double[]> projectiles = new ArrayList<>(); // {x, y}

    // --- firing cadence (gets a little faster over time) ---
    private static final double SHOOT_INTERVAL_SECONDS_START = 70.0 / 60.0;
    private static final double SHOOT_INTERVAL_SECONDS_MIN = 30.0 / 60.0;
    private double timeSinceLastShot = 0;

    // --- lives ---
    private static final int START_LIVES = 3;
    private int lives = START_LIVES;

    // --- survival / win condition ---
    private static final int SURVIVE_SECONDS = 30;
    private long startTimeNanos = -1;
    private boolean gameOver = false;
    private boolean won = false;

    private int score = 0;

    private final Image furry;
    private final Image dragonborn;
    private final Image coin;

    public CashCatch(GamePanel gamePanel) {
        furry = new Image(getClass().getResource("/furry.png").toExternalForm());
        dragonborn = new Image(getClass().getResource("/dragonbornTest.png").toExternalForm());
        coin = new Image(getClass().getResource("/coin.png").toExternalForm());
    }

    @Override
    public void update(double dt) {
        if (gameOver) return;

        if (startTimeNanos < 0) startTimeNanos = System.nanoTime();
        double elapsed = (System.nanoTime() - startTimeNanos) / 1_000_000_000.0;
        if (elapsed >= SURVIVE_SECONDS) {
            gameOver = true;
            won = true;
            return;
        }

        // difficulty ramps up the longer the round goes
        double difficulty = Math.min(1.0, elapsed / SURVIVE_SECONDS);
        double shooterSpeed = SHOOTER_BASE_SPEED + difficulty * 150; // px/sec
        double projectileSpeed = PROJECTILE_BASE_SPEED + difficulty * 210; // px/sec
        double shootInterval = SHOOT_INTERVAL_SECONDS_START
                - difficulty * (SHOOT_INTERVAL_SECONDS_START - SHOOT_INTERVAL_SECONDS_MIN);

        // move shooter, bounce off walls
        shooterX += shooterSpeed * shooterDir * dt;
        if (shooterX <= 0) {
            shooterX = 0;
            shooterDir = 1;
        } else if (shooterX >= WIDTH - SHOOTER_WIDTH) {
            shooterX = WIDTH - SHOOTER_WIDTH;
            shooterDir = -1;
        }

        // move player, clamped to screen
        if (movingLeft) playerX -= PLAYER_SPEED * dt;
        if (movingRight) playerX += PLAYER_SPEED * dt;
        playerX = Math.max(0, Math.min(WIDTH - PLAYER_WIDTH, playerX));

        // shooter fires periodically
        timeSinceLastShot += dt;
        if (timeSinceLastShot >= shootInterval) {
            timeSinceLastShot = 0;
            shoot();
        }

        // move projectiles downward
        Iterator<double[]> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            double[] p = projIt.next();
            p[1] += projectileSpeed * dt;

            // caught by player?
            if (rectsOverlap(p[0], p[1], PROJECTILE_WIDTH, PROJECTILE_HEIGHT,
                    playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT)) {
                score += 10;
                projIt.remove();
                continue;
            }

            // missed -> lose a life
            if (p[1] > HEIGHT) {
                projIt.remove();
                lives--;
                if (lives <= 0) {
                    gameOver = true;
                    won = false;
                }
            }
        }
    }

    private boolean rectsOverlap(double x1, double y1, double w1, double h1,
                                 double x2, double y2, double w2, double h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    private void shoot() {
        double x = shooterX + SHOOTER_WIDTH / 2.0 - PROJECTILE_WIDTH / 2.0;
        double y = SHOOTER_Y + SHOOTER_HEIGHT;
        projectiles.add(new double[]{x, y});
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.drawImage(dragonborn, shooterX, SHOOTER_Y, SHOOTER_WIDTH, SHOOTER_HEIGHT);
        gc.drawImage(furry, playerX, PLAYER_Y, PLAYER_WIDTH, PLAYER_HEIGHT);

        for (double[] p : projectiles) {
            gc.drawImage(coin, p[0], p[1], PROJECTILE_WIDTH, PROJECTILE_HEIGHT);
        }

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 20, 20);
        gc.fillText("Lives: " + lives, 20, 40);
        double remaining = startTimeNanos < 0
                ? SURVIVE_SECONDS
                : Math.max(0, SURVIVE_SECONDS - (System.nanoTime() - startTimeNanos) / 1_000_000_000.0);
        gc.fillText("Time: " + (int) Math.ceil(remaining), 20, 60);

        if (gameOver) {
            gc.setFill(won ? Color.LIME : Color.RED);
            gc.fillText(won ? "Survived! Final score: " + score : "Out of lives! Final score: " + score, 260, 300);
        }
    }

    @Override
    public void onKeyPress(KeyEvent e) {
        switch (e.getCode()) {
            case LEFT, A -> movingLeft = true;
            case RIGHT, D -> movingRight = true;
            case ESCAPE -> Platform.exit();
            default -> {}
        }
    }

    @Override
    public void onKeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case LEFT, A -> movingLeft = false;
            case RIGHT, D -> movingRight = false;
            default -> {}
        }
    }

    public int getScore() { return score; }
}