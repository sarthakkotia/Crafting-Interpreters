#include "chunk.h"
#include <stdlib.h>
#include "string.h"

void initChunk(Chunk* chunk){
    chunk->count = 0;
    chunk->capacity = 0;
    chunk->code = NULL;
}

void writeChunk(Chunk* chunk, uint8_t byte){
    if(chunk->count == chunk->capacity){
        // array is full
        uint8_t* new_code = (uint8_t*)calloc((2*chunk->capacity + 1),(2*chunk->capacity + 1) * sizeof (uint8_t));
        if(new_code == NULL){
            printf("Memory allocation failed!");
            return;
        }
        if(chunk->code != NULL) memcpy(new_code, chunk->code, chunk->capacity*sizeof(uint8_t));
        chunk->code = new_code;
        chunk->capacity = 2*chunk->capacity + 1;
    }
    // array is not full
    chunk->code[chunk->count] = byte;
    chunk->count++;
}