package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphPanel extends javax.swing.JPanel {

    private final Map<String, Point> nodePositions;
    private final Map<String, Map<String, Integer>> adjList;
    private final ArrayList<String> path;
    private int totalWeight = 0;

    public GraphPanel(Map<String, Map<String, Integer>> adjList, ArrayList<String> path) {
        this.adjList = adjList;
        this.path = path;
        this.nodePositions = new HashMap<>();
        calculateNodePositions();
        this.totalWeight = calculateTotalWeight();
    }

    private void calculateNodePositions() {
        int radius = 200; // Radio del círculo para distribuir los nodos
        int centerX = 250; // Centro del panel en X
        int centerY = 250; // Centro del panel en Y
        int i = 0;
        int totalNodes = adjList.size();

        for (String node : adjList.keySet()) {
            double angle = 2 * Math.PI * i / totalNodes;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            nodePositions.put(node, new Point(x, y));
            i++;
        }
    }

    private int calculateTotalWeight() {
        int weight = 0;
        if (path != null) {
            for (int i = 0; i < path.size() - 1; i++) {
                String from = path.get(i);
                String to = path.get(i + 1);
                Integer edgeWeight = adjList.get(from).get(to);
                if (edgeWeight != null) {
                    weight += edgeWeight;
                }
            }
        }
        return weight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int nodeRadius = 20;

        // Dibujar aristas con flechas
        for (String from : adjList.keySet()) {
            Point fromPos = nodePositions.get(from);
            for (String to : adjList.get(from).keySet()) {
                Point toPos = nodePositions.get(to);

                // Comprobar si la arista está en el camino
                boolean isPathEdge = false;
                if (path != null) {
                    for (int i = 0; i < path.size() - 1; i++) {
                        String pathFrom = path.get(i);
                        String pathTo = path.get(i + 1);
                        if (pathFrom.equals(from) && pathTo.equals(to)) {
                            isPathEdge = true;
                            break;
                        }
                    }
                }

                g2d.setColor(isPathEdge ? Color.ORANGE : Color.BLACK);

                // Calcular dirección y ajustar por el radio
                double dx = toPos.x - fromPos.x;
                double dy = toPos.y - fromPos.y;
                double distance = Math.sqrt(dx * dx + dy * dy);
                double offsetX = (dx / distance) * nodeRadius;
                double offsetY = (dy / distance) * nodeRadius;

                int adjustedFromX = (int) (fromPos.x + offsetX);
                int adjustedFromY = (int) (fromPos.y + offsetY);
                int adjustedToX = (int) (toPos.x - offsetX);
                int adjustedToY = (int) (toPos.y - offsetY);

                // Línea
                g2d.drawLine(adjustedFromX, adjustedFromY, adjustedToX, adjustedToY);

                // Flecha
                double angle = Math.atan2(dy, dx);
                int arrowSize = 10;
                int x1 = (int) (adjustedToX - arrowSize * Math.cos(angle - Math.PI / 6));
                int y1 = (int) (adjustedToY - arrowSize * Math.sin(angle - Math.PI / 6));
                int x2 = (int) (adjustedToX - arrowSize * Math.cos(angle + Math.PI / 6));
                int y2 = (int) (adjustedToY - arrowSize * Math.sin(angle + Math.PI / 6));

                Polygon arrowHead = new Polygon();
                arrowHead.addPoint(adjustedToX, adjustedToY);
                arrowHead.addPoint(x1, y1);
                arrowHead.addPoint(x2, y2);
                g2d.fillPolygon(arrowHead);

                // Peso
                int midX = (adjustedFromX + adjustedToX) / 2;
                int midY = (adjustedFromY + adjustedToY) / 2;
                g2d.drawString(adjList.get(from).get(to).toString(), midX, midY);
            }
        }

        // Dibujar nodos
        for (String node : nodePositions.keySet()) {
            Point pos = nodePositions.get(node);
            g2d.setColor((path != null && path.contains(node)) ? Color.ORANGE : Color.GREEN);
            g2d.fillOval(pos.x - nodeRadius, pos.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - nodeRadius, pos.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
            g2d.drawString(node, pos.x - 5, pos.y + 5);
        }

        // Mostrar peso total del camino
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Peso total del camino: " + totalWeight, 20, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
