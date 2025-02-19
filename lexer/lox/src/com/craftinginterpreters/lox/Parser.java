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
    public Expression solve(){
        return expression();
    }
    // defining the grammer
    private Expression expression(){
        return equality();
    }
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
        Expression expression = null;
        if(match(TokenType.LEFT_PAREN)){
            expression = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression");
            return new Expression.Grouping(expression);
        }

        return (expression == null) ? new Expression.Literal("") : expression;
    }
    private Token consume(TokenType type, String message){
        if(check(type)) return advance();
        throw error(peek(), message);
    }
    private ParseError error(Token token, String message){
        Lox.error(token, message);
        return new ParseError();
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
