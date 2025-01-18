package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd(){
        return current >= source.length();
    }

    private void scanToken(){
        char ch = getChar();
        switch (ch){
            //Single character tokens
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;

            // one/2 character tokens
            case '!':
                        addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                        break;
            case '=':
                        addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                        break;
            case '>':
                        addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                        break;
            case '<':
                        addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                        break;
            // handling comment case
            case '/':
                        if(match('/')){
                            // keep consuming until end of line or end of file
                            while(peek() != '\n' && isAtEnd()){
                                getChar();
                            }
                        }else{
                            addToken(TokenType.SLASH);
                        }
                        break;
            // ignore meaningless characters like space
            case ' ':
            // \r represents carraige return
            case '\r':
            case '\t': break;
            case '\n':
                        line++;
                        break;
            default: Lox.error(line, String.format("Unexpected character %c", ch));
                     break;
        }
    }

    private char getChar(){
        char ch = source.charAt(current);
        current++;
        return ch;
    }

    private void addToken(TokenType tokenType){
        addToken(tokenType, null);
    }
    private void addToken(TokenType tokenType, Object literal){
        String lexeme = source.substring(start, current);
        tokens.add(new Token(tokenType, lexeme,literal, line));
    }
    private boolean match(char expected){
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;
        current++;
        return true;
    }
    private char peek(){
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }
}
