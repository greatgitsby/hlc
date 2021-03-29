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

public class CompilerTest {

    // Test directory
    private static final String TEST_DIR =
        "./src/test/java/com.greatgitsby.hlc";

    // Location of the good programs
    public static final String GOOD_PROGRAM_DIR =
        TEST_DIR + "/good_programs";

    // Location of the bad programs
    public static final String BAD_PROGRAM_DIR =
        TEST_DIR + "/bad_programs";

    public static final String GOOD_PROGRAMS_LEXER_DIR =
        GOOD_PROGRAM_DIR + "/lexer";

    public static final String GOOD_PROGRAMS_PARSER_DIR =
        GOOD_PROGRAM_DIR + "/parser";

    public static final String BAD_PROGRAMS_LEXER_DIR =
        BAD_PROGRAM_DIR + "/lexer";

    public static final String BAD_PROGRAMS_PARSER_DIR =
        BAD_PROGRAM_DIR + "/parser";

    private static Stream<Arguments> getFilenamesAsArgsIn(String directory) {
        String[] programs;
        File programDirectory = new File(directory);

        programs = Arrays.stream(Objects.requireNonNull(programDirectory.list())).map(s -> String.format("%s/%s", directory, s)).toArray(String[]::new);

        return Arrays.stream(programs).map(Arguments::of);
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

    /**
     * Test a program file against the Parser
     *
     * @param filename the file to test
     * @throws IOException if there was a file error
     * @throws SyntaxErrorException if the code was syntactically invalid
     */
    @ParameterizedTest
    @MethodSource("provideGoodProgramParserFilenames")
    void test_parser_GoodPrograms(String filename) throws IOException, SyntaxErrorException {
        Assertions.assertTrue(
            new Parser(
                new LexicalAnalyzer(filename)
            ).isValidSyntax()
        );
    }

    /**
     * Test a program file against the Parser
     *
     * @param filename the file to test
     */
    @ParameterizedTest
    @MethodSource("provideBadProgramParserFilenames")
    void test_parser_BadPrograms(String filename) {
        Assertions.assertThrows(SyntaxErrorException.class, () ->
            new Parser(
                new LexicalAnalyzer(filename)
            ).isValidSyntax()
        );
    }
}
