package com.mycompany.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.mycompany.graph.Graphclass;
import java_cup.runtime.Symbol;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;
import javax.swing.text.*;

public class Graph {
    private static   Parser parser;
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
            codeArea.getHighlighter().removeAllHighlights();

            try {
                // Create lexer and parser instances
                Lexer lexer = new Lexer(new StringReader(code));
                 parser = new Parser(lexer);

                // Create custom error handler to capture syntax errors
                parser.setErrorHandler(new ErrorHandler() {
                    @Override
                    public void report_error(String message, Object info) {
                        if (info instanceof Symbol symbol) {
                            int line = symbol.left - 1; // Convert to 0-based indexing
                            outputArea.append("Error en línea " + (line + 1) + ": " + symbol.value + "\n");
                            System.err.println("Error en línea " + (line + 1) + ": " + symbol.value + "\n");

                            // Highlight the error line
                            highlightLine(codeArea, line, Color.PINK);
                        } else {
                            outputArea.append("Error: " + message + "\n");
                            System.err.println("Error: " + message);
                        }
                    }
                });

                // Parse the code
                parser.parse();

                // If we get here without exceptions, parsing was successful
                outputArea.append("Análisis completado sin errores sintácticos.\n");

                // Display graph information
                for (Map.Entry<String, Parser.Nodo> entry : parser.nodos.entrySet()) {
                    outputArea.append("Nodo: " + entry.getValue() + "\n");
                }

                for (Parser.Arista arista : parser.aristas) {
                    outputArea.append("Arista: " + arista + "\n");
                }

            } catch (Exception ex) {
                if (ex instanceof LexicalError lexError) {
                    outputArea.append("Error léxico en línea " + lexError.getLine() + 2 +
                                     ", columna " + lexError.getColumn() + ": " +
                                     lexError.getMessage() + "\n");

                    // Highlight the line with the lexical error
                    highlightLine(codeArea, lexError.getLine() - 1, Color.PINK);
                } else {
                    // General error handling
                    outputArea.append("Error al analizar: " + ex.getMessage() + "\n");

                }
            }
        });

        executeButton.addActionListener(e -> {
            JFrame graphFrame = new JFrame("Visualización del Grafo");
            graphFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            graphFrame.setSize(600, 600);
            frame.setLocationRelativeTo(null);
            GraphPanel graphPanel = new GraphPanel(parser.graph.getAdjList(), parser.graph.path);
            graphFrame.add(graphPanel);
            graphFrame.setVisible(true);
        });

        frame.setVisible(true);
    }
}
