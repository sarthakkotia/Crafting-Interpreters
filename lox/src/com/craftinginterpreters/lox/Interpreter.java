package com.craftinginterpreters.lox;

import java.util.Objects;

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
                if(left instanceof String || right instanceof String){
                    return stringify(left) + stringify(right);

                }
                throw new RuntimeError(expression.operator, "Operands must be two numbers or two strings");
            case TokenType.GREATER:
                if(checkNumberSingleCharOperand(expression.operator, left, right) == 0){
                    return (double) left > (double) right;
                }else{
                    String leftStr = (String) left;
                    String rightStr = (String) left;
                    return leftStr.charAt(0) > rightStr.charAt(0);
                }
            case TokenType.GREATER_EQUAL:
                if(checkNumberSingleCharOperand(expression.operator, left, right) == 0){
                    return (double) left >= (double) right;
                }else{
                    String leftStr = (String) left;
                    String rightStr = (String) left;
                    return leftStr.charAt(0) >= rightStr.charAt(0);
                }
            case TokenType.LESS:
                if(checkNumberSingleCharOperand(expression.operator, left, right) == 0){
                    return (double) left < (double) right;
                }else{
                    String leftStr = (String) left;
                    String rightStr = (String) left;
                    return leftStr.charAt(0) < rightStr.charAt(0);
                }
            case TokenType.LESS_EQUAL:
                if(checkNumberSingleCharOperand(expression.operator, left, right) == 0){
                    return (double) left <= (double) right;
                }else{
                    String leftStr = (String) left;
                    String rightStr = (String) left;
                    return leftStr.charAt(0) <= rightStr.charAt(0);
                }
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
    int checkNumberSingleCharOperand(Token operator, Object expression1, Object expression2){
        if(expression1 instanceof Double && expression2 instanceof Double) return 0 ;
        if((expression1 instanceof String && ((String) expression1).length() == 1) && (expression2 instanceof String && ((String) expression2).length() == 1)) return 1;
        throw new RuntimeError(operator, "Operand(s) must be a number or a single character string");
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
