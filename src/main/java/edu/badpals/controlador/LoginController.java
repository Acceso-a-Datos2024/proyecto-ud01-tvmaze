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
    TextField txtFieldUser;

    @FXML
    TextField txtFieldPwd;


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

            if (LinkPaginasController.mostrarConfirmacion()){
                LinkPaginasController controller = fxmlLoader.getController();
                controller.setCampos();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceder(ActionEvent actionEvent) {
        if (Objects.equals(JSONHandler.leerUsers().get(this.txtFieldUser.getText()), this.txtFieldPwd.getText())) {
            toSerie(actionEvent);
        } else {
            LinkPaginasController.showWarning("Aceso Denegado", "El usuario o a contraseña es incorrecto");
        }

    }


}
