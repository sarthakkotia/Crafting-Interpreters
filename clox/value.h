#ifndef clox_value_h
#define clox_value_h

#include "common.h"

typedef struct Obj Obj;
typedef struct ObjString ObjString;

typedef enum {
    VAL_BOOL,
    VAL_NUMBER,
    VAL_NIL,
    VAL_OBJ,
} ValueType;

typedef struct {
    ValueType type;
    uint32_t hash;
    union {
        bool boolean;
        double number;
        Obj *obj;
    } as;
} Value;

#define IS_BOOL(value) \
    ((value).type == VAL_BOOL)

#define IS_NIL(value) \
    ((value).type == VAL_NIL)

#define IS_NUMBER(value) \
    ((value).type == VAL_NUMBER)

#define IS_OBJ(value) \
    ((value).type == VAL_OBJ)

#define AS_BOOL(value) \
    ((value).as.boolean)

#define AS_NUMBER(value) \
    ((value).as.number)

#define AS_OBJ(value) \
    ((value).as.obj)

#define BOOL_VAL(value) \
    ((Value){VAL_BOOL, (value == true) ? 1 : 2, {.boolean = value}})

#define NUMBER_VAL(value, hash) \
    ((Value){VAL_NUMBER, hash, {.number = value}})

#define NIL_VAL \
    ((Value){VAL_NIL, 0, {.number = 0}})

#define OBJ_VAL(value) \
    ((Value){VAL_OBJ, 0, {.obj = (Obj *)value}})

typedef struct{
    int capacity;
    int count;
    Value* values;
} ValueArray;

void initValueArray(ValueArray* valueArray);
void writeValueArray(ValueArray* valueArray, Value value);
void freeValueArray(ValueArray* valueArray);
void printValue(Value value);
bool valuesEquals(Value a, Value b);
uint32_t hashNumber(double number);


#endif //clox_value_h
