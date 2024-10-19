package edu.badpals.controlador;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import javafx.stage.Stage;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LinkPaginasController implements Initializable {
    @FXML
    ImageView imgSerie;
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
    private ImageView imgLupa;
    @FXML
    private ImageView imgEpisodios;
    @FXML
    private ImageView imgComunidad;
    @FXML
    private ImageView imgLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imgEpisodios.setImage(new Image(getClass().getResource("/img/episodios.png").toExternalForm()));
        imgComunidad.setImage(new Image(getClass().getResource("/img/comunidad.png").toExternalForm()));
        imgLupa.setImage(new Image(getClass().getResource("/img/lupa.png").toExternalForm()));
        imgLogin.setImage(new Image(getClass().getResource("/img/login.png").toExternalForm()));
        cargarSerie();
    }

    private static Stage loadScene(ActionEvent actionEvent, FXMLLoader fxmlLoader) throws IOException {
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);

        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
        stage.setMaximized(false);
        stage.setResizable(false);

        return stage;
    }

    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean mostrarConfirmacion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cargar Busqueda Pasada");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea restaurar la busqueda anterior?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public void buscarSerie() {
        setSerie(conexion.getSerie(txtBuscarSerie.getText()));

        if (serie.getId() == 0) {
            showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
        } else {
            guardarSerie(serie);
            this.setCampos();
        }
    }

    public void setCampos() {
        try {
            cargarSerie();
            if (getSerie().getId() == 0) {
                return;
            }
            Schedule schedule = serie.getSchedule();
            txtBuscarSerie.setText(this.serie.getName());
            lblIdiomaResult.setText(this.serie.getLanguage());
            lblGeneroResult.setText(String.join(", ", this.serie.getGenres()));
            lblEstatusResult.setText(this.serie.getStatus());
            lblFechaInicioResult.setText(this.serie.getPremiered());
            lblCalificacionResult.setText(this.serie.getRating().toString());
            lblHorarioResult.setText(schedule.getTime() + " " + String.join(", ", schedule.getDays()));
            try {
                if (this.serie.getImage() != null || !this.serie.getImage().getMedium().isEmpty()) {
                    String imageUrl = this.serie.getImage().getMedium() != null ? this.serie.getImage().getMedium() : this.serie.getImage().getOriginal();

                    if (Conexion.isImageCache(serie.getId()).exists()) {
                        imgSerie.setImage(new Image(Conexion.isImageCache(serie.getId()).toURI().toString()));
                    } else {
                        imgSerie.setImage(new Image(imageUrl, true));
                    }
                }
            } catch (Exception e) {
                imgSerie.setImage(new Image(getClass().getResource("/img/imagenVacia.png").toExternalForm()));
            }
            guardarSerie(this.serie);
        } catch (Exception e) {
            showWarning("Image Load Error", "Failed to load image from URL: " + this.serie.getImage());

        }


    }

    public void toSerie(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("serie.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            LinkPaginasController controller = fxmlLoader.getController();
            controller.setCampos();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toEpisodios(ActionEvent actionEvent) {
        try {
            if (this.serie.getId() == 0) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("episodios.fxml"));
                Stage stage = loadScene(actionEvent, fxmlLoader);
                stage.setTitle("Episodios");
            }
        } catch (Exception e) {
            System.out.println("Error al ir a episodios.fxml");
        }
    }

    public void toCast(ActionEvent actionEvent) {
        try {
            if (this.serie.getId() == 0) {
                showWarning("Serie no encontrada", "No se encontró ninguna serie con el nombre proporcionado.");
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cast.fxml"));
                Stage stage = loadScene(actionEvent, fxmlLoader);
                stage.setTitle("Cast");
            }

        } catch (Exception e) {
            System.out.println("Error al ir a cast.fxml");
        }
    }

    public void toLogin(ActionEvent actionEvent) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 500, 600);

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            stage.setResizable(false);
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.show();


        } catch (Exception e) {
            System.out.println("Error al ir a login.fxml");
        }
    }

    private void guardarSerie(Serie serie) {
        try {
            if (serie.getId() == 0) {
                this.serie = null;
                return;
            }
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.serieToXML(serie)), new StreamResult(new File(dataDir, "Serie.xml")));
        } catch (Exception e) {
            System.out.println("Error al guardar Serie");
        }
    }

    private void cargarSerie() {
        File fileSerie = new File("data/Serie.xml");
        try {
            if (fileSerie.exists() && fileSerie.length() != 0) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                setSerie(xmlMapper.readValue(new File("data/Serie.xml"), Serie.class));
            } else {
                Serie serie = new Serie();
                serie.setId(0);
                setSerie(serie);
            }
        } catch (Exception e) {
            System.out.println("Error al cargar Serie");
        }
    }
}
