#include "global_identifier_stack.h"

#include <string.h>

#include "scanner.h"

void initGlobalIdentifierStack (global_identifier_stack *global_identifier_stack) {
    global_identifier_stack->identifierCount = 0;
}

static void insertIdentifier (global_identifier_stack *global_identifier_stack, identifier identifier) {
    global_identifier_stack->stack[global_identifier_stack->identifierCount] = identifier;
    global_identifier_stack->identifierCount = global_identifier_stack->identifierCount + 1;
}

int lookupIdentifier (global_identifier_stack *global_identifier_stack, Token name) {
    for (int idx = global_identifier_stack->identifierCount; idx > 0; idx = idx - 1) {
        if (memcmp(name.start, global_identifier_stack->stack[idx - 1].name.start, name.length) == 0) {
            return global_identifier_stack->stack[idx].index;
        }
    }
    return -1;
}

int getIndex(global_identifier_stack *global_identifier_stack, int index) {
    return global_identifier_stack->stack[index].index;
}

void insertName(global_identifier_stack *global_identifier_stack, Token name, int index) {
    identifier identifier;
    identifier.name = name;
    identifier.index = index;
    insertIdentifier(global_identifier_stack, identifier);
}