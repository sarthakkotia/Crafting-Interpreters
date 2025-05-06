package com.craftinginterpreters.lox;

import java.util.List;

public class LoxFunction implements LoxCallable{
    private final Statement.Function function;
    private final Environment closure;
    LoxFunction(Statement.Function function, Environment closure){
        this.function = function;
        this.closure = closure;
    }

    @Override
    public int airity() {
        return function.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(this.closure);
        for(int i=0; i<function.parameters.size(); i++){
            environment.define(function.parameters.get(i).lexeme,arguments.get(i));
        }
        try{
            interpreter.executeBlock(function.body, environment);
        } catch (ReturnException returnException){
            return returnException.value;
        }
        return null;
    }

    @Override
    public String toString(){
        return "<fn "+function.name.lexeme+" >";
    }
}
