package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        GamePanel panel = new GamePanel();
        root.setCenter(panel);

        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.BLACK); // fixes screenshake white showing

        stage.setTitle("HTRPG Minigame");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        panel.requestFocus(); //key presses actually reach the canvas
        panel.startGameLoop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}