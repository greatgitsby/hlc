package com.greatgitsby.hlc;

/**
 * Register Allocation Error
 *
 * This error will be thrown when the Parser encounters an expression
 * that is too long and fills all available registers
 */
public class RegisterAllocationException extends Exception {
    public RegisterAllocationException(String message) { super(message); }
}
