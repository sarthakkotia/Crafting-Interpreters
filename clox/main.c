#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
    initVM();
    Chunk chunk;
    initChunk(&chunk);
    int constant = addConstant(&chunk, 1.2);
    writeChunk(&chunk, OP_CONSTANT, 1);
    writeChunk(&chunk, constant, 1);
    writeChunk(&chunk, OP_CONSTANT_LONG, 2);
    int constant2 = 255;
    writeChunk(&chunk, constant2, 2);
    writeChunk(&chunk, constant2, 2);
    writeChunk(&chunk, 127, 2);
    writeChunk(&chunk, OP_RETURN, 3);

    interpret(&chunk);

//    disassembleChunk(&chunk, "test");

    freeVM();
    freeChunk(&chunk);
    return 0;
}
