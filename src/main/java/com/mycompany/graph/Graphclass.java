package com.mycompany.graph;

import java.util.*;

public class Graphclass {

    public Map<String, Map<String, Integer>> adjList;
    public ArrayList<String> path;

    public Graphclass() {
        this.adjList = new HashMap<>();
    }

    // arista
    public void addEdge(String from, String to, int weight) {
        adjList.putIfAbsent(from, new HashMap<>());
        adjList.putIfAbsent(to, new HashMap<>());
        adjList.get(from).put(to, weight);
    }

    // Encontrar el camino mas corto
    public void  findPath(String start, String end) {
        if (!adjList.containsKey(start) || !adjList.containsKey(end)) {
            return;
        }

        // Dijkstra
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : adjList.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(start);

        while (!pq.isEmpty()) {
            String current = pq.poll();

            for (Map.Entry<String, Integer> neighbor : adjList.get(current).entrySet()) {
                int newDist = distances.get(current) + neighbor.getValue();
                if (newDist < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), newDist);
                    previous.put(neighbor.getKey(), current);
                    pq.add(neighbor.getKey());
                }
            }
        }

       path = new ArrayList<>();
        for (String at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

    }

    public void draw(String start, StringBuilder output) {
        if (!adjList.containsKey(start)) {
            output.append("Nodo no existe.\n");
            return;
        }

        output.append("Árbol desde ").append(start).append(":\n");
        drawHelper(start, new HashSet<>(), 0, output);
    }

    private void drawHelper(String node, Set<String> visited, int level, StringBuilder output) {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);

        output.append("  ".repeat(level)).append(node).append("\n");
        for (String neighbor : adjList.getOrDefault(node, new HashMap<>()).keySet()) {
            drawHelper(neighbor, visited, level + 1, output);
        }
    }
    public Map<String, Map<String, Integer>> getAdjList() {
        return adjList;
    }
}
