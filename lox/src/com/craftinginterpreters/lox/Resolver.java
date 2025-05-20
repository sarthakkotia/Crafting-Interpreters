package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Statement.Visitor<Void>, Expression.Visitor<Void>{
    final Interpreter interpreter;
    private Stack<Map<String, Boolean>> scopes = new Stack<>();

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
        Map<String, Boolean>map = new HashMap<>();
        scopes.push(map);
    }


    void beginSope(){
        scopes.push(new HashMap<String, Boolean>());
    }
    void endScope(){
        scopes.pop();
    }
    void declare(Token name){
        if(scopes.isEmpty()) return;
        Map<String, Boolean>scope = scopes.peek();
        scope.put(name.lexeme, false);
    }
    void define(Token name){
        if(scopes.isEmpty()) return;
        scopes.peek().put(name.lexeme, true);
    }
    void resolveLocal(Expression expression, Token name){
        for(int i=scopes.size()-1; i>=0; i--){
            if(scopes.get(i).containsKey(name.lexeme)){
                interpreter.resolve(expression, scopes.size()-i-1);
                return;
            }
        }
    }
    @Override
    public Void visitVariableDeclaration(Statement.VariableDeclaration variableDeclaration) {
        declare(variableDeclaration.name);
        if(variableDeclaration.initializer != null){
            resolve(variableDeclaration.initializer);
        }
        define(variableDeclaration.name);
        return null;
    }

    @Override
    public Void visitVariable(Expression.Variable variable) {
        if(!scopes.empty() && scopes.peek().get(variable.name.lexeme) == Boolean.FALSE){
            Lox.error(variable.name, "Can't read local variable in its own initializer");
        }
        resolveLocal(variable, variable.name);
        return null;
    }
    @Override
    public Void visitAssignmentExpression(Expression.Assignment assignment) {



        scopes.peek().put(assignment.identifier.lexeme, true);
        resolve(assignment.expression);
        return null;
    }


    @Override
    public Void visitBlock(Statement.Block block) {
        beginSope();
//        scopes.push(new HashMap<>());
        for(Statement statement: block.statements){
            resolve(statement);
        }
        endScope();
//        scopes.pop();
        return null;
    }

    @Override
    public Void visitFunctionExpr(Expression.Function function) {
        scopes.push(new HashMap<>());
        for(Token parameter: function.parameters){
            scopes.peek().put(parameter.lexeme, true);
        }
        scopes.pop();
        return null;
    }

    private Void resolve(Expression expression){
        expression.accept(this);
        return null;
    }
    private Void resolve(Statement statement){
        statement.accept(this);
        return null;
    }


    @Override
    public Void visitGroupingExpression(Expression.Grouping expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitUnaryExpression(Expression.Unary expression) {
        resolve(expression.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpression(Expression.Literal expression) {
        return null;
    }

    @Override
    public Void visitLogical(Expression.Logical logical) {
        resolve(logical.left);
        resolve(logical.right);
        return null;
    }

    @Override
    public Void visitCall(Expression.Call call) {
        resolve(call.callee);
        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement expressionStatement) {
        resolve(expressionStatement.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.PrintStatement printStatement) {
        resolve(printStatement.expression);
        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.Binary expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }

    @Override
    public Void visitIf(Statement.If ifStatement) {
        resolve(ifStatement.condition);
        resolve(ifStatement.thenBranch);
        if(ifStatement.elseBranch != null) resolve(ifStatement.elseBranch);
        return null;
    }

    @Override
    public Void visitWhile(Statement.While whileStatement) {
        resolve(whileStatement.condition);
        resolve(whileStatement.body);
        return null;
    }

    @Override
    public Void visitBreak(Statement.Break breakStatement) {
        return null;
    }

    @Override
    public Void visitFunction(Statement.Function function) {
        resolve(function.function);
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return returnStatement) {
        resolve(returnStatement.value);
        return null;
    }
}
