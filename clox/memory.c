#include <stdlib.h>
#include "memory.h"

#include "object.h"
#include "value.h"
#include "vm.h"
#ifdef DEBUG_LOG_GC
#include <stdio.h>
#include "debug.h"
#endif


void* reallocate(void* pointer, size_t oldSize, size_t newSize) {
    if (newSize > oldSize) {
#ifdef DEBUG_STRESS_GC
        collectGarbage();
#endif
    }
    if(newSize == 0){
        free(pointer);
        return NULL;
    }
    void* result = realloc(pointer, newSize);
    if(result == NULL) exit(1);
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
        case OBJ_FUNCTION: {
            ObjFunction *objFunction = (ObjFunction *)object;
            freeChunk(&objFunction->chunk);
            FREE(ObjFunction, object);
            break;
        }
        case OBJ_NATIVE: {
            FREE(ObjNative, object);
            break;
        }
        case OBJ_CLOSURE: {
            ObjClosure *closure = (ObjClosure *)object;
            FREE_ARRAY(ObjUpvalue, closure->upvalues, closure->upvalueCount);
            FREE(ObjClosure, object);
            break;
        }
        case OBJ_UPVALUE: {
            FREE(ObjUpvalue, object);
            break;
        }
    }
}

void freeObjects() {
    Obj *object = vm.objects;
#ifdef DEBUG_LOG_GC
    printf("%p free type %d\n", object, object->type);
#endif
    while (object != NULL) {
        Obj *next = object->next;
        freeObject(object);
        object = next;
    }
}

void collectGarbage() {

#ifdef DEBUG_LOG_GC
    printf("-- gc begin\n");
#endif

#ifdef DEBUG_LOG_GC
    printf("-- gc end\n");
#endif

}
