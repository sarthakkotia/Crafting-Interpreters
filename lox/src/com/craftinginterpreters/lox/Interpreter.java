package com.craftinginterpreters.lox;

public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        switch (expression.operator.type){
            case TokenType.MINUS:
                return (double) left - (double) right;
            case TokenType.STAR:
                return (double) left * (double) right;
            case TokenType.SLASH:
                return (double) left / (double) right;
            case TokenType.PLUS:
                if(left instanceof Double && right instanceof Double) return (double) left + (double) right;
                if(left instanceof String && right instanceof String) return (String) left + (String) right;
                break;
            case TokenType.GREATER:
                return (double) left > (double) right;
            case TokenType.GREATER_EQUAL:
                return (double) left >= (double) right;
            case TokenType.LESS:
                return (double) left < (double) right;
            case TokenType.LESS_EQUAL:
                return (double) left <= (double) right;
            case TokenType.BANG_EQUAL: return !isEqual(left, right);
            case TokenType.EQUAL_EQUAL: return isEqual(left, right);
        }
        return null;
    }
    private boolean isEqual(Object left, Object right){
        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.equals(right);
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
                return !isTruthy(rightAnswer);
        }
        return null;
    }
    private boolean isTruthy(Object object){
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean) object;
        return true;
    }

    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    private Object evaluate(Expression expression){
        return expression.accept(this);
    }
}
