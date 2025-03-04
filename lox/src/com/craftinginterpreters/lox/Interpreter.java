package com.craftinginterpreters.lox;

public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        return null;
    }

    @Override
    public Object visitGroupingExpression(Expression.Grouping expression) {
        return evaluate(expression.expression);
    }

    @Override
    public Object visitUnaryExpression(Expression.Unary expression) {
        Expression right = expression.expression;
        Object rightAnswer = evaluate(right);
        switch (expression.operator.type){
            case TokenType.MINUS:
                return -(double)rightAnswer;
            case TokenType.BANG:
                return !isTruthy(right);
        }
        return null;
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    private Object evaluate(Expression expression){
        return expression.accept(this);
    }
}
