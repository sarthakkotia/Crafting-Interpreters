#include <stdio.h>
#include "debug.h"
#include "value.h"

static int simpleInstruction(const char* name, int offset){
    printf("%s\n", name);
    return offset+1;
}
static int constantInstruction(const char* name, int offset, Chunk* chunk){
    uint8_t constant_idx = chunk->code[offset+1];
    printf("%-16s %4d '", name, constant_idx);
    printValue(chunk->constants.values[constant_idx]);
    printf("\n");
    return offset+2;
}
static int byteInstruction(const char *name, int offset, Chunk *chunk) {
    uint8_t index = chunk->code[offset+1];
    printf("%-16s %4d\n", name, index);
    return offset+2;
}

static int jumpInstruction(const char *name, int sign, Chunk *chunk, int offset) {
    uint16_t jump = (uint16_t) chunk->code[offset+1] << 8;
    jump = jump | chunk->code[offset + 2];
    printf("%-16s %4d -> %d\n", name, offset, offset + 3 + sign * jump);
    return offset + 3;
}

static int longConstantInstruction(const char* name, int offset, Chunk* chunk){
    uint8_t constant_idx1 = chunk->code[offset+1];
    uint8_t constant_idx2 = chunk->code[offset+2];
    uint8_t constant_idx3 = chunk->code[offset+3];
    uint32_t result = (constant_idx3 << 16) | (constant_idx2 << 8) | (constant_idx1);
    printf("%-16s %4d '", name, result);
    printValue(chunk->constants.values[result]);
    printf("\n");
    return offset+4;
}
int getLine(LinesArray* linesArray, int offset){
    int sum = 0;
    for(int i=0; i<linesArray->count; i++){
        sum += linesArray->lines[i];
        if(sum >= offset+1) return i+1;
    }
    return -1;
}
int disassembleInstruction(Chunk* chunk, int offset){
    printf("%04d ", offset);
    if(offset > 0 && getLine(&chunk->linesArray, offset) == getLine(&chunk->linesArray, offset-1)){
        printf("   | ");
    }else{
        printf("%04d ", getLine(&chunk->linesArray, offset));
    }
    uint8_t instruction = chunk->code[offset];
    switch (instruction) {
        case OP_RETURN:
            return simpleInstruction("OP_RETURN", offset);
        case OP_NEGATE:
            return simpleInstruction("OP_NEGATE", offset);
        case OP_ADD:
            return simpleInstruction("OP_ADD", offset);
        case OP_SUBTRACT:
            return simpleInstruction("OP_SUBTRACT", offset);
        case OP_MULTIPLY:
            return simpleInstruction("OP_MULTIPLY", offset);
        case OP_DIVIDE:
            return simpleInstruction("OP_DIVIDE", offset);
        case OP_NIL:
            return simpleInstruction("OP_NIL", offset);
        case OP_TRUE:
            return simpleInstruction("OP_TRUE", offset);
        case OP_FALSE:
            return simpleInstruction("OP_FALSE", offset);
        case OP_NOT:
            return simpleInstruction("OP_NOT", offset);
        case OP_EQUAL:
            return simpleInstruction("OP_EQUAL", offset);
        case OP_LESS:
            return simpleInstruction("OP_LESS", offset);
        case OP_GREATER:
            return simpleInstruction("OP_GREATER", offset);
        case OP_PRINT:
            return simpleInstruction("OP_PRINT", offset);
        case OP_POP:
            return simpleInstruction("OP_POP", offset);
        case OP_CONSTANT:
            return constantInstruction("OP_CONSTANT", offset, chunk);
        case OP_DEFINE_GLOBAL:
            return constantInstruction("OP_DEFINE_GLOBAL", offset, chunk);
        case OP_GET_GLOBAL:
            return constantInstruction("OP_GET_GLOBAL", offset, chunk);
        case OP_SET_GLOBAL:
            return constantInstruction("OP_SET_GLOBAL", offset, chunk);
        case OP_GET_LOCAL:
            return byteInstruction("OP_GET_LOCAL", offset, chunk);
        case OP_SET_LOCAL:
            return byteInstruction("OP_SET_LOCAL", offset, chunk);
        case OP_JUMP:
            return jumpInstruction("OP_JUMP", 1, chunk, offset);
        case OP_JUMP_IF_FALSE:
            return jumpInstruction("OP_JUMP_IF_FALSE", 1,  chunk, offset);
        case OP_LOOP:
            return jumpInstruction("OP_LOOP", -1, chunk, offset);
        case OP_CONSTANT_LONG:
            return longConstantInstruction("OP_CONSTANT_LONG", offset, chunk);
        default:
            printf("Unknown opcode %d\n", instruction);
            return offset+1;
    }
}

void disassembleChunk(Chunk* chunk, const char* name){
    printf("offset|lineNo|opcode...constant\n");
    printf("== %s ==\n", name);
    for(int i=0; i<chunk->count;){
        i = disassembleInstruction(chunk, i);
    }
    printf("\n");
}

