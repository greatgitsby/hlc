package com.greatgitsby.hlc;

import java.io.IOException;

public class LexicalAnalyzerTest {
    public static void main(String[] args) throws IOException, SyntaxErrorException {
        System.out.println(
            new Parser(new LexicalAnalyzer(
                "./src/test/java/com.greatgitsby.hlc/myboi.h"
            )).isValidSyntax()
        );
    }
}
