#ifndef CLOX_OBJECT_H
#define CLOX_OBJECT_H

#include "common.h"
#include "value.h"

#define OBJ_TYPE(value) \
    (AS_OBJ(value)->type)

#define IS_STRING(object) \
    (isObjType(object, OBJ_STRING))

#define AS_STRING(value) \
    ((ObjString *)AS_OBJ(value))

#define AS_CSTRING(value) \
    (((ObjString *)AS_OBJ(value))->characters)

typedef enum {
    OBJ_STRING,
} ObjectType;

struct Obj {
    ObjectType type;
    struct Obj *next;
};

struct ObjString {
    Obj obj;
    int length;
    char characters[];
};

ObjString* takeString(char *characters, int length);
ObjString* copyString(const char *characters, int length);
void printObject(Value value);

static inline bool isObjType(Value object, ObjectType type) {
    return (IS_OBJ(object) && AS_OBJ(object)->type == type);
}

#endif //CLOX_OBJECT_H