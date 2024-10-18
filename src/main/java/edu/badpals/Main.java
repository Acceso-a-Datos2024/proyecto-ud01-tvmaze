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

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("controlador/serie.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        if (mostrarConfirmacion()){
            LinkPaginasController controller = fxmlLoader.getController();
            controller.setCampos();
        }

    }

    private boolean mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cargar Busqueda Pasada");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea restaurar la busqueda anterior?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void main(String[] args) {
        launch();
    }
}
