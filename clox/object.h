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

#define AS_FUNCTION(value) \
    ((ObjFunction *)AS_OBJ(value))

typedef enum {
    OBJ_STRING,
    OBJ_FUNCTION,
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
} ObjFunction;

struct ObjString {
    Obj obj;
    int length;
    char *characters;
    uint32_t hash;
};

ObjFunction* newFunction();
ObjString* takeString(char *characters, int length);
ObjString* copyString(const char *characters, int length);
void printObject(Value value);

static inline bool isObjType(Value object, ObjectType type) {
    return (IS_OBJ(object) && AS_OBJ(object)->type == type);
}

#endif //CLOX_OBJECT_H