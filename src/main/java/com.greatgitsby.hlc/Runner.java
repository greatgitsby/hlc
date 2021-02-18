package com.greatgitsby.hlc;

public class Runner {
    public static void main(String[] args) throws Exception {
        LexicalAnalyzer lex = new LexicalAnalyzer();
        lex.analyze("src/test/java/com.greatgitsby.hlc/mygcd.h");

        while (lex.hasNextLexeme()) {
            Symbol s = lex.nextLexeme();
            if (!s.toString().startsWith("WHITESPACE")) {
                System.out.printf("%s (%s)\n", s.toString(), s.hashCode());
            }
        }
    }
}
