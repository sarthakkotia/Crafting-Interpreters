#include <stdio.h>
#include "compiler.h"
#include "scanner.h"


void compile(const char *source){
    // lexing for that we will need the scanner
    // converting it to bytecode
    // save them into chunks
    // save those chunks into bytecode
    int line = -1;
    for(;;){
        Token token = scanToken();
        if(token.line != line){
            printf("%4d", token.line);
            line = token.line;
        }else{
            printf("   | ");
        }
        printf("%2d '%.*s'\n", token.type, token.length, token.start);

        if(token.type == EOF) break;
    }
}