package com.frostholl.routingproject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class InfoBoxController {

    private FXMLLoader fxmlLoader;

    @FXML
    private Label messageText;

    @FXML
    private Button closeButton;

    @FXML
    private void onCloseButton() {
        ((Stage)closeButton.getScene().getWindow()).close();
    }

    public static void display(String message) {
        FXMLLoader fxmlLoader = new FXMLLoader(RoutingApplication.class.getResource("infoBox.fxml"));
        Scene scene;
        Parent layout;
        try {
            layout = fxmlLoader.load();
        }
        catch (IOException e) {
            System.out.println("Scene file was not found!");
            return;
        }
        Label l = (Label)layout.lookup("#messageText");
        if (l != null) {
            l.setText(message);
        }
        scene = new Scene(layout, 260, 120);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Информация");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
