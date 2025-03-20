package com.craftinginterpreters.lox;

abstract class Statement {
    interface Visitor<R>{
        R visitExpressionStatement(ExpressionStatement expressionStatement);
        R visitPrintStatement(PrintStatement printStatement);
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
}
