#include <stdio.h>
#include <stdlib.h>
#include "debug.h"
#include "memory.h"
#include "vm.h"
#include "compiler.h"
#include "stack.h"

VM vm;

static void resetStack(){
    initVMStack(&vm.vmStack);
}

void initVM(){
    resetStack();
}

void freeVM(){
    freeVMStack(&vm.vmStack);
}

void push(Value value){
    pushVMStack(value, &vm.vmStack);
}

Value pop(){
    return popVMStack(&vm.vmStack);
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
        for(int i = 0; i<vm.vmStack.count; i++){
            printf("[ ");
            printValue(vm.vmStack.stack[i]);
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
                Value *value = &vm.vmStack.stack[vm.vmStack.count-1];
                *(value) = -(Value)(*(value));
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

InterpretResult interpret(const char* source){
    Chunk chunk;
    initChunk(&chunk);

    if(!compile(source, &chunk)){
        freeChunk(&chunk);
        return INTERPRET_COMPILE_ERROR;
    }
    vm.chunk = &chunk;
    vm.ip = vm.chunk->code;

    InterpretResult result = run();
    freeChunk(&chunk);
    return result;
}

InterpretResult interpretChunk(Chunk* chunk){
    vm.chunk = chunk;
    vm.ip = vm.chunk->code;
    return run();
}