#include <stdio.h>
#include <stdlib.h>
#include "debug.h"
#include "memory.h"
#include "vm.h"
VM vm;

static void resetStack(){
    vm.stackTop = vm.stack;
}

void initVM(){
    resetStack();
}

void freeVM(){}

void push(Value value){
    *(vm.stackTop) = value;
    vm.stackTop++;
}

Value pop(){
    vm.stackTop--;
    Value value = *(vm.stackTop);
    return value;
}


static InterpretResult run(){
#define READ_BYTE() ({\
    uint8_t bytecode = *(vm.ip); \
    vm.ip++;\
    bytecode;\
})
#define READ_CONSTANT()({ \
    uint8_t constantIndex = READ_BYTE();\
    vm.chunk->constants.values[constantIndex];\
})

    for(;;){
#ifdef DEBUG_TRACE_EXECUTION
        printf("******** Stack trace ********\n");
        for(Value* slot = vm.stack; slot<vm.stackTop; slot++){
            printf("[ ");
            printValue(*slot);
            printf(" ]");
        }
        printf("\n*****************************\n");
        disassembleInstruction(vm.chunk, (int) (vm.ip - vm.chunk->code));
#endif
        uint8_t instruction = READ_BYTE();
        switch (instruction) {
            case OP_RETURN:{
                printValue(pop());
                printf("\n");
                return INTERPRET_OK;
            }
            case OP_CONSTANT:{
                Value constant = READ_CONSTANT();
                push(constant);
                break;
            }
            
        }

    }

#undef READ_BYTE
#undef READ_CONSTANT
}

InterpretResult interpret(Chunk* chunk){
    vm.chunk = chunk;
    vm.ip = vm.chunk->code;
    return run();
}