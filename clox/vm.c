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
#define READ_CONSTANT()({\
    uint8_t constantIndex = READ_BYTE();\
    vm.chunk->constants.values[constantIndex];\
})
#define READ_LONG_CONSTANT()({\
    uint8_t index1 = READ_BYTE();\
    uint8_t index2 = READ_BYTE();\
    uint8_t index3 = READ_BYTE();\
    uint32_t result = index3<<16 | index2<<8 | index1;\
    vm.chunk->constants.values[result];\
})
#define BINARY_OPERATION(operator)({\
    Value right = pop();\
    Value left = pop();\
    left operator right;\
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
            //unary operations
            case OP_NEGATE:{
                Value value = -pop();
                push(value);
                break;
            }
            //binary operations
            case OP_ADD:{
                Value value = BINARY_OPERATION(+);
                push(value);
                break;
            }
            case OP_SUBTRACT:{
                Value value = BINARY_OPERATION(-);
                push(value);
                break;
            }
            case OP_MULTIPLY:{
                Value value = BINARY_OPERATION(*);
                push(value);
                break;
            }
            case OP_DIVIDE:{
                Value value = BINARY_OPERATION(/);
                push(value);
                break;
            }
            case OP_CONSTANT:{
                Value constant = READ_CONSTANT();
                push(constant);
                break;
            }
            case OP_CONSTANT_LONG:{
                Value longConstant = READ_LONG_CONSTANT();
                push(longConstant);
                break;
            }
            
        }

    }

#undef BINARY_OPERATION
#undef READ_LONG_CONSTANT
#undef READ_CONSTANT
#undef READ_BYTE
}

InterpretResult interpret(Chunk* chunk){
    vm.chunk = chunk;
    vm.ip = vm.chunk->code;
    return run();
}