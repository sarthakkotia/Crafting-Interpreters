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
        ArrayList<TokenType> operators = new ArrayList<>();
        operators.add(TokenType.BANG);
        operators.add(TokenType.MINUS);
        int index = match(operators);
        if(index != -1){
            Token operator = getToken();
            advance();
            return new Expression.Unary(operator, unary());
        }
        return primary();
    }
    private Expression primary(){
        Token present = getToken();
        Expression expression = null;
        if(present.type == TokenType.NUMBER || present.type == TokenType.STRING){
            return new Expression.Literal(present.lexeme);
        }else if(present.type == TokenType.TRUE){
            return new Expression.Literal("true");
        }else if(present.type == TokenType.FALSE){
            return new Expression.Literal("false");
        }else if(present.type == TokenType.NIL){
            return new Expression.Literal("nil");
        }else if(present.type == TokenType.LEFT_PAREN){
            // get all the expression from it until right paren is not got
            while(getToken().type != TokenType.RIGHT_PAREN){
                expression = expression();
            }
        }
        return (expression == null) ? new Expression.Literal("") : expression;
    }
    private int match(ArrayList<TokenType> operators){
        TokenType present = getToken().type;
        return operators.indexOf(present);
    }
    private Token getToken(){
        if(current == tokens.size()) Lox.error(-1, "");
        return tokens.get(current);
    }
    private void advance(){
        if(current == tokens.size()) Lox.error(1, "");
        current++;
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
