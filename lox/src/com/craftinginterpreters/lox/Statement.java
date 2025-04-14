package com.craftinginterpreters.lox;

import java.util.List;

abstract class Statement {
    interface Visitor<R>{
        R visitExpressionStatement(ExpressionStatement expressionStatement);
        R visitPrintStatement(PrintStatement printStatement);
        R visitVariableDeclaration(VariableDeclaration variableDeclaration);
        R visitBlock(Block block);
        R visitIf(If ifStatement);
        R visitWhile(While whileStatement);
        R visitFor(For forStatement);
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
    static class Block extends Statement{
        List<Statement> statements;
        Block(List<Statement> statements){
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlock(this);
        }
    }
    static class If extends Statement{
        final Expression condition;
        final Statement thenBranch;
        final Statement elseBranch;
        If(Expression condition, Statement thenBranch, Statement elseBranch){
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }


        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIf(this);
        }
    }
    static class While extends Statement{
        final Expression condition;
        final Statement body;
        While(Expression condition, Statement body){
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhile(this);
        }
    }
    static class For extends Statement{
        Statement initializer;
        Expression condition;
        Expression action;
        Statement body;
        For(Statement initializer, Expression condition, Expression action, Statement body){
            this.initializer = initializer;
            this.condition = condition;
            this.action = action;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFor(this);
        }
    }
}
