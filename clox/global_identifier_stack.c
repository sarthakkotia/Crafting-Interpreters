#include "global_identifier_stack.h"

void initGlobalIdentifierStack(global_identifier_stack *global_identifier_stack) {
    global_identifier_stack->identifierCount = 0;
}

void insertIdentifier(global_identifier_stack *global_identifier_stack, identifier identifier) {
    global_identifier_stack->stack[global_identifier_stack->identifierCount] = identifier;
    global_identifier_stack->identifierCount = global_identifier_stack->identifierCount + 1;
}

int lookupIdentifier(global_identifier_stack *global_identifier_stack, ObjString *name) {
    for (int idx = global_identifier_stack->identifierCount; idx >= 0; idx = idx - 1) {
        if (global_identifier_stack->stack[idx].name == name ) {
            return global_identifier_stack->stack[idx].index;
        }
    }
    return -1;
}