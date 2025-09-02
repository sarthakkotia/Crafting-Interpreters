#include <stdio.h>
#include <stdlib.h>
#include "debug.h"
#include "vm.h"
VM vm;

void initVM(){}

void freeVM(){}

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
        disassembleInstruction(vm.chunk, (int) (vm.ip - vm.chunk->code));
#endif
        uint8_t instruction = READ_BYTE();
        switch (instruction) {
            case OP_RETURN:{
                return INTERPRET_OK;
            }
            case OP_CONSTANT:{
                Value constant = READ_CONSTANT();
                printValue(constant);
                printf("\n");
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