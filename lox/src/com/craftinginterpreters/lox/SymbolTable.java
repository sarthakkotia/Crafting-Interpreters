package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public class Pair{
        Boolean value;
        Token token;
        Pair(Boolean value, Token token){
            this.token = token;
            this.value = value;
        }
    }
    Map<String, Pair> map = new HashMap<>();
    final SymbolTable parent;

    SymbolTable(){
        this.parent = null;
    }

    SymbolTable(SymbolTable parent){
        this.parent = parent;
    }

    void put(Token token){
        map.put(token.lexeme, new Pair(false, token));
    }

    void update(Token token){
        if(map.containsKey(token.lexeme)) {
            map.put(token.lexeme, new Pair(true, token));
            return;
        }
        if(parent != null) parent.update(token);
    }
}
