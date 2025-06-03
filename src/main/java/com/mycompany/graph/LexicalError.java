package com.mycompany.graph;

class LexicalError extends Exception {
    private int line;
    private int column;

    public LexicalError(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public int getLine() { return line; }
    public int getColumn() { return column; }
}
