#include "value.h"

#ifndef clox_stack_h
#define clox_stack_h

typedef struct {
    Value *stack;
    int capacity;
    int count;
}VMStack;

void initVMStack(VMStack *vmStack);
void freeVMStack(VMStack *vmStack);
void pushVMStack(Value value, VMStack *vmStack);
Value popVMStack(VMStack *vmStack);

#endif //clox_stack_h
