#include <string.h>
#include <stdio.h>

#include "object.h"
#include "memory.h"
#include "vm.h"

#define ALLOCATE_OBJ(type, objectType) \
    ((type *)allocateObject(sizeof(type), objectType))

static Obj* allocateObject(size_t size, ObjectType objectType) {
    Obj *object = (Obj* )reallocate(NULL, 0, size);
    object->type = objectType;
    object->next = vm.objects;
    vm.objects = object;
    return object;
}

static ObjString* allocateString(char *heapChars, int length, uint32_t hash) {
    ObjString *string = ALLOCATE_OBJ(ObjString, OBJ_STRING);
    string->length = length;
    string->characters = heapChars;
    string->hash = hash;
    tableSet(&vm.strings, string, NIL_VAL);
    return string;
}

static void printFunction(ObjFunction *objFunction) {
    printf("<fn %s>", objFunction->name->characters);
}

uint32_t hashString(const char *key, int length) {
    /* FNV-1a algorithm */
    uint32_t hash = 2166136261u;
    for (int i = 0; i < length; i++) {
        hash ^= (uint8_t) key[i];
        hash *= 16777619;
    }
    return hash;
}

ObjFunction* newFunction() {
    ObjFunction *function = ALLOCATE_OBJ(ObjFunction, OBJ_FUNCTION);
    function->arity = 0;
    function->name = NULL;
    initChunk(&function->chunk);

    return function;
}

ObjString* takeString(char *characters, int length) {
    uint32_t hash = hashString(characters, length);
    ObjString *interned = tableFindString(&vm.strings, characters, length, hash);
    if (interned != NULL) {
        FREE_ARRAY(char, characters, length + 1);
        return interned;
    }
    return allocateString(characters, length, hash);
}

ObjString* copyString(const char *characters, int length) {
    uint32_t hash = hashString(characters, length);
    ObjString *interned = tableFindString(&vm.strings, characters, length, hash);
    if (interned != NULL) {
        return interned;
    }
    char *heapChars = ALLOCATE(char, length + 1);
    memcpy(heapChars, characters, length);
    heapChars[length] = '\0';
    return allocateString(heapChars, length, hash);
}

void printObject(Value value) {
    switch (OBJ_TYPE(value)) {
        case OBJ_STRING:
            printf("%s", AS_CSTRING(value));
            break;
        case OBJ_FUNCTION:
            printFunction(AS_FUNCTION(value));
            break;
    }
}