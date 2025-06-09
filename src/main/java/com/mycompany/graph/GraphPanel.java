package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author katii
 */
public class GraphPanel extends javax.swing.JPanel {
    private final Map<String, Point> nodePositions;
    private final Map<String, Map<String, Integer>> adjList;


    public GraphPanel(Map<String, Map<String, Integer>> adjList) {
        this.adjList = adjList;
        this.nodePositions = new HashMap<>();
        calculateNodePositions();
    }
    
    private void calculateNodePositions() {
        int radius = 200; // Radio del círculo para distribuir los nodos
        int centerX = 250; // Centro del panel en X
        int centerY = 250; // Centro del panel en Y
        int i = 0;
        int totalNodes = adjList.keySet().size();

        for (String node : adjList.keySet()) {
            double angle = 2 * Math.PI * i / totalNodes;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            nodePositions.put(node, new Point(x, y));
            i++;
        }
    }@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar aristas
        g2d.setColor(Color.BLACK);
        for (String from : adjList.keySet()) {
            Point fromPos = nodePositions.get(from);
            for (String to : adjList.get(from).keySet()) {
                Point toPos = nodePositions.get(to);
                g2d.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);

                // Dibujar peso de la arista
                int midX = (fromPos.x + toPos.x) / 2;
                int midY = (fromPos.y + toPos.y) / 2;
                g2d.drawString(adjList.get(from).get(to).toString(), midX, midY);
            }
        }

        // Dibujar nodos
        for (String node : nodePositions.keySet()) {
            Point pos = nodePositions.get(node);
            g2d.setColor(Color.green);
            g2d.fillOval(pos.x - 20, pos.y - 20, 40, 40); 
            g2d.setColor(Color.BLACK);
            g2d.drawOval(pos.x - 20, pos.y - 20, 40, 40); 
            g2d.drawString(node, pos.x - 5, pos.y + 5); 
        }
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
