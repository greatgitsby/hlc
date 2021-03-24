package com.greatgitsby.hlc;

/**
 * Token
 *
 * Each Token is an element of the Hansen programming
 * language
 */
public enum Token implements Symbol {
    VARIABLE,
    IDENTIFIER,
    NUMBER,
    PRINT,
    IF,
    THEN,
    ELSE,
    WHILE,
    DO,
    BEGIN,
    END,
    STRING_CONST,
    MULTIPLICATIVE_OP,
    RELATIONAL_OP,
    ASSIGNMENT_OP,
    ADDITIVE_OP,
    STATEMENT_SEP,
    LEFT_PAREN,
    RIGHT_PAREN,
    WHITESPACE,
    COMMENT,
    END_OF_INPUT;

    /**
     * General behavior for terminals
     */
    @Override
    public void doTheThing() {
        // TODO Implement general behavior
    }
}