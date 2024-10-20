package edu.badpals;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("controlador/login.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 500, 600);

        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();


    }
}
