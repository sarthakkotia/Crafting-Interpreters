package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expression {
    //defining the visitor interface
    interface Visitor<R>{
        R visitBinaryExpression(Binary expression);
        R visitGroupingExpression(Grouping expression);
        R visitUnaryExpression(Unary expression);
        R visitLiteralExpression(Literal expression);
        R visitAssignmentExpression(Assignment assignment);
        R visitVariable(Variable variable);
        R visitLogical(Logical logical);
        R visitCall(Call call);
        R visitFunctionExpr(Function function);
        R visitGetExpression(Get get);
        R visitSetExpression(Set set);
        R visitThisExpression(This thisExpression);
        R visitSuperExpression(Super superExpression);
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
    static class Assignment extends Expression{
        final Token identifier;
        final Expression expression;
        Assignment(Token identifier, Expression expression){
            this.identifier = identifier;
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignmentExpression(this);
        }
    }
    static class Logical extends Expression{
        final Expression left;
        final Expression right;
        final Token operator;
        Logical(Expression left, Expression right, Token operator){
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogical(this);
        }
    }
    static class Call extends Expression{
        final Expression callee;
        final Token paren;
        final List<Expression> arguments;

        Call(Expression callee, Token paren, List<Expression> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor){ return visitor.visitCall(this); }
    }
    static class Function extends Expression{
        final List<Token> parameters;
        final Statement.Block block;

        public Function(List<Token> parameters, Statement.Block block) {
            this.parameters = parameters;
            this.block = block;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitFunctionExpr(this); }
    }
    static class Get extends Expression{
        final Expression object;
        final Token name;

         public Get(Expression object, Token name){
             this.name = name;
             this.object = object;
         }

         @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitGetExpression(this); }
    }
    static class Set extends Expression{
        final Expression object;
        final Token name;
        final Expression value;

        public Set(Expression object, Token name, Expression value){
            this.name = name;
            this.object = object;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitSetExpression(this); }
    }
    static class This extends Expression{
        final Token name;

        public This(Token name){
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitThisExpression(this); }
    }
    static class Super extends Expression{
        final Token keyword;
        final Token method;

        public Super(Token keyword, Token method){
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor){ return visitor.visitSuperExpression(this); }

    }
}
