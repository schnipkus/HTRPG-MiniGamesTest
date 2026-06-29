package org.example;
import java.awt.*;
import java.awt.event.KeyEvent;

public interface MiniGame {
    void update();
    void draw(Graphics g);
    void onKeyPress(KeyEvent e);
}