package edu.badpals.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {

    @FXML
    TextField txtFieldUser; // Campo de texto para el nombre de usuario

    @FXML
    TextField txtFieldPwd; // Campo de texto para la contraseña

    // Método para cambiar a la vista de series
    public void toSerie(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serie.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(false);
            stage.setResizable(false);
            stage.setTitle("Series");

            // Mostrar confirmación y establecer campos si es necesario
            if (LinkPaginasController.mostrarConfirmacion()) {
                LinkPaginasController controller = fxmlLoader.getController();
                controller.setCampos();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para registrar un nuevo usuario
    public void registrarUser() {
        JSONHandler.crearUser(this.txtFieldUser.getText(), this.txtFieldPwd.getText());
    }

    // Método para acceder a la aplicación
    public void acceder(ActionEvent actionEvent) {
        // Verificar si el usuario y la contraseña son correctos
        if (Objects.equals(JSONHandler.leerUsers().get(this.txtFieldUser.getText()), this.txtFieldPwd.getText())) {
            toSerie(actionEvent); // Cambiar a la vista de series si las credenciales son correctas
        } else {
            LinkPaginasController.showWarning("Acceso Denegado", "El usuario o la contraseña es incorrecto");
        }
    }
}