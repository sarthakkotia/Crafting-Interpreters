package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAST {
    public static void main(String[] args) throws IOException {
        if(args.length != 1){
            System.err.println("Usage: generate_Ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAST(outputDir, "Expression", Arrays.asList(
                "Binary : Expression left, Token operator, Expression right",
                "Grouping : Expression expression",
                "Literal : Object value",
                "Unary : Token operator Expression right"
        ));
    }
    public static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package.com.craftinginterpreters,lox");
        writer.println();
        writer.println("import java.util.List");
        writer.println();
        writer.println("abstract class " + baseName + " {");
        for(String type: types){
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
        writer.println("}");
        writer.close();
    }
    public static void defineType(PrintWriter writer, String baseName, String className, String fieldList){
        writer.println(" static class " + className + " extends "+baseName + "{");
        writer.println(" "+className+"("+fieldList+") {");
        String[] fields = fieldList.split(", ");

        for(String field: fields){
            String name = field.split(" ")[1];
            writer.println("   this." + name + " = " + name + ";");
        }
        writer.println("  }");
        writer.println();

        for(String field: fields){
            writer.println("   final "+ field + ";");
        }
        writer.println(" }");
    }
}
