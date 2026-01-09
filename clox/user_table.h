#ifndef CLOX_USER_TABLE_H
#define CLOX_USER_TABLE_H

#include "value.h"

#define TABLE_MAX_LOAD 0.75


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
bool tableSet(user_table* table, Value* key, Value value);
bool tableGet(user_table* table, Value* key, Value* value);
bool tableDelete(user_table* table, Value* key);
void tableAddAll(user_table* from, user_table* to);
void freeUserTable(user_table *user_table);



#endif //CLOX_USER_TABLE_H