#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "debug.h"
#include "memory.h"
#include "vm.h"

#include <time.h>

#include "compiler.h"

VM vm;

static Value clockNative(int argCount, Value *args) {
    return NUMBER_VAL((double) clock() / CLOCKS_PER_SEC);
}

static void resetStack() {
    vm.stackTop = vm.stack;
    vm.frameCount = 0;
}

static void runtimeError(const char *msg, ...) {
    va_list args;
    va_start(args, msg);
    vfprintf(stderr, msg, args);
    va_end(args);
    fputs("\n", stderr);

    for (int i = vm.frameCount - 1; i >= 0; i--) {
        CallFrame *frame = &vm.frames[i];
        ObjFunction *function = frame->function;
        size_t instruction = frame->ip - frame->function->chunk.code - 1;
        int line = frame->function->chunk.lines[instruction];
        fprintf(stderr, "[line %d] in ", line);
        if (function->name == NULL) {
            fprintf(stderr, "script\n");
        } else {
            char *name = frame->function->name->characters;
            fprintf(stderr, "%s()\n", name);
        }
    }
    resetStack();
}

static void defineNative(const char *name, NativeFn function) {
    push(OBJ_VAL(copyString(name, (int)strlen(name))));
    push(OBJ_VAL(newNative(function)));
    tableSet(&vm.globals, AS_STRING(vm.stack[0]), vm.stack[1]);
    pop();
    pop();
}

void initVM() {
    resetStack();
    vm.frameCount = 0;
    vm.objects = NULL;
    initTable(&vm.strings);
    initTable(&vm.globals);

    defineNative("clock", clockNative);
}

void freeVM() {
    freeObjects();
    freeTable(&vm.strings);
    freeTable(&vm.globals);
}

void push(Value value) {
    *vm.stackTop = value;
    vm.stackTop++;
}

Value pop() {
    vm.stackTop--;
    return *vm.stackTop;
}

Value peek(int distance) {
    return vm.stackTop[-1 - distance];
}

static bool call(ObjFunction *function, int argCount) {
    if (argCount != function->arity) {
        runtimeError("Expect %d arguments, but got %d", function->arity, argCount);
        return false;
    }
    if (vm.frameCount == FRAMES_MAX) {
        runtimeError("Stack Overflow");
        return false;
    }
    CallFrame *frame = &vm.frames[vm.frameCount++];
    frame->function = function;
    frame->ip = function->chunk.code;
    frame->slots = vm.stackTop - argCount - 1;
    return true;
}

static bool callValue(Value callee, int argCount) {
    if (IS_OBJ(callee)) {
        switch (OBJ_TYPE(callee)) {
            case OBJ_FUNCTION:
                return call(AS_FUNCTION(callee), argCount);
            case OBJ_NATIVE: {
                NativeFn native = AS_NATIVE(callee);
                Value result = native(argCount, vm.stackTop-argCount);
                vm.stackTop = vm.stackTop - argCount - 1;
                push(result);
                return true;
            }
            default:
                break;
        }
    }
    runtimeError("Can ony call functions and classes");
    return false;
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
    register uint8_t *ip;
    CallFrame *frame = &vm.frames[vm.frameCount - 1];
    ip = frame->ip;

#define READ_BYTE() ({\
    uint8_t bytecode = *(ip); \
    ip++;\
    bytecode;\
})
#define READ_CONSTANT()({\
    uint8_t constantIndex = READ_BYTE();\
    frame->function->chunk.constants.values[constantIndex];\
})
#define READ_SHORT()({\
    uint16_t jump = (uint16_t)((uint8_t)(*ip)) << 8 | ((uint8_t)(*(ip + 1)));\
    ip = ip + 2;\
    jump;\
})
#define READ_STRING()({\
    AS_STRING(READ_CONSTANT());\
})
#define READ_LONG_CONSTANT()({\
    uint8_t index1 = READ_BYTE();\
    uint8_t index2 = READ_BYTE();\
    uint8_t index3 = READ_BYTE();\
    uint32_t result = index3<<16 | index2<<8 | index1;\
    frame->function->chunk.constants.values[result];\
})
#define BINARY_OPERATION(valueType, operator)({\
    if(!IS_NUMBER(peek(0)) || !IS_NUMBER(peek(1))){\
        frame->ip = ip;\
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
        for(Value *slot = vm.stack; slot < vm.stackTop; slot++){
            printf("[ ");
            printValue(*slot);
            printf(" ]");
        }
        printf("\n*****************************\n");
        disassembleInstruction(&frame->function->chunk, (int) (frame->ip - frame->function->chunk.code));
        // disassembleInstruction(&frame->function->chunk, (int) (ip - frame->function->chunk.code));
#endif
        uint8_t instruction = READ_BYTE();
        switch (instruction) {
            case OP_RETURN: {
                Value result = pop();
                vm.frameCount = vm.frameCount - 1;
                if (vm.frameCount == 0) {
                    pop();
                    return INTERPRET_OK;
                }

                vm.stackTop = frame->slots;
                push(result);
                frame = &vm.frames[vm.frameCount - 1];
                ip = frame->ip;
                break;
            }
            //unary operations
            case OP_NEGATE: {
                if (!IS_NUMBER(peek(0))) {
                    frame->ip = ip;
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
            case OP_PRINT: {
                printf("\n");
                printValue(pop());
                printf("\n");
                break;
            }
            case OP_POP:
                pop(); break;
            case OP_CONSTANT: {
                Value constant = READ_CONSTANT();
                push(constant);
                break;
            }
            case OP_DEFINE_GLOBAL: {
                ObjString *name = READ_STRING();
                tableSet(&vm.globals, name, peek(0));
                pop();
                break;
            }
            case OP_GET_GLOBAL: {
                ObjString *name = READ_STRING();
                Value value;
                if (!tableGet(&vm.globals, name, &value)) {
                    frame->ip = ip;
                    runtimeError("Undefined variable '%s'.", name->characters);
                    return INTERPRET_COMPILE_ERROR;
                }
                push(value);
                break;
            }
            case OP_SET_GLOBAL: {
                ObjString *name = READ_STRING();
                if (tableSet(&vm.globals, name, peek(0))) {
                    tableDelete(&vm.globals, name);
                    frame->ip = ip;
                    runtimeError("Undefined variable '%s'.", name->characters);
                    return INTERPRET_COMPILE_ERROR;
                }
                break;
            }
            case OP_GET_LOCAL: {
                uint8_t index = READ_BYTE();
                push(frame->slots[index]);
                break;
            }
            case OP_SET_LOCAL: {
                uint8_t index = READ_BYTE();
                frame->slots[index] = peek(0);
                break;
            }
            case OP_JUMP: {
                uint16_t offset = READ_SHORT();
                ip += offset;
                break;
            }
            case OP_JUMP_IF_FALSE: {
                uint16_t offset = READ_SHORT();
                if (!isTruthy(peek(0))) ip += offset;
                break;
            }
            case OP_LOOP: {
                uint16_t offset = READ_SHORT();
                ip -= offset;
                break;
            }
            case OP_CONSTANT_LONG: {
                Value longConstant = READ_LONG_CONSTANT();
                push(longConstant);
                break;
            }
            case OP_CALL: {
                uint8_t argCount = READ_BYTE();
                frame->ip = ip;
                if (!callValue(peek(argCount), argCount)) {
                    return INTERPRET_RUNTIME_ERROR;
                }
                frame = &vm.frames[vm.frameCount - 1];
                ip = frame->ip;
                break;
            }
        }

    }

#undef BINARY_OPERATION
#undef READ_LONG_CONSTANT
#undef READ_STRING
#undef READ_SHORT
#undef READ_CONSTANT
#undef READ_BYTE
}

InterpretResult interpret(const char* source) {
    ObjFunction *function = compile(source);
    if (function == NULL) return INTERPRET_COMPILE_ERROR;

    push(OBJ_VAL(function));
    call(function, 0);

    return run();
}