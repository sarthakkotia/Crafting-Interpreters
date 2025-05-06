package com.craftinginterpreters.lox;

import java.util.List;

interface LoxCallable {
    int airity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
