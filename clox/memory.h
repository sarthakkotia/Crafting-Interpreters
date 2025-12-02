#ifndef clox_memory_h
#define clox_memory_h

#include "common.h"

#define ALLOCATE(type, count) \
    ((type *)reallocate(NULL, 0, sizeof(type) * count))

#define GROW_CAPACITY(capacity) \
    (capacity < 8 ? 8 : capacity*2)

#define GROW_ARRAY(type, pointer, oldCap, newCap) \
    ((type*)reallocate(pointer, sizeof(type)*oldCap, sizeof(type)*newCap))

#define GROW_ARRAY_WITH_ZERO(type, pointer, oldCap, newCap) \
    ((type*)reallocateWithZero(pointer, sizeof(type)*oldCap, sizeof(type)*newCap))

#define FREE_ARRAY(type, pointer, capacity) \
    (reallocate(pointer, sizeof(type)*capacity, 0))

void* reallocate(void* pointer, size_t oldSize, size_t newSize);
void* reallocateWithZero(void* pointer, size_t oldSize, size_t newSize);

#endif //clox_memory_h
