package org.example;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GamePanel extends Canvas {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private MiniGame currentGame;
    private AnimationTimer gameLoop;

    public GamePanel() {
        super(WIDTH, HEIGHT);
        setFocusTraversable(true); // canvas needs this to receive key events

        setOnKeyPressed(e -> {
            if (currentGame != null) currentGame.onKeyPress(e);
        });
        setOnKeyReleased(e -> {
            if (currentGame != null) currentGame.onKeyRelease(e);
        });

        //currentGame = new org.example.games.SmithyGame(this);
        //currentGame = new org.example.games.PolicySwiper(this);
        //currentGame = new org.example.games.CashDrop(this);
        currentGame = new org.example.games.CashCatch(this);

    }

    public void setGame(MiniGame game) { // chooses the game that plays. just for this project to try all easily
        currentGame = game;
    }

    // fixed time: game logic always advances at this many updates per real second, not bound to fps no more
    private static final long UPDATES_PER_SECOND = 60;
    private static final long UPDATE_INTERVAL_NANOS = 1_000_000_000L / UPDATES_PER_SECOND;
    private static final long MAX_ACCUMULATED_NANOS = UPDATE_INTERVAL_NANOS * 5; // cap "catch up" after a freeze/tab switch

    private long lastUpdateNanos = -1;
    private long accumulatorNanos = 0;

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateNanos < 0) {
                    lastUpdateNanos = now; // first frame: nothing to catch up on yet
                }

                accumulatorNanos += now - lastUpdateNanos;
                lastUpdateNanos = now;

                // avoid a "spiral of death" if the app was paused/backgrounded
                if (accumulatorNanos > MAX_ACCUMULATED_NANOS) {
                    accumulatorNanos = MAX_ACCUMULATED_NANOS;
                }

                while (accumulatorNanos >= UPDATE_INTERVAL_NANOS) {
                    if (currentGame != null) currentGame.update(UPDATE_INTERVAL_NANOS / 1_000_000_000.0);
                    accumulatorNanos -= UPDATE_INTERVAL_NANOS;
                }

                render();
            }
        };
        gameLoop.start();
    }

    private void render() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        if (currentGame != null) currentGame.draw(gc);
    }
}