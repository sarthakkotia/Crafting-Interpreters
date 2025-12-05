#include <stdlib.h>
#include <string.h>

#include "memory.h"

#include "object.h"
#include "value.h"
#include "vm.h"

void* reallocate(void* pointer, size_t oldSize, size_t newSize) {
    if(newSize == 0){
        free(pointer);
        return NULL;
    }
    void* result = realloc(pointer, newSize);
    if(result == NULL) exit(1);
    return result;
}

void* reallocateWithZero(void* pointer, size_t oldSize, size_t newSize) {
    if(newSize == 0){
        free(pointer);
        return NULL;
    }
    void* result = realloc(pointer, newSize);
    if(result == NULL) exit(1);
    memset(result+oldSize, 0,newSize-oldSize);
    return result;
}

void freeObject(Obj *object) {
    switch (object->type) {
        case OBJ_STRING: {
            ObjString *objectString = (ObjString *)object;
            FREE_ARRAY(char, objectString->characters, objectString->length);
            FREE(ObjString, object);
            break;
        }
    }
}

void freeObjects() {
    Obj *object = vm.objects;
    while (object != NULL) {
        Obj *next = object->next;
        freeObject(object);
        object = next;
    }
}
