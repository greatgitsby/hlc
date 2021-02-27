package com.greatgitsby.hlc;

/**
 * Syntax Error
 *
 * This error will be thrown when the Lexical Analyzer
 * (and perhaps the Parser) encounters a sequence of
 * characters (or Symbols...) that is not part of
 * the language
 */
public class SyntaxErrorException extends Exception {
    public SyntaxErrorException(String message) {
        super(message);
    }
}
