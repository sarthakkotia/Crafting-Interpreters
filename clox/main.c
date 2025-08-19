#include "common.h"
#include "chunk.h"
#include <stdlib.h>
#include "chunk.c"

int main(int argc, const char* argv[]) {
    Chunk* chunk = (Chunk*)malloc(sizeof (chunk));
    initChunk(chunk);
    writeChunk(chunk, 1);
    writeChunk(chunk, 2);
    writeChunk(chunk, 3);
    return 0;
}
