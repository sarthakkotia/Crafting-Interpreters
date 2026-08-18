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
    vm.bytesAllocated = vm.bytesAllocated + newSize - oldSize;
    if (newSize > oldSize) {
#ifdef DEBUG_STRESS_GC
        collectGarbage();
#endif
        if (vm.bytesAllocated >= vm.nextGC) collectGarbage();
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
        case OBJ_CLASS: {
            FREE(ObjClass, obkect);
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
    free(vm.grayStack);
}


void markObject(Obj *object) {
    if (object == NULL) return;
    if (object->isMarked) return;
#ifdef DEBUG_LOG_GC
    printf("%p mark ", object);
    printValue(OBJ_VAL(object));
    printf("\n");
#endif

    object->isMarked = true;

    if (vm.grayCapacity < vm.grayCount + 1) {
        vm.grayCapacity = GROW_CAPACITY(vm.grayCapacity);
        vm.grayStack = (Obj **)realloc(vm.grayStack, sizeof(Obj*) * vm.grayCapacity);
        if (vm.grayStack == NULL) exit(1);
    }
    vm.grayStack[vm.grayCount] = object;
    vm.grayCount = vm.grayCount + 1;
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

static void markArray(ValueArray *array) {
    for (int i = 0; i < array->count; i = i + 1) {
        markValue(array->values[i]);
    }
}

static void blackenObject(Obj *obj) {
#ifdef DEBUG_LOG_GC
    printf("%p blacken ", (void *)obj);
    printValue(OBJ_VAL(obj));
    printf("\n");
#endif

    switch (obj->type) {
        case OBJ_NATIVE:
        case OBJ_STRING:
            break;
        case OBJ_UPVALUE: {
            ObjUpvalue *upvalue = (ObjUpvalue *)obj;
            markValue(upvalue->closed);
            break;
        }
        case OBJ_FUNCTION: {
            ObjFunction *function = (ObjFunction *)obj;
            markObject((Obj *)function->name);
            markArray(&function->chunk.constants);
            break;
        }
        case OBJ_CLOSURE: {
            ObjClosure *closure = (ObjClosure *)obj;
            markObject((Obj *)closure->function);
            for (int i = 0; i < closure->upvalueCount; i = i + 1) {
                markObject((Obj *)closure->upvalues[i]);
            }
            break;
        }
        case OBJ_CLASS: {
            ObjClass *class = (ObjClass *)obj;
            markObject((Obj *)class->name);
            break;
        }
    }
}

static void trackReferences() {
    while (vm.grayCount > 0) {
        Obj *obj = vm.grayStack[vm.grayCount];
        vm.grayCount = vm.grayCount - 1;
        blackenObject(obj);
        while (obj != NULL) {
            if (!obj->isMarked) markObject(obj);
            obj = obj->next;
        }
    }
}

static void sweep() {
    Obj *obj = vm.objects;
    Obj *previous = NULL;
    while (obj != NULL) {
        if (obj->isMarked) {
            obj->isMarked = false;
            previous = obj;
            obj = obj->next;
        } else {
            Obj *unreachable = obj;
            obj = obj->next;
            if (previous == NULL) {
                vm.objects = obj;
            } else {
                previous->next = obj;
            }
            freeObject(unreachable);
        }
    }
}

static void tableRemoveWhite(Table *table) {
    for (int i = 0; i < table->count; i = i + 1) {
        Entry *entry = &table->entries[i];
        if (entry->key != NULL && !entry->key->obj.isMarked) tableDelete(table, entry->key);
    }
}

void collectGarbage() {

#ifdef DEBUG_LOG_GC
    printf("-- gc begin\n");
    size_t before = vm.bytesAllocated;
#endif

    markRoots();
    trackReferences();
    tableRemoveWhite(&vm.strings);
    sweep();

    vm.nextGC = vm.bytesAllocated * GC_HEAP_GROW_FACTOR;

#ifdef DEBUG_LOG_GC
    printf("-- gc end\n");
    printf("collected %zu bytes from (%zu to %zu) next at %zu", before - vm.bytesAllocated, before, vm.bytesAllocated, vm.nextGC);
#endif

}
