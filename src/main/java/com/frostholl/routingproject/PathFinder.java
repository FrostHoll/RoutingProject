package com.frostholl.routingproject;

import com.frostholl.routingproject.models.Joint;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.*;

public class PathFinder {

    private static HashMap<String, Joint> joints;

    private static ArrayList<Line> lastPath;

    public static void init(HashMap<String, Joint> js) {
        joints = js;
        lastPath = new ArrayList<>();
    }

    public static void drawPath(Pane map, Joint A, Joint B) {
        if (!lastPath.isEmpty()) {
            for (var line: lastPath) {
                map.getChildren().remove(line);
            }
            lastPath.clear();
        }
        List<Joint> path = findPath(A, B);
        for (int i = 0; i < path.size() - 1; i++) {
            Line line = new Line(path.get(i).getLayoutX(), path.get(i).getLayoutY(),
                    path.get(i + 1).getLayoutX(), path.get(i + 1).getLayoutY());
            line.setStroke(new Color(0, 0, 1, 1));
            line.setStrokeWidth(5);
            map.getChildren().add(line);
            lastPath.add(line);
        }
    }

    public static List<Joint> findPath(Joint A, Joint B) {
        Map<String, PathNode> pathNodes = new HashMap<>();
        PathNode start = new PathNode(A);
        PathNode end = new PathNode(B);
        pathNodes.put(A.getId(), start);
        pathNodes.put(B.getId(), end);
        for (var j: joints.values()) {
            PathNode currentNode;
            if (!pathNodes.containsKey(j.getId())) {
                currentNode = new PathNode(j);
                pathNodes.put(j.getId(), currentNode);
            }
            else currentNode = pathNodes.get(j.getId());
            for (var jj: j.getNeighbors()) {
                PathNode adjacentNode;
                if (!pathNodes.containsKey(jj.getId())) {
                    adjacentNode = new PathNode(jj);
                    pathNodes.put(jj.getId(), adjacentNode);
                }
                else adjacentNode = pathNodes.get(jj.getId());
                currentNode.addAdjacent(adjacentNode);
            }
        }
        calculateShortestPath(start);
        List<Joint> shortestPath = new LinkedList<>();
        for (var n: end.getShortestPath()) {
            shortestPath.add(n.getJoint());
        }
        shortestPath.add(B);
        return shortestPath;
    }

    private static void calculateShortestPath(PathNode source) {
        source.setDistance(0d);
        Set<PathNode> settledNodes = new HashSet<>();
        Set<PathNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (!unsettledNodes.isEmpty()) {
            PathNode currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry<PathNode, Double> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {
                PathNode adjacentNode = adjacencyPair.getKey();
                double edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
    }

    private static PathNode getLowestDistanceNode(Set<PathNode> unsettledNodes) {
        PathNode lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;

        for (PathNode node: unsettledNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(PathNode evalNode, double edgeWeight, PathNode sourceNode) {
        double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeight < evalNode.getDistance()) {
            evalNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<PathNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evalNode.setShortestPath(shortestPath);
        }
    }

}
