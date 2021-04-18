package com.greatgitsby.hlc;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Compiler
 *
 * Compiles a source file into an assembled
 */
public class Compiler {
    public static void main(String[] args) {

        LexicalAnalyzer theLexer;
        FileWriter output;
        Parser theParser;
        Instant start;

        if (args.length != 1) {
            System.out.println("Usage: ");
            System.exit(1);
        }

        // Compile the source indicated by the first argument
        try {
            start = Instant.now();
            theLexer = new LexicalAnalyzer(args[0]);
            theParser = new Parser(theLexer);
            output = new FileWriter(theLexer.getFileName().replace(".h", ".s"));

            output.write(theParser.dumpOutput());
            output.close();

            System.out.printf(
                "Compiled %s in %dms%n",
                theLexer.getFileName(),
                Duration.between(start, Instant.now()).toMillis()
            );
        } catch (CompilerException | IOException e) {
            System.err.println("Compiler Error: " + e.getMessage());
        }
    }
}
