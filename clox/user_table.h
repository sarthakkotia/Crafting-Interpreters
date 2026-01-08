#ifndef CLOX_USER_TABLE_H
#define CLOX_USER_TABLE_H

#include "value.h"

typedef struct {
    Value *key;
    Value value;
} Entry;

typedef struct {
    int count;
    int capacity;
    Entry *entries;
} user_table;

void initUserTable(user_table *user_table);
void freeUserTable(user_table *user_table);



#endif //CLOX_USER_TABLE_H