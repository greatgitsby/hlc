package com.greatgitsby.hlc;

/**
 * VariableAlreadyDefinedException
 *
 * This exception is used when a variable is already defined. This
 * ought not be allowed in the Hansen language
 */
public class VariableAlreadyDefinedException extends CompilerException {

    /**
     * No-arg exception constructor
     */
    public VariableAlreadyDefinedException() {
        super();
    }

    /**
     * Construct a new exception with a message
     *
     * @param message the message to display
     */
    public VariableAlreadyDefinedException(String message) {
        super(message);
    }
}