module org.example.lab7 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens org.example.lab7 to javafx.fxml;
    opens org.example.lab7.domain;
    opens org.example.lab7.controller to javafx.fxml;
    exports org.example.lab7;
    exports org.example.lab7.controller to javafx.fxml;
}