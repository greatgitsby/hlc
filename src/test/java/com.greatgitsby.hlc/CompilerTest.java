package com.greatgitsby.hlc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * TODO Description
 */
public class CompilerTest {

    /**
     * Test a program file against the Parser
     *
     * @param filename the file to test
     * @throws IOException if there was a file error
     * @throws SyntaxErrorException if the code was syntactically invalid
     */
    @ParameterizedTest(name = "Parser - Good Program {index}: {0}")
    @MethodSource("provideGoodProgramParserFilenames")
    void test_parser_GoodPrograms(String filename) throws IOException, SyntaxErrorException {
        Assertions.assertTrue(
            new Parser(
                new LexicalAnalyzer(resolveGoodParserFile(filename))
            ).isValidSyntax()
        );
    }

    /**
     * Test a program file against the Parser
     *
     * @param filename the file to test
     */
    @ParameterizedTest(name = "Parser - Bad Program {index}: {0}")
    @MethodSource("provideBadProgramParserFilenames")
    void test_parser_BadPrograms(String filename) {
        Assertions.assertThrows(SyntaxErrorException.class, () -> {
            try {
                new Parser(
                    new LexicalAnalyzer(resolveBadParserFile(filename))
                ).isValidSyntax();
            } catch (SyntaxErrorException e) {
                e.printStackTrace();
                throw e;
            }
        });
    }

    /**
     * Test a program file against the Parser
     *
     * @param filename the file to test
     * @throws IOException if there was a file error
     * @throws SyntaxErrorException if the code was syntactically invalid
     */
    @ParameterizedTest(name = "Lexer - Good Program {index}: {0}")
    @MethodSource("provideGoodProgramLexerFilenames")
    void test_lexer_GoodPrograms(String filename) throws IOException, SyntaxErrorException {
        LexicalAnalyzer l = new LexicalAnalyzer(resolveGoodLexerFile(filename));

        while (l.hasNextSymbol()) {
            System.out.println(l.nextSymbol());
        }

        Assertions.assertTrue(true);
    }

    private static Stream<Arguments> getFilenamesAsArgsIn(String directory) {
        String[] programs;
        File programDirectory = new File(directory);

        programs = Arrays.stream(Objects.requireNonNull(programDirectory.list())).toArray(String[]::new);

        return Arrays.stream(programs).map(Arguments::of);
    }

    private static String resolveFile(String filepath, String filename) {
        return String.format("%s/%s", filepath, filename);
    }

    private static String resolveGoodLexerFile(String filename) {
        return resolveFile(GOOD_PROGRAMS_LEXER_DIR, filename);
    }

    private static String resolveGoodParserFile(String filename) {
        return resolveFile(GOOD_PROGRAMS_PARSER_DIR, filename);
    }

    private static String resolveBadParserFile(String filename) {
        return resolveFile(BAD_PROGRAMS_PARSER_DIR, filename);
    }

    /**
     * Provide a stream of filenames for good programs
     *
     * @return a stream of filenames for good programs
     */
    private static Stream<Arguments> provideGoodProgramLexerFilenames() {
        return getFilenamesAsArgsIn(GOOD_PROGRAMS_LEXER_DIR);
    }

    /**
     * Provide a stream of filenames for good programs
     *
     * @return a stream of filenames for good programs
     */
    private static Stream<Arguments> provideBadProgramLexerFilenames() {
        return getFilenamesAsArgsIn(BAD_PROGRAMS_LEXER_DIR);
    }

    /**
     * Provide a stream of filenames for good programs
     *
     * @return a stream of filenames for good programs
     */
    private static Stream<Arguments> provideGoodProgramParserFilenames() {
        return getFilenamesAsArgsIn(GOOD_PROGRAMS_PARSER_DIR);
    }

    /**
     * Provide a stream of filenames for good programs
     *
     * @return a stream of filenames for good programs
     */
    private static Stream<Arguments> provideBadProgramParserFilenames() {
        return getFilenamesAsArgsIn(BAD_PROGRAMS_PARSER_DIR);
    }

    // Test directory
    private static final String TEST_DIR =
            "./src/test/java/com.greatgitsby.hlc";

    // Location of the good programs
    private static final String GOOD_PROGRAM_DIR =
            TEST_DIR + "/good_programs";

    // Location of the bad programs
    private static final String BAD_PROGRAM_DIR =
            TEST_DIR + "/bad_programs";

    private static final String GOOD_PROGRAMS_LEXER_DIR =
            GOOD_PROGRAM_DIR + "/lexer";

    private static final String GOOD_PROGRAMS_PARSER_DIR =
            GOOD_PROGRAM_DIR + "/parser";

    private static final String BAD_PROGRAMS_LEXER_DIR =
            BAD_PROGRAM_DIR + "/lexer";

    private static final String BAD_PROGRAMS_PARSER_DIR =
            BAD_PROGRAM_DIR + "/parser";
}
