package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
    private static class BreakException extends RuntimeException{}
//    private Environment environment = new Environment();
    final Environment globals = new Environment();
    private Environment environment = globals;
    private Map<Expression, Integer> locals = new HashMap<>();
    Interpreter(){
        globals.define("clock", new LoxCallable() {
            @Override
            public int airity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            public String toString(){
                return "<native fn>";
            }
        });
    }
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

    private Object lookUp(Token name, Expression expression){
        Integer distance = locals.get(expression);
        if(distance != null){
            return environment.getAt(name, distance);
        }else{
            return globals.get(name);
        }

    }
    @Override
    public Object visitLiteralExpression(Expression.Literal expression) {
        return expression.value;
    }

    @Override
    public Object visitVariable(Expression.Variable expression) {
//        return environment.get(expression.name);
        return lookUp(expression.name, expression);
    }

    @Override
    public Object visitLogical(Expression.Logical logical) {
        Object leftValue = evaluate(logical.left);
        boolean leftAns = isTruthy(leftValue);
        if(logical.operator.type == TokenType.OR){
            if(leftAns) return true;
        }else {
            if (!leftAns) return false;
        }
        Object rightValue = evaluate(logical.right);
        return isTruthy(rightValue);

    }

    @Override
    public Object visitCall(Expression.Call call) {
        Object callee = evaluate(call.callee);
        List<Object> arguments = new ArrayList<>();
        for(Expression argument: call.arguments){
            arguments.add(evaluate(argument));
        }
        if(!(callee instanceof LoxCallable)) {
            throw new RuntimeError(call.paren, "Could only call functions and classes");
        }
        LoxCallable function = (LoxCallable) callee;
        if(arguments.size() != function.airity()){
            throw new RuntimeError(call.paren, "Expected " + function.airity() + " arguments, but got " + arguments.size() + "." );
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitFunctionExpr(Expression.Function function) {
        return new LoxFunction(null, function, environment);
    }

    public Object evaluate(Expression expression){
        return expression.accept(this);
    }

    void interpret(List<Statement> statements){
        try {
            for (Statement statement: statements){
                execute(statement);
            }
        } catch (RuntimeError e){
            Lox.runtimeError(e);
        }
    }
    void execute(Statement statement){
        statement.accept(this);
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

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement expressionStatement) {
        evaluate(expressionStatement.expression);
        return null;
    }

    @Override
    public Void visitPrintStatement(Statement.PrintStatement printStatement) {
        Object value = evaluate(printStatement.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVariableDeclaration(Statement.VariableDeclaration variableDeclaration) {
        Object value = null;
        if(variableDeclaration.initializer != null){
            value = evaluate(variableDeclaration.initializer);
        }
        environment.define(variableDeclaration.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlock(Statement.Block block) {
        Environment env = new Environment(environment);
        executeBlock(block, env);
        return null;
    }

    @Override
    public Void visitIf(Statement.If ifStatement) {
        Object conditionValue = evaluate(ifStatement.condition);
        if(isTruthy(conditionValue)){
            execute(ifStatement.thenBranch);
        }else if(ifStatement.elseBranch != null) execute(ifStatement.elseBranch);
        return null;
    }

    @Override
    public Void visitWhile(Statement.While whileStatement) {
        try{
            while(isTruthy(evaluate(whileStatement.condition))){
                execute(whileStatement.body);
            }
        } catch (BreakException e){}

        return null;
    }

    @Override
    public Void visitBreak(Statement.Break breakStatement) {
        throw new BreakException();
    }

    @Override
    public Void visitFunction(Statement.Function function) {
        String fnName = function.name.lexeme;
        environment.define(fnName, new LoxFunction(fnName, function.function, environment));
        return null;
    }

    @Override
    public Void visitReturn(Statement.Return returnStatement) {
        Object value = null;
        if(returnStatement.value != null) value = evaluate(returnStatement.value);
        throw new ReturnException(value);
    }

    @Override
    public Void visitLoxClass(Statement.LoxClass loxClass) {
        environment.define(loxClass.name.lexeme, null);
        LoxClass loxclass = new LoxClass(loxClass.name.lexeme);
        environment.assign(loxClass.name, loxclass);
        return null;
    }

    public void executeBlock(Statement.Block block, Environment environment){
        Environment previous = this.environment;
        try{
            this.environment = environment;
            for(Statement statement: block.statements){
                execute(statement);
            }
        }
        finally {
            this.environment = previous;
        }
    }

    @Override
    public Object visitAssignmentExpression(Expression.Assignment assignment) {
        Object value = evaluate(assignment.expression);
        Integer distance = locals.get(assignment);
        if(distance != null){
            environment.assignAt(distance, assignment.identifier, value);
        }else{
            globals.assign(assignment.identifier, value);
        }
        return value;
    }

    public void resolve(Expression expression, int depth){
        locals.put(expression, depth);
    }
}
