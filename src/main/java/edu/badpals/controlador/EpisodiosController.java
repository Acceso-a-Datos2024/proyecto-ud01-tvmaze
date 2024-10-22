package edu.badpals.controlador;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.badpals.modelo.Episodio;
import edu.badpals.modelo.Serie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EpisodiosController implements Initializable {
    private final Conexion conexion = new Conexion(); // Conexión a la base de datos
    private Serie serie; // Objeto Serie actual
    private final JSONHandler jsonHandler = new JSONHandler(); // Manejador de JSON

    @FXML
    private ListView<String> listViewEpisodios; // Vista de lista para mostrar episodios

    @FXML
    private javafx.scene.control.Label lblNameSerieEpisodios; // Etiqueta para mostrar el nombre de la serie


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
            System.out.println("Error al cargar la ventana de serie en episodiosController");
        }
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
        lblNameSerieEpisodios.setText(serie.getName().toUpperCase()); // Establecer el nombre de la serie en la etiqueta
        cargarEpisodios(); // Cargar los episodios de la serie
    }

    private void cargarEpisodios() {
        try {
            List<Episodio> episodiosFromXML = conexion.getEpisodios(serie.getId()); // Obtener episodios desde la base de datos
            saveXML(episodiosFromXML); // Guardar episodios en un archivo XML
            ObservableList<String> episodiosList = FXCollections.observableArrayList();
            for (Episodio episodio : episodiosFromXML) {
                String textoEpisodio = "Temporada " + episodio.getSeason() + ", Episodio " + episodio.getNumber() + ", " + episodio.getName();
                episodiosList.add(textoEpisodio); // Agregar información del episodio a la lista observable
            }

            listViewEpisodios.setItems(episodiosList); // Establecer la lista de episodios en la vista de lista

        } catch (Exception e) {
            System.out.println("Error al cargar los episodios");
        }
    }

    public void toSerie(ActionEvent actionEvent) throws IOException {
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
    }

    public void toExportacion(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("exportacion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600 , 400);
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setTitle("Exportacion");
    }

    public void saveXML(List<Episodio> episodiosFromXML) {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs(); // Crear directorio de datos si no existe
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(JSONHandler.episodiosToXML(episodiosFromXML));
            StreamResult result = new StreamResult(new File(dataDir, "episodios.xml"));
            transformer.transform(source, result); // Transformar y guardar episodios en un archivo XML
        } catch (Exception e) {
            System.out.println("Error al guardar el episodios.xml");
        }
    }
}