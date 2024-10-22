package edu.badpals.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.badpals.modelo.Episodio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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

public class ExportacionController implements Initializable {
    private final String ruta = "data/episodios.xml"; // Ruta del archivo XML de episodios

    List<Episodio> episodios; // Lista de episodios

    @FXML
    private TextField txtRuta; // Campo de texto para la ruta de exportación

    @FXML
    private javafx.scene.control.Label lblNameSerieEpisodios; // Etiqueta para el nombre de la serie de episodios

    // Método de inicialización
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (new File(ruta).exists()){
            setEpisodios(JSONHandler.fileToEpisodios(new File(ruta))); // Cargar episodios desde el archivo XML
        }
    }

    // Método auxiliar para preparar el directorio de exportación y validar la ruta
    private boolean prepareExportDirectory(String subDir, String extension) {
        File exportaciones = new File("data/exportaciones");
        File subDirectory = new File("data/exportaciones/" + subDir);
        if (!exportaciones.exists()) {
            exportaciones.mkdir(); // Crear directorio de exportaciones si no existe
        }
        if (!subDirectory.exists()) {
            subDirectory.mkdir(); // Crear subdirectorio si no existe
        }
        if (txtRuta.getText().contains(".")) {
            this.txtRuta.setText("Sin extension PALETO");
            return false;
        }
        File outputFile = new File(subDirectory, txtRuta.getText() + extension);
        if (outputFile.isDirectory()) {
            this.txtRuta.setText("The specified path is a directory, not a file: " + txtRuta.getText());
            return false;
        }
        if (!outputFile.getParentFile().canWrite()) {
            this.txtRuta.setText("Cannot write to the specified directory: " + outputFile.getParent());
            return false;
        }
        return true;
    }

    // Método para exportar episodios a XML
    public void toXML(ActionEvent actionEvent) {
        if (!prepareExportDirectory("XML", ".xml")) return;
        try {
            File outputFile = new File("data/exportaciones/XML/" + txtRuta.getText() + ".xml");
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.episodiosToXML(this.episodios)), new StreamResult(outputFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para exportar episodios a TXT
    public void toTXT(ActionEvent actionEvent) {
        if (!prepareExportDirectory("TXT", ".txt")) return;
        try {
            File outputFile = new File("data/exportaciones/TXT/" + txtRuta.getText() + ".txt");
            for (Episodio episodio : this.episodios) {
                JSONHandler.EscritorFile(episodio.toString(), outputFile); // Escribir episodios en archivo TXT
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para exportar episodios a JSON
    public void toJSON(ActionEvent actionEvent) {
        if (!prepareExportDirectory("JSON", ".json")) return;
        try {
            File outputFile = new File("data/exportaciones/JSON/" + txtRuta.getText() + ".json");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(outputFile, this.episodios); // Escribir episodios en archivo JSON
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para exportar episodios a BIN
    public void toBIN(ActionEvent actionEvent) {
        if (!prepareExportDirectory("BIN", ".bin")) return;
        try {
            File outputFile = new File("data/exportaciones/BIN/" + txtRuta.getText() + ".bin");
            for (Episodio episodio : this.episodios) {
                JSONHandler.EscritorObjects(episodio, outputFile); // Escribir episodios en archivo BIN
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para establecer la lista de episodios
    public void setEpisodios(List<Episodio> episodios) {
        this.episodios = episodios;
    }

    // Método para cambiar a la vista de series
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
}