#include <string.h>

#include "object.h"
#include "memory.h"

#define ALLOCATE_OBJ(type, objectType) \
    ((type *)allocateObject(sizeof(type), objectType))

static Obj* allocateObject(size_t size, ObjectType objectType) {
    Obj *object = (Obj* )reallocate(NULL, 0, size);
    object->type = objectType;
    return object;
}

static ObjString* allocateString(char *heapChars, int length) {
    ObjString *string = ALLOCATE_OBJ(ObjString, OBJ_STRING);
    string->length = length;
    string->characters = heapChars;
    return string;
}

ObjString* copyString(const char *characters, int length) {
    char *heapChars = ALLOCATE(char, length + 1);
    memcpy(heapChars, characters, length);
    heapChars[length] = '\0';
    return allocateString(heapChars, length);
}