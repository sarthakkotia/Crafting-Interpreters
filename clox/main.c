#include <stdlib.h>
#include <stdio.h>

#include "common.h"
#include "chunk.h"
#include "debug.h"
#include "vm.h"

static char *readFile(const char *filePath){
    FILE *filePtr = fopen(filePath, "rb");
    if(filePtr == NULL){
        fprintf(stderr, "Could not open file \"%s\". \n", filePath);
        exit(74);
    }

    fseek(filePtr, 0L, SEEK_END);
    size_t fileSize = ftell(filePtr);
    rewind(filePtr);

    char *buffer = (char *) malloc(fileSize + 1);
    if(buffer == NULL){
        fprintf(stderr, "Not enough memory to read \" %s \". \n", filePath);
        exit(74);
    }
    size_t bytesRead = fread(buffer, sizeof(char), fileSize, filePtr);
    if(bytesRead < fileSize){
        fprintf(stderr, "Could not read file \" %s \". \n", filePath);
        exit(74);
    }
    buffer[bytesRead] = '\0';

    fclose(filePtr);
    return buffer;
}

static void runFile(const char *file_path){
    char *source = readFile(file_path);
    InterpretResult result = interpret(source);
    free(source);

    if(result == INTERPRET_COMPILE_ERROR) exit(65);
    if(result == INTERPRET_RUNTIME_ERROR) exit(70);

}

static void repl(){
#define MAX_LENGTH 1024

    char line[MAX_LENGTH];
    while(true){
        printf("> ");
        if(fgets(line, sizeof(line), stdin) != NULL){
            //run line
            interpret(line);
        }else{
            printf("\n");
            break;
        }
    }
#undef MAX_LENGTH
}


int main(int argc, const char* argv[]) {
    initVM();
//    Chunk chunk;
//    initChunk(&chunk);

    if(argc > 2){
        fprintf(stderr, "Usage clox [path]\n");
        exit(64);
    }else if(argc == 2){
        runFile(argv[1]);
    }else{
        repl();
    }

//    interpret(&chunk);

//    disassembleChunk(&chunk, "test");

    freeVM();
//    freeChunk(&chunk);
    return 0;
}
