#include "string.h"
#include "scanner.h"
#include "common.h"

Scanner scanner;

static bool isAtEnd(){
    return *scanner.current == '\0';
}

void initScanner(const char *source){
    scanner.start = source;
    scanner.current = source;
    scanner.line = 0;
}

static Token makeToken(TokenType type){
    Token token;
    token.type = type;
    token.start = scanner.start;
    token.length = (int)(scanner.current - scanner.start + 1);
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


Token scanToken(){
    scanner.start = scanner.current;

    if(isAtEnd()) return makeToken(TOKEN_EOF);

    return errorToken("Unexpected character.");
}
