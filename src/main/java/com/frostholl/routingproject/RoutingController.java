package com.frostholl.routingproject;

import com.frostholl.routingproject.interfaces.InitEventListener;
import com.frostholl.routingproject.models.BusRoute;
import com.frostholl.routingproject.models.BusStop;
import com.frostholl.routingproject.models.House;
import com.frostholl.routingproject.models.Joint;
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
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class RoutingController implements Initializable, InitEventListener {

    private HashMap<String, House> houses = new HashMap<>();

    private HashMap<String, Joint> joints = new HashMap<>();

    private HashMap<String, BusStop> busStops = new HashMap<>();

    private List<BusRoute> busRoutes = new ArrayList<>();

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
    private Label autoPathInfo;

    @FXML
    private Label busPathInfo;

    @FXML
    protected void onHouseClicked(MouseEvent mouseEvent) {
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
    protected void onBusStopClicked(MouseEvent mouseEvent) {
        var b = busStops.getOrDefault(((Rectangle)mouseEvent.getSource()).getId(), null);
        if (b != null) {
            String sb = "Информация о остановке:\n" +
                    b.getName();
            InfoBoxController.display(sb);
        }
        else
            System.out.println(mouseEvent.getSource());
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
        var path = PathController.findShortestPath(currentStart, currentEnd);
        PathController.drawPath(map, path);
        autoPathInfo.setText(String.format("Длина пути: %.0fм", PathController.getDistance(path)));
        errorMessage.setText("");
    }

    @FXML
    protected void drawPathBus() {
        validateStartPoint();
        validateEndPoint();
        if (currentStart == null || currentEnd == null) {
            return;
        }
        if (currentStart == currentEnd) {
            errorMessage.setText("Точки отправления и прибытия совпадают!");
            return;
        }
        var pathNodesA = PathController.findShortestPaths(currentStart);
        var pathNodesB = PathController.findShortestPaths(currentEnd);
        BusRoute optimalRoute = null;
        Joint optimalBusStopA = null;
        Joint optimalBusStopB = null;
        double minimalDistA = 0d;
        double minimalDistB = 0d;
        while (optimalRoute == null) {
            PathNode nearestBusStopA = null;
            PathNode nearestBusStopB = null;
            for (var bs: busStops.values()) {
                Joint j = joints.get(bs.getNearestJointId());
                if (pathNodesA.get(j.getId()).getDistance() > minimalDistA) {
                    if (nearestBusStopA == null) nearestBusStopA = pathNodesA.get(j.getId());
                    else if (pathNodesA.get(j.getId()).getDistance() < nearestBusStopA.getDistance()) {
                        nearestBusStopA = pathNodesA.get(j.getId());
                    }
                }
                if (pathNodesB.get(j.getId()).getDistance() > minimalDistB) {
                    if (nearestBusStopB == null) nearestBusStopB = pathNodesB.get(j.getId());
                    else if (pathNodesB.get(j.getId()).getDistance() < nearestBusStopB.getDistance()) {
                        nearestBusStopB = pathNodesB.get(j.getId());
                    }
                }
            }
            if (nearestBusStopA == null || nearestBusStopB == null) break;
            for (var br: busRoutes) {
                if (br.containsInRoute(nearestBusStopA.getJoint(), nearestBusStopB.getJoint())) {
                    optimalRoute = br;
                }
            }
            if (optimalRoute == null) {
                if (nearestBusStopA.getDistance() > nearestBusStopB.getDistance())
                    minimalDistB = nearestBusStopB.getDistance();
                else
                    minimalDistA = nearestBusStopA.getDistance();
            }
            else {
                optimalBusStopA = nearestBusStopA.getJoint();
                optimalBusStopB = nearestBusStopB.getJoint();
            }
        }
        if (optimalRoute == null) {
            errorMessage.setText("Маршрут не найден!");
            return;
        }
        List<Joint> fullRoute = optimalRoute.getFullRoute();
        var pathA = PathController.getShortestPath(pathNodesA, optimalBusStopA);
        var pathB = PathController.getShortestPath(pathNodesB, optimalBusStopB);
        int BusStopIndexA = fullRoute.indexOf(optimalBusStopA);
        int BusStopIndexB = fullRoute.indexOf(optimalBusStopB);
        if (BusStopIndexA < BusStopIndexB)
            fullRoute = fullRoute.subList(BusStopIndexA, BusStopIndexB + 1);
        else
            fullRoute = fullRoute.subList(BusStopIndexB, BusStopIndexA + 1);
        PathController.setCurrentPathColor(new Color(0, 0, 1, 1));
        PathController.drawPath(map, fullRoute);
        PathController.overrideLast = false;
        PathController.setCurrentPathColor(new Color(1, 0, 0, 1));
        PathController.drawPath(map, pathA);
        PathController.setCurrentPathColor(new Color(1, 0, 0, 1));
        PathController.drawPath(map, pathB);
        PathController.setCurrentPathColor(new Color(0, 0, 1, 1));
        PathController.overrideLast = true;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Расстояние до остановки: %.0fм\n", PathController.getDistance(pathA)));
        sb.append(String.format("В пути на маршруте \"%s\": %.0fм\n", optimalRoute.getId(), PathController.getDistance(fullRoute)));
        sb.append(String.format("Расстояние от остановки: %.0fм\n", PathController.getDistance(pathB)));
        busPathInfo.setText(sb.toString());
    }

    public void onInit(List<House> houses, List<Joint> joints, List<BusStop> busStops, List<BusRoute> busRoutes) {
        initHouses(houses);
        initJoints(joints);
        initBusStops(busStops);
        PathController.init(this.joints);
        initBusRoutes(busRoutes);
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
            rect.setOnMouseClicked(this::onHouseClicked);
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

    private void initBusStops(List<BusStop> busStops) {
        for (var b: busStops) {
            this.busStops.put(b.getId(), b);
            Rectangle rect = new Rectangle();
            rect.setLayoutX(b.getLayoutX());
            rect.setLayoutY(b.getLayoutY());
            rect.setWidth(b.getWidth());
            rect.setHeight(b.getHeight());
            rect.setId(b.getId());
            rect.setOpacity(0d);
            rect.setOnMouseClicked(this::onBusStopClicked);
            map.getChildren().add(rect);
        }
    }

    private void initBusRoutes(List<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
        for (var br: busRoutes) {
            LinkedList<Joint> fullPath = new LinkedList<>();
            ArrayList<Joint> routeJoints = new ArrayList<>();
            for (var bs: br.getRoute()) {
                routeJoints.add(joints.get(busStops.get(bs).getNearestJointId()));
            }
            fullPath.add(routeJoints.get(0));
            for (int i = 0; i < routeJoints.size() - 1; i++) {
                var currentPath = PathController.findShortestPath(routeJoints.get(i), routeJoints.get(i + 1));
                fullPath.addAll(currentPath.subList(1, currentPath.size()));
            }
            br.setFullRoute(fullPath);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RoutingApplication.addInitEventListener(this);
    }
}