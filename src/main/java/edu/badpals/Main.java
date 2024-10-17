package edu.badpals;

import edu.badpals.controlador.Conexion;
import edu.badpals.controlador.JSONHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Conexion conexion = new Conexion();
        JSONHandler jsonHandler = new JSONHandler();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("controlador/serie.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
