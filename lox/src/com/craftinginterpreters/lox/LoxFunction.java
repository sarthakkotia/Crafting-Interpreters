package com.craftinginterpreters.lox;

import java.util.List;

public class LoxFunction implements LoxCallable{
    private final Statement.Function function;
    LoxFunction(Statement.Function function){
        this.function = function;
    }

    @Override
    public int airity() {
        return function.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(interpreter.globals);
        for(int i=0; i<function.parameters.size(); i++){
            environment.define(function.parameters.get(i).lexeme,arguments.get(i));
        }
        interpreter.executeBlock(function.body, environment);
        return null;
    }
}
