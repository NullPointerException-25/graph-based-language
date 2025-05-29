package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.mycompany.graph.Graphclass;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import javax.swing.text.*;

public class Graph {
    private static void highlightLine(JTextArea textArea, int lineNumber, Color color) {
        try {
            Highlighter highlighter = textArea.getHighlighter();
            highlighter.removeAllHighlights();

            int start = textArea.getLineStartOffset(lineNumber);
            int end = textArea.getLineEndOffset(lineNumber);
            highlighter.addHighlight(start, end, new DefaultHighlighter.DefaultHighlightPainter(color));
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
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
        JTextArea lineNumbers = new JTextArea("0");
        lineNumbers.setEditable(false);
        lineNumbers.setBackground(Color.LIGHT_GRAY);
        lineNumbers.setFont(codeArea.getFont());

        codeArea.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int lines = codeArea.getLineCount();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= lines; i++) {
                    sb.append(i).append(System.lineSeparator());
                }
                return sb.toString();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }
        });
        // Mostrar números de línea a la izquierda
        JScrollPane lineScroll = new JScrollPane(lineNumbers);
        lineScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        lineScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        lineScroll.setPreferredSize(new Dimension(30, Integer.MAX_VALUE));

        JPanel editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(lineScroll, BorderLayout.WEST);
        editorPanel.add(codeScroll, BorderLayout.CENTER);
        panel.add(editorPanel, BorderLayout.CENTER);
        // la salida
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Salida"));
        panel.add(outputScroll, BorderLayout.SOUTH);
        outputScroll.setPreferredSize(new Dimension(800, 150));

        // botonsito
        JPanel buttonPanel = new JPanel();
        JButton clearButton = new JButton("Limpiar"); // agregue un boton de limpiar
        JButton analyzeButton = new JButton("Analizar");
        JButton executeButton = new JButton("Ejecutar");
        JButton openButton = new JButton("Abrir Archivo");
        JButton saveButton = new JButton("Guardar Archivo");


        buttonPanel.add(openButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(analyzeButton);
        buttonPanel.add(executeButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.NORTH);

        Graphclass graph = new Graphclass();
        Map<String, Integer> nodes = new HashMap<>();
        clearButton.addActionListener(e -> {
            codeArea.setText("");
            outputArea.setText("");
        });
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
            codeArea.getHighlighter().removeAllHighlights(); // Limpia resaltados anteriores
            try {
                String[] lines = code.split("\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
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
                    highlightLine(codeArea, i, Color.PINK); // 'i' es el índice de la línea con error
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
