package com.craftinginterpreters.lox;

import java.util.List;

public class LoxFunction implements LoxCallable{
    private final String name;
    private final Expression.Function function;
    private final Environment closure;
    private final boolean isInitializer;
    LoxFunction(String name, Expression.Function function, Environment closure, boolean isInitializer){
        this.name = name;
        this.function = function;
        this.closure = closure;
        this.isInitializer = isInitializer;
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
            interpreter.executeBlock(function.block, environment);
        } catch (ReturnException returnException){
            if(this.isInitializer) return closure.getAt("this", 0);
            return returnException.value;
        }
        if(this.isInitializer) return closure.getAt("this", 0);
        return null;
    }
    LoxFunction bind(LoxInstance loxInstance, String name){
        Environment environment = new Environment(closure);
        environment.define("this", loxInstance);
        return new LoxFunction(name, function, environment, isInitializer);
    }

    @Override
    public String toString(){
        if(name == null) return "<fn>";
        return "<fn "+name+" >";
    }
}
