package com.craftinginterpreters.lox;

abstract class Expression {
    static class Binary extends Expression {
        Expression left;
        Expression right;
        Token operator;
        Binary(Expression left, Expression right, Token operator){
            this.left = left;
            this.right = right;
            this.operator = operator;
        }
    }
    static class Grouping extends Expression {
        Expression expression;
        Grouping(Expression expression){
            this.expression = expression;
        }
    }
    static class Unary extends Expression {
        Token operator;
        Expression expression;
        Unary(Token operator, Expression expression){
            this.expression = expression;
            this.operator = operator;
        }
    }
    static class Literal extends Expression {
        Object value;
        Literal(Object value){
            this.value = value;
        }
    }
}
