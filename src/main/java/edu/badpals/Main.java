package edu.badpals;

import edu.badpals.controlador.Conexion;
import edu.badpals.controlador.JSONHandler;
import edu.badpals.controlador.LinkPaginasController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("controlador/login.fxml"));

        Scene scene = new Scene(fxmlLoader.load(),500,600);

        stage.setTitle("Login");

        stage.setScene(scene);
        stage.show();


    }



    public static void main(String[] args) {
        launch();
    }
}
