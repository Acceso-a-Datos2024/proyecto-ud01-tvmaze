module edu.badpals.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens edu.badpals.demo to javafx.fxml;
    exports edu.badpals.demo;
}