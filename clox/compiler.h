#ifndef clox_compiler_h
#define clox_compiler_h

#include "chunk.h"
#include "scanner.h"

typedef struct{
    Token current;
    Token previous;
    bool hadError;
    bool panicMode;
} Parser;

bool compile(const char *source, Chunk *chunk);

#endif //clox_compiler_h
