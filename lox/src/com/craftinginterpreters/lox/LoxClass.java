package com.craftinginterpreters.lox;

import java.util.List;

public class LoxClass implements LoxCallable{
    String name;

    LoxClass(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int airity() {
        return 0;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        LoxInstance loxInstance = new LoxInstance(this);
        return loxInstance;
    }
}
