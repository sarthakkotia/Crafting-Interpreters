#include "stack.h"
#include "memory.h"
#include "object.h"
#include "stdlib.h"

void initVMStack(VMStack *vmStack){
    vmStack->stack = NULL;
    vmStack->capacity = 0;
    vmStack->count = 0;
}

void pushVMStack(Value value, VMStack *vmStack){
    if(vmStack->count + 1 >= vmStack->capacity){
        int oldCapacity = vmStack->capacity;
        vmStack->capacity = GROW_CAPACITY(oldCapacity);
        vmStack->stack = GROW_ARRAY(Value, vmStack->stack, oldCapacity, vmStack->capacity);
    }
    vmStack->stack[vmStack->count] = value;
    vmStack->count++;
}

Value popVMStack(VMStack *vmStack){
    vmStack->count--;
    Value value = vmStack->stack[vmStack->count];
    return value;
}


void freeVMStack(VMStack *vmStack){
    FREE_ARRAY(Value, vmStack->stack, vmStack->capacity);
    initVMStack(vmStack);
}
