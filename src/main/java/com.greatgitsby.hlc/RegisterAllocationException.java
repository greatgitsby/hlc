package com.greatgitsby.hlc;

/**
 * RegisterAllocationException
 *
 * This exception is used when all registers are full and no new Symbol
 * can be allocated
 */
public class RegisterAllocationException extends CompilerException {

    /**
     * No-arg exception constructor
     */
    public RegisterAllocationException() {
        super();
    }

    /**
     * Construct a new exception with a message
     *
     * @param message the message to display
     */
    public RegisterAllocationException(String message) {
        super(message);
    }
}