package com.frostholl.routingproject.models;

import java.util.ArrayList;
import java.util.LinkedList;

public class BusRoute {
    private String id;

    private ArrayList<String> route;

    private LinkedList<Joint> fullRoute;

    public BusRoute(String id, ArrayList<String> route) {
        this.id = id;
        this.route = route;
    }

    public boolean containsInRoute(Joint a, Joint b) {
        return fullRoute.contains(a) && fullRoute.contains(b);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<String> route) {
        this.route = route;
    }

    public LinkedList<Joint> getFullRoute() {
        return fullRoute;
    }

    public void setFullRoute(LinkedList<Joint> fullRoute) {
        this.fullRoute = fullRoute;
    }
}
