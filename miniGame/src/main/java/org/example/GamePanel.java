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
        currentGame = new org.example.games.CashDrop(this);

    }

    public void setGame(MiniGame game) { // chooses the game that plays. just for this project to try all easily
        currentGame = game;
    }

    public void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentGame != null) currentGame.update();
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