package com.greatgitsby.hlc;

import java.io.File;
import java.io.IOException;

public class ParserTest {
    public static final String TEST_PROG_DIR = "./src/test/java/com.greatgitsby.hlc/test_programs";

    public static void main(String[] args) throws NullPointerException, IOException, SyntaxErrorException {
        testOne("isPrime.h");
    }

    public static void testOne(String program) throws IOException, SyntaxErrorException {
        String path = "./src/test/java/com.greatgitsby.hlc/test_programs";
        boolean isValidProgram = new Parser(new LexicalAnalyzer(String.format("%s/%s", TEST_PROG_DIR, program))).isValidSyntax();
        System.out.printf("| %-25s| %-6b|\n", program, isValidProgram);
    }

    public static void testAll() throws IOException, SyntaxErrorException {
        String[] programs;
        File programDirectory = new File(TEST_PROG_DIR);

        programs = programDirectory.list();

        if (programs != null) {
            for (String file : programs) {
                String programPath = String.format("%s/%s", TEST_PROG_DIR, file);

                boolean isValidProgram = new Parser(new LexicalAnalyzer(programPath)).isValidSyntax();

                System.out.printf("| %-25s| %-6b|\n", file, isValidProgram);
            }
        }
    }
}
