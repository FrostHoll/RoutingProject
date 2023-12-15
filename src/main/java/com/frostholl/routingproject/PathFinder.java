package com.frostholl.routingproject;

import com.frostholl.routingproject.models.Joint;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;

public class PathFinder {

    private static HashMap<String, Joint> joints;

    private static ArrayList<Line> lastPath;

    public static void init(HashMap<String, Joint> js) {
        joints = js;
    }

    public static void drawPath(Pane map, Joint A, Joint B) {

    }

}
