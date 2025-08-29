#include <stdlib.h>
#include "chunk.h"
#include "memory.h"

void initLinesArray(LinesArray* linesArray){
    linesArray->count = 0;
    linesArray->capacity = 0;
    linesArray->lines = NULL;
}

void initChunk(Chunk* chunk){
    chunk->count = 0;
    chunk->capacity = 0;
    chunk->code = NULL;
    initValueArray(&chunk->constants);
    initLinesArray(&chunk->linesArray);
}

void writeLinesArray(LinesArray* linesArray, int line){
    if(linesArray->capacity == linesArray->count){
        int oldCapacity = linesArray->capacity;
        linesArray->capacity = GROW_CAPACITY(oldCapacity);
        linesArray->lines = GROW_ARRAY_WITH_ZERO(int, linesArray->lines, oldCapacity, linesArray->capacity);
    }
    linesArray->lines[line-1]++;
    linesArray->count++;
}

void writeChunk(Chunk* chunk, uint8_t byte, int line){
    if(chunk->capacity == chunk->count){
        int oldCapacity = chunk->capacity;
        chunk->capacity = GROW_CAPACITY(oldCapacity);
        chunk->code = GROW_ARRAY(uint8_t, chunk->code, oldCapacity, chunk->capacity);
//        chunk->lines = GROW_ARRAY(int, chunk->lines, oldCapacity, chunk->capacity);
    }
    chunk->code[chunk->count] = byte;
    writeLinesArray(&chunk->linesArray, line);
//    chunk->lines[chunk->count] = line;
    chunk->count++;
}

void freeLinesArray(LinesArray* linesArray){
    FREE_ARRAY(int, linesArray->lines, linesArray->capacity);
    initLinesArray(linesArray);
}

void freeChunk(Chunk* chunk){
    freeValueArray(&chunk->constants);
    FREE_ARRAY(uint8_t, chunk->code, chunk->capacity);
    freeLinesArray(&chunk->linesArray);
    initChunk(chunk);
}

int addConstant(Chunk* chunk, Value value){
    writeValueArray(&chunk->constants, value);
    return chunk->constants.count-1;
}