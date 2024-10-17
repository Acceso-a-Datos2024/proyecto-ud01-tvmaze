package edu.badpals.controlador;

import edu.badpals.modelo.Episodio;
import edu.badpals.modelo.Serie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EpisodiosController implements Initializable {
    private Conexion conexion = new Conexion();
    private Serie serie;

    @FXML
    private ListView<String> listViewEpisodios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialization logic if needed
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
        System.out.println(conexion.getEpisodios(serie.getId()));
    }

    private void cargarEpisodios() {
        setSerie(JSONHandler.cargarSerie());
    }

    public void toSerie(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("episodios.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        EpisodiosController controller = fxmlLoader.getController();
        controller.setSerie(this.serie);
    }
}