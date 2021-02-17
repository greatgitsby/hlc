package com.greatgitsby.hlc;

import java.io.IOException;
import java.util.Arrays;

public class Runner {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer lex = new LexicalAnalyzer();
        lex.analyze("src/test/java/com.greatgitsby.hlc/basic.h");

        while (lex.hasNextLexeme()) {
            Symbol s = lex.nextLexeme();
            System.out.printf("%s (%s)\n", s.toString(), s.hashCode());
        }
    }
}
