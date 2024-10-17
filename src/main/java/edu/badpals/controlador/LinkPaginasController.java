package edu.badpals.controlador;

import edu.badpals.modelo.Schedule;
import edu.badpals.modelo.Serie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class LinkPaginasController implements Initializable {
    private Conexion conexion = new Conexion();
    private JSONHandler jsonHandler = new JSONHandler();
    private Serie serie;

    @FXML
    private TextField txtBuscarSerie;

    @FXML
    private Label lblIdiomaResult;

    @FXML
    private Label lblGeneroResult;

    @FXML
    private Label lblEstatusResult;

    @FXML
    private Label lblFechaInicioResult;

    @FXML
    private Label lblCalificacionResult;

    @FXML
    private Label lblHorarioResult;

    @FXML
    ImageView imgSerie;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void buscarSerie(){
        this.setSerie(jsonHandler.toSerie(conexion.getSerie(txtBuscarSerie.getText())));
        if (this.serie == null) {
            showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
        } else {
            this.setCampos();
        }
    }

    public void setCampos(){
        try {
            Schedule schedule = serie.getSchedule();
            lblIdiomaResult.setText(this.serie.getLanguage());
            lblGeneroResult.setText(String.join(", ", this.serie.getGenres()));
            lblEstatusResult.setText(this.serie.getStatus());
            lblFechaInicioResult.setText(this.serie.getPremiered());
            lblCalificacionResult.setText(this.serie.getRating().toString());
            lblHorarioResult.setText(schedule.getTime() + " " + String.join(", ", schedule.getDays()));
            imgSerie.setImage(new Image(this.serie.getImage().getMedium(),true));
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Image Load Error", "Failed to load image from URL: " + this.serie.getImage());

        }


    }

    public void toEpisodios(ActionEvent actionEvent){
        try {
            if (this.serie == null) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("episodios.fxml"));
                loadScene(actionEvent, fxmlLoader);
            }

        }catch (Exception e){
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
            if (this.serie == null) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cast.fxml"));
                loadScene(actionEvent, fxmlLoader);
            }

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

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
