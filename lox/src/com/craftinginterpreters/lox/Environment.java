package com.craftinginterpreters.lox;

import java.util.HashMap;

public class Environment {
    final Environment parent;
    final HashMap<String, Object>values = new HashMap<String, Object>();

    Environment(){
        this.parent = null;
    }

    Environment(Environment parent){
        this.parent = parent;
    }

    void define(String name, Object value){
        if(values.containsKey(name)) throw new RuntimeError(name, "Variable redefinition is not allowed");
        values.put(name, value);
    }

    void assign(Token name, Object value){
        if(values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }
        if(parent != null){
            parent.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Underfined variable " + name);
    }

    Object get(Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        if(parent != null) return parent.get(name);
        throw new RuntimeError(name, "Undefined Variable " + name.lexeme + ".");
    }
    Object getAt(Token name, int distance){
        Environment environment = ancestor(distance);
        return environment.values.get(name.lexeme);
    }
    private Environment ancestor(int distance){
        Environment environment = this;
        for(int i=0; i<distance; i++){
            environment = environment.parent;
        }
        return environment;
    }
    void assignAt(int distance, Token name, Object value){
        ancestor(distance).values.put(name.lexeme, value);
    }


}
