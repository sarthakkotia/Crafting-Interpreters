#ifndef CLOX_OBJECT_H
#define CLOX_OBJECT_H

#include "common.h"
#include "value.h"
#include "chunk.h"

#define OBJ_TYPE(value) \
    (AS_OBJ(value)->type)

#define IS_STRING(object) \
    (isObjType(object, OBJ_STRING))

#define AS_STRING(value) \
    ((ObjString *)AS_OBJ(value))

#define AS_CSTRING(value) \
    (((ObjString *)AS_OBJ(value))->characters)

#define IS_FUNCTION(object) \
    (isObjType(object, OBJ_FUNCTION))

#define IS_NATIVE(object) \
    (isObjType(object,OBJ_NATIVE))

#define IS_CLOSURE(object) \
    (isObjType((object, OBJ_CLOSURE)))

#define AS_FUNCTION(value) \
    ((ObjFunction *)AS_OBJ(value))

#define AS_NATIVE(value) \
    (((ObjNative *)AS_OBJ(value))->function)

#define AS_CLOSURE(value) \
    ((ObjClosure *)AS_OBJ(value))


typedef enum {
    OBJ_STRING,
    OBJ_FUNCTION,
    OBJ_NATIVE,
    OBJ_CLOSURE,
    OBJ_UPVALUE,
} ObjectType;

struct Obj {
    ObjectType type;
    struct Obj *next;
};

typedef struct {
    Obj obj;
    int arity; /* no. of parameters */
    Chunk chunk;
    ObjString *name;
    int upvalueCount;
} ObjFunction;

typedef struct ObjUpvalue {
    Obj obj;
    Value *location;
} ObjUpvalue;

typedef struct {
    Obj obj;
    ObjFunction *function;
    ObjUpvalue **upvalues;
    int upvalueCount;
} ObjClosure;


typedef Value (*NativeFn)(int argCount, Value *args);

typedef struct {
    Obj obj;
    NativeFn function;
} ObjNative;

struct ObjString {
    Obj obj;
    int length;
    char *characters;
    uint32_t hash;
};


ObjFunction* newFunction();
ObjNative* newNative(NativeFn function);
ObjClosure* newClosure(ObjFunction* function);
ObjString* takeString(char *characters, int length);
ObjString* copyString(const char *characters, int length);
ObjUpvalue* newUpvalue(Value *slot);
void printObject(Value value);

static inline bool isObjType(Value object, ObjectType type) {
    return (IS_OBJ(object) && AS_OBJ(object)->type == type);
}

#endif //CLOX_OBJECT_H