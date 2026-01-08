#include "user_table.h"

#include "memory.h"

void initUserTable(user_table *table) {
    table->capacity = 0;
    table->count = 0;
    table->entries = NULL;
}

void freeUserTable(user_table *table) {
    FREE_ARRAY(Entry, table->entries, table->capacity);
    initUserTable(table);
}