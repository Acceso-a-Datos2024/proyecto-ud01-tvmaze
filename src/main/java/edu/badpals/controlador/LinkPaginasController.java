package edu.badpals.controlador;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LinkPaginasController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void toEpisodios(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("episodios.fxml"));
            loadScene(actionEvent, fxmlLoader);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void toSerie(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serie.fxml"));
            loadScene(actionEvent, fxmlLoader);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void toCast(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cast.fxml"));
            loadScene(actionEvent, fxmlLoader);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void loadScene(ActionEvent actionEvent, FXMLLoader fxmlLoader) throws IOException {
        Scene scene = new Scene(fxmlLoader.load(),1600,900);

        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }
}
