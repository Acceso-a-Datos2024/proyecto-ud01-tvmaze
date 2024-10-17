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
import org.w3c.dom.Document;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EpisodiosController implements Initializable {
    private Conexion conexion = new Conexion();
    private Serie serie;
    private JSONHandler jsonHandler = new JSONHandler();

    @FXML
    private ListView<String> listViewEpisodios;

    @FXML
    private javafx.scene.control.Label lblNameSerieEpisodios; // Para mostrar el nombre de la serie

    @FXML
    private javafx.scene.image.ImageView imgEpisodio; // Si quieres mostrar una imagen de la serie

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarSerie();
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

    public void setSerie(Serie serie) {
        this.serie = serie;
        lblNameSerieEpisodios.setText(serie.getName().toUpperCase()); // Muestra el nombre de la serie
        cargarEpisodios(); // Cargar episodios al establecer la serie
    }

    private void cargarEpisodios() {
        try {
            // Obtener el JSON de episodios desde la conexión
            String jsonEpisodios = conexion.getEpisodios(serie.getId());


            // Convertir el JSON a una lista de objetos Episodio utilizando el JSONHandler
            List<Episodio> episodios = jsonHandler.JSONtoEpisodios(jsonEpisodios);

            // Crear un documento XML a partir de la lista de episodios
            Document xmlEpisodios = jsonHandler.episodiosToXML(episodios);

            // Guardar el XML a un archivo
            File xmlFile = new File("data/episodios.xml");
            jsonHandler.saveXMLToFile(xmlEpisodios, xmlFile);

            // Cargar los episodios desde el archivo XML
            List<Episodio> episodiosFromXML = jsonHandler.fromXMLtoEpisodios(xmlFile);

            // Crear una lista observable para el ListView
            ObservableList<String> episodiosList = FXCollections.observableArrayList();

            // Recorrer la lista de episodios y añadir el texto formateado
            for (Episodio episodio : episodiosFromXML) {
                String textoEpisodio = "Temporada " + episodio.getSeason() + ", Episodio " + episodio.getNumber() + ", " + episodio.getName();
                episodiosList.add(textoEpisodio);
            }

            // Establecer la lista en el ListView
            listViewEpisodios.setItems(episodiosList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void toSerie(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serie.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();

        LinkPaginasController controller = fxmlLoader.getController();
        controller.setCampos();
    }
}
