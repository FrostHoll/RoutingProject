package com.frostholl.routingproject.models;

public class BusStop {
    private String id;

    private double layoutX;

    private double layoutY;

    private double width;

    private double height;

    private String name;

    private String nearestJointId;

    public BusStop(String id, double layoutX, double layoutY, double width, double height, String name, String nearestJointId) {
        this.id = id;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.width = width;
        this.height = height;
        this.name = name;
        this.nearestJointId = nearestJointId;
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

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNearestJointId() {
        return nearestJointId;
    }

    public void setNearestJointId(String nearestJointId) {
        this.nearestJointId = nearestJointId;
    }

    @Override
    public String toString() {
        return "BusStop{" +
                "id='" + id + '\'' +
                ", layoutX=" + layoutX +
                ", layoutY=" + layoutY +
                ", width=" + width +
                ", height=" + height +
                ", name='" + name + '\'' +
                ", nearestJointId='" + nearestJointId + '\'' +
                '}';
    }
}
