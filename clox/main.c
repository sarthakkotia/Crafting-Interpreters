#include "common.h"
#include "chunk.h"
#include "debug.h"

int main(int argc, const char* argv[]) {
    Chunk chunk;
    initChunk(&chunk);
    writeChunk(&chunk, OP_RETURN, 1);
    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 2);
    writeChunk(&chunk, constant, 2);
    writeChunk(&chunk, OP_RETURN, 3);
    writeChunk(&chunk, OP_RETURN, 4);
    writeChunk(&chunk, OP_RETURN, 5);
    writeChunk(&chunk, OP_RETURN, 6);
    writeChunk(&chunk, OP_RETURN, 7);
    writeChunk(&chunk, OP_RETURN, 8);
    writeChunk(&chunk, OP_RETURN, 9);
    writeChunk(&chunk, OP_RETURN, 10);
    disassembleChunk(&chunk, "test");

    freeChunk(&chunk);
    return 0;
}
