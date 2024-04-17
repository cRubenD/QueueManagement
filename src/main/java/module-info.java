module com.example.GraphicalUserInterface {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.GraphicalUserInterface to javafx.fxml;
    exports com.example.GraphicalUserInterface;
}