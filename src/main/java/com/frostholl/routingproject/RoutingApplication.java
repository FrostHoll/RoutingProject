package com.frostholl.routingproject;

import com.frostholl.routingproject.interfaces.InitEventListener;
import com.frostholl.routingproject.models.BusRoute;
import com.frostholl.routingproject.models.BusStop;
import com.frostholl.routingproject.models.House;
import com.frostholl.routingproject.models.Joint;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoutingApplication extends Application {
    private static ArrayList<InitEventListener> initEventListeners = new ArrayList<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RoutingApplication.class.getResource("map-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        stage.setTitle("Маршрутизация");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        String mapDataPath = "src/main/resources/com/frostholl/routingproject/mapData/";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type housesListToken = new TypeToken<Collection<House> >(){}.getType();
        Type jointsListToken = new TypeToken<Collection<Joint> >(){}.getType();
        Type busStopsListToken = new TypeToken<Collection<BusStop> >(){}.getType();
        Type busRoutesListToken = new TypeToken<Collection<BusRoute> >(){}.getType();
        List<House> deserializedHouses = gson.fromJson(new FileReader(mapDataPath + "houses.json"), housesListToken);
        List<Joint> deserializedJoints = gson.fromJson(new FileReader(mapDataPath + "joints.json"), jointsListToken);
        List<BusStop> deserializedBusStops = gson.fromJson(new FileReader(mapDataPath + "busStops.json"), busStopsListToken);
        List<BusRoute> deserializedBusRoutes = gson.fromJson(new FileReader(mapDataPath + "busRoutes.json"), busRoutesListToken);
        invokeInit(deserializedHouses, deserializedJoints, deserializedBusStops, deserializedBusRoutes);
    }

    public static void main(String[] args) {
        launch();
    }

    public static void addInitEventListener(InitEventListener listener) {
        initEventListeners.add(listener);
    }

    public static void invokeInit(List<House> houses, List<Joint> joints, List<BusStop> busStops, List<BusRoute> busRoutes) {
        for (var l: initEventListeners) {
            l.onInit(houses, joints, busStops, busRoutes);
        }
    }
}