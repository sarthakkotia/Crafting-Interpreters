package com.craftinginterpreters.lox;

public class Interpreter implements Expression.Visitor<Object> {
    @Override
    public Object visitBinaryExpression(Expression.Binary expression) {
        Object left = evaluate(expression.left);
        Object right = evaluate(expression.right);
        switch (expression.operator.type){
            case TokenType.MINUS:
                checkNumberOperand(expression.operator, left, right);
                return (double) left - (double) right;
            case TokenType.STAR:
                checkNumberOperand(expression.operator, left, right);
                return (double) left * (double) right;
            case TokenType.SLASH:
                checkNumberOperand(expression.operator, left, right);
                return (double) left / (double) right;
            case TokenType.PLUS:
                if(left instanceof Double && right instanceof Double){
                    checkNumberOperand(expression.operator, left, right);
                    return (double) left + (double) right;
                }
                if(left instanceof String && right instanceof String) return (String) left + (String) right;
                throw new RuntimeError(expression.operator, "Operands must be two numbers or two strings");
            case TokenType.GREATER:
                checkNumberOperand(expression.operator, left, right);
                return (double) left > (double) right;
            case TokenType.GREATER_EQUAL:
                checkNumberOperand(expression.operator, left, right);
                return (double) left >= (double) right;
            case TokenType.LESS:
                checkNumberOperand(expression.operator, left, right);
                return (double) left < (double) right;
            case TokenType.LESS_EQUAL:
                checkNumberOperand(expression.operator, left, right);
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
                checkNumberOperand(expression.operator, rightAnswer);
                return -(double)rightAnswer;
            case TokenType.BANG:
                return !isTruthy(rightAnswer);
        }
        return null;
    }
    void checkNumberOperand(Token operator, Object expression){
        if(expression instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }
    void checkNumberOperand(Token operator, Object expression1, Object expression2){
        if(expression1 instanceof Double && expression2 instanceof Double) return;
        throw new RuntimeError(operator, "Operand(s) must be a number");
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

    public Object evaluate(Expression expression){
        return expression.accept(this);
    }

    void interpret(Expression expression){
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError e) {
            Lox.runtimeError(e);
        }
    }

    private String stringify(Object object){
        if(object == null) return "nil";
        if(object instanceof Double){
            String text = object.toString();
            if(text.endsWith(".0")){
                return text.substring(0, text.length()-2);
            }
            return text;
        }
        return object.toString();
    }
}
