#ifndef CLOX_GLOBAL_IDENTIFIER_STACK_H
#define CLOX_GLOBAL_IDENTIFIER_STACK_H
#include "common.h"
#include "object.h"

typedef struct {
    ObjString *name;
    uint8_t index;
} identifier;

typedef struct {
    identifier stack[UINT8_MAX];
    int identifierCount;
} global_identifier_stack;

void initGlobalIdentifierStack(global_identifier_stack *global_identifier_stack);
void insertIdentifier(global_identifier_stack *global_identifier_stack, identifier identifier);
int lookupIdentifier(global_identifier_stack *global_identifier_stack, ObjString *identifier);

#endif //CLOX_GLOBAL_IDENTIFIER_STACK_H