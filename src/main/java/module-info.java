module com.frostholl.routingproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.frostholl.routingproject to javafx.fxml;
    opens com.frostholl.routingproject.models to com.google.gson;
    exports com.frostholl.routingproject;
}