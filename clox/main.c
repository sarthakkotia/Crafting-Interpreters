#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

int main(int argc, const char* argv[]) {
    initVM();
    Chunk chunk;
    initChunk(&chunk);

    if(argc > 2){
        printf("Usage: jlox or jlox [script]");
        exit(1);
    }else if(argc == 2){
        //runfile
        printf("script\n");
    }else{
        printf("repl\n");
        //run
    }

    interpret(&chunk);

//    disassembleChunk(&chunk, "test");

    freeVM();
    freeChunk(&chunk);
    return 0;
}
