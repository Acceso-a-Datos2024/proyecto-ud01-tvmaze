module edu.badpals.controlador {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.badpals.controlador to javafx.fxml;
    exports edu.badpals.controlador;
}