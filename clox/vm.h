#ifndef clox_vm_h
#define clox_vm_h

#include "chunk.h"
#include "stack.h"
#include "table.h"

#define STACK_MAX 256

typedef struct{
    Chunk* chunk;
    uint8_t* ip; // instruction pointer / program counter
    VMStack vmStack;
    Table strings;
    Obj *objects;
}VM;

typedef enum{
    INTERPRET_OK,
    INTERPRET_COMPILE_ERROR,
    INTERPRET_RUNTIME_ERROR
}InterpretResult;

extern VM vm;

void initVM();
void freeVM();
InterpretResult interpretChunk(Chunk *chunk);
InterpretResult interpret(const char *source);
void push(Value value);
Value pop();

#endif //clox_vm_h
