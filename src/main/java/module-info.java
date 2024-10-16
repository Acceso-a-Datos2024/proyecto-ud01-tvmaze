module edu.badpals.controlador {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.badpals.controlador to javafx.fxml;
    exports edu.badpals.controlador;
    exports edu.badpals;
    opens edu.badpals to javafx.fxml;
}