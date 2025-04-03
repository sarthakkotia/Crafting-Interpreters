package com.craftinginterpreters.lox;

public class AstPrinter implements Expression.Visitor<String>, Statement.Visitor<String>{
    String print(Expression expression){
        return expression.accept(this);
    }
    String print(Statement statement){
        return statement.accept(this);
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

    @Override
    public String visitAssignmentExpression(Expression.Assignment assignment) {
        Expression[] expressionsArr = {assignment.expression};
        return parenthesize("Assignment", expressionsArr);
    }

    @Override
    public String visitVariable(Expression.Variable variable) {
        return variable.name.toString();
    }

    String parenthesize(String name, Expression[] expressions){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(name);
        for(Expression expression: expressions){
            stringBuilder.append(" ");
            stringBuilder.append(print(expression));
        }
        stringBuilder.append(") ");
        return stringBuilder.toString();
    }

    public static void main(String[] args){
        Expression expression = new Expression.Binary(
                new Expression.Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Expression.Literal(123)
                ),
                new Expression.Grouping(
                        new Expression.Literal(45.67)
                ),
                new Token(TokenType.STAR, "*", null, 1)
        );
        AstPrinter astPrinter = new AstPrinter();
        System.out.println(astPrinter.print(expression));
    }

    @Override
    public String visitExpressionStatement(Statement.ExpressionStatement expressionStatement) {
        Expression[] expressions = {expressionStatement.expression};
        return parenthesize("Expression", expressions);
    }

    @Override
    public String visitPrintStatement(Statement.PrintStatement printStatement) {
        Expression[] expressions = {printStatement.expression};
        return parenthesize("Print", expressions);
    }

    @Override
    public String visitVariableDeclaration(Statement.VariableDeclaration variableDeclaration) {
        Expression[] expressions = {variableDeclaration.initializer};
        return parenthesize("VariableDeclaration", expressions);
    }
}
