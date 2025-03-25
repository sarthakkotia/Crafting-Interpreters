package com.craftinginterpreters.lox;

abstract class Statement {
    interface Visitor<R>{
        R visitExpressionStatement(ExpressionStatement expressionStatement);
        R visitPrintStatement(PrintStatement printStatement);
        R visitVariableDeclaration(VariableDeclaration variableDeclaration);
    }
    abstract <R> R accept(Visitor<R> visitor);
    static class ExpressionStatement extends Statement{
        final Expression expression;
        ExpressionStatement(Expression expression){
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStatement(this);
        }
    }
    static class PrintStatement extends Statement{
        final Expression expression;
        PrintStatement(Expression expression){
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitPrintStatement(this);
        }
    }
    static class VariableDeclaration extends Statement{
        final Token name;
        final Expression initializer;
        VariableDeclaration(Token name, Expression initializer){
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableDeclaration(this);
        }
    }
}
