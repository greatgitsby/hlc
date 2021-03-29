package com.greatgitsby.hlc;

import java.io.File;
import java.io.IOException;

public class ParserTest {
    public static final String TEST_PROG_DIR = "./src/test/java/com.greatgitsby.hlc/test_programs";

    public static void main(String[] args) throws NullPointerException, IOException, SyntaxErrorException {
//        testOne("buggy.h");
        testAll();
    }


    public static void testOne(String program) throws IOException, SyntaxErrorException {
        System.out.printf(
            "| %-25s| %-6b|\n",
            program,
            new Parser(new LexicalAnalyzer(String.format("%s/%s", TEST_PROG_DIR, program))).isValidSyntax()
        );
    }

    public static void testAll() throws IOException, SyntaxErrorException {
        String[] programs;
        File programDirectory = new File(TEST_PROG_DIR);

        programs = programDirectory.list();

        System.out.printf("| %-25s| %-6s|\n", "------------------------", "-----");
        System.out.printf("| %-25s| %-6s|\n", "program", "valid");
        System.out.printf("| %-25s| %-6s|\n", "------------------------", "-----");

        if (programs != null) {
            for (String file : programs) {
                String programPath = String.format("%s/%s", TEST_PROG_DIR, file);

                boolean isValidProgram = new Parser(new LexicalAnalyzer(programPath)).isValidSyntax();

                System.out.printf("| %-25s| %-6b|\n", file, isValidProgram);
            }
        }

        System.out.printf("| %-25s| %-6s|\n", "------------------------", "-----");
    }
}
