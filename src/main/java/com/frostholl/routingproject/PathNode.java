package com.frostholl.routingproject;

import com.frostholl.routingproject.models.Joint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PathNode {
    private Joint joint;

    private List<PathNode> shortestPath = new LinkedList<>();

    private Double distance = Double.MAX_VALUE;

    private Map<PathNode, Double> adjacentNodes = new HashMap<>();

    public PathNode(Joint joint) {
        this.joint = joint;
    }

    public void addAdjacent(PathNode node) {
        adjacentNodes.put(node, Joint.getDistance(joint, node.joint));
    }

    public Joint getJoint() {
        return joint;
    }

    public List<PathNode> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<PathNode> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Map<PathNode, Double> getAdjacentNodes() {
        return adjacentNodes;
    }
}
