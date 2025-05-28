package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.mycompany.graph.Graphclass;


public class Graph {

    public static void main(String[] args) {
        JFrame frame = new JFrame("IDE Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        //código
        JTextArea codeArea = new JTextArea();
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Editor de Código"));
        panel.add(codeScroll, BorderLayout.CENTER);

        // la salida
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Salida"));
        panel.add(outputScroll, BorderLayout.SOUTH);
        outputScroll.setPreferredSize(new Dimension(800, 150));

        // botonsito
        JPanel buttonPanel = new JPanel();
        JButton analyzeButton = new JButton("Analizar");
        JButton executeButton = new JButton("Ejecutar");
        JButton openButton = new JButton("Abrir Archivo");
        JButton saveButton = new JButton("Guardar Archivo");

        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(executeButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        Graphclass graph = new Graphclass();
        Map<String, Integer> nodes = new HashMap<>();

        //abrir archivos
        openButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    codeArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        codeArea.append(line + "\n");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al leer el archivo: " + ex.getMessage());
                }
            }
        });

        //guardar archivos
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(codeArea.getText());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error al guardar el archivo: " + ex.getMessage());
                }
            }
        });

        //analizar
        analyzeButton.addActionListener(e -> {
            String code = codeArea.getText();
            outputArea.setText("Analizando código...\n");

            try {
                String[] lines = code.split("\\n");
                for (String line : lines) {
                    line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                // Nodo: nombre = valor;
                if (line.matches("\\s*[A-Za-z_][A-Za-z_0-9]*\\s*=\\s*\\d+\\s*;\\s*")) {
                    String[] parts = line.split("=");
                    String node = parts[0].trim();
                    int value = Integer.parseInt(parts[1].replace(";", "").trim());
                    nodes.put(node, value);
                    outputArea.append("Nodo " + node + " asignado con valor " + value + "\n");

                // Arista: origen => destino : peso;
                } else if (line.matches("\\s*[A-Za-z_][A-Za-z_0-9]*\\s*=>\\s*[A-Za-z_][A-Za-z_0-9]*\\s*:\\s*\\d+\\s*;\\s*")) {
                    String[] parts = line.split("=>|:");
                    String from = parts[0].trim();
                    String to = parts[1].trim();
                    int weight = Integer.parseInt(parts[2].replace(";", "").trim());
                    graph.addEdge(from, to, weight);
                    outputArea.append("Arista añadida de " + from + " a " + to + " con peso " + weight + "\n");

                // Camino: path(origen, destino);
                } else if (line.matches("\\s*path\\s*\\(\\s*[A-Za-z_][A-Za-z_0-9]*\\s*,\\s*[A-Za-z_][A-Za-z_0-9]*\\s*\\)\\s*;\\s*")) {
                    String[] parts = line.replaceAll("path\\s*\\(|\\)\\s*;", "").split(",");
                    String start = parts[0].trim();
                    String end = parts[1].trim();
                    String path = graph.findPath(start, end);
                    outputArea.append("Camino de " + start + " a " + end + ": " + path + "\n");

                // Dibujar: draw(nombre);
                } else if (line.matches("\\s*draw\\s*\\(\\s*[A-Za-z_][A-Za-z_0-9]*\\s*\\)\\s*;\\s*")) {
                    String node = line.replaceAll("draw\\s*\\(|\\)\\s*;", "").trim();
                    StringBuilder drawOutput = new StringBuilder();
                    graph.draw(node, drawOutput); 
                    outputArea.append(drawOutput.toString());

                // Error: línea no reconocida
                } else {
                    outputArea.append("Línea no reconocida: " + line + "\n");
                }

                }
            } catch (Exception ex) {
                outputArea.append("Error al analizar el código: " + ex.getMessage() + "\n");
            }
        });

        executeButton.addActionListener(e -> {
            JFrame graphFrame = new JFrame("Visualización del Grafo");
            graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            graphFrame.setSize(600, 600);

            GraphPanel graphPanel = new GraphPanel(graph.getAdjList());
            graphFrame.add(graphPanel);
            graphFrame.setVisible(true);
        });

        frame.setVisible(true);
    }
}
