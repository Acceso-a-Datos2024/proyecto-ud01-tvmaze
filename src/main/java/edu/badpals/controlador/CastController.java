package edu.badpals.controlador;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.badpals.modelo.JSONHandler;
import edu.badpals.modelo.Serie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CastController implements Initializable {
    private final Conexion conexion = new Conexion(); // Conexión a la base de datos y API
    private Serie serie; // Objeto Serie actual
    private final JSONHandler jsonHandler = new JSONHandler(); // Manejador de JSON

    @FXML
    private ListView<String> listViewCast; // Vista de lista para mostrar el elenco

    @FXML
    private Label lblNameSerieCast; // Etiqueta para mostrar el nombre de la serie

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarSerie(); // Cargar la serie al inicializar
    }

    private void cargarSerie() {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            setSerie(xmlMapper.readValue(new File("data/Serie.xml"), Serie.class)); // Leer la serie desde un archivo XML
        } catch (Exception e) {
            System.out.println("Error al cargar la serie en castController");
        }
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
        lblNameSerieCast.setText(serie.getName().toUpperCase()); // Establecer el nombre de la serie en la etiqueta
        cargarActores(); // Cargar los actores de la serie
    }

    private void cargarActores() {
        try {
            List<String> actoresFromJSON = conexion.getCast(serie.getId()); // Obtener el elenco desde la API
            saveXML(actoresFromJSON); // Guardar el elenco en un archivo XML
            ObservableList<String> actoresList = FXCollections.observableArrayList(actoresFromJSON);
            listViewCast.setItems(actoresList); // Establecer la lista de actores en la vista de lista

        } catch (Exception e) {
            System.out.println("Error al cargar los actores en castController");
        }
    }

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

            LinkPaginasController controller = fxmlLoader.getController();
            controller.setCampos(); // Establecer campos en la nueva vista
        } catch (IOException e) {
            System.out.println("Error al cargar la ventana de serie en castController");
        }
    }

    private void saveXML(List<String> actores) {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            xmlMapper.writeValue(new File(dataDir, "/actores.xml"), actores); // Guardar el elenco en un archivo XML
        } catch (Exception e) {
            System.out.println("Error al guardar el xml en CastController");
        }
    }
}