
%%

%public
%class Lexer
%unicode
%cup
%line
%column

%{
  // import java_cup.runtime.Symbol;
%}

%%

/* --- Comentarios --- */
\s+                { /* ignorar espacios */ }
"//".*             { /* comentario de una línea */ }

/* --- palabras reservadas --- */
"draw"             { return Token(Token.DRAW); }
"path"             { return Token(Token.PATH); }

/* --- Tokens --- */
"=>"               { return Token(Token.ARROW); }
":"                { return Token(Token.COLON); }
";"                { return Token(Token.SEMICOLON); }
"="                { return Token(Token.EQUAL); }
","                { return Token(Token.COMMA); }
"("                { return Token(Token.LPAREN); }
")"                { return Token(Token.RPAREN); }

/* --- IDENTIFICADORES Y NÚMEROS --- */
[A-Za-z_][A-Za-z_0-9]*   { return Token(Token.ID, yytext()); }
[0-9]+                   { return Token(Token.NUM, Integer.parseInt(yytext())); }

/* --- ERROR --- */
.                  { throw new Error("Símbolo ilegal: " + yytext()); }
