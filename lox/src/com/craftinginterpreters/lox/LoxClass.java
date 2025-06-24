package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable{
    final String name;
    private final Map<String, LoxFunction> methods;
    LoxClass(String name, Map<String, LoxFunction>methods){
        this.name = name;
        this.methods = methods;
    }
    public LoxFunction findMethod(String name){
        if(methods.containsKey(name)){
            return methods.get(name);
        }
        return null;
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
