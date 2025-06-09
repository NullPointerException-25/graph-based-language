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

        JTextArea codeArea = new JTextArea();
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Editor de Código"));
        panel.add(codeScroll, BorderLayout.CENTER);

        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Salida"));
        panel.add(outputScroll, BorderLayout.SOUTH);
        outputScroll.setPreferredSize(new Dimension(800, 150));

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

        executeButton.setEnabled(false); // ← Aquí deshabilitamos el botón al inicio

        Graphclass graph = new Graphclass();
        Map<String, Integer> nodes = new HashMap<>();

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

        analyzeButton.addActionListener(e -> {
            String code = codeArea.getText();
            outputArea.setText("Analizando código...\n");
            boolean hayErrores = false;
            nodes.clear();
            graph.clear(); // ← Si tienes un método clear en Graphclass para limpiar aristas previas

            try {
                String[] lines = code.split("\\n");
                for (String line : lines) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if (line.matches("[A-Za-z] = \\d+;")) {
                        String[] parts = line.split(" = ");
                        String node = parts[0].trim();
                        int value = Integer.parseInt(parts[1].replace(";", ""));
                        if (nodes.containsKey(node)) {
                            outputArea.append("Error: Nodo '" + node + "' ya fue definido.\n");
                            hayErrores = true;
                        } else {
                            nodes.put(node, value);
                            outputArea.append("Nodo " + node + " asignado con valor " + value + "\n");
                        }
                    } else if (line.matches("[A-Za-z] => [A-Za-z]: \\d+;")) {
                        String[] parts = line.split("=>|:");
                        String from = parts[0].trim();
                        String to = parts[1].trim();
                        int weight = Integer.parseInt(parts[2].replace(";", "").trim());
                        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
                            outputArea.append("Error: Uno o ambos nodos no existen para la arista: " + from + " => " + to + "\n");
                            hayErrores = true;
                        } else {
                            graph.addEdge(from, to, weight);
                            outputArea.append("Arista añadida de " + from + " a " + to + " con peso " + weight + "\n");
                        }
                    } else if (line.matches("path\\([A-Za-z], [A-Za-z]\\);")) {
                        String[] parts = line.replace("path(", "").replace(");", "").split(",");
                        String start = parts[0].trim();
                        String end = parts[1].trim();
                        if (!nodes.containsKey(start) || !nodes.containsKey(end)) {
                            outputArea.append("Error: Nodo de inicio o fin no definido: " + start + " a " + end + "\n");
                            hayErrores = true;
                        } else {
                            String path = graph.findPath(start, end);
                            outputArea.append("Camino de " + start + " a " + end + ": " + path + "\n");
                        }
                    } else if (line.matches("draw\\([A-Za-z]\\);")) {
                        String node = line.replace("draw(", "").replace(");", "").trim();
                        if (!nodes.containsKey(node)) {
                            outputArea.append("Error: Nodo no definido para dibujar: " + node + "\n");
                            hayErrores = true;
                        } else {
                            StringBuilder drawOutput = new StringBuilder();
                            graph.draw(node, drawOutput);
                            outputArea.append(drawOutput.toString());
                        }
                    } else {
                        outputArea.append("Línea no reconocida: " + line + "\n");
                        hayErrores = true;
                    }
                }

                if (hayErrores) {
                    outputArea.append("\nSe encontraron errores. No se ejecutará la lógica principal.\n");
                    executeButton.setEnabled(false); // ← Deshabilita el botón
                } else {
                    outputArea.append("\nAnálisis completado sin errores.\n");
                    executeButton.setEnabled(true); // ← Habilita el botón
                }

            } catch (Exception ex) {
                outputArea.append("Error al analizar el código: " + ex.getMessage() + "\n");
                executeButton.setEnabled(false);
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