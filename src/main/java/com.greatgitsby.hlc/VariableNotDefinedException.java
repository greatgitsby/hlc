package com.greatgitsby.hlc;

/**
 * VariableNotDefinedException
 *
 * This exception is used when a variable is not defined. This
 * ought not be allowed in the Hansen language
 */
public class VariableNotDefinedException extends CompilerException {

    /**
     * No-arg exception constructor
     */
    public VariableNotDefinedException() {
        super();
    }

    /**
     * Construct a new exception with a message
     *
     * @param message the message to display
     */
    public VariableNotDefinedException(String message) {
        super(message);
    }
}