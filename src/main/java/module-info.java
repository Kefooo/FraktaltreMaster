module com.example.fraktaltremaster {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.fraktaltremaster to javafx.fxml;
    exports com.example.fraktaltremaster;
}