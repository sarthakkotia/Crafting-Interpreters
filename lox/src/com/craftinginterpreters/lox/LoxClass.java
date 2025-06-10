package com.craftinginterpreters.lox;

public class LoxClass {
    String name;

    LoxClass(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }
}
