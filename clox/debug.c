#include <stdio.h>
#include "debug.h"

static void simpleInstruction(const char* name){
    printf("%s\n", name);
    return;
}

void disassembleInstruction(Chunk* chunk, int offset){
    printf("%04d ", offset);
    uint8_t instruction = chunk->code[offset];
    switch (instruction) {
        case OP_RETURN:
            return simpleInstruction("OP_RETURN");
        default:
            printf("Unknown opcode %d\n", instruction);
    }
}

void disassembleChunk(Chunk* chunk, const char* name){
    printf("== %s ==\n", name);
    for(int i=0; i<chunk->count; i++){
        disassembleInstruction(chunk, i);
    }
}

