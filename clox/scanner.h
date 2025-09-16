#ifndef clox_scanner_h
#define clox_scanner_h

typedef struct {
    const char *start;
    const char *current;
    int line;
}Scanner;

typedef enum{
    // Single character tokens
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, BREAK,
    // One/Two character tokens
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
    // Literals
    IDENTIFIER, STRING, NUMBER,
    // Keywords
    AND, CLASS, IF, ELSE, TRUE, FALSE, FUN, FOR, NIL, OR, PRINT, RETURN, SUPER, THIS, VAR, WHILE,
    EOF, ERROR
}TokenType;

typedef struct {
    TokenType type;
    const char *start;
    int length;
    int line;
}Token;



void initScanner(const char *source);
Token scanToken();

#endif //clox_scanner_h
