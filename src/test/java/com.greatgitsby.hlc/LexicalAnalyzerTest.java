package com.greatgitsby.hlc;

import java.io.IOException;

public class LexicalAnalyzerTest {
    public static void main(String[] args) throws IOException, SyntaxErrorException {
        LexicalAnalyzer l = new LexicalAnalyzer(
                "./src/test/java/com.greatgitsby.hlc/myboi.h"
        );

        for (Symbol s : l) {
            if (!s.toString().startsWith("WHITE")) {
                System.out.println(s);
            }
        }
    }
}
