package com.frostholl.routingproject;

import com.frostholl.routingproject.interfaces.InitEventListener;
import com.frostholl.routingproject.models.House;
import com.frostholl.routingproject.models.Joint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

public class RoutingController implements Initializable, InitEventListener {

    private HashMap<String, House> houses = new HashMap<>();

    private HashMap<String, Joint> joints = new HashMap<>();

    @FXML
    private Pane map;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void onHouseClick(MouseEvent mouseEvent) {
        var h = houses.getOrDefault(((Rectangle)mouseEvent.getSource()).getId(), null);
        if (h != null) {
            String sb = "Информация о доме:\n" +
                    h.getStreetName() +
                    ", " +
                    h.getNumber();
            InfoBoxController.display(sb);
        }
        else {
            System.out.println(mouseEvent.getSource());
        }
    }

    @FXML
    protected void createJoint(MouseEvent mouseEvent) {

    }

    @FXML
    protected void drawPath() {
        String startJointId = "j67";
        Joint startJoint = joints.get(startJointId);
        String endJointId = "j52";
        Joint endJoint = joints.get(endJointId);
        Line path = new Line(startJoint.getLayoutX(), startJoint.getLayoutY(), endJoint.getLayoutX(), endJoint.getLayoutY());
        path.setStroke(new Color(0, 0, 1, 1));
        path.setStrokeWidth(5);
        map.getChildren().add(path);
    }

    @FXML
    protected void saveJoints() {
        String mapDataPath = "src/main/resources/com/frostholl/routingproject/mapData/";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type jointsListToken = new TypeToken<Collection<Joint>>(){}.getType();
        String json = gson.toJson(joints, jointsListToken);
        try {
            FileWriter fw = new FileWriter(mapDataPath + "joints.json", false);
            fw.append(json);
            fw.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onInit(List<House> houses, List<Joint> joints) {
        initHouses(houses);
        initJoints(joints);
        PathFinder.init(this.joints);
    }

    private void initHouses(List<House> houses) {
        for (var h: houses) {
            this.houses.put(h.getId(), h);
            Rectangle rect = new Rectangle();
            rect.setLayoutX(h.getLayoutX());
            rect.setLayoutY(h.getLayoutY());
            rect.setWidth(h.getWidth());
            rect.setHeight(h.getHeight());
            rect.setId(h.getId());
            rect.setOpacity(0d);
            rect.setOnMouseClicked(this::onHouseClick);
            map.getChildren().add(rect);
        }
    }

    private void initJoints(List<Joint> joints) {
        for (var j: joints) {
            this.joints.put(j.getId(), j);
            Circle circle = new Circle(j.getLayoutX(), j.getLayoutY(), 8);
            Label label = new Label(j.getId());
            label.setLayoutX(j.getLayoutX() - 3);
            label.setLayoutY(j.getLayoutY() + 8);
            label.setTextFill(new Color(1, 0, 0, 1));
            map.getChildren().addAll(circle, label);
        }
        for (var j: joints) {
            for (var n : j.getNeighborsIds())
            {
                j.addNeighbor(this.joints.get(n));
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RoutingApplication.addInitEventListener(this);
    }
}