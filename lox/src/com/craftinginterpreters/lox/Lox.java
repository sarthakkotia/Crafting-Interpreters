package com.craftinginterpreters.lox;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class Lox {
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;
    public static void main(String[] args) throws IOException {
        // args here refers to the additional arguments given upon running the main function
        if(args.length > 1){
            System.out.println("Usage: jlox [script]");
            System.exit(64);
            //   The exit code 64 corresponds to : The command was used incorrectly, e.g., with the
            //			   wrong number of arguments, a bad flag, a bad syntax
            //			   in a parameter, or whatever. defined in sysexits.h file in UNIX

        } else if (args.length == 1) {
            // Our interpreter supports two ways of running code.
            // If you start jlox from the command line and give it a path to a file, it reads the file and executes it.
            runFile(args[0]);
        }else{
            // If you want a more intimate conversation with your interpreter, you can also run it interactively.
            // Fire up jlox without any arguments, and it drops you into a prompt where you can enter and execute code one line at a time.
            runPrompt();
        }
    }
    private static void runFile(String path) throws IOException{
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            // read all the data in the file in the path defined as byte format so that theres no loss of data
            // all the source code is stores in the bytes array
            run(new String(bytes, Charset.defaultCharset()));
            // from the bytes array convert it back to source code as a String data type and then run that code

            if(hadError) System.exit(65);
            if(hadRuntimeError) System.exit(70);
            // The input data was incorrect in some way.  This
            // should only be used for user's data and not system
            // files.
    }
    private static void runPrompt() throws IOException{
        // this is the prompt function of the interpreter where it would process each prompt line by line
        // InputStreamReader converts bytes to stream of character save it to reader buffer to be read later
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while(true){
            System.out.println("> ");
            String line = reader.readLine();
            if(line == null) break;
            run(line);
            //reset the variable in the interactive loop
            hadError = false;
        }
    }
    // both run prompt and run file internally calls this function and this does the actual lexing
    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
//        System.out.println("testing: "+tokens);
//        List<Token>tokens = scanner.scanTokens();
//        for(Token token: tokens){
//            System.out.println(token);
//        }
        Parser parser = new Parser(tokens);
//        Expression expression = parser.parse();
        List<Statement> statements = parser.parse();
        AstPrinter astPrinter = new AstPrinter();
        if(hadError) return;
        for(Statement statement: statements){
            System.out.println(astPrinter.print(statement));
        }
//        System.out.println(new AstPrinter().print(expression));
        interpreter.interpret(statements);
    }
    static void error(int line, String message){
        report(line, "", message);
    }
    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, " at end", message);
        }else{
            report(token.line, " at " + token.lexeme + " ", message);
        }
    }
    private static void report(int line, String where, String message){
        System.err.println("[line "+line+"] Error"+where+": "+message);
        // TODO: implement the report function as following:
        // Error: Unexpected "," in argument list.
        //
        //     15 | function(first, second,);
        //                               ^-- Here.
        hadError = true;
    }
    static void runtimeError(RuntimeError error){
        System.err.println(error.getMessage() + "\n[line "+ error.token.line + "]");
        hadRuntimeError = true;
    }
}