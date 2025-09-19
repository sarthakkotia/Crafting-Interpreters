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
    Chunk chunk;
    initChunk(&chunk);

    int constant = addConstant(&chunk, 4);
    writeChunk(&chunk, OP_CONSTANT, 1);
    writeChunk(&chunk, constant, 1);

    constant = addConstant(&chunk, 3);
    writeChunk(&chunk, OP_CONSTANT, 2);
    writeChunk(&chunk, constant, 2);
    constant = addConstant(&chunk, 2);
    writeChunk(&chunk, OP_CONSTANT, 2);
    writeChunk(&chunk, constant, 2);
    writeChunk(&chunk, OP_NEGATE, 2);
    writeChunk(&chunk, OP_MULTIPLY, 2);

    writeChunk(&chunk, OP_SUBTRACT, 3);

    writeChunk(&chunk, OP_RETURN, 4);

//    int constant = addConstant(&chunk, 1);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 2);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 3);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 4);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 5);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 6);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 7);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 8);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//
//    constant = addConstant(&chunk, 9);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 10);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 11);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 12);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 13);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 14);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 15);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 16);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);
//    constant = addConstant(&chunk, 17);
//    writeChunk(&chunk, OP_CONSTANT, 1);
//    writeChunk(&chunk, constant, 1);

//    writeChunk(&chunk, OP_RETURN, 2);

    interpretChunk(&chunk);

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

    freeChunk(&chunk);
    freeVM();
    return 0;
}
