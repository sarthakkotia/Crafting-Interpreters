package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class AstPrinter implements Expression.Visitor<String>, Statement.Visitor<String>{
    String print(Expression expression){
        return expression.accept(this);
    }
    String print(Statement statement){
        return statement.accept(this);
    }

    @Override
    public String visitBinaryExpression(Expression.Binary expression) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(expression.left);
        expressionsArr.add(expression.right);
        return parenthesize(expression.operator.lexeme, expressionsArr);
    }

    @Override
    public String visitGroupingExpression(Expression.Grouping expression) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(expression.expression);
        return parenthesize("Grouping", expressionsArr);
    }

    @Override
    public String visitUnaryExpression(Expression.Unary expression) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(expression.expression);
        return parenthesize(expression.operator.lexeme, expressionsArr);
    }

    @Override
    public String visitLiteralExpression(Expression.Literal expression) {
        if(expression.value == null) return "nil";
        return expression.value.toString();
    }

    @Override
    public String visitAssignmentExpression(Expression.Assignment assignment) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(assignment.expression);
        return parenthesize("Assignment", expressionsArr);
    }

    @Override
    public String visitVariable(Expression.Variable variable) {
        return variable.name.toString();
    }

    @Override
    public String visitLogical(Expression.Logical logical) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(logical.left);
        expressionsArr.add(logical.right);
        return parenthesize(logical.operator.lexeme, expressionsArr);
    }

    @Override
    public String visitCall(Expression.Call call) {
        List<Expression>expressionsArr = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("function call\n");
        expressionsArr.add(call.callee);
        stringBuilder.append(parenthesize("callee", expressionsArr));
        stringBuilder.append("\n");
        expressionsArr.clear();
        stringBuilder.append(parenthesize("Arguments", call.arguments));
        return stringBuilder.toString();
    }

    @Override
    public String visitFunctionExpr(Expression.Function function) {
        StringBuilder sb = new StringBuilder();
        sb.append("<function expression>: "+ ", parameters: ");
        for (int i=0; i<function.parameters.size(); i++){
            sb.append(" " + function.parameters.get(i).lexeme + " ");
        }
        sb.append(function.block.accept(this));
        return sb.toString();
    }

    String parenthesize(String name, List<Expression> expressions){
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
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(expressionStatement.expression);
        return parenthesize("Expression", expressionsArr);
    }

    @Override
    public String visitPrintStatement(Statement.PrintStatement printStatement) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(printStatement.expression);
        return parenthesize("Print", expressionsArr);
    }

    @Override
    public String visitVariableDeclaration(Statement.VariableDeclaration variableDeclaration) {
        List<Expression>expressionsArr = new ArrayList<>();
        expressionsArr.add(variableDeclaration.initializer);
        return parenthesize("VariableDeclaration", expressionsArr);
    }

    @Override
    public String visitBlock(Statement.Block block) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Block starts {\n");
        for(Statement statement: block.statements){
            stringBuilder.append("\t");
            stringBuilder.append(statement.accept(this));
            stringBuilder.append("\n");
        }
        stringBuilder.append("Block ends }\n");
        return stringBuilder.toString();
    }

    @Override
    public String visitIf(Statement.If ifStatement) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("if ");
        stringBuilder.append("<condition>: "+ ifStatement.condition.accept(this));
        stringBuilder.append("then: "+ ifStatement.thenBranch.accept(this));
        if(ifStatement.elseBranch != null) stringBuilder.append("else: " + ifStatement.elseBranch.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitWhile(Statement.While whileStatement) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("while, <condition>->");
        stringBuilder.append(whileStatement.condition.accept(this));
        stringBuilder.append("<statement>->");
        stringBuilder.append(whileStatement.body.accept(this));
        return stringBuilder.toString();
    }

    @Override
    public String visitBreak(Statement.Break breakStatement) {
        return "break statement";
    }

    @Override
    public String visitFunction(Statement.Function function) {
        StringBuilder sb = new StringBuilder();
        sb.append("<function declaration>: " + function.name.lexeme + ", parameters: ");
        sb.append(function.function.accept(this));
        return sb.toString();
    }

    @Override
    public String visitReturn(Statement.Return returnStatement) {
        List<Expression>expressions = new ArrayList<>();
        expressions.add(returnStatement.value);
        return parenthesize("return", expressions);
    }
}
