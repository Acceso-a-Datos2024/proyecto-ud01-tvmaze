package edu.badpals.controlador;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.badpals.modelo.Schedule;
import edu.badpals.modelo.Serie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.stage.Stage;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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
        cargarSerie();
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void buscarSerie(){
        Serie serie = jsonHandler.JSONtoSerie(conexion.getSerie(txtBuscarSerie.getText()));

        if (serie.getId() == 0) {
            setSerie(serie);
            showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
        } else {
            guardarSerie(serie);
            this.setCampos();
        }
    }

    public void setCampos(){
        try {
            cargarSerie();
            Schedule schedule = serie.getSchedule();
            txtBuscarSerie.setText(this.serie.getName());
            lblIdiomaResult.setText(this.serie.getLanguage());
            lblGeneroResult.setText(String.join(", ", this.serie.getGenres()));
            lblEstatusResult.setText(this.serie.getStatus());
            lblFechaInicioResult.setText(this.serie.getPremiered());
            lblCalificacionResult.setText(this.serie.getRating().toString());
            lblHorarioResult.setText(schedule.getTime() + " " + String.join(", ", schedule.getDays()));
            imgSerie.setImage(new Image(this.serie.getImage().getMedium(),true));
            guardarSerie(this.serie);
        } catch (Exception e) {
            e.printStackTrace();
            showWarning("Image Load Error", "Failed to load image from URL: " + this.serie.getImage());

        }


    }

    public void toSerie(ActionEvent actionEvent){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serie.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            LinkPaginasController controller = fxmlLoader.getController();
            controller.setCampos();

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void toEpisodios(ActionEvent actionEvent){
        try {
            if (this.serie.getId() == 0) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("episodios.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
                Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                EpisodiosController controller = fxmlLoader.getController();
                controller.setSerie(this.serie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toCast(ActionEvent actionEvent){
        try{
            if (this.serie.getId() == 0) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cast.fxml"));
                loadScene(actionEvent, fxmlLoader);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void guardarSerie(Serie serie) {
        try {
            if (serie.getId() == 0){
                this.serie = null;
                return;
            }
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(jsonHandler.serieToXML(serie)), new StreamResult(new File(dataDir,"Serie.xml")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void cargarSerie() {
        try{
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            setSerie(xmlMapper.readValue(new File("data/Serie.xml"), Serie.class));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
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

    private boolean mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea cargar?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
