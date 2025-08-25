#ifndef clox_value_h
#define clox_value_h

#include "common.h"

typedef double Value;

typedef struct{
    int capacity;
    int count;
    Value* values;
} ValueArray;

void initValueArray(ValueArray* valueArray);
void writeValueArray(ValueArray* valueArray, Value value);
void freeValueArray(ValueArray* valueArray);
void printValue(Value value);

#endif //clox_value_h
