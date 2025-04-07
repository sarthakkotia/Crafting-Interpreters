package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static class ParseError extends RuntimeException{}
    private final List<Token> tokens;
    private int current = 0;
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
            if(match(TokenType.VAR)) return variableDeclaration();
            return statement();
        } catch (ParseError e){
            synchronize();
            return null;
        }
    }
    private Statement variableDeclaration(){
        Token name = consume(TokenType.IDENTIFIER, "Expect variable name");
        Expression expression = null;
        if(match(TokenType.EQUAL)) expression = expression();
        consume(TokenType.SEMICOLON, "Expeected ';' after variable Declaration");
        return new Statement.VariableDeclaration(name, expression);
    }
    private Statement statement(){
        if(match(TokenType.PRINT)) return printStatement();
        if(match(TokenType.LEFT_BRACE)) return block();
        return expressionStatement();
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
        Expression expression = comma();
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
        return primary();
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
        Lox.error(token, message);
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
