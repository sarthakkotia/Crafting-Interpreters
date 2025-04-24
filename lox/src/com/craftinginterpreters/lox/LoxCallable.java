package com.craftinginterpreters.lox;

import java.util.List;

public interface LoxCallable {
    Object call(Object callee, List<Object> arguments);
}
