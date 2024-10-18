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
    private Conexion conexion = new Conexion();
    private Serie serie;
    private JSONHandler jsonHandler = new JSONHandler();

    @FXML
    private ListView<String> listViewEpisodios;

    @FXML
    private javafx.scene.control.Label lblNameSerieEpisodios;


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

            List<Episodio> episodiosFromXML = jsonHandler.JSONtoEpisodios(conexion.getEpisodios(serie.getId()));
            saveXML(episodiosFromXML);
            ObservableList<String> episodiosList = FXCollections.observableArrayList();
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

    public void saveXML(List<Episodio> episodiosFromXML) {
        try {

            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(jsonHandler.episodiosToXML(episodiosFromXML));
            StreamResult result = new StreamResult(new File(dataDir,"episodios.xml"));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
