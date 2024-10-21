package edu.badpals.controlador;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.badpals.modelo.Episodio;
import edu.badpals.modelo.Serie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.badpals.controlador.JSONHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExportacionController implements Initializable {
    private final String ruta = "data/episodios.xml";

    List<Episodio> episodios;

    @FXML
    private TextField txtRuta;

    @FXML
    private javafx.scene.control.Label lblNameSerieEpisodios;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (new File(ruta).exists()){
            setEpisodios(JSONHandler.fileToEpisodios(new File(ruta)));
        }
    }

    public void toXML(ActionEvent actionEvent){
        try {
            File outputFile = new File("data/exportaciones/" + txtRuta.getText() + ".xml");
            if(txtRuta.getText().contains(".")){
                this.txtRuta.setText("Sin extension PALETO");
                return;
            }
            if (outputFile.isDirectory()) {
                this.txtRuta.setText("The specified path is a directory, not a file: " + txtRuta.getText());
                return;
            }

            if (!outputFile.getParentFile().canWrite()) {
                this.txtRuta.setText("Cannot write to the specified directory: " + outputFile.getParent());
                return;
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.episodiosToXML(this.episodios)), new StreamResult(outputFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
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
        controller.setCampos();
    }
}
