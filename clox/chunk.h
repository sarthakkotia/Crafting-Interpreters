#ifndef clox_chunk_h
#define clox_chunk_h

#include "common.h"
#include "value.h"

typedef enum {
    OP_RETURN,
    OP_NEGATE,
    OP_ADD,
    OP_SUBTRACT,
    OP_MULTIPLY,
    OP_DIVIDE,
    OP_NIL,
    OP_TRUE,
    OP_FALSE,
    OP_NOT,
    OP_EQUAL,
    OP_LESS,
    OP_GREATER,
    OP_PRINT,
    OP_POP,
    // from this line all the opcodes have operands
    OP_CONSTANT,
    OP_DEFINE_GLOBAL,
    OP_CONSTANT_LONG,
} OpCode;

typedef struct {
    int capacity;
    int count;
    int* lines;
}LinesArray;

typedef struct {
    int capacity;
    int count;
    uint8_t* code;
    LinesArray linesArray;
    ValueArray constants;
} Chunk;

void initChunk(Chunk* chunk);
void writeChunk(Chunk* chunk, uint8_t byte, int line);
void freeChunk(Chunk* chunk);
int addConstant(Chunk* chunk, Value value);

#endif //clox_chunk_h
