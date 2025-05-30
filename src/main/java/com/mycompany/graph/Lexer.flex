%%

%public
%class Lexer
%unicode
%cup
%line
%column

%{
  // CUP expects this import
  import java_cup.runtime.Symbol;
  // Token codes defined elsewhere (typically in sym.java generated by CUP)
%}

%%

/* --- ESPACIOS Y COMENTARIOS --- */
\s+                { /* ignorar espacios */ }
"//".*             { /* ignorar comentario de una línea */ }

/* --- PALABRAS RESERVADAS --- */
"draw"             { return new Symbol(sym.DRAW, yyline + 1, yycolumn + 1, yytext()); }
"path"             { return new Symbol(sym.PATH, yyline + 1, yycolumn + 1, yytext()); }

/* --- SÍMBOLOS --- */
"=>"               { return new Symbol(sym.ARROW, yyline + 1, yycolumn + 1, yytext()); }
":"                { return new Symbol(sym.COLON, yyline + 1, yycolumn + 1, yytext()); }
";"                { return new Symbol(sym.SEMICOLON, yyline + 1, yycolumn + 1, yytext()); }
"="                { return new Symbol(sym.EQUAL, yyline + 1, yycolumn + 1, yytext()); }
","                { return new Symbol(sym.COMMA, yyline + 1, yycolumn + 1, yytext()); }
"("                { return new Symbol(sym.LPAREN, yyline + 1, yycolumn + 1, yytext()); }
")"                { return new Symbol(sym.RPAREN, yyline + 1, yycolumn + 1, yytext()); }

/* --- IDENTIFICADORES Y NÚMEROS --- */
[A-Za-z_][A-Za-z_0-9]*   { return new Symbol(sym.ID, yyline + 1, yycolumn + 1, yytext()); }
[0-9]+                   { return new Symbol(sym.NUM, yyline + 1, yycolumn + 1, Integer.parseInt(yytext())); }

/* --- ERROR --- */
.                        { throw new Error("Símbolo ilegal: " + yytext() +
                                " en línea " + (yyline + 1) + ", columna " + (yycolumn + 1)); }
