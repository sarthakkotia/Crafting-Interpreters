package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private int current = 0;
    private int loopDepth = 0;
    Parser(List<Token> tokens){
        this.tokens = tokens;
    }
    List<Statement> parse(){
        List<Statement> statements = new ArrayList<Statement>();
        while(!isAtEnd()){
            statements.add(declaration());
        }
        return statements;
    }
    private Statement declaration(){
        try{
            if(match(TokenType.FUN)) return function("function");
            if(match(TokenType.VAR)) return variableDeclaration();
            return statement();
        } catch (ParseError e){
            synchronize();
            return null;
        }
    }
    private Statement function(String kind){
        Token name = consume(TokenType.IDENTIFIER, "Expected " + kind + "name. ");
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name");
        List<Token> parameters = new ArrayList<>();
        if(!check(TokenType.LEFT_PAREN)){
            do{
                if(parameters.size() >= 255){
                    error(peek(), "Can't have more than 255 arguments");
                }
                parameters.add(consume(TokenType.IDENTIFIER, "Expected a parameter name"));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters");
        consume(TokenType.LEFT_BRACE, "Expected '{' before" + kind + "body. ");
        Statement.Block body = ((Statement.Block)block());
        return new Statement.Function(name, parameters, body);

    }
    private Statement variableDeclaration(){
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name");
        Expression expression = null;
        if(match(TokenType.EQUAL)) expression = expression();
        else error(previous(),  previous().lexeme + " not initialized");
        consume(TokenType.SEMICOLON, "Expected ';' after variable Declaration");
        return new Statement.VariableDeclaration(name, expression);
    }
    private Statement statement(){
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.LEFT_BRACE)) return block();
        if(match(TokenType.IF)) return ifStatement();
        if(match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.FOR)) return forStatement();
        if(match(TokenType.BREAK)) return breakStatement();
        return expressionStatement();
    }
    private Statement breakStatement(){
        if(loopDepth == 0){
            error(previous(), "break must be used inside a loop");
        }
        consume(TokenType.SEMICOLON, "Expect ';' after break");
        return new Statement.Break();
    }
    private Statement forStatement(){
        consume(TokenType.LEFT_PAREN, "Expected '(' after for");
        Statement initializer = null;
        if(peek().type != TokenType.SEMICOLON){
            initializer = declaration();
            if(!(initializer instanceof Statement.VariableDeclaration) && !(initializer instanceof Statement.ExpressionStatement)){
                throw error(previous(), "Expected a variable declartion or a expression");
            }
        }else{
            consume(TokenType.SEMICOLON, "Expected ';' after loop initializer");
        }
        Expression condition = null;
        if(peek().type != TokenType.SEMICOLON){
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after loop condition");
        Expression action = null;
        if(peek().type != TokenType.RIGHT_PAREN){
            action = expression();
        }
        consume(TokenType.RIGHT_PAREN, "Expeced ')' after for");
        try{
            loopDepth++;
            Statement body = statement();
            if(action != null){
                body = new Statement.Block(
                        Arrays.asList(
                                body,
                                new Statement.ExpressionStatement(action)
                        )
                );
            }
            if(condition == null) condition = new Expression.Literal(true);
            body = new Statement.While(condition, body);
            if(initializer != null){
                body = new Statement.Block(
                        Arrays.asList(
                                initializer,
                                body
                        )
                );
            }
            return body;
        }finally {
            loopDepth--;
        }


    }
    private Statement whileStatement(){
        consume(TokenType.LEFT_PAREN, "Expected '(' after while");
        Expression condititon = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
        try{
            loopDepth++;
            Statement body = statement();
            return new Statement.While(condititon, body);
        }finally {
            loopDepth--;
        }

    }
    private Statement ifStatement(){
        consume(TokenType.LEFT_PAREN, "Expected '(' after if");
        Expression condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expected ')' after condition");
        Statement thenBranch = statement();
        Statement elseBranch = null;
        if(match(TokenType.ELSE)){
            elseBranch = statement();
        }
        return new Statement.If(condition, thenBranch, elseBranch);

    }
    private Statement block(){
        List<Statement>statements = new ArrayList<>();
        while(!check(TokenType.RIGHT_BRACE) && !isAtEnd()){
            statements.add(declaration());
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
        return new Statement.Block(statements);
    }
    private Statement printStatement(){
        Expression expression = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        return new Statement.PrintStatement(expression);
    }
    private Statement expressionStatement(){
        Expression expression = expression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        return new Statement.ExpressionStatement(expression);
    }
//    Expression parse(){
//        try {
//            return expression();
//        }catch (ParseError err){
//            return null;
//        }
//    }
    public Expression solve(){
        return expression();
    }
    // defining the grammer
    private Expression expression(){
        return assign();
    }
    private Expression assign(){
        Expression expression = or();
        if(match(TokenType.EQUAL)){
            Token equals = previous();
            Expression value = assign();
            if(expression instanceof Expression.Variable){
                Token identifier = ((Expression.Variable)expression).name;
                return new Expression.Assignment(identifier, value);
            }
            error(equals, "Invalid assignment target");
        }
        return expression;
    }
    private Expression or(){
        Expression expression = and();
        while(match(TokenType.OR)){
            Token operator = previous();
            Expression right = and();
            expression = new Expression.Logical(expression, right, operator);
        }
        return expression;
    }
    private Expression and(){
        Expression expression = comma();
        while (match(TokenType.AND)){
            Token operator = previous();
            Expression right = comma();
            expression = new Expression.Logical(expression, right, operator);
        }
        return expression;
    }
//    private Expression assign(){
//        Expression expression = comma();
//        if(match(TokenType.EQUAL)){
//            Token identifier = previous();
//            consume(TokenType.EQUAL, "identifier is needed to be assigned");
////            Expression expression = comma();
////            consume(TokenType.SEMICOLON, "Expeected ';' after variable Declaration");
//            return new Expression.Assignment(identifier, expression);
//        }
//    }
    private Expression comma(){
        Expression expression = equality();
        while(match(TokenType.COMMA)){
            Token operator = previous();
            Expression right = equality();
            expression = new Expression.Binary(expression, right, operator);
        }
        return expression;
        //TODO: this could cause problems for functions solve that
    }
    //TODO: Add support for ternary expressions
    private Expression equality(){
        Expression expression = comparison();
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            Token operator = previous();
            Expression right = comparison();
            expression = new Expression.Binary(expression, right, operator);
        }
        return expression;
    }
    private Expression comparison(){
        Expression expression = term();
        while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            Token operator = previous();
            Expression right = term();
            expression = new Expression.Binary(expression, right, operator);
        }
        return expression;
    }
    private Expression term(){
        Expression factor = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)){
            Token operator = previous();
            Expression right = factor();
            factor = new Expression.Binary(factor, right, operator);
        }
        return factor;
    }
    private Expression factor(){
        Expression unary = unary();
        while (match(TokenType.SLASH, TokenType.STAR)){
            Token operator = previous();
            Expression right = unary();
            unary = new Expression.Binary(unary, right, operator);
        }
        return unary;
    }
    private Expression unary(){
        if(match(TokenType.BANG, TokenType.MINUS)){
            Token operator = previous();
            Expression right = unary();
            return new Expression.Unary(operator, right);
        }
        return call();
    }
    private Expression call(){
        Expression primary = primary();
        while(true){
            if(match(TokenType.LEFT_PAREN)){
                primary = finishCall(primary);
            }else{
                break;
            }
        }
        return primary;
    }
    private Expression finishCall(Expression callee){
        List<Expression> arguments = new ArrayList<>();
        if(!check(TokenType.RIGHT_PAREN)){
            do{
                if(arguments.size() >= 255){
                    error(peek(), "function can't have more than 255 arguments");
                }
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after function call arguments");
        return new Expression.Call(callee, paren, arguments);
    }
    private Expression primary(){
        if (match(TokenType.FALSE)) return new Expression.Literal(false);
        if (match(TokenType.TRUE)) return new Expression.Literal(true);
        if (match(TokenType.NIL)) return new Expression.Literal(null);
        if (match(TokenType.NUMBER, TokenType.STRING)){
            return new Expression.Literal(previous().literal);
        }
        if(match(TokenType.LEFT_PAREN)){
            Expression expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression");
            return new Expression.Grouping(expression);
        }
        if(match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)){
            error(previous(), "The operator does not have a left operand");
            equality();
            return null;
        }
        if(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
            error(previous(), "The operator does not have a left operand");
            comparison();
            return null;
        }
        if(match(TokenType.PLUS)){
            error(previous(), "The operator does not have a left operand");
            term();
            return null;
        }
        if(match(TokenType.SLASH, TokenType.STAR)){
            error(previous(), "The operator does not have a left operand");
            factor();
            return null;
        }
        if(match(TokenType.IDENTIFIER)){
            return new Expression.Variable(previous());
        }
        throw error(peek(), "Expect expression");
    }
    private Token consume(TokenType type, String message){
        if(check(type)) return advance();
        throw error(peek(), message);
    }
    private ParseError error(Token token, String message){
        Lox.error(token, "Parse Error: "+message);
        return new ParseError();
    }
    private void synchronize(){
        advance();
        while(!isAtEnd()){
            if(previous().type == TokenType.SEMICOLON) return;

            switch (peek().type){
                case TokenType.CLASS:
                case TokenType.FOR:
                case TokenType.FUN:
                case TokenType.IF:
                case TokenType.PRINT:
                case TokenType.RETURN:
                case TokenType.VAR:
                case TokenType.WHILE:
                    return;
            }
            advance();
        }
    }
    private Token previous(){
        if(current == 0) Lox.error(-1, "previous character not available");
        return tokens.get(current-1);
    }
    private boolean match(TokenType... operators){
        for(TokenType type: operators){
            if(check(type)){
                advance();
                return true;
            }
        }
        return false;
    }
    private boolean check(TokenType type){
        if(isAtEnd()) return false;
        return peek().type == type;
    }
    private boolean isAtEnd(){
        return current == tokens.size() || peek().type == TokenType.EOF;
    }
    private Token peek(){
        return tokens.get(current);
    }
    private Token advance(){
        if(!isAtEnd()) current++;
        return previous();
    }

    public static void main(String[] args) {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.NUMBER, "2", 2, 1));
        tokens.add(new Token(TokenType.SLASH, "/", '/', 1));
        tokens.add(new Token(TokenType.NUMBER, "2", 2, 1));;
        tokens.add(new Token(TokenType.SLASH, "/", '/', 1));
        tokens.add(new Token(TokenType.NUMBER, "2", 2, 1));
        Parser parser = new Parser(tokens);
        Expression expression = parser.solve();
        AstPrinter astPrinter = new AstPrinter();
        System.out.println(astPrinter.print(expression));
    }
}
