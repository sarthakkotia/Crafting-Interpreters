#include <stdlib.h>

#include "memory.h"
#include "compiler.h"
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


void markObject(Obj *object) {
    if (object == NULL) return;
#ifdef DEBUG_LOG_GC
    printf("%p mark ", object);
    printValue(OBJ_VAL(object));
    printf("\n");
#endif

    object->isMarked = true;
}

void markValue(Value value) {
    if (IS_OBJ(value)) markObject(AS_OBJ(value));
}

void markTable(Table table) {
    for (int i = 0; i < table.capacity; i = i + 1) {
        Entry *entry = &table.entries[i];
        markObject((Obj *)entry->key);
        markValue(entry->value);
    }
}

static void markRoots() {
    for (Value *slot = vm.stack; slot < vm.stackTop; slot = slot + 1) {
        markValue(*slot);
    }

    for (int i = 0; i < vm.frameCount; i = i + 1) {
        CallFrame *frame = &vm.frames[i];
        markObject((Obj *)frame->closure);
    }

    ObjUpvalue *upvalue = vm.openUpvalues;
    while (upvalue != NULL) {
        markObject((Obj *)upvalue);
        upvalue = upvalue->next;
    }
    markTable(vm.globals);
    markCompilerRoots();
}

void collectGarbage() {

#ifdef DEBUG_LOG_GC
    printf("-- gc begin\n");
#endif

    markRoots();

#ifdef DEBUG_LOG_GC
    printf("-- gc end\n");
#endif

}
