package com.craftinginterpreters.lox;

enum TokenType {
    // Single character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, BREAK,

    // One/Two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    // Keywords
    AND, CLASS, IF, ELSE, TRUE, FALSE, FUN, FOR, NIL, OR, PRINT, RETURN, SUPER, THIS, VAR, WHILE,

    EOF
}
