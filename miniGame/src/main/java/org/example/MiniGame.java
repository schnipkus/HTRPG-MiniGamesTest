package org.example;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public interface MiniGame {
    void update();
    void draw(GraphicsContext gc);
    void onKeyPress(KeyEvent e);
    default void onKeyRelease(KeyEvent e) {}
}