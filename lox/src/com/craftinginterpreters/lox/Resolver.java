package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Statement.Visitor<Void>, Expression.Visitor<Void>{
    enum FunctionType{
        NONE,
        FUNCTION
    }
    final Interpreter interpreter;
    private Stack<Map<String, Boolean>> scopes = new Stack<>();
    private FunctionType currentFunctionType = FunctionType.NONE;
    private Boolean isInLoop = false;
    private Map<String, Boolean> used = new HashMap<>();

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }


    void beginScope(){
        scopes.push(new HashMap<String, Boolean>());
    }
    void endScope(){
        scopes.pop();
    }
    void declare(Token name){
        if(scopes.isEmpty()) return;
        Map<String, Boolean>scope = scopes.peek();
        if(scope.containsKey(name.lexeme)){
            Lox.error(name, "[Resolver Error]: A variable is already declared with the same name in the local scope");
        }
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
        used.put(variableDeclaration.name.lexeme, false);
        return null;
    }

    @Override
    public Void visitVariable(Expression.Variable variable) {
        if(!scopes.empty() && scopes.peek().get(variable.name.lexeme) == Boolean.FALSE){
            Lox.error(variable.name, "[Resolver Error]: Can't read local variable in its own initializer");
        }
        resolveLocal(variable, variable.name);
        used.put(variable.name.lexeme, true);
        return null;
    }
    @Override
    public Void visitAssignmentExpression(Expression.Assignment assignment) {
        resolve(assignment.expression);
        resolveLocal(assignment, assignment.identifier);
        return null;
    }


    @Override
    public Void visitBlock(Statement.Block block) {
        beginScope();
//        scopes.push(new HashMap<>());
        for(Statement statement: block.statements){
            resolve(statement);
        }
        endScope();
//        scopes.pop();
        return null;
    }

    private void resolveFunction(Expression.Function function, FunctionType type){
        FunctionType enclosingFunctionType = currentFunctionType;
        currentFunctionType = type;
        beginScope();
        for(Token param: function.parameters){
            declare(param);
            define(param);
        }
        resolve(function.block);
        endScope();
        currentFunctionType = enclosingFunctionType;
    }
    @Override
    public Void visitFunction(Statement.Function function) {
        declare(function.name);
        define(function.name);
        resolveFunction(function.function, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visitFunctionExpr(Expression.Function function) {
        resolveFunction(function, FunctionType.FUNCTION);
        return null;
    }

    Void resolve(Expression expression){
        expression.accept(this);
        return null;
    }
    Void resolve(Statement statement){
        statement.accept(this);
        return null;
    }
    Void checkUnusedVariables(){
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_YELLOW = "\u001B[33m";
        for(String name: used.keySet()){
            if(!used.get(name)){
                System.out.println(ANSI_YELLOW + "[Warning]: Unused Variable: "+ name + " in the program" + ANSI_RESET);
            }
        }
        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement expressionStatement) {
        resolve(expressionStatement.expression);
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
    public Void visitPrintStatement(Statement.PrintStatement printStatement) {
        resolve(printStatement.expression);
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return returnStatement) {
        if(currentFunctionType == FunctionType.NONE){
            Lox.error(returnStatement.keyword, "[Resolver Error]: cannot return from the top level code");
        }
        if(returnStatement.value != null) resolve(returnStatement.value);
        return null;
    }
    @Override
    public Void visitWhile(Statement.While whileStatement) {
        Boolean enclosingLoop = isInLoop;
        resolve(whileStatement.condition);
        isInLoop = true;
        resolve(whileStatement.body);
        isInLoop = enclosingLoop;
        return null;
    }

    @Override
    public Void visitBinaryExpression(Expression.Binary expression) {
        resolve(expression.left);
        resolve(expression.right);
        return null;
    }
    @Override
    public Void visitCall(Expression.Call call) {
        resolve(call.callee);
        for(Expression arg: call.arguments){
            resolve(arg);
        }
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
    public Void visitBreak(Statement.Break breakStatement) {
        if(!isInLoop) Lox.error(breakStatement.name, "[Resolver Error]: break could not be used outside of loops");
        return null;
    }

}
