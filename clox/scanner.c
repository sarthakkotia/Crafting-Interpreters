#include "string.h"
#include "scanner.h"
#include "common.h"

Scanner scanner;

void initScanner(const char *source){
    scanner.start = source;
    scanner.current = source;
    scanner.line = 0;
}


static bool isAtEnd(){
    return *scanner.current == '\0';
}

static bool match(char expected){
    if(isAtEnd()) return false;
    if(*(scanner.current) != expected) return false;
    scanner.current++;
    return true;
}

static char getChar(){
    char ch = *(scanner.current);
    scanner.current++;
    return ch;
}

static Token makeToken(TokenType type){
    Token token;
    token.type = type;
    token.start = scanner.start;
    token.length = (int)(scanner.current - scanner.start);
    token.line = scanner.line;
    return token;
}
static Token errorToken(const char *errMessage){
    Token token;
    token.type = TOKEN_ERROR;
    token.start = errMessage;
    token.length = (int) strlen(errMessage);
    token.line = scanner.line;
    return token;
}

static char peek(){
    return *scanner.current;
}

static char peekNext(){
    if(isAtEnd()) return '\0';
    return *(scanner.current+1);
}

static void skipWhitespace(){
    for(;;){
        char ch = peek();
        switch (ch) {
            case ' ':
            case '\r':
            case '/':
                if(peekNext() == '/'){
                    while(peek() != '\n' && !isAtEnd())
                        getChar();
                }else{
                    return;
                }
            case '\t':
                getChar();
                break;
            case '\n':
                scanner.line++;
                break;
            default:
                return;
        }
    }
}

Token scanToken(){
    scanner.start = scanner.current;

    if(isAtEnd()) return makeToken(TOKEN_EOF);
    
    skipWhitespace();
    
    char c = getChar();
    switch (c) {
        case '(': makeToken(TOKEN_LEFT_PAREN);
        case ')': makeToken(TOKEN_RIGHT_PAREN);
        case '{': makeToken(TOKEN_LEFT_BRACE);
        case '}': makeToken(TOKEN_RIGHT_BRACE);
        case ',': makeToken(TOKEN_COMMA);
        case '.': makeToken(TOKEN_DOT);
        case '-': makeToken(TOKEN_MINUS);
        case '+': makeToken(TOKEN_PLUS);
        case ';': makeToken(TOKEN_SEMICOLON);
        case '/': makeToken(TOKEN_SLASH);
        case '*': makeToken(TOKEN_STAR);

        case '!':
            makeToken(match('=') ? TOKEN_BANG_EQUAL : TOKEN_BANG);
            break;
        case '=':
            makeToken(match('=') ? TOKEN_EQUAL_EQUAL : TOKEN_EQUAL);
            break;
        case '>':
            makeToken(match('=') ? TOKEN_GREATER_EQUAL : TOKEN_GREATER);
            break;
        case '<':
            makeToken(match('=') ? TOKEN_LESS_EQUAL : TOKEN_LESS);
            break;

    }


    return errorToken("Unexpected character.");
}
