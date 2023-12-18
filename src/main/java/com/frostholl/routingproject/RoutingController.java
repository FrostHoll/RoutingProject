package com.frostholl.routingproject;

import com.frostholl.routingproject.interfaces.InitEventListener;
import com.frostholl.routingproject.models.House;
import com.frostholl.routingproject.models.Joint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    private List<House> housesList;

    private Joint currentStart = null;

    private Joint currentEnd = null;

    private ChooseState chooseState = ChooseState.NONE;

    @FXML
    private Pane map;

    @FXML
    private Button chooseStartHouseButton;

    @FXML
    private Button chooseEndHouseButton;

    @FXML
    private TextField startPointText;

    @FXML
    private TextField endPointText;

    @FXML
    private Label errorMessage;

    @FXML
    protected void onHouseClick(MouseEvent mouseEvent) {
        var h = houses.getOrDefault(((Rectangle)mouseEvent.getSource()).getId(), null);
        if (h != null) {
            switch (chooseState) {
                case START -> {
                    startPointText.setText(h.getStreetName() + ", " + h.getNumber());
                    chooseStartHouseButton.setText("Выбрать");
                    chooseState = ChooseState.NONE;
                    map.setCursor(Cursor.DEFAULT);
                }
                case END -> {
                    endPointText.setText(h.getStreetName() + ", " + h.getNumber());
                    chooseEndHouseButton.setText("Выбрать");
                    chooseState = ChooseState.NONE;
                    map.setCursor(Cursor.DEFAULT);
                }
                case NONE -> {
                    String sb = "Информация о доме:\n" +
                            h.getStreetName() +
                            ", " +
                            h.getNumber();
                    InfoBoxController.display(sb);
                }
            }
        }
        else {
            System.out.println(mouseEvent.getSource());
        }
    }

    @FXML
    protected void onChooseStartHouseClicked() {
        if (chooseState == ChooseState.START) {
            chooseStartHouseButton.setText("Выбрать");
            chooseState = ChooseState.NONE;
            map.setCursor(Cursor.DEFAULT);
            return;
        }
        if (chooseState == ChooseState.END) {
            chooseEndHouseButton.setText("Выбрать");
        }
        chooseState = ChooseState.START;
        chooseStartHouseButton.setText("Отмена");
        map.setCursor(Cursor.CROSSHAIR);
    }

    @FXML
    protected void onChooseEndHouseClicked() {
        if (chooseState == ChooseState.END) {
            chooseEndHouseButton.setText("Выбрать");
            chooseState = ChooseState.NONE;
            map.setCursor(Cursor.DEFAULT);
            return;
        }
        if (chooseState == ChooseState.START) {
            chooseStartHouseButton.setText("Выбрать");
        }
        chooseState = ChooseState.END;
        chooseEndHouseButton.setText("Отмена");
        map.setCursor(Cursor.CROSSHAIR);
    }

    @FXML
    protected void validateStartPoint() {
        var house = getHouseFromText(startPointText.getText());
        if (house == null) {
            currentStart = null;
            errorMessage.setText("Неправильно указана точка отправления!");
            return;
        }
        currentStart = joints.get(house.getNearestJointId());
        errorMessage.setText("");
    }

    @FXML
    protected void validateEndPoint() {
        var house = getHouseFromText(endPointText.getText());
        if (house == null) {
            currentEnd = null;
            errorMessage.setText("Неправильно указана точка прибытия!");
            return;
        }
        currentEnd = joints.get(house.getNearestJointId());
        errorMessage.setText("");
    }

    private House getHouseFromText(String text) {
        if (!text.contains(",")) return null;
        var arr = text.split(",");
        if (arr.length != 2) return null;
        String street = arr[0].trim();
        String number = arr[1].trim();
        var house = housesList.stream().filter(h -> Objects.equals(h.getStreetName(), street) && Objects.equals(h.getNumber(), number)).findFirst();
        return house.orElse(null);
    }

    @FXML
    protected void drawPath() {
        validateStartPoint();
        validateEndPoint();
        if (currentStart == null || currentEnd == null) {
            return;
        }
        if (currentStart == currentEnd) {
            errorMessage.setText("Точки отправления и прибытия совпадают!");
            return;
        }
        PathFinder.drawPath(map, currentStart, currentEnd);

        errorMessage.setText("");
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
        housesList = houses;
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
            circle.setOpacity(0d);
            Label label = new Label(j.getId());
            label.setLayoutX(j.getLayoutX() - 3);
            label.setLayoutY(j.getLayoutY() + 8);
            label.setTextFill(new Color(1, 0, 0, 1));
            label.setOpacity(0d);
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