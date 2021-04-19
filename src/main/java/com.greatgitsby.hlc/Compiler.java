package com.greatgitsby.hlc;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Compiler
 *
 * Compiles a source file in the Hansen language into equivalent ARM assembly
 */
public class Compiler {

    /**
     * Compile a file into ARM code
     *
     * @param args where args[0] is the filename
     */
    public static void main(String[] args) {

        // Variables
        LexicalAnalyzer theLexer;
        FileWriter output;
        Parser theParser;
        Instant start;
        String destinationFilename;

        // Output the usage message if the user does not supply
        // the correct amount of arguments
        if (args.length != 1) {
            System.out.println("Usage: Compiler.java [filename]");
            System.exit(1);
        }

        // Mark the current time to approximate compile time
        start = Instant.now();

        // Compile the source indicated by the first argument
        try {

            // Create a new Lexer for the given filename
            theLexer = new LexicalAnalyzer(args[0]);

            // Form the destination filename with the ARM convention
            // file extension ".s"
            destinationFilename = String.format("%s.s", theLexer.getFileName());

            // Begin the lexical and semantic analysis
            // stages by creating a new Parser
            theParser = new Parser(theLexer);

            // Setup the output file writer
            output = new FileWriter(destinationFilename);

            // Write the ARM output to the file
            output.write(theParser.dumpOutput());
            output.close();

            // Print the successful compile message
            System.out.printf(
                "Compiled %s in %dms%n",

                theLexer.getFileName(),
                Duration.between(start, Instant.now()).toMillis()
            );

        }
        // If any errors occur, print them to stderr
        catch (CompilerException | IOException e) {
            System.err.printf("Compiler Error: %s%n", e.getMessage());
        }
    }
}
