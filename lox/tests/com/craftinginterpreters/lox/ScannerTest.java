package com.craftinginterpreters.lox;
import org.junit.jupiter.api.*;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


@DisplayName("Testing Scanner")
public class ScannerTest {
    Scanner scanner = new Scanner("");
    @Test
    @DisplayName("Testing Single character tokens")
    void singleCharacterToken(){
        String source = ".";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        String lexeme = source.substring(0, 1);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.DOT, lexeme, null, 1));
        expected.add(new Token(TokenType.EOF, "", null,1));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "single character token");
    }
    @Nested
    @DisplayName("Testing operators tokens") // should be nested, we would want it to test both single charaters anaad multiple characters
    public class OperatorTest{
        @Test
        @DisplayName("Single character operator")
        void singleCharOperators(){
            String source = "<";
            scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            String lexeme = source.substring(0, 1);
            List<Token> expected = new ArrayList<>();
            expected.add(new Token(TokenType.LESS, lexeme, null, 1));
            expected.add(new Token(TokenType.EOF, "", null,1));
            Assertions.assertEquals(expected.toString(), tokens.toString(), "Single character operator");
        }
        @Test
        @DisplayName("Double character operator")
        void doubleCharOperators(){
            String source = ">=";
            scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            String lexeme = source.substring(0, 2);
            List<Token> expected = new ArrayList<>();
            expected.add(new Token(TokenType.GREATER_EQUAL, lexeme, null, 1));
            expected.add(new Token(TokenType.EOF, "", null,1));
            Assertions.assertEquals(expected.toString(), tokens.toString(), "Double character operator");
        }
    }
    @Test
    @DisplayName("Testing Comments")
    void comments(){
        String source = "//this should not exist";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        String lexeme = source.substring(0, 0);
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.EOF, "", null,1));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "comments");
    }
    @Test
    @DisplayName("Testing String")
    void string(){
        String source = "\"this is a string\"";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        String lexeme = source;
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.STRING, lexeme, source.substring(1,source.length()-1), 1));
        expected.add(new Token(TokenType.EOF, "", null,1));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "string");
    }
    @Test
    @DisplayName("Testing New Line")
    void newLine(){
        String source = "\n";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.EOF, "", null,2));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "new Line");
    }
    @Nested
    @DisplayName("Testing Number Literals")
    public class NumbersTest{
        @Test
        @DisplayName("Testing positive integers")
        void positiveIntegers(){
            String source = "123";
            scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            List<Token> expected = new ArrayList<>();
            expected.add(new Token(TokenType.NUMBER, source, Double.parseDouble(source), 1));
            expected.add(new Token(TokenType.EOF, "", null,1));
            Assertions.assertEquals(expected.toString(), tokens.toString(), "positive integer");
        }
        @Test
        @DisplayName("Testing negative integers")
        void negativeIntegers(){
            String source = "-3";
            scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            List<Token> expected = new ArrayList<>();
            expected.add(new Token(TokenType.MINUS, "-", null, 1));
            expected.add(new Token(TokenType.NUMBER, "3", Double.parseDouble(source.substring(1,2)), 1));
            expected.add(new Token(TokenType.EOF, "", null,1));
            Assertions.assertEquals(expected.toString(), tokens.toString(), "Negative integer");
        }
        @Test
        @DisplayName("Testing floating point numbers")
        void floatNumbers(){
            String source = "12.3456";
            scanner = new Scanner(source);
            List<Token> tokens = scanner.scanTokens();
            List<Token> expected = new ArrayList<>();
            expected.add(new Token(TokenType.NUMBER, source, Double.parseDouble(source), 1));
            expected.add(new Token(TokenType.EOF, "", null,1));
            Assertions.assertEquals(expected.toString(), tokens.toString(), "Float number");
        }
    }
    @Test
    @DisplayName("Testing reserved word")
    void reservedWord(){
        String source = "super";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.SUPER, source, null, 1));
        expected.add(new Token(TokenType.EOF, "", null,1));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "Float number");
    }
    @Test
    @DisplayName("Testing identifier")
    void identifier(){
        String source = "my_name_is_Lox";
        scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        List<Token> expected = new ArrayList<>();
        expected.add(new Token(TokenType.IDENTIFIER, source, null, 1));
        expected.add(new Token(TokenType.EOF, "", null,1));
        Assertions.assertEquals(expected.toString(), tokens.toString(), "identifier");
    }
    @Test
    @DisplayName("Testing Invalid characters")
    void invalidCharacters(){
        String source = "$hello";
        scanner = new Scanner(source);
        PrintStream ps = new PrintStream(new FileOutputStream(FileDescriptor.out));
        System.setOut(ps);
        List<Token> tokens = scanner.scanTokens();

    }
}
