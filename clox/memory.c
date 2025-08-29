#include <stdlib.h>
#include <string.h>
#include "memory.h"

void* reallocate(void* pointer, size_t oldSize, size_t newSize){
    if(newSize == 0){
        free(pointer);
        return NULL;
    }
    void* result = realloc(pointer, newSize);
    if(result == NULL) exit(1);
    return result;

}

void* reallocateWithZero(void* pointer, size_t oldSize, size_t newSize){
    if(newSize == 0){
        free(pointer);
        return NULL;
    }
    void* result = realloc(pointer, newSize);
    if(result == NULL) exit(1);
    memset(result+oldSize, 0,newSize-oldSize);
    return result;
}
