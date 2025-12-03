#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "debug.h"
#include "memory.h"
#include "vm.h"
#include "compiler.h"
#include "stack.h"

VM vm;

static void resetStack() {
    initVMStack(&vm.vmStack);
}

static void runtimeError(const char *msg, ...) {
    va_list args;
    va_start(args, msg);
    vfprintf(stderr, msg, args);
    va_end(args);
    fputs("\n", stderr);

    size_t instruction = vm.ip - vm.chunk->code - 1;
    int line = vm.chunk->linesArray.lines[instruction];
    fprintf(stderr, "[line %d] in script\n", line);
    resetStack();
}

void initVM() {
    resetStack();
}

void freeVM() {
    freeVMStack(&vm.vmStack);
}

void push(Value value) {
    pushVMStack(value, &vm.vmStack);
}

Value pop() {
    return popVMStack(&vm.vmStack);
}

Value peek(int distance) {
    return vm.vmStack.stack[vm.vmStack.count - distance - 1];
}

static bool isTruthy(Value value) {
    if (IS_NIL(value) || (IS_BOOL(value) && !AS_BOOL(value))) return false;
    return true;
}

static void concatenate() {
    ObjString *right = AS_STRING(pop());
    ObjString *left = AS_STRING(pop());
    int length = right->length + left->length;
    char *result = ALLOCATE(char, length + 1);
    memcpy(result, left->characters, left->length);
    memcpy(result + left->length, right->characters, right->length);
    result[length] = '\0';

    ObjString *answer = takeString(result, length);
    push(OBJ_VAL(answer));
}

static InterpretResult run() {
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
#define BINARY_OPERATION(valueType, operator)({\
    if(!IS_NUMBER(peek(0)) || !IS_NUMBER(peek(1))){\
        runtimeError("Operands must be numbers");\
        return INTERPRET_RUNTIME_ERROR;\
    }\
    double right = AS_NUMBER(pop());\
    double left = AS_NUMBER(pop());\
    push(valueType(left operator right));\
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
            case OP_RETURN: {
                printValue(pop());
                printf("\n");
                return INTERPRET_OK;
            }
            //unary operations
            case OP_NEGATE: {
                if (!IS_NUMBER(peek(0))) {
                    runtimeError("Operand must be a number.");
                    return INTERPRET_RUNTIME_ERROR;
                }
                push(NUMBER_VAL(-AS_NUMBER(pop())));
                break;
            }
            //binary operations
            case OP_ADD: {
                if (IS_STRING(peek(0)) && IS_STRING(peek(1))) {
                    concatenate();
                }else {
                    BINARY_OPERATION(NUMBER_VAL, +);
                }
                break;
            }
            case OP_SUBTRACT: {
                BINARY_OPERATION(NUMBER_VAL, -);
                break;
            }
            case OP_MULTIPLY: {
                BINARY_OPERATION(NUMBER_VAL, *);
                break;
            }
            case OP_DIVIDE: {
                BINARY_OPERATION(NUMBER_VAL, /);
                break;
            }
            case OP_NIL: {
                push(NIL_VAL);
                break;
            }
            case OP_TRUE: {
                push(BOOL_VAL(true));
                break;
            }
            case OP_FALSE: {
                push(BOOL_VAL(false));
                break;
            }
            case OP_NOT: {
                push(BOOL_VAL(!isTruthy(pop())));
                break;
            }
            case OP_EQUAL: {
                Value b = pop();
                Value a = pop();
                push(BOOL_VAL(valuesEquals(a, b)));
                break;
            }
            case OP_LESS: {
                BINARY_OPERATION(BOOL_VAL, <);
                break;
            }
            case OP_GREATER: {
                BINARY_OPERATION(BOOL_VAL, >);
                break;
            }
            case OP_CONSTANT: {
                Value constant = READ_CONSTANT();
                push(constant);
                break;
            }
            case OP_CONSTANT_LONG: {
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

InterpretResult interpret(const char* source) {
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

InterpretResult interpretChunk(Chunk* chunk) {
    vm.chunk = chunk;
    vm.ip = vm.chunk->code;
    return run();
}