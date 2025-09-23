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
            case '\t':
                getChar();
                break;
            case '\n':
                scanner.line++;
                getChar();
                break;
            case '/':
                if(peekNext() == '/'){
                    while(peek() != '\n' && !isAtEnd())
                        getChar();
                }else{
                    return;
                }
            default:
                return;
        }
    }
}

static Token handleString(){
    char ch = peek();
    while(ch != '"' && !isAtEnd()){
        if(ch == '\n') scanner.line++;
        getChar();
        ch = peek();
    }
    if(isAtEnd()) return errorToken("Unterminated String. ");
    getChar();
    return makeToken(TOKEN_STRING);
}

static bool isDigit(char c){
    return c >= '0' && c <= '9';
}

static Token handleNumber(){
    while(isDigit(peek())) getChar();
    if(peek() == '.' && isDigit(peekNext())){
        getChar();
        while (isDigit(peek())) getChar();
    }
    return makeToken(TOKEN_NUMBER);
}

Token scanToken(){
    scanner.start = scanner.current;

    if(isAtEnd()) return makeToken(TOKEN_EOF);
    
    skipWhitespace();

    scanner.start = scanner.current;


    char c = getChar();
    switch (c) {
        case '(': return makeToken(TOKEN_LEFT_PAREN);
        case ')': return makeToken(TOKEN_RIGHT_PAREN);
        case '{': return makeToken(TOKEN_LEFT_BRACE);
        case '}': return makeToken(TOKEN_RIGHT_BRACE);
        case ',': return makeToken(TOKEN_COMMA);
        case '.': return makeToken(TOKEN_DOT);
        case '-': return makeToken(TOKEN_MINUS);
        case '+': return makeToken(TOKEN_PLUS);
        case ';': return makeToken(TOKEN_SEMICOLON);
        case '/': return makeToken(TOKEN_SLASH);
        case '*': return makeToken(TOKEN_STAR);

        case '!': return makeToken(match('=') ? TOKEN_BANG_EQUAL : TOKEN_BANG);
        case '=': return makeToken(match('=') ? TOKEN_EQUAL_EQUAL : TOKEN_EQUAL);
        case '>': return makeToken(match('=') ? TOKEN_GREATER_EQUAL : TOKEN_GREATER);
        case '<': return makeToken(match('=') ? TOKEN_LESS_EQUAL : TOKEN_LESS);

        case '"': return handleString();

        default:
            if(isDigit(c)) return handleNumber();

    }


    return errorToken("Unexpected character.");
}
