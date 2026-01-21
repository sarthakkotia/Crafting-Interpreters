#ifndef CLOX_GLOBAL_IDENTIFIER_STACK_H
#define CLOX_GLOBAL_IDENTIFIER_STACK_H
#include "common.h"
#include "scanner.h"

typedef struct {
    Token name;
    uint8_t index;
} identifier;

typedef struct {
    identifier stack[UINT8_MAX];
    int identifierCount;
} global_identifier_stack;

void initGlobalIdentifierStack(global_identifier_stack *global_identifier_stack);
static void insertIdentifier(global_identifier_stack *global_identifier_stack, identifier identifier);
int lookupIdentifier(global_identifier_stack *global_identifier_stack, Token name);
int getIndex(global_identifier_stack *global_identifier_stack, int index);
void insertName(global_identifier_stack *global_identifier_stack, Token name, int index);

#endif //CLOX_GLOBAL_IDENTIFIER_STACK_H