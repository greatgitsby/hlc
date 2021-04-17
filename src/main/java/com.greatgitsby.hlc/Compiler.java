package com.greatgitsby.hlc;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws CompilerException, IOException {
        String filename = System.getProperty("user.dir") + "\\src\\test\\java\\com.greatgitsby.hlc\\bad_programs\\compiler\\badprog3.h";

        System.out.println(filename);
        LexicalAnalyzer lexer = new LexicalAnalyzer(filename);
        Parser parser = new Parser(lexer);

        try {
            parser.isValidSyntax();
            System.out.println(parser.dumpOutput());
        } catch (CompilerException e) {
            e.printStackTrace();
        }
    }
}
