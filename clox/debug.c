#include <stdio.h>
#include "debug.h"

char* disassembleCode(int code){
    switch (code) {
        case 0: return "OP_RETURN";
    }
    return "CODE NOT FOUND";
}

void disassembleChunk(Chunk* chunk){
    printf("********disassembling chunk - Start*********\n");
    printf("chunk->capacity: %d\n", chunk->capacity);
    printf("chunk->count: %d\n", chunk->count);
    printf("********disassembling chunk code - Start*********\n");
    for(int i=0; i<chunk->count; i++){
        printf("chunk->code[%d]: %d: %s\n", i, chunk->code[i], disassembleCode(chunk->code[i]));
    }
    printf("********disassembling chunk code - End*********\n");
    printf("********disassembling chunk - End*********\n");
}
