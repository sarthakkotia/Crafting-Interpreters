#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
    initVM();
    Chunk chunk;
    initChunk(&chunk);
//    int constant = addConstant(&chunk, 1.2);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    writeChunk(&chunk, OP_NEGATE, 2);
//
//    constant = addConstant(&chunk, 3);
//    writeChunk(&chunk, OP_CONSTANT, 3);
//    writeChunk(&chunk, constant, 3);
//    constant = addConstant(&chunk, 2);
//    writeChunk(&chunk, OP_CONSTANT, 3);
//    writeChunk(&chunk, constant, 3);
//    writeChunk(&chunk, OP_SUBTRACT, 3);
    writeChunk(&chunk, OP_CONSTANT, 1);
    int constant = addConstant(&chunk, 2);
    writeChunk(&chunk, constant, 1);
    writeChunk(&chunk, OP_CONSTANT, 1);
    constant = addConstant(&chunk, 3);
    writeChunk(&chunk, constant, 1);
    writeChunk(&chunk, OP_MULTIPLY, 1);

    writeChunk(&chunk, OP_CONSTANT, 2);
    constant = addConstant(&chunk, 1);
    writeChunk(&chunk, constant, 2);
    writeChunk(&chunk, OP_ADD, 2);

    writeChunk(&chunk, OP_CONSTANT, 3);
    constant = addConstant(&chunk, 4);
    writeChunk(&chunk, constant, 3);
    writeChunk(&chunk, OP_SUBTRACT, 3);

    writeChunk(&chunk, OP_CONSTANT, 4);
    constant = addConstant(&chunk, 5);
    writeChunk(&chunk, constant, 4);
    writeChunk(&chunk, OP_NEGATE, 4);

    writeChunk(&chunk, OP_DIVIDE, 5);

    writeChunk(&chunk, OP_RETURN, 6);

    interpret(&chunk);

//    disassembleChunk(&chunk, "test");

    freeVM();
    freeChunk(&chunk);
    return 0;
}
