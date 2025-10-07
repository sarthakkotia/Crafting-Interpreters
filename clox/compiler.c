#include <stdio.h>
#include "compiler.h"

Parser parser;


static void errorAt(Token *token, const char *message){
    if(parser.panicMode) return;
    parser.panicMode = true;
    fprintf(stderr, "[line %d] Error", token->line);

    if(token->type == TOKEN_EOF){
        fprintf(stderr, " at end");
    }else if(token->type == TOKEN_ERROR){

    }else{
        fprintf(stderr, "at '%.*s'", token->length, token->start);
    }
    fprintf(stderr, ": %s\n", message);
    parser.hadError = true;
}

static void errorAtCurrent(const char *message){
    errorAt(&parser.current, message);
}


static void advance(){
    parser.previous = parser.current;
    for(;;){
        Token token = scanToken();
        if(token.type == TOKEN_ERROR) errorAtCurrent(parser.current.start);
        else break;
    }
}

static void consume(TokenType tokenType, const char *message){
    if(parser.current.type != tokenType){
        errorAt(&parser.current, message);
    }
    advance();
    return;
}

bool compile(const char *source, Chunk *chunk){
    // lexing for that we will need the scanner
    // converting it to bytecode
    // save them into chunks
    // save those chunks into bytecode
    initScanner(source);

    parser.hadError = false;
    parser.panicMode = false;

    advance();
    expression();
    consume(TOKEN_EOF, "Expect end of Expression");
    return !parser.hadError;
}