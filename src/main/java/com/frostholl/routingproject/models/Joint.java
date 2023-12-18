package com.frostholl.routingproject.models;

import java.util.ArrayList;

public class Joint {
    private String id;

    private double layoutX;

    private double layoutY;

    private ArrayList<String> neighborsIds;

    private ArrayList<Joint> neighbors;

    public static Double getDistance(Joint a, Joint b) {
        return Math.sqrt(Math.pow(a.layoutX - b.layoutX, 2) + Math.pow(a.layoutY - b.layoutY, 2));
    }

    public Joint(String id, double layoutX, double layoutY, ArrayList<String> neighborsIds) {
        this.id = id;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.neighborsIds = neighborsIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }

    public ArrayList<String> getNeighborsIds() {
        return neighborsIds;
    }

    public void setNeighborsIds(ArrayList<String> neighborsIds) {
        this.neighborsIds = neighborsIds;
    }

    public void addNeighbor(Joint joint) {
        if (neighbors == null) neighbors = new ArrayList<>();
        neighbors.add(joint);
    }

    public ArrayList<Joint> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return "Joint{" +
                "id='" + id + '\'' +
                ", layoutX=" + layoutX +
                ", layoutY=" + layoutY +
                ", neighborsIds=" + neighborsIds +
                '}';
    }
}
