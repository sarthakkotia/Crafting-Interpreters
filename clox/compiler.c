#include <stdio.h>
#include "compiler.h"

Parser parser;
Chunk *compilingChunk;

static Chunk *currentChunk(){
    return compilingChunk;
}

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

static void emitByte(uint8_t byte){
    writeChunk(currentChunk(), byte, parser.previous.line);
}

static void emitBytes(uint8_t byte1, uint8_t byte2){
    emitByte(byte1);
    emitByte(byte2);
}
//TODO: Add another helper function to write / emit the long byte operand as well the same way like we did the emit Bytes function

static void emitReturn(){
    emitByte(OP_RETURN);
}

static void endCompiler(){
    emitReturn();
}

bool compile(const char *source, Chunk *chunk){
    // lexing for that we will need the scanner
    // converting it to bytecode
    // save them into chunks
    // save those chunks into bytecode
    initScanner(source);

    parser.hadError = false;
    parser.panicMode = false;
    compilingChunk = chunk;

    advance();
    expression();
    consume(TOKEN_EOF, "Expect end of Expression");
    endCompiler();
    return !parser.hadError;
}