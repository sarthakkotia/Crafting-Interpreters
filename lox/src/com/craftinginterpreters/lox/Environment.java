package com.craftinginterpreters.lox;

import java.util.HashMap;

public class Environment {
    final HashMap<String, Object>values = new HashMap<String, Object>();

    void define(String name, Object value){
        if(values.containsKey(name)) throw new RuntimeError(name, "Variable redefinition is not allowed");
        values.put(name, value);
    }

    Object get(Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        throw new RuntimeError(name, "Undefined Variable " + name.lexeme + ".");
    }
}
