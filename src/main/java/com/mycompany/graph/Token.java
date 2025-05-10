package com.mycompany.graph;

public class Token {

    // Tipos de token (pueden corresponder a los definidos en "sym.java" de CUP)
    public static final int ID = 1;           // Identificadores como A, B, C
    public static final int NUM = 2;          // Números como 1, 2, 15
    public static final int EQUAL = 3;        // Signo '='
    public static final int ARROW = 4;        // Signo '=>'
    public static final int COLON = 5;        // Signo ':'
    public static final int SEMICOLON = 6;    // Signo ';'
    public static final int COMMA = 7;        // Signo ','
    public static final int LPAREN = 8;       // Paréntesis '('
    public static final int RPAREN = 9;       // Paréntesis ')'
    public static final int DRAW = 10;        // Palabra reservada 'draw'
    public static final int PATH = 11;        // Palabra reservada 'path'

    public int tipo;      // Tipo del token
    public String valor;  // Valor del token (como texto)

    // Constructores
    public Token(int tipo) {
        this.tipo = tipo;
        this.valor = null;
    }

    public Token(int tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Token(" + tipoToString(tipo) + ", " + valor + ")";
    }

    // Opcional: para mostrar el tipo en texto
    private String tipoToString(int tipo) {
        switch (tipo) {
            case ID: return "ID";
            case NUM: return "NUM";
            case EQUAL: return "EQUAL";
            case ARROW: return "ARROW";
            case COLON: return "COLON";
            case SEMICOLON: return "SEMICOLON";
            case COMMA: return "COMMA";
            case LPAREN: return "LPAREN";
            case RPAREN: return "RPAREN";
            case DRAW: return "DRAW";
            case PATH: return "PATH";
            default: return "UNKNOWN";
        }
    }
}
