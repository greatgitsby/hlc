package com.greatgitsby.hlc;

/**
 * CompilerException
 *
 * A generic compiler error; used when the meaning of the exception
 * is ambiguous (not in one of the pre-defined subclasses)
 */
public class CompilerException extends Exception {

    /**
     * No-arg exception constructor
     */
    public CompilerException() {
        super();
    }

    /**
     * Construct a new exception with a message
     *
     * @param message the message to display
     */
    public CompilerException(String message) {
        super(message);
    }
}