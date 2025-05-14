package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.mycompany.graph.Graphclass;

/**
 * Simple IDE integrado con la clase Graph.
 *
 * @author user
 */
public class Graph {

    public static void main(String[] args) {
        // Crear la ventana principal
        JFrame frame = new JFrame("IDE Graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Crear el panel principal
        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        // Área de texto para escribir el código
        JTextArea codeArea = new JTextArea();
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Editor de Código"));
        panel.add(codeScroll, BorderLayout.CENTER);

        // Área de texto para mostrar la salida
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Salida"));
        panel.add(outputScroll, BorderLayout.SOUTH);
        outputScroll.setPreferredSize(new Dimension(800, 150));

        // Botones de acción
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

        // Estructura para representar el grafo
        Graphclass graph = new Graphclass();
        Map<String, Integer> nodes = new HashMap<>();

        // Funcionalidad para abrir archivos
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

        // Funcionalidad para guardar archivos
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

        // Funcionalidad del botón "Analizar"
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

                    if (line.matches("[A-Za-z] = \\d+;")) {
                        // Asignar valor a un nodo
                        String[] parts = line.split(" = ");
                        String node = parts[0].trim();
                        int value = Integer.parseInt(parts[1].replace(";", ""));
                        nodes.put(node, value);
                        outputArea.append("Nodo " + node + " asignado con valor " + value + "\n");
                    } else if (line.matches("[A-Za-z] => [A-Za-z]: \\d+;")) {
                        // Crear una arista con peso
                        String[] parts = line.split("=>|:");
                        String from = parts[0].trim();
                        String to = parts[1].trim();
                        int weight = Integer.parseInt(parts[2].replace(";", "").trim()); // Eliminar espacios extra aquí
                        graph.addEdge(from, to, weight);
                        outputArea.append("Arista añadida de " + from + " a " + to + " con peso " + weight + "\n");
                    } else if (line.matches("path\\([A-Za-z], [A-Za-z]\\);")) {
                        // Dibujar un camino
                        String[] parts = line.replace("path(", "").replace(");", "").split(",");
                        String start = parts[0].trim();
                        String end = parts[1].trim();
                        String path = graph.findPath(start, end);
                        outputArea.append("Camino de " + start + " a " + end + ": " + path + "\n");
                    } else if (line.matches("draw\\([A-Za-z]\\);")) {
                        String node = line.replace("draw(", "").replace(");", "").trim();
                        StringBuilder drawOutput = new StringBuilder();
                        graph.draw(node, drawOutput); // Redirigir la salida a un StringBuilder
                        outputArea.append(drawOutput.toString()); // Mostrar el resultado en el área de salida
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

        // Mostrar la ventana
        frame.setVisible(true);
    }
}
