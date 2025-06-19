package com.craftinginterpreters.lox;

public class LoxInstance {
    private LoxClass loxClass;
    LoxInstance(LoxClass loxClass){
        this.loxClass = loxClass;
    }

    @Override
    public String toString(){
        return loxClass.name + " instance ";
    }
}
