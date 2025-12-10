#include <string.h>
#include <stdio.h>

#include "object.h"
#include "memory.h"
#include "vm.h"

#define ALLOCATE_OBJ(type, objectType) \
    ((type *)allocateObject(sizeof(type), objectType))

#define ALLOCATE_OBJSTRING(type, objectType, length) \
    ((type *)allocateObject(sizeof(type) + length * sizeof(char), objectType))

static Obj* allocateObject(size_t size, ObjectType objectType) {
    Obj *object = (Obj* )reallocate(NULL, 0, size);
    object->type = objectType;
    object->next = vm.objects;
    vm.objects = object;
    return object;
}

/*
static ObjString* allocateString(char *heapChars, int length) {
    ObjString *string = ALLOCATE_OBJ(ObjString, OBJ_STRING);
    string->length = length;
    string->characters = heapChars;
    return string;
}
*/

static ObjString* allocateString(const char *characters, int length) {
    ObjString *string = ALLOCATE_OBJSTRING(ObjString, OBJ_STRING, length + 1);
    string->length = length;
    memcpy(string->characters, characters, length);
    string->characters[length] = '\0';
    return string;
}

ObjString* takeString(char *charactes, int length) {
    return allocateString(charactes, length);
}

/*
ObjString* copyString(const char *characters, int length) {
    char *heapChars = ALLOCATE(char, length + 1);
    memcpy(heapChars, characters, length);
    heapChars[length] = '\0';
    return allocateString(heapChars, length);
}
*/

ObjString* copyString(const char *characters, int length) {
    // char *heapChars = ALLOCATE(char, length + 1);
    // memcpy(heapChars, characters, length);
    // heapChars[length] = '\0';
    return allocateString(characters, length);
}

void printObject(Value value) {
    switch (OBJ_TYPE(value)) {
        case OBJ_STRING:
            printf("%s", AS_CSTRING(value));
            break;
    }
}