package com.craftinginterpreters.lox;

abstract class Expression {
    //defining the visitor interface
    interface Visitor<R>{
        R visitBinaryExpression(Binary expression);
        R visitGroupingExpression(Grouping expression);
        R visitUnaryExpression(Unary expression);
        R visitLiteralExpression(Literal expression);
        R visitVariable(Variable variable);
    }
    // this abstract method will be implemented by each child class to call the
    abstract <R> R accept(Visitor<R> visitor);
    static class Binary extends Expression {
        Expression left;
        Expression right;
        Token operator;
        Binary(Expression left, Expression right, Token operator){
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpression(this);
        }
    }
    static class Grouping extends Expression {
        Expression expression;
        Grouping(Expression expression){
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpression(this);
        }
    }
    static class Unary extends Expression {
        Token operator;
        Expression expression;
        Unary(Token operator, Expression expression){
            this.expression = expression;
            this.operator = operator;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitUnaryExpression(this);
        }
    }
    static class Literal extends Expression {
        Object value;
        Literal(Object value){
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitLiteralExpression(this);
        }
    }
    static class Variable extends Expression{
        final Token name;
        Variable(Token name){this.name = name;}
        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariable(this);
        }
    }
}
