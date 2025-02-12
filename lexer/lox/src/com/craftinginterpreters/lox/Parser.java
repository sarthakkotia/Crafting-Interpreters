package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class Parser {
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
        Expression comparison = comparison();
        return comparison;
    }
    private Expression comparison(){
        Expression term = term();
        return term;
    }
    private Expression term(){
        Expression factor = factor();
        return factor;
    }
    private Expression factor(){
        Expression unary = unary();
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
//            consume(TokenType.RIGHT_PAREN)
            while(peek().type != TokenType.RIGHT_PAREN){
                expression = expression();
            }
        }
        return (expression == null) ? new Expression.Literal("") : expression;
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
        return peek().type == TokenType.EOF;
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
        tokens.add(new Token(TokenType.BANG, "!", null, 1));
        tokens.add(new Token(TokenType.BANG, "!", null, 1));
        tokens.add(new Token(TokenType.NUMBER, "5", null, 1));
        Parser parser = new Parser(tokens);
        Expression expression = parser.solve();
        AstPrinter astPrinter = new AstPrinter();
        System.out.println(astPrinter.print(expression));
    }
}
