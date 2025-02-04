package com.craftinginterpreters.lox;

public class AstPrinter implements Expression.Visitor<String>{
    String print(Expression expression){
        return expression.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        Expression[] expressionsArr = {expression.left, expression.right};
        return parenthesize(expression.operator.lexeme, expressionsArr);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        Expression[] expressionsArr = {expression.expression};
        return parenthesize("Grouping", expressionsArr);
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        Expression[] expressionsArr = {expression.expression};
        return parenthesize(expression.operator.lexeme, expressionsArr);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if(expression.value == null) return "nil";
        return expression.value.toString();
    }

    String parenthesize(String name, Expression[] expressions){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" ( ");
        for(Expression expression: expressions){
            stringBuilder.append(" ");
            stringBuilder.append(print(expression));
        }
        stringBuilder.append(" ) ");
        return stringBuilder.toString();
    }
}
