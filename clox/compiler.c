#include <stdio.h>
#include "compiler.h"
#include "scanner.h"


bool compile(const char *source, Chunk *chunk){
    // lexing for that we will need the scanner
    // converting it to bytecode
    // save them into chunks
    // save those chunks into bytecode
    initScanner(source);
    getchar();
    expression();
    consume(TOKEN_EOF, "Expect end of Expression");
    return true;
}