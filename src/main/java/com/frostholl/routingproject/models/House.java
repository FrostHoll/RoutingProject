package com.frostholl.routingproject.models;

import java.io.Serial;
import java.io.Serializable;

public class House {
    private String id;

    private double layoutX;

    private double layoutY;

    private double width;

    private double height;

    private String streetName;

    private String number;

    private String nearestJointId;

    public House(String id, double layoutX, double layoutY, double width, double height, String streetName, String number, String nearestJointId) {
        this.id = id;
        this.layoutX = layoutX;
        this.layoutY = layoutY;
        this.width = width;
        this.height = height;
        this.streetName = streetName;
        this.number = number;
        this.nearestJointId = nearestJointId;
    }

    public String getId() {
        return id;
    }

    public double getLayoutX() {
        return layoutX;
    }

    public double getLayoutY() {
        return layoutY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getNumber() {
        return number;
    }

    public String getNearestJointId() {
        return nearestJointId;
    }

    @Override
    public String toString() {
        return "House{" +
                "id='" + id + '\'' +
                ", layoutX=" + layoutX +
                ", layoutY=" + layoutY +
                ", width=" + width +
                ", height=" + height +
                ", streetName='" + streetName + '\'' +
                ", number='" + number + '\'' +
                ", nearestJointId='" + nearestJointId + '\'' +
                '}';
    }
}
