#include <stdio.h>
#include <stdlib.h>
#include "vm.h"
VM vm;

void initVM(){}

void freeVM(){}

void interpret(Chunk* chunk){
    int i = 0;
    for(i=0; i<chunk->count;){
        switch (chunk->code[i]) {
            case OP_RETURN:
                printf("OP_RETURN");
                break;
            case OP_CONSTANT:
                printf("OP_CONSTANT: ");
                if(i >= chunk->count-1){
                    //runtime error
                    exit(1);
                }
                int constantIndex = chunk->code[i+1];
                printf("%.2f", chunk->constants.values[constantIndex]);
                i++;
        }
        printf("\n");
        i++;
    }
}