module org.example.mysocialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;


    opens org.example.mysocialnetworkgui to javafx.fxml;
    exports org.example.mysocialnetworkgui;
}