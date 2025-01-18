package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    public Scanner(String source) {
        this.source = source;
    }
    // we have created a static keywordshashmap to recognize if the identifier is a keyword
    private static final Map<String, TokenType>keywords;
    static{
        keywords = new HashMap<String, TokenType>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
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
            case '\t':  break;
            case '\n':
                        line++;
                        break;
            case '"':
                        handleString();
                        break;
            default:
                    if(isDigit(ch)){
                        handleNumber();
                    }
                    // assuming that our identifier is starting with underscore or any alphabet
                    else if(isAlphaUnderscore(ch)){
                        handleIdentifier();
                    }
                    else{
                        Lox.error(line, String.format("Unexpected character %c", ch));
                        break;
                    }
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
        tokens.add(new Token(tokenType, lexeme, literal, line));
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
    private void handleString(){
        while(peek()!='"' && !isAtEnd()){
            if(peek()=='\n') line++;
            getChar();
        }
        if(isAtEnd()){
            Lox.error(line, "Unterminated String");
            return;
        }
        // the closing "
        getChar();
        // we could also have used the substring inside the helper function but with this we are essentially removing the quotes
        String lexeme = source.substring(start+1, current-1);
        addToken(TokenType.STRING, lexeme);
    }
    private boolean isDigit(char ch){
        return ch >= '0' && ch <= '9';
    }
    private void handleNumber(){
        while(isDigit(peek())) getChar();
        // if we foung the decimal point, then before consuming the point we need to make sure that the next character should be a digit
        if(peek() == '.' && isDigit(peekNext())){
            getChar();

            while (isDigit(peek())) getChar();
        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }
    private char peekNext(){
        if(current+1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }
    private boolean isAlphaUnderscore(char ch){
        return (ch == '_') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }
    private void handleIdentifier(){
        while (isAlphaNumeric(peek())) getChar();

        // handling keywords
        String text = source.substring(start, current);
        TokenType tokenType = keywords.get(text);
        if(tokenType == null) tokenType = TokenType.IDENTIFIER;
        addToken(tokenType);

    }
    private boolean isAlphaNumeric(char ch){
        return isDigit(ch) || isAlphaNumeric(ch);
    }
}
